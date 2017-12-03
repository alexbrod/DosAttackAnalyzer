package comparators;

import java.util.Comparator;

import bl.Attack;

public class AttackTimestampComparator implements Comparator<Attack> {

	@Override
	public int compare(Attack a1, Attack a2) {
		long t1 = a1.getStartTime();
		long t2 = a2.getStartTime();
		return t1 < t2 ? -1 : t1 == t2 ? 0:1;
	}

}
