package com.pennant.webui.batch.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.ProcessExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.endofday.main.BatchMonitor;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.policy.model.UserImpl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Batch/BatchAdmin.zul file.
 */
public class BatchAdminCtrl extends GFCBaseCtrl<Object> {
	private static final long		serialVersionUID	= 4309463490869641570L;
	private final static Logger		logger				= Logger.getLogger(BatchAdminCtrl.class);

	protected Window				window_BatchAdmin;
	protected Textbox				lable_LastBusiness_Date;
	protected Textbox				lable_NextBusiness_Date;
	protected Textbox				lable_Value_Date;
	protected Checkbox				lock;
	protected Timer					timer;
	protected Textbox				estimatedTime;
	protected Textbox				completedTime;
	protected Label					label_elapsed_Time;
	protected Hbox					batchStatus;
	protected Button				btnStartJob;
	protected Button				btnStaleJob;
	protected Label					lable_current_step;
	protected Borderlayout			borderLayoutBatchAdmin;

	protected Label					status				= new Label();

	String[]						args				= new String[1];
	private boolean					isInitialise		= false;
	private boolean					islock				= false;

	protected ProcessExecution		databaseBackupBeforeEod;
	protected ProcessExecution		dailyDownload;
	protected ProcessExecution		amortizationCalculation;
	protected ProcessExecution		amortizationPostings;
	protected ProcessExecution		uploadPftDetails;
	protected ProcessExecution		repayQueueCalculation;
	protected ProcessExecution		provisionCalculation;
	protected ProcessExecution		provisionPostings;
	protected ProcessExecution		depreciationPostings;
	protected ProcessExecution		dibursementPostings;
	protected ProcessExecution		collateralDeMarkPostings;
	protected ProcessExecution		ddaCancellationPostings;
	protected ProcessExecution		postLimitUtilization;
	protected ProcessExecution		postGLPLPostings;
	protected ProcessExecution		postBenchmarkInfo;
	protected ProcessExecution		postODDetails;
	protected ProcessExecution		ddaRepresentmentPostings;
	protected ProcessExecution		rateReviewCalculation;
	protected ProcessExecution		databaseBackupAfterEod;
	protected ProcessExecution		auditDataPurging;

	protected ProcessExecution		repayRequest;
	protected ProcessExecution		repayResponse;
	protected ProcessExecution		prepareRecoveryFile;
	protected ProcessExecution		readRecoveryFile;
	protected ProcessExecution		capitalizationPostings;
	protected ProcessExecution		overdueCalculation;
	protected ProcessExecution		suspenseCalculation;
	protected ProcessExecution		notification;
	protected ProcessExecution		statusUpdate;
	protected ProcessExecution		postNextPaymentDetails;
	protected ProcessExecution		postInstallmentDueSMS;
	protected ProcessExecution		postPastDueSMS;
	protected ProcessExecution		financeMovement;
	protected ProcessExecution		thirdPartyPostings;
	protected ProcessExecution		installmentDueDatePostings;
	protected ProcessExecution		postStudyAndDocFee;
	protected ProcessExecution		postFXRevaluation;
	protected ProcessExecution		archiveDocument;
	protected ProcessExecution		sasExtract;
	protected ProcessExecution		sendMail;
	protected ProcessExecution		snapShotPreparation;
	protected ProcessExecution		beforeEOD;

	Map<String, ExecutionStatus>	processMap			= new HashMap<String, ExecutionStatus>();
	private JobExecution			jobExecution;

	public enum PFSBatchProcessess {
		databaseBackupBeforEod,
		dailyDownload,
		amortizationCalculation,
		amortizationPostings,
		uploadPftDetails,
		repayQueueCalculation,
		repayRequest,
		prepareRecoveryFile,
		readRecoveryFile,
		repayResponse,
		provisionCalculation,
		provisionPostings,
		depreciationPostings,
		dibursementPostings,
		collateralDeMarkPostings,
		ddaCancellationPostings,
		postLimitUtilization,
		postGLPLPostings,
		postBenchmarkInfo,
		postODDetails,
		ddaRepresentmentPostings,
		rateReviewCalculation,
		limitDecision,
		databaseBackupAfterEod,
		auditDataPurging,
		overdueCalculation,
		notification,
		statusUpdate,
		postNextPaymentDetails,
		postStudyAndDocFee,
		postFXRevaluation,
		postInstallmentDueSMS,
		postPastDueSMS,
		financeMovement,
		thirdPartyPostings,
		installmentDueDatePostings,
		beforeEOD,
		suspenseCalculation,
		archiveDocument,
		sasExtract,
		sendMail,
		snapShotPreparation,
		capitalizationPostings
	}

	public BatchAdminCtrl() {
		super();
		BatchMonitor.getInstance();
	}

	public void onCreate$window_BatchAdmin(Event event) throws Exception {
		// databaseBackupBeforEod.setVisible(false);

		if (!isInitialise) {
			setDates();
			this.timer.setDelay(SysParamUtil.getValueAsInt("EOD_BATCH_REFRESH_TIME"));
			this.borderLayoutBatchAdmin.setHeight(getBorderLayoutHeight());
			isInitialise = true;
		}

		this.jobExecution = BatchMonitor.getJobExecution();

		if (this.jobExecution != null) {
			if (this.jobExecution.isRunning()) {

				if (!this.timer.isRunning()) {
					this.timer.start();
				}

				this.btnStartJob.setDisabled(true);
				this.btnStaleJob.setDisabled(false);

				label_elapsed_Time.setValue("Processing Time");
				completedTime.setStyle("color:#FF4500;font-weight: bold; font-size:12px;");

				estimatedTime.setStyle("color:#FF4500;font-weight: bold; font-size:12px;");
				status.setStyle("color:#FF4500;font-weight: bold; font-size:12px;");
				this.lock.setDisabled(false);

				// Reset the dates
				setDates();

			} else {
				this.btnStartJob.setDisabled(false);
				this.btnStaleJob.setDisabled(true);

				label_elapsed_Time.setValue("Completed Time");
				completedTime.setStyle("");
				estimatedTime.setStyle("");
				status.setStyle("");

				if ("STARTED".equals(this.jobExecution.getStatus().toString())) {
					this.btnStartJob.setDisabled(true);
					if (this.jobExecution.isRunning()) {
						this.btnStaleJob.setDisabled(false);
					}
				}

				this.timer.stop();
				this.lock.setDisabled(true);
				BatchUtil.EXECUTING = null;
				PFSBatchAdmin.destroy();
			}

			status.setValue(this.jobExecution.getStatus().toString());
			if (this.batchStatus.getChildren() != null) {
				this.batchStatus.getChildren().clear();
			}

			this.batchStatus.appendChild(status);
			setRunningStatus();

		}

		if (this.btnStartJob.isDisabled()) {
			this.btnStaleJob.setDisabled(false);
		} else {
			this.btnStaleJob.setDisabled(true);
		}

	}

	private void setDates() {
		lable_Value_Date.setValue(DateUtility.getValueDate(DateFormat.LONG_DATE));
		lable_NextBusiness_Date.setValue(DateUtility.formatToLongDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT)));
		lable_LastBusiness_Date.setValue(DateUtility.formatToLongDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_LAST)));
	}

	private void setRunningStatus() {
		String jobStatus = this.jobExecution.getStatus().toString();

		try {
			if ("Y".equals(SysParamUtil.getValueAsString("EOD_BATCH_MONITOR"))) {
				if (!islock) {
					doFillStepExecutions(BatchMonitor.getStepExecution(this.jobExecution.getJobInstance()));
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		completedTime.setValue(BatchMonitor.getProcessingTime());
		estimatedTime.setValue(BatchMonitor.getEstimateTime());

		if ("FAILED".equals(jobStatus) || "STOPPED".equals(jobStatus)) {
			status.setStyle("color:red;");
			this.btnStartJob.setLabel("Restart");
			this.btnStartJob.setTooltiptext("Restart Job");
			estimatedTime.setValue("");
		}
		if ("COMPLETED".equals(jobStatus)) {
			this.btnStartJob.setLabel("Start");
			this.btnStartJob.setTooltiptext("Start");
			this.lable_current_step.setValue("");
			estimatedTime.setValue("");
			setDates();

			String jobStatusMsg = "";

			jobStatusMsg = jobExecution.getExecutionContext().get("DBBACKUP_STATUS") == null ? "" : (String) jobExecution.getExecutionContext().get("DBBACKUP_STATUS");
			StringBuilder clientNotifications = new StringBuilder();
			if (StringUtils.isNotEmpty(jobStatusMsg)) {
				clientNotifications.append("Data Base Back After EOD is Failed");
				clientNotifications.append(" : " + jobStatusMsg);
				logger.warn(clientNotifications.toString());
			}

			jobStatusMsg = jobExecution.getExecutionContext().get("AUDITPURGING_STATUS") == null ? "" : (String) jobExecution.getExecutionContext().get("AUDITPURGING_STATUS");
			if (StringUtils.isNotEmpty(jobStatusMsg)) {
				if (StringUtils.isNotEmpty(clientNotifications.toString())) {
					clientNotifications.append("/n");
				}
				clientNotifications.append("Audit Data Purging Failed");
				clientNotifications.append(" : " + jobStatusMsg);
				logger.warn(clientNotifications.toString());
			}

			if (StringUtils.isNotEmpty(clientNotifications.toString())) {
				Clients.showNotification(clientNotifications.toString(), "info", null, null, -1);
			}

			BatchMonitor.jobExecutionId = 0;
		}
	}

	public void onClick$btnStartJob(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		MultiLineMessageBox.doSetTemplate();
		int conf = 0;

		String loggedInUsers = getLoggedInUsers();
		if (StringUtils.isNotEmpty(loggedInUsers)) {
			loggedInUsers = "\n" + loggedInUsers;
			Clients.showNotification(Labels.getLabel("label_current_logged_users", new String[] { loggedInUsers }), "info", null, null, -1);
			return;
		}

		String msg = "";
		if ("Start".equals(this.btnStartJob.getLabel())) {
			args[0] = DateUtility.formatToShortDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT));
			msg = Labels.getLabel("labe_start_job", args);
		} else {
			msg = Labels.getLabel("labe_reStart_job");
		}

		conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Information"), MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			closeOtherTabs();
			PFSBatchAdmin.getInstance();

			this.btnStartJob.setDisabled(true);
			BatchMonitor.jobExecutionId = 0;
			BatchMonitor.avgTime = 0;
			this.processMap.clear();

			if ("Start".equals(this.btnStartJob.getLabel())) {
				args[0] = DateUtility.formatToShortDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT));
				PFSBatchAdmin.setArgs(args);
				PFSBatchAdmin.setRunType("START");
				resetPanels();
			} else {
				args[0] = this.jobExecution.getJobParameters().getString("Date");
				PFSBatchAdmin.setArgs(args);
			}

			try {

				Thread thread = new Thread(new EODJob());
				thread.start();
				Thread.sleep(1000);

			} catch (Exception e) {
				timer.stop();
				MessageUtil.showErrorMessage(e.getMessage());
				logger.error("Exception: ", e);
			}
		}

		// Event for Recreation Of Window
		Events.postEvent("onCreate", this.window_BatchAdmin, event);

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnStaleJob(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		PFSBatchAdmin.getInstance();
		String msg = Labels.getLabel("labe_terminate_job");
		MultiLineMessageBox.doSetTemplate();
		int conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Information"), MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			try {
				args[0] = this.jobExecution.getJobParameters().getString("Date");
				resetPanels();
				PFSBatchAdmin.setArgs(args);
				PFSBatchAdmin.resetStaleJob(this.jobExecution);
				BatchMonitor.jobExecutionId = 0;
				BatchMonitor.avgTime = 0;
				Events.postEvent("onCreate", this.window_BatchAdmin, event);
			} catch (Exception e) {
				logger.error("Exception: ", e);
				MessageUtil.showErrorMessage(e.getMessage());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onTimer$timer(Event event) {
		Events.postEvent("onCreate", this.window_BatchAdmin, event);
	}

	/**
	 * Method for Rendering Step Execution Details List
	 * 
	 * @param stepExecution
	 * @throws Exception
	 */

	private void doFillStepExecutions(List<StepExecution> stepExecutionList) throws Exception {
		ExecutionStatus exeStatus = null;
		if (this.jobExecution != null) {
			if (stepExecutionList != null && !stepExecutionList.isEmpty()) {

				// Collections.reverse(stepExecutionList);
				for (StepExecution stepExecution : stepExecutionList) {

					if ("EXECUTING".equals(stepExecution.getExitStatus().getExitCode())) {
						exeStatus = BatchUtil.EXECUTING;
					} else {
						exeStatus = new ExecutionStatus();
						exeStatus = copyDetails(stepExecution, exeStatus);
					}

					processMap.put(stepExecution.getStepName(), exeStatus);

					if (this.jobExecution.getId().equals(stepExecution.getJobExecutionId())) {
						if ("FAILED".equals(stepExecution.getExitStatus().getExitCode())) {
							this.btnStartJob.setLabel("Restart");
							this.btnStartJob.setTooltip("Restart");

							if (this.batchStatus.getChildren() != null) {
								this.batchStatus.getChildren().clear();
							}

							status.setValue(this.jobExecution.getStatus().toString());
							status.setStyle("color:red;");
							batchStatus.appendChild(status);
							batchStatus.appendChild(new Space());

							Image imgFail = new Image("/images/icons/ErrorFile.png");
							imgFail.setStyle("cursor:hand;cursor:pointer");
							ComponentsCtrl.applyForward(imgFail, "onClick = onClickError");

							batchStatus.setAttribute("data", stepExecution);
							batchStatus.appendChild(new Space());
							batchStatus.appendChild(imgFail);

							this.lable_current_step.setValue("");
						}
					}
				}

				for (String stepName : processMap.keySet()) {
					renderPanels(stepName);
				}

			}

		}
	}

	/**
	 * This method render the Defined Executions for which value date matched
	 * with Current value date
	 * 
	 * @param ExecutionStatus
	 *            (status)
	 */
	private void renderPanels(String stepName) {
		PFSBatchProcessess processName;

		try {
			processName = PFSBatchProcessess.valueOf(stepName);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}

		ExecutionStatus status = processMap.get(stepName);
		if (status != null) {

			switch (processName) {
			case databaseBackupBeforEod:
				this.databaseBackupBeforeEod.setProcess(status);
				this.databaseBackupBeforeEod.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.databaseBackupBeforeEod);
				}
				break;
			case dailyDownload:
				this.dailyDownload.setProcess(status);
				this.dailyDownload.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.dailyDownload);
				}
				break;
			case repayQueueCalculation:
				this.repayQueueCalculation.setProcess(status);
				this.repayQueueCalculation.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.repayQueueCalculation);
				}
				break;
			case repayRequest:
				this.repayRequest.setProcess(status);
				this.repayRequest.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.repayRequest);
				}
				break;
			case prepareRecoveryFile:
				this.prepareRecoveryFile.setProcess(status);
				this.prepareRecoveryFile.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.prepareRecoveryFile);
				}
				break;
			case readRecoveryFile:
				this.readRecoveryFile.setProcess(status);
				this.readRecoveryFile.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.readRecoveryFile);
				}
				break;
			case repayResponse:
				this.repayResponse.setProcess(status);
				this.repayResponse.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.repayResponse);
				}
				break;
			case amortizationCalculation:
				this.amortizationCalculation.setProcess(status);
				this.amortizationCalculation.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.amortizationCalculation);
				}
				break;
			case amortizationPostings:
				this.amortizationPostings.setProcess(status);
				this.amortizationPostings.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.amortizationPostings);
				}
				break;
			case uploadPftDetails:
				this.uploadPftDetails.setProcess(status);
				this.uploadPftDetails.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.uploadPftDetails);
				}
				break;
			case provisionCalculation:
				this.provisionCalculation.setProcess(status);
				this.provisionCalculation.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.provisionCalculation);
				}
				break;
			case provisionPostings:
				this.provisionPostings.setProcess(status);
				this.provisionPostings.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.provisionPostings);
				}
				break;
			case depreciationPostings:
				this.depreciationPostings.setProcess(status);
				this.depreciationPostings.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.depreciationPostings);
				}
				break;
			case dibursementPostings:
				this.dibursementPostings.setProcess(status);
				this.dibursementPostings.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.dibursementPostings);
				}
				break;
			case collateralDeMarkPostings:
				this.collateralDeMarkPostings.setProcess(status);
				this.collateralDeMarkPostings.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.collateralDeMarkPostings);
				}
				break;
			case ddaCancellationPostings:
				this.ddaCancellationPostings.setProcess(status);
				this.ddaCancellationPostings.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.ddaCancellationPostings);
				}
				break;
			case postLimitUtilization:
				this.postLimitUtilization.setProcess(status);
				this.postLimitUtilization.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postLimitUtilization);
				}
				break;
			case postGLPLPostings:
				this.postGLPLPostings.setProcess(status);
				this.postGLPLPostings.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postGLPLPostings);
				}
				break;
			case ddaRepresentmentPostings:
				this.ddaRepresentmentPostings.setProcess(status);
				this.ddaRepresentmentPostings.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.ddaRepresentmentPostings);
				}
				break;
			case rateReviewCalculation:
				this.rateReviewCalculation.setProcess(status);
				this.rateReviewCalculation.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.rateReviewCalculation);
				}
				break;
			case limitDecision:
				if ("COMPLETED".equals(status.getStatus())) {
					this.timer.stop();
					setDates();
				}
				break;
			case databaseBackupAfterEod:
				this.databaseBackupAfterEod.setProcess(status);
				this.databaseBackupAfterEod.render();
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.databaseBackupAfterEod);
				}
				break;
			case auditDataPurging:
				this.auditDataPurging.setProcess(status);
				this.auditDataPurging.render();
				setRunningProcess(this.auditDataPurging);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.auditDataPurging);
				}
				break;
			case capitalizationPostings:
				this.capitalizationPostings.setProcess(status);
				this.capitalizationPostings.render();
				setRunningProcess(this.capitalizationPostings);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.capitalizationPostings);
				}
				break;
			case overdueCalculation:
				this.overdueCalculation.setProcess(status);
				this.overdueCalculation.render();
				setRunningProcess(this.overdueCalculation);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.overdueCalculation);
				}
				break;
			case suspenseCalculation:
				this.suspenseCalculation.setProcess(status);
				this.suspenseCalculation.render();
				setRunningProcess(this.suspenseCalculation);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.suspenseCalculation);
				}
				break;
			case notification:
				this.notification.setProcess(status);
				this.notification.render();
				setRunningProcess(this.notification);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.notification);
				}
				break;
			case postBenchmarkInfo:
				this.postBenchmarkInfo.setProcess(status);
				this.postBenchmarkInfo.render();
				setRunningProcess(this.postBenchmarkInfo);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postBenchmarkInfo);
				}
				break;
			case postODDetails:
				this.postODDetails.setProcess(status);
				this.postODDetails.render();
				setRunningProcess(this.postODDetails);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postODDetails);
				}
				break;
			case statusUpdate:
				this.statusUpdate.setProcess(status);
				this.statusUpdate.render();
				setRunningProcess(this.statusUpdate);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.statusUpdate);
				}
				break;

			case postNextPaymentDetails:
				this.postNextPaymentDetails.setProcess(status);
				this.postNextPaymentDetails.render();
				setRunningProcess(this.postNextPaymentDetails);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postNextPaymentDetails);
				}
				break;
			case postStudyAndDocFee:
				this.postStudyAndDocFee.setProcess(status);
				this.postStudyAndDocFee.render();
				setRunningProcess(this.postStudyAndDocFee);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postStudyAndDocFee);
				}
				break;
			case postFXRevaluation:
				this.postFXRevaluation.setProcess(status);
				this.postFXRevaluation.render();
				setRunningProcess(this.postFXRevaluation);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postFXRevaluation);
				}
				break;
			case postInstallmentDueSMS:
				this.postInstallmentDueSMS.setProcess(status);
				this.postInstallmentDueSMS.render();
				setRunningProcess(this.postInstallmentDueSMS);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postInstallmentDueSMS);
				}
				break;
			case postPastDueSMS:
				this.postPastDueSMS.setProcess(status);
				this.postPastDueSMS.render();
				setRunningProcess(this.postPastDueSMS);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.postPastDueSMS);
				}
				break;
			case financeMovement:
				this.financeMovement.setProcess(status);
				this.financeMovement.render();
				setRunningProcess(this.financeMovement);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.financeMovement);
				}
				break;
			case thirdPartyPostings:
				this.thirdPartyPostings.setProcess(status);
				this.thirdPartyPostings.render();
				setRunningProcess(this.thirdPartyPostings);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.thirdPartyPostings);
				}
				break;
			case installmentDueDatePostings:
				this.installmentDueDatePostings.setProcess(status);
				this.installmentDueDatePostings.render();
				setRunningProcess(this.installmentDueDatePostings);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.installmentDueDatePostings);
				}
				break;
			case archiveDocument:
				this.archiveDocument.setProcess(status);
				this.archiveDocument.render();
				setRunningProcess(this.archiveDocument);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.archiveDocument);
				}
				break;
			case sasExtract:
				this.sasExtract.setProcess(status);
				this.sasExtract.render();
				setRunningProcess(this.sasExtract);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.sasExtract);
				}
				break;
			case sendMail:
				this.sendMail.setProcess(status);
				this.sendMail.render();
				setRunningProcess(this.sendMail);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.sendMail);
				}
				break;
			case snapShotPreparation:
				this.snapShotPreparation.setProcess(status);
				this.snapShotPreparation.render();
				setRunningProcess(this.snapShotPreparation);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.snapShotPreparation);
				}
				break;
			case beforeEOD:
				this.beforeEOD.setProcess(status);
				this.beforeEOD.render();
				setRunningProcess(this.beforeEOD);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.beforeEOD);
				}
				break;

			}
		}

		status = null;
	}

	private void setRunningProcess(ProcessExecution panel) {
		if (panel.getProgress() == null || "null".equalsIgnoreCase(panel.getProgress().trim())) {
			lable_current_step.setValue(panel.getTitle() + "	...");
		} else {
			lable_current_step.setValue(panel.getTitle() + panel.getProgress());
		}

	}

	private void resetPanels() {

		if (dailyDownload.getChildren() != null) {
			dailyDownload.getChildren().clear();
		}
		if (repayQueueCalculation.getChildren() != null) {
			repayQueueCalculation.getChildren().clear();
		}
		if (repayRequest.getChildren() != null) {
			repayRequest.getChildren().clear();
		}
		if (readRecoveryFile.getChildren() != null) {
			readRecoveryFile.getChildren().clear();
		}
		if (prepareRecoveryFile.getChildren() != null) {
			prepareRecoveryFile.getChildren().clear();
		}
		if (repayResponse.getChildren() != null) {
			repayResponse.getChildren().clear();
		}
		if (amortizationCalculation.getChildren() != null) {
			amortizationCalculation.getChildren().clear();
		}
		if (amortizationPostings.getChildren() != null) {
			amortizationPostings.getChildren().clear();
		}
		if (uploadPftDetails.getChildren() != null) {
			uploadPftDetails.getChildren().clear();
		}
		if (provisionCalculation.getChildren() != null) {
			provisionCalculation.getChildren().clear();
		}
		if (provisionPostings.getChildren() != null) {
			provisionPostings.getChildren().clear();
		}
		if (depreciationPostings.getChildren() != null) {
			depreciationPostings.getChildren().clear();
		}
		if (dibursementPostings.getChildren() != null) {
			dibursementPostings.getChildren().clear();
		}
		if (collateralDeMarkPostings.getChildren() != null) {
			collateralDeMarkPostings.getChildren().clear();
		}
		if (ddaCancellationPostings.getChildren() != null) {
			ddaCancellationPostings.getChildren().clear();
		}
		if (postLimitUtilization.getChildren() != null) {
			postLimitUtilization.getChildren().clear();
		}
		if (postGLPLPostings.getChildren() != null) {
			postGLPLPostings.getChildren().clear();
		}
		if (ddaRepresentmentPostings.getChildren() != null) {
			ddaRepresentmentPostings.getChildren().clear();
		}
		if (rateReviewCalculation.getChildren() != null) {
			rateReviewCalculation.getChildren().clear();
		}
		if (databaseBackupBeforeEod.getChildren() != null) {
			databaseBackupBeforeEod.getChildren().clear();
		}
		if (databaseBackupAfterEod.getChildren() != null) {
			databaseBackupAfterEod.getChildren().clear();
		}
		if (auditDataPurging.getChildren() != null) {
			auditDataPurging.getChildren().clear();
		}
		if (capitalizationPostings.getChildren() != null) {
			capitalizationPostings.getChildren().clear();
		}
		if (overdueCalculation.getChildren() != null) {
			overdueCalculation.getChildren().clear();
		}
		if (suspenseCalculation.getChildren() != null) {
			suspenseCalculation.getChildren().clear();
		}
		if (postBenchmarkInfo.getChildren() != null) {
			postBenchmarkInfo.getChildren().clear();
		}
		if (postODDetails.getChildren() != null) {
			postODDetails.getChildren().clear();
		}
		if (notification.getChildren() != null) {
			notification.getChildren().clear();
		}
		if (statusUpdate.getChildren() != null) {
			statusUpdate.getChildren().clear();
		}
		if (postNextPaymentDetails.getChildren() != null) {
			postNextPaymentDetails.getChildren().clear();
		}
		if (postStudyAndDocFee.getChildren() != null) {
			postStudyAndDocFee.getChildren().clear();
		}
		if (postFXRevaluation.getChildren() != null) {
			postFXRevaluation.getChildren().clear();
		}
		if (postInstallmentDueSMS.getChildren() != null) {
			postInstallmentDueSMS.getChildren().clear();
		}
		if (postPastDueSMS.getChildren() != null) {
			postPastDueSMS.getChildren().clear();
		}
		if (financeMovement.getChildren() != null) {
			financeMovement.getChildren().clear();
		}
		if (thirdPartyPostings.getChildren() != null) {
			thirdPartyPostings.getChildren().clear();
		}
		if (installmentDueDatePostings.getChildren() != null) {
			installmentDueDatePostings.getChildren().clear();
		}
		if (archiveDocument.getChildren() != null) {
			archiveDocument.getChildren().clear();
		}
		if (sasExtract.getChildren() != null) {
			sasExtract.getChildren().clear();
		}
		if (sendMail.getChildren() != null) {
			sendMail.getChildren().clear();
		}
		if (snapShotPreparation.getChildren() != null) {
			snapShotPreparation.getChildren().clear();
		}
		if (beforeEOD.getChildren() != null) {
			beforeEOD.getChildren().clear();
		}

	}

	private ExecutionStatus copyDetails(StepExecution source, ExecutionStatus destination) {
		int total = 0;
		int processed = 0;

		if (source.getExecutionContext().containsKey("TOTAL")) {
			total = source.getExecutionContext().getInt("TOTAL");
		}

		if (source.getExecutionContext().containsKey("PROCESSED")) {
			processed = source.getExecutionContext().getInt("PROCESSED");
		}

		if (source.getExecutionContext().containsKey("INFO")) {
			destination.setInfo(source.getExecutionContext().getString("INFO"));
		}

		if (source.getExecutionContext().containsKey("VDATE")) {
			destination.setValueDate((Date) source.getExecutionContext().get("VDATE"));
		}

		destination.setExecutionName(source.getStepName());
		destination.setActualCount(total);
		destination.setProcessedCount(processed);
		destination.setStartTime(source.getStartTime());
		destination.setEndTime(source.getEndTime());
		destination.setStatus(source.getExitStatus().getExitCode());

		return destination;
	}

	public void onClickError(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		StepExecution stepExecution = (StepExecution) batchStatus.getAttribute("data");

		if (stepExecution != null) {
			Filedownload.save(stepExecution.getExitStatus().getExitDescription(), "text/plain", stepExecution.getStepName());
		}

		logger.debug("Leacing" + event.toString());
	}

	public void onCheck$lock(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (this.lock.isChecked()) {
			islock = true;
			resetPanels();
			lable_current_step.setVisible(false);

		} else {
			islock = false;
			lable_current_step.setVisible(true);
		}
		logger.debug("Leaving" + event.toString());
	}

	private String getLoggedInUsers() {
		StringBuilder builder = new StringBuilder();
		List<UserImpl> users = SessionUtil.getLoggedInUsers();
		SecurityUser secUser = null;
		if (!users.isEmpty()) {
			for (UserImpl user : users) {
				if (user.getUserId() != getUserWorkspace().getLoggedInUser().getLoginUsrID()) {
					if (builder.length() > 0) {
						builder.append("</br>");
					}
					secUser = user.getSecurityUser();
					builder.append("&bull;").append("&nbsp;").append(user.getUserId()).append("&ndash;").append(secUser.getUsrFName() + " " + StringUtils.trimToEmpty(secUser.getUsrMName()) + " " + secUser.getUsrLName());
				}
			}
		}
		return builder.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public class EODJob implements Runnable {

		public EODJob() {

		}

		@Override
		public void run() {
			PFSBatchAdmin.statrJob();
		}

	}

	private void closeOtherTabs() {

		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		Tabs tabs = tabbox.getTabs();
		List<Component> childs = new ArrayList<Component>(tabs.getChildren());

		for (Component component : childs) {
			if (component instanceof Tab) {
				Tab tab = (Tab) component;
				if ("tab_Home".equals(tab.getId()) || tab.getId().equals(tabbox.getSelectedTab().getId())) {
					continue;
				}

				tab.close();
			}

		}
	}

}
