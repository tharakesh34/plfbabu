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
 * FileName    		:  ReportList.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2012    														*
 *                                                                  						*
 * Modified Date    :  23-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2012       Pennant	                 0.1                                            * 
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

import java.util.Map;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>ReportList table</b>.<br>
 *
 */
public class ReportList extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String code = null;
	private String module = null;
	private String fieldLabels;
	private String fieldValues;
	private String fieldType;
	private String addfields;
	private String reportFileName;
	private String reportHeading;
	private String moduleType;
	private boolean newRecord;
	private String lovValue;
	private ReportList befImage;
	private LoggedInUser userDetails;
	private boolean formatReq = false;

	public boolean isNew() {
		return isNewRecord();
	}

	public ReportList() {
		super();
	}

	public ReportList(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return code;
	}
	public void setId (String id) {
		this.code = id;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	public String getFieldLabels() {
		return fieldLabels;
	}
	public String[] getLabels() {
		String[] strLabels=new String[15];
		if(fieldLabels!=null){
			strLabels= fieldLabels.split(",");
			for (int i = 0; i < strLabels.length; i++) {
				strLabels[i]= PennantJavaUtil.getLabel(strLabels[i]);
			}
		}
		return strLabels;
	}
	public void setFieldLabels(String fieldLabels) {
		this.fieldLabels = fieldLabels;
	}
	
	public String getFieldValues() {
		return fieldValues;
	}
	public String[] getValues() {
		String[] strValues=null;
		if(fieldValues!=null){
			strValues= fieldValues.split(",");
		}
		return strValues;
	}
	public void setFieldValues(String fieldValues) {
		this.fieldValues = fieldValues;
	}
	
	public String getFieldType() {
		return fieldType;
	}
	public String[] getType() {
		String[] strType=null;
		if(fieldType!=null){
			strType= fieldType.split(",");
		}
		return strType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	public String getAddfields() {
		return addfields;
	}
	public void setAddfields(String addfields) {
		this.addfields = addfields;
	}
	
	public String getReportFileName() {
		return reportFileName;
	}
	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}
	
	public String getReportHeading() {
		return reportHeading;
	}
	public void setReportHeading(String reportHeading) {
		this.reportHeading = reportHeading;
	}
	
	public String getModuleType() {
		return moduleType;
	}
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}
	
	public boolean isFormatReq() {
	    return formatReq;
    }
	public void setFormatReq(boolean formatReq) {
	    this.formatReq = formatReq;
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

	public ReportList getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(ReportList beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public Map<String, Object> getMainHeaderDetails(Map<String, Object> mainHeaders){
		User userImpl =  SessionUserDetails.getLogiedInUser();

		mainHeaders.put("reportHeading", getReportHeading());
		mainHeaders.put("moduleType", getModuleType());
		mainHeaders.put("userId", userImpl.getSecurityUser().getUsrLogin());
		return mainHeaders;

	}

}
