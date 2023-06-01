package com.pennanttech.pff.scheduler.jobs;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.administration.SecurityUserDAO;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.service.finance.NonLanReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.service.finance.covenant.impl.CovenantAlerts;
import com.pennant.backend.service.finance.covenant.impl.PutCallAlerts;
import com.pennant.backend.service.finance.lmsservicelog.impl.LMSServiceLogAlerts;
import com.pennant.backend.service.financemanagement.impl.PresentmentJobService;
import com.pennant.backend.service.fincancelupload.impl.FinanceCancellationUploadJob;
import com.pennant.backend.upload.job.CrossLoanKnockOffJob;
import com.pennant.backend.upload.job.FeeWaiverUploadJob;
import com.pennant.backend.upload.job.PaymentInstJob;
import com.pennant.pff.branchchange.upload.job.BranchChangeUploadJob;
import com.pennant.pff.cheques.upload.job.ChequeUploadJob;
import com.pennant.pff.customer.upload.job.KycDetailUploadJob;
import com.pennant.pff.excess.upload.job.ExcessTransferUploadJob;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.holdmarking.upload.job.HoldMarkingUploadJob;
import com.pennant.pff.holdrefund.upload.job.HoldRefundJob;
import com.pennant.pff.hostglmapping.upload.job.HostGLMappingUploadJob;
import com.pennant.pff.lien.upload.job.LienUploadJob;
import com.pennant.pff.lpp.upload.job.LPPLoanTypeUploadJob;
import com.pennant.pff.lpp.upload.job.LPPLoanUploadJob;
import com.pennant.pff.mandate.upload.job.MandateUploadJob;
import com.pennant.pff.manualknockoff.upload.job.ManualKnockOffJob;
import com.pennant.pff.miscellaneouspostingupload.service.impl.MiscellaneousPostingUploadJob;
import com.pennant.pff.noc.upload.job.BlockAutoLetterGenerateUploadJob;
import com.pennant.pff.noc.upload.job.CourierDetailUploadJob;
import com.pennant.pff.noc.upload.job.LoanLetterUploadJob;
import com.pennant.pff.presentment.upload.job.FateCorrectionJob;
import com.pennant.pff.presentment.upload.job.RepresentmentJob;
import com.pennant.pff.receipt.upload.job.CreateReceiptUploadJob;
import com.pennant.pff.receipt.upload.job.LoanClosureUploadJob;
import com.pennant.pff.receipt.upload.job.ReceiptStatusUploadJob;
import com.pennant.pff.revwriteoffupload.upload.job.RevWriteOffUploadJob;
import com.pennant.pff.upload.service.UploadService;
import com.pennant.pff.writeoffupload.upload.job.WriteOffUploadJob;
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
import com.pennanttech.pff.provision.upload.job.ProvisionUploadJob;

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
	private SecurityUserService securityUserService;

	private UploadService excessTransferUploadService;
	private UploadService holdRefundUploadService;
	private UploadService rePresentmentUploadService;
	private UploadService mandateUploadService;
	private UploadService fateCorrectionUploadService;
	private UploadService paymentInstructionUploadService;
	private UploadService lPPUploadService;
	private UploadService crossLoanKnockOffUploadService;
	private UploadService manualKnockOffUploadService;
	private UploadService chequeUploadService;
	private UploadService kycDetailsUploadService;
	private UploadService hostGLMappingUploadService;
	private UploadService miscellaneouspostingUploadService;
	private UploadService financeCancellationUploadService;
	private UploadService bulkFeeWaiverUploadService;
	private UploadService lienUploadService;
	private UploadService createReceiptUploadService;
	private UploadService receiptStatusUploadService;
	private UploadService writeOffUploadService;
	private UploadService revWriteOffUploadService;
	private UploadService branchChangeUploadService;
	private UploadService courierDetailUploadService;
	private UploadService loanLetterUploadService;
	private UploadService blockAutoGenLetterUploadService;
	private UploadService loanClosureUploadService;
	private UploadService provisionUploadService;
	private UploadService holdMarkingUploadService;

	@Autowired(required = false)
	private JobSchedulerExtension jobSchedulerExtension;

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
		args.put("securityUserService", securityUserService);
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
		if (MandateExtension.AUTO_DOWNLOAD) {
			args = new JobDataMap();
			args.put("externalInterfaceService", externalInterfaceService);

			jobData = new JobData("MANDATE_AUTO_DOWNLOAD_JOB", AutoMandateDownloadJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 18. MANDATE_AUTO_UPLOAD_JOB
		 */
		if (MandateExtension.AUTO_UPLOAD) {
			args = new JobDataMap();
			args.put("mandateProcess", getMandateProcess());
			args.put("job", "MANDATES_IMPORT");

			jobData = new JobData("MANDATE_AUTO_UPLOAD_JOB", AutoMandateUploadJob.class, args);
			jobDataList.add(jobData);
		}

		/**
		 * 19. MANDATE_AUTO_UPLOAD_ACK_JOB
		 */
		if (MandateExtension.AUTO_UPLOAD) {
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

		/**
		 * 29. UPLOAD_RESPONSE_JOB
		 */

		args = new JobDataMap();
		args.put("excessTransferUploadService", excessTransferUploadService);
		jobData = new JobData("EXCESS_TRANSFER_JOB", ExcessTransferUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("holdRefundUploadService", holdRefundUploadService);
		jobData = new JobData("HOLD_REFUND_JOB", HoldRefundJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("rePresentmentUploadService", rePresentmentUploadService);
		jobData = new JobData("REPRESENTMENT_JOB", RepresentmentJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("mandateUploadService", mandateUploadService);
		jobData = new JobData("MANDATE_JOB", MandateUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("fateCorrectionUploadService", fateCorrectionUploadService);
		jobData = new JobData("FATE_CORRECTION_JOB", FateCorrectionJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("paymentInstructionUploadService", paymentInstructionUploadService);
		jobData = new JobData("PAY_INS_JOB", PaymentInstJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("crossLoanKnockOffUploadService", crossLoanKnockOffUploadService);
		jobData = new JobData("CROSSLOAN_KNOCKOFF_JOB", CrossLoanKnockOffJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("manualKnockOffUploadService", manualKnockOffUploadService);
		jobData = new JobData("MANUAL_KNOCKOFF_JOB", ManualKnockOffJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("chequeUploadService", chequeUploadService);
		jobData = new JobData("CHEQUE_JOB", ChequeUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("kycDetailsUploadService", kycDetailsUploadService);
		jobData = new JobData("KYC_DETAILS_JOB", KycDetailUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("hostGLMappingUploadService", hostGLMappingUploadService);
		jobData = new JobData("HOST_GL_DETAILS_JOB", HostGLMappingUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("miscellaneouspostingUploadService", miscellaneouspostingUploadService);
		jobData = new JobData("MIS_POST_DETAILS_JOB", MiscellaneousPostingUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("financeCancellationUploadService", financeCancellationUploadService);
		jobData = new JobData("FIN_CAN_DETAILS_JOB", FinanceCancellationUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("bulkFeeWaiverUploadService", bulkFeeWaiverUploadService);
		jobData = new JobData("WAIVER_DETAILS_JOB", FeeWaiverUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("lienUploadService", lienUploadService);
		jobData = new JobData("LIEN_DETAILS_JOB", LienUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("createReceiptUploadService", createReceiptUploadService);
		jobData = new JobData("CREATE_RECEIPT_DETAILS_JOB", CreateReceiptUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("receiptStatusUploadService", receiptStatusUploadService);
		jobData = new JobData("RECEIPT_STATUS_DETAILS_JOB", ReceiptStatusUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("writeOffUploadService", writeOffUploadService);
		jobData = new JobData("WRITE_OFF_DETAILS_JOB", WriteOffUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("revWriteOffUploadService", revWriteOffUploadService);
		jobData = new JobData("RE_WRITE_OFF_DETAILS_JOB", RevWriteOffUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("branchChangeUploadService", branchChangeUploadService);
		jobData = new JobData("BRANCH_DETAILS_JOB", BranchChangeUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("courierDetailUploadService", courierDetailUploadService);
		jobData = new JobData("COURIER_DETAILS_JOB", CourierDetailUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("loanLetterUploadService", loanLetterUploadService);
		jobData = new JobData("LOAN_LETTER_JOB", LoanLetterUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("blockAutoGenLetterUploadService", blockAutoGenLetterUploadService);
		jobData = new JobData("BLOCK_AUTO_GEN_LETTER_JOB", BlockAutoLetterGenerateUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("loanClosureUploadService", loanClosureUploadService);
		jobData = new JobData("LOAN_CLOSURE_JOB", LoanClosureUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("provisionUploadService", provisionUploadService);
		jobData = new JobData("PROVISION_JOB", ProvisionUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("lPPLoanUploadService", lPPUploadService);
		jobData = new JobData("LPP_LOAN_JOB", LPPLoanUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("lPPLoanTypeUploadService", lPPUploadService);
		jobData = new JobData("LPP_LOAN_TYPE_JOB", LPPLoanTypeUploadJob.class, args);
		jobDataList.add(jobData);

		args = new JobDataMap();
		args.put("holdMarkingUploadService", holdMarkingUploadService);
		jobData = new JobData("HOLD_MARKING_JOB", HoldMarkingUploadJob.class, args);
		jobDataList.add(jobData);
		/**
		 * For client specific jobs
		 */
		if (jobSchedulerExtension != null) {
			jobDataList.addAll(jobSchedulerExtension.loadJobs());
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

	@Autowired(required = false)
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

	@Autowired
	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

	@Autowired
	public void setExcessTransferUploadService(UploadService excessTransferUploadService) {
		this.excessTransferUploadService = excessTransferUploadService;
	}

	@Autowired
	public void setHoldRefundUploadService(UploadService holdRefundUploadService) {
		this.holdRefundUploadService = holdRefundUploadService;
	}

	@Autowired
	public void setRePresentmentUploadService(UploadService rePresentmentUploadService) {
		this.rePresentmentUploadService = rePresentmentUploadService;
	}

	@Autowired
	public void setMandateUploadService(UploadService mandateUploadService) {
		this.mandateUploadService = mandateUploadService;
	}

	@Autowired
	public void setFateCorrectionUploadService(UploadService fateCorrectionUploadService) {
		this.fateCorrectionUploadService = fateCorrectionUploadService;
	}

	@Autowired
	public void setPaymentInstructionUploadService(UploadService paymentInstructionUploadService) {
		this.paymentInstructionUploadService = paymentInstructionUploadService;
	}

	@Autowired
	public void setLPPUploadService(UploadService lPPUploadService) {
		this.lPPUploadService = lPPUploadService;
	}

	@Autowired
	public void setCrossLoanKnockOffUploadService(UploadService crossLoanKnockOffUploadService) {
		this.crossLoanKnockOffUploadService = crossLoanKnockOffUploadService;
	}

	@Autowired
	public void setManualKnockOffUploadService(UploadService manualKnockOffUploadService) {
		this.manualKnockOffUploadService = manualKnockOffUploadService;
	}

	@Autowired
	public void setChequeUploadService(UploadService chequeUploadService) {
		this.chequeUploadService = chequeUploadService;
	}

	@Autowired
	public void setKycDetailsUploadService(UploadService kycDetailsUploadService) {
		this.kycDetailsUploadService = kycDetailsUploadService;
	}

	@Autowired
	public void setHostGLMappingUploadService(UploadService hostGLMappingUploadService) {
		this.hostGLMappingUploadService = hostGLMappingUploadService;
	}

	@Autowired
	public void setMiscellaneouspostingUploadService(UploadService miscellaneouspostingUploadService) {
		this.miscellaneouspostingUploadService = miscellaneouspostingUploadService;
	}

	@Autowired
	public void setFinanceCancellationUploadService(UploadService financeCancellationUploadService) {
		this.financeCancellationUploadService = financeCancellationUploadService;
	}

	@Autowired
	public void setBulkFeeWaiverUploadService(UploadService bulkFeeWaiverUploadService) {
		this.bulkFeeWaiverUploadService = bulkFeeWaiverUploadService;
	}

	@Autowired
	public void setLienUploadService(UploadService lienUploadService) {
		this.lienUploadService = lienUploadService;
	}

	@Autowired
	public void setCreateReceiptUploadService(UploadService createReceiptUploadService) {
		this.createReceiptUploadService = createReceiptUploadService;
	}

	@Autowired
	public void setReceiptStatusUploadService(UploadService receiptStatusUploadService) {
		this.receiptStatusUploadService = receiptStatusUploadService;
	}

	@Autowired
	public void setWriteOffUploadService(UploadService writeOffUploadService) {
		this.writeOffUploadService = writeOffUploadService;
	}

	@Autowired
	public void setRevWriteOffUploadService(UploadService revWriteOffUploadService) {
		this.revWriteOffUploadService = revWriteOffUploadService;
	}

	@Autowired
	public void setBranchChangeUploadService(UploadService branchChangeUploadService) {
		this.branchChangeUploadService = branchChangeUploadService;
	}

	@Autowired
	public void setCourierDetailUploadService(UploadService courierDetailUploadService) {
		this.courierDetailUploadService = courierDetailUploadService;
	}

	@Autowired
	public void setLoanLetterUploadService(UploadService loanLetterUploadService) {
		this.loanLetterUploadService = loanLetterUploadService;
	}

	@Autowired
	public void setBlockAutoGenLetterUploadService(UploadService blockAutoGenLetterUploadService) {
		this.blockAutoGenLetterUploadService = blockAutoGenLetterUploadService;
	}

	@Autowired
	public void setLoanClosureUploadService(UploadService loanClosureUploadService) {
		this.loanClosureUploadService = loanClosureUploadService;
	}

	@Autowired
	public void setProvisionUploadService(UploadService provisionUploadService) {
		this.provisionUploadService = provisionUploadService;
	}

	@Autowired
	public void setHoldMarkingUploadService(UploadService holdMarkingUploadService) {
		this.holdMarkingUploadService = holdMarkingUploadService;
	}
}