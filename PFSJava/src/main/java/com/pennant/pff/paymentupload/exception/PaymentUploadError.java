package com.pennant.pff.paymentupload.exception;

import java.util.Arrays;
import java.util.List;

public enum PaymentUploadError {
	REFUP001("Reference should not be empty."),

	REFUP002("Reference is not valid."),

	REFUP003("Reference is not in active."),

	REFUP004("Type of Excess is not valid."),

	REFUP005("FeeType is not valid"),

	REFUP006("Payment instruction already in progress."),

	REFUP007("Payable Advises are not found."),

	REFUP008(" There is no available amount to proceed for Refund."),

	REFUP009("Dues available for knock off"),

	REFUP011("Fee Refund already in progress"),

	REFUP012("Fee Type is not required."),

	REFUP013("Override Overdue value should be either Y/N"),

	HOLDUP001("FinReference is Mandatory"),

	HOLDUP002("HoldStatus is Mandatory"),

	HOLDUP003("When Hold Flag is true, Reason is Mandatory"),

	HOLDUP004("Hold Flag already exists against the Loan"),

	HOLDUP005("Hold Flag is not available against the loan"),

	HOLDUP006("Records already exists in the Upload Queue"),

	HOLDUP007("Reason Code is not valid"),

	HOLDUP008("Reason Code not applicable while removing Hold flag"),

	HOLDUP009("FinReference is not valid"),

	HOLDUP0010("HoldStatus is not valid"),

	HOLDUP0011("Fin Reference is not in active."),

	REFUP014("Duplicate Record is found in File for the Reference and Excess Type"),

	REFUP015("Refunds not allowed for Written off Loan");

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
