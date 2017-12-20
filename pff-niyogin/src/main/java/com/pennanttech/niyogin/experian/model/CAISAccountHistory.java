package com.pennanttech.niyogin.experian.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "year", "month", "daysPastDue", "assetClassification" })
@XmlRootElement(name = "CAIS_Account_History")
@XmlAccessorType(XmlAccessType.FIELD)
public class CAISAccountHistory implements Serializable {

	private static final long serialVersionUID = 3196204061059085758L;
	
	@XmlElement(name = "Year")
	private int		year;
	@XmlElement(name = "Month")
	private int		month;
	@XmlElement(name = "Days_Past_Due")
	private int		daysPastDue;
	@XmlElement(name = "Asset_Classification")
	private String	assetClassification;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDaysPastDue() {
		return daysPastDue;
	}

	public void setDaysPastDue(int daysPastDue) {
		this.daysPastDue = daysPastDue;
	}

	public String getAssetClassification() {
		return assetClassification;
	}

	public void setAssetClassification(String assetClassification) {
		this.assetClassification = assetClassification;
	}

	@Override
	public String toString() {
		return "EMIBounceResponse [year=" + year + ", month=" + month + ", daysPastDue=" + daysPastDue
				+ ", assetClassification=" + assetClassification + "]";
	}

}
