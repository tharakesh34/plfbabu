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

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LatePayInterestService extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(LatePayInterestService.class);

	/**
	 * Default constructor
	 */
	public LatePayInterestService() {
		super();
	}

	public List<OverdueChargeRecovery> computeLPI(FinODDetails fod, Date valueDate, FinanceMain fm,
			List<FinanceScheduleDetail> schedules, List<FinanceRepayments> repayments) {
		long finID = fod.getFinID();
		String finReference = fod.getFinReference();
		logger.info(Literal.ENTERING);

		Date odDate = fod.getFinODSchdDate();
		BigDecimal lpiMargin = fm.getPastduePftMargin();

		BigDecimal odPri = fod.getFinMaxODPri();
		BigDecimal odPft = fod.getFinMaxODPft();
		BigDecimal penaltypaid = fod.getTotPenaltyPaid();
		fod.setLPIAmt(BigDecimal.ZERO);

		List<OverdueChargeRecovery> schdODCRecoveries = new ArrayList<>();
		OverdueChargeRecovery odcr = new OverdueChargeRecovery();

		// Add Schedule Date to the ODC Recovery
		odcr.setFinID(finID);
		odcr.setFinReference(finReference);
		odcr.setFinODSchdDate(odDate);
		odcr.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);
		odcr.setMovementDate(odDate);
		odcr.setFinCurODPri(odPri);
		odcr.setFinCurODPft(odPft);
		odcr.setFinCurODAmt(odPri.add(odPft));
		odcr.setPenaltyPaid(penaltypaid);
		odcr.setWaivedAmt(fod.getTotWaived());
		schdODCRecoveries.add(odcr);

		if (repayments == null) {
			repayments = financeRepaymentsDAO.getByFinRefAndSchdDate(finID, odDate);
		}

		// Load Overdue Charge Recovery from Repayment Movements
		for (FinanceRepayments repayment : repayments) {

			// check the payment made against the actual schedule date
			if (repayment.getFinSchdDate().compareTo(odDate) != 0) {
				continue;
			}

			// MAx OD amounts is same as repayment balance amounts
			if (repayment.getFinSchdDate().compareTo(repayment.getFinValueDate()) == 0) {
				continue;
			}

			odPri = odPri.subtract(repayment.getFinSchdPriPaid());
			odPft = odPft.subtract(repayment.getFinSchdPftPaid());

			odcr = new OverdueChargeRecovery();
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);
			odcr.setMovementDate(repayment.getFinValueDate());
			odcr.setFinCurODPri(odPri);
			odcr.setFinCurODPft(odPft);
			odcr.setFinCurODAmt(odPri.add(odPft));
			schdODCRecoveries.add(odcr);
		}
		BigDecimal odAmt = fod.getLPIAmt().add(fod.getTotPenaltyAmt());

		if (odAmt.compareTo(BigDecimal.ZERO) <= 0) {
			fod.setLPIAmt(BigDecimal.ZERO);
		}

		// Add record with today date
		boolean isAddTodayRcd = true;
		for (OverdueChargeRecovery item : schdODCRecoveries) {
			if (item.getMovementDate().compareTo(valueDate) == 0) {
				isAddTodayRcd = false;
				break;
			}
		}

		if (isAddTodayRcd) {
			odcr = new OverdueChargeRecovery();
			odcr.setFinID(finID);
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_LATEPAYPROFIT);
			odcr.setMovementDate(valueDate);
			schdODCRecoveries.add(odcr);
		}

		// Calculate the Penalty
		String roundingMode = fm.getCalRoundingMode();
		int roundingTarget = fm.getRoundingTarget();
		String profitDaysBasis = fm.getProfitDaysBasis();

		for (int i = 0; i < schdODCRecoveries.size() - 1; i++) {
			OverdueChargeRecovery odcrCur = schdODCRecoveries.get(i);
			OverdueChargeRecovery odcrNext = schdODCRecoveries.get(i + 1);

			Date dateCur = odcrCur.getMovementDate();
			Date dateNext = odcrNext.getMovementDate();

			BigDecimal penaltyRate = getPenaltyRate(schedules, dateCur, lpiMargin);

			BigDecimal penalty = CalculationUtil.calInterest(dateCur, dateNext, odcrCur.getFinCurODPri(),
					profitDaysBasis, penaltyRate);
			penalty = CalculationUtil.roundAmount(penalty, roundingMode, roundingTarget);

			odcrNext.setODDays(DateUtil.getDaysBetween(dateCur, dateNext));
			odcrNext.setPenaltyAmtPerc(penaltyRate);
			odcrNext.setPenalty(penalty);
			odcrNext.setPenaltyBal(
					odcrCur.getPenalty().subtract(odcrCur.getPenaltyPaid().subtract(odcrCur.getWaivedAmt())));

			odcrCur.setODDays(DateUtil.getDaysBetween(dateCur, dateNext));
			odcrCur.setPenaltyAmtPerc(penaltyRate);
			odcrCur.setPenalty(penalty);
			odcrCur.setPenaltyBal(
					odcrCur.getPenalty().subtract(odcrCur.getPenaltyPaid().subtract(odcrCur.getWaivedAmt())));
			fod.setLPIAmt(fod.getLPIAmt().add(penalty));

		}

		fod.setLPIAmt(CalculationUtil.roundAmount(fod.getLPIAmt(), roundingMode, roundingTarget));
		fod.setLPIBal(fod.getLPIAmt().subtract(fod.getLPIPaid()).subtract(fod.getLPIWaived()));

		// if the record added for calculation it should not be displayed in screen.
		if (isAddTodayRcd) {
			schdODCRecoveries.remove(schdODCRecoveries.size() - 1);
		}

		logger.info(Literal.LEAVING);
		return schdODCRecoveries;
	}

	public BigDecimal getPenaltyRate(List<FinanceScheduleDetail> schedules, Date mvtDate, BigDecimal lpiMargin) {

		BigDecimal penaltyRate = BigDecimal.ZERO;

		for (FinanceScheduleDetail schd : schedules) {
			if (schd.getSchDate().compareTo(mvtDate) > 0) {
				break;
			}

			penaltyRate = schd.getCalculatedRate();
		}
		penaltyRate = penaltyRate.add(lpiMargin);
		return penaltyRate;
	}

	public void postLPIAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) {
		List<FinOverDueCharges> saveList = new ArrayList<>();

		Date monthEndDate = custEODEvent.getEodDate();
		int dueDays = 0;
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinODDetails> odList = finEODEvent.getFinODDetails();

		for (FinODDetails fod : odList) {
			Date schdDate = fod.getFinODSchdDate();

			if ((fod.getFinCurODPri().add(fod.getFinCurODPft())).compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			List<FinOverDueCharges> odcList = finODCAmountDAO.getFinODCAmtByFinRef(fm.getFinID(), schdDate,
					RepayConstants.FEE_TYPE_LPI);
			BigDecimal totLPIAmt = fod.getLPIAmt();
			BigDecimal prvMnthLPIAmt = BigDecimal.ZERO;

			if (CollectionUtils.isEmpty(odcList)) {
				if (totLPIAmt.compareTo(BigDecimal.ZERO) > 0) {
					FinOverDueCharges finLPIAmount = createLPIAmounts(fod, totLPIAmt, monthEndDate);
					saveList.add(finLPIAmount);
				}
			} else {
				FinOverDueCharges prvFinLPIAmount = null;

				for (FinOverDueCharges odc : odcList) {
					Date postDate = odc.getPostDate();
					dueDays = DateUtil.getDaysBetween(postDate, monthEndDate);
					if (postDate.compareTo(monthEndDate) < 0) {
						prvMnthLPIAmt = prvMnthLPIAmt.add(odc.getAmount());
					} else if (postDate.compareTo(monthEndDate) == 0) {
						prvFinLPIAmount = odc;
					}
				}

				if (prvFinLPIAmount != null) {
					prvFinLPIAmount.setAmount(totLPIAmt.subtract(prvMnthLPIAmt));
					prvFinLPIAmount.setBalanceAmt(prvFinLPIAmount.getAmount().subtract(prvFinLPIAmount.getPaidAmount())
							.subtract(prvFinLPIAmount.getWaivedAmount()));
					prvFinLPIAmount.setDueDays(dueDays);
					saveList.add(prvFinLPIAmount);
				} else {
					FinOverDueCharges finODCAmount = createLPIAmounts(fod, totLPIAmt.subtract(prvMnthLPIAmt),
							monthEndDate);
					finODCAmount.setDueDays(dueDays);
					saveList.add(finODCAmount);
				}

			}
		}

		finEODEvent.getFinODCAmounts().addAll(saveList);
	}

	private FinOverDueCharges createLPIAmounts(FinODDetails od, BigDecimal lpi, Date monthEndDate) {
		FinOverDueCharges odc = new FinOverDueCharges();

		odc.setFinID(od.getFinID());
		odc.setSchDate(od.getFinODSchdDate());
		odc.setPostDate(monthEndDate);
		odc.setValueDate(monthEndDate);
		odc.setAmount(lpi);
		odc.setNewRecord(true);
		odc.setBalanceAmt(lpi);
		odc.setOdPri(od.getFinCurODPri());
		odc.setOdPft(od.getFinCurODPft());
		odc.setFinOdTillDate(od.getFinODTillDate());
		odc.setDueDays(DateUtil.getDaysBetween(od.getFinODSchdDate(), od.getFinODTillDate()));
		odc.setChargeType(RepayConstants.FEE_TYPE_LPI);

		return odc;
	}

}
