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
 * FileName    		:  NextBussinessDateUpdation.java										*                           
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

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class ThreadAllocation implements Tasklet {

	private Logger				logger	= Logger.getLogger(ThreadAllocation.class);

	private CustomerQueuingDAO	customerQueuingDAO;

	public ThreadAllocation() {

	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getValueDate();
		logger.debug("START: Thread Allocation On : " + valueDate);

		boolean recordslessThanThread = false;
		int threadCount = SysParamUtil.getValueAsInt("EOD_THREAD_COUNT");
		long custIdCount = customerQueuingDAO.getCountByProgress(valueDate, null);
		if (custIdCount != 0) {

			long noOfRows = custIdCount / threadCount;
			if (custIdCount < threadCount) {
				recordslessThanThread = true;
				noOfRows = 1;
			}

			for (int i = 1; i <= threadCount; i++) {

				if (i == threadCount) {
					customerQueuingDAO.updateThreadID(valueDate, "Thread" + i);
				} else {
					customerQueuingDAO.updateThreadIDByRowNumber(valueDate, noOfRows, "Thread" + i);
				}

				if (recordslessThanThread && i == custIdCount) {
					break;
				}
			}
		}

		ExecutionContext executionContext = context.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext();
		executionContext.put(EodConstants.MICRO_EOD, EodConstants.STATUS_STARTED);

		logger.debug("COMPLETE: Thread Allocation On :" + valueDate);
		return RepeatStatus.FINISHED;
	}

	public CustomerQueuingDAO getCustomerQueuingDAO() {
		return customerQueuingDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

}
