package com.pennant.pff.paymentupload.exception;

import java.util.Arrays;
import java.util.List;

public enum PaymentUploadError {
	REFUP001("Fin Reference should not be empty."),

	REFUP002("Fin Reference is not valid."),

	REFUP003("Fin Reference is not in active."),

	REFUP004("Excess Type is not valid."),

	REFUP005("Fee Type is not valid"),

	REFUP006("Payment instruction already in progress for Loan"),

	REFUP007("Payable Advises are not found for the Loan Reference"),

	REFUP008("Receipt Amount Should not be greater than Excess Available Amount"),

	REFUP009("Dues Available for knock off"),

	REFUP011("Fee Refund already in progress for Loan"),

	REFUP012("Fee Type is not valid if Excess Type is E"),

	// Hold Refund Errors
	HOLDUP001("Fin Reference is Mandatory"),

	HOLDUP002("Hold Status is Mandatory"),

	HOLDUP003("When Hold Flag is true, Reason is Mandatory"),

	HOLDUP004("Hold Flag already exists against the Loan"),

	HOLDUP005("Hold Flag is not available against the loan"),

	HOLDUP006("Records already exists in the Upload Queue"),

	HOLDUP007("Reason Code is not valid"),

	HOLDUP008("Reason Code not applicable while removing Hold flag"),

	HOLDUP009("FinReference is not valid"),

	HOLDUP0010("HoldStatus is not valid"),

	HOLDUP0011("Fin Reference is not in active.");

	private String description;

	private PaymentUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		PaymentUploadError error = getError(errorCode);

		if (error == null) {
			return false;
		}

		return true;
	}

	private static PaymentUploadError getError(String errorCode) {
		List<PaymentUploadError> list = Arrays.asList(PaymentUploadError.values());

		for (PaymentUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}
