package com.pennanttech.pff.npa;

public enum NpaScope {
	LOAN(0, "Loan Level"), CUSTOMER(1, "Primary Customer"), CO_APPLICANT(2, "Primary Customer & Co-Applicants"),
	GUARANTOR(3, "Primary Customer, Co-Applicants & Guarantors");

	private final int scope;
	private final String description;

	private NpaScope(int scope, String description) {
		this.scope = scope;
		this.description = description;
	}

	public int scope() {
		return scope;
	}

	public String descriptin() {
		return description;
	}
}
