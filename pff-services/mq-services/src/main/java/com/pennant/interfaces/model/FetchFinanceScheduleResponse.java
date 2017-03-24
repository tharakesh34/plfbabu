package com.pennant.interfaces.model;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FetchFinanceAmortizationScheduleResponse")
public class FetchFinanceScheduleResponse {

	private String referenceNum;
	private String returnCode;
	private String returnText;
	private AmortizationSchedulePeriod amortizationSchedulePeriod;
	private Timestamp timestamp;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return this.referenceNum;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name = "ReturnCode")
	public String getReturnCode() {
		return this.returnCode;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@XmlElement(name = "ReturnText")
	public String getReturnText() {
		return this.returnText;
	}

	@XmlElement(name = "AmortizationSchedulePeriod")
	public AmortizationSchedulePeriod getAmortizationSchedulePeriod() {
		return amortizationSchedulePeriod;
	}

	public void setAmortizationSchedulePeriod(AmortizationSchedulePeriod amortizationSchedulePeriod) {
		this.amortizationSchedulePeriod = amortizationSchedulePeriod;
	}
	
	@XmlElement(name = "Timestamp")
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
