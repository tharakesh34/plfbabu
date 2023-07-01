package com.pennant.backend.endofday.tasklet.external;

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

public class BaselTwoExtractionTasklet implements Tasklet {

	private static Logger logger = LogManager.getLogger(BaselTwoExtractionTasklet.class);
	private EODExtractionsHook extExtractionHook;

	public BaselTwoExtractionTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (extExtractionHook != null) {
			extExtractionHook.processBaselTwoExtraction();
		}

		return RepeatStatus.FINISHED;
	}

	@Autowired(required = false)
	@Qualifier(value = "extExtractionHook")
	public void setExtExtractionHook(EODExtractionsHook extExtractionHook) {
		this.extExtractionHook = extExtractionHook;
	}

}
