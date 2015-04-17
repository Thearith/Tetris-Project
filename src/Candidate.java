import java.util.Collections;
import java.util.Random;
import java.util.Vector;

public class Candidate implements Comparable<Candidate>{
	public static final int SIZE = 6;
	public double[] weights;
	Vector< Double > centreWeightRange = new Vector <Double>();
	
	final Random rand = new Random();
	
	public Candidate(Vector <Double> expWeightRange) {
		weights = new double[SIZE];
		centreWeightRange = expWeightRange;
	}
	
	public Candidate(float[] expWeight) {
		weights = new double[SIZE];
		for (int i=0; i < SIZE; i++) {
			weights[i] = expWeight[i];
		}
	}
	
	void random() {
		for (int i=0; i < SIZE; i++) {
			weights[i] = rand.nextFloat() * (2.5f - 0.0f) + centreWeightRange.get(i) - 1.25f;
		}
	}
	
	float fitness() {
		//long startTime = System.currentTimeMillis();
		final int numGamePlays = 10;
		float meanNumRowsCleared = 0.0f;
		
		for(int i=0; i<numGamePlays; i++) {
			PlayerSkeleton p = new PlayerSkeleton(this.weights);
			p.run();
			meanNumRowsCleared += p.getRowsCleared();
//			if (p.getRowsCleared() < 100000) {
//				return 0.0f;
//			} else {
//				meanNumRowsCleared += p.getRowsCleared();
//			}
		}
		return meanNumRowsCleared / numGamePlays;
		
		//long endTime = System.currentTimeMillis();
		//return endTime - startTime;
	}
	
	public int compareTo(Candidate o) {
		float f1 = this.fitness();
		float f2 = o.fitness();
		if (f1 < f2) return 1;
		else if (f1 > f2) return -1;
		else return 0;
	}
}
