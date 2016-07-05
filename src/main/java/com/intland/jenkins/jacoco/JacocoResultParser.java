package com.intland.jenkins.jacoco;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.intland.jenkins.HTMLMarkupBuilder;
import com.intland.jenkins.coverage.ICoverageCoverter;
import com.intland.jenkins.coverage.model.CoverageBase;
import com.intland.jenkins.coverage.model.CoverageReport;
import com.intland.jenkins.coverage.model.DirectoryCoverage;
import com.intland.jenkins.jacoco.model.Class;
import com.intland.jenkins.jacoco.model.Counter;
import com.intland.jenkins.jacoco.model.Group;
import com.intland.jenkins.jacoco.model.Package;
import com.intland.jenkins.jacoco.model.Report;

public class JacocoResultParser implements ICoverageCoverter {

	@Override
	public CoverageReport collectCoverageReport(String reportFilePath) {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Report.class);

			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			spf.setFeature("http://xml.org/sax/features/validation", false);

			XMLReader xmlReader = spf.newSAXParser().getXMLReader();
			InputSource inputSource = new InputSource(new FileReader(reportFilePath));
			SAXSource source = new SAXSource(xmlReader, inputSource);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			Report report = (Report) jaxbUnmarshaller.unmarshal(source);

			return this.convertToCoverageReport(report);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	private CoverageReport convertToCoverageReport(Report report) {

		CoverageReport coverageReport = new CoverageReport();
		coverageReport.setName(report.getName());

		List<Group> groups = report.getGroup();
		if (groups != null) {
			for (Group group : groups) {
				for (Package pack : group.getPackage()) {
					coverageReport.getDirectories().add(this.converPackage(pack));
				}
			}
		}

		List<Package> packages = report.getPackage();
		for (Package onePackage : packages) {
			coverageReport.getDirectories().add(this.converPackage(onePackage));
		}

		this.setCoverage(coverageReport, report.getCounter());
		coverageReport.setMarkup(HTMLMarkupBuilder.genearteSummary(report));

		return coverageReport;
	}

	private DirectoryCoverage converPackage(Package onePackage) {

		DirectoryCoverage directoryCoverage = new DirectoryCoverage();
		directoryCoverage.setName(StringUtils.replace(onePackage.getName(), "/", "."));

		for (Class clazz : onePackage.getClazz()) {
			directoryCoverage.getFiles().add(this.converClass(clazz, onePackage.getName()));
		}

		this.setCoverage(directoryCoverage, onePackage.getCounter());
		directoryCoverage.setMarkup(HTMLMarkupBuilder.genearteSummary(onePackage));

		return directoryCoverage;
	}

	private CoverageBase converClass(Class clazz, String packageName) {

		CoverageBase base = new CoverageBase();
		base.setName(StringUtils.replace(clazz.getName(), packageName + "/", ""));

		this.setCoverage(base, clazz.getCounter());
		base.setMarkup(HTMLMarkupBuilder.genearteSummary(clazz));

		return base;
	}

	private void setCoverage(CoverageBase base, List<Counter> counters) {
		for (Counter counter : counters) {
			String type = StringUtils.lowerCase(counter.getType());
			switch (type) {
			case "line":
				base.setLineCovered(counter.getCovered());
				base.setLineMissed(counter.getMissed());
				break;
			case "instruction":
				base.setInstructionCovered(counter.getCovered());
				base.setInstructionMissed(counter.getMissed());
				break;
			case "complexity":
				base.setComplexityCovered(counter.getCovered());
				base.setComplexityMissed(counter.getMissed());
				break;
			case "method":
				base.setMethodCovered(counter.getCovered());
				base.setMethodMissed(counter.getMissed());
				break;
			case "branch":
				base.setBranchCovered(counter.getCovered());
				base.setBranchMissed(counter.getMissed());
				break;
			case "class":
				base.setClassCovered(counter.getCovered());
				base.setClassMissed(counter.getMissed());
				break;
			default:
				break;
			}
		}
	}

}
