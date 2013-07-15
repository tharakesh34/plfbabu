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
 *
 * FileName    		:  SEcRight.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.administration;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;

public class SecurityRight implements java.io.Serializable, Entity {

	private static final long serialVersionUID = -1574628715506591010L;

	private long           rightID = Long.MIN_VALUE;
	private int            version;
	private Integer        rightType;
	private String         rightName;
	private long           lastMntBy;
	private Timestamp      lastMntOn;
	private String         loginAppCode;
	private long           loginUsrId;
	private String         loginGrpCode;
	private String         loginRoleCd;
	private boolean        newRecord = false;
	private String         lovValue;
	private SecurityRight       befImage;
	private String         recordStatus;
	private String         roleCode = "";
	private String         nextRoleCode = "";
	private String         taskId = "";
	private String         nextTaskId = "";
	private String         recordType;
	private String         userAction = "Save";
	private long           workflowId = 0;
	private LoginUserDetails userDetails;

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE);
	}

	public SecurityRight() {
	}

	public SecurityRight(long rightId){
		this.rightID=rightId;
	}
	
	public SecurityRight(long rightID, String rightName) {
		this.setId(rightID);
		this.rightName = rightName;
	}

	public SecurityRight(long rightID, Integer rightType, String rightName) {
		this.setRightID(rightID);
		this.rightType = rightType;
		this.rightName = rightName;
	}

	public void setId(long id) {
		this.rightID = id;
	}

	public long getId() {
		return rightID;
	}

	public long getRightID() {
		return rightID;
	}

	public void setRightID(long rightID) {
		this.rightID = rightID;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Integer getRightType() {
		return rightType;
	}

	public void setRightType(Integer rightType) {
		this.rightType = rightType;
	}

	public String getRightName() {
		return rightName;
	}

	public void setRightName(String rightName) {
		this.rightName = rightName;
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

	@Override
	public int hashCode() {
		return Long.valueOf(getId()).hashCode();
	}

	public boolean equals(SecurityRight secRight) {
		return getId() == secRight.getId();
	}

	public String getLoginAppCode() {
		return loginAppCode;
	}

	public void setLoginAppCode(String loginAppCode) {
		this.loginAppCode = loginAppCode;
	}

	public long getLoginUsrId() {
		return loginUsrId;
	}

	public long getUsrID() {
		return loginUsrId;
	}

	public void setLoginUsrId(long loginUsrId) {
		this.loginUsrId = loginUsrId;
	}

	public void setUsrID(long usrID) {
		this.loginUsrId = usrID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof SecurityRight) {
			SecurityRight secRight = (SecurityRight) obj;
			return equals(secRight);
		}

		return false;
	}

	public String getLoginGrpCode() {
		return loginGrpCode;
	}

	public String getGrpCode() {
		return loginGrpCode;
	}

	public void setLoginGrpCode(String loginGrpCode) {
		this.loginGrpCode = loginGrpCode;
	}

	public void setGrpCode(String grpCode) {
		this.loginGrpCode = grpCode;
	}

	public String getLoginRoleCd() {
		return loginRoleCd;
	}

	public String getRoleCd() {
		return loginRoleCd;
	}

	public void setLoginRoleCd(String loginRoleCd) {
		this.loginRoleCd = loginRoleCd;
	}

	public void setRoleCd(String roleCd) {
		this.loginRoleCd = roleCd;
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

	public SecurityRight getBefImage() {
		return befImage;
	}

	public void setBefImage(SecurityRight befImage) {
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

}
