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

public class SequenceFlow implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private boolean userAction;
	private String conditionExpression;
	private String action;
	private String state;
	private boolean notesMandatory;
	private String targetRef;

	public SequenceFlow() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isUserAction() {
		return userAction;
	}

	public void setUserAction(boolean userAction) {
		this.userAction = userAction;
	}

	public String getConditionExpression() {
		return conditionExpression;
	}

	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getActionAsString() {
		return action.concat("=").concat(state);
	}

	public boolean isNotesMandatory() {
		return notesMandatory;
	}

	public void setNotesMandatory(boolean notesMandatory) {
		this.notesMandatory = notesMandatory;
	}

	public String getTargetRef() {
		return targetRef;
	}

	public void setTargetRef(String targetRef) {
		this.targetRef = targetRef;
	}
}
