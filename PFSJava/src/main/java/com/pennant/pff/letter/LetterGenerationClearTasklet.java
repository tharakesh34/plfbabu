package com.pennant.pff.letter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.presentment.tasklet.ResponseClearTasklet;
import com.pennanttech.pennapps.core.resource.Literal;

public class LetterGenerationClearTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(ResponseClearTasklet.class);

	private BatchJobQueueDAO ebjqDAO;

	public LetterGenerationClearTasklet(BatchJobQueueDAO ebjqDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		ebjqDAO.clearQueue();

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

}
