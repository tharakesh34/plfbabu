package com.pennant.coreinterface.exception;


public class EquationInterfaceException extends Exception {

	private static final long serialVersionUID = 1L;
	public String message;
	public String data = null;

	public EquationInterfaceException() {
		super();
	}

	public EquationInterfaceException(Exception e) {
		data = super.getMessage();
	}

	public String getMessage() {
		System.out.println("message : " + message);
		return message;
	}
	
	public EquationInterfaceException(String messsage) {
		super(messsage);
	}

	public String getMsgData() {
		return this.data;
	}

}
