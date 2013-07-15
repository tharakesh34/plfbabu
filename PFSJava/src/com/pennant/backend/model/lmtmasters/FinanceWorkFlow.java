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
 * FileName    		:  FinanceWorkFlow.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.lmtmasters;

import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>FinanceWorkFlow table</b>.<br>
 *
 */
public class FinanceWorkFlow implements java.io.Serializable {

	private static final long serialVersionUID = 6176092273862848126L;
	private String finType = null;
	private String lovDescFinTypeName;
	private String lovDescProductCodeName;
	private String screenCode = PennantConstants.defaultScreenCode;
	private String workFlowType;
	private String lovDescWorkFlowTypeName;
	private String lovDescWorkFlowRolesName;
	private String lovDescFirstTaskOwner;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private FinanceWorkFlow befImage;
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

	public FinanceWorkFlow() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("FinanceWorkFlow");
	}
	public FinanceWorkFlow(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getId() {
		return finType;
	}
	public void setId (String id) {
		this.finType = id;
	}
	
	public String getFinType() {
		return finType;
	}
	public void setFinType(String finType) {
		this.finType = finType;
	}
	
	public String getLovDescFinTypeName() {
		return this.lovDescFinTypeName;
	}
	public void setLovDescFinTypeName (String lovDescFinTypeName) {
		this.lovDescFinTypeName = lovDescFinTypeName;
	}
	
	public void setLovDescProductCodeName(String lovDescProductCodeName) {
	    this.lovDescProductCodeName = lovDescProductCodeName;
    }

	public String getLovDescProductCodeName() {
	    return lovDescProductCodeName;
    }

	public String getScreenCode() {
		return screenCode;
	}
	public void setScreenCode(String screenCode) {
		this.screenCode = screenCode;
	}
	
	public String getWorkFlowType() {
		return workFlowType;
	}
	public void setWorkFlowType(String workFlowType) {
		this.workFlowType = workFlowType;
	}

	public String getLovDescWorkFlowTypeName() {
		return this.lovDescWorkFlowTypeName;
	}
	public void setLovDescWorkFlowTypeName (String lovDescWorkFlowTypeName) {
		this.lovDescWorkFlowTypeName = lovDescWorkFlowTypeName;
	}
	
	public String getLovDescWorkFlowRolesName() {
		return lovDescWorkFlowRolesName;
	}
	public void setLovDescWorkFlowRolesName(String lovDescWorkFlowRolesName) {
		this.lovDescWorkFlowRolesName = lovDescWorkFlowRolesName;
	}

	public String getLovDescFirstTaskOwner() {
    	return lovDescFirstTaskOwner;
    }
	public void setLovDescFirstTaskOwner(String lovDescFirstTaskOwner) {
    	this.lovDescFirstTaskOwner = lovDescFirstTaskOwner;
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

	public FinanceWorkFlow getBefImage(){
		return this.befImage;
	}
	public void setBefImage(FinanceWorkFlow beforeImage){
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
	public boolean equals(FinanceWorkFlow financeWorkFlow) {
		return getId() == financeWorkFlow.getId();
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

		if (obj instanceof FinanceWorkFlow) {
			FinanceWorkFlow financeWorkFlow = (FinanceWorkFlow) obj;
			return equals(financeWorkFlow);
		}
		return false;
	}
}
