package com.pennant.exception;

public class CustomerNotFoundException extends Exception {

	private static final long serialVersionUID = -7905376200908320501L;
	
	private String errorMsg;

	public CustomerNotFoundException() {
		super();
	}

	public CustomerNotFoundException(String message) {
		super(message);
		this.errorMsg = message;
	}

	public CustomerNotFoundException(Exception e) {
		super(e);
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

}
