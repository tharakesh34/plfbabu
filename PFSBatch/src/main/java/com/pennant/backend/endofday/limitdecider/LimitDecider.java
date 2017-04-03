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

import com.pennant.app.core.DateService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.SysParamUtil.Param;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.BatchFileUtil;
import com.pennant.eod.PaymentRecoveryService;
import com.pennant.equation.util.HostConnection;

public class LimitDecider implements JobExecutionDecider {

	private Logger					logger	= Logger.getLogger(LimitDecider.class);

	private DateService				dateService;
	private PaymentRecoveryService	paymentRecoveryService;
	private DataSource				dataSource;
	private HostConnection			hostConnection;

	public LimitDecider() {

	}

	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		Date valueDate = DateUtility.getValueDate();
		Date nextBusinessDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);

		logger.debug("START: Limit Decider for generation of Loop for Value Date: " + DateUtility.addDays(valueDate, -1));
		stepExecution.getExecutionContext().put(stepExecution.getId().toString(), valueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;

		try {

			StringBuilder query = new StringBuilder(" UPDATE Accounts SET  AcPrvDayBal = (AcPrvDayBal+AcTodayBal) , ");
			query.append(" AcTodayDr = 0, AcTodayCr =0, AcTodayNet =0,AcTodayBal= 0 ");
			if (valueDate.compareTo(nextBusinessDate) < 0) {
				query.append(" ,AcAccrualBal = 0");
			}

			//Update Today Account Balances to ZeroValue
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(query.toString());
			sqlStatement.executeUpdate();
			sqlStatement.close();

			getPaymentRecoveryService().moveDataToLog(BatchFileUtil.getBatchReference());

			boolean processed = getDateService().doUpdateAftereod(true,false);

			if (processed) {
				SysParamUtil.updateParamDetails(Param.AUTOHUNTING.getCode(), PennantConstants.AUTOHUNT_RUNNING);
				logger.debug("COMPLETE: Limit Decider with Value Date: " + DateUtility.addDays(valueDate, -1));
			}
			return FlowExecutionStatus.COMPLETED;

		} catch (SQLException e) {
			logger.error("Exception: ", e);
		} finally {
			try {
				if (sqlStatement!=null) {
					sqlStatement.close();
				}
				DataSourceUtils.doReleaseConnection(connection, getDataSource());
			} catch (SQLException e) {
				logger.error("Exception: ", e);
			}
		}

		logger.debug("COMPLETE: Limit Decider for generation of Loop with Value Date: " + DateUtility.addDays(valueDate, -1));
		return FlowExecutionStatus.UNKNOWN;
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

	public HostConnection getHostConnection() {
		return hostConnection;
	}

	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}

	public PaymentRecoveryService getPaymentRecoveryService() {
		return paymentRecoveryService;
	}

	public void setPaymentRecoveryService(PaymentRecoveryService paymentRecoveryService) {
		this.paymentRecoveryService = paymentRecoveryService;
	}

	public DateService getDateService() {
		return dateService;
	}

	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

}
