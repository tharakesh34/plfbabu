package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

public class BulkProcessDetails implements Serializable,Entity {

    private static final long serialVersionUID = 1L;

     
	private String finReference;
	private String finType; 
	private String finCCY;
	private String scheduleMethod;
	private String profitDaysBasis;
	private long custID;
	private String finBranch;
	private String lovDescProductCode;
	private Date lovDescEventFromDate;
	private Date lovDescEventToDate;
	private Date deferedSchdDate;
	private Date reCalStartDate;
	private Date reCalEndDate;
	
	private long bulkProcessId;
	private BigDecimal oldProfitRate = BigDecimal.ZERO;
	private BigDecimal newProfitRate = BigDecimal.ZERO;
	private int profitChange = 0;
	private boolean alwProcess=false;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private BulkProcessDetails befImage;
	private LoginUserDetails userDetails;
	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public BulkProcessDetails() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("BulkProcessDetails");
	}
	
	public BulkProcessDetails(long id) {
		this.setId(id);
	}
	
	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ Getters & Setters +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public String getFinType() {
    	return finType;
    }
	public void setFinType(String finType) {
    	this.finType = finType;
    }
	
	public String getFinCCY() {
    	return finCCY;
    }
	public void setFinCCY(String finCCY) {
    	this.finCCY = finCCY;
    }
	
	public String getScheduleMethod() {
    	return scheduleMethod;
    }
	public void setScheduleMethod(String scheduleMethod) {
    	this.scheduleMethod = scheduleMethod;
    }
	
	public String getProfitDaysBasis() {
    	return profitDaysBasis;
    }
	public void setProfitDaysBasis(String profitDaysBasis) {
    	this.profitDaysBasis = profitDaysBasis;
    }
	
	public long getCustID() {
    	return custID;
    }
	public void setCustID(long custID) {
    	this.custID = custID;
    }
	
	public String getFinBranch() {
    	return finBranch;
    }
	public void setFinBranch(String finBranch) {
    	this.finBranch = finBranch;
    }
	
	 public String getLovDescProductCode() {
    	return lovDescProductCode;
    }
	public void setLovDescProductCode(String lovDescProductCode) {
    	this.lovDescProductCode = lovDescProductCode;
    } 
	
	public Date getLovDescEventFromDate() {
    	return lovDescEventFromDate;
    }
	public void setLovDescEventFromDate(Date lovDescEventFromDate) {
    	this.lovDescEventFromDate = lovDescEventFromDate;
    }
	
	public Date getLovDescEventToDate() {
    	return lovDescEventToDate;
    }
	public void setLovDescEventToDate(Date lovDescEventToDate) {
    	this.lovDescEventToDate = lovDescEventToDate;
    }
	public long getBulkProcessId() {
    	return bulkProcessId;
    }
	public void setBulkProcessId(long bulkProcessId) {
    	this.bulkProcessId = bulkProcessId;
    }
	public BigDecimal getOldProfitRate() {
    	return oldProfitRate;
    }
	public void setOldProfitRate(BigDecimal oldProfitRate) {
    	this.oldProfitRate = oldProfitRate;
    }
	public BigDecimal getNewProfitRate() {
    	return newProfitRate;
    }
	public void setNewProfitRate(BigDecimal newProfitRate) {
    	this.newProfitRate = newProfitRate;
    }
	public int getProfitChange() {
    	return profitChange;
    }
	public void setProfitChange(int profitChange) {
    	this.profitChange = profitChange;
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
	public BulkProcessDetails getBefImage() {
    	return befImage;
    }
	public void setBefImage(BulkProcessDetails befImage) {
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
	public long getId() {
	    return bulkProcessId;
    }
	public void setId(long id) {
	    this.bulkProcessId = id;
    }

	public Date getDeferedSchdDate() {
	    return deferedSchdDate;
    }

	public void setDeferedSchdDate(Date deferedSchdDate) {
	    this.deferedSchdDate = deferedSchdDate;
    }

	public boolean isAlwProcess() {
    	return alwProcess;
    }

	public void setAlwProcess(boolean alwProcess) {
    	this.alwProcess = alwProcess;
    }

	public Date getReCalStartDate() {
    	return reCalStartDate;
    }

	public void setReCalStartDate(Date reCalStartDate) {
    	this.reCalStartDate = reCalStartDate;
    }

	public Date getReCalEndDate() {
    	return reCalEndDate;
    }

	public void setReCalEndDate(Date reCalEndDate) {
    	this.reCalEndDate = reCalEndDate;
    }

}
