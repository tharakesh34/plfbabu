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
import com.pennanttech.pff.notifications.service.ProcessSystemNotifications;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SystemNotificationsProcessJob implements Job {
	private static final Logger logger = LogManager.getLogger(SystemNotificationsInvokeJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getKey().getName();
		logger.debug(String.format("JOB: %s", jobName));

		ProcessSystemNotifications processSystemNotifications = null;

		try {
			processSystemNotifications = (ProcessSystemNotifications) SpringBeanUtil
					.getBean("processSystemNotifications");
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		if (processSystemNotifications != null) {
			try {
				processSystemNotifications.processNotifications();
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}

	}

}