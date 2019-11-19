package com.pennant.backend.model.crm;

import java.math.BigDecimal;
import java.util.ArrayList;

public class LeadDetails {
	private float version;
	private float lastMntBy;
	private String lastMntOn;
	private String recordStatus;
	private float workflowId;
	private String userAction;
	private float leadId;
	private String leadReference;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private boolean dNCFlag;
	private float employerId;
	private boolean upload;
	private BigDecimal eliteCardLimit = BigDecimal.ZERO;
	ArrayList<AddressDetails> addressDetails = new ArrayList<AddressDetails>();
	ArrayList<PhoneDetails> phoneDetails = new ArrayList<PhoneDetails>();
	ArrayList<EmailDetails> emailDetails = new ArrayList<EmailDetails>();
	private boolean newRecord;
	ArrayList<ProductOfferDetails> productOfferDetails = new ArrayList<ProductOfferDetails>();

	public LeadDetails() {
		super();
	}

	public float getVersion() {
		return version;
	}

	public float getLastMntBy() {
		return lastMntBy;
	}

	public String getLastMntOn() {
		return lastMntOn;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public float getWorkflowId() {
		return workflowId;
	}

	public String getUserAction() {
		return userAction;
	}

	public float getLeadId() {
		return leadId;
	}

	public String getLeadReference() {
		return leadReference;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public boolean getDNCFlag() {
		return dNCFlag;
	}

	public float getEmployerId() {
		return employerId;
	}

	public boolean getUpload() {
		return upload;
	}

	public boolean getNewRecord() {
		return newRecord;
	}

	// Setter Methods

	public void setVersion(float version) {
		this.version = version;
	}

	public void setLastMntBy(float lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public void setLastMntOn(String lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public void setWorkflowId(float workflowId) {
		this.workflowId = workflowId;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public void setLeadId(float leadId) {
		this.leadId = leadId;
	}

	public void setLeadReference(String leadReference) {
		this.leadReference = leadReference;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setDNCFlag(boolean dNCFlag) {
		this.dNCFlag = dNCFlag;
	}

	public void setEmployerId(float employerId) {
		this.employerId = employerId;
	}

	public void setUpload(boolean upload) {
		this.upload = upload;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isdNCFlag() {
		return dNCFlag;
	}

	public void setdNCFlag(boolean dNCFlag) {
		this.dNCFlag = dNCFlag;
	}

	public ArrayList<AddressDetails> getAddressDetails() {
		return addressDetails;
	}

	public void setAddressDetails(ArrayList<AddressDetails> addressDetails) {
		this.addressDetails = addressDetails;
	}

	public ArrayList<PhoneDetails> getPhoneDetails() {
		return phoneDetails;
	}

	public void setPhoneDetails(ArrayList<PhoneDetails> phoneDetails) {
		this.phoneDetails = phoneDetails;
	}

	public ArrayList<EmailDetails> getEmailDetails() {
		return emailDetails;
	}

	public void setEmailDetails(ArrayList<EmailDetails> emailDetails) {
		this.emailDetails = emailDetails;
	}

	public ArrayList<ProductOfferDetails> getProductOfferDetails() {
		return productOfferDetails;
	}

	public void setProductOfferDetails(ArrayList<ProductOfferDetails> productOfferDetails) {
		this.productOfferDetails = productOfferDetails;
	}

	public BigDecimal getEliteCardLimit() {
		return eliteCardLimit;
	}

	public void setEliteCardLimit(BigDecimal eliteCardLimit) {
		this.eliteCardLimit = eliteCardLimit;
	}
}