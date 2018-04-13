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
package com.pennanttech.pennapps.pff.verification;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

/**
 * 
 * Enumeration representing the the Agencies Verification.
 *
 */
public enum Agencies {
	 
	FIELD_INVESTIGATION("FIAGENCY", Labels.getLabel("label_Field_Investigation")), LEGAL_VERIFICATION("LVAGENCY", Labels.getLabel("label_Legal_Verification")), RCU_VERIFICATION(
			"RCUVAGENCY", Labels.getLabel("label_RCU_Verification")), TECHNICAL_VERIFICATION("TVAGENCY", Labels.getLabel("label_Technical_Verification"));

	private final String key;
	private final String value;

	private Agencies(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static Agencies getType(String key) {
		for (Agencies type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (Agencies type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}

		return list;
	}
}
