package com.pennant.pff.bulkfeewaiverupload.service.impl;

public enum BulkFeeWaiverUploadError {

	FWU_001("Loan Reference is not valid."),

	FWU_002("Loan Reference is not in active."),

	FWU_003("Invalid Data, For Fee Type code."),

	FWU_004("Invalid Data, Waiver Amount should be greater than 0."),

	FWU_005("Reciepts are initiated for this Loan Reference."),

	FWU_006("Incorrect Waived Amount provided against the Fee Code for the Loan Reference."),

	FWU_007("Incorrect Fee Code provided against the Loan Reference.");

	private String description;

	private BulkFeeWaiverUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

}