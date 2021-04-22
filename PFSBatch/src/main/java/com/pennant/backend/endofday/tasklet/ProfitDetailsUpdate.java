package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennanttech.pff.eod.EODUtil;

public class ProfitDetailsUpdate implements Tasklet {
	private Logger logger = LogManager.getLogger(ProfitDetailsUpdate.class);

	private FinanceProfitDetailDAO financeProfitDetailDAO;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_DATE", context);
		logger.debug("START: Finance Profit Details Update Preparation On : {}", valueDate);

		financeProfitDetailDAO.updateODDetailsEOD(valueDate);
		financeProfitDetailDAO.updateTDDetailsEOD(valueDate);
		financeProfitDetailDAO.updateReceivableDetailsEOD(valueDate);
		financeProfitDetailDAO.updateBounceDetailsEOD(valueDate);

		logger.debug("COMPLETE: Finance Profit Details  Preparation On  : {}", valueDate);
		return RepeatStatus.FINISHED;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

}
