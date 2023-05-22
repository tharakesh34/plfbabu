package com.pennant.backend.endofday.tasklet;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.app.eod.service.UploadFinPftDetailService;
import com.pennanttech.pff.eod.EODUtil;

public class UploadFinProfitDetails implements Tasklet {
	private Logger logger = LogManager.getLogger(UploadFinProfitDetails.class);

	private UploadFinPftDetailService uploadFinPftDetailService;

	private Date dateValueDate = null;

	private ExecutionContext stepExecutionContext;

	public UploadFinProfitDetails() {
	    super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		dateValueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.debug("START: Upload Profit Details for Value Date: " + dateValueDate);

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		try {
			uploadFinPftDetailService.doUploadPftDetails(context);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("COMPLETE: Upload Profit Details for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public UploadFinPftDetailService getUploadFinPftDetailService() {
		return uploadFinPftDetailService;
	}

	public void setUploadFinPftDetailService(UploadFinPftDetailService uploadFinPftDetailService) {
		this.uploadFinPftDetailService = uploadFinPftDetailService;
	}

}
