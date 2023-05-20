package com.pennant.backend.util;

public class InsuranceConstants {

	private InsuranceConstants() {
		super();
	}

	// Calculation Type
	public static final String CALTYPE_RULE = "R";
	public static final String CALTYPE_CON_AMT = "C";
	public static final String CALTYPE_PERCENTAGE = "P";
	public static final String CALTYPE_PROVIDERRATE = "I";

	// Calculation Depends on Field for Percentage
	public static final String CALCON_OSAMT = "OSAMT";
	public static final String CALCON_FINAMT = "FINAMT";

	// Default Frequency
	public static final String DEFAULT_FRQ = "M0001";

	// Payment Methods
	public static final String PAYTYPE_SCH_FRQ = "SCHFRQ";
	public static final String PAYTYPE_DF_DISB = "DFDISB";
	public static final String PAYTYPE_ADD_DISB = "ADDDISB";

	public static final String RECON_STATUS_AUTO = "A";
	public static final String RECON_STATUS_MANUAL = "M";

	public static final String ISSUED = "ISSUED";
	public static final String DECLINE = "DECLINE";
	public static final String CANCEL = "CANCEL";
	public static final String REJECT = "REJECT";
	public static final String PENDING = "PENDING";
	public static final String DISCREPENT = "DISCREPENT";
	public static final String ACTIVE = "ACTIVE";
	public static final String CANCELLED = "CANCELLED";
	public static final String SURRENDER = "SURRENDER";
	public static final String CLAIM = "CLAIM";
	public static final String MATURED = "MATURED";
	public static final String SUCCESS = "SUCCESS";

}
