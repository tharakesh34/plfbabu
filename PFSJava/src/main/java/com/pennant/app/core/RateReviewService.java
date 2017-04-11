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
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceRateReviewDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRateReview;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pff.core.TableType;

public class RateReviewService extends ServiceHelper {

	private static final long		serialVersionUID	= -4939080414435712845L;

	private Logger					logger				= Logger.getLogger(RateReviewService.class);

	private FinanceDisbursementDAO	financeDisbursementDAO;
	private RepayInstructionDAO		repayInstructionDAO;
	private FinLogEntryDetailDAO	finLogEntryDetailDAO;
	private FinanceRateReviewDAO	financeRateReviewDAO;

	//fetch rates changed yesterday or effective date is today
	public static final String		QUERY_BASERATE		= "SELECT BRType,Currency,BREffDate,BRRate,LastMdfDate FROM RMTBaserates";
	public static final String		QUERY_FINANCE		= "SELECT rv.FinReference FROM  (Select Distinct FSD.FinReference FinReference FROM FinScheduleDetails FSD WHERE"
																+ " FSD.RvwOnSchDate = 1 AND FSD.BaseRate = ? ) rv  INNER JOIN Financemain fm ON fm.FinReference = rv.FinReference"
																+ " WHERE fm.finccy = ? AND fm.finisActive=1 AND fm.custid = ? ";

	public RateReviewService() {
		super();
	}

	public void processRateReview(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {

			sqlStatement = connection.prepareStatement(QUERY_BASERATE);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				BaseRate baseRate = prepareBaseRate(resultSet);
				processRateReview(connection, custId, baseRate, date);
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

	private void processRateReview(Connection connection, long custId, BaseRate baseRate, Date date) throws Exception {

		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			sqlStatement = connection.prepareStatement(QUERY_FINANCE);
			sqlStatement.setString(1, baseRate.getBRType());
			sqlStatement.setString(2, baseRate.getCurrency());
			sqlStatement.setLong(3, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				String finref = resultSet.getString("finReference");

				boolean rateRveCompled = false;
				FinanceRateReview finRateReview = null;
				List<FinanceRateReview> list = financeRateReviewDAO.getFinanceRateReviewById(finref, date);
				if (!list.isEmpty()) {
					rateRveCompled = true;
					finRateReview = list.get(0);
				}

				if (!rateRveCompled) {
					doRateReview(finref, date, baseRate);
				} else {
					if (finRateReview != null) {
						finRateReview.setFinReference(finref);
						finRateReview.setRateType(baseRate.getBRType());
						finRateReview.setCurrency(baseRate.getCurrency());
						finRateReview.setValueDate(date);
						finRateReview.setEffectiveDate(baseRate.getBREffDate());
						financeRateReviewDAO.save(finRateReview);
					}
				}

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

	private void doRateReview(String finref, Date date, BaseRate baseRate) throws Exception {

		//rate review will be on start Day.
		Date businessDate = DateUtility.addDays(date, 1);
		FinScheduleData finScheduleData = getFinSchDataByFinRef(finref, "_AView");

		FinanceMain finMain = finScheduleData.getFinanceMain();
		FinanceType finType = finScheduleData.getFinanceType();
		List<FinanceScheduleDetail> schList = finScheduleData.getFinanceScheduleDetails();
		String rvwAppFor = StringUtils.trimToEmpty(finType.getFinRvwRateApplFor());

		// if not auto rate review 
		if ("".equals(rvwAppFor) || rvwAppFor.equals(CalculationConstants.RATEREVIEW_NORVW)) {
			return;
		}

		if (businessDate.compareTo(finMain.getGrcPeriodEndDate()) <= 0) {
			if (!finMain.isAllowGrcPftRvw()) {
				return;
			}
		} else {
			if (!finMain.isAllowRepayRvw()) {
				return;
			}
		}

		//the base rate is not applicable for the finance if effective date is after maturity or before loan start
		if (baseRate.getBREffDate().compareTo(finMain.getMaturityDate()) >= 0
				|| baseRate.getBREffDate().compareTo(finMain.getFinStartDate()) < 0) {
			return;
		}

		if (finMain.getNextRepayRvwDate().compareTo(finMain.getMaturityDate()) == 0) {
			return;
		}
		
		
		Date effectiveDate = null;

		//if rate change allowed any date , then check last modified date is grater than yesterday and get the effective date 
		//else check next review date of the loan is equal to value date.
		if (finType.isRateChgAnyDay()) {

			java.sql.Date yesterDay = DateUtility.addDays(date, -1);

			if (baseRate.getLastMdfDate().compareTo(yesterDay) > 0) {
				effectiveDate = baseRate.getBREffDate();
			} else {
				return;
			}

		} else {
			if (finMain.getNextRepayRvwDate().compareTo(date) == 0
					&& baseRate.getBREffDate().compareTo(finMain.getNextRepayRvwDate()) <= 0) {
				effectiveDate = finMain.getNextRepayRvwDate();
			} else {
				return;
			}
		}

		if (effectiveDate == null) {
			return;
		}
		

		//Rate review required

		//Log Entry before the schedule change Saving for Total Finance Detail
		listSave(finScheduleData, "_Log", date);

		Date eventFromDate = null;
		Date eventToDate = finMain.getMaturityDate();
		String rvwCalon = finType.getFinSchCalCodeOnRvw();

		Date nextReviewDate = null;
		Date nextUnpaiddate = null;

		for (FinanceScheduleDetail finSchDetail : schList) {

			Date schdate = finSchDetail.getSchDate();

			if (schdate.compareTo(effectiveDate) < 0) {
				continue;
			}

			if (schdate.compareTo(businessDate) < 0) {
				continue;
			}

			//grace review
			if (nextReviewDate == null) {
				nextReviewDate = schdate;
			}

			if (nextUnpaiddate == null
					&& (finSchDetail.isRepayOnSchDate() && !finSchDetail.isSchPftPaid() && !finSchDetail.isSchPriPaid())) {
				nextUnpaiddate = schdate;
			}

			if (nextReviewDate != null && nextUnpaiddate != null) {
				break;
			}

		}

		if (rvwAppFor.equals(CalculationConstants.RATEREVIEW_RVWUPR)) {
			eventFromDate = nextUnpaiddate;
		} else if (rvwAppFor.equals(CalculationConstants.RATEREVIEW_RVWALL)) {
			eventFromDate = nextReviewDate;
		}

		finMain.setEventFromDate(eventFromDate);
		finMain.setEventToDate(eventToDate);
		finMain.setRecalType(rvwCalon);

		// Rate Changes applied for Finance Schedule Data
		finScheduleData = ScheduleCalculator.refreshRates(finScheduleData);

		// Finance Profit Details
		FinanceProfitDetail profitDetail = AEAmounts.calProfitDetails(finMain, schList, null, businessDate);
		// Amount Codes Details Preparation
		AEAmountCodes amountCodes = AEAmounts.procCalAEAmounts(finMain, profitDetail, businessDate);

		// DataSet preparation
		DataSet dataSet = AEAmounts.createDataSet(finMain, AccountEventConstants.ACCEVENT_RATCHG, date, businessDate);
		List<ReturnDataSet> list = prepareAccounting(dataSet, amountCodes, finScheduleData.getFinanceType());
		saveAccounting(list);
		// Update New Finance Schedule Details Data
		saveOrUpdate(finScheduleData, profitDetail);
		FinanceRateReview rateReview = new FinanceRateReview();
		rateReview.setFinReference(finref);
		rateReview.setRateType(baseRate.getBRType());
		rateReview.setCurrency(baseRate.getCurrency());
		rateReview.setValueDate(date);
		rateReview.setEffectiveDate(baseRate.getBREffDate());
		rateReview.setEventFromDate(eventFromDate);
		rateReview.setEventToDate(eventToDate);
		rateReview.setRecalFromdate(finMain.getRecalFromDate());
		rateReview.setRecalToDate(finMain.getRecalToDate());
		financeRateReviewDAO.save(rateReview);

	}

	private BaseRate prepareBaseRate(ResultSet resultSet) throws SQLException {
		BaseRate baseRate = new BaseRate();
		baseRate.setBRType(resultSet.getString("BRType"));
		baseRate.setCurrency(resultSet.getString("Currency"));
		baseRate.setBREffDate(resultSet.getDate("BREffDate"));
		baseRate.setBRRate(resultSet.getBigDecimal("BRRate"));
		baseRate.setLastMdfDate(resultSet.getDate("LastMdfDate"));

		return baseRate;
	}

	/**
	 * Method for fetching Finance Schedule Data based on FinReference
	 * 
	 * @param finRef
	 * @param type
	 * @return
	 */
	public FinScheduleData getFinSchDataByFinRef(String finRef, String type) {
		logger.debug("Entering");
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinReference(finRef);
		FinanceMain finMain = getFinanceMainDAO().getFinanceMainById(finRef, type, false);
		FinanceType fintype = getFinanceTypeDAO().getFinanceTypeByID(finMain.getFinType(), type);
		List<FinanceScheduleDetail> list = getFinanceScheduleDetailDAO().getFinScheduleDetails(finRef, type, false);
		List<RepayInstruction> insrt = repayInstructionDAO.getRepayInstructions(finRef, type, false);
		finSchData.setFinanceMain(finMain);
		finSchData.setFinanceType(fintype);
		finSchData.setFinanceScheduleDetails(list);
		finSchData.setRepayInstructions(insrt);
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

	public void listSave(FinScheduleData finDetail, String tableType, Date valueDate) {
		logger.debug("Entering ");
		//Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();
		entryDetail.setFinReference(finDetail.getFinReference());
		entryDetail.setEventAction(AccountEventConstants.ACCEVENT_RATCHG);
		entryDetail.setSchdlRecal(true);
		entryDetail.setPostDate(valueDate);
		entryDetail.setReversalCompleted(false);
		long logKey = finLogEntryDetailDAO.save(entryDetail);

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
		financeDisbursementDAO.saveList(finDetail.getDisbursementDetails(), tableType, false);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		repayInstructionDAO.saveList(finDetail.getRepayInstructions(), tableType, false);

		logger.debug("Leaving ");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public void setFinanceRateReviewDAO(FinanceRateReviewDAO financeRateReviewDAO) {
		this.financeRateReviewDAO = financeRateReviewDAO;
	}

}
