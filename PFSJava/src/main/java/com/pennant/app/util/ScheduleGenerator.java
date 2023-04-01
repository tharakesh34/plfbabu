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
 * FileName : ScheduleGenerator.java *
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.FrequencyDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

public class ScheduleGenerator {

	public ScheduleGenerator() {
	}

	private static final Logger logger = LogManager.getLogger(ScheduleGenerator.class);

	private FinScheduleData finScheduleData;

	public static FinScheduleData getNewSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		int fixedRateTenor = financeMain.getFixedRateTenor();
		BigDecimal fixedTenorRate = financeMain.getFixedTenorRate();
		Date fixedTenorEndDate = DateUtil.addMonths(financeMain.getGrcPeriodEndDate(),
				fixedRateTenor > 0 ? fixedRateTenor : 0);

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverdraft = true;
		}
		finScheduleData.setErrorDetail(
				validateFinanceMain(financeMain, finScheduleData.getDisbursementDetails(), isOverdraft));

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
			finScheduleData.getFinanceScheduleDetails()
					.add(finScheduleData.getScheduleMap().get(schdDateKeyList.get(j)));
		}

		// Advised Profit Rate Calculation Process & Profit Days Basis , Reference Rates Setting
		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

			curSchd.setTDSApplicable(TDSCalculator.isTDSApplicable(financeMain));

			if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
				curSchd.setActRate(financeMain.getGrcPftRate());
				curSchd.setCalculatedRate(financeMain.getGrcPftRate());
				curSchd.setBaseRate(financeMain.getGraceBaseRate());
				curSchd.setSplRate(financeMain.getGraceSpecialRate());
				curSchd.setMrgRate(financeMain.getGrcMargin());
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);

				curSchd.setPftDaysBasis(financeMain.getGrcProfitDaysBasis());

			} else {
				if (fixedRateTenor > 0
						&& (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0
								|| curSchd.isRvwOnSchDate() || curSchd.isRepayOnSchDate())
						&& curSchd.getSchDate().compareTo(fixedTenorEndDate) <= 0) {
					curSchd.setActRate(fixedTenorRate);
					curSchd.setCalculatedRate(fixedTenorRate);
					curSchd.setRvwOnSchDate(false);
				} else {
					curSchd.setActRate(financeMain.getRepayProfitRate());
					curSchd.setCalculatedRate(financeMain.getRepayProfitRate());
				}
				curSchd.setBaseRate(financeMain.getRepayBaseRate());
				curSchd.setSplRate(financeMain.getRepaySpecialRate());
				curSchd.setMrgRate(financeMain.getRepayMargin());
				curSchd.setPftDaysBasis(financeMain.getProfitDaysBasis());

				if (curSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0) {
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
					if (DateUtil.compare(financeMain.getFinStartDate(), financeMain.getGrcPeriodEndDate()) != 0) {
						curSchd.setPftDaysBasis(financeMain.getGrcProfitDaysBasis());
					}
				} else if (curSchd.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
				} else {
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				}
			}

		}

		finScheduleData.getScheduleMap().clear();

		logger.debug("Leaving");
		return finScheduleData;
	}

	public static FinScheduleData getChangedSchd(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			// TODO PV: Change Schedule will not work for OD facility at this point.
			return finScheduleData;
		}

		finScheduleData.setErrorDetail(validateFrqAndDates(financeMain));

		if (finScheduleData.getErrorDetails().size() != 0) {
			logger.debug("Leaving - Error");
			return finScheduleData;
		}

		if (finScheduleData.getScheduleMap() != null) {
			finScheduleData.getScheduleMap().clear();
		}

		List<FinanceScheduleDetail> finScheduleDetails = finScheduleData.getFinanceScheduleDetails();
		Date newGraceEnd = financeMain.getGrcPeriodEndDate();
		Date prvGraceEnd = financeMain.getEventFromDate();
		Date newSchdAfter = new Date();
		int iUntouch = 0;
		String newGrcSchdMethod = "";
		String newRpySchdMethod = "";

		// Find Date from which schedule dates to be removed.
		if (newGraceEnd.compareTo(prvGraceEnd) > 0) {
			newSchdAfter = prvGraceEnd;
		} else {
			newSchdAfter = newGraceEnd;
			financeMain.setEventFromDate(newSchdAfter);
		}

		// newSchdAfter = newGraceEnd;

		FinanceScheduleDetail prvSchdGrcEnd = null;
		boolean repayRateReset = false;

		for (int i = 0; i < finScheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleDetails.get(i);

			if (DateUtil.compare(curSchd.getSchDate(), prvGraceEnd) < 0) {
				prvSchdGrcEnd = curSchd;
			}

			if (DateUtil.compare(curSchd.getSchDate(), prvGraceEnd) == 0) {
				newGrcSchdMethod = curSchd.getSchdMethod();

				if (prvSchdGrcEnd != null) {
					if (!StringUtils.equals(prvSchdGrcEnd.getBaseRate(), curSchd.getBaseRate())) {
						repayRateReset = true;
					}
					if (!StringUtils.equals(prvSchdGrcEnd.getSplRate(), curSchd.getSplRate())) {
						repayRateReset = true;
					}
					if (prvSchdGrcEnd.getMrgRate().compareTo(curSchd.getMrgRate()) != 0) {
						repayRateReset = true;
					}
				}
			}

			if (DateUtil.compare(curSchd.getSchDate(), newSchdAfter) <= 0) {
				finScheduleData.setScheduleMap(curSchd);
				iUntouch = i;
				continue;
			}

			if (StringUtils.isBlank(newRpySchdMethod)) {
				newRpySchdMethod = financeMain.getScheduleMethod();
			}

			finScheduleDetails.remove(i);
			i = i - 1;

		}

		newSchdAfter = finScheduleDetails.get(iUntouch).getSchDate();
		iUntouch = iUntouch + 1;

		// CHANGE SCHEDULE
		finScheduleData = changeSchdProcess(finScheduleData, newSchdAfter);
		List<Date> schdDateKeyList = new ArrayList<Date>(finScheduleData.getScheduleMap().keySet());

		Collections.sort(schdDateKeyList);
		for (int j = iUntouch; j < schdDateKeyList.size(); j++) {
			finScheduleData.getFinanceScheduleDetails()
					.add(finScheduleData.getScheduleMap().get(schdDateKeyList.get(j)));
		}

		// Advised Profit Rate Calculation Process & Profit Days Basis , Reference Rates Setting
		Date appDate = SysParamUtil.getAppDate();
		FinanceScheduleDetail prvSchd = null;
		BigDecimal minRate = BigDecimal.ZERO;
		BigDecimal maxRate = BigDecimal.ZERO;

		for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
			if (i != 0) {
				prvSchd = finScheduleData.getFinanceScheduleDetails().get(i - 1);
			}

			if (DateUtil.compare(curSchd.getSchDate(), financeMain.getGrcPeriodEndDate()) == 0
					&& financeMain.getRepayBaseRate() == null) {
				if (prvSchd != null) {
					prvSchd.setActRate(financeMain.getRepayProfitRate());
				} else {
					curSchd.setActRate(financeMain.getRepayProfitRate());
					curSchd.setCalculatedRate(financeMain.getRepayProfitRate());
				}
			}

			// Interest Days basis kept as same for both grace and repayment periods.
			// curSchd.setPftDaysBasis(financeMain.getProfitDaysBasis());

			// Interest Days basis kept as same for both grace and repayment periods.
			if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				curSchd.setPftDaysBasis(finScheduleData.getFinanceMain().getBpiPftDaysBasis());
			} else {
				if (DateUtil.compare(curSchd.getSchDate(), financeMain.getGrcPeriodEndDate()) <= 0) {
					curSchd.setPftDaysBasis(financeMain.getGrcProfitDaysBasis());
				} else {
					curSchd.setPftDaysBasis(financeMain.getProfitDaysBasis());
				}
			}

			curSchd.setTDSApplicable(TDSCalculator.isTDSApplicable(financeMain));

			if (DateUtil.compare(curSchd.getSchDate(), financeMain.getGrcPeriodEndDate()) < 0) {
				// Bugfix:Removed '=' condition to fix ROI Issue in Change Moratorium at GraceEnd
				if (DateUtil.compare(curSchd.getSchDate(), newSchdAfter) >= 0) {
					if (prvSchd != null) {
						curSchd.setActRate(prvSchd.getActRate());
						curSchd.setBaseRate(prvSchd.getBaseRate());
						curSchd.setSplRate(prvSchd.getSplRate());
						curSchd.setMrgRate(prvSchd.getMrgRate());
					}
				}

				curSchd.setSchdMethod(newGrcSchdMethod);
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);

				minRate = financeMain.getGrcMinRate();
				maxRate = financeMain.getGrcMaxRate();
			} else {

				if (repayRateReset) {
					curSchd.setActRate(financeMain.getRepayProfitRate());
					curSchd.setBaseRate(financeMain.getRepayBaseRate());
					curSchd.setSplRate(financeMain.getRepaySpecialRate());
					curSchd.setMrgRate(financeMain.getRepayMargin());
					repayRateReset = false;
				} else {
					if (prvSchd != null) {
						curSchd.setActRate(prvSchd.getActRate());
						curSchd.setBaseRate(prvSchd.getBaseRate());
						curSchd.setSplRate(prvSchd.getSplRate());
						curSchd.setMrgRate(prvSchd.getMrgRate());
					}
				}

				if (DateUtil.compare(curSchd.getSchDate(), financeMain.getGrcPeriodEndDate()) == 0) {
					curSchd.setSchdMethod(newGrcSchdMethod);
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);

					// FIXME : SATYA - Not Always True
					if (FrequencyUtil.isFrqDate(financeMain.getGrcPftRvwFrq(), financeMain.getGrcPeriodEndDate())) {
						curSchd.setRvwOnSchDate(true);
					}

					if (financeMain.isFinIsRateRvwAtGrcEnd()) {
						curSchd.setRvwOnSchDate(true);
					}

					curSchd.setCpzOnSchDate(false);
					curSchd.setPftOnSchDate(false);
					curSchd.setRepayOnSchDate(false);

					// Set Profit on Schedule Flag
					if (CalculationConstants.SCHMTHD_GRCENDPAY.equals(curSchd.getSchdMethod())) {
						curSchd.setPftOnSchDate(true);
					} else if (CalculationConstants.SCHMTHD_PFT.equals(curSchd.getSchdMethod())
							|| CalculationConstants.SCHMTHD_PFTCAP.equals(curSchd.getSchdMethod())
							|| CalculationConstants.SCHMTHD_PRI_PFTC.equals(curSchd.getSchdMethod())
							|| CalculationConstants.SCHMTHD_PFTCPZ.equals(curSchd.getSchdMethod())
							|| (financeMain.isStepFinance()
									&& PennantConstants.STEPPING_CALC_AMT.equals(financeMain.getCalcOfSteps())
									&& CalculationConstants.SCHMTHD_EQUAL.equals(curSchd.getSchdMethod()))) {
						if (FrequencyUtil.isFrqDate(financeMain.getGrcPftFrq(), financeMain.getGrcPeriodEndDate())) {
							curSchd.setPftOnSchDate(true);
							curSchd.setRepayOnSchDate(true);
						}
					}

					// Capitalize Flag
					if (financeMain.isCpzAtGraceEnd()) {
						curSchd.setCpzOnSchDate(true);
					} else if (financeMain.isAllowGrcCpz()) {
						if (FrequencyUtil.isFrqDate(financeMain.getGrcCpzFrq(), financeMain.getGrcPeriodEndDate())) {
							curSchd.setCpzOnSchDate(true);
						}
					}

				} else if (curSchd.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
					curSchd.setSchdMethod(newRpySchdMethod);
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
				} else {
					curSchd.setSchdMethod(newRpySchdMethod);
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				}

				minRate = financeMain.getRpyMinRate();
				maxRate = financeMain.getRpyMaxRate();

			}

			if (StringUtils.isNotBlank(curSchd.getBaseRate())) {
				if (DateUtil.compare(curSchd.getSchDate(), appDate) >= 0) {
					BigDecimal calrate = RateUtil.rates(curSchd.getBaseRate(), financeMain.getFinCcy(),
							curSchd.getSplRate(), curSchd.getMrgRate(), curSchd.getSchDate(), minRate, maxRate)
							.getNetRefRateLoan();
					curSchd.setCalculatedRate(calrate);
				}
			} else {
				if (prvSchd != null) {
					curSchd.setCalculatedRate(prvSchd.getActRate());
					curSchd.setActRate(prvSchd.getActRate());
				}
			}
			finScheduleData.setScheduleMap(curSchd);
		}

		// SATYA : Disbursement Details from Grace End onwards
		for (FinanceDisbursement disbursementDetail : finScheduleData.getDisbursementDetails()) {
			Date disbDate = formatDate(disbursementDetail.getDisbDate());

			if (disbDate.compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
				continue;
			}

			FinanceScheduleDetail schedule = new FinanceScheduleDetail();

			if (finScheduleData.getScheduleMap() != null && finScheduleData.getScheduleMap().containsKey(disbDate)) {

				schedule = finScheduleData.getScheduleMap().get(disbDate);
			}

			schedule.setDisbOnSchDate(true);
			schedule.setFeeChargeAmt(disbursementDetail.getFeeChargeAmt());
			schedule.setDisbAmount(disbursementDetail.getDisbAmount());

		}

		finScheduleData.getScheduleMap().clear();
		financeMain.setSkipRateReset(true);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for recreating Schedules based on Rvw Frequency Between Event Dates
	 * 
	 * @param scheduleData
	 * @return
	 */
	public static FinScheduleData setRvwDatesOnRateFrq(FinScheduleData scheduleData, String frequency) {

		FinanceMain finMain = scheduleData.getFinanceMain();
		Date evtFromDate = finMain.getEventFromDate();
		Date evtToDate = finMain.getEventToDate();

		if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {

			Date fddLockPeriodDate = DateUtil.addDays(evtFromDate, scheduleData.getFinanceType().getFddLockPeriod());
			fddLockPeriodDate = DateUtil.addDays(fddLockPeriodDate, 1);

			boolean isFrqDate = FrequencyUtil.isFrqDate(frequency, fddLockPeriodDate);

			scheduleData = getSchedule(scheduleData, frequency, fddLockPeriodDate, evtToDate,
					CalculationConstants.SCHDFLAG_RVW, false, isFrqDate);
		} else {
			scheduleData = getSchedule(scheduleData, frequency, evtFromDate, evtToDate,
					CalculationConstants.SCHDFLAG_RVW, false, true);
		}

		List<Date> schdDateKeyList = new ArrayList<Date>(scheduleData.getScheduleMap().keySet());
		Collections.sort(schdDateKeyList);
		for (int j = 0; j < schdDateKeyList.size(); j++) {
			scheduleData.getFinanceScheduleDetails().add(scheduleData.getScheduleMap().get(schdDateKeyList.get(j)));
		}

		// Reference Rates Setting
		FinanceScheduleDetail curSchd = null;
		for (int i = 1; i < scheduleData.getFinanceScheduleDetails().size(); i++) {
			curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			Date schdDate = curSchd.getSchDate();
			if (DateUtil.compare(schdDate, evtFromDate) < 0 || DateUtil.compare(schdDate, evtToDate) > 0) {
				continue;
			}

			// Interest Days basis kept as same for both grace and repayment periods.
			if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
				curSchd.setPftDaysBasis(scheduleData.getFinanceMain().getBpiPftDaysBasis());
			} else {
				if (DateUtil.compare(schdDate, finMain.getGrcPeriodEndDate()) <= 0) {
					curSchd.setPftDaysBasis(finMain.getGrcProfitDaysBasis());
				} else {
					curSchd.setPftDaysBasis(finMain.getProfitDaysBasis());
				}
			}

			curSchd.setTDSApplicable(TDSCalculator.isTDSApplicable(finMain));
			if (DateUtil.compare(schdDate, finMain.getGrcPeriodEndDate()) < 0) {
				curSchd.setSchdMethod(finMain.getGrcSchdMthd());
				curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
			} else {
				if (DateUtil.compare(schdDate, finMain.getGrcPeriodEndDate()) == 0) {
					curSchd.setSchdMethod(finMain.getGrcSchdMthd());
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
				} else if (DateUtil.compare(schdDate, finMain.getMaturityDate()) == 0) {
					curSchd.setSchdMethod(finMain.getScheduleMethod());
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
				} else {
					curSchd.setSchdMethod(finMain.getScheduleMethod());
					curSchd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				}
			}
		}

		scheduleData.getScheduleMap().clear();

		logger.debug("Leaving");
		return scheduleData;
	}

	/**
	 * Method for Generating New Schedule Dates List with particulars
	 * 
	 * @param scheduleData
	 * @param serviceInst
	 * @param startDate
	 * @param endDate
	 * @param includeStartDate
	 * @param scheduleFlag
	 * @return
	 */
	public static FinScheduleData getScheduleDateList(FinScheduleData scheduleData, FinServiceInstruction serviceInst,
			Date grcStartDate, Date rpyStartDate, Date endDate) {
		return new ScheduleGenerator(scheduleData, serviceInst, grcStartDate, rpyStartDate, endDate)
				.getFinScheduleData();
	}

	public ScheduleGenerator(FinScheduleData scheduleData, FinServiceInstruction serviceInst, Date grcStartDate,
			Date rpyStartDate, Date endDate) {

		scheduleData = maintainFrqSchdProcess(scheduleData, serviceInst, grcStartDate, rpyStartDate, endDate);
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
			schedule.setDisbOnSchDate(true);

			if (i == 0) {
				if (financeMain.getDownPayment() != null
						&& financeMain.getDownPayment().compareTo(BigDecimal.ZERO) != 0) {
					schedule.setDownPaymentAmount(financeMain.getDownPayment());
					schedule.setDownpaymentOnSchDate(true);
				}
			}

			finScheduleData.setScheduleMap(schedule);
		}

		if (financeMain.isAllowGrcPeriod()) {

			// Load Grace period profit dates
			finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftFrq(), financeMain.getNextGrcPftDate(),
					financeMain.getGrcPeriodEndDate(), CalculationConstants.SCHDFLAG_PFT, false, true);

			// Load Grace period profit review dates
			if (financeMain.isAllowGrcPftRvw()) {
				finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftRvwFrq(),
						financeMain.getNextGrcPftRvwDate(), financeMain.getGrcPeriodEndDate(),
						CalculationConstants.SCHDFLAG_RVW, false, true);
			}

			// Load Grace period capitalization dates
			if (financeMain.isAllowGrcCpz()) {
				finScheduleData = getSchedule(finScheduleData, financeMain.getGrcCpzFrq(),
						financeMain.getNextGrcCpzDate(), financeMain.getGrcPeriodEndDate(),
						CalculationConstants.SCHDFLAG_CPZ, false, true);
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

		// schedule.setPftOnSchDate(true);
		if (financeMain.isAllowGrcPeriod() && financeMain.isAllowGrcCpz() && financeMain.isCpzAtGraceEnd()) {
			schedule.setCpzOnSchDate(true);
		}
		schedule.setActRate(financeMain.getRepayProfitRate());
		schedule.setCalculatedRate(financeMain.getRepayProfitRate());
		schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
		finScheduleData.setScheduleMap(schedule);

		Date derivedMDT = financeMain.getMaturityDate();
		int advTerms = financeMain.getAdvTerms();
		if (advTerms > 0) {
			// derivedMDT = DateUtility.addMonths(derivedMDT, -advEMITerms);
		}

		// Load Repay profit dates
		if (StringUtils.isNotBlank(financeMain.getRepayPftFrq())) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayPftFrq(),
					financeMain.getNextRepayPftDate(), derivedMDT, CalculationConstants.SCHDFLAG_PFT, false, true);
		}

		// Load Repay profit review dates
		if (financeMain.isAllowRepayRvw()) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayRvwFrq(),
					financeMain.getNextRepayRvwDate(), derivedMDT, CalculationConstants.SCHDFLAG_RVW, false, true);
		}

		// Load Repay capitalize dates
		if (financeMain.isAllowRepayCpz()) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayCpzFrq(),
					financeMain.getNextRepayCpzDate(), derivedMDT, CalculationConstants.SCHDFLAG_CPZ, false, true);
		}

		// Load Repayment dates
		finScheduleData = getSchedule(finScheduleData, financeMain.getRepayFrq(), financeMain.getNextRepayDate(),
				derivedMDT, CalculationConstants.SCHDFLAG_RPY, false, true);

		finScheduleData.setFinanceMain(financeMain);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private static FinScheduleData changeSchdProcess(FinScheduleData finScheduleData, Date newSchdAfter) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();

		// Load Grace period profit dates
		finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftFrq(), newSchdAfter,
				financeMain.getGrcPeriodEndDate(), CalculationConstants.SCHDFLAG_PFT, false, true);

		// Load Grace period profit review dates
		if (financeMain.isAllowGrcPftRvw()) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getGrcPftRvwFrq(), newSchdAfter,
					financeMain.getGrcPeriodEndDate(), CalculationConstants.SCHDFLAG_RVW, true, true);
		}

		// Load Grace period capitalization dates
		if (financeMain.isAllowGrcCpz()) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getGrcCpzFrq(), newSchdAfter,
					financeMain.getGrcPeriodEndDate(), CalculationConstants.SCHDFLAG_CPZ, true, true);
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

		finScheduleData.setScheduleMap(schedule);

		// Load Repay profit dates
		if (StringUtils.isNotBlank(financeMain.getRepayPftFrq())) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayPftFrq(),
					financeMain.getNextRepayPftDate(), financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_PFT,
					false, true);
		}

		// Load Repay profit review dates
		if (financeMain.isAllowRepayRvw()) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayRvwFrq(),
					financeMain.getNextRepayRvwDate(), financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_RVW,
					false, true);
		}

		// Load Repay capitalize dates
		if (financeMain.isAllowRepayCpz()) {
			finScheduleData = getSchedule(finScheduleData, financeMain.getRepayCpzFrq(),
					financeMain.getNextRepayCpzDate(), financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_CPZ,
					false, true);
		}

		// Load Repayment dates
		finScheduleData = getSchedule(finScheduleData, financeMain.getRepayFrq(), financeMain.getNextRepayDate(),
				financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_RPY, false, true);

		finScheduleData.setFinanceMain(financeMain);

		logger.debug("Leaving");
		return finScheduleData;
	}

	private static FinScheduleData maintainFrqSchdProcess(FinScheduleData finScheduleData,
			FinServiceInstruction serviceInst, Date grcStartDate, Date rpyStartDate, Date endDate) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		Date startCalFrom = null;
		if (financeMain.isAllowRepayRvw()
				&& StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			if (!FrequencyUtil.isFrqCodeMatch(financeMain.getRepayRvwFrq(), financeMain.getRepayFrq())) {

				FinanceScheduleDetail prvSchd = null;
				for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
					FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
					if (DateUtil.compare(curSchd.getSchDate(), financeMain.getEventFromDate()) >= 0) {
						if (prvSchd == null) {
							startCalFrom = FrequencyUtil.getNextDate(financeMain.getRepayRvwFrq(), 1,
									financeMain.getFinStartDate(), "A", false).getNextFrequencyDate();
						} else {
							startCalFrom = FrequencyUtil
									.getNextDate(financeMain.getRepayRvwFrq(), 1, prvSchd.getSchDate(), "A", false)
									.getNextFrequencyDate();
						}
						if (DateUtil.compare(startCalFrom, financeMain.getMaturityDate()) >= 0) {
							startCalFrom = financeMain.getMaturityDate();
						}
						break;
					}
					prvSchd = curSchd;
				}

			}

		}

		finScheduleData.getFinanceScheduleDetails().clear();
		if (financeMain.isAllowGrcPeriod() && grcStartDate != null
				&& grcStartDate.compareTo(financeMain.getGrcPeriodEndDate()) < 0) {

			// Load Grace period profit dates
			finScheduleData = getSchedule(finScheduleData, serviceInst.getGrcPftFrq(), grcStartDate,
					financeMain.getGrcPeriodEndDate(), CalculationConstants.SCHDFLAG_PFT, false, true);

			// Load Repayment dates during grace period
			if (financeMain.isAllowGrcRepay()) {
				finScheduleData = getSchedule(finScheduleData, serviceInst.getGrcPftFrq(), grcStartDate,
						financeMain.getGrcPeriodEndDate(), CalculationConstants.SCHDFLAG_RPY, false, true);
			}

			// Load Grace period profit review dates
			if (financeMain.isAllowGrcPftRvw()) {
				finScheduleData = getSchedule(finScheduleData, serviceInst.getGrcRvwFrq(), grcStartDate,
						financeMain.getGrcPeriodEndDate(), CalculationConstants.SCHDFLAG_RVW, false, true);
			}

			// Load Grace period capitalization dates
			if (financeMain.isAllowGrcCpz()) {
				finScheduleData = getSchedule(finScheduleData, serviceInst.getGrcCpzFrq(), grcStartDate,
						financeMain.getGrcPeriodEndDate(), CalculationConstants.SCHDFLAG_CPZ, false, true);
			}

			// To Check schedule date is found with grace period end date
			FinanceScheduleDetail schedule = null;
			if (finScheduleData.getScheduleMap() != null && finScheduleData.getScheduleMap()
					.containsKey(DateUtil.getDate(
							DateUtil.format(financeMain.getGrcPeriodEndDate(), PennantConstants.DBDateFormat),
							PennantConstants.DBDateFormat))) {
				schedule = finScheduleData.getScheduleMap()
						.get(DateUtil.getDate(
								DateUtil.format(financeMain.getGrcPeriodEndDate(), PennantConstants.DBDateFormat),
								PennantConstants.DBDateFormat));
			} else {

				schedule = new FinanceScheduleDetail();
				if (financeMain.getGrcPeriodEndDate() != null) {
					schedule.setSchDate(financeMain.getGrcPeriodEndDate());
					schedule.setDefSchdDate(financeMain.getGrcPeriodEndDate());
				}
			}

			// schedule.setPftOnSchDate(true);
			if (financeMain.isAllowGrcPeriod() && financeMain.isAllowGrcCpz()) {
				schedule.setCpzOnSchDate(true);
			}
			schedule.setActRate(financeMain.getRepayProfitRate());
			schedule.setCalculatedRate(financeMain.getRepayProfitRate());
			schedule.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
			finScheduleData.setScheduleMap(schedule);
		}

		// Load Repay profit dates
		if (StringUtils.isNotBlank(serviceInst.getRepayPftFrq())) {

			String frequency = serviceInst.getRepayPftFrq();
			Date recalFrom = grcStartDate;
			if (DateUtil.compare(recalFrom, financeMain.getGrcPeriodEndDate()) <= 0) {
				recalFrom = financeMain.getGrcPeriodEndDate();
			}
			if (!StringUtils.equals(frequency, serviceInst.getRepayFrq())) {
				recalFrom = FrequencyUtil.getNextDate(frequency, 1, recalFrom, "A", false, 0).getNextFrequencyDate();
				if (DateUtil.compare(recalFrom, rpyStartDate) > 0) {
					recalFrom = rpyStartDate;
				}
			} else {
				recalFrom = rpyStartDate;
			}

			recalFrom = DateUtil.getDatePart(recalFrom);

			finScheduleData = getSchedule(finScheduleData, serviceInst.getRepayPftFrq(), recalFrom,
					financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_PFT, false, true);
		}

		// Load Repay profit review dates
		if (financeMain.isAllowRepayRvw()) {

			if (startCalFrom == null) {

				String frequency = serviceInst.getRepayRvwFrq();
				Date recalFrom = grcStartDate;
				if (DateUtil.compare(recalFrom, financeMain.getGrcPeriodEndDate()) <= 0) {
					recalFrom = financeMain.getGrcPeriodEndDate();
				}
				if (!StringUtils.equals(frequency, serviceInst.getRepayFrq())) {
					recalFrom = FrequencyUtil.getNextDate(frequency, 1, recalFrom, "A", false, 0)
							.getNextFrequencyDate();
					if (DateUtil.compare(recalFrom, rpyStartDate) > 0) {
						recalFrom = rpyStartDate;
					}
				} else {
					recalFrom = rpyStartDate;
				}

				recalFrom = DateUtil.getDatePart(recalFrom);

				finScheduleData = getSchedule(finScheduleData, serviceInst.getRepayRvwFrq(), recalFrom,
						financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_RVW, false, true);
			} else {

				finScheduleData = getSchedule(finScheduleData, financeMain.getRepayRvwFrq(), startCalFrom,
						financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_RVW, false, true);
			}
		}

		// Load Repay capitalize dates
		if (financeMain.isAllowRepayCpz()) {

			String frequency = serviceInst.getRepayCpzFrq();
			Date recalFrom = grcStartDate;
			if (DateUtil.compare(recalFrom, financeMain.getGrcPeriodEndDate()) <= 0) {
				recalFrom = financeMain.getGrcPeriodEndDate();
			}
			if (!StringUtils.equals(frequency, serviceInst.getRepayFrq())) {
				recalFrom = FrequencyUtil.getNextDate(frequency, 1, recalFrom, "A", false, 0).getNextFrequencyDate();
				if (DateUtil.compare(recalFrom, rpyStartDate) > 0) {
					recalFrom = rpyStartDate;
				}
			} else {
				recalFrom = rpyStartDate;
			}

			recalFrom = DateUtil.getDatePart(recalFrom);

			finScheduleData = getSchedule(finScheduleData, serviceInst.getRepayCpzFrq(), recalFrom,
					financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_CPZ, false, true);
		}

		// Load Repayment dates
		finScheduleData = getSchedule(finScheduleData, serviceInst.getRepayFrq(), rpyStartDate,
				financeMain.getMaturityDate(), CalculationConstants.SCHDFLAG_RPY, false, true);

		finScheduleData.setFinanceMain(financeMain);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Preparation of Frequency based on existing
	 * 
	 * @param frq1
	 * @param frq2
	 * @return
	 */
	private static String getFrequency(String frq1, String frq2) {

		if (frq2.substring(3).equals("00")) {
			return frq1;
		}
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
			Date endDate, int scheduleFlag, boolean reCheckFlags, boolean includeStartDate) {
		logger.debug("Entering");

		// TODO: As of now reCheckFlags code is incorporated only for capitalizations. The same can be incorporated on
		// need basis

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		FrequencyDetails frequencyDetails = FrequencyUtil.getTerms(frequency, startDate, endDate, includeStartDate,
				true);

		if (frequencyDetails.getErrorDetails() != null) {
			logger.warn("Schedule Error: on condition --->  Validate frequency:" + frequency);
			finScheduleData.setErrorDetail(frequencyDetails.getErrorDetails());
		}

		if (frequencyDetails.getScheduleList() != null && frequencyDetails.getScheduleList().size() > 0) {

			for (int i = 0; i < frequencyDetails.getScheduleList().size(); i++) {
				Calendar calendar = frequencyDetails.getScheduleList().get(i);
				FinanceScheduleDetail schedule = null;

				if (finScheduleData.getScheduleMap().containsKey(
						DateUtil.getDate(DateUtil.format(calendar.getTime(), PennantConstants.dateFormat)))) {

					schedule = finScheduleData.getScheduleMap().get(
							DateUtil.getDate(DateUtil.format(calendar.getTime(), PennantConstants.dateFormat)));
				} else {
					schedule = new FinanceScheduleDetail();
					schedule.setSchDate(
							DateUtil.getDate(DateUtil.format(calendar.getTime(), PennantConstants.dateFormat)));
					schedule.setDefSchdDate(
							DateUtil.getDate(DateUtil.format(calendar.getTime(), PennantConstants.dateFormat)));
				}

				// SET various schedule flags
				// Profit On Schedule Date
				if (scheduleFlag == 0) {
					schedule.setPftOnSchDate(false);
					schedule.setRepayOnSchDate(false);
					schedule.setFrqDate(true);

					if (schedule.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) <= 0) {
						// Pay at Grace end and schedule date is grace end
						if (schedule.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0
								&& financeMain.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_GRCENDPAY)) {
							schedule.setPftOnSchDate(true);
						} else if (financeMain.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_PFT)
								|| financeMain.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_PFTCAP)
								|| financeMain.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_PRI_PFTC)
								|| financeMain.getGrcSchdMthd().equals(CalculationConstants.SCHMTHD_PFTCPZ)) {

							// TODO : SATYA - Re Verification Required
							if (FrequencyUtil.isFrqDate(financeMain.getGrcPftFrq(), schedule.getSchDate())) {
								schedule.setPftOnSchDate(true);
							}
						}

						schedule.setActRate(financeMain.getGrcPftRate());
						schedule.setBaseRate(financeMain.getGraceBaseRate());
						schedule.setSplRate(financeMain.getGraceSpecialRate());
						schedule.setMrgRate(financeMain.getGrcMargin());

						schedule.setRepayOnSchDate(false);

					} else {
						if (financeMain.isFinRepayPftOnFrq()) {
							schedule.setPftOnSchDate(true);
						}
					}

					// Profit Review On Schedule Date
				} else if (scheduleFlag == 1) {
					schedule.setRvwOnSchDate(FrequencyUtil.isFrqDate(frequency, schedule.getSchDate()));
					if (schedule.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0) {
						if (financeMain.isFinIsRateRvwAtGrcEnd()) {
							schedule.setRvwOnSchDate(true);
						}
					}
					// Profit Capitalize On Schedule Date
				} else if (scheduleFlag == 2) {

					if (reCheckFlags && (schedule.getSchDate().compareTo(startDate) == 0
							|| schedule.getSchDate().compareTo(endDate) == 0)) {
						schedule.setCpzOnSchDate(FrequencyUtil.isFrqDate(frequency, schedule.getSchDate()));
					} else {
						schedule.setCpzOnSchDate(true);
					}

					// Repayment On Schedule Date
				} else if (scheduleFlag == 3) {
					schedule.setPftOnSchDate(true);
					schedule.setRepayOnSchDate(true);
					schedule.setFrqDate(true);

					if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
						financeMain.setNumberOfTerms(frequencyDetails.getScheduleList().size());
					}
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

	private static ErrorDetail validateFinanceMain(FinanceMain financeMain,
			List<FinanceDisbursement> financeDisbursements, boolean isOverdraft) {
		logger.debug("Entering");

		String[] errorParm2 = new String[2];

		ErrorDetail errorDetails = null;
		if (financeMain == null) {
			logger.warn("Schedule Error: on condition --->  financeMain == null");
			return getErrorDetail("Schedule", "", new String[] { " " }, new String[] { " " });
		}

		errorDetails = validateFrqAndDates(financeMain);
		if (errorDetails != null) {
			return errorDetails;
		}

		if (isOverdraft) {
			return errorDetails;
		}

		if (financeDisbursements.size() < 1) {
			errorParm2[0] = Integer.toString(financeDisbursements.size());
			errorParm2[1] = Integer.toString(1);
			return getErrorDetail("Schedule", "30516", errorParm2, errorParm2);
		}

		for (int i = 0; i < financeDisbursements.size(); i++) {
			if (financeDisbursements.get(i).getDisbDate() != null
					&& financeDisbursements.get(i).getDisbAmount() == null) {
				errorParm2[0] = DateUtil.formatToShortDate(financeDisbursements.get(i).getDisbDate());
				errorParm2[1] = PennantJavaUtil.getLabel("label_DisbursementAmount");
				return getErrorDetail("Schedule", "30517", errorParm2, errorParm2);
			}
		}

		// Reset Grace Period End Date
		if (financeMain.getGrcPeriodEndDate() == null) {
			financeMain.setGrcPeriodEndDate(financeDisbursements.get(0).getDisbDate());
		}

		if (financeMain.getGrcPeriodEndDate().before(financeDisbursements.get(0).getDisbDate())) {
			errorParm2[0] = DateUtil.formatToShortDate(financeMain.getGrcPeriodEndDate());
			errorParm2[1] = DateUtil.formatToShortDate(financeDisbursements.get(0).getDisbDate());
			return getErrorDetail("Schedule", "30518", errorParm2, errorParm2);
		}

		if (financeDisbursements.get(financeDisbursements.size() - 1).getDisbDate()
				.after(financeMain.getMaturityDate())) {
			errorParm2[0] = DateUtil
					.formatToShortDate(financeDisbursements.get(financeDisbursements.size() - 1).getDisbDate());
			errorParm2[1] = DateUtil.formatToShortDate(financeMain.getMaturityDate());
			return getErrorDetail("Schedule", "30519", errorParm2, errorParm2);
		}

		logger.debug("Leaving");
		return errorDetails;
	}

	private static ErrorDetail validateFrqAndDates(FinanceMain financeMain) {
		logger.debug("Entering");

		String[] errorParm2 = new String[2];

		ErrorDetail errorDetails = null;
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

		if (financeMain.getMaturityDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_WIFinMaturityDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
		}

		if (financeMain.getNextRepayDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_WIFinNextRepaymentDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
		}

		if (financeMain.getNextRepayDate().before(financeMain.getGrcPeriodEndDate())) {
			errorParm2[0] = DateUtil.formatToShortDate(financeMain.getNextRepayDate());
			errorParm2[1] = DateUtil.formatToShortDate(financeMain.getGrcPeriodEndDate());
			return getErrorDetail("Schedule", "30522", errorParm2, errorParm2);
		}

		if (financeMain.getNextRepayPftDate() == null) {
			errorParm2[0] = PennantJavaUtil.getLabel("label_NextRepayProfitDate");
			errorParm2[1] = "";
			return getErrorDetail("Schedule", "30101", errorParm2, new String[] { "" });
		}

		if (financeMain.getNextRepayPftDate().before(financeMain.getGrcPeriodEndDate())) {
			errorParm2[0] = DateUtil.formatToShortDate(financeMain.getNextRepayPftDate());
			errorParm2[1] = DateUtil.formatToShortDate(financeMain.getGrcPeriodEndDate());
			return getErrorDetail("Schedule", "30523", errorParm2, errorParm2);
		}

		if (financeMain.getMaturityDate().before(financeMain.getNextRepayDate())) {
			errorParm2[0] = DateUtil.formatToShortDate(financeMain.getMaturityDate());
			errorParm2[1] = DateUtil.formatToShortDate(financeMain.getNextRepayDate());
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
					errorParm2[0] = DateUtil.formatToShortDate(financeMain.getNextGrcPftRvwDate());
					errorParm2[1] = DateUtil.formatToShortDate(financeMain.getGrcPeriodEndDate());
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
					errorParm2[0] = DateUtil.formatToShortDate(financeMain.getNextGrcCpzDate());
					errorParm2[1] = DateUtil.formatToShortDate(financeMain.getGrcPeriodEndDate());
					return getErrorDetail("Schedule", "30512", errorParm2, errorParm2);
				}

				if (financeMain.getNextGrcCpzDate().before(financeMain.getNextGrcPftDate())) {
					errorParm2[0] = DateUtil.formatToShortDate(financeMain.getNextGrcCpzDate());
					errorParm2[1] = DateUtil.formatToShortDate(financeMain.getNextGrcPftDate());
					// return getErrorDetail("Schedule", "30526", errorParm2, errorParm2);
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
				errorParm2[0] = DateUtil.formatToShortDate(financeMain.getNextRepayRvwDate());
				errorParm2[1] = DateUtil.formatToShortDate(financeMain.getGrcPeriodEndDate());
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

			if (SysParamUtil.isAllowed("VALIDATION_REQ_NEXT_REPAYMENT_DATE")) {
				if (financeMain.getNextRepayCpzDate().before(financeMain.getNextRepayPftDate())) {
					errorParm2[0] = DateUtil.formatToShortDate(financeMain.getNextRepayCpzDate());
					errorParm2[1] = DateUtil.formatToShortDate(financeMain.getNextRepayPftDate());
					return getErrorDetail("Schedule", "30528", errorParm2, errorParm2);
				}
			}
		}

		logger.debug("Leaving");
		return errorDetails;
	}

	private static ErrorDetail getErrorDetail(String errorField, String errorCode, String[] errParm,
			String[] valueParm) {
		ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorField, errorCode, errParm, valueParm),
				SessionUserDetails.getUserLanguage());

		logger.warn("Schedule Error: on condition --->  " + errorDetail.getCode() + "-" + errorDetail.getError());
		return errorDetail;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	private static Date formatDate(Date date) {
		return DateUtil.getDate(DateUtil.format(date, PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);
	}

}
