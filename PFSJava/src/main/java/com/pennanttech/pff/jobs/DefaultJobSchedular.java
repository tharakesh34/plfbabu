package com.pennanttech.pff.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.TriggerBuilder;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.scheduler.AbstractJobScheduler;
import com.pennanttech.pennapps.core.scheduler.Job;

public class DefaultJobSchedular extends AbstractJobScheduler {
	private static final Logger logger = LogManager.getLogger(DefaultJobSchedular.class);

	private static final String GST_INVOICE_GENERATE_JOB = "GST_INVOICE_GENERATE_JOB";
	private static final boolean GENERATE_GST_INVOICE_NO = Boolean.valueOf(App.getProperty("gstInvoice.job.enabled"));
	private static final String GST_INVOICE_SCHEDULE_TIME = App.getProperty("gstInvoice.scheduleTime");
	private static final String SYS_NOTIFICATION_INVOKE_TIME = App
			.getProperty("sys.notification.invoke.cronExpression");
	private static final String SYS_NOTIFICATION_PROCESS_TIME = App
			.getProperty("sys.notification.process.cronExpression");
	private static final String SYS_NOTIFICATIONS_INVOKE_JOB = "SYS_NOTIFICATIONS_INVOKE_JOB";
	private static final String SYS_NOTIFICATIONS_PROCESS_JOB = "SYS_NOTIFICATIONS_PROCESS_JOB";
	private static final String AUTO_RECPT_RESPONSE_JOB = "AUTO_RECPT_RES_JOB";
	private static final String AUTO_RECPT_RESPONSE_JOB_TRIGGER = "AUTO_RECPT_RES_JOB_TRIGGER";

	@Override
	protected void registerJobs() throws Exception {
		registerGstInvoiceJob();
		registerSystemNotificationInvokeJob();
		registerSystemNotificationProcessJob();
		autoReceiptResponseJob();
		registercovenantAlertsJob();
		registerPutCallAlertsJob();
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
			job.setTrigger(
					TriggerBuilder.newTrigger().withIdentity("GST_INVOICE_GENERATE_JOB", "GST_INVOICE_GENERATE_JOB")
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

	private void autoReceiptResponseJob() {
		logger.debug(Literal.ENTERING);
		Job job = new Job();

		String scheduleTime = SysParamUtil.getValueAsString("RECEIPT_RESPONSE_JOB_CORNEXP");

		job.setJobDetail(JobBuilder.newJob(ReceiptResponseJob.class)
				.withIdentity(AUTO_RECPT_RESPONSE_JOB, AUTO_RECPT_RESPONSE_JOB)
				.withDescription("Auto receipt reponse job").build());
		job.setTrigger(TriggerBuilder.newTrigger()
				.withIdentity(AUTO_RECPT_RESPONSE_JOB_TRIGGER, AUTO_RECPT_RESPONSE_JOB_TRIGGER)
				.withDescription("Auto receipt reponse trigger")
				.withSchedule(CronScheduleBuilder.cronSchedule(scheduleTime)).build());

		jobs.put(AUTO_RECPT_RESPONSE_JOB, job);

		logger.debug(Literal.LEAVING);
	}

	private void registercovenantAlertsJob() {
		logger.debug(Literal.ENTERING);

		String alertsRequired = App.getProperty("covenants.alerts");

		if (alertsRequired == null) {
			alertsRequired = "false";
		}

		if (!Boolean.parseBoolean(alertsRequired)) {
			logger.warn(CovenantAlertsJob.JOB_KEY + " not registred.");
			return;
		}

		String jobKey = CovenantAlertsJob.JOB_KEY;
		String trigger = CovenantAlertsJob.JOB_TRIGGER;
		String cronExpression = CovenantAlertsJob.getCronExpression();

		Job job = new Job();

		job.setJobDetail(JobBuilder.newJob(CovenantAlertsJob.class).withIdentity(jobKey, jobKey).withDescription(jobKey)
				.build());
		job.setTrigger(TriggerBuilder.newTrigger().withIdentity(trigger, trigger).withDescription(trigger)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build());

		jobs.put(AUTO_RECPT_RESPONSE_JOB, job);

		logger.debug(Literal.LEAVING);
	}

	private void registerPutCallAlertsJob() {
		logger.debug(Literal.ENTERING);

		String alertsRequired = App.getProperty("fin.put.call.alerts");

		if (alertsRequired == null) {
			alertsRequired = "false";
		}

		if (!Boolean.parseBoolean(alertsRequired)) {
			logger.warn(FinPutCallAlertsJob.JOB_KEY + " not registred.");
			return;
		}

		String jobKey = FinPutCallAlertsJob.JOB_KEY;
		String trigger = FinPutCallAlertsJob.JOB_TRIGGER;
		String cronExpression = FinPutCallAlertsJob.getCronExpression();

		Job job = new Job();

		job.setJobDetail(JobBuilder.newJob(FinPutCallAlertsJob.class).withIdentity(jobKey, jobKey)
				.withDescription(jobKey).build());
		job.setTrigger(TriggerBuilder.newTrigger().withIdentity(trigger, trigger).withDescription(trigger)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build());

		jobs.put(AUTO_RECPT_RESPONSE_JOB, job);

		logger.debug(Literal.LEAVING);

	}

}