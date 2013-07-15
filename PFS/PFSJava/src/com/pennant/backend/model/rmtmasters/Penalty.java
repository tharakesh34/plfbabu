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
 * FileName    		:  Penalty.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.rmtmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Penalty table</b>.<br>
 *
 */
public class Penalty implements java.io.Serializable {

	private static final long serialVersionUID = 670834961932455516L;

	private String penaltyType = null;
	private String lovDescPenaltyTypeName = null;
	private Timestamp penaltyEffDate;
	private boolean isPenaltyCapitalize;
	private boolean isPenaltyOnPriOnly;
	private boolean isPenaltyAftGrace;
	private int oDueGraceDays;
	private String penaltyPriRateBasis;
	private BigDecimal penaltyPriBaseRate;
	private BigDecimal penaltyPriSplRate;
	private BigDecimal penaltyPriNetRate;
	private String penaltyIntRateBasis;
	private BigDecimal penaltyIntBaseRate;
	private BigDecimal penaltyIntSplRate;
	private BigDecimal penaltyIntNetRate;
	private boolean penaltyIsActive;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private Penalty befImage;
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

	public Penalty() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Penalty");
	}

	public Penalty(String id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public String getId() {
		return penaltyType;
	}
	public void setId (String id) {
		this.penaltyType = id;
	}

	public String getPenaltyType() {
		return penaltyType;
	}
	public void setPenaltyType(String penaltyType) {
		this.penaltyType = penaltyType;
	}

	public String getLovDescPenaltyTypeName() {
		return this.lovDescPenaltyTypeName;
	}
	public void setLovDescPenaltyTypeName(String lovDescPenaltyTypeName) {
		this.lovDescPenaltyTypeName = lovDescPenaltyTypeName;
	}

	public Timestamp getPenaltyEffDate() {
		return penaltyEffDate;
	}
	public void setPenaltyEffDate(Timestamp penaltyEffDate) {
		this.penaltyEffDate = penaltyEffDate;
	}

	public boolean isIsPenaltyCapitalize() {
		return isPenaltyCapitalize;
	}
	public void setIsPenaltyCapitalize(boolean isPenaltyCapitalize) {
		this.isPenaltyCapitalize = isPenaltyCapitalize;
	}

	public boolean isIsPenaltyOnPriOnly() {
		return isPenaltyOnPriOnly;
	}
	public void setIsPenaltyOnPriOnly(boolean isPenaltyOnPriOnly) {
		this.isPenaltyOnPriOnly = isPenaltyOnPriOnly;
	}

	public boolean isIsPenaltyAftGrace() {
		return isPenaltyAftGrace;
	}
	public void setIsPenaltyAftGrace(boolean isPenaltyAftGrace) {
		this.isPenaltyAftGrace = isPenaltyAftGrace;
	}

	public int getODueGraceDays() {
		return oDueGraceDays;
	}
	public void setODueGraceDays(int oDueGraceDays) {
		this.oDueGraceDays = oDueGraceDays;
	}

	public String getPenaltyPriRateBasis() {
		return penaltyPriRateBasis;
	}
	public void setPenaltyPriRateBasis(String penaltyPriRateBasis) {
		this.penaltyPriRateBasis = penaltyPriRateBasis;
	}

	public BigDecimal getPenaltyPriBaseRate() {
		return penaltyPriBaseRate;
	}
	public void setPenaltyPriBaseRate(BigDecimal penaltyPriBaseRate) {
		this.penaltyPriBaseRate = penaltyPriBaseRate;
	}

	public BigDecimal getPenaltyPriSplRate() {
		return penaltyPriSplRate;
	}
	public void setPenaltyPriSplRate(BigDecimal penaltyPriSplRate) {
		this.penaltyPriSplRate = penaltyPriSplRate;
	}

	public BigDecimal getPenaltyPriNetRate() {
		return penaltyPriNetRate;
	}
	public void setPenaltyPriNetRate(BigDecimal penaltyPriNetRate) {
		this.penaltyPriNetRate = penaltyPriNetRate;
	}

	public String getPenaltyIntRateBasis() {
		return penaltyIntRateBasis;
	}
	public void setPenaltyIntRateBasis(String penaltyIntRateBasis) {
		this.penaltyIntRateBasis = penaltyIntRateBasis;
	}

	public BigDecimal getPenaltyIntBaseRate() {
		return penaltyIntBaseRate;
	}
	public void setPenaltyIntBaseRate(BigDecimal penaltyIntBaseRate) {
		this.penaltyIntBaseRate = penaltyIntBaseRate;
	}

	public BigDecimal getPenaltyIntSplRate() {
		return penaltyIntSplRate;
	}
	public void setPenaltyIntSplRate(BigDecimal penaltyIntSplRate) {
		this.penaltyIntSplRate = penaltyIntSplRate;
	}

	public BigDecimal getPenaltyIntNetRate() {
		return penaltyIntNetRate;
	}
	public void setPenaltyIntNetRate(BigDecimal penaltyIntNetRate) {
		this.penaltyIntNetRate = penaltyIntNetRate;
	}

	public boolean isPenaltyIsActive() {
		return penaltyIsActive;
	}
	public void setPenaltyIsActive(boolean penaltyIsActive) {
		this.penaltyIsActive = penaltyIsActive;
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

	public Penalty getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Penalty beforeImage){
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
	public boolean equals(Penalty penalty) {
		return getId() == penalty.getId();
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

		if (obj instanceof Penalty) {
			Penalty penalty = (Penalty) obj;
			return equals(penalty);
		}
		return false;
	}
}
