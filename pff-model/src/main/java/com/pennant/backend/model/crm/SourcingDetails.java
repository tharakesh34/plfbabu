package com.pennant.backend.model.crm;

public class SourcingDetails {
	private String userAction;

	private String recordStatus;

	private String lastMntBy;

	private String sourcingChannel;

	private String recordType;

	private String aSMName;

	private String offerId;

	private String lastMntOn;

	private String version;

	private String workflowId;

	private String newRecord;

	private String leadId;

	public SourcingDetails() {
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

	public String getSourcingChannel() {
		return sourcingChannel;
	}

	public void setSourcingChannel(String sourcingChannel) {
		this.sourcingChannel = sourcingChannel;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getASMName() {
		return aSMName;
	}

	public void setASMName(String aSMName) {
		this.aSMName = aSMName;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(String lastMntOn) {
		this.lastMntOn = lastMntOn;
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
				+ lastMntBy + ", sourcingChannel = " + sourcingChannel + ", recordType = " + recordType + ", aSMName = "
				+ aSMName + ", offerId = " + offerId + ", lastMntOn = " + lastMntOn + ", version = " + version
				+ ", workflowId = " + workflowId + ", newRecord = " + newRecord + ", leadId = " + leadId + "]";
	}
}