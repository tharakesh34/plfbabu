package com.pennanttech.pff.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SecurityUserAccountLockJob implements Job {
	private static final Logger logger = LogManager.getLogger(SecurityUserAccountLockJob.class);

	static final String JOB_KEY = "USER_AUTO_LOCKING_JOB";
	static final String JOB_TRIGGER = "USER_AUTO_LOCKING_JOB_TRIGGER";

	// Every day 5 Am
	static final String CRON_EXPRESSION = "0 0 5 1/1 * ? *";

	private static SecurityUserDAO securityUserDAO;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();

		logger.trace(String.format("JOB: %s", jobName));

		getSecurityUserDAO().lockUserAccounts();
	}

	private SecurityUserDAO getSecurityUserDAO() {
		if (securityUserDAO == null) {
			securityUserDAO = SpringBeanUtil.getBean(SecurityUserDAO.class);
		}
		return securityUserDAO;
	}
}
