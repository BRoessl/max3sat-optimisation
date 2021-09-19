package de.roessl.annealing;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import de.roessl.cnf.CNF3;
import de.roessl.cnf.CNF3Parser;
import de.roessl.optimization.MAX3SATOptimization;

public class SimulatedAnnealingTest {

	@Test
	public void testSimulatedAnnealing_20() throws Exception {
		InputStream testResource = this.getClass().getResourceAsStream("/uf20-01.cnf");
		assert testResource != null;
		CNF3 testProblem = CNF3Parser.readDimacFormat(testResource);
		test(testProblem);
	}
	
	@Test
	public void testSimulatedAnnealing_200() throws Exception {
		InputStream testResource = this.getClass().getResourceAsStream("/uf200-01.cnf");
		assert testResource != null;
		CNF3 testProblem = CNF3Parser.readDimacFormat(testResource);
		test(testProblem);
	}

	private void test(CNF3 testProblem) {
		MAX3SATOptimization simulatedAnnealing = new SimulatedAnnealing(10000000, 0.0001);
		simulatedAnnealing.setProblem(testProblem);
		simulatedAnnealing.init();
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		double printedFitness = simulatedAnnealing.getFitness();
		
		while (!simulatedAnnealing.fulfilsStoppingCriterion() && ! (stopWatch.getTime(TimeUnit.SECONDS) > 5)) {
			simulatedAnnealing.executeOptimizationStep();
			if (simulatedAnnealing.getFitness() != printedFitness) {
				System.out.println("Time: " + stopWatch.getTime() +"\nStep:"+ simulatedAnnealing.getStepCount()+  "\nFitness: " +  simulatedAnnealing.getFitness());
				printedFitness = simulatedAnnealing.getFitness();
			}
		}
		
		System.out.println(simulatedAnnealing.getSolution());

	}


}
