package com.intland.jenkins.coverage.model;

public class CoverageBase {

	private String markup;
	private String name;
	private Double lineCoverage;

	public Double getLineCoverage() {
		return this.lineCoverage;
	}

	public void setLineCoverage(Double lineCoverage) {
		this.lineCoverage = lineCoverage;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMarkup() {
		return this.markup;
	}

	public void setMarkup(String markup) {
		this.markup = markup;
	}

}
