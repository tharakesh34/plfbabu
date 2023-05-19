package com.pennant.pff.noc.upload.error;

public enum BlockAutoGenLetterUploadError {

	BALG_01("[Loan Reference] is not valid."),

	BALG_02("[Action] is not valid, possible value should be either of the following {B/R} Only."),

	BALG_03("Flag already exists against the Loan."),

	BALG_04("Hold Flag already not available against the loan."),

	BALG_05("[Action] is mandatory.");

	private String description;

	private BlockAutoGenLetterUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
