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
package com.pennanttech.pennapps.core.resource;

/**
 * Represents the exact description to message.
 */
public class Message {
	public static final String NO_RECORD_FOUND = "Zero rows were returned when at least one row expected";

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException If the constructor is used to create and initialize a new instance of the
	 *                                declaring class by suppressing Java language access checking.
	 */
	private Message() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
