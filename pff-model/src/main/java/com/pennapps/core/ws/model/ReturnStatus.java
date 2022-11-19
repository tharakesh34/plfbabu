package com.pennapps.core.ws.model;

import java.io.Serializable;

public class ReturnStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	String returnCode;
	String returnText;

	public ReturnStatus() {
		super();
	}

	public ReturnStatus(String returnCode, String returnText) {
		this.returnCode = returnCode;
		this.returnText = returnText;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

}
