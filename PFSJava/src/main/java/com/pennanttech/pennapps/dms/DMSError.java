package com.pennanttech.pennapps.dms;

public enum DMSError {
	E999("");

	private String message;

	DMSError(String message) {
		this.message = message;
	}

	public String getCode() {
		return name();
	}

	String getMessage() {
		return message;
	}

	String getMessage(Object... args) {
		return String.format(getMessage(), args);
	}
}
