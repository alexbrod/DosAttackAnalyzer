package bl;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;

import startProgram.AttackManager;
import strategyInterfaces.DatasetLoadStrategy;
import templates.DatasetLoaderTemplate;

public class DosAttackDatasetLoader extends DatasetLoaderTemplate implements Serializable {
	
	
	
	@Override
	protected JavaRDD<Packet> load(String dataPath) {
		ArrayList<String> al = new ArrayList<>();
		al.add(dataPath);
		return loadData(al);
		
	}

	@Override
	protected JavaRDD<Packet> load(ArrayList<String> dataPath) {
		JavaRDD<String> tmpRDD = AttackManager.sc.textFile(dataPath.get(0));
		for (int i = 1; i < dataPath.size(); i++) {
			System.out.println("path is: " + dataPath.get(i));
			JavaRDD<String> rdd = AttackManager.sc.textFile(dataPath.get(i));
			tmpRDD.union(rdd);
		}
		JavaRDD<Packet> packetsRDD =  tmpRDD.map(new ReadDataToPacket());
		return packetsRDD;
	}
	
	
	private class ReadDataToPacket implements Function<String,Packet> , Serializable{

		@Override
		public Packet call(String s) throws Exception {
			String[] record = s.split(" ", 3);
			long timestamp = convertTimeToLong(record[0]);
	    	Packet p = new Packet(
	    			timestamp,
	    			Integer.parseInt(record[1]),
	    			Integer.parseInt(record[2]));
			return p;
		} 
	}

	private long convertTimeToLong(String time){
		/*  10 cahrs	- 10 digits of time and date
			1 char	 	- dot
			3 chars		- 3 digits for miliseconds
		*/
		String dateTime = time.substring(0, 10);
		String milis = time.substring(11,14); 
		return Long.parseLong(dateTime) * 1000 + Long.parseLong(milis);
		 
	}


}
