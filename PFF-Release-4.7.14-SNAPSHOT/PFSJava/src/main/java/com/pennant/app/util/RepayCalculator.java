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
 * FileName    		:  RepayCalculator.java													*                           
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
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.finance.FinanceMainDAO;
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
import com.rits.cloning.Cloner;

public class RepayCalculator implements Serializable {

	private static final long					serialVersionUID	= 8062681791631293126L;
	private static Logger						logger				= Logger.getLogger(RepayCalculator.class);
	private RepayData							repayData;
	private BigDecimal							balanceRepayAmount	= BigDecimal.ZERO;
	private boolean								setEarlyPayAmt		= false;
	private boolean								setPastPenalties	= false;
	private String								sqlRule				= "";
	private SubHeadRule							subHeadRule;
	private Map<Date, FinanceScheduleDetail>	scheduleMap			= null;

	Date										curBussniessDate	= DateUtility.getAppDate();
	private RuleExecutionUtil					ruleExecutionUtil;
	private OverDueRecoveryPostingsUtil			recoveryPostingsUtil;
	private FinanceMainDAO						financeMainDAO;
	private OverdueChargeRecoveryDAO			recoveryDAO;

	// Default Constructor
	public RepayCalculator() {
		super();
	}

	/*
	 * ___________________________________________________________________________________________
	 * 
	 * Main Methods ___________________________________________________________________________________________
	 */

	public RepayData initiateRepay(RepayData repayData, FinanceMain financeMain,
			List<FinanceScheduleDetail> financeScheduleDetails, String sqlRule, SubHeadRule subHeadRule,
			boolean isReCal, String method, Date valueDate, String processMethod) {
		return procInitiateRepay(repayData, financeMain, financeScheduleDetails, sqlRule, subHeadRule, isReCal, method,
				valueDate, processMethod);
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
		logger.debug("Entering");

		//Reset Current Business Application Date
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
		logger.debug("Leaving");
		return repayData;
	}

	// INITIALIZE REPAY PROCESS
	private RepayData initializeRepay(RepayData repayData, FinanceMain financeMain,
			List<FinanceScheduleDetail> financeScheduleDetails) {

		logger.debug("Entering");

		repayData.setFinReference(financeMain.getFinReference());
		repayData.setRepayMain(null);
		repayData.setRepayScheduleDetails(null);

		RepayMain repayMain = new RepayMain();
		repayMain.setFinReference(financeMain.getFinReference());
		repayMain.setFinCcy(financeMain.getFinCcy());
		repayMain.setProfitDaysBais(financeMain.getProfitDaysBasis());
		repayMain.setFinType(financeMain.getFinType());
		repayMain.setLovDescFinTypeName(financeMain.getLovDescFinTypeName());
		repayMain.setFinBranch(financeMain.getFinBranch());
		repayMain.setLovDescFinBranchName(financeMain.getLovDescFinBranchName());
		repayMain.setCustID(financeMain.getCustID());
		repayMain.setLovDescCustCIF(financeMain.getLovDescCustCIF());
		repayMain.setLovDescSalutationName(financeMain.getLovDescSalutationName());
		repayMain.setLovDescCustFName(financeMain.getLovDescCustFName());
		repayMain.setLovDescCustLName(financeMain.getLovDescCustLName());
		repayMain.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
		repayMain.setDateStart(financeMain.getFinStartDate());
		repayMain.setDateMatuirty(financeMain.getMaturityDate());
		repayMain.setAccrued(repayData.getAccruedTillLBD());
		repayMain.setRepayAccountId(financeMain.getRepayAccountId());
		repayMain.setFinAccount(financeMain.getFinAccount());
		repayMain.setFinCustPftAccount(financeMain.getFinCustPftAccount());
		repayMain.setPendindODCharges(repayData.getPendingODC());
		repayMain.setEarlyPayEffectOn(financeMain.getLovDescFinScheduleOn());

		repayMain.setFinAmount(BigDecimal.ZERO);
		repayMain.setCurFinAmount(BigDecimal.ZERO);
		repayMain.setProfit(BigDecimal.ZERO);
		repayMain.setProfitBalance(BigDecimal.ZERO);
		repayMain.setPrincipal(BigDecimal.ZERO);
		repayMain.setPrincipalBalance(BigDecimal.ZERO);
		repayMain.setTotalCapitalize(BigDecimal.ZERO);
		repayMain.setCapitalizeBalance(BigDecimal.ZERO);
		repayMain.setOverduePrincipal(BigDecimal.ZERO);
		repayMain.setOverdueProfit(BigDecimal.ZERO);
		repayMain.setDateLastFullyPaid(financeMain.getFinStartDate());
		repayMain.setDateNextPaymentDue(financeMain.getMaturityDate());
		repayMain.setDownpayment(BigDecimal.ZERO);
		repayMain.setPrincipalPayNow(BigDecimal.ZERO);
		repayMain.setProfitPayNow(BigDecimal.ZERO);
		repayMain.setRefundNow(BigDecimal.ZERO);

		repayMain.setRepayAmountNow(BigDecimal.ZERO);
		repayMain.setRepayAmountExcess(BigDecimal.ZERO);
		repayData.setRepayMain(repayMain);

		repayData = calRepayMain(repayData, financeScheduleDetails);
		logger.debug("Leaving");
		return repayData;
	}

	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	// RECALCULATE REPAYMENTS PROCESS
	private RepayData recalRepay(RepayData repayData, FinanceMain financeMain,
			final List<FinanceScheduleDetail> scheduleDetails, boolean isReCal, String method, String processMethod) {

		logger.debug("Entering");

		repayData.setRepayScheduleDetails(new ArrayList<RepayScheduleDetail>());
		balanceRepayAmount = repayData.getRepayMain().getRepayAmountNow();

		// If no balance for repayment then return with out calculation
		if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
			return repayData;
		}

		//Copy Actual Schedule Details List
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> tempScheduleDetails = cloner.deepClone(scheduleDetails);
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
			List<OverdueChargeRecovery> recList = getRecoveryDAO().getPastSchedulePenalties(
					financeMain.getFinReference());
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
		repayData.getRepayMain().setEarlyPayNextSchDate(null);
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
			if (!chkPastPenalty && schdDate.compareTo(repayData.getRepayMain().getDateLastFullyPaid()) <= 0
					&& schdDate.compareTo(curBussniessDate) != 0) {
				continue;
			}

			// Skip if not a repayment
			if (!(curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(
					BigDecimal.ZERO) > 0))) {
				continue;
			}

			// Skip if indicative rate
			if (curSchd.isCalOnIndRate()) {
				continue;
			}

			// Add schedule repayment if scheduled profit or principal to be paid
			if (curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()).compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()).compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()).compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid()).compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getIncrCost().subtract(curSchd.getIncrCostPaid()).compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getSuplRent().subtract(curSchd.getSuplRentPaid()).compareTo(BigDecimal.ZERO) > 0) {

				repayData = addRepayRecord(financeMain, repayData, curSchd, prvSchd, i, false, isReCal, method, false,
						null, processMethod);
				// No more repayment amount left for next schedules
				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			//Check For Fully Paid Installment to Recover penalty Amount, if Any
			if (chkPastPenalty
					&& (curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()).compareTo(BigDecimal.ZERO) == 0 && curSchd
							.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()).compareTo(BigDecimal.ZERO) == 0)) {

				if (seperatePenaltyProc) {
					if (recMap.containsKey(schdDate)) {
						repayData = addRepayRecord(financeMain, repayData, curSchd, prvSchd, i, false, isReCal, method,
								nxtSchd == null, recMap.get(schdDate), processMethod);
					}
				} else {
					if (nxtSchd == null
							|| (nxtSchd.getProfitSchd().subtract(nxtSchd.getSchdPftPaid()).compareTo(BigDecimal.ZERO) > 0 || nxtSchd
									.getPrincipalSchd().subtract(nxtSchd.getSchdPriPaid()).compareTo(BigDecimal.ZERO) > 0)) {
						repayData = addRepayRecord(financeMain, repayData, curSchd, prvSchd, i, false, isReCal, method,
								nxtSchd == null, null, processMethod);
					}
				}
				// No more repayment amount left for next schedules
				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}

		//Set Penalty Payments for Prepared Past due Schedule Details
		if (!setPastPenalties
				&& (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS) || ImplementationConstants.REPAY_HIERARCHY_METHOD
						.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {

			if (repayData.getRepayScheduleDetails() != null && !repayData.getRepayScheduleDetails().isEmpty()) {
				for (int i = 0; i < repayData.getRepayScheduleDetails().size(); i++) {

					RepayScheduleDetail rpySchd = repayData.getRepayScheduleDetails().get(i);
					if (rpySchd.getPenaltyAmt().compareTo(BigDecimal.ZERO) > 0) {

						applyOverdueCharges(rpySchd, processMethod);

						// No more repayment amount left for next schedules
						if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
							break;
						}

					}
				}

				//Reset Balances for Excess Amount
				repayData.getRepayMain().setRepayAmountNow(balanceRepayAmount);
				repayData.getRepayMain().setRepayAmountExcess(balanceRepayAmount);
			}
		}

		// Process for setting Balance Amount after Payment Apportionment
		repayData = doReversalPayApportionmentForBal(repayData);

		// Find Open for refund amounts
		repayData = calRefunds(repayData, BigDecimal.ZERO, false);
		logger.debug("Leaving");
		return repayData;
	}

	private RepayData calRepayMain(RepayData repayData, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");

		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal cpzTillNow = BigDecimal.ZERO;
		Boolean isNextDueSet = false;
		boolean isSkipLastDateSet = false;

		RepayMain repayMain = repayData.getRepayMain();

		for (int i = 0; i < financeScheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
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

			//Total Fee Amount
			repayMain.setTotalFeeAmt(repayMain.getTotalFeeAmt().add(
					curSchd.getFeeSchd() == null ? BigDecimal.ZERO : curSchd.getFeeSchd()));

			// Overdue Principal and Profit
			if (schdDate.compareTo(curBussniessDate) < 0 && DateUtility.getDaysBetween(curBussniessDate, schdDate) >= 0) {
				cpzTillNow = cpzTillNow.add(curSchd.getCpzAmount());
				repayMain.setOverduePrincipal(repayMain.getOverduePrincipal().add(
						curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())));

				repayMain.setOverdueProfit(repayMain.getOverdueProfit().add(
						curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())));

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

		logger.debug("Leaving");
		return repayData;
	}

	private RepayData addRepayRecord(FinanceMain financeMain, RepayData repayData, FinanceScheduleDetail curSchd,
			FinanceScheduleDetail prvSchd, int schdIndex, boolean isDefSchd, boolean isReCal, String method,
			boolean isLastRcd, OverdueChargeRecovery recovery, String processMethod) {
		logger.debug("Entering");

		BigDecimal toPay = BigDecimal.ZERO;

		RepayScheduleDetail rsd = new RepayScheduleDetail();
		rsd.setFinReference(repayData.getFinReference());
		rsd.setSchDate(curSchd.getSchDate());
		rsd.setDefSchdDate(curSchd.getSchDate());

		// PROFIT PAYMENT
		rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		rsd.setProfitSchd(curSchd.getProfitSchd());
		rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
		rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
		rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());

		// Fee Details on Each Schedule Wise
		rsd.setSchdIns(curSchd.getInsSchd());
		rsd.setSchdInsPaid(curSchd.getSchdInsPaid());
		rsd.setSchdSuplRent(curSchd.getSuplRent());
		rsd.setSchdSuplRentPaid(curSchd.getSuplRentPaid());
		rsd.setSchdIncrCost(curSchd.getIncrCost());
		rsd.setSchdIncrCostPaid(curSchd.getIncrCostPaid());
		rsd.setSchdFee(curSchd.getFeeSchd());
		rsd.setSchdFeePaid(curSchd.getSchdFeePaid());

		if (curBussniessDate.after(curSchd.getSchDate())) {
			rsd.setDaysLate(DateUtility.getDaysBetween(curSchd.getSchDate(), curBussniessDate));
			rsd.setDaysEarly(0);

			try {

				//Finance Repay Queue object Data Preparation
				FinRepayQueue finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(repayData.getRepayMain().getFinReference());
				finRepayQueue.setBranch(repayData.getRepayMain().getFinBranch());
				finRepayQueue.setFinType(repayData.getRepayMain().getFinType());
				finRepayQueue.setCustomerID(repayData.getRepayMain().getCustID());
				finRepayQueue.setRpyDate(curSchd.getSchDate());
				finRepayQueue.setFinPriority(9999);
				finRepayQueue.setFinRpyFor(rsd.getSchdFor());
				finRepayQueue.setSchdPft(rsd.getProfitSchd());
				finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
				finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());
				finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
				finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
				finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));

				if (rsd.getProfitSchd().compareTo(rsd.getProfitSchdPaid()) == 0) {
					finRepayQueue.setSchdIsPftPaid(true);
					if (rsd.getPrincipalSchd().compareTo(rsd.getPrincipalSchdPaid()) == 0) {
						finRepayQueue.setSchdIsPriPaid(true);
					} else {
						finRepayQueue.setSchdIsPriPaid(false);
					}
				} else {
					finRepayQueue.setSchdIsPftPaid(false);
					finRepayQueue.setSchdIsPriPaid(false);
				}

				//Overdue Penalty Recovery Calculation
				boolean recoverPastPenlaty = false;
				if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
						|| ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC)) {

					if (finRepayQueue.isSchdIsPftPaid() && finRepayQueue.isSchdIsPriPaid()) {
						recoverPastPenlaty = true;
					}
				}

				BigDecimal totWaived = BigDecimal.ZERO;
				if (recovery != null) {

					//Total Waived Amount
					totWaived = recovery.getTotWaived();

				} else if (recoverPastPenlaty) {
					//Fetch Max Overdue Charge Recovery for Past Schedule before Processed Schedule Date with Reference 
					recovery = getRecoveryDAO().getPastSchedulePenalty(finRepayQueue.getFinReference(),
							curSchd.getSchDate(), true, false);

					if (recovery == null || recovery.getPenaltyBal().compareTo(BigDecimal.ZERO) <= 0) {
						logger.debug("Leaving");
						return repayData;
					}

					//Total Waived Amount
					totWaived = recovery.getTotWaived();
				} else {

					List<Object> odObjDetailList = getRecoveryPostingsUtil().recoveryCalculation(finRepayQueue,
							financeMain.getProfitDaysBasis(), curBussniessDate, false, true);

					totWaived = ((FinODDetails) odObjDetailList.get(0)).getTotWaived();
					recovery = (OverdueChargeRecovery) odObjDetailList.get(1);
				}

				if (recovery != null) {

					rsd.setPenaltyAmt(recovery.getPenaltyBal());
					rsd.setChargeType(recovery.getPenaltyType());

					//Max Waiver Amount Calculation
					BigDecimal maxCalWaiver = (recovery.getPenaltyBal().multiply(recovery.getMaxWaiver())).divide(
							new BigDecimal(100), 0, RoundingMode.HALF_DOWN);

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

				//Resetting Object
				finRepayQueue = null;

			} catch (Exception e) {
				logger.error("Exception: ", e);
				rsd.setPenaltyAmt(BigDecimal.ZERO);
				rsd.setMaxWaiver(BigDecimal.ZERO);
			}

		} else {

			rsd.setDaysLate(0);
			rsd.setDaysEarly(DateUtility.getDaysBetween(curBussniessDate, curSchd.getSchDate()));

			if (setEarlyPayAmt && repayData.getRepayMain().getEarlyPayNextSchDate() == null) {
				repayData.getRepayMain().setEarlyPayNextSchDate(curSchd.getSchDate());
			}

			//Set Penalty Payments for Prepared Past due Schedule Details
			if (!setPastPenalties
					&& (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS) || ImplementationConstants.REPAY_HIERARCHY_METHOD
							.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {

				if (repayData.getRepayScheduleDetails() != null && !repayData.getRepayScheduleDetails().isEmpty()) {
					for (int i = 0; i < repayData.getRepayScheduleDetails().size(); i++) {

						RepayScheduleDetail rpySchd = repayData.getRepayScheduleDetails().get(i);
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

			//To Stop Penalty Setting From Remaining Balance
			setPastPenalties = true;
			// No more repayment amount left for next schedules
			if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
				repayData.getRepayMain().setRepayAmountNow(balanceRepayAmount);
				repayData.getRepayMain().setRepayAmountExcess(balanceRepayAmount);

				logger.debug("Leaving");
				return repayData;
			}

			if (rsd.getDaysEarly() >= 0) {

				final BigDecimal earlypayAmt = balanceRepayAmount;
				if (rsd.getDaysEarly() == 0) {
					if (earlypayAmt.compareTo(curSchd.getRepayAmount()) > 0) {
						repayData.getRepayMain().setEarlyPay(true);
						setEarlyPayAmt = true;

						if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
							repayData.getRepayMain().setEarlyPayAmount(earlypayAmt.add(curSchd.getRepayAmount()));
						} else {
							repayData.getRepayMain().setEarlyPayAmount(earlypayAmt);
						}
						repayData.getRepayMain().setEarlyPayOnSchDate(curSchd.getSchDate());
						repayData.getRepayMain().setEarlyRepayNewSchd(null);
					}
				} else {
					repayData.getRepayMain().setEarlyPay(true);
					if (!setEarlyPayAmt) {

						setEarlyPayAmt = true;
						repayData.getRepayMain().setEarlyPayAmount(earlypayAmt);

						if ("NONSCH".equals(SysParamUtil.getValueAsString("EARLYPAY_TERM_INS"))) {
							repayData.getRepayMain().setEarlyPayOnSchDate(curBussniessDate);
							repayData.getRepayMain().setEarlyPayNextSchDate(curSchd.getSchDate());
						} else {
							repayData.getRepayMain().setEarlyPayOnSchDate(curSchd.getSchDate());
						}

						if (!scheduleMap.containsKey(curBussniessDate)) {
							FinanceScheduleDetail newSchdlEP = new FinanceScheduleDetail(repayData.getRepayMain()
									.getFinReference());
							newSchdlEP.setDefSchdDate(repayData.getRepayMain().getEarlyPayOnSchDate());
							newSchdlEP.setSchDate(repayData.getRepayMain().getEarlyPayOnSchDate());
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
							repayData.getRepayMain().setEarlyRepayNewSchd(newSchdlEP);
						} else {
							FinanceScheduleDetail detail = scheduleMap.get(curBussniessDate);
							if (isReCal) {
								repayData.getRepayMain().setEarlyPayAmount(detail.getRepayAmount());
							} else {
								repayData.getRepayMain().setEarlyPayAmount(earlypayAmt.add(detail.getRepayAmount()));
							}
						}
					}
				}
			}
		}

		// Fee Details Payment First from Schedule Details in order as below
		//	1. Ins Fee Amount (Life Ins)
		//	2. Property Ins Fee Amount
		//	3. Credit insurance (Fixed Fee Schedule based amount)
		//	4. Schedule payment Supplementary Rent
		//	5. Schedule payment Increased Cost Amount
		//	6. Schedule payment Fee Charge Amount

		// Payment Process only for Installment or No default Payment Exists
		if (StringUtils.equals(repayData.getRepayMain().getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT)
				|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

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

			//Profit Amount Checking
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
			toPay = rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid());

			// Payment Process only for Installment or Profit only from Schedules or No default Payment Exists
			if (StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
					FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
							FinanceConstants.PAY_APPORTIONMENT_SPFT)
					|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setProfitSchdPayNow(toPay);
				} else {
					rsd.setProfitSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
			} else {
				rsd.setProfitSchdPayNow(BigDecimal.ZERO);
			}
			repayData.getRepayMain().setProfitPayNow(
					repayData.getRepayMain().getProfitPayNow().add(rsd.getProfitSchdPayNow()));

			//Principal Amount Checking
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
			toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

			// Payment Process only for Installment or Principal only from Schedules or No default Payment Exists
			if (StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
					FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
							FinanceConstants.PAY_APPORTIONMENT_SPRI)
					|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setPrincipalSchdPayNow(toPay);
				} else {
					rsd.setPrincipalSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
			} else {
				rsd.setPrincipalSchdPayNow(BigDecimal.ZERO);
			}
			repayData.getRepayMain().setPrincipalPayNow(
					repayData.getRepayMain().getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));

		} else if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {

			//Principal Amount Checking
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
			toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

			// Payment Process only for Installment or Principal only from Schedules or No default Payment Exists
			if (StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
					FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
							FinanceConstants.PAY_APPORTIONMENT_SPRI)
					|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setPrincipalSchdPayNow(toPay);
				} else {
					rsd.setPrincipalSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
			} else {
				rsd.setPrincipalSchdPayNow(BigDecimal.ZERO);
			}
			repayData.getRepayMain().setPrincipalPayNow(
					repayData.getRepayMain().getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));

			//Profit Amount Checking
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
			toPay = rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid());
			// Payment Process only for Installment or Profit only from Schedules or No default Payment Exists
			if (StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
					FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
							FinanceConstants.PAY_APPORTIONMENT_SPFT)
					|| StringUtils.equals(repayData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setProfitSchdPayNow(toPay);
				} else {
					rsd.setProfitSchdPayNow(balanceRepayAmount);
				}
				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
			} else {
				rsd.setProfitSchdPayNow(BigDecimal.ZERO);
			}
			repayData.getRepayMain().setProfitPayNow(
					repayData.getRepayMain().getProfitPayNow().add(rsd.getProfitSchdPayNow()));
		}

		// Based on repayments method then do charges postings first then profit or principal
		// C - PENALTY / CHRAGES, P - PRINCIPAL , I - PROFIT / INTEREST
		if (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPC)
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC))) {
			applyOverdueCharges(rsd, processMethod);
		}

		rsd.setRepayNet(rsd.getProfitSchdPayNow().add(rsd.getPrincipalSchdPayNow()).add(rsd.getSchdInsPayNow())
				.add(rsd.getSchdSuplRentPayNow()).add(rsd.getSchdIncrCostPayNow()).add(rsd.getSchdFeePayNow()));
		rsd.setRepayBalance(rsd.getPrincipalSchdBal().add(rsd.getProfitSchdBal()).add(rsd.getSchdInsBal())
				.add(rsd.getSchdSuplRentBal()).add(rsd.getSchdIncrCostBal()).add(rsd.getSchdFeeBal()).subtract(rsd.getRepayNet()));
		rsd.setRefundMax(BigDecimal.ZERO);
		rsd.setRefundReq(BigDecimal.ZERO);
		rsd.setAllowRefund(false);
		rsd.setSchdIndex(schdIndex);

		repayData.getRepayMain().setRepayAmountNow(balanceRepayAmount);
		repayData.getRepayMain().setRepayAmountExcess(balanceRepayAmount);
		repayData.getRepayScheduleDetails().add(rsd);

		logger.debug("Leaving");
		return repayData;
	}

	private RepayScheduleDetail applyOverdueCharges(RepayScheduleDetail rsd, String processMethod) {
		//Overdue charge Amount Checking
		if (rsd.getPenaltyAmt().compareTo(BigDecimal.ZERO) > 0) {

			if (balanceRepayAmount.compareTo(rsd.getPenaltyAmt()) > 0) {
				rsd.setPenaltyPayNow(rsd.getPenaltyAmt());
			} else {
				rsd.setPenaltyPayNow(balanceRepayAmount);
			}

			// Addition for Penalty Amount not added before calculation.
			// First add Penalty amount to Total Repayment Amount / Balance Repayment amount and 
			// adjust same to each schedule in reverse entries
			if (processMethod.equals(FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
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

		//	1. Ins Fee Amount
		if (balanceRepayAmount.compareTo(rsd.getSchdIns().subtract(rsd.getSchdInsPaid())) > 0) {
			rsd.setSchdInsPayNow(rsd.getSchdIns().subtract(rsd.getSchdInsPaid()));
		} else {
			rsd.setSchdInsPayNow(balanceRepayAmount);
		}
		rsd.setSchdInsBal(rsd.getSchdIns().subtract(rsd.getSchdInsPaid()));
		balanceRepayAmount = balanceRepayAmount.subtract(rsd.getSchdInsPayNow());

		//	2. Supplementary Rent Amount
		if (balanceRepayAmount.compareTo(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid())) > 0) {
			rsd.setSchdSuplRentPayNow(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid()));
		} else {
			rsd.setSchdSuplRentPayNow(balanceRepayAmount);
		}
		rsd.setSchdSuplRentBal(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid()));
		balanceRepayAmount = balanceRepayAmount.subtract(rsd.getSchdSuplRentPayNow());

		//	3. Increased Cost Amount
		if (balanceRepayAmount.compareTo(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid())) > 0) {
			rsd.setSchdIncrCostPayNow(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid()));
		} else {
			rsd.setSchdIncrCostPayNow(balanceRepayAmount);
		}
		rsd.setSchdIncrCostBal(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid()));
		balanceRepayAmount = balanceRepayAmount.subtract(rsd.getSchdIncrCostPayNow());

		//	4. Scheduled Fee Amount
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
		logger.debug("Entering");

		if (this.sqlRule == null || subHeadRule == null) {
			if(isManualProc && manualRefundAmt.compareTo(BigDecimal.ZERO) > 0){
				repayData.setMaxRefundAmt(BigDecimal.ZERO);
				repayData.setSufficientRefund(false);
			}
			logger.debug("Leaving");
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
		//Refund Rule Execution for Max Allowed Refund Amount
		BigDecimal refundResult = BigDecimal.ZERO;
		subHeadRule.setREFUNDPFT(calRefundPft);
		refundResult = (BigDecimal) getRuleExecutionUtil().executeRule(this.sqlRule,
				this.subHeadRule.getDeclaredFieldValues(),repayData.getRepayMain().getFinCcy(), RuleReturnType.DECIMAL);

		//Check For Maximum Allowed Refund Amount
		if (isManualProc && manualRefundAmt.compareTo(refundResult) > 0) {
			repayData.setMaxRefundAmt(refundResult);
			repayData.setSufficientRefund(false);

			logger.debug("Leaving");
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
		logger.debug("Leaving");
		return repayData;
	}

	/**
	 * Method for adjusting Balance amount after Payment Apportionment, if Exists any.
	 */
	private RepayData doReversalPayApportionmentForBal(RepayData repayData) {

		// Set Remaining Balance Amount for Payment Apportionment Method
		if (!(StringUtils.equals(repayData.getRepayMain().getPayApportionment(), PennantConstants.List_Select) || StringUtils
				.equals(repayData.getRepayMain().getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT))
				&& balanceRepayAmount.compareTo(BigDecimal.ZERO) > 0) {

			List<RepayScheduleDetail> rpySchdDetails = repayData.getRepayScheduleDetails();

			for (RepayScheduleDetail rsd : rpySchdDetails) {

				// Adjust Remaining Balance to Fee Details on particular Schedule
				schdFeeCollectionProcess(rsd);

				// Adjust Remaiing balance to Either Principal or Profit based on (Reversal)Payment Apportionment Method
				BigDecimal toPay = BigDecimal.ZERO;

				//Profit Amount Checking
				if (StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
						FinanceConstants.PAY_APPORTIONMENT_SPRI)) {

					rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
					toPay = rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid());

					if (balanceRepayAmount.compareTo(toPay) >= 0) {
						rsd.setProfitSchdPayNow(toPay);
					} else {
						rsd.setProfitSchdPayNow(balanceRepayAmount);
					}

					balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
					repayData.getRepayMain().setProfitPayNow(
							repayData.getRepayMain().getProfitPayNow().add(rsd.getProfitSchdPayNow()));
				}

				//Principal Amount Checking
				if (StringUtils.equals(repayData.getRepayMain().getPayApportionment(),
						FinanceConstants.PAY_APPORTIONMENT_SPFT)) {
					rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
					toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

					if (balanceRepayAmount.compareTo(toPay) >= 0) {
						rsd.setPrincipalSchdPayNow(toPay);
					} else {
						rsd.setPrincipalSchdPayNow(balanceRepayAmount);
					}

					balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
					repayData.getRepayMain().setPrincipalPayNow(
							repayData.getRepayMain().getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));
				}

				rsd.setRepayNet(rsd.getProfitSchdPayNow().add(rsd.getPrincipalSchdPayNow())
						.add(rsd.getSchdInsPayNow()).add(rsd.getSchdSuplRentPayNow())
						.add(rsd.getSchdIncrCostPayNow()).add(rsd.getSchdFeePayNow()));
				rsd.setRepayBalance(rsd.getPrincipalSchdBal().add(rsd.getProfitSchdBal()).add(rsd.getSchdInsBal())
						.add(rsd.getSchdSuplRentBal()).add(rsd.getSchdIncrCostBal()).add(rsd.getSchdFeeBal()).subtract(rsd.getRepayNet()));
				repayData.getRepayMain().setRepayAmountNow(balanceRepayAmount);
				repayData.getRepayMain().setRepayAmountExcess(balanceRepayAmount);

				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}
		logger.debug("Leaving");
		return repayData;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public RepayData getRepayData() {
		return this.repayData;
	}

	public void setRepayData(RepayData repayData) {
		this.repayData = repayData;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}

	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
		return recoveryPostingsUtil;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public OverdueChargeRecoveryDAO getRecoveryDAO() {
		return recoveryDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

}
