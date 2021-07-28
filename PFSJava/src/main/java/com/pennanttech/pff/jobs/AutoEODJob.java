package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.eod.EODService;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoEODJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		logger.debug(Literal.ENTERING);

		getEODService(context).startEOD();
	}

	private EODService getEODService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (EODService) jobDataMap.get("eodService");
	}
}
