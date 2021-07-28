package com.pennanttech.pff.jobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennant.backend.service.financemanagement.impl.PresentmentJobService;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoUploadPresentmentJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			getUploadService(context).uploadPresentment(context.getJobDetail().getJobDataMap().get("job").toString());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private PresentmentJobService getUploadService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (PresentmentJobService) jobDataMap.get("presentmentJobService");
	}

}
