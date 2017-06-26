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

import java.text.MessageFormat;

/**
 * Exception thrown on factory configuration exceptions.
 */
public class FactoryException extends AppException {
	private static final long serialVersionUID = 6906237326561801602L;

	/**
	 * Constructs a {@code FactoryException} with the default detail message.
	 */
	protected FactoryException() {
		super();
	}

	/**
	 * Constructs a {@code FactoryException} with the default detail message.
	 *
	 * @param type
	 *            The type of factory.
	 */
	public FactoryException(String type) {
		super(MessageFormat.format(ErrorCode.PPS_899.getMessage(), type));
	}

	/**
	 * Constructs a {@code FactoryException} with the default detail message and cause.
	 *
	 * @param type
	 *            The type of factory.
	 * @param cause
	 *            The cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public FactoryException(String type, Throwable cause) {
		super(MessageFormat.format(ErrorCode.PPS_899.getMessage(), type), cause);
	}
}
