package de.roessl.cnf;

import static org.junit.Assert.*;

import org.junit.Test;

public class FitnessTest {

	@Test
	public void testOf() throws Exception {
		CNF3 cnf3 = new CNF3(3, 4);
		cnf3.putClause(0, -1, 2, 3);
		cnf3.putClause(1, 1, -2, 3);
		cnf3.putClause(2, 1, 2, -3);
		cnf3.putClause(3, 1, 2, 3);
		cnf3.valid();
		System.out.println(cnf3.toString());

		VariableAssignments assignment = new VariableAssignments(new int[] { 1, 2, 3 });
		double fitness = Fitness.of(assignment, cnf3);
		assertEquals(1.0, fitness, 0.01);
		
		VariableAssignments assignment2 = new VariableAssignments(new int[] { 1, -2, -3 });
		double fitness2 = Fitness.of(assignment2, cnf3);
		assertEquals(0.75, fitness2, 0.01);
	}

}
