package com.pennant.interfaces.model;

import java.sql.Timestamp;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType (propOrder={"referenceNum","returnCode","returnText","customerNo","finance", "timestamp"})
@XmlRootElement(name = "FetchCustomerFinancesResponse")
public class FetchCustomerFinancesResponse {

	private String referenceNum;
	private String returnCode;
	private String returnText;
	private String customerNo;
	private List<Finance> finance;
	private Timestamp timestamp;
	
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
	
	@XmlElement(name = "CustomerNo")
	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	@XmlElement(name = "Timestamp")
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@XmlElement(name = "Finance")
	public List<Finance> getFinance() {
		return finance;
	}

	public void setFinance(List<Finance> finance) {
		this.finance = finance;
	}
}
