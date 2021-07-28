package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DMSJob extends AbstractJob {
	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			getDMSService(context).processDocuments();
		} catch (Exception e) {
			logger.debug(Literal.LEAVING);
		}
	}

	private DMSService getDMSService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (DMSService) jobDataMap.get("dMSService");
	}

}
