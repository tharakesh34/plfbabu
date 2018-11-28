package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.service.finance.FinanceCancellationService;

public class AutoFinanceCancellation  implements Tasklet  {
	private Logger				logger	= Logger.getLogger(AutoFinanceCancellation.class);

	private FinanceCancellationService financeCancellationService;
	
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Prepare OD Cancellation Loan's On : " + valueDate);

		if (ImplementationConstants.ALW_LOAN_AUTO_CANCEL) {
			financeCancellationService.executeLoanCancelProcess();
		}

		logger.debug("COMPLETE: Prepare OD Cancellation Loan's On :" + valueDate);
		return RepeatStatus.FINISHED;
	}
	
	public FinanceCancellationService getFinanceCancellationService() {
		return financeCancellationService;
	}

	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

}
