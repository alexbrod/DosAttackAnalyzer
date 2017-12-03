package templates;
import java.util.ArrayList;

import org.apache.spark.api.java.JavaRDD;

import bl.Packet;
import strategyInterfaces.DatasetLoadStrategy;

public abstract class DatasetLoaderTemplate implements DatasetLoadStrategy {
	
	protected abstract JavaRDD<Packet> load(String dataPath);
	protected abstract JavaRDD<Packet> load(ArrayList<String> dataPath);
	
	@Override
	public JavaRDD<Packet> loadData(String dataPath) {
		return load(dataPath);
	}

	@Override
	public JavaRDD<Packet> loadData(ArrayList<String> dataPath) {
		return load(dataPath);
	}

}
