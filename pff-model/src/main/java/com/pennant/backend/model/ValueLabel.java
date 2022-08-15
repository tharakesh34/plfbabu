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
 *
 * FileName : ValueLabel.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model;

import java.io.Serializable;

import org.zkoss.util.resource.Labels;

public class ValueLabel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;
	private String label;

	public ValueLabel(String newValue, String newLabel) {
		this.value = newValue;
		this.label = newLabel;
	}

	public ValueLabel() {
		this.value = "#";
		this.label = Labels.getLabel("common.Select");
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public void setLabel(String string) {
		label = string;
	}

	public void setValue(String string) {
		value = string;
	}
}
