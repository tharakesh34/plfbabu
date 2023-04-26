package com.pennant.webui.batch.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.CollateralStructureDAO;
import com.pennant.backend.endofday.main.BatchMonitor;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.service.batchProcessStatus.BatchProcessStatusService;
import com.pennant.backend.service.eod.EODConfigService;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.batch.model.BatchProcessStatus;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.eod.step.StepUtil.Step;
import com.pennanttech.pff.external.GSTDownloadService;
import com.pennanttech.pff.external.LedgerDownloadService;
import com.pennanttech.pff.external.eod.EODNotificationService;
import com.pennanttech.pff.external.service.ExternalFinanceSystemService;
import com.pennanttech.pff.process.collection.CollectionDataDownloadProcess;

/**
 * This is the controller class for the /WEB-INF/pages/Batch/BatchAdmin.zul file.
 */
public class BatchAdminCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = 4309463490869641570L;
	private static Logger logger = LogManager.getLogger(BatchAdminCtrl.class);

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
	protected Hbox panelCustomerMicroEOD;
	protected Label status = new Label();
	protected Rows posEodSteps;

	String[] args = new String[1];
	private boolean isInitialise = false;
	private boolean islock = false;

	protected ProcessExecution beforeEOD;
	protected ProcessExecution loanCancel;
	protected ProcessExecution masterStep;
	protected ProcessExecution microEOD;
	protected ProcessExecution microEODMonitor;
	protected ProcessExecution prepareCustomerQueue;
	protected ProcessExecution dmsRetrieveProcess;

	private JobExecution jobExecution;

	private transient BatchProcessStatusService batchProcessStatusService;
	private transient CollateralStructureDAO collateralStructureDAO;
	private transient CustomerGroupQueuingDAO customerGroupQueuingDAO;
	private boolean collectionProcess = false;
	private EODConfigService eODConfigService;
	private EODConfig eodConfig = null;
	private BatchProcessStatus bps = null;
	private com.pennanttech.pff.batch.backend.service.BatchProcessStatusService bpsService;

	private static String ALLOW_MULITIPLE_EODS_ON_SAME_DAY = null;
	private static boolean allowMultiEODOnSameDay = false;
	private static String ALLOW_EOD_START_ON_SAME_DAY = null;
	private static boolean allowEODStartOnSameDay = false;
	private boolean allowClearParameters = true;
	private static String QDP = null;
	private static int EOD_BATCH_REFRESH_TIME = -1;
	private static String EOD_BATCH_MONITOR = "NA";
	private static int EOD_THREAD_COUNT = -1;

	public BatchAdminCtrl() {
		super();
		BatchMonitor.getInstance();
	}

	public void onCreate$window_BatchAdmin(Event event) {

		if (EOD_BATCH_REFRESH_TIME == -1) {
			EOD_BATCH_REFRESH_TIME = SysParamUtil.getValueAsInt("EOD_BATCH_REFRESH_TIME");
		}

		if (allowClearParameters) {
			doClearParameters();
		}

		if (this.jobExecution == null || !isInitialise) {
			this.jobExecution = BatchMonitor.getJobExecution();
		}

		if (this.jobExecution != null) {
			this.jobExecution = BatchMonitor.getJobExecution(jobExecution.getId());
		}

		if (!isInitialise) {
			setDates();
			this.timer.setDelay(EOD_BATCH_REFRESH_TIME);
			noOfthread.setValue(SysParamUtil.getValueAsInt("EOD_THREAD_COUNT"));
			this.borderLayoutBatchAdmin.setHeight(getBorderLayoutHeight());
			isInitialise = true;
			collectionProcess = false;
			appendPostEodStep();
		}

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
				PFSBatchAdmin.destroy();
			}

			status.setValue(this.jobExecution.getStatus().toString());
			if (this.batchStatus.getChildren() != null) {
				this.batchStatus.getChildren().clear();
			}

			this.batchStatus.appendChild(status);
			setRunningStatus(collectionProcess);

		} else {
			if (this.timer.isRunning()) {
				this.timer.stop();
			}
		}

		if (eodConfig == null) {
			List<EODConfig> list = eODConfigService.getEODConfig();
			if (list.size() > 0) {
				eodConfig = list.get(0);
			}

			if (eodConfig == null) {
				eodConfig = new EODConfig();
			}
		}

		if (bps == null) {
			bps = new BatchProcessStatus();
			bps.setName("PLF_EOD");
			bps = bpsService.getBatchStatus(bps);
			if (bps == null) {
				bps = new BatchProcessStatus();
			}
		}

		if (eodConfig != null && eodConfig.isEnableAutoEod()) {
			this.btnStartJob.setDisabled(true);
			this.btnStartJob.setTooltiptext(Labels.getLabel("AUTO_EOD"));
		}

		Date sysDate = DateUtil.getSysDate();

		if (ALLOW_MULITIPLE_EODS_ON_SAME_DAY == null) {
			ALLOW_MULITIPLE_EODS_ON_SAME_DAY = SMTParameterConstants.ALLOW_MULITIPLE_EODS_ON_SAME_DAY;
			allowMultiEODOnSameDay = SysParamUtil.isAllowed(ALLOW_MULITIPLE_EODS_ON_SAME_DAY);
		}

		if (ALLOW_EOD_START_ON_SAME_DAY == null) {
			ALLOW_EOD_START_ON_SAME_DAY = SMTParameterConstants.ALLOW_EOD_START_ON_SAME_DAY;
			allowEODStartOnSameDay = SysParamUtil.isAllowed(ALLOW_EOD_START_ON_SAME_DAY);
		}

		if (!allowMultiEODOnSameDay) {
			if (bps != null && bps.getEndTime() != null && "S".equals(bps.getStatus())) {
				int days = DateUtil.getDaysBetween(sysDate, bps.getEndTime());
				if (days == 0) {
					int timeBetween = Integer.valueOf(DateUtil.timeBetween(sysDate, bps.getEndTime(), "HH"));

					if (timeBetween > 20) {
						this.btnStartJob.setDisabled(false);
					} else {
						this.btnStartJob.setDisabled(true);
						this.btnStartJob.setTooltiptext(Labels.getLabel("label_EOD_BEFORE_TIME"));
					}
				}
			}
		}

		if (!allowEODStartOnSameDay) {
			if (DateUtil.getDaysBetween(SysParamUtil.getNextBusinessdate(), sysDate) == 0) {
				this.btnStartJob.setDisabled(false);
			} else {
				this.btnStartJob.setDisabled(true);
				this.btnStartJob.setTooltiptext(Labels.getLabel("label_EOD_START_ON_SAMEDAY"));
			}
		}

		if (this.btnStartJob.isDisabled()) {
			this.btnStaleJob.setDisabled(false);
		} else {
			this.btnStaleJob.setDisabled(true);
		}

	}

	private void doClearParameters() {
		ALLOW_MULITIPLE_EODS_ON_SAME_DAY = null;
		allowMultiEODOnSameDay = false;
		ALLOW_EOD_START_ON_SAME_DAY = null;
		allowEODStartOnSameDay = false;
		allowClearParameters = false;
		QDP = null;
		EODUtil.setDatesReload(true);
	}

	/**
	 * used for Collections
	 * 
	 * @param uri
	 * @param tabName
	 * @param args
	 */
	protected void createNewPage(String uri, String tabName, Map<String, Object> args) {
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Center center = bl.getCenter();
		final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter")
				.getFellow("tabsIndexCenter");

		Tab tab = null;
		if (tabs.getFellowIfAny(tabName.trim().replace("menu_Item_", "tab_")) != null) {
			tab = (Tab) tabs.getFellow(tabName.trim().replace("menu_Item_", "tab_"));
			if (tab != null) {
				tab.close();
			}
		}
		tab = new Tab();
		tab.setId(tabName.trim().replace("menu_Item_", "tab_"));
		tab.setLabel(Labels.getLabel(tabName));
		tab.setClosable(true);
		tab.setParent(tabs);
		tab.setLabel(Labels.getLabel("menu_Item_CollectionsExtract"));

		final Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");
		final Tabpanel tabpanel = new Tabpanel();
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");
		tabpanel.setParent(tabpanels);

		Executions.createComponents(uri, tabpanel, args);
		tab.setSelected(true);
	}

	private void setDates() {
		if (EODUtil.isDatesReload()) {
			lable_Value_Date.setValue(DateUtil.formatToLongDate(SysParamUtil.getAppDate()));
			lable_NextBusiness_Date
					.setValue(DateUtil.formatToLongDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT)));
			lable_LastBusiness_Date
					.setValue(DateUtil.formatToLongDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_LAST)));
			EODUtil.setDatesReload(false);
		}
	}

	private void setRunningStatus(boolean collectionProcess) {
		String jobStatus = this.jobExecution.getStatus().toString();

		if (EOD_BATCH_MONITOR.equals("NA")) {
			EOD_BATCH_MONITOR = SysParamUtil.getValueAsString("EOD_BATCH_MONITOR");
		}

		try {
			if ("Y".equals(EOD_BATCH_MONITOR)) {
				if (!islock) {
					doFillStepExecutions(BatchMonitor.getStepExecution(this.jobExecution.getJobInstance()));
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		completedTime.setValue(BatchMonitor.getProcessingTime());

		if ("FAILED".equals(jobStatus) || "STOPPED".equals(jobStatus)) {
			status.setStyle("color:red;");
			this.btnStartJob.setLabel("Restart");
			this.btnStartJob.setTooltiptext("Restart Job");
			estimatedTime.setValue("");
			EODUtil.setDatesReload(true);
			setDates();
		}

		if ("COMPLETED".equals(jobStatus)) {
			this.btnStartJob.setLabel("Start");
			this.btnStartJob.setTooltiptext("Start");
			this.lable_current_step.setValue("");
			estimatedTime.setValue("");
			EODUtil.setDatesReload(true);
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

	public void onClick$btnStartJob(Event event) {
		logger.debug(Literal.ENTERING);

		String msg = "";
		if ("Start".equals(this.btnStartJob.getLabel())) {
			args[0] = DateUtil.formatToShortDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT));
			msg = Labels.getLabel("labe_start_job", args);
		} else {
			msg = Labels.getLabel("labe_reStart_job");
		}

		// Auto-Approval if it is in Progress.
		String status = null;

		if (QDP == null) {
			QDP = "QDP";
			status = batchProcessStatusService.getBatchStatus(QDP);
		}

		if (StringUtils.equals("I", status)) {
			MessageUtil.showError("Auto Approval of Disbursements is InProcess..");
			return;
		}

		if (ImplementationConstants.ALLOW_EOD_INTERVAL_VALIDATION) {
			if (this.jobExecution != null && StringUtils.equals(this.jobExecution.getExitStatus().getExitCode(),
					FlowExecutionStatus.COMPLETED.toString())) {
				int eODTimeInterval = SysParamUtil.getValueAsInt(SMTParameterConstants.EOD_INTERVAL_TIME);
				Date sysDate = DateUtil.getSysDate();
				Date lastJobExecutionTime = jobExecution.getEndTime();
				if (eODTimeInterval != 0) {
					int days = DateUtil.getDaysBetween(sysDate, lastJobExecutionTime);
					int hours = 0;

					if (days == 0) {
						hours = hours + Integer
								.valueOf(DateUtil.timeBetween(DateUtil.getSysDate(), lastJobExecutionTime, "HH"));
					} else {
						hours = days * 24;
						lastJobExecutionTime = DateUtil.addDays(lastJobExecutionTime, days);
						hours = hours + Integer
								.valueOf(DateUtil.timeBetween(DateUtil.getSysDate(), lastJobExecutionTime, "HH"));
					}

					if (hours < eODTimeInterval) {
						MessageUtil.showError(Labels.getLabel("label_EOD_BEFORE_TIME") + eODTimeInterval + " hours");
						return;
					}
				}
			}
		}

		try {
			MessageUtil.confirm(msg, evnt -> {
				if (Messagebox.ON_YES.equals(evnt.getName())) {
					PFSBatchAdmin.startedBy = getUserWorkspace().getLoggedInUser().getUserName();
					PFSBatchAdmin.loggedInUser = getUserWorkspace().getLoggedInUser();
					closeOtherTabs();
					PFSBatchAdmin.getInstance();
					estimatedTime.setValue(BatchMonitor.getEstimateTime());
					timer.start();

					this.btnStartJob.setDisabled(true);
					BatchMonitor.jobExecutionId = 0;
					BatchMonitor.avgTime = 0;

					if ("Start".equals(this.btnStartJob.getLabel())) {
						args[0] = DateUtil
								.formatToShortDate(SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT));
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
						collectionProcess = true;
						isInitialise = false;
					} catch (Exception e) {
						timer.stop();
						MessageUtil.showError(e);
					}

					Events.postEvent("onCreate", this.window_BatchAdmin, event);
				} else if (Messagebox.ON_NO.equals(evnt.getName())) {
					collectionProcess = false;
					Events.postEvent("onCreate", this.window_BatchAdmin, event);
				}
			});
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnStaleJob(Event event) {
		logger.debug(Literal.ENTERING);

		PFSBatchAdmin.getInstance();
		String msg = Labels.getLabel("labe_terminate_job");

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				try {
					args[0] = this.jobExecution.getJobParameters().getString("Date");
					resetPanels();
					PFSBatchAdmin.setArgs(args);
					PFSBatchAdmin.resetStaleJob(this.jobExecution);
					BatchMonitor.jobExecutionId = 0;
					BatchMonitor.avgTime = 0;
					Events.postEvent("onCreate", this.window_BatchAdmin, event);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		});

		logger.debug(Literal.LEAVING);
	}

	public void onTimer$timer(Event event) {
		Events.postEvent("onCreate", this.window_BatchAdmin, event);
	}

	/**
	 * Method for Rendering Step Execution Details List
	 * 
	 * @param stepExecution
	 */
	private void doFillStepExecutions(List<StepExecution> stepExecutionList) {
		if (this.jobExecution == null || CollectionUtils.isEmpty(stepExecutionList)) {
			return;
		}

		for (StepExecution stepExecution : stepExecutionList) {
			DataEngineStatus statu = BatchUtil.getRunningStatus(stepExecution);

			String exitCode = stepExecution.getExitStatus().getExitCode();

			if (this.jobExecution.getId().equals(stepExecution.getJobExecutionId())) {
				if ("FAILED".equals(exitCode)) {
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

			if (statu == null) {
				continue;
			}

			renderPanels(statu.getReference(), statu);
		}
	}

	/**
	 * This method render the Defined Executions for which value date matched with Current value date
	 * 
	 * @param ExecutionStatus (status)
	 */
	private void renderPanels(String stepName, DataEngineStatus status) {
		Step processName = null;
		try {

			if (stepName.contains(Step.microEOD.name())) {
				if (listBoxThread != null) {
					this.microEOD.setProcess(status);
					doFillCustomerEodDetails(status);
					setRunningProcess(this.microEOD);
				}

			} else {
				stepName = stepName.split(":")[0];
				processName = Step.valueOf(stepName);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}

		if (status == null || processName == null || stepName.contains(Step.microEOD.name())) {
			return;
		}

		switch (processName) {
		case beforeEOD:
			renderDetials(this.beforeEOD, status);
			break;

		case loanCancel:
			renderDetials(this.loanCancel, status);
			break;

		case prepareCustomerQueue:
			renderDetials(this.prepareCustomerQueue, status);
			break;

		case masterStep:
			renderDetials(this.masterStep, status);
			break;

		case microEOD:
			renderDetials(this.microEOD, status);
			break;

		case microEODMonitor:
			renderDetials(this.microEODMonitor, status);
			break;

		default:
			renderDetials((ProcessExecution) this.posEodSteps.getFellowIfAny(stepName), status);
			break;

		}

		status = null;
	}

	private void renderDetials(ProcessExecution execution, DataEngineStatus status) {
		if (execution == null) {
			return;
		}
		execution.setProcess(status);
		execution.render();
		setRunningProcess(execution);

	}

	private void setRunningProcess(ProcessExecution panel) {
		if (panel.getProgress() == null || "null".equalsIgnoreCase(panel.getProgress().trim())) {
			lable_current_step.setValue(panel.getTitle() + "	...");
		} else {
			lable_current_step.setValue(panel.getTitle() + panel.getProgress());
		}
	}

	private void resetPanels() {
		clearChilds(beforeEOD);
		clearChilds(loanCancel);
		clearChilds(masterStep);
		clearChilds(microEOD);
		clearChilds(microEODMonitor);
		clearChilds(prepareCustomerQueue);

		clearPostEodSteps();

		if (listBoxThread.getItems() != null) {
			this.listBoxThread.getItems().clear();
		}
	}

	private void clearChilds(ProcessExecution execution) {
		if (execution != null && execution.getChildren() != null) {
			execution.getChildren().clear();
		}
	}

	public void onClickError(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		StepExecution stepExecution = (StepExecution) batchStatus.getAttribute("data");

		if (stepExecution != null) {
			Filedownload.save(stepExecution.getExitStatus().getExitDescription(), "text/plain",
					stepExecution.getStepName());
		}

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$lock(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		if (this.lock.isChecked()) {
			islock = true;
			resetPanels();
			lable_current_step.setVisible(false);

		} else {
			islock = false;
			lable_current_step.setVisible(true);
		}
		logger.debug(Literal.LEAVING);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public class EODJob implements Runnable {

		public EODJob() {
			super();
		}

		@Override
		public void run() {
			PFSBatchAdmin.startJob();
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
	protected Longbox noOfCustomer;

	public void doFillCustomerEodDetails(DataEngineStatus status) {
		if (status == null) {
			return;
		}

		if (EOD_THREAD_COUNT == -1) {
			EOD_THREAD_COUNT = SysParamUtil.getValueAsInt("EOD_THREAD_COUNT");
		}

		noOfthread.setValue(EOD_THREAD_COUNT);

		boolean containsKey = status.getKeyAttributes().containsKey(EodConstants.DATA_TOTALCUSTOMER);

		if (!containsKey) {
			return;
		}

		if (containsKey) {
			noOfCustomer.setValue(
					Long.parseLong(status.getKeyAttributes().get(EodConstants.DATA_TOTALCUSTOMER).toString()));
		}
		String trheadName = status.getName();
		Listitem listitem = null;
		Listcell listcell = null;
		String threadId = trheadName.replace(":", "_");

		Component listItemComp = listBoxThread.getFellowIfAny(threadId);
		if (listItemComp != null && listItemComp instanceof Listitem) {
			listitem = (Listitem) listItemComp;
			listitem.getChildren().clear();
		} else {
			listitem = new Listitem();
		}
		listitem.setId(threadId);

		listcell = new Listcell(threadId);
		listcell.setParent(listitem);

		listcell = new Listcell(Long.toString(status.getTotalRecords()));
		listcell.setParent(listitem);

		listcell = new Listcell(Long.toString(status.getProcessedRecords()));
		listcell.setParent(listitem);
		try {
			listcell = new Listcell(ExecutionStatus.getStatus(status.getStatus()).getValue());
			listcell.setId(threadId + EodConstants.STATUS);
			if (!listitem.hasFellow(threadId + EodConstants.STATUS))
				listcell.setParent(listitem);

			listcell = new Listcell(DateUtil.timeBetween(status.getEndTime(), status.getStartTime()));
			listcell.setParent(listitem);
			listBoxThread.appendChild(listitem);

		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	private void appendPostEodStep() {
		if (posEodSteps.getChildren() != null && !posEodSteps.getChildren().isEmpty()) {
			return;
		}
		appendRow(Step.processInActiveFinances.name(), StepUtil.PROCESS_INACTIVE_FINANCES.getName());

		if (ImplementationConstants.ALLOW_NPA) {
			appendRow(Step.assetClassification.name(), StepUtil.NPA_CLASSIFICATION.getName());
			appendRow(Step.effAssetClassification.name(), StepUtil.EFF_NPA_CLASSIFICATION.getName());
		}

		if (ImplementationConstants.ALLOW_PROVISION) {
			appendRow(Step.provisionCalc.name(), StepUtil.PROVISION_CALC.getName());
		}
		// Auto Write Off
		appendRow(Step.autoWriteOffCalc.name(), StepUtil.AUTOWRITEOFF_CALC.getName());

		appendRow(Step.processINDASForInActiveFinances.name(), StepUtil.PROCESS_INDAS_INACTIVE_FINANCES.getName());

		if (customerGroupQueuingDAO.isLimitsConfigured()) {
			appendRow(Step.prepareCustomerGroupQueue.name(), StepUtil.PREPARE_CUSTOMER_GROUP_QUEUE.getName());
			appendRow(Step.limitCustomerGroupsUpdate.name(), StepUtil.CUSTOMER_GROUP_LIMITS_UPDATE.getName());
			appendRow(Step.institutionLimitUpdate.name(), StepUtil.INSTITUTION_LIMITS_UPDATE.getName());
		}

		// appendRow(Step.limitsUpdate.name(), StepUtil.CUSTOMER_LIMITS_UPDATE.getName());

		if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
			appendRow(Step.manualAdvisesCancellation.name(), StepUtil.CANCEL_INACTIVE_FINANCES_ADVISES.getName());
		}

		if (isServiceExists(ExternalFinanceSystemService.class)) {
			appendRow(Step.notifyLoanClosureDetailsToEFS.name(), StepUtil.LOAN_CLOSURE_DETAILS.getName());
		}

		appendRow(Step.datesUpdate.name(), StepUtil.DATES_UPDATE.getName());
		appendRow(Step.snapShotPreparation.name(), StepUtil.SNAPSHOT_PREPARATION.getName());

		if (isServiceExists(GSTDownloadService.class)) {
			appendRow(Step.gstDownload.name(), StepUtil.GST_DOWNLOAD.getName());
		}

		if (isServiceExists(LedgerDownloadService.class)) {
			appendRow(Step.ledgerDownLoad.name(), StepUtil.LEDGER_DOWNLOAD.getName());
		}

		if (isServiceExists(EODNotificationService.class)) {
			appendRow(Step.ledgerNotification.name(), StepUtil.LEDGER_NOTIFICATION.getName());
		}

		if (ImplementationConstants.COLLECTION_DOWNLOAD_REQ && isServiceExists(CollectionDataDownloadProcess.class)) {
			appendRow(Step.collectionDataDownLoad.name(), StepUtil.COLLECTION_DOWNLOAD.getName());

			if (isServiceExists(EODNotificationService.class)) {
				appendRow(Step.collectionNotification.name(), StepUtil.COLLECTION_NOTIFICATION.getName());
			}
		}

		if (collateralStructureDAO.isMarketablesecuritiesExists()) {
			appendRow(Step.loadCollateralRevaluationData.name(), StepUtil.COLLATERAL_REVALUATION.getName());
		}

		appendRow(Step.retailcibil.name(), StepUtil.CIBIL_EXTRACT_RETAIL.getName());
		appendRow(Step.corporatecibil.name(), StepUtil.CIBIL_EXTRACT_CORPORATE.getName());

	}

	private boolean isServiceExists(Class<?> requiredType) {
		try {

			if (PFSBatchAdmin.PFS_JOB_CONTEXT == null) {
				PFSBatchAdmin.getInstance();
			}

			PFSBatchAdmin.PFS_JOB_CONTEXT.getBean(requiredType);

			return true;
		} catch (NoUniqueBeanDefinitionException e1) {
			return true;
		} catch (NoSuchBeanDefinitionException e) {
			return false;
		}
	}

	private void appendRow(String id, String title) {
		Row row = (Row) posEodSteps.getLastChild();
		if (row == null) {
			row = new Row();
			posEodSteps.appendChild(row);
		}

		if (row.getChildren() != null && row.getChildren().size() == 3) {
			row = new Row();
			posEodSteps.appendChild(row);
		}

		Cell cell = new Cell();
		cell.setWidth("33%");
		row.appendChild(cell);

		ProcessExecution processExecution = new ProcessExecution();
		processExecution.setId(id);
		processExecution.setBorder("normal");
		processExecution.setTitle(title);
		cell.appendChild(processExecution);

	}

	private void clearPostEodSteps() {
		List<Row> rows = posEodSteps.getChildren();

		for (Row row : rows) {
			List<Cell> cells = row.getChildren();

			for (Cell cell : cells) {
				ProcessExecution processExecution = (ProcessExecution) cell.getFirstChild();
				clearChilds(processExecution);
			}
		}
	}

	@Autowired
	public void setBatchProcessStatusService(BatchProcessStatusService batchProcessStatusService) {
		this.batchProcessStatusService = batchProcessStatusService;
	}

	@Autowired
	public void setCollateralStructureDAO(CollateralStructureDAO collateralStructureDAO) {
		this.collateralStructureDAO = collateralStructureDAO;
	}

	@Autowired
	public void setCustomerGroupQueuingDAO(CustomerGroupQueuingDAO customerGroupQueuingDAO) {
		this.customerGroupQueuingDAO = customerGroupQueuingDAO;
	}

	@Autowired
	public void setEODConfigService(EODConfigService eODConfigService) {
		this.eODConfigService = eODConfigService;
	}

	@Autowired
	public void setBpsService(com.pennanttech.pff.batch.backend.service.BatchProcessStatusService bpsService) {
		this.bpsService = bpsService;
	}
}
