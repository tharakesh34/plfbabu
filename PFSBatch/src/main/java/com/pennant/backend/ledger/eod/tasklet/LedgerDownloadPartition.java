package com.pennant.backend.ledger.eod.tasklet;

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
import com.pennanttech.pff.external.LedgerDownloadService;
import com.pennanttech.pff.ledger.dao.LedgerDownloadDAO;

public class LedgerDownloadPartition implements Partitioner {
	private Logger logger = LogManager.getLogger(LedgerDownloadPartition.class);

	private LedgerDownloadService ledgerDownloadService;
	private LedgerDownloadDAO ledgerDownloadDAO;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> partitionData = new HashMap<>();

		if (ledgerDownloadService == null || ledgerDownloadDAO == null || !ledgerDownloadService.isMultiThread()) {
			logger.info("Implementations are not defined / Mutli-thread is not available");
			return partitionData;
		}

		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.EOD_THREAD_COUNT); // Doubt

		long queueCount = ledgerDownloadDAO.getQueueCount();

		if (queueCount == 0) {
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
				noOfRows = queueCount;
			}

			to = to + noOfRows;
			loanCount = ledgerDownloadDAO.updateThreadID(from, to, i);
			from = to;

			totalLoans = totalLoans + loanCount;

			ExecutionContext execution = addExecution(i, loanCount);
			partitionData.put(Integer.toString(i), execution);

			if (recordsLessThanThread && noOfRows == loanCount) {
				break;
			}
		}

		logger.info("Thread allocation completed, Total threads {}, Total loans {}", threadCount, totalLoans);
		return partitionData;
	}

	private ExecutionContext addExecution(int threadID, int loansPerThread) {
		ExecutionContext execution = new ExecutionContext();

		DataEngineStatus status = new DataEngineStatus("ledgerDownLoad:" + threadID);

		status.setTotalRecords(loansPerThread);
		execution.put(status.getName(), status);
		execution.put(EodConstants.THREAD, String.valueOf(threadID));

		return execution;
	}

	@Autowired(required = false)
	public void setLedgerDownloadService(LedgerDownloadService ledgerDownloadService) {
		this.ledgerDownloadService = ledgerDownloadService;
	}

	@Autowired(required = false)
	public void setLedgerDownloadDAO(LedgerDownloadDAO ledgerDownloadDAO) {
		this.ledgerDownloadDAO = ledgerDownloadDAO;
	}
}
