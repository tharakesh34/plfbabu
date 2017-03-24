package com.pennant.coreinterface.model.customer;

import java.io.Serializable;
import java.util.Date;

public class InterfaceNorkamCheck implements Serializable {

	private static final long serialVersionUID = -5727376601170364598L;

	public InterfaceNorkamCheck() {
		super();
	}

	private String referenceNum;
	private String customerName;
	private String customerId;
	private String customerAddress;
	private String customerCountry;
	private Date customerDOB;
	private String customerPOB;
	private String customerOrganization;
	private String returnCode;
	private String returnText;
	private long timeStamp;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerCountry() {
		return customerCountry;
	}

	public void setCustomerCountry(String customerCountry) {
		this.customerCountry = customerCountry;
	}

	public Date getCustomerDOB() {
		return customerDOB;
	}

	public void setCustomerDOB(Date customerDOB) {
		this.customerDOB = customerDOB;
	}

	public String getCustomerPOB() {
		return customerPOB;
	}

	public void setCustomerPOB(String customerPOB) {
		this.customerPOB = customerPOB;
	}

	public String getCustomerOrganization() {
		return customerOrganization;
	}

	public void setCustomerOrganization(String customerOrganization) {
		this.customerOrganization = customerOrganization;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
