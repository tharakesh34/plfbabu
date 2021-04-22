package com.pennant.webui.financemanagement.insurance;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ImportInsuranceDetailCtrl extends GFCBaseCtrl<InsuranceDetails> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(ImportInsuranceDetailCtrl.class);

	protected Window window_ImportInsuranceDetails;
	protected Button btnUpload;
	protected Button btnSave;
	protected ExtendedCombobox vasManufacturer;
	protected Textbox txtFileName;
	protected Timer timer;
	protected Rows panelRows;

	private InsuranceFileImportService insuranceFileImportService;

	private String errorMsg = null;
	protected DataEngineConfig dataEngineConfig;
	private Configuration config = null;
	private Media media = null;
	private DataEngineStatus INSURANCE_FILE_IMPORT_STATUS = new DataEngineStatus("INSURANCE_FILE_IMPORT");

	public ImportInsuranceDetailCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ImportInsuranceDetails(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_ImportInsuranceDetails);
		doSetFieldProiperties();

		config = dataEngineConfig.getConfigurationByName("INSURANCE_FILE_IMPORT");
		doFillPanel(config, INSURANCE_FILE_IMPORT_STATUS);

		logger.debug(Literal.LEAVING);

	}

	private void doSetFieldProiperties() {
		this.vasManufacturer.setMandatoryStyle(true);
		this.vasManufacturer.setModuleName("VehicleDealer");
		this.vasManufacturer.setValueColumn("DealerId");
		this.vasManufacturer.setValueType(DataType.LONG);
		this.vasManufacturer.setDescColumn("DealerName");
		this.vasManufacturer.setValidateColumns(new String[] { "DealerId" });
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

		errorMsg = null;
		setMedia(event.getMedia());

		if (!MediaUtil.isCsv(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "csv" }));
			return;
		}
		txtFileName.setText(media.getName());

		logger.debug(Literal.LEAVING);
	}

	private void doValidations() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.vasManufacturer);
		Clients.clearWrongValue(this.txtFileName);

		this.vasManufacturer.setErrorMessage("");
		this.txtFileName.setErrorMessage("");

		if (StringUtils.trimToNull(this.vasManufacturer.getValue()) == null) {
			throw new WrongValueException(this.vasManufacturer, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_ImportInsuranceDetails_CompanyID/VASManufacturer.value") }));
		}

		if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
			throw new WrongValueException(this.txtFileName, Labels.getLabel("empty_file"));
		}

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

	private void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		ProcessData processData = new ProcessData(getUserWorkspace().getLoggedInUser(), INSURANCE_FILE_IMPORT_STATUS,
				Long.valueOf(this.vasManufacturer.getValue()));
		this.btnSave.setDisabled(true);
		Thread thread = new Thread(processData);
		thread.start();
		Thread.sleep(1000);

		logger.debug(Literal.LEAVING);
	}

	public class ProcessData extends Thread {
		private LoggedInUser userDetails;
		private DataEngineStatus status;
		private long providerId;

		public ProcessData(LoggedInUser userDetails, DataEngineStatus status, long providerId) {
			this.userDetails = userDetails;
			this.status = status;
			this.providerId = providerId;
		}

		@Override
		public void run() {
			try {
				getInsuranceFileImportService().processFile(userDetails, status, getMedia(), providerId);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				MessageUtil.showError(e.getMessage());
			}
		}
	}

	private void doFillPanel(Configuration config, DataEngineStatus ds) {
		ProcessExecution pannel = new ProcessExecution();
		pannel.setId(config.getName());
		pannel.setBorder("normal");
		pannel.setTitle(config.getName());
		pannel.setWidth("480px");
		pannel.setProcess(ds);
		pannel.render();

		Row rows = (Row) panelRows.getLastChild();

		if (rows == null) {
			Row row = new Row();
			row.setStyle("overflow: visible !important");
			Hbox hbox = new Hbox();
			hbox.setAlign("center");
			hbox.appendChild(pannel);
			row.appendChild(hbox);
			panelRows.appendChild(row);
		} else {
			Hbox hbox = null;
			List<Hbox> item = rows.getChildren();
			hbox = (Hbox) item.get(0);
			if (hbox.getChildren().size() == 2) {
				rows = new Row();
				rows.setStyle("overflow: visible !important");
				hbox = new Hbox();
				hbox.setAlign("center");
				hbox.appendChild(pannel);
				rows.appendChild(hbox);
				panelRows.appendChild(rows);
			} else {
				hbox.appendChild(pannel);
			}
		}
	}

	public void onTimer$timer(Event event) {
		List<Row> rows = this.panelRows.getChildren();
		for (Row row : rows) {
			List<Hbox> hboxs = row.getChildren();
			for (Hbox hbox : hboxs) {
				List<ProcessExecution> list = hbox.getChildren();
				for (ProcessExecution pe : list) {
					String status = pe.getProcess().getStatus();

					if (ExecutionStatus.I.name().equals(status)) {
						this.btnUpload.setDisabled(true);
						this.btnSave.setDisabled(true);
					} else {
						this.btnUpload.setDisabled(false);
						this.btnSave.setDisabled(false);
					}
					pe.render();
				}
			}
		}
	}

	// Getters and setters
	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
	}

	public InsuranceFileImportService getInsuranceFileImportService() {
		return insuranceFileImportService;
	}

	public void setInsuranceFileImportService(InsuranceFileImportService insuranceFileImportService) {
		this.insuranceFileImportService = insuranceFileImportService;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

}
