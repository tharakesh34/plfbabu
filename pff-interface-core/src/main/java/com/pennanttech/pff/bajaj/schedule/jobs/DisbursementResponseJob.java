package com.pennanttech.pff.bajaj.schedule.jobs;

import java.io.Serializable;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.bajaj.services.DisbursementResponseFileService;

public class DisbursementResponseJob implements Job, Serializable {
	private static final long serialVersionUID = 1L;

	private static DisbursementResponseFileService disbursementResponseFileService;

	public DisbursementResponseJob() {
		super();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		disbursementResponseFileService.execute(context);
	}

	public static void setDisbursementResponseFileService(DisbursementResponseFileService disbursementResponseFileService) {
		DisbursementResponseJob.disbursementResponseFileService = disbursementResponseFileService;
	}
}