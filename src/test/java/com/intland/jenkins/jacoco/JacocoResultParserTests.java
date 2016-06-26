package com.intland.jenkins.jacoco;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.intland.jenkins.coverage.model.CoverageReport;

public class JacocoResultParserTests {

	@Test
	public void testCollectCoverageReport() {
		URL resource = this.getClass().getResource("jacoco.xml");

		JacocoResultParser parser = new JacocoResultParser();
		CoverageReport collectCoverageReport = parser.collectCoverageReport(resource.getPath());
		Double lineCoverage = collectCoverageReport.getLineCoverage();
		Assert.assertEquals(lineCoverage.doubleValue(), 18d / 30d, 0.001d);
	}
}