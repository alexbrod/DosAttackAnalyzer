package templates;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import strategyInterfaces.VisualizationStrategy;

public abstract class VisualizatorTemplate implements VisualizationStrategy {

	abstract protected void visualizeAttacks(JavaPairRDD<Attack,Integer> attacks);
	
	@Override
	public void visualize(JavaPairRDD<Attack,Integer> attacks) {
		visualizeAttacks(attacks);

	}

}
