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
import com.pennant.pff.presentment.dao.PresentmentDAO;

public class ApprovalPartitioner implements Partitioner, StepExecutionListener {
	private Logger logger = LogManager.getLogger(ApprovalPartitioner.class);

	private BatchJobQueueDAO bjqDAO;
	private PresentmentDAO presentmentDAO;

	private Long batchId;

	public ApprovalPartitioner(BatchJobQueueDAO bjqDAO, PresentmentDAO presentmentDAO) {
		super();
		this.bjqDAO = bjqDAO;
		this.presentmentDAO = presentmentDAO;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		int threadCount = SysParamUtil.getValueAsInt(SMTParameterConstants.PRESENTMENT_EXTRACTION_THREAD_COUNT);

		Map<String, ExecutionContext> partitionData = new HashMap<>();

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchId);

		int totalRecords = bjqDAO.getQueueCount(jobQueue);

		bjqDAO.handleFailures(jobQueue);

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
		ExitStatus exitStatus = stepExecution.getExitStatus();

		String exitCode = exitStatus.getExitCode();
		String exitDescription = exitStatus.getExitDescription();

		JobParameters jobParameters = stepExecution.getJobParameters();
		long batchId = jobParameters.getLong("BATCH_ID");

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchId);

		if ("FAILED".equals(exitCode)) {
			jobQueue.setFailedStep(stepExecution.getStepName());
			jobQueue.setError(exitDescription);

			presentmentDAO.updateFailureError(jobQueue);
		} else {
			jobQueue = presentmentDAO.getBatch(jobQueue);

			int total = jobQueue.getTotalRecords();
			int processed = jobQueue.getProcessRecords();
			int success = jobQueue.getSuccessRecords();
			int failed = jobQueue.getFailedRecords();
			String msg = jobQueue.getRemarks();

			msg = msg + "\n" + String.format(
					"Presentment approval completed successfully with, total Records: %d, processed: %d, success: %d, failed: %d",
					total, success, processed, failed);

			jobQueue.setBatchId(batchId);
			jobQueue.setRemarks(msg);

			presentmentDAO.updateRemarks(jobQueue);
		}

		return exitStatus;
	}

}
