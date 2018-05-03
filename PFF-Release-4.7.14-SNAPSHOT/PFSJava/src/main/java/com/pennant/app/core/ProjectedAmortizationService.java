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
 * Modified Date    :  22-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-01-2018       Pennant	                 0.1                                            * 
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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.finance.FinExpenseDetailsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;

public class ProjectedAmortizationService extends ServiceHelper {

	private static final long serialVersionUID = 1961398570971714190L;
	private static Logger logger = Logger.getLogger(ProjectedAmortizationService.class);

	private RuleDAO ruleDAO;
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
	public CustEODEvent processAmortization(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			finEODEvent = incomeAmortization(finEODEvent, custEODEvent);
		}

		logger.debug(" Leaving ");
		return custEODEvent;
	}

	/**
	 * 
	 * @param finEODEvent
	 * @param custEODEvent
	 * @return
	 * @throws Exception 
	 */
	private FinEODEvent incomeAmortization(FinEODEvent finEODEvent, CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");

		// Income/ManualAdvise/Expense details
		saveOrUpdateAMZDetails(finEODEvent);

		// amortization method
		updateAMZMethod(finEODEvent);

		// calculate MonthEnd ACCRUALS
		List<ProjectedAccrual> accrualList = calculateMonthEndAccruals(finEODEvent, custEODEvent.getEodValueDate(), BigDecimal.ZERO, true);
		finEODEvent.setProjectedAccrualList(accrualList);

		logger.debug(" Leaving ");
		return finEODEvent;
	}

	/**
	 * Amortized Details Fees/ManualAdvises/Expenses <br>
	 * @param finEODEvent
	 */
	private void saveOrUpdateAMZDetails(FinEODEvent finEODEvent) {
		logger.debug(" Entering ");

		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>(1);
		FinanceMain finMain = finEODEvent.getFinanceMain();

		// prepare map based on IncomeType
		List<ProjectedAmortization> projectedAMZList = projectedAmortizationDAO.getIncomeAMZDetailsByRef(finMain.getFinReference());
		HashMap<String, List<ProjectedAmortization>> map = prepareAMZMap(projectedAMZList);

		projAMZList.addAll(getFeeProjAMZList(map, finMain));
		projAMZList.addAll(getManualAdviseProjAMZList(map, finMain));
		projAMZList.addAll(getExpProjAMZList(map, finMain));

		logger.debug(" Leaving ");
		finEODEvent.setProjectedAMZList(projAMZList);
	}

	/**
	 * 
	 * @param finEODEvent
	 */
	private void updateAMZMethod(FinEODEvent finEODEvent) {
		logger.debug(" Entering ");

		HashMap<String, String> map = new HashMap<String, String>(1);

		FinanceType finType =  finEODEvent.getFinType();
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<ProjectedAmortization> projAMZList = finEODEvent.getProjectedAMZList();

		for (ProjectedAmortization amortization : projAMZList) {
			if (amortization.getFinReference().equals(finMain.getFinReference())
					&& StringUtils.isNotBlank(amortization.getAMZMethod())) {

				if (!map.containsKey(finMain.getFinReference())) {
					map.put(finMain.getFinReference(), amortization.getAMZMethod());
				}
				break;
			}
		}

		for (ProjectedAmortization projAMZ : projAMZList) {
			if (map.containsKey(projAMZ.getFinReference())) {

				projAMZ.setAMZMethod(map.get(projAMZ.getFinReference()));
			} else {

				// execute rule and get amortization method
				String ruleResult = identifyAMZMethod(finMain, finType);
				map.put(finMain.getFinReference(), ruleResult);
				projAMZ.setAMZMethod(ruleResult);
			}
		}

		if (map.containsKey(finMain.getFinReference())) {
			finEODEvent.setAMZMethod(map.get(finMain.getFinReference()));
		}

		logger.debug(" Leaving ");
	}

	/**
	 * 1. Income / Expense Amortization Calculation Based on Method </br>
	 * 2. Interest Method - All other loan types except FLEXI and 0 interest loan types </br>
	 * 3. Opening Principal Balance Method - 0 Interest </br>
	 * 4. Straight Line Method - FLEXI/OD Loans and Floating rate type
	 * 
	 * @param finMain
	 * @param financeType
	 * @return
	 * 
	 */
	private String identifyAMZMethod(FinanceMain finMain, FinanceType finType) {
		logger.debug("Entering");

		// prepare map
		HashMap<String, Object> dataMap = getDataMap(finMain, finType);

		// get SQL rule
		String sqlRule = ruleDAO.getAmountRule(AmortizationConstants.AMZ_METHOD_RULE, AmortizationConstants.AMZ_METHOD_RULE,
				AmortizationConstants.AMZ_METHOD_RULE);

		// Rule Execution
		String ruleResult = (String) ruleExecutionUtil.executeRule(sqlRule, dataMap, "", RuleReturnType.STRING);

		logger.debug("Leaving");
		return ruleResult;
	}

	/**
	 * 
	 * @param map
	 * @param finFeeAMZList
	 * @param custID
	 * @param valueDate
	 * @return
	 */
	private List<ProjectedAmortization> getFeeProjAMZList(HashMap<String, List<ProjectedAmortization>> map, FinanceMain finMain) {

		boolean isSave = true;
		Date valueDate = DateUtility.getAppDate();
		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>(1);
		List<FinFeeDetail> finFeeAMZList = finFeeDetailDAO.getAMZFinFeeDetails(finMain.getFinReference(), "");

		for (FinFeeDetail finFeeDetail : finFeeAMZList) {

			isSave = true;
			ProjectedAmortization feeAMZ = null; 

			if (map.containsKey(AmortizationConstants.AMZ_INCOMETYPE_FEE)) {
				for (ProjectedAmortization projAMZ : map.get(AmortizationConstants.AMZ_INCOMETYPE_FEE)) {

					if (finFeeDetail.getFinReference().equals(projAMZ.getFinReference())
							&& finFeeDetail.getFeeTypeID() == projAMZ.getRefenceID()) {
						isSave = false;
						feeAMZ = prepareFeeProjAMZ(finMain, projAMZ, finFeeDetail, valueDate);
						break;
					}
				}
				if (isSave) {
					feeAMZ = prepareFeeProjAMZ(finMain, null, finFeeDetail, valueDate);
				}
			} else {
				feeAMZ = prepareFeeProjAMZ(finMain, null, finFeeDetail, valueDate);
			}
			projAMZList.add(feeAMZ);
		}

		return projAMZList;
	}

	/**
	 * 
	 * @param isSave
	 * @param custID
	 * @param projectedAMZ
	 * @param feeDetail
	 * @return
	 */
	private ProjectedAmortization prepareFeeProjAMZ(FinanceMain finMain, ProjectedAmortization projectedAMZ,
			FinFeeDetail finfeeDetail, Date valueDate) {

		ProjectedAmortization projAMZ = null;
		if (projectedAMZ == null) {
			projAMZ = new ProjectedAmortization();
			projAMZ.setActive(true);
			projAMZ.setFinReference(finMain.getFinReference());
			projAMZ.setFinType(finMain.getFinType());
			projAMZ.setCustID(finMain.getCustID());
			projAMZ.setRefenceID(finfeeDetail.getFeeTypeID());
			projAMZ.setCalcFactor(finfeeDetail.getTaxPercent());
			projAMZ.setIncomeType(AmortizationConstants.AMZ_INCOMETYPE_FEE);
		} else {
			projAMZ = projectedAMZ;
			projAMZ.setUpdProjAMZ(true);
		}
		projAMZ.setLastMntOn(valueDate);
		projAMZ.setAmount(finfeeDetail.getActualAmount().subtract(finfeeDetail.getWaivedAmount()));

		return projAMZ;
	}

	/**
	 * 
	 * @param map
	 * @param finAdviseAMZList
	 * @param custID
	 * @param valueDate
	 * @return
	 */
	private List<ProjectedAmortization> getManualAdviseProjAMZList(HashMap<String, List<ProjectedAmortization>> map,
			FinanceMain finMain) {

		boolean isSave = true;
		Date valueDate = DateUtility.getAppDate();
		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>(1);
		List<ManualAdvise> finAdviseAMZList = manualAdviseDAO.getAMZManualAdviseDetails(finMain.getFinReference(), "");

		for (ManualAdvise manualAdvise : finAdviseAMZList) {

			isSave = true;
			ProjectedAmortization mdAMZ = null; 

			if (map.containsKey(AmortizationConstants.AMZ_INCOMETYPE_MANUALADVISE)) {
				for (ProjectedAmortization projAMZ : map.get(AmortizationConstants.AMZ_INCOMETYPE_MANUALADVISE)) {

					if (manualAdvise.getFinReference().equals(projAMZ.getFinReference())
							&& manualAdvise.getFeeTypeID() == projAMZ.getRefenceID()) {
						isSave = false;
						mdAMZ = prepareManualAdviseProjAMZ(finMain, projAMZ, manualAdvise, valueDate);
						break;
					}
				}
				if (isSave) {
					mdAMZ = prepareManualAdviseProjAMZ(finMain, null, manualAdvise, valueDate);
				}
			} else {
				mdAMZ = prepareManualAdviseProjAMZ(finMain, null, manualAdvise, valueDate);
			}
			projAMZList.add(mdAMZ);
		}
		return projAMZList;
	}

	/**
	 * 
	 * @param isSave
	 * @param custID
	 * @param projectedAMZ
	 * @param feeDetail
	 * @return
	 */
	private ProjectedAmortization prepareManualAdviseProjAMZ(FinanceMain finMain, ProjectedAmortization projectedAMZ,
			ManualAdvise manualAdvise, Date valueDate) {

		ProjectedAmortization projAMZ = null;
		if (projectedAMZ == null) {
			projAMZ = new ProjectedAmortization();
			projAMZ.setActive(true);
			projAMZ.setFinReference(finMain.getFinReference());
			projAMZ.setFinType(finMain.getFinType());
			projAMZ.setCustID(finMain.getCustID());
			projAMZ.setRefenceID(manualAdvise.getFeeTypeID());
			projAMZ.setCalcFactor(manualAdvise.getTaxPercent());
			projAMZ.setIncomeType(AmortizationConstants.AMZ_INCOMETYPE_MANUALADVISE);
		} else {
			projAMZ = projectedAMZ;
			projAMZ.setUpdProjAMZ(true);
		}
		projAMZ.setLastMntOn(valueDate);
		projAMZ.setAmount(manualAdvise.getAdviseAmount());

		return projAMZ;
	}

	/**
	 * 
	 * @param map
	 * @param finExpenseAMZList
	 * @param custID
	 * @param valueDate
	 * @return
	 */
	private List<ProjectedAmortization> getExpProjAMZList(HashMap<String, List<ProjectedAmortization>> map,
			FinanceMain finMain) {

		boolean isSave = true;
		Date valueDate = DateUtility.getAppDate();
		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>(1);
		List<FinExpenseDetails> finExpenseAMZList = finExpenseDetailsDAO.getAMZFinExpenseDetails(finMain.getFinReference(), "");

		for (FinExpenseDetails finExpDetail : finExpenseAMZList) {

			isSave = true;
			ProjectedAmortization expAMZ = null; 

			if (map.containsKey(AmortizationConstants.AMZ_INCOMETYPE_EXPENSE)) {
				for (ProjectedAmortization projAMZ : map.get(AmortizationConstants.AMZ_INCOMETYPE_EXPENSE)) {

					if (finExpDetail.getFinReference().equals(projAMZ.getFinReference())
							&& finExpDetail.getExpenseTypeId() == projAMZ.getRefenceID()) {
						isSave = false;
						expAMZ = prepareExpProjAMZ(finMain, projAMZ, finExpDetail, valueDate);
						break;
					}
				}
				if (isSave) {
					expAMZ = prepareExpProjAMZ(finMain, null, finExpDetail, valueDate);
				}
			} else {
				expAMZ = prepareExpProjAMZ(finMain, null, finExpDetail, valueDate);
			}
			projAMZList.add(expAMZ);
		}
		return projAMZList;
	}

	/**
	 * 
	 * @param isSave
	 * @param custID
	 * @param projectedAMZ
	 * @param feeDetail
	 * @return
	 */
	private ProjectedAmortization prepareExpProjAMZ(FinanceMain finMain, ProjectedAmortization projectedAMZ,
			FinExpenseDetails expenseDetail, Date valueDate) {

		ProjectedAmortization projAMZ = null;
		if (projectedAMZ == null) {
			projAMZ = new ProjectedAmortization();
			projAMZ.setActive(true);
			projAMZ.setFinReference(finMain.getFinReference());
			projAMZ.setFinType(finMain.getFinType());
			projAMZ.setCustID(finMain.getCustID());
			projAMZ.setRefenceID(expenseDetail.getExpenseTypeId());
			projAMZ.setIncomeType(AmortizationConstants.AMZ_INCOMETYPE_EXPENSE);
		} else {
			projAMZ = projectedAMZ;
			projAMZ.setUpdProjAMZ(true);
		}
		projAMZ.setLastMntOn(valueDate);
		projAMZ.setAmount(expenseDetail.getAmount());

		return projAMZ;
	}

	/**
	 * Month End ACCRUAL Calculation
	 * @param finMain
	 * @param schdDetails
	 * @param appDate
	 * @return
	 */
	public List<ProjectedAccrual> calculateMonthEndAccruals(FinEODEvent finEODEvent, Date appDate, BigDecimal prvMthAmz,
			boolean calFromFinStartDate) throws Exception {
		logger.debug(" Entering ");

		FinanceMain finMain = finEODEvent.getFinanceMain();
		String amzMethod = StringUtils.trimToEmpty(finEODEvent.getAMZMethod());
		List<FinanceScheduleDetail> schdDetails = finEODEvent.getFinanceScheduleDetails();

		HashMap<Date, ProjectedAccrual> map = new HashMap<Date, ProjectedAccrual>(1);
		List<ProjectedAccrual> list = new ArrayList<ProjectedAccrual>();
		Date appDateMonthStart = DateUtility.getMonthStartDate(appDate);
		Date appDateMonthEnd = DateUtility.getMonthEndDate(appDate);

		if (getFormatDate(finMain.getMaturityDate()).compareTo(appDateMonthStart) < 0) {
			return list;
		}

		int noOfDays = 0;
		int cumulativeDays = 0;
		BigDecimal cumulativePft = BigDecimal.ZERO;
		BigDecimal cumulativePOS = BigDecimal.ZERO;
		BigDecimal amzPercentage = BigDecimal.ZERO;

		// get schedules totals
		FinanceScheduleDetail schdTotals = getSchdTotals(schdDetails);
		BigDecimal totalProfit = schdTotals.getProfitCalc();
		BigDecimal totalPOS = schdTotals.getClosingBalance();

		FinanceScheduleDetail prvSchd	= null;
		FinanceScheduleDetail curSchd	= null;
		FinanceScheduleDetail nextSchd	= null;

		Date prvSchdDate	= null;
		Date curSchdDate	= null;
		Date nextSchdDate	= null;
		Date prvMonthEnd	= null;

		List<Date> months = new ArrayList<Date>();
		List<Date> monthsCopy = new ArrayList<Date>();
		Date newMonth = null;

		if (calFromFinStartDate
				|| DateUtility.getMonthEndDate(getFormatDate(finMain.getFinStartDate())).compareTo(appDateMonthEnd) == 0) {

			newMonth = new Date(DateUtility.getMonthEndDate(finMain.getFinStartDate()).getTime());
		} else {

			newMonth = new Date(appDateMonthEnd.getTime());
			prvMonthEnd = DateUtility.addDays(appDateMonthStart, -1);
			cumulativePft = prvMthAmz;

			ProjectedAccrual prjAcc = new ProjectedAccrual();
			prjAcc.setFinReference(finMain.getFinReference());
			prjAcc.setAccruedOn(prvMonthEnd);
			prjAcc.setCumulativeAccrued(prvMthAmz);
			map.put(prvMonthEnd, prjAcc);
		}

		// Prepare Months list From FinStartDate to MaturityDate
		while (DateUtility.getMonthEndDate(finMain.getMaturityDate()).compareTo(newMonth) >= 0) {
			months.add(getFormatDate((Date) newMonth.clone()));
			newMonth = DateUtility.addMonths(newMonth, 1);
			newMonth = DateUtility.getMonthEndDate(newMonth);
		}
		monthsCopy.addAll(months);

		for (int i = 0; i < schdDetails.size(); i++) {

			curSchd = schdDetails.get(i);
			curSchdDate = curSchd.getSchDate();

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

			if (!calFromFinStartDate && curSchdDate.compareTo(appDateMonthStart) < 0) {
				continue;
			}

			for (Date curMonthEnd : monthsCopy) {

				BigDecimal schdPftAmz = BigDecimal.ZERO;
				BigDecimal curPftAmz = BigDecimal.ZERO;
				BigDecimal prvPftAmz = BigDecimal.ZERO;
				BigDecimal pftAmz = BigDecimal.ZERO;

				BigDecimal schdPOSAmz = BigDecimal.ZERO;
				BigDecimal curPOSAmz = BigDecimal.ZERO;
				BigDecimal prvPOSAmz = BigDecimal.ZERO;
				BigDecimal posAmz = BigDecimal.ZERO;

				boolean isSchdAmz = false;
				ProjectedAccrual prjAcc = null;

				if (map.containsKey(curMonthEnd)) {
					prjAcc = map.get(curMonthEnd);			
				} else {
					prjAcc = new ProjectedAccrual();
					prjAcc.setFinReference(finMain.getFinReference());
					prjAcc.setAccruedOn(curMonthEnd);
				}

				// ACCRUAL calculation includes current date
				Date nextMonthStart = DateUtility.addDays(curMonthEnd, 1);
				Date curMonthStart = DateUtility.getMonthStartDate(curMonthEnd);

				// Schedules between Previous MonthEnd to CurMonthEnd
				if (prvSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0) {

					isSchdAmz = true;
					schdPftAmz = curSchd.getProfitCalc();
					if (curSchdDate.compareTo(getFormatDate(finMain.getFinStartDate())) != 0) {
						schdPOSAmz = prvSchd.getClosingBalance();
					}
				}

				if (curMonthEnd.compareTo(curSchdDate) < 0) {

					// multiple months between schedules
					curPftAmz = calProfitAMZ(curSchd, prvSchdDate, nextMonthStart, curMonthStart);
					curPOSAmz = calPOSAMZ(curSchd, prvSchdDate, nextMonthStart, curMonthStart, prvSchd.getClosingBalance());
				} else {

					// Calculation From MonthEnd to CurSchdDate 
					if (curMonthEnd.compareTo(prvSchdDate) >= 0 && curMonthEnd.compareTo(nextSchdDate) < 0) {

						curPftAmz = calProfitAMZ(nextSchd, curSchdDate, nextMonthStart, curSchdDate);
						curPOSAmz = calPOSAMZ(nextSchd, curSchdDate, nextMonthStart, curSchdDate, curSchd.getClosingBalance());
					}

					// Calculation From CurSchdDate to Previous MonthEnd
					if (prvMonthEnd != null && !isSchdAmz) {

						prvPftAmz = calProfitAMZ(curSchd, prvSchdDate, curSchdDate, curMonthStart);
						prvPOSAmz = calPOSAMZ(curSchd, prvSchdDate, curSchdDate, curMonthStart, prvSchd.getClosingBalance());
					}
				}

				pftAmz = schdPftAmz.add(prvPftAmz).add(curPftAmz);
				posAmz = schdPOSAmz.add(prvPOSAmz).add(curPOSAmz);

				// Adjust remaining profit to maturity MonthEnd to avoid rounding issues
				if (DateUtility.getMonthEndDate(getFormatDate(finMain.getMaturityDate())).compareTo(curMonthEnd) == 0) {

					prjAcc.setPftAmz(totalProfit.subtract(cumulativePft));
					prjAcc.setPOSAccrued(totalPOS.subtract(cumulativePOS));
				} else {

					prjAcc.setPftAmz(prjAcc.getPftAmz().add(pftAmz));
					prjAcc.setPOSAccrued(prjAcc.getPOSAccrued().add(posAmz));
				}

				if (curSchdDate.compareTo(curMonthStart) >= 0 && curSchdDate.compareTo(curMonthEnd) <= 0) {
					if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
						prjAcc.setSchdDate(curSchdDate);
						prjAcc.setSchdPri(curSchd.getPrincipalSchd().subtract(curSchd.getCpzAmount()));
						prjAcc.setSchdPft(curSchd.getProfitSchd().add(curSchd.getCpzAmount()));
						prjAcc.setSchdTot(curSchd.getRepayAmount());
					}
				}

				// Current and Next Schedules are equal in Maturity
				if (curMonthEnd.compareTo(nextSchdDate) < 0 || curSchdDate.compareTo(nextSchdDate) == 0) {

					// No of Days and Cumulative Days
					cumulativeDays = getCumulativeDays(finMain, curMonthEnd);
					prjAcc.setNoOfDays(cumulativeDays - noOfDays);
					prjAcc.setCumulativeDays(cumulativeDays);
					noOfDays = cumulativeDays;

					// AMZ Percentage, use Previous Cumulative for calculation
					amzPercentage = calAMZPercentage(totalProfit, totalPOS, finMain, prjAcc, cumulativePft, cumulativePOS, amzMethod);
					prjAcc.setAMZPercentage(amzPercentage);

					cumulativePft = cumulativePft.add(prjAcc.getPftAmz());
					prjAcc.setPftAccrued(prjAcc.getPftAmz());
					prjAcc.setCumulativeAccrued(cumulativePft);

					cumulativePOS = cumulativePOS.add(prjAcc.getPOSAccrued());
					prjAcc.setCumulativePOS(cumulativePOS);

					prvMonthEnd = curMonthEnd;
					months.remove(curMonthEnd);
					monthsCopy = new ArrayList<Date>(months);
				}

				// Prepare Map and List
				if (!map.containsKey(curMonthEnd)) {
					map.put(curMonthEnd, prjAcc);
					list.add(map.get(curMonthEnd));
				}

				if (curMonthEnd.compareTo(curSchdDate) >= 0) {
					break;
				}
			}
		}

		logger.debug(" Leaving ");
		return list;
	}

	/**
	 * Month End Income/Expense Amortization Calculation
	 * 
	 * @param finMain
	 * @param schdDetails
	 * @param appDate
	 * @return
	 */
	public List<ProjectedAmortization> calculateMonthEndIncomeAmortizations(FinEODEvent finEODEvent) throws Exception {
		logger.debug(" Entering ");

		List<ProjectedAmortization> projIncomeAMZList = new ArrayList<ProjectedAmortization>();
		BigDecimal cumulativeAmount = BigDecimal.ZERO;
		BigDecimal unAmortizedAmount = BigDecimal.ZERO;

		List<ProjectedAmortization> projAMZList = finEODEvent.getProjectedAMZList();
		List<ProjectedAccrual> projAccrualList = finEODEvent.getProjectedAccrualList();

		for (ProjectedAmortization projAmortization : projAMZList) {

			BigDecimal totalAmount = calActualTotalAmount(projAmortization);

			for (ProjectedAccrual projAccrual : projAccrualList) {

				ProjectedAmortization projIncomeAMZ = new ProjectedAmortization();
				projIncomeAMZ.setFinReference(projAccrual.getFinReference());
				projIncomeAMZ.setFinType(projAccrual.getFinType());
				projIncomeAMZ.setMonthEndDate(projAccrual.getAccruedOn());
				projIncomeAMZ.setRefenceID(projAmortization.getRefenceID());
				projIncomeAMZ.setIncomeType(projAmortization.getIncomeType());

				// calculate income / expense amortization
				BigDecimal amortizedAmount = calculateMonthlyIncomeAMZ(projAmortization, projAccrual, totalAmount, cumulativeAmount);
				cumulativeAmount = cumulativeAmount.add(amortizedAmount);
				unAmortizedAmount = totalAmount.subtract(cumulativeAmount);

				projIncomeAMZ.setAmortizedAmount(amortizedAmount);
				projIncomeAMZ.setCumulativeAmount(cumulativeAmount);
				projIncomeAMZ.setUnAmortizedAmount(unAmortizedAmount);

				projIncomeAMZList.add(projIncomeAMZ);
			}
		}

		logger.debug(" Leaving ");
		return projIncomeAMZList;
	}

	/**
	 * calculate Monthly Amortization for Income and Expense based on AMZPerentage
	 * @param projAmortization
	 * @param projAccrual
	 * @return
	 */
	private BigDecimal calculateMonthlyIncomeAMZ(ProjectedAmortization projAmortization, ProjectedAccrual projAccrual,
			BigDecimal totalAmount, BigDecimal prvCummAMZ) {

		BigDecimal amortizedAmount = BigDecimal.ZERO;

		if (StringUtils.equals(projAmortization.getAMZMethod(), AmortizationConstants.AMZ_METHOD_STRAIGHTLINE)) {
			amortizedAmount = projAccrual.getAMZPercentage().multiply(totalAmount);
		} else {
			amortizedAmount = projAccrual.getAMZPercentage().multiply(totalAmount.subtract(prvCummAMZ));
		}
		return amortizedAmount;
	}

	/**
	 * Month End Income/Expense Amortization Calculation
	 * 
	 * @param finMain
	 * @param schdDetails
	 * @param appDate
	 * @return
	 */
	public FinEODEvent updateProjectedAMZAmounts(FinEODEvent finEODEvent, List<ProjectedAmortization> projIncomeAMZList) throws Exception {
		logger.debug(" Entering ");

		List<ProjectedAmortization> projAMZList = finEODEvent.getProjectedAMZList();
		Date appDate = DateUtility.getAppDate();
		ProjectedAmortization curProjAMZ = null;
		ProjectedAmortization prvProjAMZ = null;

		for (ProjectedAmortization projAmortization : projAMZList) { 
			for (int i = 0; i < projIncomeAMZList.size(); i++) {

				curProjAMZ = projIncomeAMZList.get(i);
				if (i == 0) {
					prvProjAMZ = curProjAMZ;
				} else {
					prvProjAMZ = projIncomeAMZList.get(i - 1);
				}

				if (curProjAMZ.getMonthEndDate().compareTo(appDate) == 0) {

					projAmortization.setPrvMonthAmz(prvProjAMZ.getAmortizedAmount());
					projAmortization.setCurMonthAmz(curProjAMZ.getAmortizedAmount());
					projAmortization.setAmortizedAmount(curProjAMZ.getCumulativeAmount());
					projAmortization.setUnAmortizedAmount(curProjAMZ.getUnAmortizedAmount());
				}
			}
		}

		logger.debug(" Leaving ");
		return finEODEvent;
	}

	/**
	 * 
	 * @param projAmortization
	 * @return
	 */
	private BigDecimal calActualTotalAmount(ProjectedAmortization projAmortization) {

		BigDecimal totalAmount = BigDecimal.ZERO;

		// for expense 100% amortized
		if (StringUtils.equals(projAmortization.getIncomeType(), AmortizationConstants.AMZ_INCOMETYPE_EXPENSE)) {
			totalAmount = projAmortization.getAmount();
		} else {
			// for income and manual advise percentage factor considered
			totalAmount = (projAmortization.getAmount().multiply(new BigDecimal(100)))
					.divide((new BigDecimal(100).add(projAmortization.getCalcFactor())), 2, RoundingMode.HALF_DOWN);
		}
		return totalAmount;
	}

	/**
	 * Method for calculate part of the profit, Rounding calculation not
	 * required.
	 * 
	 * @param schdDetail
	 * @param monthEnd
	 * @return
	 * 
	 */
	private static BigDecimal calProfitAMZ(FinanceScheduleDetail schdDetail, Date prvSchdDate, Date date1, Date date2) {

		BigDecimal pftAmz = BigDecimal.ZERO;

		int days = DateUtility.getDaysBetween(date1, date2);
		int daysInCurPeriod = DateUtility.getDaysBetween(schdDetail.getSchDate(), prvSchdDate);
		pftAmz = schdDetail.getProfitCalc().multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);

		return pftAmz;
	}

	/**
	 * Method for calculate part of the Principal OutStanding, Rounding
	 * calculation not required.
	 * 
	 * @param schdDetail
	 * @param monthEnd
	 * @return
	 * 
	 */
	private static BigDecimal calPOSAMZ(FinanceScheduleDetail schdDetail, Date prvSchdDate, Date date1,
			Date date2, BigDecimal prvClosingBal) {

		BigDecimal pftAmz = BigDecimal.ZERO;

		int days = DateUtility.getDaysBetween(date1, date2);
		int daysInCurPeriod = DateUtility.getDaysBetween(schdDetail.getSchDate(), prvSchdDate);
		pftAmz = prvClosingBal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 0, RoundingMode.HALF_DOWN);

		return pftAmz;
	}

	/**
	 * Method for calculate Cumulative Days.
	 * 
	 * @param schdDetail
	 * @param monthEnd
	 * @return
	 * 
	 */
	private static int getCumulativeDays(FinanceMain finMain, Date curMonthEnd) {

		int cumDays = 0;
		Date monthEnd = null;

		if (DateUtility.getMonthEndDate(getFormatDate(finMain.getMaturityDate())).compareTo(curMonthEnd) == 0) {
			monthEnd = finMain.getMaturityDate();
		} else {
			monthEnd = DateUtility.addDays(curMonthEnd, 1);
		}
		cumDays = DateUtility.getDaysBetween(monthEnd, finMain.getFinStartDate());
		return cumDays;
	}

	/**
	 * 
	 * @param totalProfit
	 * @param totalPOS
	 * @param finMain
	 * @param prjAcc
	 * @param cumulativePft
	 * @param cumulativePOS
	 * @return
	 */
	private static BigDecimal calAMZPercentage(BigDecimal totalProfit, BigDecimal totalPOS, FinanceMain finMain,
			ProjectedAccrual prjAcc, BigDecimal cumulativePft, BigDecimal cumulativePOS, String amzMethod) {

		BigDecimal amzPercentage = BigDecimal.ZERO;

		// income amortization methods
		switch (amzMethod) {

		case AmortizationConstants.AMZ_METHOD_INTEREST:
			amzPercentage = calPftAMZPercentage(totalProfit, prjAcc.getPftAmz(), cumulativePft);
			break;

		case AmortizationConstants.AMZ_METHOD_OPENINGPRIBAL:
			amzPercentage = calPriAMZPercentage(totalPOS, prjAcc.getPOSAccrued(), cumulativePOS);
			break;

		case AmortizationConstants.AMZ_METHOD_STRAIGHTLINE:
			amzPercentage = calDayAMZPercentage(finMain, prjAcc.getNoOfDays());
			break;

		default:
			break;
		}

		return amzPercentage;
	}

	/**
	 * Method for calculate Profit Amortization Percentage.
	 * 
	 * @param schdDetail
	 * @param monthEnd
	 * @return
	 * 
	 */
	private static BigDecimal calPftAMZPercentage(BigDecimal totalProfit, BigDecimal pftAccrued,
			BigDecimal prvCumProfit) {

		BigDecimal amzPercentage = (pftAccrued.multiply(new BigDecimal(100)))
				.divide((totalProfit.subtract(prvCumProfit)), 9, RoundingMode.HALF_DOWN);

		return amzPercentage;
	}

	/**
	 * Method for calculate Principal Amortization Percentage.
	 * 
	 * @param schdDetail
	 * @param monthEnd
	 * @return
	 * 
	 */
	private static BigDecimal calPriAMZPercentage(BigDecimal totalPOS, BigDecimal priAccrued,
			BigDecimal prvCumPrincipal) {

		BigDecimal amzPercentage = (priAccrued.multiply(new BigDecimal(100)))
				.divide((totalPOS.subtract(prvCumPrincipal)), 9, RoundingMode.HALF_DOWN);

		return amzPercentage;
	}

	/**
	 * Method for calculate Days Amortization Percentage.
	 * 
	 * @param schdDetail
	 * @param monthEnd
	 * @return
	 * 
	 */
	private static BigDecimal calDayAMZPercentage(FinanceMain finMain, int noOfDays) {

		int totalDays = DateUtility.getDaysBetween(finMain.getMaturityDate(), finMain.getFinStartDate());

		BigDecimal amzPercentage = (new BigDecimal(noOfDays).multiply(new BigDecimal(100)))
				.divide(new BigDecimal(totalDays), 9, RoundingMode.HALF_DOWN);

		return amzPercentage;
	}

	/**
	 * Method for calculate Cumulative Days.
	 * 
	 * @param schdDetail
	 * @param monthEnd
	 * @return
	 * 
	 */
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

	/**
	 * 
	 * @param date
	 * @return
	 */
	private static Date getFormatDate(Date date) {
		return DateUtility.getDBDate(DateUtility.formatDate(date, PennantConstants.DBDateFormat));
	}

	/**
	 * 
	 * @param financeMain
	 * @param financeType
	 * @return
	 */
	private HashMap<String, Object> getDataMap(FinanceMain finMain, FinanceType finType) {

		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		if (finMain != null) {
			dataMap.putAll(finMain.getDeclaredFieldValues());
		}
		if (finType != null) {
			dataMap.putAll(finType.getDeclaredFieldValues());
		}
		return dataMap;
	}

	/**
	 * 
	 * @param proAMZList
	 */
	private HashMap<String, List<ProjectedAmortization>> prepareAMZMap(List<ProjectedAmortization> proAMZList) {

		HashMap<String, List<ProjectedAmortization>> map = new HashMap<String, List<ProjectedAmortization>>(1);
		List<ProjectedAmortization> feeList = new ArrayList<ProjectedAmortization>(1);
		List<ProjectedAmortization> manualAdviseList = new ArrayList<ProjectedAmortization>(1);
		List<ProjectedAmortization> expenseList = new ArrayList<ProjectedAmortization>(1);

		for (ProjectedAmortization amortization : proAMZList) {

			if (map.containsKey(amortization.getIncomeType())) {
				map.get(amortization.getIncomeType()).add(amortization);
			} else {

				if (amortization.getIncomeType().equals(AmortizationConstants.AMZ_INCOMETYPE_FEE)) {

					feeList = new ArrayList<ProjectedAmortization>();
					feeList.add(amortization);
					map.put(amortization.getIncomeType(), feeList);

				} else if (amortization.getIncomeType().equals(AmortizationConstants.AMZ_INCOMETYPE_MANUALADVISE)) {

					manualAdviseList = new ArrayList<ProjectedAmortization>();
					manualAdviseList.add(amortization);
					map.put(amortization.getIncomeType(), manualAdviseList);
				} else {
					expenseList = new ArrayList<ProjectedAmortization>();
					expenseList.add(amortization);
					map.put(amortization.getIncomeType(), expenseList);
				}
			}
		}
		return map;
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

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}
}
