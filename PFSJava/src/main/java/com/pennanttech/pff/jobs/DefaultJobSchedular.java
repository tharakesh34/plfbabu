package com.pennanttech.pff.jobs;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.TriggerBuilder;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.scheduler.AbstractJobScheduler;
import com.pennanttech.pennapps.core.scheduler.Job;

public class DefaultJobSchedular extends AbstractJobScheduler {
	private static final String GST_INVOICE_GENERATE_JOB = "GST_INVOICE_GENERATE_JOB";
	private static final boolean GENERATE_GST_INVOICE_NO = Boolean.valueOf(App.getProperty("gstInvoice.job.enabled"));
	private static final String GST_INVOICE_SCHEDULE_TIME = App.getProperty("gstInvoice.scheduleTime");
	private static final String SYS_NOTIFICATION_INVOKE_TIME = App
			.getProperty("sys.notification.invoke.cronExpression");
	private static final String SYS_NOTIFICATION_PROCESS_TIME = App
			.getProperty("sys.notification.process.cronExpression");
	private static final String SYS_NOTIFICATIONS_INVOKE_JOB = "SYS_NOTIFICATIONS_INVOKE_JOB";
	private static final String SYS_NOTIFICATIONS_PROCESS_JOB = "SYS_NOTIFICATIONS_PROCESS_JOB";


	@Override
	protected void registerJobs() throws Exception {
		registerGstInvoiceJob();
		registerSystemNotificationInvokeJob();
		registerSystemNotificationProcessJob();
	}


	/**
	 * Invoice number Auto Generation
	 */
	private void registerGstInvoiceJob() {
		
		if (GENERATE_GST_INVOICE_NO) {
			Job job = new Job();
			job.setJobDetail(JobBuilder.newJob(GSTInvoiceGeneratorJob.class)
					.withIdentity(GST_INVOICE_GENERATE_JOB, GST_INVOICE_GENERATE_JOB)
					.withDescription("GST Invoice Preparation").build());
			job.setTrigger(TriggerBuilder.newTrigger().withIdentity("GST_INVOICE_GENERATE_JOB", "GST_INVOICE_GENERATE_JOB")
					.withDescription("GST Invoice job trigger")
					.withSchedule(CronScheduleBuilder.cronSchedule(GST_INVOICE_SCHEDULE_TIME)).build());
			
			jobs.put(GST_INVOICE_GENERATE_JOB, job);
		}

	}

	private void registerSystemNotificationInvokeJob() {

		Job job = new Job();
		job.setJobDetail(JobBuilder.newJob(SystemNotificationsInvokeJob.class)
				.withIdentity(SYS_NOTIFICATIONS_INVOKE_JOB, SYS_NOTIFICATIONS_INVOKE_JOB)
				.withDescription("Invoking System Notifications").build());
		job.setTrigger(
				TriggerBuilder.newTrigger().withIdentity("SYS_NOTIFICATIONS_INVOKE_JOB", "SYS_NOTIFICATIONS_INVOKE_JOB")
						.withDescription("System Notifcations Invoke Job Trigger")
						.withSchedule(CronScheduleBuilder.cronSchedule(SYS_NOTIFICATION_INVOKE_TIME)).build());

		jobs.put(SYS_NOTIFICATIONS_INVOKE_JOB, job);

	}

	private void registerSystemNotificationProcessJob() {

		Job job = new Job();
		job.setJobDetail(JobBuilder.newJob(SystemNotificationsProcessJob.class)
				.withIdentity(SYS_NOTIFICATIONS_PROCESS_JOB, SYS_NOTIFICATIONS_PROCESS_JOB)
				.withDescription("Processing System Notifications").build());
		job.setTrigger(TriggerBuilder.newTrigger()
				.withIdentity("SYS_NOTIFICATIONS_PROCESS_JOB", "SYS_NOTIFICATIONS_PROCESS_JOB")
				.withDescription("System Notifcations Process Job Trigger")
				.withSchedule(CronScheduleBuilder.cronSchedule(SYS_NOTIFICATION_PROCESS_TIME)).build());

		jobs.put(SYS_NOTIFICATIONS_PROCESS_JOB, job);

	}

}