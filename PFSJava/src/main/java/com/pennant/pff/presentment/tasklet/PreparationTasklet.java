package com.pennant.pff.presentment.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.pff.extension.PresentmentExtension;
import com.pennant.pff.presentment.service.ExtractionService;
import com.pennanttech.pennapps.core.resource.Literal;

public class PreparationTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(PreparationTasklet.class);

	@Autowired
	private ExtractionService extractionService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (PresentmentExtension.AUTO_EXTRACTION) {
			extractionService.preparePresentment();
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

}
