package com.pennanttech.pff.scheduler.jobs;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.service.finance.covenant.impl.CovenantAlerts;
import com.pennant.backend.service.finance.covenant.impl.PutCallAlerts;
import com.pennant.backend.service.finance.lmsservicelog.impl.LMSServiceLogAlerts;
import com.pennant.backend.service.financemanagement.impl.PresentmentJobService;
import com.pennanttech.pennapps.core.job.JobDataAccess;
import com.pennanttech.pennapps.core.job.scheduler.JobData;
import com.pennanttech.pennapps.core.job.scheduler.JobScheduler;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.notification.email.EmailEngine;
import com.pennanttech.pennapps.notification.email.EmailEngineJob;
import com.pennanttech.pennapps.notification.email.EmailNotificationService;
import com.pennanttech.pennapps.notification.sms.SmsEngine;
import com.pennanttech.pennapps.notification.sms.SmsEngineJob;
import com.pennanttech.pennapps.notification.sms.SmsNotificationService;
import com.pennanttech.pff.cashback.CashBackDBDProcess;
import com.pennanttech.pff.core.account.engine.AccountUpdater;
import com.pennanttech.pff.core.account.job.AccountUpdaterJob;
import com.pennanttech.pff.eod.EODService;
import com.pennanttech.pff.external.DisbursementResponse;
import com.pennanttech.pff.external.MandateProcesses;
import com.pennanttech.pff.external.PmayProcess;
import com.pennanttech.pff.external.disbursement.DisbursementRequestService;
import com.pennanttech.pff.external.gst.GSTInvoiceGeneratorService;
import com.pennanttech.pff.external.service.ExternalInterfaceService;
import com.pennanttech.pff.notifications.service.InvokeSysNotifications;
import com.pennanttech.pff.notifications.service.ProcessSystemNotifications;
import com.pennanttech.pff.process.ExtractCustomerData;

public class PFFJobScheduler extends JobScheduler {
	private AccountUpdater accountUpdater;
	private EmailNotificationService emailNotificationService;
	private EmailEngine emailEngine;
	private SmsNotificationService smsNotificationService;
	private SmsEngine smsEngine;
	private DisbursementRequestService disbursementRequestService;
	private GSTInvoiceGeneratorService defaultGstInvoiceGenerator;
	private InvokeSysNotifications invokeSysNotifications;
	private ProcessSystemNotifications processSystemNotifications;
	private ReceiptUploadHeaderService receiptUploadHeaderService;
	private CovenantAlerts covenantAlerts;
	private PutCallAlerts putCallAlerts;
	private LMSServiceLogAlerts lmsServiceLogAlerts;
	private SecurityUserDAO securityUserDAO;
	private CashBackDBDProcess cashBackDBDProcess;
	private DMSService dMSService;
	private ExternalInterfaceService externalInterfaceService;
	private MandateProcesses defaultMandateProcess;
	private PresentmentJobService presentmentJobService;
	private ExtractCustomerData extractCustomerData;
	private EODService eodService;
	private NonLanReceiptService nonLanReceiptService;
	private GSTInvoiceGeneratorService gstInvoiceGenerator;
	private DisbursementResponse defaultDisbursementResponse;
	private DisbursementResponse disbursementResponse;
	private MandateProcesses mandateProcesses;
	private PmayProcess pmayProcess;

	@Override
	protected List<JobData> loadJobs() {
		List<JobData> jobDataList = new ArrayList<>();

		/**
		 * 1. ACCOUNT_UPDATER_JOB
		 */
		JobDataMap args = new JobDataMap();
		args.put("accountUpdater", accountUpdater);

		JobData jobData = new JobData("ACCOUNT_UPDATER_JOB", AccountUpdaterJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 2. NOTIFICATION_EMAIL_JOB
		 */
		args = new JobDataMap();
		args.put("emailNotificationService", emailNotificationService);
		args.put("emailEngine", emailEngine);

		jobData = new JobData("NOTIFICATION_EMAIL_JOB", EmailEngineJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 3. NOTIFICATION_SMS_JOB
		 */
		if (smsNotificationService != null) {
			args = new JobDataMap();
			args.put("smsNotificationService", smsNotificationService);
			args.put("smsEngine", smsEngine);

			jobData = new JobData("NOTIFICATION_SMS_JOB", SmsEngineJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 4. DISBURSEMENT_PROCESS_JOB
		 */
		args = new JobDataMap();
		args.put("disbursementRequestService", disbursementRequestService);

		jobData = new JobData("DISBURSEMENT_PROCESS_JOB", DisbursementProcessJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 5. GST_INVOICE_GENERATE_JOB
		 */
		args = new JobDataMap();
		args.put("gstInvoiceGenerator", gstInvoiceGenerator);
		args.put("defaultGstInvoiceGenerator", defaultGstInvoiceGenerator);

		jobData = new JobData("GST_INVOICE_GENERATE_JOB", GSTInvoiceGeneratorJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 6. SYS_NOTIFICATIONS_INVOKE_JOB
		 */
		if (invokeSysNotifications != null) {
			args = new JobDataMap();
			args.put("sysNotificationsService", invokeSysNotifications);

			jobData = new JobData("SYS_NOTIFICATIONS_INVOKE_JOB", SystemNotificationsInvokeJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 7. SYS_NOTIFICATIONS_PROCESS_JOB
		 */
		if (processSystemNotifications != null) {
			args = new JobDataMap();
			args.put("processSystemNotifications", processSystemNotifications);

			jobData = new JobData("SYS_NOTIFICATIONS_PROCESS_JOB", SystemNotificationsProcessJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 8. AUTO_RECPT_RESPONSE_JOB
		 */
		args = new JobDataMap();
		args.put("receiptUploadHeaderService", receiptUploadHeaderService);

		jobData = new JobData("AUTO_RECPT_RESPONSE_JOB", ReceiptResponseJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 9. COVENANT_ALERTS_JOB
		 */
		args = new JobDataMap();
		args.put("covenantAlerts", covenantAlerts);

		jobData = new JobData("COVENANT_ALERTS_JOB", CovenantAlertsJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 10. COVENANT_ALERTS_JOB
		 */
		args = new JobDataMap();
		args.put("putCallAlerts", putCallAlerts);

		jobData = new JobData("FIN_PUT_CALL_ALERTS_JOB", FinPutCallAlertsJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 11. LMS_SERVICE_LOG_ALERTS_JOB
		 */
		args = new JobDataMap();
		args.put("lmsServiceLogAlerts", lmsServiceLogAlerts);

		jobData = new JobData("LMS_SERVICE_LOG_ALERTS_JOB", LMSServiceLogAlertsJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 12. USER_AUTO_LOCKING_JOB
		 */
		args = new JobDataMap();
		args.put("securityUserDAO", securityUserDAO);
		jobData = new JobData("USER_AUTO_LOCKING_JOB", SecurityUserAccountLockJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 13. CASH_BACK_DBD_JOB
		 */
		args = new JobDataMap();
		args.put("cashBackDBDProcess", cashBackDBDProcess);

		jobData = new JobData("CASH_BACK_DBD_JOB", CashBackDBDJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 14. DMS_JOB
		 */
		args = new JobDataMap();
		args.put("dMSService", dMSService);

		jobData = new JobData("DMS_JOB", DMSJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 15. DISBURSEMENT_AUTO_DOWNLOAD_JOB
		 */
		if (ImplementationConstants.DISBURSEMENT_AUTO_DOWNLOAD) {
			args = new JobDataMap();
			args.put("disbursementRequestService", disbursementRequestService);

			jobData = new JobData("DISBURSEMENT_DOWNLOAD_JOB", AutoDisbursementDownloadJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 16. DISBURSEMENT_AUTO_UPLOAD_JOB
		 */
		if (ImplementationConstants.DISBURSEMENT_AUTO_UPLOAD) {
			args = new JobDataMap();
			args.put("disbursementResponse", getDisbursementResponse());

			jobData = new JobData("AUTO_DISBURSEMENT_UPLOAD_JOB", AutoDisbursementUploadJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 17. MANDATE_AUTO_DOWNLOAD_JOB
		 */
		if (ImplementationConstants.MANDATE_AUTO_DOWNLOAD) {
			args = new JobDataMap();
			args.put("externalInterfaceService", externalInterfaceService);

			jobData = new JobData("MANDATE_AUTO_DOWNLOAD_JOB", AutoMandateDownloadJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 18. MANDATE_AUTO_DOWNLOAD_JOB
		 */
		if (ImplementationConstants.MANDATE_AUTO_UPLOAD) {
			args = new JobDataMap();
			args.put("externalInterfaceService", getMandateProcess());
			args.put("job", "MANDATES_ACK");

			jobData = new JobData("MANDATE_AUTO_UPLOAD_JOB", AutoMandateUploadJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 19. MANDATE_AUTO_UPLOAD_ACK_JOB
		 */
		if (ImplementationConstants.MANDATE_AUTO_UPLOAD) {
			args = new JobDataMap();
			args.put("externalInterfaceService", getMandateProcess());
			args.put("job", "MANDATES_ACK");

			jobData = new JobData("MANDATE_AUTO_UPLOAD_ACK_JOB", AutoMandateAcknowledgeJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 20. PRESENTMENT_AUTO_EXTRACT_JOB
		 */
		if (ImplementationConstants.PRESENTMENT_AUTO_DOWNLOAD) {
			args = new JobDataMap();
			args.put("presentmentJobService", presentmentJobService);

			jobData = new JobData("PRESENTMENT_AUTO_EXTRACT_JOB", AutoExtractPresentmentJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 21. PRESENTMENT_NACH_AUTO_UPLOAD_JOB
		 */
		if (ImplementationConstants.PRESENTMENT_AUTO_UPLOAD) {
			args = new JobDataMap();
			args.put("presentmentJobService", presentmentJobService);
			args.put("job", "PRESENTMENT_RESPONSE_NACH");

			jobData = new JobData("PRESENTMENT_NACH_AUTO_UPLOAD_JOB", AutoUploadPresentmentJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 22. PRESENTMENT_PDC_AUTO_UPLOAD_JOB
		 */
		if (ImplementationConstants.PRESENTMENT_AUTO_UPLOAD) {
			args = new JobDataMap();
			args.put("presentmentJobService", presentmentJobService);
			args.put("job", "PRESENTMENT_RESPONSE_PDC");

			jobData = new JobData("PRESENTMENT_PDC_AUTO_UPLOAD_JOB", AutoUploadPdcPresentmentJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 23. PORTAL_CUSTOMER_EXTRACT_JOB
		 */
		args = new JobDataMap();
		args.put("extractCustomerData", extractCustomerData);
		jobData = new JobData("PORTAL_CUSTOMER_EXTRACT_JOB", CustomerExtractJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 24. AUTO_EOD_JOB
		 */
		if (ImplementationConstants.AUTO_EOD_REQUIRED) {
			args = new JobDataMap();
			args.put("eodService", eodService);
			jobData = new JobData("AUTO_EOD_JOB", AutoEODJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 25. EOD_REMINDER_JOB
		 */
		if (ImplementationConstants.AUTO_EOD_REQUIRED) {
			args = new JobDataMap();
			args.put("eodService", eodService);
			jobData = new JobData("EOD_REMINDER_JOB", EODReminderJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 26. EOD_DELAY_JOB
		 */
		if (ImplementationConstants.AUTO_EOD_REQUIRED) {
			args = new JobDataMap();
			args.put("eodService", eodService);
			jobData = new JobData("EOD_DELAY_JOB", EODDelayJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 27. MOB_AGENCY_RECEIPT_LIMIT_UPDATE_JOB
		 */
		args = new JobDataMap();
		args.put("nonLanReceiptService", nonLanReceiptService);
		jobData = new JobData("MOB_AGENCY_RECEIPT_LIMIT_UPDATE_JOB", MobAgencyReciptLimitUpdateJob.class, args);
		jobDataList.add(jobData);

		/**
		 * 28. PMAY_RESPONSE_JOB
		 */
		if (pmayProcess != null) {
			args = new JobDataMap();
			args.put("pmayProcess", pmayProcess);
			jobData = new JobData("PMAY_RESPONSE_JOB", PmayJob.class, args);
			jobDataList.add(jobData);
		}

		return jobDataList;
	}

	public void setJobDataAccess(JobDataAccess jobDataAccess) {
		this.jobDataAccess = jobDataAccess;
	}

	public void setAccountUpdater(AccountUpdater accountUpdater) {
		this.accountUpdater = accountUpdater;
	}

	public void setSmsEngine(SmsEngine smsEngine) {
		this.smsEngine = smsEngine;
	}

	public void setEmailEngine(EmailEngine emailEngine) {
		this.emailEngine = emailEngine;
	}

	public void setDisbursementRequestService(DisbursementRequestService disbursementRequestService) {
		this.disbursementRequestService = disbursementRequestService;
	}

	public void setDefaultGstInvoiceGenerator(GSTInvoiceGeneratorService defaultGstInvoiceGenerator) {
		this.defaultGstInvoiceGenerator = defaultGstInvoiceGenerator;
	}

	public void setInvokeSysNotifications(InvokeSysNotifications invokeSysNotifications) {
		this.invokeSysNotifications = invokeSysNotifications;
	}

	public void setProcessSystemNotifications(ProcessSystemNotifications processSystemNotifications) {
		this.processSystemNotifications = processSystemNotifications;
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

	public void setCovenantAlerts(CovenantAlerts covenantAlerts) {
		this.covenantAlerts = covenantAlerts;
	}

	public void setPutCallAlerts(PutCallAlerts putCallAlerts) {
		this.putCallAlerts = putCallAlerts;
	}

	public void setLmsServiceLogAlerts(LMSServiceLogAlerts lmsServiceLogAlerts) {
		this.lmsServiceLogAlerts = lmsServiceLogAlerts;
	}

	public void setSecurityUserDAO(SecurityUserDAO securityUserDAO) {
		this.securityUserDAO = securityUserDAO;
	}

	public void setCashBackDBDProcess(CashBackDBDProcess cashBackDBDProcess) {
		this.cashBackDBDProcess = cashBackDBDProcess;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

	public void setExternalInterfaceService(ExternalInterfaceService externalInterfaceService) {
		this.externalInterfaceService = externalInterfaceService;
	}

	public void setDefaultMandateProcess(MandateProcesses defaultMandateProcess) {
		this.defaultMandateProcess = defaultMandateProcess;
	}

	public void setNonLanReceiptService(NonLanReceiptService nonLanReceiptService) {
		this.nonLanReceiptService = nonLanReceiptService;
	}

	private DisbursementResponse getDisbursementResponse() {
		return disbursementResponse == null ? defaultDisbursementResponse : disbursementResponse;
	}

	private MandateProcesses getMandateProcess() {
		return mandateProcesses == null ? defaultMandateProcess : mandateProcesses;
	}

	public void setPresentmentJobService(PresentmentJobService presentmentJobService) {
		this.presentmentJobService = presentmentJobService;
	}

	public void setExtractCustomerData(ExtractCustomerData extractCustomerData) {
		this.extractCustomerData = extractCustomerData;
	}

	public void setEodService(EODService eodService) {
		this.eodService = eodService;
	}

	@Autowired(required = false)
	public void setSmsNotificationService(SmsNotificationService smsNotificationService) {
		this.smsNotificationService = smsNotificationService;
	}

	@Autowired(required = false)
	public void setEmailNotificationService(EmailNotificationService emailNotificationService) {
		this.emailNotificationService = emailNotificationService;
	}

	@Autowired(required = false)
	public void setGstInvoiceGenerator(GSTInvoiceGeneratorService gstInvoiceGenerator) {
		this.gstInvoiceGenerator = gstInvoiceGenerator;
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
	public void setMandateProcesses(MandateProcesses mandateProcesses) {
		this.mandateProcesses = mandateProcesses;
	}

	@Autowired(required = false)
	public void setPmayProcess(PmayProcess pmayProcess) {
		this.pmayProcess = pmayProcess;
	}

}
