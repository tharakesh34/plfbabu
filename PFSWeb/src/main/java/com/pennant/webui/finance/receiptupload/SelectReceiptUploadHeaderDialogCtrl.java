package com.pennant.webui.finance.receiptupload;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.util.DateUtil.DateFormat;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectReceiptUploadHeaderDialogCtrl extends GFCBaseCtrl<UploadHeader> {

	private static final long serialVersionUID = 4783031677099154138L;
	private static final Logger logger = LogManager.getLogger(SelectReceiptUploadHeaderDialogCtrl.class);

	protected Window window_ReceiptUpload;

	protected Button btnBrowse;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Button btndownload;

	protected Textbox fileName;
	protected ExtendedCombobox entity;

	private Workbook workbook = null;
	private DataFormatter objDefaultFormat = new DataFormatter(); // for cell
																	// value
																	// formating
	private FormulaEvaluator formulaEvaluator = null; // for cell value
														// formating
	private String errorMsg = null;
	private ExcelFileImport fileImport = null;

	private ReceiptUploadHeader receiptUploadHeader = new ReceiptUploadHeader();
	private ReceiptUploadHeaderService receiptUploadHeaderService;

	private ReceiptUploadHeaderListCtrl receiptUploadHeaderListCtrl;
	private Media media = null;
	private String filePath = null;
	private File file;

	private List<ReceiptUploadDetail> rudList = new ArrayList<>();
	private FormulaEvaluator objFormulaEvaluator = null;
	private List<ReceiptUploadDetail> uploadNewList = new ArrayList<>();
	List<UploadAlloctionDetail> uadList = new ArrayList<>();

	private ReceiptService receiptService;

	public SelectReceiptUploadHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReceiptUpload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_ReceiptUpload);

		try {

			if (arguments.containsKey("receiptUploadListCtrl")) {
				setReceiptUploadHeaderListCtrl((ReceiptUploadHeaderListCtrl) arguments.get("receiptUploadListCtrl"));
			} else {
				setReceiptUploadHeaderListCtrl(null);
			}

			if (arguments.containsKey("receiptUploadHeader")) {
				this.receiptUploadHeader = (ReceiptUploadHeader) arguments.get("receiptUploadHeader");
			} else {
				this.setReceiptUploadHeader(null);
			}

			doSetFieldProperties();
			this.window_ReceiptUpload.doModal();
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

		this.entity.setModuleName("Entity");
		this.entity.setMandatoryStyle(true);
		this.entity.setDisplayStyle(2);
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param uploadHeader
	 *            The entity that need to be render.
	 */
	public void doShowDialog(ReceiptUploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> aruments = new HashMap<String, Object>();
		aruments.put("uploadReceiptHeader", uploadHeader);
		aruments.put("receiptUploadListCtrl", this.receiptUploadHeaderListCtrl);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/ReceiptUpload/ReceiptUploadHeaderDialog.zul", null,
					aruments);
			this.window_ReceiptUpload.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	@SuppressWarnings("unused")
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.receiptUploadHeader.isNew()) {
			this.btnBrowse.setVisible(true);
			this.btnBrowse.setDisabled(false);
		}

		this.fileName.setReadonly(true);

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		this.fileName.setReadonly(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		this.fileName.setConstraint("");
		this.fileName.setErrorMessage("");
		this.entity.setConstraint("");
		this.entity.setErrorMessage("");
	}

	/**
	 * Change the Entity Code
	 * 
	 * @param event
	 */
	public void onFulfill$entity(Event event) {
		logger.debug("Entering");
		this.entity.setConstraint("");
		this.entity.setErrorMessage("");
		if (StringUtils.isBlank(this.entity.getValue())) {
			this.entity.setValue("", "");
		}
		logger.debug("Leaving");
	}

	/**
	 * This Method/Event for getting the uploaded document should be comma separated values and then read the document
	 * and setting the values to the Lead VO and added those vos to the List and it also shows the information about
	 * where we go the wrong data
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnBrowse(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();
		this.fileName.setText("");
		this.fileImport = null;
		this.errorMsg = null;
		media = event.getMedia();

		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			return;
		}

		uploadNewList = new ArrayList<>();
		String fileName = media.getName();

		filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		this.fileImport = new ExcelFileImport(media, filePath);
		this.fileName.setText(fileName);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get the List of Rows in a sheet.<br>
	 * 
	 * @param workbook
	 * @param sheetIndex
	 * @param rowindex
	 * @return
	 */
	private List<String> getRowValuesByIndex(Workbook workbook, int sheetIndex, int rowindex) {

		List<String> rowValues = new ArrayList<String>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowindex);
		for (Cell cell : row) {
			this.formulaEvaluator.evaluate(cell);
			String cellValue = this.objDefaultFormat.formatCellValue(cell, this.formulaEvaluator);
			rowValues.add(cellValue.trim());
		}
		return rowValues;
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		doResetData();
	}

	/**
	 * Reset the Data onclick the refresh button
	 */
	private void doResetData() {
		doRemoveValidation();

		this.fileName.setText("");
		this.entity.setValue("");
		this.entity.setDescription("");

		this.fileImport = null;
		this.errorMsg = null;

		this.workbook = null;
		this.objDefaultFormat = new DataFormatter();// for cell value formating
		this.formulaEvaluator = null; // for cell value formating
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		rudList = new ArrayList<>();
		uadList = new ArrayList<>();

		validateFileName();

		try {

			// If any error message on Browsing File
			if (StringUtils.isNotBlank(this.errorMsg)) {
				throw new Exception(this.errorMsg);
			}

			// If read file having data or not
			if (media != null) {
				try {
					writeFile(media);
				} catch (Exception e) {
					MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Path_Invalid"));
					return;
				}
			}

			// On writing file, if it not exists then not allowed to proceed
			if (file == null) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Exists"));
				return;
			}

			// Excel file reading with headers, in case if any mismatch on
			// uploaded data with the format
			if (!validateFileContent()) {
				return;
			}

			boolean isError = validateFileData();
			// Load Data and blockers found
			if (isError) {
				return;
			}

			// Validate Receipt from service as inquiry
			validateReceipt();

			this.receiptUploadHeader.setReceiptUploadList(this.rudList);
			// Create backup file
			this.fileImport.backUpFile();
			doShowDialog(this.receiptUploadHeader);

		} catch (Exception e) {
			this.errorMsg = e.getMessage();
			doResetData();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Validate the ModuleType And File Name
	 */
	private void validateFileName() {
		doRemoveValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.trimToNull(this.fileName.getValue()) == null) {
				throw new WrongValueException(this.fileName, Labels.getLabel("empty_file"));
			} else if (StringUtils.trimToEmpty(this.fileName.getValue()).length() > 200) {
				throw new WrongValueException(this.fileName,
						this.fileName.getValue() + ": file name should not exceed 200 characters.");
			} else if (!this.fileName.getValue().toString().matches("^[a-zA-Z0-9 ._]*$")) {
				throw new WrongValueException(this.fileName, this.fileName.getValue()
						+ ": file name should not contain special characters, Allowed special charaters are space,dot and underScore.");
			} else {
				boolean fileExist = this.receiptUploadHeaderService.isFileNameExist(this.fileName.getValue());
				if (fileExist) {
					throw new WrongValueException(this.fileName,
							this.fileName.getValue() + ": file name already Exist.");
				}
			}
			this.receiptUploadHeader.setFileName(this.fileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.entity.isReadonly()) {
				this.entity.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptUpload_entity.value"), null, true, true));
				this.receiptUploadHeader.setEntityCode(this.entity.getValue());
				this.receiptUploadHeader.setEntityCodeDesc(this.entity.getDescription());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// set uploadprocess value to zero
		this.receiptUploadHeader.setUploadProgress(PennantConstants.RECEIPT_DEFAULT);

		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	/**
	 * Method for Validate uploaded file content whether data in proper manner or not
	 * 
	 * @throws Exception
	 */
	private boolean validateFileContent() {
		logger.debug(Literal.ENTERING);

		if (this.fileImport == null) {
			logger.debug(Literal.LEAVING);
			return false;
		}

		// Reading excel data and returning as a workbook
		try {
			this.workbook = this.fileImport.writeFile();
		} catch (Exception e) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Path_Invalid"));
			return false;
		}

		try {
			// If Workbook Not exists
			if (this.workbook == null) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoData"));
				return false;
			}

			Sheet sheet = this.workbook.getSheetAt(0);
			// If no Data exists or data without Headers
			if (sheet.getPhysicalNumberOfRows() <= 1) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoData"));
				return false;
			}

			// If Uploaded Receipt record count > 1000
			if (sheet.getPhysicalNumberOfRows() > 1001) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_MaxRows"));
				return false;
			}

			// Reading Excel based on uploaded format
			if (this.workbook instanceof HSSFWorkbook) {
				this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) this.workbook);
			} else if (this.workbook instanceof XSSFWorkbook) {
				this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) this.workbook);
			}

			// Validate First Sheet Header Keys
			List<String> fsHeaderKeys = getRowValuesByIndex(this.workbook, 0, 0);
			if (fsHeaderKeys == null || !(fsHeaderKeys.contains("<ROOT>_id") && fsHeaderKeys.contains("REFERENCE")
					&& fsHeaderKeys.contains("RECEIPTPURPOSE") && fsHeaderKeys.contains("EXCESSADJUSTTO")
					&& fsHeaderKeys.contains("ALLOCATIONTYPE") && fsHeaderKeys.contains("RECEIPTAMOUNT")
					&& fsHeaderKeys.contains("EFFECTSCHDMETHOD") && fsHeaderKeys.contains("REMARKS")
					&& fsHeaderKeys.contains("VALUEDATE") && fsHeaderKeys.contains("RECEIVEDDATE")
					&& fsHeaderKeys.contains("RECEIPTMODE") && fsHeaderKeys.contains("SUBRECEIPTMODE")
					&& fsHeaderKeys.contains("RECEIPTCHANNEL") && fsHeaderKeys.contains("FUNDINGAC")
					&& fsHeaderKeys.contains("PAYMENTREF") && fsHeaderKeys.contains("FAVOURNUMBER")
					&& fsHeaderKeys.contains("BANKCODE") && fsHeaderKeys.contains("CHEQUEACNO")
					&& fsHeaderKeys.contains("TRANSACTIONREF") && fsHeaderKeys.contains("STATUS")
					&& fsHeaderKeys.contains("DEPOSITDATE") && fsHeaderKeys.contains("REALIZATIONDATE")
					&& fsHeaderKeys.contains("INSTRUMENTDATE") && fsHeaderKeys.contains("PANNUMBER")
					&& fsHeaderKeys.contains("EXTERNALREF") && fsHeaderKeys.contains("COLLECTIONAGENT")
					&& fsHeaderKeys.contains("RECEIVEDFROM"))) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_Format_NotAllowed.value"));
				return false;
			}

			// Validate Second Sheet Header Keys
			List<String> shHeaderKeys = getRowValuesByIndex(this.workbook, 1, 0);
			if (shHeaderKeys == null || !(shHeaderKeys.contains("<ROOT>_id") && shHeaderKeys.contains("ALLOCATIONTYPE")
					&& shHeaderKeys.contains("REFERENCECODE") && shHeaderKeys.contains("PAIDAMOUNT")
					&& shHeaderKeys.contains("WAIVEDAMOUNT"))) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_Format_NotAllowed.value"));
				return false;
			}

		} catch (Exception e) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Invalid"));
			return false;
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method for Checking Uniqueness on ID
	 * 
	 * @return
	 */
	private boolean validateFileData() {
		logger.debug(Literal.ENTERING);

		final Set<String> setRowIds = new HashSet<String>();
		final Set<String> setTxnKeys = new HashSet<String>();
		final Set<String> setTxnKeysCheque = new HashSet<String>();
		Sheet rchSheet = this.workbook.getSheetAt(0);
		int rowCount = rchSheet.getLastRowNum();
		String txnKey = "";
		String errorMsg = "";

		for (int i = 1; i <= rowCount; i++) {
			Row rchRow = rchSheet.getRow(i);

			// To avoid possibility of blank row in between
			if (rchRow == null) {
				continue;
			}

			String strValue = getCellStringValue(rchRow, 0);

			if (StringUtils.isBlank(strValue)) {
				errorMsg = "<ROOT>_id with blank value";
				MessageUtil.showError(errorMsg);
				return true;
			}

			// Check for Row ID duplication
			if (!setRowIds.add(strValue)) {
				errorMsg = "<ROOT>_id " + strValue + " Has duplicate reference in Header Sheet";
				MessageUtil.showError(errorMsg);
				return true;
			}

			//duplicate check
			boolean dedupCheck = SysParamUtil.isAllowed(SMTParameterConstants.RECEIPTUPLOAD_DEDUPCHECK);
			ReceiptUploadDetail rud = loadReceiptData(rchRow);

			if (rud.getFavourNumber() != null && rud.getFavourNumber().length() > 6) {
				errorMsg = "Favour Number more than 6 digits";
				setErrorToRUD(rud, "90405", errorMsg);
			}
			if (dedupCheck) {
				// Load Receipt Header Data to Receipts Bean

				if (StringUtils.equalsIgnoreCase(rud.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_ONLINE)) {
					txnKey = rud.getReference() + "/" + rud.getTransactionRef() + "/" + rud.getSubReceiptMode();
					if (!setTxnKeys.add(txnKey)) {
						errorMsg = "with combination REFERENCE/TRANSACTIONREF/SubReceiptMode:" + txnKey;
						setErrorToRUD(rud, "90273", errorMsg);
					}
				} else if (StringUtils.equalsIgnoreCase(rud.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
						|| StringUtils.equalsIgnoreCase(rud.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_DD)) {
					txnKey = rud.getReference() + "/" + rud.getReceiptMode() + "/" + rud.getBankCode() + "/"
							+ rud.getFavourNumber();
					if (!setTxnKeysCheque.add(txnKey)) {
						errorMsg = "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey;
						setErrorToRUD(rud, "90273", errorMsg);
					}
				}
				boolean isRecDtlExist = isReceiptDetailExist(rud);
				if (!isRecDtlExist) {
					boolean isTranExist = false;
					if (StringUtils.equalsIgnoreCase(rud.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
							|| StringUtils.equalsIgnoreCase(rud.getReceiptMode(),
									DisbursementConstants.PAYMENT_TYPE_DD)) {
						String mode = rud.getReceiptMode();
						isTranExist = receiptUploadHeaderService.isChequeExist(rud.getReference(), mode,
								rud.getBankCode(), rud.getFavourNumber(), "_View");
						if (isTranExist) {
							errorMsg = "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey;
							setErrorToRUD(rud, "90273", errorMsg);
						}
					} else if (StringUtils.equalsIgnoreCase(rud.getReceiptMode(),
							DisbursementConstants.PAYMENT_TYPE_ONLINE)) {
						isTranExist = receiptUploadHeaderService.isOnlineExist(rud.getReference(),
								rud.getSubReceiptMode(), rud.getTransactionRef(), "_View");
						if (isTranExist) {
							errorMsg = "with combination REFERENCE/ReceiptMode/BankCode/FavourNumber:" + txnKey;
							setErrorToRUD(rud, "90273", errorMsg);
						}
					}
				}
			}

			//duplicate check
			//check  FinReference whether it is present in maker stage
			if (StringUtils.equals(rud.getReceiptPurpose(), FinanceConstants.EARLYSETTLEMENT)
					|| StringUtils.equals(rud.getReceiptPurpose(), FinanceConstants.PARTIALSETTLEMENT)) {
				if (StringUtils.isNotBlank(this.fileName.getValue())) {
					String finReferenceValue = getReceiptUploadHeaderService().getLoanReferenc(rud.getReference(),
							this.fileName.getValue());
					if (StringUtils.isNotBlank(finReferenceValue)) {
						errorMsg = "Receipt In process for " + rud.getReference();
						setErrorToRUD(rud, "90273", errorMsg);

					}
				}

			}
			ErrorDetail errorDetail = receiptService.getWaiverValidation(rud.getReference(), rud.getReceiptPurpose(),
					rud.getValueDate());
			if (errorDetail != null) {
				rud.getErrorDetails().add(ErrorUtil.getErrorDetail(errorDetail));
			}

			rudList.add(rud);
		}

		if (rudList == null || rudList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoData"));
			return true;
		}

		// Load Allocation Details to an array
		loadAllocationFromUL();

		// Validate Receipt Header Vs Allocations
		validateRUDvsRAD();

		/*
		 * if (uadList != null && !uadList.isEmpty()) {
		 * MessageUtil.showError("Allocations not related to Receipts found in allocation sheet"); return true; }
		 */

		logger.debug(Literal.LEAVING);
		return false;
	}

	private boolean isReceiptDetailExist(ReceiptUploadDetail rud) {
		boolean isreceiptdataExits = false;
		if ((StringUtils.equalsIgnoreCase(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equalsIgnoreCase(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))
				&& StringUtils.equalsIgnoreCase(rud.getStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			String mode = rud.getReceiptMode();

			if (StringUtils.equalsIgnoreCase(rud.getReceiptPurpose(), "SP")) {
				isreceiptdataExits = receiptUploadHeaderService.isReceiptDetailsExits(rud.getReference(), mode,
						rud.getChequeNo(), rud.getFavourNumber(), "");
			} else {
				isreceiptdataExits = receiptUploadHeaderService.isReceiptDetailsExits(rud.getReference(), mode,
						rud.getChequeNo(), rud.getFavourNumber(), "_Temp");
			}

		}
		return isreceiptdataExits;

	}

	/**
	 * Method for Loading Receipt Details from uploaded Excel File
	 * 
	 * @param rchRow
	 * @return
	 */
	private ReceiptUploadDetail loadReceiptData(Row rchRow) {
		logger.debug(Literal.ENTERING);
		Date appDate = DateUtility.getAppDate();
		ReceiptUploadDetail rud = new ReceiptUploadDetail();
		String strValue = "";
		long longValue = 0;
		Date dateValue = appDate;

		// Root ID
		strValue = getCellStringValue(rchRow, 0).trim();
		rud.setRootId(strValue);

		if (strValue.length() > 4) {
			setErrorToRUD(rud, "RU0040", "[<ROOT>_id] with length > 4 ");
			rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);
		}

		// Loan Reference
		strValue = getCellStringValue(rchRow, 1).trim().toUpperCase();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReference(strValue);
		} else {
			setErrorToRUD(rud, "RU0040", "Blanks/Nulls in [REFERENCE] ");
		}

		// Receipt Purpose
		strValue = getCellStringValue(rchRow, 2).trim().toUpperCase();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReceiptPurpose(strValue);
		} else {
			setErrorToRUD(rud, "RU0040", "Blanks/Nulls in [RECEIPTPURPOSE] ");
		}

		if (!StringUtils.equals(strValue, "SP") && !StringUtils.equals(strValue, "EP")
				&& !StringUtils.equals(strValue, "ES")) {
			setErrorToRUD(rud, "RU0040", "Values other than SP/EP/ES in [RECEIPTPURPOSE] ");
		}

		// Excess Adjusted to
		strValue = getCellStringValue(rchRow, 3).trim();
		if (StringUtils.isBlank(strValue)) {
			strValue = "E";
		}

		if (!StringUtils.equals(strValue, "E") && !StringUtils.equals(strValue, "A") && StringUtils.isNotBlank(strValue)
				&& !StringUtils.equals(strValue, "#")) {
			setErrorToRUD(rud, "RU0040", "Values other than E/A/ /# in [EXCESSADJUSTTO] ");
		} else {
			rud.setExcessAdjustTo(strValue);
		}

		// Allocation type
		strValue = getCellStringValue(rchRow, 4);
		if (StringUtils.isBlank(strValue)) {
			strValue = "A";
		}

		if (!StringUtils.equals(strValue, "A") && !StringUtils.equals(strValue, "M")) {
			setErrorToRUD(rud, "RU0040", "Values other than A/M in [ALLOCATIONTYPE] ");
		} else {
			rud.setAllocationType(strValue);
		}

		if (StringUtils.equals(strValue, "M") && !StringUtils.equals(rud.getReceiptPurpose(), "SP")) {
			setErrorToRUD(rud, "RU0040", "Values other than A in [ALLOCATIONTYPE] ");
		}

		// Receipt Amount
		strValue = getCellStringValue(rchRow, 5);
		if (StringUtils.isBlank(strValue)) {
			strValue = "0";
		}

		try {
			//receipt upload issue fixed allow the decimal values in receipupload file receiptamount(27-12-2019)
			BigDecimal precisionAmount = new BigDecimal(strValue);
			precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
			BigDecimal actualAmount = precisionAmount;

			precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
			if (precisionAmount.compareTo(actualAmount) != 0) {
				actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
				setErrorToRUD(rud, "RU0040", "Minor Currency (Decimals) in [RECEIPTAMOUNT] ");
				rud.setReceiptAmount(actualAmount);
			} else {
				//precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
				rud.setReceiptAmount(precisionAmount);
			}

			if (precisionAmount.compareTo(BigDecimal.ZERO) <= 0) {
				setErrorToRUD(rud, "RU0040", "[RECEIPTAMOUNT] with value <=0 ");
			}
		} catch (Exception e) {
			rud.setReceiptAmount(BigDecimal.ZERO);
			setErrorToRUD(rud, "RU0040", "[RECEIPTAMOUNT] ");
		}

		// Effective Schedule Method
		strValue = getCellStringValue(rchRow, 6);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setEffectSchdMethod(strValue);
		}

		// Remarks
		strValue = getCellStringValue(rchRow, 7).trim();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setRemarks(strValue);
		}

		if (strValue.length() > 100) {
			setErrorToRUD(rud, "RU0040", "[REMARKS] with length more than 100 characters");

		}

		// Value Date
		strValue = getCellStringValue(rchRow, 8);
		try {
			if (StringUtils.isNotBlank(strValue)) {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				rud.setValueDate(dateValue);
			} else {

				setErrorToRUD(rud, "RU0040", "Blanks in [VALUEDATE] ");
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [VALUEDATE] ");
		}

		// Received Date
		strValue = getCellStringValue(rchRow, 9);

		try {
			if (StringUtils.isNotBlank(strValue)) {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				rud.setReceivedDate(dateValue);
			} else {
				setErrorToRUD(rud, "RU0040", "Blanks in [RECEIVEDDATE] ");
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [RECEIVEDDATE] ");
		}

		// 30-08-19:Date comparision should be with date and not on string
		// Value Date and Received Date
		String strValueDate = getCellStringValue(rchRow, 8);
		String strReceivedDate = getCellStringValue(rchRow, 9);

		try {
			if (StringUtils.isNotBlank(strValueDate) && StringUtils.isNotBlank(strReceivedDate)
					&& !(DateUtility.getDate(strReceivedDate, DateFormat.LONG_DATE.getPattern())
							.compareTo(DateUtility.getDate(strValueDate, DateFormat.LONG_DATE.getPattern())) >= 0)) {
				setErrorToRUD(rud, "RU0008", "");
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0008", "");
		}
		// Receipt Mode
		strValue = getCellStringValue(rchRow, 10);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReceiptMode(strValue);
		}

		if (strValue.length() > 10) {
			setErrorToRUD(rud, "RU0040", "[RECEIPTMODE] with length more than 10 characters");
		}

		// Sub Receipt Mode
		strValue = getCellStringValue(rchRow, 11);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setSubReceiptMode(strValue);
		}

		if (strValue.length() > 11) {
			setErrorToRUD(rud, "RU0040", "[SUBRECEIPTMODE] with length more than 10 characters");
		}

		// Receipt Channel
		strValue = getCellStringValue(rchRow, 12);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReceiptChannel(strValue);
		}

		if (strValue.length() > 10) {
			setErrorToRUD(rud, "RU0040", "[RECEIPTCHANNEL] with length more than 10 characters");
		}

		try {
			if (StringUtils.isBlank(strValue)
					&& (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CASH)
							|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
							|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))) {
				setErrorToRUD(rud, "RU0040", "Blanks in [RECEIPTCHANNEL]");
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Blanks in [RECEIPTCHANNEL]");
		}

		// Funding Account
		strValue = getCellStringValue(rchRow, 13);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setFundingAc(strValue);
		}

		if (strValue.length() > 8) {
			setErrorToRUD(rud, "RU0040", "[FUNDINGAC] with length more than 8 characters");
		}

		// Payment reference
		strValue = getCellStringValue(rchRow, 14);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setPaymentRef(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[PAYMENTREF] with length more than 50 characters");
		}

		// Favour Number
		strValue = getCellStringValue(rchRow, 15).trim();

		//Check no validation in case payment type cheque
		if (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				&& StringUtils.isBlank(strValue)) {
			setErrorToRUD(rud, "RU0040", "[FAVOURNUMBER] is Mandatary");
		}

		if (StringUtils.isNotBlank(strValue)) {
			rud.setFavourNumber(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[FAVOURNUMBER] with length more than 50");
		}

		// Bank Code
		strValue = getCellStringValue(rchRow, 16).trim();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setBankCode(strValue);
		}

		// Cheque Account Number
		strValue = getCellStringValue(rchRow, 17).trim();
		if (StringUtils.isNotBlank(strValue)) {
			rud.setChequeNo(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[CHEQUEACNO] with length more than 50");
		}

		// Transaction Reference
		strValue = getCellStringValue(rchRow, 18);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setTransactionRef(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[TRANSACTIONREF] with length more than 50");
		}

		// Status
		strValue = getCellStringValue(rchRow, 19);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setStatus(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[STATUS] with length more than 1");
		}

		// Deposit Date
		strValue = getCellStringValue(rchRow, 20);
		try {
			if (StringUtils.isBlank(strValue)
					&& (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
							|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))) {
				setErrorToRUD(rud, "RU0040", "Blanks in [DEPOSITDATE] ");
			} else {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				rud.setDepositDate(dateValue);
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [DEPOSITDATE] ");
		}

		// Realization Date
		strValue = getCellStringValue(rchRow, 21);
		try {
			if (StringUtils.isBlank(strValue)
					&& (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
							|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))) {
				setErrorToRUD(rud, "RU0040", "Blanks in [REALIZATIONDATE] ");
			} else {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				rud.setRealizationDate(dateValue);
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [REALIZATIONDATE] ");
		}

		// Instrument Date
		strValue = getCellStringValue(rchRow, 22);
		try {
			if (StringUtils.isNotBlank(strValue)) {
				dateValue = DateUtility.parse(strValue, DateFormat.LONG_DATE.getPattern());
				// rch.set
			} else {
				if (StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
						|| StringUtils.equals(rud.getReceiptMode(), RepayConstants.RECEIPTMODE_DD)) {
					setErrorToRUD(rud, "RU0040", "Blanks in [INSTRUMENTDATE] ");
				}
			}
		} catch (Exception e) {
			setErrorToRUD(rud, "RU0040", "Value in [INSTRUMENTDATE] ");
		}

		// PAN Number
		strValue = getCellStringValue(rchRow, 23);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setPanNumber(strValue);
		}

		// External Reference
		strValue = getCellStringValue(rchRow, 24);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setExtReference(strValue);
		}

		if (strValue.length() > 50) {
			setErrorToRUD(rud, "RU0040", "[EXTERNALREF] with length more than 1 ");
		}

		// Collection Agent
		strValue = getCellStringValue(rchRow, 25);

		if (StringUtils.isBlank(strValue)) {
			strValue = "0";
		}

		if (StringUtils.isNumeric(strValue)) {
			longValue = Long.parseLong(strValue);
			rud.setCollectionAgentId(longValue);
		} else {
			setErrorToRUD(rud, "RU0040", "Non numeric value in [COLLECTIONAGENT] ");
		}

		// Received From
		strValue = getCellStringValue(rchRow, 26);
		if (StringUtils.isNotBlank(strValue)) {
			rud.setReceivedFrom(strValue);
		}

		if (rud.getErrorDetails() == null || rud.getErrorDetails().isEmpty()) {
			rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
			rud.setReason("");
		} else {
			rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);
			rud.setReason(rud.getErrorDetails().get(0).getError());
		}

		logger.debug(Literal.LEAVING);
		return rud;
	}

	/**
	 * Method for Loading Allocation details from uploaded Excel File
	 */
	private void loadAllocationFromUL() {
		logger.debug(Literal.ENTERING);

		String strValue = "";

		Sheet rchSheet = this.workbook.getSheetAt(1);
		int rowCount = rchSheet.getLastRowNum();

		// Just Load the valid fields
		for (int i = 1; i <= rowCount; i++) {
			Row rchRow = rchSheet.getRow(i);

			// To avoid possibility of blank row in between
			if (rchRow == null) {
				continue;
			}

			UploadAlloctionDetail uad = new UploadAlloctionDetail();
			strValue = getCellStringValue(rchRow, 0);
			if (StringUtils.isBlank(strValue)) {
				setErrorToUAD(uad, "RU0040", "Allocation Sheet: <ROOT>_id with blank value");
			}

			uad.setRootId(strValue);

			strValue = getCellStringValue(rchRow, 1);
			if (StringUtils.isBlank(strValue)) {
				setErrorToUAD(uad, "RU0040", "Allocation Sheet: [ALLOCATIONTYPE] with blank value ");
			}
			uad.setAllocationType(strValue);

			strValue = getCellStringValue(rchRow, 2);
			if (StringUtils.isNotBlank(strValue)) {
				if (strValue.length() > 8) {
					setErrorToUAD(uad, "RU0040",
							"Allocation Sheet: [REFERENCECODE] with lenght more than 8 characters ");
				}
			}
			uad.setReferenceCode(strValue);

			strValue = getCellStringValue(rchRow, 3);
			if (StringUtils.isBlank(strValue)) {
				strValue = "0";
			}

			try {
				BigDecimal precisionAmount = new BigDecimal(strValue);
				precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
				BigDecimal actualAmount = precisionAmount;

				precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
				if (precisionAmount.compareTo(actualAmount) != 0) {
					actualAmount = actualAmount.multiply(BigDecimal.valueOf(100));
					actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
					setErrorToUAD(uad, "RU0040", "Allocation Sheet: Minor Currency (Decimals) in [PAIDAMOUNT] ");
					uad.setPaidAmount(actualAmount);
				} else {
					uad.setPaidAmount(precisionAmount);
				}

				if (precisionAmount.compareTo(BigDecimal.ZERO) < 0) {
					setErrorToUAD(uad, "RU0040", "Allocation Sheet: [PAIDAMOUNT] with value <0 ");
				}
			} catch (Exception e) {
				uad.setPaidAmount(BigDecimal.ZERO);
				setErrorToUAD(uad, "RU0040", "Allocation Sheet: [PAIDAMOUNT] ");
			}

			strValue = getCellStringValue(rchRow, 4);
			if (StringUtils.isBlank(strValue)) {
				strValue = "0";
			}

			try {
				BigDecimal precisionAmount = new BigDecimal(strValue);
				BigDecimal actualAmount = precisionAmount;

				precisionAmount = precisionAmount.setScale(0, RoundingMode.HALF_DOWN);
				if (precisionAmount.compareTo(actualAmount) != 0) {
					actualAmount = actualAmount.multiply(BigDecimal.valueOf(100));
					actualAmount = actualAmount.setScale(0, RoundingMode.HALF_DOWN);
					setErrorToUAD(uad, "RU0040", "Allocation Sheet: Minor Currency (Decimals) in [WAIVEDAMOUNT] ");
					uad.setWaivedAmount(actualAmount);
				} else {
					precisionAmount = precisionAmount.multiply(BigDecimal.valueOf(100));
					uad.setWaivedAmount(precisionAmount);
				}

				if (precisionAmount.compareTo(BigDecimal.ZERO) < 0) {
					setErrorToUAD(uad, "RU0040", "Allocation Sheet: [WAIVEDAMOUNT] with value <0 ");
				}
			} catch (Exception e) {
				uad.setWaivedAmount(BigDecimal.ZERO);
				setErrorToUAD(uad, "RU0040", "Allocation Sheet: [WAIVEDAMOUNT] ");
			}

			// Add to Uploaded allocation details list
			uadList.add(uad);
		}

		logger.debug(Literal.LEAVING);
		return;

	}

	/**
	 * Validate Receipt Details against uploaded Allocation Details
	 */
	private void validateRUDvsRAD() {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < rudList.size(); i++) {
			ReceiptUploadDetail rud = rudList.get(i);
			List<UploadAlloctionDetail> radList = setFromUadList(rud.getRootId());
			rud.setListAllocationDetails(radList);

			boolean isManualAloc = false;
			if (StringUtils.equals(rud.getAllocationType(), "M")) {
				isManualAloc = true;
			}

			if (isManualAloc && radList.isEmpty()) {
				setErrorToRUD(rud, "RU0040", "Allocation Type is M but allocations not found");
			} else if (!isManualAloc && !radList.isEmpty()) {
				setErrorToRUD(rud, "RU0040", "Allocation Type is A but allocations found");
			}

			// Bring any errors from allocation details to header details
			if (radList.isEmpty()) {
				continue;
			}

			// Bring errors from allocation details to header details
			setErrorsToRUD(rud);

			// Validate sum of allocations against the receipt amount
			BigDecimal manualAllocated = BigDecimal.ZERO;
			for (int j = 0; j < radList.size(); j++) {
				manualAllocated = manualAllocated.add(radList.get(j).getPaidAmount());
			}

			if (manualAllocated.compareTo(rud.getReceiptAmount()) != 0) {
				String strAlloate = PennantApplicationUtil.amountFormate(manualAllocated, 2);
				setErrorToRUD(rud, "RU0040", "Manual allocation " + strAlloate + " Not matching with Receipt Amount ");
			}

			if (rud.getErrorDetails() == null || rud.getErrorDetails().isEmpty()) {
				rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
				rud.setReason("");
			} else {
				rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);
				rud.setReason(rud.getErrorDetails().get(0).getError());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private List<UploadAlloctionDetail> setFromUadList(String rootID) {
		List<UploadAlloctionDetail> radList = new ArrayList<>();
		for (int i = 0; i < uadList.size(); i++) {
			UploadAlloctionDetail uad = uadList.get(i);
			if (StringUtils.equals(rootID, uad.getRootId())) {
				radList.add(uad);
				uadList.remove(i);
				i = i - 1;
			}
		}
		return radList;
	}

	private void setErrorsToRUD(ReceiptUploadDetail rud) {
		List<UploadAlloctionDetail> list = rud.getListAllocationDetails();

		for (UploadAlloctionDetail uploadAlloctionDetail : list) {
			List<ErrorDetail> uadErrors = uploadAlloctionDetail.getErrorDetails();
			if (CollectionUtils.isEmpty(uadErrors)) {
				continue;
			}

			for (ErrorDetail errorDetail : uadErrors) {
				rud.getErrorDetails().add(errorDetail);
			}
		}
	}

	private String getCellStringValue(Row rchRow, int cellIdx) {
		Cell cell = rchRow.getCell(cellIdx);
		return StringUtils.trimToEmpty(objDefaultFormat.formatCellValue(cell, objFormulaEvaluator));
	}

	public void setErrorToRUD(ReceiptUploadDetail rud, String errorCode, String parm0) {
		String[] valueParm = new String[1];
		valueParm[0] = parm0;
		rud.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm)));
	}

	public void setErrorToUAD(UploadAlloctionDetail uad, String errorCode, String parm0) {
		String[] valueParm = new String[1];
		valueParm[0] = parm0;
		uad.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, "", valueParm)));
	}

	/**
	 * Writing Media file into File Object
	 * 
	 * @param media
	 * @throws IOException
	 */
	private void writeFile(Media media) throws IOException {
		logger.debug(Literal.ENTERING);

		File parent = new File(filePath);

		// IF Directory Does not Exists
		if (!parent.exists()) {
			parent.mkdirs();
		}

		file = new File(parent.getPath().concat(File.separator).concat(media.getName()));

		// If File already exists in path, we need to delete first before
		// replace
		if (file.exists()) {
			file.delete();
		}

		// Creating File in Server path for Loading/Reading file from path
		file.createNewFile();
		FileUtils.writeByteArrayToFile(file, media.getByteData());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Validate Receipt Details against Each Record
	 */
	private void validateReceipt() {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < rudList.size(); i++) {
			ReceiptUploadDetail rud = rudList.get(i);

			// Already error marked. No need to find new errors
			if (!rud.getErrorDetails().isEmpty()) {
				rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);

				String code = StringUtils.trimToEmpty(rud.getErrorDetails().get(0).getCode());
				String description = StringUtils.trimToEmpty(rud.getErrorDetails().get(0).getError());

				rud.setReason(String.format("%s %s %s", code, "-", description));
				continue;
			}

			FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(rud, this.entity.getValidatedValue());
			fsi.setReqType("Inquiry");
			fsi.setReceiptUpload(true);
			FinanceDetail financeDetail = receiptService.receiptTransaction(fsi, fsi.getReceiptPurpose());

			WSReturnStatus returnStatus = financeDetail.getReturnStatus();
			if (returnStatus != null) {
				rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);

				String code = StringUtils.trimToEmpty(returnStatus.getReturnCode());
				String description = StringUtils.trimToEmpty(returnStatus.getReturnText());

				rud.setReason(String.format("%s %s %s", code, "-", description));
			} else {
				rud.setUploadStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
				rud.setReason("");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * validate each object with list and check dedub for loan reference with status,transaction ref,cheque or dd number
	 * 
	 * @param receiptUploadDetail
	 * @return dedup Check
	 */
	@SuppressWarnings("unused")
	private boolean checkDedupCondition(ReceiptUploadDetail receiptUploadDetail) {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < uploadNewList.size(); i++) {

			if (StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(),
					DisbursementConstants.PAYMENT_TYPE_NEFT)
					|| StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(),
							DisbursementConstants.PAYMENT_TYPE_RTGS)
					|| StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(),
							DisbursementConstants.PAYMENT_TYPE_IMPS)) {
				if (uploadNewList.get(i).getReference().equals(receiptUploadDetail.getReference())
						&& uploadNewList.get(i).getReceiptMode().equals(receiptUploadDetail.getReceiptMode())
						&& uploadNewList.get(i).getTransactionRef().equals(receiptUploadDetail.getTransactionRef())) {

					logger.debug(Literal.LEAVING);
					return true;
				}
			} else if (StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(),
					DisbursementConstants.PAYMENT_TYPE_CHEQUE)
					|| StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(),
							DisbursementConstants.PAYMENT_TYPE_DD)) {
				if (uploadNewList.get(i).getReference().equals(receiptUploadDetail.getReference())
						&& uploadNewList.get(i).getReceiptMode().equals(receiptUploadDetail.getReceiptMode())
						&& uploadNewList.get(i).getBankCode().equals(receiptUploadDetail.getBankCode())
						&& uploadNewList.get(i).getFavourNumber().equals(receiptUploadDetail.getFavourNumber())) {

					logger.debug(Literal.LEAVING);
					return true;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return false;
	}

	public void onClick$btnClose(Event event) {
		this.window_ReceiptUpload.onClose();
	}

	public ReceiptUploadHeader getReceiptUploadHeader() {
		return receiptUploadHeader;
	}

	public void setReceiptUploadHeader(ReceiptUploadHeader receiptUploadHeader) {
		this.receiptUploadHeader = receiptUploadHeader;
	}

	public ReceiptUploadHeaderService getReceiptUploadHeaderService() {
		return receiptUploadHeaderService;
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

	public ReceiptUploadHeaderListCtrl getReceiptUploadHeaderListCtrl() {
		return receiptUploadHeaderListCtrl;
	}

	public void setReceiptUploadHeaderListCtrl(ReceiptUploadHeaderListCtrl receiptUploadHeaderListCtrl) {
		this.receiptUploadHeaderListCtrl = receiptUploadHeaderListCtrl;
	}

	public ReceiptService getReceiptService() {
		return receiptService;
	}

	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}
}
