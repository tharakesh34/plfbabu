package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class AutoFinanceCancellation implements Tasklet {
	private Logger logger = LogManager.getLogger(AutoFinanceCancellation.class);

	private FinanceDetailService financeDetailService;

	public AutoFinanceCancellation() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.info("START Prepare OD Cancellation Loan's On {}", valueDate);
		BatchUtil.setExecutionStatus(context, StepUtil.AUTO_CANCELLATION);

		financeDetailService.executeAutoFinRejectProcess();

		logger.debug("COMPLETE: Prepare OD Cancellation Loan's On {}", valueDate);
		return RepeatStatus.FINISHED;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	@Autowired
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}
