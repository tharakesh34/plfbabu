package com.pennant.webui.batch.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
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
import org.zkoss.zul.Space;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.endofday.main.PFSBatchAdmin;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Batch/BatchAdmin.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class BatchAdminCtrl  extends GFCBaseCtrl implements Serializable  {

	private static final long serialVersionUID = 4309463490869641570L;
	private final static Logger logger = Logger.getLogger(BatchAdminCtrl.class);

	protected Window 		window_BatchAdmin;
	protected Label 		lable_LastBusiness_Date;
	protected Label 		lable_NextBusiness_Date;
	protected Label 		lable_Value_Date;	
	protected Hbox 			BatchStatus;
	protected Checkbox 		lock;
	protected Timer 		timer;

	protected Image 		image_Status = new Image("/images/stepExecution.gif");
	protected Label 		lable_Job_Status = new Label();

	protected Button  		btnStartJob; 
	protected Button  		btnStaleJob; 
	protected Label	 		lable_current_step;
	protected Listbox 		listBoxStepExecution;
	protected Borderlayout	borderLayoutBatchAdmin;

	String[] args = new String[1];

	protected JobExecution 	jobExecution;
	private BatchAdminDAO 	batchAdminDAO;
	private PFSBatchAdmin pfsBatchAdmin;
	private long JOB_ID;

	public BatchAdminCtrl() {
		super();
		
		//New Apllication Context(Launch Context XML initiation)
		pfsBatchAdmin = new PFSBatchAdmin();
		setPfsBatchAdmin(pfsBatchAdmin);
	}

	public void onCreate$window_BatchAdmin(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		//Fetch Current Execution Job ID
		JOB_ID = getBatchAdminDAO().getCurrentBatch().getJobId();
		
		//Reset labels with Application Dates of System Parameters
		lable_Value_Date.setValue(DateUtility.formatUtilDate((Date)SystemParameterDetails.getSystemParameterValue(
				"APP_VALUEDATE"), PennantConstants.dateFormate));
		lable_NextBusiness_Date.setValue(DateUtility.formatUtilDate((Date)SystemParameterDetails.getSystemParameterValue(
				"APP_NEXT_BUS_DATE"), PennantConstants.dateFormate));
		lable_LastBusiness_Date.setValue(DateUtility.formatUtilDate((Date)SystemParameterDetails.getSystemParameterValue(
				"APP_LAST_BUS_DATE"), PennantConstants.dateFormate));
		
		this.borderLayoutBatchAdmin.setHeight(String.valueOf(((Intbox) Path.getComponent(
				"/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - 
				PennantConstants.borderlayoutMainNorth)+"px");
		
		this.listBoxStepExecution.setHeight(String.valueOf(((Intbox) Path.getComponent(
				"/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - 
				PennantConstants.borderlayoutMainNorth - 70)+"px");

		//Retrieve Presently Executing Job Reference
		this.jobExecution  = getPfsBatchAdmin().getSimpleJobExplorer().getJobExecution(JOB_ID);

		//Reset Executed Button Statuses depends on Job Status
		if(this.jobExecution != null) {
			if(this.jobExecution.isRunning()) {				
				this.btnStartJob.setDisabled(true);
				this.btnStaleJob.setDisabled(false);
			} else {
				this.btnStartJob.setDisabled(false);
				this.btnStaleJob.setDisabled(true);
			}

			this.lable_Job_Status.setValue(this.jobExecution.getStatus().toString());
			this.BatchStatus.removeChild(this.image_Status);
			this.BatchStatus.removeChild(this.lable_Job_Status);

			if("STARTED".equals(this.jobExecution.getStatus().toString())) {					
				this.BatchStatus.appendChild(this.image_Status);
			} else if("STOPPED".equals(this.jobExecution.getStatus().toString())){
				this.btnStartJob.setLabel("Restart");
				this.btnStartJob.setTooltiptext("Restart");
				this.BatchStatus.appendChild(this.lable_Job_Status);
			} else {
				this.BatchStatus.appendChild(this.lable_Job_Status);
				this.btnStartJob.setLabel("Run");
				this.btnStartJob.setTooltiptext("Run");
			}

			this.listBoxStepExecution.getItems().clear();
			this.jobExecution = getPfsBatchAdmin().getSimpleJobExplorer().getJobExecution(this.jobExecution.getId());
			
			List<StepExecution> stepExecutionList = new ArrayList<StepExecution>(
					this.jobExecution.getStepExecutions());
			Collections.reverse(stepExecutionList);
			this.listBoxStepExecution.getItems().clear();
			doFillStepExecutions(stepExecutionList);

		}
		if(this.btnStartJob.isDisabled()) {
			this.btnStaleJob.setDisabled(false);
		} else {
			this.btnStaleJob.setDisabled(true);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnStartJob(Event event) throws Exception{		
		logger.debug("Entering" + event.toString());
		
		String msg = "";
		if ("Run".equals(this.btnStartJob.getLabel())) {
			msg = "Do you wish to continue for Next Business Date " + 
			DateUtility.formatUtilDate((Date) SystemParameterDetails.getSystemParameterValue("APP_NEXT_BUS_DATE"),
					PennantConstants.dateFormate);
		} else {
			msg = "Do you wish to Restart the Job";
		}

		MultiLineMessageBox.doSetTemplate();
		int conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Information"), MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				MultiLineMessageBox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {			

			this.listBoxStepExecution.getItems().clear();
			this.btnStartJob.setDisabled(true);
			
			args[0] = DateUtility.formatDate((Date) SystemParameterDetails.getSystemParameterValue("APP_NEXT_BUS_DATE"), 
					PennantConstants.dateFormat);
			
			if ("Run".equals(this.btnStartJob.getLabel())) {				
				getPfsBatchAdmin().statrJob(args);
				this.JOB_ID = getBatchAdminDAO().getCurrentBatch().getJobId();
				this.jobExecution  = getPfsBatchAdmin().getSimpleJobExplorer().getJobExecution(this.JOB_ID);
			} else {
				getPfsBatchAdmin().reStatrJob(this.jobExecution);
				this.JOB_ID = getBatchAdminDAO().getCurrentBatch().getJobId();
				this.jobExecution  = getPfsBatchAdmin().getSimpleJobExplorer().getJobExecution(this.JOB_ID);
			}

			if (getPfsBatchAdmin().getJobStatus() != null) {
				PTMessageUtils.showErrorMessage(getPfsBatchAdmin().getJobStatus());
				btnStaleJob.setDisabled(false);
			}	
		}
		
		//Event for Recreation Of Window
		Events.postEvent("onCreate", this.window_BatchAdmin, event);
		
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnStaleJob(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		String msg = "Do you wish to  Terminate the Job";
		MultiLineMessageBox.doSetTemplate();
		int conf = MultiLineMessageBox.show(msg, Labels.getLabel("message.Information"), MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			args[0] = DateUtility.formatDate((Date) SystemParameterDetails.getSystemParameterValue("APP_NEXT_BUS_DATE"), 
					PennantConstants.dateFormat);
			getPfsBatchAdmin().resetStaleJob(this.jobExecution);

			if (getPfsBatchAdmin().getJobStatus() != null) {
				PTMessageUtils.showErrorMessage(getPfsBatchAdmin().getJobStatus());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onTimer$timer(Event event) {
		logger.debug("Entering" + event.toString());
		if (btnStartJob.isDisabled()) {
			this.window_BatchAdmin.invalidate();
			Events.postEvent("onCreate", this.window_BatchAdmin, event);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Rendering Step Execution Details List
	 * @param stepExecution
	 * @throws Exception
	 */
	private void doFillStepExecutions(List<StepExecution> stepExecutionList) throws Exception {
		logger.debug("Entering");

		Listitem item = null;
		Listcell lc = null;
		if(stepExecutionList != null && stepExecutionList.size() > 0){
			for (StepExecution stepExecution : stepExecutionList) {

				if("EXECUTING".equals(stepExecution.getExitStatus().getExitCode())) {
					lable_current_step.setStyle("color:green;");
					lable_current_step.setValue(stepExecution.getStepName().toUpperCase()) ;
				} else {
					lable_current_step.setValue("") ;
				}

				item = new Listitem();
				lc = new Listcell(String.valueOf(stepExecution.getId()));
				lc.setParent(item);

				if(stepExecution.getExecutionContext().containsKey("FIELD_COUNT")) {
					lc = new Listcell(stepExecution.getStepName().toUpperCase()+"["+
							stepExecution.getExecutionContext().get("FIELD_COUNT")+"]");
				} else {
					lc = new Listcell(stepExecution.getStepName().toUpperCase());
				}
				lc.setParent(item);	

				Date date = null;
				if(stepExecution.getExecutionContext().containsKey(stepExecution.getId().toString())) {
					date =  (java.sql.Date) stepExecution.getExecutionContext().get(stepExecution.getId().toString());	
				}

				if("EXECUTING".equals(stepExecution.getExitStatus().getExitCode()) || 
						"FAILED".equals(stepExecution.getExitStatus().getExitCode()) && date == null) {
					date = (Date) SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE");
				}		

				lc = new Listcell(DateUtility.formatUtilDate(date, PennantConstants.dateFormate));
				lc.setParent(item);

				// StartTime
				lc = new Listcell(DateUtility.formatUtilDate(stepExecution.getStartTime(), PennantConstants.dateFormate));
				lc.setParent(item);

				// EndTime
				lc = new Listcell(DateUtility.formatUtilDate(stepExecution.getEndTime(), PennantConstants.dateFormate));
				lc.setParent(item);

				// Duration
				lc = new Listcell(DateUtility.timeBetween(stepExecution.getEndTime(), stepExecution.getStartTime()));
				lc.setParent(item);

				lc = new Listcell();
				if("COMPLETED".equals(stepExecution.getExitStatus().getExitCode())) {
					lc.setStyle("color:green;");
					lc.setLabel(stepExecution.getExitStatus().getExitCode());
				} else if(this.jobExecution.isRunning() && 
						"EXECUTING".equals(stepExecution.getExitStatus().getExitCode())) {
					lc.setImage("/images/stepExecution.gif");	
				} else if(!this.jobExecution.isRunning() && 
						"EXECUTING".equals(stepExecution.getExitStatus().getExitCode())){
					lc.setStyle("color:red;");
					lc.setLabel(this.jobExecution.getExitStatus().getExitCode());
				} else if ("FAILED".equals(stepExecution.getExitStatus().getExitCode())) {
					this.btnStartJob.setLabel("Restart");
					this.btnStartJob.setTooltip("Restart");
					Image img_fail = new Image("/images/icons/ErrorFile.png");
					ComponentsCtrl.applyForward(img_fail, "onClick = onClickError");

					Hbox hbox = new Hbox();
					Label error = new Label(stepExecution.getExitStatus().getExitCode());
					error.setStyle("color:red;");
					hbox.appendChild(error);
					hbox.appendChild(new Space());
					hbox.appendChild(img_fail);
					hbox.setParent(lc);
				} 

				if(!this.jobExecution.isRunning() && 
						"FAILED".equals(stepExecution.getExitStatus().getExitCode())) {
					btnStaleJob.setDisabled(false);
				}
				lc.setParent(item);
				
				item.setAttribute("data", stepExecution);		
				ComponentsCtrl.applyForward(item, "onDoubleClick=onStepItemDoubleClicked");
				listBoxStepExecution.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	public void onClickError(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		final Listitem item = (Listitem) ((ForwardEvent) event).getOrigin().getTarget().getParent().getParent().getParent();

		if (item != null) {
			StepExecution stepExecution = (StepExecution) item.getAttribute("data");
			if (stepExecution != null) {
				Filedownload.save(stepExecution.getExitStatus().getExitDescription(), 
						"text/plain", stepExecution.getStepName());
			}
		}

		logger.debug("Leacing" + event.toString());
	}

	public void onStepItemDoubleClicked(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the selected Process object
		final Listitem item = this.listBoxStepExecution.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final StepExecution stepExecution = (StepExecution) item.getAttribute("data");

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("step", stepExecution);
			map.put("batchAdminCtrl", this);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Batch/StepDetails.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$lock(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (this.lock.isChecked()) {
			this.timer.stop();
		} else {
			this.timer.start();
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public PFSBatchAdmin getPfsBatchAdmin() {
		return pfsBatchAdmin;
	}
	public void setPfsBatchAdmin(PFSBatchAdmin pfsBatchAdmin) {
		this.pfsBatchAdmin = pfsBatchAdmin;
	}

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}
	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}
	
}
