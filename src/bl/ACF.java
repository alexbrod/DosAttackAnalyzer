package bl;

import java.io.Serializable;
import java.util.ArrayList;

public class ACF implements Serializable {
	private ArrayList<Double> x;
	private ArrayList<Double> originalY;
	private ArrayList<Double> autoCorrelatedY;
	
	public ACF(Segment segment){
		x = new ArrayList<Double>();
		originalY = new ArrayList<Double>();
		for (Packet packets : segment.getPacketsArray()) {
			x.add((double) packets.getTimestamp());
			originalY.add((double) packets.getNumOfPackets());
		}
	}
	
	public ACF clacACF(){
	    double sum;
	    autoCorrelatedY = new ArrayList<Double>();
	    for (int i = 0; i < x.size(); i++) {
	        sum = 0;
	        for (int j = 0; j < x.size() - i; j++) {
	            sum += originalY.get(j) * originalY.get(j+i);
	        }
	        autoCorrelatedY.add(sum);
	    }
	    
	    return this;
	}

	public ArrayList<Double> getX() {
		return x;
	}

	public ArrayList<Double> getOriginalY() {
		return originalY;
	}

	public ArrayList<Double> getAutoCorrelatedY() {
		return autoCorrelatedY;
	}
	
	
}
