package com.pennant.pff.holdmarking.upload.error;

public enum HoldMarkingUploadError {

	HM_01("Loan Reference is In-valid."),

	HM_02("Action Possible values should be (H/R) Only."),

	HM_03("Hold Marking Details not available against the loan."),

	HM_05("Type is mandatory."),

	HM_06("Amount should not be negative."),

	HM_07("Amount should not be Empty and should be greater than Zero."),

	HM_08("Release amount should be less than or equal to Hold amount againt loan Reference and Account number");

	private String description;

	private HoldMarkingUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
