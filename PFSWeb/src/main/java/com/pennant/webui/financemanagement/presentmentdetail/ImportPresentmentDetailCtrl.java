package com.pennant.webui.financemanagement.presentmentdetail;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.interfacebajaj.fileextract.PresentmentDetailExtract;
import com.pennanttech.interfacebajaj.fileextract.service.FileExtractService;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.PresentmentImportProcess;
import com.pennanttech.pff.notifications.service.NotificationService;

public class ImportPresentmentDetailCtrl extends GFCBaseCtrl<Object> {

	private static final long serialVersionUID = 4783031677099154138L;
	private static final Logger logger = LogManager.getLogger(ImportPresentmentDetailCtrl.class);

	protected Window window_ImportPresentmentDetails;

	protected Button btnUpload;
	protected Button btnSave;
	protected Textbox txtFileName;
	protected Timer timer;
	protected Rows panelRows;

	protected Row rowInstrumentType;
	protected Combobox instrumentType;
	protected Grid grid_Default;
	protected Grid grid_DataEngine;
	protected Row defaultPanelRow;

	private FileExtractService<PresentmentDetailExtract> presentmentExtractService;

	/* Data-Source */
	private DataSource dataSource;

	/* DAO's */
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private CustomerDAO customerDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;

	/* Service's */
	private PresentmentDetailService presentmentDetailService;
	private NotificationService notificationService;
	private ReceiptPaymentService receiptPaymentService;
	private ReceiptCancellationService receiptCancellationService;
	private PostingsPreparationUtil postingsPreparationUtil;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private ReceiptCalculator receiptCalculator;
	private PresentmentImportProcess presentmentImportProcess;
	private EventPropertiesService eventPropertiesService;

	private DataEngineConfig dataEngineConfig;
	private Configuration config = null;
	private DataEngineStatus PRSENTMENT_FILE_IMPORT_STATUS = null;
	private ProcessExecution processExecution = null;
	private PresentmentDetailExtract fileImport;
	private String errorMsg = null;
	private boolean allowInstrumentType;
	private Media media = null;

	private List<ValueLabel> defaultPaymentType;

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
		logger.debug(Literal.ENTERING);

		setPageComponents(window_ImportPresentmentDetails);

		allowInstrumentType = "Y".equals(
				SysParamUtil.getValueAsString(SMTParameterConstants.PRESENTMENT_RESPONSE_ALLOW_INSTRUMENT_TYPE)) ? true
						: false;

		if (allowInstrumentType) {
			this.rowInstrumentType.setVisible(true);
			doSetFieldProperties();
			grid_Default.setVisible(false);
			grid_DataEngine.setVisible(true);
			this.defaultPanelRow.setVisible(false);
			this.panelRows.setVisible(true);
		} else {
			this.rowInstrumentType.setVisible(false);
			grid_Default.setVisible(true);
			grid_DataEngine.setVisible(false);
			this.defaultPanelRow.setVisible(true);
			this.panelRows.setVisible(false);
		}

		if (!allowInstrumentType) {
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
				instrumentType.setDisabled(false);
			} else if (ExecutionStatus.F.name().equals(status)) {
				btnSave.setDisabled(true);
				btnUpload.setDisabled(true);
				instrumentType.setDisabled(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$instrumentType(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		if (PennantConstants.List_Select.equals(getComboboxValue(this.instrumentType))) {
			return;
		}

		String instType = this.instrumentType.getSelectedItem().getValue();
		this.txtFileName.setValue("");

		String instrumentTypeConfigName = "PRESENTMENT_RESPONSE_";
		instrumentTypeConfigName = instrumentTypeConfigName.concat(instType);

		String configName = SysParamUtil.getValueAsString(instrumentTypeConfigName);
		if (configName == null) {
			configName = "PRESENTMENT_RESPONSE";
		}

		try {
			config = dataEngineConfig.getConfigurationByName(configName);
		} catch (Exception e) {
			MessageUtil.showError(e);
			return;
		}

		PRSENTMENT_FILE_IMPORT_STATUS = dataEngineConfig.getLatestExecution(configName);

		doFillPanel(config, PRSENTMENT_FILE_IMPORT_STATUS);

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the component level properties.
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		if (allowInstrumentType) {
			fillComboBox(this.instrumentType, "", PennantStaticListUtil.getMandateTypeList(), "");
			logger.debug(Literal.LEAVING);

			if (ImplementationConstants.PRESENTMENT_AUTO_UPLOAD
					&& (SysParamUtil.isAllowed(SMTParameterConstants.PRESENTMENT_NACH_AUTO_UPLOAD_JOB_ENABLED)
							|| SysParamUtil.isAllowed(SMTParameterConstants.PRESENTMENT_PDC_AUTO_UPLOAD_JOB_ENABLED))) {
				this.btnSave.setDisabled(true);
			}
		} else {
			defaultPaymentType = new ArrayList<ValueLabel>(1);
			defaultPaymentType.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_IST,
					Labels.getLabel("label_Presentment_Default")));
			fillComboBox(this.instrumentType, Labels.getLabel("label_Presentment_Default"), defaultPaymentType, "");
			this.instrumentType.setSelectedIndex(1);
			setConfigData();
			this.instrumentType.setDisabled(true);
		}

	}

	public void setConfigData() {
		String instrumentTypeConfigName = "";
		if (allowInstrumentType) {
			String instType = "";

			if (PennantConstants.List_Select.equals(getComboboxValue(this.instrumentType))) {
				return;
			}

			instType = this.instrumentType.getSelectedItem().getValue();
			this.txtFileName.setValue("");

			instrumentTypeConfigName = "PRESENTMENT_RESPONSE_";
			instrumentTypeConfigName = instrumentTypeConfigName.concat(instType);

		} else {
			instrumentTypeConfigName = "PRESENTMENT_RESPONSE";
		}

		String configName = SysParamUtil.getValueAsString(instrumentTypeConfigName);
		if (configName == null) {
			configName = "PRESENTMENT_RESPONSE";
		}

		try {
			config = dataEngineConfig.getConfigurationByName(configName);
		} catch (Exception e) {
			MessageUtil.showError(e);
			return;
		}
		PRSENTMENT_FILE_IMPORT_STATUS = dataEngineConfig.getLatestExecution(configName);

		doFillPanel(config, PRSENTMENT_FILE_IMPORT_STATUS);
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
		setMedia(event.getMedia());

		if (!PennantAppUtil.uploadDocFormatValidation(media)) {
			return;
		}
		txtFileName.setText(media.getName());

		if (allowInstrumentType) {
			String mediaName = media.getName();
			String prefix = config.getFilePrefixName();
			String extension = config.getFileExtension();

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

			if (presentmentDetailDAO.isFileProcessed(mediaName)) {
				MessageUtil.showError("Selected file already processed");

				media = null;
				return;
			}

		} else {
			int row_NumberOfCells = 14;
			Object valu = SysParamUtil.getValue(SMTParameterConstants.PRESENTMENT_RESPONSE_ROW_LENGTH);
			if (valu != null) {
				row_NumberOfCells = Integer.parseInt(valu.toString());
			}

			try {
				String fileName = StringUtils.lowerCase(media.getName());
				if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
					fileImport = null;
					txtFileName.setText(media.getName());
					setFileImportData(media.getName().substring(media.getName().lastIndexOf('.')));
					fileImport.row_NumberOfCells = row_NumberOfCells;
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
		}
		logger.debug(Literal.LEAVING);
	}

	private void setFileImportData(String contentType) throws Exception {
		logger.debug(Literal.ENTERING);

		if (fileImport == null) {
			fileImport = presentmentExtractService.getFileExtract(getUserWorkspace().getLoggedInUser().getUserId(),
					contentType);
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

		try {
			doValidations();
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

		if (allowInstrumentType && PennantConstants.List_Select.equals(getComboboxValue(this.instrumentType))) {
			throw new WrongValueException(this.instrumentType, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_PresentmentDetailList_MandateType.value") }));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSave() throws Exception {
		logger.debug(Literal.ENTERING);

		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		PresentmentDetailExtract pde = new PresentmentDetailExtract(this.dataSource);
		/* Data */
		pde.setInstrumentType(this.instrumentType.getSelectedItem().getValue());
		pde.setUserDetails(loggedInUser);
		pde.setStatus(PRSENTMENT_FILE_IMPORT_STATUS);
		pde.setMediaOnly(getMedia());
		presentmentDetailService.setProperties(pde);

		if (processExecution.getChildren() != null) {
			processExecution.getChildren().clear();
		}

		Thread thread = new Thread(pde);
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
		defaultPanelRow.setStyle("overflow: visible !important");
		Hbox hbox = new Hbox();
		hbox.setAlign("center");
		hbox.appendChild(pannel);
		defaultPanelRow.appendChild(hbox);

		logger.debug(Literal.LEAVING);
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

	public void onTimer$timer(Event event) {
		if (allowInstrumentType) {
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
							this.instrumentType.setDisabled(true);
						} else {
							this.btnUpload.setDisabled(false);
							this.btnSave.setDisabled(false);
							this.instrumentType.setDisabled(false);
						}
						pe.render();
					}
				}
			}
		} else {
			Events.postEvent("onCreate", this.window_ImportPresentmentDetails, event);
		}
	}

	/**
	 * Starting the Timer
	 */
	public void startTimer() {
		if (!timer.isRunning()) {
			timer.start();
		}
	}

	// Getters and setters
	public void setPresentmentExtractService(FileExtractService<PresentmentDetailExtract> presentmentExtractService) {
		this.presentmentExtractService = presentmentExtractService;
	}

	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
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

	@Autowired(required = false)
	@Qualifier(value = "presentmentImportProcess")
	public void setPresentmentImportProcess(PresentmentImportProcess presentmentImportProcess) {
		this.presentmentImportProcess = presentmentImportProcess;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public PresentmentDetailDAO getPresentmentDetailDAO() {
		return presentmentDetailDAO;
	}

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public PresentmentDetailService getPresentmentDetailService() {
		return presentmentDetailService;
	}

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public ReceiptPaymentService getReceiptPaymentService() {
		return receiptPaymentService;
	}

	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	public ReceiptCancellationService getReceiptCancellationService() {
		return receiptCancellationService;
	}

	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public RepaymentPostingsUtil getRepaymentPostingsUtil() {
		return repaymentPostingsUtil;
	}

	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	public ReceiptCalculator getReceiptCalculator() {
		return receiptCalculator;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public FileExtractService<PresentmentDetailExtract> getPresentmentExtractService() {
		return presentmentExtractService;
	}

	public PresentmentImportProcess getPresentmentImportProcess() {
		return presentmentImportProcess;
	}

	public EventPropertiesService getEventPropertiesService() {
		return eventPropertiesService;
	}

	public void setEventPropertiesService(EventPropertiesService eventPropertiesService) {
		this.eventPropertiesService = eventPropertiesService;
	}

}
