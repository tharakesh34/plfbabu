package com.pennant.pff.writeoffupload.exception;

import java.util.Arrays;
import java.util.List;

public enum WriteOffUploadError {
	WOUP001("Reference should not be empty."),

	WOUP002("Reference is not valid or not matched with Entity code."),

	WOUP003("Reference is not in active."),

	WOUP004("Not allowed to maintain the Reference due to previous Presentments/Receipts are in process."),

	WOUP005("Excess/Payable available! Knock off same and then initiate write off"),

	WOUP007("Not allowed to do Write Off, because loan is Cancelled/Earlysettled/Inactive."),

	WOUP008("This Loan Reference is already Write-Off."),

	WOUP009("Duplicate Record is found in File for the Reference."),

	WOUP0010("This Loan Reference is under maintainance in Write-Off screen."),

	WOUP0011("Not allowed to do Reverse Write Off, because loan is Cancelled/Earlysettled/Inactive."),

	WOUP0012("This Reference is not a Write-Off loan.");

	private String description;

	private WriteOffUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		WriteOffUploadError error = getError(errorCode);

		if (error == null) {
			return false;
		}

		return true;
	}

	private static WriteOffUploadError getError(String errorCode) {
		List<WriteOffUploadError> list = Arrays.asList(WriteOffUploadError.values());

		for (WriteOffUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}
