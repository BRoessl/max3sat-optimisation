package de.roessl.annealing;

import java.util.HashMap;
import java.util.Map;

import de.roessl.cnf.CNF3;
import de.roessl.optimization.TimedMAX3SATOptimization;

public class TimedSimulatedAnnealing extends SimulatedAnnealing implements TimedMAX3SATOptimization {

	TimedSimulatedAnnealing(double initialTemperature) {
		super(initialTemperature, 0);
		additionalMetrics.put("temperature", this.currentTemperature);
	}

	public TimedSimulatedAnnealing(double initialTemperature, long maxRuntime, CNF3 cnf3) {
		this(initialTemperature);
		setProblem(cnf3);
		setMaxRuntime(maxRuntime);
		init();
	}

	protected long overallDurationNano;

	@Override
	public void executeOptimizationStep() {
		long start = System.nanoTime();
		super.executeOptimizationStep();
		overallDurationNano = overallDurationNano + (System.nanoTime() - start);
	}

	private Map<String, Object> additionalMetrics = new HashMap<>();

	public Map<String, Object> getAdditionalMetrics() {
		return additionalMetrics;
	}

	@Override
	public long getOverallDuration() {
		return overallDurationNano;
	}

	long maxRuntime = Long.MAX_VALUE;

	public void setMaxRuntime(long maxRuntime) {
		this.maxRuntime = maxRuntime;
	}

	@Override
	public boolean fulfilsStoppingCriterion() {
		return super.fulfilsStoppingCriterion() || (overallDurationNano > maxRuntime);
	}

	@Override
	protected void executeCoolingFunction() {
		if (maxRuntime == Long.MAX_VALUE) {
			throw new IllegalStateException("no runtime set");
		} else {
			double exponent = (double)overallDurationNano/(double)maxRuntime;
			this.currentTemperature  = this.initialTemperature * Math.pow((1.0d/this.initialTemperature), exponent);
		}
		additionalMetrics.put("temperature", this.currentTemperature);
	}
	
	
	@Override
	public long getMaxRuntime() {
		return maxRuntime;
	}

}
