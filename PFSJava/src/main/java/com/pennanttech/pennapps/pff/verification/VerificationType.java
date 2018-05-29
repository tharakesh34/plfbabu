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
 * Enumeration representing the the request type of validation.
 *
 */
public enum VerificationType {
	FI(1, "FI", "FI_VER"), TV(2, "TV", "TECHNICAL_VER"), LV(3, "LV", "LEGAL_VER"), RCU(4, "RCU",
			"RISK_CREDIT_UNIT_VER");

	private final Integer key;
	private final String value;
	private final String code;

	private VerificationType(Integer key, String value, String code) {
		this.key = key;
		this.value = value;
		this.code = code;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getCode() {
		return code;
	}

	public static VerificationType getVerificationType(Integer key) {
		for (VerificationType type : values()) {
			if (type.getKey() == key) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (VerificationType requestType : values()) {
			list.add(new ValueLabel(String.valueOf(requestType.getKey()), requestType.getValue()));
		}
		return list;
	}
}
