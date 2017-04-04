package com.pennanttech.bajaj.model;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonPropertyOrder;


@JsonPropertyOrder({"Customer_ID__c","Customer_Type__c","Name","Fathers_Husband_s_Name__c","DOB__c","PAN__c","Voterid__c"})
public class CustDGDetail {
	@XmlElement(name="Customer_ID__c")
	private String customerId;
	@XmlElement(name="Customer_Type__c")
	private String customerType;
	@XmlElement(name="Name")
	private String customerName;
	@XmlElement(name="Fathers_Husband_s_Name__c")
	private String FatherorHusbandName;
	@XmlElement(name="DOB__c")
	private String dateOfBirth;
	@XmlElement(name="PAN__c")
	private String panNumber;
	@XmlElement(name="Voterid__c")
	private String voiterId;
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

	
}
