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
package com.pennant.datamigration.constants;

import java.math.RoundingMode;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.util.FinanceConstants;

public class MigrationConstants {

	public MigrationConstants() {
		super();
	}

	public static final String SOURCE_ID = "MIGR";

	public static final String REPAY_RATE_BASIS = CalculationConstants.RATE_BASIS_R;
	public static final String BPI_PFT_DAYS_BASIS = CalculationConstants.IDB_ACT_360;
	public static final String BPI_TREATMENT = FinanceConstants.BPI_SCHEDULE;
	public static final String GRC_PFT_DAYS_BASIS = CalculationConstants.IDB_30U360;
	public static final String RPY_PFT_DAYS_BASIS = CalculationConstants.IDB_30U360;
	public static final String RECAL_TYPE = CalculationConstants.RPYCHG_TILLMDT;
	public static final String ROUNDING_MODE = RoundingMode.HALF_UP.name();
	public static final int ROUNDING_TARGET = 100;
}
