package com.pennanttech.pff.core.disbursement;

public enum PaymentChannel {
	Disbursement("D"), Payment("P"), Insurance("I");

	private final String value;

	private PaymentChannel(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
