package com.pennant.coreinterface.exception;

public class AddressNotFoundException extends EquationInterfaceException {

	private static final long serialVersionUID = -7905376200908320501L;

	public AddressNotFoundException() {
		super();
	}

	public AddressNotFoundException(String message) {
		super(message);
	}

	public AddressNotFoundException(Exception e) {
		super(e);
	}

}