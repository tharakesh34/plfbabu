package com.pennanttech.interfacebajaj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.FinAutoApprovalProcess;
import com.pennant.app.core.InstBasedSchdProcess;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.ConfigUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.DisbursementResponse;

public class DisbursementDataImportCtrl extends GFCBaseCtrl<Configuration> {
	private static final Logger logger = Logger.getLogger(DisbursementDataImportCtrl.class);
	private static final long serialVersionUID = 1297405999029019920L;

	protected Window window_DisbursementDataImportCtrl;
	protected Button btnImport;
	protected Button btnFileUpload;

	protected Textbox fileName;
	protected Combobox fileConfiguration;
	protected Combobox serverFileName;

	protected Row row1;
	protected Rows panelRows;

	protected Timer timer;
	private Media media = null;
	private File file = null;

	protected DataEngineConfig dataEngineConfig;
	private Configuration config = null;
	private List<ValueLabel> serverFiles = null;

	private long userId;
	private DataEngineStatus DISB_IMPORT_STATUS = null;

	private DataEngineStatus DISBURSEMENT_FILE_IMPORT_STATUS = null;
	private boolean allowPaymentType;
	private DisbursementResponse defaultDisbursementResponse;
	private DisbursementResponse disbursementResponse;

	@Autowired(required = false)
	private FinAutoApprovalProcess finAutoApprovalService;

	@Autowired(required = false)
	private InstBasedSchdProcess instBasedSchdProcess;

	/**
	 * default constructor.<br>
	 */
	public DisbursementDataImportCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FileDBInterface";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_DisbursementDataImportCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window);

		allowPaymentType = "Y"
				.equals(SysParamUtil.getValueAsString(SMTParameterConstants.DISBURSEMENT_RESPONSE_ALLOW_PAYMENT_TYPE))
						? true : false;

		ValueLabel valueLabel = null;
		List<ValueLabel> menuList = new ArrayList<ValueLabel>();
		userId = getUserWorkspace().getUserDetails().getLoginId();
		List<Configuration> configList = dataEngineConfig.getMenuList(true);

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		for (Configuration config : configList) {
			String configName = config.getName();
			if (configName.startsWith("DISB_") && configName.endsWith("_IMPORT")) {
				DISB_IMPORT_STATUS = dataEngineConfig.getLatestExecution(configName);
				valueLabel = new ValueLabel(configName, configName);
				doFillPanel(config, DISB_IMPORT_STATUS);
				menuList.add(valueLabel);

			}
		}

		fillComboBox(fileConfiguration, "", menuList, "");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$fileConfiguration(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			String fileConfig = this.fileConfiguration.getSelectedItem().getValue();
			fileName.setValue("");
			serverFileName.setValue("");
			row1.setVisible(false);
			if (!StringUtils.equals("#", fileConfig)) {
				if (!(ImplementationConstants.DISBURSEMENT_AUTO_UPLOAD
						&& SysParamUtil.isAllowed(SMTParameterConstants.DISBURSEMENT_AUTO_UPLOAD_JOB_ENABLED))) {
					this.btnImport.setDisabled(false);
				}
				config = dataEngineConfig.getConfigurationByName(fileConfig);
			} else {
				this.btnImport.setDisabled(true);
				return;
			}
			try {
				fileConfigurationSetup();
			} catch (Exception e) {
				exceptionTrace(e);
			}
			if (allowPaymentType) {
				DISBURSEMENT_FILE_IMPORT_STATUS = dataEngineConfig.getLatestExecution(fileConfig);
				//FIXME overriding the DISB_IMPORT_STATUS by selected file configuration
				DISB_IMPORT_STATUS = DISBURSEMENT_FILE_IMPORT_STATUS;
				doFillPanel(config, DISBURSEMENT_FILE_IMPORT_STATUS);
			}
		} catch (Exception e) {
			exceptionTrace(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	private void fileConfigurationSetup() throws Exception {
		String path = config.getUploadPath();
		String uploadLoc = config.getUploadLocation();

		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}

		if (ConfigUtil.CLIENT_FILE_LOCATION.equals(uploadLoc)) {
			setComponentsVisibility(true);
		} else if (ConfigUtil.SERVER_FILE_LOCATION.equals(uploadLoc)) {
			setComponentsVisibility(false);
			serverFiles = new ArrayList<ValueLabel>();
			File file = new File(path);
			File[] files = file.listFiles();

			if (files != null) {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						continue;
					}
					serverFiles.add(new ValueLabel(file2.getPath(), file2.getName()));
				}
				fillComboBox(serverFileName, "", serverFiles, "");
			}
		}
	}

	/**
	 * Setting the components visibility based on the flag
	 */
	private void setComponentsVisibility(boolean isVisible) {
		row1.setVisible(true);
		btnFileUpload.setVisible(isVisible);
		fileName.setVisible(isVisible);
		serverFileName.setVisible(!isVisible);
	}

	/**
	 * When user clicks on "btnImport"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnImport(Event event) throws InterruptedException {
		this.btnImport.setDisabled(false);

		if (media == null) {
			MessageUtil.showError("Please upload any file.");
			return;
		}
		try {

			ProcessData t1 = null;
			FinAutoApprove t2 = null;
			FinInstBasedSchd t3 = null;

			t1 = new ProcessData(userId, DISB_IMPORT_STATUS);

			t1.start();
			t1.join();

			t2 = new FinAutoApprove(userId, DISB_IMPORT_STATUS.getId());
			t2.start();
			t2.join();

			t3 = new FinInstBasedSchd(DISB_IMPORT_STATUS.getId());
			t3.start();

		} catch (Exception e) {
			MessageUtil.showError(e);
			return;
		}
	}

	private void exceptionTrace(Exception e) throws InterruptedException {
		fileConfiguration.setValue(PennantConstants.List_Select);
		fileConfiguration.setSelectedIndex(0);
		this.btnImport.setDisabled(true);
		MessageUtil.showError(e);
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		// Clear the file name.
		this.fileName.setText("");

		// Get the media of the selected file.
		media = event.getMedia();

		String mediaName = media.getName();
		// Get the selected configuration details.
		String prefix = config.getFilePrefixName();
		String extension = config.getFileExtension();

		// Validate the file extension.
		if (StringUtils.isNotEmpty(extension)) {
			if (!(StringUtils.endsWithIgnoreCase(mediaName, extension))) {
				MessageUtil.showError(Labels.getLabel("invalid_file_ext", new String[] { extension }));

				media = null;
				return;
			}
		}

		// Validate the file prefix.
		if (prefix != null && !(StringUtils.startsWith(mediaName, prefix))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_prefix", new String[] { prefix }));

			media = null;
			return;
		}

		this.fileName.setText(mediaName);
	}

	public void onChange$serverFileName(Event event) throws Exception {
		try {
			file = new File(this.serverFileName.getSelectedItem().getValue().toString());
		} catch (Exception e) {
			logger.error("Exception :", e);
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

					if ("DISB_OTHER_IMPORT".equals(pe.getProcess().getName())) {
						if (ExecutionStatus.I.name().equals(status)) {
							this.btnImport.setDisabled(true);
							this.btnFileUpload.setDisabled(true);
						} else {
							this.btnImport.setDisabled(false);
							this.btnFileUpload.setDisabled(false);
						}
					}
					pe.render();
				}
			}
		}
	}

	private void doFillPanel(Configuration config, DataEngineStatus ds) {
		// Clear the existing rows.
		panelRows.getChildren().clear();

		// Add the rows.
		ProcessExecution pannel = new ProcessExecution();
		pannel.setId(config.getName());
		pannel.setBorder("normal");
		pannel.setTitle(config.getName());
		pannel.setWidth("480px");
		pannel.setProcess(ds);
		pannel.render();

		Row row = new Row();
		row.setStyle("overflow: visible !important");
		Hbox hbox = new Hbox();
		hbox.setAlign("center");
		hbox.appendChild(pannel);
		row.appendChild(hbox);
		panelRows.appendChild(row);
	}

	public class ProcessData extends Thread {
		private long userId;
		private DataEngineStatus status;

		public ProcessData(long userId, DataEngineStatus status) {
			this.userId = userId;
			this.status = status;
		}

		@Override
		public void run() {
			try {
				getDisbursementResponse().processResponseFile(userId, status, file, media, false,
						getUserWorkspace().getLoggedInUser());
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	public class FinAutoApprove extends Thread {
		private long batchId;

		public FinAutoApprove(long userId, long batchId) {
			this.batchId = batchId;
		}

		@Override
		public void run() {
			try {
				finAutoApprovalService.checkForAutoApproval(getUserWorkspace().getLoggedInUser(), batchId);

			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	public class FinInstBasedSchd extends Thread {
		private long batchId;

		public FinInstBasedSchd(long batchId) {
			this.batchId = batchId;
		}

		@Override
		public void run() {
			try {
				instBasedSchdProcess.rebuildSchdBasedOnInst(getUserWorkspace().getLoggedInUser(), batchId);
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	private DisbursementResponse getDisbursementResponse() {
		return disbursementResponse == null ? defaultDisbursementResponse : disbursementResponse;
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

}