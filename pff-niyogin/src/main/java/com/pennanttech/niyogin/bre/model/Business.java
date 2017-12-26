
package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "busPremisesOwnership", "orgType", "numberOfOwnersOrShareholdingPattern",
		"operationalBusVintage" })
@XmlRootElement(name = "Business")
@XmlAccessorType(XmlAccessType.FIELD)
public class Business {

	@XmlElement(name = "businesspremisesownership")
	private String	busPremisesOwnership;

	@XmlElement(name = "organizationtype")
	private String	orgType;

	@XmlElement(name = "numberofownersorshareholdingpattern")
	private String	numberOfOwnersOrShareholdingPattern;

	@XmlElement(name = "operationalbusinessvintage")
	private String	operationalBusVintage;

	public String getBusPremisesOwnership() {
		return busPremisesOwnership;
	}

	public void setBusPremisesOwnership(String busPremisesOwnership) {
		this.busPremisesOwnership = busPremisesOwnership;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getNumberOfOwnersOrShareholdingPattern() {
		return numberOfOwnersOrShareholdingPattern;
	}

	public void setNumberOfOwnersOrShareholdingPattern(String numberOfOwnersOrShareholdingPattern) {
		this.numberOfOwnersOrShareholdingPattern = numberOfOwnersOrShareholdingPattern;
	}

	public String getOperationalBusVintage() {
		return operationalBusVintage;
	}

	public void setOperationalBusVintage(String operationalBusVintage) {
		this.operationalBusVintage = operationalBusVintage;
	}

}
