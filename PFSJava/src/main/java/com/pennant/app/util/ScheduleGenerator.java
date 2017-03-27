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
 * FileName    		:  ScheduleGenerator.java													*                           
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.FrequencyDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class ScheduleGenerator {

	public ScheduleGenerator() {
	}

	private final static Logger	logger	= Logger.getLogger(ScheduleGenerator.class);

	private FinScheduleData		finScheduleData;

	public static FinScheduleData getNewSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		boolean isOverdraft = false;
		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,financeMain.getProductCategory())){
			isOverdraft = true;
		}
		finScheduleData.setErrorDetail(validateFinanceMain(financeMain, finScheduleData.getDisbursementDetails(), isOverdraft));

		/*
		 * //Check for error if (finScheduleData.getErrorDetails() != null) { logger.debug("Leaving - Error"); return
		 * finScheduleData; }
		 */

		if (finScheduleData.getErrorDetails().size() != 0) {
			logger.debug("Leaving - Error");
			return finScheduleData;
		}

		finScheduleData = newSchdProcess(finScheduleData);
		List<Date> schdDateKeyList = new ArrayList<Date>(finScheduleData.getScheduleMap().keySet());
		finScheduleData.getFinanceScheduleDetails().clear();

		Collections.sort(schdDateKeyList);
		for (int j = 0; j < schdDateKeyList.size(); j++) {
			finScheduleData.getFinanceScheduleDetails().add(
					finScheduleData.getScheduleMap().get(schdDateKeyList.get(j)));
		}

		//Applying Rate Margin from Step Policy Details
		/*
		 * if (financeMain.isStepFinance()) { int repayStart = 0; int repayEnd = 0; int stepCount = -1;
		 * FinanceStepPolicyDetail policyDetail = null;
		 * 
		 * for (int i = financeMain.getGraceTerms(); i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
		 * 
		 * FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i); if (repayStart ==
		 * repayEnd) { stepCount = stepCount + 1; if (finScheduleData.getStepPolicyDetails() != null &&
		 * finScheduleData.getStepPolicyDetails().size() > stepCount) { policyDetail =
		 * finScheduleData.getStepPolicyDetails().get(stepCount); repayEnd = repayEnd + policyDetail.getInstallments();
		 * } }
		 * 
		 * if (policyDetail != null) {
		 * curSchd.setCalculatedRate(curSchd.getCalculatedRate().add(policyDetail.getRateMargin())); repayStart++; } } }
		 */

		// Advised Profit Rate Calculation Process & Profit Days Basis , Reference Rates Setting
		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
				// Profit Days Basis Setting
				curSchd.setPftDaysBasis(financeMain.getGrcProfitDaysBasis());

				// Reference Rates Setting
				curSchd.setBaseRate(financeMain.getGraceBaseRate());
				curSchd.setMrgRate(financeMain.getGrcMargin());
				curSchd.setSplRate(financeMain.getGraceSpecialRate());

				// Advised Profit Rate Calculation
				curSchd.setAdvBaseRate(financeMain.getGrcAdvBaseRate());
				curSchd.setAdvMargin(financeMain.getGrcAdvMargin());
				curSchd.setAdvPftRate(financeMain.getGrcAdvPftRate());
			} else {

				// Profit Days Basis Setting
				curSchd.setPftDaysBasis(financeMain.getProfitDaysBasis());

				// Reference Rates Setting
				curSchd.setBaseRate(financeMain.getRepayBaseRate());
				curSchd.setMrgRate(financeMain.getRepayMargin());
				curSchd.setSplRate(financeMain.getRepaySpecialRate());

				// Advised Profit Rate Calculation
				curSchd.setAdvBaseRate(financeMain.getRpyAdvBaseRate());
				curSchd.setAdvMargin(financeMain.getRpyAdvMargin());
				curSchd.setAdvPftRate(financeMain.getRpyAdvPftRate());
			}
		}
		
		finScheduleData.getScheduleMap().clear();

		//prepare Insurance Schedule
		getInsSchedule(finScheduleData, financeMain);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Setting Schedule Frequency Insurance details based on Selection frequency
	 */
	private static void getInsSchedule(FinScheduleData finScheduleData, FinanceMain financeMain) {
		
		// No Insurance Details exists then close the process of calculation
		if(finScheduleData.getFinInsuranceList() == null || finScheduleData.getFinInsuranceList().isEmpty()){
			return;
		}

		//prepare the schedule frequency Insurances
		for (int i = 0; i < finScheduleData.getFinInsuranceList().size(); i++) {
			
			FinInsurances finInsurance = finScheduleData.getFinInsuranceList().get(i);
			if (StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {
				List<FinSchFrqInsurance> schFrqList = getInsuranceSchedule(finScheduleData, finInsurance,
						financeMain.getFinStartDate(), financeMain.getMaturityDate(),false);
				finInsurance.setFinSchFrqInsurances(schFrqList);
			}
		}
	}

	/**
	 * Method for Generating New Schedule Dates List with particulars
	 * 
	 * @param scheduleData
	 * @param frequency
	 * @param startDate
	 * @param endDate
	 * @param includeStartDate
	 * @param scheduleFlag
	 * @return
	 */
	public static FinScheduleData getScheduleDateList(FinScheduleData scheduleData, String frequency,
			Date grcStartDate, Date rpyStartDate, Date endDate) {
		return new ScheduleGenerator(scheduleData, frequency, grcStartDate, rpyStartDate, endDate)
				.getFinScheduleData();
	}

	public ScheduleGenerator(FinScheduleData scheduleData, String frequency, Date grcStartDate, Date rpyStartDate,
			Date endDate) {
		scheduleData.getFinanceScheduleDetails().clear();
		scheduleData = maintainFrqSchdProcess(scheduleData, frequency, grcStartDate, rpyStartDate, endDate);
		List<Date> schdDateKeyList = new ArrayList<Date>(scheduleData.getScheduleMap().keySet());

		Collections.sort(schdDateKeyList);
		int grcTerms = 0;
		int repayTerms = 0;
		for (int j = 0; j < schdDateKeyList.size(); j++) {
			scheduleData.getFinanceScheduleDetails().add(scheduleData.getScheduleMap().get(schdDateKeyList.get(j)));
			if (schdDateKeyList.get(j).compareTo(scheduleData.getFinanceMain().getGrcPeriodEndDate()) <= 0) {
				if (scheduleData.getFinanceMain().getGrcPeriodEndDate()
						.compareTo(scheduleData.getFinanceMain().getFinStartDate()) == 0) {
					continue;
				}
				grcTerms = grcTerms + 1;
			} else {
				repayTerms = repayTerms + 1;
			}
		}

		scheduleData.getFinanceMain().setNumberOfTerms(repayTerms);
		scheduleData.getFinanceMain().setGraceTerms(grcTerms);

		scheduleData.getScheduleMap().clear();
		setFinScheduleData(scheduleData);
	}

	/**
	 * Method for preparing List of Schedule Details with including Details:
	 * 
	 * 1. Schdule Date 2. Schedule Rates (Actual Rate, Calculated Rate) 3. Schedule Specifier 4. Schedule Flag Details
	 * 5. Number of Terms 6. Set Schedule Details List into ScheduleMap
	 * 
	 * =====================================================================================
	 * 
	 * 1. Loop Finance Disbursement Details(Create Schedule Details List based upon Disbursement Details) 2. Allow Grace
	 * Period a) Schedule Detail List Creation based upon Grace Profit Frequency with 'SCHDFLAG_PFT' b) Allow Grace
	 * Repay i) Schedule Detail List Creation based upon Grace Profit Frequency with 'SCHDFLAG_RPY' c) Allow Grace
	 * Profit Review i) Schedule Detail List Creation based upon Grace Profit Review Frequency with 'SCHDFLAG_RVW' d)
	 * Allow Grace Capitalization i) Schedule Detail List Creation based upon Grace Capitalization Frequency with
	 * 'SCHDFLAG_CPZ' 3. Create New Schedule / Use Existing Schedule for Grace Period End Date 4. Schedule Detail List
	 * Creation based upon Repayment Frequency with 'SCHDFLAG_RPY' 5. Schedule Detail List Creation based upon Repayment
	 * Profit Frequency with 'SCHDFLAG_PFT' if RepayPftFrq exists. 6. Allow Repayment Review a) Schedule Detail List
	 * Creation based upon Repayment Review Frequency with 'SCHDFLAG_RVW' 7. Allow Repayment Capitalization a) Schedule
	 * Detail List Creation based upon Repayment Capitalization Frequency with 'SCHDFLAG_CPZ'
	 * 
	 * =======================================================================================
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private static FinScheduleData newSchdProcess(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		for (int i = 0; i < finScheduleData.getDisbursementDetails().size(); i++) {

			FinanceDisbursement disbursementDetail = finScheduleData.getDisbursementDetails().get(i);
			FinanceScheduleDetail schedule = new FinanceScheduleDetail();

			BigDecimal prvTermDisbAmount = BigDecimal.ZERO;
			if (finScheduleData.getScheduleMap() != null
					&& finScheduleData.getScheduleMap().containsKey(disbursementDetail.getDisbDate())) {
				prvTermDisbAmount = finScheduleData.getScheduleMap().get(disbursementDetail.getDisbDate())
						.getDisbAmount();
			}

			schedule.setSchDate(disbursementDetail.getDisbDate());
			schedule.setDefSchdDate(disbursementDetail.getDisbDate());
			schedule.setDisbAmount(disbursementDetail.getDisbAmount().add(prvTermDisbAmount));
			schedule.setFeeChargeAmt(disbursementDetail.getFeeChargeAmt());
			schedule.setInsuranceAmt(disbursementDetail.getInsuranceAmt());
			schedule.setDisbOnSchDate(true);

			if (disbursementDetail.getDisbDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
				schedule.setActRate(financeMain.getGrcPftRate());
				schedule.setCalculatedRate(financeMain.getGrcPftRate());
				schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
			} else {
				schedule.setActRate(financeMain.getRepayProfitRate());
				schedule.setCalculatedRate(financeMain.getRepayProfitRate());
				schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
			}

			if (i == 0) {
				if (financeMain.getDownPayment() != null && financeMain.getDownPayment().compareTo(BigDecimal.ZERO) != 0) {
					schedule.setDownPaymentAmount(financeMain.getDownPayment());
					schedule.setDownpaymentOnSchDate(true);
				}
			}

			finScheduleData.setScheduleMap(schedule);
		}

		if (financeMain.isAllowGrcPeriod()) {

			// Load Grace period profit dates
			finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftFrq(), financeMain.getNextGrcPftDate(),
					financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_PFT);

			/*
			 * // Load Repayment dates during grace period if (financeMain.isAllowGrcRepay()) { finScheduleData =
			 * getSchedule(finScheduleData, financeMain.getGrcPftFrq(), financeMain.getNextGrcPftDate(),
			 * financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_RPY); }
			 */

			// Load Grace period profit review dates
			if (financeMain.isAllowGrcPftRvw()) {
				finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftRvwFrq(),
						financeMain.getNextGrcPftRvwDate(), financeMain.getGrcPeriodEndDate(), true,
						CalculationConstants.SCHDFLAG_RVW);
			}

			// Load Grace period capitalization dates
			if (financeMain.isAllowGrcCpz()) {
				finScheduleData = getSchedule(finScheduleData, financeMain.getGrcCpzFrq(),
						financeMain.getNextGrcCpzDate(), financeMain.getGrcPeriodEndDate(), true,
						CalculationConstants.SCHDFLAG_CPZ);
			}
		}

		// To Check schedule date is found with grace period end date
		FinanceScheduleDetail schedule = null;
		if (finScheduleData.getScheduleMap() != null
				&& finScheduleData.getScheduleMap().containsKey(financeMain.getGrcPeriodEndDate())) {
			schedule = finScheduleData.getScheduleMap().get(financeMain.getGrcPeriodEndDate());
		} else {

			schedule = new FinanceScheduleDetail();
			if (financeMain.getGrcPeriodEndDate() != null) {
				schedule.setSchDate(financeMain.getGrcPeriodEndDate());
				schedule.setDefSchdDate(financeMain.getGrcPeriodEndDate());
			}
		}

		//schedule.setPftOnSchDate(true);
		if (financeMain.isAllowGrcPeriod() && financeMain.isAllowGrcCpz()) {
			schedule.setCpzOnSchDate(true);
		}
		schedule.setActRate(financeMain.getRepayProfitRate());
		schedule.setCalculatedRate(financeMain.getRepayProfitRate());
		schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
		finScheduleData.setScheduleMap(schedule);

		// Load Repay profit dates
		if (StringUtils.isNotBlank(financeMain.getRepayPftFrq())) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayPftFrq(),
					financeMain.getNextRepayPftDate(), financeMain.getMaturityDate(), true,
					CalculationConstants.SCHDFLAG_PFT);
		}

		// Load Repay profit review dates
		if (financeMain.isAllowRepayRvw()) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayRvwFrq(),
					financeMain.getNextRepayRvwDate(), financeMain.getMaturityDate(), true,
					CalculationConstants.SCHDFLAG_RVW);
		}

		// Load Repay capitalize dates
		if (financeMain.isAllowRepayCpz()) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayCpzFrq(),
					financeMain.getNextRepayCpzDate(), financeMain.getMaturityDate(), true,
					CalculationConstants.SCHDFLAG_CPZ);
		}

		// Load Repayment dates
		finScheduleData = getSchedule(finScheduleData, financeMain.getRepayFrq(), financeMain.getNextRepayDate(),
				financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_RPY);

		finScheduleData.setFinanceMain(financeMain);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private static FinScheduleData maintainFrqSchdProcess(FinScheduleData finScheduleData, String frequency,
			Date grcStartDate, Date rpyStartDate, Date endDate) {
		logger.debug("Entering");
		
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		if (financeMain.isAllowGrcPeriod() && grcStartDate != null
				&& grcStartDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {

			// Load Grace period profit dates
			finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftFrq(), grcStartDate, financeMain.getGrcPeriodEndDate(),
					true, CalculationConstants.SCHDFLAG_PFT);

			// Load Repayment dates during grace period
			if (financeMain.isAllowGrcRepay()) {
				finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftFrq(), grcStartDate,
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_RPY);
			}

			// Load Grace period profit review dates
			if (financeMain.isAllowGrcPftRvw()) {
				finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftRvwFrq(), grcStartDate,
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_RVW);
			}

			// Load Grace period capitalization dates
			if (financeMain.isAllowGrcCpz()) {
				finScheduleData = getSchedule(finScheduleData, financeMain.getGrcCpzFrq(), grcStartDate,
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_CPZ);
			}

			// To Check schedule date is found with grace period end date
			FinanceScheduleDetail schedule = null;
			if (finScheduleData.getScheduleMap() != null
					&& finScheduleData.getScheduleMap().containsKey(
							DateUtility.getDate(DateUtility.formateDate(financeMain.getGrcPeriodEndDate(),
									PennantConstants.DBDateFormat), PennantConstants.DBDateFormat))) {
				schedule = finScheduleData.getScheduleMap().get(
						DateUtility.getDate(DateUtility.formateDate(financeMain.getGrcPeriodEndDate(),
								PennantConstants.DBDateFormat), PennantConstants.DBDateFormat));
			} else {

				schedule = new FinanceScheduleDetail();
				if (financeMain.getGrcPeriodEndDate() != null) {
					schedule.setSchDate(financeMain.getGrcPeriodEndDate());
					schedule.setDefSchdDate(financeMain.getGrcPeriodEndDate());
				}
			}

			//schedule.setPftOnSchDate(true);
			if (financeMain.isAllowGrcPeriod() && financeMain.isAllowGrcCpz()) {
				schedule.setCpzOnSchDate(true);
			}
			schedule.setActRate(financeMain.getRepayProfitRate());
			schedule.setCalculatedRate(financeMain.getRepayProfitRate());
			schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
			finScheduleData.setScheduleMap(schedule);
		}

		// Load Repay profit dates
		if (StringUtils.isNotBlank(financeMain.getRepayPftFrq())) {
			finScheduleData = getSchedule(finScheduleData, getFrequency(financeMain.getRepayPftFrq(), frequency), rpyStartDate, financeMain.getMaturityDate(),
					true, CalculationConstants.SCHDFLAG_PFT);
		}

		// Load Repay profit review dates
		if (financeMain.isAllowRepayRvw()) {
			finScheduleData = getSchedule(finScheduleData, getFrequency(financeMain.getRepayRvwFrq(), frequency), rpyStartDate, financeMain.getMaturityDate(),
					true, CalculationConstants.SCHDFLAG_RVW);
		}

		// Load Repay capitalize dates
		if (financeMain.isAllowRepayCpz()) {
			finScheduleData = getSchedule(finScheduleData, getFrequency(financeMain.getRepayCpzFrq(), frequency), rpyStartDate, financeMain.getMaturityDate(),
					true, CalculationConstants.SCHDFLAG_CPZ);
		}

		// Load Repayment dates
		finScheduleData = getSchedule(finScheduleData, frequency, rpyStartDate, financeMain.getMaturityDate(), true,
				CalculationConstants.SCHDFLAG_RPY);

		finScheduleData.setFinanceMain(financeMain);

		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for Preparation of Frequency based on existing
	 * @param frq1
	 * @param frq2
	 * @return 
	 */
	private static String getFrequency(String frq1, String frq2){
		
		char frqCode = frq1.charAt(0);
		String returnfrq = "";
		switch (frqCode) {
		case 'Y':
		case 'H':
		case 'Q':
		case 'B':
		case 'M':
			returnfrq = frq1.substring(0, 3).concat(frq2.substring(3));
			break;
		case 'F':
		case 'X':
		case 'W':
		case 'D':
		default:
			returnfrq = frq2;
			break;
		}
		
		return returnfrq;
	}

	private static FinScheduleData getSchedule(FinScheduleData finScheduleData, String frequency, Date startDate,
			Date endDate, boolean includeStartDate, int scheduleFlag) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FrequencyDetails frequencyDetails = FrequencyUtil.getTerms(frequency, startDate, endDate, true, true);

		if (frequencyDetails.getErrorDetails() != null) {
			logger.warn("Schedule Error: on condition --->  Validate frequency:" + frequency);
			finScheduleData.setErrorDetail(frequencyDetails.getErrorDetails());
		}

		if (frequencyDetails.getScheduleList() != null && frequencyDetails.getScheduleList().size() > 0) {

			for (int i = 0; i < frequencyDetails.getScheduleList().size(); i++) {
				Calendar calendar = frequencyDetails.getScheduleList().get(i);
				FinanceScheduleDetail schedule = null;

				if (finScheduleData.getScheduleMap()
						.containsKey(
								DateUtility.getDate(DateUtility.formatUtilDate(calendar.getTime(),
										PennantConstants.dateFormat)))) {

					schedule = finScheduleData.getScheduleMap().get(
							DateUtility.getDate(DateUtility.formatUtilDate(calendar.getTime(),
									PennantConstants.dateFormat)));
				} else {
					schedule = new FinanceScheduleDetail();
					schedule.setSchDate(calendar.getTime());
					schedule.setDefSchdDate(calendar.getTime());

					if (calendar.getTime().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
						schedule.setActRate(financeMain.getGrcPftRate());
						schedule.setCalculatedRate(financeMain.getGrcPftRate());

						if (financeMain.getGraceBaseRate() != null) {
							schedule.setBaseRate(financeMain.getGraceBaseRate());
						}

						if (financeMain.getGraceSpecialRate() != null) {
							schedule.setSplRate(financeMain.getGraceSpecialRate());
						}

						schedule.setMrgRate(financeMain.getGrcMargin());

						schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
					} else {

						schedule.setActRate(financeMain.getRepayProfitRate());
						schedule.setCalculatedRate(financeMain.getRepayProfitRate());

						if (financeMain.getRepayBaseRate() != null) {
							schedule.setBaseRate(financeMain.getRepayBaseRate());
						}

						if (financeMain.getRepaySpecialRate() != null) {
							schedule.setSplRate(financeMain.getRepaySpecialRate());
						}

						schedule.setMrgRate(financeMain.getRepayMargin());

						schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
					}

					if (DateUtility.compare(calendar.getTime(), financeMain.getMaturityDate()) == 0) {
						schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
					}
				}

				//SET various schedule flags

				//Profit On Schedule Date
				if (scheduleFlag == 0) {
					schedule.setPftOnSchDate(false);
					schedule.setRepayOnSchDate(false);
					schedule.setFrqDate(true);

					if (schedule.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
						//Pay at Grace end and schedule date is grace end
						if (schedule.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0
								&& financeMain.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_GRCENDPAY)) {
							schedule.setPftOnSchDate(true);
						} else if (financeMain.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_PFT)) {
							schedule.setPftOnSchDate(true);
						}

						schedule.setRepayOnSchDate(false);

					} else {
						if (financeMain.isFinRepayPftOnFrq()) {
							schedule.setPftOnSchDate(true);
						}
					}

					//Profit Review On Schedule Date
				} else if (scheduleFlag == 1) {
					schedule.setRvwOnSchDate(true);

					//Profit Capitalize On Schedule Date
				} else if (scheduleFlag == 2) {
					schedule.setCpzOnSchDate(true);

					//Repayment On Schedule Date
				} else if (scheduleFlag == 3) {
					schedule.setPftOnSchDate(true);
					schedule.setRepayOnSchDate(true);
					schedule.setFrqDate(true);
					financeMain.setNumberOfTerms(frequencyDetails.getScheduleList().size());
				}

				finScheduleData.setScheduleMap(schedule);

			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for preparing list of Schedule Frequency Insurance Details
	 */
	private static List<FinSchFrqInsurance> getInsuranceSchedule(FinScheduleData finScheduleData,FinInsurances finInsurance, Date startDate,
			Date endDate, boolean includeStartDate) {
		logger.debug("Entering");

		FrequencyDetails frequencyDetails = FrequencyUtil.getTerms(finInsurance.getInsuranceFrq(), startDate, endDate, true, includeStartDate);
		if (frequencyDetails.getErrorDetails() != null) {
			logger.warn("Schedule Error: on condition --->  Validate frequency:" + finInsurance.getInsuranceFrq());
			finScheduleData.setErrorDetail(frequencyDetails.getErrorDetails());
		}

		List<FinSchFrqInsurance> schFrqList = null;
		if (frequencyDetails.getScheduleList() != null && frequencyDetails.getScheduleList().size() > 0) {
			schFrqList = new ArrayList<>();
			for (int i = 0; i < frequencyDetails.getScheduleList().size(); i++) {
				Calendar calendar = frequencyDetails.getScheduleList().get(i);
				Date insDate= DateUtility.getDate(DateUtility.formatUtilDate(calendar.getTime(), PennantConstants.dateFormat));

				if(DateUtility.compare(finScheduleData.getFinanceMain().getFinStartDate(), insDate) == 0){
					continue;
				}
				FinSchFrqInsurance frqInsurance = new FinSchFrqInsurance();
				frqInsurance.setInsSchDate(insDate);
				frqInsurance.setInsuranceRate(finInsurance.getInsuranceRate());
				//If calculation type is Percentage then percentage is captured as insurance Rate
				if (StringUtils.equals(finInsurance.getCalType(), InsuranceConstants.CALTYPE_PERCENTAGE)) {
					frqInsurance.setInsuranceRate(finInsurance.getCalPerc());
				}
				frqInsurance.setReference(finInsurance.getReference());
				frqInsurance.setModule(FinanceConstants.MODULE_NAME);
				frqInsurance.setInsuranceType(finInsurance.getInsuranceType());
				frqInsurance.setInsReference(finInsurance.getInsReference());
				frqInsurance.setInsuranceRate(finInsurance.getInsuranceRate());
				frqInsurance.setInsuranceFrq(finInsurance.getInsuranceFrq());

				schFrqList.add(frqInsurance);
			}
		}
		return schFrqList;
	}

	/**
	 * =======================================================================================================
	 * Validating Finance Main Object with Frequencies & Disbursement Details
	 * 
	 * Order Of Validation Fields
	 * 
	 * 1. Finance Main Object (Null Check) 2. Repay Frequency 3. Repay Profit Frequency 4. Disbursement Details
	 * (Disbursement Amount) 5. Grace period End Date(Null & before First Disbursement Date) 6. Maturity Date (Null
	 * Check) 7. Disbursement Details (Disbursement Date after Maturity Date) 8. Next Repayment Date (Null Check &&
	 * NextRepayDate before Grace Period End Date) 9. Next Repayment Profit Date (Null Check && NextRepayPftDate before
	 * Grace Period End Date) 10. Maturity Date (Maturity Date before Next Repayment Date) 11. Allow Grace Period a)
	 * Validate Frequency (GrcPftFrq -- Grace Profit Frequency) b) Next Geace Profit Date (Null Check && NextGrcPftDate
	 * after Grace Period End Date) c) Allow Grace Profit Review i) Next Grace Profit Review Date (Null Check &&
	 * NextGrcPftRvwDate after Grace Period End Date) d) Allow Grace Capitalization i) Next Grace Capitalization Date
	 * (Null Check && NextGrcCpzDate after Grace Period End Date && NextGrcCpzDate before NextGrcPftDate) e) Allow Grace
	 * Profit Review i) Validate Frequency (GrcPftRvwFrq -- Grace Profit Review Frequency) f) Allow Grace Capitalization
	 * i) Validate Frequency (GrcCpzFrq -- Grace Capitalization Frequency) 12) Allow Repay Review a) Validate Frequency
	 * (RepayRvwFrq -- Repay Review Frequency) 13) Next Repay Review Date (Null Check && NextRepayRvwDate before Grace
	 * Period End Date) 14) Allow Repay Capitalization a) Validate Frequency (RepayCpzFrq -- Repay Capitalization
	 * Frequency) 15) Next Repay Capitalization Date (Null Check && NextRepayCpzDate before Grace Period End Date)
	 * 
	 * =======================================================================================================
	 * 
	 * @param financeMain
	 * @param financeDisbursements
	 * @return
	 */

	private static ErrorDetails validateFinanceMain(FinanceMain financeMain,
			List<FinanceDisbursement> financeDisbursements, boolean isOverdraft) {
		logger.debug("Entering");

		String[] errorParm2 = new String[2];

		ErrorDetails errorDetails = null;
		if (financeMain == null) {
			logger.warn("Schedule Error: on condition --->  financeMain == null");
			return getErrorDetail("Schedule", "", new String[] { " " }, new String[] { " " });
		}

		errorDetails = FrequencyUtil.validateFrequency(financeMain.getRepayFrq());
		if (errorDetails != null) {
			logger.warn("Schedule Error: on condition --->  Validate RepayFrq");
			return errorDetails;
		}

		errorDetails = FrequencyUtil.validateFrequency(financeMain.getRepayPftFrq());
		if (errorDetails != null) {
			logger.warn("Schedule Error: on condition --->  Validate RepayPftFrq");
			return errorDetails;
		}

		if(!isOverdraft){
			if (financeDisbursements.size() < 1) {
				errorParm2[0] = Integer.toString(financeDisbursements.size());
				errorParm2[1] = Integer.toString(1);
				return getErrorDetail("Schedule", "30516", errorParm2, errorParm2);
			}

			for (int i = 0; i < financeDisbursements.size(); i++) {
				if (financeDisbursements.get(i).getDisbDate() != null
						&& financeDisbursements.get(i).getDisbAmount() == null) {
					errorParm2[0] = DateUtility.formatToShortDate(financeDisbursements.get(i).getDisbDate());
					errorParm2[1] = PennantJavaUtil.getLabel("label_DisbursementAmount");
					return getErrorDetail("Schedule", "30517", errorParm2, errorParm2);
				}
			}

			//Reset Grace Period End Date
			if (financeMain.getGrcPeriodEndDate() == null) {
				financeMain.setGrcPeriodEndDate(financeDisbursements.get(0).getDisbDate());
			}

			if (financeMain.getGrcPeriodEndDate().before(financeDisbursements.get(0).getDisbDate())) {
				errorParm2[0] = DateUtility.formatToShortDate(financeMain.getGrcPeriodEndDate());
				errorParm2[1] = DateUtility.formatToShortDate(financeDisbursements.get(0).getDisbDate());
				return getErrorDetail("Schedule", "30518", errorParm2, errorParm2);
			}
		}

		if (financeMain.getMaturityDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_WIFinMaturityDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
		}

		if(!isOverdraft){
			if (financeDisbursements.get(financeDisbursements.size() - 1).getDisbDate()
					.after(financeMain.getMaturityDate())) {
				errorParm2[0] = DateUtility.formatToShortDate(financeDisbursements.get(financeDisbursements.size() - 1)
						.getDisbDate());
				errorParm2[1] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
				return getErrorDetail("Schedule", "30519", errorParm2, errorParm2);
			}
		}

		if (financeMain.getNextRepayDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_WIFinNextRepaymentDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
		}

		if (financeMain.getNextRepayDate().before(financeMain.getGrcPeriodEndDate())) {
			errorParm2[0] = DateUtility.formatToShortDate(financeMain.getNextRepayDate());
			errorParm2[1] = DateUtility.formatToShortDate(financeMain.getGrcPeriodEndDate());
			return getErrorDetail("Schedule", "30522", errorParm2, errorParm2);
		}

		if (financeMain.getNextRepayPftDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_NextRepayProfitDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
		}

		if (financeMain.getNextRepayPftDate().before(financeMain.getGrcPeriodEndDate())) {
			errorParm2[0] = DateUtility.formatToShortDate(financeMain.getNextRepayPftDate());
			errorParm2[1] = DateUtility.formatToShortDate(financeMain.getGrcPeriodEndDate());
			return getErrorDetail("Schedule", "30523", errorParm2, errorParm2);
		}

		if (financeMain.getMaturityDate().before(financeMain.getNextRepayDate())) {
			errorParm2[0] = DateUtility.formatToShortDate(financeMain.getMaturityDate());
			errorParm2[1] = DateUtility.formatToShortDate(financeMain.getNextRepayDate());
			return getErrorDetail("Schedule", "30527", errorParm2, errorParm2);
		}

		// if specified mandatory
		if (financeMain.isAllowGrcPeriod()) {
			errorDetails = FrequencyUtil.validateFrequency(financeMain.getGrcPftFrq());

			if (errorDetails != null) {
				logger.warn("Schedule Error: on condition --->  Validate GrcPftFrq");
				return errorDetails;
			}

			if (financeMain.getNextGrcPftDate() == null) {
				errorParm2[0] = PennantJavaUtil.getLabel("label_NextGraceProfitDate");
				errorParm2[1] = "";
				return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
			}

			if (financeMain.getNextGrcPftDate().after(financeMain.getGrcPeriodEndDate())) {
				errorParm2[0] = DateUtility.formatToShortDate(financeMain.getNextGrcPftDate());
				errorParm2[1] = DateUtility.formatToShortDate(financeMain.getGrcPeriodEndDate());
				return getErrorDetail("Schedule", "90161", errorParm2, errorParm2);
			}

			if (financeMain.isAllowGrcPftRvw()) {

				errorDetails = FrequencyUtil.validateFrequency(financeMain.getGrcPftRvwFrq());
				if (errorDetails != null) {
					logger.warn("Schedule Error: on condition --->  Validate GrcPftRvwFrq");
					return errorDetails;
				}

				if (financeMain.getNextGrcPftRvwDate() == null) {
					errorParm2[0] = PennantJavaUtil.getLabel("label_NextGraceReviewDate");
					errorParm2[1] = "";
					return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
				}

				if (financeMain.getNextGrcPftRvwDate().after(financeMain.getGrcPeriodEndDate())) {
					errorParm2[0] = DateUtility.formatToShortDate(financeMain.getNextGrcPftRvwDate());
					errorParm2[1] = DateUtility.formatToShortDate(financeMain.getGrcPeriodEndDate());
					return getErrorDetail("Schedule", "30520", errorParm2, errorParm2);
				}

			}

			if (financeMain.isAllowGrcCpz()) {

				errorDetails = FrequencyUtil.validateFrequency(financeMain.getGrcCpzFrq());
				if (errorDetails != null) {
					logger.warn("Schedule Error: on condition --->  Validate GrcCpzFrq");
					return errorDetails;
				}

				if (financeMain.getNextGrcCpzDate() == null) {
					errorParm2[0] = PennantJavaUtil.getLabel("label_NextGraceCapatalisationDate");
					errorParm2[1] = "";
					return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
				}

				if (financeMain.getNextGrcCpzDate().after(financeMain.getGrcPeriodEndDate())) {
					errorParm2[0] = DateUtility.formatToShortDate(financeMain.getNextGrcCpzDate());
					errorParm2[1] = DateUtility.formatToShortDate(financeMain.getGrcPeriodEndDate());
					return getErrorDetail("Schedule", "30512", errorParm2, errorParm2);
				}

				if (financeMain.getNextGrcCpzDate().before(financeMain.getNextGrcPftDate())) {
					errorParm2[0] = DateUtility.formatToShortDate(financeMain.getNextGrcCpzDate());
					errorParm2[1] = DateUtility.formatToShortDate(financeMain.getNextGrcPftDate());
					//return getErrorDetail("Schedule", "30526", errorParm2, errorParm2);
				}

			}

		}

		if (financeMain.isAllowRepayRvw()) {
			errorDetails = FrequencyUtil.validateFrequency(financeMain.getRepayRvwFrq());
			if (errorDetails != null) {
				logger.warn("Schedule Error: on condition --->  Validate RepayRvwFrq");
				return errorDetails;
			}

			if (financeMain.getNextRepayRvwDate() == null) {
				errorParm2[0] = PennantJavaUtil.getLabel("label_NextRepaymentReviewDate");
				errorParm2[1] = "";
				return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
			}

			if (financeMain.getNextRepayRvwDate().before(financeMain.getGrcPeriodEndDate())) {
				errorParm2[0] = DateUtility.formatToShortDate(financeMain.getNextRepayRvwDate());
				errorParm2[1] = DateUtility.formatToShortDate(financeMain.getGrcPeriodEndDate());
				return getErrorDetail("Schedule", "30524", errorParm2, errorParm2);
			}
		}

		if (financeMain.isAllowRepayCpz()) {
			errorDetails = FrequencyUtil.validateFrequency(financeMain.getRepayCpzFrq());
			if (errorDetails != null) {
				logger.warn("Schedule Error: on condition --->  Validate RepayCpzFrq");
				return errorDetails;
			}

			if (financeMain.getNextRepayCpzDate() == null) {
				errorParm2[0] = PennantJavaUtil.getLabel("label_NextRepaymentCapatalisationDate");
				errorParm2[1] = "";
				return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
			}

			if (financeMain.getNextRepayCpzDate().before(financeMain.getNextRepayPftDate())) {
				errorParm2[0] = DateUtility.formatToShortDate(financeMain.getNextRepayCpzDate());
				errorParm2[1] = DateUtility.formatToShortDate(financeMain.getNextRepayPftDate());
				return getErrorDetail("Schedule", "30525", errorParm2, errorParm2);
			}

		}

		logger.debug("Leaving");
		return errorDetails;
	}

	private static ErrorDetails getErrorDetail(String errorField, String errorCode, String[] errParm, String[] valueParm) {
		ErrorDetails errorDetail = ErrorUtil.getErrorDetail(
				new ErrorDetails(errorField, errorCode, errParm, valueParm), SessionUserDetails.getUserLanguage());

		logger.warn("Schedule Error: on condition --->  " + errorDetail.getErrorCode() + "-" + errorDetail.getError());
		return errorDetail;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

}
