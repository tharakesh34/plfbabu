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
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtractionClearTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(ExtractionClearTasklet.class);

	private PresentmentDAO presentmentDAO;
	private BatchJobQueueDAO ebjqDAO;

	public ExtractionClearTasklet(PresentmentDAO presentmentDAO, BatchJobQueueDAO ebjqDAO) {
		super();
		this.presentmentDAO = presentmentDAO;
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BATCH_ID");

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchId);

		this.presentmentDAO.clearQueue(batchId);
		this.ebjqDAO.clearQueue();

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

}
