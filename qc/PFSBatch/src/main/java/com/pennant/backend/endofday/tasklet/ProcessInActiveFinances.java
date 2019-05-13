package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.core.FinMaturityService;
import com.pennant.app.util.DateUtility;

public class ProcessInActiveFinances implements Tasklet {
	private Logger logger = Logger.getLogger(ProcessInActiveFinances.class);

	private FinMaturityService finMaturityService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Process InActive Loans On : " + valueDate);

		finMaturityService.processInActiveFinancesAMZ();

		logger.debug("COMPLETE: Process InActive Loans On : " + valueDate);
		return RepeatStatus.FINISHED;
	}

	// getters / setters

	public void setFinMaturityService(FinMaturityService finMaturityService) {
		this.finMaturityService = finMaturityService;
	}
}
