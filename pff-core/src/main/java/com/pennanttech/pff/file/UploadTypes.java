package com.pennanttech.pff.file;

public enum UploadTypes {
	RE_PRESENTMENT("Representment Upload Details Screen"),

	HOLD_REFUND(""),

	PAYMINS("Payment Instruction Refund"),

	MANDATES("Mandate Upload Details Screen"),

	FATE_CORRECTION("Fate Correction Upload Details Screen"),

	CHEQUES("Cheque Upload Details Screen"),

	EXCESS_TRANSFER("Excess Transfer Screen Details"),

	MANUAL_KNOCKOFF("Manual KnockOff Upload Details Screen"),

	CROSS_LOAN_KNOCKOFF("CrossLoan KnockOff Upload Details Screen"),

	CUSTOMER_KYC_DETAILS("Bulk Customer details upload screen"),

	HOST_GL("Host GL Mapping Upload Details Screen"),

	MISCELLANEOUS_POSTING("Miscellaneous Posting Upload Details Screen"),

	LOAN_CANCEL("Loan Cancellation Upload Screen"),

	FEE_WAIVER("Fee Wavier Upload Details"),

	LIEN("LIEN Upload Details Screen"),

	CREATE_RECEIPT("Create Receipt Upload detail screen"),

	RECEIPT_STATUS("RECEIPT STATUS Upload Details Screen"),

	WRITE_OFF("WriteOff Upload Details Screen"),

	REV_WRITE_OFF("Reverse WriteOff Upload Details Screen"),

	BRANCH_CHANGE("Branch Change Upload Details Screen"),

	BLOCK_AUTO_GEN_LTR("Block Auto Letter Generate Upload Details Screen"),

	LOAN_LETTER("Loan Letter Upload Details Screen"),

	COURIER_DETAILS("Courier Details Upload Screen"),

	LOAN_CLOSURE("Loan Closure Upload Details Screen"),

	PROVISION("Provision Upload Details Screen"),

	LPP_LOAN("Bulk Loan Overdue Penalty Upload Details"),

	LPP_LOAN_TYPE("Bulk Loan Type Overdue Penalty Upload Details");

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
