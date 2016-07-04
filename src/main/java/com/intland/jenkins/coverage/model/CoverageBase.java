package com.intland.jenkins.coverage.model;

public class CoverageBase {

	private String markup;
	private String name;

	private Integer lineMissed;
	private Integer lineCovered;

	private Integer instructionMissed;
	private Integer instructionCovered;

	private Integer complexityMissed;
	private Integer complexityCovered;

	private Integer methodMissed;
	private Integer methodCovered;

	private Integer branchMissed;
	private Integer branchCovered;

	private Integer classMissed;
	private Integer classCovered;

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

	public Integer getLineMissed() {
		return this.lineMissed;
	}

	public void setLineMissed(Integer lineMissed) {
		this.lineMissed = lineMissed;
	}

	public Integer getLineCovered() {
		return this.lineCovered;
	}

	public void setLineCovered(Integer lineCovered) {
		this.lineCovered = lineCovered;
	}

	public Integer getInstructionMissed() {
		return this.instructionMissed;
	}

	public void setInstructionMissed(Integer instructionMissed) {
		this.instructionMissed = instructionMissed;
	}

	public Integer getInstructionCovered() {
		return this.instructionCovered;
	}

	public void setInstructionCovered(Integer instructionCovered) {
		this.instructionCovered = instructionCovered;
	}

	public Integer getComplexityMissed() {
		return this.complexityMissed;
	}

	public void setComplexityMissed(Integer complexityMissed) {
		this.complexityMissed = complexityMissed;
	}

	public Integer getComplexityCovered() {
		return this.complexityCovered;
	}

	public void setComplexityCovered(Integer complexityCovered) {
		this.complexityCovered = complexityCovered;
	}

	public Integer getMethodMissed() {
		return this.methodMissed;
	}

	public void setMethodMissed(Integer methodMissed) {
		this.methodMissed = methodMissed;
	}

	public Integer getMethodCovered() {
		return this.methodCovered;
	}

	public void setMethodCovered(Integer methodCovered) {
		this.methodCovered = methodCovered;
	}

	public Integer getBranchMissed() {
		return this.branchMissed;
	}

	public void setBranchMissed(Integer branchMissed) {
		this.branchMissed = branchMissed;
	}

	public Integer getBranchCovered() {
		return this.branchCovered;
	}

	public void setBranchCovered(Integer branchCovered) {
		this.branchCovered = branchCovered;
	}

	public Integer getClassMissed() {
		return this.classMissed;
	}

	public void setClassMissed(Integer classMissed) {
		this.classMissed = classMissed;
	}

	public Integer getClassCovered() {
		return this.classCovered;
	}

	public void setClassCovered(Integer classCovered) {
		this.classCovered = classCovered;
	}

}
