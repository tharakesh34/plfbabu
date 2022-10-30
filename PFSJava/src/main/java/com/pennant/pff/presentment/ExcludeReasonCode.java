package com.pennant.pff.presentment;

public enum ExcludeReasonCode {
	EMI_INCLUDE(0, ""),

	EMI_IN_ADVANCE(1, ""),

	EMI_HOLD(2, ""),

	MANDATE_HOLD(3, ""),

	MANDATE_NOT_APPROVED(4, ""),

	MANDATE_EXPIRED(5, ""),

	MANUAL_EXCLUDE(6, ""),

	MANDATE_REJECTED(7, ""),

	CHEQUE_PRESENT(8, ""),

	// CHEQUE_BOUNCE(9, ""),

	// CHEQUE_REALISE(10, ""),

	// CHEQUE_REALISED(11, ""),

	INT_ADV(12, ""),

	EMI_ADV(13, "");

	private int id;
	private String description;

	private ExcludeReasonCode(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public String code() {
		return this.name();
	}

	public int id() {
		return id;
	}

	public String description() {
		return description;
	}

}
