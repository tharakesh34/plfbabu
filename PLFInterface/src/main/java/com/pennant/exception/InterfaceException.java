package com.pennant.exception;

public class InterfaceException extends Exception {
	private static final long	serialVersionUID	= 7376419132045778376L;

	public String				errorMessage;
	public String				errorCode;
	public String[]				parameters;

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public InterfaceException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.errorMessage = message;

	}

	/**
	 * PFFInterfaceException with multiple field values
	 * 
	 * @param errorCode
	 * @param parameters
	 * @param message
	 */
	public InterfaceException(String errorCode, String[] parameters, String message) {
		super(message);
		this.errorCode = errorCode;
		this.parameters = parameters;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
