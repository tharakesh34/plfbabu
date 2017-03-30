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
package com.pennant.backend.dao;

/**
 * Enumerates the table types that were used in the data access layer. This includes physical and logical tables.
 */
public enum TableType {
	MAIN_TAB(""), TEMP_TAB("_Temp");

	private String suffix;

	private TableType(String suffix) {
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}
}
