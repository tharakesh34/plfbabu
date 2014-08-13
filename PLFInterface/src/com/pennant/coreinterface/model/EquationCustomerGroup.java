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
 * FileName    		:  CustomerGroup.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>CustomerGroup table</b>.<br>
 *
 */
public class EquationCustomerGroup {
	
	private long custGrpID = Long.MIN_VALUE;
	private String custGrpCode;
	private String custGrpDesc;
	private String custGrpRO1;
	private String lovDescCustGrpRO1Name;
	private long custGrpLimit;
	private boolean custGrpIsActive;
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


	public long getCustGrpID() {
		return custGrpID;
	}
	public void setCustGrpID(long custGrpID) {
		this.custGrpID = custGrpID;
	}
	
	public String getCustGrpCode() {
		return custGrpCode;
	}
	public void setCustGrpCode(String custGrpCode) {
		this.custGrpCode = custGrpCode;
	}
	
	public String getCustGrpDesc() {
		return custGrpDesc;
	}
	public void setCustGrpDesc(String custGrpDesc) {
		this.custGrpDesc = custGrpDesc;
	}
	
	public String getCustGrpRO1() {
		return custGrpRO1;
	}
	public void setCustGrpRO1(String custGrpRO1) {
		this.custGrpRO1 = custGrpRO1;
	}

	public String getLovDescCustGrpRO1Name() {
		return lovDescCustGrpRO1Name;
	}
	public void setLovDescCustGrpRO1Name(String lovDescCustGrpRO1Name) {
		this.lovDescCustGrpRO1Name = lovDescCustGrpRO1Name;
	}

	public long getCustGrpLimit() {
		return custGrpLimit;
	}
	public void setCustGrpLimit(long custGrpLimit) {
		this.custGrpLimit = custGrpLimit;
	}

	public boolean isCustGrpIsActive() {
		return custGrpIsActive;
	}
	public void setCustGrpIsActive(boolean custGrpIsActive) {
		this.custGrpIsActive = custGrpIsActive;
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
