package com.pennant.webui.finance.receiptupload;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.ReceiptUploadHeaderDAO;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class SelectReceiptUploadHeaderDialogCtrl extends GFCBaseCtrl<UploadHeader> {
	private static final Logger logger = LogManager.getLogger(SelectReceiptUploadHeaderDialogCtrl.class);
	private static final long serialVersionUID = 4783031677099154138L;

	protected Window window_ReceiptUpload;
	protected Button btnBrowse;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Button btndownload;
	protected Textbox fileName;
	protected ExtendedCombobox entity;
	private Workbook workbook = null;
	private DataFormatter objDefaultFormat = new DataFormatter();

	private FormulaEvaluator formulaEvaluator = null;

	private String errorMsg = null;
	private ExcelFileImport fileImport = null;

	private ReceiptUploadHeader ruh = new ReceiptUploadHeader();
	private ReceiptUploadHeaderService receiptUploadHeaderService;

	private ReceiptUploadHeaderListCtrl receiptUploadHeaderListCtrl;
	private Media media = null;
	private String filePath = null;
	private File file;

	private FormulaEvaluator objFormulaEvaluator = null;

	private ReceiptService receiptService;
	private ReceiptUploadHeaderDAO receiptUploadHeaderDAO;

	public SelectReceiptUploadHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_ReceiptUpload(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(this.window_ReceiptUpload);

		try {

			if (arguments.containsKey("receiptUploadListCtrl")) {
				setReceiptUploadHeaderListCtrl((ReceiptUploadHeaderListCtrl) arguments.get("receiptUploadListCtrl"));
			}

			if (arguments.containsKey("receiptUploadHeader")) {
				this.ruh = (ReceiptUploadHeader) arguments.get("receiptUploadHeader");
			}

			doSetFieldProperties();
			this.window_ReceiptUpload.doModal();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

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

	public void doShowDialog(ReceiptUploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> aruments = new HashMap<String, Object>();
		aruments.put("uploadReceiptHeader", uploadHeader);
		aruments.put("receiptUploadListCtrl", this.receiptUploadHeaderListCtrl);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/ReceiptUpload/ReceiptUploadHeaderDialog.zul", null,
					aruments);
			this.window_ReceiptUpload.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings("unused")
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.ruh.isNewRecord()) {
			this.btnBrowse.setVisible(true);
			this.btnBrowse.setDisabled(false);
		}

		this.fileName.setReadonly(true);

		logger.debug("Leaving ");
	}

	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		this.fileName.setReadonly(true);
		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		this.fileName.setConstraint("");
		this.fileName.setErrorMessage("");
		this.entity.setConstraint("");
		this.entity.setErrorMessage("");
	}

	public void onFulfill$entity(Event event) {
		logger.debug("Entering");
		this.entity.setConstraint("");
		this.entity.setErrorMessage("");
		if (StringUtils.isBlank(this.entity.getValue())) {
			this.entity.setValue("", "");
		}
		logger.debug("Leaving");
	}

	public void onUpload$btnBrowse(UploadEvent event) {
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

		String fileName = media.getName();

		filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		this.fileImport = new ExcelFileImport(media, filePath);
		this.fileName.setText(fileName);

		logger.debug(Literal.LEAVING);
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
		this.objDefaultFormat = new DataFormatter();
		this.formulaEvaluator = null;
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		final Set<String> setRowIds = new HashSet<String>();

		validateFileName();

		try {

			if (StringUtils.isNotBlank(this.errorMsg)) {
				throw new Exception(this.errorMsg);
			}

			if (media != null) {
				try {
					writeFile(media);
				} catch (Exception e) {
					MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Path_Invalid"));
					return;
				}
			}

			if (file == null) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Exists"));
				return;
			}

			logger.info("Validating File content for the filename {}", this.fileName.getValue());
			if (!validateFileContent()) {
				return;
			}

			Sheet rchSheet = this.workbook.getSheetAt(0);
			int rowCount = rchSheet.getLastRowNum();

			for (int i = 1; i <= rowCount; i++) {
				Row rchRow = rchSheet.getRow(i);

				if (rchRow == null) {
					continue;
				}

				String strValue = getCellStringValue(rchRow, 0);

				if (StringUtils.isBlank(strValue)) {
					errorMsg = "<ROOT>_id with blank value";
					MessageUtil.showError(errorMsg);
					return;
				}

				if (!setRowIds.add(strValue)) {
					errorMsg = "<ROOT>_id " + strValue + " Has duplicate reference in Header Sheet";
					MessageUtil.showError(errorMsg);
					return;
				}
			}

			logger.info("Saving Record as import in progress in ReceiptUploadHeader table before validating records");
			saveUploadHeader();

			logger.info("Initiating Import Process For the HeaderID{}", ruh.getId());

			new Thread(() -> receiptUploadHeaderService.initiateImport(ruh, workbook,
					ReceiptUploadHeaderListCtrl.importStatusMap, fileImport)).start();

			ReceiptUploadHeaderListCtrl.importStatusMap.put(ruh.getId(), 0);
			receiptUploadHeaderListCtrl.search();

			Clients.showNotification("Receipt Import is in Progress", "info", null, null, 2000);
			closeDialog();

		} catch (Exception e) {
			this.errorMsg = e.getMessage();
			doResetData();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void saveUploadHeader() throws FactoryConfigurationError {
		doLoadWorkFlow(true, ruh.getWorkflowId(), "");
		ruh.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		ruh.setTransactionDate(SysParamUtil.getAppDate());
		ruh.setNewRecord(true);
		ruh.setUploadProgress(ReceiptUploadConstants.RECEIPT_IMPORTINPROCESS);
		String taskId = getTaskId(getRole());
		nextTaskId = taskId + ";";
		if (StringUtils.isNotBlank(nextTaskId)) {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {

					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode = getTaskOwner(nextTasks[i]);
				}
			} else {
				nextRoleCode = getTaskOwner(nextTaskId);
			}
		}

		ruh.setTaskId(taskId);
		ruh.setNextTaskId(nextTaskId);
		ruh.setRoleCode(getRole());
		ruh.setNextRoleCode(nextRoleCode);
		ruh.setVersion(0);
		ruh.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ruh.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		ruh.setUserDetails(loggedInUser);
		ruh.setLastMntBy(loggedInUser.getUserId());
		receiptUploadHeaderDAO.save(ruh, TableType.TEMP_TAB);
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
			this.ruh.setFileName(this.fileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.entity.isReadonly()) {
				this.entity.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ReceiptUpload_entity.value"), null, true, true));
				this.ruh.setEntityCode(this.entity.getValue());
				this.ruh.setEntityCodeDesc(this.entity.getDescription());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// set uploadprocess value to zero
		this.ruh.setUploadProgress(PennantConstants.RECEIPT_DEFAULT);

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
			int maxRecords = SysParamUtil.getValueAsInt(SMTParameterConstants.RECEIPT_UPLOAD_RECORD_DEFAULT_SIZE);
			// If Uploaded Receipt record count > 1000
			if (sheet.getPhysicalNumberOfRows() > maxRecords + 1) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_MaxRows" + maxRecords));
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

			if (fsHeaderKeys == null || !(fsHeaderKeys.contains("BOUNCE/CANCELDATE")
					&& fsHeaderKeys.contains("RECEIPTID") && fsHeaderKeys.contains("BOUNCE/CANCELREASON"))) {
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

	public void onClick$btnClose(Event event) {
		this.window_ReceiptUpload.onClose();
	}

	public ReceiptUploadHeader getReceiptUploadHeader() {
		return ruh;
	}

	public void setReceiptUploadHeader(ReceiptUploadHeader receiptUploadHeader) {
		this.ruh = receiptUploadHeader;
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

	@Autowired
	public void setReceiptUploadHeaderDAO(ReceiptUploadHeaderDAO receiptUploadHeaderDAO) {
		this.receiptUploadHeaderDAO = receiptUploadHeaderDAO;
	}

}
