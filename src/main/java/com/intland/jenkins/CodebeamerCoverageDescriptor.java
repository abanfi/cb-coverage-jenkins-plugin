package com.intland.jenkins;

import java.io.IOException;

import org.kohsuke.stapler.QueryParameter;

import com.intland.jenkins.api.CodebeamerApiClient;
import com.intland.jenkins.api.dto.TrackerDto;
import com.intland.jenkins.api.dto.TrackerItemDto;
import com.intland.jenkins.dto.PluginConfiguration;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

public class CodebeamerCoverageDescriptor extends BuildStepDescriptor<Publisher> {

	public static final String PLUGIN_SHORTNAME = "codebeamer-coverage";

	@Override
	public String getDisplayName() {
		return "Codebeamer Coverage Plugin";
	}

	@Override
	public String getHelpFile() {
		return "/plugin/" + PLUGIN_SHORTNAME + "/help/help.html";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isApplicable(Class<? extends AbstractProject> jobType) {
		return true;
	}

	public FormValidation doCheckTestSetTrackerId(@QueryParameter Integer value, @QueryParameter String uri,
			@QueryParameter String username, @QueryParameter String password) throws IOException {
		return this.validateTrackerType(value, 108, new PluginConfiguration(uri, username, password), true);
	}

	public FormValidation doCheckTestCaseTrackerId(@QueryParameter Integer value, @QueryParameter String uri,
			@QueryParameter String username, @QueryParameter String password, @QueryParameter Integer testCaseParentId)
			throws IOException {
		if (testCaseParentId == null) {
			return this.validateTrackerType(value, 102, new PluginConfiguration(uri, username, password), true);
		} else {
			return FormValidation.ok();
		}
	}

	public FormValidation doCheckTestCaseParentId(@QueryParameter Integer value, @QueryParameter String uri,
			@QueryParameter String username, @QueryParameter String password) throws IOException {
		return this.validateTrackerItemWithTracker(value, new PluginConfiguration(uri, username, password), 102, false);
	}

	public FormValidation doCheckTestRunTrackerId(@QueryParameter Integer value, @QueryParameter String uri,
			@QueryParameter String username, @QueryParameter String password) throws IOException {
		return this.validateTrackerType(value, 9, new PluginConfiguration(uri, username, password), true);
	}

	public FormValidation doCheckTestConfigurationId(@QueryParameter Integer value, @QueryParameter String uri,
			@QueryParameter String username, @QueryParameter String password) throws IOException {
		return this.validateTrackerItemWithTracker(value, new PluginConfiguration(uri, username, password), 109, true);
	}

	private FormValidation validateTrackerItemWithTracker(Integer value, PluginConfiguration pluginConfiguration,
			int validTrackerTypeId, boolean required) {
		FormValidation result = FormValidation.ok();
		if (value != null) {
			try {
				CodebeamerApiClient apiClient = new CodebeamerApiClient(pluginConfiguration, null);
				TrackerItemDto trackerItem = apiClient.getTrackerItem(value);
				if (trackerItem != null) {
					Integer trackerId = trackerItem.getTracker().getId();
					result = this.validateTrackerType(trackerId, validTrackerTypeId, pluginConfiguration, false);
				} else {
					result = FormValidation.error("Tracker Item can not be found");
				}
			} catch (IOException e) {
				result = FormValidation.error("codeBeamer could not be reached with the provided uri/credentials");
			}
		} else if (required) {
			result = FormValidation.error("This field is required");
		}
		return result;
	}

	private FormValidation validateTrackerType(Integer value, int validTrackerTypeId,
			PluginConfiguration pluginConfiguration, boolean required) {
		FormValidation result = FormValidation.ok();

		if (value != null) {
			try {
				boolean valid = this.checkTrackerType(pluginConfiguration, value, validTrackerTypeId);
				if (valid) {
					result = FormValidation.ok();
				} else {
					result = FormValidation.error("Tracker Type does not match the required Type");
				}
			} catch (IOException e) {
				result = FormValidation.error("codeBeamer could not be reached with the provided uri/credentials");
			}
		} else if (required) {
			result = FormValidation.error("This field is required");
		}

		return result;
	}

	private boolean checkTrackerType(PluginConfiguration pluginConfiguration, Integer trackerId, int validTrackerTypeId)
			throws IOException {
		CodebeamerApiClient apiClient = new CodebeamerApiClient(pluginConfiguration, null);
		TrackerDto trackerDto = apiClient.getTrackerType(trackerId);
		boolean result = false;

		if (trackerDto != null) {
			Integer typeId = trackerDto.getType().getTypeId();
			result = (typeId != null) && (typeId.intValue() == validTrackerTypeId);
		}

		return result;
	}
}