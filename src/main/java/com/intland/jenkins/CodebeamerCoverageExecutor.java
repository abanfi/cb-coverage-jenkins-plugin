package com.intland.jenkins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.intland.jenkins.api.CodebeamerApiClient;
import com.intland.jenkins.api.dto.ReferenceDto;
import com.intland.jenkins.api.dto.TestCaseDto;
import com.intland.jenkins.api.dto.TestRunDto;
import com.intland.jenkins.api.dto.TrackerItemDto;
import com.intland.jenkins.coverage.ICoverageCoverter;
import com.intland.jenkins.coverage.model.CoverageBase;
import com.intland.jenkins.coverage.model.CoverageReport;
import com.intland.jenkins.coverage.model.DirectoryCoverage;
import com.intland.jenkins.dto.PluginConfiguration;
import com.intland.jenkins.jacoco.JacocoResultParser;

public class CodebeamerCoverageExecutor {

	private static final String SUCCESS_STATUS = "Passed";
	private static final String FAILED_STATUS = "Failed";
	private static final String DEFAULT_TESTSET_NAME = "Jenkins-Coverage";

	/**
	 * Executes the codebeamer coverage registration process
	 *
	 * @param context
	 *            the context of the execution {@link ExecutionContext}
	 * @throws IOException
	 */
	public static void execute(ExecutionContext context) throws IOException {

		context.log("Start to process coverage report.");

		// get and check the report
		CoverageReport report = loadReport(context);
		if (report == null) {
			context.log("Report cannot be parsed or cannot be found! Execution stopping.");
			return;
		}

		context.log("Load existing test cases.");
		CodebeamerApiClient client = context.getClient();
		PluginConfiguration configuration = context.getConfiguration();
		List<TrackerItemDto> testCases = client.getTrackerItemList(configuration.getTestCaseTrackerId());
		context.logFormat("%d test cases found in tracker %d", testCases.size(), configuration.getTestCaseTrackerId());

		context.log("Collect test case ids.");
		Map<String, Integer> testCasesForCurrentResults = collectTestCaseIds(report, testCases, context);
		context.logFormat("%d Test Case items are collected.", testCasesForCurrentResults.size());

		context.log("Create or get default test set for coverage.");
		TrackerItemDto coverageTestSet = getOrCreateTestSet(context);
		context.logFormat("Coverage test set found: <%d>.", coverageTestSet.getId());

		context.log("Create new Test Run.");
		TrackerItemDto coverageTestRun = createTestRun(report, testCasesForCurrentResults, coverageTestSet, context);
		context.logFormat(" Test Run created: <%s>.", coverageTestRun.getId());

		context.log("Upload coverage result to the test run.");
		uploadResults(report, testCasesForCurrentResults, coverageTestSet, coverageTestRun, context);
		context.log("Uploading coverage result is completed!");
	}

	private static void uploadResults(CoverageReport report, Map<String, Integer> testCasesForCurrentResults,
			TrackerItemDto coverageTestSet, TrackerItemDto coverageTestRun, ExecutionContext context)
			throws IOException {

		Integer testConfigurationId = context.getConfiguration().getTestConfigurationId();

		// iterate over the coverage report result
		for (DirectoryCoverage directory : report.getDirectories()) {

			// create test for directory (package in java)
			Integer testCaseId = testCasesForCurrentResults.get(directory.getName());
			createTestCaseRun(testConfigurationId, coverageTestSet, coverageTestRun, directory, testCaseId, context);

			// create test for file items (classes in java)
			for (CoverageBase fileCoverage : directory.getFiles()) {
				testCaseId = testCasesForCurrentResults.get(fileCoverage.getName());
				createTestCaseRun(testConfigurationId, coverageTestSet, coverageTestRun, fileCoverage, testCaseId,
						context);
			}
		}

		updateTestSetTestCases(coverageTestSet, testCasesForCurrentResults.values(), context);
		updateTrackerItemStatus(coverageTestSet, "Completed", context);
	}

	private static void updateTrackerItemStatus(TrackerItemDto coverageTestSet, String status, ExecutionContext context)
			throws IOException {
		TestCaseDto testCaseDto = new TestCaseDto(coverageTestSet.getId(), status);
		context.getClient().put(testCaseDto);
	}

	private static void updateTestSetTestCases(TrackerItemDto coverageTestSet, Collection<Integer> testCases,
			ExecutionContext context) throws IOException {
		List<Object[]> testCasesList = new ArrayList<>();
		for (Integer testCaseId : testCases) {
			testCasesList.add(new Object[] { new ReferenceDto("/item/" + testCaseId), Boolean.TRUE, Boolean.TRUE });
		}

		TrackerItemDto trackerItemDto = new TrackerItemDto();
		trackerItemDto.setUri(coverageTestSet.getUri());
		trackerItemDto.setTestCases(testCasesList);

		context.getClient().put(trackerItemDto);
	}

	private static void createTestCaseRun(Integer testConfigurationId, TrackerItemDto coverageTestSet,
			TrackerItemDto coverageTestRun, CoverageBase coverageBase, Integer testCaseId, ExecutionContext context)
			throws IOException {

		Integer testRunTrackerId = context.getConfiguration().getTestRunTrackerId();
		// TODO calculate status
		TestRunDto testRunDto = new TestRunDto(coverageBase.getName(), coverageTestRun.getId(), testRunTrackerId,
				Arrays.asList(new Integer[] { testCaseId }), testConfigurationId, SUCCESS_STATUS);
		testRunDto.setDescription(coverageBase.getMarkup());
		testRunDto.setDescFormat("W");
		testRunDto.setTestSet(coverageTestSet.getId());

		// create test run item
		TrackerItemDto testRunItem = context.getClient().postTrackerItem(testRunDto);
		TestCaseDto testCaseDto = new TestCaseDto(testRunItem.getId(), "Completed");
		testCaseDto.setSpentMillis(0l);
		context.getClient().put(testCaseDto);
	}

	/**
	 * Creates a new test run which assigned to the current build
	 *
	 * @param report
	 *            the coverage report that contains the report markup
	 * @param testCasesForCurrentResults
	 *            a name - id key value map that hold all of the test case in
	 *            the configured test case tracker
	 * @param testSetDto
	 * @param context
	 * @return
	 * @throws IOException
	 */
	private static TrackerItemDto createTestRun(CoverageReport report, Map<String, Integer> testCasesForCurrentResults,
			TrackerItemDto testSetDto, ExecutionContext context) throws IOException {

		Integer testRunTrackerId = context.getConfiguration().getTestRunTrackerId();
		Integer testConfigurationId = context.getConfiguration().getTestConfigurationId();

		// TODO compute status
		TestRunDto testRunDto = new TestRunDto(context.getBuildIdentifier(), null, testRunTrackerId,
				testCasesForCurrentResults.values(), testConfigurationId, SUCCESS_STATUS);

		testRunDto.setTestSet(testSetDto.getId());
		testRunDto.setDescription(report.getMarkup());
		testRunDto.setDescFormat("W");
		return context.getClient().postTrackerItem(testRunDto);
	}

	/**
	 * Creates or returns a test set assigned to the current build
	 *
	 * @param context
	 *            the context of the execution {@link ExecutionContext}
	 * @return the test set for the current build
	 * @throws IOException
	 */
	private static TrackerItemDto getOrCreateTestSet(ExecutionContext context) throws IOException {

		String name = DEFAULT_TESTSET_NAME + "-" + context.getBuildIdentifier();
		Integer testSetTrackerId = context.getConfiguration().getTestSetTrackerId();

		// TODO Auto-generated method stub
		return context.getClient().findOrCreateTrackerItem(testSetTrackerId, name, "--");
	}

	/**
	 * Collect test case id's to the report's results
	 *
	 * @param report
	 *            the coverage result model {@link CoverageReport}
	 * @param testCases
	 *            list of the exsisting test cases in the configured test case
	 *            tracker
	 * @param context
	 *            the context of the execution {@link ExecutionContext}
	 * @return a name - test case id map
	 * @throws IOException
	 */
	private static Map<String, Integer> collectTestCaseIds(CoverageReport report, List<TrackerItemDto> testCases,
			ExecutionContext context) throws IOException {

		// create a test case map by name
		ImmutableMap<String, TrackerItemDto> testCasesMapByName = Maps.uniqueIndex(testCases,
				new Function<TrackerItemDto, String>() {

					@Override
					public String apply(TrackerItemDto itemDto) {
						return itemDto.getName();
					}
				});

		// create a test case map by id
		ImmutableMap<Integer, TrackerItemDto> testCasesMapById = Maps.uniqueIndex(testCases,
				new Function<TrackerItemDto, Integer>() {

					@Override
					public Integer apply(TrackerItemDto itemDto) {
						return itemDto.getId();
					}
				});

		// resolve test case root node if it is exists
		TrackerItemDto parent = null;
		Integer parentId = context.getConfiguration().getTestCaseParentId();
		if ((parentId != null) && testCasesMapById.containsKey(parentId)) {
			parent = testCasesMapById.get(parentId);
			context.logFormat("Parent Test Case can be resolved by id: <%d>", parentId);
		}

		// log the parent is not found
		if (parent == null) {
			context.log("No parent node is found. The test caase root will be the tracker root node.");
		}

		Map<String, Integer> testCaseMap = new HashMap<>();

		// resolve ids or create new test cases
		for (DirectoryCoverage directory : report.getDirectories()) {

			// get test case for directory (package in java)
			String name = directory.getName();
			TrackerItemDto testCaseDto = searchForOrCreateTestCase(name, parent, context, testCasesMapByName);
			testCaseMap.put(name, testCaseDto.getId());

			// get test case for file items (classes in java)
			for (CoverageBase fileCoverage : directory.getFiles()) {
				String fileName = fileCoverage.getName();
				TrackerItemDto fileTestCaseDto = searchForOrCreateTestCase(fileName, testCaseDto, context,
						testCasesMapByName);

				testCaseMap.put(fileName, fileTestCaseDto.getId());
			}
		}

		return testCaseMap;
	}

	/**
	 * Search in the specified test case map for a test case with the specified
	 * name and the specified parent. Returns the tracker item if it can be
	 * found in the map otherwise create a new one and returns with that
	 *
	 * @param name
	 *            the test case name
	 * @param parent
	 *            the test case parent item or null, if it is a root node
	 * @param context
	 *            the context of the execution {@link ExecutionContext}
	 * @param testCasesMapByName
	 *            existing test cases from the test case which set by the
	 *            configuration mapped by name
	 * @return the tracker item dto for the name
	 * @throws IOException
	 */
	private static TrackerItemDto searchForOrCreateTestCase(String name, TrackerItemDto parent,
			ExecutionContext context, Map<String, TrackerItemDto> testCasesMapByName) throws IOException {

		if (testCasesMapByName.containsKey(name)) {

			// get test case item from the map
			TrackerItemDto trackerItemDto = testCasesMapByName.get(name);
			context.logFormat("Test case with name is already exist: <%s>", trackerItemDto.getId());

			// check parent
			TrackerItemDto trackerDtoParent = trackerItemDto.getParent();
			if (((trackerDtoParent == null) && (parent == null))
					|| ((trackerDtoParent != null) && trackerDtoParent.equals(parent))) {
				context.logFormat("Test case with the correct parent is exists: <%s>", trackerItemDto.getId());
				return trackerItemDto;
			}
		}

		return createNewTestCase(name, parent, context);
	}

	/**
	 * Creates and returns a new test case in the test case tracker which set in
	 * the configuration
	 *
	 * @param name
	 *            the name of the new test case
	 * @param parentTestCase
	 *            the parent URI (eg. /item/{id}) of the parent test case or
	 *            null if the test case should create in the root
	 * @param context
	 *            the context of the execution {@link ExecutionContext}
	 * @return the created test case as a tracker item
	 * @throws IOException
	 */
	private static TrackerItemDto createNewTestCase(String name, TrackerItemDto parentTestCase,
			ExecutionContext context) throws IOException {

		Integer testCaseTrackerId = context.getConfiguration().getTestCaseTrackerId();
		// TODO create normal test case DTO
		TestRunDto testCaseDto = new TestRunDto(name, "/tracker/" + testCaseTrackerId,
				parentTestCase == null ? null : parentTestCase.getId());
		testCaseDto.setDescription("--");
		testCaseDto.setType("Automated");

		// create the tracker item and log the result
		TrackerItemDto newTackerItem = context.getClient().postTrackerItem(testCaseDto);
		context.logFormat("New test case <%d> created in tracker <%s> with parent <%s>", newTackerItem.getId(),
				testCaseTrackerId, parentTestCase);

		return newTackerItem;
	}

	/**
	 * Loads report by the given context
	 *
	 * @param context
	 *            the context of the execution {@link ExecutionContext}
	 * @return the common coverage report result {@link CoverageReport}
	 */
	private static CoverageReport loadReport(ExecutionContext context) {

		// get report XML file
		File rootDirectory = context.getRootDirectory();
		String reportPath = context.getConfiguration().getReportPath();
		File reportFile = new File(rootDirectory, reportPath);

		// validate report file - it should be exists
		if (!reportFile.exists()) {
			context.log(String.format("Report file cannot be found at path <%s>", reportFile.getAbsolutePath()));
			return null;
		}

		context.log(String.format("Report file found at <%s> with length <%d>", reportPath, reportFile.length()));

		// TODO more converted can be specified
		ICoverageCoverter converter = new JacocoResultParser();

		return converter.collectCoverageReport(reportFile.getAbsolutePath());
	}

}
