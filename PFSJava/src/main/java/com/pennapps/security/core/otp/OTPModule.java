package com.pennapps.security.core.otp;

public enum OTPModule {
	LOGIN(1, "Login"), RE(2, "RESETPASSWORD");

	private final Integer key;
	private final String value;

	private OTPModule(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
