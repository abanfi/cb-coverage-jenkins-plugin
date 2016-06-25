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

import com.intland.jenkins.coverage.ICoverageCoverter;
import com.intland.jenkins.coverage.model.CoverageBase;
import com.intland.jenkins.coverage.model.CoverageReport;
import com.intland.jenkins.coverage.model.DirectoryCoverage;
import com.intland.jenkins.jacoco.model.Class;
import com.intland.jenkins.jacoco.model.Counter;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private CoverageReport convertToCoverageReport(Report report) {

		CoverageReport coverageReport = new CoverageReport();

		List<Package> packages = report.getPackage();
		for (Package onePackage : packages) {
			coverageReport.getDirectories().add(this.converPackage(onePackage));
		}

		this.setCoverage(coverageReport, report.getCounter());

		return coverageReport;
	}

	private DirectoryCoverage converPackage(Package onePackage) {

		DirectoryCoverage directoryCoverage = new DirectoryCoverage();
		directoryCoverage.setName(onePackage.getName());

		for (Class clazz : onePackage.getClazz()) {
			directoryCoverage.getFiles().add(this.converClass(clazz));
		}

		this.setCoverage(directoryCoverage, onePackage.getCounter());

		return directoryCoverage;
	}

	private CoverageBase converClass(Class clazz) {

		CoverageBase base = new CoverageBase();
		base.setName(clazz.getName());

		this.setCoverage(base, clazz.getCounter());

		return base;
	}

	private void setCoverage(CoverageBase base, List<Counter> counters) {
		for (Counter counter : counters) {
			if (StringUtils.equalsIgnoreCase(counter.getType(), "line")) {
				Integer all = counter.getCovered() + counter.getMissed();
				base.setLineCoverage(counter.getCovered() / (double) all);
			}
		}
	}

}
