package startProgram;



import java.time.LocalTime;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import bl.RampUp;
import bl.XMLConfig;
import factory.AttackAnalyzerFactory;
import factory.AttackDetector;
import factory.AttackIdentifier;
import factory.AttackVisualizer;
import factory.DatasetLoader;

public class AttackManager {
	public static JavaSparkContext sc;
	private static SparkConf sparkConf;
	
	public static void main(String[] args) {
		int st = LocalTime.now().toSecondOfDay();
		AttackManager attMan = new AttackManager();
		AttackAnalyzerFactory attackAnalyzerFactory = new AttackAnalyzerFactory();
		DatasetLoader datasetLoader = attackAnalyzerFactory.getDatasetLoader();
		AttackDetector attackDetector = attackAnalyzerFactory.getAttackDetector();
		AttackIdentifier attackIdentifier = attackAnalyzerFactory.getAttackIdentifier();
		AttackVisualizer attackVisualizer = attackAnalyzerFactory.getAttackVisualizer();
 		
		attMan.createSparkContext("/sparkCntrlConfig.xml"); 
		datasetLoader.loadData(XMLConfig.getDatasetPaths("/datasetsPath.xml"));
		attackDetector.detectAttacks(datasetLoader.getPacketDataset());
		//sourceQuantityIdentificationContext.setSourceQuantityIdentificationStrategy(new SpectralAnalysis());
		attackIdentifier.setSourceQuantityIdentificationStrategy(new RampUp());
		attackIdentifier.identifySourceQuantity(attackDetector.getAttackDataset());
		attackVisualizer.visualize(attackIdentifier.getQuantityIdentifiedAttacks());
		
		System.out.println("The time is: " + (LocalTime.now().toSecondOfDay() - st) + "s");
		System.out.println("finished program");
	}

	
	
	public void createSparkContext(String configFilePath){
		sparkConf = XMLConfig.getSparkConfigFromFile(configFilePath);
		sc = new JavaSparkContext(sparkConf);
	}
	
	public JavaSparkContext getSparkContext(){
		return sc;
	}
	
}
