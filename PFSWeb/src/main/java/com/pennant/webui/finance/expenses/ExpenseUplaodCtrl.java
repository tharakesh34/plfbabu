package com.pennant.webui.finance.expenses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennant.backend.model.expenses.UploadFinExpenses;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExpenseUplaodCtrl extends GFCBaseCtrl<UploadHeader> {

	private static final long serialVersionUID = 4783031677099154138L;
	private static final Logger logger = LogManager.getLogger(ExpenseUplaodCtrl.class);

	protected Window window_ExpenseUpload;

	protected Button btnBrowse;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Button btndownload;

	protected Textbox txtFileName;
	protected Row panelRow;
	protected Combobox moduleType;
	protected Button sampleFileDownload;

	protected Label fileName;
	protected Label totalCount;
	protected Label successCount;
	protected Label failedCount;

	protected Grid statusGrid;
	private String errorMsg = null;

	private UploadHeader uploadHeader = new UploadHeader();

	private transient DataFormatter objDefaultFormat = new DataFormatter();
	private transient FormulaEvaluator formulaEvaluator = null;
	private transient Media media;

	private transient UploadHeaderService uploadHeaderService;

	public ExpenseUplaodCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExpenseUpload";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ExpenseUpload(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(this.window_ExpenseUpload);

		try {

			// Store the before image.
			UploadHeader uploadHeader = new UploadHeader();
			BeanUtils.copyProperties(this.uploadHeader, uploadHeader);
			this.uploadHeader.setBefImage(uploadHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.uploadHeader.isWorkflow(), this.uploadHeader.getWorkflowId(),
					this.uploadHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.uploadHeader);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		this.statusGrid.setVisible(false);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnBrowse.setVisible(getUserWorkspace().isAllowed("button_ExpenseUpload_Browse"));
		this.btnRefresh.setVisible(getUserWorkspace().isAllowed("button_ExpenseUpload_Refresh"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExpenseUpload_Save"));
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic The entity that need to be render.
	 */
	public void doShowDialog(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		this.sampleFileDownload.setDisabled(true);
		if (uploadHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.moduleType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.moduleType.focus();
				if (StringUtils.isNotBlank(uploadHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		doWriteBeanToComponents(uploadHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param academic
	 * 
	 */
	public void doWriteBeanToComponents(UploadHeader uploadHeader) {
		logger.debug("Entering");

		fillComboBox(this.moduleType, uploadHeader.getModule(), PennantStaticListUtil.getUploadLevelsList(), "");

		logger.debug("Leaving");
	}

	public void onSelect$moduleType() {
		if (getComboboxValue(this.moduleType).contains("Loan")) {
			this.sampleFileDownload.setDisabled(false);
		} else {
			this.sampleFileDownload.setDisabled(true);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.uploadHeader.isNewRecord()) {
			readOnlyComponent(false, this.moduleType);
			this.btnBrowse.setVisible(true);
			this.btnBrowse.setDisabled(false);
		}

		this.txtFileName.setReadonly(true);
		readOnlyComponent(isReadOnly("ExpenseUpload_UploadLevel"), this.moduleType);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.uploadHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		readOnlyComponent(true, this.moduleType);
		this.txtFileName.setReadonly(true);

		if (isWorkFlowEnabled()) {

			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}

			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.moduleType.setConstraint("");
		this.moduleType.setErrorMessage("");

		this.txtFileName.setConstraint("");
		this.txtFileName.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * This Method/Event for getting the uploaded document should be comma separated values and then read the document
	 * and setting the values to the Lead VO and added those vos to the List and it also shows the information about
	 * where we go the wrong data
	 * 
	 * @param event
	 */
	public void onUpload$btnBrowse(UploadEvent event) {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setText("");
		this.fileName.setValue("");
		this.errorMsg = null;

		doRemoveValidation();

		isValidComboValue(this.moduleType, Labels.getLabel("label_ExpenseUpload_UploadLevel.value"));

		readOnlyComponent(true, this.moduleType);

		this.media = event.getMedia();

		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			readOnlyComponent(false, this.moduleType);
			this.media = null;
			return;
		}

		this.txtFileName.setText(this.media.getName());
		this.fileName.setValue(this.media.getName());

		logger.debug(Literal.LEAVING);
	}

	private String getFolderPath() {
		String path = PathUtil.FILE_UPLOADS_PATH.concat(File.separator).concat("ExpenseUploads");
		path = path.concat(File.separator).concat(getComboboxValue(this.moduleType));
		return path;
	}

	/**
	 * when the "Process" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btndownload(Event event) {
		logger.debug(Literal.ENTERING);

		String whereCond = " and FILENAME in (" + "'" + this.txtFileName.getValue() + "'" + ")";
		StringBuilder searchCriteria = new StringBuilder(" ");
		searchCriteria.append("File Name is " + this.txtFileName.getValue());

		String selectedModuleType = getComboboxValue(this.moduleType);
		String reportName = "";

		if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(selectedModuleType)) {
			reportName = "ExpenseReport_LoanLevel";
		} else if (PennantConstants.EXPENSE_UPLOAD_LOANTYPE.equals(selectedModuleType)) {
			reportName = "ExpenseReport_LoanType";
		}

		String userName = getUserWorkspace().getLoggedInUser().getFullName();

		try {
			ReportsUtil.generateReport(userName, reportName, whereCond, searchCriteria);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * entry point of program, reading whole excel and calling other methods to prepare jsonObject.
	 * 
	 * @return String
	 */
	private List<UploadFinExpenses> processUploadFinExpenses(Workbook workbook, String moduleType, long uploadId) {
		logger.debug("Entering");

		List<UploadFinExpenses> expenses = new ArrayList<>();

		if (PennantConstants.EXPENSE_UPLOAD_LOANTYPE.equals(moduleType)) {
			expenses = readLoanTypeWiseExpense(workbook, uploadId);
		} else if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(moduleType)) {
			expenses = readLoanWiseExpense(workbook, uploadId);
		}

		logger.debug(Literal.LEAVING);

		return expenses;
	}

	private List<UploadFinExpenses> readLoanWiseExpense(Workbook workbook, long uploadId) {
		List<UploadFinExpenses> uploadFinExpensesList = new ArrayList<>();

		Sheet sheet = workbook.getSheetAt(0);
		Iterator<org.apache.poi.ss.usermodel.Row> rows = sheet.iterator();

		String finReference;
		String expenseTypeCode;
		String percentage;
		String amountValue;
		String appendOrOverride;

		while (rows.hasNext()) {
			org.apache.poi.ss.usermodel.Row row = rows.next();
			int rowIndex = row.getRowNum();
			List<String> rowValue = null;

			if (rowIndex > 0) {
				rowValue = getRowValuesByIndex(workbook, 0, rowIndex);
			}

			if (CollectionUtils.isEmpty(rowValue)) {
				continue;
			}

			String reason = null;
			boolean valid = true;

			if (rowValue.size() < 5) {
				MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
				return new ArrayList<>();
			}

			finReference = rowValue.get(0);
			expenseTypeCode = rowValue.get(1);
			percentage = rowValue.get(2);
			amountValue = rowValue.get(3);
			appendOrOverride = rowValue.get(4);

			UploadFinExpenses uploadFinExpenses = new UploadFinExpenses();
			uploadFinExpenses.setUploadId(uploadId);
			uploadFinExpenses.setFinReference(finReference);
			uploadFinExpenses.setType(appendOrOverride);
			uploadFinExpenses.setExpenseTypeCode(expenseTypeCode);

			valid = validateCommonData(uploadFinExpenses, expenseTypeCode, percentage, amountValue, valid, reason);
			reason = uploadFinExpenses.getReason();

			// FinReference
			if (StringUtils.isBlank(finReference)) {
				if (valid) {
					valid = false;
					reason = "Loan Reference is mandatory";
				} else {
					reason = reason + "| Loan Reference is mandatory";
				}
				uploadFinExpenses.setFinReference("FINREF");
			} else if (finReference.length() > 20) {
				if (valid) {
					valid = false;
					reason = "Expense Type Code : (" + finReference
							+ ") length is exceeded, it should be lessthan or equal to 8.";
				} else {
					reason = reason + "| Expense Type Code : (" + finReference
							+ ") length is exceeded, it should be lessthan or equal to 8.";
				}
				uploadFinExpenses.setFinReference(finReference.substring(0, 20));
			} else {
				Long finID = this.uploadHeaderService.getActiveFinID(finReference);

				if (finID == null) {
					if (valid) {
						reason = "Loan Reference: (" + finReference + ") is not valid.";
						valid = false;
					} else {
						reason = reason + "|Loan Reference: (" + finReference + ") is not valid.";
					}
				}
			}

			if (valid) {
				uploadFinExpenses.setStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
			} else {
				uploadFinExpenses.setStatus(PennantConstants.UPLOAD_STATUS_FAIL);
			}

			uploadFinExpenses.setReason(reason);

			uploadFinExpensesList.add(uploadFinExpenses);
		}

		return uploadFinExpensesList;
	}

	private List<UploadFinExpenses> readLoanTypeWiseExpense(Workbook workbook, long uploadId) {
		List<UploadFinExpenses> uploadFinExpensesList = new ArrayList<>();

		Sheet sheet = workbook.getSheetAt(0);
		Iterator<org.apache.poi.ss.usermodel.Row> rows = sheet.iterator();

		String finType;
		String fromDate;
		String toDate;
		String expenseTypeCode;
		String percentage;
		String amountValue;
		String appendOrOverride;
		Date financeStartDate = null;
		Date financeEndDate = null;

		while (rows.hasNext()) {
			org.apache.poi.ss.usermodel.Row row = rows.next();
			int rowIndex = row.getRowNum();
			List<String> rowValue = null;

			if (rowIndex > 0) {
				rowValue = getRowValuesByIndex(workbook, 0, rowIndex);
			}

			if (CollectionUtils.isEmpty(rowValue)) {
				continue;
			}

			if (rowValue.size() < 7) {
				MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
				return new ArrayList<>();
			}

			String reason = null;
			boolean valid = true;

			finType = rowValue.get(0);
			fromDate = rowValue.get(1);
			toDate = rowValue.get(2);
			expenseTypeCode = rowValue.get(3);
			percentage = rowValue.get(4);
			amountValue = rowValue.get(5);
			appendOrOverride = rowValue.get(6);

			UploadFinExpenses uploadFinExpense = new UploadFinExpenses();
			uploadFinExpense.setUploadId(uploadId);
			uploadFinExpense.setFinType(finType);
			uploadFinExpense.setType(appendOrOverride);
			uploadFinExpense.setExpenseTypeCode(expenseTypeCode);

			if (StringUtils.isNotBlank(fromDate)) {
				try {
					if (fromDate.contains("/")) {
						fromDate = fromDate.replace("/", "-");
					}
					financeStartDate = DateUtil.parse(fromDate, DateFormat.LONG_DATE.getPattern());
				} catch (IllegalArgumentException e) {
					reason = "Invalid Approval Start Date, it should be in " + DateFormat.LONG_DATE.getPattern()
							+ " format.";
					valid = false;
				}
			} else {
				reason = "Approval Start Date is mandatory, it should be in  " + DateFormat.LONG_DATE.getPattern()
						+ " format.";
				valid = false;
			}

			if (StringUtils.isNotBlank(toDate)) {
				try {
					if (toDate.contains("/")) {
						toDate = toDate.replace("/", "-");
					}
					financeEndDate = DateUtil.parse(toDate, DateFormat.LONG_DATE.getPattern());
				} catch (IllegalArgumentException e) {
					reason = "Invalid Approval Start Date, it should be in " + DateFormat.LONG_DATE.getPattern()
							+ " format.";
					valid = false;
				}
			} else {
				reason = "Approval Start Date is mandatory, it should be in " + DateFormat.LONG_DATE.getPattern()
						+ " format.";
				valid = false;
			}

			if (valid) {
				if (financeStartDate.compareTo(financeEndDate) > 0) {
					reason = "Invalid Approval End Date, it should be less than or equals to Approval Start Date.";
					valid = false;
					financeEndDate = null;
				}
			}

			uploadFinExpense.setFinApprovalStartDate(financeStartDate);
			uploadFinExpense.setFinApprovalEndDate(financeEndDate);

			// Loan Type validation
			if (StringUtils.isBlank(uploadFinExpense.getFinType())) {
				if (valid) {
					reason = "Loan Type is mandatory.";
					valid = false;
				} else {
					reason = reason + "| Loan Type is mandatory.";
				}
			} else {

				if (uploadFinExpense.getFinType().length() > 8) {
					if (valid) {
						reason = "Loan Type : (" + uploadFinExpense.getFinType()
								+ ") length is exceeded, it should be lessthan or equals to 8.";
						valid = false;
					} else {
						reason = reason + "| Loan Type : (" + uploadFinExpense.getFinType()
								+ ") length is exceeded, it should be lessthan or equals to 8.";
					}

					uploadFinExpense.setFinType(uploadFinExpense.getFinType().substring(0, 8));
				} else {

					int count = this.uploadHeaderService.getFinTypeCount(uploadFinExpense.getFinType());

					if (count <= 0) {
						if (valid) {
							reason = "Loan Type : " + uploadFinExpense.getFinType() + " is invalid.";
							valid = false;
						} else {
							reason = reason + "| Loan Type : " + uploadFinExpense.getFinType() + " is invalid.";
						}
					}
				}
			}

			// validate the common data
			valid = validateCommonData(uploadFinExpense, expenseTypeCode, percentage, amountValue, valid, reason);
			reason = uploadFinExpense.getReason();

			if (valid) {
				uploadFinExpense.setStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
			} else {
				uploadFinExpense.setStatus(PennantConstants.UPLOAD_STATUS_FAIL);
			}

			uploadFinExpense.setReason(reason);

			uploadFinExpensesList.add(uploadFinExpense);
		}

		return uploadFinExpensesList;
	}

	public boolean validateCommonData(UploadFinExpenses uploadFinExpenses, String expenseType, String percentage,
			String amount, boolean valid, String reason) {

		BigDecimal percentageValue = new BigDecimal(percentage.equals("") ? "0" : percentage);
		BigDecimal amountValue = new BigDecimal(amount.equals("") ? "0" : amount.replaceAll(",", "").trim());

		// AppendOrOverride
		String type = uploadFinExpenses.getType();
		if (!(PennantConstants.EXPENSE_UPLOAD_ADD.equalsIgnoreCase(type)
				|| PennantConstants.EXPENSE_UPLOAD_OVERRIDE.equalsIgnoreCase(type))) {

			uploadFinExpenses.setType("E"); // default value for Type

			if (valid) {
				reason = "Append/Override is mandatory, it should be (A) or (O).";
				valid = false;
			} else {
				reason = reason + "| Append/Override is mandatory, it should be (A) or (O).";
			}
		}

		if (BigDecimal.ZERO.compareTo(amountValue) == 0 && BigDecimal.ZERO.compareTo(percentageValue) == 0) {
			if (valid) {
				valid = false;
				reason = "Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			} else {
				reason = reason
						+ "| Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			}
		} else if (BigDecimal.ZERO.compareTo(amountValue) != 0 && BigDecimal.ZERO.compareTo(percentageValue) != 0) {
			if (valid) {
				reason = "Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
				valid = false;
			} else {
				reason = reason
						+ "| Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			}
			uploadFinExpenses.setAmountValue(BigDecimal.ZERO);
			uploadFinExpenses.setPercentage(BigDecimal.ZERO);
		}

		// Percentage
		if (StringUtils.isBlank(percentage) && StringUtils.isBlank(amount)) {
			if (valid) {
				reason = "Either Percentage or Amount is mandatory.";
				valid = false;
			} else {
				reason = reason + "| Percentage is mandatory.";
			}
		} else if (StringUtils.isNotBlank(percentage) && new BigDecimal(percentage).compareTo(BigDecimal.ZERO) > 0) {
			try {
				percentageValue = new BigDecimal(percentage);

				if (percentageValue.compareTo(BigDecimal.ZERO) < 0) {
					throw new AppException();
				} else if (percentageValue.compareTo(new BigDecimal(100)) > 0) {
					throw new AppException();
				} else {
					uploadFinExpenses.setPercentage(percentageValue);
				}
			} catch (Exception e) {
				if (valid) {
					reason = "Percentage is mandatory, it should be greater than or equals to 0 and less than or equals to 100.";
					valid = false;
				} else {
					reason = reason
							+ "| Percentage is mandatory, it should be greater than or equals to 0 and less than or equals to 100.";
				}
				uploadFinExpenses.setPercentage(BigDecimal.ZERO);
			}
		} else {
			try {

				amountValue = new BigDecimal(amount);

				if (amountValue.compareTo(BigDecimal.ZERO) < 0
						&& PennantConstants.EXPENSE_UPLOAD_OVERRIDE.equals(type)) {
					throw new AppException("Negative values are not allowed in Orverride method.");
				}

				if (amount.length() > 19) {
					throw new AppException("Length is exceeded, it should be lessthan or equal to 19.");
				}

				int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
				uploadFinExpenses.setAmountValue(CurrencyUtil.unFormat(amountValue, formatter));
			} catch (Exception exception) {

				uploadFinExpenses.setAmountValue(BigDecimal.ZERO);

				if (valid) {
					reason = "Amount: (" + amount + ") is invalid. ";
					valid = false;
				} else {
					reason = reason + "| Amount: (" + amount + ") is invalid. ";
				}

				if (StringUtils.isNotBlank(exception.getMessage())) {
					reason = reason + exception.getMessage();
				}
			}
		}

		// Expense Type Code
		if (StringUtils.isBlank(expenseType)) {
			if (valid) {
				valid = false;
				reason = "Expense Type Code is mandatory.";
			} else {
				reason = reason + "| Expense Type Code is mandatory.";
			}
			uploadFinExpenses.setExpenseTypeCode("EXPError");
		} else if (expenseType.length() > 8) {
			if (valid) {
				valid = false;
				reason = "Expense Type Code : (" + expenseType
						+ ") length is exceeded, it should be lessthan or equal to 8.";
			} else {
				reason = reason + "| Expense Type Code : (" + expenseType
						+ ") length is exceeded, it should be lessthan or equal to 8.";
			}
			uploadFinExpenses.setExpenseTypeCode(expenseType.substring(0, 8));
		} else {

			long finExpenseId = this.uploadHeaderService.getFinExpenseIdByExpType(expenseType);

			if (finExpenseId == 0 || finExpenseId == Long.MIN_VALUE) {

				if (valid) {
					valid = false;
					reason = "Expense Type Code : (" + expenseType + ") is invalid.";
				} else {
					reason = reason + "| Expense Type Code : (" + expenseType + ") is invalid.";
				}
			}

			uploadFinExpenses.setExpenseId(finExpenseId);
			uploadFinExpenses.setExpenseTypeCode(expenseType);
		}

		uploadFinExpenses.setReason(reason);

		return valid;
	}

	private List<String> getRowValuesByIndex(Workbook workbook, int sheetIndex, int rowindex) {
		List<String> rowValues = new ArrayList<>();
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
	 */
	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING);

		doResetData();

		logger.debug(Literal.LEAVING);
	}

	private void doResetData() {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		this.txtFileName.setText("");
		this.fileName.setValue("");
		this.moduleType.setSelectedIndex(0);

		this.errorMsg = null;

		this.objDefaultFormat = new DataFormatter();
		this.formulaEvaluator = null;

		this.statusGrid.setVisible(false);
		this.btndownload.setVisible(false);

		readOnlyComponent(false, this.moduleType);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);

		File folderPath = new File(PathUtil.getPath(getFolderPath()));

		if (!folderPath.exists() && !folderPath.mkdirs()) {
			MessageUtil.showError(String.format(
					"Unable to create %s directories in %s location, please contact system administrator. ",
					getFolderPath(), App.HOME_PATH));
			this.media = null;
			return;
		}

		doValidations();

		readOnlyComponent(true, this.moduleType);
		this.btnBrowse.setDisabled(true);
		this.btnRefresh.setDisabled(true);

		try {

			if (this.errorMsg != null) {
				throw new Exception(this.errorMsg);
			}

			doSave();

		} catch (Exception e) {
			this.errorMsg = e.getMessage();
			doResetData();
			MessageUtil.showError(e);
		} finally {
			readOnlyComponent(false, this.moduleType);
			this.btnBrowse.setDisabled(false);
			this.btnRefresh.setDisabled(false);
			logger.debug(Literal.LEAVING);
		}
	}

	private void doValidations() {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			isValidComboValue(this.moduleType, Labels.getLabel("label_ExpenseUpload_ModuleType.value"));

			if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
				throw new WrongValueException(this.txtFileName, Labels.getLabel("empty_file"));
			} else {
				boolean fileExist = this.uploadHeaderService.isFileNameExist(this.txtFileName.getValue());
				if (fileExist) {
					throw new WrongValueException(this.txtFileName,
							this.txtFileName.getValue() + ": file name already Exist.");
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	protected void doSave() throws IOException, DataFormatException {
		logger.debug(Literal.ENTERING);

		if (media == null) {
			return;
		}

		ExcelFileImport fileImport = new ExcelFileImport(media, PathUtil.getPath(getFolderPath()));

		// Reading excel data and returning as a workbook
		Workbook workbook = fileImport.writeFile();

		if (workbook == null) {
			return;
		}

		String selectedModuleType = getComboboxValue(this.moduleType);

		Sheet sheet = workbook.getSheetAt(0);

		if (sheet.getPhysicalNumberOfRows() > 1) {
			if (workbook instanceof HSSFWorkbook) {
				this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
			} else if (workbook instanceof XSSFWorkbook) {
				this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
			}

			List<String> keys = getRowValuesByIndex(workbook, 0, 0);

			if (PennantConstants.EXPENSE_UPLOAD_LOANTYPE.equals(selectedModuleType)) {
				if (!StringUtils.equalsIgnoreCase("Loan Type", keys.get(0))) {
					MessageUtil.showError(
							"The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
					return;
				}
			} else if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(selectedModuleType)) {
				if (!StringUtils.equalsIgnoreCase("Loan Reference", keys.get(0))) {
					MessageUtil.showError(
							"The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
					return;
				}
			} else {
				return;
			}

			UploadHeader auploadHeader = new UploadHeader();
			auploadHeader.setFileName(this.txtFileName.getValue());
			auploadHeader.setTransactionDate(DateUtil.getSysDate());
			auploadHeader.setModule(selectedModuleType);
			auploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			auploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

			long uploadId = this.uploadHeaderService.save(auploadHeader);

			// Process the UploadFinExpenses
			List<UploadFinExpenses> expenses = processUploadFinExpenses(workbook, selectedModuleType, uploadId);

			if (CollectionUtils.isNotEmpty(expenses)) {
				this.uploadHeaderService.saveUploadFinExpenses(expenses);

				List<FinanceMain> finances;
				for (UploadFinExpenses expense : expenses) {
					long finExpenseId = expense.getExpenseId();
					if (StringUtils.isNotBlank(expense.getReason())
							|| (finExpenseId == 0 || finExpenseId == Long.MIN_VALUE)) {
						continue; // if data is not entered correctly
					}

					if (PennantConstants.EXPENSE_UPLOAD_LOANTYPE.equals(selectedModuleType)) {
						String finType = expense.getFinType();
						Date startdate = expense.getFinApprovalStartDate();
						Date endDate = expense.getFinApprovalEndDate();
						finances = this.uploadHeaderService.getFinancesByExpenseType(finType, startdate, endDate);

						if (CollectionUtils.isNotEmpty(finances)) {
							for (FinanceMain fm : finances) {
								processFinExpenseDetails(fm, expense, finExpenseId);
							}
						}
					} else if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(selectedModuleType)) {
						FinanceMain fm = this.uploadHeaderService.getFinanceMain(expense.getFinReference());

						processFinExpenseDetails(fm, expense, finExpenseId);
					}
				}
			}

			this.uploadHeaderService.updateRecordCounts(auploadHeader);

			auploadHeader = this.uploadHeaderService.getUploadHeader(auploadHeader.getUploadId());

			Clients.showNotification("Data imported successfully.", "info", null, null, -1);

			// Create backup file
			fileImport.backUpFile();

			this.statusGrid.setVisible(true);
			this.btndownload.setVisible(true);

			this.totalCount.setValue(String.valueOf(auploadHeader.getTotalRecords()));
			this.successCount.setValue(String.valueOf(auploadHeader.getSuccessCount()));
			this.failedCount.setValue(String.valueOf(auploadHeader.getFailedCount()));

		} else {
			MessageUtil.showError("File should not contain the data.");
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * process the Fin Expense Details and Fee Fin Expense Movements
	 * 
	 * @param fm
	 * @param uploadDetail
	 * @param finExpenseId
	 */
	private void processFinExpenseDetails(FinanceMain fm, UploadFinExpenses uploadDetail, long finExpenseId) {
		logger.debug(Literal.ENTERING);

		int formatter = CurrencyUtil.getFormat(fm.getFinCcy());
		BigDecimal txnAmount = uploadDetail.getAmountValue();
		Date transactionDate = SysParamUtil.getAppDate();

		if (BigDecimal.ZERO.compareTo(txnAmount) == 0) {
			if (BigDecimal.ZERO.compareTo(uploadDetail.getPercentage()) == 0) {
				return;
			}

			if (fm.getFinAssetValue() != null && fm.getFinAssetValue().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal percentage = uploadDetail.getPercentage();
				// formatting the amount
				txnAmount = CurrencyUtil.parse(fm.getFinAssetValue(), formatter);
				// calculating percentage
				txnAmount = (percentage.multiply(txnAmount)).divide(new BigDecimal(100));
				// un-formatting the amount
				txnAmount = CurrencyUtil.unFormat(txnAmount, formatter);
			} else {
				return;
			}
		}

		long finExpenseDetailId = 0;
		FinExpenseDetails fed = this.uploadHeaderService.getFinExpenseDetailsByReference(fm.getFinReference(),
				finExpenseId);

		if (fed == null) {
			fed = new FinExpenseDetails();
			fed.setFinID(fm.getFinID());
			fed.setFinReference(fm.getFinReference());
			fed.setExpenseTypeId(finExpenseId);
			fed.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			fed.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			fed.setAmount(txnAmount);

			finExpenseDetailId = this.uploadHeaderService.saveFinExpenseDetails(fed);

			fed.setFinExpenseId(finExpenseDetailId);
		} else {
			finExpenseDetailId = fed.getFinExpenseId();

			if (PennantConstants.EXPENSE_UPLOAD_ADD.equals(uploadDetail.getType())) {
				fed.setAmount(txnAmount.add(fed.getAmount()));
			} else if (PennantConstants.EXPENSE_UPLOAD_OVERRIDE.equals(uploadDetail.getType())) {
				fed.setAmount(txnAmount);
			}

			this.uploadHeaderService.update(fed);
		}

		FinExpenseMovements finExpenseMovements = new FinExpenseMovements();
		finExpenseMovements.setFinExpenseId(finExpenseDetailId);
		finExpenseMovements.setFinID(fm.getFinID());
		finExpenseMovements.setFinReference(fm.getFinReference());
		finExpenseMovements.setUploadId(uploadDetail.getUploadId());
		finExpenseMovements.setModeType(PennantConstants.EXPENSE_MODE_UPLOAD);
		finExpenseMovements.setTransactionAmount(txnAmount);
		finExpenseMovements.setTransactionType(uploadDetail.getType());
		finExpenseMovements.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		finExpenseMovements.setTransactionDate(transactionDate);
		finExpenseMovements.setExpenseTypeID(uploadDetail.getExpenseId());
		finExpenseMovements.setExpenseTypeCode(uploadDetail.getExpenseTypeCode());

		finExpenseMovements.setFinanceMain(fm);
		this.uploadHeaderService.saveFinExpenseMovements(finExpenseMovements);

		logger.debug(Literal.LEAVING);
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}

	public void setUploadHeader(UploadHeader uploadHeader) {
		this.uploadHeader = uploadHeader;
	}

	// sample file download for adding to include or exclude manually
	public void onClick$sampleFileDownload(Event event) throws FileNotFoundException {
		logger.debug(Literal.ENTERING);
		String path = PathUtil.getPath(PathUtil.TEMPLATES);
		String fileName = "Expense_Upload_" + getComboboxValue(this.moduleType) + ".xlsx";

		File template = new File(path.concat(File.separator).concat(fileName));

		if (!template.exists()) {
			MessageUtil.showError(String.format(
					"%s template not exists in %s location, please contact system administrator", fileName, path));
			return;
		}

		Filedownload.save(template, DocType.XLSX.getContentType());

		logger.debug(Literal.LEAVING);
	}
}
