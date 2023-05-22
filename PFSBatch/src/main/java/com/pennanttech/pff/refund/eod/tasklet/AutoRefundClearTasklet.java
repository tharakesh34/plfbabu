package com.pennanttech.pff.refund.eod.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoRefundClearTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(AutoRefundClearTasklet.class);

	private BatchJobQueueDAO ebjqDAO;

	public AutoRefundClearTasklet(BatchJobQueueDAO ebjqDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		this.ebjqDAO.clearQueue();

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

}
