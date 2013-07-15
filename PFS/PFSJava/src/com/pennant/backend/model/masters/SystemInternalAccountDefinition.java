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
 * FileName    		:  SystemInternalAccountDefinition.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2011    														*
 *                                                                  						*
 * Modified Date    :  17-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.masters;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>SystemInternalAccountDefinition table</b>.<br>
 *
 */
public class SystemInternalAccountDefinition implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String sIACode = null;
	private String sIAName;
	private String sIAShortName;
	private String sIAAcType;
	private String lovDescSIAAcTypeName;
	private String sIANumber;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private SystemInternalAccountDefinition befImage;
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

	public SystemInternalAccountDefinition() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("SystemInternalAccountDefinition");
	}

	public SystemInternalAccountDefinition(String id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	public String getId() {
		return sIACode;
	}
	
	public void setId (String id) {
		this.sIACode = id;
	}
	
	public String getSIACode() {
		return sIACode;
	}
	public void setSIACode(String sIACode) {
		this.sIACode = sIACode;
	}
	
	
		
	
	public String getSIAName() {
		return sIAName;
	}
	public void setSIAName(String sIAName) {
		this.sIAName = sIAName;
	}
	
	
		
	
	public String getSIAShortName() {
		return sIAShortName;
	}
	public void setSIAShortName(String sIAShortName) {
		this.sIAShortName = sIAShortName;
	}
	
	
		
	
	public String getSIAAcType() {
		return sIAAcType;
	}
	public void setSIAAcType(String sIAAcType) {
		this.sIAAcType = sIAAcType;
	}
	

	public String getLovDescSIAAcTypeName() {
		return this.lovDescSIAAcTypeName;
	}

	public void setLovDescSIAAcTypeName (String lovDescSIAAcTypeName) {
		this.lovDescSIAAcTypeName = lovDescSIAAcTypeName;
	}
	
		
	
	public String getSIANumber() {
		return sIANumber;
	}
	public void setSIANumber(String sIANumber) {
		this.sIANumber = sIANumber;
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

	public SystemInternalAccountDefinition getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(SystemInternalAccountDefinition beforeImage){
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
	public boolean equals(SystemInternalAccountDefinition systemInternalAccountDefinition) {
		return getId() == systemInternalAccountDefinition.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof SystemInternalAccountDefinition) {
			SystemInternalAccountDefinition systemInternalAccountDefinition = (SystemInternalAccountDefinition) obj;
			return equals(systemInternalAccountDefinition);
		}
		return false;
	}
}
