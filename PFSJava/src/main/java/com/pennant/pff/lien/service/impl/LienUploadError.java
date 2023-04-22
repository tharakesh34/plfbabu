package com.pennant.pff.lien.service.impl;

import java.util.Arrays;
import java.util.List;

public enum LienUploadError {

	LUOU_100("Schedule has been changed"),

	LUOU_101("Fin Reference should not be empty."),

	LUOU_102("Fin Reference is not valid."),

	LUOU_103("Fin Reference is not in active."),

	LUOU_104("Action should not be empty."),

	LUOU_105("Status should be MANUAL or AUTO."),

	LUOU_106("Repaymethod is SI"),

	LUOU_107("Lien has been marked by using AUTO cannot mark or demark using upload "),

	LUOU_108("Lien is marked already for the Account number or finreference"),

	LUOU_109("Lien should be marked to Demark the Lien"),

	LUOU_110("Lien is marked cannot mark again"),

	LUOU_111("Lien is demarked cannot demark again"),

	LUOU_112("Account Number cannot be empty"),

	LUOU_113("Source cannot be empty");

	private String description;

	private LienUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		LienUploadError error = getError(errorCode);

		if (error == null || error == LUOU_100) {
			return false;
		}

		return true;
	}

	private static LienUploadError getError(String errorCode) {
		List<LienUploadError> list = Arrays.asList(LienUploadError.values());

		for (LienUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}
