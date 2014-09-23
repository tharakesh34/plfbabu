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
 *
 * FileName    		:  RateReview.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
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
package com.pennant.backend.endofday.ratereview;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FinanceProfitDetailFiller;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.rits.cloning.Cloner;

public class RateReview implements Tasklet {

	private Logger logger = Logger.getLogger(RateReview.class);

	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDisbursementDAO 	 financeDisbursementDAO;
	private RepayInstructionDAO repayInstructionDAO;
	private DefermentDetailDAO defermentDetailDAO;
	private DefermentHeaderDAO defermentHeaderDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinLogEntryDetailDAO	 finLogEntryDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceProfitDetailFiller financeProfitDetailFiller;
	private PostingsPreparationUtil postingsPreparationUtil;
	private DataSource dataSource;

	private Date dateValueDate = null;
	private Date dateAppDate = null;
	
	int processed = 0;
	int postings = 0;
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		dateAppDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());

		logger.debug("START: Rate Review for Value Date: " + DateUtility.addDays(dateValueDate, -1));
		
		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			sqlStatement.setDate(2, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			resultSet.next();			
			BatchUtil.setExecution(context,  "TOTAL", String.valueOf(resultSet.getInt(1)));
			
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			sqlStatement.setDate(2, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();

			FinScheduleData finScheduleData = null;
			FinanceProfitDetail profitDetail = null;

			while (resultSet.next()) {

				// Fetching Finance Schedule Data by FinReference
				finScheduleData = getFinSchDataByFinRef(resultSet.getString("FinReference"), "_AView");
				
				Cloner cloner = new Cloner();
				FinScheduleData orgFinScheduleData = cloner.deepClone(finScheduleData);

				// Rate Changes applied for Finance Schedule Data
				finScheduleData = ScheduleCalculator.refreshRates(finScheduleData);

				// Finance Profit Details
				profitDetail = getFinanceProfitDetailDAO().getFinProfitDetailsById(resultSet.getString("FinReference"));

				// Amount Codes Details Preparation
				AEAmountCodes amountCodes = AEAmounts.procAEAmounts(finScheduleData.getFinanceMain(),
						finScheduleData.getFinanceScheduleDetails(),profitDetail, dateValueDate);

				// DataSet preparation
				DataSet dataSet = AEAmounts.createDataSet(
						finScheduleData.getFinanceMain(), "RATCHG",dateValueDate, dateAppDate);

				// Posting Preparation Util
				List<Object> odObjDetails = getPostingsPreparationUtil().processPostingDetails(dataSet,amountCodes,true,
						finScheduleData.getFinanceType().isAllowRIAInvestment(), "Y", dateAppDate, false, Long.MIN_VALUE);
				
				if(odObjDetails!=null && !odObjDetails.isEmpty()) {
					if((Boolean)odObjDetails .get(0)) {
						postings++;
					}
				}

				// Preparation Of Profit Details with new Schedule Data
				profitDetail = getFinanceProfitDetailFiller().prepareFinPftDetails(amountCodes, profitDetail,dateValueDate);

				// Update New Finance Schedule Details Data
				listSave(finScheduleData, profitDetail);
				
				//Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinReference(finScheduleData.getFinReference());
				entryDetail.setEventAction("RATCHG");
				entryDetail.setSchdlRecal(true);
				entryDetail.setPostDate(dateValueDate);
				entryDetail.setReversalCompleted(false);
				long logKey = getFinLogEntryDetailDAO().save(entryDetail);
				
				//Log Entry Saving for TOtal Finance Detail
				listSave(orgFinScheduleData, "_Log", logKey);
				
				processed = resultSet.getRow();
				BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
				BatchUtil.setExecution(context,  "INFO", getInfo());
			}
			
			BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(processed));
			BatchUtil.setExecution(context,  "INFO", getInfo());

		} catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			if(resultSet != null) {
				resultSet.close();
			}
			
			if(sqlStatement != null) {
				sqlStatement.close();
			}
		}
		logger.debug("COMPLETE: Rate Review for Value Date: " + DateUtility.addDays(dateValueDate, -1));
		return RepeatStatus.FINISHED;

	}
	
	/**
	 * Method for preparation of Select Query To get Finances , which are
	 * changed rates on Particular dates
	 * 
	 * @param selQuery
	 * @return
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT count(fm.FinReference)");
		selQuery.append(" FROM FinanceMain AS fm");
		selQuery.append(" WHERE fm.FinIsActive ='1'  AND AllowGrcPftRvw = '1' AND LastRepayRvwDate < GrcPeriodEndDate ");
		selQuery.append(" AND NextGrcPftRvwDate = ? AND GraceBaseRate IS NOT NULL AND GraceBaseRate <> ''");
		selQuery.append(" UNION ");
		selQuery.append(" SELECT count(fm.FinReference) ");
		selQuery.append(" FROM FinanceMain AS fm");
		selQuery.append(" WHERE fm.FinIsActive ='1'  AND AllowRepayRvw = '1' AND LastRepayRvwDate < MaturityDate ");
		selQuery.append(" AND NextRepayRvwDate = ? AND RepayBaseRate IS NOT NULL AND RepayBaseRate <> ''");
		return selQuery.toString();

	}

	/**
	 * Method for preparation of Select Query To get Finances , which are
	 * changed rates on Particular dates
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT fm.FinReference AS FinReference ");
		selQuery.append(" FROM FinanceMain AS fm");
		selQuery.append(" WHERE fm.FinIsActive ='1'  AND AllowGrcPftRvw = '1' AND LastRepayRvwDate < GrcPeriodEndDate ");
		selQuery.append(" AND NextGrcPftRvwDate = ? AND GraceBaseRate IS NOT NULL AND GraceBaseRate <> ''");
		selQuery.append(" UNION ");
		selQuery.append(" SELECT fm.FinReference AS FinReference ");
		selQuery.append(" FROM FinanceMain AS fm");
		selQuery.append(" WHERE fm.FinIsActive ='1'  AND AllowRepayRvw = '1' AND LastRepayRvwDate < MaturityDate ");
		selQuery.append(" AND NextRepayRvwDate = ? AND RepayBaseRate IS NOT NULL AND RepayBaseRate <> ''");
		return selQuery.toString();

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
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(financeReference, type, false));
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(financeReference, type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(financeReference, type, false));
		finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(financeReference, type, false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(financeReference, type, false));
		finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
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

		// Finance Deferment Header Details
		getDefermentHeaderDAO().updateList(schdueleData.getDefermentHeaders(), "", false);

		// Finance Deferment Details
		getDefermentDetailDAO().updateList(schdueleData.getDefermentDetails(), "", false);

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
		Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		for (int i = 0; i < finDetail.getDisbursementDetails().size(); i++) {
			finDetail.getDisbursementDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
			int seqNo = 0;

			if (mapDateSeq.containsKey(finDetail.getDisbursementDetails().get(i).getDisbDate())) {
				seqNo = mapDateSeq.get(finDetail.getDisbursementDetails().get(i).getDisbDate());
				mapDateSeq.remove(finDetail.getDisbursementDetails().get(i).getDisbDate());
			} 
			seqNo = seqNo + 1;

			mapDateSeq.put(finDetail.getDisbursementDetails().get(i).getDisbDate(), seqNo);
			finDetail.getDisbursementDetails().get(i).setDisbSeq(seqNo);
			finDetail.getDisbursementDetails().get(i).setDisbIsActive(true);
			finDetail.getDisbursementDetails().get(i).setDisbDisbursed(true);
			finDetail.getDisbursementDetails().get(i).setLogKey(logKey);
		}
		getFinanceDisbursementDAO().saveList(finDetail.getDisbursementDetails(), tableType, false);

		//Finance Defferment Header Details
		for (int i = 0; i < finDetail.getDefermentHeaders().size(); i++) {
			finDetail.getDefermentHeaders().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDefermentHeaders().get(i).setLogKey(logKey);
		}
		getDefermentHeaderDAO().saveList(finDetail.getDefermentHeaders(), tableType, false);

		//Finance Defferment Details
		for (int i = 0; i < finDetail.getDefermentDetails().size(); i++) {
			finDetail.getDefermentDetails().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getDefermentDetails().get(i).setLogKey(logKey);
		}
		getDefermentDetailDAO().saveList(finDetail.getDefermentDetails(), tableType, false);

		//Finance Repay Instruction Details
		for (int i = 0; i < finDetail.getRepayInstructions().size(); i++) {
			finDetail.getRepayInstructions().get(i).setFinReference(finDetail.getFinReference());
			finDetail.getRepayInstructions().get(i).setLogKey(logKey);
		}
		getRepayInstructionDAO().saveList(finDetail.getRepayInstructions(), tableType, false);

		logger.debug("Leaving ");
	}
	
	private String getInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append("Total Rate Change Posting's").append(": ").append(postings);
		return builder.toString();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}
	public void setFinanceDisbursementDAO(
			FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}
	
	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		this.defermentDetailDAO = defermentDetailDAO;
	}

	public DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		this.defermentHeaderDAO = defermentHeaderDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}
	
	public FinLogEntryDetailDAO getFinLogEntryDetailDAO() {
		return finLogEntryDetailDAO;
	}
	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailFiller(FinanceProfitDetailFiller financeProfitDetailFiller) {
		this.financeProfitDetailFiller = financeProfitDetailFiller;
	}
	public FinanceProfitDetailFiller getFinanceProfitDetailFiller() {
		return financeProfitDetailFiller;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
