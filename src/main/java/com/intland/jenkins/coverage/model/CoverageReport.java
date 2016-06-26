package com.intland.jenkins.coverage.model;

import java.util.ArrayList;
import java.util.List;

public class CoverageReport extends CoverageBase {

	private List<DirectoryCoverage> directories = new ArrayList<>();

	public List<DirectoryCoverage> getDirectories() {
		return this.directories;
	}

	public void setDirectories(List<DirectoryCoverage> directories) {
		this.directories = directories;
	}

	public String getStatus() {
		// TODO
		return "SUCCESS";
	}

}