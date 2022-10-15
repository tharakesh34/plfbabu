package com.pennant.pff.presentment.tasklet;

import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.pff.extension.PresentmentExtension;
import com.pennant.pff.presentment.service.PresentmentEngine;

public class ApprovalTasklet implements Tasklet {

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		if (!PresentmentExtension.AUTO_APPROVAL) {
			return RepeatStatus.FINISHED;
		}

		Date fromDate = jobParameters.getDate("FromDate");
		Date toDate = jobParameters.getDate("ToDate");
		Date dueDate = jobParameters.getDate("DueDate");

		if (fromDate == null && toDate == null) {
			fromDate = dueDate;
			toDate = dueDate;
		}

		presentmentEngine.approve(fromDate, toDate);

		return RepeatStatus.FINISHED;
	}

}
