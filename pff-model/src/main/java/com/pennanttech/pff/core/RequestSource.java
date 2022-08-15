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
package com.pennanttech.pff.core;

/**
 * Enumerates the interface types that were used in the data access layer.
 */
public enum RequestSource {
	UI(1), API(2), UPLOAD(3);

	private int source;

	private RequestSource(int source) {
		this.source = source;
	}

	public int source() {
		return source;
	}
}
