package com.pennant.pff.noc.service.impl;

public enum BulkGenerateLetterUploadErrorCode {
	NOC01(" Enter Valid Loan Reference "),

	NOC02(" Allowed Values For {LETTERTYPE} are CLOSURE/CANCEL LETTER/NOC "),

	NOC03(" Allowed Values For {MODE} are EMAIL/COURIER "),

	NOC04(" Allowed Values For {WAIVERCHARGES} are Y/N "),

	NOC05(" Active Loans not eligible for issuance for NOC or Closure Letter or Cancellation Letter "),

	NOC06(" {LETTERTYPE} NOC/CLOSURE  are not allowed for 'CANCELLED LOANS' "),

	NOC07(" {LETTERTYPE} CANCELLETTER is not allowed for this 'LOAN' "),

	NOC08(" Specified {LETTERTYPE} is not mapped in 'LOAN TYPE LETTER MAPPING' "),

	NOC09(" {WAIVERCHARGES} for specified LOAREFERENCE is not configured"),

	NOC10(" {LOANREFERENCE} with 'CANCEL AND REBOOK' are not allowed to issue letter"),

	NOC11(" LOANREFERENCE/LETTERTYPE/WAIVERCHARGES/MODE Duplicate data found");

	private String description;

	private BulkGenerateLetterUploadErrorCode(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}
}