package de.roessl.abc;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import de.roessl.cnf.CNF3;
import de.roessl.cnf.CNF3Parser;
import de.roessl.optimization.MAX3SATOptimization;

public class DiscreteABCTest {

	@Test
	public void testDiscreteABC_20() throws Exception {
		InputStream testResource = this.getClass().getResourceAsStream("/uf20-01.cnf");
		assert testResource != null;
		CNF3 testProblem = CNF3Parser.readDimacFormat(testResource);
		test(testProblem);
	}
	
	@Test
	public void testDiscreteABC_200() throws Exception {
		InputStream testResource = this.getClass().getResourceAsStream("/uf200-01.cnf");
		assert testResource != null;
		CNF3 testProblem = CNF3Parser.readDimacFormat(testResource);
		test(testProblem);
	}

	private void test(CNF3 testProblem) {
		MAX3SATOptimization discreteABC = new DiscreteABC(testProblem.getNumberOfVariables(), testProblem.getNumberOfVariables());
		discreteABC.setProblem(testProblem);
		discreteABC.init();
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		double printedFitness = discreteABC.getFitness();
		
		while (!discreteABC.fulfilsStoppingCriterion() && ! (stopWatch.getTime(TimeUnit.SECONDS) > 5)) {
			discreteABC.executeOptimizationStep();
			if (discreteABC.getFitness() != printedFitness) {
				System.out.println("Time: " + stopWatch.getTime() +"\nStep:"+ discreteABC.getStepCount()+  "\nFitness: " +  discreteABC.getFitness());
				printedFitness = discreteABC.getFitness();
			}
		}
		
		System.out.println(discreteABC.getSolution());
	}
}
