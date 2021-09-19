package de.roessl.abc;

import java.util.HashMap;
import java.util.Map;

import de.roessl.cnf.CNF3;
import de.roessl.optimization.TimedMAX3SATOptimization;

public class TimedDiscreteABC extends DiscreteABC implements TimedMAX3SATOptimization {

	private TimedDiscreteABC(int colonySize, int abandonmentLimit) {
		super(colonySize, abandonmentLimit);
		additionalMetrics.put("totalAbandonments", this.getTotalAbandonments());
	}

	public TimedDiscreteABC(int colonySize, int abandonmentLimit, long maxRuntime, CNF3 cnf3) {
		this(colonySize, abandonmentLimit);
		setProblem(cnf3);
		setMaxRuntime(maxRuntime);
		init();
	}

	long overallDurationNano;

	public long getOverallDuration() {
		return overallDurationNano;
	}

	@Override
	public void executeOptimizationStep() {
		long start = System.nanoTime();
		super.executeOptimizationStep();
		overallDurationNano = overallDurationNano + (System.nanoTime() - start);
		additionalMetrics.put("totalAbandonments", this.getTotalAbandonments());
	}

	private Map<String, Object> additionalMetrics = new HashMap<>();

	public Map<String, Object> getAdditionalMetrics() {
		return additionalMetrics;
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
	public long getMaxRuntime() {
		return maxRuntime;
	}

}
