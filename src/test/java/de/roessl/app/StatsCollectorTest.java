package de.roessl.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.roessl.annealing.TimedSimulatedAnnealing;
import de.roessl.cnf.CNF3;
import de.roessl.optimization.TimedMAX3SATOptimization;
import de.roessl.testset.TestSet;
import de.roessl.testset.TestSet.SubSet;

public class StatsCollectorTest {

	@Test
	public void testRun2() throws Exception {
		List<CNF3> problems = TestSet.getAllProblems(SubSet.uf200_860);
		List<TimedMAX3SATOptimization> simA_Runners = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			CNF3 cnf3 = problems.get(i);
			simA_Runners.add(new TimedSimulatedAnnealing(100000, TimeUnit.SECONDS.toNanos(1), cnf3));
		}

		StatsCollector statsCollector = new StatsCollector(simA_Runners);
		statsCollector.run();
		
		for (int j = 0; j < statsCollector.fitnessMeans.size(); j++) {
			Double mean = statsCollector.fitnessMeans.get(j);
			System.out.println(mean);
		}
	}

}
