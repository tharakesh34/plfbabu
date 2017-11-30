package com.pennanttech.niyogin.experian.commercial.model;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "year", "paymentStatusValue", "monthvalue", "assetClassification", "weekNumber", "timePeriodInd",
		"daysPastDue", "segmentCode", "hdetailsResponse" })
@XmlRootElement(name = "BPAYGRID")
@XmlAccessorType(XmlAccessType.FIELD)
public class BpayGridResponse implements Serializable {

	private static final long serialVersionUID = 6501472708129670451L;

	@XmlElement(name = "Year")
	private int year;
	
	@XmlElement(name = "PaymentStatusValue")
    private String paymentStatusValue;
	
	@XmlElement(name = "Monthvalue")
    private int monthvalue;
	
	@XmlElement(name = "AssetClassification")
	private String assetClassification;
	
	@XmlElement(name = "WeekNumber")
    private String weekNumber;
	
	@XmlElement(name = "TimePeriodInd")
    private String timePeriodInd;
	
	@XmlElement(name = "DaysPastDue")
    private String[] daysPastDue;
	
	@XmlElement(name = "SegmentCode")
    private String segmentCode;
	
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

	public int getMonthvalue() {
		return monthvalue;
	}

	public void setMonthvalue(int monthvalue) {
		this.monthvalue = monthvalue;
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

	@Override
	public String toString() {
		return "BpayGridResponse [year=" + year + ", paymentStatusValue=" + paymentStatusValue + ", monthvalue="
				+ monthvalue + ", assetClassification=" + assetClassification + ", weekNumber=" + weekNumber
				+ ", timePeriodInd=" + timePeriodInd + ", daysPastDue=" + Arrays.toString(daysPastDue)
				+ ", segmentCode=" + segmentCode + ", hdetailsResponse=" + hdetailsResponse + "]";
	}

}
