package com.pennanttech.pff.manualadvise.eod.tasklet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.step.StepUtil;

public class ManualAdvisesCancellation implements Tasklet {
	private Logger logger = LogManager.getLogger(ManualAdvisesCancellation.class);

	private ManualAdviseService manualAdviseService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
			int count = manualAdviseService.cancelFutureDatedAdvises();

			StepUtil.CANCEL_INACTIVE_FINANCES_ADVISES.setTotalRecords(count);
			StepUtil.CANCEL_INACTIVE_FINANCES_ADVISES.setProcessedRecords(count);

			BatchUtil.setExecutionStatus(chunkContext, StepUtil.CANCEL_INACTIVE_FINANCES_ADVISES);
		}

		logger.debug(Literal.LEAVING);

		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

}
