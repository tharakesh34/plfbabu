package com.pennant.webui.finance.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessorConstatnt;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.batchupload.util.BatchProcessorUtil;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectMiscPostingUploadDialogCtrl extends GFCBaseCtrl<UploadHeader> {

	private static final long serialVersionUID = 4783031677099154138L;
	private static final Logger logger = LogManager.getLogger(SelectMiscPostingUploadDialogCtrl.class);

	protected Window window_MiscPostingUpload;

	protected Button btnBrowse;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Button btndownload;

	protected Textbox fileName;
	protected ExtendedCombobox entity;

	private Workbook workbook = null;

	private String errorMsg = null;
	private ExcelFileImport fileImport = null;

	private UploadHeader uploadHeader = new UploadHeader();
	private UploadHeaderService uploadHeaderService;

	private UploadListCtrl uploadListCtrl;
	private Media media = null;
	private String filePath = null;
	private File file;

	private List<MiscPostingUpload> uploadDetailList = new ArrayList<>();
	private List<MiscPostingUpload> uploadNewList = new ArrayList<>();

	boolean isReceiptDetailsExits = false;
	int totalColumns = 14;
	private boolean csvFile = false;
	private String dateFormat = "dd-MM-yyyy";

	public SelectMiscPostingUploadDialogCtrl() {
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
	public void onCreate$window_MiscPostingUpload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_MiscPostingUpload);

		try {

			if (arguments.containsKey("uploadListCtrl")) {
				setUploadListCtrl((UploadListCtrl) arguments.get("uploadListCtrl"));
			} else {
				setUploadListCtrl(null);
			}

			if (arguments.containsKey("uploadHeader")) {
				this.uploadHeader = (UploadHeader) arguments.get("uploadHeader");
			} else {
				this.setUploadHeader(null);
			}

			doSetFieldProperties();
			doCheckRights();

			this.window_MiscPostingUpload.doModal();
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
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param uploadHeader The entity that need to be render.
	 */
	public void doShowDialog(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> aruments = new HashMap<String, Object>();
		aruments.put("uploadHeader", uploadHeader);
		aruments.put("uploadListCtrl", this.uploadListCtrl);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Uploads/MiscPostingUploadDialog.zul", null, aruments);
			this.window_MiscPostingUpload.onClose();
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

		if (this.uploadHeader.isNewRecord()) {
			this.btnBrowse.setVisible(true);
			this.btnBrowse.setDisabled(false);
		}

		this.fileName.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.uploadHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.fileName.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.fileName.setConstraint("");
		this.fileName.setErrorMessage("");
		this.entity.setConstraint("");
		this.entity.setErrorMessage("");

		logger.debug(Literal.LEAVING);
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

		uploadNewList = new ArrayList<>();
		String fileName = media.getName();

		try {
			if (!(StringUtils.endsWith(fileName.toLowerCase(), ".csv"))) {
				MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid csv file.");
				media = null;
				return;
			} else {
				filePath = getFilePath();
				if (StringUtils.endsWith(fileName.toLowerCase(), ".csv")) {
					csvFile = true;
				} else {
					csvFile = false;
					this.fileImport = new ExcelFileImport(media, filePath);
				}
				this.fileName.setText(fileName);
			}
		} catch (Exception e) {
			this.errorMsg = e.getMessage();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		doResetData();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Reset the Data onclicke the refresh button
	 */
	private void doResetData() {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		this.fileName.setText("");
		this.entity.setValue("");
		this.entity.setDescription("");

		this.fileImport = null;
		this.errorMsg = null;

		this.workbook = null;

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		doValidations();

		this.btnBrowse.setDisabled(true);
		this.btnRefresh.setDisabled(true);

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
					MessageUtil.showError(Labels.getLabel("label_MiscPostingUpload_File_Path_Invalid"));
					return;
				}
			}

			// On writing file, if it not exists then not allowed to proceed
			if (file == null) {
				MessageUtil.showError(Labels.getLabel("label_MiscPostingUpload_File_Exists"));
				return;
			}

			// Excel file reading with headers, in case if any mismatch on
			// uploaded data with the format
			if (!validateUploadedFile()) {
				return;
			}

			// If data not exists, no need to proceed further
			if (uploadHeader.getMiscPostingUploads() == null || uploadHeader.getMiscPostingUploads().isEmpty()) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoData"));
				return;
			}

			this.uploadHeader.setMiscPostingUploads(this.uploadHeader.getMiscPostingUploads());

			doShowDialog(this.uploadHeader);

		} catch (Exception e) {
			this.errorMsg = e.getMessage();
			doResetData();
			MessageUtil.showError(e);
			return;
		} finally {
			this.btnBrowse.setDisabled(false);
			this.btnRefresh.setDisabled(false);
			logger.debug(Literal.LEAVING);
		}
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
		if (media.isBinary()) {
			FileUtils.writeByteArrayToFile(file, this.media.getByteData());
		} else {
			FileUtils.writeStringToFile(file, this.media.getStringData());
		}

		logger.debug(Literal.LEAVING);
	}

	/** deciding cell type based on column format */
	public Object getValueByColumnType(Cell cell, String value) {
		Object result = null;
		if (value.equalsIgnoreCase(BatchUploadProcessorConstatnt.TRUE)
				|| value.equalsIgnoreCase(BatchUploadProcessorConstatnt.FALSE)) {
			result = BatchProcessorUtil.boolFormater(value);
		} else if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			result = BatchProcessorUtil.dateFormater(cell.toString());
		} else {
			result = value.trim();
		}
		return result;
	}

	/**
	 * Validate the ModuleType And File Name
	 */
	private void doValidations() {
		logger.debug(Literal.ENTERING);

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
				boolean fileExist = this.uploadHeaderService.isFileNameExist(this.fileName.getValue());
				if (fileExist) {
					throw new WrongValueException(this.fileName,
							this.fileName.getValue() + ": file name already Exist.");
				}
			}
			this.uploadHeader.setFileName(this.fileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.entity.isReadonly()) {
				this.entity.setConstraint(new PTStringValidator(Labels.getLabel("label_MiscPostingUpload_entity.value"),
						null, true, true));
				this.uploadHeader.setEntityCode(this.entity.getValue());
				this.uploadHeader.setEntityDesc(this.entity.getDescription());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Save the
	 * 
	 * @throws Exception
	 */
	private boolean validateUploadedFile() throws Exception {
		logger.debug(Literal.ENTERING);

		if (this.csvFile) {
			// CSV File
			return processCSVUploadDetails(uploadHeader);
		} else if (this.fileImport != null) {

			this.workbook = this.fileImport.writeFile();

			if (this.workbook != null) {
				List<String> keys = this.fileImport.getRowValuesByIndex(this.workbook, 0, 0, totalColumns);

				if (!("TransactionId".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(0)))
						&& "Branch".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(0)))
						&& "BatchPurpose".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(1)))
						&& "PostAgainst".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(2)))
						&& "Reference".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(3)))
						&& "PostingDivision".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(4)))
						&& "Account".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(5)))
						&& "TxnEntry".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(6)))
						&& "ValueDate".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(7)))
						&& "TxnAmount".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(8)))
						&& "NarrLine1".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(9)))
						&& "NarrLine2".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(10)))
						&& "NarrLine3".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(11)))
						&& "NarrLine4".equalsIgnoreCase(StringUtils.trimToEmpty(keys.get(12))))) {
					MessageUtil.showError(
							"The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
					return false;
				}

				Sheet sheet = this.workbook.getSheetAt(0);
				int noOfRows = sheet.getPhysicalNumberOfRows();

				if (noOfRows == 0) {
					MessageUtil.showError("File is empty.");
					return false;
				} else if (noOfRows == 1) {
					MessageUtil.showError("Please provide some records in file");
					return false;
				} else if (noOfRows <= 1001) {
					// Process the records
					processExcelUploadDetails(uploadHeader);
					// Create backup file
					this.fileImport.backUpFile();
				} else {
					MessageUtil.showError("File should not contain more than 1000 records.");
					return false;
				}
			}
		} else {
			MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
			return false;
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * entry point of program, reading whole excel and calling other methods to prepare jsonObject.
	 * 
	 * @return String
	 * @throws Exception
	 */
	private void processExcelUploadDetails(UploadHeader uploadHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		List<MiscPostingUpload> miscPostingUploads = new ArrayList<>();
		Sheet sheet = this.workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
		String dateFormat = "dd-MM-yyyy";

		while (rows.hasNext()) {
			Row row = rows.next();
			int rowIndex = row.getRowNum();
			if (rowIndex > 0) {
				List<String> columns = this.fileImport.getRowValuesByIndex(this.workbook, 0, rowIndex, totalColumns);

				if (CollectionUtils.isNotEmpty(columns)) {
					// String[] beanValues = rowValue.toArray(new String[0]);
					// //convert list to Array
					if (columns.size() >= totalColumns) {
						// Success case
						miscPostingUploads.add(prepareMiscPostingUploadBean(columns, formatter, dateFormat));
					} else {
						MiscPostingUpload miscPostingUpload = new MiscPostingUpload();
						miscPostingUpload.setNewRecord(true);
						miscPostingUpload.setRecordType(PennantConstants.RCD_ADD);
						miscPostingUpload.setVersion(miscPostingUpload.getVersion() + 1);
						miscPostingUpload.setUploadStatus("SUCCESS");
						miscPostingUpload.setReason("Number of columns not matching.");
						miscPostingUploads.add(miscPostingUpload);
					}
				}
			}
		}
		uploadHeader.setTotalRecords(sheet.getPhysicalNumberOfRows() - 1);
		uploadHeader.setTransactionDate(SysParamUtil.getAppDate());
		uploadHeader.setMiscPostingUploads(miscPostingUploads);

		logger.debug(Literal.LEAVING);
	}

	private String getFilePath() {
		String filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		filePath = filePath.concat(File.separator).concat("MiscPostingUpload");
		return filePath;
	}

	@SuppressWarnings("resource")
	private boolean processCSVUploadDetails(UploadHeader uploadHeader) throws IOException {
		logger.debug(Literal.ENTERING);

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String filePath = getFilePath();
		File parent = new File(filePath);

		if (!parent.exists()) {
			parent.mkdirs();
		}

		File file = new File(parent.getPath().concat(File.separator).concat(this.media.getName()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		if (media.isBinary()) {
			FileUtils.writeByteArrayToFile(file, this.media.getByteData());
		} else {
			FileUtils.writeStringToFile(file, this.media.getStringData());
		}

		try {
			br = new BufferedReader(new FileReader(file));
			int count = 0;
			List<MiscPostingUpload> miscPostingUploads = new ArrayList<MiscPostingUpload>();
			int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
			String dateFormat = "dd-MM-yyyy";

			int countRows = 0;
			while ((line = br.readLine()) != null) {
				countRows++;
				if (countRows > 1001) {
					break;
				}
			}

			if (countRows > 1001) {
				MessageUtil.showError("File should not contain more than 1000 records.");
				return false;
			}

			line = "";
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {

				List<String> row = Arrays.asList(line.split(cvsSplitBy, totalColumns));

				if (row.size() >= 0) {
					if (count == 0) { // Skip Header row
						if (!("TransactionId".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(0)))
								&& "Branch".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(1)))
								&& "BatchPurpose".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(2)))
								&& "PostAgainst".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(3)))
								&& "Reference".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(4)))
								&& "PostingDivision".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(5)))
								&& "Account".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(6)))
								&& "TxnEntry".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(7)))
								&& "ValueDate".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(8)))
								&& "TxnAmount".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(9)))
								&& "NarrLine1".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(10)))
								&& "NarrLine2".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(11)))
								&& "NarrLine3".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(12)))
								&& "NarrLine4".equalsIgnoreCase(StringUtils.trimToEmpty(row.get(13))))) {
							MessageUtil.showError(
									"The uploaded file could not be recognized. Please upload valid csv file(Key field is not matching).");
							return false;
						}
					} else {
						miscPostingUploads.add(prepareMiscPostingUploadBean(row, formatter, dateFormat));
					}
				} else {
					if (count == 1) {
						MessageUtil.showError("Please upload a file with records.");
						return false;
					}
					// Failure Case
					MiscPostingUpload miscPostingUpload = new MiscPostingUpload();
					miscPostingUpload.setNewRecord(true);
					miscPostingUpload.setRecordType(PennantConstants.RCD_ADD);
					miscPostingUpload.setVersion(miscPostingUpload.getVersion() + 1);
					miscPostingUpload.setUploadStatus("FAILED");
					miscPostingUpload.setReason("Number of columns not matching.");
					miscPostingUploads.add(miscPostingUpload);
				}

				count++;
			}
			uploadHeader.setMiscPostingUploads(miscPostingUploads);
			uploadHeader.setTransactionDate(SysParamUtil.getAppDate());
			uploadHeader.setTotalRecords(count - 1);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
			return false;
		} finally {
			backUpFile(file);
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	public void backUpFile(File file) throws IOException {
		logger.debug(Literal.ENTERING);

		if (file != null) {

			File backupFile = new File(file.getParent() + "/BackUp");

			if (!backupFile.exists()) {
				backupFile.mkdir();
			}

			FileUtils.copyFile(file, new File(backupFile.getPath().concat(File.separator).concat(file.getName())));

			if (file.exists()) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private MiscPostingUpload prepareMiscPostingUploadBean(List<String> row, int formatter, String dateFormat)
			throws ParseException {
		logger.debug(Literal.ENTERING);

		boolean error = false;
		String reason = "";
		Date valueDate = null;
		BigDecimal txnAmount = BigDecimal.ZERO;
		MiscPostingUpload miscPostingUpload = new MiscPostingUpload();

		try {
			miscPostingUpload.setTransactionId(Long.valueOf(row.get(0)));
		} catch (NumberFormatException e) {
			miscPostingUpload.setTransactionId(0);
			if (!error) {
				reason = "Invalid Transaction Id";
				error = true;
			}
			logger.warn(e);
		}

		try {
			miscPostingUpload.setBranch(row.get(1).toUpperCase());
		} catch (Exception e) {
			logger.warn(e);
		}

		try {
			miscPostingUpload.setBatchPurpose(row.get(2));
		} catch (Exception e) {
			logger.warn(e);
		}
		try {
			miscPostingUpload.setPostAgainst(row.get(3).toUpperCase());
		} catch (Exception e) {
			logger.warn(e);
		}
		try {
			miscPostingUpload.setReference(row.get(4).toUpperCase());
		} catch (Exception e) {
			logger.warn(e);
		}
		try {
			miscPostingUpload.setPostingDivision(row.get(5).toUpperCase());
		} catch (Exception e) {
			logger.warn(e);
		}
		try {
			miscPostingUpload.setAccount(row.get(6).toUpperCase());
		} catch (Exception e) {
			logger.warn(e);
		}
		try {
			miscPostingUpload.setTxnEntry(row.get(7).toUpperCase());
		} catch (Exception e) {
			logger.warn(e);
		}

		if (StringUtils.isNotBlank(row.get(8))) {
			try {
				DateFormat dateFormt = new SimpleDateFormat(this.dateFormat);
				valueDate = dateFormt.parse(row.get(8));
			} catch (ParseException e) {
				valueDate = null;
				error = true;
				reason = "Invalid Value Date, it should be in the dd-MM-yyyy format";
			}
		}

		try {
			txnAmount = CurrencyUtil.unFormat(row.get(9), formatter);
		} catch (Exception e) {
			txnAmount = BigDecimal.ZERO;
			reason = "Wrong Transaction Amount format";
			error = true;
		}

		miscPostingUpload.setValueDate(valueDate);
		miscPostingUpload.setTxnAmount(txnAmount);

		try {
			miscPostingUpload.setNarrLine1(row.get(10));
		} catch (Exception e) {
			logger.warn(e);
		}
		try {
			miscPostingUpload.setNarrLine2(row.get(11));
		} catch (Exception e) {
			logger.warn(e);
		}
		try {
			miscPostingUpload.setNarrLine3(row.get(12));
		} catch (Exception e) {
			logger.warn(e);
		}
		try {
			miscPostingUpload.setNarrLine4(row.get(13));
		} catch (Exception e) {
			logger.warn(e);
		}

		miscPostingUpload.setNewRecord(true);
		miscPostingUpload.setRecordType(PennantConstants.RCD_ADD);
		miscPostingUpload.setVersion(miscPostingUpload.getVersion() + 1);

		if (error) {
			miscPostingUpload.setUploadStatus("FAILED");
			miscPostingUpload.setReason(reason);
		} else {
			miscPostingUpload.setUploadStatus("SUCCESS");
		}

		logger.debug(Literal.LEAVING);
		return miscPostingUpload;
	}

	public void onClick$btnClose(Event event) {
		this.window_MiscPostingUpload.onClose();
	}

	public UploadHeader getUploadHeader() {
		return uploadHeader;
	}

	public void setUploadHeader(UploadHeader uploadHeader) {
		this.uploadHeader = uploadHeader;
	}

	public UploadHeaderService getUploadHeaderService() {
		return uploadHeaderService;
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}

	public UploadListCtrl getUploadListCtrl() {
		return uploadListCtrl;
	}

	public void setUploadListCtrl(UploadListCtrl uploadListCtrl) {
		this.uploadListCtrl = uploadListCtrl;
	}

	public List<MiscPostingUpload> getUploadDetailList() {
		return uploadDetailList;
	}

	public void setUploadDetailList(List<MiscPostingUpload> uploadDetailList) {
		this.uploadDetailList = uploadDetailList;
	}

	public List<MiscPostingUpload> getUploadNewList() {
		return uploadNewList;
	}

	public void setUploadNewList(List<MiscPostingUpload> uploadNewList) {
		this.uploadNewList = uploadNewList;
	}

}
