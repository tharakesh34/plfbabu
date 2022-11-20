package com.pennant.pff.presentment.tasklet;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;

public class ExtractionPartitioner implements Partitioner, StepExecutionListener {
	private Logger logger = LogManager.getLogger(ExtractionPartitioner.class);

	private BatchJobQueueDAO bjqDAO;
	private Long batchId;

	public ExtractionPartitioner(BatchJobQueueDAO bjqDAO) {
		super();
		this.bjqDAO = bjqDAO;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.PRESENTMENT_EXTRACTION_THREAD_COUNT);

		Map<String, ExecutionContext> partitionData = new HashMap<>();

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchId);

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

	@Override
	public void beforeStep(StepExecution stepExecution) {
		JobParameters jobParameters = stepExecution.getJobParameters();

		batchId = jobParameters.getLong("BATCH_ID");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return stepExecution.getExitStatus();
	}

}
