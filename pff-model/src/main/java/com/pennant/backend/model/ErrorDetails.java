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
 * FileName    		:  ErrorDetails.java													*                           
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
package com.pennant.backend.model;

import org.apache.commons.lang.StringUtils;

public class ErrorDetails {
	private String		errorField;
	private String		errorCode;
	private String		errorLanguage;
	private String		errorSeverity;
	private String		errorMessage;
	private String		errorExtendedMessage;
	private String[]	errorParameters;
	private String[]	errorFieldValues;
	private boolean		errorOveride	= false;

	public ErrorDetails() {
		super();
	}

	public ErrorDetails(String errorCode, String message, String[] errorParameters) {
		super();
		this.errorCode = errorCode;
		this.errorSeverity = "E";				//PennantConstants.ERR_SEV_ERROR;
		this.errorMessage = message;
		this.errorParameters = errorParameters;
	}

	public ErrorDetails(String errorCode, String[] errorParameters) {
		super();
		this.errorCode = errorCode;
		this.errorSeverity = "E";		// PennantConstants.ERR_SEV_ERROR;
		this.errorParameters = errorParameters;
	}

	public ErrorDetails(String errorField, String errorCode, String[] errorParameters, String[] errorFieldValues) {
		super();
		this.errorField = errorField;
		this.errorCode = errorCode;
		this.errorParameters = errorParameters;
		this.errorFieldValues = errorFieldValues;
	}

	public ErrorDetails(String errorField, String errorCode, String severity, String message, String[] errorParameters,
			String[] errorFieldValues) {
		super();
		this.errorField = errorField;
		this.errorCode = errorCode;
		this.errorSeverity = severity;
		this.errorMessage = message;
		this.errorParameters = errorParameters;
		this.errorFieldValues = errorFieldValues;
	}

	public String getErrorField() {
		return errorField;
	}

	public void setErrorField(String errorField) {
		this.errorField = errorField;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorLanguage() {
		return errorLanguage;
	}

	public void setErrorLanguage(String errorLanguage) {
		this.errorLanguage = errorLanguage;
	}

	public String getErrorSeverity() {
		return errorSeverity;
	}

	public void setErrorSeverity(String errorSeverity) {
		this.errorSeverity = errorSeverity;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorExtendedMessage() {
		return errorExtendedMessage;
	}

	public void setErrorExtendedMessage(String errorExtendedMessage) {
		this.errorExtendedMessage = errorExtendedMessage;
	}

	public String[] getErrorParameters() {
		return errorParameters;
	}

	public void setErrorParameters(String[] errorParameters) {
		this.errorParameters = errorParameters;
	}

	public String getError(String[] parameters) {
		this.errorParameters = parameters;
		return getError();
	}

	public String getError() {
		String error = StringUtils.trimToEmpty(this.errorMessage);

		if (this.errorParameters != null) {

			for (int i = 0; i < errorParameters.length; i++) {
				String parameter = StringUtils.trimToEmpty(errorParameters[i]);
				error = error.replace("{" + (i) + "}", parameter);
			}
		}

		for (int i = 0; i < 5; i++) {
			error = error.replace("{" + (i) + "}", "");
		}

		return error;
	}

	public boolean isErrorOveride() {
		return errorOveride;
	}

	public void setErrorOveride(boolean errorOveride) {
		this.errorOveride = errorOveride;
	}

	public String[] getErrorFieldValues() {
		return errorFieldValues;
	}

	public void setErrorFieldValues(String[] errorFieldValues) {
		this.errorFieldValues = errorFieldValues;
	}
}
