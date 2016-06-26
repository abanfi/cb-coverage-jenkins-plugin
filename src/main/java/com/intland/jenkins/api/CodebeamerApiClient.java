/*
 * Copyright (c) 2016 Intland Software (support@intland.com)
 */
package com.intland.jenkins.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intland.jenkins.api.dto.PagedTrackerItemsDto;
import com.intland.jenkins.api.dto.ReferenceDto;
import com.intland.jenkins.api.dto.TestCaseDto;
import com.intland.jenkins.api.dto.TestRunDto;
import com.intland.jenkins.api.dto.TrackerDto;
import com.intland.jenkins.api.dto.TrackerItemDto;
import com.intland.jenkins.api.dto.UserDto;
import com.intland.jenkins.dto.NodeMapping;
import com.intland.jenkins.dto.PluginConfiguration;
import com.intland.jenkins.dto.TestResultItem;
import com.intland.jenkins.dto.TestResults;
import com.intland.jenkins.markup.BuildDataCollector;
import com.intland.jenkins.markup.BuildDto;
import com.intland.jenkins.markup.ScmDataCollector;
import com.intland.jenkins.markup.ScmDto;
import com.intland.jenkins.markup.TestResultCollector;
import com.intland.jenkins.markup.TestResultDto;
import com.intland.jenkins.markup.WikiMarkupBuilder;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

public class CodebeamerApiClient {
	private final String DEFAULT_TESTSET_NAME = "Coverage-TestSet";
	private final int HTTP_TIMEOUT = 10000;
	private HttpClient client;
	private RequestConfig requestConfig;
	private PluginConfiguration pluginConfiguration;
	private String baseUrl;
	private ObjectMapper objectMapper;
	private BuildListener listener;

	public CodebeamerApiClient(PluginConfiguration pluginConfiguration, BuildListener listener) {
		this.pluginConfiguration = pluginConfiguration;
		this.baseUrl = pluginConfiguration.getUri();
		this.listener = listener;

		this.objectMapper = new ObjectMapper();
		CredentialsProvider provider = this.getCredentialsProvider(pluginConfiguration.getUsername(),
				pluginConfiguration.getPassword());
		this.client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
		this.requestConfig = RequestConfig.custom().setConnectionRequestTimeout(this.HTTP_TIMEOUT)
				.setConnectTimeout(this.HTTP_TIMEOUT).setSocketTimeout(this.HTTP_TIMEOUT).build();
	}

	public void postTestRuns(TestResults tests, AbstractBuild<?, ?> build) throws IOException {
		String buildIdentifier = this.getBuildIdentifier(build);

		TrackerItemDto[] testCases = this.getTrackerItems(this.pluginConfiguration.getTestCaseTrackerId());
		NodeMapping testCasesMap = null; // XUnitUtil.getNodeMapping(testCases);

		Integer testSetId = null;
		/// this.findOrCreateTrackerItem(this.pluginConfiguration.getTestSetTrackerId(),
		// this.DEFAULT_TESTSET_NAME + "-" + buildIdentifier, "--");
		Map<String, Integer> testCasesForCurrentTestRun = new HashMap<>();
		for (TestResultItem test : tests.getTestResultItems()) {
			Integer testCaseId = this.findOrCreateTrackerItemInTree(test.getFullName(),
					this.pluginConfiguration.getTestCaseTrackerId(), testCasesMap,
					this.pluginConfiguration.getTestCaseParentId(), null);
			testCasesForCurrentTestRun.put(test.getFullName(), testCaseId);

		}

		TrackerItemDto parentItem = this.createParentTestRun(tests, buildIdentifier, build,
				this.pluginConfiguration.getTestConfigurationId(), testSetId, testCasesForCurrentTestRun.values());

		int uploadCounter = 0;
		for (TestResultItem test : tests.getTestResultItems()) {
			Integer testCaseId = testCasesForCurrentTestRun.get(test.getFullName());
			TrackerItemDto createdRun = this.createTestRun(this.pluginConfiguration.getTestConfigurationId(), testSetId,
					parentItem, test, testCaseId);
			this.updateTestRunStatusAndSpentTime(createdRun.getId(), "Completed", (long) (test.getDuration() * 1000));

			uploadCounter++;
			if ((uploadCounter % 100) == 0) {
				// XUnitUtil.log(this.listener, "uploaded: " + uploadCounter + "
				// test runs");
			}
		}

		this.updateTestSetTestCases(testSetId, testCasesForCurrentTestRun.values());
		this.updateTrackerItemStatus(testSetId, "Completed");
		// XUnitUtil.log(this.listener, "Upload finished, uploaded: " +
		// uploadCounter + " test runs");
	}

	private TrackerItemDto createTestRun(Integer testConfigurationId, Integer testSetId, TrackerItemDto parentItem,
			TestResultItem test, Integer testCaseId) throws IOException {
		TestRunDto testRunDto = new TestRunDto(test.getName(), parentItem.getId(),
				this.pluginConfiguration.getTestRunTrackerId(), Arrays.asList(new Integer[] { testCaseId }),
				testConfigurationId, test.getResult());
		testRunDto.setDescription("--");
		testRunDto.setTestSet(testSetId);
		return this.postTrackerItem(testRunDto);
	}

	private String createParentMarkup(AbstractBuild<?, ?> build) throws IOException {
		long currentTime = System.currentTimeMillis();
		BuildDto buildDto = BuildDataCollector.collectBuildData(build, currentTime);
		ScmDto scmDto = ScmDataCollector.collectScmData(build, this);
		TestResultDto testResultDto = TestResultCollector.collectTestResultData(build, this.listener);

		return new WikiMarkupBuilder().initWithTestReportTemplate().withBuildInfo(buildDto)
				.withTestReportInfo(testResultDto).withScmInfo(scmDto).build();
	}

	private TrackerItemDto createParentTestRun(TestResults tests, String buildIdentifier, AbstractBuild<?, ?> build,
			Integer testConfigurationId, Integer testSetId, Collection<Integer> testCaseIds) throws IOException {
		String newMarkupContent = this.createParentMarkup(build);
		TrackerItemDto parentItem;
		TestRunDto parentRunDto = new TestRunDto(buildIdentifier, null, this.pluginConfiguration.getTestRunTrackerId(),
				testCaseIds, testConfigurationId, tests.getStatus());
		parentRunDto.setTestSet(testSetId);
		parentRunDto.setDescription(tests.getTestSummary().toWikiMarkup() + newMarkupContent);
		parentRunDto.setDescFormat("Wiki");
		parentItem = this.postTrackerItem(parentRunDto);
		return parentItem;
	}

	/**
	 * Finds the tracker item by name in the specified tracker. If it is not
	 * exists the it create with the specified parameters
	 *
	 * @param trackerId
	 *            the item's tracker id
	 * @param name
	 *            the tracker's name
	 * @param description
	 *            the new item's description
	 * @return the found or the newly created tracker item
	 * @throws IOException
	 */
	public TrackerItemDto findOrCreateTrackerItem(Integer trackerId, String name, String description)
			throws IOException {
		String urlParamName = this.encodeParam(name);
		String content = this
				.get(String.format(this.baseUrl + "/rest/tracker/%s/items/or/name=%s/page/1", trackerId, urlParamName));
		PagedTrackerItemsDto pagedTrackerItemsDto = this.objectMapper.readValue(content, PagedTrackerItemsDto.class);

		if (pagedTrackerItemsDto.getTotal() > 0) {
			return pagedTrackerItemsDto.getItems()[0];
		} else {
			TestRunDto testConfig = new TestRunDto();
			testConfig.setName(name);
			testConfig.setTracker("/tracker/" + trackerId);
			testConfig.setDescription(description);
			return this.postTrackerItem(testConfig);
		}
	}

	public String encodeParam(String param) {
		try {
			String result = URLEncoder.encode(param, "UTF-8");
			return result.replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			return param;
		}
	}

	private String getBuildIdentifier(AbstractBuild<?, ?> build) {
		return build.getProject().getName() + "#" + build.getNumber();
	}

	private Integer findOrCreateTrackerItemInTree(String fullName, Integer trackerId, NodeMapping nodeMapping,
			Integer folder, Integer limit) throws IOException {
		fullName = null; // XUnitUtil.limitName(fullName, limit, ".");

		if ((folder != null) && (nodeMapping.getIdNodeMapping().get(folder) != null)) {
			fullName = nodeMapping.getIdNodeMapping().get(folder) + "." + fullName;
		}

		Integer result = nodeMapping.getNodeIdMapping().get(fullName);
		if (result == null) {
			StringTokenizer tokenizer = new StringTokenizer(fullName, ".");
			String segment = null;
			while (tokenizer.hasMoreElements()) {
				String token = tokenizer.nextToken();
				segment = segment == null ? token : segment + "." + token;

				if (nodeMapping.getNodeIdMapping().get(segment) == null) {
					result = this.postTrackerItemWithParent(segment, trackerId, result);
					// updateTrackerItemStatus(result, "Accepted");
					nodeMapping.getNodeIdMapping().put(segment, result);
				} else {
					result = nodeMapping.getNodeIdMapping().get(segment);
				}
			}
		}

		return result;
	}

	private Integer postTrackerItemWithParent(String path, Integer trackerId, Integer parentId) throws IOException {
		String name = path.substring(path.lastIndexOf(".") + 1);
		String tracker = String.format("/tracker/%s", trackerId);
		TestRunDto testCaseDto = new TestRunDto(name, tracker, parentId);
		testCaseDto.setDescription("--");
		testCaseDto.setType(trackerId.equals(this.pluginConfiguration.getTestCaseTrackerId()) ? "Automated" : null);
		String content = this.objectMapper.writeValueAsString(testCaseDto);
		return this.post(content).getId();
	}

	public TrackerItemDto postTrackerItem(TestRunDto testRunDto) throws IOException {
		String content = this.objectMapper.writeValueAsString(testRunDto);
		return this.post(content);
	}

	/**
	 * Get all tracker item from the specified tracker
	 *
	 * @param trackerId
	 *            the tracker's id
	 * @return all of the tracker item in the tracker
	 * @throws IOException
	 */
	public List<TrackerItemDto> getTrackerItemList(Integer trackerId) throws IOException {

		String url = "%s/rest/tracker/%s/items/page/%s?pagesize=500";

		List<TrackerItemDto> items = new ArrayList<>();
		int total = 0;
		int page = 1;
		do {
			// get a page from codebeamer
			String json = this.get(String.format(url, this.baseUrl, trackerId, page));
			PagedTrackerItemsDto pagedTrackerItemsDto = this.objectMapper.readValue(json, PagedTrackerItemsDto.class);
			total = pagedTrackerItemsDto.getTotal();
			items.addAll(Arrays.asList(pagedTrackerItemsDto.getItems()));

			page++;
		} while (items.size() < total);

		return items;
	}

	@Deprecated
	public TrackerItemDto[] getTrackerItems(Integer trackerId) throws IOException {
		String url = String.format("%s/rest/tracker/%s/items/page/1?pagesize=500", this.baseUrl, trackerId);
		String json = this.get(url);
		PagedTrackerItemsDto pagedTrackerItemsDto = this.objectMapper.readValue(json, PagedTrackerItemsDto.class);

		int numberOfRequests = (pagedTrackerItemsDto.getTotal() / 500) + 1;
		List<TrackerItemDto> items = Arrays.asList(pagedTrackerItemsDto.getItems());
		for (int i = 2; i < numberOfRequests; i++) {
			url = String.format("%s/rest/tracker/%s/items/page/%s?pagesize=500", this.baseUrl, trackerId,
					numberOfRequests);
			json = this.get(url);
			pagedTrackerItemsDto = this.objectMapper.readValue(json, PagedTrackerItemsDto.class);
			items.addAll(Arrays.asList(pagedTrackerItemsDto.getItems()));
		}

		return items.toArray(new TrackerItemDto[items.size()]);
	}

	private TrackerItemDto updateTestSetTestCases(Integer testSetId, Collection<Integer> testCases) throws IOException {
		List<Object[]> testCasesList = new ArrayList<>();
		for (Integer testCaseId : testCases) {
			testCasesList.add(new Object[] { new ReferenceDto("/item/" + testCaseId), Boolean.TRUE, Boolean.TRUE });
		}

		TrackerItemDto trackerItemDto = new TrackerItemDto();
		trackerItemDto.setUri("/item/" + testSetId);
		trackerItemDto.setTestCases(testCasesList);
		String content = this.objectMapper.writeValueAsString(trackerItemDto);
		return this.put(content);
	}

	private TrackerItemDto updateTestRunStatusAndSpentTime(Integer id, String status, Long duration)
			throws IOException {
		TestCaseDto testCaseDto = new TestCaseDto(id, status);
		testCaseDto.setSpentMillis(duration);
		String content = this.objectMapper.writeValueAsString(testCaseDto);
		return this.put(content);
	}

	private TrackerItemDto updateTrackerItemStatus(Integer id, String status) throws IOException {
		TestCaseDto testCaseDto = new TestCaseDto(id, status);
		String content = this.objectMapper.writeValueAsString(testCaseDto);
		return this.put(content);
	}

	public TrackerItemDto getTrackerItem(Integer itemId) throws IOException {
		String value = this.get(this.baseUrl + "/rest/item/" + itemId);
		return value != null ? this.objectMapper.readValue(value, TrackerItemDto.class) : null;
	}

	public TrackerDto getTrackerType(Integer trackerId) throws IOException {
		String value = this.get(this.baseUrl + "/rest/tracker/" + trackerId);
		return value != null ? this.objectMapper.readValue(value, TrackerDto.class) : null;
	}

	public String getUserId(String author) throws IOException {
		String authorNoSpace = author.replaceAll(" ", "");
		String tmpUrl = String.format("%s/rest/user/%s", this.baseUrl, authorNoSpace);

		String httpResult = this.get(tmpUrl);
		String result = null;

		if (httpResult != null) { // 20X success
			UserDto userDto = this.objectMapper.readValue(httpResult, UserDto.class);
			String uri = userDto.getUri();
			result = uri.substring(uri.lastIndexOf("/") + 1);
		}

		return result;
	}

	private TrackerItemDto post(String content) throws IOException {
		HttpPost post = new HttpPost(String.format("%s/rest/item", this.baseUrl));
		post.setConfig(this.requestConfig);

		StringEntity stringEntity = new StringEntity(content, "UTF-8");
		stringEntity.setContentType("application/json");
		post.setEntity(stringEntity);

		HttpResponse response = this.client.execute(post);
		String json = new BasicResponseHandler().handleResponse(response);
		post.releaseConnection();

		return this.objectMapper.readValue(json, TrackerItemDto.class);
	}

	public TrackerItemDto put(Object dto) throws IOException {
		String content = this.objectMapper.writeValueAsString(dto);
		return this.put(content);
	}

	private TrackerItemDto put(String content) throws IOException {
		HttpPut put = new HttpPut(String.format("%s/rest/item", this.baseUrl));
		put.setConfig(this.requestConfig);

		StringEntity stringEntity = new StringEntity(content, "UTF-8");
		stringEntity.setContentType("application/json");
		put.setEntity(stringEntity);

		HttpResponse response = this.client.execute(put);
		String json = new BasicResponseHandler().handleResponse(response);
		put.releaseConnection();

		return this.objectMapper.readValue(json, TrackerItemDto.class);
	}

	private String get(String url) throws IOException {
		HttpGet get = new HttpGet(url);
		get.setConfig(this.requestConfig);
		HttpResponse response = this.client.execute(get);
		int statusCode = response.getStatusLine().getStatusCode();

		String result = null;
		if (statusCode == 200) {
			result = new BasicResponseHandler().handleResponse(response);
		}

		get.releaseConnection();

		return result;
	}

	private CredentialsProvider getCredentialsProvider(String username, String password) {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		provider.setCredentials(AuthScope.ANY, credentials);
		return provider;
	}
}
