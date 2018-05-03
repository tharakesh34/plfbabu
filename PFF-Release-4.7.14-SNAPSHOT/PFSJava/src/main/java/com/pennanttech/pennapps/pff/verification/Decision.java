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
 * Enumeration representing the the decision of validation.
 *
 */
public enum Decision {
	SELECT(0, Labels.getLabel("Combo.Select")), APPROVE(1, "Approve"), RE_INITIATE(2, "Re-initiate"), OVERRIDE(3, "Override");

	private final Integer key;
	private final String value;

	private Decision(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static Decision getType(Integer key) {
		for (Decision type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}
	
	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (Decision type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}

		return list;
	}
}
