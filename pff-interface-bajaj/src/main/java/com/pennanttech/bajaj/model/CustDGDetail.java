package com.pennanttech.bajaj.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;


@JsonPropertyOrder({"Customer_ID__c","Customer_Type__c","Name","Fathers_Husband_s_Name__c","DOB__c","PAN__c","Voterid__c","Source_System"})
@JsonIgnoreProperties(ignoreUnknown = true)
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
	@Override
	public String toString() {
		return "CustDGDetail [customerId=" + customerId + ", customerType="
				+ customerType + ", customerName=" + customerName
				+ ", FatherorHusbandName=" + FatherorHusbandName
				+ ", dateOfBirth=" + dateOfBirth + ", panNumber=" + panNumber
				+ ", voiterId=" + voiterId + "]";
	}

	
}
