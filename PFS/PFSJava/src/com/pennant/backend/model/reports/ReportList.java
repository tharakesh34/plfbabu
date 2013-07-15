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

import java.sql.Timestamp;
import java.util.Map;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.policy.model.UserImpl;

/**
 * Model class for the <b>ReportList table</b>.<br>
 *
 */
public class ReportList implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String module = null;
	private String fieldLabels;
	private String fieldValues;
	private String fieldType;
	private String addfields;
	private String reportFileName;
	private String reportHeading;
	private String moduleType;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private ReportList befImage;
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

	public ReportList() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("ReportList");
	}

	public ReportList(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getId() {
		return module;
	}
	public void setId (String id) {
		this.module = id;
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

	public ReportList getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(ReportList beforeImage){
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

	// Overridden Equals method to handle the comparison
	public boolean equals(ReportList reportList) {
		return getId() == reportList.getId();
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

		if (obj instanceof ReportList) {
			ReportList reportList = (ReportList) obj;
			return equals(reportList);
		}
		return false;
	}
	
	public Map<String, Object> getMainHeaderDetails(Map<String, Object> mainHeaders){
		UserImpl userImpl =  SessionUserDetails.getLogiedInUser();

		mainHeaders.put("reportHeading", getReportHeading());
		mainHeaders.put("moduleType", getModuleType());
		mainHeaders.put("userId", userImpl.getSecurityUser().getUsrLogin());
		return mainHeaders;

	}
}
