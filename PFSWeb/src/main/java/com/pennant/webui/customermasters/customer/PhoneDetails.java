package com.pennant.webui.customermasters.customer;

public class PhoneDetails {
	private String userAction;

	private String recordStatus;

	private String phoneNumber;

	private String lastMntBy;

	private String lastMntOn;

	private String priority;

	private String version;

	private String phoneTypeCode;

	private String workflowId;

	private String newRecord;

	private String leadId;

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(String lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(String lastMntOn) {
		this.lastMntOn = lastMntOn;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getNewRecord() {
		return newRecord;
	}

	public void setNewRecord(String newRecord) {
		this.newRecord = newRecord;
	}

	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

	@Override
	public String toString() {
		return "ClassPojo [userAction = " + userAction + ", recordStatus = " + recordStatus + ", phoneNumber = "
				+ phoneNumber + ", lastMntBy = " + lastMntBy + ", lastMntOn = " + lastMntOn + ", priority = " + priority
				+ ", version = " + version + ", phoneTypeCode = " + phoneTypeCode + ", workflowId = " + workflowId
				+ ", newRecord = " + newRecord + ", leadId = " + leadId + "]";
	}
}
