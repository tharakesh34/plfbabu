package com.pennanttech.bajaj.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)	
public class CustDGDetail {
	@XmlElement(name = "Customer_ID__c")
	private String customerId;
	@XmlElement(name = "Customer_Type__c")
	private String customerType;
	@XmlElement(name = "Name")
	private String customerName;
	@XmlElement(name = "Fathers_Husband_s_Name__c")
	private String FatherorHusbandName;
	@XmlElement(name = "DOB__c")
	private String dateOfBirth;
	@XmlElement(name = "PAN__c")
	private String panNumber;
	@XmlElement(name = "Voterid__c")
	private String voiterId;
	@XmlElement(name = "Cin__c")
	private String cinNumber;
	@XmlElement(name = "Din__c")
	private String dinNumber;
	@XmlElement(name = "AadhaarNo")
	private String aadhaarNo;
	@XmlElement(name = "Gender_Flag")
	private String gender;
	@XmlElement(name = "Marital_Status_Flag")
	private String maritalSts;
	@XmlElement(name = "Source_System")
	private String sourceSystem;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getFatherorHusbandName() {
		return FatherorHusbandName;
	}

	public void setFatherorHusbandName(String fatherorHusbandName) {
		FatherorHusbandName = fatherorHusbandName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getVoiterId() {
		return voiterId;
	}

	public void setVoiterId(String voiterId) {
		this.voiterId = voiterId;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getCinNumber() {
		return cinNumber;
	}

	public void setCinNumber(String cinNumber) {
		this.cinNumber = cinNumber;
	}

	public String getDinNumber() {
		return dinNumber;
	}

	public void setDinNumber(String dinNumber) {
		this.dinNumber = dinNumber;
	}

	public String getAadhaarNo() {
		return aadhaarNo;
	}

	public void setAadhaarNo(String aadhaarNo) {
		this.aadhaarNo = aadhaarNo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalSts() {
		return maritalSts;
	}

	public void setMaritalSts(String maritalSts) {
		this.maritalSts = maritalSts;
	}

	@Override
	public String toString() {
		return "CustDGDetail [customerId=" + customerId + ", customerType=" + customerType + ", customerName="
				+ customerName + ", FatherorHusbandName=" + FatherorHusbandName + ", dateOfBirth=" + dateOfBirth
				+ ", panNumber=" + panNumber + ", voiterId=" + voiterId + ", sourceSystem=" + sourceSystem + "]";
	}

}
