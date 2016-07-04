package com.intland.jenkins;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.intland.jenkins.jacoco.model.Counter;
import com.intland.jenkins.jacoco.model.Method;
import com.intland.jenkins.jacoco.model.Package;
import com.intland.jenkins.jacoco.model.Report;

public class HTMLMarkupBuilder {

	private static String header = "<table border=\"1\" class=\"wikitable\"><tbody><tr><th>Element</th><th>Instructions</th>"
			+ "<th>Branches</th><th>Complexity</th><th>Lines</th><th>Methods</th><th>Classes</th></tr>";

	private static String INSTRUCTION = "INSTRUCTION";
	private static String BRANCH = "BRANCH";
	private static String COMPLEXITY = "COMPLEXITY";
	private static String LINE = "LINE";
	private static String METHOD = "METHOD";
	private static String CLASS = "CLASS";

	private static String[] COLUMNS = new String[] { INSTRUCTION, BRANCH, COMPLEXITY, LINE, METHOD, CLASS };

	public static String genearteSummary(com.intland.jenkins.jacoco.model.Class base) {

		StringBuilder builder = new StringBuilder();
		builder.append(header);

		for (Method method : base.getMethod()) {
			builder.append("<tr>");
			builder.append("<td>");
			builder.append(method.getName());
			builder.append("</td>");

			appendColumns(builder, method.getCounter());
			builder.append("</tr>");
		}

		builder.append("<tr>");
		appendTotal(builder, base.getCounter());
		builder.append("</tr></tbody></table>");

		return builder.toString();
	}

	public static String genearteSummary(Package pack) {

		StringBuilder builder = new StringBuilder();
		builder.append(header);

		for (com.intland.jenkins.jacoco.model.Class clazz : pack.getClazz()) {

			builder.append("<tr>");
			builder.append("<td>");
			builder.append(StringUtils.substringAfterLast(clazz.getName(), "/"));
			builder.append("</td>");

			appendColumns(builder, clazz.getCounter());
			builder.append("</tr>");
		}

		builder.append("<tr>");
		appendTotal(builder, pack.getCounter());
		builder.append("</tr></tbody></table>");

		return builder.toString();
	}

	public static String genearteSummary(Report report) {

		StringBuilder builder = new StringBuilder();
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
		builder.append("<tr>");
		appendTotal(builder, report.getCounter());
		builder.append("</tr></tbody></table>");

		return builder.toString();
	}

	private static void appendTotal(StringBuilder builder, List<Counter> counters) {
		builder.append("<td>TOTAL:</td>");

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

		return "<div style=\"text-align:center;\">N/A</div>";
	}

	private static String generateDiagramMarkup(Integer missed, Integer covered, Integer coveredPercent) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"miniprogressbar\" style=\"width: 120px;\"> ");
		builder.append("<div style=\"width:");
		builder.append(coveredPercent);
		builder.append("%;\" class=\"testingProgressBarPassed\"></div>");
		builder.append("<div style=\"width:");
		builder.append(100 - coveredPercent);
		builder.append("%;\" class=\"testingProgressBarFailed\">");
		builder.append(
				"</div><div style=\"position: absolute; width: 100%; background: transparent; text-align: center;\">");
		builder.append(coveredPercent);
		builder.append("%</div></div>");
		return builder.toString();
	}
}
