package com.pennant.pff.presentment.istener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;

import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.dao.PresentmentDAO;

public class PresentmentJobListener implements JobExecutionListener {
	private PresentmentDAO presentmentDAO;

	public PresentmentJobListener(PresentmentDAO presentmentDAO) {
		super();
		this.presentmentDAO = presentmentDAO;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		JobParameters jobParameters = jobExecution.getJobParameters();
		String exitStatus = jobExecution.getExitStatus().getExitCode();
		BatchJobQueue jobQueue = new BatchJobQueue();

		long batchId = jobParameters.getLong("BATCH_ID");

		jobQueue.setBatchId(batchId);
		jobQueue.setBatchStatus(exitStatus);

		presentmentDAO.updateEndTimeStatus(jobQueue);
	}

}
