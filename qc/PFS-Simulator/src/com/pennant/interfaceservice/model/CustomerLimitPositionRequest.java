package com.pennant.interfaceservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CustomerLimitSummaryRequest")
public class CustomerLimitPositionRequest {

	private String referenceNum;
	private String customerReference;
	private String branchCode;
	private String custRef;
	private String groupRef;
	private String limitCurrency;
	private long timeStamp;

	public CustomerLimitPositionRequest() {
		super();
	}
	

	@XmlElement(name="ReferenceNum")
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

	@XmlElement(name = "BranchCode")
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	@XmlElement(name="CustRef")
	public String getCustRef() {
		return custRef;
	}

	public void setCustRef(String custRef) {
		this.custRef = custRef;
	}

	@XmlElement(name="GroupRef")
	public String getGroupRef() {
		return groupRef;
	}

	public void setGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@XmlElement(name = "LimitCurrency")
	public String getLimitCurrency() {
		return limitCurrency;
	}

	public void setLimitCurrency(String limitCurrency) {
		this.limitCurrency = limitCurrency;
	}
}
