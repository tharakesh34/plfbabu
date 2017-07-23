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
package com.pennanttech.pennapps.core.engine.workflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserTask implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String actor;
	private String assignmentLevel;
	private String baseActor;
	private List<String> additionalForms = new ArrayList<>();
	private boolean allowSave;
	private boolean reinstateLevel;
	private boolean delegator;

	public UserTask() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getAssignmentLevel() {
		return assignmentLevel;
	}

	public void setAssignmentLevel(String assignmentLevel) {
		this.assignmentLevel = assignmentLevel;
	}

	public String getBaseActor() {
		return baseActor;
	}

	public void setBaseActor(String baseActor) {
		this.baseActor = baseActor;
	}

	public List<String> getAdditionalForms() {
		return additionalForms;
	}

	public void setAdditionalForms(List<String> additionalForms) {
		this.additionalForms = additionalForms;
	}

	public boolean isAllowSave() {
		return allowSave;
	}

	public void setAllowSave(boolean allowSave) {
		this.allowSave = allowSave;
	}

	public boolean isReinstateLevel() {
		return reinstateLevel;
	}

	public void setReinstateLevel(boolean reinstateLevel) {
		this.reinstateLevel = reinstateLevel;
	}

	public boolean isDelegator() {
		return delegator;
	}

	public void setDelegator(boolean delegator) {
		this.delegator = delegator;
	}
}
