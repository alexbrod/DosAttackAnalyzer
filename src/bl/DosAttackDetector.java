package bl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;

import comparators.AttackTimestampComparator;
import comparators.TimestampComparator;
import exceptions.InvalidDatasetException;
import startProgram.AttackManager;
import strategyInterfaces.DetectionStrategy;
import templates.DetectorTemplate;


public class DosAttackDetector extends DetectorTemplate implements Serializable{
	public final String DETECTOR_CONFIG_PATH = "/attackDetectorConfig.xml";
	public final double PACKET_RATE_THRESHOLD; // Packets/s
	public final double LOAD_THRESHOLD; 		// MB/s
	public final double MEASURING_WINDOW; 	// milliseconds 
	public final double ATTACK_MIN_LENGTH; 	// milliseconds
	
	public DosAttackDetector() {
		HashMap<String,String> configMap = XMLConfig.getAttackdetectorConfig(DETECTOR_CONFIG_PATH);
		PACKET_RATE_THRESHOLD = Double.parseDouble(configMap.get("PACKET_RATE_THRESHOLD"));
		LOAD_THRESHOLD = Double.parseDouble(configMap.get("LOAD_THRESHOLD"));
		MEASURING_WINDOW = Double.parseDouble(configMap.get("MEASURING_WINDOW"));
		ATTACK_MIN_LENGTH = Double.parseDouble(configMap.get("ATTACK_MIN_LENGTH"));	
	}
	


	@Override
	protected ArrayList<Attack> findPotentialAttacks(JavaRDD<Packet> packets) throws InvalidDatasetException{
		ArrayList<Attack> attackList = new ArrayList<>();
		int windowStartIndex = 0;
		int windowEndIndex = 0;
		double numOfPacketsInWindow = 0;
		double measuringWindowInSec =  MEASURING_WINDOW/1000; //seconds
		long startTime = 0;
		long endTime = 0;
		
	
		JavaRDD<Packet> packetsFiltered = packets.filter(r -> 
			r.getNumOfPackets() > 0 && 
			r.getNumOfBytes() > 0 &&
			r.getTimestamp() > 0);
		
		
		ArrayList<Packet> tmpPackets = new ArrayList<Packet>(packetsFiltered.collect());
		tmpPackets.sort(new TimestampComparator());
		System.out.println("packets in list: " + tmpPackets.size());
		
		
		while(windowEndIndex < tmpPackets.size()){
			startTime = tmpPackets.get(windowStartIndex).getTimestamp(); 	//in milliseconds
			endTime = tmpPackets.get(windowEndIndex).getTimestamp();		//in milliseconds
			numOfPacketsInWindow += tmpPackets.get(windowEndIndex).getNumOfPackets();
			
			//set window size
			while (++windowEndIndex < tmpPackets.size() && ((endTime - startTime) < MEASURING_WINDOW )) {

				if(startTime > endTime){
					System.out.println("start time " + startTime + " end time " + endTime);
					throw new InvalidDatasetException("Data is not chronological");
				}
				
				numOfPacketsInWindow += tmpPackets.get(windowEndIndex).getNumOfPackets();
				endTime = tmpPackets.get(windowEndIndex).getTimestamp();
			}
			
			//check if there is a potential attack in measuring window
			if(numOfPacketsInWindow/measuringWindowInSec > PACKET_RATE_THRESHOLD){
				
				attackList.add(new Attack(startTime, endTime));
				
			}
			numOfPacketsInWindow -= tmpPackets.get(windowStartIndex++).getNumOfPackets();
			
		}
		
		return attackList;
	}
	
	@Override
	protected JavaRDD<Attack> clusterPotentialAttacks(ArrayList<Attack> attackList){
		ArrayList<Attack> clusteredAttackList = new ArrayList<Attack>();
		int clusteredAttackIndex = 0;
		

		clusteredAttackList.add(new Attack(attackList.get(0)));
		
		for (Attack attack : attackList) {
			Attack clusteredAttack = clusteredAttackList.get(clusteredAttackIndex);
			if(attack.getStartTime() >= clusteredAttack.getStartTime() &&
					attack.getStartTime() <= clusteredAttack.getEndTime()){
				clusteredAttack.setEndTime(attack.getEndTime());	
			}
			else{
				clusteredAttackList.add(new Attack(attack));
				clusteredAttackIndex++;
			}
		}
		System.out.println("Num of Attacks: " + clusteredAttackList.size());
		return AttackManager.sc.parallelize(clusteredAttackList);
	}
	
	@Override
	protected JavaRDD<Attack> discardIrrelevantAttacks(JavaRDD<Attack> attackList){
		
		return attackList.filter(r -> (r.getEndTime() - r.getStartTime()) > ATTACK_MIN_LENGTH);
	}
	
	@Override
	protected  JavaRDD<Attack> fillAttacksWithPacketsData(JavaRDD<Attack> attacks, JavaRDD<Packet> packets){
		ArrayList<Attack> attackList = new ArrayList<>(attacks.collect());
		ArrayList<Attack> filledAttacks = new ArrayList<>();
		
		for (Attack attack : attackList) {
			filledAttacks.add(new Attack(
					attack.getStartTime(),
					attack.getEndTime(),
					(ArrayList<Packet>) packets.filter(p ->
						p.getTimestamp() >= attack.getStartTime() &&
						p.getTimestamp() <= attack.getEndTime()).collect()));
		}
		
		
		return AttackManager.sc.parallelize(filledAttacks);
	}
	
	private class MapAttacksToPacketsList implements Function2<Attack,JavaRDD<Packet>,ArrayList<Packet>>{

		@Override
		public ArrayList<Packet> call(Attack attack, JavaRDD<Packet> packets) throws Exception {
			ArrayList<Packet> packetsList;
			packetsList = new ArrayList<>(packets.filter(p ->
				p.getTimestamp() >= attack.getStartTime() &&
				p.getTimestamp() <= attack.getEndTime()).collect());
			return packetsList;
		}
		
	}
}
