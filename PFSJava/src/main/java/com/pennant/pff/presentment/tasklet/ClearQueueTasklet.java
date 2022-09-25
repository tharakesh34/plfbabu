package com.pennant.pff.presentment.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.pff.presentment.service.PresentmentEngine;

public class ClearQueueTasklet implements Tasklet {

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		presentmentEngine.clearQueue();

		return RepeatStatus.FINISHED;
	}

}
