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
 * Enumeration representing the the type of validation.
 *
 */
public enum RequestType {
	INITIATE(1, Labels.getLabel("label_INITIATE")),
	WAIVE(2, Labels.getLabel("label_WAIVE")),
	NOT_REQUIRED(3, Labels.getLabel("label_NOT_REQUIRED")),
	REQUEST(4, Labels.getLabel("label_REQUEST"));

	private final Integer key;
	private final String value;

	private RequestType(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static RequestType getType(Integer key) {
		for (RequestType type : values()) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (RequestType type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}
		return list;
	}

	public static List<ValueLabel> getRCURequestTypes() {
		List<ValueLabel> list = new ArrayList<>();
		for (RequestType type : values()) {
			String labelCode = "label_RCU_" + type.name();
			list.add(new ValueLabel(String.valueOf(type.getKey()), Labels.getLabel(labelCode)));
		}
		return list;
	}
}