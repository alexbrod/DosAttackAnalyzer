package bl;

import java.io.Serializable;
import java.util.ArrayList;

public class Attack implements Serializable{
	private long startTime;
	private long endTime;
	private ArrayList<Packet>	packetsArray;

	
	public Attack(long startTime, long endTime, ArrayList<Packet> packetsArray) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.packetsArray = copyPacketsArray(packetsArray);
	}
	
	public Attack(long startTime, long endTime){
		this(startTime, endTime, new ArrayList<Packet>());
	}
	
	public Attack(Attack attack){
		this(attack.getStartTime(), attack.getEndTime(), attack.getPacketsArray());
	}
	
	public long getStartTime() {
		return startTime;
	}



	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}



	public long getEndTime() {
		return endTime;
	}



	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	
	
	public ArrayList<Packet> getPacketsArray() {
		return packetsArray;
	}

	

	private ArrayList<Packet> copyPacketsArray(ArrayList<Packet> packetsArray){
		ArrayList<Packet> tmp = new ArrayList<Packet>();
		for (Packet packet : packetsArray) {
			tmp.add(new Packet(packet));
		}
		return tmp;
	}



	@Override
	public String toString() {
		return "Attack [startTime=" + startTime + ", endTime=" + endTime
				+ ", duration=" + (endTime - startTime) +", numOfPackets=" + getNumOfPackets() + "]";
		
	}
	
	public String toStringLong(){
		return "Attack [startTime=" + startTime + ", endTime=" + endTime 
				+ ", duration=" + (endTime - startTime) + ", packetsArray=" + packetsArray + "]";
	}
	
	public long getNumOfPackets(){
		long sum = 0;
		for (Packet packets : packetsArray) {
			sum += packets.getNumOfPackets();
		}
		return sum;
	}
	
	public long getNumOfBytes(){
		long sum = 0;
		for (int i = 0; i < packetsArray.size(); i++) {
			sum += packetsArray.get(i).getNumOfBytes();
		
		}
		return sum;
	}
	
	
}
