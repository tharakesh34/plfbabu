package com.pennanttech.framework.component.dataengine;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.DataEngineConstants.ConfigNames;
import com.pennanttech.dataengine.constants.DataEngineConstants.ParserNames;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dbengine.DataEngineDBProcess;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class DataExportCtrl extends GFCBaseCtrl<Configuration> {
	private static final long serialVersionUID = 1297405999029019920L;
	private static final Logger logger = Logger.getLogger(DataExportCtrl.class);

	protected Window window_DataExportCtrl;

	protected Textbox fileName;
	protected Combobox fileConfiguration;
	protected Combobox fileNames;
	protected Label fileDownload;
	protected Rows panelRows;

	protected Datebox fromDate;
	protected Datebox toDate;
	protected Uppercasebox branchDetails;
	protected Button btnbranchDetails;

	protected Row row_Branches;
	protected Row row_Dates;

	protected Button btnExport;
	protected Button btnFileUpload;

	protected Timer timer;
	protected Media media = null;

	protected DataEngineConfig dataEngineConfig;
	private DataSource dataSource;
	private Configuration config = null;

	private boolean preview = false;
	private long userId;

	/**
	 * default constructor.<br>
	 */
	public DataExportCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_DataExportCtrl(Event event) throws Exception {
		// Set the page level components.
		setPageComponents(window);

		ValueLabel valueLabel = null;
		List<ValueLabel> menuList = new ArrayList<ValueLabel>();
		userId = getUserWorkspace().getUserDetails().getLoginId();

		String[] parsers = new String[2];
		parsers[0] = ParserNames.WRITER.name();
		parsers[1] = ParserNames.DBWRITER.name();
		List<Configuration> configList = dataEngineConfig.getMenuList(parsers);
		for (Configuration config : configList) {
			valueLabel = new ValueLabel(String.valueOf(config.getParser()), config.getName());
			menuList.add(valueLabel);
			doFillExePanels(config, dataEngineConfig.getLatestExecution(config.getName()));
			fillComboBox(fileConfiguration, "", menuList, "");
			doFillFileNames();
		}
		doSetFieldProperties();
	}

	private void doSetFieldProperties() {
		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
	}

	private void doFillFileNames() {
		String configName = this.fileConfiguration.getSelectedItem().getLabel();
		if (StringUtils.equals(PennantConstants.List_Select, configName)) {
			fillComboBox(this.fileNames, "", new ArrayList<ValueLabel>(), "");
			this.fileDownload.setVisible(false);
			return;
		}

		List<DataEngineStatus> des = dataEngineConfig.getDataEngineStatus(configName);
		List<ValueLabel> fileList = new ArrayList<ValueLabel>();
		ValueLabel valueLabel = null;
		for (int i = 0; i < des.size(); i++) {
			String fileName = des.get(i).getFileName();
			if (StringUtils.trimToNull(fileName) == null) {
				fileName = "<< Invalid >>";
			}
			valueLabel = new ValueLabel(String.valueOf(des.get(i).getId()), fileName);
			fileList.add(valueLabel);
			if (i == 10) {
				break;
			}
		}
		fillComboBox(this.fileNames, "", fileList, "");
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$fileConfiguration(Event event) throws Exception {
		try {
			doClear();
			String fileConfig = this.fileConfiguration.getSelectedItem().getLabel();
			String parserId = this.fileConfiguration.getSelectedItem().getValue().toString();
			if (!StringUtils.equals(Labels.getLabel("Combo.Select"), fileConfig)) {
				this.btnExport.setDisabled(false);
				config = dataEngineConfig.getConfiguration(fileConfig, Integer.valueOf(parserId));
				doFillFileNames();
			} else {
				doFillFileNames();
				this.btnExport.setDisabled(true);
				this.row_Branches.setVisible(false);
				this.row_Dates.setVisible(false);
				return;
			}

			if (ConfigNames.MANDATES_EXPORT.name().equals(config.getName())) {
				this.row_Branches.setVisible(true);
				this.row_Dates.setVisible(true);
			} else {
				this.row_Branches.setVisible(false);
				this.row_Dates.setVisible(false);
			}
		} catch (Exception e) {
			exceptionTrace(e);
		}
	}

	private void doClear() {
		this.btnExport.setDisabled(true);
		fillComboBox(this.fileNames, "", new ArrayList<ValueLabel>(), "");
		this.fileDownload.setVisible(false);
		this.branchDetails.setValue("");
		this.fromDate.setValue(null);
		this.toDate.setValue(null);
	}

	/**
	 * When the filename is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$fileNames(Event event) throws Exception {
		try {
			String fileName = this.fileNames.getSelectedItem().getLabel().toString();
			if (StringUtils.equals(Labels.getLabel("Combo.Select"), fileName)) {
				this.fileDownload.setVisible(false);
				return;
			} else {
				DataEngineStatus ds = dataEngineConfig.getDataEngineStatus(Long.valueOf(this.fileNames.getSelectedItem().getValue().toString()));
				if (ExecutionStatus.S.name().equals(ds.getStatus())) {
					this.fileDownload.setVisible(true);
				} else {
					this.fileDownload.setVisible(false);
				}
				// doFillPanel(ds, config);
			}
		} catch (Exception e) {
			exceptionTrace(e);
		}
	}

	public void onClick$fileDownload(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (Labels.getLabel("Combo.Select").equals(this.fileNames.getSelectedItem().getLabel())) {
			MessageUtil.showErrorMessage("Please select file from fileNames list.");
			return;
		}
		if (Labels.getLabel("Combo.Select").equals(this.fileConfiguration.getSelectedItem().getLabel())) {
			MessageUtil.showErrorMessage("Please select config from fileConfiguration list.");
			return;
		}

		long id = Long.valueOf(this.fileNames.getSelectedItem().getValue().toString());
		String configName = this.fileConfiguration.getSelectedItem().getLabel().toString();
		doDownloadFile(id, configName);

		logger.debug("Leaving" + event.toString());
	}

	private void doDownloadFile(long desId, String configName) {
		try {
			DataEngineStatus ds = dataEngineConfig.getDataEngineStatus(desId);

			String filePath = config.getUploadPath();
			if (ds.getFileName() != null) {
				filePath = filePath.concat("/").concat(ds.getFileName());
			}

			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			InputStream inputStream = new FileInputStream(filePath);
			int data;

			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}
			inputStream.close();
			inputStream = null;
			Filedownload.save(stream.toByteArray(), "text/plain", ds.getFileName());
			stream.close();
			stream = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnExport"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnExport(Event event) throws InterruptedException {

		DataEngineStatus status = dataEngineConfig.getLatestExecution(config.getName());
		if (status != null && ExecutionStatus.I.name().equals(status.getStatus())) {
			MessageUtil.showErrorMessage("Export is in progress for the selected configuration.");
			return;
		}

		Map<String, Object> filterMap = getFilterMap();
		try {
			Thread thread = new Thread(new ProcessData(userId, config, filterMap));
			thread.start();
			timer.start();
		} catch (Exception e) {
			MessageUtil.showErrorMessage(e.getMessage());
			return;
		} finally {
			doFillFileNames();
		}
	}

	private Map<String, Object> getFilterMap() {

		Map<String, Object> filterMap = null;
		if (this.row_Branches.isVisible() && this.row_Dates.isVisible()) {
			filterMap = new HashMap<String, Object>();

			if (this.fromDate.getValue() == null) {
				throw new WrongValueException(this.fromDate, "From date mandatory.");
			}

			if (this.toDate.getValue() == null) {
				throw new WrongValueException(this.toDate, "To date mandatory.");
			}

			if (this.fromDate.getValue().compareTo(this.toDate.getValue()) == 1) {
				throw new WrongValueException(this.toDate, "To date should greater than or equal to From date.");
			}

			filterMap.put("FROMDATE", this.fromDate.getValue());
			filterMap.put("TODATE", this.toDate.getValue());

			if (StringUtils.trimToNull(this.branchDetails.getValue()) != null) {
				filterMap.put("BRANCHCODE", Arrays.asList(this.branchDetails.getValue().split(",")));
			}
		}
		return filterMap;
	}

	public void onClick$btnbranchDetails(Event event) {
		logger.debug("Entering  " + event.toString());

		Object dataObject = MultiSelectionSearchListBox.show(this.window_DataExportCtrl, "DataEngine",
				this.branchDetails.getValue(), null);
		if (dataObject instanceof String) {
			this.branchDetails.setValue(dataObject.toString());
		} else {
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String tempflagcode = "";
				List<String> flagKeys = new ArrayList<>(details.keySet());
				for (int i = 0; i < flagKeys.size(); i++) {
					if (StringUtils.isEmpty(flagKeys.get(i))) {
						continue;
					}
					if (i == 0) {
						tempflagcode = flagKeys.get(i);
					} else {
						tempflagcode = tempflagcode + "," + flagKeys.get(i);
					}
				}
				this.branchDetails.setValue(tempflagcode);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	private void exceptionTrace(Exception e) throws InterruptedException {
		fileConfiguration.setValue(Labels.getLabel("Combo.Select"));
		fileConfiguration.setSelectedIndex(0);
		this.btnExport.setDisabled(true);
		fileNames.setValue(PennantConstants.List_Select);
		fileNames.setSelectedIndex(0);
		MessageUtil.showErrorMessage(e.getMessage());
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
						this.btnExport.setDisabled(true);
					} else {
						this.btnExport.setDisabled(false);
					}
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
		pannelExecution.setPreview(preview);

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
				/*
				 * if (hbox.getFellowIfAny(config.getName()) != null) { rows.removeChild(hbox); }
				 */
				hbox.appendChild(pannelExecution);
				// rows.appendChild(hbox);
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
						status.setStatus("Loading configuration...");
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
		private Map<String, Object> filterMap;

		public ProcessData(long userId, Configuration config, Map<String, Object> filterMap) {
			this.userId = userId;
			this.config = config;
			this.filterMap = filterMap;
		}

		@Override
		public void run() {
			DataEngineStatus status = null;
			try {
				status = getPannelExecution(config);
				if (ParserNames.DB.name().equals(config.getParserName())) {
					DataEngineDBProcess dbDataEngine = new DataEngineDBProcess(dataSource, userId, App.DATABASE.name(), status);
					dbDataEngine.processDBData(config);
				} else {
					DataEngineExport dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), status);
					dataEngine.setFilterMap(filterMap);
					dataEngine.exportData(config.getName());
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
