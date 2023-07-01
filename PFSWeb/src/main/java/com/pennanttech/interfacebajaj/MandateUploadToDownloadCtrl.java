package com.pennanttech.interfacebajaj;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.ConfigUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.MandateProcesses;

public class MandateUploadToDownloadCtrl extends GFCBaseCtrl<Configuration> {
	private static final long serialVersionUID = 1297405999029019920L;
	private static final Logger logger = LogManager.getLogger(MandateUploadToDownloadCtrl.class);
	protected Window window_MandateUploadToDownloadCtrl;
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
	private List<ValueLabel> serverFiles = null;
	private long userId;
	private Configuration config = null;
	private MandateProcesses mandateProcesses;
	private MandateProcesses defaultMandateProcess;
	private DataEngineStatus MANDATES_UPLOADTODOWNLOAD = new DataEngineStatus("MANDATES_UPLOADTODOWNLOAD");

	/**
	 * default constructor.<br>
	 */
	public MandateUploadToDownloadCtrl() {
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
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_MandateUploadToDownloadCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window);
		List<ValueLabel> menuList = new ArrayList<>();
		userId = getUserWorkspace().getUserDetails().getLoginId();
		List<Configuration> configList = dataEngineConfig.getMenuList(true);
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		for (Configuration config : configList) {
			String configName = config.getName();
			ValueLabel valueLabel;
			valueLabel = new ValueLabel(config.getName(), config.getName());
			if ("MANDATES_UPLOADTODOWNLOAD".equals(configName)) {
				if ("MANDATES_UPLOADTODOWNLOAD".equals(configName)) {
					MANDATES_UPLOADTODOWNLOAD = dataEngineConfig.getLatestExecution("MANDATES_UPLOADTODOWNLOAD");
					valueLabel = new ValueLabel(configName, "Mandate Upload To Download");
					doFillPanel(config, MANDATES_UPLOADTODOWNLOAD);
					menuList.add(valueLabel);
				}
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
				this.btnImport.setDisabled(false);
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
		} catch (Exception e) {
			exceptionTrace(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 */
	private void fileConfigurationSetup() {
		logger.debug(Literal.ENTERING);

		String path = config.getUploadPath();
		String uploadLoc = config.getUploadLocation();

		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}

		if (StringUtils.equalsIgnoreCase(ConfigUtil.CLIENT_FILE_LOCATION, uploadLoc)) {
			setComponentsVisibility(true);
		} else if (StringUtils.equalsIgnoreCase(ConfigUtil.SERVER_FILE_LOCATION, uploadLoc)) {
			setComponentsVisibility(false);
			serverFiles = new ArrayList<>();
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
			hbox = item.get(0);
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
	 */
	public void onClick$btnImport(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.fileConfiguration.getSelectedIndex() == 0) {
			MessageUtil.showError("Please select file configuration.");
			return;
		}

		if (media == null) {
			MessageUtil.showError("Please upload any file.");
			return;
		}
		try {
			Thread thread;
			thread = new Thread(new ProcessData(userId, MANDATES_UPLOADTODOWNLOAD));
			thread.start();
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
			return;
		}

		logger.debug(Literal.LEAVING);
	}

	private void exceptionTrace(Exception e) {
		fileConfiguration.setValue(PennantConstants.List_Select);
		fileConfiguration.setSelectedIndex(0);
		MessageUtil.showError(e.getMessage());
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
		if (!(StringUtils.endsWithIgnoreCase(mediaName, extension))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_ext", new String[] { extension }));

			media = null;
			return;
		}

		// Validate the file prefix.
		if (prefix != null && !(StringUtils.startsWith(mediaName, prefix))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_prefix", new String[] { prefix }));
			media = null;
			return;
		}
		// check the file row count
		Workbook workbook = null;
		try (InputStream fis = media.getStreamData()) {
			if (mediaName.endsWith(".xls")) {
				workbook = new HSSFWorkbook(fis);
			} else {
				workbook = new XSSFWorkbook(fis);
			}
			try {
				Sheet firstSheet = workbook.getSheet(config.getWorksheet());
				if (firstSheet == null) {
					throw new AppException(String.format("%s Work Sheet not found", config.getWorksheet()));
				}
				int rowCount = firstSheet.getPhysicalNumberOfRows();
				if (rowCount > 1000) {
					MessageUtil.showError("Can't allow more than 1000 records for a sheet");
					return;
				}
			} catch (Exception e) {
				logger.debug(e);
			}
			this.fileName.setText(mediaName);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void onChange$serverFileName(Event event) {
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
					if (ExecutionStatus.I.name().equals(status)) {
						this.btnImport.setDisabled(true);
						this.btnFileUpload.setDisabled(true);
					} else {
						this.btnImport.setDisabled(false);
						this.btnFileUpload.setDisabled(false);
					}
					pe.render();
				}
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

		@Override
		public void run() {
			try {
				getMandateProcess().processUploadToDownLoadFile(userId, file, media, status);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	/**
	 * This method is to validate the upload document formats like .EXE,.BAT,.SH.
	 * 
	 * @param Media
	 * @return boolean
	 */
	public static boolean uploadDocFormatValidation(final Media media) {

		if (media != null) {
			String filenamesplit[] = media.getName().split("\\.");
			if (filenamesplit.length >= 2) {
				for (int i = 0; i < filenamesplit.length; i++) {
					if (filenamesplit[i] != null && (filenamesplit[i].equalsIgnoreCase("exe")
							|| filenamesplit[i].equalsIgnoreCase("bat") || filenamesplit[i].equalsIgnoreCase("sh")
							|| filenamesplit[i].equalsIgnoreCase("jpg") || filenamesplit[i].equalsIgnoreCase("jpeg")
							|| filenamesplit[i].equalsIgnoreCase("png") || filenamesplit[i].equalsIgnoreCase("rar")
							|| filenamesplit[i].equalsIgnoreCase("zip") || filenamesplit[i].equalsIgnoreCase("msg")
							|| filenamesplit[i].equalsIgnoreCase("doc") || filenamesplit[i].equalsIgnoreCase("docx")
							|| filenamesplit[i].equalsIgnoreCase("ppt") || filenamesplit[i].equalsIgnoreCase("pptx")
							|| filenamesplit[i].equalsIgnoreCase("java") || filenamesplit[i].equalsIgnoreCase("csv")
							|| filenamesplit[i].equalsIgnoreCase("txt"))) {
						MessageUtil.showError(Labels.getLabel("Mandate_Supported_Document"));
						return false;
					}
				}
			}
		}
		return true;
	}

	public void validateMandateStatus(String status) {
		logger.debug(Literal.ENTERING);
		// validate status
		if (StringUtils.isNotBlank(status)) {
			List<ValueLabel> statusList = MandateUtil.getMandateStatus();
			boolean sts = false;
			for (ValueLabel value : statusList) {
				if (StringUtils.equals(value.getValue(), status)) {
					sts = true;
					break;
				}
			}
			if (!sts) {
				MessageUtil.showError(status + " INVALID status.");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	@Autowired(required = false)
	@Qualifier(value = "mandateProcesses")
	public void setMandateProces(MandateProcesses mandateProcesses) {
		this.mandateProcesses = mandateProcesses;
	}

	@Autowired
	public void setDefaultMandateProcess(MandateProcesses defaultMandateProcess) {
		this.defaultMandateProcess = defaultMandateProcess;
	}

	private MandateProcesses getMandateProcess() {
		return mandateProcesses == null ? defaultMandateProcess : mandateProcesses;
	}

}