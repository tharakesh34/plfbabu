package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.backend.service.finance.lmsservicelog.impl.LMSServiceLogAlerts;
import com.pennanttech.pennapps.core.job.AbstractJob;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LMSServiceLogAlertsJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		getCovenantAlertsService(context).sendAlerts();
	}

	private LMSServiceLogAlerts getCovenantAlertsService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (LMSServiceLogAlerts) jobDataMap.get("lmsServiceLogAlerts");
	}
}
