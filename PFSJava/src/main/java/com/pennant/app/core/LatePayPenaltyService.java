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

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RuleExecutionUtil;
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
import com.pennant.pff.eod.cache.RuleConfigCache;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.eod.EODUtil;

public class LatePayPenaltyService extends ServiceHelper {

	/**
	 * Default constructor
	 */
	public LatePayPenaltyService() {
		super();
	}

	public void computeLPP(FinODDetails fod, Date valueDate, FinanceMain fm, List<FinanceScheduleDetail> schedules,
			List<FinanceRepayments> repayments) {

		BigDecimal penalty = BigDecimal.ZERO;

		// Late Payment Penalty. Do not apply LPP
		if (!fod.isApplyODPenalty()) {
			// #PSD 137379
			fod.setTotPenaltyAmt(penalty);
			fod.setTotPenaltyBal(penalty);
			setTotals(fod);
			return;
		}

		/* Still before the grace days no need to calculate OD penalty */
		if (fod.getFinCurODDays() <= fod.getODGraceDays()) {
			// #PSD 137379
			fod.setTotPenaltyAmt(penalty);
			fod.setTotPenaltyBal(penalty);
			setTotals(fod);
			return;
		}

		BigDecimal balanceForCal = BigDecimal.ZERO;
		BigDecimal odChargeAmtOrPerc = fod.getODChargeAmtOrPerc();

		switch (fod.getODChargeType()) {
		case FinanceConstants.PENALTYTYPE_FLAT:
			/* Fixed Fee. One Time */
			penalty = odChargeAmtOrPerc;
			break;
		case FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH:
			/* Fixed Fee. On Every Passing Schedule Month */
			balanceForCal = getBalanceForCal(fod);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				int numberOfMonths = getMonthsBetween(fod, schedules, valueDate);
				penalty = odChargeAmtOrPerc.multiply(new BigDecimal(numberOfMonths));
			}
			break;
		case FinanceConstants.PENALTYTYPE_PERC_ONETIME:
			/* Percentage ON OD Amount. One Time */
			balanceForCal = getBalanceForCal(fod);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal amtOrPercetage = odChargeAmtOrPerc.divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
				penalty = balanceForCal.multiply(amtOrPercetage).divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			}

			break;
		case FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH:
			/* Percentage ON OD Amount. One Time */
			balanceForCal = getBalanceForCal(fod);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				int numberOfMonths = getMonthsBetween(fod, schedules, valueDate);
				BigDecimal amtOrPercetage = odChargeAmtOrPerc.divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
				penalty = balanceForCal.multiply(amtOrPercetage).multiply(new BigDecimal(numberOfMonths))
						.divide(new BigDecimal(100));
			}
			break;

		default:
			/* On Due Days (or) Rule Fixed amount by Due Days */
			long finID = fod.getFinID();
			Date odDate = fod.getFinODSchdDate();
			if (repayments == null) {
				repayments = financeRepaymentsDAO.getByFinRefAndSchdDate(finID, odDate);
			}

			String pftDaysBasis = fm.getProfitDaysBasis();
			if (CalculationConstants.IDB_BY_PERIOD.equals(pftDaysBasis)) {
				pftDaysBasis = CalculationConstants.IDB_ACT_365LEAP;
			}

			repayments = sortRpdListByValueDate(repayments);
			prepareDueDateData(fod, valueDate, pftDaysBasis, repayments, fm, schedules);
			penalty = fod.getTotPenaltyAmt();

			break;
		}

		penalty = CalculationUtil.roundAmount(penalty, fm.getCalRoundingMode(), fm.getRoundingTarget());
		fod.setTotPenaltyAmt(penalty); // ### 03-12-2018 PSD Ticket ID: 130669
		fod.setFinODTillDate(valueDate);

		setTotals(fod);
	}

	private void setTotals(FinODDetails fod) {
		BigDecimal totPenaltyBal = fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid())
				.subtract(fod.getTotWaived());
		if (totPenaltyBal.compareTo(BigDecimal.ZERO) >= 0) {
			fod.setTotPenaltyBal(totPenaltyBal);
			fod.setPayableAmount(BigDecimal.ZERO);
		} else {
			fod.setTotPenaltyBal(BigDecimal.ZERO);
			fod.setPayableAmount(fod.getTotPenaltyPaid().subtract(fod.getTotWaived()).subtract(fod.getTotPenaltyAmt()));
		}
	}

	private BigDecimal getBalanceForCal(FinODDetails fod) {
		BigDecimal balanceForCal = BigDecimal.ZERO;

		if (FinanceConstants.ODCALON_SPFT.equals(fod.getODChargeCalOn())) {
			balanceForCal = fod.getFinMaxODPft();
		} else if (FinanceConstants.ODCALON_SPRI.equals(fod.getODChargeCalOn())) {
			balanceForCal = fod.getFinMaxODPri();
		} else {
			balanceForCal = fod.getFinMaxODAmt();
		}
		return balanceForCal;
	}

	private void prepareDueDateData(FinODDetails fod, Date valueDate, String idb, List<FinanceRepayments> rpdList,
			FinanceMain fm, List<FinanceScheduleDetail> schedules) {

		long finID = fod.getFinID();
		String finReference = fod.getFinReference();
		Date odDate = fod.getFinODSchdDate();

		BigDecimal odPri = fod.getFinMaxODPri();
		BigDecimal odPft = fod.getFinMaxODPft();

		fod.setTotPenaltyAmt(BigDecimal.ZERO);
		String odChargeCalOn = fod.getODChargeCalOn();

		if (FinanceConstants.ODCALON_PIPD_FRQ.equals(odChargeCalOn)
				|| FinanceConstants.ODCALON_PIPD_EOM.equals(odChargeCalOn)) {
			fod.setTotPenaltyPaid(BigDecimal.ZERO);
			fod.setTotPenaltyBal(BigDecimal.ZERO);
			fod.setTotWaived(BigDecimal.ZERO);
		}

		BigDecimal totPenaltyPaid = BigDecimal.ZERO;
		BigDecimal totWaived = BigDecimal.ZERO;

		List<OverdueChargeRecovery> odcrList = new ArrayList<OverdueChargeRecovery>();
		OverdueChargeRecovery odcr = new OverdueChargeRecovery();

		// Add Schedule Date to the ODC Recovery
		odcr.setFinReference(finReference);
		odcr.setFinODSchdDate(odDate);
		odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		odcr.setMovementDate(odDate);

		if (DateUtil.compare(odcr.getMovementDate(), DateUtil.getMonthStart(odcr.getMovementDate())) == 0) {
			odcr.setLpCpz(true);
			odcr.setNewRecord(true);
		}

		odcr.setFinCurODPri(odPri);
		odcr.setFinCurODPft(odPft);
		odcr.setFinCurODAmt(odPri.add(odPft));
		odcrList.add(odcr);

		if (rpdList == null) {
			rpdList = new ArrayList<>();
		}

		boolean isAddTodayRcd = true;

		Date grcDate = DateUtil.addDays(odDate, fod.getODGraceDays());

		// Load Overdue Charge Recovery from Repayments Movements
		for (FinanceRepayments rpd : rpdList) {

			Date schdDate = rpd.getFinSchdDate();
			Date finValueDate = rpd.getFinValueDate();

			// check the payment made against the actual schedule date
			if (schdDate.compareTo(odDate) != 0) {
				continue;
			}

			// PSD#163479
			totPenaltyPaid = totPenaltyPaid.add(rpd.getPenaltyPaid());
			totWaived = totWaived.add(rpd.getPenaltyWaived());

			/*
			 * PSD#169647 Removing This Condition as We need to consider past paid value date receipts as well in LPP
			 * calculation
			 */
			// common issue 20
			/*
			 * if (finValueDate.compareTo(valueDate) > 0) { continue; }
			 */

			if (schdDate.compareTo(finValueDate) == 0 || grcDate.compareTo(finValueDate) > 0) {
				continue;
			}

			// MAx OD amounts is same as repayments balance amounts
			if (schdDate.compareTo(finValueDate) == 0 || grcDate.compareTo(finValueDate) >= 0) {
				continue;
			}

			boolean isOdcrFound = false;

			for (OverdueChargeRecovery oldOdcr : odcrList) {
				if (oldOdcr.getMovementDate().compareTo(finValueDate) == 0) {
					isOdcrFound = true;

					oldOdcr.setFinCurODPri(oldOdcr.getFinCurODPri().subtract(rpd.getFinSchdPriPaid()));
					oldOdcr.setFinCurODPft(oldOdcr.getFinCurODPft().subtract(rpd.getFinSchdPftPaid()));
					oldOdcr.setFinCurODAmt(oldOdcr.getFinCurODPri().add(oldOdcr.getFinCurODPft()));
					oldOdcr.setPenaltyPaid(oldOdcr.getPenaltyPaid().add(rpd.getPenaltyPaid()));
					oldOdcr.setWaivedAmt(oldOdcr.getWaivedAmt().add(rpd.getPenaltyWaived()));

					odPri = oldOdcr.getFinCurODPri();
					odPft = oldOdcr.getFinCurODPft();
					break;
				}
			}

			if (isOdcrFound) {
				continue;
			}

			odcr = new OverdueChargeRecovery();
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(finValueDate);
			odcr.setFinCurODPri(odPri.subtract(rpd.getFinSchdPriPaid()));
			odcr.setFinCurODPft(odPft.subtract(rpd.getFinSchdPftPaid()));
			odcr.setFinCurODAmt(odcr.getFinCurODPri().add(odcr.getFinCurODPft()));
			odcr.setPenaltyPaid(rpd.getPenaltyPaid());
			odcr.setWaivedAmt(rpd.getPenaltyWaived());

			BigDecimal schdpaid = rpd.getFinSchdPriPaid().add(rpd.getFinSchdPftPaid());
			if (schdpaid.compareTo(BigDecimal.ZERO) > 0) {
				odcrList.add(odcr);
			}
			odPri = odcr.getFinCurODPri();
			odPft = odcr.getFinCurODPft();

			if (odcr.getMovementDate().compareTo(valueDate) == 0 && schdpaid.compareTo(BigDecimal.ZERO) > 0) {
				isAddTodayRcd = false;
			}

		}

		// Add record with today date
		if (isAddTodayRcd) {
			odcr = new OverdueChargeRecovery();
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odDate);
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(valueDate);
			odcr.setFinCurODPri(odPri);
			odcr.setFinCurODPft(odPft);
			odcr.setFinCurODAmt(odPri.add(odPft));
			odcrList.add(odcr);
		}

		// If LPP capitalization required then load capitalize dates
		if (FinanceConstants.ODCALON_PIPD_FRQ.equals(odChargeCalOn)) {
			loadFrqCpzDate(schedules, odcrList, valueDate, fm);
		} else if (FinanceConstants.ODCALON_PIPD_EOM.equals(odChargeCalOn)) {
			loadEOMCpzDate(schedules, odcrList, valueDate, fm);
		}

		OverdueChargeRecovery odcrCur = null;
		OverdueChargeRecovery odcrPrv = null;
		fod.setLpCurCpzBal(BigDecimal.ZERO);
		fod.setLpCpzAmount(BigDecimal.ZERO);
		fod.setTotPenaltyAmt(BigDecimal.ZERO);

		// Calculate the Penalty
		for (int iOdcr = 1; iOdcr < odcrList.size(); iOdcr++) {
			odcrCur = odcrList.get(iOdcr);
			odcrPrv = odcrList.get(iOdcr - 1);

			// Calculate the Penalty
			BigDecimal balanceForCal = BigDecimal.ZERO;

			switch (odChargeCalOn) {
			case FinanceConstants.ODCALON_SPFT:
				balanceForCal = odcrPrv.getFinCurODPft();
				break;
			case FinanceConstants.ODCALON_SPRI:
				balanceForCal = odcrPrv.getFinCurODPri();
				break;
			case FinanceConstants.ODCALON_PIPD_FRQ:
			case FinanceConstants.ODCALON_PIPD_EOM:
				balanceForCal = odcrPrv.getFinCurODAmt().add(odcrPrv.getLpCurCpzBal());
				break;
			default:
				balanceForCal = odcrPrv.getFinCurODAmt();
				break;
			}

			Date dateCur = odcrCur.getMovementDate();
			Date datePrv = odcrPrv.getMovementDate();

			BigDecimal penalty = BigDecimal.ZERO;

			// If charge calculation Type is Rule Fixed amount by Due Days
			if (FinanceConstants.PENALTYTYPE_RULEFXDD.equals(fod.getODChargeType())) {
				int dueDays = DateUtil.getDaysBetween(datePrv, dateCur);
				FinanceProfitDetail pfd = financeProfitDetailDAO.getFinProfitDetailsById(finID);

				Map<String, Object> datamap = new HashMap<>();
				datamap.put("fm_finType", fod.getFinType());
				datamap.put("fm_finBranch", fod.getFinBranch());
				datamap.put("fin_ODDays", dueDays);
				datamap.put("fin_curODAmt", fod.getFinCurODAmt());

				if (pfd != null) {
					datamap.put("fpd_tdSchdPriBal", pfd.getTdSchdPriBal());
					datamap.put("fpd_tdSchdPftBal", pfd.getTdSchdPftBal());
				}

				datamap.put("fm_productCategory", fm.getProductCategory());
				datamap.put("ft_product", fm.getFinCategory());

				List<ExtendedField> extData = extendedFieldDetailsService.getExtndedFieldDetails(
						ExtendedFieldConstants.MODULE_LOAN, fm.getFinCategory(), FinServiceEvent.ORG, finReference);

				if (extData != null && !extData.isEmpty()) {
					for (ExtendedField extendedField : extData) {
						for (ExtendedFieldData extendedFieldData : extendedField.getExtendedFieldDataList()) {
							datamap.put("LOAN_LOC_STOREID", extendedFieldData.getFieldValue());
						}
					}
				}
				// Fetch Rule Query
				String module = RuleConstants.MODULE_LPPRULE;
				String sqlRule = "";

				if (EODUtil.isEod()) {
					sqlRule = RuleConfigCache.getCacheRuleCode(fod.getODRuleCode(), module, module);
				} else {
					sqlRule = ruleDAO.getAmountRule(fod.getODRuleCode(), module, module);
				}

				BigDecimal fixedAmt = BigDecimal.ZERO;
				if (StringUtils.isNotEmpty(sqlRule)) {
					fixedAmt = (BigDecimal) RuleExecutionUtil.executeRule(sqlRule, datamap,
							CurrencyUtil.getCcyNumber(PennantConstants.LOCAL_CCY), RuleReturnType.DECIMAL);
				}
				if (fixedAmt.compareTo(BigDecimal.ZERO) > 0) {
					penalty = fixedAmt.multiply(new BigDecimal(dueDays));
				}
			} else {
				// Due Days accrual calculation
				BigDecimal penaltyRate = fod.getODChargeAmtOrPerc().divide(new BigDecimal(100), 2,
						RoundingMode.HALF_DOWN);
				penalty = CalculationUtil.calInterest(datePrv, dateCur, balanceForCal, idb, penaltyRate);
			}

			penalty = CalculationUtil.roundAmount(penalty, fm.getCalRoundingMode(), fm.getRoundingTarget());

			odcrCur.setPenalty(penalty);
			odcrCur.setPenaltyBal(
					odcrCur.getPenalty().subtract(odcrCur.getPenaltyPaid()).subtract(odcrCur.getWaivedAmt()));

			if (odcrCur.isLpCpz()) {
				odcrCur.setLpCpzAmount(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid())
						.subtract(fod.getTotWaived()).add(odcrCur.getPenaltyBal()));
				odcrCur.setLpCurCpzBal(odcrCur.getLpCpzAmount());
			} else {
				odcrCur.setLpCurCpzBal(
						odcrPrv.getLpCurCpzBal().subtract(odcrCur.getPenaltyPaid().subtract(odcrCur.getWaivedAmt())));
			}

			if (odcrCur.getLpCurCpzBal().compareTo(BigDecimal.ZERO) < 0) {
				odcrCur.setLpCurCpzBal(BigDecimal.ZERO);
			}

			fod.setLpCpz(odcrCur.isLpCpz());

			fod.setLpCpzAmount(odcrCur.getLpCpzAmount());
			fod.setLpCurCpzBal(odcrCur.getLpCurCpzBal());

			fod.setTotPenaltyAmt(fod.getTotPenaltyAmt().add(penalty));
			fod.setTotPenaltyPaid(fod.getTotPenaltyPaid().add(odcrCur.getPenaltyPaid()));
			fod.setTotWaived(fod.getTotWaived().add(odcrCur.getWaivedAmt()));
			fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));

			if (fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) <= 0) {
				fod.setTotPenaltyBal(BigDecimal.ZERO);
			}
		}

		// Add Today Paid and Waived
		fod.setTotPenaltyPaid(totPenaltyPaid);
		fod.setTotWaived(totWaived);
		fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
	}

	private void loadFrqCpzDate(List<FinanceScheduleDetail> schedules, List<OverdueChargeRecovery> odcrList,
			Date valueDate, FinanceMain fm) {
		OverdueChargeRecovery odcrStart = odcrList.get(0);
		String frequency = null;

		for (FinanceScheduleDetail schd : schedules) {

			// No capita;ization on OD Start Date
			if (schd.getSchDate().compareTo(odcrStart.getFinODSchdDate()) <= 0) {
				continue;
			}

			// Capitalize not required on value date becuase effect only in
			// future
			if (schd.getSchDate().compareTo(valueDate) >= 0) {
				break;
			}

			if (fm.isAllowGrcPeriod() && DateUtil.compare(schd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
				frequency = fm.getGrcPftFrq();
			} else {
				frequency = fm.getRepayPftFrq();
			}

			// Capitalize only on profit
			if (!FrequencyUtil.isFrqDate(frequency, schd.getSchDate())) {
				continue;
			}

			OverdueChargeRecovery odcr = new OverdueChargeRecovery();
			odcr.setFinReference(schd.getFinReference());
			odcr.setFinODSchdDate(odcrStart.getFinODSchdDate());
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(schd.getSchDate());
			odcr.setLpCpz(true);
			odcr.setFinCurODPri(BigDecimal.ZERO);
			odcr.setFinCurODPft(BigDecimal.ZERO);
			odcr.setFinCurODAmt(BigDecimal.ZERO);
			odcrList.add(odcr);
		}

		setCurODOnCpzLoaded(odcrList);
	}

	private void loadEOMCpzDate(List<FinanceScheduleDetail> schedules, List<OverdueChargeRecovery> odcrList,
			Date valueDate, FinanceMain fm) {

		String finReference = fm.getFinReference();
		OverdueChargeRecovery odcrStart = odcrList.get(0);

		Date somDate = odcrStart.getFinODSchdDate();
		somDate = DateUtil.getMonthEnd(somDate);
		somDate = DateUtil.addDays(somDate, 1);

		if (DateUtil.compare(somDate, valueDate) >= 0) {
			return;
		}

		while (DateUtil.compare(somDate, valueDate) < 0) {

			boolean isAddODCR = true;
			for (int iOdcr = 1; iOdcr < odcrList.size(); iOdcr++) {
				Date odMvtDate = odcrList.get(iOdcr).getMovementDate();
				if (DateUtil.compare(odMvtDate, somDate) == 0) {
					isAddODCR = false;
					break;
				}
			}

			if (!isAddODCR) {
				somDate = DateUtil.addDays(somDate, 31);
				somDate = DateUtil.getMonthStart(somDate);
				continue;
			}

			OverdueChargeRecovery odcr = new OverdueChargeRecovery();
			odcr.setFinReference(finReference);
			odcr.setFinODSchdDate(odcrStart.getFinODSchdDate());
			odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
			odcr.setMovementDate(somDate);
			odcr.setLpCpz(true);
			odcr.setFinCurODPri(BigDecimal.ZERO);
			odcr.setFinCurODPft(BigDecimal.ZERO);
			odcr.setFinCurODAmt(BigDecimal.ZERO);
			odcrList.add(odcr);

			somDate = DateUtil.addDays(somDate, 31);
			somDate = DateUtil.getMonthStart(somDate);
		}

		setCurODOnCpzLoaded(odcrList);
	}

	private void setCurODOnCpzLoaded(List<OverdueChargeRecovery> odcrList) {
		// Set negativeValue with previous value
		odcrList = sortOdcrListByValueDate(odcrList);
		OverdueChargeRecovery odcrPrv = odcrList.get(0);
		for (int iOdcr = 1; iOdcr < odcrList.size(); iOdcr++) {
			OverdueChargeRecovery odcrCur = odcrList.get(iOdcr);
			if (odcrCur.isLpCpz() && !odcrCur.isNewRecord()) {
				odcrCur.setFinCurODPri(odcrPrv.getFinCurODPri());
				odcrCur.setFinCurODPft(odcrPrv.getFinCurODPft());
				odcrCur.setFinCurODAmt(odcrPrv.getFinCurODAmt());
			}

			odcrPrv = odcrCur;
		}
	}

	private int getMonthsBetween(FinODDetails fod, List<FinanceScheduleDetail> schedules, Date valueDate) {
		int terms = 0;
		for (FinanceScheduleDetail schd : schedules) {

			if (DateUtil.compare(schd.getSchDate(), fod.getFinODSchdDate()) < 0) {
				continue;
			}

			if (schd.getSchDate().compareTo(valueDate) > 0) {
				break;
			}

			if ((schd.isRepayOnSchDate() || schd.isPftOnSchDate()) && schd.isFrqDate()) {
				terms++;
			}
		}

		return terms;

	}

	public List<FinanceRepayments> sortRpdListByValueDate(List<FinanceRepayments> repayments) {

		if (repayments != null && repayments.size() > 0) {
			Collections.sort(repayments, new Comparator<FinanceRepayments>() {
				@Override
				public int compare(FinanceRepayments detail1, FinanceRepayments detail2) {
					return DateUtil.compare(detail1.getFinValueDate(), detail2.getFinValueDate());
				}
			});
		}

		return repayments;
	}

	public List<OverdueChargeRecovery> sortOdcrListByValueDate(List<OverdueChargeRecovery> odcrList) {

		if (odcrList != null && odcrList.size() > 0) {
			Collections.sort(odcrList, new Comparator<OverdueChargeRecovery>() {
				@Override
				public int compare(OverdueChargeRecovery detail1, OverdueChargeRecovery detail2) {
					return DateUtil.compare(detail1.getMovementDate(), detail2.getMovementDate());
				}
			});
		}

		return odcrList;
	}

}
