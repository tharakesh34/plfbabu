package com.pennant.pff.fincancelupload.exception;

import java.util.Arrays;
import java.util.List;

public enum FinCancelUploadError {
	LANCLUP001("Reference should not be empty."),

	LANCLUP002("Reference is not valid."),

	LANCLUP003("Reference is not in active."),

	LANCLUP004("Some one else processed this record."),

	LANCLUP005("Loan cannot be cancelled since First installment was crossed."),

	LANCLUP006("Repayments done on this Finance. Cannot Proceed Further."),

	LANCLUP007("Refunds given for this Finance. Cannot Proceed Further For Cancellation."),

	LANCLUP008("Repayments done on this Finance are in progress. Cannot Proceed Further For Cancellation"),

	LANCLUP009(
			"System will not allow to cancel the loan as all the disbursement instructions of the loan are not cancelled."),

	LANCLUP010("Rescheduling done on this Finance. Cannot Proceed Further For Cancellation."),

	LANCLUP011("Matured loan can not be cancelled."),

	LANCLUP012("Restructuring done for this Finance. Cannot Proceed Further For Cancellation."),

	LANCLUP013("This Finance is Written Off. Cannot Proceed Further For Cancellation."),

	LANCLUP014("Settlement is initiated for this Finance. Cannot Proceed Further For Cancellation"),

	LANCLUP015(
			"Cannot Proceed Further For Cancellation, as Cross Loan Trnasfers are done for this Finance, need to cancel the transfers before cancelling the Finance."),

	LANCLUP016("Duplicate Record is found in File for the Reference"),

	LANCLUP017("Loan is not eligible for cancellation, as cancellation not allowed in loan type"),

	LANCLUP018("Loan Start Date should after %s only allowed for cancellelation."),

	LANCLUP019("InValid Data, For Cancel Type : only C and CR are Allowed");

	private String description;

	private FinCancelUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String getOverrideDescription(FinCancelUploadError error, Object... parameters) {
		return String.format(error.description(), parameters);
	}

	public static boolean isValidation(String errorCode) {
		FinCancelUploadError error = getError(errorCode);

		if (error == null) {
			return false;
		}

		return true;
	}

	private static FinCancelUploadError getError(String errorCode) {
		List<FinCancelUploadError> list = Arrays.asList(FinCancelUploadError.values());

		for (FinCancelUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}
