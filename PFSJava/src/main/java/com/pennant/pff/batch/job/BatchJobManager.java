package com.pennant.pff.batch.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;

import com.pennant.pff.batch.job.model.BatchJob;
import com.pennant.pff.batch.job.model.StepDetail;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;

public abstract class BatchJobManager extends BatchConfiguration {

	public BatchJobManager(DataSource dataSource, String tablePrefix) throws Exception {
		super(dataSource, tablePrefix);
	}

	public void start(Job job, JobParameters jobParameters) {
		try {
			jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public void restart(long executionId) {
		try {
			jobOperator.restart(executionId);
		} catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | NoSuchJobException
				| JobRestartException | JobParametersInvalidException e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public JobInstance getLastJobInstance(String jobName) {
		return jobExplorer.getLastJobInstance(jobName);
	}

	public JobExecution getLastJobExecution(JobInstance jobInstance) {
		return jobExplorer.getLastJobExecution(jobInstance);
	}

	public Set<JobExecution> findRunningJobExecutions(String jobName) {
		return jobExplorer.findRunningJobExecutions(jobName);
	}

	public List<StepExecution> getStepExecutions(JobInstance jobInstance) throws Exception {
		List<StepExecution> list = new ArrayList<>();

		List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);

		jobExecutions.stream().forEach(je -> list.addAll(je.getStepExecutions()));

		Map<String, StepExecution> distinctSteps = new HashMap<>();

		list.stream().forEach(se -> distinctSteps.put(se.getStepName(), se));

		return new ArrayList<StepExecution>(distinctSteps.values());
	}

	protected void setRunningJobExecutionDetails(BatchJob eodJob) throws Exception {
		JobInstance lastJobInstance = eodJob.getLastJobInstance();

		while (lastJobInstance == null) {
			lastJobInstance = getLastJobInstance(eodJob.getJobName());
		}

		JobInstance currentJobInstance = null;
		while (currentJobInstance == null || currentJobInstance.getInstanceId() <= lastJobInstance.getInstanceId()) {
			currentJobInstance = getLastJobInstance(eodJob.getJobName());
		}

		setJobExecutionDetails(eodJob, currentJobInstance);
	}

	public void setJobExecutionDetails(BatchJob eodJob) throws Exception {
		JobInstance lastJobInstance = getLastJobInstance(eodJob.getJobName());

		if (lastJobInstance != null) {
			setJobExecutionDetails(eodJob, lastJobInstance);
		}
	}

	private void setJobExecutionDetails(BatchJob eodJob, JobInstance jobInstance) throws Exception {
		JobExecution jobExecution = getLastJobExecution(jobInstance);

		BatchStatus batchStatus = jobExecution.getStatus();
		ExitStatus exitStatus = jobExecution.getExitStatus();

		eodJob.setJobName(jobInstance.getJobName());
		eodJob.setJobExecutionId(jobExecution.getJobId());
		eodJob.setStartTime(jobExecution.getStartTime());
		eodJob.setEndTime(jobExecution.getEndTime());

		eodJob.setStatus(batchStatus.getBatchStatus().name());

		eodJob.setExitCode(exitStatus.getExitCode());
		eodJob.setExitDescription(exitStatus.getExitDescription());

		List<StepExecution> stepExecutions = getStepExecutions(jobInstance);

		for (StepExecution stepExecution : stepExecutions) {
			StepDetail sd = new StepDetail();
			sd.setReference(stepExecution.getStepName());
			sd.setStartTime(stepExecution.getStartTime());
			sd.setEndTime(stepExecution.getEndTime());
			sd.setExitCode(stepExecution.getExitStatus().getExitCode());
			sd.setExitDescription(stepExecution.getExitStatus().getExitDescription());

			ExecutionContext executionContext = stepExecution.getExecutionContext();
			DataEngineStatus des = (DataEngineStatus) executionContext.get("STATUS");

			if (des != null) {
				sd.setTotalRecords(des.getTotalRecords());
				sd.setProcessedRecords(des.getProcessedRecords());
				sd.setSuccessRecords(des.getSuccessRecords());
				sd.setFailedRecords(des.getFailedRecords());
			} else {
				sd.setTotalRecords(getTotal(executionContext));
				sd.setProcessedRecords(getProcessed(executionContext));
				sd.setSuccessRecords(getSuccess(executionContext));
				sd.setFailedRecords(getFailed(executionContext));
			}

			eodJob.getSteps().add(sd);
		}
	}

	private int getTotal(ExecutionContext context) {
		return context.getInt("TOTAL", 0);
	}

	private int getProcessed(ExecutionContext context) {
		return context.getInt("PROCESSED", 0);
	}

	private int getSuccess(ExecutionContext context) {
		return context.getInt("SUCCESS", 0);
	}

	private int getFailed(ExecutionContext context) {
		return context.getInt("FAILED", 0);
	}
}
