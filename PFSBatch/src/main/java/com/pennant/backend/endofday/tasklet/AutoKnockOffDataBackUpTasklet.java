package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AutoKnockOffService;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoKnockOffDataBackUpTasklet implements Tasklet {
	private static Logger logger = LogManager.getLogger(AutoKnockOffDataBackUpTasklet.class);

	private AutoKnockOffService eodAutoKnockOffService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (ImplementationConstants.ALLOW_AUTO_KNOCK_OFF) {
			eodAutoKnockOffService.backupExecutionData();
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setEodAutoKnockOffService(AutoKnockOffService eodAutoKnockOffService) {
		this.eodAutoKnockOffService = eodAutoKnockOffService;
	}

}
