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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.eod.constants.EodSql;
import com.rits.cloning.Cloner;

public class RateReviewService extends ServiceHelper {

	private static final long		serialVersionUID	= -4939080414435712845L;

	private Logger					logger				= Logger.getLogger(RateReviewService.class);

	private FinanceDisbursementDAO	financeDisbursementDAO;
	private RepayInstructionDAO		repayInstructionDAO;
	private FinLogEntryDetailDAO	finLogEntryDetailDAO;

	public RateReviewService() {
		super();
	}

	public void processRateReview(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = connection.prepareStatement(EodSql.rateReview);
			sqlStatement.setDate(1, DateUtility.getDBDate(date.toString()));
			sqlStatement.setLong(2, custId);
			sqlStatement.setDate(3, DateUtility.getDBDate(date.toString()));
			sqlStatement.setLong(4, custId);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				processRateReview(resultSet.getString("FinReference"), date);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	public void processRateReview(String finReference, Date date) throws Exception {

		logger.debug("START: Rate Review for Value Date: " + DateUtility.addDays(date, -1));

		FinanceProfitDetail profitDetail = null;
		FinanceMain financeMain;

		// Fetching Finance Schedule Data by FinReference
		FinScheduleData finScheduleData = getFinSchDataByFinRef(finReference, "_AView");
		financeMain = finScheduleData.getFinanceMain();

		Cloner cloner = new Cloner();
		FinScheduleData orgFinScheduleData = cloner.deepClone(finScheduleData);

		// Rate Changes applied for Finance Schedule Data
		finScheduleData = ScheduleCalculator.refreshRates(finScheduleData);

		// Finance Profit Details
		profitDetail = AEAmounts.calProfitDetails(financeMain, finScheduleData.getFinanceScheduleDetails(),
				profitDetail, date);

		// Amount Codes Details Preparation
		AEAmountCodes amountCodes = AEAmounts.procCalAEAmounts(financeMain, profitDetail, date);

		// DataSet preparation
		DataSet dataSet = AEAmounts.createDataSet(financeMain, AccountEventConstants.ACCEVENT_RATCHG, date,
				DateUtility.getAppDate());

		// Posting Preparation Util

		List<ReturnDataSet> list = prepareAccounting(dataSet, amountCodes, finScheduleData.getFinanceType());
		saveAccounting(list);

		// Update New Finance Schedule Details Data
		listSave(finScheduleData, profitDetail);

		//Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();
		entryDetail.setFinReference(finScheduleData.getFinReference());
		entryDetail.setEventAction(AccountEventConstants.ACCEVENT_RATCHG);
		entryDetail.setSchdlRecal(true);
		entryDetail.setPostDate(date);
		entryDetail.setReversalCompleted(false);
		long logKey = getFinLogEntryDetailDAO().save(entryDetail);

		//Log Entry Saving for TOtal Finance Detail
		listSave(orgFinScheduleData, "_Log", logKey);

		logger.debug("COMPLETE: Rate Review for Value Date: " + DateUtility.addDays(date, -1));

	}

	/**
	 * Method for fetching Finance Schedule Data based on FinReference
	 * 
	 * @param financeReference
	 * @param type
	 * @return
	 */
	public FinScheduleData getFinSchDataByFinRef(String financeReference, String type) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(financeReference);
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(financeReference, type, false));
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(financeReference,
				type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(financeReference, type, false));
		finSchData.setFinanceType(getFinanceTypeDAO()
				.getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
		logger.debug("Leaving");
		return finSchData;

	}

	/**
	 * Method to save Finance Related sublist
	 * 
	 * @param schdueleData
	 */
	public void listSave(FinScheduleData schdueleData, FinanceProfitDetail profitDetail) {
		logger.debug("Entering ");

		// FinanceMain updation
		schdueleData.getFinanceMain().setVersion(schdueleData.getFinanceMain().getVersion() + 1);
		getFinanceMainDAO().update(schdueleData.getFinanceMain(), "", false);

		// Finance Schedule Details
		getFinanceScheduleDetailDAO().updateList(schdueleData.getFinanceScheduleDetails(), "");

		// Finance Repay Instruction Details
		getRepayInstructionDAO().deleteByFinReference(schdueleData.getFinanceMain().getFinReference(), "", false, 0);

		for (int i = 0; i < schdueleData.getRepayInstructions().size(); i++) {
			schdueleData.getRepayInstructions().get(i).setFinReference(schdueleData.getFinanceMain().getFinReference());
		}

		getRepayInstructionDAO().saveList(schdueleData.getRepayInstructions(), "", false);

		// UPDATE Finance Profit Details
		getFinanceProfitDetailDAO().update(profitDetail, false);

		logger.debug("Leaving ");
	}

	public void listSave(FinScheduleData finDetail, String tableType, long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < finDetail.getFinanceScheduleDetails().size(); i++) {
			finDetail.getFinanceScheduleDetails().get(i).setLastMntBy(finDetail.getFinanceMain().getLastMntBy());
			finDetail.getFinanceScheduleDetails().get(i).setFinReference(finDetail.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getFinanceScheduleDetails().get(i).getSchDate())) {
				seqNo = mapDateSeq.get(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
				mapDateSeq.remove(finDetail.getFinanceScheduleDetails().get(i).getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(finDetail.getFinanceScheduleDetails().get(i).getSchDate(), seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setSchSeq(seqNo);
			finDetail.getFinanceScheduleDetails().get(i).setLogKey(logKey);
		}
		getFinanceScheduleDetailDAO().saveList(finDetail.getFinanceScheduleDetails(), tableType, false);

		// Finance Disbursement Details
		mapDateSeq = new HashMap<Date, Integer>();
		Date curBDay = DateUtility.getAppDate();
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
			finDetail.getDisbursementDetails().get(i).setLogKey(logKey);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, false);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, false);

		logger.debug("Leaving ");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

}
