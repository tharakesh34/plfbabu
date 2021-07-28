package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.service.ExternalInterfaceService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoMandateDownloadJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			getExternalInterfaceService(context).processAutoMandateRequest();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private ExternalInterfaceService getExternalInterfaceService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (ExternalInterfaceService) jobDataMap.get("externalInterfaceService");
	}

}
