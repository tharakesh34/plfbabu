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
 * FileName    		:  LimitDecider.java													*                           
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
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.HolidayUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.equation.util.HostConnection;

public class LimitDecider implements JobExecutionDecider {
	
	private Logger logger = Logger.getLogger(LimitDecider.class);

	private DataSource dataSource;
	private HostConnection hostConnection;

	private Date dateValueDate = null;
	private Date dateNextBusinessDate = null;

	@SuppressWarnings("serial")
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());
		dateNextBusinessDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_NEXT).toString());

		logger.debug("START: Limit Decider for generation of Loop for Value Date: "+ DateUtility.addDays(dateValueDate,-1));
		stepExecution.getExecutionContext().put(stepExecution.getId().toString(), dateValueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;		

		try {

			//Update Today Account Balances to ZeroValue
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(prepareAcUpdateQuery());
			sqlStatement.executeUpdate();
			
			// If NBD is holiday then loop continues, else end process.
			if (dateValueDate.compareTo(dateNextBusinessDate) == 0) {			
				
				String nextBussDate = DateUtility.formatUtilDate(HolidayUtil.getWorkingBussinessDate("GEN", "N", dateNextBusinessDate).getTime(),"yyyy-MM-dd");				
				sqlStatement = connection.prepareStatement(prepareUpdateQuery());
				sqlStatement.setString(1, nextBussDate);
				sqlStatement.setString(2, PennantConstants.APP_DATE_NEXT);
				sqlStatement.executeUpdate();
				
				String prevBussDate = DateUtility.formatUtilDate(HolidayUtil.getWorkingBussinessDate("GEN", "P", dateNextBusinessDate).getTime(),"yyyy-MM-dd");				
				sqlStatement = connection.prepareStatement(prepareUpdateQuery());
				sqlStatement.setString(1, prevBussDate);
				sqlStatement.setString(2, PennantConstants.APP_DATE_LAST);
				sqlStatement.executeUpdate();
							
				sqlStatement = connection.prepareStatement(prepareUpdateQuery());
				sqlStatement.setString(1, dateNextBusinessDate.toString());
				sqlStatement.setString(2, PennantConstants.APP_DATE_CUR);
				sqlStatement.executeUpdate();
				
				sqlStatement = connection.prepareStatement(prepareUpdateQuery());
				sqlStatement.setString(1, PennantConstants.APP_PHASE_DAY);
				sqlStatement.setString(2, PennantConstants.APP_PHASE);
				sqlStatement.executeUpdate();
				
				//Reset Next Business Date in System Parameters
				SystemParameterDetails.setParmDetails(PennantConstants.APP_DATE_NEXT, nextBussDate);
				SystemParameterDetails.setParmDetails(PennantConstants.APP_DATE_LAST, prevBussDate);
				SystemParameterDetails.setParmDetails(PennantConstants.APP_DATE_CUR, dateNextBusinessDate.toString());
				SystemParameterDetails.setParmDetails(PennantConstants.APP_PHASE, PennantConstants.APP_PHASE_DAY);
				
				//Close Host Connection
				getHostConnection().closeAllConnection();
				
				logger.debug("COMPLETE: Limit Decider with Value Date: "+ DateUtility.addDays(dateValueDate,-1));
				return FlowExecutionStatus.COMPLETED;
				
			}
			
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
				DataSourceUtils.doReleaseConnection(connection, getDataSource());
			} catch (SQLException e) {
				logger.error(e);
			}
		}

		logger.debug("COMPLETE: Limit Decider for generation of Loop with Value Date: "+ DateUtility.addDays(dateValueDate,-1));
		return FlowExecutionStatus.UNKNOWN;
	} 

	/**
	 * Method for preparation of Update Query To update APP_VALUEDATE
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String prepareAcUpdateQuery() {
		
		StringBuilder query =  new StringBuilder(" UPDATE Accounts SET  AcPrvDayBal = (AcPrvDayBal+AcTodayBal) , " );
		query.append(" AcTodayDr = 0, AcTodayCr =0, AcTodayNet =0,AcTodayBal= 0 ");
		if (dateValueDate.compareTo(dateNextBusinessDate) < 0) {		
			query.append(" ,AcAccrualBal = 0");
		}
		return query.toString();
		
	}
	
	/**
	 * Method for preparation of Update Query To update APP_VALUEDATE
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String prepareUpdateQuery() {

		StringBuilder query = new StringBuilder();
		query.append(" UPDATE SMTparameters SET SysParmValue = ?");
		query.append(" Where SysParmCode =?");
		return query.toString();

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

	public HostConnection getHostConnection() {
		return hostConnection;
	}
	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}
	
}
