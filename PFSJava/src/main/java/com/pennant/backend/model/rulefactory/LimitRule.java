/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : Limitrule.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-07-2016 * * Modified Date :
 * 23-07-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.rulefactory;

import java.io.Serializable;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>DedupParm table</b>.<br>
 *
 */
public class LimitRule implements Serializable {

	private static final long serialVersionUID = -5474091857496782851L;

	private long queryId = Long.MIN_VALUE;
	private String queryCode = null;
	private String queryDesc;
	private String queryModule;
	private String querySubCode;
	private String sQLQuery;
	private String actualBlock;
	private boolean active;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord = false;
	private String lovValue;
	private LimitRule befImage;
	private LoggedInUser userDetails;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public LimitRule() {
	    super();
	}

	public LimitRule(long id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public long getId() {
		return queryId;
	}

	public void setId(long id) {
		this.queryId = id;
	}

	public long getQueryId() {
		return queryId;
	}

	public void setQueryId(long queryId) {
		this.queryId = queryId;
	}

	public String getQueryCode() {
		return queryCode;
	}

	public void setQueryCode(String queryCode) {
		this.queryCode = queryCode;
	}

	public String getQueryDesc() {
		return queryDesc;
	}

	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	public String getQueryModule() {
		return queryModule;
	}

	public void setQueryModule(String queryModule) {
		this.queryModule = queryModule;
	}

	public void setQuerySubCode(String querySubCode) {
		this.querySubCode = querySubCode;
	}

	public String getQuerySubCode() {
		return querySubCode;
	}

	public String getSQLQuery() {
		return sQLQuery;
	}

	public void setSQLQuery(String sQLQuery) {
		this.sQLQuery = sQLQuery;
	}

	public String getActualBlock() {
		return actualBlock;
	}

	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
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

	public LimitRule getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LimitRule beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
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
		if (this.workflowId == 0) {
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
