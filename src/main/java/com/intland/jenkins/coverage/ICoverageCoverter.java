package com.intland.jenkins.coverage;

import com.intland.jenkins.coverage.model.CoverageReport;

public interface ICoverageCoverter {

	public CoverageReport collectCoverageReport(String reportFilePath);
	
}
