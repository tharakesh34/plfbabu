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
 *																							*
 * FileName    		:  ProjectedAmortizationService.java                                    * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-01-2018    														*
 *                                                                  						*
 * Modified Date    :  16-05-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-01-2018       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 09-03-2018       Satya	                 0.2          PSD Ticket : 124705               * 
 *                                                        IND AS : Income / Expense			*
 *                                                        Amortization.                     * 
 *                                                                                          * 
 * 16-05-2018       Satya	                 0.3          PSD Ticket : 126328               * 
 *                                                        IND AS : Performance related		* 
 *                                                        changes.					        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.finance.FinExpenseDetailsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pff.eod.step.StepUtil;

public class ProjectedAmortizationService {
	private static Logger logger = Logger.getLogger(ProjectedAmortizationService.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private RuleExecutionUtil ruleExecutionUtil;
	private FinExpenseDetailsDAO finExpenseDetailsDAO;
	private ProjectedAmortizationDAO projectedAmortizationDAO;

	/**
	 * 
	 * @param custEODEvent
	 * @return
	 * @throws Exception
	 */
	public CustEODEvent prepareMonthEndAccruals(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");

		Date appDate = custEODEvent.getEodDate();
		Date curMonthStart = DateUtility.getMonthStart(appDate);
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			// ignore ACCRUAL calculation after maturity date
			if (getFormatDate(finEODEvent.getFinanceMain().getMaturityDate()).compareTo(curMonthStart) < 0) {
				continue;
			}

			// For ODFacility Finances initially doesn't have any Schedules
			List<FinanceScheduleDetail> schdDetails = finEODEvent.getFinanceScheduleDetails();
			FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();

			if (schdDetails != null && !schdDetails.isEmpty()) {

				// AMZ Method : execute rule and get amortization method
				if (StringUtils.isBlank(finPftDetail.getAMZMethod())) {
					String amzMethod = identifyAMZMethod(finEODEvent, custEODEvent.getAmzMethodRule());
					finPftDetail.setAMZMethod(amzMethod);
				}

				// calculate MonthEnd ACCRUALS / POS
				processMonthEndAccruals(finEODEvent, custEODEvent.getEodDate());

				// Amortization Percentage
				calculateAMZPercentage(finEODEvent);
			}
		}

		logger.debug(" Leaving ");
		return custEODEvent;
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
		HashMap<String, Object> dataMap = getDataMap(finEODEvent);
		String ruleResult = (String) this.ruleExecutionUtil.executeRule(amzMethodRule, dataMap, "",
				RuleReturnType.STRING);

		return ruleResult;
	}

	/**
	 * 
	 * @param finEODEvent
	 * @param amzMonth
	 * @throws Exception
	 */
	private void processMonthEndAccruals(FinEODEvent finEODEvent, Date amzMonth) throws Exception {
		logger.debug(" Entering ");

		FinanceMain finMain = finEODEvent.getFinanceMain();
		String finReference = finMain.getFinReference();

		Date curMonthStart = DateUtility.getMonthStart(amzMonth);
		Date prvMonthEndDate = DateUtility.addDays(curMonthStart, -1);
		ProjectedAccrual prvProjAccrual = null;

		// Month End ACCRUAL Calculation configuration from SMT parameter
		String fromFinStartDate = SysParamUtil.getValueAsString("MONTHENDACC_FROMFINSTARTDATE");

		boolean calFromFinStartDate = true;
		if (StringUtils.equals(fromFinStartDate, "N")) {

			calFromFinStartDate = false;
			prvProjAccrual = this.projectedAmortizationDAO.getPrvProjectedAccrual(finReference, prvMonthEndDate,
					"_WORK");
		}

		// calculate month end and partial settlements ACCRUALS / POS
		calculateMonthEndAccruals(finEODEvent, prvProjAccrual, amzMonth, calFromFinStartDate);
		logger.debug(" Leaving ");
	}

	/**
	 * Calculate Month End ACCRUALS, POS and Process Partial Settlements </br>
	 * 
	 * Calculation logic is Monthly based not cumulative based. </br>
	 * 
	 * @param finEODEvent
	 * @param prvProjAccrual
	 * @param amzMonth
	 * @param calFromFinStartDate
	 * @throws Exception
	 */
	private void calculateMonthEndAccruals(FinEODEvent finEODEvent, ProjectedAccrual prvProjAccrual, Date amzMonth,
			boolean calFromFinStartDate) throws Exception {
		logger.debug(" Entering ");

		HashMap<Date, ProjectedAccrual> map = new HashMap<Date, ProjectedAccrual>(1);
		List<ProjectedAccrual> projAccrualList = new ArrayList<ProjectedAccrual>(1);
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

		FinanceMain finMain = finEODEvent.getFinanceMain();
		String amzMethod = finEODEvent.getFinProfitDetail().getAMZMethod();
		List<FinanceScheduleDetail> schdDetails = finEODEvent.getFinanceScheduleDetails();

		Date appDateMonthStart = DateUtility.getMonthStart(amzMonth);
		Date appDateMonthEnd = DateUtility.getMonthEnd(amzMonth);
		Date maturityDate = getFormatDate(finMain.getMaturityDate());

		// Prepare Months list From FinStartDate to MaturityDate
		if (calFromFinStartDate || finMain.getFinStartDate().compareTo(amzMonth) >= 0) {

			newMonth = new Date(DateUtility.getMonthEnd(finMain.getFinStartDate()).getTime());

		} else {

			prvMonthEnd = DateUtility.addDays(appDateMonthStart, -1);
			newMonth = new Date(appDateMonthEnd.getTime());

			if (prvProjAccrual != null) {
				prvCumDays = prvProjAccrual.getCumulativeDays();
				cumulativePOS = prvProjAccrual.getCumulativePOS();
				cumulativePft = prvProjAccrual.getCumulativeAccrued();
			}
		}

		Date mdtMonthEnd = DateUtility.getMonthEnd(maturityDate);

		while (mdtMonthEnd.compareTo(newMonth) >= 0) {
			months.add(getFormatDate((Date) newMonth.clone()));
			newMonth = DateUtility.addMonths(newMonth, 1);
			newMonth = DateUtility.getMonthEnd(newMonth);
		}
		monthsCopy.addAll(months);

		// looping schedules
		for (int i = 0; i < schdDetails.size(); i++) {

			curSchd = schdDetails.get(i);
			curSchdDate = curSchd.getSchDate();
			curSchdMonthEnd = DateUtility.getMonthEnd(curSchdDate);

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
				prvSchd = schdDetails.get(i - 1);
			}
			if (i == schdDetails.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schdDetails.get(i + 1);
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
					prjAcc.setFinReference(finMain.getFinReference());
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
				Date nextMonthStart = DateUtility.addDays(curMonthEnd, 1);
				Date curMonthStart = DateUtility.getMonthStart(curMonthEnd);

				// Schedules between Previous MonthEnd and CurMonthEnd and past dated finances( Weekly..)
				if ((prvSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0)
						|| prvProjAccrual == null) {

					isSchdAmz = true;
					schdPftAmz = curSchd.getProfitCalc();

					if (curSchdDate.compareTo(getFormatDate(finMain.getFinStartDate())) != 0) {
						schdPOSAmz = prvSchd.getClosingBalance();

						int days = DateUtility.getDaysBetween(curSchdDate, prvSchdDate);
						schdAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
					}
				}

				// multiple months between two schedules (Quarterly, Half Yearly ..)
				if (curMonthEnd.compareTo(curSchdDate) < 0) {

					int days = DateUtility.getDaysBetween(nextMonthStart, curMonthStart);
					int daysInCurPeriod = DateUtility.getDaysBetween(curSchdDate, prvSchdDate);

					prvPftAmz = curSchd.getProfitCalc().multiply(new BigDecimal(days))
							.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
					prvPOSAmz = prvSchd.getClosingBalance().multiply(new BigDecimal(days))
							.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
					prvAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));

				} else {

					// Calculation From CurSchdDate to Current MonthEnd 
					if (curMonthEnd.compareTo(prvSchdDate) >= 0 && curMonthEnd.compareTo(nextSchdDate) < 0) {

						int days = DateUtility.getDaysBetween(nextMonthStart, curSchdDate);
						int daysInCurPeriod = DateUtility.getDaysBetween(nextSchdDate, curSchdDate);

						curPftAmz = nextSchd.getProfitCalc().multiply(new BigDecimal(days))
								.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
						curPOSAmz = curSchd.getClosingBalance().multiply(new BigDecimal(days))
								.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
						curAvgPOS = curSchd.getClosingBalance().multiply(new BigDecimal(days));
					}

					// Calculation From CurSchdDate to Previous MonthEnd
					if (prvMonthEnd != null && !isSchdAmz) {

						int days = DateUtility.getDaysBetween(curSchdDate, curMonthStart);
						int daysInCurPeriod = DateUtility.getDaysBetween(curSchdDate, prvSchdDate);

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

				// Adjust remaining profit to maturity MonthEnd to avoid rounding issues
				if (DateUtility.getMonthEnd(maturityDate).compareTo(curMonthEnd) == 0
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
							cumulativePft, cumulativePOS, partialCumPft, partialCumPOS, finMain.getFinStartDate());

					// calculate AMZ percentage for Partial Settlement
					BigDecimal partialAMZPerc = calPartialAMZPerc(curSchd.getPartialPaidAmt(), closingBalPS, amzMethod);
					partialProjAcc.setPartialAMZPerc(partialAMZPerc);

					partialCumPft = partialCumPft.add(partialProjAcc.getPftAmz());
					partialCumPOS = partialCumPOS.add(partialProjAcc.getPOSAccrued());
					projAccrualList.add(partialProjAcc);
				}
				// END

				// START : Remove current month end from list, 2nd one is to handle Quarterly, Half Yearly (Maturity month end).. 
				if (curMonthEnd.compareTo(nextSchdDate) < 0 || curSchdDate.compareTo(maturityDate) == 0) {

					// No of Days and Cumulative Days
					curCumDays = getCumulativeDays(finMain, curMonthEnd);
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

					// calculate AMZ percentage for Partial Settlement on month end
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
		logger.debug(" Leaving ");
	}

	/**
	 * 
	 * @param prvProjAccrual
	 * @param finSchdTotals
	 * @param curMonthEndDate
	 * @param maturityDate
	 */
	public ProjectedAccrual prepareMaturityMonthProjAcc(ProjectedAccrual prvProjAccrual,
			List<FinanceScheduleDetail> schdDetails, String finReference, Date curMonthEnd) {

		Date prvSchdDate = null;
		Date curSchdDate = null;
		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;

		int noOfDays = 0;
		BigDecimal posAmz = BigDecimal.ZERO;
		BigDecimal avgPOS = BigDecimal.ZERO;
		BigDecimal totalProfit = BigDecimal.ZERO;
		boolean isSchdPartCalReq = true;

		Date curMonthStart = DateUtility.getMonthStart(curMonthEnd);

		// looping schedules
		for (int i = 1; i < schdDetails.size(); i++) {

			curSchd = schdDetails.get(i);
			curSchdDate = curSchd.getSchDate();

			prvSchd = schdDetails.get(i - 1);
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
				days = DateUtility.getDaysBetween(curSchdDate, curMonthStart);
				int daysInCurPeriod = DateUtility.getDaysBetween(curSchdDate, prvSchdDate);

				schdPOSAmz = prvSchd.getClosingBalance().multiply(new BigDecimal(days))
						.divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);
				schdAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));

			} else {
				schdPOSAmz = prvSchd.getClosingBalance();
				days = DateUtility.getDaysBetween(curSchdDate, prvSchdDate);
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
			prvProjAccrual.setFinReference(finReference);
		}

		projAccrual.setAccruedOn(curMonthEnd);
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
		logger.debug(" Entering ");

		BigDecimal amzPercentage = BigDecimal.ZERO;
		FinanceMain finMain = finEODEvent.getFinanceMain();
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();

		List<ProjectedAccrual> projAccList = finEODEvent.getProjectedAccrualList();
		if (projAccList == null || projAccList.isEmpty()) {
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

				// AMZ percentage is ZERO for partial settlement entries, AMZ Percentage is Cumulative Not Monthly.
				amzPercentage = calDayAMZPercentage(finMain, projAccrual.getCumulativeDays(), projAccrual.isMonthEnd());
				projAccrual.setAMZPercentage(amzPercentage);
			}
			break;

		default:
			break;
		}

		logger.debug(" Leaving ");
	}

	/**
	 * Process Partial Settlements other than month ends </br>
	 * 
	 * @param curSchd
	 * @param prjAcc
	 * @param partialCumPft
	 * @param partialCumPOS
	 * @param curPftAmz
	 * @param curPOSAmz
	 * @param totPrvCumPft
	 * @param totPrvCumPOS
	 * @return
	 */
	private ProjectedAccrual processPartialSettlements(FinanceScheduleDetail curSchd, ProjectedAccrual prjAcc,
			BigDecimal curPftAmz, BigDecimal curPOSAmz, BigDecimal cumulativePft, BigDecimal cumulativePOS,
			BigDecimal partialCumPft, BigDecimal partialCumPOS, Date finStartDate) {

		BigDecimal partAccrual = BigDecimal.ZERO;
		BigDecimal partPOS = BigDecimal.ZERO;

		BigDecimal prvCumPft = cumulativePft.add(partialCumPft);
		BigDecimal prvCumPOS = cumulativePOS.add(partialCumPOS);

		ProjectedAccrual projAcc = new ProjectedAccrual();

		projAcc.setPartialPaidAmt(curSchd.getPartialPaidAmt());
		projAcc.setFinReference(curSchd.getFinReference());
		projAcc.setAccruedOn(curSchd.getSchDate());
		projAcc.setMonthEnd(false);

		// calculate part ACCRUAL / POS
		partAccrual = prjAcc.getPftAmz().subtract(curPftAmz).subtract(partialCumPft);
		partPOS = prjAcc.getPOSAccrued().subtract(curPOSAmz).subtract(partialCumPOS);

		projAcc.setPftAmz(partAccrual);
		projAcc.setPftAccrued(partAccrual);
		projAcc.setCumulativeAccrued(prvCumPft.add(partAccrual));

		projAcc.setPOSAccrued(partPOS);
		projAcc.setCumulativePOS(prvCumPOS.add(partPOS));

		projAcc.setCumulativeDays(DateUtility.getDaysBetween(curSchd.getSchDate(), finStartDate));

		return projAcc;
	}

	/**
	 * Method for calculate Cumulative Days. </br>
	 * 
	 * @param finMain
	 * @param curMonthEnd
	 * @return
	 */
	private static int getCumulativeDays(FinanceMain finMain, Date curMonthEnd) {

		int cumDays = 0;
		Date monthEnd = null;
		Date maturityDate = getFormatDate(finMain.getMaturityDate());

		if (maturityDate.compareTo(curMonthEnd) <= 0) {
			monthEnd = finMain.getMaturityDate();
		} else {
			monthEnd = DateUtility.addDays(curMonthEnd, 1);
		}
		cumDays = DateUtility.getDaysBetween(monthEnd, finMain.getFinStartDate());
		return cumDays;
	}

	/**
	 * Method for calculate Partial Settlement Amortization Percentage. </br>
	 * 
	 * For StraightLine Method : </br>
	 * -- AMZ percentage is ZERO for Partial Settlements </br>
	 * 
	 * @param partialPaidAmt
	 * @param prvClosingBal
	 * @param amzMethod
	 * @return
	 */
	private static BigDecimal calPartialAMZPerc(BigDecimal partialPaidAmt, BigDecimal prvClosingBal, String amzMethod) {

		BigDecimal partialAMZPerc = BigDecimal.ZERO;
		if (!StringUtils.equals(AmortizationConstants.AMZ_METHOD_STRAIGHTLINE, amzMethod)) {

			partialAMZPerc = (partialPaidAmt.multiply(new BigDecimal(100))).divide(prvClosingBal, 9,
					RoundingMode.HALF_DOWN);
		}
		return partialAMZPerc;
	}

	/**
	 * Method for calculate Profit Amortization Percentage. </br>
	 * 
	 * @param totalProfit
	 * @param pftAccrued
	 * @param prvCumProfit
	 * @return
	 */
	private static BigDecimal calPftAMZPercentage(BigDecimal totalProfit, BigDecimal pftAccrued,
			BigDecimal prvCumProfit) {

		BigDecimal amzPercentage = BigDecimal.ZERO;
		BigDecimal unAMZProfit = totalProfit.subtract(prvCumProfit);

		if (unAMZProfit.compareTo(BigDecimal.ZERO) <= 0 || pftAccrued.compareTo(BigDecimal.ZERO) <= 0) {
			return amzPercentage;
		}
		amzPercentage = (pftAccrued.multiply(new BigDecimal(100))).divide(unAMZProfit, 9, RoundingMode.HALF_DOWN);
		return amzPercentage;
	}

	/**
	 * Method for calculate POS Amortization Percentage.
	 * 
	 * @param totalPOS
	 * @param posAccrued
	 * @param prvCumPOS
	 * @return
	 */
	private static BigDecimal calPriAMZPercentage(BigDecimal totalPOS, BigDecimal posAccrued, BigDecimal prvCumPOS) {

		BigDecimal amzPercentage = BigDecimal.ZERO;
		BigDecimal unAMZPOS = totalPOS.subtract(prvCumPOS);

		if (unAMZPOS.compareTo(BigDecimal.ZERO) == 0) {
			return amzPercentage;
		}
		amzPercentage = (posAccrued.multiply(new BigDecimal(100))).divide(unAMZPOS, 9, RoundingMode.HALF_DOWN);
		return amzPercentage;
	}

	/**
	 * Method for calculate Days Amortization Percentage. </br>
	 * 
	 * @param finMain
	 * @param noOfDays
	 * @return
	 */
	private static BigDecimal calDayAMZPercentage(FinanceMain finMain, int cumDays, boolean isMonthEnd) {

		BigDecimal amzPercentage = BigDecimal.ZERO;

		if (isMonthEnd) {
			int totalDays = DateUtility.getDaysBetween(finMain.getMaturityDate(), finMain.getFinStartDate());
			amzPercentage = (new BigDecimal(cumDays).multiply(new BigDecimal(100))).divide(new BigDecimal(totalDays), 9,
					RoundingMode.HALF_DOWN);
		}

		return amzPercentage;
	}

	/**
	 * Amortized Details Fees/Expenses <br>
	 * 
	 * @param custEODEvent
	 * @return
	 */
	public void prepareAMZDetails(Date monthEndDate, Date appDate) {
		logger.debug(" Entering ");

		int count = 0;
		// insert new finance fees
		count = this.projectedAmortizationDAO.prepareAMZFeeDetails(monthEndDate, appDate);
		this.projectedAmortizationDAO.updateActualAmount(appDate);

		// insert new finance expenses
		count = count + this.projectedAmortizationDAO.prepareAMZExpenseDetails(monthEndDate, appDate);

		StepUtil.PREPARE_INCOME_AMZ_DETAILS.setTotalRecords(count);
		StepUtil.PREPARE_INCOME_AMZ_DETAILS.setProcessedRecords(count);

		logger.debug(" Leaving ");
	}

	/**
	 * 
	 * @param finEODEvent
	 * @throws Exception
	 */
	public void processMonthEndIncomeAMZ(FinEODEvent finEODEvent) throws Exception {

		List<ProjectedAmortization> calProjIncomeAMZList = new ArrayList<ProjectedAmortization>(1);

		FinanceMain financeMain = finEODEvent.getFinanceMain();
		Date amzMonth = finEODEvent.getEventFromDate();

		if (StringUtils.equals(FinanceConstants.CLOSE_STATUS_CANCELLED, financeMain.getClosingStatus())
				&& financeMain.getClosedDate().compareTo(amzMonth) <= 0) {

			calProjIncomeAMZList = calIncomeAMZForCancelledFins(finEODEvent);
		} else {
			calProjIncomeAMZList = calculateMonthEndIncomeAMZ(finEODEvent);
		}

		// finance created and cancelled in same month then list empty
		if (!calProjIncomeAMZList.isEmpty()) {

			// reset income AMZ details and save Projected AMZ Details
			doSetIncomeAMZAmounts(finEODEvent, calProjIncomeAMZList);
			this.projectedAmortizationDAO.saveBatchProjIncomeAMZ(calProjIncomeAMZList);
		}

		// update fee / expense AMZ details
		this.projectedAmortizationDAO.updateBatchIncomeAMZAmounts(finEODEvent.getIncomeAMZList());

		calProjIncomeAMZList = null;
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
		List<ProjectedAmortization> projIncomeAMZList = new ArrayList<ProjectedAmortization>(1);

		List<ProjectedAmortization> finIncomeAMZList = finEODEvent.getIncomeAMZList();
		Date curMonthEnd = DateUtility.getMonthEnd(finEODEvent.getEventFromDate());

		for (ProjectedAmortization incomeAMZ : finIncomeAMZList) {

			// current month amortization (Table : ProjectedIncomeAMZ)
			projIncomeAMZ = new ProjectedAmortization();
			projIncomeAMZList.add(projIncomeAMZ);

			projIncomeAMZ.setMonthEndDate(curMonthEnd);
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
		}

		return projIncomeAMZList;
	}

	/**
	 * Month End Income/Expense Amortization Calculation </br>
	 * 
	 * @param finIncomeAMZList
	 * @param amzMonth
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<ProjectedAmortization> calculateMonthEndIncomeAMZ(FinEODEvent finEODEvent) throws Exception {

		Date curMonthEnd = null;
		Date prvMonthEnd = null;
		ProjectedAccrual projAccrual = null;
		ProjectedAmortization projIncomeAMZ = null;
		ProjectedAmortization prvProjIncomeAMZ = null;

		HashMap<String, ProjectedAmortization> projIncomeAMZMap = new HashMap<String, ProjectedAmortization>(1);
		List<ProjectedAmortization> projIncomeAMZList = new ArrayList<ProjectedAmortization>(1);

		Date amzMonth = finEODEvent.getEventFromDate();
		List<ProjectedAmortization> finIncomeAMZList = finEODEvent.getIncomeAMZList();
		List<ProjectedAccrual> finProjAccList = finEODEvent.getProjectedAccrualList();

		// run amortization EOD or EOM
		Date amzMonthEnd = DateUtility.getMonthEnd(amzMonth);

		for (int i = 0; i < finProjAccList.size(); i++) {

			projAccrual = finProjAccList.get(i);
			curMonthEnd = projAccrual.getAccruedOn();

			// month end or partial settlement
			if (!projAccrual.isMonthEnd()) {
				curMonthEnd = DateUtility.getMonthEnd(projAccrual.getAccruedOn());
			}
			curMonthEnd = formatDate(curMonthEnd);

			prvMonthEnd = DateUtility.getMonthStart(projAccrual.getAccruedOn());
			prvMonthEnd = formatDate(DateUtility.addDays(prvMonthEnd, -1));

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

					if (StringUtils.equals(incomeAMZ.getAMZMethod(), AmortizationConstants.AMZ_METHOD_STRAIGHTLINE)) {

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
			}
		}

		return projIncomeAMZList;
	}

	/**
	 * This Method used to calculate the future amortizations based on previous cumulative </br>
	 * 
	 * @param incomeAMZ
	 * @param curMonthEnd
	 * 
	 * @return
	 */
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

	/**
	 * 
	 * Month End Income/Expense Amortization Calculation </br>
	 * 
	 * @param finEODEvent
	 * @param calProjIncomeAMZList
	 * @return
	 * @throws Exception
	 */
	private List<ProjectedAmortization> doSetIncomeAMZAmounts(FinEODEvent finEODEvent,
			List<ProjectedAmortization> calProjIncomeAMZList) throws Exception {

		Date appDate = finEODEvent.getAppDate();
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<ProjectedAmortization> finIncomeAMZList = finEODEvent.getIncomeAMZList();
		Date curMonthEnd = DateUtility.getMonthEnd(finEODEvent.getEventFromDate());

		for (ProjectedAmortization incomeAMZ : finIncomeAMZList) {
			for (ProjectedAmortization curProjIncomeAMZ : calProjIncomeAMZList) {

				if (curMonthEnd.compareTo(curProjIncomeAMZ.getMonthEndDate()) == 0
						&& incomeAMZ.getReferenceID() == curProjIncomeAMZ.getReferenceID()
						&& StringUtils.equals(incomeAMZ.getIncomeType(), curProjIncomeAMZ.getIncomeType())) {

					// PrvMonthAmz setting : EOM - Required, EOD - First run Required (EOM_ON_EOD : true).
					if (curMonthEnd.compareTo(incomeAMZ.getMonthEndDate()) != 0) {
						incomeAMZ.setPrvMonthAmz(incomeAMZ.getCurMonthAmz());
					}

					// SCREEN : appDate - DateUtility.getAppDate() ; EOM : amzMonth
					incomeAMZ.setCalculatedOn(appDate);

					incomeAMZ.setMonthEndDate(curMonthEnd);
					incomeAMZ.setActive(finMain.isFinIsActive());

					incomeAMZ.setCurMonthAmz(curProjIncomeAMZ.getAmortizedAmount());
					incomeAMZ.setAmortizedAmount(curProjIncomeAMZ.getCumulativeAmount());
					incomeAMZ.setUnAmortizedAmount(curProjIncomeAMZ.getUnAmortizedAmount());

					break;
				}
			}
		}

		return finIncomeAMZList;
	}

	/**
	 * 
	 * @param monthEndDate
	 * @param incomeAMZ
	 * @return
	 */
	private String prepareMapKey(Date monthEndDate, ProjectedAmortization incomeAMZ) {

		StringBuilder key = new StringBuilder();
		key.append(monthEndDate + "_");
		key.append(incomeAMZ.getIncomeType() + "_");
		key.append(incomeAMZ.getReferenceID());

		return key.toString();
	}

	/**
	 * calculate Amortization for Income and Expense based on AMZPerentage </br>
	 * 
	 * For Interest and Open Principal Balance methods calculate Monthly AMZ. </br>
	 * 
	 * For Straight Line Method calculate Cumulative AMZ. </br>
	 * 
	 * @param incomeAMZ
	 * @param amzPercentage
	 * @param prvCumAMZ
	 * @return
	 */
	private BigDecimal calculateProjIncomeAMZ(ProjectedAmortization incomeAMZ, ProjectedAccrual projAccrual,
			BigDecimal prvCumAMZ) {

		BigDecimal totAMZAmount = BigDecimal.ZERO;

		if (StringUtils.equals(incomeAMZ.getAMZMethod(), AmortizationConstants.AMZ_METHOD_STRAIGHTLINE)) {

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

	/**
	 * 
	 * @param finEODEvent
	 * @return
	 */
	private HashMap<String, Object> getDataMap(FinEODEvent finEODEvent) {

		HashMap<String, Object> dataMap = new HashMap<String, Object>();

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
					return DateUtility.compare(detail1.getAccruedOn(), detail2.getAccruedOn());
				}
			});
		}

		return projectedAccruals;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	private static Date getFormatDate(Date date) {
		return DateUtility.getDBDate(DateUtility.format(date, PennantConstants.DBDateFormat));
	}

	/**
	 * 
	 * @return
	 */
	public Date getPrvAMZMonthLog() {
		return projectedAmortizationDAO.getPrvAMZMonthLog();
	}

	/**
	 * 
	 * @param finReference
	 * @return
	 */
	public FinanceMain getFinanceForIncomeAMZ(String finReference) {
		return financeMainDAO.getFinanceForIncomeAMZ(finReference);
	}

	/**
	 * 
	 * @param finReference
	 * @return
	 */
	public FinanceMain getFinanceForAMZMethod(String finReference, boolean isActive) {
		return financeMainDAO.getFinMainsForEODByFinRef(finReference, isActive);
	}

	/**
	 * 
	 * @param finReference
	 * @return
	 */
	public FinanceProfitDetail getFinProfitForAMZ(String finReference) {
		return profitDetailsDAO.getFinProfitForAMZ(finReference);
	}

	/**
	 * 
	 * @param curMonthStart
	 * @return
	 */
	public List<FinanceMain> getFinListForIncomeAMZ(Date curMonthStart) {
		return financeMainDAO.getFinListForIncomeAMZ(curMonthStart);
	}

	private Date formatDate(Date date) {
		if (date != null) {
			return DateUtility.getDate(DateUtility.format(date, PennantConstants.DBDateFormat),
					PennantConstants.DBDateFormat);
		}
		return null;

	}

	// unused Methods

	/**
	 * Amortized Details Fees/ManualAdvises/Expenses <br>
	 * 
	 * @param custEODEvent
	 * @return
	 */
	@SuppressWarnings("unused")
	private CustEODEvent prepareAmortizationDetails(CustEODEvent custEODEvent) {
		logger.debug(" Entering ");

		for (FinEODEvent finEODEvent : custEODEvent.getFinEODEvents()) {

			FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
			String finRef = finPftDetail.getFinReference();

			// Get income/expense details
			List<ProjectedAmortization> projectedAMZList = this.projectedAmortizationDAO
					.getIncomeAMZDetailsByRef(finRef);
			//List<ProjectedAmortization> projectedAMZList = this.projectedAmortizationDAO.getIncomeAMZDetailsByRef(finRef, true);

			// Get Finance FeeDetails, ExpenseDetails
			List<FinFeeDetail> finFeeAMZList = this.finFeeDetailDAO.getAMZFinFeeDetails(finRef, "");
			List<FinExpenseDetails> finExpAMZList = this.finExpenseDetailsDAO.getAMZFinExpenseDetails(finRef, "");

			// prepare map based on IncomeType
			List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>(1);
			HashMap<String, List<ProjectedAmortization>> map = prepareAMZMap(projectedAMZList);

			projAMZList.addAll(getFeeProjAMZList(map, finFeeAMZList, finPftDetail));
			projAMZList.addAll(getExpProjAMZList(map, finExpAMZList, finPftDetail));

			if (AmortizationConstants.AMZ_REQ_MANUALADVISES) {

				List<ManualAdvise> finAdviseAMZList = this.manualAdviseDAO.getAMZManualAdviseDetails(finRef, "");
				projAMZList.addAll(getManualAdviseProjAMZList(map, finAdviseAMZList, finPftDetail));
			}

			finEODEvent.setIncomeAMZList(projAMZList);

			projAMZList = null;
			projectedAMZList = null;
		}

		logger.debug(" Leaving ");
		return custEODEvent;
	}

	/**
	 * 
	 * @param proAMZList
	 * @return
	 */
	private HashMap<String, List<ProjectedAmortization>> prepareAMZMap(List<ProjectedAmortization> proAMZList) {

		HashMap<String, List<ProjectedAmortization>> map = new HashMap<String, List<ProjectedAmortization>>(1);

		for (ProjectedAmortization amortization : proAMZList) {

			if (map.containsKey(amortization.getIncomeType())) {
				map.get(amortization.getIncomeType()).add(amortization);
			} else {

				if (amortization.getIncomeType().equals(AmortizationConstants.AMZ_INCOMETYPE_FEE)) {

					List<ProjectedAmortization> feeList = new ArrayList<ProjectedAmortization>(1);

					feeList.add(amortization);
					map.put(amortization.getIncomeType(), feeList);

				} else if (amortization.getIncomeType().equals(AmortizationConstants.AMZ_INCOMETYPE_EXPENSE)) {

					List<ProjectedAmortization> expenseList = new ArrayList<ProjectedAmortization>(1);

					expenseList.add(amortization);
					map.put(amortization.getIncomeType(), expenseList);

				} else if (amortization.getIncomeType().equals(AmortizationConstants.AMZ_INCOMETYPE_MANUALADVISE)) {

					List<ProjectedAmortization> manualAdviseList = new ArrayList<ProjectedAmortization>(1);

					manualAdviseList.add(amortization);
					map.put(amortization.getIncomeType(), manualAdviseList);
				}
			}
		}
		return map;
	}

	/**
	 * 
	 * @param map
	 * @param finMain
	 * @return
	 */
	private List<ProjectedAmortization> getFeeProjAMZList(HashMap<String, List<ProjectedAmortization>> map,
			List<FinFeeDetail> finFeeAMZList, FinanceProfitDetail finPftDetail) {

		boolean isSave = true;
		Date appDate = DateUtility.getAppDate();
		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>(1);

		for (FinFeeDetail finFeeDetail : finFeeAMZList) {

			isSave = true;
			ProjectedAmortization feeAMZ = null;

			if (map.containsKey(AmortizationConstants.AMZ_INCOMETYPE_FEE)) {
				for (ProjectedAmortization projAMZ : map.get(AmortizationConstants.AMZ_INCOMETYPE_FEE)) {

					if (finFeeDetail.getFinReference().equals(projAMZ.getFinReference())
							&& finFeeDetail.getFeeID() == projAMZ.getReferenceID()) {
						isSave = false;
						feeAMZ = prepareFeeProjAMZ(finPftDetail, projAMZ, finFeeDetail, appDate);
						break;
					}
				}
				if (isSave) {
					feeAMZ = prepareFeeProjAMZ(finPftDetail, null, finFeeDetail, appDate);
				}
			} else {
				feeAMZ = prepareFeeProjAMZ(finPftDetail, null, finFeeDetail, appDate);
			}

			if (feeAMZ != null) {
				projAMZList.add(feeAMZ);
			}
		}
		finFeeAMZList = null;

		return projAMZList;
	}

	/**
	 * 
	 * @param finMain
	 * @param projectedAMZ
	 * @param finfeeDetail
	 * @param appDate
	 * @return
	 */
	private ProjectedAmortization prepareFeeProjAMZ(FinanceProfitDetail finPftDetail,
			ProjectedAmortization projectedAMZ, FinFeeDetail finfeeDetail, Date appDate) {

		ProjectedAmortization projAMZ = null;
		if (projectedAMZ == null) {
			projAMZ = new ProjectedAmortization();
			projAMZ.setActive(true);
			projAMZ.setSaveProjAMZ(true);
			projAMZ.setLastMntOn(DateUtility.getSysDate());
			projAMZ.setFinReference(finPftDetail.getFinReference());
			projAMZ.setFinType(finPftDetail.getFinType());
			projAMZ.setCustID(finPftDetail.getCustId());
			projAMZ.setAMZMethod(finPftDetail.getAMZMethod());
			projAMZ.setReferenceID(finfeeDetail.getFeeID());
			projAMZ.setIncomeTypeID(finfeeDetail.getFeeTypeID());
			projAMZ.setIncomeType(AmortizationConstants.AMZ_INCOMETYPE_FEE);

			if (finfeeDetail.isTaxApplicable() && finfeeDetail.getTaxPercent() != null) {
				projAMZ.setCalcFactor(finfeeDetail.getTaxPercent());
			}
		} else {

			// Update required then change it as true AND REMOVE RETURN
			projAMZ = projectedAMZ;
			projAMZ.setUpdProjAMZ(false);
			return projAMZ;
		}

		projAMZ.setCalculatedOn(appDate);
		projAMZ.setMonthEndDate(DateUtility.getMonthEnd(appDate));
		projAMZ.setAmount(finfeeDetail.getActualAmount().subtract(finfeeDetail.getWaivedAmount()));

		BigDecimal actualIncomeAmt = getActualIncomeAmt(projAMZ);
		projAMZ.setActualAmount(actualIncomeAmt);

		return projAMZ;
	}

	/**
	 * 
	 * @param map
	 * @param finMain
	 * @return
	 */
	private List<ProjectedAmortization> getExpProjAMZList(HashMap<String, List<ProjectedAmortization>> map,
			List<FinExpenseDetails> finExpAMZList, FinanceProfitDetail finPftDetail) {

		boolean isSave = true;
		Date appDate = DateUtility.getAppDate();
		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>(1);

		for (FinExpenseDetails finExpDetail : finExpAMZList) {

			isSave = true;
			ProjectedAmortization expAMZ = null;

			if (map.containsKey(AmortizationConstants.AMZ_INCOMETYPE_EXPENSE)) {
				for (ProjectedAmortization projAMZ : map.get(AmortizationConstants.AMZ_INCOMETYPE_EXPENSE)) {

					if (finExpDetail.getFinReference().equals(projAMZ.getFinReference())
							&& finExpDetail.getFinExpenseId() == projAMZ.getReferenceID()) {
						isSave = false;
						expAMZ = prepareExpProjAMZ(finPftDetail, projAMZ, finExpDetail, appDate);
						break;
					}
				}
				if (isSave) {
					expAMZ = prepareExpProjAMZ(finPftDetail, null, finExpDetail, appDate);
				}
			} else {
				expAMZ = prepareExpProjAMZ(finPftDetail, null, finExpDetail, appDate);
			}

			if (expAMZ != null) {
				projAMZList.add(expAMZ);
			}
		}
		finExpAMZList = null;

		return projAMZList;
	}

	/**
	 * 
	 * @param finMain
	 * @param projectedAMZ
	 * @param expenseDetail
	 * @param appDate
	 * @return
	 */
	private ProjectedAmortization prepareExpProjAMZ(FinanceProfitDetail finPftDetail,
			ProjectedAmortization projectedAMZ, FinExpenseDetails expenseDetail, Date appDate) {

		ProjectedAmortization projAMZ = null;
		if (projectedAMZ == null) {
			projAMZ = new ProjectedAmortization();
			projAMZ.setActive(true);
			projAMZ.setSaveProjAMZ(true);
			projAMZ.setLastMntOn(DateUtility.getSysDate());
			projAMZ.setFinReference(finPftDetail.getFinReference());
			projAMZ.setFinType(finPftDetail.getFinType());
			projAMZ.setCustID(finPftDetail.getCustId());
			projAMZ.setAMZMethod(finPftDetail.getAMZMethod());
			projAMZ.setCalcFactor(BigDecimal.ZERO);
			projAMZ.setReferenceID(expenseDetail.getFinExpenseId());
			projAMZ.setIncomeTypeID(expenseDetail.getExpenseTypeId());
			projAMZ.setIncomeType(AmortizationConstants.AMZ_INCOMETYPE_EXPENSE);
		} else {

			// Update required then mark it as true AND REMOVE RETURN
			projAMZ = projectedAMZ;
			projAMZ.setUpdProjAMZ(false);
			return projAMZ;
		}

		projAMZ.setCalculatedOn(appDate);
		projAMZ.setMonthEndDate(DateUtility.getMonthEnd(appDate));
		projAMZ.setAmount(expenseDetail.getAmount());

		// for expense 100% amount amortized
		projAMZ.setActualAmount(projAMZ.getAmount());

		return projAMZ;
	}

	/**
	 * 
	 * @param map
	 * @param finMain
	 * @return
	 */
	private List<ProjectedAmortization> getManualAdviseProjAMZList(HashMap<String, List<ProjectedAmortization>> map,
			List<ManualAdvise> finAdviseAMZList, FinanceProfitDetail finPftDetail) {

		boolean isSave = true;
		Date appDate = DateUtility.getAppDate();
		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>(1);

		for (ManualAdvise manualAdvise : finAdviseAMZList) {

			isSave = true;
			ProjectedAmortization mdAMZ = null;

			if (map.containsKey(AmortizationConstants.AMZ_INCOMETYPE_MANUALADVISE)) {
				for (ProjectedAmortization projAMZ : map.get(AmortizationConstants.AMZ_INCOMETYPE_MANUALADVISE)) {

					if (manualAdvise.getFinReference().equals(projAMZ.getFinReference())
							&& manualAdvise.getAdviseID() == projAMZ.getReferenceID()) {
						isSave = false;
						mdAMZ = prepareManualAdviseProjAMZ(finPftDetail, projAMZ, manualAdvise, appDate);
						break;
					}
				}
				if (isSave) {
					mdAMZ = prepareManualAdviseProjAMZ(finPftDetail, null, manualAdvise, appDate);
				}
			} else {
				mdAMZ = prepareManualAdviseProjAMZ(finPftDetail, null, manualAdvise, appDate);
			}

			if (mdAMZ != null) {
				projAMZList.add(mdAMZ);
			}
		}
		finAdviseAMZList = null;

		return projAMZList;
	}

	/**
	 * 
	 * @param finMain
	 * @param projectedAMZ
	 * @param manualAdvise
	 * @param appDate
	 * @return
	 */
	private ProjectedAmortization prepareManualAdviseProjAMZ(FinanceProfitDetail finPftDetail,
			ProjectedAmortization projectedAMZ, ManualAdvise manualAdvise, Date appDate) {

		ProjectedAmortization projAMZ = null;
		if (projectedAMZ == null) {
			projAMZ = new ProjectedAmortization();
			projAMZ.setActive(true);
			projAMZ.setSaveProjAMZ(true);
			projAMZ.setLastMntOn(DateUtility.getSysDate());
			projAMZ.setFinReference(finPftDetail.getFinReference());
			projAMZ.setFinType(finPftDetail.getFinType());
			projAMZ.setCustID(finPftDetail.getCustId());
			projAMZ.setAMZMethod(finPftDetail.getAMZMethod());
			projAMZ.setReferenceID(manualAdvise.getAdviseID());
			projAMZ.setIncomeTypeID(manualAdvise.getFeeTypeID());
			projAMZ.setIncomeType(AmortizationConstants.AMZ_INCOMETYPE_MANUALADVISE);

			// Need to add Column "TaxPercent" in ManualAdvise Table
			if (manualAdvise.isTaxApplicable() && manualAdvise.getTaxPercent() != null) {
				projAMZ.setCalcFactor(manualAdvise.getTaxPercent());
			}
		} else {

			// Update required then mark it as true AND REMOVE RETURN
			projAMZ = projectedAMZ;
			projAMZ.setUpdProjAMZ(false);
			return projAMZ;
		}

		projAMZ.setCalculatedOn(appDate);
		projAMZ.setMonthEndDate(DateUtility.getMonthEnd(appDate));
		projAMZ.setAmount(manualAdvise.getAdviseAmount());

		BigDecimal actualIncomeAmt = getActualIncomeAmt(projAMZ);
		projAMZ.setActualAmount(actualIncomeAmt);

		return projAMZ;
	}

	/**
	 * for expense 100% amortized, for income and manual advises percentage factor considered </br>
	 * 
	 * @param projAmortization
	 * @return
	 */
	private BigDecimal getActualIncomeAmt(ProjectedAmortization projAMZ) {

		// Remove GST percentage from fee amount
		BigDecimal actualIncomeAmt = (projAMZ.getAmount().multiply(new BigDecimal(100)))
				.divide((new BigDecimal(100).add(projAMZ.getCalcFactor())), 2, RoundingMode.HALF_DOWN);

		return actualIncomeAmt;
	}

	/**
	 * 
	 * @param prvMonthEnd
	 * @param prvProjIncomeAMZList
	 * @return
	 */
	@SuppressWarnings("unused")
	private HashMap<String, ProjectedAmortization> getPrvProjIncomeAMZMap(Date prvMonthEnd,
			List<ProjectedAmortization> prvProjIncomeAMZList) {

		HashMap<String, ProjectedAmortization> prvProjIncomeAMZMap = new HashMap<String, ProjectedAmortization>(1);

		if (prvProjIncomeAMZList != null && !prvProjIncomeAMZList.isEmpty()) {
			for (ProjectedAmortization prvProjAMZ : prvProjIncomeAMZList) {

				String prvKey = prepareMapKey(prvMonthEnd, prvProjAMZ);
				prvProjIncomeAMZMap.put(prvKey, prvProjAMZ);
			}
		}

		return prvProjIncomeAMZMap;
	}

	/**
	 * 
	 * @param prvProjIncomeAMZMap
	 * @param incomeAMZ
	 * @param prvKey
	 * @return
	 */
	@SuppressWarnings("unused")
	private ProjectedAmortization getPrvProjIncomeAMZ(HashMap<String, ProjectedAmortization> prvProjIncomeAMZMap,
			ProjectedAmortization incomeAMZ, String prvKey) {

		ProjectedAmortization prvProjIncomeAMZ = null;

		if (prvProjIncomeAMZMap != null && prvProjIncomeAMZMap.containsKey(prvKey)) {
			prvProjIncomeAMZ = prvProjIncomeAMZMap.get(prvKey);
		} else {
			prvProjIncomeAMZ = new ProjectedAmortization();
			prvProjIncomeAMZ.setUnAmortizedAmount(incomeAMZ.getActualAmount());
		}
		return prvProjIncomeAMZ;
	}

	/**
	 * 
	 * @param incomeAMZList
	 * @return
	 */
	@SuppressWarnings("unused")
	private HashMap<String, List<ProjectedAmortization>> prepareIncomeAMZMap(
			List<ProjectedAmortization> incomeAMZList) {

		List<ProjectedAmortization> list = null;
		HashMap<String, List<ProjectedAmortization>> incomeAMZMap = new HashMap<String, List<ProjectedAmortization>>(1);

		for (ProjectedAmortization projAMZ : incomeAMZList) {

			if (incomeAMZMap.containsKey(projAMZ.getFinReference())) {
				incomeAMZMap.get(projAMZ.getFinReference()).add(projAMZ);
			} else {
				list = new ArrayList<ProjectedAmortization>(1);
				list.add(projAMZ);

				incomeAMZMap.put(projAMZ.getFinReference(), list);
			}
		}
		return incomeAMZMap;
	}

	/**
	 * Method for calculate Cumulative Days.</br>
	 * 
	 * @param finSchdDetails
	 * @return
	 */
	@SuppressWarnings("unused")
	private static FinanceScheduleDetail getSchdTotals(List<FinanceScheduleDetail> finSchdDetails) {

		FinanceScheduleDetail schdDetail = new FinanceScheduleDetail();
		BigDecimal totalPft = BigDecimal.ZERO;
		BigDecimal totalPriOS = BigDecimal.ZERO;

		for (FinanceScheduleDetail scheduleDetail : finSchdDetails) {

			totalPft = totalPft.add(scheduleDetail.getProfitCalc());
			totalPriOS = totalPriOS.add(scheduleDetail.getClosingBalance());
		}

		schdDetail.setProfitCalc(totalPft);
		schdDetail.setClosingBalance(totalPriOS);
		return schdDetail;
	}

	// Calculate Average POS

	/**
	 * 
	 * @param finEODEvent
	 * @param appDate
	 * @param calFromFinStartDate
	 * @throws Exception
	 */
	public BigDecimal calculateAveragePOS(FinEODEvent finEODEvent) throws Exception {

		HashMap<Date, ProjectedAccrual> map = new HashMap<Date, ProjectedAccrual>(1);
		List<Date> months = new ArrayList<Date>(1);
		months.add(finEODEvent.getEventFromDate());

		FinanceScheduleDetail prvSchd = null;
		FinanceScheduleDetail curSchd = null;
		FinanceScheduleDetail nextSchd = null;

		Date prvSchdDate = null;
		Date curSchdDate = null;
		Date nextSchdDate = null;

		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schdDetails = finEODEvent.getFinanceScheduleDetails();

		Date appDateMonthStart = DateUtility.getMonthStart(finEODEvent.getEventFromDate());
		Date maturityDate = getFormatDate(finMain.getMaturityDate());
		Date prvMonthEnd = DateUtility.addDays(appDateMonthStart, -1);

		int noOfDays = 0;

		// looping schedules
		for (int i = 0; i < schdDetails.size(); i++) {

			curSchd = schdDetails.get(i);
			curSchdDate = curSchd.getSchDate();

			if (curSchdDate.compareTo(appDateMonthStart) < 0) {
				continue;
			}

			if (i == 0) {
				prvSchd = curSchd;
			} else {
				prvSchd = schdDetails.get(i - 1);
			}
			if (i == schdDetails.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schdDetails.get(i + 1);
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
					prjAcc.setFinReference(finMain.getFinReference());
					prjAcc.setAccruedOn(curMonthEnd);
					prjAcc.setMonthEnd(true);

					// Prepare Map and List
					map.put(curMonthEnd, prjAcc);
				}

				// START : Actual ACCRUAL calculation includes current date
				Date nextMonthStart = DateUtility.addDays(curMonthEnd, 1);
				Date curMonthStart = DateUtility.getMonthStart(curMonthEnd);

				// Schedules between Previous MonthEnd and CurMonthEnd
				if (prvSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0) {

					isSchdAmz = true;
					if (curSchdDate.compareTo(getFormatDate(finMain.getFinStartDate())) != 0) {

						int days = DateUtility.getDaysBetween(curSchdDate, prvSchdDate);
						schdAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
						noOfDays = noOfDays + days;
					}
				}

				// multiple months between two schedules (Quarterly, Half Yearly ..)
				if (curMonthEnd.compareTo(curSchdDate) < 0) {

					int days = DateUtility.getDaysBetween(nextMonthStart, curMonthStart);
					prvAvgPOS = prvSchd.getClosingBalance().multiply(new BigDecimal(days));
					noOfDays = noOfDays + days;

				} else {
					// Calculation From CurSchdDate to Current MonthEnd 
					if (curMonthEnd.compareTo(prvSchdDate) >= 0 && curMonthEnd.compareTo(nextSchdDate) < 0) {

						int days = DateUtility.getDaysBetween(nextMonthStart, curSchdDate);
						curAvgPOS = curSchd.getClosingBalance().multiply(new BigDecimal(days));
						noOfDays = noOfDays + days;
					}
					// Calculation From CurSchdDate to Previous MonthEnd
					if (prvMonthEnd != null && !isSchdAmz) {

						int days = DateUtility.getDaysBetween(curSchdDate, curMonthStart);
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

	/**
	 * 
	 * @param finReference
	 * @param amzMethod
	 */
	void updateAMZMethod(String finReference, String amzMethod) {
		profitDetailsDAO.updateAMZMethod(finReference, amzMethod);
	}

	// setters / getters

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public void setFinExpenseDetailsDAO(FinExpenseDetailsDAO finExpenseDetailsDAO) {
		this.finExpenseDetailsDAO = finExpenseDetailsDAO;
	}

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}
}
