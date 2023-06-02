package com.pennanttech.pff.receipt.upload;

import java.util.Arrays;
import java.util.List;

public enum ReceiptStatusUploadError {
	RU01(" ReceiptId is Mandatory "),

	RU02(" Enter Valid RECEIPTID "),

	RU03(" Already BOUNCED or CANCELLED Receipts are not allowed "),

	RU04(" Status is Mandatory "),

	RU05(" Status Allowed values are 'R','B' Or 'C' "),

	RU06(" Already Realized receipts are not allowed to Realize again "),

	RU07(" Status B i.e BOUNCE is allowed only when ReceiptMode are CHEQUE or DD "),

	RU08(" REALIZATION DATE is allowed only when RECEIPTMODE is { CHEQUE or DD } "),

	RU09(" REALIZATION DATE is Mandatory in case of RECEIPT MODE { CHEQUE or DD }"),

	RU010(" REALIZATION DATE should not be less than DEPOSITED DATE "),

	RU011(" BOUNCE/CANCEL Reason is Mandatory when STATUS is B or C "),

	RU012(" BOUNCE/CANCEL Reason is allowed only when STATUS is B or C "),

	RU013(" Enter Valid Bounce(or)Reason Code {In case of Status BOUNCE Refer BounceReason Master for BounceCode} "),

	RU014(" Enter Valid Bounce(or)Reason Code {In case of Status CANCEL Refer RejectionDetails Master for RejectCode} "),

	RU015(" Allowed Characters for Bounce(or)Cancel Remarks is 50 "),

	RU016(" BOUNCEDATE is allowed only when the STATUS is BOUNCED "),

	RU017(" BOUNCEDATE is Mandatory when STATUS is B "),

	RU018(" BOUNCEDATE should not be less than REALIZATION DATE "),

	RU019(" BOUNCEDATE should not be greater than APPLICATION DATE "),

	RU020(" NO DATA FOUND with specified receipt"),

	RU021("B/C status codes for already Realized receipts of ES/EP receipt modes's not ALLOWED");

	private String description;

	private ReceiptStatusUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		return getError(errorCode) != null;
	}

	private static ReceiptStatusUploadError getError(String errorCode) {
		List<ReceiptStatusUploadError> list = Arrays.asList(ReceiptStatusUploadError.values());

		for (ReceiptStatusUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}