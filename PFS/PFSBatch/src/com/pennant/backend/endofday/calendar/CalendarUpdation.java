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
import java.util.Calendar;
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
import com.pennant.app.util.HolidayUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.coreinterface.exception.EquationInterfaceException;

public class CalendarUpdation implements Tasklet {

	private Logger logger = Logger.getLogger(CalendarUpdation.class);

	private CalendarInterfaceService calendarInterfaceService;
	private AccountsDAO accountsDAO;
	private DataSource dataSource;
	private BatchAdminDAO batchAdminDAO;
	
	private Date dateValueDate = null;
	

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		
		//Date Parameter List
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
		logger.debug("START: CalendarUpdation for Value Date: "+ dateValueDate);
		
		context.getStepContext().getStepExecution().getExecutionContext().put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);
	
		Connection connection = null;
		PreparedStatement sqlStatement = null;
		StringBuffer updateQuery = new StringBuffer();
		
		// Accounts Accrual Balance reset to Zero
		getAccountsDAO().updateAccrualBalance(); 
		
		try {
			
			//Value Date updation with Application Date
			updateQuery = prepareUpdateQuery(updateQuery,SystemParameterDetails.getSystemParameterValue("APP_DATE").toString(),
					"APP_VALUEDATE");

			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(updateQuery.toString());
			sqlStatement.executeUpdate();
			
			SystemParameterDetails.setParmDetails("APP_VALUEDATE", SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());
			
			//Calendar Updation 
			boolean isUpdated = getCalendarInterfaceService().calendarUpdate();

			//Process for Next BussinessDate only if Calendar days are updated
			if(isUpdated){

				Calendar calendar = HolidayUtil.getNextBusinessDate("GEN", "N", dateValueDate);
				updateQuery = prepareUpdateQuery(updateQuery,DateUtility.formatUtilDate(calendar.getTime(),"yyyy-MM-dd"), "APP_NEXT_BUS_DATE");

				connection = DataSourceUtils.doGetConnection(dataSource);
				sqlStatement = connection.prepareStatement(updateQuery.toString());
				sqlStatement.executeUpdate();
				
			}
			
			//Update PHASE Parameter
			updateQuery = prepareUpdateQuery(updateQuery,"EOD","PHASE");
			sqlStatement = connection.prepareStatement(updateQuery.toString());
			sqlStatement.executeUpdate();
			
			//Reset System Parameter Value
			SystemParameterDetails.setParmDetails("PHASE", "EOD");
						
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
		
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Method for preparation of Update Query To update 
	 * 
	 * @param updateQuery
	 * @return
	 */
	private StringBuffer prepareUpdateQuery(StringBuffer updateQuery, String parameterValue,
			String parameterCode) {
		
		updateQuery.append(" UPDATE SMTparameters SET SysParmValue = '"+  parameterValue +"'");
		updateQuery.append(" Where SysParmCode ='"+  parameterCode +"'");
		return updateQuery;
		
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

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}

	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}
	
}
