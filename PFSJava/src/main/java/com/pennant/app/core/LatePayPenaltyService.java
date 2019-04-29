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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;

public class LatePayPenaltyService extends ServiceHelper {

	private static final long serialVersionUID = 6161809223570900644L;
	private static Logger logger = Logger.getLogger(LatePayPenaltyService.class);

	/**
	 * Default constructor
	 */
	public LatePayPenaltyService() {
		super();
	}

	public void computeLPP(FinODDetails fod, Date valueDate, FinanceMain financeMain,
			List<FinanceScheduleDetail> finScheduleDetails, List<FinanceRepayments> repayments) {
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

			BigDecimal balanceForCal = getBalanceForCal(fod);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				int numberOfMonths = getMonthsBetween(fod, finScheduleDetails, valueDate);
				penalty = fod.getODChargeAmtOrPerc().multiply(new BigDecimal(numberOfMonths));
			}

			//Percentage ON OD Amount. One Time 
		} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(fod.getODChargeType())) {
			BigDecimal balanceForCal = getBalanceForCal(fod);

			//As same field is used to store both amount and percentage the value is stored in minor units without decimals
			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal amtOrPercetage = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100),
						RoundingMode.HALF_DOWN);
				penalty = balanceForCal.multiply(amtOrPercetage).divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			}

			//Percentage ON OD Amount. One Time 
		} else if (FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(fod.getODChargeType())) {
			BigDecimal balanceForCal = getBalanceForCal(fod);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				int numberOfMonths = getMonthsBetween(fod, finScheduleDetails, valueDate);
				BigDecimal amtOrPercetage = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100),
						RoundingMode.HALF_DOWN);
				penalty = balanceForCal.multiply(amtOrPercetage).multiply(new BigDecimal(numberOfMonths))
						.divide(new BigDecimal(100));
			}

			//On Due Days (or) Rule Fixed amount by Due Days
		} else {
			String finReference = fod.getFinReference();
			Date odDate = fod.getFinODSchdDate();
			if (repayments == null) {
				repayments = getFinanceRepaymentsDAO().getByFinRefAndSchdDate(finReference, odDate);
			}
			repayments = sortFinanceRepayments(repayments);
			prepareDueDateData(fod, valueDate, financeMain.getProfitDaysBasis(), repayments, financeMain);
			penalty = fod.getTotPenaltyAmt();
		}

		penalty = CalculationUtil.roundAmount(penalty, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());
		fod.setTotPenaltyAmt(penalty); // ### 03-12-2018 PSD Ticket ID: 130669

		/*
		 * fod.setTotPenaltyAmt(penalty);
		 * fod.setTotPenaltyBal(penalty.subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
		 */
		if (fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived())
				.compareTo(BigDecimal.ZERO) >= 0) {
			fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
			fod.setPayableAmount(BigDecimal.ZERO);
		} else {
			fod.setTotPenaltyBal(BigDecimal.ZERO);
			fod.setPayableAmount(fod.getTotPenaltyPaid().subtract(fod.getTotWaived()).subtract(fod.getTotPenaltyAmt()));
		}

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

	private void prepareDueDateData(FinODDetails fod, Date valueDate, String idb, List<FinanceRepayments> repayments,
			FinanceMain financeMain) {

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

			//check the payment made against the actual schedule date 
			if (repayment.getFinSchdDate().compareTo(odDate) != 0) {
				continue;
			}

			if (repayment.getFinValueDate().compareTo(valueDate) > 0) {
				continue;
			}

			if (repayment.getFinSchdDate().compareTo(repayment.getFinValueDate()) == 0) {
				continue;
			}

			Date grcDate = DateUtility.addDays(repayment.getFinSchdDate(), fod.getODGraceDays());

			//MAx OD amounts is same as repayments balance amounts
			if (repayment.getFinSchdDate().compareTo(repayment.getFinValueDate()) == 0
					|| DateUtility.compare(grcDate, repayment.getFinValueDate()) > 0) {
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

			BigDecimal penalty = BigDecimal.ZERO;
			// If charge calculation Type is Rule Fixed amount by Due Days
			if (FinanceConstants.PENALTYTYPE_RULEFXDD.equals(fod.getODChargeType())) {

				int dueDays = DateUtility.getDaysBetween(dateCur, dateNext);
				FinanceProfitDetail finPftDetails = getFinanceProfitDetailDAO().getFinProfitDetailsById(finReference);
				List<ExtendedField> extData = getExtendedFieldDetailsService().getExtndedFieldDetails(
						ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinCategory(),
						FinanceConstants.FINSER_EVENT_ORG, finReference);

				Map<String, Object> datamap = new HashMap<>();
				datamap.put("fm_finType", fod.getFinType()); // Finance Type
				datamap.put("fm_finBranch", fod.getFinBranch()); // Branch
				datamap.put("fin_ODDays", dueDays); // Due days
				datamap.put("fin_curODAmt", fod.getFinCurODAmt()); // Due Amount
				if (finPftDetails != null) {
					datamap.put("fpd_tdSchdPriBal", finPftDetails.getTdSchdPriBal()); // Principal Balance
					datamap.put("fpd_tdSchdPftBal", finPftDetails.getTdSchdPftBal()); // Profit Balance
				}
				datamap.put("fm_productCategory", financeMain.getProductCategory()); // Product Category
				datamap.put("ft_product", financeMain.getFinCategory()); // Product
				if (extData != null && !extData.isEmpty()) {
					for (ExtendedField extendedField : extData) {
						for (ExtendedFieldData extendedFieldData : extendedField.getExtendedFieldDataList()) {

							datamap.put("LOAN_LOC_STOREID", extendedFieldData.getFieldValue()); // Store ID
						}
					}
				}
				// Fetch Rule Query
				String sqlRule = getRuleDAO().getAmountRule(fod.getODRuleCode(), RuleConstants.MODULE_LPPRULE,
						RuleConstants.MODULE_LPPRULE);
				BigDecimal fixedAmt = BigDecimal.ZERO;
				if (StringUtils.isNotEmpty(sqlRule)) {

					fixedAmt = (BigDecimal) getRuleExecutionUtil().executeRule(sqlRule, datamap,
							SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY), RuleReturnType.DECIMAL);
				}
				if (fixedAmt.compareTo(BigDecimal.ZERO) > 0) {
					penalty = fixedAmt.multiply(new BigDecimal(dueDays));
				}
			} else {
				//Due Days accrual calculation
				BigDecimal penaltyRate = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
				penalty = CalculationUtil.calInterest(dateCur, dateNext, balanceForCal, idb, penaltyRate);
			}

			odcr.setPenalty(penalty);
			fod.setTotPenaltyAmt(fod.getTotPenaltyAmt().add(penalty));
		}

		fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
	}

	private int getMonthsBetween(FinODDetails fod, List<FinanceScheduleDetail> finScheduleDetails, Date valueDate) {
		int terms = 0;
		for (FinanceScheduleDetail finSchd : finScheduleDetails) {

			if (DateUtility.compare(finSchd.getSchDate(), fod.getFinODSchdDate()) < 0) {
				continue;
			}

			if (DateUtility.compare(finSchd.getSchDate(), valueDate) > 0) {
				break;
			}

			if ((finSchd.isRepayOnSchDate() || finSchd.isPftOnSchDate()) && finSchd.isFrqDate()) {
				terms++;
			}
		}

		return terms;

	}

	public List<FinanceRepayments> sortFinanceRepayments(List<FinanceRepayments> repayments) {

		if (repayments != null && repayments.size() > 0) {
			Collections.sort(repayments, new Comparator<FinanceRepayments>() {
				@Override
				public int compare(FinanceRepayments detail1, FinanceRepayments detail2) {
					return DateUtility.compare(detail1.getFinValueDate(), detail2.getFinValueDate());
				}
			});
		}

		return repayments;
	}

}
