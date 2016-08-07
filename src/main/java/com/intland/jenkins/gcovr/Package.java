//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2016.07.30 at 04:15:50 PM CEST
//

package com.intland.jenkins.gcovr;

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
 *     &lt;extension base="{}classes">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="line-rate" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="branch-rate" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="complexity" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "package")
public class Package extends Classes {

	@XmlAttribute(name = "name", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String name;
	@XmlAttribute(name = "line-rate", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String lineRate;
	@XmlAttribute(name = "branch-rate", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String branchRate;
	@XmlAttribute(name = "complexity", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String complexity;

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Gets the value of the lineRate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLineRate() {
		return this.lineRate;
	}

	/**
	 * Sets the value of the lineRate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLineRate(String value) {
		this.lineRate = value;
	}

	/**
	 * Gets the value of the branchRate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBranchRate() {
		return this.branchRate;
	}

	/**
	 * Sets the value of the branchRate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBranchRate(String value) {
		this.branchRate = value;
	}

	/**
	 * Gets the value of the complexity property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getComplexity() {
		return this.complexity;
	}

	/**
	 * Sets the value of the complexity property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setComplexity(String value) {
		this.complexity = value;
	}

}
