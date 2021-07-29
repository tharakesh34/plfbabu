package com.pennant.webui.financemanagement.insurance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class InsPaymentUploadDialogCtrl extends GFCBaseCtrl<InsurancePaymentInstructions> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InsPaymentUploadDialogCtrl.class);

	protected Window window_InsPaymentUploadDialog;

	protected ExtendedCombobox entityCode;
	protected ExtendedCombobox companyCode;
	protected Textbox fileName;

	protected Intbox noOfInsurances;
	protected Checkbox adjustReceivables;
	protected Intbox noOfPayments;
	protected Intbox noOfReceivables;
	protected CurrencyBox totalPayableAmt;
	protected CurrencyBox totalReceviableAmt;

	protected CurrencyBox paymentAmount;
	protected Datebox paymentDate;
	protected ExtendedCombobox partnerBank;

	protected Textbox ifscCode;
	protected Textbox micrCode;
	protected Textbox bankBranch;
	protected Label bankBranchDesc;
	protected Textbox bankName;
	protected Label bankNameDesc;
	protected Textbox accountNumber;
	protected Textbox paymentMode;
	protected Textbox city;
	protected Label cityName;
	protected Textbox accountHolderName;
	protected Textbox phoneNumber;

	protected Timer timer;
	protected Rows panelRows;
	protected Button btnUpload;
	protected Button btnImport;
	protected Button btnValidate;

	private Configuration config = null;
	private Media media = null;
	private DataEngineStatus INSURANCE_PAYMENT_UPLOAD = new DataEngineStatus("INSURANCE_PAYMENT_UPLOAD");
	private InsurancePaymentInstructions paymentInstructions;
	private InsurancePaymentInstructions paymentInstructionsFromFile = null;
	private transient InsPaymentUploadListCtrl insPaymentUploadListCtrl;
	private boolean validate = false;

	private DataEngineConfig dataEngineConfig;
	private InsuranceFileImportService insuranceFileImportService;

	/**
	 * default constructor.<br>
	 */
	public InsPaymentUploadDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InsurancePaymentHeaderDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.paymentInstructions.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_InsPaymentUploadDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_InsPaymentUploadDialog);
		try {
			// Get the required arguments.
			this.paymentInstructions = (InsurancePaymentInstructions) arguments.get("insurancePaymentInstructions");
			this.insPaymentUploadListCtrl = (InsPaymentUploadListCtrl) arguments.get("insurancePaymentUploadListCtrl");

			if (this.paymentInstructions == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			InsurancePaymentInstructions insPaymentUpload = new InsurancePaymentInstructions();
			BeanUtils.copyProperties(this.paymentInstructions, insPaymentUpload);
			this.paymentInstructions.setBefImage(insPaymentUpload);

			doSetFieldProperties();

			config = dataEngineConfig.getConfigurationByName("INSURANCE_PAYMENT_UPLOAD");
			doFillPanel(config, INSURANCE_PAYMENT_UPLOAD);

			doCheckRights();
			doShowDialog(this.paymentInstructions);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.entityCode.setModuleName("Entity");
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		this.companyCode.setMandatoryStyle(true);
		this.companyCode.setModuleName("VehicleDealer");
		this.companyCode.setValueColumn("DealerId");
		this.companyCode.setDescColumn("DealerName");
		this.companyCode.setValueType(DataType.LONG);
		this.companyCode.setValidateColumns(new String[] { "DealerId" });
		this.companyCode.setButtonDisabled(true);

		this.partnerBank.setMandatoryStyle(true);
		this.partnerBank.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.partnerBank.setModuleName("PartnerBank");
		this.partnerBank.setValueColumn("PartnerBankId");
		this.partnerBank.setValueType(DataType.LONG);
		this.partnerBank.setDescColumn("PartnerBankCode");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankId" });
		this.partnerBank.setButtonDisabled(true);

		this.totalPayableAmt.setProperties(false, getCcyFormat());
		this.totalReceviableAmt.setProperties(false, getCcyFormat());
		this.paymentAmount.setProperties(false, getCcyFormat());

		this.paymentAmount.setReadonly(true);
		this.totalPayableAmt.setReadonly(true);
		this.totalReceviableAmt.setReadonly(true);

		this.totalPayableAmt.setTextBoxWidth(200);
		this.totalReceviableAmt.setTextBoxWidth(200);
		this.paymentDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.paymentDate.setValue(DateUtility.getAppDate());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnSave.setVisible(true);
		this.btnImport.setVisible(true);
		this.btnValidate.setVisible(true);
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.paymentInstructions);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		insPaymentUploadListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(InsurancePaymentInstructions paymentInstructions) {
		logger.debug(Literal.LEAVING);

		if (paymentInstructions.isNewRecord()) {
			this.btnCtrl.setInitNew();
			this.entityCode.focus();
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		setDialog(DialogType.EMBEDDED);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		this.companyCode.setConstraint("");
		this.entityCode.setConstraint("");
		this.fileName.setConstraint("");
		this.noOfInsurances.setConstraint("");
		this.noOfPayments.setConstraint("");
		this.totalPayableAmt.setConstraint("");
		this.partnerBank.setConstraint("");
		this.paymentDate.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.companyCode.setErrorMessage("");
		this.entityCode.setErrorMessage("");
		this.fileName.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**************** File Reading and Browsing **********************/
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

		setMedia(event.getMedia());

		if (!MediaUtil.isCsv(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "csv" }));
			media = null;
			return;
		}

		fileName.setText(media.getName());

		validate = false;
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnImport(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		doWriteComponentsToBean(new InsurancePaymentInstructions(), false);
		try {
			importFile();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e.getMessage());
			return;
		}
		logger.debug(Literal.LEAVING);
	}

	private void importFile() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		ProcessData processData = new ProcessData(getUserWorkspace().getUserDetails().getLoginId(),
				INSURANCE_PAYMENT_UPLOAD, Long.valueOf(this.companyCode.getValue()), this.entityCode.getValue(), this);
		this.btnSave.setDisabled(true);
		this.btnImport.setDisabled(true);
		this.btnValidate.setDisabled(true);
		Thread thread = new Thread(processData);
		thread.start();
		Thread.sleep(1000);

		logger.debug(Literal.LEAVING);
	}

	public class ProcessData extends Thread {
		private long userId;
		private DataEngineStatus status;
		private long providerId;
		private String entityCode;
		private InsPaymentUploadDialogCtrl dialogCtrl;

		public ProcessData(long userId, DataEngineStatus status, long providerId, String entityCode,
				InsPaymentUploadDialogCtrl dialogCtrl) {
			this.userId = userId;
			this.status = status;
			this.entityCode = entityCode;
			this.providerId = providerId;
			this.dialogCtrl = dialogCtrl;
		}

		@Override
		public void run() {
			try {
				getInsuranceFileImportService().processPaymentUploadsFile(userId, status, getMedia(), providerId,
						entityCode, dialogCtrl);
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
						this.btnImport.setDisabled(true);
						this.btnValidate.setDisabled(true);
					} else {
						this.btnUpload.setDisabled(false);
						this.btnSave.setDisabled(false);
						this.btnImport.setDisabled(false);
						this.btnValidate.setDisabled(false);
					}
					pe.render();
				}
			}
		}
	}

	/**************** File Reading and Browsing **********************/

	public void onClick$btnValidate(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		doWriteComponentsToBean(new InsurancePaymentInstructions(), false);
		setDataFromFile();
		validate = true;
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Setting the Components data from From file import
	 */
	private void setDataFromFile() {
		InsurancePaymentInstructions instructions = getPaymentInstructionsFromFile();
		if (instructions != null) {

			Map<Long, String> adviseRefMap = instructions.getAdviseRefMap();
			if (adviseRefMap.size() > 0) {
				instructions = getInsuranceFileImportService().getManualAdvises(instructions);
			}
			this.noOfInsurances.setValue(instructions.getNoOfInsurances());
			this.noOfPayments.setValue(instructions.getNoOfPayments());
			this.noOfReceivables.setValue(instructions.getNoOfReceivables());
			this.totalPayableAmt
					.setValue(PennantAppUtil.formateAmount(instructions.getPayableAmount(), getCcyFormat()));
			this.totalReceviableAmt
					.setValue(PennantAppUtil.formateAmount(instructions.getReceivableAmount(), getCcyFormat()));
			BigDecimal paymentAmount = instructions.getPayableAmount().subtract(instructions.getReceivableAmount());
			this.paymentAmount.setValue(PennantAppUtil.formateAmount(paymentAmount, getCcyFormat()));
			this.paymentInstructions.setDataEngineStatusId(instructions.getDataEngineStatusId());
			this.paymentInstructions.setAdviseRefMap(adviseRefMap);
			this.paymentInstructions.setVasRecordindList(instructions.getVasRecordindList());
		}
	}

	public void onFulfill$companyCode(Event event) throws InterruptedException {
		this.companyCode.setConstraint("");
		this.companyCode.clearErrorMessage();
		Clients.clearWrongValue(companyCode);
		Object dataObject = this.companyCode.getObject();
		if (dataObject instanceof String) {
			this.companyCode.setValue(dataObject.toString());
			this.companyCode.setDescription("");
			clearVasProviderAcctDetails();
			this.accountNumber.setValue("");
			this.paymentMode.setValue("");
		} else {
			if (dataObject instanceof VehicleDealer) {
				VehicleDealer details = (VehicleDealer) dataObject;
				this.companyCode.setValue(String.valueOf(details.getDealerId()));
				this.companyCode.setDescription(details.getDealerName());
				setVasProviderAcctDetails(details.getDealerId());
			} else {
				clearVasProviderAcctDetails();
				this.accountNumber.setValue("");
				this.paymentMode.setValue("");
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	private void clearVasProviderAcctDetails() {
		this.ifscCode.setValue("");
		this.micrCode.setValue("");
		this.bankBranch.setValue("");
		this.bankBranchDesc.setValue("");
		this.bankName.setValue("");
		this.bankNameDesc.setValue("");
		this.city.setValue("");
		this.cityName.setValue("");
		this.accountHolderName.setValue("");
		this.phoneNumber.setValue("");
		this.adjustReceivables.setChecked(false);
	}

	private void setVasProviderAcctDetails(long providerId) {
		VASProviderAccDetail providerAccDetail = getInsuranceFileImportService()
				.getVASProviderAccDetByPRoviderId(providerId, this.entityCode.getValue());
		if (providerAccDetail != null) {
			BankBranch bankBranch = getInsuranceFileImportService()
					.getBankBranchById(providerAccDetail.getBankBranchID());
			VehicleDealer vehicleDealer = getInsuranceFileImportService()
					.getProviderDetails(providerAccDetail.getProviderId());
			this.adjustReceivables.setChecked(providerAccDetail.isReceivableAdjustment());
			if (bankBranch != null) {
				this.ifscCode.setValue(bankBranch.getIFSC());
				this.micrCode.setValue(bankBranch.getMICR());
				this.bankBranch.setValue(bankBranch.getBranchCode());
				this.bankBranchDesc.setValue(bankBranch.getBranchDesc());
				this.bankName.setValue(bankBranch.getBankCode());
				this.bankNameDesc.setValue(bankBranch.getBankName());
				this.city.setValue(bankBranch.getCity());
				this.cityName.setValue(bankBranch.getPCCityName());
				if (vehicleDealer != null) {
					this.accountHolderName.setValue(vehicleDealer.getDealerName());
					this.phoneNumber.setValue(vehicleDealer.getDealerTelephone());
				}
			} else {
				clearVasProviderAcctDetails();
			}
			this.accountNumber.setValue(providerAccDetail.getAccountNumber());
			this.paymentMode.setValue(providerAccDetail.getPaymentMode());

		} else {
			this.accountNumber.setValue("");
			this.paymentMode.setValue("");
			clearVasProviderAcctDetails();
			MessageUtil.showError("Provider Account details are not available with the slected entity code. ");
		}

	}

	/**
	 * Based On Entity field,Partner Bank will be Filtered
	 * 
	 * @param event
	 */
	public void onFulfill$entityCode(Event event) {
		logger.debug("Entering");
		Object dataObject = entityCode.getObject();
		if (dataObject instanceof String) {
			this.partnerBank.setButtonDisabled(true);
			this.partnerBank.setValue("");
			this.partnerBank.setDescription("");
			this.companyCode.setButtonDisabled(true);
			this.companyCode.setValue("");
			this.companyCode.setDescription("");
		} else {
			Entity details = (Entity) dataObject;
			this.partnerBank.setObject("");
			this.partnerBank.setValue("");
			this.partnerBank.setDescription("");
			this.companyCode.setObject("");
			this.companyCode.setValue("");
			this.companyCode.setDescription("");
			if (details != null) {
				this.partnerBank.setButtonDisabled(false);
				this.companyCode.setButtonDisabled(false);
				Filter[] filters = new Filter[1];
				filters[0] = new Filter("Entity", details.getEntityCode(), Filter.OP_EQUAL);
				this.partnerBank.setFilters(filters);
			} else {
				this.partnerBank.setValue("");
				this.partnerBank.setDescription("");
				this.partnerBank.setButtonDisabled(true);
				this.companyCode.setValue("");
				this.companyCode.setDescription("");
				this.companyCode.setButtonDisabled(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_InsPaymentUploadDialog_EntityCode.value"), null, true, true));
		}
		if (!this.companyCode.isReadonly()) {
			this.companyCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_InsPaymentUploadDialog_CompanyCode.value"), null, true, true));
		}
		this.partnerBank.setConstraint(new PTStringValidator(
				Labels.getLabel("label_DisbInstructionsDialog_Partnerbank.value"), null, true, true));
		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(InsurancePaymentInstructions instructions, boolean isSave) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();
		doSetValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (DateUtility.compare(this.paymentDate.getValue(), DateUtility.getAppDate()) < 0
					&& !paymentDate.isDisabled()) {
				throw new WrongValueException(this.paymentDate,
						"Payment Date should be greater than or equal to :" + DateUtility.getAppDate());
			}
			instructions.setPaymentDate(this.paymentDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			instructions.setPaymentType(this.paymentMode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Code
		try {
			instructions.setEntityCode(this.entityCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// companyCode
		try {
			instructions.setProviderId(Long.valueOf(this.companyCode.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// fileName
		try {
			if (StringUtils.trimToNull(this.fileName.getValue()) == null) {
				throw new WrongValueException(this.fileName, "File Name is mandatory.");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (isSave) {
			// partnerBank
			try {
				instructions.setPartnerBankId(Long.valueOf(this.partnerBank.getValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// noOfInsurances
			try {
				if (this.noOfInsurances.intValue() <= 0) {
					throw new WrongValueException(this.noOfInsurances,
							"Number Of Insurances should be greater than 0. Please import the file. ");
				}
				instructions.setNoOfInsurances(this.noOfInsurances.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// noOfPayments
			try {
				if (this.noOfPayments.intValue() <= 0) {
					throw new WrongValueException(this.noOfPayments,
							"Number Of Payments should be greater than 0. Please import the file. ");
				}
				instructions.setNoOfPayments(this.noOfPayments.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// totalPayableAmt
			try {
				if (this.totalPayableAmt.getActualValue().compareTo(BigDecimal.ZERO) <= 0) {
					throw new WrongValueException(this.totalPayableAmt,
							"Total Payble amount should be greater than 0. Please import the file. ");
				}
				instructions.setPayableAmount(
						PennantAppUtil.unFormateAmount(this.totalPayableAmt.getActualValue(), getCcyFormat()));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// paymentAmount
			try {
				if ((this.noOfReceivables.intValue() < 0)
						&& (this.paymentAmount.getActualValue().compareTo(BigDecimal.ZERO) <= 0)) {
					throw new WrongValueException(this.paymentAmount,
							" Payble amount should be greater than 0. Please import the file. ");
				}
				instructions.setPaymentAmount(
						PennantAppUtil.unFormateAmount(this.paymentAmount.getActualValue(), getCcyFormat()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			// total Receviable Amt
			instructions.setReceivableAmount(
					PennantAppUtil.unFormateAmount(this.totalReceviableAmt.getActualValue(), getCcyFormat()));
			instructions.setNoOfReceivables(this.noOfReceivables.intValue());
			instructions.setAdjustedReceivable(this.adjustReceivables.isChecked());
		}

		doRemoveValidation();
		doRemoveLOVValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		if (!validate) {
			MessageUtil.showError("Please click on validate button and validate.");
			this.btnValidate.setFocus(true);
			return;
		}

		final InsurancePaymentInstructions paymentInstructions = new InsurancePaymentInstructions();
		BeanUtils.copyProperties(this.paymentInstructions, paymentInstructions);

		doWriteComponentsToBean(paymentInstructions, true);

		if (paymentInstructions.getPayableAmount().compareTo(paymentInstructions.getReceivableAmount()) == 0) {
			MessageUtil.showMessage(
					"Total Payble amount and Total Receivable amounts are equal, So there is no inaurance payment download.");
		}

		paymentInstructions.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		paymentInstructions.setStatus(DisbursementConstants.STATUS_APPROVED);
		paymentInstructions.setPaymentCCy(SysParamUtil.getAppCurrency());
		paymentInstructions.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		paymentInstructions.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		paymentInstructions.setUserDetails(getUserWorkspace().getLoggedInUser());

		getInsuranceFileImportService().saveInsurancePayments(paymentInstructions);
		try {
			refreshList();
			closeDialog();
		} catch (final DataAccessException e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	public InsuranceFileImportService getInsuranceFileImportService() {
		return insuranceFileImportService;
	}

	public void setInsuranceFileImportService(InsuranceFileImportService insuranceFileImportService) {
		this.insuranceFileImportService = insuranceFileImportService;
	}

	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public InsurancePaymentInstructions getPaymentInstructionsFromFile() {
		return paymentInstructionsFromFile;
	}

	public void setPaymentInstructionsFromFile(InsurancePaymentInstructions paymentInstructionsFromFile) {
		this.paymentInstructionsFromFile = paymentInstructionsFromFile;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public Media getMedia() {
		return media;
	}
}
