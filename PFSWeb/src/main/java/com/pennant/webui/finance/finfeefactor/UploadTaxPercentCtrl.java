package com.pennant.webui.finance.finfeefactor;

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

import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class UploadTaxPercentCtrl extends GFCBaseCtrl<UploadHeader> {

	private static final long serialVersionUID = 4783031677099154138L;
	private static final Logger logger = LogManager.getLogger(UploadTaxPercentCtrl.class);

	protected Window window_FinFeeFactoreUpload;
	protected Button btnUpload;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Textbox txtFileName;
	protected Label totalCount;
	protected Label successCount;
	protected Label failedCount;
	protected Grid statusGrid;
	protected Button btnDownload;
	protected Label label_fileName;

	private Workbook workbook = null;
	private DataFormatter objDefaultFormat = new DataFormatter();// for cell value formating
	private FormulaEvaluator formulaEvaluator = null; // for cell value formating
	private String errorMsg = null;
	private ExcelFileImport fileImport = null;
	private UploadHeader uploadHeader = new UploadHeader();
	private UploadHeaderService uploadHeaderService;

	public UploadTaxPercentCtrl() {
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
	public void onCreate$window_FinFeeFactoreUpload(Event event) {
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
		// this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypeExpenseUpload_Save"));
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
		Media media = event.getMedia();

		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			return;
		}

		this.fileImport = new ExcelFileImport(media, SysParamUtil.getValueAsString("UPLOAD_FILEPATH"));
		this.txtFileName.setText(media.getName());
		this.label_fileName.setValue(this.txtFileName.getValue());

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
			ReportsUtil.generateReport(userName, "FeeFactoringExceptionReport", whereCond, searchCriteriaDesc);
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
	private List<UploadTaxPercent> processUploadDetails(long uploadId) {
		logger.debug("Entering");

		List<UploadTaxPercent> uploadDetails = new ArrayList<UploadTaxPercent>();
		Sheet sheet = this.workbook.getSheetAt(0);

		Iterator<Row> rows = sheet.iterator();

		String finReference = null;
		String feeTypeCode = null;
		String calcFactor = null;

		while (rows.hasNext()) {

			Row row = rows.next();
			int rowIndex = row.getRowNum();

			if (rowIndex > 0) {

				List<String> rowValue = getAllValuesOfRowByIndex(this.workbook, 0, rowIndex);

				if (CollectionUtils.isNotEmpty(rowValue)) {

					String reason = null; // tell us the reason of the failure
					boolean valid = true; // for data has been getting any errors or not

					finReference = rowValue.get(0);
					feeTypeCode = rowValue.get(1);
					calcFactor = rowValue.get(2);

					UploadTaxPercent uploadDetail = new UploadTaxPercent();
					uploadDetail.setUploadId(uploadId);
					uploadDetail.setFinReference(finReference);

					// FinReference
					if (StringUtils.isBlank(finReference)) {

						reason = "Loan Reference is mandatory";
						valid = false;
						uploadDetail.setFinReference("FINREF"); // FIXME default value
					} else if (finReference.length() > 20) {
						valid = false;
						reason = "Expense Type Code : (" + finReference
								+ ") length is exceeded, it should be lessthan or equal to 20.";
						uploadDetail.setFinReference(finReference.substring(0, 20));
					} else {
						Long finID = this.uploadHeaderService.getActiveFinID(finReference);

						if (finID == null) {
							reason = "Loan Reference: (" + finReference + ") is not valid.";
							valid = false;
						}
					}
					// Fee Type Code
					if (StringUtils.isBlank(feeTypeCode)) {
						if (valid) {
							valid = false;
							reason = "Fee Type Code is mandatory.";
						} else {
							reason = reason + "| Fee Type Code is mandatory.";
						}
						uploadDetail.setFeeTypeCode("FeeError");
					} else if (feeTypeCode.length() > 8) {
						if (valid) {
							valid = false;
							reason = "Fee Type Code : (" + feeTypeCode
									+ ") length is exceeded, it should be lessthan or equal to 8.";
						} else {
							reason = reason + "| Fee Type Code : (" + feeTypeCode
									+ ") length is exceeded, it should be lessthan or equal to 8.";
						}
						uploadDetail.setFeeTypeCode(feeTypeCode.substring(0, 8));
					} else {

						Long feeTypeId = this.uploadHeaderService.getFinFeeTypeIdByFeeType(feeTypeCode);

						if (feeTypeId != null) {

							if (valid) {
								valid = false;
								reason = "Fee Type Code : (" + feeTypeCode + ") is invalid.";
							} else {
								reason = reason + "| Fee Type Code : (" + feeTypeCode + ") is invalid.";
							}
							feeTypeId = Long.MIN_VALUE;
						}

						uploadDetail.setFeeTypeId(feeTypeId);
						uploadDetail.setFeeTypeCode(feeTypeCode);
					}

					// Calculation Factor
					if (StringUtils.isBlank(calcFactor)) {
						if (valid) {
							reason = "Calculation Factor is mandatory.";
							valid = false;
						} else {
							reason = reason + "| Calculation Factor is mandatory.";
						}
					} else {
						try {
							BigDecimal calcFactorValue = new BigDecimal(calcFactor);

							if (calcFactorValue.compareTo(BigDecimal.ZERO) < 1) {
								throw new Exception();
							} else if (calcFactorValue.compareTo(new BigDecimal(100)) > 0) {
								throw new Exception();
							} else {
								uploadDetail.setTaxPercent(calcFactorValue);
							}
						} catch (NumberFormatException e) {
							if (valid) {
								reason = "Calculation Factor : (" + calcFactor + ") is invalid";
								valid = false;
							} else {
								reason = reason + "| Calculation Factor :  (" + calcFactor + ") is invalid";
							}
							uploadDetail.setTaxPercent(BigDecimal.ZERO);
						} catch (Exception e) {
							if (valid) {
								reason = "Calculation Factor is mandatory, it should be greater than or equals to 0 and less than or equals to 100.";
								valid = false;
							} else {
								reason = reason
										+ "| Calculation Factor is mandatory, it should be greater than or equals to 0 and less than or equals to 100.";
							}
							uploadDetail.setTaxPercent(BigDecimal.ZERO);
						}
					}
					if (valid) {
						uploadDetail.setStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
					} else {
						uploadDetail.setStatus(PennantConstants.UPLOAD_STATUS_FAIL);
					}

					uploadDetail.setReason(reason);

					uploadDetails.add(uploadDetail);
				}
			}
		}
		return uploadDetails;
	}

	public List<String> getAllValuesOfRowByIndex(Workbook workbook, int sheetIndex, int rowindex) {
		List<String> keys = new ArrayList<String>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		org.apache.poi.ss.usermodel.Row headings = sheet.getRow(rowindex);

		for (Cell cell : headings) {
			formulaEvaluator.evaluate(cell);
			String cellValueStr = objDefaultFormat.formatCellValue(cell, formulaEvaluator);
			keys.add(cellValueStr.trim());
		}
		return keys;
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

		this.btnDownload.setVisible(false);
		this.statusGrid.setVisible(false);

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

		if (this.fileImport == null) {
			return;
		}

		this.workbook = this.fileImport.writeFile();

		if (this.workbook == null) {
			return;
		}

		if (this.workbook instanceof HSSFWorkbook) {
			this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) this.workbook);
		} else {
			this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
		}

		List<String> keys = getAllValuesOfRowByIndex(this.workbook, 0, 0);

		if (!keys.contains("Loan Reference")) {
			throw new AppException(
					"The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
		}

		Sheet sheet = this.workbook.getSheetAt(0);

		if (sheet.getPhysicalNumberOfRows() <= 1) {
			MessageUtil.showError("File should not contain the data.");
		}

		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setFileName(this.txtFileName.getValue());
		uploadHeader.setModule("FinFeeFactor");
		uploadHeader.setTransactionDate(DateUtil.getSysDate());
		uploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		uploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		long uploadId = this.uploadHeaderService.save(uploadHeader);

		// Process the Upload Details
		List<UploadTaxPercent> uploadDetails = processUploadDetails(uploadId);

		if (uploadDetails != null && !uploadDetails.isEmpty()) {
			this.uploadHeaderService.saveFeeUploadDetails(uploadDetails);
			for (UploadTaxPercent uploadDetail : uploadDetails) {
				Long finID = this.uploadHeaderService.getActiveFinID(uploadDetail.getFinReference());
				if (uploadDetail.getFeeTypeId() != Long.MIN_VALUE && finID != null) {
					uploadDetail.setTaxPercent(uploadDetail.getTaxPercent());
					this.uploadHeaderService.updateTaxPercent(uploadDetail);
				}
			}
		}

		List<UploadTaxPercent> countList = this.uploadHeaderService.getSuccesFailedCountForFactor(uploadId);
		int totCount = 0;
		for (UploadTaxPercent expenseUpload : countList) {
			if (StringUtils.equals(expenseUpload.getStatus(), PennantConstants.UPLOAD_STATUS_SUCCESS)) {
				totCount += expenseUpload.getCount();
				uploadHeader.setSuccessCount(expenseUpload.getCount());
			} else {
				totCount += expenseUpload.getCount();
				uploadHeader.setFailedCount(expenseUpload.getCount());
			}
		}

		uploadHeader.setTotalRecords(totCount);
		this.uploadHeaderService.updateRecordCounts(uploadHeader);

		Clients.showNotification("Data imported successfully.", "info", null, null, -1);
		// Create backup file
		this.fileImport.backUpFile();
		// doResetData();
		this.btnDownload.setVisible(true);
		this.statusGrid.setVisible(true);
		this.totalCount.setValue(String.valueOf(uploadHeader.getTotalRecords()));
		this.successCount.setValue(String.valueOf(uploadHeader.getSuccessCount()));
		this.failedCount.setValue(String.valueOf(uploadHeader.getFailedCount()));

		logger.debug(Literal.LEAVING);
	}

	public UploadHeaderService getUploadHeaderService() {
		return uploadHeaderService;
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}
}
