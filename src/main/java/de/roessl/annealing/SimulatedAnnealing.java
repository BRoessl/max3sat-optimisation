package de.roessl.annealing;

import java.util.Random;

import de.roessl.cnf.VariableAssignments;
import de.roessl.cnf.CNF3;
import de.roessl.cnf.Fitness;
import de.roessl.optimization.MAX3SATOptimization;

public class SimulatedAnnealing implements MAX3SATOptimization {

	private final double coolingRate;
	protected double initialTemperature;
	protected double currentTemperature;
	private double targetTemperature = 1.0;
	private CNF3 problem;
	private VariableAssignments currentSolution;
	private VariableAssignments bestSolution;
 	private double fitnessCurrent;
	private double fitnessBest;
	
	private Random rnd = new Random();
	private int steps;
	
	public SimulatedAnnealing(double initialTemperature, double coolingRate) {
		this.initialTemperature = initialTemperature;
		this.currentTemperature = initialTemperature;
		this.coolingRate = coolingRate;
	}

	private void accept(VariableAssignments neighbourSolution) {
		currentSolution = neighbourSolution;
		fitnessCurrent = Fitness.of(currentSolution, problem); // this could be optimized, the calculation is duplicated within one iteration
	}

	@Override
	public void setProblem(CNF3 problem) {
		this.problem = problem;
	}

	@Override
	public void init() {
		bestSolution = currentSolution = new VariableAssignments(problem.getNumberOfClauses(), true);
		fitnessBest = fitnessCurrent = Fitness.of(currentSolution, problem);		
	}

	@Override
	public void executeOptimizationStep() {
		VariableAssignments neighbourSolution = currentSolution.createNeighbour(problem);
		double fitnessNeighbour = Fitness.of(neighbourSolution, problem);
		if (fitnessNeighbour > fitnessCurrent) {
			accept(neighbourSolution);
			if (fitnessCurrent > fitnessBest) {
				bestSolution = currentSolution;
				fitnessBest = fitnessCurrent;
			}
		} else {
			double acceptanceProbability = Math.exp((fitnessNeighbour - fitnessCurrent)/currentTemperature);
			if (acceptanceProbability > rnd.nextDouble()) {
				accept(neighbourSolution);
			}
		}
		executeCoolingFunction();
		steps++;
	}

	protected void executeCoolingFunction() {
		currentTemperature = (1-coolingRate)*currentTemperature;
	}
	
	@Override
	public int getStepCount() {
		return steps;
	}

	@Override
	public VariableAssignments getSolution() {
		return bestSolution;
	}

	@Override
	public double getFitness() {
		return fitnessBest;
	}

	@Override
	public boolean fulfilsStoppingCriterion() {
		if (fitnessBest == 1.0) {
			return true;
		}
		if (currentTemperature < targetTemperature) {
			return true;
		};
		return false;
	}
	
	
}
