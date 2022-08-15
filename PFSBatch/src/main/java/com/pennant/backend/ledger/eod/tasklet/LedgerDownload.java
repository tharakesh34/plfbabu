package com.pennant.backend.ledger.eod.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
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

		Date valueDate = SysParamUtil.getAppDate();

		logger.info("START Ledger Download On  {}", valueDate);
		BatchUtil.setExecutionStatus(chunkContext, StepUtil.LEDGER_DOWNLOAD);

		valueDate = DateUtil.addDays(valueDate, -1);

		if (ledgerDownloadService != null) {
			int totalRecords = ledgerDownloadService.processDownload(valueDate);

			StepUtil.LEDGER_DOWNLOAD.setProcessedRecords(totalRecords);
			StepUtil.LEDGER_DOWNLOAD.setTotalRecords(totalRecords);
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
