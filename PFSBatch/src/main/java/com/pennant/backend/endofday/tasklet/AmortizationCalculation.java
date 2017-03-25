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
 * FileName    		:  AmortizationCalculation.java													*                           
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
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.AccrualService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.BatchUtil;

public class AmortizationCalculation implements Tasklet {
	private Logger			logger			= Logger.getLogger(AmortizationCalculation.class);

	private DataSource		dataSource;
	private AccrualService	accrualService;

	private Date			dateValueDate	= null;
	int						accruals		= 0;
	int						processed		= 0;

	public AmortizationCalculation() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = DateUtility.getValueDate();

		logger.debug("START: Amortization Caluclation for Value Date: " + dateValueDate);

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			statement = connection.prepareStatement(getCountQuery());
			statement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = statement.executeQuery();
			int accruals = 0;
			if (resultSet.next()) {
				accruals = resultSet.getInt(1);
			}
			resultSet.close();
			statement.close();
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(accruals));

			statement = connection.prepareStatement(getSelectQuery());
			statement.setDate(1, DateUtility.getDBDate(dateValueDate.toString()));
			resultSet = statement.executeQuery();

			while (resultSet.next()) {

				String finReference = resultSet.getString("FinReference");
				getAccrualService().calculateAccruals(finReference, dateValueDate);
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

			if (statement != null) {
				statement.close();
			}
		}

		logger.debug("COMPLETE: Amortization Caluclation for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Select Query To get Active finances based on data
	 * 
	 * @return sqlQuery
	 */
	private String getCountQuery() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" SELECT count(F.FinReference) FROM FinanceMain F ");
		sqlQuery.append(" WHERE F.FinIsActive = 1");
		sqlQuery.append(" AND F.FinStartDate <=? ");
		return sqlQuery.toString();
	}

	/**
	 * Method for preparation of Select Query To get Active finances based on data
	 * 
	 * @return sqlQuery
	 */
	private String getSelectQuery() {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(" SELECT F.FinReference  FROM FinanceMain F ");
		sqlQuery.append(" WHERE F.FinIsActive = 1");
		sqlQuery.append(" AND F.FinStartDate <=? ");
		return sqlQuery.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

}
