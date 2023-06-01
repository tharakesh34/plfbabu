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
 * Enumeration representing the the Agencies Verification.
 *
 */
public enum WaiverReasons {

	FIWRES("FIWRES", "Feild Invetigation Waiver Reasons"), LVWRES("LVWRES", "Legal Verification Waiver Reasons"),
	TVWRES("TVWRES", "Technical Verification Waiver Reasons"), RCUWRES("RCUWRES", "Risk Control Unit Waiver Reasons"),
	PDWRES("PDWRES", "Pesonal Discussion Waiver Reasons");

	private final String key;
	private final String value;

	private WaiverReasons(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static WaiverReasons getType(String key) {
		for (WaiverReasons type : values()) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (WaiverReasons type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}

		return list;
	}
}