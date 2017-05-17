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
	 * Method for Processing Schedule calculation to set the Fee Scheduled based on the Schedule Fee method 
	 * 
	 * @param finScheduleData
	 * @return
	 */
	public static FinScheduleData feeSchdBuild(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		Map<Date, Integer> rpySchdMap = new HashMap<Date, Integer>();
		Map<Date, Integer> hldSchdMap = new HashMap<Date, Integer>();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		List<FinFeeDetail> feeDetails;
		Date evtFromDate = null;
		boolean isNewLoan = false;

		if (financeMain.isNew() || StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isNewLoan = true;
		}
		
		feeDetails = finScheduleData.getFinFeeDetailList();

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
			evtFromDate = financeMain.getFinStartDate();
		} else {
			if (evtFromDate == null) {
				evtFromDate = DateUtility.getAppDate();
			}

			if (evtFromDate.compareTo(DateUtility.getAppDate()) < 0) {
				evtFromDate = DateUtility.getAppDate();
			}
		}

		//Fill Repayments Schedules to Map to simplify the schedule search
		fillSchdMap(finScheduleData, evtFromDate, rpySchdMap, hldSchdMap);
		String feeScheduleMethod ;
		
		for (FinFeeDetail finFeeDetail : feeDetails) {

			if(finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) <= 0){
				continue;
			}

			//If Not Schedule Fee method Clear the Fee Schedule Details
			feeScheduleMethod = finFeeDetail.getFeeScheduleMethod();
			if(feeScheduleMethod.equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)
					|| feeScheduleMethod.equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {

				if(finFeeDetail.getFinFeeScheduleDetailList() != null){
					finFeeDetail.getFinFeeScheduleDetailList().clear();
				}
				continue;
			}

			if (isNewLoan) {
				prepareNewLoanSchd(financeMain, rpySchdMap, finFeeDetail);
			} else {
				prepareExistingLoanSchd(financeMain, rpySchdMap, finFeeDetail, evtFromDate);
			}

			calFeeSchd(financeMain, rpySchdMap, finFeeDetail, evtFromDate);
		}

		setFeeToSchd(finScheduleData, rpySchdMap, hldSchdMap);

		logger.debug("Leaving");

		return finScheduleData;
	}
	
	/**
	 * Calculate the recalculate Terms for new terms
	 * @param financemain
	 * @param rpySchdMap
	 * @param feeDetail
	 */
	private static void prepareNewLoanSchd(FinanceMain financemain, Map<Date, Integer> rpySchdMap, FinFeeDetail feeDetail) {

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
		
		feeScheduleDetails.clear();
		

		financemain.setRecalTerms(recalTerms);
		financemain.setRecalFee(recalFee);
	}

	/**
	 * 
	 * @param financeMain
	 * @param rpySchdMap
	 * @param feeIdx
	 * @param evtFromDate
	 */
	private static void prepareExistingLoanSchd(FinanceMain financeMain, Map<Date, Integer> rpySchdMap,
			FinFeeDetail finFeeDetail, Date evtFromDate) {
		logger.debug("Entering");


		List<FinFeeScheduleDetail> feeSchdDetails = finFeeDetail.getFinFeeScheduleDetailList();
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
				if (rpySchdList.get(i).compareTo(financeMain.getReqMaturity()) <= 0) {
					availableTerms = availableTerms + 1;
				}
			}
		}

		//Rare case if original maturity date is declared as holiday and recalculation triggers after last but one installment
		if (availableTerms == 0) {
			availableTerms = 1;
		}

		if (availableTerms < recalTerms
				|| StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
			recalTerms = availableTerms;
		}

		financeMain.setRecalTerms(recalTerms);
		financeMain.setRecalFee(recalFee);

		logger.debug("Leaving");

	}

	private static void calFeeSchd(FinanceMain financeMain, Map<Date, Integer> rpySchdMap, FinFeeDetail finFeeDetail,
			Date evtFromDate) {

		logger.debug("Entering");

		List<FinFeeScheduleDetail> feeSchdDetails = finFeeDetail.getFinFeeScheduleDetailList();
		FinFeeScheduleDetail feeSchdDetail;

		long feeID = finFeeDetail.getFeeID();
		int schTerms = 0;
		int recalTerms = financeMain.getRecalTerms();
		BigDecimal recalFee = financeMain.getRecalFee();

		BigDecimal totalNewSchdFee = BigDecimal.ZERO;
		BigDecimal newSchdFee = recalFee.divide(new BigDecimal(recalTerms), 0, RoundingMode.HALF_DOWN);
		newSchdFee = CalculationUtil.roundAmount(newSchdFee, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());

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

	private static void fillSchdMap(FinScheduleData finScheduleData, Date evtFromDate, Map<Date, Integer> rpySchdMap,
			Map<Date, Integer> hldSchdMap) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd;

		//Place Repayment Schedule dates and Holiday Schedule Dates to respective maps
		for (int i = 0; i < finSchdDetails.size(); i++) {
			curSchd = finSchdDetails.get(i);

			curSchd.setFeeSchd(BigDecimal.ZERO);							//This might cause issue  in servicing

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

	private static void setFeeToSchd(FinScheduleData finScheduleData, Map<Date, Integer> rpySchdMap,
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