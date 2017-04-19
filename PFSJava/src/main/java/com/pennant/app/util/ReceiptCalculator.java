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
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.rits.cloning.Cloner;

public class ReceiptCalculator implements Serializable {

	private static final long					serialVersionUID	= 8062681791631293126L;
	private static Logger						logger				= Logger.getLogger(ReceiptCalculator.class);
	private FinReceiptData						receiptData;
	private BigDecimal							balanceRepayAmount	= BigDecimal.ZERO;
	private boolean								setEarlyPayAmt		= false;
	private boolean								setPastPenalties	= false;
	private Map<Date, FinanceScheduleDetail>	scheduleMap			= null;

	Date										curBussniessDate	= DateUtility.getAppDate();
	private RuleExecutionUtil					ruleExecutionUtil;
	private OverDueRecoveryPostingsUtil			recoveryPostingsUtil;
	private FinanceMainDAO						financeMainDAO;
	private OverdueChargeRecoveryDAO			recoveryDAO;
	private FinODDetailsDAO						finODDetailsDAO;

	// Default Constructor
	public ReceiptCalculator() {
		super();
	}

	/*
	 * ___________________________________________________________________________________________
	 * 
	 * Main Methods 
	 * ___________________________________________________________________________________________
	 */

	public FinReceiptData initiateReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, 
			boolean isReCal, String method, Date valueDate, String processMethod) {
		return procInitiateReceipt(receiptData, scheduleData, isReCal, method, valueDate, processMethod);
	}

	/** To Calculate the Amounts for given schedule */
	public FinReceiptData procInitiateReceipt(FinReceiptData receiptData, FinScheduleData scheduleData,
			boolean isReCal, String method, Date valueDate, String processMethod) {
		logger.debug("Entering");

		//Reset Current Business Application Date
		if (valueDate != null) {
			curBussniessDate = valueDate;
		}
		
		// Initialize Repay
		if ("I".equals(receiptData.getBuildProcess())) {
			receiptData = initializeReceipt(receiptData, scheduleData);
		}

		// Recalculate Repay
		if ("R".equals(receiptData.getBuildProcess())) {
			receiptData = recalReceipt(receiptData, scheduleData, isReCal, method, processMethod);
		}
		logger.debug("Leaving");
		return receiptData;
	}

	// INITIALIZE REPAY PROCESS
	private FinReceiptData initializeReceipt(FinReceiptData receiptData, FinScheduleData scheduleData) {
		logger.debug("Entering");
		
		FinanceMain financeMain = scheduleData.getFinanceMain();
		receiptData.setFinReference(financeMain.getFinReference());
		receiptData.setRepayMain(null);

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
		repayMain.setAccrued(receiptData.getAccruedTillLBD());
		repayMain.setRepayAccountId(financeMain.getRepayAccountId());
		repayMain.setFinAccount(financeMain.getFinAccount());
		repayMain.setFinCustPftAccount(financeMain.getFinCustPftAccount());
		repayMain.setPendindODCharges(receiptData.getPendingODC());
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
		receiptData.setRepayMain(repayMain);

		receiptData = calRepayMain(receiptData, scheduleData);
		
		logger.debug("Leaving");
		return receiptData;
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
	private FinReceiptData recalReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, 
			boolean isReCal, String method, String processMethod) {
		logger.debug("Entering");
		
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetails = scheduleData.getFinanceScheduleDetails();

		//receiptData.setRepayScheduleDetails(new ArrayList<RepayScheduleDetail>());
		balanceRepayAmount = receiptData.getRepayMain().getRepayAmountNow();

		// If no balance for repayment then return with out calculation
		if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
			return receiptData;
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
		receiptData.getRepayMain().setEarlyPayNextSchDate(null);
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
			if (!chkPastPenalty && schdDate.compareTo(receiptData.getRepayMain().getDateLastFullyPaid()) <= 0
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

				receiptData = addRepayRecord(financeMain, receiptData, curSchd, prvSchd, i, false, isReCal, method, false,
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
						receiptData = addRepayRecord(financeMain, receiptData, curSchd, prvSchd, i, false, isReCal, method,
								nxtSchd == null, recMap.get(schdDate), processMethod);
					}
				} else {
					if (nxtSchd == null
							|| (nxtSchd.getProfitSchd().subtract(nxtSchd.getSchdPftPaid()).compareTo(BigDecimal.ZERO) > 0 || nxtSchd
									.getPrincipalSchd().subtract(nxtSchd.getSchdPriPaid()).compareTo(BigDecimal.ZERO) > 0)) {
						receiptData = addRepayRecord(financeMain, receiptData, curSchd, prvSchd, i, false, isReCal, method,
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

			/*if (receiptData.getRepayScheduleDetails() != null && !receiptData.getRepayScheduleDetails().isEmpty()) {
				for (int i = 0; i < receiptData.getRepayScheduleDetails().size(); i++) {

					RepayScheduleDetail rpySchd = receiptData.getRepayScheduleDetails().get(i);
					if (rpySchd.getPenaltyAmt().compareTo(BigDecimal.ZERO) > 0) {

						applyOverdueCharges(rpySchd, processMethod);

						// No more repayment amount left for next schedules
						if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
							break;
						}

					}
				}

				//Reset Balances for Excess Amount
				receiptData.getRepayMain().setRepayAmountNow(balanceRepayAmount);
				receiptData.getRepayMain().setRepayAmountExcess(balanceRepayAmount);
			}*/
		}

		// Process for setting Balance Amount after Payment Apportionment
		receiptData = doReversalPayApportionmentForBal(receiptData);

		logger.debug("Leaving");
		return receiptData;
	}
	
	/**
	 * Method for Processing Schedule Data and Receipts to Prepare Allocation Details
	 * @param receiptData
	 * @param aFinScheduleData
	 */
	public Map<String, BigDecimal> recalAutoAllocation(FinScheduleData scheduleData, BigDecimal totalReceiptAmt) {
		logger.debug("Entering");
		
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetails = scheduleData.getFinanceScheduleDetails();

		// If no balance for repayment then return with out calculation
		if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}
		
		// Fetch total overdue details
		Map<Date, FinODDetails> overdueMap = new HashMap<Date, FinODDetails>();
		List<FinODDetails> overdueList = getFinODDetailsDAO().getFinODDetailsByFinReference(financeMain.getFinReference(),"");
		if (overdueList != null && !overdueList.isEmpty()) {
			for (int i = 0; i < overdueList.size(); i++) {
				if (overdueMap.containsKey(overdueList.get(i).getFinODSchdDate())) {
					overdueMap.remove(overdueList.get(i).getFinODSchdDate());
				}
				overdueMap.put(overdueList.get(i).getFinODSchdDate(), overdueList.get(i));
			}
		}
		
		String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy();
		boolean seperatePenalties = false;
		if(repayHierarchy.contains("CS")){
			seperatePenalties = true;
		}
		char[] rpyOrder = repayHierarchy.replace("CS", "").toCharArray();
		Map<String, BigDecimal> allocatePaidMap = new HashMap<>();
		int lastRenderSeq = 0;
		
		// Load Pending Schedules until balance available for payment
		for (int i = 1; i < scheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = scheduleDetails.get(i);
			Date schdDate = curSchd.getSchDate();

			// Skip if repayment date after Current Business date
			if (schdDate.compareTo(curBussniessDate) >= 0) {
				break;
			}

			// Skip if not a repayment
			if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			for (int j = 0; j < rpyOrder.length; j++) {
				
				char repayTo = rpyOrder[j];
				if(repayTo == RepayConstants.REPAY_PRINCIPAL){
					BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
					if(totalReceiptAmt.compareTo(balPri) > 0){
						totalReceiptAmt = totalReceiptAmt.subtract(balPri);
					}else{
						totalReceiptAmt = BigDecimal.ZERO;
					}
					
					BigDecimal totPriPayNow = BigDecimal.ZERO;
					if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PRI)){
						totPriPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_PRI);
						allocatePaidMap.remove(RepayConstants.ALLOCATION_PRI);
					}
					allocatePaidMap.put(RepayConstants.ALLOCATION_PRI, totPriPayNow.add(balPri));
				}else if(repayTo == RepayConstants.REPAY_PROFIT){
					
					String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
					char[] pftPayOrder = profit.toCharArray();
					for (char pftPayTo : pftPayOrder) {
						if(pftPayTo == RepayConstants.REPAY_PROFIT){
							
							BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
							if(totalReceiptAmt.compareTo(balPft) > 0){
								totalReceiptAmt = totalReceiptAmt.subtract(balPft);
							}else{
								totalReceiptAmt = BigDecimal.ZERO;
							}
							
							BigDecimal totPftPayNow = BigDecimal.ZERO;
							if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PFT)){
								totPftPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_PFT);
								allocatePaidMap.remove(RepayConstants.ALLOCATION_PFT);
							}
							allocatePaidMap.put(RepayConstants.ALLOCATION_PFT, totPftPayNow.add(balPft));
							
						}else if(pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT){
							
							FinODDetails overdue = overdueMap.get(schdDate);
							BigDecimal balLatePft = overdue.getTotPftBal();
							if(totalReceiptAmt.compareTo(balLatePft) > 0){
								totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
							}else{
								totalReceiptAmt = BigDecimal.ZERO;
							}
							
							BigDecimal totLatePftPayNow = BigDecimal.ZERO;
							if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_LPFT)){
								totLatePftPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_LPFT);
								allocatePaidMap.remove(RepayConstants.ALLOCATION_LPFT);
							}
							allocatePaidMap.put(RepayConstants.ALLOCATION_LPFT, totLatePftPayNow.add(balLatePft));
						}
					}
					
				}else if(repayTo == RepayConstants.REPAY_PENALTY){
					if(!seperatePenalties){
						FinODDetails overdue = overdueMap.get(schdDate);
						BigDecimal balPenalty = overdue.getTotPenaltyBal();
						if(totalReceiptAmt.compareTo(balPenalty) > 0){
							totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
						}else{
							totalReceiptAmt = BigDecimal.ZERO;
						}
						
						BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
						if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_ODC)){
							totPenaltyPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_ODC);
							allocatePaidMap.remove(RepayConstants.ALLOCATION_ODC);
						}
						allocatePaidMap.put(RepayConstants.ALLOCATION_ODC, totPenaltyPayNow.add(balPenalty));
					}
					
				}else if(repayTo == RepayConstants.REPAY_OTHERS){
					// Fee Detail Collection
					for (int k = 0; k < scheduleData.getFinFeeDetailList().size(); k++) {
						FinFeeDetail feeSchd = scheduleData.getFinFeeDetailList().get(k);
						List<FinFeeScheduleDetail> feeSchdList = feeSchd.getFinFeeScheduleDetailList();
						if(feeSchdList == null || feeSchdList.isEmpty()){
							continue;
						}
						
						// If Schedule Fee terms are less than actual calculated Sequence
						if(feeSchdList.size() < lastRenderSeq){
							continue;
						}
						
						// Calculate and set Fee Amount based on Fee ID
						for (int l = lastRenderSeq; l < feeSchdList.size(); l++) {
							if(feeSchdList.get(l).getSchDate().compareTo(schdDate) == 0){
								
								BigDecimal balFee = feeSchdList.get(l).getSchAmount().subtract(feeSchdList.get(l).getPaidAmount());
								if(totalReceiptAmt.compareTo(balFee) > 0){
									totalReceiptAmt = totalReceiptAmt.subtract(balFee);
								}else{
									totalReceiptAmt = BigDecimal.ZERO;
								}
								
								BigDecimal totFeePayNow = BigDecimal.ZERO;
								if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_FEE+"~"+feeSchd.getFeeID())){
									totFeePayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_FEE+"~"+feeSchd.getFeeID());
									allocatePaidMap.remove(RepayConstants.ALLOCATION_FEE+"~"+feeSchd.getFeeID());
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_FEE+"~"+feeSchd.getFeeID(), totFeePayNow.add(balFee));
								if(lastRenderSeq == 0){
									lastRenderSeq = l;
								}
								break;
							}
						}
					}
					
					// Insurance Details Collection
					// Fee Detail Collection
					for (int k = 0; k < scheduleData.getFinInsuranceList().size(); k++) {
						FinInsurances insSchd = scheduleData.getFinInsuranceList().get(k);
						List<FinSchFrqInsurance> insSchdList = insSchd.getFinSchFrqInsurances();
						if(insSchdList == null || insSchdList.isEmpty()){
							continue;
						}
						
						// Calculate and set Fee Amount based on Fee ID
						// TODO : Check/Confirm id Day of Frequency as same as Repay Frequency
						for (int l = 0; l < insSchdList.size(); l++) {
							if(insSchdList.get(l).getInsSchDate().compareTo(schdDate) == 0){
								
								BigDecimal balIns = insSchdList.get(l).getAmount();//TODO : Subtract Paid Amounts
								if(totalReceiptAmt.compareTo(balIns) > 0){
									totalReceiptAmt = totalReceiptAmt.subtract(balIns);
								}else{
									totalReceiptAmt = BigDecimal.ZERO;
								}
								
								BigDecimal totInsPayNow = BigDecimal.ZERO;
								if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_INS+"~"+insSchd.getInsId())){
									totInsPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_INS+"~"+insSchd.getInsId());
									allocatePaidMap.remove(RepayConstants.ALLOCATION_INS+"~"+insSchd.getInsId());
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_INS+"~"+insSchd.getInsId(), totInsPayNow.add(balIns));
								break;
							}
						}
					}
					
					// Manual Advises TODO
				}
				
				// No more Receipt amount left for next schedules
				if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// No more Receipt amount left for next schedules
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
		}

		//Set Penalty Payments for Prepared Past due Schedule Details
		if (seperatePenalties && totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
			
			if(!overdueMap.isEmpty()){
				List<Date> odDateList = new ArrayList<Date>(overdueMap.keySet());
				Collections.sort(odDateList);
				for (int i = 0; i < odDateList.size(); i++) {

					FinODDetails overdue = overdueMap.get(odDateList.get(i));
					BigDecimal balPenalty = overdue.getTotPenaltyBal();
					if(totalReceiptAmt.compareTo(balPenalty) > 0){
						totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
					}else{
						totalReceiptAmt = BigDecimal.ZERO;
					}

					BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
					if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_ODC)){
						totPenaltyPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_ODC);
						allocatePaidMap.remove(RepayConstants.ALLOCATION_ODC);
					}
					allocatePaidMap.put(RepayConstants.ALLOCATION_ODC, totPenaltyPayNow.add(balPenalty));
				}
			}
			
		}

		logger.debug("Leaving");
		return allocatePaidMap;
	}

	private FinReceiptData calRepayMain(FinReceiptData receiptData, FinScheduleData finScheduleData) {
		logger.debug("Entering");

		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal cpzTillNow = BigDecimal.ZERO;
		Boolean isNextDueSet = false;
		boolean isSkipLastDateSet = false;

		RepayMain repayMain = receiptData.getRepayMain();
		List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();

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
			if (DateUtility.compare(schdDate, curBussniessDate) < 0) {
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
		
		// Allocation Map and Description Details Preparation
		if(receiptData.getAllocationMap() == null){
			receiptData.setAllocationMap(new HashMap<String, BigDecimal>());
		}
		
		// Applicable only when Fees, Insurances & Manual Advises to maintain descriptions in screen level
		if(receiptData.getAllocationDescMap() == null){
			receiptData.setAllocationDescMap(new HashMap<String, String>());
		}

		// Overdue Principal Amount
		if(repayMain.getOverduePrincipal().compareTo(BigDecimal.ZERO) > 0){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PRI, repayMain.getOverduePrincipal());
		}
		
		if(repayMain.getOverdueProfit().compareTo(BigDecimal.ZERO) > 0){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PFT, repayMain.getOverdueProfit());
		}
		
		// Fetch Late Pay Profit Details
		BigDecimal latePayPftBal = getFinODDetailsDAO().getTotalPenaltyBal(repayMain.getFinReference());
		if(latePayPftBal.compareTo(BigDecimal.ZERO) > 0){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_LPFT, latePayPftBal);
		}
		
		// Fetch Sum of Overdue Charges
		BigDecimal penaltyBal = getFinODDetailsDAO().getTotalPenaltyBal(repayMain.getFinReference());
		if(penaltyBal.compareTo(BigDecimal.ZERO) > 0){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_ODC, penaltyBal);
		}
		
		// Fee Details
		if(finScheduleData.getFinFeeDetailList() != null && 
				!finScheduleData.getFinFeeDetailList().isEmpty()){
			
			for (int i = 0; i < finScheduleData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = finScheduleData.getFinFeeDetailList().get(i);

				if(StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT) ||
						StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS) ||
						StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)){
					
					// Calculate Overdue Fee Schedule Amount
					List<FinFeeScheduleDetail> feeSchdList = feeDetail.getFinFeeScheduleDetailList();
					BigDecimal pastFeeAmount = BigDecimal.ZERO;
					for (int j = 0; j < feeSchdList.size(); j++) {
						
						FinFeeScheduleDetail feeSchd = feeSchdList.get(j);
						if (DateUtility.compare(feeSchd.getSchDate(), curBussniessDate) < 0) {
							pastFeeAmount = pastFeeAmount.add(feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount()));
						}else{
							break;
						}
					}
					
					// Adding Fee Details to Map
					if(pastFeeAmount.compareTo(BigDecimal.ZERO) > 0){
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_FEE+"~"+feeDetail.getFeeID(), pastFeeAmount);
						receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_FEE+"~"+feeDetail.getFeeID(), feeDetail.getFeeTypeDesc());
					}
				}
			}
		}
		
		// Insurance Details
		if(finScheduleData.getFinInsuranceList() != null && 
				!finScheduleData.getFinInsuranceList().isEmpty()){
			
			for (int i = 0; i < finScheduleData.getFinInsuranceList().size(); i++) {
				FinInsurances finInsurance = finScheduleData.getFinInsuranceList().get(i);

				if(StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)){
					
					// Calculate Overdue Fee Schedule Amount
					List<FinSchFrqInsurance> insSchdList = finInsurance.getFinSchFrqInsurances();
					BigDecimal pastInsAmount = BigDecimal.ZERO;
					for (int j = 0; j < insSchdList.size(); j++) {
						
						FinSchFrqInsurance insSchd = insSchdList.get(j);
						if (DateUtility.compare(insSchd.getInsSchDate(), curBussniessDate) < 0) {
							pastInsAmount = pastInsAmount.add(insSchd.getAmount());// TODO : Paid To be Added in Insurance Schedules
						}else{
							break;
						}
					}
					
					// Adding Fee Details to Map
					if(pastInsAmount.compareTo(BigDecimal.ZERO) > 0){
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_INS+"~"+finInsurance.getInsId(), pastInsAmount);
						receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_INS+"~"+finInsurance.getInsId(), 
								finInsurance.getInsuranceTypeDesc()+"-"+finInsurance.getInsReference());
					}
				}
			}
		}
		
		// Manual Advises :TODO - Waiting for development completion
		
		logger.debug("Leaving");
		return receiptData;
	}

	private FinReceiptData addRepayRecord(FinanceMain financeMain, FinReceiptData receiptData, FinanceScheduleDetail curSchd,
			FinanceScheduleDetail prvSchd, int schdIndex, boolean isDefSchd, boolean isReCal, String method,
			boolean isLastRcd, OverdueChargeRecovery recovery, String processMethod) {
		logger.debug("Entering");

		BigDecimal toPay = BigDecimal.ZERO;

		RepayScheduleDetail rsd = new RepayScheduleDetail();
		rsd.setFinReference(receiptData.getFinReference());
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
				finRepayQueue.setFinReference(receiptData.getRepayMain().getFinReference());
				finRepayQueue.setBranch(receiptData.getRepayMain().getFinBranch());
				finRepayQueue.setFinType(receiptData.getRepayMain().getFinType());
				finRepayQueue.setCustomerID(receiptData.getRepayMain().getCustID());
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
						return receiptData;
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

			if (setEarlyPayAmt && receiptData.getRepayMain().getEarlyPayNextSchDate() == null) {
				receiptData.getRepayMain().setEarlyPayNextSchDate(curSchd.getSchDate());
			}

			//Set Penalty Payments for Prepared Past due Schedule Details
			if (!setPastPenalties
					&& (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FIPCS) || ImplementationConstants.REPAY_HIERARCHY_METHOD
							.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {

				/*if (receiptData.getRepayScheduleDetails() != null && !receiptData.getRepayScheduleDetails().isEmpty()) {
					for (int i = 0; i < receiptData.getRepayScheduleDetails().size(); i++) {

						RepayScheduleDetail rpySchd = receiptData.getRepayScheduleDetails().get(i);
						if (rpySchd.getPenaltyAmt().compareTo(BigDecimal.ZERO) > 0) {

							applyOverdueCharges(rpySchd, processMethod);

							// No more repayment amount left for next schedules
							if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
								break;
							}

						}
					}
				}*/
			}

			//To Stop Penalty Setting From Remaining Balance
			setPastPenalties = true;
			// No more repayment amount left for next schedules
			if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
				receiptData.getRepayMain().setRepayAmountNow(balanceRepayAmount);
				receiptData.getRepayMain().setRepayAmountExcess(balanceRepayAmount);

				logger.debug("Leaving");
				return receiptData;
			}

			if (rsd.getDaysEarly() >= 0) {

				final BigDecimal earlypayAmt = balanceRepayAmount;
				if (rsd.getDaysEarly() == 0) {
					if (earlypayAmt.compareTo(curSchd.getRepayAmount()) > 0) {
						receiptData.getRepayMain().setEarlyPay(true);
						setEarlyPayAmt = true;

						if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
							receiptData.getRepayMain().setEarlyPayAmount(earlypayAmt.add(curSchd.getRepayAmount()));
						} else {
							receiptData.getRepayMain().setEarlyPayAmount(earlypayAmt);
						}
						receiptData.getRepayMain().setEarlyPayOnSchDate(curSchd.getSchDate());
						receiptData.getRepayMain().setEarlyRepayNewSchd(null);
					}
				} else {
					receiptData.getRepayMain().setEarlyPay(true);
					if (!setEarlyPayAmt) {

						setEarlyPayAmt = true;
						receiptData.getRepayMain().setEarlyPayAmount(earlypayAmt);

						if ("NONSCH".equals(SysParamUtil.getValueAsString("EARLYPAY_TERM_INS"))) {
							receiptData.getRepayMain().setEarlyPayOnSchDate(curBussniessDate);
							receiptData.getRepayMain().setEarlyPayNextSchDate(curSchd.getSchDate());
						} else {
							receiptData.getRepayMain().setEarlyPayOnSchDate(curSchd.getSchDate());
						}

						if (!scheduleMap.containsKey(curBussniessDate)) {
							FinanceScheduleDetail newSchdlEP = new FinanceScheduleDetail(receiptData.getRepayMain()
									.getFinReference());
							newSchdlEP.setDefSchdDate(receiptData.getRepayMain().getEarlyPayOnSchDate());
							newSchdlEP.setSchDate(receiptData.getRepayMain().getEarlyPayOnSchDate());
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
							receiptData.getRepayMain().setEarlyRepayNewSchd(newSchdlEP);
						} else {
							FinanceScheduleDetail detail = scheduleMap.get(curBussniessDate);
							if (isReCal) {
								receiptData.getRepayMain().setEarlyPayAmount(detail.getRepayAmount());
							} else {
								receiptData.getRepayMain().setEarlyPayAmount(earlypayAmt.add(detail.getRepayAmount()));
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
		if (StringUtils.equals(receiptData.getRepayMain().getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT)
				|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

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
			if (StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
					FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
							FinanceConstants.PAY_APPORTIONMENT_SPFT)
					|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setProfitSchdPayNow(toPay);
				} else {
					rsd.setProfitSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
			} else {
				rsd.setProfitSchdPayNow(BigDecimal.ZERO);
			}
			receiptData.getRepayMain().setProfitPayNow(
					receiptData.getRepayMain().getProfitPayNow().add(rsd.getProfitSchdPayNow()));

			//Principal Amount Checking
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
			toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

			// Payment Process only for Installment or Principal only from Schedules or No default Payment Exists
			if (StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
					FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
							FinanceConstants.PAY_APPORTIONMENT_SPRI)
					|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setPrincipalSchdPayNow(toPay);
				} else {
					rsd.setPrincipalSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
			} else {
				rsd.setPrincipalSchdPayNow(BigDecimal.ZERO);
			}
			receiptData.getRepayMain().setPrincipalPayNow(
					receiptData.getRepayMain().getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));

		} else if ((ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FCPI))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPIC))
				|| (ImplementationConstants.REPAY_HIERARCHY_METHOD.equals(RepayConstants.REPAY_HIERARCHY_FPICS))) {

			//Principal Amount Checking
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
			toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

			// Payment Process only for Installment or Principal only from Schedules or No default Payment Exists
			if (StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
					FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
							FinanceConstants.PAY_APPORTIONMENT_SPRI)
					|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setPrincipalSchdPayNow(toPay);
				} else {
					rsd.setPrincipalSchdPayNow(balanceRepayAmount);
				}

				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
			} else {
				rsd.setPrincipalSchdPayNow(BigDecimal.ZERO);
			}
			receiptData.getRepayMain().setPrincipalPayNow(
					receiptData.getRepayMain().getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));

			//Profit Amount Checking
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
			toPay = rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid());
			// Payment Process only for Installment or Profit only from Schedules or No default Payment Exists
			if (StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
					FinanceConstants.PAY_APPORTIONMENT_STOT)
					|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
							FinanceConstants.PAY_APPORTIONMENT_SPFT)
					|| StringUtils.equals(receiptData.getRepayMain().getPayApportionment(), PennantConstants.List_Select)) {

				if (balanceRepayAmount.compareTo(toPay) >= 0) {
					rsd.setProfitSchdPayNow(toPay);
				} else {
					rsd.setProfitSchdPayNow(balanceRepayAmount);
				}
				balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
			} else {
				rsd.setProfitSchdPayNow(BigDecimal.ZERO);
			}
			receiptData.getRepayMain().setProfitPayNow(
					receiptData.getRepayMain().getProfitPayNow().add(rsd.getProfitSchdPayNow()));
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

		receiptData.getRepayMain().setRepayAmountNow(balanceRepayAmount);
		receiptData.getRepayMain().setRepayAmountExcess(balanceRepayAmount);
		//receiptData.getRepayScheduleDetails().add(rsd);

		logger.debug("Leaving");
		return receiptData;
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
	 * Method for adjusting Balance amount after Payment Apportionment, if Exists any.
	 */
	private FinReceiptData doReversalPayApportionmentForBal(FinReceiptData receiptData) {

		// Set Remaining Balance Amount for Payment Apportionment Method
		if (!(StringUtils.equals(receiptData.getRepayMain().getPayApportionment(), PennantConstants.List_Select) || StringUtils
				.equals(receiptData.getRepayMain().getPayApportionment(), FinanceConstants.PAY_APPORTIONMENT_STOT))
				&& balanceRepayAmount.compareTo(BigDecimal.ZERO) > 0) {/*

			List<RepayScheduleDetail> rpySchdDetails = receiptData.getRepayScheduleDetails();

			for (RepayScheduleDetail rsd : rpySchdDetails) {

				// Adjust Remaining Balance to Fee Details on particular Schedule
				schdFeeCollectionProcess(rsd);

				// Adjust Remaiing balance to Either Principal or Profit based on (Reversal)Payment Apportionment Method
				BigDecimal toPay = BigDecimal.ZERO;

				//Profit Amount Checking
				if (StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
						FinanceConstants.PAY_APPORTIONMENT_SPRI)) {

					rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
					toPay = rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid());

					if (balanceRepayAmount.compareTo(toPay) >= 0) {
						rsd.setProfitSchdPayNow(toPay);
					} else {
						rsd.setProfitSchdPayNow(balanceRepayAmount);
					}

					balanceRepayAmount = balanceRepayAmount.subtract(rsd.getProfitSchdPayNow());
					receiptData.getRepayMain().setProfitPayNow(
							receiptData.getRepayMain().getProfitPayNow().add(rsd.getProfitSchdPayNow()));
				}

				//Principal Amount Checking
				if (StringUtils.equals(receiptData.getRepayMain().getPayApportionment(),
						FinanceConstants.PAY_APPORTIONMENT_SPFT)) {
					rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
					toPay = rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid());

					if (balanceRepayAmount.compareTo(toPay) >= 0) {
						rsd.setPrincipalSchdPayNow(toPay);
					} else {
						rsd.setPrincipalSchdPayNow(balanceRepayAmount);
					}

					balanceRepayAmount = balanceRepayAmount.subtract(rsd.getPrincipalSchdPayNow());
					receiptData.getRepayMain().setPrincipalPayNow(
							receiptData.getRepayMain().getPrincipalPayNow().add(rsd.getPrincipalSchdPayNow()));
				}

				rsd.setRepayNet(rsd.getProfitSchdPayNow().add(rsd.getPrincipalSchdPayNow())
						.add(rsd.getSchdInsPayNow()).add(rsd.getSchdSuplRentPayNow())
						.add(rsd.getSchdIncrCostPayNow()).add(rsd.getSchdFeePayNow()));
				rsd.setRepayBalance(rsd.getPrincipalSchdBal().add(rsd.getProfitSchdBal()).add(rsd.getSchdInsBal())
						.add(rsd.getSchdSuplRentBal()).add(rsd.getSchdIncrCostBal()).add(rsd.getSchdFeeBal()).subtract(rsd.getRepayNet()));
				receiptData.getRepayMain().setRepayAmountNow(balanceRepayAmount);
				receiptData.getRepayMain().setRepayAmountExcess(balanceRepayAmount);

				if (balanceRepayAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		*/}
		logger.debug("Leaving");
		return receiptData;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinReceiptData getFinReceiptData() {
		return this.receiptData;
	}

	public void setFinReceiptData(FinReceiptData receiptData) {
		this.receiptData = receiptData;
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

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

}
