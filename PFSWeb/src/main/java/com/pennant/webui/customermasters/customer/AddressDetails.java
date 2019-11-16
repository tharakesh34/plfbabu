package com.pennant.webui.customermasters.customer;

public class AddressDetails {
	private String userAction;

	private String recordStatus;

	private String lastMntBy;

	private String leadAddressId;

	private String pinCode;

	private String lastMntOn;

	private String priority;

	private String version;

	private String workflowId;

	private String newRecord;

	private String leadId;

	private String addrType;

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

	public String getLeadAddressId() {
		return leadAddressId;
	}

	public void setLeadAddressId(String leadAddressId) {
		this.leadAddressId = leadAddressId;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
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

	public String getAddrType() {
		return addrType;
	}

	public void setAddrType(String addrType) {
		this.addrType = addrType;
	}

	@Override
	public String toString() {
		return "ClassPojo [userAction = " + userAction + ", recordStatus = " + recordStatus + ", lastMntBy = "
				+ lastMntBy + ", leadAddressId = " + leadAddressId + ", pinCode = " + pinCode + ", lastMntOn = "
				+ lastMntOn + ", priority = " + priority + ", version = " + version + ", workflowId = " + workflowId
				+ ", newRecord = " + newRecord + ", leadId = " + leadId + ", addrType = " + addrType + "]";
	}
}
