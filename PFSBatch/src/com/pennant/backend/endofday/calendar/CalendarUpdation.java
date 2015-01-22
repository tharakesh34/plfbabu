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
 * FileName    		:  CalendarUpdation.java													*                           
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
package com.pennant.backend.endofday.calendar;

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

import com.pennant.Interface.service.CalendarInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.EquationInterfaceException;

public class CalendarUpdation implements Tasklet {

	private Logger logger = Logger.getLogger(CalendarUpdation.class);

	private CalendarInterfaceService calendarInterfaceService;
	private AccountsDAO accountsDAO;
	private DataSource dataSource;

	private Date dateValueDate = null;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {

		//Date Parameter List
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());

		logger.debug("START: CalendarUpdation for Value Date: "+ dateValueDate);
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;

		try {
			
			// Accounts Accrual Balance reset to Zero
			getAccountsDAO().updateAccrualBalance(); 

			//Value Date updation with Application Date
			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(prepareUpdateQuery());
			sqlStatement.setString(1, SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());
			sqlStatement.setString(2, PennantConstants.APP_DATE_VALUE);
			sqlStatement.executeUpdate();

			SystemParameterDetails.setParmDetails(PennantConstants.APP_DATE_VALUE, SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString());

			//Calendar Updation Calculation Process
			getCalendarInterfaceService().calendarUpdate();

		} catch (EquationInterfaceException e) {
			logger.error(e);
			throw new EquationInterfaceException(e.getMessage());
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage());
		}finally {
			if(sqlStatement != null){
				sqlStatement.close();
			}
		} 

		logger.debug("COMPLETE: CalendarUpdation for Value Date: "+ dateValueDate);
		
		ExecutionStatus executionStatus = new ExecutionStatus();
		context.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("current", executionStatus);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Method for preparation of Update Query To update 
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String prepareUpdateQuery() {
		StringBuilder updateQuery = new StringBuilder(" UPDATE SMTparameters SET SysParmValue = ?");
		updateQuery.append(" Where SysParmCode = ?");
		return updateQuery.toString();

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCalendarInterfaceService(CalendarInterfaceService calendarInterfaceService) {
		this.calendarInterfaceService = calendarInterfaceService;
	}
	public CalendarInterfaceService getCalendarInterfaceService() {
		return calendarInterfaceService;
	}

	public void setAccountsDAO(AccountsDAO accountsDAO) {
		this.accountsDAO = accountsDAO;
	}
	public AccountsDAO getAccountsDAO() {
		return accountsDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}

}
