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
package com.pennant.Exception;

/**
 * The class {@code AppException} and its subclasses are a form of {@code Throwable} that indicates conditions that the
 * application might catch.
 */
public class AppException extends Exception {
	private static final long serialVersionUID = 888700447794830646L;

	public enum ErrorCode {
		APP_00001("00001: Unable to process the request. Please try again later or contact the system administrator."),
		APP_00101(
				"00101: The record has been modified by another user. Please refresh the list to get the latest details of the record.");

		private String message;

		private ErrorCode(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	/**
	 * Constructs an {@code AppException} with the default detail message.
	 */
	public AppException() {
		super(ErrorCode.APP_00001.getMessage());
	}

	/**
	 * Constructs an {@code AppException} with the specified code and detail message.
	 * 
	 * @param code
	 *            The code.
	 * @param message
	 *            The detail message.
	 */
	public AppException(String code, String message) {
		super(code.concat(": ").concat(message));
	}

	/**
	 * Constructs an {@code AppException} with the specified detail message.
	 *
	 * @param message
	 *            The detail message.
	 */
	public AppException(String message) {
		super(message);
	}

	/**
	 * Constructs an {@code AppException} with the specified detail message and cause.
	 *
	 * @param message
	 *            The detail message.
	 * @param cause
	 *            The cause. (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public AppException(String message, Throwable cause) {
		super(message, cause);
		getCause();
	}
}
