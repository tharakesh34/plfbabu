package com.pennanttech.pff.knockoff.eod.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.knockoff.service.ExcessKnockOffService;

public class ExcessKnockOffQueuing implements Tasklet {
	private Logger logger = LogManager.getLogger(ExcessKnockOffQueuing.class);

	private ExcessKnockOffService excessKnockOffService;

	public ExcessKnockOffQueuing() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		logger.debug(Literal.ENTERING);

		if (!ImplementationConstants.ALW_AUTO_CROSS_LOAN_KNOCKOFF) {
			logger.debug(Literal.LEAVING);
			return RepeatStatus.FINISHED;
		}

		long count = excessKnockOffService.prepareQueue();

		StepUtil.CROSS_LOAN_KNOCKOFF.setTotalRecords(count);

		logger.info("Queueing preparation for CrossLoan Knock Off completed with total loans {}", count);

		ExcessKnockOffTasklet.processedCount.set(0);
		ExcessKnockOffTasklet.failedCount.set(0);

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setExcessKnockOffService(ExcessKnockOffService excessKnockOffService) {
		this.excessKnockOffService = excessKnockOffService;
	}

}
