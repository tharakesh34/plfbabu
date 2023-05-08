package com.pennanttech.pff.knockoff.eod.tasklet;

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
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.knockoff.service.ExcessKnockOffService;

public class ExcessKnockOffPartition implements Partitioner {
	private Logger logger = LogManager.getLogger(ExcessKnockOffPartition.class);

	private ExcessKnockOffService excessKnockOffService;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		logger.debug(Literal.ENTERING);
		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.EOD_THREAD_COUNT);

		Map<String, ExecutionContext> partitionData = new HashMap<>();

		excessKnockOffService.handleFailures();

		long queueCount = excessKnockOffService.getQueueCount();

		if (queueCount == 0) {
			logger.debug(Literal.LEAVING);
			return partitionData;
		}

		boolean recordsLessThanThread = false;
		long totalLoans = 0;

		long noOfRows = Math.round(Double.valueOf(queueCount) / Double.valueOf(threadCount));

		if (queueCount < threadCount) {
			recordsLessThanThread = true;
			noOfRows = queueCount;
		}

		long from = 0;
		long to = 0;
		for (int i = 1; i <= threadCount; i++) {
			int loanCount = 0;
			if (i == threadCount) {
				/* Last thread will have the remaining records */
				noOfRows = queueCount;
			}

			to = to + noOfRows;
			loanCount = excessKnockOffService.updateThreadID(from, to, i);
			from = to;

			totalLoans = totalLoans + loanCount;

			ExecutionContext execution = addExecution(i, queueCount, loanCount);
			partitionData.put(Integer.toString(i), execution);

			if (recordsLessThanThread && noOfRows == loanCount) {
				logger.debug(Literal.LEAVING);
				break;

			}
		}

		logger.info("Thread allocation completed, Total threads {}, Total loans {}", threadCount, totalLoans);
		logger.debug(Literal.LEAVING);
		return partitionData;
	}

	private ExecutionContext addExecution(int threadID, long totalLoans, long loansPerThread) {
		logger.debug(Literal.ENTERING);
		ExecutionContext execution = new ExecutionContext();

		DataEngineStatus status = new DataEngineStatus("Cross_Loan_Knockoff :" + threadID);

		status.setTotalRecords(loansPerThread);
		execution.put(status.getName(), status);
		execution.put(EodConstants.THREAD, String.valueOf(threadID));

		logger.debug(Literal.LEAVING);
		return execution;
	}

	@Autowired
	public void setExcessKnockOffService(ExcessKnockOffService excessKnockOffService) {
		this.excessKnockOffService = excessKnockOffService;
	}

}
