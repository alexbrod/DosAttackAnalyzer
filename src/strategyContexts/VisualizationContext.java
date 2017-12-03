package strategyContexts;

import org.apache.spark.api.java.JavaPairRDD;

import bl.Attack;
import bl.DosAttackVisualizator;
import factory.AttackVisualizer;
import strategyInterfaces.VisualizationStrategy;

public class VisualizationContext implements AttackVisualizer{
	private VisualizationStrategy visualizationStrategy;
	
	public VisualizationContext(){
		visualizationStrategy = new DosAttackVisualizator();
	}

	public VisualizationStrategy getVisualizationStrategy() {
		return visualizationStrategy;
	}
	@Override
	public void setVisualizationStrategy(VisualizationStrategy visualizationStrategy) {
		this.visualizationStrategy = visualizationStrategy;
	}
	@Override
	public void visualize(JavaPairRDD<Attack,Integer> attacks){
		this.visualizationStrategy.visualize(attacks);
	}
}
