package com.pennant.pff.presentment.exception;

import java.util.Arrays;
import java.util.List;

public enum PresentmentError {
	PRMNT501("Status should not be empty."),

	PRMNT502("Bounce code should not be blank."),

	PRMNT503("Bounce code is not valid."),

	PRMNT504("Bounce remarks should not be empty."),

	PRMNT505("Bounce Remarks length should be less than or equal to 100."),

	PRMNT506("Presentment response already marked as success."),

	PRMNT507("Presentment response already marked as bounce."),

	PRMNT508("The presentment not proceed with schedule date greater than application bussiness date."),

	PRMNT5010("Receipt not exists to bounce."),

	PRMNT5011("Unable to update the fate correstion as bounce since loan is closed."),

	PRMNT512("Schedule has been changed"),

	REPRMNT513("Fin Reference should not be empty."),

	REPRMNT514("Fin Reference is not valid."),

	REPRMNT515("Fin Reference is not in active."),

	REPRMNT516("Due Date should not be empty."),

	REPRMNT517("Due Date should not be Future Date."),

	REPRMNT518("Not a valid representment."),

	REPRMNT519("Unable to do the re-presenment, since Account is closed."),

	REPRMNT520("Receipt already proceessed for this schedule."),

	REPRMNT521("There is no over dues for this Reference."),

	REPRMNT522("Due date reached the next installment date."),

	REPRMNT523(""),

	FC_601("Fate Correction already proceessed for this schedule."),

	FC_602("Presentment is not found for this respective schedule."),

	FC_603("Clearing status in invalid in Fate Correction Upload File"),

	EX_607("Transfer amount should be less/equal to Balance Amount"),

	EX_608("File having duplicate records with same Loan Reference and Amount Type");

	private String description;

	private PresentmentError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		PresentmentError error = getError(errorCode);

		if (error == null || error == PRMNT512) {
			return false;
		}

		return true;
	}

	private static PresentmentError getError(String errorCode) {
		List<PresentmentError> list = Arrays.asList(PresentmentError.values());

		for (PresentmentError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}

}
