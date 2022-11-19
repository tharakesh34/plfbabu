package com.pennant.pff.presentment.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.pff.extension.PresentmentExtension;
import com.pennant.pff.presentment.service.ExtractionService;

public class PreparationTasklet implements Tasklet {

	@Autowired
	private ExtractionService extractionService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		if (PresentmentExtension.AUTO_EXTRACTION) {
			extractionService.preparePresentment();
		}

		return RepeatStatus.FINISHED;
	}

}
