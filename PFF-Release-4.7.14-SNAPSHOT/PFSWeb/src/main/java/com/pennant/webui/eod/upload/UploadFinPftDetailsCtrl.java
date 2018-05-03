package com.pennant.webui.eod.upload;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.ProcessExecution;
import com.pennant.app.eod.service.UploadFinPftDetailService;
import com.pennant.app.eod.upload.UploadProfitDetailProcess;
import com.pennant.webui.util.GFCBaseCtrl;


public class UploadFinPftDetailsCtrl  extends GFCBaseCtrl<Object> {

	private static final long serialVersionUID = 1L;
	public static final Logger logger = Logger.getLogger(UploadFinPftDetailsCtrl.class);

	protected Window window_UploadFinPftDetails;
	protected Button    btnUpload;      //autowire
	protected Timer timer;
	
	ProcessExecution processs;
	
	private UploadFinPftDetailService uploadFinPftDetailService; 
	UploadProfitDetailProcess process = UploadProfitDetailProcess.getInstance();
		
	public UploadFinPftDetailsCtrl(){
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
	
	int calcPercentage = 0;
	int postingPercentage = 0;
	
	
	public void onCreate$window_UploadFinPftDetails(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_UploadFinPftDetails);

		logger.debug("Entering : "+event);
		
		if(!"".equals(UploadProfitDetailProcess.RUNNING)) {
			this.btnUpload.setDisabled(true);
			doFillExecutions(process);
		} else {
			timer.stop();
			this.btnUpload.setDisabled(false);
			
		}
		
		if("COMPLETED".equals(UploadProfitDetailProcess.RUNNING)) {
			Clients.showNotification(Labels.getLabel("labels_ProcessCompleted.value"),  "info", null, null, -1);
			UploadProfitDetailProcess.RUNNING = "";
		}
		
		if("FAILED".equals(UploadProfitDetailProcess.RUNNING)) {
			doFillExecutions(process);
			Clients.showNotification(Labels.getLabel("labels_ProcessFailed.value"),  "info", null, null, -1);
			UploadProfitDetailProcess.RUNNING = "";
			timer.stop();
			this.btnUpload.setDisabled(false);
		}
		
		logger.debug("Leaving  : "+event);
	}


	public void onClick$btnUpload(Event event){
		logger.debug("Entering : "+event);
		
		try {
			process = UploadProfitDetailProcess.getInstance(getUploadFinPftDetailService());
			this.timer.start();
			this.btnUpload.setDisabled(true);
			process.start();
		}  catch (Exception e) {
			logger.error("Exception: ", e);
		}finally {
			this.btnUpload.setDisabled(false);
		}
		logger.debug("Leaving  : "+event);
	}
	
	
	/**
	 * Method for Rendering Step Execution Details List
	 * @param stepExecution
	 * @throws Exception
	 */
	private void doFillExecutions(UploadProfitDetailProcess process) throws Exception {
		logger.debug("Entering");
		this.processs.setProcess(process.getStatus());	
		this.processs.render();
		logger.debug("Leaving");
	}
	
	
	public void onTimer$timer(Event event) {
		logger.debug("Entering" + event.toString());
		Events.postEvent("onCreate", this.window_UploadFinPftDetails, event);
		logger.debug("Leaving" + event.toString());
	}
	
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public UploadFinPftDetailService getUploadFinPftDetailService() {
		return uploadFinPftDetailService;
	}


	public void setUploadFinPftDetailService(
			UploadFinPftDetailService uploadFinPftDetailService) {
		this.uploadFinPftDetailService = uploadFinPftDetailService;
	}

	
}
