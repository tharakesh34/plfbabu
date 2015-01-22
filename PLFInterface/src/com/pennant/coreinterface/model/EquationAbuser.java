package com.pennant.coreinterface.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class EquationAbuser implements Serializable { 

    private static final long serialVersionUID = -7305078754814792618L;
    
	private String abuserIDType;
	private String abuserIDNumber;
	private Date abuserExpDate;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private String recordStatus;
	private String recordType;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private long workflowId = 0;
	
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getAbuserIDType() {
    	return abuserIDType;
    }
	public void setAbuserIDType(String abuserIDType) {
    	this.abuserIDType = abuserIDType;
    }
	
	public String getAbuserIDNumber() {
    	return abuserIDNumber;
    }
	public void setAbuserIDNumber(String abuserIDNumber) {
    	this.abuserIDNumber = abuserIDNumber;
    }
	
	public Date getAbuserExpDate() {
		return abuserExpDate;
	}
	public void setAbuserExpDate(Date abuserExpDate) {
		this.abuserExpDate = abuserExpDate;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public long getLastMntBy() {
		return lastMntBy;
	}
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}
	
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}
	public void setLastMntOn(Timestamp lastMntOn) {
		this.lastMntOn = lastMntOn;
	}
	
	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getNextTaskId() {
		return nextTaskId;
	}
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
}
