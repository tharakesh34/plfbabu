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
 *******************************************************************************************************
 * FILE HEADER *
 *******************************************************************************************************
 *
 * FileName : ScheduleCalculator.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 10-05-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************************
 * 26-04-2011 Pennant 0.1 * * 10-05-2018 Satya 0.2 PSD - Ticket : 126189 * While doing Add Disbursement getting *
 * ArthemeticException in AccrualService due to * NoofDays is ZERO in newly added Schedule * 01-08-2018 Mangapathi 0.3
 * PSD - Ticket : 125445, 125588 * Mail Sub : Freezing Period, Dt : 30-May-2018 * To address Freezing period case when
 * schedule * term is in Presentment. * *
 * 
 * 05-12-2018 Pradeep Varma 0.4 Schedules sent for presentment should and * waiting for fate should be untouched for any
 * * schedule change * 05-12-2018 Pradeep Varma 0.5 Interest should not be left for future * adjustments based on loan
 * type flag * schedule change * 05-12-2018 Pradeep Varma 0.6 Adjut Terms while Rate Change * * *
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
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.overdraft.OverdraftConstants;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdDetail;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdHeader;
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
	public static final String PROC_BUILDOVERDRAFTSCHD = "buildOverdraftSchd";
	public static final String PROC_GETFRQEMIH = "procGetFrqEMIHoliday";
	public static final String PROC_GETADHOCEMIH = "procGetAdhocEMIHoliday";
	public static final String PROC_CHANGETDS = "changeTDS";
	public static final String PROC_REBUILDSCHD = "reBuildSchd";
	public static final String PROC_ADDDATEDSCHEDULE = "procAddDatedSchedule";
	public static final String PROC_CALDREMIHOLIDAYS = "procCalDREMIHolidays";
	public static final String PROC_INSTBASEDSCHEDULE = "procInstBasedSchedule";
	public static final String PROC_RESTRUCTURE = "procRestructure";
	public static final String PROC_PRINCIPALHOLIDAY = "procPrinHoliday";
	public static final String PROC_GETMANUALSCHD = "procGetCalManualSchd";

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

	public static FinScheduleData recalLPISchedule(FinScheduleData finScheduleData, Date lpiMinDate, Date lpiMaxDate) {
		return new ScheduleCalculator(finScheduleData, lpiMinDate, lpiMaxDate).getFinScheduleData();
	}

	public static FinScheduleData addSubSchedule(FinScheduleData finScheduleData, int noOfTerms, Date subSchStartDate,
			String frqNewSchd) {
		return new ScheduleCalculator(PROC_SUBSCHEDULE, finScheduleData, noOfTerms, subSchStartDate, frqNewSchd)
				.getFinScheduleData();
	}

	public static FinScheduleData getCalERR(FinScheduleData finScheduleData) {
		return new ScheduleCalculator(finScheduleData, PROC_CALEFFECTIVERATE).getFinScheduleData();
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

	// InstBasedProcess
	public static FinScheduleData instBasedSchedule(FinScheduleData finScheduleData, BigDecimal amount,
			boolean utilizeGrcEndDisb, boolean isLoanNotApproved, FinanceDisbursement finDisb, boolean feeAmtInclude) {
		return new ScheduleCalculator(PROC_INSTBASEDSCHEDULE, finScheduleData, amount, utilizeGrcEndDisb,
				isLoanNotApproved, finDisb, feeAmtInclude).getFinScheduleData();
	}

	public static FinScheduleData procRestructure(FinScheduleData finScheduleData) {
		return (new ScheduleCalculator(PROC_RESTRUCTURE, finScheduleData)).getFinScheduleData();
	}

	public static FinScheduleData procPrinHoliday(FinScheduleData finScheduleData) {
		return (new ScheduleCalculator(PROC_PRINCIPALHOLIDAY, finScheduleData)).getFinScheduleData();
	}

	// Manual Schedule
	public static FinScheduleData getCalManualSchd(FinScheduleData finScheduleData, BigDecimal desiredPftAmount) {
		return new ScheduleCalculator(PROC_GETMANUALSCHD, finScheduleData, desiredPftAmount).getFinScheduleData();
	}

	// Constructors
	private ScheduleCalculator(String method, FinScheduleData finScheduleData) {
		logger.debug("Entering");

		finScheduleData.getFinanceMain().setRecalIdx(-1);
		FinanceMain fm = finScheduleData.getFinanceMain();
		fm.setAppDate(SysParamUtil.getAppDate());
		fm.setDevFinCalReq(isDeveloperFinance(finScheduleData));

		// re generate original schedule from Flexi Schedule
		if (fm.isAlwFlexi() && fm.isChgDropLineSchd()) {

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

		if (PROC_PRINCIPALHOLIDAY.equals(method)) {
			this.setFinScheduleData(buildPriHoliday(finScheduleData));
		}

		logger.debug("Leaving");
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal desiredPftAmount) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		fm.setRecalIdx(-1);
		fm.setAppDate(SysParamUtil.getAppDate());
		fm.setDevFinCalReq(isDeveloperFinance(finScheduleData));
		finScheduleData.setModuleDefiner(method);

		// re generate original schedule from Flexi Schedule
		if (fm.isAlwFlexi() && fm.isChgDropLineSchd()) {

			rebuildOrgSchdFromFlexiSchd(finScheduleData);
		}

		if (StringUtils.equals(method, PROC_GETCALSCHD)) {

			fm.setRateChange(true);

			if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
					&& finScheduleData.getFinanceMain().getAdvTerms() > 0) {
				finScheduleData.getFinanceMain().setAdjustClosingBal(true);
			}

			setFinScheduleData(procGetCalSchd(finScheduleData));
			fm.setRateChange(false);

			if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
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

		// Manual Schedule
		if (PROC_GETMANUALSCHD.equals(method)) {
			setFinScheduleData(procGetCalManualSchd(finScheduleData));
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

		FinScheduleData dpScheduleData = finScheduleData.copyEntity();
		FinanceMain fm = dpScheduleData.getFinanceMain();
		fm.setRecalIdx(-1);

		fm.setFinReference(fm.getFinReference() + "_DP");
		dpScheduleData.setFinID(fm.getFinID());
		dpScheduleData.setFinReference(fm.getFinReference());

		fm.setFinAmount(fm.getDownPayBank());
		fm.setFeeChargeAmt(BigDecimal.ZERO);
		fm.setDownPayBank(BigDecimal.ZERO);
		fm.setDownPayment(BigDecimal.ZERO);
		fm.setDownPaySupl(BigDecimal.ZERO);

		// Grace Period Details
		fm.setGraceBaseRate("");
		fm.setGraceSpecialRate("");
		fm.setGrcPftRate(BigDecimal.ZERO);

		// Repay period Details
		fm.setRepayBaseRate("");
		fm.setRepaySpecialRate("");
		fm.setRepayProfitRate(BigDecimal.ZERO);
		fm.setScheduleMethod(CalculationConstants.SCHMTHD_EQUAL);
		fm.setEqualRepay(true);
		fm.setCalculateRepay(true);

		// Step Details
		fm.setStepFinance(false);
		fm.setStepPolicy("");
		fm.setNoOfSteps(0);
		fm.setAlwManualSteps(false);
		fm.setPlanDeferCount(0);
		fm.setDefferments(0);
		fm.setNoOfGrcSteps(0);

		// Child List Details
		dpScheduleData.getDisbursementDetails().get(0).setDisbAmount(fm.getFinAmount());
		dpScheduleData.getDisbursementDetails().get(0).setFeeChargeAmt(BigDecimal.ZERO);
		dpScheduleData.getStepPolicyDetails().clear();
		dpScheduleData.getRepayInstructions().clear();
		dpScheduleData.getFeeRules().clear();

		// Schedule Details
		dpScheduleData.getFinanceScheduleDetails().get(0).setClosingBalance(fm.getFinAmount());
		dpScheduleData.getFinanceScheduleDetails().get(0).setFeeChargeAmt(BigDecimal.ZERO);
		dpScheduleData.getFinanceScheduleDetails().get(0).setDisbAmount(fm.getFinAmount());
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

	private ScheduleCalculator(String method, FinScheduleData schdData, String baseRate, String splRate,
			BigDecimal mrgRate, BigDecimal calculatedRate, boolean isCalSchedule) {
		logger.debug(Literal.ENTERING);

		schdData.setModuleDefiner(method);
		if (PROC_CHANGERATE.equals(method)
				&& CalculationConstants.RPYCHG_CURPRD.equals(schdData.getFinanceMain().getRecalType())) {
			getCurPerodDates(schdData);
		}

		setExpectedEndBal(schdData);

		schdData.getFinanceMain().setRecalIdx(-1);
		schdData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());
		schdData.getFinanceMain().setDevFinCalReq(isStepFinance(schdData));

		if (StringUtils.equals(method, PROC_CHANGERATE)) {
			schdData.getFinanceMain().setRateChange(true);

			FinanceMain fm = schdData.getFinanceMain();

			// re generate original schedule from Flexi Schedule
			if (fm.isAlwFlexi() && fm.isChgDropLineSchd()) {
				rebuildOrgSchdFromFlexiSchd(schdData);
			}

			schdData = procChangeRate(schdData, baseRate, splRate, mrgRate, calculatedRate, isCalSchedule, false);

			schdData.getFinanceMain().setRateChange(false);
			if (StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_STEPPOS)) {
				schdData = maintainPOSStep(schdData);
			}

			fm.setScheduleMaintained(true);
			setFinScheduleData(schdData);

		}
		logger.debug(Literal.LEAVING);
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal amount, String schdMethod) {
		logger.debug(Literal.ENTERING);

		finScheduleData.setModuleDefiner(method);
		setExpectedEndBal(finScheduleData);

		FinanceMain fm = finScheduleData.getFinanceMain();
		fm.setRecalIdx(-1);
		fm.setAppDate(SysParamUtil.getAppDate());
		fm.setDevFinCalReq(isDeveloperFinance(finScheduleData));

		// re generate original schedule from Flexi Schedule
		if (fm.isAlwFlexi() && fm.isChgDropLineSchd()) {

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

		logger.debug(Literal.LEAVING);
	}

	private ScheduleCalculator(String method, FinScheduleData finScheduleData, BigDecimal amount,
			BigDecimal feeChargeAmt, boolean utilizeGrcEndDisb) {
		logger.debug("Entering");
		finScheduleData.getFinanceMain().setRecalIdx(-1);
		finScheduleData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());
		finScheduleData.setModuleDefiner(method);

		if (StringUtils.equals(method, PROC_ADDDISBURSEMENT)) {
			setFinScheduleData(procAddDisbursement(finScheduleData, amount, feeChargeAmt, utilizeGrcEndDisb));
		}

		finScheduleData.getFinanceMain().setDevFinCalReq(isDeveloperFinance(finScheduleData));

		if (!finScheduleData.getFinanceMain().isDevFinCalReq()
				&& finScheduleData.getFinanceMain().isStepRecalOnProrata()) {
			finScheduleData.getFinanceMain().setDevFinCalReq(true);
		} else {
			if (finScheduleData.getFinanceMain().isStepFinance() && (CalculationConstants.SCHMTHD_PRI_PFT
					.equals(finScheduleData.getFinanceMain().getScheduleMethod())
					|| CalculationConstants.SCHMTHD_PRI.equals(finScheduleData.getFinanceMain().getScheduleMethod()))) {
				finScheduleData.getFinanceMain().setDevFinCalReq(true);
			}
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
		finScheduleData.getFinanceMain().setDevFinCalReq(isDeveloperFinance(finScheduleData));

		if (StringUtils.equals(method, PROC_CALEFFECTIVERATE)) {
			setFinScheduleData(calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
					finScheduleData.getFinanceMain().getTotalGrossPft(),
					finScheduleData.getFinanceMain().getFinStartDate(),
					finScheduleData.getFinanceMain().getMaturityDate(), true));
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
		finScheduleData.getFinanceMain().setDevFinCalReq(isDeveloperFinance(finScheduleData));

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

		FinanceMain fm = finScheduleData.getFinanceMain();
		fm.setProcMethod(FinServiceEvent.RECEIPT);
		fm.setEarlyPayAmount(earlyPayAmt);

		if (!fm.isApplySanctionCheck()) {
			fm.setApplySanctionCheck(SanctionBasedSchedule.isApplySanctionBasedSchedule(finScheduleData));
		}

		if (fm.isAllowGrcPeriod() && CalculationConstants.SCHMTHD_EQUAL.equals(fm.getScheduleMethod())) {
			fm.setBpiResetReq(false);
		}

		fm.setDevFinCalReq(isStepFinance(fm));
		fm.setEffSchdMethod(method);

		if (CalculationConstants.EARLYPAY_ADJMUR.equals(method)
				|| CalculationConstants.RPYCHG_ADJTNR_STEP.equals(method)
				|| CalculationConstants.RPYCHG_ADJEMI_STEP.equals(method)) {
			method = CalculationConstants.RPYCHG_ADJMDT;
		}

		// Developer Finance, Original Ending Balance Not Changed from Receipts
		if (CalculationConstants.EARLYPAY_PRIHLD.equals(method)) {
			fm.setResetOrgBal(false);
		}

		fm.setEventFromDate(earlyPayOnSchdl);
		fm.setEventToDate(earlyPayOnSchdl);
		fm.setRecalType(method);

		finScheduleData.getFinanceMain().setIndexMisc(-1);
		finScheduleData = resetRecalData(finScheduleData, earlyPayOnSchdl, earlyPayAmt, fm.getReceiptPurpose());

		if (finScheduleData.getFinanceMain().getIndexMisc() >= 0) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails()
					.get(finScheduleData.getFinanceMain().getIndexMisc());

			/* PSD#160197 */
			if (CalculationConstants.SCHMTHD_EQUAL.equals(curSchd.getSchdMethod())) {
				if (FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday()) && !CalculationConstants.SCHMTHD_EQUAL
						.equals(finScheduleData.getFinanceMain().getRecalSchdMethod())) {
					earlyPayAmt = earlyPayAmt.add(curSchd.getPrincipalSchd());
				} else {
					if (FinanceConstants.BPI_SCHD_FIRSTEMI.equals(fm.getBpiTreatment())
							&& curSchd.getInstNumber() == 1) {
						earlyPayAmt = earlyPayAmt.add(curSchd.getPrincipalSchd()).add(curSchd.getProfitCalc());
					} else {
						earlyPayAmt = earlyPayAmt.add(curSchd.getPrincipalSchd()).add(curSchd.getProfitSchd());
					}
				}
			} else {
				earlyPayAmt = earlyPayAmt.add(curSchd.getPrincipalSchd());
			}

		}

		if (fm.isSanBsdSchdle() && CalculationConstants.EARLYPAY_PRIHLD.equals(method)) {
			setRepayForSanctionBasedPriHld(finScheduleData, earlyPayAmt);
		} else if (CalculationConstants.RPYCHG_ADJMDT.equals(method)
				|| CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(method)
				|| CalculationConstants.EARLYPAY_ADMPFI.equals(method)) {

			fm.setRecalToDate(fm.getMaturityDate());
			final BigDecimal totalDesiredProfit = fm.getTotalGrossPft();

			// TODO: PV 19JAN17 schedule method should be sent correctly
			finScheduleData = changeRepay(finScheduleData, earlyPayAmt, fm.getRecalSchdMethod());

			List<FinanceScheduleDetail> finSchdlDetailList = finScheduleData.getFinanceScheduleDetails();
			int size = finScheduleData.getFinanceScheduleDetails().size();
			Date eventToDate = fm.getMaturityDate();

			if (!FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory()) && !fm.isApplySanctionCheck()) {
				for (int i = size - 1; i >= 0; i--) {
					FinanceScheduleDetail schDetail = finSchdlDetailList.get(i);
					if ((schDetail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0)) {
						finSchdlDetailList.remove(i);
					} else {
						eventToDate = schDetail.getSchDate();
						break;
					}
				}

				fm.setMaturityDate(eventToDate);
			}

			if (CalculationConstants.EARLYPAY_ADMPFI.equals(method)) {

				fm.setEventToDate(eventToDate);
				// Apply Effective Rate for ReSchedule to get Desired Profit
				finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
						totalDesiredProfit, null, null, false);
				finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			}

		} else if (CalculationConstants.EARLYPAY_RECRPY.equals(method)
				|| (!fm.isAlwFlexi() && CalculationConstants.EARLYPAY_PRIHLD.equals(method))) {

			fm.setRecalToDate(fm.getMaturityDate());

			// Schedule Repayment Change
			// TODO: PV 19JAN17 schedule method should be sent correctly
			finScheduleData = changeRepay(finScheduleData, earlyPayAmt, fm.getRecalSchdMethod());

			if (earlyPayOnNextSchdl == null) {
				List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
				for (FinanceScheduleDetail curSchd : schdList) {

					if (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
						continue;
					}

					if (DateUtil.compare(curSchd.getSchDate(), earlyPayOnSchdl) > 0) {
						if (!curSchd.isRepayOnSchDate()) {
							continue;
						}

						earlyPayOnNextSchdl = curSchd.getSchDate();
						break;
					}
				}
			}

			// Schedule ReCalculations afetr Early Repayment Period based upon
			// Schedule Method
			if (earlyPayOnNextSchdl == null) {
				earlyPayOnNextSchdl = fm.getRecalFromDate();
			}

			fm.setEventFromDate(earlyPayOnNextSchdl);
			// finMain.setRecalFromDate(earlyPayOnNextSchdl);
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			fm.setResetFromLastStep(true);

			// TODO: PV 19JAN17 schedule method should be sent correctly
			finScheduleData = reCalSchd(finScheduleData, fm.getScheduleMethod());

			// finScheduleData.getFinanceScheduleDetails().addAll(tempSchdlDetailList);

		} else if (CalculationConstants.EARLYPAY_RECPFI.equals(method)) {

			final BigDecimal totalDesiredProfit = fm.getTotalGrossPft();

			fm.setRecalToDate(fm.getMaturityDate());

			// Apply Effective Rate for ReSchedule to get Desired Profit
			finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
					totalDesiredProfit, null, fm.getMaturityDate(), false);

		} else if (CalculationConstants.RPYCHG_STEPPOS.equals(method)) {

			fm.setRecalToDate(fm.getMaturityDate());

			// Schedule Repayment Change
			finScheduleData = changeRepay(finScheduleData, earlyPayAmt, fm.getRecalSchdMethod());

			// Schedule ReCalculations afetr Early Repayment Period based upon
			// Schedule Method
			fm.setEventFromDate(earlyPayOnNextSchdl);
			fm.setRecalFromDate(earlyPayOnNextSchdl);
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

			finScheduleData = maintainPOSStep(finScheduleData);
			finScheduleData.getFinanceMain().setScheduleMaintained(true);

			// PV : Principal Holiday, Removed here and Included in
			// EARLYPAY_RECRPY
		} else if (CalculationConstants.EARLYPAY_PRIHLD.equals(method)) {

			/*
			 * if (!finMain.isAlwFlexi()) { finScheduleData = principalHoliday(finScheduleData, earlyPayAmt); }
			 */
		} else if (fm.isManualSchedule()) {
			setFinScheduleData(calManualSchdProcess(finScheduleData, true, FinServiceEvent.RECEIPT));
			logger.debug(Literal.LEAVING);
			return;
		}

		// Recalculation of Details after Schedule calculation
		finScheduleData = afterChangeRepay(finScheduleData);
		finScheduleData.getFinanceMain().setBpiResetReq(false);
		setFinanceTotals(finScheduleData);

		if (CalculationConstants.EARLYPAY_ADJMUR.equals(receivedRecalMethod)) {
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

		if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance()) {
			finScheduleData.getFinanceMain().setDevFinCalReq(true);
		}

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

	public ScheduleCalculator(FinScheduleData schdData, Date lpiMinDate, Date lpiMaxDate) {
		FinanceMain fm = schdData.getFinanceMain();
		fm.setRecalFromDate(lpiMinDate);
		fm.setRecalToDate(lpiMaxDate);
		fm.setEventFromDate(lpiMinDate);
		fm.setEventToDate(lpiMaxDate);

		setFinScheduleData(calSchdProcess(schdData, false, false));
	}

	private FinScheduleData procGetCalSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();

		// For FLEXI Loans (only origination)
		fm.setChgDropLineSchd(true);

		List<FinanceScheduleDetail> finScheduleDetails = finScheduleData.getFinanceScheduleDetails();
		Boolean isCalFlat = false;

		if (fm.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_C)) {
			isCalFlat = true;
		}

		if (fm.getGrcProfitDaysBasis() == null) {
			fm.setGrcProfitDaysBasis(fm.getProfitDaysBasis());
		}

		// START BPI
		if (!fm.isAllowGrcPeriod() && StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
			fm.setBpiResetReq(false);
		}

		// BPI then add BPI record to the schedule
		if (fm.isAlwBPI()) {
			finScheduleData.getFinanceMain().setModifyBpi(true);
			finScheduleData = addBPISchd(finScheduleData);
		}

		fm.setEventFromDate(fm.getFinStartDate());
		fm.setEventToDate(fm.getMaturityDate());
		fm.setRecalFromDate(fm.getFinStartDate());
		fm.setRecalToDate(fm.getMaturityDate());

		// PREPARE FIND SCHDULE DATA
		finScheduleData = preapareFinSchdData(finScheduleData, isCalFlat);
		finScheduleData = calSchdProcess(finScheduleData, isCalFlat, true);

		// If Grace Period calculation amount has maximum capping
		if (fm.isAllowGrcPeriod() && fm.getGrcMaxAmount().compareTo(BigDecimal.ZERO) > 0
				&& StringUtils.equals(CalculationConstants.SCHMTHD_PFTCAP, fm.getGrcSchdMthd())) {
			finScheduleData.getFinanceMain().setEventFromDate(fm.getFinStartDate());
			finScheduleData.getFinanceMain().setEventToDate(fm.getGrcPeriodEndDate());
			finScheduleData = procChangeRepay(finScheduleData, fm.getGrcMaxAmount(),
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
		fm.setEventFromDate(fm.getFinStartDate());
		fm.setEventToDate(fm.getMaturityDate());

		// START BPI
		fm.setBpiResetReq(true);
		// START BPI

		// Subvention Schedule Details Calculation
		buildSubventionSchedule(finScheduleData);

		// BPI Change
		finScheduleData = setFinanceTotals(finScheduleData);
		finScheduleData.getFinanceMain().setModifyBpi(false);
		setFinScheduleData(finScheduleData);

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	public static Date getFirstInstalmentDate(List<FinanceScheduleDetail> schedules) {
		for (FinanceScheduleDetail schedule : schedules) {
			if (schedule.getInstNumber() == 1) {
				return schedule.getSchDate();
			}
		}
		return null;
	}

	private FinScheduleData procGetFrqEMIHoliday(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		FinanceMain fm = finScheduleData.getFinanceMain();

		finScheduleData = validateEMIHoliday(finScheduleData);
		if (finScheduleData.getErrorDetails() != null && finScheduleData.getErrorDetails().size() > 0) {
			// Return the schedule header
			logger.debug("Leaving");
			return finScheduleData;
		}

		int frqSize = finScheduleData.getPlanEMIHmonths().size();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
		int sdSize = schedules.size();

		int planEMIHMaxPerYear = fm.getPlanEMIHMaxPerYear();
		int planEMIHMax = fm.getPlanEMIHMax();
		boolean planEMICpz = fm.isPlanEMICpz();

		int markedEMIHMaxPerYear = 0;
		int markedEMIHMax = 0;

		Date firstInstalmentDate = getFirstInstalmentDate(schedules);
		Date datePlanEMIHLock = DateUtil.addMonths(firstInstalmentDate, fm.getPlanEMIHLockPeriod());
		Date dateAfterYear = DateUtil.addMonths(firstInstalmentDate, 12);
		boolean maxReached = false;

		for (int i = 0; i < sdSize - 1; i++) {
			FinanceScheduleDetail curSchd = schedules.get(i);
			Date schdDate = curSchd.getSchDate();

			// Before Lock Period do not mark holiday
			if (DateUtil.compare(schdDate, datePlanEMIHLock) <= 0) {
				continue;
			}

			// Before Grace Period should not mark as Holiday if EMI Holiday not required
			if (DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) <= 0 && !fm.isPlanEMIHAlwInGrace()) {
				continue;
			}

			// In Repay Period should not mark as Holiday if Plan EMI Holiday not required
			if (DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) > 0 && !fm.isPlanEMIHAlw()) {
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
				if (isEMIHoliday(curSchd.getBpiOrHoliday())) {
					curSchd.setBpiOrHoliday("");
				}
				continue;
			}

			// Find schedule date is requested holiday or not
			if (planEMIHMaxPerYear == 0 || markedEMIHMaxPerYear < planEMIHMaxPerYear) {
				int curSchdMonth = DateUtil.getMonth(schdDate);
				for (int j = 0; j < frqSize; j++) {
					int curFrqMonth = finScheduleData.getPlanEMIHmonths().get(j);
					if (curSchdMonth == curFrqMonth && (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())) {

						// Before Event From Date don't do any changes(Used same
						// for servicing Plan EMI/Rescheduling)
						if (DateUtil.compare(schdDate, fm.getEventFromDate()) > 0) {
							curSchd.setCpzOnSchDate(planEMICpz);
							curSchd.setBpiOrHoliday(FinanceConstants.FLAG_HOLIDAY);
						}

						if (isEMIHoliday(curSchd.getBpiOrHoliday())) {
							markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
							markedEMIHMax = markedEMIHMax + 1;
						}
						break;
					} else {

						// Before Event From Date don't do any changes(Used same
						// for servicing Plan EMI/Rescheduling)
						if (DateUtil.compare(schdDate, fm.getEventFromDate()) > 0) {
							if (isEMIHoliday(curSchd.getBpiOrHoliday())) {
								curSchd.setBpiOrHoliday("");
							}
						} else {
							if (isEMIHoliday(curSchd.getBpiOrHoliday())) {
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
				if (DateUtil.compare(schdDate, fm.getEventFromDate()) > 0) {
					if (isEMIHoliday(curSchd.getBpiOrHoliday())) {
						curSchd.setBpiOrHoliday("");
					}
				} else {
					if (isEMIHoliday(curSchd.getBpiOrHoliday())) {
						markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
						markedEMIHMax = markedEMIHMax + 1;
					}
				}
			}

			// Reset marked holidays per year
			if (DateUtil.compare(schdDate, dateAfterYear) >= 0) {
				markedEMIHMaxPerYear = 0;
				dateAfterYear = DateUtil.addMonths(schdDate, 12);
			}

			if (planEMIHMax != 0 && markedEMIHMax >= planEMIHMax) {
				maxReached = true;
			}
		}

		if (fm.getEventFromDate() != null && DateUtil.compare(fm.getEventFromDate(), fm.getFinStartDate()) == 0) {
			if (finScheduleData.getFinanceType() != null) {
				fm.setEqualRepay(finScheduleData.getFinanceType().isEqualRepayment());
			} else {
				fm.setEqualRepay(true);
			}
		}

		fm.setCalculateRepay(true);

		if (fm.isStepFinance() && fm.isAlwManualSteps()) {
			if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
					&& CollectionUtils.isNotEmpty(finScheduleData.getStepPolicyDetails())) {
				prepareManualRepayRI(finScheduleData);
			}
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procGetAdhocEMIHoliday(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		finScheduleData = validateEMIHoliday(finScheduleData);

		if (finScheduleData.getErrorDetails() != null && finScheduleData.getErrorDetails().size() > 0) {
			// Return the schedule header
			logger.debug("Leaving");
			return finScheduleData;
		}

		int hdSize = finScheduleData.getPlanEMIHDates().size();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();
		int sdSize = schedules.size();

		int planEMIHMaxPerYear = fm.getPlanEMIHMaxPerYear();
		int planEMIHMax = fm.getPlanEMIHMax();
		boolean planEMICpz = fm.isPlanEMICpz();

		int markedEMIHMaxPerYear = 0;
		int markedEMIHMax = 0;

		Date firstInstalmentDate = getFirstInstalmentDate(schedules);
		Date datePlanEMIHLock = DateUtil.addMonths(firstInstalmentDate, fm.getPlanEMIHLockPeriod());
		Date dateAfterYear = DateUtil.addMonths(firstInstalmentDate, 12);
		Collections.sort(finScheduleData.getPlanEMIHDates());

		for (int j = 0; j < sdSize - 1; j++) {
			FinanceScheduleDetail curSchd = schedules.get(j);
			Date schdDate = curSchd.getSchDate();

			// First payment date also cannot be allowed in planned EMI holiday
			// declaration
			if (curSchd.getInstNumber() == 1) {
				continue;
			}

			// Reset marked holidays per year
			if (DateUtil.compare(schdDate, dateAfterYear) >= 0) {
				markedEMIHMaxPerYear = 0;
				dateAfterYear = DateUtil.addMonths(schdDate, 12);
			}

			if (DateUtil.compare(schdDate, datePlanEMIHLock) <= 0) {
				continue;
			}

			boolean isPlanEmiHFound = false;
			if (hdSize != markedEMIHMax) {
				for (int i = 0; i < hdSize; i++) {
					Date hdDate = finScheduleData.getPlanEMIHDates().get(i);

					// Find schedule date is requested holiday or not
					if (planEMIHMaxPerYear == 0 || markedEMIHMaxPerYear < planEMIHMaxPerYear) {
						if (DateUtil.compare(hdDate, schdDate) == 0) {
							if ((curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())) {

								// Before Event From Date don't do any
								// changes(Used same for servicing Plan
								// EMI/Rescheduling)
								if (DateUtil.compare(schdDate, fm.getEventFromDate()) > 0) {
									curSchd.setCpzOnSchDate(planEMICpz);
									curSchd.setBpiOrHoliday(FinanceConstants.FLAG_HOLIDAY);
								}

								if (isEMIHoliday(curSchd)) {
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
				if (DateUtil.compare(schdDate, fm.getEventFromDate()) > 0) {
					if (isEMIHoliday(curSchd)) {
						curSchd.setBpiOrHoliday("");
						curSchd.setCpzOnSchDate(isCpzOnSchDate(fm, curSchd));
					}
				} else {
					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
						markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
						markedEMIHMax = markedEMIHMax + 1;
					}
				}
			}
		}

		if (FinServiceEvent.CHGFRQ.equals(fm.getProcMethod())) {
			fm.setEqualRepay(false);
		} else {
			if (finScheduleData.getFinanceType() != null) {
				fm.setEqualRepay(finScheduleData.getFinanceType().isEqualRepayment());
			} else {
				fm.setEqualRepay(true);
			}
		}
		fm.setCalculateRepay(true);

		if (fm.isStepFinance() && fm.isAlwManualSteps()) {
			if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
					&& CollectionUtils.isNotEmpty(finScheduleData.getStepPolicyDetails())) {
				if (!FinServiceEvent.PLANNEDEMI.equals(fm.getModuleDefiner())) {
					prepareManualRepayRI(finScheduleData);
				}
			}
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		// Return the schedule header
		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData validateEMIHoliday(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		// TODO: PV: Correct Error Code
		if (!fm.isPlanEMIHAlw() && !fm.isPlanEMIHAlwInGrace()) {
			finScheduleData.setErrorDetail(
					new ErrorDetail("SCH37", "Loan Does not allow Planned EMI's", new String[] { " " }));
			return finScheduleData;
		}

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(fm.getProductCategory())) {
			finScheduleData.setErrorDetail(
					new ErrorDetail("SCH37", "Product Category not allow Planned EMI's", new String[] { " " }));
			return finScheduleData;
		}

		if (fm.isStepFinance() && !PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {
			finScheduleData.setErrorDetail(
					new ErrorDetail("SCH37", "Step Loans does not allow Planned EMI's", new String[] { " " }));
			return finScheduleData;
		}

		if (fm.isFinIsAlwMD()) {
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		fm.setRecalIdx(-1);
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		int sdSize = finSchdDetails.size();

		Date prvGraceEnd = fm.getEventFromDate();
		Date newGraceEnd = fm.getGrcPeriodEndDate();

		fm.setRecalIdx(-1);

		Date oldInstructions = new Date();
		if (DateUtil.compare(prvGraceEnd, newGraceEnd) <= 0) {
			oldInstructions = prvGraceEnd;
		} else {
			oldInstructions = newGraceEnd;
		}

		String prvRpySchMthd = null;
		// Delete All repay instructions in repayment period
		for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {
			if (DateUtil.compare(finScheduleData.getRepayInstructions().get(i).getRepayDate(),
					oldInstructions) >= 0) {
				finScheduleData.getRepayInstructions().remove(i);
				i = i - 1;
			} else if (DateUtil.compare(finScheduleData.getRepayInstructions().get(i).getRepayDate(),
					oldInstructions) < 0
					&& (StringUtils.equals(finScheduleData.getRepayInstructions().get(i).getRepaySchdMethod(),
							CalculationConstants.SCHMTHD_EQUAL)
							|| StringUtils.equals(finScheduleData.getRepayInstructions().get(i).getRepaySchdMethod(),
									CalculationConstants.SCHMTHD_PRI_PFT))) {
				finScheduleData.getRepayInstructions().get(i).setRepaySchdMethod(fm.getGrcSchdMthd());
				finScheduleData.getRepayInstructions().get(i).setRepayAmount(BigDecimal.ZERO);
			}
		}

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			if (DateUtil.compare(curSchd.getSchDate(), newGraceEnd) <= 0) {
				continue;
			}

			if (!curSchd.isPftOnSchDate() && !curSchd.isRepayOnSchDate()) {
				continue;
			}

			fm.setRecalFromDate(curSchd.getSchDate());
			// finMain.setEventFromDate(curSchd.getSchDate());
			fm.setRecalSchdMethod(fm.getScheduleMethod());
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			break;
		}

		// Setting EventFromDate and EventToDate
		fm.setEventToDate(fm.getMaturityDate());
		if (DateUtil.compare(newGraceEnd, prvGraceEnd) <= 0) {
			fm.setEventFromDate(newGraceEnd);
		}

		if (!StringUtils.equals(prvRpySchMthd, fm.getGrcSchdMthd())) {
			finScheduleData = setRpyInstructDetails(finScheduleData, newGraceEnd, newGraceEnd, fm.getGrcMaxAmount(),
					fm.getGrcSchdMthd());
		}

		finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGEGRACEEND, BigDecimal.ZERO, BigDecimal.ZERO);

		if (fm.isStepFinance() && fm.isAlwManualSteps()) {
			if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
					&& CollectionUtils.isNotEmpty(finScheduleData.getStepPolicyDetails())) {
				prepareManualRepayRI(finScheduleData);
			}
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);
		fm.setScheduleMaintained(true);
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();
		fm.setApplySanctionCheck(SanctionBasedSchedule.isApplySanctionBasedSchedule(finScheduleData));

		int risize = repayInstructions.size();

		// DE#1550(24-10-2020) - While doing the ReScheduling, RPS is not plotting properly in case of Grace.
		/*
		 * Date evtFromDate = finMain.getRecalFromDate(); finMain.setEventFromDate(evtFromDate);
		 * finMain.setEventToDate(evtFromDate);
		 */

		Date evtFromDate = fm.getRecalFromDate();

		sortRepayInstructions(repayInstructions);
		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.equals(schdMethod, "")) {
			for (int i = 0; i < risize; i++) {
				if (DateUtil.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
				}

				// FIXME: below code is temporary fix, need to set schedule
				// method
				if (StringUtils.isBlank(fm.getRecalSchdMethod())) {
					fm.setRecalSchdMethod(fm.getScheduleMethod());
				}

				if (DateUtil.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) >= 0) {
					break;
				}
			}
		}

		fm.setRecalSchdMethod(schdMethod);

		if (fm.isApplySanctionCheck()) {
			boolean isResetRecalDate = false;
			if (!StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					&& !CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(fm.getRecalType())) {

				if (StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
					isResetRecalDate = true;
				}

				finScheduleData = setRepayForSanctionBasedAddRecal(finScheduleData, isResetRecalDate);
			}

		} else {
			if (!fm.isStepFinance()) {
				finScheduleData = setRecalAttributes(finScheduleData, PROC_RECALSCHD, BigDecimal.ZERO, BigDecimal.ZERO);
			}
		}

		boolean isStepLoan = false;
		if (fm.isStepFinance() && fm.isAlwManualSteps() && !CalculationConstants.RPYCHG_ADJMDT.equals(fm.getRecalType())
				&& !CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(fm.getRecalType())) {
			if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
					&& CollectionUtils.isNotEmpty(finScheduleData.getStepPolicyDetails())) {
				prepareManualRepayRI(finScheduleData);
				isStepLoan = true;
				// finScheduleData.getFinanceMain().setEqualRepay(false);
			}
		}

		// Recalculate : Manual Schedule Changes
		if (fm.isManualSchedule()) {
			calManualSchdProcess(finScheduleData, true, FinServiceEvent.RECALCULATE);

			logger.debug(Literal.LEAVING);
			return finScheduleData;
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		fm.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		if (isStepLoan && ImplementationConstants.SAN_BASED_EMI_REQUIRED_STEP) {
			getSanBasedInterest(finScheduleData);
		}

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

		FinanceMain fm = finScheduleData.getFinanceMain();

		// BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

		Date evtFromDate = fm.getRecalFromDate();
		fm.setEventFromDate(evtFromDate);
		fm.setEventToDate(evtFromDate);
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

		FinanceMain fm = finScheduleData.getFinanceMain();

		Date evtFromDate = fm.getEventFromDate();
		Date curBussniessDate = fm.getAppDate();

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		/*----------------------------------------------------------------
		 *To Delete Terms the below conditions must meet the below conditions
		 * i) Calculated Final Date after deletion must be on/after current business date
		 * ii) Calculated Final Date after deletion must be with repayment on schedule date TRUE
		 * iii) Should not have any deferment payments pending after calculated final date  
		 */

		// Current Schedule Date is after current business date
		// PV 02JUN18: isException is not in use. To overcome Business Date comparison in TestNG cases it is introduced.
		if (DateUtil.compare(evtFromDate, curBussniessDate) < 0 && !fm.isException()) {
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
			if (DateUtil.compare(curSchd.getSchDate(), evtFromDate) >= 0) {
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
			fm.setIndexStart(i);
			if (DateUtil.compare(curSchd.getSchDate(), evtFromDate) == 0) {
				break;
			}
		}

		// Delete all schedule details after requested delete terms date
		for (int i = fm.getIndexStart(); i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			if (DateUtil.compare(finScheduleData.getFinanceScheduleDetails().get(i).getSchDate(), evtFromDate) > 0) {
				finScheduleData.getFinanceScheduleDetails().remove(i);
				i--;
			}
		}

		// Reset Maturity Date, Recal type
		fm.setMaturityDate(finScheduleData.getFinanceScheduleDetails()
				.get(finScheduleData.getFinanceScheduleDetails().size() - 1).getSchDate());
		fm.setCalMaturity(fm.getMaturityDate());

		// Delete All repay instructions after requested delete terms date
		for (int i = 0; i < finScheduleData.getRepayInstructions().size(); i++) {
			if (DateUtil.compare(finScheduleData.getRepayInstructions().get(i).getRepayDate(),
					fm.getCalMaturity()) >= 0) {
				finScheduleData.getRepayInstructions().remove(i);
			}
		}

		// Recalculate Schedule
		if (fm.getRecalFromDate() != null) {
			fm.setEventFromDate(fm.getRecalFromDate());
		}
		finScheduleData = procReCalSchd(finScheduleData, "");
		fm.setScheduleMaintained(true);

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

		FinanceMain fm = finScheduleData.getFinanceMain();
		String recalType = fm.getRecalType();
		String rcvRecalType = recalType;
		Date evtFromDate = fm.getEventFromDate();
		Date evtToDate = fm.getEventToDate();

		fm.setCompareToExpected(false);
		fm.setCompareExpectedResult(BigDecimal.ZERO);
		boolean isApplySanctionBasedSchedule = SanctionBasedSchedule.isApplySanctionBasedSchedule(finScheduleData);

		if (isApplySanctionBasedSchedule) {
			if (!StringUtils.equals(recalType, CalculationConstants.RPYCHG_ADJMDT)
					&& !CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(recalType)) {
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
			fm.setRecalType(recalType);
		}

		// Current Period or Till MDT set Recal from date and recal todate
		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_CURPRD)) {
			getCurPerodDates(finScheduleData);
		}

		// Force Set recaltype and recal to date to TILLMDT
		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_CURPRD)) {

			if (DateUtil.compare(evtToDate, fm.getGrcPeriodEndDate()) <= 0) {
				fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			}

			if (DateUtil.compare(evtToDate, fm.getMaturityDate()) >= 0) {
				fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			}
		}

		// FIXME: When Recal From Date and Recal To Date are different periods.
		// Delete this line once proved working
		// Same code is kept in add disbursement also (Whereever recal is
		// possible in two periods..)

		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_ADJMDT)
				|| CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(recalType)) {
			fm.setRecalFromDate(fm.getMaturityDate());
			fm.setRecalToDate(fm.getMaturityDate());
		}

		if (StringUtils.equals(recalType, CalculationConstants.RPYCHG_TILLMDT)) {
			fm.setRecalToDate(fm.getMaturityDate());
		}

		if (DateUtil.compare(fm.getRecalFromDate(), fm.getGrcPeriodEndDate()) <= 0
				&& DateUtil.compare(fm.getRecalToDate(), fm.getGrcPeriodEndDate()) > 0) {

			for (int i = 0; i < finSchdDetails.size(); i++) {
				if (DateUtil.compare(finSchdDetails.get(i).getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
					continue;
				}
				if (!finSchdDetails.get(i).isRepayOnSchDate()) {
					continue;
				}

				fm.setRecalFromDate(finSchdDetails.get(i).getSchDate());
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

			if (DateUtil.compare(schdDate, evtFromDate) > 0) {
				break;
			}

			if (DateUtil.compare(schdDate, evtFromDate) == 0) {
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

			if (fm.isSkipRateReset()) {
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

			if (DateUtil.compare(schdDate, evtToDate) > 0) {
				break;
			}

			if (DateUtil.compare(schdDate, evtToDate) == 0) {
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

		if (!fm.isSkipRateReset()) {
			// Parameter for counting the number of schedules in between
			// evtFromDate and evtToDate
			BigDecimal calRate = finSchdDetails.get(0).getCalculatedRate();

			for (int i = 0; i < sdSize; i++) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(i);
				Date schdDate = curSchd.getSchDate();

				// Setting Rates between Fromdate and Todate
				if ((DateUtil.compare(schdDate, evtFromDate) >= 0 && DateUtil.compare(schdDate, evtToDate) < 0)
						|| (i == (sdSize - 1))) {

					if (DateUtil.compare(schdDate, evtFromDate) == 0 && StringUtils.isBlank(baseRate)) {
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
							if (DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) < 0) {
								recalculateRate = RateUtil.rates(baseRate, fm.getFinCcy(), splRate, mrgRate, schdDate,
										fm.getGrcMinRate(), fm.getGrcMaxRate()).getNetRefRateLoan();
							} else {
								recalculateRate = RateUtil.rates(baseRate, fm.getFinCcy(), splRate, mrgRate, schdDate,
										fm.getRpyMinRate(), fm.getRpyMaxRate()).getNetRefRateLoan();
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
				if (DateUtil.compare(schdDate, evtToDate) >= 0) {
					break;
				}
			}
		}

		// Rate Change Not Required. Do not recalculate again
		if (!isRateChgReq) {
			logger.debug("Leaving - No change in Rates, so exit without calculation");
			return finScheduleData;
		}

		// Rate Change : Manual Schedule, always Till Maturity
		if (fm.isManualSchedule()) {
			finScheduleData = calManualSchdProcess(finScheduleData, false, FinServiceEvent.RATECHG);

			logger.debug(Literal.LEAVING);
			return finScheduleData;
		}

		// call the process
		fm.setProcMethod(FinServiceEvent.RATECHG);
		if (isCalSchedule) {
			if (!fm.isStepFinance()) {
				finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGERATE, BigDecimal.ZERO,
						BigDecimal.ZERO);
			}
			finScheduleData = calSchdProcess(finScheduleData, false, false);
		}

		// Actual Requirement is adjust terms
		if (StringUtils.equals(rcvRecalType, CalculationConstants.RPYCHG_ADJTERMS)) {
			recalType = CalculationConstants.RPYCHG_ADJTERMS;
			fm.setRecalType(recalType);

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

	private FinScheduleData addSchdRcd(FinScheduleData schdData, Date newSchdDate, int prvIndex,
			boolean addClosingBal) {
		FinanceScheduleDetail prvSchd = schdData.getFinanceScheduleDetails().get(prvIndex);
		FinanceMain fm = schdData.getFinanceMain();

		FinanceScheduleDetail sd = new FinanceScheduleDetail();
		sd.setFinID(fm.getFinID());
		sd.setFinReference(fm.getFinReference());
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
		if (FinanceConstants.FLAG_BPI.equals(prvSchd.getBpiOrHoliday())) {
			if (DateUtil.compare(newSchdDate, fm.getGrcPeriodEndDate()) <= 0) {
				sd.setPftDaysBasis(fm.getGrcProfitDaysBasis());
			} else {
				sd.setPftDaysBasis(fm.getProfitDaysBasis());
			}
		} else {
			sd.setPftDaysBasis(prvSchd.getPftDaysBasis());
		}
		sd.setOrgEndBal(prvSchd.getOrgEndBal());
		sd.setLimitDrop(prvSchd.getLimitDrop());
		sd.setODLimit(prvSchd.getODLimit());

		if (addClosingBal) {
			sd.setClosingBalance(prvSchd.getClosingBalance());
		}

		// ### 10-05-2018 - PSD Ticket ID : 126189, Flexi
		sd.setNoOfDays(DateUtil.getDaysBetween(newSchdDate, prvSchd.getSchDate()));
		sd.setDayFactor(CalculationUtil.getInterestDays(prvSchd.getSchDate(), newSchdDate, sd.getPftDaysBasis()));

		schdData.getFinanceScheduleDetails().add(sd);
		schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));

		return schdData;
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();
		String reqSchdMethod = schdMethod;

		// BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

		Date evtFromDate = fm.getEventFromDate();
		Date evtToDate = fm.getEventToDate();

		Date recalFromDate = fm.getRecalFromDate();

		// Repay Change with in Grace Period
		if (CalculationConstants.SCHMTHD_PFTCAP.equals(reqSchdMethod)
				|| CalculationConstants.SCHMTHD_PRI_PFTC.equals(reqSchdMethod)) {

			if (CalculationConstants.RPYCHG_ADJMDT.equals(fm.getRecalType())
					|| CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(fm.getRecalType())) {
				schdMethod = repayInstructions.get(risize - 1).getRepaySchdMethod();
			} else {
				for (int i = 0; i < risize; i++) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
					fm.setRecalSchdMethod(schdMethod);

					if (repayInstructions.get(i).getRepayDate().compareTo(recalFromDate) > 0) {
						break;
					}
				}
			}
		}

		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.isBlank(schdMethod)) {
			for (int i = 0; i < risize; i++) {
				if (DateUtil.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
					reqSchdMethod = schdMethod;
				}

				// FIXME: below code is temporary fix, need to set schedule
				// method
				if (StringUtils.isBlank(fm.getRecalSchdMethod())) {
					fm.setRecalSchdMethod(fm.getScheduleMethod());
				}

				if (DateUtil.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) >= 0) {
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

			if (DateUtil.compare(schdDate, evtFromDate) > 0) {
				break;
			}

			if (DateUtil.compare(schdDate, evtFromDate) == 0) {
				// To make sure the flags are TRUE when repayment happens
				curSchd.setPftOnSchDate(true);
				curSchd.setRepayOnSchDate(true);
				if (i > 1) {
					curSchd.setClosingBalance(finSchdDetails.get(i - 1).getClosingBalance().add(curSchd.getDisbAmount())
							.subtract(repayAmount));
				} else {
					curSchd.setClosingBalance(finSchdDetails.get(i).getClosingBalance().add(curSchd.getDisbAmount())
							.subtract(repayAmount));
				}
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
			curSchd.setClosingBalance(finSchdDetails.get(prvIndex).getClosingBalance().add(curSchd.getDisbAmount())
					.subtract(repayAmount));
		}

		finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtToDate, repayAmount, reqSchdMethod);
		fm.setRecalSchdMethod(fm.getScheduleMethod());

		if (finScheduleData.getFinanceMain().getRecalFromDate() == null) {
			// FIXME:Passing evtFromDate as RecalFromDate to resolve 900 error
			finScheduleData.getFinanceMain().setRecalFromDate(evtFromDate);
		}

		if (!fm.isSanBsdSchdle()) {
			finScheduleData = setRecalAttributes(finScheduleData, PROC_CHANGEREPAY, BigDecimal.ZERO, repayAmount);
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		fm.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData afterChangeRepay(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		String recaltype = fm.getRecalType();

		// TODO: PV Will be addresses while working for Flat to converting and
		/*
		 * if (finMain.isPftIntact()) { finScheduleData = calEffectiveRate(finScheduleData,
		 * CalculationConstants.SCH_SPECIFIER_TOTAL, totalDesiredProfit, evtFromDate, finMain.getCalMaturity(), false);
		 * } else { finScheduleData = calSchdProcess(finScheduleData, false, false); }
		 */

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADDTERM)
				|| StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADDRECAL)) {

		}

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

		FinanceMain fm = finScheduleData.getFinanceMain();

		// TODO: Added for Bajaj Demo on 10 APR16
		// finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		fm.setEventToDate(fm.getEventFromDate());
		fm.setRecalFromDate(fm.getEventFromDate());
		fm.setRecalToDate(fm.getEventFromDate());

		Date newScheduleDate = fm.getEventFromDate();

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

			if (newScheduleDate.compareTo(fm.getGrcPeriodEndDate()) > 0) {
				sd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				sd.setSchdMethod(fm.getScheduleMethod());
			} else {
				sd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
				sd.setSchdMethod(fm.getGrcSchdMthd());
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
				fm.setNumberOfTerms(fm.getNumberOfTerms() + 1);
			}

			break;
		}

		// Recalculate Schedule
		// finScheduleData = procReCalSchd(finScheduleData);

		// START PROCESS
		finScheduleData = fetchGraceCurRates(finScheduleData);
		finScheduleData = fetchRepayCurRates(finScheduleData);

		fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		// finScheduleData = procChangeRepay(finScheduleData, BigDecimal.ZERO, finMain.getScheduleMethod());
		finScheduleData = getRpyInstructDetails(finScheduleData);

		/* Grace Schedule calculation */
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);
		finScheduleData = setFinanceTotals(finScheduleData);

		fm.setScheduleMaintained(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procPostpone(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		Date evtFromDate = fm.getEventFromDate();
		Date evtToDate = fm.getEventToDate();
		int adjTerms = 0;
		fm.setCompareToExpected(false);
		fm.setRecalSchdMethod(CalculationConstants.RPYCHG_ADJMDT);
		fm.setCalculateRepay(false);

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		Date schdDate = new Date();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (DateUtil.compare(schdDate, evtFromDate) < 0) {
				continue;
			}

			if (DateUtil.compare(schdDate, evtToDate) > 0) {
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

		fm.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procReAgeH(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		Date evtFromDate = fm.getEventFromDate();
		Date evtToDate = fm.getEventToDate();
		int adjTerms = 0;
		fm.setCompareToExpected(false);
		fm.setRecalSchdMethod(CalculationConstants.RPYCHG_ADJMDT);
		fm.setCalculateRepay(false);

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();

		Date schdDate = new Date();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (DateUtil.compare(schdDate, evtFromDate) < 0) {
				continue;
			}

			if (DateUtil.compare(schdDate, evtToDate) > 0) {
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

		fm.setScheduleMaintained(true);

		finScheduleData = afterChangeRepay(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private FinScheduleData procUnPlanEMIH(FinScheduleData finScheduleData, BigDecimal repayAmount, String schdMethod) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();

		// BigDecimal totalDesiredProfit = finMain.getTotalGrossPft();

		Date evtFromDate = fm.getEventFromDate();
		Date evtToDate = fm.getEventToDate();

		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.isBlank(schdMethod)) {
			for (int i = 0; i < risize; i++) {
				if (DateUtil.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
				}

				// FIXME: below code is temporary fix, need to set schedule
				// method
				if (StringUtils.isBlank(fm.getRecalSchdMethod())) {
					fm.setRecalSchdMethod(fm.getScheduleMethod());
				}

				if (DateUtil.compare(repayInstructions.get(i).getRepayDate(), evtFromDate) >= 0) {
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

			if (DateUtil.compare(schdDate, evtFromDate) > 0) {
				break;
			}

			if (DateUtil.compare(schdDate, evtFromDate) == 0) {
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

			if (DateUtil.compare(schdDate, evtFromDate) < 0) {
				continue;
			}

			if (DateUtil.compare(schdDate, evtToDate) > 0) {
				break;
			}

			if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())
					|| StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
				curSchd.setBpiOrHoliday(FinanceConstants.FLAG_UNPLANNED);
			}
		}

		finScheduleData = setRecalAttributes(finScheduleData, PROC_UNPLANEMIH, BigDecimal.ZERO, repayAmount);
		finScheduleData = setRpyInstructDetails(finScheduleData, evtFromDate, evtToDate, repayAmount, schdMethod);

		if (fm.isStepFinance() && fm.isAlwManualSteps()) {
			if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
					&& CollectionUtils.isNotEmpty(finScheduleData.getStepPolicyDetails())) {
				prepareManualRepayRI(finScheduleData);
			}
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);

		fm.setScheduleMaintained(true);

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

		// Create duplicate fsDATA for calculation purpose. If calculation is successful
		// send back calculated DATA else send original data with errors
		FinScheduleData finScheduleData = orgFinScheduleData.copyEntity();
		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<FinanceDisbursement> finDisbDetails = finScheduleData.getDisbursementDetails();
		finScheduleData.getFinanceMain().setResetOrgBal(false);

		// Original END Balance update for Developer Finance
		Date graceEndDate = fm.getGrcPeriodEndDate();
		String recaltype = fm.getRecalType();
		Date evtFromDate = fm.getEventFromDate();

		fm.setCalculateRepay(false);
		fm.setEqualRepay(false);
		fm.setCompareToExpected(false);
		fm.setCompareExpectedResult(BigDecimal.ZERO);
		fm.setSchdIndex(0);

		boolean isDisbDateFoundInSD = false;

		int disbSeq = 0;
		int disbIndex = 0;
		Date schdDate = fm.getFinStartDate();

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
		if (DateUtil.compare(fm.getFinStartDate(), graceEndDate) == 0) {
			utilizeGrcEndDisb = false;
		}

		// If Disbursement Date is after Grace End Date no adjustment is
		// applicable
		if (DateUtil.compare(evtFromDate, graceEndDate) >= 0) {
			utilizeGrcEndDisb = false;
		}

		int sdSize = finSchdDetails.size();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		if (utilizeGrcEndDisb) {
			for (int i = 0; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
				schdDate = curSchd.getSchDate();

				if (DateUtil.compare(schdDate, graceEndDate) < 0) {
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
			} else if (DateUtil.compare(curSchd.getSchDate(), evtFromDate) == 0) {
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
		if (fm.isDevFinCalReq() && !fm.isStepFinance()) {
			setOrgEndBalances(finScheduleData, newDisbAmount, newFeeAmt);
		} else if (fm.isStepRecalOnProrata()) {
			setOrgEndBalanceOnStep(finScheduleData, newDisbAmount, newFeeAmt);
		}

		if (fm.isManualSchedule()) {
			finScheduleData = calManualSchdProcess(finScheduleData, true, FinServiceEvent.ADDDISB);

			logger.debug(Literal.LEAVING);
			return finScheduleData;
		}

		// SATYA : FLEXI CODE REMOVED HERE

		Date recalToDate = fm.getRecalToDate();

		// If recalculation type is TILL DATE and event to date is >= maturity
		// date then force it to TILLMDT
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLDATE)
				&& DateUtil.compare(recalToDate, fm.getMaturityDate()) >= 0) {
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		}

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_STEPPOS)) {
			finScheduleData = maintainPOSStep(finScheduleData);
			finScheduleData.getFinanceMain().setScheduleMaintained(true);

			logger.debug("Leaving");
			return finScheduleData;
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// SET RECALCULATION ATTRIBUTES
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		sdSize = finSchdDetails.size();
		fm.setCompareToExpected(false);

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

			if ((StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT)
					|| CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(recaltype)) && !fm.isSanBsdSchdle()) {
				fm.setRecalFromDate(fm.getMaturityDate());
				fm.setRecalToDate(fm.getMaturityDate());
			}

			if (DateUtil.compare(fm.getRecalFromDate(), fm.getGrcPeriodEndDate()) <= 0
					&& DateUtil.compare(fm.getRecalToDate(), fm.getGrcPeriodEndDate()) > 0) {

				for (int i = 0; i < finSchdDetails.size(); i++) {
					if (DateUtil.compare(finSchdDetails.get(i).getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
						continue;
					}

					if (!finSchdDetails.get(i).isRepayOnSchDate()) {
						continue;
					}

					fm.setRecalFromDate(finSchdDetails.get(i).getSchDate());
					break;

				}
			}

			if (fm.isSanBsdSchdle() && (StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					|| CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(fm.getRecalType()))) {
				finScheduleData = setRepayForSanctionBasedDisbADJMDT(finScheduleData, newDisbAmount);
			} else {
				finScheduleData = setRecalAttributes(finScheduleData, PROC_ADDDISBURSEMENT, newDisbAmount,
						BigDecimal.ZERO);
			}
		}

		setExpectedEndBal(finScheduleData);
		finScheduleData = calSchdProcess(finScheduleData, false, false);
		fm = finScheduleData.getFinanceMain();
		fm.setScheduleMaintained(true);

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

		// Create duplicate fsDATA for calculation purpose. If calculation is successful
		// send back calculated DATA else send original data with errors
		FinScheduleData finScheduleData = orgFinScheduleData.copyEntity();
		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<FinanceDisbursement> finDisbDetails = finScheduleData.getDisbursementDetails();
		List<FinFeeDetail> listFeeDetails = finScheduleData.getFinFeeDetailList();
		List<VASRecording> listVasRecording = finScheduleData.getVasRecordingList();
		finScheduleData.getFinanceMain().setResetOrgBal(false);

		// Original END Balance update for Developer Finance
		Date graceEndDate = fm.getGrcPeriodEndDate();
		String recaltype = fm.getRecalType();
		Date evtFromDate = fm.getEventFromDate();

		fm.setCalculateRepay(false);
		fm.setEqualRepay(false);
		fm.setCompareToExpected(false);
		fm.setCompareExpectedResult(BigDecimal.ZERO);
		fm.setSchdIndex(0);

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

				if (StringUtils.equals(finFeeDetail.getFinEvent(), AccountingEvent.ADDDBSP) && StringUtils
						.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
					deductDisbFee = deductDisbFee.add(finFeeDetail.getRemainingFee());
				}

				if (StringUtils.equals(finFeeDetail.getFinEvent(), AccountingEvent.ADDDBSP) && StringUtils
						.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
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
		Date schdDate = fm.getFinStartDate();

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

		// validate if this disb date is equal to realization date
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
		if (DateUtil.compare(fm.getFinStartDate(), graceEndDate) == 0) {
			utilizeGrcEndDisb = false;
		}

		// If Disbursement Date is after Grace End Date no adjustment is
		// applicable
		if (DateUtil.compare(evtFromDate, graceEndDate) >= 0) {
			utilizeGrcEndDisb = false;
		}

		int sdSize = finSchdDetails.size();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		if (utilizeGrcEndDisb) {
			for (int i = 0; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
				schdDate = curSchd.getSchDate();

				if (DateUtil.compare(schdDate, graceEndDate) < 0) {
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
			} else if (DateUtil.compare(curSchd.getSchDate(), evtFromDate) == 0) {
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

		Date recalToDate = fm.getRecalToDate();

		// If recalculation type is TILL DATE and event to date is >= maturity
		// date then force it to TILLMDT
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLDATE)
				&& DateUtil.compare(recalToDate, fm.getMaturityDate()) >= 0) {
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		}

		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_STEPPOS)) {
			finScheduleData = maintainPOSStep(finScheduleData);
			finScheduleData.getFinanceMain().setScheduleMaintained(true);

			logger.debug("Leaving");
			return finScheduleData;
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// SET RECALCULATION ATTRIBUTES
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		sdSize = finSchdDetails.size();
		fm.setCompareToExpected(false);

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

			if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJMDT)
					&& CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(recaltype)) {
				fm.setRecalFromDate(fm.getMaturityDate());
				fm.setRecalToDate(fm.getMaturityDate());
			}

			if (DateUtil.compare(fm.getRecalFromDate(), fm.getGrcPeriodEndDate()) <= 0
					&& DateUtil.compare(fm.getRecalToDate(), fm.getGrcPeriodEndDate()) > 0) {

				for (int i = 0; i < finSchdDetails.size(); i++) {
					if (DateUtil.compare(finSchdDetails.get(i).getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
						continue;
					}

					fm.setRecalFromDate(finSchdDetails.get(i).getSchDate());
					break;

				}
			}

			if (fm.isSanBsdSchdle() && !StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
					&& !CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(fm.getRecalType())) {
				finScheduleData = setRepayForSanctionBasedDisbADJMDT(finScheduleData, newDisbAmount);
			} else {
				finScheduleData = setRecalAttributes(finScheduleData, PROC_ADDDISBURSEMENT, newDisbAmount,
						BigDecimal.ZERO);
			}
		}

		finScheduleData = calSchdProcess(finScheduleData, false, false);
		fm = finScheduleData.getFinanceMain();
		fm.setScheduleMaintained(true);

		// Subvention Schedule Details Calculation
		buildSubventionSchedule(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private void setOrgEndBalances(FinScheduleData finScheduleData, BigDecimal newDisbAmount, BigDecimal newFeeAmt) {

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		// Original END Balance update for Developer Finance
		Date graceEndDate = fm.getGrcPeriodEndDate();
		String recaltype = fm.getRecalType();
		Date evtFromDate = fm.getEventFromDate();

		int recalTermsForOrgBal = 0;
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLDATE)
				|| StringUtils.equals(recaltype, CalculationConstants.RPYCHG_TILLMDT)
				|| StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJTERMS)) {

			for (FinanceScheduleDetail schd : finSchdDetails) {
				if (DateUtil.compare(schd.getSchDate(), graceEndDate) > 0
						&& DateUtil.compare(schd.getSchDate(), fm.getRecalFromDate()) >= 0
						&& DateUtil.compare(schd.getSchDate(), fm.getRecalToDate()) <= 0) {

					if (StringUtils.isNotEmpty(schd.getBpiOrHoliday())
							&& (FinanceConstants.FLAG_BPI.equals(schd.getBpiOrHoliday())
									|| FinanceConstants.FLAG_HOLDEMI.equals(schd.getBpiOrHoliday()))) {
						continue;
					}

					if (!schd.isRepayOnSchDate() && !schd.isPftOnSchDate() && schd.isRvwOnSchDate()) {
						continue;
					}

					if (!schd.isRepayOnSchDate()) {
						continue;
					}

					recalTermsForOrgBal = recalTermsForOrgBal + 1;
				}
			}
		}

		// Add Additional Requested Terms For Adjust Term Functionality
		if (StringUtils.equals(recaltype, CalculationConstants.RPYCHG_ADJTERMS)) {
			recalTermsForOrgBal = recalTermsForOrgBal + fm.getAdjTerms();
		}

		BigDecimal balDisbAmount = newDisbAmount.add(newFeeAmt);
		BigDecimal adjOrgAmount = balDisbAmount;
		if (recalTermsForOrgBal > 0) {
			adjOrgAmount = balDisbAmount.divide(new BigDecimal(recalTermsForOrgBal), 0, RoundingMode.HALF_DOWN);
			adjOrgAmount = CalculationUtil.roundAmount(adjOrgAmount, fm.getCalRoundingMode(), fm.getRoundingTarget());
		}

		// Setting New Disbursement changes into Original Ending Balance
		for (FinanceScheduleDetail schd : finSchdDetails) {

			// No Change in Disbursement addition to Original End Balance
			// before selected recalculation
			if (DateUtil.compare(schd.getSchDate(), fm.getRecalFromDate()) < 0
					&& DateUtil.compare(schd.getSchDate(), evtFromDate) >= 0) {
				schd.setOrgEndBal(schd.getOrgEndBal().add(balDisbAmount));
			}

			// If Schedule Date before Grace End date, should not consider
			// on adjustment
			if (DateUtil.compare(schd.getSchDate(), graceEndDate) <= 0) {
				continue;
			}

			// Adjust amounts between recalculation dates
			if (DateUtil.compare(schd.getSchDate(), fm.getRecalFromDate()) >= 0
					&& DateUtil.compare(schd.getSchDate(), fm.getRecalToDate()) <= 0) {

				if (StringUtils.isNotEmpty(schd.getBpiOrHoliday())
						&& (FinanceConstants.FLAG_BPI.equals(schd.getBpiOrHoliday())
								|| FinanceConstants.FLAG_HOLDEMI.equals(schd.getBpiOrHoliday()))) {
					continue;
				}

				if (!schd.isRepayOnSchDate() && !schd.isPftOnSchDate() && schd.isRvwOnSchDate()) {
					continue;
				}

				if (!schd.isRepayOnSchDate()) {
					continue;
				}

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

	/*
	 * ========================================================================= =======================================
	 * Method : procAddTerm Description : ADD TERM Process : Add Term will add another term to the schedule details.
	 * =========================================================================
	 * ========================================
	 */

	private FinScheduleData procAddTerm(FinScheduleData orgFinScheduleData, int noOfTerms, boolean isRepayOnSchd) {
		logger.debug("Entering");

		// Create duplicate fsDATA for calculation purpose. If calculation is successful
		// send back calculated DATA else send original data with errors
		FinScheduleData finScheduleData = orgFinScheduleData.copyEntity();

		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceType ft = finScheduleData.getFinanceType();

		int totalTerms = fm.getNumberOfTerms() + noOfTerms;

		if (totalTerms > ft.getFinMaxTerm()) {
			orgFinScheduleData.setErrorDetail(new ErrorDetail("SCH30",
					"ADD/ADJ TERMS REACHED MAXIMUM TERMS IN CONFIGURATION. NOT ALLOWED TO ADD MORE TERMS.",
					new String[] { " " }));
			return orgFinScheduleData;
		}

		// Set the limits based on system values table
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtil.addYears(fm.getFinStartDate(), maxFinYears);

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

		FinanceMain fm = finScheduleData.getFinanceMain();
		Date evtFromDate = fm.getEventFromDate();
		Date evtToDate = fm.getEventToDate();

		fm.setRecalFromDate(evtFromDate);
		fm.setRecalToDate(evtToDate);

		if (DateUtil.compare(fm.getRecalFromDate(), fm.getGrcPeriodEndDate()) < 0) {
			int size = finScheduleData.getFinanceScheduleDetails().size();
			for (int i = 0; i < size; i++) {
				FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) > 0) {
					fm.setRecalFromDate(curSchd.getSchDate());
					break;
				}
			}

		}

		finScheduleData = calEffectiveRate(finScheduleData, CalculationConstants.SCH_SPECIFIER_SELECT, desiredPftAmount,
				null, null, false);

		// Calculate Effective Rate after Change profit
		// Create duplicate fsDATA for calculation purpose
		FinScheduleData orgFinScheduleData = finScheduleData.copyEntity();
		FinanceMain orgFinMain = orgFinScheduleData.getFinanceMain();

		// isProtectSchdPft = true; TODO check by pradeep, is it needed or not
		// SET START AND DATES FOR EFFECTIVE RATE CALCULATION
		orgFinMain.setEventFromDate(orgFinMain.getFinStartDate());
		orgFinMain.setEventToDate(orgFinMain.getMaturityDate());

		orgFinScheduleData = calEffectiveRate(orgFinScheduleData, CalculationConstants.SCH_SPECIFIER_TOTAL,
				orgFinMain.getTotalGrossPft(), orgFinMain.getFinStartDate(), orgFinMain.getMaturityDate(), true);

		fm.setEffectiveRateOfReturn(orgFinScheduleData.getFinanceScheduleDetails().get(0).getCalculatedRate());

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

		FinanceMain fm = finScheduleData.getFinanceMain();

		// Rate Review in EOD : Manual Schedule Process
		if (fm.isManualSchedule()) {
			boolean rateReviewFlag = finScheduleData.getFinanceMain().isRateChange();

			if (!rateReviewFlag) {
				finScheduleData.getFinanceMain().setRateChange(true);
			}

			finScheduleData = calManualSchdProcess(finScheduleData, false, FinServiceEvent.RATECHG);

			if (!rateReviewFlag) {
				finScheduleData.getFinanceMain().setRateChange(false);
			}

			logger.debug(Literal.LEAVING);
			return finScheduleData;
		}

		if (!StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)
				&& !CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(fm.getRecalType())) {
			if (fm.isStepFinance()) {
				if ((CalculationConstants.SCHMTHD_PRI_PFT.equals(finScheduleData.getFinanceMain().getScheduleMethod())
						|| CalculationConstants.SCHMTHD_PRI
								.equals(finScheduleData.getFinanceMain().getScheduleMethod()))) {
					finScheduleData.getFinanceMain().setDevFinCalReq(true);
				}

				fm.setCalculateRepay(false);
				fm.setScheduleMaintained(true);

				if (!CalculationConstants.SCHMTHD_EQUAL.equals(fm.getScheduleMethod())) {
					for (FinanceScheduleDetail finSch : finScheduleData.getFinanceScheduleDetails()) {
						if (finSch.getSchDate().compareTo(fm.getAppDate()) > 0) {
							if (!finSch.isRepayOnSchDate()) {
								continue;
							}
							fm.setRecalFromDate(finSch.getSchDate());
							break;
						}
					}
					fm.setResetOrgBal(false);
				}

			} else {
				fm.setCalculateRepay(true);
			}

			Date recalFromDate = fm.getRecalFromDate();
			Date recalToDate = fm.getRecalToDate();
			String schdMethod = fm.getScheduleMethod();

			if (DateUtil.compare(fm.getRecalFromDate(), fm.getGrcPeriodEndDate()) <= 0
					&& DateUtil.compare(fm.getRecalToDate(), fm.getGrcPeriodEndDate()) > 0) {

				for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
					if (DateUtil.compare(finScheduleData.getFinanceScheduleDetails().get(i).getSchDate(),
							fm.getGrcPeriodEndDate()) <= 0) {
						continue;
					}
					if (!finScheduleData.getFinanceScheduleDetails().get(i).isRepayOnSchDate()) {
						continue;
					}

					recalFromDate = finScheduleData.getFinanceScheduleDetails().get(i).getSchDate();
					break;

				}
			}

			if (!fm.isStepFinance()) {
				List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();
				for (RepayInstruction ri : repayInstructions) {
					schdMethod = ri.getRepaySchdMethod();
					if (DateUtil.compare(ri.getRepayDate(), fm.getRecalFromDate()) > 0) {
						break;
					}
				}

				finScheduleData = setRpyInstructDetails(finScheduleData, recalFromDate, recalToDate, BigDecimal.ONE,
						schdMethod);
			}
		}

		// finScheduleData = calSchdProcess(finScheduleData, false, false); // In auto rate review process we commented
		// this

		// In auto rate review process we added this
		if (DateUtil.compare(fm.getGrcPeriodEndDate(), fm.getAppDate()) > 0) {
			finScheduleData = procChangeRate(finScheduleData, fm.getGraceBaseRate(), fm.getGraceSpecialRate(),
					fm.getGrcMargin(), fm.getEffectiveRateOfReturn(), true, true);
		} else {
			finScheduleData = procChangeRate(finScheduleData, fm.getRepayBaseRate(), fm.getRepaySpecialRate(),
					fm.getRepayMargin(), fm.getEffectiveRateOfReturn(), true, true);
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		if (StringUtils.isBlank(fm.getGrcSchdMthd())) {
			fm.setGrcSchdMthd(fm.getScheduleMethod());
		}

		// Set Default scheduled date and schedule method first time
		// PSD #138659 Number of Grace Terms should not include the BPI term.
		int i = 0;

		for (FinanceScheduleDetail curSchd : finSchdDetails) {
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0
					&& !StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				fm.setGraceTerms(i);
				i++;
			}
		}

		if (fm.getLastRepayDate() == null) {
			fm.setLastRepayDate(fm.getFinStartDate());
		}

		if (fm.getLastRepayCpzDate() == null) {
			fm.setLastRepayCpzDate(fm.getGrcPeriodEndDate());
		}

		if (fm.getLastRepayPftDate() == null) {
			fm.setLastRepayPftDate(fm.getGrcPeriodEndDate());
		}

		if (fm.getLastRepayRvwDate() == null) {
			fm.setLastRepayRvwDate(fm.getFinStartDate());
		}

		if (!fm.isAllowGrcPftRvw()) {
			fm.setNextGrcPftRvwDate(fm.getGrcPeriodEndDate());
		}

		if (!fm.isAllowRepayRvw()) {
			fm.setNextRepayRvwDate(fm.getMaturityDate());
		}

		if (!fm.isAllowGrcCpz()) {
			fm.setNextGrcCpzDate(fm.getGrcPeriodEndDate());
		}
		if (!fm.isAllowRepayCpz()) {
			fm.setNextRepayCpzDate(fm.getMaturityDate());
		}

		if (fm.isAllowGrcPeriod() && fm.isAllowGrcRepay()) {
			finScheduleData = setRpyInstructDetails(finScheduleData, fm.getNextGrcPftDate(), fm.getMaturityDate(),
					BigDecimal.ZERO, fm.getGrcSchdMthd());
		}

		fm.setRecalSchdMethod(fm.getScheduleMethod());
		finScheduleData = setRpyInstructDetails(finScheduleData, fm.getNextRepayPftDate(), fm.getMaturityDate(),
				BigDecimal.ZERO, fm.getScheduleMethod());

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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		// Get Adjustment Limits parameters from system values table
		BigDecimal lastTermPercent = new BigDecimal(SysParamUtil.getValueAsString("ADJTERM_LASTTERM_PERCENT"));
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");

		// Set the limits based on system values table
		BigDecimal lastTermLimit = BigDecimal.valueOf(0.0);
		Date lastDateLimit = new Date();

		lastDateLimit = DateUtil.addYears(fm.getFinStartDate(), maxFinYears);

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

		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finSchdDetails.get(iAddTermsAfter);

		Date curSchdDate = curSchd.getSchDate();

		// get next Repayment Date
		Date nextSchdDate = DateUtil
				.getDate(
						DateUtil.format(
								FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, curSchdDate,
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
			lastSchd.setOrgEndBal(lastSchd.getOrgEndBal().add(fm.getAdjOrgBal()));

			fm.setNumberOfTerms(fm.getNumberOfTerms() + 1);
			fm.setCalTerms(fm.getCalTerms() + 1);
			fm.setMaturityDate(nextSchdDate);
			fm.setCalMaturity(nextSchdDate);
		}

		// Set Profit dates between current schedule and next repayment

		finScheduleData = setOtherSchdDates(finScheduleData, fm.getRepayPftFrq(), curSchdDate,
				CalculationConstants.SCHDFLAG_PFT);

		// Set Profit review dates between current schedule and next repayment
		if (fm.isAllowRepayRvw()) {
			finScheduleData = setOtherSchdDates(finScheduleData, fm.getRepayRvwFrq(), curSchdDate,
					CalculationConstants.SCHDFLAG_RVW);
		}

		// Set Capitalization dates between current schedule and next repayment
		if (fm.isAllowRepayCpz()) {
			finScheduleData = setOtherSchdDates(finScheduleData, fm.getRepayCpzFrq(), curSchdDate,
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

	private FinScheduleData resetNewSchdDetail(FinScheduleData schdData, Date nextSchdDate, int scheduleFlag) {
		logger.debug("Entering");

		// Set Next Repayment Date
		FinanceMain fm = schdData.getFinanceMain();
		FinanceScheduleDetail sd = new FinanceScheduleDetail();

		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();
		int lastIndex = fsdList.size() - 1;
		FinanceScheduleDetail lastSchd = fsdList.get(lastIndex);

		int lastInstNumber = lastSchd.getInstNumber();
		for (int i = lastIndex; i > 0; i--) {
			if (fsdList.get(i).getInstNumber() > 0) {
				lastInstNumber = fsdList.get(i).getInstNumber();
				break;
			}
		}

		sd.setFinID(fm.getFinID());
		sd.setFinReference(fm.getFinReference());
		sd.setSchDate(nextSchdDate);
		sd.setDefSchdDate(nextSchdDate);

		// Grace Period Flags Setting
		if (DateUtil.compare(nextSchdDate, fm.getGrcPeriodEndDate()) <= 0) {

			// Set Profit On date based on frequency
			if (FrequencyUtil.isFrqDate(fm.getGrcPftFrq(), nextSchdDate)) {
				sd.setPftOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				sd.setPftOnSchDate(false);
			}

			// Review Date
			if (fm.isAllowGrcPftRvw() && FrequencyUtil.isFrqDate(fm.getGrcPftRvwFrq(), nextSchdDate)) {
				sd.setRvwOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				if (DateUtil.compare(nextSchdDate, fm.getGrcPeriodEndDate()) == 0 && fm.isFinIsRateRvwAtGrcEnd()) {
					sd.setRvwOnSchDate(true);
				} else {
					sd.setRvwOnSchDate(false);
				}
			}

			// Set Capitalize On date based on frequency
			if (fm.isAllowGrcCpz() && FrequencyUtil.isFrqDate(fm.getGrcCpzFrq(), nextSchdDate)) {
				sd.setCpzOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				if (DateUtil.compare(nextSchdDate, fm.getGrcPeriodEndDate()) == 0 && fm.isCpzAtGraceEnd()) {
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
			if (FrequencyUtil.isFrqDate(fm.getRepayPftFrq(), nextSchdDate)) {
				if (fm.isFinRepayPftOnFrq()) {
					sd.setPftOnSchDate(true);
					sd.setFrqDate(true);
				} else {
					sd.setPftOnSchDate(false);
				}

			} else {
				sd.setPftOnSchDate(false);
			}

			// Review Date
			if (fm.isAllowRepayRvw() && FrequencyUtil.isFrqDate(fm.getRepayRvwFrq(), nextSchdDate)) {
				sd.setRvwOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				sd.setRvwOnSchDate(false);
			}

			// Set Capitalize On date based on frequency
			if (FrequencyUtil.isFrqDate(fm.getRepayCpzFrq(), nextSchdDate)) {
				sd.setCpzOnSchDate(true);
				sd.setFrqDate(true);
			} else {
				sd.setCpzOnSchDate(false);
			}

			// Repay on Schedule Date
			if (scheduleFlag == CalculationConstants.SCHDFLAG_RPY) {
				sd.setRepayOnSchDate(true);
				if (FrequencyUtil.isFrqDate(fm.getRepayFrq(), nextSchdDate)) {
					sd.setFrqDate(true);
				}
			} else {
				if (FrequencyUtil.isFrqDate(fm.getRepayFrq(), nextSchdDate)) {
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
		sd.setTDSApplicable(TDSCalculator.isTDSApplicable(fm));

		int iRepay = schdData.getRepayInstructions().size();
		String schdMethod = schdData.getFinanceMain().getScheduleMethod();
		if (iRepay > 0) {
			schdMethod = schdData.getRepayInstructions().get(iRepay - 1).getRepaySchdMethod();
		}

		if (StringUtils.isBlank(schdData.getFinanceMain().getRecalSchdMethod())) {
			schdData.getFinanceMain().setRecalSchdMethod(schdMethod);
		}

		sd.setSchdMethod(schdMethod);
		sd.setPftDaysBasis(lastSchd.getPftDaysBasis());
		sd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);

		if (sd.isRepayOnSchDate()) {
			sd.setInstNumber(lastInstNumber + 1);
		}

		schdData.getFinanceScheduleDetails().add(sd);
		schdData.setFinanceScheduleDetails(sortSchdDetails(schdData.getFinanceScheduleDetails()));

		logger.debug("Leaving");
		return schdData;
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

			Date nextSchdDate = DateUtil.getDate(DateUtil
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
					if (DateUtil.compare(nextSchdDate, curSchd.getSchDate()) > 0 && DateUtil.compare(nextSchdDate,
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

	private FinScheduleData setRpyInstructDetails(FinScheduleData schdData, Date fromDate, Date toDate,
			BigDecimal repayAmount, String schdMethod) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		if (StringUtils.equals(CalculationConstants.SCHMTHD_POS_INT, fm.getScheduleMethod()) || fm.isManualSchedule()) {
			logger.debug(Literal.LEAVING);
			return schdData;
		}

		List<FinanceScheduleDetail> finSchdDetails = schdData.getFinanceScheduleDetails();

		BigDecimal nextInstructAmount = BigDecimal.ZERO;
		Date nextInstructDate = null;
		String nextInstructSchdMethod = null;

		boolean isAddNewInstruction = true;
		int instructIndex = -1;

		// Find next date for instruction
		if (DateUtil.compare(toDate, fm.getMaturityDate()) >= 0) {
			nextInstructDate = fm.getMaturityDate();
		} else {
			int sdSize = finSchdDetails.size();
			FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

			for (int i = 0; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
				// ### 01-08-2018 - PSD Ticket ID : 125445, 125588, Freezing
				// period - End
				if (DateUtil.compare(curSchd.getSchDate(), toDate) > 0
						&& (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())
						&& DateUtil.compare(curSchd.getSchDate(), fm.getMaturityDate()) != 0) {
					if (CalculationConstants.SCHMTHD_PRI.equals(curSchd.getSchdMethod())
							&& !curSchd.isRepayOnSchDate()) {
						continue;
					}
					nextInstructDate = curSchd.getSchDate();
					nextInstructSchdMethod = curSchd.getSchdMethod();
					break;
				}
			}
			// Next instruction amount and schedule method
			sortRepayInstructions(schdData.getRepayInstructions());
			if (nextInstructDate != null) {
				instructIndex = fetchRpyInstruction(schdData, nextInstructDate);
			}

			if (instructIndex >= 0) {
				nextInstructAmount = schdData.getRepayInstructions().get(instructIndex).getRepayAmount();
				nextInstructSchdMethod = schdData.getRepayInstructions().get(instructIndex).getRepaySchdMethod();
			}
		}

		RepayInstruction curInstruction = new RepayInstruction();

		// Remove any instructions between fromdate and todate
		for (int i = 0; i < schdData.getRepayInstructions().size(); i++) {
			curInstruction = schdData.getRepayInstructions().get(i);

			if ((DateUtil.compare(curInstruction.getRepayDate(), fromDate) >= 0
					&& DateUtil.compare(curInstruction.getRepayDate(), toDate) <= 0)) {
				schdData.getRepayInstructions().remove(i);
				i = i - 1;
			}

			if (DateUtil.compare(curInstruction.getRepayDate(), nextInstructDate) == 0) {
				isAddNewInstruction = false;
			}
		}

		schdData.setRepayInstructions(sortRepayInstructions(schdData.getRepayInstructions()));

		// Add repay instructions on from date
		RepayInstruction ri = new RepayInstruction();
		ri.setRepayDate(fromDate);
		ri.setRepayAmount(repayAmount);
		ri.setRepaySchdMethod(schdMethod);
		ri.setFinID(finID);
		ri.setFinReference(finReference);

		schdData.getRepayInstructions().add(ri);

		// Add (reset) repay instruction after todate
		if (DateUtil.compare(toDate, fm.getMaturityDate()) >= 0 || !isAddNewInstruction) {
			schdData.setRepayInstructions(sortRepayInstructions(schdData.getRepayInstructions()));
			return schdData;
		}

		if (DateUtil.compare(nextInstructDate, fromDate) > 0) {
			ri = new RepayInstruction();
			ri.setFinID(finID);
			ri.setFinReference(finReference);
			ri.setRepayDate(nextInstructDate);
			ri.setRepayAmount(nextInstructAmount);
			ri.setRepaySchdMethod(nextInstructSchdMethod);
			schdData.getRepayInstructions().add(ri);
		}
		schdData.setRepayInstructions(sortRepayInstructions(schdData.getRepayInstructions()));

		logger.debug(Literal.LEAVING);
		return schdData;
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

		FinanceMain fm = finScheduleData.getFinanceMain();

		if (fm.getRecalIdx() < 0) {
			setRecalIndex(finScheduleData);
		}

		Date fromDate = fm.getFinStartDate();
		Date toDate = fm.getMaturityDate();
		String fromSchdMethod = null;
		String toSchdMethod = null;

		fm.setIndexStart(0);

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

		if (DateUtil.compare(toDate, fm.getMaturityDate()) <= 0) {
			toDate = fm.getMaturityDate();
			setRpyChanges(finScheduleData, fromDate, toDate, instructAmount, fromSchdMethod);
		}

		if (fm.isAdjustClosingBal()) {
			riSize = finScheduleData.getRepayInstructions().size();
			finScheduleData.setRepayInstructions(sortRepayInstructions(finScheduleData.getRepayInstructions()));
			for (int j = 0; j < riSize; j++) {
				RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(j);
				if (DateUtil.compare(curInstruction.getRepayDate(), fm.getGrcPeriodEndDate()) > 0) {
					if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())) {
						fm.setAdvanceEMI(
								curInstruction.getRepayAmount().multiply(BigDecimal.valueOf(fm.getAdvTerms())));
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		int indexStart = fm.getIndexStart();
		boolean isManualAmtStep = false;

		if (fm.isStepFinance() && fm.isAlwManualSteps() && fm.isGrcStps()) {
			if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {
				isManualAmtStep = true;
			}
		}

		String orgSchdMethod = schdMethod;
		BigDecimal bpiBalance = BigDecimal.ZERO;
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		Date firstRepayDate = null;
		if (fm.isBpiResetReq() && StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)
				&& StringUtils.equals(fm.getBpiTreatment(), FinanceConstants.BPI_SCHD_FIRSTEMI)) {
			for (int i = 1; i < sdSize; i++) {
				curSchd = finSchdDetails.get(i);
				if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
					bpiBalance = curSchd.getProfitCalc();
				}

				if ((curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()) && curSchd.isFrqDate()
						&& StringUtils.isEmpty(curSchd.getBpiOrHoliday())) {
					firstRepayDate = curSchd.getSchDate();
					break;
				}
			}
		}

		for (int i = indexStart; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			Date curSchdDate = curSchd.getSchDate();

			if (i < fm.getRecalIdx()) {
				continue;
			}

			// Added for setting Schedule method in case of Different
			// frequencies for PFT,CPZ & RVW
			if (DateUtil.compare(curSchdDate, fromDate) < 0) {
				if (StringUtils.isEmpty(curSchd.getSchdMethod())) {
					if (fm.isAllowGrcPeriod() && DateUtil.compare(curSchdDate, fm.getGrcPeriodEndDate()) <= 0) {
						curSchd.setSchdMethod(fm.getGrcSchdMthd());
					} else {
						curSchd.setSchdMethod(fm.getScheduleMethod());
					}
				}
			}

			if (DateUtil.compare(curSchdDate, fromDate) >= 0 && (DateUtil.compare(curSchdDate, toDate) < 0)) {

				if (FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())) {
					schdMethod = CalculationConstants.SCHMTHD_PFT;
				} else {
					schdMethod = orgSchdMethod;
				}

				curSchd.setSchdMethod(schdMethod);

				boolean isFreezeSchd = false;

				if (curSchd.getPresentmentId() != 0 && !fm.isManualSchedule()
						&& !StringUtils.equals(fm.getProcMethod(), FinServiceEvent.RECEIPT)) {
					isFreezeSchd = true;
				}

				if ((curSchd.isRepayOnSchDate() || isManualAmtStep) && !isFreezeSchd
						&& !(FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday()))) {
					if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
						if (firstRepayDate != null && DateUtil.compare(firstRepayDate, curSchdDate) == 0) {// bpi
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
							|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI)
							|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI_PFTC)) {
						curSchd.setPrincipalSchd(instructAmount);
					} else if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCAP)) {
						curSchd.setRepayAmount(instructAmount);
					}
				} else if (curSchd.isPftOnSchDate()
						&& StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCAP)) {
					curSchd.setRepayAmount(instructAmount);
				}

			} else if (DateUtil.compare(curSchd.getSchDate(), toDate) >= 0) {

				indexStart = i;
				break;
			}
		}

		fm.setIndexStart(indexStart);

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

		FinanceMain fm = finScheduleData.getFinanceMain();

		// FIXME: PV: 13MAY17: It is kept on the assumption reqMaturity fields
		// in not used any where else
		if (fm.isNewRecord() || StringUtils.equals(fm.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			fm.setReqMaturity(fm.getCalMaturity());
		}

		// rebuild the schedule for FLEXI Loans
		if (fm.isAlwFlexi()) {
			rebuildHybridFlexiSchd(finScheduleData);
		}

		// rebuild the schedule for OD Drop Line Loans
		if (OverdraftConstants.DROPING_METHOD_CONSTANT.equals(fm.getDroppingMethod())
				|| OverdraftConstants.DROPING_METHOD_VARIABLE.equals(fm.getDroppingMethod())) {
			if (!FinServiceEvent.EARLYSETTLE.equals(fm.getReceiptPurpose())) {
				rebuildDroplineSchd(finScheduleData);
			}
		}

		FeeScheduleCalculator.feeSchdBuild(finScheduleData);

		List<FinanceScheduleDetail> finScheduleDetails = finScheduleData.getFinanceScheduleDetails();
		boolean isFirstAdjSet = false;
		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		fm.setTotalGraceCpz(BigDecimal.ZERO);
		fm.setTotalGracePft(BigDecimal.ZERO);
		fm.setTotalGrossGrcPft(BigDecimal.ZERO);
		fm.setTotalCpz(BigDecimal.ZERO);
		fm.setTotalProfit(BigDecimal.ZERO);
		fm.setTotalGrossPft(BigDecimal.ZERO);
		fm.setTotalRepayAmt(BigDecimal.ZERO);
		fm.setSchdIndex(0);
		fm.setAdjOrgBal(BigDecimal.ZERO);
		fm.setRemBalForAdj(BigDecimal.ZERO);

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		Date schdDate = new Date();
		int instNumber = 0;
		// PSD#166759
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
		 * int recalIdx = fm.getRecalIdx(); if (recalIdx < 0) { finScheduleData = setRecalIndex(finScheduleData);
		 * recalIdx = fm.getRecalIdx(); }
		 */

		for (int i = 0; i < sdSize; i++) {
			curSchd = finScheduleDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (TDSCalculator.isTDSApplicable(fm) && tdsPerc.compareTo(BigDecimal.ZERO) != 0) {

				boolean taxOnSysPerc = true;
				if (ltdApplicable) {
					BigDecimal tdsAmount = BigDecimal.ZERO;

					if (ltd == null || DateUtil.compare(schdDate, ltd.getEndDate()) > 0) {
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

			String bpiOrHoliday = curSchd.getBpiOrHoliday();
			if (FinanceConstants.FLAG_BPI.equals(bpiOrHoliday) && fm.isModifyBpi()) {
				fm.setBpiAmount(curSchd.getProfitSchd().subtract(curSchd.getTDSAmount()));
			}

			if (DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) <= 0) {
				fm.setTotalGraceCpz(fm.getTotalGraceCpz().add(curSchd.getCpzAmount()));
				fm.setTotalGracePft(fm.getTotalGracePft().add(curSchd.getProfitSchd()));
			}

			fm.setTotalCpz(fm.getTotalCpz().add(curSchd.getCpzAmount()));
			fm.setTotalProfit(fm.getTotalProfit().add(curSchd.getProfitSchd()));
			fm.setTotalGrossPft(fm.getTotalGrossPft().add(curSchd.getProfitSchd()));
			fm.setTotalRepayAmt(fm.getTotalRepayAmt().add(curSchd.getRepayAmount()));

			if (curSchd.isRepayOnSchDate()) {
				if (!isFirstAdjSet) {
					fm.setFirstRepay(curSchd.getRepayAmount());
					isFirstAdjSet = true;
				}

				fm.setLastRepay(curSchd.getRepayAmount());
			}

			if (((curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate())
					&& !FinanceConstants.FLAG_BPI.equals(bpiOrHoliday)
					&& !FinanceConstants.FLAG_ADDTNL_BPI.equals(bpiOrHoliday) && i != 0)
					|| FinanceConstants.FLAG_STRTPRDHLD.equals(bpiOrHoliday)) {

				// PSD Ticket : 133179, Partial Settlement Cases
				// Removed the condition because for part payment installment no is setting for step loans.
				// FIXME Lakshmi.N
				if (curSchd.isFrqDate()) {
					instNumber = instNumber + 1;
					curSchd.setInstNumber(instNumber);
				} else {
					curSchd.setInstNumber(0);
				}
				// PSD#166759 Reset the CalTerms
				if (curSchd.isFrqDate() && DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) > 0) {
					calTerms = calTerms + 1;
				}

				// Reset Grace Terms -- PSD Ticket :136185
				if (curSchd.isFrqDate() && DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) <= 0) {
					graceTerms = graceTerms + 1;
				} // End PSD#166759
			} else {
				curSchd.setInstNumber(0);
			}

			if (i == (sdSize - 1)) {
				curSchd.setSchdMethod(finScheduleDetails.get(i - 1).getSchdMethod());
			}

			if (DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) < 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
			} else if (DateUtil.compare(schdDate, fm.getGrcPeriodEndDate()) == 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
			} else if (DateUtil.compare(schdDate, fm.getMaturityDate()) < 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
			} else if (DateUtil.compare(schdDate, fm.getMaturityDate()) == 0) {
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
			}

			// Reset Original Balance for Developer Finance
			if ((fm.isDevFinCalReq() || fm.isSanBsdSchdle() || fm.isStepFinance()) && fm.isResetOrgBal()) {
				if (DateUtil.compare(schdDate, fm.getRecalFromDate()) >= 0
						|| DateUtil.compare(fm.getEventFromDate(), fm.getGrcPeriodEndDate()) < 0) {
					curSchd.setOrgEndBal(curSchd.getClosingBalance());
				} else if (fm.isStepFinance() && DateUtil.compare(schdDate, fm.getAppDate()) >= 0) {
					curSchd.setOrgEndBal(curSchd.getClosingBalance());
				}
			}
		}
		// PSD#169262:Issue while decreasing ROI when by Adjusting terms.
		// FIXME if Required: below condition added while decreasing the ROI for adjust terms.
		// add rate change tenor showing wrong when rate is decreased.
		if (!FinServiceEvent.RATECHG.equals(fm.getProcMethod())) {
			// PSD#166759
			fm.setCalTerms(calTerms);
		}
		// Ticket id:PSD Ticket :136185,resetting grace terms
		fm.setGraceTerms(graceTerms);// End PSD#166759

		fm.setTotalGrossGrcPft(fm.getTotalGraceCpz().add(fm.getTotalGracePft()));
		fm.setTotalGrossPft(fm.getTotalProfit().add(fm.getTotalCpz()));

		boolean isXIRRCalc = SysParamUtil.isAllowed(SMTParameterConstants.CALC_EFFRATE_ON_XIRR);
		IRRCalculator.calculateXIRRAndIRR(finScheduleData, null, isXIRRCalc);

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
			if (DateUtil.compare(taxDeduction.getStartDate(), schDate) > 0) {
				break;
			}

			// If Current LTD End date is more than Schedule Date
			if (DateUtil.compare(taxDeduction.getEndDate(), schDate) < 0) {
				continue;
			}
			// if End Date Greater than Start Date
			if (DateUtil.compare(taxDeduction.getEndDate(), taxDeduction.getStartDate()) <= 0) {
				continue;
			}

			// If LTD percentage less than Zero
			if (taxDeduction.getPercentage().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			if (DateUtil.compare(taxDeduction.getStartDate(), schDate) <= 0) {
				if (DateUtil.compare(taxDeduction.getEndDate(), schDate) >= 0) {
					return taxDeduction;
				}
			}
		}

		return null;
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
		FinanceMain fm = finScheduleData.getFinanceMain();
		fm.setCpzPosIntact(cpzPosIntact);

		// START PROCESS
		finScheduleData = fetchRatesHistory(finScheduleData);

		// If Errors Exists in calculation, return back
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			return finScheduleData;
		}

		finScheduleData = fetchGraceCurRates(finScheduleData);
		finScheduleData = fetchRepayCurRates(finScheduleData);

		if (fm.isStepFinance() && fm.isAlwManualSteps() && (isFirstRun || fm.isStepRecalOnProrata()
				|| CalculationConstants.RPYCHG_ADDRECAL.equals(fm.getRecalType()))) {
			if (PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
					&& CollectionUtils.isNotEmpty(finScheduleData.getStepPolicyDetails())) {
				prepareManualRepayRI(finScheduleData, isFirstRun);
				// fm.setEqualRepay(false);
			}
		}

		/*
		 * Doing part payment for step loan if effective schedule method selected as Adjust to stepping EMI then last
		 * repay step amount need to be auto calculate.
		 */
		if (fm.isStepFinance() && fm.isAlwManualSteps()
				&& CalculationConstants.RPYCHG_ADJEMI_STEP.equals(fm.getEffSchdMethod())
				&& PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())
				&& CollectionUtils.isNotEmpty(finScheduleData.getStepPolicyDetails())) {
			autoCalcLastRepayStepAmt(finScheduleData, fm);
		}

		if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage()) && fm.getAdvTerms() > 0
				&& !PROC_GETCALSCHD.equals(finScheduleData.getModuleDefiner())) {
			fm.setAdjustClosingBal(false);
		}

		finScheduleData = getRpyInstructDetails(finScheduleData);

		if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
				&& fm.getAdvTerms() > 0) {
			fm.setAdjustClosingBal(true);
			int idx = finScheduleData.getFinanceScheduleDetails().size() - fm.getAdvTerms() - 1;
			fm.setRecalToDate(finScheduleData.getFinanceScheduleDetails().get(idx).getSchDate());
		}

		/* Grace Schedule calculation */
		finScheduleData = graceSchdCal(finScheduleData);

		if (isFirstRun) {
			finScheduleData = prepareFirstSchdCal(finScheduleData);

			if (fm.isStepFinance() && PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
				finScheduleData = adjustBPISchd(finScheduleData);
				finScheduleData = setFinanceTotals(finScheduleData);
				logger.debug("Leaving");
				return finScheduleData;
			}

			finScheduleData = getRpyInstructDetails(finScheduleData);

			/* Grace Schedule calculation */
			if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
					&& finScheduleData.getFinanceMain().getAdvTerms() > 0) {
				finScheduleData.getFinanceMain().setAdjustClosingBal(true);
				finScheduleData = graceSchdCal(finScheduleData);
			}
		} else {
			if (fm.isStepFinance() && PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
				finScheduleData = calStepSchd(finScheduleData);
				finScheduleData = adjustBPISchd(finScheduleData);
				finScheduleData = setFinanceTotals(finScheduleData);
				logger.debug(Literal.LEAVING);

				return finScheduleData;
			}
		}

		if (fm.isRecalSteps()) {
			finScheduleData = calStepSchd(finScheduleData);
		}

		finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

		if (fm.isEqualRepay() && fm.isCalculateRepay()
				&& !StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
				&& !StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PFTCPZ)
				&& !StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
				&& !FinServiceEvent.PRINH.equals(finScheduleData.getModuleDefiner())) {

			if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
					&& fm.getAdvTerms() > 0 && isFirstRun) {
				fm.setAdjustClosingBal(true);
			}

			finScheduleData = calEqualInst(finScheduleData);
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		if (fm.isSkipRateReset()) {
			logger.debug("Leaving - Skip Base/Special Rate Change");
			return finScheduleData;
		}

		if (!fm.isRateChange()) {
			logger.debug("Leaving - Not Rate Change Method");
			return finScheduleData;
		}

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		Date dateAllowedChange = fm.getLastRepayRvwDate();

		if (DateUtil.compare(dateAllowedChange, fm.getGrcPeriodEndDate()) >= 0) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		// FIND ALLOWED RATE CHANGE DATE
		String rvwRateApplFor = fm.getRvwRateApplFor();
		if (StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)) {
			dateAllowedChange = findAllowedChangeDate(finScheduleData, rvwRateApplFor, dateAllowedChange);
		}

		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = finSchdDetails.size();
		BigDecimal prvRate = BigDecimal.ZERO;

		// Set Rates from Allowed Date and Grace Period End Date
		for (int i = 0; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			fm.setSchdIndex(i);

			if (DateUtil.compare(curSchd.getSchDate(), dateAllowedChange) < 0) {
				prvRate = curSchd.getCalculatedRate();
				continue;
			}

			// SATYA TODO: 08DEC18 : Issue Facing with Multiple Base Rates.
			/*
			 * if (DateUtility.compare(curSchd.getSchDate(), fm.getEventFromDate()) < 0) { prvRate =
			 * curSchd.getCalculatedRate(); continue; }
			 * 
			 * if (DateUtility.compare(curSchd.getSchDate(), fm.getEventToDate()) > 0) { prvRate =
			 * curSchd.getCalculatedRate(); break; }
			 */

			if (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) >= 0) {
				prvRate = curSchd.getCalculatedRate();
				break;
			}

			// Rate Change Required
			boolean rateChangeReq = false;
			if (fm.isAllowSubvention() && finScheduleData.getSubventionDetail() != null) {
				if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_PARTIAL,
						finScheduleData.getSubventionDetail().getType())) {
					rateChangeReq = true;
				} else if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL,
						finScheduleData.getSubventionDetail().getType())
						&& DateUtil.compare(curSchd.getSchDate(),
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
								&& DateUtil.compare(curSchd.getSchDate(), dateAllowedChange) == 0)) {
					curSchd.setCalculatedRate(RateUtil.ratesFromLoadedData(finScheduleData, i));
				} else {
					curSchd.setCalculatedRate(finSchdDetails.get(i - 1).getCalculatedRate());
				}

			}

			// Subvention Rate Reset
			if (fm.isAllowSubvention() && finScheduleData.getSubventionDetail() != null && DateUtil
					.compare(curSchd.getSchDate(), finScheduleData.getSubventionDetail().getEndDate()) < 0) {

				String subventionType = finScheduleData.getSubventionDetail().getType();

				if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL, subventionType)) {
					if (DateUtil.compare(curSchd.getSchDate(),
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
		FinanceMain fm = finScheduleData.getFinanceMain();

		if (fm.isSkipRateReset()) {
			logger.debug("Leaving - Skip Base/Special Rate Change");
			return finScheduleData;
		}

		if (!fm.isRateChange()) {
			logger.debug("Leaving - Not Rate Change Method");
			return finScheduleData;
		}

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		Date dateAllowedChange = fm.getLastRepayRvwDate();
		int fixedRateTenor = fm.getFixedRateTenor();
		/*
		 * Date fixedTenorEndDate = DateUtility.addMonths(fm.getGrcPeriodEndDate(), fixedRateTenor > 0 ? fixedRateTenor
		 * : 0);
		 */
		Date fixedTenorEndDate = fm.getFinStartDate();

		if (fixedRateTenor > 0) {
			fixedTenorEndDate = fm.getNextRepayDate();
			for (int i = 0; i < (fixedRateTenor - 1); i++) {
				fixedTenorEndDate = DateUtil
						.getDate(DateUtil.format(
								FrequencyUtil.getNextDate(fm.getRepayFrq(), 1, fixedTenorEndDate,
										HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
								PennantConstants.dateFormat));
			}
		}

		// PROFIT LAST REVIEW IS ON OR AFTER MATURITY THEN NOT ALLOWED THEN DO
		// NOT SET
		if (DateUtil.compare(dateAllowedChange, fm.getMaturityDate()) >= 0) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		// FIND ALLOWED RATE CHANGE DATE
		String rvwRateApplFor = fm.getRvwRateApplFor();
		if (StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)) {
			dateAllowedChange = findAllowedChangeDate(finScheduleData, rvwRateApplFor, dateAllowedChange);
		}

		int schdIndex = fm.getSchdIndex();
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
			 * if (curSchd.getSchDate().compareTo(fm.getEventFromDate()) < 0) { continue; }
			 */
			/*
			 * if (curSchd.getSchDate().compareTo(fm.getEventToDate()) > 0) { break; }
			 */
			// Fetch current rates from DB
			if (DateUtil.compare(curSchd.getSchDate(), (fixedTenorEndDate)) < 0
					&& DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) >= 0) {
				curSchd.setCalculatedRate(fm.getFixedTenorRate());
				fixedRateTenor = fixedRateTenor > 0 ? fixedRateTenor - 1 : fixedRateTenor;
			} else {
				if (StringUtils.isNotEmpty(curSchd.getBaseRate())) {
					if (curSchd.isRvwOnSchDate() || i == 0
							|| (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) == 0
									&& fm.isFinIsRateRvwAtGrcEnd())
							|| (StringUtils.trimToEmpty(fm.getRvwRateApplFor())
									.equals(CalculationConstants.RATEREVIEW_RVWUPR)
									&& DateUtil.compare(curSchd.getSchDate(), dateAllowedChange) == 0)) {
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

		if (StringUtils.equals(finScheduleData.getFinanceMain().getProcMethod(), FinServiceEvent.RECEIPT)) {
			logger.debug("Leaving");
			return finScheduleData.getFinanceMain().getMaturityDate();
		}

		if (!StringUtils.trimToEmpty(rvwRateApplFor).equals(CalculationConstants.RATEREVIEW_RVWUPR)) {
			return dateAllowedChange;
		}

		int size = finScheduleData.getFinanceScheduleDetails().size();
		for (int i = 0; i < size; i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (DateUtil.compare(curSchd.getSchDate(), dateAllowedChange) <= 0) {
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();

		fm.setPftForSelectedPeriod(BigDecimal.ZERO);
		fm.setSchdIndex(0);

		int sdSize = schdDetails.size();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();

		int recalIdx = fm.getRecalIdx();
		if (recalIdx < 0) {
			finScheduleData = setRecalIndex(finScheduleData);
			recalIdx = fm.getRecalIdx();
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

			if (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) == 0) {
				fm.setSchdIndex(i);
				break;
			}

			fm.setSchdIndex(i);
		}

		logger.debug("Leaving");
		return finScheduleData;

	}

	private FinScheduleData setRecalIndex(FinScheduleData fsd) {
		FinanceMain fm = fsd.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = fsd.getFinanceScheduleDetails();
		Date evtFromDate = fm.getEventFromDate();

		if (evtFromDate == null) {
			evtFromDate = fm.getFinStartDate();
		}

		if (evtFromDate.compareTo(fm.getFinStartDate()) < 0) {
			evtFromDate = fm.getFinStartDate();
		}

		boolean isPftCpzFromReset = false;
		if (CalculationConstants.SCHMTHD_PFTCPZ.equals(fm.getScheduleMethod())) {
			isPftCpzFromReset = true;
		}

		fm.setPftCpzFromReset(BigDecimal.ZERO);
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = schdDetails.size();

		boolean cpzPOSIntact = SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT);

		for (int i = 0; i < sdSize; i++) {
			curSchd = schdDetails.get(i);
			if (curSchd.getSchDate().compareTo(evtFromDate) < 0) {

				if (cpzPOSIntact) {
					fm.setPftCpzFromReset(BigDecimal.ZERO);
				} else {
					if (isPftCpzFromReset && curSchd.isCpzOnSchDate() && curSchd.isRepayOnSchDate()) {
						fm.setPftCpzFromReset(BigDecimal.ZERO);
					} else {
						fm.setPftCpzFromReset(fm.getPftCpzFromReset().add(curSchd.getCpzAmount()));
					}
				}

				continue;
			}

			fm.setRecalIdx(i);
			break;
		}

		if (fm.getRecalIdx() < 0) {
			fm.setRecalIdx(sdSize - 1);
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
		FinanceMain fm = finScheduleData.getFinanceMain();
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

		if (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) == 0) {
			if (fm.isFinIsRateRvwAtGrcEnd()) {
				curSchd.setRvwOnSchDate(true);
			}
		}

		if (AdvanceType.hasAdvEMI(fm.getAdvType())) {
			if (fm.isAdjustClosingBal()) {
				curSchd.setClosingBalance(curSchd.getClosingBalance().subtract(fm.getAdvanceEMI()));
				fm.setAdjustClosingBal(false);
			}
		}

		if (fm.isAllowGrcPeriod()) {
			curSchd.setPftDaysBasis(fm.getGrcProfitDaysBasis());
		} else {
			curSchd.setPftDaysBasis(fm.getProfitDaysBasis());
		}

		// NOT Discount Deal
		if (!CalculationConstants.RATE_BASIS_D.equals(fm.getRepayRateBasis())) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		// Discount Deal Total profit Calculation in FLAT

		BigDecimal grcTotalPft = BigDecimal.ZERO;
		BigDecimal calIntFraction = BigDecimal.ZERO;

		if (fm.isAllowGrcPeriod()) {
			grcTotalPft = CalculationUtil.calInterest(fm.getFinStartDate(), fm.getGrcPeriodEndDate(),
					curSchd.getClosingBalance(), fm.getRepayRateBasis(), fm.getGrcPftRate());

			calIntFraction = grcTotalPft.subtract(round(grcTotalPft));
			grcTotalPft = round(grcTotalPft);
		}

		BigDecimal rpyTotalPft = CalculationUtil.calInterest(fm.getGrcPeriodEndDate(),
				DateUtil.addDays(fm.getMaturityDate(), 1), curSchd.getClosingBalance(), fm.getRepayRateBasis(),
				fm.getRepayProfitRate());

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
		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(iCur);
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(iCur - 1);

		// TODO: TO AVOID RECALCULATION OF SCHEDULES COMPLETED
		if (fm.getRecalIdx() < 0) {
			finScheduleData = setRecalIndex(finScheduleData);
		}

		if (iCur < fm.getRecalIdx()) {
			return finScheduleData;
		}

		boolean cpzPOSIntact = fm.isCpzPosIntact();

		if (!fm.getGrcRateBasis().equals(CalculationConstants.RATE_BASIS_F)) {
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

		curSchd.setNoOfDays(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchd.getSchDate()));
		curSchd.setDayFactor(
				CalculationUtil.getInterestDays(prvSchd.getSchDate(), curSchd.getSchDate(), curSchd.getPftDaysBasis()));

		// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);

		BigDecimal prvPftFraction = prvSchd.getProfitFraction();
		if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_NO_ADJ)
				|| (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)
						&& DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) != 0)) {

			prvPftFraction = BigDecimal.ZERO;
		}

		/* Calculate interest and set interest payment details */
		BigDecimal calint = CalculationUtil.calInterest(prvSchd.getSchDate(), curSchd.getSchDate(),
				curSchd.getBalanceForPftCal(), curSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

		calint = calint.add(prvPftFraction);

		BigDecimal calIntRounded = BigDecimal.ZERO;
		if (calint.compareTo(BigDecimal.ZERO) > 0) {
			calIntRounded = CalculationUtil.roundAmount(calint, fm.getCalRoundingMode(), fm.getRoundingTarget());
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
			if (!fm.isAlwFlexi()) {
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
				calPftPriRpy(finScheduleData, iCur, (iCur - 1), fm.getEventFromDate(), cpzPOSIntact);
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
		if (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) == 0 && fm.isCpzAtGraceEnd()) {
			curSchd.setCpzOnSchDate(true);
			if (ImplementationConstants.NON_FRQ_CAPITALISATION) {
				if (!FrequencyUtil.isFrqDate(fm.getGrcCpzFrq(), fm.getGrcPeriodEndDate())) {
					curSchd.setCpzOnSchDate(fm.isCpzAtGraceEnd());
				}
			}
		}

		// In the Process of Rate Change, for Future Review Period method, schedule should not modify for Past Due
		// schedules
		// Because of Installment dues already passed for the same
		boolean cpzResetReq = true;
		if (StringUtils.equals(FinServiceEvent.RATECHG, fm.getProcMethod())) {
			if (StringUtils.equals(CalculationConstants.RATEREVIEW_RVWFUR, fm.getRvwRateApplFor())) {
				if (DateUtil.compare(curSchd.getSchDate(), fm.getAppDate()) <= 0) {
					cpzResetReq = false;
				}
			}
		}

		setCpzAmounts(prvSchd, curSchd, cpzPOSIntact, cpzResetReq);

		curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, fm.getProfitDaysBasis(), cpzPOSIntact));

		if (DateUtil.compare(curSchd.getSchDate(), fm.getEventFromDate()) > 0
				&& DateUtil.compare(curSchd.getSchDate(), fm.getEventToDate()) <= 0) {
			fm.setPftForSelectedPeriod(fm.getPftForSelectedPeriod().add(curSchd.getProfitCalc()));
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		fm.setPftCpzFromReset(BigDecimal.ZERO);
		String module = finScheduleData.getModuleDefiner();

		boolean isRepayComplete = false;
		boolean isSanctionBasedSchd = fm.isSanBsdSchdle();

		fm.setCalTerms(0);

		BigDecimal calIntFraction = BigDecimal.ZERO;
		BigDecimal calInt = BigDecimal.valueOf(0.0);
		Date derivedMDT = fm.getMaturityDate();
		int advEMITerms = fm.getAdvTerms();

		String repayRateBasis = fm.getRepayRateBasis();

		// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);

		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();
		fm.setNewMaturityIndex(schdDetails.size() - 1);
		int schdDetailsSize = schdDetails.size();

		// FIND LAST REPAYMENT SCHEDULE DATE
		int schdIndex = fm.getSchdIndex();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		FinanceScheduleDetail prvSchd = new FinanceScheduleDetail();

		// Start Period Holiday Functionality
		boolean alwStrtPrdHday = fm.isAlwStrtPrdHday();
		int strtPrdHdays = fm.getStrtPrdHdays();
		int usedStrtPrdHdays = 0;

		if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())) {
			derivedMDT = schdDetails.get(schdDetails.size() - advEMITerms - 1).getSchDate();
			fm.setNewMaturityIndex(schdDetails.size() - advEMITerms - 1);
		} else {
			fm.setNewMaturityIndex(schdDetails.size() - 1);
			derivedMDT = schdDetails.get(schdDetails.size() - 1).getSchDate();
		}
		// common issue 8
		if (FinServiceEvent.EARLYSETTLE.equals(fm.getReceiptPurpose())) {
			derivedMDT = fm.getEventFromDate();
		}

		int recalIdx = fm.getRecalIdx();
		if (recalIdx < 0) {
			finScheduleData = setRecalIndex(finScheduleData);
			recalIdx = fm.getRecalIdx();
		} else {
			calIntFraction = prvSchd.getProfitFraction();
		}

		boolean cpzPOSIntact = fm.isCpzPosIntact();

		for (int i = schdIndex + 1; i < schdDetailsSize; i++) {

			curSchd = schdDetails.get(i);

			if (i < recalIdx) {
				calIntFraction = schdDetails.get(i).getProfitFraction();
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

			curSchd.setNoOfDays(DateUtil.getDaysBetween(curSchDate, prvSchDate));
			curSchd.setDayFactor(CalculationUtil.getInterestDays(prvSchDate, curSchDate, curSchd.getPftDaysBasis()));

			// Calculate Interest
			if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {

				if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_NO_ADJ)
						|| (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)
								&& DateUtil.compare(curSchd.getSchDate(), fm.getMaturityDate()) != 0)) {

					calIntFraction = BigDecimal.ZERO;
				}

				calInt = CalculationUtil.calInterest(prvSchDate, curSchDate, curSchd.getBalanceForPftCal(),
						curSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

				calInt = calInt.add(calIntFraction);
				BigDecimal calIntRounded = BigDecimal.ZERO;
				if (calInt.compareTo(BigDecimal.ZERO) > 0) {
					calIntRounded = CalculationUtil.roundAmount(calInt, fm.getCalRoundingMode(),
							fm.getRoundingTarget());
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
			if (DateUtil.compare(curSchDate, derivedMDT) == 0) {
				finScheduleData = procMDTRecord(finScheduleData, i, isRepayComplete, cpzPOSIntact);
				// common issue when early settlement doing at the time of same BPI schedule Date
				if (FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())) {
					curSchd.setBpiOrHoliday(null);
				}
				isRepayComplete = true;
			}

			Date recalToDate = fm.getRecalToDate();
			boolean flag = !PROC_CHANGEREPAY.equals(module) && !PROC_ADDDISBURSEMENT.equals(module)
					&& !PROC_CHANGERATE.equals(module) && !PROC_RECALSCHD.equals(module);
			if (StringUtils.isBlank(curSchd.getBpiOrHoliday()) && flag
					&& CalculationConstants.SCHMTHD_EQUAL.equals(curSchd.getSchdMethod())
					&& DateUtil.compare(recalToDate, derivedMDT) != 0
					&& DateUtil.compare(curSchDate, recalToDate) == 0) {
				procTDTRecord(finScheduleData, i, cpzPOSIntact);
			}

			if (!isRepayComplete) {

				if (curSchd.isRepayOnSchDate()) {
					curSchd = calPftPriRpy(finScheduleData, i, (i - 1), fm.getEventFromDate(), cpzPOSIntact);
					fm.setNewMaturityIndex(i);

					if (repayRateBasis.equals(CalculationConstants.RATE_BASIS_D)) {
						if (curSchd.getRepayAmount().compareTo(prvClosingBalance.add(curSchd.getDisbAmount())) >= 0) {
							curSchd.setProfitSchd(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())
									.add(curSchd.getProfitCalc()));
							curSchd.setRepayAmount(prvClosingBalance.add(curSchd.getDisbAmount()));
							curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
							isRepayComplete = true;
						}
					} else {
						if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
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

							if (!fm.isManualSchedule()) {
								isRepayComplete = true;
							} else if (curSchDate.compareTo(derivedMDT) == 0) {
								isRepayComplete = true;
							}

							if (isRepayComplete) {

								curSchd.setPrincipalSchd(prvClosingBalance.add(curSchd.getDisbAmount()));
								curSchd.setProfitSchd(prvSchd.getProfitBalance().subtract(prvSchd.getCpzAmount())
										.add(curSchd.getProfitCalc()));
								curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));

								if (StringUtils.equals(roundAdjMth, CalculationConstants.PFTFRACTION_ADJ_LAST_INST)
										&& !curSchd.isFrqDate()) {
									calInt = CalculationUtil.calInterest(prvSchDate, curSchDate,
											curSchd.getBalanceForPftCal(), curSchd.getPftDaysBasis(),
											prvSchd.getCalculatedRate());

									// Common issue 16
									if (calInt.add(prvSchd.getProfitFraction()).compareTo(BigDecimal.ZERO) <= 0) {
										calInt = BigDecimal.ZERO;
									} else {
										calInt = calInt.add(prvSchd.getProfitFraction());
									}

									if (calInt.compareTo(BigDecimal.ZERO) > 0) {
										calInt = CalculationUtil.roundAmount(calInt, fm.getCalRoundingMode(),
												fm.getRoundingTarget());
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
									curSchd.setProfitSchd(
											curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));

									if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) < 0) {
										curSchd.setProfitSchd(BigDecimal.ZERO);
									}

									if (!cpzPOSIntact) {
										curSchd.setProfitCalc(curSchd.getProfitSchd()
												.subtract(prvSchd.getProfitBalance()).add(prvSchd.getCpzAmount()));
									} else {
										curSchd.setProfitCalc(
												curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance()));
									}
								}

								if (!isSanctionBasedSchd) {
									isRepayComplete = true;
								}

							}

						}
					}

					/* Count Repay schedules only */
					fm.setCalTerms(fm.getCalTerms() + 1);
					fm.setCalMaturity(curSchDate);

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
						if (fm.isBpiResetReq()) {
							if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
									&& !StringUtils.equals(fm.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE)
									&& !StringUtils.equals(fm.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)) {
								curSchd.setProfitSchd(curSchd.getSchdPftPaid());
							} else {
								curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, false));
							}
						} else {
							curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, false));
						}
						curSchd.setRepayAmount(curSchd.getProfitSchd());
						curSchd.setPrincipalSchd(BigDecimal.ZERO);
					}
				}

				// Resetting Capitalize flag
				if (!curSchd.isCpzOnSchDate() && StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())) {
					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
						curSchd.setCpzOnSchDate(fm.isPlanEMICpz());
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
						curSchd.setCpzOnSchDate(fm.isReAgeCpz());
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
						curSchd.setCpzOnSchDate(fm.isUnPlanEMICpz());
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_STRTPRDHLD)) {

						if (StringUtils.equals(fm.getStrtprdCpzMethod(), CalculationConstants.STRTPRDCPZ_CPZONFRQ)) {
							curSchd.setCpzOnSchDate(true);
						} else if (StringUtils.equals(fm.getStrtprdCpzMethod(),
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
						if (StringUtils.equals(fm.getStrtprdCpzMethod(), CalculationConstants.STRTPRDCPZ_NOCPZ)) {
							curSchd.setCpzOnSchDate(false);
						} else if (StringUtils.equals(fm.getStrtprdCpzMethod(),
								CalculationConstants.STRTPRDCPZ_CPZATEND)) {
							if (usedStrtPrdHdays == strtPrdHdays) {
								curSchd.setCpzOnSchDate(true);
							} else {
								curSchd.setCpzOnSchDate(false);
							}
						}

					} else if (StringUtils.isEmpty(curSchd.getBpiOrHoliday())) {
						if (!fm.isAllowRepayCpz() || !FrequencyUtil.isFrqDate(fm.getRepayCpzFrq(), curSchDate)) {
							curSchd.setCpzOnSchDate(false);
						}
					}
				}

				curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, cpzPOSIntact));

				// In the Process of Rate Change, for Future Review Period method, schedule should not modify for Past
				// Due schedules
				// Because of Installment dues already passed for the same
				boolean cpzResetReq = true;
				if (StringUtils.equals(FinServiceEvent.RATECHG, fm.getProcMethod())) {
					if (StringUtils.equals(CalculationConstants.RATEREVIEW_RVWFUR, fm.getRvwRateApplFor())) {
						if (DateUtil.compare(curSchd.getSchDate(), fm.getAppDate()) <= 0) {
							cpzResetReq = false;
						}
					}
				}

				setCpzAmounts(prvSchd, curSchd, cpzPOSIntact, cpzResetReq);
				if (!cpzPOSIntact) {
					fm.setPftCpzFromReset(fm.getPftCpzFromReset().add(curSchd.getCpzAmount()));
				}

				curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, repayRateBasis, cpzPOSIntact));

				// 08-JAN-2018 : When Rounding Effect creates new Record with
				// Negative values after Closing Balance using Profit Balance
				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					curSchd.setClosingBalance(BigDecimal.ZERO);

					int roundingTarget = fm.getRoundingTarget();
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

				if (DateUtil.compare(curSchDate, fm.getEventFromDate()) > 0
						&& DateUtil.compare(curSchDate, fm.getEventToDate()) <= 0) {
					fm.setPftForSelectedPeriod(fm.getPftForSelectedPeriod().add(curSchd.getProfitCalc()));
				}

				if (StringUtils.isBlank(curSchd.getBpiOrHoliday()) && !PROC_CHANGEREPAY.equals(module)
						&& CalculationConstants.SCHMTHD_EQUAL.equals(curSchd.getSchdMethod())
						&& DateUtil.compare(recalToDate, derivedMDT) != 0
						&& DateUtil.compare(curSchDate, recalToDate) == 0) {
					adjustTDTSchedule(curSchd, fm, prvSchd);
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(iCur);
		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails().get(iPrv);
		BigDecimal schdInterest = BigDecimal.ZERO;

		// FIX: PV: To address Postponements, Reage, Unplanned Holidays,
		// Holidays without additional instructions
		String bpiOrHoliday = curSchd.getBpiOrHoliday();
		if (!FinServiceEvent.RECEIPT.equals(fm.getProcMethod())
				|| DateUtil.compare(evtFromDate, curSchd.getSchDate()) != 0) {
			if (FinanceConstants.FLAG_HOLIDAY.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_POSTPONE.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_REAGE.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_STRTPRDHLD.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_MORTEMIHOLIDAY.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_UNPLANNED.equals(bpiOrHoliday)
					|| FinanceConstants.FLAG_RESTRUCTURE.equals(bpiOrHoliday)) {
				curSchd.setProfitSchd(curSchd.getSchdPftPaid());
				curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
				curSchd.setRepayAmount(curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid()));

				// store first repay amount
				if (fm.getCalTerms() == 1) {
					fm.setFirstRepay(curSchd.getRepayAmount());
				}

				fm.setLastRepay(curSchd.getRepayAmount());
				return curSchd;

			}
		}

		boolean protectPftSchd = fm.isProtectSchdPft();

		// FIXME: PV 23MAY17
		// NEW CODE ADDED HERE TO AVOID CHANGES RELATED TO COMPLETED SCHEDULES.
		// LIKE IF DUE SENT FOR PRESENTMENT THEN SCHEDULE SHOULD NOT CHANGE
		// MEANS BALANCE WILL BE ADJUSTED TO NEXT SCHEDULES
		/*
		 * if (curSchd.getSchDate().compareTo(fm.getRecalFromDate()) < 0) { return curSchd; }
		 */

		// SIVA : For Presentment Detail Schedule should be recalculate on
		// Schedule Maintenance
		if (FinanceConstants.FLAG_RESTRUCTURE_PRIH.equals(curSchd.getBpiOrHoliday())) {
			calProfitSchedule(cpzPOSIntact, fm, curSchd, prvSchd, protectPftSchd);
		} else if (curSchd.getPresentmentId() > 0 && !fm.isManualSchedule()
				&& !FinServiceEvent.RECEIPT.equals(fm.getProcMethod())) {

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
		if (StringUtils.equals(FinServiceEvent.RATECHG, fm.getProcMethod())) {
			if (StringUtils.equals(CalculationConstants.RATEREVIEW_RVWFUR, fm.getRvwRateApplFor())) {
				if (DateUtil.compare(curSchd.getSchDate(), fm.getAppDate()) <= 0) {
					protectPftSchd = true;
				}

				// On maturity Date case, default total Will adjust
				if (DateUtil.compare(curSchd.getSchDate(), fm.getMaturityDate()) == 0) {
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
			if (fm.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
					&& (StringUtils.equals(fm.getBpiTreatment(), FinanceConstants.BPI_DISBURSMENT)
							|| StringUtils.equals(fm.getBpiTreatment(), FinanceConstants.BPI_SCHEDULE))) {// Bpi
																											// changes
				schdInterest = curSchd.getProfitCalc();

				// FIXME: PV 02JUN18 WHY BELOW CODE IS REQUIRED?. Commented for
				// testing
				/*
				 * if (DateUtility.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) { if
				 * (!fm.isAllowGrcCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
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
				BigDecimal pftToSchd = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, false);

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

			if (fm.isDevFinCalReq()) {
				getNewPrincipal(fm, curSchd, prvSchd);
			}

			curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

			// CALCULATED PROFIT ONLY: Applicable for GRACE & REPAYMENT period
		} else if (CalculationConstants.SCHMTHD_PFT.equals(curSchd.getSchdMethod())) {
			calProfitSchedule(cpzPOSIntact, fm, curSchd, prvSchd, protectPftSchd);
		} else if (CalculationConstants.SCHMTHD_PFTCPZ.equals(curSchd.getSchdMethod())) {

			BigDecimal cpzDue = BigDecimal.ZERO;
			if (curSchd.isRepayOnSchDate() && curSchd.isCpzOnSchDate()) {
				if (!fm.isCpzPosIntact()) {
					cpzDue = fm.getPftCpzFromReset();
					fm.setPftCpzFromReset(BigDecimal.ZERO);
				}
			}

			if (curSchd.getPresentmentId() <= 0) {
				curSchd.setPrincipalSchd(cpzDue);
			}

			if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) < 0) {
				curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			}

			if (!protectPftSchd) {
				schdInterest = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, false);

				// FIXME: PV 02JUN18 WHY BELOW CODE IS REQUIRED?. Commented for
				// testing
				/*
				 * if (DateUtility.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0) { if
				 * (!finMain.isAllowGrcCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } } else { if
				 * (!finMain.isAllowRepayCpz()) { schdInterest = CalculationUtil.roundAmount(schdInterest,
				 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } }
				 */

				if (fm.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
						&& fm.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
					schdInterest = BigDecimal.ZERO;
				}

				curSchd.setProfitSchd(schdInterest);
				if (fm.isDevFinCalReq()) {
					getNewPrincipal(fm, curSchd, prvSchd);
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
			if (curSchd.getPresentmentId() > 0 && !fm.isManualSchedule()) {

				if (!protectPftSchd) {
					curSchd.setProfitSchd(
							prvSchd.getProfitBalance().add(curSchd.getProfitCalc()).subtract(prvSchd.getCpzAmount()));
				}

				if (StringUtils.isNotBlank(fm.getReceiptPurpose())
						&& (StringUtils.equals(fm.getReceiptPurpose(), FinServiceEvent.EARLYRPY)
								|| StringUtils.equals(fm.getReceiptPurpose(), FinServiceEvent.EARLYSETTLE))) {

					if (curSchd.getSchDate().compareTo(fm.getAppDate()) <= 0) {
						curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
					} else {
						curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
					}
				} else {
					curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
				}

			} else if (!protectPftSchd) {
				schdInterest = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, false);

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
				if (fm.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
						&& fm.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
					schdInterest = BigDecimal.ZERO;
				}

				curSchd.setProfitSchd(schdInterest);
				if (fm.isDevFinCalReq()) {
					getNewPrincipal(fm, curSchd, prvSchd);
				}
				curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			} else {
				if (fm.isDevFinCalReq()) {
					getNewPrincipal(fm, curSchd, prvSchd);
				}
				curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
			}

			// NOPAYMENT IN GRACE SCHEDULES AND COMPLETE PAYMENT AT GRACE END
			// DATE: Applicable for GRACE period Only
		} else if (CalculationConstants.SCHMTHD_GRCENDPAY.equals(curSchd.getSchdMethod())) {

			if (fm.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
					&& (fm.getBpiTreatment().equals(FinanceConstants.BPI_DISBURSMENT)
							|| fm.getBpiTreatment().equals(FinanceConstants.BPI_SCHEDULE)
							|| fm.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI))) {
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
				if (fm.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {

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

			if (curSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) == 0) {
				curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, false));
			}
		} else if (CalculationConstants.SCHMTHD_PFTCAP.equals(curSchd.getSchdMethod())) {
			if (!protectPftSchd) {
				schdInterest = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, false);

				if (fm.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
						&& fm.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
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
		} else if (CalculationConstants.SCHMTHD_PRI_PFTC.equals(curSchd.getSchdMethod())) {
			if (!protectPftSchd) {
				schdInterest = calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, false);

				if (fm.isAlwBPI() && FinanceConstants.FLAG_BPI.equals(curSchd.getBpiOrHoliday())
						&& FinanceConstants.BPI_SCHD_FIRSTEMI.equals(fm.getBpiTreatment())) {
					schdInterest = BigDecimal.ZERO;
				}

				if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) == 0
						|| schdInterest.compareTo(curSchd.getProfitSchd()) < 0) {
					curSchd.setProfitSchd(schdInterest);
				}

				if (FinServiceEvent.RECEIPT.equals(fm.getProcMethod())) {
					if (curSchd.getPrincipalSchd().compareTo(prvSchd.getClosingBalance()) == 0) {
						curSchd.setProfitSchd(schdInterest);
					}
				}
			}
		}

		curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

		// store first repay amount
		if (fm.getCalTerms() == 1) {
			fm.setFirstRepay(curSchd.getRepayAmount());
		}

		// store last repay amount
		fm.setLastRepay(curSchd.getRepayAmount());

		return curSchd;

	}

	private void calProfitSchedule(boolean cpzPOSIntact, FinanceMain finMain, FinanceScheduleDetail curSchd,
			FinanceScheduleDetail prvSchd, boolean protectPftSchd) {
		BigDecimal schdInterest;
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
			 * finMain.getCalRoundingMode(), finMain.getRoundingTarget()); } } else { if (!finMain.isAllowRepayCpz()) {
			 * schdInterest = CalculationUtil.roundAmount(schdInterest, finMain.getCalRoundingMode(),
			 * finMain.getRoundingTarget()); } }
			 */

			if (finMain.isAlwBPI() && StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)
					&& finMain.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
				schdInterest = BigDecimal.ZERO;
			}

			curSchd.setProfitSchd(schdInterest);

			if (finMain.isDevFinCalReq()) {
				getNewPrincipal(finMain, curSchd, prvSchd);
			} else {
				curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
			}
		}
		// curSchd.setPrincipalSchd(curSchd.getSchdPriPaid());
		curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));

		// PRINCIPAL + CALCULATED PROFIT: Applicable for GRACE & REPAYMENT
		// period
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

		if (CalculationConstants.RATE_BASIS_D.equals(repayRateBasis)) {
			closingBal = closingBal.subtract(curSchd.getRepayAmount());
		} else {
			closingBal = closingBal.subtract(curSchd.getPrincipalSchd());
		}

		if (!cpzPOSIntact || FinanceConstants.FLAG_MORTEMIHOLIDAY.equals(curSchd.getBpiOrHoliday())) {
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
			boolean cpzPOSIntact, FinanceMain fm, boolean isMDTDate) {
		BigDecimal newProfit = BigDecimal.ZERO;

		boolean protectPftSchd = fm.isProtectSchdPft();
		if (StringUtils.equals(FinServiceEvent.RATECHG, fm.getProcMethod())) {
			if (StringUtils.equals(CalculationConstants.RATEREVIEW_RVWFUR, fm.getRvwRateApplFor())) {
				if (DateUtil.compare(curSchd.getSchDate(), fm.getAppDate()) <= 0) {
					protectPftSchd = true;
				}

				// On maturity Date case, default total Will adjust
				if (DateUtil.compare(curSchd.getSchDate(), fm.getMaturityDate()) == 0) {
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
		} else if (curSchd.getPresentmentId() > 0 && !fm.isManualSchedule()) {

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

		FinanceMain fm = finScheduleData.getFinanceMain();

		FinScheduleData orgFinScheduleData = finScheduleData.copyEntity();

		fm.setCompareToExpected(false);
		fm.setCompareExpectedResult(BigDecimal.ZERO);
		fm.setRecalFromDate(null);
		fm.setRecalToDate(null);

		fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);

		// Set the limits based on system values table
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtil.addYears(fm.getFinStartDate(), maxFinYears);

		Date newSchdDate = DateUtil.getDate(DateUtil.format(subSchStartDate, PennantConstants.dateFormat));

		for (int i = 0; i < noOfTerms; i++) {
			finScheduleData = addSubScheduleTerm(finScheduleData, lastDateLimit, true, newSchdDate, frqNewSchd);

			if (finScheduleData.getErrorDetails().size() > 0) {
				return orgFinScheduleData;
			}

			newSchdDate = DateUtil.getDate(DateUtil
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(addTermAfterIndex);

		Date curSchdDate = newSchdDate;

		if (DateUtil.compare(newSchdDate, lastDateLimit) > 0) {
			// Through Error
			finScheduleData.setErrorDetail(
					new ErrorDetail("SCH30", "ADD/ADJ TERMS REACHED MAXIMUM FINANCE YEARS", new String[] { " " }));
			return finScheduleData;
		}

		// Set Profit On date based on frequency
		curSchd.setPftOnSchDate(FrequencyUtil.isFrqDate(fm.getRepayPftFrq(), curSchdDate));

		// Set Profit Review On date based on frequency
		curSchd.setRvwOnSchDate(FrequencyUtil.isFrqDate(fm.getRepayRvwFrq(), curSchdDate));

		// Set Capitalize On date based on frequency
		curSchd.setCpzOnSchDate(FrequencyUtil.isFrqDate(fm.getRepayCpzFrq(), curSchdDate));

		// Reset New Schedule Record if record not found
		finScheduleData = resetNewSchdDetail(finScheduleData, newSchdDate, CalculationConstants.SCHDFLAG_RPY);

		fm.setNumberOfTerms(fm.getNumberOfTerms() + 1);
		fm.setCalTerms(fm.getCalTerms() + 1);
		fm.setMaturityDate(newSchdDate);
		fm.setCalMaturity(newSchdDate);

		// Set Profit dates between current schedule and next repayment

		finScheduleData = setOtherSchdDates(finScheduleData, frqNewSchd, curSchdDate,
				CalculationConstants.SCHDFLAG_PFT);

		// Set Profit review dates between current schedule and next repayment
		if (fm.isAllowRepayRvw()) {
			finScheduleData = setOtherSchdDates(finScheduleData, frqNewSchd, curSchdDate,
					CalculationConstants.SCHDFLAG_RVW);
		}

		// Set Capitalization dates between current schedule and next repayment
		if (fm.isAllowRepayCpz()) {
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
		FinanceMain fm = finScheduleData.getFinanceMain();
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
				returnCalProfit = fm.getTotalGrossGrcPft();
			} else if (CalculationConstants.SCH_SPECIFIER_REPAY.equals(scheduleType)) {
				returnCalProfit = fm.getTotalGrossPft().subtract(fm.getTotalGrossGrcPft());
			} else if (CalculationConstants.SCH_SPECIFIER_TOTAL.equals(scheduleType)) {
				returnCalProfit = fm.getTotalGrossPft();
			} else if (CalculationConstants.SCH_SPECIFIER_SELECT.equals(scheduleType)) {
				returnCalProfit = fm.getPftForSelectedPeriod();
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
			fm.setEventFromDate(effcFromDate);
		}

		if (effcToDate != null) {
			fm.setEventToDate(effcToDate);
		}

		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);
		finScheduleData = setFinanceTotals(finScheduleData);

		if (CalculationConstants.SCH_SPECIFIER_GRACE.equals(scheduleType)) {
			returnCalProfit = fm.getTotalGrossGrcPft();

			if (totalDesiredProfit.compareTo(returnCalProfit) >= 0) {
				lowAssumptionRate = fm.getGrcPftRate();
				highAssumptionRate = lowAssumptionRate.multiply(new BigDecimal(10));
			} else {
				lowAssumptionRate = BigDecimal.ZERO;
				highAssumptionRate = fm.getGrcPftRate();
			}

		} else if (CalculationConstants.SCH_SPECIFIER_REPAY.equals(scheduleType)) {
			returnCalProfit = fm.getTotalGrossPft().subtract(fm.getTotalGrossGrcPft());

			if (totalDesiredProfit.compareTo(returnCalProfit) >= 0) {
				lowAssumptionRate = fm.getRepayProfitRate();
				highAssumptionRate = lowAssumptionRate.multiply(new BigDecimal(10));
			} else {
				lowAssumptionRate = BigDecimal.ZERO;
				highAssumptionRate = fm.getRepayProfitRate();
			}

		} else if (CalculationConstants.SCH_SPECIFIER_TOTAL.equals(scheduleType)) {
			returnCalProfit = fm.getTotalGrossPft();
			lowAssumptionRate = BigDecimal.ZERO;
			highAssumptionRate = new BigDecimal(1000);

		} else if (CalculationConstants.SCH_SPECIFIER_SELECT.equals(scheduleType)) {
			returnCalProfit = fm.getPftForSelectedPeriod();
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
				returnCalProfit = fm.getTotalGrossGrcPft();
			} else if (CalculationConstants.SCH_SPECIFIER_REPAY.equals(scheduleType)) {
				returnCalProfit = fm.getTotalGrossPft().subtract(fm.getTotalGrossGrcPft());
			} else if (CalculationConstants.SCH_SPECIFIER_TOTAL.equals(scheduleType)) {
				returnCalProfit = fm.getTotalGrossPft();
			} else if (CalculationConstants.SCH_SPECIFIER_SELECT.equals(scheduleType)) {
				returnCalProfit = fm.getPftForSelectedPeriod();
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
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
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
					return DateUtil.compare(detail1.getRepayDate(), detail2.getRepayDate());
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
					return DateUtil.compare(detail1.getBREffDate(), detail2.getBREffDate());
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
					return DateUtil.compare(detail1.getSREffDate(), detail2.getSREffDate());
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
					return DateUtil.compare(odSchd1.getDroplineDate(), odSchd2.getDroplineDate());
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
					return DateUtil.compare(odSchd1.getStartDate(), odSchd2.getStartDate());
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
	private BigDecimal approxPMT(FinanceMain fm, BigDecimal intRate, int terms, BigDecimal presentValue,
			BigDecimal futureValue, int type) {

		if (intRate.compareTo(BigDecimal.ZERO) == 0) {
			BigDecimal pmtValue = BigDecimal.ZERO;
			if (fm.isCalculateRepay()) {
				pmtValue = presentValue.divide(new BigDecimal(terms), 0, RoundingMode.HALF_DOWN);
			}
			return pmtValue;
		}

		String idb = fm.getProfitDaysBasis();
		String calFrq = StringUtils.mid(fm.getRepayFrq(), 0, 1);
		BigDecimal periods = BigDecimal.ZERO;
		BigDecimal days365 = new BigDecimal(365);
		BigDecimal days360 = new BigDecimal(360);

		// Interest Rate Per Day
		intRate = intRate.divide(BigDecimal.valueOf(36000), 13, RoundingMode.HALF_DOWN);

		if (!fm.getRepayPftFrq().equals(fm.getRepayFrq())) {
			if (fm.isFinRepayPftOnFrq()) {
				calFrq = StringUtils.mid(fm.getRepayPftFrq(), 0, 1);
			}
		}

		if (FrequencyCodeTypes.FRQ_MONTHLY.equals(calFrq)) {
			periods = new BigDecimal(12);
		} else if (FrequencyCodeTypes.FRQ_QUARTERLY.equals(calFrq)) {
			periods = new BigDecimal(4);
		} else if (FrequencyCodeTypes.FRQ_HALF_YEARLY.equals(calFrq)) {
			periods = new BigDecimal(2);
		} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(calFrq)) {
			periods = new BigDecimal(1);
		} else if (FrequencyCodeTypes.FRQ_BIWEEKLY.equals(calFrq)
				|| FrequencyCodeTypes.FRQ_FORTNIGHTLY.equals(calFrq)) {
			periods = new BigDecimal(26);
		} else if (FrequencyCodeTypes.FRQ_15DAYS.equals(calFrq)) {
			periods = new BigDecimal(24);
		} else if (FrequencyCodeTypes.FRQ_BIMONTHLY.equals(calFrq)) {
			periods = new BigDecimal(6);
		} else if (FrequencyCodeTypes.FRQ_WEEKLY.equals(calFrq)) {
			periods = new BigDecimal(52);
		} else {
			periods = new BigDecimal(365);
		}

		// Interest Rate Per Period
		// PMT calculation Changes 19-06-2019
		if (fm.isEqualRepay()
				&& (CalculationConstants.IDB_ACT_ISDA.equals(idb) || CalculationConstants.IDB_ACT_365FIXED.equals(idb)
						|| CalculationConstants.IDB_ACT_365LEAPS.equals(idb)
						|| CalculationConstants.IDB_ACT_365LEAP.equals(idb))) {
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
		pmt = CalculationUtil.roundAmount(pmt, fm.getCalRoundingMode(), fm.getRoundingTarget());

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
			bpiDate = DateUtil.getDate(
					DateUtil.format(termSchList.get(termSchList.size() - 1).getTime(), PennantConstants.dateFormat));
		} else {
			bpiDate = DateUtil
					.getDate(
							DateUtil.format(
									FrequencyUtil.getNextDate(frqBPI, 1, fm.getFinStartDate(),
											HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
									PennantConstants.dateFormat));
		}

		if (DateUtil.compare(bpiDate, firstSchdDate) >= 0) {
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

			if (DateUtil.compare(bpiDate, fm.getGrcPeriodEndDate()) > 0) {
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

		sd.setFinID(fm.getFinID());
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
		sd.setTDSApplicable(TDSCalculator.isTDSApplicable(fm));

		if (DateUtil.compare(bpiDate, fm.getGrcPeriodEndDate()) > 0) {
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		BigDecimal zeroAmount = new BigDecimal(0);

		if (!fm.isAlwBPI()) {
			logger.debug("Leaving - Not BPI");
			return finScheduleData;
		}

		if (!fm.getBpiTreatment().equals(FinanceConstants.BPI_SCHD_FIRSTEMI)) {
			logger.debug("Leaving - Not Add to First Inst");
			return finScheduleData;
		}

		if (StringUtils.equals(fm.getReceiptPurpose(), FinServiceEvent.EARLYSETTLE)) {
			logger.debug("Leaving - Early settlement");
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

			if (curSchd.getSchDate().equals(fm.getMaturityDate())) {
				break;
			}
		}

		curSchd.setProfitSchd(curSchd.getProfitSchd().add(bpiSchd.getProfitSchd()));
		curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));
		bpiSchd.setRepayAmount(bpiSchd.getRepayAmount().subtract(bpiSchd.getProfitSchd()));

		if (!CalculationConstants.EARLYPAY_ADJMUR.equals(finScheduleData.getMethod())) {
			bpiSchd.setProfitSchd(zeroAmount);
		}

		bpiSchd.setDefSchdDate(curSchd.getSchDate());
		// curSchd.setProfitBalance(bpiSchd.getProfitBalance().add(curSchd.getProfitCalc().subtract(curSchd.getProfitSchd())));
		// bpiSchd.setProfitBalance(zeroAmount);

		logger.debug("Leaving");
		return finScheduleData;

	}

	private FinScheduleData procMDTRecord(FinScheduleData finScheduleData, int i, boolean isRepayComplete,
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
			curSchd.setPrincipalSchd(prvSchd.getClosingBalance().add(curSchd.getDisbAmount()));
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
			BigDecimal balPft = curSchd.getProfitCalc().add(prvSchd.getProfitBalance());
			if (!cpzPOSIntact) {
				balPft = balPft.subtract(prvSchd.getCpzAmount());
			}
			if (prvSchd.getCalculatedRate().compareTo(BigDecimal.ZERO) > 0 || balPft.compareTo(BigDecimal.ZERO) > 0) {
				curSchd.setProfitSchd(curSchd.getRepayAmount().subtract(curSchd.getPrincipalSchd()));
			}

			if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) < 0) {
				curSchd.setProfitSchd(BigDecimal.ZERO);
			}

			// PSD#184263 - Getting Negative amount under schedule when EMI holidays are capitalized and 1 EMI holiday
			// given before maturity Date.
			if (!FinanceConstants.FLAG_HOLIDAY.equals(prvSchd.getBpiOrHoliday())) {
				curSchd.setProfitCalc(curSchd.getProfitSchd().subtract(prvSchd.getProfitBalance()));
			}
		}

		logger.debug("Leaving");
		return finScheduleData;

	}

	private FinScheduleData prepareFirstSchdCal(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		int indexRepay = finScheduleData.getFinanceMain().getSchdIndex();
		FinanceScheduleDetail grcEndSchd = finScheduleData.getFinanceScheduleDetails().get(indexRepay);
		FinanceScheduleDetail rpySchd = finScheduleData.getFinanceScheduleDetails().get(indexRepay);

		FinanceMain fm = finScheduleData.getFinanceMain();
		BigDecimal instAmt = new BigDecimal(0);
		int terms = fm.getNumberOfTerms();
		BigDecimal presentValue = grcEndSchd.getClosingBalance();
		String schdMethod = fm.getScheduleMethod();

		fm.setAdjTerms(terms);

		if (fm.isStepFinance() && fm.isRpyStps()) {
			if (PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
				finScheduleData = calStepSchd(finScheduleData);
			}
		} else {

			if (schdMethod.equals(CalculationConstants.SCHMTHD_EQUAL)) {

				if (fm.getReqRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
					instAmt = fm.getReqRepayAmount();
				} else {
					BigDecimal intRate = rpySchd.getCalculatedRate();
					instAmt = approxPMT(fm, intRate, terms, presentValue, BigDecimal.ZERO, 0);
				}

			} else if (schdMethod.equals(CalculationConstants.SCHMTHD_PRI)
					|| schdMethod.equals(CalculationConstants.SCHMTHD_PRI_PFT)) {

				if (fm.isSanBsdSchdle()) {
					instAmt = fm.getFinAssetValue().divide(BigDecimal.valueOf(terms), 0, RoundingMode.HALF_DOWN);
					fm.setEqualRepay(false);
					fm.setCalculateRepay(false);
				} else if (fm.getReqRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
					instAmt = fm.getReqRepayAmount();
				} else {
					instAmt = presentValue.divide(BigDecimal.valueOf(terms), 0, RoundingMode.HALF_DOWN);
				}
				instAmt = CalculationUtil.roundAmount(instAmt, fm.getCalRoundingMode(), fm.getRoundingTarget());
			}

			Date startFrom = fm.getNextRepayPftDate();
			if (StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
					&& DateUtil.compare(fm.getNextRepayDate(), startFrom) < 0) {
				startFrom = fm.getNextRepayDate();
			}

			finScheduleData = setRpyInstructDetails(finScheduleData, startFrom, fm.getMaturityDate(), instAmt,
					schdMethod);
			fm.setRecalFromDate(fm.getNextRepayPftDate());
			fm.setIndexMisc(finScheduleData.getRepayInstructions().size() - 1);
			fm.setMiscAmount(instAmt);
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceStepPolicyDetail> stepPolicyDetails = finScheduleData.getStepPolicyDetails();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int iTerms = 0;

		BigDecimal closeBalPercentage = BigDecimal.ONE;
		BigDecimal stepOpenBal = BigDecimal.ZERO;
		BigDecimal openingBal = finSchdDetails.get(fm.getSchdIndex()).getClosingBalance();
		BigDecimal stepAmount = BigDecimal.ZERO;
		BigDecimal stepIntRate = BigDecimal.ZERO;

		int sdSize = finSchdDetails.size();
		int stepSize = stepPolicyDetails.size();

		fm.setIndexStart(fm.getSchdIndex() + 1);
		fm.setIndexEnd(sdSize - 1);

		boolean isRateStepOnly = false;

		if (!fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_EQUAL)
				&& !fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI_PFT)
				&& !fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI)) {
			isRateStepOnly = true;
		}

		BigDecimal sumOfStepPerc = BigDecimal.ZERO;
		stepOpenBal = openingBal;
		int stepInstCount = 0;
		for (int i = 0; i < stepSize; i++) {
			FinanceStepPolicyDetail stepDetail = stepPolicyDetails.get(i);
			stepIntRate = stepDetail.getRateMargin();
			sumOfStepPerc = sumOfStepPerc.add(stepDetail.getEmiSplitPerc());
			stepInstCount = stepDetail.getInstallments();

			iTerms = 0;
			BigDecimal exclStepPerc = BigDecimal.ZERO;
			for (int j = (fm.getIndexStart()); j < sdSize; j++) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(j);
				FinanceScheduleDetail prvSchd = finSchdDetails.get(j - 1);

				if (prvSchd.getBaseRate() != null) {
					prvSchd.setMrgRate(prvSchd.getMrgRate().add(stepIntRate));
				} else {
					prvSchd.setActRate(prvSchd.getActRate().add(stepIntRate));
					prvSchd.setCalculatedRate(prvSchd.getCalculatedRate().add(stepIntRate));
				}

				boolean procForStepRecal = false;
				stepOpenBal = stepOpenBal.add(curSchd.getDisbAmount());
				openingBal = openingBal.add(curSchd.getDisbAmount());
				if (!curSchd.isRepayOnSchDate() && !curSchd.isDisbOnSchDate()) {
					continue;
				} else if (curSchd.isDisbOnSchDate()) {
					if (iTerms == 0) {
						if (!curSchd.isRepayOnSchDate()) {
							continue;
						}
					}
					procForStepRecal = true;
					if (curSchd.isFrqDate()) {
						stepOpenBal = prvSchd.getClosingBalance();
						openingBal = prvSchd.getClosingBalance();
					}
				} else if (!curSchd.isFrqDate()) {
					if (iTerms == 0) {
						stepOpenBal = curSchd.getClosingBalance().add(curSchd.getDisbAmount())
								.subtract(curSchd.getPrincipalSchd());
						openingBal = openingBal.subtract(curSchd.getPrincipalSchd());
						continue;
					}
					procForStepRecal = true;
				} else {
					curSchd.setRvwOnSchDate(true);
				}

				if (curSchd.isRepayOnSchDate()) {
					if (iTerms == 0) {
						fm.setIndexStart(j);
					}
					iTerms = iTerms + 1;
					fm.setIndexEnd(j);
				}

				if (iTerms != stepInstCount && !procForStepRecal) {
					continue;
				}

				exclStepPerc = BigDecimal.ZERO;
				if (procForStepRecal && iTerms != stepInstCount) {
					int totalStepTerms = stepDetail.getInstallments();
					int completedStepCount = totalStepTerms - stepInstCount;
					BigDecimal actStepPercPerInst = stepDetail.getEmiSplitPerc().divide(new BigDecimal(totalStepTerms),
							9, RoundingMode.HALF_DOWN);
					exclStepPerc = actStepPercPerInst
							.multiply(new BigDecimal(totalStepTerms - completedStepCount - iTerms));
				}
				if (j == (sdSize - 1)) {
					if (curSchd.getBaseRate() != null) {
						curSchd.setMrgRate(curSchd.getMrgRate().add(stepIntRate));
					} else {
						curSchd.setActRate(curSchd.getActRate().add(stepIntRate));
						curSchd.setCalculatedRate(curSchd.getCalculatedRate().add(stepIntRate));
					}
				}

				// Prepare Compare expected Result based on Previous schedule Closing Balance on Each Installment
				if (!isRateStepOnly) {
					if (stepDetail.getEmiSplitPerc().compareTo(BigDecimal.ZERO) == 0) {
						fm.setCompareExpectedResult(stepOpenBal);
					} else {
						closeBalPercentage = BigDecimal.ONE.subtract((sumOfStepPerc.subtract(exclStepPerc))
								.divide(new BigDecimal(100), 9, RoundingMode.HALF_DOWN));
						fm.setCompareExpectedResult(round(openingBal.multiply(closeBalPercentage)));
					}
				}

				if (fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_EQUAL)) {
					stepAmount = approxPMT(fm, curSchd.getCalculatedRate(), iTerms, stepOpenBal,
							fm.getCompareExpectedResult(), 0);
				} else if (fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI_PFT)
						|| fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI)) {
					stepAmount = stepOpenBal.subtract(fm.getCompareExpectedResult()).divide(BigDecimal.valueOf(iTerms),
							0, RoundingMode.HALF_DOWN);
				}

				stepAmount = CalculationUtil.roundAmount(stepAmount, fm.getCalRoundingMode(), fm.getRoundingTarget());

				if (!isRateStepOnly) {
					finScheduleData = fetchRepayCurRates(finScheduleData);
					fm.setMiscAmount(stepAmount);
					finScheduleData = targetPriOSBal(finScheduleData, iTerms, false);
					stepOpenBal = finScheduleData.getFinanceScheduleDetails().get(j).getClosingBalance();
				}

				fm.setIndexStart(j + 1);

				if (procForStepRecal) {
					procForStepRecal = false;
					stepInstCount = stepInstCount - iTerms;
					iTerms = 0;
					if (curSchd.isFrqDate()) {
						openingBal = openingBal.add(curSchd.getDisbAmount());
					}
				}

				if (iTerms == stepInstCount) {
					break;
				}
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int sdSize = finScheduleData.getFinanceScheduleDetails().size();
		BigDecimal stepAmount = fm.getMiscAmount();
		Date fromDate = finSchdDetails.get(fm.getIndexStart()).getSchDate();
		Date toDate = finSchdDetails.get(fm.getIndexEnd()).getSchDate();
		Boolean isFinalStep = false;
		BigDecimal comparisionAmount = BigDecimal.ZERO;
		BigDecimal comparisionToAmount = BigDecimal.ZERO;
		String schdMethod = fm.getScheduleMethod();
		BigDecimal adjAmount = BigDecimal.ZERO;
		BigDecimal adjAmountAbsolute = BigDecimal.ZERO;
		BigDecimal maxAlwDif = new BigDecimal(iTerms);
		maxAlwDif = maxAlwDif.divide(new BigDecimal(2)).subtract(BigDecimal.ONE);
		maxAlwDif = roundCeiling(maxAlwDif);

		// It is rare case and happen only when last step size is 1
		if (maxAlwDif.compareTo(BigDecimal.ZERO) < 0) {
			maxAlwDif = BigDecimal.ZERO;
		}

		FinanceScheduleDetail compSchd = finSchdDetails.get(fm.getIndexEnd());

		if (fm.getIndexEnd() == (sdSize - 1)) {
			isFinalStep = true;
		}

		int riSize = finScheduleData.getRepayInstructions().size();

		for (int i = 0; i < riSize; i++) {
			RepayInstruction curInstruction = finScheduleData.getRepayInstructions().get(i);

			if (DateUtil.compare(curInstruction.getRepayDate(), fromDate) >= 0) {
				schdMethod = curInstruction.getRepaySchdMethod();
				break;
			}
		}

		setRpyInstructDetails(finScheduleData, fromDate, toDate, stepAmount, schdMethod);

		finScheduleData = getRpyInstructDetails(finScheduleData);
		// finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

		compSchd = finSchdDetails.get(fm.getIndexEnd());

		if (!isFinalStep) {
			comparisionAmount = compSchd.getClosingBalance().subtract(compSchd.getDisbAmount());
			comparisionToAmount = fm.getCompareExpectedResult();
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

			if (adjAmount.abs().compareTo(BigDecimal.valueOf(fm.getRoundingTarget())) <= 0) {
				break;
			}

			stepAmount = stepAmount.add(adjAmount);
			stepAmount = CalculationUtil.roundAmount(stepAmount, fm.getCalRoundingMode(), fm.getRoundingTarget());

			setRpyInstructDetails(finScheduleData, fromDate, toDate, stepAmount, schdMethod);

			finScheduleData = getRpyInstructDetails(finScheduleData);
			// finScheduleData = graceSchdCal(finScheduleData);
			finScheduleData = repaySchdCal(finScheduleData, isCalFlat);

			compSchd = finSchdDetails.get(fm.getIndexEnd());

			if (!isFinalStep) {
				comparisionAmount = compSchd.getClosingBalance().subtract(compSchd.getDisbAmount());
				comparisionToAmount = fm.getCompareExpectedResult();
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceStepPolicyDetail> stepPolicyDetails = finScheduleData.getStepPolicyDetails();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail geSchd = null;

		for (int i = 1; i < finSchdDetails.size(); i++) {
			if (finSchdDetails.get(i).isRepayOnSchDate() && finSchdDetails.get(i).isFrqDate()) {
				geSchd = finSchdDetails.get(i - 1);
				fm.setSchdIndex(i - 1);
				break;
			}
		}

		boolean isRateStepOnly = false;

		BigDecimal approxEMI = BigDecimal.ZERO;
		BigDecimal stepEMI = BigDecimal.ZERO;

		finScheduleData = fetchRatesHistory(finScheduleData);
		finScheduleData = fetchGraceCurRates(finScheduleData);

		BigDecimal remBal = geSchd.getClosingBalance().subtract(geSchd.getDisbAmount());

		int numberOfTerms = fm.getNumberOfTerms();
		String scheduleMethod = fm.getScheduleMethod();
		if (CalculationConstants.SCHMTHD_EQUAL.equals(scheduleMethod)) {
			approxEMI = approxPMT(fm, geSchd.getCalculatedRate(), numberOfTerms, remBal, BigDecimal.ZERO, 0);
		} else if (CalculationConstants.SCHMTHD_PRI.equals(scheduleMethod)
				|| CalculationConstants.SCHMTHD_PRI_PFT.equals(scheduleMethod)) {
			approxEMI = remBal.divide(BigDecimal.valueOf(numberOfTerms), 0, RoundingMode.HALF_DOWN);
		} else {
			approxEMI = BigDecimal.ZERO;
			isRateStepOnly = true;
		}

		approxEMI = CalculationUtil.roundAmount(approxEMI, fm.getCalRoundingMode(), fm.getRoundingTarget());

		BigDecimal stepIntRate = BigDecimal.ZERO;

		int sdSize = finSchdDetails.size();
		int stepSize = stepPolicyDetails.size();
		int iTerms = 0;

		fm.setIndexStart(fm.getSchdIndex() + 1);
		fm.setIndexEnd(sdSize - 1);

		for (int i = 0; i < stepSize; i++) {
			FinanceStepPolicyDetail stepDetail = stepPolicyDetails.get(i);
			stepIntRate = stepDetail.getRateMargin();

			boolean isSetRepayIntructions = true;
			iTerms = 0;

			for (int j = (fm.getIndexStart()); j < sdSize; j++) {
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

				if (!curSchd.isRepayOnSchDate() || !curSchd.isFrqDate()) {
					continue;
				}

				if (isSetRepayIntructions) {
					if (!isRateStepOnly) {
						stepEMI = (approxEMI.multiply(stepDetail.getEmiSplitPerc()).divide(new BigDecimal(100), 0,
								RoundingMode.HALF_DOWN));
						stepEMI = CalculationUtil.roundAmount(stepEMI, fm.getCalRoundingMode(), fm.getRoundingTarget());
						setRpyInstructDetails(finScheduleData, curSchd.getSchDate(), fm.getMaturityDate(), stepEMI,
								curSchd.getSchdMethod());
					}
					isSetRepayIntructions = false;
				}

				if (stepDetail.getStepStart() == null) {
					stepDetail.setStepStart(curSchd.getSchDate());
				}

				iTerms = iTerms + 1;
				fm.setIndexEnd(j);
				stepDetail.setStepEnd(curSchd.getSchDate());

				if (iTerms == stepDetail.getInstallments()) {
					fm.setIndexStart(j + 1);
					break;
				}

			}
		}

		finScheduleData = fetchRepayCurRates(finScheduleData);
		finScheduleData = getRpyInstructDetails(finScheduleData);
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);

		fm.setMiscAmount(approxEMI);
		fm.setAdjTerms(numberOfTerms);
		finScheduleData = calEqualStepPayment(finScheduleData);

		logger.debug("Leaving");
		return finScheduleData;
	}

	// This is a new method introduced to calculate Equal Installment Amount for the given dates
	// Algorithm used Linear Interpolation Method
	private FinScheduleData calEqualInst(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);

		// STEP0: Set required variables
		FinanceMain fm = schdData.getFinanceMain();
		List<RepayInstruction> instructions = schdData.getRepayInstructions();
		boolean isAdjustClosingBal = fm.isAdjustClosingBal();

		// Find repay instruction index
		int idxRI = 0;
		for (int i = 0; i < instructions.size(); i++) {
			if (DateUtil.compare(instructions.get(i).getRepayDate(), fm.getRecalFromDate()) >= 0) {
				idxRI = i;
				break;
			}
		}

		if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
				&& fm.getAdvTerms() > 0) {
			int idx = schdData.getFinanceScheduleDetails().size() - fm.getAdvTerms() - 1;
			fm.setRecalToDate(schdData.getFinanceScheduleDetails().get(idx).getSchDate());
		}

		// STEP1: Get the First Co-ordinates for approximation
		// Set New EMI Guess using PMT Calculation

		BigDecimal newEmiGuess = BigDecimal.ZERO;
		BigDecimal curEmiGuess = BigDecimal.ZERO;
		BigDecimal curEmiDiff = BigDecimal.ZERO;

		RepayInstruction ri = instructions.get(idxRI);
		boolean isEmiCheck = false;
		String repaySchdMethod = ri.getRepaySchdMethod();

		if (CalculationConstants.SCHMTHD_EQUAL.equals(repaySchdMethod)) {
			newEmiGuess = getApproxPMT(schdData);
			isEmiCheck = true;
		} else if (CalculationConstants.SCHMTHD_PRI.equals(repaySchdMethod)
				|| CalculationConstants.SCHMTHD_PRI_PFT.equals(repaySchdMethod)
				|| CalculationConstants.SCHMTHD_MAN_PRI.equals(repaySchdMethod)) {
			newEmiGuess = calEqualPri(schdData);
		}

		schdData.setEqualInst(true);
		for (int iLoop = 0; iLoop < 50; iLoop++) {
			// STEP2: Set Repay instructions with emiGuess and calculate the schedule

			if (newEmiGuess.compareTo(BigDecimal.ZERO) < 0) {
				newEmiGuess = BigDecimal.ZERO;
			}

			instructions.get(idxRI).setRepayAmount(newEmiGuess);
			fm.setAdjustClosingBal(isAdjustClosingBal);

			if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
					&& fm.getAdvTerms() > 0 && !PROC_GETCALSCHD.equals(schdData.getModuleDefiner())) {
				fm.setAdjustClosingBal(false);
			}

			schdData = getRpyInstructDetails(schdData);

			if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
					&& fm.getAdvTerms() > 0) {
				fm.setAdjustClosingBal(true);
			}

			schdData = graceSchdCal(schdData);
			schdData = repaySchdCal(schdData, false);

			// STEP3: Find the Difference in EMI.
			// Difference in Average EMI for the period = emiGuess * terms - (Sum of Repayments)
			BigDecimal prvEmiGuess = curEmiGuess;
			BigDecimal prvEmiDiff = curEmiDiff;

			curEmiGuess = newEmiGuess;
			curEmiDiff = calAvgEMIDifference(schdData, idxRI, isEmiCheck);

			// STEP4: Find New EMI Guess
			if (iLoop == 0) {
				newEmiGuess = curEmiGuess.add(curEmiDiff);
				continue;
			}

			if ((curEmiDiff.subtract(prvEmiDiff)).compareTo(BigDecimal.ZERO) == 0) {
				BigDecimal recalFromDateEMI = BigDecimal.ZERO;
				BigDecimal recalToDateEMI = BigDecimal.ZERO;
				int iTerms = 0;
				for (int iFsd = 0; iFsd < schdData.getFinanceScheduleDetails().size(); iFsd++) {
					FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(iFsd);

					if (DateUtil.compare(curSchd.getSchDate(), fm.getRecalFromDate()) < 0) {
						continue;
					}
					if (!curSchd.isRepayOnSchDate()) {
						continue;
					}
					iTerms = iTerms + 1;
					if (DateUtil.compare(curSchd.getSchDate(), fm.getRecalFromDate()) == 0) {
						if (StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
							recalFromDateEMI = curSchd.getRepayAmount();
						} else if (StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
								|| StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT)) {
							recalFromDateEMI = curSchd.getPrincipalSchd();
						}
					}
					if (DateUtil.compare(curSchd.getSchDate(), fm.getRecalToDate()) == 0) {
						if (StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
							recalToDateEMI = curSchd.getRepayAmount();
						} else if (StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
								|| StringUtils.equals(fm.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT)) {
							recalToDateEMI = curSchd.getPrincipalSchd();
						}
						break;
					}
				}

				BigDecimal diffEMI = recalFromDateEMI.subtract(recalToDateEMI).abs();
				diffEMI = diffEMI.divide(BigDecimal.valueOf(iTerms), 0, RoundingMode.HALF_DOWN);
				if (diffEMI.compareTo(BigDecimal.valueOf(fm.getRoundingTarget())) <= 0) {
					break;
				}
			}

			if (curEmiDiff.compareTo(prvEmiDiff) == 0) {
				logger.info(
						"Current EMI Difference {} & Previous EMI difference {}, both are same, System is terminating the calculations.",
						curEmiDiff, prvEmiDiff);

				break;
			}

			newEmiGuess = prvEmiDiff.negate().multiply((curEmiGuess.subtract(prvEmiGuess)));
			newEmiGuess = newEmiGuess.divide((curEmiDiff.subtract(prvEmiDiff)), 0, RoundingMode.HALF_DOWN);
			newEmiGuess = newEmiGuess.add(prvEmiGuess);
			newEmiGuess = CalculationUtil.roundAmount(newEmiGuess, fm.getCalRoundingMode(), fm.getRoundingTarget());

			// STEP6: Check if new EMI is Final guess
			if (curEmiDiff.compareTo(BigDecimal.ZERO) == 0 || newEmiGuess.compareTo(curEmiGuess) == 0
					|| newEmiGuess.compareTo(prvEmiGuess) == 0) {
				break;
			}

		}

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	private BigDecimal calAvgEMIDifference(FinScheduleData schdData, int idxRI, boolean isEmiCheck) {
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		List<RepayInstruction> instructions = schdData.getRepayInstructions();

		BigDecimal emiSum = BigDecimal.ZERO;
		Date recalFromDate = fm.getRecalFromDate();
		Date recalToDate = fm.getRecalToDate();
		int calTerms = 0;
		BigDecimal newEndinBalance = BigDecimal.ZERO;

		for (int iFsd = 0; iFsd < schedules.size(); iFsd++) {
			FinanceScheduleDetail curSchd = schedules.get(iFsd);

			if (DateUtil.compare(curSchd.getSchDate(), recalFromDate) < 0) {
				continue;
			}

			if (DateUtil.compare(curSchd.getSchDate(), recalToDate) == 0) {
				newEndinBalance = curSchd.getClosingBalance();
			}

			if (DateUtil.compare(curSchd.getSchDate(), recalToDate) > 0) {
				break;
			}
			if (!curSchd.isRepayOnSchDate()) {
				continue;
			}

			if (StringUtils.isBlank(curSchd.getBpiOrHoliday())) {
				calTerms = calTerms + 1;
			}

			if (isEmiCheck) {
				emiSum = emiSum.add(curSchd.getRepayAmount());
			} else {
				emiSum = emiSum.add(curSchd.getPrincipalSchd());
			}

		}

		BigDecimal totEmiGuess = instructions.get(idxRI).getRepayAmount();
		if (calTerms > 0) {
			BigDecimal bgCalTerms = BigDecimal.valueOf(calTerms);
			totEmiGuess = totEmiGuess.multiply(bgCalTerms);
		}

		emiSum = emiSum.add(newEndinBalance).subtract(fm.getExpectedEndBal());

		BigDecimal avgEMIDiff = totEmiGuess.subtract(emiSum).negate();

		if (calTerms > 0) {
			avgEMIDiff = avgEMIDiff.divide(BigDecimal.valueOf(calTerms), 0, RoundingMode.HALF_DOWN);
		}
		avgEMIDiff = CalculationUtil.roundAmount(avgEMIDiff, fm.getCalRoundingMode(), fm.getRoundingTarget());

		return avgEMIDiff;
	}

	private BigDecimal calEqualPri(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		boolean isAdjustClosingBal = fm.isAdjustClosingBal();

		BigDecimal equalPri = BigDecimal.ZERO;

		int sdSize = schedules.size();

		if (!isAdjustClosingBal && AdvanceType.hasAdvEMI(fm.getAdvType())
				&& AdvanceStage.hasFrontEnd(fm.getAdvStage())) {
			sdSize = schedules.size() - fm.getAdvTerms();
		}

		Date recalFromDate = fm.getRecalFromDate();
		Date recalToDate = fm.getRecalToDate();
		BigDecimal closingBal = BigDecimal.ZERO;
		int calTerms = 0;

		for (int iFsd = 0; iFsd < sdSize; iFsd++) {
			FinanceScheduleDetail curSchd = schedules.get(iFsd);
			Date schDate = curSchd.getSchDate();
			if (DateUtil.compare(schDate, recalFromDate) < 0) {
				closingBal = curSchd.getClosingBalance();
				continue;
			}

			if (DateUtil.compare(schDate, recalToDate) > 0) {
				break;
			}

			if (!curSchd.isRepayOnSchDate()) {
				continue;
			}

			if (StringUtils.isBlank(curSchd.getBpiOrHoliday())) {
				calTerms = calTerms + 1;
			}
		}

		if (calTerms > 0) {
			equalPri = closingBal.divide(BigDecimal.valueOf(calTerms), 9, RoundingMode.HALF_DOWN);
			equalPri = CalculationUtil.roundAmount(equalPri, fm.getCalRoundingMode(), fm.getRoundingTarget());
		}

		return equalPri;
	}

	private FinScheduleData calEqualStepPayment(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		List<RepayInstruction> instructions = schdData.getRepayInstructions();
		List<FinanceStepPolicyDetail> fspd = schdData.getStepPolicyDetails();

		int sdSize = schedules.size();
		int riSize = instructions.size();
		int iRpyInst = 0;
		int iCompare = 0;

		// Setting Compare to Expected defaults to false for Further actions
		fm.setCompareToExpected(false);
		String schdMethod = fm.getScheduleMethod();

		boolean isComapareWithEMI = false;

		if (!CalculationConstants.SCHMTHD_EQUAL.equals(schdMethod)
				&& !CalculationConstants.SCHMTHD_PRI_PFT.equals(schdMethod)
				&& !CalculationConstants.SCHMTHD_PRI.equals(schdMethod)) {
			logger.debug(Literal.LEAVING);
			return schdData;
		}

		if (CalculationConstants.SCHMTHD_EQUAL.equals(schdMethod)) {
			isComapareWithEMI = true;
		}

		iRpyInst = riSize - 1;
		iCompare = sdSize - 1;

		BigDecimal approxEMI = fm.getMiscAmount();
		approxEMI = CalculationUtil.roundAmount(approxEMI, fm.getCalRoundingMode(), fm.getRoundingTarget());

		// SET COMPARISION TO REPAYMENT or PRINCIPAL
		FinanceScheduleDetail curSchd = schedules.get(iCompare);

		BigDecimal iCompareEMI = schedules.get(sdSize - 1).getPrincipalSchd();
		if (isComapareWithEMI) {
			iCompareEMI = schedules.get(sdSize - 1).getRepayAmount();
		}

		BigDecimal hundred = new BigDecimal(100);
		FinanceStepPolicyDetail stepDetail = fspd.get(fspd.size() - 1);
		if (stepDetail.getEmiSplitPerc().compareTo(BigDecimal.ZERO) > 0) {
			iCompareEMI = iCompareEMI.multiply(hundred).divide(stepDetail.getEmiSplitPerc(), 0, RoundingMode.HALF_DOWN);
		} else {
			iCompareEMI = stepDetail.getSteppedEMI();
		}

		BigDecimal repayAmountLow = approxEMI;
		BigDecimal repayAmountHigh = iCompareEMI;

		if (approxEMI.compareTo(iCompareEMI) > 0) {
			repayAmountLow = iCompareEMI;
			repayAmountHigh = approxEMI;
		}

		BigDecimal lastTriedEMI = BigDecimal.ZERO;
		BigDecimal number2 = new BigDecimal(2);

		for (int i = 0; i < 50; i++) {
			approxEMI = (repayAmountLow.add(repayAmountHigh)).divide(number2, 0, RoundingMode.HALF_DOWN);

			if (repayAmountLow.compareTo(approxEMI) == 0 || repayAmountHigh.compareTo(approxEMI) == 0) {
				break;
			}

			BigDecimal diffLowHigh = (repayAmountHigh.subtract(repayAmountLow)).abs();
			if (diffLowHigh.compareTo(BigDecimal.valueOf(fm.getRoundingTarget())) <= 0) {
				break;
			}

			lastTriedEMI = approxEMI;

			// Step Based Repay Instruction re-modification based on Approximate EMI Value
			// Repay Instructions Reset based on Step Start Date
			setStepRepayAmount(schdData, approxEMI);

			schdData = getRpyInstructDetails(schdData);
			schdData = graceSchdCal(schdData);
			schdData = repaySchdCal(schdData, false);

			// Find COMPARISION AND COMPARIONTO amounts
			BigDecimal comparisionAmount = instructions.get(iRpyInst).getRepayAmount();
			BigDecimal comparisionToAmount = curSchd.getPrincipalSchd();

			iCompareEMI = schedules.get(sdSize - 1).getPrincipalSchd();

			// SET COMPARISION TO
			if (isComapareWithEMI) {
				comparisionToAmount = curSchd.getRepayAmount();
				iCompareEMI = schedules.get(sdSize - 1).getRepayAmount();
			}

			if (comparisionToAmount.compareTo(comparisionAmount) == 0) {
				logger.debug(Literal.LEAVING);
				return schdData;
			}

			diffLowHigh = (comparisionToAmount.subtract(comparisionAmount)).abs();
			if (diffLowHigh.compareTo(BigDecimal.valueOf(fm.getRoundingTarget())) <= 0) {
				logger.debug(Literal.LEAVING);
				return schdData;
			}

			stepDetail = fspd.get(fspd.size() - 1);
			if (stepDetail.getEmiSplitPerc().compareTo(BigDecimal.ZERO) > 0) {
				iCompareEMI = iCompareEMI.multiply(hundred).divide(stepDetail.getEmiSplitPerc(), 0,
						RoundingMode.HALF_DOWN);
			} else {
				iCompareEMI = stepDetail.getSteppedEMI();
			}

			if (iCompareEMI.compareTo(approxEMI) > 0) {
				repayAmountLow = approxEMI;
			} else {
				repayAmountHigh = approxEMI;
			}

		}

		// Find Nearest EMI
		BigDecimal minRepayDifference = BigDecimal.ZERO;
		BigDecimal maxRepayDifference = BigDecimal.ZERO;

		// Find COMPARISION AND COMPARIONTO amounts
		BigDecimal comparisionAmount = instructions.get(iRpyInst).getRepayAmount();
		BigDecimal comparisionToAmount = curSchd.getPrincipalSchd();

		// SET COMPARISION TO
		if (isComapareWithEMI) {
			comparisionToAmount = curSchd.getRepayAmount();
		}

		if (repayAmountLow.compareTo(repayAmountHigh) != 0) {
			if (lastTriedEMI.compareTo(repayAmountLow) == 0) {
				minRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				approxEMI = repayAmountHigh;
			} else {
				maxRepayDifference = (comparisionToAmount.subtract(comparisionAmount)).abs();
				approxEMI = repayAmountLow;
			}

			approxEMI = CalculationUtil.roundAmount(approxEMI, fm.getCalRoundingMode(), fm.getRoundingTarget());

			lastTriedEMI = approxEMI;
			// Step Based Repay Instruction re-modification based on Approximate EMI Value
			// Repay Instructions Reset based on Step Start Date
			setStepRepayAmount(schdData, approxEMI);

			schdData = getRpyInstructDetails(schdData);
			schdData = graceSchdCal(schdData);
			schdData = repaySchdCal(schdData, false);

			// Find COMPARISION AND COMPARIONTO amounts
			comparisionAmount = instructions.get(iRpyInst).getRepayAmount();

			// SET COMPARISION TO
			if (isComapareWithEMI) {
				comparisionToAmount = curSchd.getRepayAmount();
			} else {
				comparisionToAmount = curSchd.getPrincipalSchd();
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

		// Step Based Repay Instruction re-modification based on Approximate EMI Value
		// Repay Instructions Reset based on Step Start Date
		setStepRepayAmount(schdData, approxEMI);

		schdData = getRpyInstructDetails(schdData);
		schdData = graceSchdCal(schdData);
		schdData = repaySchdCal(schdData, false);

		logger.debug(Literal.LEAVING);
		return schdData;
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

		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();

		if ((FinServiceEvent.EARLYSETTLE.equals(receiptPurpose) || FinServiceEvent.EARLYRPY.equals(receiptPurpose))
				&& SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK)) {
			if (DateUtil.compare(graceEndDate, eventFromDate) >= 0) {
				schedules.forEach(l1 -> {
					if (DateUtil.compare(l1.getSchDate(), eventFromDate) >= 0) {
						l1.setRecalLock(false);
					}
				});
			}
		}

		// Resetting Recal Schedule Method & Next Recal From Date if not exists
		String recalSchdMethod = null;
		for (int i = 0; i < schedules.size(); i++) {
			FinanceScheduleDetail curSchd = schedules.get(i);

			if (DateUtil.compare(curSchd.getSchDate(), eventFromDate) == 0) {
				fm.setIndexMisc(i);
				openSchd = curSchd;

				if (curSchd.isRepayOnSchDate()) {
					recalSchdMethod = curSchd.getSchdMethod();
					if (CalculationConstants.SCHMTHD_PFT.equals(recalSchdMethod)
							|| CalculationConstants.SCHMTHD_PFTCPZ.equals(recalSchdMethod)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					}

					if (CalculationConstants.SCHMTHD_PFTCAP.equals(recalSchdMethod)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFTC;
					}

					if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
						if (CalculationConstants.SCHMTHD_PRI.equals(recalSchdMethod)) {
							recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
						} else if (StringUtils.equals(recalSchdMethod, CalculationConstants.SCHMTHD_PFTCAP)) {
							recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
						}
					}
				} else if (curSchd.isPftOnSchDate()) {
					recalSchdMethod = curSchd.getSchdMethod();
					if (StringUtils.equals(recalSchdMethod, CalculationConstants.SCHMTHD_PFTCAP)
							&& !StringUtils.equals(receiptPurpose, FinServiceEvent.EARLYSETTLE)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFTC;
					} else {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					}
				} else {
					recalSchdMethod = CalculationConstants.SCHMTHD_PRI;
					if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					}
				}

				String bpiOrHoliday = curSchd.getBpiOrHoliday();
				if (FinanceConstants.FLAG_HOLIDAY.equals(bpiOrHoliday)
						|| FinanceConstants.FLAG_POSTPONE.equals(bpiOrHoliday)
						|| FinanceConstants.FLAG_STRTPRDHLD.equals(bpiOrHoliday)
						|| FinanceConstants.FLAG_MORTEMIHOLIDAY.equals(bpiOrHoliday)
						|| FinanceConstants.FLAG_UNPLANNED.equals(bpiOrHoliday)) {

					if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
					} else {
						recalSchdMethod = CalculationConstants.SCHMTHD_PRI;
					}
				}

			} else if (DateUtil.compare(curSchd.getSchDate(), eventFromDate) > 0) {
				if (curSchd.getPresentmentId() != 0 || DateUtil.compare(curSchd.getSchDate(), graceEndDate) <= 0) {
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

			if (FinServiceEvent.EARLYRPY.equals(receiptPurpose)) {
				recalSchdMethod = CalculationConstants.SCHMTHD_PRI;
			} else if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
				recalSchdMethod = CalculationConstants.SCHMTHD_PRI_PFT;
			}

			if (DateUtil.compare(openSchd.getSchDate(), graceEndDate) < 0) {
				openSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
			} else {
				openSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
			}
		}

		if (FinServiceEvent.EARLYSETTLE.equals(receiptPurpose)) {
			openSchd.setPftOnSchDate(true);
		} else {
			openSchd.setPartialPaidAmt(openSchd.getPartialPaidAmt().add(amount));
		}
		openSchd.setRepayOnSchDate(true);
		if (TDSCalculator.isTDSApplicable(fm)) {
			// openSchd.setTDSApplicable(schedules.get(prvIndex + 2).isTDSApplicable());
			Date maturityDate = fm.getMaturityDate();

			if (maturityDate.compareTo(SysParamUtil.getAppDate()) <= 0) {
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int sdSize = finSchdDetails.size();

		fm.setCompareToExpected(false);
		fm.setCompareExpectedResult(BigDecimal.ZERO);
		fm.setCalculateRepay(true);
		if (StringUtils.isEmpty(fm.getRecalSchdMethod())) {
			finScheduleData = getSchdMethod(finScheduleData);
		}
		if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isSchdOnPMTCal()
				&& CalculationConstants.SCHMTHD_EQUAL.equals(fm.getRecalSchdMethod())
				&& CalculationConstants.RPYCHG_TILLMDT.equals(fm.getRecalType())) {
			fm.setEqualRepay(false);
		} else {
			fm.setEqualRepay(true);
		}
		fm.setIndexMisc(0);
		fm.setMiscAmount(BigDecimal.ZERO);

		String recaltype = fm.getRecalType();
		int adjTerms = fm.getAdjTerms();
		int iOldMDT = finSchdDetails.size() - 1;
		boolean resetRpyInstruction = true;

		// Force set recaltype to TILLMDT. TILLDATE comparision will happen with
		// closing balance, which gives wrong results for last record.
		if (CalculationConstants.RPYCHG_TILLDATE.equals(recaltype)) {
			if (fm.getRecalToDate().equals(finSchdDetails.get(sdSize - 1).getSchDate())) {
				recaltype = CalculationConstants.RPYCHG_TILLMDT;
				fm.setRecalType(recaltype);
			}
		}

		if (CalculationConstants.RPYCHG_ADDTERM.equals(recaltype)
				|| CalculationConstants.RPYCHG_ADDRECAL.equals(recaltype)) {

			finScheduleData = procAddTerm(finScheduleData, adjTerms, false);

			// If error return error message
			if (finScheduleData.getErrorDetails().size() > 0) {
				return finScheduleData;
			}

			fm = finScheduleData.getFinanceMain();
			finSchdDetails = finScheduleData.getFinanceScheduleDetails();
			sdSize = finSchdDetails.size();

			// Set Recalculation Start and End Dates
			if (CalculationConstants.RPYCHG_ADDRECAL.equals(recaltype)) {
				fm.setRecalToDate(finSchdDetails.get(sdSize - 1).getSchDate());
			} else {

				fm.setRecalFromDate(finSchdDetails.get(iOldMDT + 1).getSchDate());
				fm.setRecalToDate(finSchdDetails.get(sdSize - 1).getSchDate());
				if (PROC_UNPLANEMIH.equals(recalPurpose)) {
					resetRpyInstruction = false;
				}
			}

		} else if (CalculationConstants.RPYCHG_ADJMDT.equals(recaltype)
				|| CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(recaltype)) {
			fm.setCalculateRepay(false);
			fm.setEqualRepay(false);

			fm.setRecalFromDate(fm.getMaturityDate());
			fm.setRecalToDate(fm.getMaturityDate());

		} else if (CalculationConstants.RPYCHG_TILLMDT.equals(recaltype)) {
			fm.setRecalToDate(finSchdDetails.get(sdSize - 1).getSchDate());
		}

		// Set maturity Date schedule amount
		if (CalculationConstants.SCHMTHD_EQUAL.equals(finSchdDetails.get(sdSize - 1).getSchdMethod())) {
			fm.setCompareExpectedResult(finSchdDetails.get(sdSize - 1).getRepayAmount());
		} else {
			fm.setCompareExpectedResult(finSchdDetails.get(sdSize - 1).getPrincipalSchd());
		}

		Date recalFromDate = fm.getRecalFromDate();
		Date recalToDate = fm.getRecalToDate();
		String schdMethod = fm.getRecalSchdMethod();

		// Set RecalSchdMethod
		finScheduleData = getSchdMethod(finScheduleData);
		schdMethod = fm.getRecalSchdMethod();

		if (CalculationConstants.SCHMTHD_PFT.equals(schdMethod) || fm.isStepRecalOnProrata()) {
			resetRpyInstruction = false;
		}

		// Set Repayment Instructions as 1 for recalFromDate to recalToDate.
		// Reason for not setting zero is to avoid deleting future zero
		// instructions
		if (!CalculationConstants.RPYCHG_ADJMDT.equals(recaltype)
				&& !CalculationConstants.RPYCHG_ADDITIONAL_BPI.equals(recaltype) && resetRpyInstruction) {
			finScheduleData = setRpyInstructDetails(finScheduleData, recalFromDate, recalToDate, BigDecimal.ONE,
					schdMethod);
		} else if (!resetRpyInstruction) {
			fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
			fm.setCalculateRepay(false);
		}

		finScheduleData.setFinanceMain(fm);
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		if (!StringUtils.equals(fm.getRecalSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		if (!StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
			logger.debug("Leaving");
			return finScheduleData;
		}

		Date recalFromDate = fm.getRecalFromDate();
		Date recalToDate = fm.getRecalToDate();
		String schdMethod = fm.getScheduleMethod();
		fm.setEqualRepay(false);

		if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_EQUAL)) {
			rpyAmt = getApproxPMT(finScheduleData);
		}

		finScheduleData = setRpyInstructDetails(finScheduleData, recalFromDate, recalToDate, rpyAmt, schdMethod);
		if (AdvanceType.hasAdvEMI(fm.getAdvType()) && AdvanceStage.hasFrontEnd(fm.getAdvStage())
				&& fm.getAdvTerms() > 0) {
			fm.setAdjustClosingBal(false);
		}
		finScheduleData = getRpyInstructDetails(finScheduleData);
		finScheduleData = graceSchdCal(finScheduleData);
		finScheduleData = repaySchdCal(finScheduleData, false);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private BigDecimal getApproxPMT(FinScheduleData schdData) {
		BigDecimal pmtAmount = BigDecimal.ZERO;
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		Date recalFromDate = fm.getRecalFromDate();
		Date recalToDate = fm.getRecalToDate();

		int calTerms = 0;
		BigDecimal startBalance = BigDecimal.ZERO;
		BigDecimal endBalance = fm.getExpectedEndBal();
		BigDecimal rate = BigDecimal.ZERO;

		for (int iFsd = 0; iFsd < schedules.size(); iFsd++) {
			FinanceScheduleDetail curSchd = schedules.get(iFsd);

			Date schDate = curSchd.getSchDate();
			if (DateUtil.compare(schDate, recalFromDate) < 0) {
				startBalance = curSchd.getClosingBalance();
				continue;
			}

			if (DateUtil.compare(schDate, recalToDate) > 0) {
				break;
			}

			if (!curSchd.isRepayOnSchDate()) {
				continue;
			}

			if (DateUtil.compare(schDate, recalFromDate) == 0) {
				if (iFsd == 0) {
					rate = curSchd.getCalculatedRate();
				} else {
					rate = schedules.get(iFsd - 1).getCalculatedRate();
				}

			}
			// endBalance = curSchd.getClosingBalance();
			if (StringUtils.isBlank(curSchd.getBpiOrHoliday())) {
				calTerms = calTerms + 1;
			}
		}

		if (calTerms > 0) {
			pmtAmount = approxPMT(fm, rate, calTerms, startBalance, endBalance, 0);
		}

		return pmtAmount;
	}

	private FinScheduleData setRepayForSanctionBasedDisbADJMDT(FinScheduleData finScheduleData,
			BigDecimal balDisbAmount) {

		FinanceMain fm = finScheduleData.getFinanceMain();
		Date evtFromDate = fm.getEventFromDate();

		List<FinanceScheduleDetail> fsdList = finScheduleData.getFinanceScheduleDetails();

		// BASED ON SANCTIONED AMOUNT, DEFINITION OF INSTALLMENT USING TERMS
		BigDecimal instAmt = fm.getFinAssetValue().divide(BigDecimal.valueOf(fm.getNumberOfTerms()), 0,
				RoundingMode.HALF_DOWN);

		fm.setRecalFromDate(fm.getMaturityDate());

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

		if (fm.getRecalFromDate() == null) {
			fm.setRecalFromDate(fm.getMaturityDate());
		}
		fm.setRecalToDate(fm.getMaturityDate());
		fm.setEventToDate(fm.getRecalToDate());

		fm.setEqualRepay(false);
		fm.setCalculateRepay(false);

		fm.setIndexMisc(finScheduleData.getRepayInstructions().size() - 1);
		fm.setMiscAmount(instAmt);
		return finScheduleData;
	}

	private FinScheduleData setRepayForSanctionBasedPriHld(FinScheduleData finScheduleData, BigDecimal earlyPayAmt) {
		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = finScheduleData.getFinanceScheduleDetails();

		Date maturityDate = fsdList.get(fsdList.size() - 1).getSchDate();
		Date evtFromDate = fm.getEventFromDate();
		fm.setEqualRepay(false);
		fm.setCalculateRepay(false);

		fm.setEventToDate(evtFromDate);
		fm.setRecalFromDate(maturityDate);
		fm.setRecalToDate(maturityDate);

		String recalType = fm.getRecalType();
		fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		finScheduleData = changeRepay(finScheduleData, earlyPayAmt, fm.getRecalSchdMethod());

		fm.setRecalType(recalType);
		BigDecimal prvRepayAmount = new BigDecimal(-1);

		boolean isRecalFromSet = false;
		fm.setRecalFromDate(evtFromDate);
		fm.setEventFromDate(maturityDate);
		fm.setRecalToDate(maturityDate);

		// BASED ON SANCTIONED AMOUNT, DEFINITION OF INSTALLMENT USING TERMS
		BigDecimal instAmt = fm.getFinAssetValue().divide(BigDecimal.valueOf(fm.getNumberOfTerms()), 0,
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
					fm.setRecalFromDate(curSchd.getSchDate());
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
		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> fsdList = finScheduleData.getFinanceScheduleDetails();
		int sdSize = fsdList.size();
		int adjTerms = fm.getAdjTerms();

		if (StringUtils.equals(fm.getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
			finScheduleData = procAddTerm(finScheduleData, adjTerms, false);

			if (finScheduleData.getErrorDetails().size() > 0) {
				return finScheduleData;
			}

			fm = finScheduleData.getFinanceMain();
			fsdList = finScheduleData.getFinanceScheduleDetails();
			sdSize = fsdList.size();
			fm.setRecalToDate(fsdList.get(sdSize - 1).getSchDate());
		}

		Date recalFromDate = fm.getRecalFromDate();
		BigDecimal disbursedAmount = BigDecimal.ZERO;
		// BigDecimal recalPosBalance = BigDecimal.ZERO;

		if (isResetRecalFRomDate) {
			recalFromDate = fm.getFinStartDate();
			for (int iFsd = 0; iFsd < sdSize; iFsd++) {
				FinanceScheduleDetail curSchd = fsdList.get(iFsd);
				if (curSchd.getSchDate().compareTo(fm.getAppDate()) > 0) {
					break;
				}

				if (!curSchd.isRepayOnSchDate()) {
					continue;
				}

				recalFromDate = curSchd.getSchDate();
			}

			fm.setRecalFromDate(recalFromDate);
			fm.setEventToDate(fm.getRecalToDate());
		}

		adjTerms = 0;

		for (int iFsd = 0; iFsd < sdSize; iFsd++) {
			FinanceScheduleDetail curSchd = fsdList.get(iFsd);

			if (curSchd.getSchDate().compareTo(fm.getEventFromDate()) < 0) {
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
			fm.setEventFromDate(recalFromDate);
		}

		// THIS IS BASED ON ASSUMPTION. MAX DISBURSEMENT CHECK REQUIRED MUST BE
		// TRUE FOR LOAN TYPE
		BigDecimal unDisbursedAmount = fm.getFinAssetValue().subtract(disbursedAmount);
		// recalPosBalance = unDisbursedAmount.add(recalPosBalance);

		BigDecimal instAmt = unDisbursedAmount.divide(BigDecimal.valueOf(adjTerms), 0, RoundingMode.HALF_DOWN);
		fm.setEqualRepay(false);
		fm.setCalculateRepay(false);
		finScheduleData = setRpyInstructDetails(finScheduleData, recalFromDate, fm.getMaturityDate(), instAmt,
				CalculationConstants.SCHMTHD_PRI_PFT);
		fm.setIndexMisc(finScheduleData.getRepayInstructions().size() - 1);
		fm.setMiscAmount(instAmt);
		return finScheduleData;
	}

	private FinScheduleData getSchdMethod(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
		String schdMethod = fm.getRecalSchdMethod();

		if (StringUtils.equals(schdMethod, PennantConstants.List_Select)) {
			schdMethod = "";
		}

		if (!StringUtils.isBlank(schdMethod)) {
			return finScheduleData;
		}

		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();
		int risize = repayInstructions.size();

		Date schdMethodDate = fm.getRecalFromDate();

		// Set date from which schedule method can be taken
		if (DateUtil.compare(schdMethodDate, fm.getGrcPeriodEndDate()) <= 0) {
			schdMethodDate = fm.getGrcPeriodEndDate();

			// Add one day to bring comparison date to repayment period
			schdMethodDate = DateUtil.addDays(schdMethodDate, 1);
		}

		// Find Schedule Method used for existing instruction
		for (int i = 0; i < risize; i++) {
			schdMethod = repayInstructions.get(i).getRepaySchdMethod();

			if (DateUtil.compare(repayInstructions.get(i).getRepayDate(), schdMethodDate) >= 0) {
				break;
			}
		}

		// If it is first time(cases applicable for OD) , then there are no
		// Repay Instructions
		if (StringUtils.isBlank(schdMethod)) {
			schdMethod = fm.getScheduleMethod();
		}

		fm.setRecalSchdMethod(schdMethod);

		return finScheduleData;

	}

	private void getCurPerodDates(FinScheduleData schData) {
		logger.debug(Literal.ENTERING);

		boolean isFromDateSet = false;

		FinanceMain fm = schData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schData.getFinanceScheduleDetails();

		for (FinanceScheduleDetail schedule : schedules) {
			Date schDate = schedule.getSchDate();

			if (schDate.compareTo(fm.getEventFromDate()) > 0) {
				boolean repayOrPftOnSchDate = schedule.isPftOnSchDate() || schedule.isRepayOnSchDate();

				if (!isFromDateSet && repayOrPftOnSchDate) {
					fm.setRecalFromDate(schDate);
					fm.setRecalToDate(schDate);
					isFromDateSet = true;
				}

				if (schDate.compareTo(fm.getEventToDate()) >= 0 && repayOrPftOnSchDate) {
					fm.setRecalToDate(schDate);
					break;
				}
			}
		}

		FinanceScheduleDetail nextSchedule = null;
		boolean recalFromInHldy = false;

		Date recalToDate = fm.getRecalToDate();
		Date recalFromDate = fm.getRecalFromDate();
		int index = 0;

		for (FinanceScheduleDetail schedule : schedules) {
			index++;

			if (!StringUtils.isBlank(schedule.getBpiOrHoliday())) {
				if (schedule.getSchDate().compareTo(recalFromDate) == 0) {
					recalFromInHldy = true;
				}

				if (schedule.getSchDate().compareTo(recalToDate) == 0) {
					nextSchedule = schedules.get(index);
					break;
				}
			}
		}

		if (nextSchedule != null && (nextSchedule.isPftOnSchDate() || nextSchedule.isRepayOnSchDate())) {
			fm.setRecalToDate(nextSchedule.getSchDate());
			fm.setRecalFromDate(recalFromInHldy ? nextSchedule.getSchDate() : recalFromDate);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to Calculate the Overdraft Schedule Details
	 */
	private FinScheduleData buildOverdraftSchd(FinScheduleData orgFinScheduleData) {
		logger.debug("Entering");

		FinScheduleData finScheduleData = orgFinScheduleData.copyEntity();
		FinanceMain fm = finScheduleData.getFinanceMain();
		FinanceType financeType = finScheduleData.getFinanceType();

		// Overdraft Maintenance Changes for calculation
		BigDecimal prvDropLineAmount = BigDecimal.ZERO;
		BigDecimal incrLimit = BigDecimal.ZERO;
		Date startCalFrom = fm.getFinStartDate();
		boolean inclStartDate = false;
		boolean inclEndDate = true;
		List<OverdraftScheduleDetail> oldOverdraftList = new ArrayList<>();
		BigDecimal totalOSLimit = BigDecimal.ZERO;
		OverdraftScheduleDetail prvODSchd = null;

		if (DateUtil.compare(fm.getEventFromDate(), fm.getFinStartDate()) == 0) {
			totalOSLimit = fm.getFinAssetValue();
			inclStartDate = true;
			if (StringUtils.isNotEmpty(fm.getDroplineFrq())) {
				if (fm.getFirstDroplineDate() != null) {
					startCalFrom = fm.getFirstDroplineDate();
				} else {
					startCalFrom = FrequencyUtil.getNextDate(fm.getDroplineFrq(), 1, startCalFrom, "A", false)
							.getNextFrequencyDate();
				}
			}

			// Adding Start date Overdraft Schedule
			OverdraftScheduleDetail curODSchd = new OverdraftScheduleDetail();
			curODSchd.setODLimit(fm.getFinAssetValue());
			curODSchd.setDroplineDate(fm.getFinStartDate());
			if (StringUtils.isEmpty(fm.getRepayBaseRate())) {
				curODSchd.setActualRate(fm.getRepayProfitRate());
			} else {
				curODSchd.setBaseRate(fm.getRepayBaseRate());
				curODSchd.setSplRate(fm.getRepaySpecialRate());
				curODSchd.setMargin(fm.getRepayMargin());
			}

			if (StringUtils.isEmpty(curODSchd.getBaseRate())) {
				curODSchd.setDroplineRate(fm.getRepayProfitRate());
			} else {
				RateDetail rateDetail = RateUtil.rates(fm.getRepayBaseRate(), fm.getFinCcy(), fm.getRepaySpecialRate(),
						fm.getRepayMargin(), curODSchd.getDroplineDate(), fm.getRpyMinRate(), fm.getRpyMaxRate());
				if (rateDetail.getErrorDetails() == null) {
					curODSchd.setDroplineRate(rateDetail.getNetRefRateLoan());
				}
			}
			prvODSchd = curODSchd;
			oldOverdraftList.add(curODSchd);

		} else {
			boolean limitIncrDateFound = false;
			startCalFrom = fm.getEventFromDate();
			for (OverdraftScheduleDetail curODSchd : finScheduleData.getOverdraftScheduleDetails()) {
				if (DateUtil.compare(curODSchd.getDroplineDate(), fm.getEventFromDate()) > 0) {
					if (StringUtils.isNotEmpty(fm.getDroplineFrq())) {
						startCalFrom = FrequencyUtil
								.getNextDate(fm.getDroplineFrq(), 1, prvODSchd.getDroplineDate(), "A", false)
								.getNextFrequencyDate();
						if (DateUtil.compare(startCalFrom, fm.getMaturityDate()) >= 0) {
							startCalFrom = fm.getMaturityDate();
						}
						inclStartDate = true;
					}
					break;
				}

				// Calculate sum of all Limit drop lines before Current
				// Application date
				prvODSchd = curODSchd;
				prvDropLineAmount = prvDropLineAmount.add(curODSchd.getLimitDrop());
				incrLimit = fm.getFinAssetValue().subtract(prvDropLineAmount).subtract(curODSchd.getODLimit());
				if (DateUtil.compare(curODSchd.getDroplineDate(), fm.getEventFromDate()) == 0
						&& incrLimit.compareTo(BigDecimal.ZERO) > 0) {
					curODSchd.setLimitIncreaseAmt(curODSchd.getLimitIncreaseAmt().add(incrLimit));
					curODSchd.setODLimit(curODSchd.getODLimit().add(incrLimit));
					limitIncrDateFound = true;
				}
				totalOSLimit = curODSchd.getODLimit();
				oldOverdraftList.add(curODSchd);
			}

			if (DateUtil.compare(startCalFrom, fm.getFirstDroplineDate()) < 0) {
				startCalFrom = fm.getFirstDroplineDate();
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
				newOdSchd.setDroplineDate(fm.getEventFromDate());
				newOdSchd.setLimitIncreaseAmt(incrLimit);
				newOdSchd.setLimitDrop(BigDecimal.ZERO);
				newOdSchd.setODLimit(prvODSchd.getODLimit().add(incrLimit));
				oldOverdraftList.add(newOdSchd);
				totalOSLimit = prvODSchd.getODLimit().add(incrLimit);
				prvODSchd = newOdSchd;
			}
		}

		// if Overdraft Dropline Not Exists
		if (StringUtils.isEmpty(fm.getDroplineFrq()) && !(financeType.isDroplineOD()
				&& OverdraftConstants.DROPING_METHOD_VARIABLE.equals(fm.getDroppingMethod()))) {

			// Creating Expiry Schedule
			OverdraftScheduleDetail curODSchd = new OverdraftScheduleDetail();
			curODSchd.setDroplineDate(fm.getMaturityDate());
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

		if (financeType.isDroplineOD() && OverdraftConstants.DROPING_METHOD_VARIABLE.equals(fm.getDroppingMethod())) {
			return buildVariableOdSchedule(finScheduleData, totalOSLimit, prvODSchd, oldOverdraftList);
		}

		// Building Schedule terms with Dates
		FrequencyDetails frequencyDetails = FrequencyUtil.getTerms(fm.getDroplineFrq(), startCalFrom,
				fm.getMaturityDate(), inclStartDate, inclEndDate);

		// Validate Frequency Schedule Details
		if (frequencyDetails.getErrorDetails() != null) {
			logger.warn("Schedule Error: on condition --->  Validate frequency:" + fm.getDroplineFrq());
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

		limitDrop = CalculationUtil.roundAmount(limitDrop, fm.getCalRoundingMode(), fm.getRoundingTarget());

		// Setting Overdraft Schedule details
		for (int i = 0; i < odSchdDateList.size(); i++) {

			OverdraftScheduleDetail curODSchd = new OverdraftScheduleDetail();
			curODSchd.setDroplineDate(DateUtil.getDatePart(odSchdDateList.get(i)));
			curODSchd.setActualRate(prvODSchd.getActualRate());
			curODSchd.setBaseRate(prvODSchd.getBaseRate());
			curODSchd.setSplRate(prvODSchd.getSplRate());
			curODSchd.setMargin(prvODSchd.getMargin());

			// Dropline Rate
			if (StringUtils.isEmpty(curODSchd.getBaseRate())) {
				curODSchd.setDroplineRate(prvODSchd.getActualRate());
			} else {
				RateDetail rateDetail = RateUtil.rates(fm.getRepayBaseRate(), fm.getFinCcy(), fm.getRepaySpecialRate(),
						fm.getRepayMargin(), curODSchd.getDroplineDate(), fm.getRpyMinRate(), fm.getRpyMaxRate());
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

	private FinScheduleData fetchRatesHistory(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain fm = finScheduleData.getFinanceMain();
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
			List<BaseRate> baseRatesHist = getBaseRateDAO().getBaseRateHistByType(baseRateCodes.get(i), fm.getFinCcy(),
					fm.getFinStartDate());

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
					fm.getFinStartDate());

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

		FinanceMain fm = finScheduleData.getFinanceMain();
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
		if (fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PFT)
				|| fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PFTCPZ)
						&& !fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_NOPAY)) {
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
				fm.setIndexStart(i + 1);
				fm.setIndexEnd(sdSize - 1);
				break;
			}
		}

		BigDecimal stepOpenBal = openOSBalance;
		BigDecimal stepAmount = BigDecimal.ZERO;

		for (int i = 0; i < stepSize; i++) {
			FinanceStepPolicyDetail stepDetail = stepPolicyDetails.get(i);

			closeBalPercentage = closeBalPercentage.subtract(stepDetail.getEmiSplitPerc().divide(new BigDecimal(100)));
			fm.setCompareExpectedResult(round(openPrincipal.multiply(closeBalPercentage)));

			iTerms = 0;

			for (int j = (fm.getIndexStart()); j < sdSize; j++) {
				FinanceScheduleDetail curSchd = finSchdDetails.get(j);

				if (!curSchd.isRepayOnSchDate()) {
					continue;
				}

				iTerms = iTerms + 1;
				fm.setIndexEnd(j);

				if (iTerms != stepDetail.getInstallments()) {
					continue;
				}

				if (fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_EQUAL)) {
					stepAmount = approxPMT(fm, curSchd.getCalculatedRate(), iTerms, stepOpenBal,
							fm.getCompareExpectedResult(), 0);
				} else if (fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI_PFT)
						|| fm.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PRI)) {
					stepAmount = stepOpenBal.subtract(fm.getCompareExpectedResult()).divide(BigDecimal.valueOf(iTerms),
							0, RoundingMode.HALF_DOWN);
				}

				stepAmount = CalculationUtil.roundAmount(stepAmount, fm.getCalRoundingMode(), fm.getRoundingTarget());
				finScheduleData = fetchRepayCurRates(finScheduleData);
				fm.setMiscAmount(stepAmount);
				finScheduleData = targetPriOSBal(finScheduleData, iTerms, false);
				stepOpenBal = finScheduleData.getFinanceScheduleDetails().get(j).getClosingBalance();
				fm.setIndexStart(j + 1);

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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<RepayInstruction> repayInstructions = finScheduleData.getRepayInstructions();

		int risize = repayInstructions.size();

		Date evtFromDate = fm.getEventFromDate();
		Date evtToDate = fm.getEventToDate();

		String schdMethod = fm.getRecalSchdMethod();
		if (StringUtils.equals(schdMethod, PennantConstants.List_Select) || StringUtils.isBlank(schdMethod)) {
			for (int i = 0; i < risize; i++) {
				if (repayInstructions.get(i).getRepayDate().compareTo(evtFromDate) <= 0) {
					schdMethod = repayInstructions.get(i).getRepaySchdMethod();
				}

				if (StringUtils.isBlank(fm.getRecalSchdMethod())) {
					fm.setRecalSchdMethod(fm.getScheduleMethod());
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
		fm.setScheduleMaintained(true);

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

		FinanceMain fm = finScheduleData.getFinanceMain();

		for (int i = 0; i < sdSize; i++) {

			curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			schdDate = curSchd.getSchDate();

			if (TDSCalculator.isTDSApplicable(fm, curSchd) && tdsPerc.compareTo(BigDecimal.ZERO) != 0) {

				/*
				 * if (i < recalIdx) { continue; }
				 */

				boolean taxOnSysPerc = true;
				if (ltdApplicable) {
					BigDecimal tdsAmount = BigDecimal.ZERO;

					if (ltd == null || DateUtil.compare(schdDate, ltd.getEndDate()) > 0) {
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

		FinanceMain fm = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();

		BigDecimal calIntFraction = BigDecimal.ZERO;
		BigDecimal calInt = BigDecimal.valueOf(0.0);
		FinanceScheduleDetail prvSchd = null;

		// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);
		boolean cpzPOSIntact = fm.isCpzPosIntact();

		for (FinanceScheduleDetail curSchd : schdDetails) {

			// reset disbursement details
			setDisbursementDetails(curSchd, finScheduleData.getDisbursementDetails());

			// calculation not required for first record - Finance Start Date
			// Record
			if (DateUtil.compare(curSchd.getSchDate(), fm.getFinStartDate()) == 0) {

				if (fm.isChgDropLineSchd()) {
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
			if (fm.isChgDropLineSchd()) {

				dropLineLimit = curSchd.getClosingBalance();
				curSchd.setLimitDrop(curSchd.getPrincipalSchd());
				curSchd.setODLimit(dropLineLimit);

				curSchd.setSchdPriPaid(curSchd.getSchdPriPaid().add(curSchd.getPartialPaidAmt()));
			}

			// Profit Calculation
			curSchd.setBalanceForPftCal(prvSchd.getClosingBalance());

			if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {

				if (StringUtils.equals(roundAdjMth, "NO_ADJ") || (StringUtils.equals(roundAdjMth, "ADJ_LAST_INST")
						&& DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) != 0
						&& DateUtil.compare(curSchd.getSchDate(), fm.getMaturityDate()) != 0)) {

					calIntFraction = BigDecimal.ZERO;
				}

				calInt = CalculationUtil.calInterest(prvSchDate, curSchDate, curSchd.getBalanceForPftCal(),
						curSchd.getPftDaysBasis(), prvSchd.getCalculatedRate());

				calInt = calInt.add(calIntFraction);

				BigDecimal calIntRounded = BigDecimal.ZERO;
				if (calInt.compareTo(BigDecimal.ZERO) > 0) {
					calIntRounded = CalculationUtil.roundAmount(calInt, fm.getCalRoundingMode(),
							fm.getRoundingTarget());
				}
				calIntFraction = calInt.subtract(calIntRounded);
				calInt = calIntRounded;

				if (StringUtils.equals(roundAdjMth, "ADJ_LAST_INST")
						&& DateUtil.compare(prvSchDate, fm.getGrcPeriodEndDate()) != 0) {
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
			if (DateUtil.compare(curSchDate, fm.getGrcPeriodEndDate()) == 0) {
				calIntFraction = BigDecimal.ZERO;
				curSchd.setPftOnSchDate(true);
			}

			// 1. Principal Schedule : PrincipalSchd Calculation except for
			// Early Settlement
			if (!StringUtils.equals(fm.getReceiptPurpose(), FinServiceEvent.EARLYSETTLE)) {

				BigDecimal calPri = BigDecimal.ZERO;
				if (dropLineLimit.compareTo(prvSchd.getClosingBalance()) <= 0) {
					calPri = prvSchd.getClosingBalance().subtract(dropLineLimit);
				}
				curSchd.setPrincipalSchd(calPri.add(curSchd.getPartialPaidAmt()));
			} else {
				if (DateUtil.compare(curSchDate, fm.getEventFromDate()) > 0) {
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
					curSchd.setCpzOnSchDate(fm.isPlanEMICpz());
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
					curSchd.setCpzOnSchDate(fm.isReAgeCpz());
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
					curSchd.setCpzOnSchDate(fm.isUnPlanEMICpz());
				}
			}

			setCpzAmounts(prvSchd, curSchd, cpzPOSIntact, false);

			// ClosingBalance or Utilization
			curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, fm.getRepayRateBasis(), cpzPOSIntact));
			curSchd.setAvailableLimit(dropLineLimit.subtract(curSchd.getClosingBalance()));

			if (curSchDate.compareTo(fm.getMaturityDate()) == 0) {
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
		FinanceMain fm = finScheduleData.getFinanceMain();
		if (!fm.isAllowSubvention()) {
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
				if (DateUtil.compare(curSchd.getSchDate(), curDisb.getDisbDate()) <= 0) {
					continue;
				}

				// If Schedule date greater than Grace Period end date , not
				// consider.
				if (DateUtil.compare(curSchd.getSchDate(), fm.getGrcPeriodEndDate()) > 0) {
					break;
				}

				// If Schedule date greater than Subvention end date , not
				// consider.
				if (DateUtil.compare(curSchd.getSchDate(), subvention.getEndDate()) > 0) {
					break;
				}

				svSchd = new SubventionScheduleDetail();
				svSchd.setFinReference(fm.getFinReference());
				svSchd.setDisbSeqID(curDisb.getDisbSeq());
				svSchd.setSchDate(curSchd.getSchDate());
				svSchd.setNoOfDays(CalculationUtil.calNoOfDays(prvSchd.getSchDate(), curSchd.getSchDate(),
						curSchd.getPftDaysBasis()));

				// Calculated Rate for the Schedule
				BigDecimal calRate = prvSchd.getActRate();
				if (StringUtils.equals(FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL, subvention.getType())) {
					if (StringUtils.isNotBlank(prvSchd.getBaseRate())) {
						calRate = RateUtil.rates(prvSchd.getBaseRate(), fm.getFinCcy(), prvSchd.getSplRate(),
								prvSchd.getMrgRate(), prvSchd.getSchDate(), fm.getGrcMinRate(), fm.getGrcMaxRate())
								.getNetRefRateLoan();
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
								&& DateUtil.compare(curSchd.getSchDate(), subvention.getEndDate()) != 0)) {

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
					futureValRounded = CalculationUtil.roundAmount(futureVal, fm.getCalRoundingMode(),
							fm.getRoundingTarget());
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

					int yearOfDate = DateUtil.getYear(curSchd.getSchDate());
					if (DateUtil.isLeapYear(yearOfDate)) {
						daysBasis = 366;
					}
				} else if (StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_ACT_365LEAPS)) {

					int yearOfDate = DateUtil.getYear(prvSchd.getSchDate());
					if (DateUtil.isLeapYear(yearOfDate)) {
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

						int yearOfDate = DateUtil.getYear(prvSchd.getSchDate());
						if (DateUtil.isLeapYear(yearOfDate)) {
							daysBasis = 366;
						} else {
							yearOfDate = DateUtil.getYear(curSchd.getSchDate());
							if (DateUtil.isLeapYear(yearOfDate)) {
								daysBasis = 366;
							}
						}
					}
				} else if (StringUtils.equals(curSchd.getPftDaysBasis(), CalculationConstants.IDB_30E360IA)) {

					BigDecimal idb30Factor = BigDecimal.valueOf(30 / 360d).setScale(9);
					BigDecimal dayFactor = CalculationUtil.getInterestDays(prvSchd.getSchDate(), curSchd.getSchDate(),
							CalculationConstants.IDB_30E360IA);
					BigDecimal dayFactorScale = dayFactor.setScale(9);

					if (idb30Factor.compareTo(dayFactorScale) == 0) {
						daysBasis = 360;
					} else {
						daysBasis = 365;

						int yearOfDate = DateUtil.getYear(prvSchd.getSchDate());
						if (DateUtil.isLeapYear(yearOfDate)) {
							daysBasis = 366;
						} else {
							yearOfDate = DateUtil.getYear(curSchd.getSchDate());
							if (DateUtil.isLeapYear(yearOfDate)) {
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
					closingBalRounded = CalculationUtil.roundAmount(closingBal, fm.getCalRoundingMode(),
							fm.getRoundingTarget());
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
				discPft = CalculationUtil.roundAmount(discPft, fm.getCalRoundingMode(), fm.getRoundingTarget());
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

					if (DateUtil.compare(maturityDate, schDetail.getSchDate()) == 0) {

						if (!FrequencyUtil.isFrqDate(fm.getRepayFrq(), schDetail.getSchDate())) {

							List<Calendar> scheduleDatesList = FrequencyUtil.getNextDate(fm.getRepayFrq(),
									fm.getAdjTerms(), maturityDate, HolidayHandlerTypes.MOVE_NONE, false)
									.getScheduleList();

							Date lastFrqDate = null;
							if (CollectionUtils.isNotEmpty(scheduleDatesList)) {
								Calendar calendar = scheduleDatesList.get(scheduleDatesList.size() - 1);
								lastFrqDate = DateUtil.getDatePart(calendar.getTime());
							}

							Calendar calLastFrqDate = Calendar.getInstance();
							calLastFrqDate.setTime(lastFrqDate);

							int day = DateUtil.getDay(maturityDate);
							int maxdays = calLastFrqDate.getActualMaximum(Calendar.DAY_OF_MONTH);

							if (day > maxdays) {
								calLastFrqDate.set(calLastFrqDate.get(Calendar.YEAR),
										calLastFrqDate.get(Calendar.MONTH), maxdays);
							} else {
								calLastFrqDate.set(calLastFrqDate.get(Calendar.YEAR),
										calLastFrqDate.get(Calendar.MONTH), day);
							}

							newMDT = DateUtil.getDatePart(calLastFrqDate.getTime());
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

				if (DateUtil.compare(maturityDate, schDetail.getSchDate()) == 0) {

					if (!FrequencyUtil.isFrqDate(fm.getRepayFrq(), schDetail.getSchDate())) {

						List<Calendar> scheduleDatesList = FrequencyUtil.getNextDate(fm.getRepayFrq(), fm.getAdjTerms(),
								maturityDate, HolidayHandlerTypes.MOVE_NONE, false).getScheduleList();

						Date lastFrqDate = null;
						if (CollectionUtils.isNotEmpty(scheduleDatesList)) {
							Calendar calendar = scheduleDatesList.get(scheduleDatesList.size() - 1);
							lastFrqDate = DateUtil.getDatePart(calendar.getTime());
						}

						Calendar calLastFrqDate = Calendar.getInstance();
						calLastFrqDate.setTime(lastFrqDate);

						int day = DateUtil.getDay(maturityDate);
						int maxdays = calLastFrqDate.getActualMaximum(Calendar.DAY_OF_MONTH);

						if (day > maxdays) {
							calLastFrqDate.set(calLastFrqDate.get(Calendar.YEAR), calLastFrqDate.get(Calendar.MONTH),
									maxdays);
						} else {
							calLastFrqDate.set(calLastFrqDate.get(Calendar.YEAR), calLastFrqDate.get(Calendar.MONTH),
									day);
						}

						Date derivedMaturityDate = DateUtil.getDatePart(calLastFrqDate.getTime());
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
	 * Method to get the EMI amount on total loan amount used only in agreements/email templates
	 * 
	 */
	public static BigDecimal getEMIOnFinAssetValue(FinScheduleData fsData) {
		logger.debug(Literal.ENTERING);
		BigDecimal emi = BigDecimal.ZERO;
		try {
			// Prepare Finance Schedule Generator Details List
			FinScheduleData fsDataCopy = fsData.copyEntity();
			FinanceMain fm = fsDataCopy.getFinanceMain();

			fsDataCopy.setRepayInstructions(new ArrayList<RepayInstruction>());
			fsDataCopy.setFinanceScheduleDetails(new ArrayList<FinanceScheduleDetail>());
			fsDataCopy.setDisbursementDetails(new ArrayList<FinanceDisbursement>());

			// Set Disbursement Details with total loan amount
			FinanceDisbursement fddCopy = new FinanceDisbursement();
			fddCopy.setDisbDate(fm.getFinStartDate());
			fddCopy.setDisbSeq(1);
			fddCopy.setDisbAmount(fm.getFinAssetValue());
			fddCopy.setDisbReqDate(fddCopy.getDisbDate());
			fddCopy.setFeeChargeAmt(fm.getFeeChargeAmt());
			fddCopy.setQuickDisb(fm.isQuickDisb());
			fsDataCopy.getDisbursementDetails().add(fddCopy);

			fsDataCopy = ScheduleGenerator.getNewSchd(fsDataCopy);
			fsDataCopy = getCalSchd(fsDataCopy, null);

			if (CollectionUtils.isNotEmpty(fsDataCopy.getPlanEMIHmonths())) {
				fsDataCopy.setPlanEMIHmonths(fsDataCopy.getPlanEMIHmonths());
				fsDataCopy = ScheduleCalculator.getFrqEMIHoliday(fsDataCopy);
			}

			if (null != fsDataCopy) {
				List<FinanceScheduleDetail> fsdListCopy = fsDataCopy.getFinanceScheduleDetails();
				if (CollectionUtils.isNotEmpty(fsdListCopy)) {
					for (FinanceScheduleDetail fsdCopy : fsdListCopy) {
						if (fsdCopy.isRepayOnSchDate() && fsdCopy.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
							if (emi.compareTo(fsdCopy.getRepayAmount()) == 0) {
								break;
							}
							emi = fsdCopy.getRepayAmount();
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
	private void prepareManualRepayInstructions(FinScheduleData fsData, boolean isFirstRun) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fsData.getFinanceMain();
		List<FinanceStepPolicyDetail> spdList = fsData.getStepPolicyDetails();
		List<FinanceScheduleDetail> schedules = fsData.getFinanceScheduleDetails();

		fsData.setStepPolicyDetails(fsData.getStepPolicyDetails(), true);
		spdList = fsData.getStepPolicyDetails();

		int idxStart = 0;
		int riStart = 0;
		int riEnd = 0;
		String schdMethod = fm.getScheduleMethod();
		String grcSchdMethod = fm.getGrcSchdMthd();
		// PSD#170539 BPI amount is capitalized and adding to POS because of schedule method, issue fixed.
		boolean bpiFound = false;
		int rpyStpTerms = 0;
		int grcStpTerms = 0;
		int rpyTerms = 0;
		int grcTerms = 0;
		int nonFrqTerms = 0;

		for (FinanceStepPolicyDetail spd : spdList) {
			if (PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
				fm.setGrcStps(true);
				idxStart = 1;
				grcStpTerms = grcStpTerms + spd.getInstallments();
			} else {
				rpyStpTerms = rpyStpTerms + spd.getInstallments();
			}
		}

		if (!fm.isGrcStps()) {
			for (FinanceScheduleDetail schd : schedules) {
				if (FinanceConstants.FLAG_BPI.equals(schd.getBpiOrHoliday())
						|| schd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) <= 0) {
					idxStart = idxStart + 1;
				}

				if (!schd.isRepayOnSchDate() && !schd.isPftOnSchDate() && schd.isRvwOnSchDate()) {
					continue;
				}

				if (!schd.isRepayOnSchDate() || !schd.isFrqDate()) {
					if (rpyTerms == 0 && schd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
						idxStart = idxStart + 1;
					}
					continue;
				}

				if (schd.isFrqDate() && DateUtil.compare(schd.getSchDate(), fm.getGrcPeriodEndDate()) > 0) {
					rpyTerms = rpyTerms + 1;
				}
			}
		} else {
			idxStart = 0;
			boolean resetStartIdx = true;
			for (FinanceScheduleDetail schd : schedules) {
				if (schd.isFrqDate() && DateUtil.compare(schd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
					grcTerms = grcTerms + 1;
				}

				if (resetStartIdx && DateUtil.compare(schd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
					if (!schd.isFrqDate()) {
						idxStart = idxStart + 1;
					} else {
						resetStartIdx = false;
					}
				}

				if (FinanceConstants.FLAG_BPI.equals(schd.getBpiOrHoliday())) {
					bpiFound = false;
					Date schDate = schd.getSchDate();
					if (fm.isAllowGrcPeriod()) {
						setRpyInstructDetails(fsData, schDate, schDate, BigDecimal.ZERO, grcSchdMethod);
					} else {
						setRpyInstructDetails(fsData, schDate, schDate, BigDecimal.ZERO, schdMethod);
					}
				}

				if (bpiFound && schd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) > 0) {
					break;
				}
			}
		}

		boolean grcEnd = false;

		if (fm.isGrcStps() && grcStpTerms != 0 && grcTerms != grcStpTerms) {
			idxStart = 1;
			int remGrcTerms = grcTerms - grcStpTerms;
			for (int i = 0; i < remGrcTerms; i++) {
				idxStart = idxStart + 1;
			}
			FinanceScheduleDetail fsd = schedules.get(idxStart);
			for (FinanceScheduleDetail fsDetail : schedules) {
				if (fsDetail.getSchDate().compareTo(fsd.getSchDate()) <= 0 && !fsDetail.isFrqDate()) {
					nonFrqTerms = nonFrqTerms + 1;
				} else if (fsDetail.getSchDate().compareTo(fsd.getSchDate()) > 0) {
					break;
				}
			}

			if (nonFrqTerms > 0) {
				// Already first disbursement date included in idxStart so minus 1 term.
				nonFrqTerms = nonFrqTerms - 1;
			}
			idxStart = idxStart + nonFrqTerms;
		}

		if (!fm.isGrcStps() && rpyStpTerms != 0 && rpyTerms != rpyStpTerms) {
			idxStart = 1;
			int remRpyTerms = rpyTerms - rpyStpTerms;
			for (int i = 0; i < remRpyTerms; i++) {
				idxStart = idxStart + 1;
			}
			FinanceScheduleDetail fsd = schedules.get(idxStart);
			for (FinanceScheduleDetail fsDetail : schedules) {
				if (fsDetail.getSchDate().compareTo(fsd.getSchDate()) <= 0 && !fsDetail.isFrqDate()) {
					nonFrqTerms = nonFrqTerms + 1;
				} else if (fsDetail.getSchDate().compareTo(fsd.getSchDate()) > 0) {
					break;
				}
			}

			if (nonFrqTerms > 0) {
				// Already first disbursement date included in idxStart so minus 1 term.
				nonFrqTerms = nonFrqTerms - 1;
			}
			idxStart = idxStart + nonFrqTerms;
		}

		boolean recalLastStep = true;

		for (int i = 0; i < spdList.size(); i++) {

			FinanceStepPolicyDetail spd = spdList.get(i);
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
			boolean setStepStart = true;
			for (int iFsd = idxStart; iFsd < schedules.size(); iFsd++) {
				FinanceScheduleDetail fsd = schedules.get(iFsd);
				// Part payment installment is also considering for installment count so added isFrqDate condition.
				String specifier = fsd.getSpecifier();
				if (fsd.isRepayOnSchDate() && fsd.isFrqDate() && !(isBPIHoliday(fsd.getBpiOrHoliday()))) {
					instCount = instCount + 1;
				} else if (iFsd != 0 && PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())
						&& !(FinanceConstants.FLAG_BPI.equals(fsd.getBpiOrHoliday()))
						&& (CalculationConstants.SCH_SPECIFIER_GRACE.equals(specifier)
								|| CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier))
						&& fsd.isFrqDate()) {
					instCount = instCount + 1;
				}

				if (instCount == 1 && setStepStart) {
					spd.setStepStart(fsd.getSchDate());
					setStepStart = false;
				}

				// iFsd == riEnd
				if (spd.getInstallments() == instCount) {
					if (CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier)) {
						grcEnd = true;
					}
					spd.setStepEnd(fsd.getSchDate());
					idxStart = iFsd + 1;

					idxStart = getRINextIndex(schedules, idxStart, spd, iFsd);

					break;
				}
			}
			boolean resetLastRpyInst = true;
			if ((fm.isStepRecalOnProrata() || FinServiceEvent.RESCHD.equals(fm.getProcMethod()))
					&& spd.getSteppedEMI().compareTo(BigDecimal.ZERO) == 0 && spdList.size() - 1 == i) {
				resetLastRpyInst = false;
			}
			if (resetLastRpyInst) {
				if (!spd.isAutoCal()) {

					Date startDate = spd.getStepStart();
					if (DateUtil.compare(fm.getRecalFromDate(), spd.getStepStart()) > 0) {
						startDate = fm.getRecalFromDate();
					}
					if (DateUtil.compare(fm.getRecalFromDate(), spd.getStepEnd()) > 0) {
						continue;
					}

					BigDecimal rpyAmount = BigDecimal.ZERO;
					String rpySchdMthd = null;
					for (RepayInstruction repayInstruction : fsData.getRepayInstructions()) {
						if (repayInstruction.getRepayDate().compareTo(startDate) > 0) {
							break;
						}
						rpyAmount = repayInstruction.getRepayAmount();
						rpySchdMthd = repayInstruction.getRepaySchdMethod();
					}

					// Condition verification to avoid duplicate repay instructions without checking previous RI
					if (rpyAmount.compareTo(spd.getSteppedEMI()) == 0 && StringUtils.equals(rpySchdMthd, schdMethod)) {
						continue;
					}

					setRpyInstructDetails(fsData, startDate, spd.getStepEnd(), spd.getSteppedEMI(), schdMethod);
				} else {
					fm.setEqualRepay(true);
					setRpyInstructDetails(fsData, fm.getRecalFromDate(), spd.getStepEnd(), BigDecimal.ZERO,
							grcSchdMethod);
				}
			} else {
				Date startDate = spd.getStepStart();
				if (DateUtil.compare(fm.getRecalFromDate(), spd.getStepStart()) > 0) {
					startDate = fm.getRecalFromDate();
				}
				boolean rpyInstFound = false;
				BigDecimal rpyAmount = BigDecimal.ZERO;

				for (RepayInstruction repayInstruction : fsData.getRepayInstructions()) {
					if (repayInstruction.getRepayDate().compareTo(startDate) <= 0) {
						if (repayInstruction.getRepayDate().compareTo(startDate) == 0) {
							repayInstruction
									.setRepayAmount(repayInstruction.getRepayAmount().add(spd.getStepDiffEMI()));
							rpyInstFound = true;
							fm.setEqualRepay(false);
							recalLastStep = false;
							break;
						}
						rpyAmount = repayInstruction.getRepayAmount();
					}
				}
				if (!rpyInstFound) {
					fm.setEqualRepay(false);
					recalLastStep = false;
					setRpyInstructDetails(fsData, startDate, spd.getStepEnd(), rpyAmount.add(spd.getStepDiffEMI()),
							schdMethod);
				}
			}
		}
		FinanceStepPolicyDetail spd = spdList.get(spdList.size() - 1);

		if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(spd.getStepSpecifier()) && recalLastStep) {
			fm.setRecalFromDate(spd.getStepStart());
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			fm.setCalculateRepay(true);
			fm.setEqualRepay(true);
			fm.setRecalSchdMethod(schdMethod);
			setRpyInstructDetails(fsData, spd.getStepStart(), fm.getMaturityDate(), BigDecimal.ZERO, schdMethod);
		}

		logger.debug(Literal.LEAVING);
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
			fsData = restructureEMIRecal(fsData, !fsData.getFinanceMain().isStepFinance());
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

	private FinScheduleData restructureEMIRecal(FinScheduleData schdData, boolean isEqualRepay) {
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		RestructureDetail rstDetail = schdData.getRestructureDetail();
		FinanceMain fm = schdData.getFinanceMain();
		int idxHldEnd = -1;
		Date HldStart = rstDetail.getRestructureDate();

		if (rstDetail.getEmiHldPeriod() > 0) {
			idxHldEnd = buildRestructureEMIHLD(schdData, HldStart);
			HldStart = schedules.get(idxHldEnd + 1).getSchDate();
		}

		// HldStart = fsdList.get(idxHldEnd).getSchDate();

		if (rstDetail.getPriHldPeriod() > 0) {
			if (idxHldEnd < 0) {
				idxHldEnd = 1;
			}
			idxHldEnd = buildRestructurePRIHLD(schdData, HldStart, idxHldEnd);
		}

		if (fm.getEventFromDate() == null) {
			fm.setEventFromDate(rstDetail.getRestructureDate());
			for (FinanceScheduleDetail fsd : schedules) {
				if (fsd.isFrqDate() && DateUtil.compare(fsd.getSchDate(), rstDetail.getRestructureDate()) >= 0) {
					fm.setRecalFromDate(fsd.getSchDate());
					break;
				}
			}
		}

		fm.setEventToDate(fm.getMaturityDate());
		fm.setRecalToDate(fm.getMaturityDate());

		if (idxHldEnd < 1) {
			setRpyInstructDetails(schdData, fm.getRecalFromDate(), fm.getMaturityDate(), BigDecimal.ZERO,
					fm.getRecalSchdMethod());
		}

		if (isEqualRepay) {
			fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
		} else {
			if (!fm.isStepFinance()) {
				fm.setRecalType(CalculationConstants.RPYCHG_ADJTERMS);
			}
		}

		fm.setEqualRepay(isEqualRepay);
		fm.setCalculateRepay(isEqualRepay);

		schdData = calSchdProcess(schdData, false, false);

		return schdData;
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

			if (!fsd.isFrqDate()) {
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
		lastDateLimit = DateUtil.addYears(fm.getFinStartDate(), maxFinYears);

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
		lastDateLimit = DateUtil.addYears(fm.getFinStartDate(), maxFinYears);

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

			if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
				if (fsd.getSchDate().compareTo(rstDetail.getRestructureDate()) == 0) {
					continue;
				}
			}

			// Mark Capitalize
			if (fsd.isFrqDate() && (fsd.isRepayOnSchDate() || fsd.isPftOnSchDate())) {
				fsd.setCpzOnSchDate(true);
				fsd.setBpiOrHoliday(FinanceConstants.FLAG_RESTRUCTURE_PRIH);

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

		// fsData.getFinanceMain().setRecalIdx(idxHldStart);
		fsData.getFinanceMain().setEventFromDate(rstDetail.getRestructureDate());

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

			if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
				if (fsd.getSchDate().compareTo(rstDetail.getRestructureDate()) == 0) {
					continue;
				}
			}

			// Mark Capitalize
			if (fsd.isFrqDate() && (fsd.isRepayOnSchDate() || fsd.isPftOnSchDate())) {
				fsd.setCpzOnSchDate(true);
				fsd.setBpiOrHoliday(FinanceConstants.FLAG_RESTRUCTURE);

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
			// fsData.getFinanceMain().setRecalIdx(idxHldStart);
			fsData.getFinanceMain().setEventFromDate(rstDetail.getRestructureDate());
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

	public static Date getFirstInstallmentDate(List<FinanceScheduleDetail> schedules) {
		for (FinanceScheduleDetail schedule : schedules) {
			BigDecimal repayAmt = BigDecimal.ZERO;
			repayAmt = repayAmt.add(schedule.getProfitSchd());
			repayAmt = repayAmt.add(schedule.getPrincipalSchd());
			repayAmt = repayAmt.subtract(schedule.getPartialPaidAmt());

			if (repayAmt.compareTo(BigDecimal.ZERO) > 0) {
				return schedule.getSchDate();
			}
		}

		return null;
	}

	private void setExpectedEndBal(FinScheduleData fsData) {
		fsData.getFinanceMain().setExpectedEndBal(BigDecimal.ZERO);

		String recalType = fsData.getFinanceMain().getRecalType();
		if (StringUtils.isBlank(recalType)) {
			return;
		}

		if (!CalculationConstants.RPYCHG_CURPRD.equals(recalType)
				&& !CalculationConstants.RPYCHG_TILLDATE.equals(recalType)) {
			return;
		}

		Date recalToDate = fsData.getFinanceMain().getRecalToDate();

		List<FinanceScheduleDetail> fsdList = fsData.getFinanceScheduleDetails();
		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);
			if (DateUtil.compare(fsd.getSchDate(), recalToDate) < 0) {
				continue;
			}

			if (DateUtil.compare(fsd.getSchDate(), recalToDate) == 0) {
				fsData.getFinanceMain().setExpectedEndBal(fsd.getClosingBalance());
			} else {
				FinanceScheduleDetail prvSchd = fsdList.get(iFsd - 1);
				fsData.getFinanceMain().setExpectedEndBal(prvSchd.getClosingBalance());
			}

			break;
		}

	}

	private void adjustTDTSchedule(FinanceScheduleDetail curSchd, FinanceMain fm, FinanceScheduleDetail prvSchd) {
		logger.debug(Literal.ENTERING);

		curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, fm.isCpzPosIntact(), fm, true));
		curSchd.setPrincipalSchd(curSchd.getRepayAmount().subtract(curSchd.getProfitSchd()));
		curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
		curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, fm.getRepayRateBasis(), fm.isCpzPosIntact()));

		logger.debug(Literal.LEAVING);
	}

	public void procTDTRecord(FinScheduleData schdData, int i, boolean cpzPOSIntact) {
		logger.debug(Literal.ENTERING);

		FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(i);
		FinanceScheduleDetail prvSchd = schdData.getFinanceScheduleDetails().get(i - 1);

		FinanceMain fm = schdData.getFinanceMain();
		String repayRateBasis = fm.getRepayRateBasis();

		curSchd.setProfitSchd(calProfitToSchd(curSchd, prvSchd, cpzPOSIntact, fm, true));
		curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, cpzPOSIntact));
		curSchd.setRepayAmount(curSchd.getPrincipalSchd().add(curSchd.getProfitSchd()));
		curSchd.setPrincipalSchd(prvSchd.getClosingBalance().subtract(fm.getExpectedEndBal()));
		curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, repayRateBasis, cpzPOSIntact));

		logger.debug(Literal.LEAVING);
	}

	private FinScheduleData buildPriHoliday(FinScheduleData schdData) {
		FinanceMain fm = schdData.getFinanceMain();

		schdData = setPrincipalHolidayTenor(schdData, false);

		if (schdData.getErrorDetails().size() > 0) {
			return schdData;
		}

		int idxHldEnd = -1;
		Date HldStart = fm.getRecalFromDate();

		if (fm.getNoOfPrincipalHdays() > 0) {
			if (idxHldEnd < 0) {
				idxHldEnd = 1;
			}

			idxHldEnd = getNoOfPrincipalHdays(schdData, HldStart, idxHldEnd);
		}

		fm.setEventToDate(fm.getMaturityDate());
		fm.setRecalToDate(fm.getMaturityDate());

		if (idxHldEnd < 1) {
			setRpyInstructDetails(schdData, fm.getRecalFromDate(), fm.getMaturityDate(), BigDecimal.ZERO,
					fm.getRecalSchdMethod());
		}

		fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		fm.setEqualRepay(!schdData.getFinanceMain().isStepFinance());
		fm.setCalculateRepay(!schdData.getFinanceMain().isStepFinance());

		schdData = calSchdProcess(schdData, false, false);

		return schdData;

	}

	private int getNoOfPrincipalHdays(FinScheduleData schdData, Date HldStart, int idxRef) {
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		FinanceMain fm = schdData.getFinanceMain();
		int fsdSize = schedules.size();
		int idxHldStart = -1;
		int idxHldEnd = -1;
		int hldPeriods = 0;

		for (int iFsd = idxRef; iFsd < fsdSize; iFsd++) {
			FinanceScheduleDetail fsd = schedules.get(iFsd);

			if (fsd.getSchDate().compareTo(HldStart) < 0) {
				continue;
			}

			if (fsd.getSchDate().compareTo(fm.getRecalFromDate()) < 0) {
				continue;
			}

			if ((fsd.isRepayOnSchDate() || fsd.isPftOnSchDate())) {
				fsd.setBpiOrHoliday(FinanceConstants.FLAG_RESTRUCTURE_PRIH);

				hldPeriods = hldPeriods + 1;

				if (idxHldStart < 0) {
					idxHldStart = iFsd;
				}

				idxHldEnd = iFsd;
			}

			if (hldPeriods == fm.getNoOfPrincipalHdays()) {
				break;
			}
		}

		for (int iFsd = (idxHldEnd + 1); iFsd < fsdSize; iFsd++) {
			FinanceScheduleDetail fsd = schedules.get(iFsd);

			if (fsd.isFrqDate() && fsd.isRepayOnSchDate()) {
				schdData.getFinanceMain().setRecalFromDate(fsd.getSchDate());
				break;
			}
		}

		return idxHldEnd;
	}

	private FinScheduleData setPrincipalHolidayTenor(FinScheduleData schdData, boolean isTenorAdjust) {
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		FinanceMain fm = schdData.getFinanceMain();
		Date grcEndDate = fm.getGrcPeriodEndDate();
		Date rstDate = fm.getRecalFromDate();

		int reqTerms = 0;
		reqTerms = fm.getNoOfPrincipalHdays();

		int availableTerms = 0;

		for (FinanceScheduleDetail fsd : schedules) {
			if (fsd.getSchDate().compareTo(grcEndDate) <= 0) {
				continue;
			}

			if (fsd.getSchDate().compareTo(rstDate) < 0) {
				continue;
			}

			if (!fsd.isFrqDate()) {
				continue;
			}

			// If exsitng periods are more than newly required periods remove
			// excee periods
			if (availableTerms < reqTerms) {
				availableTerms = availableTerms + 1;
				continue;
			} else {

			}
		}

		// if existing periods are less than required periods then add new
		// periods
		reqTerms = reqTerms - availableTerms + fm.getNoOfPrincipalHdays();

		if (reqTerms <= 0) {
			return schdData;
		}

		// Set the limits based on system values table
		int maxFinYears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS");
		Date lastDateLimit = new Date();
		lastDateLimit = DateUtil.addYears(fm.getFinStartDate(), maxFinYears);

		for (int i = 0; i < reqTerms; i++) {
			schdData = addOneTerm(schdData, lastDateLimit, true);
			if (schdData.getErrorDetails().size() > 0) {
				return schdData;
			}
		}

		schdData.getFinanceScheduleDetails().get(schdData.getFinanceScheduleDetails().size() - 1)
				.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
		return schdData;
	}
	// START : Manual Schedule Changes

	/**
	 * Re generate the Manual Schedule
	 * 
	 * @param orgFinScheduleData
	 * @return
	 */
	private FinScheduleData procGetCalManualSchd(FinScheduleData orgFinScheduleData) {
		Cloner cloner = new Cloner();
		FinScheduleData finScheduleData = cloner.deepClone(orgFinScheduleData);

		finScheduleData.getScheduleMap().clear();
		finScheduleData.getFinanceScheduleDetails().clear();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		finMain.setRateChange(true);

		finMain.setEventFromDate(finMain.getFinStartDate());
		finMain.setEventToDate(finMain.getMaturityDate());
		finMain.setRecalFromDate(finMain.getFinStartDate());
		finMain.setRecalToDate(finMain.getMaturityDate());

		// Prepare Disbursement Schedule
		finScheduleData = prepareManualSchdDisbRcd(finScheduleData);

		// Schedule preparation and Calculation
		finScheduleData = calManualSchdProcess(finScheduleData, false, FinServiceEvent.ORG);
		finMain.setRateChange(false);

		return finScheduleData;
	}

	/**
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData prepareManualSchdDisbRcd(FinScheduleData finScheduleData) {
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		// Disbursement Details
		for (int i = 0; i < finScheduleData.getDisbursementDetails().size(); i++) {

			FinanceDisbursement disbursementDetail = finScheduleData.getDisbursementDetails().get(i);
			FinanceScheduleDetail schedule = new FinanceScheduleDetail();

			BigDecimal prvTermDisbAmount = BigDecimal.ZERO;
			if (finScheduleData.getScheduleMap() != null
					&& finScheduleData.getScheduleMap().containsKey(disbursementDetail.getDisbDate())) {

				prvTermDisbAmount = finScheduleData.getScheduleMap().get(disbursementDetail.getDisbDate())
						.getDisbAmount();
			}

			schedule.setDisbOnSchDate(true);
			schedule.setSchDate(disbursementDetail.getDisbDate());
			schedule.setDefSchdDate(disbursementDetail.getDisbDate());
			schedule.setDisbAmount(disbursementDetail.getDisbAmount().add(prvTermDisbAmount));
			schedule.setFeeChargeAmt(disbursementDetail.getFeeChargeAmt());

			if (i == 0) {
				if (financeMain.getDownPayment() != null
						&& financeMain.getDownPayment().compareTo(BigDecimal.ZERO) != 0) {

					schedule.setDownPaymentAmount(financeMain.getDownPayment());
					schedule.setDownpaymentOnSchDate(true);
				}
			}

			finScheduleData.getFinanceScheduleDetails().add(schedule);
		}

		return finScheduleData;
	}

	/**
	 * 
	 * @param finScheduleData
	 * @return
	 */
	private FinScheduleData calManualSchdProcess(FinScheduleData finScheduleData, boolean isDelFutureSchd,
			String event) {
		logger.debug(Literal.ENTERING);

		FinanceMain finMain = finScheduleData.getFinanceMain();
		Date evtFromDate = finMain.getEventFromDate();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		int recalIdx = finMain.getRecalIdx();
		if (recalIdx < 0) {
			finScheduleData = setRecalIndex(finScheduleData);

			if (finMain.getRecalIdx() < 0) {
				finMain.setRecalIdx(0);
			}
			recalIdx = finMain.getRecalIdx();
		}

		/* Delete all future schedules after EventFromDate */
		if (isDelFutureSchd) {
			for (int i = recalIdx; i < finSchdDetails.size(); i++) {

				// PSD ID : 138168
				FinanceScheduleDetail curSchd = finSchdDetails.get(i);
				if (DateUtil.compare(curSchd.getSchDate(), evtFromDate) > 0 && curSchd.getPresentmentId() == 0
						&& !(curSchd.getDisbAmount().compareTo(BigDecimal.ZERO) > 0
								|| curSchd.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0)) {

					finSchdDetails.remove(i);
					i--;
				}
			}
		}

		for (int i = recalIdx; i < finSchdDetails.size(); i++) {

			FinanceScheduleDetail curSchd = finSchdDetails.get(i);

			if (curSchd.isRepayOnSchDate() && curSchd.isPftOnSchDate()) {
				curSchd.setSchdMethod(CalculationConstants.SCHMTHD_PRI_PFT);
			} else if (curSchd.isRepayOnSchDate()) {
				curSchd.setSchdMethod(CalculationConstants.SCHMTHD_PRI);
			} else if (curSchd.isPftOnSchDate()) {
				curSchd.setSchdMethod(CalculationConstants.SCHMTHD_PFT);
			}
		}

		// Receipt Changes
		if (FinServiceEvent.RECEIPT.equals(finMain.getProcMethod())
				&& FinServiceEvent.EARLYRPY.equals(finMain.getReceiptPurpose())) {

			for (int i = recalIdx; i < finSchdDetails.size(); i++) {

				FinanceScheduleDetail curSchd = finSchdDetails.get(i);

				// set PartialPaid Amount
				if (DateUtil.compare(curSchd.getSchDate(), evtFromDate) == 0) {
					curSchd.setPrincipalSchd(curSchd.getPrincipalSchd().add(finMain.getEarlyPayAmount()));
					break;
				}
			}
		}

		// If first record no calculation is required
		finScheduleData = prepareFirstGraceRcd(finScheduleData);

		/* Prepare Upload Manual Schedule Details */
		ManualScheduleHeader scheduleHeader = finScheduleData.getManualScheduleHeader();
		if (scheduleHeader != null) {
			List<ManualScheduleDetail> manualSchdList = scheduleHeader.getManualSchedules();

			if (CollectionUtils.isNotEmpty(manualSchdList)) {

				ManualScheduleDetail maturityManSchd = manualSchdList.get(manualSchdList.size() - 1);
				finMain.setMaturityDate(maturityManSchd.getSchDate());

				for (ManualScheduleDetail manualSchedule : manualSchdList) {
					finScheduleData = addManualSchdRcd(finScheduleData, manualSchedule, event);
				}
				finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			}
		}

		// Rates
		finScheduleData = fetchRatesHistory(finScheduleData);
		finScheduleData = fetchRepayCurRates(finScheduleData);

		/* Repay Schedule calculation */
		finScheduleData = repaySchdCal(finScheduleData, false);
		setFinanceTotals(finScheduleData);

		// TODO : Prepare Repay instructions
		finScheduleData = setRpyInstructDetails(finScheduleData, finMain.getNextRepayPftDate(),
				finMain.getMaturityDate(), BigDecimal.ZERO, finMain.getScheduleMethod());

		finScheduleData.getFinanceMain().setScheduleMaintained(true);

		logger.debug(Literal.LEAVING);
		return finScheduleData;
	}

	/**
	 * Schedules Preparation
	 * 
	 * @param finScheduleData
	 * @param i
	 */
	private FinScheduleData addManualSchdRcd(FinScheduleData finScheduleData, ManualScheduleDetail manualSchdDetail,
			String finEvent) {

		FinanceScheduleDetail schedule = new FinanceScheduleDetail();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		FinanceScheduleDetail prvSchd = finScheduleData.getFinanceScheduleDetails()
				.get(finScheduleData.getFinanceScheduleDetails().size() - 1);

		schedule.setSchSeq(1);
		// sandeep
		schedule.setFrqDate(true);
		schedule.setBpiOrHoliday("");
		schedule.setPftDaysBasis(finMain.getProfitDaysBasis());
		schedule.setFinReference(finMain.getFinReference());

		// Prepare First SchdRcd
		if (DateUtil.compare(finMain.getFinStartDate(), prvSchd.getSchDate()) == 0
				&& FinServiceEvent.ORG.equals(finEvent)) {

			prvSchd.setTDSApplicable(finMain.isTDSApplicable());
			prvSchd.setActRate(finMain.getRepayProfitRate());
			prvSchd.setBaseRate(finMain.getRepayBaseRate());
			prvSchd.setCalculatedRate(finMain.getRepayProfitRate());
			prvSchd.setSplRate(finMain.getRepaySpecialRate());
			prvSchd.setMrgRate(finMain.getRepayMargin());
		}

		schedule.setTDSApplicable(prvSchd.isTDSApplicable());
		schedule.setBaseRate(prvSchd.getBaseRate());
		schedule.setSplRate(prvSchd.getSplRate());
		schedule.setMrgRate(prvSchd.getMrgRate());
		schedule.setActRate(prvSchd.getActRate());
		schedule.setCalculatedRate(prvSchd.getCalculatedRate());
		schedule.setOrgEndBal(prvSchd.getOrgEndBal());

		schedule.setNoOfDays(DateUtil.getDaysBetween(manualSchdDetail.getSchDate(), prvSchd.getSchDate()));
		schedule.setDayFactor(CalculationUtil.getInterestDays(prvSchd.getSchDate(), manualSchdDetail.getSchDate(),
				prvSchd.getPftDaysBasis()));

		if (manualSchdDetail.getSchDate().compareTo(finMain.getMaturityDate()) == 0) {
			schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
		} else {
			schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
		}

		// Set UploadMaualSchedule Data
		schedule.setSchDate(manualSchdDetail.getSchDate());
		schedule.setDefSchdDate(manualSchdDetail.getSchDate());
		schedule.setPrincipalSchd(manualSchdDetail.getPrincipalSchd());

		if (manualSchdDetail.isPftOnSchDate()) {
			schedule.setPftOnSchDate(true);
		}

		// TODO : Rate View on MaturityDate, Validate once
		if (manualSchdDetail.isRvwOnSchDate()
				&& manualSchdDetail.getSchDate().compareTo(finMain.getMaturityDate()) != 0) {
			schedule.setRvwOnSchDate(true);
		}

		if (manualSchdDetail.getPrincipalSchd().compareTo(BigDecimal.ZERO) > 0) {
			schedule.setRepayOnSchDate(true);
		}

		// TODO : Schedule Method
		if (schedule.isRepayOnSchDate() && schedule.isPftOnSchDate()) {
			schedule.setSchdMethod(CalculationConstants.SCHMTHD_PRI_PFT);
		} else if (schedule.isRepayOnSchDate()) {
			schedule.setSchdMethod(CalculationConstants.SCHMTHD_PRI);
		} else if (schedule.isPftOnSchDate()) {
			schedule.setSchdMethod(CalculationConstants.SCHMTHD_PFT);
		}

		finScheduleData.getFinanceScheduleDetails().add(schedule);
		return finScheduleData;
	}

	// END : Manual Schedule Changes

	private FinScheduleData buildVariableOdSchedule(FinScheduleData schdData, BigDecimal totalOSLimit,
			OverdraftScheduleDetail prvODSchd, List<OverdraftScheduleDetail> oldOverdraftList) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();

		VariableOverdraftSchdHeader header = schdData.getVariableOverdraftSchdHeader();

		if (header == null) {
			return schdData;
		}

		List<VariableOverdraftSchdDetail> Details = header.getVariableOverdraftSchdDetails();

		if (CollectionUtils.isEmpty(Details)) {
			return schdData;
		}

		// Setting Overdraft Schedule details
		for (int i = 0; i < Details.size(); i++) {
			VariableOverdraftSchdDetail odSchd = Details.get(i);

			OverdraftScheduleDetail schd = new OverdraftScheduleDetail();
			schd.setDroplineDate(odSchd.getSchDate());
			schd.setActualRate(prvODSchd.getActualRate());
			schd.setBaseRate(prvODSchd.getBaseRate());
			schd.setSplRate(prvODSchd.getSplRate());
			schd.setMargin(prvODSchd.getMargin());

			// Dropline Rate
			if (StringUtils.isEmpty(schd.getBaseRate())) {
				schd.setDroplineRate(prvODSchd.getActualRate());
			} else {
				RateDetail rateDetail = RateUtil.rates(fm.getRepayBaseRate(), fm.getFinCcy(), fm.getRepaySpecialRate(),
						fm.getRepayMargin(), schd.getDroplineDate(), fm.getRpyMinRate(), fm.getRpyMaxRate());
				if (rateDetail.getErrorDetails() == null) {
					schd.setDroplineRate(rateDetail.getNetRefRateLoan());
				}
			}

			// If Last Overdraft schedule or Limit Expiry
			if (i == (Details.size() - 1)) {
				schd.setLimitDrop(odSchd.getDroplineAmount());
				schd.setODLimit(BigDecimal.ZERO);

			} else {
				schd.setLimitDrop(odSchd.getDroplineAmount());
				schd.setODLimit(prvODSchd.getODLimit().subtract(odSchd.getDroplineAmount()));
			}

			oldOverdraftList.add(schd);
			prvODSchd = schd;

			// Add Maturity OD Schedule
			if (i == (Details.size() - 1)) {
				if (fm.getMaturityDate().compareTo(odSchd.getSchDate()) > 0) {
					OverdraftScheduleDetail maturityODSchd = new OverdraftScheduleDetail();
					maturityODSchd.setDroplineDate(fm.getMaturityDate());
					maturityODSchd.setActualRate(prvODSchd.getActualRate());
					maturityODSchd.setBaseRate(prvODSchd.getBaseRate());
					maturityODSchd.setSplRate(prvODSchd.getSplRate());
					maturityODSchd.setMargin(prvODSchd.getMargin());

					// Dropline Rate
					if (StringUtils.isEmpty(maturityODSchd.getBaseRate())) {
						maturityODSchd.setDroplineRate(prvODSchd.getActualRate());
					} else {
						RateDetail rateDetail = RateUtil.rates(fm.getRepayBaseRate(), fm.getFinCcy(),
								fm.getRepaySpecialRate(), fm.getRepayMargin(), maturityODSchd.getDroplineDate(),
								fm.getRpyMinRate(), fm.getRpyMaxRate());
						if (rateDetail.getErrorDetails() == null) {
							maturityODSchd.setDroplineRate(rateDetail.getNetRefRateLoan());
						}
					}

					maturityODSchd.setLimitDrop(BigDecimal.ZERO);
					maturityODSchd.setODLimit(BigDecimal.ZERO);

					oldOverdraftList.add(maturityODSchd);
					prvODSchd = maturityODSchd;
				}
			}
		}

		schdData.setOverdraftScheduleDetails(sortOverdraftSchedules(oldOverdraftList));

		logger.debug(Literal.ENTERING);
		return schdData;
	}

	private void setOrgEndBalanceOnStep(FinScheduleData finScheduleData, BigDecimal newDisbAmount,
			BigDecimal newFeeAmt) {

		FinanceMain finMain = finScheduleData.getFinanceMain();
		Date evtFromDate = finMain.getEventFromDate();
		BigDecimal balDisbAmount = newDisbAmount.add(newFeeAmt);

		int idxStart = 0;
		int riStart = 0;
		int riEnd = 0;
		boolean grcEnd = false;
		List<FinanceStepPolicyDetail> spdList = finScheduleData.getStepPolicyDetails();
		List<FinanceScheduleDetail> schedules = finScheduleData.getFinanceScheduleDetails();

		for (FinanceStepPolicyDetail spd : spdList) {
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
			for (int iFsd = idxStart; iFsd < schedules.size(); iFsd++) {
				FinanceScheduleDetail fsd = schedules.get(iFsd);
				if (iFsd == riStart) {
					spd.setStepStart(fsd.getSchDate());
				}

				// Part payment installment is also considering for installment count so added isFrqDate condition.
				String specifier = fsd.getSpecifier();
				if (fsd.isRepayOnSchDate() && fsd.isFrqDate()
						&& !(StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI))) {
					instCount = instCount + 1;
				} else if (iFsd != 0 && PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())
						&& !(FinanceConstants.FLAG_BPI.equals(fsd.getBpiOrHoliday()))
						&& (CalculationConstants.SCH_SPECIFIER_GRACE.equals(specifier)
								|| CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier))
						&& fsd.isFrqDate()) {
					instCount = instCount + 1;
				}

				if (DateUtil.compare(fsd.getSchDate(), evtFromDate) >= 0) {
					fsd.setOrgEndBal(fsd.getOrgEndBal().add(balDisbAmount));
					if (!StringUtils.equals(FinanceConstants.FLAG_BPI, fsd.getBpiOrHoliday())) {
						if (fsd.isRepayOnSchDate() && fsd.isFrqDate()) {
							fsd.setOrgEndBal(fsd.getOrgEndBal().subtract(spd.getStepDiffEMI()));
							balDisbAmount = balDisbAmount.subtract(spd.getStepDiffEMI());
						}
					}
				}

				// iFsd == riEnd
				if (spd.getInstallments() == instCount) {
					if (CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier)) {
						grcEnd = true;
					}
					idxStart = iFsd + 1;

					break;
				}
			}
		}
	}

	private void rebuildDroplineSchd(FinScheduleData finScheduleData) {
		logger.debug(" Entering ");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = finScheduleData.getFinanceScheduleDetails();
		List<OverdraftScheduleDetail> odSchdDetails = finScheduleData.getOverdraftScheduleDetails();

		BigDecimal calIntFraction = BigDecimal.ZERO;
		BigDecimal calInt = BigDecimal.ZERO;
		FinanceScheduleDetail prvSchd = null;

		// Possible Values : NO_ADJ, ADJ_LAST_INST, ADJ_NEXT_INST
		String roundAdjMth = SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD);

		int schdIndex = 0;

		for (int j = 0; j < odSchdDetails.size(); j++) {
			OverdraftScheduleDetail odSchd = odSchdDetails.get(j);

			boolean rcdFound = false;
			for (int i = schdIndex; i < schdDetails.size(); i++) {
				FinanceScheduleDetail curSchd = schdDetails.get(i);
				if (DateUtil.compare(odSchd.getDroplineDate(), curSchd.getSchDate()) == 0) {
					rcdFound = true;
				} else if (DateUtil.compare(curSchd.getSchDate(), odSchd.getDroplineDate()) > 0) {
					schdIndex = i;
					break;
				}
			}

			if (!rcdFound) {
				finScheduleData = addSchdRcd(finScheduleData, odSchd.getDroplineDate(), schdIndex - 1, false);
				schdDetails = finScheduleData.getFinanceScheduleDetails();
			}
		}

		for (int i = 0; i < schdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = schdDetails.get(i);

			if (i == 0) {
				prvSchd = curSchd;
				continue;
			}

			// fields which are used for calculation
			Date curSchDate = curSchd.getSchDate();
			Date prvSchDate = prvSchd.getSchDate();

			// Verification of OCurrent OD Limit compared to previous closing balance to Set Principal Due adjustments
			BigDecimal curODLimit = BigDecimal.ZERO;
			boolean droplineAvial = false;
			for (int j = 0; j < odSchdDetails.size(); j++) {
				OverdraftScheduleDetail odSchd = odSchdDetails.get(j);
				if (DateUtil.compare(odSchd.getDroplineDate(), curSchd.getSchDate()) == 0) {
					curODLimit = odSchd.getODLimit();
					droplineAvial = true;
					break;
				} else if (DateUtil.compare(odSchd.getDroplineDate(), curSchd.getSchDate()) > 0) {
					break;
				}
			}

			BigDecimal priDue = BigDecimal.ZERO;
			if (droplineAvial && prvSchd.getClosingBalance().compareTo(curODLimit) > 0) {
				priDue = prvSchd.getClosingBalance().subtract(curODLimit);
			}

			// Profit Calculation
			curSchd.setBalanceForPftCal(prvSchd.getClosingBalance());

			if (curSchd.getBalanceForPftCal().compareTo(BigDecimal.ZERO) > 0) {

				if (StringUtils.equals(roundAdjMth, "NO_ADJ") || (StringUtils.equals(roundAdjMth, "ADJ_LAST_INST")
						&& DateUtil.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) != 0
						&& DateUtil.compare(curSchd.getSchDate(), finMain.getMaturityDate()) != 0)) {

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
						&& DateUtil.compare(prvSchDate, finMain.getGrcPeriodEndDate()) != 0) {
					calIntFraction = calIntFraction.add(prvSchd.getProfitFraction());
				}

				curSchd.setRepayComplete(false);
				curSchd.setProfitFraction(calIntFraction);
			} else {
				calInt = BigDecimal.ZERO;
				curSchd.setRepayComplete(true);
			}
			curSchd.setProfitCalc(calInt);

			// 1. Principal Due Adjustments
			curSchd.setPrincipalSchd(priDue.add(curSchd.getPartialPaidAmt()));
			if (priDue.add(curSchd.getPartialPaidAmt()).compareTo(BigDecimal.ZERO) > 0) {
				curSchd.setRepayOnSchDate(true);
			}

			// 2. Profit Schedule : ProfitSchd Calculation
			BigDecimal profitSchd = BigDecimal.ZERO;
			if (curSchd.isPftOnSchDate() && (curSchd.isFrqDate() || i == schdDetails.size() - 1)) {
				profitSchd = curSchd.getProfitCalc().add(prvSchd.getProfitBalance()).subtract(prvSchd.getCpzAmount());
			}
			curSchd.setProfitSchd(profitSchd);
			curSchd.setProfitBalance(getProfitBalance(curSchd, prvSchd, false));

			// 3. Repay Amount : RepayAmount Calculation
			curSchd.setRepayAmount(curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()));

			// ClosingBalance or Utilization
			curSchd.setClosingBalance(getClosingBalance(curSchd, prvSchd, finMain.getRepayRateBasis(), false));

			prvSchd = curSchd;
		}

		logger.debug(" Leaving ");
	}

	private void autoCalcLastRepayStepAmt(FinScheduleData schdData, FinanceMain fm) {
		Date stpStart = null;
		List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();
		schdData.setStepPolicyDetails(schdData.getStepPolicyDetails(), true);
		List<FinanceStepPolicyDetail> spdList = schdData.getStepPolicyDetails();

		String stepSpecifier = null;
		for (FinanceStepPolicyDetail spd : spdList) {
			stepSpecifier = spd.getStepSpecifier();
			if (PennantConstants.STEP_SPECIFIER_GRACE.equals(stepSpecifier)) {
				fm.setGrcStps(true);
				break;
			}
		}

		for (int iSpd = 0; iSpd < spdList.size(); iSpd++) {
			FinanceStepPolicyDetail spd = spdList.get(iSpd);
			stepSpecifier = spd.getStepSpecifier();

			if (PennantConstants.STEP_SPECIFIER_GRACE.equals(stepSpecifier)) {
				continue;
			}

			if ((iSpd == spdList.size() - 1) && PennantConstants.STEP_SPECIFIER_REG_EMI.equals(stepSpecifier)) {
				stpStart = spd.getStepStart();

				if (spd.getStepStart().compareTo(fm.getEventFromDate()) > 0) {
					stpStart = spd.getStepStart();
				} else {
					for (FinanceScheduleDetail fsd : fsdList) {
						if (fsd.getSchDate().compareTo(fm.getEventFromDate()) > 0) {
							stpStart = fsd.getSchDate();
							break;
						}
					}
				}

				fm.setRecalFromDate(stpStart);
				fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
				fm.setCalculateRepay(true);
				fm.setEqualRepay(true);
				fm.setRecalSchdMethod(fm.getScheduleMethod());

				setRpyInstructDetails(schdData, stpStart, spd.getStepEnd(), BigDecimal.ZERO, fm.getScheduleMethod());
			}
		}
	}

	/*
	 * (In step loan user can pay repay amount less than the interest amount in grace period in this scenario remaining
	 * amount should capitalize to ending balance). But while calculating planned EMI holiday Cpzonschdate flag getting
	 * false for the schedules after planned EMI lock period date. So remaining amount not capitalizing to ending
	 * balance in grace period. So added the below code. In repay user doesn't have provision to give the step amount
	 * less than repay amount.
	 */
	private boolean isCpzOnSchDate(FinanceMain finMain, FinanceScheduleDetail curSchd) {
		if (finMain.isStepFinance() && DateUtil.compare(curSchd.getSchDate(), finMain.getGrcPeriodEndDate()) <= 0
				&& PennantConstants.STEPPING_CALC_AMT.equals(finMain.getCalcOfSteps())) {
			return false;
		} else {
			return FrequencyUtil.isFrqDate(finMain.getRepayCpzFrq(), curSchd.getSchDate());
		}
	}

	private void getNewPrincipal(FinanceMain fm, FinanceScheduleDetail curSchd, FinanceScheduleDetail prvSchd) {
		BigDecimal newPrincipal = BigDecimal.ZERO;

		Date schDate = curSchd.getSchDate();

		boolean falg = false;
		if ((fm.isScheduleMaintained() || StringUtils.isNotBlank(fm.getReceiptPurpose()))
				&& schDate.compareTo(fm.getRecalFromDate()) >= 0) {
			falg = true;
		} else if (fm.isStepFinance() && fm.isStepRecalOnProrata()) {
			if (schDate.compareTo(fm.getAppDate()) > 0) {
				falg = true;
			}
		}

		if (falg) {
			newPrincipal = prvSchd.getClosingBalance().subtract(curSchd.getOrgEndBal());
			if (newPrincipal.compareTo(BigDecimal.ZERO) < 0) {
				newPrincipal = BigDecimal.ZERO;
			}

			curSchd.setPrincipalSchd(newPrincipal);
		}
	}

	private void getSanBasedInterest(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = schdData.getFinanceMain();

		BigDecimal dayFactor = BigDecimal.valueOf(30).divide(BigDecimal.valueOf(360), 9, RoundingMode.HALF_DOWN);

		schdData.setStepPolicyDetails(schdData.getStepPolicyDetails(), true);

		List<FinanceScheduleDetail> details = schdData.getFinanceScheduleDetails();

		if (CollectionUtils.isNotEmpty(details)) {
			return;
		}

		for (FinanceScheduleDetail fsd : details) {
			if (fsd.isRepayOnSchDate() && fsd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0
					&& CalculationConstants.SCH_SPECIFIER_REPAY.equals(fsd.getSpecifier())) {
				BigDecimal finAssetValue = fm.getFinAssetValue();
				BigDecimal rate = fsd.getCalculatedRate();

				fm.setSanBasedPft(CalculationUtil.calInterestWithDaysFactor(dayFactor, finAssetValue, rate));
				break;

			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void prepareManualRepayRI(FinScheduleData fsData) {
		prepareManualRepayRI(fsData, false);
	}

	private void prepareManualRepayRI(FinScheduleData fsData, boolean isFirstRun) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fsData.getFinanceMain();

		if (PennantConstants.FINSOURCE_ID_API.equals(fm.getFinSourceID()) && fm.isRecalSteps()) {
			prepareManualRepayInstructionsForStep(fsData);
		} else {
			prepareManualRepayInstructions(fsData, isFirstRun);
		}
		logger.debug(Literal.LEAVING);
	}

	private void prepareManualRepayInstructionsForStep(FinScheduleData schdData) {
		List<FinanceStepPolicyDetail> spdList = schdData.getStepPolicyDetails();
		schdData.setStepPolicyDetails(schdData.getStepPolicyDetails(), true);
		spdList = schdData.getStepPolicyDetails();

		FinanceMain fm = schdData.getFinanceMain();

		fm.setRecalSteps(false);
		fm.setCpzPosIntact(true);

		String schdMethod = fm.getScheduleMethod();
		String grcSchMtd = fm.getGrcSchdMthd();
		String stepSpecifier = null;

		for (FinanceStepPolicyDetail spd : spdList) {
			stepSpecifier = spd.getStepSpecifier();
			if (PennantConstants.STEP_SPECIFIER_GRACE.equals(stepSpecifier)) {
				fm.setGrcStps(true);
				break;
			}
		}

		for (int iSpd = 0; iSpd < spdList.size(); iSpd++) {
			FinanceStepPolicyDetail spd = spdList.get(iSpd);
			stepSpecifier = spd.getStepSpecifier();

			if (PennantConstants.STEP_SPECIFIER_GRACE.equals(stepSpecifier)) {
				continue;
			}

			if (!spd.isAutoCal()) {
				setRpyInstructDetails(schdData, spd.getStepStart(), spd.getStepEnd(), spd.getSteppedEMI(), schdMethod);
			} else {
				fm.setRecalFromDate(spd.getStepStart());
				fm.setRecalType(CalculationConstants.RPYCHG_TILLDATE);
				fm.setCalculateRepay(true);
				fm.setEqualRepay(true);
				fm.setRecalSchdMethod(grcSchMtd);

				setRpyInstructDetails(schdData, fm.getRecalFromDate(), spd.getStepEnd(), BigDecimal.ZERO, grcSchMtd);
			}

			if ((iSpd == spdList.size() - 1) && PennantConstants.STEP_SPECIFIER_REG_EMI.equals(stepSpecifier)) {
				fm.setRecalFromDate(spd.getStepStart());
				fm.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
				fm.setCalculateRepay(true);
				fm.setEqualRepay(true);
				fm.setRecalSchdMethod(schdMethod);
			}
		}
	}

	private int getRINextIndex(List<FinanceScheduleDetail> schedules, int idxStart, FinanceStepPolicyDetail spd,
			int iFsd) {
		for (int j = idxStart; j < schedules.size(); j++) {
			FinanceScheduleDetail schd = schedules.get(j);

			if (schd.isRepayOnSchDate() && schd.isFrqDate() && !schd.isDisbOnSchDate()
					&& !isBPIHoliday(schd.getBpiOrHoliday())) {
				break;
			} else {
				String stepSpecifier = spd.getStepSpecifier();
				String specifier = schd.getSpecifier();

				if (iFsd != 0 && PennantConstants.STEP_SPECIFIER_GRACE.equals(stepSpecifier)
						&& !isBPIHoliday(schd.getBpiOrHoliday())
						&& (CalculationConstants.SCH_SPECIFIER_GRACE.equals(specifier)
								|| CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier))
						&& !schd.isDisbOnSchDate() && schd.isFrqDate()) {
					break;
				}
			}

			idxStart++;
		}

		return idxStart;
	}

	private boolean isDeveloperFinance(FinScheduleData finScheduleData) {
		return finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isDeveloperFinance();
	}

	private boolean isStepFinance(FinanceMain fm) {
		String schdMthd = fm.getScheduleMethod();

		return fm.isStepFinance() && (CalculationConstants.SCHMTHD_PRI_PFT.equals(schdMthd)
				|| CalculationConstants.SCHMTHD_PRI.equals(schdMthd));
	}

	private boolean isStepFinance(FinScheduleData finScheduleData) {
		if (isDeveloperFinance(finScheduleData)) {
			return true;
		}

		return isStepFinance(finScheduleData.getFinanceMain());
	}

	private boolean isEMIHoliday(String bpiOrHoliday) {
		return FinanceConstants.FLAG_HOLIDAY.equals(bpiOrHoliday);
	}

	private boolean isEMIHoliday(FinanceScheduleDetail curSchd) {
		String bpiOrHoliday = curSchd.getBpiOrHoliday();

		return StringUtils.isEmpty(bpiOrHoliday) || isEMIHoliday(bpiOrHoliday);
	}

	private boolean isBPIHoliday(String bpiOrHoliday) {
		return FinanceConstants.FLAG_BPI.equals(bpiOrHoliday);
	}

	private void setStepRepayAmount(FinScheduleData schdData, BigDecimal approxEMI) {
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceStepPolicyDetail> stepPolicyDetails = schdData.getStepPolicyDetails();
		List<RepayInstruction> repayInstructions = schdData.getRepayInstructions();

		String calRoundingMode = fm.getCalRoundingMode();
		int roundingTarget = fm.getRoundingTarget();

		for (FinanceStepPolicyDetail spd : stepPolicyDetails) {
			BigDecimal stepEMI = (approxEMI.multiply(spd.getEmiSplitPerc())).divide(new BigDecimal(100), 0,
					RoundingMode.HALF_DOWN);

			stepEMI = CalculationUtil.roundAmount(stepEMI, calRoundingMode, roundingTarget);

			//
			for (RepayInstruction rpyInst : repayInstructions) {
				if (DateUtil.compare(spd.getStepStart(), rpyInst.getRepayDate()) == 0) {
					rpyInst.setRepayAmount(stepEMI);

					break;
				}
			}
		}
	}

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