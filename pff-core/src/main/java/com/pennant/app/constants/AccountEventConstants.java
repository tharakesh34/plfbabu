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
 * FileName    		:  AccountEventConstants.java											*                           
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

public class AccountEventConstants {

	public AccountEventConstants() {
		super();
	}

	public static final String	ACCEVENT_ADDDBS			= "ADDDBS";
	public static final String	ACCEVENT_ADDDBSF		= "ADDDBSF";
	public static final String	ACCEVENT_ADDDBSN		= "ADDDBSN";
	public static final String	ACCEVENT_ADDDBSP		= "ADDDBSP";
	public static final String	ACCEVENT_AMZ			= "AMZ";
	public static final String	ACCEVENT_AMZ_MON		= "AMZ_MON";
	public static final String	ACCEVENT_AMZSUSP		= "AMZSUSP";
	public static final String 	ACCEVENT_AMZPD			= "AMZPD";
	public static final String	ACCEVENT_CMTDISB		= "CMTDISB";
	public static final String	ACCEVENT_CMTRPY			= "CMTRPY";
	public static final String	ACCEVENT_COMPOUND		= "COMPOUND";
	public static final String	ACCEVENT_DEFFRQ			= "DEFFRQ";
	public static final String	ACCEVENT_DEFRPY			= "DEFRPY";
	public static final String	ACCEVENT_DPRCIATE		= "DPRCIATE";
	public static final String	ACCEVENT_EARLYPAY		= "EARLYPAY";
	public static final String	ACCEVENT_EARLYSTL		= "EARLYSTL";
	public static final String	ACCEVENT_GRACEEND		= "GRACEEND";
	public static final String	ACCEVENT_INSTDATE		= "INSTDATE";
	public static final String	ACCEVENT_LATEPAY		= "LATEPAY";
	public static final String	ACCEVENT_PIS_NORM		= "PIS_NORM";
	public static final String	ACCEVENT_NORM_PD		= "NORM_PD";
	public static final String	ACCEVENT_NORM_PIS		= "NORM_PIS";
	public static final String	ACCEVENT_PD_NORM		= "PD_NORM";
	public static final String	ACCEVENT_PD_PIS			= "PD_PIS";
	public static final String	ACCEVENT_PIS_PD			= "PIS_PD";
	public static final String	ACCEVENT_MATURITY		= "MATURITY";
	public static final String	ACCEVENT_MNTCMT			= "MNTCMT";
	public static final String	ACCEVENT_NEWCMT			= "NEWCMT";
	public static final String	ACCEVENT_PRGCLAIM		= "PRGCLAIM";
	public static final String	ACCEVENT_PROVSN			= "PROVSN";
	public static final String	ACCEVENT_RATCHG			= "RATCHG";
	public static final String	ACCEVENT_REPAY			= "REPAY";
	public static final String	ACCEVENT_SCDCHG			= "SCDCHG";
	public static final String	ACCEVENT_STAGE			= "STAGE";
	public static final String	ACCEVENT_WRITEBK		= "WRITEBK";
	public static final String	ACCEVENT_WRITEOFF		= "WRITEOFF";
	public static final String	ACCEVENT_CMTINV_NEW		= "COMMINV";
	public static final String	ACCEVENT_CMTINV_MAT		= "COMMINVM";
	public static final String	ACCEVENT_CMTINV_DEL		= "COMMINVD";
	public static final String	ACCEVENT_CMTINV_SET		= "COMMINVS";
	public static final String	ACCEVENT_CANCELFIN		= "CANFIN";
	public static final String	ACCEVENT_AMENDMENT		= "AMENDMNT";
	public static final String	ACCEVENT_SEGMENT		= "SEGCHG";
	public static final String	ACCEVENT_LIABILITY		= "LIBILITY";
	public static final String	ACCEVENT_NOCISSUANCE	= "NOCISU";
	public static final String	ACCEVENT_TIMELYCLOSURE	= "TIMECLS";
	public static final String	ACCEVENT_TAKAFULCLAIM	= "TFCLAIM";
	public static final String	ACCEVENT_BRANCH_CLOSE	= "BRNCLOSE";
	public static final String	THIRDPARTY_TRANSFER		= "THIRDPT";
	public static final String	STUDY_DOCUMENTATION		= "SDFAMZ";
	public static final String	FX_REVALUATION			= "FXREVAL";
	public static final String	ACCEVENT_ROLLOVER		= "ROLLOVER";
	public static final String  ACCEVENT_EMIHOLIDAY   	= "EMIDAY";
	public static final String  ACCEVENT_REAGING 		= "REAGING";
	public static final String  ACCEVENT_VAS_ACCRUAL 	= "VASACRUL";
	public static final String  ACCEVENT_VAS_FEE 		= "VASFEE";	
	public static final String	ACCEVENT_DEFAULT		= "DEFAULT";
	public static final String	ACCEVENT_DISBINS		= "DISBINS";
	public static final String	ACCEVENT_HOLDEMI		= "HLDEMI";
	public static final String	ACCEVENT_FEEPAY			= "FEEPAY";
	public static final String	ACCEVENT_MANFEE			= "MANFEE";
	public static final String	ACCEVENT_PAYMTINS		= "PAYMTINS";
	
	//Deposit Details Events
	public static final String	ACCEVENT_CASHTOBANK				= "C2B";
	public static final String	ACCEVENT_DEPOSIT_TYPE_CASH  	= "CASH";
	public static final String	ACCEVENT_DEPOSIT_TYPE_CHEQUE  	= "CHEQUE";
	public static final String	ACCEVENT_DEPOSIT_TYPE_DD  		= "DD";

}
