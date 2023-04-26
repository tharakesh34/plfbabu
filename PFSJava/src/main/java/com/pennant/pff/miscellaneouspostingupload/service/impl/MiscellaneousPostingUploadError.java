package com.pennant.pff.miscellaneouspostingupload.service.impl;

public enum MiscellaneousPostingUploadError {
	MP01(" FinReference is Mandatory. "),

	MP02(" FinReference is not valid. "),

	MP03(" FinReference is not in active."),

	MP04(" Credit GL is not valid."),

	MP05(" Debit GL is not valid."),

	MP06("  Value Date should greater than or equal to Loan StartDate."),

	ML07(" Value Date should lesser than or equal to Loan MaturityDate."),

	MP08(" Batch Name is invalid.Allowed Characters are [a-z A-Z 0-9]."),

	MP09(" TxnAmount should be Mandatory."),

	MP010(" TxnAmount should be greater than zero.");

	private String description;

	private MiscellaneousPostingUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

}
