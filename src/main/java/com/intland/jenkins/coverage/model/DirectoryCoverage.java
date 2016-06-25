package com.intland.jenkins.coverage.model;

import java.util.ArrayList;
import java.util.List;

public class DirectoryCoverage extends CoverageBase {

	private List<CoverageBase> files = new ArrayList<>();

	public List<CoverageBase> getFiles() {
		return this.files;
	}

	public void setFiles(List<CoverageBase> files) {
		this.files = files;
	}

}
