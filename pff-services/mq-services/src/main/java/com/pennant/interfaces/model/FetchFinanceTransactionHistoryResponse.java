package com.pennant.interfaces.model;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FetchFinanceDetailsResponse")
public class FetchFinanceTransactionHistoryResponse {

	private String referenceNum;
	private String returnCode;
	private String returnText;
	private Timestamp timestamp;
	private Transaction transaction;

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

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@XmlElement(name = "Timestamp")
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	@XmlElement(name = "Transaction")
	public Transaction getTransaction() {
		return this.transaction;
	}
}
