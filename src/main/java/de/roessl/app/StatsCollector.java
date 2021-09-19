package de.roessl.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.math.Stats;

import de.roessl.optimization.TimedMAX3SATOptimization;

public class StatsCollector {

	private List<TimedMAX3SATOptimization> runners;
	
	public List<Double> fitnessMeans = new ArrayList<Double>();
	public List<Double> fitnessMin= new ArrayList<Double>();
	public List<Double> fitnessMax = new ArrayList<Double>();
	public List<Double> fitnessStdDev = new ArrayList<Double>();
	public double[][] fitnessesForMillisecond;

	private long samples;

	public StatsCollector(List<TimedMAX3SATOptimization> runners) {
		this.runners = runners;
		long millis = TimeUnit.NANOSECONDS.toMillis(runners.get(0).getMaxRuntime());
		//one sample per millisecond
		samples = millis;
	}
	
	public void run() {
		
		long resolution = (runners.get(0).getMaxRuntime() / samples);
		double[][] records = new double[runners.size()][(int)samples];
		
		for (int i = 0; i < runners.size(); i++) {
			TimedMAX3SATOptimization runner = runners.get(i);
			double[] fitnesses = records[i];
			long j = 0;			while (j < fitnesses.length) {
				
				long threshold = j*resolution;
				
				if (runner.fulfilsStoppingCriterion()) {
					fitnesses[(int)j] = runner.getFitness();
					j++;
					continue;
				}
				
				if(runner.getOverallDuration() >= threshold) {
					fitnesses[(int)j] = runner.getFitness();
					j++;
					continue;
				}
				runner.executeOptimizationStep();
			}
			System.out.println(String.format("Run %d of %d completed.", i+1, runners.size()));

		}
		
	    double[][] transposedMatrix = new double[(int)samples][runners.size()];
	    for(int x = 0; x < samples; x++) {
	        for(int y = 0; y < runners.size(); y++) {
	            transposedMatrix[x][y] = records[y][x];
	        }
	    }

	    for (double[] values : transposedMatrix) {
			Stats fitnessStats = Stats.of(values);
			fitnessMeans.add(fitnessStats.mean());
			fitnessMin.add(fitnessStats.min());
			fitnessMax.add(fitnessStats.max());
			fitnessStdDev.add(fitnessStats.populationStandardDeviation());
		}
	    
	    this.fitnessesForMillisecond = transposedMatrix;
		
	}

}
