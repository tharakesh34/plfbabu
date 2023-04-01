/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : AMZBatchAdminCtrl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 13-10-2018 *
 * 
 * Modified Date : 13-10-2018 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-10-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.batch.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.endofday.main.AMZBatchAdmin;
import com.pennant.backend.endofday.main.AMZBatchMonitor;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Batch/AMZBatchAdmin.zul file.
 */
public class AMZBatchAdminCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = 4309463490869641570L;
	private static final Logger logger = LogManager.getLogger(AMZBatchAdminCtrl.class);

	protected Window window_AMZBatchAdmin;
	protected Textbox lable_LastAMZMonth_Date;
	protected Textbox lable_NextAMZMonth_Date;
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

	protected Listbox listBoxThread;
	protected Intbox noOfthread;
	protected Longbox noOfCustomer;

	protected Label status = new Label();
	String[] args = new String[1];
	private boolean isInitialise = false;
	private boolean islock = false;

	protected ProcessExecution beforeAMZProcess;
	protected ProcessExecution prepareIncomeAMZDetails;
	protected ProcessExecution prepareAmortizationQueue;
	protected ProcessExecution amzMasterStep;
	protected ProcessExecution amzProcess;
	protected ProcessExecution afterAMZProcess;

	Map<String, ExecutionStatus> processMap = new HashMap<String, ExecutionStatus>();
	private JobExecution jobExecution;

	int threadCount = SysParamUtil.getValueAsInt("AMZ_THREAD_COUNT");
	String amzBatchMonitor = SysParamUtil.getValueAsString("EOD_BATCH_MONITOR");

	public enum AMZBatchProcess {
		beforeAMZProcess, prepareIncomeAMZDetails, prepareAmortizationQueue, amzMasterStep, amzProcess, afterAMZProcess
	}

	public AMZBatchAdminCtrl() {
		super();
		AMZBatchMonitor.getInstance();
	}

	public class AMZJob implements Runnable {

		public AMZJob() {
		}

		@Override
		public void run() {
			AMZBatchAdmin.statrJob();
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void onCreate$window_AMZBatchAdmin(Event event) {

		if (!isInitialise) {
			setDates();
			this.timer.setDelay(SysParamUtil.getValueAsInt("EOD_BATCH_REFRESH_TIME"));
			this.borderLayoutBatchAdmin.setHeight(getBorderLayoutHeight());
			isInitialise = true;
		}

		this.jobExecution = AMZBatchMonitor.getJobExecution();

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

				// BatchUtil.EXECUTING = null;
				AMZBatchAdmin.destroy();
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

	/**
	 * 
	 */
	private void setRunningStatus() {
		try {
			if (StringUtils.equals("Y", amzBatchMonitor)) {
				if (!islock) {
					doFillStepExecutions(AMZBatchMonitor.getStepExecution(this.jobExecution.getJobInstance()));
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		String jobStatus = this.jobExecution.getStatus().toString();
		completedTime.setValue(AMZBatchMonitor.getProcessingTime());

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
			AMZBatchMonitor.jobExecutionId = 0;
		}
	}

	/**
	 * Update dates
	 */
	private void setDates() {
		Date prvAMZMonth = SysParamUtil.getValueAsDate(AmortizationConstants.AMZ_MONTHEND);

		Date nextAMZMonth = DateUtil.addDays(prvAMZMonth, 1);
		nextAMZMonth = DateUtil.getMonthEnd(nextAMZMonth);

		lable_LastAMZMonth_Date.setValue(DateUtil.format(prvAMZMonth, DateFormat.LONG_MONTH.getPattern()));
		lable_NextAMZMonth_Date.setValue(DateUtil.format(nextAMZMonth, DateFormat.LONG_MONTH.getPattern()));
	}

	/**
	 * 
	 * @param event
	 */
	public void onClick$btnStartJob(Event event) {
		logger.debug(Literal.ENTERING);

		String msg = "";
		Date prvAMZMonth = SysParamUtil.getValueAsDate(AmortizationConstants.AMZ_MONTHEND);
		Date amzMonth = DateUtil.addDays(prvAMZMonth, 1);
		amzMonth = DateUtil.getMonthEnd(amzMonth);

		String strAMZMonth = DateUtil.format(amzMonth, DateFormat.LONG_MONTH.getPattern());

		// Validate EOD is in progress or not
		String phase = SysParamUtil.getValueAsString(PennantConstants.APP_PHASE);
		if (StringUtils.equals(phase, PennantConstants.APP_PHASE_EOD)) {
			MessageUtil.showError(Labels.getLabel("Amortization_EOD_Check"));
			return;
		}

		if (amzMonth.compareTo(SysParamUtil.getAppDate()) >= 0) {
			MessageUtil.showError(Labels.getLabel("label_AMZProcessNotAllowed", new String[] { strAMZMonth }));
			return;
		}

		if ("Start".equals(this.btnStartJob.getLabel())) {
			args[0] = strAMZMonth;
			msg = Labels.getLabel("labe_AMZStart_job", args);
		} else {
			msg = Labels.getLabel("labe_reStart_job");
		}

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {

				AMZBatchAdmin.getInstance();
				estimatedTime.setValue(AMZBatchMonitor.getEstimateTime());
				timer.start();

				this.btnStartJob.setDisabled(true);
				AMZBatchMonitor.jobExecutionId = 0;
				AMZBatchMonitor.avgTime = 0;
				this.processMap.clear();

				if ("Start".equals(this.btnStartJob.getLabel())) {
					args[0] = strAMZMonth;
					AMZBatchAdmin.setArgs(args);
					AMZBatchAdmin.setRunType("START");
					resetPanels();
				} else {
					args[0] = this.jobExecution.getJobParameters().getString(AmortizationConstants.AMZ_JOB_PARAM);
					AMZBatchAdmin.setArgs(args);
					AMZBatchAdmin.setRunType("RE-START");
				}

				try {

					Thread thread = new Thread(new AMZJob());
					thread.start();
					Thread.sleep(1000);

				} catch (Exception e) {
					timer.stop();
					MessageUtil.showError(e);
				}

				Events.postEvent("onCreate", this.window_AMZBatchAdmin, event);
			}
		});

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnStaleJob(Event event) {
		logger.debug(Literal.ENTERING);

		AMZBatchAdmin.getInstance();
		String msg = Labels.getLabel("labe_terminate_job");

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				try {
					args[0] = this.jobExecution.getJobParameters().getString(AmortizationConstants.AMZ_JOB_PARAM);
					resetPanels();
					AMZBatchAdmin.setArgs(args);
					AMZBatchAdmin.resetStaleJob(this.jobExecution);
					AMZBatchMonitor.jobExecutionId = 0;
					AMZBatchMonitor.avgTime = 0;
					Events.postEvent("onCreate", this.window_AMZBatchAdmin, event);

				} catch (Exception e) {
					MessageUtil.showError(e);
				}

			}
		});

		logger.debug(Literal.LEAVING);
	}

	public void onTimer$timer(Event event) {
		Events.postEvent("onCreate", this.window_AMZBatchAdmin, event);
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

			if (statu == null) {
				continue;
			}

			String exitCode = stepExecution.getExitStatus().getExitCode();

			renderPanels(statu.getReference(), statu);

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
		}
	}

	/**
	 * This method render the Defined Executions for which value date matched with Current value date
	 * 
	 * @param ExecutionStatus (status)
	 */
	private void renderPanels(String stepName, DataEngineStatus status) {
		AMZBatchProcess processName = null;
		try {

			if (stepName.contains(AMZBatchProcess.amzProcess.name())) {
				if (listBoxThread != null) {
					this.amzProcess.setProcess(status);
					doFillFinanceAMZDetails(status);
					setRunningProcess(this.amzProcess);
				}

			} else {
				processName = AMZBatchProcess.valueOf(stepName);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			return;
		}

		if (status == null || processName == null) {
			return;
		}

		switch (processName) {

		case beforeAMZProcess:
			renderDetials(this.beforeAMZProcess, status);
			break;

		case prepareIncomeAMZDetails:
			renderDetials(this.prepareIncomeAMZDetails, status);
			break;

		case prepareAmortizationQueue:
			renderDetials(this.prepareAmortizationQueue, status);
			break;

		case amzMasterStep:
			renderDetials(this.amzMasterStep, status);
			break;

		case amzProcess:
			renderDetials(this.amzProcess, status);
			break;

		case afterAMZProcess:
			renderDetials(this.afterAMZProcess, status);
			break;

		default:
			break;

		}

		status = null;
	}

	/**
	 * 
	 * @param execution
	 * @param status
	 */
	private void renderDetials(ProcessExecution execution, DataEngineStatus status) {
		if (execution == null) {
			return;
		}
		execution.setProcess(status);
		execution.render();
		setRunningProcess(execution);

	}

	/**
	 * 
	 * @param panel
	 */
	private void setRunningProcess(ProcessExecution panel) {

		if (panel.getProgress() == null || "null".equalsIgnoreCase(panel.getProgress().trim())) {
			lable_current_step.setValue(panel.getTitle() + "	...");
		} else {
			lable_current_step.setValue(panel.getTitle() + panel.getProgress());
		}

	}

	/**
	 * 
	 */
	private void resetPanels() {

		clearChilds(beforeAMZProcess);
		clearChilds(prepareIncomeAMZDetails);
		clearChilds(prepareAmortizationQueue);
		clearChilds(amzMasterStep);
		clearChilds(amzProcess);
		clearChilds(afterAMZProcess);

		if (listBoxThread.getItems() != null) {
			this.listBoxThread.getItems().clear();
		}
	}

	/**
	 * 
	 * @param execution
	 */
	private void clearChilds(ProcessExecution execution) {
		if (execution.getChildren() != null) {
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

	/**
	 * 
	 * @param status
	 */
	public void doFillFinanceAMZDetails(DataEngineStatus status) {
		if (status == null) {
			return;
		}

		boolean containsKey = status.getKeyAttributes().containsKey(AmortizationConstants.DATA_TOTALFINANCES);

		if (!containsKey) {
			return;
		}

		if (containsKey) {
			noOfCustomer.setValue(
					Long.parseLong(status.getKeyAttributes().get(AmortizationConstants.DATA_TOTALFINANCES).toString()));
		}

		noOfthread.setValue(threadCount);

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

		listcell = new Listcell(status.getStatus());
		listcell.setId(threadId + AmortizationConstants.STATUS);

		if (!listitem.hasFellow(threadId + AmortizationConstants.STATUS))
			listcell.setParent(listitem);

		listcell = new Listcell(DateUtil.timeBetween(status.getEndTime(), status.getStartTime()));
		listcell.setParent(listitem);

		listBoxThread.appendChild(listitem);
	}
}