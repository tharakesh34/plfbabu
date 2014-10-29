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
 * FileName : ScheduleCalculator.java *
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.model.RateDetail;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.util.PennantConstants;
import com.rits.cloning.Cloner;

public class ScheduleCalculator {

	private final static Logger logger = Logger.getLogger(ScheduleCalculator.class);

	private BigDecimal totalGrossGraceProfit = BigDecimal.ZERO;
	private BigDecimal totalGrossRepayProfit = BigDecimal.ZERO;
	private BigDecimal pftForSelectedPeriod = BigDecimal.ZERO;

	private BigDecimal totalGraceCpz = BigDecimal.ZERO;
	private BigDecimal totalRepayCpz = BigDecimal.ZERO;
	private BigDecimal totalRepayAmt = BigDecimal.ZERO;
	private BigDecimal totalPrincipal = BigDecimal.ZERO;
	private BigDecimal firstAdjAmount = BigDecimal.ZERO;
	private BigDecimal lastAdjAmount = BigDecimal.ZERO;
	private BigDecimal expectedResult = BigDecimal.ZERO;
	private BigDecimal comparisionAmount = BigDecimal.ZERO;
	private BigDecimal comparisionToAmount = BigDecimal.ZERO;
	private BigDecimal defPrincipalBal = BigDecimal.ZERO;
	private BigDecimal defProfitBal = BigDecimal.ZERO;
	private BigDecimal totalEarlyPaidBal = BigDecimal.ZERO;
	private BigDecimal number2 = new BigDecimal(2);

	private boolean isExactMatch = false;
	private boolean isRepayComplete = false;
	private boolean isFirstAdjSet;
	private boolean isLastAdjSet;
	private boolean isCompareToExpected = false;
	private boolean isProtectSchdPft = false;

	private int schdIndex = 0;
	private int curTerm = 0;
	private int indexStart = 0;
	private int indexEnd = 0;
	private int newMaturityIndex = 0;

	private Date recalStartDate = new Date();
	private Date recalEndDate = new Date();
	private Date lastRepayDate = null;
	private Date curBussniessDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
	private String defermentMethod = SystemParameterDetails.getSystemParameterValue("DEF_METHOD").toString();
	
	private FinScheduleData finScheduleData;
	
	

	/*
	 * ################################################################################################################
	 * 
	 * 
	 * ################################################################################################################
	 */

	public static FinScheduleData getCalSchd(FinScheduleData finScheduleData) {
		return new ScheduleCalculator("procGetCalSchd", finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData changeRate(FinScheduleData finScheduleData, String baseRate,
	        String splRate, BigDecimal mrgRate, BigDecimal calculatedRate, boolean isCalSchedule) {
		return new ScheduleCalculator("procChangeRate", finScheduleData, baseRate, splRate,
		        mrgRate, calculatedRate, isCalSchedule).getFinScheduleData();
	}

	public static FinScheduleData changeRepay(FinScheduleData finScheduleData, BigDecimal amount,
	        String schdMethod) {
		return new ScheduleCalculator("procChangeRepay", finScheduleData, amount, schdMethod, null)
		        .getFinScheduleData();
	}

	public static FinScheduleData changeProfit(FinScheduleData finScheduleData,
	        BigDecimal desiredPftAmount) {
		return new ScheduleCalculator("procChangeProfit", finScheduleData, desiredPftAmount)
		        .getFinScheduleData();
	}

	public static FinScheduleData addDisbursement(FinScheduleData finScheduleData,
	        BigDecimal amount, String schdMethod, BigDecimal feeChargeAmt) {
		return new ScheduleCalculator("procAddDisbursement", finScheduleData, amount, schdMethod,
		        feeChargeAmt).getFinScheduleData();
	}
	
	public static FinScheduleData addDatedSchedule(FinScheduleData finScheduleData) {
		return new ScheduleCalculator("procAddDatedSchedule", finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData addRepayment(FinScheduleData finScheduleData, BigDecimal amount,
	        String schdMethod) {
		return new ScheduleCalculator("procAddRepayment", finScheduleData, amount, schdMethod, null)
		        .getFinScheduleData();
	}

	public static FinScheduleData reCalSchd(FinScheduleData finScheduleData) {
		return new ScheduleCalculator("procReCalSchd", finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData addTerm(FinScheduleData finScheduleData, int noOfTerms,
	        String schdMethod) {
		return new ScheduleCalculator("procAddTerm", finScheduleData, noOfTerms, schdMethod)
		        .getFinScheduleData();
	}

	public static FinScheduleData deleteTerm(FinScheduleData finScheduleData) {
		return new ScheduleCalculator("procDeleteTerm", finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData addDeferment(FinScheduleData finScheduleData) {
		return new ScheduleCalculator("procAddDeferment", finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData rmvDeferment(FinScheduleData finScheduleData) {
		return new ScheduleCalculator("procRmvDeferment", finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData refreshRates(FinScheduleData finScheduleData) {
		return new ScheduleCalculator("procRefreshRates", finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData recalEarlyPaySchedule(FinScheduleData finScheduleData,
	        Date earlyPayOnSchdl, Date earlyPayOnNextSchdl, BigDecimal earlyPayAmt, String method) {
		return new ScheduleCalculator(finScheduleData, earlyPayOnSchdl, earlyPayOnNextSchdl,
		        earlyPayAmt, method).getFinScheduleData();
	}

	public static FinScheduleData addSubSchedule(FinScheduleData finScheduleData, int noOfTerms,
	        Date subSchStartDate, String frqNewSchd) {
		return new ScheduleCalculator("procSubSchedule", finScheduleData, noOfTerms,
		        subSchStartDate, frqNewSchd).getFinScheduleData();
	}
	
	public static FinScheduleData getCalERR(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(finScheduleData, "calEffectiveRate").getFinScheduleData();
	}

	// Constructors
	private ScheduleCalculator(String method, FinScheduleData finScheduleData) {
		logger.debug("Entering");
		
		finScheduleData.getFinanceMain().setCpzAtGraceEnd(false);
		
		if (method.equals("procGetCalSchd")) {
			
			BigDecimal totalDesiredProfit =   BigDecimal.ZERO;
			String pftComparisionFor = null;
			BigDecimal repayAmount =   BigDecimal.ZERO;
								
			//IF Repayments is Convert from Flat to Reducing and first time calculation.
			Boolean isCalFlat = false;
			if (finScheduleData.getFinanceMain().getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_C)) {
				isCalFlat = true;
			}

			if (finScheduleData.getFinanceMain().getGrcProfitDaysBasis() == null) {
				finScheduleData.getFinanceMain().setGrcProfitDaysBasis(finScheduleData.getFinanceMain().getProfitDaysBasis());
			}
			
			finScheduleData = procGetCalSchd(finScheduleData, isCalFlat);

			if (isCalFlat) {
				
				Date calStart = new Date();
				finScheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_TILLMDT);
				FinanceMain financeMain = finScheduleData.getFinanceMain();
				
				totalDesiredProfit = financeMain.getTotalGrossPft();
				if (financeMain.isAllowGrcPeriod() && StringUtils.trimToEmpty(financeMain.getGrcRateBasis()).equals(CalculationConstants.RATE_BASIS_R)
				        && financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_C)) {
					
					calStart = financeMain.getFinStartDate();
					pftComparisionFor = CalculationConstants.TOTAL;
				
				} else {
					calStart = financeMain.getGrcPeriodEndDate();
					pftComparisionFor = CalculationConstants.REPAY;
				}				
				
				//Set Total Amounts After Calculations
				finScheduleData = setFinanceTotals(finScheduleData);

				if (financeMain.isEqualRepay()) {
					
					int iRepayStart = financeMain.getGraceTerms()+1;
					
					repayAmount = financeMain.getFinAmount().add(totalDesiredProfit)
					        .divide(BigDecimal.valueOf(financeMain.getNumberOfTerms()), 0, RoundingMode.HALF_DOWN);

					finScheduleData = setRpyInstructDetails(finScheduleData, 
							finScheduleData.getFinanceScheduleDetails().get(iRepayStart).getSchDate(), 
							financeMain.getCalMaturity(), repayAmount, 
					        finScheduleData.getFinanceScheduleDetails().get(iRepayStart).getSchdMethod());

					if (financeMain.isStepFinance()) {

						finScheduleData.getFinanceMain().setEventFromDate(finScheduleData.getFinanceMain().getFinStartDate());
						finScheduleData.getFinanceMain().setEventToDate(finScheduleData.getFinanceMain().getMaturityDate());				

						finScheduleData = calEffectiveRate(finScheduleData, pftComparisionFor,totalDesiredProfit, 
								finScheduleData.getFinanceMain().getFinStartDate(), finScheduleData.getFinanceMain().getCalMaturity(), false);

						int size = finScheduleData.getFinanceScheduleDetails().size();
						for (int i = 0; i < size; i++) {
							FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
							curSchd.setOrgEndBal(curSchd.getClosingBalance());
							curSchd.setOrgPft(curSchd.getProfitSchd());
							curSchd.setOrgPri(curSchd.getPrincipalSchd());
						}

						//Setting Repay Instructions using Step Policy Details List
						int stepSize = finScheduleData.getStepPolicyDetails().size();
						BigDecimal stepAmount = BigDecimal.ZERO;
						int iRepayEnd = 0;
						
						//Sorting Step Details as of Step Order / Step Number
						
						for (int i = 0; i < stepSize; i++) {
							
							FinanceStepPolicyDetail policyDetail = finScheduleData.getStepPolicyDetails().get(i);
							iRepayEnd = iRepayStart + policyDetail.getInstallments();
							stepAmount = repayAmount.multiply(policyDetail.getEmiSplitPerc()).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
							policyDetail.setSteppedEMI(stepAmount);
							
							finScheduleData = setRpyInstructDetails(finScheduleData, finScheduleData
									.getFinanceScheduleDetails().get(iRepayStart).getSchDate(), finScheduleData
									.getFinanceScheduleDetails().get(iRepayEnd-1).getSchDate(), 
									stepAmount, finScheduleData.getFinanceScheduleDetails().get(iRepayStart).getSchdMethod());
							
							iRepayStart = iRepayEnd;
						}
						
					} 
				}			

				finScheduleData.getFinanceMain().setEventFromDate(finScheduleData.getFinanceMain().getFinStartDate());
				finScheduleData.getFinanceMain().setEventToDate(finScheduleData.getFinanceMain().getMaturityDate());				

				finScheduleData = calEffectiveRate(finScheduleData, pftComparisionFor, totalDesiredProfit, calStart, 
						finScheduleData.getFinanceMain().getCalMaturity(), false);
				
				if (financeMain.isStepFinance()) {
					int size = finScheduleData.getFinanceScheduleDetails().size();
					BigDecimal orgPft =   BigDecimal.ZERO;
                	for (int i = 0; i < size; i++) {
                		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
                		orgPft = curSchd.getOrgPft();
                		curSchd.setOrgPft(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()).subtract(orgPft));
                		curSchd.setOrgPri(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()).subtract(curSchd.getOrgPri()).subtract(orgPft));
                    }
				}
			}

			//Effective Rate Of Return Calculations
			if (finScheduleData.getFinanceMain().getTotalProfit().compareTo(BigDecimal.ZERO) > 0) {
				Cloner cloner = new Cloner();
				FinScheduleData orgFinScheduleData = cloner.deepClone(finScheduleData);
				
				isProtectSchdPft = true;
				//SET START AND DATES FOR EFFECTIVE RATE CALCULATION
				orgFinScheduleData.getFinanceMain().setEventFromDate(orgFinScheduleData.getFinanceMain().getFinStartDate());
				orgFinScheduleData.getFinanceMain().setEventToDate(orgFinScheduleData.getFinanceMain().getMaturityDate());
				
				FinanceMain finMain = orgFinScheduleData.getFinanceMain();				
				orgFinScheduleData = calEffectiveRate(orgFinScheduleData,CalculationConstants.TOTAL, 
						(finMain.getTotalProfit().add(finMain.getTotalCpz())), finMain.getFinStartDate(), finMain.getMaturityDate(), true);

				finScheduleData.getFinanceMain().setEffectiveRateOfReturn(orgFinScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate());
			} else {
				finScheduleData.getFinanceMain().setEffectiveRateOfReturn(BigDecimal.ZERO);
			}

			setFinScheduleData(finScheduleData);
		}

		if (method.equals("procReCalSchd")) {
			setFinScheduleData(procReCalSchd(finScheduleData));
		}
		
		if (method.equals("procAddDatedSchedule")) {
			setFinScheduleData(procAddDatedSchedule(finScheduleData));
		}

		if (method.equals("procAddDeferment")) {
			if (defermentMethod.equals(PennantConstants.DEF_METHOD_RECALRATE)) {
				finScheduleData.getFinanceMain().setPftIntact(true);
				if (finScheduleData.getFinanceMain().getRecalType()
				        .equals(CalculationConstants.RPYCHG_TILLMDT)) {
					finScheduleData.getFinanceMain().setRecalToDate(finScheduleData.getFinanceMain().getMaturityDate());
				} else if (finScheduleData.getFinanceMain().getRecalType()
				        .equals(CalculationConstants.RPYCHG_ADDTERM)
				        || finScheduleData.getFinanceMain().getRecalType()
				                .equals(CalculationConstants.RPYCHG_ADDRECAL)) {
					finScheduleData.getFinanceMain().setAdjTerms(1);;
				}
		
				setFinScheduleData(procChangeRepay(finScheduleData, BigDecimal.ZERO, finScheduleData.getFinanceMain().getScheduleMethod()));
            } else {
            	setFinScheduleData(procAddDeferment(finScheduleData));
			}
		}

		if (method.equals("procRmvDeferment")) {
			setFinScheduleData(procRmvDeferment(finScheduleData));
		}

		if (method.equals("procDeleteTerm")) {
			setFinScheduleData(procDeleteTerm(finScheduleData));
		}

		if (method.equals("procRefreshRates")) {
			setFinScheduleData(procRefreshRates(finScheduleData));
		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, String baseRate,
	        String splRate, BigDecimal mrgRate, BigDecimal calculatedRate, boolean isCalSchedule) {
		logger.debug("Entering");

		if (method.equals("procChangeRate")) {

			finScheduleData = procChangeRate(finScheduleData, baseRate, splRate, mrgRate,
			        calculatedRate, isCalSchedule, false);

			//Effective Rate Of Return Calculations
			Cloner cloner = new Cloner();
			FinScheduleData orgFinScheduleData = cloner.deepClone(finScheduleData);
			
			FinanceMain finMain = orgFinScheduleData.getFinanceMain();
			
			orgFinScheduleData = calEffectiveRate(orgFinScheduleData, CalculationConstants.TOTAL,
			        (finMain.getTotalProfit().add(finMain.getTotalCpz())), finMain.getFinStartDate(), finMain.getMaturityDate(), true);

			finScheduleData.getFinanceMain().setEffectiveRateOfReturn(
			        orgFinScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate());
			finScheduleData.getFinanceMain().setScheduleMaintained(true);
			setFinScheduleData(finScheduleData);

		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal amount,
	        String schdMethod, BigDecimal feeChargeAmt) {
		logger.debug("Entering");

		if (method.equals("procChangeRepay")) {
			setFinScheduleData(procChangeRepay(finScheduleData, amount, schdMethod));
		}

		if (method.equals("procAddRepayment")) {
			setFinScheduleData(procAddRepayment(finScheduleData, amount, schdMethod));
		}

		if (method.equals("procAddDisbursement")) {
			setFinScheduleData(procAddDisbursement(finScheduleData, amount, schdMethod,
			        feeChargeAmt));
		}
		logger.debug("Leaving");
	}
	
	private ScheduleCalculator(FinScheduleData finScheduleData,String method) {
		logger.debug("Entering");

		if (method.equals("calEffectiveRate")) {
			setFinScheduleData(calEffectiveRate(finScheduleData, CalculationConstants.TOTAL, finScheduleData.getFinanceMain().getTotalGrossPft(),
					finScheduleData.getFinanceMain().getFinStartDate(), finScheduleData.getFinanceMain().getMaturityDate(), true));
		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, int noOfTerms,
	        String schdMethod) {
		logger.debug("Entering");
		if (method.equals("procAddTerm")) {
			setFinScheduleData(procAddTerm(finScheduleData, noOfTerms, schdMethod, true));
		}
		logger.debug("Leaving");
	}

	
	private ScheduleCalculator(String method, FinScheduleData finScheduleData,
	        BigDecimal desiredPftAmount) {
		logger.debug("Entering");
		if (method.equals("procChangeProfit")) {
			setFinScheduleData(procChangeProfit(finScheduleData, desiredPftAmount));
		}
		
		logger.debug("Leaving");
	}

	private ScheduleCalculator(FinScheduleData finScheduleData, Date earlyPayOnSchdl,
	        Date earlyPayOnNextSchdl, BigDecimal earlyPayAmt, String method) {
		logger.debug("Entering");

		finScheduleData.getFinanceMain().setEventFromDate(earlyPayOnSchdl);
		finScheduleData.getFinanceMain().setEventToDate(earlyPayOnSchdl);
		finScheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_ADJMDT);

		if (CalculationConstants.EARLYPAY_ADJMUR.equals(method)
		        || CalculationConstants.EARLYPAY_ADMPFI.equals(method)) {

			finScheduleData.getFinanceMain().setRecalToDate(finScheduleData.getFinanceMain().getMaturityDate());
			final BigDecimal totalDesiredProfit = finScheduleData.getFinanceMain().getTotalGrossPft();
			finScheduleData = ScheduleCalculator.changeRepay(finScheduleData, earlyPayAmt,finScheduleData.getFinanceMain().getScheduleMethod());
			
			List<FinanceScheduleDetail> finSchdlDetailList = finScheduleData.getFinanceScheduleDetails();
			int size = finScheduleData.getFinanceScheduleDetails().size();
			Date eventToDate = finScheduleData.getFinanceMain().getMaturityDate();
			for (int i = size - 1; i >= 0; i--) {
				FinanceScheduleDetail schDetail = finSchdlDetailList.get(i);
				if ((schDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0)
				        && (schDetail.getDefProfitSchd().compareTo(BigDecimal.ZERO) == 0)
				        && (schDetail.getDefPrincipalSchd().compareTo(BigDecimal.ZERO) == 0)) {
					finSchdlDetailList.remove(i);
				} else {
					eventToDate = schDetail.getSchDate();
					break;
				}
			}
			
			finScheduleData.getFinanceMain().setMaturityDate(eventToDate);

			if (CalculationConstants.EARLYPAY_ADMPFI.equals(method)) {

				finScheduleData.getFinanceMain().setEventToDate(eventToDate);
				//Apply Effective Rate for ReSchedule to get Desired Profit
				finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.TOTAL, totalDesiredProfit, null, null, false);

				//Set Total Amounts After Calculations
				finScheduleData = setFinanceTotals(finScheduleData);
						
				finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData
				        .getFinanceScheduleDetails()));
			}

		} else if (CalculationConstants.EARLYPAY_RECRPY.equals(method)) {

			finScheduleData.getFinanceMain().setRecalToDate(
			        finScheduleData.getFinanceMain().getMaturityDate());

			//Schedule Repayment Change
			finScheduleData = ScheduleCalculator.changeRepay(finScheduleData, earlyPayAmt,
			        finScheduleData.getFinanceMain().getScheduleMethod());

			//Schedule ReCalculations afetr Early Repayment Period based upon Schedule Method
			finScheduleData.getFinanceMain().setEventFromDate(earlyPayOnNextSchdl);
			finScheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			finScheduleData = ScheduleCalculator.reCalSchd(finScheduleData);

			//finScheduleData.getFinanceScheduleDetails().addAll(tempSchdlDetailList);

		} else if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {

			final BigDecimal totalDesiredProfit = finScheduleData.getFinanceMain().getTotalGrossPft();

			finScheduleData.getFinanceMain().setRecalToDate(
			        finScheduleData.getFinanceMain().getMaturityDate());

			//Apply Effective Rate for ReSchedule to get Desired Profit
			finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.TOTAL, totalDesiredProfit,
					null, finScheduleData.getFinanceMain().getMaturityDate(), false);

			//Set Total Amounts After Calculations
			finScheduleData = setFinanceTotals(finScheduleData);
		}

		setFinScheduleData(finScheduleData);
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, int noOfTerms,
	        Date subSchStartDate, String frqNewSchd) {
		logger.debug("Entering");
		if (method.equals("procSubSchedule")) {
			setFinScheduleData(procSubSchedule(finScheduleData, noOfTerms, subSchStartDate,
			        frqNewSchd));
		}
		logger.debug("Leaving");
	}

	/*
	 * ################################################################################################################
	 * MAIN METHODS
	 * ################################################################################################################
	 */

	/*
	 * ================================================================================================================
	 * Method 		: procGetCalSchd 
	 * Description 	: GET CALCULATED SCHEDULE 
	 * Process 		:
	 * ================================================================================================================
	 */

	private FinScheduleData procGetCalSchd(FinScheduleData finScheduleData, Boolean isCalFlat) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		// Set Defered scheduled date and schedule method first time
		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
				if (!finScheduleData.getFinanceMain().isAllowGrcRepay()) {
					curSchd.setSchdMethod(CalculationConstants.EQUAL);
				} else {
					curSchd.setSchdMethod(financeMain.getGrcSchdMthd());
				}
			} else {
				curSchd.setSchdMethod(financeMain.getScheduleMethod());
			}

		}

		// Set Grace Period Details
		if (finScheduleData.getFinanceScheduleDetails().get(0).getSchDate()
		        .compareTo(financeMain.getGrcPeriodEndDate()) != 0) {
			finScheduleData = setGraceDetails(finScheduleData);
		}

		// Set Repayment Period Details
		finScheduleData = setRepayDetails(finScheduleData);

		// Call CALCULATE SCHEDULE PROCESS
		if (financeMain.isCalculateRepay()) {
			financeMain.setEqualRepay(true);
		}

		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		finScheduleData = calSchdProcess(finScheduleData, isCalFlat);

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/*
	 * ================================================================================================================
	 * Method 		: procAddDatedSchedule 
	 * Description 	: Insert dated ScheduleTerm & Re Calculate schedule from a given date to end date 
	 * Process 		: Should Add the New Scheduled term and recalculate repay amounts From Scheduled Term
	 * ================================================================================================================
	 */
	private FinScheduleData procAddDatedSchedule(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		Date newScheduleDate = financeMain.getEventFromDate();
		
		//insert new Schedule Dated term
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		FinanceScheduleDetail prvSchd = null;
		for (int i = 1; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			
			if(curSchd.getSchDate().compareTo(newScheduleDate) <= 0){
				
				if(curSchd.getSchDate().compareTo(newScheduleDate) == 0){
					finScheduleData.setErrorDetail(new ErrorDetails("SCH37",
							"Schedule Term with Mentioned Date is Already Exist", new String[] { " " }));
					return finScheduleData;
				}
				
				prvSchd = curSchd;
				continue;
			}
			
			boolean isAfterFirstTerm = false;
			if(prvSchd == null){
				prvSchd = finScheduleData.getFinanceScheduleDetails().get(0);
				isAfterFirstTerm = true;
			}
			
			FinanceScheduleDetail sd = new FinanceScheduleDetail();

			sd.setSchDate(newScheduleDate);
			sd.setDefSchdDate(newScheduleDate);
			sd.setRepayOnSchDate(true);
			sd.setRvwOnSchDate(prvSchd.isRvwOnSchDate());
			sd.setDisbOnSchDate(false);
			sd.setDownpaymentOnSchDate(false);
			sd.setBalanceForPftCal(BigDecimal.ZERO);
			sd.setNoOfDays(0);
			sd.setDayFactor(BigDecimal.ZERO);
			sd.setProfitCalc(BigDecimal.ZERO);
			sd.setProfitSchd(BigDecimal.ZERO);
			sd.setPrincipalSchd(BigDecimal.ZERO);
			sd.setRepayAmount(BigDecimal.ZERO);
			sd.setProfitBalance(BigDecimal.ZERO);
			sd.setDisbAmount(BigDecimal.ZERO);
			sd.setDownPaymentAmount(BigDecimal.ZERO);
			sd.setCpzAmount(BigDecimal.ZERO);
			sd.setClosingBalance(BigDecimal.ZERO);
			sd.setProfitFraction(BigDecimal.ZERO);
			
			sd.setPftOnSchDate(prvSchd.isPftOnSchDate());
			sd.setCpzOnSchDate(prvSchd.isCpzOnSchDate());
			sd.setBaseRate(prvSchd.getBaseRate());
			sd.setSplRate(prvSchd.getSplRate());
			sd.setMrgRate(prvSchd.getMrgRate());
			sd.setActRate(prvSchd.getActRate());
			sd.setCalculatedRate(prvSchd.getCalculatedRate());
			
			if(isAfterFirstTerm){
				sd.setPftOnSchDate(curSchd.isPftOnSchDate());
				sd.setCpzOnSchDate(curSchd.isCpzOnSchDate());
				sd.setRvwOnSchDate(curSchd.isRvwOnSchDate());
			}
			
			if(newScheduleDate.compareTo(financeMain.getGrcPeriodEndDate()) > 0){
				sd.setSpecifier(CalculationConstants.REPAY);
				sd.setSchdMethod(financeMain.getScheduleMethod());
			}else{
				sd.setSpecifier(CalculationConstants.GRACE);
				sd.setSchdMethod(financeMain.getGrcSchdMthd());
			}

			finScheduleData.getFinanceScheduleDetails().add(sd);
			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData
			        .getFinanceScheduleDetails()));
			financeMain.setNumberOfTerms(financeMain.getNumberOfTerms() + 1);
			break;
		}

		// Recalculate Schedule
		finScheduleData = procReCalSchd(finScheduleData);
		finScheduleData.getFinanceMain().setScheduleMaintained(true);
		
		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method 		: procReCalSchd 
	 * Description 	: Re Calculate schedule from a given date to end date 
	 * Process 		: Should change the repay amount and recalculate repay amounts of remaining
	 * ================================================================================================================
	 */
	private FinScheduleData procReCalSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		String recaltype = financeMain.getRecalType();
		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		Date evtFromDate = financeMain.getEventFromDate();
		Date evtToDate = evtFromDate;
		recalStartDate = null;
		recalEndDate = null;

		// financeMain = inzPrvRepayAmount(financeMain);

		// If recalculation type is TILL DATE and event to date is >= maturity
		// date then force it to TILLMDT
		if (evtToDate.compareTo(financeMain.getMaturityDate()) >= 0
		        && recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
			financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_ADJMDT)) {
			financeMain.setCalculateRepay(false);
			financeMain.setEqualRepay(false);
			recalStartDate = financeMain.getMaturityDate();
			recalEndDate = recalStartDate;
			isFirstAdjSet = true;
			isLastAdjSet = true;
			financeMain.setCalculateRepay(false);
		} else {
			financeMain.setCalculateRepay(true);
			financeMain.setEqualRepay(true);

			if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
				recalEndDate = financeMain.getRecalToDate();
				isLastAdjSet = true;
				isCompareToExpected = true;
			}

			if (recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				recalEndDate = financeMain.getMaturityDate();
				isLastAdjSet = true;
			}
		}

		// Setting repayments between Fromdate and Todate
		if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)
		        || recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				Date getSchdDate = new Date();
				getSchdDate = curSchd.getSchDate();

				// Setting first date after event end date
				if (getSchdDate.compareTo(evtToDate) >= 0 && curSchd.isRepayOnSchDate()) {

					if (!isFirstAdjSet) {
						isFirstAdjSet = true;
						if (i != (sdSize - 1)) {
							recalStartDate = curSchd.getSchDate();
						} else {
							recalStartDate = recalEndDate;
						}
					}
				}

				// Set expected result even for schedule method PROFIT
				if (curSchd.isRepayOnSchDate() && isCompareToExpected) {
					expectedResult = fetchCalAmount(curSchd);

				}
			}
		}

		if (!isFirstAdjSet) {
			recalStartDate = recalEndDate;
			isFirstAdjSet = true;
		}

		//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
		finScheduleData = calSchdProcess(finScheduleData, false);

		finScheduleData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method		: procAddDeferment
	 * Description 	: ADD DEFERMENTS
	 * Process 		:
	 * ================================================================================================================
	 */
	private FinScheduleData procAddDeferment(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String recaltype = financeMain.getRecalType();
		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;
		String schdMethod = CalculationConstants.ADDTERM_AFTMDT;

		BigDecimal deferedPrincipal = BigDecimal.ZERO;
		BigDecimal deferedProfit = BigDecimal.ZERO;

		Date evtFromDate = financeMain.getEventFromDate();
		Date evtToDate = evtFromDate;
		recalStartDate = null;
		recalEndDate = null;

		int defAdjTerms = 0;
		indexStart = 0;
		indexEnd = 0;

		// If recalculation type is TILL DATE and event to date is >= maturity
		// date then force it to TILLMDT
		if (evtToDate.compareTo(financeMain.getMaturityDate()) >= 0
		        && recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
			financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
			recalEndDate = financeMain.getRecalToDate();
			isLastAdjSet = true;

			isFirstAdjSet = true;
			recalStartDate = financeMain.getRecalFromDate();
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_ADJMDT)) {
			recalEndDate = financeMain.getRecalToDate();
			recalStartDate = recalEndDate;
			isLastAdjSet = true;
			isFirstAdjSet = true;
			defAdjTerms = 1;
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			recalEndDate = financeMain.getMaturityDate();
			isLastAdjSet = true;

			isFirstAdjSet = true;
			recalStartDate = financeMain.getRecalFromDate();
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_ADDTERM)) {
			recalEndDate = financeMain.getMaturityDate();
			recalStartDate = recalEndDate;
			isLastAdjSet = true;
			isFirstAdjSet = true;
			defAdjTerms = 1;
		}

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		// Set Repayment amount as zero for the defered dates
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (curSchd.getSchDate().compareTo(evtFromDate) == 0) {
				if (!curSchd.isRepayOnSchDate()) {
					financeMain.setNumberOfTerms(financeMain.getNumberOfTerms() + 1);
					curSchd.setRepayOnSchDate(true);
				}
				curSchd.setDefered(true);
				deferedPrincipal = curSchd.getPrincipalSchd();
				deferedProfit = curSchd.getProfitSchd();
				
				curSchd.setDefPrincipal(deferedPrincipal);
				curSchd.setDefProfit(deferedProfit);
				curSchd.setPrincipalSchd(BigDecimal.ZERO);
				curSchd.setProfitSchd(BigDecimal.ZERO);
				curSchd.setRepayAmount(BigDecimal.ZERO);

				finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtToDate,
				        BigDecimal.ZERO, curSchd.getSchdMethod());
			}

			// Find number of defered payment terms
			if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)
			        || recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {

				if (curSchd.getSchDate().compareTo(recalStartDate) >= 0
				        && curSchd.getSchDate().compareTo(recalEndDate) <= 0) {

					if (financeMain.isExcludeDeferedDates() && curSchd.isDefered()) {
						// Do Nothing
					} else {
						defAdjTerms = defAdjTerms + 1;
						indexEnd = i;
					}
				}

				if (curSchd.getSchDate().equals(recalStartDate)) {
					indexStart = i;
				}

			}
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_ADJMDT)) {
			indexStart = sdSize - 1;
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_ADDTERM)) {
			indexStart = sdSize;
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)
		        || recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			if (defAdjTerms == 0) {
				// Through Error
				finScheduleData.setErrorDetail(new ErrorDetails("SCH31", "DEFERED PAYMENT DATES CANNOT BE SET", new String[] { " " }));
				return finScheduleData;
			}
		}

		finScheduleData = setAddDeferment(finScheduleData, deferedPrincipal, deferedProfit, defAdjTerms, schdMethod);
		financeMain.setCalculateRepay(false);

		//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
		//finScheduleData = calSchdProcess(finScheduleData, false);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method 		: procRmvDeferment 
	 * Description 	: REMOVE DEFERMENTS 
	 * Process 		:
	 * ================================================================================================================
	 */

	private FinScheduleData procRmvDeferment(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		Date evtFromDate = financeMain.getEventFromDate();
		recalStartDate = null;
		recalEndDate = null;

		boolean isDeferRecordFound = false;
		BigDecimal defRpyAmount = BigDecimal.ZERO;

		// Remove Deferment Header Details
		DefermentHeader rmvDefHeader = null;
		for (int i = 0; i < finScheduleData.getDefermentHeaders().size(); i++) {
			DefermentHeader header = finScheduleData.getDefermentHeaders().get(i);
			if (header.getDeferedSchdDate().compareTo(evtFromDate) == 0) {
				defRpyAmount = header.getDefSchdPrincipal().add(header.getDefSchdProfit());
				rmvDefHeader = finScheduleData.getDefermentHeaders().get(i);;
				finScheduleData.getDefermentHeaders().remove(i);
				isDeferRecordFound = true;
				break;
			}
		}
		
		//Re-Adjust Schedule Term Balances Back After Remove Deferment
		int size = finScheduleData.getFinanceScheduleDetails().size();
		if(isDeferRecordFound && rmvDefHeader != null){
			
			for (int i = 0; i < size; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if(curSchd.getSchDate().compareTo(rmvDefHeader.getDeferedSchdDate()) == 0){
					curSchd.setProfitSchd(rmvDefHeader.getDefSchdProfit());
					curSchd.setPrincipalSchd(rmvDefHeader.getDefSchdPrincipal());
					curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
					curSchd.setDeferedPay(false);
					curSchd.setDefered(false);
					curSchd.setDefPrincipal(BigDecimal.ZERO);
					curSchd.setDefProfit(BigDecimal.ZERO);
					break;
				}
			}
		}

		finScheduleData.setDefermentHeaders(sortDefHeaders(finScheduleData.getDefermentHeaders()));

		// Remove date record from Deferment Details
		if (isDeferRecordFound) {
			finScheduleData.setDefermentDetails(sortDefDetailsBySchdDate(finScheduleData
			        .getDefermentDetails()));
			for (int i = 0; i < finScheduleData.getDefermentDetails().size(); i++) {
				
				DefermentDetail detail = finScheduleData.getDefermentDetails().get(i);
				if (detail.getDeferedSchdDate().equals(evtFromDate)) {
					
					for (int k = 0; k < size; k++) {
						FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(k);
						if(curSchd.getSchDate().compareTo(detail.getDeferedRpyDate()) == 0){
							curSchd.setDefRepaySchd(curSchd.getDefRepaySchd().subtract(detail.getDefRpySchdPft().add(detail.getDefRpySchdPri())));
							curSchd.setDefProfitSchd(curSchd.getDefProfitSchd().subtract(detail.getDefRpySchdPft()));
							curSchd.setDefPrincipalSchd(curSchd.getDefPrincipalSchd().subtract(detail.getDefRpySchdPri()));
							break;
						}
					}					
					
					finScheduleData.getDefermentDetails().remove(i);
					i = i - 1;
				}
			}

			finScheduleData.setDefermentDetails(sortDefDetailsBySchdDate(finScheduleData.getDefermentDetails()));

		}

		if (finScheduleData.getDefermentHeaders().isEmpty()) {
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				curSchd.setDefered(false);
				curSchd.setDefPrincipal(BigDecimal.ZERO);
				curSchd.setDefProfit(BigDecimal.ZERO);
				curSchd.setDeferedPay(false);
				curSchd.setDefPrincipalSchd(BigDecimal.ZERO);
				curSchd.setDefProfitSchd(BigDecimal.ZERO);
				curSchd.setDefPrincipalBal(BigDecimal.ZERO);
				curSchd.setDefProfitBal(BigDecimal.ZERO);
			}
		}

		// Remove any instructions on defered date
		if (isDeferRecordFound) {
			/*	RepayInstruction prvRpyInst = null;
			RepayInstruction nxtRpyInst = null;
			
			Boolean isInsert = true;
			for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {
				if (finScheduleData.getRepayInstructions().get(i).getRepayDate()
				        .equals(evtFromDate)) {
	                isInsert = false;
	                finScheduleData.getRepayInstructions().get(i).setRepayAmount(finScheduleData.getRepayInstructions().get(i).getRepayAmount().add(defRpyAmount));
	                break;
                }
			}*/
			
			
			//if (isInsert) {
				setRpyInstructDetails(finScheduleData, evtFromDate, evtFromDate, defRpyAmount, finScheduleData.getFinanceMain().getScheduleMethod());
            //}
			
			
			
			/*for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {

				if (i != 0) {
					prvRpyInst = finScheduleData.getRepayInstructions().get(i - 1);
				}
				if (i != (finScheduleData.getRepayInstructions().size() - 1)) {
					nxtRpyInst = finScheduleData.getRepayInstructions().get(i + 1);
				}

				if (finScheduleData.getRepayInstructions().get(i).getRepayDate()
				        .equals(evtFromDate)) {

					//1. If Deferment term is the First term of Schedule
					if (prvRpyInst == null && nxtRpyInst != null) {
						nxtRpyInst.setRepayDate(evtFromDate);
					}

					//2. If nxtRpyInst is null no need to do anything

					//3.If Previous Repay Term & Next Repay Term Details are Same
					boolean rmvNxtRpyTerm = false;
					if (prvRpyInst != null && nxtRpyInst != null) {
						if (prvRpyInst.getRepayAmount().compareTo(nxtRpyInst.getRepayAmount()) == 0
						        && prvRpyInst.getRepaySchdMethod().equals(
						                nxtRpyInst.getRepaySchdMethod())) {
							rmvNxtRpyTerm = true;
						} else if (defRpyAmount.compareTo(prvRpyInst.getRepayAmount()) == 0) {
							//Nothing todo for Date Re-modification
						} else if (defRpyAmount.compareTo(nxtRpyInst.getRepayAmount()) == 0) {
							nxtRpyInst.setRepayDate(evtFromDate);
						}
					}

					if (rmvNxtRpyTerm) {
						finScheduleData.getRepayInstructions().remove(i + 1);
					}
					finScheduleData.getRepayInstructions().remove(i);
					break;
				}
				nxtRpyInst = null;
			}*/
		}

		//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
		//finScheduleData = calSchdProcess(finScheduleData, false);
		finScheduleData.getFinanceMain().setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method 		: procDeleteTerm 
	 * Description 	: DELETE TERM 
	 * Process 		:
	 * ================================================================================================================
	 */

	private FinScheduleData procDeleteTerm(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		Date evtFromDate = financeMain.getEventFromDate();

		recalStartDate = null;
		recalEndDate = null;

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		/*----------------------------------------------------------------
		 *To Delete Terms the below conditions must meet the below conditions
		 * i) Calculated Final Date after deletion must be on/after current business date
		 * ii) Calculated Final Date after deletion must be with repayment on schedule date TRUE
		 * iii) Should not have any deferment payments pending after calculated final date  
		 */

		// Current Schedule Date is after current business date
		if (evtFromDate.before(curBussniessDate)) {
			// Through Error
			finScheduleData.setErrorDetail(new ErrorDetails("SCH36", 
					"REQUETSED DELETED TERMS DATE IS BEFORE CURRENT BUSINESS DATE. DELETION OF TERMS NOT POSSIBLE", new String[] { " " }));
			logger.error("SCH36 - REQUETSED DELETED TERMS DATE IS BEFORE CURRENT BUSINESS DATE. DELETION OF TERMS NOT POSSIBLE");
			return finScheduleData;

		}

		for (int i = sdSize - 1; i > 0; i--) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			// Current schedule date is On/After Delete from date
			if (curSchd.getSchDate().compareTo(evtFromDate) >= 0) {
				// Deferred repay found after requested deleted date then
				// deletion of terms not possible
				if (curSchd.getDefPrincipalSchd().compareTo(BigDecimal.ZERO) > 0
				        || curSchd.getDefProfitSchd().compareTo(BigDecimal.ZERO) > 0) {
					// Through Error
					finScheduleData.setErrorDetail(new ErrorDetails("SCH33",
							"DEFERED REPAYMENT FOUND AFTER REQUETSED DELETED TERMS DATE. MOVE DEFERED REPAYMENT AND TRY AGAIN",new String[] { " " }));
					logger.error("SCH33 - DEFERED REPAYMENT FOUND AFTER REQUETSED DELETED TERMS DATE. MOVE DEFERED REPAYMENT AND TRY AGAIN");
					return finScheduleData;
				} else {
					continue;
				}
			}

			// Both Profit and Principal Payments already completed for last
			// possible date before requested deleted date then deletion of
			// terms not possible
			if (curSchd.getSchDate().compareTo(evtFromDate) >= 0) {
				if (curSchd.getProfitSchd().equals(curSchd.getSchdPftPaid())
						&& curSchd.getPrincipalSchd().equals(curSchd.getSchdPriPaid())) {
					// Through Error
					finScheduleData.setErrorDetail(new ErrorDetails("SCH34",
							"NO UNPAID SCHEDULE DATES FOUNDS BEFORE REQUESTED DELETE TERMS DATE. DELETION OF TERMS NOT POSSIBLE", new String[] { " " }));
					logger.error("SCH34 - NO UNPAID SCHEDULE DATES FOUNDS BEFORE REQUESTED DELETE TERMS DATE. DELETION OF TERMS NOT POSSIBLE");
					return finScheduleData;
				}
			}

			// Found Unpaid date after current business date and before
			// requested deleted date
			indexStart = i;
			recalEndDate = curSchd.getSchDate();
			break;
		}

		if (recalEndDate == null) {
			// Through Error
			finScheduleData.setErrorDetail(new ErrorDetails("SCH35",
			        "NO REPAYMENT DATE FOUND BEFORE REQUESTED DELETE TERMS DATE AND AFTER CURRENT BUSINESS DATE. DELETION OF TERMS NOT POSSIBLE",
			                                new String[] { " " }));
			logger.error("SCH35 - NO REPAYMENT DATE FOUND BEFORE REQUESTED DELETE TERMS DATE AND AFTER CURRENT BUSINESS DATE. DELETION OF TERMS NOT POSSIBLE");
			return finScheduleData;

		}

		recalStartDate = recalEndDate;

		// Delete all schedule details on/after requested delete terms date
		for (int i = indexStart; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			if (finScheduleData.getFinanceScheduleDetails().get(i).getSchDate()
			        .compareTo(evtFromDate) >= 0) {
				finScheduleData.getFinanceScheduleDetails().remove(i);
				i--;
			}
		}

		// Reset Maturity Date, Recal type
		financeMain.setMaturityDate(finScheduleData.getFinanceScheduleDetails()
		        .get(finScheduleData.getFinanceScheduleDetails().size() - 1).getSchDate());
		financeMain.setCalMaturity(financeMain.getMaturityDate());
		financeMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);

		// Delete All repay instructions after requested delete terms date
		for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {
			if (finScheduleData.getRepayInstructions().get(i).getRepayDate().after(recalEndDate)) {
				finScheduleData.getRepayInstructions().remove(i);
			}
		}

		// Recalculate Schedule
		finScheduleData = procReCalSchd(finScheduleData);
		finScheduleData.getFinanceMain().setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method 		: procChangeRate 
	 * Description 	: CHANGE RATE 
	 * Process 		: Should change the rate for the requested dates and recalculate the repay amount based on the
	 * 				  requested recalculation type
	 * 				 i) CURPRD: Change the rate for the given dates and repay recalculation will happen for the period
	 * 					covered in the dates only. All remaining repayments should be unchanged.
	 * 
	 * 				ii) TILLMDT: Change rate for given dates. Repay recalculation will happen till maturity date
	 * 
	 * 				iii) ADJMDT: Change the rate for the given dates but no repay recalculation will happen. Means
	 * 					increased/decreased profit will be adjusted to final repay.
	 * 
	 * Schedule Method CURPRD TILLDATE TILLMDT ADJMDT
	 * EQUAL			Y 		N 		Y 		Y
	 * PFT 				Y 		N 		N 		N
	 * PRI 				Y 		N 		Y 		Y
	 * PRI_PFT 			Y 		N 		Y 		Y
	 * ================================================================================================================
	 */

	private FinScheduleData procChangeRate(FinScheduleData finScheduleData, String baseRate,
	        String splRate, BigDecimal mrgRate, BigDecimal calculatedRate, boolean isCalSchedule, boolean newSchdGeneratedNow) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		String recaltype = financeMain.getRecalType();
		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		Date evtFromDate = financeMain.getEventFromDate();
		Date evtToDate = financeMain.getEventToDate();
		recalStartDate = null;
		recalEndDate = null;

		/*
		 * if (event Todate is maturity date OR event Todate is before grace period end date) AND recalculation type is
		 * for CURPRD then force it to TILLMDT
		 */
		if (recaltype.equals("#")) {
			financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			recaltype = financeMain.getRecalType();
		}
		if ((evtToDate.compareTo(financeMain.getMaturityDate()) >= 0 || evtToDate
		        .compareTo(financeMain.getGrcPeriodEndDate()) <= 0)
		        && (recaltype.equals(CalculationConstants.RPYCHG_CURPRD))) {
			financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_ADJMDT)) {
			financeMain.setCalculateRepay(false);
			financeMain.setEqualRepay(false);
			recalStartDate = financeMain.getMaturityDate();
			recalEndDate = recalStartDate;
			isFirstAdjSet = true;
			isLastAdjSet = true;
		} else {
			financeMain.setCalculateRepay(true);
			financeMain.setEqualRepay(true);

			if (recaltype.equals(CalculationConstants.RPYCHG_CURPRD)) {
				recalEndDate = evtToDate;
				isLastAdjSet = true;

				if (isCalSchedule) {
					isCompareToExpected = true;
				}
			}

			if (recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				recalEndDate = financeMain.getMaturityDate();
				isLastAdjSet = true;
			}
		}

		// Setting Rates between Fromdate and Todate
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		BigDecimal recalculateRate = BigDecimal.ZERO;

		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			Date getSchdDate = new Date();
			getSchdDate = curSchd.getSchDate();

			// Setting Rates between Fromdate and Todate
			if ((getSchdDate.compareTo(evtFromDate) >= 0 && getSchdDate.compareTo(evtToDate) < 0)
			        || (i == (sdSize - 1))) {

				recalculateRate = calculatedRate;

				if (curSchd.isRvwOnSchDate()) {
					if (baseRate != null && baseRate.trim() != "") {
						RateDetail rateDetail = new RateDetail(baseRate, splRate, mrgRate,
						        curSchd.getSchDate());
						recalculateRate = RateUtil.getRefRate(rateDetail).getNetRefRateLoan();
					}
				}

				curSchd.setBaseRate(baseRate);
				curSchd.setSplRate(splRate);
				curSchd.setMrgRate(mrgRate);
				
				//Applying same Profit Rate with Rate Margin on Step Policy Details, If finance is Step Finance
				if(!newSchdGeneratedNow || !financeMain.isStepFinance()){
					curSchd.setCalculatedRate(recalculateRate);
				}

				if (!isFirstAdjSet) {
					isFirstAdjSet = true;
					if (i != (sdSize - 1)) {
						recalStartDate = finScheduleData.getFinanceScheduleDetails().get(i + 1)
						        .getSchDate();
					} else {
						recalStartDate = recalEndDate;
					}
				}
			}

			// Set expected result even for schedule method PROFIT
			if (curSchd.isRepayOnSchDate() && isCompareToExpected) {
				expectedResult = fetchCalAmount(curSchd);
			}
		}

		if (!isFirstAdjSet) {
			recalStartDate = recalEndDate;
			isFirstAdjSet = true;
		}

		finScheduleData.setFinanceMain(financeMain);
		// call the process
		if (isCalSchedule) {
			//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
			finScheduleData = calSchdProcess(finScheduleData, false);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method 		: procChangeRepay 
	 * Description 	: CHANGE REPAY AMOUNT 
	 * Process 		: Should change the repay amount and recalculate repay amounts of remaining schedule based on the
	 * 				  requested recalculation type. Not Applicable for schedule method "PFT"
	 * 					i) TILLDATE: Change the repay for requested dates and recalculate schedule.
	 * 					ii) TILLMDT: Change the repay till maturity date 
	 * 					iii) ADJMDT: Change the repay and increase/decrease in profit will be 	 * adjusted to maturity.
	 * 
	 * NOTE : Schedule Method EQUAL/PRI will allow all recalculation types Schedule Method PFT/PRI_PFT will allow CURPRD
	 * only
	 * =================================================================================================================
	 */

	private FinScheduleData procChangeRepay(FinScheduleData finScheduleData,
	        BigDecimal repayAmount, String schdMethod) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String recaltype = financeMain.getRecalType();

		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;
		
		BigDecimal totalDesiredProfit = financeMain.getTotalGrossPft();
		
		BigDecimal totOrginalPayment = BigDecimal.ZERO;

		Date evtFromDate = financeMain.getEventFromDate();
		Date evtToDate = financeMain.getEventToDate();
		

		recalStartDate = null;
		recalEndDate = null;

		//Set new repayments amount for the selected dates 
		if (repayAmount != null) {
			finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtToDate,
			        repayAmount, schdMethod);
		}
		
		totOrginalPayment = financeMain.getFinAmount().add(financeMain.getTotalProfit());

		//=====================================================================================
		//ENHANCEMENT DATE: 04 SEP 14
		//=====================================================================================
		//RECALCULATION TYPE				| RECAL START DATE				| RECAL TO DATE
		//=====================================================================================
		//ADD TERMS							| OLD MATURITY DATE + 1			| NEW MATURITY DATE
		//ADD TERMS AND RECALCULATE			| EVENT TO DATE + 1				| NEW MATURITY DATE
		//ADJUST TO MATURITY				| MATURITY DATE					t MATURITY DATE
		//TILL DATE							| RECAL FROM DATE				| RECAL TO DATE
		//TILL MATURITY						| RECAL FROM DATE				| MATURITY DATE
		//=====================================================================================

		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//RECALCULATION TYPE: ADD TERMS & ADD TERMS AND RECALCULATE
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		if (recaltype.equals(CalculationConstants.RPYCHG_ADDTERM) || recaltype.equals(CalculationConstants.RPYCHG_ADDRECAL)) {
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
	        int indexNewTermStart = sdSize;
			
	        finScheduleData = procAddTerm(finScheduleData, financeMain.getAdjTerms(), schdMethod, false);
	        financeMain = finScheduleData.getFinanceMain();
	        
	        sdSize = finScheduleData.getFinanceScheduleDetails().size();
			financeMain.setCalculateRepay(true);
			financeMain.setEqualRepay(true);

	        // Set Recalculation Start and End Dates
	        if (recaltype.equals(CalculationConstants.RPYCHG_ADDTERM)) {
	        	recalStartDate = finScheduleData.getFinanceScheduleDetails().get(indexNewTermStart).getSchDate();	
	        } else {
	        	sdSize = finScheduleData.getFinanceScheduleDetails().size();
	        	for (int i = 0; i < sdSize; i++) {
	        		if (finScheduleData.getFinanceScheduleDetails().get(i).getSchDate().compareTo(evtToDate)>0) {
	        			recalStartDate = finScheduleData.getFinanceScheduleDetails().get(i).getSchDate();
	        			break;
                    }
	        	}
			}
	        
	        recalEndDate = finScheduleData.getFinanceScheduleDetails().get(sdSize-1).getSchDate();

	        //Calculate New EMI
	        finScheduleData = calculateNewEMI(finScheduleData, evtFromDate, evtToDate, recaltype, schdMethod, repayAmount, totOrginalPayment);
        }
		
		
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//RECALCULATION TYPE: ADJUST TO MATURITY
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		if (recaltype.equals(CalculationConstants.RPYCHG_ADJMDT)) {
			recalStartDate = financeMain.getMaturityDate();
			recalEndDate = recalStartDate;
			financeMain.setCalculateRepay(false);
			financeMain.setEqualRepay(false);
		} 

		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//RECALCULATION TYPE: TILL DATE OR TILL MATURITY
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// Setting repayments between From date and To date
		if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)
		        || recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {

			//DEFAULTTING RCEALCULATION TYPE TO TILLMDT INCASE EVENT TO DATE OR RECAL TO DATE >= MATURITY DATE
			if (evtToDate.compareTo(financeMain.getMaturityDate()) >= 0
			        || financeMain.getRecalToDate().compareTo(financeMain.getMaturityDate()) >= 0
			        && recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
				financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			}

			recalStartDate = financeMain.getRecalFromDate();
			financeMain.setCalculateRepay(true);			

			if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
				isCompareToExpected = true;
				recalEndDate = financeMain.getRecalToDate();
			} else {
				recalEndDate = financeMain.getMaturityDate();
			}

	        //Calculate New EMI
	        finScheduleData = calculateNewEMI(finScheduleData, evtFromDate, evtToDate, recaltype, schdMethod, repayAmount, totOrginalPayment);

		}

		//---------------------------------------------------------------------------------------------------------------------
		//RECALCULATION
		//---------------------------------------------------------------------------------------------------------------------

		finScheduleData.setFinanceMain(financeMain);
		
		
		if (financeMain.isPftIntact()) {
			//Adjust Repay amount till maturity date
			finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.TOTAL, totalDesiredProfit, evtFromDate, financeMain.getCalMaturity(), false);
			finScheduleData = setFinanceTotals(finScheduleData);
        } else {
        	finScheduleData = calSchdProcess(finScheduleData, false);	
		}

		//REMOVE LAST RECORDS AFTER NEWLY CALCULATED MATURITY DATE.
		if (newMaturityIndex < (finScheduleData.getFinanceScheduleDetails().size()-1)) {
            finScheduleData.getFinanceMain().setMaturityDate(finScheduleData.getFinanceScheduleDetails().get(newMaturityIndex).getSchDate());
            
        	int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = (newMaturityIndex+1) ; i < sdSize; i++) {
                finScheduleData.getFinanceScheduleDetails().remove(i);
                i=i-1;
                sdSize = finScheduleData.getFinanceScheduleDetails().size();
            }
        }
		
		//EFFECTIVE RATE CALCULATION
		Cloner cloner = new Cloner();
		FinScheduleData orgFinScheduleData = cloner.deepClone(finScheduleData);
		
		isProtectSchdPft = true;
		//SET START AND DATES FOR EFFECTIVE RATE CALCULATION
		orgFinScheduleData.getFinanceMain().setEventFromDate(orgFinScheduleData.getFinanceMain().getFinStartDate());
		orgFinScheduleData.getFinanceMain().setEventToDate(orgFinScheduleData.getFinanceMain().getMaturityDate());
		
		FinanceMain finMain = orgFinScheduleData.getFinanceMain();
		
		orgFinScheduleData = calEffectiveRate(orgFinScheduleData,
		        CalculationConstants.TOTAL, (finMain.getTotalProfit().add(finMain.getTotalCpz())), finMain.getFinStartDate(), finMain.getMaturityDate(), true);
		finScheduleData.getFinanceMain().setEffectiveRateOfReturn(
		        orgFinScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate());

		finScheduleData.getFinanceMain().setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method 		: procAddRepayment 
	 * Description 	: ADD REPAYMENT 
	 * Process 		:
	 * =================================================================================================================
	 */
	private FinScheduleData procAddRepayment(FinScheduleData finScheduleData,
	        BigDecimal repayAmount, String schdMethod) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String recaltype = financeMain.getRecalType();
		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		Date evtFromDate = financeMain.getEventFromDate();
		Date evtToDate = evtFromDate;
		recalStartDate = null;
		recalEndDate = null;

		boolean isRepaymentFoundInSD = false;

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		int prvIndex = 0;
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (curSchd.getSchDate().compareTo(evtFromDate) == 0) {
				isRepaymentFoundInSD = true;

				break;
			} else {
				if (curSchd.getSchDate().before(evtFromDate)) {
					prvIndex = i;
				}
			}

		}

		if (!isRepaymentFoundInSD) {
			FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(
			        prvIndex);

			FinanceScheduleDetail sd = new FinanceScheduleDetail();

			sd.setSchDate(evtToDate);
			sd.setPftOnSchDate(true);
			sd.setCpzOnSchDate(false);
			sd.setRepayOnSchDate(true);
			sd.setRvwOnSchDate(false);
			sd.setDisbOnSchDate(false);
			sd.setDownpaymentOnSchDate(false);
			sd.setBalanceForPftCal(BigDecimal.ZERO);
			sd.setBaseRate(prvSchd.getBaseRate());
			sd.setSplRate(prvSchd.getSplRate());
			sd.setMrgRate(prvSchd.getMrgRate());
			sd.setActRate(prvSchd.getActRate());
			sd.setCalculatedRate(prvSchd.getCalculatedRate());
			sd.setNoOfDays(0);
			sd.setDayFactor(BigDecimal.ZERO);
			sd.setProfitCalc(BigDecimal.ZERO);
			sd.setProfitSchd(BigDecimal.ZERO);
			sd.setPrincipalSchd(BigDecimal.ZERO);
			sd.setRepayAmount(repayAmount);
			sd.setProfitBalance(BigDecimal.ZERO);
			sd.setDisbAmount(BigDecimal.ZERO);
			sd.setDownPaymentAmount(BigDecimal.ZERO);
			sd.setCpzAmount(BigDecimal.ZERO);
			sd.setClosingBalance(BigDecimal.ZERO);
			sd.setProfitFraction(BigDecimal.ZERO);
			sd.setPrvRepayAmount(repayAmount);
			sd.setSchdMethod(schdMethod);

			finScheduleData.getFinanceScheduleDetails().add(sd);
			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData
			        .getFinanceScheduleDetails()));
			financeMain.setNumberOfTerms(financeMain.getNumberOfTerms() + 1);

		}

		// financeMain = inzPrvRepayAmount(financeMain);

		// If recalculation type is TILL DATE and event to date is >= maturity
		// date then force it to TILLMDT
		if (evtToDate.compareTo(financeMain.getMaturityDate()) >= 0
		        && recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
			financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_ADJMDT)) {
			financeMain.setCalculateRepay(false);
			financeMain.setEqualRepay(false);
			recalStartDate = financeMain.getMaturityDate();
			recalEndDate = recalStartDate;
			isFirstAdjSet = true;
			isLastAdjSet = true;
			financeMain.setCalculateRepay(false);
		} else {
			financeMain.setCalculateRepay(true);
			financeMain.setEqualRepay(true);

			if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
				recalEndDate = financeMain.getRecalToDate();
				isLastAdjSet = true;
				isCompareToExpected = true;
			}

			if (recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				recalEndDate = financeMain.getMaturityDate();
				isLastAdjSet = true;
			}
		}

		// Setting repayments between Fromdate and Todate
		if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)
		        || recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			sdSize = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				Date getSchdDate = new Date();
				getSchdDate = curSchd.getSchDate();

				// Setting first date after event end date
				if (getSchdDate.compareTo(evtToDate) > 0) {

					if (!isFirstAdjSet) {
						isFirstAdjSet = true;
						if (i != (sdSize - 1)) {
							recalStartDate = curSchd.getSchDate();
						} else {
							recalStartDate = recalEndDate;
						}
					}
				}

				// Set expected result even for schedule method PROFIT
				if (curSchd.isRepayOnSchDate() && isCompareToExpected) {
					expectedResult = fetchCalAmount(curSchd);
					// ExpectedResult = BigDecimal.ZERO;
				}

			}
		}

		if (!isFirstAdjSet) {
			recalStartDate = recalEndDate;
			isFirstAdjSet = true;
		}

		finScheduleData.setFinanceMain(financeMain);
		finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtFromDate,
		        repayAmount, schdMethod);

		//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
		finScheduleData = calSchdProcess(finScheduleData, false);
		finScheduleData.getFinanceMain().setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method 		: procAddDisburse 
	 * Description 	: ADD DISBURSEMENT 
	 * Process 		:
	 * =================================================================================================================
	 */

	private FinScheduleData procAddDisbursement(FinScheduleData finScheduleData, BigDecimal amount,
	        String addTermAfter, BigDecimal feeChargeAmt) {
		logger.debug("Entering");

		FinScheduleData orgFinScheduleData = null;

		Cloner cloner = new Cloner();
		orgFinScheduleData = cloner.deepClone(finScheduleData);
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		String recaltype = financeMain.getRecalType();
		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		Date evtFromDate = financeMain.getEventFromDate();
		Date evtToDate = evtFromDate;
		recalStartDate = null;
		recalEndDate = null;

		boolean isDisburseFoundInDB = false;
		boolean isDisburseFoundInSD = false;

		int dbSize = finScheduleData.getDisbursementDetails().size();
		for (int i = 0; i < dbSize; i++) {
			FinanceDisbursement curDisb = finScheduleData.getDisbursementDetails().get(i);
			if (curDisb.getDisbDate().compareTo(evtFromDate) == 0) {
				isDisburseFoundInDB = true;
				curDisb.setDisbAmount(curDisb.getDisbAmount().add(amount));
				curDisb.setFeeChargeAmt(curDisb.getFeeChargeAmt().add(feeChargeAmt));
				break;
			}
		}

		if (!isDisburseFoundInDB) {
			FinanceDisbursement dd = new FinanceDisbursement();
			dd.setDisbAmount(amount);
			dd.setDisbDate(evtFromDate);
			dd.setFeeChargeAmt(feeChargeAmt);
			finScheduleData.getDisbursementDetails().add(dd);
		}

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		int prvIndex = 0;
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (curSchd.getSchDate().compareTo(evtFromDate) == 0) {
				isDisburseFoundInSD = true;
				curSchd.setDisbAmount(curSchd.getDisbAmount().add(amount));
				curSchd.setDisbOnSchDate(true);
				curSchd.setFeeChargeAmt(curSchd.getFeeChargeAmt().add(feeChargeAmt));
				break;
			} else {
				if (curSchd.getSchDate().before(evtFromDate)) {
					prvIndex = i;
				}
			}
		}

		if (!isDisburseFoundInSD) {
			FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(
			        prvIndex);

			FinanceScheduleDetail sd = new FinanceScheduleDetail();

			sd.setSchDate(evtToDate);
			sd.setDefSchdDate(evtToDate);
			sd.setPftOnSchDate(true);
			sd.setCpzOnSchDate(false);
			sd.setRepayOnSchDate(false);
			sd.setRvwOnSchDate(false);
			sd.setDisbOnSchDate(true);
			sd.setDownpaymentOnSchDate(false);
			sd.setBalanceForPftCal(BigDecimal.ZERO);
			sd.setBaseRate(prvSchd.getBaseRate());
			sd.setSplRate(prvSchd.getSplRate());
			sd.setMrgRate(prvSchd.getMrgRate());
			sd.setActRate(prvSchd.getActRate());
			sd.setCalculatedRate(prvSchd.getCalculatedRate());
			sd.setNoOfDays(0);
			sd.setDayFactor(BigDecimal.ZERO);
			sd.setProfitCalc(BigDecimal.ZERO);
			sd.setProfitSchd(BigDecimal.ZERO);
			sd.setPrincipalSchd(BigDecimal.ZERO);
			sd.setRepayAmount(BigDecimal.ZERO);
			sd.setProfitBalance(BigDecimal.ZERO);
			sd.setDisbAmount(amount);
			sd.setDownPaymentAmount(BigDecimal.ZERO);
			sd.setCpzAmount(BigDecimal.ZERO);
			sd.setClosingBalance(BigDecimal.ZERO);
			sd.setProfitFraction(BigDecimal.ZERO);
			sd.setPrvRepayAmount(BigDecimal.ZERO);
			sd.setFeeChargeAmt(feeChargeAmt);
			// sd.setSchdMethod("");

			if (evtFromDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
				sd.setSpecifier(CalculationConstants.GRACE);
			} else {
				sd.setSpecifier(CalculationConstants.REPAY);
			}

			finScheduleData.getFinanceScheduleDetails().add(sd);
			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData
			        .getFinanceScheduleDetails()));

		}

		// financeMain = inzPrvRepayAmount(financeMain);

		// If recalculation type is TILL DATE and event to date is >= maturity
		// date then force it to TILLMDT
		if (evtToDate.compareTo(financeMain.getMaturityDate()) >= 0
		        && recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {
			financeMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		}

		if (recaltype.equals(CalculationConstants.RPYCHG_ADJMDT)) {
			financeMain.setCalculateRepay(false);
			financeMain.setEqualRepay(false);
			recalStartDate = financeMain.getMaturityDate();
			recalEndDate = recalStartDate;
			isFirstAdjSet = true;
			isLastAdjSet = true;
			financeMain.setCalculateRepay(false);
		} else if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)) {

			financeMain.setCalculateRepay(true);
			financeMain.setEqualRepay(true);
			recalEndDate = financeMain.getRecalToDate();
			isLastAdjSet = true;
			isCompareToExpected = true;
		} else if (recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {

			financeMain.setCalculateRepay(true);
			financeMain.setEqualRepay(true);
			recalEndDate = financeMain.getMaturityDate();
			isLastAdjSet = true;

		} else if (recaltype.equals(CalculationConstants.RPYCHG_ADJTERMS)) {
			finScheduleData = adjTerms(finScheduleData, addTermAfter, true);

			if (finScheduleData.getErrorDetails().size() > 0) {
				orgFinScheduleData.setErrorDetails(finScheduleData.getErrorDetails());
				return orgFinScheduleData;
			}

			logger.debug("Leaving");
			return finScheduleData;
		}

		// Setting repayments between Fromdate and Todate
		if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)
		        || recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			sdSize = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				Date getSchdDate = new Date();
				getSchdDate = curSchd.getSchDate();

				// Setting first date after event end date
				if (getSchdDate.compareTo(evtToDate) > 0) {

					if (!isFirstAdjSet && curSchd.getSpecifier().equals(CalculationConstants.REPAY)) {
						isFirstAdjSet = true;
						if (i != (sdSize - 1)) {
							recalStartDate = curSchd.getSchDate();
						} else {
							recalStartDate = recalEndDate;
						}
					}
				}

				// Set expected result even for schedule method PROFIT
				if (curSchd.isRepayOnSchDate() && isCompareToExpected) {
					expectedResult = fetchCalAmount(curSchd);
				}

			}
		}

		if (!isFirstAdjSet) {
			recalStartDate = recalEndDate;
			isFirstAdjSet = true;
		}

		finScheduleData.setFinanceMain(financeMain);

		//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
		finScheduleData = calSchdProcess(finScheduleData, false);
		finScheduleData.getFinanceMain().setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ================================================================================================================
	 * Method 		: procAddTerm 
	 * Description 	: ADD TERM 
	 * Process 		: Add Term will add another term to the schedule details.
	 * =================================================================================================================
	 */

	private FinScheduleData procAddTerm(FinScheduleData orgFinScheduleData, int noOfTerms,
	        String schdMethod, boolean isCalSchedule) {
		
		logger.debug("Entering");

		FinScheduleData finScheduleData = null;
		Cloner cloner = new Cloner();
		finScheduleData = cloner.deepClone(orgFinScheduleData);

		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		recalStartDate = null;
		recalEndDate = null;

		finScheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_TILLMDT);

		// Set the limits based on system values table
		int maxFinYears = Integer.parseInt(SystemParameterDetails.getSystemParameterValue(
		        "MAX_FIN_YEARS").toString());
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtility.addYears(finScheduleData.getFinanceMain().getFinStartDate(),
		        maxFinYears);

		for (int i = 0; i < noOfTerms; i++) {
			finScheduleData = addOneTerm(finScheduleData, lastDateLimit, schdMethod, true);

			if (finScheduleData.getErrorDetails().size() > 0) {
				orgFinScheduleData.getErrorDetails().addAll(finScheduleData.getErrorDetails());
				return orgFinScheduleData;
			}
		}

		//Except first time creation of schedule convert flat rate to reducing will be treated as reducing only
		if (isCalSchedule) {
			finScheduleData = calSchdProcess(finScheduleData, false);
			finScheduleData.getFinanceMain().setScheduleMaintained(true);
		}
		
		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * ================================================================================================================
	 * Method 		: procChangeProfit 
	 * Description 	: CHANGE Profit Amount between two dates 
	 * Process 		: 
	 * 
	 * =================================================================================================================
	 */

	private FinScheduleData procChangeProfit(FinScheduleData finScheduleData,
	        BigDecimal desiredPftAmount) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		Date evtFromDate = financeMain.getEventFromDate();
		Date evtToDate = financeMain.getEventToDate();

		recalStartDate = evtFromDate;
		recalEndDate = evtToDate;

		if (recalStartDate.before(financeMain.getGrcPeriodEndDate())) {
			int size = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = 0; i < size; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if (curSchd.getSchDate().after(financeMain.getGrcPeriodEndDate())) {
					recalStartDate = curSchd.getSchDate();
					break;
				}
			}

		}

		//TODO- Calculate New EMI is newly added but seems not useful so to be delete. 15Oct'14 
		//finScheduleData = calculateNewEMI(finScheduleData, evtFromDate, evtToDate, recaltype,
		//      schdMethod, repayAmount, totOrginalPayment);
		
		finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.SELECT,
		        desiredPftAmount, null, null, false);

		finScheduleData = setFinanceTotals(finScheduleData);

		//Calculate Effective Rate after Change profit
		Cloner cloner = new Cloner();
		FinScheduleData orgFinScheduleData = cloner.deepClone(finScheduleData);

		//isProtectSchdPft = true;  TODO check by pradeep, is it needed or not
		//SET START AND DATES FOR EFFECTIVE RATE CALCULATION
		orgFinScheduleData.getFinanceMain().setEventFromDate(
		        orgFinScheduleData.getFinanceMain().getFinStartDate());
		orgFinScheduleData.getFinanceMain().setEventToDate(
		        orgFinScheduleData.getFinanceMain().getMaturityDate());

		FinanceMain finMain = orgFinScheduleData.getFinanceMain();
		orgFinScheduleData = calEffectiveRate(orgFinScheduleData, CalculationConstants.TOTAL,
		        finMain.getTotalGrossPft(), finMain.getFinStartDate(),
		        finMain.getMaturityDate(), true);

		finScheduleData.getFinanceMain().setEffectiveRateOfReturn(
		        orgFinScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate());

		setFinScheduleData(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/*
	 * ================================================================================================================
	 * Method 		: procRefreshRates 
	 * Description 	:REFRESH RATES AND RECALCULATE SCHEDULE BASED ON REVIEW RATE APPLIED FOR SCHEDULE RECAL TYPE
	 * Process 		: 
	 * ================================================================================================================
	 */

	private FinScheduleData procRefreshRates(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String recaltype = financeMain.getRecalType();

		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		recalStartDate = null;
		recalEndDate = null;

		/*
		 * Check reviews allowed or not. If not allowed then return without any change
		 */
		if (curBussniessDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
			if (!financeMain.isAllowGrcPftRvw()) {
				return finScheduleData;
			}
		} else {
			if (!financeMain.isAllowRepayRvw()) {
				return finScheduleData;
			}
		}

		/*
		 * Three types of recalculation allowed in review rate refresh
		 * CURPRD, TILLMDT, and ADJMDT
		 * When current review period installments paid completely recalculation will be forced to ADJMDT
		 */
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		//CURPRD OR TILLMDT
		if (recaltype.equals(CalculationConstants.RPYCHG_CURPRD)
		        || recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {

			//TILLMDT
			if (recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
				isLastAdjSet = true;
				recalEndDate = financeMain.getMaturityDate();
			}

			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				Date schdDate = curSchd.getSchDate();

				if (schdDate.compareTo(financeMain.getLastRepayRvwDate()) <= 0) {
					continue;
				}

				if (curSchd.isRepayOnSchDate()
				        && (!curSchd.isSchPftPaid() || !curSchd.isSchPriPaid()) && !isFirstAdjSet) {
					isFirstAdjSet = true;
					recalStartDate = schdDate;

					//?TILLMDT No need to check end date again
					if (recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
						break;
					}
				}

				expectedResult = fetchCalAmount(curSchd);

				// Applicable to CURPRD only. If not current period will not come to this stage 
				if (curSchd.isRvwOnSchDate() && isFirstAdjSet) {
					isLastAdjSet = true;
					recalEndDate = schdDate;
					break;
				}
			}

			if (!isFirstAdjSet) {
				recalStartDate = recalEndDate;
				recaltype = CalculationConstants.RPYCHG_ADJMDT;
			} else if (recaltype.equals(CalculationConstants.RPYCHG_CURPRD)) {
				isCompareToExpected = true;
			}
		}

		//ADJMDT (No other types are allowed)
		if (!recaltype.equals(CalculationConstants.RPYCHG_CURPRD)
		        && !recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			recaltype = CalculationConstants.RPYCHG_ADJMDT;
			isFirstAdjSet = true;
			isLastAdjSet = true;
			recalStartDate = financeMain.getMaturityDate();
			recalEndDate = recalStartDate;
		}

		// Call schedule calculation process
		//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
		finScheduleData = calSchdProcess(finScheduleData, false);

		logger.debug("Leaving");
		return finScheduleData;
	}

	
	/* ################################################################################################################
	 * SUB METHODS
	 * ################################################################################################################
	 */

	/* ________________________________________________________________________________________________________________
	 * Method 		: setGraceDetails
	 * Description	: Set Grace Period Details
	 * Process		:
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setGraceDetails(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		financeMain.setEventFromDate(finScheduleData.getFinanceScheduleDetails().get(0)
		        .getSchDate());
		financeMain.setEventToDate(financeMain.getGrcPeriodEndDate());

		boolean tmpIsCalRepay = financeMain.isCalculateRepay();

		finScheduleData = procChangeRate(finScheduleData, financeMain.getGraceBaseRate(),
		        financeMain.getGraceSpecialRate(), financeMain.getGrcMargin(),
		        financeMain.getGrcPftRate(), false , true);

		// Reset to original isCalRepayFlag
		financeMain.setCalculateRepay(tmpIsCalRepay);

		// If repay amount in grace period is not allowed then leave
		if (!financeMain.isAllowGrcRepay()) {
			financeMain.setGrcSchdMthd(CalculationConstants.EQUAL);
			finScheduleData.setFinanceMain(financeMain);
			return finScheduleData;
		}

		recalStartDate = financeMain.getNextGrcPftDate();
		recalEndDate = financeMain.getMaturityDate();

		finScheduleData = setRpyInstructDetails(finScheduleData, recalStartDate, recalEndDate,
		        BigDecimal.ZERO, finScheduleData.getFinanceMain().getGrcSchdMthd());

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: setRepayDetails
	 * Description	: Set Repay Period Details
	 * Process		:
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setRepayDetails(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		BigDecimal repayAmount = BigDecimal.ZERO;

		// Change Rates Grace period end date to maturity
		financeMain.setEventFromDate(financeMain.getGrcPeriodEndDate());
		financeMain.setEventToDate(financeMain.getMaturityDate());

		boolean tmpIsCalRepay = financeMain.isCalculateRepay();

		finScheduleData = procChangeRate(finScheduleData, financeMain.getRepayBaseRate(),
		        financeMain.getRepaySpecialRate(), financeMain.getRepayMargin(),
		        financeMain.getRepayProfitRate(), false , true);

		// Reset to original isCalRepayFlag
		financeMain.setCalculateRepay(tmpIsCalRepay);

		// Set Repay Amount
		if (financeMain.isCalculateRepay()
		        || finScheduleData.getFinanceMain().getScheduleMethod()
		                .equals(CalculationConstants.PFT)) {
			repayAmount = BigDecimal.ZERO;
		} else {
			repayAmount = financeMain.getReqRepayAmount();
		}

		recalStartDate = financeMain.getNextRepayDate();
		recalEndDate = financeMain.getMaturityDate();

		finScheduleData = setRpyInstructDetails(finScheduleData, recalStartDate, recalEndDate,
		        repayAmount, finScheduleData.getFinanceMain().getScheduleMethod());

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: adjTerms
	 * Description	: Adjust Terms
	 * Process		:
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData adjTerms(FinScheduleData finScheduleData, String addTermAfter,
	        boolean isSetRepay) {
		logger.debug("Entering");

		finScheduleData.getFinanceMain().setCalculateRepay(false);
		finScheduleData.getFinanceMain().setEqualRepay(false);
		finScheduleData.getFinanceMain().setCalculateRepay(false);

		recalStartDate = finScheduleData.getFinanceMain().getMaturityDate();
		recalEndDate = recalStartDate;
		isFirstAdjSet = true;
		isLastAdjSet = true;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		// Get Adjustment Limits parameters from system values table
		BigDecimal lastTermPercent = new BigDecimal(SystemParameterDetails.getSystemParameterValue(
		        "ADJTERM_LASTTERM_PERCENT").toString());
		int maxFinYears = Integer.parseInt(SystemParameterDetails.getSystemParameterValue(
		        "MAX_FIN_YEARS").toString());

		// Set the limits based on system values table
		BigDecimal lastTermLimit = new BigDecimal(0.0);
		Date lastDateLimit = new Date();

		lastDateLimit = DateUtility.addYears(finScheduleData.getFinanceMain().getFinStartDate(),
		        maxFinYears);

		int i = finScheduleData.getRepayInstructions().size() - 1;
		lastTermLimit = finScheduleData.getRepayInstructions().get(i).getRepayAmount();

		lastTermLimit = lastTermLimit.add(finScheduleData.getRepayInstructions().get(i)
		        .getRepayAmount().multiply(lastTermPercent));

		// Calculate Schedule
		//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
		finScheduleData = calSchdProcess(finScheduleData, false);

		i = finScheduleData.getFinanceScheduleDetails().size() - 1;

		// If The calculated schedule last repayment is under limit then no need
		// to adjust terms
		if (finScheduleData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.EQUAL)) {
			if (finScheduleData.getFinanceScheduleDetails().get(i).getRepayAmount()
			        .compareTo(lastTermLimit) <= 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}
		} else {
			if (finScheduleData.getFinanceScheduleDetails().get(i).getPrincipalSchd()
			        .compareTo(lastTermLimit) <= 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}
		}

		// Adjust Terms
		boolean isAdjTermsComplete = false;
		while (!isAdjTermsComplete) {
			finScheduleData = addOneTerm(finScheduleData, lastDateLimit, addTermAfter, isSetRepay);

			if (finScheduleData.getErrorDetails().size() > 0) {
				return finScheduleData;
			}

			//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
			finScheduleData = calSchdProcess(finScheduleData, false);

			if (finScheduleData.getFinanceMain().getScheduleMethod()
			        .equals(CalculationConstants.EQUAL)) {
				if (finScheduleData.getFinanceScheduleDetails()
				        .get(finScheduleData.getFinanceScheduleDetails().size() - 1)
				        .getRepayAmount().compareTo(lastTermLimit) <= 0) {
					isAdjTermsComplete = true;
					break;
				}
			} else {
				if (finScheduleData.getFinanceScheduleDetails()
				        .get(finScheduleData.getFinanceScheduleDetails().size() - 1)
				        .getPrincipalSchd().compareTo(lastTermLimit) <= 0) {
					isAdjTermsComplete = true;
					break;
				}
			}

		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: addOneTerm
	 * Description	: ADD One Terms
	 * Process		: Add Term will add another term to the schedule details.
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData addOneTerm(FinScheduleData finScheduleData, Date lastDateLimit,
	        String addTermAfter, boolean isSetRepay) {
		logger.debug("Entering");

		// Maturity Record Index
		int maturityIndex = finScheduleData.getFinanceScheduleDetails().size() - 1;

		// Set After which index new term should be added
		int addTermAfterIndex = maturityIndex;

		// If new record to be added after last repayment then find last
		// repayment index and assign
		if (addTermAfter.equals(CalculationConstants.ADDTERM_AFTRPY)) {
			for (int i = maturityIndex; i >= 0; i--) {
				if (finScheduleData.getFinanceScheduleDetails().get(i).isRepayOnSchDate()) {
					addTermAfterIndex = i;
					break;
				}
			}
		}

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(
		        addTermAfterIndex);

		Date curSchdDate = curSchd.getSchDate();

		// get next Repayment Date
		Date nextSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(
		        FrequencyUtil.getNextDate(financeMain.getRepayFrq(), 1, curSchdDate,
		                HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
		        PennantConstants.dateFormat));

		if (nextSchdDate.after(lastDateLimit)) {
			// Through Error
			finScheduleData.setErrorDetail(new ErrorDetails("SCH30","ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS", new String[] { " " }));
			return finScheduleData;
		}

		if (curSchd.getSpecifier().equals(CalculationConstants.MATURITY)) {
			curSchd.setSpecifier(CalculationConstants.REPAY);
		}

		// Set Profit On date based on frequency
		if (FrequencyUtil.isFrqDate(financeMain.getRepayPftFrq(), curSchdDate)) {
			curSchd.setPftOnSchDate(true);
		} else {
			curSchd.setPftOnSchDate(false);
		}

		// Set Profit Review On date based on frequency
		if (financeMain.isAllowRepayRvw() && FrequencyUtil.isFrqDate(financeMain.getRepayRvwFrq(), curSchdDate)) {
			curSchd.setRvwOnSchDate(true);
		} else {
			curSchd.setRvwOnSchDate(false);
		}

		// Set Capitalize On date based on frequency
		if (FrequencyUtil.isFrqDate(financeMain.getRepayCpzFrq(), curSchdDate)) {
			curSchd.setCpzOnSchDate(true);
		} else {
			curSchd.setCpzOnSchDate(false);
		}

		// Find whether record with next schedule date already exist in schedule
		// details
		boolean isSchdExist = false;
		for (int i = maturityIndex; i >= 0; i--) {
			if (finScheduleData.getFinanceScheduleDetails().get(i).getSchDate()
			        .equals(nextSchdDate)) {
				isSchdExist = true;
				finScheduleData.getFinanceScheduleDetails().get(i).setRepayOnSchDate(isSetRepay);
				break;
			}

			if (finScheduleData.getFinanceScheduleDetails().get(i).getSchDate().after(nextSchdDate)) {
				isSchdExist = false;
				break;
			}
		}

		// Reset New Schedule Record if record not found
		if (!isSchdExist) {
			finScheduleData = resetNewSchdDetail(finScheduleData, nextSchdDate,
			        CalculationConstants.SCHDFLAG_RPY);

			finScheduleData.getFinanceMain().setNumberOfTerms(
			        finScheduleData.getFinanceMain().getNumberOfTerms() + 1);
			finScheduleData.getFinanceMain().setCalTerms(financeMain.getCalTerms() + 1);
			finScheduleData.getFinanceMain().setMaturityDate(nextSchdDate);
			finScheduleData.getFinanceMain().setCalMaturity(nextSchdDate);

		}

		// Set Profit dates between current schedule and next repayment

		finScheduleData = setOtherSchdDates(finScheduleData, financeMain.getRepayPftFrq(),
		        curSchdDate, CalculationConstants.SCHDFLAG_PFT);

		// Set Profit review dates between current schedule and next repayment
		if (financeMain.isAllowRepayRvw()) {
			finScheduleData = setOtherSchdDates(finScheduleData, financeMain.getRepayRvwFrq(),
			        curSchdDate, CalculationConstants.SCHDFLAG_RVW);
		}

		// Set Capitalization dates between current schedule and next repayment
		if (financeMain.isAllowRepayCpz()) {
			finScheduleData = setOtherSchdDates(finScheduleData, financeMain.getRepayCpzFrq(),
			        curSchdDate, CalculationConstants.SCHDFLAG_CPZ);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: resetNewSchdDetail
	 * Description	: Reset Schedule Detail
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData resetNewSchdDetail(FinScheduleData finScheduleData, Date nextSchdDate,
	        int scheduleFlag) {
		logger.debug("Entering");

		// Set Next Repayment Date
		FinanceScheduleDetail sd = new FinanceScheduleDetail();

		int lastIndex = finScheduleData.getFinanceScheduleDetails().size() - 1;

		sd.setFinReference(finScheduleData.getFinanceMain().getFinReference());
		sd.setSchDate(nextSchdDate);
		sd.setDefSchdDate(nextSchdDate);
		sd.setPftOnSchDate(false);
		sd.setCpzOnSchDate(false);
		sd.setRepayOnSchDate(false);
		sd.setRvwOnSchDate(false);
		sd.setBaseRate(finScheduleData.getFinanceScheduleDetails().get(lastIndex - 1).getBaseRate());
		sd.setSplRate(finScheduleData.getFinanceScheduleDetails().get(lastIndex - 1).getSplRate());
		sd.setMrgRate(finScheduleData.getFinanceScheduleDetails().get(lastIndex - 1).getMrgRate());
		sd.setActRate(finScheduleData.getFinanceScheduleDetails().get(lastIndex - 1).getActRate());
		sd.setCalculatedRate(finScheduleData.getFinanceScheduleDetails().get(lastIndex - 1)
		        .getCalculatedRate());
		sd.setSchdMethod(finScheduleData.getFinanceScheduleDetails().get(lastIndex - 1)
		        .getSchdMethod());
		sd.setDisbOnSchDate(false);
		sd.setDownpaymentOnSchDate(false);
		sd.setBalanceForPftCal(BigDecimal.ZERO);
		sd.setNoOfDays(0);
		sd.setDayFactor(BigDecimal.ZERO);
		sd.setProfitCalc(BigDecimal.ZERO);
		sd.setProfitSchd(BigDecimal.ZERO);
		sd.setPrincipalSchd(BigDecimal.ZERO);
		sd.setRepayAmount(BigDecimal.ZERO);
		sd.setProfitBalance(BigDecimal.ZERO);
		sd.setDisbAmount(BigDecimal.ZERO);
		sd.setDownPaymentAmount(BigDecimal.ZERO);
		sd.setCpzAmount(BigDecimal.ZERO);
		sd.setClosingBalance(BigDecimal.ZERO);
		sd.setProfitFraction(BigDecimal.ZERO);
		sd.setPrvRepayAmount(BigDecimal.ZERO);
		sd.setSpecifier(CalculationConstants.MATURITY);

		sd = setcurSchdFlags(sd, scheduleFlag);

		finScheduleData.getFinanceScheduleDetails().add(sd);

		finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData
		        .getFinanceScheduleDetails()));

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: setOtherSchdDates
	 * Description	: Reset Other Schedule dates than repay dates
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setOtherSchdDates(FinScheduleData finScheduleData, String frequency,
	        Date curStartDate, int scheduleFlag) {
		logger.debug("Entering");

		boolean isSchdEventMarked = false;

		while (!isSchdEventMarked) {
			// Get Next Schedule Date

			Date nextSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(FrequencyUtil
			        .getNextDate(frequency, 1, curStartDate, HolidayHandlerTypes.MOVE_NONE, false)
			        .getNextFrequencyDate(), PennantConstants.dateFormat));

			int sdSize = finScheduleData.getFinanceScheduleDetails().size();
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(
			        sdSize - 1);

			// Next Schedule Date is after last repayment date
			if (nextSchdDate.after(finScheduleData.getFinanceMain().getCalMaturity())) {
				isSchdEventMarked = true;
				break;
			}

			for (int i = sdSize - 1; i > 0; i--) {
				curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

				if (nextSchdDate.equals(curSchd.getSchDate())) {

					curSchd = setcurSchdFlags(curSchd, scheduleFlag);
					finScheduleData.getFinanceScheduleDetails().set(i, curSchd);
					curStartDate = nextSchdDate;
					break;
				} else {
					if (nextSchdDate.after(curSchd.getSchDate())
					        && nextSchdDate.compareTo(finScheduleData.getFinanceMain()
					                .getCalMaturity()) <= 0) {
						// Set Schedule Dates in between Previous schedule (Cur
						// Schedule and
						// Last Repayment Date)
						finScheduleData = resetNewSchdDetail(finScheduleData, nextSchdDate,
						        scheduleFlag);
						curStartDate = nextSchdDate;
						break;
					}
				}
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: setcurSchdFlags
	 * Description	: R
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */
	private FinanceScheduleDetail setcurSchdFlags(FinanceScheduleDetail curSchd, int scheduleFlag) {

		switch (scheduleFlag) {
		case 0:
			curSchd.setPftOnSchDate(true);
			break;

		case 1:
			curSchd.setRvwOnSchDate(true);
			break;
		case 2:
			curSchd.setCpzOnSchDate(true);
			break;
		case 3:
			curSchd.setRepayOnSchDate(true);
			break;
		default:
			break;
		}

		return curSchd;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: setRpyInstructDetails
	 * Description	: Set Repay Instruction Details 
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setRpyInstructDetails(FinScheduleData finScheduleData, Date fromDate,
	        Date toDate, BigDecimal repayAmount, String SchdMethod) {
		logger.debug("Entering");

		BigDecimal nextInstructAmount = BigDecimal.ZERO;
		Date nextInstructDate = null;
		String nextInstructSchdMethod = null;

		boolean isAddNewInstruction = true;
		int instructIndex = -1;
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		// Find next date for instruction
		if (DateUtility.compare(toDate, financeMain.getMaturityDate()) >= 0) {
			nextInstructDate = financeMain.getMaturityDate();
		} else {
			int sdSize = finScheduleData.getFinanceScheduleDetails().size();

			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

				if (curSchd.getSchDate().after(toDate)) {
					nextInstructDate = curSchd.getSchDate();
					nextInstructSchdMethod = curSchd.getSchdMethod();
					break;
				}
			}
			// Next instruction amount and schedule method
			instructIndex = fetchRpyInstruction(finScheduleData, nextInstructDate);

			if (instructIndex >= 0) {
				nextInstructAmount = finScheduleData.getRepayInstructions().get(instructIndex)
				        .getRepayAmount();
				nextInstructSchdMethod = finScheduleData.getRepayInstructions().get(instructIndex)
				        .getRepaySchdMethod();
			}
		}

		// Remove any instructions between fromdate and todate
		for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {
			RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(i);

			if (curInstruction.getRepayDate().compareTo(fromDate) >= 0
			        && curInstruction.getRepayDate().compareTo(toDate) <= 0) {
				// finScheduleData.getRepayInstructions().remove(curInstruction);
				finScheduleData.getRepayInstructions().remove(i);
				i = i - 1;
			}

			if (curInstruction.getRepayDate().equals(nextInstructDate)) {
				isAddNewInstruction = false;
			}
		}

		finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData
		        .getRepayInstructions()));

		// Add repay instructions on from date
		RepayInstruction ri = new RepayInstruction();
		ri.setRepayDate(fromDate);
		ri.setRepayAmount(repayAmount);
		ri.setRepaySchdMethod(SchdMethod);

		finScheduleData.getRepayInstructions().add(ri);

		// Add (reset) repay instruction after todate
		if (toDate.compareTo(financeMain.getMaturityDate()) >= 0 || !isAddNewInstruction) {
			finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData
			        .getRepayInstructions()));
			return finScheduleData;
		}

		if (nextInstructDate.compareTo(fromDate) > 0) {
			ri = new RepayInstruction();
			ri.setRepayDate(nextInstructDate);
			ri.setRepayAmount(nextInstructAmount);
			ri.setRepaySchdMethod(nextInstructSchdMethod);
			finScheduleData.getRepayInstructions().add(ri);
		}

		finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData
		        .getRepayInstructions()));

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: getRpyInstructDetails
	 * Description	: Get Repay Instruction Details 
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData getRpyInstructDetails(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		int riSize = finScheduleData.getRepayInstructions().size();
		BigDecimal instructAmount = BigDecimal.ZERO;

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		Date fromDate = financeMain.getFinStartDate();
		Date toDate = financeMain.getMaturityDate();
		String fromSchdMethod = null;
		String toSchdMethod = null;

		indexStart = 0;

		for (int j = 0; j < riSize; j++) {
			RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(j);
			if (j == 0) {
				fromDate = curInstruction.getRepayDate();
				instructAmount = curInstruction.getRepayAmount();
				fromSchdMethod = curInstruction.getRepaySchdMethod();
				continue;
			}

			toDate = curInstruction.getRepayDate();
			toSchdMethod = curInstruction.getRepaySchdMethod();

			finScheduleData = setRpyChanges(finScheduleData, fromDate, toDate, instructAmount,
			        fromSchdMethod);
			fromDate = toDate;
			fromSchdMethod = toSchdMethod;

			instructAmount = curInstruction.getRepayAmount();
		}

		if (toDate.compareTo(financeMain.getMaturityDate()) <= 0) {
			toDate = financeMain.getMaturityDate();
			setRpyChanges(finScheduleData, fromDate, toDate, instructAmount, fromSchdMethod);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: getDefInsructions
	 * Description	: Get Deferred Payment Instruction Details 
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData getDefInsructions(FinScheduleData finScheduleData) {

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		for (int i = schdIndex + 1; i < sdSize; i++) {
			finScheduleData = setDefermentOnSchd(finScheduleData, i);
		}

		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: fetchRpyInstruction
	 * Description	: Fetch Repay Instruction index by date
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */

	private int fetchRpyInstruction(FinScheduleData finScheduleData, Date instructDate) {

		int riSize = finScheduleData.getRepayInstructions().size();
		int j = -1;

		for (int i = 0; i < riSize; i++) {
			RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(i);

			if (curInstruction.getRepayDate().after(instructDate)) {
				break;
			}

			j = i;
		}

		return j;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: setRpyChanges
	 * Description	: Set Repay Changes
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setRpyChanges(FinScheduleData finScheduleData, Date fromDate,
	        Date toDate, BigDecimal instructAmount, String schdMethod) {
		logger.debug("Entering");

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		for (int i = indexStart; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			Date curSchdDate = curSchd.getSchDate();

			if (curSchdDate.compareTo(fromDate) >= 0 && curSchdDate.before(toDate)) {

				curSchd.setSchdMethod(schdMethod);

				if (curSchd.isRepayOnSchDate() && !curSchd.isDefered()) {
					if (schdMethod.equals(CalculationConstants.EQUAL)) {
						curSchd.setRepayAmount(instructAmount);
					} else if (schdMethod.equals(CalculationConstants.PFT)) {
						curSchd.setRepayAmount(BigDecimal.ZERO);
					} else {
						curSchd.setPrincipalSchd(instructAmount);
					}
				}

				if (curSchd.isDefered()) {
					curSchd.setRepayAmount(BigDecimal.ZERO);
				}

			} else if (curSchd.getSchDate().compareTo(toDate) >= 0) {
				indexStart = i;
				break;
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: setAddDeferment
	 * Description	: Add New Deferment
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setAddDeferment(FinScheduleData finScheduleData,
	        BigDecimal defPrincipal, BigDecimal defProfit, int defAdjTerms, String schdMethod) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String recaltype = financeMain.getRecalType();

		// Add deferment header record
		DefermentHeader dh = new DefermentHeader();
		dh.setDeferedSchdDate(financeMain.getEventFromDate());
		dh.setDefSchdPrincipal(defPrincipal);
		dh.setDefSchdProfit(defProfit);
		dh.setDefRecalType(recaltype);
		dh.setDefTillDate(financeMain.getRecalToDate());

		finScheduleData.getDefermentHeaders().add(dh);
		finScheduleData.setDefermentHeaders(sortDefHeaders(finScheduleData.getDefermentHeaders()));

		// Add deferment details for maturity adjustments
		if (recaltype.equals(CalculationConstants.RPYCHG_ADJMDT)) {
			finScheduleData = addDefermentRepay(finScheduleData, finScheduleData.getFinanceMain()
			        .getEventFromDate(), defPrincipal, defProfit, financeMain.getMaturityDate(),
			        defPrincipal, defProfit);
		}

		// Add deferment details to new term
		if (recaltype.equals(CalculationConstants.RPYCHG_ADDTERM)) {
			// Get Adjustment Limits parameters from system values table

			int maxFinYears = Integer.parseInt(SystemParameterDetails.getSystemParameterValue(
			        "MAX_FIN_YEARS").toString());

			// Set the limits based on system values table
			Date lastDateLimit = new Date();

			lastDateLimit = DateUtility.addYears(
			        finScheduleData.getFinanceMain().getFinStartDate(), maxFinYears);

			finScheduleData = addOneTerm(finScheduleData, lastDateLimit, schdMethod, false);

			// Reset new term as non repayment and defered repayment
			finScheduleData.getFinanceScheduleDetails()
			        .get(finScheduleData.getFinanceScheduleDetails().size() - 1)
			        .setRepayOnSchDate(false);

			finScheduleData.getFinanceScheduleDetails()
			        .get(finScheduleData.getFinanceScheduleDetails().size() - 1)
			        .setDeferedPay(true);

			finScheduleData = addDefermentRepay(finScheduleData, financeMain.getEventFromDate(),
			        defPrincipal, defProfit, financeMain.getMaturityDate(), defPrincipal, defProfit);

		}

		// Add deferment details for TillMDT and TillDate
		if (recaltype.equals(CalculationConstants.RPYCHG_TILLDATE)
		        || recaltype.equals(CalculationConstants.RPYCHG_TILLMDT)) {

			BigDecimal totDefered = defPrincipal.add(defProfit);
			BigDecimal calDefSchdPerTerm = totDefered.divide(BigDecimal.valueOf(defAdjTerms), 0,
			        RoundingMode.HALF_DOWN);

			BigDecimal schdDefPrincipal = BigDecimal.ZERO;
			BigDecimal schdDefProfit = BigDecimal.ZERO;

			BigDecimal defPrincipalBal = defPrincipal;
			BigDecimal defProfitBal = defProfit;

			for (int i = indexStart; i < (indexEnd + 1); i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

				if (financeMain.isExcludeDeferedDates() && curSchd.isDefered()) {
					continue;
				}

				if (i == indexEnd) {
					finScheduleData = addDefermentRepay(finScheduleData, finScheduleData
					        .getFinanceMain().getEventFromDate(), defPrincipal, defProfit,
					        curSchd.getSchDate(), defPrincipalBal, defProfitBal);

				} else {

					if (calDefSchdPerTerm.compareTo(defProfitBal) >= 0) {
						schdDefProfit = defProfitBal;
					} else {
						schdDefProfit = calDefSchdPerTerm;
					}

					schdDefPrincipal = calDefSchdPerTerm.subtract(schdDefProfit);
					defProfitBal = defProfitBal.subtract(schdDefProfit);
					defPrincipalBal = defPrincipalBal.subtract(schdDefPrincipal);

					finScheduleData = addDefermentRepay(finScheduleData, finScheduleData
					        .getFinanceMain().getEventFromDate(), defPrincipal, defProfit,
					        curSchd.getSchDate(), schdDefPrincipal, schdDefProfit);
				}
			}
		}
		
		//Deferment Details Setting to Schedule 
		for (int i = 0; i < finScheduleData.getDefermentDetails().size(); i++) {
			
			DefermentDetail detail = finScheduleData.getDefermentDetails().get(i);
			if (detail.getDeferedSchdDate().equals(financeMain.getEventFromDate())) {
				
				for (int k = 0; k < finScheduleData.getFinanceScheduleDetails().size(); k++) {
					FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(k);
					if(curSchd.getSchDate().compareTo(detail.getDeferedRpyDate()) == 0){
						curSchd.setDefRepaySchd(curSchd.getDefRepaySchd().add(detail.getDefRpySchdPft().add(detail.getDefRpySchdPri())));
						curSchd.setDefProfitSchd(curSchd.getDefProfitSchd().add(detail.getDefRpySchdPft()));
						curSchd.setDefPrincipalSchd(curSchd.getDefPrincipalSchd().add(detail.getDefRpySchdPri()));
						break;
					}
				}
			}
		}
		
		
		//Adjust Closing Balances after Deferment
		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
	        
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if(i == 0){
				curSchd.setClosingBalance(curSchd.getDisbAmount().add(curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : 
					curSchd.getFeeChargeAmt()).subtract(curSchd.getDownPaymentAmount()));
				continue;
			}
			
			FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(i - 1);
			curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, finScheduleData.getFinanceMain().getRepayRateBasis()));
        }
		
		finScheduleData.getFinanceMain().setScheduleMaintained(true);
		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: addDefermentRepay
	 * Description	: Add New Deferment Repay
	 * Process		: 
	 * ________________________________________________________________________________________________________________
	 */
	private FinScheduleData addDefermentRepay(FinScheduleData finScheduleData, Date schdDate,
	        BigDecimal schdDefPrincipal, BigDecimal schdDefProfit, Date rpySchdDate,
	        BigDecimal rpySchdDefPrincipal, BigDecimal rpySchdDefProfit) {
		logger.debug("Entering");

		// Add deferment detail record
		DefermentDetail dd = new DefermentDetail();
		dd.setDeferedSchdDate(schdDate);
		dd.setDefSchdProfit(schdDefProfit);
		dd.setDefSchdPrincipal(schdDefPrincipal);
		dd.setDeferedRpyDate(rpySchdDate);
		dd.setDefRpySchdPft(rpySchdDefProfit);
		dd.setDefRpySchdPri(rpySchdDefPrincipal);
		dd.setDefRpySchdPftBal(BigDecimal.ZERO);
		dd.setDefRpySchdPriBal(BigDecimal.ZERO);
		dd.setDefPaidPftTillDate(BigDecimal.ZERO);
		dd.setDefPaidPriTillDate(BigDecimal.ZERO);
		dd.setDefPftBalance(BigDecimal.ZERO);
		dd.setDefPriBalance(BigDecimal.ZERO);

		finScheduleData.getDefermentDetails().add(dd);
		finScheduleData.setDefermentDetails(sortDefDetailsBySchdDate(finScheduleData.getDefermentDetails()));

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData setDefermentOnSchd(FinScheduleData finScheduleData, int indexPos) {
		logger.debug("Entering");

		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(indexPos);
		curSchd.setDefered(false);

		for (int i = 0; i < finScheduleData.getDefermentHeaders().size(); i++) {
			if (finScheduleData.getDefermentHeaders().get(i).getDeferedSchdDate()
			        .equals(curSchd.getSchDate())) {
				curSchd.setDefered(true);
				curSchd.setDefPrincipal(finScheduleData.getDefermentHeaders().get(i)
				        .getDefSchdPrincipal());
				curSchd.setDefProfit(finScheduleData.getDefermentHeaders().get(i)
				        .getDefSchdProfit());
				break;
			}
		}

		curSchd.setDeferedPay(false);

		BigDecimal totDeferRepayPri = BigDecimal.ZERO;
		BigDecimal totDeferRepayPft = BigDecimal.ZERO;

		for (int i = 0; i < finScheduleData.getDefermentDetails().size(); i++) {
			if (finScheduleData.getDefermentDetails().get(i).getDeferedRpyDate()
			        .equals(curSchd.getSchDate())) {
				curSchd.setDeferedPay(true);
				totDeferRepayPri = totDeferRepayPri.add(finScheduleData.getDefermentDetails()
				        .get(i).getDefRpySchdPri());
				totDeferRepayPft = totDeferRepayPft.add(finScheduleData.getDefermentDetails()
				        .get(i).getDefRpySchdPft());
			}
		}

		curSchd.setDefPrincipalSchd(totDeferRepayPri);
		curSchd.setDefProfitSchd(totDeferRepayPft);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/** Grace period schedule calculation for reducing rate */
	private FinScheduleData setFirstAndLastAmt(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		for (int i = 1; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (curSchd.isRepayOnSchDate()) {
				if (!isFirstAdjSet) {
					finScheduleData.getFinanceMain().setFirstRepay(curSchd.getRepayAmount());
					isFirstAdjSet = true;
				}
				finScheduleData.getFinanceMain().setLastRepay(curSchd.getRepayAmount());
			}

		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/* ****************************************************************************************************************
	 * COMMON CALCULATION METHODS 
	 * ****************************************************************************************************************
	 */

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: calSchdProcess
	 * Description	: Calculate Schedule Process 
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/** To Calculate the Amounts for given schedule */
	private FinScheduleData calSchdProcess(FinScheduleData finScheduleData, Boolean isCalFlat) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		// SET VARIOUS UNATTENDED DATES
		if (finMain.getLastDepDate() == null) {
			finMain.setLastDepDate(finMain.getFinStartDate());
		}

		if (finMain.getLastRepayCpzDate() == null) {
			finMain.setLastRepayCpzDate(finMain.getGrcPeriodEndDate());
		}

		if (finMain.getLastRepayPftDate() == null) {
			finMain.setLastRepayPftDate(finMain.getGrcPeriodEndDate());
		}

		if (finMain.getLastRepayRvwDate() == null) {
			finMain.setLastRepayRvwDate(finMain.getFinStartDate());
		}

		if (!finMain.isAllowGrcPftRvw()) {
			finMain.setNextGrcPftRvwDate(finMain.getGrcPeriodEndDate());
		}

		if (!finMain.isAllowRepayRvw()) {
			finMain.setNextRepayRvwDate(finMain.getMaturityDate());
		}

		if (!finMain.isAllowGrcCpz()) {
			finMain.setNextGrcCpzDate(finMain.getGrcPeriodEndDate());
		}
		if (!finMain.isAllowRepayCpz()) {
			finMain.setNextRepayCpzDate(finMain.getMaturityDate());
		}

		// START PROCESS
		finScheduleData = fetchGraceCurRates(finScheduleData);
		finScheduleData = fetchRepayCurRates(finScheduleData);

		finScheduleData = getRpyInstructDetails(finScheduleData);

		/* Grace Schedule calculation */
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

		if (finScheduleData.getFinanceMain().isEqualRepay()
		        && finScheduleData.getFinanceMain().isCalculateRepay()
		        && !finScheduleData.getFinanceMain().getScheduleMethod()
		                .equals(CalculationConstants.PFT)) {
			equalRepayCal(finScheduleData, isCalFlat);
		}

		//Set Total Amounts After Calculations
		finScheduleData = setFinanceTotals(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: fetchGraceCurRates
	 * Description	: SET CURRENT GRACE RATES 
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	private FinScheduleData fetchGraceCurRates(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		Date dateAllowedChange = finScheduleData.getFinanceMain().getLastRepayRvwDate();

		//PROFIT REVIEW NOT ALLOWED THEN DO NO REBUILD
		//PRADEEP COMMENTED BELOW.
		//if (!finScheduleData.getFinanceMain().isAllowGrcPftRvw()) {
		//return finScheduleData;
		//}

		//PROFIT LAST REVIEW IS ON OR AFTER GRACE PERIOD END THEN NOT ALLOWED THEN DO NOT SET
		if (dateAllowedChange.compareTo(finScheduleData.getFinanceMain().getGrcPeriodEndDate()) >= 0) {
			return finScheduleData;
		}

		//FIND ALLOWED RATE CHANGE DATE
		String rvwRateApplFor = finScheduleData.getFinanceMain().getFinGrcRvwRateApplFor();
		if (!StringUtils.trimToEmpty(rvwRateApplFor).equals(PennantConstants.RVW_ALL)) {
			dateAllowedChange = findAllowedChangeDate(finScheduleData, rvwRateApplFor,
			        dateAllowedChange);
		}

		//Set Rates from Allowed Date and Grace Period End Date
		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			schdIndex = i;

			if (curSchd.getSchDate().before(dateAllowedChange)) {
				continue;
			}

			if (curSchd.getSchDate().compareTo(
			        finScheduleData.getFinanceMain().getGrcPeriodEndDate()) >= 0) {
				break;
			}

			// Indicative Rate condition
			if (curSchd.getSchDate().compareTo(
			        finScheduleData.getFinanceMain().getNextGrcPftRvwDate()) >= 0
			        && (finScheduleData.getFinanceMain().isGrcAlwIndRate())) {
				RateDetail rateDetail = new RateDetail(finScheduleData.getFinanceMain()
				        .getGrcIndBaseRate(), null, BigDecimal.ZERO, curSchd.getSchDate());
				curSchd.setCalculatedRate(RateUtil.getRefRate(rateDetail).getNetRefRateLoan());

				curSchd.setCalOnIndRate(true);
				continue;
			}

			// Fetch current rates from DB
			if (curSchd.getBaseRate() != null && !StringUtils.trimToEmpty(curSchd.getBaseRate()).equals("")) {
				if (curSchd.isRvwOnSchDate() || i == 0) {
					RateDetail rateDetail = new RateDetail(curSchd.getBaseRate(),
					        curSchd.getSplRate(), curSchd.getMrgRate(), curSchd.getSchDate());
					curSchd.setCalculatedRate(RateUtil.getRefRate(rateDetail).getNetRefRateLoan());
				} else {
					curSchd.setCalculatedRate(finScheduleData.getFinanceScheduleDetails()
					        .get(i - 1).getCalculatedRate());
				}

				curSchd.setCalOnIndRate(false);
			}

		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: fetchRepayCurRates
	 * Description	: SET CURRENT REPAY RATES 
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	private FinScheduleData fetchRepayCurRates(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		Date dateAllowedChange = finScheduleData.getFinanceMain().getLastRepayRvwDate();
		//PROFIT REVIEW NOT ALLOWED THEN DO NO REBUILD
		//PRADEEP COMMENTED BELOW.
		//if (!finScheduleData.getFinanceMain().isAllowRepayRvw()) {
		//return finScheduleData;
		//}

		//PROFIT LAST REVIEW IS ON OR AFTER MATURITY THEN NOT ALLOWED THEN DO NOT SET
		if (dateAllowedChange.compareTo(finScheduleData.getFinanceMain().getMaturityDate()) >= 0) {
			return finScheduleData;
		}

		//FIND ALLOWED RATE CHANGE DATE
		String rvwRateApplFor = finScheduleData.getFinanceMain().getFinRvwRateApplFor();
		if (!StringUtils.trimToEmpty(rvwRateApplFor).equals(PennantConstants.RVW_ALL)) {
			dateAllowedChange = findAllowedChangeDate(finScheduleData, rvwRateApplFor,
			        dateAllowedChange);
		}

		//Set Rates from Allowed Date and Maturity
		for (int i = schdIndex; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			if (curSchd.getSchDate().before(dateAllowedChange)) {
				continue;
			}

			// Indicative Rate condition
			if (curSchd.getSchDate().compareTo(
			        finScheduleData.getFinanceMain().getNextRepayRvwDate()) >= 0
			        && (finScheduleData.getFinanceMain().isAlwIndRate())) {
				RateDetail rateDetail = new RateDetail(finScheduleData.getFinanceMain()
				        .getIndBaseRate(), null, BigDecimal.ZERO, curSchd.getSchDate());
				curSchd.setCalculatedRate(RateUtil.getRefRate(rateDetail).getNetRefRateLoan());

				curSchd.setCalOnIndRate(true);
				continue;
			}

			// Fetch current rates from DB
			if (curSchd.getBaseRate() != null && !StringUtils.trimToEmpty(curSchd.getBaseRate()).equals("")) {
				if (curSchd.isRvwOnSchDate() || i == 0) {
					RateDetail rateDetail = new RateDetail(curSchd.getBaseRate(),
					        curSchd.getSplRate(), curSchd.getMrgRate(), curSchd.getSchDate());
					curSchd.setCalculatedRate(RateUtil.getRefRate(rateDetail).getNetRefRateLoan());
				} else {
					curSchd.setCalculatedRate(finScheduleData.getFinanceScheduleDetails()
					        .get(i - 1).getCalculatedRate());
				}
				curSchd.setCalOnIndRate(false);
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: findAllowedChangeDate
	 * Description	: FIND DATE FROM WHICH RATE CHANGE IS ALLOWED 
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	private Date findAllowedChangeDate(FinScheduleData finScheduleData, String rvwRateApplFor,
	        Date dateAllowedChange) {
		logger.debug("Entering");

		int size = finScheduleData.getFinanceScheduleDetails().size();
		for (int i = 0; i < size; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (curSchd.getSchDate().compareTo(dateAllowedChange) <= 0) {
				continue;
			}

			if (StringUtils.trimToEmpty(rvwRateApplFor).equals(PennantConstants.RVW_UNPAID_INST)) {
				if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
					dateAllowedChange = curSchd.getSchDate();
				} else {
					break;
				}
			} else {
				if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
					if (curSchd.isRvwOnSchDate()) {
						dateAllowedChange = curSchd.getSchDate();
					}
				} else {
					break;
				}
			}
		}

		logger.debug("Leaving");
		return dateAllowedChange;
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: graceSchdCal
	 * Description	: Grace period schedule calculation for reducing rate 
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	private FinScheduleData graceSchdCal(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		totalGrossGraceProfit = BigDecimal.ZERO;
		totalGrossRepayProfit = BigDecimal.ZERO;
		totalGraceCpz = BigDecimal.ZERO;
		totalRepayCpz = BigDecimal.ZERO;
		totalRepayAmt = BigDecimal.ZERO;
		totalEarlyPaidBal = BigDecimal.ZERO;
		pftForSelectedPeriod = BigDecimal.ZERO;
		schdIndex = 0;
		
		//Setting Fraction Decimal Values to Reset
		BigDecimal calIntFraction = BigDecimal.ZERO;
		
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String repayRateBasis = financeMain.getRepayRateBasis();
		
		if(finScheduleData.getFinanceType() != null){
			financeMain.setCpzAtGraceEnd(finScheduleData.getFinanceType().isFinIsIntCpzAtGrcEnd());
		}

		/* Loop through grace period schedule */
		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			if (StringUtils.trimToEmpty(curSchd.getSchdMethod()).equals("")) {
		        curSchd.setSchdMethod(financeMain.getGrcSchdMthd());
	        }
			
			// If first record no calculation is required
			if (i == 0) {
				curSchd.setBalanceForPftCal(BigDecimal.ZERO);
				curSchd.setNoOfDays(0);
				curSchd.setDayFactor(BigDecimal.ZERO);
				curSchd.setProfitCalc(BigDecimal.ZERO);
				curSchd.setProfitSchd(BigDecimal.ZERO);
				curSchd.setPrincipalSchd(BigDecimal.ZERO);
				curSchd.setRepayAmount(BigDecimal.ZERO);
				curSchd.setProfitBalance(BigDecimal.ZERO);
				curSchd.setCpzAmount(BigDecimal.ZERO);
				curSchd.setProfitFraction(BigDecimal.ZERO);
				curSchd.setClosingBalance(curSchd.getDisbAmount().add(curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : 
					curSchd.getFeeChargeAmt()).subtract(curSchd.getDownPaymentAmount()));
				curSchd.setRvwOnSchDate(true);
				
			} else {
				/* not first record then do the calculation */
				FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(
				        i - 1);

				if (!financeMain.getGrcRateBasis().equals(CalculationConstants.RATE_BASIS_F)) {
					curSchd.setBalanceForPftCal(prvSchd.getClosingBalance().subtract(
					        totalEarlyPaidBal));
				} else {
					curSchd.setBalanceForPftCal(prvSchd.getBalanceForPftCal()
					        .add(prvSchd.getDisbAmount()).subtract(prvSchd.getDownPaymentAmount()).add(
					        		prvSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : prvSchd.getFeeChargeAmt())
					        .add(prvSchd.getCpzAmount()));
				}

				/*
				 * Find days and days factor based in profit days basis between two dates
				 */
				curSchd.setNoOfDays(DateUtility.getDaysBetween(curSchd.getSchDate(),
				        prvSchd.getSchDate()));
				curSchd.setDayFactor(CalculationUtil.getInterestDays(prvSchd.getSchDate(),
				        curSchd.getSchDate(), financeMain.getGrcProfitDaysBasis()));

				/* Calculate interest and set interest payment details */
				BigDecimal calint = CalculationUtil.calInterest(prvSchd.getSchDate(),
				        curSchd.getSchDate(), curSchd.getBalanceForPftCal(),
				        financeMain.getGrcProfitDaysBasis(), prvSchd.getCalculatedRate());

				calint = calint.add(calIntFraction);
				calIntFraction = calint.subtract(round(calint));
				calint = round(calint);

				curSchd.setProfitCalc(calint);
				curSchd.setRepayComplete(false);

				if (finScheduleData.getFinanceMain().isAllowGrcRepay()) {
					if (!curSchd.isRepayOnSchDate()) {
						curSchd.setRepayAmount(BigDecimal.ZERO);
						curSchd.setPrincipalSchd(BigDecimal.ZERO);
						curSchd.setProfitSchd(BigDecimal.ZERO);
					} else {

						calPftPriRpy(finScheduleData, curSchd, prvSchd);
						curSchd.setRepayAmount(curSchd.getProfitSchd().add(
						        curSchd.getPrincipalSchd()));
					}

				} else {
					curSchd.setPrincipalSchd(BigDecimal.ZERO);
					curSchd.setProfitSchd(BigDecimal.ZERO);
					curSchd.setRepayAmount(BigDecimal.ZERO);
				}

				defPrincipalBal = defPrincipalBal.add(curSchd.getDefPrincipal()).subtract(
				        curSchd.getDefPrincipalSchd());
				defProfitBal = defProfitBal.add(curSchd.getDefProfit()).subtract(
				        curSchd.getDefProfitSchd());

				totalEarlyPaidBal = totalEarlyPaidBal.add(curSchd.getEarlyPaid());

				curSchd.setDefPrincipalBal(defPrincipalBal);
				curSchd.setDefProfitBal(defProfitBal);

				//EarlyPaid Balance
				if (prvSchd.getEarlyPaidBal().compareTo(BigDecimal.ZERO) > 0
				        || curSchd.getEarlyPaid().compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal earlyPaidBal = curSchd.getEarlyPaid().add(prvSchd.getEarlyPaidBal());
					/*if (curSchd.getDefSchdDate().compareTo(financeMain.getEventFromDate()) > 0) {
						earlyPaidBal = earlyPaidBal.subtract(curSchd.getPrincipalSchd());
					}*/

					if(financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_D)){
						earlyPaidBal = earlyPaidBal.subtract(curSchd.getPrincipalSchd()).subtract(curSchd.getProfitSchd());
					} else {
						earlyPaidBal = earlyPaidBal.subtract(curSchd.getPrincipalSchd());
					}

					if (earlyPaidBal.compareTo(BigDecimal.ZERO) >= 0) {
						curSchd.setEarlyPaidBal(earlyPaidBal);
					} else {
						curSchd.setEarlyPaidBal(BigDecimal.ZERO);
					}
				} else {
					curSchd.setEarlyPaidBal(BigDecimal.ZERO);
				}

				/* Balance unpaid interest */
				curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd,
				        financeMain.getScheduleMethod()));

				/*
				 * If grace period end date and allow grace period is true but capitalize on schedule date is false OR
				 * capitalize at end of grace is True THEN force it to true
				 */
				if ((curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0 && financeMain
				        .isAllowGrcCpz())
				        || (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0)
				        && financeMain.isCpzAtGraceEnd()) {
					curSchd.setCpzOnSchDate(true);
				}

				/* If capitalize on this schedule date */
				if (curSchd.isCpzOnSchDate()) {
					curSchd.setCpzAmount(curSchd.getProfitBalance());
				} else {
					curSchd.setCpzAmount(BigDecimal.ZERO);
				}

				totalGrossGraceProfit = totalGrossGraceProfit.add(curSchd.getProfitCalc());
				totalGraceCpz = totalGraceCpz.add(curSchd.getCpzAmount());
				totalRepayAmt = totalRepayAmt.add(curSchd.getRepayAmount());
				curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, repayRateBasis));

				if (financeMain.getEventToDate()!=null) {
					if (curSchd.getSchDate().compareTo(financeMain.getEventFromDate()) > 0
							&& curSchd.getSchDate().compareTo(financeMain.getEventToDate()) <= 0) {
						pftForSelectedPeriod = pftForSelectedPeriod.add(curSchd.getProfitCalc());
					}
				}
			}

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0) {
				schdIndex = i;
				break;
			}

			schdIndex = i;
		}

		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: repaySchdCal
	 * Description	: Repay period schedule calculation for reducing rate
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	private FinScheduleData repaySchdCal(FinScheduleData finScheduleData, Boolean isCalFlat) {
		logger.debug("Entering");

		isExactMatch = false;
		isRepayComplete = false;
		curTerm = 0;
		isFirstAdjSet = false;
		isLastAdjSet = false;
		defPrincipalBal = BigDecimal.ZERO;
		defProfitBal = BigDecimal.ZERO;
		totalPrincipal = BigDecimal.ZERO;
		
		totalGrossRepayProfit = BigDecimal.ZERO;
		totalRepayCpz = BigDecimal.ZERO;
		
		BigDecimal calIntFraction = BigDecimal.ZERO;
		boolean isSkipCloseBalCal = false;
		newMaturityIndex = finScheduleData.getFinanceScheduleDetails().size()-1;
		
		// FIND LAST REPAYMENT SCHEDULE DATE
		int size = finScheduleData.getFinanceScheduleDetails().size();
		lastRepayDate = finScheduleData.getFinanceScheduleDetails().get(size - 1).getSchDate();

		for (int i = size - 1; i >= 0; i--) {
			if (finScheduleData.getFinanceScheduleDetails().get(i).isRepayOnSchDate()) {
				lastRepayDate = finScheduleData.getFinanceScheduleDetails().get(i).getSchDate();
				break;
			}
		}

		BigDecimal calInt = new BigDecimal(0.0);
		firstAdjAmount = BigDecimal.ZERO;
		lastAdjAmount = BigDecimal.ZERO;
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String repayRateBasis = financeMain.getRepayRateBasis();

		if (!finScheduleData.getDefermentHeaders().isEmpty()) {
			finScheduleData = getDefInsructions(finScheduleData);
		}

		for (int i = schdIndex + 1; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {

			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(i - 1);
			
			if (StringUtils.trimToEmpty(curSchd.getSchdMethod()).equals("")) {
		        curSchd.setSchdMethod(financeMain.getScheduleMethod());
	        }

			// Correcting any wrong settings of period specifier in between the code execution
			curSchd.setSpecifier(CalculationConstants.REPAY);

			if (financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_F)
			        || (financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_C) && isCalFlat)) {
				
				curSchd.setBalanceForPftCal(prvSchd.getBalanceForPftCal().add(prvSchd.getDisbAmount()).subtract(prvSchd.getDownPaymentAmount())
				        .add(prvSchd.getCpzAmount()).add(prvSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : prvSchd.getFeeChargeAmt()));
				
			} else {
				
				if (financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_D)){
					if (isRepayComplete) {
						curSchd.setBalanceForPftCal(BigDecimal.ZERO);    
                    } else {
                    	curSchd.setBalanceForPftCal(prvSchd.getClosingBalance().subtract(defPrincipalBal).subtract(defProfitBal)
    					        .subtract(totalEarlyPaidBal));
                    }
					
				}else{
					curSchd.setBalanceForPftCal(prvSchd.getClosingBalance().subtract(defPrincipalBal)
					        .subtract(totalEarlyPaidBal));
				}
			}

			/* Count Repay schedules only */
			if ((curSchd.isRepayOnSchDate() || curSchd.isDeferedPay())
			        && curSchd.getSchDate().after(finScheduleData.getFinanceMain().getGrcPeriodEndDate())) {
				curTerm = curTerm + 1;
			}

			curSchd.setNoOfDays(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchd.getSchDate()));
			curSchd.setDayFactor(CalculationUtil.getInterestDays(prvSchd.getSchDate(), curSchd.getSchDate(), financeMain.getProfitDaysBasis()));

			if (!isRepayComplete) {
				if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {
					calInt = CalculationUtil.calInterest(prvSchd.getSchDate(),
					        curSchd.getSchDate(), curSchd.getBalanceForPftCal(),
					        financeMain.getProfitDaysBasis(), prvSchd.getCalculatedRate());
					 
					calInt = calInt.add(calIntFraction);
					calIntFraction = calInt.subtract(round(calInt));
					calInt = round(calInt);
				} else {
					calInt = BigDecimal.ZERO;
				}
				curSchd.setProfitCalc(calInt);
				curSchd.setRepayComplete(false);
			} else {
				calInt = BigDecimal.ZERO;
				curSchd.setProfitCalc(BigDecimal.ZERO);
				curSchd.setRepayComplete(true);
			}

			// On Maturity Date set Deferment Details
			if (curSchd.getSchDate().equals(financeMain.getMaturityDate())) {
				curSchd.setDefPrincipalSchd(defPrincipalBal);
				curSchd.setDefProfitSchd(defProfitBal);
				curSchd.setDefRepaySchd(defPrincipalBal.add(defProfitBal));
				curSchd.setDefPrincipalBal(BigDecimal.ZERO);
				curSchd.setDefProfitBal(BigDecimal.ZERO);
			}

		
			// LAST REPAYMENT DATE
			
			if (curSchd.getSchDate().equals(lastRepayDate)) {
				curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));

				if (financeMain.isLovDescAdjClosingBal()) {
					calPftPriRpy(finScheduleData, curSchd, prvSchd);
				} else {
					
					//NEED TO THINK ABOUT EARLY PAID BALANCE DEDUCTION??? PRADEEP & SIVA
					if (financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_D)) {

/*	TODO To be deleted. 12 March'2014					
 * The below lines replaced with new code because discounted principal is being increased for the profit reduced 
						BigDecimal remainingPriBal = financeMain.getFinAmount().subtract(totalPrincipal).subtract(defPrincipalBal);
						
						if(totalPrincipal.compareTo(financeMain.getFinAmount()) > 0){
							curSchd.setPrincipalSchd(financeMain.getFinAmount().subtract(totalPrincipal).subtract(defPrincipalBal));
						}else{
							curSchd.setPrincipalSchd(BigDecimal.ZERO);
						}

						curSchd.setRefundOrWaiver(curSchd.getRefundOrWaiver().add(prvSchd.getClosingBalance().subtract(
						        prvSchd.getDefPrincipalBal()).subtract(prvSchd.getDefProfitBal()).subtract(curSchd.getProfitSchd()).subtract(curSchd.getPrincipalSchd())));
						curSchd.setRefundOrWaiver(prvSchd.getClosingBalance().subtract(totalPrincipal));
*/
						
						curSchd.setPrincipalSchd(financeMain.getFinAmount().subtract(totalPrincipal));
						
						curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
						
						
						//Different than normal repayments or discounted repayments other than last date
						BigDecimal endBal = getClosingBalance(curSchd, prvSchd, repayRateBasis).subtract(curSchd.getRefundOrWaiver());
						
						//To overcome issue with deferred after actual maturity.
						endBal = endBal.subtract(defPrincipalBal).subtract(defProfitBal);
						
						if(endBal.compareTo(BigDecimal.ZERO) < 0){
							curSchd.setClosingBalance(BigDecimal.ZERO);
						}else{
							curSchd.setClosingBalance(endBal);
						}
						
						isSkipCloseBalCal = true;
		
                    } else {
                    	curSchd.setPrincipalSchd(prvSchd.getClosingBalance().subtract(
    					        prvSchd.getDefPrincipalBal()));
                    	curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

        				curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, repayRateBasis));

					}
				}
				
				// SET LAST REPAYMENT DATE AND AMOUNT
				//finScheduleData.getFinanceMain().setLastRepayDate(lastRepayDate);

				if (curSchd.getSchdMethod().equals(CalculationConstants.EQUAL)) {
					finScheduleData.getFinanceMain().setLastRepay(curSchd.getRepayAmount());
				} else if (curSchd.getSchdMethod().equals(CalculationConstants.PFT)) {
					finScheduleData.getFinanceMain().setLastRepay(curSchd.getProfitSchd());
				} else if (curSchd.getSchdMethod().equals(CalculationConstants.NOPAY)) {
					finScheduleData.getFinanceMain().setLastRepay(BigDecimal.ZERO);
				} else {
					finScheduleData.getFinanceMain().setLastRepay(curSchd.getPrincipalSchd());
				}

				if (!isLastAdjSet) {
					lastAdjAmount = fetchCalAmount(curSchd);
				}

				financeMain.setCalTerms(curTerm);
				if (financeMain.getNumberOfTerms() == 1) {
					financeMain.setFirstRepay(curSchd.getRepayAmount());
					finScheduleData.getFinanceMain().setLastRepayDate(
					        finScheduleData.getFinanceMain().getFinStartDate());
				}
			} else {
				// TODO TEST OVERLAPPING DEFERPAYMENT
				if (!curSchd.isRepayOnSchDate()) {
					if (financeMain.isFinRepayPftOnFrq()) {
						if (financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_D)) {
							curSchd.setRepayAmount(BigDecimal.ZERO);
							curSchd.setPrincipalSchd(BigDecimal.ZERO);
							curSchd.setProfitSchd(BigDecimal.ZERO);
                        } else {
                        	curSchd.setProfitSchd(curSchd.getProfitCalc().add(
    						        prvSchd.getProfitBalance()));
    						curSchd.setPrincipalSchd(BigDecimal.ZERO);
    						curSchd.setRepayAmount(curSchd.getProfitSchd());
						}
						
					} else {
						curSchd.setRepayAmount(BigDecimal.ZERO);
						curSchd.setPrincipalSchd(BigDecimal.ZERO);
						curSchd.setProfitSchd(BigDecimal.ZERO);
					}
				} else {

					BigDecimal outstandingPrincipal = prvSchd.getClosingBalance().subtract(
					        prvSchd.getDefPrincipalBal());

					// In case principal outstanding is less than requested
					// payment & schedule method is EQUAL
					if (curSchd.getSchdMethod().equals(CalculationConstants.EQUAL) ){

						if(!financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_D)){
							if((curSchd.getRepayAmount()).compareTo(outstandingPrincipal) > 0){
								curSchd.setRepayAmount(outstandingPrincipal.add(prvSchd.getProfitBalance()
										.add(curSchd.getProfitCalc())));
								if (!isRepayComplete) {
									newMaturityIndex = i;    
                                }
								isRepayComplete = true;
							}
						} else {
							if (isRepayComplete) {
	                            curSchd.setProfitSchd(BigDecimal.ZERO);
	                            curSchd.setPrincipalSchd(BigDecimal.ZERO);
								curSchd.setRepayAmount(BigDecimal.ZERO);
                            }
						} 
						
						
						/* To be deleted 12 March'2014
						 * else if((curSchd.getRepayAmount()).compareTo(prvSchd.getClosingBalance()) > 0){
							
							//The below lines repalced with new code becuase discounted proncipal is being increased for the profit reduced 
							if (calProfitToSchd(curSchd, prvSchd).compareTo(curSchd.getRepayAmount()) < 0) {
								curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));
							} else {
								curSchd.setProfitSchd(curSchd.getRepayAmount());
							}
							
							curSchd.setPrincipalSchd(financeMain.getFinAmount().subtract(totalPrincipal).subtract(defPrincipalBal));
							
							curSchd.setRefundOrWaiver(prvSchd.getClosingBalance().subtract(
							        prvSchd.getDefPrincipalBal()).subtract(prvSchd.getDefProfitBal()).subtract(curSchd.getProfitSchd()).subtract(curSchd.getPrincipalSchd()));
							
							curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

							//Different than normal repayment or discounted repayments other than last date
							curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, repayRateBasis).subtract(curSchd.getRefundOrWaiver()));
							
							isSkipCloseBalCal = true;

							isRepayComplete = true;
						}*/
					}

					// In case principal outstanding is less than requested
					// payment & schedule method is EQUAL
					if ((curSchd.getSchdMethod().equals(CalculationConstants.PRI) || curSchd
					        .getSchdMethod().equals(CalculationConstants.PRI_PFT))
					        && curSchd.getPrincipalSchd().compareTo(outstandingPrincipal) > 0) {
						curSchd.setPrincipalSchd(outstandingPrincipal);
						isRepayComplete = true;
					}

					calPftPriRpy(finScheduleData, curSchd, prvSchd);
				}

				if (!curSchd.getSchDate().equals(financeMain.getMaturityDate())) {
					defPrincipalBal = defPrincipalBal.add(curSchd.getDefPrincipal()).subtract(
					        curSchd.getDefPrincipalSchd());
					defProfitBal = defProfitBal.add(curSchd.getDefProfit()).subtract(
					        curSchd.getDefProfitSchd());

					curSchd.setDefPrincipalBal(defPrincipalBal);
					curSchd.setDefProfitBal(defProfitBal);
				}

			}

			curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd,
			        financeMain.getScheduleMethod()));
			// Capitalize OR not
			if (curSchd.isCpzOnSchDate()) {
				curSchd.setCpzAmount(curSchd.getProfitBalance());
			} else {
				curSchd.setCpzAmount(BigDecimal.ZERO);
			}

			//EarlyPaid Balance
			
			if(financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_D)){
				totalEarlyPaidBal = totalEarlyPaidBal.add(curSchd.getEarlyPaid()).subtract(curSchd.getPrincipalSchd()).subtract(curSchd.getProfitSchd());
			} else {
				totalEarlyPaidBal = totalEarlyPaidBal.add(curSchd.getEarlyPaid()).subtract(curSchd.getPrincipalSchd());
			}
			
			if (totalEarlyPaidBal.compareTo(BigDecimal.ZERO)<=0) {
				totalEarlyPaidBal = BigDecimal.ZERO;
            }
			
			if (prvSchd.getEarlyPaidBal().compareTo(BigDecimal.ZERO) > 0
			        || curSchd.getEarlyPaid().compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal earlyPaidBal = curSchd.getEarlyPaid().add(prvSchd.getEarlyPaidBal());
				/*if (curSchd.getDefSchdDate().compareTo(financeMain.getEventFromDate()) > 0) {
					earlyPaidBal = earlyPaidBal.subtract(curSchd.getPrincipalSchd());
				}*/
				
				if(financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_D)){
					earlyPaidBal = earlyPaidBal.subtract(curSchd.getPrincipalSchd()).subtract(curSchd.getProfitSchd());
				} else {
					earlyPaidBal = earlyPaidBal.subtract(curSchd.getPrincipalSchd());
				}

				if (earlyPaidBal.compareTo(BigDecimal.ZERO) >= 0) {
					curSchd.setEarlyPaidBal(earlyPaidBal);
				} else {
					curSchd.setEarlyPaidBal(BigDecimal.ZERO);
				}
			} else {
				curSchd.setEarlyPaidBal(BigDecimal.ZERO);
			}

			//totalPrincipal = totalPrincipal.add(curSchd.getPrincipalSchd()).add(curSchd.getDefPrincipalSchd());
			totalPrincipal = totalPrincipal.add(curSchd.getPrincipalSchd()).add(curSchd.getDefPrincipal());
			
			/*			Discounted deal in Equation calculate the profit on total outstanding amount. 
						But in case of any schedule changes like early repayments should check and make sure outstanding principal
						not crossed the original principal
			*/
			if (financeMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_D)
			        && (totalPrincipal.compareTo(financeMain.getFinAmount()) > 0)) {
				BigDecimal schdExtra = totalPrincipal.subtract(financeMain.getFinAmount());
				
				curSchd.setPrincipalSchd(curSchd.getPrincipalSchd().subtract(schdExtra));
				curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
				totalPrincipal = financeMain.getFinAmount();
				isRepayComplete = true;
			}
			
			totalGrossRepayProfit = totalGrossRepayProfit.add(curSchd.getProfitCalc());
			totalRepayCpz = totalRepayCpz.add(curSchd.getCpzAmount());
			totalRepayAmt = totalRepayAmt.add(curSchd.getRepayAmount().add(
			        curSchd.getDefProfit().add(curSchd.getDefPrincipal())));
			
		
			if (financeMain.getEventToDate()!=null) {
				if (curSchd.getSchDate().compareTo(financeMain.getEventFromDate()) > 0
				        && curSchd.getSchDate().compareTo(financeMain.getEventToDate()) <= 0) {
					pftForSelectedPeriod = pftForSelectedPeriod.add(curSchd.getProfitCalc());
				}
            }

			if (!isSkipCloseBalCal) {
				curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, repayRateBasis));    
            }
			
			financeMain.setCalMaturity(curSchd.getSchDate());
		}

		// Correcting Any wrong settings of period specifier in between the code
		// execution
		if (!financeMain.isLovDescAdjClosingBal()) {
			finScheduleData.getFinanceScheduleDetails().get(size - 1)
			        .setSpecifier(CalculationConstants.MATURITY);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: calPftPriRpy
	 * Description	: Calculate profit and principal for schedule payment
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	private FinanceScheduleDetail calPftPriRpy(FinScheduleData finScheduleData,
	        FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		//NO PAYMENT: Applicable for Grace Period And REPAYMENT period with PFT or PRI+PFT)
		if (curSchd.getSchdMethod().equals(CalculationConstants.NOPAY)) {
			curSchd.setProfitSchd(BigDecimal.ZERO);
			curSchd.setPrincipalSchd(BigDecimal.ZERO);
			
			//EQUAL PAYMENT: Applicable for REPAYMENT period
		} else if (curSchd.getSchdMethod().equals(CalculationConstants.EQUAL)) {
			if (calProfitToSchd(curSchd, prvSchd).compareTo(curSchd.getRepayAmount()) < 0) {
				curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));
			} else {
				curSchd.setProfitSchd(curSchd.getRepayAmount());
			}

			curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
	        
			//PRINCIPAL ONLY: Applicable for REPAYMENT period
        } else if (curSchd.getSchdMethod().equals(CalculationConstants.PRI)) {
			curSchd.setProfitSchd(BigDecimal.ZERO);
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			
			//CALCULATED PROFIT ONLY: Applicable for GRACE & REPAYMENT period
        } else if (curSchd.getSchdMethod().equals(CalculationConstants.PFT)) {
			//IF Scheduled Profit cannot change (Effective Rate Calculation) Then leave actual scheduled else calculate
			if (!isProtectSchdPft) {
				curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));	
            } 
			curSchd.setPrincipalSchd(BigDecimal.ZERO);
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			
			//PRINCIPAL + CALCULATED PROFIT: Applicable for GRACE & REPAYMENT period
        } else if (curSchd.getSchdMethod().equals(CalculationConstants.PRI_PFT)) {
			//IF Scheduled Profit cannot change (Effective Rate Calculation) Then leave actual scheduled else calculate
			if (!isProtectSchdPft) {
				curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));	
            } 
			
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			
			//NOPAYMENT IN GRACE SCHEDULES AND COMPLETE PAYMENT AT GRACE END DATE: Applicable for GRACE period Only
        } else if (curSchd.getSchdMethod().equals(CalculationConstants.GRCENDPAY)) {
        	curSchd.setProfitSchd(BigDecimal.ZERO);
			curSchd.setPrincipalSchd(BigDecimal.ZERO);
			
			if (curSchd.getSchDate().equals(financeMain.getGrcPeriodEndDate())) {
				curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));
            }
		}

		
		//COMMON CODE FOR all schedule methods
		curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
		
		// store first repay amount
		if (curTerm == 1) {
			financeMain.setFirstRepay(curSchd.getRepayAmount());
		}
		
		// store last repay amount
				financeMain.setLastRepay(curSchd.getRepayAmount());

		if (recalStartDate != null) {
			if (curSchd.getSchDate().compareTo(recalStartDate) >= 0
			        && !isFirstAdjSet
			        && !curSchd.isDefered()
			        && curSchd.getSchDate().after(financeMain.getGrcPeriodEndDate())) {
				isFirstAdjSet = true;
				
				//For PRI_PFT Method only, First Adjustment amounts taken as Principal Amount
				if (curSchd.getSchdMethod().equals(CalculationConstants.PRI_PFT)) {
					firstAdjAmount = curSchd.getPrincipalSchd();
				}else{
					firstAdjAmount = curSchd.getRepayAmount();
				}
			}
		}

		if (recalEndDate != null) {
			if (recalEndDate.compareTo(curSchd.getSchDate()) <= 0 && isFirstAdjSet
			        && !curSchd.isDefered()) {
				isLastAdjSet = true;
				//For PRI_PFT Method only, Last Adjustment amounts taken as Principal Amount
				if (curSchd.getSchdMethod().equals(CalculationConstants.PRI_PFT)) {
					lastAdjAmount = curSchd.getPrincipalSchd();
				}else{
					lastAdjAmount = curSchd.getRepayAmount();
				}
			}
		}

		logger.debug("Leaving");
		return curSchd;

	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: equalRepayCal
	 * Description	: Iteration process to get equal repay amount
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	private FinScheduleData equalRepayCal(FinScheduleData finScheduleData, Boolean isCalFlat) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		financeMain.setCalculateRepay(false);

		// SET EQUAL REPAYMENT TO TRUE
		isExactMatch = false;
		BigDecimal repayAmountLow = BigDecimal.ZERO;
		BigDecimal repayAmountHigh = BigDecimal.ZERO;
		BigDecimal effectiveRepayAmount = BigDecimal.ZERO;

		BigDecimal minRepayDifference = BigDecimal.ZERO;
		BigDecimal maxRepayDifference = BigDecimal.ZERO;


		Date fromDate = new Date();
		Date toDate = new Date();
		String schdMethod = null;

		
		// Set FROM
		if (recalStartDate != null
		        && recalStartDate.after(finScheduleData.getFinanceMain().getGrcPeriodEndDate())) {
			fromDate = recalStartDate;
			toDate = recalEndDate;
		} else {
			fromDate = finScheduleData.getFinanceMain().getNextRepayDate();
			toDate = finScheduleData.getFinanceMain().getMaturityDate();
		}

		int riSize = finScheduleData.getRepayInstructions().size();
		schdMethod = finScheduleData.getFinanceMain().getScheduleMethod();

		for (int i = 0; i < riSize; i++) {
			RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(i);

			if (curInstruction.getRepayDate().after(fromDate)) {
				schdMethod = curInstruction.getRepaySchdMethod();
				break;
			}
		}
		
		effectiveRepayAmount = BigDecimal.ZERO;
		
		setRpyInstructDetails(finScheduleData, fromDate, toDate,
		        effectiveRepayAmount, schdMethod);

		finScheduleData = getRpyInstructDetails(finScheduleData);
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

		setCompareAmounts(financeMain);

		if (comparisionAmount.compareTo(comparisionToAmount) == 1) {
			repayAmountLow = comparisionToAmount;
			repayAmountHigh = comparisionAmount;
		} else {
			repayAmountLow = comparisionAmount;
			repayAmountHigh = comparisionToAmount;
		}


		for (int i = 0; i < 50; i++) {
			effectiveRepayAmount = (repayAmountLow.add(repayAmountHigh)).divide(number2, 0,
			        RoundingMode.HALF_DOWN);
			// SET EQUAL REPAYMENT AMOUNT AS EFFECTIVE REPAY AMOUNT AND
			// CALL PROCESS

			setRpyInstructDetails(finScheduleData, fromDate, toDate, effectiveRepayAmount,
			        schdMethod);

			finScheduleData = getRpyInstructDetails(finScheduleData);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

			setCompareAmounts(financeMain);

			if (comparisionAmount.compareTo(comparisionToAmount) == 0) {
				isExactMatch = true;
				logger.debug("Leaving");
				return finScheduleData;
			}

			if (repayAmountLow.compareTo(effectiveRepayAmount) == 0
			        || repayAmountHigh.compareTo(effectiveRepayAmount) == 0) {
				break;
			}

			if (comparisionAmount.compareTo(comparisionToAmount) == 1) {
				repayAmountHigh = effectiveRepayAmount;
			} else {
				repayAmountLow = effectiveRepayAmount;
			}
		}

		if (!isExactMatch) {

			setCompareAmounts(financeMain);

			if (repayAmountLow.compareTo(repayAmountHigh) != 0) {
				if (effectiveRepayAmount.compareTo(repayAmountLow) == 0) {
					minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
					effectiveRepayAmount = repayAmountHigh;
				} else {
					maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
					effectiveRepayAmount = repayAmountLow;
				}
				// SET EQUAL REPAYMENT AMOUNT AS EFFECTIVE REPAY AMOUNT AND
				// CALL PROCESS

				setRpyInstructDetails(finScheduleData, fromDate, toDate, effectiveRepayAmount,
				        schdMethod);

				finScheduleData = getRpyInstructDetails(finScheduleData);

				finScheduleData = graceSchdCal(finScheduleData);
				finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

				setCompareAmounts(financeMain);

				if (effectiveRepayAmount.compareTo(repayAmountLow) == 0) {
					minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				} else {
					maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				}

				if (maxRepayDifference.compareTo(minRepayDifference) > 0) {
					effectiveRepayAmount = repayAmountLow;
				} else {
					effectiveRepayAmount = repayAmountHigh;
				}
			}
		}

		// SET EQUAL REPAYMENT AMOUNT AS EFFECTIVE REPAY AMOUNT AND
		// CALL PROCESS
		setRpyInstructDetails(finScheduleData, fromDate, toDate, effectiveRepayAmount, schdMethod);

		finScheduleData = getRpyInstructDetails(finScheduleData);

		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, isCalFlat);
		//}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: setCompareAmounts
	 * Description	: Set amounts for comparision
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	private void setCompareAmounts(FinanceMain financeMain) {
		if (isCompareToExpected) {
			comparisionAmount = expectedResult;
			comparisionToAmount = financeMain.getLastRepay();
		} else {
			comparisionAmount = firstAdjAmount;
			comparisionToAmount = lastAdjAmount;
		}
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: getProfitBalance
	 * Description	: Get profit balance unscheduled till schedule date
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	private BigDecimal getProfitBalance(FinanceScheduleDetail curSchd,
	        FinanceScheduleDetail prvSchd, String schdMethod) {
		if (curSchd.isDefered() && !(schdMethod.equals(CalculationConstants.PRI))) {
			
			return prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())
			        .subtract(curSchd.getProfitSchd()).add(curSchd.getProfitCalc())
			        .subtract(curSchd.getDefProfit());
		} else {
			
			return prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())
			        .add(curSchd.getProfitCalc()).subtract(curSchd.getProfitSchd());
		}
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: getClosingBalance
	 * Description	: Schedule record Closing balance
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	private BigDecimal getClosingBalance(FinanceScheduleDetail curSchd,
	        FinanceScheduleDetail prvSchd, String repayRateBasis) {

		BigDecimal closingBal = BigDecimal.ZERO;

		if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_D)) {
			closingBal = prvSchd
			        .getClosingBalance()
			        .add(curSchd.getDisbAmount())
			        .add(curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : curSchd
			                .getFeeChargeAmt())
			        .subtract(curSchd.getDownPaymentAmount())
			        .subtract(curSchd.getRepayAmount())
			        .add(curSchd.getCpzAmount().subtract(curSchd.getDefPrincipalSchd())
			                .subtract(curSchd.getDefProfitSchd()));
		} else {
			closingBal = prvSchd
			        .getClosingBalance()
			        .add(curSchd.getDisbAmount())
			        .add(curSchd.getFeeChargeAmt() == null ? BigDecimal.ZERO : curSchd
			                .getFeeChargeAmt()).subtract(curSchd.getDownPaymentAmount())
			        .subtract(curSchd.getPrincipalSchd())
			        .add(curSchd.getCpzAmount().subtract(curSchd.getDefPrincipalSchd()));
		}

		return closingBal;
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: getProfitSchd
	 * Description	: Get Profit to be scheduled
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	private BigDecimal calProfitToSchd(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd) {

		if (curSchd.isSchPftPaid()) {
			return curSchd.getSchdPftPaid();
		}
		

		if (curSchd.isDefered()) {
			return prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount());
		} else {
			return prvSchd.getProfitBalance().add(curSchd.getProfitCalc())
			        .subtract(prvSchd.getCpzAmount());
		}
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: round
	 * Description	: To round the BigDecimal value to the basic rounding mode
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	private BigDecimal round(BigDecimal value) {
		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: isDateUnderGivenFrq
	 * Description	: Validate requested date with requested frequency and date
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	/*private boolean isDateUnderGivenFrq(String frq, Date startDate, Date dateToCheck) {
		boolean isfrqDate = false;
		if (dateToCheck.after(startDate)) {
			while (true) {
				Date nextDate = DateUtility.getDate(DateUtility.formatUtilDate(FrequencyUtil
				        .getNextDate(frq, 1, startDate, HolidayHandlerTypes.MOVE_NONE, false)
				        .getNextFrequencyDate(), PennantConstants.dateFormat));
				if (DateUtility.compare(nextDate, dateToCheck) == 0) {
					isfrqDate = true;
					break;
				} else {
					startDate = nextDate;
				}
				if (nextDate.after(dateToCheck)) {
					break;
				}
			}
		}
		return isfrqDate;
	}*/

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * Method		: fetchCalAmount
	 * Description	: Fetch amount to be used for calculation
	 * Process		:
	 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	private BigDecimal fetchCalAmount(FinanceScheduleDetail curSchd) {
		// store last repay amount
		if (curSchd.getSchdMethod().equals(CalculationConstants.EQUAL)) {
			return curSchd.getRepayAmount();
		} else if (curSchd.getSchdMethod().equals(CalculationConstants.PRI)
		        || curSchd.getSchdMethod().equals(CalculationConstants.PRI_PFT)) {
			return curSchd.getPrincipalSchd();
		} else {
			return curSchd.getPrincipalSchd();
		}
	}

	/*
	 * ================================================================================================================
	 * Method 		: procSubSchedule 
	 * Description 	: SUB SCHEDULE 
	 * Process 		: Add Term will add another term to the schedule details.
	 * =================================================================================================================
	 */

	private FinScheduleData procSubSchedule(FinScheduleData finScheduleData, int noOfTerms,
	        Date subSchStartDate, String frqNewSchd) {
		logger.debug("Entering");

		FinScheduleData orgFinScheduleData = null;
		Cloner cloner = new Cloner();
		orgFinScheduleData = cloner.deepClone(finScheduleData);

		isFirstAdjSet = false;
		isLastAdjSet = false;
		isCompareToExpected = false;
		expectedResult = BigDecimal.ZERO;

		recalStartDate = null;
		recalEndDate = null;

		finScheduleData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_TILLMDT);

		// Set the limits based on system values table
		int maxFinYears = Integer.parseInt(SystemParameterDetails.getSystemParameterValue(
		        "MAX_FIN_YEARS").toString());
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtility.addYears(finScheduleData.getFinanceMain().getFinStartDate(),
		        maxFinYears);

		Date newSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(subSchStartDate,
		        PennantConstants.dateFormat));

		for (int i = 0; i < noOfTerms; i++) {
			finScheduleData = addSubScheduleTerm(finScheduleData, lastDateLimit, true, newSchdDate,
			        frqNewSchd);

			if (finScheduleData.getErrorDetails().size() > 0) {
				return orgFinScheduleData;
			}

			newSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(
			        FrequencyUtil.getNextDate(frqNewSchd, 1, newSchdDate,
			                HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
			        PennantConstants.dateFormat));
		}

		//Except first time creation of schedule covert flat rate to reducing will be treated as reducing only
		finScheduleData = calSchdProcess(finScheduleData, false);
		logger.debug("Leaving");
		return finScheduleData;

	}

	/* ________________________________________________________________________________________________________________
	 * Method 		: addSubScheduleTerm
	 * Description	: ADD SubSchedule Term 
	 * Process		: Add SubSchedule Term will add another term to the schedule details.
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData addSubScheduleTerm(FinScheduleData finScheduleData, Date lastDateLimit,
	        boolean isSetRepay, Date newSchdDate, String frqNewSchd) {
		logger.debug("Entering");

		// Maturity Record Index
		int maturityIndex = finScheduleData.getFinanceScheduleDetails().size() - 1;

		// Set After which index new term should be added
		int addTermAfterIndex = maturityIndex;

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(
		        addTermAfterIndex);

		Date curSchdDate = newSchdDate;

		if (newSchdDate.after(lastDateLimit)) {
			// Through Error
			finScheduleData.setErrorDetail(new ErrorDetails("SCH30","ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS", new String[] { " " }));
			return finScheduleData;
		}

		if (curSchd.getSpecifier().equals(CalculationConstants.MATURITY)) {
			curSchd.setSpecifier(CalculationConstants.REPAY);
		}

		// Set Profit On date based on frequency
		if (FrequencyUtil.isFrqDate(financeMain.getRepayPftFrq(), curSchdDate)) {
			curSchd.setPftOnSchDate(true);
		} else {
			curSchd.setPftOnSchDate(false);
		}

		// Set Profit Review On date based on frequency
		if (FrequencyUtil.isFrqDate(financeMain.getRepayRvwFrq(), curSchdDate)) {
			curSchd.setRvwOnSchDate(true);
		} else {
			curSchd.setRvwOnSchDate(false);
		}

		// Set Capitalize On date based on frequency
		if (FrequencyUtil.isFrqDate(financeMain.getRepayCpzFrq(), curSchdDate)) {
			curSchd.setCpzOnSchDate(true);
		} else {
			curSchd.setCpzOnSchDate(false);
		}

		// Reset New Schedule Record if record not found
		finScheduleData = resetNewSchdDetail(finScheduleData, newSchdDate,
		        CalculationConstants.SCHDFLAG_RPY);

		finScheduleData.getFinanceMain().setNumberOfTerms(
		        finScheduleData.getFinanceMain().getNumberOfTerms() + 1);
		finScheduleData.getFinanceMain().setCalTerms(financeMain.getCalTerms() + 1);
		finScheduleData.getFinanceMain().setMaturityDate(newSchdDate);
		finScheduleData.getFinanceMain().setCalMaturity(newSchdDate);

		// Set Profit dates between current schedule and next repayment

		finScheduleData = setOtherSchdDates(finScheduleData, frqNewSchd, curSchdDate,
		        CalculationConstants.SCHDFLAG_PFT);

		// Set Profit review dates between current schedule and next repayment
		if (financeMain.isAllowRepayRvw()) {
			finScheduleData = setOtherSchdDates(finScheduleData, frqNewSchd, curSchdDate,
			        CalculationConstants.SCHDFLAG_RVW);
		}

		// Set Capitalization dates between current schedule and next repayment
		if (financeMain.isAllowRepayCpz()) {
			finScheduleData = setOtherSchdDates(finScheduleData, frqNewSchd, curSchdDate,
			        CalculationConstants.SCHDFLAG_CPZ);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for Calculating Schedule from FLAT to REDUCE rate with Effective Rate calculations
	 * 
	 * @param finScheduleData
	 * @param eventFromDate
	 * @param eventToDate
	 * @param scheduleType
	 * @param totalDesiredProfit
	 * @return
	 */
	private FinScheduleData calEffectiveRate(FinScheduleData finScheduleData, String scheduleType,
			BigDecimal totalDesiredProfit, Date effcFromDate, Date effcToDate, Boolean forInfoOnly) {
		logger.debug("Entering");

		if (forInfoOnly) {
			BigDecimal effRateofReturn = BigDecimal.ZERO;
			BigDecimal returnCalProfit = BigDecimal.ZERO;

			effRateofReturn = finScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate();
			finScheduleData = procChangeRate(finScheduleData, "", "", BigDecimal.ZERO,
					effRateofReturn, false , false);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);

			if (CalculationConstants.GRACE.equals(scheduleType)) {
				returnCalProfit = totalGrossGraceProfit;
			} else if (CalculationConstants.REPAY.equals(scheduleType)) {
				returnCalProfit = totalGrossRepayProfit;
			} else if (CalculationConstants.TOTAL.equals(scheduleType)) {
				returnCalProfit = totalGrossGraceProfit.add(totalGrossRepayProfit);
			} else if (CalculationConstants.SELECT.equals(scheduleType)) {
				returnCalProfit = pftForSelectedPeriod;
			}

			if (totalDesiredProfit.compareTo(returnCalProfit) == 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}
		}

		isExactMatch = false;
		BigDecimal amtDiffWithLowAssumption = BigDecimal.ZERO;
		BigDecimal amtDiffWithHighAssumption = BigDecimal.ZERO;

		BigDecimal lowAssumptionRate = BigDecimal.ZERO;
		BigDecimal highAssumptionRate = BigDecimal.ZERO;

		BigDecimal effRateofReturn = BigDecimal.ZERO;
		BigDecimal returnCalProfit = BigDecimal.ZERO;

		if (effcFromDate != null) {
			finScheduleData.getFinanceMain().setEventFromDate(effcFromDate);
		}

		if (effcToDate != null) {
			finScheduleData.getFinanceMain().setEventToDate(effcToDate);
		}

		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);

		if (CalculationConstants.GRACE.equals(scheduleType)) {
			returnCalProfit = totalGrossGraceProfit;

			if (totalDesiredProfit.compareTo(returnCalProfit) >= 0) {
				lowAssumptionRate = finScheduleData.getFinanceMain().getGrcPftRate();
				highAssumptionRate = lowAssumptionRate.multiply(new BigDecimal(10));
			} else {
				lowAssumptionRate = BigDecimal.ZERO;
				highAssumptionRate = finScheduleData.getFinanceMain().getGrcPftRate();
			}

		} else if (CalculationConstants.REPAY.equals(scheduleType)) {
			returnCalProfit = totalGrossRepayProfit;

			if (totalDesiredProfit.compareTo(returnCalProfit) >= 0) {
				lowAssumptionRate = finScheduleData.getFinanceMain().getRepayProfitRate();
				highAssumptionRate = lowAssumptionRate.multiply(new BigDecimal(10));
			} else {
				lowAssumptionRate = BigDecimal.ZERO;
				highAssumptionRate = finScheduleData.getFinanceMain().getRepayProfitRate();
			}

		} else if (CalculationConstants.TOTAL.equals(scheduleType)) {
			returnCalProfit = totalGrossRepayProfit.add(totalGrossGraceProfit);
			lowAssumptionRate = BigDecimal.ZERO;
			highAssumptionRate = new BigDecimal(1000);

		} else if (CalculationConstants.SELECT.equals(scheduleType)) {
			returnCalProfit = pftForSelectedPeriod;
			lowAssumptionRate = BigDecimal.ZERO;
			highAssumptionRate = new BigDecimal(1000);
		}

		for (int i = 0; i < 50; i++) {

			effRateofReturn = (lowAssumptionRate.add(highAssumptionRate)).divide(number2, 9,
					RoundingMode.HALF_DOWN);

			finScheduleData = procChangeRate(finScheduleData, "", "", BigDecimal.ZERO,
					effRateofReturn, false , false);

			finScheduleData = getRpyInstructDetails(finScheduleData);

			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);

			if (CalculationConstants.GRACE.equals(scheduleType)) {
				returnCalProfit = totalGrossGraceProfit;
			} else if (CalculationConstants.REPAY.equals(scheduleType)) {
				returnCalProfit = totalGrossRepayProfit;
			} else if (CalculationConstants.TOTAL.equals(scheduleType)) {
				returnCalProfit = totalGrossGraceProfit.add(totalGrossRepayProfit);
			} else if (CalculationConstants.SELECT.equals(scheduleType)) {
				returnCalProfit = pftForSelectedPeriod;
			}

			if (totalDesiredProfit.compareTo(returnCalProfit) == 0) {
				isExactMatch = true;
				logger.debug("Leaving");
				return finScheduleData;
			}

			if (returnCalProfit.compareTo(totalDesiredProfit) < 0) {
				//Increase effective calculation rate
				lowAssumptionRate = effRateofReturn;
			} else {
				//Decrease effective calculation rate
				highAssumptionRate = effRateofReturn;
			}

		}
		if (!isExactMatch && (lowAssumptionRate.compareTo(highAssumptionRate) != 0)) {

			if (effRateofReturn.compareTo(lowAssumptionRate) == 0) {
				amtDiffWithLowAssumption = (totalDesiredProfit.subtract(returnCalProfit)).abs();
				effRateofReturn = highAssumptionRate;
			} else {
				amtDiffWithHighAssumption = (totalDesiredProfit.subtract(returnCalProfit)).abs();
				effRateofReturn = lowAssumptionRate;
			}

			//Calculate Schedule Building process with Effective Rate
			finScheduleData = procChangeRate(finScheduleData, "", "", BigDecimal.ZERO,
					effRateofReturn, false,  false);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);

			if (effRateofReturn.compareTo(lowAssumptionRate) == 0) {
				amtDiffWithLowAssumption = (totalDesiredProfit.subtract(returnCalProfit)).abs();
			} else {
				amtDiffWithHighAssumption = (totalDesiredProfit.subtract(returnCalProfit)).abs();
			}

			if (amtDiffWithHighAssumption.compareTo(amtDiffWithLowAssumption) > 0) {
				effRateofReturn = lowAssumptionRate;
			} else {
				effRateofReturn = highAssumptionRate;
			}
		}

		finScheduleData = procChangeRate(finScheduleData, "", "", BigDecimal.ZERO,
				effRateofReturn, false , false);

		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);

		logger.debug("Leaving");
		return finScheduleData;
	}
	
	
	/* ________________________________________________________________________________________________________________
	 * Method : setFinanceTotals
	 * Description: Set Finance Totals after Grace and Repayment schedules calculation
	 * ________________________________________________________________________________________________________________
	 */
	private FinScheduleData setFinanceTotals(FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setTotalGraceCpz(totalGraceCpz);
		finMain.setTotalGracePft(totalGrossGraceProfit.subtract(totalGraceCpz));
		finMain.setTotalGrossGrcPft(totalGrossGraceProfit);
		finMain.setTotalCpz(totalGraceCpz.add(totalRepayCpz));
		finMain.setTotalProfit(totalGrossGraceProfit.add(totalGrossRepayProfit)
		        .subtract(totalGraceCpz).subtract(totalRepayCpz));
		finMain.setTotalGrossPft(totalGrossGraceProfit.add(totalGrossRepayProfit));
		finMain.setTotalRepayAmt(totalRepayAmt);

		finScheduleData = setFirstAndLastAmt(finScheduleData);
		return finScheduleData;
	}


	/* ________________________________________________________________________________________________________________
	 * Method : sortRepayInstructions
	 * Description: Sort Repay Instructions
	 * ________________________________________________________________________________________________________________
	 */
	private FinScheduleData calculateNewEMI(FinScheduleData finScheduleData, Date evtFromDate,
	        Date evtToDate, String recaltype, String schdMethod, BigDecimal repayAmount,
	        BigDecimal totOrginalPayment) {
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		int termTobeAdjusted = 0;
		Date getSchdDate = new Date();
		BigDecimal calRepayAmount = BigDecimal.ZERO;
		BigDecimal schdAmountFixed = BigDecimal.ZERO;
		BigDecimal curSchdAmount = BigDecimal.ZERO;
		
		
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		
		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			getSchdDate = curSchd.getSchDate();

			if (curSchd.isRepayOnSchDate()) {
				if (financeMain.getScheduleMethod().equals(CalculationConstants.EQUAL)) {
					curSchdAmount = curSchd.getRepayAmount();
				} else if (financeMain.getScheduleMethod().equals(CalculationConstants.PRI)
				        || financeMain.getScheduleMethod().equals(CalculationConstants.PRI_PFT)) {
					curSchdAmount = curSchd.getPrincipalSchd();
				}

				//Find Total scheduled till recalculation starts AND after recalculation Ends
				if (getSchdDate.compareTo(evtFromDate) < 0) {
					schdAmountFixed = schdAmountFixed.add(curSchdAmount);
				} else if (getSchdDate.compareTo(evtToDate) <= 0) {
					schdAmountFixed = schdAmountFixed.add(repayAmount);
				} else if (getSchdDate.compareTo(recalStartDate) < 0) {
					schdAmountFixed = schdAmountFixed.add(curSchdAmount);
				} else if (getSchdDate.compareTo(recalEndDate) > 0) {
					schdAmountFixed = schdAmountFixed.add(curSchdAmount);
				}

				if (getSchdDate.compareTo(recalStartDate) >= 0
				        && getSchdDate.compareTo(recalEndDate) <= 0) {
					termTobeAdjusted = termTobeAdjusted + 1;
				}

				// Set expected result even for schedule method PROFIT
				if (curSchd.isRepayOnSchDate() && isCompareToExpected) {
					expectedResult = fetchCalAmount(curSchd);
				}
			}
		}
		
		if (termTobeAdjusted >=0) {
			if (financeMain.getScheduleMethod().equals(CalculationConstants.EQUAL)) {
				if (recaltype.equals(CalculationConstants.RPYCHG_ADDTERM)) {
					calRepayAmount = financeMain.getFinAmount().add(financeMain.getTotalProfit()).subtract(schdAmountFixed);    
                } else {
                	calRepayAmount = totOrginalPayment.subtract(schdAmountFixed);
                }
			} else if (financeMain.getScheduleMethod().equals(CalculationConstants.PRI)
			        || financeMain.getScheduleMethod().equals(CalculationConstants.PRI_PFT)) {
				calRepayAmount = financeMain.getFinAmount().subtract(schdAmountFixed);
			}
			
			if (termTobeAdjusted >0) {
				calRepayAmount = calRepayAmount.divide(BigDecimal.valueOf(termTobeAdjusted), 0, RoundingMode.HALF_DOWN);
			}
			
			finScheduleData = setRpyInstructDetails(finScheduleData, recalStartDate, recalEndDate,
			        calRepayAmount, schdMethod);
        }
		
		return finScheduleData;
	}
	

	/* >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * SORTING METHODS
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	/* ________________________________________________________________________________________________________________
	 * Method : sortSchdDetails
	 * Description: Sort Schedule Details
	 * ________________________________________________________________________________________________________________
	 */
	public List<FinanceScheduleDetail> sortSchdDetails(
	        List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					if (detail1.getSchDate().after(detail2.getSchDate())) {
						return 1;
					}
					return 0;
				}
			});
		}

		return financeScheduleDetail;
	}

	/* ________________________________________________________________________________________________________________
	 * Method : sortDefHeaders
	 * Description: Sort Deferment Headers
	 * ________________________________________________________________________________________________________________
	 */
	private List<DefermentHeader> sortDefHeaders(List<DefermentHeader> defermentHeaders) {

		if (defermentHeaders != null && defermentHeaders.size() > 0) {
			Collections.sort(defermentHeaders, new Comparator<DefermentHeader>() {
				@Override
				public int compare(DefermentHeader detail1, DefermentHeader detail2) {
					if (detail1.getDeferedSchdDate().after(detail2.getDeferedSchdDate())) {
						return 1;
					}
					return 0;
				}
			});
		}

		return defermentHeaders;
	}

	/* ________________________________________________________________________________________________________________
	 * Method : sortDefDetailsBySchdDate
	 * Description: Sort Deferment Details by Schedule Date
	 * ________________________________________________________________________________________________________________
	 */
	private List<DefermentDetail> sortDefDetailsBySchdDate(List<DefermentDetail> defermentDetails) {

		if (defermentDetails != null && defermentDetails.size() > 0) {
			Collections.sort(defermentDetails, new Comparator<DefermentDetail>() {
				@Override
				public int compare(DefermentDetail detail1, DefermentDetail detail2) {
					if (detail1.getDeferedSchdDate().after(detail2.getDeferedSchdDate())) {
						return 1;
					}
					return 0;
				}
			});
		}

		return defermentDetails;
	}
	
	
	

	/* ________________________________________________________________________________________________________________
	 * Method : sortRepayInstructions
	 * Description: Sort Repay Instructions
	 * ________________________________________________________________________________________________________________
	 */
	private List<RepayInstruction> sortRepayInstructions(List<RepayInstruction> repayInstructions) {

		if (repayInstructions != null && repayInstructions.size() > 0) {
			Collections.sort(repayInstructions, new Comparator<RepayInstruction>() {
				@Override
				public int compare(RepayInstruction detail1, RepayInstruction detail2) {
					if (detail1.getRepayDate().after(detail2.getRepayDate())) {
						return 1;
					}
					return 0;
				}
			});
		}
		return repayInstructions;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

}
