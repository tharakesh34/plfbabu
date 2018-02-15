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
 * FileName    		: ReportSearchTemplate.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  5-09-2012    														*
 *                                                                  						*
 * Modified Date    :  5-09-2012       														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012            Pennant	                 0.1                                            * 
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

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ReportSearchTemplate extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -619353564397203914L;
	
	private long      reportID;
	private long      fieldID  ; 
	private long      usrID  ; 
	private String    filter;
	private String    fieldType ;
	private String    fieldValue ;
	private String    templateName ;
	private boolean   newRecord=false;
	private String    lovValue;

	public ReportSearchTemplate() {
		super();
	}
	
	public long getReportID() {
		return reportID;
	}
	public void setReportID(long reportID) {
		this.reportID = reportID;
	}
	public long getFieldID() {
		return fieldID;
	}
	public void setFieldID(long fieldID) {
		this.fieldID = fieldID;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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
	public void setUsrID(long usrID) {
		this.usrID = usrID;
	}
	public long getUsrID() {
		return usrID;
	}
}
