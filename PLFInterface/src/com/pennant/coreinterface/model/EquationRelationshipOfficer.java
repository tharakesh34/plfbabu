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
 * FileName    		:  RelationshipOfficer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.coreinterface.model;

import java.sql.Timestamp;

/**
 * Model class for the <b>RelationshipOfficer table</b>.<br>
 *
 */
public class EquationRelationshipOfficer{
	
	private String rOfficerCode = null;
	private String rOfficerDesc;
	private String rOfficerDeptCode;
	private String lovDescROfficerDeptCodeName;
	private boolean rOfficerIsActive;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private long workflowId = 0;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getROfficerCode() {
		return rOfficerCode;
	}
	public void setROfficerCode(String rOfficerCode) {
		this.rOfficerCode = rOfficerCode;
	}
	
	public String getROfficerDesc() {
		return rOfficerDesc;
	}
	public void setROfficerDesc(String rOfficerDesc) {
		this.rOfficerDesc = rOfficerDesc;
	}
		
	public String getROfficerDeptCode() {
		return rOfficerDeptCode;
	}
	public void setROfficerDeptCode(String rOfficerDeptCode) {
		this.rOfficerDeptCode = rOfficerDeptCode;
	}

	public String getLovDescROfficerDeptCodeName() {
		return this.lovDescROfficerDeptCodeName;
	}
	public void setLovDescROfficerDeptCodeName (String lovDescROfficerDeptCodeName) {
		this.lovDescROfficerDeptCodeName = lovDescROfficerDeptCodeName;
	}
	
	public boolean isROfficerIsActive() {
		return rOfficerIsActive;
	}
	public void setROfficerIsActive(boolean rOfficerIsActive) {
		this.rOfficerIsActive = rOfficerIsActive;
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

}
