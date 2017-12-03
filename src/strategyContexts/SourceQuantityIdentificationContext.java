package strategyContexts;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import bl.RampUp;
import factory.AttackIdentifier;
import strategyInterfaces.SourceQuantityIdentificationStrategy;

public class SourceQuantityIdentificationContext implements AttackIdentifier{
	private SourceQuantityIdentificationStrategy sourceQuantityIdentificationStrategy;
	private JavaPairRDD<Attack, Integer> quantityIdentifiedAttacks;
	
	public SourceQuantityIdentificationContext(){
		this.sourceQuantityIdentificationStrategy = new RampUp();
	}
	
	public SourceQuantityIdentificationStrategy getSourceQuantityIdentificationStrategy() {
		return sourceQuantityIdentificationStrategy;
	}
	@Override
	public void setSourceQuantityIdentificationStrategy(
			SourceQuantityIdentificationStrategy sourceQuantityIdentificationStrategy) {
		this.sourceQuantityIdentificationStrategy = sourceQuantityIdentificationStrategy;
	}
	@Override
	public JavaPairRDD<Attack, Integer> getQuantityIdentifiedAttacks() {
		return quantityIdentifiedAttacks;
	}
	@Override
	public void identifySourceQuantity(JavaRDD<Attack> dataset){
		quantityIdentifiedAttacks = this.sourceQuantityIdentificationStrategy.identifySourceQuantity(dataset);
	}
	
}
