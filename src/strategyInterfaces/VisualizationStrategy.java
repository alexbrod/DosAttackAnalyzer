package strategyInterfaces;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import bl.Attack;

public interface VisualizationStrategy {
	public void visualize(JavaPairRDD<Attack,Integer> attacks);
}
