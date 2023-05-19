package com.pennant.pff.letter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;

import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.letter.dao.AutoLetterGenerationDAO;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class LetterGenerationtJobListener implements JobExecutionListener {
	private static Logger logger = LogManager.getLogger(LetterGenerationtJobListener.class);
	private AutoLetterGenerationDAO autoLetterGenerationDAO;

	public LetterGenerationtJobListener(AutoLetterGenerationDAO autoLetterGenerationDAO) {
		super();
		this.autoLetterGenerationDAO = autoLetterGenerationDAO;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		logger.info("Listener process started...");

		String jobName = jobExecution.getJobInstance().getJobName();

		JobParameters jobParameters = jobExecution.getJobParameters();
		String exitStatus = jobExecution.getExitStatus().getExitCode();

		BatchJobQueue jobQueue = new BatchJobQueue();

		long batchId = jobParameters.getLong("BATCH_ID");

		jobQueue.setBatchId(batchId);
		jobQueue.setBatchStatus(exitStatus);

		autoLetterGenerationDAO.updateEndTimeStatus(jobQueue);

		String sysDate = DateUtil.getSysDate(DateFormat.LONG_DATE_TIME);

		logger.debug("Listener process completed...");

		logger.info("{} completed at {} with Batch_ID {} and Batch_Status {} ", jobName, sysDate, batchId, exitStatus);
	}

}
