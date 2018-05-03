package com.pennant.backend.model.dashboard;

import java.sql.Timestamp;


public class DetailStatistics implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4399638057132606254L;

	private long auditId = Long.MIN_VALUE;
	private Timestamp auditDate;
	private String moduleName;
	private String roleCode;
	private String AuditReference;
	private long timeInMS;
	private Timestamp lastMntOn;
	private boolean recordStatus;
	
	private String currentRoleCode;
	private String nextRoleCode;

	public DetailStatistics() {
		super();
	}
	
	
	public DetailStatistics(String moduleName, String roleCode,
			String auditReference, long timeInMS, Timestamp lastMntOn,
			boolean recordStatus) {
		super();
		this.moduleName = moduleName;
		this.roleCode = roleCode;
		AuditReference = auditReference;
		this.timeInMS = timeInMS;
		this.lastMntOn = lastMntOn;
		this.recordStatus = recordStatus;
	}


	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getAuditReference() {
		return AuditReference;
	}
	public void setAuditReference(String auditReference) {
		AuditReference = auditReference;
	}
	public long getTimeInMS() {
		return timeInMS;
	}
	public void setTimeInMS(long timeInMS) {
		this.timeInMS = timeInMS;
	}
	
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}
	public boolean isRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(boolean recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	public String getCurrentRoleCode() {
		return currentRoleCode;
	}
	public void setCurrentRoleCode(String currentRoleCode) {
		this.currentRoleCode = currentRoleCode;
	}
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	public long getAuditId() {
		return auditId;
	}
	public void setAuditId(long auditId) {
		this.auditId = auditId;
	}
	public Timestamp getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Timestamp auditDate) {
		this.auditDate = auditDate;
	}
}
