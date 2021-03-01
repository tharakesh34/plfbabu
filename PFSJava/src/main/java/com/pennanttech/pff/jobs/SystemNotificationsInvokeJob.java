package com.pennanttech.pff.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pff.notifications.service.InvokeSysNotifications;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SystemNotificationsInvokeJob implements Job {
	private static final Logger logger = LogManager.getLogger(SystemNotificationsInvokeJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));
		InvokeSysNotifications sysNotificationsService = null;

		try {
			sysNotificationsService = (InvokeSysNotifications) SpringBeanUtil.getBean("invokeSysNotifications");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (sysNotificationsService != null) {
			try {
				sysNotificationsService.invokeNotifications();
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

	}

}
