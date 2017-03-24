package com.pennant.interfaces.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LimitActivationRequest")
public class LimitActivationRequest {

	private String referenceNum;
	private String customerReference;
	private List<LimitDetails> limitDetails = new ArrayList<LimitDetails>();
	private String branchCode;
	private long timestamp;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name = "ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@XmlElement(name = "CustomerReference")
	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	@XmlElement(name = "LimitDetails")
	public List<LimitDetails> getLimitDetails() {
		return limitDetails;
	}

	public void setLimitDetails(List<LimitDetails> limitDetails) {
		this.limitDetails = limitDetails;
	}

	@XmlElement(name = "BranchCode")
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}