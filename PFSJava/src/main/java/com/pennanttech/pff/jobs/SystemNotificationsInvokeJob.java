package com.pennanttech.pff.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.pennanttech.pennapps.core.job.AbstractJob;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.notifications.service.InvokeSysNotifications;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SystemNotificationsInvokeJob extends AbstractJob {

	@Override
	public void executeJob(JobExecutionContext context) throws JobExecutionException {
		try {
			getInvokeSysNotifications(context).invokeNotifications();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private InvokeSysNotifications getInvokeSysNotifications(JobExecutionContext context) {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		return (InvokeSysNotifications) jobDataMap.get("invokeSysNotifications");
	}
}
