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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
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
	private static final long serialVersionUID = 4309463490869641570L;
	private final static Logger logger = Logger.getLogger(BatchAdminCtrl.class);

	protected Window window_BatchAdmin;
	protected Textbox lable_LastBusiness_Date;
	protected Textbox lable_NextBusiness_Date;
	protected Textbox lable_Value_Date;
	protected Checkbox lock;
	protected Timer timer;
	protected Textbox estimatedTime;
	protected Textbox completedTime;
	protected Label label_elapsed_Time;
	protected Hbox batchStatus;
	protected Button btnStartJob;
	protected Button btnStaleJob;
	protected Label lable_current_step;
	protected Borderlayout borderLayoutBatchAdmin;
	// protected Hbox panelCustomerMicroEOD;

	protected Label status = new Label();

	String[] args = new String[1];
	private boolean isInitialise = false;
	private boolean islock = false;

	protected ProcessExecution beforeEOD;
	protected ProcessExecution prepareCustomerQueue;

	protected ProcessExecution masterStep;
	protected ProcessExecution microEOD;
	protected ProcessExecution microEODMonitor;

	protected ProcessExecution snapShotPreparation;
	protected ProcessExecution dataExtract;

	Map<String, ExecutionStatus> processMap = new HashMap<String, ExecutionStatus>();
	private JobExecution jobExecution;

	public enum PFSBatchProcessess {
		beforeEOD, prepareCustomerQueue, masterStep, microEOD, microEODMonitor, snapShotPreparation, dataExtract
	}

	public BatchAdminCtrl() {
		super();
		BatchMonitor.getInstance();
	}

	public void onCreate$window_BatchAdmin(Event event) throws Exception {
		// databaseBackupBeforEod.setVisible(false);
		// panelCustomerMicroEOD.setVisible(true);
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

		} else {
			if (this.timer.isRunning()) {
				this.timer.stop();
			}
		}

		if (this.btnStartJob.isDisabled()) {
			this.btnStaleJob.setDisabled(false);
		} else {
			this.btnStaleJob.setDisabled(true);
		}

	}

	private void setDates() {
		lable_Value_Date.setValue(DateUtility.getAppValueDate(DateFormat.LONG_DATE));
		lable_NextBusiness_Date.setValue(DateUtility.formatToLongDate(SysParamUtil
				.getValueAsDate(PennantConstants.APP_DATE_NEXT)));
		lable_LastBusiness_Date.setValue(DateUtility.formatToLongDate(SysParamUtil
				.getValueAsDate(PennantConstants.APP_DATE_LAST)));
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

			jobStatusMsg = jobExecution.getExecutionContext().get("DBBACKUP_STATUS") == null ? ""
					: (String) jobExecution.getExecutionContext().get("DBBACKUP_STATUS");
			StringBuilder clientNotifications = new StringBuilder();
			if (StringUtils.isNotEmpty(jobStatusMsg)) {
				clientNotifications.append("Data Base Back After EOD is Failed");
				clientNotifications.append(" : " + jobStatusMsg);
				logger.warn(clientNotifications.toString());
			}

			jobStatusMsg = jobExecution.getExecutionContext().get("AUDITPURGING_STATUS") == null ? ""
					: (String) jobExecution.getExecutionContext().get("AUDITPURGING_STATUS");
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

		String msg = "";
		if ("Start".equals(this.btnStartJob.getLabel())) {
			args[0] = DateUtility.formatToShortDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT));
			msg = Labels.getLabel("labe_start_job", args);
		} else {
			msg = Labels.getLabel("labe_reStart_job");
		}

		conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Information"), MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			closeOtherTabs();
			PFSBatchAdmin.getInstance();
			timer.start();

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
				PFSBatchAdmin.setRunType("RE-START");
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
		int conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Information"), MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

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

					if ("EXECUTING".equals(stepExecution.getExitStatus().getExitCode())
							&& !stepExecution.getStepName().contains(PFSBatchProcessess.microEOD.name())) {
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
	 * This method render the Defined Executions for which value date matched with Current value date
	 * 
	 * @param ExecutionStatus
	 *            (status)
	 */
	private void renderPanels(String stepName) {
		PFSBatchProcessess processName = null;
		ExecutionStatus status = processMap.get(stepName);
		try {

			if (stepName.contains(PFSBatchProcessess.microEOD.name())) {
				if (listBoxThread != null) {
					this.microEOD.setProcess(status);
					doFillCustomerEodDetails(status);
					setRunningProcess(this.microEOD);
					if ("EXECUTING".equals(status.getStatus())) {
						setRunningProcess(this.microEOD);
					}
				}

			} else {
				processName = PFSBatchProcessess.valueOf(stepName);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}

		if (status != null && processName != null) {

			switch (processName) {
			case beforeEOD:
				this.beforeEOD.setProcess(status);
				this.beforeEOD.render();
				setRunningProcess(this.beforeEOD);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.beforeEOD);
				}
				break;
			case prepareCustomerQueue:
				this.prepareCustomerQueue.setProcess(status);
				this.prepareCustomerQueue.render();
				setRunningProcess(this.prepareCustomerQueue);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.prepareCustomerQueue);
				}
				break;
			case masterStep:
				this.masterStep.setProcess(status);
				this.masterStep.render();
				setRunningProcess(this.masterStep);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.masterStep);
				}
				break;
			case microEOD:
				this.microEOD.setProcess(status);
				this.microEOD.render();
				setRunningProcess(this.microEOD);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.microEOD);
				}
				break;
			case microEODMonitor:
				this.microEODMonitor.setProcess(status);
				this.microEODMonitor.render();
				setRunningProcess(this.microEODMonitor);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.microEODMonitor);
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

			case dataExtract:
				this.dataExtract.setProcess(status);
				this.dataExtract.render();
				setRunningProcess(this.dataExtract);
				if ("EXECUTING".equals(status.getStatus())) {
					setRunningProcess(this.dataExtract);
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

		if (beforeEOD.getChildren() != null) {
			beforeEOD.getChildren().clear();
		}
		if (prepareCustomerQueue.getChildren() != null) {
			prepareCustomerQueue.getChildren().clear();
		}
		if (masterStep.getChildren() != null) {
			masterStep.getChildren().clear();
		}
		if (microEOD.getChildren() != null) {
			microEOD.getChildren().clear();
		}
		if (microEODMonitor.getChildren() != null) {
			microEODMonitor.getChildren().clear();
		}

		if (snapShotPreparation.getChildren() != null) {
			snapShotPreparation.getChildren().clear();
		}
		if (dataExtract.getChildren() != null) {
			dataExtract.getChildren().clear();
		}
		this.listBoxThread.getItems().clear();

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

		if (source.getExecutionContext().containsKey("CustomerCount")) {
			destination.setCustomerCount(source.getExecutionContext().getInt("CustomerCount"));
		}

		if (source.getExecutionContext().containsKey("Completed")) {
			destination.setCompleted((Integer) source.getExecutionContext().get("Completed"));
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
			Filedownload.save(stepExecution.getExitStatus().getExitDescription(), "text/plain",
					stepExecution.getStepName());
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
					builder.append("&bull;")
							.append("&nbsp;")
							.append(user.getUserId())
							.append("&ndash;")
							.append(secUser.getUsrFName() + " " + StringUtils.trimToEmpty(secUser.getUsrMName()) + " "
									+ secUser.getUsrLName());
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
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
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

	protected Listbox listBoxThread;
	protected Intbox noOfthread;
	protected Intbox noOfCustomer;

	public void doFillCustomerEodDetails(ExecutionStatus status) {
		noOfthread.setValue(SysParamUtil.getValueAsInt("EOD_THREAD_COUNT"));
		noOfCustomer.setValue(0);

		if (status != null) {
			String trheadName = status.getExecutionName();
			Listitem listitem = null;
			Listcell listcell = null;
			String threadId = trheadName.replace(":", "_");

			Component listItemComp = listBoxThread.getFellowIfAny(threadId);
			if (listItemComp != null && listItemComp instanceof Listitem) {
				listitem = (Listitem) listItemComp;
				Component listcellComp = listitem.getFellowIfAny(threadId + "Status".hashCode());

				if (listcellComp != null && listcellComp instanceof Listcell) {
					listcell = (Listcell) listcellComp;
					if (!status.getStatus().equals(listcell.getLabel().toString())) {
						listitem.getChildren().clear();
					}
				}
			} else {
				listitem = new Listitem();
			}
			listitem.setId(threadId);

			listcell = new Listcell(threadId);
			listcell.setParent(listitem);

			listcell = new Listcell(Integer.toString(status.getCustomerCount()));
			listcell.setParent(listitem);

			listcell = new Listcell(Integer.toString(status.getCompleted()));
			listcell.setParent(listitem);

			listcell = new Listcell(status.getStatus());
			listcell.setId(threadId + "Status".hashCode());
			if (!listitem.hasFellow(threadId + "Status".hashCode()))
				listcell.setParent(listitem);
			listBoxThread.appendChild(listitem);

		}
	}

}
