package com.pennant.pff.manualknockoff.service.impl;

import java.util.Arrays;
import java.util.List;

public enum ManualKnockOffUploadError {

	MKOU_100("Schedule has been changed"),

	MKOU_101("Fin Reference should not be empty."),

	MKOU_102("Fin Reference is not valid."),

	MKOU_103("Fin Reference is not in active."),

	MKOU_104("Allocation type should not be empty."),

	MKOU_105("Allocation type should be MANUAL or AUTO."),

	MKOU_106("Allocations amount should be given if allocation type is MANUAL."),

	MKOU_107("Allocations amount should not be given if allocation type is AUTO."),

	MKOU_108("Excess type should be 'E'(Excess Amount) OR 'A'(EMI in advance) OR Payable Advise."),

	MKOU_109("Insufficent Balance in Excess Type"),

	MKOU_1010("Receipt amount should be less than or equal to balance amount."),

	MKOU_1011("Manual Advise is not found."),

	MKOU_1012("Its not a payable advise"),

	MKOU_1013("AdviseID is Mandatory when excess type is Payable"),

	MKOU_1014("Either pricipal or interest amount should be given or only emi");

	private String description;

	private ManualKnockOffUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		ManualKnockOffUploadError error = getError(errorCode);

		if (error == null || error == MKOU_100) {
			return false;
		}

		return true;
	}

	private static ManualKnockOffUploadError getError(String errorCode) {
		List<ManualKnockOffUploadError> list = Arrays.asList(ManualKnockOffUploadError.values());

		for (ManualKnockOffUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}
