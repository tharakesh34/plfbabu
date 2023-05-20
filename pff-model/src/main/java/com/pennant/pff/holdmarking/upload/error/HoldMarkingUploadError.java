package com.pennant.pff.holdmarking.upload.error;

public enum HoldMarkingUploadError {

	HM_01("Loan Reference is In-valid."),

	HM_02("Action Possible values should be (H/R) Only."),

	HM_03("Flag already exists against the Loan."),

	HM_05("Type is mandatory.");

	private String description;

	private HoldMarkingUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
