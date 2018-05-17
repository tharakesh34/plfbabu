package com.pennant.backend.util;

public class InsuranceConstants {

	
	private InsuranceConstants() {
		super();
	}
	
	// Calculation Type 
	public static final String CALTYPE_RULE 			= "R";
	public static final String CALTYPE_CON_AMT 			= "C";
	public static final String CALTYPE_PERCENTAGE 		= "P";
	public static final String CALTYPE_PROVIDERRATE		= "I";

	// Calculation Depends on Field for Percentage
	public static final String CALCON_OSAMT 			= "OSAMT";
	public static final String CALCON_FINAMT 			= "FINAMT";
	
	// Default Frequency 
	public static final String DEFAULT_FRQ 				= "M0001";

	// Payment Methods
	public static final String PAYTYPE_SCH_FRQ 			= "SCHFRQ";
	public static final String PAYTYPE_DF_DISB 			= "DFDISB";
	public static final String PAYTYPE_ADD_DISB 		= "ADDDISB";

}
