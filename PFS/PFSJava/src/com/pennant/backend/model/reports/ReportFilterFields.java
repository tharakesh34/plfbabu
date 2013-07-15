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
 * FileName    		: ReportFilterFields.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-09-2012    														*
 *                                                                  						*
 * Modified Date    :  23-09-2012     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-09-2012             Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.reports;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;


public class ReportFilterFields implements java.io.Serializable, Entity{

	private static final long serialVersionUID = -619353564397203914L;
	private long      reportID;
	private long      fieldID =Long.MIN_VALUE ; 
	private String    fieldName;
	private String    fieldType ;
	private String    fieldLabel ;
	private String    fieldDBName ;
	private String    appUtilMethodName;     
	private String    moduleName;           
	private String    lovHiddenFieldMethod;
	private String    lovTextFieldMethod ;
	private boolean   multiSelectSearch;
	private int       fieldLength;
	private int       fieldMaxValue ;
	private int       fieldMinValue;
	private String    fieldConstraint ;
	private boolean   mandatory;
	private int       seqOrder ;
	private String    whereCondition;
	private String    fieldErrorMessage;
	private String    staticValue;
	private int       fieldWidth;  
	private boolean   filterRequired;
	private String    defaultFilter;

	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private ReportFilterFields befImage;
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
	public ReportFilterFields() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("ReportFilterFields");
	}
	public ReportFilterFields(long id) {
		this.setId(id);
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//



	public int getFieldWidth() {
		return fieldWidth;
	}
	public void setFieldWidth(int fieldWidth) {
		this.fieldWidth = fieldWidth;
	}
	public String getStaticValue() {
		return staticValue;
	}
	public void setStaticValue(String staticValue) {
		this.staticValue = staticValue;
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
	public String getFieldLabel() {
		return fieldLabel;
	}
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	public String getFieldDBName() {
		return fieldDBName;
	}
	public void setFieldDBName(String fieldDBName) {
		this.fieldDBName = fieldDBName;
	}
	public String getAppUtilMethodName() {
		return appUtilMethodName;
	}

	public String getLovTextFieldMethod() {
		return lovTextFieldMethod;
	}
	public void setLovTextFieldMethod(String lovTextFieldMethod) {
		this.lovTextFieldMethod = lovTextFieldMethod;
	}
	public boolean isMultiSelectSearch() {
		return multiSelectSearch;
	}
	public void setMultiSelectSearch(boolean multiSelectSearch) {
		this.multiSelectSearch = multiSelectSearch;
	}

	public int getFieldMinValue() {
		return fieldMinValue;
	}
	public void setFieldMinValue(int fieldMinValue) {
		this.fieldMinValue = fieldMinValue;
	}
	public String getFieldConstraint() {
		return fieldConstraint;
	}
	public void setFieldConstraint(String fieldConstraint) {
		this.fieldConstraint = fieldConstraint;
	}
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	public void setAppUtilMethodName(String appUtilMethodName) {
		this.appUtilMethodName = appUtilMethodName;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getSeqOrder() {
		return seqOrder;
	}
	public void setSeqOrder(int seqOrder) {
		this.seqOrder = seqOrder;
	}

	public void setLovHiddenFieldMethod(String lovHiddenFieldMethod) {
		this.lovHiddenFieldMethod = lovHiddenFieldMethod;
	}
	public String getLovHiddenFieldMethod() {
		return lovHiddenFieldMethod;
	}
	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}
	public String getWhereCondition() {
		return whereCondition;
	}
	public void setFieldErrorMessage(String fieldErrorMessage) {
		this.fieldErrorMessage = fieldErrorMessage;
	}
	public String getFieldErrorMessage() {
		return fieldErrorMessage;
	}
	public void setFilterRequired(boolean filterRequired) {
		this.filterRequired = filterRequired;
	}
	public boolean isFilterRequired() {
		return filterRequired;
	}
	public void setDefaultFilter(String defaultFilter) {
		this.defaultFilter = defaultFilter;
	}
	public String getDefaultFilter() {
		return defaultFilter;
	}
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}
	public int getFieldLength() {
		return fieldLength;
	}
	public void setFieldMaxValue(int fieldMaxValue) {
		this.fieldMaxValue = fieldMaxValue;
	}
	public int getFieldMaxValue() {
		return fieldMaxValue;
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

	public ReportFilterFields getBefImage() {
		return befImage;
	}

	public void setBefImage(ReportFilterFields befImage) {
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	// Overridden Equals method to handle the comparison
	public boolean equals(ReportConfiguration reportConfiguration) {
		return getId() == reportConfiguration.getId();
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}	
	public void setReportID(long reportID) {
		this.reportID = reportID;
	}
	public long getReportID() {
		return reportID;
	}
	public void setFieldID(long fieldID) {
		this.fieldID = fieldID;
	}
	public long getFieldID() {
		return fieldID;
	}
	@Override
	public long getId() {
		return fieldID;
	}
	@Override
	public void setId(long id) {
		this.fieldID = id;

	}
}

