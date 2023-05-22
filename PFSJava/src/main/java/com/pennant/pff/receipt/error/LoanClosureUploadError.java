package com.pennant.pff.receipt.error;

import java.util.Arrays;
import java.util.List;

public enum LoanClosureUploadError {

	LCU_01("Loan reference should not be empty."),

	LCU_02("Loan reference is not valid."),

	LCU_03("Loan reference is not in active."),

	LCU_04("Closure type is not valid."),

	LCU_05("Reason code is not valid.");

	private String description;

	private LoanClosureUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		return getError(errorCode) != null;
	}

	private static LoanClosureUploadError getError(String errorCode) {
		List<LoanClosureUploadError> list = Arrays.asList(LoanClosureUploadError.values());

		for (LoanClosureUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}
