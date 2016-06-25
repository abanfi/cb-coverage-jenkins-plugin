package com.intland.jenkins.coverage.model;

public class CoverageBase {

	private String name;
	private Double lineCoverage;

	public Double getLineCoverage() {
		return lineCoverage;
	}

	public void setLineCoverage(Double lineCoverage) {
		this.lineCoverage = lineCoverage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
