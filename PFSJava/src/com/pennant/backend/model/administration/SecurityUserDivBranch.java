package com.pennant.backend.model.administration;

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
 * FileName    		:  SecurityUserDivBranch.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>SecurityUserDivBranch table</b>.<br>
 *
 */
public class SecurityUserDivBranch implements java.io.Serializable {

	private static final long serialVersionUID = -306657295035931426L;

	private long  usrID = Long.MIN_VALUE;
	private String    userDivision;
	private String    userBranch;
	private String    userBranchDesc;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private String lovDescPriKey;
	private SecurityUserDivBranch befImage;
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
		return (getUsrID() == Long.MIN_VALUE);
		//return isNewRecord();
	}

	public SecurityUserDivBranch() {
	//	this.workflowId = WorkFlowUtil.getWorkFlowID("SecurityUserDivBranch");
	}

	public SecurityUserDivBranch(long id) {
		this.setId(id);
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("userBranchDesc");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setId(long id) {
		this.usrID = id;
	}
	public long getId() {
		return usrID;
	}

	public void setUsrID(long usrID) {
		this.usrID = usrID;
	}
	public long getUsrID() {
		return usrID;
	}
	public String getUserDivision() {
		return userDivision;
	}
	public void setUserDivision(String userDivision) {
		this.userDivision = userDivision;
	}
	public String getUserBranch() {
		return userBranch;
	}
	public void setUserBranch(String userBranch) {
		this.userBranch = userBranch;
	}
	public void setUserBranchDesc(String userBranchDesc) {
	    this.userBranchDesc = userBranchDesc;
    }

	public String getUserBranchDesc() {
	    return userBranchDesc;
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

	public SecurityUserDivBranch getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(SecurityUserDivBranch beforeImage){
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

	public String getLovDescPriKey() {
	    return lovDescPriKey;
    }

	public void setLovDescPriKey(String lovDescPriKey) {
	    this.lovDescPriKey = lovDescPriKey;
    }

	// Overridden Equals method to handle the comparison
	public boolean equals(SecurityUserDivBranch securityUserDivBranch) {
		return getUsrID() == securityUserDivBranch.getUsrID();
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
		if (obj instanceof SecurityUserDivBranch) {
			SecurityUserDivBranch securityUserDivBranch = (SecurityUserDivBranch) obj;
			return equals(securityUserDivBranch);
		}
		return false;
	}

}
