package com.pennanttech.pff.jobs;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.financemanagement.impl.PresentmentJobService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.scheduler.AbstractJobScheduler;
import com.pennanttech.pennapps.core.scheduler.Job;
import com.pennanttech.pennapps.dms.DMSProperties;
import com.pennanttech.pennapps.dms.DMSStorage;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pff.eod.EODService;
import com.pennanttech.pff.external.DisbursementResponse;
import com.pennanttech.pff.external.MandateProcesses;
import com.pennanttech.pff.external.disbursement.DisbursementProcessJob;
import com.pennanttech.pff.external.disbursement.DisbursementRequestService;
import com.pennanttech.pff.external.service.ExternalInterfaceService;
import com.pennanttech.pff.process.ExtractCustomerData;
import com.pennanttech.pff.schedule.jobs.CustomerExtractJob;
import com.pennanttech.pff.schedule.jobs.DMSAddDocJob;

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
	private static final String LMS_SERVICE_LOG_ALERTS_JOB = "LMS_SERVICE_LOG_ALERTS_JOB";
	private static final String DMS_INVOKE_TIME = App.getProperty("dms.invoke.cronExpression");
	private static final String REG_CASH_BACK_DBD_JOB = "REG_CASH_BACK_DBD_JOB";
	private static final String REG_CASH_BACK_DBD_JOB_TRIGGER = "REG_CASH_BACK_DBD_JOB_TRIGGER";

	private DMSService dMSService;
	DMSStorage dmsStorageType = DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE));

	private DisbursementRequestService disbursementRequestService;
	private DisbursementResponse defaultDisbursementResponse;
	private DisbursementResponse disbursementResponse;
	private MandateProcesses mandateProcesses;
	private MandateProcesses defaultMandateProcess;
	private ExternalInterfaceService externalInterfaceService;
	private ExtractCustomerData extractCustomerData;
	private PresentmentJobService presentmentJobService;
	private EODService eodService;

	@Override
	protected void registerJobs() throws Exception {
		disbursementProcessJob();
		registerGstInvoiceJob();
		registerSystemNotificationInvokeJob();
		registerSystemNotificationProcessJob();
		autoReceiptResponseJob();
		registercovenantAlertsJob();
		registerPutCallAlertsJob();
		registerLMSServiceAlertsJob();
		registerUserAccountLockingJob();
		registerDmsServiceInvokeJob();
		registerCashBackDbdInvokeJob();

		if ((DMSStorage.FS == dmsStorageType) || (DMSStorage.EXTERNAL == dmsStorageType)) {
			registerDMSJob();
		}

		if (ImplementationConstants.DISBURSEMENT_AUTO_DOWNLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.DISBURSEMENT_AUTO_DOWNLOAD)) {
			registerAutoDisbDownlaodJob();
		}

		if (ImplementationConstants.DISBURSEMENT_AUTO_UPLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.DISBURSEMENT_AUTO_UPLOAD)) {
			registerDisbAutoUploadJob();
		}

		if (ImplementationConstants.MANDATE_AUTO_DOWNLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_AUTO_DOWNLOAD)) {
			registerMandateAutoDownloadJob();
		}

		if (ImplementationConstants.MANDATE_AUTO_UPLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_AUTO_UPLOAD)) {
			registerMandateAutoUploadJob();
			registerMandateAutoAcknowledgeJob();
		}

		if (ImplementationConstants.PRESENTMENT_AUTO_DOWNLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.PRESENTMENT_AUTO_DOWNLOAD)) {
			registerPresentmentAutoExtractJob();
		}

		if (ImplementationConstants.PRESENTMENT_AUTO_UPLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.PRESENTMENT_AUTO_UPLOAD)) {
			registerPresentmentNachAutoUploadJob();
			registerPresentmentPdcAutoUploadJob();
		}

		if (App.getBooleanProperty("customer.portal.enabled")) {
			registerCustomerPortalJob();
		}

		if (ImplementationConstants.AUTO_EOD_REQUIRED) {
			registerAutoEODJob();
			registerEODReminderJob();
			registerEODDelayJob();
		}
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

		try {
			CronExpression.validateExpression(SYS_NOTIFICATION_INVOKE_TIME);
		} catch (Exception e) {
			return;
		}

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

		try {
			CronExpression.validateExpression(SYS_NOTIFICATION_PROCESS_TIME);
		} catch (Exception e) {
			return;
		}

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

		try {
			CronExpression.validateExpression(scheduleTime);
		} catch (Exception e) {
			return;
		}

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

		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}

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

		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}

		Job job = new Job();

		job.setJobDetail(JobBuilder.newJob(FinPutCallAlertsJob.class).withIdentity(jobKey, jobKey)
				.withDescription(jobKey).build());
		job.setTrigger(TriggerBuilder.newTrigger().withIdentity(trigger, trigger).withDescription(trigger)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build());

		jobs.put(AUTO_RECPT_RESPONSE_JOB, job);

		logger.debug(Literal.LEAVING);

	}

	private void registerLMSServiceAlertsJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = LMSServiceLogAlertsJob.JOB_KEY;
		String trigger = LMSServiceLogAlertsJob.JOB_TRIGGER;
		String cronExpression = SysParamUtil.getValueAsString("LMS_SERVICE_LOG_ALERTS_SCHD_TIME");

		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			return;
		}

		String lmsServiceLogReq = SysParamUtil.getValueAsString(SMTParameterConstants.LMS_SERVICE_LOG_REQ);
		if (!StringUtils.equals(lmsServiceLogReq, PennantConstants.YES)) {
			logger.debug("LMS_SERVICE_LOG_REQ parameter value :" + lmsServiceLogReq);
			return;
		}
		Job job = new Job();

		job.setJobDetail(JobBuilder.newJob(LMSServiceLogAlertsJob.class).withIdentity(jobKey, jobKey)
				.withDescription(jobKey).build());
		job.setTrigger(TriggerBuilder.newTrigger().withIdentity(trigger, trigger).withDescription(trigger)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build());

		jobs.put(LMS_SERVICE_LOG_ALERTS_JOB, job);

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Lock the user accounts when the user is not logging into the application from the specified number of days.
	 */
	private void registerUserAccountLockingJob() {
		logger.debug(Literal.ENTERING);

		String cronExpression = SysParamUtil.getValueAsString(SMTParameterConstants.USR_ACCT_LOCK_CRON_EXPRESSION);

		if (cronExpression == null) {
			cronExpression = SecurityUserAccountLockJob.CRON_EXPRESSION;
		}

		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			logger.warn(String.format("The cron expression specified with %s SMTP parameter is invalid.",
					SMTParameterConstants.USR_ACCT_LOCK_CRON_EXPRESSION));
			return;
		}

		String jobTriggger = SecurityUserAccountLockJob.JOB_TRIGGER;
		String jobKey = SecurityUserAccountLockJob.JOB_KEY;

		Job job = new Job();
		job.setJobDetail(JobBuilder.newJob(SecurityUserAccountLockJob.class).withIdentity(jobKey, jobKey)
				.withDescription("User account locking job").build());
		job.setTrigger(TriggerBuilder.newTrigger().withIdentity(jobTriggger, jobTriggger)
				.withDescription("User account locking job trigger")
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build());
		jobs.put(jobKey, job);
		logger.debug(Literal.LEAVING);
	}

	private void registerDmsServiceInvokeJob() {

		try {
			CronExpression.validateExpression(DMS_INVOKE_TIME);
		} catch (Exception e) {
			return;
		}

		Job job = new Job();
		job.setJobDetail(JobBuilder.newJob(DMSAddDocJob.class).withIdentity(DMS_INVOKE_TIME, DMS_INVOKE_TIME)
				.withDescription("Invoking Dms Serivce").build());
		job.setTrigger(
				TriggerBuilder.newTrigger().withIdentity("SYS_DMS_SERVICE_INVOKE_TIME", "SYS_DMS_SERVICE_INVOKE_TIME")
						.withDescription("Dms Service Invoke Job Trigger")
						.withSchedule(CronScheduleBuilder.cronSchedule(DMS_INVOKE_TIME)).build());

		jobs.put(DMS_INVOKE_TIME, job);

	}

	private void registerCashBackDbdInvokeJob() {
		logger.debug(Literal.ENTERING);

		if (SysParamUtil.isAllowed(SMTParameterConstants.CD_CASHBACK_JOB_REQUIRED)) {

			Job job = new Job();

			String scheduleTime = SysParamUtil.getValueAsString(SMTParameterConstants.CD_CASHBACK_CRON_EXPRESSION);

			try {
				CronExpression.validateExpression(scheduleTime);
			} catch (Exception e) {
				return;
			}

			job.setJobDetail(
					JobBuilder.newJob(CashBackDBDJob.class).withIdentity(REG_CASH_BACK_DBD_JOB, REG_CASH_BACK_DBD_JOB)
							.withDescription("Auto receipt reponse job").build());
			job.setTrigger(TriggerBuilder.newTrigger()
					.withIdentity(REG_CASH_BACK_DBD_JOB_TRIGGER, REG_CASH_BACK_DBD_JOB_TRIGGER)
					.withDescription("Auto receipt reponse trigger")
					.withSchedule(CronScheduleBuilder.cronSchedule(scheduleTime)).build());

			jobs.put(REG_CASH_BACK_DBD_JOB, job);
		}
		logger.debug(Literal.LEAVING);
	}

	private void registerDMSJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = DMSJob.JOB_KEY;
		String jobDescription = DMSJob.JOB_KEY_DESCRIPTION;
		String trigger = DMSJob.JOB_TRIGGER;
		String cronExpression = DMSJob.getCronExpression();

		JobDataMap args = new JobDataMap();
		args.put("dMSService", dMSService);

		registerJob(DMSJob.class, jobKey, jobDescription, trigger, cronExpression, args);

		logger.debug(Literal.LEAVING);
	}

	private void registerDisbAutoUploadJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = AutoDisbursementUploadJob.JOB_KEY;
		String jobDescription = AutoDisbursementUploadJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoDisbursementUploadJob.JOB_TRIGGER;
		String cronExpression = AutoDisbursementUploadJob.getCronExpression();

		JobDataMap args = new JobDataMap();
		args.put("disbursementResponse", getDisbursementResponse());

		registerJob(AutoDisbursementUploadJob.class, jobKey, jobDescription, trigger, cronExpression, args);

		logger.debug(Literal.LEAVING);
	}

	private void registerAutoDisbDownlaodJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = AutoDisbursementDownloadJob.JOB_KEY;
		String jobDescription = AutoDisbursementDownloadJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoDisbursementDownloadJob.JOB_TRIGGER;
		String cronExpression = AutoDisbursementDownloadJob.getCronExpression();

		JobDataMap args = new JobDataMap();
		args.put("disbursementRequestService", disbursementRequestService);

		registerJob(AutoDisbursementDownloadJob.class, jobKey, jobDescription, trigger, cronExpression, args);

		logger.debug(Literal.LEAVING);

	}

	private void disbursementProcessJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = DisbursementProcessJob.JOB_KEY;
		String jobDescription = DisbursementProcessJob.JOB_KEY_DESCRIPTION;
		String trigger = DisbursementProcessJob.JOB_TRIGGER;
		String cronExpression = DisbursementProcessJob.getCronExpression();

		JobDataMap args = new JobDataMap();
		args.put("disbursementRequestService", disbursementRequestService);

		registerJob(DisbursementProcessJob.class, jobKey, jobDescription, trigger, cronExpression, args);

		logger.debug(Literal.LEAVING);

	}

	private void registerMandateAutoDownloadJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = AutoMandateDownloadJob.JOB_KEY;
		String jobDescription = AutoMandateDownloadJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoMandateDownloadJob.JOB_TRIGGER;
		String cronExpression = AutoMandateDownloadJob.getCronExpression();
		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("externalInterfaceServiceTarget", externalInterfaceService);

		registerJob(AutoMandateDownloadJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);
		logger.debug(Literal.LEAVING);

	}

	private void registerMandateAutoUploadJob() {
		logger.debug(Literal.ENTERING);
		String jobKey = AutoMandateUploadJob.JOB_KEY;
		String jobDescription = AutoMandateUploadJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoMandateUploadJob.JOB_TRIGGER;
		String cronExpression = AutoMandateUploadJob.getCronExpression();
		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("mandateProcesses", getMandateProcess());
		dataMap.put("job", "MANDATES_IMPORT");

		registerJob(AutoMandateUploadJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);
		logger.debug(Literal.LEAVING);

	}

	private void registerMandateAutoAcknowledgeJob() {
		logger.debug(Literal.ENTERING);
		String jobKey = AutoMandateAcknowledgeJob.JOB_KEY;
		String jobDescription = AutoMandateAcknowledgeJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoMandateAcknowledgeJob.JOB_TRIGGER;
		String cronExpression = AutoMandateAcknowledgeJob.getCronExpression();
		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("mandateProcesses", getMandateProcess());
		dataMap.put("job", "MANDATES_ACK");

		registerJob(AutoMandateAcknowledgeJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);
		logger.debug(Literal.LEAVING);
	}

	private void registerCustomerPortalJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = CustomerExtractJob.JOB_KEY;
		String jobDescription = CustomerExtractJob.JOB_KEY_DESCRIPTION;
		String trigger = CustomerExtractJob.JOB_TRIGGER;
		String cronExpression = CustomerExtractJob.getCronExpression();

		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("extractCustomerData", extractCustomerData);

		registerJob(CustomerExtractJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);

		logger.debug(Literal.LEAVING);
	}

	private void registerPresentmentAutoExtractJob() {
		logger.debug(Literal.ENTERING);
		String jobKey = AutoExtractPresentmentJob.JOB_KEY;
		String jobDescription = AutoExtractPresentmentJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoExtractPresentmentJob.JOB_TRIGGER;
		String cronExpression = AutoExtractPresentmentJob.getCronExpression();
		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("presentmentJobService", getPresentmentJobService());

		registerJob(AutoExtractPresentmentJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);
		logger.debug(Literal.LEAVING);

	}

	private void registerPresentmentNachAutoUploadJob() {
		logger.debug(Literal.ENTERING);
		String jobKey = AutoUploadPresentmentJob.JOB_KEY;
		String jobDescription = AutoUploadPresentmentJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoUploadPresentmentJob.JOB_TRIGGER;
		String cronExpression = AutoUploadPresentmentJob.getCronExpression();
		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("presentmentJobService", getPresentmentJobService());
		dataMap.put("job", "PRESENTMENT_RESPONSE_NACH");

		registerJob(AutoUploadPresentmentJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);
		logger.debug(Literal.LEAVING);
	}

	private void registerPresentmentPdcAutoUploadJob() {
		logger.debug(Literal.ENTERING);
		String jobKey = AutoUploadPdcPresentmentJob.JOB_KEY;
		String jobDescription = AutoUploadPdcPresentmentJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoUploadPdcPresentmentJob.JOB_TRIGGER;
		String cronExpression = AutoUploadPdcPresentmentJob.getCronExpression();
		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("presentmentJobService", getPresentmentJobService());
		dataMap.put("job", "PRESENTMENT_RESPONSE_PDC");

		registerJob(AutoUploadPdcPresentmentJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);
		logger.debug(Literal.LEAVING);
	}

	private void registerAutoEODJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = AutoEODJob.JOB_KEY;
		String jobDescription = AutoEODJob.JOB_KEY_DESCRIPTION;
		String trigger = AutoEODJob.JOB_TRIGGER;
		String cronExpression = eodService.getCronExpression();

		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("eodService", eodService);

		registerJob(AutoEODJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);

		logger.debug(Literal.LEAVING);
	}

	private void registerEODReminderJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = EODReminderJob.JOB_KEY;
		String jobDescription = EODReminderJob.JOB_KEY_DESCRIPTION;
		String trigger = EODReminderJob.JOB_TRIGGER;
		String cronExpression = eodService.getReminderCronExp();

		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("eodService", eodService);

		registerJob(EODReminderJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);

		logger.debug(Literal.LEAVING);
	}

	private void registerEODDelayJob() {
		logger.debug(Literal.ENTERING);

		String jobKey = EODDelayJob.JOB_KEY;
		String jobDescription = EODDelayJob.JOB_KEY_DESCRIPTION;
		String trigger = EODDelayJob.JOB_TRIGGER;
		String cronExpression = eodService.getDelayCronExp();

		try {
			CronExpression.validateExpression(cronExpression);
		} catch (Exception e) {
			return;
		}

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("eodService", eodService);

		registerJob(EODDelayJob.class, jobKey, jobDescription, trigger, cronExpression, dataMap);

		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void registerJob(Class jobClass, String jobKey, String jobDescription, String trigger,
			String cronExpression, JobDataMap args) {
		Job job = new Job();
		job.setJobDetail(JobBuilder.newJob(jobClass).withIdentity(jobKey, jobKey).withDescription(jobDescription)
				.setJobData(args).build());
		job.setTrigger(TriggerBuilder.newTrigger().withIdentity(trigger, trigger).withDescription(jobDescription)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build());

		jobs.put(jobKey, job);
	}

	public void setDisbursementRequestService(DisbursementRequestService disbursementRequestService) {
		this.disbursementRequestService = disbursementRequestService;
	}

	private DisbursementResponse getDisbursementResponse() {
		return disbursementResponse == null ? defaultDisbursementResponse : disbursementResponse;
	}

	@Autowired
	@Qualifier(value = "defaultDisbursementResponse")
	public void setDefaultDisbursementResponse(DisbursementResponse defaultDisbursementResponse) {
		this.defaultDisbursementResponse = defaultDisbursementResponse;
	}

	@Autowired(required = false)
	@Qualifier(value = "disbursementResponse")
	public void setDisbursementResponse(DisbursementResponse disbursementResponse) {
		this.disbursementResponse = disbursementResponse;
	}

	@Autowired(required = false)
	@Qualifier(value = "mandateProcesses")
	public void setMandateProces(MandateProcesses mandateProcesses) {
		this.mandateProcesses = mandateProcesses;
	}

	@Autowired
	public void setDefaultMandateProcess(MandateProcesses defaultMandateProcess) {
		this.defaultMandateProcess = defaultMandateProcess;
	}

	private MandateProcesses getMandateProcess() {
		return mandateProcesses == null ? defaultMandateProcess : mandateProcesses;
	}

	@Autowired
	public void setExternalInterfaceService(ExternalInterfaceService externalInterfaceService) {
		this.externalInterfaceService = externalInterfaceService;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

	@Autowired(required = false)
	public void setExtractCustomerData(ExtractCustomerData extractCustomerData) {
		this.extractCustomerData = extractCustomerData;
	}

	public PresentmentJobService getPresentmentJobService() {
		return presentmentJobService;
	}

	@Autowired
	public void setPresentmentJobService(PresentmentJobService presentmentJobService) {
		this.presentmentJobService = presentmentJobService;
	}

	@Autowired
	public void setEodService(EODService eodService) {
		this.eodService = eodService;
	}

}