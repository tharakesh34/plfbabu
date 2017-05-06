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
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennant.eod.util.EODProperties;

public class CompleteEOD implements JobExecutionDecider {

	private Logger				logger	= Logger.getLogger(CompleteEOD.class);

	private DataSource			dataSource;
	private DateService			dateService;

	private CustomerQueuingDAO	customerQueuingDAO;
	private EODProperties		eodProperties;

	public CompleteEOD() {

	}

	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		Date valueDate = DateUtility.getAppValueDate();
		Date nextBusinessDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);

		logger.debug("START: Complete EOD On : " + valueDate);

		stepExecution.getExecutionContext().put(stepExecution.getId().toString(), valueDate);

		updateAccounts(valueDate, nextBusinessDate);
		// Log the Customer queuing data and threads status
		customerQueuingDAO.logCustomerQueuing();
		//Update value dates check Holiday 
		dateService.doUpdateValueDate();
		boolean processed = dateService.doUpdateAftereod(true);
		if (processed) {
			//clear the data which is loaded in before  end of day
			eodProperties.destroy();
			logger.debug("COMPLETE: Complete EOD On :" + valueDate);
			return FlowExecutionStatus.COMPLETED;
		} else {
			return FlowExecutionStatus.UNKNOWN;
		}
	}

	private void updateAccounts(Date valueDate, Date nextBusinessDate) {
		Connection connection = null;
		PreparedStatement sqlStatement = null;

		try {

			StringBuilder query = new StringBuilder(" UPDATE Accounts SET  AcPrvDayBal = (AcPrvDayBal+AcTodayBal) , ");
			query.append(" AcTodayDr = 0, AcTodayCr =0, AcTodayNet =0,AcTodayBal= 0 ");
			if (valueDate.compareTo(nextBusinessDate) < 0) {
				query.append(" ,AcAccrualBal = 0");
			}

			//Update Today Account Balances to ZeroValue
			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(query.toString());
			sqlStatement.executeUpdate();
			sqlStatement.close();

		} catch (SQLException e) {
			logger.error("Exception: ", e);
		}
	}

	public void setDateService(DateService dateService) {
		this.dateService = dateService;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public void setEodProperties(EODProperties eodProperties) {
		this.eodProperties = eodProperties;
	}


}
