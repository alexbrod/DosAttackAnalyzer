package factory;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import strategyInterfaces.SourceQuantityIdentificationStrategy;

public interface AttackIdentifier {
	public JavaPairRDD<Attack, Integer> getQuantityIdentifiedAttacks();

	public void identifySourceQuantity(JavaRDD<Attack> dataset);
	public void setSourceQuantityIdentificationStrategy(
			SourceQuantityIdentificationStrategy sourceQuantityIdentificationStrategy);
}
