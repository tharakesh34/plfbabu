package com.pennant.backend.endofday.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.external.GSTDownloadService;

public class GSTDownload implements Tasklet {
	private Logger logger = LogManager.getLogger(GSTDownload.class);

	private GSTDownloadService gstDownloadService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (gstDownloadService != null) {
			BatchUtil.setExecutionStatus(chunkContext, StepUtil.GST_DOWNLOAD);
			gstDownloadService.processDownload();
		} else {
			logger.debug("GSTDownload Not Configred");
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	@Autowired(required = false)
	public void setGstDownloadService(GSTDownloadService gstDownloadService) {
		this.gstDownloadService = gstDownloadService;
	}

}
