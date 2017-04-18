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
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.PennantConstants;

public class AEAmounts implements Serializable {
	private static final long			serialVersionUID	= 4594615740716296558L;
	private static Logger				logger				= Logger.getLogger(AEAmounts.class);
	private static FinanceSuspHeadDAO	suspHeadDAO;
	private static FinODDetailsDAO		finODDetailsDAO;
	private static CustomerDAO			customerDAO;

	public AEAmounts() {
		super();
	}

	// -------------------------------------------------------------------------------------------------
	// Processing Schedule Details to fill AmountCode Details DATA
	// -------------------------------------------------------------------------------------------------

	public static AEAmountCodes procAEAmounts(FinanceMain financeMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, Date valueDate) {
		calProfitDetails(financeMain, schdDetails, pftDetail, valueDate);
		return procCalAEAmounts(financeMain, pftDetail, valueDate);
	}

	public static AEAmountCodes procCalAEAmounts(FinanceMain financeMain, FinanceProfitDetail pftDetail, Date valueDate) {
		logger.debug("Entering");

		AEAmountCodes aeAmountCodes = new AEAmountCodes();
		aeAmountCodes.setFinReference(financeMain.getFinReference());
		aeAmountCodes.setAstValO(financeMain.getFinAssetValue());
		aeAmountCodes.setAstValC(financeMain.getFinCurrAssetValue());
		aeAmountCodes.setNextRepayRvwDate(financeMain.getNextRepayRvwDate());
		aeAmountCodes.setNextRepayPftDate(pftDetail.getNSchdDate());
		aeAmountCodes.setPftInSusp(pftDetail.isPftInSusp());

		// profit
		aeAmountCodes.setPft(pftDetail.getTotalPftSchd());
		aeAmountCodes.setPftAP(pftDetail.getTotalPftPaid());
		aeAmountCodes.setPftAB(pftDetail.getTotalPftBal());
		aeAmountCodes.setCpzTot(pftDetail.getTotalPftCpz());

		//principal
		aeAmountCodes.setPri(pftDetail.getTotalpriSchd());
		aeAmountCodes.setPriAP(pftDetail.getTotalPriPaid());
		aeAmountCodes.setPriAB(pftDetail.getTotalPriBal());

		// Till date Calculation
		// profit
		aeAmountCodes.setPftS(pftDetail.getTdSchdPft());
		aeAmountCodes.setPftSP(pftDetail.getTdSchdPftPaid());
		aeAmountCodes.setPftSB(pftDetail.getTdSchdPftBal());

		// principal
		aeAmountCodes.setPriS(pftDetail.getTdSchdPri());
		aeAmountCodes.setPriSP(pftDetail.getTdSchdPriPaid());
		aeAmountCodes.setPriSB(pftDetail.getTdSchdPriBal());

		//Accural
		aeAmountCodes.setAccrue(pftDetail.getPftAccrued());
		aeAmountCodes.setAccrueS(pftDetail.getPftAccrueSusp());
		aeAmountCodes.setlAccrue(pftDetail.getAcrTillLBD());
		aeAmountCodes.setAmz(pftDetail.getPftAmz());
		aeAmountCodes.setAmzS(pftDetail.getPftAmzSusp());
		//first Repayments
		aeAmountCodes.setFirstRepayDate(pftDetail.getFirstRepayDate());
		aeAmountCodes.setFirstRepayAmt(pftDetail.getFirstRepayAmt());
		//Last Repayments
		aeAmountCodes.setPrvRpySchDate(pftDetail.getPrvRpySchDate());
		aeAmountCodes.setFinalRepayAmt(pftDetail.getFinalRepayAmt());
		;
		aeAmountCodes.setPrvRpySchPft(pftDetail.getPrvRpySchPft());
		aeAmountCodes.setPrvRpySchPri(pftDetail.getPrvRpySchPri());
		//next Repayments
		aeAmountCodes.setNextRpySchDate(pftDetail.getNSchdDate());
		aeAmountCodes.setNextSchdPri(pftDetail.getNSchdPri());
		aeAmountCodes.setNextSchdPft(pftDetail.getNSchdPft());
		aeAmountCodes.setNextSchdPriBal(pftDetail.getNSchdPriDue());
		aeAmountCodes.setNextSchdPftBal(pftDetail.getNSchdPftDue());
		//OD Details
		aeAmountCodes.setODDays(pftDetail.getCurODDays());
		aeAmountCodes.setODInst(pftDetail.getNOODInst());
		aeAmountCodes.setPftOD(pftDetail.getODProfit());
		aeAmountCodes.setPriOD(pftDetail.getODPrincipal());
		aeAmountCodes.setPenaltyDue(pftDetail.getPenaltyDue());
		aeAmountCodes.setPenaltyPaid(pftDetail.getPenaltyPaid());
		aeAmountCodes.setPenaltyWaived(pftDetail.getPenaltyWaived());
		aeAmountCodes.setFirstODDate(pftDetail.getFirstODDate());
		aeAmountCodes.setPrvODDate(pftDetail.getPrvODDate());
		//Depreciation
		aeAmountCodes.setAccumulatedDepPri(pftDetail.getAccumulatedDepPri());
		aeAmountCodes.setDepreciatePri(pftDetail.getDepreciatePri());
		//others
		aeAmountCodes.setCurFlatRate(pftDetail.getCurFlatRate());
		aeAmountCodes.setCurReducingRate(pftDetail.getCurReducingRate());
		aeAmountCodes.setPaidInst(pftDetail.getNOPaidInst());
		aeAmountCodes.setFullyPaidDate(pftDetail.getFullPaidDate());
		aeAmountCodes.setDisburse(pftDetail.getDisburse());
		aeAmountCodes.setDownpay(pftDetail.getDownpay());
		aeAmountCodes.setPftInAdv(pftDetail.getTotalPftPaidInAdv());
		aeAmountCodes.setDaysFromFullyPaid(getNoDays(pftDetail.getFullPaidDate(), valueDate));
		aeAmountCodes.setTtlTerms(pftDetail.getNOInst());
		aeAmountCodes.setElpTerms(pftDetail.getNOInst() - pftDetail.getFutureInst());

		aeAmountCodes.setAccrue(pftDetail.getPftAccrued());
		aeAmountCodes.setlAccrue(pftDetail.getAcrTillLBD());
		aeAmountCodes.setDAccrue(aeAmountCodes.getlAccrue().subtract(aeAmountCodes.getAccrue()));

		aeAmountCodes.setAccrueS(pftDetail.getPftAccrueSusp());
		aeAmountCodes.setlAccrueS(pftDetail.getAcrSuspTillLBD());
		aeAmountCodes.setDAccrue(aeAmountCodes.getlAccrueS().subtract(aeAmountCodes.getAccrueS()));

		aeAmountCodes.setAmz(pftDetail.getPftAmz());
		aeAmountCodes.setlAmz(pftDetail.getAmzTillLBD());
		aeAmountCodes.setdAmz(aeAmountCodes.getlAmz().subtract(aeAmountCodes.getAmz()));

		aeAmountCodes.setAmzNRM(pftDetail.getPftAmzNormal());
		aeAmountCodes.setlAmzNRM(pftDetail.getAmzTillLBDNormal());
		aeAmountCodes.setdAmz(aeAmountCodes.getlAmzNRM().subtract(aeAmountCodes.getAmzNRM()));

		aeAmountCodes.setAmzPD(pftDetail.getPftAmzPD());
		aeAmountCodes.setlAmzPD(pftDetail.getAmzTillLBDPD());
		aeAmountCodes.setdAmzPD(aeAmountCodes.getlAmzPD().subtract(aeAmountCodes.getAmzPD()));

		aeAmountCodes.setAmzS(pftDetail.getPftAmzSusp());
		aeAmountCodes.setlAmzS(pftDetail.getAmzTillLBDPIS());
		aeAmountCodes.setdAmzS(aeAmountCodes.getlAmzS().subtract(aeAmountCodes.getAmzS()));

		// +++++++++++++++++++
		//		aeAmountCodes.setCpzNxt();
		//		aeAmountCodes.setdAmz();
		//		aeAmountCodes.setnAmz();
		logger.debug("Leaving");
		return aeAmountCodes;

	}

	public static FinanceProfitDetail calProfitDetails(FinanceMain finMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, Date valueDate) {
		logger.debug("Entering");

		String finRef = finMain.getFinReference();
		Date dateSusp = null;

		if (StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			dateSusp = getSuspHeadDAO().getFinSuspDate(finRef);
		}

		if (dateSusp == null) {
			dateSusp = DateUtility.addDays(finMain.getMaturityDate(), 1);
		}

		//Reset Totals
		resetCalculatedTotals(finMain, pftDetail);

		//Calculate Accruals
		//How schdDetails will be empty? OD loans before disbursement?
		if (schdDetails == null || schdDetails.isEmpty()) {
		} else {
			calAccruals(finMain, schdDetails, pftDetail, valueDate, dateSusp);
		}

		//Gross Totals
		calculateTotals(finMain, pftDetail, dateSusp, valueDate);

		return pftDetail;

	}

	private static void calAccruals(FinanceMain finMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, Date valueDate, Date dateSusp) {
		String finState = CalculationConstants.FIN_STATE_NORMAL;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date curSchdDate = null;
		Date nextSchdDate = null;

		for (int i = 0; i < schdDetails.size(); i++) {
			curSchd = schdDetails.get(i);
			curSchdDate = curSchd.getSchDate();

			// Next details: in few cases  there might be schedules present even after the maturity date. ex: when calculating the fees
			if (curSchdDate.compareTo(finMain.getMaturityDate()) == 0 || i == schdDetails.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schdDetails.get(i + 1);
			}

			nextSchdDate = nextSchd.getSchDate();

			//-------------------------------------------------------------------------------------
			//Cumulative Totals
			//-------------------------------------------------------------------------------------
			calCumulativeTotals(pftDetail, curSchd);

			//-------------------------------------------------------------------------------------
			//Till Date and Future Date Totals
			//-------------------------------------------------------------------------------------

			// Till date Calculation
			if (curSchdDate.compareTo(valueDate) <= 0) {
				calTillDateTotals(pftDetail, curSchd);
			} else {
				calNextDateTotals(pftDetail, curSchd);
			}

			//-------------------------------------------------------------------------------------
			//ACCRUAL CALCULATION
			//-------------------------------------------------------------------------------------
			BigDecimal pftAmz = BigDecimal.ZERO;
			BigDecimal pftAmzNormal = BigDecimal.ZERO;
			BigDecimal pftAmzPD = BigDecimal.ZERO;
			BigDecimal acrNormal = BigDecimal.ZERO;

			// Amortization
			if (curSchdDate.compareTo(valueDate) >= 0) {
				// do nothing
			} else if (valueDate.after(curSchdDate) && valueDate.compareTo(nextSchdDate) <= 0) {
				int days = getNoDays(valueDate, curSchdDate);
				int daysInCurPeriod = nextSchd.getNoOfDays();
				pftAmz = nextSchd.getProfitCalc().multiply(new BigDecimal(days))
						.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
			} else {
				pftAmz = nextSchd.getProfitCalc();
			}

			if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) && curSchdDate.compareTo(valueDate) <= 0) {
				if ((!curSchd.isSchPftPaid() || !curSchd.isSchPriPaid())) {
					finState = CalculationConstants.FIN_STATE_PD;
				}
			}

			if (finState.equals(CalculationConstants.FIN_STATE_NORMAL)) {
				pftAmzNormal = pftAmz;
			}

			if (finState.equals(CalculationConstants.FIN_STATE_PD)) {
				// PD Amortization
				if (curSchdDate.after(dateSusp)) {
					// do nothing
				} else if (dateSusp.after(curSchdDate) && dateSusp.compareTo(nextSchdDate) <= 0) {
					int days = getNoDays(dateSusp, curSchdDate);
					int daysInCurPeriod = nextSchd.getNoOfDays();
					pftAmzPD = nextSchd.getProfitCalc().multiply(new BigDecimal(days))
							.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
				} else if (curSchdDate.before(dateSusp)) {
					pftAmzPD = pftAmz.subtract(pftAmzNormal);
				}
			}

			//Accrue Till Suspense
			//At this point the value will be similar to the amortization till suspense date
			if (curSchdDate.compareTo(dateSusp) >= 0) {
				// do nothing
			} else if (dateSusp.compareTo(curSchdDate) > 0 && dateSusp.compareTo(nextSchdDate) <= 0) {
				int days = getNoDays(dateSusp, curSchdDate);
				int daysInCurPeriod = nextSchd.getNoOfDays();
				acrNormal = nextSchd.getProfitCalc().multiply(new BigDecimal(days))
						.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
			} else {
				acrNormal = nextSchd.getProfitCalc();
			}

			pftDetail.setPftAccrueSusp(pftDetail.getPftAccrueSusp().add(acrNormal));

			//Set Amortization for various periods
			pftDetail.setPftAmz(pftDetail.getPftAmz().add(pftAmz));
			pftDetail.setPftAmzNormal(pftDetail.getPftAmzNormal().add(pftAmzNormal));
			pftDetail.setPftAmzPD(pftDetail.getPftAmzPD().add(pftAmzPD));

		}

	}

	private static void calCumulativeTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// profit
		pftDetail.setTotalPftSchd(pftDetail.getTotalPftSchd().add(curSchd.getProfitSchd()));
		pftDetail.setTotalPftCpz(pftDetail.getTotalPftCpz().add(curSchd.getCpzAmount()));
		pftDetail.setTotalPftPaid(pftDetail.getTotalPftPaid().add(curSchd.getSchdPftPaid()));

		// principal
		pftDetail.setTotalpriSchd(pftDetail.getTotalpriSchd().add(curSchd.getPrincipalSchd()));
		pftDetail.setTotalPriPaid(pftDetail.getTotalPriPaid().add(curSchd.getSchdPriPaid()));

		// advised Profit
		pftDetail.setTotalAdvPftSchd(pftDetail.getTotalAdvPftSchd().add(curSchd.getAdvProfit()));
		pftDetail.setTotalRbtSchd(pftDetail.getTotalRbtSchd().add(curSchd.getRebate()));

		//Schedule Information
		if (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) {
			//Installments, Paid and OD
			pftDetail.setNOInst(pftDetail.getNOInst() + 1);

			if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
				pftDetail.setNOPaidInst(pftDetail.getNOPaidInst() + 1);
			}

			//First Repayment Date and Amount
			if (curSchd.getSchDate().compareTo(pftDetail.getFinStartDate()) > 0) {
				if (pftDetail.getFirstRepayDate().compareTo(pftDetail.getFinStartDate()) == 0) {
					pftDetail.setFirstRepayDate(curSchd.getSchDate());
					pftDetail.setFirstRepayAmt(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
				}
			}

		}

		//Final Repayment Amount
		if (curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0) {
			pftDetail.setFinalRepayAmt(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
		}

		if (curSchd.isDisbOnSchDate()) {
			if (pftDetail.getFirstDisbDate().compareTo(pftDetail.getMaturityDate()) == 0) {
				pftDetail.setFirstDisbDate(curSchd.getSchDate());
				pftDetail.setLatestDisbDate(curSchd.getSchDate());
			}

			pftDetail.setLatestDisbDate(curSchd.getSchDate());
		}

		logger.debug("Leaving");
	}

	private static void calTillDateTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// profit
		pftDetail.setTdSchdPft(pftDetail.getTdSchdPft().add(curSchd.getProfitSchd()));
		pftDetail.setTdPftCpz(pftDetail.getTdPftCpz().add(curSchd.getCpzAmount()));
		pftDetail.setTdSchdPftPaid(pftDetail.getTdSchdPftPaid().add(curSchd.getSchdPftPaid()));

		// principal
		pftDetail.setTdSchdPri(pftDetail.getTdSchdPri().add(curSchd.getPrincipalSchd()));
		pftDetail.setTdSchdPriPaid(pftDetail.getTdSchdPftPaid().add(curSchd.getSchdPriPaid()));

		// advised Profit
		pftDetail.setTdSchdAdvPft(pftDetail.getTdSchdAdvPft().add(curSchd.getAdvProfit()));
		pftDetail.setTdSchdRbt(pftDetail.getTdSchdRbt().add(curSchd.getRebate()));

		//Fully paid Date. Fully paid flags will be only used for setting the fully paid date. will not update back in the schedule
		if (curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) <= 0) {
			curSchd.setSchPftPaid(true);
		}

		if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) <= 0) {
			curSchd.setSchPriPaid(true);
		}

		if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
			pftDetail.setFullPaidDate(curSchd.getSchDate());
		}

		if (curSchd.isPftOnSchDate() || curSchd.isPftOnSchDate()) {
			pftDetail.setCurReducingRate(curSchd.getCalculatedRate());
			pftDetail.setPrvRpySchDate(curSchd.getSchDate());
			pftDetail.setPrvRpySchPft(curSchd.getProfitSchd());
			pftDetail.setPrvRpySchPri(curSchd.getPrincipalSchd());

			if (!curSchd.isSchPftPaid() || !curSchd.isSchPriPaid()) {
				pftDetail.setNOODInst(pftDetail.getNOODInst() + 1);
			}
		}

		logger.debug("Leaving");
	}

	private static void calNextDateTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// advance Profit and Principal
		pftDetail.setTotalPftPaidInAdv(pftDetail.getTotalPftPaidInAdv().add(curSchd.getSchdPftPaid()));
		pftDetail.setTotalPriPaidInAdv(pftDetail.getTotalPriPaidInAdv().add(curSchd.getSchdPriPaid()));

		//NEXT Schedule Details
		if (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) {
			if (pftDetail.getNSchdDate().compareTo(pftDetail.getMaturityDate()) != 0) {
				pftDetail.setNSchdDate(curSchd.getSchDate());
				pftDetail.setNSchdPri(curSchd.getPrincipalSchd());
				pftDetail.setNSchdPft(curSchd.getProfitSchd());
				pftDetail.setNSchdPriDue(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
				pftDetail.setNSchdPftDue(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
			}

			pftDetail.setFutureInst(pftDetail.getFutureInst() + 1);
		}

		if (curSchd.getSchDate().compareTo(pftDetail.getMaturityDate()) == 0
				&& pftDetail.getNSchdDate().compareTo(pftDetail.getMaturityDate()) == 0) {
			pftDetail.setNSchdPri(curSchd.getPrincipalSchd());
			pftDetail.setNSchdPft(curSchd.getProfitSchd());
			pftDetail.setNSchdPriDue(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
			pftDetail.setNSchdPftDue(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		}

		logger.debug("Leaving");
	}

	private static void calculateTotals(FinanceMain finMain, FinanceProfitDetail pftDetail, Date dateSusp,
			Date valueDate) {
		logger.debug("Entering");

		pftDetail.setTotalPftBal(pftDetail.getTotalPftSchd().subtract(pftDetail.getTotalPftPaid()));
		pftDetail.setTotalPriBal(pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPriPaid()));
		pftDetail.setTdSchdPftBal(pftDetail.getTdSchdPft().subtract(pftDetail.getTdSchdPftPaid()));
		pftDetail.setTdSchdPriBal(pftDetail.getTdSchdPri().subtract(pftDetail.getTdSchdPriPaid()));

		// Current Flat Rate
		BigDecimal calPart1 = pftDetail.getTotalPftSchd().add(pftDetail.getTotalPftCpz());
		BigDecimal calPart2 = pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPftCpz()).max(new BigDecimal(100));
		BigDecimal daysFactor = CalculationUtil.getInterestDays(pftDetail.getFinStartDate(),
				pftDetail.getMaturityDate(), finMain.getProfitDaysBasis());
		pftDetail.setCurFlatRate(calPart1.divide(calPart2.multiply(daysFactor), 9, RoundingMode.HALF_DOWN));

		// Suspense Amortization
		if (dateSusp.compareTo(pftDetail.getMaturityDate()) <= 0) {
			pftDetail.setPftAmzSusp(pftDetail.getPftAmz().subtract(pftDetail.getPftAmzNormal())
					.subtract(pftDetail.getPftAmzPD()));
			pftDetail.setPftInSusp(true);
		} else {
			pftDetail.setPftInSusp(false);
		}

		pftDetail.setPftAccrued(pftDetail.getPftAmz().subtract(pftDetail.getTotalPftPaid()));

		//Value Equivalent to accrued till the suspended date
		pftDetail.setPftAccrueSusp(pftDetail.getPftAccrueSusp().subtract(pftDetail.getTotalPftPaid()));
		//Value Equivalent accrual after suspended date
		pftDetail.setPftAccrueSusp(pftDetail.getPftAccrued().subtract(pftDetail.getPftAccrueSusp()));

		// OD Details
		if (!StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			FinODDetails finODDetails = getFinODDetailsDAO().getFinODSummary(pftDetail.getFinReference());
			if (finODDetails != null) {
				pftDetail.setODPrincipal(finODDetails.getFinCurODPri());
				pftDetail.setODProfit(finODDetails.getFinCurODPft());
				pftDetail.setPenaltyPaid(finODDetails.getTotPenaltyPaid());
				pftDetail.setPenaltyDue(finODDetails.getTotPenaltyBal());
				pftDetail.setPenaltyWaived(finODDetails.getTotWaived());
				pftDetail.setFirstODDate(finODDetails.getFinODSchdDate());
				pftDetail.setPrvODDate(finODDetails.getFinODTillDate());
				pftDetail.setCurODDays(getNoDays(valueDate, finODDetails.getFinODTillDate()));

				//Workaround solution to avoid another fields in the FinODDetails
				pftDetail.setMaxODDays(finODDetails.getFinCurODDays());
			}

		}

		int tenor = DateUtility.getMonthsBetween(pftDetail.getNSchdDate(), pftDetail.getMaturityDate());
		pftDetail.setRemainingTenor(tenor);

		tenor = DateUtility.getMonthsBetween(pftDetail.getNSchdDate(), pftDetail.getMaturityDate());
		;
		pftDetail.setTotalTenor(tenor);

		logger.debug("Leaving");
	}

	private static void resetCalculatedTotals(FinanceMain finMain, FinanceProfitDetail pftDetail) {

		if (StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			pftDetail.setFinReference(finMain.getFinReference());
			pftDetail.setFinStartDate(finMain.getFinStartDate());
			pftDetail.setCustId(finMain.getCustID());
			pftDetail.setCustCIF(finMain.getCustCIF());
			pftDetail.setFinBranch(finMain.getFinBranch());
			pftDetail.setFinType(finMain.getFinType());
			pftDetail.setFinCcy(finMain.getFinCcy());
			pftDetail.setFinPurpose(finMain.getFinPurpose());
			pftDetail.setFinContractDate(finMain.getFinContractDate());
			pftDetail.setFinApprovedDate(finMain.getFinApprovedDate());
			pftDetail.setFullPaidDate(finMain.getFinStartDate());
			pftDetail.setFinAmount(finMain.getFinAmount());
			pftDetail.setDownPayment(finMain.getDownPayment());
			pftDetail.setFinCommitmentRef(finMain.getFinCommitmentRef());
			pftDetail.setFinCategory(finMain.getProductCategory());
			pftDetail.setFinWorstStatus(finMain.getFinStatus());
		}

		//Miscellaneous Fields
		pftDetail.setLastMdfDate(DateUtility.getAppDate());
		pftDetail.setMaturityDate(finMain.getMaturityDate());
		pftDetail.setFinIsActive(finMain.isFinIsActive());
		pftDetail.setClosingStatus(finMain.getClosingStatus());
		pftDetail.setRepayFrq(finMain.getRepayFrq());
		pftDetail.setFinStatus(finMain.getFinStatus());
		pftDetail.setFinStsReason(finMain.getFinStsReason());
		

		//Setting date for recal purpose
		pftDetail.setFirstRepayDate(pftDetail.getFinStartDate());
		pftDetail.setPrvRpySchDate(pftDetail.getFinStartDate());
		pftDetail.setNSchdDate(pftDetail.getMaturityDate());
		pftDetail.setFirstDisbDate(pftDetail.getMaturityDate());
		pftDetail.setLatestDisbDate(pftDetail.getMaturityDate());
		pftDetail.setLatestRpyDate(finMain.getFinStartDate());
		pftDetail.setFirstODDate(pftDetail.getFinStartDate());
		pftDetail.setPrvODDate(pftDetail.getFinStartDate());
		

		//Interest Calculaiton on Pastdue
		if (StringUtils.equals(finMain.getPastduePftCalMthd(), CalculationConstants.PDPFTCAL_NOTAPP)) {
			pftDetail.setCalPftOnPD(false);
		} else {
			pftDetail.setCalPftOnPD(true);
		}
		pftDetail.setPftOnPDMethod(finMain.getPastduePftCalMthd());
		pftDetail.setPftOnPDMrg(finMain.getPastduePftMargin());

		// profit
		pftDetail.setTotalPftSchd(BigDecimal.ZERO);
		pftDetail.setTotalPftCpz(BigDecimal.ZERO);
		pftDetail.setTotalPftPaid(BigDecimal.ZERO);
		pftDetail.setTotalPftBal(BigDecimal.ZERO);

		pftDetail.setTdSchdPft(BigDecimal.ZERO);
		pftDetail.setTdPftCpz(BigDecimal.ZERO);
		pftDetail.setTdSchdPftPaid(BigDecimal.ZERO);
		pftDetail.setTdSchdPftBal(BigDecimal.ZERO);

		pftDetail.setODProfit(BigDecimal.ZERO);
		pftDetail.setNSchdPft(BigDecimal.ZERO);
		pftDetail.setNSchdPftDue(BigDecimal.ZERO);
		pftDetail.setPrvRpySchPft(BigDecimal.ZERO);

		// principal
		pftDetail.setTotalpriSchd(BigDecimal.ZERO);
		pftDetail.setTotalPriPaid(BigDecimal.ZERO);
		pftDetail.setTotalPriBal(BigDecimal.ZERO);

		pftDetail.setTdSchdPri(BigDecimal.ZERO);
		pftDetail.setTdSchdPriPaid(BigDecimal.ZERO);
		pftDetail.setTdSchdPriBal(BigDecimal.ZERO);

		pftDetail.setODPrincipal(BigDecimal.ZERO);
		pftDetail.setNSchdPri(BigDecimal.ZERO);
		pftDetail.setNSchdPriDue(BigDecimal.ZERO);
		pftDetail.setPrvRpySchPri(BigDecimal.ZERO);

		// advised Profit & Rebate
		pftDetail.setTotalAdvPftSchd(BigDecimal.ZERO);
		pftDetail.setTotalRbtSchd(BigDecimal.ZERO);
		pftDetail.setTdSchdAdvPft(BigDecimal.ZERO);
		pftDetail.setTdSchdRbt(BigDecimal.ZERO);

		//Accruals and amortizations
		pftDetail.setPftAccrued(BigDecimal.ZERO);
		pftDetail.setPftAccrueSusp(BigDecimal.ZERO);
		pftDetail.setPftAmz(BigDecimal.ZERO);
		pftDetail.setPftAmzNormal(BigDecimal.ZERO);
		pftDetail.setPftAmzPD(BigDecimal.ZERO);
		pftDetail.setPftAmzSusp(BigDecimal.ZERO);

		//Interest on PD
		pftDetail.setTotPftOnPD(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDPaid(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDWaived(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDDue(BigDecimal.ZERO);

		//Penalty
		pftDetail.setPenaltyPaid(BigDecimal.ZERO);
		pftDetail.setPenaltyDue(BigDecimal.ZERO);
		pftDetail.setPenaltyWaived(BigDecimal.ZERO);

		
		pftDetail.setTotalPriPaidInAdv(BigDecimal.ZERO);
		pftDetail.setTotalPftPaidInAdv(BigDecimal.ZERO);
		
		//Terms
		pftDetail.setNOInst(0);
		pftDetail.setNOPaidInst(0);
		pftDetail.setNOODInst(0);
		pftDetail.setCurODDays(0);
		pftDetail.setMaxODDays(0);
		pftDetail.setFutureInst(0);
		pftDetail.setRemainingTenor(0);
		pftDetail.setTotalTenor(0);

	}

	/**
	 * Method for Preparation of DataSet object
	 * 
	 * @param financeMain
	 * @param eventCode
	 * @return
	 */
	public static DataSet createDataSet(FinanceMain financeMain, String eventCode, Date dateValueDate, Date dateSchdDate) {

		logger.debug("Entering");
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(financeMain.getFinReference());

		if (("").equals(eventCode)) {
			if (financeMain.getFinStartDate().after(DateUtility.getAppDate())) {
				if (AccountEventConstants.ACCEVENT_ADDDBSF_REQ) {
					dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSF);
				} else {
					dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
				}
			} else {
				dataSet.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
			}
		} else {
			dataSet.setFinEvent(eventCode);
		}

		dataSet.setFinBranch(financeMain.getFinBranch());
		dataSet.setFinCcy(financeMain.getFinCcy());
		dataSet.setPostDate(DateUtility.getAppDate());
		dataSet.setValueDate(dateValueDate);
		dataSet.setSchdDate(dateSchdDate);
		dataSet.setFinType(financeMain.getFinType());
		dataSet.setCustId(financeMain.getCustID());
		dataSet.setDisburseAccount(financeMain.getDisbAccountId());
		dataSet.setRepayAccount(financeMain.getRepayAccountId());
		dataSet.setFinAccount(financeMain.getFinAccount());
		dataSet.setFinCustPftAccount(financeMain.getFinCustPftAccount());
		dataSet.setDisburseAmount(financeMain.getCurDisbursementAmt());
		dataSet.setFinPurpose(financeMain.getFinPurpose());
		dataSet.setCmtReference(financeMain.getFinCommitmentRef());
		dataSet.setFinAmount(financeMain.getFinAmount());
		dataSet.setFinRepayMethod(financeMain.getFinRepayMethod());

		if (financeMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			dataSet.setNewRecord(true);
		} else {
			dataSet.setNewRecord(false);
		}

		dataSet.setDownPayment(financeMain.getDownPayment() == null ? BigDecimal.ZERO : financeMain.getDownPayment());
		dataSet.setDownPayBank(financeMain.getDownPayBank() == null ? BigDecimal.ZERO : financeMain.getDownPayBank());
		dataSet.setDownPaySupl(financeMain.getDownPaySupl() == null ? BigDecimal.ZERO : financeMain.getDownPaySupl());
		dataSet.setDownPayAccount(financeMain.getDownPayAccount());
		dataSet.setFinCancelAc(StringUtils.trimToEmpty(financeMain.getFinCancelAc()));
		dataSet.setFinWriteoffAc(StringUtils.trimToEmpty(financeMain.getFinWriteoffAc()));
		dataSet.setFeeAccountId(StringUtils.trimToEmpty(financeMain.getFeeAccountId()));
		dataSet.setSecurityDeposit(financeMain.getSecurityDeposit() == null ? BigDecimal.ZERO : financeMain
				.getSecurityDeposit());
		dataSet.setDeductFeeDisb(financeMain.getDeductFeeDisb() == null ? BigDecimal.ZERO : financeMain
				.getDeductFeeDisb());
		dataSet.setDeductInsDisb(financeMain.getDeductInsDisb() == null ? BigDecimal.ZERO : financeMain
				.getDeductInsDisb());
		dataSet.setGrcPftChg(financeMain.getTotalGrossGrcPft() == null ? BigDecimal.ZERO : financeMain
				.getTotalGrossGrcPft());
		dataSet.setNoOfTerms(financeMain.getNumberOfTerms() + financeMain.getGraceTerms());

		dataSet.setBpiAmount(financeMain.getBpiAmount());

		// Tenure Calculation
		int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate(), true);
		dataSet.setTenure(months);
		dataSet.setPromotionCode(financeMain.getPromotionCode());
		logger.debug("Leaving");
		return dataSet;

	}

	private static int getNoDays(Date date1, Date date2) {
		return DateUtility.getDaysBetween(date1, date2);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public static FinanceSuspHeadDAO getSuspHeadDAO() {
		return suspHeadDAO;
	}

	public void setSuspHeadDAO(FinanceSuspHeadDAO suspHeadDAO) {
		AEAmounts.suspHeadDAO = suspHeadDAO;
	}

	public static FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		AEAmounts.finODDetailsDAO = finODDetailsDAO;
	}

	public static CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		AEAmounts.customerDAO = customerDAO;
	}
}
