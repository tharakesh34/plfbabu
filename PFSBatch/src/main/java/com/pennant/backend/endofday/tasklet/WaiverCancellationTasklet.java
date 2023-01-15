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
import com.pennant.backend.service.finance.FeeWaiverCancelService;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class WaiverCancellationTasklet implements Tasklet {
	private Logger logger = LogManager.getLogger(WaiverCancellationTasklet.class);

	private FeeWaiverCancelService feeWaiverCancelService;

	private static final String SUCCESS_MSG = "Waiver Cancellation completed at {} for the APP_DATE {} with THREAD_ID {}";
	private static final String EXCEPTION_MSG = "Waiver Cancellation failed on {} for the APP_DATE {} with THREAD_ID {}";
	private static final String ERROR_MSG = "Cause {}\\nMessage {}\\nLocalizedMessage {}\\nStackTrace {}";

	public WaiverCancellationTasklet() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Date appDate = SysParamUtil.getAppDate();

		try {
			feeWaiverCancelService.processConditionalWaiver(appDate);
		} catch (Exception e) {
			logger.error(ERROR_MSG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);
			logger.info(EXCEPTION_MSG, DateUtil.getSysDate(DateFormat.FULL_DATE_TIME));

			throw e;
		}

		logger.info(SUCCESS_MSG, DateUtil.getSysDate(DateFormat.FULL_DATE_TIME));
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setFeeWaiverCancelService(FeeWaiverCancelService feeWaiverCancelService) {
		this.feeWaiverCancelService = feeWaiverCancelService;
	}

}
