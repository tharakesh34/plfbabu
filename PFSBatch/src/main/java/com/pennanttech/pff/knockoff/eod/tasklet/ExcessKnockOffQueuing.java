package com.pennanttech.pff.knockoff.eod.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.step.StepUtil;

public class ExcessKnockOffQueuing implements Tasklet {
	private Logger logger = LogManager.getLogger(ExcessKnockOffQueuing.class);

	private BatchJobQueueDAO ebjqDAO;

	public ExcessKnockOffQueuing(BatchJobQueueDAO ebjqDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		logger.debug(Literal.ENTERING);

		if (!ImplementationConstants.ALW_AUTO_CROSS_LOAN_KNOCKOFF) {
			logger.debug(Literal.LEAVING);
			return RepeatStatus.FINISHED;
		}

		BatchJobQueue jobQueue = new BatchJobQueue();

		ebjqDAO.clearQueue();

		long totalRecords = ebjqDAO.prepareQueue(jobQueue);

		StepUtil.CROSS_LOAN_KNOCKOFF.setTotalRecords(totalRecords);

		logger.info("Queueing preparation for CrossLoan Knock Off completed with total loans {}", totalRecords);

		ExcessKnockOffTasklet.processedCount.set(0);

		ExcessKnockOffTasklet.failedCount.set(0);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}
}
