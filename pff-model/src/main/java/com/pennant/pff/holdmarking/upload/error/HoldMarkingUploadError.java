package com.pennant.pff.holdmarking.upload.error;

public enum HoldMarkingUploadError {

	HM_01("Loan Reference is In-valid."),

	HM_02("Action Possible values should be (H/R) Only."),

	HM_03("Hold Marking Details not available against the loan."),

	HM_05("Type is mandatory."),

	HM_06("Amount should not be negative.");

	private String description;

	private HoldMarkingUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
