package com.pennant.coreinterface.exception;


public class AccountNotFoundException extends EquationInterfaceException {

	private static final long serialVersionUID = -7905376200908320501L;

	private String errorMsg;
	
	public AccountNotFoundException() {
		super();
	}

	public AccountNotFoundException(String message) {
		super(message);
		this.errorMsg = message;
		
	}

	public AccountNotFoundException(Exception e) {
		super(e);
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}


}
