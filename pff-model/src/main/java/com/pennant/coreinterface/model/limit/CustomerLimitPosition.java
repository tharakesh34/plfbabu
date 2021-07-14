package com.pennant.coreinterface.model.limit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CustomerLimitSummaryReply")
public class CustomerLimitPosition implements Serializable {

	private static final long serialVersionUID = 6044558650461849275L;

	private String referenceNum;
	private String customerReference;
	private String branchCode;
	private String custRef;
	private String groupRef;
	private String limitCurrency;
	private BigDecimal totalOutstanding = BigDecimal.ZERO;
	private String returnCode;
	private String returnText;
	private long timeStamp;
	private List<CustomerLimitSummary> limitSummary;

	public CustomerLimitPosition() {
		super();
	}

	@JsonProperty("ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	@JsonProperty("CustomerReference")
	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	@JsonProperty("BranchCode")
	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	@JsonProperty("TotalOutstanding")
	public BigDecimal getTotalOutstanding() {
		return totalOutstanding;
	}

	public void setTotalOutstanding(BigDecimal totalOutstanding) {
		this.totalOutstanding = totalOutstanding;
	}

	@JsonProperty("CustRef")
	public String getCustRef() {
		return custRef;
	}

	public void setCustRef(String custRef) {
		this.custRef = custRef;
	}

	@JsonProperty("GroupRef")
	public String getGroupRef() {
		return groupRef;
	}

	public void setGroupRef(String groupRef) {
		this.groupRef = groupRef;
	}

	@JsonProperty("ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	@JsonProperty("ReturnText")
	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@JsonProperty("TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@JsonProperty("Limits")
	public List<CustomerLimitSummary> getLimitSummary() {
		return limitSummary;
	}

	public void setLimitSummary(List<CustomerLimitSummary> limitSummary) {
		this.limitSummary = limitSummary;
	}

	@JsonProperty("LimitCurrency")
	public String getLimitCurrency() {
		return limitCurrency;
	}

	public void setLimitCurrency(String limitCurrency) {
		this.limitCurrency = limitCurrency;
	}
}
