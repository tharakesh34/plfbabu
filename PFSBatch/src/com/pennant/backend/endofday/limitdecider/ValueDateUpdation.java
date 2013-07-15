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
 * FileName    		:  ValueDateUpdation.java													*                           
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
package com.pennant.backend.endofday.limitdecider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;

public class ValueDateUpdation implements Tasklet {
	
	private Logger logger = Logger.getLogger(ValueDateUpdation.class);

	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
		
	private Date dateValueDate = null;

	@SuppressWarnings("serial")
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());

		logger.debug("START: Updation of ValueDate on Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;
		StringBuffer updateQuery = new StringBuffer();
		updateQuery = prepareUpdateQuery(updateQuery, DateUtility.addDays(dateValueDate, 1).toString(), "APP_VALUEDATE");

		try {

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(updateQuery.toString());
			sqlStatement.executeUpdate();
			
			//Value Date Updation 
			SystemParameterDetails.setParmDetails("APP_VALUEDATE", DateUtility.addDays(dateValueDate, 1).toString());
			
		} catch (SQLException e) {
			logger.error(e);
			try {
				throw new SQLException(e.getMessage()) {};
			} catch (SQLException exception) {
				logger.error(exception);
			}
		} finally {
			try {
				sqlStatement.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}

		logger.debug("COMPLETE: Updation of ValueDate on Value Date: "+ dateValueDate);
		return RepeatStatus.FINISHED;
	} 

	/**
	 * Method for preparation of Update Query To update APP_VALUEDATE
	 * 
	 * @param updateQuery
	 * @return
	 */
	private StringBuffer prepareUpdateQuery(StringBuffer updateQuery, String value, 
			String parameterCode) {
		
		updateQuery.append(" UPDATE SMTparameters SET SysParmValue = '"+value+"'");
		updateQuery.append(" Where SysParmCode ='"+parameterCode+"'");
		return updateQuery;
		
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}

	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}
	
}
