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
import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

public class ReportConfiguration implements java.io.Serializable, Entity{

	private static final long serialVersionUID = -619353564397203914L;


	private long reportID = Long.MIN_VALUE;
	private String  reportName;
	private String   reportHeading;
	private boolean   promptRequired;
	private String   reportJasperName;
	private boolean  showTempLibrary;
	private String  dataSourceName;	
	private String  menuItemCode;	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private ReportConfiguration befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
    private  List<ReportFilterFields> listReportFieldsDetails;


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public List<ReportFilterFields> getListReportFieldsDetails() {
		return listReportFieldsDetails;
	}
	public void setListReportFieldsDetails(
			List<ReportFilterFields> listReportFieldsDetails) {
		this.listReportFieldsDetails = listReportFieldsDetails;
	}
	public boolean isNew() {
		return isNewRecord();
	}
	public ReportConfiguration() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("ReportConfiguration");
	}
	public ReportConfiguration(long id) {
		this.setId(id);
	}



	public long getId() {
		return reportID;
	}
	public void setId (long id) {
		this.reportID = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportHeading() {
		return reportHeading;
	}

	public void setReportHeading(String reportHeading) {
		this.reportHeading = reportHeading;
	}

	public boolean isPromptRequired() {
		return promptRequired;
	}

	public void setPromptRequired(boolean promptRequired) {
		this.promptRequired = promptRequired;
	}

	public String getReportJasperName() {
		return reportJasperName;
	}

	public void setReportJasperName(String reportJasperName) {
		this.reportJasperName = reportJasperName;
	}

	public boolean isShowTempLibrary() {
		return showTempLibrary;
	}

	public void setShowTempLibrary(boolean showTempLibrary) {
		this.showTempLibrary = showTempLibrary;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getMenuItemCode() {
		return menuItemCode;
	}

	public void setMenuItemCode(String menuItemCode) {
		this.menuItemCode = menuItemCode;
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

	public ReportConfiguration getBefImage() {
		return befImage;
	}

	public void setBefImage(ReportConfiguration befImage) {
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

		if (obj instanceof ReportConfiguration) {
			ReportConfiguration reportConfiguration = (ReportConfiguration) obj;
			return equals(reportConfiguration);
		}
		return false;
	}
	public void setReportID(long reportID) {
		this.reportID = reportID;
	}
	public long getReportID() {
		return reportID;
	}
}
