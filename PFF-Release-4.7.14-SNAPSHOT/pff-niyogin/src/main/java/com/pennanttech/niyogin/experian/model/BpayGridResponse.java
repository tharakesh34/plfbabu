package com.pennanttech.niyogin.experian.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "segmentCode", "timePeriodInd", "year", "weekNumber", "month",
		 "assetClassification", "hdetailsResponse" })
@XmlRootElement(name = "BPAYGRID")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpayGridResponse implements Serializable {

	private static final long serialVersionUID = 6501472708129670451L;

	@XmlElement(name = "SegmentCode")
    private String segmentCode;
	
	@XmlElement(name = "TimePeriodInd")
    private String timePeriodInd;
	
	@XmlElement(name = "Year")
	private int year;
	
	@XmlElement(name = "WeekNumber")
    private String weekNumber;
	
	
	@XmlElement(name = "Monthvalue")
    private String month;
	
	
	@XmlElement(name = "PaymentStatusValue")
    private String paymentStatusValue;
	
	@XmlElement(name = "DaysPastDue")
	private String[] daysPastDue;
	
	
	@XmlElement(name = "AssetClassification")
	private String assetClassification;
	
	@XmlElement(name = "HDETAILS")
	private HDetailsResponse	hdetailsResponse;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getPaymentStatusValue() {
		return paymentStatusValue;
	}

	public void setPaymentStatusValue(String paymentStatusValue) {
		this.paymentStatusValue = paymentStatusValue;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getAssetClassification() {
		return assetClassification;
	}

	public void setAssetClassification(String assetClassification) {
		this.assetClassification = assetClassification;
	}

	public String getWeekNumber() {
		return weekNumber;
	}

	public void setWeekNumber(String weekNumber) {
		this.weekNumber = weekNumber;
	}

	public String getTimePeriodInd() {
		return timePeriodInd;
	}

	public void setTimePeriodInd(String timePeriodInd) {
		this.timePeriodInd = timePeriodInd;
	}

	public String[] getDaysPastDue() {
		return daysPastDue;
	}

	public void setDaysPastDue(String[] daysPastDue) {
		this.daysPastDue = daysPastDue;
	}

	public String getSegmentCode() {
		return segmentCode;
	}

	public void setSegmentCode(String segmentCode) {
		this.segmentCode = segmentCode;
	}

	public HDetailsResponse getHdetailsResponse() {
		return hdetailsResponse;
	}

	public void setHdetailsResponse(HDetailsResponse hdetailsResponse) {
		this.hdetailsResponse = hdetailsResponse;
	}

}
