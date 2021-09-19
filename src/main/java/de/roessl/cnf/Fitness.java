package de.roessl.cnf;

public class Fitness {

	public static double of(VariableAssignments assignment, CNF3 formula) {
		boolean[] checkAssignment = formula.calculateClauseSatisfaction(assignment);
		double p = 0;
		double n = 0;
		for (boolean b : checkAssignment) {
			if (b) {
				p = p+1.0;
			} else {
				n = n+1.0;
			}
		}
		return p/(p+n);
	}
	
	public static String visualize(VariableAssignments assignment, CNF3 formula) {
		boolean[] checkAssignment = formula.calculateClauseSatisfaction(assignment);
		StringBuilder builder = new StringBuilder();
		for (boolean b : checkAssignment) {
			if (b) {
				builder.append('1');
			} else {
				builder.append('0');	
			}
		}
		return builder.toString();
	}
	
}
