package factory;

import strategyContexts.DatasetLoadContext;
import strategyContexts.DetectionContext;
import strategyContexts.SourceQuantityIdentificationContext;
import strategyContexts.VisualizationContext;

public class AttackAnalyzerFactory {
	

	public DatasetLoader getDatasetLoader() {
		return new DatasetLoadContext();
	}


	public AttackDetector getAttackDetector() {
		return new DetectionContext();
	}


	public AttackIdentifier getAttackIdentifier() {
		return new SourceQuantityIdentificationContext();
	}


	public AttackVisualizer getAttackVisualizer() {
		return new VisualizationContext();
	}
	
	
	
	
	
	
	
}
