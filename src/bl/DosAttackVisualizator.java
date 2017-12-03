package bl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;
import templates.VisualizatorTemplate;

public class DosAttackVisualizator extends VisualizatorTemplate implements Serializable{
	
	
	
	@Override
	protected void visualizeAttacks(JavaPairRDD<Attack,Integer> attacks) {
		visualizeTypeDistribution(attacks);
		visualizeTypeVsDuration(attacks);
	}
	
	private void visualizeTypeDistribution(JavaPairRDD<Attack,Integer> attacks){
		StringBuilder sb = new StringBuilder();
		
		sb.append("#1 is for multiple source\n");
		sb.append("#-1 is for single source\n");
		sb.append("#0 is for unknown\n");
		
		
		JavaPairRDD<Integer,Integer> attackTypes = attacks.mapToPair(
				t -> new Tuple2<Integer,Integer>(t._2(),1));
		
		ArrayList<Tuple2<Integer,Integer>> types = new ArrayList<>(
				attackTypes.reduceByKey((a,b) -> a + b).collect());
		
		for (Tuple2<Integer, Integer> t : types) {
			sb.append(t._1() + "," + t._2() + "\n");
		}
		writeDataToFile(sb, "/afeka_nfs/AlexBrod/results/attackTypes.txt", StandardOpenOption.CREATE);
	}
	
	private void visualizeTypeVsDuration(JavaPairRDD<Attack,Integer> attacks){
		StringBuilder sb = new StringBuilder();
		
		sb.append("#1 is for multiple source\n");
		sb.append("#-1 is for single source\n");
		sb.append("#0 is for unknown\n");
		
		JavaRDD<ArrayList<Long>> attackTypes = attacks.map(
				new Function<Tuple2<Attack,Integer>,ArrayList<Long>>(){

					@Override
					public ArrayList<Long> call(Tuple2<Attack, Integer> t) throws Exception {
						ArrayList<Long> al = new ArrayList<>();
						al.add((long)t._2()); //type
						al.add(t._1().getEndTime() - t._1().getStartTime()); //duration
						al.add(t._1().getNumOfPackets()); 
						al.add(t._1().getNumOfBytes());
						return al;
					}
					
				});
		
		ArrayList<ArrayList<Long>> lists = new ArrayList<>(
				attackTypes.collect());
		for (int i = 0; i < lists.size(); i++) {
			sb.append(i + "," +					//attack index
					lists.get(i).get(0) + "," +	//type
					lists.get(i).get(1)/60000 + "," +	//duration in minutes
					lists.get(i).get(2)/1000 + "," + //packets in kilo
					lists.get(i).get(3)/1048576 + "\n");//bytes in mega
		}
		
		writeDataToFile(sb, "/afeka_nfs/AlexBrod/results/attackStats.txt", StandardOpenOption.CREATE);
		
	}
	
	private void writeDataToFile(StringBuilder sb, String filePath, StandardOpenOption openOperation){
		
		try {
			Files.write(Paths.get(filePath), sb.toString().getBytes(), openOperation);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
