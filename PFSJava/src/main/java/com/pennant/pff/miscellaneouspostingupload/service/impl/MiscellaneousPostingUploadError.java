package com.pennant.pff.miscellaneouspostingupload.service.impl;

public enum MiscellaneousPostingUploadError {
	MP01("Loan Reference is Mandatory. "),

	MP02("Loan Reference is not valid. "),

	MP03("Loan Reference is not in active."),

	MP04("Credit GL is not valid."),

	MP05("Debit GL is not valid."),

	MP06("Value Date should greater than or equal to Loan StartDate."),

	ML07("Future Date is not allowed."),

	MP08("Batch Name is invalid.Allowed Characters are [a-z A-Z 0-9]."),

	MP09("TxnAmount should be Mandatory."),

	MP010("TxnAmount should be greater than zero.");

	private String description;

	private MiscellaneousPostingUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

}
