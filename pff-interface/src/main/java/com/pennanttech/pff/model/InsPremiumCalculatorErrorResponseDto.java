package com.pennanttech.pff.model;

public class InsPremiumCalculatorErrorResponseDto {
	private String responseStatus;
	private String error;
	private String message;

	// Getter Methods

	public String getResponseStatus() {
		return responseStatus;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	// Setter Methods

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}