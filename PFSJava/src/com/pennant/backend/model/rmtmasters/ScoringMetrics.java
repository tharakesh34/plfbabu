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
 * FileName    		:  ScoringMetrics.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2011    														*
 *                                                                  						*
 * Modified Date    :  05-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>ScoringMetrics table</b>.<br>
 *
 */
public class ScoringMetrics implements java.io.Serializable {

	private static final long serialVersionUID = 5228231887285744745L;
	private long scoreGroupId = 0; //Scoring Group ID
	private long scoringId; //Scoring Metric Id
	private String categoryType;
	
	private String lovDescScoringGroupCode; //Scoring Group Code
	private String lovDescScoringCode; //Scoring Metric Group Id/Rule Code
	private String lovDescScoringCodeDesc;//Scoring Metric Group Desc/ Rule Desc
	private BigDecimal lovDescMetricMaxPoints;//Rule Max Score/ Sum of Rules Max Score for SubGroup
	private String lovDescMetricTotPerc;
	private int lovDescScoreMetricSeq; // Scoring Metric Group Seq / Rule Seq order
	private String lovDescSQLRule;
	private BigDecimal lovDescExecutedScore; // Scoring Metric Group Seq / Rule Seq order
	
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private ScoringMetrics befImage;
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

	public ScoringMetrics() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("ScoringMetrics");
	}

	public ScoringMetrics(long id) {
		this.setId(id);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getId() {
		return scoreGroupId;
	}
	public void setId (long id) {
		this.scoreGroupId = id;
	}

	public long getScoreGroupId() {
		return scoreGroupId;
	}
	public void setScoreGroupId(long scoreGroupId) {
		this.scoreGroupId = scoreGroupId;
	}
	
	public String getLovDescScoringGroupCode() {
    	return lovDescScoringGroupCode;
    }
	public void setLovDescScoringGroupCode(String lovDescScoringGroupCode) {
    	this.lovDescScoringGroupCode = lovDescScoringGroupCode;
    }

	public long getScoringId() {
		return scoringId;
	}
	public void setScoringId(long scoringId) {
		this.scoringId = scoringId;
	}

	public void setLovDescScoringCode(String lovDescScoringCode) {
		this.lovDescScoringCode = lovDescScoringCode;
	}
	public String getLovDescScoringCode() {
		return lovDescScoringCode;
	}

	public String getLovDescScoringCodeDesc() {
    	return lovDescScoringCodeDesc;
    }
	public void setLovDescScoringCodeDesc(String lovDescScoringCodeDesc) {
    	this.lovDescScoringCodeDesc = lovDescScoringCodeDesc;
    }

	public int getLovDescScoreMetricSeq() {
    	return lovDescScoreMetricSeq;
    }
	public void setLovDescScoreMetricSeq(int lovDescScoreMetricSeq) {
    	this.lovDescScoreMetricSeq = lovDescScoreMetricSeq;
    }

	public String getLovDescSQLRule() {
    	return lovDescSQLRule;
    }
	public void setLovDescSQLRule(String lovDescSQLRule) {
    	this.lovDescSQLRule = lovDescSQLRule;
    }
	
	public BigDecimal getLovDescExecutedScore() {

		if (lovDescExecutedScore == null) {
			lovDescExecutedScore = BigDecimal.ZERO;
		}
		return lovDescExecutedScore;
	}

	public void setLovDescExecutedScore(BigDecimal lovDescExecutedScore) {
    	this.lovDescExecutedScore = lovDescExecutedScore;
    }

	public String getCategoryType() {
    	return categoryType;
    }
	public void setCategoryType(String categoryType) {
    	this.categoryType = categoryType;
    }

	public BigDecimal getLovDescMetricMaxPoints() {
    	return lovDescMetricMaxPoints;
    }

	public void setLovDescMetricMaxPoints(BigDecimal lovDescMetricMaxPoints) {
    	this.lovDescMetricMaxPoints = lovDescMetricMaxPoints;
    }

	public String getLovDescMetricTotPerc() {
    	return lovDescMetricTotPerc;
    }
	public void setLovDescMetricTotPerc(String lovDescMetricTotPerc) {
    	this.lovDescMetricTotPerc = lovDescMetricTotPerc;
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

	public ScoringMetrics getBefImage(){
		return this.befImage;
	}
	public void setBefImage(ScoringMetrics beforeImage){
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
	public boolean equals(ScoringMetrics scoringMetrics) {
		return getId() == scoringMetrics.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof ScoringMetrics) {
			ScoringMetrics scoringMetrics = (ScoringMetrics) obj;
			return equals(scoringMetrics);
		}
		return false;
	}

}
