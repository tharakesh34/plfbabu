package com.pennant.pff.presentment.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.pff.extension.PresentmentExtension;
import com.pennant.pff.presentment.ExtractionJobManager;

public class PresentmentExtractionTasklet implements Tasklet {

	@Autowired
	private ExtractionJobManager presentmentExtractionService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		if (PresentmentExtension.AUTO_EXTRACTION) {
			presentmentExtractionService.extractPresentment();
		}

		return RepeatStatus.FINISHED;
	}

}
