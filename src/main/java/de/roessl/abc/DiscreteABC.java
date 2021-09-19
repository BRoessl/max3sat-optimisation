package de.roessl.abc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.roessl.cnf.CNF3;
import de.roessl.cnf.Fitness;
import de.roessl.cnf.VariableAssignments;
import de.roessl.optimization.MAX3SATOptimization;

public class DiscreteABC implements MAX3SATOptimization {

	public class FoodSource {
		int abandonmentCounter = 0;
		protected VariableAssignments solution;
		protected double fitness;
		
		public void initRandom() {
			solution = new VariableAssignments(problem.getNumberOfVariables(), true);
			fitness = Fitness.of(solution, problem);
			abandonmentCounter = 0;
		}
	}

	private int abandonmentLimit;
	private int employeedBeeSize;
	private int onlookerBeeSize;
	List<FoodSource> foodSources = new ArrayList<>();
	private CNF3 problem;

	protected VariableAssignments globalBestSolution;
	protected double globalBestFitness;
	private int steps;
	
	private Roulette roulette = new Roulette();
	private long totalAbandonments;

	public DiscreteABC(int colonySize, int abandonmentLimit) {
		this.abandonmentLimit = abandonmentLimit;
		this.employeedBeeSize = colonySize/2;
		this.onlookerBeeSize = colonySize/2;
	}
	
	@Override
	public void setProblem(CNF3 problem) {
		this.problem = problem;
	}
	
	@Override
	public void init() {
		for (int i = 0; i < employeedBeeSize; i++) {
			foodSources.add(new FoodSource());
		}
		for (FoodSource foodSource : foodSources) {
			foodSource.initRandom();
		}
		foodSources.forEach(initSource());
		updateGlobalBest();
	}

	public void executeOptimizationStep() {
		employeedBeePhase();
		onlookerBeePhase();
		scoutBeePhase();
		updateGlobalBest();
		steps++;
	}
	
	@Override
	public int getStepCount() {
		return steps;
	}
	
	@Override
	public VariableAssignments getSolution() {
		return globalBestSolution;
	}


	@Override
	public double getFitness() {
		return globalBestFitness;
	}

	private Consumer<? super FoodSource> initSource() {
		return source -> {
			source.solution = new VariableAssignments(problem.getNumberOfVariables(), true);
			source.fitness = Fitness.of(source.solution, problem);
			source.abandonmentCounter = 0;
		};
	}


	private void updateGlobalBest() {
		foodSources.forEach(bee -> {
			if (bee.fitness > globalBestFitness) {
				globalBestSolution = bee.solution;
				globalBestFitness = bee.fitness;
			}
		});
	}


	private void employeedBeePhase() {
		for (FoodSource foodSource : foodSources) {
			exploreNeighbourhood(foodSource);
		}
		
	}


	private void exploreNeighbourhood(FoodSource foodSource) {
		VariableAssignments neighbourSolution = foodSource.solution.createNeighbour(problem);
		double neighbourFitness = Fitness.of(neighbourSolution, problem);
		if (neighbourFitness > foodSource.fitness) {
			foodSource.solution = neighbourSolution;
			foodSource.fitness = neighbourFitness;
			foodSource.abandonmentCounter = 0;
		} else {
			foodSource.abandonmentCounter++;
		}
	}

	private void onlookerBeePhase() {
		double[] fitnessValues = new double[foodSources.size()];
		for (int i = 0; i < employeedBeeSize; i++) {
			fitnessValues[i] = foodSources.get(i).fitness;
		}
		roulette.update(fitnessValues);
		for (int i = 0; i < onlookerBeeSize; i++) {
			int beeIndex = roulette.select();
			FoodSource selectedBee = foodSources.get(beeIndex);
			exploreNeighbourhood(selectedBee);
		}
	}

	private void scoutBeePhase() {
		for (FoodSource foodSource : foodSources) {
			if (foodSource.abandonmentCounter >= this.abandonmentLimit) {
				foodSource.initRandom();
				totalAbandonments++;
			}
		}
	}

	@Override
	public boolean fulfilsStoppingCriterion() {
		if (globalBestFitness == 1.0) {
			return true;
		}
		return false;
	}
	
	public long getTotalAbandonments() {
		return totalAbandonments;
	}
	

}
