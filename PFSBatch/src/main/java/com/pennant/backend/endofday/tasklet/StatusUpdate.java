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
/*

 *//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  OverdueCalculation.java													*                           
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.StatusService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.util.BatchUtil;

public class StatusUpdate implements Tasklet {
	private Logger			logger	= Logger.getLogger(StatusUpdate.class);

	private DataSource		dataSource;
	private StatusService	statusService;

	public StatusUpdate() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		logger.debug("START: OverDue Recovery Caluclation for Value Date: " + DateUtility.getValueDate());

		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), DateUtility.getValueDate());

		// FETCH Finance Repayment Queues
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		int processed = 0;

		try {

			// Finance Repayments Details
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getValueDate().toString()));
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();
			
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getValueDate().toString()));
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				// Prepare Finance RepayQueue Data
				FinRepayQueue finRepayQueue = doWriteDataToBean(resultSet);

				getStatusService().processStatus(DateUtility.getValueDate(),finRepayQueue);

				processed = resultSet.getRow();
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));

			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {

			if (resultSet != null) {
				resultSet.close();
			}

			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETED: OverDue Recovery Caluclation for Value Date: " + DateUtility.getValueDate());
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for get count of Repay postings
	 * 
	 * @return selQuery
	 */
	private String getCountQuery() {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT count(1)");
		selectSql.append(" FROM Financemain FM ");
		selectSql.append(" INNER JOIN FinRpyQueue RQ ON RQ.FinReference = FM.FinReference ");
		selectSql.append(" WHERE RQ.RpyDate < ? ");
		return selectSql.toString();
	}

	/**
	 * Method for Preparation of Select Query for Preparing resultSet
	 * 
	 * @param selectSql
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT RQ.FinReference, RQ.FinType, RQ.RpyDate, RQ.Branch, ");
		selectSql.append(" RQ.CustomerID, RQ.FinRpyFor, RQ.SchdPft, RQ.SchdPri, RQ.SchdPftPaid, RQ.SchdPriPaid,FM.FinStatus,FM.FinStsReason, ");
		selectSql.append(" FM.ProfitDaysBasis ");
		selectSql.append(" FROM FinRpyQueue RQ  INNER JOIN FinanceMain FM ON FM.FinReference = RQ.FinReference ");
		selectSql.append(" WHERE RQ.RpyDate <= ? ");
		return selectSql.toString();
	}

	/**
	 * Method for Creating RepayQueue Object using resultSet
	 * 
	 * @param FinRepayQueue
	 *            finRepayQueue
	 * @param ResultSet
	 *            resultSet
	 * @return FinRepayQueue finRepayQueue
	 */
	@SuppressWarnings("serial")
	private FinRepayQueue doWriteDataToBean(ResultSet resultSet) {

		FinRepayQueue finRepayQueue = new FinRepayQueue();

		try {
			finRepayQueue.setFinReference(resultSet.getString("FinReference"));
			finRepayQueue.setBranch(resultSet.getString("Branch"));
			finRepayQueue.setFinType(resultSet.getString("FinType"));
			finRepayQueue.setCustomerID(resultSet.getLong("CustomerID"));
			finRepayQueue.setRpyDate(resultSet.getDate("RpyDate"));
			finRepayQueue.setFinRpyFor(resultSet.getString("FinRpyFor"));
			finRepayQueue.setSchdPft(resultSet.getBigDecimal("SchdPft"));
			finRepayQueue.setSchdPri(resultSet.getBigDecimal("SchdPri"));
			finRepayQueue.setSchdPftPaid(resultSet.getBigDecimal("SchdPftPaid"));
			finRepayQueue.setSchdPriPaid(resultSet.getBigDecimal("SchdPriPaid"));
			finRepayQueue.setFinStatus(resultSet.getString("FinStatus"));
			finRepayQueue.setFinStsReason(resultSet.getString("FinStsReason"));

		} catch (SQLException e) {
			logger.warn("Exception: ", e);
			throw new DataAccessException(e.getMessage()) {
			};
		}
		return finRepayQueue;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public StatusService getStatusService() {
		return statusService;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}
}
