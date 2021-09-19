package de.roessl.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.roessl.abc.TimedDiscreteABC;
import de.roessl.annealing.TimedSimulatedAnnealing;
import de.roessl.cnf.CNF3;
import de.roessl.optimization.TimedMAX3SATOptimization;
import de.roessl.testset.TestSet;
import de.roessl.testset.TestSet.SubSet;

public class Main {

	static NumberFormat FORMAT = DecimalFormat.getInstance(Locale.GERMAN);

	private static SubSet problemSet;
	private static long timePerInstanceNano;
	private static HashMap<String, Long[]> algorithmDescriptors;
	private static String specificName;
	private static int problemSize;

	public static void main(String[] args) throws IOException {

		print("--- TH OWL ATA EXAM PROGRAM ---");
		readUserInput();

		// build algorithms
		List<CNF3> problems = TestSet.getProblems(problemSet, problemSize);
		HashMap<String, List<TimedMAX3SATOptimization>> solverMap = new HashMap<>();
		for (Entry<String, Long[]> alg : algorithmDescriptors.entrySet()) {
			if (alg.getKey().startsWith("sa")) {
				List<TimedMAX3SATOptimization> list = new ArrayList<>();
				solverMap.put(alg.getKey(), list);
				for (CNF3 cnf3 : problems) {
					list.add(new TimedSimulatedAnnealing(alg.getValue()[0], timePerInstanceNano, cnf3));
				}
			}
			if (alg.getKey().startsWith("abc")) {
				List<TimedMAX3SATOptimization> list = new ArrayList<>();
				solverMap.put(alg.getKey(), list);
				for (CNF3 cnf3 : problems) {
					list.add(new TimedDiscreteABC(alg.getValue()[0].intValue(), alg.getValue()[1].intValue(),
							timePerInstanceNano, cnf3));
				}
			}
		}

		// prepare statistics
		HashMap<String, StatsCollector> statsMap = new HashMap<>();
		for (Entry<String, List<TimedMAX3SATOptimization>> solvers : solverMap.entrySet()) {
			statsMap.put(solvers.getKey(), new StatsCollector(solvers.getValue()));
		}

		// run optimizations and collect statistics
		XYSeriesCollection collection = new XYSeriesCollection();
		for (Entry<String, StatsCollector> stats : statsMap.entrySet()) {
			print("Starting %s ...", stats.getKey());
			stats.getValue().run();
			XYSeries xySeries = new XYSeries(stats.getKey() + "_mean");
			List<Double> fitnessMeans = stats.getValue().fitnessMeans;
			for (int i = 0; i < fitnessMeans.size(); i++) {
				Double fitnesses = fitnessMeans.get(i);
				xySeries.add(i + 1, fitnesses);
			}
			collection.addSeries(xySeries);
		}

		// plot result
		String pathname = "./" + specificName + ".png";
		print("Plotting Results in %s ...", pathname);
		JFreeChart createXYLineChart = createChart(collection, specificName);
		ChartUtils.saveChartAsPNG(new File(pathname), createXYLineChart, 600, 400, null);

		// write metadata
		String metadata = "./" + specificName + ".txt";
		try (BufferedWriter newBufferedWriter = Files.newBufferedWriter(Paths.get(metadata), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			newBufferedWriter.write("#" + specificName);
			newBufferedWriter.newLine();
			newBufferedWriter.newLine();

			for (Entry<String, StatsCollector> stats : statsMap.entrySet()) {
				newBufferedWriter.write("#" + stats.getKey());
				newBufferedWriter.newLine();
				int lastValueIndex = stats.getValue().fitnessMax.size() - 1;
				Double mean = stats.getValue().fitnessMeans.get(lastValueIndex);
				Double max = stats.getValue().fitnessMax.get(lastValueIndex);
				Double min = stats.getValue().fitnessMin.get(lastValueIndex);
				Double stddev = stats.getValue().fitnessStdDev.get(lastValueIndex);
				newBufferedWriter
						.write(String.format("mean=%f\nmax=%f\nmin=%f\nstddev=%f\n\n", mean, max, min, stddev));
				newBufferedWriter.flush();
			}
		}
		print("All task completed!");
		print("Press any key to exit JVM");
		System.in.read();
	}

	private static void readUserInput() {
		print("choose problem set:");
		SubSet[] values = TestSet.SubSet.values();
		for (int i = 0; i < values.length; i++) {
			SubSet subSet = values[i];
			print("(%d) %s with %d problems", i, subSet.name(), subSet.getSize());
		}
		int index = (int) readInput();
		problemSet = values[index];
		print(problemSet.name());

		print("choose problem size, 1 to %d:", problemSet.getSize());
		problemSize = (int) readInput();

		print("choose time in milliseconds for each algorithm and problem:");
		long ms = readInput();
		timePerInstanceNano = TimeUnit.MILLISECONDS.toNanos(ms);

		specificName = problemSet.name() + "_" + problemSize + "_" + ms + "ms";

		algorithmDescriptors = new HashMap<>();
		print("add algorithms to compare:");
		while (true) {
			print("(1) Add Simulated Annealing");
			print("(2) Add Artificial Bee Colony");
			print("(3) Add Standard Set");
			print("(4) Next");
			print("Added: " + Arrays.toString(algorithmDescriptors.keySet().toArray()));
			long in = readInput();

			if (in == 1) {
				print("enter initial temperature:");
				long temp = readInput();
				algorithmDescriptors.put("sa_t" + temp, new Long[] { temp });
			}
			if (in == 2) {
				print("enter colony size:");
				long cs = readInput();
				print("enter abandonment number:");
				long a = readInput();
				algorithmDescriptors.put("abc_c" + cs + "_a" + a, new Long[] { cs, a });
			}
			if (in == 3) {
				algorithmDescriptors.put("sa_t" + 1000, new Long[] { 1000L });
				algorithmDescriptors.put("sa_t" + 1000000, new Long[] { 1000000L });
				algorithmDescriptors.put("abc_c" + 20 + "_a" + 10, new Long[] { 20L, 10L });
				algorithmDescriptors.put("abc_c" + 200 + "_a" + 10, new Long[] { 200L, 10L });
				algorithmDescriptors.put("abc_c" + 2000 + "_a" + 10, new Long[] { 2000L, 10L });
				algorithmDescriptors.put("abc_c" + 20 + "_a" + 100, new Long[] { 20L, 100L });
				algorithmDescriptors.put("abc_c" + 200 + "_a" + 100, new Long[] { 200L, 100L });
				algorithmDescriptors.put("abc_c" + 2000 + "_a" + 100, new Long[] { 2000L, 100L });
			}
			if (in == 4) {
				break;
			}
			if (in < 0) {
				String toRemove = (String) algorithmDescriptors.keySet().toArray()[-1 * (int) in];
				algorithmDescriptors.remove(toRemove);
			} else
				continue;
		}

		long algs = algorithmDescriptors.size();
		long msTotal = algs * problemSize * ms;
		long minTotal = TimeUnit.MILLISECONDS.toMinutes(msTotal);
		print("%s with %d algoritms and %d problems could take up to %d minutes", specificName,
				algorithmDescriptors.size(), problemSize, minTotal);
	}

	private static long readInput() {
		try {
			Scanner scanner = new Scanner(System.in);
			long nextLong = scanner.nextLong();
			return nextLong;
		} catch (Exception e) {
			return -1;
		} finally {
		}
	}

	private static void print(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	private static JFreeChart createChart(XYSeriesCollection collection, String charttitle) {
		int drawStyleThresholdsSeconds = 10;

		JFreeChart createXYLineChart = ChartFactory.createXYLineChart(charttitle, "_X_", "fitness", collection);
		LogarithmicAxis logarithmicAxis = new LogarithmicAxis("runtime in milliseconds (log)");
		if (TimeUnit.NANOSECONDS.toSeconds(timePerInstanceNano) > 10) {
			createXYLineChart.getXYPlot().getRangeAxis().setRange(new Range(0.95, 1.00));
			logarithmicAxis.setRange(new Range(
					TimeUnit.SECONDS.toMillis(drawStyleThresholdsSeconds),
					TimeUnit.NANOSECONDS.toMillis(timePerInstanceNano)));
		} else {
			createXYLineChart.getXYPlot().getRangeAxis().setRange(new Range(0.85, 1.00));
		}
		createXYLineChart.getXYPlot().setDomainAxis(logarithmicAxis);
		return createXYLineChart;
	}

}
