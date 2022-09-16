package com.pennanttech.pff.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.job.AbstractJob;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SecurityUserAccountLockJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_USR_LOCKING_JOB)) {
			getSecurityUserDAO(context).lockUserAccounts();
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_USR_DISABLE_JOB)) {
			getSecurityUserService(context).disableUserAccount();
		}
	}

	private SecurityUserDAO getSecurityUserDAO(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (SecurityUserDAO) jobDataMap.get("securityUserDAO");
	}

	private SecurityUserService getSecurityUserService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (SecurityUserService) jobDataMap.get("securityUserService");
	}
}
