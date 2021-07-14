package com.pennant.backend.model.dedup.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class CustDGDetail {
	@JsonProperty("Customer_ID__c")
	private String customerId;
	@JsonProperty("Customer_Type__c")
	private String customerType;
	@JsonProperty("Name")
	private String customerName;
	@JsonProperty("Fathers_Husband_s_Name__c")
	private String FatherorHusbandName;
	@JsonProperty("DOB__c")
	private String dateOfBirth;
	@JsonProperty("PAN__c")
	private String panNumber;
	@JsonProperty("Voterid__c")
	private String voiterId;
	@JsonProperty("Cin__c")
	private String cinNumber;
	@JsonProperty("Din__c")
	private String dinNumber;
	@JsonProperty("AadhaarNo")
	private String aadhaarNo;
	@JsonProperty("Gender_Flag")
	private String gender;
	@JsonProperty("Marital_Status_Flag")
	private String maritalSts;
	@JsonProperty("Source_System")
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
