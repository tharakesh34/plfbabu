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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  Notifications.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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

package com.pennant.backend.model.rulefactory;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Notifications table</b>.<br>
 *
 */
public class Notifications implements java.io.Serializable{

	private static final long serialVersionUID = 522289325946000330L;

	private String ruleCode = null;
	private String ruleModule;
	private String ruleCodeDesc;
	private String ruleTemplate;
	private String actualBlockTemplate;
	private String ruleReciepent;
	private String actualBlockReciepent;
	private String ruleAttachment;
	private String actualBlockAtachment;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private Notifications befImage;
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

	public Notifications() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Notifications");
	}

	public Notifications(String id) {
		this.setRuleCode(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getId() {
		return ruleCode;
	}
	public void setId(String ruleCode) {
		this.ruleCode = ruleCode;
	}
	
	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}
	
	public String getRuleModule() {
		return ruleModule;
	}
	public void setRuleModule(String ruleModule) {
		this.ruleModule = ruleModule;
	}

	public String getRuleCodeDesc() {
		return ruleCodeDesc;
	}
	public void setRuleCodeDesc(String ruleCodeDesc) {
		this.ruleCodeDesc = ruleCodeDesc;
	}
	
	public String getRuleTemplate() {
	    return ruleTemplate;
    }

	public void setRuleTemplate(String ruleTemplate) {
	    this.ruleTemplate = ruleTemplate;
    }

	public String getActualBlockTemplate() {
	    return actualBlockTemplate;
    }

	public void setActualBlockTemplate(String actualBlockTemplate) {
	    this.actualBlockTemplate = actualBlockTemplate;
    }

	public String getRuleReciepent() {
	    return ruleReciepent;
    }

	public void setRuleReciepent(String ruleReciepent) {
	    this.ruleReciepent = ruleReciepent;
    }

	public String getActualBlockReciepent() {
	    return actualBlockReciepent;
    }

	public void setActualBlockReciepent(String actualBlockReciepent) {
	    this.actualBlockReciepent = actualBlockReciepent;
    }

	public String getRuleAttachment() {
	    return ruleAttachment;
    }

	public void setRuleAttachment(String ruleAttachment) {
	    this.ruleAttachment = ruleAttachment;
    }

	public String getActualBlockAtachment() {
	    return actualBlockAtachment;
    }

	public void setActualBlockAtachment(String actualBlockAtachment) {
	    this.actualBlockAtachment = actualBlockAtachment;
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

	public Notifications getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Notifications beforeImage){
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
	public boolean equals(Notifications notifications) {
		return getId() == notifications.getId();
	}

	/**
	 * Check object is equal or not with Other object
	 * 
	 *  @return boolean
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Notifications) {
			Notifications notifications = (Notifications) obj;
			return equals(notifications);
		}
		return false;
	}


}