/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : SecondaryMandateListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * *
 * Modified Date : 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.mandate.mandate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.UploadSecondaryMandate;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.mandate.UploadSecondaryMandateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/SecondaryMandateList.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class UploadSecondaryMandateListCtrl extends GFCBaseListCtrl<Mandate> {
	private static final long serialVersionUID = 1L;

	protected Window window_SecondaryMandateList;
	protected Borderlayout borderLayout_MandateList;
	protected Paging pagingSecondaryMandateList;
	protected Listbox listBoxMandate;
	protected Textbox fileName;
	protected Button btnUpload;
	protected Button btndownload;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Label totalCount;
	protected Label successCount;
	protected Label failedCount;
	protected Grid statusGrid;

	private final String uploadLoaction = "/opt/pennant/external";
	private File file;
	private Workbook workbook = null;
	private DataFormatter objDefaultFormat = new DataFormatter();// for cell value formating
	private FormulaEvaluator objFormulaEvaluator = null; // for cell value formating

	private transient MandateService mandateService;
	private BankBranchService bankBranchService;
	private FinanceMainService financeMainService;
	private UploadSecondaryMandateService uploadSecondaryMandateService;
	private UploadHeaderService uploadHeaderService;
	private BankDetailService bankDetailService;

	/**
	 * default constructor.<br>
	 */
	public UploadSecondaryMandateListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Mandate";
		super.pageRightName = "MandateList";
		super.tableName = "Mandates_AView";
		super.queueTableName = "Mandates_View";
	}

	@Override
	protected void doAddFilters() {

		super.doAddFilters();
		searchObject.addFilterEqual("active", 1);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_SecondaryMandateList(Event event) {

		// Set the page level components.
		setPageComponents(window_SecondaryMandateList, borderLayout_MandateList, listBoxMandate,
				pagingSecondaryMandateList);

		// Render the page and display the data.
		doRenderPage();
	}

	/**
	 * when the "Process" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btndownload(Event event) {
		logger.debug("Entering" + event.toString());
		long userId = getUserWorkspace().getLoggedInUser().getUserId();
		Date transcationDate = DateUtil.getSqlDate(DateUtil.getSysDate());
		String whereCond = "and FILENAME in (" + "'" + fileName.getValue() + "'" + ")and lastmntby = " + "'" + userId
				+ "'" + "and transactiondate = '" + transcationDate + "'";
		StringBuilder searchCriteriaDesc = new StringBuilder(" ");
		searchCriteriaDesc.append("File Name is " + fileName.getValue());
		searchCriteriaDesc.append("User ID is" + userId);
		searchCriteriaDesc.append("Upload Date is" + transcationDate);

		String userName = getUserWorkspace().getLoggedInUser().getFullName();
		try {
			ReportsUtil.generateReport(userName, "MandateUploadReport", whereCond, searchCriteriaDesc);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws IOException If an I/O error occurred.
	 */
	public void onUpload$btnUpload(UploadEvent event) throws IOException {
		logger.debug(Literal.ENTERING);
		fileName.setText("");
		Media media = event.getMedia();

		btndownload.setVisible(false);
		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			return;
		}

		this.btnUpload.setDisabled(false);
		fileName.setText(media.getName());
		String fName = media.getName();
		writeFile(media, fName);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) {
		if (StringUtils.isBlank(fileName.getValue())) {
			MessageUtil.showError("Please upload a excel file");
		} else {
			boolean fileExists = uploadHeaderService.isFileNameExist(fileName.getValue());
			if (fileExists) {
				MessageUtil.showError("File with " + fileName.getValue() + " already exists. ");
				return;
			}
			doProcessMandates(fileName.getValue());
		}
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

		this.fileName.setText("");

		this.workbook = null;
		this.objDefaultFormat = new DataFormatter();// for cell value formating
		this.objFormulaEvaluator = null; // for cell value formating

		this.statusGrid.setVisible(false);
		this.btndownload.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	private void writeFile(Media media, String fName) throws IOException {
		logger.debug(Literal.ENTERING);
		File parent = new File(uploadLoaction);

		if (!parent.exists()) {
			parent.mkdirs();
		}
		file = new File(parent.getPath().concat(File.separator).concat(media.getName()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileUtils.writeByteArrayToFile(file, media.getByteData());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			if (file.toString().toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook(fis);
			} else {
				workbook = new XSSFWorkbook(fis);
			}
		} catch (Exception e) {
			MessageUtil.showError("contact admin");
			logger.error(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * reading whole excel and calling other methods to prepare mandate object and Save the mandates .
	 * 
	 * @return String
	 */
	public void doProcessMandates(String fName) {
		logger.debug("Entering");

		if (this.workbook instanceof HSSFWorkbook) {
			this.objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) this.workbook);
		} else if (this.workbook instanceof XSSFWorkbook) {
			this.objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) this.workbook);
		}

		List<String> keys = getAllValuesOfRowByIndex(workbook, 0, 1);
		if (!keys.contains("Mandate ID")) {
			MessageUtil.showError("Invlid format");
			return;
		}
		Sheet sheet = workbook.getSheetAt(0);

		Iterator<Row> rows = sheet.iterator();
		int totalCount = 0;
		int successCount = 0;
		int failedCount = 0;
		long uploadId = Long.MIN_VALUE;
		// read the excel rows
		while (rows.hasNext()) {
			Row row = rows.next();
			int rowIndex = row.getRowNum();
			// skip the header rows
			if (rowIndex > 1) {
				// prepare the row data
				List<String> rowValue = getAllValuesOfRowByIndex(workbook, 0, rowIndex);
				// prepare the mandate object to excel data
				Mandate mandate = new Mandate();
				if (StringUtils.isNotBlank(rowValue.get(0)) && StringUtils.isNumeric(rowValue.get(0))) {
					mandate.setMandateID(Long.valueOf(rowValue.get(0)));
				}
				mandate.setMandateType(rowValue.get(1));
				mandate.setMICR(rowValue.get(2));
				mandate.setBarCodeNumber(rowValue.get(3));
				mandate.setAccNumber(rowValue.get(4));
				mandate.setAccType(rowValue.get(5));
				mandate.setBankName(rowValue.get(6));
				mandate.setAccHolderName(rowValue.get(7));
				// get the primary mandate Date using given mandate Id
				Mandate preMandate = mandateService.getApprovedMandateById(mandate.getMandateID());
				StringBuilder remarks = new StringBuilder();

				if (preMandate != null) {
					// set the work flow data
					preMandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					preMandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					preMandate.setNewRecord(true);
					preMandate.setActive(true);
					preMandate.setVersion(1);
					preMandate.setMandateCcy(SysParamUtil.getAppCurrency());
					preMandate.setInputDate(SysParamUtil.getAppDate());
					preMandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					preMandate.setSecondaryMandate(true);

					// save the header table data
					if (rowIndex == 2) {
						uploadId = saveUploadHeaderDetails(fName);
					}
					// validate the given mandate details
					remarks = validateMandateData(mandate, preMandate, remarks);
					// overide the primary mandate data to secondary mandate
					preMandate.setPrimaryMandateId(preMandate.getMandateID());
					preMandate.setMandateID(Long.MIN_VALUE);
					preMandate.setMandateType(mandate.getMandateType());
					preMandate.setMICR(mandate.getMICR());
					preMandate.setBarCodeNumber(mandate.getBarCodeNumber());
					preMandate.setAccNumber(mandate.getAccNumber());
					preMandate.setAccType(mandate.getAccType());
					preMandate.setBankName(mandate.getBankName());
					preMandate.setAccHolderName(mandate.getAccHolderName());
					if (remarks.length() > 0) { // validation failed
						// log the mandate data
						UploadSecondaryMandate mandateStatus = prepareMandateStatus(mandate, false, remarks.toString());
						mandateStatus.setUploadId(uploadId);
						failedCount++;
						uploadSecondaryMandateService.save(mandateStatus);
						continue;
					}
					AuditHeader auditHeader = getAuditHeader(preMandate, PennantConstants.TRAN_WF);
					auditHeader = mandateService.doApprove(auditHeader);
					// if any validations in mandate creation log the details
					// with reason
					if (auditHeader.getAuditDetail().getErrorDetails() != null
							&& !auditHeader.getAuditDetail().getErrorDetails().isEmpty()) {
						UploadSecondaryMandate mandateStatus = prepareMandateStatus(mandate, false, "Invalid BarCode");
						failedCount++;
						mandateStatus.setUploadId(uploadId);
						uploadSecondaryMandateService.save(mandateStatus);
						continue;
					}
					// swap the primary mandate to secondary mandate for finance
					Mandate curMandate = (Mandate) auditHeader.getAuditDetail().getModelData();
					Long finID = financeMainService.getFinID(curMandate.getOrgReference());

					financeMainService.loanMandateSwapping(finID, curMandate.getMandateID(),
							curMandate.getMandateType(), "", false);
					UploadSecondaryMandate mandateStatus = prepareMandateStatus(mandate, true, "Success");
					successCount++;
					mandateStatus.setUploadId(uploadId);
					uploadSecondaryMandateService.save(mandateStatus);
				} else { // primary mandate not available log the excel upload
							// data
					if (rowIndex == 2) {
						uploadId = saveUploadHeaderDetails(fName);
					}
					UploadSecondaryMandate mandateStatus = prepareMandateStatus(mandate, false, "MandateId");
					mandateStatus.setUploadId(uploadId);
					failedCount++;
					uploadSecondaryMandateService.save(mandateStatus);
					continue;
				}

			}

		}
		// upload header updated Data
		this.successCount.setValue(String.valueOf(successCount));
		this.failedCount.setValue(String.valueOf(failedCount));
		totalCount = successCount + failedCount;
		this.totalCount.setValue(String.valueOf(totalCount));
		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setSuccessCount(successCount);
		uploadHeader.setFailedCount(failedCount);
		uploadHeader.setTotalRecords(totalCount);
		uploadHeader.setUploadId(uploadId);
		uploadHeaderService.updateRecord(uploadHeader);
		statusGrid.setVisible(true);
		btndownload.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings("deprecation")
	private long saveUploadHeaderDetails(String fileName) {
		logger.debug(Literal.ENTERING);
		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setFileName(fileName);
		uploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		uploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		uploadHeader.setTransactionDate(new Date(DateUtil.getSysDate("dd-MMM-yy")));
		uploadHeader.setModule("Mandate");
		uploadHeader.setVersion(1);
		logger.debug(Literal.LEAVING);
		return uploadHeaderService.save(uploadHeader);
	}

	/*
	 * prepare the SecondaryMandateStatus
	 */
	private UploadSecondaryMandate prepareMandateStatus(Mandate mandate, boolean status, String reason) {
		logger.debug(Literal.ENTERING);

		UploadSecondaryMandate mandateStatus = new UploadSecondaryMandate();
		mandateStatus.setMandateID(mandate.getMandateID());
		mandateStatus.setMandateType(mandate.getMandateType());
		mandateStatus.setmICR(mandate.getMICR());
		mandateStatus.setBankCode(mandate.getBankName());
		mandateStatus.setAccNumber(mandate.getAccNumber());
		mandateStatus.setAccType(mandate.getAccType());
		mandateStatus.setAccHolderName(mandate.getAccHolderName());
		mandateStatus.setBarCodeNumber(mandate.getBarCodeNumber());
		mandateStatus.setStatus(status);
		mandateStatus.setReason(reason);

		logger.debug(Literal.LEAVING);
		return mandateStatus;
	}

	/**
	 * validating the uploaded data
	 * 
	 * @param mandate
	 * @param preMandate
	 * @param remarks
	 * @return String
	 */
	private StringBuilder validateMandateData(Mandate mandate, Mandate preMandate, StringBuilder remarks) {
		logger.debug(Literal.ENTERING);
		// validate the Mandatory data
		if (mandate.getMandateID() <= 0) {
			remarks.append("MandateID is Mandatory,");
		} else {
			String mandateId = String.valueOf(mandate.getMandateID());
			if (mandateId.length() > 19) {
				mandate.setMandateID(Long.valueOf((String) mandateId.subSequence(0, 18)));
				remarks.append("Invalid MandateID Size,");
			}
		}

		if (StringUtils.isBlank(mandate.getMandateType())) {
			remarks.append("MandateType is Mandatory,");
		} else {
			if (mandate.getMandateType().length() > 20) {
				mandate.setMandateType(mandate.getMandateType().substring(0, 19));
				remarks.append("Invalid MandateType Size,");
			}
		}
		if (StringUtils.isBlank(mandate.getMICR())) {
			remarks.append("MICR is Mandatory,");
		} else {
			if (mandate.getMICR().length() > 20) {
				mandate.setMICR(mandate.getMICR().substring(0, 19));
				remarks.append("Invalid MICR Size,");
			}
		}
		if (StringUtils.isBlank(mandate.getBarCodeNumber())) {
			remarks.append("BarCodeNumber is Mandatory,");
		} else {
			if (mandate.getBarCodeNumber().length() > 10) {
				mandate.setBarCodeNumber(mandate.getBarCodeNumber().substring(0, 9));
				remarks.append("Invalid BarCodeNumber Size,");
			}
		}
		if (StringUtils.isBlank(mandate.getAccNumber())) {
			remarks.append("AccNumber is Mandatory,");
		} else {
			if (mandate.getAccNumber().length() > 50) {
				mandate.setAccNumber(mandate.getAccNumber().substring(0, 49));
				remarks.append("Invalid AccNumber Size,");
			}
		}
		if (StringUtils.isBlank(mandate.getAccType())) {
			remarks.append("AccType is Mandatory,");
		} else {
			if (mandate.getAccType().length() > 20) {
				mandate.setAccType(mandate.getAccType().substring(0, 19));
				remarks.append("Invalid AccType Size,");
			}
		}
		if (StringUtils.isBlank(mandate.getBankName())) {
			remarks.append("Bank is Mandatory,");
		} else {
			if (mandate.getBankName().length() > 100) {
				mandate.setBankName(mandate.getBankName().substring(0, 99));
				remarks.append("Invalid Bank Size,");
			}
		}
		if (StringUtils.isBlank(mandate.getAccHolderName())) {
			remarks.append("AccHolderName is Mandatory,");
		} else {
			if (mandate.getAccHolderName().length() > 50) {
				mandate.setAccHolderName(mandate.getAccHolderName().substring(0, 49));
				remarks.append("Invalid AccHolderName Size,");
			}
		}
		// validate accountHolder name
		if (StringUtils.isNotBlank(mandate.getAccHolderName())) {
			Pattern pattern = Pattern.compile(
					PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME));
			Matcher matcher = pattern.matcher(mandate.getAccHolderName());

			if (matcher.matches() == false) {
				remarks.append("Invalid Account HolderName,");
			}
		}

		// validate MandateType
		if (StringUtils.isNotBlank(mandate.getMandateType())) {
			boolean mandateTypeSts = false;
			for (ValueLabel value : MandateUtil.getInstrumentTypes()) {
				if (StringUtils.equals(value.getValue(), mandate.getMandateType())) {
					mandateTypeSts = true;
					break;
				}
			}
			if (!mandateTypeSts) {
				remarks.append("Invalid MandateType");
			}
		}
		if (StringUtils.equals(mandate.getAccType(), "Savings Account")) {
			mandate.setAccType("10");
		} else if (StringUtils.equals(mandate.getAccType(), "Current Account")) {
			mandate.setAccType("11");
		} else if (StringUtils.equals(mandate.getAccType(), "Cash Credit")) {
			mandate.setAccType("12");
		}
		// validate AccType
		if (StringUtils.isNotBlank(mandate.getAccType())) {
			List<ValueLabel> accType = MandateUtil.getAccountTypes();
			boolean accTypeSts = false;
			for (ValueLabel value : accType) {
				if (StringUtils.equals(value.getValue(), mandate.getAccType())) {
					accTypeSts = true;
					break;
				}
			}
			if (!accTypeSts) {
				remarks.append("Invalid AccType,");
			}
		}
		String bankCode = bankDetailService.getBankCodeByName(mandate.getBankName());
		if (StringUtils.isBlank(bankCode)) {
			remarks.append("Invalid Bank,");
		} else {
			mandate.setBankCode(bankCode);
		}
		// validate the MICR code
		BankBranch bankBranch = bankBranchService.getBankBrachByMicr(mandate.getMICR());
		if (bankBranch == null) {
			remarks.append("Invalid MICR,");
		} else {
			// set the bank banchId
			preMandate.setBankBranchID(bankBranch.getBankBranchID());
			// validate the bank code
			if (!StringUtils.equals(bankBranch.getBankCode(), mandate.getBankCode())) {
				remarks.append("MICR Not matched with Bank,");
			}
			// validate the AccNo length
			if (bankBranch.getAccNoLength() != 0) {
				if (mandate.getAccNumber().length() != bankBranch.getAccNoLength()) {
					remarks.append("Invalid Account Number Length,");
				}
			}
		}
		// Mandate Type in the upload should match with the mandate type
		// available against the mandate ID.
		if (StringUtils.equals(mandate.getMandateType(), preMandate.getMandateType())) {
			mandate.setMandateType(InstrumentType.ECS.name());
		} else {
			remarks.append("Invalid Mandate Type,");
		}
		// Barcode Number validation
		List<ErrorDetail> errors = mandateService.doValidations(mandate);
		if (errors != null && !errors.isEmpty()) {
			remarks.append("Invalid BarCode,");
		}

		if (!MandateStatus.isAwaitingConf(preMandate.getStatus())) {
			remarks.append("Invalid Status,");
		}
		// Upload will not be allowed if there is an active secondary mandate
		// for the mandate Id (Primary Mandate)
		int count = mandateService.getSecondaryMandateCount(mandate.getMandateID());
		if (count > 0) {
			remarks.append("Secodary mandate already exists.");
		}
		logger.debug(Literal.LEAVING);
		return remarks;

	}

	public List<String> getAllValuesOfRowByIndex(Workbook workbook, int sheetIndex, int rowindex) {
		logger.debug(Literal.ENTERING);
		List<String> keys = new ArrayList<String>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Row headings = sheet.getRow(rowindex);

		for (Cell cell : headings) {
			objFormulaEvaluator.evaluate(cell);
			String cellValueStr = objDefaultFormat.formatCellValue(cell, objFormulaEvaluator);
			keys.add(cellValueStr.trim());
		}
		logger.debug(Literal.LEAVING);
		return keys;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), null, null, null, auditDetail,
				aMandate.getUserDetails(), getOverideMap());
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public BankBranchService getBankBranchService() {
		return bankBranchService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public UploadHeaderService getUploadHeaderService() {
		return uploadHeaderService;
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public UploadSecondaryMandateService getUploadSecondaryMandateService() {
		return uploadSecondaryMandateService;
	}

	public void setUploadSecondaryMandateService(UploadSecondaryMandateService uploadSecondaryMandateService) {
		this.uploadSecondaryMandateService = uploadSecondaryMandateService;
	}
}