package com.pennant.backend.endofday.tasklet;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.eod.EODNotificationService;

public class CollectionNotification implements Tasklet {
	private Logger logger = Logger.getLogger(CollectionNotification.class);

	@Autowired(required = false)
	private EODNotificationService eodNotificationService;

	public CollectionNotification() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (eodNotificationService != null) {
			eodNotificationService.sendCollectionNotifycation();
		} else {
			logger.debug("EODNotificationService Not Configured");
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}
}
