/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  RateReview.java														*                           
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  						*
 * Creation Date    :  26-04-2011															*
 *                                                                  						*
 * Modified Date    :  30-07-2011															*
 *                                                                  						*
 * Description 		:												 						*                                 
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceRateReviewDAO;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRateReview;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;

public class RateReviewService extends ServiceHelper {

	private static final long		serialVersionUID	= -4939080414435712845L;
	private Logger					logger				= Logger.getLogger(RateReviewService.class);

	private FinanceRateReviewDAO	financeRateReviewDAO;
	private AccrualService			accrualService;

	//fetch rates changed yesterday or effective date is today

	public RateReviewService() {
		super();
	}

	public CustEODEvent processRateReview(CustEODEvent custEODEvent) throws Exception {
		logger.debug(" Entering ");
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
		logger.debug(" Leaving ");
		return custEODEvent;
	}

	private void processRateReview(FinEODEvent finEODEvent, Date valueDate) throws Exception {

		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();

		finEODEvent.setRateReviewExist(false);

		//No Rate Review on start date
		if (valueDate.compareTo(finMain.getFinStartDate()) == 0) {
			return;
		}

		//No Rate Review on Maturity date
		if (valueDate.compareTo(finMain.getMaturityDate()) == 0) {
			return;
		}

		int iEvtFrom = 0;
		int iEvtTo = finSchdDetails.size()-1;
		
		finEODEvent.setEventFromDate(valueDate);
		finEODEvent.setEventToDate(finSchdDetails.get(iEvtTo).getSchDate());
		finEODEvent.setRecalFromDate(finSchdDetails.get(iEvtTo).getSchDate());
		finEODEvent.setRecalToDate(finSchdDetails.get(iEvtTo).getSchDate());

		for (int i = 0; i < finSchdDetails.size(); i++) {
			if (finSchdDetails.get(i).getSchDate().compareTo(valueDate) == 0 && !StringUtils.isEmpty(finSchdDetails.get(i).getBaseRate())) {
				//FIXME Filed name should be renamed
				finEODEvent.setRateOnChgDate(finSchdDetails.get(i).getBaseRate());
				iEvtFrom = i;
				break;
			}
		}

		//Never Happens
		if (iEvtFrom == 0) {
			return;
		}

		FinanceScheduleDetail curSchd = null;

		//SET Event From Date in case Unpaid Schedules only
		if (StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_RVWUPR)) {
			for (int i = iEvtFrom; i < finSchdDetails.size(); i++) {
				curSchd = finSchdDetails.get(i);
				if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) == 0
						&& curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0) {
					continue;
				}
				
				finEODEvent.setRateReviewExist(true);
				iEvtFrom = i;
				finEODEvent.setRateOnChgDate(finSchdDetails.get(i).getBaseRate());
				finEODEvent.setEventFromDate(curSchd.getSchDate());
				break;
			}
		} else {
			finEODEvent.setRateReviewExist(true);
		}

		if (!finEODEvent.isRateReviewExist()) {
			return;
		}
		
		//SET RECAL From Date
		finEODEvent.setRecalType(finMain.getSchCalOnRvw());

		if (StringUtils.isEmpty(finEODEvent.getRecalType())) {
			finEODEvent.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		}

		if (!StringUtils.equals(finEODEvent.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
			finEODEvent.setRecalFromDate(findRecalFromDate(finEODEvent, iEvtFrom));
		}
		
		if (finEODEvent.getRecalFromDate()==null) {
			finEODEvent.setRateReviewExist(false);
			return;
		}

		finEODEvent.setRateReviewExist(true);
	}

	private void reviewRateUpdate(FinEODEvent finEODEvent, CustEODEvent custEODEvent) throws Exception {

		String finRef = finEODEvent.getFinanceMain().getFinReference();
		FinScheduleData finScheduleData = getFinSchDataByFinRef(finEODEvent);
		Date valueDate = custEODEvent.getEodValueDate();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		finMain.setEventFromDate(finEODEvent.getEventFromDate());
		finMain.setEventToDate(finEODEvent.getEventToDate());
		finMain.setRecalFromDate(finEODEvent.getRecalFromDate());
		finMain.setRecalToDate(finEODEvent.getRecalToDate());
		finMain.setRecalSchdMethod(finEODEvent.getRecalSchdMethod());
		finMain.setRecalType(finEODEvent.getRecalType());

		// Finance Profit Details
		FinanceProfitDetail profitDetail = finEODEvent.getFinProfitDetail();
		if (profitDetail.getFinReference() == null) {
			profitDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(finMain.getFinReference());
		}

		BigDecimal totalPftSchdOld = profitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzOld = profitDetail.getTotalPftCpz();

		//finScheduleData.getFinanceMain().setCalRoundingMode(finScheduleData.getFinanceType().getRoundingMode());
		//finScheduleData.getFinanceMain().setRoundingTarget(finScheduleData.getFinanceType().getRoundingTarget());

		// Rate Changes applied for Finance Schedule Data
		finScheduleData = ScheduleCalculator.refreshRates(finScheduleData);

		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		newProfitDetail = accrualService.calProfitDetails(finMain, finSchdDetails, profitDetail, valueDate);
		// Amount Codes Details Preparation
		AEEvent aeEvent = AEAmounts.procCalAEAmounts(profitDetail, finSchdDetails, AccountEventConstants.ACCEVENT_RATCHG, valueDate,
				valueDate);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		if (amountCodes.getPftChg().compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		finEODEvent.setUpdFinMain(true);
		finEODEvent.setupdFinSchdForRateRvw(true);
		finEODEvent.setUpdRepayInstruct(true);
		finEODEvent.setFinanceScheduleDetails(finScheduleData.getFinanceScheduleDetails());
		finEODEvent.setRepayInstructions(finScheduleData.getRepayInstructions());

		//Saving Rate Review Details
		FinanceRateReview rateReview = new FinanceRateReview();
		rateReview.setFinReference(finRef);
		rateReview.setRateType(finEODEvent.getRateOnChgDate());
		rateReview.setCurrency(finMain.getFinCcy());
		rateReview.setValueDate(valueDate);
		rateReview.setEffectiveDate(valueDate);
		rateReview.setEventFromDate(finEODEvent.getEventFromDate());
		rateReview.setEventToDate(finEODEvent.getEventToDate());
		rateReview.setRecalFromdate(finMain.getRecalFromDate());
		rateReview.setRecalToDate(finMain.getRecalToDate());
		financeRateReviewDAO.save(rateReview);
		aeEvent.setFinType(finMain.getFinType());
		long accountingID = getAccountingID(finMain, AccountEventConstants.ACCEVENT_RATCHG);

		if (accountingID == Long.MIN_VALUE) {
			return;
		} else {
			aeEvent.getAcSetIDList().add(accountingID);
		}

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		aeEvent.setPostDate(custEODEvent.getCustomer().getCustAppDate());
		//Postings Process and save all postings related to finance for one time accounts update
		postAccountingEOD(aeEvent);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());
	}

	/**
	 * Method for fetching Finance Schedule Data based on FinReference
	 * 
	 * @param finRef
	 * @param type
	 * @return
	 */
	public FinScheduleData getFinSchDataByFinRef(FinEODEvent finEodEvent) {
		logger.debug("Entering");
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(finEodEvent.getFinanceMain().getFinReference());
		finSchData.setFinanceMain(finEodEvent.getFinanceMain());
		finSchData.setFinanceScheduleDetails(finEodEvent.getFinanceScheduleDetails());
		FinanceType fintype = getFinanceType(finEodEvent.getFinanceMain().getFinType());
		finSchData.setFinanceType(fintype);
		finEodEvent.setFinType(fintype);
		List<RepayInstruction> repayInstructions = getRepayInstructionDAO().getRepayInstrEOD(
				finSchData.getFinReference());
		finSchData.setRepayInstructions(repayInstructions);
		finEodEvent.setRepayInstructions(repayInstructions);
		logger.debug("Leaving");
		return finSchData;
	}

	private Date findRecalFromDate(FinEODEvent finEODEvent, int iEvtFrom) {

		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = finSchdDetails.size();
		Date recalFromDate = null;

		for (int i = iEvtFrom; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);

			if (i==iEvtFrom) {
				continue;
			}
			//Since schedule is presented it should not considered for rate review.
			if (curSchd.getPresentmentId() != 0) {
				continue;
			}

			//SET RECAL FROMDATE
			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) == 0
						&& curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) == 0) {
					recalFromDate = curSchd.getSchDate();
					break;
				}
			}
		}

		return recalFromDate;

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceRateReviewDAO(FinanceRateReviewDAO financeRateReviewDAO) {
		this.financeRateReviewDAO = financeRateReviewDAO;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

}
