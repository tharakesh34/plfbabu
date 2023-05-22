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
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.ledger.dao.LedgerDownloadDAO;

public class LedgerSaveLog implements Tasklet {
	private Logger logger = LogManager.getLogger(LedgerDownload.class);

	private LedgerDownloadDAO ledgerDownloadDAO;

	public LedgerSaveLog() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		Date valueDate = SysParamUtil.getAppDate();

		valueDate = DateUtil.addDays(valueDate, -1);

		if (ledgerDownloadDAO != null) {
			ledgerDownloadDAO.saveLog(valueDate);

		} else {
			logger.debug("LedgerDownloadService Not Configured");
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	@Autowired(required = false)
	public void setLedgerDownloadDAO(LedgerDownloadDAO ledgerDownloadDAO) {
		this.ledgerDownloadDAO = ledgerDownloadDAO;
	}

}
