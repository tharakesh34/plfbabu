package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.backend.service.finance.covenant.impl.PutCallAlerts;
import com.pennanttech.pennapps.core.job.AbstractJob;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class FinPutCallAlertsJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		getCovenantAlertsService(context).sendAlerts();
	}

	private PutCallAlerts getCovenantAlertsService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (PutCallAlerts) jobDataMap.get("putCallAlerts");
	}
}
