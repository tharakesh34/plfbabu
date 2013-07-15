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

package com.pennant.backend.model.customermasters;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CustomerGroup table</b>.<br>
 *
 */
public class CustomerGroup implements java.io.Serializable, Entity {
	
	private static final long serialVersionUID = 6577918356160899476L;
	
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
	private boolean newRecord=false;
	private String lovValue;
	private CustomerGroup befImage;
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

	public CustomerGroup() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CustomerGroup");
	}

	public CustomerGroup(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return custGrpID;
	}
	public void setId (long id) {
		this.custGrpID = id;
	}
	
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

	public CustomerGroup getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerGroup beforeImage){
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

	// Overridden Equals method to handle the comparison
	public boolean equals(CustomerGroup customerGroup) {
		return getId() == customerGroup.getId();
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

		if (obj instanceof CustomerGroup) {
			CustomerGroup customerGroup = (CustomerGroup) obj;
			return equals(customerGroup);
		}
		return false;
	}
}
