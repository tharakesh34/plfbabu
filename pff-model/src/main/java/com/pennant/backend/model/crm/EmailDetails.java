package com.pennant.backend.model.crm;

public class EmailDetails {
	private String userAction;

	private String recordStatus;

	private String lastMntBy;

	private String emailId;

	private String lastMntOn;

	private String priority;

	private String version;

	private String workflowId;

	private String emailTypeCode;

	private String newRecord;

	private String leadId;

	public EmailDetails() {
		super();
	}

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

	public String getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(String lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getEmailTypeCode() {
		return emailTypeCode;
	}

	public void setEmailTypeCode(String emailTypeCode) {
		this.emailTypeCode = emailTypeCode;
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
		return "ClassPojo [userAction = " + userAction + ", recordStatus = " + recordStatus + ", lastMntBy = "
				+ lastMntBy + ", emailId = " + emailId + ", lastMntOn = " + lastMntOn + ", priority = " + priority
				+ ", version = " + version + ", workflowId = " + workflowId + ", emailTypeCode = " + emailTypeCode
				+ ", newRecord = " + newRecord + ", leadId = " + leadId + "]";
	}
}
