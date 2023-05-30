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
public enum StatuReasons {

	FISRES("FISTATUS", "Feild Invetigation Status Reasons"), LVSRES("LVSTATUS", "Legal Verification Status Reasons"),
	TVSRES("TVSTATUS", "Technical Verification Status Reasons"),
	RCUSRES("RCUSTATUS", "Risk Control Unit Status Reasons"),
	// FI status reason types
	FINTVRTY("FINTSTATUS", Labels.getLabel("label_FI_Negative_ReasonTypes.value")),
	FIRFRRTY("FIRRSTATUS", Labels.getLabel("label_FI_Refer_ReasonTypes.value")),
	FIPOSTVRTY("FIPTSTATUS", Labels.getLabel("label_FI_Positive_ReasonTypes.value")),
	// LV status reason types
	LVNTVRTY("LVNTSTATUS", Labels.getLabel("label_LV_Negative_ReasonTypes.value")),
	LVRFRRTY("LVRRSTATUS", Labels.getLabel("label_LV_Refer_ReasonTypes.value")),
	LVPOSTVRTY("LVPTSTATUS", Labels.getLabel("label_LV_Positive_ReasonTypes.value")),
	// TV status reason types
	TVPOSTVRTY("TVPTSTATUS", Labels.getLabel("label_TV_Positive_ReasonTypes.value")),
	TVNTVRTY("TVNTSTATUS", Labels.getLabel("label_TV_Negative_ReasonTypes.value")),
	TVRFRRTY("TVRRSTATUS", Labels.getLabel("label_TV_Refer_ReasonTypes.value")),
	// RCU status reason types
	RCUPOSTVRTY("RCUPTSTAT", Labels.getLabel("label_RCU_Positive_ReasonTypes.value")),
	RCUNTVRTY("RCUNTSTAT", Labels.getLabel("label_RCU_Negative_ReasonTypes.value")),
	RCURFRRTY("RCURRSTAT", Labels.getLabel("label_RCU_Refer_ReasonTypes.value")),
	// PD status reason types
	PDPOSTVRTY("PDPTSTATUS", Labels.getLabel("label_PD_Positive_ReasonTypes.value")),
	PDNTVRTY("PDNTSTATUS", Labels.getLabel("label_PD_Negative_ReasonTypes.value")),
	PDRFRRTY("PDRRSTATUS", Labels.getLabel("label_PD_Refer_ReasonTypes.value"));

	private final String key;
	private final String value;

	private StatuReasons(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static StatuReasons getType(String key) {
		for (StatuReasons type : values()) {
			if (type.getKey().equals(key)) {
				return type;
			}
		}
		return null;
	}

	public static List<ValueLabel> getList() {
		List<ValueLabel> list = new ArrayList<>();
		for (StatuReasons type : values()) {
			list.add(new ValueLabel(String.valueOf(type.getKey()), type.getValue()));
		}

		return list;
	}

}