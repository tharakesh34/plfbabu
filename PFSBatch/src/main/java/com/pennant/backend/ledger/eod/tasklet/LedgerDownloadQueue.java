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
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.external.LedgerDownloadService;
import com.pennanttech.pff.ledger.dao.LedgerDownloadDAO;

public class LedgerDownloadQueue implements Tasklet {
	private Logger logger = LogManager.getLogger(LedgerDownloadQueue.class);

	private LedgerDownloadService ledgerDownloadService;
	private LedgerDownloadDAO ledgerDownloadDAO;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		if (ledgerDownloadService == null || ledgerDownloadDAO == null || !ledgerDownloadService.isMultiThread()) {
			logger.info("Implementations are not defined / Mutli-thread is not available");

			return RepeatStatus.FINISHED;
		}

		Date appDate = SysParamUtil.getAppDate();
		appDate = DateUtil.addDays(appDate, -1);

		ledgerDownloadDAO.clearQueue();

		long count = ledgerDownloadDAO.prepareQueue(appDate);

		StepUtil.LEDGER_DOWNLOAD.setTotalRecords(count);

		logger.info("Queueing preparation for GL Download completed with total loans {}", count);

		LedgerDownloadProcess.processedCount.set(0);
		LedgerDownloadProcess.failedCount.set(0);

		return RepeatStatus.FINISHED;
	}

	@Autowired(required = false)
	public void setLedgerDownloadService(LedgerDownloadService ledgerDownloadService) {
		this.ledgerDownloadService = ledgerDownloadService;
	}

	@Autowired(required = false)
	public void setLedgerDownloadDAO(LedgerDownloadDAO ledgerDownloadDAO) {
		this.ledgerDownloadDAO = ledgerDownloadDAO;
	}
}
