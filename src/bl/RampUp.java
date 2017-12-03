package bl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;
import strategyInterfaces.SourceQuantityIdentificationStrategy;
import templates.SourceQuantityIdentifierTemplate;

public class RampUp extends SourceQuantityIdentifierTemplate implements Serializable {
	
	public final String CONFIG_PATH = "/quantityIdentifierConf.xml";
	public final double LINEAR_TRENDLINE_DEVIATION; //percent 
	public final double TIME_THRESHOLD; //Milliseconds
	
	public RampUp() {
		HashMap<String,String> configMap = XMLConfig.getIdentifierConfig(CONFIG_PATH, "RampUp");
		LINEAR_TRENDLINE_DEVIATION = Double.parseDouble(configMap.get("LINEAR_TRENDLINE_DEVIATION"));
		TIME_THRESHOLD = Double.parseDouble(configMap.get("TIME_THRESHOLD"));
			
	}
	
	@Override
	protected JavaPairRDD<Attack,Integer> identifySrcQuantity(JavaRDD<Attack> attacks) {
		
		JavaPairRDD<Attack,Integer> definedAttacks = attacks.mapToPair(a -> 
			new Tuple2<Attack,Integer>(a,new DefineAttackType().call(a)));
		
		
		return definedAttacks;
		
	}
	
	private class DefineAttackType implements Function<Attack,Integer>{

		@Override
		public Integer call(Attack attack) throws Exception {
			/*
			 * the function gets packets and decides if there is a ramp up or not
			 * true - indicates that there is ramp up and hence this a single source attack
			 * false - indicates that there is no ramp up and hence this multi-source attack
			 */
			ArrayList<Packet> packets = attack.getPacketsArray();
			SimpleRegression regLine = new SimpleRegression();
			SimpleRegression tmpRegLine = new SimpleRegression();
			int packetsSize = packets.size();
			long startTimeStamp = packets.get(0).getTimestamp();
			Packet p;
			//calculate attack regression line
			for (Packet packet : packets) {
				regLine.addData(packet.getTimestamp(), packet.getNumOfPackets());
			}
			
			//check if there is a ramp up
			tmpRegLine.addData(startTimeStamp, packets.get(0).getNumOfPackets());
			for (int i = 1; i < packetsSize; i++) {
				p = packets.get(i);
				tmpRegLine.addData(p.getTimestamp(), p.getNumOfPackets());
				if(isRegressionLineInDeviation(regLine,tmpRegLine,
						startTimeStamp, p.getTimestamp())){
					System.out.println("Ramp up time: " + (p.getTimestamp() - startTimeStamp));
					if(p.getTimestamp() - startTimeStamp < TIME_THRESHOLD){
						return -1;
					}else{
						return 1;
					}
				}
			}
			
			return 0;
		}
		
	}
	
	
	
	private boolean isRegressionLineInDeviation(SimpleRegression originalAttackRegressionLine,
					SimpleRegression regressionLine, double leftX, double rightX){
		double oM,oN,leftY,rightY,m,n;
		double rightDownYdeviation,rightUpYdeviation,leftUpYdeviation,leftDownYdeviation;
		oM = originalAttackRegressionLine.getSlope();
		oN = originalAttackRegressionLine.getIntercept();
		m = regressionLine.getSlope();
		n = regressionLine.getIntercept();
		
		//check left limit of line
		leftUpYdeviation = (leftX * oM + oN) + (leftX * oM + oN) * LINEAR_TRENDLINE_DEVIATION/100;
		leftDownYdeviation = (leftX * oM + oN) - (leftX * oM + oN) * LINEAR_TRENDLINE_DEVIATION/100;
		leftY = leftX * m + n;
		
		if(leftY > leftUpYdeviation || leftY < leftDownYdeviation){
			return false;
		}
		//check right limit of line
		rightUpYdeviation = (rightX * oM + oN) + (rightX * oM + oN) * LINEAR_TRENDLINE_DEVIATION/100;
		rightDownYdeviation = (rightX * oM + oN) - (rightX * oM + oN) * LINEAR_TRENDLINE_DEVIATION/100;
		rightY = rightX * m + n;
		
		if(rightY > rightUpYdeviation || rightY < rightDownYdeviation){
			return false;
		}

		return true;
	
	}

}
