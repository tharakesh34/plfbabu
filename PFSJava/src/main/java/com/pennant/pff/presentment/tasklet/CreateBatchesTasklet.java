package com.pennant.pff.presentment.tasklet;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class CreateBatchesTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(CreateBatchesTasklet.class);

	private PresentmentEngine presentmentEngine;

	public CreateBatchesTasklet(PresentmentEngine presentmentEngine) {
		super();
		this.presentmentEngine = presentmentEngine;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BATCH_ID");

		List<PresentmentHeader> headerList = presentmentEngine.getPresentmenHeaders(batchId);

		for (PresentmentHeader ph : headerList) {
			presentmentEngine.sendToPresentment(ph);
		}

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

}
