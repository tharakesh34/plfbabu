package com.pennant.coreinterface.model.customer;

import java.io.Serializable;
import java.util.Date;

public class InterfaceMortgageDetail implements Serializable {

	private static final long serialVersionUID = 8834741927485659871L;
	
	private String transactionType;
	private String mortgageRefNo;
	private String mortgageSourceCode;
	private String chassisNo;
	private String trafficProfileNo;
	private String custCRCPR;
	private String approvedBy;
	private Date approvedDate;
	private String remarks;
	private String transactionId;
	private String returncode;
	private String returnText;
	

	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public String getMortgageRefNo() {
		return mortgageRefNo;
	}
	public void setMortgageRefNo(String mortgageRefNo) {
		this.mortgageRefNo = mortgageRefNo;
	}
	
	public String getMortgageSourceCode() {
		return mortgageSourceCode;
	}
	public void setMortgageSourceCode(String mortgageSourceCode) {
		this.mortgageSourceCode = mortgageSourceCode;
	}
	
	public String getChassisNo() {
		return chassisNo;
	}
	public void setChassisNo(String chassisNo) {
		this.chassisNo = chassisNo;
	}
	
	public String getTrafficProfileNo() {
		return trafficProfileNo;
	}
	public void setTrafficProfileNo(String trafficProfileNo) {
		this.trafficProfileNo = trafficProfileNo;
	}
	
	public String getCustCRCPR() {
		return custCRCPR;
	}
	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}
	
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
		
	public Date getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getReturncode() {
		return returncode;
	}
	public void setReturncode(String returncode) {
		this.returncode = returncode;
	}
	public String getReturnText() {
		return returnText;
	}
	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}
	
	
}
