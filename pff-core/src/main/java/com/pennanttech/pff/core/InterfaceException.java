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

	public String				errorMessage;
	public String				errorCode;
	public String[]				parameters;

	public String getErrorMessage() {
		return errorMessage;
	}

	public InterfaceException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.errorMessage = message;

	}

	public InterfaceException(String errorCode, String[] parameters, String message) {
		super(message);
		this.errorCode = errorCode;
		this.parameters = parameters;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
