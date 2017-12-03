package strategyInterfaces;

import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import bl.Packet;

public interface DetectionStrategy {
	//create a data set of attacks (if detected)
	public JavaRDD<Attack> detectAttacks(JavaRDD<Packet> dataset);
}
