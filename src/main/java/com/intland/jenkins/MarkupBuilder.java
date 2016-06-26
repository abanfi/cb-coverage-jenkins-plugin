package com.intland.jenkins;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.intland.jenkins.jacoco.model.Counter;
import com.intland.jenkins.jacoco.model.Method;
import com.intland.jenkins.jacoco.model.Package;
import com.intland.jenkins.jacoco.model.Report;

public class MarkupBuilder {
	private static String header = "|| Element || Instructions || Branches || Complexity || Lines || Methods || Classes ";

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
			builder.append("\n|");
			builder.append(method.getName());

			appendColumns(builder, method.getCounter());
		}

		appendTotal(builder, base.getCounter());

		return builder.toString();
	}

	public static String genearteSummary(Package pack) {

		StringBuilder builder = new StringBuilder();
		builder.append(header);

		for (com.intland.jenkins.jacoco.model.Class clazz : pack.getClazz()) {
			builder.append("\n|");
			builder.append(clazz.getName());

			appendColumns(builder, clazz.getCounter());
		}

		appendTotal(builder, pack.getCounter());

		return builder.toString();
	}

	public static String genearteSummary(Report report) {

		StringBuilder builder = new StringBuilder();
		builder.append(header);

		for (Package pack : report.getPackage()) {
			builder.append("\n|");
			builder.append(StringUtils.isBlank(pack.getName()) ? "default" : pack.getName());

			appendColumns(builder, pack.getCounter());
		}

		appendTotal(builder, report.getCounter());

		return builder.toString();
	}

	private static void appendTotal(StringBuilder builder, List<Counter> counters) {
		builder.append("\n||TOTAL:");

		for (String column : COLUMNS) {
			builder.append("||");
			builder.append(calculate(column, counters));
		}
	}

	private static void appendColumns(StringBuilder builder, List<Counter> counter) {
		for (String column : COLUMNS) {
			builder.append("|");
			builder.append(calculate(column, counter));
		}
	}

	private static String calculate(String type, List<Counter> counters) {

		for (Counter counter : counters) {
			if (counter.getType().equals(type)) {
				Integer all = counter.getMissed() + counter.getCovered();
				Double percent = (counter.getCovered() / (double) all) * 100d;
				return String.valueOf(percent.intValue()) + "%";
			}
		}

		return "N/A";
	}

}
