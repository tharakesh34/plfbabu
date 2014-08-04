package com.pennant.backend.model.rmtmasters;

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
 * FileName    		:  FinTypeAccount.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoginUserDetails;

/**
 * Model class for the <b>Collateral table</b>.<br>
 *
 */
public class FinTypeAccount implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String finType = null;
	private String finCcy;
	private String finCcyName;
	private int finFormatter;
	private String event;
	private boolean alwManualEntry;
	private boolean alwCustomerAccount;
	private String accountReceivable;
	private String custAccountTypes;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinTypeAccount befImage;
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

	public FinTypeAccount() {
		//this.workflowId = WorkFlowUtil.getWorkFlowID("FinTypeAccount");
	}

	public FinTypeAccount(String id) {
		this.setId(id);
	}
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("finCcyName");
		excludeFields.add("finFormatter");
		return excludeFields;
	}
	//Getter and Setter methods
	
	public String getId() {
		return finType;
	}
	
	public void setId (String id) {
		this.finType = id;
	}
		
	
	public String getFinType() {
    	return finType;
    }

	public void setFinType(String finType) {
    	this.finType = finType;
    }

	public String getFinCcy() {
    	return finCcy;
    }

	public void setFinCcy(String finCcy) {
    	this.finCcy = finCcy;
    }

	public String getFinCcyName() {
	    return finCcyName;
    }

	public void setFinCcyName(String finCcyName) {
	    this.finCcyName = finCcyName;
    }

	public int getFinFormatter() {
	    return finFormatter;
    }

	public void setFinFormatter(int finFormatter) {
	    this.finFormatter = finFormatter;
    }

	public String getEvent() {
    	return event;
    }

	public void setEvent(String event) {
    	this.event = event;
    }

	public boolean isAlwManualEntry() {
    	return alwManualEntry;
    }

	public void setAlwManualEntry(boolean alwManualEntry) {
    	this.alwManualEntry = alwManualEntry;
    }

	public boolean isAlwCustomerAccount() {
    	return alwCustomerAccount;
    }

	public void setAlwCustomerAccount(boolean alwCustomerAccount) {
    	this.alwCustomerAccount = alwCustomerAccount;
    }

	public String getAccountReceivable() {
    	return accountReceivable;
    }

	public void setAccountReceivable(String accountReceivable) {
    	this.accountReceivable = accountReceivable;
    }

	public String getCustAccountTypes() {
    	return custAccountTypes;
    }

	public void setCustAccountTypes(String custAccountTypes) {
    	this.custAccountTypes = custAccountTypes;
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

	public FinTypeAccount getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(FinTypeAccount beforeImage){
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

	// Overidden Equals method to handle the comparision
	public boolean equals(FinTypeAccount finTypeAccount) {
		return getId() == finTypeAccount.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof FinTypeAccount) {
			FinTypeAccount finTypeAccount = (FinTypeAccount) obj;
			return equals(finTypeAccount);
		}
		return false;
	}
	
}

