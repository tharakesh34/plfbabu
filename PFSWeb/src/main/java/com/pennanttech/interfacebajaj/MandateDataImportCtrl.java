package com.pennanttech.interfacebajaj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.DataEngineConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.service.financemanagement.PartnerBankModeConfigService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.util.ConfigUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.MandateProcesses;

public class MandateDataImportCtrl extends GFCBaseCtrl<Configuration> {
	private static final long serialVersionUID = 1297405999029019920L;
	private static final Logger logger = LogManager.getLogger(MandateDataImportCtrl.class);
	private ExtendedCombobox entityCode;

	protected Window window_MandateDataImportCtrl;
	protected Button btnImport;
	protected Button btnFileUpload;

	protected Textbox fileName;
	protected Combobox fileConfiguration;
	protected Combobox serverFileName;
	protected Combobox mandateType;
	protected ExtendedCombobox partnerBank;
	protected Row rowfileConfig;

	protected Row row1;
	protected Rows panelRows;
	protected Row rowMandateType;
	protected Row rowPartnerBank;

	protected Timer timer;
	private Media media = null;
	private File file = null;

	protected DataEngineConfig dataEngineConfig;

	protected Label label_EntityCode;

	private List<ValueLabel> serverFiles = null;

	private long userId;

	private Configuration config = null;

	private MandateProcesses mandateProcesses;
	private MandateProcesses defaultMandateProcess;
	private PartnerBankModeConfigService partnerBankModeConfigService;
	private DataEngineStatus MANDATES_IMPORT = new DataEngineStatus("MANDATES_IMPORT");
	private DataEngineStatus MANDATES_ACK = new DataEngineStatus("MANDATES_ACK");

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
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_MandateDataImportCtrl(Event event) throws Exception {
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
			if ("MANDATES_IMPORT".equals(configName) || "MANDATES_ACK".equals(configName)) {
				if ("MANDATES_IMPORT".equals(configName)) {
					MANDATES_IMPORT = dataEngineConfig.getLatestExecution("MANDATES_IMPORT");
					valueLabel = new ValueLabel(configName, "Mandate Response");
					doFillPanel(config, MANDATES_IMPORT);
					menuList.add(valueLabel);
				} else {
					MANDATES_ACK = dataEngineConfig.getLatestExecution("MANDATES_ACK");
					valueLabel = new ValueLabel(configName, "Mandate Acknowledgement");
					doFillPanel(config, MANDATES_ACK);
					menuList.add(valueLabel);
				}
			}
		}

		fillComboBox(fileConfiguration, "", menuList, "");
		if (!MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
			this.rowMandateType.setVisible(false);
			this.rowPartnerBank.setVisible(false);
		}
		doSetFieldProperties();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the component level properties.
	 */
	private void doSetFieldProperties() {
		if (MandateExtension.AUTO_UPLOAD
				&& (SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_AUTO_UPLOAD_JOB_ENABLED)
						|| SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_AUTO_UPLOAD_ACK_JOB_ENABLED))) {

			this.btnImport.setVisible(false);
		}
		if (MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
			fillComboBox(this.mandateType, "", MandateUtil.getInstrumentTypes(), "");
			this.partnerBank.setMaxlength(8);
			this.partnerBank.setTextBoxWidth(135);
			this.partnerBank.setMandatoryStyle(false);
			this.partnerBank.setModuleName("PartnerBank");
			this.partnerBank.setValueColumn("PartnerBankCode");
			this.partnerBank.setDescColumn("PartnerBankName");
			this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });
		}
	}

	public void onChange$mandateType(Event event) {
		logger.debug(Literal.ENTERING);

		if (PennantConstants.List_Select.equals(getComboboxValue(this.mandateType))) {
			return;
		}

		String instType = this.mandateType.getSelectedItem().getValue();
		this.fileName.setValue("");

		String configName = getConfigByPartnerBank(instType, this.partnerBank.getValue());
		setDEStatus(configName);

		logger.debug(Literal.LEAVING);
	}

	private void setDEStatus(String configName) {
		if (configName != null) {
			fileName.setValue("");
			serverFileName.setValue("");
			row1.setVisible(false);
			this.btnImport.setDisabled(false);
			MANDATES_IMPORT = dataEngineConfig.getLatestExecution(configName);
			config = dataEngineConfig.getConfigurationByName(configName);
			try {
				fileConfigurationSetup();
				this.rowfileConfig.setVisible(false);
				media = null;
				doFillPanel(config, MANDATES_IMPORT);
			} catch (Exception e) {
				exceptionTrace(e);
			}
		} else {
			fileName.setValue("");
			serverFileName.setValue("");
			row1.setVisible(true);
			this.rowfileConfig.setVisible(true);
			String fileConfig = this.fileConfiguration.getSelectedItem().getValue();
			if (!StringUtils.equals("#", fileConfig)) {
				this.btnImport.setDisabled(false);
				config = dataEngineConfig.getConfigurationByName(fileConfig);
			} else {
				this.btnImport.setDisabled(true);
				this.row1.setVisible(false);
				media = null;
				return;
			}
			this.fileConfiguration.setSelectedIndex(0);
			try {
				fileConfigurationSetup();
				doFillPanel(config, dataEngineConfig.getLatestExecution(fileConfig));
			} catch (Exception e) {
				exceptionTrace(e);
			}
		}

	}

	public void onFulfill$partnerBank(Event event) {
		Object dataObject = partnerBank.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.partnerBank.setValue("");
			this.partnerBank.setDescription("");
			this.partnerBank.setAttribute("PartnerBankId", null);
		} else {
			PartnerBank details = (PartnerBank) dataObject;
			this.partnerBank.setValue(String.valueOf(details.getId()));
			this.partnerBank.setAttribute("PartnerBankId", details.getId());
		}

		String configName = getConfigByPartnerBank(getComboboxValue(this.mandateType), this.partnerBank.getValue());
		setDEStatus(configName);
	}

	private String getConfigByPartnerBank(String instType, String partnerBank) {
		if (PennantConstants.List_Select.equals(instType)) {
			return null;
		}
		if (StringUtils.isEmpty(partnerBank)) {
			return null;
		}

		long partnerBankId = Long.valueOf(partnerBank);

		return partnerBankModeConfigService.getConfigName(instType, partnerBankId, DataEngineConstants.MANDATE,
				DataEngineConstants.IMPORT, false);
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 */
	public void onChange$fileConfiguration(Event event) {
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
				media = null;
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
			// Adding Entity
			if (MandateExtension.UPLOAD_ENITITY_CODE_MANDATORY) {
				this.entityCode.setMaxlength(8);
				this.entityCode.setDisplayStyle(2);
				this.entityCode.setMandatoryStyle(true);
				this.entityCode.setModuleName("Entity");
				this.entityCode.setValueColumn("EntityCode");
				this.entityCode.setDescColumn("EntityDesc");
				this.entityCode.setValidateColumns(new String[] { "EntityCode" });
				Filter[] filter = new Filter[1];
				filter[0] = new Filter("Active", 1, Filter.OP_EQUAL);
				this.entityCode.setFilters(filter);
			}
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
		// Clear the existing rows.
		panelRows.getChildren().clear();
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
		if (MandateExtension.UPLOAD_ENITITY_CODE_MANDATORY) {
			this.label_EntityCode.setVisible(isVisible);
			this.entityCode.setVisible(isVisible);
		} else {
			this.label_EntityCode.setVisible(false);
			this.entityCode.setVisible(false);
		}

	}

	/**
	 * When user clicks on "btnImport"
	 * 
	 * @param event
	 */
	public void onClick$btnImport(Event event) {
		logger.debug(Literal.ENTERING);

		if (MandateExtension.UPLOAD_ENITITY_CODE_MANDATORY) {
			if (this.entityCode.getValue() == null || this.entityCode.getValue().isEmpty()) {
				MessageUtil.showError("Entity Code is Mandatory.");
				return;
			}
		}

		if (this.fileConfiguration.getSelectedIndex() == 0 && this.rowfileConfig.isVisible()) {
			MessageUtil.showError("Please select file configuration.");
			return;
		}

		if (this.mandateType.getSelectedIndex() == 0 && MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
			MessageUtil.showError("Please select Mandate Type.");
			return;
		}

		if (media == null) {
			MessageUtil.showError("Please upload any file.");
			return;
		}

		if (MandateProcesses.MANDATES_IMPORT != null
				&& ExecutionStatus.I.name().equals(MandateProcesses.MANDATES_IMPORT.getStatus())) {
			MessageUtil.showError("Export is in progress for the selected configuration.");
			return;
		}

		try {
			Thread thread;

			if (fileConfiguration.getSelectedItem().getValue().equals("MANDATES_IMPORT")
					|| MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
				thread = new Thread(new ProcessData(userId, MANDATES_IMPORT));
			} else {
				thread = new Thread(new ProcessData(userId, MANDATES_ACK));
			}

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
	 */
	public void onUpload$btnFileUpload(UploadEvent event) {
		// Clear the file name.
		this.fileName.setText("");
		// Get the media of the selected file.
		media = event.getMedia();

		String mediaName = media.getName();
		// Get the selected configuration details.
		String prefix = config.getFilePrefixName();
		String extension = config.getFileExtension();

		if (MandateExtension.UPLOAD_ENITITY_CODE_MANDATORY) {
			String entityCode = this.entityCode.getValue();
			String fileName = entityCode.concat(prefix);
			// validate the file name.
			if (!(StringUtils.containsAny(fileName, mediaName))) {
				MessageUtil.showError("Invalid File Name");

				media = null;
				return;
			}
		} else if (prefix != null && !(StringUtils.containsAny(mediaName, prefix))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_prefix", new String[] { prefix }));
			media = null;
			return;
		}
		// Validate the file extension.
		if (!(StringUtils.endsWithIgnoreCase(mediaName, extension))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_ext", new String[] { extension }));

			media = null;
			return;
		}
		this.fileName.setText(mediaName);
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

		byte[] fileData;

		@Override
		public void run() {
			try {
				getMandateProcess().processResponseFile(userId, file, media, status);

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
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

	public void setPartnerBankModeConfigService(PartnerBankModeConfigService partnerBankModeConfigService) {
		this.partnerBankModeConfigService = partnerBankModeConfigService;
	}
}