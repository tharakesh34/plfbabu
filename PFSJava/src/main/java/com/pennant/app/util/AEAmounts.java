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
 * FileName : AEAmounts.java *
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
package com.pennant.app.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.core.AccrualService;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;

public class AEAmounts implements Serializable {
	private static Logger logger = LogManager.getLogger(AEAmounts.class);
	private static final long serialVersionUID = 4594615740716296558L;
	private static AccrualService accrualService;
	private static AdvancePaymentService advancePaymentService;

	public AEAmounts() {
		super();
	}

	// -------------------------------------------------------------------------------------------------
	// Processing Schedule Details to fill AmountCode Details DATA
	// -------------------------------------------------------------------------------------------------

	public static AEEvent procAEAmounts(FinanceMain financeMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, String eventCode, Date valueDate, Date schdDate) {
		pftDetail = accrualService.calProfitDetails(financeMain, schdDetails, pftDetail, valueDate);
		return procCalAEAmounts(financeMain, pftDetail, schdDetails, eventCode, valueDate, schdDate);
	}

	public static AEEvent procCalAEAmounts(FinanceMain fm, FinanceProfitDetail pfd,
			List<FinanceScheduleDetail> schedules, String finEvent, Date valueDate, Date dateSchdDate) {

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = new AEAmountCodes();
		EventProperties eventProperties = fm.getEventProperties();

		aeEvent.setFinID(pfd.getFinID());
		aeEvent.setFinReference(pfd.getFinReference());
		aeEvent.setAccountingEvent(finEvent);

		if (eventProperties.isParameterLoaded()) {
			aeEvent.setPostDate(eventProperties.getAppDate());
		} else {
			aeEvent.setPostDate(SysParamUtil.getAppDate());
		}

		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(dateSchdDate);
		aeEvent.setBranch(pfd.getFinBranch());
		aeEvent.setCcy(pfd.getFinCcy());
		aeEvent.setFinType(pfd.getFinType());
		aeEvent.setCustID(pfd.getCustId());

		// Finance Fields
		amountCodes.setFinType(aeEvent.getFinType());

		// profit
		amountCodes.setPft(pfd.getTotalPftSchd());
		amountCodes.setPftAP(pfd.getTotalPftPaid());
		amountCodes.setPftAB(pfd.getTotalPftBal());
		amountCodes.setCpzTot(pfd.getTotalPftCpz());

		// principal
		amountCodes.setPri(pfd.getTotalpriSchd());
		amountCodes.setPriAP(pfd.getTotalPriPaid());
		amountCodes.setPriAB(pfd.getTotalPriBal());

		// Till date Calculation
		// profit
		amountCodes.setPftS(pfd.getTdSchdPft());
		amountCodes.setPftSP(pfd.getTdSchdPftPaid());
		amountCodes.setPftSB(pfd.getTdSchdPftBal());

		// principal
		amountCodes.setPriS(pfd.getTdSchdPri());
		amountCodes.setPriSP(pfd.getTdSchdPriPaid());
		amountCodes.setPriSB(pfd.getTdSchdPriBal());

		// Accural
		amountCodes.setAccrue(pfd.getPftAccrued());
		amountCodes.setdAccrue(amountCodes.getAccrue().subtract(pfd.getAcrTillLBD()));
		amountCodes.setAccrueS(pfd.getPftAccrueSusp());
		amountCodes.setAmz(pfd.getPftAmz());
		amountCodes.setAmzS(pfd.getPftAmzSusp());
		amountCodes.setdAmz(amountCodes.getAmz().subtract(pfd.getAmzTillLBD()));
		amountCodes.setdGapAmz(pfd.getGapIntAmz().subtract(pfd.getGapIntAmzLbd()));
		amountCodes.setPrvMthAcr(pfd.getPrvMthAcr());
		amountCodes.setPrvMntAmz(pfd.getPrvMthAmz());

		// LPI Amortization calculation
		if (pfd.getLpiAmount().compareTo(BigDecimal.ZERO) > 0) {
			amountCodes.setdLPIAmz(pfd.getLpiAmount().subtract(pfd.getLpiTillLBD()));

			// Calculate GST Amount on LPI Amount
			if (pfd.getGstLpiAmount().compareTo(BigDecimal.ZERO) > 0) {
				amountCodes.setdGSTLPIAmz(pfd.getGstLpiAmount().subtract(pfd.getGstLpiTillLBD()));
			}
		}

		// LPP Amortization calculation
		if (pfd.getLppAmount().compareTo(BigDecimal.ZERO) > 0) {
			amountCodes.setdLPPAmz(pfd.getLppAmount().subtract(pfd.getLppTillLBD()));

			// Calculate GST Amount on LPP Amount
			if (pfd.getGstLppAmount().compareTo(BigDecimal.ZERO) > 0) {
				amountCodes.setdGSTLPPAmz(pfd.getGstLppAmount().subtract(pfd.getGstLppTillLBD()));
			}
		}

		// -----------------------------------------------------------------------
		// FIXME: PV 23MAR18
		BigDecimal accruedIncome = BigDecimal.ZERO;
		BigDecimal unRealizedIncome = BigDecimal.ZERO;

		for (FinanceScheduleDetail schd : schedules) {

			if (schd.getSchDate().compareTo(valueDate) > 0) {
				break;
			}
			accruedIncome = accruedIncome.add(schd.getProfitCalc());
		}

		if (accruedIncome.compareTo(pfd.getAmzTillLBD()) > 0) {
			unRealizedIncome = accruedIncome.subtract(pfd.getAmzTillLBD());
		}

		amountCodes.setuAmz(unRealizedIncome);

		// -----------------------------------------------------------------------

		// OD Details
		amountCodes.setODDays(pfd.getCurODDays());
		amountCodes.setODInst(pfd.getNOODInst());
		amountCodes.setPenaltyDue(pfd.getPenaltyDue());
		amountCodes.setPenaltyPaid(pfd.getPenaltyPaid());
		amountCodes.setPenaltyWaived(pfd.getPenaltyWaived());
		amountCodes.setOdPri(pfd.getODPrincipal());
		amountCodes.setOdPft(pfd.getODProfit());

		if (pfd.isWriteoffLoan()) {
			amountCodes.setWriteOff(true);
		}

		// others
		amountCodes.setPaidInst(pfd.getNOPaidInst());
		// amountCodes.setDisburse(pftDetail.getDisburse());
		// amountCodes.setDownpay(pftDetail.getDownpay());
		amountCodes.setDownpay(pfd.getDownPayment());
		amountCodes.setAdvanceEMI(pfd.getAdvanceEMI());
		amountCodes.setDaysFromFullyPaid(getNoDays(pfd.getFullPaidDate(), valueDate));
		amountCodes.setAccrue(pfd.getPftAccrued());
		amountCodes.setAccrueS(pfd.getPftAccrueSusp());
		amountCodes.setAmz(pfd.getPftAmz());
		amountCodes.setAmzNRM(pfd.getPftAmzNormal());
		amountCodes.setAmzPD(pfd.getPftAmzPD());

		amountCodes.setAmzS(pfd.getPftAmzSusp());
		aeEvent.setAeAmountCodes(amountCodes);

		if (amountCodes.getdAmz().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setdAmz(BigDecimal.ZERO);
		}

		if (amountCodes.getuAmz().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setuAmz(BigDecimal.ZERO);
		}

		amountCodes.setSvAmount(pfd.getSvAmount());
		amountCodes.setCbAmount(pfd.getCbAmount());

		advancePaymentService.setIntAdvFlag(fm, amountCodes, false);

		// NPA and Provision
		amountCodes.setAmzS(pfd.getPftAmzSusp());
		amountCodes.setAccrueS(pfd.getPftAccrueSusp());

		if (pfd.getCurODDays() > 0 && pfd.getPftAmzSusp().compareTo(BigDecimal.ZERO) > 0) {
			amountCodes.setAmz(BigDecimal.ZERO);
			amountCodes.setAccrue(BigDecimal.ZERO);
		}

		logger.debug("Leaving");
		return aeEvent;

	}

	private static int getNoDays(Date date1, Date date2) {
		return DateUtil.getDaysBetween(date1, date2);
	}

	public void setAccrualService(AccrualService accrualService) {
		AEAmounts.accrualService = accrualService;
	}

	public static void setAdvancePaymentService(AdvancePaymentService advancePaymentService) {
		AEAmounts.advancePaymentService = advancePaymentService;
	}

}
