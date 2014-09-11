package com.pennant.coreinterface.exception;


public class CustomerLimitProcessException extends Exception {

	private static final long serialVersionUID = 1L;
	public String message;
	public String data = null;

	public CustomerLimitProcessException() {
		super();
	}

	public CustomerLimitProcessException(Exception e) {
		data = super.getMessage();
	}

	public String getMessage() {
		return message;
	}
	
	public CustomerLimitProcessException(String messsage) {
		this.message = messsage;
	}

	public String getMsgData() {
		return this.data;
	}

}
