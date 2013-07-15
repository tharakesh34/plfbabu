package com.pennant.backend.model.testing;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.LoginUserDetails;

public class AdditionalFieldValues implements Serializable{
	
	
    private static final long serialVersionUID = 7689821626513321759L;
    
	private String moduleName;
	private String fieldName;
	private String fieldValue;
	private List<AdditionalFieldValues> listOfValues =new ArrayList<AdditionalFieldValues>();
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private LoginUserDetails userDetails;
	
	
	public String getId() {
		return moduleName;
	}
	
	public void setId (String id) {
		this.moduleName = id;
	}
	
	public String getModuleName() {
    	return moduleName;
    }
	public void setModuleName(String moduleName) {
    	this.moduleName = moduleName;
    }
	public String getFieldName() {
    	return fieldName;
    }
	public void setFieldName(String fieldName) {
    	this.fieldName = fieldName;
    }
	public String getFieldValue() {
    	return fieldValue;
    }
	public void setFieldValue(String fieldValue) {
    	this.fieldValue = fieldValue;
    }
	public void setListOfValues(List<AdditionalFieldValues> listOfValues) {
	    this.listOfValues = listOfValues;
    }
	public List<AdditionalFieldValues> getListOfValues() {
	    return listOfValues;
    }
	
	
	
	public String getRecordStatus() {
    	return recordStatus;
    }
	public void setRecordStatus(String recordStatus) {
    	this.recordStatus = recordStatus;
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
	public String getRecordType() {
    	return recordType;
    }
	public void setRecordType(String recordType) {
    	this.recordType = recordType;
    }
	public String getUserAction() {
    	return userAction;
    }
	public void setUserAction(String userAction) {
    	this.userAction = userAction;
    }
	public long getWorkflowId() {
    	return workflowId;
    }
	public void setWorkflowId(long workflowId) {
    	this.workflowId = workflowId;
    }
	public boolean isNewRecord() {
    	return newRecord;
    }
	public void setNewRecord(boolean newRecord) {
    	this.newRecord = newRecord;
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

	public LoginUserDetails getUserDetails() {
    	return userDetails;
    }

	public void setUserDetails(LoginUserDetails userDetails) {
    	this.userDetails = userDetails;
    }
	
}
