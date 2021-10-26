package com.pennapps.security.core.otp;

public enum OTPStatus {
	INVALID(-1, "Invalid"), SENT(0, "Sent"), VERIFIED(2, "Verified"), EXPIRED(3, "Expired"), RE_SEND(4, "Resend");

	private final Integer key;
	private final String value;

	private OTPStatus(Integer key, String value) {
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
