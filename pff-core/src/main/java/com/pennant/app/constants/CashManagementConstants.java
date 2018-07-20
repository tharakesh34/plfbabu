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
 * FileName    		:  CashManagementConstants.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  19-07-2018															*
 *                                                                  
 * Modified Date    :  19-07-2018															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-07-2018       Pennant	                 0.1                                            * 
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

public class CashManagementConstants {

	public CashManagementConstants() {
		super();
	}

	//Deposit Movement Transaction Types
	public static final String	DEPOSIT_MOVEMENT_CREDIT					= "C";
	public static final String	DEPOSIT_MOVEMENT_DEBIT					= "D";

	//Cheque/DD process Accounting Status
	public static final String	DEPOSIT_CHEQUE_STATUS_APPROVE			= "A";
	public static final String	DEPOSIT_CHEQUE_STATUS_REVERSE			= "R";

	//Deposit Amount Editable
	public static final String	DEPOSIT_AMOUNT_EDIT						= "Y";

	//Deposit Details Events
	public static final String	ACCEVENT_DEPOSIT_TYPE_CASH				= "CASH";
	public static final String	ACCEVENT_DEPOSIT_TYPE_CHEQUE_DD			= "CHQDD";
	public static final String	ACCEVENT_DEPOSIT_TYPE_CHEQUE_DD_REVERSE	= "CHQDDR";

}
