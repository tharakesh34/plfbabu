package com.pennanttech.pff.scheduler.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennant.backend.service.financemanagement.impl.PresentmentJobService;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoUploadPdcPresentmentJob extends AbstractJob {
	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			getUploadService(context).uploadPresentment(jobDataMap.get("job").toString());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private PresentmentJobService getUploadService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (PresentmentJobService) jobDataMap.get("presentmentJobService");
	}

}
