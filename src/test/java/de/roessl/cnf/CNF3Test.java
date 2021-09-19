package de.roessl.cnf;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CNF3Test {

	@Test
		public void testCalculateClauseSatisfaction() throws Exception {
			CNF3 cnf3 = new CNF3(3, 4);
			cnf3.putClause(0, -1, 2, 3);
			cnf3.putClause(1, 1, -2, 3);
			cnf3.putClause(2, 1, 2, -3);
			cnf3.putClause(3, 1, 2, 3);
			cnf3.valid();
			System.out.println(cnf3.toString());
	
			VariableAssignments assignment = new VariableAssignments(new int[] { 1, 2, 3 });
			boolean[] checkAssignment = cnf3.calculateClauseSatisfaction(assignment);
			assertArrayEquals(new boolean[] { true, true, true, true }, checkAssignment);
	
			VariableAssignments assignment2 = new VariableAssignments(new int[] { 1, -2, -3 });
			boolean[] checkAssignment2 = cnf3.calculateClauseSatisfaction(assignment2);
			assertArrayEquals(new boolean[] { false, true, true, true }, checkAssignment2);
		}
}
