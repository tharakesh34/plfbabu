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
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtractionQueueTasklet implements Tasklet {
	private Logger logger = LogManager.getLogger(ExtractionQueueTasklet.class);

	private static final String LITERAL_1 = "Queueing preparation for presentment extraction completed with total records  {}";

	private BatchJobQueueDAO bjqDAO;

	public ExtractionQueueTasklet(BatchJobQueueDAO bjqDAO) {
		super();
		this.bjqDAO = bjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(jobParameters.getLong("BATCH_ID"));
		jobQueue.setJobName("EXTRACTION");

		bjqDAO.clearQueue();

		int totalRecords = bjqDAO.prepareQueue(jobQueue);

		ExtractionTasklet.processRecords.set(0);
		ExtractionTasklet.successRecords.set(0);
		ExtractionTasklet.failedRecords.set(0);

		logger.info(LITERAL_1, totalRecords);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}
}
