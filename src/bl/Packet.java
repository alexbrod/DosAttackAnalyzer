package bl;

import java.io.Serializable;

public class Packet implements Serializable {
	private long timestamp;
	private int numOfPackets;
	private int numOfBytes;
	
	public Packet(long timestamp, int numOfPackets, int numOfBytes) {
		this.timestamp = timestamp;
		this.numOfPackets = numOfPackets;
		this.numOfBytes = numOfBytes;
	}
	
	public Packet(Packet packet){
		this.timestamp = packet.timestamp;
		this.numOfPackets = packet.numOfPackets;
		this.numOfBytes = packet.numOfBytes;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public int getNumOfPackets() {
		return numOfPackets;
	}
	public void setNumOfPackets(int numOfPackets) {
		this.numOfPackets = numOfPackets;
	}
	public int getNumOfBytes() {
		return numOfBytes;
	}
	public void setNumOfBytes(int numOfBytes) {
		this.numOfBytes = numOfBytes;
	}
	@Override
	public String toString() {
		return "Packets [timestamp=" + timestamp + ", numOfPackets=" + numOfPackets + ", numOfBytes=" + numOfBytes
				+ "]";
	}

	@Override
	public boolean equals(Object obj) {
		Packet p = (Packet)obj;
		return (p.getNumOfBytes() == this.numOfBytes &&
				p.getNumOfPackets() == this.numOfPackets &&
				p.getTimestamp() == this.getTimestamp());
	}
	
	
	
}
