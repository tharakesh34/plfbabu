package com.pennant.pff.letter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.pennapps.core.resource.Literal;

public class LetterGenerationQueueTasklet implements Tasklet {
	private BatchJobQueueDAO ebjqDAO;

	public LetterGenerationQueueTasklet(BatchJobQueueDAO ebjqDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Logger logger = LogManager.getLogger(LetterGenerationQueueTasklet.class);

		logger.debug(Literal.ENTERING);

		BatchJobQueue jobQueue = new BatchJobQueue();
		/*
		 * long totalRecords = ebjqDAO.prepareQueue(jobQueue);
		 * 
		 * StepUtil.LETTER_GENERATION.setTotalRecords(totalRecords);
		 * 
		 * logger.info("Queueing preparation for Letter Generation completed with total loans {}", totalRecords);
		 */
		LetterGenerationTasklet.processedCount.set(0);
		LetterGenerationTasklet.failedCount.set(0);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

}
