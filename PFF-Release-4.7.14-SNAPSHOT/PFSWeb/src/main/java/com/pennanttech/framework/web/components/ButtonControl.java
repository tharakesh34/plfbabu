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
package com.pennanttech.framework.web.components;

import java.io.Serializable;

import org.zkoss.zul.Button;

public class ButtonControl implements Serializable {
	private static final long serialVersionUID = 3939439610372524593L;

	private Button button;
	private String rightName;
	private boolean firstTaskButton;

	protected ButtonControl() {
		super();
	}
	
	public ButtonControl(Button button) {
		this.button = button;
	}

	public ButtonControl(Button button, String rightName) {
		this.button = button;
		this.rightName = rightName;
	}

	public ButtonControl(Button button, String rightName, boolean firstTaskButton) {
		this.button = button;
		this.rightName = rightName;
		this.firstTaskButton = firstTaskButton;
	}

	public Button getButton() {
		return button;
	}

	public String getRightName() {
		return rightName;
	}

	public boolean isfirstTaskButton() {
		return firstTaskButton;
	}

}
