package de.roessl.optimization;

import java.util.Map;

public interface TimedMAX3SATOptimization extends MAX3SATOptimization {
	
	public long getOverallDuration();
	
	public void setMaxRuntime(long maxRuntime);

	public Map<String, Object> getAdditionalMetrics();

	public long getMaxRuntime();

}
