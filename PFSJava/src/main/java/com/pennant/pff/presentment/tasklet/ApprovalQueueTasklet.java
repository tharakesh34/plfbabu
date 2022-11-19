package com.pennant.pff.presentment.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;

public class ApprovalQueueTasklet implements Tasklet {
	private Logger logger = LogManager.getLogger(ApprovalQueueTasklet.class);

	private BatchJobQueueDAO approvalBatchJobQueueDAO;

	public ApprovalQueueTasklet(BatchJobQueueDAO approvalBatchJobQueueDAO) {
		super();
		this.approvalBatchJobQueueDAO = approvalBatchJobQueueDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BATCH_ID");

		BatchJobQueue jobQueue = new BatchJobQueue();

		jobQueue.setBatchId(batchId);

		approvalBatchJobQueueDAO.deleteQueue(jobQueue);

		int totalRecords = approvalBatchJobQueueDAO.prepareQueue(jobQueue);

		logger.info("Queueing preparation for presentment Approval job completed with total records  {}", totalRecords);

		return RepeatStatus.FINISHED;
	}
}
