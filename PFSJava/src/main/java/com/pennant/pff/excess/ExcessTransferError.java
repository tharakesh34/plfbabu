package com.pennant.pff.excess;

public enum ExcessTransferError {

	EXT_001("Fin Reference should not be empty."),

	EXT_002("Fin Reference is not valid."),

	EXT_003("Fin Reference is not in active."),

	EXT_004("Transfer amount should be less/equal to Balance Amount"),

	EXT_005("File having duplicate records with same Loan Reference and Amount Type"),

	EXT_006("Excess Transfer From and Excess Transfer To should be different."),

	EXT_007("Invalid Excess Transfer From or Excess Transfer To, possible values are {E/A/T/S}.");

	private String description;

	private ExcessTransferError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

}
