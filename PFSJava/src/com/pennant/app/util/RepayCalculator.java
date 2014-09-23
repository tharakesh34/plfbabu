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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.SubHeadRule;
import com.pennant.backend.util.PennantConstants;
import com.rits.cloning.Cloner;

public class RepayCalculator implements Serializable {

    private static final long serialVersionUID = 8062681791631293126L;
    private static Logger logger = Logger.getLogger(RepayCalculator.class);
    
	private RepayData repayData;
	private BigDecimal balanceRepayAmount = BigDecimal.ZERO;
	boolean setEarlyPayAmt = false;
	private String sqlRule = "";
	private SubHeadRule subHeadRule;
	private Map<Date,FinanceScheduleDetail> scheduleMap = null;

	Date curBussniessDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
	private static DefermentDetailDAO defermentDetailDAO;
	private static RuleExecutionUtil ruleExecutionUtil;
	private static OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	private static FinanceMainDAO financeMainDAO;
	
	// Default Constructor
	public RepayCalculator() {
		super();
	}

	/*
	 * ___________________________________________________________________________________________
	 * 
	 * Main Methods 
	 * ___________________________________________________________________________________________
	 */
	
	public static RepayData initiateRepay(RepayData repayData, FinanceMain financeMain,
	        List<FinanceScheduleDetail> financeScheduleDetails, String sqlRule,
	        SubHeadRule subHeadRule, boolean isReCal, String method, Date valueDate) {
		return new RepayCalculator(repayData, financeMain, financeScheduleDetails, sqlRule,
		        subHeadRule, isReCal, method,valueDate).getRepayData();
	}

	private RepayCalculator(RepayData repayData, FinanceMain financeMain,
	        List<FinanceScheduleDetail> financeScheduleDetails, String sqlRule,
	        SubHeadRule subHeadRule, boolean isReCal, String method, Date valueDate) {
		setRepayData(procInitiateRepay(repayData, financeMain, financeScheduleDetails, sqlRule,
		        subHeadRule, isReCal,method, valueDate));
	}
	
	public static RepayData calculateRefunds(RepayData repayData, BigDecimal manualRefundAmt, boolean isManualProc, String sqlRule,
	        SubHeadRule subHeadRule) {
		return new RepayCalculator(repayData, manualRefundAmt, isManualProc, sqlRule, subHeadRule).getRepayData();
	}
	
	private RepayCalculator(RepayData repayData, BigDecimal manualRefundAmt, boolean isManualProc, String sqlRule,
	        SubHeadRule subHeadRule) {
		this.sqlRule = sqlRule ;
		this.subHeadRule = subHeadRule;
		setRepayData(calRefunds(repayData, manualRefundAmt, isManualProc));
	}

	/** To Calculate the Amounts for given schedule */
	public RepayData procInitiateRepay(RepayData repayData, FinanceMain financeMain,
	        List<FinanceScheduleDetail> financeScheduleDetails, String sqlRule,
	        SubHeadRule subHeadRule, boolean isReCal, String method, Date valueDate) {
		logger.debug("Entering");
		
		//Reset Current Business Application Date
		if(valueDate != null){
			curBussniessDate = valueDate;
		}
		
		// Initialize Repay
		if (repayData.getBuildProcess().equals("I")) {
			repayData = initializeRepay(repayData, financeMain, financeScheduleDetails);
		}

		// Recalculate Repay
		if (repayData.getBuildProcess().equals("R")) {
			this.sqlRule = sqlRule;
			this.subHeadRule = subHeadRule;
			repayData = recalRepay(repayData, financeMain, financeScheduleDetails, isReCal, method);
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
		repayMain.setLovDescFinCcyName(financeMain.getLovDescFinCcyName());
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

	// RECALCULATE REPAYMENTS PROCESS
	private RepayData recalRepay(RepayData repayData, FinanceMain financeMain,
	       final List<FinanceScheduleDetail> scheduleDetails, boolean isReCal, String method) {
		
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
		
		setEarlyPayAmt = false;
		scheduleMap = new HashMap<Date, FinanceScheduleDetail>();
		repayData.getRepayMain().setEarlyPayNextSchDate(null);
		// Load Pending Repay Schedules until balance available for payment
		for (int i = 1; i < tempScheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = tempScheduleDetails.get(i);
			FinanceScheduleDetail prvSchd = tempScheduleDetails.get(i-1);
			Date schdDate = curSchd.getSchDate();
			scheduleMap.put(schdDate, curSchd);

			// Skip if repayment already competed
			if (schdDate.compareTo(repayData.getRepayMain().getDateLastFullyPaid()) <= 0
					&& schdDate.compareTo(curBussniessDate) != 0) {
				continue;
			}

			// Skip if not a repayment
			if (!curSchd.isDeferedPay() && !(curSchd.isRepayOnSchDate() || 
					(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
				continue;
			}

			// Skip if indicative rate
			if (curSchd.isCalOnIndRate()) {
				continue;
			}
			
			// Add deferment record if deferred profit or principal to be paid
			if (curSchd.getDefProfitSchd().subtract(curSchd.getDefSchdPftPaid())
			        .compareTo(BigDecimal.ZERO) > 0
			        || curSchd.getDefPrincipalSchd().subtract(curSchd.getDefSchdPriPaid())
			                .compareTo(BigDecimal.ZERO) > 0) {
				repayData = addRepayRecord(repayData, curSchd, prvSchd,  i, true, isReCal,method);
				// No more repayment amount left for next schedules
				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// Add schedule repayment if scheduled profit or principal to be paid
			if (curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()).compareTo(BigDecimal.ZERO) > 0
			        || curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())
			                .compareTo(BigDecimal.ZERO) > 0) {

				repayData = addRepayRecord(repayData, curSchd,prvSchd, i, false, isReCal, method);
				// No more repayment amount left for next schedules
				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}

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
			repayMain.setFinAmount(repayMain.getFinAmount().add(curSchd.getDisbAmount()).add(
					curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : curSchd.getFeeChargeAmt()));

			repayMain.setCurFinAmount(repayMain.getFinAmount().subtract(curSchd.getSchdPriPaid())
			                .subtract(curSchd.getSchdPftPaid())
			                .subtract(curSchd.getDefSchdPriPaid())
			                .subtract(curSchd.getDefSchdPftPaid())
			                .subtract(curSchd.getDownPaymentAmount()));

			if (schdDate.compareTo(curBussniessDate) < 0) {
				repayMain.setCurFinAmount(repayMain.getCurFinAmount().subtract(
				        curSchd.getCpzAmount()));
			}

			// Profit scheduled and Paid
			repayMain.setProfit(repayMain.getProfit().add(curSchd.getProfitSchd())
			        .add(curSchd.getDefProfitSchd()));
			pftPaid = pftPaid.add(curSchd.getSchdPftPaid()).add(curSchd.getDefSchdPftPaid());

			// Principal scheduled and Paid
			repayMain.setPrincipal(repayMain.getPrincipal().add(curSchd.getPrincipalSchd())
			        .add(curSchd.getDefPrincipalSchd()));
			priPaid = priPaid.add(curSchd.getSchdPriPaid()).add(curSchd.getDefSchdPriPaid());

			// Capitalization and Capitalized till now
			repayMain.setTotalCapitalize(repayMain.getTotalCapitalize().add(curSchd.getCpzAmount()));
			
			//Total Fee Amount
			repayMain.setTotalFeeAmt(repayMain.getTotalFeeAmt().add(
					curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : curSchd.getFeeChargeAmt()));

			// Overdue Principal and Profit
			if (schdDate.compareTo(curBussniessDate) < 0 && DateUtility.getDaysBetween(curBussniessDate, schdDate) >= 0) { 
					//Integer.parseInt(SystemParameterDetails.getSystemParameterValue("ODC_GRACE").toString())) {
				cpzTillNow = cpzTillNow.add(curSchd.getCpzAmount());
				repayMain.setOverduePrincipal(repayMain.getOverduePrincipal().add(curSchd.getPrincipalSchd()
				        .add(curSchd.getDefPrincipalSchd()).subtract(curSchd.getSchdPriPaid())
				        .subtract(curSchd.getDefSchdPriPaid())));

				repayMain.setOverdueProfit(repayMain.getOverdueProfit().add(curSchd.getProfitSchd().add(curSchd.getDefProfitSchd())
				        .subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDefSchdPftPaid())));

			}

			// Down Payment
			repayMain.setDownpayment(repayMain.getDownpayment().add(curSchd.getDownPaymentAmount()));

			// REPAY SCHEDULE RECORD
			if (curSchd.isRepayOnSchDate() || curSchd.isDeferedPay() || 
					(curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				BigDecimal balance = curSchd.getPrincipalSchd().add(curSchd.getDefPrincipalSchd())
				        .subtract(curSchd.getSchdPriPaid()).subtract(curSchd.getDefSchdPriPaid());
				balance = balance.add(curSchd.getProfitSchd()).add(curSchd.getDefProfitSchd())
				        .subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDefSchdPftPaid());

				if (balance.compareTo(BigDecimal.ZERO) > 0) {
					isSkipLastDateSet = true;
				}

				// Set the last and next scheduled repayments to deferred dates (Agreed with customer)
				if (balance.equals(BigDecimal.ZERO) && !isSkipLastDateSet) {
					repayMain.setDateLastFullyPaid(curSchd.getDefSchdDate());
				}

				if (balance.compareTo(BigDecimal.ZERO) > 0 && !isNextDueSet) {
					repayMain.setDateNextPaymentDue(curSchd.getDefSchdDate());
					isNextDueSet = true;
				}
			}
		}

		repayMain.setProfitBalance(repayMain.getProfit().subtract(pftPaid));
		repayMain.setPrincipalBalance(repayMain.getPrincipal().subtract(priPaid));
		
		logger.debug("Leaving");
		return repayData;
	}

	private RepayData addRepayRecord(RepayData repayData, FinanceScheduleDetail curSchd,
			FinanceScheduleDetail prvSchd, int schdIndex, boolean isDefSchd, boolean isReCal, String method) {
		logger.debug("Entering");
		
		BigDecimal toPay = BigDecimal.ZERO;

		RepayScheduleDetail rsd = new RepayScheduleDetail();
		rsd.setFinReference(repayData.getFinReference());
		rsd.setSchDate(curSchd.getSchDate());
		rsd.setDefSchdDate(curSchd.getDefSchdDate());

		// PROFIT PAYMENT
		if (isDefSchd) {
			rsd.setSchdFor(PennantConstants.DEFERED);
			rsd.setProfitSchd(curSchd.getDefProfitSchd());
			rsd.setProfitSchdPaid(curSchd.getDefSchdPftPaid());
			rsd.setPrincipalSchd(curSchd.getDefPrincipalSchd());
			rsd.setPrincipalSchdPaid(curSchd.getDefSchdPriPaid());
		} else {
			rsd.setSchdFor(PennantConstants.SCHEDULE);
			rsd.setProfitSchd(curSchd.getProfitSchd());
			rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
			rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
			rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
		}
		
		if (curBussniessDate.after(curSchd.getDefSchdDate())) {
			rsd.setDaysLate(DateUtility.getDaysBetween(curSchd.getDefSchdDate(), curBussniessDate));
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
				
				if(rsd.getProfitSchd().compareTo(rsd.getProfitSchdPaid()) == 0){
					finRepayQueue.setSchdIsPftPaid(true);
					if(rsd.getPrincipalSchd().compareTo(rsd.getPrincipalSchdPaid()) == 0){
						finRepayQueue.setSchdIsPriPaid(true);
					}else{
						finRepayQueue.setSchdIsPriPaid(false);
					}
				}else{
					finRepayQueue.setSchdIsPftPaid(false);
					finRepayQueue.setSchdIsPriPaid(false);
				}
				 
				//Overdue Penalty Recovery Calculation
				FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finRepayQueue.getFinReference(), "", false);
				List<Object> odObjDetailList = getRecoveryPostingsUtil().recoveryProcess(financeMain, finRepayQueue,
						curBussniessDate, false, false, false, Long.MIN_VALUE, repayData.getFinanceType().getFinDivision());
				
				FinODDetails odDetails = (FinODDetails) odObjDetailList.get(0);
				OverdueChargeRecovery recovery = (OverdueChargeRecovery) odObjDetailList.get(1);
				
				if(recovery != null){
					
					rsd.setPenaltyAmt(recovery.getPenaltyBal());
					rsd.setChargeType(recovery.getPenaltyType());
					
					//Max Waiver Amount Calculation
					BigDecimal maxCalWaiver = (recovery.getPenaltyBal().multiply(recovery.getMaxWaiver())).divide(
							new BigDecimal(100),0,RoundingMode.HALF_DOWN);
					
					if((maxCalWaiver.subtract(odDetails.getTotWaived())).compareTo(BigDecimal.ZERO) > 0){
						rsd.setMaxWaiver(maxCalWaiver.subtract(odDetails.getTotWaived()));
						rsd.setAllowWaiver(true);
					}else{
						rsd.setMaxWaiver(BigDecimal.ZERO);
					}
					
				}else{
					rsd.setPenaltyAmt(BigDecimal.ZERO);
					rsd.setMaxWaiver(BigDecimal.ZERO);
				}
				
            } catch (Exception e) {
	            logger.error(e.getMessage());//TODO For Error Capture
	            rsd.setPenaltyAmt(BigDecimal.ZERO);
				rsd.setMaxWaiver(BigDecimal.ZERO);
            }
			
		} else {
			rsd.setDaysLate(0);
			rsd.setDaysEarly(DateUtility.getDaysBetween(curBussniessDate, curSchd.getDefSchdDate()));
			
			if(setEarlyPayAmt && repayData.getRepayMain().getEarlyPayNextSchDate() == null){
				repayData.getRepayMain().setEarlyPayNextSchDate(curSchd.getDefSchdDate());
			}
			
			if(rsd.getDaysEarly() >= 0){
				
				final BigDecimal earlypayAmt = balanceRepayAmount;
				if(rsd.getDaysEarly() == 0){
					if(earlypayAmt.compareTo(curSchd.getRepayAmount()) > 0){
						repayData.getRepayMain().setEarlyPay(true);
						setEarlyPayAmt = true;
						
						if(curSchd.isSchPftPaid() && curSchd.isSchPriPaid()){
							repayData.getRepayMain().setEarlyPayAmount(earlypayAmt.add(curSchd.getRepayAmount()));
						}else{
							repayData.getRepayMain().setEarlyPayAmount(earlypayAmt);
						}
						repayData.getRepayMain().setEarlyPayOnSchDate(curSchd.getDefSchdDate());
						repayData.getRepayMain().setEarlyRepayNewSchd(null);
					}
				}else{
					repayData.getRepayMain().setEarlyPay(true);
					if(!setEarlyPayAmt){
						
						setEarlyPayAmt = true;
						repayData.getRepayMain().setEarlyPayAmount(earlypayAmt);
						
						if("NONSCH".equals(SystemParameterDetails.getSystemParameterValue("EARLYPAY_TERM_INS").toString())){
							repayData.getRepayMain().setEarlyPayOnSchDate(curBussniessDate);
							repayData.getRepayMain().setEarlyPayNextSchDate(curSchd.getDefSchdDate());
						}else{
							repayData.getRepayMain().setEarlyPayOnSchDate(curSchd.getDefSchdDate());
						}
						
						if(!scheduleMap.containsKey(curBussniessDate)){
							FinanceScheduleDetail newSchdlEP = new FinanceScheduleDetail(repayData.getRepayMain().getFinReference());
							newSchdlEP.setDefSchdDate(repayData.getRepayMain().getEarlyPayOnSchDate());
							newSchdlEP.setSchDate(repayData.getRepayMain().getEarlyPayOnSchDate());
							newSchdlEP.setSchSeq(1);
							newSchdlEP.setSpecifier("R");
							newSchdlEP.setRepayOnSchDate(true);
							newSchdlEP.setPftOnSchDate(true);
							newSchdlEP.setSchdMethod(prvSchd.getSchdMethod());
							newSchdlEP.setBaseRate(prvSchd.getBaseRate());
							newSchdlEP.setSplRate(prvSchd.getSplRate());
							newSchdlEP.setMrgRate(prvSchd.getMrgRate());
							newSchdlEP.setActRate(prvSchd.getActRate());
							newSchdlEP.setCalculatedRate(prvSchd.getCalculatedRate());
							newSchdlEP.setEarlyPaidBal(prvSchd.getEarlyPaidBal());

							if(CalculationConstants.EARLYPAY_RECPFI.equals(method)){
								newSchdlEP.setEarlyPaid(earlypayAmt);
							}
							repayData.getRepayMain().setEarlyRepayNewSchd(newSchdlEP);
						}else{
							FinanceScheduleDetail detail = scheduleMap.get(curBussniessDate);
							if(isReCal){
								repayData.getRepayMain().setEarlyPayAmount(detail.getRepayAmount());
							}else{
								repayData.getRepayMain().setEarlyPayAmount(earlypayAmt.add(detail.getRepayAmount()));
							}
						}
					}
				}
			}
		}
		
		//Profit Amount Checking
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

		//Principal Amount Checking
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

		rsd.setRepayNet(rsd.getProfitSchdPayNow().add(rsd.getPrincipalSchdPayNow()));
		rsd.setRepayBalance(rsd.getPrincipalSchdBal().add(rsd.getProfitSchdBal())
		        .subtract(rsd.getPrincipalSchdPayNow()).subtract(rsd.getProfitSchdPayNow()));
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

	/**
	 * Method for Calculation Refund Amount Either Manual Process Or in Auto Calculation
	 * @param repayData
	 * @param manualRefundAmt
	 * @param isManualProc
	 * @return
	 */
	private RepayData calRefunds(RepayData repayData, BigDecimal manualRefundAmt, boolean isManualProc) {
		logger.debug("Entering");
		
		String refundWhenPastDefDue = StringUtils.trimToEmpty(SystemParameterDetails
		        .getSystemParameterValue("REFUND_PAST_DEF_DUE").toString());
		
		BigDecimal calRefundPft = BigDecimal.ZERO;
		boolean curTermExcluded = false;
		repayData.getRepayMain().setRefundCalStartDate(null);

		for (int i = 0; i < repayData.getRepayScheduleDetails().size(); i++) {
			RepayScheduleDetail rsd = repayData.getRepayScheduleDetails().get(i);

			if(curBussniessDate.compareTo(rsd.getSchDate()) <= 0){
				if(!curTermExcluded){
					curTermExcluded = true;
					rsd.setAllowRefund(true);
					rsd.setRefundMax(BigDecimal.ZERO);
				}else{
					rsd.setRefundMax(BigDecimal.ZERO);
					if(repayData.getRepayMain().getRefundCalStartDate() == null){
						repayData.getRepayMain().setRefundCalStartDate(rsd.getSchDate());
					}
					if (refundWhenPastDefDue.equals(PennantConstants.NO)) {
						if (!isPastDefDueInFuture(repayData.getFinReference(), rsd.getDefSchdDate(), rsd.getDefSchdDate())) {
							rsd.setAllowRefund(false);
							rsd.setRefundMax(BigDecimal.ZERO);
						} else {
							rsd.setAllowRefund(true);
							calRefundPft = calRefundPft.add(rsd.getProfitSchdPayNow());
						}
					} else {
						rsd.setAllowRefund(true);
						calRefundPft = calRefundPft.add(rsd.getProfitSchdPayNow());
					}
				}
			}else{
				rsd.setAllowRefund(false);
				rsd.setRefundMax(BigDecimal.ZERO);
			}
		}
		
		//Refund Rule Execution for Max Allowed Refund Amount
		BigDecimal refundResult = BigDecimal.ZERO;
		subHeadRule.setREFUNDPFT(calRefundPft);
		refundResult = new BigDecimal(getRuleExecutionUtil().executeRule(this.sqlRule,
				this.subHeadRule, SystemParameterDetails.getGlobaVariableList(),repayData.getRepayMain().getFinCcy()).toString());

		//Check For Maximum Allowed Refund Amount
		if(isManualProc && manualRefundAmt.compareTo(refundResult) > 0){
			repayData.setMaxRefundAmt(refundResult);
			repayData.setSufficientRefund(false);
			
			logger.debug("Leaving");
			return repayData;
		}else if(isManualProc){
			refundResult = manualRefundAmt;
		}
		
		int size = repayData.getRepayScheduleDetails().size();
		for (int i = size -1 ; i >= 0; i--) {
			RepayScheduleDetail rsd = repayData.getRepayScheduleDetails().get(i);

			if (rsd.isAllowRefund() && refundResult.compareTo(BigDecimal.ZERO) > 0 && 
					rsd.getProfitSchdPayNow().compareTo(BigDecimal.ZERO) > 0) {
				rsd.setAllowRefund(true);
				if(rsd.getProfitSchdPayNow().compareTo(refundResult) >= 0){
					rsd.setRefundMax(refundResult);
					rsd.setRefundReq(refundResult);
					refundResult = BigDecimal.ZERO;
				}else{
					rsd.setRefundMax(rsd.getProfitSchdPayNow());
					rsd.setRefundReq(rsd.getProfitSchdPayNow());
					refundResult = refundResult.subtract(rsd.getProfitSchdPayNow());
				}
			} else {
				rsd.setAllowRefund(false);
				rsd.setRefundMax(BigDecimal.ZERO);
				rsd.setRefundReq(BigDecimal.ZERO);
			}

			/* before reversal case
			 * if((i == repayData.getRepayScheduleDetails().size()-1) && 
					refundResult.compareTo(BigDecimal.ZERO) > 0){
				rsd.setAllowRefund(true);
				rsd.setRefundMax(refundResult);
			}*/
		}
		logger.debug("Leaving");
		return repayData;
	}

	private boolean isPastDefDueInFuture(String finReference, Date defSchdDate, Date defRpySchdDate) {
		if (getDefermentDetailDAO().getFinReferenceCount(finReference, defSchdDate, defRpySchdDate) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public RepayData getRepayData() {
		return this.repayData;
	}
	public void setRepayData(RepayData repayData) {
		this.repayData = repayData;
	}

	public static DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		RepayCalculator.defermentDetailDAO = defermentDetailDAO;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
	    RepayCalculator.ruleExecutionUtil = ruleExecutionUtil;
    }
	public static RuleExecutionUtil getRuleExecutionUtil() {
	    return ruleExecutionUtil;
    }

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
	    RepayCalculator.recoveryPostingsUtil = recoveryPostingsUtil;
    }
	public static OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
	    return recoveryPostingsUtil;
    }

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		RepayCalculator.financeMainDAO = financeMainDAO;
    }

	public static FinanceMainDAO getFinanceMainDAO() {
	    return financeMainDAO;
    }

}
