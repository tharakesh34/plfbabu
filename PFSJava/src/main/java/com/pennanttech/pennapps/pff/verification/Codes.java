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

/**
 * 
 * Enumeration representing the the type of validation.
 *
 */
public enum Codes {
	FIAGENCY("FIAGENCY", "Feild Invetigation Agency"), 
	LVAGENCY("LVAGENCY", "Legal Verification Agency"), 
	TVAGENCY("TVAGENCY", "Technical Verification Agency"), 
	RCUAGNCY("RCUAGNCY", "Risk Control Unit Agency"), 
	FIWRES("FIWRES", "Feild Invetigation Waiver Reasons"), 
	LVWRES("LVWRES", "Legal Verification Waiver Reasons"),
	TVWRES("TVWRES", "Technical Verification Waiver Reasons"), 
	RCUWRES("RCUWRES", "Risk Control Unit Waiver Reasons"),
	FI_STSTUS_REASON("FISTATUS", "Feild Invetigation Waiver Reasons");

	private final String key;
	private final String value;

	private Codes(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
