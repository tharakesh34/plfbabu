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

	public static final String IDB_30U360 = "30U/360";
	public static final String IDB_30E360 = "30E/360";
	public static final String IDB_30E360I = "30E/360I";
	public static final String IDB_30EP360 = "30E+/360";
	public static final String IDB_ACT_ICMS = "A/A_ICMA";
	public static final String IDB_ACT_ISDA = "A/A_ISDA";
	public static final String IDB_ACT_AFB = "A/A_AFB";
	public static final String IDB_ACT_365FIXED = "A/A_365F";
	public static final String IDB_ACT_360 = "A/A_360";
	public static final String IDB_ACT_365LEAP = "A/A_365L";
	
	// Schedule Calculations constants
	public static final String EQUAL = "EQUAL";
	public static final String MAN_PRI = "MAN_PRI";
	public static final String MANUAL = "MANUAL";
	public static final String PRI = "PRI";
	public static final String PRI_PFT = "PRI_PFT";
	public static final String PFT = "PFT";
	public static final String NOPAY = "NO_PAY";

	//Reducing, Flat, Convert FLat to Reducing, At Maturity
	public static final String RATE_BASIS_R = "R";
	public static final String RATE_BASIS_F = "F";
	public static final String RATE_BASIS_C = "C";
	public static final String RATE_BASIS_M = "M";
	public static final String RATE_BASIS_D = "D";
	

	
	public static final String REPAY = "R";
	public static final String GRACE = "G";
	public static final String GRACE_END = "E";
	public static final String MATURITY = "M";
	public static final String TOTAL = "T";
	public static final String SELECT = "S";

	public static final int FRQ_YEARLY = 1;
	public static final int FRQ_HALF_YEARLY = 2;
	public static final int FRQ_QUARTERLY = 4;
	public static final int FRQ_MONTHLY = 12;
	public static final int FRQ_FORTNIGHTLY = 26;
	public static final int FRQ_WEEKLY = 52;
	public static final int FRQ_DAILY = 365;

	// Repayment calculation types
	public static final String RPYCHG_CURPRD = "CURPRD";
	public static final String RPYCHG_TILLMDT = "TILLMDT";
	public static final String RPYCHG_ADJMDT = "ADJMDT";
	public static final String RPYCHG_TILLDATE = "TILLDATE";
	public static final String RPYCHG_ADDTERM = "ADDTERM";
	//public static final String RPYCHG_ADDLAST = "ADDLAST";
	public static final String RPYCHG_ADJTERMS = "ADJTERMS";
	public static final String RPYCHG_ADDRECAL = "ADDRECAL";
	
	// Schedule Date Event Flags
	public static final int SCHDFLAG_PFT = 0;
	public static final int SCHDFLAG_RVW = 1;
	public static final int SCHDFLAG_CPZ = 2;
	public static final int SCHDFLAG_RPY = 3;
	
	// Add Terms After
	public static final String ADDTERM_AFTMDT = "MATURITY";
	public static final String ADDTERM_AFTRPY = "LAST REPAY";
	
	public static final String EARLYPAY_NOEFCT = "NOEFCT";
	public static final String EARLYPAY_ADJMUR = "ADJMUR";
	public static final String EARLYPAY_ADMPFI = "ADMPFI";
	public static final String EARLYPAY_RECRPY = "RECRPY";
	public static final String EARLYPAY_RECPFI = "RECPFI";
	
}
