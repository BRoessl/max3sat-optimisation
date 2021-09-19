package de.roessl.cnf;

import static org.junit.Assert.*;

import org.junit.Test;

public class VariableAssignmentsTest {

	@Test
	public void testAssignment() throws Exception {
		VariableAssignments assignment = new VariableAssignments(100, true);
		System.out.println(assignment.toString());
	}

	@Test
	public void testForLiteral() throws Exception {
		VariableAssignments assignment = new VariableAssignments(new int[] {-1,2,3,-4});
		assertTrue(assignment.forLiteral(-1));
		assertFalse(assignment.forLiteral(1));
		assertTrue(assignment.forLiteral(3));
		assertFalse(assignment.forLiteral(-3));
	}

	@Test
	public void testAssignmentIntArray() throws Exception {
		new VariableAssignments(new int[] {-1,2,3,-4});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAssignmentIntArray_Invalid() throws Exception {
		new VariableAssignments(new int[] {-1,2,3,-4,7});
	}


}
