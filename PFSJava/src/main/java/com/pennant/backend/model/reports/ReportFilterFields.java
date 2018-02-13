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

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;


public class ReportFilterFields extends AbstractWorkflowEntity implements Entity{

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

	private boolean newRecord=false;
	private String lovValue;
	private ReportFilterFields befImage;
	private LoggedInUser userDetails;
	private String filterFileds= null;
	
	public boolean isNew() {
		return isNewRecord();
	}
	public ReportFilterFields() {
		super();
	}
	public ReportFilterFields(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
	public String getFilterFileds() {
		return filterFileds;
	}
	public void setFilterFileds(String filterFileds) {
		this.filterFileds = filterFileds;
	}
}

