package com.pennant.Exception;

public class PFFException extends Exception {
    private static final long serialVersionUID = -4043122980787816882L;
	private final String  errorCode;
	private final String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public PFFException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.errorMessage = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
