package com.pennant.interfaceservice.model;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CustomerLimitSummaryReply")
public class CustomerLimitPositionReply {

	private String referenceNum;
	private String custRef;
	private String groupRef;
	private String branchCode;
	private BigDecimal totalLimit = BigDecimal.ZERO;
	private BigDecimal totalOutstanding = BigDecimal.ZERO;
	private String returnCode;
	private String returnText;
	private long timeStamp;
	private List<CustomerLimitSummary> limitSummary;

	public CustomerLimitPositionReply() {
		super();
	}
	
	@XmlElement(name = "BranchCode")
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	@XmlElement(name="TotalLimit")
	public BigDecimal getTotalLimit() {
		return totalLimit;
	}

	public void setTotalLimit(BigDecimal totalLimit) {
		this.totalLimit = totalLimit;
	}

	@XmlElement(name="TotalOutstanding")
	public BigDecimal getTotalOutstanding() {
		return totalOutstanding;
	}

	public void setTotalOutstanding(BigDecimal totalOutstanding) {
		this.totalOutstanding = totalOutstanding;
	}

	@XmlElement(name="ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
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

	@XmlElement(name = "ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@XmlElement(name = "ReturnText")
	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@XmlElement(name = "TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@XmlElement(name="Summary")
	public List<CustomerLimitSummary> getLimitSummary() {
		return limitSummary;
	}

	public void setLimitSummary(List<CustomerLimitSummary> limitSummary) {
		this.limitSummary = limitSummary;
	}
}
