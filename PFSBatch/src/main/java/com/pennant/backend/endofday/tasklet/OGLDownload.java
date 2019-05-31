package com.pennant.backend.endofday.tasklet;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.OGLDownloadService;

public class OGLDownload implements Tasklet {
	private Logger logger = Logger.getLogger(OGLDownload.class);

	@Autowired(required = false)
	private OGLDownloadService oglDownloadService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (oglDownloadService != null) {
			oglDownloadService.processDownload(DateUtility.getAppDate());
		} else {
			logger.debug("OGLDownloadService Not Configured");
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

}
