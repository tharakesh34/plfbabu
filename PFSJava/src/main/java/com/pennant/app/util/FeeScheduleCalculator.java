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
 * FILE HEADER 																				*
 ******************************************************************************************** 
 * 
 * FileName : ScheduleCalculator.java 														*
 * 
 * Author : PENNANT TECHONOLOGIES 															*
 * 
 * Creation Date : 26-04-2011 																*
 * 
 * Modified Date : 30-07-2011 																*
 * 
 * Description : 																			*
 * 
 ******************************************************************************************** 
 * Date Author Version Comments 															*
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 																	*
 ******************************************************************************************** 
 */
package com.pennant.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;

public class FeeScheduleCalculator {
	private final static Logger	logger	= Logger.getLogger(FeeScheduleCalculator.class);

	private FeeScheduleCalculator() {
		super();
	}

	/**
	 * Method for Processing Schedule calculation to get the Total Desired Profit by including Planned Deferment Terms
	 * 
	 * @param finScheduleData
	 * @return
	 */
	public static FinScheduleData feeSchdBuild(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		Map<Date, Integer> rpySchdMap = new HashMap<Date, Integer>();
		Map<Date, Integer> hldSchdMap = new HashMap<Date, Integer>();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		Date evtFromDate = null;
		boolean isNewLoan = false;

		if (finMain.isNew() || StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isNewLoan = true;
		}

		List<FinFeeDetail> feeDetails = finScheduleData.getFinFeeDetailList();

		// No Fees available
		if (feeDetails == null || feeDetails.isEmpty()) {
			return finScheduleData;
		}

		// Schedule Detail not available
		if (finSchdDetails == null || finSchdDetails.isEmpty()) {
			return finScheduleData;
		}

		//Event From Date
		if (isNewLoan) {
			evtFromDate = finMain.getFinStartDate();
		} else {
			if (evtFromDate == null) {
				evtFromDate = DateUtility.getAppDate();
			}

			if (evtFromDate.compareTo(DateUtility.getAppDate()) < 0) {
				evtFromDate = DateUtility.getAppDate();
			}
		}

		//Fill Repayment Schedules to Map to simplify the schedule search
		fillSchdMap(finScheduleData, evtFromDate, rpySchdMap, hldSchdMap);

		for (int i = 0; i < feeDetails.size(); i++) {
			if (isNewLoan) {
				prepareNewLoanSchd(finScheduleData, rpySchdMap, i);
			} else {
				prepareExistingLoanSchd(finScheduleData, rpySchdMap, i, evtFromDate);
			}

			calFeeSchd(finScheduleData, rpySchdMap, i, evtFromDate);
		}

		setFeeToSchd(finScheduleData, rpySchdMap, hldSchdMap);

		logger.debug("Leaving");

		return finScheduleData;
	}

	public static void prepareNewLoanSchd(FinScheduleData finScheduleData, Map<Date, Integer> rpySchdMap, int feeIdx) {

		List<FinFeeDetail> feeDetails = finScheduleData.getFinFeeDetailList();
		FinFeeDetail feeDetail = feeDetails.get(feeIdx);
		List<FinFeeScheduleDetail> feeScheduleDetails = feeDetail.getFinFeeScheduleDetailList();

		int recalTerms = 0;
		BigDecimal recalFee = BigDecimal.ZERO;
		int avalableTerms = rpySchdMap.size();

		if (StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)) {
			recalTerms = 1;
		} else if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
				CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
			recalTerms = feeDetail.getTerms();

			if (avalableTerms < recalTerms) {
				recalTerms = avalableTerms;
			}
		} else {
			recalTerms = avalableTerms;
		}

		recalFee = feeDetail.getRemainingFee();

		//Reset Fee Schedules
		for (int i = 0; i < feeScheduleDetails.size(); i++) {
			feeScheduleDetails.remove(i);
			i = i - 1;
		}

		finScheduleData.getFinanceMain().setRecalTerms(recalTerms);
		finScheduleData.getFinanceMain().setRecalFee(recalFee);

	}

	public static void prepareExistingLoanSchd(FinScheduleData finScheduleData, Map<Date, Integer> rpySchdMap,
			int feeIdx, Date evtFromDate) {
		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();

		List<FinFeeDetail> feeDetails = finScheduleData.getFinFeeDetailList();
		List<FinFeeScheduleDetail> feeSchdDetails = feeDetails.get(feeIdx).getFinFeeScheduleDetailList();
		FinFeeScheduleDetail feeSchdDetail;

		BigDecimal totalSchdFee = BigDecimal.ZERO;
		int availableTerms = 0;
		int recalTerms = 0;
		BigDecimal recalFee = BigDecimal.ZERO;

		//Loop through all Fee schedules of a specific fee
		for (int i = 0; i < feeSchdDetails.size(); i++) {
			feeSchdDetail = feeSchdDetails.get(i);
			Date feeSchdDate = feeSchdDetail.getSchDate();

			totalSchdFee = totalSchdFee.add(feeSchdDetail.getSchAmount());

			//Fee Schedule date is before event from date
			if (feeSchdDate.compareTo(evtFromDate) < 0) {
				continue;
			}

			//Find O/S Fee exclusing written-off fee.
			BigDecimal osFee = feeSchdDetail.getSchAmount().subtract(feeSchdDetail.getPaidAmount())
					.subtract(feeSchdDetail.getWaiverAmount());

			//No Fees are due, so no postponement required
			if (osFee.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			//Fee paid partially or Fully cannot be rescheduled (Reason after recalculation if Paid becomes > calSchdFee it fails)
			if (feeSchdDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
				continue;
			}

			recalFee = recalFee.add(feeSchdDetail.getSchAmount());
			recalTerms = recalTerms + 1;
			feeSchdDetails.remove(i);
			i = i - 1;
		}

		//Find Total Available Repayment Schedules for Fee Recalculation
		List<Date> rpySchdList = new ArrayList<>(rpySchdMap.keySet());
		Collections.sort(rpySchdList);

		for (int i = 0; i < rpySchdList.size(); i++) {
			boolean isDateFound = false;

			for (int j = 0; j < feeSchdDetails.size(); j++) {
				if (feeSchdDetails.get(j).getSchDate().compareTo(rpySchdList.get(i)) == 0) {
					isDateFound = true;
				}

				if (feeSchdDetails.get(j).getSchDate().compareTo(rpySchdList.get(i)) > 0) {
					break;
				}
			}

			if (!isDateFound) {
				if (rpySchdList.get(i).compareTo(finMain.getReqMaturity()) <= 0) {
					availableTerms = availableTerms + 1;
				}
			}
		}

		//Rare case if original maturity date is declared as holiday and recalculation triggers after last but one installment
		if (availableTerms == 0) {
			availableTerms = 1;
		}

		if (availableTerms < recalTerms
				|| StringUtils.equals(feeDetails.get(feeIdx).getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
			recalTerms = availableTerms;
		}

		finScheduleData.getFinanceMain().setRecalTerms(recalTerms);
		finScheduleData.getFinanceMain().setRecalFee(recalFee);

		logger.debug("Leaving");

	}

	public static void calFeeSchd(FinScheduleData finScheduleData, Map<Date, Integer> rpySchdMap, int feeIdx,
			Date evtFromDate) {

		logger.debug("Entering");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinFeeDetail> feeDetails = finScheduleData.getFinFeeDetailList();
		List<FinFeeScheduleDetail> feeSchdDetails = feeDetails.get(feeIdx).getFinFeeScheduleDetailList();
		FinFeeScheduleDetail feeSchdDetail;

		long feeID = feeDetails.get(feeIdx).getFeeID();
		int schTerms = 0;
		int recalTerms = finMain.getRecalTerms();
		BigDecimal recalFee = finMain.getRecalFee();

		BigDecimal totalNewSchdFee = BigDecimal.ZERO;
		BigDecimal newSchdFee = recalFee.divide(new BigDecimal(recalTerms), 0, RoundingMode.HALF_DOWN);
		newSchdFee = CalculationUtil.roundAmount(newSchdFee, finMain.getCalRoundingMode(), finMain.getRoundingTarget());

		//Find Total Available Repayment Schedules for Fee Recalculation
		List<Date> rpySchdList = new ArrayList<>(rpySchdMap.keySet());
		Collections.sort(rpySchdList);

		for (int i = 0; i < rpySchdList.size(); i++) {
			Date schdDate = rpySchdList.get(i);
			boolean isDateFound = false;

			for (int j = 0; j < feeSchdDetails.size(); j++) {
				if (feeSchdDetails.get(j).getSchDate().compareTo(rpySchdList.get(i)) == 0) {
					isDateFound = true;
				}

				if (feeSchdDetails.get(j).getSchDate().compareTo(rpySchdList.get(i)) > 0) {
					break;
				}
			}

			if (isDateFound) {
				continue;
			}

			schTerms = schTerms + 1;

			if (recalTerms == schTerms) {
				newSchdFee = recalFee.subtract(totalNewSchdFee);
			}

			totalNewSchdFee = totalNewSchdFee.add(newSchdFee);
			feeSchdDetail = new FinFeeScheduleDetail();
			feeSchdDetail.setFeeID(feeID);
			feeSchdDetail.setSchDate(schdDate);
			feeSchdDetail.setSchAmount(newSchdFee);
			feeSchdDetails.add(feeSchdDetail);

			if (recalTerms == schTerms) {
				break;
			}

		}

		logger.debug("Leaving");

	}

	public static void fillSchdMap(FinScheduleData finScheduleData, Date evtFromDate, Map<Date, Integer> rpySchdMap,
			Map<Date, Integer> hldSchdMap) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd;

		//Place Repayment Schedule dates and Holiday Schedule Dates to respective maps
		for (int i = 0; i < finSchdDetails.size(); i++) {
			curSchd = finSchdDetails.get(i);

			curSchd.setFeeSchd(BigDecimal.ZERO);

			if (curSchd.getSchDate().before(evtFromDate)) {
				continue;
			}

			if (!curSchd.isRepayOnSchDate()) {
				continue;
			}

			if (i > 0) {
				if (finSchdDetails.get(i - 1).getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
			}

			if (StringUtils.isNotEmpty(curSchd.getBpiOrHoliday())) {
				hldSchdMap.put(curSchd.getSchDate(), i);
			} else {
				rpySchdMap.put(curSchd.getSchDate(), i);
			}

		}

		logger.debug("Leaving");

	}

	public static void setFeeToSchd(FinScheduleData finScheduleData, Map<Date, Integer> rpySchdMap,
			Map<Date, Integer> hldSchdMap) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		List<FinFeeDetail> feeDetails = finScheduleData.getFinFeeDetailList();
		FinFeeScheduleDetail feeSchdDetail;

		List<Date> rpySchdList = new ArrayList<>(rpySchdMap.keySet());
		Collections.sort(rpySchdList);

		//Loop through all Fees and set fees in installment schedules
		for (int i = 0; i < feeDetails.size(); i++) {
			List<FinFeeScheduleDetail> feeSchdDetails = feeDetails.get(i).getFinFeeScheduleDetailList();
			for (int j = 0; j < feeSchdDetails.size(); j++) {
				feeSchdDetail = feeSchdDetails.get(j);
				int schdIdx = rpySchdMap.get(feeSchdDetail.getSchDate());

				if (schdIdx <= 0) {
					schdIdx = hldSchdMap.get(feeSchdDetail.getSchDate());
				}

				FinanceScheduleDetail curSchd = finSchdDetails.get(schdIdx);
				curSchd.setFeeSchd(curSchd.getFeeSchd().add(feeSchdDetail.getSchAmount()));
			}
		}

		logger.debug("Leaving");
	}

}