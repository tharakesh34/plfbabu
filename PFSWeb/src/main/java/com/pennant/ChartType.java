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
package com.pennant;

/**
 * Enumerates the chart types that were used in business logic layer.
 */
public enum ChartType {
	Pie3D("pieRadius='90' startingAngle='310' formatNumberScale='0' enableRotation='1' forceDecimals='1'"), MSLine("labelDisplay='ROTATE' formatNumberScale='0' rotateValues='0' startingAngle='310' showValues='0' forceDecimals='1' skipOverlapLabels='0'");

	private String value;

	private ChartType(String value) {
		this.value = value;
	}

	public String getDefaultRemarks() {
		return value;
	}
}
