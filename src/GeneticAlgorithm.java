import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Random;

public class GeneticAlgorithm {
	//static long BEGIN;
	//static final boolean _DEBUG = true;
	LinkedList<Candidate> population = new LinkedList<Candidate>();
	Vector< Boolean > hasParticipated = new Vector< Boolean >();
	Vector< Float > resultScore = new Vector< Float >();
	final Random rand = new Random();
	
	final int populationSize = 60;
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
	
	boolean hasAllParticipated() {
		for (int i=0; i<population.size(); i++) {
			if (hasParticipated.get(i) == false) return false;
		}
		return true;
	}
	
	void initTraversal() {
		hasParticipated.clear();
		hasParticipated.addAll(Collections.nCopies(population.size(), false));
		resultScore.clear();
		resultScore.addAll(Collections.nCopies(population.size(), 0.0f));
	}
	
	void produceNextGen() {
		System.out.println("producing new generation");
		LinkedList<Candidate> newpopulation = new LinkedList<Candidate>();
		initTraversal();
		
		//while (newpopulation.size() < populationSize * (1.0 - (parentUsePercent / 100.0))) {
		while (newpopulation.size() < populationSize * (1.0 - (parentUsePercent / 100.0))) {
			int size = population.size();
			int i, j, k, l;
			i = rand.nextInt(size);
			j = k = l = i;
			if (!hasAllParticipated()) {
				//do { i = rand.nextInt(size);} while (hasParticipated.get(i));
				while (hasParticipated.get(i)) i = rand.nextInt(size);
				j = k = l = i;
				
				while (j == i || hasParticipated.get(j)) j = rand.nextInt(size);
				while (k == i || k == j || hasParticipated.get(k)) k = rand.nextInt(size);
				while (l == i || l == j || k == l || hasParticipated.get(l)) l = rand.nextInt(size);
			} else {
				while (j == i) j = rand.nextInt(size);
				while (k == i || k == j) k = rand.nextInt(size);
				while (l == i || l == j || k == l) l = rand.nextInt(size);
			}
			
			hasParticipated.set(i, true);
			hasParticipated.set(j, true);
			hasParticipated.set(k, true);
			hasParticipated.set(l, true);
			
			Candidate c1 = population.get(i);
			Candidate c2 = population.get(j);
			Candidate c3 = population.get(k);
			Candidate c4 = population.get(l);
			
			float f1 = c1.fitness();
			float f2 = c2.fitness();
			float f3 = c3.fitness();
			float f4 = c4.fitness();
			
			System.out.println("f1 is " + f1);
			System.out.println("f2 is " + f2);
			System.out.println("f3 is " + f3);
			System.out.println("f4 is " + f4);
			
			resultScore.set(i, f1);
			resultScore.set(j, f2);
			resultScore.set(k, f3);
			resultScore.set(l, f4);
			
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
			
			if (isChild1Good) System.out.println("add child1"); else System.out.println("add w1");
			if (isChild2Good) System.out.println("add child2"); else System.out.println("add w2");
			
			newpopulation.add(isChild1Good ? child1 : w1);
			newpopulation.add(isChild2Good ? child2 : w2);
		}
		// Use only top 10% from the parent generation for the next generation
		int j = (int) (populationSize * parentUsePercent / 100.0);
		for (int i=0; i < j; i++) {
			int index = resultScore.indexOf(Collections.max(resultScore));
			newpopulation.add(population.get(index));
			population.remove(index);
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
