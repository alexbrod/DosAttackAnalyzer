package strategyInterfaces;

import java.util.ArrayList;

import org.apache.spark.api.java.JavaRDD;

import bl.Packet;

public interface DatasetLoadStrategy{

	
	public JavaRDD<Packet> loadData(String dataPath);
	
	public JavaRDD<Packet> loadData(ArrayList<String> dataPath);
}
