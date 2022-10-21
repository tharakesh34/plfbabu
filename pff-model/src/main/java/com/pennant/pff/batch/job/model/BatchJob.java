package com.pennant.pff.batch.job.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.JobInstance;

import com.pennant.backend.model.crm.ReturnStatus;

public class BatchJob implements Serializable {
	private static final long serialVersionUID = 1L;

	private String jobName;
	private Long jobExecutionId;
	private Date nextBusinessDate;
	private Long userId;
	private Date startTime;
	private Date endTime;
	private String status;
	private String exitCode;
	private String exitDescription;
	private List<StepDetail> steps = new ArrayList<>();
	private ReturnStatus returnStatus = new ReturnStatus();
	private JobInstance lastJobInstance;

	public BatchJob() {
		super();
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Long getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public Date getNextBusinessDate() {
		return nextBusinessDate;
	}

	public void setNextBusinessDate(Date nextBusinessDate) {
		this.nextBusinessDate = nextBusinessDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public String getExitDescription() {
		return exitDescription;
	}

	public void setExitDescription(String exitDescription) {
		this.exitDescription = exitDescription;
	}

	public List<StepDetail> getSteps() {
		return steps;
	}

	public void setSteps(List<StepDetail> steps) {
		this.steps = steps;
	}

	public ReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(ReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public JobInstance getLastJobInstance() {
		return lastJobInstance;
	}

	public void setLastJobInstance(JobInstance lastJobInstance) {
		this.lastJobInstance = lastJobInstance;
	}

}
