package com.pennanttech.niyogin.bre.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "busPremisesOwnership", "orgType", "numbOfOwnersOrShareholdingPattern",
		"operationalBusinessVintage" })
@XmlRootElement(name = "BUSINESS")
@XmlAccessorType(XmlAccessType.FIELD)
public class Business {

	@XmlElement(name = "BUSINESSPREMISESOWNERSHIP")
	private String		busPremisesOwnership;

	@XmlElement(name = "ORGANIZATIONTYPE")
	private String		orgType;

	@XmlElement(name = "NUMBEROFOWNERSORSHAREHOLDINGPATTERN")
	private int			numbOfOwnersOrShareholdingPattern;

	@XmlElement(name = "OPERATIONALBUSINESSVINTAGE")
	private BigDecimal	operationalBusinessVintage	= BigDecimal.ZERO;

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

	public int getNumbOfOwnersOrShareholdingPattern() {
		return numbOfOwnersOrShareholdingPattern;
	}

	public void setNumbOfOwnersOrShareholdingPattern(int numbOfOwnersOrShareholdingPattern) {
		this.numbOfOwnersOrShareholdingPattern = numbOfOwnersOrShareholdingPattern;
	}

	public BigDecimal getOperationalBusinessVintage() {
		return operationalBusinessVintage;
	}

	public void setOperationalBusinessVintage(BigDecimal operationalBusinessVintage) {
		this.operationalBusinessVintage = operationalBusinessVintage;
	}

}
