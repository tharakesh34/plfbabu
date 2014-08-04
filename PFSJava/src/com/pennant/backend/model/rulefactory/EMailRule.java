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
 * FileName    		:  EmailRule.java                           
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

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;

/**
 * Model class for the <b>Rule table</b>.<br>
 *
 */
public class EMailRule implements java.io.Serializable ,Entity{

	private static final long serialVersionUID = 522289325946000330L;

	private long ruleId=Long.MIN_VALUE;
	private String ruleCode = null;
	private String ruleModule;
	private String lovDescRuleModuleName;
	private String ruleEvent;
	private String ruleCodeDesc;
	private String sQLRule;
	private String actualBlock;
	private String returnType;
	private RuleModule ruleModuleObj;
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private EMailRule befImage;
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

	public EMailRule() {
		//this.workflowId = WorkFlowUtil.getWorkFlowID("Rule");
	}

	public EMailRule(String id) {
		this.setRuleCode(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return ruleId;
	}
	public void setId(long id) {
		this.ruleId=id;
	}
	
	public String getRuleCode() {
		return ruleCode;
	}
	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
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
	
	public String getLovDescRuleModuleName() {
		return lovDescRuleModuleName;
	}
	public void setLovDescRuleModuleName(String lovDescRuleModuleName) {
		this.lovDescRuleModuleName = lovDescRuleModuleName;
	}

	public String getRuleEvent() {
		return ruleEvent;
	}
	public void setRuleEvent(String ruleEvent) {
		this.ruleEvent = ruleEvent;
	}

	public String getRuleCodeDesc() {
		return ruleCodeDesc;
	}
	public void setRuleCodeDesc(String ruleCodeDesc) {
		this.ruleCodeDesc = ruleCodeDesc;
	}
	
	public String getSQLRule() {
		return sQLRule;
	}
	public void setSQLRule(String sQLRule) {
		this.sQLRule = sQLRule;
	}
	
	public String getActualBlock() {
		return actualBlock;
	}
	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
	}
	
	public void setReturnType(String returnType) {
	    this.returnType = returnType;
    }
	public String getReturnType() {
	    return returnType;
    }

	public RuleModule getRuleModuleObj() {
		this.ruleModuleObj = null;
		this.ruleModuleObj = new RuleModule();
		ruleModuleObj.setRbmModule(this.ruleModule);
		//ruleModuleObj.setRuleModuleDesc(this.ruleModuleDesc);
		//ruleModuleObj.setTableName(this.tableName);
    	return ruleModuleObj;
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

	public EMailRule getBefImage(){
		return this.befImage;
	}
	public void setBefImage(EMailRule beforeImage){
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
	public boolean equals(Rule rule) {
		return getId() == rule.getId();
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

		if (obj instanceof Rule) {
			Rule rule = (Rule) obj;
			return equals(rule);
		}
		return false;
	}


}