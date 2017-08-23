/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  CalculationConstants.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.constants;

public class CalculationConstants {

	public CalculationConstants() {
		super();
	}

	public static final String	IDB_30U360							= "30U/360";
	public static final String	IDB_30E360							= "30E/360";
	public static final String	IDB_30E360I							= "30E/360I";
	public static final String	IDB_30EP360							= "30E+/360";
	public static final String	IDB_ACT_ISDA						= "A/A_ISDA";
	public static final String	IDB_ACT_365FIXED					= "A/A_365F";
	public static final String	IDB_ACT_360							= "A/A_360";
	public static final String	IDB_ACT_365LEAP						= "A/A_365L";
	public static final String	IDB_ACT_365LEAPS					= "A/A365LS";

	// Schedule Calculations constants
	public static final String	SCHMTHD_EQUAL						= "EQUAL";
	public static final String	SCHMTHD_MAN_PRI						= "MAN_PRI";
	public static final String	SCHMTHD_MANUAL						= "MANUAL";
	public static final String	SCHMTHD_PRI							= "PRI";
	public static final String	SCHMTHD_PRI_PFT						= "PRI_PFT";
	public static final String	SCHMTHD_PFT							= "PFT";
	public static final String	SCHMTHD_NOPAY						= "NO_PAY";
	public static final String	SCHMTHD_GRCENDPAY					= "GRCNDPAY";

	//Reducing, Flat, Convert FLat to Reducing, At Maturity, Discounted Deal
	public static final String	RATE_BASIS_R						= "R";
	public static final String	RATE_BASIS_F						= "F";
	public static final String	RATE_BASIS_C						= "C";
	public static final String	RATE_BASIS_M						= "M";
	public static final String	RATE_BASIS_D						= "D";

	public static final String	SCH_SPECIFIER_REPAY					= "R";
	public static final String	SCH_SPECIFIER_GRACE					= "G";
	public static final String	SCH_SPECIFIER_GRACE_END				= "E";
	public static final String	SCH_SPECIFIER_MATURITY				= "M";
	public static final String	SCH_SPECIFIER_TOTAL					= "T";
	public static final String	SCH_SPECIFIER_SELECT				= "S";

	public static final int		FRQ_YEARLY							= 1;
	public static final int		FRQ_HALF_YEARLY						= 2;
	public static final int		FRQ_QUARTERLY						= 4;
	public static final int		FRQ_BIMONTHLY						= 6;
	public static final int		FRQ_MONTHLY							= 12;
	public static final int		FRQ_FORTNIGHTLY						= 24;
	public static final int		FRQ_BIWEEKLY						= 26;
	public static final int		FRQ_WEEKLY							= 52;
	public static final int		FRQ_DAILY							= 365;

	// Repayment calculation types
	public static final String	RPYCHG_CURPRD						= "CURPRD";
	public static final String	RPYCHG_TILLMDT						= "TILLMDT";
	public static final String	RPYCHG_ADJMDT						= "ADJMDT";
	public static final String	RPYCHG_TILLDATE						= "TILLDATE";
	public static final String	RPYCHG_ADDTERM						= "ADDTERM";
	public static final String	RPYCHG_ADJTERMS						= "ADJTERMS";
	public static final String	RPYCHG_ADDRECAL						= "ADDRECAL";
	public static final String	RPYCHG_STEPPOS						= "STEPPOS";

	// Schedule Date Event Flags
	public static final int		SCHDFLAG_PFT						= 0;
	public static final int		SCHDFLAG_RVW						= 1;
	public static final int		SCHDFLAG_CPZ						= 2;
	public static final int		SCHDFLAG_RPY						= 3;

	public static final String	EARLYPAY_NOEFCT						= "NOEFCT";
	public static final String	EARLYPAY_EMIINADV					= "EMIADV";
	public static final String	EARLYPAY_ADJMUR						= "ADJMUR";
	public static final String	EARLYPAY_ADMPFI						= "ADMPFI";
	public static final String	EARLYPAY_RECRPY						= "RECRPY";
	public static final String	EARLYPAY_RECPFI						= "RECPFI";

	// FINANCE FEE SCHEDULE METHODS
	public static final String	REMFEE_PART_OF_DISBURSE				= "DISB";
	public static final String	REMFEE_PART_OF_SALE_PRICE			= "POSP";
	public static final String	REMFEE_SCHD_TO_FIRST_INSTALLMENT	= "STFI";
	public static final String	REMFEE_SCHD_TO_ENTIRE_TENOR			= "STET";
	public static final String	REMFEE_SCHD_TO_N_INSTALLMENTS		= "STNI";
	public static final String	REMFEE_PAID_BY_CUSTOMER				= "PBCU";
	public static final String	REMFEE_WAIVED_BY_BANK				= "WVEB";

	// PAST DUE PROFIT CALCULATION METHODS
	public static final String	PDPFTCAL_NOTAPP						= "N";
	public static final String	PDPFTCAL_SCHRATE					= "S";
	public static final String	PDPFTCAL_SCHRATEMARGIN				= "M";

	public static final String	FIN_STATE_NORMAL					= "N";
	public static final String	FIN_STATE_PD						= "PD";

	public static final String	COMPARE_PRI							= "P";
	public static final String	COMPARE_PFT							= "I";
	public static final String	COMPARE_REPAY						= "R";
	public static final String	COMPARE_CLOSEBAL					= "C";

	//Rate Review
	public static final String	RATEREVIEW_RVWUPR					= "RVWUPR";
	public static final String	RATEREVIEW_RVWALL					= "RVWALL";
	public static final String	RATEREVIEW_NORVW					= "NORVW";

	public static final String	TDS_PERCENTAGE						= "TDS_PERCENTAGE";
	public static final String	TDS_ROUNDINGMODE					= "TDS_ROUNDINGMODE";
	public static final String	TDS_ROUNDINGTARGET					= "TDS_ROUNDINGTARGET";
}
