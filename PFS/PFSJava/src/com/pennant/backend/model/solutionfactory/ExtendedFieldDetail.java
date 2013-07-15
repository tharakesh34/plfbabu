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
 * FileName    		:  ExtendedFieldDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.solutionfactory;

import java.io.Serializable;
import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>ExtendedFieldDetail table</b>.<br>
 */
public class ExtendedFieldDetail implements Serializable, Entity {
	
	private static final long	serialVersionUID	= -6761267821648279163L;
	
	private long moduleId = Long.MIN_VALUE;
	private String lovDescModuleName;
	private String lovDescSubModuleName;
	private String fieldName;
	private String fieldType;
	private int fieldLength;
	private int fieldPrec;
	private String fieldLabel;
	private boolean fieldMandatory;
	private String fieldConstraint;
	private int fieldSeqOrder;
	private String fieldList;
	private String fieldDefaultValue;
	private long fieldMinValue;
	private long fieldMaxValue;
	private boolean fieldUnique;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private ExtendedFieldDetail befImage;
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

	public ExtendedFieldDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("ExtendedFieldDetail");
	}

	public ExtendedFieldDetail(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return moduleId;
	}
	public void setId (long id) {
		this.moduleId = id;
	}
	
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	public String getLovDescModuleName() {
		return lovDescModuleName;
	}
	public void setLovDescModuleName(String lovDescModuleName) {
		this.lovDescModuleName = lovDescModuleName;
	}

	public String getLovDescSubModuleName() {
		return lovDescSubModuleName;
	}
	public void setLovDescSubModuleName(String lovDescSubModuleName) {
		this.lovDescSubModuleName = lovDescSubModuleName;
	}

	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	public int getFieldLength() {
		return fieldLength;
	}
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}
	
	public int getFieldPrec() {
		return fieldPrec;
	}
	public void setFieldPrec(int fieldPrec) {
		this.fieldPrec = fieldPrec;
	}
	
	public String getFieldLabel() {
		return fieldLabel;
	}
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	
	public boolean isFieldMandatory() {
		return fieldMandatory;
	}
	public void setFieldMandatory(boolean fieldMandatory) {
		this.fieldMandatory = fieldMandatory;
	}
	
	public String getFieldConstraint() {
		return fieldConstraint;
	}
	public void setFieldConstraint(String fieldConstraint) {
		this.fieldConstraint = fieldConstraint;
	}
	
	public int getFieldSeqOrder() {
		return fieldSeqOrder;
	}
	public void setFieldSeqOrder(int fieldSeqOrder) {
		this.fieldSeqOrder = fieldSeqOrder;
	}
	
	public String getFieldList() {
		return fieldList;
	}
	public void setFieldList(String fieldList) {
		this.fieldList = fieldList;
	}
	
	public String getFieldDefaultValue() {
		return fieldDefaultValue;
	}
	public void setFieldDefaultValue(String fieldDefaultValue) {
		this.fieldDefaultValue = fieldDefaultValue;
	}
	
	public long getFieldMinValue() {
		return fieldMinValue;
	}
	public void setFieldMinValue(long fieldMinValue) {
		this.fieldMinValue = fieldMinValue;
	}
	
	public long getFieldMaxValue() {
		return fieldMaxValue;
	}
	public void setFieldMaxValue(long fieldMaxValue) {
		this.fieldMaxValue = fieldMaxValue;
	}
	
	public boolean isFieldUnique() {
		return fieldUnique;
	}
	public void setFieldUnique(boolean fieldUnique) {
		this.fieldUnique = fieldUnique;
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

	public ExtendedFieldDetail getBefImage(){
		return this.befImage;
	}
	public void setBefImage(ExtendedFieldDetail beforeImage){
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

	// Overidden Equals method to handle the comparision
	public boolean equals(ExtendedFieldDetail extendedFieldDetail) {
		return getId() == extendedFieldDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof ExtendedFieldDetail) {
			ExtendedFieldDetail extendedFieldDetail = (ExtendedFieldDetail) obj;
			return equals(extendedFieldDetail);
		}
		return false;
	}

}
