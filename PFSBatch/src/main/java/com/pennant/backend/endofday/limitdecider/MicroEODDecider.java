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
 * FileName    		:  EndOfMonthDecider.java													*                           
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

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.pennant.app.util.DateUtility;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class MicroEODDecider implements JobExecutionDecider {

	private CustomerQueuingDAO	customerQueuingDAO;

	public MicroEODDecider() {
	}

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		long failedCount = customerQueuingDAO.getCountByStatus(DateUtility.getValueDate(), EodConstants.STATUS_FAILED);

		if (failedCount > 0) {
			jobExecution.getExecutionContext().put(EodConstants.MICRO_EOD, EodConstants.STATUS_FAILED);
			return FlowExecutionStatus.UNKNOWN;
		} else {
			return FlowExecutionStatus.COMPLETED;
		}

	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

}
