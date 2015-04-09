import java.util.Random;

public class Candidate implements Comparable<Candidate>{
	public static final int SIZE = 5;
	public double[] weights;
	final Random rand = new Random();
	
	public Candidate() {
		weights = new double[SIZE];
	}
	
	void random() {
		for (int i=0; i < weights.length; i++) {
			weights[i] = rand.nextFloat() * (1.0f - 0.0f) - 1.0f;
		}
	}
	
	long fitness() {
		//long startTime = System.currentTimeMillis();
		
		PlayerSkeleton p = new PlayerSkeleton(this.weights);
		p.run();
		return p.getRowsCleared();
		
		//long endTime = System.currentTimeMillis();
		//return endTime - startTime;
	}
	
	public int compareTo(Candidate o) {
		long f1 = this.fitness();
		long f2 = o.fitness();
		if (f1 < f2) return 1;
		else if (f1 > f2) return -1;
		else return 0;
	}
}
