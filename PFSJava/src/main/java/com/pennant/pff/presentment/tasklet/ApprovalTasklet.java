package com.pennant.pff.presentment.tasklet;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.extension.PresentmentExtension;
import com.pennant.pff.presentment.service.PresentmentEngine;

public class ApprovalTasklet implements Tasklet {
	private PresentmentEngine presentmentEngine;

	public ApprovalTasklet(PresentmentEngine presentmentEngine) {
		super();
		this.presentmentEngine = presentmentEngine;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		if (!PresentmentExtension.AUTO_APPROVAL) {
			return RepeatStatus.FINISHED;
		}

		long batchId = jobParameters.getLong("BTACH_ID");

		presentmentEngine.approve(batchId);

		return RepeatStatus.FINISHED;
	}

}
