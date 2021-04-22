package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.DocumentManagementService;

public class DMSRetrieveProcess implements Tasklet {
	private Logger logger = LogManager.getLogger(DMSRetrieveProcess.class);

	@Autowired(required = false)
	private DocumentManagementService documentManagementService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (documentManagementService != null) {
			documentManagementService.dmsRetrieveProcess();

		} else {
			logger.debug("DMSRetrieveProcess job Not Configured");
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

}
