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
		}
	}
	
	
	Candidate getFittest() {
		int fittestIndex = resultScore.indexOf(Collections.max(resultScore));
		return population.get(fittestIndex);
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
			
			hasParticipated.set(i, true);
			hasParticipated.set(j, true);
			hasParticipated.set(k, true);
			hasParticipated.set(l, true);
			
			Candidate c1 = population.get(i);
			Candidate c2 = population.get(j);
			Candidate c3 = population.get(k);
			Candidate c4 = population.get(l);
			
			float f1, f2, f3, f4;
			if (!hasParticipated.get(i)) f1 = c1.fitness(); else f1 = resultScore.get(i);
			if (!hasParticipated.get(j)) f2 = c2.fitness(); else f2 = resultScore.get(j);
			if (!hasParticipated.get(k)) f3 = c3.fitness(); else f3 = resultScore.get(k);
			if (!hasParticipated.get(l)) f4 = c4.fitness(); else f4 = resultScore.get(l);
			
			resultScore.set(i, f1);
			resultScore.set(j, f2);
			resultScore.set(k, f3);
			resultScore.set(l, f4);
			
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
			
			boolean isChild1Good = child1.fitness() >= resultScore.get(w1Index);
			boolean isChild2Good = child2.fitness() >= resultScore.get(w2Index);
			
			newpopulation.add(isChild1Good ? child1 : w1);
			newpopulation.add(isChild2Good ? child2 : w2);
		}
		
		int j = (int) (populationSize * parentUsePercent / 100.0);
		for (int i=0; i < j; i++) {
			int index = resultScore.indexOf(Collections.max(resultScore));
			newpopulation.add(population.get(index));
			population.remove(index);
		}
		population = newpopulation;
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
		if (i == Candidate.SIZE-1) {
			c.weights[i] = rand.nextFloat() * (5.0f - 0.0f) + 0.0f;
		} else {
			c.weights[i] = rand.nextFloat() * (10.0f - 0.0f) - 10.0f;
		}
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
