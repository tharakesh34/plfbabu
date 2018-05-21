package com.pennanttech.interfacebajaj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
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
	private DataEngineStatus	DISB_OTHER_IMPORT_STATUS	= new DataEngineStatus("DISB_OTHER_IMPORT");
	private DataEngineStatus				DISB_STP_IMPORT_STATUS		= new DataEngineStatus("DISB_CITI_IMPORT");
	
	@Autowired(required=false)
	private DisbursementResponse disbursementResponse;

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

		ValueLabel valueLabel = null;
		List<ValueLabel> menuList = new ArrayList<ValueLabel>();
		userId = getUserWorkspace().getUserDetails().getLoginId();
		List<Configuration> configList = dataEngineConfig.getMenuList(true);

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		for (Configuration config : configList) {
			String configName = config.getName();
			if ("DISB_HDFC_IMPORT".equals(configName) || "DISB_OTHER_IMPORT".equals(configName)) {
				if ("DISB_HDFC_IMPORT".equals(configName)) {
					DISB_STP_IMPORT_STATUS = dataEngineConfig.getLatestExecution("DISB_HDFC_IMPORT");
					valueLabel = new ValueLabel(configName, "Bank Disbursement Response");
					doFillPanel(config, DISB_STP_IMPORT_STATUS);
					menuList.add(valueLabel);
				} else {
					DISB_OTHER_IMPORT_STATUS = dataEngineConfig.getLatestExecution("DISB_OTHER_IMPORT");
					valueLabel = new ValueLabel(configName, "Other Bank Disbursement Response");
					doFillPanel(config, DISB_OTHER_IMPORT_STATUS);
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
		this.btnImport.setDisabled(true);

		if (media == null) {
			MessageUtil.showError("Please upload any file.");
			return;
		}

		try {
			try {
				Thread thread = null;
				if (fileConfiguration.getSelectedItem().getValue().equals("DISB_HDFC_IMPORT")) {
					thread = new Thread(new ProcessData(userId, DISB_STP_IMPORT_STATUS));
				} else {
					thread = new Thread(new ProcessData(userId, DISB_OTHER_IMPORT_STATUS));
				}

				thread.start();
			} catch (Exception e) {
				MessageUtil.showError(e);
				return;
			}
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
		fileName.setText("");
		media = event.getMedia();
		
		if (!(StringUtils.endsWith(media.getName().toUpperCase(), ".TXT"))) {
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
				disbursementResponse.processResponseFile(userId, status, file, media, false);
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}
