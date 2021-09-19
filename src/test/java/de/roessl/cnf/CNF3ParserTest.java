package de.roessl.cnf;

import java.io.InputStream;
import org.junit.Test;

import de.roessl.testset.TestSet;
import de.roessl.testset.TestSet.SubSet;

public class CNF3ParserTest {

	@Test
	public void testReadDimacFormat() throws Exception {

		for (SubSet subSet : TestSet.SubSet.values()) {
			InputStream cnfFiles = TestSet.getCNFResource(subSet, 99);
			CNF3 readDimacFormat = CNF3Parser.readDimacFormat(cnfFiles);
			System.out.println(readDimacFormat);
		}
	}
}
