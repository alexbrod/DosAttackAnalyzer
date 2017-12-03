package factory;

import org.apache.spark.api.java.JavaPairRDD;

import bl.Attack;
import strategyInterfaces.VisualizationStrategy;

public interface AttackVisualizer {
	public void visualize(JavaPairRDD<Attack,Integer> attacks);
	public void setVisualizationStrategy(VisualizationStrategy visualizationStrategy);
}
