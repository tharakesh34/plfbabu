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
 * FileName    		:  SecurityRole.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
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
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;

/**
 * Model class for the <b>SecurityRole table</b>.<br>
 *
 */
public class SecurityRole implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private long        roleID = Long.MIN_VALUE;
	private long        roleApp;
	private String      roleCd;
	private String       roleDesc;
	private String       roleCategory;
	private int          version;
	private long         lastMntBy;
	private Timestamp    lastMntOn;
	private long         loginUsrId;
	private boolean      newRecord=false;
	private String       lovValue;
	private SecurityRole      befImage;
	private LoginUserDetails userDetails;
	private String       recordStatus;
	private String       roleCode="";
	private String       nextRoleCode= "";
	private String       taskId="";
	private String       nextTaskId= "";
	private String       recordType;
	private String       userAction = "Save";
	private long         workflowId = 0;
	private String       loginAppCode;
	private String       lovDescRoleAppName;
	private List<SecurityGroup> lovDescAllGroups=new ArrayList<SecurityGroup>();
	private List<SecurityRoleGroups> lovDescAssignedGroups=new ArrayList<SecurityRoleGroups>();
	//getters and setters 

	public SecurityRole() {
	}

	public SecurityRole(long roleId) {
		this.roleID=roleId;
	}

	public List<SecurityRoleGroups> getLovDescAssignedGroups() {
		return lovDescAssignedGroups;
	}

	public void setLovDescAssignedGroups(
			List<SecurityRoleGroups> lovDescAssignedGroups) {
		this.lovDescAssignedGroups = lovDescAssignedGroups;
	}

	public List<SecurityGroup> getLovDescAllGroups() {
		return lovDescAllGroups;
	}

	public void setLovDescAllGroups(List<SecurityGroup> lovDescAllGroups) {
		this.lovDescAllGroups = lovDescAllGroups;
	}

	public String getLovDescRoleAppName() {
		return lovDescRoleAppName;
	}

	public void setLovDescRoleAppName(String lovDescRoleAppName) {
		this.lovDescRoleAppName = lovDescRoleAppName;
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

	public SecurityRole getBefImage() {
		return befImage;
	}

	public void setBefImage(SecurityRole befImage) {
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

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE);
	}

	
	public void setId(long id) {
		this.roleID = id;
	}

	public long getId() {
		return roleID;
	}

	public long getRoleID() {
		return roleID;
	}

	public void setRoleID(long roleID) {
		this.roleID = roleID;
	}


	public String getRoleCd() {
		return roleCd;
	}

	public void setRoleCd(String roleCd) {
		this.roleCd = roleCd;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getRoleCategory() {
		return roleCategory;
	}

	public void setRoleCategory(String roleCategory) {
		this.roleCategory = roleCategory;
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

	public int hashCode() {
		return Long.valueOf(getId()).hashCode();
	}

	public boolean equals(SecurityRight secRight) {
		return getId() == secRight.getId();
	}

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

	public long getLoginUsrId() {
		return loginUsrId;
	}
		public long getLoginUsrID() {
		return loginUsrId;
	}

	public void setLoginUsrId(long loginUsrId ) {
		this.loginUsrId = loginUsrId;
	}
	
	public void setLoginUsrID(long loginUsrId) {
		this.loginUsrId = loginUsrId;
	}

	public String getLoginAppCode() {
		return loginAppCode;
	}

	public void setLoginAppCode(String appCode) {
		this.loginAppCode = appCode;
	}

	public long getRoleApp() {
		return roleApp;
	}

	public void setRoleApp(long roleApp) {
		this.roleApp = roleApp;
	}
	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}
}
