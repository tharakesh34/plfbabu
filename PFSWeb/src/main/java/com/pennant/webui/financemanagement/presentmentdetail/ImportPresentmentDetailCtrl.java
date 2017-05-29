package com.pennant.webui.financemanagement.presentmentdetail;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.ProcessExecution;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;
import com.pennanttech.interfacebajaj.fileextract.service.FileExtractService;
import com.pennanttech.pff.core.Literal;

public class ImportPresentmentDetailCtrl extends GFCBaseCtrl<Object> {

	private static final long serialVersionUID = 4783031677099154138L;

	private final static Logger logger = Logger.getLogger(ImportPresentmentDetailCtrl.class);

	protected Window window_ImportPresentmentDetails;
	protected Button btnUpload;
	protected Button btnSave;
	protected Textbox txtFileName;
	protected Timer timer;

	protected ProcessExecution importPresentments;
	private PresentmentDetailExtract fileImport;
	private FileExtractService<PresentmentDetailExtract> presentmentExtractService;
	String errorMsg = null;

	Media media;

	public ImportPresentmentDetailCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ImportPresentmentDetails(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		this.importPresentments.setProcess(PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT);
		String status = PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.getStatus();
		timer.start();
		this.importPresentments.render();
		if (PennantConstants.FILESTATUS_STARTING.equals(status) || PennantConstants.FILESTATUS_FAILED.equals(status) || PennantConstants.FILESTATUS_SUCCESS.equals(status)) {
			this.btnSave.setDisabled(false);
			this.timer.stop();
			this.btnUpload.setDisabled(false);
		} else if (PennantConstants.FILESTATUS_EXECUTING.equals(status)) {
			this.btnSave.setDisabled(true);
			this.btnUpload.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This Method/Event for getting the uploaded document should be comma separated values and then read the document
	 * and setting the values to the Lead VO and added those vos to the List and it also shows the information about
	 * where we go the wrong data
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnUpload(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		txtFileName.setText("");
		errorMsg = null;
		Media media = event.getMedia();
		txtFileName.setText(media.getName());
		try {
			setFileImportData();
			fileImport.setMedia(media);
			fileImport.load(true);
			renderPannel();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			MessageUtil.showErrorMessage(e.getMessage());
		}
		
		logger.debug(Literal.LEAVING);
	}

	private void setFileImportData() throws Exception {
		logger.debug(Literal.ENTERING);
		
		if (fileImport == null) {
			fileImport = presentmentExtractService.getFileExtract(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		}
		
		logger.debug(Literal.LEAVING);
	}

	private void renderPannel() {
		logger.debug(Literal.ENTERING);
		
		renderPannel(fileImport);
		importPresentments.render();
		
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		doValidations();

		try {
			if (errorMsg != null) {
				throw new Exception(errorMsg);
			}
			doSave();
		} catch (Exception e) {
			errorMsg = e.getMessage();
			MessageUtil.showErrorMessage(e.getMessage());
			return;
		}
		
		logger.debug(Literal.LEAVING);
	}

	private void doValidations() {
		logger.debug(Literal.ENTERING);
		
		if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
			throw new WrongValueException(this.txtFileName, Labels.getLabel("empty_file"));
		}
		
		logger.debug(Literal.LEAVING);
	}

	private void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (importPresentments.getChildren() != null) {
			importPresentments.getChildren().clear();
		}
		Thread thread = new Thread(fileImport);
		timer.start();
		thread.start();
		Thread.sleep(1000);

		logger.debug(Literal.LEAVING);
	}

	public void onTimer$timer(Event event) {
		Events.postEvent("onCreate", this.window_ImportPresentmentDetails, event);
	}

	public void renderPannel(PresentmentDetailExtract extractDetails) {
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.reset();
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setFileName(extractDetails.getFile().getName());
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setActualCount(extractDetails.getTotalRecords());
		PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.setExecutionName("PRESENTMENT_RESPONSE_IMPORT");
	}

	public void setPresentmentExtractService(FileExtractService<PresentmentDetailExtract> presentmentExtractService) {
		this.presentmentExtractService = presentmentExtractService;
	}

}
