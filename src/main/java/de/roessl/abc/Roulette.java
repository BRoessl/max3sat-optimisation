package de.roessl.abc;

import java.util.Random;

public class Roulette {
	
	Random r = new Random();

	private double[] roulette;

	public void update(double[] fitnessValues){
		roulette = new double[fitnessValues.length];
		double totalFitness = 0.0;
		for (double fitness : fitnessValues) {
			totalFitness += fitness;
		}
		double upperbound = 0.0;
		for (int i = 0; i < fitnessValues.length; i++) {
			upperbound += fitnessValues[i]/totalFitness;
			roulette[i] = upperbound;
		}
		checkIfValid(upperbound);
	}

	private void checkIfValid(double upperbound) {
		//check if the cumulative probability is close to 1.0 as it should be
		double acceptableError = 0.0001d;
		if (Math.abs(1.0d - upperbound) > acceptableError) {
			throw new IllegalStateException("calculated cumulative probability is not 1.0d but " + upperbound);
		}
	}
	
	public int select() {
		double rdouble = r.nextDouble();
		for (int i = 0; i < roulette.length; i++) {
			if (rdouble < roulette[i])
					return i;
		}
		return -1;
	}

}
