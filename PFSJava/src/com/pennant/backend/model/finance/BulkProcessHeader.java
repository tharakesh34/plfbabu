package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;

public class BulkProcessHeader implements Serializable,Entity {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	private long bulkProcessId= Long.MIN_VALUE;
	private Date fromDate;
	private Date toDate;
	private Date reCalFromDate;
	private Date reCalToDate;
	private boolean excludeDeferement = false;
	private String addTermAfter;
	private BigDecimal newProcessedRate;
	private String reCalType;
	private String lovDescReCalType="";
	private String bulkProcessFor; 
	private String ruleType;
	private String lovDescSqlQuery;
	private boolean lovDescIsOlddataChanged;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private BulkProcessHeader befImage;
	private LoginUserDetails userDetails;
	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private List<BulkProcessDetails> bulkProcessDetailsList;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();


	public boolean isNew() {
		return isNewRecord();
	}

	public BulkProcessHeader() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("BulkProcessHeader");
	}
	
	public BulkProcessHeader(long id) {
		this.setId(id);
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getBulkProcessId() {
    	return bulkProcessId;
    }
	public void setBulkProcessId(long bulkProcessId) {
    	this.bulkProcessId = bulkProcessId;
    }
	public Date getFromDate() {
    	return fromDate;
    }
	public void setFromDate(Date fromDate) {
    	this.fromDate = fromDate;
    }
	public Date getToDate() {
    	return toDate;
    }
	public void setToDate(Date toDate) {
    	this.toDate = toDate;
    }
	public BigDecimal getNewProcessedRate() {
    	return newProcessedRate;
    }
	public void setNewProcessedRate(BigDecimal newProcessedRate) {
    	this.newProcessedRate = newProcessedRate;
    }
	public String getReCalType() {
    	return reCalType;
    }
	public void setReCalType(String reCalType) {
    	this.reCalType = reCalType;
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
	public BulkProcessHeader getBefImage() {
    	return befImage;
    }
	public void setBefImage(BulkProcessHeader befImage) {
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
	
	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}

	public List<BulkProcessDetails> getBulkProcessDetailsList() {
    	return bulkProcessDetailsList;
    }

	public void setBulkProcessDetailsList(List<BulkProcessDetails> lovDescBulkProcessDetails) {
    	this.bulkProcessDetailsList = lovDescBulkProcessDetails;
    }

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
	    return auditDetailMap;
    }

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
	    this.auditDetailMap = auditDetailMap;
    }

	public String getBulkProcessFor() {
	    return bulkProcessFor;
    }

	public void setBulkProcessFor(String bulkProcessFor) {
	    this.bulkProcessFor = bulkProcessFor;
    }

	public Date getReCalFromDate() {
	    return reCalFromDate;
    }

	public void setReCalFromDate(Date reCalFromDate) {
	    this.reCalFromDate = reCalFromDate;
    }

	public Date getReCalToDate() {
	    return reCalToDate;
    }

	public void setReCalToDate(Date reCalToDate) {
	    this.reCalToDate = reCalToDate;
    }

	public boolean isExcludeDeferement() {
	    return excludeDeferement;
    }

	public void setExcludeDeferement(boolean excludeDeferement) {
	    this.excludeDeferement = excludeDeferement;
    }

	public String getAddTermAfter() {
	    return addTermAfter;
    }

	public void setAddTermAfter(String addTermAfter) {
	    this.addTermAfter = addTermAfter;
    }

	public String getRuleType() {
	    return ruleType;
    }

	public void setRuleType(String ruleType) {
	    this.ruleType = ruleType;
    }

	public String getLovDescSqlQuery() {
	    return lovDescSqlQuery;
    }

	public void setLovDescSqlQuery(String lovDescSqlQuery) {
	    this.lovDescSqlQuery = lovDescSqlQuery;
    }

	public boolean isLovDescIsOlddataChanged() {
    	return lovDescIsOlddataChanged;
    }

	public void setLovDescIsOlddataChanged(boolean lovDescIsOlddataChanged) {
    	this.lovDescIsOlddataChanged = lovDescIsOlddataChanged;
    }

	public String getLovDescReCalType() {
		return lovDescReCalType;
	}
	public void setLovDescReCalType(String lovDescReCalType) {
		this.lovDescReCalType = lovDescReCalType;
	}

}
