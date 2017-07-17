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
package com.pennanttech.pennapps.core;

/**
 * Exception thrown on dependencies found for the record to be deleted.
 */
public class DependencyFoundException extends AppException {
	private static final long serialVersionUID = 4471400442589963105L;
	private static final String DEFAULT_MESSAGE = "849: The record is in use and therefore cannot be deleted.";

	/**
	 * Constructs a {@code DependencyFoundException} with the default detail message.
	 */
	public DependencyFoundException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Constructs a {@code DependencyFoundException} with the specified detail message.
	 *
	 * @param message
	 *            The detail message.
	 */
	public DependencyFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a {@code DependencyFoundException} with the specified detail message and cause.
	 *
	 * @param message
	 *            The detail message.
	 * @param cause
	 *            The cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public DependencyFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a {@code DependencyFoundException} with the default detail message and cause.
	 *
	 * @param cause
	 *            The cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public DependencyFoundException(Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}
}
