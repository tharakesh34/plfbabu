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
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class ScheduleGenerator {
	
	private final static Logger logger = Logger.getLogger(ScheduleGenerator.class);
	
	private FinScheduleData finScheduleData;

	public static FinScheduleData getNewSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		finScheduleData.setErrorDetail(validateFinanceMain(financeMain, finScheduleData.getDisbursementDetails()));

		if (finScheduleData.getErrorDetails() == null || finScheduleData.getErrorDetails().size() == 0) {
			finScheduleData = newSchdProcess(finScheduleData);
			List<Date> schdDateKeyList = new ArrayList<Date>(finScheduleData.getScheduleMap().keySet());
			finScheduleData.getFinanceScheduleDetails().clear();
			
			Collections.sort(schdDateKeyList);
			for (int j = 0; j < schdDateKeyList.size(); j++) {
				finScheduleData.getFinanceScheduleDetails().add(finScheduleData.getScheduleMap().get(schdDateKeyList.get(j)));
			}
			
			//Applying Rate Margin from Step Policy Details
			if(financeMain.isStepFinance()){
				int repayStart = 0;
				int repayEnd = 0;
				int stepCount = -1;
				FinanceStepPolicyDetail policyDetail = null;
				
				for (int i = financeMain.getGraceTerms(); i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
					
					FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
					if(repayStart == repayEnd){
						stepCount = stepCount + 1;
						if(finScheduleData.getStepPolicyDetails() != null && finScheduleData.getStepPolicyDetails().size() > stepCount){
							policyDetail = finScheduleData.getStepPolicyDetails().get(stepCount);
							repayEnd = repayEnd + policyDetail.getInstallments();
						}
					}

					if(policyDetail != null){
						curSchd.setCalculatedRate(curSchd.getCalculatedRate().add(policyDetail.getRateMargin()));
						repayStart++;
					}
				}
			}
			
			finScheduleData.getScheduleMap().clear();
		}
		
		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for Generating New Schedule Dates List with particulars
	 * @param scheduleData
	 * @param frequency
	 * @param startDate
	 * @param endDate
	 * @param includeStartDate
	 * @param scheduleFlag
	 * @return
	 */
	public static FinScheduleData getScheduleDateList(FinScheduleData scheduleData, String frequency, Date grcStartDate, Date rpyStartDate, Date endDate,
			boolean includeStartDate) {
		return new ScheduleGenerator(scheduleData, frequency, grcStartDate, rpyStartDate, endDate, includeStartDate).getFinScheduleData();
	}
	
	public ScheduleGenerator(FinScheduleData scheduleData, String frequency, Date grcStartDate, Date rpyStartDate, Date endDate, boolean includeStartDate) {
		scheduleData = maintainFrqSchdProcess(scheduleData, frequency , grcStartDate, rpyStartDate, endDate, includeStartDate);
		List<Date> schdDateKeyList = new ArrayList<Date>(scheduleData.getScheduleMap().keySet());
		scheduleData.getFinanceScheduleDetails().clear();

		Collections.sort(schdDateKeyList);
		int grcTerms = 0;
		int repayTerms = 0;
		for (int j = 0; j < schdDateKeyList.size(); j++) {
			scheduleData.getFinanceScheduleDetails().add(scheduleData.getScheduleMap().get(schdDateKeyList.get(j)));
			if(schdDateKeyList.get(j).compareTo(scheduleData.getFinanceMain().getGrcPeriodEndDate()) <= 0){
				grcTerms = grcTerms+1;
			}else{
				repayTerms = repayTerms +1;
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
	 *  1. Schdule Date
	 *  2. Schedule Rates (Actual Rate, Calculated Rate)
	 *  3. Schedule Specifier
	 *  4. Schedule Flag Details
	 *  5. Number of Terms
	 *  6. Set Schedule Details List into ScheduleMap
	 *  
	 *  =====================================================================================
	 *  
	 *  1.	Loop Finance Disbursement Details(Create Schedule Details List based upon Disbursement Details)
	 *  2.	Allow Grace Period
	 *  		a)	Schedule Detail List Creation based upon Grace Profit Frequency with 'SCHDFLAG_PFT'
	 *  		b)	Allow Grace Repay
	 *  				i)	Schedule Detail List Creation based upon Grace Profit Frequency with 'SCHDFLAG_RPY'
	 *  		c)	Allow Grace Profit Review
	 *  				i)	Schedule Detail List Creation based upon Grace Profit Review Frequency with 'SCHDFLAG_RVW'
	 *  		d)	Allow Grace Capitalization
	 *  				i)	Schedule Detail List Creation based upon Grace Capitalization Frequency with 'SCHDFLAG_CPZ'
	 *  3. 	Create New Schedule / Use Existing Schedule for Grace Period End Date
	 *  4. 	Schedule Detail List Creation based upon Repayment Frequency with 'SCHDFLAG_RPY'
	 *  5.	Schedule Detail List Creation based upon Repayment Profit Frequency with 'SCHDFLAG_PFT' if RepayPftFrq exists.
	 *  6.	Allow Repayment Review
	 *  		a)	Schedule Detail List Creation based upon Repayment Review Frequency with 'SCHDFLAG_RVW'
	 *  7.	Allow Repayment Capitalization
	 *  		a)	Schedule Detail List Creation based upon Repayment Capitalization Frequency with 'SCHDFLAG_CPZ'
	 *  
	 *  =======================================================================================
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
			if(finScheduleData.getScheduleMap() != null && 
					finScheduleData.getScheduleMap().containsKey(disbursementDetail.getDisbDate())){
				prvTermDisbAmount = finScheduleData.getScheduleMap().get(disbursementDetail.getDisbDate()).getDisbAmount();
			}

			schedule.setSchDate(disbursementDetail.getDisbDate());
			schedule.setDisbAmount(disbursementDetail.getDisbAmount().add(prvTermDisbAmount));
			schedule.setFeeChargeAmt(disbursementDetail.getFeeChargeAmt());
			schedule.setDisbOnSchDate(true);
			
			if (disbursementDetail.getDisbDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
				schedule.setActRate(financeMain.getGrcPftRate());
				schedule.setCalculatedRate(financeMain.getGrcPftRate());
				schedule.setSpecifier(CalculationConstants.GRACE);
			} else {
				schedule.setActRate(financeMain.getRepayProfitRate());
				schedule.setCalculatedRate(financeMain.getRepayProfitRate());
				schedule.setSpecifier(CalculationConstants.REPAY);
			}

			if (i == 0) {
				if (financeMain.getDownPayment() != null
						&& !financeMain.getDownPayment().equals(BigDecimal.ZERO)) {
					schedule.setDownPaymentAmount(financeMain.getDownPayment());
					schedule.setDownpaymentOnSchDate(true);
				}
			}

			finScheduleData.setScheduleMap(schedule);
		}

		if (financeMain.isAllowGrcPeriod()) {
			
			// Load Grace period profit dates
			finScheduleData = getSchedule(finScheduleData,
					financeMain.getGrcPftFrq(), financeMain.getNextGrcPftDate(),
					financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_PFT);
			
			// Load Repayment dates during grace period
			if (financeMain.isAllowGrcRepay()) {
				finScheduleData = getSchedule(finScheduleData,
						financeMain.getGrcPftFrq(), financeMain.getNextGrcPftDate(),
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_RPY);
			}

			// Load Grace period profit review dates
			if (financeMain.isAllowGrcPftRvw()) {
				finScheduleData = getSchedule(finScheduleData,
						financeMain.getGrcPftRvwFrq(), financeMain.getNextGrcPftRvwDate(),
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_RVW);
			}

			// Load Grace period capitalization dates
			if (financeMain.isAllowGrcCpz()) {
				finScheduleData = getSchedule(finScheduleData,
						financeMain.getGrcCpzFrq(), financeMain.getNextGrcCpzDate(),
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_CPZ);
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
			}
		}

		schedule.setPftOnSchDate(true);
		if (financeMain.isAllowGrcPeriod() && financeMain.isAllowGrcCpz()) {
			schedule.setCpzOnSchDate(true);
		}
		schedule.setActRate(financeMain.getRepayProfitRate());
		schedule.setCalculatedRate(financeMain.getRepayProfitRate());
		schedule.setSpecifier(CalculationConstants.GRACE_END);
		finScheduleData.setScheduleMap(schedule);

		// Load Repayment dates
		finScheduleData = getSchedule(finScheduleData,
				financeMain.getRepayFrq(), financeMain.getNextRepayDate(),
				financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_RPY);

		// Load Repay profit dates
		if (!StringUtils.trim(financeMain.getRepayPftFrq()).equals("")) {
			finScheduleData = getSchedule(finScheduleData,
					financeMain.getRepayPftFrq(), financeMain.getNextRepayPftDate(),
					financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_PFT);
		}

		// Load Repay profit review dates
		if (financeMain.isAllowRepayRvw()) {
			finScheduleData = getSchedule(finScheduleData,
					financeMain.getRepayRvwFrq(), financeMain.getNextRepayRvwDate(),
					financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_RVW);
		}

		// Load Repay capitalize dates
		if (financeMain.isAllowRepayCpz()) {
			finScheduleData = getSchedule(finScheduleData,
					financeMain.getRepayCpzFrq(), financeMain.getNextRepayCpzDate(),
					financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_CPZ);
		}
		
		finScheduleData.setFinanceMain(financeMain);
		
		logger.debug("Leaving");
		return finScheduleData;
	}
	
	private static FinScheduleData maintainFrqSchdProcess(FinScheduleData finScheduleData,String frequency, Date grcStartDate ,
			Date rpyStartDate, Date endDate, boolean includeStartDate) {
		logger.debug("Entering");
		
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		if (financeMain.isAllowGrcPeriod() && grcStartDate != null && 
				grcStartDate.compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
			
			// Load Grace period profit dates
			finScheduleData = getSchedule(finScheduleData,frequency, grcStartDate,
					financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_PFT);
			
			// Load Repayment dates during grace period
			if (financeMain.isAllowGrcRepay()) {
				finScheduleData = getSchedule(finScheduleData,frequency,grcStartDate,
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_RPY);
			}

			// Load Grace period profit review dates
			if (financeMain.isAllowGrcPftRvw()) {
				finScheduleData = getSchedule(finScheduleData,frequency, grcStartDate,
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_RVW);
			}

			// Load Grace period capitalization dates
			if (financeMain.isAllowGrcCpz()) {
				finScheduleData = getSchedule(finScheduleData,frequency, grcStartDate,
						financeMain.getGrcPeriodEndDate(), true, CalculationConstants.SCHDFLAG_CPZ);
			}
		}

		// To Check schedule date is found with grace period end date
		FinanceScheduleDetail schedule = null;
		if (finScheduleData.getScheduleMap() != null
				&& finScheduleData.getScheduleMap().containsKey(DateUtility.getDate(DateUtility.formateDate(
						financeMain.getGrcPeriodEndDate(), PennantConstants.DBDateFormat), PennantConstants.DBDateFormat))) {
			schedule = finScheduleData.getScheduleMap().get(DateUtility.getDate(DateUtility.formateDate(
					financeMain.getGrcPeriodEndDate(), PennantConstants.DBDateFormat), PennantConstants.DBDateFormat));
		} else {

			schedule = new FinanceScheduleDetail();
			if (financeMain.getGrcPeriodEndDate() != null) {
				schedule.setSchDate(financeMain.getGrcPeriodEndDate());
			}
		}

		schedule.setPftOnSchDate(true);
		if (financeMain.isAllowGrcPeriod() && financeMain.isAllowGrcCpz()) {
			schedule.setCpzOnSchDate(true);
		}
		schedule.setActRate(financeMain.getRepayProfitRate());
		schedule.setCalculatedRate(financeMain.getRepayProfitRate());
		schedule.setSpecifier(CalculationConstants.GRACE_END);
		finScheduleData.setScheduleMap(schedule);

		// Load Repayment dates
		finScheduleData = getSchedule(finScheduleData,frequency,rpyStartDate,
				financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_RPY);

		// Load Repay profit dates
		if (!StringUtils.trim(financeMain.getRepayPftFrq()).equals("")) {
			finScheduleData = getSchedule(finScheduleData,frequency, rpyStartDate,
					financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_PFT);
		}

		// Load Repay profit review dates
		if (financeMain.isAllowRepayRvw()) {
			finScheduleData = getSchedule(finScheduleData,frequency, rpyStartDate,
					financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_RVW);
		}

		// Load Repay capitalize dates
		if (financeMain.isAllowRepayCpz()) {
			finScheduleData = getSchedule(finScheduleData,frequency, rpyStartDate,
					financeMain.getMaturityDate(), true, CalculationConstants.SCHDFLAG_CPZ);
		}

		finScheduleData.setFinanceMain(financeMain);
		
		logger.debug("Leaving");
		return finScheduleData;
	}

	private static FinScheduleData getSchedule(FinScheduleData finScheduleData,
			String frequency, Date startDate, Date endDate,
			boolean includeStartDate, int scheduleFlag) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FrequencyDetails frequencyDetails = FrequencyUtil.getTerms(frequency,
				startDate, endDate, true, true);

		if (frequencyDetails.getErrorDetails() != null) {
			logger.error("Schedule Error: on condition --->  Validate frequency:"+frequency);
			finScheduleData.setErrorDetail(frequencyDetails.getErrorDetails());
		}

		if (frequencyDetails.getScheduleList() != null
				&& frequencyDetails.getScheduleList().size() > 0) {

			for (int i = 0; i < frequencyDetails.getScheduleList().size(); i++) {
				Calendar calendar = frequencyDetails.getScheduleList().get(i);
				FinanceScheduleDetail schedule = null;

				if (finScheduleData.getScheduleMap().containsKey(DateUtility.getDate(
						DateUtility.formatUtilDate(calendar.getTime(),PennantConstants.dateFormat)))) {
					
					schedule = finScheduleData.getScheduleMap().get(DateUtility.getDate(
							DateUtility.formatUtilDate(calendar.getTime(), PennantConstants.dateFormat)));
				} else {
					schedule = new FinanceScheduleDetail();
					schedule.setSchDate(calendar.getTime());
				}

				if (calendar.getTime().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {

					schedule.setActRate(financeMain.getGrcPftRate());
					schedule.setCalculatedRate(financeMain.getGrcPftRate());
					schedule.setSpecifier(CalculationConstants.GRACE);
				} else {

					schedule.setActRate(financeMain.getRepayProfitRate());
					schedule.setCalculatedRate(financeMain.getRepayProfitRate());
					schedule.setSpecifier(CalculationConstants.REPAY);
				}

				if (DateUtility.compare(calendar.getTime(), financeMain.getMaturityDate()) == 0) {
					schedule.setSpecifier(CalculationConstants.MATURITY);
				}

				switch (scheduleFlag) {
					case 0:
						schedule.setPftOnSchDate(true);
						break;

					case 1:
						schedule.setRvwOnSchDate(true);
						break;
					case 2:
						schedule.setCpzOnSchDate(true);
						break;
					case 3:
						schedule.setRepayOnSchDate(true);
						break;
					default:
						break;
				}
				if (scheduleFlag == 3) {
					financeMain.setNumberOfTerms(frequencyDetails.getScheduleList().size());
				}
				finScheduleData.setScheduleMap(schedule);

			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * =======================================================================================================
	 * Validating Finance Main Object with Frequencies & Disbursement Details
	 * 
	 * Order Of Validation Fields
	 * 
	 * 	1. 	Finance Main Object (Null Check)
	 * 	2. 	Repay Frequency
	 * 	3. 	Repay Profit Frequency
	 * 	4. 	Disbursement Details (Disbursement Amount)
	 * 	5. 	Grace period End Date(Null & before First Disbursement Date)
	 * 	6. 	Maturity Date (Null Check)
	 * 	7. 	Disbursement Details (Disbursement Date after Maturity Date)
	 * 	8. 	Next Repayment Date (Null Check && NextRepayDate before Grace Period End Date)
	 * 	9. 	Next Repayment Profit Date (Null Check && NextRepayPftDate before Grace Period End Date)
	 *	10.	Maturity Date (Maturity Date before Next Repayment Date)
	 * 	11.	Allow Grace Period
	 * 			a)	Validate Frequency (GrcPftFrq -- Grace Profit Frequency)
	 * 			b)	Next Geace Profit Date (Null Check && NextGrcPftDate after Grace Period End Date)
	 * 			c) 	Allow Grace Profit Review
	 * 				i)	Next Grace Profit Review Date (Null Check && NextGrcPftRvwDate after Grace Period End Date)
	 * 			d) 	Allow Grace Capitalization
	 * 				i)	Next Grace Capitalization Date (Null Check && NextGrcCpzDate after Grace Period End Date
	 * 														&& NextGrcCpzDate before NextGrcPftDate)
	 * 			e)	Allow Grace Profit Review
	 * 				i) 	Validate Frequency (GrcPftRvwFrq -- Grace Profit Review Frequency)
	 * 			f)	Allow Grace Capitalization
	 * 				i) 	Validate Frequency (GrcCpzFrq -- Grace Capitalization Frequency)
	 * 	12)	Allow Repay Review
	 * 			a) 	Validate Frequency (RepayRvwFrq -- Repay Review Frequency)
	 * 	13)	Next Repay Review Date (Null Check && NextRepayRvwDate before Grace Period End Date)
	 * 	14)	Allow Repay Capitalization
	 * 			a) 	Validate Frequency (RepayCpzFrq -- Repay Capitalization Frequency)
	 * 	15)	Next Repay Capitalization Date (Null Check && NextRepayCpzDate before Grace Period End Date)
	 * 
	 * =======================================================================================================
	 *  
	 * @param financeMain
	 * @param financeDisbursements
	 * @return
	 */
	
	private static ErrorDetails validateFinanceMain(
			FinanceMain financeMain, List<FinanceDisbursement> financeDisbursements) {
		logger.debug("Entering");

		String[] errorParm2 = new String[2];

		ErrorDetails errorDetails = null;
		if (financeMain == null) {
			logger.error("Schedule Error: on condition --->  financeMain == null");
			return getErrorDetail("Schedule", "", new String[] { " " }, new String[] { " " });
		}

		errorDetails = FrequencyUtil.validateFrequency(financeMain.getRepayFrq());
		if (errorDetails != null) {
			logger.error("Schedule Error: on condition --->  Validate RepayFrq");
			return errorDetails;
		}

		errorDetails = FrequencyUtil.validateFrequency(financeMain.getRepayPftFrq());
		if (errorDetails != null) {
			logger.error("Schedule Error: on condition --->  Validate RepayPftFrq");
			return errorDetails;
		}

		if (financeDisbursements.size() < 1) {
			errorParm2[0] = Integer.toString(financeDisbursements.size());
			errorParm2[1] = Integer.toString(1);
			return getErrorDetail("Schedule", "E0016", errorParm2, errorParm2);
		}

		for (int i = 0; i < financeDisbursements.size(); i++) {
			if (financeDisbursements.get(i).getDisbDate() != null && 
					financeDisbursements.get(i).getDisbAmount() == null) {
				errorParm2[0] = DateUtility.formatUtilDate(financeDisbursements.get(i).getDisbDate(),
						PennantConstants.dateFormat);
				errorParm2[1] = PennantJavaUtil.getLabel("label_DisbursementAmount");
				return getErrorDetail("Schedule", "E0017", errorParm2, errorParm2);
			}
		}

		//Reset Grace Period End Date
		if (financeMain.getGrcPeriodEndDate() == null) {
			financeMain.setGrcPeriodEndDate(financeDisbursements.get(0).getDisbDate());
		}

		if (financeMain.getGrcPeriodEndDate().before(financeDisbursements.get(0).getDisbDate())) {
			errorParm2[0] = DateUtility.formatUtilDate(
					financeMain.getGrcPeriodEndDate(), PennantConstants.dateFormat);
			errorParm2[1] = DateUtility.formatUtilDate(financeDisbursements.get(0).getDisbDate(),
					PennantConstants.dateFormat);
			return getErrorDetail("Schedule", "E0018", errorParm2, errorParm2);
		}

		if (financeMain.getMaturityDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_WIFinMaturityDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "D0002", errorParm2, new String[] { "" });
		}

		if (financeDisbursements.get(financeDisbursements.size() - 1)
				.getDisbDate().after(financeMain.getMaturityDate())) {
			errorParm2[0] = DateUtility.formatUtilDate(financeDisbursements.get(
					financeDisbursements.size() - 1).getDisbDate(), PennantConstants.dateFormat);
			errorParm2[1] = DateUtility.formatUtilDate(financeMain.getMaturityDate(),
					PennantConstants.dateFormat);
			return getErrorDetail("Schedule", "E0019", errorParm2, errorParm2);
		}

		if (financeMain.getNextRepayDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_WIFinNextRepaymentDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "D0002", errorParm2, new String[] { "" });
		}

		if (financeMain.getNextRepayDate().before(financeMain.getGrcPeriodEndDate())) {
			errorParm2[0] = DateUtility.formatUtilDate(financeMain.getNextRepayDate(),
					PennantConstants.dateFormat);
			errorParm2[1] = DateUtility.formatUtilDate(financeMain.getGrcPeriodEndDate(),
					PennantConstants.dateFormat);
			return getErrorDetail("Schedule", "E0023", errorParm2, errorParm2);
		}

		if (financeMain.getNextRepayPftDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_NextRepayProfitDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "D0002", errorParm2, new String[] { "" });
		}

		if (financeMain.getNextRepayPftDate().before(financeMain.getGrcPeriodEndDate())) {
			errorParm2[0] = DateUtility.formatUtilDate(financeMain.getNextRepayPftDate(),
					PennantConstants.dateFormat);
			errorParm2[1] = DateUtility.formatUtilDate(financeMain.getGrcPeriodEndDate(),
					PennantConstants.dateFormat);
			return getErrorDetail("Schedule", "E0024", errorParm2, errorParm2);
		}

		if (financeMain.getMaturityDate().before(financeMain.getNextRepayDate())) {
			errorParm2[0] = DateUtility.formatUtilDate(financeMain.getMaturityDate(),
					PennantConstants.dateFormat);
			errorParm2[1] = DateUtility.formatUtilDate(financeMain.getNextRepayDate(),
					PennantConstants.dateFormat);
			return getErrorDetail("Schedule", "E0028", errorParm2, errorParm2);
		}

		// if specified mandatory
		if (financeMain.isAllowGrcPeriod()) {
			errorDetails = FrequencyUtil.validateFrequency(financeMain.getGrcPftFrq());

			if (errorDetails != null) {
				logger.error("Schedule Error: on condition --->  Validate GrcPftFrq");
				return errorDetails;
			}

			if (financeMain.getNextGrcPftDate() == null) {
				errorParm2[0] = PennantJavaUtil.getLabel("label_NextGraceProfitDate");
				errorParm2[1] = "";
				return getErrorDetail("Schedule", "D0002", errorParm2, new String[] { "" });
			}

			if (financeMain.getNextGrcPftDate().after(financeMain.getGrcPeriodEndDate())) {
				errorParm2[0] = DateUtility.formatUtilDate(financeMain.getNextGrcPftDate(),
						PennantConstants.dateFormat);
				errorParm2[1] = DateUtility.formatUtilDate(financeMain.getGrcPeriodEndDate(),
						PennantConstants.dateFormat);
				return getErrorDetail("Schedule", "E0020", errorParm2, errorParm2);
			}

			if (financeMain.isAllowGrcPftRvw()) {

				if (financeMain.getNextGrcPftRvwDate() == null) {
					errorParm2[0] = PennantJavaUtil.getLabel("label_NextGraceReviewDate");
					errorParm2[1] = "";
					return getErrorDetail("Schedule", "D0002", errorParm2,new String[] { "" });
				}

				if (financeMain.getNextGrcPftRvwDate().after(financeMain.getGrcPeriodEndDate())) {
					errorParm2[0] = DateUtility.formatUtilDate(financeMain.getNextGrcPftRvwDate(),
							PennantConstants.dateFormat);
					errorParm2[1] = DateUtility.formatUtilDate(financeMain.getGrcPeriodEndDate(),
							PennantConstants.dateFormat);
					return getErrorDetail("Schedule", "E0021", errorParm2, errorParm2);
				}

			}

			if (financeMain.isAllowGrcCpz()) {

				if (financeMain.getNextGrcCpzDate() == null) {
					errorParm2[0] = PennantJavaUtil.getLabel("label_NextGraceCapatalisationDate");
					errorParm2[1] = "";
					return getErrorDetail("Schedule", "D0002", errorParm2, new String[] { "" });
				}

				if (financeMain.getNextGrcCpzDate().after(financeMain.getGrcPeriodEndDate())) {
					errorParm2[0] = DateUtility.formatUtilDate(financeMain.getNextGrcCpzDate(),
							PennantConstants.dateFormat);
					errorParm2[1] = DateUtility.formatUtilDate(financeMain.getGrcPeriodEndDate(),
							PennantConstants.dateFormat);
					return getErrorDetail("Schedule", "E0012", errorParm2, errorParm2);
				}

				if (financeMain.getNextGrcCpzDate().before(financeMain.getNextGrcPftDate())) {
					errorParm2[0] = DateUtility.formatUtilDate(financeMain.getNextGrcCpzDate(),
							PennantConstants.dateFormat);
					errorParm2[1] = DateUtility.formatUtilDate(financeMain.getNextGrcPftDate(),
							PennantConstants.dateFormat);
					return getErrorDetail("Schedule", "E0027", errorParm2, errorParm2);
				}

			}
			
			if (financeMain.isAllowGrcPftRvw()) {
				errorDetails = FrequencyUtil.validateFrequency(financeMain.getGrcPftRvwFrq());
				if (errorDetails != null) {
					logger.error("Schedule Error: on condition --->  Validate GrcPftRvwFrq");
					return errorDetails;
				}
			}

			if (financeMain.isAllowGrcCpz()) {
				errorDetails = FrequencyUtil.validateFrequency(financeMain.getGrcCpzFrq());
				if (errorDetails != null) {
					logger.error("Schedule Error: on condition --->  Validate GrcCpzFrq");
					return errorDetails;
				}
			}

		}

		if (financeMain.isAllowRepayRvw()) {
			errorDetails = FrequencyUtil.validateFrequency(financeMain.getRepayRvwFrq());
			if (errorDetails != null) {
				logger.error("Schedule Error: on condition --->  Validate RepayRvwFrq");
				return errorDetails;
			}

			if (financeMain.getNextRepayRvwDate() == null) {
				errorParm2[0] = PennantJavaUtil.getLabel("label_NextRepaymentReviewDate");
				errorParm2[1] = "";
				return getErrorDetail("Schedule", "D0002", errorParm2, new String[] { "" });
			}

			if (financeMain.getNextRepayRvwDate().before(financeMain.getGrcPeriodEndDate())) {
				errorParm2[0] = DateUtility.formatUtilDate(financeMain.getNextRepayRvwDate(),
						PennantConstants.dateFormat);
				errorParm2[1] = DateUtility.formatUtilDate(financeMain.getGrcPeriodEndDate(),
						PennantConstants.dateFormat);
				return getErrorDetail("Schedule", "E0025", errorParm2, errorParm2);
			}
		}

		if (financeMain.isAllowRepayCpz()) {
			errorDetails = FrequencyUtil.validateFrequency(financeMain.getRepayCpzFrq());
			if (errorDetails != null) {
				logger.error("Schedule Error: on condition --->  Validate RepayCpzFrq");
				return errorDetails;
			}

			if (financeMain.getNextRepayCpzDate() == null) {
				errorParm2[0] = PennantJavaUtil.getLabel("label_NextRepaymentCapatalisationDate");
				errorParm2[1] = "";
				return getErrorDetail("Schedule", "D0002", errorParm2, new String[] { "" });
			}

			if (financeMain.getNextRepayCpzDate().before(financeMain.getNextRepayPftDate())) {
				errorParm2[0] = DateUtility.formatUtilDate(financeMain.getNextRepayCpzDate(),
						PennantConstants.dateFormat);
				errorParm2[1] = DateUtility.formatUtilDate(financeMain.getNextRepayPftDate(),
						PennantConstants.dateFormat);
				return getErrorDetail("Schedule", "E0026", errorParm2, errorParm2);
			}

		}

		logger.debug("Leaving");
		return errorDetails;
	}

	private static ErrorDetails getErrorDetail(String errorField,
			String errorCode, String[] errParm, String[] valueParm) {
		ErrorDetails errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails(errorField, errorCode,
				errParm, valueParm), SessionUserDetails.getUserLanguage());
		
		logger.error("Schedule Error: on condition --->  "+errorDetail.getErrorCode()+"-"+errorDetail.getError());
		return errorDetail;
	}

	public FinScheduleData getFinScheduleData() {
	    return finScheduleData;
    }
	public void setFinScheduleData(FinScheduleData finScheduleData) {
	    this.finScheduleData = finScheduleData;
    }

}
