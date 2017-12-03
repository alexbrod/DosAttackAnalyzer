package bl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;
import strategyInterfaces.SourceQuantityIdentificationStrategy;
import templates.SourceQuantityIdentifierTemplate;

public class SpectralAnalysis extends SourceQuantityIdentifierTemplate implements Serializable {
	public final String CONFIG_PATH = "/quantityIdentifierConf.xml";
	public final int SEGMENT_SIZE; //in milliseconds
	public final double DISCARD_ANGLE_THRESHOLD;	//in degrees
	public final int FREQUENCY_RANGE;	//Hz
	public final int FREQUENCY_QUANTILE; //percent
	public final int MULTI_SOURCE_UPPER_THRESHOLD; //Hz
	public final int MULTI_SOURCE_LOWER_THRESHOLD; //Hz
	public final int SINGLE_SOURCE_UPPER_THRESHOLD; //Hz
	public final int SINGLE_SOURCE_LOWER_THRESHOLD; //Hz
	
	public SpectralAnalysis() {
		HashMap<String,String> configMap = XMLConfig.getIdentifierConfig(CONFIG_PATH, "SpectralAnalysis");
		SEGMENT_SIZE = Integer.parseInt(configMap.get("SEGMENT_SIZE"));
		DISCARD_ANGLE_THRESHOLD = Double.parseDouble(configMap.get("DISCARD_ANGLE_THRESHOLD"));
		FREQUENCY_RANGE = Integer.parseInt(configMap.get("FREQUENCY_RANGE"));
		FREQUENCY_QUANTILE = Integer.parseInt(configMap.get("FREQUENCY_QUANTILE"));
		MULTI_SOURCE_UPPER_THRESHOLD = Integer.parseInt(configMap.get("MULTI_SOURCE_UPPER_THRESHOLD"));
		MULTI_SOURCE_LOWER_THRESHOLD = Integer.parseInt(configMap.get("MULTI_SOURCE_LOWER_THRESHOLD"));
		SINGLE_SOURCE_UPPER_THRESHOLD = Integer.parseInt(configMap.get("SINGLE_SOURCE_UPPER_THRESHOLD"));
		SINGLE_SOURCE_LOWER_THRESHOLD = Integer.parseInt(configMap.get("SINGLE_SOURCE_LOWER_THRESHOLD"));
	}
	
	@Override
	protected JavaPairRDD<Attack,Integer> identifySrcQuantity(JavaRDD<Attack> attacks) {
		//gets an attacks
		//returns positive for multiple source attacks
		//returns zero for unknown attacks
		//returns negative for single source attacks
		JavaPairRDD<Attack,Segment> segments = attacks.flatMapToPair(new DevideToSegments());
		
		JavaPairRDD<Attack,Segment> filteredSegments = segments.filter(new DiscardSegments());
		
		JavaPairRDD<Attack,ArrayList<Double>> acf = filteredSegments.mapToPair(new ACF());
		
		JavaPairRDD<Attack,ArrayList<Double>> unionedAcf = acf.reduceByKey(
				new Function2<ArrayList<Double>,ArrayList<Double>,ArrayList<Double>>(){

					@Override
					public ArrayList<Double> call(ArrayList<Double> al1,ArrayList<Double> al2) throws Exception {
						al1.addAll(al2);
						return al1;
					}
					
				});
		
		JavaPairRDD<Attack,ArrayList<Double>> powerSpectrum = unionedAcf.mapToPair(
				t -> new Tuple2<Attack,ArrayList<Double>>(t._1(),calcPowerSpectrum(t._2())));
		
		JavaPairRDD<Attack,ArrayList<Double>> ncs = powerSpectrum.mapToPair(
				t -> new Tuple2<Attack,ArrayList<Double>>(t._1(),calcNCS(t._2())));
		
		JavaPairRDD<Attack,Integer> quantile = ncs.mapToPair(
				t -> new Tuple2<Attack,Integer>(t._1(),calcQuntile(t._2())));
		
		JavaPairRDD<Attack,Integer> multiOrSingle = quantile.mapToPair(
				t -> new Tuple2<Attack,Integer>(t._1(),decideQuantity(t._2())));
		/*
		ArrayList<Tuple2<Attack,Integer>> al = new ArrayList<>(multiOrSingle.collect());
		for (Tuple2<Attack,Integer> tuple : al) {
			System.out.println("Attack: " + tuple._1() +
					" is: " + tuple._2());
		}
		*/
		return multiOrSingle;
	}
	
	private Integer decideQuantity(Integer frequency){
		if(frequency <= MULTI_SOURCE_UPPER_THRESHOLD &&
				frequency >= MULTI_SOURCE_LOWER_THRESHOLD){
			return 1;
		}
		else if(frequency <= SINGLE_SOURCE_UPPER_THRESHOLD &&
				frequency >= SINGLE_SOURCE_LOWER_THRESHOLD){
			return -1;
		}
		return 0;
	}
	
	private Integer calcQuntile(ArrayList<Double> ncs){
		//f is for frequency
		for (int f = 0; f < ncs.size(); f++) {
			if(ncs.get(f) >= (double)FREQUENCY_QUANTILE/100){
				return f;
			}
		}
		// returns if invalid
		return -1;
	}
	
	private class ACF implements PairFunction<Tuple2<Attack,Segment>,Attack,ArrayList<Double>>{

		@Override
		public Tuple2<Attack,ArrayList<Double>> call(Tuple2<Attack, Segment> tuple) throws Exception {
			ArrayList<Double> c = new ArrayList<>();
			ArrayList<Double> r = new ArrayList<>();
			
			ArrayList<Packet> packetsArray = tuple._2().getPacketsArray();
			double mean = tuple._2().calcMeanPacketRate();
			//System.out.println("mean:" + mean);
			int N = packetsArray.size();
			double sum;
			for (int k = 0; k < N; k++) {
		        sum = 0;
		        for (int t = 0; t < N - k; t++) {
		            sum += (packetsArray.get(t).getNumOfPackets() - mean) 
		            		* (packetsArray.get(t+k).getNumOfPackets() - mean);
		        }
		        c.add(sum/N);
		    }

			for (int k = 0; k < N; k++) {
				r.add(c.get(k)/c.get(0));
			}
			
			return new Tuple2<Attack,ArrayList<Double>>(tuple._1(),r);
		}
		
	}
	
	private class DiscardSegments implements Function<Tuple2<Attack,Segment>,Boolean>{

		@Override
		public Boolean call(Tuple2<Attack,Segment> tuple) throws Exception {
			ArrayList<Packet> packets = tuple._2().getPacketsArray();
			SimpleRegression linearReg = new SimpleRegression();
			for (Packet packet : packets) {
				linearReg.addData(packet.getTimestamp(), packet.getNumOfPackets());
			}
			//System.out.println("slope:" + linearReg.getSlope() + " tan: " + Math.tan(DISCARD_ANGLE_THRESHOLD));
			//Tangents represents the slope of a given angle
			if(linearReg.getSlope() < Math.tan(DISCARD_ANGLE_THRESHOLD) &&
					linearReg.getSlope() > Math.tan(-DISCARD_ANGLE_THRESHOLD)){
				return true;
			}
			return false;
		}
		
	}
	
	private class DevideToSegments implements PairFlatMapFunction<Attack,Attack,Segment>{

		@Override
		public Iterable<Tuple2<Attack, Segment>> call(Attack attack) throws Exception {
			return devideStreamIntoSegments(attack);
		}
		
	}
	
	private ArrayList<Tuple2<Attack,Segment>> devideStreamIntoSegments(Attack attack){
		ArrayList<Tuple2<Attack,Segment>> segments = new ArrayList<>();
		ArrayList<Packet> packetsArray = attack.getPacketsArray();
		int segmentIndex = 0;
		segments.add(new Tuple2<Attack,Segment>(attack,new Segment()));
		segments.get(segmentIndex)._2().addPackets(packetsArray.get(0));
		for (int i = 1; i < packetsArray.size(); i++) {
			if(packetsArray.get(i).getTimestamp() - segments.get(segmentIndex)._2().getPacket(0)
					.getTimestamp() <= SEGMENT_SIZE){
				segments.get(segmentIndex)._2().addPackets(packetsArray.get(i));
			}
			else{
				segments.add(new Tuple2<Attack,Segment>(attack,new Segment()));
				segmentIndex += 1;
				segments.get(segmentIndex)._2().addPackets(packetsArray.get(i));
			}
		}
		return segments;
	}
	
	
	private ArrayList<Double> calcNCS(ArrayList<Double> powerSpectrum){
		ArrayList<Double> c = new ArrayList<>();
		ArrayList<Double> p = new ArrayList<>();
		double sum;
		for (int f = 0; f <= FREQUENCY_RANGE; f++) {
			sum = 0;
			for (int i = 0; i < f; i++) {
				sum += (powerSpectrum.get(i) + powerSpectrum.get(i + 1))/2;
			}
			p.add(sum);
		}
		
		for (int f = 0; f <= FREQUENCY_RANGE; f++) {
			c.add(p.get(f)/p.get(FREQUENCY_RANGE));
		}
		
		return c;
	}
	
	private ArrayList<Double> calcPowerSpectrum(ArrayList<Double> attackAutoCorrelationData){
		ArrayList<Double> powerSpectrumList = new ArrayList<>();
		ArrayList<Complex> S = new ArrayList<>();
		int M = attackAutoCorrelationData.size();
		
		for (int f = 0; f <= FREQUENCY_RANGE; f++) {
			Complex sumComplex = Complex.ZERO;
			for (int k = 0; k < M; k++) {
				
				//Complex a = Complex.I.negate().multiply(2*Math.PI*f*k);
				Complex a = Complex.valueOf(-2*Math.PI*f*k);
				sumComplex = sumComplex.add((a.exp()).multiply(attackAutoCorrelationData.get(k)));
			}
			//System.out.println(sumComplex);
			S.add(sumComplex);
		}
		
		for (Complex complex : S) {
			double powerSpectrum = Math.sqrt(Math.pow(complex.getReal(), 2) 
					+ Math.pow(complex.getImaginary(), 2));
			powerSpectrumList.add(powerSpectrum);
		}
		
		return powerSpectrumList;
	}

}
