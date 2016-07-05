package com.intland.jenkins;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.intland.jenkins.jacoco.model.Counter;
import com.intland.jenkins.jacoco.model.Method;
import com.intland.jenkins.jacoco.model.Package;
import com.intland.jenkins.jacoco.model.Report;

public class HTMLMarkupBuilder {

	private static String header = "<table class=\"trackerItems relationsExpander displaytag treetable trackerItemTreeTable\" style=\"width: 300px;\">"
			+ "<thead><tr><th style=\"min-width: 200px;\" class=\"textData\">Element</th><th class=\"textData\">Instructions</th>"
			+ "<th class=\"textData\">Branches</th><th class=\"textData\">Complexity</th>"
			+ "<th class=\"textData\">Lines</th><th class=\"textData\">Methods</th>"
			+ "<th class=\"textData\">Classes</th></tr></thead><tbody>";

	private static String INSTRUCTION = "INSTRUCTION";
	private static String BRANCH = "BRANCH";
	private static String COMPLEXITY = "COMPLEXITY";
	private static String LINE = "LINE";
	private static String METHOD = "METHOD";
	private static String CLASS = "CLASS";

	private static String[] COLUMNS = new String[] { INSTRUCTION, BRANCH, COMPLEXITY, LINE, METHOD, CLASS };

	public static String genearteSummary(com.intland.jenkins.jacoco.model.Class base) {

		StringBuilder builder = new StringBuilder();

		// total part
		builder.append("<h2><b>Overall coverage Summary</b></h2>");
		builder.append(header);
		builder.append("<tr>");
		appendTotal(builder, "all methods", base.getCounter());
		builder.append("</tr></tbody></table>");

		// method part
		builder.append("<br><h2><b>Coverage Breakdown by Method</b></h2>");
		builder.append(header);
		for (Method method : base.getMethod()) {
			builder.append("<tr>");
			builder.append("<td>");
			builder.append(method.getName());
			builder.append("</td>");

			appendColumns(builder, method.getCounter());
			builder.append("</tr>");
		}
		builder.append("</tbody></table>");

		return builder.toString();
	}

	public static String genearteSummary(Package pack) {

		StringBuilder builder = new StringBuilder();

		// total part
		builder.append("<h2><b>Overall coverage Summary</b></h2>");
		builder.append(header);
		builder.append("<tr>");
		appendTotal(builder, "all classes", pack.getCounter());
		builder.append("</tr></tbody></table>");

		// classes part
		builder.append("<br><h2><b>Coverage Breakdown by Class</b></h2>");
		builder.append(header);
		for (com.intland.jenkins.jacoco.model.Class clazz : pack.getClazz()) {

			builder.append("<tr>");
			builder.append("<td>");
			builder.append(StringUtils.substringAfterLast(clazz.getName(), "/"));
			builder.append("</td>");

			appendColumns(builder, clazz.getCounter());
			builder.append("</tr>");
		}
		builder.append("</tbody></table>");

		return builder.toString();
	}

	public static String genearteSummary(Report report) {

		StringBuilder builder = new StringBuilder();

		builder.append("<h2><b>Overall Coverage Summary</b></h2>");
		builder.append(header);
		builder.append("<tr>");
		appendTotal(builder, "all packages", report.getCounter());
		builder.append("</tr></tbody></table>");

		builder.append("<br><h2><b>Coverage Breakdown by Package</b></h2>");
		builder.append(header);
		for (Package pack : report.getPackage()) {

			builder.append("<tr>");
			builder.append("<td>");
			builder.append(
					StringUtils.isBlank(pack.getName()) ? "default" : StringUtils.replace(pack.getName(), "/", "."));
			builder.append("</td>");

			appendColumns(builder, pack.getCounter());
			builder.append("</tr>");
		}
		builder.append("</tbody></table>");

		return builder.toString();
	}

	private static void appendTotal(StringBuilder builder, String label, List<Counter> counters) {
		builder.append("<td>" + label + ":</td>");

		for (String column : COLUMNS) {
			builder.append("<td>");
			builder.append(calculate(column, counters));
			builder.append("</td>");
		}
	}

	private static void appendColumns(StringBuilder builder, List<Counter> counter) {
		for (String column : COLUMNS) {
			builder.append("<td>");
			builder.append(calculate(column, counter));
			builder.append("</td>");
		}

	}

	private static String calculate(String type, List<Counter> counters) {

		for (Counter counter : counters) {
			if (counter.getType().equals(type)) {
				Integer all = counter.getMissed() + counter.getCovered();
				Double percent = (counter.getCovered() / (double) all) * 100d;
				return generateDiagramMarkup(counter.getMissed(), counter.getCovered(), percent.intValue());
			}
		}

		return "<div style=\"text-align:center; line-height: 20px;\">N/A</div>";
	}

	private static String generateDiagramMarkup(Integer missed, Integer covered, Integer coveredPercent) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"miniprogressbar\" style=\"width: 120px; height: 20px;\"> ");
		builder.append("<div style=\"width:");
		builder.append(coveredPercent);
		builder.append("%; background-color:#00A85D;\"></div>");
		builder.append("<div style=\"width:");
		builder.append(100 - coveredPercent);
		builder.append("%; background-color:#CC3F44;\">");
		builder.append(
				"</div><div style=\"position: absolute; font-weight: bold; width: 100%; background: transparent; text-align: center; color: white; line-height: 20px;\">");
		builder.append(coveredPercent);
		builder.append("%</div></div>");
		return builder.toString();
	}
}
