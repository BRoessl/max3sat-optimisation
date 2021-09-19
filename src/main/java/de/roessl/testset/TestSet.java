package de.roessl.testset;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import de.roessl.cnf.CNF3;
import de.roessl.cnf.CNF3Parser;

public class TestSet {

	public enum SubSet {
		uf100_430(1000), uf150_645(100), uf20_91(1000), uf200_860(100), uf50_218(1000), uuf100_430(1000),
		uuf150_645(100), uuf200_860(100), uf250_1065(100), uuf250_1065(100), uuf50_218(1000);

		private int size;

		SubSet(int size) {
			this.size = size;
		}

		public int getSize() {
			return size;
		}

		public String getFolderName() {
			return name().replace('_', '-');
		}

		public String getFileName(int number) throws IllegalAccessException {
			if (number < 1 || number > size) {
				throw new IllegalAccessException();
			}
			return name().split("_")[0] + ("-0") + number + ".cnf";
		}

	}

	public static InputStream getCNFResource(SubSet subset, int number) throws Exception {
		return TestSet.class
				.getResourceAsStream("/testset/" + subset.getFolderName() + "/" + subset.getFileName(number));
	}

	public static List<CNF3> getAllProblems(SubSet subset) {
		return getProblems(subset, subset.size);
	}

	public static List<CNF3> getProblems(SubSet subset, int limit) {
		List<CNF3> problems = new ArrayList<>();
		for (int i = 1; i <= limit; i++) {
			InputStream cnfFiles;
			try {
				cnfFiles = TestSet.getCNFResource(subset, i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			CNF3 readDimacFormat = CNF3Parser.readDimacFormat(cnfFiles);
			problems.add(readDimacFormat);
		}
		return problems;

	}

}
