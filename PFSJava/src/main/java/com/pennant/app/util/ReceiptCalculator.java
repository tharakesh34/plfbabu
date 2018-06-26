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
 * 26-04-2018		Vinay					 0.2     	As discussed with siva kumar 		*
 * 														In Suspense (NPA) case to
 * 														Allow different repay Heirarchy		* 
 *                                                                                          * 
 * 09-06-2018       Siva					 0.3        GST Rounding issues with Bounce on 
 * 														exclusive case on top  of GST       * 
 *                                                                                          * 
 * 13-06-2018       Siva					 0.4        Partial Settlement Amount 
 * 														Double Entry                        * 
 * 
 * 26-06-2018       Siva					 0.5        Early Settlement balance Amount 
 * 														not closing fully(127641)           * 
 * 
 * 26-06-2018       Siva					 0.6        Early Settlement balance Amount 
 * 														not closing fully(127641)           * 
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
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.rits.cloning.Cloner;

public class ReceiptCalculator implements Serializable {

	private static final long serialVersionUID = 8062681791631293126L;
	private static Logger logger = Logger.getLogger(ReceiptCalculator.class);

	private FinODDetailsDAO						finODDetailsDAO;
	private ManualAdviseDAO						manualAdviseDAO;
	private ReceiptService						receiptService;
	private FinanceProfitDetailDAO				financeProfitDetailDAO;
	private FinFeeDetailService 				finFeeDetailService;
	private RuleDAO								ruleDAO;
	private RuleExecutionUtil					ruleExecutionUtil;
	private FinanceTaxDetailDAO					financeTaxDetailDAO;
	private CustomerDetailsService				customerDetailsService;				

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

	public FinReceiptData initiateReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, Date valueDate,
			String receiptPurpose, boolean isPresentment) {
		return procInitiateReceipt(receiptData, scheduleData, valueDate, receiptPurpose, isPresentment);
	}

	/** To Calculate the Amounts for given schedule */
	private FinReceiptData procInitiateReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, Date valueDate,
			String receiptPurpose, boolean isPresentment) {
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
	 * Method for Initialize the data from Finance Details to Receipt Data to
	 * render Summary
	 * 
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 */
	private FinReceiptData initializeReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, Date valueDate,
			String receiptPurpose) {
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
	 * Method for calculating Schedule Total and Unpaid amounts based on
	 * Schedule Details
	 */
	private FinReceiptData calSummaryDetail(FinReceiptData receiptData, FinScheduleData finScheduleData, Date valueDate,
			String receiptPurpose) {
		logger.debug("Entering");

		BigDecimal priPaid = BigDecimal.ZERO;
		BigDecimal pftPaid = BigDecimal.ZERO;
		BigDecimal tdsAmount = BigDecimal.ZERO;
		BigDecimal cpzTillNow = BigDecimal.ZERO;
		Boolean isNextDueSet = false;
		boolean isSkipLastDateSet = false;

		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if (finScheduleData.getFinanceMain().isTDSApplicable()) {

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/

			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
						RoundingMode.HALF_DOWN);
			}
		}

		RepayMain repayMain = receiptData.getRepayMain();
		List<FinanceScheduleDetail> scheduleDetails = finScheduleData.getFinanceScheduleDetails();
		
		String roundingMode = finScheduleData.getFinanceMain().getCalRoundingMode();
		int roundingTarget = finScheduleData.getFinanceMain().getRoundingTarget();

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
			if (i != 0) {
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
			} else {

				if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)
						|| StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
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
			if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				if (DateUtility.compare(schdDate, valueDate) < 0) {
					pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitSchd());
					priBalance = priBalance.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));

					if (finScheduleData.getFinanceMain().isTDSApplicable()) {
						BigDecimal pft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
						BigDecimal actualPft = pft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
						
						/*actualPft = CalculationUtil.roundAmount(actualPft, roundingMode, roundingTarget);*/
						tdsAccruedTillNow = tdsAccruedTillNow.add(pft.subtract(actualPft));
					}

				} else if (DateUtility.compare(valueDate, schdDate) == 0) {

					BigDecimal remPft = curSchd.getProfitCalc().subtract(curSchd.getSchdPftPaid());
					//pftAccruedTillNow = pftAccruedTillNow.add(curSchd.getProfitCalc());
					if (prvSchd != null) {
						remPft = remPft.add(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount()));
					}
					
					int roundRequired = SysParamUtil.getValueAsInt(SMTParameterConstants.ROUND_LASTSCHD);
					if (roundRequired == 1 && prvSchd != null) {
						BigDecimal lastEMI = CalculationUtil.roundAmount(prvSchd.getClosingBalance().add(remPft),roundingMode, roundingTarget);
						remPft = lastEMI.subtract(prvSchd.getClosingBalance());
						if (remPft.compareTo(BigDecimal.ZERO) < 0) {
							remPft = BigDecimal.ZERO;
						}
					}
					
					if(prvSchd != null){
						pftAccruedTillNow = pftAccruedTillNow.add(remPft);
					}
					
					priBalance = priBalance.add(curSchd.getPrincipalSchd().add(curSchd.getClosingBalance()))
							.subtract(curSchd.getCpzAmount()).subtract(curSchd.getSchdPriPaid());

					if (StringUtils.equals(curSchd.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
						repayMain.setEarlyPayAmount(curSchd.getClosingBalance().subtract(curSchd.getCpzAmount())
								.add(curSchd.getProfitCalc()));
						if (prvSchd != null) {
							repayMain.setEarlyPayAmount(repayMain.getEarlyPayAmount().add(prvSchd.getProfitBalance()));
						}
					} else {
						repayMain.setEarlyPayAmount(curSchd.getClosingBalance().subtract(curSchd.getCpzAmount()));
					}

					if (finScheduleData.getFinanceMain().isTDSApplicable()) {
						BigDecimal actualPft = remPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
						/*actualPft = CalculationUtil.roundAmount(actualPft, roundingMode, roundingTarget);*/
						tdsAccruedTillNow = tdsAccruedTillNow.add(remPft.subtract(actualPft));

					}
					partAccrualReq = false;

				} else {
					if (partAccrualReq && prvSchd != null) {
						partAccrualReq = false;
						BigDecimal accruedPft = CalculationUtil.calInterest(prvSchd.getSchDate(), valueDate,
								curSchd.getBalanceForPftCal(), prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());
						accruedPft = accruedPft.add(prvSchd.getProfitFraction()).add(prvSchd.getProfitBalance()).subtract(prvSchd.getCpzAmount());
						accruedPft = CalculationUtil.roundAmount(accruedPft, finScheduleData.getFinanceMain().getCalRoundingMode(),
								finScheduleData.getFinanceMain().getRoundingTarget());
						if (accruedPft.compareTo(BigDecimal.ZERO) < 0) {
							accruedPft = BigDecimal.ZERO;
						}

						// Rounding Profit Adjustment on Last Term
						int roundRequired = SysParamUtil.getValueAsInt(SMTParameterConstants.ROUND_LASTSCHD);
						if (roundRequired == 1) {
							BigDecimal lastTermEMI = CalculationUtil.roundAmount(accruedPft.add(prvSchd.getClosingBalance()),
									finScheduleData.getFinanceMain().getCalRoundingMode(), finScheduleData.getFinanceMain().getRoundingTarget());
							BigDecimal roundingPftDiff = lastTermEMI.subtract(prvSchd.getClosingBalance()).subtract(accruedPft);
							accruedPft = accruedPft.add(roundingPftDiff);
						}

						pftAccruedTillNow = pftAccruedTillNow.add(accruedPft);
						priBalance = priBalance.add(prvSchd.getClosingBalance());
						repayMain.setEarlyPayAmount(prvSchd.getClosingBalance());
						
						if(finScheduleData.getFinanceMain().isTDSApplicable()){
							BigDecimal actualPft = accruedPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
							/*actualPft = CalculationUtil.roundAmount(actualPft, finScheduleData.getFinanceMain().getCalRoundingMode(), 
									finScheduleData.getFinanceMain().getRoundingTarget());*/
							tdsAccruedTillNow = tdsAccruedTillNow.add(accruedPft.subtract(actualPft));
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

			// Total Fee Amount
			repayMain.setTotalFeeAmt(repayMain.getTotalFeeAmt()
					.add(curSchd.getFeeSchd() == null ? BigDecimal.ZERO : curSchd.getFeeSchd()));

			// Overdue Principal and Profit
			if (DateUtility.compare(schdDate, valueDate) <= 0) {
				cpzTillNow = cpzTillNow.add(curSchd.getCpzAmount());

				if (!StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)
						|| curSchd.getPresentmentId() == 0) {

					repayMain.setOverduePrincipal(repayMain.getOverduePrincipal()
							.add(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())));

					repayMain.setOverdueProfit(repayMain.getOverdueProfit()
							.add(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())));

					// TDS Calculation
					BigDecimal unpaidPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
					BigDecimal actualPft = unpaidPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
					/*
					 * actualPft = CalculationUtil.roundAmount(actualPft,
					 * roundingMode,roundingTarget);
					 */
					tdsAmount = tdsAmount.add(unpaidPft.subtract(actualPft));
				}

				if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)
						&& curSchd.getPresentmentId() > 0) {
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

				// Set the last and next scheduled repayments to deferred dates
				// (Agreed with customer)
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
		receiptData.setAllocationMap(new HashMap<String, BigDecimal>());

		// Applicable only when Fees, Insurances & Manual Advises to maintain
		// descriptions in screen level
		if (receiptData.getAllocationDescMap() == null) {
			receiptData.setAllocationDescMap(new HashMap<String, String>());
		}

		// Principal Amount
		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PRI, priBalance);
		} else {
			if (repayMain.getOverduePrincipal().compareTo(BigDecimal.ZERO) > 0) {
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PRI, repayMain.getOverduePrincipal());
			}
		}

		// Profit Amount
		BigDecimal pftAmt = BigDecimal.ZERO;
		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			pftAmt = pftAccruedTillNow.subtract(pftPaid);
		} else {
			pftAmt = repayMain.getOverdueProfit();
		}

		// TDS calculation Process
		if (pftAmt.compareTo(BigDecimal.ZERO) > 0) {
			if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_TDS, tdsAccruedTillNow);
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_NPFT, pftAmt.subtract(tdsAccruedTillNow));
			} else {
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_TDS, tdsAmount);
				receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_NPFT, pftAmt.subtract(tdsAmount));
			}
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_PFT, pftAmt);
		}

		// Fetching Actual Late Payments based on Value date passing
		BigDecimal latePayPftBal = BigDecimal.ZERO;
		BigDecimal penaltyBal = BigDecimal.ZERO;
		if (DateUtility.compare(valueDate, DateUtility.getAppDate()) == 0) {
			latePayPftBal = finODDetailsDAO.getTotalODPftBal(repayMain.getFinReference(), presentmentDates);
			penaltyBal = finODDetailsDAO.getTotalPenaltyBal(repayMain.getFinReference(), presentmentDates);
		} else {

			Date reqMaxODDate = valueDate;
			if (!ImplementationConstants.LPP_CALC_SOD) {
				reqMaxODDate = DateUtility.addDays(valueDate, -1);
			}

			// Calculate overdue Penalties
			List<FinODDetails> overdueList = receiptService.getValueDatePenalties(finScheduleData,
					receiptData.getTotReceiptAmount(), reqMaxODDate, null, true);

			// Calculating Actual Sum of Penalty Amount & Late Pay Interest
			if (overdueList != null && !overdueList.isEmpty()) {
				for (int i = 0; i < overdueList.size(); i++) {
					FinODDetails finODDetail = overdueList.get(i);
					if (finODDetail.getFinODSchdDate().compareTo(reqMaxODDate) > 0) {
						continue;
					}

					// Not allowed Presentment/Freezing Period Schedule dates
					if (presentmentDates.contains(finODDetail.getFinODSchdDate())) {
						continue;
					}
					latePayPftBal = latePayPftBal.add(finODDetail.getLPIBal());
					penaltyBal = penaltyBal.add(finODDetail.getTotPenaltyBal());
				}
			}
		}

		// Fetch Late Pay Profit Details
		if (latePayPftBal.compareTo(BigDecimal.ZERO) > 0) {
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_LPFT, latePayPftBal);
		}

		// Fetch Sum of Overdue Charges
		if (penaltyBal.compareTo(BigDecimal.ZERO) > 0) {
			receiptData.setPendingODC(penaltyBal);
			receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_ODC, penaltyBal);
		}

		// Fee Details
		if (finScheduleData.getFinFeeDetailList() != null && !finScheduleData.getFinFeeDetailList().isEmpty()) {

			for (int i = 0; i < finScheduleData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = finScheduleData.getFinFeeDetailList().get(i);
				if (StringUtils.equals(feeDetail.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)) {
					continue;
				}

				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {

					// Calculate Overdue Fee Schedule Amount
					List<FinFeeScheduleDetail> feeSchdList = feeDetail.getFinFeeScheduleDetailList();
					BigDecimal pastFeeAmount = BigDecimal.ZERO;
					for (int j = 0; j < feeSchdList.size(); j++) {

						FinFeeScheduleDetail feeSchd = feeSchdList.get(j);
						if (DateUtility.compare(feeSchd.getSchDate(), valueDate) <= 0
								|| StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
							pastFeeAmount = pastFeeAmount.add(feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount()));
						} else {
							break;
						}
					}

					// Adding Fee Details to Map
					if (pastFeeAmount.compareTo(BigDecimal.ZERO) > 0) {
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_FEE + "_" + feeDetail.getFeeID(),
								pastFeeAmount);
						receiptData.getAllocationDescMap().put(
								RepayConstants.ALLOCATION_FEE + "_" + feeDetail.getFeeID(),
								StringUtils.isEmpty(feeDetail.getFeeTypeDesc()) ? feeDetail.getVasReference()
										: feeDetail.getFeeTypeDesc());
					}
				}
			}
		}

		// Insurance Details
		if (finScheduleData.getFinInsuranceList() != null && !finScheduleData.getFinInsuranceList().isEmpty()) {

			for (int i = 0; i < finScheduleData.getFinInsuranceList().size(); i++) {
				FinInsurances finInsurance = finScheduleData.getFinInsuranceList().get(i);

				if (StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {

					// Calculate Overdue Fee Schedule Amount
					List<FinSchFrqInsurance> insSchdList = finInsurance.getFinSchFrqInsurances();
					BigDecimal pastInsAmount = BigDecimal.ZERO;
					for (int j = 0; j < insSchdList.size(); j++) {

						FinSchFrqInsurance insSchd = insSchdList.get(j);
						if (DateUtility.compare(insSchd.getInsSchDate(), valueDate) <= 0
								|| StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
							pastInsAmount = pastInsAmount.add(insSchd.getAmount().subtract(insSchd.getInsurancePaid()));
						} else {
							break;
						}
					}

					// Adding Fee Details to Map
					if (pastInsAmount.compareTo(BigDecimal.ZERO) > 0) {
						receiptData.getAllocationMap()
								.put(RepayConstants.ALLOCATION_INS + "_" + finInsurance.getInsId(), pastInsAmount);
						receiptData.getAllocationDescMap().put(
								RepayConstants.ALLOCATION_INS + "_" + finInsurance.getInsId(),
								finInsurance.getInsuranceTypeDesc() + "-" + finInsurance.getInsReference());
					}
				}
			}
		}

		// Manual Advises
		List<ManualAdvise> adviseList = manualAdviseDAO.getManualAdviseByRef(repayMain.getFinReference(),
				FinanceConstants.MANUAL_ADVISE_RECEIVABLE, "_AView");
		if (adviseList != null && !adviseList.isEmpty()) {
			
			// Calculate total GST percentage
			Map<String, BigDecimal> taxPercmap = getTaxPercentages(receiptData.getFinanceDetail());
			
			BigDecimal cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
			BigDecimal sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
			BigDecimal ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
			BigDecimal igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);
			BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);
			String taxApplicable = SysParamUtil.getValueAsString("BOUNCE_TAX_APPLICABLE");
			
			for (ManualAdvise advise : adviseList) {
				BigDecimal adviseBal = advise.getAdviseAmount().subtract(advise.getPaidAmount())
						.subtract(advise.getWaivedAmount());
				// Adding Advise Details to Map
				if (adviseBal.compareTo(BigDecimal.ZERO) > 0) {
					String taxType = null;
					if (advise.getBounceID() > 0) {

						if (receiptData.getAllocationMap().containsKey(RepayConstants.ALLOCATION_BOUNCE)) {
							adviseBal = adviseBal
									.add(receiptData.getAllocationMap().get(RepayConstants.ALLOCATION_BOUNCE));
							receiptData.getAllocationMap().remove(RepayConstants.ALLOCATION_BOUNCE);
						}
						receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_BOUNCE, adviseBal);

						if (!receiptData.getAllocationDescMap().containsKey(RepayConstants.ALLOCATION_BOUNCE)) {
							receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_BOUNCE, "Bounce Charges");
						}
						
						if(!StringUtils.equals(taxApplicable, PennantConstants.YES)){
							continue;
						}
						
						taxType = SysParamUtil.getValueAsString("BOUNCE_TAX_TYPE");
					} else {
						receiptData.getAllocationMap()
								.put(RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID(), adviseBal);
						
						// Calculation Receivable Advises
						if(!advise.isTaxApplicable()){
							
							receiptData.getAllocationDescMap().put(
									RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID(), advise.getFeeTypeDesc());
							
							continue;
						}
						
						taxType = advise.getTaxComponent();
					}
						
					BigDecimal gstAmount = BigDecimal.ZERO;
					if(StringUtils.equals(advise.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)){

						if(cgstPerc.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal cgst =  (adviseBal.multiply(cgstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
							cgst = CalculationUtil.roundAmount(gstAmount, roundingMode,roundingTarget);
							gstAmount = gstAmount.add(cgst);
						}
						if(sgstPerc.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal sgst =  (adviseBal.multiply(sgstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
							sgst = CalculationUtil.roundAmount(gstAmount, roundingMode,roundingTarget);
							gstAmount = gstAmount.add(sgst);
						}
						if(ugstPerc.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal ugst =  (adviseBal.multiply(ugstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
							ugst = CalculationUtil.roundAmount(gstAmount, roundingMode,roundingTarget);
							gstAmount = gstAmount.add(ugst);
						}
						if(igstPerc.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal igst =  (adviseBal.multiply(igstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
							igst = CalculationUtil.roundAmount(gstAmount, roundingMode,roundingTarget);
							gstAmount = gstAmount.add(igst);
						}

					}else if(StringUtils.equals(advise.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)){

						BigDecimal percentage = (totalGSTPerc.add(new BigDecimal(100))).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
						BigDecimal actualAmt = adviseBal.divide(percentage, 9, RoundingMode.HALF_DOWN);
						actualAmt = CalculationUtil.roundAmount(actualAmt, roundingMode, roundingTarget);
						BigDecimal actTaxAmount = adviseBal.subtract(actualAmt);

						if(cgstPerc.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal cgst = (actTaxAmount.multiply(cgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
							cgst = CalculationUtil.roundAmount(cgst, roundingMode, roundingTarget);
							gstAmount = gstAmount.add(cgst);
						}
						if(sgstPerc.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal sgst = (actTaxAmount.multiply(sgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
							sgst = CalculationUtil.roundAmount(sgst, roundingMode, roundingTarget);
							gstAmount = gstAmount.add(sgst);
						}
						if(ugstPerc.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal ugst = (actTaxAmount.multiply(ugstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
							ugst = CalculationUtil.roundAmount(ugst, roundingMode, roundingTarget);
							gstAmount = gstAmount.add(ugst);
						}
						if(igstPerc.compareTo(BigDecimal.ZERO) > 0){
							BigDecimal igst = (actTaxAmount.multiply(igstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
							igst = CalculationUtil.roundAmount(igst, roundingMode, roundingTarget);
							gstAmount = gstAmount.add(igst);
						}
					}
						
					if(StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)){

						if (advise.getBounceID() > 0) {
							BigDecimal totalGST = BigDecimal.ZERO;
							if(receiptData.getAllocationMap().containsKey(RepayConstants.ALLOCATION_BOUNCE +"_GST_E")){
								totalGST = receiptData.getAllocationMap().get(RepayConstants.ALLOCATION_BOUNCE +"_GST_E");
							}
							receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_BOUNCE +"_GST_E", totalGST.add(gstAmount));
							receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_BOUNCE, "Bounce Charges (Exclusive)");
						}else{
							receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID()+"_GST_E", gstAmount);
							receiptData.getAllocationDescMap().put(
									RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID(), advise.getFeeTypeDesc() +" (Exclusive)");
						}
					}else if(StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)){

						if (advise.getBounceID() > 0) {
							
							BigDecimal totalGST = BigDecimal.ZERO;
							if(receiptData.getAllocationMap().containsKey(RepayConstants.ALLOCATION_BOUNCE +"_GST_I")){
								totalGST = receiptData.getAllocationMap().get(RepayConstants.ALLOCATION_BOUNCE +"_GST_I");
							}
							
							receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_BOUNCE +"_GST_I", totalGST.add(gstAmount));
							receiptData.getAllocationDescMap().put(RepayConstants.ALLOCATION_BOUNCE, "Bounce Charges (Inclusive)");
						}else{
							receiptData.getAllocationMap().put(RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID()+"_GST_I", gstAmount);
							receiptData.getAllocationDescMap().put(
									RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID(), advise.getFeeTypeDesc() +" (Inclusive)");
						}
					}
				}
			}
		}

		logger.debug("Leaving");
		return receiptData;
	}

	/**
	 * Method for Calculation of Schedule payment based on Allocated Details
	 * from Receipts
	 * 
	 * @param receiptData
	 * @param scheduleData
	 * @return
	 */
	private FinReceiptData recalReceipt(FinReceiptData receiptData, FinScheduleData scheduleData, Date valueDate,
			String receiptPurpose, boolean isPresentment) {
		logger.debug("Entering");

		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetails = scheduleData.getFinanceScheduleDetails();
		List<FinReceiptDetail> receiptDetailList = receiptData.getReceiptHeader().getReceiptDetails();

		// Rendering
		if (receiptDetailList == null || receiptDetailList.isEmpty()) {
			return null;
		}

		// Fetch total Advise details
		Map<Long, ManualAdvise> adviseMap = new HashMap<Long, ManualAdvise>();
		List<ManualAdvise> advises = manualAdviseDAO.getManualAdviseByRef(financeMain.getFinReference(),
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
		if (DateUtility.compare(valueDate, DateUtility.getAppDate()) == 0) {
			overdueList = finODDetailsDAO.getFinODBalByFinRef(financeMain.getFinReference());
		} else {
			// Calculate overdue Penalties
			Date reqMaxODDate = valueDate;
			if (!ImplementationConstants.LPP_CALC_SOD) {
				reqMaxODDate = DateUtility.addDays(valueDate, -1);
			}
			overdueList = receiptService.getValueDatePenalties(scheduleData, receiptData.getReceiptHeader()
					.getReceiptAmount().subtract(receiptData.getReceiptHeader().getTotFeeAmount()), reqMaxODDate, null,
					true);
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
		Map<String, BigDecimal> paidGSTMap = new HashMap<String, BigDecimal>();
		BigDecimal totalWaivedAmt = BigDecimal.ZERO;
		BigDecimal totalSchdWaivedAmt = BigDecimal.ZERO;
		List<ReceiptAllocationDetail> allocationList = receiptData.getReceiptHeader().getAllocations();
		if (allocationList != null && !allocationList.isEmpty()) {
			for (int a = 0; a < allocationList.size(); a++) {
				ReceiptAllocationDetail allocate = allocationList.get(a);
				if (allocate.getAllocationTo() == 0 || allocate.getAllocationTo() == Long.MIN_VALUE ||
						StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE)) {

					paidAllocationMap.put(allocate.getAllocationType(), allocate.getPaidAmount());
					waivedAllocationMap.put(allocate.getAllocationType(), allocate.getWaivedAmount());
				} else {

					paidAllocationMap.put(allocate.getAllocationType() + "_" + allocate.getAllocationTo(),
							allocate.getPaidAmount());
					BigDecimal waivedAmount = BigDecimal.ZERO;
					if (waivedAllocationMap
							.containsKey(allocate.getAllocationType() + "_" + allocate.getAllocationTo())) {
						waivedAmount = waivedAllocationMap.get(allocate.getAllocationType());
					}
					waivedAllocationMap.put(allocate.getAllocationType() + "_" + allocate.getAllocationTo(),
							waivedAmount.add(allocate.getWaivedAmount()));
				}
				
				if(allocate.getPaidGST().compareTo(BigDecimal.ZERO) > 0){
					if(allocate.getAllocationTo() > 0){
						paidGSTMap.put(allocate.getAllocationType() + "_" + allocate.getAllocationTo(),allocate.getPaidGST());
					}else{
						paidGSTMap.put(allocate.getAllocationType() ,allocate.getPaidGST());
					}
				}

				if (!(StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_TDS)
						|| StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_NPFT))) {
					totalWaivedAmt = totalWaivedAmt.add(allocate.getWaivedAmount());

					if (!(StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_MANADV)
							|| StringUtils.equals(allocate.getAllocationType(), RepayConstants.ALLOCATION_BOUNCE))) {
						totalSchdWaivedAmt = totalSchdWaivedAmt.add(allocate.getWaivedAmount());
					}
				}
			}

			// Setting Total Waived amount to Header details for Information
			// Only
			receiptData.getReceiptHeader().setWaviedAmt(totalWaivedAmt);
		}

		List<FinRepayHeader> repayHeaderList = null;
		Cloner cloner = new Cloner();
		List<FinanceScheduleDetail> tempScheduleDetails = cloner.deepClone(scheduleDetails);
		tempScheduleDetails = sortSchdDetails(tempScheduleDetails);

		// TDS Calculation, if Applicable
		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if (financeMain.isTDSApplicable()) {

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/
			
			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
						RoundingMode.HALF_DOWN);
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
		BigDecimal totalReceiptAmt = BigDecimal.ZERO;
		BigDecimal actualReceiptAmt = BigDecimal.ZERO;

		// Start Receipt Details Rendering Process using allocation Details
		for (int i = 0; i < receiptDetailList.size(); i++) {

			FinReceiptDetail receiptDetail = receiptDetailList.get(i);

			// Internal temporary Record can't be processed for Calculation
			if (receiptDetail.isDelRecord()) {
				continue;
			}

			boolean isSchdPaid = true;
			if(StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				totalReceiptAmt = totalReceiptAmt.add(receiptDetail.getAmount());
				actualReceiptAmt = actualReceiptAmt.add(receiptDetail.getAmount());
			}else{
				totalReceiptAmt = receiptDetail.getAmount();
				actualReceiptAmt = receiptDetail.getAmount();
				isSchdPaid = false;
			}
			if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)) {
				if (eventFeeBal.compareTo(BigDecimal.ZERO) > 0) {
					if (eventFeeBal.compareTo(totalReceiptAmt) > 0) {
						eventFeeBal = eventFeeBal.subtract(totalReceiptAmt);
						totalReceiptAmt = BigDecimal.ZERO;
					} else {
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
			// Making Waived amount to total Receipts to set payment on Schedule
			// cleared
			if (i == receiptDetailList.size() - 1) {
				// totalReceiptAmt = totalReceiptAmt.add(totalWaivedAmt);
			}

			List<RepayScheduleDetail> partialRpySchdList = new ArrayList<>();
			String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy();
			// ###_0.2 
			boolean isSusp = financeProfitDetailDAO.isSuspenseFinance(financeMain.getFinReference());
			if(isSusp){
				String allDiffRepayNPA = SysParamUtil.getValueAsString(SMTParameterConstants.ALW_DIFF_RPYHCY_NPA);
				if (StringUtils.equals(PennantConstants.YES, allDiffRepayNPA)) {
					repayHierarchy = SysParamUtil.getValueAsString(SMTParameterConstants.RPYHCY_ON_NPA);
				}
			}
			char[] rpyOrder = repayHierarchy.replace("CS", "C").toCharArray();
			int lastRenderSeq = 0;

			if ((StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE) && i == 0)
					|| !StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
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
				RepayScheduleDetail partRsd = null;

				if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)
						&& curSchd.getPresentmentId() > 0 && !isPresentment) {
					continue;
				}

				// Skip if repayment date after Current Business date
				if (schdDate.compareTo(valueDate) > 0
						&& !StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
					break;
				}

				// If Presentment Process, only Presentment Date schedule should
				// be effected.
				if (isPresentment && (DateUtility.compare(valueDate, schdDate) != 0)) {
					continue;
				}

				// Find out early payment/ partial Settlement schedule term and
				// amount
				if ((schdDate.compareTo(valueDate) == 0
						&& StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY))
						|| (schdDate.compareTo(valueDate) >= 0
								&& StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE))) {

					// Manual Advises
					if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {

						if (!adviseMap.isEmpty()) {
							List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
							Collections.sort(adviseIdList);
							
							// Calculate total GST percentage
							Map<String, BigDecimal> taxPercmap  = null;
							String taxApplicable = SysParamUtil.getValueAsString("BOUNCE_TAX_APPLICABLE");
							BigDecimal totalBounceTaxAmount = BigDecimal.ZERO;
							if(paidGSTMap.containsKey(RepayConstants.ALLOCATION_BOUNCE)){
								totalBounceTaxAmount = paidGSTMap.get(RepayConstants.ALLOCATION_BOUNCE);
							}
							
							// Calculate TAX Amounts against each TAX Type
							BigDecimal totalGSTPerc = BigDecimal.ZERO;
							BigDecimal cgstPerc = BigDecimal.ZERO;
							BigDecimal sgstPerc = BigDecimal.ZERO;
							BigDecimal ugstPerc = BigDecimal.ZERO;
							BigDecimal igstPerc = BigDecimal.ZERO;
							
							for (int a = 0; a < adviseIdList.size(); a++) {

								ManualAdvise advise = adviseMap.get(adviseIdList.get(a));
								
								if((advise.isTaxApplicable() || StringUtils.equals(taxApplicable, PennantConstants.YES)) && taxPercmap == null){
									taxPercmap = getTaxPercentages(receiptData.getFinanceDetail());
									
									cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
									sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
									ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
									igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);
									totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);
								}
								
								if (advise != null) {

									String allocateType = "";
									boolean addGSTAmount = false;
									if (advise.getBounceID() > 0) {
										allocateType = RepayConstants.ALLOCATION_BOUNCE;
										
										if(StringUtils.equals(taxApplicable, PennantConstants.YES)){
											String taxType = SysParamUtil.getValueAsString("BOUNCE_TAX_TYPE");
											if(StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)){
												addGSTAmount = true;
											}
										}
										
									} else {
										allocateType = RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID();
										if(advise.isTaxApplicable() &&
												StringUtils.equals(advise.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)){
											addGSTAmount = true;
										}
									}

									if (paidAllocationMap.containsKey(allocateType)) {
										BigDecimal advAllocateBal = paidAllocationMap.get(allocateType);
										if (advAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
											BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount())
													.subtract(advise.getWaivedAmount());
											
											// In case of GST is Exclusive then GST amount should add before payment collection
											BigDecimal gstAmount = BigDecimal.ZERO;
											if(addGSTAmount){
												if(cgstPerc.compareTo(BigDecimal.ZERO) > 0){
													BigDecimal cgst =  (balAdvise.multiply(cgstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
													cgst = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
													gstAmount = gstAmount.add(cgst);
												}
												if(sgstPerc.compareTo(BigDecimal.ZERO) > 0){
													BigDecimal sgst =  (balAdvise.multiply(sgstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
													sgst = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
													gstAmount = gstAmount.add(sgst);
												}
												if(ugstPerc.compareTo(BigDecimal.ZERO) > 0){
													BigDecimal ugst =  (balAdvise.multiply(ugstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
													ugst = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
													gstAmount = gstAmount.add(ugst);
												}
												if(igstPerc.compareTo(BigDecimal.ZERO) > 0){
													BigDecimal igst =  (balAdvise.multiply(igstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
													igst = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
													gstAmount = gstAmount.add(igst);
												}
												
												balAdvise = balAdvise.add(gstAmount);
											}
											
											if (balAdvise.compareTo(BigDecimal.ZERO) > 0) {
												if (totalReceiptAmt.compareTo(advAllocateBal) >= 0
														&& advAllocateBal.compareTo(balAdvise) < 0) {
													balAdvise = advAllocateBal;
												} else if (totalReceiptAmt.compareTo(advAllocateBal) < 0
														&& balAdvise.compareTo(totalReceiptAmt) > 0) {
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
												movement.setWaivedAmount(BigDecimal.ZERO);
												movement.setFeeTypeCode(advise.getFeeTypeCode());
												
												// Total Paid/Adjusted GST amount
												BigDecimal totalTaxAmount = BigDecimal.ZERO;
												BigDecimal actTaxAmount = BigDecimal.ZERO;
												if(advise.getBounceID() > 0){
													totalTaxAmount = totalBounceTaxAmount;
															
													BigDecimal percentage = (totalGSTPerc.add(new BigDecimal(100))).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
													BigDecimal actualAmt = balAdvise.divide(percentage, 9, RoundingMode.HALF_DOWN);
													actualAmt = CalculationUtil.roundAmount(actualAmt, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
													actTaxAmount = balAdvise.subtract(actualAmt);
													
												}else{
													if(paidGSTMap.containsKey(allocateType)){
														totalTaxAmount = paidGSTMap.get(allocateType);
														actTaxAmount = paidGSTMap.get(allocateType);
													}
												}
												
												if ((advise.getBounceID() > 0 && StringUtils.equals(taxApplicable, PennantConstants.YES)) ||
														advise.getBounceID() == 0 && advise.isTaxApplicable()) {

													if(totalGSTPerc.compareTo(BigDecimal.ZERO) > 0){

														String roundingMode = financeMain.getCalRoundingMode();
														int roundingtarget = financeMain.getRoundingTarget();													

														// CGST Calculation
														if(taxPercmap.containsKey(RuleConstants.CODE_CGST)){
															BigDecimal taxPerc = taxPercmap.get(RuleConstants.CODE_CGST);	
															BigDecimal taxAmount = calculateTaxAmount(actTaxAmount, totalGSTPerc, taxPerc, roundingMode,roundingtarget);
															if(taxAmount.compareTo(totalTaxAmount) > 0){
																taxAmount = totalTaxAmount;
															}
															movement.setPaidCGST(taxAmount);
															totalTaxAmount = totalTaxAmount.subtract(taxAmount);
														}

														// SGST Calculation
														if(taxPercmap.containsKey(RuleConstants.CODE_SGST)){
															BigDecimal taxPerc = taxPercmap.get(RuleConstants.CODE_SGST);	
															BigDecimal taxAmount = calculateTaxAmount(actTaxAmount, totalGSTPerc,taxPerc, roundingMode,roundingtarget);
															if(taxAmount.compareTo(totalTaxAmount) > 0){
																taxAmount = totalTaxAmount;
															}
															movement.setPaidSGST(taxAmount);
															totalTaxAmount = totalTaxAmount.subtract(taxAmount);
														}

														// IGST Calculation
														if(taxPercmap.containsKey(RuleConstants.CODE_IGST)){
															BigDecimal taxPerc = taxPercmap.get(RuleConstants.CODE_IGST);	
															BigDecimal taxAmount = calculateTaxAmount(actTaxAmount, totalGSTPerc,taxPerc, roundingMode,roundingtarget);
															if(taxAmount.compareTo(totalTaxAmount) > 0){
																taxAmount = totalTaxAmount;
															}
															movement.setPaidIGST(taxAmount);
															totalTaxAmount = totalTaxAmount.subtract(taxAmount);
														}

														// UGST Calculation
														if(taxPercmap.containsKey(RuleConstants.CODE_UGST)){
															BigDecimal taxPerc = taxPercmap.get(RuleConstants.CODE_UGST);	
															BigDecimal taxAmount = calculateTaxAmount(actTaxAmount,totalGSTPerc, taxPerc, roundingMode,roundingtarget);
															if(taxAmount.compareTo(totalTaxAmount) > 0){
																taxAmount = totalTaxAmount;
															}
															movement.setPaidUGST(taxAmount);
															totalTaxAmount = totalTaxAmount.subtract(taxAmount);
														}
														// Reset remaining tax amount only in case of Bounce
														if(advise.getBounceID() > 0){
															totalBounceTaxAmount = totalTaxAmount;
														}
													}
												}
												
												// In case of Exclusive
												if(addGSTAmount){
													movement.setPaidAmount(balAdvise.subtract(movement.getPaidCGST()).subtract(
															movement.getPaidIGST()).subtract(movement.getPaidSGST()).subtract(movement.getPaidUGST()));
												}else{
													movement.setPaidAmount(balAdvise);
												}
												
												receiptDetail.getAdvMovements().add(movement);
											}
										}
									}
								}
							}
						}

						// Event Action Fee Deduction from
						if (eventFeeBal.compareTo(BigDecimal.ZERO) > 0) {
							if (eventFeeBal.compareTo(totalReceiptAmt) > 0) {
								eventFeeBal = eventFeeBal.subtract(totalReceiptAmt);
								totalReceiptAmt = BigDecimal.ZERO;
							} else {
								totalReceiptAmt = totalReceiptAmt.subtract(eventFeeBal);
								actualReceiptAmt = actualReceiptAmt.subtract(eventFeeBal);
								eventFeeBal = BigDecimal.ZERO;
							}
						}
					}

					// Only For Partial Settlement
					if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
						isPartialPayNow = true;
						//partialSettleAmount = curSchd.getPartialPaidAmt().subtract(curSchd.getSchdPriPaid());
						
						if(curSchd.getRepayAmount().compareTo(curSchd.getPartialPaidAmt()) == 0){
							partialSettleAmount = curSchd.getPartialPaidAmt();
						}else if(curSchd.getRepayAmount().compareTo(curSchd.getPartialPaidAmt()) > 0){
							if(curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) == 0){
								partialSettleAmount = curSchd.getPartialPaidAmt();
							}else if(curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0){
								BigDecimal priBal = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
								if(priBal.compareTo(curSchd.getPartialPaidAmt()) >= 0){
									partialSettleAmount = curSchd.getPartialPaidAmt();
								}else if(priBal.compareTo(curSchd.getPartialPaidAmt()) < 0){
									partialSettleAmount = priBal;
								}
							}
						}
					}
				}

				for (int j = 0; j < rpyOrder.length; j++) {

					char repayTo = rpyOrder[j];
					if (repayTo == RepayConstants.REPAY_PRINCIPAL) {

						if (paidAllocationMap.containsKey(RepayConstants.ALLOCATION_PRI)) {
							BigDecimal priAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_PRI);
							if (priAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
								BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
								if (balPri.compareTo(BigDecimal.ZERO) > 0) {
									if (totalReceiptAmt.compareTo(priAllocateBal) >= 0
											&& priAllocateBal.compareTo(balPri) < 0) {
										balPri = priAllocateBal;
									} else if (totalReceiptAmt.compareTo(priAllocateBal) < 0
											&& balPri.compareTo(totalReceiptAmt) > 0) {
										balPri = totalReceiptAmt;
									}

									// Reset Total Receipt Amount
									totalReceiptAmt = totalReceiptAmt.subtract(balPri);
									if (!isPartialPayNow) {
										totPriPaidNow = totPriPaidNow.add(balPri);
										rsd = prepareRpyPaidRecord(curSchd, rsd, repayTo, balPri, valueDate, null);
									} else {
										if (balPri.compareTo(partialSettleAmount) > 0) {
											totPriPaidNow = totPriPaidNow.add(balPri.subtract(partialSettleAmount));
											rsd = prepareRpyPaidRecord(curSchd, rsd, repayTo,
													balPri.subtract(partialSettleAmount), valueDate, null);
										}
										if (balPri.compareTo(partialSettleAmount) < 0) {
											partRsd = prepareRpyPaidRecord(curSchd, partRsd, repayTo, balPri, valueDate,
													null);
											partialSettleAmount = balPri;
										} else {
											partRsd = prepareRpyPaidRecord(curSchd, partRsd, repayTo,
													partialSettleAmount, valueDate, null);
										}
									}
									
									paidAllocationMap.put(RepayConstants.ALLOCATION_PRI,
											priAllocateBal.subtract(balPri));

									// Update Schedule to avoid on Next loop
									// Payment
									curSchd.setSchdPriPaid(curSchd.getSchdPriPaid().add(balPri));
									isSchdPaid = true;
								}
							}
						}
					} else if (repayTo == RepayConstants.REPAY_PROFIT) {

						String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
						char[] pftPayOrder = profit.toCharArray();
						for (char pftPayTo : pftPayOrder) {
							if (pftPayTo == RepayConstants.REPAY_PROFIT) {

								if (paidAllocationMap.containsKey(RepayConstants.ALLOCATION_PFT)) {
									BigDecimal pftAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_PFT);
									if (pftAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
										BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
										if (balPft.compareTo(BigDecimal.ZERO) > 0) {

											if (pftAllocateBal.compareTo(balPft) < 0) {
												balPft = pftAllocateBal;
											}

											BigDecimal actPftAdjust = balPft.divide(tdsMultiplier, 0,
													RoundingMode.HALF_DOWN);
											if (totalReceiptAmt.compareTo(pftAllocateBal) < 0
													&& actPftAdjust.compareTo(pftAllocateBal) >= 0) {
												if (totalReceiptAmt.compareTo(actPftAdjust) >= 0) {
													balPft = pftAllocateBal;
												} else {
													balPft = totalReceiptAmt.multiply(tdsMultiplier);
												}
											} else if (totalReceiptAmt.compareTo(pftAllocateBal) < 0
													&& totalReceiptAmt.compareTo(actPftAdjust) < 0) {
												balPft = totalReceiptAmt.multiply(tdsMultiplier);
												actPftAdjust = totalReceiptAmt;
											}

											// TEMP FIX
											if ((s == scheduleDetails.size() - 1)
													&& waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_PFT)) {
												BigDecimal diff = curSchd.getProfitSchd()
														.subtract(curSchd.getSchdPftPaid()).subtract(balPft).subtract(
																waivedAllocationMap.get(RepayConstants.ALLOCATION_PFT));
												if (diff.compareTo(BigDecimal.ONE) == 0) {
													balPft = balPft.add(diff);
												}

												if (paidAllocationMap.containsKey(RepayConstants.ALLOCATION_NPFT)
														&& actPftAdjust.compareTo(paidAllocationMap
																.get(RepayConstants.ALLOCATION_NPFT)) != 0) {
													BigDecimal diffNPFT = paidAllocationMap
															.get(RepayConstants.ALLOCATION_NPFT).subtract(actPftAdjust);
													boolean isNegative = false;
													if (diffNPFT.compareTo(BigDecimal.ZERO) < 0) {
														diffNPFT = diffNPFT.negate();
														isNegative = true;
													}
													if (diffNPFT.compareTo(BigDecimal.ONE) == 0) {
														if (isNegative) {
															actPftAdjust = actPftAdjust.subtract(diffNPFT);
														} else {
															actPftAdjust = actPftAdjust.add(diffNPFT);
														}
													}
												}
											}

											rsd = prepareRpyPaidRecord(curSchd, rsd, pftPayTo, balPft, valueDate, null);

											// TDS Payments
											BigDecimal tdsAdjust = balPft.subtract(actPftAdjust);
											if (tdsAdjust.compareTo(BigDecimal.ZERO) > 0) {
												rsd = prepareRpyPaidRecord(curSchd, rsd, RepayConstants.REPAY_TDS,
														tdsAdjust, valueDate, null);

												if (paidAllocationMap.containsKey(RepayConstants.ALLOCATION_TDS)) {
													BigDecimal totTDSPayNow = paidAllocationMap
															.get(RepayConstants.ALLOCATION_TDS);
													paidAllocationMap.remove(RepayConstants.ALLOCATION_TDS);
													paidAllocationMap.put(RepayConstants.ALLOCATION_TDS,
															totTDSPayNow.subtract(tdsAdjust)
																	.compareTo(BigDecimal.ZERO) >= 0
																			? totTDSPayNow.subtract(tdsAdjust)
																			: BigDecimal.ZERO);
												}
											}

											// Reset Total Receipt Amount
											totalReceiptAmt = totalReceiptAmt.subtract(actPftAdjust);
											totPftPaidNow = totPftPaidNow.add(balPft);
											paidAllocationMap.put(RepayConstants.ALLOCATION_PFT,
													pftAllocateBal.subtract(balPft).compareTo(BigDecimal.ZERO) >= 0
															? pftAllocateBal.subtract(balPft) : BigDecimal.ZERO);

											if (paidAllocationMap.containsKey(RepayConstants.ALLOCATION_NPFT)) {
												paidAllocationMap.put(RepayConstants.ALLOCATION_NPFT,
														paidAllocationMap.get(RepayConstants.ALLOCATION_NPFT)
																.subtract(actPftAdjust).compareTo(BigDecimal.ZERO) >= 0
																		? paidAllocationMap
																				.get(RepayConstants.ALLOCATION_NPFT)
																				.subtract(actPftAdjust)
																		: BigDecimal.ZERO);
											}

											// Update Schedule to avoid on Next
											// loop Payment
											curSchd.setSchdPftPaid(curSchd.getSchdPftPaid().add(balPft));
											isSchdPaid = true;
										}
									}
								}

							} else if (pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT) {

								if (paidAllocationMap.containsKey(RepayConstants.ALLOCATION_LPFT)) {
									BigDecimal latePftAllocateBal = paidAllocationMap
											.get(RepayConstants.ALLOCATION_LPFT);
									if (latePftAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
										FinODDetails overdue = overdueMap.get(schdDate);
										if (overdue != null) {
											BigDecimal balLatePft = overdue.getLPIBal();
											if (balLatePft.compareTo(BigDecimal.ZERO) > 0) {
												if (totalReceiptAmt.compareTo(latePftAllocateBal) >= 0
														&& latePftAllocateBal.compareTo(balLatePft) < 0) {
													balLatePft = latePftAllocateBal;
												} else if (totalReceiptAmt.compareTo(latePftAllocateBal) < 0
														&& balLatePft.compareTo(totalReceiptAmt) > 0) {
													balLatePft = totalReceiptAmt;
												}
												rsd = prepareRpyPaidRecord(curSchd, rsd, pftPayTo, balLatePft,
														valueDate, null);

												// Reset Total Receipt Amount
												totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
												totLPftPaidNow = totLPftPaidNow.add(balLatePft);
												paidAllocationMap.put(RepayConstants.ALLOCATION_LPFT,
														latePftAllocateBal.subtract(balLatePft));

												// Update Schedule to avoid on
												// Next loop Payment
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

					} else if (repayTo == RepayConstants.REPAY_PENALTY) {

						if (paidAllocationMap.containsKey(RepayConstants.ALLOCATION_ODC)) {
							BigDecimal penaltyAllocateBal = paidAllocationMap.get(RepayConstants.ALLOCATION_ODC);
							if (penaltyAllocateBal.compareTo(BigDecimal.ZERO) >= 0) {
								FinODDetails overdue = overdueMap.get(schdDate);
								if (overdue != null) {
									BigDecimal balPenalty = overdue.getTotPenaltyBal();
									if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
										if (totalReceiptAmt.compareTo(penaltyAllocateBal) >= 0
												&& penaltyAllocateBal.compareTo(balPenalty) < 0) {
											balPenalty = penaltyAllocateBal;
										} else if (totalReceiptAmt.compareTo(penaltyAllocateBal) < 0
												&& balPenalty.compareTo(totalReceiptAmt) > 0) {
											balPenalty = totalReceiptAmt;
										}
										rsd = prepareRpyPaidRecord(curSchd, rsd, repayTo, balPenalty, valueDate,
												overdue.getTotPenaltyBal());

										// Reset Total Receipt Amount
										totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
										totPenaltyPaidNow = totPenaltyPaidNow.add(balPenalty);

										paidAllocationMap.put(RepayConstants.ALLOCATION_ODC,
												penaltyAllocateBal.subtract(balPenalty));

										// Update Schedule to avoid on Next loop
										// Payment
										overdue.setTotPenaltyBal(overdue.getTotPenaltyBal().subtract(balPenalty));
										overdueMap.remove(schdDate);
										overdueMap.put(schdDate, overdue);
										isSchdPaid = true;
									}
								}
							}
						}

					} else if (repayTo == RepayConstants.REPAY_OTHERS) {

						// If Schedule has Unpaid Fee Amount
						if (curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()).compareTo(BigDecimal.ZERO) > 0) {

							// Fee Detail Collection
							for (int k = 0; k < scheduleData.getFinFeeDetailList().size(); k++) {
								FinFeeDetail feeSchd = scheduleData.getFinFeeDetailList().get(k);
								if (StringUtils.equals(feeSchd.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)) {
									continue;
								}
								List<FinFeeScheduleDetail> feeSchdList = feeSchd.getFinFeeScheduleDetailList();

								// No more Receipt amount left for next
								// schedules
								if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
									totalReceiptAmt = BigDecimal.ZERO;
									break;
								}

								if (feeSchdList == null || feeSchdList.isEmpty()) {
									continue;
								}

								// If Schedule Fee terms are less than actual
								// calculated Sequence
								if (feeSchdList.size() < lastRenderSeq) {
									continue;
								}

								// Calculate and set Fee Amount based on Fee ID
								for (int l = lastRenderSeq; l < feeSchdList.size(); l++) {
									if (feeSchdList.get(l).getSchDate().compareTo(schdDate) == 0) {

										if (paidAllocationMap.containsKey(
												RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID())) {
											BigDecimal feeAllocateBal = paidAllocationMap
													.get(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID());
											if (feeAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
												BigDecimal balFee = feeSchdList.get(l).getSchAmount()
														.subtract(feeSchdList.get(l).getPaidAmount());
												if (balFee.compareTo(BigDecimal.ZERO) > 0) {
													if (totalReceiptAmt.compareTo(feeAllocateBal) >= 0
															&& feeAllocateBal.compareTo(balFee) < 0) {
														balFee = feeAllocateBal;
													} else if (totalReceiptAmt.compareTo(feeAllocateBal) < 0
															&& balFee.compareTo(totalReceiptAmt) > 0) {
														balFee = totalReceiptAmt;
													}
													rsd = prepareRpyPaidRecord(curSchd, rsd, RepayConstants.REPAY_FEE,
															balFee, valueDate, null);

													// Reset Total Receipt
													// Amount
													totalReceiptAmt = totalReceiptAmt.subtract(balFee);
													totFeePaidNow = totFeePaidNow.add(balFee);

													paidAllocationMap.put(
															RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID(),
															feeAllocateBal.subtract(balFee));

													// Update Schedule to avoid
													// on Next loop Payment
													feeSchdList.get(l).setPaidAmount(
															feeSchdList.get(l).getPaidAmount().add(balFee));
													isSchdPaid = true;
												}
											}
										}
										if (lastRenderSeq == 0) {
											lastRenderSeq = l;
										}
										break;
									}
								}
							}
						}

						// If Schedule has Unpaid Insurance Amount
						if ((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0) {

							// Insurance Details Collection
							for (int k = 0; k < scheduleData.getFinInsuranceList().size(); k++) {
								FinInsurances insSchd = scheduleData.getFinInsuranceList().get(k);
								List<FinSchFrqInsurance> insSchdList = insSchd.getFinSchFrqInsurances();

								// No more Receipt amount left for next
								// schedules
								if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
									totalReceiptAmt = BigDecimal.ZERO;
									break;
								}

								if (insSchdList == null || insSchdList.isEmpty()) {
									continue;
								}

								// Calculate and set Fee Amount based on Fee ID
								for (int l = 0; l < insSchdList.size(); l++) {
									if (insSchdList.get(l).getInsSchDate().compareTo(schdDate) == 0) {

										if (paidAllocationMap.containsKey(
												RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId())) {
											BigDecimal insAllocateBal = paidAllocationMap
													.get(RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId());
											if (insAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
												BigDecimal balIns = insSchdList.get(l).getAmount()
														.subtract(insSchdList.get(l).getInsurancePaid());
												if (balIns.compareTo(BigDecimal.ZERO) > 0) {
													if (totalReceiptAmt.compareTo(insAllocateBal) >= 0
															&& insAllocateBal.compareTo(balIns) < 0) {
														balIns = insAllocateBal;
													} else if (totalReceiptAmt.compareTo(insAllocateBal) < 0
															&& balIns.compareTo(totalReceiptAmt) > 0) {
														balIns = totalReceiptAmt;
													}
													rsd = prepareRpyPaidRecord(curSchd, rsd, RepayConstants.REPAY_INS,
															balIns, valueDate, null);

													// Reset Total Receipt
													// Amount
													totalReceiptAmt = totalReceiptAmt.subtract(balIns);
													totInsPaidNow = totInsPaidNow.add(balIns);

													paidAllocationMap.put(
															RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId(),
															insAllocateBal.subtract(balIns));

													// Update Schedule to avoid
													// on Next loop Payment
													insSchdList.get(l).setInsurancePaid(
															insSchdList.get(l).getInsurancePaid().add(balIns));
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
				if (rsd != null || partRsd != null) {
					if (isPartialPayNow) {
						partialRpySchdList.add(partRsd);
						if (rsd != null) {
							pastdueRpySchdList.add(rsd);
						}
					} else {
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

				if (!adviseMap.isEmpty()) {
					List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
					Collections.sort(adviseIdList);
					
					// Calculate total GST percentage
					Map<String, BigDecimal> taxPercmap  = null;
					String taxApplicable = SysParamUtil.getValueAsString("BOUNCE_TAX_APPLICABLE");
					BigDecimal totalBounceTaxAmount = BigDecimal.ZERO;
					if(paidGSTMap.containsKey(RepayConstants.ALLOCATION_BOUNCE)){
						totalBounceTaxAmount = paidGSTMap.get(RepayConstants.ALLOCATION_BOUNCE);
					}
					
					// Calculate TAX Amounts against each TAX Type
					BigDecimal totalGSTPerc = BigDecimal.ZERO;
					
					for (int a = 0; a < adviseIdList.size(); a++) {

						ManualAdvise advise = adviseMap.get(adviseIdList.get(a));
						
						if((advise.isTaxApplicable() || StringUtils.equals(taxApplicable, PennantConstants.YES)) && taxPercmap == null){
							taxPercmap = getTaxPercentages(receiptData.getFinanceDetail());
							if(taxPercmap.containsKey("TOTALGST")){
								totalGSTPerc = taxPercmap.get("TOTALGST");
							}
						}
						
						if (advise != null) {

							String allocateType = "";
							boolean addGSTAmount = false;
							if (advise.getBounceID() > 0) {
								allocateType = RepayConstants.ALLOCATION_BOUNCE;
								
								if(StringUtils.equals(taxApplicable, PennantConstants.YES)){
									String taxType = SysParamUtil.getValueAsString("BOUNCE_TAX_TYPE");
									if(StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)){
										addGSTAmount = true;
									}
								}
								
							} else {
								allocateType = RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID();
								if(advise.isTaxApplicable() &&
										StringUtils.equals(advise.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)){
									addGSTAmount = true;
								}
							}

							if (paidAllocationMap.containsKey(allocateType)) {
								BigDecimal advAllocateBal = paidAllocationMap.get(allocateType);
								if (advAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
									BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount())
											.subtract(advise.getWaivedAmount());
									
									// In case of GST is Exclusive then GST amount should add before payment collection
									BigDecimal gstAmount = BigDecimal.ZERO;
									if(addGSTAmount){
										gstAmount = (balAdvise.multiply(totalGSTPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
										gstAmount = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
										balAdvise = balAdvise.add(gstAmount);
									}
									
									if (balAdvise.compareTo(BigDecimal.ZERO) > 0) {
										if (totalReceiptAmt.compareTo(advAllocateBal) >= 0
												&& advAllocateBal.compareTo(balAdvise) < 0) {
											balAdvise = advAllocateBal;
										} else if (totalReceiptAmt.compareTo(advAllocateBal) < 0
												&& balAdvise.compareTo(totalReceiptAmt) > 0) {
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
										movement.setWaivedAmount(BigDecimal.ZERO);
										movement.setFeeTypeCode(advise.getFeeTypeCode());
										
										// Total Paid/Adjusted GST amount
										BigDecimal totalTaxAmount = BigDecimal.ZERO;
										BigDecimal actTaxAmount = BigDecimal.ZERO;
										if(advise.getBounceID() > 0){
											totalTaxAmount = totalBounceTaxAmount;
													
											BigDecimal percentage = (totalGSTPerc.add(new BigDecimal(100))).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
											BigDecimal actualAmt = balAdvise.divide(percentage, 9, RoundingMode.HALF_DOWN);
											actualAmt = CalculationUtil.roundAmount(actualAmt, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
											actTaxAmount = balAdvise.subtract(actualAmt);
											
										}else{
											if(paidGSTMap.containsKey(allocateType)){
												totalTaxAmount = paidGSTMap.get(allocateType);
												actTaxAmount = paidGSTMap.get(allocateType);
											}
										}
										
										if ((advise.getBounceID() > 0 && StringUtils.equals(taxApplicable, PennantConstants.YES)) ||
												advise.getBounceID() == 0 && advise.isTaxApplicable()) {

											if(totalGSTPerc.compareTo(BigDecimal.ZERO) > 0){

												String roundingMode = financeMain.getCalRoundingMode();
												int roundingtarget = financeMain.getRoundingTarget();													

												// CGST Calculation
												if(taxPercmap.containsKey(RuleConstants.CODE_CGST)){
													BigDecimal taxPerc = taxPercmap.get(RuleConstants.CODE_CGST);	
													BigDecimal taxAmount = calculateTaxAmount(actTaxAmount, totalGSTPerc, taxPerc, roundingMode,roundingtarget);
													if(taxAmount.compareTo(totalTaxAmount) > 0){
														taxAmount = totalTaxAmount;
													}
													movement.setPaidCGST(taxAmount);
													totalTaxAmount = totalTaxAmount.subtract(taxAmount);
												}

												// SGST Calculation
												if(taxPercmap.containsKey(RuleConstants.CODE_SGST)){
													BigDecimal taxPerc = taxPercmap.get(RuleConstants.CODE_SGST);	
													BigDecimal taxAmount = calculateTaxAmount(actTaxAmount, totalGSTPerc,taxPerc, roundingMode,roundingtarget);
													if(taxAmount.compareTo(totalTaxAmount) > 0){
														taxAmount = totalTaxAmount;
													}
													movement.setPaidSGST(taxAmount);
													totalTaxAmount = totalTaxAmount.subtract(taxAmount);
												}

												// IGST Calculation
												if(taxPercmap.containsKey(RuleConstants.CODE_IGST)){
													BigDecimal taxPerc = taxPercmap.get(RuleConstants.CODE_IGST);	
													BigDecimal taxAmount = calculateTaxAmount(actTaxAmount, totalGSTPerc,taxPerc, roundingMode,roundingtarget);
													if(taxAmount.compareTo(totalTaxAmount) > 0){
														taxAmount = totalTaxAmount;
													}
													movement.setPaidIGST(taxAmount);
													totalTaxAmount = totalTaxAmount.subtract(taxAmount);
												}

												// UGST Calculation
												if(taxPercmap.containsKey(RuleConstants.CODE_UGST)){
													BigDecimal taxPerc = taxPercmap.get(RuleConstants.CODE_UGST);	
													BigDecimal taxAmount = calculateTaxAmount(actTaxAmount,totalGSTPerc, taxPerc, roundingMode,roundingtarget);
													if(taxAmount.compareTo(totalTaxAmount) > 0){
														taxAmount = totalTaxAmount;
													}
													movement.setPaidUGST(taxAmount);
													totalTaxAmount = totalTaxAmount.subtract(taxAmount);
												}
												// Reset remaining tax amount only in case of Bounce
												if(advise.getBounceID() > 0){
													totalBounceTaxAmount = totalTaxAmount;
												}
											}
										}
										
										// In case of Exclusive
										if(addGSTAmount){
											movement.setPaidAmount(balAdvise.subtract(movement.getPaidCGST()).subtract(
													movement.getPaidIGST()).subtract(movement.getPaidSGST()).subtract(movement.getPaidUGST()));
										}else{
											movement.setPaidAmount(balAdvise);
										}
										
										receiptDetail.getAdvMovements().add(movement);
									}
								}
							}
						}
					}
				}
			}

			if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				if (i != receiptDetailList.size() - 1) {
					continue;
				}
			}
			
			FinRepayHeader repayHeader = null;
			BigDecimal balAmount = actualReceiptAmt.subtract(totalReceiptAmt);
			if (((actualReceiptAmt.compareTo(totalReceiptAmt) > 0 && balAmount.compareTo(advAmountPaid) != 0)
					|| totalWaivedAmt.compareTo(BigDecimal.ZERO) > 0)
					&& actualReceiptAmt.compareTo(partialSettleAmount) > 0 && isSchdPaid) {
				// Prepare Repay Header Details
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(valueDate);
				if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
					repayHeader.setRepayAmount(receiptData.getReceiptHeader().getReceiptAmount());
					repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
				} else {
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
				repayHeader.setRepayScheduleDetails(resetRpySchedules(pastdueRpySchdList));
				repayHeaderList.add(repayHeader);
			}

			if (totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0
					&& (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)
							|| StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE))) {
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

			// Prepare Remaining Balance Amount as Partial Settlement , If
			// selected Receipt Purpose is same
			if (partialSettleAmount.compareTo(BigDecimal.ZERO) > 0
					&& StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {

				// Prepare Repay Header for Partial Settlement
				repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(valueDate);
				
				repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_EARLYRPY);
				repayHeader.setRepayAmount(partialSettleAmount);
				repayHeader.setPriAmount(partialSettleAmount);
				repayHeader.setPftAmount(BigDecimal.ZERO);
				repayHeader.setLatePftAmount(BigDecimal.ZERO);
				repayHeader.setTotalPenalty(BigDecimal.ZERO);
				repayHeader.setTotalIns(BigDecimal.ZERO);
				repayHeader.setTotalSchdFee(BigDecimal.ZERO);

				// Adding Repay Schedule Details to Repay Header
				repayHeader.setRepayScheduleDetails(resetRpySchedules(partialRpySchdList));
				repayHeaderList.add(repayHeader);
			}

			// Adding Repay Headers to Receipt Details
			receiptDetail.setRepayHeaders(repayHeaderList);

		}

		overdueList = null;
		paidAllocationMap = null;

		// If Total Waived amount is Zero, no need to Waived amount settings
		if (totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0) {
			waivedAllocationMap = null;

			logger.debug("Leaving");
			return receiptData;
		}

		// Schedule Payments & Overdue details Waived adjustment
		// Finding Repayment Schedule details record on Last Repay Header,
		// because all waivers should add to last record only
		int size = receiptDetailList.size();
		boolean schWaivedAdjComplete = false;
		for (int i = size - 1; i >= 0; i--) {
			if (schWaivedAdjComplete) {
				break;
			}
			FinReceiptDetail rd = receiptDetailList.get(i);
			int rhSize = rd.getRepayHeaders().size();

			boolean isRcdFound = false;
			for (int j = rhSize - 1; j >= 0; j--) {
				if (StringUtils.equals(rd.getRepayHeaders().get(j).getFinEvent(), RepayConstants.EXCESSADJUSTTO_EXCESS)
						|| StringUtils.equals(rd.getRepayHeaders().get(j).getFinEvent(),
								RepayConstants.EXCESSADJUSTTO_EMIINADV)) {
					continue;
				}
				isRcdFound = true;
			}

			// Case will occur only in case of full schedule payments waiver
			if (!isRcdFound) {
				// Prepare Repay Header Details
				FinRepayHeader repayHeader = new FinRepayHeader();
				repayHeader.setFinReference(receiptData.getFinReference());
				repayHeader.setValueDate(valueDate);
				if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
					repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_EARLYSETTLE);
				} else {
					repayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_SCHDRPY);
				}
				repayHeader.setRepayAmount(BigDecimal.ZERO);
				repayHeader.setPriAmount(totPriPaidNow);
				repayHeader.setPftAmount(totPftPaidNow);
				repayHeader.setLatePftAmount(totLPftPaidNow);
				repayHeader.setTotalPenalty(totPenaltyPaidNow);
				repayHeader.setTotalIns(totInsPaidNow);
				repayHeader.setTotalSchdFee(totFeePaidNow);
				repayHeader.setTotalWaiver(totalWaivedAmt);

				// Adding Repay Schedule Details to Repay Header
				rd.getRepayHeaders().add(repayHeader);
				rhSize = rd.getRepayHeaders().size();
			}

			FinRepayHeader rh = null;
			for (int j = rhSize - 1; j >= 0; j--) {
				if (StringUtils.equals(rd.getRepayHeaders().get(j).getFinEvent(), RepayConstants.EXCESSADJUSTTO_EXCESS)
						|| StringUtils.equals(rd.getRepayHeaders().get(j).getFinEvent(),
								RepayConstants.EXCESSADJUSTTO_EMIINADV)) {
					continue;
				}
				rh = rd.getRepayHeaders().get(j);
			}

			Map<Date, RepayScheduleDetail> rpySchMap = new HashMap<>();
			if (rh.getRepayScheduleDetails() != null && !rh.getRepayScheduleDetails().isEmpty()) {
				for (RepayScheduleDetail rsd : rh.getRepayScheduleDetails()) {
					rpySchMap.put(rsd.getSchDate(), rsd);
				}
			}

			schWaivedAdjComplete = true;

			// Schedule Details verification
			for (int s = 1; s < tempScheduleDetails.size(); s++) {
				FinanceScheduleDetail curSchd = tempScheduleDetails.get(s);
				Date schdDate = curSchd.getSchDate();
				RepayScheduleDetail rsd = null;

				// Schedule Wise Balance
				BigDecimal schdBal = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())
						.add(curSchd.getProfitSchd()).subtract(curSchd.getSchdPftPaid()).add(curSchd.getFeeSchd())
						.subtract(curSchd.getSchdFeePaid()).add(curSchd.getInsSchd())
						.subtract(curSchd.getSchdInsPaid());

				// Overdue Balance
				BigDecimal overdueBal = BigDecimal.ZERO;
				if (overdueMap.containsKey(schdDate)) {
					FinODDetails overdue = overdueMap.get(schdDate);
					overdueBal = overdue.getTotPenaltyBal().add(overdue.getLPIBal());
				}

				// If Schedule Balance & Overdue (Penalty & LPI) Balance
				if ((schdBal.add(overdueBal)).compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				if (rpySchMap.containsKey(schdDate)) {
					rsd = rpySchMap.get(schdDate);
				}

				// Principal Amount
				if (waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_PRI)) {
					if ((curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()))
							.compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal waivedNow = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());
						BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_PRI);
						if (waivedNow.compareTo(balWaived) > 0) {
							waivedNow = balWaived;
						}
						rsd = prepareRpyWaivedRecord(curSchd, rsd, RepayConstants.REPAY_PRINCIPAL, waivedNow, valueDate,
								null);
						totalSchdWaivedAmt = totalSchdWaivedAmt.subtract(waivedNow);
						totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
						waivedAllocationMap.put(RepayConstants.ALLOCATION_PRI, balWaived.subtract(waivedNow));
					}
				}

				// Profit Amount
				if (waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_PFT)) {
					BigDecimal waivedNow = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());
					BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_PFT);
					if (waivedNow.compareTo(balWaived) > 0) {
						waivedNow = balWaived;
					}

					rsd = prepareRpyWaivedRecord(curSchd, rsd, RepayConstants.REPAY_PROFIT, waivedNow, valueDate, null);
					totalSchdWaivedAmt = totalSchdWaivedAmt.subtract(waivedNow);
					totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
					waivedAllocationMap.put(RepayConstants.ALLOCATION_PFT, balWaived.subtract(waivedNow));

				}

				// Late Payment Profit Amount
				if (waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_LPFT)) {

					BigDecimal waivedNow = BigDecimal.ZERO;
					if (overdueMap.containsKey(schdDate)) {
						waivedNow = overdueMap.get(schdDate).getLPIBal();
					}
					BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_LPFT);
					if (waivedNow.compareTo(balWaived) > 0) {
						waivedNow = balWaived;
					}
					rsd = prepareRpyWaivedRecord(curSchd, rsd, RepayConstants.REPAY_LATEPAY_PROFIT, waivedNow,
							valueDate, null);
					totalSchdWaivedAmt = totalSchdWaivedAmt.subtract(waivedNow);
					totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
					waivedAllocationMap.put(RepayConstants.ALLOCATION_LPFT, balWaived.subtract(waivedNow));
				}

				// Penalty Amount
				if (waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_ODC)) {
					BigDecimal waivedNow = BigDecimal.ZERO;
					if (overdueMap.containsKey(schdDate)) {
						waivedNow = overdueMap.get(schdDate).getTotPenaltyBal();
					}
					BigDecimal balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_ODC);
					if (waivedNow.compareTo(balWaived) > 0) {
						waivedNow = balWaived;
					}
					rsd = prepareRpyWaivedRecord(curSchd, rsd, RepayConstants.REPAY_PENALTY, waivedNow, valueDate,
							overdueMap.containsKey(schdDate) ? overdueMap.get(schdDate).getTotPenaltyBal()
									: BigDecimal.ZERO);
					totalSchdWaivedAmt = totalSchdWaivedAmt.subtract(waivedNow);
					totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);
					waivedAllocationMap.put(RepayConstants.ALLOCATION_ODC, balWaived.subtract(waivedNow));
				}

				// Schedule Fee Amount
				if (waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_FEE)) {

					BigDecimal waivedNow = curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid());

					// If Schedule has Unpaid Fee Amount
					if (waivedNow.compareTo(BigDecimal.ZERO) > 0) {

						// Fee Detail Collection
						for (int k = 0; k < scheduleData.getFinFeeDetailList().size(); k++) {
							FinFeeDetail feeSchd = scheduleData.getFinFeeDetailList().get(k);
							if (StringUtils.equals(feeSchd.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)) {
								continue;
							}
							List<FinFeeScheduleDetail> feeSchdList = feeSchd.getFinFeeScheduleDetailList();

							// No more Receipt amount left for next schedules
							if (totalSchdWaivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
								totalSchdWaivedAmt = BigDecimal.ZERO;
								break;
							}

							if (feeSchdList == null || feeSchdList.isEmpty()) {
								continue;
							}

							// Calculate and set Fee Amount based on Fee ID
							for (int l = 0; l < feeSchdList.size(); l++) {
								if (feeSchdList.get(l).getSchDate().compareTo(schdDate) == 0) {

									if (waivedAllocationMap
											.containsKey(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID())) {
										BigDecimal feeAllocateBal = waivedAllocationMap
												.get(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID());
										if (feeAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
											BigDecimal balFee = feeSchdList.get(l).getSchAmount()
													.subtract(feeSchdList.get(l).getPaidAmount());
											if (balFee.compareTo(BigDecimal.ZERO) > 0) {
												if (totalSchdWaivedAmt.compareTo(feeAllocateBal) >= 0
														&& feeAllocateBal.compareTo(balFee) < 0) {
													balFee = feeAllocateBal;
												} else if (totalSchdWaivedAmt.compareTo(feeAllocateBal) < 0
														&& balFee.compareTo(totalSchdWaivedAmt) > 0) {
													balFee = totalSchdWaivedAmt;
												}
												rsd = prepareRpyWaivedRecord(curSchd, rsd, RepayConstants.REPAY_FEE,
														waivedNow, valueDate, null);

												// Reset Total Receipt Amount
												totalSchdWaivedAmt = totalSchdWaivedAmt.subtract(waivedNow);
												totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);

												waivedAllocationMap.put(
														RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID(),
														feeAllocateBal.subtract(balFee));

												// Update Schedule to avoid on
												// Next loop Payment
												feeSchdList.get(l).setWaiverAmount(
														feeSchdList.get(l).getWaiverAmount().add(balFee));
											}
										}
									}
									break;
								}
							}
						}
					}
				}

				// Insurance Amount
				if (waivedAllocationMap.containsKey(RepayConstants.ALLOCATION_INS)) {

					BigDecimal waivedNow = curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid());

					// If Schedule has Unpaid Insurance Amount
					if (waivedNow.compareTo(BigDecimal.ZERO) > 0) {

						// Insurance Details Collection
						for (int k = 0; k < scheduleData.getFinInsuranceList().size(); k++) {
							FinInsurances insSchd = scheduleData.getFinInsuranceList().get(k);
							List<FinSchFrqInsurance> insSchdList = insSchd.getFinSchFrqInsurances();

							// No more Receipt amount left for next schedules
							if (totalSchdWaivedAmt.compareTo(BigDecimal.ZERO) <= 0) {
								totalSchdWaivedAmt = BigDecimal.ZERO;
								break;
							}

							if (insSchdList == null || insSchdList.isEmpty()) {
								continue;
							}

							// Calculate and set Fee Amount based on Fee ID
							for (int l = 0; l < insSchdList.size(); l++) {
								if (insSchdList.get(l).getInsSchDate().compareTo(schdDate) == 0) {

									if (waivedAllocationMap
											.containsKey(RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId())) {
										BigDecimal insAllocateBal = waivedAllocationMap
												.get(RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId());
										if (insAllocateBal.compareTo(BigDecimal.ZERO) > 0) {
											BigDecimal balIns = insSchdList.get(l).getAmount()
													.subtract(insSchdList.get(l).getInsurancePaid());
											if (balIns.compareTo(BigDecimal.ZERO) > 0) {
												if (totalSchdWaivedAmt.compareTo(insAllocateBal) >= 0
														&& insAllocateBal.compareTo(balIns) < 0) {
													balIns = insAllocateBal;
												} else if (totalSchdWaivedAmt.compareTo(insAllocateBal) < 0
														&& balIns.compareTo(totalSchdWaivedAmt) > 0) {
													balIns = totalSchdWaivedAmt;
												}
												rsd = prepareRpyWaivedRecord(curSchd, rsd, RepayConstants.REPAY_INS,
														waivedNow, valueDate, null);

												// Reset Total Receipt Amount
												totalSchdWaivedAmt = totalSchdWaivedAmt.subtract(waivedNow);
												totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);

												waivedAllocationMap.put(
														RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId(),
														insAllocateBal.subtract(balIns));

												// Update Schedule to avoid on
												// Next loop Payment
												insSchdList.get(l).setInsurancePaid(
														insSchdList.get(l).getInsurancePaid().add(balIns));
											}
										}
									}
									break;
								}
							}
						}
					}
				}

				// Resetting Repayment Schedule Details
				if (rsd != null) {
					rpySchMap.put(schdDate, rsd);
				}

				// If No Outstanding Waiver amount
				if (totalSchdWaivedAmt.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}

			// Resetting prepared Repayment Schedule after Waivers setting
			rh.setRepayScheduleDetails(new ArrayList<>(rpySchMap.values()));

			// Check whether Manual Advise & Bounce Waivers exists or not
			boolean advWaiverFound = false;
			if (waivedAllocationMap != null && !waivedAllocationMap.isEmpty()) {

				for (String allocationKey : waivedAllocationMap.keySet()) {

					if (allocationKey.contains(RepayConstants.ALLOCATION_MANADV)
							|| allocationKey.contains(RepayConstants.ALLOCATION_BOUNCE)) {
						if (waivedAllocationMap.get(allocationKey).compareTo(BigDecimal.ZERO) > 0) {
							advWaiverFound = true;
						}
					}
				}
			}

			// Receivable Advises & Bounce
			if (advWaiverFound) {

				// Total Movements exists against last adjusted Receipt(Because
				// waivers are always adjusting to Last Receipt only
				if (!adviseMap.isEmpty()) {
					List<ManualAdviseMovements> advMovements = rd.getAdvMovements();

					// First Adjusting existed Movement details with Waiver
					// amounts allowed
					if (advMovements != null && !advMovements.isEmpty()) {
						for (ManualAdviseMovements advMovement : advMovements) {

							boolean isBounce = false;
							BigDecimal balAdv = BigDecimal.ZERO;
							if (adviseMap.containsKey(advMovement.getAdviseID())) {
								ManualAdvise adv = adviseMap.get(advMovement.getAdviseID());
								if (adv.getBounceID() > 0) {
									isBounce = true;
								}
								balAdv = adv.getAdviseAmount().subtract(adv.getPaidAmount())
										.subtract(adv.getWaivedAmount());
							}
							BigDecimal waivedNow = balAdv.subtract(advMovement.getPaidAmount());
							BigDecimal balWaived = BigDecimal.ZERO;
							if (isBounce) {
								balWaived = waivedAllocationMap.get(RepayConstants.ALLOCATION_BOUNCE);
							} else {
								balWaived = waivedAllocationMap
										.get(RepayConstants.ALLOCATION_MANADV + "_" + advMovement.getAdviseID());
							}
							if (waivedNow.compareTo(balWaived) > 0) {
								waivedNow = balWaived;
							}
							advMovement.setWaivedAmount(waivedNow);
							advMovement.setMovementAmount(advMovement.getMovementAmount().add(waivedNow));
							totalWaivedAmt = totalWaivedAmt.subtract(waivedNow);

							if (isBounce) {
								waivedAllocationMap.put(RepayConstants.ALLOCATION_BOUNCE,
										balWaived.subtract(waivedNow));
							} else {
								waivedAllocationMap.put(
										RepayConstants.ALLOCATION_MANADV + "_" + advMovement.getAdviseID(),
										balWaived.subtract(waivedNow));
							}

							// Resetting Actual Advise map Data, because we need
							// to re-check movements which are not exists
							if (adviseMap.containsKey(advMovement.getAdviseID())) {
								ManualAdvise adv = adviseMap.get(advMovement.getAdviseID());
								adv.setWaivedAmount(adv.getWaivedAmount().add(waivedNow));
								adviseMap.remove(advMovement.getAdviseID());
								adviseMap.put(advMovement.getAdviseID(), adv);
							}
						}
					}

					// Still Waiver Amount exists for Manual Advise or Bounce
					if (totalWaivedAmt.compareTo(BigDecimal.ZERO) > 0) {
						List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
						Collections.sort(adviseIdList);
						for (int a = 0; a < adviseIdList.size(); a++) {

							ManualAdvise adv = adviseMap.get(adviseIdList.get(a));
							if (adv != null) {

								String allocateType = "";
								if (adv.getBounceID() > 0) {
									allocateType = RepayConstants.ALLOCATION_BOUNCE;
								} else {
									allocateType = RepayConstants.ALLOCATION_MANADV + "_" + adv.getAdviseID();
								}

								BigDecimal waivedNow = adv.getAdviseAmount().subtract(adv.getPaidAmount())
										.subtract(adv.getWaivedAmount());
								BigDecimal balWaived = waivedAllocationMap.get(allocateType);
								if (waivedNow.compareTo(balWaived) > 0) {
									waivedNow = balWaived;
								}

								// Save Movements for Manual Advise
								ManualAdviseMovements movement = new ManualAdviseMovements();
								movement.setAdviseID(adv.getAdviseID());
								movement.setMovementDate(valueDate);
								movement.setMovementAmount(waivedNow);
								movement.setPaidAmount(BigDecimal.ZERO);
								movement.setWaivedAmount(waivedNow);
								movement.setFeeTypeCode(adv.getFeeTypeCode());
								movement.setFeeTypeDesc(adv.getFeeTypeDesc());
								advMovements.add(movement);
							}
						}
					}
					rd.setAdvMovements(advMovements);
				}
			}

			// If No Outstanding Waiver amount
			if (totalWaivedAmt.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
		}

		overdueMap = null;
		waivedAllocationMap = null;

		logger.debug("Leaving");
		return receiptData;
	}

	/**
	 * Method for Sorting Schedule Details
	 * 
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
	 * Method for Resetting Duplicate repayment schedule data into single record
	 * 
	 * @param rpySchdList
	 * @return
	 */
	private List<RepayScheduleDetail> resetRpySchedules(List<RepayScheduleDetail> rpySchdList) {

		// Making Single Set of Repay Schedule Details and sent to Rendering
		Cloner cloner = new Cloner();
		List<RepayScheduleDetail> tempRpySchdList = cloner.deepClone(rpySchdList);
		Map<Date, RepayScheduleDetail> rpySchdMap = new HashMap<>();
		for (RepayScheduleDetail rpySchd : tempRpySchdList) {

			RepayScheduleDetail curRpySchd = null;
			if (rpySchdMap.containsKey(rpySchd.getSchDate())) {
				curRpySchd = rpySchdMap.get(rpySchd.getSchDate());

				if (curRpySchd.getPrincipalSchdBal().compareTo(rpySchd.getPrincipalSchdBal()) < 0) {
					curRpySchd.setPrincipalSchdBal(rpySchd.getPrincipalSchdBal());
				}

				if (curRpySchd.getProfitSchdBal().compareTo(rpySchd.getProfitSchdBal()) < 0) {
					curRpySchd.setProfitSchdBal(rpySchd.getProfitSchdBal());
				}

				// Paid Details
				curRpySchd.setPrincipalSchdPayNow(
						curRpySchd.getPrincipalSchdPayNow().add(rpySchd.getPrincipalSchdPayNow()));
				curRpySchd.setProfitSchdPayNow(curRpySchd.getProfitSchdPayNow().add(rpySchd.getProfitSchdPayNow()));
				curRpySchd.setTdsSchdPayNow(curRpySchd.getTdsSchdPayNow().add(rpySchd.getTdsSchdPayNow()));
				curRpySchd.setLatePftSchdPayNow(curRpySchd.getLatePftSchdPayNow().add(rpySchd.getLatePftSchdPayNow()));
				curRpySchd.setSchdFeePayNow(curRpySchd.getSchdFeePayNow().add(rpySchd.getSchdFeePayNow()));
				curRpySchd.setSchdInsPayNow(curRpySchd.getSchdInsPayNow().add(rpySchd.getSchdInsPayNow()));
				curRpySchd.setPenaltyPayNow(curRpySchd.getPenaltyPayNow().add(rpySchd.getPenaltyPayNow()));

				// Waiver Details
				curRpySchd.setPriSchdWaivedNow(curRpySchd.getPriSchdWaivedNow().add(rpySchd.getPriSchdWaivedNow()));
				curRpySchd.setPftSchdWaivedNow(curRpySchd.getPftSchdWaivedNow().add(rpySchd.getPftSchdWaivedNow()));
				curRpySchd.setLatePftSchdWaivedNow(
						curRpySchd.getLatePftSchdWaivedNow().add(rpySchd.getLatePftSchdWaivedNow()));
				curRpySchd.setWaivedAmt(curRpySchd.getWaivedAmt().add(rpySchd.getWaivedAmt()));
				curRpySchd.setSchdFeeWaivedNow(curRpySchd.getSchdFeeWaivedNow().add(rpySchd.getSchdFeeWaivedNow()));
				curRpySchd.setSchdInsWaivedNow(curRpySchd.getSchdInsWaivedNow().add(rpySchd.getSchdInsWaivedNow()));
				curRpySchd.setSchdSuplRentWaivedNow(
						curRpySchd.getSchdSuplRentWaivedNow().add(rpySchd.getSchdSuplRentWaivedNow()));
				curRpySchd.setSchdIncrCostWaivedNow(
						curRpySchd.getSchdIncrCostWaivedNow().add(rpySchd.getSchdIncrCostWaivedNow()));

				rpySchdMap.remove(rpySchd.getSchDate());
			} else {
				curRpySchd = rpySchd;
			}

			// Adding New Repay Schedule Object to Map after Summing data
			rpySchdMap.put(rpySchd.getSchDate(), curRpySchd);
		}

		return new ArrayList<>(rpySchdMap.values());
	}

	/**
	 * Method for Preparation of Repayment Schedule Details
	 * 
	 * @param curSchd
	 * @param rsd
	 * @param rpyTo
	 * @param balPayNow
	 * @return
	 */
	private RepayScheduleDetail prepareRpyPaidRecord(FinanceScheduleDetail curSchd, RepayScheduleDetail rsd, char rpyTo,
			BigDecimal balPayNow, Date valueDate, BigDecimal actualPenalty) {

		if (rsd == null) {
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
		if (rpyTo == RepayConstants.REPAY_PRINCIPAL) {
			rsd.setPrincipalSchdPayNow(balPayNow);
		}

		// Profit Payment
		if (rpyTo == RepayConstants.REPAY_PROFIT) {
			rsd.setProfitSchdPayNow(balPayNow);
		}

		// TDS Payment
		if (rpyTo == RepayConstants.REPAY_TDS) {
			rsd.setTdsSchdPayNow(balPayNow);
		}

		// Late Payment Profit Payment
		if (rpyTo == RepayConstants.REPAY_LATEPAY_PROFIT) {
			rsd.setLatePftSchdPayNow(balPayNow);
		}

		// Fee Detail Payment
		if (rpyTo == RepayConstants.REPAY_FEE) {
			rsd.setSchdFeePayNow(rsd.getSchdFeePayNow().add(balPayNow));
		}

		// Insurance Detail Payment
		if (rpyTo == RepayConstants.REPAY_INS) {
			rsd.setSchdInsPayNow(rsd.getSchdInsPayNow().add(balPayNow));
		}

		// Penalty Charge Detail Payment
		if (rpyTo == RepayConstants.REPAY_PENALTY) {
			rsd.setPenaltyAmt(actualPenalty);
			rsd.setPenaltyPayNow(balPayNow);
		}

		return rsd;

	}

	/**
	 * Method for Preparation of Repayment Schedule Details
	 * 
	 * @param curSchd
	 * @param rsd
	 * @param rpyTo
	 * @param waivedNow
	 * @return
	 */
	private RepayScheduleDetail prepareRpyWaivedRecord(FinanceScheduleDetail curSchd, RepayScheduleDetail rsd,
			char rpyTo, BigDecimal waivedNow, Date valueDate, BigDecimal actualPenalty) {

		if (rsd == null) {

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
		if (rpyTo == RepayConstants.REPAY_PRINCIPAL) {
			rsd.setPriSchdWaivedNow(waivedNow);
		}

		// Profit Payment
		if (rpyTo == RepayConstants.REPAY_PROFIT) {
			rsd.setPftSchdWaivedNow(waivedNow);
		}

		// Late Payment Profit Payment
		if (rpyTo == RepayConstants.REPAY_LATEPAY_PROFIT) {
			rsd.setLatePftSchdWaivedNow(waivedNow);
		}

		// Fee Detail Payment
		if (rpyTo == RepayConstants.REPAY_FEE) {
			rsd.setSchdFeeWaivedNow(rsd.getSchdFeeWaivedNow().add(waivedNow));
		}

		// Insurance Detail Payment
		if (rpyTo == RepayConstants.REPAY_INS) {
			rsd.setSchdInsWaivedNow(rsd.getSchdInsWaivedNow().add(waivedNow));
		}

		// Penalty Charge Detail Payment
		if (rpyTo == RepayConstants.REPAY_PENALTY) {
			if (rsd.getPenaltyAmt().compareTo(BigDecimal.ZERO) == 0) {
				rsd.setPenaltyAmt(actualPenalty);
			}
			rsd.setWaivedAmt(waivedNow);
		}

		return rsd;

	}

	/**
	 * Method for Processing Schedule Data and Receipts to Prepare Allocation
	 * Details
	 * 
	 * @param receiptData
	 * @param aFinScheduleData
	 */
	public Map<String, BigDecimal> recalAutoAllocation(FinanceDetail financeDetail, BigDecimal totalReceiptAmt,
			Date valueDate, String receiptPurpose, boolean isPresentment) {
		logger.debug("Entering");

		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleDetails = scheduleData.getFinanceScheduleDetails();

		// If no balance for repayment then return with out calculation
		if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}
		// Fetch total Advise details
		Map<Long, ManualAdvise> adviseMap = new HashMap<Long, ManualAdvise>();
		if (!isPresentment) {
			List<ManualAdvise> advises = manualAdviseDAO.getManualAdviseByRef(financeMain.getFinReference(),
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
		Date reqMaxODDate = valueDate;
		if (!ImplementationConstants.LPP_CALC_SOD) {
			reqMaxODDate = DateUtility.addDays(valueDate, -1);
		}
		List<FinODDetails> overdueList = null;
		if (DateUtility.compare(valueDate, DateUtility.getAppDate()) == 0) {
			overdueList = finODDetailsDAO.getFinODBalByFinRef(financeMain.getFinReference());
		} else {
			// Calculate overdue Penalties
			overdueList = receiptService.getValueDatePenalties(scheduleData, totalReceiptAmt, reqMaxODDate, null,
					true);
		}

		if (overdueList != null && !overdueList.isEmpty()) {
			for (int i = 0; i < overdueList.size(); i++) {
				if (overdueMap.containsKey(overdueList.get(i).getFinODSchdDate())) {
					overdueMap.remove(overdueList.get(i).getFinODSchdDate());
				}

				if (DateUtility.compare(overdueList.get(i).getFinODSchdDate(), reqMaxODDate) <= 0) {
					overdueMap.put(overdueList.get(i).getFinODSchdDate(), overdueList.get(i));
				}
			}
		}

		String repayHierarchy = scheduleData.getFinanceType().getRpyHierarchy();
		boolean seperatePenalties = false;
		if (repayHierarchy.contains("CS")) {
			seperatePenalties = true;
		}
		char[] rpyOrder = repayHierarchy.replace("CS", "").toCharArray();
		Map<String, BigDecimal> allocatePaidMap = new HashMap<>();
		int lastRenderSeq = 0;
		boolean isLastTermForES = false;// Last Term for Early Settlement
		boolean pftCalcCompleted = false;// Profit calculation Completed flag
											// setting for Last term on Early
											// settlement
		boolean priCalcCompleted = false;// Principal calculation Completed flag
											// setting for Last term on Early
											// settlement

		// TDS Calculation, if Applicable
		BigDecimal tdsMultiplier = BigDecimal.ONE;
		if (financeMain.isTDSApplicable()) {

			BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValue(CalculationConstants.TDS_PERCENTAGE).toString());
			/*String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
			int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);*/
			
			if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
				tdsMultiplier = (new BigDecimal(100)).divide(new BigDecimal(100).subtract(tdsPerc), 20,
						RoundingMode.HALF_DOWN);
			}
		}

		// Load Pending Schedules until balance available for payment
		for (int i = 1; i < scheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = scheduleDetails.get(i);
			FinanceScheduleDetail prvSchd = scheduleDetails.get(i - 1);
			Date schdDate = curSchd.getSchDate();

			// Skip if repayments date after Current Business date
			if (schdDate.compareTo(valueDate) > 0
					&& !StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				break;
			}

			if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_SCHDRPY)
					&& curSchd.getPresentmentId() > 0 && !isPresentment) {
				continue;
			}

			// If Presentment Process, only Presentment Date schedule should be
			// effected.
			if (isPresentment && (DateUtility.compare(valueDate, schdDate) != 0)) {
				continue;
			}

			// Early settlement case to calculate profit Till Accrual amount
			if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				if (schdDate.compareTo(valueDate) >= 0) {
					isLastTermForES = true;
				}
			}
			
			for (int j = 0; j < rpyOrder.length; j++) {

				char repayTo = rpyOrder[j];
				if (repayTo == RepayConstants.REPAY_PRINCIPAL) {

					// On Early settlement case, If profit is calculated upto
					// Accruals, from Next Schedule onwards no need to consider
					// Profit Amount
					if (priCalcCompleted) {
						continue;
					}

					BigDecimal balPri = curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid());

					if (isLastTermForES) {
						priCalcCompleted = true;
						if (schdDate.compareTo(valueDate) >= 0) {
							balPri = balPri.add(curSchd.getClosingBalance().subtract(curSchd.getCpzAmount()));
						}
					}

					if (balPri.compareTo(BigDecimal.ZERO) > 0) {
						if (totalReceiptAmt.compareTo(balPri) > 0) {
							totalReceiptAmt = totalReceiptAmt.subtract(balPri);
						} else {
							balPri = totalReceiptAmt;
							totalReceiptAmt = BigDecimal.ZERO;
						}

						BigDecimal totPriPayNow = BigDecimal.ZERO;
						if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PRI)) {
							totPriPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_PRI);
							allocatePaidMap.remove(RepayConstants.ALLOCATION_PRI);
						}
						allocatePaidMap.put(RepayConstants.ALLOCATION_PRI, totPriPayNow.add(balPri));
					}
				} else if (repayTo == RepayConstants.REPAY_PROFIT) {

					// On Early settlement case, If profit is calculated upto
					// Accruals, from Next Schedule onwards no need to consider
					// Profit Amount
					if (pftCalcCompleted) {
						continue;
					}

					String profit = ImplementationConstants.REPAY_INTEREST_HIERARCHY;
					char[] pftPayOrder = profit.toCharArray();
					for (char pftPayTo : pftPayOrder) {
						if (pftPayTo == RepayConstants.REPAY_PROFIT) {

							BigDecimal balPft = curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid());

							// On Early settlement Case, for the last terms
							// should consider total outstanding Principal
							if (isLastTermForES) {
								pftCalcCompleted = true;
								if (schdDate.compareTo(valueDate) > 0){
									
									balPft = CalculationUtil.calInterest(prvSchd.getSchDate(), valueDate, curSchd.getBalanceForPftCal(),
											prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());
									balPft = balPft.add(prvSchd.getProfitFraction().add(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())));
									balPft = CalculationUtil.roundAmount(balPft, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
									if(balPft.compareTo(BigDecimal.ZERO) < 0){
										balPft = BigDecimal.ZERO;
									}
									// Rounding Profit Adjustment on Last Term
									BigDecimal lastTermEMI = CalculationUtil.roundAmount(
											balPft.add(prvSchd.getClosingBalance()), financeMain.getCalRoundingMode(),
											financeMain.getRoundingTarget());
									BigDecimal roundingPftDiff = lastTermEMI.subtract(prvSchd.getClosingBalance())
											.subtract(balPft);
									balPft = balPft.add(roundingPftDiff);

								} else if (schdDate.compareTo(valueDate) == 0) {
									balPft = curSchd.getProfitCalc().subtract(curSchd.getSchdPftPaid())
											.add(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount()));
									
									int roundRequired = SysParamUtil.getValueAsInt(SMTParameterConstants.ROUND_LASTSCHD);
									if (roundRequired == 1) {
										BigDecimal lastEMI = CalculationUtil.roundAmount(prvSchd.getClosingBalance().add(balPft),financeMain.getCalRoundingMode(), 
												financeMain.getRoundingTarget());
										balPft = lastEMI.subtract(prvSchd.getClosingBalance());
										if (balPft.compareTo(BigDecimal.ZERO) < 0) {
											balPft = BigDecimal.ZERO;
										}
									}
								}
							}

							if (balPft.compareTo(BigDecimal.ZERO) > 0) {

								BigDecimal actPftAdjust = BigDecimal.ZERO;
								if (isLastTermForES) {
									actPftAdjust = balPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
									//actPftAdjust = CalculationUtil.roundAmount(actPftAdjust, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
								}else{
									actPftAdjust = balPft.divide(tdsMultiplier, 0, RoundingMode.HALF_DOWN);
									//actPftAdjust = CalculationUtil.roundAmount(actPftAdjust, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
								}

								// TDS Adjustments
								BigDecimal tdsAdjust = BigDecimal.ZERO;
								if (totalReceiptAmt.compareTo(actPftAdjust) > 0) {
									totalReceiptAmt = totalReceiptAmt.subtract(actPftAdjust);
									tdsAdjust = balPft.subtract(actPftAdjust);
								} else {
									actPftAdjust = totalReceiptAmt;
									tdsAdjust = (actPftAdjust.multiply(tdsMultiplier)).subtract(actPftAdjust);
									totalReceiptAmt = BigDecimal.ZERO;
								}

								// TDS Re-adjust(minor difference) TEMP FIX
								if (balPft.compareTo(actPftAdjust.add(tdsAdjust)) < 0) {
									tdsAdjust = tdsAdjust.add(balPft.subtract(actPftAdjust.add(tdsAdjust)));
								}

								// Profit Amount Payments
								BigDecimal totPftPayNow = BigDecimal.ZERO;
								if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PFT)) {
									totPftPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_PFT);
									allocatePaidMap.remove(RepayConstants.ALLOCATION_PFT);
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_PFT,
										totPftPayNow.add(actPftAdjust.add(tdsAdjust)));

								// TDS Payments
								BigDecimal totTDSPayNow = BigDecimal.ZERO;
								if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_TDS)) {
									totTDSPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_TDS);
									allocatePaidMap.remove(RepayConstants.ALLOCATION_TDS);
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_TDS, totTDSPayNow.add(tdsAdjust));
							}

						} else if (pftPayTo == RepayConstants.REPAY_LATEPAY_PROFIT) {

							FinODDetails overdue = overdueMap.get(schdDate);
							if (overdue != null && DateUtility.compare(overdue.getFinODSchdDate(), valueDate) <= 0) {
								BigDecimal balLatePft = overdue.getLPIBal();
								if (balLatePft.compareTo(BigDecimal.ZERO) > 0) {
									if (totalReceiptAmt.compareTo(balLatePft) > 0) {
										totalReceiptAmt = totalReceiptAmt.subtract(balLatePft);
									} else {
										balLatePft = totalReceiptAmt;
										totalReceiptAmt = BigDecimal.ZERO;
									}

									BigDecimal totLatePftPayNow = BigDecimal.ZERO;
									if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_LPFT)) {
										totLatePftPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_LPFT);
										allocatePaidMap.remove(RepayConstants.ALLOCATION_LPFT);
									}
									allocatePaidMap.put(RepayConstants.ALLOCATION_LPFT,
											totLatePftPayNow.add(balLatePft));
								}
							}
						}
					}

				} else if (repayTo == RepayConstants.REPAY_PENALTY) {
					if (!seperatePenalties) {
						FinODDetails overdue = overdueMap.get(schdDate);
						if (overdue != null && DateUtility.compare(overdue.getFinODSchdDate(), valueDate) <= 0) {
							BigDecimal balPenalty = overdue.getTotPenaltyBal();
							if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
								if (totalReceiptAmt.compareTo(balPenalty) > 0) {
									totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
								} else {
									balPenalty = totalReceiptAmt;
									totalReceiptAmt = BigDecimal.ZERO;
								}

								BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
								if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_ODC)) {
									totPenaltyPayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_ODC);
									allocatePaidMap.remove(RepayConstants.ALLOCATION_ODC);
								}
								allocatePaidMap.put(RepayConstants.ALLOCATION_ODC, totPenaltyPayNow.add(balPenalty));
							}
						}
					}

				} else if (repayTo == RepayConstants.REPAY_OTHERS) {

					// If Schedule has Unpaid Fee Amount
					if ((curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())).compareTo(BigDecimal.ZERO) > 0) {

						// Fee Detail Collection
						for (int k = 0; k < scheduleData.getFinFeeDetailList().size(); k++) {
							FinFeeDetail feeSchd = scheduleData.getFinFeeDetailList().get(k);
							if (StringUtils.equals(feeSchd.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)) {
								continue;
							}
							List<FinFeeScheduleDetail> feeSchdList = feeSchd.getFinFeeScheduleDetailList();

							// No more Receipt amount left for next schedules
							if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
								totalReceiptAmt = BigDecimal.ZERO;
								break;
							}

							if (feeSchdList == null || feeSchdList.isEmpty()) {
								continue;
							}

							// If Schedule Fee terms are less than actual
							// calculated Sequence
							if (feeSchdList.size() < lastRenderSeq) {
								continue;
							}

							// Calculate and set Fee Amount based on Fee ID
							for (int l = lastRenderSeq; l < feeSchdList.size(); l++) {
								if (feeSchdList.get(l).getSchDate().compareTo(schdDate) == 0) {

									BigDecimal balFee = feeSchdList.get(l).getSchAmount()
											.subtract(feeSchdList.get(l).getPaidAmount());
									if (balFee.compareTo(BigDecimal.ZERO) > 0) {
										if (totalReceiptAmt.compareTo(balFee) > 0) {
											totalReceiptAmt = totalReceiptAmt.subtract(balFee);
										} else {
											balFee = totalReceiptAmt;
											totalReceiptAmt = BigDecimal.ZERO;
										}

										BigDecimal totFeePayNow = BigDecimal.ZERO;
										if (allocatePaidMap.containsKey(
												RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID())) {
											totFeePayNow = allocatePaidMap
													.get(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID());
											allocatePaidMap
													.remove(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID());
										}
										allocatePaidMap.put(RepayConstants.ALLOCATION_FEE + "_" + feeSchd.getFeeID(),
												totFeePayNow.add(balFee));
									}
									if (lastRenderSeq == 0) {
										lastRenderSeq = l;
									}
									break;
								}
							}
						}
					}

					// If Schedule has Unpaid Fee Amount
					if ((curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())).compareTo(BigDecimal.ZERO) > 0) {

						// Insurance Details Collection
						for (int k = 0; k < scheduleData.getFinInsuranceList().size(); k++) {
							FinInsurances insSchd = scheduleData.getFinInsuranceList().get(k);
							List<FinSchFrqInsurance> insSchdList = insSchd.getFinSchFrqInsurances();

							// No more Receipt amount left for next schedules
							if (totalReceiptAmt.compareTo(BigDecimal.ZERO) <= 0) {
								totalReceiptAmt = BigDecimal.ZERO;
								break;
							}

							if (insSchdList == null || insSchdList.isEmpty()) {
								continue;
							}

							// Calculate and set Fee Amount based on Fee ID
							for (int l = 0; l < insSchdList.size(); l++) {
								if (insSchdList.get(l).getInsSchDate().compareTo(schdDate) == 0) {

									BigDecimal balIns = insSchdList.get(l).getAmount()
											.subtract(insSchdList.get(l).getInsurancePaid());
									if (balIns.compareTo(BigDecimal.ZERO) > 0) {
										if (totalReceiptAmt.compareTo(balIns) > 0) {
											totalReceiptAmt = totalReceiptAmt.subtract(balIns);
										} else {
											balIns = totalReceiptAmt;
											totalReceiptAmt = BigDecimal.ZERO;
										}

										BigDecimal totInsPayNow = BigDecimal.ZERO;
										if (allocatePaidMap.containsKey(
												RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId())) {
											totInsPayNow = allocatePaidMap
													.get(RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId());
											allocatePaidMap
													.remove(RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId());
										}
										allocatePaidMap.put(RepayConstants.ALLOCATION_INS + "_" + insSchd.getInsId(),
												totInsPayNow.add(balIns));
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
		if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_TDS)) {
			tdsAmount = allocatePaidMap.get(RepayConstants.ALLOCATION_TDS);
		}
		if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_PFT)) {
			pftAmount = allocatePaidMap.get(RepayConstants.ALLOCATION_PFT);
		}
		// Net Interest Amount Setting Excluding TDS
		allocatePaidMap.put(RepayConstants.ALLOCATION_NPFT, pftAmount.subtract(tdsAmount));

		// Set Penalty Payments for Prepared Past due Schedule Details
		if (seperatePenalties && totalReceiptAmt.compareTo(BigDecimal.ZERO) > 0) {

			if (!overdueMap.isEmpty()) {
				List<Date> odDateList = new ArrayList<Date>(overdueMap.keySet());
				Collections.sort(odDateList);
				for (int i = 0; i < odDateList.size(); i++) {

					FinODDetails overdue = overdueMap.get(odDateList.get(i));
					if (overdue != null && DateUtility.compare(overdue.getFinODSchdDate(), valueDate) <= 0) {
						BigDecimal balPenalty = overdue.getTotPenaltyBal();
						if (balPenalty.compareTo(BigDecimal.ZERO) > 0) {
							if (totalReceiptAmt.compareTo(balPenalty) > 0) {
								totalReceiptAmt = totalReceiptAmt.subtract(balPenalty);
							} else {
								balPenalty = totalReceiptAmt;
								totalReceiptAmt = BigDecimal.ZERO;
							}

							BigDecimal totPenaltyPayNow = BigDecimal.ZERO;
							if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_ODC)) {
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
				if (!adviseMap.isEmpty()) {
					
					// Calculate total GST percentage
					Map<String, BigDecimal> taxPercmap = getTaxPercentages(financeDetail);
					
					BigDecimal cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
					BigDecimal sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
					BigDecimal ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
					BigDecimal igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);
					BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);
					
					String taxApplicable = SysParamUtil.getValueAsString("BOUNCE_TAX_APPLICABLE");
					String taxType = SysParamUtil.getValueAsString("BOUNCE_TAX_TYPE");
					
					List<Long> adviseIdList = new ArrayList<Long>(adviseMap.keySet());
					Collections.sort(adviseIdList);
					for (int i = 0; i < adviseIdList.size(); i++) {

						ManualAdvise advise = adviseMap.get(adviseIdList.get(i));
						if (advise != null) {
							BigDecimal balAdvise = advise.getAdviseAmount().subtract(advise.getPaidAmount())
									.subtract(advise.getWaivedAmount());
							
							// In case of GST is Exclusive then GST amount should add before payment collection
							boolean addGSTAmount = false;
							if((advise.getBounceID() == 0 && advise.isTaxApplicable() &&
									StringUtils.equals(advise.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)) ||
									(advise.getBounceID() > 0 && StringUtils.equals(taxApplicable, PennantConstants.YES) &&
									StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE))){
								addGSTAmount = true;
							}
							
							if(addGSTAmount){
								
								BigDecimal gstAmount = BigDecimal.ZERO;
								if(cgstPerc.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal cgst =  (balAdvise.multiply(cgstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
									cgst = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
									gstAmount = gstAmount.add(cgst);
								}
								if(sgstPerc.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal sgst =  (balAdvise.multiply(sgstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
									sgst = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
									gstAmount = gstAmount.add(sgst);
								}
								if(ugstPerc.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal ugst =  (balAdvise.multiply(ugstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
									ugst = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
									gstAmount = gstAmount.add(ugst);
								}
								if(igstPerc.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal igst =  (balAdvise.multiply(igstPerc)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
									igst = CalculationUtil.roundAmount(gstAmount, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
									gstAmount = gstAmount.add(igst);
								}
								
								balAdvise = balAdvise.add(gstAmount);
							}

							if (balAdvise.compareTo(BigDecimal.ZERO) > 0) {
								if (totalReceiptAmt.compareTo(balAdvise) > 0) {
									totalReceiptAmt = totalReceiptAmt.subtract(balAdvise);
								} else {
									balAdvise = totalReceiptAmt;
									totalReceiptAmt = BigDecimal.ZERO;
								}

								BigDecimal totAdvisePayNow = BigDecimal.ZERO;
								if (advise.getBounceID() > 0) {
									if (allocatePaidMap.containsKey(RepayConstants.ALLOCATION_BOUNCE)) {
										totAdvisePayNow = allocatePaidMap.get(RepayConstants.ALLOCATION_BOUNCE);
										allocatePaidMap.remove(RepayConstants.ALLOCATION_BOUNCE);
									}
									allocatePaidMap.put(RepayConstants.ALLOCATION_BOUNCE,
											totAdvisePayNow.add(balAdvise));

									if(!StringUtils.equals(taxApplicable, PennantConstants.YES)){
										continue;
									}

								} else {
									if (allocatePaidMap.containsKey(
											RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID())) {
										totAdvisePayNow = allocatePaidMap
												.get(RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID());
										allocatePaidMap
										.remove(RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID());
									}
									allocatePaidMap.put(RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID(),
											totAdvisePayNow.add(balAdvise));

									// Calculation Receivable Advises
									if(!advise.isTaxApplicable()){
										continue;
									}
								}

								// GST Calculation
								BigDecimal percentage = (totalGSTPerc.add(new BigDecimal(100))).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
								BigDecimal actualAmt = balAdvise.divide(percentage, 9, RoundingMode.HALF_DOWN);
								actualAmt = CalculationUtil.roundAmount(actualAmt, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
								BigDecimal actTaxAmount = balAdvise.subtract(actualAmt);

								BigDecimal gstAmount = BigDecimal.ZERO;
								if(cgstPerc.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal cgst = (actTaxAmount.multiply(cgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
									cgst = CalculationUtil.roundAmount(cgst, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
									gstAmount = gstAmount.add(cgst);
								}
								if(sgstPerc.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal sgst = (actTaxAmount.multiply(sgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
									sgst = CalculationUtil.roundAmount(sgst, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
									gstAmount = gstAmount.add(sgst);
								}
								if(ugstPerc.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal ugst = (actTaxAmount.multiply(ugstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
									ugst = CalculationUtil.roundAmount(ugst, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
									gstAmount = gstAmount.add(ugst);
								}
								if(igstPerc.compareTo(BigDecimal.ZERO) > 0){
									BigDecimal igst = (actTaxAmount.multiply(igstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
									igst = CalculationUtil.roundAmount(igst, financeMain.getCalRoundingMode(),financeMain.getRoundingTarget());
									gstAmount = gstAmount.add(igst);
								}

								if (advise.getBounceID() > 0) {
									BigDecimal totalGST = BigDecimal.ZERO;
									if(StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)){
										if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_BOUNCE +"_GST_E")){
											totalGST = allocatePaidMap.get(RepayConstants.ALLOCATION_BOUNCE +"_GST_E");
										}
										allocatePaidMap.put(RepayConstants.ALLOCATION_BOUNCE +"_GST_E", totalGST.add(gstAmount));
									}else if(StringUtils.equals(taxType, FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)){
										if(allocatePaidMap.containsKey(RepayConstants.ALLOCATION_BOUNCE +"_GST_I")){
											totalGST = allocatePaidMap.get(RepayConstants.ALLOCATION_BOUNCE +"_GST_I");
										}
										allocatePaidMap.put(RepayConstants.ALLOCATION_BOUNCE +"_GST_I", totalGST.add(gstAmount));
									}
								}else{

									if(StringUtils.equals(advise.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE)){
										allocatePaidMap.put(RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID()+"_GST_E", gstAmount);
									}else if(StringUtils.equals(advise.getTaxComponent(), FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE)){
										allocatePaidMap.put(RepayConstants.ALLOCATION_MANADV + "_" + advise.getAdviseID()+"_GST_I", gstAmount);
									}
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
	
	/**
	 * Method for Preparing all GST fee amounts based on configurations
	 * @param manAdvList
	 * @param financeDetail
	 * @return
	 */
	public Map<String, BigDecimal> getTaxPercentages(FinanceDetail financeDetail){
		
		// Set Tax Details if Already exists
		if(financeDetail.getFinanceTaxDetails() == null){
			financeDetail.setFinanceTaxDetails(financeTaxDetailDAO.getFinanceTaxDetail(
					financeDetail.getFinScheduleData().getFinanceMain().getFinReference(), ""));
		}
		
		// Customer Details			
		if (financeDetail.getCustomerDetails() == null) {
			financeDetail.setCustomerDetails(customerDetailsService.getCustomerDetailsById(
					financeDetail.getFinScheduleData().getFinanceMain().getCustID(), true, "_View"));
		}
		
		// Map Preparation for Executing GST rules
		HashMap<String, Object> dataMap = finFeeDetailService.prepareGstMappingDetails(financeDetail, null);
		
		List<Rule> rules = ruleDAO.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");
		String finCcy = financeDetail.getFinScheduleData().getFinanceMain().getFinCcy();
		
		BigDecimal totalTaxPerc = BigDecimal.ZERO;
		Map<String, BigDecimal> taxPercMap = new HashMap<>();
		
		for (Rule rule : rules) {
			BigDecimal taxPerc = BigDecimal.ZERO;
			if (StringUtils.equals(RuleConstants.CODE_CGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_CGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_IGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_IGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_SGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_SGST, taxPerc);
			} else if (StringUtils.equals(RuleConstants.CODE_UGST, rule.getRuleCode())) {
				taxPerc = getRuleResult(rule.getSQLRule(), dataMap, finCcy);
				totalTaxPerc = totalTaxPerc.add(taxPerc);
				taxPercMap.put(RuleConstants.CODE_UGST, taxPerc);
			}
		}
		taxPercMap.put("TOTALGST", totalTaxPerc);
		
		return taxPercMap;
	}
	
	/**
	 * Method for Processing of SQL Rule and get Executed Result
	 * 
	 * @return
	 */
	private BigDecimal getRuleResult(String sqlRule, HashMap<String, Object> executionMap,String finCcy) {
		logger.debug("Entering");
		
		BigDecimal result = BigDecimal.ZERO;
		try {
			Object exereslut = this.ruleExecutionUtil.executeRule(sqlRule, executionMap, finCcy, RuleReturnType.DECIMAL);
			if (exereslut == null || StringUtils.isEmpty(exereslut.toString())) {
				result = BigDecimal.ZERO;
			} else {
				result = new BigDecimal(exereslut.toString());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		
		logger.debug("Leaving");
		return result;
	}
	
	/**
	 * Method for calculating GST percentage Values
	 */
	private BigDecimal calculateTaxAmount(BigDecimal amount, BigDecimal totalTaxPerc, BigDecimal taxPerc, String roundingMode, int roundingtarget){
		
		if(amount.compareTo(BigDecimal.ZERO) == 0 || taxPerc.compareTo(BigDecimal.ZERO) == 0){
			return BigDecimal.ZERO;
		}
		
		BigDecimal gstAmount = (amount.multiply(taxPerc)).divide(totalTaxPerc, 9, RoundingMode.HALF_DOWN);
		gstAmount = CalculationUtil.roundAmount(gstAmount, roundingMode, roundingtarget);
		
		return gstAmount;
	}
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	
}
