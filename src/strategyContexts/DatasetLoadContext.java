package strategyContexts;

import java.util.ArrayList;

import org.apache.spark.api.java.JavaRDD;

import bl.DosAttackDatasetLoader;
import bl.Packet;
import factory.DatasetLoader;
import strategyInterfaces.DatasetLoadStrategy;

public class DatasetLoadContext implements DatasetLoader {
	private JavaRDD<Packet> packetDataset;
	private DatasetLoadStrategy datasetLoadStrategy;
	

	public DatasetLoadContext() {
		this.datasetLoadStrategy = new DosAttackDatasetLoader();
	}

	public DatasetLoadStrategy getDatasetLoadStrategy() {
		return datasetLoadStrategy;
	}
	@Override
	public void setDatasetLoadStrategy(DatasetLoadStrategy datasetLoadStrategy) {
		this.datasetLoadStrategy = datasetLoadStrategy;
	}
	@Override
	public JavaRDD<Packet> getPacketDataset() {
		return packetDataset;
	}
	@Override
	public void loadData(String datasetPath){
		packetDataset = this.datasetLoadStrategy.loadData(datasetPath);
	}
	@Override
	public void loadData(ArrayList<String> datasetPath){
		packetDataset = this.datasetLoadStrategy.loadData(datasetPath);
	}

	
	

	
}
