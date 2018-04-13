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

import com.pennant.backend.model.ValueLabel;

/**
 * 
 * Enumeration representing the the status of validation.
 *
 */
public enum Status {
	NONE(1, "None"), POSITIVE(2, "Positive"), NEGATIVE(3, "Negative"), NOTCMPLTD(4, "FI Not Completed");

	private final Integer key;
	private final String value;

	private Status(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	public static Status getType(Integer key) {
		for (Status type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}

	public List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (Status status : values()) {
			list.add(new ValueLabel(String.valueOf(status.getKey()), status.getValue()));
		}
		return list;
	}
}
