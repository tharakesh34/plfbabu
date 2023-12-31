package com.pennanttech.pff.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.MandateProcesses;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoMandateUploadJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getJobDataMap().get("job").toString();
		try {
			getMandateProcess(context).processAutoResponseFiles(jobName);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private MandateProcesses getMandateProcess(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (MandateProcesses) jobDataMap.get("mandateProcess");
	}

}
