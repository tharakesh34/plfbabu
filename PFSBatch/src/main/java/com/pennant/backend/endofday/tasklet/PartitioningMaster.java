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
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class PartitioningMaster implements Partitioner {
	private Logger logger = LogManager.getLogger(PartitioningMaster.class);

	private CustomerQueuingDAO customerQueuingDAO;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Date valueDate = SysParamUtil.getAppValueDate();
		logger.info("START: Thread Allocation On {}", valueDate);

		Map<String, ExecutionContext> partitionData = new HashMap<>();

		boolean recordsLessThanThread = false;
		/* Configured thread count */
		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.EOD_THREAD_COUNT);

		/* Count by progress */
		long loanCount = customerQueuingDAO.getLoanCountByProgress();

		if (loanCount != 0) {
			long noOfRows = loanCount / threadCount;

			if (loanCount < threadCount) {
				recordsLessThanThread = true;
				noOfRows = 1;
			}

			long from = 0;
			long to = 0;
			for (int i = 1; i <= threadCount; i++) {

				int customerCount = 0;
				if (i == threadCount) {
					/* Last thread will have the remaining records */
					noOfRows = loanCount;
				}

				to = to + noOfRows;
				customerCount = customerQueuingDAO.updateThreadIDByLoanCount(valueDate, from, to, i);
				from = to;

				ExecutionContext execution = addExecution(i, loanCount, customerCount);
				partitionData.put(Integer.toString(i), execution);

				if (recordsLessThanThread && i == customerCount) {
					break;
				}
			}

		}

		logger.info("COMPLETE: Thread Allocation On {}", valueDate);
		return partitionData;
	}

	private ExecutionContext addExecution(int threadID, long totalLoans, long customersPerThread) {
		ExecutionContext execution = new ExecutionContext();

		DataEngineStatus status = new DataEngineStatus("microEOD:" + threadID);
		status.getKeyAttributes().put(EodConstants.DATA_TOTALCUSTOMER, totalLoans);

		status.setTotalRecords(customersPerThread);
		execution.put(status.getName(), status);
		execution.put(EodConstants.THREAD, String.valueOf(threadID));

		return execution;
	}

	@Autowired
	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

}
