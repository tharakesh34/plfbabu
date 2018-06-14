package com.pennanttech.niyogin.bre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "coAppscore", "noOfTimes30inL6M", "coAppAssetClassification" })
@XmlRootElement(name = "COBUREAU")
@XmlAccessorType(XmlAccessType.FIELD)
public class CoAppBureau {

	@XmlElement(name = "COAPPLICANTSCORE")
	private String	coAppscore;

	@XmlElement(name = "NOOFTIMES30INL6M")
	private int		noOfTimes30inL6M;

	@XmlElement(name = "COAPPLICANTASSETCLASSIFICATION")
	private String	coAppAssetClassification;

	public String getCoAppscore() {
		return coAppscore;
	}

	public void setCoAppscore(String coAppscore) {
		this.coAppscore = coAppscore;
	}

	public int getNoOfTimes30inL6M() {
		return noOfTimes30inL6M;
	}

	public void setNoOfTimes30inL6M(int noOfTimes30inL6M) {
		this.noOfTimes30inL6M = noOfTimes30inL6M;
	}

	public String getCoAppAssetClassification() {
		return coAppAssetClassification;
	}

	public void setCoAppAssetClassification(String coAppAssetClassification) {
		this.coAppAssetClassification = coAppAssetClassification;
	}

}
