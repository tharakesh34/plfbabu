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

import org.apache.commons.lang3.StringUtils;

public class AccountEventConstants {

	public AccountEventConstants() {
		super();
	}

	public static final String ACCEVENT_ADDDBS = "ADDDBS";
	public static final String ACCEVENT_ADDDBSF = "ADDDBSF";
	public static final String ACCEVENT_ADDDBSN = "ADDDBSN";
	public static final String ACCEVENT_ADDDBSP = "ADDDBSP";
	public static final String ACCEVENT_AMZ = "AMZ";
	public static final String ACCEVENT_AMZ_MON = "AMZ_MON";
	public static final String ACCEVENT_AMZSUSP = "AMZSUSP";
	public static final String ACCEVENT_AMZPD = "AMZPD";
	public static final String ACCEVENT_CMTDISB = "CMTDISB";
	public static final String ACCEVENT_CMTRPY = "CMTRPY";
	public static final String ACCEVENT_COMPOUND = "COMPOUND";
	public static final String ACCEVENT_DEFFRQ = "DEFFRQ";
	public static final String ACCEVENT_DEFRPY = "DEFRPY";
	public static final String ACCEVENT_EARLYPAY = "EARLYPAY";
	public static final String ACCEVENT_EARLYSTL = "EARLYSTL";
	public static final String ACCEVENT_GRACEEND = "GRACEEND";
	public static final String ACCEVENT_INSTDATE = "INSTDATE";
	public static final String ACCEVENT_LATEPAY = "LATEPAY";
	public static final String ACCEVENT_PIS_NORM = "PIS_NORM";
	public static final String ACCEVENT_NORM_PD = "NORM_PD";
	public static final String ACCEVENT_NORM_PIS = "NORM_PIS";
	public static final String ACCEVENT_PD_NORM = "PD_NORM";
	public static final String ACCEVENT_PD_PIS = "PD_PIS";
	public static final String ACCEVENT_PIS_PD = "PIS_PD";
	public static final String ACCEVENT_MATURITY = "MATURITY";
	public static final String ACCEVENT_MNTCMT = "MNTCMT";
	public static final String ACCEVENT_NEWCMT = "NEWCMT";
	public static final String ACCEVENT_PROVSN = "PROVSN";
	public static final String ACCEVENT_PRVSN_MN = "PRVSN_MN";
	public static final String ACCEVENT_PROVCHG = "PROVCHG";
	public static final String ACCEVENT_RATCHG = "RATCHG";
	public static final String ACCEVENT_REPAY = "REPAY";
	public static final String ACCEVENT_SCDCHG = "SCDCHG";
	public static final String ACCEVENT_STAGE = "STAGE";
	public static final String ACCEVENT_WRITEBK = "WRITEBK";
	public static final String ACCEVENT_WRITEOFF = "WRITEOFF";
	public static final String ACCEVENT_CMTINV_NEW = "COMMINV";
	public static final String ACCEVENT_CMTINV_MAT = "COMMINVM";
	public static final String ACCEVENT_CMTINV_DEL = "COMMINVD";
	public static final String ACCEVENT_CMTINV_SET = "COMMINVS";
	public static final String ACCEVENT_CANCELFIN = "CANFIN";
	public static final String ACCEVENT_AMENDMENT = "AMENDMNT";
	public static final String ACCEVENT_SEGMENT = "SEGCHG";
	public static final String ACCEVENT_LIABILITY = "LIBILITY";
	public static final String ACCEVENT_NOCISSUANCE = "NOCISU";
	public static final String ACCEVENT_TIMELYCLOSURE = "TIMECLS";
	public static final String ACCEVENT_BRANCH_CLOSE = "BRNCLOSE";
	public static final String ACCEVENT_EMIHOLIDAY = "EMIDAY";
	public static final String ACCEVENT_REAGING = "REAGING";
	public static final String ACCEVENT_VAS_ACCRUAL = "VASACRUL";
	public static final String ACCEVENT_VAS_FEE = "VASFEE";
	public static final String ACCEVENT_DEFAULT = "DEFAULT";
	public static final String ACCEVENT_DISBINS = "DISBINS";
	public static final String ACCEVENT_HOLDEMI = "HLDEMI";
	public static final String ACCEVENT_FEEPAY = "FEEPAY";
	public static final String ACCEVENT_FEEREFUND = "FEREFUND";
	public static final String ACCEVENT_MANFEE = "MANFEE";
	public static final String ACCEVENT_PAYMTINS = "PAYMTINS";
	public static final String ACCEVENT_CASHTOPENNANT = "C2P";
	public static final String ACCEVENT_LPPAMZ = "LPPAMZ";
	public static final String ACCEVENT_LPIAMZ = "LPIAMZ";
	public static final String ACCEVENT_CASHTOBANK = "C2B";
	public static final String ACCEVENT_CASHINTRANSIT = "CIT";
	public static final String ACCEVENT_BANKTOCASH = "B2C";
	public static final String ACCEVENT_CHANGETDSAPPICABLE = "CHANGETDS";
	public static final String ACCEVENT_CASHDISBTOCUST = "D2C";
	public static final String ACCEVENT_CHEQUETOBANK = "CHQ2B";
	public static final String ACCEVENT_ASSIGNMENT = "ASSIGN";
	public static final String ACCEVENT_INSADJ = "INSADJ";
	public static final String ACCEVENT_INSPAY = "INSPAY";
	public static final String ACCEVENT_CANINS = "CANINS";
	public static final String ACCEVENT_RECIP = "RECIP";
	public static final String ACCEVENT_WAIVER = "WAIVER"; //For Waivers
	public static final String ACCEVENT_ADVDUE = "ADVDUE";
	public static final String ACCEVENT_OEMSBV = "OEMSBV";
	public static final String ACCEVENT_EXPENSE = "EXPENSE";
	public static final String ACCEVENT_INDAS = "INDAS";
	public static final String ACCEVENT_PRSNT = "PRSNT";
	public static final String ACCEVENT_PRSNTRSP = "PRSNTRSP";
	public static final String PART_CANCELATION = "PARTCAN";
	public static final String ACCEVENT_NLRCPT = "NLRCPT";
	// Category Code Constants
	public static final String EVENTCTG_FINANCE = "F";
	public static final String EVENTCTG_OVERDRAFT = "O";
	public static final String EVENTCTG_GOLD = "G";
	public static final String EVENTCTG_CD = "C";
	
	public static boolean isDisbursementEvent(String eventCode) {
		if (StringUtils.isEmpty(eventCode)) {
			return false;
		}
		return eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBS)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSF)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSN)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSP);
	}

}
