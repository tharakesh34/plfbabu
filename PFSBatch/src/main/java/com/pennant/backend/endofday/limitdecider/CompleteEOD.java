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

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.pennant.app.core.DateService;
import com.pennant.app.util.DateUtility;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class CompleteEOD implements JobExecutionDecider {

	private Logger				logger	= Logger.getLogger(CompleteEOD.class);

	@SuppressWarnings("unused")
	private DataSource			dataSource;
	private DateService			dateService;

	private CustomerQueuingDAO	customerQueuingDAO;

	public CompleteEOD() {

	}

	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		Date valueDate = DateUtility.getAppValueDate();

		logger.debug("START: Complete EOD On : " + valueDate);

		stepExecution.getExecutionContext().put(stepExecution.getId().toString(), valueDate);
		// Log the Customer queuing data and threads status
		customerQueuingDAO.logCustomerQueuing(EodConstants.PROGRESS_SUCCESS);
		//check extended month end and update the dates.
		dateService.doUpdateAftereod(true);
		logger.debug("COMPLETE: Complete EOD On :" + valueDate);
		return FlowExecutionStatus.COMPLETED;
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


}
