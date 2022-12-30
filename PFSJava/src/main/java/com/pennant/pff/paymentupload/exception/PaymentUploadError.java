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

	REFUP008("Receipt Amount Should not be greater than Excess Available Amount");

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
