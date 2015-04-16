import java.util.Random;

public class Candidate implements Comparable<Candidate>{
	public static final int SIZE = 6;
	public double[] weights;
	
	final Random rand = new Random();
	
	public Candidate() {
		weights = new double[SIZE];
	}
	
	void random() {
		for (int i=0; i < weights.length - 1; i++) {
			weights[i] = rand.nextFloat() * (10.0f - 0.0f) - 10.0f;
		}
		weights[SIZE-1] = rand.nextFloat() * (5.0f - 0.0f) + 0.0f;
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
