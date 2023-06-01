package com.pennant.pff.lpp.service.impl;

import java.util.Arrays;
import java.util.List;

public enum LPPUploadError {
	LPP_01("Either [Loan Reference] or [LoanType]  is mandatory."),

	LPP_02("[Loan Reference] is not valid."),

	LPP_03("[Apply Overdue Penalty] is mandatory."),

	LPP_04("[Apply Overdue Penalty] is not valid, possible value should be either of the following {Y/N}."),

	LPP_05("[Penalty Type] is not valid, possible value should be either of the following {F/A/P/M/D/E}."),

	LPP_06("[Calculated On] is invalid, possible value should be either of the following {STOT/SPRI/SPFT}, when [PenaltyType] is  either of the following {P/D/M/E}."),

	LPP_07("[Amount/percentage] should be greater than ZERO, when PenaltyType either of following {F/A}."),

	LPP_08("[Amount/percentage] should be between {1 to 100}, when PenaltyType either of following {P/D/M/E}."),

	LPP_09("When [Apply Overdue Penalty] is N then the following fields [Amount/Percent, GraceDays, MaxWaiver, AllowWaivers, Calculated On, Include Grace Days, Penalty Type] should  blank."),

	LPP_10("[MaxWaiver] should be between {1 to 100}, when [AllowWaivers] is Y."),

	LPP_11("[MaxWaiver] should be blank, when [AllowWaivers] is N."),

	LPP_12("[Loan Reference] is not active."),

	LPP_13("[Loan Type] is not valid."),

	LPP_14("[Apply To Existing Loans] is not valid, possible value should be either of the following {Y/N}."),

	LPP_15("[Grace Days] should be between {0 to 999}."),

	LPP_16("[Grace Days] should be blank, when [Include Grace Days] value is N"),

	LPP_17("[Apply To Existing Loans] should be blank, when [Loan Reference] is not blank."),

	LPP_18("[MaxWaiver] should not be blank, when [AllowWaivers] is Y."),

	LPP_19("[Include Grace Days] is not valid, possible value should be either of the following {Y/N}."),

	LPP_20("[AllowWaivers] is not valid, possible value should be either of the following {Y/N}."),

	LPP_21("[MaxWaiver] should be 0, when [AllowWaivers] is N"),

	LPP_22("[Calculated On] should be blank, when [Penalty Type] either of following {F/A}."),

	LPP_23("[Calculated On] should not blank, when [PenaltyType] either of following {P/D/M/E}."),

	LPP_24("Not allowed to maintain the LAN as it is already initiated."),

	LPP_25("[Calculated On] is invalid, possible value should be {INST}, when PenaltyType either of following {P/M}."),

	LPP_26("[Minimum Amount] should not be Blank, when [Penalty Type] either of following {P/M}."),

	LPP_27("[Include Grace Days] should be N, when [PenaltyType] either of following {F/A/P/M}."),

	LPP_28("Not allowed to maintain the [Loan Type] as it is in under maintenance."),

	LPP_29("Not allowed to maintain the duplicate [Loan Type].");

	private String description;

	private LPPUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		LPPUploadError error = getError(errorCode);

		if (error == null) {
			return false;
		}

		return true;
	}

	private static LPPUploadError getError(String errorCode) {
		List<LPPUploadError> list = Arrays.asList(LPPUploadError.values());

		for (LPPUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}
}