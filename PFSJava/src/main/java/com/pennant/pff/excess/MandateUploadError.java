package com.pennant.pff.excess;

public enum MandateUploadError {

	MANUP_001("For SI Mandate, External Mandate is not required."),

	MANUP_002("For SI Mandate, Swap Mandate is not required."),

	MANUP_003("For SI Mandate, Open Mandate is not required."),

	MANUP_004("For SI Mandate, Default Mandate is not required."),

	MANUP_005("For SI Mandate, Security Mandate is not required."),

	MANUP_006("For DAS Mandate, External Mandate is not required. "),

	MANUP_008("For DAS Mandate, open Mandate is not required."),

	MANUP_009("For DAS Mandate, default Mandate is not required."),

	MANUP_010("For DAS Mandate, security Mandate is not required."),

	MANUP_011("Invalid Data, possible Values are T or F for security Mandate."),

	MANUP_012("Invalid Data, possible Values are T or F for default Mandate."),

	MANUP_013("Invalid Data, possible Values are T or F for open Mandate"),

	MANUP_014("Invalid Data, possible Values are T or F for swap Mandate"),

	MANUP_015("Invalid Data, possible Values are T or F for external Mandate");

	private String description;

	private MandateUploadError(String description) {
		this.description = description;
	}

	String code() {
		return this.name();
	}

	public String description() {
		return description;
	}

}
