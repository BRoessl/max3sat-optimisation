package de.roessl.optimization;

import de.roessl.cnf.CNF3;
import de.roessl.cnf.VariableAssignments;

public interface MAX3SATOptimization {
	
	public void setProblem(CNF3 problem);

	public void init();
	
	public void executeOptimizationStep();
	
	public int getStepCount();
	
	public VariableAssignments getSolution();
		
	public double getFitness();
	
	public boolean fulfilsStoppingCriterion();

}
