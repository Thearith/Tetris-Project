
public class Tetris {
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		
		double[] optimalWeights = new double[4];
		System.out.println("creating population");
		GeneticAlgorithm pop = new GeneticAlgorithm();
		System.out.println("producing new generations");
		for (int i=0; i < 100; i++) {
			System.out.println(i);
			pop.produceNextGen();
		}
		
		Candidate bestPlayer = pop.getFittest();
		optimalWeights = bestPlayer.weights;
		System.out.println(optimalWeights);
		PlayerSkeleton p = new PlayerSkeleton(optimalWeights);
		p.run();
	}
}
