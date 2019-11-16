package com.pennant.webui.customermasters.customer;

public class CampaignDetails {
	private String userAction;

	private String utmSource;

	private String recordStatus;

	private String lastMntBy;

	private String recordType;

	private String offerId;

	private String lastMntOn;

	private String version;

	private String workflowId;

	private String newRecord;

	private String leadId;

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public String getUtmSource() {
		return utmSource;
	}

	public void setUtmSource(String utmSource) {
		this.utmSource = utmSource;
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

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
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
		return "ClassPojo [userAction = " + userAction + ", utmSource = " + utmSource + ", recordStatus = "
				+ recordStatus + ", lastMntBy = " + lastMntBy + ", recordType = " + recordType + ", offerId = "
				+ offerId + ", lastMntOn = " + lastMntOn + ", version = " + version + ", workflowId = " + workflowId
				+ ", newRecord = " + newRecord + ", leadId = " + leadId + "]";
	}
}