package factory;

import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import bl.Packet;
import strategyInterfaces.DetectionStrategy;

public interface AttackDetector {
	public JavaRDD<Attack> getAttackDataset();
	
	public void detectAttacks(JavaRDD<Packet> dataset);
	public void setDetectionStrategy(DetectionStrategy detectionStrategy);
}
