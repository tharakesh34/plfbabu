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
 * FileName    		:  CustomerNotesType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CustomerNotesType table</b>.<br>
 *
 */
public class CustomerNotesType implements java.io.Serializable {

	private static final long serialVersionUID = -4184815336367307646L;
	
	private String custNotesTypeCode = null;
	private String custNotesTypeDesc;
	private boolean custNotesTypeIsPerminent;
	private String custNotesTypeArchiveFrq;
	private String lovDescCustNotesTypeArchiveFrqName;
	private boolean custNotesTypeIsActive;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CustomerNotesType befImage;
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

	public CustomerNotesType() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CustomerNotesType");
	}

	public CustomerNotesType(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getId() {
		return custNotesTypeCode;
	}
	public void setId (String id) {
		this.custNotesTypeCode = id;
	}
	
	public String getCustNotesTypeCode() {
		return custNotesTypeCode;
	}
	public void setCustNotesTypeCode(String custNotesTypeCode) {
		this.custNotesTypeCode = custNotesTypeCode;
	}
	
	public String getCustNotesTypeDesc() {
		return custNotesTypeDesc;
	}
	public void setCustNotesTypeDesc(String custNotesTypeDesc) {
		this.custNotesTypeDesc = custNotesTypeDesc;
	}
	
	public boolean isCustNotesTypeIsPerminent() {
		return custNotesTypeIsPerminent;
	}
	public void setCustNotesTypeIsPerminent(boolean custNotesTypeIsPerminent) {
		this.custNotesTypeIsPerminent = custNotesTypeIsPerminent;
	}
	
	public String getCustNotesTypeArchiveFrq() {
		return custNotesTypeArchiveFrq;
	}
	public void setCustNotesTypeArchiveFrq(String custNotesTypeArchiveFrq) {
		this.custNotesTypeArchiveFrq = custNotesTypeArchiveFrq;
	}

	public String getLovDescCustNotesTypeArchiveFrqName() {
		return this.lovDescCustNotesTypeArchiveFrqName;
	}
	public void setLovDescCustNotesTypeArchiveFrqName(String lovDescCustNotesTypeArchiveFrqName) {
		this.lovDescCustNotesTypeArchiveFrqName = lovDescCustNotesTypeArchiveFrqName;
	}
	
	public void setCustNotesTypeIsActive(boolean custNotesTypeIsActive) {
		this.custNotesTypeIsActive = custNotesTypeIsActive;
	}
	public boolean isCustNotesTypeIsActive() {
		return custNotesTypeIsActive;
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

	public CustomerNotesType getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerNotesType beforeImage){
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
	public boolean equals(CustomerNotesType customerNotesType) {
		return getId() == customerNotesType.getId();
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

		if (obj instanceof CustomerNotesType) {
			CustomerNotesType customerNotesType = (CustomerNotesType) obj;
			return equals(customerNotesType);
		}
		return false;
	}

}
