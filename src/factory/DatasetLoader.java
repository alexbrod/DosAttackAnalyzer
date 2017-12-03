package factory;

import java.util.ArrayList;

import org.apache.spark.api.java.JavaRDD;

import bl.Packet;
import strategyInterfaces.DatasetLoadStrategy;

public interface DatasetLoader {
	public void loadData(String dataPath);
	public void loadData(ArrayList<String> datasetPath);
	public JavaRDD<Packet> getPacketDataset();
	public void setDatasetLoadStrategy(DatasetLoadStrategy datasetLoadStrategy);
}
