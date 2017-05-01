package com.pennanttech.pff.core.job.scheduler;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobSchedulerDetails {
	private JobDetail	jobDetail;
	private Trigger		trigger;
	private String		configName;

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

}
