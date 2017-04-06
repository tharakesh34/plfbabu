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
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
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

	public static AEAmountCodes procAEAmounts(FinanceMain financeMain, List<FinanceScheduleDetail> schdDetails, FinanceProfitDetail pftDetail, Date valueDate) {
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
		aeAmountCodes.setAccrue(pftDetail.getAccruePft());
		aeAmountCodes.setAccrueS(pftDetail.getSuspPft());
		aeAmountCodes.setlAccrue(pftDetail.getAcrTillLBD());
		aeAmountCodes.setAmz(pftDetail.getTdPftAmortized());
		aeAmountCodes.setAmzS(pftDetail.getTdPftAmortizedSusp());
		//first Repayments
		aeAmountCodes.setFirstRepayDate(pftDetail.getFirstRepayDate());
		aeAmountCodes.setFirstRepayAmt(pftDetail.getFirstRepayAmt());
		//Last Repayments
		aeAmountCodes.setLastRpySchDate(pftDetail.getLastRpySchDate());
		aeAmountCodes.setLastRepayAmt(pftDetail.getLastRepayAmt());
		aeAmountCodes.setLastRpySchPft(pftDetail.getLastRpySchPft());
		aeAmountCodes.setLastRpySchPri(pftDetail.getLastRpySchPri());
		//next Repayments
		aeAmountCodes.setNextRpySchDate(pftDetail.getNextRpySchDate());
		aeAmountCodes.setNextSchdPri(pftDetail.getNSchdPri());
		aeAmountCodes.setNextSchdPft(pftDetail.getNSchdPft());
		aeAmountCodes.setNextSchdPriBal(pftDetail.getNSchdPriDue());
		aeAmountCodes.setNextSchdPftBal(pftDetail.getNSchdPftDue());
		//OD Details
		aeAmountCodes.setODDays(pftDetail.getoDDays());
		aeAmountCodes.setODInst(pftDetail.getNOODInst());
		aeAmountCodes.setPftOD(pftDetail.getODProfit());
		aeAmountCodes.setPriOD(pftDetail.getODPrincipal());
		aeAmountCodes.setPenaltyDue(pftDetail.getPenaltyDue());
		aeAmountCodes.setPenaltyPaid(pftDetail.getPenaltyPaid());
		aeAmountCodes.setPenaltyWaived(pftDetail.getPenaltyWaived());
		aeAmountCodes.setFirstODDate(pftDetail.getFirstODDate());
		aeAmountCodes.setLastODDate(pftDetail.getLastODDate());
		//Depreciation
		aeAmountCodes.setAccumulatedDepPri(pftDetail.getAccumulatedDepPri());
		aeAmountCodes.setDepreciatePri(pftDetail.getDepreciatePri());
		//others
		aeAmountCodes.setCurFlatRate(pftDetail.getCurFlatRate());
		aeAmountCodes.setPaidInst(pftDetail.getNOPaidInst());
		aeAmountCodes.setFullyPaidDate(pftDetail.getFullPaidDate());
		aeAmountCodes.setDisburse(pftDetail.getDisburse());
		aeAmountCodes.setDownpay(pftDetail.getDownpay());
		aeAmountCodes.setPftInAdv(pftDetail.getTotalPftPaidInAdv());
		aeAmountCodes.setDaysFromFullyPaid(getNoDays(pftDetail.getFullPaidDate(), valueDate));

		// +++++++++++++++++++
		//		aeAmountCodes.setCpzCur();
		//		aeAmountCodes.setCurReducingRate();
		//		aeAmountCodes.setTtlTerms();
		//		aeAmountCodes.setElpTerms();
		//		aeAmountCodes.setCpzPrv();
		//		aeAmountCodes.setCPNoOfDays();
		//		aeAmountCodes.setCpDaysTill();
		//		aeAmountCodes.setDaysDiff();
		//		aeAmountCodes.setCpzNxt();
		//		aeAmountCodes.setDAccrue();
		//		aeAmountCodes.setNAccrue();
		//		aeAmountCodes.setlAmz();
		//		aeAmountCodes.setdAmz();
		//		aeAmountCodes.setnAmz();
		//		aeAmountCodes.setCRBODDays();
		//		aeAmountCodes.setCRBPriOD();
		//		aeAmountCodes.setCRBPftOD();
		//		aeAmountCodes.setCRBFirstODDate();
		//		aeAmountCodes.setCRBODInst();
		//		aeAmountCodes.setCRBLastODDate();
		logger.debug("Leaving");
		return aeAmountCodes;

	}

	public static FinanceProfitDetail calProfitDetails(FinanceMain main, List<FinanceScheduleDetail> schdDetails, FinanceProfitDetail pftDetail, Date valueDate) {
		logger.debug("Entering");

		if (pftDetail == null) {
			pftDetail = new FinanceProfitDetail();
		}

		String finRef = main.getFinReference();
		// Fetch Customer Object Details
		Customer customer = getCustomerDAO().getCustomerByID(main.getCustID());
		if (customer!=null) {
			pftDetail.setCustCIF(customer.getCustCIF());
		}
		pftDetail.setFinStatus(main.getFinStatus());
		pftDetail.setFinStsReason(main.getFinStsReason());
		pftDetail.setFinAccount(main.getFinAccount());
		pftDetail.setFinAcType(main.getAccountType());
		pftDetail.setDisbAccountId(main.getDisbAccountId());
		pftDetail.setRepayAccountId(main.getRepayAccountId());
		pftDetail.setFinCustPftAccount(main.getFinCustPftAccount());
		pftDetail.setFinCommitmentRef(main.getFinCommitmentRef());
		pftDetail.setFinIsActive(main.isFinIsActive());
		pftDetail.setCustId(main.getCustID());
		pftDetail.setFinBranch(main.getFinBranch());
		pftDetail.setFinType(main.getFinType());
		pftDetail.setRepayFrq(main.getRepayFrq());
		pftDetail.setFinCcy(main.getFinCcy());
		pftDetail.setFinPurpose(main.getFinPurpose());
		pftDetail.setFinContractDate(main.getFinContractDate());
		pftDetail.setFinApprovedDate(main.getFinApprovedDate());
		pftDetail.setFinStartDate(main.getFinStartDate());
		pftDetail.setMaturityDate(main.getMaturityDate());
		pftDetail.setFinAmount(main.getFinAmount());
		pftDetail.setDownPayment(main.getDownPayment());
		pftDetail.setCurReducingRate(main.getEffectiveRateOfReturn());
		pftDetail.setFinReference(finRef);
		pftDetail.setLastMdfDate(valueDate);

		String finState = CalculationConstants.FIN_STATE_NORMAL;
		boolean isSusp = false;
		Date dateSusp = DateUtility.addDays(main.getMaturityDate(), 1);

		Boolean isNextSchdSet = false;
		Boolean isFirstSchdSet = false;

		FinanceSuspHead suspHead = getSuspHeadDAO().getFinanceSuspHeadById(finRef, "");

		if (suspHead != null && suspHead.isFinIsInSusp()) {
			isSusp = true;
			dateSusp = suspHead.getFinSuspDate();
		}

		if (schdDetails == null || schdDetails.isEmpty()) {
			return pftDetail;
		}

		// reset fields
		resetCalculatedTotals(pftDetail);

		for (int i = 0; i < schdDetails.size(); i++) {
			// current processing schedule in the loop
			FinanceScheduleDetail curSchd = schdDetails.get(i);
			Date curSchdDate = curSchd.getSchDate();
			// previous Schedule
			FinanceScheduleDetail prvSchd = null;
			Date prvSchdDate = null;
			// next Schedule
			FinanceScheduleDetail nextSchd = null;
			Date nextSchdDate = null;
			// previous details
			if (curSchdDate.compareTo(main.getFinStartDate()) == 0 || i == 0) {
				prvSchd = curSchd;
				prvSchdDate = curSchdDate;
			} else {
				prvSchd = schdDetails.get(i - 1);
				prvSchdDate = prvSchd.getSchDate();
			}
			// next details
			//in few cases  there might be schedules present even after the maturity date. ex: when calculating the fees
			if (curSchdDate.compareTo(main.getMaturityDate()) == 0 || i == schdDetails.size()-1) {
				nextSchd = curSchd;
				nextSchdDate = curSchdDate;
			}  else {
				nextSchd = schdDetails.get(i + 1);
				nextSchdDate = nextSchd.getSchDate();
			}
			// profit
			pftDetail.setTotalPftSchd(pftDetail.getTotalPftSchd().add(curSchd.getProfitSchd()));
			pftDetail.setTotalPftCpz(pftDetail.getTotalPftCpz().add(curSchd.getCpzAmount()));
			pftDetail.setTotalPftPaid(pftDetail.getTotalPftPaid().add(curSchd.getSchdPftPaid()));
			pftDetail.setTotalPftBal(pftDetail.getTotalPftSchd().subtract(pftDetail.getTotalPftPaid()));
			// principal
			pftDetail.setTotalpriSchd(pftDetail.getTotalpriSchd().add(curSchd.getPrincipalSchd()));
			pftDetail.setTotalPriPaid(pftDetail.getTotalPriPaid().add(curSchd.getSchdPriPaid()));
			pftDetail.setTotalPriBal(pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPriPaid()));
			// advance Profit and Principal
			if (curSchdDate.compareTo(valueDate) > 0) {
				pftDetail.setTotalPftPaidInAdv(pftDetail.getTotalPftPaidInAdv().add(curSchd.getSchdPftPaid()));
				pftDetail.setTotalPriPaidInAdv(pftDetail.getTotalPriPaidInAdv().add(curSchd.getPrincipalSchd()));
			}
			// advised Profit
			pftDetail.setTotalAdvPftSchd(pftDetail.getTotalAdvPftSchd().add(curSchd.getAdvProfit()));
			// rebate
			pftDetail.setTotalRbtSchd(pftDetail.getTotalRbtSchd().add(curSchd.getRebate()));
			// Till date Calculation
			if (curSchdDate.compareTo(valueDate) <= 0) {
				// profit
				pftDetail.setTdSchdPft(pftDetail.getTdSchdPft().add(curSchd.getProfitSchd()));
				pftDetail.setTdPftCpz(pftDetail.getTdPftCpz().add(curSchd.getCpzAmount()));
				pftDetail.setTdSchdPftPaid(pftDetail.getTdSchdPftPaid().add(curSchd.getSchdPftPaid()));
				pftDetail.setTdSchdPftBal(pftDetail.getTdSchdPft().subtract(pftDetail.getTdSchdPftPaid()));
				// principal
				pftDetail.setTdSchdPri(pftDetail.getTdSchdPri().add(curSchd.getPrincipalSchd()));
				pftDetail.setTdSchdPriPaid(pftDetail.getTdSchdPftPaid().add(curSchd.getSchdPriPaid()));
				pftDetail.setTdSchdPriBal(pftDetail.getTdSchdPri().subtract(pftDetail.getTdSchdPriPaid()));
				// advised Profit
				pftDetail.setTdSchdAdvPft(pftDetail.getTdSchdAdvPft().add(curSchd.getAdvProfit()));
				// rebate
				pftDetail.setTdSchdRbt(pftDetail.getTdSchdRbt().add(curSchd.getRebate()));
			}

			BigDecimal tdPftAmortized = BigDecimal.ZERO;
			BigDecimal tdPftAmortizedNormal = BigDecimal.ZERO;
			BigDecimal tdPftAmortizedPD = BigDecimal.ZERO;
			// Amortization
			if (curSchdDate.compareTo(valueDate)>=0) {
				// do nothing
			} else if (valueDate.after(curSchdDate) && valueDate.compareTo(nextSchdDate) <= 0) {
				int days = getNoDays(valueDate, curSchdDate);
				int daysInCurPeriod = nextSchd.getNoOfDays();
				tdPftAmortized = nextSchd.getProfitCalc().multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
			} else {
				tdPftAmortized = nextSchd.getProfitCalc();
			}

			if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) && curSchdDate.compareTo(valueDate) <= 0) {
				if ((!curSchd.isSchPftPaid() || !curSchd.isSchPriPaid())) {
					finState = CalculationConstants.FINSTATE_PD;
				}
			}

			if (finState.equals(CalculationConstants.FIN_STATE_NORMAL)) {
				tdPftAmortizedNormal = tdPftAmortized;
			}

			if (finState.equals(CalculationConstants.FINSTATE_PD)) {
				// PD Amortization
				if (curSchdDate.after(dateSusp)) {
					// do nothing
				} else if (dateSusp.after(curSchdDate) && dateSusp.compareTo(nextSchdDate) <= 0) {
					int days = getNoDays(dateSusp, curSchdDate);
					int daysInCurPeriod = nextSchd.getNoOfDays();
					tdPftAmortizedPD = nextSchd.getProfitCalc().multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
				} else if (curSchdDate.before(dateSusp)) {
					tdPftAmortizedPD = tdPftAmortized.subtract(tdPftAmortizedNormal);
				}
			}

			pftDetail.setTdPftAmortized(pftDetail.getTdPftAmortized().add(tdPftAmortized));
			pftDetail.setTdPftAmortizedNormal(pftDetail.getTdPftAmortizedNormal().add(tdPftAmortizedNormal));
			pftDetail.setTdPftAmortizedPD(pftDetail.getTdPftAmortizedPD().add(tdPftAmortizedPD));

			if (curSchdDate.compareTo(valueDate) <= 0 && curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
				pftDetail.setFullPaidDate(curSchdDate);
			}

			if (!isNextSchdSet) {
				if (curSchdDate.compareTo(valueDate) >= 0) {
					if (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) {
						isNextSchdSet = true;
						pftDetail.setNSchdDate(curSchdDate);
						pftDetail.setNSchdPri(curSchd.getPrincipalSchd());
						pftDetail.setNSchdPft(curSchd.getProfitSchd());
						pftDetail.setNSchdPriDue(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
						pftDetail.setNSchdPftDue(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
					}
				}
			}

			if (!isFirstSchdSet) {
				if (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) {
					isFirstSchdSet = true;
					pftDetail.setFirstRepayDate(curSchdDate);
					pftDetail.setFirstRepayAmt(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
				}
			}

			if (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) {
				pftDetail.setNOInst(pftDetail.getNOInst() + 1);

				if (curSchd.isRepayOnSchDate()) {
					pftDetail.setNORepayments(pftDetail.getNORepayments() + 1);
				}

				if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
					pftDetail.setNOPaidInst(pftDetail.getNOPaidInst() + 1);
				} else if (curSchdDate.compareTo(valueDate) <= 0) {
					pftDetail.setNOODInst(pftDetail.getNOODInst() + 1);
				}
			}

			if (curSchdDate.compareTo(pftDetail.getMaturityDate()) == 0) {
				pftDetail.setLastRepayAmt(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			}

			if ((i != 0) && (valueDate.compareTo(curSchd.getSchDate()) <= 0 || (i == schdDetails.size() - 1)) && pftDetail.getNextRpySchDate() == null) {
				pftDetail.setNextRpySchDate(curSchdDate);
				pftDetail.setLastRpySchDate(prvSchdDate);
				pftDetail.setLastRpySchPft(prvSchd.getPrincipalSchd());
				pftDetail.setLastRpySchPri(prvSchd.getProfitSchd());
			}

			if (curSchdDate.compareTo(valueDate) == 0) {
				pftDetail.setDisburse(curSchd.getDisbAmount());
				pftDetail.setDownpay(curSchd.getDownPaymentAmount());
			}
		}

		// Suspense Amortization
		if (isSusp) {
			pftDetail.setTdPftAmortizedSusp(pftDetail.getTdPftAmortized().subtract(pftDetail.getTdPftAmortizedNormal()).subtract(pftDetail.getTdPftAmortizedPD()));
		}
		// Current Flat Rate
		BigDecimal calPart1 = pftDetail.getTotalPftSchd().add(pftDetail.getTotalPftCpz());
		BigDecimal calPart2 = pftDetail.getTotalpriSchd().subtract(pftDetail.getTotalPftCpz()).max(new BigDecimal(100));
		BigDecimal daysFactor = CalculationUtil.getInterestDays(main.getFinStartDate(), main.getMaturityDate(), main.getProfitDaysBasis());
		pftDetail.setCurFlatRate(calPart1.divide(calPart2.multiply(daysFactor), 9, RoundingMode.HALF_DOWN));
		// OD Details
		FinODDetails finODDetails = getFinODDetailsDAO().getFinODSummary(main.getFinReference());
		if (finODDetails != null) {
			pftDetail.setODPrincipal(finODDetails.getFinCurODPri());
			pftDetail.setODProfit(finODDetails.getFinCurODPft());
			pftDetail.setPenaltyPaid(finODDetails.getTotPenaltyPaid());
			pftDetail.setPenaltyDue(finODDetails.getTotPenaltyBal());
			pftDetail.setPenaltyWaived(finODDetails.getTotWaived());
			pftDetail.setFirstODDate(finODDetails.getFinODSchdDate());
			pftDetail.setLastODDate(finODDetails.getFinODTillDate());
			pftDetail.setoDDays(getNoDays(valueDate, finODDetails.getFinODTillDate()));
		}
		pftDetail.setPftInSusp(isSusp);
		/*
		 * Depreciation Calculation depends on Total Finance Amount with Days
		 * basis
		 */
		BigDecimal daysBtwStartAndMaturity = getDays(main.getMaturityDate(), main.getFinStartDate());
		BigDecimal daysBtwStartAndValue = getDays(valueDate, main.getFinStartDate());
		BigDecimal daysBtwStartYearAndValue = getDays(valueDate, DateUtility.getYearStartDate(valueDate));

		BigDecimal finAmount = main.getFinAmount().subtract(main.getDownPayment()).add(main.getFeeChargeAmt()).add(main.getInsuranceAmt());
		BigDecimal depreciatePri = BigDecimal.ZERO;
		BigDecimal accumulatedPriTillDate = (finAmount.multiply(daysBtwStartAndValue)).divide(daysBtwStartAndMaturity, 0, RoundingMode.HALF_DOWN);

		if (daysBtwStartYearAndValue.compareTo(daysBtwStartAndValue) < 0) {
			depreciatePri = (finAmount.multiply(daysBtwStartYearAndValue)).divide(daysBtwStartAndMaturity, 0, RoundingMode.HALF_DOWN);
		} else {
			depreciatePri = (finAmount.multiply(daysBtwStartAndValue)).divide(daysBtwStartAndMaturity, 0, RoundingMode.HALF_DOWN);
		}
		// Principal Depreciation Value Till now
		pftDetail.setAccumulatedDepPri(accumulatedPriTillDate);

		if (main.getMaturityDate() != null && (valueDate.compareTo(main.getMaturityDate()) > 0) && (DateUtility.getYear(valueDate) != DateUtility.getYear(main.getMaturityDate()))) {
			depreciatePri = BigDecimal.ZERO;
		}
		pftDetail.setDepreciatePri(depreciatePri);

		return pftDetail;

	}

	private static void resetCalculatedTotals(FinanceProfitDetail pftDetail) {
		// profit
		pftDetail.setTotalPftSchd(BigDecimal.ZERO);
		pftDetail.setTotalPftCpz(BigDecimal.ZERO);
		pftDetail.setTotalPftPaid(BigDecimal.ZERO);
		pftDetail.setTotalPftBal(BigDecimal.ZERO);
		// principal
		pftDetail.setTotalpriSchd(BigDecimal.ZERO);
		pftDetail.setTotalPriPaid(BigDecimal.ZERO);
		pftDetail.setTotalPriBal(BigDecimal.ZERO);

		pftDetail.setTotalPftPaidInAdv(BigDecimal.ZERO);
		pftDetail.setTotalPriPaidInAdv(BigDecimal.ZERO);
		// advised Profit
		pftDetail.setTotalAdvPftSchd(BigDecimal.ZERO);
		// rebate
		pftDetail.setTotalRbtSchd(BigDecimal.ZERO);
		// Till date Calculation
		// profit
		pftDetail.setTdSchdPft(BigDecimal.ZERO);
		pftDetail.setTdPftCpz(BigDecimal.ZERO);
		pftDetail.setTdSchdPftPaid(BigDecimal.ZERO);
		pftDetail.setTdSchdPftBal(BigDecimal.ZERO);
		// principal
		pftDetail.setTdSchdPri(BigDecimal.ZERO);
		pftDetail.setTdSchdPriPaid(BigDecimal.ZERO);
		pftDetail.setTdSchdPriBal(BigDecimal.ZERO);
		// advised Profit
		pftDetail.setTdSchdAdvPft(BigDecimal.ZERO);
		// rebate
		pftDetail.setTdSchdRbt(BigDecimal.ZERO);
		pftDetail.setTdPftAmortized(BigDecimal.ZERO);
		pftDetail.setTdPftAmortizedNormal(BigDecimal.ZERO);
		pftDetail.setTdPftAmortizedPD(BigDecimal.ZERO);
		pftDetail.setNOInst(0);
		pftDetail.setNORepayments(0);
		pftDetail.setNOPaidInst(0);
		pftDetail.setNOODInst(0);
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
		dataSet.setSecurityDeposit(financeMain.getSecurityDeposit() == null ? BigDecimal.ZERO : financeMain.getSecurityDeposit());
		dataSet.setDeductFeeDisb(financeMain.getDeductFeeDisb() == null ? BigDecimal.ZERO : financeMain.getDeductFeeDisb());
		dataSet.setDeductInsDisb(financeMain.getDeductInsDisb() == null ? BigDecimal.ZERO : financeMain.getDeductInsDisb());
		dataSet.setGrcPftChg(financeMain.getTotalGrossGrcPft() == null ? BigDecimal.ZERO : financeMain.getTotalGrossGrcPft());
		dataSet.setNoOfTerms(financeMain.getNumberOfTerms() + financeMain.getGraceTerms());
		// Tenure Calculation
		int months = DateUtility.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate(), true);
		dataSet.setTenure(months);
		logger.debug("Leaving");
		return dataSet;

	}

	private static int getNoDays(Date date1, Date date2) {
		return DateUtility.getDaysBetween(date1, date2);
	}

	private static BigDecimal getDays(Date date1, Date date2) {
		return new BigDecimal(getNoDays(date1, date2));
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
