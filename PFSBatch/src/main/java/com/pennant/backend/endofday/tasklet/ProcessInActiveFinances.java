package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.FinMaturityService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class ProcessInActiveFinances implements Tasklet {
	private Logger logger = LogManager.getLogger(ProcessInActiveFinances.class);

	private FinMaturityService finMaturityService;

	public ProcessInActiveFinances() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = SysParamUtil.getAppValueDate();
		logger.info("START  Process InActive Loans On {}", valueDate);
		BatchUtil.setExecutionStatus(context, StepUtil.PROCESS_INACTIVE_FINANCES);

		if (SysParamUtil.isAllowed(AmortizationConstants.MONTHENDACC_CALREQ)) {
			finMaturityService.processInActiveFinancesAMZ();
		}

		logger.info("COMPLETED  Process InActive Loans On {}", valueDate);
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setFinMaturityService(FinMaturityService finMaturityService) {
		this.finMaturityService = finMaturityService;
	}
}
