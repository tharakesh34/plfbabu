package com.pennant.pff.blockautolettergenerate.service.impl;

public enum BlockAutoGenLetterUploadError {

	BALG_01("Loan Reference is In-valid."),

	BALG_02("The Possible Values Should be 'B' and 'U' Only"),

	BALG_03("Flag already exists against the Loan."),

	BALG_04("Hold Flag already not available against the loan.");

	private String description;

	private BlockAutoGenLetterUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
