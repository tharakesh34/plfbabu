package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.autorefund.AutoRefundProcess;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class AutoRefundProcessTasklet implements Tasklet {
	private static final Logger logger = LogManager.getLogger(AutoRefundProcessTasklet.class);

	private AutoRefundProcess autoRefundProcess;

	public AutoRefundProcessTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		try {
			logger.info("START Auto Refund Process for the value date {}", valueDate);

			BatchUtil.setExecutionStatus(context, StepUtil.AUTO_REFUND_PROCESS);
			autoRefundProcess.startRefundProcess(valueDate);
			logger.info("COMPLETED: Auto Refund Process for the value date {}", valueDate);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		return RepeatStatus.FINISHED;
	}

	public void setAutoRefundProcess(AutoRefundProcess autoRefundProcess) {
		this.autoRefundProcess = autoRefundProcess;
	}

}
