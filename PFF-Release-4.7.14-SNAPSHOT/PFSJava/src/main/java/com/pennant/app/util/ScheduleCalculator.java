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
 * 30-04-2018 Vinay   0.2 	As Discussed with Raju and Siva, IRR Code calculation functionality 
 * 							implemented  
 ******************************************************************************************** 
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.model.FrequencyDetails;
import com.pennant.app.model.RateDetail;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.IRRFeeTypeDAO;
import com.pennant.backend.dao.applicationmaster.IRRFinanceTypeDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.rits.cloning.Cloner;

public class ScheduleCalculator {
	private static final Logger logger = Logger.getLogger(ScheduleCalculator.class);

	private FinScheduleData finScheduleData;
	private static BaseRateDAO baseRateDAO;
	private static SplRateDAO splRateDAO;
	// ####_ 0.2
	private static IRRFeeTypeDAO iRRFeeTypeDAO;
	private static IRRFinanceTypeDAO irrFinanceTypeDAO;
	
	// PROCESS METHODS IN SCHEDULE CALCULATOR
	public static final String PROC_GETCALSCHD = "procGetCalSchd";
	public static final String PROC_CHANGEGRACEEND = "procChangeGraceEnd";
	public static final String PROC_PLANDEFERPFT = "procPlanDeferPft";
	public static final String PROC_DOWNPAYSCHD = "procDownPaySchd";
	public static final String PROC_CHANGERATE = "procChangeRate";
	public static final String PROC_CHANGEREPAY = "procChangeRepay";
	public static final String PROC_POSTPONE = "procPostpone";
	public static final String PROC_UNPLANEMIH = "procUnPlannedEMIH";
	public static final String PROC_REAGEH = "procReAgeH";
	public static final String PROC_CHANGEPROFIT = "procChangeProfit";
	public static final String PROC_ADDDISBURSEMENT = "procAddDisbursement";
	public static final String PROC_RECALSCHD = "procReCalSchd";
	public static final String PROC_ADDTERM = "procAddTerm";
	public static final String PROC_DELETETERM = "procDeleteTerm";
	public static final String PROC_REFRESHRATES = "procRefreshRates";
	public static final String PROC_SUBSCHEDULE = "procSubSchedule";
	public static final String PROC_CALEFFECTIVERATE = "calEffectiveRate";
	public static final String PROC_ADVPFTRATESCHEDULE = "advPftRateSchedule";
	public static final String PROC_SUPLRENTINCRCOST = "calSuplRentIncrCost";
	public static final String PROC_BUILDOVERDRAFTSCHD = "buildOverdraftSchd";
	public static final String PROC_GETFRQEMIH = "procGetFrqEMIHoliday";
	public static final String PROC_GETADHOCEMIH = "procGetAdhocEMIHoliday";
	public static final String PROC_INSURANCESCHEDULE = "insuranceSchedule";

	public ScheduleCalculator() {

	}

	/*
	 * #########################################################################
	 * ###################
	 * 
	 * 
	 * #########################################################################
	 * ###################
	 */

	public static FinScheduleData getCalSchd(FinScheduleData finScheduleData, BigDecimal desiredPftAmount) {
		return new ScheduleCalculator(PROC_GETCALSCHD, finScheduleData, desiredPftAmount).getFinScheduleData();
	}

	public static FinScheduleData changeGraceEnd(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_CHANGEGRACEEND, finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData getFrqEMIHoliday(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_GETFRQEMIH, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	public static FinScheduleData getAdhocEMIHoliday(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_GETADHOCEMIH, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	public static FinScheduleData getPlanDeferPft(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_PLANDEFERPFT, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	public static FinScheduleData getDownPaySchd(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_DOWNPAYSCHD, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	public static FinScheduleData changeRate(FinScheduleData finScheduleData, String baseRate, String splRate,
			BigDecimal mrgRate, BigDecimal calculatedRate, boolean isCalSchedule) {
		return new ScheduleCalculator(PROC_CHANGERATE, finScheduleData, baseRate, splRate, mrgRate, calculatedRate,
				isCalSchedule).getFinScheduleData();
	}

	public static FinScheduleData changeRepay(FinScheduleData finScheduleData, BigDecimal amount, String schdMethod) {
		return new ScheduleCalculator(PROC_CHANGEREPAY, finScheduleData, amount, schdMethod).getFinScheduleData();
	}

	public static FinScheduleData postpone(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_POSTPONE, finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData unPlannedEMIH(FinScheduleData finScheduleData, BigDecimal amount, String schdMethod) {
		return new ScheduleCalculator(PROC_UNPLANEMIH, finScheduleData, amount, schdMethod).getFinScheduleData();
	}

	public static FinScheduleData reAging(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_REAGEH, finScheduleData).getFinScheduleData();
	}

	public static FinScheduleData changeProfit(FinScheduleData finScheduleData, BigDecimal desiredPftAmount) {
		return new ScheduleCalculator(PROC_CHANGEPROFIT, finScheduleData, desiredPftAmount).getFinScheduleData();
	}

	public static FinScheduleData addDisbursement(FinScheduleData finScheduleData, BigDecimal amount,
			BigDecimal feeChargeAmt, boolean utilizeGrcEndDisb) {
		return new ScheduleCalculator(PROC_ADDDISBURSEMENT, finScheduleData, amount, feeChargeAmt, utilizeGrcEndDisb)
				.getFinScheduleData();
	}

	public static FinScheduleData reCalSchd(FinScheduleData finScheduleData, String schdMethod) {
		return new ScheduleCalculator(PROC_RECALSCHD, finScheduleData, BigDecimal.ZERO, schdMethod)
				.getFinScheduleData();
	}

	public static FinScheduleData addTerm(FinScheduleData finScheduleData, int noOfTerms) {
		return new ScheduleCalculator(PROC_ADDTERM, finScheduleData, noOfTerms).getFinScheduleData();
	}

	public static FinScheduleData deleteTerm(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_DELETETERM, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	public static FinScheduleData refreshRates(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_REFRESHRATES, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	public static FinScheduleData recalEarlyPaySchedule(FinScheduleData finScheduleData, Date earlyPayOnSchdl,
			Date earlyPayOnNextSchdl, BigDecimal earlyPayAmt, String method) {
		return new ScheduleCalculator(finScheduleData, earlyPayOnSchdl, earlyPayOnNextSchdl, earlyPayAmt, method)
				.getFinScheduleData();
	}

	public static FinScheduleData addSubSchedule(FinScheduleData finScheduleData, int noOfTerms, Date subSchStartDate,
			String frqNewSchd) {
		return new ScheduleCalculator(PROC_SUBSCHEDULE, finScheduleData, noOfTerms, subSchStartDate, frqNewSchd)
				.getFinScheduleData();
	}

	public static FinScheduleData getCalERR(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(finScheduleData, PROC_CALEFFECTIVERATE).getFinScheduleData();
	}

	public static FinScheduleData recalInsuranceSchedule(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(finScheduleData, PROC_INSURANCESCHEDULE).getFinScheduleData();
	}

	public static FinScheduleData recalAdvPftRateSchedule(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(finScheduleData, PROC_ADVPFTRATESCHEDULE).getFinScheduleData();
	}

	public static FinScheduleData calSuplRentIncrCost(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(finScheduleData, PROC_SUPLRENTINCRCOST).getFinScheduleData();
	}

	public static FinScheduleData buildODSchedule(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(finScheduleData, PROC_BUILDOVERDRAFTSCHD).getFinScheduleData();
	}

	// Constructors

	// Constructors
	private ScheduleCalculator(String method, FinScheduleData finScheduleData) {
		logger.debug("Entering");

		if (StringUtils.equals(method, PROC_CHANGEGRACEEND)) {
			setFinScheduleData(procChangeGraceEnd(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_REAGEH)) {
			setFinScheduleData(procReAgeH(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_POSTPONE)) {
			setFinScheduleData(procPostpone(finScheduleData));
		}

		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal desiredPftAmount) {
		logger.debug("Entering");

		if (StringUtils.equals(method, PROC_GETCALSCHD)) {
			setFinScheduleData(procGetCalSchd(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_GETFRQEMIH)) {
			setFinScheduleData(procGetFrqEMIHoliday(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_GETADHOCEMIH)) {
			setFinScheduleData(procGetAdhocEMIHoliday(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_DELETETERM)) {
			setFinScheduleData(procDeleteTerm(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_REFRESHRATES)) {
			setFinScheduleData(procRefreshRates(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_PLANDEFERPFT)) {
			setFinScheduleData(procPlanDeferPft(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_DOWNPAYSCHD)) {
			setFinScheduleData(procDownpaySchd(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_CHANGEPROFIT)) {
			setFinScheduleData(procChangeProfit(finScheduleData, desiredPftAmount));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Schedule calculation to get the Total Desired
	 * Profit by including Planned Deferment Terms
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData procPlanDeferPft(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finScheduleData.getFinanceMain().setCpzAtGraceEnd(false);
		if (finScheduleData.getFinanceMain().getGrcProfitDaysBasis() == null) {
			finScheduleData.getFinanceMain()
					.setGrcProfitDaysBasis(finScheduleData.getFinanceMain().getProfitDaysBasis());
		}

		// Schedule calculation
		finScheduleData = preapareFinSchdData(finScheduleData, true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Processing Schedule calculation to get the Total Desired
	 * Profit by including Planned Deferment Terms
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData procDownpaySchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		Cloner cloner = new Cloner();
		FinScheduleData dpScheduleData = cloner.deepClone(finScheduleData);
		FinanceMain finMain = dpScheduleData.getFinanceMain();

		finMain.setFinReference(finMain.getFinReference() + "_DP");
		dpScheduleData.setFinReference(finMain.getFinReference());

		finMain.setFinAmount(finMain.getDownPayBank());
		finMain.setFeeChargeAmt(BigDecimal.ZERO);
		finMain.setInsuranceAmt(BigDecimal.ZERO);
		finMain.setDownPayBank(BigDecimal.ZERO);
		finMain.setDownPayment(BigDecimal.ZERO);
		finMain.setDownPaySupl(BigDecimal.ZERO);

		// Grace Period Details
		finMain.setGraceBaseRate("");
		finMain.setGraceSpecialRate("");
		finMain.setGrcPftRate(BigDecimal.ZERO);

		// Repay period Details
		finMain.setRepayBaseRate("");
		finMain.setRepaySpecialRate("");
		finMain.setRepayProfitRate(BigDecimal.ZERO);
		finMain.setScheduleMethod(CalculationConstants.SCHMTHD_EQUAL);
		finMain.setEqualRepay(true);
		finMain.setCalculateRepay(true);

		// Step Details
		finMain.setStepFinance(false);
		finMain.setStepPolicy("");
		finMain.setNoOfSteps(0);
		finMain.setAlwManualSteps(false);
		finMain.setPlanDeferCount(0);
		finMain.setDefferments(0);

		// Child List Details
		dpScheduleData.getDisbursementDetails().get(0).setDisbAmount(finMain.getFinAmount());
		dpScheduleData.getDisbursementDetails().get(0).setFeeChargeAmt(BigDecimal.ZERO);
		dpScheduleData.getStepPolicyDetails().clear();
		dpScheduleData.getRepayInstructions().clear();
		dpScheduleData.getFeeRules().clear();

		// Schedule Details
		dpScheduleData.getFinanceScheduleDetails().get(0).setClosingBalance(finMain.getFinAmount());
		dpScheduleData.getFinanceScheduleDetails().get(0).setFeeChargeAmt(BigDecimal.ZERO);
		dpScheduleData.getFinanceScheduleDetails().get(0).setDisbAmount(finMain.getFinAmount());
		dpScheduleData.getFinanceScheduleDetails().get(0).setDownPaymentAmount(BigDecimal.ZERO);
		dpScheduleData.getFinanceScheduleDetails().get(0).setDownpaymentOnSchDate(false);

		// Rate Reset to all Schedule terms
		for (FinanceScheduleDetail curSchd : dpScheduleData.getFinanceScheduleDetails()) {
			curSchd.setCalculatedRate(BigDecimal.ZERO);
			curSchd.setActRate(BigDecimal.ZERO);
		}

		// Schedule Calculation with New Setup Data for Down payment Program
		dpScheduleData = getCalSchd(dpScheduleData, BigDecimal.ZERO);

		logger.debug("Leaving");
		return dpScheduleData;
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, String baseRate, String splRate,
			BigDecimal mrgRate, BigDecimal calculatedRate, boolean isCalSchedule) {
		logger.debug("Entering");

		if (StringUtils.equals(method, PROC_CHANGERATE)) {

			finScheduleData = procChangeRate(finScheduleData, baseRate, splRate, mrgRate, calculatedRate,
					isCalSchedule);
			
			if(StringUtils.equals(finScheduleData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_STEPPOS)){
				finScheduleData = maintainPOSStep(finScheduleData);
			}

			// Advised Profit Rate Calculation Process
			finScheduleData = advPftRateCalculation(finScheduleData,
					finScheduleData.getFinanceMain().getEventFromDate(),
					finScheduleData.getFinanceMain().getEventToDate());

			finScheduleData.getFinanceMain().setScheduleMaintained(true);
			setFinScheduleData(finScheduleData);

		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal amount, String schdMethod) {
		logger.debug("Entering");

		if (StringUtils.equals(method, PROC_CHANGEREPAY)) {
			setFinScheduleData(procChangeRepay(finScheduleData, amount, schdMethod));
		}

		if (StringUtils.equals(method, PROC_UNPLANEMIH)) {
			setFinScheduleData(procUnPlanEMIH(finScheduleData, amount, schdMethod));
		}

		if (StringUtils.equals(method, PROC_RECALSCHD)) {
			setFinScheduleData(procReCalSchd(finScheduleData, schdMethod));
		}

		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal amount,
			BigDecimal feeChargeAmt, boolean utilizeGrcEndDisb) {
		logger.debug("Entering");

		if (StringUtils.equals(method, PROC_ADDDISBURSEMENT)) {
			setFinScheduleData(procAddDisbursement(finScheduleData, amount, feeChargeAmt, utilizeGrcEndDisb));
		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(FinScheduleData finScheduleData, String method) {
		logger.debug("Entering");

		if (StringUtils.equals(method, PROC_CALEFFECTIVERATE)) {
			setFinScheduleData(calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
					finScheduleData.getFinanceMain().getTotalGrossPft(),
					finScheduleData.getFinanceMain().getFinStartDate(),
					finScheduleData.getFinanceMain().getMaturityDate(), true));
		}
		if (StringUtils.equals(method, PROC_INSURANCESCHEDULE)) {
			insuranceCalculation(finScheduleData);
		}

		if (StringUtils.equals(method, PROC_ADVPFTRATESCHEDULE)) {
			setFinScheduleData(
					advPftRateCalculation(finScheduleData, finScheduleData.getFinanceMain().getEventFromDate(),
							finScheduleData.getFinanceMain().getEventToDate()));
		}
		if (StringUtils.equals(method, PROC_SUPLRENTINCRCOST)) {
			setFinScheduleData(calSuplRentIncrCost(finScheduleData, finScheduleData.getFinanceMain().getCurSuplRent(),
					finScheduleData.getFinanceMain().getCurIncrCost()));
		}
		if (StringUtils.equals(method, PROC_BUILDOVERDRAFTSCHD)) {
			setFinScheduleData(buildOverdraftSchd(finScheduleData));
		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, int noOfTerms) {
		logger.debug("Entering");
		if (StringUtils.equals(method, PROC_ADDTERM)) {
			setFinScheduleData(procAddTerm(finScheduleData, noOfTerms, false));
		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(FinScheduleData finScheduleData, Date earlyPayOnSchdl, Date earlyPayOnNextSchdl,
			BigDecimal earlyPayAmt, String method) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		finScheduleData.getFinanceMain().setResetOrgBal(false);
		finMain.setProcMethod(FinanceConstants.FINSER_EVENT_RECEIPT);

		finMain.setEventFromDate(earlyPayOnSchdl);
		finMain.setEventToDate(earlyPayOnSchdl);
		finMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);

		if (StringUtils.equals(CalculationConstants.EARLYPAY_ADJMUR, method)
				|| StringUtils.equals(CalculationConstants.EARLYPAY_ADMPFI, method)) {

			finMain.setRecalToDate(finMain.getMaturityDate());
			final BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

			// TODO: PV 19JAN17 schedule method should be sent correctly
			finScheduleData = changeRepay(finScheduleData, earlyPayAmt, finMain.getRecalSchdMethod());

			List<FinanceScheduleDetail> finSchdlDetailList = finScheduleData.getFinanceScheduleDetails();
			int size = finScheduleData.getFinanceScheduleDetails().size();
			Date eventToDate = finMain.getMaturityDate();
			for (int i = size - 1; i >= 0; i--) {
				FinanceScheduleDetail schDetail = finSchdlDetailList.get(i);
				if ((schDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0)) {
					finSchdlDetailList.remove(i);
				} else {
					eventToDate = schDetail.getSchDate();
					break;
				}
			}

			finMain.setMaturityDate(eventToDate);

			if (StringUtils.equals(CalculationConstants.EARLYPAY_ADMPFI, method)) {

				finMain.setEventToDate(eventToDate);
				// Apply Effective Rate for ReSchedule to get Desired Profit
				finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
						totalDesiredProfit, null, null, false);
				finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			}

		} else if (StringUtils.equals(CalculationConstants.EARLYPAY_RECRPY, method)) {

			finMain.setRecalToDate(finMain.getMaturityDate());

			// Schedule Repayment Change
			// TODO: PV 19JAN17 schedule method should be sent correctly
			finScheduleData = changeRepay(finScheduleData, earlyPayAmt, finMain.getRecalSchdMethod());

			// Schedule ReCalculations afetr Early Repayment Period based upon
			// Schedule Method
			finMain.setEventFromDate(earlyPayOnNextSchdl);
			finMain.setRecalFromDate(earlyPayOnNextSchdl);
			finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

			// TODO: PV 19JAN17 schedule method should be sent correctly
			finScheduleData = reCalSchd(finScheduleData, finMain.getScheduleMethod());

			// finScheduleData.getFinanceScheduleDetails().addAll(tempSchdlDetailList);

		} else if (StringUtils.equals(CalculationConstants.EARLYPAY_RECPFI, method)) {

			final BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

			finMain.setRecalToDate(finMain.getMaturityDate());

			// Apply Effective Rate for ReSchedule to get Desired Profit
			finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
					totalDesiredProfit, null, finMain.getMaturityDate(), false);
			
		} else if (StringUtils.equals(CalculationConstants.RPYCHG_STEPPOS, method)) {
			
			finMain.setRecalToDate(finMain.getMaturityDate());

			// Schedule Repayment Change
			finScheduleData = changeRepay(finScheduleData, earlyPayAmt, finMain.getRecalSchdMethod());

			// Schedule ReCalculations afetr Early Repayment Period based upon
			// Schedule Method
			finMain.setEventFromDate(earlyPayOnNextSchdl);
			finMain.setRecalFromDate(earlyPayOnNextSchdl);
			finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			
			finScheduleData = maintainPOSStep(finScheduleData);
			finScheduleData.getFinanceMain().setScheduleMaintained(true);

		} else if (StringUtils.equals(CalculationConstants.EARLYPAY_PRIHLD, method)) {
			finScheduleData = principalHoliday(finScheduleData, earlyPayAmt);
		}

		// Recalculation of Details after Schedule calculation
		finScheduleData = afterChangeRepay(finScheduleData);

		setFinScheduleData(finScheduleData);
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, int noOfTerms, Date subSchStartDate,
			String frqNewSchd) {
		logger.debug("Entering");
		if (StringUtils.equals(method, PROC_SUBSCHEDULE)) {
			setFinScheduleData(procSubSchedule(finScheduleData, noOfTerms, subSchStartDate, frqNewSchd));
		}
		logger.debug("Leaving");
	}

	/*
	 * #########################################################################
	 * ####################################### MAIN METHODS
	 * #########################################################################
	 * #######################################
	 */

	/*
	 * =========================================================================
	 * = ===================================== Method : procGetCalSchd
	 * Description : GET CALCULATED SCHEDULE Process This method will be be
	 * called only at the time of initial schedule creation by BUILD SCHEDULE
	 * FUNCTION
	 * =========================================================================
	 * =======================================
	 */

	private FinScheduleData procGetCalSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		/*
		 * Down payment support program is allowed only with Rate Basis
		 * "Flat converting to reducing" Planned Deferment is allowed only with
		 * Rate Basis "Flat converting to reducing" Step Finance is allowed in
		 * below scenarios Rate Basis Flat OR Rate Basis is
		 * "Flat converting to reducing" OR Rate Basis "Reducing" If (Schedule
		 * Method = "EQUAL" AND "Profit Rate basis" == 30/360 (Both European and
		 * US) OR If (Schedule Method = "PRI_PFT"
		 */

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finScheduleDetails = finScheduleData.getFinanceScheduleDetails();
		Boolean isCalFlat = false;

		if (finMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_C)) {
			isCalFlat = true;
		}

		if (finMain.getGrcProfitDaysBasis() == null) {
			finMain.setGrcProfitDaysBasis(finMain.getProfitDaysBasis());
		}

		// BPI then add BPI record to the schedule
		if (finMain.isAlwBPI()) {
			finScheduleData = addBPISchd(finScheduleData);
		}

		finMain.setEventFromDate(finMain.getFinStartDate());
		finMain.setEventToDate(finMain.getMaturityDate());
		finMain.setRecalFromDate(finMain.getFinStartDate());
		finMain.setRecalToDate(finMain.getMaturityDate());

		// PREPARE FIND SCHDULE DATA
		finScheduleData = preapareFinSchdData(finScheduleData, isCalFlat);
		finScheduleData = calSchdProcess(finScheduleData, isCalFlat, true);

		// CONVERT FLAT RATE TO REDUCING RATE.
		if (isCalFlat) {
			// finScheduleData = convertFlatToReduce(finScheduleData);
		}

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		// Load Original Schedule Data
		// If any changes in the schedule dates happened these values doesnot
		// have any meaning.
		int size = finScheduleDetails.size();
		for (int i = 0; i < size; i++) {
			curSchd = finScheduleDetails.get(i);
			curSchd.setOrgEndBal(curSchd.getClosingBalance());
			curSchd.setOrgPft(curSchd.getProfitSchd());
			curSchd.setOrgPri(curSchd.getPrincipalSchd());
		}

		// Reset Schedule Event Start & End Dates
		finMain.setEventFromDate(finMain.getFinStartDate());
		finMain.setEventToDate(finMain.getMaturityDate());

		// Insurance calculation
		insuranceCalculation(finScheduleData);

		// Advised Profit Rate Calculation Process
		finScheduleData = advPftRateCalculation(finScheduleData, finMain.getEventFromDate(), finMain.getEventToDate());

		// Supplementary Rent & Increased Cost Calculation
		if (finMain.getSupplementRent().compareTo(BigDecimal.ZERO) > 0
				|| finMain.getIncreasedCost().compareTo(BigDecimal.ZERO) > 0) {
			finScheduleData = calSuplRentIncrCost(finScheduleData, finMain.getSupplementRent(),
					finMain.getIncreasedCost());
		}

		setFinScheduleData(finScheduleData);

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procGetFrqEMIHoliday(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		FinanceMain finMain = finScheduleData.getFinanceMain();

		finScheduleData = validateEMIHoliday(finScheduleData);
		if (finScheduleData.getErrorDetails() != null && finScheduleData.getErrorDetails().size() > 0) {
			// Return the schedule header
			logger.debug("Leaving");
			return finScheduleData;
		}

		int frqSize = finScheduleData.getPlanEMIHmonths().size();
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		int planEMIHMaxPerYear = finMain.getPlanEMIHMaxPerYear();
		int planEMIHMax = finMain.getPlanEMIHMax();
		boolean planEMICpz = finMain.isPlanEMICpz();

		int markedEMIHMaxPerYear = 0;
		int markedEMIHMax = 0;

		Date datePlanEMIHLock = DateUtility.addMonths(finMain.getFinStartDate(), finMain.getPlanEMIHLockPeriod());
		Date dateAfterYear = DateUtility.addMonths(finMain.getFinStartDate(), 12);

		for (int i = 0; i < sdSize - 1; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			Date schdDate = curSchd.getSchDate();

			// Before Lock Period do not mark holiday
			if (DateUtility.compare(schdDate, datePlanEMIHLock) <= 0) {
				continue;
			}

			// Before Grace Period should not mark as Holiday
			if (DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) <= 0) {
				continue;
			}

			// First payment date also cannot be allowed in planned EMI holiday
			// declaration
			if (curSchd.getInstNumber() == 1) {
				continue;
			}

			// Find schedule date is requested holiday or not
			if (planEMIHMaxPerYear == 0 || markedEMIHMaxPerYear < planEMIHMaxPerYear) {
				int curSchdMonth = DateUtility.getMonth(schdDate);
				for (int j = 0; j < frqSize; j++) {
					int curFrqMonth = finScheduleData.getPlanEMIHmonths().get(j);
					if (curSchdMonth == curFrqMonth && (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())) {

						// Before Event From Date don't do any changes(Used same
						// for servicing Plan EMI/Rescheduling)
						if (DateUtility.compare(schdDate, finMain.getEventFromDate()) > 0) {
							curSchd.setCpzOnSchDate(planEMICpz);
							curSchd.setBpiOrHoliday(FinanceConstants.FLAG_HOLIDAY);
						}

						if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
							markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
							markedEMIHMax = markedEMIHMax + 1;
						}
						break;
					} else {

						// Before Event From Date don't do any changes(Used same
						// for servicing Plan EMI/Rescheduling)
						if (DateUtility.compare(schdDate, finMain.getEventFromDate()) > 0) {
							if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())
									|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
								curSchd.setCpzOnSchDate(
										FrequencyUtil.isFrqDate(finMain.getRepayCpzFrq(), curSchd.getSchDate()));
								curSchd.setBpiOrHoliday("");
							}
						} else {
							if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
								markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
								markedEMIHMax = markedEMIHMax + 1;
								break;
							}
						}
					}
				}
			} else {

				// Before Event From Date don't do any changes(Used same for
				// servicing Plan EMI/Rescheduling)
				if (DateUtility.compare(schdDate, finMain.getEventFromDate()) > 0) {
					if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
						curSchd.setCpzOnSchDate(
								FrequencyUtil.isFrqDate(finMain.getRepayCpzFrq(), curSchd.getSchDate()));
						curSchd.setBpiOrHoliday("");
					}
				} else {
					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
						markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
						markedEMIHMax = markedEMIHMax + 1;
					}
				}
			}

			// Reset marked holidays per year
			if (DateUtility.compare(schdDate, dateAfterYear) >= 0) {
				markedEMIHMaxPerYear = 0;
				dateAfterYear = DateUtility.addMonths(schdDate, 12);
			}

			if (planEMIHMax != 0 && markedEMIHMax >= planEMIHMax) {
				break;
			}
		}

		if (finMain.getEventFromDate() != null
				&& DateUtility.compare(finMain.getEventFromDate(), finMain.getFinStartDate()) == 0) {
			finMain.setEqualRepay(true);
			finMain.setCalculateRepay(true);
		}

		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null,BigDecimal.ZERO);
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procGetAdhocEMIHoliday(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		finScheduleData = validateEMIHoliday(finScheduleData);

		if (finScheduleData.getErrorDetails() != null && finScheduleData.getErrorDetails().size() > 0) {
			// Return the schedule header
			logger.debug("Leaving");
			return finScheduleData;
		}

		int hdSize = finScheduleData.getPlanEMIHDates().size();
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		int planEMIHMaxPerYear = finMain.getPlanEMIHMaxPerYear();
		int planEMIHMax = finMain.getPlanEMIHMax();
		boolean planEMICpz = finMain.isPlanEMICpz();

		int markedEMIHMaxPerYear = 0;
		int markedEMIHMax = 0;

		Date datePlanEMIHLock = DateUtility.addMonths(finMain.getFinStartDate(), finMain.getPlanEMIHLockPeriod());
		Date dateAfterYear = DateUtility.addMonths(finMain.getFinStartDate(), 12);
		Collections.sort(finScheduleData.getPlanEMIHDates());

		for (int j = 0; j < sdSize - 1; j++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(j);
			Date schdDate = curSchd.getSchDate();

			// First payment date also cannot be allowed in planned EMI holiday
			// declaration
			if (curSchd.getInstNumber() == 1) {
				continue;
			}

			// Reset marked holidays per year
			if (DateUtility.compare(schdDate, dateAfterYear) >= 0) {
				markedEMIHMaxPerYear = 0;
				dateAfterYear = DateUtility.addMonths(schdDate, 12);
			}

			if (DateUtility.compare(schdDate, datePlanEMIHLock) <= 0) {
				continue;
			}

			boolean isPlanEmiHFound = false;
			if (hdSize != markedEMIHMax) {
				for (int i = 0; i < hdSize; i++) {
					Date hdDate = finScheduleData.getPlanEMIHDates().get(i);

					// Find schedule date is requested holiday or not
					if (planEMIHMaxPerYear == 0 || markedEMIHMaxPerYear < planEMIHMaxPerYear) {
						if (DateUtility.compare(hdDate, schdDate) == 0) {
							if ((curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())) {

								// Before Event From Date don't do any
								// changes(Used same for servicing Plan
								// EMI/Rescheduling)
								if (DateUtility.compare(schdDate, finMain.getEventFromDate()) > 0) {
									curSchd.setCpzOnSchDate(planEMICpz);
									curSchd.setBpiOrHoliday(FinanceConstants.FLAG_HOLIDAY);
								}

								if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
									markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
									markedEMIHMax = markedEMIHMax + 1;
								}
							}
							isPlanEmiHFound = true;
							break;
						}
					}

					if (planEMIHMax != 0 && markedEMIHMax >= planEMIHMax) {
						break;
					}
				}
			}

			// Before Event From Date don't do any changes(Used same for
			// servicing Plan EMI/Rescheduling)
			if (!isPlanEmiHFound) {
				if (DateUtility.compare(schdDate, finMain.getEventFromDate()) > 0) {
					if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
						curSchd.setCpzOnSchDate(
								FrequencyUtil.isFrqDate(finMain.getRepayCpzFrq(), curSchd.getSchDate()));
						curSchd.setBpiOrHoliday("");
					}
				} else {
					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
						markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
						markedEMIHMax = markedEMIHMax + 1;
					}
				}
			}
		}

		finMain.setEqualRepay(true);
		finMain.setCalculateRepay(true);

		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData validateEMIHoliday(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		// TODO: PV: Correct Error Code
		if (!finMain.isPlanEMIHAlw()) {
			finScheduleData.setErrorDetail(
					new ErrorDetail("SCH37", "Loan Does not allow Planned EMI's", new String[] { " " }));
			return finScheduleData;
		}

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, finMain.getProductCategory())) {
			finScheduleData.setErrorDetail(
					new ErrorDetail("SCH37", "Product Category not allow Planned EMI's", new String[] { " " }));
			return finScheduleData;
		}

		if (finMain.isStepFinance()) {
			finScheduleData.setErrorDetail(
					new ErrorDetail("SCH37", "Step Loans does not allow Planned EMI's", new String[] { " " }));
			return finScheduleData;
		}

		if (finMain.isFinIsAlwMD()) {
			finScheduleData.setErrorDetail(new ErrorDetail("SCH37",
					"Multi Disbursement Loans does not allow Planned EMI's", new String[] { " " }));
			return finScheduleData;
		}

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * = ====================================== Method : procReCalSchd
	 * Description : Re Calculate schedule from a given date to end date Process
	 * : Should change the repay amount and recalculate repay amounts of
	 * remaining ========
	 * ==================================================================
	 * ======================================
	 */
	private FinScheduleData procChangeGraceEnd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finScheduleData = ScheduleGenerator.getChangedSchd(finScheduleData);
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		int sdSize = finSchdDetails.size();

		Date oldInstructions = new Date();
		if (DateUtility.compare(finMain.getEventFromDate(), finMain.getGrcPeriodEndDate()) <= 0) {
			oldInstructions = finMain.getEventFromDate();
		} else {
			oldInstructions = finMain.getGrcPeriodEndDate();
		}

		// Delete All repay instructions in repayment period
		for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {
			if (DateUtility.compare(finScheduleData.getRepayInstructions().get(i).getRepayDate(),
					oldInstructions) >= 0) {
				finScheduleData.getRepayInstructions().remove(i);
				i = i - 1;
			}
		}

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
				continue;
			}

			if (!curSchd.isPftOnSchDate() && !curSchd.isRepayOnSchDate()) {
				continue;
			}

			finMain.setRecalFromDate(curSchd.getSchDate());
			finMain.setEventFromDate(finMain.getGrcPeriodEndDate());
			finMain.setRecalSchdMethod(finMain.getScheduleMethod());
			finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			break;
		}

		finMain.setEventToDate(finMain.getMaturityDate());
		finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGEGRACEEND, BigDecimal.ZERO, BigDecimal.ZERO);
		
		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}
		
		finScheduleData = calSchdProcess(finScheduleData, false, false);
		finMain.setScheduleMaintained(true);
		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * = ====================================== Method : procReCalSchd
	 * Description : Re Calculate schedule from a given date to end date Process
	 * : Should change the repay amount and recalculate repay amounts of
	 * remaining ========
	 * ==================================================================
	 * ======================================
	 */
	private FinScheduleData procReCalSchd(FinScheduleData finScheduleData, String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();

		// BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

		Date evtFromDate = finMain.getRecalFromDate();
		finMain.setEventFromDate(evtFromDate);
		finMain.setEventToDate(evtFromDate);

		sortRepayInstructions(repayInstructions);
		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.equals(schdMethod, "")) {
			for (int i = 0; i < risize; i++) {
				if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
				}

				// FIXME: below code is temporary fix, need to set schedule
				// method
				if (StringUtils.isBlank(finMain.getRecalSchdMethod())) {
					finMain.setRecalSchdMethod(finMain.getScheduleMethod());
				}

				if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) >= 0) {
					break;
				}
			}
		}

		finMain.setRecalSchdMethod(schdMethod);

		finScheduleData = setRecalAttributes(finScheduleData, PROC_RECALSCHD, BigDecimal.ZERO, BigDecimal.ZERO);
		
		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}
		
		finScheduleData = calSchdProcess(finScheduleData, false, false);

		finMain.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * ======================================= Method : procDeleteTerm
	 * Description : DELETE TERM Process :
	 * ====================================================
	 * =========================================================================
	 * =======================================
	 */

	private FinScheduleData procDeleteTerm(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		Date evtFromDate = finMain.getEventFromDate();
		Date curBussniessDate = DateUtility.getAppDate();

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
			finScheduleData.setErrorDetail(new ErrorDetail("SCH36",
					"REQUETSED DELETED TERMS DATE IS BEFORE CURRENT BUSINESS DATE. DELETION OF TERMS NOT POSSIBLE",
					new String[] { " " }));
			logger.warn(
					"SCH36 - REQUETSED DELETED TERMS DATE IS BEFORE CURRENT BUSINESS DATE. DELETION OF TERMS NOT POSSIBLE");
			return finScheduleData;

		}

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		for (int i = sdSize - 1; i > 0; i--) {
			curSchd = finSchdDetails.get(i);

			// If any of Profit and Principal Payments already completed for
			// last possible date before requested deleted date then deletion of
			// terms not possible
			if (DateUtility.compare(curSchd.getSchDate(), evtFromDate) >= 0) {
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
						|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					// Through Error
					finScheduleData.setErrorDetail(new ErrorDetail("SCH34",
							"NO UNPAID SCHEDULE DATES FOUNDS BEFORE REQUESTED DELETE TERMS DATE. DELETION OF TERMS NOT POSSIBLE",
							new String[] { " " }));
					logger.warn(
							"SCH34 - NO UNPAID SCHEDULE DATES FOUNDS BEFORE REQUESTED DELETE TERMS DATE. DELETION OF TERMS NOT POSSIBLE");
					return finScheduleData;
				}
			}

			// Found Unpaid date after current business date and before
			// requested deleted date
			finMain.setIndexStart(i);
			if (DateUtility.compare(curSchd.getSchDate(), evtFromDate) == 0) {
				break;
			}
		}

		// Delete all schedule details after requested delete terms date
		for (int i = finMain.getIndexStart(); i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			if (DateUtility.compare(finScheduleData.getFinanceScheduleDetails().get(i).getSchDate(), evtFromDate) > 0) {
				finScheduleData.getFinanceScheduleDetails().remove(i);
				i--;
			}
		}

		// Reset Maturity Date, Recal type
		finMain.setMaturityDate(finScheduleData.getFinanceScheduleDetails()
				.get(finScheduleData.getFinanceScheduleDetails().size() - 1).getSchDate());
		finMain.setCalMaturity(finMain.getMaturityDate());

		// Delete All repay instructions after requested delete terms date
		for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {
			if (DateUtility.compare(finScheduleData.getRepayInstructions().get(i).getRepayDate(),
					finMain.getCalMaturity()) >= 0) {
				finScheduleData.getRepayInstructions().remove(i);
			}
		}

		// Recalculate Schedule
		finScheduleData = procReCalSchd(finScheduleData, "");
		finMain.setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * ======================================== Method : procChangeRate
	 * Description : CHANGE RATE Process : Should change the rate for the
	 * requested dates and recalculate the repay amount based on the requested
	 * recalculation type i) CURPRD: Change the rate for the given dates and
	 * repay recalculation will happen for the period covered in the dates only.
	 * All remaining repayments should be unchanged.
	 * 
	 * ii) TILLMDT: Change rate for given dates. Repay recalculation will happen
	 * till maturity date
	 * 
	 * iii) ADJMDT: Change the rate for the given dates but no repay
	 * recalculation will happen. Means increased/decreased profit will be
	 * adjusted to final repay.
	 * 
	 * Schedule Method CURPRD TILLDATE TILLMDT ADJMDT EQUAL Y N Y Y PFT Y N N N
	 * PRI Y N Y Y PRI_PFT Y N Y Y
	 * =========================================================================
	 * ========================================
	 */

	private FinScheduleData procChangeRate(FinScheduleData finScheduleData, String baseRate, String splRate,
			BigDecimal mrgRate, BigDecimal calculatedRate, boolean isCalSchedule) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		int sdSize = finSchdDetails.size();

		FinanceMain finMain = finScheduleData.getFinanceMain();
		String recaltype = finMain.getRecalType();
		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		finMain.setCompareToExpected(false);
		finMain.setCompareExpectedResult(BigDecimal.ZERO);

		// Current Period or Till MDT set Recal from date and recal todate
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_CURPRD)) {
			finScheduleData = getCurPerodDates(finScheduleData);
		}

		// Force Set recaltype and recal to date to TILLMDT
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_CURPRD)) {

			if (DateUtility.compare(evtToDate, finMain.getGrcPeriodEndDate()) <= 0) {
				finMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			}

			if (DateUtility.compare(evtToDate, finMain.getMaturityDate()) >= 0) {
				finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			}
		}

		// FIXME: When Recal From Date and Recal To Date are different periods.
		// Delete this line once proved working
		// Same code is kept in add disbursement also (Whereever recal is
		// possible in two periods..)

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT)) {
			finMain.setRecalFromDate(finMain.getMaturityDate());
			finMain.setRecalToDate(finMain.getMaturityDate());
		}

		if (DateUtility.compare(finMain.getRecalFromDate(), finMain.getGrcPeriodEndDate()) <= 0
				&& DateUtility.compare(finMain.getRecalToDate(), finMain.getGrcPeriodEndDate()) > 0) {

			for (int i = 0; i < finSchdDetails.size(); i++) {
				if (DateUtility.compare(finSchdDetails.get(i).getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
					continue;
				}

				finMain.setRecalFromDate(finSchdDetails.get(i).getSchDate());
				break;

			}
		}

		// Setting Rates between Fromdate and Todate
		BigDecimal recalculateRate = BigDecimal.ZERO;

		// Find EVENT FROM Date in the schedule. If not found add.
		int prvIndex = 0;
		boolean isSchdDateFound = false;

		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);
			Date schdDate = curSchd.getSchDate();

			if (DateUtility.compare(schdDate, evtFromDate) > 0) {
				break;
			}

			if (DateUtility.compare(schdDate, evtFromDate) == 0) {
				curSchd.setRvwOnSchDate(true);
				isSchdDateFound = true;
			}

			prvIndex = i;
		}

		if (!isSchdDateFound) {
			addSchdRcd(finScheduleData, evtFromDate, prvIndex);
			finSchdDetails = finScheduleData.getFinanceScheduleDetails();
			prvIndex = prvIndex + 1;
			finSchdDetails.get(prvIndex).setRvwOnSchDate(true);
			sdSize = finSchdDetails.size();
		}

		// Find EVENT TO Date in the schedule. If not found add.
		isSchdDateFound = false;

		for (int i = prvIndex; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);
			Date schdDate = curSchd.getSchDate();

			if (DateUtility.compare(schdDate, evtToDate) > 0) {
				break;
			}

			if (DateUtility.compare(schdDate, evtToDate) == 0) {
				curSchd.setRvwOnSchDate(true);
				isSchdDateFound = true;
			}

			prvIndex = i;

		}

		if (!isSchdDateFound) {
			addSchdRcd(finScheduleData, evtToDate, prvIndex);
			finSchdDetails = finScheduleData.getFinanceScheduleDetails();
			prvIndex = prvIndex + 1;
			finSchdDetails.get(prvIndex).setRvwOnSchDate(true);
			sdSize = finSchdDetails.size();
		}

		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);
			Date schdDate = curSchd.getSchDate();

			// Setting Rates between Fromdate and Todate
			if ((DateUtility.compare(schdDate, evtFromDate) >= 0 && DateUtility.compare(schdDate, evtToDate) < 0)
					|| (i == (sdSize - 1))) {
				recalculateRate = calculatedRate;

				if (curSchd.isRvwOnSchDate()) {
					if (StringUtils.isNotBlank(baseRate)) {
						if (DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) < 0) {
							recalculateRate = RateUtil.rates(baseRate, finMain.getFinCcy(), splRate, mrgRate, schdDate,
									finMain.getGrcMinRate(), finMain.getGrcMaxRate()).getNetRefRateLoan();
						} else {
							recalculateRate = RateUtil.rates(baseRate, finMain.getFinCcy(), splRate, mrgRate, schdDate,
									finMain.getRpyMinRate(), finMain.getRpyMaxRate()).getNetRefRateLoan();
						}
					}
				}

				curSchd.setBaseRate(baseRate);
				curSchd.setSplRate(splRate);
				curSchd.setMrgRate(mrgRate);
				curSchd.setCalculatedRate(recalculateRate);
			}

			if (DateUtility.compare(schdDate, evtToDate) >= 0) {
				break;
			}
		}

		// call the process
		if (isCalSchedule) {
			finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGERATE, BigDecimal.ZERO, BigDecimal.ZERO);
			
			// Developer Finance Principal Holiday Repayment Instructions Recalculation
			if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
				finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
			}
			
			finScheduleData = calSchdProcess(finScheduleData, false, false);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData addSchdRcd(FinScheduleData finScheduleData, Date newSchdDate, int prvIndex) {
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(prvIndex);

		FinanceScheduleDetail sd = new FinanceScheduleDetail();
		sd.setFinReference(finScheduleData.getFinanceMain().getFinReference());
		sd.setBpiOrHoliday("");
		sd.setSchDate(newSchdDate);
		sd.setDefSchdDate(newSchdDate);

		sd.setBaseRate(prvSchd.getBaseRate());
		sd.setSplRate(prvSchd.getSplRate());
		sd.setMrgRate(prvSchd.getMrgRate());
		sd.setActRate(prvSchd.getActRate());
		sd.setCalculatedRate(prvSchd.getCalculatedRate());
		sd.setSchdMethod(prvSchd.getSchdMethod());
		sd.setPftDaysBasis(prvSchd.getPftDaysBasis());
		sd.setAdvBaseRate(prvSchd.getAdvBaseRate());
		sd.setAdvMargin(prvSchd.getAdvMargin());
		sd.setAdvPftRate(prvSchd.getAdvPftRate());
		sd.setSuplRent(prvSchd.getSuplRent());
		sd.setIncrCost(prvSchd.getIncrCost());
		sd.setOrgEndBal(prvSchd.getOrgEndBal());

		finScheduleData.getFinanceScheduleDetails().add(sd);
		finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));

		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * ======================================= Method : procChangeRepay
	 * Description : CHANGE REPAY AMOUNT Process : Should change the repay
	 * amount and recalculate repay amounts of remaining schedule based on the
	 * requested recalculation type. Not Applicable for schedule method "PFT" i)
	 * TILLDATE: Change the repay for requested dates and recalculate schedule.
	 * ii) TILLMDT: Change the repay till maturity date iii) ADJMDT: Change the
	 * repay and increase/decrease in profit will be * adjusted to maturity.
	 * 
	 * NOTE : Schedule Method EQUAL/PRI will allow all recalculation types
	 * Schedule Method PFT/PRI_PFT will allow CURPRD only
	 * =========================================================================
	 * =======================================
	 */

	private FinScheduleData procChangeRepay(FinScheduleData finScheduleData, BigDecimal repayAmount,
			String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();

		// BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.isBlank(schdMethod)) {
			for (int i = 0; i < risize; i++) {
				if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
				}

				// FIXME: below code is temporary fix, need to set schedule
				// method
				if (StringUtils.isBlank(finMain.getRecalSchdMethod())) {
					finMain.setRecalSchdMethod(finMain.getScheduleMethod());
				}

				if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) >= 0) {
					break;
				}
			}
		}

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		int prvIndex = 0;
		boolean isRepaymentFoundInSD = false;

		Date schdDate = new Date();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (DateUtility.compare(schdDate, evtFromDate) > 0) {
				break;
			}

			if (DateUtility.compare(schdDate, evtFromDate) == 0) {
				// To make sure the flags are TRUE when repayment happens
				curSchd.setPftOnSchDate(true);
				curSchd.setRepayOnSchDate(true);
				isRepaymentFoundInSD = true;
				break;
			}

			prvIndex = i;
		}

		if (!isRepaymentFoundInSD) {
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, prvIndex);
			curSchd = finSchdDetails.get(prvIndex + 1);
			curSchd.setPftOnSchDate(true);
			curSchd.setRepayOnSchDate(true);
			curSchd.setRepayAmount(repayAmount);
		}

		finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGEREPAY, BigDecimal.ZERO, repayAmount);
		
		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}
		
		finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtToDate, repayAmount, schdMethod);
		finScheduleData = calSchdProcess(finScheduleData, false, false);

		finMain.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData afterChangeRepay(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		String recaltype = finMain.getRecalType();

		// TODO: PV Will be addresses while working for Flat to converting and
		// Islamic
		/*
		 * if (finMain.isPftIntact()) { finScheduleData =
		 * calEffectiveRate(finScheduleData,
		 * CalculationConstants.SCH_SPECIFIER_TOTAL, totalDesiredProfit,
		 * evtFromDate, finMain.getCalMaturity(), false); } else {
		 * finScheduleData = calSchdProcess(finScheduleData, false, false); }
		 */

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADDTERM)
				|| StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADDRECAL)) {

			// Insurance Schedule Calculation
			finScheduleData = insuranceCalculation(finScheduleData);

		}

		// Advised Profit Rate Calculation Process
		finScheduleData = advPftRateCalculation(finScheduleData, finMain.getEventFromDate(), finMain.getMaturityDate());

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procPostpone(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();
		int adjTerms = 0;
		finMain.setCompareToExpected(false);
		finMain.setRecalSchdMethod(CalculationConstants.RPYCHG_ADJMDT);
		finMain.setCalculateRepay(false);

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		Date schdDate = new Date();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (DateUtility.compare(schdDate, evtFromDate) < 0) {
				continue;
			}

			if (DateUtility.compare(schdDate, evtToDate) > 0) {
				break;
			}

			adjTerms = adjTerms + 1;

			if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
				curSchd.setBpiOrHoliday(FinanceConstants.FLAG_POSTPONE);
			}

		}

		finScheduleData = procAddTerm(finScheduleData, adjTerms, false);
		
		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		finMain.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procReAgeH(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();
		int adjTerms = 0;
		finMain.setCompareToExpected(false);
		finMain.setRecalSchdMethod(CalculationConstants.RPYCHG_ADJMDT);
		finMain.setCalculateRepay(false);

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		Date schdDate = new Date();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (DateUtility.compare(schdDate, evtFromDate) < 0) {
				continue;
			}

			if (DateUtility.compare(schdDate, evtToDate) > 0) {
				break;
			}

			adjTerms = adjTerms + 1;

			if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
				curSchd.setBpiOrHoliday(FinanceConstants.FLAG_REAGE);
			}

		}

		finScheduleData = procAddTerm(finScheduleData, adjTerms, false);

		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		finMain.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procUnPlanEMIH(FinScheduleData finScheduleData, BigDecimal repayAmount, String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();

		// BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.isBlank(schdMethod)) {
			for (int i = 0; i < risize; i++) {
				if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
				}

				// FIXME: below code is temporary fix, need to set schedule
				// method
				if (StringUtils.isBlank(finMain.getRecalSchdMethod())) {
					finMain.setRecalSchdMethod(finMain.getScheduleMethod());
				}

				if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) >= 0) {
					break;
				}
			}
		}

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		int prvIndex = 0;
		boolean isRepaymentFoundInSD = false;

		Date schdDate = new Date();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (DateUtility.compare(schdDate, evtFromDate) > 0) {
				break;
			}

			if (DateUtility.compare(schdDate, evtFromDate) == 0) {
				// To make sure flags are TRUE even on holiday
				curSchd.setPftOnSchDate(true);
				curSchd.setRepayOnSchDate(true);
				isRepaymentFoundInSD = true;
				break;
			}

			prvIndex = i;
		}

		if (!isRepaymentFoundInSD) {
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, prvIndex);
			curSchd = finSchdDetails.get(prvIndex + 1);
			curSchd.setPftOnSchDate(true);
			curSchd.setRepayOnSchDate(true);
			curSchd.setRepayAmount(repayAmount);
		}

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (DateUtility.compare(schdDate, evtFromDate) < 0) {
				continue;
			}

			if (DateUtility.compare(schdDate, evtToDate) > 0) {
				break;
			}

			if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
				curSchd.setBpiOrHoliday(FinanceConstants.FLAG_UNPLANNED);
			}
		}

		finScheduleData = setRecalAttributes(finScheduleData, PROC_UNPLANEMIH, BigDecimal.ZERO, repayAmount);
		finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtToDate, repayAmount, schdMethod);
	
		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}
		finScheduleData = calSchdProcess(finScheduleData, false, false);

		finMain.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * ======================================= Method : procAddDisburse
	 * Description : ADD DISBURSEMENT Process :
	 * ==============================================
	 * ========================================
	 * =======================================================================
	 */

	private FinScheduleData procAddDisbursement(FinScheduleData orgFinScheduleData, BigDecimal newDisbAmount,
			BigDecimal newFeeAmt, boolean utilizeGrcEndDisb) {
		logger.debug("Entering");

		// Create Cloner for calculation purpose. If calculation is successful
		// send back calculated DATA else send original data with errors
		FinScheduleData finScheduleData = null;
		Cloner cloner = new Cloner();
		finScheduleData = cloner.deepClone(orgFinScheduleData);
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<FinanceDisbursement> finDisbDetails = finScheduleData.getDisbursementDetails();
		finScheduleData.getFinanceMain().setResetOrgBal(false);
		
		// Original END Balance update for Developer Finance
		Date graceEndDate = finMain.getGrcPeriodEndDate();
		String recaltype = finMain.getRecalType();
		Date evtFromDate = finMain.getEventFromDate();

		finMain.setCalculateRepay(false);
		finMain.setEqualRepay(false);
		finMain.setCompareToExpected(false);
		finMain.setCompareExpectedResult(BigDecimal.ZERO);
		finMain.setSchdIndex(0);
		

		boolean isDisbDateFoundInSD = false;

		int disbSeq = 0;
		int disbIndex = 0;
		Date schdDate = finMain.getFinStartDate();

		// Find highest sequence for the current disbursement
		int dbSize = finDisbDetails.size();
		for (int i = 0; i < dbSize; i++) {
			FinanceDisbursement curDisb = finDisbDetails.get(i);
			if (curDisb.getDisbSeq() > disbSeq) {
				disbSeq = curDisb.getDisbSeq();
			}
		}

		// ADD new disbursement Record.
		FinanceDisbursement dd = new FinanceDisbursement();
		dd.setDisbAmount(newDisbAmount);
		dd.setDisbDate(evtFromDate);
		dd.setFeeChargeAmt(newFeeAmt);
		dd.setDisbSeq(disbSeq + 1);
		finScheduleData.getDisbursementDetails().add(dd);

		// Applicable for For few products (Ijarah) only.
		if (utilizeGrcEndDisb && !StringUtils.equals(finMain.getProductCategory(), FinanceConstants.PRODUCT_IJARAH)) {
			utilizeGrcEndDisb = false;
		}

		// If No Grace no adjustment is applicable
		if (DateUtility.compare(finMain.getFinStartDate(), graceEndDate) == 0) {
			utilizeGrcEndDisb = false;
		}

		// If Disbursement Date is after Grace End Date no adjustment is
		// applicable
		if (DateUtility.compare(evtFromDate, graceEndDate) >= 0) {
			utilizeGrcEndDisb = false;
		}

		int sdSize = finSchdDetails.size();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		if (utilizeGrcEndDisb) {
			for (int i = 0; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
				schdDate = curSchd.getSchDate();

				if (DateUtility.compare(schdDate, graceEndDate) < 0) {
					continue;
				}

				curSchd.setDisbAmount(curSchd.getDisbAmount().subtract(newDisbAmount));
				if (curSchd.getDisbAmount().compareTo(BigDecimal.ZERO) < 0) {
					curSchd.setDisbAmount(BigDecimal.ZERO);
					break;
				}
			}
		}

		// Add Disbursement amount to existing record if found
		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			// Schedule Date before event from date
			if (curSchd.getSchDate().before(evtFromDate)) {
				disbIndex = i;
				continue;

				// Schedule Date matches event from date
			} else if (DateUtility.compare(curSchd.getSchDate(), evtFromDate) == 0) {
				isDisbDateFoundInSD = true;
				curSchd.setDisbAmount(curSchd.getDisbAmount().add(newDisbAmount));
				curSchd.setDisbOnSchDate(true);
				curSchd.setFeeChargeAmt(curSchd.getFeeChargeAmt().add(newFeeAmt));
				if(i != 0){
					curSchd.setClosingBalance(finSchdDetails.get(i-1).getClosingBalance().add(newDisbAmount).add(newFeeAmt));
				}
				disbIndex = i;
				break;

				// Event from date not found
			} else {
				break;
			}
		}

		FinanceScheduleDetail prvSchd = new FinanceScheduleDetail();
		// If new disbursement date add a record in schedule
		if (!isDisbDateFoundInSD) {
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, disbIndex);
			prvSchd = finSchdDetails.get(disbIndex);
			disbIndex = disbIndex + 1;
			curSchd = finScheduleData.getFinanceScheduleDetails().get(disbIndex);

			curSchd.setDisbOnSchDate(true);
			curSchd.setDisbAmount(newDisbAmount);
			curSchd.setFeeChargeAmt(newFeeAmt);
			curSchd.setClosingBalance(prvSchd.getClosingBalance().add(newDisbAmount).add(newFeeAmt));
		}
		
		// Setting Original Schedule End balances with New Disbursement changes
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			int recalTermsForOrgBal = 0;
			if(StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLDATE) ||
					StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLMDT) ||
					StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJTERMS)){

				for (FinanceScheduleDetail schd : finSchdDetails) {
					if(DateUtility.compare(schd.getSchDate(), graceEndDate) > 0 && 
							DateUtility.compare(schd.getSchDate(), finMain.getRecalFromDate()) >= 0 &&
							DateUtility.compare(schd.getSchDate(), finMain.getRecalToDate()) <= 0){

						recalTermsForOrgBal = recalTermsForOrgBal + 1;
					}
				}
			}

			// Add Additional Requested Terms For Adjust Term Functionality
			if(StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJTERMS)){
				recalTermsForOrgBal = recalTermsForOrgBal + finMain.getAdjTerms();
			}

			BigDecimal balDisbAmount = newDisbAmount.add(newFeeAmt);
			BigDecimal adjOrgAmount = balDisbAmount;
			if(recalTermsForOrgBal > 0){
				adjOrgAmount = balDisbAmount.divide(new BigDecimal(recalTermsForOrgBal), 0, RoundingMode.HALF_DOWN);
				adjOrgAmount = CalculationUtil.roundAmount(adjOrgAmount, finMain.getCalRoundingMode(), finMain.getRoundingTarget());
			}

			// Setting New Disbursement changes into Original Ending Balance
			for (FinanceScheduleDetail schd : finSchdDetails) {

				// No Change in Disbursement addition to Original End Balance before selected recalculation
				if(DateUtility.compare(schd.getSchDate(), finMain.getRecalFromDate()) < 0 &&
						DateUtility.compare(schd.getSchDate(), evtFromDate) >= 0){
					schd.setOrgEndBal(schd.getOrgEndBal().add(balDisbAmount));
				}
				
				// If Schedule Date before Grace End date, should not consider on adjustment
				if(DateUtility.compare(schd.getSchDate(), graceEndDate) <= 0){
					continue;
				}

				// Adjust amounts between recalculation dates
				if(DateUtility.compare(schd.getSchDate(), finMain.getRecalFromDate()) >= 0 &&
						DateUtility.compare(schd.getSchDate(), finMain.getRecalToDate()) <= 0){

					balDisbAmount = balDisbAmount.subtract(adjOrgAmount);
					if(balDisbAmount.compareTo(adjOrgAmount) < 0){
						adjOrgAmount = balDisbAmount;
					}
					schd.setOrgEndBal(schd.getOrgEndBal().add(balDisbAmount));
					if(balDisbAmount.compareTo(BigDecimal.ZERO) == 0){
						break;
					}
				}
			}
		}

		Date recalToDate = finMain.getRecalToDate();

		// If recalculation type is TILL DATE and event to date is >= maturity
		// date then force it to TILLMDT
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLDATE)
				&& DateUtility.compare(recalToDate, finMain.getMaturityDate()) >= 0) {
			finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		}

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_STEPPOS)) {
			finScheduleData = maintainPOSStep(finScheduleData);
			finScheduleData.getFinanceMain().setScheduleMaintained(true);

			// Insurance Schedule Calculation
			finScheduleData = insuranceCalculation(finScheduleData);

			logger.debug("Leaving");
			return finScheduleData;
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// SET RECALCULATION ATTRIBUTES
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		sdSize = finSchdDetails.size();
		finMain.setCompareToExpected(false);

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJTERMS)) {

			finScheduleData = adjTerms(finScheduleData, true);

			// If error return error message
			if (finScheduleData.getErrorDetails().size() > 0) {
				orgFinScheduleData.setErrorDetails(finScheduleData.getErrorDetails());
				return orgFinScheduleData;
			}

			logger.debug("Leaving");
			return finScheduleData;
		} else {

			if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT)) {
				finMain.setRecalFromDate(finMain.getMaturityDate());
				finMain.setRecalToDate(finMain.getMaturityDate());
			}

			if (DateUtility.compare(finMain.getRecalFromDate(), finMain.getGrcPeriodEndDate()) <= 0
					&& DateUtility.compare(finMain.getRecalToDate(), finMain.getGrcPeriodEndDate()) > 0) {

				for (int i = 0; i < finSchdDetails.size(); i++) {
					if (DateUtility.compare(finSchdDetails.get(i).getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
						continue;
					}

					finMain.setRecalFromDate(finSchdDetails.get(i).getSchDate());
					break;

				}
			}

			finScheduleData = setRecalAttributes(finScheduleData, PROC_ADDDISBURSEMENT, newDisbAmount, BigDecimal.ZERO);
		}
		
		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, evtFromDate, newDisbAmount.add(newFeeAmt));
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);
		finMain = finScheduleData.getFinanceMain();
		finMain.setScheduleMaintained(true);

		// Insurance Schedule Calculation
		finScheduleData = insuranceCalculation(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}
	
	/**
	 * Method for Resetting Repayment Instructions for Developer Finance in case of Principal Holiday applied on Partial Settlement
	 * @param finScheduleData
	 * @param additionalAmount
	 * @return
	 */
	private FinScheduleData setOrgRpyInstructions(FinScheduleData finScheduleData, Date newDisbDate, BigDecimal additionalAmount){
		
		if(!finScheduleData.getFinanceMain().isDevFinCalReq()){
			return finScheduleData;
		}
		
		// If Developer Finance then RepayInstructions should be reset
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		boolean zeroInstrAdded = false;
		boolean newRpyInstReq = true;
		boolean priHolidayApplied = false;
		Date startFrom = null;
		for (int i = 1; i < finSchdDetails.size() - 1; i++) {
			
			FinanceScheduleDetail schd = finSchdDetails.get(i);

			// Adjust amounts between recalculation dates
			if(DateUtility.compare(schd.getSchDate(), finMain.getRecalFromDate()) >= 0){
				if((schd.getClosingBalance().add(additionalAmount).compareTo(schd.getOrgEndBal())) < 0){
					if(!zeroInstrAdded){
						zeroInstrAdded = true;
						startFrom = schd.getSchDate();
					}
				}else{
					FinanceScheduleDetail pSchd = finSchdDetails.get(i-1);
					if(zeroInstrAdded){
						finScheduleData = setRpyInstructDetails(finScheduleData, startFrom, 
								pSchd.getSchDate(), BigDecimal.ZERO, finMain.getScheduleMethod());
					}
					if(newRpyInstReq && priHolidayApplied){
						BigDecimal amount = pSchd.getClosingBalance().subtract(schd.getOrgEndBal());
						if(newDisbDate != null && DateUtility.compare(newDisbDate, pSchd.getSchDate()) != 0){
							amount = amount.add(additionalAmount);
						}
						finScheduleData = setRpyInstructDetails(finScheduleData, schd.getSchDate(), 
								schd.getSchDate(), amount, finMain.getScheduleMethod());
						
						if(i != finSchdDetails.size() - 1){
							finScheduleData.getFinanceMain().setRecalFromDate(finSchdDetails.get(i+1).getSchDate());
						}
						finScheduleData.getFinanceMain().setResetNxtRpyInstReq(true);
					}
					break;
				}
			}else{
				
				if(schd.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0){
					priHolidayApplied = true;
				}
				if(priHolidayApplied && !finMain.isResetNxtRpyInstReq()){
					if((schd.getClosingBalance().add(additionalAmount).compareTo(schd.getOrgEndBal())) == 0){
						newRpyInstReq = false;
					}
				}
			}
		}
		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * ======================================= Method : procAddTerm Description
	 * : ADD TERM Process : Add Term will add another term to the schedule
	 * details.
	 * =========================================================================
	 * ========================================
	 */

	private FinScheduleData procAddTerm(FinScheduleData orgFinScheduleData, int noOfTerms, boolean isRepayOnSchd) {
		logger.debug("Entering");

		FinScheduleData finScheduleData = null;
		Cloner cloner = new Cloner();
		finScheduleData = cloner.deepClone(orgFinScheduleData);

		FinanceMain finMain = finScheduleData.getFinanceMain();

		// Set the limits based on system values table
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtility.addYears(finMain.getFinStartDate(), maxFinYears);

		for (int i = 0; i < noOfTerms; i++) {
			finScheduleData = addOneTerm(finScheduleData, lastDateLimit, isRepayOnSchd);
			if (finScheduleData.getErrorDetails().size() > 0) {
				orgFinScheduleData.setErrorDetails(finScheduleData.getErrorDetails());
				return orgFinScheduleData;
			}
		}

		finScheduleData.getFinanceScheduleDetails().get(finScheduleData.getFinanceScheduleDetails().size() - 1)
				.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);

		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * =========================================================================
	 * ======================================= Method : procChangeProfit
	 * Description : CHANGE Profit Amount between two dates Process :
	 * =========================================================================
	 * =======================================
	 */

	private FinScheduleData procChangeProfit(FinScheduleData finScheduleData, BigDecimal desiredPftAmount) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		finMain.setRecalFromDate(evtFromDate);
		finMain.setRecalToDate(evtToDate);

		if (finMain.getRecalFromDate().before(finMain.getGrcPeriodEndDate())) {
			int size = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = 0; i < size; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if (curSchd.getSchDate().after(finMain.getGrcPeriodEndDate())) {
					finMain.setRecalFromDate(curSchd.getSchDate());
					break;
				}
			}

		}

		finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_SELECT, desiredPftAmount,
				null, null, false);

		// Advised Profit Rate Calculation Process
		finScheduleData = advPftRateCalculation(finScheduleData, finScheduleData.getFinanceMain().getEventFromDate(),
				finScheduleData.getFinanceMain().getEventToDate());

		// Calculate Effective Rate after Change profit
		Cloner cloner = new Cloner();
		FinScheduleData orgFinScheduleData = cloner.deepClone(finScheduleData);
		FinanceMain orgFinMain = orgFinScheduleData.getFinanceMain();

		// isProtectSchdPft = true; TODO check by pradeep, is it needed or not
		// SET START AND DATES FOR EFFECTIVE RATE CALCULATION
		orgFinMain.setEventFromDate(orgFinMain.getFinStartDate());
		orgFinMain.setEventToDate(orgFinMain.getMaturityDate());

		orgFinScheduleData = calEffectiveRate(orgFinScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
				orgFinMain.getTotalGrossPft(), orgFinMain.getFinStartDate(), orgFinMain.getMaturityDate(), true);

		finMain.setEffectiveRateOfReturn(orgFinScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate());

		setFinScheduleData(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * ======================================= Method : procRefreshRates
	 * Description :REFRESH RATES AND RECALCULATE SCHEDULE BASED ON REVIEW RATE
	 * APPLIED FOR
	 * =========================================================================
	 * =======================================
	 */

	private FinScheduleData procRefreshRates(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		if (!StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
			finMain.setCalculateRepay(true);
			Date recalFromDate = finMain.getRecalFromDate();
			Date recalToDate = finMain.getRecalToDate();
			String schdMethod = finMain.getScheduleMethod();

			List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

			for (int i = 0; i < repayInstructions.size(); i++) {

				schdMethod = repayInstructions.get(i).getRepaySchdMethod();
				if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), finMain.getRecalFromDate()) > 0) {
					break;
				}
			}

			finScheduleData = setRpyInstructDetails(finScheduleData, recalFromDate, recalToDate, BigDecimal.ONE,
					schdMethod);

		}

		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * #########################################################################
	 * #######################################
	 * 
	 * SUB METHODS
	 * 
	 * #########################################################################
	 * #######################################
	 */

	/*
	 * =========================================================================
	 * ======================================= PREPARE FinSchedule
	 * =========================================================================
	 * =======================================
	 */
	private FinScheduleData preapareFinSchdData(FinScheduleData finScheduleData, Boolean isCalFlat) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = finSchdDetails.get(0);

		if (StringUtils.isBlank(finMain.getGrcSchdMthd())) {
			finMain.setGrcSchdMthd(finMain.getScheduleMethod());
		}

		// Set Default scheduled date and schedule method first time
		for (int i = 0; i < finSchdDetails.size(); i++) {
			curSchd = finSchdDetails.get(i);
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
				finMain.setGraceTerms(i);
			}
		}

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

		if (finMain.isAllowGrcPeriod() && finMain.isAllowGrcRepay()) {
			finScheduleData = setRpyInstructDetails(finScheduleData, finMain.getNextGrcPftDate(),
					finMain.getMaturityDate(), BigDecimal.ZERO, finMain.getGrcSchdMthd());
		}

		finMain.setRecalSchdMethod(finMain.getScheduleMethod());
		finScheduleData = setRpyInstructDetails(finScheduleData, finMain.getNextRepayPftDate(),
				finMain.getMaturityDate(), BigDecimal.ZERO, finMain.getScheduleMethod());

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : adjTerms Description : Adjust Terms Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData adjTerms(FinScheduleData finScheduleData, boolean isSetRepay) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		// Get Adjustment Limits parameters from system values table
		BigDecimal lastTermPercent = new BigDecimal(SysParamUtil.getValueAsString("ADJTERM_LASTTERM_PERCENT"));
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");

		// Set the limits based on system values table
		BigDecimal lastTermLimit = BigDecimal.valueOf(0.0);
		Date lastDateLimit = new Date();

		lastDateLimit = DateUtility.addYears(finMain.getFinStartDate(), maxFinYears);

		String schdMethod = "";
		int iLast = finScheduleData.getRepayInstructions().size() - 1;
		lastTermLimit = finScheduleData.getRepayInstructions().get(iLast).getRepayAmount();
		lastTermLimit = lastTermLimit.multiply(lastTermPercent);
		lastTermLimit = round(lastTermLimit);
		lastTermLimit = finScheduleData.getRepayInstructions().get(iLast).getRepayAmount().add(lastTermLimit);
		schdMethod = finScheduleData.getRepayInstructions().get(iLast).getRepaySchdMethod();

		// Developer Finance Principal Holiday Repayment Instructions Recalculation
		if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()){
			finScheduleData = setOrgRpyInstructions(finScheduleData, null, BigDecimal.ZERO);
		}

		// Calculate Schedule
		finScheduleData = calSchdProcess(finScheduleData, false, false);

		iLast = finSchdDetails.size() - 1;

		// If The calculated schedule last repayment is under limit then no need
		// to adjust terms
		if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
			if (finSchdDetails.get(iLast).getRepayAmount().compareTo(lastTermLimit) <= 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}
		} else {
			if (finSchdDetails.get(iLast).getPrincipalSchd().compareTo(lastTermLimit) <= 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}
		}

		// Adjust Terms
		while (true) {
			
			if(finScheduleData.getFinanceMain().getRemBalForAdj().compareTo(finScheduleData.getFinanceMain().getAdjOrgBal()) < 0){
				finScheduleData.getFinanceMain().setAdjOrgBal(finScheduleData.getFinanceMain().getRemBalForAdj());
			}
			
			finScheduleData = addOneTerm(finScheduleData, lastDateLimit, isSetRepay);
			finScheduleData.getFinanceMain().setRemBalForAdj(finScheduleData.getFinanceMain().getRemBalForAdj().subtract(
					finScheduleData.getFinanceMain().getAdjOrgBal())); 

			if (finScheduleData.getErrorDetails().size() > 0) {
				return finScheduleData;
			}

			// Calculate Schedule
			finScheduleData = calSchdProcess(finScheduleData, false, false);

			iLast = iLast + 1;

			if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
				if (finSchdDetails.get(iLast).getRepayAmount().compareTo(lastTermLimit) <= 0) {
					break;
				}
			} else {
				if (finSchdDetails.get(iLast).getPrincipalSchd().compareTo(lastTermLimit) <= 0) {
					break;
				}
			}
		}

		finSchdDetails.get(finSchdDetails.size() - 1).setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : addOneTerm Description : ADD One Terms Process : Add Term will
	 * add another term to the schedule details.
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData addOneTerm(FinScheduleData finScheduleData, Date lastDateLimit,boolean isSetRepay) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		// Maturity Record Index
		int iMaturity = finSchdDetails.size() - 1;

		// Set After which index new term should be added
		int iAddTermsAfter = iMaturity;

		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finSchdDetails.get(iAddTermsAfter);

		Date curSchdDate = curSchd.getSchDate();

		// get next Repayment Date
		Date nextSchdDate = DateUtility
				.getDate(
						DateUtility.formatUtilDate(
								FrequencyUtil.getNextDate(finMain.getRepayFrq(), 1, curSchdDate,
										HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
								PennantConstants.dateFormat));

		if (nextSchdDate.after(lastDateLimit)) {
			// Through Error
			finScheduleData.setErrorDetail(new ErrorDetail("SCH30",
					"ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS. NOT ALLOWED TO ADD MORE TERMS.",
					new String[] { " " }));
			return finScheduleData;
		}

		// Find whether record with next schedule date already exist in schedule
		// details
		boolean isSchdExist = false;
		for (int i = iMaturity; i >= 0; i--) {
			if (finSchdDetails.get(i).getSchDate().equals(nextSchdDate)) {
				isSchdExist = true;
				finSchdDetails.get(i).setRepayOnSchDate(isSetRepay);
				break;
			}

			if (finSchdDetails.get(i).getSchDate().after(nextSchdDate)) {
				isSchdExist = false;
				break;
			}
		}

		// Reset New Schedule Record if record not found
		if (!isSchdExist) {
			finScheduleData = resetNewSchdDetail(finScheduleData, nextSchdDate, CalculationConstants.SCHDFLAG_RPY);
			
			int schSize = finScheduleData.getFinanceScheduleDetails().size();
			FinanceScheduleDetail lastSchd = finScheduleData.getFinanceScheduleDetails().get(schSize - 1);
			lastSchd.setOrgEndBal(lastSchd.getOrgEndBal().add(finMain.getAdjOrgBal()));
			
			finMain.setNumberOfTerms(finMain.getNumberOfTerms() + 1);
			finMain.setCalTerms(finMain.getCalTerms() + 1);
			finMain.setMaturityDate(nextSchdDate);
			finMain.setCalMaturity(nextSchdDate);
		}

		// Set Profit dates between current schedule and next repayment

		finScheduleData = setOtherSchdDates(finScheduleData, finMain.getRepayPftFrq(), curSchdDate,
				CalculationConstants.SCHDFLAG_PFT);

		// Set Profit review dates between current schedule and next repayment
		if (finMain.isAllowRepayRvw()) {
			finScheduleData = setOtherSchdDates(finScheduleData, finMain.getRepayRvwFrq(), curSchdDate,
					CalculationConstants.SCHDFLAG_RVW);
		}

		// Set Capitalization dates between current schedule and next repayment
		if (finMain.isAllowRepayCpz()) {
			finScheduleData = setOtherSchdDates(finScheduleData, finMain.getRepayCpzFrq(), curSchdDate,
					CalculationConstants.SCHDFLAG_CPZ);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : resetNewSchdDetail Description : Reset Schedule Detail Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData resetNewSchdDetail(FinScheduleData finScheduleData, Date nextSchdDate, int scheduleFlag) {
		logger.debug("Entering");

		// Set Next Repayment Date
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail sd = new FinanceScheduleDetail();

		int lastIndex = finScheduleData.getFinanceScheduleDetails().size() - 1;
		FinanceScheduleDetail lastSchd = finScheduleData.getFinanceScheduleDetails().get(lastIndex);

		sd.setFinReference(finScheduleData.getFinanceMain().getFinReference());
		sd.setSchDate(nextSchdDate);
		sd.setDefSchdDate(nextSchdDate);

		// Grace Period Flags Setting
		if (DateUtility.compare(nextSchdDate, finMain.getGrcPeriodEndDate()) <= 0) {

			// Set Profit On date based on frequency
			if (FrequencyUtil.isFrqDate(finMain.getGrcPftFrq(), nextSchdDate)) {
				sd.setPftOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				sd.setPftOnSchDate(false);
			}

			// Review Date
			if (finMain.isAllowGrcPftRvw() && FrequencyUtil.isFrqDate(finMain.getGrcPftRvwFrq(), nextSchdDate)) {
				sd.setRvwOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				sd.setRvwOnSchDate(false);
			}

			// Set Capitalize On date based on frequency
			if (FrequencyUtil.isFrqDate(finMain.getGrcCpzFrq(), nextSchdDate)) {
				sd.setCpzOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				sd.setCpzOnSchDate(false);
			}

			// Repay on Schedule Date
			sd.setRepayOnSchDate(false);

		}

		// Repayment Period Flags Setting
		else {

			// Set Profit On date based on frequency
			if (FrequencyUtil.isFrqDate(finMain.getRepayPftFrq(), nextSchdDate)) {
				if (finMain.isFinRepayPftOnFrq()) {
					sd.setPftOnSchDate(true);
					sd.setFrqDate(true);
				} else {
					sd.setPftOnSchDate(false);
				}

			} else {
				sd.setPftOnSchDate(false);
			}

			// Review Date
			if (finMain.isAllowRepayRvw() && FrequencyUtil.isFrqDate(finMain.getRepayRvwFrq(), nextSchdDate)) {
				sd.setRvwOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				sd.setRvwOnSchDate(false);
			}

			// Set Capitalize On date based on frequency
			if (FrequencyUtil.isFrqDate(finMain.getRepayCpzFrq(), nextSchdDate)) {
				sd.setCpzOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				sd.setCpzOnSchDate(false);
			}

			// Repay on Schedule Date
			if (scheduleFlag == CalculationConstants.SCHDFLAG_RPY) {
				sd.setRepayOnSchDate(true);
			} else {
				if (FrequencyUtil.isFrqDate(finMain.getRepayFrq(), nextSchdDate)) {
					sd.setRepayOnSchDate(true);
					sd.setFrqDate(true);
				} else {
					sd.setRepayOnSchDate(false);
				}
			}
		}

		sd.setBaseRate(lastSchd.getBaseRate());
		sd.setSplRate(lastSchd.getSplRate());
		sd.setMrgRate(lastSchd.getMrgRate());
		sd.setActRate(lastSchd.getActRate());
		sd.setCalculatedRate(lastSchd.getCalculatedRate());

		int iRepay = finScheduleData.getRepayInstructions().size();
		String schdMethod = finScheduleData.getFinanceMain().getScheduleMethod();
		if (iRepay > 0) {
			schdMethod = finScheduleData.getRepayInstructions().get(iRepay - 1).getRepaySchdMethod();
		}
		sd.setSchdMethod(schdMethod);
		sd.setPftDaysBasis(lastSchd.getPftDaysBasis());
		sd.setAdvBaseRate(lastSchd.getAdvBaseRate());
		sd.setAdvMargin(lastSchd.getAdvMargin());
		sd.setAdvPftRate(lastSchd.getAdvPftRate());
		sd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);

		finScheduleData.getFinanceScheduleDetails().add(sd);
		finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setOtherSchdDates Description : Reset Other Schedule dates than
	 * repay dates Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setOtherSchdDates(FinScheduleData finScheduleData, String frequency, Date curStartDate,
			int scheduleFlag) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		while (true) {
			// Get Next Schedule Date

			Date nextSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(
					FrequencyUtil.getNextDate(frequency, 1, curStartDate, HolidayHandlerTypes.MOVE_NONE, false)
							.getNextFrequencyDate(),
					PennantConstants.dateFormat));

			int sdSize = finSchdDetails.size();
			curSchd = finSchdDetails.get(sdSize - 1);

			// Next Schedule Date is after last repayment date
			if (nextSchdDate.after(finScheduleData.getFinanceMain().getCalMaturity())) {
				break;
			}

			for (int i = sdSize - 1; i > 0; i--) {
				curSchd = finSchdDetails.get(i);

				if (nextSchdDate.equals(curSchd.getSchDate())) {

					curSchd = setcurSchdFlags(curSchd, scheduleFlag);
					finSchdDetails.set(i, curSchd);
					curStartDate = nextSchdDate;
					break;
				} else {
					if (nextSchdDate.after(curSchd.getSchDate()) && DateUtility.compare(nextSchdDate,
							finScheduleData.getFinanceMain().getCalMaturity()) <= 0) {
						// Set Schedule Dates in between Previous schedule (Cur
						// Schedule and
						// Last Repayment Date)
						finScheduleData = resetNewSchdDetail(finScheduleData, nextSchdDate, scheduleFlag);
						curStartDate = nextSchdDate;
						break;
					}
				}
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	// TODO: The below method to be reviewd and modified if required. New Method
	// resetCurSchdFlags added
	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setcurSchdFlags Description : R Process :
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

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setRpyInstructDetails Description : Set Repay Instruction
	 * Details Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setRpyInstructDetails(FinScheduleData finScheduleData, Date fromDate, Date toDate,
			BigDecimal repayAmount, String schdMethod) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		BigDecimal nextInstructAmount = BigDecimal.ZERO;
		Date nextInstructDate = null;
		String nextInstructSchdMethod = null;

		boolean isAddNewInstruction = true;
		int instructIndex = -1;
		FinanceMain finMain = finScheduleData.getFinanceMain();

		// Find next date for instruction
		if (DateUtility.compare(toDate, finMain.getMaturityDate()) >= 0) {
			nextInstructDate = finMain.getMaturityDate();
		} else {
			int sdSize = finSchdDetails.size();
			FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

			for (int i = 0; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);

				if (curSchd.getSchDate().after(toDate) && (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())) {
					nextInstructDate = curSchd.getSchDate();
					nextInstructSchdMethod = curSchd.getSchdMethod();
					break;
				}
			}
			// Next instruction amount and schedule method
			sortRepayInstructions(finScheduleData.getRepayInstructions());
			if(nextInstructDate != null){
				instructIndex = fetchRpyInstruction(finScheduleData, nextInstructDate);
			}

			if (instructIndex >= 0) {
				nextInstructAmount = finScheduleData.getRepayInstructions().get(instructIndex).getRepayAmount();
				nextInstructSchdMethod = finScheduleData.getRepayInstructions().get(instructIndex).getRepaySchdMethod();
			}
		}

		RepayInstruction curInstruction = new RepayInstruction();

		// Remove any instructions between fromdate and todate
		for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {
			curInstruction = finScheduleData.getRepayInstructions().get(i);

			if (DateUtility.compare(curInstruction.getRepayDate(), fromDate) >= 0
					&& DateUtility.compare(curInstruction.getRepayDate(), toDate) <= 0) {
				// finScheduleData.getRepayInstructions().remove(curInstruction);
				finScheduleData.getRepayInstructions().remove(i);
				i = i - 1;
			}

			if (DateUtility.compare(curInstruction.getRepayDate(), nextInstructDate) == 0) {
				isAddNewInstruction = false;
			}
		}

		finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData.getRepayInstructions()));

		// Add repay instructions on from date
		RepayInstruction ri = new RepayInstruction();
		ri.setRepayDate(fromDate);
		ri.setRepayAmount(repayAmount);
		ri.setRepaySchdMethod(schdMethod);

		finScheduleData.getRepayInstructions().add(ri);

		// Add (reset) repay instruction after todate
		if (DateUtility.compare(toDate, finMain.getMaturityDate()) >= 0 || !isAddNewInstruction) {
			finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData.getRepayInstructions()));
			return finScheduleData;
		}

		if (DateUtility.compare(nextInstructDate, fromDate) > 0) {
			ri = new RepayInstruction();
			ri.setRepayDate(nextInstructDate);
			ri.setRepayAmount(nextInstructAmount);
			ri.setRepaySchdMethod(nextInstructSchdMethod);
			finScheduleData.getRepayInstructions().add(ri);
		}

		finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData.getRepayInstructions()));

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : getRpyInstructDetails Description : Get Repay Instruction
	 * Details Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData getRpyInstructDetails(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		int riSize = finScheduleData.getRepayInstructions().size();
		BigDecimal instructAmount = BigDecimal.ZERO;

		FinanceMain finMain = finScheduleData.getFinanceMain();

		Date fromDate = finMain.getFinStartDate();
		Date toDate = finMain.getMaturityDate();
		String fromSchdMethod = null;
		String toSchdMethod = null;

		finMain.setIndexStart(0);

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

			finScheduleData = setRpyChanges(finScheduleData, fromDate, toDate, instructAmount, fromSchdMethod);
			fromDate = toDate;
			fromSchdMethod = toSchdMethod;

			instructAmount = curInstruction.getRepayAmount();
		}

		if (DateUtility.compare(toDate, finMain.getMaturityDate()) <= 0) {
			toDate = finMain.getMaturityDate();
			setRpyChanges(finScheduleData, fromDate, toDate, instructAmount, fromSchdMethod);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : fetchRpyInstruction Description : Fetch Repay Instruction index
	 * by date Process :
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

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setRpyChanges Description : Set Repay Changes Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setRpyChanges(FinScheduleData finScheduleData, Date fromDate, Date toDate,
			BigDecimal instructAmount, String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		int indexStart = finMain.getIndexStart();

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		for (int i = indexStart; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			Date curSchdDate = curSchd.getSchDate();

			// Added for setting Schedule method in case of Different
			// frequencies for PFT,CPZ & RVW
			if (DateUtility.compare(curSchdDate, fromDate) < 0) {
				if (StringUtils.isEmpty(curSchd.getSchdMethod())) {
					if (finMain.isAllowGrcPeriod()
							&& DateUtility.compare(curSchdDate, finMain.getGrcPeriodEndDate()) <= 0) {
						curSchd.setSchdMethod(finMain.getGrcSchdMthd());
					} else {
						curSchd.setSchdMethod(finMain.getScheduleMethod());
					}
				}
			}

			if (DateUtility.compare(curSchdDate, fromDate) >= 0
					&& (curSchdDate.before(toDate) || (finMain.getNextRolloverDate() != null
							&& DateUtility.compare(curSchd.getSchDate(), finMain.getNextRolloverDate()) == 0))) {

				curSchd.setSchdMethod(schdMethod);

				if (curSchd.isRepayOnSchDate()) {
					if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
						curSchd.setRepayAmount(instructAmount);
					} else if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)) {
						curSchd.setRepayAmount(BigDecimal.ZERO);
					} else {
						curSchd.setPrincipalSchd(instructAmount);
					}
				}

			} else if (DateUtility.compare(curSchd.getSchDate(), toDate) >= 0) {

				indexStart = i;
				break;
			}
		}

		finMain.setIndexStart(indexStart);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setFinanceTotals Description: Set Finance Totals after Grace and
	 * Repayment schedules calculation
	 * ________________________________________________________________________________________________________________
	 */
	private FinScheduleData setFinanceTotals(FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finScheduleDetails = finScheduleData.getFinanceScheduleDetails();

		// FIXME: PV: 13MAY17: It is kept on the assumption reqMaturity fields
		// in not used any where else
		if (finMain.isNew() || StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			finMain.setReqMaturity(finMain.getCalMaturity());
		}

		FeeScheduleCalculator.feeSchdBuild(finScheduleData);

		boolean isFirstAdjSet = false;
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		finMain.setTotalGraceCpz(BigDecimal.ZERO);
		finMain.setTotalGracePft(BigDecimal.ZERO);
		finMain.setTotalGrossGrcPft(BigDecimal.ZERO);
		finMain.setTotalCpz(BigDecimal.ZERO);
		finMain.setTotalProfit(BigDecimal.ZERO);
		finMain.setTotalGrossPft(BigDecimal.ZERO);
		finMain.setTotalRepayAmt(BigDecimal.ZERO);
		finMain.setSchdIndex(0);
		finMain.setAdjOrgBal(BigDecimal.ZERO);
		finMain.setRemBalForAdj(BigDecimal.ZERO);
		finMain.setDevFinCalReq(true);

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		Date schdDate = new Date();
		int instNumber = 0;

		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);

		for (int i = 0; i < sdSize; i++) {
			curSchd = finScheduleDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (finMain.isTDSApplicable() && tdsPerc.compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal tdsAmount = (curSchd.getProfitSchd().multiply(tdsPerc)).divide(new BigDecimal(100), 0,
						RoundingMode.HALF_DOWN);
				CalculationUtil.roundAmount(tdsAmount, tdsRoundMode, tdsRoundingTarget);
				curSchd.setTDSAmount(tdsAmount);
			}

			if (i == 0) {
				curSchd.setSchdMethod(finScheduleDetails.get(i + 1).getSchdMethod());
			}

			if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
				finMain.setBpiAmount(curSchd.getProfitSchd());
			}

			if (DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) <= 0) {
				finMain.setTotalGraceCpz(finMain.getTotalGraceCpz().add(curSchd.getCpzAmount()));
				finMain.setTotalGracePft(finMain.getTotalGracePft().add(curSchd.getProfitSchd()));
			}

			finMain.setTotalCpz(finMain.getTotalCpz().add(curSchd.getCpzAmount()));
			finMain.setTotalProfit(finMain.getTotalProfit().add(curSchd.getProfitSchd()));
			finMain.setTotalGrossPft(finMain.getTotalGrossPft().add(curSchd.getProfitSchd()));
			finMain.setTotalRepayAmt(finMain.getTotalRepayAmt().add(curSchd.getRepayAmount()));

			if (curSchd.isRepayOnSchDate()) {
				if (!isFirstAdjSet) {
					finMain.setFirstRepay(curSchd.getRepayAmount());
					isFirstAdjSet = true;
				}

				finMain.setLastRepay(curSchd.getRepayAmount());
			}

			if ((curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())
					&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				instNumber = instNumber + 1;
				curSchd.setInstNumber(instNumber);
			} else {
				curSchd.setInstNumber(0);
			}

			if (i == (sdSize - 1)) {
				curSchd.setSchdMethod(finScheduleDetails.get(i - 1).getSchdMethod());
			}

			if (DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) < 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
			} else if (DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) == 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
			} else if (DateUtility.compare(schdDate, finMain.getMaturityDate()) < 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
			} else if (DateUtility.compare(schdDate, finMain.getMaturityDate()) == 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
			}
			
			// Reset Original Balance for Developer Finance
			if(finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance() 
					&& finMain.isResetOrgBal()){
				if(DateUtility.compare(schdDate, finMain.getRecalFromDate()) >= 0){
					curSchd.setOrgEndBal(curSchd.getClosingBalance());
				}
			}
		}

		finMain.setTotalGrossGrcPft(finMain.getTotalGraceCpz().add(finMain.getTotalGracePft()));
		finMain.setTotalGrossPft(finMain.getTotalProfit().add(finMain.getTotalCpz()));

		// Effective Rate Of Return Calculations / XIRR && IRR
		// ####_ 0.2
		List<IRRFinanceType> irrFinanceTypes = getIrrFinanceTypeDAO().getIRRFinanceTypeByFinType(finMain.getFinType(),
				"_AView");
		finScheduleData.setiRRDetails(new ArrayList<FinIRRDetails>()); // reseting list object.
		
		//FIXME: PV: To avoid error
		if (irrFinanceTypes.isEmpty()) {
			//calculateXIRRAndIRR(finScheduleData, finMain, null);
		} else {
			for (int j = 0; j < irrFinanceTypes.size(); j++) {
				calculateXIRRAndIRR(finScheduleData, finMain, irrFinanceTypes.get(j));
			}
		}
		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * 
	 * @param finSchdData
	 * @param finMain
	 * @return
	 */
	// ####_ 0.2
	private void calculateXIRRAndIRR(FinScheduleData finSchdData, FinanceMain finMain, IRRFinanceType irrFinType) {
		logger.debug("Entering");

		//BigDecimal cal_IRR = BigDecimal.ZERO;
		BigDecimal cal_XIRR = BigDecimal.ZERO;
		BigDecimal cal_XIRR_WithFee = BigDecimal.ZERO;
		BigDecimal calcAmount = BigDecimal.ZERO;

		List<BigDecimal> schAmountList = new ArrayList<BigDecimal>(1);
		List<Date> repayDateList = new ArrayList<Date>(1);
		List<BigDecimal> schAmountListWithFee = new ArrayList<BigDecimal>(1);
		List<FinFeeDetail> finFeeDList = finSchdData.getFinFeeDetailList();

		BigDecimal feeAmount = BigDecimal.ZERO;
		List<IRRFeeType> irrFeeList = null;
		if (irrFinType != null) {
			irrFeeList = getiRRFeeTypeDAO().getIRRFeeTypeList(irrFinType.getIRRID(), "");
		}

		for (FinFeeDetail fee : finFeeDList) {
			if (!StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT, fee.getFeeScheduleMethod())
					&& !StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR, fee.getFeeScheduleMethod())
					&& !StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS,
							fee.getFeeScheduleMethod())
					&& !StringUtils.equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE,
							fee.getFeeScheduleMethod())) {

				BigDecimal feePerc = BigDecimal.ZERO;
				boolean isRcdFound = false;
				if (irrFeeList != null) {
					for (IRRFeeType irrFeeType : irrFeeList) {
						if (irrFeeType.getFeeTypeID() == fee.getFeeTypeID()) {
							feePerc = irrFeeType.getFeePercentage();
							isRcdFound = true;
						}
					}
				}
				BigDecimal calFeeAmount = BigDecimal.ZERO;
				if (isRcdFound) {
					calFeeAmount = fee.getActualAmount().subtract(fee.getWaivedAmount());
					calFeeAmount = (calFeeAmount.multiply(feePerc)).divide(new BigDecimal(100), 0,
							RoundingMode.HALF_DOWN);
					feeAmount = feeAmount.add(calFeeAmount);
				} else {
					feeAmount = feeAmount.add(fee.getActualAmount().subtract(fee.getWaivedAmount()));
				}
			}
		}

		//FIXME CH Servicing Fees should be handled
		for (FinanceScheduleDetail finScheduleDetail : finSchdData.getFinanceScheduleDetails()) {

			if (finScheduleDetail.isDisbOnSchDate()) {
				repayDateList.add(finScheduleDetail.getSchDate());

				calcAmount = finScheduleDetail.getDisbAmount().subtract(finScheduleDetail.getDownPaymentAmount());
				calcAmount = calcAmount.multiply(new BigDecimal(-1));
				schAmountList.add(calcAmount);

				if (DateUtility.compare(finScheduleDetail.getSchDate(), finMain.getFinStartDate()) == 0) {
					calcAmount = calcAmount.add(feeAmount);
				}

				schAmountListWithFee.add(calcAmount);
			}

			if (finScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
				schAmountList.add(finScheduleDetail.getRepayAmount());
				schAmountListWithFee.add(finScheduleDetail.getRepayAmount().add(finScheduleDetail.getFeeSchd()));
				repayDateList.add(finScheduleDetail.getSchDate());
			}
		}
		/*
		 * cal_IRR = RateCalculation.calculateIRR(schAmountList); int termsPerYear =
		 * CalculationUtil.getTermsPerYear(finMain.getRepayPftFrq()); calculated_IRR = calculated_IRR.multiply(new
		 * BigDecimal(termsPerYear));
		 */

		cal_XIRR = RateCalculation.calculateXIRR(schAmountList, repayDateList);
		cal_XIRR_WithFee = RateCalculation.calculateXIRR(schAmountListWithFee, repayDateList);

		finMain.setAnualizedPercRate(cal_XIRR.setScale(9));
		finMain.setEffectiveRateOfReturn(cal_XIRR_WithFee.setScale(9));

		if (irrFinType != null) {
			FinIRRDetails irr = new FinIRRDetails();
			irr.setiRRID(irrFinType.getIRRID());
			irr.setiRRCode(irrFinType.getIrrCode());
			irr.setIrrCodeDesc(irrFinType.getIrrCodeDesc());
			irr.setIRR(cal_XIRR_WithFee.setScale(9));
			finSchdData.getiRRDetails().add(irr);
		}
		logger.debug("Leaving");
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : calSchdProcess
	 * Description : Calculate Schedule Process Process :
	 * *************************************************************************
	 * ***************************************
	 * 
	 * /** To Calculate the Amounts for given schedule
	 */
	private FinScheduleData calSchdProcess(FinScheduleData finScheduleData, Boolean isCalFlat, boolean isFirstRun) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		// START PROCESS
		finScheduleData = fetchRatesHistory(finScheduleData);
		finScheduleData = fetchGraceCurRates(finScheduleData);
		finScheduleData = fetchRepayCurRates(finScheduleData);

		finScheduleData = getRpyInstructDetails(finScheduleData);

		/* Grace Schedule calculation */
		finScheduleData = graceSchdCal(finScheduleData);

		if (isFirstRun) {
			finScheduleData = prepareFirstSchdCal(finScheduleData);

			if (finMain.isStepFinance()) {
				finScheduleData = adjustBPISchd(finScheduleData);
				finScheduleData = setFinanceTotals(finScheduleData);
				logger.debug("Leaving");
				return finScheduleData;
			}

			finScheduleData = getRpyInstructDetails(finScheduleData);
		}

		finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

		if (finMain.isEqualRepay() && finMain.isCalculateRepay()
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)) {
			finScheduleData = calEqualPayment(finScheduleData);
			// equalRepayCal(finScheduleData);
		}

		finScheduleData = adjustBPISchd(finScheduleData);
		finScheduleData = setFinanceTotals(finScheduleData);

		finScheduleData.getFinanceScheduleDetails().get(0)
				.setSchdMethod(finScheduleData.getFinanceScheduleDetails().get(1).getSchdMethod());

		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * *************************************************************************
	 * *************************************** Method : fetchGraceCurRates
	 * Description : SET CURRENT GRACE RATES Process :
	 * *************************************************************************
	 * ***************************************
	 */
	private FinScheduleData fetchGraceCurRates(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		Date dateAllowedChange = finMain.getLastRepayRvwDate();

		if (DateUtility.compare(dateAllowedChange, finMain.getGrcPeriodEndDate()) >= 0) {
			return finScheduleData;
		}

		// FIND ALLOWED RATE CHANGE DATE
		String rvwRateApplFor = finMain.getRvwRateApplFor();
		if (StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)) {
			dateAllowedChange = findAllowedChangeDate(finScheduleData, rvwRateApplFor, dateAllowedChange);
		}

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = finSchdDetails.size();

		// Set Rates from Allowed Date and Grace Period End Date
		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			finMain.setSchdIndex(i);

			if (curSchd.getSchDate().before(dateAllowedChange)) {
				continue;
			}

			if (DateUtility.compare(curSchd.getSchDate(), finMain.getEventFromDate()) < 0) {
				continue;
			}

			if (DateUtility.compare(curSchd.getSchDate(), finMain.getEventToDate()) > 0) {
				break;
			}

			if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) >= 0) {
				break;
			}

			// Fetch current rates from DB
			if (StringUtils.isNotEmpty(curSchd.getBaseRate())) {
				if (curSchd.isRvwOnSchDate() || i == 0
						|| (StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)
								&& DateUtility.compare(curSchd.getSchDate(), dateAllowedChange) == 0)) {
					curSchd.setCalculatedRate(RateUtil.ratesFromLoadedData(finScheduleData, i));
				} else {
					curSchd.setCalculatedRate(finSchdDetails.get(i - 1).getCalculatedRate());
				}

				curSchd.setCalOnIndRate(false);
			}

		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : fetchRepayCurRates
	 * Description : SET CURRENT REPAY RATES Process :
	 * *************************************************************************
	 * ***************************************
	 */
	private FinScheduleData fetchRepayCurRates(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		Date dateAllowedChange = finMain.getLastRepayRvwDate();

		// PROFIT LAST REVIEW IS ON OR AFTER MATURITY THEN NOT ALLOWED THEN DO
		// NOT SET
		if (DateUtility.compare(dateAllowedChange, finMain.getMaturityDate()) >= 0) {
			return finScheduleData;
		}

		// FIND ALLOWED RATE CHANGE DATE
		String rvwRateApplFor = finMain.getRvwRateApplFor();
		if (StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)) {
			dateAllowedChange = findAllowedChangeDate(finScheduleData, rvwRateApplFor, dateAllowedChange);
		}

		int schdIndex = finMain.getSchdIndex();
		int sdSize = finSchdDetails.size();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		// Set Rates from Allowed Date and Maturity
		for (int i = schdIndex; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);

			if (curSchd.getSchDate().before(dateAllowedChange)) {
				continue;
			}

			// TODO: 27JUN17: PV After Successful unit test cases execution and
			// various normal tests, it can be removed.
			/*
			 * if (curSchd.getSchDate().compareTo(finMain.getEventFromDate()) <
			 * 0) { continue; }
			 */
			/*
			 * if (curSchd.getSchDate().compareTo(finMain.getEventToDate()) > 0)
			 * { break; }
			 */
			// Fetch current rates from DB
			if (StringUtils.isNotEmpty(curSchd.getBaseRate())) {
				if (curSchd.isRvwOnSchDate() || i == 0
						|| DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) == 0
						|| (StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)
								&& DateUtility.compare(curSchd.getSchDate(), dateAllowedChange) == 0)) {
					curSchd.setCalculatedRate(RateUtil.ratesFromLoadedData(finScheduleData, i));
				} else {
					curSchd.setCalculatedRate(finSchdDetails.get(i - 1).getCalculatedRate());
				}
				curSchd.setCalOnIndRate(false);
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : findAllowedChangeDate
	 * Description : FIND DATE FROM WHICH RATE CHANGE IS ALLOWED Process :
	 * ***********************************************
	 * *****************************************************************
	 */

	private Date findAllowedChangeDate(FinScheduleData finScheduleData, String rvwRateApplFor, Date dateAllowedChange) {
		logger.debug("Entering");

		int size = finScheduleData.getFinanceScheduleDetails().size();
		for (int i = 0; i < size; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (DateUtility.compare(curSchd.getSchDate(), dateAllowedChange) <= 0) {
				continue;
			}

			if (curSchd.isSchPftPaid() && curSchd.isSchPriPaid()) {
				dateAllowedChange = curSchd.getSchDate();
			} else {
				break;
			}
		}

		logger.debug("Leaving");
		return dateAllowedChange;
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : graceSchdCal Description
	 * : Grace period schedule calculation for reducing rate Process :
	 * ***************************************************
	 * *************************************************************
	 */

	private FinScheduleData graceSchdCal(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();

		finMain.setPftForSelectedPeriod(BigDecimal.ZERO);
		finMain.setSchdIndex(0);

		int sdSize = schdDetails.size();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		/* Loop through grace period schedule */
		for (int i = 0; i < sdSize; i++) {
			curSchd = schdDetails.get(i);

			// If first record no calculation is required
			if (i == 0) {
				prepareFirstGraceRcd(finScheduleData);
			} else {
				prepareRemainingGraceRcd(finScheduleData, i);
			}

			if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) == 0) {
				finMain.setSchdIndex(i);
				break;
			}

			finMain.setSchdIndex(i);
		}

		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * *************************************************************************
	 * *************************************** Method : repaySchdCal Description
	 * : Repay period schedule calculation for reducing rate Process :
	 * ***************************************************
	 * ************************************************************
	 */

	private FinScheduleData prepareFirstGraceRcd(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(0);

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
		curSchd.setClosingBalance(
				curSchd.getDisbAmount().add(curSchd.getFeeChargeAmt()).subtract(curSchd.getDownPaymentAmount()));
		curSchd.setRvwOnSchDate(true);

		curSchd.setPftDaysBasis(finMain.getProfitDaysBasis());

		// NOT Discount Deal
		if (!CalculationConstants.RATE_BASIS_D.equals(finMain.getRepayRateBasis())) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		// Discount Deal Total profit Calculation in FLAT

		BigDecimal grcTotalPft = BigDecimal.ZERO;
		BigDecimal calIntFraction = BigDecimal.ZERO;

		if (finMain.isAllowGrcPeriod()) {
			grcTotalPft = CalculationUtil.calInterest(finMain.getFinStartDate(), finMain.getGrcPeriodEndDate(),
					curSchd.getClosingBalance(), finMain.getRepayRateBasis(), finMain.getGrcPftRate());

			calIntFraction = grcTotalPft.subtract(round(grcTotalPft));
			grcTotalPft = round(grcTotalPft);
		}

		BigDecimal rpyTotalPft = CalculationUtil.calInterest(finMain.getGrcPeriodEndDate(),
				DateUtility.addDays(finMain.getMaturityDate(), 1), curSchd.getClosingBalance(),
				finMain.getRepayRateBasis(), finMain.getRepayProfitRate());

		rpyTotalPft = rpyTotalPft.add(calIntFraction);
		calIntFraction = rpyTotalPft.subtract(round(rpyTotalPft));
		rpyTotalPft = round(rpyTotalPft);

		curSchd.setClosingBalance(curSchd.getClosingBalance().add(grcTotalPft).add(rpyTotalPft));

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : repaySchdCal Description
	 * : Repay period schedule calculation for reducing rate Process :
	 * ***************************************************
	 * ************************************************************
	 */

	private FinScheduleData prepareRemainingGraceRcd(FinScheduleData finScheduleData, int iCur) {
		logger.debug("Entering");
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(iCur);
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(iCur - 1);

		if (!finMain.getGrcRateBasis().equals(CalculationConstants.RATE_BASIS_F)) {
			curSchd.setBalanceForPftCal(prvSchd.getClosingBalance());
		} else {
			curSchd.setBalanceForPftCal(prvSchd.getBalanceForPftCal().add(prvSchd.getDisbAmount())
					.subtract(prvSchd.getDownPaymentAmount()).add(prvSchd.getFeeChargeAmt()).add(prvSchd.getCpzAmount())
					.subtract(prvSchd.getPrincipalSchd()));
		}

		curSchd.setNoOfDays(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchd.getSchDate()));
		curSchd.setDayFactor(
				CalculationUtil.getInterestDays(prvSchd.getSchDate(), curSchd.getSchDate(), prvSchd.getPftDaysBasis()));

		/* Calculate interest and set interest payment details */
		BigDecimal calint = CalculationUtil.calInterest(prvSchd.getSchDate(), curSchd.getSchDate(),
				curSchd.getBalanceForPftCal(), prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

		calint = calint.add(prvSchd.getProfitFraction());
		BigDecimal calIntRounded = BigDecimal.ZERO;
		if(calint.compareTo(BigDecimal.ZERO) > 0){
			calIntRounded = CalculationUtil.roundAmount(calint, finMain.getCalRoundingMode(),
					finMain.getRoundingTarget());
		}

		curSchd.setProfitFraction(calint.subtract(calIntRounded));
		calint = calIntRounded;

		curSchd.setProfitCalc(calint);
		curSchd.setRepayComplete(false);

		if (!curSchd.isPftOnSchDate()) {
			curSchd.setRepayAmount(BigDecimal.ZERO);
			curSchd.setPrincipalSchd(BigDecimal.ZERO);
			curSchd.setProfitSchd(BigDecimal.ZERO);
		} else {

			// FIX: PV: To address Postponements, Reage, Unplanned Holidays,
			// Holidays without additional instructions
			if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_POSTPONE)
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
				curSchd.setProfitSchd(curSchd.getSchdPftPaid());
				curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
				curSchd.setRepayAmount(curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid()));
			} else {
				calPftPriRpy(finScheduleData, iCur, (iCur - 1), finMain.getEventFromDate());
				curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
			}
		}

		/* Balance unpaid interest */
		curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, finMain.getScheduleMethod()));

		/*
		 * If grace period end date and allow grace period is true but
		 * capitalize on schedule date is false OR capitalize at end of grace is
		 * True THEN force it to true
		 */
		if ((DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) == 0 && finMain.isAllowGrcCpz())
				|| (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) == 0)
						&& finMain.isCpzAtGraceEnd()) {
			curSchd.setCpzOnSchDate(true);
		}

		/* If capitalize on this schedule date */
		if (curSchd.isCpzOnSchDate()) {
			curSchd.setCpzAmount(curSchd.getProfitBalance());
		} else {
			curSchd.setCpzAmount(BigDecimal.ZERO);
		}

		curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, finMain.getProfitDaysBasis()));

		if (DateUtility.compare(curSchd.getSchDate(), finMain.getEventFromDate()) > 0
				&& DateUtility.compare(curSchd.getSchDate(), finMain.getEventToDate()) <= 0) {
			finMain.setPftForSelectedPeriod(finMain.getPftForSelectedPeriod().add(curSchd.getProfitCalc()));
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : repaySchdCal Description
	 * : Repay period schedule calculation for reducing rate Process :
	 * ***************************************************
	 * ************************************************************
	 */

	private FinScheduleData repaySchdCal(FinScheduleData finScheduleData, Boolean isCalFlat) {
		logger.debug("Entering");
		FinanceMain finMain = finScheduleData.getFinanceMain();
		boolean isRepayComplete = false;

		finMain.setCalTerms(0);

		BigDecimal calIntFraction = BigDecimal.ZERO;
		BigDecimal calInt = BigDecimal.valueOf(0.0);

		String repayRateBasis = finMain.getRepayRateBasis();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();
		finMain.setNewMaturityIndex(schdDetails.size() - 1);

		// FIND LAST REPAYMENT SCHEDULE DATE
		int size = schdDetails.size();
		Date finalRepayDate = schdDetails.get(size - 1).getSchDate();

		int schdIndex = finMain.getSchdIndex();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		FinanceScheduleDetail prvSchd = new FinanceScheduleDetail();

		for (int i = schdIndex + 1; i < schdDetails.size(); i++) {

			curSchd = schdDetails.get(i);
			prvSchd = schdDetails.get(i - 1);

			// fields which are used for calculation
			BigDecimal prvBalanceForPftCal = prvSchd.getBalanceForPftCal();
			BigDecimal prvDisbAmount = prvSchd.getDisbAmount();
			BigDecimal prvDownPaymentAmount = prvSchd.getDownPaymentAmount();
			BigDecimal prvCpzAmount = prvSchd.getCpzAmount();
			BigDecimal prvFeeChargeAmt = prvSchd.getFeeChargeAmt();
			BigDecimal prvClosingBalance = prvSchd.getClosingBalance();

			Date curSchDate = curSchd.getSchDate();
			Date prvSchDate = prvSchd.getSchDate();

			curSchd.setBalanceForPftCal(BigDecimal.ZERO);

			if (!isRepayComplete) {
				if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_F)
						|| (repayRateBasis.equals(CalculationConstants.RATE_BASIS_C) && isCalFlat)) {
					curSchd.setBalanceForPftCal(prvBalanceForPftCal.add(prvDisbAmount).subtract(prvDownPaymentAmount)
							.add(prvCpzAmount).add(prvFeeChargeAmt));

				} else {
					curSchd.setBalanceForPftCal(prvClosingBalance);
				}
			}

			curSchd.setNoOfDays(DateUtility.getDaysBetween(curSchDate, prvSchDate));
			curSchd.setDayFactor(CalculationUtil.getInterestDays(prvSchDate, curSchDate, prvSchd.getPftDaysBasis()));

			// Calculate Interest
			if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {
				calInt = CalculationUtil.calInterest(prvSchDate, curSchDate, curSchd.getBalanceForPftCal(),
						prvSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

				calInt = calInt.add(calIntFraction);
				BigDecimal calIntRounded = BigDecimal.ZERO;
				if(calInt.compareTo(BigDecimal.ZERO) > 0){
					calIntRounded = CalculationUtil.roundAmount(calInt, finMain.getCalRoundingMode(),
							finMain.getRoundingTarget());
				}
				calIntFraction = calInt.subtract(calIntRounded);
				calInt = calIntRounded;
				curSchd.setRepayComplete(false);
				curSchd.setProfitFraction(calIntFraction);
			} else {
				calInt = BigDecimal.ZERO;
				curSchd.setRepayComplete(true);
			}

			curSchd.setProfitCalc(calInt);

			// APPLICABLE FOR ISLAMIC BANKS ONLY
			boolean isRolloverDate = false;
			if (finMain.getNextRolloverDate() != null
					&& DateUtility.compare(curSchDate, finMain.getNextRolloverDate()) == 0) {
				isRolloverDate = true;
			}

			if (isRepayComplete) {
				curSchd.setProfitSchd(BigDecimal.ZERO);
				curSchd.setPrincipalSchd(BigDecimal.ZERO);
				curSchd.setRepayAmount(BigDecimal.ZERO);
				curSchd.setClosingBalance(BigDecimal.ZERO);
				curSchd.setProfitBalance(BigDecimal.ZERO);
				curSchd.setCpzAmount(BigDecimal.ZERO);
			}

			// LAST REPAYMENT DATE
			if (curSchDate.equals(finalRepayDate) && !isRolloverDate) {
				finScheduleData = procMDTRecord(finScheduleData, i, isRepayComplete);
				isRepayComplete = true;
			}

			if (!isRepayComplete) {

				if (curSchd.isRepayOnSchDate()) {
					curSchd = calPftPriRpy(finScheduleData, i, (i - 1), finMain.getEventFromDate());
					finMain.setNewMaturityIndex(i);

					if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_D)) {
						if (curSchd.getRepayAmount().compareTo(prvClosingBalance.add(curSchd.getDisbAmount())) >= 0) {
							curSchd.setProfitSchd(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())
									.add(curSchd.getProfitCalc()));
							curSchd.setRepayAmount(prvClosingBalance.add(curSchd.getDisbAmount()));
							curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
							isRepayComplete = true;
						}
					} else {
						if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, finMain.getProductCategory())) {
							if (curSchd.getPrincipalSchd().compareTo(prvClosingBalance.add(curSchd.getDisbAmount())) > 0) {
								curSchd.setPrincipalSchd(prvClosingBalance.add(curSchd.getDisbAmount()));
								curSchd.setProfitSchd(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())
										.add(curSchd.getProfitCalc()));
								curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
								isRepayComplete = true;
							}
						} else if (curSchd.getPrincipalSchd().compareTo(prvClosingBalance.add(curSchd.getDisbAmount())) >= 0) {
							curSchd.setPrincipalSchd(prvClosingBalance.add(curSchd.getDisbAmount()));
							curSchd.setProfitSchd(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())
									.add(curSchd.getProfitCalc()));
							curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));

							// Rounding Last Installment
							String roundingMode = finScheduleData.getFinanceMain().getCalRoundingMode();
							int roundingTarget = finScheduleData.getFinanceMain().getRoundingTarget();

							int roundRequired = SysParamUtil.getValueAsInt(SMTParameterConstants.ROUND_LASTSCHD);

							if (roundRequired == 1) {
								curSchd.setRepayAmount(CalculationUtil.roundAmount(curSchd.getRepayAmount(),
										roundingMode, roundingTarget));
								curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));

								if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) < 0) {
									curSchd.setProfitSchd(BigDecimal.ZERO);
								}
							}

							isRepayComplete = true;
						}
					}

					/* Count Repay schedules only */
					finMain.setCalTerms(finMain.getCalTerms() + 1);
					finMain.setCalMaturity(curSchDate);

				} else if (curSchd.isPftOnSchDate()) {

					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_POSTPONE)
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
						curSchd.setProfitSchd(curSchd.getSchdPftPaid());
						curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
						curSchd.setRepayAmount(curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid()));

					} else {
						curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));
						curSchd.setRepayAmount(curSchd.getProfitSchd());
						curSchd.setPrincipalSchd(BigDecimal.ZERO);
					}
				}

				// Resetting Capitalize flag
				if (!curSchd.isCpzOnSchDate() && StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())) {
					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
						curSchd.setCpzOnSchDate(finMain.isPlanEMICpz());
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
						curSchd.setCpzOnSchDate(finMain.isReAgeCpz());
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
						curSchd.setCpzOnSchDate(finMain.isUnPlanEMICpz());
					}
				}

				curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, finMain.getScheduleMethod()));

				// Capitalize OR not
				if (curSchd.isCpzOnSchDate()) {
					curSchd.setCpzAmount(curSchd.getProfitBalance());
				} else {
					curSchd.setCpzAmount(BigDecimal.ZERO);
				}

				curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, repayRateBasis));
				
				// 08-JAN-2018 : When Rounding Effect creates new Record with Negative values after Closing Balance using Profit Balance
				if(curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0){
					curSchd.setClosingBalance(BigDecimal.ZERO);
					
					int roundingTarget = finScheduleData.getFinanceMain().getRoundingTarget();
					BigDecimal pftBal = curSchd.getProfitBalance();
					if(curSchd.getProfitBalance().compareTo(BigDecimal.ZERO) < 0){
						pftBal = curSchd.getProfitBalance().negate();
					}
					if(pftBal.compareTo(new BigDecimal(roundingTarget)) < 0){
						curSchd.setProfitBalance(BigDecimal.ZERO);
						if (curSchd.isCpzOnSchDate()) {
							curSchd.setCpzAmount(BigDecimal.ZERO);
						}
					}
				}

				if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_D)
						&& (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0)) {
					isRepayComplete = true;
				}

				if (DateUtility.compare(curSchDate, finMain.getEventFromDate()) > 0
						&& DateUtility.compare(curSchDate, finMain.getEventToDate()) <= 0) {
					finMain.setPftForSelectedPeriod(finMain.getPftForSelectedPeriod().add(curSchd.getProfitCalc()));
				}

				// For RollOver Finance, adjust Remaining Total Balance Amount
				// to Rollover Amount on Next Rollover Date
				if (finMain.getNextRolloverDate() != null
						&& DateUtility.compare(curSchDate, finMain.getNextRolloverDate()) == 0) {
					curSchd.setRolloverAmount(curSchd.getClosingBalance());
					curSchd.setRolloverOnSchDate(true);
					curSchd.setClosingBalance(BigDecimal.ZERO);
				}

			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : calPftPriRpy Description
	 * : Calculate profit and principal for schedule payment Process :
	 * ************************************************
	 * ****************************************************************
	 */
	private FinanceScheduleDetail calPftPriRpy(FinScheduleData finScheduleData, int iCur, int iPrv, Date evtFromDate) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(iCur);
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(iPrv);
		BigDecimal schdInterest = BigDecimal.ZERO;

		// FIX: PV: To address Postponements, Reage, Unplanned Holidays,
		// Holidays without additional instructions
		if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)
				|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_POSTPONE)
				|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)
				|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
			curSchd.setProfitSchd(curSchd.getSchdPftPaid());
			curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			curSchd.setRepayAmount(curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid()));

			// store first repay amount
			if (finMain.getCalTerms() == 1) {
				finMain.setFirstRepay(curSchd.getRepayAmount());
			}

			finMain.setLastRepay(curSchd.getRepayAmount());
			return curSchd;

		}

		// FIXME: PV 23MAY17
		// NEW CODE ADDED HERE TO AVOID CHANGES RELATED TO COMPLETED SCHEDULES.
		// LIKE IF DUE SENT FOR PRESENTMENT THEN SCHEDULE SHOULD NOT CHANGE
		// MEANS BALANCE WILL BE ADJUSTED TO NEXT SCHEDULES
		/*
		 * if (curSchd.getSchDate().compareTo(finMain.getRecalFromDate()) < 0) {
		 * return curSchd; }
		 */

		// Schedule should be recalculated even Schedule Term is in Presentment Process
		if (curSchd.getPresentmentId() > 0 && !StringUtils.equals(FinanceConstants.FINSER_EVENT_RECEIPT, finMain.getProcMethod())) {
			
			// This case should not be applicable only for Partial Settlement
			// For Partial Settlement in case of Presentment exists after value date of event, schedule should be recalculated
			// For other Servicing actions, even presentment exists in future after event action value date, it should not be recalculated
			// In Screen level we are restricting Recalculation from date for Presentment cases, but in case of PRI_PFT & PFT schedule methods
			// it will be auto calculated and adjusted.
			if((curSchd.getProfitCalc().add(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount()))).compareTo(curSchd.getProfitSchd()) > 0){
				curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
				return curSchd;
			}
		}

		// NO PAYMENT: Applicable for Grace Period And REPAYMENT period with PFT
		// or PRI+PFT)
		if (CalculationConstants.SCHMTHD_NOPAY.equals(curSchd.getSchdMethod())) {
			if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
					&& (StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)
							|| StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE)
							|| StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHD_FIRSTEMI))) {
				schdInterest = curSchd.getProfitCalc();

				if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
					if (!finMain.isAllowGrcCpz()) {
						schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
					}
				} else {
					if (!finMain.isAllowRepayCpz()) {
						schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
					}
				}

				curSchd.setProfitSchd(schdInterest);
			} else {
				curSchd.setProfitSchd(BigDecimal.ZERO);

			}

			curSchd.setPrincipalSchd(BigDecimal.ZERO);

			// EQUAL PAYMENT: Applicable for REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_EQUAL.equals(curSchd.getSchdMethod())) {
			BigDecimal pftToSchd = calProfitToSchd(curSchd, prvSchd);

			if (pftToSchd.compareTo(curSchd.getRepayAmount()) < 0) {
				curSchd.setProfitSchd(pftToSchd);
			} else {
				curSchd.setProfitSchd(curSchd.getRepayAmount());
			}

			curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));

			// PRINCIPAL ONLY: Applicable for REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_PRI.equals(curSchd.getSchdMethod())) {
			curSchd.setProfitSchd(BigDecimal.ZERO);
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			// CALCULATED PROFIT ONLY: Applicable for GRACE & REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_PFT.equals(curSchd.getSchdMethod())) {
			// IF Scheduled Profit cannot change (Effective Rate Calculation)
			// Then leave actual scheduled else calculate
			if (!finMain.isProtectSchdPft()) {
				schdInterest = calProfitToSchd(curSchd, prvSchd);

				if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
					if (!finMain.isAllowGrcCpz()) {
						schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
					}
				} else {
					if (!finMain.isAllowRepayCpz()) {
						schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
					}
				}

				curSchd.setProfitSchd(schdInterest);
			}
			curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			// PRINCIPAL + CALCULATED PROFIT: Applicable for GRACE & REPAYMENT
			// period
		} else if (CalculationConstants.SCHMTHD_PRI_PFT.equals(curSchd.getSchdMethod())) {
			// IF Scheduled Profit cannot change (Effective Rate Calculation)
			// Then leave actual scheduled else calculate
			if (!finMain.isProtectSchdPft()) {
				schdInterest = calProfitToSchd(curSchd, prvSchd);

				if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
					if (!finMain.isAllowGrcCpz()) {
						schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
					}
				} else {
					if (!finMain.isAllowRepayCpz()) {
						schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
					}
				}

				curSchd.setProfitSchd(schdInterest);
			}

			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			// NOPAYMENT IN GRACE SCHEDULES AND COMPLETE PAYMENT AT GRACE END
			// DATE: Applicable for GRACE period Only
		} else if (CalculationConstants.SCHMTHD_GRCENDPAY.equals(curSchd.getSchdMethod())) {

			if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
					&& (finMain.getBpiTreatment().equals(FinanceConstants.BPI_DISBURSMENT)
							|| finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHEDULE)
							|| finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI))) {
				schdInterest = curSchd.getProfitCalc();

				if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
					if (!finMain.isAllowGrcCpz()) {
						schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
					}
				} else {
					if (!finMain.isAllowRepayCpz()) {
						schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
					}
				}

				curSchd.setProfitSchd(schdInterest);
			} else {
				curSchd.setProfitSchd(BigDecimal.ZERO);

			}

			curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());

			if (curSchd.getSchDate().equals(finMain.getGrcPeriodEndDate())) {
				curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));
			}
		}

		curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

		// store first repay amount
		if (finMain.getCalTerms() == 1) {
			finMain.setFirstRepay(curSchd.getRepayAmount());
		}

		// store last repay amount
		finMain.setLastRepay(curSchd.getRepayAmount());

		// logger.debug("Leaving");
		return curSchd;

	}

	/*
	 * *************************************************************************
	 * *************************************** Method : getProfitBalance
	 * Description : Get profit balance unscheduled till schedule date Process :
	 * **********************************************
	 * ******************************************************************
	 */

	private BigDecimal getProfitBalance(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd,
			String schdMethod) {
		if (!(schdMethod.equals(CalculationConstants.SCHMTHD_PRI))) {

			return prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount()).subtract(curSchd.getProfitSchd())
					.add(curSchd.getProfitCalc());
		} else {

			return prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount()).add(curSchd.getProfitCalc())
					.subtract(curSchd.getProfitSchd());
		}
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : getClosingBalance
	 * Description : Schedule record Closing balance Process :
	 * *****************************************************************
	 * ***********************************************
	 */
	private BigDecimal getClosingBalance(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd,
			String repayRateBasis) {

		BigDecimal closingBal = BigDecimal.ZERO;

		if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_D)) {
			closingBal = prvSchd.getClosingBalance().add(curSchd.getDisbAmount()).add(curSchd.getFeeChargeAmt())
					.subtract(curSchd.getDownPaymentAmount()).subtract(curSchd.getRepayAmount())
					.add(curSchd.getCpzAmount());
		} else {
			closingBal = prvSchd.getClosingBalance().add(curSchd.getDisbAmount()).add(curSchd.getFeeChargeAmt())
					.subtract(curSchd.getDownPaymentAmount()).subtract(curSchd.getPrincipalSchd())
					.add(curSchd.getCpzAmount());
		}

		return closingBal;
	}

	/*
	 * *************************************************************************
	 * *************************************** Method : getProfitSchd
	 * Description : Get Profit to be scheduled Process : *
	 * *************************************************************************
	 * **************************************
	 */
	private BigDecimal calProfitToSchd(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd) {

		// If profit already paid do not touch the schedule profit.
		if (curSchd.isSchPftPaid()) {
			return curSchd.getSchdPftPaid();
		}

		return prvSchd.getProfitBalance().add(curSchd.getProfitCalc()).subtract(prvSchd.getCpzAmount());

	}

	/*
	 * *************************************************************************
	 * *************************************** Method : round Description : To
	 * round the BigDecimal value to the basic rounding mode Process :
	 * ***************************************************
	 * *************************************************************
	 */

	private BigDecimal round(BigDecimal value) {
		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	private BigDecimal roundCeiling(BigDecimal value) {
		return value.setScale(0, RoundingMode.CEILING);
	}

	/*
	 * =========================================================================
	 * = ====================================== Method : procSubSchedule
	 * Description : SUB SCHEDULE Process : Add Term will add another term to
	 * the schedule details.
	 * ====================================================
	 * =============================================================
	 */

	private FinScheduleData procSubSchedule(FinScheduleData finScheduleData, int noOfTerms, Date subSchStartDate,
			String frqNewSchd) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		FinScheduleData orgFinScheduleData = null;
		Cloner cloner = new Cloner();
		orgFinScheduleData = cloner.deepClone(finScheduleData);

		finMain.setCompareToExpected(false);
		finMain.setCompareExpectedResult(BigDecimal.ZERO);
		finMain.setRecalFromDate(null);
		finMain.setRecalToDate(null);

		finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

		// Set the limits based on system values table
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtility.addYears(finMain.getFinStartDate(), maxFinYears);

		Date newSchdDate = DateUtility
				.getDate(DateUtility.formatUtilDate(subSchStartDate, PennantConstants.dateFormat));

		for (int i = 0; i < noOfTerms; i++) {
			finScheduleData = addSubScheduleTerm(finScheduleData, lastDateLimit, true, newSchdDate, frqNewSchd);

			if (finScheduleData.getErrorDetails().size() > 0) {
				return orgFinScheduleData;
			}

			newSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(
					FrequencyUtil.getNextDate(frqNewSchd, 1, newSchdDate, HolidayHandlerTypes.MOVE_NONE, false)
							.getNextFrequencyDate(),
					PennantConstants.dateFormat));
		}

		// Except first time creation of schedule covert flat rate to reducing
		// will be treated as reducing only
		finScheduleData = calSchdProcess(finScheduleData, false, false);
		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : addSubScheduleTerm Description : ADD SubSchedule Term Process :
	 * Add SubSchedule Term will add another term to the schedule details.
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData addSubScheduleTerm(FinScheduleData finScheduleData, Date lastDateLimit, boolean isSetRepay,
			Date newSchdDate, String frqNewSchd) {
		logger.debug("Entering");

		// Maturity Record Index
		int maturityIndex = finScheduleData.getFinanceScheduleDetails().size() - 1;

		// Set After which index new term should be added
		int addTermAfterIndex = maturityIndex;

		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(addTermAfterIndex);

		Date curSchdDate = newSchdDate;

		if (newSchdDate.after(lastDateLimit)) {
			// Through Error
			finScheduleData.setErrorDetail(
					new ErrorDetail("SCH30", "ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS", new String[] { " " }));
			return finScheduleData;
		}

		// Set Profit On date based on frequency
		if (FrequencyUtil.isFrqDate(finMain.getRepayPftFrq(), curSchdDate)) {
			curSchd.setPftOnSchDate(true);
		} else {
			curSchd.setPftOnSchDate(false);
		}

		// Set Profit Review On date based on frequency
		if (FrequencyUtil.isFrqDate(finMain.getRepayRvwFrq(), curSchdDate)) {
			curSchd.setRvwOnSchDate(true);
		} else {
			curSchd.setRvwOnSchDate(false);
		}

		// Set Capitalize On date based on frequency
		if (FrequencyUtil.isFrqDate(finMain.getRepayCpzFrq(), curSchdDate)) {
			curSchd.setCpzOnSchDate(true);
		} else {
			curSchd.setCpzOnSchDate(false);
		}

		// Reset New Schedule Record if record not found
		finScheduleData = resetNewSchdDetail(finScheduleData, newSchdDate, CalculationConstants.SCHDFLAG_RPY);

		finMain.setNumberOfTerms(finMain.getNumberOfTerms() + 1);
		finMain.setCalTerms(finMain.getCalTerms() + 1);
		finMain.setMaturityDate(newSchdDate);
		finMain.setCalMaturity(newSchdDate);

		// Set Profit dates between current schedule and next repayment

		finScheduleData = setOtherSchdDates(finScheduleData, frqNewSchd, curSchdDate,
				CalculationConstants.SCHDFLAG_PFT);

		// Set Profit review dates between current schedule and next repayment
		if (finMain.isAllowRepayRvw()) {
			finScheduleData = setOtherSchdDates(finScheduleData, frqNewSchd, curSchdDate,
					CalculationConstants.SCHDFLAG_RVW);
		}

		// Set Capitalization dates between current schedule and next repayment
		if (finMain.isAllowRepayCpz()) {
			finScheduleData = setOtherSchdDates(finScheduleData, frqNewSchd, curSchdDate,
					CalculationConstants.SCHDFLAG_CPZ);
		}

		finScheduleData.getFinanceScheduleDetails().get(finScheduleData.getFinanceScheduleDetails().size() - 1)
				.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Calculating Schedule from FLAT to REDUCE rate with Effective
	 * Rate calculations
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
		FinanceMain finMain = finScheduleData.getFinanceMain();
		BigDecimal number2 = new BigDecimal(2);
		boolean isExactMatch = false;

		if (forInfoOnly) {
			BigDecimal effRateofReturn = BigDecimal.ZERO;
			BigDecimal returnCalProfit = BigDecimal.ZERO;

			effRateofReturn = finScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate();
			finScheduleData = procChangeRate(finScheduleData, null, null, BigDecimal.ZERO, effRateofReturn, false);

			// START PROCESS
			finScheduleData = fetchRatesHistory(finScheduleData);
			finScheduleData = fetchGraceCurRates(finScheduleData);
			finScheduleData = fetchRepayCurRates(finScheduleData);
			finScheduleData = getRpyInstructDetails(finScheduleData);

			/* Grace Schedule calculation */
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);
			finScheduleData = setFinanceTotals(finScheduleData);

			if (CalculationConstants.SCH_SPECIFIER_GRACE.equals(scheduleType)) {
				returnCalProfit = finMain.getTotalGrossGrcPft();
			} else if (CalculationConstants.SCH_SPECIFIER_REPAY.equals(scheduleType)) {
				returnCalProfit = finMain.getTotalGrossPft().subtract(finMain.getTotalGrossGrcPft());
			} else if (CalculationConstants.SCH_SPECIFIER_TOTAL.equals(scheduleType)) {
				returnCalProfit = finMain.getTotalGrossPft();
			} else if (CalculationConstants.SCH_SPECIFIER_SELECT.equals(scheduleType)) {
				returnCalProfit = finMain.getPftForSelectedPeriod();
			}

			if (totalDesiredProfit.compareTo(returnCalProfit) == 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}
		}

		BigDecimal amtDiffWithLowAssumption = BigDecimal.ZERO;
		BigDecimal amtDiffWithHighAssumption = BigDecimal.ZERO;

		BigDecimal lowAssumptionRate = BigDecimal.ZERO;
		BigDecimal highAssumptionRate = BigDecimal.ZERO;

		BigDecimal effRateofReturn = BigDecimal.ZERO;
		BigDecimal returnCalProfit = BigDecimal.ZERO;

		if (effcFromDate != null) {
			finMain.setEventFromDate(effcFromDate);
		}

		if (effcToDate != null) {
			finMain.setEventToDate(effcToDate);
		}

		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);
		finScheduleData = setFinanceTotals(finScheduleData);

		if (CalculationConstants.SCH_SPECIFIER_GRACE.equals(scheduleType)) {
			returnCalProfit = finMain.getTotalGrossGrcPft();

			if (totalDesiredProfit.compareTo(returnCalProfit) >= 0) {
				lowAssumptionRate = finMain.getGrcPftRate();
				highAssumptionRate = lowAssumptionRate.multiply(new BigDecimal(10));
			} else {
				lowAssumptionRate = BigDecimal.ZERO;
				highAssumptionRate = finMain.getGrcPftRate();
			}

		} else if (CalculationConstants.SCH_SPECIFIER_REPAY.equals(scheduleType)) {
			returnCalProfit = finMain.getTotalGrossPft().subtract(finMain.getTotalGrossGrcPft());

			if (totalDesiredProfit.compareTo(returnCalProfit) >= 0) {
				lowAssumptionRate = finMain.getRepayProfitRate();
				highAssumptionRate = lowAssumptionRate.multiply(new BigDecimal(10));
			} else {
				lowAssumptionRate = BigDecimal.ZERO;
				highAssumptionRate = finMain.getRepayProfitRate();
			}

		} else if (CalculationConstants.SCH_SPECIFIER_TOTAL.equals(scheduleType)) {
			returnCalProfit = finMain.getTotalGrossPft();
			lowAssumptionRate = BigDecimal.ZERO;
			highAssumptionRate = new BigDecimal(1000);

		} else if (CalculationConstants.SCH_SPECIFIER_SELECT.equals(scheduleType)) {
			returnCalProfit = finMain.getPftForSelectedPeriod();
			lowAssumptionRate = BigDecimal.ZERO;
			highAssumptionRate = new BigDecimal(1000);
		}

		for (int i = 0; i < 50; i++) {

			effRateofReturn = (lowAssumptionRate.add(highAssumptionRate)).divide(number2, 9, RoundingMode.HALF_DOWN);

			finScheduleData = procChangeRate(finScheduleData, null, null, BigDecimal.ZERO, effRateofReturn, false);

			finScheduleData = getRpyInstructDetails(finScheduleData);

			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);
			finScheduleData = setFinanceTotals(finScheduleData);

			if (CalculationConstants.SCH_SPECIFIER_GRACE.equals(scheduleType)) {
				returnCalProfit = finMain.getTotalGrossGrcPft();
			} else if (CalculationConstants.SCH_SPECIFIER_REPAY.equals(scheduleType)) {
				returnCalProfit = finMain.getTotalGrossPft().subtract(finMain.getTotalGrossGrcPft());
			} else if (CalculationConstants.SCH_SPECIFIER_TOTAL.equals(scheduleType)) {
				returnCalProfit = finMain.getTotalGrossPft();
			} else if (CalculationConstants.SCH_SPECIFIER_SELECT.equals(scheduleType)) {
				returnCalProfit = finMain.getPftForSelectedPeriod();
			}

			if (totalDesiredProfit.compareTo(returnCalProfit) == 0) {
				isExactMatch = true;
				logger.debug("Leaving");
				return finScheduleData;
			}

			if (returnCalProfit.compareTo(totalDesiredProfit) < 0) {
				// Increase effective calculation rate
				lowAssumptionRate = effRateofReturn;
			} else {
				// Decrease effective calculation rate
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

			// Calculate Schedule Building process with Effective Rate
			finScheduleData = procChangeRate(finScheduleData, null, null, BigDecimal.ZERO, effRateofReturn, false);
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

		finScheduleData = procChangeRate(finScheduleData, null, null, BigDecimal.ZERO, effRateofReturn, false);

		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);
		finScheduleData = setFinanceTotals(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Setting Supplementary Rent & increased Cost Charges for Ijarah
	 * product
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData calSuplRentIncrCost(FinScheduleData finScheduleData, BigDecimal suplRent,
			BigDecimal incrCost) {

		FinanceMain finMain = finScheduleData.getFinanceMain();

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		// Setting Rates between Fromdate and Todate
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int i = 0; i < sdSize; i++) {
			curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
				curSchd.setSuplRent(BigDecimal.ZERO);
				curSchd.setSuplRentPaid(BigDecimal.ZERO);
				curSchd.setIncrCost(BigDecimal.ZERO);
				curSchd.setIncrCostPaid(BigDecimal.ZERO);
				continue;
			}

			// Setting Rates between From date and To date
			if (DateUtility.compare(curSchd.getSchDate(), evtFromDate) >= 0
					&& DateUtility.compare(curSchd.getSchDate(), evtToDate) <= 0) {
				curSchd.setSuplRent(suplRent);
				curSchd.setIncrCost(incrCost);
			}
		}

		return finScheduleData;
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * >> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SORTING METHODS
	 * >>>>>>>>>>>>>>>>>>>>>
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sortSchdDetails Description: Sort Schedule Details
	 * ________________________________________________________________________________________________________________
	 */
	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

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

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sortRepayInstructions Description: Sort Repay Instructions
	 * ________________________________________________________________________________________________________________
	 */
	private List<RepayInstruction> sortRepayInstructions(List<RepayInstruction> repayInstructions) {

		if (repayInstructions != null && repayInstructions.size() > 0) {
			Collections.sort(repayInstructions, new Comparator<RepayInstruction>() {
				@Override
				public int compare(RepayInstruction detail1, RepayInstruction detail2) {
					return DateUtility.compare(detail1.getRepayDate(), detail2.getRepayDate());
				}
			});
		}
		return repayInstructions;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sort Base Rates History
	 * ________________________________________________________________________________________________________________
	 */
	private List<BaseRate> sortBaseRateHistrates(List<BaseRate> baseRates) {

		if (baseRates != null && baseRates.size() > 0) {
			Collections.sort(baseRates, new Comparator<BaseRate>() {
				@Override
				public int compare(BaseRate detail1, BaseRate detail2) {
					return DateUtility.compare(detail1.getBREffDate(), detail2.getBREffDate());
				}
			});
		}

		return baseRates;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sort Special Rates History
	 * ________________________________________________________________________________________________________________
	 */
	private List<SplRate> sortSplRateHistrates(List<SplRate> splRates) {

		if (splRates != null && splRates.size() > 0) {
			Collections.sort(splRates, new Comparator<SplRate>() {
				@Override
				public int compare(SplRate detail1, SplRate detail2) {
					return DateUtility.compare(detail1.getSREffDate(), detail2.getSREffDate());
				}
			});
		}

		return splRates;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sortOverdraftSchedules Description: Sort Overdraft Schedule
	 * Details
	 * ________________________________________________________________________________________________________________
	 */
	private List<OverdraftScheduleDetail> sortOverdraftSchedules(List<OverdraftScheduleDetail> overdraftSchedules) {

		if (overdraftSchedules != null && overdraftSchedules.size() > 0) {

			Collections.sort(overdraftSchedules, new Comparator<OverdraftScheduleDetail>() {
				@Override
				public int compare(OverdraftScheduleDetail odSchd1, OverdraftScheduleDetail odSchd2) {
					return DateUtility.compare(odSchd1.getDroplineDate(), odSchd2.getDroplineDate());
				}
			});
		}

		return overdraftSchedules;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : PMT
	 * ________________________________________________________________________________________________________________
	 */
	private BigDecimal approxPMT(FinanceMain finMain, BigDecimal intRate, int terms, BigDecimal presentValue,
			BigDecimal futureValue, int type) {

		if (intRate.compareTo(BigDecimal.ZERO) == 0) {
			BigDecimal pmtValue = BigDecimal.ZERO;
			if (finMain.isCalculateRepay()) {
				pmtValue = presentValue.divide(new BigDecimal(terms), 0, RoundingMode.HALF_DOWN);
			}
			return pmtValue;
		}

		String idb = finMain.getProfitDaysBasis();
		String calFrq = StringUtils.mid(finMain.getRepayFrq(), 0, 1);
		BigDecimal periods = BigDecimal.ZERO;
		BigDecimal days365 = new BigDecimal(365);
		BigDecimal days360 = new BigDecimal(360);

		// Interest Rate Per Day
		intRate = intRate.divide(BigDecimal.valueOf(36000), 13, RoundingMode.HALF_DOWN);

		if (!finMain.getRepayPftFrq().equals(finMain.getRepayFrq())) {
			if (finMain.isFinRepayPftOnFrq()) {
				calFrq = StringUtils.mid(finMain.getRepayPftFrq(), 0, 1);
			}
		}

		if (calFrq.equals(FrequencyCodeTypes.FRQ_MONTHLY)) {
			periods = new BigDecimal(12);
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_QUARTERLY)) {
			periods = new BigDecimal(4);
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_HALF_YEARLY)) {
			periods = new BigDecimal(2);
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_YEARLY)) {
			periods = new BigDecimal(1);
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_BIWEEKLY)
				|| calFrq.equals(FrequencyCodeTypes.FRQ_FORTNIGHTLY)) {
			periods = new BigDecimal(26);
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_BIMONTHLY)) {
			periods = new BigDecimal(6);
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_WEEKLY)) {
			periods = new BigDecimal(52);
		} else {
			periods = new BigDecimal(365);
		}

		// Interest Rate Per Period
		if (idb.equals(CalculationConstants.IDB_ACT_ISDA) || idb.equals(CalculationConstants.IDB_ACT_365FIXED)
				|| idb.equals(CalculationConstants.IDB_ACT_365LEAPS)
				|| idb.equals(CalculationConstants.IDB_ACT_365LEAP)) {
			intRate = intRate.multiply(days365.divide(periods, 13, RoundingMode.HALF_DOWN));
		} else {
			intRate = intRate.multiply(days360.divide(periods, 13, RoundingMode.HALF_DOWN));
		}

		presentValue = presentValue.subtract(futureValue);
		futureValue = BigDecimal.ZERO;

		double dPMT = 0;
		double dIntRate = intRate.doubleValue();
		double dPresentValue = presentValue.doubleValue();
		double dFutureValue = futureValue.doubleValue();
		BigDecimal pmt = BigDecimal.ZERO;

		dPMT = dIntRate / (Math.pow(1 + dIntRate, terms) - 1)
				* (dPresentValue * Math.pow(1 + dIntRate, terms) + dFutureValue);

		if (type == 1) {
			dPMT = dPMT / (1 + dIntRate);
		}

		pmt = round(BigDecimal.valueOf(dPMT));

		// pmt = pmt.setScale(0, RoundingMode.HALF_DOWN);
		pmt = CalculationUtil.roundAmount(pmt, finMain.getCalRoundingMode(), finMain.getRoundingTarget());

		return pmt;

	}

	/*
	 * Method to Add BPI schedule record if applicable
	 */

	public FinScheduleData addBPISchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		if (StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_NO)) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		Date firstSchdDate = finScheduleData.getFinanceScheduleDetails().get(1).getSchDate();
		String frqBPI = "";

		if (finMain.isAllowGrcPeriod()) {
			frqBPI = finMain.getGrcPftFrq();
		} else {
			frqBPI = finMain.getRepayPftFrq();
		}

		Date bpiDate = DateUtility
				.getDate(
						DateUtility.formatUtilDate(
								FrequencyUtil.getNextDate(frqBPI, 1, finMain.getFinStartDate(),
										HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
								PennantConstants.dateFormat));

		if (DateUtility.compare(bpiDate, firstSchdDate) >= 0) {
			finScheduleData.getFinanceScheduleDetails().get(1).setBpiOrHoliday(FinanceConstants.FLAG_BPI);

			if (StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_CAPITALIZE)) {
				finScheduleData.getFinanceScheduleDetails().get(1).setPftOnSchDate(false);
				finScheduleData.getFinanceScheduleDetails().get(1).setCpzOnSchDate(true);
			} else {
				finScheduleData.getFinanceScheduleDetails().get(1).setPftOnSchDate(true);
				finScheduleData.getFinanceScheduleDetails().get(1).setCpzOnSchDate(false);
			}

			if (DateUtility.compare(bpiDate, finMain.getGrcPeriodEndDate()) > 0) {
				finScheduleData.getFinanceScheduleDetails().get(1).setSchdMethod(finMain.getScheduleMethod());
			} else {
				finScheduleData.getFinanceScheduleDetails().get(1).setSchdMethod(finMain.getGrcSchdMthd());
			}
			logger.debug("Leaving");
			return finScheduleData;
		}

		// insert new Schedule Dated term
		FinanceScheduleDetail openSchd = finScheduleData.getFinanceScheduleDetails().get(0);
		FinanceScheduleDetail sd = new FinanceScheduleDetail();

		sd.setSchDate(bpiDate);
		sd.setDefSchdDate(bpiDate);
		sd.setBpiOrHoliday(FinanceConstants.FLAG_BPI);
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
		sd.setBaseRate(openSchd.getBaseRate());
		sd.setSplRate(openSchd.getSplRate());
		sd.setMrgRate(openSchd.getMrgRate());
		sd.setActRate(openSchd.getActRate());
		sd.setCalculatedRate(openSchd.getCalculatedRate());
		sd.setPftDaysBasis(openSchd.getPftDaysBasis());
		sd.setAdvBaseRate(openSchd.getAdvBaseRate());
		sd.setAdvMargin(openSchd.getAdvMargin());
		sd.setAdvPftRate(openSchd.getAdvPftRate());
		sd.setSuplRent(openSchd.getSuplRent());
		sd.setIncrCost(openSchd.getIncrCost());

		if (DateUtility.compare(bpiDate, finMain.getGrcPeriodEndDate()) > 0) {
			sd.setSchdMethod(finMain.getScheduleMethod());
		} else {
			sd.setSchdMethod(finMain.getGrcSchdMthd());
		}

		if (StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_CAPITALIZE)) {
			sd.setPftOnSchDate(false);
			sd.setCpzOnSchDate(true);
		} else {
			sd.setPftOnSchDate(true);
			sd.setCpzOnSchDate(false);
		}

		sd.setRvwOnSchDate(false);
		sd.setRepayOnSchDate(false);

		finScheduleData.getFinanceScheduleDetails().add(sd);
		finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));

		if (sd.isRepayOnSchDate()) {
			finMain.setNumberOfTerms(finMain.getNumberOfTerms() + 1);
		}

		logger.debug("Leaving");
		return finScheduleData;

	}

	public FinScheduleData adjustBPISchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		BigDecimal zeroAmount = new BigDecimal(0);

		if (!finMain.isAlwBPI()) {
			logger.debug("Leaving - Not BPI");
			return finScheduleData;
		}

		if (!finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
			logger.debug("Leaving - Not Add to First Inst");
			return finScheduleData;
		}

		FinanceScheduleDetail bpiSchd = finScheduleData.getFinanceScheduleDetails().get(1);
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		int Size = finScheduleData.getFinanceScheduleDetails().size();

		for (int i = 2; i < (Size - 1); i++) {
			curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (curSchd.isPftOnSchDate()) {
				break;
			}

			if (curSchd.isRepayOnSchDate()) {
				break;
			}

			if (curSchd.getSchDate().equals(finMain.getMaturityDate())) {
				break;
			}
		}

		curSchd.setProfitSchd(curSchd.getProfitSchd().add(bpiSchd.getProfitSchd()));
		curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
		bpiSchd.setRepayAmount(bpiSchd.getRepayAmount().subtract(bpiSchd.getProfitSchd()));
		bpiSchd.setProfitSchd(zeroAmount);
		bpiSchd.setDefSchdDate(curSchd.getSchDate());
		// curSchd.setProfitBalance(bpiSchd.getProfitBalance().add(curSchd.getProfitCalc().subtract(curSchd.getProfitSchd())));
		// bpiSchd.setProfitBalance(zeroAmount);

		logger.debug("Leaving");
		return finScheduleData;

	}

	public FinScheduleData procMDTRecord(FinScheduleData finScheduleData, int i, boolean isRepayComplete) {
		logger.debug("Entering");

		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(i - 1);

		String repayRateBasis = finScheduleData.getFinanceMain().getRepayRateBasis();

		curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd));

		if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_D)) {
			curSchd.setRepayAmount(prvSchd.getClosingBalance());
			curSchd.setPrincipalSchd(curSchd.getClosingBalance().subtract(curSchd.getProfitSchd()));
			BigDecimal endBal = getClosingBalance(curSchd, prvSchd, repayRateBasis)
					.subtract(curSchd.getRefundOrWaiver());

			if (endBal.compareTo(BigDecimal.ZERO) < 0) {
				curSchd.setClosingBalance(BigDecimal.ZERO);
			} else {
				curSchd.setClosingBalance(endBal);
			}

		} else {
			curSchd.setPrincipalSchd(prvSchd.getClosingBalance());
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			BigDecimal endBal = getClosingBalance(curSchd, prvSchd, repayRateBasis);
			curSchd.setClosingBalance(endBal);
		}

		curSchd.setSchdMethod(prvSchd.getSchdMethod());
		if (!isRepayComplete) {
			finScheduleData.getFinanceMain().setCalTerms(finScheduleData.getFinanceMain().getCalTerms() + 1);
			finScheduleData.getFinanceMain().setCalMaturity(curSchd.getSchDate());
		}

		// Rounding Last Installment
		String roundingMode = finScheduleData.getFinanceMain().getCalRoundingMode();
		int roundingTarget = finScheduleData.getFinanceMain().getRoundingTarget();

		int roundRequired = SysParamUtil.getValueAsInt(SMTParameterConstants.ROUND_LASTSCHD);

		if (roundRequired == 1) {
			curSchd.setRepayAmount(CalculationUtil.roundAmount(curSchd.getRepayAmount(), roundingMode, roundingTarget));
			curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));

			if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) < 0) {
				curSchd.setProfitSchd(BigDecimal.ZERO);
			}
		}

		logger.debug("Leaving");
		return finScheduleData;

	}

	public FinScheduleData prepareFirstSchdCal(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		int indexRepay = finScheduleData.getFinanceMain().getSchdIndex();
		FinanceScheduleDetail grcEndSchd = finScheduleData.getFinanceScheduleDetails().get(indexRepay);
		FinanceScheduleDetail rpySchd = finScheduleData.getFinanceScheduleDetails().get(indexRepay);

		FinanceMain finMain = finScheduleData.getFinanceMain();
		BigDecimal instAmt = new BigDecimal(0);
		int terms = finMain.getNumberOfTerms();
		BigDecimal presentValue = grcEndSchd.getClosingBalance();
		String schdMethod = finMain.getScheduleMethod();

		finMain.setAdjTerms(terms);

		if (finMain.isStepFinance()) {

			finScheduleData = calStepSchd(finScheduleData);

		} else {

			if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {

				if (finMain.getReqRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
					instAmt = finMain.getReqRepayAmount();
				} else {
					BigDecimal intRate = rpySchd.getCalculatedRate();
					instAmt = approxPMT(finMain, intRate, terms, presentValue, BigDecimal.ZERO, 0);
				}

			} else if (schdMethod.equals(CalculationConstants.SCHMTHD_PRI)
					|| schdMethod.equals(CalculationConstants.SCHMTHD_PRI_PFT)) {

				if (finMain.getReqRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
					instAmt = finMain.getReqRepayAmount();
				} else {
					instAmt = presentValue.divide(BigDecimal.valueOf(terms), 0, RoundingMode.HALF_DOWN);
				}

			}

			finScheduleData = setRpyInstructDetails(finScheduleData, finMain.getNextRepayPftDate(),
					finMain.getMaturityDate(), instAmt, schdMethod);
			finMain.setRecalFromDate(finMain.getNextRepayPftDate());
			finMain.setIndexMisc(finScheduleData.getRepayInstructions().size() - 1);
			finMain.setMiscAmount(instAmt);
		}

		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData calStepSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		if (finScheduleData.getFinanceMain().getStepType().equals(FinanceConstants.STEPTYPE_PRIBAL)) {
			finScheduleData = calPercentageSteps(finScheduleData);
		} else {
			finScheduleData = calEMISteps(finScheduleData);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData calPercentageSteps(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceStepPolicyDetail> stepPolicyDetails = finScheduleData.getStepPolicyDetails();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int iTerms = 0;

		BigDecimal openPrincipal = finSchdDetails.get(0).getClosingBalance();
		BigDecimal openOSBalance = finSchdDetails.get(finMain.getSchdIndex()).getClosingBalance();
		BigDecimal closeBalPercentage = BigDecimal.ONE;

		BigDecimal stepOpenBal = openOSBalance;
		BigDecimal stepAmount = BigDecimal.ZERO;
		BigDecimal stepIntRate = BigDecimal.ZERO;

		int sdSize = finSchdDetails.size();
		int stepSize = stepPolicyDetails.size();

		finMain.setIndexStart(finMain.getSchdIndex() + 1);
		finMain.setIndexEnd(sdSize - 1);

		stepOpenBal = openOSBalance;

		boolean isRateStepOnly = false;

		if (!finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_EQUAL)
				&& !finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI_PFT)
				&& !finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI)) {
			isRateStepOnly = true;
		}

		for (int i = 0; i < stepSize; i++) {
			FinanceStepPolicyDetail stepDetail = stepPolicyDetails.get(i);

			if (!isRateStepOnly) {
				closeBalPercentage = closeBalPercentage
						.subtract(stepDetail.getEmiSplitPerc().divide(new BigDecimal(100)));
				finMain.setCompareExpectedResult(round(openPrincipal.multiply(closeBalPercentage)));
			}
			stepIntRate = stepDetail.getRateMargin();

			iTerms = 0;

			for (int j = (finMain.getIndexStart()); j < sdSize; j++) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(j);
				FinanceScheduleDetail prvSchd = finSchdDetails.get(j - 1);

				if (prvSchd.getBaseRate() != null) {
					prvSchd.setMrgRate(prvSchd.getMrgRate().add(stepIntRate));
				} else {
					prvSchd.setActRate(prvSchd.getActRate().add(stepIntRate));
					prvSchd.setCalculatedRate(prvSchd.getCalculatedRate().add(stepIntRate));
				}

				if (!curSchd.isRepayOnSchDate()) {
					continue;
				} else {
					curSchd.setRvwOnSchDate(true);
				}

				iTerms = iTerms + 1;
				finMain.setIndexEnd(j);

				if (iTerms != stepDetail.getInstallments()) {
					continue;
				}

				if (j == (sdSize - 1)) {
					if (curSchd.getBaseRate() != null) {
						curSchd.setMrgRate(curSchd.getMrgRate().add(stepIntRate));
					} else {
						curSchd.setActRate(curSchd.getActRate().add(stepIntRate));
						curSchd.setCalculatedRate(curSchd.getCalculatedRate().add(stepIntRate));
					}
				}

				if (finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_EQUAL)) {
					stepAmount = approxPMT(finMain, curSchd.getCalculatedRate(), iTerms, stepOpenBal,
							finMain.getCompareExpectedResult(), 0);
				} else if (finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI_PFT)
						|| finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI)) {
					stepAmount = stepOpenBal.subtract(finMain.getCompareExpectedResult())
							.divide(BigDecimal.valueOf(iTerms), 0, RoundingMode.HALF_DOWN);
				}

				stepAmount = CalculationUtil.roundAmount(stepAmount, finMain.getCalRoundingMode(),
						finMain.getRoundingTarget());

				if (!isRateStepOnly) {
					finScheduleData = fetchRepayCurRates(finScheduleData);
					finMain.setMiscAmount(stepAmount);
					finScheduleData = targetPriOSBal(finScheduleData, iTerms, false);
					stepOpenBal = finScheduleData.getFinanceScheduleDetails().get(j).getClosingBalance();
				}

				finMain.setIndexStart(j + 1);

				break;

			}
		}

		if (isRateStepOnly) {
			finScheduleData = fetchRepayCurRates(finScheduleData);
			finScheduleData = getRpyInstructDetails(finScheduleData);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData targetPriOSBal(FinScheduleData finScheduleData, int iTerms, boolean isCalFlat) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		BigDecimal stepAmount = finMain.getMiscAmount();
		Date fromDate = finSchdDetails.get(finMain.getIndexStart()).getSchDate();
		Date toDate = finSchdDetails.get(finMain.getIndexEnd()).getSchDate();
		Boolean isFinalStep = false;
		BigDecimal comparisionAmount = BigDecimal.ZERO;
		BigDecimal comparisionToAmount = BigDecimal.ZERO;
		String schdMethod = finMain.getScheduleMethod();
		BigDecimal adjAmount = BigDecimal.ZERO;
		BigDecimal adjAmountAbsolute = BigDecimal.ZERO;
		BigDecimal maxAlwDif = new BigDecimal(iTerms);
		maxAlwDif = maxAlwDif.divide(new BigDecimal(2)).subtract(BigDecimal.ONE);
		maxAlwDif = roundCeiling(maxAlwDif);

		// It is rare case and happen only when last step size is 1
		if (maxAlwDif.compareTo(BigDecimal.ZERO) < 0) {
			maxAlwDif = BigDecimal.ZERO;
		}

		FinanceScheduleDetail compSchd = finSchdDetails.get(finMain.getIndexEnd());

		if (finMain.getIndexEnd() == (sdSize - 1)) {
			isFinalStep = true;
		}

		int riSize = finScheduleData.getRepayInstructions().size();

		for (int i = 0; i < riSize; i++) {
			RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(i);

			if (DateUtility.compare(curInstruction.getRepayDate(), fromDate) >= 0) {
				schdMethod = curInstruction.getRepaySchdMethod();
				break;
			}
		}

		setRpyInstructDetails(finScheduleData, fromDate, toDate, stepAmount, schdMethod);

		finScheduleData = getRpyInstructDetails(finScheduleData);
		//finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

		compSchd = finSchdDetails.get(finMain.getIndexEnd());

		if (!isFinalStep) {
			comparisionAmount = compSchd.getClosingBalance();
			comparisionToAmount = finMain.getCompareExpectedResult();
		} else {
			if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
				comparisionAmount = compSchd.getRepayAmount();
			} else {
				comparisionAmount = compSchd.getPrincipalSchd();
			}

			comparisionToAmount = stepAmount;
		}

		adjAmount = comparisionAmount.subtract(comparisionToAmount);
		adjAmountAbsolute = adjAmount.abs();

		if (adjAmountAbsolute.compareTo(maxAlwDif) <= 0) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		for (int i = 0; i < 50; i++) {

			adjAmount = adjAmount.divide(BigDecimal.valueOf(iTerms), 0, RoundingMode.HALF_DOWN);

			if (adjAmount.abs().compareTo(BigDecimal.valueOf(finMain.getRoundingTarget())) <= 0) {
				break;
			}

			stepAmount = stepAmount.add(adjAmount);
			setRpyInstructDetails(finScheduleData, fromDate, toDate, stepAmount, schdMethod);

			finScheduleData = getRpyInstructDetails(finScheduleData);
			//finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

			compSchd = finSchdDetails.get(finMain.getIndexEnd());

			if (!isFinalStep) {
				comparisionAmount = compSchd.getClosingBalance();
				comparisionToAmount = finMain.getCompareExpectedResult();
			} else {
				if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {
					comparisionAmount = compSchd.getRepayAmount();
				} else {
					comparisionAmount = compSchd.getPrincipalSchd();
				}

				comparisionToAmount = stepAmount;
			}

			adjAmount = comparisionAmount.subtract(comparisionToAmount);
			adjAmountAbsolute = adjAmount.abs();

			if (adjAmountAbsolute.compareTo(maxAlwDif) <= 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}

		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData calEMISteps(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceStepPolicyDetail> stepPolicyDetails = finScheduleData.getStepPolicyDetails();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail geSchd = finSchdDetails.get(finMain.getSchdIndex());

		boolean isRateStepOnly = false;

		BigDecimal approxEMI = BigDecimal.ZERO;
		BigDecimal stepEMI = BigDecimal.ZERO;

		finScheduleData = fetchRatesHistory(finScheduleData);
		finScheduleData = fetchGraceCurRates(finScheduleData);

		if (finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_EQUAL)) {
			approxEMI = approxPMT(finMain, geSchd.getCalculatedRate(), finMain.getNumberOfTerms(),
					geSchd.getClosingBalance(), finMain.getCompareExpectedResult(), 0);
		} else if (finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI)
				|| finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI_PFT)) {
			approxEMI = geSchd.getClosingBalance().divide(BigDecimal.valueOf(finMain.getNumberOfTerms()), 0,
					RoundingMode.HALF_DOWN);
		} else {
			approxEMI = BigDecimal.ZERO;
			isRateStepOnly = true;
		}

		approxEMI = CalculationUtil.roundAmount(approxEMI, finMain.getCalRoundingMode(), finMain.getRoundingTarget());

		BigDecimal stepIntRate = BigDecimal.ZERO;

		int sdSize = finSchdDetails.size();
		int stepSize = stepPolicyDetails.size();
		int iTerms = 0;

		finMain.setIndexStart(finMain.getSchdIndex() + 1);
		finMain.setIndexEnd(sdSize - 1);

		for (int i = 0; i < stepSize; i++) {
			FinanceStepPolicyDetail stepDetail = stepPolicyDetails.get(i);
			stepIntRate = stepDetail.getRateMargin();

			boolean isSetRepayIntructions = true;
			iTerms = 0;

			for (int j = (finMain.getIndexStart()); j < sdSize; j++) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(j);
				FinanceScheduleDetail prvSchd = finSchdDetails.get(j - 1);

				if (prvSchd.getBaseRate() != null) {
					prvSchd.setMrgRate(prvSchd.getMrgRate().add(stepIntRate));
				} else {
					prvSchd.setActRate(prvSchd.getActRate().add(stepIntRate));
					prvSchd.setCalculatedRate(prvSchd.getCalculatedRate().add(stepIntRate));
				}

				if (j == (sdSize - 1)) {
					if (curSchd.getBaseRate() != null) {
						curSchd.setMrgRate(curSchd.getMrgRate().add(stepIntRate));
					} else {
						curSchd.setActRate(curSchd.getActRate().add(stepIntRate));
						curSchd.setCalculatedRate(curSchd.getCalculatedRate().add(stepIntRate));
					}
				}

				if (isSetRepayIntructions) {
					if (!isRateStepOnly) {
						stepEMI = approxEMI.multiply(stepDetail.getEmiSplitPerc().divide(new BigDecimal(100)));
						stepEMI = CalculationUtil.roundAmount(stepEMI, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
						setRpyInstructDetails(finScheduleData, curSchd.getSchDate(), finMain.getMaturityDate(), stepEMI,
								curSchd.getSchdMethod());
					}
					isSetRepayIntructions = false;
				}

				if (!curSchd.isRepayOnSchDate()) {
					continue;
				}

				iTerms = iTerms + 1;
				finMain.setIndexEnd(j);

				if (iTerms == stepDetail.getInstallments()) {
					finMain.setIndexStart(j + 1);
					break;
				}

			}
		}

		finScheduleData = fetchRepayCurRates(finScheduleData);
		finScheduleData = getRpyInstructDetails(finScheduleData);
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);

		finMain.setMiscAmount(approxEMI);
		finMain.setAdjTerms(finMain.getNumberOfTerms());
		finScheduleData = calEqualStepPayment(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData calEqualPayment(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int sdSize = finSchdDetails.size();
		int riSize = repayInstructions.size();
		int iTerms = finMain.getAdjTerms();
		int iRpyInst = 0;

		BigDecimal repayAmountLow = BigDecimal.ZERO;
		BigDecimal repayAmountHigh = BigDecimal.ZERO;

		// Setting Compare to Expected defaults to false for Further actions
		finMain.setCompareToExpected(false);
		BigDecimal comparisionAmount = BigDecimal.ZERO;
		BigDecimal comparisionToAmount = BigDecimal.ZERO;
		String schdMethod = "";
		BigDecimal approxEMI = BigDecimal.ZERO;
		boolean isCompareMDTRecord = false;
		boolean isComapareWithEMI = false;

		// Comparison amount is Maturity Record or Instruction record
		if (StringUtils.equals(CalculationConstants.RPYCHG_CURPRD, finMain.getRecalType())
				|| StringUtils.equals(CalculationConstants.RPYCHG_TILLDATE, finMain.getRecalType())) {
			isCompareMDTRecord = true;
		}

		// Find Comparision with EMI or Principal
		schdMethod = repayInstructions.get(riSize - 1).getRepaySchdMethod();
		if (StringUtils.equals(CalculationConstants.SCHMTHD_EQUAL, schdMethod)) {
			isComapareWithEMI = true;
		}

		// Get Repayment instruction index
		for (int i = 0; i < riSize; i++) {
			if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), finMain.getRecalFromDate()) == 0) {
				iRpyInst = i;
				approxEMI = repayInstructions.get(i).getRepayAmount();
				break;
			}
		}

		// Calculate terms to be adjusted
		for (int i = 0; i < sdSize; i++) {
			if (DateUtility.compare(finSchdDetails.get(i).getSchDate(), finMain.getRecalFromDate()) >= 0
					&& DateUtility.compare(finSchdDetails.get(i).getSchDate(), finMain.getRecalToDate()) <= 0) {
				iTerms = iTerms + 1;
			}
		}

		// Set Recalculation Schedule Method
		schdMethod = finMain.getRecalSchdMethod();

		// Find COMPARISION Amount
		if (isCompareMDTRecord) {
			comparisionAmount = finMain.getCompareExpectedResult();
		} else {
			comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();
		}

		// Find COMPARISION TO Amount
		if (isComapareWithEMI) {
			comparisionToAmount = finSchdDetails.get(sdSize - 1).getRepayAmount();
		} else {
			comparisionToAmount = finSchdDetails.get(sdSize - 1).getPrincipalSchd();
		}

		if (approxEMI.compareTo(comparisionToAmount) == 1) {
			repayAmountLow = comparisionToAmount;
			repayAmountHigh = approxEMI;
		} else {
			repayAmountLow = approxEMI;
			repayAmountHigh = comparisionToAmount;
		}

		BigDecimal lastTriedEMI = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);
		BigDecimal diff_Low_High = BigDecimal.ZERO;

		for (int i = 0; i < 50; i++) {
			approxEMI = (repayAmountLow.add(repayAmountHigh)).divide(number2, 0, RoundingMode.HALF_DOWN);
			approxEMI = CalculationUtil.roundAmount(approxEMI, finMain.getCalRoundingMode(),
					finMain.getRoundingTarget());

			if (repayAmountLow.compareTo(approxEMI) == 0 || repayAmountHigh.compareTo(approxEMI) == 0) {
				break;
			}

			diff_Low_High = (repayAmountHigh.subtract(repayAmountLow)).abs();
			if (diff_Low_High.compareTo(BigDecimal.valueOf(finMain.getRoundingTarget())) <= 0) {
				break;
			}

			lastTriedEMI = approxEMI;
			repayInstructions.get(iRpyInst).setRepayAmount(approxEMI);
			finScheduleData = getRpyInstructDetails(finScheduleData);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);

			// Find COMPARISION Amount
			if (!isCompareMDTRecord) {
				comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();
			}

			// Find COMPARISION TO Amount
			if (isComapareWithEMI) {
				comparisionToAmount = finSchdDetails.get(sdSize - 1).getRepayAmount();
			} else {
				comparisionToAmount = finSchdDetails.get(sdSize - 1).getPrincipalSchd();
			}

			if (comparisionToAmount.compareTo(comparisionAmount) == 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}

			diff_Low_High = (comparisionToAmount.subtract(comparisionAmount)).abs();
			if (diff_Low_High.compareTo(BigDecimal.valueOf(finMain.getRoundingTarget())) <= 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}

			if (comparisionAmount.compareTo(comparisionToAmount) < 0) {
				repayAmountLow = approxEMI;
			} else {
				repayAmountHigh = approxEMI;
			}

		}

		// Find Nearest EMI
		BigDecimal minRepayDifference = BigDecimal.ZERO;
		BigDecimal maxRepayDifference = BigDecimal.ZERO;

		// Find COMPARISION Amount
		if (!isCompareMDTRecord) {
			comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();
		}

		// Find COMPARISION TO Amount
		if (isComapareWithEMI) {
			comparisionToAmount = finSchdDetails.get(sdSize - 1).getRepayAmount();
		} else {
			comparisionToAmount = finSchdDetails.get(sdSize - 1).getPrincipalSchd();
		}

		if (repayAmountLow.compareTo(repayAmountHigh) != 0) {
			if (lastTriedEMI.compareTo(repayAmountLow) == 0) {
				minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				approxEMI = repayAmountHigh;
			} else {
				maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				approxEMI = repayAmountLow;
			}

			approxEMI = CalculationUtil.roundAmount(approxEMI, finMain.getCalRoundingMode(),
					finMain.getRoundingTarget());

			lastTriedEMI = approxEMI;
			repayInstructions.get(iRpyInst).setRepayAmount(approxEMI);
			finScheduleData = getRpyInstructDetails(finScheduleData);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);

			// Find COMPARISION Amount
			if (!isCompareMDTRecord) {
				comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();
			}

			// Find COMPARISION TO Amount
			if (isComapareWithEMI) {
				comparisionToAmount = finSchdDetails.get(sdSize - 1).getRepayAmount();
			} else {
				comparisionToAmount = finSchdDetails.get(sdSize - 1).getPrincipalSchd();
			}

			if (lastTriedEMI.compareTo(repayAmountLow) == 0) {
				minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
			} else {
				maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
			}

			if (maxRepayDifference.compareTo(minRepayDifference) < 0) {
				approxEMI = repayAmountHigh;
			} else {
				approxEMI = repayAmountLow;
			}
		}

		approxEMI = CalculationUtil.roundAmount(approxEMI, finMain.getCalRoundingMode(), finMain.getRoundingTarget());

		// SET EQUAL REPAYMENT AMOUNT AS EFFECTIVE REPAY AMOUNT AND CALL PROCESS
		repayInstructions.get(iRpyInst).setRepayAmount(approxEMI);
		finScheduleData = getRpyInstructDetails(finScheduleData);
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData calEqualStepPayment(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();
		List<FinanceStepPolicyDetail> stepPolicyDetails = finScheduleData.getStepPolicyDetails();

		int sdSize = finSchdDetails.size();
		int riSize = repayInstructions.size();
		int stepSize = stepPolicyDetails.size();
		int iRpyInst = 0;
		int iCompare = 0;

		BigDecimal repayAmountLow = BigDecimal.ZERO;
		BigDecimal repayAmountHigh = BigDecimal.ZERO;

		// Setting Compare to Expected defaults to false for Further actions
		finMain.setCompareToExpected(false);
		BigDecimal comparisionAmount = BigDecimal.ZERO;
		BigDecimal comparisionToAmount = BigDecimal.ZERO;
		String schdMethod = finMain.getScheduleMethod();
		BigDecimal approxEMI = BigDecimal.ZERO;
		BigDecimal iCompareEMI = BigDecimal.ZERO;
		BigDecimal stepEMI = BigDecimal.ZERO;
		boolean isComapareWithEMI = false;

		if (!schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)
				&& !schdMethod.equals(CalculationConstants.SCHMTHD_PRI_PFT)
				&& !schdMethod.equals(CalculationConstants.SCHMTHD_PRI)) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		if (StringUtils.equals(CalculationConstants.SCHMTHD_EQUAL, schdMethod)) {
			isComapareWithEMI = true;
		}

		iRpyInst = riSize - 1;
		iCompare = sdSize - 1;

		approxEMI = finMain.getMiscAmount();
		approxEMI = CalculationUtil.roundAmount(approxEMI, finMain.getCalRoundingMode(), finMain.getRoundingTarget());

		comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();

		// SET COMPARISION TO REPAYMENT or PRINCIPAL
		if (isComapareWithEMI) {
			comparisionToAmount = finSchdDetails.get(iCompare).getRepayAmount();
			iCompareEMI = finSchdDetails.get(sdSize - 1).getRepayAmount();
		} else {
			comparisionToAmount = finSchdDetails.get(iCompare).getPrincipalSchd();
			iCompareEMI = finSchdDetails.get(sdSize - 1).getPrincipalSchd();
		}

		BigDecimal bd100 = BigDecimal.valueOf(100);
		FinanceStepPolicyDetail stepDetail = stepPolicyDetails.get(stepPolicyDetails.size() - 1);
		iCompareEMI = iCompareEMI.multiply(bd100).divide(stepDetail.getEmiSplitPerc(), 0, RoundingMode.HALF_DOWN);

		if (approxEMI.compareTo(iCompareEMI) == 1) {
			repayAmountLow = iCompareEMI;
			repayAmountHigh = approxEMI;
		} else {
			repayAmountLow = approxEMI;
			repayAmountHigh = iCompareEMI;
		}

		BigDecimal lastTriedEMI = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);
		int int100 = 100;
		BigDecimal diff_Low_High = BigDecimal.ZERO;

		for (int i = 0; i < 50; i++) {
			approxEMI = (repayAmountLow.add(repayAmountHigh)).divide(number2, 0, RoundingMode.HALF_DOWN);

			if (repayAmountLow.compareTo(approxEMI) == 0 || repayAmountHigh.compareTo(approxEMI) == 0) {
				break;
			}

			diff_Low_High = (repayAmountHigh.subtract(repayAmountLow)).abs();
			if (diff_Low_High.compareTo(BigDecimal.valueOf(finMain.getRoundingTarget())) <= 0) {
				break;
			}

			lastTriedEMI = approxEMI;
			// Step case for Grace Allowed
			int k = 0;
			if (DateUtility.compare(finMain.getFinStartDate(), finMain.getGrcPeriodEndDate()) != 0) {
				k = 1;
			}

			for (int j = 0; j < stepSize; j++) {
				stepDetail = stepPolicyDetails.get(j);
				stepEMI = approxEMI.multiply(stepDetail.getEmiSplitPerc()).divide(BigDecimal.valueOf(int100), 0,
						RoundingMode.HALF_DOWN);

				stepEMI = CalculationUtil.roundAmount(stepEMI, finMain.getCalRoundingMode(),
						finMain.getRoundingTarget());

				// stepEMI = round(stepEMI);
				repayInstructions.get(k + j).setRepayAmount(stepEMI);
			}

			finScheduleData = getRpyInstructDetails(finScheduleData);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);

			// Find COMPARISION AND COMPARIONTO amounts
			comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();

			// SET COMPARISION TO
			if (isComapareWithEMI) {
				comparisionToAmount = finSchdDetails.get(iCompare).getRepayAmount();
				iCompareEMI = finSchdDetails.get(sdSize - 1).getRepayAmount();
			} else {
				comparisionToAmount = finSchdDetails.get(iCompare).getPrincipalSchd();
				iCompareEMI = finSchdDetails.get(sdSize - 1).getPrincipalSchd();
			}

			if (comparisionToAmount.compareTo(comparisionAmount) == 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}

			diff_Low_High = (comparisionToAmount.subtract(comparisionAmount)).abs();
			if (diff_Low_High.compareTo(BigDecimal.valueOf(finMain.getRoundingTarget())) <= 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}

			stepDetail = stepPolicyDetails.get(stepPolicyDetails.size() - 1);
			iCompareEMI = iCompareEMI.multiply(bd100).divide(stepDetail.getEmiSplitPerc(), 0, RoundingMode.HALF_DOWN);

			if (iCompareEMI.compareTo(approxEMI) == 1) {
				repayAmountLow = approxEMI;
			} else {
				repayAmountHigh = approxEMI;
			}

		}

		// Find Nearest EMI
		BigDecimal minRepayDifference = BigDecimal.ZERO;
		BigDecimal maxRepayDifference = BigDecimal.ZERO;

		// Find COMPARISION AND COMPARIONTO amounts
		comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();

		// SET COMPARISION TO
		if (isComapareWithEMI) {
			comparisionToAmount = finSchdDetails.get(iCompare).getRepayAmount();
		} else {
			comparisionToAmount = finSchdDetails.get(iCompare).getPrincipalSchd();
		}

		if (repayAmountLow.compareTo(repayAmountHigh) != 0) {
			if (lastTriedEMI.compareTo(repayAmountLow) == 0) {
				minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				approxEMI = repayAmountHigh;
			} else {
				maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				approxEMI = repayAmountLow;
			}

			approxEMI = CalculationUtil.roundAmount(approxEMI, finMain.getCalRoundingMode(),
					finMain.getRoundingTarget());

			lastTriedEMI = approxEMI;
			// Step case for Grace Allowed
			int k = 0;
			if (DateUtility.compare(finMain.getFinStartDate(), finMain.getGrcPeriodEndDate()) != 0) {
				k = 1;
			}

			for (int j = 0; j < stepSize; j++) {
				stepDetail = stepPolicyDetails.get(j);
				stepEMI = approxEMI.multiply(stepDetail.getEmiSplitPerc()).divide(BigDecimal.valueOf(int100), 0,
						RoundingMode.HALF_DOWN);
				stepEMI = CalculationUtil.roundAmount(stepEMI, finMain.getCalRoundingMode(),
						finMain.getRoundingTarget());

				// stepEMI = round(stepEMI);
				repayInstructions.get(k + j).setRepayAmount(stepEMI);
			}

			finScheduleData = getRpyInstructDetails(finScheduleData);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);

			// Find COMPARISION AND COMPARIONTO amounts
			comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();

			// SET COMPARISION TO
			if (isComapareWithEMI) {
				comparisionToAmount = finSchdDetails.get(iCompare).getRepayAmount();
			} else {
				comparisionToAmount = finSchdDetails.get(iCompare).getPrincipalSchd();
			}

			if (lastTriedEMI.compareTo(repayAmountLow) == 0) {
				minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
			} else {
				maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
			}

			if (maxRepayDifference.compareTo(minRepayDifference) > 0) {
				approxEMI = repayAmountLow;
			} else {
				approxEMI = repayAmountHigh;
			}
		}

		// SET EQUAL REPAYMENT AMOUNT AS EFFECTIVE REPAY AMOUNT AND CALL PROCESS

		// Step case for Grace Allowed
		int k = 0;
		if (DateUtility.compare(finMain.getFinStartDate(), finMain.getGrcPeriodEndDate()) != 0) {
			k = 1;
		}

		for (int j = 0; j < stepSize; j++) {
			stepDetail = stepPolicyDetails.get(j);
			stepEMI = approxEMI.multiply(stepDetail.getEmiSplitPerc()).divide(BigDecimal.valueOf(int100), 0,
					RoundingMode.HALF_DOWN);

			stepEMI = CalculationUtil.roundAmount(stepEMI, finMain.getCalRoundingMode(), finMain.getRoundingTarget());

			// stepEMI = round(stepEMI);
			repayInstructions.get(k + j).setRepayAmount(stepEMI);
		}

		finScheduleData = getRpyInstructDetails(finScheduleData);
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Calculate Insurance Premium Amount based on Insurance Details
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData insuranceCalculation(FinScheduleData finScheduleData) {

		// If Errors Exists in calculation, return back
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return finScheduleData;
		}

		// Finance Insurance List verification
		List<FinInsurances> insuranceList = finScheduleData.getFinInsuranceList();
		if (insuranceList == null || insuranceList.isEmpty()) {
			return finScheduleData;
		}

		String rpyFrqCode = String.valueOf(finScheduleData.getFinanceMain().getRepayFrq().charAt(0));
		BigDecimal actualFinAmt = BigDecimal.ZERO;
		Date eventFromDate = finScheduleData.getFinanceMain().getEventFromDate();

		for (FinInsurances finInsurance : insuranceList) {

			// If Errors Exists in calculation, return back
			if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
				break;
			}

			// If Payment Method is other than Schedule Frequency, exclude
			// calculation
			if (!StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {
				continue;
			}

			char insFrqCode = finInsurance.getInsuranceFrq().charAt(0);
			// Frequency Factor
			int frqFactor = 1;
			int insFrqFactor = 1;

			if (rpyFrqCode.equals(FrequencyCodeTypes.FRQ_MONTHLY)) {
				frqFactor = 12;
				switch (insFrqCode) {
				case 'M':
					insFrqFactor = 1;
					break;
				case 'Q':
					insFrqFactor = 4;
					break;
				case 'H':
					insFrqFactor = 6;
					break;
				case 'Y':
					insFrqFactor = 12;
					break;
				default:
					break;
				}
			} else if (rpyFrqCode.equals(FrequencyCodeTypes.FRQ_QUARTERLY)) {
				frqFactor = 4;
				switch (insFrqCode) {
				case 'Q':
					insFrqFactor = 1;
					break;
				case 'H':
					insFrqFactor = 2;
					break;
				case 'Y':
					insFrqFactor = 4;
					break;
				default:
					break;
				}
			} else if (rpyFrqCode.equals(FrequencyCodeTypes.FRQ_HALF_YEARLY)) {
				frqFactor = 2;
				switch (insFrqCode) {
				case 'H':
					insFrqFactor = 1;
					break;
				case 'Y':
					insFrqFactor = 2;
					break;
				default:
					break;
				}
			}

			for (int i = 0; i < finInsurance.getFinSchFrqInsurances().size(); i++) {

				FinSchFrqInsurance frqInsurance = finInsurance.getFinSchFrqInsurances().get(i);
				FinanceScheduleDetail curSchd = null;
				for (int k = 0; k < finScheduleData.getFinanceScheduleDetails().size(); k++) {
					if (DateUtility.compare(finScheduleData.getFinanceScheduleDetails().get(k).getDefSchdDate(),
							frqInsurance.getInsSchDate()) == 0) {
						curSchd = finScheduleData.getFinanceScheduleDetails().get(k);
						break;
					}
				}

				if (curSchd == null) {
					finScheduleData.setErrorDetail(new ErrorDetail("SCH38",
							"Insurance Schedule details mismatch with Payment Schedule.", new String[] { " " }));
					break;
				}

				if (DateUtility.compare(curSchd.getSchDate(), eventFromDate) <= 0) {
					continue;
				}

				// Insurance Fee Amount Calculation
				BigDecimal insAmount = BigDecimal.ZERO;
				if (StringUtils.equals(finInsurance.getCalType(), InsuranceConstants.CALTYPE_PERCENTAGE)) {

					if (finInsurance.getCalOn().equals(InsuranceConstants.CALCON_OSAMT)) {
						actualFinAmt = curSchd.getBalanceForPftCal();
					} else if (finInsurance.getCalOn().equals(InsuranceConstants.CALCON_FINAMT)) {
						actualFinAmt = finScheduleData.getFinanceMain().getFinAmount();
					}

					insAmount = actualFinAmt.multiply(finInsurance.getCalPerc())
							.divide(BigDecimal.valueOf(frqFactor * 100), 0, RoundingMode.HALF_DOWN);

				} else if (StringUtils.equals(finInsurance.getCalType(), InsuranceConstants.CALTYPE_PROVIDERRATE)) {

					if (finInsurance.getCalOn().equals(InsuranceConstants.CALCON_OSAMT)) {
						actualFinAmt = curSchd.getBalanceForPftCal();
					} else if (finInsurance.getCalOn().equals(InsuranceConstants.CALCON_FINAMT)) {
						actualFinAmt = finScheduleData.getFinanceMain().getFinAmount();
					}

					insAmount = actualFinAmt.multiply(finInsurance.getInsuranceRate())
							.divide(BigDecimal.valueOf(frqFactor * 100), 0, RoundingMode.HALF_DOWN);

				} else if (StringUtils.equals(finInsurance.getCalType(), InsuranceConstants.CALTYPE_CON_AMT)) {
					insAmount = finInsurance.getAmount();
				}

				insAmount = insAmount.multiply(BigDecimal.valueOf(insFrqFactor));
				if (curSchd.getInsSchd() == null) {
					curSchd.setInsSchd(BigDecimal.ZERO);
				}
				curSchd.getInsSchd().add(insAmount);

				// Schedule Frequency Insurance
				frqInsurance.setClosingBalance(curSchd.getClosingBalance());
				frqInsurance.setAmount(insAmount);
			}
		}
		return finScheduleData;
	}

	/**
	 * Method for Calculate Advised Profit rate Details
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData advPftRateCalculation(FinScheduleData finScheduleData, Date recalFromDate,
			Date recalToDate) {

		FinanceMain finMain = finScheduleData.getFinanceMain();
		if (finScheduleData.getFinanceType() != null && !StringUtils.equals(FinanceConstants.PRODUCT_STRUCTMUR,
				finScheduleData.getFinanceType().getFinCategory())) {
			return finScheduleData;
		}

		// Setting Rates between Fromdate and Todate
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		BigDecimal recalculateRate = BigDecimal.ZERO;
		BigDecimal calInt = BigDecimal.valueOf(0.0);
		BigDecimal calIntFraction = BigDecimal.ZERO;

		BigDecimal advPftForGrcEnd = BigDecimal.ZERO;
		BigDecimal advPftBal = BigDecimal.ZERO;

		for (int i = 0; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			// Setting Rates between From date and To date
			if ((DateUtility.compare(curSchd.getSchDate(), recalFromDate) >= 0
					&& DateUtility.compare(curSchd.getSchDate(), recalToDate) <= 0) || (i == (sdSize - 1))) {

				if (curSchd.getAdvPftRate() != null) {
					recalculateRate = curSchd.getAdvPftRate();
				}

				if (StringUtils.isNotBlank(curSchd.getAdvBaseRate())) {
					if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) < 0) {
						recalculateRate = RateUtil.rates(curSchd.getAdvBaseRate(), finMain.getFinCcy(), null,
								curSchd.getAdvMargin(), curSchd.getSchDate(), null, null).getNetRefRateLoan();
					} else {
						recalculateRate = RateUtil.rates(curSchd.getAdvBaseRate(), finMain.getFinCcy(), null,
								curSchd.getAdvMargin(), curSchd.getSchDate(), null, null).getNetRefRateLoan();
					}
				}

				// Contract Rate Capping for Advised Profit Rate calculation
				if (StringUtils.isBlank(curSchd.getBaseRate())) {
					if (recalculateRate.compareTo(curSchd.getActRate()) > 0) {
						recalculateRate = curSchd.getActRate();
					}
				} else {
					if (recalculateRate.compareTo(curSchd.getCalculatedRate()) > 0) {
						recalculateRate = curSchd.getCalculatedRate();
					}
				}

				if (curSchd.getSchDate().compareTo(recalToDate) != 0
						|| curSchd.getSchDate().compareTo(finMain.getMaturityDate()) == 0) {
					curSchd.setAdvCalRate(recalculateRate);
				}

				// For First Term there is No profit calculation
				if (i == 0) {
					continue;
				}

				FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(i - 1);

				// Advised profit amount calculation
				if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {
					calInt = CalculationUtil.calInterest(prvSchd.getSchDate(), curSchd.getSchDate(),
							curSchd.getBalanceForPftCal(), prvSchd.getPftDaysBasis(), prvSchd.getAdvCalRate());

					calInt = calInt.add(calIntFraction);
					calIntFraction = calInt.subtract(round(calInt));
					calInt = round(calInt);
				} else {
					calInt = BigDecimal.ZERO;
				}

				if (calInt.compareTo(curSchd.getProfitSchd()) > 0) {
					calIntFraction = calIntFraction.add(calInt.subtract(curSchd.getProfitSchd()));
					calInt = curSchd.getProfitSchd();
				}

				curSchd.setAdvProfit(BigDecimal.ZERO);
				curSchd.setAdvRepayAmount(BigDecimal.ZERO);
				advPftForGrcEnd = advPftForGrcEnd.add(calInt);

				// Reset Advised Profit Amount data setting
				if (curSchd.getSchdMethod().equals(CalculationConstants.SCHMTHD_NOPAY)) {
					curSchd.setAdvProfit(BigDecimal.ZERO);
					curSchd.setAdvRepayAmount(BigDecimal.ZERO);

				} else if (curSchd.getSchdMethod().equals(CalculationConstants.SCHMTHD_EQUAL)
						|| curSchd.getSchdMethod().equals(CalculationConstants.SCHMTHD_PRI_PFT)) {

					if (calInt.compareTo(curSchd.getProfitSchd()) > 0) {
						advPftBal = advPftBal.add(calInt);
						curSchd.setAdvProfit(BigDecimal.ZERO);
					} else {
						curSchd.setAdvProfit(calInt.add(advPftBal));
						advPftBal = BigDecimal.ZERO;
					}

					curSchd.setAdvRepayAmount(curSchd.getAdvProfit().add(curSchd.getPrincipalSchd()));

				} else if (curSchd.getSchdMethod().equals(CalculationConstants.SCHMTHD_PRI)) {
					curSchd.setAdvProfit(BigDecimal.ZERO);
					curSchd.setAdvRepayAmount(curSchd.getPrincipalSchd());

				} else if (curSchd.getSchdMethod().equals(CalculationConstants.SCHMTHD_PFT)) {
					curSchd.setAdvProfit(calInt);
					curSchd.setAdvRepayAmount(curSchd.getAdvProfit());
				} else if (curSchd.getSchdMethod().equals(CalculationConstants.SCHMTHD_GRCENDPAY)
						&& DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) == 0) {
					curSchd.setAdvProfit(advPftForGrcEnd);
					curSchd.setAdvRepayAmount(curSchd.getAdvProfit());
					advPftForGrcEnd = BigDecimal.ZERO;
				}
			}
		}

		return finScheduleData;
	}

	private FinScheduleData setRecalAttributes(FinScheduleData finScheduleData, String recalPurpose,
			BigDecimal newDisbAmount, BigDecimal chgAmount) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int sdSize = finSchdDetails.size();

		finMain.setCompareToExpected(false);
		finMain.setCompareExpectedResult(BigDecimal.ZERO);
		finMain.setCalculateRepay(true);
		finMain.setEqualRepay(true);
		finMain.setIndexMisc(0);
		finMain.setMiscAmount(BigDecimal.ZERO);

		String recaltype = finMain.getRecalType();
		int adjTerms = finMain.getAdjTerms();
		int iOldMDT = finSchdDetails.size() - 1;
		boolean resetRpyInstruction = true;

		// Force set recaltype to TILLMDT. TILLDATE comparision will happen with
		// closing balance, which gives wrong results for last record.
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLDATE)) {
			if (finMain.getRecalToDate().equals(finSchdDetails.get(sdSize - 1).getSchDate())) {
				recaltype = CalculationConstants.RPYCHG_TILLMDT;
				finMain.setRecalType(recaltype);
			}
		}

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADDTERM)
				|| StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADDRECAL)) {

			finScheduleData = procAddTerm(finScheduleData, adjTerms, false);

			// If error return error message
			if (finScheduleData.getErrorDetails().size() > 0) {
				return finScheduleData;
			}

			finMain = finScheduleData.getFinanceMain();
			finSchdDetails = finScheduleData.getFinanceScheduleDetails();
			sdSize = finSchdDetails.size();

			// Set Recalculation Start and End Dates
			if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADDRECAL)) {
				finMain.setRecalToDate(finSchdDetails.get(sdSize - 1).getSchDate());
			} else {

				finMain.setRecalFromDate(finSchdDetails.get(iOldMDT + 1).getSchDate());
				finMain.setRecalToDate(finSchdDetails.get(sdSize - 1).getSchDate());
				if (StringUtils.equals(recalPurpose, PROC_UNPLANEMIH)) {
					resetRpyInstruction = false;
				}
			}

		} else if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT)) {
			finMain.setCalculateRepay(false);
			finMain.setEqualRepay(false);

			finMain.setRecalFromDate(finMain.getMaturityDate());
			finMain.setRecalToDate(finMain.getMaturityDate());

		} else if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLMDT)) {
			finMain.setRecalToDate(finSchdDetails.get(sdSize - 1).getSchDate());
		}

		// Set maturity Date schedule amount
		if (StringUtils.equals(CalculationConstants.SCHMTHD_EQUAL, finSchdDetails.get(sdSize - 1).getSchdMethod())) {
			finMain.setCompareExpectedResult(finSchdDetails.get(sdSize - 1).getRepayAmount());
		} else {
			finMain.setCompareExpectedResult(finSchdDetails.get(sdSize - 1).getPrincipalSchd());
		}

		Date recalFromDate = finMain.getRecalFromDate();
		Date recalToDate = finMain.getRecalToDate();
		String schdMethod = finMain.getRecalSchdMethod();

		// Set RecalSchdMethod
		finScheduleData = getSchdMethod(finScheduleData);
		schdMethod = finMain.getRecalSchdMethod();

		// Set Repayment Instructions as 1 for recalFromDate to recalToDate.
		// Reason for not setting zero is to avoid deleting future zero
		// instructions
		if (!StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT) && resetRpyInstruction) {
			finScheduleData = setRpyInstructDetails(finScheduleData, recalFromDate, recalToDate, BigDecimal.ONE,
					schdMethod);
		} else if (!resetRpyInstruction) {
			finMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			finMain.setCalculateRepay(false);
		}

		finScheduleData.setFinanceMain(finMain);
		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData getSchdMethod(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		String schdMethod = finMain.getRecalSchdMethod();

		if (StringUtils.equals(schdMethod, PennantConstants.List_Select)) {
			schdMethod = "";
		}

		if (!StringUtils.isBlank(schdMethod)) {
			return finScheduleData;
		}

		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();
		int risize = repayInstructions.size();

		Date schdMethodDate = finMain.getRecalFromDate();

		// Set date from which schedule method can be taken
		if (DateUtility.compare(schdMethodDate, finMain.getGrcPeriodEndDate()) <= 0) {
			schdMethodDate = finMain.getGrcPeriodEndDate();

			// Add one day to bring comparison date to repayment period
			schdMethodDate = DateUtility.addDays(schdMethodDate, 1);
		}

		// Find Schedule Method used for existing instruction
		for (int i = 0; i < risize; i++) {
			schdMethod = repayInstructions.get(i).getRepaySchdMethod();

			if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), schdMethodDate) >= 0) {
				break;
			}
		}

		// If it is first time(cases applicable for OD) , then there are no
		// Repay Instructions
		if (StringUtils.isBlank(schdMethod)) {
			schdMethod = finMain.getScheduleMethod();
		}

		finMain.setRecalSchdMethod(schdMethod);

		return finScheduleData;

	}

	private FinScheduleData getCurPerodDates(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		boolean isFromDateSet = false;
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = finSchdDetails.size();
		Date schdDate = new Date();

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (DateUtility.compare(schdDate, finMain.getEventFromDate()) <= 0) {
				continue;
			}

			// SET RECAL FROMDATE
			if (!isFromDateSet) {
				if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
					finMain.setRecalFromDate(schdDate);
					finMain.setRecalToDate(schdDate);
					isFromDateSet = true;
				}
			}

			if (DateUtility.compare(schdDate, finMain.getEventToDate()) < 0) {
				continue;
			}

			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				finMain.setRecalToDate(schdDate);
				break;
			}

		}

		return finScheduleData;

	}

	/**
	 * Method to Calculate the Overdraft Schedule Details
	 */
	private FinScheduleData buildOverdraftSchd(FinScheduleData orgFinScheduleData) {
		logger.debug("Entering");

		Cloner cloner = new Cloner();
		FinScheduleData finScheduleData = cloner.deepClone(orgFinScheduleData);
		FinanceMain finMain = finScheduleData.getFinanceMain();

		// Overdraft Maintenance Changes for calculation
		BigDecimal prvDropLineAmount = BigDecimal.ZERO;
		BigDecimal incrLimit = BigDecimal.ZERO;
		Date startCalFrom = finMain.getFinStartDate();
		boolean inclStartDate = false;
		boolean inclEndDate = true;
		List<OverdraftScheduleDetail> oldOverdraftList = new ArrayList<>();
		BigDecimal totalOSLimit = BigDecimal.ZERO;
		OverdraftScheduleDetail prvODSchd = null;

		if (DateUtility.compare(finMain.getEventFromDate(), finMain.getFinStartDate()) == 0) {
			totalOSLimit = finMain.getFinAssetValue();
			inclStartDate = true;
			if (StringUtils.isNotEmpty(finMain.getDroplineFrq())) {
				if (finMain.getFirstDroplineDate() != null) {
					startCalFrom = finMain.getFirstDroplineDate();
				} else {
					startCalFrom = FrequencyUtil.getNextDate(finMain.getDroplineFrq(), 1, startCalFrom, "A", false)
							.getNextFrequencyDate();
				}
			}

			// Adding Start date Overdraft Schedule
			OverdraftScheduleDetail curODSchd = new OverdraftScheduleDetail();
			curODSchd.setODLimit(finMain.getFinAssetValue());
			curODSchd.setDroplineDate(finMain.getFinStartDate());
			if (StringUtils.isEmpty(finMain.getRepayBaseRate())) {
				curODSchd.setActualRate(finMain.getRepayProfitRate());
			} else {
				curODSchd.setBaseRate(finMain.getRepayBaseRate());
				curODSchd.setSplRate(finMain.getRepaySpecialRate());
				curODSchd.setMargin(finMain.getRepayMargin());
			}

			if (StringUtils.isEmpty(curODSchd.getBaseRate())) {
				curODSchd.setDroplineRate(finMain.getRepayProfitRate());
			} else {
				RateDetail rateDetail = RateUtil.rates(finMain.getRepayBaseRate(), finMain.getFinCcy(),
						finMain.getRepaySpecialRate(), finMain.getRepayMargin(), curODSchd.getDroplineDate(),
						finMain.getRpyMinRate(), finMain.getRpyMaxRate());
				if (rateDetail.getErrorDetails() == null) {
					curODSchd.setDroplineRate(rateDetail.getNetRefRateLoan());
				}
			}
			prvODSchd = curODSchd;
			oldOverdraftList.add(curODSchd);

		} else {
			boolean limitIncrDateFound = false;
			startCalFrom = finMain.getEventFromDate();
			for (OverdraftScheduleDetail curODSchd : finScheduleData.getOverdraftScheduleDetails()) {
				if (DateUtility.compare(curODSchd.getDroplineDate(), finMain.getEventFromDate()) > 0) {
					if (StringUtils.isNotEmpty(finMain.getDroplineFrq())) {
						startCalFrom = FrequencyUtil
								.getNextDate(finMain.getDroplineFrq(), 1, prvODSchd.getDroplineDate(), "A", false)
								.getNextFrequencyDate();
						if (DateUtility.compare(startCalFrom, finMain.getMaturityDate()) >= 0) {
							startCalFrom = finMain.getMaturityDate();
						}
						inclStartDate = true;
					}
					break;
				}

				// Calculate sum of all Limit drop lines before Current
				// Application date
				prvODSchd = curODSchd;
				prvDropLineAmount = prvDropLineAmount.add(curODSchd.getLimitDrop());
				incrLimit = finMain.getFinAssetValue().subtract(prvDropLineAmount).subtract(curODSchd.getODLimit());
				if (DateUtility.compare(curODSchd.getDroplineDate(), finMain.getEventFromDate()) == 0
						&& incrLimit.compareTo(BigDecimal.ZERO) > 0) {
					curODSchd.setLimitIncreaseAmt(curODSchd.getLimitIncreaseAmt().add(incrLimit));
					curODSchd.setODLimit(curODSchd.getODLimit().add(incrLimit));
					limitIncrDateFound = true;
				}
				totalOSLimit = curODSchd.getODLimit();
				oldOverdraftList.add(curODSchd);
			}

			if (DateUtility.compare(startCalFrom, finMain.getFirstDroplineDate()) < 0) {
				startCalFrom = finMain.getFirstDroplineDate();
				inclStartDate = true;
			}

			// Adding New Overdraft Schedule, if not Found in the existing list
			if (!limitIncrDateFound && incrLimit.compareTo(BigDecimal.ZERO) > 0) {
				OverdraftScheduleDetail newOdSchd = new OverdraftScheduleDetail();
				newOdSchd.setActualRate(prvODSchd.getActualRate());
				newOdSchd.setBaseRate(prvODSchd.getBaseRate());
				newOdSchd.setSplRate(prvODSchd.getSplRate());
				newOdSchd.setMargin(prvODSchd.getMargin());
				newOdSchd.setDroplineRate(prvODSchd.getDroplineRate());
				newOdSchd.setDroplineDate(finMain.getEventFromDate());
				newOdSchd.setLimitIncreaseAmt(incrLimit);
				newOdSchd.setLimitDrop(BigDecimal.ZERO);
				newOdSchd.setODLimit(prvODSchd.getODLimit().add(incrLimit));
				oldOverdraftList.add(newOdSchd);
				totalOSLimit = prvODSchd.getODLimit().add(incrLimit);
				prvODSchd = newOdSchd;
			}
		}

		// if Overdraft Dropline Not Exists
		if (StringUtils.isEmpty(finMain.getDroplineFrq())) {

			// Creating Expiry Schedule
			OverdraftScheduleDetail curODSchd = new OverdraftScheduleDetail();
			curODSchd.setDroplineDate(finMain.getMaturityDate());
			curODSchd.setActualRate(prvODSchd.getActualRate());
			curODSchd.setBaseRate(prvODSchd.getBaseRate());
			curODSchd.setSplRate(prvODSchd.getSplRate());
			curODSchd.setMargin(prvODSchd.getMargin());
			curODSchd.setDroplineRate(prvODSchd.getDroplineRate());
			curODSchd.setLimitIncreaseAmt(BigDecimal.ZERO);
			curODSchd.setLimitDrop(prvODSchd.getODLimit());
			curODSchd.setODLimit(BigDecimal.ZERO);
			oldOverdraftList.add(curODSchd);

			finScheduleData.setOverdraftScheduleDetails(sortOverdraftSchedules(oldOverdraftList));

			logger.debug("Leaving");
			return finScheduleData;
		}

		// Building Schedule terms with Dates
		FrequencyDetails frequencyDetails = FrequencyUtil.getTerms(finMain.getDroplineFrq(), startCalFrom,
				finMain.getMaturityDate(), inclStartDate, inclEndDate);

		// Validate Frequency Schedule Details
		if (frequencyDetails.getErrorDetails() != null) {
			logger.warn("Schedule Error: on condition --->  Validate frequency:" + finMain.getDroplineFrq());
			orgFinScheduleData.setErrorDetail(frequencyDetails.getErrorDetails());
			return orgFinScheduleData;
		}

		// Adding Overdraft Start date to the Schedule terms
		List<Date> odSchdDateList = new ArrayList<>();
		Calendar calendar = null;
		// Rendering all Overdraft schedule details as per Frequency
		for (int i = 0; i < frequencyDetails.getScheduleList().size(); i++) {
			calendar = frequencyDetails.getScheduleList().get(i);
			odSchdDateList.add(calendar.getTime());
		}
		Collections.sort(odSchdDateList);

		// calculating Limit Drop amount based on Terms (Constant Drop line)
		BigDecimal limitDrop = totalOSLimit.divide(new BigDecimal(frequencyDetails.getTerms()), 0,
				RoundingMode.HALF_DOWN);

		// Setting Overdraft Schedule details
		for (int i = 0; i < odSchdDateList.size(); i++) {

			OverdraftScheduleDetail curODSchd = new OverdraftScheduleDetail();
			curODSchd.setDroplineDate(DateUtility
					.getDBDate(DateUtility.formatDate(odSchdDateList.get(i), PennantConstants.DBDateFormat)));
			curODSchd.setActualRate(prvODSchd.getActualRate());
			curODSchd.setBaseRate(prvODSchd.getBaseRate());
			curODSchd.setSplRate(prvODSchd.getSplRate());
			curODSchd.setMargin(prvODSchd.getMargin());

			// Dropline Rate
			if (StringUtils.isEmpty(curODSchd.getBaseRate())) {
				curODSchd.setDroplineRate(prvODSchd.getActualRate());
			} else {
				RateDetail rateDetail = RateUtil.rates(finMain.getRepayBaseRate(), finMain.getFinCcy(),
						finMain.getRepaySpecialRate(), finMain.getRepayMargin(), curODSchd.getDroplineDate(),
						finMain.getRpyMinRate(), finMain.getRpyMaxRate());
				if (rateDetail.getErrorDetails() == null) {
					curODSchd.setDroplineRate(rateDetail.getNetRefRateLoan());
				}
			}

			// If Last Overdraft schedule or Limit Expiry
			if (i == (odSchdDateList.size() - 1)) {
				curODSchd.setLimitDrop(prvODSchd.getODLimit());
				curODSchd.setODLimit(BigDecimal.ZERO);
			} else {
				curODSchd.setODLimit(prvODSchd.getODLimit().subtract(limitDrop));
				curODSchd.setLimitDrop(limitDrop);
			}

			oldOverdraftList.add(curODSchd);
			prvODSchd = curODSchd;
		}

		finScheduleData.setOverdraftScheduleDetails(sortOverdraftSchedules(oldOverdraftList));

		logger.debug("Leaving");
		return finScheduleData;

	}

	public FinScheduleData fetchRatesHistory(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = finSchdDetails.size();

		finScheduleData.getBaseRates().clear();
		finScheduleData.getSplRates().clear();
		// -------------------------------------------------------------------------------------------
		// FIND BASE RATES AND SPECIAL RATES USED IN GRACE PERIOD
		// -------------------------------------------------------------------------------------------
		List<String> baseRateCodes = new ArrayList<>();
		List<String> specialRateCodes = new ArrayList<>();

		// Load Base Rates and Special Rates
		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);

			if (StringUtils.isBlank(curSchd.getBaseRate())) {
				continue;
			}

			// Add Base Rate
			Collections.sort(baseRateCodes);
			boolean isBaseRateFound = false;
			for (int j = 0; j < baseRateCodes.size(); j++) {
				if (StringUtils.equals(baseRateCodes.get(j), curSchd.getBaseRate())) {
					isBaseRateFound = true;
					break;
				}
			}

			if (!isBaseRateFound) {
				baseRateCodes.add(curSchd.getBaseRate());
			}

			// Add Special rate
			if (StringUtils.isBlank(curSchd.getSplRate())) {
				continue;
			}

			Collections.sort(specialRateCodes);
			boolean isSpecialRateFound = false;
			for (int j = 0; j < specialRateCodes.size(); j++) {
				if (StringUtils.equals(specialRateCodes.get(j), curSchd.getSplRate())) {
					isSpecialRateFound = true;
					break;
				}
			}

			if (!isSpecialRateFound) {
				specialRateCodes.add(curSchd.getSplRate());
			}

		}

		// If no base rates are used then return received data
		if (baseRateCodes.size() == 0) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		// Load Base Rates History to the working bean structure
		for (int i = 0; i < baseRateCodes.size(); i++) {
			List<BaseRate> baseRatesHist = getBaseRateDAO().getBaseRateHistByType(baseRateCodes.get(i),
					finMain.getFinCcy(), finMain.getFinStartDate());

			for (int j = 0; j < baseRatesHist.size(); j++) {
				BaseRate baseRate = new BaseRate();
				baseRate = baseRatesHist.get(j);
				finScheduleData.getBaseRates().add(baseRate);
			}
		}

		finScheduleData.setBaseRates(sortBaseRateHistrates(finScheduleData.getBaseRates()));

		// If no special rates are used then return received data with base
		// rates history
		if (specialRateCodes.size() == 0) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		// Load Special Rates History to the working bean structure
		for (int i = 0; i < specialRateCodes.size(); i++) {
			List<SplRate> splRatesHist = getSplRateDAO().getSplRateHistByType(specialRateCodes.get(i),
					finMain.getFinStartDate());

			for (int j = 0; j < splRatesHist.size(); j++) {
				SplRate splRate = new SplRate();
				splRate = splRatesHist.get(j);
				finScheduleData.getSplRates().add(splRate);
			}
		}

		finScheduleData.setSplRates(sortSplRateHistrates(finScheduleData.getSplRates()));

		logger.debug("Leaving");
		return finScheduleData;

	}

	private FinScheduleData maintainPOSStep(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceStepPolicyDetail> stepPolicyDetails = finScheduleData.getStepPolicyDetails();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int sdSize = finSchdDetails.size();
		int stepSize = stepPolicyDetails.size();
		int iTerms = 0;

		BigDecimal openPrincipal = BigDecimal.ZERO;
		BigDecimal openOSBalance = BigDecimal.ZERO;
		BigDecimal closeBalPercentage = BigDecimal.ONE;

		finScheduleData = fetchRatesHistory(finScheduleData);
		finScheduleData = fetchGraceCurRates(finScheduleData);
		finScheduleData = fetchRepayCurRates(finScheduleData);
		finScheduleData = getRpyInstructDetails(finScheduleData);
		finScheduleData = graceSchdCal(finScheduleData);

		// If schedule method is interest only payment or no payment then
		// stepping of EMI/Equated Principal not required
		if (finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PFT)
				&& !finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_NOPAY)) {
			finScheduleData = repaySchdCal(finScheduleData, false);
			finScheduleData = setFinanceTotals(finScheduleData);
			logger.debug("Leaving");
			return finScheduleData;
		}

		// Find Opening balance for step calculation
		for (int i = 0; i < sdSize; i++) {
			if (StringUtils.equals(finSchdDetails.get(i).getSpecifier(),
					CalculationConstants.SCH_SPECIFIER_GRACE_END)) {
				openPrincipal = finSchdDetails.get(i).getClosingBalance();
				openOSBalance = openPrincipal;
				finMain.setIndexStart(i + 1);
				finMain.setIndexEnd(sdSize - 1);
				break;
			}
		}

		BigDecimal stepOpenBal = openOSBalance;
		BigDecimal stepAmount = BigDecimal.ZERO;

		for (int i = 0; i < stepSize; i++) {
			FinanceStepPolicyDetail stepDetail = stepPolicyDetails.get(i);

			closeBalPercentage = closeBalPercentage.subtract(stepDetail.getEmiSplitPerc().divide(new BigDecimal(100)));
			finMain.setCompareExpectedResult(round(openPrincipal.multiply(closeBalPercentage)));

			iTerms = 0;

			for (int j = (finMain.getIndexStart()); j < sdSize; j++) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(j);

				if (!curSchd.isRepayOnSchDate()) {
					continue;
				}

				iTerms = iTerms + 1;
				finMain.setIndexEnd(j);

				if (iTerms != stepDetail.getInstallments()) {
					continue;
				}

				if (finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_EQUAL)) {
					stepAmount = approxPMT(finMain, curSchd.getCalculatedRate(), iTerms, stepOpenBal,
							finMain.getCompareExpectedResult(), 0);
				} else if (finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI_PFT)
						|| finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI)) {
					stepAmount = stepOpenBal.subtract(finMain.getCompareExpectedResult())
							.divide(BigDecimal.valueOf(iTerms), 0, RoundingMode.HALF_DOWN);
				}

				stepAmount = CalculationUtil.roundAmount(stepAmount, finMain.getCalRoundingMode(),
						finMain.getRoundingTarget());
				finScheduleData = fetchRepayCurRates(finScheduleData);
				finMain.setMiscAmount(stepAmount);
				finScheduleData = targetPriOSBal(finScheduleData, iTerms, false);
				stepOpenBal = finScheduleData.getFinanceScheduleDetails().get(j).getClosingBalance();
				finMain.setIndexStart(j + 1);

				break;

			}

		}

		finScheduleData = setFinanceTotals(finScheduleData);
		logger.debug("Leaving");
		return finScheduleData;

	}
	
	/**
	 * Method for applying Principal Holiday for the Payments
	 */
	private FinScheduleData principalHoliday(FinScheduleData finScheduleData, BigDecimal earlyPayAmt) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		String schdMethod = finMain.getRecalSchdMethod();
		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.isBlank(schdMethod)) {
			for (int i = 0; i < risize; i++) {
				if (repayInstructions.get(i).getRepayDate().compareTo(evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
				}

				if (StringUtils.isBlank(finMain.getRecalSchdMethod())) {
					finMain.setRecalSchdMethod(finMain.getScheduleMethod());
				}

				if (repayInstructions.get(i).getRepayDate().compareTo(evtFromDate) >= 0) {
					break;
				}
			}
		}

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		int prvIndex = 0;
		boolean isRepaymentFoundInSD = false;

		Date schdDate = new Date();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		BigDecimal prvSchdPrincipal = BigDecimal.ZERO;
		BigDecimal remainingPrincipal = earlyPayAmt;

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (schdDate.compareTo(evtFromDate) > 0) {
				break;
			}

			if (schdDate.compareTo(evtFromDate) == 0) {
				//To make sure the flags are TRUE when repayment happens
				curSchd.setPftOnSchDate(true);
				curSchd.setRepayOnSchDate(true);
				isRepaymentFoundInSD = true;
				prvSchdPrincipal = curSchd.getPrincipalSchd();
				break;
			}

			prvIndex = i;
		}

		if (!isRepaymentFoundInSD) {
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, prvIndex);
			curSchd = finSchdDetails.get(prvIndex + 1);
			curSchd.setPftOnSchDate(true);
			curSchd.setRepayOnSchDate(true);
			curSchd.setRepayAmount(earlyPayAmt);
			schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
		}

		finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtToDate, earlyPayAmt, schdMethod);
		remainingPrincipal = remainingPrincipal.subtract(prvSchdPrincipal);

		sdSize = finScheduleData.getFinanceScheduleDetails().size();
		BigDecimal newSchdPri = BigDecimal.ZERO;
		boolean zeroInstAdded = false;
		Date zeroCalStartDate = null;

		for (int i = prvIndex; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);

			if (curSchd.getSchDate().compareTo(evtToDate) <= 0) {
				continue;
			}

			//If not repayment schedule no change
			if (!curSchd.isRepayOnSchDate()) {
				continue;
			}

			//Already paid then no change
			if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
				continue;
			}

			//Already sent for presentment
			// SIVA : Schedule should be recalculated even Schedule Term is in Presentment Process
			/*if (curSchd.getPresentmentId() != 0) {
				continue;
			}*/

			schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
			if (remainingPrincipal.compareTo(curSchd.getPrincipalSchd()) >= 0) {

				Date fromDate = curSchd.getSchDate();
				if(zeroInstAdded){
					fromDate = zeroCalStartDate;
				}
				finScheduleData = setRpyInstructDetails(finScheduleData, fromDate, curSchd.getSchDate(),
						newSchdPri, schdMethod);
				remainingPrincipal = remainingPrincipal.subtract(curSchd.getPrincipalSchd());
				if(!zeroInstAdded){
					zeroCalStartDate = curSchd.getSchDate();
				}
				zeroInstAdded = true;

				if (remainingPrincipal.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			} else {
				newSchdPri = curSchd.getPrincipalSchd().subtract(remainingPrincipal);
				finScheduleData = setRpyInstructDetails(finScheduleData, curSchd.getSchDate(), curSchd.getSchDate(),
						newSchdPri, schdMethod);
				break;
			}
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);
		finMain.setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public static BaseRateDAO getBaseRateDAO() {
		return baseRateDAO;
	}

	public void setBaseRateDAO(BaseRateDAO baseRateDAO) {
		ScheduleCalculator.baseRateDAO = baseRateDAO;
	}

	public static SplRateDAO getSplRateDAO() {
		return splRateDAO;
	}

	public void setSplRateDAO(SplRateDAO splRateDAO) {
		ScheduleCalculator.splRateDAO = splRateDAO;
	}

	public static IRRFeeTypeDAO getiRRFeeTypeDAO() {
		return iRRFeeTypeDAO;
	}

	public static void setiRRFeeTypeDAO(IRRFeeTypeDAO iRRFeeTypeDAO) {
		ScheduleCalculator.iRRFeeTypeDAO = iRRFeeTypeDAO;
	}

	public static IRRFinanceTypeDAO getIrrFinanceTypeDAO() {
		return irrFinanceTypeDAO;
	}

	public static void setIrrFinanceTypeDAO(IRRFinanceTypeDAO irrFinanceTypeDAO) {
		ScheduleCalculator.irrFinanceTypeDAO = irrFinanceTypeDAO;
	}

}