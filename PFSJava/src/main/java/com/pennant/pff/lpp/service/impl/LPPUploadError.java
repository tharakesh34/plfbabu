package com.pennant.pff.lpp.service.impl;

import java.util.Arrays;
import java.util.List;

public enum LPPUploadError {
	LPP01(" Either FinReference or LoanType  is Mandatory "),

	LPP02(" FinReference is not valid "),

	LPP03(" ApplyOverDueOD is Mandatory "),

	LPP04(" ApplyOverDueOD Possible values should be 'Y' or 'N' "),

	LPP05(" PenaltyType Possible values should be 'F','A','P','M','D' Or 'E' "),

	LPP06(" CalculatedOn Possible values should be 'STOT','SPRI' Or 'SPFT'. If you are penalty type captured as 'P','D','M' OR 'E' "),

	LPP07(" Amount Possible values should be '0-9999999' and calculatedon should be blank if you are penalty type captured as 'F' OR 'A' "),

	LPP08(" Percentage Possible values should be '1-100' and calculatedon should not blank if you are penalty type captured as 'P','D','M' OR 'E' "),

	LPP09(" If ApplyOverDueOD captured as 'N' then follwing fields ( AmountOrPercent, GraceDays, MaxWaiver, AllowWaiver, CalculatedOn, IncludeGraceDays, PenaltyType) should  be kept as Blank "),

	LPP10(" MaxWaiver possible values should be '1 - 100 ' if AllowWaiver captured as 'Y' "),

	LPP11(" MaxWaiver should be blank if AllowWaiver captured as 'N' "),

	LPP12(" Fin Reference is not in active."),

	LPP13(" Should give only one Either FinReference or LoanType. "),

	LPP14(" ApplyToExistingLoans Possible values should be 'Y' or 'N' "),

	LPP15(" GraceDays Possible values should be '0 - 999' only' "),

	LPP16(" GraceDays Should be Blank if includegracedays captured as 'N' "),

	LPP17(" It should be blank if you choose loan reference."),

	LPP18(" It should not be blank if allow waiver captured as 'y'.");

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