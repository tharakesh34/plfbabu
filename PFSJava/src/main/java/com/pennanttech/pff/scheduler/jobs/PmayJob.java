package com.pennanttech.pff.scheduler.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.PmayProcess;

public class PmayJob extends AbstractJob {
	public PmayJob() {
		super();
	}

	@Override
	public void executeJob(JobExecutionContext context) {
		try {
			getPmayProcess(context).processPmayResponse();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private PmayProcess getPmayProcess(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (PmayProcess) jobDataMap.get("pmayProcess");

	}
}