package com.pennant.backend.endofday.tasklet;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.LedgerDownloadService;

public class LedgerDownload implements Tasklet {
	private Logger logger = Logger.getLogger(LedgerDownload.class);

	@Autowired(required = false)
	private LedgerDownloadService ledgerDownloadService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (ledgerDownloadService != null) {
			ledgerDownloadService.processDownload(SysParamUtil.getAppDate());
		} else {
			logger.debug("LedgerDownloadService Not Configured");
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

}
