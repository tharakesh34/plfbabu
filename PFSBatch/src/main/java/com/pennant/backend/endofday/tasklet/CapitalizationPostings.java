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
 * FileName    		:  CapitalizationPostings.java													*                           
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
package com.pennant.backend.endofday.tasklet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SuspensePostingUtil;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;

public class CapitalizationPostings implements Tasklet {

	private Logger					logger			= Logger.getLogger(CapitalizationPostings.class);

	private FinanceProfitDetailDAO	financeProfitDetailDAO;
	private SuspensePostingUtil		suspensePostingUtil;

	private DataSource				dataSource;

	private Date					dateValueDate	= null;
	private Date					dateAppDate		= null;

	private ExecutionContext		jobExecutionContext;
	private ExecutionContext		stepExecutionContext;

	public CapitalizationPostings() {

	}

	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getAppValueDate();
		dateAppDate = DateUtility.getAppDate();

		logger.debug("START: Capitalization Postings for Value Date: " + dateValueDate);

		jobExecutionContext = context.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();

		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		FinanceProfitDetail pftDetail = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			resultSet = sqlStatement.executeQuery();

			List<FinanceProfitDetail> pftDetailsList = new ArrayList<FinanceProfitDetail>();

			while (resultSet.next()) {

				//Amount Codes preparation using FinProfitDetails
				AEEvent aeEvent = new AEEvent();
				AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
				aeEvent.setFinReference(resultSet.getString("FinReference"));
				amountCodes.setCpzCur(resultSet.getBigDecimal("CpzAmount"));
				amountCodes.setCpzPrv(resultSet.getBigDecimal("PrvCpzAmt") == null ? BigDecimal.ZERO : resultSet
						.getBigDecimal("PrvCpzAmt"));
				amountCodes.setCpzTot(resultSet.getBigDecimal("TotalCpz"));
				amountCodes.setCpzNxt(amountCodes.getCpzTot().subtract(amountCodes.getCpzPrv())
						.subtract(amountCodes.getCpzCur()));

				// **** Accounting Set Execution for Amortization ******//

				aeEvent.setFinReference(resultSet.getString("FinReference"));
				aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_COMPOUND);
				aeEvent.setBranch(resultSet.getString("FinBranch"));
				aeEvent.setCcy(resultSet.getString("FinCcy"));
				aeEvent.setPostDate(dateAppDate);
				aeEvent.setValueDate(dateValueDate);
				aeEvent.setSchdDate(resultSet.getDate("NextRepayDate"));
				aeEvent.setFinType(resultSet.getString("FinType"));
				aeEvent.setCustID(resultSet.getLong("CustID"));


				//Postings Process
				//FIXME: 050517 Needs to fill return dataset
				//getPostingsPreparationUtil().processPostingDetails(aeEvent);

				//Update Finance Profit Details
				pftDetail = new FinanceProfitDetail();
				pftDetail.setTdPftCpz(resultSet.getBigDecimal("CpzAmount").add(resultSet.getBigDecimal("PrvCpzAmt")));
				pftDetail.setLastMdfDate(dateValueDate);

				pftDetailsList.add(pftDetail);
				if (pftDetailsList.size() == 500) {
					getFinanceProfitDetailDAO().updateCpzDetail(pftDetailsList, "");
					pftDetailsList.clear();
				}
			}

			if (pftDetailsList.size() > 0) {
				getFinanceProfitDetailDAO().updateCpzDetail(pftDetailsList, "");
				pftDetailsList = null;
			}

		} catch (SQLException e) {
			logger.error("Exception: ", e);
			throw new SQLException(e.getMessage()) {
			};
		} finally {
			pftDetail = null;
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();

			}
		}

		logger.debug("COMPLETE: Capitalization Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Select Query To get Schedule data
	 * 
	 * @param selQuery
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder selQuery = new StringBuilder(" SELECT T1.FinReference, T1.FinBranch, T1.FinType, ");
		selQuery.append(" T1.FinCcy , T1.CustID , T2.CpzAmount, T1.TotalCpz ,");
		selQuery.append(" (SELECT SUM(CpzAmount)  TotCurCpzAmt FROM  FinScheduleDetails ");
		selQuery.append(" WHERE DefSchdDate < '");
		selQuery.append(dateValueDate);
		selQuery.append("' )  PrvCpzAmt ");
		selQuery.append(" FROM FinanceMain  T1 INNER JOIN RMTFinanceTypes  T3  ");
		selQuery.append(" ON T1.FinType = T3.FinType , FinScheduleDetails  T2 ");
		selQuery.append(" WHERE T1.FinReference = T2.FinReference ");
		selQuery.append(" AND T2.DefSchdDate = '");
		selQuery.append(dateValueDate);
		selQuery.append("'");
		selQuery.append(" AND T2.CpzOnSchDate= 1 AND T2.CpzAmount > 0 AND T1.FinIsActive = 1");
		return selQuery.toString();

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public SuspensePostingUtil getSuspensePostingUtil() {
		return suspensePostingUtil;
	}

	public void setSuspensePostingUtil(SuspensePostingUtil suspensePostingUtil) {
		this.suspensePostingUtil = suspensePostingUtil;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}