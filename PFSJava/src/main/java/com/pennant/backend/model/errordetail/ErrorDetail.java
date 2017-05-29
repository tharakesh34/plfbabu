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
 * FileName    		:  ErrorDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2016    														*
 *                                                                  						*
 * Modified Date    :  05-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2016       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.errordetail;

import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.LoggedInUser;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>ErrorDetail table</b>.<br>
 *
 */
public class ErrorDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String errorCode;
	private String errorLanguage;
	private String errorSeverity;
	private String errorSeverityName;
	private String errorMessage;
	private String errorExtendedMessage;
	private boolean newRecord;
	private String lovValue;
	private ErrorDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public ErrorDetail() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("ErrorDetail"));
	}

	public ErrorDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("errorSeverityName");
	return excludeFields;
	}

	    // ******************************************************//
		// ****************** getter / setter  ******************//
		// ******************************************************//

	public String getId() {
		return errorCode;
	}
	
	public void setId (String id) {
		this.errorCode = id;
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
	

	public String getErrorSeverityName() {
		return this.errorSeverityName;
	}

	public void setErrorSeverityName (String errorSeverityName) {
		this.errorSeverityName = errorSeverityName;
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

	public ErrorDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(ErrorDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getErrorSeverity() {
		return errorSeverity;
	}

	public void setErrorSeverity(String errorSeverity) {
		this.errorSeverity = errorSeverity;
	}
}
