
public class Tetris {
	
	private static final int RUN_TIME = 5;
	private static final int WEIGHTS = Candidate.SIZE;
	
	public static void main(String[] args) {	
		
		double[] optimalWeights;
		long[] time = new long[RUN_TIME];
		double[][] producedWeights = new double[RUN_TIME][WEIGHTS];
		
		GeneticAlgorithm pop = new GeneticAlgorithm();
		
		for (int i=0; i < RUN_TIME; i++) {
			long startTime = System.currentTimeMillis();
			
			System.out.println("This is the generation # " + i);
			pop.produceNextGen();
			
			long endTime = System.currentTimeMillis();
			time[i] = endTime - startTime;
			
			System.out.println("Time: " + time[i]);
			
			Candidate c = pop.getFittest();
			producedWeights[i] = c.weights;
			for(int j=0; j<WEIGHTS; j++)
				System.out.println("Weight " + j + " " + producedWeights[i][j]);
			float rowsCleared = pop.getFittestResult();
			System.out.println("which helped to clear" + rowsCleared);
		}
		
		//System.out.println("Checking for the best candidates");
		
//		try {
//			PrintWriter writer = new PrintWriter("best_candidates.txt");
//			for (int i=0; i<10; i++) {
//				for (int j=0; j<5; j++) {	
//					writer.println(pop.getBestTen().get(i).weights[j]);
//				}
//			}
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
		Candidate bestPlayer = pop.getFittest();
		optimalWeights = bestPlayer.weights;
		for(int i=0; i<optimalWeights.length; i++)
			System.out.println("Weight " + i + " " + optimalWeights[i]);
		
		PlayerSkeleton p = new PlayerSkeleton(optimalWeights);
		p.run();
	}
}
