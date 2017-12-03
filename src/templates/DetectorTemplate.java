package templates;

import java.util.ArrayList;

import org.apache.spark.api.java.JavaRDD;

import bl.Attack;
import bl.Packet;
import exceptions.InvalidDatasetException;
import strategyInterfaces.DetectionStrategy;

public abstract class DetectorTemplate implements DetectionStrategy {
	
	protected abstract ArrayList<Attack> findPotentialAttacks(JavaRDD<Packet> packets) throws InvalidDatasetException;
	protected abstract JavaRDD<Attack> clusterPotentialAttacks(ArrayList<Attack> attackList);
	protected abstract JavaRDD<Attack> discardIrrelevantAttacks(JavaRDD<Attack> attackList);
	protected abstract JavaRDD<Attack> fillAttacksWithPacketsData(JavaRDD<Attack> attacks, JavaRDD<Packet> packets);
	
	@Override
	public JavaRDD<Attack> detectAttacks(JavaRDD<Packet> dataset) {
		ArrayList<Attack> potentialAttackList;
		try {
			potentialAttackList = findPotentialAttacks(dataset);
			System.out.println("Find potential attacks - COMPLETE");
			
			JavaRDD<Attack> clusteredPotentialAttackList = clusterPotentialAttacks(potentialAttackList);
			System.out.println("Cluster attacks - COMPLETE");
			
			
			System.out.println("Trying to drop small attacks...");
			
			JavaRDD<Attack> discardedAttacks = discardIrrelevantAttacks(clusteredPotentialAttackList);
			System.out.println("Drop small attacks - COMPLETE");
			
			JavaRDD<Attack> filledAttacks = fillAttacksWithPacketsData(discardedAttacks, dataset);

			return filledAttacks;
			
		} catch (NullPointerException | InvalidDatasetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
}
