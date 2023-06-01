package com.pennant.pff.noc.upload.error;

public enum LoanLetterUploadError {
	LOAN_LTR_01("Enter valid loan reference."),

	LOAN_LTR_02("Allowed values For LETTERTYPE are NOC/CLOSURE/CANCELLATION."),

	LOAN_LTR_03("Allowed values For Mode are EMAIL/COURIER"),

	LOAN_LTR_04("Allowed values for Waiver charges are Y/N"),

	LOAN_LTR_05("Active loans are not eligible for issuance for NOC or Closure Letter or Cancellation Letter."),

	LOAN_LTR_06("Letter type {NOC/CLOSURE} are not allowed for 'CANCELLED LOANS'."),

	LOAN_LTR_07("Cancellation letter is not allowed for this LOAN."),

	LOAN_LTR_08("Specified LETTERTYPE is not mapped in 'LOAN TYPE LETTER MAPPING' master."),

	LOAN_LTR_09("Waiver charges is not configured for the loan"),

	LOAN_LTR_10("Loan reference with 'CANCEL AND REBOOK' are not allowed to issue letter"),

	LOAN_LTR_11("Duplicate data found with loan reference on same date"),

	LOAN_LTR_12("Not allowed to generate CLOSELTR for this loan, {CANCELLATION} is already generated"),

	LOAN_LTR_13("Not allowed to generate CANCLLTR for this loan, {CLOSURE} is already generated");

	private String description;

	private LoanLetterUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}