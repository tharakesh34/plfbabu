package com.pennant.webui.customermasters.customer;

public class ReturnStatus {
	private String returnCode;

	private String returnText;

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

	@Override
	public String toString() {
		return "ClassPojo [returnCode = " + returnCode + ", returnText = " + returnText + "]";
	}
}
