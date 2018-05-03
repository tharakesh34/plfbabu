package com.pennant.ws.exception;

public class APIException extends Exception {
	private static final long serialVersionUID = -4043122980787816882L;
	String errorCode;
	String errorMessage;
	
	public APIException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
