package com.pennanttech.interfacebajaj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.DataEngineConstants.ParserNames;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.ConfigUtil;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.file.service.FileService;

public class MandateDataImportCtrl extends GFCBaseCtrl<Configuration> {
	private static final long	serialVersionUID	= 1297405999029019920L;
	private static final Logger	logger				= Logger.getLogger(MandateDataImportCtrl.class);

	protected Window			window_MandateDataImportCtrl;
	protected Button			btnImport;
	protected Button			btnFileUpload;

	protected Textbox			fileName;
	protected Combobox			fileConfiguration;
	protected Combobox			serverFileName;

	protected Row				row1;
	protected Rows				panelRows;

	protected Timer				timer;
	private Media				media				= null;
	private File				file				= null;

	protected DataEngineConfig	dataEngineConfig;
	private List<ValueLabel>	serverFiles			= null;

	private long				userId;

	private Configuration		config				= null;

	@Autowired
	private FileService			mandateResponseFileService;

	/**
	 * default constructor.<br>
	 */
	public MandateDataImportCtrl() {
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
	public void onCreate$window_MandateDataImportCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window);

		ValueLabel valueLabel = null;
		List<ValueLabel> menuList = new ArrayList<ValueLabel>();
		userId = getUserWorkspace().getUserDetails().getLoginId();

		String[] parsers = new String[2];
		parsers[0] = ParserNames.READER.name();
		parsers[1] = ParserNames.DBREADER.name();
		List<Configuration> configList = dataEngineConfig.getMenuList(parsers, true);

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		for (Configuration config : configList) {
			String configName = config.getName();
			valueLabel = new ValueLabel(config.getName(), config.getName());
			if ((StringUtils.equals(configName, "MANDATES_IMPORT"))) {
				this.config = config;
				menuList.add(valueLabel);
				BajajInterfaceConstants.MANDATE_INMPORT_STATUS = dataEngineConfig.getLatestExecution(config.getName());
				doFillPanel();
				break;
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
			} else {
				this.btnImport.setDisabled(true);
				return;
			}
			try {
				if (!ParserNames.DB.name().equals(config.getParserName())) {
					fileConfigurationSetup();
				}
			} catch (Exception e) {
				exceptionTrace(e);
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
		logger.debug(Literal.ENTERING);

		String path = config.getUploadPath();
		String uploadLoc = config.getUploadLocation();

		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}

		if (StringUtils.equalsIgnoreCase(ConfigUtil.CLIENT_FILE_LOCATION, uploadLoc)) {
			setComponentsVisibility(true);
		} else if (StringUtils.equalsIgnoreCase(ConfigUtil.SERVER_FILE_LOCATION,
				uploadLoc)) {
			setComponentsVisibility(false);
			serverFiles = new ArrayList<ValueLabel>();
			File file = new File(path);
			File[] files = file.listFiles();

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
			MessageUtil.showError("Please upload any file.");
			return;
		}

		if (BajajInterfaceConstants.MANDATE_INMPORT_STATUS != null
				&& ExecutionStatus.I.name().equals(BajajInterfaceConstants.MANDATE_INMPORT_STATUS.getStatus())) {
			MessageUtil.showError("Export is in progress for the selected configuration.");
			return;
		}

		this.btnImport.setDisabled(true);
		try {
			Thread thread = new Thread(new ProcessData(userId, BajajInterfaceConstants.MANDATE_INMPORT_STATUS));
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

		if (!(StringUtils.endsWith(media.getName().toUpperCase(), ".CSV"))) {
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
					pe.setProcess(BajajInterfaceConstants.MANDATE_INMPORT_STATUS);

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

	private void doFillPanel() {
		ProcessExecution pannelExecution = new ProcessExecution();
		pannelExecution.setId(config.getName());
		pannelExecution.setBorder("normal");
		pannelExecution.setTitle(config.getName());
		pannelExecution.setWidth("480px");
		pannelExecution.setProcess(BajajInterfaceConstants.MANDATE_INMPORT_STATUS);
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
			hbox = (Hbox) item.get(0);
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
		private long				userId;
		private DataEngineStatus	status;

		public ProcessData(long userId, DataEngineStatus status) {
			this.userId = userId;
			this.status = status;
		}

		byte[]	fileData;

		@Override
		public void run() {
			try {
				mandateResponseFileService.processFile(userId, status, file, media, false);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}
