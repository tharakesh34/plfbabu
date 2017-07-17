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
 * Exception thrown on concurrency failure.
 */
public class ConcurrencyException extends AppException {
	private static final long serialVersionUID = 6906237326561801602L;
	private static final String DEFAULT_MESSAGE = "850: The record has been modified by another user. Please refresh the list to get the latest details of the record.";

	/**
	 * Constructs a {@code ConcurrencyException} with the default detail message.
	 */
	public ConcurrencyException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Constructs a {@code ConcurrencyException} with the specified detail message.
	 *
	 * @param message
	 *            The detail message.
	 */
	public ConcurrencyException(String message) {
		super(message);
	}

	/**
	 * Constructs a {@code ConcurrencyException} with the default detail message and cause.
	 *
	 * @param cause
	 *            The cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public ConcurrencyException(Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}

	/**
	 * Constructs a {@code ConcurrencyException} with the specified detail message and cause.
	 *
	 * @param message
	 *            The detail message.
	 * @param cause
	 *            The cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public ConcurrencyException(String message, Throwable cause) {
		super(message, cause);
	}
}
