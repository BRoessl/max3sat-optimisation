package de.roessl.cnf;

import java.util.Arrays;

public class CNF3 {
	
	private int nbvar;
	private int nbclauses;
	private int[][] clauses;

	public CNF3(int nbvar, int nbclauses) {
		this.nbvar = nbvar;
		this.nbclauses = nbclauses;
		this.clauses = new int[nbclauses][3];
	}

	public int getNumberOfClauses() {
		return nbclauses;
	}

	public int getNumberOfVariables() {
		return nbvar;
	}

	public void putClause(int idx, int l1, int l2, int l3) {
		clauses[idx][0] = l1;
		clauses[idx][1] = l2;
		clauses[idx][2] = l3;
	}
	
	public int[] getClause(int idx) {
		return clauses[idx];
	}

	public void valid() {
		for (int i = 0; i < clauses.length; i++) {
			for (int j = 0; j < clauses[i].length; j++) {
				int literal = clauses[i][j];
				if (literal == 0) {
					throw new IllegalStateException(String.format("uninitialized literal, clause %d, %d", i, j));
				}
				if (literal > nbvar || literal < -nbvar) {
					throw new IllegalStateException(String.format("literal out of bound, clause %d, %d - foudn %d", i, j, literal));
				}
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("CNF3SAT [clauses=");
		for (int[] clause : clauses) {
			sb.append(Arrays.toString(clause));
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * @param assignment 
	 * @return if a clause is satisfied, the boolean with the corresponding index is true, otherwise false
	 */
	public boolean[] calculateClauseSatisfaction(VariableAssignments assignment) {
		boolean[] clauseIsTrue = new boolean[nbclauses];
		for (int j = 0; j < clauses.length; j++) {
			int[] clause = clauses[j];
			for (int i : clause) {
				clauseIsTrue[j] = clauseIsTrue[j] | assignment.forLiteral(i);
			}
		}
		return clauseIsTrue;
	}

}
