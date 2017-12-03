package templates;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import strategyInterfaces.SourceQuantityIdentificationStrategy;

public abstract class SourceQuantityIdentifierTemplate implements SourceQuantityIdentificationStrategy {
	
	protected abstract JavaPairRDD<Attack, Integer> identifySrcQuantity(JavaRDD<Attack> attacksDataset);
	
	
	@Override
	public JavaPairRDD<Attack, Integer> identifySourceQuantity(JavaRDD<Attack> attacksDataset) {
		return identifySrcQuantity(attacksDataset);
	}

}
