package bl;

import java.io.Serializable;
import java.util.ArrayList;

public class Segment implements Serializable {
	private ArrayList<Packet> packetsInSegment;
	
	public Segment(){
		packetsInSegment = new ArrayList<Packet>();
	}
	
	public void addPackets(Packet packets){
		packetsInSegment.add(packets);
	}
	
	public Packet getPacket(int index){
		return packetsInSegment.get(index);
	}
	
	public ArrayList<Packet> getPacketsArray(){
		return packetsInSegment;
	}
	
	public int calcMeanPacketRate(){
		int packetsSum = 0;
		for (Packet packets : this.packetsInSegment) {
			packetsSum += packets.getNumOfPackets();
		}
		return packetsSum / this.packetsInSegment.size();
	}


	
	
}
