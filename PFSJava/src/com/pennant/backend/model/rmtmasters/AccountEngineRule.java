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
 * FileName    		:  AccountEngineRule.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>AccountEngineRule table</b>.<br>
 * 
 */
public class AccountEngineRule implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 8103277694920797718L;

	private long aERuleId = Long.MIN_VALUE;
	private String aEEvent = null;
	private String lovDescAEEventName;
	private String aERule;
	private String aERuleDesc;
	private boolean aEIsSysDefault;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private AccountEngineRule befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	public AccountEngineRule(String aEEvent) {
		super();
		this.aEEvent = aEEvent;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public AccountEngineRule() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("AccountEngineRule");
	}

	public AccountEngineRule(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getId() {
		return aERuleId;
	}
	public void setId(long id) {
		this.aERuleId = id;
	}

	public long getaERuleId() {
		return aERuleId;
	}
	public void setaERuleId(long aERuleId) {
		this.aERuleId = aERuleId;
	}

	public String getAEEvent() {
		return aEEvent;
	}
	public void setAEEvent(String aEEvent) {
		this.aEEvent = aEEvent;
	}

	public String getLovDescAEEventName() {
		return this.lovDescAEEventName;
	}
	public void setLovDescAEEventName(String lovDescAEEventName) {
		this.lovDescAEEventName = lovDescAEEventName;
	}

	public String getAERule() {
		return aERule;
	}
	public void setAERule(String aERule) {
		this.aERule = aERule;
	}

	public String getAERuleDesc() {
		return aERuleDesc;
	}
	public void setAERuleDesc(String aERuleDesc) {
		this.aERuleDesc = aERuleDesc;
	}

	public boolean isAEIsSysDefault() {
		return aEIsSysDefault;
	}
	public void setAEIsSysDefault(boolean aEIsSysDefault) {
		this.aEIsSysDefault = aEIsSysDefault;
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

	public AccountEngineRule getBefImage() {
		return this.befImage;
	}
	public void setBefImage(AccountEngineRule beforeImage) {
		this.befImage = beforeImage;
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
		if (this.workflowId == 0) {
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
	public boolean equals(AccountEngineRule accountEngineRule) {
		return getId() == accountEngineRule.getId();
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

		if (obj instanceof AccountEngineRule) {
			AccountEngineRule accountEngineRule = (AccountEngineRule) obj;
			return equals(accountEngineRule);
		}
		return false;
	}
	public String getStringaERuleId(){
	return String.valueOf(this.aERuleId);	
	}
	
}
