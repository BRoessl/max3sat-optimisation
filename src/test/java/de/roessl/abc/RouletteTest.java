package de.roessl.abc;


import java.util.Arrays;

import org.junit.Test;

public class RouletteTest {

	@Test
	public void test_1() {
		//test first update
		int[] selections = new int[3];
		Roulette roulette = new Roulette();
		roulette.update(new double[] {0.1, 0.7, 0.2});
		for (int i = 0; i < 1000; i++) {
			int select = roulette.select();
			selections[select] = selections[select] + 1;
		}
		System.out.println(Arrays.toString(selections));
		//test second update
		selections = new int[3];
		roulette.update(new double[] {0.7, 0.1, 0.2});
		for (int i = 0; i < 1000; i++) {
			int select = roulette.select();
			selections[select] = selections[select] + 1;
		}
		System.out.println(Arrays.toString(selections));
	}

	@Test
	public void test_2() {
		int[] selections = new int[3];
		Roulette roulette = new Roulette();
		roulette.update(new double[] {10, 20, 30});
		for (int i = 0; i < 1000; i++) {
			int select = roulette.select();
			selections[select] = selections[select] + 1;
		}
		System.out.println(Arrays.toString(selections));
	}

}
