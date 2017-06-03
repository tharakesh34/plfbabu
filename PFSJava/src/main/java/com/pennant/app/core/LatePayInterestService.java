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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;

public class LatePayInterestService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(LatePayInterestService.class);

	/**
	 * Default constructor
	 */
	public LatePayInterestService() {
		super();
	}

	public void computeLPI(FinEODEvent finEODEvent, FinODDetails fod, Date valueDate) throws Exception {
		logger.debug(" Entering ");

		String finReference = fod.getFinReference();
		Date odDate = fod.getFinODSchdDate();
		String idb = finEODEvent.getFinanceMain().getProfitDaysBasis();
		BigDecimal lpiMargin = finEODEvent.getFinanceMain().getPastduePftMargin().divide(new BigDecimal(100));
		String roundingMode = finEODEvent.getFinanceMain().getCalRoundingMode();
		int roundingTarget = finEODEvent.getFinanceMain().getRoundingTarget();

		BigDecimal odPri = fod.getFinMaxODPri();
		BigDecimal odPft = fod.getFinMaxODPft();

		List<OverdueChargeRecovery> schdODCRecoveries = new ArrayList<OverdueChargeRecovery>();
		OverdueChargeRecovery odcr = new OverdueChargeRecovery();

		//Add Schedule Date to the ODC Recovery
		odcr.setFinReference(finReference);
		odcr.setFinODSchdDate(odDate);
		odcr.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);
		odcr.setMovementDate(odDate);
		odcr.setFinCurODPri(odPri);
		odcr.setFinCurODPft(odPft);
		odcr.setFinCurODAmt(odPri.add(odPft));
		schdODCRecoveries.add(odcr);

		List<FinanceRepayments> repayments = getFinanceRepaymentsDAO().getByFinRefAndSchdDate(finReference, odDate);

		//Load Overdue Charge Recovery from Repayment Movements
		for (int i = 0; i < repayments.size(); i++) {
			FinanceRepayments repayment = repayments.get(i);

			//MAx OD amounts is same as repayment balance amounts
			if (repayment.getFinSchdDate().compareTo(repayment.getFinValueDate()) == 0) {
				continue;
			}

			odPri = odPri.subtract(repayment.getFinSchdPriPaid());
			odPft = odPri.subtract(repayment.getFinSchdPftPaid());

			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);
			odcr.setMovementDate(repayment.getFinValueDate());
			odcr.setFinCurODPri(odPri);
			odcr.setFinCurODPft(odPft);
			odcr.setFinCurODAmt(odPri.add(odPft));
			schdODCRecoveries.add(odcr);
		}

		fod.setLPIAmt(BigDecimal.ZERO);

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
			schdODCRecoveries.add(odcr);
		}

		//Calculate the Penalty
		for (int i = 0; i < schdODCRecoveries.size() - 1; i++) {
			OverdueChargeRecovery odcrCur = schdODCRecoveries.get(i);
			OverdueChargeRecovery odcrNext = schdODCRecoveries.get(i + 1);

			Date dateCur = odcrCur.getMovementDate();
			Date dateNext = odcrNext.getMovementDate();

			BigDecimal penaltyRate = getPenaltyRate(finEODEvent.getFinanceScheduleDetails(), dateCur, lpiMargin);
			BigDecimal penalty = CalculationUtil.calInterest(dateCur, dateNext, odcrCur.getFinCurODPri(), idb,
					penaltyRate);

			odcr.setPenalty(penalty);
			fod.setLPIAmt(fod.getLPIAmt().add(penalty));
		}
		
		fod.setLPIAmt(CalculationUtil.roundAmount(fod.getLPIAmt(), roundingMode, roundingTarget));
		fod.setLPIBal(fod.getLPIAmt().subtract(fod.getLPIPaid()).subtract(fod.getLPIWaived()));
		logger.debug(" Leaving ");
	}

	public BigDecimal getPenaltyRate(List<FinanceScheduleDetail> finSchdDetails, Date mvtDate, BigDecimal lpiMargin)
			throws Exception {

		BigDecimal penaltyRate = BigDecimal.ZERO;

		for (int i = 0; i < finSchdDetails.size(); i++) {
			if (finSchdDetails.get(i).getSchDate().compareTo(mvtDate) > 0) {
				break;
			}

			penaltyRate = finSchdDetails.get(i).getCalculatedRate();

		}

		penaltyRate = penaltyRate.add(lpiMargin);
		return penaltyRate;
	}

}
