package com.pennant.webui.cersai.cersaiupload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.DataEngineConstants.ParserNames;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.ConfigUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.CersaiProcess;

public class CersaiDataFileImportCtrl extends GFCBaseCtrl<Configuration> {
	private static final long serialVersionUID = 1297405999029019920L;
	private static final Logger logger = LogManager.getLogger(CersaiDataFileImportCtrl.class);

	protected Window window_CersaiDataFileImportCtrl;
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

	private long userId;

	private Configuration config = null;

	@Autowired(required = false)
	private CersaiProcess cersaiProcess;
	private DataEngineStatus CERSAI_IMPORT = null;

	public CersaiDataFileImportCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FileDBInterface";
	}

	public void onCreate$window_CersaiDataFileImportCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window);

		ValueLabel valueLabel = null;
		List<ValueLabel> menuList = new ArrayList<ValueLabel>();
		userId = getUserWorkspace().getUserDetails().getLoginId();
		List<Configuration> configList = dataEngineConfig.getMenuList(true);

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		for (Configuration configuration : configList) {
			String configName = configuration.getName();
			if (configName.startsWith("CERSAI_") && configName.endsWith("_IMPORT")) {
				this.config = configuration;
				CERSAI_IMPORT = dataEngineConfig.getLatestExecution(configuration.getName());
				valueLabel = new ValueLabel(configName, configName);
				doFillPanel(configuration, CERSAI_IMPORT);
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
			String fileConfig = this.fileConfiguration.getSelectedItem().getLabel();
			fileName.setValue("");
			serverFileName.setValue("");
			row1.setVisible(false);
			if (!StringUtils.equals(Labels.getLabel("Combo.Select"), fileConfig)) {
				this.btnImport.setDisabled(false);
				config = dataEngineConfig.getConfigurationByName(fileConfig);
				CERSAI_IMPORT = dataEngineConfig.getLatestExecution(config.getName());
				doFillPanel(config, CERSAI_IMPORT);
				media = null;
			} else {
				this.btnImport.setDisabled(true);
				media = null;
				return;
			}
			setFileConfig();
		} catch (Exception e) {
			exceptionTrace(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setFileConfig() throws InterruptedException {
		try {
			if (!ParserNames.DB.name().equals(config.getParserName())) {
				fileConfigurationSetup();
			}
		} catch (Exception e) {
			exceptionTrace(e);
		}
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	private void fileConfigurationSetup() throws Exception {
		logger.debug(Literal.ENTERING);

		String path = config.getUploadPath();
		String uploadLoc = config.getUploadLocation();
		List<ValueLabel> serverFiles = null;

		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}

		if (StringUtils.equalsIgnoreCase(ConfigUtil.CLIENT_FILE_LOCATION, uploadLoc)) {
			setComponentsVisibility(true);
		} else if (StringUtils.equalsIgnoreCase(ConfigUtil.SERVER_FILE_LOCATION, uploadLoc)) {
			setComponentsVisibility(false);
			serverFiles = new ArrayList<ValueLabel>();
			File writeFile = new File(path);
			File[] files = writeFile.listFiles();

			if (files != null) {
				for (File file2 : files) {
					if (StringUtils.startsWith(file2.getName(), config.getFilePrefixName())) {
						serverFiles.add(new ValueLabel(file2.getPath(), file2.getName()));
					}
				}
				fillComboBox(serverFileName, "", serverFiles, "");
			}
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		if (media == null) {
			MessageUtil.showError("Please upload any File.");
			return;
		}
		if (CERSAI_IMPORT != null && ExecutionStatus.I.name().equals(CERSAI_IMPORT.getStatus())) {
			MessageUtil.showError("Export is in progress for the selected configuration.");
			return;
		}

		this.btnImport.setDisabled(true);
		try {
			Thread thread = new Thread(new ProcessData(userId, CERSAI_IMPORT));
			thread.start();
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
			return;
		}

		logger.debug(Literal.LEAVING);
	}

	private void exceptionTrace(Exception e) throws InterruptedException {
		fileConfiguration.setValue(PennantConstants.List_Select);
		fileConfiguration.setSelectedIndex(0);
		this.btnImport.setDisabled(true);
		MessageUtil.showError(e.getMessage());
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		fileName.setText("");
		media = event.getMedia();

		if (!(StringUtils.endsWith(media.getName(), config.getFileExtension()))) {
			MessageUtil.showError("Invalid file format.");
			media = null;
			return;
		}

		fileName.setText(media.getName());
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
					pe.setProcess(CERSAI_IMPORT);

					String status = pe.getProcess().getStatus();
					if (ExecutionStatus.I.name().equals(status)) {
						this.btnImport.setDisabled(true);
					} else {
						this.btnImport.setDisabled(false);
					}

					String fileConfig = this.fileConfiguration.getSelectedItem().getLabel();
					if (!StringUtils.equals(Labels.getLabel("Combo.Select"), fileConfig)) {
						this.btnImport.setDisabled(false);
					} else {
						this.btnImport.setDisabled(true);
					}
					pe.render();
				}
			}
		}
	}

	private void doFillPanel(Configuration config, DataEngineStatus ds) {
		// Clear the existing rows.
		panelRows.getChildren().clear();
		ProcessExecution pannelExecution = new ProcessExecution();
		pannelExecution.setId(config.getName());
		pannelExecution.setBorder("normal");
		pannelExecution.setTitle(config.getName());
		pannelExecution.setWidth("480px");
		pannelExecution.setProcess(ds);
		pannelExecution.render();

		Row rows = (Row) panelRows.getLastChild();

		if (rows == null) {
			Row row = new Row();
			row.setStyle("overflow: visible !important");
			Hbox hbox = new Hbox();
			hbox.setAlign("center");
			hbox.appendChild(pannelExecution);
			row.appendChild(hbox);
			panelRows.appendChild(row);
		} else {
			Hbox hbox = null;
			List<Hbox> item = rows.getChildren();
			hbox = item.get(0);
			if (hbox.getChildren().size() == 2) {
				rows = new Row();
				rows.setStyle("overflow: visible !important");
				hbox = new Hbox();
				hbox.setAlign("center");
				hbox.appendChild(pannelExecution);
				rows.appendChild(hbox);
				panelRows.appendChild(rows);
			} else {
				hbox.appendChild(pannelExecution);
			}
		}
	}

	public class ProcessData implements Runnable {
		private long userId;
		private DataEngineStatus status;

		public ProcessData(long userId, DataEngineStatus status) {
			this.userId = userId;
			this.status = status;
		}

		byte[] fileData;

		@Override
		public void run() {
			try {
				if (cersaiProcess != null) {
					if (file == null) {
						file = new File(media.getName());
					}
					cersaiProcess.processResponseFile(userId, file, media, status);
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}
