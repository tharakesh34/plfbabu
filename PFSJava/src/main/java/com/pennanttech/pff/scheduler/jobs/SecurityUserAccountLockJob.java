package com.pennanttech.pff.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennanttech.pennapps.core.job.AbstractJob;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SecurityUserAccountLockJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		getSecurityUserDAO(context).lockUserAccounts();
	}

	private SecurityUserDAO getSecurityUserDAO(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (SecurityUserDAO) jobDataMap.get("securityUserDAO");
	}
}
