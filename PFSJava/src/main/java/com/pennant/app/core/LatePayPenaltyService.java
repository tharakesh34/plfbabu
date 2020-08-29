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

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
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
			List<FinanceScheduleDetail> fsdList, List<FinanceRepayments> rpdList) {
		logger.debug("Entering");
		BigDecimal penalty = BigDecimal.ZERO;

		// Late Payment Penalty. Do not apply LPP
		if (!fod.isApplyODPenalty()) {
			// #PSD 137379
			fod.setTotPenaltyAmt(penalty);
			fod.setTotPenaltyBal(penalty);

			return;
		}

		// Still before the grace days no need to calculate OD penalty
		if (fod.getFinCurODDays() <= fod.getODGraceDays()) {
			// #PSD 137379
			fod.setTotPenaltyAmt(penalty);
			fod.setTotPenaltyBal(penalty);
			return;
		}

		// Fixed Fee. One Time
		if (FinanceConstants.PENALTYTYPE_FLAT.equals(fod.getODChargeType())) {
			penalty = fod.getODChargeAmtOrPerc();

			// Fixed Fee. On Every Passing Schedule Month
		} else if (FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(fod.getODChargeType())) {

			BigDecimal balanceForCal = getBalanceForCal(fod);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				int numberOfMonths = getMonthsBetween(fod, fsdList, valueDate);
				penalty = fod.getODChargeAmtOrPerc().multiply(new BigDecimal(numberOfMonths));
			}

			// Percentage ON OD Amount. One Time
		} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(fod.getODChargeType())) {
			BigDecimal balanceForCal = getBalanceForCal(fod);

			// As same field is used to store both amount and percentage the
			// value is stored in minor units without decimals
			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal amtOrPercetage = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100),
						RoundingMode.HALF_DOWN);
				penalty = balanceForCal.multiply(amtOrPercetage).divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			}

			// Percentage ON OD Amount. One Time
		} else if (FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(fod.getODChargeType())) {
			BigDecimal balanceForCal = getBalanceForCal(fod);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				int numberOfMonths = getMonthsBetween(fod, fsdList, valueDate);
				BigDecimal amtOrPercetage = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100),
						RoundingMode.HALF_DOWN);
				penalty = balanceForCal.multiply(amtOrPercetage).multiply(new BigDecimal(numberOfMonths))
						.divide(new BigDecimal(100));
			}

			// On Due Days (or) Rule Fixed amount by Due Days
		} else {
			String finReference = fod.getFinReference();
			Date odDate = fod.getFinODSchdDate();
			if (rpdList == null) {
				rpdList = getFinanceRepaymentsDAO().getByFinRefAndSchdDate(finReference, odDate);
			}

			String pftDaysBasis = financeMain.getProfitDaysBasis();
			if (StringUtils.equals(pftDaysBasis, CalculationConstants.IDB_BY_PERIOD)) {
				pftDaysBasis = CalculationConstants.IDB_ACT_365LEAP;
			}

			rpdList = sortRpdListByValueDate(rpdList);
			prepareDueDateData(fod, valueDate, pftDaysBasis, rpdList, financeMain, fsdList);
			penalty = fod.getTotPenaltyAmt();
		}

		penalty = CalculationUtil.roundAmount(penalty, financeMain.getCalRoundingMode(),
				financeMain.getRoundingTarget());
		fod.setTotPenaltyAmt(penalty); // ### 03-12-2018 PSD Ticket ID: 130669
		fod.setFinODTillDate(valueDate);

		/*
		 * fod.setTotPenaltyAmt(penalty); fod.setTotPenaltyBal(penalty.subtract(fod.getTotPenaltyPaid()).
		 * subtract(fod.getTotWaived()));
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

	private void prepareDueDateData(FinODDetails fod, Date valueDate, String idb, List<FinanceRepayments> rpdList,
			FinanceMain financeMain, List<FinanceScheduleDetail> fsdList) {

		String finReference = fod.getFinReference();
		Date odDate = fod.getFinODSchdDate();

		BigDecimal odPri = fod.getFinMaxODPri();
		BigDecimal odPft = fod.getFinMaxODPft();

		fod.setTotPenaltyAmt(BigDecimal.ZERO);
		fod.setTotPenaltyPaid(BigDecimal.ZERO);
		fod.setTotPenaltyBal(BigDecimal.ZERO);
		fod.setTotWaived(BigDecimal.ZERO);

		BigDecimal totPenaltyPaid = BigDecimal.ZERO;
		BigDecimal totWaived = BigDecimal.ZERO;

		List<OverdueChargeRecovery> odcrList = new ArrayList<OverdueChargeRecovery>();
		OverdueChargeRecovery odcr = new OverdueChargeRecovery();

		// Add Schedule Date to the ODC Recovery
		odcr.setFinReference(finReference);
		odcr.setFinODSchdDate(odDate);
		odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		odcr.setMovementDate(odDate);
		odcr.setFinCurODPri(odPri);
		odcr.setFinCurODPft(odPft);
		odcr.setFinCurODAmt(odPri.add(odPft));
		odcrList.add(odcr);

		if (rpdList == null) {
			rpdList = new ArrayList<FinanceRepayments>();
		}

		boolean isAddTodayRcd = true;

		// Load Overdue Charge Recovery from Repayments Movements
		for (int iRpd = 0; iRpd < rpdList.size(); iRpd++) {
			FinanceRepayments rpd = rpdList.get(iRpd);

			// check the payment made against the actual schedule date
			if (rpd.getFinSchdDate().compareTo(odDate) != 0) {
				continue;
			}

			Date grcDate = DateUtility.addDays(rpd.getFinSchdDate(), fod.getODGraceDays());

			if (rpd.getFinValueDate().compareTo(valueDate) > 0) {
				continue;
			}

			// MAx OD amounts is same as repayments balance amounts
			if (rpd.getFinSchdDate().compareTo(rpd.getFinValueDate()) == 0
					|| DateUtility.compare(grcDate, rpd.getFinValueDate()) >= 0) {
				continue;
			}

			odPri = odPri.subtract(rpd.getFinSchdPriPaid());
			odPft = odPft.subtract(rpd.getFinSchdPftPaid());

			odcr = new OverdueChargeRecovery();
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(rpd.getFinValueDate());
			odcr.setFinCurODPri(odPri);
			odcr.setFinCurODPft(odPft);
			odcr.setFinCurODAmt(odPri.add(odPft));
			odcr.setPenaltyPaid(rpd.getPenaltyPaid());
			odcr.setWaivedAmt(rpd.getPenaltyWaived());

			BigDecimal schdpaid = rpd.getFinSchdPriPaid().add(rpd.getFinSchdPftPaid());
			if (schdpaid.compareTo(BigDecimal.ZERO) > 0) {
				odcrList.add(odcr);
			}

			if (odcr.getMovementDate().compareTo(valueDate) == 0) {
				isAddTodayRcd = false;
			}

			totPenaltyPaid = totPenaltyPaid.add(rpd.getPenaltyPaid());
			totWaived = totWaived.add(rpd.getPenaltyWaived());

		}

		fod.setTotPenaltyAmt(BigDecimal.ZERO);

		// Add record with today date
		if (isAddTodayRcd) {
			odcr = new OverdueChargeRecovery();
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(valueDate);
			odcrList.add(odcr);
		}

		// If LPP capitalization required then load capitalize dates
		if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_PIPD)) {
			loadCpzDate(fsdList, odcrList, valueDate, financeMain);
		}

		OverdueChargeRecovery odcrCur = null;
		OverdueChargeRecovery odcrNext = null;
		fod.setLpCurCpzBal(BigDecimal.ZERO);
		fod.setLpCpzAmount(BigDecimal.ZERO);

		// Calculate the Penalty
		for (int iOdcr = 0; iOdcr < odcrList.size() - 1; iOdcr++) {
			odcrCur = odcrList.get(iOdcr);
			odcrNext = odcrList.get(iOdcr + 1);

			BigDecimal prvCpzBal = BigDecimal.ZERO;
			if (iOdcr > 0) {
				prvCpzBal = odcrList.get(iOdcr - 1).getLpCurCpzBal();
			}

			// Calculate the Penalty
			BigDecimal balanceForCal = BigDecimal.ZERO;
			if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPFT)) {
				balanceForCal = odcrCur.getFinCurODPft();
			} else if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_SPRI)) {
				balanceForCal = odcrCur.getFinCurODPri();
			} else if (StringUtils.equals(fod.getODChargeCalOn(), FinanceConstants.ODCALON_PIPD)) {
				balanceForCal = odcrCur.getFinCurODAmt().add(prvCpzBal);
			} else {
				balanceForCal = odcrCur.getFinCurODAmt();
			}

			// As same field is used to store both amount and percentage the
			// value is stored in minor units without decimals
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
				datamap.put("fm_finType", fod.getFinType());
				datamap.put("fm_finBranch", fod.getFinBranch());
				datamap.put("fin_ODDays", dueDays);
				datamap.put("fin_curODAmt", fod.getFinCurODAmt());
				if (finPftDetails != null) {
					datamap.put("fpd_tdSchdPriBal", finPftDetails.getTdSchdPriBal());
					datamap.put("fpd_tdSchdPftBal", finPftDetails.getTdSchdPftBal());
				}
				datamap.put("fm_productCategory", financeMain.getProductCategory());
				datamap.put("ft_product", financeMain.getFinCategory());
				if (extData != null && !extData.isEmpty()) {
					for (ExtendedField extendedField : extData) {
						for (ExtendedFieldData extendedFieldData : extendedField.getExtendedFieldDataList()) {
							datamap.put("LOAN_LOC_STOREID", extendedFieldData.getFieldValue());
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
				// Due Days accrual calculation
				BigDecimal penaltyRate = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100), 2,
						RoundingMode.HALF_DOWN);
				penalty = CalculationUtil.calInterest(dateCur, dateNext, balanceForCal, idb, penaltyRate);
			}

			penalty = CalculationUtil.roundAmount(penalty, financeMain.getCalRoundingMode(),
					financeMain.getRoundingTarget());

			odcrCur.setPenalty(penalty);
			odcrCur.setPenaltyBal(
					odcrCur.getPenalty().subtract(odcrCur.getPenaltyPaid()).subtract(odcrCur.getWaivedAmt()));

			if (odcrNext.isLpCpz()) {
				odcrCur.setLpCpzAmount(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid())
						.subtract(fod.getTotWaived()).add(odcrCur.getPenaltyBal()));
				odcrCur.setLpCurCpzBal(odcrCur.getLpCpzAmount());
			} else {
				odcrCur.setLpCurCpzBal(prvCpzBal.subtract(odcrCur.getPenaltyPaid()));
			}

			if (odcrCur.getLpCurCpzBal().compareTo(BigDecimal.ZERO) < 0) {
				odcrCur.setLpCurCpzBal(odcrCur.getLpCpzAmount());
			}

			fod.setLpCpz(odcrNext.isLpCpz());
			fod.setLpCpzAmount(fod.getLpCpzAmount().add(odcrCur.getLpCpzAmount()));
			fod.setLpCurCpzBal(fod.getLpCurCpzBal().add(odcrCur.getLpCurCpzBal()));
			fod.setTotPenaltyAmt(fod.getTotPenaltyAmt().add(penalty));
			fod.setTotPenaltyPaid(fod.getTotPenaltyPaid().add(odcrCur.getPenaltyPaid()));
			fod.setTotWaived(fod.getTotWaived().add(odcrCur.getWaivedAmt()));
		}

		// Add Today Paid and Waived
		fod.setTotPenaltyPaid(totPenaltyPaid);
		fod.setTotWaived(totWaived);
		fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
	}

	private void loadCpzDate(List<FinanceScheduleDetail> fsdList, List<OverdueChargeRecovery> odcrList, Date valueDate,
			FinanceMain financeMain) {
		OverdueChargeRecovery odcrStart = odcrList.get(0);
		String frequency = null;

		for (int iFsd = 0; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			// No capita;ization on OD Start Date
			if (fsd.getSchDate().compareTo(odcrStart.getFinODSchdDate()) <= 0) {
				continue;
			}

			// Capitalize not required on value date becuase effect only in
			// future
			if (fsd.getSchDate().compareTo(valueDate) >= 0) {
				break;
			}

			if (financeMain.isAllowGrcPeriod()
					&& DateUtility.compare(fsd.getSchDate(), financeMain.getGrcPeriodEndDate()) <= 0) {
				frequency = financeMain.getGrcPftFrq();
			} else {
				frequency = financeMain.getRepayPftFrq();
			}

			// Capitalize only on profit 
			if (!FrequencyUtil.isFrqDate(frequency, fsd.getSchDate())) {
				continue;
			}

			OverdueChargeRecovery odcr = new OverdueChargeRecovery();
			odcr.setFinReference(fsd.getFinReference());
			odcr.setFinODSchdDate(odcrStart.getFinODSchdDate());
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(fsd.getSchDate());
			odcr.setLpCpz(true);
			odcr.setFinCurODPri(BigDecimal.ZERO);
			odcr.setFinCurODPft(BigDecimal.ZERO);
			odcr.setFinCurODAmt(BigDecimal.ZERO);
			odcrList.add(odcr);
		}

		// Set negativeValue with previous value
		odcrList = sortOdcrListByValueDate(odcrList);
		OverdueChargeRecovery odcrPrv = odcrList.get(0);
		for (int iOdcr = 1; iOdcr < odcrList.size(); iOdcr++) {
			OverdueChargeRecovery odcrCur = odcrList.get(iOdcr);
			if (odcrCur.isLpCpz()) {
				odcrCur.setFinCurODPri(odcrPrv.getFinCurODPri());
				odcrCur.setFinCurODPft(odcrPrv.getFinCurODPft());
				odcrCur.setFinCurODAmt(odcrPrv.getFinCurODAmt());
			}
			odcrPrv = odcrCur;
		}
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

	public List<FinanceRepayments> sortRpdListByValueDate(List<FinanceRepayments> rpdList) {

		if (rpdList != null && rpdList.size() > 0) {
			Collections.sort(rpdList, new Comparator<FinanceRepayments>() {
				@Override
				public int compare(FinanceRepayments detail1, FinanceRepayments detail2) {
					return DateUtility.compare(detail1.getFinValueDate(), detail2.getFinValueDate());
				}
			});
		}

		return rpdList;
	}

	public List<OverdueChargeRecovery> sortOdcrListByValueDate(List<OverdueChargeRecovery> odcrList) {

		if (odcrList != null && odcrList.size() > 0) {
			Collections.sort(odcrList, new Comparator<OverdueChargeRecovery>() {
				@Override
				public int compare(OverdueChargeRecovery detail1, OverdueChargeRecovery detail2) {
					return DateUtility.compare(detail1.getMovementDate(), detail2.getMovementDate());
				}
			});
		}

		return odcrList;
	}

}
