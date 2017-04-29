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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinanceRateReviewDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRateReview;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.eod.util.EODProperties;
import com.pennanttech.pff.core.TableType;

public class RateReviewService extends ServiceHelper {

	private static final long		serialVersionUID	= -4939080414435712845L;

	private Logger					logger				= Logger.getLogger(RateReviewService.class);

	private RepayInstructionDAO		repayInstructionDAO;
	private FinanceRateReviewDAO	financeRateReviewDAO;

	//fetch rates changed yesterday or effective date is today

	public RateReviewService() {
		super();
	}

	public List<FinEODEvent> processRateReview(List<FinEODEvent> custEODEvents) throws Exception {

		for (FinEODEvent finEODEvent : custEODEvents) {
			if (!finEODEvent.isRateReview()) {
				continue;
			}

			processRateReview(finEODEvent);

			if (finEODEvent.isRateReview()) {
				reviewRateUpdate(finEODEvent);
			}
		}

		return custEODEvents;
	}

	private void processRateReview(FinEODEvent finEODEvent) throws Exception {
		Date valueDate = finEODEvent.getEodValueDate();
		FinanceMain finMain = finEODEvent.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		Map<Date, Integer> datesMap = finEODEvent.getDatesMap();

		int i = datesMap.get(valueDate);
		int iNext = i + 1;

		finEODEvent.setRateReview(false);

		//No Rate Review on start date
		if (valueDate.compareTo(finMain.getFinStartDate()) == 0) {
			return;
		}

		//No Rate Review on Maturity date
		if (valueDate.compareTo(finMain.getFinStartDate()) == 0) {
			return;
		}

		for (int j = i; j < finSchdDetails.size(); j++) {

			if (StringUtils.isEmpty(finSchdDetails.get(j).getBaseRate())) {
				continue;
			}

			finEODEvent.setRateReview(true);
			break;
		}

		//No base Rate found after the new review date
		if (!finEODEvent.isRateReview()) {
			return;
		}

		finEODEvent.setRateReview(false);

		FinanceScheduleDetail curSchd = null;

		//SET Event From Date
		if (StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_RVWALL)) {
			finEODEvent.setEventFromDate(valueDate);
			finEODEvent.setRateReview(true);
			finEODEvent.setRateOnChgDate(finSchdDetails.get(i).getBaseRate());
		} else if (StringUtils.equals(finMain.getRvwRateApplFor(), CalculationConstants.RATEREVIEW_RVWUPR)) {
			for (int j = iNext; j < finSchdDetails.size(); j++) {
				curSchd = finSchdDetails.get(i);
				if (curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) == 0
						&& curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0) {
					continue;
				}

				finEODEvent.setRateReview(true);
				finEODEvent.setEventFromDate(finSchdDetails.get(j - 1).getSchDate());
				finEODEvent.setRateOnChgDate(finSchdDetails.get(j - 1).getBaseRate());
				break;
			}
		}

		if (!finEODEvent.isRateReview()) {
			return;
		}

		finEODEvent.setEventToDate(finMain.getMaturityDate());
		finEODEvent.setRecalToDate(finMain.getMaturityDate());

		//SET RECAL From Date
		finEODEvent.setRecalType(finMain.getSchCalOnRvw());

		if (StringUtils.isEmpty(finEODEvent.getRecalType())) {
			finEODEvent.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		}

		if (StringUtils.equals(finEODEvent.getRecalType(), CalculationConstants.RPYCHG_ADJMDT)) {
			finEODEvent.setRecalFromDate(finMain.getMaturityDate());
		} else {
			finEODEvent = findRecalFromDate(finEODEvent, iNext);
		}

	}

	private void reviewRateUpdate(FinEODEvent finEODEvent) throws Exception {

		String finRef = finEODEvent.getFinanceMain().getFinReference();
		Date businessDate = finEODEvent.getEodValueDate();
		FinScheduleData finScheduleData = getFinSchDataByFinRef(finEODEvent);

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
		if (profitDetail.getFinReference()==null) {
			profitDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(finMain.getFinReference());
		}
		
		BigDecimal totalPftSchdOld = profitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzOld = profitDetail.getTotalPftCpz();

		// Rate Changes applied for Finance Schedule Data
		finScheduleData = ScheduleCalculator.refreshRates(finScheduleData);
		
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		newProfitDetail = AccrualService.calProfitDetails(finMain, finSchdDetails, profitDetail, businessDate);
		// Amount Codes Details Preparation
		AEAmountCodes amountCodes = AEAmounts.procCalAEAmounts(profitDetail, AccountEventConstants.ACCEVENT_RATCHG,
				businessDate, businessDate);
		
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();
		
		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		if (amountCodes.getPftChg().compareTo(BigDecimal.ZERO)==0) {
			return;
		}
		
		finEODEvent.setUpdFinMain(true);;
		finEODEvent.setUpdFinSchedule(true);
		finEODEvent.setUpdRepayInstruct(true);
		finEODEvent.setFinanceScheduleDetails(finScheduleData.getFinanceScheduleDetails());
		finEODEvent.setRepayInstructions(finScheduleData.getRepayInstructions());
		
		HashMap<String, Object> executingMap = amountCodes.getDeclaredFieldValues();
		List<ReturnDataSet> list = prepareAccounting(executingMap, finScheduleData.getFinanceType());
		saveAccounting(list);
		
		//FIXME: PV 28APR17 Returning without saving because it is decided to save all records once
		//Code for one time saving is not yet ready
		// Update New Finance Schedule Details Data
		saveOrUpdate(finScheduleData, profitDetail);
		
		//Saving Rate Review Details
		FinanceRateReview rateReview = new FinanceRateReview();
		rateReview.setFinReference(finRef);
		rateReview.setRateType(finEODEvent.getRateOnChgDate());
		rateReview.setCurrency(finMain.getFinCcy());
		rateReview.setValueDate(businessDate);
		rateReview.setEffectiveDate(businessDate);
		rateReview.setEventFromDate(finEODEvent.getEventFromDate());
		rateReview.setEventToDate(finEODEvent.getEventToDate());
		rateReview.setRecalFromdate(finMain.getRecalFromDate());
		rateReview.setRecalToDate(finMain.getRecalToDate());
		financeRateReviewDAO.save(rateReview);

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
		FinanceType fintype = EODProperties.getFinanceType(finEodEvent.getFinanceMain().getFinType());
		finSchData.setFinanceType(fintype);
		finEodEvent.setFinType(fintype);
		List<RepayInstruction> repayInstructions = repayInstructionDAO.getRepayInstrEOD(finSchData.getFinReference());
		finSchData.setRepayInstructions(repayInstructions);
		finEodEvent.setRepayInstructions(repayInstructions);
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method to save Finance Related sublist
	 * 
	 * @param schdueleData
	 */
	public void saveOrUpdate(FinScheduleData schdueleData, FinanceProfitDetail profitDetail) {
		logger.debug("Entering ");
		FinanceMain finMain = schdueleData.getFinanceMain();
		// FinanceMain updation
		finMain.setVersion(finMain.getVersion() + 1);
		getFinanceMainDAO().update(finMain, TableType.MAIN_TAB, false);
		// Finance Schedule Details
		getFinanceScheduleDetailDAO().updateList(schdueleData.getFinanceScheduleDetails(), "");
		// Finance Repay Instruction Details
		repayInstructionDAO.deleteByFinReference(finMain.getFinReference(), "", false, 0);
		//Add repay instructions
		List<RepayInstruction> lisRepayIns = schdueleData.getRepayInstructions();
		for (RepayInstruction repayInstruction : lisRepayIns) {
			repayInstruction.setFinReference(finMain.getFinReference());
		}
		repayInstructionDAO.saveList(lisRepayIns, "", false);
		// UPDATE Finance Profit Details
		//getFinanceProfitDetailDAO().update(profitDetail, false);

		logger.debug("Leaving ");
	}

	private FinEODEvent findRecalFromDate(FinEODEvent finEODEvent, int iNext) {

		List<FinanceScheduleDetail> finSchdDetails = finEODEvent.getFinanceScheduleDetails();
		FinanceScheduleDetail curSchd = new FinanceScheduleDetail();
		int sdSize = finSchdDetails.size();
		Date schdDate = new Date();

		for (int i = iNext; i < sdSize; i++) {
			curSchd = finSchdDetails.get(i);
			schdDate = curSchd.getSchDate();

			if (schdDate.compareTo(finEODEvent.getEventFromDate()) <= 0) {
				continue;
			}

			//SET RECAL FROMDATE
			if (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) {
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) == 0
						&& curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) == 0) {
					finEODEvent.setRecalFromDate(schdDate);
				}

				break;
			}
		}

		return finEODEvent;

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public void setFinanceRateReviewDAO(FinanceRateReviewDAO financeRateReviewDAO) {
		this.financeRateReviewDAO = financeRateReviewDAO;
	}

}
