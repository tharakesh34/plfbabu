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
package com.pennanttech.pennapps.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * AbstractWorkflowEntity class. Any class that should be uniquely identifiable from another with work-flow process
 * properties should subclass from AbstractWorkflowEntity.
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractWorkflowEntity extends AbstractEntity {
	private static final long serialVersionUID = 8987922026116401165L;

	private String recordStatus;
	private String roleCode = "";
	private String nextRoleCode = "";
	private String taskId = "";
	private String nextTaskId = "";
	private String recordType;
	
	private long workflowId = 0;
	private String userAction = "Save";

	protected AbstractWorkflowEntity() {
		super();
	}

	public final String getRecordStatus() {
		return recordStatus;
	}

	public final void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public final String getRoleCode() {
		return roleCode;
	}

	public final void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public final String getNextRoleCode() {
		return nextRoleCode;
	}

	public final void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public final String getTaskId() {
		return taskId;
	}

	public final void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public final String getNextTaskId() {
		return nextTaskId;
	}

	public final void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public final String getRecordType() {
		return recordType;
	}

	public final void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public final long getWorkflowId() {
		return workflowId;
	}

	public final void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	public final boolean isWorkflow() {
		return this.workflowId == 0 ? false : true;
	}

	public final String getUserAction() {
		return userAction;
	}

	public final void setUserAction(String userAction) {
		this.userAction = userAction;
	}
}
