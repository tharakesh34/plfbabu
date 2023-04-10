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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.LookupMethods;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.pff.eod.cache.RuleConfigCache;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennanttech.pff.overdue.constants.PenaltyCalculator;

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

		BigDecimal balanceForCal = BigDecimal.ZERO;
		BigDecimal odChargeAmtOrPerc = fod.getODChargeAmtOrPerc();

		/* On Due Days (or) Rule Fixed amount by Due Days (or) On Days with Effective new LPP Rate */
		long finID = fod.getFinID();
		Date odDate = fod.getFinODSchdDate();
		if (repayments == null) {
			repayments = financeRepaymentsDAO.getByFinRefAndSchdDate(finID, odDate);
		} else {
			repayments = sortRpdListByValueDate(repayments);
		}

		List<OverdueChargeRecovery> odcrList = prepareDueDateData(fod, valueDate, repayments, fm, schedules);

		String odChargeType = fod.getODChargeType();
		switch (odChargeType) {
		case ChargeType.FLAT:
			/* Fixed Fee. One Time */
			penalty = odChargeAmtOrPerc;
			valueDate = deriveValueDate(fod, valueDate, odcrList, repayments);
			break;
		case ChargeType.FLAT_ON_PD_MTH:
			/* Fixed Fee. On Every Passing Schedule Month */
			balanceForCal = getBalanceForCal(fod, schedules);

			valueDate = deriveValueDate(fod, valueDate, odcrList, repayments);
			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				int numberOfMonths = getMonthsBetween(fod, schedules, valueDate);
				penalty = odChargeAmtOrPerc.multiply(new BigDecimal(numberOfMonths));
			}
			break;
		case ChargeType.PERC_ONE_TIME:
			/* Percentage ON OD Amount. One Time */
			balanceForCal = getBalanceForCal(fod, schedules);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal amtOrPercetage = odChargeAmtOrPerc.divide(new BigDecimal(100));
				penalty = balanceForCal.multiply(amtOrPercetage).divide(new BigDecimal(100));
			}

			break;
		case ChargeType.PERC_ON_PD_MTH:
			/* Percentage ON OD Amount. One Time */
			balanceForCal = getBalanceForCal(fod, schedules);

			if (balanceForCal.compareTo(BigDecimal.ZERO) > 0) {
				valueDate = deriveValueDate(fod, valueDate, odcrList, repayments);
				int numberOfMonths = getMonthsBetween(fod, schedules, valueDate);
				BigDecimal amtOrPercetage = odChargeAmtOrPerc.divide(new BigDecimal(100));
				penalty = balanceForCal.multiply(amtOrPercetage).multiply(new BigDecimal(numberOfMonths))
						.divide(new BigDecimal(100));
			}

			break;

		default:
			penalty = fod.getTotPenaltyAmt();
			break;
		}

		int curODDays = DateUtil.getDaysBetween(odDate, valueDate);
		/* Still before the grace days no need to calculate OD penalty */
		if (curODDays <= fod.getODGraceDays()) {
			fod.setFinCurODDays(curODDays);
			penalty = BigDecimal.ZERO;
		}

		penalty = CalculationUtil.roundAmount(penalty, fm.getCalRoundingMode(), fm.getRoundingTarget());
		fod.setTotPenaltyAmt(penalty); // ### 03-12-2018 PSD Ticket ID: 130669
		if (FinanceUtil.isMinimunODCChargeReq(odChargeType)) {
			if (penalty.compareTo(fod.getOdMinAmount()) < 0) {
				fod.setTotPenaltyAmt(fod.getOdMinAmount());
			}
		}

		fod.setFinODTillDate(valueDate);

		fod.setTotPenaltyBal(fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));

		if (fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) <= 0) {
			fod.setTotPenaltyBal(BigDecimal.ZERO);
		}

		if (fod.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0) {
			fod.setFinCurODDays(DateUtil.getDaysBetween(fod.getFinODSchdDate(), valueDate));
		} else {
			fod.setFinCurODDays(0);
		}

		setTotals(fod);

		if (fod.getLppDueTillDate() != null && fod.getLppDueAmt().compareTo(fod.getTotPenaltyAmt()) > 0) {
			if (fm.isEOD()) {
				fod.setTotPenaltyBal(fod.getTotPenaltyAmt());
				fod.setTotPenaltyAmt(fod.getLppDueAmt().add(fod.getTotPenaltyAmt()));
			} else {
				fod.setPayableAmount(fod.getLppDueAmt().subtract(fod.getTotPenaltyAmt()));
				fod.setTotPenaltyAmt(fod.getLppDueAmt());
				if (fod.getTotPenaltyPaid().compareTo(fod.getTotPenaltyAmt()) != 0) {
					fod.setTotPenaltyBal(fod.getLppDueAmt());
				}
			}
		}
	}

	public void postLatePayAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent) {
		Date monthEndDate = custEODEvent.getEodDate();
		int dueDays = 0;

		FinanceMain fm = finEODEvent.getFinanceMain();

		List<FinODDetails> odList = finEODEvent.getFinODDetails();

		List<FinOverDueCharges> saveList = new ArrayList<>();

		for (FinODDetails fod : odList) {
			Date schdDate = fod.getFinODSchdDate();

			if ((fod.getFinCurODPri().add(fod.getFinCurODPft())).compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			List<FinOverDueCharges> finODCAmounts = finODCAmountDAO.getFinODCAmtByFinRef(fm.getFinID(), schdDate,
					RepayConstants.FEE_TYPE_LPP);
			BigDecimal totPenaltyAmt = fod.getTotPenaltyAmt();
			BigDecimal prvMnthPenaltyAmt = BigDecimal.ZERO;

			if (CollectionUtils.isEmpty(finODCAmounts)) {
				FinOverDueCharges finODCAmount = createODCAmounts(fod, totPenaltyAmt, monthEndDate);
				saveList.add(finODCAmount);
			} else {

				FinOverDueCharges prvFinODCAmount = null;

				for (FinOverDueCharges finODCAmount : finODCAmounts) {
					Date postDate = finODCAmount.getPostDate();
					dueDays = DateUtil.getDaysBetween(postDate, monthEndDate);
					if (postDate.compareTo(monthEndDate) < 0) {
						prvMnthPenaltyAmt = prvMnthPenaltyAmt.add(finODCAmount.getAmount());
					} else if (postDate.compareTo(monthEndDate) == 0) {
						prvFinODCAmount = finODCAmount;
					}
				}
				if (prvFinODCAmount != null) {
					prvFinODCAmount.setAmount(totPenaltyAmt.subtract(prvMnthPenaltyAmt));
					prvFinODCAmount.setBalanceAmt(prvFinODCAmount.getAmount().subtract(prvFinODCAmount.getPaidAmount())
							.subtract(prvFinODCAmount.getWaivedAmount()));
					prvFinODCAmount.setDueDays(dueDays);
					saveList.add(prvFinODCAmount);
				} else {
					FinOverDueCharges finODCAmount = createODCAmounts(fod, totPenaltyAmt.subtract(prvMnthPenaltyAmt),
							monthEndDate);
					finODCAmount.setDueDays(dueDays);
					saveList.add(finODCAmount);
				}
			}
		}

		finEODEvent.getFinODCAmounts().addAll(saveList);
	}

	private static FinOverDueCharges createODCAmounts(FinODDetails od, BigDecimal penalty, Date monthEndDate) {
		FinOverDueCharges finod = new FinOverDueCharges();

		finod.setFinID(od.getFinID());
		finod.setSchDate(od.getFinODSchdDate());
		finod.setPostDate(monthEndDate);
		finod.setValueDate(monthEndDate);
		finod.setAmount(penalty);
		finod.setNewRecord(true);
		finod.setBalanceAmt(penalty);
		finod.setOdPri(od.getFinCurODPri());
		finod.setOdPft(od.getFinCurODPft());
		finod.setFinOdTillDate(od.getFinODTillDate());
		finod.setDueDays(DateUtil.getDaysBetween(od.getFinODSchdDate(), monthEndDate));
		finod.setChargeType(RepayConstants.FEE_TYPE_LPP);

		return finod;
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

	private BigDecimal getBalanceForCal(FinODDetails fod, List<FinanceScheduleDetail> schedules) {
		if (fod.getODChargeCalOn() == null) {
			return fod.getFinMaxODAmt();
		}

		switch (fod.getODChargeCalOn()) {
		case FinanceConstants.ODCALON_SPFT:
			return fod.getFinMaxODPft();
		case FinanceConstants.ODCALON_SPRI:
			return fod.getFinMaxODPri();
		case FinanceConstants.ODCALON_INST:
			FinanceScheduleDetail schd = schedules.stream()
					.filter(s -> s.getSchDate().compareTo(fod.getFinODSchdDate()) == 0).findFirst().orElse(null);
			if (schd != null) {
				return schd.getRepayAmount();
			} else {
				return BigDecimal.ZERO;
			}
		default:
			return fod.getFinMaxODAmt();
		}
	}

	/*
	 * This method will prepare the ODC recovery list based on RPD list by value date STEP 1: Add Due date record to ODC
	 * Recovery List with schedule date as value date STEP 2: Add/Update records in ODC Recovery list by value date STEP
	 * 3: Add/Update today record in ODC Recovery STEP 4: If LPP calculation method is Effective rate then Add/Update
	 * effective date rates in ODC Recovery STEP 5: Go through the ODC Recovery list and compute LPP charges
	 */

	private List<OverdueChargeRecovery> prepareDueDateData(FinODDetails fod, Date valueDate,
			List<FinanceRepayments> rpdList, FinanceMain fm, List<FinanceScheduleDetail> fsdList) {

		fod.setTotPenaltyAmt(BigDecimal.ZERO);

		// Prepare Due paid history
		List<OverdueChargeRecovery> odcrList = prepareDuePaidHist(fod, rpdList, fm, valueDate, fsdList);

		if (!StringUtils.equals(fod.getODChargeType(), ChargeType.RULE)
				&& !StringUtils.equals(fod.getODChargeType(), ChargeType.PERC_ON_DUE_DAYS)
				&& !StringUtils.equals(fod.getODChargeType(), ChargeType.PERC_ON_EFF_DUE_DAYS)) {
			computeMaxAndCurDetails(fod, odcrList, fm, fsdList);
			return odcrList;
		}

		odcrList = addDuePaidHistForDueDateCal(fod, fm, valueDate, fsdList, odcrList);
		calculateLPPAmounts(fod, odcrList, fm, fsdList);
		return odcrList;
	}

	/**
	 * Prepare the receipt schedule for the due installment
	 *
	 * @param fod:       Fin OD Details record created when installment turned in to Due and penalty received/waived.
	 * @param rpdList:   Installment reapyment details with sorted order by Value date
	 * @param fm:        Finance Main data along with penalty rates list sorted by effective date
	 * @param valueDate: Value date (Till Date) for LPP calculation
	 * @return ODCRList: Due installment paid history. In case of LPP calculation method is effective rate then
	 *         additional history of rate changes will be appended and sorted.
	 */
	private List<OverdueChargeRecovery> prepareDuePaidHist(FinODDetails fod, List<FinanceRepayments> rpdList,
			FinanceMain fm, Date valueDate, List<FinanceScheduleDetail> fsdList) {

		// Declare empty ODCR list
		List<OverdueChargeRecovery> odcrList = new ArrayList<>();
		FinanceRepayments tempRpd = new FinanceRepayments();

		// Add First Day Record to ODCR List with movement date as installment due date
		OverdueChargeRecovery odcr1 = resetODCR(fod, fod.getFinODSchdDate(), tempRpd, false);

		// Quick Scrum DE#87: Original Requirement Penal capilization should happen on onth end
		// Later it changed to Start of the month.
		if (DateUtil.compare(odcr1.getMovementDate(), DateUtil.getMonthStart(odcr1.getMovementDate())) == 0) {
			odcr1.setLpCpz(true);
			odcr1.setNewRecord(true);
		}

		odcrList.add(odcr1);

		// PREPARE WORKING LIST FOR HISTORY CREATION.
		// REASON FOR CREATION OF WORKING LIST -->
		// IN CASE OF THE CALLER (EXAMPLE: RECEIPTS) FETCHES FULL DATA OF RPD WITH VALUE DATE ORDER
		// THEN THERE IS POSSIBILITY OF ADDITIONAL DATA AND DISORTED ORDER OF SCHDDATE POSSIBLE.

		List<FinanceRepayments> rpdwList = createWorkingRPDList(rpdList, fod.getFinODSchdDate());

		// START ADDING INSTALLMENT DUE PAYMENT HISTORY
		fod.setTotPenaltyPaid(BigDecimal.ZERO);
		fod.setTotPenaltyBal(BigDecimal.ZERO);
		fod.setTotWaived(BigDecimal.ZERO);
		Date prvMvtDate = null;

		// This list will continue only schedule date records order by value date
		for (FinanceRepayments rpd : rpdwList) {
			fod.setTotPenaltyPaid(fod.getTotPenaltyPaid().add(rpd.getPenaltyPaid()));
			fod.setTotWaived(fod.getTotWaived().add(rpd.getPenaltyWaived()));

			// Find if the latest ODCR record movement date matches with RPD value date
			prvMvtDate = odcrList.get(odcrList.size() - 1).getMovementDate();

			if (DateUtil.compare(prvMvtDate, rpd.getFinValueDate()) == 0) {
				OverdueChargeRecovery odcr = odcrList.get(odcrList.size() - 1);
				odcr.setPriPaid(odcr.getPriPaid().add(rpd.getFinSchdPriPaid()));
				odcr.setPftPaid(odcr.getPftPaid().add(rpd.getFinSchdPftPaid()));
				odcr.setPenaltyPaid(odcr.getPenaltyPaid().add(rpd.getPenaltyPaid()));
				odcr.setWaivedAmt(odcr.getWaivedAmt().add(rpd.getPenaltyWaived()));
			} else {
				// Add New ODCR record
				OverdueChargeRecovery odcr = resetODCR(fod, rpd.getFinValueDate(), rpd, false);
				odcrList.add(odcr);
			}
		}

		tempRpd = null;

		// ADD TODAY RECORD
		odcrList = addODCRRecord(fod, odcrList, valueDate, false);

		return odcrList;
	}

	private List<OverdueChargeRecovery> addDuePaidHistForDueDateCal(FinODDetails fod, FinanceMain fm, Date valueDate,
			List<FinanceScheduleDetail> fsdList, List<OverdueChargeRecovery> odcrList) {
		// ADD NEW MOVEMENT DATE IF GRACE DAYS TO BE EXCLUDED
		odcrList = addODGraceDate(fod, odcrList, fm);

		// If LPP capitalization required then ADD capitalize dates
		if (FinanceConstants.ODCALON_PIPD_FRQ.equals(fod.getODChargeCalOn())) {
			odcrList = loadFrqCpzDate(fod, fsdList, odcrList, valueDate, fm);
		} else if (FinanceConstants.ODCALON_PIPD_EOM.equals(fod.getODChargeCalOn())) {
			odcrList = loadEOMCpzDate(fod, fsdList, odcrList, valueDate, fm);
		}

		if (StringUtils.equals(fod.getODChargeType(), "E")) {
			// Add Effective Rates
			odcrList = addEffectiveRates(fod, odcrList, fm, valueDate);
		} else {
			// Add Due Days Rates
			odcrList = applyNewEffRate(odcrList, fod.getFinODSchdDate(), fod.getODChargeAmtOrPerc());
		}

		return odcrList;
	}

	private List<OverdueChargeRecovery> addODGraceDate(FinODDetails fod, List<OverdueChargeRecovery> odcrList,
			FinanceMain fm) {

		Date odGrcDate = fod.getFinODSchdDate();
		fod.setOdGrcDate(odGrcDate);
		// If OD days to be included in the calculation then no need to add any record
		if (fod.isODIncGrcDays()) {
			return odcrList;
		}

		// Not overdraft product category
		if (!ProductUtil.isOverDraft(fm.getProductCategory())) {
			if (fod.getODGraceDays() > 0) {
				odGrcDate = DateUtil.addDays(fod.getFinODSchdDate(), fod.getODGraceDays());
				addODCRRecord(fod, odcrList, odGrcDate, false);
				fod.setOdGrcDate(odGrcDate);
			}
			return odcrList;
		}

		// Code reaches here in case of Overdraft loan
		FinODPenaltyRate pr = PenaltyCalculator.getEffectiveRate(fod.getFinODSchdDate(), fm.getPenaltyRates(),
				fod.getODChargeType());

		int netODDays = fod.getODGraceDays() + pr.getOverDraftExtGraceDays();
		if (netODDays > 0) {
			odGrcDate = DateUtil.addDays(fod.getFinODSchdDate(), netODDays);
			addODCRRecord(fod, odcrList, odGrcDate, false);
			fod.setOdGrcDate(odGrcDate);
		}
		return odcrList;

	}

	// This method reset values required for calculation.
	// As we are not saving the ODCR data key fields but not required for calculation also can be ignored
	private OverdueChargeRecovery resetODCR(FinODDetails fod, Date mvtDate, FinanceRepayments rpd, boolean isLpCpz) {
		OverdueChargeRecovery odcr = new OverdueChargeRecovery();
		odcr.setFinID(fod.getFinID());
		odcr.setFinReference(fod.getFinReference());
		odcr.setFinODSchdDate(fod.getFinODSchdDate());
		odcr.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		odcr.setMovementDate(mvtDate);
		odcr.setPriPaid(rpd.getFinSchdPriPaid());
		odcr.setPftPaid(rpd.getFinSchdPftPaid());
		odcr.setPenaltyPaid(rpd.getPenaltyPaid());
		odcr.setWaivedAmt(rpd.getPenaltyWaived());

		// FIXME: GOPAL.P 25FEB2023. Value was set to penaltyAmtPerc Instead of ODChargeAmtOrPerc
		// Commented the code and added new line. PenaltyAmtPerc( usage was not found any where
		// odcr.setPenaltyAmtPerc(fod.getODChargeAmtOrPerc());
		odcr.setoDChargeAmtOrPerc(fod.getODChargeAmtOrPerc());
		odcr.setLpCpz(isLpCpz);
		return odcr;
	}

	// This method reset values required for calculation.
	// As we are not saving the ODCR data key fields but not required for calculation also can be ignored
	private List<OverdueChargeRecovery> addODCRRecord(FinODDetails fod, List<OverdueChargeRecovery> odcrList,
			Date mvtDate, boolean isLpCpz) {

		for (OverdueChargeRecovery odcr : odcrList) {
			if (DateUtil.compare(mvtDate, odcr.getMovementDate()) == 0) {
				if (isLpCpz) {
					odcr.setLpCpz(true);
				}

				return odcrList;
			}
		}

		FinanceRepayments tempRpd = new FinanceRepayments();
		OverdueChargeRecovery odcr = resetODCR(fod, mvtDate, tempRpd, isLpCpz);
		tempRpd = null;
		odcrList.add(odcr);
		LookupMethods.sortODCRByMvtDate(odcrList);
		return odcrList;
	}

	private List<FinanceRepayments> createWorkingRPDList(List<FinanceRepayments> rpdList, Date schdDate) {
		List<FinanceRepayments> rpdwList = new ArrayList<>(1);
		if (rpdList == null || rpdList.size() == 0) {
			return rpdwList;
		}

		for (FinanceRepayments rpd : rpdList) {
			if (DateUtil.compare(rpd.getFinSchdDate(), schdDate) == 0) {
				rpdwList.add(rpd.copyEntity());
			}
		}

		rpdwList = LookupMethods.sortRPDByValueDate(rpdwList);

		return rpdwList;
	}

	private List<OverdueChargeRecovery> addEffectiveRates(FinODDetails fod, List<OverdueChargeRecovery> odcrList,
			FinanceMain fm, Date valueDate) {

		// Loop through Rates history
		List<FinODPenaltyRate> prList = fm.getPenaltyRates();
		for (FinODPenaltyRate pr : prList) {
			// Future change reached then exit
			if (DateUtil.compare(pr.getFinEffectDate(), valueDate) > 0) {
				break;
			}

			// OD charge type is on Effective Rate by Due days
			if (!StringUtils.equals(fod.getODChargeType(), pr.getODChargeType())) {
				continue;
			}

			// Before the installment due date. no need to consider
			if (DateUtil.compare(pr.getFinEffectDate(), fod.getFinODSchdDate()) <= 0) {
				odcrList = applyNewEffRate(odcrList, pr.getFinEffectDate(), pr.getODChargeAmtOrPerc());
				continue;
			}

			// In case of
			if (!StringUtils.equals(fod.getODChargeType(), ChargeType.PERC_ON_EFF_DUE_DAYS)) {
				break;
			}

			// Add ODCR Record
			odcrList = addODCRRecord(fod, odcrList, pr.getFinEffectDate(), false);

			// Apply new rate
			odcrList = applyNewEffRate(odcrList, pr.getFinEffectDate(), pr.getODChargeAmtOrPerc());

		}

		return odcrList;
	}

	// This method reset values required for calculation.
	// As we are not saving the ODCR data key fields but not required for calculation also can be ignored
	private List<OverdueChargeRecovery> applyNewEffRate(List<OverdueChargeRecovery> odcrList, Date mvtDate,
			BigDecimal newRate) {

		for (OverdueChargeRecovery odcr : odcrList) {
			if (DateUtil.compare(mvtDate, odcr.getMovementDate()) > 0) {
				continue;
			}

			odcr.setoDChargeAmtOrPerc(newRate);
		}

		return odcrList;
	}

	private void computeMaxAndCurDetails(FinODDetails fod, List<OverdueChargeRecovery> odcrList, FinanceMain fm,
			List<FinanceScheduleDetail> fsdList) {

		int iFsd = LookupMethods.lookupFSD(fsdList, fod.getFinODSchdDate(), LookupMethods.EQ);
		BigDecimal priDue = BigDecimal.ZERO;
		BigDecimal pftDue = BigDecimal.ZERO;

		if (iFsd >= 0) {
			priDue = fsdList.get(iFsd).getPrincipalSchd();
			pftDue = fsdList.get(iFsd).getProfitSchd();
		}

		// Calculate the Penalty
		for (int iOdcr = 0; iOdcr < odcrList.size(); iOdcr++) {
			OverdueChargeRecovery odcrCur = odcrList.get(iOdcr);
			priDue = priDue.subtract(odcrCur.getPriPaid());
			pftDue = pftDue.subtract(odcrCur.getPftPaid());

			odcrCur.setFinCurODPri(priDue);
			odcrCur.setFinCurODPft(pftDue);
			odcrCur.setFinCurODAmt(priDue.add(pftDue));

			fod.setFinCurODPri(priDue);
			fod.setFinCurODPft(pftDue);
			fod.setFinCurODAmt(odcrCur.getFinCurODAmt());

			if (iOdcr == 0) {
				fod.setFinMaxODPri(priDue);
				fod.setFinMaxODPft(pftDue);
				fod.setFinMaxODAmt(priDue.add(pftDue));
				continue;
			}
		}
	}

	private void calculateLPPAmounts(FinODDetails fod, List<OverdueChargeRecovery> odcrList, FinanceMain fm,
			List<FinanceScheduleDetail> fsdList) {

		fod.setLpCurCpzBal(BigDecimal.ZERO);
		fod.setLpCpzAmount(BigDecimal.ZERO);
		fod.setTotPenaltyAmt(BigDecimal.ZERO);

		String pftDaysBasis = fm.getProfitDaysBasis();
		if (CalculationConstants.IDB_BY_PERIOD.equals(pftDaysBasis)) {
			pftDaysBasis = CalculationConstants.IDB_ACT_365LEAP;
		}

		int iFsd = LookupMethods.lookupFSD(fsdList, fod.getFinODSchdDate(), LookupMethods.EQ);
		BigDecimal priDue = BigDecimal.ZERO;
		BigDecimal pftDue = BigDecimal.ZERO;
		BigDecimal balanceForCal = BigDecimal.ZERO;

		if (iFsd >= 0) {
			priDue = fsdList.get(iFsd).getPrincipalSchd();
			pftDue = fsdList.get(iFsd).getProfitSchd();
		}

		// Calculate the Penalty
		for (int iOdcr = 0; iOdcr < odcrList.size(); iOdcr++) {
			OverdueChargeRecovery odcrCur = odcrList.get(iOdcr);
			priDue = priDue.subtract(odcrCur.getPriPaid());
			pftDue = pftDue.subtract(odcrCur.getPftPaid());

			odcrCur.setFinCurODPri(priDue);
			odcrCur.setFinCurODPft(pftDue);
			odcrCur.setFinCurODAmt(priDue.add(pftDue));

			fod.setFinCurODPri(priDue);
			fod.setFinCurODPft(pftDue);
			fod.setFinCurODAmt(odcrCur.getFinCurODAmt());

			if (iOdcr == 0) {
				fod.setFinMaxODPri(priDue);
				fod.setFinMaxODPft(pftDue);
				fod.setFinMaxODAmt(priDue.add(pftDue));
				continue;
			}

			OverdueChargeRecovery odcrPrv = odcrList.get(iOdcr - 1);

			balanceForCal = findBalanceForLPPCalculation(fod, odcrPrv);

			Date dateCur = odcrCur.getMovementDate();
			Date datePrv = odcrPrv.getMovementDate();

			BigDecimal penalty = BigDecimal.ZERO;

			// If charge calculation Type is Rule Fixed amount by Due Days
			if (ChargeType.RULE.equals(fod.getODChargeType())) {
				penalty = calPenaltyByRule(fod, fm, dateCur, datePrv);
			} else {
				if (!fod.isODIncGrcDays() && DateUtil.compare(odcrCur.getMovementDate(), fod.getOdGrcDate()) <= 0) {
					penalty = BigDecimal.ZERO;
				} else {
					// If charge calculation Type is by Due Days irrespective of one rate OR effective rate
					BigDecimal penaltyRate = odcrPrv.getoDChargeAmtOrPerc().divide(new BigDecimal(100), 2,
							RoundingMode.HALF_DOWN);
					penalty = CalculationUtil.calInterest(datePrv, dateCur, balanceForCal, pftDaysBasis, penaltyRate);
				}
			}

			// Calculate Penalty Amount
			penalty = CalculationUtil.roundAmount(penalty, fm.getCalRoundingMode(), fm.getRoundingTarget());

			odcrCur.setPenalty(penalty);
			odcrCur.setPenaltyBal(penalty.subtract(odcrCur.getPenaltyPaid()).subtract(odcrCur.getWaivedAmt()));

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

			if (odcrCur.isLpCpz()) {
				fod.setLpCpz(odcrCur.isLpCpz());
			}

			fod.setLpCpzAmount(odcrCur.getLpCpzAmount());
			fod.setLpCurCpzBal(odcrCur.getLpCurCpzBal());

			fod.setTotPenaltyAmt(fod.getTotPenaltyAmt().add(penalty));

			if (fod.getFinCurODAmt().compareTo(BigDecimal.ZERO) == 0) {
				break;
			}

		}
	}

	private BigDecimal calPenaltyByRule(FinODDetails fod, FinanceMain fm, Date dateCur, Date datePrv) {
		BigDecimal penalty = BigDecimal.ZERO;

		int dueDays = DateUtil.getDaysBetween(datePrv, dateCur);
		FinanceProfitDetail fpd = financeProfitDetailDAO.getFinProfitDetailsById(fm.getFinID());

		Map<String, Object> datamap = new HashMap<>();
		datamap.put("fm_finType", fod.getFinType());
		datamap.put("fm_finBranch", fod.getFinBranch());
		datamap.put("fin_ODDays", dueDays);
		datamap.put("fin_curODAmt", fod.getFinCurODAmt());

		if (fpd != null) {
			datamap.put("fpd_tdSchdPriBal", fpd.getTdSchdPriBal());
			datamap.put("fpd_tdSchdPftBal", fpd.getTdSchdPftBal());
		}

		datamap.put("fm_productCategory", fm.getProductCategory());
		datamap.put("ft_product", fm.getFinCategory());

		List<ExtendedField> extData = extendedFieldDetailsService.getExtndedFieldDetails(
				ExtendedFieldConstants.MODULE_LOAN, fm.getFinCategory(), FinServiceEvent.ORG, fm.getFinReference());

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

		return penalty;
	}

	private BigDecimal findBalanceForLPPCalculation(FinODDetails fod, OverdueChargeRecovery odcrPrv) {
		BigDecimal balanceForCal;
		// Derive Balance for Penalty Calculation
		switch (fod.getODChargeCalOn()) {
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
		return balanceForCal;
	}

	private List<OverdueChargeRecovery> loadFrqCpzDate(FinODDetails fod, List<FinanceScheduleDetail> fsdList,
			List<OverdueChargeRecovery> odcrList, Date valueDate, FinanceMain fm) {
		Date odSchdDate = odcrList.get(0).getFinODSchdDate();
		String frq = null;

		// Find index of schedule where scheulde date >= OD schedule date to avoid multiple loops
		int idxLatest = LookupMethods.lookupFSD(fsdList, odSchdDate, LookupMethods.GE);

		// Loop through the scheule from OD schedule date to current value date
		for (int iFsd = idxLatest; iFsd < fsdList.size(); iFsd++) {
			FinanceScheduleDetail fsd = fsdList.get(iFsd);

			if (DateUtil.compare(fsd.getSchDate(), valueDate) >= 0) {
				break;
			}

			if (fm.isAllowGrcPeriod() && DateUtil.compare(fsd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
				frq = fm.getGrcPftFrq();
			} else {
				frq = fm.getRepayPftFrq();
			}

			// Capitalize only on due frequency date
			if (!FrequencyUtil.isFrqDate(frq, fsd.getSchDate())) {
				continue;
			}

			// Add new ODCR record with Capitalize
			addODCRRecord(fod, odcrList, fsd.getSchDate(), true);

		}

		return odcrList;
	}

	/*
	 * Initial Development Penal Calculation on a month end basis.(22-07-2020), Revision id :144757
	 * 
	 * On 11-08-2020, we switched from a month end basis to start of the month, Revision id :144857
	 * 
	 * Commit comment: DE#87:Penal Calculation: In the loan type If the OD Interest Calculated is “Total OD amount, P+I
	 * in Past due with Cpz on EOM” application is calculating Penal on month end basis, now it is changed to start of
	 * month.
	 */

	private List<OverdueChargeRecovery> loadEOMCpzDate(FinODDetails fod, List<FinanceScheduleDetail> schedules,
			List<OverdueChargeRecovery> odcrList, Date valueDate, FinanceMain fm) {

		Date newCpzDate = odcrList.get(0).getFinODSchdDate();
		newCpzDate = DateUtil.getMonthEnd(newCpzDate);
		newCpzDate = DateUtil.addDays(newCpzDate, 1);

		if (DateUtil.compare(newCpzDate, valueDate) >= 0) {
			return odcrList;
		}

		while (DateUtil.compare(newCpzDate, valueDate) < 0) {
			// Add new ODCR record with Capitalize
			addODCRRecord(fod, odcrList, newCpzDate, true);
			newCpzDate = DateUtil.getMonthEnd(newCpzDate);
			newCpzDate = DateUtil.addDays(newCpzDate, 1);
		}

		return odcrList;

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

		if (CollectionUtils.isNotEmpty(repayments)) {
			Collections.sort(repayments, new Comparator<>() {
				@Override
				public int compare(FinanceRepayments detail1, FinanceRepayments detail2) {
					return DateUtil.compare(detail1.getFinValueDate(), detail2.getFinValueDate());
				}
			});
		}

		return repayments;
	}

	public List<OverdueChargeRecovery> sortOdcrListByValueDate(List<OverdueChargeRecovery> odcrList) {

		if (CollectionUtils.isNotEmpty(odcrList)) {
			Collections.sort(odcrList, new Comparator<>() {
				@Override
				public int compare(OverdueChargeRecovery detail1, OverdueChargeRecovery detail2) {
					return DateUtil.compare(detail1.getMovementDate(), detail2.getMovementDate());
				}
			});
		}

		return odcrList;
	}

	private Date deriveValueDate(FinODDetails fod, Date valueDate, List<OverdueChargeRecovery> odcrList,
			List<FinanceRepayments> repayments) {
		boolean flag = true;

		if (odcrList.isEmpty()) {
			return valueDate;
		}

		Date finValueDate = odcrList.get(odcrList.size() - 1).getMovementDate();
		Date movementDate = odcrList.get(odcrList.size() - 1).getMovementDate();

		if (CollectionUtils.isNotEmpty(repayments)) {
			finValueDate = repayments.get(repayments.size() - 1).getFinValueDate();
			movementDate = odcrList.get(0).getMovementDate();
		}

		if (DateUtil.compare(movementDate, finValueDate) == 0) {
			flag = false;
		}

		for (int iOdcr = 0; iOdcr < odcrList.size(); iOdcr++) {
			OverdueChargeRecovery odcrCur = odcrList.get(iOdcr);

			if (iOdcr == 0 && flag) {
				continue;
			}

			if (fod.getFinCurODAmt().compareTo(BigDecimal.ZERO) == 0) {
				return odcrCur.getMovementDate();
			}
		}

		return valueDate;
	}
}
