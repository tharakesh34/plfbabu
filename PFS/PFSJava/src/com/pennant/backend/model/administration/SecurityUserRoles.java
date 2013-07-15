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
 * FileName    		: SecurityUserRoles.java                                                   * 	  
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
	private long      roleID;
	private int       version;
	private long      lastMntBy;
	private Timestamp lastMntOn;;
	private String    recordStatus;
	private String    roleCode;  // work flow role code
	private String    nextRoleCode;
	private String    lovDescRoleCd;//role code
	private String    taskId;
	private String    nextTaskId;
	private String    recordType;
	private long      workflowId=0;  
	private String    lovDescUserLogin;
	private LoginUserDetails  userDetails;
	private SecurityUserRoles befImage;
	

	public SecurityUserRoles() {
		super();
	}

	public SecurityUserRoles(long usrRoleId) {
		super();
		this.usrRoleID=usrRoleId;
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

	public void setLovDescUserLogin(String lovDescUserLogin) {
		this.lovDescUserLogin = lovDescUserLogin;
	}
	public String getLovDescUserLogin() {
		return lovDescUserLogin;
	}
	public void setLovDescRoleCd(String lovDescRoleCd) {
		this.lovDescRoleCd = lovDescRoleCd;
	}
	public String getLovDescRoleCd() {
		return lovDescRoleCd;
	}

	public boolean isWorkflow() {
		if (this.workflowId == 0) {
			return false;
		}
		return true;
	}

}
