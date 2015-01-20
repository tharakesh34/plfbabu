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
 * FileName    		:  AgreementDefinition.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-11-2011    														*
 *                                                                  						*
 * Modified Date    :  23-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>AgreementDefinition table</b>.<br>
 *
 */
public class AgreementDefinition implements java.io.Serializable,Entity {

	private static final long serialVersionUID = 6547333014929558827L;
	private long aggId= Long.MIN_VALUE;
	private String aggCode = null;
	private String aggName;
	private String aggDesc;
	private String aggReportName;
	private String aggReportPath;
	private String agrRule;
	private String lovDescAgrRuleDesc;
	private boolean aggIsActive;
	private String aggtype;
	private String aggImage;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private AgreementDefinition befImage;
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

	public AgreementDefinition() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("AgreementDefinition");
	}
	public AgreementDefinition(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getId() {
		return aggId;
	}
	public void setId (long id) {
		this.aggId= id;
	}

	
	public long getAggId() {
		return aggId;
	}

	public void setAggId(long aggId) {
		this.aggId = aggId;
	}

	public String getAggCode() {
		return aggCode;
	}
	public void setAggCode(String aggCode) {
		this.aggCode = aggCode;
	}

	public String getAggName() {
		return aggName;
	}
	public void setAggName(String aggName) {
		this.aggName = aggName;
	}

	public String getAggDesc() {
		return aggDesc;
	}
	public void setAggDesc(String aggDesc) {
		this.aggDesc = aggDesc;
	}

	public String getAggReportName() {
		return aggReportName;
	}
	public void setAggReportName(String aggReportName) {
		this.aggReportName = aggReportName;
	}

	public String getAggReportPath() {
		return aggReportPath;
	}
	public void setAggReportPath(String aggReportPath) {
		this.aggReportPath = aggReportPath;
	}
	
	public String getAgrRule() {
		return agrRule;
	}
	public void setAgrRule(String agrRule) {
		this.agrRule = agrRule;
	}

	public String getLovDescAgrRuleDesc() {
		return lovDescAgrRuleDesc;
	}
	public void setLovDescAgrRuleDesc(String lovDescAgrRuleDesc) {
		this.lovDescAgrRuleDesc = lovDescAgrRuleDesc;
	}

	public boolean isAggIsActive() {
		return aggIsActive;
	}
	public void setAggIsActive(boolean aggIsActive) {
		this.aggIsActive = aggIsActive;
	}
	
	public String getAggtype() {
    	return aggtype;
    }

	public void setAggtype(String aggtype) {
    	this.aggtype = aggtype;
    }

	public String getAggImage() {
    	return aggImage;
    }

	public void setAggImage(String aggImage) {
    	this.aggImage = aggImage;
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

	public AgreementDefinition getBefImage(){
		return this.befImage;
	}
	public void setBefImage(AgreementDefinition beforeImage){
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
	public boolean equals(AgreementDefinition agreementDefinition) {
		return getId() == agreementDefinition.getId();
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

		if (obj instanceof AgreementDefinition) {
			AgreementDefinition agreementDefinition = (AgreementDefinition) obj;
			return equals(agreementDefinition);
		}
		return false;
	}
}
