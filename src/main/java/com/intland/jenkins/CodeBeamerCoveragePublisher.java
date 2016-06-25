package com.intland.jenkins;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;

@SuppressWarnings("unchecked")
public class CodeBeamerCoveragePublisher extends Notifier implements SimpleBuildStep {

	@Extension
	public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new CodebeamerCoverageDescriptor();

	private String uri;
	private String reportPath;
	private String username;
	private String password;
	private Integer testSetTrackerId;
	private Integer testCaseTrackerId;
	private Integer testCaseParentId;
	private Integer testRunTrackerId;
	private Integer testConfigurationId;
	private String includedPackages;
	private String excludedPackages;

	@DataBoundConstructor
	public CodeBeamerCoveragePublisher() {
		this.uri = StringUtils.EMPTY;
		this.username = StringUtils.EMPTY;
		this.password = StringUtils.EMPTY;
		this.testSetTrackerId = null;
		this.testCaseTrackerId = null;
		this.testCaseParentId = null;
		this.testRunTrackerId = null;
		this.testConfigurationId = null;
		this.includedPackages = StringUtils.EMPTY;
		this.excludedPackages = StringUtils.EMPTY;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException {

		CodebeamerCoverageExecutor.execute(this, listener);
	}

	public String getUri() {
		return this.uri;
	}

	@DataBoundSetter
	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUsername() {
		return this.username;
	}

	@DataBoundSetter
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	@DataBoundSetter
	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getTestSetTrackerId() {
		return this.testSetTrackerId;
	}

	@DataBoundSetter
	public void setTestSetTrackerId(Integer testSetTrackerId) {
		this.testSetTrackerId = testSetTrackerId;
	}

	public Integer getTestCaseTrackerId() {
		return this.testCaseTrackerId;
	}

	@DataBoundSetter
	public void setTestCaseTrackerId(Integer testCaseTrackerId) {
		this.testCaseTrackerId = testCaseTrackerId;
	}

	public Integer getTestCaseParentId() {
		return this.testCaseParentId;
	}

	@DataBoundSetter
	public void setTestCaseParentId(Integer testCaseParentId) {
		this.testCaseParentId = testCaseParentId;
	}

	public Integer getTestRunTrackerId() {
		return this.testRunTrackerId;
	}

	@DataBoundSetter
	public void setTestRunTrackerId(Integer testRunTrackerId) {
		this.testRunTrackerId = testRunTrackerId;
	}

	public Integer getTestConfigurationId() {
		return this.testConfigurationId;
	}

	@DataBoundSetter
	public void setTestConfigurationId(Integer testConfigurationId) {
		this.testConfigurationId = testConfigurationId;
	}

	public String getIncludedPackages() {
		return this.includedPackages;
	}

	@DataBoundSetter
	public void setIncludedPackages(String includedPackages) {
		this.includedPackages = includedPackages;
	}

	public String getExcludedPackages() {
		return this.excludedPackages;
	}

	@DataBoundSetter
	public void setExcludedPackages(String excludedPackages) {
		this.excludedPackages = excludedPackages;
	}

	public String getReportPath() {
		return this.reportPath;
	}

	@DataBoundSetter
	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

}
