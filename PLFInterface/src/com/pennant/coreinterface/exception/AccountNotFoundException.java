package com.pennant.coreinterface.exception;


public class AccountNotFoundException extends EquationInterfaceException {

	private static final long serialVersionUID = -7905376200908320501L;

	public AccountNotFoundException() {
		super();
	}

	public AccountNotFoundException(String message) {
		super(message);
		
	}

	public AccountNotFoundException(Exception e) {
		super(e);
	}

}
