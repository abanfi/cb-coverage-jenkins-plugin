package com.intland.jenkins;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.intland.jenkins.api.CodebeamerApiClient;
import com.intland.jenkins.dto.PluginConfiguration;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

public class ExecutionContext {

	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final BuildListener listener;
	private final AbstractBuild<?, ?> build;
	private PluginConfiguration configuration;
	private CodebeamerApiClient client;

	public ExecutionContext(BuildListener listener, AbstractBuild<?, ?> build) {
		this.listener = listener;
		this.build = build;
	}

	public File getRootDirectory() {
		try {
			return new File(this.build.getWorkspace().toURI());
		} catch (IOException | InterruptedException e) {
			this.log("Workspace root path cannot be resolved!");
		}
		return null;
	}

	public void logFormat(String message, Object... parameters) {
		this.log(String.format(message, parameters));
	}

	public void log(String message) {
		String log = String.format("%s %s", this.DATE_FORMAT.format(new Date()), message);
		this.listener.getLogger().println(log);
	}

	public PluginConfiguration getConfiguration() {
		return this.configuration;
	}

	public void setConfiguration(PluginConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Returns the API client - and initializes it if it is not created yet
	 *
	 * @return
	 */
	public CodebeamerApiClient getClient() {
		if (this.client == null) {
			this.client = new CodebeamerApiClient(this.configuration, this.listener);
		}
		return this.client;
	}

	/**
	 * Returns a unique identifier for the current build (job name + # + build
	 * number)
	 *
	 * @return
	 */
	public String getBuildIdentifier() {
		return this.getJobName() + " #" + this.build.getNumber();
	}

	/**
	 * Get the current job's name
	 * 
	 * @return
	 */
	public String getJobName() {
		return this.build.getProject().getName();
	}

}
