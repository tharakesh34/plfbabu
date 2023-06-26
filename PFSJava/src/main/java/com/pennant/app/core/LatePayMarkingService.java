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
 * FileName : LatePayMarkingService.java *
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.LookupMethods;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.finance.FinODAmzTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODAmzTaxDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.InvoiceDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.GSTInvoiceTxnService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.eod.cache.FeeTypeConfigCache;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennanttech.pff.overdue.constants.PenaltyCalculator;
import com.pennanttech.pff.receipt.constants.Allocation;

public class LatePayMarkingService extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(LatePayMarkingService.class);

	private LatePayInterestService latePayInterestService;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private CustomerAddresDAO customerAddresDAO;
	private FinODAmzTaxDetailDAO finODAmzTaxDetailDAO;
	private GSTInvoiceTxnService gstInvoiceTxnService;

	/**
	 * Default constructor
	 */
	public LatePayMarkingService() {
		super();
	}

	public void calPDOnBackDatePayment(FinanceMain fm, List<FinODDetails> fodList, Date valueDate,
			List<FinanceScheduleDetail> schedules, List<FinanceRepayments> rpdList, boolean calcwithoutDue,
			boolean zeroIfpaid) {
		logger.debug(Literal.ENTERING);

		EventProperties eventProperties = fm.getEventProperties();

		Date appDate = null;
		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		fm.setAppDate(appDate);

		// IF LOAN CREATED BACK DATED AND IT COMES FOR RECEIPT ON DAY 1 BEFORE EOD FOD WILL BE EMPTY
		fodList = findInstDueButFODNotFound(fm, fodList, valueDate, schedules);

		for (FinODDetails fod : fodList) {
			// Find OD schedule date from FSD list. In theoretical scenario index must found
			int fsdIdx = LookupMethods.lookupFSD(schedules, fod.getFinODSchdDate(), LookupMethods.EQ);
			FinanceScheduleDetail curSchd = null;
			if (fsdIdx >= 0) {
				curSchd = schedules.get(fsdIdx);
			}

			if (curSchd == null) {
				// if there is no schedule for od now then there is no penalty
				fod.setFinODTillDate(valueDate);
				fod.setFinCurODPri(BigDecimal.ZERO);
				fod.setFinCurODPft(BigDecimal.ZERO);
				fod.setFinCurODAmt(BigDecimal.ZERO);
				fod.setFinCurODDays(0);
				// TODO ###124902 - New field to be included for future use which stores the last payment date. This
				// needs to be worked.
				fod.setFinLMdfDate(appDate);
				fod.setTotPenaltyAmt(BigDecimal.ZERO);
				fod.setTotPenaltyBal(
						fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
				continue;
			}

			if (calcwithoutDue) {
				latePayMarking(fm, fod, schedules, rpdList, curSchd, valueDate, valueDate, false);

				if (DateUtil.compare(valueDate, curSchd.getSchDate()) <= 0) {
					resetLPPToZero(fod, curSchd, rpdList, zeroIfpaid, valueDate);
				}

				continue;
			}

			// check due and proceed
			boolean isAmountDue = false;
			if (ImplementationConstants.ALLOW_OLDEST_DUE) {
				isAmountDue = isOldestDueOverDue(curSchd);
			}
			// Paid Principal OR Paid Interest Less than scheduled amounts
			if (curSchd.getSchdPriPaid().compareTo(curSchd.getPrincipalSchd()) < 0
					|| curSchd.getSchdPftPaid().compareTo(curSchd.getProfitSchd()) < 0) {
				isAmountDue = true;
			}

			if (isAmountDue) {
				latePayMarking(fm, fod, schedules, rpdList, curSchd, valueDate, valueDate, false);
			}

			if (DateUtil.compare(valueDate, curSchd.getSchDate()) <= 0) {
				resetLPPToZero(fod, curSchd, rpdList, zeroIfpaid, valueDate);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	public List<FinODDetails> findInstDueButFODNotFound(FinanceMain fm, List<FinODDetails> fodList, Date valueDate,
			List<FinanceScheduleDetail> fsdList) {
		if (fodList != null && fodList.size() > 0) {
			return fodList;
		}

		if (fodList == null) {
			fodList = new ArrayList<>();
		}

		for (FinanceScheduleDetail fsd : fsdList) {
			if (DateUtil.compare(fsd.getSchDate(), valueDate) >= 0) {
				break;
			}

			if (!fsd.isFrqDate()) {
				continue;
			}

			if (fsd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			FinODDetails fod = createODDetails(fsd, fm, BigDecimal.ZERO);
			fodList.add(fod);

		}

		return fodList;
	}

	public void resetMaxODAmount(List<FinanceRepayments> repayments, FinODDetails fod, FinanceScheduleDetail curSchd) {
		fod.setFinMaxODPri(curSchd.getPrincipalSchd());
		fod.setFinMaxODPft(curSchd.getProfitSchd());

		if (repayments == null || repayments.size() == 0) {
			fod.setFinMaxODAmt(fod.getFinMaxODPft().add(fod.getFinMaxODPri()));
			return;
		}

		for (FinanceRepayments repayment : repayments) {
			// Check the payment made against the actual schedule date
			if (repayment.getFinSchdDate().compareTo(fod.getFinODSchdDate()) != 0) {
				continue;
			}

			// MAx OD amounts is same as repayments balance amounts
			fod.setFinMaxODPri(fod.getFinMaxODPri().subtract(repayment.getFinSchdPriPaid()));
			fod.setFinMaxODPft(fod.getFinMaxODPft().subtract(repayment.getFinSchdPftPaid()));
		}
		fod.setFinMaxODAmt(fod.getFinMaxODPft().add(fod.getFinMaxODPri()));
	}

	private void resetLPPToZero(FinODDetails fod, FinanceScheduleDetail schd, List<FinanceRepayments> rpdList,
			boolean reset, Date valueDate) {
		logger.debug(Literal.ENTERING);

		BigDecimal totalDue = schd.getProfitSchd().add(schd.getPrincipalSchd()).subtract(schd.getSchdPftPaid())
				.subtract(schd.getSchdPriPaid());

		BigDecimal totPaidBefSchDate = BigDecimal.ZERO;
		for (FinanceRepayments repayment : rpdList) {
			if (repayment.getFinSchdDate().compareTo(fod.getFinODSchdDate()) == 0
					&& repayment.getFinValueDate().compareTo(fod.getFinODSchdDate()) <= 0) {
				totPaidBefSchDate = totPaidBefSchDate.add(repayment.getFinSchdPriPaid())
						.add(repayment.getFinSchdPftPaid());
			}
		}

		if (fod.getFinCurODPri().add(fod.getFinCurODPft()).compareTo(BigDecimal.ZERO) > 0) {
			reset = false;
		}

		if (totPaidBefSchDate.compareTo(totalDue) >= 0 || schd.getSchDate().compareTo(valueDate) >= 0) {
			fod.setFinCurODDays(0);
			if (reset) {
				fod.setTotPenaltyAmt(BigDecimal.ZERO);
				fod.setTotPenaltyBal(
						fod.getTotPenaltyAmt().subtract(fod.getTotPenaltyPaid()).subtract(fod.getTotWaived()));
				fod.setLPIAmt(BigDecimal.ZERO);
				fod.setLPIBal(fod.getLPIAmt().subtract(fod.getLPIPaid()).subtract(fod.getLPIWaived()));
			}
		}

		logger.debug(Literal.LEAVING);

	}

	public void processLatePayMarking(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			if (finEODEvent.getIdxPD() <= 0) {
				continue;
			}

			findLatePay(finEODEvent, custEODEvent);
		}
	}

	public void findLatePay(FinEODEvent finEODEvent, CustEODEvent custEODEvent) {
		Date valueDate = custEODEvent.getEodValueDate();

		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		// Get details first time from DB
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		Date finStartDate = fm.getFinStartDate();

		logger.info("Checking penalties for the FinID >> {}", finID);

		List<FinODDetails> finODdetails = finODDetailsDAO.getFinODDByFinRef(finID, finStartDate);
		finEODEvent.setFinODDetails(finODdetails);
		finEODEvent.setFinODDetailsLBD(new ArrayList<>(finODdetails));

		EventProperties ep = fm.getEventProperties();
		Date appDate = null;
		if (ep.isParameterLoaded()) {
			appDate = ep.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		fm.setAppDate(appDate);

		List<FinODPenaltyRate> penaltyRates = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finID, "");

		if (CollectionUtils.isEmpty(penaltyRates)) {
			logger.warn("Penalty rate not found.");
			FinODPenaltyRate penaltyRate = new FinODPenaltyRate();
			penaltyRates.add(penaltyRate);
		}

		fm.setPenaltyRates(penaltyRates);

		Date lppCheckingDate = valueDate;
		if (ImplementationConstants.LP_MARK_FIRSTDAY) {
			logger.info("Including today in Late payment Calculation.");
			lppCheckingDate = DateUtil.addDays(valueDate, 1);
		}

		for (FinanceScheduleDetail schd : schedules) {
			if (schd.getSchDate().compareTo(lppCheckingDate) >= 0) {
				break;
			}

			// Checking the respective schedule has Overdue or Not
			boolean isAmountDue = false;

			if (ImplementationConstants.ALLOW_OLDEST_DUE) {
				isAmountDue = isOldestDueOverDue(schd);
			}

			// Paid Principal OR Paid Interest Less than scheduled amounts
			BigDecimal principalSchd = schd.getPrincipalSchd();
			BigDecimal schdPriPaid = schd.getSchdPriPaid();
			BigDecimal profitSchd = schd.getProfitSchd();
			BigDecimal schdPftPaid = schd.getSchdPftPaid();

			BigDecimal remBalncAmount = BigDecimal.ZERO;

			if (ProductUtil.isOverDraftChargeReq(fm)) {
				String custBranch = null;
				if (custEODEvent != null && custEODEvent.getCustomer() != null) {
					custBranch = custEODEvent.getCustomer().getCustAddrProvince();
				} else {
					custBranch = customerAddresDAO.getCustHighPriorityAddr(fm.getCustID());
				}

				remBalncAmount = overdrafLoanService.calculateODAmounts(fm, schd.getSchDate(), custBranch)
						.getCurOverdraftTxnChrg();
			}

			if (schdPriPaid.compareTo(principalSchd) < 0 || schdPftPaid.compareTo(profitSchd) < 0
					|| remBalncAmount.compareTo(BigDecimal.ZERO) > 0) {
				isAmountDue = true;
			}

			FinODDetails fod = findExisingOD(finEODEvent.getFinODDetails(), schd);

			// No current Overdue and No Previous Overdue
			if (!isAmountDue && fod == null) {
				continue;
			}

			// No current Overdue But Previous Overdue, check LPP balance for CPZ method
			if (!isAmountDue && fod != null) {
				isAmountDue = isLPCpzRequired(fod);
			}

			// Current Overdue and No Previous Overdue, create OD create
			if (isAmountDue && fod == null) {
				fod = createODDetails(schd, fm, remBalncAmount);
				finEODEvent.getFinODDetails().add(fod);
			}

			if (isAmountDue) {
				Date penaltyCalDate = valueDate;
				if (ImplementationConstants.LPP_CALC_SOD) {
					penaltyCalDate = DateUtil.addDays(valueDate, 1);
					logger.info("Calculating penalty on SOD basis.");
				}
				// TODO ###124902 - New field to be included for future use which stores the last payment date. This
				// needs to be worked.
				fod.setFinLMdfDate(appDate);
				logger.info("Calculating penalty from {}", penaltyCalDate);

				latePayMarking(fm, fod, schedules, null, schd, penaltyCalDate, valueDate, true);
			}
		}

		lppAccrualProcess(custEODEvent, finEODEvent);
		updateFinPftDetails(finEODEvent.getFinProfitDetail(), finEODEvent.getFinODDetails(), valueDate);

		logger.info("Checking penalties for the FinReference {} completed.", finReference);
	}

	public FinODDetails findExisingOD(List<FinODDetails> fodList, FinanceScheduleDetail curSchd) {
		for (FinODDetails finODDetails : fodList) {
			if (finODDetails.getFinODSchdDate().compareTo(curSchd.getSchDate()) == 0) {
				return finODDetails;
			}
		}

		return null;
	}

	public boolean isLPCpzRequired(FinODDetails fod) {
		if (!ChargeType.PERC_ON_DUE_DAYS.equals(fod.getODChargeType())) {
			return false;
		}

		String odChargeCalOn = fod.getODChargeCalOn();
		if (!FinanceConstants.ODCALON_PIPD_FRQ.equals(odChargeCalOn)
				&& !FinanceConstants.ODCALON_PIPD_EOM.equals(odChargeCalOn)) {
			return false;
		}

		if (fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) == 0) {
			return false;
		}

		return true;
	}

	public void lppAccrualProcess(CustEODEvent custEODEvent, FinEODEvent finEODEvent) {
		if (!ImplementationConstants.LPP_GST_DUE_ON.equals("D")) {
			return;
		}

		List<FinODDetails> fodList = finEODEvent.getFinODDetails();

		// Due Basis LPP Accrual Postings
		boolean isExists = false;
		for (FinODDetails fod : fodList) {
			if (fod.getTotPenaltyBal().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			isExists = finODAmzTaxDetailDAO.isDueCreatedForDate(fod.getFinID(), fod.getFinODSchdDate(), "LPP");

			if (!isExists) {
				postLppAccruals(finEODEvent, custEODEvent, fod);
			}
		}
	}

	public void latePayMarking(FinanceMain fm, FinODDetails fod, List<FinanceScheduleDetail> schedules,
			List<FinanceRepayments> repayments, FinanceScheduleDetail curSchd, Date penaltyCalDate, Date valueDate,
			boolean isEODprocess) {

		if (fod.isLockODRecalCal()) {
			return;
		}

		BigDecimal overallBalAmount = BigDecimal.ZERO;
		BigDecimal maxOdAmount = BigDecimal.ZERO;
		List<ManualAdviseMovements> movements = new ArrayList<>();

		fod.setFinODTillDate(penaltyCalDate);
		fod.setFinCurODPri(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));

		fod.setFinCurODPft(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		fod.setFinCurODAmt(fod.getFinCurODPft().add(fod.getFinCurODPri()));

		fod.setFinMaxODPri(curSchd.getPrincipalSchd());
		fod.setFinMaxODPft(curSchd.getProfitSchd());
		fod.setFinODTillDate(penaltyCalDate);

		String productCategory = fm.getProductCategory();
		String finReference = fm.getFinReference();
		long finID = fm.getFinID();

		int grcDays = 0;
		Date schDate = fod.getFinODSchdDate();
		List<FinODPenaltyRate> penaltyRates = fm.getPenaltyRates();

		if (ProductUtil.isOverDraft(fm)) {
			movements = manualAdviseDAO.getAdviseMovements(finID, fod.getFinODSchdDate(), "_AView");
			String custBranch = customerAddresDAO.getCustHighPriorityAddr(fm.getCustID());
			overallBalAmount = overdrafLoanService.calculateODAmounts(fm, schDate, custBranch).getCurOverdraftTxnChrg();
			grcDays = overdrafLoanService.getGraceDays(fm);
		} else {
			grcDays = fod.getODGraceDays();
			if (CollectionUtils.isEmpty(penaltyRates)) {
				penaltyRates = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finID, "");
				fm.setPenaltyRates(penaltyRates);
			}

		}

		fod.setCurOverdraftTxnChrg(overallBalAmount);
		fod.setMaxOverdraftTxnChrg(maxOdAmount);

		Date grcDate = DateUtil.addDays(schDate, grcDays);

		if (repayments == null) {
			repayments = financeRepaymentsDAO.getByFinRefAndSchdDate(finID, schDate);
		} else {
			repayments = LookupMethods.sortRPDByValueDate(repayments);
		}

		// FIXME: 24FEB23. WHY THE BELOW CODE IS REQUIRED.
		/*
		 * for (FinanceRepayments rpd : repayments) {
		 * 
		 * // check the payment made against the actual schedule date Date finSchdDate = rpd.getFinSchdDate(); if
		 * (finSchdDate.compareTo(schDate) != 0) { continue; }
		 * 
		 * // Max OD amounts is same as rpdList balance amounts Date finValueDate = rpd.getFinValueDate(); if
		 * (finSchdDate.compareTo(finValueDate) == 0) {
		 * fod.setFinMaxODPri(fod.getFinMaxODPri().subtract(rpd.getFinSchdPriPaid()));
		 * fod.setFinMaxODPft(fod.getFinMaxODPft().subtract(rpd.getFinSchdPftPaid())); }
		 * 
		 * }
		 */

		if (ProductUtil.isOverDraft(productCategory)) {
			BigDecimal txnChrg = overdrafLoanService.getTransactionCharge(movements, schDate, grcDate);
			fod.setMaxOverdraftTxnChrg(fod.getMaxOverdraftTxnChrg().subtract(txnChrg));
		}

		// FIXME: 24FEB23. WHY THE BELOW CODE IS REQUIRED.

		fod.setFinMaxODAmt(fod.getFinMaxODPft().add(fod.getFinMaxODPri()).add(fod.getMaxOverdraftTxnChrg()));
		Date odtCaldate = penaltyCalDate;
		if (ImplementationConstants.LP_MARK_FIRSTDAY && isEODprocess) {
			odtCaldate = DateUtil.addDays(penaltyCalDate, 1);
		}

		fod.setFinCurODDays(DateUtil.getDaysBetween(fod.getFinODSchdDate(), odtCaldate));

		EventProperties eventProperties = fm.getEventProperties();

		// TODO ###124902 - New field to be included for future use which stores the last payment date. This needs to be
		// worked.
		if (eventProperties.isParameterLoaded()) {
			fod.setFinLMdfDate(eventProperties.getAppDate());
		} else {
			fod.setFinLMdfDate(SysParamUtil.getAppDate());
		}

		latePayPenaltyService.computeLPP(fod, penaltyCalDate, fm, schedules, repayments);

		String lpiMethod = fm.getPastduePftCalMthd();

		if (StringUtils.isEmpty(lpiMethod)) {
			logger.info("LPFT Method value not available for Loan Reference {}", finReference);
		} else {
			if (!CalculationConstants.PDPFTCAL_NOTAPP.equals(lpiMethod)) {
				latePayInterestService.computeLPI(fod, penaltyCalDate, fm, schedules, repayments);
			}
		}
	}

	public void updateFinPftDetails(FinanceProfitDetail pftDetail, List<FinODDetails> fodList, Date valueDate) {

		pftDetail.setODPrincipal(BigDecimal.ZERO);
		pftDetail.setODProfit(BigDecimal.ZERO);
		pftDetail.setPenaltyPaid(BigDecimal.ZERO);
		pftDetail.setPenaltyDue(BigDecimal.ZERO);
		pftDetail.setPenaltyWaived(BigDecimal.ZERO);
		pftDetail.setPrvODDate(valueDate);
		pftDetail.setNOODInst(0);
		pftDetail.setCurODDays(0);

		pftDetail.setTotPftOnPD(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDDue(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDPaid(BigDecimal.ZERO);
		pftDetail.setTotPftOnPDWaived(BigDecimal.ZERO);
		pftDetail.setLpiAmount(BigDecimal.ZERO);

		for (FinODDetails fod : fodList) {
			pftDetail.setODPrincipal(pftDetail.getODPrincipal().add(fod.getFinCurODPri()));
			pftDetail.setODProfit(pftDetail.getODProfit().add(fod.getFinCurODPft()));

			pftDetail.setPenaltyPaid(pftDetail.getPenaltyPaid().add(fod.getTotPenaltyPaid()));
			pftDetail.setPenaltyDue(pftDetail.getPenaltyDue().add(fod.getTotPenaltyBal()));
			pftDetail.setPenaltyWaived(pftDetail.getPenaltyWaived().add(fod.getTotWaived()));
			pftDetail.setTotPftOnPD(pftDetail.getTotPftOnPD().add(fod.getLPIAmt()));
			pftDetail.setTotPftOnPDDue(pftDetail.getTotPftOnPDDue().add(fod.getLPIBal()));
			pftDetail.setTotPftOnPDPaid(pftDetail.getTotPftOnPDPaid().add(fod.getLPIPaid()));
			pftDetail.setTotPftOnPDWaived(pftDetail.getTotPftOnPDWaived().add(fod.getLPIWaived()));
			pftDetail.setLpiAmount(pftDetail.getLpiAmount().add(fod.getLPIAmt()));

			if (pftDetail.getFirstODDate() == null
					|| pftDetail.getFirstODDate().compareTo(pftDetail.getFinStartDate()) == 0) {
				pftDetail.setFirstODDate(fod.getFinODSchdDate());
			}

			// There is chance OD dates might not be in ascending order so take the least date
			if (fod.getFinCurODAmt().compareTo(BigDecimal.ZERO) > 0
					&& fod.getFinODSchdDate().compareTo(pftDetail.getPrvODDate()) <= 0) {
				pftDetail.setPrvODDate(fod.getFinODSchdDate());
			}

			if (pftDetail.getCurODDays() < fod.getFinCurODDays()) {
				pftDetail.setCurODDays(fod.getFinCurODDays());
			}

			pftDetail.setNOODInst(pftDetail.getNOODInst() + 1);
		}

		if (pftDetail.getCurODDays() > pftDetail.getMaxODDays()) {
			pftDetail.setMaxODDays(pftDetail.getCurODDays());
		}

	}

	public void processCustomerStatus(CustEODEvent custEODEvent) {
		Date valueDate = custEODEvent.getEodValueDate();
		String newBucketCode = FinanceConstants.FINSTSRSN_SYSTEM;
		int maxBuckets = 0;

		Customer customer = custEODEvent.getCustomer();
		String currentBucketCode = StringUtils.trimToEmpty(customer.getCustSts());

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();
			if (fm.getDueBucket() > maxBuckets) {
				maxBuckets = fm.getDueBucket();
				newBucketCode = fm.getFinStatus();
			}
		}

		if (!StringUtils.equals(newBucketCode, currentBucketCode)) {
			custEODEvent.setUpdCustomer(true);
			custEODEvent.getCustomer().setCustSts(newBucketCode);
			custEODEvent.getCustomer().setCustStsChgDate(valueDate);
		}
	}

	public void processLatePayAccrual(CustEODEvent custEODEvent) {
		logger.debug(Literal.ENTERING);

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {

			if (finEODEvent.getIdxPD() <= 0) {
				continue;
			}

			latePayPenaltyService.postLatePayAccruals(finEODEvent, custEODEvent);
		}

		logger.debug(Literal.LEAVING);
	}

	public CustEODEvent processLPIAccrual(CustEODEvent custEODEvent) {
		for (FinEODEvent finEODEvent : custEODEvent.getFinEODEvents()) {

			if (finEODEvent.getIdxPD() <= 0) {
				continue;
			}

			latePayInterestService.postLPIAccruals(finEODEvent, custEODEvent);
		}
		return custEODEvent;
	}

	private FinODDetails createODDetails(FinanceScheduleDetail schd, FinanceMain fm, BigDecimal balanceAmount) {
		FinODDetails finOD = new FinODDetails();

		Date valueDate = fm.getAppDate();

		FinODPenaltyRate pr = PenaltyCalculator.getEffectiveRate(valueDate, fm.getPenaltyRates());

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		finOD.setFinID(finID);
		finOD.setFinReference(finReference);
		finOD.setFinODSchdDate(schd.getSchDate());
		finOD.setFinODFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		finOD.setFinBranch(fm.getFinBranch());
		finOD.setFinType(fm.getFinType());
		finOD.setCustID(fm.getCustID());
		finOD.setFinODTillDate(valueDate);
		finOD.setRcdAction("I");

		finOD.setFinCurODPri(schd.getPrincipalSchd().subtract(schd.getSchdPriPaid()).add(schd.getCpzBalance()));
		finOD.setFinCurODPft(schd.getProfitSchd().subtract(schd.getSchdPftPaid()));
		finOD.setCurOverdraftTxnChrg(balanceAmount);
		finOD.setFinCurODAmt(finOD.getFinCurODPft().add(finOD.getFinCurODPri()).add(finOD.getCurOverdraftTxnChrg()));
		finOD.setFinMaxODPri(finOD.getFinCurODPri());
		finOD.setFinMaxODPft(finOD.getFinCurODPft());
		finOD.setMaxOverdraftTxnChrg(finOD.getCurOverdraftTxnChrg());
		finOD.setFinMaxODAmt(finOD.getFinMaxODPft().add(finOD.getFinMaxODPri()).add(finOD.getMaxOverdraftTxnChrg()));

		finOD.setFinCurODDays(DateUtil.getDaysBetween(finOD.getFinODSchdDate(), valueDate));
		// TODO ###124902 - New field to be included for future use which stores the last payment date. This needs to be
		// worked.

		finOD.setFinLMdfDate(valueDate);

		finOD.setApplyODPenalty(pr.isApplyODPenalty());
		finOD.setODIncGrcDays(pr.isODIncGrcDays());
		finOD.setODChargeType(pr.getODChargeType());
		finOD.setODGraceDays(pr.getODGraceDays());
		finOD.setODChargeCalOn(pr.getODChargeCalOn());
		finOD.setODChargeAmtOrPerc(getDecimal(pr.getODChargeAmtOrPerc()));
		finOD.setODAllowWaiver(pr.isODAllowWaiver());
		finOD.setODMaxWaiverPerc(pr.getODMaxWaiverPerc());
		finOD.setLpCpz(false);
		finOD.setLpCpzAmount(BigDecimal.ZERO);
		finOD.setLpCurCpzBal(BigDecimal.ZERO);
		finOD.setOdMinAmount(pr.getOdMinAmount());

		return finOD;
	}

	private void postLppAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent, FinODDetails fod) {
		logger.info(Literal.ENTERING);

		String eventCode = AccountingEvent.LPPAMZ;
		FinanceProfitDetail pfd = finEODEvent.getFinProfitDetail();
		FinanceMain fm = finEODEvent.getFinanceMain();

		Long accountingID = getAccountingID(fm, eventCode);
		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			return;
		}

		// Setting LPP Amount from Overdue Details for LPP amortization

		FeeType lppFeeType = null;
		BigDecimal penaltyDue = fod.getTotPenaltyBal();
		BigDecimal penaltyDueGst = BigDecimal.ZERO;

		// Prepare Finance Detail
		FinanceDetail detail = new FinanceDetail();
		detail.getFinScheduleData().setFinanceMain(fm);
		detail.getFinScheduleData().setFinanceType(finEODEvent.getFinType());
		prepareFinanceDetail(detail, custEODEvent);

		// Calculate GSTAmount
		boolean gstCalReq = false;

		// LPP GST Amount calculation
		if (penaltyDue.compareTo(BigDecimal.ZERO) > 0) {

			if (EODUtil.isEod()) {
				lppFeeType = FeeTypeConfigCache.getCacheFeeTypeByCode(Allocation.ODC);
			} else {
				lppFeeType = feeTypeDAO.getTaxDetailByCode(Allocation.ODC);
			}

			if (lppFeeType != null) {
				if (lppFeeType.isTaxApplicable()) {
					gstCalReq = true;
				}
				if (!lppFeeType.isAmortzReq()) {
					penaltyDue = BigDecimal.ZERO;
				}
			} else {
				penaltyDue = BigDecimal.ZERO;
			}
		}

		if (penaltyDue.compareTo(BigDecimal.ZERO) == 0) {
			logger.info("Penalty Due is zero");
			return;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, pfd, finEODEvent.getFinanceScheduleDetails(), eventCode,
				custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		aeEvent.getAeAmountCodes().setdLPPAmz(penaltyDue);
		aeEvent.setDataMap(aeEvent.getAeAmountCodes().getDeclaredFieldValues());

		Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(fm.getFinID());

		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					aeEvent.getDataMap().put(key, gstExecutionMap.get(key));
				}
			}
		}

		Map<String, BigDecimal> taxPercmap = null;
		// IF GST Calculation Required for LPI or LPP
		if (gstCalReq) {
			taxPercmap = GSTCalculator.getTaxPercentages(gstExecutionMap, fm.getFinCcy());

			// Calculate LPP GST Amount
			penaltyDueGst = getTotalTaxAmount(taxPercmap, penaltyDue, lppFeeType.getTaxComponent());
		}

		// LPI GST Amount for Postings
		Map<String, BigDecimal> calGstMap = new HashMap<>();
		boolean addGSTInvoice = false;

		// LPP GST Amount for Postings
		long finID = pfd.getFinID();
		String finReference = pfd.getFinReference();
		if (penaltyDue.compareTo(BigDecimal.ZERO) > 0 && lppFeeType != null && lppFeeType.isTaxApplicable()) {

			if (taxPercmap == null) {
				taxPercmap = GSTCalculator.getTaxPercentages(gstExecutionMap, fm.getFinCcy());
			}

			FinODAmzTaxDetail taxDetail = getTaxDetail(taxPercmap, penaltyDueGst, lppFeeType.getTaxComponent());
			taxDetail.setFinID(finID);
			taxDetail.setFinReference(finReference);
			taxDetail.setTaxFor("LPP");
			taxDetail.setAmount(penaltyDue);
			taxDetail.setValueDate(fod.getFinODSchdDate());
			taxDetail.setPostDate(new Timestamp(System.currentTimeMillis()));

			calGstMap.put("LPP_CGST_R", taxDetail.getCGST());
			calGstMap.put("LPP_SGST_R", taxDetail.getSGST());
			calGstMap.put("LPP_UGST_R", taxDetail.getUGST());
			calGstMap.put("LPP_IGST_R", taxDetail.getIGST());
			calGstMap.put("LPP_CESS_R", taxDetail.getCESS());

			// Save Tax Details
			finODAmzTaxDetailDAO.save(taxDetail);

			EventProperties eventProperties = fm.getEventProperties();
			if (eventProperties.isParameterLoaded()) {
				addGSTInvoice = eventProperties.isGstInvOnDue();
			} else {
				addGSTInvoice = SysParamUtil.isAllowed(SMTParameterConstants.GST_INV_ON_DUE);
			}
		} else {
			addZeroifNotContains(calGstMap, "LPP_CGST_R");
			addZeroifNotContains(calGstMap, "LPP_SGST_R");
			addZeroifNotContains(calGstMap, "LPP_UGST_R");
			addZeroifNotContains(calGstMap, "LPP_IGST_R");
			addZeroifNotContains(calGstMap, "LPP_CESS_R");
		}

		// GST Details
		if (calGstMap != null) {
			aeEvent.getDataMap().putAll(calGstMap);
		}

		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());

		// Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);

		// GST Invoice Preparation
		if (aeEvent.getLinkedTranId() > 0) {

			// LPP Receivable Data Update for Future Accounting
			if (penaltyDue.compareTo(BigDecimal.ZERO) > 0) {

				// Save Tax Receivable Details
				FinTaxReceivable taxRcv = finODAmzTaxDetailDAO.getFinTaxReceivable(finID, "LPP");
				boolean isSave = false;
				if (taxRcv == null) {
					taxRcv = new FinTaxReceivable();
					taxRcv.setFinID(finID);
					taxRcv.setFinReference(finReference);
					taxRcv.setTaxFor("LPP");
					isSave = true;
				}

				if (calGstMap != null) {
					taxRcv.setCGST(taxRcv.getCGST().add(calGstMap.get("LPP_CGST_R")));
					taxRcv.setSGST(taxRcv.getSGST().add(calGstMap.get("LPP_SGST_R")));
					taxRcv.setUGST(taxRcv.getUGST().add(calGstMap.get("LPP_UGST_R")));
					taxRcv.setIGST(taxRcv.getIGST().add(calGstMap.get("LPP_IGST_R")));
					taxRcv.setCESS(taxRcv.getCESS().add(calGstMap.get("LPP_CESS_R")));
				}

				taxRcv.setReceivableAmount(taxRcv.getReceivableAmount().add(penaltyDue));

				if (isSave) {
					finODAmzTaxDetailDAO.saveTaxReceivable(taxRcv);
				} else {
					finODAmzTaxDetailDAO.updateTaxReceivable(taxRcv);
				}
			}

			// GST Invoice Generation
			if (addGSTInvoice) {
				List<FinFeeDetail> feesList = prepareFeesList(lppFeeType, taxPercmap, calGstMap, penaltyDue);
				if (CollectionUtils.isNotEmpty(feesList)) {
					InvoiceDetail invoiceDetail = new InvoiceDetail();
					invoiceDetail.setLinkedTranId(aeEvent.getLinkedTranId());
					invoiceDetail.setFinanceDetail(detail);
					invoiceDetail.setFinFeeDetailsList(feesList);
					invoiceDetail.setOrigination(false);
					invoiceDetail.setWaiver(false);
					invoiceDetail.setDbInvSetReq(false);
					invoiceDetail.setInvoiceType(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT);

					this.gstInvoiceTxnService.feeTaxInvoicePreparation(invoiceDetail);
				}
			}
		}

		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
		finEODEvent.setUpdLBDPostings(true);

		// LPP Due Amount , Which is already marked as Income/Receivable should be updated
		pfd.setLppTillLBD(pfd.getLppTillLBD().add(penaltyDue));
		pfd.setGstLppTillLBD(pfd.getGstLppTillLBD().add(penaltyDueGst));

		logger.info(Literal.LEAVING);
	}

	private void prepareFinanceDetail(FinanceDetail financeDetail, CustEODEvent custEODEvent) {

		// Set Tax Details if Already exists
		FinanceMain fm = financeDetail.getFinScheduleData().getFinanceMain();
		if (financeDetail.getFinanceTaxDetail() == null) {
			financeDetail.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetail(fm.getFinID(), ""));
		}

		CustomerAddres addres = customerAddresDAO.getHighPriorityCustAddr(fm.getCustID(), "_AView");
		if (addres == null) {
			return;
		}

		CustomerDetails customerDetails = new CustomerDetails();
		List<CustomerAddres> addressList = new ArrayList<>();
		addressList.add(addres);
		customerDetails.setAddressList(addressList);
		customerDetails.setCustomer(custEODEvent.getCustomer());
		financeDetail.setCustomerDetails(customerDetails);
	}

	/**
	 * Method for Calculating Total GST Amount with the Requested Amount
	 */
	private BigDecimal getTotalTaxAmount(Map<String, BigDecimal> taxPercmap, BigDecimal amount, String taxType) {
		TaxAmountSplit taxSplit = null;
		BigDecimal gstAmount = BigDecimal.ZERO;

		if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(taxType)) {
			taxSplit = GSTCalculator.getExclusiveGST(amount, taxPercmap);
		} else if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(taxType)) {
			taxSplit = GSTCalculator.getInclusiveGST(amount, taxPercmap);
		}

		if (taxSplit != null) {
			gstAmount = taxSplit.gettGST();
		}

		return gstAmount;
	}

	private FinODAmzTaxDetail getTaxDetail(Map<String, BigDecimal> taxPercmap, BigDecimal actTaxAmount,
			String taxType) {

		BigDecimal cgstPerc = taxPercmap.get(RuleConstants.CODE_CGST);
		BigDecimal sgstPerc = taxPercmap.get(RuleConstants.CODE_SGST);
		BigDecimal ugstPerc = taxPercmap.get(RuleConstants.CODE_UGST);
		BigDecimal igstPerc = taxPercmap.get(RuleConstants.CODE_IGST);
		BigDecimal cessPerc = taxPercmap.get(RuleConstants.CODE_CESS);
		BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc).add(cessPerc);

		FinODAmzTaxDetail taxDetail = new FinODAmzTaxDetail();
		taxDetail.setTaxType(taxType);
		BigDecimal totalGST = BigDecimal.ZERO;

		if (cgstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal cgstAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, cgstPerc, totalGSTPerc);
			taxDetail.setCGST(cgstAmount);
			totalGST = totalGST.add(cgstAmount);
		}

		if (sgstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal sgstAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, sgstPerc, totalGSTPerc);
			taxDetail.setSGST(sgstAmount);
			totalGST = totalGST.add(sgstAmount);
		}

		if (ugstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal ugstAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, ugstPerc, totalGSTPerc);
			taxDetail.setUGST(ugstAmount);
			totalGST = totalGST.add(ugstAmount);
		}

		if (igstPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal igstAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, igstPerc, totalGSTPerc);
			taxDetail.setIGST(igstAmount);
			totalGST = totalGST.add(igstAmount);
		}

		if (cessPerc.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal cessAmount = GSTCalculator.calGstTaxAmount(actTaxAmount, cessPerc, totalGSTPerc);
			taxDetail.setCESS(cessAmount);
			totalGST = totalGST.add(cessAmount);
		}

		taxDetail.setTotalGST(totalGST);

		return taxDetail;
	}

	/**
	 * Method for Setting default Value to Zero
	 * 
	 * @param dataMap
	 * @param key
	 */
	private void addZeroifNotContains(Map<String, BigDecimal> dataMap, String key) {
		if (dataMap != null) {
			if (!dataMap.containsKey(key)) {
				dataMap.put(key, BigDecimal.ZERO);
			}
		}
	}

	private List<FinFeeDetail> prepareFeesList(FeeType lppFeeType, Map<String, BigDecimal> taxPercMap,
			Map<String, BigDecimal> calGstMap, BigDecimal penaltyDue) {

		List<FinFeeDetail> feeList = new ArrayList<>();

		if (lppFeeType == null || (taxPercMap == null || calGstMap == null)) {
			return feeList;
		}

		FinFeeDetail fee = new FinFeeDetail();
		TaxHeader taxHeader = new TaxHeader();
		fee.setTaxHeader(taxHeader);

		fee.setFeeTypeCode(lppFeeType.getFeeTypeCode());
		fee.setFeeTypeDesc(lppFeeType.getFeeTypeDesc());
		fee.setTaxApplicable(true);
		fee.setOriginationFee(false);
		fee.setNetAmountOriginal(penaltyDue);

		Taxes cgstTax = new Taxes();
		Taxes sgstTax = new Taxes();
		Taxes igstTax = new Taxes();
		Taxes ugstTax = new Taxes();
		Taxes cessTax = new Taxes();

		cgstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_CGST));
		sgstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_SGST));
		igstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_IGST));
		ugstTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_UGST));
		cessTax.setTaxPerc(taxPercMap.get(RuleConstants.CODE_CESS));

		cgstTax.setTaxType(RuleConstants.CODE_CGST);
		sgstTax.setTaxType(RuleConstants.CODE_SGST);
		igstTax.setTaxType(RuleConstants.CODE_IGST);
		ugstTax.setTaxType(RuleConstants.CODE_UGST);
		cessTax.setTaxType(RuleConstants.CODE_CESS);

		cgstTax.setNetTax(calGstMap.get("LPP_CGST_R"));
		sgstTax.setNetTax(calGstMap.get("LPP_SGST_R"));
		igstTax.setNetTax(calGstMap.get("LPP_IGST_R"));
		ugstTax.setNetTax(calGstMap.get("LPP_UGST_R"));
		cessTax.setNetTax(calGstMap.get("LPP_CESS_R"));

		taxHeader.getTaxDetails().add(cgstTax);
		taxHeader.getTaxDetails().add(sgstTax);
		taxHeader.getTaxDetails().add(igstTax);
		taxHeader.getTaxDetails().add(ugstTax);
		taxHeader.getTaxDetails().add(cessTax);

		if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(lppFeeType.getTaxComponent())) {
			BigDecimal gstAmount = cgstTax.getNetTax().add(sgstTax.getNetTax()).add(igstTax.getNetTax())
					.add(ugstTax.getNetTax()).add(cessTax.getNetTax());
			fee.setNetAmountOriginal(penaltyDue.subtract(gstAmount));
		}

		feeList.add(fee);

		return feeList;
	}

	public void setLatePayInterestService(LatePayInterestService latePayInterestService) {
		this.latePayInterestService = latePayInterestService;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public void setFinODAmzTaxDetailDAO(FinODAmzTaxDetailDAO finODAmzTaxDetailDAO) {
		this.finODAmzTaxDetailDAO = finODAmzTaxDetailDAO;
	}

	public void setGstInvoiceTxnService(GSTInvoiceTxnService gstInvoiceTxnService) {
		this.gstInvoiceTxnService = gstInvoiceTxnService;
	}
}