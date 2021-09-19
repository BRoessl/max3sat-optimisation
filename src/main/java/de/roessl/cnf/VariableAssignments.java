package de.roessl.cnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class VariableAssignments {

	final static Random random = new Random();

	//contains 1 at first element, when var_1 is assigned positive
	//contains -1 at first element, when var_1 is negated
	//contains 2 at second element, when var_2 is assigned positive
	//...
	//see also Dimac Format
	final int[] assignment;
	
	/**
	 * creates a assignment with N variables assigned positive or optionally randomized the assignments
	 * 
	 * @param nbvar N variables
	 * @param randomize
	 */
	public VariableAssignments(int nbvar, boolean randomize) {
		assignment = new int[nbvar];	
		for (int i = 0; i < assignment.length; i++) {
			assignment[i] = i+1;
			if (randomize && random.nextBoolean()) {
				assignment[i] = assignment[i]*-1;
			}
		}
	}
	
	/**
	 * @param assignmentToCopy must follow convention, e.g. new int[]{1,-2,3,4,-5,...}
	 */
	protected VariableAssignments(int[] assignmentToCopy) {
		this.assignment = new int[assignmentToCopy.length];	
		for (int i = 0; i < assignmentToCopy.length; i++) {
			if (Math.abs(assignmentToCopy[i]) != (i+1)) {
				throw new IllegalArgumentException("invalid assigment");
			}
			this.assignment[i] = assignmentToCopy[i];
		}
	}
	
	public VariableAssignments clone(VariableAssignments toCopy) {
		return new VariableAssignments(toCopy.assignment);
	}
	
	/**
	 * creates a neighbour assignment by flipping one random variable
	 * 
	 * @return the neighbour Solution
	 */
	public VariableAssignments createNeighbour() {
		VariableAssignments neighbour = clone(this);
		int variableToFlip = random.nextInt(assignment.length-1);
		neighbour.flipVariable(variableToFlip);
		return neighbour;
	}

	public boolean forLiteral(int i) {
		return assignment[Math.abs(i) - 1] == i;
	}
	
	@Override
	public String toString() {
		return "assignment=" + Arrays.toString(assignment);
	}

	/**
	 * creates a neighbour solution by using following heuristic:
	 * (1) collect literals of all unsatisified clauses for this assignment and problem
	 * (2) choose one random literal of that list
	 * (3) flips the variable of the random literal
	 * 
	 * the main idea is to preselect the literals in such way because if not, the neighbour assignment can only be worse.
	 */
	public VariableAssignments createNeighbour(CNF3 problem) {
		boolean[] clauseSatisfaction = problem.calculateClauseSatisfaction(this);
		ArrayList<Integer> literalPreselection = new ArrayList<>();
		for (int i = 0; i < clauseSatisfaction.length; i++) {
			if(!clauseSatisfaction[i]) {
				int[] unsatisfiedClause = problem.getClause(i);
				for (int literal : unsatisfiedClause) {
					literalPreselection.add(literal);
				}
			}
		}
		if (literalPreselection.size() == 0) {
			//solution is optimized, no possibility to find a better neighbour
			return this;
		}
		int randomLiteral = literalPreselection.get(random.nextInt(literalPreselection.size()));
		
		//create a copy and flip the chosen variable
		VariableAssignments neighbour = clone(this);
		int variable = Utils.getVariableForLiteral(randomLiteral);
		neighbour.flipVariable(variable);
		return neighbour;
	}
	
	private void flipVariable(int var) {
		assignment[var-1] = -1 * assignment[var-1];
	}
	
}
