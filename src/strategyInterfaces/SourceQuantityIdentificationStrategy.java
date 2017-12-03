package strategyInterfaces;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import scala.Tuple2;

public interface SourceQuantityIdentificationStrategy {
	//identify if the attacks are single source or multi-source
	//return positive if multi-source
	//return negative if single source
	//return zero if unknown
	public JavaPairRDD<Attack,Integer> identifySourceQuantity(JavaRDD<Attack> attacksDataset);
}
