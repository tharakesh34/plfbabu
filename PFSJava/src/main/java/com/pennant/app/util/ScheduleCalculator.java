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
 *******************************************************************************************************
 *                                 FILE HEADER                                              			*
 *******************************************************************************************************
 *
 * FileName    		:  ScheduleCalculator.java															*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES															*
 *                                                                  
 * Creation Date    :  26-04-2011																		*
 *                                                                  
 * Modified Date    :  10-05-2018																		*
 *                                                                  
 * Description 		:												 									*                                 
 *                                                                                          
 ********************************************************************************************************
 * Date             Author                   Version      Comments                          			*
 ********************************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            			*	 
 *                                                                                          			* 
 * 10-05-2018       Satya	                 0.2          PSD - Ticket : 126189							*
 * 														  While doing Add Disbursement getting 			*
 * 														  ArthemeticException in AccrualService due to  *
 * 														  NoofDays is ZERO in newly added Schedule 		*
 * 01-08-2018  		Mangapathi				 0.3		  PSD - Ticket : 125445, 125588					*
 * 														  Mail Sub : Freezing Period, Dt : 30-May-2018  *
 *                                                        To address Freezing period case when schedule *
 *														  term is in Presentment. 						*                                  			* 
 *            
 * 05-12-2018		Pradeep Varma			 0.4		  Schedules sent for presentment should	and     * 
 *                                                        waiting for fate should be untouched for any  * 
 *                                                        schedule change								* 
 * 05-12-2018		Pradeep Varma			 0.5		  Interest should not be left for future        * 
 *                                                        adjustments based on loan type flag           * 
 *                                                        schedule change								*  
 * 05-12-2018		Pradeep Varma			 0.6		  Adjut Terms while Rate Change				    *
 *                                                                                          			*
 *                                                                                          			* 
 ********************************************************************************************************
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.FrequencyDetails;
import com.pennant.app.model.RateDetail;
import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.dao.applicationmaster.SplRateDAO;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.rits.cloning.Cloner;

public class ScheduleCalculator {
	private static final Logger logger = LogManager.getLogger(ScheduleCalculator.class);

	private FinScheduleData finScheduleData;
	private static BaseRateDAO baseRateDAO;
	private static SplRateDAO splRateDAO;

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
	public static final String PROC_CHANGETDS = "changeTDS";
	public static final String PROC_REBUILDSCHD = "reBuildSchd";
	public static final String PROC_ADDDATEDSCHEDULE = "procAddDatedSchedule";
	public static final String PROC_CALDREMIHOLIDAYS = "procCalDREMIHolidays";
	public static final String PROC_INSTBASEDSCHEDULE = "procInstBasedSchedule";
	public static final String PROC_RESTRUCTURE = "procRestructure";

	public ScheduleCalculator() {
		super();
	}

	/*
	 * ######################################################################### ###################
	 * 
	 * 
	 * ######################################################################### ###################
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

	public static FinScheduleData reBuildSchd(FinScheduleData finScheduleData, String schdMethod) {
		return new ScheduleCalculator(PROC_REBUILDSCHD, finScheduleData, BigDecimal.ZERO, schdMethod)
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

	public static FinScheduleData calDREMIHolidays(FinScheduleData finScheduleData) {
		return (new ScheduleCalculator(PROC_CALDREMIHOLIDAYS, finScheduleData)).getFinScheduleData();
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

	// Ticket ID:124631,TDS Round Off
	public static FinScheduleData procReCalTDSAmount(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(finScheduleData, PROC_CHANGETDS).getFinScheduleData();
	}

	public static FinScheduleData addDatedSchedule(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(PROC_ADDDATEDSCHEDULE, finScheduleData, BigDecimal.ZERO).getFinScheduleData();
	}

	//InstBasedProcess
	public static FinScheduleData instBasedSchedule(FinScheduleData finScheduleData, BigDecimal amount,
			boolean utilizeGrcEndDisb, boolean isLoanNotApproved, FinanceDisbursement finDisb, boolean feeAmtInclude) {
		return new ScheduleCalculator(PROC_INSTBASEDSCHEDULE, finScheduleData, amount, utilizeGrcEndDisb,
				isLoanNotApproved, finDisb, feeAmtInclude).getFinScheduleData();
	}

	public static FinScheduleData procRestructure(FinScheduleData finScheduleData) {
		return (new ScheduleCalculator(PROC_RESTRUCTURE, finScheduleData)).getFinScheduleData();
	}

	// Constructors
	private ScheduleCalculator(String method, FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finScheduleData.getFinanceMain().setRecalIdx(-1);
		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setAppDate(SysParamUtil.getAppDate());

		// re generate original schedule from Flexi Schedule
		if (finMain.isAlwFlexi() && finMain.isChgDropLineSchd()) {

			rebuildOrgSchdFromFlexiSchd(finScheduleData);
		}

		if (StringUtils.equals(method, PROC_CHANGEGRACEEND)) {
			setFinScheduleData(procChangeGraceEnd(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_REAGEH)) {
			setFinScheduleData(procReAgeH(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_POSTPONE)) {
			setFinScheduleData(procPostpone(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_CALDREMIHOLIDAYS)) {
			this.setFinScheduleData(procCalDREMIHolidays(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_RESTRUCTURE)) {
			this.setFinScheduleData(buildRestructure(finScheduleData));
		}

		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal desiredPftAmount) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setRecalIdx(-1);
		finMain.setAppDate(SysParamUtil.getAppDate());

		// re generate original schedule from Flexi Schedule
		if (finMain.isAlwFlexi() && finMain.isChgDropLineSchd()) {

			rebuildOrgSchdFromFlexiSchd(finScheduleData);
		}

		if (StringUtils.equals(method, PROC_GETCALSCHD)) {

			finMain.setRateChange(true);

			if (AdvanceType.hasAdvEMI(finMain.getAdvType()) && AdvanceStage.hasFrontEnd(finMain.getAdvStage())
					&& finScheduleData.getFinanceMain().getAdvTerms() > 0) {
				finScheduleData.getFinanceMain().setAdjustClosingBal(true);
			}

			setFinScheduleData(procGetCalSchd(finScheduleData));
			finMain.setRateChange(false);

			if (AdvanceType.hasAdvEMI(finMain.getAdvType()) && AdvanceStage.hasFrontEnd(finMain.getAdvStage())
					&& finScheduleData.getFinanceMain().getAdvTerms() > 0) {
				finScheduleData.getFinanceMain().setAdjustClosingBal(true);
			}
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

		if (StringUtils.equals(method, PROC_ADDDATEDSCHEDULE)) {
			setFinScheduleData(procAddDatedSchedule(finScheduleData));
		}

		if (StringUtils.equals(method, PROC_RESTRUCTURE)) {
			this.setFinScheduleData(buildRestructure(finScheduleData));
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Schedule calculation to get the Total Desired Profit by including Planned Deferment Terms
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData procPlanDeferPft(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finScheduleData.getFinanceMain().setCpzAtGraceEnd(false);
		finScheduleData.getFinanceMain().setRecalIdx(-1);
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
	 * Method for Processing Schedule calculation to get the Total Desired Profit by including Planned Deferment Terms
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData procDownpaySchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		Cloner cloner = new Cloner();
		FinScheduleData dpScheduleData = cloner.deepClone(finScheduleData);
		FinanceMain finMain = dpScheduleData.getFinanceMain();
		finMain.setRecalIdx(-1);

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
		finMain.setNoOfGrcSteps(0);

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

		finScheduleData.getFinanceMain().setRecalIdx(-1);
		finScheduleData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());

		if (StringUtils.equals(method, PROC_CHANGERATE)) {
			finScheduleData.getFinanceMain().setRateChange(true);

			FinanceMain finMain = finScheduleData.getFinanceMain();

			// re generate original schedule from Flexi Schedule
			if (finMain.isAlwFlexi() && finMain.isChgDropLineSchd()) {

				rebuildOrgSchdFromFlexiSchd(finScheduleData);
			}

			finScheduleData = procChangeRate(finScheduleData, baseRate, splRate, mrgRate, calculatedRate, isCalSchedule,
					false);

			finScheduleData.getFinanceMain().setRateChange(false);
			if (StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_STEPPOS)) {
				finScheduleData = maintainPOSStep(finScheduleData);
			}

			// Advised Profit Rate Calculation Process
			finScheduleData = advPftRateCalculation(finScheduleData, finMain.getEventFromDate(),
					finMain.getEventToDate());

			finMain.setScheduleMaintained(true);
			setFinScheduleData(finScheduleData);

		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal amount, String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setRecalIdx(-1);
		finMain.setAppDate(SysParamUtil.getAppDate());

		// re generate original schedule from Flexi Schedule
		if (finMain.isAlwFlexi() && finMain.isChgDropLineSchd()) {

			rebuildOrgSchdFromFlexiSchd(finScheduleData);
		}

		if (StringUtils.equals(method, PROC_CHANGEREPAY)) {
			setFinScheduleData(procChangeRepay(finScheduleData, amount, schdMethod));
		}

		if (StringUtils.equals(method, PROC_UNPLANEMIH)) {
			setFinScheduleData(procUnPlanEMIH(finScheduleData, amount, schdMethod));
		}

		if (StringUtils.equals(method, PROC_RECALSCHD)) {
			setFinScheduleData(procReCalSchd(finScheduleData, schdMethod));
		}

		if (StringUtils.equals(method, PROC_REBUILDSCHD)) {
			setFinScheduleData(procRebuildSchd(finScheduleData, schdMethod));
		}

		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal amount,
			BigDecimal feeChargeAmt, boolean utilizeGrcEndDisb) {
		logger.debug("Entering");
		finScheduleData.getFinanceMain().setRecalIdx(-1);
		finScheduleData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());

		if (StringUtils.equals(method, PROC_ADDDISBURSEMENT)) {
			setFinScheduleData(procAddDisbursement(finScheduleData, amount, feeChargeAmt, utilizeGrcEndDisb));
		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal amount,
			boolean utilizeGrcEndDisb, boolean isLoanNotApproved, FinanceDisbursement finDisb, boolean feeAmtInclude) {
		logger.debug("Entering");
		finScheduleData.getFinanceMain().setRecalIdx(-1);

		if (StringUtils.equals(method, PROC_INSTBASEDSCHEDULE)) {
			setFinScheduleData(procInstBasedSchedule(finScheduleData, amount, utilizeGrcEndDisb, isLoanNotApproved,
					finDisb, feeAmtInclude));
		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(FinScheduleData finScheduleData, String method) {
		logger.debug("Entering");
		finScheduleData.getFinanceMain().setRecalIdx(-1);
		finScheduleData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());

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

		// Ticket id:124631 TDS Round OFF
		if (StringUtils.equals(method, PROC_CHANGETDS)) {
			setFinScheduleData(procChangeTDS(finScheduleData));
		}

		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, int noOfTerms) {
		logger.debug("Entering");
		finScheduleData.getFinanceMain().setRecalIdx(-1);
		finScheduleData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());

		if (StringUtils.equals(method, PROC_ADDTERM)) {
			setFinScheduleData(procAddTerm(finScheduleData, noOfTerms, false));
		}
		logger.debug("Leaving");
	}

	private ScheduleCalculator(FinScheduleData finScheduleData, Date earlyPayOnSchdl, Date earlyPayOnNextSchdl,
			BigDecimal earlyPayAmt, String method) {
		logger.debug("Entering");
		finScheduleData.getFinanceMain().setRecalIdx(-1);
		finScheduleData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());

		String receivedRecalMethod = method;

		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setProcMethod(FinanceConstants.FINSER_EVENT_RECEIPT);

		if (!finMain.isApplySanctionCheck()) {
			finMain.setApplySanctionCheck(SanctionBasedSchedule.isApplySanctionBasedSchedule(finScheduleData));
		}

		// BPI change
		// finScheduleData.getFinanceMain().setBpiResetReq(true);

		if (StringUtils.equals(CalculationConstants.EARLYPAY_ADJMUR, method)) {
			method = CalculationConstants.RPYCHG_ADJMDT;
		}

		// Developer Finance, Original Ending Balance Not Changed from Receipts
		if (StringUtils.equals(CalculationConstants.EARLYPAY_PRIHLD, method)) {
			finMain.setResetOrgBal(false);
		}

		finMain.setEventFromDate(earlyPayOnSchdl);
		finMain.setEventToDate(earlyPayOnSchdl);
		finMain.setRecalType(method);

		finScheduleData.getFinanceMain().setIndexMisc(-1);
		finScheduleData = resetRecalData(finScheduleData, earlyPayOnSchdl, earlyPayAmt, finMain.getReceiptPurpose());

		if (finScheduleData.getFinanceMain().getIndexMisc() >= 0) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails()
					.get(finScheduleData.getFinanceMain().getIndexMisc());

			if (StringUtils.equals(curSchd.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
				earlyPayAmt = earlyPayAmt.add(curSchd.getPrincipalSchd()).add(curSchd.getProfitSchd());
			} else {
				earlyPayAmt = earlyPayAmt.add(curSchd.getPrincipalSchd());
			}

		}

		if (finMain.isSanBsdSchdle() && StringUtils.equals(CalculationConstants.EARLYPAY_PRIHLD, method)) {
			setRepayForSanctionBasedPriHld(finScheduleData, earlyPayAmt);
		} else if (StringUtils.equals(CalculationConstants.RPYCHG_ADJMDT, method)
				|| StringUtils.equals(CalculationConstants.EARLYPAY_ADMPFI, method)) {

			finMain.setRecalToDate(finMain.getMaturityDate());
			final BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

			// TODO: PV 19JAN17 schedule method should be sent correctly
			finScheduleData = changeRepay(finScheduleData, earlyPayAmt, finMain.getRecalSchdMethod());

			List<FinanceScheduleDetail> finSchdlDetailList = finScheduleData.getFinanceScheduleDetails();
			int size = finScheduleData.getFinanceScheduleDetails().size();
			Date eventToDate = finMain.getMaturityDate();

			if (!finMain.isApplySanctionCheck()) {
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
			}

			if (StringUtils.equals(CalculationConstants.EARLYPAY_ADMPFI, method)) {

				finMain.setEventToDate(eventToDate);
				// Apply Effective Rate for ReSchedule to get Desired Profit
				finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
						totalDesiredProfit, null, null, false);
				finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			}

		} else if (StringUtils.equals(CalculationConstants.EARLYPAY_RECRPY, method)
				|| (!finMain.isAlwFlexi() && StringUtils.equals(CalculationConstants.EARLYPAY_PRIHLD, method))) {

			finMain.setRecalToDate(finMain.getMaturityDate());

			// Schedule Repayment Change
			// TODO: PV 19JAN17 schedule method should be sent correctly
			finScheduleData = changeRepay(finScheduleData, earlyPayAmt, finMain.getRecalSchdMethod());

			// Schedule ReCalculations afetr Early Repayment Period based upon
			// Schedule Method
			finMain.setEventFromDate(earlyPayOnNextSchdl);
			// finMain.setRecalFromDate(earlyPayOnNextSchdl);
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

			// PV : Principal Holiday, Removed here and Included in
			// EARLYPAY_RECRPY
		} else if (StringUtils.equals(CalculationConstants.EARLYPAY_PRIHLD, method)) {

			/*
			 * if (!finMain.isAlwFlexi()) { finScheduleData = principalHoliday(finScheduleData, earlyPayAmt); }
			 */
		}

		// Recalculation of Details after Schedule calculation
		finScheduleData = afterChangeRepay(finScheduleData);
		finScheduleData.getFinanceMain().setBpiResetReq(false);
		setFinanceTotals(finScheduleData);

		if (StringUtils.equals(CalculationConstants.EARLYPAY_ADJMUR, receivedRecalMethod)) {
			method = receivedRecalMethod;
		}

		setFinScheduleData(finScheduleData);
		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, int noOfTerms, Date subSchStartDate,
			String frqNewSchd) {
		logger.debug("Entering");
		finScheduleData.getFinanceMain().setRecalIdx(-1);
		finScheduleData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());

		if (StringUtils.equals(method, PROC_SUBSCHEDULE)) {
			setFinScheduleData(procSubSchedule(finScheduleData, noOfTerms, subSchStartDate, frqNewSchd));
		}
		logger.debug("Leaving");
	}

	/*
	 * ######################################################################### #######################################
	 * MAIN METHODS #########################################################################
	 * #######################################
	 */

	/*
	 * ========================================================================= = =====================================
	 * Method : procGetCalSchd Description : GET CALCULATED SCHEDULE Process This method will be be called only at the
	 * time of initial schedule creation by BUILD SCHEDULE FUNCTION
	 * ========================================================================= =======================================
	 */

	private FinScheduleData procGetCalSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		// For FLEXI Loans (only origination)
		finMain.setChgDropLineSchd(true);

		List<FinanceScheduleDetail> finScheduleDetails = finScheduleData.getFinanceScheduleDetails();
		Boolean isCalFlat = false;

		if (finMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_C)) {
			isCalFlat = true;
		}

		if (finMain.getGrcProfitDaysBasis() == null) {
			finMain.setGrcProfitDaysBasis(finMain.getProfitDaysBasis());
		}

		// START BPI
		if (!finMain.isAllowGrcPeriod()
				&& StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
			finMain.setBpiResetReq(false);
		}

		// BPI then add BPI record to the schedule
		if (finMain.isAlwBPI()) {
			finScheduleData.getFinanceMain().setModifyBpi(true);
			finScheduleData = addBPISchd(finScheduleData);
		}

		finMain.setEventFromDate(finMain.getFinStartDate());
		finMain.setEventToDate(finMain.getMaturityDate());
		finMain.setRecalFromDate(finMain.getFinStartDate());
		finMain.setRecalToDate(finMain.getMaturityDate());

		// PREPARE FIND SCHDULE DATA
		finScheduleData = preapareFinSchdData(finScheduleData, isCalFlat);
		finScheduleData = calSchdProcess(finScheduleData, isCalFlat, true);

		// If Grace Period calculation amount has maximum capping
		if (finMain.isAllowGrcPeriod() && finMain.getGrcMaxAmount().compareTo(BigDecimal.ZERO) > 0
				&& StringUtils.equals(CalculationConstants.SCHMTHD_PFTCAP, finMain.getGrcSchdMthd())) {
			finScheduleData.getFinanceMain().setEventFromDate(finMain.getFinStartDate());
			finScheduleData.getFinanceMain().setEventToDate(finMain.getGrcPeriodEndDate());
			finScheduleData = procChangeRepay(finScheduleData, finMain.getGrcMaxAmount(),
					CalculationConstants.SCHMTHD_PFTCAP);
		}

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

		// START BPI
		finMain.setBpiResetReq(true);
		// START BPI

		// Insurance calculation
		insuranceCalculation(finScheduleData);

		// Subvention Schedule Details Calculation
		buildSubventionSchedule(finScheduleData);

		// Advised Profit Rate Calculation Process
		finScheduleData = advPftRateCalculation(finScheduleData, finMain.getEventFromDate(), finMain.getEventToDate());

		// Supplementary Rent & Increased Cost Calculation
		if (finMain.getSupplementRent().compareTo(BigDecimal.ZERO) > 0
				|| finMain.getIncreasedCost().compareTo(BigDecimal.ZERO) > 0) {
			finScheduleData = calSuplRentIncrCost(finScheduleData, finMain.getSupplementRent(),
					finMain.getIncreasedCost());
		}

		// BPI Change
		finScheduleData = setFinanceTotals(finScheduleData);
		finScheduleData.getFinanceMain().setModifyBpi(false);
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
		boolean maxReached = false;

		for (int i = 0; i < sdSize - 1; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			Date schdDate = curSchd.getSchDate();

			// Before Lock Period do not mark holiday
			if (DateUtility.compare(schdDate, datePlanEMIHLock) <= 0) {
				continue;
			}

			// Before Grace Period should not mark as Holiday if EMI Holiday not required
			if (DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) <= 0 && !finMain.isPlanEMIHAlwInGrace()) {
				continue;
			}

			// In Repay Period should not mark as Holiday if Plan EMI Holiday not required
			if (DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) > 0 && !finMain.isPlanEMIHAlw()) {
				continue;
			}

			// First payment date also cannot be allowed in planned EMI holiday
			// declaration
			if (curSchd.getInstNumber() == 1) {
				continue;
			}

			// previous PlannedEmiHolidays setting to "" ,that doesn't effect
			// schedule
			if (maxReached) {
				if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
					curSchd.setBpiOrHoliday("");
				}
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
				maxReached = true;
			}
		}

		if (finMain.getEventFromDate() != null
				&& DateUtility.compare(finMain.getEventFromDate(), finMain.getFinStartDate()) == 0) {
			if (finScheduleData.getFinanceType() != null) {
				finMain.setEqualRepay(finScheduleData.getFinanceType().isEqualRepayment());
			} else {
				finMain.setEqualRepay(true);
			}
			finMain.setCalculateRepay(true);
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

		if (finScheduleData.getFinanceType() != null) {
			finMain.setEqualRepay(finScheduleData.getFinanceType().isEqualRepayment());
		} else {
			finMain.setEqualRepay(true);
		}
		finMain.setCalculateRepay(true);

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData validateEMIHoliday(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		// TODO: PV: Correct Error Code
		if (!finMain.isPlanEMIHAlw() && !finMain.isPlanEMIHAlwInGrace()) {
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
	 * ========================================================================= =
	 * ====================================== Method : procReCalSchd Description : Re Calculate schedule from a given
	 * date to end date Process : Should change the repay amount and recalculate repay amounts of remaining ========
	 * ================================================================== ======================================
	 */
	private FinScheduleData procChangeGraceEnd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finScheduleData = ScheduleGenerator.getChangedSchd(finScheduleData);

		if (finScheduleData.getErrorDetails().size() != 0) {
			logger.debug(" Leaving - Error ");
			return finScheduleData;
		}

		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setRecalIdx(-1);
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
			} else if (DateUtility.compare(finScheduleData.getRepayInstructions().get(i).getRepayDate(),
					oldInstructions) < 0
					&& (StringUtils.equals(finScheduleData.getRepayInstructions().get(i).getRepaySchdMethod(),
							CalculationConstants.SCHMTHD_EQUAL)
							|| StringUtils.equals(finScheduleData.getRepayInstructions().get(i).getRepaySchdMethod(),
									CalculationConstants.SCHMTHD_PRI_PFT))) {
				finScheduleData.getRepayInstructions().get(i).setRepaySchdMethod(finMain.getGrcSchdMthd());
				finScheduleData.getRepayInstructions().get(i).setRepayAmount(BigDecimal.ZERO);
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
			// finMain.setEventFromDate(curSchd.getSchDate());
			finMain.setRecalSchdMethod(finMain.getScheduleMethod());
			finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			break;
		}

		// Setting EventFromDate and EventToDate
		finMain.setEventToDate(finMain.getMaturityDate());
		finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGEGRACEEND, BigDecimal.ZERO, BigDecimal.ZERO);

		finScheduleData = calSchdProcess(finScheduleData, false, false);
		finMain.setScheduleMaintained(true);
		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ========================================================================= =
	 * ====================================== Method : procReCalSchd Description : Re Calculate schedule from a given
	 * date to end date Process : Should change the repay amount and recalculate repay amounts of remaining ========
	 * ================================================================== ======================================
	 */
	private FinScheduleData procReCalSchd(FinScheduleData finScheduleData, String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();
		finMain.setApplySanctionCheck(SanctionBasedSchedule.isApplySanctionBasedSchedule(finScheduleData));

		int risize = repayInstructions.size();

		// DE#1550(24-10-2020) - While doing the ReScheduling, RPS is not plotting properly in case of Grace.
		/*
		 * Date evtFromDate = finMain.getRecalFromDate(); finMain.setEventFromDate(evtFromDate);
		 * finMain.setEventToDate(evtFromDate);
		 */

		Date evtFromDate = finMain.getRecalFromDate();

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

		if (finMain.isApplySanctionCheck()) {
			boolean isResetRecalDate = false;
			if (!StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {

				if (StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
					isResetRecalDate = true;
				}

				finScheduleData = setRepayForSanctionBasedAddRecal(finScheduleData, isResetRecalDate);
			}

		} else {
			finScheduleData = setRecalAttributes(finScheduleData, PROC_RECALSCHD, BigDecimal.ZERO, BigDecimal.ZERO);
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		finMain.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ========================================================================= = This method on;y recalculates the
	 * schedule without any changes in schedule method and amounts
	 * ================================================================== ======================================
	 */
	private FinScheduleData procRebuildSchd(FinScheduleData finScheduleData, String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();

		// BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

		Date evtFromDate = finMain.getRecalFromDate();
		finMain.setEventFromDate(evtFromDate);
		finMain.setEventToDate(evtFromDate);
		finScheduleData = calSchdProcess(finScheduleData, false, false);
		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ========================================================================= =======================================
	 * Method : procDeleteTerm Description : DELETE TERM Process : ====================================================
	 * ========================================================================= =======================================
	 */

	private FinScheduleData procDeleteTerm(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		Date evtFromDate = finMain.getEventFromDate();
		Date curBussniessDate = finMain.getAppDate();

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		/*----------------------------------------------------------------
		 *To Delete Terms the below conditions must meet the below conditions
		 * i) Calculated Final Date after deletion must be on/after current business date
		 * ii) Calculated Final Date after deletion must be with repayment on schedule date TRUE
		 * iii) Should not have any deferment payments pending after calculated final date  
		 */

		// Current Schedule Date is after current business date
		// PV 02JUN18: isException is not in use. To overcome Business Date comparison in TestNG cases it is introduced.
		if (DateUtil.compare(evtFromDate, curBussniessDate) < 0 && !finMain.isException()) {
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
		if (finMain.getRecalFromDate() != null) {
			finMain.setEventFromDate(finMain.getRecalFromDate());
		}
		finScheduleData = procReCalSchd(finScheduleData, "");
		finMain.setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * =========================================================================
	 * ======================================== Method : procChangeRate Description : CHANGE RATE Process : Should
	 * change the rate for the requested dates and recalculate the repay amount based on the requested recalculation
	 * type i) CURPRD: Change the rate for the given dates and repay recalculation will happen for the period covered in
	 * the dates only. All remaining repayments should be unchanged.
	 * 
	 * ii) TILLMDT: Change rate for given dates. Repay recalculation will happen till maturity date
	 * 
	 * iii) ADJMDT: Change the rate for the given dates but no repay recalculation will happen. Means
	 * increased/decreased profit will be adjusted to final repay.
	 * 
	 * Schedule Method CURPRD TILLDATE TILLMDT ADJMDT EQUAL Y N Y Y PFT Y N N N PRI Y N Y Y PRI_PFT Y N Y Y
	 * =========================================================================
	 * ========================================
	 */

	private FinScheduleData procChangeRate(FinScheduleData finScheduleData, String baseRate, String splRate,
			BigDecimal mrgRate, BigDecimal actualRate, boolean isCalSchedule, boolean isRefreshRates) {
		logger.debug("Entering");

		boolean isRateChgReq = false;
		if (!isRefreshRates) {
			isRateChgReq = true;
		}

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		int sdSize = finSchdDetails.size();

		FinanceMain finMain = finScheduleData.getFinanceMain();
		String recalType = finMain.getRecalType();
		String rcvRecalType = recalType;
		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		finMain.setCompareToExpected(false);
		finMain.setCompareExpectedResult(BigDecimal.ZERO);
		boolean isApplySanctionBasedSchedule = SanctionBasedSchedule.isApplySanctionBasedSchedule(finScheduleData);

		if (isApplySanctionBasedSchedule) {
			if (!StringUtils.equals(recalType, CalculationConstants.RPYCHG_ADJMDT)) {
				finScheduleData.setErrorDetail(new ErrorDetail("SCH31",
						"Schedules Build on SANCTION AMOUNT does not allow any RECAL TYPE other than ADJMDT Till full disbursement",
						new String[] { " " }));
				return finScheduleData;
			}
		}

		// To allow recaclulation happens as per Adjust to maturity. Once
		// calculation completes
		// addting terms will be done if required
		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_ADJTERMS)) {
			recalType = CalculationConstants.RPYCHG_ADJMDT;
			finMain.setRecalType(recalType);
		}

		// Current Period or Till MDT set Recal from date and recal todate
		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_CURPRD)) {
			finScheduleData = getCurPerodDates(finScheduleData);
		}

		// Force Set recaltype and recal to date to TILLMDT
		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_CURPRD)) {

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

		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_ADJMDT)) {
			finMain.setRecalFromDate(finMain.getMaturityDate());
			finMain.setRecalToDate(finMain.getMaturityDate());
		}

		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_TILLMDT)) {
			finMain.setRecalToDate(finMain.getMaturityDate());
		}

		if (DateUtility.compare(finMain.getRecalFromDate(), finMain.getGrcPeriodEndDate()) <= 0
				&& DateUtility.compare(finMain.getRecalToDate(), finMain.getGrcPeriodEndDate()) > 0) {

			for (int i = 0; i < finSchdDetails.size(); i++) {
				if (DateUtility.compare(finSchdDetails.get(i).getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
					continue;
				}
				if (!finSchdDetails.get(i).isRepayOnSchDate()) {
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
			addSchdRcd(finScheduleData, evtFromDate, prvIndex, true);
			finSchdDetails = finScheduleData.getFinanceScheduleDetails();
			prvIndex = prvIndex + 1;
			finSchdDetails.get(prvIndex).setRvwOnSchDate(true);
			sdSize = finSchdDetails.size();

			isRateChgReq = true;

			if (finMain.isSkipRateReset()) {
				BigDecimal oldCalRate = finSchdDetails.get(prvIndex).getCalculatedRate();
				BigDecimal oldMrgRate = finSchdDetails.get(prvIndex).getMrgRate();
				BigDecimal newCalRate = oldCalRate.subtract(oldMrgRate).add(mrgRate);
				finSchdDetails.get(prvIndex).setCalculatedRate(newCalRate);

				finSchdDetails.get(prvIndex).setBaseRate(baseRate);
				finSchdDetails.get(prvIndex).setMrgRate(mrgRate);
			}
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
			addSchdRcd(finScheduleData, evtToDate, prvIndex, true);
			finSchdDetails = finScheduleData.getFinanceScheduleDetails();
			prvIndex = prvIndex + 1;
			finSchdDetails.get(prvIndex).setRvwOnSchDate(true);
			sdSize = finSchdDetails.size();
		}

		if (!finMain.isSkipRateReset()) {
			// Parameter for counting the number of schedules in between
			// evtFromDate and evtToDate
			BigDecimal calRate = finSchdDetails.get(0).getCalculatedRate();

			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(i);
				Date schdDate = curSchd.getSchDate();

				// Setting Rates between Fromdate and Todate
				if ((DateUtility.compare(schdDate, evtFromDate) >= 0 && DateUtility.compare(schdDate, evtToDate) < 0)
						|| (i == (sdSize - 1))) {

					if (DateUtility.compare(schdDate, evtFromDate) == 0 && StringUtils.isBlank(baseRate)) {
						calRate = actualRate;
					}

					recalculateRate = calRate;

					// If Refresh Rate Consider whatever rates available in Schedule
					if (isRefreshRates) {
						baseRate = curSchd.getBaseRate();
						splRate = curSchd.getSplRate();
						mrgRate = curSchd.getMrgRate();
					}

					if (curSchd.isRvwOnSchDate()) {
						if (StringUtils.isNotBlank(baseRate)) {
							if (DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) < 0) {
								recalculateRate = RateUtil.rates(baseRate, finMain.getFinCcy(), splRate, mrgRate,
										schdDate, finMain.getGrcMinRate(), finMain.getGrcMaxRate()).getNetRefRateLoan();
							} else {
								recalculateRate = RateUtil.rates(baseRate, finMain.getFinCcy(), splRate, mrgRate,
										schdDate, finMain.getRpyMinRate(), finMain.getRpyMaxRate()).getNetRefRateLoan();
							}
						}
					}

					if (curSchd.getCalculatedRate().compareTo(recalculateRate) != 0) {
						isRateChgReq = true;
					}

					curSchd.setBaseRate(baseRate);
					curSchd.setSplRate(splRate);
					curSchd.setMrgRate(mrgRate);
					curSchd.setCalculatedRate(recalculateRate);

					if (StringUtils.isBlank(baseRate)) {
						curSchd.setActRate(recalculateRate);
					}
				}

				calRate = curSchd.getCalculatedRate();
				if (DateUtility.compare(schdDate, evtToDate) >= 0) {
					break;
				}
			}
		}

		// Rate Change Not Required. Do not recalculate again
		if (!isRateChgReq) {
			logger.debug("Leaving - No change in Rates, so exit without calculation");
			return finScheduleData;
		}

		// call the process
		finMain.setProcMethod(FinanceConstants.FINSER_EVENT_RATECHG);
		if (isCalSchedule) {
			finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGERATE, BigDecimal.ZERO, BigDecimal.ZERO);

			finScheduleData = calSchdProcess(finScheduleData, false, false);
		}

		// Actual Requirement is adjust terms
		if (StringUtils.equals(rcvRecalType, CalculationConstants.RPYCHG_ADJTERMS)) {
			recalType = CalculationConstants.RPYCHG_ADJTERMS;
			finMain.setRecalType(recalType);

			int iSchd = finScheduleData.getFinanceScheduleDetails().size() - 1;
			int iLast = finScheduleData.getRepayInstructions().size() - 1;
			BigDecimal lastInstruction = BigDecimal.ZERO;
			BigDecimal lastSchdAmt = BigDecimal.ZERO;

			lastInstruction = finScheduleData.getRepayInstructions().get(iLast).getRepayAmount();

			String schdMethod = finScheduleData.getRepayInstructions().get(iLast).getRepaySchdMethod();

			if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
				lastSchdAmt = finScheduleData.getFinanceScheduleDetails().get(iSchd).getRepayAmount();
			} else if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI)
					|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI_PFT)) {
				lastSchdAmt = finScheduleData.getFinanceScheduleDetails().get(iSchd).getPrincipalSchd();
			}

			if (lastSchdAmt.compareTo(lastInstruction) <= 0) {
				logger.debug("Leaving");
				return finScheduleData;
			}

			BigDecimal lastTermPercent = new BigDecimal(SysParamUtil.getValueAsString("ADJTERM_LASTTERM_PERCENT"));

			lastInstruction = lastInstruction.multiply(lastTermPercent);
			lastInstruction = round(lastInstruction);
			lastInstruction = finScheduleData.getRepayInstructions().get(iLast).getRepayAmount().add(lastInstruction);

			if (lastSchdAmt.compareTo(lastInstruction) > 0) {
				finScheduleData = adjTerms(finScheduleData, true);
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData addSchdRcd(FinScheduleData finScheduleData, Date newSchdDate, int prvIndex,
			boolean addClosingBal) {
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(prvIndex);
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		FinanceScheduleDetail sd = new FinanceScheduleDetail();
		sd.setFinReference(finScheduleData.getFinanceMain().getFinReference());
		sd.setBpiOrHoliday("");
		sd.setSchDate(newSchdDate);
		sd.setDefSchdDate(newSchdDate);
		sd.setSchSeq(1);

		sd.setBaseRate(prvSchd.getBaseRate());
		sd.setSplRate(prvSchd.getSplRate());
		sd.setMrgRate(prvSchd.getMrgRate());
		sd.setActRate(prvSchd.getActRate());
		sd.setCalculatedRate(prvSchd.getCalculatedRate());
		sd.setSchdMethod(prvSchd.getSchdMethod());
		if (StringUtils.equals(prvSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
			if (DateUtility.compare(newSchdDate, financeMain.getGrcPeriodEndDate()) <= 0) {
				sd.setPftDaysBasis(financeMain.getGrcProfitDaysBasis());
			} else {
				sd.setPftDaysBasis(financeMain.getProfitDaysBasis());
			}
		} else {
			sd.setPftDaysBasis(prvSchd.getPftDaysBasis());
		}
		sd.setAdvBaseRate(prvSchd.getAdvBaseRate());
		sd.setAdvMargin(prvSchd.getAdvMargin());
		sd.setAdvPftRate(prvSchd.getAdvPftRate());
		sd.setSuplRent(prvSchd.getSuplRent());
		sd.setIncrCost(prvSchd.getIncrCost());
		sd.setOrgEndBal(prvSchd.getOrgEndBal());
		sd.setLimitDrop(prvSchd.getLimitDrop());
		sd.setODLimit(prvSchd.getODLimit());

		if (addClosingBal) {
			sd.setClosingBalance(prvSchd.getClosingBalance());
		}

		// ### 10-05-2018 - PSD Ticket ID : 126189, Flexi
		sd.setNoOfDays(DateUtility.getDaysBetween(newSchdDate, prvSchd.getSchDate()));
		sd.setDayFactor(CalculationUtil.getInterestDays(prvSchd.getSchDate(), newSchdDate, sd.getPftDaysBasis()));

		finScheduleData.getFinanceScheduleDetails().add(sd);
		finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));

		return finScheduleData;
	}

	/*
	 * ========================================================================= =======================================
	 * Method : procChangeRepay Description : CHANGE REPAY AMOUNT Process : Should change the repay amount and
	 * recalculate repay amounts of remaining schedule based on the requested recalculation type. Not Applicable for
	 * schedule method "PFT" i) TILLDATE: Change the repay for requested dates and recalculate schedule. ii) TILLMDT:
	 * Change the repay till maturity date iii) ADJMDT: Change the repay and increase/decrease in profit will be *
	 * adjusted to maturity.
	 * 
	 * NOTE : Schedule Method EQUAL/PRI will allow all recalculation types Schedule Method PFT/PRI_PFT will allow CURPRD
	 * only =========================================================================
	 * =======================================
	 */

	private FinScheduleData procChangeRepay(FinScheduleData finScheduleData, BigDecimal repayAmount,
			String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();
		String reqSchdMethod = schdMethod;

		// BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		Date recalFromDate = finMain.getRecalFromDate();

		// Repay Change with in Grace Period
		if (StringUtils.equals(reqSchdMethod, CalculationConstants.SCHMTHD_PFTCAP)) {

			if (StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				schdMethod = repayInstructions.get(risize - 1).getRepaySchdMethod();
			} else {
				for (int i = 0; i < risize; i++) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
					finMain.setRecalSchdMethod(schdMethod);

					if (repayInstructions.get(i).getRepayDate().compareTo(recalFromDate) > 0) {
						break;
					}
				}
			}
		}

		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.isBlank(schdMethod)) {
			for (int i = 0; i < risize; i++) {
				if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
					reqSchdMethod = schdMethod;
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
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, prvIndex, true);
			curSchd = finSchdDetails.get(prvIndex + 1);
			curSchd.setPftOnSchDate(true);
			curSchd.setRepayOnSchDate(true);
			curSchd.setRepayAmount(repayAmount);
		}

		finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtToDate, repayAmount, reqSchdMethod);
		finMain.setRecalSchdMethod(finMain.getScheduleMethod());

		if (finScheduleData.getFinanceMain().getRecalFromDate() == null) {
			// FIXME:Passing evtFromDate as RecalFromDate to resolve 900 error
			finScheduleData.getFinanceMain().setRecalFromDate(evtFromDate);
		}

		if (!finMain.isSanBsdSchdle()) {
			finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGEREPAY, BigDecimal.ZERO, repayAmount);
		}

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
		 * if (finMain.isPftIntact()) { finScheduleData = calEffectiveRate(finScheduleData,
		 * CalculationConstants.SCH_SPECIFIER_TOTAL, totalDesiredProfit, evtFromDate, finMain.getCalMaturity(), false);
		 * } else { finScheduleData = calSchdProcess(finScheduleData, false, false); }
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

	/*
	 * ========================================================================== Method : procAddDatedSchedule
	 * Description : Insert dated ScheduleTerm & Re Calculate schedule from a given date to end date Process : Should
	 * Add the New Scheduled term and recalculate repay amounts From Scheduled Term
	 * ==========================================================================
	 */
	private FinScheduleData procAddDatedSchedule(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		// TODO: Added for Bajaj Demo on 10 APR16
		// finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		finMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		finMain.setEventToDate(finMain.getEventFromDate());
		finMain.setRecalFromDate(finMain.getEventFromDate());
		finMain.setRecalToDate(finMain.getEventFromDate());

		Date newScheduleDate = finMain.getEventFromDate();

		// insert new Schedule Dated term
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		FinanceScheduleDetail prvSchd = null;
		for (int i = 1; i < sdSize; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			if (curSchd.getSchDate().compareTo(newScheduleDate) <= 0) {

				if (curSchd.getSchDate().compareTo(newScheduleDate) == 0) {
					finScheduleData.setErrorDetail(new ErrorDetail("SCH37",
							"Schedule Term with Mentioned Date is Already Exist", new String[] { " " }));
					return finScheduleData;
				}

				prvSchd = curSchd;
				continue;
			}

			// boolean isAfterFirstTerm = false; Commented as the flag is not
			// used anywhere
			if (prvSchd == null) {
				prvSchd = finScheduleData.getFinanceScheduleDetails().get(0);
				// isAfterFirstTerm = true;
			}

			FinanceScheduleDetail sd = new FinanceScheduleDetail();

			sd.setSchDate(newScheduleDate);
			sd.setDefSchdDate(newScheduleDate);
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
			sd.setBaseRate(prvSchd.getBaseRate());
			sd.setSplRate(prvSchd.getSplRate());
			sd.setMrgRate(prvSchd.getMrgRate());
			sd.setActRate(prvSchd.getActRate());
			sd.setCalculatedRate(prvSchd.getCalculatedRate());
			sd.setPftDaysBasis(prvSchd.getPftDaysBasis());
			sd.setAdvBaseRate(prvSchd.getAdvBaseRate());
			sd.setAdvMargin(prvSchd.getAdvMargin());
			sd.setAdvPftRate(prvSchd.getAdvPftRate());
			sd.setSuplRent(prvSchd.getSuplRent());
			sd.setIncrCost(prvSchd.getIncrCost());

			if (newScheduleDate.compareTo(finMain.getGrcPeriodEndDate()) > 0) {
				sd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				sd.setSchdMethod(finMain.getScheduleMethod());
			} else {
				sd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
				sd.setSchdMethod(finMain.getGrcSchdMthd());
			}

			// Commented to Add Flags to change amount on Change Repay option based on previous schedule flags.
			// sd = resetCurSchdFlags(sd, finMain);
			sd.setPftOnSchDate(prvSchd.isPftOnSchDate());
			sd.setCpzOnSchDate(prvSchd.isCpzOnSchDate());
			sd.setRvwOnSchDate(prvSchd.isRvwOnSchDate());
			sd.setRepayOnSchDate(prvSchd.isRepayOnSchDate());

			// Set new repayments amount for the selected dates
			finScheduleData = setRpyInstructDetails(finScheduleData, newScheduleDate, newScheduleDate, BigDecimal.ZERO,
					sd.getSchdMethod());

			finScheduleData.getFinanceScheduleDetails().add(sd);
			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));

			if (sd.isRepayOnSchDate()) {
				finMain.setNumberOfTerms(finMain.getNumberOfTerms() + 1);
			}

			break;
		}

		// Recalculate Schedule
		// finScheduleData = procReCalSchd(finScheduleData);

		// START PROCESS
		finScheduleData = fetchGraceCurRates(finScheduleData);
		finScheduleData = fetchRepayCurRates(finScheduleData);

		finMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		// finScheduleData = procChangeRepay(finScheduleData, BigDecimal.ZERO, finMain.getScheduleMethod());
		finScheduleData = getRpyInstructDetails(finScheduleData);

		/* Grace Schedule calculation */
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);
		finScheduleData = setFinanceTotals(finScheduleData);

		finMain.setScheduleMaintained(true);

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
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, prvIndex, true);
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

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		finMain.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ========================================================================= =======================================
	 * Method : procAddDisburse Description : ADD DISBURSEMENT Process : ==============================================
	 * ======================================== =======================================================================
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
			if (DateUtil.compare(curSchd.getSchDate(), evtFromDate) < 0) {
				disbIndex = i;
				continue;

				// Schedule Date matches event from date
			} else if (DateUtility.compare(curSchd.getSchDate(), evtFromDate) == 0) {
				isDisbDateFoundInSD = true;
				curSchd.setDisbAmount(curSchd.getDisbAmount().add(newDisbAmount));
				curSchd.setDisbOnSchDate(true);
				curSchd.setFeeChargeAmt(curSchd.getFeeChargeAmt().add(newFeeAmt));
				if (i != 0) {
					// FIXME MUR>> Check with Satish/Siva//Pradeep
					curSchd.setClosingBalance(finSchdDetails.get(i - 1).getClosingBalance().add(newDisbAmount)
							.add(newFeeAmt).subtract(curSchd.getPrincipalSchd()));
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
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, disbIndex, false);
			prvSchd = finSchdDetails.get(disbIndex);
			disbIndex = disbIndex + 1;
			curSchd = finScheduleData.getFinanceScheduleDetails().get(disbIndex);

			curSchd.setDisbOnSchDate(true);
			curSchd.setDisbAmount(newDisbAmount);
			curSchd.setFeeChargeAmt(newFeeAmt);
			curSchd.setClosingBalance(prvSchd.getClosingBalance().add(newDisbAmount).add(newFeeAmt));
		}

		// Setting Original Schedule End balances with New Disbursement changes
		if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()) {
			setOrgEndBalances(finScheduleData, newDisbAmount, newFeeAmt);
		}

		// SATYA : FLEXI CODE REMOVED HERE

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

			if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT) && !finMain.isSanBsdSchdle()) {
				finMain.setRecalFromDate(finMain.getMaturityDate());
				finMain.setRecalToDate(finMain.getMaturityDate());
			}

			if (DateUtility.compare(finMain.getRecalFromDate(), finMain.getGrcPeriodEndDate()) <= 0
					&& DateUtility.compare(finMain.getRecalToDate(), finMain.getGrcPeriodEndDate()) > 0) {

				for (int i = 0; i < finSchdDetails.size(); i++) {
					if (DateUtility.compare(finSchdDetails.get(i).getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) {
						continue;
					}

					if (!finSchdDetails.get(i).isRepayOnSchDate()) {
						continue;
					}

					finMain.setRecalFromDate(finSchdDetails.get(i).getSchDate());
					break;

				}
			}

			if (finMain.isSanBsdSchdle()
					&& StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				finScheduleData = setRepayForSanctionBasedDisbADJMDT(finScheduleData, newDisbAmount);
			} else {
				finScheduleData = setRecalAttributes(finScheduleData, PROC_ADDDISBURSEMENT, newDisbAmount,
						BigDecimal.ZERO);
			}
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);
		finMain = finScheduleData.getFinanceMain();
		finMain.setScheduleMaintained(true);

		// Insurance Schedule Calculation
		finScheduleData = insuranceCalculation(finScheduleData);

		// Subvention Schedule Details Calculation
		buildSubventionSchedule(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ========================================================================= =======================================
	 * Method : procInstProcess Description : Instruction Process : ==============================================
	 * ======================================== =======================================================================
	 */

	private FinScheduleData procInstBasedSchedule(FinScheduleData orgFinScheduleData, BigDecimal newDisbAmount,
			boolean utilizeGrcEndDisb, boolean isLoanNotApproved, FinanceDisbursement finDisb, boolean feeAmtInclude) {
		logger.debug("Entering");

		// Create Cloner for calculation purpose. If calculation is successful
		// send back calculated DATA else send original data with errors
		FinScheduleData finScheduleData = null;
		Cloner cloner = new Cloner();
		finScheduleData = cloner.deepClone(orgFinScheduleData);
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<FinanceDisbursement> finDisbDetails = finScheduleData.getDisbursementDetails();
		List<FinFeeDetail> listFeeDetails = finScheduleData.getFinFeeDetailList();
		List<VASRecording> listVasRecording = finScheduleData.getVasRecordingList();
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

		// Get Fee Detail amount and add in disb amount
		// if loan is not approved then consider fee amount
		BigDecimal deductDisbFee = BigDecimal.ZERO;
		BigDecimal newFeeAmt = BigDecimal.ZERO;

		if (CollectionUtils.isNotEmpty(listFeeDetails) && feeAmtInclude) {
			for (FinFeeDetail finFeeDetail : listFeeDetails) {

				if (isLoanNotApproved) {
					if (!finFeeDetail.isOriginationFee()) {
						continue;
					}
				} else {
					if (finFeeDetail.isOriginationFee()) {
						continue;
					}
				}

				if (StringUtils.equals(finFeeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_ADDDBSP)
						&& StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
					deductDisbFee = deductDisbFee.add(finFeeDetail.getRemainingFee());
				}

				if (StringUtils.equals(finFeeDetail.getFinEvent(), AccountEventConstants.ACCEVENT_ADDDBSP)
						&& StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					newFeeAmt = newFeeAmt.add(finFeeDetail.getRemainingFee());
				}
			}
		}

		/*
		 * if (isLoanNotApproved) {
		 * 
		 * // Calc vasFee amount if (CollectionUtils.isNotEmpty(listVasRecording)) { for (VASRecording vasRecording :
		 * listVasRecording) { deductDisbFee = deductDisbFee.add(vasRecording.getFee()); } }
		 * 
		 * }
		 */

		// add if any fee amount present
		newDisbAmount = newDisbAmount.add(deductDisbFee);

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

		long disbId = 0;
		if (finDisb != null) {
			disbId = finDisb.getInstructionUID();
		}

		// ADD new disbursement Record.
		FinanceDisbursement dd = new FinanceDisbursement();
		dd.setDisbAmount(newDisbAmount);
		dd.setDisbDate(evtFromDate);
		dd.setFeeChargeAmt(newFeeAmt);
		dd.setDisbSeq(disbSeq + 1);
		dd.setLinkedDisbId(disbId);
		finScheduleData.getDisbursementDetails().add(dd);

		//validate if this disb date is equal to realization date
		// return schedule

		/*
		 * if (finDisb != null) { if (evtFromDate.compareTo(finDisb.getDisbDate()) == 0) { return finScheduleData; } }
		 */

		if (isLoanNotApproved) {
			for (FinanceScheduleDetail financeScheduleDetail : finScheduleData.getFinanceScheduleDetails()) {

				// Removing Closing balance
				if (financeScheduleDetail.isDisbOnSchDate()) {
					financeScheduleDetail.setDisbAmount(BigDecimal.ZERO);
					financeScheduleDetail.setFeeChargeAmt(BigDecimal.ZERO);
				}

				// Remove closing balance and update schedule
				if (!financeScheduleDetail.isDisbOnSchDate()
						&& evtFromDate.compareTo(financeScheduleDetail.getSchDate()) > 0) {
					financeScheduleDetail.setClosingBalance(BigDecimal.ZERO);
					financeScheduleDetail.setRepayOnSchDate(false);
					financeScheduleDetail.setRvwOnSchDate(false);
					financeScheduleDetail.setBalanceForPftCal(BigDecimal.ZERO);
					financeScheduleDetail.setProfitCalc(BigDecimal.ZERO);
					financeScheduleDetail.setDayFactor(BigDecimal.ZERO);
					financeScheduleDetail.setProfitSchd(BigDecimal.ZERO);
					financeScheduleDetail.setPrincipalSchd(BigDecimal.ZERO);
					financeScheduleDetail.setRepayAmount(BigDecimal.ZERO);
					financeScheduleDetail.setTDSAmount(BigDecimal.ZERO);
					financeScheduleDetail.setOrgEndBal(newDisbAmount.add(deductDisbFee));
				}
			}
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
				if (i != 0) {
					if (isLoanNotApproved) {
						curSchd.setClosingBalance((newDisbAmount).add(newFeeAmt).subtract(curSchd.getPrincipalSchd()));
					} else {
						curSchd.setClosingBalance(finSchdDetails.get(i - 1).getClosingBalance().add(newDisbAmount)
								.add(newFeeAmt).subtract(curSchd.getPrincipalSchd()));
					}
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
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, disbIndex, false);
			prvSchd = finSchdDetails.get(disbIndex);
			disbIndex = disbIndex + 1;
			curSchd = finScheduleData.getFinanceScheduleDetails().get(disbIndex);

			curSchd.setDisbOnSchDate(true);
			curSchd.setDisbAmount(newDisbAmount);
			curSchd.setFeeChargeAmt(newFeeAmt);

			if (isLoanNotApproved) {
				curSchd.setClosingBalance(newDisbAmount.add(newFeeAmt));
			} else {
				curSchd.setClosingBalance(prvSchd.getClosingBalance().add(newDisbAmount).add(newFeeAmt));
			}

		}

		// Setting Original Schedule End balances with New Disbursement changes
		if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()) {
			setOrgEndBalances(finScheduleData, newDisbAmount, newFeeAmt);
		}

		// SATYA : FLEXI CODE REMOVED HERE

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

			if (finMain.isSanBsdSchdle()
					&& !StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
				finScheduleData = setRepayForSanctionBasedDisbADJMDT(finScheduleData, newDisbAmount);
			} else {
				finScheduleData = setRecalAttributes(finScheduleData, PROC_ADDDISBURSEMENT, newDisbAmount,
						BigDecimal.ZERO);
			}
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);
		finMain = finScheduleData.getFinanceMain();
		finMain.setScheduleMaintained(true);

		// Insurance Schedule Calculation
		finScheduleData = insuranceCalculation(finScheduleData);

		// Subvention Schedule Details Calculation
		buildSubventionSchedule(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private void setOrgEndBalances(FinScheduleData finScheduleData, BigDecimal newDisbAmount, BigDecimal newFeeAmt) {

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		// Original END Balance update for Developer Finance
		Date graceEndDate = finMain.getGrcPeriodEndDate();
		String recaltype = finMain.getRecalType();
		Date evtFromDate = finMain.getEventFromDate();

		int recalTermsForOrgBal = 0;
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLDATE)
				|| StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJTERMS)) {

			for (FinanceScheduleDetail schd : finSchdDetails) {
				if (DateUtility.compare(schd.getSchDate(), graceEndDate) > 0
						&& DateUtility.compare(schd.getSchDate(), finMain.getRecalFromDate()) >= 0
						&& DateUtility.compare(schd.getSchDate(), finMain.getRecalToDate()) <= 0) {

					if (StringUtils.isNotEmpty(schd.getBpiOrHoliday())
							&& !StringUtils.equals(FinanceConstants.FLAG_BPI, schd.getBpiOrHoliday())
							&& !StringUtils.equals(FinanceConstants.FLAG_HOLDEMI, schd.getBpiOrHoliday())) {
						continue;
					}

					recalTermsForOrgBal = recalTermsForOrgBal + 1;
				}
			}
		}

		// Add Additional Requested Terms For Adjust Term Functionality
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJTERMS)) {
			recalTermsForOrgBal = recalTermsForOrgBal + finMain.getAdjTerms();
		}

		BigDecimal balDisbAmount = newDisbAmount.add(newFeeAmt);
		BigDecimal adjOrgAmount = balDisbAmount;
		if (recalTermsForOrgBal > 0) {
			adjOrgAmount = balDisbAmount.divide(new BigDecimal(recalTermsForOrgBal), 0, RoundingMode.HALF_DOWN);
			adjOrgAmount = CalculationUtil.roundAmount(adjOrgAmount, finMain.getCalRoundingMode(),
					finMain.getRoundingTarget());
		}

		// Setting New Disbursement changes into Original Ending Balance
		for (FinanceScheduleDetail schd : finSchdDetails) {

			// No Change in Disbursement addition to Original End Balance
			// before selected recalculation
			if (DateUtility.compare(schd.getSchDate(), finMain.getRecalFromDate()) < 0
					&& DateUtility.compare(schd.getSchDate(), evtFromDate) >= 0) {
				schd.setOrgEndBal(schd.getOrgEndBal().add(balDisbAmount));
			}

			// If Schedule Date before Grace End date, should not consider
			// on adjustment
			if (DateUtility.compare(schd.getSchDate(), graceEndDate) <= 0) {
				continue;
			}

			// Adjust amounts between recalculation dates
			if (DateUtility.compare(schd.getSchDate(), finMain.getRecalFromDate()) >= 0
					&& DateUtility.compare(schd.getSchDate(), finMain.getRecalToDate()) <= 0) {

				balDisbAmount = balDisbAmount.subtract(adjOrgAmount);
				if (balDisbAmount.compareTo(adjOrgAmount) < 0) {
					adjOrgAmount = balDisbAmount;
				}
				schd.setOrgEndBal(schd.getOrgEndBal().add(balDisbAmount));
				if (balDisbAmount.compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}
	}

	/**
	 * Method for Resetting Repayment Instructions for Developer Finance in case of Principal Holiday applied on Partial
	 * Settlement
	 * 
	 * @param finScheduleData
	 * @param additionalAmount
	 * @return
	 */
	@SuppressWarnings("unused")
	private FinScheduleData setOrgRpyInstructions(FinScheduleData finScheduleData, Date newDisbDate,
			BigDecimal additionalAmount) {

		if (!finScheduleData.getFinanceMain().isDevFinCalReq()) {
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
			if (DateUtility.compare(schd.getSchDate(), finMain.getRecalFromDate()) >= 0) {
				if ((schd.getClosingBalance().add(additionalAmount).compareTo(schd.getOrgEndBal())) < 0) {
					if (!zeroInstrAdded) {
						zeroInstrAdded = true;
						startFrom = schd.getSchDate();
					}
				} else {
					FinanceScheduleDetail pSchd = finSchdDetails.get(i - 1);
					if (zeroInstrAdded) {
						finScheduleData = setRpyInstructDetails(finScheduleData, startFrom, pSchd.getSchDate(),
								BigDecimal.ZERO, finMain.getScheduleMethod());
					}
					if (newRpyInstReq && priHolidayApplied) {
						BigDecimal amount = pSchd.getClosingBalance().subtract(schd.getOrgEndBal());
						if (newDisbDate != null && DateUtility.compare(newDisbDate, pSchd.getSchDate()) != 0) {
							amount = amount.add(additionalAmount);
						}
						finScheduleData = setRpyInstructDetails(finScheduleData, schd.getSchDate(), schd.getSchDate(),
								amount, finMain.getScheduleMethod());

						if (i != finSchdDetails.size() - 1) {
							for (int j = i + 1; j < finSchdDetails.size() - 2; j++) {
								if (finSchdDetails.get(j).isRepayOnSchDate()) {
									finScheduleData.getFinanceMain()
											.setRecalFromDate(finSchdDetails.get(j).getSchDate());
									break;
								}
							}
						}
						finScheduleData.getFinanceMain().setResetNxtRpyInstReq(true);
					}
					break;
				}
			} else {

				if (schd.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0
						&& DateUtility.compare(schd.getSchDate(), finMain.getGrcPeriodEndDate()) >= 0) {
					priHolidayApplied = true;
				}
				if (priHolidayApplied && !finMain.isResetNxtRpyInstReq()) {
					if ((schd.getClosingBalance().add(additionalAmount).compareTo(schd.getOrgEndBal())) == 0) {
						newRpyInstReq = false;
					}
				}
			}
		}
		return finScheduleData;
	}

	/*
	 * ========================================================================= =======================================
	 * Method : procAddTerm Description : ADD TERM Process : Add Term will add another term to the schedule details.
	 * =========================================================================
	 * ========================================
	 */

	private FinScheduleData procAddTerm(FinScheduleData orgFinScheduleData, int noOfTerms, boolean isRepayOnSchd) {
		logger.debug("Entering");

		FinScheduleData finScheduleData = null;
		Cloner cloner = new Cloner();
		finScheduleData = cloner.deepClone(orgFinScheduleData);

		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType ft = finScheduleData.getFinanceType();

		int totalTerms = finMain.getNumberOfTerms() + noOfTerms;

		if (totalTerms > ft.getFinMaxTerm()) {
			orgFinScheduleData.setErrorDetail(new ErrorDetail("SCH30",
					"ADD/ADJ TERMS REACHED MAXIMUM TERMS IN CONFIGURATION. NOT ALLOWED TO ADD MORE TERMS.",
					new String[] { " " }));
			return orgFinScheduleData;
		}

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
	 * ========================================================================= =======================================
	 * Method : procChangeProfit Description : CHANGE Profit Amount between two dates Process :
	 * ========================================================================= =======================================
	 */

	private FinScheduleData procChangeProfit(FinScheduleData finScheduleData, BigDecimal desiredPftAmount) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		finMain.setRecalFromDate(evtFromDate);
		finMain.setRecalToDate(evtToDate);

		if (DateUtil.compare(finMain.getRecalFromDate(), finMain.getGrcPeriodEndDate()) < 0) {
			int size = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = 0; i < size; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if (DateUtil.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) > 0) {
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
	 * ========================================================================= =======================================
	 * Method : procRefreshRates Description :REFRESH RATES AND RECALCULATE SCHEDULE BASED ON REVIEW RATE APPLIED FOR
	 * ========================================================================= =======================================
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

		// finScheduleData = calSchdProcess(finScheduleData, false, false); // In auto rate review process we commented
		// this

		// In auto rate review process we added this
		if (DateUtility.compare(finMain.getGrcPeriodEndDate(), finMain.getAppDate()) > 0) {
			finScheduleData = procChangeRate(finScheduleData, finMain.getGraceBaseRate(), finMain.getGraceSpecialRate(),
					finMain.getGrcMargin(), finMain.getEffectiveRateOfReturn(), true, true);
		} else {
			finScheduleData = procChangeRate(finScheduleData, finMain.getRepayBaseRate(), finMain.getRepaySpecialRate(),
					finMain.getRepayMargin(), finMain.getEffectiveRateOfReturn(), true, true);
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ######################################################################### #######################################
	 * 
	 * SUB METHODS
	 * 
	 * ######################################################################### #######################################
	 */

	/*
	 * ========================================================================= =======================================
	 * PREPARE FinSchedule =========================================================================
	 * =======================================
	 */
	private FinScheduleData preapareFinSchdData(FinScheduleData finScheduleData, Boolean isCalFlat) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		if (StringUtils.isBlank(finMain.getGrcSchdMthd())) {
			finMain.setGrcSchdMthd(finMain.getScheduleMethod());
		}

		// Set Default scheduled date and schedule method first time
		// PSD #138659 Number of Grace Terms should not include the BPI term.
		int i = 0;

		for (FinanceScheduleDetail curSchd : finSchdDetails) {
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0
					&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				finMain.setGraceTerms(i);
				i++;
			}
		}

		if (finMain.getLastRepayDate() == null) {
			finMain.setLastRepayDate(finMain.getFinStartDate());
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

			if (finScheduleData.getFinanceMain().getRemBalForAdj()
					.compareTo(finScheduleData.getFinanceMain().getAdjOrgBal()) < 0) {
				finScheduleData.getFinanceMain().setAdjOrgBal(finScheduleData.getFinanceMain().getRemBalForAdj());
			}

			finScheduleData = addOneTerm(finScheduleData, lastDateLimit, isSetRepay);
			finScheduleData.getFinanceMain().setRemBalForAdj(finScheduleData.getFinanceMain().getRemBalForAdj()
					.subtract(finScheduleData.getFinanceMain().getAdjOrgBal()));

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
	 * Method : addOneTerm Description : ADD One Terms Process : Add Term will add another term to the schedule details.
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData addOneTerm(FinScheduleData finScheduleData, Date lastDateLimit, boolean isSetRepay) {
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
						DateUtility.format(
								FrequencyUtil.getNextDate(finMain.getRepayFrq(), 1, curSchdDate,
										HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
								PennantConstants.dateFormat));

		if (DateUtil.compare(nextSchdDate, lastDateLimit) > 0) {
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

			if (DateUtil.compare(finSchdDetails.get(i).getSchDate(), nextSchdDate) > 0) {
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

		List<FinanceScheduleDetail> fsdList = finScheduleData.getFinanceScheduleDetails();
		int lastIndex = fsdList.size() - 1;
		FinanceScheduleDetail lastSchd = fsdList.get(lastIndex);

		int lastInstNumber = lastSchd.getInstNumber();
		for (int i = lastIndex; i > 0; i--) {
			if (fsdList.get(i).getInstNumber() > 0) {
				lastInstNumber = fsdList.get(i).getInstNumber();
				break;
			}
		}

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
				if (DateUtility.compare(nextSchdDate, finMain.getGrcPeriodEndDate()) == 0
						&& finMain.isFinIsRateRvwAtGrcEnd()) {
					sd.setRvwOnSchDate(true);
				} else {
					sd.setRvwOnSchDate(false);
				}
			}

			// Set Capitalize On date based on frequency
			if (finMain.isAllowGrcCpz() && FrequencyUtil.isFrqDate(finMain.getGrcCpzFrq(), nextSchdDate)) {
				sd.setCpzOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				if (DateUtility.compare(nextSchdDate, finMain.getGrcPeriodEndDate()) == 0
						&& finMain.isCpzAtGraceEnd()) {
					sd.setCpzOnSchDate(true);
				} else {
					sd.setCpzOnSchDate(false);
				}
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
		sd.setTDSApplicable(TDSCalculator.isTDSApplicable(finMain));

		int iRepay = finScheduleData.getRepayInstructions().size();
		String schdMethod = finScheduleData.getFinanceMain().getScheduleMethod();
		if (iRepay > 0) {
			schdMethod = finScheduleData.getRepayInstructions().get(iRepay - 1).getRepaySchdMethod();
		}

		if (StringUtils.isBlank(finScheduleData.getFinanceMain().getRecalSchdMethod())) {
			finScheduleData.getFinanceMain().setRecalSchdMethod(schdMethod);
		}

		sd.setSchdMethod(schdMethod);
		sd.setPftDaysBasis(lastSchd.getPftDaysBasis());
		sd.setAdvBaseRate(lastSchd.getAdvBaseRate());
		sd.setAdvMargin(lastSchd.getAdvMargin());
		sd.setAdvPftRate(lastSchd.getAdvPftRate());
		sd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);

		if (sd.isRepayOnSchDate()) {
			sd.setInstNumber(lastInstNumber + 1);
		}

		finScheduleData.getFinanceScheduleDetails().add(sd);
		finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : setOtherSchdDates Description : Reset Other Schedule dates than repay dates Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setOtherSchdDates(FinScheduleData finScheduleData, String frequency, Date curStartDate,
			int scheduleFlag) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		while (true) {
			// Get Next Schedule Date

			Date nextSchdDate = DateUtility.getDate(DateUtility
					.format(FrequencyUtil.getNextDate(frequency, 1, curStartDate, HolidayHandlerTypes.MOVE_NONE, false)
							.getNextFrequencyDate(), PennantConstants.dateFormat));

			int sdSize = finSchdDetails.size();
			curSchd = finSchdDetails.get(sdSize - 1);

			// Next Schedule Date is after last repayment date
			if (DateUtil.compare(nextSchdDate, finScheduleData.getFinanceMain().getCalMaturity()) > 0) {
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
					if (DateUtil.compare(nextSchdDate, curSchd.getSchDate()) > 0 && DateUtility.compare(nextSchdDate,
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
	 * Method : setRpyInstructDetails Description : Set Repay Instruction Details Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData setRpyInstructDetails(FinScheduleData finScheduleData, Date fromDate, Date toDate,
			BigDecimal repayAmount, String schdMethod) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		if (StringUtils.equals(CalculationConstants.SCHMTHD_POS_INT, finMain.getScheduleMethod())
				|| finMain.isManualSchedule()) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		BigDecimal nextInstructAmount = BigDecimal.ZERO;
		Date nextInstructDate = null;
		String nextInstructSchdMethod = null;

		boolean isAddNewInstruction = true;
		int instructIndex = -1;

		// Find next date for instruction
		if (DateUtility.compare(toDate, finMain.getMaturityDate()) >= 0) {
			nextInstructDate = finMain.getMaturityDate();
		} else {
			int sdSize = finSchdDetails.size();
			FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

			for (int i = 0; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
				// ### 01-08-2018 - PSD Ticket ID : 125445, 125588, Freezing
				// period - End
				if (DateUtil.compare(curSchd.getSchDate(), toDate) > 0
						&& (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())
						&& DateUtil.compare(curSchd.getSchDate(), finMain.getMaturityDate()) != 0) {
					nextInstructDate = curSchd.getSchDate();
					nextInstructSchdMethod = curSchd.getSchdMethod();
					break;
				}
			}
			// Next instruction amount and schedule method
			sortRepayInstructions(finScheduleData.getRepayInstructions());
			if (nextInstructDate != null) {
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

			if ((DateUtility.compare(curInstruction.getRepayDate(), fromDate) >= 0
					&& DateUtility.compare(curInstruction.getRepayDate(), toDate) <= 0)) {
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
		ri.setFinReference(finMain.getFinReference());

		finScheduleData.getRepayInstructions().add(ri);

		// Add (reset) repay instruction after todate
		if (DateUtility.compare(toDate, finMain.getMaturityDate()) >= 0 || !isAddNewInstruction) {
			finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData.getRepayInstructions()));
			return finScheduleData;
		}

		if (DateUtility.compare(nextInstructDate, fromDate) > 0) {
			ri = new RepayInstruction();
			ri.setFinReference(finMain.getFinReference());
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
	 * Method : getRpyInstructDetails Description : Get Repay Instruction Details Process :
	 * ________________________________________________________________________________________________________________
	 */

	private FinScheduleData getRpyInstructDetails(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		int riSize = finScheduleData.getRepayInstructions().size();
		BigDecimal instructAmount = BigDecimal.ZERO;

		FinanceMain finMain = finScheduleData.getFinanceMain();

		if (finMain.getRecalIdx() < 0) {
			setRecalIndex(finScheduleData);
		}

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

		if (finMain.isAdjustClosingBal()) {
			riSize = finScheduleData.getRepayInstructions().size();
			finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData.getRepayInstructions()));
			for (int j = 0; j < riSize; j++) {
				RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(j);
				if (DateUtility.compare(curInstruction.getRepayDate(), finMain.getGrcPeriodEndDate()) > 0) {
					if (AdvanceType.hasAdvEMI(finMain.getAdvType())
							&& AdvanceStage.hasFrontEnd(finMain.getAdvStage())) {
						finMain.setAdvanceEMI(
								curInstruction.getRepayAmount().multiply(BigDecimal.valueOf(finMain.getAdvTerms())));
					}

				}
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : fetchRpyInstruction Description : Fetch Repay Instruction index by date Process :
	 * ________________________________________________________________________________________________________________
	 */

	private int fetchRpyInstruction(FinScheduleData finScheduleData, Date instructDate) {

		int riSize = finScheduleData.getRepayInstructions().size();
		int j = -1;

		for (int i = 0; i < riSize; i++) {
			RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(i);

			if (DateUtil.compare(curInstruction.getRepayDate(), instructDate) > 0) {
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
		boolean isManualAmtStep = false;

		if (finMain.isStepFinance() && finMain.isAlwManualSteps() && finMain.isGrcStps()) {
			if (StringUtils.equals(finMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
				isManualAmtStep = true;
			}
		}

		BigDecimal bpiBalance = BigDecimal.ZERO;
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		Date firstRepayDate = null;
		if (finMain.isBpiResetReq()
				&& StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)
				&& StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHD_FIRSTEMI)) {
			for (int i = 1; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
				if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
					bpiBalance = curSchd.getProfitCalc();
				}

				if (curSchd.isRepayOnSchDate() && curSchd.isFrqDate()
						&& StringUtils.isEmpty(curSchd.getBpiOrHoliday())) {
					firstRepayDate = curSchd.getSchDate();
					break;
				}
			}
		}

		for (int i = indexStart; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			Date curSchdDate = curSchd.getSchDate();

			if (i < finMain.getRecalIdx()) {
				continue;
			}

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

			if (DateUtil.compare(curSchdDate, fromDate) >= 0
					&& (DateUtil.compare(curSchdDate, toDate) < 0 || (finMain.getNextRolloverDate() != null
							&& DateUtil.compare(curSchd.getSchDate(), finMain.getNextRolloverDate()) == 0))) {

				curSchd.setSchdMethod(schdMethod);

				boolean isFreezeSchd = false;

				if (curSchd.getPresentmentId() != 0
						&& !StringUtils.equals(finMain.getProcMethod(), FinanceConstants.FINSER_EVENT_RECEIPT)) {
					isFreezeSchd = true;
				}

				if ((curSchd.isRepayOnSchDate() || isManualAmtStep) && !isFreezeSchd
						&& !(StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI))) {
					if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
						if (firstRepayDate != null && DateUtility.compare(firstRepayDate, curSchdDate) == 0) {// bpi
																													// change
							curSchd.setRepayAmount(instructAmount.add(bpiBalance));
							bpiBalance = BigDecimal.ZERO;
						} else {
							curSchd.setRepayAmount(instructAmount);
						}
					} else if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)
							|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCPZ)) {
						curSchd.setRepayAmount(BigDecimal.ZERO);
					} else if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI_PFT)
							|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI)) {
						curSchd.setPrincipalSchd(instructAmount);
					} else if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCAP)) {
						curSchd.setRepayAmount(instructAmount);
					}
				} else if (curSchd.isPftOnSchDate()
						&& StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCAP)) {
					curSchd.setRepayAmount(instructAmount);
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
	 * Method : setFinanceTotals Description: Set Finance Totals after Grace and Repayment schedules calculation
	 * ________________________________________________________________________________________________________________
	 */
	private FinScheduleData setFinanceTotals(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		// FIXME: PV: 13MAY17: It is kept on the assumption reqMaturity fields
		// in not used any where else
		if (finMain.isNew() || StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			finMain.setReqMaturity(finMain.getCalMaturity());
		}

		// rebuild the schedule for FLEXI Loans
		if (finMain.isAlwFlexi()) {
			rebuildHybridFlexiSchd(finScheduleData);
		}

		FeeScheduleCalculator.feeSchdBuild(finScheduleData);

		List<FinanceScheduleDetail> finScheduleDetails = finScheduleData.getFinanceScheduleDetails();
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
		//PSD#166759
		int calTerms = 0;
		int graceTerms = 0;
		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);
		boolean ltdApplicable = SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_LOWER_TAX_DED_REQ);

		List<LowerTaxDeduction> ltdList = sortLowerTaxDeduction(finScheduleData.getLowerTaxDeductionDetails());
		LowerTaxDeduction ltd = null;
		BigDecimal ltdLimitByRcd = BigDecimal.ZERO;

		/*
		 * int recalIdx = finMain.getRecalIdx(); if (recalIdx < 0) { finScheduleData = setRecalIndex(finScheduleData);
		 * recalIdx = finMain.getRecalIdx(); }
		 */

		for (int i = 0; i < sdSize; i++) {
			curSchd = finScheduleDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (TDSCalculator.isTDSApplicable(finMain) && tdsPerc.compareTo(BigDecimal.ZERO) != 0) {

				boolean taxOnSysPerc = true;
				if (ltdApplicable) {
					BigDecimal tdsAmount = BigDecimal.ZERO;

					if (ltd == null || DateUtility.compare(schdDate, ltd.getEndDate()) > 0) {
						ltd = fetchLTDRecord(ltdList, schdDate);
						ltdLimitByRcd = BigDecimal.ZERO;
					}

					if (ltd != null) {
						tdsAmount = (curSchd.getProfitSchd().multiply(ltd.getPercentage())).divide(new BigDecimal(100),
								0, RoundingMode.HALF_DOWN);
						tdsAmount = CalculationUtil.roundAmount(tdsAmount, tdsRoundMode, tdsRoundingTarget);
						if (ltd.getLimitAmt().compareTo(BigDecimal.ZERO) > 0
								&& ltd.getLimitAmt().compareTo(ltdLimitByRcd.add(tdsAmount)) >= 0) {
							taxOnSysPerc = false;
						} else if (ltd.getLimitAmt().compareTo(BigDecimal.ZERO) == 0) {
							taxOnSysPerc = false;
						}
						ltdLimitByRcd = ltdLimitByRcd.add(tdsAmount);
					}
					curSchd.setTDSAmount(tdsAmount);
				}

				if (taxOnSysPerc) {
					BigDecimal tdsAmount = (curSchd.getProfitSchd().multiply(tdsPerc)).divide(new BigDecimal(100), 0,
							RoundingMode.HALF_DOWN);
					tdsAmount = CalculationUtil.roundAmount(tdsAmount, tdsRoundMode, tdsRoundingTarget);
					curSchd.setTDSAmount(tdsAmount);
				}
			}

			if (i == 0) {
				curSchd.setSchdMethod(finScheduleDetails.get(i + 1).getSchdMethod());
			}

			if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday()) && finMain.isModifyBpi()) {
				finMain.setBpiAmount(curSchd.getProfitSchd().subtract(curSchd.getTDSAmount()));
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

			if (((curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())
					&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI) && i != 0)
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_STRTPRDHLD)) {

				// PSD Ticket : 133179, Partial Settlement Cases
				if (curSchd.getRepayAmount().compareTo(curSchd.getPartialPaidAmt()) != 0 || curSchd.isFrqDate()) {
					instNumber = instNumber + 1;
					curSchd.setInstNumber(instNumber);
				} else {
					curSchd.setInstNumber(0);
				}
				// PSD#166759 Reset the CalTerms
				if (curSchd.isFrqDate() && DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) > 0) {
					calTerms = calTerms + 1;
				}

				// Reset Grace Terms -- PSD Ticket :136185
				if (curSchd.isFrqDate() && DateUtility.compare(schdDate, finMain.getGrcPeriodEndDate()) <= 0) {
					graceTerms = graceTerms + 1;
				} //End PSD#166759
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
			if (((finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance())
					|| finMain.isSanBsdSchdle()) && finMain.isResetOrgBal()) {
				if (DateUtility.compare(schdDate, finMain.getRecalFromDate()) >= 0
						|| DateUtility.compare(finMain.getEventFromDate(), finMain.getGrcPeriodEndDate()) < 0) {
					curSchd.setOrgEndBal(curSchd.getClosingBalance());
				}
			}
		}
		// PSD#169262:Issue while decreasing ROI when by Adjusting terms.
		// FIXME if Required: below condition added while decreasing the ROI for adjust terms.
		//add rate change tenor showing wrong when rate is decreased.
		if (!FinanceConstants.FINSER_EVENT_RATECHG.equals(finMain.getProcMethod())) {
			// PSD#166759
			finMain.setCalTerms(calTerms);
		}
		// Ticket id:PSD Ticket :136185,resetting grace terms
		finMain.setGraceTerms(graceTerms);//End PSD#166759

		finMain.setTotalGrossGrcPft(finMain.getTotalGraceCpz().add(finMain.getTotalGracePft()));
		finMain.setTotalGrossPft(finMain.getTotalProfit().add(finMain.getTotalCpz()));

		IRRCalculator.calculateXIRRAndIRR(finScheduleData, true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Fetching Lower Tax Deduction details
	 * 
	 * @param scheduleData
	 * @return
	 */
	private LowerTaxDeduction fetchLTDRecord(List<LowerTaxDeduction> ltdList, Date schDate) {

		if (ltdList == null || ltdList.isEmpty()) {
			return null;
		}

		for (int i = 0; i < ltdList.size(); i++) {
			LowerTaxDeduction taxDeduction = ltdList.get(i);

			// If No LTD for the Current Schedule Dates
			if (DateUtility.compare(taxDeduction.getStartDate(), schDate) > 0) {
				break;
			}

			// If Current LTD End date is more than Schedule Date
			if (DateUtility.compare(taxDeduction.getEndDate(), schDate) < 0) {
				continue;
			}
			// if End Date Greater than Start Date
			if (DateUtility.compare(taxDeduction.getEndDate(), taxDeduction.getStartDate()) <= 0) {
				continue;
			}

			// If LTD percentage less than Zero
			if (taxDeduction.getPercentage().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (DateUtility.compare(taxDeduction.getStartDate(), schDate) <= 0) {
				if (DateUtility.compare(taxDeduction.getEndDate(), schDate) >= 0) {
					return taxDeduction;
				}
			}
		}

		return null;
	}

	/**
	 * Method for calculate TDS Amount
	 * 
	 * @param finMain
	 * @param curSchd
	 * @param tdsPerc
	 * @return
	 */
	// FIXME Have to disucss with Siva and remove this method
	private BigDecimal calTDSAmount(FinanceMain finMain, FinanceScheduleDetail curSchd, BigDecimal tdsPerc) {

		BigDecimal tdsAmount = BigDecimal.ZERO;

		List<LowerTaxDeduction> ltdList = sortLowerTaxDeduction(finScheduleData.getLowerTaxDeductionDetails());
		LowerTaxDeduction ltd = null;
		BigDecimal ltdLimitByRcd = BigDecimal.ZERO;

		Date schdDate = new Date();
		boolean ltdApplicable = SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_LOWER_TAX_DED_REQ);

		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			schdDate = curSchd.getSchDate();

			if (TDSCalculator.isTDSApplicable(finMain) && tdsPerc.compareTo(BigDecimal.ZERO) != 0) {
				tdsAmount = null;
				boolean taxOnSysPerc = true;
				if (ltdApplicable) {

					if (ltd == null || DateUtility.compare(schdDate, ltd.getEndDate()) > 0) {
						ltd = fetchLTDRecord(ltdList, schdDate);
						ltdLimitByRcd = BigDecimal.ZERO;
					}

					if (ltd != null) {
						tdsAmount = (curSchd.getProfitSchd().multiply(ltd.getPercentage())).divide(new BigDecimal(100),
								0, RoundingMode.HALF_DOWN);
						tdsAmount = CalculationUtil.roundAmount(tdsAmount, finMain.getCalRoundingMode(),
								finMain.getRoundingTarget());
						if (ltd.getLimitAmt().compareTo(BigDecimal.ZERO) > 0
								&& ltd.getLimitAmt().compareTo(ltdLimitByRcd.add(tdsAmount)) >= 0) {
							taxOnSysPerc = false;
						} else if (ltd.getLimitAmt().compareTo(BigDecimal.ZERO) == 0) {
							taxOnSysPerc = false;
						}
						ltdLimitByRcd = ltdLimitByRcd.add(tdsAmount);
					}
				}

				if (taxOnSysPerc) {
					tdsAmount = (curSchd.getProfitSchd().multiply(tdsPerc)).divide(new BigDecimal(100), 0,
							RoundingMode.HALF_DOWN);
					tdsAmount = CalculationUtil.roundAmount(tdsAmount, finMain.getCalRoundingMode(),
							finMain.getRoundingTarget());

				}
				curSchd.setTDSAmount(tdsAmount);
			}
		}
		return tdsAmount;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : calSchdProcess Description : Calculate Schedule Process Process :
	 * ************************************************************************* ***************************************
	 * 
	 * /** To Calculate the Amounts for given schedule
	 */
	private FinScheduleData calSchdProcess(FinScheduleData finScheduleData, Boolean isCalFlat, boolean isFirstRun) {
		logger.debug("Entering");

		boolean cpzPosIntact = SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT);
		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setCpzPosIntact(cpzPosIntact);

		// START PROCESS
		finScheduleData = fetchRatesHistory(finScheduleData);

		// If Errors Exists in calculation, return back
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return finScheduleData;
		}

		finScheduleData = fetchGraceCurRates(finScheduleData);
		finScheduleData = fetchRepayCurRates(finScheduleData);

		if (finMain.isStepFinance() && finMain.isAlwManualSteps()
				&& (!CalculationConstants.RPYCHG_ADJMDT.equals(finMain.getRecalType()))) {
			if (StringUtils.equals(finMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
				prepareManualRepayRI(finScheduleData);
				//finMain.setEqualRepay(false);
			}
		}
		finScheduleData = getRpyInstructDetails(finScheduleData);

		/* Grace Schedule calculation */
		finScheduleData = graceSchdCal(finScheduleData);

		if (isFirstRun) {
			finScheduleData = prepareFirstSchdCal(finScheduleData);

			if (finMain.isStepFinance() && !finMain.isAlwManualSteps()) {
				finScheduleData = adjustBPISchd(finScheduleData);
				finScheduleData = setFinanceTotals(finScheduleData);
				logger.debug("Leaving");
				return finScheduleData;
			}

			finScheduleData = getRpyInstructDetails(finScheduleData);

			/* Grace Schedule calculation */
			if (AdvanceType.hasAdvEMI(finMain.getAdvType()) && AdvanceStage.hasFrontEnd(finMain.getAdvStage())
					&& finScheduleData.getFinanceMain().getAdvTerms() > 0) {
				finScheduleData.getFinanceMain().setAdjustClosingBal(true);
				finScheduleData = graceSchdCal(finScheduleData);
			}
		}

		if (finMain.isRecalSteps()) {
			finScheduleData = calStepSchd(finScheduleData);
		}

		finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

		if (finMain.isEqualRepay() && finMain.isCalculateRepay()
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
				&& !StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFTCPZ)
				&& !StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, finMain.getProductCategory())) {

			if (AdvanceType.hasAdvEMI(finMain.getAdvType()) && AdvanceStage.hasFrontEnd(finMain.getAdvStage())
					&& finMain.getAdvTerms() > 0 && isFirstRun) {
				finMain.setAdjustClosingBal(true);
			}

			finScheduleData = calEqualPayment(finScheduleData);
			// equalRepayCal(finScheduleData);
		} else {
			if (!isFirstRun) {
				finScheduleData = pmtCalc(finScheduleData);
			}
		}

		finScheduleData = adjustBPISchd(finScheduleData);
		finScheduleData = setFinanceTotals(finScheduleData);

		finScheduleData.getFinanceScheduleDetails().get(0)
				.setSchdMethod(finScheduleData.getFinanceScheduleDetails().get(1).getSchdMethod());

		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : fetchGraceCurRates Description : SET CURRENT GRACE RATES Process :
	 * ************************************************************************* ***************************************
	 */
	private FinScheduleData fetchGraceCurRates(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		if (finMain.isSkipRateReset()) {
			logger.debug("Leaving - Skip Base/Special Rate Change");
			return finScheduleData;
		}

		if (!finMain.isRateChange()) {
			logger.debug("Leaving - Not Rate Change Method");
			return finScheduleData;
		}

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		Date dateAllowedChange = finMain.getLastRepayRvwDate();

		if (DateUtility.compare(dateAllowedChange, finMain.getGrcPeriodEndDate()) >= 0) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		// FIND ALLOWED RATE CHANGE DATE
		String rvwRateApplFor = finMain.getRvwRateApplFor();
		if (StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)) {
			dateAllowedChange = findAllowedChangeDate(finScheduleData, rvwRateApplFor, dateAllowedChange);
		}

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = finSchdDetails.size();
		BigDecimal prvRate = BigDecimal.ZERO;

		// Set Rates from Allowed Date and Grace Period End Date
		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			finMain.setSchdIndex(i);

			if (DateUtil.compare(curSchd.getSchDate(), dateAllowedChange) < 0) {
				prvRate = curSchd.getCalculatedRate();
				continue;
			}

			// SATYA TODO: 08DEC18 : Issue Facing with Multiple Base Rates.
			/*
			 * if (DateUtility.compare(curSchd.getSchDate(), finMain.getEventFromDate()) < 0) { prvRate =
			 * curSchd.getCalculatedRate(); continue; }
			 * 
			 * if (DateUtility.compare(curSchd.getSchDate(), finMain.getEventToDate()) > 0) { prvRate =
			 * curSchd.getCalculatedRate(); break; }
			 */

			if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) >= 0) {
				prvRate = curSchd.getCalculatedRate();
				break;
			}

			// Rate Change Required
			boolean rateChangeReq = false;
			if (finMain.isAllowSubvention() && finScheduleData.getSubventionDetail() != null) {
				if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_PARTIAL,
						finScheduleData.getSubventionDetail().getType())) {
					rateChangeReq = true;
				} else if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL,
						finScheduleData.getSubventionDetail().getType())
						&& DateUtility.compare(curSchd.getSchDate(),
								finScheduleData.getSubventionDetail().getEndDate()) >= 0) {
					rateChangeReq = true;
				}
			} else {
				rateChangeReq = true;
			}

			// Fetch current rates from DB
			if (rateChangeReq && StringUtils.isNotEmpty(curSchd.getBaseRate())) {
				if (curSchd.isRvwOnSchDate() || i == 0
						|| (StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)
								&& DateUtility.compare(curSchd.getSchDate(), dateAllowedChange) == 0)) {
					curSchd.setCalculatedRate(RateUtil.ratesFromLoadedData(finScheduleData, i));
				} else {
					curSchd.setCalculatedRate(finSchdDetails.get(i - 1).getCalculatedRate());
				}

				curSchd.setCalOnIndRate(false);
			}

			// Subvention Rate Reset
			if (finMain.isAllowSubvention() && finScheduleData.getSubventionDetail() != null && DateUtility
					.compare(curSchd.getSchDate(), finScheduleData.getSubventionDetail().getEndDate()) < 0) {

				String subventionType = finScheduleData.getSubventionDetail().getType();

				if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL, subventionType)) {
					if (DateUtility.compare(curSchd.getSchDate(),
							finScheduleData.getSubventionDetail().getEndDate()) != 0) {
						curSchd.setCalculatedRate(BigDecimal.ZERO);
					}
				} else if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_PARTIAL, subventionType)) {
					BigDecimal subventionRate = BigDecimal.ZERO;
					if (finScheduleData.getSubventionDetail() != null) {
						subventionRate = finScheduleData.getSubventionDetail().getRate();
					}
					curSchd.setCalculatedRate(curSchd.getCalculatedRate().subtract(subventionRate));
				}

				if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_PARTIAL, subventionType)
						&& "N".equalsIgnoreCase(SysParamUtil.getValueAsString("ADDRATE_CHANGE_IMMEDIATELY"))) {
					// To avoid Rate reset for already subvention Disbursement
					// details
					if (curSchd.isDisbOnSchDate()) {
						prvRate = curSchd.getCalculatedRate();
					}
					if (prvRate.compareTo(curSchd.getCalculatedRate()) != 0) {
						curSchd.setCalculatedRate(prvRate);
					}
				}
			}

			if (curSchd.getCalculatedRate().compareTo(BigDecimal.ZERO) < 0) {
				curSchd.setCalculatedRate(BigDecimal.ZERO);
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : fetchRepayCurRates Description : SET CURRENT REPAY RATES Process :
	 * ************************************************************************* ***************************************
	 */
	private FinScheduleData fetchRepayCurRates(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		FinanceMain finMain = finScheduleData.getFinanceMain();

		if (finMain.isSkipRateReset()) {
			logger.debug("Leaving - Skip Base/Special Rate Change");
			return finScheduleData;
		}

		if (!finMain.isRateChange()) {
			logger.debug("Leaving - Not Rate Change Method");
			return finScheduleData;
		}

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		Date dateAllowedChange = finMain.getLastRepayRvwDate();
		int fixedRateTenor = finMain.getFixedRateTenor();
		/*
		 * Date fixedTenorEndDate = DateUtility.addMonths(finMain.getGrcPeriodEndDate(), fixedRateTenor > 0 ?
		 * fixedRateTenor : 0);
		 */
		Date fixedTenorEndDate = finMain.getFinStartDate();

		if (fixedRateTenor > 0) {
			fixedTenorEndDate = finMain.getNextRepayDate();
			for (int i = 0; i < (fixedRateTenor - 1); i++) {
				fixedTenorEndDate = DateUtility.getDate(DateUtility.format(
						FrequencyUtil.getNextDate(finMain.getRepayFrq(), 1, fixedTenorEndDate,
								HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
						PennantConstants.dateFormat));
			}
		}

		// PROFIT LAST REVIEW IS ON OR AFTER MATURITY THEN NOT ALLOWED THEN DO
		// NOT SET
		if (DateUtility.compare(dateAllowedChange, finMain.getMaturityDate()) >= 0) {
			logger.debug("Leaving");
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

			if (DateUtil.compare(curSchd.getSchDate(), dateAllowedChange) < 0) {
				continue;
			}

			// TODO: 27JUN17: PV After Successful unit test cases execution and
			// various normal tests, it can be removed.
			/*
			 * if (curSchd.getSchDate().compareTo(finMain.getEventFromDate()) < 0) { continue; }
			 */
			/*
			 * if (curSchd.getSchDate().compareTo(finMain.getEventToDate()) > 0) { break; }
			 */
			// Fetch current rates from DB
			if (DateUtility.compare(curSchd.getSchDate(), (fixedTenorEndDate)) < 0
					&& DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) >= 0) {
				curSchd.setCalculatedRate(finMain.getFixedTenorRate());
				fixedRateTenor = fixedRateTenor > 0 ? fixedRateTenor - 1 : fixedRateTenor;
			} else {
				if (StringUtils.isNotEmpty(curSchd.getBaseRate())) {
					if (curSchd.isRvwOnSchDate() || i == 0
							|| (DateUtil.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) == 0
									&& finMain.isFinIsRateRvwAtGrcEnd())
							|| (StringUtils.trimToEmpty(finMain.getRvwRateApplFor())
									.equals(CalculationConstants.RATEREVIEW_RVWUPR)
									&& DateUtility.compare(curSchd.getSchDate(), dateAllowedChange) == 0)) {
						curSchd.setCalculatedRate(RateUtil.ratesFromLoadedData(finScheduleData, i));
					} else {
						if (fixedRateTenor == 0) {
							curSchd.setCalculatedRate(RateUtil.ratesFromLoadedData(finScheduleData, i));
						} else if (curSchd.getSchDate().compareTo(fixedTenorEndDate) == 0) {
							curSchd.setCalculatedRate(RateUtil.ratesFromLoadedData(finScheduleData, i));
						} else {
							curSchd.setCalculatedRate(finSchdDetails.get(i - 1).getCalculatedRate());
						}
					}
					curSchd.setCalOnIndRate(false);
				}
			}
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : findAllowedChangeDate Description : FIND DATE FROM WHICH RATE CHANGE IS ALLOWED Process :
	 * *********************************************** *****************************************************************
	 */

	private Date findAllowedChangeDate(FinScheduleData finScheduleData, String rvwRateApplFor, Date dateAllowedChange) {
		logger.debug("Entering");

		if (StringUtils.equals(finScheduleData.getFinanceMain().getProcMethod(),
				FinanceConstants.FINSER_EVENT_RECEIPT)) {
			logger.debug("Leaving");
			return finScheduleData.getFinanceMain().getMaturityDate();
		}

		if (!StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)) {
			return dateAllowedChange;
		}

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
	 * ************************************************************************* ***************************************
	 * Method : graceSchdCal Description : Grace period schedule calculation for reducing rate Process :
	 * *************************************************** *************************************************************
	 */

	private FinScheduleData graceSchdCal(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();

		finMain.setPftForSelectedPeriod(BigDecimal.ZERO);
		finMain.setSchdIndex(0);

		int sdSize = schdDetails.size();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		int recalIdx = finMain.getRecalIdx();
		if (recalIdx < 0) {
			finScheduleData = setRecalIndex(finScheduleData);
			recalIdx = finMain.getRecalIdx();
		}

		/* Loop through grace period schedule */
		for (int i = 0; i < sdSize; i++) {
			curSchd = schdDetails.get(i);

			// If first record no calculation is required
			if (i == 0) {
				prepareFirstGraceRcd(finScheduleData);
			} else {

				if (i >= recalIdx) {
					prepareRemainingGraceRcd(finScheduleData, i);
				}
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

	private FinScheduleData setRecalIndex(FinScheduleData fsd) {
		FinanceMain finMain = fsd.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = fsd.getFinanceScheduleDetails();
		Date evtFromDate = finMain.getEventFromDate();

		if (evtFromDate == null) {
			evtFromDate = finMain.getFinStartDate();
		}

		if (evtFromDate.compareTo(finMain.getFinStartDate()) < 0) {
			evtFromDate = finMain.getFinStartDate();
		}

		boolean isPftCpzFromReset = false;
		if (StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFTCPZ)) {
			isPftCpzFromReset = true;
		}

		finMain.setPftCpzFromReset(BigDecimal.ZERO);
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = schdDetails.size();
		for (int i = 0; i < sdSize; i++) {
			curSchd = schdDetails.get(i);
			if (curSchd.getSchDate().compareTo(evtFromDate) < 0) {

				if (finMain.isCpzPosIntact()) {
					finMain.setPftCpzFromReset(BigDecimal.ZERO);
				} else {
					if (isPftCpzFromReset && curSchd.isCpzOnSchDate() && curSchd.isRepayOnSchDate()) {
						finMain.setPftCpzFromReset(BigDecimal.ZERO);
					} else {
						finMain.setPftCpzFromReset(finMain.getPftCpzFromReset().add(curSchd.getCpzAmount()));
					}
				}

				continue;
			}

			finMain.setRecalIdx(i);
			break;
		}

		if (finMain.getRecalIdx() < 0) {
			finMain.setRecalIdx(sdSize - 1);
		}

		return fsd;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : repaySchdCal Description : Repay period schedule calculation for reducing rate Process :
	 * *************************************************** ************************************************************
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

		if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) == 0) {
			if (finMain.isFinIsRateRvwAtGrcEnd()) {
				curSchd.setRvwOnSchDate(true);
			}
		}

		if (AdvanceType.hasAdvEMI(finMain.getAdvType())) {
			if (finMain.isAdjustClosingBal()) {
				curSchd.setClosingBalance(curSchd.getClosingBalance().subtract(finMain.getAdvanceEMI()));
				finMain.setAdjustClosingBal(false);
			}
		}

		if (finMain.isAllowGrcPeriod()) {
			curSchd.setPftDaysBasis(finMain.getGrcProfitDaysBasis());
		} else {
			curSchd.setPftDaysBasis(finMain.getProfitDaysBasis());
		}

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
	 * ************************************************************************* ***************************************
	 * Method : repaySchdCal Description : Repay period schedule calculation for reducing rate Process :
	 * *************************************************** ************************************************************
	 */

	private FinScheduleData prepareRemainingGraceRcd(FinScheduleData finScheduleData, int iCur) {
		logger.debug("Entering");
		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(iCur);
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(iCur - 1);

		// TODO: TO AVOID RECALCULATION OF SCHEDULES COMPLETED
		if (finMain.getRecalIdx() < 0) {
			finScheduleData = setRecalIndex(finScheduleData);
		}

		if (iCur < finMain.getRecalIdx()) {
			return finScheduleData;
		}

		boolean cpzPOSIntact = finMain.isCpzPosIntact();

		if (!finMain.getGrcRateBasis().equals(CalculationConstants.RATE_BASIS_F)) {
			if (cpzPOSIntact) {
				curSchd.setBalanceForPftCal(prvSchd.getClosingBalance().add(prvSchd.getCpzBalance()));
			} else {
				curSchd.setBalanceForPftCal(prvSchd.getClosingBalance());
			}

		} else {
			// PV 11OCT19: Compounding development not considered for Flat rate
			curSchd.setBalanceForPftCal(prvSchd.getBalanceForPftCal().add(prvSchd.getDisbAmount())
					.subtract(prvSchd.getDownPaymentAmount()).add(prvSchd.getFeeChargeAmt()).add(prvSchd.getCpzAmount())
					.subtract(prvSchd.getPrincipalSchd()));
		}

		curSchd.setNoOfDays(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchd.getSchDate()));
		curSchd.setDayFactor(
				CalculationUtil.getInterestDays(prvSchd.getSchDate(), curSchd.getSchDate(), curSchd.getPftDaysBasis()));

		// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);

		BigDecimal prvPftFraction = prvSchd.getProfitFraction();
		if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_NO_ADJ)
				|| (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)
						&& DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) != 0)) {

			prvPftFraction = BigDecimal.ZERO;
		}

		/* Calculate interest and set interest payment details */
		BigDecimal calint = CalculationUtil.calInterest(prvSchd.getSchDate(), curSchd.getSchDate(),
				curSchd.getBalanceForPftCal(), curSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

		calint = calint.add(prvPftFraction);

		BigDecimal calIntRounded = BigDecimal.ZERO;
		if (calint.compareTo(BigDecimal.ZERO) > 0) {
			calIntRounded = CalculationUtil.roundAmount(calint, finMain.getCalRoundingMode(),
					finMain.getRoundingTarget());
		}

		BigDecimal calIntFraction = calint.subtract(calIntRounded);
		if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)) {
			calIntFraction = calIntFraction.add(prvSchd.getProfitFraction());
		}

		curSchd.setProfitFraction(calIntFraction);
		calint = calIntRounded;

		curSchd.setProfitCalc(calint);
		curSchd.setRepayComplete(false);

		if (!curSchd.isPftOnSchDate()) {
			if (!finMain.isAlwFlexi()) {
				curSchd.setRepayAmount(BigDecimal.ZERO);
				curSchd.setPrincipalSchd(BigDecimal.ZERO);
				curSchd.setProfitSchd(BigDecimal.ZERO);
			}
		} else {

			// FIX: PV: To address Postponements, Reage, Unplanned Holidays,
			// Holidays without additional instructions
			if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_POSTPONE)
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_MORTEMIHOLIDAY)
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
				curSchd.setProfitSchd(curSchd.getSchdPftPaid());
				curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
				curSchd.setRepayAmount(curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid()));
			} else {
				calPftPriRpy(finScheduleData, iCur, (iCur - 1), finMain.getEventFromDate(), cpzPOSIntact);
				curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
			}
		}

		/* Balance unpaid interest */
		curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, cpzPOSIntact));

		/*
		 * If grace period end date and allow grace period is true but capitalize on schedule date is false OR
		 * capitalize at end of grace is True THEN force it to true
		 */

		// 28-08 Fix for 128478 : Error while capitalize interest into principal
		// OS at the end of grace period
		if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) == 0
				&& (finMain.isAllowGrcCpz() || finMain.isCpzAtGraceEnd())) {
			curSchd.setCpzOnSchDate(true);
			if (ImplementationConstants.NON_FRQ_CAPITALISATION) {
				if (!FrequencyUtil.isFrqDate(finMain.getGrcCpzFrq(), finMain.getGrcPeriodEndDate())) {
					curSchd.setCpzOnSchDate(finMain.isCpzAtGraceEnd());
				}
			}
		}

		// In the Process of Rate Change, for Future Review Period method, schedule should not modify for Past Due
		// schedules
		// Because of Installment dues already passed for the same
		boolean cpzResetReq = true;
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_RATECHG, finMain.getProcMethod())) {
			if (StringUtils.equals(CalculationConstants.RATEREVIEW_RVWFUR, finMain.getRvwRateApplFor())) {
				if (DateUtility.compare(curSchd.getSchDate(), finMain.getAppDate()) <= 0) {
					cpzResetReq = false;
				}
			}
		}

		setCpzAmounts(prvSchd, curSchd, cpzPOSIntact, cpzResetReq);

		curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, finMain.getProfitDaysBasis(), cpzPOSIntact));

		if (DateUtility.compare(curSchd.getSchDate(), finMain.getEventFromDate()) > 0
				&& DateUtility.compare(curSchd.getSchDate(), finMain.getEventToDate()) <= 0) {
			finMain.setPftForSelectedPeriod(finMain.getPftForSelectedPeriod().add(curSchd.getProfitCalc()));
		}

		logger.debug("Leaving");
		return finScheduleData;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : repaySchdCal Description : Repay period schedule calculation for reducing rate Process :
	 * *************************************************** ************************************************************
	 */

	private FinScheduleData repaySchdCal(FinScheduleData finScheduleData, Boolean isCalFlat) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setPftCpzFromReset(BigDecimal.ZERO);

		boolean isRepayComplete = false;
		boolean isSanctionBasedSchd = finMain.isSanBsdSchdle();

		finMain.setCalTerms(0);

		BigDecimal calIntFraction = BigDecimal.ZERO;
		BigDecimal calInt = BigDecimal.valueOf(0.0);
		Date derivedMDT = finMain.getMaturityDate();
		int advEMITerms = finMain.getAdvTerms();

		String repayRateBasis = finMain.getRepayRateBasis();

		// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);

		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();
		finMain.setNewMaturityIndex(schdDetails.size() - 1);
		int schdDetailsSize = schdDetails.size();

		// FIND LAST REPAYMENT SCHEDULE DATE
		int schdIndex = finMain.getSchdIndex();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		FinanceScheduleDetail prvSchd = new FinanceScheduleDetail();

		// Start Period Holiday Functionality
		boolean alwStrtPrdHday = finMain.isAlwStrtPrdHday();
		int strtPrdHdays = finMain.getStrtPrdHdays();
		int usedStrtPrdHdays = 0;

		if (AdvanceType.hasAdvEMI(finMain.getAdvType()) && AdvanceStage.hasFrontEnd(finMain.getAdvStage())) {
			derivedMDT = schdDetails.get(schdDetails.size() - advEMITerms - 1).getSchDate();
			finMain.setNewMaturityIndex(schdDetails.size() - advEMITerms - 1);
		} else {
			finMain.setNewMaturityIndex(schdDetails.size() - 1);
			derivedMDT = schdDetails.get(schdDetails.size() - 1).getSchDate();
		}
		// common issue 16
		if (FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(finMain.getReceiptPurpose())) {
			derivedMDT = finMain.getEventFromDate();
		}

		int recalIdx = finMain.getRecalIdx();
		if (recalIdx < 0) {
			finScheduleData = setRecalIndex(finScheduleData);
			recalIdx = finMain.getRecalIdx();
		} else {
			calIntFraction = prvSchd.getProfitFraction();
		}

		boolean cpzPOSIntact = finMain.isCpzPosIntact();

		for (int i = schdIndex + 1; i < schdDetailsSize; i++) {

			curSchd = schdDetails.get(i);

			if (i < recalIdx) {
				if (curSchd.isRepayOnSchDate()) {
					finScheduleData.getFinanceMain().setCalTerms(finScheduleData.getFinanceMain().getCalTerms() + 1);
				}
				continue;
			}

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
					// PV 11OCT19: Compounding development not considered for
					// Flat rate
					curSchd.setBalanceForPftCal(prvBalanceForPftCal.add(prvDisbAmount).subtract(prvDownPaymentAmount)
							.add(prvCpzAmount).add(prvFeeChargeAmt));

				} else {

					if (cpzPOSIntact) {
						curSchd.setBalanceForPftCal(prvSchd.getClosingBalance().add(prvSchd.getCpzBalance()));
					} else {
						curSchd.setBalanceForPftCal(prvSchd.getClosingBalance());
					}
				}
			}

			curSchd.setNoOfDays(DateUtility.getDaysBetween(curSchDate, prvSchDate));
			curSchd.setDayFactor(CalculationUtil.getInterestDays(prvSchDate, curSchDate, curSchd.getPftDaysBasis()));

			// Calculate Interest
			if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {

				if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_NO_ADJ)
						|| (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)
								&& DateUtility.compare(curSchd.getSchDate(), finMain.getMaturityDate()) != 0)) {

					calIntFraction = BigDecimal.ZERO;
				}

				calInt = CalculationUtil.calInterest(prvSchDate, curSchDate, curSchd.getBalanceForPftCal(),
						curSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

				calInt = calInt.add(calIntFraction);
				BigDecimal calIntRounded = BigDecimal.ZERO;
				if (calInt.compareTo(BigDecimal.ZERO) > 0) {
					calIntRounded = CalculationUtil.roundAmount(calInt, finMain.getCalRoundingMode(),
							finMain.getRoundingTarget());
				}
				calIntFraction = calInt.subtract(calIntRounded);
				calInt = calIntRounded;

				if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)) {
					calIntFraction = calIntFraction.add(prvSchd.getProfitFraction());
				}

				curSchd.setRepayComplete(false);
				curSchd.setProfitFraction(calIntFraction);
			} else {
				calInt = BigDecimal.ZERO;
				curSchd.setRepayComplete(true);
			}

			curSchd.setProfitCalc(calInt);

			// Start Period Holiday Functionality
			if (alwStrtPrdHday && strtPrdHdays > 0) {
				if (usedStrtPrdHdays < strtPrdHdays) {
					if (curSchd.isPftOnSchDate() && curSchd.isFrqDate()) {
						curSchd.setBpiOrHoliday(FinanceConstants.FLAG_STRTPRDHLD);
						usedStrtPrdHdays = usedStrtPrdHdays + 1;
					}
				} else if (usedStrtPrdHdays == strtPrdHdays) {
					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_STRTPRDHLD)) {
						curSchd.setBpiOrHoliday(null);
					}
				}
			}

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
				curSchd.setCpzBalance(BigDecimal.ZERO);
			}

			// LAST REPAYMENT DATE
			if ((DateUtility.compare(curSchDate, derivedMDT) == 0) && !isRolloverDate) {
				finScheduleData = procMDTRecord(finScheduleData, i, isRepayComplete, cpzPOSIntact);
				//common issue 22:if earlysettlement doing on bpi schedule date.
				if (FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())) {
					curSchd.setBpiOrHoliday(null);
				}
				isRepayComplete = true;
			}

			if (!isRepayComplete) {

				if (curSchd.isRepayOnSchDate()) {
					curSchd = calPftPriRpy(finScheduleData, i, (i - 1), finMain.getEventFromDate(), cpzPOSIntact);
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
							if (curSchd.getPrincipalSchd()
									.compareTo(prvClosingBalance.add(curSchd.getDisbAmount())) > 0) {
								curSchd.setPrincipalSchd(prvClosingBalance.add(curSchd.getDisbAmount()));
								curSchd.setProfitSchd(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())
										.add(curSchd.getProfitCalc()));
								curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
								isRepayComplete = true;
							}
						} else if (curSchd.getPrincipalSchd()
								.compareTo(prvClosingBalance.add(curSchd.getDisbAmount())) >= 0) {
							curSchd.setPrincipalSchd(prvClosingBalance.add(curSchd.getDisbAmount()));

							if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)
									&& !curSchd.isFrqDate()) {
								calInt = CalculationUtil.calInterest(prvSchDate, curSchDate,
										curSchd.getBalanceForPftCal(), curSchd.getPftDaysBasis(),
										prvSchd.getCalculatedRate());

								if (calInt.add(prvSchd.getProfitFraction()).compareTo(BigDecimal.ZERO) <= 0) {
									calInt = BigDecimal.ZERO;
								} else {
									calInt = calInt.add(prvSchd.getProfitFraction());
								}

								if (calInt.compareTo(BigDecimal.ZERO) > 0) {
									calInt = CalculationUtil.roundAmount(calInt, finMain.getCalRoundingMode(),
											finMain.getRoundingTarget());
								}
							}
							curSchd.setProfitCalc(calInt);
							BigDecimal newProfit = prvSchd.getProfitBalance().add(curSchd.getProfitCalc());
							newProfit = newProfit.subtract(prvSchd.getCpzAmount());

							if (prvSchd.isCpzOnSchDate()) {
								newProfit = newProfit.add(prvSchd.getCpzBalance());
							}

							curSchd.setProfitSchd(newProfit);

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

								if (!cpzPOSIntact) {
									curSchd.setProfitCalc(curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance())
											.add(prvSchd.getCpzAmount()));
								} else {
									curSchd.setProfitCalc(curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance()));
								}
							}

							if (!isSanctionBasedSchd) {
								isRepayComplete = true;
							}

						}
					}

					/* Count Repay schedules only */
					finMain.setCalTerms(finMain.getCalTerms() + 1);
					finMain.setCalMaturity(curSchDate);

				} else if (curSchd.isPftOnSchDate()) {

					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_POSTPONE)
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_STRTPRDHLD)
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_MORTEMIHOLIDAY)
							|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {

						curSchd.setProfitSchd(curSchd.getSchdPftPaid());
						curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
						curSchd.setRepayAmount(curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid()));

					} else {
						// BPI changes
						if (finMain.isBpiResetReq()) {
							if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
									&& !StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE)
									&& !StringUtils.equals(finMain.getBpiTreatment(),
											FinanceConstants.BPI_DISBURSMENT)) {
								curSchd.setProfitSchd(curSchd.getSchdPftPaid());
							} else {
								curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finMain, false));
							}
						} else {
							curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finMain, false));
						}
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
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_STRTPRDHLD)) {

						if (StringUtils.equals(finMain.getStrtprdCpzMethod(),
								CalculationConstants.STRTPRDCPZ_CPZONFRQ)) {
							curSchd.setCpzOnSchDate(true);
						} else if (StringUtils.equals(finMain.getStrtprdCpzMethod(),
								CalculationConstants.STRTPRDCPZ_CPZATEND)) {
							if (usedStrtPrdHdays == strtPrdHdays) {
								curSchd.setCpzOnSchDate(true);
							} else {
								curSchd.setCpzOnSchDate(false);
							}
						}
					}
				} else if (curSchd.isCpzOnSchDate()) {
					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_STRTPRDHLD)) {
						if (StringUtils.equals(finMain.getStrtprdCpzMethod(), CalculationConstants.STRTPRDCPZ_NOCPZ)) {
							curSchd.setCpzOnSchDate(false);
						} else if (StringUtils.equals(finMain.getStrtprdCpzMethod(),
								CalculationConstants.STRTPRDCPZ_CPZATEND)) {
							if (usedStrtPrdHdays == strtPrdHdays) {
								curSchd.setCpzOnSchDate(true);
							} else {
								curSchd.setCpzOnSchDate(false);
							}
						}

					} else if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())) {
						if (!finMain.isAllowRepayCpz()
								|| !FrequencyUtil.isFrqDate(finMain.getRepayCpzFrq(), curSchDate)) {
							curSchd.setCpzOnSchDate(false);
						}
					}
				}

				curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, cpzPOSIntact));

				// In the Process of Rate Change, for Future Review Period method, schedule should not modify for Past
				// Due schedules
				// Because of Installment dues already passed for the same
				boolean cpzResetReq = true;
				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_RATECHG, finMain.getProcMethod())) {
					if (StringUtils.equals(CalculationConstants.RATEREVIEW_RVWFUR, finMain.getRvwRateApplFor())) {
						if (DateUtility.compare(curSchd.getSchDate(), finMain.getAppDate()) <= 0) {
							cpzResetReq = false;
						}
					}
				}

				setCpzAmounts(prvSchd, curSchd, cpzPOSIntact, cpzResetReq);
				if (!cpzPOSIntact) {
					finMain.setPftCpzFromReset(finMain.getPftCpzFromReset().add(curSchd.getCpzAmount()));
				}

				curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, repayRateBasis, cpzPOSIntact));

				// 08-JAN-2018 : When Rounding Effect creates new Record with
				// Negative values after Closing Balance using Profit Balance
				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					curSchd.setClosingBalance(BigDecimal.ZERO);

					int roundingTarget = finMain.getRoundingTarget();
					BigDecimal pftBal = curSchd.getProfitBalance();
					if (curSchd.getProfitBalance().compareTo(BigDecimal.ZERO) < 0) {
						pftBal = curSchd.getProfitBalance().negate();
					}
					if (pftBal.compareTo(new BigDecimal(roundingTarget)) < 0) {
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
	 * ************************************************************************* ***************************************
	 * Method : calPftPriRpy Description : Calculate profit and principal for schedule payment Process :
	 * ************************************************ ****************************************************************
	 */
	private FinanceScheduleDetail calPftPriRpy(FinScheduleData finScheduleData, int iCur, int iPrv, Date evtFromDate,
			boolean cpzPOSIntact) {

		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType finType = finScheduleData.getFinanceType();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(iCur);
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(iPrv);
		BigDecimal schdInterest = BigDecimal.ZERO;

		// FIX: PV: To address Postponements, Reage, Unplanned Holidays,
		// Holidays without additional instructions
		String bpiOrHoliday = curSchd.getBpiOrHoliday();
		if (!FinanceConstants.FINSER_EVENT_RECEIPT.equals(finMain.getProcMethod())
				|| DateUtility.compare(evtFromDate, curSchd.getSchDate()) != 0) {
			if (FinanceConstants.FLAG_HOLIDAY.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_POSTPONE.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_REAGE.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_STRTPRDHLD.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_MORTEMIHOLIDAY.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_UNPLANNED.equals(bpiOrHoliday)) {
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
		}

		// FIXME: PV 23MAY17
		// NEW CODE ADDED HERE TO AVOID CHANGES RELATED TO COMPLETED SCHEDULES.
		// LIKE IF DUE SENT FOR PRESENTMENT THEN SCHEDULE SHOULD NOT CHANGE
		// MEANS BALANCE WILL BE ADJUSTED TO NEXT SCHEDULES
		/*
		 * if (curSchd.getSchDate().compareTo(finMain.getRecalFromDate()) < 0) { return curSchd; }
		 */

		// SIVA : For Presentment Detail Schedule should be recalculate on
		// Schedule Maintenance
		if (curSchd.getPresentmentId() > 0
				&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_RECEIPT, finMain.getProcMethod())) {

			// This case should not be applicable only for Partial Settlement
			// For Partial Settlement in case of Presentment exists after value
			// date of event, schedule should be recalculated
			// For other Servicing actions, even presentment exists in future
			// after event action value date, it should not be recalculated
			// In Screen level we are restricting Recalculation from date for
			// Presentment cases, but in case of PRI_PFT & PFT schedule methods
			// it will be auto calculated and adjusted.
			if ((curSchd.getProfitCalc().add(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())))
					.compareTo(curSchd.getProfitSchd()) > 0) {
				curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
				return curSchd;
			}
		}

		// In the Process of Rate Change, for Future Review Period method, schedule should not modify for Past Due
		// schedules
		// Because of Installment dues already passed for the same
		boolean protectPftSchd = finMain.isProtectSchdPft();
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_RATECHG, finMain.getProcMethod())) {
			if (StringUtils.equals(CalculationConstants.RATEREVIEW_RVWFUR, finMain.getRvwRateApplFor())) {
				if (DateUtility.compare(curSchd.getSchDate(), finMain.getAppDate()) <= 0) {
					protectPftSchd = true;
				}

				// On maturity Date case, default total Will adjust
				if (DateUtility.compare(curSchd.getSchDate(), finMain.getMaturityDate()) == 0) {
					protectPftSchd = false;
				}
			}
		}

		// If Schedule recalculation has Lock for the particular schedule term,
		// it should not recalculate
		if (curSchd.isRecalLock()) {
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {
				curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
				return curSchd;
			}
		}

		// NO PAYMENT: Applicable for Grace Period And REPAYMENT period with PFT
		// or PRI+PFT)
		if (CalculationConstants.SCHMTHD_NOPAY.equals(curSchd.getSchdMethod())) {
			if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
					&& (StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)
							|| StringUtils.equals(finMain.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE))) {// Bpi
																																																																							// changes
				schdInterest = curSchd.getProfitCalc();

				// FIXME: PV 02JUN18 WHY BELOW CODE IS REQUIRED?. Commented for
				// testing
				/*
				 * if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) { if
				 * (!finMain.isAllowGrcCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } } else { if
				 * (!finMain.isAllowRepayCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } }
				 */

				curSchd.setProfitSchd(schdInterest);
			} else {
				curSchd.setProfitSchd(BigDecimal.ZERO);
			}

			curSchd.setPrincipalSchd(BigDecimal.ZERO);

			// EQUAL PAYMENT: Applicable for REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_EQUAL.equals(curSchd.getSchdMethod())) {

			if (!protectPftSchd) {
				BigDecimal pftToSchd = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finMain, false);

				if (pftToSchd.compareTo(curSchd.getRepayAmount()) > 0
						&& !finScheduleData.getFinanceType().isAllowPftBal()) {
					curSchd.setProfitSchd(pftToSchd);
					curSchd.setPrincipalSchd(BigDecimal.ZERO);
					curSchd.setRepayAmount(pftToSchd);
				} else {

					if (pftToSchd.compareTo(curSchd.getRepayAmount()) < 0) {
						curSchd.setProfitSchd(pftToSchd);
					} else {
						curSchd.setProfitSchd(curSchd.getRepayAmount());
					}

					curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
				}
			} else {
				curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
			}

			// PRINCIPAL ONLY: Applicable for REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_PRI.equals(curSchd.getSchdMethod())) {
			curSchd.setProfitSchd(BigDecimal.ZERO);
			// DEVELOPER FINANCE
			if (finType.isDeveloperFinance()) {
				if ((finMain.isScheduleMaintained() || StringUtils.isNotBlank(finMain.getReceiptPurpose()))
						&& curSchd.getSchDate().compareTo(finMain.getRecalFromDate()) >= 0) {
					BigDecimal newPrincipal = prvSchd.getClosingBalance().subtract(curSchd.getOrgEndBal());
					if (newPrincipal.compareTo(BigDecimal.ZERO) < 0) {
						newPrincipal = BigDecimal.ZERO;
					}

					curSchd.setPrincipalSchd(newPrincipal);
				}
			}
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			// CALCULATED PROFIT ONLY: Applicable for GRACE & REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_PFT.equals(curSchd.getSchdMethod())) {
			// IF Scheduled Profit cannot change (Effective Rate Calculation)
			// Then leave actual scheduled else calculate

			// pradeep changes merged from bajaj trunk
			if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) < 0) {
				curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			}

			if (!protectPftSchd) {
				schdInterest = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finMain, false);

				// FIXME: PV 02JUN18 WHY BELOW CODE IS REQUIRED?. Commented for
				// testing
				/*
				 * if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) { if
				 * (!finMain.isAllowGrcCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } } else { if
				 * (!finMain.isAllowRepayCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } }
				 */

				if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
						&& finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
					schdInterest = BigDecimal.ZERO;
				}

				curSchd.setProfitSchd(schdInterest);
				// DEVELOPER FINANCE
				if (finType.isDeveloperFinance()) {
					if ((finMain.isScheduleMaintained() || StringUtils.isNotBlank(finMain.getReceiptPurpose()))
							&& curSchd.getSchDate().compareTo(finMain.getRecalFromDate()) >= 0) {
						BigDecimal newPrincipal = prvSchd.getClosingBalance().subtract(curSchd.getOrgEndBal());
						if (newPrincipal.compareTo(BigDecimal.ZERO) < 0) {
							newPrincipal = BigDecimal.ZERO;
						}

						curSchd.setPrincipalSchd(newPrincipal);
					}
				} else {
					curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
				}
			}
			// curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			// PRINCIPAL + CALCULATED PROFIT: Applicable for GRACE & REPAYMENT
			// period
		} else if (CalculationConstants.SCHMTHD_PFTCPZ.equals(curSchd.getSchdMethod())) {

			BigDecimal cpzDue = BigDecimal.ZERO;
			if (curSchd.isRepayOnSchDate() && curSchd.isCpzOnSchDate()) {
				if (!finMain.isCpzPosIntact()) {
					cpzDue = finMain.getPftCpzFromReset();
					finMain.setPftCpzFromReset(BigDecimal.ZERO);
				}
			}

			if (curSchd.getPresentmentId() <= 0) {
				curSchd.setPrincipalSchd(cpzDue);
			}

			if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) < 0) {
				curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			}

			if (!protectPftSchd) {
				schdInterest = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finMain, false);

				// FIXME: PV 02JUN18 WHY BELOW CODE IS REQUIRED?. Commented for
				// testing
				/*
				 * if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) { if
				 * (!finMain.isAllowGrcCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } } else { if
				 * (!finMain.isAllowRepayCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } }
				 */

				if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
						&& finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
					schdInterest = BigDecimal.ZERO;
				}

				curSchd.setProfitSchd(schdInterest);
				// DEVELOPER FINANCE
				if (finType.isDeveloperFinance()) {
					if ((finMain.isScheduleMaintained() || StringUtils.isNotBlank(finMain.getReceiptPurpose()))
							&& curSchd.getSchDate().compareTo(finMain.getRecalFromDate()) >= 0) {
						BigDecimal newPrincipal = prvSchd.getClosingBalance().subtract(curSchd.getOrgEndBal());
						if (newPrincipal.compareTo(BigDecimal.ZERO) < 0) {
							newPrincipal = BigDecimal.ZERO;
						}

						curSchd.setPrincipalSchd(newPrincipal);
					}
				}
			}

			// curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			// PRINCIPAL + CALCULATED PROFIT: Applicable for GRACE & REPAYMENT
			// period
		} else if (CalculationConstants.SCHMTHD_PRI_PFT.equals(curSchd.getSchdMethod())) {
			// IF Scheduled Profit cannot change (Effective Rate Calculation)
			// Then leave actual scheduled else calculate

			// FIXME: PV. To be tested various scenarios. Once successful
			// deleted related code
			if (curSchd.getPresentmentId() > 0) {

				if (!protectPftSchd) {
					curSchd.setProfitSchd(
							prvSchd.getProfitBalance().add(curSchd.getProfitCalc()).subtract(prvSchd.getCpzAmount()));
				}

				if (StringUtils.isNotBlank(finMain.getReceiptPurpose())
						&& (StringUtils.equals(finMain.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY)
								|| StringUtils.equals(finMain.getReceiptPurpose(),
										FinanceConstants.FINSER_EVENT_EARLYSETTLE))) {

					if (curSchd.getSchDate().compareTo(finMain.getAppDate()) <= 0) {
						curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
					} else {
						curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
					}
				} else {
					curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
				}

			} else if (!protectPftSchd) {
				schdInterest = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finMain, false);

				// FIXME: PV 02JUN18 WHY BELOW CODE IS REQUIRED?. Commented for
				// testing
				/*
				 * if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) { if
				 * (!finMain.isAllowGrcCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } } else { if
				 * (!finMain.isAllowRepayCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } }
				 */
				// BPI changes
				if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
						&& finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
					schdInterest = BigDecimal.ZERO;
				}

				curSchd.setProfitSchd(schdInterest);
				// DEVELOPER FINANCE
				if (finType.isDeveloperFinance()) {
					if ((finMain.isScheduleMaintained() || StringUtils.isNotBlank(finMain.getReceiptPurpose()))
							&& curSchd.getSchDate().compareTo(finMain.getRecalFromDate()) >= 0) {

						BigDecimal newPrincipal = prvSchd.getClosingBalance().subtract(curSchd.getOrgEndBal());
						if (newPrincipal.compareTo(BigDecimal.ZERO) < 0) {
							newPrincipal = BigDecimal.ZERO;
						}
						curSchd.setPrincipalSchd(newPrincipal);
					}
				}
				curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			} else {
				// DEVELOPER FINANCE
				if (finType.isDeveloperFinance()) {
					if ((finMain.isScheduleMaintained() || StringUtils.isNotBlank(finMain.getReceiptPurpose()))
							&& curSchd.getSchDate().compareTo(finMain.getRecalFromDate()) >= 0) {

						BigDecimal newPrincipal = prvSchd.getClosingBalance().subtract(curSchd.getOrgEndBal());
						if (newPrincipal.compareTo(BigDecimal.ZERO) < 0) {
							newPrincipal = BigDecimal.ZERO;
						}
						curSchd.setPrincipalSchd(newPrincipal);
					}
				}
				curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			}

			// NOPAYMENT IN GRACE SCHEDULES AND COMPLETE PAYMENT AT GRACE END
			// DATE: Applicable for GRACE period Only
		} else if (CalculationConstants.SCHMTHD_GRCENDPAY.equals(curSchd.getSchdMethod())) {

			if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
					&& (finMain.getBpiTreatment().equals(FinanceConstants.BPI_DISBURSMENT)
							|| finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHEDULE)
							|| finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI))) {
				schdInterest = curSchd.getProfitCalc();

				// FIXME: PV 02JUN18 WHY BELOW CODE IS REQUIRED?. Commented for
				// testing
				/*
				 * if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) { if
				 * (!finMain.isAllowGrcCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } } else { if
				 * (!finMain.isAllowRepayCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } }
				 */
				// BPI changes
				if (finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {

					if (curSchd.getInstNumber() == 1) {
						curSchd.setProfitSchd(schdInterest);
					} else {
						curSchd.setProfitSchd(BigDecimal.ZERO);
					}
				} else {
					curSchd.setProfitSchd(schdInterest);
				}
			} else {
				curSchd.setProfitSchd(BigDecimal.ZERO);

			}

			curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());

			if (curSchd.getSchDate().compareTo(finMain.getGrcPeriodEndDate()) == 0) {
				curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finMain, false));
			}
		} else if (CalculationConstants.SCHMTHD_PFTCAP.equals(curSchd.getSchdMethod())) {
			if (!protectPftSchd) {
				schdInterest = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finMain, false);

				if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
						&& finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
					schdInterest = BigDecimal.ZERO;
				}

				if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_RESTRUCTURE)) {
					curSchd.setPrincipalSchd(BigDecimal.ZERO);
				}

				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0
						|| schdInterest.compareTo(curSchd.getRepayAmount()) < 0) {
					curSchd.setProfitSchd(schdInterest);
				} else {
					curSchd.setProfitSchd(curSchd.getRepayAmount());
				}
			}
		}

		curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

		// store first repay amount
		if (finMain.getCalTerms() == 1) {
			finMain.setFirstRepay(curSchd.getRepayAmount());
		}

		// store last repay amount
		finMain.setLastRepay(curSchd.getRepayAmount());

		return curSchd;

	}

	private BigDecimal getCpzFromLastRepayFrq(FinScheduleData finScheduleData, int iCur, int iPrv, Date evtFromDate) {
		BigDecimal cpzAmount = BigDecimal.ZERO;
		String frq = finScheduleData.getFinanceMain().getRepayFrq();
		return cpzAmount;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : getProfitBalance Description : Get profit balance unscheduled till schedule date Process :
	 * ********************************************** ******************************************************************
	 */

	private BigDecimal getProfitBalance(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd,
			boolean cpzPOSIntact) {

		BigDecimal profitBalance = BigDecimal.ZERO;

		profitBalance = prvSchd.getProfitBalance().add(curSchd.getProfitCalc()).subtract(curSchd.getProfitSchd())
				.subtract(prvSchd.getCpzAmount());

		if (cpzPOSIntact) {
			profitBalance = profitBalance.add(prvSchd.getCpzBalance());
		}

		return profitBalance;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : getClosingBalance Description : Schedule record Closing balance Process :
	 * ***************************************************************** ***********************************************
	 */
	private BigDecimal getClosingBalance(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd,
			String repayRateBasis, boolean cpzPOSIntact) {

		BigDecimal closingBal = prvSchd.getClosingBalance().add(curSchd.getDisbAmount()).add(curSchd.getFeeChargeAmt())
				.subtract(curSchd.getDownPaymentAmount());

		if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_D)) {
			closingBal = closingBal.subtract(curSchd.getRepayAmount());
		} else {
			closingBal = closingBal.subtract(curSchd.getPrincipalSchd());
		}

		if (!cpzPOSIntact || StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_MORTEMIHOLIDAY)) {
			closingBal = closingBal.add(curSchd.getCpzAmount());
		}
		return closingBal;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : getProfitSchd Description : Get Profit to be scheduled Process : *
	 * ************************************************************************* **************************************
	 */
	private BigDecimal calProfitToSchd(FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd,
			boolean cpzPOSIntact, FinanceMain finMain, boolean isMDTDate) {
		BigDecimal newProfit = BigDecimal.ZERO;

		boolean protectPftSchd = finMain.isProtectSchdPft();
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_RATECHG, finMain.getProcMethod())) {
			if (StringUtils.equals(CalculationConstants.RATEREVIEW_RVWFUR, finMain.getRvwRateApplFor())) {
				if (DateUtility.compare(curSchd.getSchDate(), finMain.getAppDate()) <= 0) {
					protectPftSchd = true;
				}

				// On maturity Date case, default total Will adjust
				if (DateUtility.compare(curSchd.getSchDate(), finMain.getMaturityDate()) == 0) {
					protectPftSchd = false;
				}
			}
		}
		// If profit already paid do not touch the schedule profit.
		if (curSchd.isSchPftPaid()) {
			newProfit = curSchd.getSchdPftPaid();
			if (prvSchd.getClosingBalance().compareTo(curSchd.getPrincipalSchd()) <= 0 || isMDTDate) {
				newProfit = prvSchd.getProfitBalance().add(curSchd.getProfitCalc());
				newProfit = newProfit.subtract(prvSchd.getCpzAmount());

				if (prvSchd.isCpzOnSchDate()) {
					newProfit = newProfit.add(prvSchd.getCpzBalance());
				}
			}
		} else if (curSchd.getPresentmentId() > 0) {

			if (protectPftSchd) {
				newProfit = curSchd.getProfitSchd();
			} else {
				newProfit = prvSchd.getProfitBalance().add(curSchd.getProfitCalc());
				newProfit = newProfit.subtract(prvSchd.getCpzAmount());
				if (prvSchd.isCpzOnSchDate()) {
					newProfit = newProfit.add(prvSchd.getCpzBalance());
				}

				if (curSchd.getProfitSchd().compareTo(newProfit) > 0) {
					curSchd.setPrincipalSchd(
							curSchd.getPrincipalSchd().add(curSchd.getProfitSchd().subtract(newProfit)));
				} else {
					newProfit = curSchd.getProfitSchd();
				}
			}
		} else {
			if (protectPftSchd) {
				newProfit = curSchd.getProfitSchd();
			} else {
				newProfit = prvSchd.getProfitBalance().add(curSchd.getProfitCalc());
				newProfit = newProfit.subtract(prvSchd.getCpzAmount());

				if (prvSchd.isCpzOnSchDate()) {
					newProfit = newProfit.add(prvSchd.getCpzBalance());
				}
			}
		}

		return newProfit;
	}

	/*
	 * ************************************************************************* ***************************************
	 * Method : round Description : To round the BigDecimal value to the basic rounding mode Process :
	 * *************************************************** *************************************************************
	 */

	private BigDecimal round(BigDecimal value) {
		return value.setScale(0, RoundingMode.HALF_DOWN);
	}

	private BigDecimal roundCeiling(BigDecimal value) {
		return value.setScale(0, RoundingMode.CEILING);
	}

	/*
	 * ========================================================================= =
	 * ====================================== Method : procSubSchedule Description : SUB SCHEDULE Process : Add Term
	 * will add another term to the schedule details. ====================================================
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

		Date newSchdDate = DateUtility.getDate(DateUtility.format(subSchStartDate, PennantConstants.dateFormat));

		for (int i = 0; i < noOfTerms; i++) {
			finScheduleData = addSubScheduleTerm(finScheduleData, lastDateLimit, true, newSchdDate, frqNewSchd);

			if (finScheduleData.getErrorDetails().size() > 0) {
				return orgFinScheduleData;
			}

			newSchdDate = DateUtility.getDate(DateUtility
					.format(FrequencyUtil.getNextDate(frqNewSchd, 1, newSchdDate, HolidayHandlerTypes.MOVE_NONE, false)
							.getNextFrequencyDate(), PennantConstants.dateFormat));
		}

		// Except first time creation of schedule covert flat rate to reducing
		// will be treated as reducing only
		finScheduleData = calSchdProcess(finScheduleData, false, false);
		logger.debug("Leaving");
		return finScheduleData;

	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : addSubScheduleTerm Description : ADD SubSchedule Term Process : Add SubSchedule Term will add another
	 * term to the schedule details.
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

		if (DateUtil.compare(newSchdDate, lastDateLimit) > 0) {
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
		FinanceMain finMain = finScheduleData.getFinanceMain();
		BigDecimal number2 = new BigDecimal(2);
		boolean isExactMatch = false;

		if (forInfoOnly) {
			BigDecimal effRateofReturn = BigDecimal.ZERO;
			BigDecimal returnCalProfit = BigDecimal.ZERO;

			effRateofReturn = finScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate();
			finScheduleData = procChangeRate(finScheduleData, null, null, BigDecimal.ZERO, effRateofReturn, false,
					false);

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

			finScheduleData = procChangeRate(finScheduleData, null, null, BigDecimal.ZERO, effRateofReturn, false,
					false);

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
			finScheduleData = procChangeRate(finScheduleData, null, null, BigDecimal.ZERO, effRateofReturn, false,
					false);
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

		finScheduleData = procChangeRate(finScheduleData, null, null, BigDecimal.ZERO, effRateofReturn, false, false);

		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);
		finScheduleData = setFinanceTotals(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Setting Supplementary Rent & increased Cost Charges for Ijarah product
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
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> >>
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SORTING METHODS >>>>>>>>>>>>>>>>>>>>>
	 * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : sortSchdDetails Description: Sort Schedule Details
	 * ________________________________________________________________________________________________________________
	 */
	public static List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

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
	 * Method : sortOverdraftSchedules Description: Sort Overdraft Schedule Details
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
	 * Method : sortLowerTaxDeduction Description: Sort Lower Tax Details
	 * ________________________________________________________________________________________________________________
	 */
	private List<LowerTaxDeduction> sortLowerTaxDeduction(List<LowerTaxDeduction> taxDeductions) {

		if (taxDeductions != null && taxDeductions.size() > 0) {

			Collections.sort(taxDeductions, new Comparator<LowerTaxDeduction>() {
				@Override
				public int compare(LowerTaxDeduction odSchd1, LowerTaxDeduction odSchd2) {
					return DateUtility.compare(odSchd1.getStartDate(), odSchd2.getStartDate());
				}
			});
		}

		return taxDeductions;
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
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_15DAYS)) {
			periods = new BigDecimal(24);
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_BIMONTHLY)) {
			periods = new BigDecimal(6);
		} else if (calFrq.equals(FrequencyCodeTypes.FRQ_WEEKLY)) {
			periods = new BigDecimal(52);
		} else {
			periods = new BigDecimal(365);
		}

		// Interest Rate Per Period
		// PMT calculation Changes 19-06-2019
		if (finMain.isEqualRepay()
				&& (idb.equals(CalculationConstants.IDB_ACT_ISDA) || idb.equals(CalculationConstants.IDB_ACT_365FIXED)
						|| idb.equals(CalculationConstants.IDB_ACT_365LEAPS)
						|| idb.equals(CalculationConstants.IDB_ACT_365LEAP))) {
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
		FinanceMain fm = finScheduleData.getFinanceMain();

		if (FinanceConstants.BPI_NO.equals(fm.getBpiTreatment())) {
			logger.info("NO BPI Treatment.");
			return finScheduleData;
		}

		FinanceScheduleDetail schd = finScheduleData.getFinanceScheduleDetails().get(1);
		Date firstSchdDate = schd.getSchDate();
		String frqBPI = "";

		Date nextInstDate = null;
		if (fm.isAllowGrcPeriod()) {
			frqBPI = fm.getGrcPftFrq();
			nextInstDate = fm.getNextGrcPftDate();
		} else {
			frqBPI = fm.getRepayPftFrq();
			nextInstDate = fm.getNextRepayPftDate();
		}

		String bpiCalOn = SysParamUtil.getValueAsString("BPI_CALC_ON");
		Date bpiDate = null;
		if (StringUtils.equals(bpiCalOn, FinanceConstants.BPI_CAL_ON_LASTFRQDATE)) {
			int terms = FrequencyUtil.getTerms(frqBPI, fm.getFinStartDate(), nextInstDate, false, true).getTerms();
			List<Calendar> termSchList = FrequencyUtil
					.getNextDate(frqBPI, terms - 1, fm.getFinStartDate(), HolidayHandlerTypes.MOVE_NONE, false)
					.getScheduleList();
			bpiDate = DateUtility.getDate(
					DateUtility.format(termSchList.get(termSchList.size() - 1).getTime(), PennantConstants.dateFormat));
		} else {
			bpiDate = DateUtility
					.getDate(
							DateUtility.format(
									FrequencyUtil.getNextDate(frqBPI, 1, fm.getFinStartDate(),
											HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
									PennantConstants.dateFormat));
		}

		if (DateUtility.compare(bpiDate, firstSchdDate) >= 0) {
			schd.setBpiOrHoliday(FinanceConstants.FLAG_BPI);
			if (StringUtils.isNotEmpty(fm.getBpiPftDaysBasis())) {
				schd.setPftDaysBasis(fm.getBpiPftDaysBasis());
			}

			if (StringUtils.equals(fm.getBpiTreatment(), FinanceConstants.BPI_CAPITALIZE)) {
				schd.setPftOnSchDate(false);
				schd.setCpzOnSchDate(true);
			} else {
				schd.setPftOnSchDate(true);
				schd.setCpzOnSchDate(false);
				schd.setTDSApplicable(TDSCalculator.isTDSApplicable(fm));
			}

			if (DateUtility.compare(bpiDate, fm.getGrcPeriodEndDate()) > 0) {
				schd.setSchdMethod(fm.getScheduleMethod());
			} else {
				schd.setSchdMethod(fm.getGrcSchdMthd());
			}
			logger.debug("Leaving");
			return finScheduleData;
		}

		// insert new Schedule Dated term
		FinanceScheduleDetail openSchd = finScheduleData.getFinanceScheduleDetails().get(0);
		FinanceScheduleDetail sd = new FinanceScheduleDetail();

		sd.setFinReference(fm.getFinReference());
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
		sd.setCpzBalance(BigDecimal.ZERO);
		sd.setClosingBalance(BigDecimal.ZERO);
		sd.setProfitFraction(BigDecimal.ZERO);
		sd.setBaseRate(openSchd.getBaseRate());
		sd.setSplRate(openSchd.getSplRate());
		sd.setMrgRate(openSchd.getMrgRate());
		sd.setActRate(openSchd.getActRate());
		sd.setCalculatedRate(openSchd.getCalculatedRate());
		if (StringUtils.isNotEmpty(fm.getBpiPftDaysBasis())) {
			sd.setPftDaysBasis(fm.getBpiPftDaysBasis());
		} else {
			sd.setPftDaysBasis(openSchd.getPftDaysBasis());
		}
		sd.setAdvBaseRate(openSchd.getAdvBaseRate());
		sd.setAdvMargin(openSchd.getAdvMargin());
		sd.setAdvPftRate(openSchd.getAdvPftRate());
		sd.setSuplRent(openSchd.getSuplRent());
		sd.setIncrCost(openSchd.getIncrCost());
		sd.setTDSApplicable(TDSCalculator.isTDSApplicable(fm));

		if (DateUtility.compare(bpiDate, fm.getGrcPeriodEndDate()) > 0) {
			sd.setSchdMethod(fm.getScheduleMethod());
		} else {
			sd.setSchdMethod(fm.getGrcSchdMthd());
		}

		if (StringUtils.equals(fm.getBpiTreatment(), FinanceConstants.BPI_CAPITALIZE)) {
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
			fm.setNumberOfTerms(fm.getNumberOfTerms() + 1);
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

			if (curSchd.isPftOnSchDate() && curSchd.isFrqDate()) {
				break;
			}

			if (curSchd.isRepayOnSchDate() && curSchd.isFrqDate()) {
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

	public FinScheduleData procMDTRecord(FinScheduleData finScheduleData, int i, boolean isRepayComplete,
			boolean cpzPOSIntact) {
		logger.debug("Entering");

		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(i - 1);

		String repayRateBasis = finScheduleData.getFinanceMain().getRepayRateBasis();

		curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, finScheduleData.getFinanceMain(), true));

		/*
		 * PSD#171829 START Loan have capitalized schedule while perform early settlement on exact schedule date loan
		 * status showing as matured but in the loan basic details screen getting OS amount that is equal to due date
		 * capitalized amount
		 */
		curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, cpzPOSIntact));
		setCpzAmounts(prvSchd, curSchd, cpzPOSIntact, true);

		/* PSD#171829 END */

		if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_D)) {
			curSchd.setRepayAmount(prvSchd.getClosingBalance());
			curSchd.setPrincipalSchd(curSchd.getClosingBalance().subtract(curSchd.getProfitSchd()));
			BigDecimal endBal = getClosingBalance(curSchd, prvSchd, repayRateBasis, cpzPOSIntact)
					.subtract(curSchd.getRefundOrWaiver());

			if (endBal.compareTo(BigDecimal.ZERO) < 0) {
				curSchd.setClosingBalance(BigDecimal.ZERO);
			} else {
				curSchd.setClosingBalance(endBal);
			}

		} else {
			curSchd.setPrincipalSchd(prvSchd.getClosingBalance());
			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			BigDecimal endBal = getClosingBalance(curSchd, prvSchd, repayRateBasis, cpzPOSIntact);
			curSchd.setClosingBalance(endBal);
		}

		curSchd.setSchdMethod(prvSchd.getSchdMethod());
		if (!isRepayComplete || finScheduleData.getFinanceMain().isSanBsdSchdle()) {
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

			curSchd.setProfitCalc(curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance()));
		}

		logger.debug("Leaving");
		return finScheduleData;

	}

	public FinScheduleData prepareFirstSchdCal(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		int indexRepay = finScheduleData.getFinanceMain().getSchdIndex();
		FinanceScheduleDetail grcEndSchd = finScheduleData.getFinanceScheduleDetails().get(indexRepay);
		FinanceScheduleDetail rpySchd = finScheduleData.getFinanceScheduleDetails().get(indexRepay);
		FinanceType ft = finScheduleData.getFinanceType();

		FinanceMain finMain = finScheduleData.getFinanceMain();
		BigDecimal instAmt = new BigDecimal(0);
		int terms = finMain.getNumberOfTerms();
		BigDecimal presentValue = grcEndSchd.getClosingBalance();
		String schdMethod = finMain.getScheduleMethod();

		finMain.setAdjTerms(terms);

		if (finMain.isStepFinance() && finMain.isRpyStps()) {
			if (StringUtils.equals(finMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_PERC)) {
				finScheduleData = calStepSchd(finScheduleData);
			}
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

				if (finMain.isSanBsdSchdle()) {
					instAmt = finMain.getFinAssetValue().divide(BigDecimal.valueOf(terms), 0, RoundingMode.HALF_DOWN);
					finMain.setEqualRepay(false);
					finMain.setCalculateRepay(false);
				} else if (finMain.getReqRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
					instAmt = finMain.getReqRepayAmount();
				} else {
					instAmt = presentValue.divide(BigDecimal.valueOf(terms), 0, RoundingMode.HALF_DOWN);
				}
				instAmt = CalculationUtil.roundAmount(instAmt, finMain.getCalRoundingMode(),
						finMain.getRoundingTarget());
			}

			Date startFrom = finMain.getNextRepayPftDate();
			if (StringUtils.equals(finMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
					&& DateUtil.compare(finMain.getNextRepayDate(), startFrom) < 0) {
				startFrom = finMain.getNextRepayDate();
			}

			finScheduleData = setRpyInstructDetails(finScheduleData, startFrom, finMain.getMaturityDate(), instAmt,
					schdMethod);
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

		if (FinanceConstants.STEPTYPE_PRIBAL.equals(finScheduleData.getFinanceMain().getStepType())) {
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
		// finScheduleData = graceSchdCal(finScheduleData);
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
			// finScheduleData = graceSchdCal(finScheduleData);
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

		boolean isAdjustClosingBal = finMain.isAdjustClosingBal();

		int sdSize = finSchdDetails.size();

		if (!isAdjustClosingBal && AdvanceType.hasAdvEMI(finMain.getAdvType())
				&& AdvanceStage.hasFrontEnd(finMain.getAdvStage())) {
			sdSize = finSchdDetails.size() - finMain.getAdvTerms();
		}

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

		// TODO: PV: 25 MAY 19 CHANGED THE CONDITION finMain.getRecalFromDate())
		// = 0 TO finMain.getRecalFromDate()) >= 0
		// IF RECAL SPREADS IN TO GRACE AND REPAY, DATE WILL NOT BE FOUND WITH
		// EQUAL CONDITION
		// PENDING THOUROUGH TESTING
		// Get Repayment instruction index
		for (int i = 0; i < riSize; i++) {
			if (DateUtility.compare(repayInstructions.get(i).getRepayDate(), finMain.getRecalFromDate()) >= 0) {
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
			int size = finSchdDetails.size() - 1;
			if (AdvanceType.hasAdvEMI(finMain.getAdvType()) && AdvanceStage.hasFrontEnd(finMain.getAdvStage())) {
				size = size - finMain.getAdvTerms();
			}
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
			finMain.setAdjustClosingBal(isAdjustClosingBal);
			finScheduleData = getRpyInstructDetails(finScheduleData);
			finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, false);

			// Find COMPARISION Amount
			if (!isCompareMDTRecord) {
				comparisionAmount = repayInstructions.get(iRpyInst).getRepayAmount();
			}

			// Find COMPARISION TO Amount
			if (isComapareWithEMI) {
				comparisionToAmount = finSchdDetails.get(size).getRepayAmount();
			} else {
				comparisionToAmount = finSchdDetails.get(size).getPrincipalSchd();
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
			finMain.setAdjustClosingBal(isAdjustClosingBal);
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
		finMain.setAdjustClosingBal(isAdjustClosingBal);
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
		if (stepDetail.getEmiSplitPerc().compareTo(BigDecimal.ZERO) > 0) {
			iCompareEMI = iCompareEMI.multiply(bd100).divide(stepDetail.getEmiSplitPerc(), 0, RoundingMode.HALF_DOWN);
		} else {
			iCompareEMI = stepDetail.getSteppedEMI();
		}

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
			if (stepDetail.getEmiSplitPerc().compareTo(BigDecimal.ZERO) > 0) {
				iCompareEMI = iCompareEMI.multiply(bd100).divide(stepDetail.getEmiSplitPerc(), 0,
						RoundingMode.HALF_DOWN);
			} else {
				iCompareEMI = stepDetail.getSteppedEMI();
			}

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

				curSchd.getInsSchd().add(curSchd.getInsSchd().add(insAmount));

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
							curSchd.getBalanceForPftCal(), curSchd.getPftDaysBasis(), prvSchd.getAdvCalRate());

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

				} else if (curSchd.getSchdMethod().equals(CalculationConstants.SCHMTHD_PFT)
						|| curSchd.getSchdMethod().equals(CalculationConstants.SCHMTHD_PFTCPZ)) {
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

	/**
	 * Method for Fetching Recal From Date
	 * 
	 * @param finScheduleData
	 * @param eventFromDate
	 * @return
	 */
	private FinScheduleData resetRecalData(FinScheduleData finScheduleData, Date eventFromDate, BigDecimal amount,
			String receiptPurpose) {

		// Resetting Next Recal From Date if not exists
		Date recalFromDate = eventFromDate;

		// TODO : If Early settle on or before Grace end , need to re-modify
		FinanceMain fm = finScheduleData.getFinanceMain();
		Date graceEndDate = fm.getGrcPeriodEndDate();
		FinanceScheduleDetail openSchd = null;
		int prvIndex = -1;

		// Resetting Recal Schedule Method & Next Recal From Date if not exists
		String recalSchdMethod = null;
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
		for (int i = 0; i < schedules.size(); i++) {
			FinanceScheduleDetail curSchd = schedules.get(i);

			if (DateUtility.compare(curSchd.getSchDate(), eventFromDate) == 0) {
				fm.setIndexMisc(i);
				openSchd = curSchd;

				if (curSchd.isRepayOnSchDate()) {
					recalSchdMethod = curSchd.getSchdMethod();
					if (StringUtils.equals(recalSchdMethod, CalculationConstants.SCHMTHD_PFT)
							|| StringUtils.equals(recalSchdMethod, CalculationConstants.SCHMTHD_PFTCPZ)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					}
					if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
						if (StringUtils.equals(recalSchdMethod, CalculationConstants.SCHMTHD_PRI)) {
							recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
						}
					}
				} else if (curSchd.isPftOnSchDate()) {
					recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
				} else {
					recalSchdMethod = CalculationConstants.SCHMTHD_PRI;
					if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					}
				}

				String bpiOrHoliday = curSchd.getBpiOrHoliday();
				if (FinanceConstants.FLAG_HOLIDAY.equals(bpiOrHoliday)
						|| FinanceConstants.FLAG_POSTPONE.equals(bpiOrHoliday)
						|| FinanceConstants.FLAG_STRTPRDHLD.equals(bpiOrHoliday)
						|| FinanceConstants.FLAG_MORTEMIHOLIDAY.equals(bpiOrHoliday)
						|| FinanceConstants.FLAG_UNPLANNED.equals(bpiOrHoliday)) {

					if (FinanceConstants.FINSER_EVENT_EARLYSETTLE.equals(receiptPurpose)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					} else {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI;
					}
				}

			} else if (DateUtility.compare(curSchd.getSchDate(), eventFromDate) > 0) {
				if (curSchd.getPresentmentId() != 0 || DateUtility.compare(curSchd.getSchDate(), graceEndDate) <= 0) {
					continue;
				}
				if (!curSchd.isRepayOnSchDate()) {
					continue;
				}
				recalFromDate = curSchd.getSchDate();
				break;
			} else {
				prvIndex = prvIndex + 1;
			}

		}

		// If schedule Not found
		if (openSchd == null) {
			finScheduleData = addSchdRcd(finScheduleData, eventFromDate, prvIndex, true);
			openSchd = schedules.get(prvIndex + 1);

			if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				recalSchdMethod = CalculationConstants.SCHMTHD_PRI;
			} else if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
				recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
			}

			if (DateUtility.compare(openSchd.getSchDate(), graceEndDate) < 0) {
				openSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
			} else {
				openSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
			}
		}

		if (StringUtils.equals(receiptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {
			openSchd.setPftOnSchDate(true);
		} else {
			openSchd.setPartialPaidAmt(openSchd.getPartialPaidAmt().add(amount));
		}
		openSchd.setRepayOnSchDate(true);
		if (TDSCalculator.isTDSApplicable(fm)) {
			openSchd.setTDSApplicable(schedules.get(prvIndex + 2).isTDSApplicable());
			Date maturityDate = fm.getMaturityDate();

			if (maturityDate.compareTo(SysParamUtil.getAppDate()) < 0) {
				openSchd.setTDSApplicable(schedules.get(prvIndex + 1).isTDSApplicable());
			} else {
				openSchd.setTDSApplicable(schedules.get(prvIndex + 2).isTDSApplicable());
			}
		}

		fm.setRecalSchdMethod(recalSchdMethod);
		fm.setRecalFromDate(recalFromDate);

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
		if (StringUtils.isEmpty(finMain.getRecalSchdMethod())) {
			finScheduleData = getSchdMethod(finScheduleData);
		}
		if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isSchdOnPMTCal()
				&& StringUtils.equals(finMain.getRecalSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)
				&& StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
			finMain.setEqualRepay(false);
		} else {
			finMain.setEqualRepay(true);
		}
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

		if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)) {
			resetRpyInstruction = false;
		}

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

	private FinScheduleData pmtCalc(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		BigDecimal rpyAmt = BigDecimal.ZERO;
		if (finScheduleData.getFinanceType() != null && !finScheduleData.getFinanceType().isSchdOnPMTCal()) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		FinanceMain finMain = finScheduleData.getFinanceMain();
		if (!StringUtils.equals(finMain.getRecalSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		if (!StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		Date recalFromDate = finMain.getRecalFromDate();
		Date recalToDate = finMain.getRecalToDate();
		String schdMethod = finMain.getScheduleMethod();
		int sdSize = finSchdDetails.size();

		finMain.setEqualRepay(false);
		int calTerms = 0;
		BigDecimal startBalance = null;
		BigDecimal endBalance = null;
		BigDecimal rate = null;

		for (int j = 0; j < sdSize; j++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(j);

			if (DateUtility.compare(curSchd.getSchDate(), recalFromDate) < 0) {
				startBalance = curSchd.getClosingBalance();
				continue;
			}
			if (DateUtility.compare(curSchd.getSchDate(), recalToDate) > 0) {
				break;
			}
			if (!curSchd.isRepayOnSchDate()) {
				continue;
			}
			if (DateUtility.compare(curSchd.getSchDate(), recalFromDate) == 0) {
				rate = curSchd.getCalculatedRate();
			}
			endBalance = curSchd.getClosingBalance();
			calTerms = calTerms + 1;
		}

		if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
			if (rate != null) {
				rpyAmt = approxPMT(finMain, rate, calTerms, startBalance, endBalance, 0);
			}
		}

		finScheduleData = setRpyInstructDetails(finScheduleData, recalFromDate, recalToDate, rpyAmt, schdMethod);
		finScheduleData = getRpyInstructDetails(finScheduleData);
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData setRepayForSanctionBasedDisbADJMDT(FinScheduleData finScheduleData,
			BigDecimal balDisbAmount) {

		FinanceMain finMain = finScheduleData.getFinanceMain();
		Date evtFromDate = finMain.getEventFromDate();

		List<FinanceScheduleDetail> fsdList = finScheduleData.getFinanceScheduleDetails();

		// BASED ON SANCTIONED AMOUNT, DEFINITION OF INSTALLMENT USING TERMS
		BigDecimal instAmt = finMain.getFinAssetValue().divide(BigDecimal.valueOf(finMain.getNumberOfTerms()), 0,
				RoundingMode.HALF_DOWN);

		finMain.setRecalFromDate(finMain.getMaturityDate());

		// Set Original Balances to Closing Balances
		for (int iFsd = 1; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail curSchd = fsdList.get(iFsd);
			FinanceScheduleDetail prvSchd = fsdList.get(iFsd - 1);

			if (curSchd.getSchDate().compareTo(evtFromDate) <= 0) {

				if (curSchd.getSchDate().compareTo(evtFromDate) == 0) {
					curSchd.setOrgEndBal(curSchd.getOrgEndBal().add(balDisbAmount));
				}
				continue;
			}

			if (curSchd.isFrqDate() && curSchd.isRepayOnSchDate()
					&& prvSchd.getOrgEndBal().compareTo(BigDecimal.ZERO) > 0) {
				curSchd.setOrgEndBal(prvSchd.getOrgEndBal().subtract(instAmt));
				if (curSchd.getOrgEndBal().compareTo(BigDecimal.ZERO) < 0) {
					curSchd.setOrgEndBal(BigDecimal.ZERO);
				}
			} else {
				curSchd.setOrgEndBal(prvSchd.getOrgEndBal());
			}
		}

		if (finMain.getRecalFromDate() == null) {
			finMain.setRecalFromDate(finMain.getMaturityDate());
		}
		finMain.setRecalToDate(finMain.getMaturityDate());
		finMain.setEventToDate(finMain.getRecalToDate());

		finMain.setEqualRepay(false);
		finMain.setCalculateRepay(false);

		finMain.setIndexMisc(finScheduleData.getRepayInstructions().size() - 1);
		finMain.setMiscAmount(instAmt);
		return finScheduleData;
	}

	private FinScheduleData setRepayForSanctionBasedPriHld(FinScheduleData finScheduleData, BigDecimal earlyPayAmt) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = finScheduleData.getFinanceScheduleDetails();

		Date maturityDate = fsdList.get(fsdList.size() - 1).getSchDate();
		Date evtFromDate = finMain.getEventFromDate();
		finMain.setEqualRepay(false);
		finMain.setCalculateRepay(false);

		finMain.setEventToDate(evtFromDate);
		finMain.setRecalFromDate(maturityDate);
		finMain.setRecalToDate(maturityDate);

		String recalType = finMain.getRecalType();
		finMain.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		finScheduleData = changeRepay(finScheduleData, earlyPayAmt, finMain.getRecalSchdMethod());

		finMain.setRecalType(recalType);
		BigDecimal prvRepayAmount = new BigDecimal(-1);

		boolean isRecalFromSet = false;
		finMain.setRecalFromDate(evtFromDate);
		finMain.setEventFromDate(maturityDate);
		finMain.setRecalToDate(maturityDate);

		// BASED ON SANCTIONED AMOUNT, DEFINITION OF INSTALLMENT USING TERMS
		BigDecimal instAmt = finMain.getFinAssetValue().divide(BigDecimal.valueOf(finMain.getNumberOfTerms()), 0,
				RoundingMode.HALF_DOWN);

		// Set Original Balances to Closing Balances
		for (int iFsd = 1; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail curSchd = fsdList.get(iFsd);
			FinanceScheduleDetail prvSchd = fsdList.get(iFsd - 1);

			if (curSchd.getSchDate().compareTo(evtFromDate) <= 0) {
				continue;
			}

			if (!isRecalFromSet) {
				if (curSchd.isRepayOnSchDate() || iFsd == (fsdList.size())) {
					finMain.setRecalFromDate(curSchd.getSchDate());
				}
			}

			if (!curSchd.isRepayOnSchDate()) {
				curSchd.setClosingBalance(prvSchd.getClosingBalance());
				continue;
			}

			if (curSchd.getOrgEndBal().compareTo(prvSchd.getClosingBalance()) <= 0) {
				curSchd.setClosingBalance(curSchd.getOrgEndBal());
			} else {
				curSchd.setClosingBalance(prvSchd.getClosingBalance());
			}

			curSchd.setPrincipalSchd(prvSchd.getClosingBalance().subtract(curSchd.getClosingBalance()));

			if (curSchd.getOrgEndBal().compareTo(BigDecimal.ZERO) == 0) {
				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
						&& prvSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0) {
					curSchd.setPrincipalSchd(instAmt);
				} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
						&& prvRepayAmount.compareTo(instAmt) <= 0) {

					if (prvRepayAmount.compareTo(BigDecimal.ZERO) <= 0) {
						curSchd.setPrincipalSchd(
								instAmt.add(prvSchd.getClosingBalance().subtract(prvSchd.getOrgEndBal())));
					} else {
						curSchd.setPrincipalSchd(instAmt);
					}
				}
			}

			if (curSchd.getPrincipalSchd().compareTo(prvRepayAmount) == 0) {
				continue;
			}

			finScheduleData = setRpyInstructDetails(finScheduleData, curSchd.getSchDate(), maturityDate,
					curSchd.getPrincipalSchd(), curSchd.getSchdMethod());
			prvRepayAmount = curSchd.getPrincipalSchd();
		}

		calSchdProcess(finScheduleData, false, false);
		return finScheduleData;
	}

	private FinScheduleData setRepayForSanctionBasedAddRecal(FinScheduleData finScheduleData,
			boolean isResetRecalFRomDate) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = finScheduleData.getFinanceScheduleDetails();
		int sdSize = fsdList.size();
		int adjTerms = finMain.getAdjTerms();

		if (StringUtils.equals(finMain.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			finScheduleData = procAddTerm(finScheduleData, adjTerms, false);

			if (finScheduleData.getErrorDetails().size() > 0) {
				return finScheduleData;
			}

			finMain = finScheduleData.getFinanceMain();
			fsdList = finScheduleData.getFinanceScheduleDetails();
			sdSize = fsdList.size();
			finMain.setRecalToDate(fsdList.get(sdSize - 1).getSchDate());
		}

		Date recalFromDate = finMain.getRecalFromDate();
		BigDecimal disbursedAmount = BigDecimal.ZERO;
		// BigDecimal recalPosBalance = BigDecimal.ZERO;

		if (isResetRecalFRomDate) {
			recalFromDate = finMain.getFinStartDate();
			for (int iFsd = 0; iFsd < sdSize; iFsd++) {
				FinanceScheduleDetail curSchd = fsdList.get(iFsd);
				if (curSchd.getSchDate().compareTo(finMain.getAppDate()) > 0) {
					break;
				}

				if (!curSchd.isRepayOnSchDate()) {
					continue;
				}

				recalFromDate = curSchd.getSchDate();
			}

			finMain.setRecalFromDate(recalFromDate);
			finMain.setEventToDate(finMain.getRecalToDate());
		}

		adjTerms = 0;

		for (int iFsd = 0; iFsd < sdSize; iFsd++) {
			FinanceScheduleDetail curSchd = fsdList.get(iFsd);

			if (curSchd.getSchDate().compareTo(finMain.getEventFromDate()) < 0) {
				// recalPosBalance = curSchd.getClosingBalance();
				disbursedAmount = disbursedAmount.add(curSchd.getDisbAmount());
				continue;
			}

			if (curSchd.getSchDate().compareTo(recalFromDate) < 0) {
				continue;
			}

			if (curSchd.isRepayOnSchDate()) {
				adjTerms = adjTerms + 1;
			}
		}

		if (isResetRecalFRomDate) {
			finMain.setEventFromDate(recalFromDate);
		}

		// THIS IS BASED ON ASSUMPTION. MAX DISBURSEMENT CHECK REQUIRED MUST BE
		// TRUE FOR LOAN TYPE
		BigDecimal unDisbursedAmount = finMain.getFinAssetValue().subtract(disbursedAmount);
		// recalPosBalance = unDisbursedAmount.add(recalPosBalance);

		BigDecimal instAmt = unDisbursedAmount.divide(BigDecimal.valueOf(adjTerms), 0, RoundingMode.HALF_DOWN);
		finMain.setEqualRepay(false);
		finMain.setCalculateRepay(false);
		finScheduleData = setRpyInstructDetails(finScheduleData, recalFromDate, finMain.getMaturityDate(), instAmt,
				CalculationConstants.SCHMTHD_PRI_PFT);
		finMain.setIndexMisc(finScheduleData.getRepayInstructions().size() - 1);
		finMain.setMiscAmount(instAmt);
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
			curODSchd.setDroplineDate(
					DateUtility.getDBDate(DateUtility.format(odSchdDateList.get(i), PennantConstants.DBDateFormat)));
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

			if (baseRatesHist.isEmpty()) {
				finScheduleData.setErrorDetail(new ErrorDetail("SCHRVW",
						"Interest Rate Codes not available for the Schedule date.", new String[] { " " }));
				return finScheduleData;
			}

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

			if (splRatesHist.isEmpty()) {
				finScheduleData.setErrorDetail(new ErrorDetail("SCHRVW",
						"Special Rate Codes not available for the Schedule date.", new String[] { " " }));
				return finScheduleData;
			}

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
				|| finMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PFTCPZ)
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
	@SuppressWarnings("unused")
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
				// To make sure the flags are TRUE when repayment happens
				curSchd.setPftOnSchDate(true);
				curSchd.setRepayOnSchDate(true);
				isRepaymentFoundInSD = true;
				prvSchdPrincipal = curSchd.getPrincipalSchd();
				break;
			}

			prvIndex = i;
		}

		if (!isRepaymentFoundInSD) {
			finScheduleData = addSchdRcd(finScheduleData, evtFromDate, prvIndex, true);
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

			// If not repayment schedule no change
			if (!curSchd.isRepayOnSchDate()) {
				continue;
			}

			// Already paid then no change
			if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
				continue;
			}

			// Already sent for presentment
			// SIVA : Schedule should be recalculated even Schedule Term is in
			// Presentment Process
			/*
			 * if (curSchd.getPresentmentId() != 0) { continue; }
			 */

			schdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
			if (remainingPrincipal.compareTo(curSchd.getPrincipalSchd()) >= 0) {

				Date fromDate = curSchd.getSchDate();
				if (zeroInstAdded) {
					fromDate = zeroCalStartDate;
				}
				finScheduleData = setRpyInstructDetails(finScheduleData, fromDate, curSchd.getSchDate(), newSchdPri,
						schdMethod);
				remainingPrincipal = remainingPrincipal.subtract(curSchd.getPrincipalSchd());
				if (!zeroInstAdded) {
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

	/**
	 * Ticket id:124631,TDS Round OFF </br>
	 * 
	 * changing TDSAmount based on TDS Available check in each Schedule. </br>
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData procChangeTDS(FinScheduleData finScheduleData) {
		FinanceMain finMain = finScheduleData.getFinanceMain();
		BigDecimal ltdLimitByRcd = BigDecimal.ZERO;
		FinanceScheduleDetail curSchd = null;
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		Date schdDate = new Date();
		boolean ltdApplicable = SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_LOWER_TAX_DED_REQ);

		List<LowerTaxDeduction> ltdList = sortLowerTaxDeduction(finScheduleData.getLowerTaxDeductionDetails());
		LowerTaxDeduction ltd = null;

		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);

		/*
		 * int recalIdx = finMain.getRecalIdx(); if (recalIdx < 0) { finScheduleData = setRecalIndex(finScheduleData);
		 * recalIdx = finMain.getRecalIdx(); }
		 */

		for (int i = 0; i < sdSize; i++) {

			curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			schdDate = curSchd.getSchDate();

			if (TDSCalculator.isTDSApplicable(finMain) && tdsPerc.compareTo(BigDecimal.ZERO) != 0) {

				/*
				 * if (i < recalIdx) { continue; }
				 */

				boolean taxOnSysPerc = true;
				if (ltdApplicable) {
					BigDecimal tdsAmount = BigDecimal.ZERO;

					if (ltd == null || DateUtility.compare(schdDate, ltd.getEndDate()) > 0) {
						ltd = fetchLTDRecord(ltdList, schdDate);
						ltdLimitByRcd = BigDecimal.ZERO;
					}

					if (ltd != null) {
						tdsAmount = (curSchd.getProfitSchd().multiply(ltd.getPercentage())).divide(new BigDecimal(100),
								0, RoundingMode.HALF_DOWN);
						tdsAmount = CalculationUtil.roundAmount(tdsAmount, tdsRoundMode, tdsRoundingTarget);
						if (ltd.getLimitAmt().compareTo(BigDecimal.ZERO) > 0
								&& ltd.getLimitAmt().compareTo(ltdLimitByRcd.add(tdsAmount)) >= 0) {
							taxOnSysPerc = false;
						} else if (ltd.getLimitAmt().compareTo(BigDecimal.ZERO) == 0) {
							taxOnSysPerc = false;
						}
						ltdLimitByRcd = ltdLimitByRcd.add(tdsAmount);
					}
					curSchd.setTDSAmount(tdsAmount);
				}

				if (taxOnSysPerc) {
					BigDecimal tdsAmount = (curSchd.getProfitSchd().multiply(tdsPerc)).divide(new BigDecimal(100), 0,
							RoundingMode.HALF_DOWN);
					tdsAmount = CalculationUtil.roundAmount(tdsAmount, tdsRoundMode, tdsRoundingTarget);
					curSchd.setTDSAmount(tdsAmount);
				}
			}
		}

		return finScheduleData;
	}

	// START : FLEXI Changes

	/**
	 * Build FLEXI Schedule from original normal schedule
	 * 
	 * ChgDropLineSchd is true when there is a change required in drop line schedule
	 * 
	 * @param finScheduleData
	 */
	private void rebuildHybridFlexiSchd(FinScheduleData finScheduleData) {
		logger.debug(" Entering ");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();

		BigDecimal calIntFraction = BigDecimal.ZERO;
		BigDecimal calInt = BigDecimal.valueOf(0.0);
		FinanceScheduleDetail prvSchd = null;

		// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);
		boolean cpzPOSIntact = finMain.isCpzPosIntact();

		for (FinanceScheduleDetail curSchd : schdDetails) {

			// reset disbursement details
			setDisbursementDetails(curSchd, finScheduleData.getDisbursementDetails());

			// calculation not required for first record - Finance Start Date
			// Record
			if (DateUtility.compare(curSchd.getSchDate(), finMain.getFinStartDate()) == 0) {

				if (finMain.isChgDropLineSchd()) {
					curSchd.setODLimit(curSchd.getClosingBalance());
				}
				curSchd.setClosingBalance(curSchd.getDisbAmount());
				curSchd.setAvailableLimit(curSchd.getODLimit().subtract(curSchd.getDisbAmount()));

				prvSchd = curSchd;
				continue;
			}

			// fields which are used for calculation
			Date curSchDate = curSchd.getSchDate();
			Date prvSchDate = prvSchd.getSchDate();

			BigDecimal dropLineLimit = curSchd.getODLimit();
			if (finMain.isChgDropLineSchd()) {

				dropLineLimit = curSchd.getClosingBalance();
				curSchd.setLimitDrop(curSchd.getPrincipalSchd());
				curSchd.setODLimit(dropLineLimit);

				curSchd.setSchdPriPaid(curSchd.getSchdPriPaid().add(curSchd.getPartialPaidAmt()));
			}

			// Profit Calculation
			curSchd.setBalanceForPftCal(prvSchd.getClosingBalance());

			if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {

				if (StringUtils.equals(roundAdjMth, "NO_ADJ") || (StringUtils.equals(roundAdjMth, "ADJ_LAST_INST")
						&& DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) != 0
						&& DateUtility.compare(curSchd.getSchDate(), finMain.getMaturityDate()) != 0)) {

					calIntFraction = BigDecimal.ZERO;
				}

				calInt = CalculationUtil.calInterest(prvSchDate, curSchDate, curSchd.getBalanceForPftCal(),
						curSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

				calInt = calInt.add(calIntFraction);

				BigDecimal calIntRounded = BigDecimal.ZERO;
				if (calInt.compareTo(BigDecimal.ZERO) > 0) {
					calIntRounded = CalculationUtil.roundAmount(calInt, finMain.getCalRoundingMode(),
							finMain.getRoundingTarget());
				}
				calIntFraction = calInt.subtract(calIntRounded);
				calInt = calIntRounded;

				if (StringUtils.equals(roundAdjMth, "ADJ_LAST_INST")
						&& DateUtility.compare(prvSchDate, finMain.getGrcPeriodEndDate()) != 0) {
					calIntFraction = calIntFraction.add(prvSchd.getProfitFraction());
				}

				curSchd.setRepayComplete(false);
				curSchd.setProfitFraction(calIntFraction);
			} else {
				calInt = BigDecimal.ZERO;
				curSchd.setRepayComplete(true);
			}
			curSchd.setProfitCalc(calInt);

			// As the Calculation factor is reset to 0 for grace schedule for
			// original Schedule
			if (DateUtility.compare(curSchDate, finMain.getGrcPeriodEndDate()) == 0) {
				calIntFraction = BigDecimal.ZERO;
				curSchd.setPftOnSchDate(true);
			}

			// 1. Principal Schedule : PrincipalSchd Calculation except for
			// Early Settlement
			if (!StringUtils.equals(finMain.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)) {

				BigDecimal calPri = BigDecimal.ZERO;
				if (dropLineLimit.compareTo(prvSchd.getClosingBalance()) <= 0) {
					calPri = prvSchd.getClosingBalance().subtract(dropLineLimit);
				}
				curSchd.setPrincipalSchd(calPri.add(curSchd.getPartialPaidAmt()));
			} else {
				if (DateUtility.compare(curSchDate, finMain.getEventFromDate()) > 0) {
					curSchd.setPrincipalSchd(BigDecimal.ZERO);
				}
			}

			// 2. Profit Schedule : ProfitSchd Calculation
			BigDecimal profitSchd = BigDecimal.ZERO;
			if (curSchd.isPftOnSchDate()) {

				profitSchd = curSchd.getProfitCalc().add(prvSchd.getProfitBalance()).subtract(prvSchd.getCpzAmount());
				if (curSchd.getPresentmentId() > 0) {

					BigDecimal pftToSchd = curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd());
					if (profitSchd.compareTo(pftToSchd) > 0) {
						profitSchd = pftToSchd;
					}

					// 2.1 Partial Paid Amount Calculation
					BigDecimal freezingAmt = pftToSchd.subtract(profitSchd);

					if (freezingAmt.compareTo(prvSchd.getClosingBalance()) <= 0) {

						// curSchd.setPartialPaidAmt(curSchd.getPartialPaidAmt().add(freezingAmt));
						curSchd.setPrincipalSchd(curSchd.getPrincipalSchd().add(freezingAmt));
					}
				}
			}
			curSchd.setProfitSchd(profitSchd);
			curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, cpzPOSIntact));

			// 3. Repay Amount : RepayAmount Calculation
			curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));

			// Resetting Capitalize flag and Capitalize OR not
			if (!curSchd.isCpzOnSchDate() && StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())) {
				if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
					curSchd.setCpzOnSchDate(finMain.isPlanEMICpz());
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
					curSchd.setCpzOnSchDate(finMain.isReAgeCpz());
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
					curSchd.setCpzOnSchDate(finMain.isUnPlanEMICpz());
				}
			}

			setCpzAmounts(prvSchd, curSchd, cpzPOSIntact, false);

			// ClosingBalance or Utilization
			curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, finMain.getRepayRateBasis(), cpzPOSIntact));
			curSchd.setAvailableLimit(dropLineLimit.subtract(curSchd.getClosingBalance()));

			if (curSchDate.compareTo(finMain.getMaturityDate()) == 0) {
				curSchd.setODLimit(BigDecimal.ZERO);
				curSchd.setAvailableLimit(BigDecimal.ZERO);
			}

			prvSchd = curSchd;
		}

		logger.debug(" Leaving ");
	}

	/**
	 * Set Disbursement Details
	 * 
	 * @param curSchd
	 * @param disbursementDetails
	 */
	private void setDisbursementDetails(FinanceScheduleDetail curSchd, List<FinanceDisbursement> disbursementDetails) {
		logger.debug(" Entering ");

		curSchd.setDisbAmount(BigDecimal.ZERO);
		for (FinanceDisbursement finDisb : disbursementDetails) {

			if (!FinanceConstants.DISB_STATUS_CANCEL.equals(finDisb.getDisbStatus())
					&& curSchd.getSchDate().compareTo(finDisb.getDisbDate()) == 0) {

				curSchd.setDisbAmount(curSchd.getDisbAmount().add(finDisb.getDisbAmount()));
				curSchd.setDisbOnSchDate(true);
			}
		}
		logger.debug(" Leaving ");
	}

	/**
	 * Re generate the original Schedule from FLEXI Schedule
	 * 
	 * @param finScheduleData
	 */
	private void rebuildOrgSchdFromFlexiSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();

		FinanceScheduleDetail schdDetail = null;
		for (int i = 0; i < schdDetails.size(); i++) {

			schdDetail = schdDetails.get(i);

			schdDetail.setRepayAmount(schdDetail.getRepayAmount().subtract(schdDetail.getPartialPaidAmt()));
			schdDetail.setSchdPriPaid(schdDetail.getSchdPriPaid().subtract(schdDetail.getPartialPaidAmt()));
			schdDetail.setPrincipalSchd(schdDetail.getLimitDrop());
			schdDetail.setClosingBalance(schdDetail.getODLimit());

			// BHFL : FLEXI Schedule plotted on DISBURSED AMOUNT not on
			// FINASSETVALUE
			/*
			 * schdDetail.setDisbAmount(BigDecimal.ZERO); if (i == 0) {
			 * schdDetail.setDisbAmount(finScheduleData.getFinanceMain(). getFinAmount()); }
			 */
		}

		logger.debug("leaving");
	}

	// END : FLEXI Changes

	/**
	 * Method for Calculate Subvention Details based on Disbursement Details
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData buildSubventionSchedule(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		// If Errors Exists in calculation, return back
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("leaving");
			return finScheduleData;
		}

		// Finance Disbursement List verification
		List<FinanceDisbursement> disbList = finScheduleData.getDisbursementDetails();
		if (disbList == null || disbList.isEmpty()) {
			logger.debug("leaving");
			return finScheduleData;
		}

		// If Subvention not applicable, no calculation required.
		FinanceMain finMain = finScheduleData.getFinanceMain();
		if (!finMain.isAllowSubvention()) {
			logger.debug("leaving");
			return finScheduleData;
		}

		// If Subvention Details not exists, no calculation
		SubventionDetail subvention = finScheduleData.getSubventionDetail();
		if (subvention == null) {
			logger.debug("leaving");
			return finScheduleData;
		}

		List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
		for (FinanceDisbursement curDisb : disbList) {

			// If Subvention Details already exists against Disbursement , no
			// action to be done.
			if (curDisb.getSubventionSchedules() != null && !curDisb.getSubventionSchedules().isEmpty()) {
				continue;
			}

			FinanceScheduleDetail curSchd = null;
			FinanceScheduleDetail prvSchd = null;
			List<SubventionScheduleDetail> svSchedules = new ArrayList<SubventionScheduleDetail>(1);
			SubventionScheduleDetail svSchd = null;
			BigDecimal prvClosingBal = BigDecimal.ZERO;
			BigDecimal subAmount = BigDecimal.ZERO;

			BigDecimal fvPftFraction = BigDecimal.ZERO;
			BigDecimal cbPftFraction = BigDecimal.ZERO;
			BigDecimal prvFvPftFraction = BigDecimal.ZERO;
			BigDecimal prvCbPftFraction = BigDecimal.ZERO;

			for (int i = 1; i < schdList.size(); i++) {

				curSchd = schdList.get(i);
				prvSchd = schdList.get(i - 1);

				// If Schedule date greater than Grace Period end date , not
				// consider.
				if (DateUtility.compare(curSchd.getSchDate(), curDisb.getDisbDate()) <= 0) {
					continue;
				}

				// If Schedule date greater than Grace Period end date , not
				// consider.
				if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) > 0) {
					break;
				}

				// If Schedule date greater than Subvention end date , not
				// consider.
				if (DateUtility.compare(curSchd.getSchDate(), subvention.getEndDate()) > 0) {
					break;
				}

				svSchd = new SubventionScheduleDetail();
				svSchd.setFinReference(finMain.getFinReference());
				svSchd.setDisbSeqID(curDisb.getDisbSeq());
				svSchd.setSchDate(curSchd.getSchDate());
				svSchd.setNoOfDays(CalculationUtil.calNoOfDays(prvSchd.getSchDate(), curSchd.getSchDate(),
						curSchd.getPftDaysBasis()));

				// Calculated Rate for the Schedule
				BigDecimal calRate = prvSchd.getActRate();
				if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL, subvention.getType())) {
					if (StringUtils.isNotBlank(prvSchd.getBaseRate())) {
						calRate = RateUtil.rates(prvSchd.getBaseRate(), finMain.getFinCcy(), prvSchd.getSplRate(),
								prvSchd.getMrgRate(), prvSchd.getSchDate(), finMain.getGrcMinRate(),
								finMain.getGrcMaxRate()).getNetRefRateLoan();
					}
				} else if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_PARTIAL,
						subvention.getType())) {
					calRate = subvention.getRate();
				}

				// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
				String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);

				fvPftFraction = svSchd.getFvPftFraction();
				if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_NO_ADJ)
						|| (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)
								&& DateUtility.compare(curSchd.getSchDate(), subvention.getEndDate()) != 0)) {

					fvPftFraction = BigDecimal.ZERO;
					cbPftFraction = BigDecimal.ZERO;
				}

				// Future Value
				BigDecimal futureVal = CalculationUtil.calInterest(prvSchd.getSchDate(), curSchd.getSchDate(),
						curDisb.getDisbAmount(), curSchd.getPftDaysBasis(), calRate);
				futureVal = futureVal.add(fvPftFraction);

				// Rounding Future Value
				BigDecimal futureValRounded = BigDecimal.ZERO;
				if (futureVal.compareTo(BigDecimal.ZERO) > 0) {
					futureValRounded = CalculationUtil.roundAmount(futureVal, finMain.getCalRoundingMode(),
							finMain.getRoundingTarget());
				}
				svSchd.setFutureValue(futureValRounded);
				fvPftFraction = futureVal.subtract(futureValRounded);

				if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)) {
					fvPftFraction = fvPftFraction.add(prvFvPftFraction);
				}
				svSchd.setFvPftFraction(fvPftFraction);
				prvFvPftFraction = fvPftFraction;
				futureVal = futureValRounded;

				// Finding Days basis
				int daysBasis = 365;
				if (StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_30E360)
						|| StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_30E360I)
						|| StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_30EP360)
						|| StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_30U360)
						|| StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_ACT_360)) {
					daysBasis = 360;
				} else if (StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_ACT_365LEAP)) {

					int yearOfDate = DateUtility.getYear(curSchd.getSchDate());
					if (DateUtility.isLeapYear(yearOfDate)) {
						daysBasis = 366;
					}
				} else if (StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_ACT_365LEAPS)) {

					int yearOfDate = DateUtility.getYear(prvSchd.getSchDate());
					if (DateUtility.isLeapYear(yearOfDate)) {
						daysBasis = 366;
					}
				} else if (StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_30E360IH)) {

					BigDecimal idb30Factor = BigDecimal.valueOf(30 / 360d);
					idb30Factor = idb30Factor.setScale(9, RoundingMode.HALF_DOWN);
					BigDecimal dayFactor = CalculationUtil.getInterestDays(prvSchd.getSchDate(), curSchd.getSchDate(),
							CalculationConstants.IDB_30E360IH);
					BigDecimal dayFactorScale = dayFactor.setScale(9, RoundingMode.HALF_DOWN);

					if (idb30Factor.compareTo(dayFactorScale) == 0) {
						daysBasis = 360;
					} else {
						daysBasis = 365;

						int yearOfDate = DateUtility.getYear(prvSchd.getSchDate());
						if (DateUtility.isLeapYear(yearOfDate)) {
							daysBasis = 366;
						} else {
							yearOfDate = DateUtility.getYear(curSchd.getSchDate());
							if (DateUtility.isLeapYear(yearOfDate)) {
								daysBasis = 366;
							}
						}
					}
				} else if (StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_30E360IA)) {

					BigDecimal idb30Factor = new BigDecimal(30 / 360d).setScale(9);
					BigDecimal dayFactor = CalculationUtil.getInterestDays(prvSchd.getSchDate(), curSchd.getSchDate(),
							CalculationConstants.IDB_30E360IA);
					BigDecimal dayFactorScale = dayFactor.setScale(9);

					if (idb30Factor.compareTo(dayFactorScale) == 0) {
						daysBasis = 360;
					} else {
						daysBasis = 365;

						int yearOfDate = DateUtility.getYear(prvSchd.getSchDate());
						if (DateUtility.isLeapYear(yearOfDate)) {
							daysBasis = 366;
						} else {
							yearOfDate = DateUtility.getYear(curSchd.getSchDate());
							if (DateUtility.isLeapYear(yearOfDate)) {
								daysBasis = 366;
							}
						}
					}
				}

				// Closing Balance
				BigDecimal closingBal = ((futureValRounded.add(prvClosingBal)).multiply(new BigDecimal(daysBasis)))
						.divide((BigDecimal.valueOf(daysBasis)
								.add((subvention.getDiscountRate().multiply(new BigDecimal(svSchd.getNoOfDays())))
										.divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN))),
								9, RoundingMode.HALF_DOWN);
				closingBal = closingBal.add(cbPftFraction);

				// Rounding Closing Balance
				BigDecimal closingBalRounded = BigDecimal.ZERO;
				if (futureVal.compareTo(BigDecimal.ZERO) > 0) {
					closingBalRounded = CalculationUtil.roundAmount(closingBal, finMain.getCalRoundingMode(),
							finMain.getRoundingTarget());
				}
				svSchd.setClosingBal(closingBalRounded);
				cbPftFraction = closingBal.subtract(closingBalRounded);

				if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)) {
					cbPftFraction = cbPftFraction.add(prvCbPftFraction);
				}
				svSchd.setCbPftFraction(cbPftFraction);
				prvCbPftFraction = cbPftFraction;
				closingBal = closingBalRounded;

				// Discounted Profit
				BigDecimal discPft = CalculationUtil.calInterest(prvSchd.getSchDate(), curSchd.getSchDate(),
						closingBalRounded, curSchd.getPftDaysBasis(), subvention.getDiscountRate());
				discPft = CalculationUtil.roundAmount(discPft, finMain.getCalRoundingMode(),
						finMain.getRoundingTarget());
				svSchd.setDiscountedPft(discPft);

				// Present Value
				svSchd.setPresentValue(svSchd.getFutureValue().subtract(svSchd.getDiscountedPft()));
				subAmount = subAmount.add(svSchd.getPresentValue());

				svSchedules.add(svSchd);
				prvClosingBal = closingBalRounded;
			}
			curDisb.setSubventionSchedules(svSchedules);
			curDisb.setSubventionAmount(subAmount);
		}

		logger.debug("leaving");
		return finScheduleData;
	}

	private void setCpzAmounts(FinanceScheduleDetail prvSchd, FinanceScheduleDetail curSchd, boolean cpzPOSIntact,
			boolean cpzResetReq) {

		if (curSchd.isCpzOnSchDate()) {
			// If Capitalized amount cannot be changed OR schedule cannot be
			// recalculated, do nothing.
			if (ImplementationConstants.DFT_CPZ_RESET_ON_RECAL_LOCK || curSchd.isRecalLock()) {
				// Do Nothing
			} else {
				if (cpzResetReq) {
					curSchd.setCpzAmount(curSchd.getProfitBalance());
				}
				if (cpzPOSIntact) {
					if (!StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_MORTEMIHOLIDAY)) {
						curSchd.setCpzBalance(curSchd.getProfitBalance());
					} else {
						curSchd.setCpzBalance(BigDecimal.ZERO);
					}
				}
			}

		} else {
			curSchd.setCpzAmount(BigDecimal.ZERO);
			if (cpzPOSIntact) {
				if (cpzResetReq) {
					BigDecimal newCpzBalance = BigDecimal.ZERO;

					if (curSchd.getProfitSchd().compareTo(curSchd.getProfitCalc()) > 0) {
						newCpzBalance = prvSchd.getCpzBalance().add(curSchd.getProfitCalc())
								.subtract(curSchd.getProfitSchd());
					} else {
						newCpzBalance = prvSchd.getCpzBalance();
					}

					if (newCpzBalance.compareTo(BigDecimal.ZERO) < 0) {
						curSchd.setCpzBalance(BigDecimal.ZERO);
					} else {
						curSchd.setCpzBalance(newCpzBalance);
					}
				}
			} else {
				curSchd.setCpzBalance(BigDecimal.ZERO);
			}
		}

		if (curSchd.getCpzBalance().compareTo(BigDecimal.ZERO) < 0) {
			curSchd.setCpzBalance(BigDecimal.ZERO);
		}
	}

	private FinScheduleData procCalDREMIHolidays(FinScheduleData fsData) {
		FinanceMain fm = fsData.getFinanceMain();
		fsData.getFinanceMain().setEqualRepay(false);
		fsData.getFinanceMain().setCalculateRepay(false);
		fsData.getFinanceMain().setChgDropLineSchd(true);
		boolean isAdjTerms = false;

		if (StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADJTERMS)) {
			if (fm.getAdjTerms() > 0) {
				fsData = procAddTerm(fsData, fm.getAdjTerms(), true);
			}

			isAdjTerms = true;
		} else if (StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			if (fm.getAdjTerms() > 0) {
				fsData = procAddTerm(fsData, fm.getAdjTerms(), true);
			}

			fsData.getFinanceMain().setEqualRepay(true);
			fsData.getFinanceMain().setCalculateRepay(true);
		}

		if (fm.getRecalFromDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {
			List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

			for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
				FinanceScheduleDetail fsd = fsdList.get(iFsd);

				if (fsd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
					fsData.getFinanceMain().setRecalFromDate(fsd.getSchDate());
					break;
				}
			}
		}

		if (StringUtils.isBlank(fsData.getFinanceMain().getRecalSchdMethod())) {
			fsData.getFinanceMain().setRecalSchdMethod(fsData.getFinanceMain().getScheduleMethod());
		}

		Date newMDT = fsData.getFinanceScheduleDetails()
				.get(fsData.getFinanceScheduleDetails().size() - 1 - fm.getAdvTerms()).getSchDate();

		if (!StringUtils.equals(fsData.getFinanceMain().getRecalSchdMethod(), CalculationConstants.SCHMTHD_PFT)) {
			if (StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {

				Date maturityDate = fm.getMaturityDate();

				for (int i = fsData.getFinanceScheduleDetails().size() - 1; i >= 0; i--) {
					FinanceScheduleDetail schDetail = fsData.getFinanceScheduleDetails().get(i);

					if (DateUtility.compare(maturityDate, schDetail.getSchDate()) == 0) {

						if (!FrequencyUtil.isFrqDate(fm.getRepayFrq(), schDetail.getSchDate())) {

							List<Calendar> scheduleDatesList = FrequencyUtil.getNextDate(fm.getRepayFrq(),
									fm.getAdjTerms(), maturityDate, HolidayHandlerTypes.MOVE_NONE, false)
									.getScheduleList();

							Date lastFrqDate = null;
							if (CollectionUtils.isNotEmpty(scheduleDatesList)) {
								Calendar calendar = scheduleDatesList.get(scheduleDatesList.size() - 1);
								lastFrqDate = DateUtility.getDBDate(
										DateUtility.format(calendar.getTime(), PennantConstants.DBDateFormat));
							}

							Calendar calLastFrqDate = Calendar.getInstance();
							calLastFrqDate.setTime(lastFrqDate);

							int day = DateUtility.getDay(maturityDate);
							int maxdays = calLastFrqDate.getActualMaximum(Calendar.DAY_OF_MONTH);

							if (day > maxdays) {
								calLastFrqDate.set(calLastFrqDate.get(Calendar.YEAR),
										calLastFrqDate.get(Calendar.MONTH), maxdays);
							} else {
								calLastFrqDate.set(calLastFrqDate.get(Calendar.YEAR),
										calLastFrqDate.get(Calendar.MONTH), day);
							}

							newMDT = DateUtility.getDBDate(
									DateUtility.format(calLastFrqDate.getTime(), PennantConstants.DBDateFormat));
							schDetail.setSchDate(newMDT);

							sortSchdDetails(fsData.getFinanceScheduleDetails());

							fsData.getFinanceMain().setMaturityDate(newMDT);
							fsData.getFinanceMain().setCalMaturity(newMDT);
							fsData.getFinanceMain().setReqMaturity(newMDT);
						}
						break;
					}
				}
				fsData = setRpyInstructDetails(fsData, fsData.getFinanceMain().getRecalFromDate(), newMDT,
						BigDecimal.ONE, fsData.getFinanceMain().getRecalSchdMethod());
			}
		}

		if (StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)) {

			Date maturityDate = fm.getMaturityDate();

			for (int i = fsData.getFinanceScheduleDetails().size() - 1; i >= 0; i--) {
				FinanceScheduleDetail schDetail = fsData.getFinanceScheduleDetails().get(i);

				if (DateUtility.compare(maturityDate, schDetail.getSchDate()) == 0) {

					if (!FrequencyUtil.isFrqDate(fm.getRepayFrq(), schDetail.getSchDate())) {

						List<Calendar> scheduleDatesList = FrequencyUtil.getNextDate(fm.getRepayFrq(), fm.getAdjTerms(),
								maturityDate, HolidayHandlerTypes.MOVE_NONE, false).getScheduleList();

						Date lastFrqDate = null;
						if (CollectionUtils.isNotEmpty(scheduleDatesList)) {
							Calendar calendar = scheduleDatesList.get(scheduleDatesList.size() - 1);
							lastFrqDate = DateUtility
									.getDBDate(DateUtility.format(calendar.getTime(), PennantConstants.DBDateFormat));
						}

						Calendar calLastFrqDate = Calendar.getInstance();
						calLastFrqDate.setTime(lastFrqDate);

						int day = DateUtility.getDay(maturityDate);
						int maxdays = calLastFrqDate.getActualMaximum(Calendar.DAY_OF_MONTH);

						if (day > maxdays) {
							calLastFrqDate.set(calLastFrqDate.get(Calendar.YEAR), calLastFrqDate.get(Calendar.MONTH),
									maxdays);
						} else {
							calLastFrqDate.set(calLastFrqDate.get(Calendar.YEAR), calLastFrqDate.get(Calendar.MONTH),
									day);
						}

						Date derivedMaturityDate = DateUtility
								.getDBDate(DateUtility.format(calLastFrqDate.getTime(), PennantConstants.DBDateFormat));
						schDetail.setSchDate(derivedMaturityDate);

						sortSchdDetails(fsData.getFinanceScheduleDetails());

						fm.setMaturityDate(derivedMaturityDate);
						fm.setCalMaturity(derivedMaturityDate);
						fm.setReqMaturity(derivedMaturityDate);
					} else {
						schDetail.setRepayOnSchDate(false);
					}

					break;
				}
			}
		}

		int idxLast = fsData.getFinanceScheduleDetails().size() - 1;
		fm.setRecalToDate(fsData.getFinanceScheduleDetails().get(idxLast).getSchDate());
		fsData = calSchdProcess(fsData, false, false);

		if (isAdjTerms) {
			fsData = adjTerms(fsData, true);
		}

		return fsData;
	}

	/**
	 * Method to get the EMI amount on total loan amount used only in aggreements/email templates
	 * 
	 */
	public static BigDecimal getEMIOnFinAssetValue(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);
		BigDecimal emi = BigDecimal.ZERO;
		try {
			// Prepare Finance Schedule Generator Details List
			FinanceDetail detail = new FinanceDetail();
			Cloner cloner = new Cloner();
			detail = cloner.deepClone(financeDetail);

			FinScheduleData data = new FinScheduleData();
			data = cloner.deepClone(detail.getFinScheduleData());

			FinanceMain main = new FinanceMain();
			main = cloner.deepClone(data.getFinanceMain());

			data.setRepayInstructions(new ArrayList<RepayInstruction>());
			data.setFinanceScheduleDetails(new ArrayList<FinanceScheduleDetail>());
			data.setDisbursementDetails(new ArrayList<FinanceDisbursement>());

			// Set Disbursement Details with total loan amount
			FinanceDisbursement disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(main.getFinStartDate());
			disbursementDetails.setDisbSeq(1);
			disbursementDetails.setDisbAmount(main.getFinAssetValue());
			disbursementDetails.setDisbReqDate(disbursementDetails.getDisbDate());
			disbursementDetails.setFeeChargeAmt(main.getFeeChargeAmt());
			disbursementDetails.setQuickDisb(main.isQuickDisb());
			disbursementDetails.setInsuranceAmt(main.getInsuranceAmt());
			data.getDisbursementDetails().add(disbursementDetails);

			detail.setFinScheduleData(ScheduleGenerator.getNewSchd(data));
			data = getCalSchd(data, null);

			if (CollectionUtils.isNotEmpty(detail.getFinScheduleData().getPlanEMIHmonths())) {
				data.setPlanEMIHmonths(detail.getFinScheduleData().getPlanEMIHmonths());
				data = ScheduleCalculator.getFrqEMIHoliday(data);
			}

			if (null != data) {
				List<FinanceScheduleDetail> details = data.getFinanceScheduleDetails();
				if (CollectionUtils.isNotEmpty(details)) {
					for (FinanceScheduleDetail financeScheduleDetail : details) {
						if (financeScheduleDetail.isRepayOnSchDate()
								&& financeScheduleDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
							if (emi.compareTo(financeScheduleDetail.getRepayAmount()) == 0) {
								break;
							}
							emi = financeScheduleDetail.getRepayAmount();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			return emi;
		}
		logger.debug(Literal.LEAVING);
		return emi;
	}

	/*
	 * ________________________________________________________________________________________________________________
	 * Method : prepareManualRepayRI Description: Prepare the repay instructions for step loan if step calculated on
	 * amount.
	 * ________________________________________________________________________________________________________________
	 */
	private void prepareManualRepayRI(FinScheduleData fsData) {
		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceStepPolicyDetail> spdList = fsData.getStepPolicyDetails();
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		int idxStart = 0;
		int riStart = 0;
		int riEnd = 0;
		String schdMethod = "";
		String grcSchdMethod = fm.getGrcSchdMthd();

		if (!fm.isGrcStps()) {
			for (FinanceScheduleDetail fsd : fsdList) {
				if (fsd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {
					idxStart = idxStart + 1;
				}
			}
		}

		boolean grcEnd = false;

		for (int iSpd = 0; iSpd < spdList.size(); iSpd++) {
			FinanceStepPolicyDetail spd = spdList.get(iSpd);

			if (grcEnd && PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
				continue;
			}

			riStart = idxStart;
			if (riEnd == 0) {
				riEnd = riStart + spd.getInstallments();
			} else {
				riEnd = riStart + spd.getInstallments() - 1;
			}

			int instCount = 0;
			for (int iFsd = idxStart; iFsd < fsdList.size(); iFsd++) {
				FinanceScheduleDetail fsd = fsdList.get(iFsd);
				if (iFsd == riStart) {
					spd.setStepStart(fsd.getSchDate());
				}
				String specifier = fsd.getSpecifier();
				if (fsd.isRepayOnSchDate()) {
					instCount = instCount + 1;
				} else if (iFsd != 0 && PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())
						&& !(FinanceConstants.FLAG_BPI.equals(fsd.getBpiOrHoliday()))
						&& (CalculationConstants.SCH_SPECIFIER_GRACE.equals(specifier)
								|| CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier))
						&& !fsd.isDisbOnSchDate()) {
					instCount = instCount + 1;
				}

				//iFsd == riEnd
				boolean flag = CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier)
						&& CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier);
				if (spd.getInstallments() == instCount || flag) {
					if (flag) {
						grcEnd = true;
					}
					spd.setStepEnd(fsd.getSchDate());
					idxStart = iFsd + 1;
					break;
				}
			}

			schdMethod = fm.getScheduleMethod();

			if (!spd.isAutoCal()) {
				setRpyInstructDetails(fsData, spd.getStepStart(), spd.getStepEnd(), spd.getSteppedEMI(), schdMethod);
			} else {
				fm.setRecalFromDate(spd.getStepStart());
				fm.setRecalType(CalculationConstants.RPYCHG_TILLDATE);
				fm.setCalculateRepay(true);
				fm.setEqualRepay(true);
				fm.setRecalSchdMethod(grcSchdMethod);
				setRpyInstructDetails(fsData, fm.getRecalFromDate(), spd.getStepEnd(), BigDecimal.ZERO, grcSchdMethod);
			}
		}
		FinanceStepPolicyDetail spd = spdList.get(spdList.size() - 1);

		if (StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)) {
			fm.setRecalFromDate(spd.getStepStart());
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			fm.setCalculateRepay(true);
			fm.setEqualRepay(true);
			fm.setRecalSchdMethod(schdMethod);
			setRpyInstructDetails(fsData, fm.getRecalFromDate(), fm.getMaturityDate(), BigDecimal.ZERO, schdMethod);
		}

	}

	private FinScheduleData buildRestructure(FinScheduleData fsData) {
		fsData.getFinanceMain().setEqualRepay(false);
		fsData.getFinanceMain().setCalculateRepay(false);
		// fsData.getFinanceMain().setChgDropLineSchd(false);

		RestructureDetail rstDetail = fsData.getRestructureDetail();

		if (!rstDetail.isTenorChange()) {
			fsData = restructureEMIRecal(fsData, true);
			return fsData;
		}

		if (rstDetail.isEmiRecal()) {
			fsData = setRestutureTenor(fsData, false);

			if (fsData.getErrorDetails().size() > 0) {
				return fsData;
			}
			fsData = restructureEMIRecal(fsData, true);
		} else {
			fsData = setRestutureTenor(fsData, true);
			if (fsData.getErrorDetails().size() > 0) {
				return fsData;
			}
			fsData = restructureEMIRecal(fsData, false);
			fsData = restructureTenorAdjust(fsData);
		}
		return fsData;
	}

	private FinScheduleData restructureEMIRecal(FinScheduleData fsData, boolean isEqualRepay) {
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		RestructureDetail rstDetail = fsData.getRestructureDetail();
		FinanceMain fm = fsData.getFinanceMain();
		int idxHldEnd = -1;
		Date HldStart = rstDetail.getRestructureDate();

		if (rstDetail.getEmiHldPeriod() > 0) {
			idxHldEnd = buildRestructureEMIHLD(fsData, HldStart);
			HldStart = fsdList.get(idxHldEnd + 1).getSchDate();
		}

		if (idxHldEnd < 0) {
			idxHldEnd = 1;
		}

		// HldStart = fsdList.get(idxHldEnd).getSchDate();

		if (rstDetail.getPriHldPeriod() > 0) {
			idxHldEnd = buildRestructurePRIHLD(fsData, HldStart, idxHldEnd);
		}

		if (fm.getEventFromDate() == null) {
			fm.setEventFromDate(rstDetail.getRestructureDate());
			fm.setRecalFromDate(rstDetail.getRestructureDate());
		}

		fm.setEventToDate(fm.getMaturityDate());
		fm.setRecalToDate(fm.getMaturityDate());

		if (idxHldEnd < 1) {
			setRpyInstructDetails(fsData, rstDetail.getRestructureDate(), fm.getMaturityDate(), BigDecimal.ZERO,
					fm.getRecalSchdMethod());
		}

		if (isEqualRepay) {
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		} else {
			fm.setRecalType(CalculationConstants.RPYCHG_ADJTERMS);
		}

		fm.setEqualRepay(isEqualRepay);
		fm.setCalculateRepay(isEqualRepay);

		fsData = calSchdProcess(fsData, false, false);

		return fsData;
	}

	private FinScheduleData setRestutureTenor(FinScheduleData fsData, boolean isTenorAdjust) {
		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		RestructureDetail rstDetail = fsData.getRestructureDetail();

		FinanceMain fm = fsData.getFinanceMain();
		Date grcEndDate = fm.getGrcPeriodEndDate();
		Date rstDate = rstDetail.getRestructureDate();
		// Date mdtCurrent = fsData.getFinanceMain().getMaturityDate();
		// Date mdtLimit = DateUtility.addMonths(mdtCurrent, 24); // FEB leap
		// year
		// to be
		// checked..

		int reqTerms = 0;

		if (isTenorAdjust) {
			reqTerms = rstDetail.getEmiHldPeriod() + rstDetail.getPriHldPeriod() + 1;
		} else {
			reqTerms = rstDetail.getEmiHldPeriod() + rstDetail.getPriHldPeriod();
		}

		int availableTerms = 0;

		for (FinanceScheduleDetail fsd : fsdList) {
			if (fsd.getSchDate().compareTo(grcEndDate) <= 0) {
				continue;
			}

			if (fsd.getSchDate().compareTo(rstDate) < 0) {
				continue;
			}

			// If exsitng periods are more than newly required periods remove
			// excee periods
			if (availableTerms < reqTerms) {
				availableTerms = availableTerms + 1;
				continue;
			} else {
				/*
				 * if (!isTenorAdjust) { fsdList.remove(iFsd); }
				 */
			}
		}

		// if existing periods are less than required periods then add new
		// periods
		reqTerms = reqTerms - availableTerms + rstDetail.getTotNoOfRestructure();

		if (reqTerms <= 0) {
			return fsData;
		}

		// Set the limits based on system values table
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtility.addYears(fm.getFinStartDate(), maxFinYears);

		for (int i = 0; i < reqTerms; i++) {
			fsData = addOneTerm(fsData, lastDateLimit, true);
			if (fsData.getErrorDetails().size() > 0) {
				return fsData;
			}
		}

		fsData.getFinanceScheduleDetails().get(fsData.getFinanceScheduleDetails().size() - 1)
				.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);

		return fsData;
	}

	private FinScheduleData restructureTenorAdjust(FinScheduleData fsData) {
		FinanceMain fm = fsData.getFinanceMain();

		// Date mdtCurrent = fm.getMaturityDate();
		// Date mdtLimit = DateUtility.addMonths(mdtCurrent, 24); // FEB leap
		// year
		// to be
		// checked..

		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();

		// Get Adjustment Limits parameters from system values table
		BigDecimal lastTermPercent = new BigDecimal(SysParamUtil.getValueAsString("ADJTERM_LASTTERM_PERCENT"));

		// Set the limits based on system values table
		BigDecimal lastTermLimit = BigDecimal.valueOf(0.0);

		String schdMethod = "";
		int iLast = fsdList.size() - 2;
		lastTermLimit = fsdList.get(iLast).getRepayAmount();
		lastTermLimit = lastTermLimit.multiply(lastTermPercent);
		lastTermLimit = round(lastTermLimit);
		lastTermLimit = fsdList.get(iLast).getRepayAmount().add(lastTermLimit);
		schdMethod = fsData.getRepayInstructions().get(fsData.getRepayInstructions().size() - 1).getRepaySchdMethod();
		schdMethod = CalculationConstants.SCHMTHD_EQUAL;
		// Calculate Schedule
		fsData = calSchdProcess(fsData, false, false);

		iLast = fsdList.size() - 1;

		// If The calculated schedule last repayment is under limit then no need
		// to adjust terms
		if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
			if (fsdList.get(iLast).getRepayAmount().compareTo(lastTermLimit) <= 0) {
				return fsData;
			}
		} else {
			if (fsdList.get(iLast).getPrincipalSchd().compareTo(lastTermLimit) <= 0) {
				return fsData;
			}
		}

		boolean isError = false;

		// Set the limits based on system values table
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtility.addYears(fm.getFinStartDate(), maxFinYears);

		// Adjust Terms
		while (true) {

			if (fsData.getFinanceMain().getRemBalForAdj().compareTo(fsData.getFinanceMain().getAdjOrgBal()) < 0) {
				fsData.getFinanceMain().setAdjOrgBal(fsData.getFinanceMain().getRemBalForAdj());
			}

			fsData = addOneTerm(fsData, lastDateLimit, true);
			fsData.getFinanceMain().setRemBalForAdj(
					fsData.getFinanceMain().getRemBalForAdj().subtract(fsData.getFinanceMain().getAdjOrgBal()));

			if (fsData.getErrorDetails().size() > 0) {
				isError = true;
				break;
			}

			// Calculate Schedule
			fsData = calSchdProcess(fsData, false, false);
			iLast = iLast + 1;
			if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
				if (fsdList.get(iLast).getRepayAmount().compareTo(lastTermLimit) <= 0) {
					break;
				}
			} else {
				if (fsdList.get(iLast).getPrincipalSchd().compareTo(lastTermLimit) <= 0) {
					break;
				}
			}
		}

		// Go back to the allowed date and recalculate
		if (isError) {
			fsdList.remove(fsdList.size() - 1);
			fsdList.get(fsdList.size() - 1).setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);

			fsData.getFinanceMain().setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			fsData.getFinanceMain().setEqualRepay(true);
			fsData.getFinanceMain().setCalculateRepay(true);

			fsData = calSchdProcess(fsData, false, false);

		}

		fsdList.get(fsdList.size() - 1).setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);

		return fsData;
	}

	private int buildRestructureEMIHLD(FinScheduleData fsData, Date HldStart) {
		// Date grcEndDate = fsData.getFinanceMain().getGrcPeriodEndDate();

		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		RestructureDetail rstDetail = fsData.getRestructureDetail();
		int fsdSize = fsdList.size();
		int idxHldStart = -1;
		int idxHldEnd = -1;
		int hldPeriods = 0;

		for (int iFsd = 0; iFsd < fsdSize; iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			/*
			 * if (fsd.getSchDate().compareTo(grcEndDate) <= 0) { continue; }
			 */

			if (fsd.getSchDate().compareTo(HldStart) < 0) {
				continue;
			}

			// Mark Capitalize
			fsd.setCpzOnSchDate(true);
			fsd.setBpiOrHoliday(FinanceConstants.FLAG_RESTRUCTURE);

			if (fsd.isFrqDate() && (fsd.isRepayOnSchDate() || fsd.isPftOnSchDate())) {
				hldPeriods = hldPeriods + 1;

				if (idxHldStart < 0) {
					idxHldStart = iFsd;
				}

				idxHldEnd = iFsd;
			}

			if (hldPeriods == rstDetail.getEmiHldPeriod()) {
				break;
			}
		}

		Date evtFromDate = fsdList.get(idxHldStart).getSchDate();
		Date evtToDate = fsdList.get(idxHldEnd).getSchDate();

		rstDetail.setEmiHldStartDate(evtFromDate);
		rstDetail.setEmiHldEndDate(evtToDate);

		setRpyInstructDetails(fsData, evtFromDate, evtToDate, BigDecimal.ZERO, CalculationConstants.SCHMTHD_NOPAY);

		fsData.getFinanceMain().setRecalIdx(idxHldStart);
		fsData.getFinanceMain().setEventFromDate(evtFromDate);

		for (int iFsd = (idxHldEnd + 1); iFsd < fsdSize; iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			if (fsd.isFrqDate() && fsd.isRepayOnSchDate()) {
				fsData.getFinanceMain().setRecalFromDate(fsd.getSchDate());
				break;
			}
		}

		return idxHldEnd;
	}

	private int buildRestructurePRIHLD(FinScheduleData fsData, Date HldStart, int idxRef) {
		// Date grcEndDate = fsData.getFinanceMain().getGrcPeriodEndDate();

		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		RestructureDetail rstDetail = fsData.getRestructureDetail();
		int fsdSize = fsdList.size();
		int idxHldStart = -1;
		int idxHldEnd = -1;
		int hldPeriods = 0;

		for (int iFsd = idxRef; iFsd < fsdSize; iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			/*
			 * if (fsd.getSchDate().compareTo(grcEndDate) <= 0) { continue; }
			 */

			if (fsd.getSchDate().compareTo(HldStart) < 0) {
				continue;
			}

			// Mark Capitalize
			fsd.setCpzOnSchDate(true);
			fsd.setBpiOrHoliday(FinanceConstants.FLAG_RESTRUCTURE);

			if (fsd.isFrqDate() && (fsd.isRepayOnSchDate() || fsd.isPftOnSchDate())) {
				hldPeriods = hldPeriods + 1;

				if (idxHldStart < 0) {
					idxHldStart = iFsd;
				}

				idxHldEnd = iFsd;
			}

			if (hldPeriods == rstDetail.getPriHldPeriod()) {
				break;
			}
		}

		Date evtFromDate = fsdList.get(idxHldStart).getSchDate();
		Date evtToDate = fsdList.get(idxHldEnd).getSchDate();

		rstDetail.setPriHldStartDate(evtFromDate);
		rstDetail.setPriHldEndDate(evtToDate);

		if (rstDetail.getGrcMaxAmount() != null && rstDetail.getGrcMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
			setRpyInstructDetails(fsData, evtFromDate, evtToDate, rstDetail.getGrcMaxAmount(),
					CalculationConstants.SCHMTHD_PFTCAP);
		} else {
			setRpyInstructDetails(fsData, evtFromDate, evtToDate, BigDecimal.ZERO, CalculationConstants.SCHMTHD_PFT);
		}

		if (rstDetail.getEmiHldPeriod() <= 0) {
			fsData.getFinanceMain().setRecalIdx(idxHldStart);
			fsData.getFinanceMain().setEventFromDate(evtFromDate);
		}

		for (int iFsd = (idxHldEnd + 1); iFsd < fsdSize; iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			if (fsd.isFrqDate() && fsd.isRepayOnSchDate()) {
				fsData.getFinanceMain().setRecalFromDate(fsd.getSchDate());
				break;
			}
		}

		return idxHldEnd;
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
}