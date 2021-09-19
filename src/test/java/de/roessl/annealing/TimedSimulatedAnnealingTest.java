package de.roessl.annealing;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import de.roessl.cnf.CNF3;
import de.roessl.cnf.CNF3Parser;

public class TimedSimulatedAnnealingTest {

	@Test
	public void testExecuteCoolingFunction() throws Exception {
		TimedSimulatedAnnealing timedSimulatedAnnealing = new TimedSimulatedAnnealing(100);
		timedSimulatedAnnealing.setMaxRuntime(10000000);
		timedSimulatedAnnealing.overallDurationNano = 0;
		
		Assert.assertEquals(100, timedSimulatedAnnealing.currentTemperature, 0.0001d);
		
		timedSimulatedAnnealing.overallDurationNano = 10000000;
		timedSimulatedAnnealing.executeCoolingFunction();
		Assert.assertEquals(1, timedSimulatedAnnealing.currentTemperature, 0.0001d);
	}
	
	@Test
	public void testTimedSimulatedAnnealing_200() throws Exception {
		InputStream testResource = this.getClass().getResourceAsStream("/uuf250-01.cnf");
		assert testResource != null;
		CNF3 testProblem = CNF3Parser.readDimacFormat(testResource);

		TimedSimulatedAnnealing simulatedAnnealing = new TimedSimulatedAnnealing(100d, TimeUnit.MILLISECONDS.toNanos(20), testProblem);
		System.out.println(simulatedAnnealing.overallDurationNano + " " + simulatedAnnealing.currentTemperature);

		while (!simulatedAnnealing.fulfilsStoppingCriterion()) {
			simulatedAnnealing.executeOptimizationStep();
			System.out.println(simulatedAnnealing.overallDurationNano + " " + simulatedAnnealing.currentTemperature);
		}
		System.out.println(simulatedAnnealing.overallDurationNano + " " + simulatedAnnealing.currentTemperature);
	}

}
