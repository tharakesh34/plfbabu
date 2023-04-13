package com.pennant.pff.miscellaneouspostingupload.service.impl;

public enum MiscellaneousPostingUploadError {
	MP01(" FinReference is Mandatory. "),

	MP02(" FinReference is not valid. "),

	MP03(" FinReference is not in active."),

	MP04(" Credit GL is not valid."),

	MP05(" Debit GL is not valid."),

	MP06("  Value Date should greater than or equal to Loan StartDate."),

	ML07(" Value Date should lesser than or equal to Loan MaturityDate.");

	private String description;

	private MiscellaneousPostingUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

}
