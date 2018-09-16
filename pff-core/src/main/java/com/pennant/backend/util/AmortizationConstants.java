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
 * FileName    		:  AmortizationConstants.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  24-12-2017															*
 *                                                                  
 * Modified Date    :  24-12-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-12-2017       Pennant	                 0.1                                            * 
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

package com.pennant.backend.util;

/**
 * This stores all constants required for running the application
 */
public class AmortizationConstants {

	// Amortization Rule
	public static final String AMZ_METHOD_RULE = "AMZMTH";

	// Amortization methods
	public static final String AMZ_METHOD_INTEREST = "I";
	public static final String AMZ_METHOD_OPENINGPRIBAL = "P";
	public static final String AMZ_METHOD_STRAIGHTLINE = "S";

	// Amortization Income Types
	public static final String AMZ_INCOMETYPE_FEE = "I";
	public static final String AMZ_INCOMETYPE_EXPENSE = "E";
	public static final String AMZ_INCOMETYPE_MANUALADVISE = "M";

}
