import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.logging.LogManager;

public class Tetris {
	
	public static void main(String[] args) {
		
		double[] optimalWeights = new double[4];
		GeneticAlgorithm pop = new GeneticAlgorithm();
		
		for (int i=0; i < 10; i++) {
			System.out.println("This is the generation # " + i);
			pop.produceNextGen();
		}
		
		//System.out.println("Checking for the best candidates");
		
		try {
			PrintWriter writer = new PrintWriter("best_candidates.txt");
			for (int i=0; i<10; i++) {
				for (int j=0; j<5; j++) {	
					writer.println(pop.getBestTen().get(i).weights[j]);
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Candidate bestPlayer = pop.getFittest();
		optimalWeights = bestPlayer.weights;
		System.out.println(optimalWeights[0]);
		System.out.println(optimalWeights[1]);
		System.out.println(optimalWeights[2]);
		System.out.println(optimalWeights[3]);
		System.out.println(optimalWeights[4]);
		//double[] optimalWeights = {0.0, -0.66569, -0.24077, -1/21.0, -0.46544};
		
		PlayerSkeleton p = new PlayerSkeleton(optimalWeights);
		p.run();
	}
}
