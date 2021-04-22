package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.external.LedgerDownloadService;

public class LedgerDownload implements Tasklet {
	private Logger logger = LogManager.getLogger(LedgerDownload.class);

	private LedgerDownloadService ledgerDownloadService;

	public LedgerDownload() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		Date valueDate = EODUtil.getDate("APP_VALUEDATE", chunkContext);
		Date lastDate = EODUtil.getDate("APP_LAST_BUS_DATE", chunkContext);

		logger.info("START Ledger Download On  {}", valueDate);

		BatchUtil.setExecutionStatus(chunkContext, StepUtil.LEDGER_DOWNLOAD);

		if (ledgerDownloadService != null) {
			try {
				int totalRecords = ledgerDownloadService.processDownload(lastDate);
				StepUtil.LEDGER_DOWNLOAD.setTotalRecords(totalRecords);
				StepUtil.LEDGER_DOWNLOAD.setProcessedRecords(totalRecords);
			} catch (Exception e) {
				throw e;
			}

		} else {
			logger.debug("LedgerDownloadService Not Configured");
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	@Autowired(required = false)
	public void setLedgerDownloadService(LedgerDownloadService ledgerDownloadService) {
		this.ledgerDownloadService = ledgerDownloadService;
	}

}
