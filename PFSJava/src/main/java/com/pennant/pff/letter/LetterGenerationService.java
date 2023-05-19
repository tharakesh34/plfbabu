package com.pennant.pff.letter;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennant.pff.letter.job.LetterGenerationJob;
import com.pennant.pff.presentment.service.ExtractionService;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class LetterGenerationService {
	private Logger logger = LogManager.getLogger(ExtractionService.class);

	@Autowired
	private AutoLetterGenerationDAO letterGenerationDAO;

	@Autowired
	private LetterGenerationJob letterGenerationJob;

	public LetterGenerationService() {
		super();
	}

	public void generate() {
		logger.debug(Literal.ENTERING);

		int count = 0;
		count = letterGenerationDAO.getLetterGenerationCount();

		if (count == 0) {
			return;
		}

		long batchID = letterGenerationDAO.createBatch("LETTER_GENERATION", count);

		try {
			start(batchID);
		} catch (Exception e) {
			throw new AppException("Leter generation job failed.", e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void start(long batchID) throws Exception {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();

		JobParametersBuilder builder = new JobParametersBuilder();

		builder.addLong("BATCH_ID", batchID);
		builder.addDate("AppDate", appDate);

		JobParameters jobParameters = builder.toJobParameters();

		try {
			letterGenerationJob.start(jobParameters);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e.getMessage());
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

}
