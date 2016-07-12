package com.intland.jenkins.jacoco;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.intland.jenkins.coverage.model.CoverageReport;

public class JacocoResultParserTests {

	@Test
	public void testCollectCoverageReport() throws IOException {
		URL resource = this.getClass().getResource("jacoco.xml");

		JacocoResultParser parser = new JacocoResultParser();
		CoverageReport collectCoverageReport = parser.collectCoverageReport(resource.getPath(), null);

		// Integer lineCoverage = collectCoverageReport.getLineCovered();
		// Assert.assertEquals(lineCoverage.doubleValue(), 18d / 30d, 0.001d);
	}
}
