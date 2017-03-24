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
 * FileName    		:  SuspenseCalculation.java													*                           
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
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.SuspenseService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.BatchUtil;

public class SuspenseCalculation implements Tasklet {

	private Logger					logger			= Logger.getLogger(SuspenseCalculation.class);

	private DataSource			dataSource;
	private SuspenseService	suspenseService;

	String									logRefernce	= "";

	public SuspenseCalculation() {

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
			sqlStatement.close();
			resultSet.close();

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getValueDate().toString()));
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				logRefernce = resultSet.getString("FinReference");
				// Prepare Finance RepayQueue Data
				FinRepayQueue finRepayQueue = doWriteDataToBean(resultSet);

				FinanceMain financeMain = getSuspenseService().getFinanceMain(finRepayQueue.getFinReference());

				getSuspenseService().processSuspense(DateUtility.getValueDate(),financeMain, finRepayQueue);

				processed = resultSet.getRow();
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(processed));
			}

		} catch (Exception e) {
			logger.error("Finrefernce :" + logRefernce, e);
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
		selectSql.append(" WHERE RQ.RpyDate <= ? ");
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
		selectSql.append(" RQ.CustomerID, RQ.FinRpyFor, RQ.SchdPft, RQ.SchdPri, RQ.SchdPftPaid, RQ.SchdPriPaid, ");
		selectSql.append(" FM.ProfitDaysBasis ");
		selectSql.append(" FROM FinRpyQueue RQ  INNER JOIN FinanceMain FM ON FM.FinReference = RQ.FinReference ");
		selectSql.append(" WHERE RQ.RpyDate <= ? ");
		return selectSql.toString();
	}

	/**
	 * Method for Creating RepayQueue Object using resultSet
	 * 
	 * @param FinRepayQueue
	 *          finRepayQueue
	 * @param ResultSet
	 *          resultSet
	 * @return FinRepayQueue finRepayQueue
	 * @throws SQLException
	 */
	private FinRepayQueue doWriteDataToBean(ResultSet resultSet) throws SQLException {

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

		} catch (SQLException e) {
			logger.error("Finrefernce :" + logRefernce, e);
			throw e;
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

	public SuspenseService getSuspenseService() {
		return suspenseService;
	}

	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}

}
