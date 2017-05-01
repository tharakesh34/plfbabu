package com.pennanttech.pff.bajaj.schedule.job;

import java.io.Serializable;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.services.AutoDisburseFileResponseService;

public class AutoDisburseFileResponseJob implements Job, Serializable {
	private static final long						serialVersionUID	= 1L;

	private static AutoDisburseFileResponseService	autoDisburseFileResponseService;

	public AutoDisburseFileResponseJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		autoDisburseFileResponseService.execute(context);
	}

	public static void setAutoDisburseFileResponseService(
			AutoDisburseFileResponseService autoDisburseFileResponseService) {
		AutoDisburseFileResponseJob.autoDisburseFileResponseService = autoDisburseFileResponseService;
	}

}