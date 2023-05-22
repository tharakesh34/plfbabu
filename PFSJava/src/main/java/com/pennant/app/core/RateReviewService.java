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
 * * FileName : RateReview.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-04-2011 * * Modified Date :
 * 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceRateReviewDAO;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRateReview;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.util.ProductUtil;

public class RateReviewService extends ServiceHelper {
	private Logger logger = LogManager.getLogger(RateReviewService.class);

	private FinanceRateReviewDAO financeRateReviewDAO;
	private AccrualService accrualService;

	// fetch rates changed yesterday or effective date is today

	public RateReviewService() {
		super();
	}

	public void processRateReview(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		for (FinEODEvent finEODEvent : finEODEvents) {
			if (!finEODEvent.isRateReviewExist()) {
				continue;
			}

			processRateReview(finEODEvent, custEODEvent.getEodValueDate());

			if (finEODEvent.isRateReviewExist()) {
				reviewRateUpdate(finEODEvent, custEODEvent);
			}
		}
	}

	private void processRateReview(FinEODEvent finEODEvent, Date valueDate) {

		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();

		// Reverify really Rate Review Required
		finEODEvent.setRateReviewExist(false);

		// No Rate Review on start date
		if (valueDate.compareTo(fm.getFinStartDate()) == 0) {
			return;
		}

		// No Rate Review on Maturity date
		if (valueDate.compareTo(fm.getMaturityDate()) == 0) {
			return;
		}

		int iEvtFrom = 0;
		int iEvtTo = schedules.size() - 1;

		finEODEvent.setEventFromDate(valueDate);
		finEODEvent.setEventToDate(schedules.get(iEvtTo).getSchDate());
		finEODEvent.setRecalFromDate(schedules.get(iEvtTo).getSchDate());
		finEODEvent.setRecalToDate(schedules.get(iEvtTo).getSchDate());

		for (int i = 0; i < schedules.size(); i++) {
			if (schedules.get(i).getSchDate().compareTo(valueDate) == 0
					&& !StringUtils.isEmpty(schedules.get(i).getBaseRate())) {
				// FIXME Field name should be renamed
				finEODEvent.setRateOnChgDate(schedules.get(i).getBaseRate());
				iEvtFrom = i;
				break;
			}
		}

		// Never Happens
		if (iEvtFrom == 0) {
			return;
		}

		FinanceScheduleDetail curSchd = null;

		// SET Event From Date in case Unpaid Schedules only
		if (CalculationConstants.RATEREVIEW_RVWUPR.equals(fm.getRvwRateApplFor())) {
			for (int i = iEvtFrom; i < schedules.size(); i++) {
				curSchd = schedules.get(i);
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0
						&& curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) == 0
						&& curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0) {
					continue;
				} else if (!curSchd.isRvwOnSchDate()) {
					continue;
				}

				finEODEvent.setRateReviewExist(true);
				iEvtFrom = i;
				finEODEvent.setRateOnChgDate(schedules.get(i).getBaseRate());
				finEODEvent.setEventFromDate(curSchd.getSchDate());
				break;
			}
		} else {
			finEODEvent.setRateReviewExist(true);
		}

		if (!finEODEvent.isRateReviewExist()) {
			return;
		}

		// SET RECAL From Date
		finEODEvent.setRecalType(fm.getSchCalOnRvw());

		if (StringUtils.isEmpty(finEODEvent.getRecalType())) {
			finEODEvent.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		}

		if (!CalculationConstants.RPYCHG_ADJMDT.equals(finEODEvent.getRecalType())) {
			finEODEvent.setRecalFromDate(findRecalFromDate(finEODEvent, iEvtFrom));
		}

		if (finEODEvent.getRecalFromDate() == null) {
			finEODEvent.setRateReviewExist(false);
			return;
		}

		finEODEvent.setRateReviewExist(true);
	}

	private void reviewRateUpdate(FinEODEvent finEODEvent, CustEODEvent custEODEvent) {
		String finReference = finEODEvent.getFinanceMain().getFinReference();
		logger.info("Processing rate review for the FinReference >> {}", finReference);

		FinScheduleData schdData = getFinSchDataByFinRef(finEODEvent);
		Date valueDate = custEODEvent.getEodValueDate();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();

		fm.setEventFromDate(finEODEvent.getEventFromDate());
		fm.setEventToDate(finEODEvent.getEventToDate());
		fm.setRecalFromDate(finEODEvent.getRecalFromDate());
		fm.setRecalToDate(finEODEvent.getRecalToDate());
		fm.setRecalSchdMethod(finEODEvent.getRecalSchdMethod());
		fm.setRecalType(finEODEvent.getRecalType());

		// Schedule Recalculation Locking Period Applicability
		EventProperties eventProperties = custEODEvent.getEventProperties();
		boolean schRecalLock = false;
		if (eventProperties.isParameterLoaded()) {
			schRecalLock = eventProperties.isSchRecalLock();
		} else {
			schRecalLock = SysParamUtil.isAllowed(SMTParameterConstants.ALW_SCH_RECAL_LOCK);
		}

		if (schRecalLock) {
			Date recalLockTill = fm.getRecalFromDate();
			if (recalLockTill == null) {
				recalLockTill = fm.getMaturityDate();
			}

			int sdSize = schdData.getFinanceScheduleDetails().size();
			FinanceScheduleDetail curSchd = null;
			for (int i = 0; i <= sdSize - 1; i++) {

				curSchd = schdData.getFinanceScheduleDetails().get(i);
				if (DateUtil.compare(curSchd.getSchDate(), recalLockTill) < 0 && (i != sdSize - 1) && i != 0) {
					curSchd.setRecalLock(true);
				} else {
					curSchd.setRecalLock(false);
				}
			}
		}

		// Finance Profit Details
		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();
		if (profitDetail.getFinReference() == null) {
			profitDetail = financeProfitDetailDAO.getFinProfitDetailsById(fm.getFinID());
		}

		BigDecimal totalPftSchdOld = profitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzOld = profitDetail.getTotalPftCpz();

		// finScheduleData.getFinanceMain().setCalRoundingMode(finScheduleData.getFinanceType().getRoundingMode());
		// finScheduleData.getFinanceMain().setRoundingTarget(finScheduleData.getFinanceType().getRoundingTarget());

		// Rate Changes applied for Finance Schedule Data

		if (ProductUtil.isOverDraft(fm)) {
			schdData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(fm.getFinID(), "", false));
		}

		schdData = ScheduleCalculator.refreshRates(schdData);

		FinanceProfitDetail newProfitDetail = accrualService.calProfitDetails(fm, schedules, profitDetail, valueDate);
		// Amount Codes Details Preparation
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(fm, profitDetail, schedules, AccountingEvent.RATCHG, valueDate,
				valueDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		if (amountCodes.getPftChg().compareTo(BigDecimal.ZERO) == 0) {
			logger.info("Rate review Exhausted for the FinReference >> {} due profit shcedule", finReference);
			return;
		}

		finEODEvent.setUpdFinMain(true);
		finEODEvent.setupdFinSchdForRateRvw(true);
		finEODEvent.setUpdRepayInstruct(true);
		finEODEvent.setFinanceScheduleDetails(schdData.getFinanceScheduleDetails());
		finEODEvent.setRepayInstructions(schdData.getRepayInstructions());

		// Saving Rate Review Details
		FinanceRateReview rateReview = new FinanceRateReview();
		rateReview.setFinReference(finReference);
		rateReview.setRateType(finEODEvent.getRateOnChgDate());
		rateReview.setCurrency(fm.getFinCcy());
		rateReview.setValueDate(valueDate);
		rateReview.setEffectiveDate(valueDate);
		rateReview.setEventFromDate(finEODEvent.getEventFromDate());
		rateReview.setEventToDate(finEODEvent.getEventToDate());
		rateReview.setRecalFromdate(fm.getRecalFromDate());
		rateReview.setRecalToDate(fm.getRecalToDate());
		financeRateReviewDAO.save(rateReview);
		aeEvent.setFinType(fm.getFinType());
		Long accountingID = getAccountingID(fm, AccountingEvent.RATCHG);

		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			return;
		} else {
			aeEvent.getAcSetIDList().add(accountingID);
		}

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setPostDate(custEODEvent.getCustomer().getCustAppDate());
		// Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		logger.info("Rate review Completed for the FinReference >>{}", finReference);
	}

	public FinScheduleData getFinSchDataByFinRef(FinEODEvent finEodEvent) {
		FinScheduleData schdData = new FinScheduleData();
		FinanceMain fm = finEodEvent.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);
		schdData.setFinanceMain(fm);
		schdData.setFinanceScheduleDetails(finEodEvent.getFinanceScheduleDetails());

		FinanceType fintype = getFinanceType(fm.getFinType());
		schdData.setFinanceType(fintype);
		finEodEvent.setFinType(fintype);

		List<RepayInstruction> repayInstructions = repayInstructionDAO.getRepayInstrEOD(finID);
		List<FinanceDisbursement> fd = financeDisbursementDAO.getFinanceDisbursementDetails(finID, "", false);

		if (fm.isStepFinance()) {
			List<FinanceStepPolicyDetail> stp = financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "", false);
			schdData.setStepPolicyDetails(stp, true);
			finEodEvent.setStepPolicyDetails(stp);
		}

		schdData.setRepayInstructions(repayInstructions);
		finEodEvent.setRepayInstructions(repayInstructions);
		schdData.setDisbursementDetails(fd);
		finEodEvent.setFinanceDisbursements(fd);

		return schdData;
	}

	private Date findRecalFromDate(FinEODEvent finEODEvent, int iEvtFrom) {

		List<FinanceScheduleDetail> schedules = finEODEvent.getFinanceScheduleDetails();
		FinanceScheduleDetail schd;

		int sdSize = schedules.size();
		Date recalFromDate = null;

		for (int i = iEvtFrom; i < sdSize; i++) {
			schd = schedules.get(i);

			if (i == iEvtFrom) {
				continue;
			}
			// Since schedule is presented it should not considered for rate review.
			if (schd.getPresentmentId() != 0) {
				continue;
			}

			// SET RECAL FROMDATE
			if (schd.isPftOnSchDate() || schd.isRepayOnSchDate()) {
				if (schd.getSchdPriPaid().compareTo(BigDecimal.ZERO) == 0
						&& schd.getSchdPftPaid().compareTo(BigDecimal.ZERO) == 0) {
					recalFromDate = schd.getSchDate();
					break;
				}
			}
		}

		return recalFromDate;

	}

	public void setFinanceRateReviewDAO(FinanceRateReviewDAO financeRateReviewDAO) {
		this.financeRateReviewDAO = financeRateReviewDAO;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

}
