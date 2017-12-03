package comparators;

import java.util.Comparator;

import bl.Packet;

public class TimestampComparator implements Comparator<Packet>{

	@Override
	public int compare(Packet o1, Packet o2) {
		long t1 = o1.getTimestamp();
		long t2 = o2.getTimestamp();
		return t1 < t2 ? -1 : t1 == t2 ? 0:1;
	}
	

}
