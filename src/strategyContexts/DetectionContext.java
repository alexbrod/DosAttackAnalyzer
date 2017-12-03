package strategyContexts;

import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import bl.DosAttackDetector;
import bl.Packet;
import factory.AttackDetector;
import strategyInterfaces.DetectionStrategy;

public class DetectionContext implements AttackDetector{
	private DetectionStrategy detectionStrategy;
	private JavaRDD<Attack> attackDataset;

	
	
	public DetectionContext() {
		this.detectionStrategy = new DosAttackDetector();
	}

	public DetectionStrategy getDetectionStrategy() {
		return detectionStrategy;
	}
	@Override
	public void setDetectionStrategy(DetectionStrategy detectionStrategy) {
		this.detectionStrategy = detectionStrategy;
	}
	@Override
	public JavaRDD<Attack> getAttackDataset() {
		return attackDataset;
	}
	@Override
	public void detectAttacks(JavaRDD<Packet> dataset){
		attackDataset = this.detectionStrategy.detectAttacks(dataset);
	}
	
	
}
