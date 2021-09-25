package com.pennanttech.pff.scheduler.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.mandate.DefaultMandateProcess;

public class AutoMandateAcknowledgeJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			String jobName = jobDataMap.get("job").toString();
			getMandateProcess(context).processAutoResponseFiles(jobName);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private DefaultMandateProcess getMandateProcess(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (DefaultMandateProcess) jobDataMap.get("mandateProcesses");
	}

}
