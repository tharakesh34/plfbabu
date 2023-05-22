package com.pennant.pff.branchchange.service.impl;

import java.util.Arrays;
import java.util.List;

public enum BranchChangeUploadError {
	BC_01("FinReference is Mandatory."),

	BC_02("FinReference is not valid."),

	BC_03("LAN is InActive state. not applicable for Branch Change."),

	BC_04("Old and New Loan Branch are same."),

	BC_05("Branch Code is not valid."),

	BC_06("Branch Code is Mandatory."),

	BC_07("FinReference is Under Maintainance.");

	private String description;

	private BranchChangeUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		BranchChangeUploadError error = getError(errorCode);

		if (error == null) {
			return false;
		}

		return true;
	}

	private static BranchChangeUploadError getError(String errorCode) {
		List<BranchChangeUploadError> list = Arrays.asList(BranchChangeUploadError.values());

		for (BranchChangeUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}
