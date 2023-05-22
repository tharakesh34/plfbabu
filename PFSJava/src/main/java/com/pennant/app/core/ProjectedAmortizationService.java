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
 * * FileName : ProjectedAmortizationService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-01-2018 * *
 * Modified Date : 16-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-01-2018 Pennant 0.1 * * 09-03-2018 Satya 0.2 PSD Ticket : 124705 * IND AS : Income / Expense * Amortization. * *
 * 16-05-2018 Satya 0.3 PSD Ticket : 126328 * IND AS : Performance related * changes. * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.amtmasters.ExpenseTypeDAO;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.finance.ProjectedAmortization;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.eod.step.StepUtil;

public class ProjectedAmortizationService {
	private static Logger logger = LogManager.getLogger(ProjectedAmortizationService.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private ProjectedAmortizationDAO projectedAmortizationDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private ExpenseTypeDAO expenseTypeDAO;
	private FeeTypeDAO feeTypeDAO;

	public static List<FeeType> feeTypes = new ArrayList<>();
	public static List<ExpenseType> expenseTypes = new ArrayList<>();

	public void prepareMonthEndAccruals(CustEODEvent custEODEvent) throws Exception {
		EventProperties eventProperties = custEODEvent.getEventProperties();
		Date curMonthStart = eventProperties.getMonthStartDate();
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();

			/* Ignore Accrual calculation after maturity date */
			Date maturityDate = fm.getMaturityDate();
			maturityDate = DateUtil.getDatePart(maturityDate);
			fm.setMaturityDate(maturityDate);

			if (maturityDate.compareTo(curMonthStart) < 0) {
				continue;
			}

			// Ignore ACCRUAL calculation when loan is WriteOff
			if (FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(fm.getClosingStatus())) {
				continue;
			}

			/*
			 * For ODFacility Finances initially doesn't have any Schedules
			 */
			List<FinanceScheduleDetail> schdDetails = finEODEvent.getFinanceScheduleDetails();
			FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();

			if (schdDetails != null && !schdDetails.isEmpty()) {
				// AMZ Method : execute rule and get amortization method
				if (StringUtils.isBlank(finPftDetail.getAMZMethod())) {
					String amzMethod = identifyAMZMethod(finEODEvent, custEODEvent.getAmzMethodRule());
					finPftDetail.setAMZMethod(amzMethod);
				}
				String finReference = fm.getFinReference();
				logger.info("Calculating Monthend accruals for the FinReference >>{}", finReference);
				// calculate MonthEnd ACCRUALS / POS
				processMonthEndAccruals(finEODEvent, custEODEvent.getEodDate());

				// Amortization Percentage
				calculateAMZPercentage(finEODEvent);
				logger.info("Calculating Monthend accruals for the FinReference >>{} completed.", finReference);
			}
		}

	}

	/**
	 * 1. Income / Expense Amortization Calculation Based on Method </br>
	 * 2. Interest Method - All Finances with interest greater than ZERO except FLEXI Finance types </br>
	 * 3. Opening Principal Balance Method - ZERO Interest Finances </br>
	 * 4. Straight Line Method - FLEXI/OD Finances and Floating rate type </br>
	 * 
	 * @param finEODEvent
	 * @param custEODEvent
	 * @return
	 */
	public String identifyAMZMethod(FinEODEvent finEODEvent, String amzMethodRule) {
		// Prepare Map and Rule Execution
		Map<String, Object> dataMap = getDataMap(finEODEvent);
		String ruleResult = (String) RuleExecutionUtil.executeRule(amzMethodRule, dataMap, "", RuleReturnType.STRING);

		return ruleResult;
	}

	private void processMonthEndAccruals(FinEODEvent finEODEvent, Date amzMonth) throws Exception {
		FinanceMain fm = finEODEvent.getFinanceMain();
		long finID = fm.getFinID();

		EventProperties eventProperties = fm.getEventProperties();

		Date curMonthStart = DateUtil.getMonthStart(amzMonth);
		Date prvMonthEndDate = DateUtil.addDays(curMonthStart, -1);
		ProjectedAccrual prvProjAccrual = null;

		/* Month End ACCRUAL Calculation configuration from SMT parameter */
		boolean calFromFinStartDate = true;
		if (eventProperties.isParameterLoaded()) {
			calFromFinStartDate = eventProperties.isCalAccrualFromStart();
		} else {
			calFromFinStartDate = SysParamUtil.isAllowed("MONTHENDACC_FROMFINSTARTDATE");
		}

		if (!calFromFinStartDate) {
			prvProjAccrual = this.projectedAmortizationDAO.getPrvProjectedAccrual(finID, prvMonthEndDate, "_WORK");
		}

		// calculate month end and partial settlements ACCRUALS / POS
		calculateMonthEndAccruals(finEODEvent, prvProjAccrual, amzMonth, calFromFinStartDate);
	}

	private void calculateMonthEndAccruals(FinEODEvent finEODEvent, ProjectedAccrual prvProjAccrual, Date amzMonth,
			boolean calFromFinStartDate) throws Exception {
		logger.debug(Literal.ENTERING);

		Map<Date, ProjectedAccrual> map = new HashMap<>(1);
		List<ProjectedAccrual> projAccrualList = new ArrayList<>(1);
		List<Date> monthsCopy = new ArrayList<Date>(1);
		List<Date> months = new ArrayList<Date>(1);

		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;
		Date curSchdMonthEnd = null;
		Date prvMonthEnd = null;
		Date newMonth = null;

		int prvCumDays = 0;
		int curCumDays = 0;
		BigDecimal cumulativePft = BigDecimal.ZERO;
		BigDecimal cumulativePOS = BigDecimal.ZERO;
		BigDecimal partialCumPft = BigDecimal.ZERO;
		BigDecimal partialCumPOS = BigDecimal.ZERO;

		BigDecimal closingBalPS = BigDecimal.ZERO;
		BigDecimal totalProfit = BigDecimal.ZERO;

		boolean isPartialPay = false;
		boolean isPartialPayOnMonthEnd = false;

		FinanceMain fm = finEODEvent.getFinanceMain();
		String amzMethod = finEODEvent.getFinProfitDetail().getAMZMethod();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		Date appDateMonthStart = DateUtil.getMonthStart(amzMonth);
		Date appDateMonthEnd = DateUtil.getMonthEnd(amzMonth);
		Date maturityDate = DateUtil.getDatePart(fm.getMaturityDate());

		// Prepare Months list From FinStartDate to MaturityDate
		if (calFromFinStartDate || fm.getFinStartDate().compareTo(amzMonth) >= 0) {
			newMonth = new Date(DateUtil.getMonthEnd(fm.getFinStartDate()).getTime());

		} else {
			prvMonthEnd = DateUtil.addDays(appDateMonthStart, -1);
			newMonth = new Date(appDateMonthEnd.getTime());

			if (prvProjAccrual != null) {
				prvCumDays = prvProjAccrual.getCumulativeDays();
				cumulativePOS = prvProjAccrual.getCumulativePOS();
				cumulativePft = prvProjAccrual.getCumulativeAccrued();
			}
		}

		Date mdtMonthEnd = DateUtil.getMonthEnd(maturityDate);

		while (mdtMonthEnd.compareTo(newMonth) >= 0) {
			months.add(DateUtil.getDatePart(((Date) newMonth.clone())));
			newMonth = DateUtil.addMonths(newMonth, 1);
			newMonth = DateUtil.getMonthEnd(newMonth);
		}
		monthsCopy.addAll(months);

		// looping schedules
		for (int i = 0; i < schedules.size(); i++) {
			curSchd = schedules.get(i);
			curSchdDate = curSchd.getSchDate();
			curSchdMonthEnd = DateUtil.getMonthEnd(curSchdDate);

			totalProfit = totalProfit.add(curSchd.getProfitCalc());

			/**
			 * Calculate ACCRUAL from current month onwards (OR) Calculation from finance start date (back dated
			 * finances : prvProjAccrual is null)
			 */
			if (prvProjAccrual != null && !calFromFinStartDate && curSchdDate.compareTo(appDateMonthStart) < 0) {
				continue;
			}

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schedules.get(i - 1);
			}
			if (i == schedules.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schedules.get(i + 1);
			}

			if (!curSchd.isRepayOnSchDate()) {
				i++;
				continue;
			}

			prvSchdDate = prvSchd.getSchDate();
			nextSchdDate = nextSchd.getSchDate();

			// START : Previous Closing Balance for Partial Settlements(PS)
			isPartialPay = false;
			isPartialPayOnMonthEnd = false;

			if (curSchd.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) {
				isPartialPay = true;
				BigDecimal curInstAmt = curSchd.getPrincipalSchd().add(curSchd.getProfitSchd());
				BigDecimal schdAmount = curInstAmt.subtract(curSchd.getPartialPaidAmt());

				// partial settlement on installment date
				if (schdAmount.compareTo(BigDecimal.ZERO) > 0) {
					closingBalPS = curSchd.getClosingBalance().add(curSchd.getPartialPaidAmt());
				} else {
					closingBalPS = prvSchd.getClosingBalance();
				}

				if (curSchdDate.compareTo(curSchdMonthEnd) == 0) {
					isPartialPayOnMonthEnd = true;
				}
			}
			// END

			for (Date curMonthEnd : monthsCopy) {
				// INcome Amortization (Interest Method) and ALM
				BigDecimal schdPftAmz = BigDecimal.ZERO;
				BigDecimal curPftAmz = BigDecimal.ZERO;
				BigDecimal prvPftAmz = BigDecimal.ZERO;
				BigDecimal pftAmz = BigDecimal.ZERO;

				// INcome Amortization (Principal Balance Method)
				BigDecimal schdPOSAmz = BigDecimal.ZERO;
				BigDecimal curPOSAmz = BigDecimal.ZERO;
				BigDecimal prvPOSAmz = BigDecimal.ZERO;
				BigDecimal posAmz = BigDecimal.ZERO;

				// Customer Profitability RFT
				BigDecimal schdAvgPOS = BigDecimal.ZERO;
				BigDecimal curAvgPOS = BigDecimal.ZERO;
				BigDecimal prvAvgPOS = BigDecimal.ZERO;
				BigDecimal avgPOS = BigDecimal.ZERO;

				boolean isSchdAmz = false;
				ProjectedAccrual prjAcc = null;

				if (map.containsKey(curMonthEnd)) {
					prjAcc = map.get(curMonthEnd);
				} else {
					prjAcc = new ProjectedAccrual();
					prjAcc.setFinID(fm.getFinID());
					prjAcc.setFinReference(fm.getFinReference());
					prjAcc.setAccruedOn(curMonthEnd);
					prjAcc.setMonthEnd(true);

					// Prepare Map and List
					projAccrualList.add(prjAcc);
					map.put(curMonthEnd, prjAcc);

					if (map.containsKey(prvMonthEnd)) {
						prvProjAccrual = map.get(prvMonthEnd);
					}
				}

				// START : Actual ACCRUAL calculation includes current date
				Date nextMonthStart = DateUtil.addDays(curMonthEnd, 1);
				Date curMonthStart = DateUtil.getMonthStart(curMonthEnd);

				// Schedules between Previous MonthEnd and CurMonthEnd and past
				// dated finances( Weekly..)
				if ((prvSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0)
						|| prvProjAccrual == null) {

					isSchdAmz = true;
					schdPftAmz = curSchd.getProfitCalc();

					if (curSchdDate.compareTo(DateUtil.getDatePart(fm.getFinStartDate())) != 0) {
						schdPOSAmz = prvSchd.getClosingBalance();

						int days = DateUtil.getDaysBetween(curSchdDate, prvSchdDate);
						schdAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
					}
				}

				// multiple months between two schedules (Quarterly, Half Yearly
				// ..)
				if (curMonthEnd.compareTo(curSchdDate) < 0) {

					int days = DateUtil.getDaysBetween(nextMonthStart, curMonthStart);
					int daysInCurPeriod = DateUtil.getDaysBetween(curSchdDate, prvSchdDate);

					prvPftAmz = curSchd.getProfitCalc().multiply(new BigDecimal(days))
							.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
					prvPOSAmz = prvSchd.getClosingBalance().multiply(new BigDecimal(days))
							.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
					prvAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));

				} else {

					// Calculation From CurSchdDate to Current MonthEnd
					if (curMonthEnd.compareTo(prvSchdDate) >= 0 && curMonthEnd.compareTo(nextSchdDate) < 0) {

						int days = DateUtil.getDaysBetween(nextMonthStart, curSchdDate);
						int daysInCurPeriod = DateUtil.getDaysBetween(nextSchdDate, curSchdDate);

						curPftAmz = nextSchd.getProfitCalc().multiply(new BigDecimal(days))
								.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
						curPOSAmz = curSchd.getClosingBalance().multiply(new BigDecimal(days))
								.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
						curAvgPOS = curSchd.getClosingBalance().multiply(new BigDecimal(days));
					}

					// Calculation From CurSchdDate to Previous MonthEnd
					if (prvMonthEnd != null && !isSchdAmz) {

						int days = DateUtil.getDaysBetween(curSchdDate, curMonthStart);
						int daysInCurPeriod = DateUtil.getDaysBetween(curSchdDate, prvSchdDate);

						prvPftAmz = curSchd.getProfitCalc().multiply(new BigDecimal(days))
								.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
						prvPOSAmz = prvSchd.getClosingBalance().multiply(new BigDecimal(days))
								.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
						prvAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
					}
				}

				// Total ACCRUED, POS AND Average POS
				pftAmz = schdPftAmz.add(prvPftAmz).add(curPftAmz).add(prjAcc.getPftAmz());
				posAmz = schdPOSAmz.add(prvPOSAmz).add(curPOSAmz).add(prjAcc.getPOSAccrued());
				avgPOS = schdAvgPOS.add(prvAvgPOS).add(curAvgPOS).add(prjAcc.getAvgPOS());

				// Adjust remaining profit to maturity MonthEnd to avoid
				// rounding issues
				if (DateUtil.getMonthEnd(maturityDate).compareTo(curMonthEnd) == 0
						&& curSchdDate.compareTo(maturityDate) == 0 && !isPartialPay) {

					// To avoid negative values on maturity due to rounding
					pftAmz = BigDecimal.ZERO;

					if (totalProfit.compareTo(cumulativePft) > 0) {
						pftAmz = totalProfit.subtract(cumulativePft);
					}
				}

				prjAcc.setPftAmz(pftAmz);
				prjAcc.setPOSAccrued(posAmz);
				prjAcc.setAvgPOS(avgPOS);

				// END : Actual ACCRUAL Calculation

				// START : Process Partial Settlements
				if (curSchdMonthEnd.compareTo(curMonthEnd) == 0 && isPartialPay && !isPartialPayOnMonthEnd) {

					// ACCRUAL Calculation on Partial Settlement
					ProjectedAccrual partialProjAcc = processPartialSettlements(curSchd, prjAcc, curPftAmz, curPOSAmz,
							cumulativePft, cumulativePOS, partialCumPft, partialCumPOS, fm.getFinStartDate());

					// calculate AMZ percentage for Partial Settlement
					BigDecimal partialAMZPerc = calPartialAMZPerc(curSchd.getPartialPaidAmt(), closingBalPS, amzMethod);
					partialProjAcc.setPartialAMZPerc(partialAMZPerc);

					partialCumPft = partialCumPft.add(partialProjAcc.getPftAmz());
					partialCumPOS = partialCumPOS.add(partialProjAcc.getPOSAccrued());
					projAccrualList.add(partialProjAcc);
				}
				// END

				// START : Remove current month end from list, 2nd one is to
				// handle Quarterly, Half Yearly (Maturity month end)..
				if (curMonthEnd.compareTo(nextSchdDate) < 0 || curSchdDate.compareTo(maturityDate) == 0) {

					// No of Days and Cumulative Days
					curCumDays = getCumulativeDays(fm, curMonthEnd);
					int noOfDays = curCumDays - prvCumDays;
					prjAcc.setNoOfDays(noOfDays);
					prjAcc.setCumulativeDays(curCumDays);
					prvCumDays = curCumDays;

					cumulativePft = cumulativePft.add(prjAcc.getPftAmz());
					prjAcc.setPftAccrued(prjAcc.getPftAmz());
					prjAcc.setCumulativeAccrued(cumulativePft);

					cumulativePOS = cumulativePOS.add(prjAcc.getPOSAccrued());
					prjAcc.setCumulativePOS(cumulativePOS);

					// Average POS
					if (noOfDays > 0) {
						BigDecimal monthlyAvgPOS = prjAcc.getAvgPOS().divide(new BigDecimal(noOfDays), 0,
								RoundingMode.HALF_DOWN);
						prjAcc.setAvgPOS(monthlyAvgPOS);
					}

					// calculate AMZ percentage for Partial Settlement on month
					// end
					if (isPartialPayOnMonthEnd) {

						prjAcc.setPartialPaidAmt(curSchd.getPartialPaidAmt());
						BigDecimal partialAMZPerc = calPartialAMZPerc(curSchd.getPartialPaidAmt(), closingBalPS,
								amzMethod);
						prjAcc.setPartialAMZPerc(partialAMZPerc);
					}

					partialCumPft = BigDecimal.ZERO;
					partialCumPOS = BigDecimal.ZERO;

					prvMonthEnd = curMonthEnd;
					months.remove(curMonthEnd);
					monthsCopy = new ArrayList<Date>(months);
				}
				// END

				// Current schedule Data for ALM Process (MISSED IN TABLE LEVEL)
				if (curSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0) {

					if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {

						prjAcc.setSchdDate(curSchdDate);
						prjAcc.setSchdPri(curSchd.getPrincipalSchd().subtract(curSchd.getCpzAmount()));
						prjAcc.setSchdPft(curSchd.getProfitSchd().add(curSchd.getCpzAmount()));
						prjAcc.setSchdTot(curSchd.getRepayAmount());
					}
				}

				if (curMonthEnd.compareTo(curSchdDate) >= 0) {
					break;
				}
			}
		}

		finEODEvent.setProjectedAccrualList(projAccrualList);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param prvProjAccrual
	 * @param finSchdTotals
	 * @param curMonthEndDate
	 * @param maturityDate
	 */
	public ProjectedAccrual prepareMaturityMonthProjAcc(ProjectedAccrual prvProjAccrual,
			List<FinanceScheduleDetail> schedules, long finID, String finReference, Date curMonthEnd) {

		Date prvSchdDate = null;
		Date curSchdDate = null;
		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;

		int noOfDays = 0;
		BigDecimal posAmz = BigDecimal.ZERO;
		BigDecimal avgPOS = BigDecimal.ZERO;
		BigDecimal totalProfit = BigDecimal.ZERO;
		boolean isSchdPartCalReq = true;

		Date curMonthStart = DateUtil.getMonthStart(curMonthEnd);

		// looping schedules
		for (int i = 1; i < schedules.size(); i++) {

			curSchd = schedules.get(i);
			curSchdDate = curSchd.getSchDate();

			prvSchd = schedules.get(i - 1);
			prvSchdDate = prvSchd.getSchDate();

			totalProfit = totalProfit.add(curSchd.getProfitCalc());

			if (prvProjAccrual != null && curSchdDate.compareTo(curMonthStart) < 0) {
				continue;
			}

			// Income AMZ (Principal Balance Method), Customer Profitability RFT
			int days = 0;
			BigDecimal schdPOSAmz = BigDecimal.ZERO;
			BigDecimal schdAvgPOS = BigDecimal.ZERO;

			// Calculation From CurSchdDate to Previous MonthEnd
			if (prvProjAccrual != null && isSchdPartCalReq) {

				isSchdPartCalReq = false;
				days = DateUtil.getDaysBetween(curSchdDate, curMonthStart);
				int daysInCurPeriod = DateUtil.getDaysBetween(curSchdDate, prvSchdDate);

				schdPOSAmz = prvSchd.getClosingBalance().multiply(new BigDecimal(days))
						.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
				schdAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));

			} else {
				schdPOSAmz = prvSchd.getClosingBalance();
				days = DateUtil.getDaysBetween(curSchdDate, prvSchdDate);
				schdAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
			}

			// Total Days, POS AND Average POS
			noOfDays = noOfDays + days;
			posAmz = posAmz.add(schdPOSAmz);
			avgPOS = avgPOS.add(schdAvgPOS);
		}

		// Prepare Maturity Month ProjectedAccruals
		ProjectedAccrual projAccrual = new ProjectedAccrual();

		if (prvProjAccrual == null) {
			prvProjAccrual = new ProjectedAccrual();
			prvProjAccrual.setFinID(finID);
			prvProjAccrual.setFinReference(finReference);
		}

		projAccrual.setAccruedOn(curMonthEnd);
		projAccrual.setFinID(prvProjAccrual.getFinID());
		projAccrual.setFinReference(prvProjAccrual.getFinReference());

		// ACCRUAL Calculation
		BigDecimal pftAccrued = totalProfit.subtract(prvProjAccrual.getCumulativeAccrued());
		if (pftAccrued.compareTo(BigDecimal.ZERO) < 0) {
			pftAccrued = BigDecimal.ZERO;
		}
		projAccrual.setPftAccrued(pftAccrued);
		projAccrual.setCumulativeAccrued(totalProfit);

		// Calculate POS
		projAccrual.setPOSAccrued(posAmz);
		projAccrual.setCumulativePOS(prvProjAccrual.getCumulativePOS().add(posAmz));

		// Days Calculation
		projAccrual.setNoOfDays(noOfDays);
		projAccrual.setCumulativeDays(prvProjAccrual.getCumulativeDays() + noOfDays);

		// Average POS
		if (noOfDays > 0) {
			BigDecimal maturityAvgPOS = avgPOS.divide(new BigDecimal(noOfDays), 0, RoundingMode.HALF_DOWN);
			projAccrual.setAvgPOS(maturityAvgPOS);
		}

		projAccrual.setAMZPercentage(new BigDecimal(100));
		projAccrual.setMonthEnd(true);

		return projAccrual;
	}

	/**
	 * Method for calculate AMZ Percentage based on AMZ method </br>
	 * 
	 * 1. Interest Method : </br>
	 * -- Percentage calculated on unamortized profit (Total Profit - Previous Month Cumulative Profit) </br>
	 * 
	 * 2. Opening Principal Balance Method : </br>
	 * -- Percentage calculated on unamortized POS (Total POS - Previous Month Cumulative POS) </br>
	 * 
	 * 3. StraightLine Method : </br>
	 * -- AMZ percentage is ZERO for Partial Settlements </br>
	 * -- Calc. Percentage is Cumulative Percentage not Monthly like Interest and Principal Methods. </br>
	 *
	 *
	 * @param finEODEvent
	 * @return
	 */
	private void calculateAMZPercentage(FinEODEvent finEODEvent) {
		logger.debug(Literal.ENTERING);

		BigDecimal amzPercentage = BigDecimal.ZERO;
		FinanceMain finMain = finEODEvent.getFinanceMain();
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();

		List<ProjectedAccrual> projAccList = finEODEvent.getProjectedAccrualList();
		if (CollectionUtils.isEmpty(projAccList)) {
			return;
		}

		// sort ProjectedAccruals
		projAccList = sortProjectedAccruals(projAccList);
		ProjectedAccrual maturityProjAcc = projAccList.get(projAccList.size() - 1);

		BigDecimal totalPOS = maturityProjAcc.getCumulativePOS();
		BigDecimal totalProfit = maturityProjAcc.getCumulativeAccrued();

		// income amortization methods
		String amzMethod = StringUtils.trimToEmpty(finPftDetail.getAMZMethod());

		switch (amzMethod) {

		case AmortizationConstants.AMZ_METHOD_INTEREST:

			BigDecimal pftAccrued = BigDecimal.ZERO;
			BigDecimal prvCumPft = BigDecimal.ZERO;

			for (int i = 0; i < projAccList.size(); i++) {

				ProjectedAccrual projAccrual = projAccList.get(i);

				if (i == 0) {
					prvCumPft = projAccrual.getCumulativeAccrued().subtract(projAccrual.getPftAmz());
				}

				// AMZ Percentage, use Previous Cumulative's for calculation
				pftAccrued = projAccrual.getCumulativeAccrued().subtract(prvCumPft);
				amzPercentage = calPftAMZPercentage(totalProfit, pftAccrued, prvCumPft);
				prvCumPft = projAccrual.getCumulativeAccrued();

				projAccrual.setAMZPercentage(amzPercentage);
			}
			break;

		case AmortizationConstants.AMZ_METHOD_OPENINGPRIBAL:

			BigDecimal posAccrued = BigDecimal.ZERO;
			BigDecimal prvCumPOS = BigDecimal.ZERO;

			for (int i = 0; i < projAccList.size(); i++) {

				ProjectedAccrual projAccrual = projAccList.get(i);
				if (i == 0) {
					prvCumPOS = projAccrual.getCumulativePOS().subtract(projAccrual.getPOSAccrued());
				}

				// AMZ Percentage, use Previous Cumulative's for calculation
				posAccrued = projAccrual.getCumulativePOS().subtract(prvCumPOS);
				amzPercentage = calPriAMZPercentage(totalPOS, posAccrued, prvCumPOS);
				prvCumPOS = projAccrual.getCumulativePOS();

				projAccrual.setAMZPercentage(amzPercentage);
			}

			break;

		case AmortizationConstants.AMZ_METHOD_STRAIGHTLINE:

			for (ProjectedAccrual projAccrual : projAccList) {

				// AMZ percentage is ZERO for partial settlement entries, AMZ
				// Percentage is Cumulative Not Monthly.
				amzPercentage = calDayAMZPercentage(finMain, projAccrual.getCumulativeDays(), projAccrual.isMonthEnd());
				projAccrual.setAMZPercentage(amzPercentage);
			}
			break;

		default:
			break;
		}

		logger.debug(Literal.LEAVING);
	}

	private ProjectedAccrual processPartialSettlements(FinanceScheduleDetail schd, ProjectedAccrual prjAcc,
			BigDecimal curPftAmz, BigDecimal curPOSAmz, BigDecimal cumulativePft, BigDecimal cumulativePOS,
			BigDecimal partialCumPft, BigDecimal partialCumPOS, Date finStartDate) {

		BigDecimal partAccrual = BigDecimal.ZERO;
		BigDecimal partPOS = BigDecimal.ZERO;

		BigDecimal prvCumPft = cumulativePft.add(partialCumPft);
		BigDecimal prvCumPOS = cumulativePOS.add(partialCumPOS);

		ProjectedAccrual projAcc = new ProjectedAccrual();

		projAcc.setPartialPaidAmt(schd.getPartialPaidAmt());
		projAcc.setFinID(schd.getFinID());
		projAcc.setFinReference(schd.getFinReference());
		projAcc.setAccruedOn(schd.getSchDate());
		projAcc.setMonthEnd(false);

		// calculate part ACCRUAL / POS
		partAccrual = prjAcc.getPftAmz().subtract(curPftAmz).subtract(partialCumPft);
		partPOS = prjAcc.getPOSAccrued().subtract(curPOSAmz).subtract(partialCumPOS);

		projAcc.setPftAmz(partAccrual);
		projAcc.setPftAccrued(partAccrual);
		projAcc.setCumulativeAccrued(prvCumPft.add(partAccrual));

		projAcc.setPOSAccrued(partPOS);
		projAcc.setCumulativePOS(prvCumPOS.add(partPOS));

		projAcc.setCumulativeDays(DateUtil.getDaysBetween(schd.getSchDate(), finStartDate));

		return projAcc;
	}

	private static int getCumulativeDays(FinanceMain finMain, Date curMonthEnd) {
		int cumDays = 0;
		Date monthEnd = null;
		Date maturityDate = DateUtil.getDatePart(finMain.getMaturityDate());

		if (maturityDate.compareTo(curMonthEnd) <= 0) {
			monthEnd = finMain.getMaturityDate();
		} else {
			monthEnd = DateUtil.addDays(curMonthEnd, 1);
		}
		cumDays = DateUtil.getDaysBetween(monthEnd, finMain.getFinStartDate());
		return cumDays;
	}

	private static BigDecimal calPartialAMZPerc(BigDecimal partialPaidAmt, BigDecimal prvClosingBal, String amzMethod) {
		if (AmortizationConstants.AMZ_METHOD_STRAIGHTLINE.equals(amzMethod)) {
			return BigDecimal.ZERO;
		}
		return (partialPaidAmt.multiply(new BigDecimal(100))).divide(prvClosingBal, 9, RoundingMode.HALF_DOWN);
	}

	private static BigDecimal calPftAMZPercentage(BigDecimal totalProfit, BigDecimal pftAccrued,
			BigDecimal prvCumProfit) {
		BigDecimal unAMZProfit = totalProfit.subtract(prvCumProfit);

		if (unAMZProfit.compareTo(BigDecimal.ZERO) <= 0 || pftAccrued.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		return (pftAccrued.multiply(new BigDecimal(100))).divide(unAMZProfit, 9, RoundingMode.HALF_DOWN);
	}

	private static BigDecimal calPriAMZPercentage(BigDecimal totalPOS, BigDecimal posAccrued, BigDecimal prvCumPOS) {
		BigDecimal unAMZPOS = totalPOS.subtract(prvCumPOS);

		if (unAMZPOS.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		return (posAccrued.multiply(new BigDecimal(100))).divide(unAMZPOS, 9, RoundingMode.HALF_DOWN);
	}

	private static BigDecimal calDayAMZPercentage(FinanceMain finMain, int cumDays, boolean isMonthEnd) {
		if (!isMonthEnd) {
			return BigDecimal.ZERO;
		}

		int totalDays = DateUtil.getDaysBetween(finMain.getMaturityDate(), finMain.getFinStartDate());
		return (new BigDecimal(cumDays).multiply(new BigDecimal(100))).divide(new BigDecimal(totalDays), 9,
				RoundingMode.HALF_DOWN);
	}

	/**
	 * Amortized Details Fees/Expenses <br>
	 * 
	 * @param custEODEvent
	 * @return
	 */
	public void prepareAMZDetails(Date monthEndDate, Date appDate) {
		logger.debug(Literal.ENTERING);

		int count = 0;
		// insert new finance fees
		count = this.projectedAmortizationDAO.prepareAMZFeeDetails(monthEndDate, appDate);

		this.projectedAmortizationDAO.updateActualAmount(appDate);

		count = count + this.projectedAmortizationDAO.prepareSVFeeDetails(monthEndDate, appDate);
		// insert new finance expenses
		count = count + this.projectedAmortizationDAO.prepareAMZExpenseDetails(monthEndDate, appDate);

		StepUtil.PREPARE_INCOME_AMZ_DETAILS.setTotalRecords(count);
		StepUtil.PREPARE_INCOME_AMZ_DETAILS.setProcessedRecords(count);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @param finEODEvent
	 * @throws Exception
	 */
	public void processMonthEndIncomeAMZ(FinEODEvent finEODEvent) {
		List<ProjectedAmortization> calProjIncomeAMZList = new ArrayList<>(1);

		FinanceMain fm = finEODEvent.getFinanceMain();
		Date amzMonth = finEODEvent.getEventFromDate();
		if (fm.isWriteoffLoan()) {
			calProjIncomeAMZList = calIncomeAMZForWriteOffFins(finEODEvent);
		} else if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(fm.getClosingStatus())
				&& fm.getClosedDate().compareTo(amzMonth) <= 0) {
			calProjIncomeAMZList = calIncomeAMZForCancelledFins(finEODEvent);
		} else {
			calProjIncomeAMZList = calculateMonthEndIncomeAMZ(finEODEvent);
		}

		if (calProjIncomeAMZList.isEmpty()) {
			return;
		}
		// finance created and cancelled in same month then list empty
		// reset income AMZ details and save Projected AMZ Details
		doSetIncomeAMZAmounts(finEODEvent, calProjIncomeAMZList);
		this.projectedAmortizationDAO.saveBatchProjIncomeAMZ(calProjIncomeAMZList);

		// Post Accounting
		Long accountingSetId = AccountingEngine.getAccountSetID(fm, AccountingEvent.INDAS,
				FinanceConstants.MODULEID_FINTYPE);

		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.INDAS);

		if (accountingSetId != null && accountingSetId > 0) {
			aeEvent.getAcSetIDList().add(accountingSetId);
		}

		Date appDate = SysParamUtil.getAppDate();
		aeEvent.setValueDate(appDate);
		aeEvent.setPostDate(appDate);

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
			aeEvent.setAeAmountCodes(amountCodes);
		}

		amountCodes.setFinType(fm.getFinType());

		if (CollectionUtils.isEmpty(expenseTypes)) {
			expenseTypes.addAll(expenseTypeDAO.getExpenseTypes());
		}

		if (CollectionUtils.isEmpty(feeTypes)) {
			feeTypes.addAll(feeTypeDAO.getAMZReqFeeTypes());
		}

		for (ProjectedAmortization amz : finEODEvent.getIncomeAMZList()) {
			Map<String, Object> dataMap = amountCodes.getDeclaredFieldValues();

			for (ExpenseType expenseType : expenseTypes) {
				dataMap.put(expenseType.getExpenseTypeCode() + "_AMZ", BigDecimal.ZERO);
			}

			for (FeeType feeType : feeTypes) {
				dataMap.put(feeType.getFeeTypeCode() + "_AMZ", BigDecimal.ZERO);
				dataMap.put(feeType.getFeeTypeCode() + "_AMZ_N", BigDecimal.ZERO);
				dataMap.put(feeType.getFeeTypeCode() + "_AMZ_BAL", BigDecimal.ZERO);
			}

			dataMap.put("MBD_AMZ", BigDecimal.ZERO);
			dataMap.put("DBD_AMZ", BigDecimal.ZERO);
			dataMap.put("DBMBD_AMZ", BigDecimal.ZERO);

			aeEvent.setBranch(amz.getFinBranch());
			aeEvent.setCcy(amz.getFinCcy());
			aeEvent.setCustID(amz.getCustID());
			aeEvent.setFinID(amz.getFinID());
			aeEvent.setFinReference(amz.getFinReference());
			aeEvent.setEntityCode(amz.getEntityCode());
			aeEvent.setFinType(amz.getFinType());
			if (!fm.isFinIsActive()) {
				BigDecimal amzTillClosure = BigDecimal.ZERO;
				Date monthStart = DateUtil.getMonthStart(finEODEvent.getEventFromDate());
				Date maturityDate = profitDetailsDAO.getMaturityDate(fm.getFinID(), finEODEvent.getAppDate());

				int daysTillClosure = DateUtil.getDaysBetween(monthStart, fm.getClosedDate());
				int remDaysTillMaturity = DateUtil.getDaysBetween(monthStart, maturityDate);

				if (daysTillClosure > 0) {
					BigDecimal feePerDay = amz.getCurMonthAmz().divide(new BigDecimal(remDaysTillMaturity), 0,
							RoundingMode.HALF_DOWN);
					amzTillClosure = feePerDay.multiply(new BigDecimal(daysTillClosure));
				}

				dataMap.put(amz.getFeeTypeCode() + "_AMZ", amzTillClosure);
				dataMap.put(amz.getFeeTypeCode() + "_AMZ_BAL", (amz.getCurMonthAmz().subtract(amzTillClosure)));

			} else {
				/* Current Month Amortized Amount */
				dataMap.put(amz.getFeeTypeCode() + "_AMZ", amz.getCurMonthAmz());
				/* Till Date Amortized Amount */
				dataMap.put(amz.getFeeTypeCode() + "_AMZ_N", amz.getAmortizedAmount());
				dataMap.put(amz.getFeeTypeCode() + "_AMZ_BAL", BigDecimal.ZERO);
			}

			aeEvent.setDataMap(dataMap);

			try {
				postingsPreparationUtil.postAccounting(aeEvent);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		// update fee / expense AMZ details
		this.projectedAmortizationDAO.updateBatchIncomeAMZAmounts(finEODEvent.getIncomeAMZList());

	}

	private List<ProjectedAmortization> calIncomeAMZForWriteOffFins(FinEODEvent finEODEvent) {
		ProjectedAmortization projIncomeAMZ = null;
		List<ProjectedAmortization> projIncomeAMZList = new ArrayList<ProjectedAmortization>(1);

		List<ProjectedAmortization> finIncomeAMZList = finEODEvent.getIncomeAMZList();
		Date curMonthEnd = DateUtil.getMonthEnd(finEODEvent.getEventFromDate());

		for (ProjectedAmortization incomeAMZ : finIncomeAMZList) {
			// TODO : Avoid Write Off loans from 2nd month onwards, Same is handled in Preparation
			if (!incomeAMZ.isActive()) {
				continue;
			}

			// current month amortization (Table : ProjectedIncomeAMZ)
			projIncomeAMZ = new ProjectedAmortization();
			projIncomeAMZList.add(projIncomeAMZ);

			projIncomeAMZ.setMonthEndDate(curMonthEnd);
			projIncomeAMZ.setFinID(incomeAMZ.getFinID());
			projIncomeAMZ.setFinReference(incomeAMZ.getFinReference());
			projIncomeAMZ.setFinType(incomeAMZ.getFinType());
			projIncomeAMZ.setIncomeType(incomeAMZ.getIncomeType());
			projIncomeAMZ.setReferenceID(incomeAMZ.getReferenceID());
			projIncomeAMZ.setIncomeTypeID(incomeAMZ.getIncomeTypeID());

			// Previous AMZ
			ProjectedAmortization prvProjIncomeAMZ = getPrvProjIncomeAMZFromHeader(incomeAMZ, curMonthEnd);

			// Unamortized portion will be amortized in the month of WRITEOFF event
			projIncomeAMZ.setUnAmortizedAmount(BigDecimal.ZERO);
			projIncomeAMZ.setCumulativeAmount(incomeAMZ.getActualAmount());
			projIncomeAMZ.setAmortizedAmount(prvProjIncomeAMZ.getUnAmortizedAmount());
		}

		return projIncomeAMZList;
	}

	/**
	 * Previous month cumulative is the current month amortization
	 * 
	 * @param finIncomeAMZList
	 * @param amzMonth
	 * @return
	 */
	private List<ProjectedAmortization> calIncomeAMZForCancelledFins(FinEODEvent finEODEvent) {
		ProjectedAmortization projIncomeAMZ = null;
		List<ProjectedAmortization> projIncomeAMZList = new ArrayList<>(1);

		List<ProjectedAmortization> finIncomeAMZList = finEODEvent.getIncomeAMZList();
		Date curMonthEnd = DateUtil.getMonthEnd(finEODEvent.getEventFromDate());

		for (ProjectedAmortization incomeAMZ : finIncomeAMZList) {
			// current month amortization (Table : ProjectedIncomeAMZ)
			projIncomeAMZ = new ProjectedAmortization();
			projIncomeAMZList.add(projIncomeAMZ);

			projIncomeAMZ.setMonthEndDate(curMonthEnd);
			projIncomeAMZ.setFinID(incomeAMZ.getFinID());
			projIncomeAMZ.setFinReference(incomeAMZ.getFinReference());
			projIncomeAMZ.setFinType(incomeAMZ.getFinType());
			projIncomeAMZ.setIncomeType(incomeAMZ.getIncomeType());
			projIncomeAMZ.setReferenceID(incomeAMZ.getReferenceID());
			projIncomeAMZ.setIncomeTypeID(incomeAMZ.getIncomeTypeID());

			// Previous AMZ
			BigDecimal amortizedAmount = BigDecimal.ZERO;
			ProjectedAmortization prvProjIncomeAMZ = getPrvProjIncomeAMZFromHeader(incomeAMZ, curMonthEnd);

			// Current AMZ will be the Negative of Previous AMZ
			if (prvProjIncomeAMZ.getCumulativeAmount().compareTo(BigDecimal.ZERO) > 0) {
				amortizedAmount = prvProjIncomeAMZ.getCumulativeAmount().multiply(new BigDecimal(-1));
			}

			projIncomeAMZ.setAmortizedAmount(amortizedAmount);
			projIncomeAMZ.setCumulativeAmount(BigDecimal.ZERO);
			projIncomeAMZ.setUnAmortizedAmount(incomeAMZ.getActualAmount());

			projIncomeAMZ.setFinType(incomeAMZ.getFinType());
			projIncomeAMZ.setFinBranch(incomeAMZ.getFinBranch());
			projIncomeAMZ.setFinCcy(incomeAMZ.getFinCcy());
			projIncomeAMZ.setEntityCode(incomeAMZ.getEntityCode());
			projIncomeAMZ.setFeeTypeCode(incomeAMZ.getFeeTypeCode());
		}

		return projIncomeAMZList;
	}

	private List<ProjectedAmortization> calculateMonthEndIncomeAMZ(FinEODEvent finEODEvent) {
		Date curMonthEnd = null;
		Date prvMonthEnd = null;
		ProjectedAccrual projAccrual = null;
		ProjectedAmortization projIncomeAMZ = null;
		ProjectedAmortization prvProjIncomeAMZ = null;

		Map<String, ProjectedAmortization> projIncomeAMZMap = new HashMap<>(1);
		List<ProjectedAmortization> projIncomeAMZList = new ArrayList<>(1);

		Date amzMonth = finEODEvent.getEventFromDate();
		List<ProjectedAmortization> finIncomeAMZList = finEODEvent.getIncomeAMZList();
		List<ProjectedAccrual> finProjAccList = finEODEvent.getProjectedAccrualList();

		// run amortization EOD or EOM
		Date amzMonthEnd = DateUtil.getMonthEnd(amzMonth);

		for (int i = 0; i < finProjAccList.size(); i++) {
			projAccrual = finProjAccList.get(i);
			curMonthEnd = projAccrual.getAccruedOn();

			// month end or partial settlement
			if (!projAccrual.isMonthEnd()) {
				curMonthEnd = DateUtil.getMonthEnd(projAccrual.getAccruedOn());
			}

			prvMonthEnd = DateUtil.getMonthStart(projAccrual.getAccruedOn());
			prvMonthEnd = DateUtil.getDatePart((DateUtil.addDays(prvMonthEnd, -1)));

			for (ProjectedAmortization incomeAMZ : finIncomeAMZList) {
				// previous amortization
				String prvKey = prepareMapKey(prvMonthEnd, incomeAMZ);

				if (projIncomeAMZMap.containsKey(prvKey)) {
					prvProjIncomeAMZ = projIncomeAMZMap.get(prvKey);
				} else {
					prvProjIncomeAMZ = getPrvProjIncomeAMZFromHeader(incomeAMZ, amzMonthEnd);
					projIncomeAMZMap.put(prvKey, prvProjIncomeAMZ);
				}

				// from current amortization
				String curKey = prepareMapKey(curMonthEnd, incomeAMZ);

				if (projIncomeAMZMap.containsKey(curKey)) {
					projIncomeAMZ = projIncomeAMZMap.get(curKey);
				} else {
					projIncomeAMZ = new ProjectedAmortization();

					projIncomeAMZ.setMonthEndDate(curMonthEnd);
					projIncomeAMZ.setFinID(incomeAMZ.getFinID());
					projIncomeAMZ.setFinReference(incomeAMZ.getFinReference());
					projIncomeAMZ.setFinType(incomeAMZ.getFinType());
					projIncomeAMZ.setIncomeType(incomeAMZ.getIncomeType());
					projIncomeAMZ.setReferenceID(incomeAMZ.getReferenceID());
					projIncomeAMZ.setIncomeTypeID(incomeAMZ.getIncomeTypeID());

					projIncomeAMZ.setCumulativeAmount(prvProjIncomeAMZ.getCumulativeAmount());

					projIncomeAMZList.add(projIncomeAMZ);
					projIncomeAMZMap.put(curKey, projIncomeAMZ);
				}

				// for last month directly place the remaining amount
				if (i == finProjAccList.size() - 1) {
					BigDecimal curAMZAmount = incomeAMZ.getActualAmount()
							.subtract(prvProjIncomeAMZ.getCumulativeAmount());

					projIncomeAMZ.setAmortizedAmount(curAMZAmount);
					projIncomeAMZ.setUnAmortizedAmount(BigDecimal.ZERO);
					projIncomeAMZ.setCumulativeAmount(incomeAMZ.getActualAmount());

				} else {
					BigDecimal amzAmount = BigDecimal.ZERO;
					BigDecimal cumAmount = BigDecimal.ZERO;

					// calculate income / expense AMZ for both Normal and
					// partial settlements
					BigDecimal calAMZAmount = calculateProjIncomeAMZ(incomeAMZ, projAccrual,
							projIncomeAMZ.getCumulativeAmount());

					if (StringUtils.equals(incomeAMZ.getaMZMethod(), AmortizationConstants.AMZ_METHOD_STRAIGHTLINE)) {
						// for partial settlements calAMZAmount value is ZERO
						cumAmount = calAMZAmount;
						if (calAMZAmount.compareTo(BigDecimal.ZERO) > 0) {
							amzAmount = calAMZAmount.subtract(prvProjIncomeAMZ.getCumulativeAmount());
						}
					} else {
						amzAmount = projIncomeAMZ.getAmortizedAmount().add(calAMZAmount);
						cumAmount = projIncomeAMZ.getCumulativeAmount().add(calAMZAmount);
					}

					projIncomeAMZ.setAmortizedAmount(amzAmount);
					projIncomeAMZ.setCumulativeAmount(cumAmount);
					projIncomeAMZ.setUnAmortizedAmount(incomeAMZ.getActualAmount().subtract(cumAmount));
				}

				projIncomeAMZ.setFinType(incomeAMZ.getFinType());
				projIncomeAMZ.setFinBranch(incomeAMZ.getFinBranch());
				projIncomeAMZ.setFinCcy(incomeAMZ.getFinCcy());
				projIncomeAMZ.setEntityCode(incomeAMZ.getEntityCode());
				projIncomeAMZ.setFeeTypeCode(incomeAMZ.getFeeTypeCode());
			}
		}

		return projIncomeAMZList;
	}

	private ProjectedAmortization getPrvProjIncomeAMZFromHeader(ProjectedAmortization incomeAMZ, Date curMonthEnd) {
		ProjectedAmortization prvProjIncomeAMZ = new ProjectedAmortization();

		BigDecimal prvAMZAmt = BigDecimal.ZERO;
		BigDecimal prvCumAmount = BigDecimal.ZERO;
		BigDecimal prvUnAMZAmount = BigDecimal.ZERO;

		if (curMonthEnd.compareTo(incomeAMZ.getMonthEndDate()) == 0) {

			// other than month end (EOM_ON_EOD : true or SCREEN)
			prvAMZAmt = incomeAMZ.getPrvMonthAmz();
			prvCumAmount = incomeAMZ.getAmortizedAmount().subtract(incomeAMZ.getCurMonthAmz());
			prvUnAMZAmount = incomeAMZ.getUnAmortizedAmount().add(incomeAMZ.getCurMonthAmz());

		} else {

			// run only on month end
			prvAMZAmt = incomeAMZ.getCurMonthAmz();
			prvCumAmount = incomeAMZ.getAmortizedAmount();
			prvUnAMZAmount = incomeAMZ.getUnAmortizedAmount();
		}

		prvProjIncomeAMZ.setAmortizedAmount(prvAMZAmt);
		prvProjIncomeAMZ.setCumulativeAmount(prvCumAmount);
		prvProjIncomeAMZ.setUnAmortizedAmount(prvUnAMZAmount);

		return prvProjIncomeAMZ;
	}

	private List<ProjectedAmortization> doSetIncomeAMZAmounts(FinEODEvent finEODEvent,
			List<ProjectedAmortization> calProjIncomeAMZList) {
		Date appDate = finEODEvent.getAppDate();
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<ProjectedAmortization> finIncomeAMZList = finEODEvent.getIncomeAMZList();
		Date curMonthEnd = DateUtil.getMonthEnd(finEODEvent.getEventFromDate());

		for (ProjectedAmortization incomeAMZ : finIncomeAMZList) {
			for (ProjectedAmortization curProjIncomeAMZ : calProjIncomeAMZList) {

				if (curMonthEnd.compareTo(curProjIncomeAMZ.getMonthEndDate()) == 0
						&& incomeAMZ.getReferenceID() == curProjIncomeAMZ.getReferenceID()
						&& StringUtils.equals(incomeAMZ.getIncomeType(), curProjIncomeAMZ.getIncomeType())) {

					// PrvMonthAmz setting : EOM - Required, EOD - First run
					// Required (EOM_ON_EOD : true).
					if (curMonthEnd.compareTo(incomeAMZ.getMonthEndDate()) != 0) {
						incomeAMZ.setPrvMonthAmz(incomeAMZ.getCurMonthAmz());
					}

					// SCREEN : appDate - DateUtil.getAppDate() ; EOM : amzMonth
					incomeAMZ.setCalculatedOn(appDate);

					// For WRITEOFF mark as inactive
					if (finMain.isWriteoffLoan()) {
						incomeAMZ.setActive(false);
					} else {
						incomeAMZ.setActive(finMain.isFinIsActive());
					}

					if (appDate.compareTo(finMain.getMaturityDate()) >= 0) {
						incomeAMZ.setActive(false);
					}

					incomeAMZ.setMonthEndDate(curMonthEnd);

					incomeAMZ.setCurMonthAmz(curProjIncomeAMZ.getAmortizedAmount());// Current Month Amortized Amount
					incomeAMZ.setAmortizedAmount(curProjIncomeAMZ.getCumulativeAmount());// Till Date Amortized Amount
					incomeAMZ.setUnAmortizedAmount(curProjIncomeAMZ.getUnAmortizedAmount());//

					break;
				}
			}
		}

		return finIncomeAMZList;
	}

	private String prepareMapKey(Date monthEndDate, ProjectedAmortization incomeAMZ) {
		String srtMonthEndDate = DateUtil.format(monthEndDate, DateFormat.LONG_DATE);
		StringBuilder key = new StringBuilder();
		key.append(srtMonthEndDate + "_");
		key.append(incomeAMZ.getIncomeType() + "_");
		key.append(incomeAMZ.getReferenceID());

		return key.toString();
	}

	private BigDecimal calculateProjIncomeAMZ(ProjectedAmortization incomeAMZ, ProjectedAccrual projAccrual,
			BigDecimal prvCumAMZ) {

		BigDecimal totAMZAmount = BigDecimal.ZERO;

		if (AmortizationConstants.AMZ_METHOD_STRAIGHTLINE.equals(incomeAMZ.getaMZMethod())) {
			// calculate Cumulative AMZ
			BigDecimal cumAMZAmount = projAccrual.getAMZPercentage().multiply(incomeAMZ.getActualAmount());
			totAMZAmount = cumAMZAmount.divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN);
		} else {
			// calculate AMZ for ACCRUAL
			BigDecimal amzAmount = projAccrual.getAMZPercentage()
					.multiply(incomeAMZ.getActualAmount().subtract(prvCumAMZ));
			amzAmount = amzAmount.divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN);

			// calculate AMZ for Partial Settlement
			BigDecimal totPrvCumAMZ = amzAmount.add(prvCumAMZ);

			BigDecimal partialAMZAmt = projAccrual.getPartialAMZPerc()
					.multiply(incomeAMZ.getActualAmount().subtract(totPrvCumAMZ));
			partialAMZAmt = partialAMZAmt.divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN);

			totAMZAmount = amzAmount.add(partialAMZAmt);
		}

		return totAMZAmount.setScale(0, RoundingMode.HALF_DOWN);
	}

	private Map<String, Object> getDataMap(FinEODEvent finEODEvent) {
		Map<String, Object> dataMap = new HashMap<String, Object>();

		FinanceType finType = finEODEvent.getFinType();
		FinanceMain finMain = finEODEvent.getFinanceMain();
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();

		if (finMain != null) {
			dataMap.putAll(finMain.getDeclaredFieldValues());
		}
		if (finType != null) {
			dataMap.putAll(finType.getDeclaredFieldValues());
		}
		if (finPftDetail != null) {
			dataMap.putAll(finPftDetail.getDeclaredFieldValues());
		}

		return dataMap;
	}

	/**
	 * Method for Sorting ACCRUAL Details
	 * 
	 * @param projectedAccruals
	 * @return
	 */
	public List<ProjectedAccrual> sortProjectedAccruals(List<ProjectedAccrual> projectedAccruals) {

		if (projectedAccruals != null && projectedAccruals.size() > 0) {
			Collections.sort(projectedAccruals, new Comparator<ProjectedAccrual>() {
				@Override
				public int compare(ProjectedAccrual detail1, ProjectedAccrual detail2) {
					return DateUtil.compare(detail1.getAccruedOn(), detail2.getAccruedOn());
				}
			});
		}

		return projectedAccruals;
	}

	public Date getPrvAMZMonthLog() {
		return projectedAmortizationDAO.getPrvAMZMonthLog();
	}

	public FinanceMain getFinanceForIncomeAMZ(long finID) {
		return financeMainDAO.getFinanceForIncomeAMZ(finID);
	}

	public FinanceMain getFinanceForAMZMethod(long finID, boolean isActive) {
		return financeMainDAO.getFinMainsForEODByFinRef(finID, isActive);
	}

	public FinanceProfitDetail getFinProfitForAMZ(long finID) {
		return profitDetailsDAO.getFinProfitForAMZ(finID);
	}

	public List<FinanceMain> getFinListForIncomeAMZ(Date curMonthStart) {
		return financeMainDAO.getFinListForIncomeAMZ(curMonthStart);
	}

	public BigDecimal calculateAveragePOS(FinEODEvent finEODEvent) throws Exception {
		Map<Date, ProjectedAccrual> map = new HashMap<Date, ProjectedAccrual>(1);
		List<Date> months = new ArrayList<Date>(1);
		months.add(finEODEvent.getEventFromDate());

		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;

		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		Date appDateMonthStart = DateUtil.getMonthStart(finEODEvent.getEventFromDate());
		Date maturityDate = DateUtil.getDatePart(fm.getMaturityDate());
		Date prvMonthEnd = DateUtil.addDays(appDateMonthStart, -1);

		int noOfDays = 0;

		// looping schedules
		for (int i = 0; i < schedules.size(); i++) {

			curSchd = schedules.get(i);
			curSchdDate = curSchd.getSchDate();

			if (curSchdDate.compareTo(appDateMonthStart) < 0) {
				continue;
			}

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schedules.get(i - 1);
			}
			if (i == schedules.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schedules.get(i + 1);
			}

			prvSchdDate = prvSchd.getSchDate();
			nextSchdDate = nextSchd.getSchDate();

			for (Date curMonthEnd : months) {

				// Customer Profitability RFT
				BigDecimal schdAvgPOS = BigDecimal.ZERO;
				BigDecimal curAvgPOS = BigDecimal.ZERO;
				BigDecimal prvAvgPOS = BigDecimal.ZERO;
				BigDecimal avgPOS = BigDecimal.ZERO;

				boolean isSchdAmz = false;
				ProjectedAccrual prjAcc = null;

				if (map.containsKey(curMonthEnd)) {
					prjAcc = map.get(curMonthEnd);
				} else {
					prjAcc = new ProjectedAccrual();
					prjAcc.setFinID(fm.getFinID());
					prjAcc.setFinReference(fm.getFinReference());
					prjAcc.setAccruedOn(curMonthEnd);
					prjAcc.setMonthEnd(true);

					// Prepare Map and List
					map.put(curMonthEnd, prjAcc);
				}

				// START : Actual ACCRUAL calculation includes current date
				Date nextMonthStart = DateUtil.addDays(curMonthEnd, 1);
				Date curMonthStart = DateUtil.getMonthStart(curMonthEnd);

				// Schedules between Previous MonthEnd and CurMonthEnd
				if (prvSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0) {

					isSchdAmz = true;
					if (curSchdDate.compareTo(DateUtil.getDatePart(fm.getFinStartDate())) != 0) {

						int days = DateUtil.getDaysBetween(curSchdDate, prvSchdDate);
						schdAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
						noOfDays = noOfDays + days;
					}
				}

				// multiple months between two schedules (Quarterly, Half Yearly
				// ..)
				if (curMonthEnd.compareTo(curSchdDate) < 0) {

					int days = DateUtil.getDaysBetween(nextMonthStart, curMonthStart);
					prvAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
					noOfDays = noOfDays + days;

				} else {
					// Calculation From CurSchdDate to Current MonthEnd
					if (curMonthEnd.compareTo(prvSchdDate) >= 0 && curMonthEnd.compareTo(nextSchdDate) < 0) {

						int days = DateUtil.getDaysBetween(nextMonthStart, curSchdDate);
						curAvgPOS = curSchd.getClosingBalance().multiply(new BigDecimal(days));
						noOfDays = noOfDays + days;
					}
					// Calculation From CurSchdDate to Previous MonthEnd
					if (prvMonthEnd != null && !isSchdAmz) {

						int days = DateUtil.getDaysBetween(curSchdDate, curMonthStart);
						prvAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
						noOfDays = noOfDays + days;
					}
				}
				avgPOS = schdAvgPOS.add(prvAvgPOS).add(curAvgPOS).add(prjAcc.getAvgPOS());
				prjAcc.setAvgPOS(avgPOS);

				// END : Average POS Calculation

				if (curMonthEnd.compareTo(nextSchdDate) < 0 || curSchdDate.compareTo(maturityDate) == 0) {

					// Average POS
					if (noOfDays > 0) {
						BigDecimal monthlyAvgPOS = prjAcc.getAvgPOS().divide(new BigDecimal(noOfDays), 0,
								RoundingMode.HALF_DOWN);
						prjAcc.setAvgPOS(monthlyAvgPOS);
					}

					return prjAcc.getAvgPOS();
				}

				if (curMonthEnd.compareTo(curSchdDate) >= 0) {
					break;
				}
			}
		}
		return BigDecimal.ZERO;
	}

	void updateAMZMethod(long finID, String amzMethod) {
		profitDetailsDAO.updateAMZMethod(finID, amzMethod);
	}

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setExpenseTypeDAO(ExpenseTypeDAO expenseTypeDAO) {
		this.expenseTypeDAO = expenseTypeDAO;
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

}
