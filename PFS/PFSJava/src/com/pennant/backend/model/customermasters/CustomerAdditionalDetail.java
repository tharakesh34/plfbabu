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
 * FileName    		:  CustomerAdditionalDetail.java                                                   * 	  
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

package com.pennant.backend.model.customermasters;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CustomerAdditionalDetail table</b>.<br>
 *
 */
public class CustomerAdditionalDetail implements java.io.Serializable {
	
	private static final long serialVersionUID = 6021576460912988391L;
	
	private long custID=Long.MIN_VALUE;
	private String lovDescCustShrtName;
	private String custAcademicLevel;
	private String lovDescCustAcademicLevelName;
	private String academicDecipline;
	private String lovDescAcademicDeciplineName;
	private long custRefCustID;
	private String custRefStaffID;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CustomerAdditionalDetail befImage;
	private LoginUserDetails userDetails;
	private String lovDescCustRecordType;
	private String lovDescCustCIF;

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

	public CustomerAdditionalDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CustomerAdditionalDetail");
	}

	public CustomerAdditionalDetail(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return custID;
	}
	public void setId (long id) {
		this.custID = id;
	}
	
	public long getCustID() {
		return custID;
	}
	public void setCustID(long custID) {
		this.custID = custID;
	}

	public void setLovDescCustShrtName(String lovDescCustShrtName) {
		this.lovDescCustShrtName = lovDescCustShrtName;
	}
	public String getLovDescCustShrtName() {
		return lovDescCustShrtName;
	}

	public String getCustAcademicLevel() {
		return custAcademicLevel;
	}
	public void setCustAcademicLevel(String custAcademicLevel) {
		this.custAcademicLevel = custAcademicLevel;
	}

	public String getLovDescCustAcademicLevelName() {
		return this.lovDescCustAcademicLevelName;
	}
	public void setLovDescCustAcademicLevelName(String lovDescCustAcademicLevelName) {
		this.lovDescCustAcademicLevelName = lovDescCustAcademicLevelName;
	}
	
	public String getAcademicDecipline() {
		return academicDecipline;
	}
	public void setAcademicDecipline(String academicDecipline) {
		this.academicDecipline = academicDecipline;
	}

	public String getLovDescAcademicDeciplineName() {
		return this.lovDescAcademicDeciplineName;
	}
	public void setLovDescAcademicDeciplineName(String lovDescAcademicDeciplineName) {
		this.lovDescAcademicDeciplineName = lovDescAcademicDeciplineName;
	}
	
	public long getCustRefCustID() {
		return custRefCustID;
	}
	public void setCustRefCustID(long custRefCustID) {
		this.custRefCustID = custRefCustID;
	}
	
	public String getCustRefStaffID() {
		return custRefStaffID;
	}
	public void setCustRefStaffID(String custRefStaffID) {
		this.custRefStaffID = custRefStaffID;
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

	public CustomerAdditionalDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CustomerAdditionalDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public void setLovDescCustRecordType(String lovDescCustRecordType) {
		this.lovDescCustRecordType = lovDescCustRecordType;
	}
	public String getLovDescCustRecordType() {
		return lovDescCustRecordType;
	}

	public void setLovDescCustCIF(String lovDescCustCIF) {
		this.lovDescCustCIF = lovDescCustCIF;
	}
	public String getLovDescCustCIF() {
		return lovDescCustCIF;
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
	public boolean equals(CustomerAdditionalDetail customerAdditionalDetail) {
		return getId() == customerAdditionalDetail.getId();
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

		if (obj instanceof CustomerAdditionalDetail) {
			CustomerAdditionalDetail customerAdditionalDetail = (CustomerAdditionalDetail) obj;
			return equals(customerAdditionalDetail);
		}
		return false;
	}
}
