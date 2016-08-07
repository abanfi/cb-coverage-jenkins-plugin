//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2016.07.30 at 04:15:50 PM CEST
//

package com.intland.jenkins.gcovr;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}conditions" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="hits" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="branch" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="false" />
 *       &lt;attribute name="condition-coverage" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="100%" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "conditions" })
@XmlRootElement(name = "line")
public class Line {

	protected List<Conditions> conditions;
	@XmlAttribute(name = "number", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String number;
	@XmlAttribute(name = "hits", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String hits;
	@XmlAttribute(name = "branch")
	@XmlSchemaType(name = "anySimpleType")
	protected String branch;
	@XmlAttribute(name = "condition-coverage")
	@XmlSchemaType(name = "anySimpleType")
	protected String conditionCoverage;

	/**
	 * Gets the value of the conditions property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the conditions property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getConditions().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Conditions }
	 * 
	 * 
	 */
	public List<Conditions> getConditions() {
		if (this.conditions == null) {
			this.conditions = new ArrayList<Conditions>();
		}
		return this.conditions;
	}

	/**
	 * Gets the value of the number property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * Sets the value of the number property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNumber(String value) {
		this.number = value;
	}

	/**
	 * Gets the value of the hits property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHits() {
		return this.hits;
	}

	/**
	 * Sets the value of the hits property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHits(String value) {
		this.hits = value;
	}

	/**
	 * Gets the value of the branch property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBranch() {
		if (this.branch == null) {
			return "false";
		} else {
			return this.branch;
		}
	}

	/**
	 * Sets the value of the branch property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBranch(String value) {
		this.branch = value;
	}

	/**
	 * Gets the value of the conditionCoverage property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getConditionCoverage() {
		if (this.conditionCoverage == null) {
			return "100%";
		} else {
			return this.conditionCoverage;
		}
	}

	/**
	 * Sets the value of the conditionCoverage property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setConditionCoverage(String value) {
		this.conditionCoverage = value;
	}

}
