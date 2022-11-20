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

public class SuccessResponseQueueTasklet implements Tasklet {
	private Logger logger = LogManager.getLogger(SuccessResponseQueueTasklet.class);

	private static final String LITERAL_1 = "Queueing preparation for presentment response for success records completed with total records  {}";

	private BatchJobQueueDAO bjqDAO;

	public SuccessResponseQueueTasklet(BatchJobQueueDAO bjqDAO) {
		super();
		this.bjqDAO = bjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(jobParameters.getLong("BATCH_ID"));

		bjqDAO.deleteQueue(jobQueue);

		int totalRecords = bjqDAO.prepareQueue(jobQueue);

		logger.info(LITERAL_1, totalRecords);

		return RepeatStatus.FINISHED;
	}
}
