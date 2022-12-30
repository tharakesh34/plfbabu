package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.holdrelease.FinanceHoldReleaseProcess;

public class FinanceHoldReleaseTasklet implements Tasklet {

	private Logger logger = LogManager.getLogger(FinanceHoldReleaseTasklet.class);

	private FinanceHoldReleaseProcess financeHoldReleaseProcess;

	public FinanceHoldReleaseTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		try {
			logger.info("START Auto Removal Hold Process for the value date {}", valueDate);

			BatchUtil.setExecutionStatus(context, StepUtil.FIN_HOLD_RELEASE);
			financeHoldReleaseProcess.releaseHoldProcess(valueDate);

			logger.info("COMPLETED: Auto Removal Hold  Process for the value date {}", valueDate);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {

		}

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setFinanceHoldReleaseProcess(FinanceHoldReleaseProcess financeHoldReleaseProcess) {
		this.financeHoldReleaseProcess = financeHoldReleaseProcess;
	}

}
