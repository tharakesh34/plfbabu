package com.pennant.pff.letter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.presentment.tasklet.ResponseClearTasklet;
import com.pennanttech.pennapps.core.resource.Literal;

public class GLResponseClearTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(ResponseClearTasklet.class);

	private AutoLetterGenerationDAO autoLetterGenerationDAO;
	private BatchJobQueueDAO ebjqDAO;

	public GLResponseClearTasklet(AutoLetterGenerationDAO autoLetterGenerationDAO, BatchJobQueueDAO ebjqDAO) {
		super();
		this.autoLetterGenerationDAO = autoLetterGenerationDAO;
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BATCH_ID");

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchId);

		ebjqDAO.clearQueue();

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

}
