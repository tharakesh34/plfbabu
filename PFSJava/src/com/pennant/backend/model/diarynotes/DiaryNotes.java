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
 * FileName    		:  DiaryNotes.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2011    														*
 *                                                                  						*
 * Modified Date    :  20-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.diarynotes;

import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>DiaryNotes table</b>.<br>
 *
 */
public class DiaryNotes implements java.io.Serializable, Entity {
	private static final long serialVersionUID = 1L;
	private long seqNo = Long.MIN_VALUE;
	private String dnType;
	private String dnCreatedNo;
	private String dnCreatedName;
	private String frqCode;	
	private Date firstActionDate;
	private Date nextActionDate;
	private Date lastActionDate;
	private Date finalActionDate;
	private boolean suspend;
	private Date suspendStartDate;
	private Date suspendEndDate;
	private boolean recordDeleted;
	private String narration;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private DiaryNotes befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;
	
	/*private boolean recordExist = false;
	private String  existingRecordValues;*/

	public boolean isNew() {
		return isNewRecord();
	}

	public DiaryNotes() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("DiaryNotes");
	}

	public DiaryNotes(long id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	public long getId() {
		return seqNo;
	}
	
	public void setId (long id) {
		this.seqNo = id;
	}
	
	public long getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}
	

	
	public String getDnType() {
		return dnType;
	}
	public void setDnType(String dnType) {
		this.dnType = dnType;
	}
	

	
	public String getDnCreatedNo() {
		return dnCreatedNo;
	}
	public void setDnCreatedNo(String dnCreatedNo) {
		this.dnCreatedNo = dnCreatedNo;
	}
	

	
	public String getDnCreatedName() {
		return dnCreatedName;
	}
	public void setDnCreatedName(String dnCreatedName) {
		this.dnCreatedName = dnCreatedName;
	}
	

	
	public String getFrqCode() {
		return frqCode;
	}
	public void setFrqCode(String frqCode) {
		this.frqCode = frqCode;
	}
	

	public Date getFirstActionDate() {
		return firstActionDate;
	}
	public void setFirstActionDate(Date firstActionDate) {
		this.firstActionDate = firstActionDate;
	}
	

	
	public Date getNextActionDate() {
		return nextActionDate;
	}
	public void setNextActionDate(Date nextActionDate) {
		this.nextActionDate = nextActionDate;
	}
	

	
	public Date getLastActionDate() {
		return lastActionDate;
	}
	public void setLastActionDate(Date lastActionDate) {
		this.lastActionDate = lastActionDate;
	}
	

	
	public Date getFinalActionDate() {
		return finalActionDate;
	}
	public void setFinalActionDate(Date finalActionDate) {
		this.finalActionDate = finalActionDate;
	}
	

	
	public boolean isSuspend() {
		return suspend;
	}
	public void setSuspend(boolean suspend) {
		this.suspend = suspend;
	}
	

	
	public Date getSuspendStartDate() {
		return suspendStartDate;
	}
	public void setSuspendStartDate(Date suspendStartDate) {
		this.suspendStartDate = suspendStartDate;
	}
	

	
	public Date getSuspendEndDate() {
		return suspendEndDate;
	}
	public void setSuspendEndDate(Date suspendEndDate) {
		this.suspendEndDate = suspendEndDate;
	}
	

	
	public boolean isRecordDeleted() {
		return recordDeleted;
	}
	public void setRecordDeleted(boolean recordDeleted) {
		this.recordDeleted = recordDeleted;
	}
	

	
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
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

	public DiaryNotes getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DiaryNotes beforeImage){
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
	public boolean equals(DiaryNotes diaryNotes) {
		return getId() == diaryNotes.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof DiaryNotes) {
			DiaryNotes diaryNotes = (DiaryNotes) obj;
			return equals(diaryNotes);
		}
		return false;
	}	
	
}
