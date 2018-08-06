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

import org.apache.log4j.Logger;

import com.pennant.app.core.AccrualService;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;

public class AEAmounts implements Serializable {
	private static final long	serialVersionUID	= 4594615740716296558L;
	private static Logger		logger				= Logger.getLogger(AEAmounts.class);
	private static AccrualService accrualService;

	public AEAmounts() {
		super();
	}

	// -------------------------------------------------------------------------------------------------
	// Processing Schedule Details to fill AmountCode Details DATA
	// -------------------------------------------------------------------------------------------------

	public static AEEvent procAEAmounts(FinanceMain financeMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, String eventCode, Date valueDate, Date schdDate) {
		pftDetail = accrualService.calProfitDetails(financeMain, schdDetails, pftDetail, valueDate);
		return procCalAEAmounts(pftDetail, schdDetails, eventCode, valueDate, schdDate);
	}

	public static AEEvent procCalAEAmounts(FinanceProfitDetail pftDetail,  List<FinanceScheduleDetail> schdDetails, String finEvent, Date valueDate,
			Date dateSchdDate) {
		logger.debug("Entering");

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = new AEAmountCodes();
		
		aeEvent.setFinReference(pftDetail.getFinReference());
		aeEvent.setAccountingEvent(finEvent);
		aeEvent.setPostDate(DateUtility.getAppDate());
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(dateSchdDate);
		aeEvent.setBranch(pftDetail.getFinBranch());
		aeEvent.setCcy(pftDetail.getFinCcy());
		aeEvent.setFinType(pftDetail.getFinType());
		aeEvent.setCustID(pftDetail.getCustId());
		
		// Finance Fields
		amountCodes.setFinType(aeEvent.getFinType());

		// profit
		amountCodes.setPft(pftDetail.getTotalPftSchd());
		amountCodes.setPftAP(pftDetail.getTotalPftPaid());
		amountCodes.setPftAB(pftDetail.getTotalPftBal());
		amountCodes.setCpzTot(pftDetail.getTotalPftCpz());

		//principal
		amountCodes.setPri(pftDetail.getTotalpriSchd());
		amountCodes.setPriAP(pftDetail.getTotalPriPaid());
		amountCodes.setPriAB(pftDetail.getTotalPriBal());

		// Till date Calculation
		// profit
		amountCodes.setPftS(pftDetail.getTdSchdPft());
		amountCodes.setPftSP(pftDetail.getTdSchdPftPaid());
		amountCodes.setPftSB(pftDetail.getTdSchdPftBal());

		// principal
		amountCodes.setPriS(pftDetail.getTdSchdPri());
		amountCodes.setPriSP(pftDetail.getTdSchdPriPaid());
		amountCodes.setPriSB(pftDetail.getTdSchdPriBal());

		//Accural
		amountCodes.setAccrue(pftDetail.getPftAccrued());
		amountCodes.setdAccrue(amountCodes.getAccrue().subtract(pftDetail.getAcrTillLBD()));
		amountCodes.setAccrueS(pftDetail.getPftAccrueSusp());
		amountCodes.setAmz(pftDetail.getPftAmz());
		amountCodes.setAmzS(pftDetail.getPftAmzSusp());
		amountCodes.setdAmz(amountCodes.getAmz().subtract(pftDetail.getAmzTillLBD()));
		
		//-----------------------------------------------------------------------
		//FIXME: PV 23MAR18
		BigDecimal accruedIncome = BigDecimal.ZERO;
		BigDecimal unRealizedIncome = BigDecimal.ZERO;
		
		for (int i = 0; i < schdDetails.size(); i++) {

			if (schdDetails.get(i).getSchDate().compareTo(valueDate) > 0) {
				break;
			}
			accruedIncome = accruedIncome.add(schdDetails.get(i).getProfitCalc());
		}

		if (accruedIncome.compareTo(pftDetail.getAmzTillLBD()) > 0) {
			unRealizedIncome = accruedIncome.subtract(pftDetail.getAmzTillLBD());
		}

		amountCodes.setuAmz(unRealizedIncome);
		//-----------------------------------------------------------------------

		//OD Details
		amountCodes.setODDays(pftDetail.getCurODDays());
		amountCodes.setODInst(pftDetail.getNOODInst());
		amountCodes.setPenaltyDue(pftDetail.getPenaltyDue());
		amountCodes.setPenaltyPaid(pftDetail.getPenaltyPaid());
		amountCodes.setPenaltyWaived(pftDetail.getPenaltyWaived());
		//others
		amountCodes.setPaidInst(pftDetail.getNOPaidInst());
		amountCodes.setDisburse(pftDetail.getDisburse());
		amountCodes.setDownpay(pftDetail.getDownpay());
		amountCodes.setAdvanceEMI(pftDetail.getAdvanceEMI());
		amountCodes.setDaysFromFullyPaid(getNoDays(pftDetail.getFullPaidDate(), valueDate));
		amountCodes.setAccrue(pftDetail.getPftAccrued());
		amountCodes.setAccrueS(pftDetail.getPftAccrueSusp());
		amountCodes.setAmz(pftDetail.getPftAmz());
		amountCodes.setAmzNRM(pftDetail.getPftAmzNormal());
		amountCodes.setAmzPD(pftDetail.getPftAmzPD());

		amountCodes.setAmzS(pftDetail.getPftAmzSusp());
		aeEvent.setAeAmountCodes(amountCodes);
		
		if (amountCodes.getdAmz().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setdAmz(BigDecimal.ZERO);
		}

		if (amountCodes.getuAmz().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setuAmz(BigDecimal.ZERO);
		}

		logger.debug("Leaving");
		return aeEvent;

	}

	private static int getNoDays(Date date1, Date date2) {
		return DateUtility.getDaysBetween(date1, date2);
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

}
