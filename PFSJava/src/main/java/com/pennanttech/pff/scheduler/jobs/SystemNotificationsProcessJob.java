package com.pennanttech.pff.scheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.notifications.service.ProcessSystemNotifications;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SystemNotificationsProcessJob extends AbstractJob {
	ProcessSystemNotifications processSystemNotifications = null;

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {

		try {
			getProcessSystemNotificationsService(context).processNotifications();
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

	}

	private ProcessSystemNotifications getProcessSystemNotificationsService(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (ProcessSystemNotifications) jobDataMap.get("processSystemNotifications");
	}
}