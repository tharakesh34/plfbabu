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
 * FileName : AccountEventConstants.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.pff.constants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.pff.extension.NpaAndProvisionExtension;

public class AccountingEvent {

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException If the constructor is used to create and initialize a new instance of the
	 *                                declaring class by suppressing Java language access checking.
	 */
	private AccountingEvent() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static final String ADDDBSF = "ADDDBSF";
	public static final String ADDDBSN = "ADDDBSN";
	public static final String ADDDBSP = "ADDDBSP";
	public static final String AMZ = "AMZ";
	public static final String AMZ_MON = "AMZ_REV";
	public static final String AMZSUSP = "AMZSUSP";
	public static final String AMZPD = "AMZPD";
	public static final String CMTDISB = "CMTDISB";
	public static final String CMTRPY = "CMTRPY";
	public static final String COMPOUND = "COMPOUND";
	public static final String DEFFRQ = "DEFFRQ";
	public static final String DEFRPY = "DEFRPY";
	public static final String EARLYPAY = "EARLYPAY";
	public static final String EARLYSTL = "EARLYSTL";
	public static final String GRACEEND = "GRACEEND";
	public static final String INSTDATE = "INSTDATE";
	public static final String LATEPAY = "LATEPAY";
	public static final String PIS_NORM = "PIS_NORM";
	public static final String NORM_PD = "NORM_PD";
	public static final String NORM_PIS = "NORM_PIS";
	public static final String PD_NORM = "PD_NORM";
	public static final String PD_PIS = "PD_PIS";
	public static final String PIS_PD = "PIS_PD";
	public static final String MATURITY = "MATURITY";
	public static final String MNTCMT = "MNTCMT";
	public static final String NEWCMT = "NEWCMT";
	public static final String PROVSN = "PROVSN";
	public static final String PRVSN_MN = "PRVSN_MN";
	public static final String PROVCHG = "PROVCHG";
	public static final String RATCHG = "RATCHG";
	public static final String REPAY = "REPAY";
	public static final String RESTRUCTURE = "RESTRUCT";
	public static final String SCDCHG = "SCDCHG";
	public static final String STAGE = "STAGE";
	public static final String WRITEBK = "WRITEBK";
	public static final String WRITEOFF = "WRITEOFF";
	public static final String CMTINV_NEW = "COMMINV";
	public static final String CMTINV_MAT = "COMMINVM";
	public static final String CMTINV_DEL = "COMMINVD";
	public static final String CMTINV_SET = "COMMINVS";
	public static final String CANCELFIN = "CANFIN";
	public static final String AMENDMENT = "AMENDMNT";
	public static final String SEGMENT = "SEGCHG";
	public static final String EMIHOLIDAY = "EMIDAY";
	public static final String REAGING = "REAGING";
	public static final String VAS_ACCRUAL = "VASACRUL";
	public static final String VAS_FEE = "VASFEE";
	public static final String DISBINS = "DISBINS";
	public static final String HOLDEMI = "HLDEMI";
	public static final String FEEPAY = "FEEPAY";
	public static final String FEEREFUND = "FEREFUND";
	public static final String MANFEE = "MANFEE";
	public static final String PAYMTINS = "PAYMTINS";
	public static final String LPPAMZ = "LPPAMZ";
	public static final String LPIAMZ = "LPIAMZ";
	public static final String CASHTOBANK = "C2B";
	public static final String CASHINTRANSIT = "CIT";
	public static final String BANKTOCASH = "B2C";
	public static final String CHEQUETOBANK = "CHQ2B";
	public static final String INSADJ = "INSADJ";
	public static final String INSPAY = "INSPAY";
	public static final String CANINS = "CANINS";
	public static final String RECIP = "RECIP";
	public static final String WAIVER = "WAIVER";
	public static final String ADVDUE = "ADVDUE";
	public static final String OEMSBV = "OEMSBV";
	public static final String EXPENSE = "EXPENSE";
	public static final String INDAS = "INDAS";
	public static final String PRSNT = "PRSNT";
	public static final String PRSNTRSP = "PRSNTRSP";
	public static final String PART_CANCELATION = "PARTCAN";
	public static final String NLRCPT = "NLRCPT";
	public static final String MANSUB = "MANSUB";
	public static final String CBRET = "CBRET";
	public static final String COL2CSH = "COL2CSH";
	public static final String CSH2BANK = "CSH2BANK";
	public static final String NPACHNG = "NPACHNG";
	public static final String CROSS_LOAN_FROM = "CRSLANFR";
	public static final String CROSS_LOAN_TO = "CRSLANTO";

	// Category Code Constants
	public static final String EVENTCTG_FINANCE = "F";
	public static final String EVENTCTG_OVERDRAFT = "O";
	public static final String EVENTCTG_GOLD = "G";
	public static final String EVENTCTG_CD = "C";
	public static final String EXTRF = "EXTRF";
	public static final String REV_WRITEOFF = "REVWRITE";

	public static final String ADDDBS = "ADDDBS";
	public static final String DEFAULT = "DEFAULT";
	public static final String LIABILITY = "LIABILITY";
	public static final String NOCISSUANCE = "NOCISU";
	public static final String TIMELYCLOSURE = "TIMECLS";
	public static final String BRANCH_CLOSE = "BRNCLOSE";
	public static final String CASHTOPENNANT = "C2P";
	public static final String ASSIGNMENT = "ASSIGN";
	public static final String CHANGETDSAPPICABLE = "CHANGETDS";
	public static final String CASHDISBTOCUST = "D2C";

	public static final String BRNCHG = "BRNCHG";

	public static boolean isDisbursementEvent(String eventCode) {
		if (StringUtils.isEmpty(eventCode)) {
			return false;
		}
		return eventCode.equals(AccountingEvent.ADDDBS) || eventCode.equals(AccountingEvent.ADDDBSF)
				|| eventCode.equals(AccountingEvent.ADDDBSN) || eventCode.equals(AccountingEvent.ADDDBSP);
	}

	public static List<String> getExcludedAccEvents() {
		List<String> excludeEvents = new ArrayList<>();

		if (!ImplementationConstants.ALLOW_ADDDBSF) {
			excludeEvents.add(AccountingEvent.ADDDBSF);
		}

		if (!ImplementationConstants.ALLOW_IND_AS) {
			excludeEvents.add(AccountingEvent.EXPENSE);
			excludeEvents.add(AccountingEvent.INDAS);
		}

		if (!NpaAndProvisionExtension.ALLOW_NPA) {
			excludeEvents.add(AccountingEvent.NPACHNG);
		}

		if (!NpaAndProvisionExtension.ALLOW_PROVISION) {
			excludeEvents.add(AccountingEvent.PROVSN);
			excludeEvents.add(AccountingEvent.PRVSN_MN);
			excludeEvents.add(AccountingEvent.PROVCHG);
		}

		excludeEvents.add(AccountingEvent.BRNCHG);
		
		return excludeEvents;
	}
}
