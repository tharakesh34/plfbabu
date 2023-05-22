package com.pennant.webui.finance.fintypeexpenses;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.Row;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadFinTypeExpense;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class UploadFinTypeExpenseCtrl extends GFCBaseCtrl<UploadHeader> {

	private static final long serialVersionUID = 4783031677099154138L;
	private static final Logger logger = LogManager.getLogger(UploadFinTypeExpenseCtrl.class);

	protected Window window_FinTypeExpenseUpload;
	protected Button btnUpload;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Button btnDownload;
	protected Textbox txtFileName;
	protected Label totalCount;
	protected Label successCount;
	protected Label failedCount;
	protected Grid statusGrid;
	private Label label_fileName;

	private Workbook workbook = null;
	private DataFormatter objDefaultFormat = new DataFormatter(); // for cell value formating
	private FormulaEvaluator formulaEvaluator = null; // for cell value formating
	private String errorMsg = null;
	private ExcelFileImport fileImport = null;
	private UploadHeader uploadHeader = new UploadHeader();
	private UploadHeaderService uploadHeaderService;

	public UploadFinTypeExpenseCtrl() {
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
	 */
	public void onCreate$window_FinTypeExpenseUpload(Event event) {
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

		this.statusGrid.setVisible(false);
		this.btnDownload.setVisible(false);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.statusGrid.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		// this.btnUpload.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseUpload_Browse"));
		// this.btnRefresh.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseUpload_Refresh"));
		// this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseUpload_Refresh"));
		// this.btndownload.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseUpload_Report"));

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic The entity that need to be render.
	 */
	public void doShowDialog(UploadHeader uploadHeader) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (uploadHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.txtFileName.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.txtFileName.focus();
				if (StringUtils.isNotBlank(uploadHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				// this.btnCtrl.setInitEdit();
				// doReadOnly();
				// btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.uploadHeader.isNewRecord()) {
			this.btnUpload.setVisible(true);
			this.btnUpload.setDisabled(false);
		}
		this.txtFileName.setReadonly(true);
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
		} else {
			// this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

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
	public void onUpload$btnUpload(UploadEvent event) {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setText("");
		this.fileImport = null;
		this.errorMsg = null;

		doRemoveValidation();
		doResetData();
		Media media = event.getMedia();

		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			return;
		}

		String filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		filePath.concat(File.separator).concat("LoanTypeExpenseMaster");
		this.fileImport = new ExcelFileImport(media, filePath);
		this.txtFileName.setText(media.getName());
		this.label_fileName.setValue(media.getName());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Process" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDownload(Event event) {
		logger.debug("Entering" + event.toString());

		String whereCond = "and FILENAME in (" + "'" + this.txtFileName.getValue() + "'" + ")";
		StringBuilder searchCriteriaDesc = new StringBuilder(" ");
		searchCriteriaDesc.append("File Name is " + this.txtFileName.getValue());

		String userName = getUserWorkspace().getLoggedInUser().getFullName();

		try {
			ReportsUtil.generateReport(userName, "LoanTypeExpenseMaster", whereCond, searchCriteriaDesc);
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
	private List<UploadFinTypeExpense> processUploadDetails(long uploadId) {
		logger.debug("Entering");

		List<UploadFinTypeExpense> uploadDetails = new ArrayList<UploadFinTypeExpense>();
		Sheet sheet = this.workbook.getSheetAt(0);

		Iterator<Row> rows = sheet.iterator();

		String finType = null;
		String expenseTypeCode = null;
		String percentage = null;
		String amountValue = null;

		while (rows.hasNext()) {

			org.apache.poi.ss.usermodel.Row row = rows.next();
			int rowIndex = row.getRowNum();

			if (rowIndex > 0) {
				List<String> rowValue = getRowValuesByIndex(this.workbook, 0, rowIndex);
				if (CollectionUtils.isNotEmpty(rowValue)) {
					String reason = null; // tell us the reason of the failure
					boolean valid = true; // for data has been getting any errors or not
					finType = rowValue.get(0);
					expenseTypeCode = rowValue.get(1);
					amountValue = rowValue.get(2);
					percentage = rowValue.get(3);

					UploadFinTypeExpense uploadDetail = new UploadFinTypeExpense();
					uploadDetail.setUploadId(uploadId);
					uploadDetail.setFinType(finType);
					uploadDetail.setExpenseTypeCode(expenseTypeCode);

					// validate the Upload data
					valid = validateUploadData(uploadDetail, percentage, amountValue, valid, reason);

					if (valid) {
						uploadDetail.setStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
					} else {
						uploadDetail.setStatus(PennantConstants.UPLOAD_STATUS_FAIL);
					}
					uploadDetail.setReason(uploadDetail.getReason());

					uploadDetails.add(uploadDetail);

				}
			}
		}
		return uploadDetails;
	}

	public boolean validateUploadData(UploadFinTypeExpense uploadDetail, String percentage, String amount,
			boolean valid, String reason) {

		BigDecimal percentageValue = BigDecimal.ZERO;
		BigDecimal amountValue = BigDecimal.ZERO;
		String expenseType = uploadDetail.getExpenseTypeCode();
		// Loan Type validation
		if (StringUtils.isBlank(uploadDetail.getFinType())) {
			reason = "Loan Type is mandatory.";
			valid = false;
		} else {

			if (uploadDetail.getFinType().length() > 8) {

				reason = "Loan Type : (" + uploadDetail.getFinType()
						+ ") length is exceeded, it should be lessthan or equals to 8.";
				valid = false;
				uploadDetail.setFinType(uploadDetail.getFinType().substring(0, 8));
			} else {
				int count = this.uploadHeaderService.getFinTypeCount(uploadDetail.getFinType());
				if (count <= 0) {
					reason = "Loan Type : " + uploadDetail.getFinType() + " is invalid.";
					valid = false;
				}
			}
		}
		// Percentage
		try {
			if (StringUtils.isBlank(percentage) || BigDecimal.ZERO.compareTo(new BigDecimal(percentage)) == 0) {
				uploadDetail.setPercentage(BigDecimal.ZERO);
			} else {
				percentageValue = new BigDecimal(percentage);

				if (percentageValue.compareTo(BigDecimal.ZERO) < 1) {
					throw new Exception();
				} else if (percentageValue.compareTo(new BigDecimal(100)) > 0) {
					throw new Exception();
				} else {
					uploadDetail.setPercentage(percentageValue);
				}
			}
		} catch (NumberFormatException e) {
			if (valid) {
				reason = "Percentage : (" + percentage + ") is invalid";
				valid = false;
			} else {
				reason = reason + "| Percentage :  (" + percentage + ") is invalid";
			}
			uploadDetail.setPercentage(BigDecimal.ZERO);
		}

		catch (Exception e) {
			if (valid) {
				reason = "Percentage is mandatory, it should be greater than or equals to 0 and less than or equals to 100.";
				valid = false;
			} else {
				reason = reason
						+ "| Percentage is mandatory, it should be greater than or equals to 0 and less than or equals to 100.";
			}
			uploadDetail.setPercentage(BigDecimal.ZERO);
		}

		// Amount
		try {
			if (StringUtils.isBlank(amount) || BigDecimal.ZERO.compareTo(new BigDecimal(amount)) == 0) {
				uploadDetail.setAmountValue(BigDecimal.ZERO);
			} else {

				amountValue = new BigDecimal(amount);

				if (amountValue.compareTo(BigDecimal.ZERO) < 1) {
					throw new Exception("Negative values are not allowed.");
				}

				if (amount.length() > 19) {
					throw new Exception("Length is exceeded, it should be lessthan or equal to 19.");
				}
				int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
				uploadDetail.setAmountValue(CurrencyUtil.unFormat(amountValue, formatter));
			}
		} catch (NumberFormatException e) {
			if (valid) {
				reason = "Amount: (" + amount + ") is invalid. ";
				valid = false;
			} else {
				reason = reason + "| Amount: (" + amount + ") is invalid. ";
			}
		} catch (Exception e) {
			uploadDetail.setAmountValue(BigDecimal.ZERO);

			if (valid) {
				reason = e.getMessage();
				valid = false;
			} else {
				reason = reason + "| " + e.getMessage();
			}
		}
		// Validate the amount and percentage
		if (BigDecimal.ZERO.compareTo(uploadDetail.getAmountValue()) == 0
				&& BigDecimal.ZERO.compareTo(uploadDetail.getPercentage()) == 0) {
			if (valid) {
				valid = false;
				reason = "Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			} else {
				reason = reason
						+ "| Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			}
		} else if (BigDecimal.ZERO.compareTo(uploadDetail.getAmountValue()) != 0
				&& BigDecimal.ZERO.compareTo(uploadDetail.getPercentage()) != 0) {
			if (valid) {
				reason = "Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
				valid = false;
			} else {
				reason = reason
						+ "| Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			}
			uploadDetail.setAmountValue(BigDecimal.ZERO);
			uploadDetail.setPercentage(BigDecimal.ZERO);
		}

		// Expense Type Code
		if (StringUtils.isBlank(expenseType)) {
			if (valid) {
				valid = false;
				reason = "Expense Type Code is mandatory.";
			} else {
				reason = reason + "| Expense Type Code is mandatory.";
			}
			uploadDetail.setExpenseTypeCode("EXPError");
		} else if (expenseType.length() > 8) {
			if (valid) {
				valid = false;
				reason = "Expense Type Code : (" + expenseType
						+ ") length is exceeded, it should be lessthan or equal to 8.";
			} else {
				reason = reason + "| Expense Type Code : (" + expenseType
						+ ") length is exceeded, it should be lessthan or equal to 8.";
			}
			uploadDetail.setExpenseTypeCode(expenseType.substring(0, 8));
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

			uploadDetail.setExpenseId(finExpenseId);
			uploadDetail.setExpenseTypeCode(expenseType);
		}

		uploadDetail.setReason(reason);
		return valid;

	}

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
		this.label_fileName.setValue("");
		this.fileImport = null;
		this.errorMsg = null;

		this.workbook = null;
		this.objDefaultFormat = new DataFormatter();// for cell value formating
		this.formulaEvaluator = null; // for cell value formating
		this.statusGrid.setVisible(false);
		this.btnDownload.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);

		doValidations();
		this.btnUpload.setDisabled(true);
		this.btnRefresh.setDisabled(true);
		try {
			if (this.errorMsg != null) {
				throw new Exception(this.errorMsg);
			}
			doSave();
		} catch (Exception e) {
			this.txtFileName.setText("");
			this.label_fileName.setValue("");
			doResetData();
			MessageUtil.showError(e.getMessage());
			return;
		} finally {
			this.btnUpload.setDisabled(false);
			this.btnRefresh.setDisabled(false);
			logger.debug(Literal.LEAVING);
		}
	}

	private void doValidations() {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
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
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	protected void doSave() throws IOException, DataFormatException {
		logger.debug(Literal.ENTERING);

		String tranType;
		AuditHeader auditHeader = null;
		UploadHeader uploadHeader = null;
		if (this.fileImport != null) {

			this.workbook = this.fileImport.writeFile();
			if (this.workbook != null) {
				if (this.workbook instanceof HSSFWorkbook) {
					this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) this.workbook);
				} else if (this.workbook instanceof XSSFWorkbook) {
					this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) this.workbook);
				}
				List<String> keys = getRowValuesByIndex(this.workbook, 0, 0);

				if (!keys.contains("Loan Type") || !keys.contains("Expense Type Code") || !keys.contains("Amount")
						|| !keys.contains("Percentage (%)")) {
					throw new AppException(
							"The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
				}

				Sheet sheet = this.workbook.getSheetAt(0);
				if (sheet.getPhysicalNumberOfRows() > 0) {
					uploadHeader = new UploadHeader();
					uploadHeader.setFileName(this.txtFileName.getValue());
					uploadHeader.setTransactionDate(DateUtil.getSysDate());
					uploadHeader.setModule("FinTypeExpense");
					uploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					uploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

					long uploadId = this.uploadHeaderService.save(uploadHeader);

					// Process the Upload Details
					List<UploadFinTypeExpense> uploadDetails = processUploadDetails(uploadId);

					if (uploadDetails != null && !uploadDetails.isEmpty()) {

						this.uploadHeaderService.saveExpenseUploadDetails(uploadDetails);
						FinTypeExpense finTypeExpense = null;

						for (UploadFinTypeExpense uploadDetail : uploadDetails) {

							long finExpenseId = uploadDetail.getExpenseId();
							String finType = uploadDetail.getFinType();

							if (finExpenseId != 0 && finExpenseId != Long.MIN_VALUE) {

								finTypeExpense = this.uploadHeaderService.getFinExpensesByFinType(finType,
										finExpenseId);

								if (finTypeExpense == null) {
									finTypeExpense = new FinTypeExpense();
									finTypeExpense.setFinType(finType);
									finTypeExpense.setExpenseTypeID(finExpenseId);

									if (uploadDetail.getAmountValue().compareTo(BigDecimal.ZERO) >= 1) {
										finTypeExpense
												.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
										finTypeExpense.setAmount(uploadDetail.getAmountValue());
										finTypeExpense.setPercentage(BigDecimal.ZERO);
									} else {
										finTypeExpense.setPercentage(uploadDetail.getPercentage());
										finTypeExpense
												.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE);
										finTypeExpense.setCalculateOn(PennantConstants.EXPENSE_CALCULATEDON_LOAN);
									}
									finTypeExpense.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
									finTypeExpense.setLastMntOn(new Timestamp(System.currentTimeMillis()));
									finTypeExpense.setUserDetails(getUserWorkspace().getLoggedInUser());
									finTypeExpense.setActive(true);
									finTypeExpense.setVersion(finTypeExpense.getVersion() + 1);
									finTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_NEW);
									tranType = PennantConstants.TRAN_ADD;
								} else {
									finTypeExpense.setBefImage(finTypeExpense);
									finTypeExpense.setVersion(finTypeExpense.getVersion() + 1);

									if (uploadDetail.getAmountValue().compareTo(BigDecimal.ZERO) >= 1) {
										finTypeExpense.setAmount(uploadDetail.getAmountValue());
										finTypeExpense
												.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
										finTypeExpense.setPercentage(BigDecimal.ZERO);
										finTypeExpense.setCalculateOn("");
									} else {
										finTypeExpense.setPercentage(uploadDetail.getPercentage());
										finTypeExpense
												.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE);
										finTypeExpense.setCalculateOn(PennantConstants.EXPENSE_UPLOAD_LOAN);
										finTypeExpense.setAmount(BigDecimal.ZERO);

									}
									finTypeExpense.setRecordType(PennantConstants.RECORD_TYPE_UPD);
									tranType = PennantConstants.TRAN_UPD;
								}
								auditHeader = getAuditHeader(finTypeExpense, tranType);
								auditHeader = uploadHeaderService.doApprove(auditHeader);
							}
						}
					}
					List<UploadFinTypeExpense> countList = this.uploadHeaderService
							.getSuccesFailedCountExpense(uploadId);
					int totCount = 0;
					for (UploadFinTypeExpense expenseUpload : countList) {
						if (StringUtils.equals(expenseUpload.getStatus(), PennantConstants.UPLOAD_STATUS_SUCCESS)) {
							totCount += expenseUpload.getCount();
							uploadHeader.setSuccessCount(expenseUpload.getCount());
						} else {
							totCount += expenseUpload.getCount();
							uploadHeader.setFailedCount(expenseUpload.getCount());
						}
					}

					uploadHeader.setTotalRecords(totCount);
					this.uploadHeaderService.updateRecord(uploadHeader);

					Clients.showNotification("Data imported successfully.", "info", null, null, -1);

					// Create backup file
					this.fileImport.backUpFile();

					// doResetData();
					this.statusGrid.setVisible(true);
					this.btnDownload.setVisible(true);
					this.totalCount.setValue(String.valueOf(uploadHeader.getTotalRecords()));
					this.successCount.setValue(String.valueOf(uploadHeader.getSuccessCount()));
					this.failedCount.setValue(String.valueOf(uploadHeader.getFailedCount()));
				} else {
					MessageUtil.showError("File should not contain the data.");
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader getAuditHeader(FinTypeExpense afinTypeExpense, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinTypeExpense.getBefImage(), afinTypeExpense);
		return new AuditHeader(getReference(), null, null, null, auditDetail, afinTypeExpense.getUserDetails(),
				getOverideMap());
	}

	public UploadHeaderService getUploadHeaderService() {
		return uploadHeaderService;
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}
}
