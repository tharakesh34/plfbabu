
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
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  OverDueRecoveryCalculation.java													*                           
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
package com.pennant.backend.endofday.overduepayments;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;

public class OverDueRecoveryCalculation implements Tasklet {

	private Logger logger = Logger.getLogger(OverDueRecoveryCalculation.class);
	
	private OverDueRecoveryPostingsUtil recoveryPostingsUtil;	
	private DataSource dataSource;
	
	private Date dateValueDate = null;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());

		logger.debug("START: OverDue Recovery Caluclation for Value Date: "+ dateValueDate);

		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		// FETCH Finance Repayment Queues
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		FinRepayQueue finRepayQueue = null;

		try {

			//Finance Repayments Details
			connection = DataSourceUtils.doGetConnection(getDataSource());	
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			resultSet.next();
			BatchUtil.setExecution(context, "TOTAL", String.valueOf(resultSet.getInt(1)));

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				
				// Prepare Finance RepayQueue Data
				finRepayQueue = doWriteDataToBean(finRepayQueue, resultSet);
				
				//Overdue Details preparation 
				getRecoveryPostingsUtil().recoveryCalculation(finRepayQueue, resultSet.getString("ProfitDaysBasis"), dateValueDate, true, false);
			}

		} catch (Exception e) {
			logger.error(e);
			throw e;
		}  finally {
			finRepayQueue = null;

			if(resultSet!= null) {
				resultSet.close();
			}

			if(sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETED: OverDue Recovery Caluclation for Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Method for get count of Repay postings
	 * @return selQuery 
	 */
	private String getCountQuery() {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT count(1)");
		selectSql.append(" FROM Financemain FM ");
		selectSql.append(" INNER JOIN FinRpyQueue RQ ON RQ.FinReference = FM.FinReference ");
		selectSql.append(" WHERE RQ.RpyDate <= ? AND (SchdIsPftPaid = 0 OR SchdIsPriPaid = 0)");
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
		selectSql.append(" SELECT RQ.FinReference, RQ.FinType, RQ.RpyDate, RQ.Branch, " );
		selectSql.append(" RQ.CustomerID, RQ.FinRpyFor, RQ.SchdPft, RQ.SchdPri, RQ.SchdPftPaid, RQ.SchdPriPaid, " );
		selectSql.append(" FM.ProfitDaysBasis " );
		selectSql.append(" FROM FinRpyQueue RQ  INNER JOIN FinanceMain FM ON FM.FinReference = RQ.FinReference " );
		selectSql.append(" WHERE RQ.RpyDate <= ? AND (SchdIsPftPaid = 0 OR SchdIsPriPaid = 0) " );
		return selectSql.toString();
	}

	/**
	 * Method for Creating RepayQueue Object using resultSet
	 * 
	 * @param FinRepayQueue finRepayQueue
	 * @param ResultSet resultSet
	 * @return FinRepayQueue finRepayQueue
	 */
	@SuppressWarnings("serial")
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, ResultSet resultSet) {

		finRepayQueue = new FinRepayQueue();

		try {
			finRepayQueue.setFinReference(resultSet.getString("FinReference"));
			finRepayQueue.setBranch(resultSet.getString("Branch"));
			finRepayQueue.setFinType(resultSet.getString("FinType"));
			finRepayQueue.setCustomerID(resultSet.getLong("CustomerID"));
			finRepayQueue.setRpyDate(resultSet.getDate("RpyDate"));
			finRepayQueue.setFinPriority(resultSet.getInt("FinPriority"));
			finRepayQueue.setFinRpyFor(resultSet.getString("FinRpyFor"));
			finRepayQueue.setSchdPft(resultSet.getBigDecimal("SchdPft"));
			finRepayQueue.setSchdPri(resultSet.getBigDecimal("SchdPri"));
			finRepayQueue.setSchdPftPaid(resultSet.getBigDecimal("SchdPftPaid"));
			finRepayQueue.setSchdPriPaid(resultSet.getBigDecimal("SchdPriPaid"));

		} catch (SQLException e) {
			throw new DataAccessException(e.getMessage()) { };
		}
		return finRepayQueue;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}
	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
		return recoveryPostingsUtil;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}

}
