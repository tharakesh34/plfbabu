package com.pennant.pff.letter.partitioner;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.partitioner.ResponsePartitioner;

public class LetterGeneratePartitioner implements Partitioner {
	private Logger logger = LogManager.getLogger(ResponsePartitioner.class);

	private BatchJobQueueDAO bjqDAO;

	public LetterGeneratePartitioner(BatchJobQueueDAO bjqDAO) {
		super();
		this.bjqDAO = bjqDAO;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.LETTER_GENERATION_THREAD_COUNT);

		Map<String, ExecutionContext> partitionData = new HashMap<>();

		BatchJobQueue jobQueue = new BatchJobQueue();

		bjqDAO.resetSequence();

		bjqDAO.handleFailures(jobQueue);

		int totalRecords = bjqDAO.getQueueCount(jobQueue);

		if (totalRecords == 0) {
			return partitionData;
		}

		for (int i = 1; i <= threadCount; i++) {
			ExecutionContext execution = addExecution(i);
			partitionData.put(Integer.toString(i), execution);

			if (threadCount >= totalRecords) {
				break;
			}
		}

		logger.info("Thread allocation completed, Total threads {}, Total records {}", threadCount, totalRecords);

		return partitionData;
	}

	private ExecutionContext addExecution(int threadID) {
		ExecutionContext execution = new ExecutionContext();

		execution.put("THREAD_ID", String.valueOf(threadID));

		return execution;
	}
}
