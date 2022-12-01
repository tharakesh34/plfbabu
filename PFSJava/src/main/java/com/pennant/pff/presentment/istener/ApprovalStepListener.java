package com.pennant.pff.presentment.istener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.presentment.dao.PresentmentDAO;

public class ApprovalStepListener implements StepExecutionListener {
	private static final String REMARKS = "Presentment approval completed successfully with, total Records: %d, processed: %d, success: %d, failed: %d";

	private PresentmentDAO presentmentDAO;

	public ApprovalStepListener(PresentmentDAO presentmentDAO) {
		this.presentmentDAO = presentmentDAO;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		//
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		ExitStatus exitStatus = stepExecution.getExitStatus();

		String exitCode = exitStatus.getExitCode();
		String exitDescription = exitStatus.getExitDescription();

		JobParameters jobParameters = stepExecution.getJobParameters();
		long batchId = jobParameters.getLong("BATCH_ID");

		BatchJobQueue jobQueue = new BatchJobQueue();
		jobQueue.setBatchId(batchId);

		if ("FAILED".equals(exitCode)) {
			jobQueue.setFailedStep(stepExecution.getStepName());
			jobQueue.setError(exitDescription);

			presentmentDAO.updateFailureError(jobQueue);
		} else {
			jobQueue = presentmentDAO.getBatch(jobQueue);

			int total = jobQueue.getTotalRecords();
			int processed = jobQueue.getProcessRecords();
			int success = jobQueue.getSuccessRecords();
			int failed = jobQueue.getFailedRecords();
			String msg = jobQueue.getRemarks();

			msg = msg + "\n" + String.format(REMARKS, total, success, processed, failed);

			jobQueue.setBatchId(batchId);
			jobQueue.setRemarks(msg);

			presentmentDAO.updateRemarks(jobQueue);
		}

		return exitStatus;
	}

}
