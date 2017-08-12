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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;

public class LatePayPenaltyService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(LatePayPenaltyService.class);

	/**
	 * Default constructor
	 */
	public LatePayPenaltyService() {
		super();
	}


	public void computeLPP(FinODDetails fod, Date valueDate, String idb,
			List<FinanceScheduleDetail> finScheduleDetails, List<FinanceRepayments> repayments, String roundingMode,
			int roundingTarget) {
		logger.debug("Entering");
		BigDecimal penalty = BigDecimal.ZERO;
		//Late Payment Penalty. Do not apply LPP
		if (!fod.isApplyODPenalty()) {
			return;
		}
		//Still before the grace days no need to calculate OD penalty
		if (fod.getFinCurODDays() <= fod.getODGraceDays()) {
			return;
		}

		//Fixed Fee. One Time 
		if (FinanceConstants.PENALTYTYPE_FLAT.equals(fod.getODChargeType())) {
			penalty = fod.getODChargeAmtOrPerc();

			//Fixed Fee. On Every Passing Schedule Month 
		} else if (FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(fod.getODChargeType())) {
			int numberOfMonths = getMonthsBetween(fod, finScheduleDetails, valueDate);
			penalty = fod.getODChargeAmtOrPerc().multiply(new BigDecimal(numberOfMonths));

			//Percentage ON OD Amount. One Time 
		} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(fod.getODChargeType())) {
			BigDecimal balanceForCal = getBalanceForCal(fod);

			//As same field is used to store both amount and percentage the value is stored in minor units without decimals
			BigDecimal amtOrPercetage = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			penalty = balanceForCal.multiply(amtOrPercetage).divide(new BigDecimal(100), RoundingMode.HALF_DOWN);

			//Percentage ON OD Amount. One Time 
		} else if (FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(fod.getODChargeType())) {
			BigDecimal balanceForCal = getBalanceForCal(fod);
			int numberOfMonths = getMonthsBetween(fod, finScheduleDetails, valueDate);
			BigDecimal amtOrPercetage = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			penalty = balanceForCal.multiply(amtOrPercetage).multiply(new BigDecimal(numberOfMonths))
					.divide(new BigDecimal(100));

			//On Due Days
		} else {
			String finReference = fod.getFinReference();
			Date odDate = fod.getFinODSchdDate();
			if (repayments == null) {
				repayments = getFinanceRepaymentsDAO().getByFinRefAndSchdDate(finReference, odDate);
			}
			prepareDueDateData(fod, valueDate, idb, repayments);
			penalty = fod.getTotPenaltyAmt();
		}

		penalty = CalculationUtil.roundAmount(penalty, roundingMode, roundingTarget);

		fod.setTotPenaltyAmt(penalty);
		fod.setTotPenaltyBal(penalty.subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));

		logger.debug("Leaving");
	}

	private BigDecimal getBalanceForCal(FinODDetails fod) {
		BigDecimal balanceForCal = BigDecimal.ZERO;

		if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPFT)) {
			balanceForCal = fod.getFinMaxODPft();
		} else if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPRI)) {
			balanceForCal = fod.getFinMaxODPri();
		} else {
			balanceForCal = fod.getFinMaxODAmt();
		}
		return balanceForCal;
	}

	private void prepareDueDateData(FinODDetails fod, Date valueDate, String idb, List<FinanceRepayments> repayments) {

		String finReference = fod.getFinReference();
		Date odDate = fod.getFinODSchdDate();

		BigDecimal odPri = fod.getFinMaxODPri();
		BigDecimal odPft = fod.getFinMaxODPft();

		List<OverdueChargeRecovery> schdODCRecoveries = new ArrayList<OverdueChargeRecovery>();
		OverdueChargeRecovery odcr = new OverdueChargeRecovery();

		//Add Schedule Date to the ODC Recovery
		odcr.setFinReference(finReference);
		odcr.setFinODSchdDate(odDate);
		odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		odcr.setMovementDate(odDate);
		odcr.setFinCurODPri(odPri);
		odcr.setFinCurODPft(odPft);
		odcr.setFinCurODAmt(odPri.add(odPft));
		schdODCRecoveries.add(odcr);

		if (repayments == null) {
			repayments = new ArrayList<FinanceRepayments>();
		}

		//Load Overdue Charge Recovery from Repayments Movements
		for (int i = 0; i < repayments.size(); i++) {
			FinanceRepayments repayment = repayments.get(i);

			//MAx OD amounts is same as repayments balance amounts
			if (repayment.getFinSchdDate().compareTo(odDate) != 0) {
				continue;
			}

			odPri = odPri.subtract(repayment.getFinSchdPriPaid());
			odPft = odPft.subtract(repayment.getFinSchdPftPaid());

			odcr = new OverdueChargeRecovery();
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(repayment.getFinValueDate());
			odcr.setFinCurODPri(odPri);
			odcr.setFinCurODPft(odPft);
			odcr.setFinCurODAmt(odPri.add(odPft));
			schdODCRecoveries.add(odcr);
		}

		fod.setTotPenaltyAmt(BigDecimal.ZERO);

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
			schdODCRecoveries.add(odcr);
		}

		//Calculate the Penalty
		for (int i = 0; i < schdODCRecoveries.size() - 1; i++) {
			OverdueChargeRecovery odcrCur = schdODCRecoveries.get(i);
			OverdueChargeRecovery odcrNext = schdODCRecoveries.get(i + 1);

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
	}

	private int getMonthsBetween(FinODDetails fod, List<FinanceScheduleDetail> finScheduleDetails, Date valueDate) {
		int terms = 0;
		for (FinanceScheduleDetail finSchd : finScheduleDetails) {

			if (DateUtility.compare(finSchd.getSchDate(),fod.getFinODSchdDate()) < 0) {
				continue;
			}

			if (DateUtility.compare(finSchd.getSchDate(),valueDate) > 0) {
				break;
			}

			if ((finSchd.isRepayOnSchDate() || finSchd.isPftOnSchDate()) && finSchd.isFrqDate()) {
				terms++;
			}
		}

		return terms;

	}

}
