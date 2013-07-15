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
 * FileName    		:  SecurityRoleGroups.java												*                           
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
 */package com.pennant.backend.model.administration;

 import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;

 public class SecurityRoleGroups implements java.io.Serializable,Entity{


	 private static final long serialVersionUID = 1L;
	 private long        roleGrpID=Long.MIN_VALUE;
	 private long        grpID;
	 private long        roleID;
	 private int         version;
	 private long        lastMntBy;
	 private Timestamp   lastMntOn;
	 private String      recordStatus;
	 private String      roleCode;
	 private String      nextRoleCode;
	 private String      taskId;
	 private String      nextTaskId;
	 private String      recordType;
	 private long      workflowId=0;
	 private String      lovDescGrpDesc;
	 private String      lovDescGrpCode;
	 private String      lovDescRoleCode;
	 private LoginUserDetails    userDetails;
	 private SecurityRoleGroups befImage;



	 public SecurityRoleGroups() {
		super();
	}
	public SecurityRoleGroups(long roleGrpId) {
		super();
		this.roleGrpID = roleGrpId;
	}
	
	public long getRoleGrpID() {
		 return roleGrpID;
	 }
	 public void setRoleGrpID(long roleGrpID) {
		 this.roleGrpID = roleGrpID;
	 }
	 public long getGrpID() {
		 return grpID;
	 }

	 public void  setGrpID(long grpID) {
		 this.grpID = grpID;
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
	 public String getLovDescGrpDesc() {
		 return lovDescGrpDesc;
	 }
	 public void setLovDescGrpDesc(String lovDescGrpDesc) {
		 this.lovDescGrpDesc = lovDescGrpDesc;
	 }
	 public String getLovDescGrpCode() {
		 return lovDescGrpCode;
	 }
	 public void setLovDescGrpCode(String lovDescGrpCode) {
		 this.lovDescGrpCode = lovDescGrpCode;
	 }
	 public void setUserDetails(LoginUserDetails userDetails) {
		 this.userDetails = userDetails;
	 }
	 public LoginUserDetails getUserDetails() {
		 return userDetails;
	 }
	 public void setBefImage(SecurityRoleGroups befImage) {
		 this.befImage = befImage;
	 }
	 public SecurityRoleGroups getBefImage() {
		 return befImage;
	 }
	 @Override
	 public boolean isNew() {

		 return false;
	 }
	 @Override
	 public void  setId(long roleGrpID){
		 this.roleGrpID = roleGrpID;

	 }
	 @Override
	 public long getId() {
		 return roleGrpID;
	 }
	 public void setLovDescRoleCode(String lovDescRoleCode) {
		 this.lovDescRoleCode = lovDescRoleCode;
	 }
	 public String getLovDescRoleCode() {
		 return lovDescRoleCode;
	 }

		public boolean isWorkflow() {
			if (this.workflowId == 0) {
				return false;
			}
			return true;
		}

 }
