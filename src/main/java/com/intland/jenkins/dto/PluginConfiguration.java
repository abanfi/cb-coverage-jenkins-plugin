/*
 * Copyright (c) 2016 Intland Software (support@intland.com)
 */

package com.intland.jenkins.dto;

public class PluginConfiguration {

	private String uri;
	private String reportPath;
	private String username;
	private String password;
	private Integer testSetTrackerId;
	private Integer testCaseTrackerId;
	private Integer testCaseParentId;
	private Integer testRunTrackerId;
	private Integer testConfigurationId;
	private String[] includedPackages;
	private String[] excludedPackages;
	private Integer successInstructionCoverage;
	private Integer successBranchCoverage;
	private Integer successComplexityCoverage;
	private Integer successLineCoverage;
	private Integer successMethodCoverage;
	private Integer successClassCoverage;
	private String jenkinsUrlBase;

	public PluginConfiguration() {
	}

	public PluginConfiguration(String uri, String username, String password) {
		this.uri = uri;
		this.username = username;
		this.password = password;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getTestSetTrackerId() {
		return this.testSetTrackerId;
	}

	public void setTestSetTrackerId(Integer testSetTrackerId) {
		this.testSetTrackerId = testSetTrackerId;
	}

	public Integer getTestCaseTrackerId() {
		return this.testCaseTrackerId;
	}

	public void setTestCaseTrackerId(Integer testCaseTrackerId) {
		this.testCaseTrackerId = testCaseTrackerId;
	}

	public Integer getTestCaseParentId() {
		return this.testCaseParentId;
	}

	public void setTestCaseParentId(Integer testCaseParentId) {
		this.testCaseParentId = testCaseParentId;
	}

	public Integer getTestRunTrackerId() {
		return this.testRunTrackerId;
	}

	public void setTestRunTrackerId(Integer testRunTrackerId) {
		this.testRunTrackerId = testRunTrackerId;
	}

	public Integer getTestConfigurationId() {
		return this.testConfigurationId;
	}

	public void setTestConfigurationId(Integer testConfigurationId) {
		this.testConfigurationId = testConfigurationId;
	}

	public String[] getIncludedPackages() {
		return this.includedPackages;
	}

	public void setIncludedPackages(String[] includedPackages) {
		this.includedPackages = includedPackages;
	}

	public String[] getExcludedPackages() {
		return this.excludedPackages;
	}

	public void setExcludedPackages(String[] excludedPackages) {
		this.excludedPackages = excludedPackages;
	}

	public String getReportPath() {
		return this.reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public Integer getSuccessInstructionCoverage() {
		return this.successInstructionCoverage;
	}

	public void setSuccessInstructionCoverage(Integer successInstructionCoverage) {
		this.successInstructionCoverage = successInstructionCoverage;
	}

	public Integer getSuccessBranchCoverage() {
		return this.successBranchCoverage;
	}

	public void setSuccessBranchCoverage(Integer successBranchCoverage) {
		this.successBranchCoverage = successBranchCoverage;
	}

	public Integer getSuccessComplexityCoverage() {
		return this.successComplexityCoverage;
	}

	public void setSuccessComplexityCoverage(Integer successComplexityCoverage) {
		this.successComplexityCoverage = successComplexityCoverage;
	}

	public Integer getSuccessLineCoverage() {
		return this.successLineCoverage;
	}

	public void setSuccessLineCoverage(Integer successLineCoverage) {
		this.successLineCoverage = successLineCoverage;
	}

	public Integer getSuccessMethodCoverage() {
		return this.successMethodCoverage;
	}

	public void setSuccessMethodCoverage(Integer successMethodCoverage) {
		this.successMethodCoverage = successMethodCoverage;
	}

	public Integer getSuccessClassCoverage() {
		return this.successClassCoverage;
	}

	public void setSuccessClassCoverage(Integer successClassCoverage) {
		this.successClassCoverage = successClassCoverage;
	}

	public String getJenkinsUrlBase() {
		return this.jenkinsUrlBase;
	}

	public void setJenkinsUrlBase(String jenkinsUrlBase) {
		this.jenkinsUrlBase = jenkinsUrlBase;
	}
}
