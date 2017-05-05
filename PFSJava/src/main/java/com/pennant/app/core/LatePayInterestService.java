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

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;

public class LatePayInterestService extends ServiceHelper {

	private static final long		serialVersionUID	= 6161809223570900644L;
	private static Logger			logger				= Logger.getLogger(LatePayInterestService.class);
	private FinODDetailsDAO			finODDetailsDAO;
	private FinanceRepaymentsDAO	financeRepaymentsDAO;

	/**
	 * Default constructor
	 */
	public LatePayInterestService() {
		super();
	}

	public CustEODEvent processLatePayInterest(CustEODEvent custEODEvent) throws Exception {

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		for (FinEODEvent finEODEvent : finEODEvents) {
			if (!finEODEvent.isOdFiance()) {
				continue;
			}

			//No need to apply OD penalty 
			String lpiMethod = finEODEvent.getFinanceMain().getPastduePftCalMthd();
			if (StringUtils.equals(lpiMethod, CalculationConstants.PDPFTCAL_NOTAPP)) {
				continue;
			}

			finEODEvent = computeLPI(finEODEvent, valueDate);

		}
		return custEODEvent;

	}

	public FinEODEvent computeLPI(FinEODEvent finEODEvent, Date valueDate) throws Exception {
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
					&& fod.getLPIBal().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			//On Due Days
			finEODEvent = prepareDueDateData(finEODEvent, fod, valueDate);
			//updateLPPenaltInODDetails(fod);

		}

		logger.debug("Leaving");
		return finEODEvent;
	}

	public FinEODEvent prepareDueDateData(FinEODEvent finEODEvent, FinODDetails fod, Date valueDate) throws Exception {

		String finReference = finEODEvent.getFinanceMain().getFinReference();
		Date odDate = fod.getFinODSchdDate();
		BigDecimal lpiMargin = finEODEvent.getFinanceMain().getPastduePftMargin();

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
			odcr = new OverdueChargeRecovery();
			FinanceScheduleDetail curSchd = finSchdDetails.get(i);

			if (curSchd.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if (!curSchd.isPftOnSchDate() && !curSchd.isRepayOnSchDate()) {
				continue;
			}

			if (i == idx) {
				schdPri = curSchd.getPrincipalSchd();
				schdPft = curSchd.getProfitSchd();
			}

			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);
			odcr.setMovementDate(odDate);
			odcr.setPenaltyAmtPerc(curSchd.getCalculatedRate().add(lpiMargin));
			schdODCRecoveries.add(odcr);
		}

		List<FinanceRepayments> repayments = financeRepaymentsDAO.getByFinRefAndSchdDate(finReference, odDate);

		BigDecimal paidPri = BigDecimal.ZERO;
		BigDecimal paidPft = BigDecimal.ZERO;

		//Load Overdue Charge Recovery from Repayment Movements
		for (int i = 0; i < repayments.size(); i++) {
			FinanceRepayments repayment = repayments.get(i);
			paidPri = paidPri.add(repayment.getFinSchdPriPaid());
			paidPft = paidPft.add(repayment.getFinSchdPftPaid());
			BigDecimal penaltyRate = getPenaltyRate(finSchdDetails, repayment.getFinValueDate(), lpiMargin);
			schdODCRecoveries = addRepayToODCR(repayment, schdODCRecoveries, paidPri, paidPft, penaltyRate);
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
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);
			odcr.setMovementDate(valueDate);
			odcr.setCurPaidPri(paidPri);
			odcr.setCurPaidPft(paidPft);
			schdODCRecoveries.add(odcr);
		}

		fod.setLPIAmt(BigDecimal.ZERO);;
		String idb = finEODEvent.getFinanceMain().getProfitDaysBasis();
		//Calculate the Penalty
		for (int i = 0; i < schdODCRecoveries.size() - 1; i++) {
			OverdueChargeRecovery odcrCur = schdODCRecoveries.get(i);
			OverdueChargeRecovery odcrNext = schdODCRecoveries.get(i + 1);

			odcrCur.setFinCurODPri(schdPri.subtract(paidPri));
			odcrCur.setFinCurODPft(schdPft.subtract(paidPft));
			odcrCur.setFinCurODAmt(odcrCur.getFinCurODPri().add(odcrCur.getFinCurODPft()));

			//Calculate the Penalty
			BigDecimal balanceForCal = odcrCur.getFinCurODPri();

			//As same field is used to store both amount and percentage the value is stored in minor units without decimals
			Date dateCur = odcrCur.getMovementDate();
			Date dateNext = odcrNext.getMovementDate();

			BigDecimal penaltyRate = odcrCur.getPenaltyAmtPerc().divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			BigDecimal penalty = CalculationUtil.calInterest(dateCur, dateNext, balanceForCal, idb, penaltyRate);

			odcrCur.setPenalty(penalty);
			fod.setLPIAmt(fod.getLPIAmt().add(penalty));
		}

		fod.setLPIBal(fod.getLPIAmt().subtract(fod.getLPIPaid()).subtract(fod.getLPIWaived()));

		//Load all ODC Recovery events to the finEODEvent for DB Update at one time
		for (int i = 0; i < schdODCRecoveries.size(); i++) {
			odcr = schdODCRecoveries.get(i);
			finEODEvent.getOdcRecoveries().add(odcr);
		}

		return finEODEvent;
	}

	public List<OverdueChargeRecovery> addRepayToODCR(FinanceRepayments repayment,
			List<OverdueChargeRecovery> schdODCRecoveries, BigDecimal paidPri, BigDecimal paidPft, BigDecimal penaltyRate) throws Exception {
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
		odcr.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);
		odcr.setMovementDate(repayment.getFinValueDate());
		odcr.setPenaltyAmtPerc(penaltyRate);
		odcr.setCurPaidPri(paidPri);
		odcr.setCurPaidPft(paidPft);
		schdODCRecoveries.add(odcr);

		return schdODCRecoveries;

	}

	public BigDecimal getPenaltyRate(List<FinanceScheduleDetail> finSchdDetails, Date mvtDate, BigDecimal lpiMargin) throws Exception {
		BigDecimal penaltyRate = BigDecimal.ZERO;

		for (int i = 0; i < finSchdDetails.size(); i++) {
			if (finSchdDetails.get(i).getSchDate().compareTo(mvtDate)>0) {
				break;
			}
			
			penaltyRate = finSchdDetails.get(i).getCalculatedRate();
			
		}
		
		penaltyRate = penaltyRate.add(lpiMargin);
		return penaltyRate;
	}
	
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

}
