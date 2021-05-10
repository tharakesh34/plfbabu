package com.pennanttech.pff.core.disbursement;

public enum PaymentChannel {
	Disbursement("D"), Payment("P");

	private final String value;

	private PaymentChannel(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
