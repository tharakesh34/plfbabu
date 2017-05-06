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
 * FileName : OverDueRecoveryPostingsUtil.java *
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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;

public class LatePayPenaltyService extends ServiceHelper {

	private static final long		serialVersionUID	= 6161809223570900644L;
	private static Logger			logger				= Logger.getLogger(LatePayPenaltyService.class);

	/**
	 * Default constructor
	 */
	public LatePayPenaltyService() {
		super();
	}

	public CustEODEvent processLatePayPenalty(CustEODEvent custEODEvent) throws Exception {

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			if (!finEODEvent.isOdFiance()) {
				continue;
			}

			finEODEvent = computeLPP(finEODEvent, valueDate);

		}
		return custEODEvent;

	}

	public FinEODEvent computeLPP(FinEODEvent finEODEvent, Date valueDate) throws Exception {
		logger.debug("Entering");

		List<FinODDetails> finODDetails = null;

		finODDetails = finEODEvent.getFinODDetails();

		//No OD Details found
		if (finODDetails == null || finODDetails.size() == 0) {
			return finEODEvent;
		}

		for (int i = 0; i < finODDetails.size(); i++) {
			FinODDetails fod = finODDetails.get(i);

			//OD Details found but OD cleared and No Penalty Balance
			if (fod.getFinCurODPri().compareTo(BigDecimal.ZERO) == 0
					&& fod.getFinCurODPft().compareTo(BigDecimal.ZERO) == 0
					&& fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			//No need to apply OD penalty 
			if (!fod.isApplyODPenalty()) {
				continue;
			}

			//Still before the grace days no need to calculate OD penalty
			if (fod.getFinCurODDays() <= fod.getODGraceDays()) {
				continue;
			}

			//Fixed Fee. One Time 
			if (FinanceConstants.PENALTYTYPE_FLAT.equals(fod.getODChargeType())) {
				fod.setTotPenaltyAmt(fod.getODChargeAmtOrPerc());
				fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid())
						.subtract(fod.getTotWaived()));

//				updateLPPenaltInODDetails(fod);
				continue;
			}

			//Fixed Fee. On Every Passing Schedule Month 
			if (FinanceConstants.PENALTYTYPE_FLATAMTONPASTDUEMTH.equals(fod.getODChargeType())) {
				int months = getNoOfSchdMonths(finEODEvent, fod.getFinODSchdDate(), valueDate);
				BigDecimal penalty = fod.getODChargeAmtOrPerc().multiply(new BigDecimal(months));
				fod.setTotPenaltyAmt(penalty);
				fod.setTotPenaltyBal(penalty.subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));

//				updateLPPenaltInODDetails(fod);
				continue;
			}

			//Percentage ON OD Amount. One Time 
			if (FinanceConstants.PENALTYTYPE_PERCONETIME.equals(fod.getODChargeType())) {
				BigDecimal balanceForCal = BigDecimal.ZERO;
				if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPFT)) {
					balanceForCal = fod.getFinMaxODPft();
				} else if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPRI)) {
					balanceForCal = fod.getFinMaxODPri();
				} else {
					balanceForCal = fod.getFinMaxODAmt();
				}

				//As same field is used to store both amount and percentage the value is stored in minor units without decimals
				BigDecimal amtOrPercetage = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100),
						RoundingMode.HALF_DOWN);
				BigDecimal penalty = balanceForCal.multiply(amtOrPercetage).divide(new BigDecimal(100),
						RoundingMode.HALF_DOWN);

				fod.setTotPenaltyAmt(penalty);
				fod.setTotPenaltyBal(penalty.subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));

//				updateLPPenaltInODDetails(fod);
				continue;
			}

			//Percentage ON OD Amount. One Time 
			if (FinanceConstants.PENALTYTYPE_PERCONDUEMTH.equals(fod.getODChargeType())) {
				finEODEvent = prepareMonthlyData(finEODEvent, fod, valueDate);

//				updateLPPenaltInODDetails(fod);
				continue;
			}
			//On Due Days
			finEODEvent = prepareDueDateData(finEODEvent, fod, valueDate);
//			updateLPPenaltInODDetails(fod);

		}

		logger.debug("Leaving");
		return finEODEvent;
	}

	public FinEODEvent prepareDueDateData(FinEODEvent finEODEvent, FinODDetails fod, Date valueDate) throws Exception {

		String finReference = finEODEvent.getFinanceMain().getFinReference();
		Date odDate = fod.getFinODSchdDate();

		BigDecimal schdPri = BigDecimal.ZERO;
		BigDecimal schdPft = BigDecimal.ZERO;

		List<OverdueChargeRecovery> schdODCRecoveries = new ArrayList<OverdueChargeRecovery>();
		OverdueChargeRecovery odcr = new OverdueChargeRecovery();

		//Add Schedule Date to the ODC Recovery
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();
		int idx = getIndexFromMap(datesMap, odDate);

		if (idx > 0) {
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(odDate);
			schdODCRecoveries.add(odcr);

			schdPri = finSchdDetails.get(idx).getPrincipalSchd();
			schdPft = finSchdDetails.get(idx).getProfitSchd();
		}

		List<FinanceRepayments> repayments = getFinanceRepaymentsDAO().getByFinRefAndSchdDate(finReference, odDate);

		BigDecimal paidPri = BigDecimal.ZERO;
		BigDecimal paidPft = BigDecimal.ZERO;

		//Load Overdue Charge Recovery from Repayment Movements
		for (int i = 0; i < repayments.size(); i++) {
			FinanceRepayments repayment = repayments.get(i);
			paidPri = paidPri.add(repayment.getFinSchdPriPaid());
			paidPft = paidPft.add(repayment.getFinSchdPftPaid());

			schdODCRecoveries = addRepayToODCR(repayment, schdODCRecoveries, paidPri, paidPft);
		}

		//Add record with today date
		boolean isAddTodayRcd = true;
		for (int i = 0; i < schdODCRecoveries.size(); i++) {
			odcr = schdODCRecoveries.get(i);
			if (odcr.getMovementDate().compareTo(valueDate) == 0) {
				isAddTodayRcd = false;
				break;
			}
		}

		if (isAddTodayRcd) {
			odcr = new OverdueChargeRecovery();
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(valueDate);
			odcr.setCurPaidPri(paidPri);
			odcr.setCurPaidPft(paidPft);
			schdODCRecoveries.add(odcr);
		}

		fod.setTotPenaltyAmt(BigDecimal.ZERO);
		String idb = finEODEvent.getFinanceMain().getProfitDaysBasis();
		//Calculate the Penalty
		for (int i = 0; i < schdODCRecoveries.size() - 1; i++) {
			OverdueChargeRecovery odcrCur = schdODCRecoveries.get(i);
			OverdueChargeRecovery odcrNext = schdODCRecoveries.get(i + 1);

			odcrCur.setFinCurODPri(schdPri.subtract(paidPri));
			odcrCur.setFinCurODPft(schdPft.subtract(paidPft));
			odcrCur.setFinCurODAmt(odcrCur.getFinCurODPri().add(odcrCur.getFinCurODPft()));

			//Calculate the Penalty
			BigDecimal balanceForCal = BigDecimal.ZERO;
			if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPFT)) {
				balanceForCal = odcrCur.getFinCurODPft();
			} else if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPRI)) {
				balanceForCal = odcrCur.getFinCurODPri();
			} else {
				balanceForCal = odcrCur.getFinCurODAmt();
			}

			//As same field is used to store both amount and percentage the value is stored in minor units without decimals
			Date dateCur = odcrCur.getMovementDate();
			Date dateNext = odcrNext.getMovementDate();

			BigDecimal penaltyRate = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			BigDecimal penalty = CalculationUtil.calInterest(dateCur, dateNext, balanceForCal, idb, penaltyRate);

			odcr.setPenalty(penalty);
			fod.setTotPenaltyAmt(fod.getTotPenaltyAmt().add(penalty));
		}

		fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));

		//Load all ODC Recovery events to the finEODEvent for DB Update at one time
		for (int i = 0; i < schdODCRecoveries.size(); i++) {
			odcr = schdODCRecoveries.get(i);
			finEODEvent.getOdcRecoveries().add(odcr);
		}

		return finEODEvent;
	}

	public FinEODEvent prepareMonthlyData(FinEODEvent finEODEvent, FinODDetails fod, Date valueDate) throws Exception {
		String finReference = finEODEvent.getFinanceMain().getFinReference();
		Date odDate = fod.getFinODSchdDate();

		List<OverdueChargeRecovery> schdODCRecoveries = new ArrayList<OverdueChargeRecovery>();
		OverdueChargeRecovery odcr = new OverdueChargeRecovery();

		//Add Schedule Date to the ODC Recovery
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();
		int idx = getIndexFromMap(datesMap, odDate);

		BigDecimal schdPri = BigDecimal.ZERO;
		BigDecimal schdPft = BigDecimal.ZERO;

		//Prepare schedule from OD Date to value date
		for (int i = idx; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);

			if (curSchd.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if (!curSchd.isPftOnSchDate() && curSchd.isRepayOnSchDate()) {
				continue;
			}

			if (i == idx) {
				schdPri = curSchd.getPrincipalSchd();
				schdPft = curSchd.getProfitSchd();
			}

			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(curSchd.getSchDate());
			schdODCRecoveries.add(odcr);
		}

		//Fetch Repayments for the OD Date
		List<FinanceRepayments> repayments = getFinanceRepaymentsDAO().getByFinRefAndSchdDate(finReference, odDate);
		fod.setTotPenaltyAmt(BigDecimal.ZERO);

		//Load OD balances on every movement date
		for (int i = 0; i < schdODCRecoveries.size(); i++) {
			odcr = schdODCRecoveries.get(i);

			if (repayments != null) {
				//Fill Paid amounts till now and return
				odcr = getODData(repayments, odcr);
			}

			//Get OD by deducting paid from scheduled
			odcr.setFinCurODPri(schdPri.subtract(odcr.getCurPaidPri()));
			odcr.setFinCurODPft(schdPft.subtract(odcr.getCurPaidPft()));
			odcr.setFinCurODAmt(odcr.getFinCurODPri().add(odcr.getFinCurODPft()));

			//Calculate the Penalty
			BigDecimal balanceForCal = BigDecimal.ZERO;
			if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPFT)) {
				balanceForCal = odcr.getFinCurODPft();
			} else if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPRI)) {
				balanceForCal = odcr.getFinCurODPri();
			} else {
				balanceForCal = odcr.getFinCurODAmt();
			}

			//As same field is used to store both amount and percentage the value is stored in minor units without decimals
			BigDecimal amtOrPercetage = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			BigDecimal penalty = balanceForCal.multiply(amtOrPercetage).divide(new BigDecimal(100),
					RoundingMode.HALF_DOWN);

			odcr.setPenalty(penalty);
			fod.setTotPenaltyAmt(fod.getTotPenaltyAmt().add(penalty));
		}

		fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));

		//Load all ODC Recovery events to the finEODEvent for DB Update at one time
		for (int i = 0; i < schdODCRecoveries.size(); i++) {
			odcr = schdODCRecoveries.get(i);
			finEODEvent.getOdcRecoveries().add(odcr);
		}

		return finEODEvent;
	}

	public int getNoOfSchdMonths(FinEODEvent finEODEvent, Date odDate, Date valueDate) throws Exception {
		int months = 0;

		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();
		int idx = getIndexFromMap(datesMap, odDate);

		for (int i = idx; i < finSchdDetails.size(); i++) {
			FinanceScheduleDetail finSchdDetail = finSchdDetails.get(i);
			if (finSchdDetail.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if (finSchdDetail.getSchDate().compareTo(odDate) >= 0) {
				if (finSchdDetail.isRepayOnSchDate() || finSchdDetail.isPftOnSchDate()) {
					months = months + 1;
				}
			}
		}

		return months;
	}

	public OverdueChargeRecovery getODData(List<FinanceRepayments> repayments, OverdueChargeRecovery odcr)
			throws Exception {
		Date mvtDate = odcr.getMovementDate();

		for (int i = 0; i < repayments.size(); i++) {
			FinanceRepayments repayment = repayments.get(i);

			if (repayment.getFinValueDate().compareTo(mvtDate) > 0) {
				break;
			}

			odcr.setCurPaidPri(odcr.getCurPaidPri().add(repayment.getFinSchdPriPaid()));
			odcr.setCurPaidPft(odcr.getCurPaidPft().add(repayment.getFinSchdPftPaid()));

		}

		return odcr;
	}

	public List<OverdueChargeRecovery> addRepayToODCR(FinanceRepayments repayment,
			List<OverdueChargeRecovery> schdODCRecoveries, BigDecimal paidPri, BigDecimal paidPft) throws Exception {
		Date rpyValueDate = repayment.getFinValueDate();

		for (int i = 0; i < schdODCRecoveries.size(); i++) {
			OverdueChargeRecovery odcr = schdODCRecoveries.get(i);
			if (rpyValueDate.compareTo(odcr.getMovementDate()) == 0) {
				odcr.setCurPaidPri(odcr.getCurPaidPri().add(paidPri));
				odcr.setCurPaidPft(odcr.getFinCurODPft().add(paidPft));
				schdODCRecoveries.set(i, odcr);
				return schdODCRecoveries;
			}
		}

		OverdueChargeRecovery odcr = new OverdueChargeRecovery();

		odcr.setFinReference(repayment.getFinReference());
		odcr.setFinODSchdDate(repayment.getFinSchdDate());
		odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		odcr.setMovementDate(repayment.getFinValueDate());
		odcr.setCurPaidPri(paidPri);
		odcr.setCurPaidPft(paidPft);
		schdODCRecoveries.add(odcr);

		return schdODCRecoveries;

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//


}
