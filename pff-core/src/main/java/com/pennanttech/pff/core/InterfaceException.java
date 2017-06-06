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
 * The class {@code InterfaceException} and its subclasses are a form of {@code Throwable} that indicates conditions
 * that the interface might catch.
 */
public class InterfaceException extends AppException {
	private static final long	serialVersionUID	= 7376419132045778376L;

	private final String		errorCode;
	private final String		errorMessage;

	/**
	 * Constructs an {@code InterfaceException} with the specified code and detail message.
	 * 
	 * @param code
	 *            The code.
	 * @param message
	 *            The detail message.
	 */
	public InterfaceException(String code, String message) {
		super(code.concat(": ").concat(message));

		this.errorCode = code;
		this.errorMessage = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
