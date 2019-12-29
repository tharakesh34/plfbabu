package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.external.eod.EODNotificationService;

public class LedgerNotification implements Tasklet {
	private Logger logger = LogManager.getLogger(LedgerNotification.class);

	private EODNotificationService eodNotificationService;

	public LedgerNotification() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (eodNotificationService != null) {
			BatchUtil.setExecutionStatus(chunkContext, StepUtil.LEDGER_NOTIFICATION);

			try {
				eodNotificationService.sendLedgerNotifycation();
			} catch (Exception e) {
				throw e;
			}
		} else {
			logger.info("EOD notifications service not configured");
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	@Autowired(required = false)
	public void setEodNotificationService(EODNotificationService eodNotificationService) {
		this.eodNotificationService = eodNotificationService;
	}

}
