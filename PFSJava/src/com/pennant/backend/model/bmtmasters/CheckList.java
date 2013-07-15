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
 * FileName    		:  CheckList.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.bmtmasters;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CheckList table</b>.<br>
 *
 */
public class CheckList implements java.io.Serializable, Entity {

	private static final long serialVersionUID = -3060817228345423733L;
	
	private long checkListId = Long.MIN_VALUE;
	private String checkListDesc;
	private int checkMinCount;
	private int checkMaxCount;
	private String checkRule;
	private String lovDescCheckRuleName;
	private boolean active;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CheckList befImage;
	private LoginUserDetails userDetails;
	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private List<CheckListDetail> chkListList=new ArrayList<CheckListDetail>();
	private HashMap<String, List<AuditDetail>> lovDescAuditDetailMap = new HashMap<String, List<AuditDetail>>();

	public boolean isNew() {
		return isNewRecord();
	}

	public CheckList() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CheckList");
	}
	public CheckList(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public long getId() {
		return checkListId;
	}
	public void setId (long id) {
		this.checkListId = id;
	}
	
	public long getCheckListId() {
		return checkListId;
	}
	public void setCheckListId(long checkListId) {
		this.checkListId = checkListId;
	}
	
	public String getCheckListDesc() {
		return checkListDesc;
	}
	public void setCheckListDesc(String checkListDesc) {
		this.checkListDesc = checkListDesc;
	}
	
	public int getCheckMinCount() {
		return checkMinCount;
	}
	public void setCheckMinCount(int checkMinCount) {
		this.checkMinCount = checkMinCount;
	}
	
	public int getCheckMaxCount() {
		return checkMaxCount;
	}
	public void setCheckMaxCount(int checkMaxCount) {
		this.checkMaxCount = checkMaxCount;
	}
	
	public String getCheckRule() {
    	return checkRule;
    }
	public void setCheckRule(String checkRule) {
    	this.checkRule = checkRule;
    }

	public String getLovDescCheckRuleName() {
    	return lovDescCheckRuleName;
    }
	public void setLovDescCheckRuleName(String lovDescCheckRuleName) {
    	this.lovDescCheckRuleName = lovDescCheckRuleName;
    }

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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

	public CheckList getBefImage(){
		return this.befImage;
	}
	public void setBefImage(CheckList beforeImage){
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


	public List<CheckListDetail> getChkListList() {
		return chkListList;
	}

	public void setChkListList(List<CheckListDetail> chkListList) {
		this.chkListList = chkListList;
	}

	public HashMap<String, List<AuditDetail>> getLovDescAuditDetailMap() {
		return lovDescAuditDetailMap;
	}
	public void setLovDescAuditDetailMap(
			HashMap<String, List<AuditDetail>> lovDescAuditDetailMap) {
		this.lovDescAuditDetailMap = lovDescAuditDetailMap;
	}

	// Overridden Equals method to handle the comparison
	public boolean equals(CheckList checkList) {
		return getId() == checkList.getId();
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

		if (obj instanceof CheckList) {
			CheckList checkList = (CheckList) obj;
			return equals(checkList);
		}
		return false;
	}
}
