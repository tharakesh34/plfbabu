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
 * Enumerates the error codes that were used in the custom exceptions.
 */
public enum ErrorCode {
	// The prefix PPS stands for pennApps Product Suite.
	PPS_901("901: Unable to process the request due to issues with cache manager. Please try again later or contact the system administrator."),
	PPS_801("801: The record has been modified by another user. Please refresh the list to get the latest details of the record."),
	PPS_802("802: The record is in use and therefore cannot be deleted.");

	private String message;

	private ErrorCode(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
