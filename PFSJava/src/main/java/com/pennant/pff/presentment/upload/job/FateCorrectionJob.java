package com.pennant.pff.presentment.upload.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennant.pff.upload.service.UploadService;
import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;

public class FateCorrectionJob extends AbstractJob {

	@Override
	protected void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			getUploadService(context).uploadProcess();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private UploadService getUploadService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (UploadService) jobDataMap.get("fateCorrectionUploadService");
	}

}
