/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.roessl.benchmark;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import de.roessl.abc.DiscreteABC;
import de.roessl.annealing.SimulatedAnnealing;
import de.roessl.cnf.CNF3;
import de.roessl.cnf.CNF3Parser;
import de.roessl.optimization.MAX3SATOptimization;
import de.roessl.testset.TestSet;

@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MAX3SATSyntheticBenchmark {

	@State(Scope.Thread)
	public static class AlgorithmState {

		public SimulatedAnnealing simulatedAnnealing_AcceptAlways;
		public SimulatedAnnealing simulatedAnnealing_AcceptNever;
		public DiscreteABC discreteABC_SmallColony_FastAbandonment;
		public DiscreteABC discreteABC_SmallColony_RestrainedAbandonment;
		public DiscreteABC discreteABC_MediumColony_FastAbandonment;
		public DiscreteABC discreteABC_MediumColony_RestrainedAbandonment;
		public DiscreteABC discreteABC_BigColony_RestrainedAbandonment;
		public DiscreteABC discreteABC_BigColony_FastAbandonment;

		@Setup(Level.Trial)
		public void doSetup() throws Exception {
			// using a large, unsatisifiable formula
			InputStream testFile = TestSet.getCNFResource(TestSet.SubSet.uuf250_1065, 1);
			CNF3 problem = CNF3Parser.readDimacFormat(testFile);

			List<MAX3SATOptimization> allAlgorithms = new ArrayList<>();
			// synthetic annealing with no cooling down so that stopping criteria is
			// impossible
			// worse solutions will always be accepted
			allAlgorithms.add(simulatedAnnealing_AcceptAlways = new SimulatedAnnealing(Double.MAX_VALUE, 0.0));
			// worse solutions will never be accepted
			allAlgorithms.add(simulatedAnnealing_AcceptNever = new SimulatedAnnealing(Double.MIN_VALUE, 0.0));

			int smallColony = 25; // one tenth of the medium colony
			int mediumColony = 250; // one bee for each variable
			int largeColony = 1065; // one bee for each clause

			int fastAbandonment = 1; // discard as soon as no better food source
			int restrainedAbandonment = 250; // for each variable one trial

			allAlgorithms.add(discreteABC_SmallColony_FastAbandonment = new DiscreteABC(smallColony, fastAbandonment));
			allAlgorithms.add(discreteABC_SmallColony_RestrainedAbandonment = new DiscreteABC(smallColony,
					restrainedAbandonment));
			allAlgorithms
					.add(discreteABC_MediumColony_FastAbandonment = new DiscreteABC(mediumColony, fastAbandonment));
			allAlgorithms.add(discreteABC_MediumColony_RestrainedAbandonment = new DiscreteABC(mediumColony,
					restrainedAbandonment));
			allAlgorithms.add(discreteABC_BigColony_FastAbandonment = new DiscreteABC(largeColony, fastAbandonment));
			allAlgorithms.add(
					discreteABC_BigColony_RestrainedAbandonment = new DiscreteABC(largeColony, restrainedAbandonment));

			// prepare
			for (MAX3SATOptimization max3satOptimization : allAlgorithms) {
				max3satOptimization.setProblem(problem);
				max3satOptimization.init();
			}
		}

		@TearDown(Level.Trial)
		public void doTearDown() {
			//
		}

	}

	@Benchmark
	public void SA_AA(AlgorithmState state) {
		state.simulatedAnnealing_AcceptAlways.executeOptimizationStep();
	}

	@Benchmark
	public void SA_AN(AlgorithmState state) {
		state.simulatedAnnealing_AcceptNever.executeOptimizationStep();
	}

	@Benchmark
	public void ABC_25_1(AlgorithmState state) {
		state.discreteABC_SmallColony_FastAbandonment.executeOptimizationStep();
	}

	@Benchmark
	public void ABC_25_250(AlgorithmState state) {
		state.discreteABC_SmallColony_RestrainedAbandonment.executeOptimizationStep();
	}

	@Benchmark
	public void ABC_250_1(AlgorithmState state) {
		state.discreteABC_MediumColony_FastAbandonment.executeOptimizationStep();
	}

	@Benchmark
	public void ABC_250_250(AlgorithmState state) {
		state.discreteABC_MediumColony_RestrainedAbandonment.executeOptimizationStep();
	}

	@Benchmark
	public void ABC_1065_1(AlgorithmState state) {
		state.discreteABC_BigColony_FastAbandonment.executeOptimizationStep();
	}

	@Benchmark
	public void ABC_1065_250(AlgorithmState state) {
		state.discreteABC_BigColony_RestrainedAbandonment.executeOptimizationStep();
	}

	/*
	 * public static void main(String[] args) throws RunnerException { Options
	 * options = new
	 * OptionsBuilder().include(MyBenchmark.class.getSimpleName()).forks(1).mode(
	 * Mode.AverageTime).warmupTime(TimeValue.seconds(1)).warmupIterations(1).
	 * measurementTime(TimeValue.seconds(5)).measurementIterations(1).build(); new
	 * Runner(options).run(); }
	 */

}
