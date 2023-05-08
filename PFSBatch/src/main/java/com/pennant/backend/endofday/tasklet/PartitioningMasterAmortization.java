/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : PartitioningMasterAmortization.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 18-05-2018 *
 * 
 * Modified Date : 18-05-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-05-2018 Pennant 0.1 * * * * * * * * *
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
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.util.DateUtil;

public class PartitioningMasterAmortization implements Partitioner {
	private Logger logger = LogManager.getLogger(Partitioner.class);

	private ProjectedAmortizationDAO projectedAmortizationDAO;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Date valueDate = SysParamUtil.getAppValueDate();
		logger.info("START: Amortization Thread Allocation On {}", valueDate);

		Date prvAMZMonth = SysParamUtil.getValueAsDate(AmortizationConstants.AMZ_MONTHEND);
		Date amzMonth = DateUtil.addDays(prvAMZMonth, 1);
		amzMonth = DateUtil.getMonthEnd(amzMonth);

		boolean recordsLessThanThread = false;
		Map<String, ExecutionContext> partitionData = new HashMap<String, ExecutionContext>();

		// configured thread count
		int threadCount = SysParamUtil.getValueAsInt("AMZ_THREAD_COUNT");

		// finance and total counts by progress
		long finsCount = projectedAmortizationDAO.getCountByProgress();

		if (finsCount == 0) {
			logger.info("COMPLETE: Amortization Thread Allocation On {}", valueDate);
			return partitionData;
		}

		long noOfRows = Math.round(Double.valueOf(finsCount) / Double.valueOf(threadCount));

		if (finsCount < threadCount) {
			recordsLessThanThread = true;
			noOfRows = 1;
		}

		for (int i = 1; i <= threadCount; i++) {
			int financeCount = 0;
			if (i == threadCount) {
				financeCount = projectedAmortizationDAO.updateThreadIDByRowNumber(amzMonth, 0, i);
			} else {
				financeCount = projectedAmortizationDAO.updateThreadIDByRowNumber(amzMonth, noOfRows, i);
			}

			ExecutionContext execution = addExecution(i, financeCount, finsCount); // 0 -- totalCount
			partitionData.put(Integer.toString(i), execution);

			if (i == 1) {
				execution.put(AmortizationConstants.DATA_TOTALFINANCES, finsCount);
			}

			if (recordsLessThanThread && i == finsCount) {
				break;
			}
		}

		logger.info("COMPLETE: Amortization Thread Allocation On {}", valueDate);
		return partitionData;
	}

	/**
	 * 
	 * @param threadID
	 * @param financeCount
	 * @param totalCount
	 * @return
	 */
	private ExecutionContext addExecution(int threadID, int financeCount, long totalCount) {
		ExecutionContext execution = new ExecutionContext();

		DataEngineStatus status = new DataEngineStatus("amzProcess:" + String.valueOf(threadID));
		status.getKeyAttributes().put(AmortizationConstants.DATA_TOTALFINANCES, totalCount);

		status.setTotalRecords(financeCount);
		execution.put(status.getName(), status);
		execution.put(EodConstants.THREAD, String.valueOf(threadID));

		return execution;
	}

	@Autowired
	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}
}
