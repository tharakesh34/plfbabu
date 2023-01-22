package com.pennanttech.pff.file;

public enum UploadTypes {
	RE_PRESENTMENT("Representment Upload Details Screen"),

	HOLD_REFUND("Hold Refund Upload Details Screen"),

	PAYINS_REFUND("Payment Instruction Refund"),

	MANDATES("Mandate Upload Details Screen"),

	FATE_CORRECTION("Fate Correction Upload Details Screen"),

	CHEQUE("Cheque Upload Details Screen"),

	EXCESS_TRANSFER("Excess Transfer Screen Details"),
	
	MANUAL_KNOCKOFF("Manual KnockOff Upload Details Screen");

	private String description;

	private UploadTypes(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}
}
