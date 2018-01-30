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
 * FileName    		:  ReceiptCalculator.java												*                           
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
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.RepayConstants;
import com.rits.cloning.Cloner;

public class ReceiptCalculator implements Serializable {

	private static final long					serialVersionUID	= 8062681791631293126L;
	private static Logger						logger				= Logger.getLogger(ReceiptCalculator.class);

	private FinODDetailsDAO						finODDetailsDAO;
	private ManualAdviseDAO						manualAdviseDAO;
	private ReceiptService						receiptService;

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

	public FinReceiptData initiateReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, Date valueDate, String receiptPurpose, boolean isPresentment) {
		return procInitiateReceipt(receiptData, scheduleData, valueDate, receiptPurpose, isPresentment);
	}

	/** To Calculate the Amounts for given schedule */
	private FinReceiptData procInitiateReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, Date valueDate, String receiptPurpose,  boolean isPresentment) {
		logger.debug("Entering");

		// Initialize Repay
		if ("I".equals(receiptData.getBuildProcess())) {
			receiptData = initializeReceipt(receiptData, scheduleData, valueDate, receiptPurpose);
		}

		// Recalculate Repay
		if ("R".equals(receiptData.getBuildProcess())) {
			receiptData = recalReceipt(receiptData, scheduleData, valueDate, receiptPurpose, isPresentment);
		}
		
		logger.debug("Leaving");
		return receiptData;
	}

	/**
	 * Method for Initialize the data from Finance Details to Receipt Data to render Summary
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 */
	private FinReceiptData initializeReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, Date valueDate, String receiptPurpose) {
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

		receiptData = calSummaryDetail(receiptData, scheduleData, valueDate, receiptPurpose);
		
		logger.debug("Leaving");
		return receiptData;
	}

	/**
	 * Method for calculating Schedule Total and Unpaid amounts based on Schedule Details
	 */
	private FinReceiptData calSummaryDetail(FinReceiptData receiptData, FinScheduleData finScheduleData, Date valueDate,String receiptPurpose) {
		logger.debug("Entering");

		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal tdsAmount = BigDecimal.ZERO;
		BigDecimal cpzTillNow = BigDecimal.ZERO;
		Boolean isNextDueSet = false;
		boolean isSkipLastDateSet = false;
		
		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if(finScheduleData.getFinanceMain().isTDSApplicable()){

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/

			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
			}
		}

		RepayMain repayMain = receiptData.getRepayMain();
		List<FinanceScheduleDetail> scheduleDetails = finScheduleData.getFinanceScheduleDetails();

		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> tempScheduleDetails = cloner.deepClone(scheduleDetails);
		tempScheduleDetails = sortSchdDetails(tempScheduleDetails);

		List<Date> presentmentDates = new ArrayList<>();
		boolean setEarlyPayRecord = false;
		BigDecimal pftAccruedTillNow = BigDecimal.ZERO;
		BigDecimal tdsAccruedTillNow = BigDecimal.ZERO;
		BigDecimal priBalance = BigDecimal.ZERO;
		boolean partAccrualReq = true;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail prvSchd = null;
		for (int i = 0; i < tempScheduleDetails.size(); i++) {
			curSchd = tempScheduleDetails.get(i);
			if(i != 0){
				prvSchd = tempScheduleDetails.get(i - 1);
			}
			Date schdDate = curSchd.getSchDate();

			// Finance amount and current finance amount
			repayMain.setFinAmount(repayMain.getFinAmount().add(curSchd.getDisbAmount())
					.add(curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : curSchd.getFeeChargeAmt()));

			repayMain.setCurFinAmount(repayMain.getFinAmount().subtract(curSchd.getSchdPriPaid())
					.subtract(curSchd.getSchdPftPaid()).subtract(curSchd.getDownPaymentAmount()));

			if (schdDate.compareTo(valueDate) < 0) {
				repayMain.setCurFinAmount(repayMain.getCurFinAmount().subtract(curSchd.getCpzAmount()));
			}else{
				
				if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY) || 
						StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
					if (DateUtility.getDaysBetween(valueDate, schdDate) == 0) {
						repayMain.setEarlyPayOnSchDate(curSchd.getSchDate());
						repayMain.setEarlyRepayNewSchd(null);
						setEarlyPayRecord = true;
					} else {
						if (!setEarlyPayRecord) {

							setEarlyPayRecord = true;
							if ("NONSCH".equals(SysParamUtil.getValueAsString("EARLYPAY_TERM_INS"))) {
								repayMain.setEarlyPayOnSchDate(valueDate);
								repayMain.setEarlyPayNextSchDate(curSchd.getSchDate());
							} else {
								repayMain.setEarlyPayOnSchDate(curSchd.getSchDate());
							}

							FinanceScheduleDetail newSchdlEP = new FinanceScheduleDetail(repayMain.getFinReference());
							newSchdlEP.setDefSchdDate(repayMain.getEarlyPayOnSchDate());
							newSchdlEP.setSchDate(repayMain.getEarlyPayOnSchDate());
							newSchdlEP.setSchSeq(1);
							newSchdlEP.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
							newSchdlEP.setRepayOnSchDate(true);
							newSchdlEP.setPftOnSchDate(true);
							if(prvSchd != null){
								newSchdlEP.setSchdMethod(prvSchd.getSchdMethod());
								newSchdlEP.setBaseRate(prvSchd.getBaseRate());
								newSchdlEP.setSplRate(prvSchd.getSplRate());
								newSchdlEP.setMrgRate(prvSchd.getMrgRate());
								newSchdlEP.setActRate(prvSchd.getActRate());
								newSchdlEP.setCalculatedRate(prvSchd.getCalculatedRate());
								newSchdlEP.setPftDaysBasis(prvSchd.getPftDaysBasis());
								newSchdlEP.setEarlyPaidBal(prvSchd.getEarlyPaidBal());
							}
							repayMain.setEarlyRepayNewSchd(newSchdlEP);
						}
					}
				}
			}

			// Profit scheduled and Paid
			repayMain.setProfit(repayMain.getProfit().add(curSchd.getProfitSchd()));
			
			// Accrued Profit Calculation
			if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				if (DateUtility.compare(schdDate, valueDate) < 0) {
					pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitSchd());
					priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
					
					if(finScheduleData.getFinanceMain().isTDSApplicable()){
						BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
						BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
						
						/*actualPft = CalculationUtil.roundAmount(actualPft, finScheduleData.getFinanceMain().getCalRoundingMode(), 
								finScheduleData.getFinanceMain().getRoundingTarget());*/
						tdsAccruedTillNow = tdsAccruedTillNow.add(pft.subtract(actualPft));
					}
					
				}else if (DateUtility.compare(valueDate, schdDate) == 0) {
					
					BigDecimal remPft = curSchd.getProfitCalc().subtract(curSchd.getSchdPftPaid());
					pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitCalc());
					if(prvSchd != null){
						remPft = remPft.add(prvSchd.getProfitBalance());
						pftAccruedTillNow = pftAccruedTillNow.add(prvSchd.getProfitBalance());
					}
					priBalance = priBalance.add(curSchd.getPrincipalSchd().add(curSchd.getClosingBalance())).subtract(curSchd.getCpzAmount()).subtract(curSchd.getSchdPriPaid());
					
					if(StringUtils.equals(curSchd.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)){
						repayMain.setEarlyPayAmount(curSchd.getClosingBalance().subtract(curSchd.getCpzAmount()).add(curSchd.getProfitCalc()));
						if(prvSchd != null){
							repayMain.setEarlyPayAmount(repayMain.getEarlyPayAmount().add(prvSchd.getProfitBalance()));
						}
					}else{
						repayMain.setEarlyPayAmount(curSchd.getClosingBalance().subtract(curSchd.getCpzAmount()));
					}
					
					if(finScheduleData.getFinanceMain().isTDSApplicable()){
						BigDecimal actualPft = remPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
						/*actualPft = CalculationUtil.roundAmount(actualPft, finScheduleData.getFinanceMain().getCalRoundingMode(), 
								finScheduleData.getFinanceMain().getRoundingTarget());*/
						tdsAccruedTillNow = tdsAccruedTillNow.add(remPft.subtract(actualPft));
						
					}
					partAccrualReq = false;
					
					// Future Disbursements into Early paid Balance
					repayMain.setEarlyPayAmount(repayMain.getEarlyPayAmount().add(curSchd.getDisbAmount()));
				} else {
					if(partAccrualReq && prvSchd != null){
						partAccrualReq = false;
						BigDecimal accruedPft = CalculationUtil.calInterest(prvSchd.getSchDate(), valueDate, curSchd.getBalanceForPftCal(),
								prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());
						accruedPft = accruedPft.add(prvSchd.getProfitFraction());
						accruedPft = CalculationUtil.roundAmount(accruedPft, finScheduleData.getFinanceMain().getCalRoundingMode(), 
								finScheduleData.getFinanceMain().getRoundingTarget());
						pftAccruedTillNow = pftAccruedTillNow.add(accruedPft).add(prvSchd.getProfitBalance());
						
						priBalance = priBalance.add(prvSchd.getClosingBalance());
						repayMain.setEarlyPayAmount(prvSchd.getClosingBalance());
						
						if(finScheduleData.getFinanceMain().isTDSApplicable()){
							BigDecimal actualPft = (accruedPft.add(prvSchd.getProfitBalance())).divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
							/*actualPft = CalculationUtil.roundAmount(actualPft, finScheduleData.getFinanceMain().getCalRoundingMode(), 
									finScheduleData.getFinanceMain().getRoundingTarget());*/
							tdsAccruedTillNow = tdsAccruedTillNow.add(accruedPft.add(prvSchd.getProfitBalance()).subtract(actualPft));
						}
					}
					
					// Future Disbursements into Early paid Balance
					repayMain.setEarlyPayAmount(repayMain.getEarlyPayAmount().add(curSchd.getDisbAmount()));
				}
			}
			
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
			if (DateUtility.compare(schdDate, valueDate) <= 0) {
				cpzTillNow = cpzTillNow.add(curSchd.getCpzAmount());
				
				if(!StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY) || 
						curSchd.getPresentmentId() == 0){
					
					repayMain.setOverduePrincipal(repayMain.getOverduePrincipal().add(
							curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())));

					repayMain.setOverdueProfit(repayMain.getOverdueProfit().add(
							curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())));

					// TDS Calculation
					BigDecimal unpaidPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
					BigDecimal actualPft = unpaidPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
					/*actualPft = CalculationUtil.roundAmount(actualPft, finScheduleData.getFinanceMain().getCalRoundingMode(), 
							finScheduleData.getFinanceMain().getRoundingTarget());*/
					tdsAmount = tdsAmount.add(unpaidPft.subtract(actualPft));
				}
				
				if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY) && 
						curSchd.getPresentmentId() > 0){
					presentmentDates.add(schdDate);
				}

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

		// Principal Amount
		if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PRI, priBalance);
		}else{
			if(repayMain.getOverduePrincipal().compareTo(BigDecimal.ZERO) > 0){
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PRI, repayMain.getOverduePrincipal());
			}
		}
		
		// Profit Amount
		BigDecimal pftAmt = BigDecimal.ZERO;
		if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
			pftAmt = pftAccruedTillNow.subtract(pftPaid);
		}else{
			pftAmt = repayMain.getOverdueProfit();
		}
		
		// TDS calculation Process
		if(pftAmt.compareTo(BigDecimal.ZERO) > 0){
			if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_TDS, tdsAccruedTillNow);
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_NPFT, pftAmt.subtract(tdsAccruedTillNow));
			}else{
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_TDS, tdsAmount);
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_NPFT, pftAmt.subtract(tdsAmount));
			}
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PFT, pftAmt);
		}
		
		// Fetching Actual Late Payments based on Value date passing
		BigDecimal latePayPftBal = BigDecimal.ZERO;
		BigDecimal penaltyBal = BigDecimal.ZERO;
		if(DateUtility.compare(valueDate, DateUtility.getAppDate()) == 0){
			latePayPftBal = getFinODDetailsDAO().getTotalODPftBal(repayMain.getFinReference(), presentmentDates);
			penaltyBal = getFinODDetailsDAO().getTotalPenaltyBal(repayMain.getFinReference(), presentmentDates);
		}else{
			
			Date reqMaxODDate = DateUtility.addDays(valueDate, -1);
			
			// Calculate overdue Penalties
			List<FinODDetails> overdueList = getReceiptService().getValueDatePenalties(finScheduleData, receiptData.getTotReceiptAmount(), reqMaxODDate, null, true);

			// Calculating Actual Sum of Penalty Amount & Late Pay Interest
			if(overdueList != null && !overdueList.isEmpty()){
				for (int i = 0; i < overdueList.size(); i++) {
					FinODDetails finODDetail = overdueList.get(i);
					if (finODDetail.getFinODSchdDate().compareTo(reqMaxODDate) >= 0) {
						continue;
					}
					
					// Not allowed Presentment/Freezing Period Schedule dates
					if(presentmentDates.contains(finODDetail.getFinODSchdDate())){
						continue;
					}
					latePayPftBal = latePayPftBal.add(finODDetail.getLPIBal());
					penaltyBal = penaltyBal.add(finODDetail.getTotPenaltyBal());
				}
			}
		}
		
		// Fetch Late Pay Profit Details
		if(latePayPftBal.compareTo(BigDecimal.ZERO) > 0){
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_LPFT, latePayPftBal);
		}
		
		// Fetch Sum of Overdue Charges
		if(penaltyBal.compareTo(BigDecimal.ZERO) > 0){
			receiptData.setPendingODC(penaltyBal);
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_ODC, penaltyBal);
		}
		
		// Fee Details
		if(finScheduleData.getFinFeeDetailList() != null && 
				!finScheduleData.getFinFeeDetailList().isEmpty()){
			
			for (int i = 0; i < finScheduleData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = finScheduleData.getFinFeeDetailList().get(i);
				if(StringUtils.equals(feeDetail.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)){
					continue;
				}

				if(StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT) ||
						StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS) ||
						StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)){
					
					// Calculate Overdue Fee Schedule Amount
					List<FinFeeScheduleDetail> feeSchdList = feeDetail.getFinFeeScheduleDetailList();
					BigDecimal pastFeeAmount = BigDecimal.ZERO;
					for (int j = 0; j < feeSchdList.size(); j++) {
						
						FinFeeScheduleDetail feeSchd = feeSchdList.get(j);
						if (DateUtility.compare(feeSchd.getSchDate(), valueDate) <= 0 || 
								StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
							pastFeeAmount = pastFeeAmount.add(feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount()));
						}else{
							break;
						}
					}
					
					// Adding Fee Details to Map
					if(pastFeeAmount.compareTo(BigDecimal.ZERO) > 0){
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_FEE+"_"+feeDetail.getFeeID(), pastFeeAmount);
						receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_FEE+"_"+feeDetail.getFeeID(), 
								StringUtils.isEmpty(feeDetail.getFeeTypeDesc()) ? feeDetail.getVasReference() : feeDetail.getFeeTypeDesc());
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
						if (DateUtility.compare(insSchd.getInsSchDate(), valueDate) <= 0 ||
								StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
							pastInsAmount = pastInsAmount.add(insSchd.getAmount().subtract(insSchd.getInsurancePaid()));
						}else{
							break;
						}
					}
					
					// Adding Fee Details to Map
					if(pastInsAmount.compareTo(BigDecimal.ZERO) > 0){
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_INS+"_"+finInsurance.getInsId(), pastInsAmount);
						receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_INS+"_"+finInsurance.getInsId(), 
								finInsurance.getInsuranceTypeDesc()+"-"+finInsurance.getInsReference());
					}
				}
			}
		}
		
		// Manual Advises 
		List<ManualAdvise> adviseList = getManualAdviseDAO().getManualAdviseByRef(repayMain.getFinReference(), 
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");
		if(adviseList != null && !adviseList.isEmpty()){
			for (ManualAdvise advise : adviseList) {
				BigDecimal adviseBal = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
				// Adding Advise Details to Map
				if(adviseBal.compareTo(BigDecimal.ZERO) > 0){
					if(advise.getBounceID() > 0){
					
						if(receiptData.getAllocationMap().containsKey(RepayConstants.ALLOCATION_BOUNCE)){
							adviseBal = adviseBal.add(receiptData.getAllocationMap().get(RepayConstants.ALLOCATION_BOUNCE));
							receiptData.getAllocationMap().remove(RepayConstants.ALLOCATION_BOUNCE);
						}
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_BOUNCE, adviseBal);
						
						if(!receiptData.getAllocationDescMap().containsKey(RepayConstants.ALLOCATION_BOUNCE)){
							receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_BOUNCE,"Bounce Charges");
						}
					}else{
						receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID(), advise.getFeeTypeDesc());
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID(), adviseBal);
					}
				}
			}
		}
		
		logger.debug("Leaving");
		return receiptData;
	}
	
	/**
	 * Method for Calculation of Schedule payment based on Allocated Details from Receipts
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 */
	private FinReceiptData recalReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, Date valueDate, String receiptPurpose,boolean isPresentment) {
		logger.debug("Entering");
		
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetails = scheduleData.getFinanceScheduleDetails();
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();

		// Rendering 
		if(receiptDetailList == null || receiptDetailList.isEmpty()){
			return null;
		}
		
		// Fetch total Advise details
		Map<Long, ManualAdvise> adviseMap = new HashMap<Long, ManualAdvise>();
		List<ManualAdvise> advises = getManualAdviseDAO().getManualAdviseByRef(financeMain.getFinReference(), 
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");
		if (advises != null && !advises.isEmpty()) {
			for (int i = 0; i < advises.size(); i++) {
				if (adviseMap.containsKey(advises.get(i).getAdviseID())) {
					adviseMap.remove(advises.get(i).getAdviseID());
				}
				adviseMap.put(advises.get(i).getAdviseID(), advises.get(i));
			}
		}

		// Fetch total overdue details
		List<FinODDetails> overdueList = null;
		
		// Value Date Penalty Calculation
		if(DateUtility.compare(valueDate, DateUtility.getAppDate()) == 0){
			overdueList = getFinODDetailsDAO().getFinODBalByFinRef(financeMain.getFinReference());
		}else {
			// Calculate overdue Penalties
			Date reqMaxODDate = DateUtility.addDays(valueDate, -1);
			overdueList = getReceiptService().getValueDatePenalties(scheduleData, receiptData.getReceiptHeader().getReceiptAmount().subtract(
					receiptData.getReceiptHeader().getTotFeeAmount()), reqMaxODDate, null, true);
		}
		
		// Overdue Penalties Mapping Preparations
		Map<Date, FinODDetails> overdueMap = new HashMap<Date, FinODDetails>();
		if (overdueList != null && !overdueList.isEmpty()) {
			for (int m = 0; m < overdueList.size(); m++) {
				if (overdueMap.containsKey(overdueList.get(m).getFinODSchdDate())) {
					overdueMap.remove(overdueList.get(m).getFinODSchdDate());
				}
				overdueMap.put(overdueList.get(m).getFinODSchdDate(), overdueList.get(m));
			}
		}
		
		// Allocation Details Map Preparation based on key Details
		Map<String, BigDecimal> paidAllocationMap = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> waivedAllocationMap = new HashMap<String, BigDecimal>();
		BigDecimal totalWaivedAmt = BigDecimal.ZERO;
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		if (allocationList != null && !allocationList.isEmpty()) {
			for (int a = 0; a < allocationList.size(); a++) {
				ReceiptAllocationDetail allocate = allocationList.get(a);
				if (allocate.getAllocationTo() == 0 || allocate.getAllocationTo() == Long.MIN_VALUE) {
					
					paidAllocationMap.put(allocate.getAllocationType(), allocate.getPaidAmount().add(allocate.getWaivedAmount()));
					waivedAllocationMap.put(allocate.getAllocationType(), allocate.getWaivedAmount());
				}else{
					
					paidAllocationMap.put(allocate.getAllocationType()+"_"+allocate.getAllocationTo(), allocate.getPaidAmount().add(allocate.getWaivedAmount()));
					BigDecimal waivedAmount = BigDecimal.ZERO;
					if(waivedAllocationMap.containsKey(allocate.getAllocationType())){
						waivedAmount = waivedAllocationMap.get(allocate.getAllocationType());
					}
					waivedAllocationMap.put(allocate.getAllocationType(), waivedAmount.add(allocate.getWaivedAmount()));
				}
				
				if(!(StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_TDS) || 
						StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_PFT))){
					totalWaivedAmt = totalWaivedAmt.add(allocate.getWaivedAmount());
				}
			}
			
			// Setting Total Waived amount to Header details for Information Only
			receiptData.getReceiptHeader().setWaviedAmt(totalWaivedAmt);
		}
		
		List<FinRepayHeader> repayHeaderList = null;
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> tempScheduleDetails = cloner.deepClone(scheduleDetails);
		tempScheduleDetails = sortSchdDetails(tempScheduleDetails);
		
		//TDS Calculation, if Applicable
		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if(financeMain.isTDSApplicable()){

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/
			
			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
			}
		}
		
		// Fee Amount
		BigDecimal eventFeeBal = receiptData.getReceiptHeader().getTotFeeAmount();
		BigDecimal totPriPaidNow = BigDecimal.ZERO;
		BigDecimal totPftPaidNow = BigDecimal.ZERO;
		BigDecimal totLPftPaidNow = BigDecimal.ZERO;
		BigDecimal totFeePaidNow = BigDecimal.ZERO;
		BigDecimal totInsPaidNow = BigDecimal.ZERO;
		BigDecimal totPenaltyPaidNow = BigDecimal.ZERO;
		List<RepayScheduleDetail> pastdueRpySchdList = new ArrayList<>();

		// Start Receipt Details Rendering Process using allocation Details
		for (int i = 0; i < receiptDetailList.size(); i++) {
			
			FinReceiptDetail receiptDetail = receiptDetailList.get(i);
			
			// Internal temporary Record can't be processed for Calculation
			if(receiptDetail.isDelRecord()){
				continue;
			}
			
			BigDecimal totalReceiptAmt = receiptDetail.getAmount();
			BigDecimal actualReceiptAmt = receiptDetail.getAmount();
			if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)){
				if(eventFeeBal.compareTo(BigDecimal.ZERO) > 0){
					if(eventFeeBal.compareTo(totalReceiptAmt) > 0){
						eventFeeBal = eventFeeBal.subtract(totalReceiptAmt);
						totalReceiptAmt = BigDecimal.ZERO;
					}else{
						totalReceiptAmt = totalReceiptAmt.subtract(eventFeeBal);
						actualReceiptAmt = actualReceiptAmt.subtract(eventFeeBal);
						eventFeeBal = BigDecimal.ZERO;
					}
				}
			}

			// If no balance for repayment then return with out calculation
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
				totalReceiptAmt = BigDecimal.ZERO;
				continue;
			}
			
			repayHeaderList = new ArrayList<>();
			// Making Waived amount to total Receipts to set payment on Schedule cleared
			if(i == receiptDetailList.size() -1){
				totalReceiptAmt = totalReceiptAmt.add(totalWaivedAmt);
			}

			boolean isSchdPaid = false;
			List<RepayScheduleDetail> partialRpySchdList = new ArrayList<>();
			String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy();
			char[] rpyOrder = repayHierarchy.replace("CS", "C").toCharArray();
			int lastRenderSeq = 0;
			
			if((StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE) && i == 0) ||
					!StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				totPriPaidNow = BigDecimal.ZERO;
				totPftPaidNow = BigDecimal.ZERO;
				totLPftPaidNow = BigDecimal.ZERO;
				totFeePaidNow = BigDecimal.ZERO;
				totInsPaidNow = BigDecimal.ZERO;
				totPenaltyPaidNow = BigDecimal.ZERO;
				pastdueRpySchdList = new ArrayList<>();
			}
			
			boolean isPartialPayNow = false; 
			BigDecimal partialSettleAmount = BigDecimal.ZERO;
			BigDecimal advAmountPaid = BigDecimal.ZERO;

			// Load Pending Schedules until balance available for payment
			for (int s = 1; s < tempScheduleDetails.size(); s++) {
				FinanceScheduleDetail curSchd = tempScheduleDetails.get(s);
				Date schdDate = curSchd.getSchDate();
				RepayScheduleDetail rsd = null;
				
				if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY) &&  curSchd.getPresentmentId() > 0 && !isPresentment){
					continue;
				}

				// Skip if repayment date after Current Business date
				if (schdDate.compareTo(valueDate) > 0 &&  !StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
					break;
				}
			 
				// If Presentment Process, only Presentment Date schedule should be effected.
				if (isPresentment && (DateUtility.compare(valueDate, schdDate) != 0)) {
					continue;
				}
				
				// Find out early payment/ partial Settlement schedule term and amount
				if ((schdDate.compareTo(valueDate) == 0 && 
						StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) ||
						(schdDate.compareTo(valueDate) >= 0 && 
						StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE))) {
					
					// Manual Advises 
					if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {

						if(!adviseMap.isEmpty()){
							List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
							Collections.sort(adviseIdList);
							for (int a = 0; a < adviseIdList.size(); a++) {

								ManualAdvise advise = adviseMap.get(adviseIdList.get(a));
								if(advise != null){

									String allocateType = "";
									if(advise.getBounceID() > 0){
										allocateType = RepayConstants.ALLOCATION_BOUNCE;
									}else{
										allocateType = RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID();
									}

									if(paidAllocationMap.containsKey(allocateType)){
										BigDecimal advAllocateBal = paidAllocationMap.get(allocateType);
										if(advAllocateBal.compareTo(BigDecimal.ZERO) > 0){
											BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
											if(balAdvise.compareTo(BigDecimal.ZERO) > 0){
												if(totalReceiptAmt.compareTo(advAllocateBal) >= 0 && advAllocateBal.compareTo(balAdvise) < 0){
													balAdvise = advAllocateBal;
												}else if(totalReceiptAmt.compareTo(advAllocateBal) < 0 && balAdvise.compareTo(totalReceiptAmt) > 0){
													balAdvise = totalReceiptAmt;
												}

												// Reset Total Receipt Amount
												totalReceiptAmt = totalReceiptAmt.subtract(balAdvise);
												advAmountPaid = advAmountPaid.add(balAdvise);
												paidAllocationMap.put(allocateType, advAllocateBal.subtract(balAdvise));

												// Save Movements for Manual Advise
												ManualAdviseMovements movement = new ManualAdviseMovements();
												movement.setAdviseID(advise.getAdviseID());
												movement.setMovementDate(valueDate);
												movement.setMovementAmount(balAdvise);
												movement.setPaidAmount(balAdvise);
												movement.setWaivedAmount(BigDecimal.ZERO);
												movement.setFeeTypeCode(advise.getFeeTypeCode());
												receiptDetail.getAdvMovements().add(movement);
											}
										}
									}
								}
							}
						}

						// Event Action Fee Deduction from 
						if(eventFeeBal.compareTo(BigDecimal.ZERO) > 0){
							if(eventFeeBal.compareTo(totalReceiptAmt) > 0){
								eventFeeBal = eventFeeBal.subtract(totalReceiptAmt);
								totalReceiptAmt = BigDecimal.ZERO;
							}else{
								totalReceiptAmt = totalReceiptAmt.subtract(eventFeeBal);
								actualReceiptAmt = actualReceiptAmt.subtract(eventFeeBal);
								eventFeeBal = BigDecimal.ZERO;
							}
						}
					}
					
					// Only For Partial Settlement
					if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)){
						isPartialPayNow = true;
						partialSettleAmount = curSchd.getPartialPaidAmt();
					}
				}

				for (int j = 0; j < rpyOrder.length; j++) {

					char repayTo = rpyOrder[j];
					if(repayTo == RepayConstants.REPAY_PRINCIPAL){
						
						if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_PRI)){
							BigDecimal priAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_PRI);
							if(priAllocateBal.compareTo(BigDecimal.ZERO) > 0){
								BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
								if(balPri.compareTo(BigDecimal.ZERO) > 0){
									if(totalReceiptAmt.compareTo(priAllocateBal) >= 0 && priAllocateBal.compareTo(balPri) < 0){
										balPri = priAllocateBal;
									}else if(totalReceiptAmt.compareTo(priAllocateBal) < 0 && balPri.compareTo(totalReceiptAmt) > 0){
										balPri = totalReceiptAmt;
									}
									rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPri, valueDate, null);

									// Reset Total Receipt Amount
									totalReceiptAmt = totalReceiptAmt.subtract(balPri);
									if(!isPartialPayNow){
										totPriPaidNow = totPriPaidNow.add(balPri);
									}
									paidAllocationMap.put(RepayConstants.ALLOCATION_PRI, priAllocateBal.subtract(balPri));

									// Update Schedule to avoid on Next loop Payment
									curSchd.setSchdPriPaid(curSchd.getSchdPriPaid().add(balPri));
									isSchdPaid = true;
								}
							}
						}
					}else if(repayTo == RepayConstants.REPAY_PROFIT){

						String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
						char[] pftPayOrder = profit.toCharArray();
						for (char pftPayTo : pftPayOrder) {
							if(pftPayTo == RepayConstants.REPAY_PROFIT){

								if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_PFT)){
									BigDecimal pftAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_PFT);
									if(pftAllocateBal.compareTo(BigDecimal.ZERO) > 0){
										BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
										if(balPft.compareTo(BigDecimal.ZERO) > 0){
											
											if(pftAllocateBal.compareTo(balPft) < 0){
												balPft = pftAllocateBal;
											}
											
											BigDecimal actPftAdjust = balPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
											if(totalReceiptAmt.compareTo(pftAllocateBal) < 0  && actPftAdjust.compareTo(pftAllocateBal) >= 0){
												if(totalReceiptAmt.compareTo(actPftAdjust) >= 0){
													balPft = pftAllocateBal;
												}else{
													balPft = totalReceiptAmt.multiply(tdsMultiplier);
												}
											}else if(totalReceiptAmt.compareTo(pftAllocateBal) < 0  && totalReceiptAmt.compareTo(actPftAdjust) < 0){
												balPft = totalReceiptAmt.multiply(tdsMultiplier);
												actPftAdjust = totalReceiptAmt;
											}
											
											rsd = prepareRpyRecord(curSchd, rsd, pftPayTo, balPft, valueDate,null);
											
											// TDS Payments
											BigDecimal tdsAdjust = balPft.subtract(actPftAdjust);
											if(tdsAdjust.compareTo(BigDecimal.ZERO) > 0){
												rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_TDS, tdsAdjust, valueDate,null);
												
												if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_TDS)){
													BigDecimal totTDSPayNow = paidAllocationMap.get(RepayConstants.ALLOCATION_TDS);
													paidAllocationMap.remove(RepayConstants.ALLOCATION_TDS);
													paidAllocationMap.put(RepayConstants.ALLOCATION_TDS, totTDSPayNow.subtract(tdsAdjust));
												}
											}

											// Reset Total Receipt Amount
											totalReceiptAmt = totalReceiptAmt.subtract(actPftAdjust);
											totPftPaidNow = totPftPaidNow.add(balPft);
											paidAllocationMap.put(RepayConstants.ALLOCATION_PFT, pftAllocateBal.subtract(balPft));

											// Update Schedule to avoid on Next loop Payment
											curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().add(balPft));
											isSchdPaid = true;
										}
									}
								}

							}else if(pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT){
								
								if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_LPFT)){
									BigDecimal latePftAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_LPFT);
									if(latePftAllocateBal.compareTo(BigDecimal.ZERO) > 0){
										FinODDetails overdue = overdueMap.get(schdDate);
										if(overdue != null){
											BigDecimal balLatePft = overdue.getLPIBal();
											if(balLatePft.compareTo(BigDecimal.ZERO) > 0){
												if(totalReceiptAmt.compareTo(latePftAllocateBal) >= 0 && latePftAllocateBal.compareTo(balLatePft) < 0){
													balLatePft = latePftAllocateBal;
												}else if(totalReceiptAmt.compareTo(latePftAllocateBal) < 0 && balLatePft.compareTo(totalReceiptAmt) > 0){
													balLatePft = totalReceiptAmt;
												}
												rsd = prepareRpyRecord(curSchd, rsd, pftPayTo, balLatePft,valueDate, null);

												// Reset Total Receipt Amount
												totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
												totLPftPaidNow = totLPftPaidNow.add(balLatePft);
												paidAllocationMap.put(RepayConstants.ALLOCATION_LPFT, latePftAllocateBal.subtract(balLatePft));

												// Update Schedule to avoid on Next loop Payment
												overdue.setLPIBal(overdue.getLPIBal().subtract(balLatePft));
												isSchdPaid = true;
											}
										}
									}
								}
							}
							
							// No more Receipt amount left for next schedules
							if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
								totalReceiptAmt = BigDecimal.ZERO;
								break;
							}
						}

					}else if(repayTo == RepayConstants.REPAY_PENALTY){
						
						if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_ODC)){
							BigDecimal penaltyAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_ODC);
							if(penaltyAllocateBal.compareTo(BigDecimal.ZERO) > 0){
								FinODDetails overdue = overdueMap.get(schdDate);
								if(overdue != null){
									BigDecimal balPenalty = overdue.getTotPenaltyBal();
									if(balPenalty.compareTo(BigDecimal.ZERO) > 0){
										if(totalReceiptAmt.compareTo(penaltyAllocateBal) >= 0 && penaltyAllocateBal.compareTo(balPenalty) < 0){
											balPenalty = penaltyAllocateBal;
										}else if(totalReceiptAmt.compareTo(penaltyAllocateBal) < 0 && balPenalty.compareTo(totalReceiptAmt) > 0){
											balPenalty = totalReceiptAmt;
										}
										rsd = prepareRpyRecord(curSchd, rsd, repayTo, balPenalty, valueDate,overdue.getTotPenaltyBal());

										// Reset Total Receipt Amount
										totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
										totPenaltyPaidNow = totPenaltyPaidNow.add(balPenalty);

										paidAllocationMap.put(RepayConstants.ALLOCATION_ODC, penaltyAllocateBal.subtract(balPenalty));

										// Update Schedule to avoid on Next loop Payment
										overdue.setTotPenaltyBal(overdue.getTotPenaltyBal().subtract(balPenalty));
										overdueMap.remove(schdDate);
										overdueMap.put(schdDate, overdue);
										isSchdPaid = true;
									}
								}
							}
						}

					}else if(repayTo == RepayConstants.REPAY_OTHERS){

						// If Schedule has Unpaid Fee Amount
						if(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()).compareTo(BigDecimal.ZERO) > 0){

							// Fee Detail Collection
							for (int k = 0; k < scheduleData.getFinFeeDetailList().size(); k++) {
								FinFeeDetail feeSchd = scheduleData.getFinFeeDetailList().get(k);
								if(StringUtils.equals(feeSchd.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)){
									continue;
								}
								List<FinFeeScheduleDetail> feeSchdList = feeSchd.getFinFeeScheduleDetailList();

								// No more Receipt amount left for next schedules
								if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
									totalReceiptAmt = BigDecimal.ZERO;
									break;
								}

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
										
										if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID())){
											BigDecimal feeAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
											if(feeAllocateBal.compareTo(BigDecimal.ZERO) > 0){
												BigDecimal balFee = feeSchdList.get(l).getSchAmount().subtract(feeSchdList.get(l).getPaidAmount());
												if(balFee.compareTo(BigDecimal.ZERO) > 0){
													if(totalReceiptAmt.compareTo(feeAllocateBal) >= 0 && feeAllocateBal.compareTo(balFee) < 0){
														balFee = feeAllocateBal;
													}else if(totalReceiptAmt.compareTo(feeAllocateBal) < 0 && balFee.compareTo(totalReceiptAmt) > 0){
														balFee = totalReceiptAmt;
													}
													rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_FEE, balFee, valueDate,null);

													// Reset Total Receipt Amount
													totalReceiptAmt = totalReceiptAmt.subtract(balFee);
													totFeePaidNow = totFeePaidNow.add(balFee);

													paidAllocationMap.put(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID(), feeAllocateBal.subtract(balFee));

													// Update Schedule to avoid on Next loop Payment
													feeSchdList.get(l).setPaidAmount(feeSchdList.get(l).getPaidAmount().add(balFee));
													isSchdPaid = true;
												}
											}
										}
										if(lastRenderSeq == 0){
											lastRenderSeq = l;
										}
										break;
									}
								}
							}
						}

						// If Schedule has Unpaid Fee Amount
						if((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0){

							// Insurance Details Collection
							for (int k = 0; k < scheduleData.getFinInsuranceList().size(); k++) {
								FinInsurances insSchd = scheduleData.getFinInsuranceList().get(k);
								List<FinSchFrqInsurance> insSchdList = insSchd.getFinSchFrqInsurances();

								// No more Receipt amount left for next schedules
								if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
									totalReceiptAmt = BigDecimal.ZERO;
									break;
								}

								if(insSchdList == null || insSchdList.isEmpty()){
									continue;
								}

								// Calculate and set Fee Amount based on Fee ID
								for (int l = 0; l < insSchdList.size(); l++) {
									if(insSchdList.get(l).getInsSchDate().compareTo(schdDate) == 0){

										if(paidAllocationMap.containsKey(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId())){
											BigDecimal insAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId());
											if(insAllocateBal.compareTo(BigDecimal.ZERO) > 0){
												BigDecimal balIns = insSchdList.get(l).getAmount().subtract(insSchdList.get(l).getInsurancePaid());
												if(balIns.compareTo(BigDecimal.ZERO) > 0){
													if(totalReceiptAmt.compareTo(insAllocateBal) >= 0 && insAllocateBal.compareTo(balIns) < 0){
														balIns = insAllocateBal;
													}else if(totalReceiptAmt.compareTo(insAllocateBal) < 0 && balIns.compareTo(totalReceiptAmt) > 0){
														balIns = totalReceiptAmt;
													}
													rsd = prepareRpyRecord(curSchd, rsd, RepayConstants.REPAY_INS, balIns, valueDate,null);

													// Reset Total Receipt Amount
													totalReceiptAmt = totalReceiptAmt.subtract(balIns);
													totInsPaidNow = totInsPaidNow.add(balIns);

													paidAllocationMap.put(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId(), insAllocateBal.subtract(balIns));

													// Update Schedule to avoid on Next loop Payment
													insSchdList.get(l).setInsurancePaid(insSchdList.get(l).getInsurancePaid().add(balIns));
													isSchdPaid = true;
												}
											}
										}
										break;
									}
								}
							}
						}
					}

					// No more Receipt amount left for next schedules
					if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
						totalReceiptAmt = BigDecimal.ZERO;
						break;
					}
				}
				
				// Add Repay Schedule detail List
				if(rsd != null){
					if(isPartialPayNow){
						partialRpySchdList.add(rsd);
					}else{
						pastdueRpySchdList.add(rsd);
					}
				}
				
				// No more Receipt amount left for next schedules
				if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
					totalReceiptAmt = BigDecimal.ZERO;
					break;
				}

				// Sequence Order Increment to reduce loops on Fee setting
				lastRenderSeq++;
			}
			
			// Manual Advises 
			if (!isPartialPayNow && totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {

				if(!adviseMap.isEmpty()){
					List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
					Collections.sort(adviseIdList);
					for (int a = 0; a < adviseIdList.size(); a++) {

						ManualAdvise advise = adviseMap.get(adviseIdList.get(a));
						if(advise != null){
							
							String allocateType = "";
							if(advise.getBounceID() > 0){
								allocateType = RepayConstants.ALLOCATION_BOUNCE;
							}else{
								allocateType = RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID();
							}
							
							if(paidAllocationMap.containsKey(allocateType)){
								BigDecimal insAllocateBal = paidAllocationMap.get(allocateType);
								if(insAllocateBal.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
									if(balAdvise.compareTo(BigDecimal.ZERO) > 0){
										if(totalReceiptAmt.compareTo(insAllocateBal) >= 0 && insAllocateBal.compareTo(balAdvise) < 0){
											balAdvise = insAllocateBal;
										}else if(totalReceiptAmt.compareTo(insAllocateBal) < 0 && balAdvise.compareTo(totalReceiptAmt) > 0){
											balAdvise = totalReceiptAmt;
										}

										// Reset Total Receipt Amount
										totalReceiptAmt = totalReceiptAmt.subtract(balAdvise);
										advAmountPaid = advAmountPaid.add(balAdvise);
										paidAllocationMap.put(allocateType, insAllocateBal.subtract(balAdvise));
										
										// Save Movements for Manual Advise
										ManualAdviseMovements movement = new ManualAdviseMovements();
										movement.setAdviseID(advise.getAdviseID());
										movement.setMovementDate(valueDate);
										movement.setMovementAmount(balAdvise);
										movement.setPaidAmount(balAdvise);
										movement.setWaivedAmount(BigDecimal.ZERO);
										movement.setFeeTypeCode(advise.getFeeTypeCode());
										receiptDetail.getAdvMovements().add(movement);
									}
								}
							}
						}
					}
				}
			}
			
			if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				if(i != receiptDetailList.size() - 1){
					continue;
				}
			}
			
			
			FinRepayHeader repayHeader = null;
			BigDecimal balAmount = actualReceiptAmt.subtract(totalReceiptAmt);
			if(((actualReceiptAmt.compareTo(totalReceiptAmt) > 0  && balAmount.compareTo(advAmountPaid) != 0) || 
					totalWaivedAmt.compareTo(BigDecimal.ZERO) > 0) && 
					actualReceiptAmt.compareTo(partialSettleAmount) > 0 && isSchdPaid){
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(valueDate);
				if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
					repayHeader.setRepayAmount(receiptData.getReceiptHeader().getReceiptAmount());
					repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
				}else{
					repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_SCHDRPY);
					repayHeader.setRepayAmount(actualReceiptAmt.subtract(totalReceiptAmt));
				}
				repayHeader.setPriAmount(totPriPaidNow);
				repayHeader.setPftAmount(totPftPaidNow);
				repayHeader.setLatePftAmount(totLPftPaidNow);
				repayHeader.setTotalPenalty(totPenaltyPaidNow);
				repayHeader.setTotalIns(totInsPaidNow);
				repayHeader.setTotalSchdFee(totFeePaidNow);
				repayHeader.setTotalWaiver(totalWaivedAmt);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(pastdueRpySchdList);
				repayHeaderList.add(repayHeader);
			}
			
			if(totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0 && 
					(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY) || 
							StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE))){
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(valueDate);
				repayHeader.setRepayAmount(totalReceiptAmt);
				repayHeader.setFinEvent(receiptData.getReceiptHeader().getExcessAdjustTo());
				repayHeader.setPriAmount(BigDecimal.ZERO);
				repayHeader.setPftAmount(BigDecimal.ZERO);
				repayHeader.setLatePftAmount(BigDecimal.ZERO);
				repayHeader.setTotalPenalty(BigDecimal.ZERO);
				repayHeader.setTotalIns(BigDecimal.ZERO);
				repayHeader.setTotalSchdFee(BigDecimal.ZERO);
				repayHeader.setTotalWaiver(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(null);
				repayHeaderList.add(repayHeader);
			}
			
			// Prepare Remaining Balance Amount as Partial Settlement , If selected Receipt Purpose is same
			if (partialSettleAmount.compareTo(BigDecimal.ZERO) > 0 && 
					StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {

				// Prepare Repay Header for Partial Settlement 
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(valueDate);
				repayHeader.setRepayAmount(partialSettleAmount);
				repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_EARLYRPY);
				repayHeader.setPriAmount(partialSettleAmount);
				repayHeader.setPftAmount(BigDecimal.ZERO);
				repayHeader.setLatePftAmount(BigDecimal.ZERO);
				repayHeader.setTotalPenalty(BigDecimal.ZERO);
				repayHeader.setTotalIns(BigDecimal.ZERO);
				repayHeader.setTotalSchdFee(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(partialRpySchdList);
				repayHeaderList.add(repayHeader);
			}
			
			// Adding Repay Headers to Receipt Details
			receiptDetail.setRepayHeaders(repayHeaderList);
			
		}
		
		overdueList = null;
		overdueMap = null;
		paidAllocationMap = null;
		
		// If Total Waived amount is Zero, no need to Waived amount settings
		if(totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0){
			waivedAllocationMap = null;

			logger.debug("Leaving");
			return receiptData;
		}
		
		// Waived Details Updation on Schedules
		int size = receiptDetailList.size();
		for (int i = size - 1; i >= 0; i--) {
			FinReceiptDetail rd = receiptDetailList.get(i);
			
			int rhSize = rd.getRepayHeaders().size();
			for (int j = rhSize - 1; j >= 0; j--) {
				
				FinRepayHeader rh = rd.getRepayHeaders().get(j);
				if(rh.getRepayScheduleDetails() != null && !rh.getRepayScheduleDetails().isEmpty()){
					int rsSize = rh.getRepayScheduleDetails().size();
					if(rsSize > 0){
						for (int k = rsSize - 1; k >= 0; k--) {

							RepayScheduleDetail rsd = rh.getRepayScheduleDetails().get(k);

							// Principal Amount
							if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_PRI)){
								BigDecimal waivedNow = rsd.getPrincipalSchdPayNow();
								BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_PRI);
								if(waivedNow.compareTo(balWaived) > 0){
									waivedNow = balWaived;
								}
								rsd.setPriSchdWaivedNow(waivedNow);
								totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
								waivedAllocationMap.put(RepayConstants.ALLOCATION_PRI, balWaived.subtract(waivedNow));
							}

							// Profit Amount
							if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_PFT)){
								BigDecimal waivedNow = rsd.getProfitSchdPayNow();
								BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_PFT);
								if(waivedNow.compareTo(balWaived) > 0){
									waivedNow = balWaived;
								}
								rsd.setPftSchdWaivedNow(waivedNow);
								totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
								waivedAllocationMap.put(RepayConstants.ALLOCATION_PFT, balWaived.subtract(waivedNow));
								
								// TDS Amount
								if(financeMain.isTDSApplicable()){
									BigDecimal tdsWaivedNow = rsd.getTdsSchdPayNow();
									BigDecimal tdsBalWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_TDS);
									if(tdsWaivedNow.compareTo(tdsBalWaived) > 0){
										tdsWaivedNow = tdsBalWaived;
									}
									rsd.setTdsSchdPayNow(rsd.getTdsSchdPayNow().subtract(tdsWaivedNow));
									waivedAllocationMap.put(RepayConstants.ALLOCATION_TDS, tdsBalWaived.subtract(tdsWaivedNow));
								}
							}

							// Late Payment Profit Amount
							if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_LPFT)){
								BigDecimal waivedNow = rsd.getLatePftSchdPayNow();
								BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_LPFT);
								if(waivedNow.compareTo(balWaived) > 0){
									waivedNow = balWaived;
								}
								rsd.setLatePftSchdWaivedNow(waivedNow);
								rsd.setLatePftSchdPayNow(rsd.getLatePftSchdPayNow().subtract(waivedNow));
								totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
								waivedAllocationMap.put(RepayConstants.ALLOCATION_LPFT, balWaived.subtract(waivedNow));
							}

							// Penalty Amount
							if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_ODC)){
								BigDecimal waivedNow = rsd.getPenaltyPayNow();
								BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_ODC);
								if(waivedNow.compareTo(balWaived) > 0){
									waivedNow = balWaived;
								}
								rsd.setWaivedAmt(waivedNow);
								rsd.setPenaltyPayNow(rsd.getPenaltyPayNow().subtract(waivedNow));
								totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
								waivedAllocationMap.put(RepayConstants.ALLOCATION_ODC, balWaived.subtract(waivedNow));
							}

							// Schedule Fee Amount
							if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_FEE)){
								BigDecimal waivedNow = rsd.getSchdFeePayNow();
								BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_FEE);
								if(waivedNow.compareTo(balWaived) > 0){
									waivedNow = balWaived;
								}
								rsd.setSchdFeeWaivedNow(waivedNow);
								totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
								waivedAllocationMap.put(RepayConstants.ALLOCATION_FEE, balWaived.subtract(waivedNow));
							}

							// Insurance Amount
							if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_INS)){
								BigDecimal waivedNow = rsd.getSchdInsPayNow();
								BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_INS);
								if(waivedNow.compareTo(balWaived) > 0){
									waivedNow = balWaived;
								}
								rsd.setSchdInsWaivedNow(waivedNow);
								totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
								waivedAllocationMap.put(RepayConstants.ALLOCATION_INS, balWaived.subtract(waivedNow));
							}

							// If No Outstanding Waiver amount
							if(totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0){
								break;
							}
						}
					}
				}
				
				// If No Outstanding Waiver amount
				if(totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0){
					break;
				}
			}
			
			// Schedule Fee Amount
			if(waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_MANADV) ||
					waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_BOUNCE)){
				for (ManualAdviseMovements advMovement : rd.getAdvMovements()) {
					
					boolean isBounce = false;
					if(adviseMap.containsKey(advMovement.getAdviseID())){
						ManualAdvise advise = adviseMap.get(advMovement.getAdviseID());
						if(advise.getBounceID() > 0){
							isBounce = true;
						}
					}
					BigDecimal waivedNow = advMovement.getPaidAmount();
					BigDecimal balWaived = BigDecimal.ZERO;
					if(isBounce){
						balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_BOUNCE);
					}else{
						balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_MANADV);
					}
					if(waivedNow.compareTo(balWaived) > 0){
						waivedNow = balWaived;
					}
					advMovement.setWaivedAmount(waivedNow);
					advMovement.setPaidAmount(advMovement.getPaidAmount().subtract(waivedNow));
					totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
					
					if(isBounce){
						waivedAllocationMap.put(RepayConstants.ALLOCATION_BOUNCE, balWaived.subtract(waivedNow));
					}else{
						waivedAllocationMap.put(RepayConstants.ALLOCATION_MANADV, balWaived.subtract(waivedNow));
					}
				}
			}
			
			// If No Outstanding Waiver amount
			if(totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0){
				break;
			}
		}

		waivedAllocationMap = null;
		
		logger.debug("Leaving");
		return receiptData;
	}
	
	/**
	 * Method for Sorting Schedule Details
	 * @param financeScheduleDetail
	 * @return
	 */
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
	
	/**
	 * Method for Preparation of Repayment Schedule Details
	 * @param curSchd
	 * @param rsd
	 * @param rpyTo
	 * @param balPayNow
	 * @return
	 */
	private RepayScheduleDetail prepareRpyRecord(FinanceScheduleDetail curSchd, RepayScheduleDetail rsd, char rpyTo, 
			BigDecimal balPayNow, Date valueDate, BigDecimal actualPenalty){
		
		if(rsd == null){
			rsd = new RepayScheduleDetail();
			rsd.setFinReference(curSchd.getFinReference());
			rsd.setSchDate(curSchd.getSchDate());
			rsd.setDefSchdDate(curSchd.getSchDate());

			rsd.setSchdFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			rsd.setProfitSchd(curSchd.getProfitSchd());
			rsd.setProfitSchdPaid(curSchd.getSchdPftPaid());
			rsd.setProfitSchdBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
			
			rsd.setPrincipalSchd(curSchd.getPrincipalSchd());
			rsd.setPrincipalSchdPaid(curSchd.getSchdPriPaid());
			rsd.setPrincipalSchdBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));

			rsd.setSchdIns(curSchd.getInsSchd());
			rsd.setSchdInsPaid(curSchd.getSchdInsPaid());
			rsd.setSchdInsBal(rsd.getSchdIns().subtract(rsd.getSchdInsPaid()));
			
			rsd.setSchdSuplRent(curSchd.getSuplRent());
			rsd.setSchdSuplRentPaid(curSchd.getSuplRentPaid());
			rsd.setSchdSuplRentBal(rsd.getSchdSuplRent().subtract(rsd.getSchdSuplRentPaid()));
			
			rsd.setSchdIncrCost(curSchd.getIncrCost());
			rsd.setSchdIncrCostPaid(curSchd.getIncrCostPaid());
			rsd.setSchdIncrCostBal(rsd.getSchdIncrCost().subtract(rsd.getSchdIncrCostPaid()));
			
			rsd.setSchdFee(curSchd.getFeeSchd());
			rsd.setSchdFeePaid(curSchd.getSchdFeePaid());
			rsd.setSchdFeeBal(rsd.getSchdFee().subtract(rsd.getSchdFeePaid()));
			
			rsd.setDaysLate(DateUtility.getDaysBetween(curSchd.getSchDate(), valueDate));
			rsd.setDaysEarly(0);
		}
		
		// Principal Payment 
		if(rpyTo == RepayConstants.REPAY_PRINCIPAL){
			rsd.setPrincipalSchdPayNow(balPayNow);
		}
		
		// Profit Payment 
		if(rpyTo == RepayConstants.REPAY_PROFIT){
			rsd.setProfitSchdPayNow(balPayNow);
		}
		
		// TDS Payment 
		if(rpyTo == RepayConstants.REPAY_TDS){
			rsd.setTdsSchdPayNow(balPayNow);
		}
		
		// Late Payment Profit Payment 
		if(rpyTo == RepayConstants.REPAY_LATEPAY_PROFIT){
			rsd.setLatePftSchdPayNow(balPayNow);
		}
		
		// Fee Detail Payment 
		if(rpyTo == RepayConstants.REPAY_FEE){
			rsd.setSchdFeePayNow(rsd.getSchdFeePayNow().add(balPayNow));
		}
		
		// Insurance Detail Payment 
		if(rpyTo == RepayConstants.REPAY_INS){
			rsd.setSchdInsPayNow(rsd.getSchdInsPayNow().add(balPayNow));
		}
		
		// Penalty Charge Detail Payment 
		if(rpyTo == RepayConstants.REPAY_PENALTY){
			rsd.setPenaltyAmt(actualPenalty);
			rsd.setPenaltyPayNow(balPayNow);
		}
		
		return rsd;
		
	}
	
	/**
	 * Method for Processing Schedule Data and Receipts to Prepare Allocation Details
	 * @param receiptData
	 * @param aFinScheduleData
	 */
	public Map<String, BigDecimal> recalAutoAllocation(FinScheduleData scheduleData, BigDecimal totalReceiptAmt, Date valueDate, String receiptPurpose, boolean isPresentment) {
		logger.debug("Entering");
		
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetails = scheduleData.getFinanceScheduleDetails();

		// If no balance for repayment then return with out calculation
		if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}
		// Fetch total Advise details
		Map<Long, ManualAdvise> adviseMap = new HashMap<Long, ManualAdvise>();
		if(!isPresentment){
			List<ManualAdvise> advises = getManualAdviseDAO().getManualAdviseByRef(financeMain.getFinReference(), 
					FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");
			if (advises != null && !advises.isEmpty()) {
				for (int i = 0; i < advises.size(); i++) {
					if (adviseMap.containsKey(advises.get(i).getAdviseID())) {
						adviseMap.remove(advises.get(i).getAdviseID());
					}
					adviseMap.put(advises.get(i).getAdviseID(), advises.get(i));
				}
			}
		}
		
		// Fetching Actual Late Payments based on Value date passing
		Map<Date, FinODDetails> overdueMap = new HashMap<Date, FinODDetails>();
		Date reqMaxODDate = DateUtility.addDays(valueDate, -1);
		List<FinODDetails> overdueList = null;
		if(DateUtility.compare(valueDate, DateUtility.getAppDate()) == 0){
			overdueList = getFinODDetailsDAO().getFinODBalByFinRef(financeMain.getFinReference());
		}else {
			// Calculate overdue Penalties
			overdueList = getReceiptService().getValueDatePenalties(scheduleData, totalReceiptAmt, reqMaxODDate, null, true);
		}

		if (overdueList != null && !overdueList.isEmpty()) {
			for (int i = 0; i < overdueList.size(); i++) {
				if (overdueMap.containsKey(overdueList.get(i).getFinODSchdDate())) {
					overdueMap.remove(overdueList.get(i).getFinODSchdDate());
				}
				
				if(DateUtility.compare(overdueList.get(i).getFinODSchdDate(), reqMaxODDate) <= 0){
					overdueMap.put(overdueList.get(i).getFinODSchdDate(), overdueList.get(i));
				}
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
		boolean isLastTermForES = false;// Last Term for Early Settlement
		boolean pftCalcCompleted = false;// Profit calculation Completed flag setting for Last term on Early settlement
		boolean priCalcCompleted = false;// Principal calculation Completed flag setting for Last term on Early settlement
		
		//TDS Calculation, if Applicable
		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if(financeMain.isTDSApplicable()){

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/
			
			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20, RoundingMode.HALF_DOWN);
			}
		}
		
		// Load Pending Schedules until balance available for payment
		for (int i = 1; i < scheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = scheduleDetails.get(i);
			FinanceScheduleDetail prvSchd = scheduleDetails.get(i-1);
			Date schdDate = curSchd.getSchDate();

			// Skip if repayments date after Current Business date
			if (schdDate.compareTo(valueDate) > 0 && 
					!StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				break;
			}
			
			if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY) &&  curSchd.getPresentmentId() > 0 && !isPresentment){
				continue;
			}
			
			// If Presentment Process, only Presentment Date schedule should be effected.
			if (isPresentment && (DateUtility.compare(valueDate, schdDate) != 0)) {
				continue;
			}
			
			// Early settlement case to calculate profit Till Accrual amount
			if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				if (schdDate.compareTo(valueDate) >= 0){
					isLastTermForES = true;
				}
			}

			for (int j = 0; j < rpyOrder.length; j++) {
				
				char repayTo = rpyOrder[j];
				if(repayTo == RepayConstants.REPAY_PRINCIPAL){
					
					// On Early settlement case, If profit is calculated upto Accruals, from Next Schedule onwards no need to consider Profit Amount
					if(priCalcCompleted){
						continue;
					}
					
					BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
					
					if(isLastTermForES){
						priCalcCompleted = true;
						if (schdDate.compareTo(valueDate) >= 0){
							balPri = balPri.add(curSchd.getClosingBalance().subtract(curSchd.getCpzAmount()));
						}
					}
					
					if(balPri.compareTo(BigDecimal.ZERO) > 0){
						if(totalReceiptAmt.compareTo(balPri) > 0){
							totalReceiptAmt = totalReceiptAmt.subtract(balPri);
						}else{
							balPri = totalReceiptAmt;
							totalReceiptAmt = BigDecimal.ZERO;
						}

						BigDecimal totPriPayNow = BigDecimal.ZERO;
						if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PRI)){
							totPriPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_PRI);
							allocatePaidMap.remove(RepayConstants.ALLOCATION_PRI);
						}
						allocatePaidMap.put(RepayConstants.ALLOCATION_PRI, totPriPayNow.add(balPri));
					}
				}else if(repayTo == RepayConstants.REPAY_PROFIT){
					
					// On Early settlement case, If profit is calculated upto Accruals, from Next Schedule onwards no need to consider Profit Amount
					if(pftCalcCompleted){
						continue;
					}
					
					String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
					char[] pftPayOrder = profit.toCharArray();
					for (char pftPayTo : pftPayOrder) {
						if(pftPayTo == RepayConstants.REPAY_PROFIT){
							
							BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());

							// On Early settlement Case, for the last terms should consider total outstanding Principal
							if(isLastTermForES){
								pftCalcCompleted = true;
								if (schdDate.compareTo(valueDate) > 0){
									balPft = CalculationUtil.calInterest(prvSchd.getSchDate(), valueDate, curSchd.getBalanceForPftCal(),
											prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());
									
									balPft = balPft.add(prvSchd.getProfitBalance()).add(prvSchd.getProfitFraction());
									balPft = CalculationUtil.roundAmount(balPft, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
								}else if (schdDate.compareTo(valueDate) == 0){
									balPft = curSchd.getProfitCalc().subtract(curSchd.getSchdPftPaid()).add(prvSchd.getProfitBalance());
								}
							}
							
							if(balPft.compareTo(BigDecimal.ZERO) > 0){

								BigDecimal actPftAdjust = BigDecimal.ZERO;
								if(isLastTermForES){
									actPftAdjust = balPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
									//actPftAdjust = CalculationUtil.roundAmount(actPftAdjust, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
								}else{
									actPftAdjust = balPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
									//actPftAdjust = CalculationUtil.roundAmount(actPftAdjust, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
								}
								
								// TDS Adjustments
								BigDecimal tdsAdjust = BigDecimal.ZERO;
								if(totalReceiptAmt.compareTo(actPftAdjust) > 0){
									totalReceiptAmt = totalReceiptAmt.subtract(actPftAdjust);
									tdsAdjust = balPft.subtract(actPftAdjust);
								}else{
									actPftAdjust = totalReceiptAmt;
									tdsAdjust = (actPftAdjust.multiply(tdsMultiplier)).subtract(actPftAdjust);
									totalReceiptAmt = BigDecimal.ZERO;
								}
								
								// TDS Re-adjust(minor difference) TEMP FIX
								if(balPft.compareTo(actPftAdjust.add(tdsAdjust)) < 0){
									tdsAdjust = tdsAdjust.add(balPft.subtract(actPftAdjust.add(tdsAdjust)));
								}

								// Profit Amount Payments
								BigDecimal totPftPayNow = BigDecimal.ZERO;
								if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PFT)){
									totPftPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_PFT);
									allocatePaidMap.remove(RepayConstants.ALLOCATION_PFT);
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_PFT, totPftPayNow.add(actPftAdjust.add(tdsAdjust)));
								
								// TDS Payments
								BigDecimal totTDSPayNow = BigDecimal.ZERO;
								if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_TDS)){
									totTDSPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_TDS);
									allocatePaidMap.remove(RepayConstants.ALLOCATION_TDS);
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_TDS, totTDSPayNow.add(tdsAdjust));
							}
							
						}else if(pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT){
							
							FinODDetails overdue = overdueMap.get(schdDate);
							if(overdue != null && DateUtility.compare(overdue.getFinODSchdDate(), valueDate) <= 0){
								BigDecimal balLatePft = overdue.getLPIBal();
								if(balLatePft.compareTo(BigDecimal.ZERO) > 0){
									if(totalReceiptAmt.compareTo(balLatePft) > 0){
										totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
									}else{
										balLatePft = totalReceiptAmt;
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
						}
					}
					
				}else if(repayTo == RepayConstants.REPAY_PENALTY){
					if(!seperatePenalties){
						FinODDetails overdue = overdueMap.get(schdDate);
						if(overdue != null && DateUtility.compare(overdue.getFinODSchdDate(), valueDate) <= 0){
							BigDecimal balPenalty = overdue.getTotPenaltyBal();
							if(balPenalty.compareTo(BigDecimal.ZERO) > 0){
								if(totalReceiptAmt.compareTo(balPenalty) > 0){
									totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
								}else{
									balPenalty = totalReceiptAmt;
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
					
				}else if(repayTo == RepayConstants.REPAY_OTHERS){
					
					// If Schedule has Unpaid Fee Amount
					if((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0){
						
						// Fee Detail Collection
						for (int k = 0; k < scheduleData.getFinFeeDetailList().size(); k++) {
							FinFeeDetail feeSchd = scheduleData.getFinFeeDetailList().get(k);
							if(StringUtils.equals(feeSchd.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)){
								continue;
							}
							List<FinFeeScheduleDetail> feeSchdList = feeSchd.getFinFeeScheduleDetailList();
							
							// No more Receipt amount left for next schedules
							if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
								totalReceiptAmt = BigDecimal.ZERO;
								break;
							}
							
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
									if(balFee.compareTo(BigDecimal.ZERO) > 0){
										if(totalReceiptAmt.compareTo(balFee) > 0){
											totalReceiptAmt = totalReceiptAmt.subtract(balFee);
										}else{
											balFee = totalReceiptAmt;
											totalReceiptAmt = BigDecimal.ZERO;
										}

										BigDecimal totFeePayNow = BigDecimal.ZERO;
										if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID())){
											totFeePayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
											allocatePaidMap.remove(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID());
										}
										allocatePaidMap.put(RepayConstants.ALLOCATION_FEE+"_"+feeSchd.getFeeID(), totFeePayNow.add(balFee));
									}
									if(lastRenderSeq == 0){
										lastRenderSeq = l;
									}
									break;
								}
							}
						}
					}
					
					// If Schedule has Unpaid Fee Amount
					if((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0){

						// Insurance Details Collection
						for (int k = 0; k < scheduleData.getFinInsuranceList().size(); k++) {
							FinInsurances insSchd = scheduleData.getFinInsuranceList().get(k);
							List<FinSchFrqInsurance> insSchdList = insSchd.getFinSchFrqInsurances();
							
							// No more Receipt amount left for next schedules
							if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
								totalReceiptAmt = BigDecimal.ZERO;
								break;
							}
							
							if(insSchdList == null || insSchdList.isEmpty()){
								continue;
							}

							// Calculate and set Fee Amount based on Fee ID
							for (int l = 0; l < insSchdList.size(); l++) {
								if(insSchdList.get(l).getInsSchDate().compareTo(schdDate) == 0){

									BigDecimal balIns = insSchdList.get(l).getAmount().subtract(insSchdList.get(l).getInsurancePaid());
									if(balIns.compareTo(BigDecimal.ZERO) > 0){
										if(totalReceiptAmt.compareTo(balIns) > 0){
											totalReceiptAmt = totalReceiptAmt.subtract(balIns);
										}else{
											balIns = totalReceiptAmt;
											totalReceiptAmt = BigDecimal.ZERO;
										}

										BigDecimal totInsPayNow = BigDecimal.ZERO;
										if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId())){
											totInsPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId());
											allocatePaidMap.remove(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId());
										}
										allocatePaidMap.put(RepayConstants.ALLOCATION_INS+"_"+insSchd.getInsId(), totInsPayNow.add(balIns));
									}
									break;
								}
							}
						}
					}
				}
				
				// No more Receipt amount left for next schedules
				if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
					totalReceiptAmt = BigDecimal.ZERO;
					break;
				}
			}

			// No more Receipt amount left for next schedules
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
				totalReceiptAmt = BigDecimal.ZERO;
				break;
			}
			
			// Sequence Order Increment to reduce loops on Fee setting
			lastRenderSeq++;
		}
		
		BigDecimal pftAmount = BigDecimal.ZERO;
		BigDecimal tdsAmount = BigDecimal.ZERO;
		if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_TDS)){
			tdsAmount = allocatePaidMap.get(RepayConstants.ALLOCATION_TDS);
		}
		if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PFT)){
			pftAmount = allocatePaidMap.get(RepayConstants.ALLOCATION_PFT);
		}
		// Net Interest Amount Setting Excluding TDS
		allocatePaidMap.put(RepayConstants.ALLOCATION_NPFT, pftAmount.subtract(tdsAmount));

		//Set Penalty Payments for Prepared Past due Schedule Details
		if (seperatePenalties && totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
			
			if(!overdueMap.isEmpty()){
				List<Date> odDateList = new ArrayList<Date>(overdueMap.keySet());
				Collections.sort(odDateList);
				for (int i = 0; i < odDateList.size(); i++) {

					FinODDetails overdue = overdueMap.get(odDateList.get(i));
					if(overdue != null && DateUtility.compare(overdue.getFinODSchdDate(), valueDate) <= 0){
						BigDecimal balPenalty = overdue.getTotPenaltyBal();
						if(balPenalty.compareTo(BigDecimal.ZERO) > 0){
							if(totalReceiptAmt.compareTo(balPenalty) > 0){
								totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
							}else{
								balPenalty = totalReceiptAmt;
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
			}
		}
		
		// Manual Advises
		if (!isPresentment) {
			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {
				if(!adviseMap.isEmpty()){
					List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
					Collections.sort(adviseIdList);
					for (int i = 0; i < adviseIdList.size(); i++) {
						
						ManualAdvise advise = adviseMap.get(adviseIdList.get(i));
						if(advise != null){
							BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
							
							if(balAdvise.compareTo(BigDecimal.ZERO) > 0){
								if(totalReceiptAmt.compareTo(balAdvise) > 0){
									totalReceiptAmt = totalReceiptAmt.subtract(balAdvise);
								}else{
									balAdvise = totalReceiptAmt;
									totalReceiptAmt = BigDecimal.ZERO;
								}
								
								BigDecimal totAdvisePayNow = BigDecimal.ZERO;
								if(advise.getBounceID() > 0){
									if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_BOUNCE)){
										totAdvisePayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_BOUNCE);
										allocatePaidMap.remove(RepayConstants.ALLOCATION_BOUNCE);
									}
									allocatePaidMap.put(RepayConstants.ALLOCATION_BOUNCE, totAdvisePayNow.add(balAdvise));
								}else{
									if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID())){
										totAdvisePayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID());
										allocatePaidMap.remove(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID());
									}
									allocatePaidMap.put(RepayConstants.ALLOCATION_MANADV+"_"+advise.getAdviseID(), totAdvisePayNow.add(balAdvise));
								}
							}
						}
					}
				}
			}
		}
		
		logger.debug("Leaving");
		return allocatePaidMap;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

}
