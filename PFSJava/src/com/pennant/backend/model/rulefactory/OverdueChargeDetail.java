package com.pennant.backend.model.rulefactory;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;



public class OverdueChargeDetail implements java.io.Serializable{
	
    private static final long serialVersionUID = -1032001125987199804L;
    
	private String oDCRuleCode = null;
	private String oDCCustCtg;
	private String lovDescODCCustCtgName;
	private String oDCType;
	private String lovDescODCTypeName;
	private String oDCOn;
	private BigDecimal oDCAmount;
	private int oDCGraceDays;
	private boolean oDCAllowWaiver;
	private BigDecimal oDCMaxWaiver;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private OverdueChargeDetail befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public boolean isNew() {
		return isNewRecord();
	}
	
	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	} 
	
	public String getoDCRuleCode() {
    	return oDCRuleCode;
    }
	public void setoDCRuleCode(String oDCRuleCode) {
    	this.oDCRuleCode = oDCRuleCode;
    }
	
	public String getoDCCustCtg() {
    	return oDCCustCtg;
    }
	public void setoDCCustCtg(String oDCCustCtg) {
    	this.oDCCustCtg = oDCCustCtg;
    }
	
	public String getLovDescODCCustCtgName() {
    	return lovDescODCCustCtgName;
    }
	public void setLovDescODCCustCtgName(String lovDescODCCustCtgName) {
    	this.lovDescODCCustCtgName = lovDescODCCustCtgName;
    }
	
	public String getoDCType() {
    	return oDCType;
    }
	public void setoDCType(String oDCType) {
    	this.oDCType = oDCType;
    }
	
	public String getLovDescODCTypeName() {
    	return lovDescODCTypeName;
    }
	public void setLovDescODCTypeName(String lovDescODCTypeName) {
    	this.lovDescODCTypeName = lovDescODCTypeName;
    }
	
	public String getoDCOn() {
    	return oDCOn;
    }
	public void setoDCOn(String oDCOn) {
    	this.oDCOn = oDCOn;
    }
	
	public BigDecimal getoDCAmount() {
    	return oDCAmount;
    }
	public void setoDCAmount(BigDecimal oDCAmount) {
    	this.oDCAmount = oDCAmount;
    }
	
	public int getoDCGraceDays() {
    	return oDCGraceDays;
    }
	public void setoDCGraceDays(int oDCGraceDays) {
    	this.oDCGraceDays = oDCGraceDays;
    }
	
	public boolean isoDCAllowWaiver() {
    	return oDCAllowWaiver;
    }
	public void setoDCAllowWaiver(boolean oDCAllowWaiver) {
    	this.oDCAllowWaiver = oDCAllowWaiver;
    }
	
	public BigDecimal getoDCMaxWaiver() {
    	return oDCMaxWaiver;
    }
	public void setoDCMaxWaiver(BigDecimal oDCMaxWaiver) {
    	this.oDCMaxWaiver = oDCMaxWaiver;
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
	
	public boolean isNewRecord() {
    	return newRecord;
    }
	public void setNewRecord(boolean newRecord) {
    	this.newRecord = newRecord;
    }
	
	public String getLovValue() {
    	return lovValue;
    }
	public void setLovValue(String lovValue) {
    	this.lovValue = lovValue;
    }
	
	public OverdueChargeDetail getBefImage() {
    	return befImage;
    }
	public void setBefImage(OverdueChargeDetail befImage) {
    	this.befImage = befImage;
    }
	
	public LoginUserDetails getUserDetails() {
    	return userDetails;
    }
	public void setUserDetails(LoginUserDetails userDetails) {
    	this.userDetails = userDetails;
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
	
	public String getId() {
	    return oDCRuleCode;
    }
	public void setId(String id) {
		this.oDCRuleCode = id;
	    
    }

}
