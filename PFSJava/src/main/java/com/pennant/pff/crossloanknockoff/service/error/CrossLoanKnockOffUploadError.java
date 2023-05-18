package com.pennant.pff.crossloanknockoff.service.error;

import java.util.Arrays;
import java.util.List;

public enum CrossLoanKnockOffUploadError {

	CLKU_001("From and To Loan reference's should not be empty"),

	CLKU_002("Excess Type should not be empty"),

	CLKU_003("Excess Type should be always E(Excess) or P(Payable)"),

	CLKU_004("Excess Amount should not be empty"),

	CLKU_005("Allocation should not be empty"),

	CLKU_006("Allocation type should be always A(auto) or M(manual)"),

	CLKU_007("Allocations should not be give for allocation type A(auto)"),

	CLKU_008("In valid From Loan Reference"),

	CLKU_009("In Valid To Loan Reference"),

	CLKU_010("Customer should be same for From and To Loan reference's"),

	CLKU_011("Excess amount and sum of allocations amount should be equal"),

	CLKU_012("Reciept already in process for provoided To Loan Reference"),

	CLKU_013("Excess amount not available for the given From Loan Reference"),

	CLKU_014("Excess Amount should be less than/equal to the Balance Excess amount"),

	CLKU_015("Only Active and Write off loans are allowed for To Loan Reference"),

	CLKU_016("Write off loans not allowed for From Loan Reference"),

	CLKU_017("Fee Type header not matching with fee type codes in the system"),

	CLKU_018("Invalid From and To Loan reference's"),

	CLKU_019("Manual Advise is not found."),

	CLKU_020("Its not a payable advise"),

	CLKU_021("FeeTypeCode is mandatory if Excess Type is P(Payable)"),

	CLKU_022("Either principal or interest amount should be given or only emi"),

	CLKU_023("From Loan Reference is not valid."),

	CLKU_024("To Loan Reference is not valid."),

	CLKU_025("From Fin Reference is not in active."),

	CLKU_026("To Fin Reference is not in active."),

	CLKU_027("From Fin Reference is already cancelled, cross loan knockoff is not allowed.");

	private String description;

	private CrossLoanKnockOffUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		CrossLoanKnockOffUploadError error = getError(errorCode);

		if (error == null) {
			return false;
		}

		return true;
	}

	private static CrossLoanKnockOffUploadError getError(String errorCode) {
		List<CrossLoanKnockOffUploadError> list = Arrays.asList(CrossLoanKnockOffUploadError.values());

		for (CrossLoanKnockOffUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}

}
