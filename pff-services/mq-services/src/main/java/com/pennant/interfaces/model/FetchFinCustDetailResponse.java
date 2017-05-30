package com.pennant.interfaces.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="FetchFinCustDetailResponse")
public class FetchFinCustDetailResponse {

	private String referenceNum;
	private String customerType;
	private String customerID;
	private String customerIDNum;
	private String customerName;
	private String mobileNo;
	private String emailID;
	private Date installmentDate;
	private int noOfInstallments;
	private int ccyEditField;
	private BigDecimal eMIAmount;
	private String returnCode;
	private String returnText;
	private long timeStamp;
	
	private String custCRCPR;
	private String custCtgCode;
	private String passportNumber;
	private String tradeNumber;
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	@XmlElement(name="ReferenceNum")
	public String getReferenceNum() {
		return referenceNum;
	}
	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}
	
	@XmlElement(name="CustomerType")
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	
	@XmlElement(name="CustomerID")
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	
	@XmlElement(name="CustomerIDNum")
	public String getCustomerIDNum() {
		return customerIDNum;
	}
	public void setCustomerIDNum(String customerIDNum) {
		this.customerIDNum = customerIDNum;
	}
	
	@XmlElement(name="CustomerName")
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	@XmlElement(name="MobileNo")
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	
	@XmlElement(name="EmailID")
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	
	@XmlElement(name="InstallmentDate")
	public Date getInstallmentDate() {
		return installmentDate;
	}
	public void setInstallmentDate(Date installmentDate) {
		this.installmentDate = installmentDate;
	}
	
	@XmlElement(name="NoOfInstallments")
	public int getNoOfInstallments() {
		return noOfInstallments;
	}
	public void setNoOfInstallments(int noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}
	
	@XmlElement(name="EMIAmount")
	public BigDecimal getEMIAmount() {
		return eMIAmount;
	}
	public void setEMIAmount(BigDecimal eMIAmount) {
		this.eMIAmount = eMIAmount;
	}
	
	@XmlElement(name="ReturnCode")
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	
	@XmlElement(name="ReturnText")
	public String getReturnText() {
		return returnText;
	}
	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}
	
	@XmlElement(name="TimeStamp")
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public int getCcyEditField() {
		return ccyEditField;
	}
	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}
	
	public String getCustCRCPR() {
		return custCRCPR;
	}

	public void setCustCRCPR(String custCRCPR) {
		this.custCRCPR = custCRCPR;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getTradeNumber() {
		return tradeNumber;
	}

	public void setTradeNumber(String tradeNumber) {
		this.tradeNumber = tradeNumber;
	}

	public String getCustCtgCode() {
		return custCtgCode;
	}

	public void setCustCtgCode(String custCtgCode) {
		this.custCtgCode = custCtgCode;
	}


}
