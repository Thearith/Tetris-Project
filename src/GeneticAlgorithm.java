import java.util.Collections;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Random;

public class GeneticAlgorithm {
	//static long BEGIN;
	//static final boolean _DEBUG = true;
	LinkedList<Candidate> population = new LinkedList<Candidate>();
	Vector< Boolean > hasParticipated = new Vector< Boolean >();
	Vector< Float > resultScore_curr_gen = new Vector< Float >();
	Vector< Float > resultScore_next_gen = new Vector< Float >();
	
	LinkedList<Candidate> expPopulation = new LinkedList<Candidate>();
	Vector< Float > expResults = new Vector< Float >();
	Vector< Double > expWeightRange = new Vector< Double >();
	final Random rand = new Random();
	
	final int populationSize = 20;
	final int parentUsePercent = 10;
	
	public GeneticAlgorithm() {
		//initialise();
		//expWeightRange = [-3.75, -1.25, -8.75, -8.75, -3.75, 6.25];
		expWeightRange.add(-3.75);
		expWeightRange.add(-1.25);
		expWeightRange.add(-8.75);
		expWeightRange.add(-8.75);
		expWeightRange.add(-3.75);
		expWeightRange.add(6.25);
		for (int i=0; i < populationSize; i++) {
			Candidate c = new Candidate(expWeightRange);
			c.random();
			population.add(c);
		}
	}
	
	void initialise() {
		System.out.println("initialising");
		expResults.addAll(Collections.nCopies(4096, 0.0f));
		expWeightRange.addAll(Collections.nCopies(6, 0.0d));
		
		for(float i = -1.25f; i > -10; i -= 2.5)
			for(float j = -1.25f; j > -10; j -= 2.5)
				for(float k = -1.25f; k > -10; k -= 2.5)
					for(float l = -1.25f; l > -10; l -= 2.5)
						for(float m = -1.25f; m > -10; m -= 2.5)
							for(float n = 1.25f; n < 10; n += 2.5) {
								float[] expWeight = new float[] {i, j, k, l, m, n};
								Candidate c = new Candidate(expWeight);
								expPopulation.add(c);
							}
		System.out.println("testing");
		for(int i=0; i<expPopulation.size(); i++) {
			Candidate c = expPopulation.get(i);
			float result = c.fitness();
			expResults.set(i, result);
			System.out.println(i);
		}
		
		System.out.println("weeding");
//		for(int i=0; i<populationSize; i++) {
//			int index = expResults.indexOf(Collections.max(expResults));
//			population.add(expPopulation.get(index));
//			expPopulation.remove(index);
//			expResults.remove(index);
//		}
		int index = expResults.indexOf(Collections.max(expResults));
		Candidate c = expPopulation.get(index);
		System.out.println("optimum range is +/- 1.25 of " + c.weights[0] + " " + c.weights[1] + " " + c.weights[2] + " " + c.weights[3] + " " + c.weights[4] + " " + c.weights[5]);
		for (int i=0; i<6; i++)
			expWeightRange.set(i, c.weights[i]);
	}
	
	Candidate getFittest() {
		int fittestIndex = resultScore_next_gen.indexOf(Collections.max(resultScore_next_gen));
		return population.get(fittestIndex);
	}
	
	float getFittestResult() {
		float fittestResult = Collections.max(resultScore_curr_gen);
		return fittestResult;
	}
	
	boolean hasAllParticipated() {
		for (int i=0; i<population.size(); i++) {
			if (hasParticipated.get(i) == false) return false;
		}
		return true;
	}
	
	void initTraversal() {
		hasParticipated.clear();
		hasParticipated.addAll(Collections.nCopies(populationSize, false));
		resultScore_curr_gen.clear();
		resultScore_curr_gen.addAll(Collections.nCopies(populationSize, 0.0f));
		resultScore_next_gen.clear();
	}
	
	void produceNextGen() {
		
		LinkedList<Candidate> newpopulation = new LinkedList<Candidate>();
		initTraversal();
		
		while (newpopulation.size() < populationSize * (1.0 - (parentUsePercent / 100.0))) {
			int size = population.size();
			int i, j, k, l;
			i = rand.nextInt(size);
			j = k = l = i;
			if (!hasAllParticipated()) {
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
				
			Candidate c1 = population.get(i);
			Candidate c2 = population.get(j);
			Candidate c3 = population.get(k);
			Candidate c4 = population.get(l);
			
			float f1, f2, f3, f4;
			if (!hasParticipated.get(i)) { 
				f1 = c1.fitness();
				resultScore_curr_gen.set(i, f1);
				hasParticipated.set(i, true);
			}	else 
				f1 = resultScore_curr_gen.get(i);
			
			if (!hasParticipated.get(j)) {
				f2 = c2.fitness(); 
				hasParticipated.set(j, true);
				resultScore_curr_gen.set(j, f2);
			} else
				f2 = resultScore_curr_gen.get(j);
			
			if (!hasParticipated.get(k)) {
				f3 = c3.fitness(); 
				hasParticipated.set(k, true);
				resultScore_curr_gen.set(k, f3);
			} else 
				f3 = resultScore_curr_gen.get(k);
		
			if (!hasParticipated.get(l)) {
				f4 = c4.fitness(); 
				hasParticipated.set(l, true);
				resultScore_curr_gen.set(l, f4);
			} else 
				f4 = resultScore_curr_gen.get(l);	
			
			Candidate w1, w2;
			int w1Index, w2Index;
			if (f1 > f2) {
				w1 = c1;
				w1Index = i;
			}else { 
				w1 = c2;
				w1Index = j;
			}
			if (f3 > f4) {
				w2 = c3;
				w2Index = k;
			} else {
				w2 = c4;
				w2Index = l;
			}
			
			Candidate child1, child2;
			
			Candidate[] childs = newChilds(w1, w2);
			child1 = childs[0];
			child2 = childs[1];
			
			double mutatePercent = 0.05;
			boolean m1 = rand.nextFloat() <= mutatePercent;
			boolean m2 = rand.nextFloat() <= mutatePercent;
			if (m1) mutate(child1);
			if (m2) mutate(child2);
			
			float child1Fitness = child1.fitness();
			float child2Fitness = child2.fitness();
			boolean isChild1Good = child1Fitness >= resultScore_curr_gen.get(w1Index);
			boolean isChild2Good = child2Fitness >= resultScore_curr_gen.get(w2Index);
			
			newpopulation.add(isChild1Good ? child1 : w1);
			newpopulation.add(isChild2Good ? child2 : w2);
			resultScore_next_gen.add(isChild1Good? child1Fitness : resultScore_curr_gen.get(w1Index));
			resultScore_next_gen.add(isChild2Good? child2Fitness : resultScore_curr_gen.get(w2Index));
		}
		
		int j = (int) (populationSize * parentUsePercent / 100.0);
		for (int i=0; i < j; i++) {
			int index = resultScore_curr_gen.indexOf(Collections.max(resultScore_curr_gen));
			newpopulation.add(population.get(index));
			population.remove(index);
			resultScore_curr_gen.remove(index);
		}
		population = newpopulation;
	}
	
	Candidate newChild(Candidate c1, Candidate c2, int pivot) {
		Candidate child = new Candidate(expWeightRange);
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
		c.weights[i] = rand.nextFloat() * (2.5f - 0.0f) + expWeightRange.get(i) - 1.25f;
	}
	
	Candidate[] newChilds(Candidate c1, Candidate c2) {
		Candidate child1 = new Candidate(expWeightRange);
		Candidate child2 = new Candidate(expWeightRange);
		
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
