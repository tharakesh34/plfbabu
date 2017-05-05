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
 * FileName : OverDueRecoveryPostingsUtil.java *
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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;

public class AccrualService extends ServiceHelper {

	private static Logger				logger				= Logger.getLogger(AccrualService.class);

	public static FinODDetailsDAO		finODDetailsDAO;
	private static FinExcessAmountDAO	finExcessAmountDAO;
	public static FinanceTypeDAO		financeTypeDAO;
	private static FinanceSuspHeadDAO	suspHeadDAO;
	private static final long			serialVersionUID	= 6161809223570900644L;

	public static final String			accrual				= "SELECT F.FinReference FROM FinanceMain F"
																	+ " WHERE F.FinIsActive = 1 AND F.FinStartDate <=? And F.CustID=? ";

	public CustEODEvent processAccrual(CustEODEvent custEODEvent) throws Exception {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();
		
		for (FinEODEvent finEODEvent : finEODEvents) {
			finEODEvent = calculateAccruals(finEODEvent, valueDate);
		}

		return custEODEvent;

	}

	public FinEODEvent calculateAccruals(FinEODEvent finEODEvent, Date valueDate) throws Exception {
		logger.debug(" Entering ");

		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetailList = finEODEvent.getFinanceScheduleDetails();

		// Finance Profit Details
		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();
		if (profitDetail.getFinReference() == null) {
			profitDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(finMain.getFinReference());
		}

		String finReference = finEODEvent.getFinanceMain().getFinReference();

		FinanceProfitDetail finPftDetail = calProfitDetails(finMain, scheduleDetailList, profitDetail, valueDate);
		String worstSts = getCustomerStatusCodeDAO().getFinanceStatus(finReference, false);
		finPftDetail.setFinWorstStatus(worstSts);

		//post accruals
		postAccruals(finMain, finPftDetail, valueDate);

		finEODEvent.setFinProfitDetail(finPftDetail);
		finEODEvent.setUpdFinPft(true);

		logger.debug(" Leaving ");
		return finEODEvent;
	}

	public static FinanceProfitDetail calProfitDetails(FinanceMain finMain, List<FinanceScheduleDetail> schdDetails,
			FinanceProfitDetail pftDetail, Date valueDate) {
		logger.debug("Entering");

		String finRef = finMain.getFinReference();
		Date dateSusp = null;

		if (!StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			dateSusp = suspHeadDAO.getFinSuspDate(finRef);
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
			pftDetail.setFinWorstStatus(finMain.getFinStatus());
			FinanceType finType = financeTypeDAO.getProductDetails(finMain.getFinType());
			pftDetail.setFinCategory(finType.getFinCategory());
			pftDetail.setProductCategory(finType.getProductCategory());
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

		//Set Excess Amounts
		List<FinExcessAmount> finExcessAmounts = finExcessAmountDAO.getExcessAmountsByRef(pftDetail.getFinReference());
		if (finExcessAmounts.size() > 0) {
			for (int i = 0; i < finExcessAmounts.size(); i++) {
				BigDecimal totBalAvailable = finExcessAmounts.get(i).getAmount()
						.subtract(finExcessAmounts.get(i).getUtilisedAmt());
				BigDecimal reservedAmt = finExcessAmounts.get(i).getReservedAmt();

				if (StringUtils.equals(finExcessAmounts.get(i).getAmountType(), RepayConstants.EXAMOUNTTYPE_EXCESS)) {
					pftDetail.setExcessAmt(totBalAvailable);
					pftDetail.setExcessAmtResv(reservedAmt);
				} else if (StringUtils.equals(finExcessAmounts.get(i).getAmountType(),
						RepayConstants.EXAMOUNTTYPE_EMIINADV)) {
					pftDetail.setEmiInAdvance(totBalAvailable);
					pftDetail.setEmiInAdvanceResv(reservedAmt);
				} else if (StringUtils.equals(finExcessAmounts.get(i).getAmountType(),
						RepayConstants.EXAMOUNTTYPE_PAYABLE)) {
					pftDetail.setPayableAdvise(totBalAvailable);
					pftDetail.setPayableAdviseResv(totBalAvailable);
				}
			}
		}

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
			if (curSchdDate.compareTo(valueDate) < 0) {
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
			if (curSchdDate.compareTo(valueDate) > 0) {
				// do nothing
			} else if (valueDate.compareTo(curSchdDate) > 0 && valueDate.compareTo(nextSchdDate) <= 0) {
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
			if (dateSusp.compareTo(curSchdDate) <= 0) {
				acrNormal = nextSchd.getProfitCalc();
			} else if (dateSusp.after(curSchdDate) && dateSusp.compareTo(nextSchdDate) <= 0) {
				int days = getNoDays(dateSusp, curSchdDate);
				int daysInCurPeriod = nextSchd.getNoOfDays();
				acrNormal = nextSchd.getProfitCalc().multiply(new BigDecimal(days))
						.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
			}

			//This field will carry amortization till suspend date at this stage
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
			pftDetail.setPrvRpySchDate(curSchd.getSchDate());
			pftDetail.setPrvRpySchPft(curSchd.getProfitSchd());
			pftDetail.setPrvRpySchPri(curSchd.getPrincipalSchd());

			if (!curSchd.isSchPftPaid() || !curSchd.isSchPriPaid()) {
				pftDetail.setNOODInst(pftDetail.getNOODInst() + 1);
			}
		}

		pftDetail.setCurReducingRate(curSchd.getCalculatedRate());

		logger.debug("Leaving");
	}

	private static void calNextDateTotals(FinanceProfitDetail pftDetail, FinanceScheduleDetail curSchd) {
		logger.debug("Entering");

		// advance Profit and Principal
		pftDetail.setTotalPftPaidInAdv(pftDetail.getTotalPftPaidInAdv().add(curSchd.getSchdPftPaid()));
		pftDetail.setTotalPriPaidInAdv(pftDetail.getTotalPriPaidInAdv().add(curSchd.getSchdPriPaid()));

		//NEXT Schedule Details
		if (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) {
			if (pftDetail.getNSchdDate().compareTo(pftDetail.getMaturityDate()) == 0) {
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
		BigDecimal calPart2 = pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPftCpz());
		BigDecimal daysFactor = CalculationUtil.getInterestDays(pftDetail.getFinStartDate(),
				pftDetail.getMaturityDate(), finMain.getProfitDaysBasis());
		if (calPart2.compareTo(BigDecimal.ZERO) > 0) {
			pftDetail.setCurFlatRate(calPart1.multiply(new BigDecimal(100)).divide(calPart2.multiply(daysFactor), 9,
					RoundingMode.HALF_DOWN));
		} else {
			pftDetail.setCurFlatRate(BigDecimal.ZERO);
		}

		pftDetail.setPftAccrued(pftDetail.getPftAmz().subtract(pftDetail.getTotalPftPaid()));

		// Suspense Amortization
		if (dateSusp.compareTo(pftDetail.getMaturityDate()) <= 0) {
			pftDetail.setPftAmzSusp(pftDetail.getPftAmz().subtract(pftDetail.getPftAmzNormal())
					.subtract(pftDetail.getPftAmzPD()));
			pftDetail.setPftInSusp(true);
			//Value Equivalent accrual after suspended date
			pftDetail.setPftAccrueSusp(pftDetail.getPftAccrued().subtract(pftDetail.getPftAccrueSusp()));
		} else {
			pftDetail.setPftInSusp(false);
			pftDetail.setPftAccrueSusp(BigDecimal.ZERO);
		}

		// OD Details
		if (!StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			FinODDetails finODDetails = finODDetailsDAO.getFinODSummary(pftDetail.getFinReference());
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

		tenor = DateUtility.getMonthsBetween(pftDetail.getFinStartDate(), pftDetail.getMaturityDate());
		pftDetail.setTotalTenor(tenor);

		logger.debug("Leaving");
	}

	/**
	 * @param financeMain
	 * @param resultSet
	 * @throws Exception
	 */
	public void postAccruals(FinanceMain financeMain, FinanceProfitDetail finPftDetail, Date valueDate)
			throws Exception {
		logger.debug(" Entering ");

		String eventCode = AccountEventConstants.ACCEVENT_AMZ;

		if (finPftDetail.isPftInSusp()) {
			eventCode = AccountEventConstants.ACCEVENT_AMZSUSP;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(finPftDetail, eventCode, valueDate, valueDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		HashMap<String, Object> executingMap = amountCodes.getDeclaredFieldValues();
		financeMain.getDeclaredFieldValues(executingMap);
		//Postings Process
		FinanceType financeType = getFinanceType(finPftDetail.getFinType());
		List<ReturnDataSet> list = prepareAccounting(executingMap, financeType);
		saveAccounting(list);
		//posting done update the accrual balance
		finPftDetail.setAmzTillLBD(finPftDetail.getPftAmz());
		finPftDetail.setAmzTillLBDNormal(finPftDetail.getPftAmzNormal());
		finPftDetail.setAmzTillLBDPD(finPftDetail.getPftAmzPD());
		finPftDetail.setAmzTillLBDPIS(finPftDetail.getPftAmzSusp());
		finPftDetail.setAcrTillLBD(finPftDetail.getPftAccrued());
		finPftDetail.setAcrSuspTillLBD(finPftDetail.getPftAccrueSusp());

		//Month End move all the balances to previous month also
		if (DateUtility.getDay(valueDate) == 1) {
			finPftDetail.setPrvMthAcr(finPftDetail.getPftAccrued());
			finPftDetail.setPrvMthAcrSusp(finPftDetail.getPftAccrueSusp());
			finPftDetail.setPrvMthAmz(finPftDetail.getPftAmz());
			finPftDetail.setPrvMthAmzNrm(finPftDetail.getPftAmzNormal());
			finPftDetail.setPrvMthAmzPD(finPftDetail.getPftAmzPD());
			finPftDetail.setPrvMthAmzSusp(finPftDetail.getPftAmzSusp());
		}
		// these fields should be update after the accrual posting only so these will not be considered in normal update.
		getFinanceProfitDetailDAO().updateLBDAccruals(finPftDetail, false);
		logger.debug(" Leaving ");
	}

	private static int getNoDays(Date date1, Date date2) {
		return DateUtility.getDaysBetween(date1, date2);
	}


	public static void setSuspHeadDAO(FinanceSuspHeadDAO suspHeadDAO) {
		AccrualService.suspHeadDAO = suspHeadDAO;
	}

	public static void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		AccrualService.finExcessAmountDAO = finExcessAmountDAO;
	}
}
