package de.roessl.cnf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CNF3Parser {

	private static final Pattern CNF_FORMAT_PATTERN = Pattern.compile("p cnf (\\d+)\\s+(\\d+)\\s*");
	private static final Pattern CLAUSE_PATTERN = Pattern.compile("\\s*(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+) 0");

	static public CNF3 readDimacFormat(InputStream inputStream) {
		CNF3 result = null;
		int nbvar = 0;
		int nbclauses = 0;
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("c"))
					continue; // skip comments
				else {
					Matcher matcher = CNF_FORMAT_PATTERN.matcher(line);
					matcher.matches();
					nbvar = Integer.parseInt(matcher.group(1));
					nbclauses = Integer.parseInt(matcher.group(2));
					result = new CNF3(nbvar, nbclauses);
					break;
				}
			}
			for (int i = 0; i < nbclauses; i++) {
				line = bufferedReader.readLine();
				Matcher matcher = CLAUSE_PATTERN.matcher(line);
				matcher.matches();
				int l1 = Integer.parseInt(matcher.group(1));
				int l2 = Integer.parseInt(matcher.group(2));
				int l3 = Integer.parseInt(matcher.group(3));
				result.putClause(i, l1, l2, l3);
			}
			result.valid();
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Failed to read.", e);
		}
	}

	static public CNF3 readDimacFormat(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			return readDimacFormat(fis);
		} catch (Exception e) {
			throw new RuntimeException("Failed to read.", e);
		}
	}
}
