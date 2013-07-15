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
 * FileName    		:  CheckListDetail.java                                                   * 	  
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

package com.pennant.backend.model.applicationmaster;

import java.sql.Timestamp;
import java.util.Map;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>CheckListDetail table</b>.<br>
 *
 */
public class CheckListDetail implements java.io.Serializable, Entity {

	private static final long serialVersionUID = -3176600783924484359L;
	private long checkListId = 0;
	private long ansSeqNo=Long.MIN_VALUE;
	private String ansDesc;
	private String ansCond;
	private boolean remarksMand;
	private boolean remarksAllow;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private CheckListDetail befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	private String lovDescCheckListDesc;
	private String lovDescRemarks;
	private FinanceReferenceDetail lovDescFinRefDetail;
	private String lovDescUserRole;
	Map<String , FinanceCheckListReference> lovDescPrevAnsMap;

	public Map<String, FinanceCheckListReference> getLovDescPrevAnsMap() {
		return lovDescPrevAnsMap;
	}

	public void setLovDescPrevAnsMap(
			Map<String, FinanceCheckListReference> lovDescPrevAnsMap) {
		this.lovDescPrevAnsMap = lovDescPrevAnsMap;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public CheckListDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CheckListDetail");
	}

	public CheckListDetail(long id) {
		this.setId(id);
	}

	//Getter and Setter methods

	public long getId() {
		return ansSeqNo;
	}

	public void setId (long id) {
		this.ansSeqNo = id;
	}

	public long getCheckListId() {
		return checkListId;
	}
	public void setCheckListId(long checkListId) {
		this.checkListId = checkListId;
	}




	public long getAnsSeqNo() {
		return ansSeqNo;
	}
	public void setAnsSeqNo(long ansSeqNo) {
		this.ansSeqNo = ansSeqNo;
	}




	public String getAnsDesc() {
		return ansDesc;
	}
	public void setAnsDesc(String ansDesc) {
		this.ansDesc = ansDesc;
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

	public CheckListDetail getBefImage(){
		return this.befImage;
	}

	public void setBefImage(CheckListDetail beforeImage){
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

	// Overidden Equals method to handle the comparision
	public boolean equals(CheckListDetail checkListDetail) {
		return getId() == checkListDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof CheckListDetail) {
			CheckListDetail checkListDetail = (CheckListDetail) obj;
			return equals(checkListDetail);
		}
		return false;
	}

	public void setLovDescCheckListDesc(String lovDescCheckListDesc) {
		this.lovDescCheckListDesc = lovDescCheckListDesc;
	}

	public String getLovDescCheckListDesc() {
		return lovDescCheckListDesc;
	}

	public void setLovDescFinRefDetail(FinanceReferenceDetail lovDescFinRefDetail) {
		this.lovDescFinRefDetail = lovDescFinRefDetail;
	}

	public FinanceReferenceDetail getLovDescFinRefDetail() {
		return lovDescFinRefDetail;
	}

	public void setLovDescUserRole(String lovDescUserRole) {
		this.lovDescUserRole = lovDescUserRole;
	}

	public String getLovDescUserRole() {
		return lovDescUserRole;
	}

	public void setLovDescRemarks(String lovDescRemarks) {
		this.lovDescRemarks = lovDescRemarks;
	}

	public String getLovDescRemarks() {
		return lovDescRemarks;
	}

	public void setAnsCond(String ansCond) {
		this.ansCond = ansCond;
	}

	public String getAnsCond() {
		return ansCond;
	}

	public void setRemarksMand(boolean remarksMand) {
		this.remarksMand = remarksMand;
	}

	public boolean isRemarksMand() {
		return remarksMand;
	}

	public void setRemarksAllow(boolean remarksAllow) {
		this.remarksAllow = remarksAllow;
	}

	public boolean isRemarksAllow() {
		return remarksAllow;
	}
}
