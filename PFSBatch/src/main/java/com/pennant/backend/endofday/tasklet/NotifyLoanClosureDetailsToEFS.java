package com.pennant.backend.endofday.tasklet;

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
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.external.service.ExternalFinanceSystemService;

public class NotifyLoanClosureDetailsToEFS implements Tasklet {
	private Logger logger = LogManager.getLogger(NotifyLoanClosureDetailsToEFS.class);

	private ExternalFinanceSystemService externalFinanceSystemService;

	public NotifyLoanClosureDetailsToEFS() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		if (externalFinanceSystemService == null) {
			return RepeatStatus.FINISHED;

		}

		Date valueDate = SysParamUtil.getAppValueDate();
		logger.info("START: Notify LoanClosure Details To EFS On {}", valueDate);
		BatchUtil.setExecutionStatus(context, StepUtil.LOAN_CLOSURE_DETAILS);

		try {

			int count = externalFinanceSystemService.loanClosureDetails();

			StepUtil.LOAN_CLOSURE_DETAILS.setTotalRecords(count);
			StepUtil.LOAN_CLOSURE_DETAILS.setProcessedRecords(count);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		logger.debug("COMPLETE:Notify LoanClosure Details To EFS On {}", valueDate);
		return RepeatStatus.FINISHED;

	}

	@Autowired(required = false)
	public void setExternalFinanceSystemService(ExternalFinanceSystemService externalFinanceSystemService) {
		this.externalFinanceSystemService = externalFinanceSystemService;
	}

}
