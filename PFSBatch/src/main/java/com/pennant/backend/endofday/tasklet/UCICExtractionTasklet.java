package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennanttech.external.EODExtractionsHook;
import com.pennanttech.pennapps.core.resource.Literal;

public class UCICExtractionTasklet implements Tasklet {

	private static Logger logger = LogManager.getLogger(UCICExtractionTasklet.class);
	private EODExtractionsHook extExtractionHook;

	public UCICExtractionTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (extExtractionHook != null) {
			extExtractionHook.processUCICExtraction();
		}

		return RepeatStatus.FINISHED;
	}

	@Autowired(required = false)
	@Qualifier(value = "extExtractionHook")
	public void setExtExtractionHook(EODExtractionsHook extExtractionHook) {
		this.extExtractionHook = extExtractionHook;
	}

}
