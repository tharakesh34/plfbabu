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
 * FileName    		:  ErrorDetail.java													*                           
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
*/
package com.pennanttech.pennapps.core.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

public class ErrorDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String code;
	private String language;
	private String severity;
	private String message;
	private String extendedMessage;
	private boolean newRecord;
	private ErrorDetail befImage;
	private LoggedInUser userDetails;

	private String field;
	private String[] parameters;
	private String[] fieldValues;
	private boolean overide = false;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("field");
		excludeFields.add("parameters");
		excludeFields.add("fieldValues");
		excludeFields.add("overide");
		return excludeFields;
	}

	public ErrorDetail() {
		super();
	}

	public ErrorDetail(String code) {
		super();
		this.setId(code);
	}

	public ErrorDetail(String code, String message, String[] parameters) {
		super();
		this.code = code;
		this.severity = "E"; // PennantConstants.ERR_SEV_ERROR;
		this.message = message;
		this.parameters = parameters;
	}

	public ErrorDetail(String code, String[] parameters) {
		super();
		this.code = code;
		this.severity = "E"; // PennantConstants.ERR_SEV_ERROR;
		this.parameters = parameters;
	}

	public ErrorDetail(String field, String code, String[] parameters, String[] fieldValues) {
		super();
		this.field = field;
		this.code = code;
		this.parameters = parameters;
		this.fieldValues = fieldValues;
	}

	public ErrorDetail(String field, String code, String severity, String message, String[] parameters,
			String[] fieldValues) {
		super();
		this.field = field;
		this.code = code;
		this.severity = severity;
		this.message = message;
		this.parameters = parameters;
		this.fieldValues = fieldValues;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public String getId() {
		return code;
	}

	public void setId(String id) {
		this.code = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExtendedMessage() {
		return extendedMessage;
	}

	public void setExtendedMessage(String extendedMessage) {
		this.extendedMessage = extendedMessage;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public String getError(String[] parameters) {
		this.parameters = parameters;
		return getError();
	}

	public String getError() {
		String error = StringUtils.trimToEmpty(this.message);

		if (this.parameters != null) {

			for (int i = 0; i < parameters.length; i++) {
				String parameter = StringUtils.trimToEmpty(parameters[i]);
				error = error.replace("{" + (i) + "}", parameter);
			}
		}

		for (int i = 0; i < 5; i++) {
			error = error.replace("{" + (i) + "}", "");
		}

		return error;
	}

	public boolean isOveride() {
		return overide;
	}

	public void setOveride(boolean overide) {
		this.overide = overide;
	}

	public String[] getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(String[] fieldValues) {
		this.fieldValues = fieldValues;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public ErrorDetail getBefImage() {
		return befImage;
	}

	public void setBefImage(ErrorDetail befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
