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
 * FileName : RepayCalculator.java *
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.rits.cloning.Cloner;

public class RepayCalculator implements Serializable {
	private static final long serialVersionUID = 8062681791631293126L;
	private static Logger logger = LogManager.getLogger(RepayCalculator.class);

	private BigDecimal balanceRepayAmount = BigDecimal.ZERO;
	private boolean setEarlyPayAmt = false;
	private boolean setPastPenalties = false;
	private String sqlRule = "";
	private SubHeadRule subHeadRule;
	private Map<Date, FinanceScheduleDetail> scheduleMap = null;

	Date curBussniessDate = SysParamUtil.getAppDate();
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	private OverdueChargeRecoveryDAO recoveryDAO;

	// Default Constructor
	public RepayCalculator() {
		super();
	}

	/*
	 * ___________________________________________________________________________________________
	 * 
	 * Main Methods ___________________________________________________________________________________________
	 */

	public RepayData initiateRepay(RepayData repayData, FinanceMain fm, List<FinanceScheduleDetail> schedules,
			String sqlRule, SubHeadRule subHeadRule, boolean isReCal, String method, Date valueDate,
			String processMethod) {
		return procInitiateRepay(repayData, fm, schedules, sqlRule, subHeadRule, isReCal, method, valueDate,
				processMethod);
	}

	public RepayData calculateRefunds(RepayData repayData, BigDecimal manualRefundAmt, boolean isManualProc,
			String sqlRule, SubHeadRule subHeadRule) {

		this.sqlRule = sqlRule;
		this.subHeadRule = subHeadRule;
		return calRefunds(repayData, manualRefundAmt, isManualProc);
	}

	/** To Calculate the Amounts for given schedule */
	public RepayData procInitiateRepay(RepayData repayData, FinanceMain financeMain,
			List<FinanceScheduleDetail> financeScheduleDetails, String sqlRule, SubHeadRule subHeadRule,
			boolean isReCal, String method, Date valueDate, String processMethod) {
		logger.debug(Literal.ENTERING);

		// Reset Current Business Application Date
		if (valueDate != null) {
			curBussniessDate = valueDate;
		}

		// Initialize Repay
		if ("I".equals(repayData.getBuildProcess())) {
			repayData = initializeRepay(repayData, financeMain, financeScheduleDetails);
		}

		// Recalculate Repay
		if ("R".equals(repayData.getBuildProcess())) {
			this.sqlRule = sqlRule;
			this.subHeadRule = subHeadRule;
			repayData = recalRepay(repayData, financeMain, financeScheduleDetails, isReCal, method, processMethod);
		}
		logger.debug(Literal.LEAVING);
		return repayData;
	}

	// INITIALIZE REPAY PROCESS
	private RepayData initializeRepay(RepayData rd, FinanceMain fm, List<FinanceScheduleDetail> schedules) {

		logger.debug(Literal.ENTERING);

		rd.setFinReference(fm.getFinReference());
		rd.setRepayMain(null);
		rd.setRepayScheduleDetails(null);

		RepayMain rm = new RepayMain();
		rm.setFinID(fm.getFinID());
		rm.setFinReference(fm.getFinReference());
		rm.setFinCcy(fm.getFinCcy());
		rm.setProfitDaysBais(fm.getProfitDaysBasis());
		rm.setFinType(fm.getFinType());
		rm.setLovDescFinTypeName(fm.getLovDescFinTypeName());
		rm.setFinBranch(fm.getFinBranch());
		rm.setLovDescFinBranchName(fm.getLovDescFinBranchName());
		rm.setCustID(fm.getCustID());
		rm.setLovDescCustCIF(fm.getLovDescCustCIF());
		rm.setLovDescSalutationName(fm.getLovDescSalutationName());
		rm.setLovDescCustFName(fm.getLovDescCustFName());
		rm.setLovDescCustLName(fm.getLovDescCustLName());
		rm.setLovDescCustShrtName(fm.getLovDescCustShrtName());
		rm.setDateStart(fm.getFinStartDate());
		rm.setDateMatuirty(fm.getMaturityDate());
		rm.setAccrued(rd.getAccruedTillLBD());
		rm.setPendindODCharges(rd.getPendingODC());
		rm.setEarlyPayEffectOn(fm.getLovDescFinScheduleOn());

		rm.setFinAmount(BigDecimal.ZERO);
		rm.setCurFinAmount(BigDecimal.ZERO);
		rm.setProfit(BigDecimal.ZERO);
		rm.setProfitBalance(BigDecimal.ZERO);
		rm.setPrincipal(BigDecimal.ZERO);
		rm.setPrincipalBalance(BigDecimal.ZERO);
		rm.setTotalCapitalize(BigDecimal.ZERO);
		rm.setCapitalizeBalance(BigDecimal.ZERO);
		rm.setOverduePrincipal(BigDecimal.ZERO);
		rm.setOverdueProfit(BigDecimal.ZERO);
		rm.setDateLastFullyPaid(fm.getFinStartDate());
		rm.setDateNextPaymentDue(fm.getMaturityDate());
		rm.setDownpayment(BigDecimal.ZERO);
		rm.setPrincipalPayNow(BigDecimal.ZERO);
		rm.setProfitPayNow(BigDecimal.ZERO);
		rm.setRefundNow(BigDecimal.ZERO);

		rm.setRepayAmountNow(BigDecimal.ZERO);
		rm.setRepayAmountExcess(BigDecimal.ZERO);
		rd.setRepayMain(rm);

		rd = calRepayMain(rd, schedules);
		logger.debug(Literal.LEAVING);
		return rd;
	}

	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> schedules) {
		if (schedules != null && schedules.size() > 0) {
			Collections.sort(schedules, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return schedules;
	}

	// RECALCULATE REPAYMENTS PROCESS
	private RepayData recalRepay(RepayData rd, FinanceMain fm, final List<FinanceScheduleDetail> schedules,
			boolean isReCal, String method, String processMethod) {

		logger.debug(Literal.ENTERING);

		rd.setRepayScheduleDetails(new ArrayList<RepayScheduleDetail>());
		balanceRepayAmount = rd.getRepayMain().getRepayAmountNow();

		// If no balance for repayment then return with out calculation
		if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
			return rd;
		}

		// Copy Actual Schedule Details List
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> tempScheduleDetails = cloner.deepClone(schedules);
		tempScheduleDetails = sortSchdDetails(tempScheduleDetails);
		Map<Date, OverdueChargeRecovery> recMap = new HashMap<Date, OverdueChargeRecovery>();

		boolean chkPastPenalty = false;
		boolean seperatePenaltyProc = false;
		if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC)) {
			chkPastPenalty = true;
		} else if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS)) {
			chkPastPenalty = true;
			seperatePenaltyProc = true;
			List<OverdueChargeRecovery> recList = recoveryDAO.getPastSchedulePenalties(fm.getFinID());
			if (recList != null && !recList.isEmpty()) {
				for (int i = 0; i < recList.size(); i++) {

					if (recMap.containsKey(recList.get(i).getFinODSchdDate())) {
						recMap.remove(recList.get(i).getFinODSchdDate());
					}
					recMap.put(recList.get(i).getFinODSchdDate(), recList.get(i));
				}
			}
		}

		setEarlyPayAmt = false;
		setPastPenalties = false;

		scheduleMap = new HashMap<Date, FinanceScheduleDetail>();
		rd.getRepayMain().setEarlyPayNextSchDate(null);
		// Load Pending Repay Schedules until balance available for payment
		for (int i = 1; i < tempScheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = tempScheduleDetails.get(i);
			FinanceScheduleDetail prvSchd = tempScheduleDetails.get(i - 1);
			FinanceScheduleDetail nxtSchd = null;

			if (i != tempScheduleDetails.size() - 1) {
				nxtSchd = tempScheduleDetails.get(i + 1);
			}
			Date schdDate = curSchd.getSchDate();
			scheduleMap.put(schdDate, curSchd);

			// Skip if repayment already competed
			if (!chkPastPenalty && schdDate.compareTo(rd.getRepayMain().getDateLastFullyPaid()) <= 0
					&& schdDate.compareTo(curBussniessDate) != 0) {
				continue;
			}

			// Skip if not a repayment
			if (!(curSchd.isRepayOnSchDate()
					|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
				continue;
			}

			// Add schedule repayment if scheduled profit or principal to be paid
			if (curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()).compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()).compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()).compareTo(BigDecimal.ZERO) > 0) {

				rd = addRepayRecord(fm, rd, curSchd, prvSchd, i, false, isReCal, method, false, null, processMethod);
				// No more repayment amount left for next schedules
				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// Check For Fully Paid Installment to Recover penalty Amount, if Any
			if (chkPastPenalty && (curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())
					.compareTo(BigDecimal.ZERO) == 0
					&& curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()).compareTo(BigDecimal.ZERO) == 0)) {

				if (seperatePenaltyProc) {
					if (recMap.containsKey(schdDate)) {
						rd = addRepayRecord(fm, rd, curSchd, prvSchd, i, false, isReCal, method, nxtSchd == null,
								recMap.get(schdDate), processMethod);
					}
				} else {
					if (nxtSchd == null || (nxtSchd.getProfitSchd().subtract(nxtSchd.getSchdPftPaid())
							.compareTo(BigDecimal.ZERO) > 0
							|| nxtSchd.getPrincipalSchd().subtract(nxtSchd.getSchdPriPaid())
									.compareTo(BigDecimal.ZERO) > 0)) {
						rd = addRepayRecord(fm, rd, curSchd, prvSchd, i, false, isReCal, method, nxtSchd == null, null,
								processMethod);
					}
				}
				// No more repayment amount left for next schedules
				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}

		// Set Penalty Payments for Prepared Past due Schedule Details
		if (!setPastPenalties && (ImplementationConstants.REPAY_HIERARCHY_METHOD
				.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)
				|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {

			if (rd.getRepayScheduleDetails() != null && !rd.getRepayScheduleDetails().isEmpty()) {
				for (int i = 0; i < rd.getRepayScheduleDetails().size(); i++) {

					RepayScheduleDetail rpySchd = rd.getRepayScheduleDetails().get(i);
					if (rpySchd.getPenaltyAmt().compareTo(BigDecimal.ZERO) > 0) {

						applyOverdueCharges(rpySchd, processMethod);

						// No more repayment amount left for next schedules
						if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
							break;
						}

					}
				}

				// Reset Balances for Excess Amount
				rd.getRepayMain().setRepayAmountNow(balanceRepayAmount);
				rd.getRepayMain().setRepayAmountExcess(balanceRepayAmount);
			}
		}

		// Process for setting Balance Amount after Payment Apportionment
		rd = doReversalPayApportionmentForBal(rd);

		// Find Open for refund amounts
		rd = calRefunds(rd, BigDecimal.ZERO, false);
		logger.debug(Literal.LEAVING);
		return rd;
	}

	private RepayData calRepayMain(RepayData rd, List<FinanceScheduleDetail> schedules) {
		logger.debug(Literal.ENTERING);

		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal cpzTillNow = BigDecimal.ZERO;
		Boolean isNextDueSet = false;
		boolean isSkipLastDateSet = false;

		RepayMain repayMain = rd.getRepayMain();

		for (int i = 0; i < schedules.size(); i++) {
			FinanceScheduleDetail curSchd = schedules.get(i);
			Date schdDate = curSchd.getSchDate();

			// Finance amount and current finance amount
			repayMain.setFinAmount(repayMain.getFinAmount().add(curSchd.getDisbAmount())
					.add(curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : curSchd.getFeeChargeAmt()));

			repayMain.setCurFinAmount(repayMain.getFinAmount().subtract(curSchd.getSchdPriPaid())
					.subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDownPaymentAmount()));

			if (schdDate.compareTo(curBussniessDate) < 0) {
				repayMain.setCurFinAmount(repayMain.getCurFinAmount().subtract(curSchd.getCpzAmount()));
			}

			// Profit scheduled and Paid
			repayMain.setProfit(repayMain.getProfit().add(curSchd.getProfitSchd()));
			pftPaid = pftPaid.add(curSchd.getSchdPftPaid());

			// Principal scheduled and Paid
			repayMain.setPrincipal(repayMain.getPrincipal().add(curSchd.getPrincipalSchd()));
			priPaid = priPaid.add(curSchd.getSchdPriPaid());

			// Capitalization and Capitalized till now
			repayMain.setTotalCapitalize(repayMain.getTotalCapitalize().add(curSchd.getCpzAmount()));

			// Total Fee Amount
			repayMain.setTotalFeeAmt(repayMain.getTotalFeeAmt()
					.add(curSchd.getFeeSchd() == null ? BigDecimal.ZERO : curSchd.getFeeSchd()));

			// Overdue Principal and Profit
			if (schdDate.compareTo(curBussniessDate) < 0
					&& DateUtil.getDaysBetween(curBussniessDate, schdDate) >= 0) {
				cpzTillNow = cpzTillNow.add(curSchd.getCpzAmount());
				repayMain.setOverduePrincipal(repayMain.getOverduePrincipal()
						.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())));

				repayMain.setOverdueProfit(
						repayMain.getOverdueProfit().add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())));

			}

			// Down Payment
			repayMain.setDownpayment(repayMain.getDownpayment().add(curSchd.getDownPaymentAmount()));

			// REPAY SCHEDULE RECORD
			if (curSchd.isRepayOnSchDate()
					|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				BigDecimal balance = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
				balance = balance.add(curSchd.getProfitSchd()).subtract(curSchd.getSchdPftPaid());

				if (balance.compareTo(BigDecimal.ZERO) > 0) {
					isSkipLastDateSet = true;
				}

				// Set the last and next scheduled repayments to deferred dates (Agreed with customer)
				if (balance.compareTo(BigDecimal.ZERO) == 0 && !isSkipLastDateSet) {
					repayMain.setDateLastFullyPaid(curSchd.getSchDate());
				}

				if (balance.compareTo(BigDecimal.ZERO) > 0 && !isNextDueSet) {
					repayMain.setDateNextPaymentDue(curSchd.getSchDate());
					isNextDueSet = true;
				}
			}
		}

		repayMain.setProfitBalance(repayMain.getProfit().subtract(pftPaid));
		repayMain.setPrincipalBalance(repayMain.getPrincipal().subtract(priPaid));

		logger.debug(Literal.LEAVING);
		return rd;
	}

	private RepayData addRepayRecord(FinanceMain fm, RepayData rd, FinanceScheduleDetail curSchd,
			FinanceScheduleDetail prvSchd, int schdIndex, boolean isDefSchd, boolean isReCal, String method,
			boolean isLastRcd, OverdueChargeRecovery recovery, String processMethod) {
		logger.debug(Literal.ENTERING);

		BigDecimal toPay = BigDecimal.ZERO;

		RepayScheduleDetail rsd = new RepayScheduleDetail();
		rsd.setFinID(fm.getFinID());
		rsd.setFinReference(rd.getFinReference());
		rsd.setSchDate(curSchd.getSchDate());
		rsd.setDefSchdDate(curSchd.getSchDate());

		// PROFIT PAYMENT
		rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		rsd.setProfitSchd(curSchd.getProfitSchd());
		rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
		rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
		rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());

		// Fee Details on Each Schedule Wise
		rsd.setSchdFee(curSchd.getFeeSchd());
		rsd.setSchdFeePaid(curSchd.getSchdFeePaid());

		RepayMain rm = rd.getRepayMain();
		if (curBussniessDate.after(curSchd.getSchDate())) {
			rsd.setDaysLate(DateUtil.getDaysBetween(curSchd.getSchDate(), curBussniessDate));
			rsd.setDaysEarly(0);

			try {

				// Finance Repay Queue object Data Preparation
				FinRepayQueue rq = new FinRepayQueue();

				rq.setFinID(rm.getFinID());
				rq.setFinReference(rm.getFinReference());
				rq.setBranch(rm.getFinBranch());
				rq.setFinType(rm.getFinType());
				rq.setCustomerID(rm.getCustID());
				rq.setRpyDate(curSchd.getSchDate());
				rq.setFinPriority(9999);
				rq.setFinRpyFor(rsd.getSchdFor());
				rq.setSchdPft(rsd.getProfitSchd());
				rq.setSchdPri(rsd.getPrincipalSchd());
				rq.setSchdPftPaid(rsd.getProfitSchdPaid());
				rq.setSchdPriPaid(rsd.getPrincipalSchdPaid());
				rq.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
				rq.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));

				if (rsd.getProfitSchd().compareTo(rsd.getProfitSchdPaid()) == 0) {
					rq.setSchdIsPftPaid(true);
					if (rsd.getPrincipalSchd().compareTo(rsd.getPrincipalSchdPaid()) == 0) {
						rq.setSchdIsPriPaid(true);
					} else {
						rq.setSchdIsPriPaid(false);
					}
				} else {
					rq.setSchdIsPftPaid(false);
					rq.setSchdIsPriPaid(false);
				}

				// Overdue Penalty Recovery Calculation
				boolean recoverPastPenlaty = false;
				if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
						|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC)) {

					if (rq.isSchdIsPftPaid() && rq.isSchdIsPriPaid()) {
						recoverPastPenlaty = true;
					}
				}

				BigDecimal totWaived = BigDecimal.ZERO;
				if (recovery != null) {

					// Total Waived Amount
					totWaived = recovery.getTotWaived();

				} else if (recoverPastPenlaty) {
					// Fetch Max Overdue Charge Recovery for Past Schedule before Processed Schedule Date with Reference
					recovery = recoveryDAO.getPastSchedulePenalty(rq.getFinID(), curSchd.getSchDate(), true, false);

					if (recovery == null || recovery.getPenaltyBal().compareTo(BigDecimal.ZERO) <= 0) {
						logger.debug(Literal.LEAVING);
						return rd;
					}

					// Total Waived Amount
					totWaived = recovery.getTotWaived();
				} else {

					List<Object> odObjDetailList = recoveryPostingsUtil.recoveryCalculation(rq, fm.getProfitDaysBasis(),
							curBussniessDate, false, true);

					totWaived = ((FinODDetails) odObjDetailList.get(0)).getTotWaived();
					recovery = (OverdueChargeRecovery) odObjDetailList.get(1);
				}

				if (recovery != null) {

					rsd.setPenaltyAmt(recovery.getPenaltyBal());
					rsd.setChargeType(recovery.getPenaltyType());

					// Max Waiver Amount Calculation
					BigDecimal maxCalWaiver = (recovery.getPenaltyBal().multiply(recovery.getMaxWaiver()))
							.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);

					if ((maxCalWaiver.subtract(totWaived)).compareTo(BigDecimal.ZERO) > 0) {
						rsd.setMaxWaiver(maxCalWaiver.subtract(totWaived));
						rsd.setAllowWaiver(true);
					} else {
						rsd.setMaxWaiver(BigDecimal.ZERO);
					}

				} else {
					rsd.setPenaltyAmt(BigDecimal.ZERO);
					rsd.setMaxWaiver(BigDecimal.ZERO);
				}

				// Resetting Object
				rq = null;

			} catch (Exception e) {
				logger.error("Exception: ", e);
				rsd.setPenaltyAmt(BigDecimal.ZERO);
				rsd.setMaxWaiver(BigDecimal.ZERO);
			}

		} else {

			rsd.setDaysLate(0);
			rsd.setDaysEarly(DateUtil.getDaysBetween(curBussniessDate, curSchd.getSchDate()));

			if (setEarlyPayAmt && rm.getEarlyPayNextSchDate() == null) {
				rm.setEarlyPayNextSchDate(curSchd.getSchDate());
			}

			// Set Penalty Payments for Prepared Past due Schedule Details
			if (!setPastPenalties && (ImplementationConstants.REPAY_HIERARCHY_METHOD
					.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)
					|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {

				if (rd.getRepayScheduleDetails() != null && !rd.getRepayScheduleDetails().isEmpty()) {
					for (int i = 0; i < rd.getRepayScheduleDetails().size(); i++) {

						RepayScheduleDetail rpySchd = rd.getRepayScheduleDetails().get(i);
						if (rpySchd.getPenaltyAmt().compareTo(BigDecimal.ZERO) > 0) {

							applyOverdueCharges(rpySchd, processMethod);

							// No more repayment amount left for next schedules
							if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
								break;
							}

						}
					}
				}
			}

			// To Stop Penalty Setting From Remaining Balance
			setPastPenalties = true;
			// No more repayment amount left for next schedules
			if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
				rm.setRepayAmountNow(balanceRepayAmount);
				rm.setRepayAmountExcess(balanceRepayAmount);

				logger.debug(Literal.LEAVING);
				return rd;
			}

			if (rsd.getDaysEarly() >= 0) {

				final BigDecimal earlypayAmt = balanceRepayAmount;
				if (rsd.getDaysEarly() == 0) {
					if (earlypayAmt.compareTo(curSchd.getRepayAmount()) > 0) {
						rm.setEarlyPay(true);
						setEarlyPayAmt = true;

						if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
							rm.setEarlyPayAmount(earlypayAmt.add(curSchd.getRepayAmount()));
						} else {
							rm.setEarlyPayAmount(earlypayAmt);
						}
						rm.setEarlyPayOnSchDate(curSchd.getSchDate());
						rm.setEarlyRepayNewSchd(null);
					}
				} else {
					rm.setEarlyPay(true);
					if (!setEarlyPayAmt) {

						setEarlyPayAmt = true;
						rm.setEarlyPayAmount(earlypayAmt);

						if ("NONSCH".equals(SysParamUtil.getValueAsString("EARLYPAY_TERM_INS"))) {
							rm.setEarlyPayOnSchDate(curBussniessDate);
							rm.setEarlyPayNextSchDate(curSchd.getSchDate());
						} else {
							rm.setEarlyPayOnSchDate(curSchd.getSchDate());
						}

						if (!scheduleMap.containsKey(curBussniessDate)) {
							FinanceScheduleDetail newSchdlEP = new FinanceScheduleDetail();

							newSchdlEP.setFinID(fm.getFinID());
							newSchdlEP.setFinReference(fm.getFinReference());
							newSchdlEP.setDefSchdDate(rm.getEarlyPayOnSchDate());
							newSchdlEP.setSchDate(rm.getEarlyPayOnSchDate());
							newSchdlEP.setSchSeq(1);
							newSchdlEP.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
							newSchdlEP.setRepayOnSchDate(true);
							newSchdlEP.setPftOnSchDate(true);
							newSchdlEP.setSchdMethod(prvSchd.getSchdMethod());
							newSchdlEP.setBaseRate(prvSchd.getBaseRate());
							newSchdlEP.setSplRate(prvSchd.getSplRate());
							newSchdlEP.setMrgRate(prvSchd.getMrgRate());
							newSchdlEP.setActRate(prvSchd.getActRate());
							newSchdlEP.setCalculatedRate(prvSchd.getCalculatedRate());
							newSchdlEP.setPftDaysBasis(prvSchd.getPftDaysBasis());
							newSchdlEP.setEarlyPaidBal(prvSchd.getEarlyPaidBal());

							if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {
								newSchdlEP.setEarlyPaid(earlypayAmt);
							}
							rm.setEarlyRepayNewSchd(newSchdlEP);
						} else {
							FinanceScheduleDetail detail = scheduleMap.get(curBussniessDate);
							if (isReCal) {
								rm.setEarlyPayAmount(detail.getRepayAmount());
							} else {
								rm.setEarlyPayAmount(earlypayAmt.add(detail.getRepayAmount()));
							}
						}
					}
				}
			}
		}

		// Fee Details Payment First from Schedule Details in order as below
		// 1. Ins Fee Amount (Life Ins)
		// 2. Property Ins Fee Amount
		// 3. Credit insurance (Fixed Fee Schedule based amount)
		// 4. Schedule payment Supplementary Rent
		// 5. Schedule payment Increased Cost Amount
		// 6. Schedule payment Fee Charge Amount

		// Payment Process only for Installment or No default Payment Exists
		if (StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT)
				|| StringUtils.equals(rm.getPayApportionment(), PennantConstants.List_Select)) {

			schdFeeCollectionProcess(rsd);
		}

		// Based on repayments method then do charges postings first then profit or principal
		// C - PENALTY / CHRAGES, P - PRINCIPAL , I - PROFIT / INTEREST
		if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP)
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI))) {
			applyOverdueCharges(rsd, processMethod);
		}

		// Schedule Principal and Profit payments
		if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCIP))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS))) {

			// Profit Amount Checking
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
			toPay = rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid());

			// Payment Process only for Installment or Profit only from Schedules or No default Payment Exists
			if (StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_SPFT)
					|| StringUtils.equals(rm.getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setProfitSchdPayNow(toPay);
				} else {
					rsd.setProfitSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
			} else {
				rsd.setProfitSchdPayNow(BigDecimal.ZERO);
			}
			rm.setProfitPayNow(rm.getProfitPayNow().add(rsd.getProfitSchdPayNow()));

			// Principal Amount Checking
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
			toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

			// Payment Process only for Installment or Principal only from Schedules or No default Payment Exists
			if (StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_SPRI)
					|| StringUtils.equals(rm.getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setPrincipalSchdPayNow(toPay);
				} else {
					rsd.setPrincipalSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
			} else {
				rsd.setPrincipalSchdPayNow(BigDecimal.ZERO);
			}
			rm.setPrincipalPayNow(rm.getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));

		} else if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {

			// Principal Amount Checking
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
			toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

			// Payment Process only for Installment or Principal only from Schedules or No default Payment Exists
			if (StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_SPRI)
					|| StringUtils.equals(rm.getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setPrincipalSchdPayNow(toPay);
				} else {
					rsd.setPrincipalSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
			} else {
				rsd.setPrincipalSchdPayNow(BigDecimal.ZERO);
			}
			rm.setPrincipalPayNow(rm.getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));

			// Profit Amount Checking
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
			toPay = rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid());
			// Payment Process only for Installment or Profit only from Schedules or No default Payment Exists
			if (StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_SPFT)
					|| StringUtils.equals(rm.getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setProfitSchdPayNow(toPay);
				} else {
					rsd.setProfitSchdPayNow(balanceRepayAmount);
				}
				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
			} else {
				rsd.setProfitSchdPayNow(BigDecimal.ZERO);
			}
			rm.setProfitPayNow(rm.getProfitPayNow().add(rsd.getProfitSchdPayNow()));
		}

		// Based on repayments method then do charges postings first then profit or principal
		// C - PENALTY / CHRAGES, P - PRINCIPAL , I - PROFIT / INTEREST
		if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC))) {
			applyOverdueCharges(rsd, processMethod);
		}

		rsd.setRepayNet(rsd.getProfitSchdPayNow().add(rsd.getPrincipalSchdPayNow()).add(rsd.getSchdFeePayNow()));
		rsd.setRepayBalance(rsd.getPrincipalSchdBal().add(rsd.getProfitSchdBal()).add(rsd.getSchdFeeBal())
				.subtract(rsd.getRepayNet()));
		rsd.setRefundMax(BigDecimal.ZERO);
		rsd.setRefundReq(BigDecimal.ZERO);
		rsd.setAllowRefund(false);
		rsd.setSchdIndex(schdIndex);

		rm.setRepayAmountNow(balanceRepayAmount);
		rm.setRepayAmountExcess(balanceRepayAmount);
		rd.getRepayScheduleDetails().add(rsd);

		logger.debug(Literal.LEAVING);
		return rd;
	}

	private RepayScheduleDetail applyOverdueCharges(RepayScheduleDetail rsd, String processMethod) {
		// Overdue charge Amount Checking
		if (rsd.getPenaltyAmt().compareTo(BigDecimal.ZERO) > 0) {

			if (balanceRepayAmount.compareTo(rsd.getPenaltyAmt()) > 0) {
				rsd.setPenaltyPayNow(rsd.getPenaltyAmt());
			} else {
				rsd.setPenaltyPayNow(balanceRepayAmount);
			}

			// Addition for Penalty Amount not added before calculation.
			// First add Penalty amount to Total Repayment Amount / Balance Repayment amount and
			// adjust same to each schedule in reverse entries
			if (processMethod.equals(FinServiceEvent.EARLYSETTLE)) {
				balanceRepayAmount = balanceRepayAmount.add(rsd.getPenaltyPayNow());
			}

			balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPenaltyPayNow());
		}
		return rsd;
	}

	/**
	 * Method for Collecting Fee payment before Actual PRINCIPAL , PROFIT & PENALTY CHARGE
	 */
	private RepayScheduleDetail schdFeeCollectionProcess(RepayScheduleDetail rsd) {

		// Scheduled Fee Collection Process

		// 4. Scheduled Fee Amount
		if (balanceRepayAmount.compareTo(rsd.getSchdFee().subtract(rsd.getSchdFeePaid())) > 0) {
			rsd.setSchdFeePayNow(rsd.getSchdFee().subtract(rsd.getSchdFeePaid()));
		} else {
			rsd.setSchdFeePayNow(balanceRepayAmount);
		}
		rsd.setSchdFeeBal(rsd.getSchdFee().subtract(rsd.getSchdFeePaid()));
		balanceRepayAmount = balanceRepayAmount.subtract(rsd.getSchdFeePayNow());

		return rsd;
	}

	/**
	 * Method for Calculation Refund Amount Either Manual Process Or in Auto Calculation
	 * 
	 * @param repayData
	 * @param manualRefundAmt
	 * @param isManualProc
	 * @return
	 */
	private RepayData calRefunds(RepayData repayData, BigDecimal manualRefundAmt, boolean isManualProc) {
		logger.debug(Literal.ENTERING);

		if (this.sqlRule == null || subHeadRule == null) {
			if (isManualProc && manualRefundAmt.compareTo(BigDecimal.ZERO) > 0) {
				repayData.setMaxRefundAmt(BigDecimal.ZERO);
				repayData.setSufficientRefund(false);
			}
			logger.debug(Literal.LEAVING);
			return repayData;
		}

		String refundWhenPastDefDue = SysParamUtil.getValueAsString("REFUND_PAST_DEF_DUE");

		BigDecimal calRefundPft = BigDecimal.ZERO;
		boolean curTermExcluded = false;
		repayData.getRepayMain().setRefundCalStartDate(null);
		if (repayData.getRepayScheduleDetails() != null) {
			for (int i = 0; i < repayData.getRepayScheduleDetails().size(); i++) {
				RepayScheduleDetail rsd = repayData.getRepayScheduleDetails().get(i);

				if (curBussniessDate.compareTo(rsd.getSchDate()) <= 0) {
					if (!curTermExcluded) {
						curTermExcluded = true;
						rsd.setAllowRefund(true);
						rsd.setRefundMax(BigDecimal.ZERO);
					} else {
						rsd.setRefundMax(BigDecimal.ZERO);
						if (repayData.getRepayMain().getRefundCalStartDate() == null) {
							repayData.getRepayMain().setRefundCalStartDate(rsd.getSchDate());
						}
						if (refundWhenPastDefDue.equals(PennantConstants.NO)) {
							rsd.setAllowRefund(true);
							calRefundPft = calRefundPft.add(rsd.getProfitSchdPayNow());
						} else {
							rsd.setAllowRefund(true);
							calRefundPft = calRefundPft.add(rsd.getProfitSchdPayNow());
						}
					}
				} else {
					rsd.setAllowRefund(false);
					rsd.setRefundMax(BigDecimal.ZERO);
				}
			}
		}
		// Refund Rule Execution for Max Allowed Refund Amount
		BigDecimal refundResult = BigDecimal.ZERO;
		subHeadRule.setREFUNDPFT(calRefundPft);
		refundResult = (BigDecimal) RuleExecutionUtil.executeRule(this.sqlRule,
				this.subHeadRule.getDeclaredFieldValues(), repayData.getRepayMain().getFinCcy(),
				RuleReturnType.DECIMAL);

		// Check For Maximum Allowed Refund Amount
		if (isManualProc && manualRefundAmt.compareTo(refundResult) > 0) {
			repayData.setMaxRefundAmt(refundResult);
			repayData.setSufficientRefund(false);

			logger.debug(Literal.LEAVING);
			return repayData;
		} else if (isManualProc) {
			refundResult = manualRefundAmt;
		}

		int size = repayData.getRepayScheduleDetails().size();
		for (int i = size - 1; i >= 0; i--) {
			RepayScheduleDetail rsd = repayData.getRepayScheduleDetails().get(i);

			if (rsd.isAllowRefund() && refundResult.compareTo(BigDecimal.ZERO) > 0
					&& rsd.getProfitSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
				rsd.setAllowRefund(true);
				if (rsd.getProfitSchdPayNow().compareTo(refundResult) >= 0) {
					rsd.setRefundMax(refundResult);
					rsd.setRefundReq(refundResult);
					refundResult = BigDecimal.ZERO;
				} else {
					rsd.setRefundMax(rsd.getProfitSchdPayNow());
					rsd.setRefundReq(rsd.getProfitSchdPayNow());
					refundResult = refundResult.subtract(rsd.getProfitSchdPayNow());
				}
			} else {
				rsd.setAllowRefund(false);
				rsd.setRefundMax(BigDecimal.ZERO);
				rsd.setRefundReq(BigDecimal.ZERO);
			}

		}
		logger.debug(Literal.LEAVING);
		return repayData;
	}

	/**
	 * Method for adjusting Balance amount after Payment Apportionment, if Exists any.
	 */
	private RepayData doReversalPayApportionmentForBal(RepayData rd) {

		// Set Remaining Balance Amount for Payment Apportionment Method
		RepayMain rm = rd.getRepayMain();
		if (!(StringUtils.equals(rm.getPayApportionment(), PennantConstants.List_Select)
				|| StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT))
				&& balanceRepayAmount.compareTo(BigDecimal.ZERO) > 0) {

			List<RepayScheduleDetail> rsdList = rd.getRepayScheduleDetails();

			for (RepayScheduleDetail rsd : rsdList) {

				// Adjust Remaining Balance to Fee Details on particular Schedule
				schdFeeCollectionProcess(rsd);

				// Adjust Remaiing balance to Either Principal or Profit based on (Reversal)Payment Apportionment Method
				BigDecimal toPay = BigDecimal.ZERO;

				// Profit Amount Checking
				if (StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_SPRI)) {

					rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
					toPay = rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid());

					if (balanceRepayAmount.compareTo(toPay) >= 0) {
						rsd.setProfitSchdPayNow(toPay);
					} else {
						rsd.setProfitSchdPayNow(balanceRepayAmount);
					}

					balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
					rm.setProfitPayNow(rm.getProfitPayNow().add(rsd.getProfitSchdPayNow()));
				}

				// Principal Amount Checking
				if (StringUtils.equals(rm.getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_SPFT)) {
					rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
					toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

					if (balanceRepayAmount.compareTo(toPay) >= 0) {
						rsd.setPrincipalSchdPayNow(toPay);
					} else {
						rsd.setPrincipalSchdPayNow(balanceRepayAmount);
					}

					balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
					rm.setPrincipalPayNow(rm.getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));
				}

				rsd.setRepayNet(
						rsd.getProfitSchdPayNow().add(rsd.getPrincipalSchdPayNow()).add(rsd.getSchdFeePayNow()));
				rsd.setRepayBalance(rsd.getPrincipalSchdBal().add(rsd.getProfitSchdBal()).add(rsd.getSchdFeeBal())
						.subtract(rsd.getRepayNet()));
				rm.setRepayAmountNow(balanceRepayAmount);
				rm.setRepayAmountExcess(balanceRepayAmount);

				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return rd;
	}

}
