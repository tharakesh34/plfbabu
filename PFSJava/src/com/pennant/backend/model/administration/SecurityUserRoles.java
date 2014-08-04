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
 * FileName    		: SecurityUserRoles.java                                                * 	  
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
 * 27-05-2011       Pennant	                 0.1                                            * 
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

public class SecurityUserRoles implements java.io.Serializable,Entity{

	private static final long serialVersionUID = 3894711431224067298L;
	private long      usrRoleID=Long.MIN_VALUE;
	private long      usrID;
	private String    lovDescFirstName;
	private String 	  lovDescMiddleName;
	private String    lovDescLastName;
	
	private long      roleID;
	private String    lovDescRoleCd;//role code
	private String    lovDescRoleDesc;
	private int       version;
	private long      lastMntBy;
	
	private Timestamp lastMntOn;;
	private String    recordStatus;
	private String    roleCode;  // work flow role code
	private String    nextRoleCode;
	
	private String    taskId;
	private String    nextTaskId;
	private String    recordType;
	private long      workflowId=0;  
	
	private LoginUserDetails  userDetails;
	private SecurityUserRoles befImage;
	private boolean newRecord=false;
	
	private String lovDescUsrFName;
	private String lovDescUsrMName;
	private String lovDescUsrLName;
	private String lovDescUserLogin;
	
	public SecurityUserRoles() {
		super();
	}

	public SecurityUserRoles(long usrRoleID) {
		super();
		this.usrRoleID=usrRoleID;
	}

	public long getUsrRoleID() {
		return usrRoleID;
	}
	public void setUsrRoleID(long usrRoleID) {
		this.usrRoleID = usrRoleID;
	}
	public long getUsrID() {
		return usrID;
	}
	public void setUsrID(long usrID) {
		this.usrID = usrID;
	}
	public long getRoleID() {
		return roleID;
	}
	public void setRoleID(long roleID) {
		this.roleID = roleID;
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
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}
	public SecurityUserRoles getBefImage() {
		return befImage;
	}
	public void setBefImage(SecurityUserRoles befImage) {
		this.befImage = befImage;
	}
	@Override
	public long getId() {
		return 	usrRoleID;

	}
	@Override
	public void setId(long usrRoleID) {

		this.usrRoleID=usrRoleID;
	}
	@Override
	public boolean isNew() {
		return false;
	}

	public String getLovDescFirstName() {
		return lovDescFirstName;
	}

	public void setLovDescFirstName(String lovDescFirstName) {
		this.lovDescFirstName = lovDescFirstName;
	}


	public String getLovDescMiddleName() {
		return lovDescMiddleName;
	}

	public void setLovDescMiddleName(String lovDescMiddleName) {
		this.lovDescMiddleName = lovDescMiddleName;
	}

	public String getLovDescLastName() {
		return lovDescLastName;
	}

	public void setLovDescLastName(String lovDescLastName) {
		this.lovDescLastName = lovDescLastName;
	}


	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public String getLovDescUsrFName() {
		return lovDescUsrFName;
	}

	public void setLovDescUsrFName(String lovDescUsrFName) {
		this.lovDescUsrFName = lovDescUsrFName;
	}

	public String getLovDescUsrMName() {
		return lovDescUsrMName;
	}

	public void setLovDescUsrMName(String lovDescUsrMName) {
		this.lovDescUsrMName = lovDescUsrMName;
	}

	public String getLovDescUsrLName() {
		return lovDescUsrLName;
	}
	public void setLovDescUsrLName(String lovDescUsrLName) {
		this.lovDescUsrLName = lovDescUsrLName;
	}

	public String getLovDescRoleCd() {
		return lovDescRoleCd;
	}

	public void setLovDescRoleCd(String lovDescRoleCd) {
		this.lovDescRoleCd = lovDescRoleCd;
	}

	public String getLovDescRoleDesc() {
		return lovDescRoleDesc;
	}

	public void setLovDescRoleDesc(String lovDescRoleDesc) {
		this.lovDescRoleDesc = lovDescRoleDesc;
	}

	public String getLovDescUserLogin() {
	    return lovDescUserLogin;
    }

	public void setLovDescUserLogin(String lovDescUserLogin) {
	    this.lovDescUserLogin = lovDescUserLogin;
    }
}
