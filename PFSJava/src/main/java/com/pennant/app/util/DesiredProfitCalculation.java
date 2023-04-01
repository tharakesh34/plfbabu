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
 * FileName : DesiredProfitCalculation.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-03-2015 *
 * 
 * Modified Date : 26-03-2015 *
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.rits.cloning.Cloner;

public class DesiredProfitCalculation {
	private static final Logger logger = LogManager.getLogger(DesiredProfitCalculation.class);

	private BigDecimal totDesiredProfit = BigDecimal.ZERO;

	/**
	 * Method to calculate desired profit amount.
	 * 
	 * There are two types of process executions to calculate desired profit amount.
	 * 
	 * <b style="color:blue"> <i>
	 * <ul>
	 * a) Planned Deferment
	 * </ul>
	 * <ul>
	 * </ul>
	 * </i> </b>
	 * 
	 * <p>
	 * For <b style="color:red">Planned Deferment</b> process , need to extend Number of terms based on Tenure and Max
	 * Deferments per year. Get the Profit amount with newly added Terms (Actual Number of Terms + (Tenure in Years *
	 * Planned deferments per Year) )
	 * </p>
	 * 
	 * <p>
	 * For <b style="color:red;"></b> process , need to clear the Down payment amount which was shared to Bank account
	 * and calculate the Total profit amount without DownpayBank value. Setting DownpayBank to ZERO and proceed further
	 * calculation as same
	 * </p>
	 * 
	 * @param orgFinSchdData   : Actual Schedule Data Object before creating Schedule
	 * 
	 * @param finRepayPftOnFrq : Payment allowed for different pft & repay frequencies
	 * 
	 * @param repayPftFrq      : Repayment's period profit frequency
	 * 
	 * @param repayFrq         : Repayment's period frequency
	 * 
	 * @param nextRepayPftDate : Next Repayment's Profit date entered manually by user
	 * 
	 * @param gpEndDate        : Grace period end date
	 * 
	 * @param nextRepayDate    : Next Repayment's date entered manually by user
	 * 
	 */
	public static BigDecimal getDesiredProfit(FinScheduleData orgFinSchdData, boolean finRepayPftOnFrq,
			String repayPftFrq, String repayFrq, Date nextRepayPftDate, Date gpEndDate, Date nextRepayDate) {

		return new DesiredProfitCalculation(orgFinSchdData, finRepayPftOnFrq, repayPftFrq, repayFrq, nextRepayPftDate,
				gpEndDate, nextRepayDate).getTotDesiredProfit();

	}

	/**
	 * Constructor definition for desired profit calculation
	 * 
	 * @param orgFinSchdData
	 * @param finRepayPftOnFrq
	 * @param repayPftFrq
	 * @param repayFrq
	 * @param nextRepayPftDate
	 * @param gpEndDate
	 * @param nextRepayDate
	 */
	public DesiredProfitCalculation(FinScheduleData orgFinSchdData, boolean finRepayPftOnFrq, String repayPftFrq,
			String repayFrq, Date nextRepayPftDate, Date gpEndDate, Date nextRepayDate) {
		logger.debug("Entering");

		// proceed only at least any one case is exists
		if (orgFinSchdData.getFinanceMain().getPlanDeferCount() <= 0) {
			return;
		}

		nextRepayPftDate = DateUtil.parse(DateUtil.format(nextRepayPftDate, PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);

		gpEndDate = DateUtil.parse(DateUtil.format(gpEndDate, PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);

		nextRepayDate = DateUtil.parse(DateUtil.format(nextRepayDate, PennantConstants.DBDateFormat),
				PennantConstants.DBDateFormat);

		// Calculation process based on Repay period rate type
		String repayRateType = orgFinSchdData.getFinanceMain().getRepayRateBasis();

		if (repayRateType.equals(CalculationConstants.RATE_BASIS_F)
				|| repayRateType.equals(CalculationConstants.RATE_BASIS_C)) {

			calDesPftOnFlatRateBasis(orgFinSchdData, finRepayPftOnFrq, repayPftFrq, repayFrq, nextRepayPftDate,
					gpEndDate, nextRepayDate);

		} else if (repayRateType.equals(CalculationConstants.RATE_BASIS_R)) {

			calDesPftOnReduceRateBasis(orgFinSchdData, finRepayPftOnFrq, repayPftFrq, repayFrq, nextRepayPftDate,
					gpEndDate, nextRepayDate);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for calculation of desired profit for "FLAT" rate basis
	 * 
	 * @param orgFinSchdData
	 * @param finRepayPftOnFrq
	 * @param repayPftFrq
	 * @param repayFrq
	 * @param nextRepayPftDate
	 * @param gpEndDate
	 * @param nextRepayDate
	 */
	private void calDesPftOnFlatRateBasis(FinScheduleData orgFinSchdData, boolean finRepayPftOnFrq, String repayPftFrq,
			String repayFrq, Date nextRepayPftDate, Date gpEndDate, Date nextRepayDate) {

		BigDecimal totDesPftAmount = BigDecimal.ZERO;
		FinanceMain financeMain = orgFinSchdData.getFinanceMain();

		int repayTerms = financeMain.getNumberOfTerms();
		BigDecimal finAmount = financeMain.getFinAmount();
		BigDecimal feeAmount = financeMain.getFeeChargeAmt();
		BigDecimal downpayment = financeMain.getDownPayment();
		Date finStartDate = financeMain.getFinStartDate();
		Date maturityDate = financeMain.getMaturityDate();

		// Check Planned deferment Program Setup
		int minCheckDays = orgFinSchdData.getFinanceType().getFddLockPeriod();
		if (orgFinSchdData.getFinanceMain().getPlanDeferCount() > 0) {
			repayTerms = financeMain.getNumberOfTerms() + financeMain.getDefferments();

			// Maturity Date Recalculation using Number of Terms
			List<Calendar> scheduleDateList = null;
			if (finRepayPftOnFrq) {

				Date nextPftDate = nextRepayPftDate;
				if (nextPftDate == null) {
					nextPftDate = FrequencyUtil
							.getNextDate(repayPftFrq, 1, gpEndDate, HolidayHandlerTypes.MOVE_NONE, false, minCheckDays)
							.getNextFrequencyDate();
				}

				scheduleDateList = FrequencyUtil
						.getNextDate(repayPftFrq, repayTerms, nextPftDate, HolidayHandlerTypes.MOVE_NONE, true, 0)
						.getScheduleList();
			} else {

				Date nextRpyDate = nextRepayDate;
				if (nextRpyDate == null) {
					nextRpyDate = FrequencyUtil
							.getNextDate(repayFrq, 1, gpEndDate, HolidayHandlerTypes.MOVE_NONE, false, minCheckDays)
							.getNextFrequencyDate();
				}

				scheduleDateList = FrequencyUtil
						.getNextDate(repayFrq, repayTerms, nextRpyDate, HolidayHandlerTypes.MOVE_NONE, true, 0)
						.getScheduleList();
			}

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				maturityDate = calendar.getTime();
			}
		}

		// Grace Period desired profit calculation
		BigDecimal gracePft = CalculationUtil.calInterest(finStartDate, gpEndDate,
				finAmount.add(feeAmount).subtract(downpayment), financeMain.getGrcProfitDaysBasis(),
				financeMain.getGrcPftRate());

		// Repay Period desired profit calculation
		BigDecimal rpyPft = CalculationUtil.calInterest(gpEndDate, maturityDate,
				finAmount.add(feeAmount).subtract(downpayment), financeMain.getProfitDaysBasis(),
				financeMain.getRepayProfitRate());

		gracePft = gracePft.setScale(0, RoundingMode.HALF_DOWN);
		rpyPft = rpyPft.setScale(0, RoundingMode.HALF_DOWN);

		if (financeMain.isAllowGrcPeriod() && (StringUtils.trimToEmpty(financeMain.getGrcSchdMthd())
				.equals(CalculationConstants.SCHMTHD_NOPAY)
				|| StringUtils.trimToEmpty(financeMain.getGrcSchdMthd()).equals(PennantConstants.List_Select))) {
			totDesPftAmount = gracePft.add(rpyPft);
		} else {
			totDesPftAmount = rpyPft;
		}

		setTotDesiredProfit(totDesPftAmount);
	}

	/**
	 * Method for calculation of desired profit for "REDUCE" rate basis
	 * 
	 * @param orgFinSchdData
	 * @param finRepayPftOnFrq
	 * @param repayPftFrq
	 * @param repayFrq
	 * @param nextRepayPftDate
	 * @param gpEndDate
	 * @param nextRepayDate
	 */
	private void calDesPftOnReduceRateBasis(FinScheduleData orgFinSchdData, boolean finRepayPftOnFrq,
			String repayPftFrq, String repayFrq, Date nextRepayPftDate, Date gpEndDate, Date nextRepayDate) {

		BigDecimal totDesPftAmount = BigDecimal.ZERO;
		Cloner cloner = new Cloner();
		FinScheduleData planDeferSchdData = cloner.deepClone(orgFinSchdData);
		FinanceMain planFinMain = planDeferSchdData.getFinanceMain();

		// Maturity Date Recalculation using Number of Terms
		int minCheckDays = planDeferSchdData.getFinanceType().getFddLockPeriod();
		if (planFinMain.getPlanDeferCount() > 0) {

			planFinMain.setNumberOfTerms(planFinMain.getNumberOfTerms() + planFinMain.getDefferments());

			List<Calendar> scheduleDateList = null;
			if (finRepayPftOnFrq) {

				Date nextPftDate = nextRepayPftDate;
				if (nextPftDate == null) {
					nextPftDate = FrequencyUtil
							.getNextDate(repayPftFrq, 1, gpEndDate, HolidayHandlerTypes.MOVE_NONE, false, minCheckDays)
							.getNextFrequencyDate();
				}

				scheduleDateList = FrequencyUtil.getNextDate(repayPftFrq, planFinMain.getNumberOfTerms(), nextPftDate,
						HolidayHandlerTypes.MOVE_NONE, true, 0).getScheduleList();
			} else {

				Date nextRpyDate = nextRepayDate;
				if (nextRpyDate == null) {
					nextRpyDate = FrequencyUtil
							.getNextDate(repayFrq, 1, gpEndDate, HolidayHandlerTypes.MOVE_NONE, false, minCheckDays)
							.getNextFrequencyDate();
				}

				scheduleDateList = FrequencyUtil.getNextDate(repayFrq, planFinMain.getNumberOfTerms(), nextRpyDate,
						HolidayHandlerTypes.MOVE_NONE, true, 0).getScheduleList();
			}

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				planFinMain.setMaturityDate(calendar.getTime());
			}
		}

		planDeferSchdData = ScheduleGenerator.getNewSchd(planDeferSchdData);
		planDeferSchdData = ScheduleCalculator.getPlanDeferPft(planDeferSchdData);

		FinanceMain planDefFinMain = planDeferSchdData.getFinanceMain();
		if (planDefFinMain.isAllowGrcPeriod() && (StringUtils.trimToEmpty(planDefFinMain.getGrcSchdMthd())
				.equals(CalculationConstants.SCHMTHD_NOPAY)
				|| StringUtils.trimToEmpty(planDefFinMain.getGrcSchdMthd()).equals(PennantConstants.List_Select))) {
			totDesPftAmount = planDefFinMain.getTotalGrossPft();
		} else {
			totDesPftAmount = planDefFinMain.getTotalGrossPft().subtract(planDefFinMain.getTotalGrossGrcPft());
		}

		planDeferSchdData = null;

		setTotDesiredProfit(totDesPftAmount);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public BigDecimal getTotDesiredProfit() {
		return totDesiredProfit;
	}

	public void setTotDesiredProfit(BigDecimal totDesiredProfit) {
		this.totDesiredProfit = totDesiredProfit;
	}

}
