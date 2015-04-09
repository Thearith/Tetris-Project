import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class GeneticAlgorithm {
	static long BEGIN;
	static final boolean _DEBUG = true;
	LinkedList<Candidate> population = new LinkedList<Candidate>();
	final Random rand = new Random();
	
	final int populationSize = 10;
	final int parentUsePercent = 10;
	
	public GeneticAlgorithm() {
		
		for (int i=0; i < populationSize; i++) {
			Candidate c = new Candidate();
			c.random();
			population.add(c);
			System.out.println("add new candidate");
		}
		System.out.println("out of for loop");
		//Collections.sort(population);	// sort method
		System.out.println("population completed");
	}
	
	LinkedList<Candidate> getBestTen() {
		return population;
	}
	
	Candidate getFittest() {
		Candidate fittest = population.get(0);
		for (int i=1; i < populationSize; i++) {
			if(fittest.fitness() < population.get(i).fitness()) {
				fittest = population.get(i);
			}
		}
		return fittest;
	}
	
	void produceNextGen() {
		System.out.println("producing new generation");
		LinkedList<Candidate> newpopulation = new LinkedList<Candidate>();
		
		while (newpopulation.size() < populationSize * (1.0 - (parentUsePercent / 100.0))) {
			int size = population.size();
			int i = rand.nextInt(size);
			int j, k, l;
			j = k = l = i;
			
			while (j == i) j = rand.nextInt(size);
			while (k == i || k == j) k = rand.nextInt(size);
			while (l == i || l == j || k == l) l = rand.nextInt(size);
			
			Candidate c1 = population.get(i);
			Candidate c2 = population.get(j);
			Candidate c3 = population.get(k);
			Candidate c4 = population.get(l);
			
			long f1 = c1.fitness();
			System.out.println(f1);
			long f2 = c2.fitness();
			long f3 = c3.fitness();
			long f4 = c4.fitness();
			
			System.out.println("tournament");
			
			Candidate w1, w2;
			if (f1 > f2) w1 = c1; else w1 = c2;
			if (f3 > f4) w2 = c3; else w2 = c4;
			
			Candidate child1, child2;
			
			Candidate[] childs = newChilds(w1, w2);
			child1 = childs[0];
			child2 = childs[1];
			
			double mutatePercent = 0.05;
			boolean m1 = rand.nextFloat() <= mutatePercent;
			boolean m2 = rand.nextFloat() <= mutatePercent;
			if (m1) mutate(child1);
			if (m2) mutate(child2);
			
			boolean isChild1Good = child1.fitness() >= w1.fitness();
			boolean isChild2Good = child2.fitness() >= w2.fitness();
			
			newpopulation.add(isChild1Good ? child1 : w1);
			newpopulation.add(isChild2Good ? child2 : w2);
		}
		int j = (int) (populationSize * parentUsePercent / 100.0);
		for (int i=0; i < j; i++) {
			newpopulation.add(population.get(i));
		}
		population = newpopulation;
		//Collections.sort(population);
	}
	
	Candidate newChild(Candidate c1, Candidate c2, int pivot) {
		Candidate child = new Candidate();
		for (int i=0; i < pivot; i++) {
			child.weights[i] = c1.weights[i];
		}
		for (int j=0; j < Candidate.SIZE; j++) {
			child.weights[j] = c2.weights[j];
		}
		return child;
	}
	
	void mutate(Candidate c) {
		int i = rand.nextInt(Candidate.SIZE);
		c.weights[i] = rand.nextFloat() * (1.0f - 0.0f) + 0.0f;
	}
	
	Candidate[] newChilds(Candidate c1, Candidate c2) {
		Candidate child1 = new Candidate();
		Candidate child2 = new Candidate();
		
		for (int i=0; i < Candidate.SIZE; i++) {
			boolean b = rand.nextFloat() >= 0.5;
			if (b) {
				child1.weights[i] = c1.weights[i];
				child2.weights[i] = c2.weights[i];
			} else {
				child1.weights[i] = c2.weights[i];
				child2.weights[i] = c1.weights[i];
			}
		}
		return new Candidate[] { child1, child2 };
	}
}
