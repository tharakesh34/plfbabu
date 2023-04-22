package com.pennanttech.pff.file;

public enum UploadTypes {
	RE_PRESENTMENT("Representment Upload Details Screen"),

	HOLD_REFUND(""),

	PAYINS_REFUND("Payment Instruction Refund"),

	MANDATES("Mandate Upload Details Screen"),

	FATE_CORRECTION("Fate Correction Upload Details Screen"),

	CHEQUE("Cheque Upload Details Screen"),

	LPP("Bulk Overdue Penalty Upload Details"),

	EXCESS_TRANSFER("Excess Transfer Screen Details"),

	MANUAL_KNOCKOFF("Manual KnockOff Upload Details Screen"),

	CROSS_LOAN_KNOCKOFF("CrossLoan KnockOff Upload Details Screen"),

	CUSTOMER_KYC_DETAILS("Bulk Customer details upload screen"),

	HOST_GL("Host GL Mapping Upload Details Screen"),

	MISCELLANEOUS_POSTING("Miscellaneous Posting Upload Details Screen"),

	LOAN_CANCEL("Loan Cancellation Upload Screen"),

	FEE_WAIVER("Fee Wavier Upload Details"),

	LIEN("LIEN Upload Details Screen");

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
