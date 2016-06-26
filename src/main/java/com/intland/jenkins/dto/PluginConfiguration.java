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
}
