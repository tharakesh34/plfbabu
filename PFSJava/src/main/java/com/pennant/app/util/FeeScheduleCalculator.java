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
 * 26-04-2011 Pennant 0.1 *
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FeeScheduleCalculator {
	private static final Logger logger = LogManager.getLogger(FeeScheduleCalculator.class);

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

		if (financeMain.isNewRecord()
				|| StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
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

		// Event From Date
		if (isNewLoan) {
			evtFromDate = financeMain.getFinStartDate();
		} else {
			evtFromDate = financeMain.getEventFromDate();

			Date appDate = SysParamUtil.getAppDate();
			if (evtFromDate == null) {
				evtFromDate = appDate;
			}

			if (evtFromDate.compareTo(appDate) < 0) {
				evtFromDate = appDate;
			}
		}

		// Fill Repayments Schedules to Map to simplify the schedule search
		Map<Date, Integer> avlSchdMap = fillSchdMap(finScheduleData, evtFromDate, rpySchdMap, hldSchdMap);
		String feeScheduleMethod;

		for (FinFeeDetail finFeeDetail : feeDetails) {

			if (finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) <= 0) {
				if (finFeeDetail.getFinFeeScheduleDetailList() != null) {
					finFeeDetail.getFinFeeScheduleDetailList().clear();
				}
				continue;
			}

			// If Not Schedule Fee method Clear the Fee Schedule Details
			feeScheduleMethod = finFeeDetail.getFeeScheduleMethod();
			if (!finFeeDetail.isOriginationFee()
					|| CalculationConstants.REMFEE_PART_OF_DISBURSE.equals(feeScheduleMethod)
					|| CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(feeScheduleMethod)
					|| CalculationConstants.FEE_SUBVENTION.equals(feeScheduleMethod)
					|| CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(feeScheduleMethod)) {

				if (finFeeDetail.getFinFeeScheduleDetailList() != null) {
					finFeeDetail.getFinFeeScheduleDetailList().clear();
				}
				continue;
			}

			if (isNewLoan) {
				prepareNewLoanSchd(financeMain, rpySchdMap, finFeeDetail);
			} else {
				prepareExistingLoanSchd(financeMain, rpySchdMap, avlSchdMap, finFeeDetail, evtFromDate);
			}

			calFeeSchd(financeMain, rpySchdMap, finFeeDetail, evtFromDate);
		}

		setFeeToSchd(finScheduleData, rpySchdMap, hldSchdMap);

		logger.debug("Leaving");

		return finScheduleData;
	}

	/**
	 * Calculate the recalculate Terms for new terms
	 * 
	 * @param financemain
	 * @param rpySchdMap
	 * @param feeDetail
	 */
	private static void prepareNewLoanSchd(FinanceMain financemain, Map<Date, Integer> rpySchdMap,
			FinFeeDetail feeDetail) {

		List<FinFeeScheduleDetail> feeScheduleDetails = feeDetail.getFinFeeScheduleDetailList();

		int recalTerms = 0;
		BigDecimal recalFee = BigDecimal.ZERO;
		// GST fields

		int avalableTerms = rpySchdMap.size();

		String feeScheduleMethod = feeDetail.getFeeScheduleMethod();
		if (StringUtils.equals(feeScheduleMethod, CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)) {
			recalTerms = 1;
		} else if (StringUtils.equals(feeScheduleMethod, CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
			recalTerms = feeDetail.getTerms();
			if (avalableTerms < recalTerms) {
				recalTerms = avalableTerms;
				// throw new WrongValueException("Number of Terms not matching the Available terms ");
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
			Map<Date, Integer> avlSchdMap, FinFeeDetail finFeeDetail, Date evtFromDate) {
		logger.debug("Entering");

		List<FinFeeScheduleDetail> feeSchdDetails = finFeeDetail.getFinFeeScheduleDetailList();
		FinFeeScheduleDetail feeSchdDetail;

		BigDecimal totalSchdFee = BigDecimal.ZERO;
		int availableTerms = 0;
		int recalTerms = 0;
		BigDecimal recalFee = BigDecimal.ZERO;
		// GST Fields
		BigDecimal recalCGSTFee = BigDecimal.ZERO;
		BigDecimal recalIGSTFee = BigDecimal.ZERO;
		BigDecimal recalSGSTFee = BigDecimal.ZERO;
		BigDecimal recalUGSTFee = BigDecimal.ZERO;

		// Loop through all Fee schedules of a specific fee
		for (int i = 0; i < feeSchdDetails.size(); i++) {
			feeSchdDetail = feeSchdDetails.get(i);
			Date feeSchdDate = feeSchdDetail.getSchDate();

			totalSchdFee = totalSchdFee.add(feeSchdDetail.getSchAmount());

			// Fee Schedule date is before event from date
			if (feeSchdDate.compareTo(evtFromDate) < 0) {
				feeSchdDate = DateUtil.getDatePart(feeSchdDate);
				if (!avlSchdMap.containsKey(feeSchdDate)) {
					recalFee = recalFee.add(feeSchdDetail.getSchAmount());
					feeSchdDetails.remove(i);
					i = i - 1;
				}
				continue;
			}

			// Find O/S Fee excluding written-off fee.
			BigDecimal osFee = feeSchdDetail.getSchAmount().subtract(feeSchdDetail.getPaidAmount())
					.subtract(feeSchdDetail.getWaiverAmount());

			// No Fees are due, so no postpone required
			if (osFee.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			// Fee paid partially or Fully cannot be rescheduled (Reason after recalculation if Paid becomes >
			// calSchdFee it fails)
			if (feeSchdDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
				continue;
			}

			recalFee = recalFee.add(feeSchdDetail.getSchAmount());

			// GST fields
			recalCGSTFee = recalCGSTFee.add(feeSchdDetail.getCGST());
			recalIGSTFee = recalIGSTFee.add(feeSchdDetail.getIGST());
			recalSGSTFee = recalSGSTFee.add(feeSchdDetail.getSGST());
			recalUGSTFee = recalUGSTFee.add(feeSchdDetail.getUGST());

			recalTerms = recalTerms + 1;
			feeSchdDetails.remove(i);
			i = i - 1;
		}

		// Find Total Available Repayment Schedules for Fee Recalculation
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

		// Rare case if original maturity date is declared as holiday and recalculation triggers after last but one
		// installment
		if (availableTerms == 0) {
			availableTerms = 1;
		}

		if (availableTerms < recalTerms || StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
				CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
			recalTerms = availableTerms;
		}

		financeMain.setRecalTerms(recalTerms);
		financeMain.setRecalFee(recalFee);

		// GST fields
		financeMain.setRecalCGSTFee(recalCGSTFee);
		financeMain.setRecalIGSTFee(recalIGSTFee);
		financeMain.setRecalSGSTFee(recalSGSTFee);
		financeMain.setRecalUGSTFee(recalUGSTFee);

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

		if (recalTerms == 0) {
			return;
		}

		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		BigDecimal recalFee = financeMain.getRecalFee();

		TaxHeader taxHeader = finFeeDetail.getTaxHeader();
		Taxes cgstTax = new Taxes();
		Taxes sgstTax = new Taxes();
		Taxes igstTax = new Taxes();
		Taxes ugstTax = new Taxes();
		Taxes cessTax = new Taxes();

		if (taxHeader != null) {
			List<Taxes> taxDetails = taxHeader.getTaxDetails();
			if (CollectionUtils.isNotEmpty(taxDetails)) {
				for (Taxes taxes : taxDetails) {
					if (StringUtils.equals(RuleConstants.CODE_CGST, taxes.getTaxType())) {
						cgstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_SGST, taxes.getTaxType())) {
						sgstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_IGST, taxes.getTaxType())) {
						igstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_UGST, taxes.getTaxType())) {
						ugstTax = taxes;
					} else if (StringUtils.equals(RuleConstants.CODE_CESS, taxes.getTaxType())) {
						cessTax = taxes;
					}
				}
			}
		}

		BigDecimal totalNewSchdFee = BigDecimal.ZERO;

		// GST fields
		BigDecimal totalcgstAmount = BigDecimal.ZERO;
		BigDecimal totaligstAmount = BigDecimal.ZERO;
		BigDecimal totalsgstAmount = BigDecimal.ZERO;
		BigDecimal totalugstAmount = BigDecimal.ZERO;
		BigDecimal totalcessAmount = BigDecimal.ZERO;
		BigDecimal totaloriginalSchFee = BigDecimal.ZERO;

		BigDecimal newSchdFee = recalFee.divide(new BigDecimal(recalTerms), 0, RoundingMode.HALF_DOWN);
		newSchdFee = CalculationUtil.roundAmount(newSchdFee, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());

		BigDecimal tgstPercent = cgstTax.getTaxPerc().add(sgstTax.getTaxPerc()).add(igstTax.getTaxPerc())
				.add(ugstTax.getTaxPerc()).add(cessTax.getTaxPerc());

		// Find Total Available Repayment Schedules for Fee Recalculation
		List<Date> rpySchdList = new ArrayList<>(rpySchdMap.keySet());
		Collections.sort(rpySchdList);
		boolean totFeeAdjusted = false;

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

			if (totalNewSchdFee.add(newSchdFee).compareTo(recalFee) > 0) {
				totFeeAdjusted = true;
				newSchdFee = recalFee.subtract(totalNewSchdFee);
			}

			if (recalTerms == schTerms) {
				totFeeAdjusted = true;
				newSchdFee = recalFee.subtract(totalNewSchdFee);
			}

			// GST code
			BigDecimal cgstAmount = BigDecimal.ZERO;
			BigDecimal igstAmount = BigDecimal.ZERO;
			BigDecimal sgstAmount = BigDecimal.ZERO;
			BigDecimal ugstAmount = BigDecimal.ZERO;
			BigDecimal cessAmount = BigDecimal.ZERO;
			BigDecimal originalSchFee = BigDecimal.ZERO;

			if (finFeeDetail.isTaxApplicable()) {
				if (recalTerms == schTerms) {
					cgstAmount = cgstTax.getRemFeeTax().subtract(totalcgstAmount);
					cgstAmount = CalculationUtil.roundAmount(cgstAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());

					igstAmount = igstTax.getRemFeeTax().subtract(totaligstAmount);
					igstAmount = CalculationUtil.roundAmount(igstAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());

					sgstAmount = sgstTax.getRemFeeTax().subtract(totalsgstAmount);
					sgstAmount = CalculationUtil.roundAmount(sgstAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());

					ugstAmount = ugstTax.getRemFeeTax().subtract(totalugstAmount);
					ugstAmount = CalculationUtil.roundAmount(ugstAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());

					cessAmount = cgstTax.getRemFeeTax().subtract(totalcessAmount);
					cessAmount = CalculationUtil.roundAmount(cessAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());

				} else {
					originalSchFee = calculateInclusivePercentage(newSchdFee, tgstPercent, formatter);

					cgstAmount = originalSchFee.multiply(
							(cgstTax.getTaxPerc()).divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN));
					cgstAmount = CalculationUtil.roundAmount(cgstAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());

					totalcgstAmount = totalcgstAmount.add(cgstAmount);

					igstAmount = originalSchFee.multiply(
							(igstTax.getTaxPerc()).divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN));
					igstAmount = CalculationUtil.roundAmount(igstAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());

					totaligstAmount = totaligstAmount.add(igstAmount);

					sgstAmount = originalSchFee.multiply(
							(sgstTax.getTaxPerc()).divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN));
					sgstAmount = CalculationUtil.roundAmount(sgstAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());

					totalsgstAmount = totalsgstAmount.add(sgstAmount);

					ugstAmount = originalSchFee.multiply(
							(ugstTax.getTaxPerc()).divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN));
					ugstAmount = CalculationUtil.roundAmount(ugstAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());
					totalugstAmount = totalugstAmount.add(ugstAmount);

					cessAmount = originalSchFee.multiply(
							(cessTax.getTaxPerc()).divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN));
					cessAmount = CalculationUtil.roundAmount(cessAmount, financeMain.getCalRoundingMode(),
							financeMain.getRoundingTarget());
					totalcessAmount = totalcessAmount.add(cessAmount);

				}
			}
			totaloriginalSchFee = totaloriginalSchFee.add(originalSchFee);

			totalNewSchdFee = totalNewSchdFee.add(newSchdFee);

			feeSchdDetail = new FinFeeScheduleDetail();
			feeSchdDetail.setFeeID(feeID);
			feeSchdDetail.setSchDate(schdDate);
			feeSchdDetail.setSchAmount(newSchdFee);

			// GST fields
			feeSchdDetail.setCGST(cgstAmount);
			feeSchdDetail.setIGST(igstAmount);
			feeSchdDetail.setSGST(sgstAmount);
			feeSchdDetail.setUGST(ugstAmount);
			feeSchdDetail.setTGST(cgstAmount.add(igstAmount).add(sgstAmount).add(ugstAmount));

			feeSchdDetails.add(feeSchdDetail);

			if (totFeeAdjusted) {
				break;
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * calculate the inclusive percentage used in GST
	 * 
	 * @param amount
	 * @param gstPercentage
	 * @param formatter
	 * @return
	 */
	private static BigDecimal calculateInclusivePercentage(BigDecimal amount, BigDecimal gstPercentage, int formatter) {

		// adding 100
		BigDecimal percentage = gstPercentage.add(new BigDecimal(100));

		// divide by 100
		percentage = percentage.divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN);

		return amount.divide(percentage, formatter, RoundingMode.HALF_DOWN);
	}

	private static Map<Date, Integer> fillSchdMap(FinScheduleData finScheduleData, Date evtFromDate,
			Map<Date, Integer> rpySchdMap, Map<Date, Integer> hldSchdMap) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd;
		Map<Date, Integer> avlSchdMap = new HashMap<>();

		// Place Repayment Schedule dates and Holiday Schedule Dates to respective maps
		for (int i = 0; i < finSchdDetails.size(); i++) {
			curSchd = finSchdDetails.get(i);

			if (curSchd.getSchDate().before(evtFromDate)) {
				avlSchdMap.put(curSchd.getSchDate(), i);
				continue;
			}
			curSchd.setFeeSchd(BigDecimal.ZERO); // This might cause issue in servicing
			curSchd.setFeeTax(BigDecimal.ZERO); // GST Fee Tax

			if (!curSchd.isRepayOnSchDate() && !curSchd.isPftOnSchDate()) {
				continue;
			}

			if (curSchd.getPartialPaidAmt().equals(curSchd.getRepayAmount())) {
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

		return avlSchdMap;
	}

	private static void setFeeToSchd(FinScheduleData finScheduleData, Map<Date, Integer> rpySchdMap,
			Map<Date, Integer> hldSchdMap) {
		logger.debug("Entering");

		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		List<FinFeeDetail> feeDetails = finScheduleData.getFinFeeDetailList();
		FinFeeScheduleDetail feeSchdDetail;

		List<Date> rpySchdList = new ArrayList<>(rpySchdMap.keySet());
		Collections.sort(rpySchdList);

		// Loop through all Fees and set fees in installment schedules
		for (int i = 0; i < feeDetails.size(); i++) {
			List<FinFeeScheduleDetail> feeSchdDetails = feeDetails.get(i).getFinFeeScheduleDetailList();
			if (CollectionUtils.isNotEmpty(feeSchdDetails)) {
				for (int j = 0; j < feeSchdDetails.size(); j++) {
					feeSchdDetail = feeSchdDetails.get(j);
					if (!rpySchdMap.containsKey(feeSchdDetail.getSchDate())) {
						continue;
					}

					int schdIdx = rpySchdMap.get(feeSchdDetail.getSchDate());

					if (schdIdx <= 0) {
						schdIdx = hldSchdMap.get(feeSchdDetail.getSchDate());
					}

					FinanceScheduleDetail curSchd = finSchdDetails.get(schdIdx);
					curSchd.setFeeSchd(curSchd.getFeeSchd().add(feeSchdDetail.getSchAmount()));
					curSchd.setFeeTax(curSchd.getFeeTax().add(feeSchdDetail.getTGST())); // GST Fee Tax
				}
			}

			logger.debug("Leaving");
		}
	}
}