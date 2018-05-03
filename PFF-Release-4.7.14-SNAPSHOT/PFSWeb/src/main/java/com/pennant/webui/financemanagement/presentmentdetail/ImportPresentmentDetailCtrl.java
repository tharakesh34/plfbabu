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
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;
import com.pennanttech.interfacebajaj.fileextract.service.FileExtractService;
import com.pennanttech.pennapps.core.resource.Literal;

public class ImportPresentmentDetailCtrl extends GFCBaseCtrl<Object> {

	private static final long serialVersionUID = 4783031677099154138L;
	private static final Logger logger = Logger.getLogger(ImportPresentmentDetailCtrl.class);

	protected Window window_ImportPresentmentDetails;
	protected Button btnUpload;
	protected Button btnSave;
	protected Textbox txtFileName;
	protected Timer timer;
	protected Row panelRow;

	private ProcessExecution processExecution = null;
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

		if (processExecution == null) {
			processExecution = new ProcessExecution();
			createPanel(processExecution, PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT);
		}
		processExecution.setProcess(PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT);
		String status = PennantConstants.BATCH_TYPE_PRESENTMENT_IMPORT.getStatus();
		timer.start();
		processExecution.render();

		if (ExecutionStatus.F.name().equals(status) || ExecutionStatus.I.name().equals(status)) {
			if (ExecutionStatus.S.name().equals(status) || ExecutionStatus.F.name().equals(status)) {
				btnSave.setDisabled(false);
				timer.stop();
			}
			btnUpload.setDisabled(false);
		} else if (ExecutionStatus.F.name().equals(status)) {
			btnSave.setDisabled(true);
			btnUpload.setDisabled(true);
		}

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
			String fileName = StringUtils.lowerCase(media.getName());
			if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
				fileImport = null;
				txtFileName.setText(media.getName());
				setFileImportData(media.getName().substring(media.getName().lastIndexOf('.')));
				fileImport.setExcelMedia(media);
				fileImport.loadExcelFile(true);
				renderPannel();
			} else {
				throw new Exception("Invalid file format.");
			}

		} catch (Exception e) {
			errorMsg = e.getMessage();
			MessageUtil.showError(e.getMessage());
		}

		logger.debug(Literal.LEAVING);
	}
	

	private void setFileImportData(String contentType) throws Exception {
		logger.debug(Literal.ENTERING);

		if (fileImport == null) {
			fileImport = presentmentExtractService.getFileExtract(getUserWorkspace().getLoggedInUser().getUserId(),contentType);
		}

		logger.debug(Literal.LEAVING);
	}

	private void renderPannel() {
		logger.debug(Literal.ENTERING);

		presentmentExtractService.renderPannel(fileImport);
		processExecution.render();

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
			MessageUtil.showError(e.getMessage());
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

		if (processExecution.getChildren() != null) {
			processExecution.getChildren().clear();
		}
		Thread thread = new Thread(fileImport);
		timer.start();
		thread.start();
		Thread.sleep(1000);

		logger.debug(Literal.LEAVING);
	}

	private void createPanel(ProcessExecution pannel, DataEngineStatus dataEngineStatus) {
		logger.debug(Literal.ENTERING);

		pannel.setId("Presentment Details");
		pannel.setBorder("normal");
		pannel.setTitle("Presentment Details");
		pannel.setWidth("460px");
		pannel.setProcess(dataEngineStatus);
		pannel.render();
		panelRow.setStyle("overflow: visible !important");
		Hbox hbox = new Hbox();
		hbox.setAlign("center");
		hbox.appendChild(pannel);
		panelRow.appendChild(hbox);

		logger.debug(Literal.LEAVING);
	}

	public void onTimer$timer(Event event) {
		Events.postEvent("onCreate", this.window_ImportPresentmentDetails, event);
	}

	public void setPresentmentExtractService(FileExtractService<PresentmentDetailExtract> presentmentExtractService) {
		this.presentmentExtractService = presentmentExtractService;
	}
}
