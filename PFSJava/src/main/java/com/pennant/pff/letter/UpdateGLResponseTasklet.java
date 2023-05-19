package com.pennant.pff.letter;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennanttech.pennapps.core.resource.Literal;

public class UpdateGLResponseTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(UpdateGLResponseTasklet.class);

	private AutoLetterGenerationDAO autoLetterGenerationDAO;

	public UpdateGLResponseTasklet(AutoLetterGenerationDAO autoLetterGenerationDAO) {
		this.autoLetterGenerationDAO = autoLetterGenerationDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BATCH_ID");
		String responseType = jobParameters.getString("RESPONSE_TYPE");

		synchronized (batchId) {
			List<Long> list = autoLetterGenerationDAO.getResponseHeadersByBatch(batchId, responseType);

		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	private String getRemarks(int totalCount, int successCount, int failedCount) {
		StringBuilder remarks = new StringBuilder();

		if (totalCount > 0) {
			if (failedCount > 0) {
				remarks.append(" Completed with exceptions, total Records: ");
				remarks.append(totalCount);
				remarks.append(", Success: ");
				remarks.append(successCount + ".");
				remarks.append(", Failure: ");
				remarks.append(failedCount + ".");
			} else {
				remarks.append(" Completed successfully, total Records: ");
				remarks.append(totalCount);
				remarks.append(", Success: ");
				remarks.append(successCount + ".");
			}
		}

		return remarks.toString();
	}

}
