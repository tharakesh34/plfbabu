package com.pennant.backend.util;

import java.math.BigDecimal;

public class FacilityConstants {
	
	private FacilityConstants() {
		super();
	}
	
	public static final String 	MODULE_NAME   										= "Facility";
	
	// Customer Relation Types
	public static final String CUSTRELATION_CONNECTED 								= "C";
	public static final String CUSTRELATION_RELATED 								= "R";
	public static final String CUSTRELATION_NOTRELATED 								= "N";
	
	public static final String FACILITY_CORPORATE 									= "CORPFAC";
	public static final String FACILITY_COMMERCIAL 									= "COMRFAC";
	public static final String FACILITY_NEW 										= "NEW";
	public static final String FACILITY_REVIEW 										= "RVW";
	public static final String FACILITY_AMENDMENT 									= "AMD";
	public static final String FACILITY_PRESENTING_UNIT 							= "Al Hilal Bank";
	public static final String FACILITY_BOOKING_COMM_UNIT 							= "Commercial Branch";
	public static final String FACILITY_BOOKING_CORP_UNIT 							= "IBD Branch";
	
	public static final String MONTH 												= " Months";
	public static final String YEAR 												= " Year";
	
	public static final String CREDITREVIEW_BANK_TOTASST 							= "TOTASST";
	public static final String CREDITREVIEW_BANK_TOTLIBNETWRTH 						= "TOTLIABANETWORTH";
	public static final String CREDITREVIEW_CORP_TOTASST 							= "TOTAST";
	public static final String CREDITREVIEW_CORP_TOTLIBNETWRTH 						= "TOTLBLNW";

	public static final String CREDITREVIEW_AUDITED 								= "Aud";
	public static final String CREDITREVIEW_UNAUDITED 								= "UnAud";
	public static final String CREDITREVIEW_MNGRACNTS 								= "ManAcc";
	public static final String CREDITREVIEW_QUALIFIED 								= "Qual";
	public static final String CREDITREVIEW_UNQUALIFIED 							= "UnQual";
	public static final String CREDITREVIEW_REMARKS 								= "R";
	public static final String CREDITREVIEW_GRP_CODE_TRUE 							= "TRUE";
	public static final String CREDITREVIEW_CALCULATED_FIELD 						= "Calc";
	public static final String CREDITREVIEW_ENTRY_FIELD 							= "Entry";
	public static final String CREDITREVIEW_CONSOLIDATED 							= "Consolidated";
	public static final String CREDITREVIEW_UNCONSOLIDATED 							= "UnConsolidated";

	// Facility Transaction Types
	public static final String FACILITY_TRAN_SYNDIACTION 							= "S";
	public static final String FACILITY_TRAN_DIRECT_OR_BILATERAL	 				= "D";
	public static final String FACILITY_TRAN_CLUBDEAL 								= "C";
	public static final String FACILITY_TRAN_OTHER 									= "O";
	
	// Facility Level of Approval
	public static final String FACILITY_LOA_CEO 									= "CEO";
	public static final String FACILITY_LOA_COMM_BANKING_CREDIT_COMMITTEE 			= "CBCC";
	public static final String FACILITY_LOA_CREDIT_COMMITTEE 						= "CC";
	public static final String FACILITY_LOA_EXECUTIVE_COMMITTEE 					= "EXCOM";
	public static final String FACILITY_LOA_BOARD_OF_DIRECTORS 						= "BOD";
	
	public static final BigDecimal BD_500_K 										= BigDecimal.valueOf(500000);
	public static final BigDecimal USD_5_M 											= BigDecimal.valueOf(5000000);
	public static final BigDecimal USD_15_M 										= BigDecimal.valueOf(15000000);
	public static final BigDecimal Tenor_5_Years 									= BigDecimal.valueOf(5.0);
	public static final BigDecimal Tenor_7_Years 									= BigDecimal.valueOf(7.0);
	public static final BigDecimal Tenor_10_Years 									= BigDecimal.valueOf(10.0);
	
	// Credit Review Division
	public static final String CREDIT_DIVISION_COMMERCIAL	 						= "COMM";
	public static final String CREDIT_DIVISION_CORPORATE 							= "CORP";

	public static final int CREDIT_REVIEW_USD_SCALE 								= 2;
	public static final String CORP_CRDTRVW_RATIOS_WRKCAP 							= "WRKCAP";
	public static final String CORP_CRDTRVW_RATIOS_EBITDA4 							= "EBITDA4";
	public static final String CORP_CRDTRVW_RATIOS_FCF 								= "FCF";
	
}
