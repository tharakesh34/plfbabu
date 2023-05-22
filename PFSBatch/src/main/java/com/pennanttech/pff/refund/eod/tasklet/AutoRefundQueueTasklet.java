package com.pennanttech.pff.refund.eod.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.batch.job.dao.BatchJobQueueDAO;
import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.step.StepUtil;

public class AutoRefundQueueTasklet implements Tasklet {
	private Logger logger = LogManager.getLogger(AutoRefundQueueTasklet.class);

	private BatchJobQueueDAO ebjqDAO;

	public AutoRefundQueueTasklet(BatchJobQueueDAO ebjqDAO) {
		super();
		this.ebjqDAO = ebjqDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		logger.debug(Literal.ENTERING);

		BatchJobQueue jobQueue = new BatchJobQueue();

		long totalRecords = ebjqDAO.prepareQueue(jobQueue);

		StepUtil.AUTO_REFUND_PROCESS.setTotalRecords(totalRecords);

		logger.info("Queueing preparation for Auto Refund completed with total loans {}", totalRecords);

		AutoRefundTasklet.processedCount.set(0);
		AutoRefundTasklet.failedCount.set(0);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}
}
