/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  AccountingSet.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.model.rmtmasters;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>AccountingSet table</b>.<br>
 *
 */
public class AccountingSet implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 3832850641524383002L;
	
	private long accountSetid = Long.MIN_VALUE;
	private String eventCode;
	private String lovDescEventCodeName;
	private String accountSetCode;
	private String accountSetCodeName;
	private boolean entryByInvestment;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private AccountingSet befImage;
	private LoginUserDetails userDetails;
	private boolean SystemDefault=false;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private List<TransactionEntry> lovDescTransactionEntries=new ArrayList<TransactionEntry>();
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public AccountingSet() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("AccountingSet");
	}

	public AccountingSet(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return accountSetid;
	}
	public void setId (long id) {
		this.accountSetid = id;
	}
	
	public long getAccountSetid() {
		return accountSetid;
	}
	public void setAccountSetid(long accountSetid) {
		this.accountSetid = accountSetid;
	}
	
	public String getEventCode() {
		return eventCode;
	}
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getLovDescEventCodeName() {
		return this.lovDescEventCodeName;
	}
	public void setLovDescEventCodeName (String lovDescEventCodeName) {
		this.lovDescEventCodeName = lovDescEventCodeName;
	}
	
	public String getAccountSetCode() {
		return accountSetCode;
	}
	public void setAccountSetCode(String accountSetCode) {
		this.accountSetCode = accountSetCode;
	}
	
	public String getAccountSetCodeName() {
		return accountSetCodeName;
	}
	public void setAccountSetCodeName(String accountSetCodeName) {
		this.accountSetCodeName = accountSetCodeName;
	}

	public void setEntryByInvestment(boolean entryByInvestment) {
	    this.entryByInvestment = entryByInvestment;
    }

	public boolean isEntryByInvestment() {
	    return entryByInvestment;
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
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
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

	public AccountingSet getBefImage(){
		return this.befImage;
	}
	public void setBefImage(AccountingSet beforeImage){
		this.befImage=beforeImage;
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

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}
	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(AccountingSet accountingSet) {
		return getId() == accountingSet.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof AccountingSet) {
			AccountingSet accountingSet = (AccountingSet) obj;
			return equals(accountingSet);
		}
		return false;
	}

	public void setTransactionEntries(List<TransactionEntry> transactionEntries) {
		//this.lovDescTransactionEntries.clear();
		this.lovDescTransactionEntries = transactionEntries;
	}
	public List<TransactionEntry> getTransactionEntries() {
		return lovDescTransactionEntries;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}
	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public boolean isSystemDefault() {
		return SystemDefault;
	}
	public void setSystemDefault(boolean systemDefault) {
		SystemDefault = systemDefault;
	}
	
	public String getStringaERuleId(){
		return String.valueOf(this.accountSetid);
	}
	
}
