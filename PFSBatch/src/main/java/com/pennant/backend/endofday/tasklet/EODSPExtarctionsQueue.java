package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennanttech.external.ExtExtractionHook;
import com.pennanttech.pennapps.core.resource.Literal;

public class EODSPExtarctionsQueue implements Tasklet {

	private static Logger logger = LogManager.getLogger(EODSPExtarctionsQueue.class);
	private ExtExtractionHook extExtractionHook;

	public EODSPExtarctionsQueue() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (extExtractionHook != null) {
			extExtractionHook.processFinconSP();
			extExtractionHook.processFinconFileSP();
			extExtractionHook.processUcicRequest();
		}

		return RepeatStatus.FINISHED;
	}

	@Autowired(required = false)
	@Qualifier(value = "extExtractionHook")
	public void setExtExtractionHook(ExtExtractionHook extExtractionHook) {
		this.extExtractionHook = extExtractionHook;
	}

}
