package com.pennant.pff.presentment.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.presentment.PresentmentExtractionService;

public class PresentmentExtractionTasklet implements Tasklet {

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		PresentmentExtractionService.extractPresentment();

		return RepeatStatus.FINISHED;
	}

}
