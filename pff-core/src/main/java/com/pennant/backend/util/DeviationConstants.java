/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * 
 * FileName : PennantConstants.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.util;

/**
 * This stores all constants required for running the application
 */
public class DeviationConstants {
	
	public DeviationConstants() {
	    super();
    }
	
	public static final boolean MULTIPLE_APPROVAL = true;
	public static final String DT_STRING 		= "S";
	public static final String DT_INTEGER 		= "I";
	public static final String DT_BOOLEAN 		= "B";
	public static final String DT_DECIMAL 		= "D";
	public static final String DT_PERCENTAGE 	= "P";

	public static final String CL_WAIVED 		= "_W";
	public static final String CL_POSTPONED 	= "_P";
	public static final String CL_EXPIRED 		= "_E";
	
	// Deviation categories.
	public static final String CAT_AUTO = "A";
	public static final String CAT_MANUAL = "M";
	public static final String CAT_CUSTOM = "C";	

	public static final String TY_LOAN 			= "LOAN";
	public static final String TY_PRODUCT 		= "PRODUCT";
	public static final String TY_ELIGIBILITY 	= "EILIBILITY";
	public static final String TY_CHECKLIST 	= "CHECKLIST";
	public static final String TY_FEE 			= "FEES";
	public static final String TY_SCORE 		= "SCORE";
	public static final String TY_CUSTOM 		= "CUSTOM";

	public static final String FI_DEVIATION 	= "Deviation";
	public static final String FI_RESULT 		= "Result";
	public static final String NOTES_MODULE		= "FinanceDevaitions";
}
