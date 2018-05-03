package com.pennant.interfaceservice.model;

import java.util.Date;

public class InterfaceCustomerDedup {
	
	private String  custCIF;
	private String  custName;
	private String  finReference;
	private Date    custDOB;
	private String  custPassportNo;
	private String  custCRCPR;
	private String  mobileNumber;
	private String  custMotherMaiden;
	
	public String getCustMotherMaiden() {
		return custMotherMaiden;
	}
	public void setCustMotherMaiden(String custMotherMaiden) {
		this.custMotherMaiden = custMotherMaiden;
	}
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public Date getCustDOB() {
		return custDOB;
	}
	public void setCustDOB(Date custDOB) {
		this.custDOB = custDOB;
	}
	public String getCustPassportNo() {
		return custPassportNo;
	}
	public void setCustPassportNo(String custPassportNo) {
		this.custPassportNo = custPassportNo;
	}
	public String getCustCRCPR() {
		return custCRCPR;
	}
	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}

}
