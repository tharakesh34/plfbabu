package com.pennanttech.framework.component.dataengine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.DataEngineConstants.ParserNames;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dbengine.DataEngineDBProcess;
import com.pennanttech.pff.core.App;

public class DataImportCtrl extends GFCBaseCtrl<Configuration> {

	private static final long serialVersionUID = 1297405999029019920L;
	private static final Logger logger = Logger.getLogger(DataImportCtrl.class);

	protected Window window_DataImportCtrl;

	protected Button btnImport;
	protected Button btnFileUpload;

	protected Textbox fileName;
	protected Combobox fileConfiguration;
	protected Combobox serverFileName;

	protected Row row1;
	protected Rows panelRows;

	protected Timer timer;
	private Media media = null;

	protected DataEngineConfig dataEngineConfig;
	private DataSource dataSource;
	private Configuration config = null;
	private List<ValueLabel> serverFiles = null;

	private long userId;

	/**
	 * default constructor.<br>
	 */
	public DataImportCtrl() {
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
	public void onCreate$window_DataImportCtrl(Event event) throws Exception {
		logger.debug("Entering");
		
		// Set the page level components.
		setPageComponents(window);

		ValueLabel valueLabel = null;
		List<ValueLabel> menuList = new ArrayList<ValueLabel>();
		userId = getUserWorkspace().getUserDetails().getLoginId();

		String[] parsers = new String[2];
		parsers[0] = ParserNames.READER.name();
		parsers[1] = ParserNames.DBREADER.name();
		List<Configuration> configList = dataEngineConfig.getMenuList(parsers);
		
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		
		for (Configuration config : configList) {
			valueLabel = new ValueLabel(String.valueOf(config.getParser()),config.getName());
			if (getUserWorkspace().isAllowed(config.getName())) {
				menuList.add(valueLabel);
				doFillExePanels(config,dataEngineConfig.getLatestExecution(config.getName()));
			}
		}
		fillComboBox(fileConfiguration, "", menuList, "");
		doSetFieldProperties();
		logger.debug("Leaving");
	}

	/**
	 * Setting the default properties of the fields
	 */
	private void doSetFieldProperties() {
		// Present no fields have default properties
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$fileConfiguration(Event event) throws Exception {
		logger.debug("Entering");
		
		try {
			String fileConfig = this.fileConfiguration.getSelectedItem().getLabel();
			String parserId = this.fileConfiguration.getSelectedItem().getValue().toString();
			
			fileName.setValue("");
			serverFileName.setValue("");
			row1.setVisible(false);
			if (!StringUtils.equals(Labels.getLabel("Combo.Select"), fileConfig)) {
				this.btnImport.setDisabled(false);
				config = dataEngineConfig.getConfiguration(fileConfig, Integer.valueOf(parserId));
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
		logger.debug("Leaving");
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	private void fileConfigurationSetup() throws Exception {
		logger.debug("Entering");

		String path = config.getUploadPath();
		String uploadLoc = config.getUploadLocation();
		
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		
		if (StringUtils.equalsIgnoreCase(ConfigUtil.CLIENT_FILE_LOCATION, uploadLoc)) {
			setComponentsVisibility(true);
		} else if (StringUtils.equalsIgnoreCase(ConfigUtil.SERVER_FILE_LOCATION, uploadLoc)) {
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
		logger.debug("Leaving");
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
		logger.debug("Entering");
		
		DataEngineStatus status = dataEngineConfig.getLatestExecution(config.getName());
		
		if (status != null && ExecutionStatus.I.name().equals(status.getStatus())) {
			MessageUtil.showErrorMessage("Export is in progress for the selected configuration.");
			return;
		}

		this.btnImport.setDisabled(true);
		try {
			Thread thread = new Thread(new ProcessData(userId, config));
			thread.start();
		} catch (Exception e) {
			MessageUtil.showErrorMessage(e.getMessage());
			return;
		}
		
		logger.debug("Leaving");
	}

	private void exceptionTrace(Exception e) throws InterruptedException {
		fileConfiguration.setValue(PennantConstants.List_Select);
		fileConfiguration.setSelectedIndex(0);
		this.btnImport.setDisabled(true);
		MessageUtil.showErrorMessage(e.getMessage());
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		fileName.setText("");
		media = event.getMedia();
		String type = media.getContentType();
		System.out.println("type :" + type);
		fileName.setText(media.getName());
	}

	public void onChange$serverFileName(Event event) throws Exception {
		try {
			new File(this.serverFileName.getSelectedItem().getValue().toString());
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
					pe.getProcess().getStatus();
					pe.render();
				}
			}
		}
	}

	private void doFillExePanels(Configuration config, DataEngineStatus ds) throws Exception {
		Configuration configuration = null;
		if (ds == null) {
			ds = new DataEngineStatus();
		}
		configuration = dataEngineConfig.getConfiguration(config.getName(), config.getParser());
		doFillPanel(ds, configuration);
	}
	
	private void doFillPanel(DataEngineStatus ds, Configuration config) {
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

	private DataEngineStatus getPannelExecution(Configuration config) {
		DataEngineStatus status = null;
		List<Row> rows = this.panelRows.getChildren();
		for (Row row : rows) {
			List<Hbox> hboxs = row.getChildren();
			for (Hbox hbox : hboxs) {
				List<ProcessExecution> list = hbox.getChildren();
				for (ProcessExecution pannelExecution : list) {
					if (pannelExecution.getId().equals(config.getName())) {
						status = pannelExecution.getProcess();
						status.setTotalRecords(0);
						status.setProcessedRecords(0);
						status.setRemarks("");
						status.setStatus("Loading....");
						status.setStartTime(null);
						status.setEndTime(null);
						break;
					}
					if (status != null) {
						break;
					}
				}
			}
			if (status != null) {
				break;
			}
		}
		return status;
	}

	public class ProcessData implements Runnable {
		private long userId;
		private Configuration config;

		public ProcessData(long userId, Configuration config) {
			this.userId = userId;
			this.config = config;
		}
		byte[] fileData;
		@Override
		public void run() {
			DataEngineStatus status = null;
			try {
				status = getPannelExecution(config);
				if (ParserNames.DB.name().equals(config.getParserName())) {
					DataEngineDBProcess dbDataEngine = new DataEngineDBProcess(dataSource, userId, App.DATABASE.name(), status);
					dbDataEngine.processDBData(config);
				} else {
					DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), status);
					dataEngine.setMedia(media);
					dataEngine.importData(config.getName());
				}
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
