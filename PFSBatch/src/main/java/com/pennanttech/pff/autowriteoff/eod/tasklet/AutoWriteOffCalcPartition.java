package com.pennanttech.pff.autowriteoff.eod.tasklet;

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
import com.pennanttech.pff.autowriteoff.service.AutoWriteOffService;

public class AutoWriteOffCalcPartition implements Partitioner {
	private Logger logger = LogManager.getLogger(AutoWriteOffCalcPartition.class);

	private AutoWriteOffService autoWriteOffService;

	public AutoWriteOffCalcPartition() {
		super();
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.EOD_THREAD_COUNT);

		logger.info("Thread allocation for Auto Write off calculation started...");

		Map<String, ExecutionContext> partitionData = new HashMap<>();

		long queueCount = autoWriteOffService.getQueueCount();

		if (queueCount == 0) {
			return partitionData;
		}

		boolean recordsLessThanThread = false;
		long totalLoans = 0;

		long noOfRows = Math.round((new Double(queueCount) / new Double(threadCount)));

		if (queueCount < threadCount) {
			recordsLessThanThread = true;
			noOfRows = 1;
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
			loanCount = autoWriteOffService.updateThreadID(from, to, i);
			from = to;

			totalLoans = totalLoans + loanCount;

			ExecutionContext execution = addExecution(i, queueCount, loanCount);
			partitionData.put(Integer.toString(i), execution);

			if (recordsLessThanThread && i == loanCount) {
				break;
			}
		}

		logger.info("Thread allocation completed, Total threads {}, Total loans {}", threadCount, totalLoans);
		return partitionData;
	}

	private ExecutionContext addExecution(int threadID, long totaLoans, long loansPerThread) {
		ExecutionContext execution = new ExecutionContext();

		DataEngineStatus status = new DataEngineStatus("AutoWriteOff:" + threadID);

		status.setTotalRecords(loansPerThread);
		execution.put(status.getName(), status);
		execution.put(EodConstants.THREAD, String.valueOf(threadID));

		return execution;
	}

	@Autowired
	public void setAutoWriteOffService(AutoWriteOffService autoWriteOffService) {
		this.autoWriteOffService = autoWriteOffService;
	}

}
