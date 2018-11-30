package com.pennant.webui.finance.receiptupload;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessorConstatnt;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.batchupload.util.BatchProcessorUtil;
import com.pennanttech.dataengine.util.DateUtil.DateFormat;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectReceiptUploadHeaderDialogCtrl extends GFCBaseCtrl<UploadHeader> {

	private static final long			serialVersionUID	= 4783031677099154138L;
	private static final Logger			logger				= Logger
			.getLogger(SelectReceiptUploadHeaderDialogCtrl.class);

	protected Window					window_ReceiptUpload;

	protected Button					btnBrowse;
	protected Button					btnSave;
	protected Button					btnRefresh;
	protected Button					btndownload;

	protected Textbox					fileName;
	protected ExtendedCombobox			entity;

	private Workbook					workbook			= null;
	private DataFormatter				objDefaultFormat	= new DataFormatter();		// for cell value formating
	private FormulaEvaluator			formulaEvaluator	= null;						// for cell value formating
	private String						errorMsg			= null;
	private ExcelFileImport				fileImport			= null;

	private ReceiptUploadHeader			receiptUploadHeader	= new ReceiptUploadHeader();
	private ReceiptUploadHeaderService	receiptUploadHeaderService;

	private ReceiptUploadHeaderListCtrl	receiptUploadHeaderListCtrl;
	private Media						media				= null;
	private String						filePath			= null;
	private File						file;

	private List<ReceiptUploadDetail>	uploadDetailList	= new ArrayList<>();
	private FormulaEvaluator			objFormulaEvaluator	= null;
	private List<ReceiptUploadDetail>	uploadNewList		= new ArrayList<>();
	
	int									lengthfour			= 4;
	int									lengthTwenty		= 20;
	int									lengthSingle		= 1;
	int									lengthTen			= 10;
	int									lengthHunder		= 100;
	int									lengthEight			= 8;
	int									lengthFifty			= 50;
	int									lengtheighteen		= 18;
	boolean								isReceiptDetailsExits	= false;
	
	private ReceiptService				receiptService;

	
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
			}else{
				this.setReceiptUploadHeader(null);
			}

			doSetFieldProperties();
			doCheckRights();

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
			Executions.createComponents("/WEB-INF/pages/Finance/ReceiptUpload/ReceiptUploadHeaderDialog.zul", null, aruments);
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
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.receiptUploadHeader.isNew()) {
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
			this.entity.setValue("","");
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
			if (!(StringUtils.endsWith(fileName.toLowerCase(), ".xls")
					|| StringUtils.endsWith(fileName.toLowerCase(), ".xlsx"))) {
				this.errorMsg = Labels.getLabel("label_ReceiptUpload_Format_NotAllowed.value");
				MessageUtil.showError(this.errorMsg);
				media = null;
				return;
			} else {
				filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
				this.fileImport = new ExcelFileImport(media, filePath);
				this.fileName.setText(fileName);
			}
		} catch (Exception e) {
			this.errorMsg = e.getMessage();
			MessageUtil.showError(e);
		}

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
		this.objDefaultFormat = new DataFormatter();// for cell value formating
		this.formulaEvaluator = null; // for cell value formating

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
					MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Path_Invalid"));
					return;
				}
			}
			
			// On writing file, if it not exists then not allowed to proceed
			if (file == null) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Exists"));
				return;
			}

			// Excel file reading with headers, in case if any mismatch on uploaded data with the format
			if (!validateUploadedFile()) {
				return;
			}

			// Unique Receipt ROOT ID validation
			if (checkDuplicateUniqueID()) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Invalid_UniqueID"));
				return;
			}

			// Preparation of JSON object using Excel uploaded file
			try {
				doFileProcess();
			} catch (Exception e) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_Invalid_Data"));
				return;
			}

			// If data not exists, no need to proceed further
			if (uploadDetailList == null || uploadDetailList.isEmpty()) {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoData"));
				return;
			}

			this.receiptUploadHeader.setReceiptUploadList(this.uploadDetailList);
			//Create backup file
			this.fileImport.backUpFile();
			doShowDialog(this.receiptUploadHeader);

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
	 * Method for Checking Uniqueness on ID
	 * @return
	 */
	private boolean checkDuplicateUniqueID() {
		logger.debug(Literal.ENTERING);

		List<String> list = new ArrayList<>();
		Sheet sheet = this.workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			int rowIndex = row.getRowNum();
			Cell cell = row.getCell(0);

			if (rowIndex > 0 && cell != null) {
				list.add(objDefaultFormat.formatCellValue(cell, objFormulaEvaluator));
			}
		}

		if (!list.isEmpty()) {
			final Set<String> set1 = new HashSet<String>();

			for (String yourInt : list) {
				if (!set1.add(yourInt)) {
					logger.debug(Literal.LEAVING);
					return true;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	/**
	 * Writing Media file into File Object
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
		
		// If File already exists in path, we need to delete first before replace
		if (file.exists()) {
			file.delete();
		}
		
		// Creating File in Server path for Loading/Reading file from path
		file.createNewFile();
		FileUtils.writeByteArrayToFile(file, media.getByteData());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Reading Excel file and writing data into JSON objects
	 * @throws Exception
	 */
	private void doFileProcess() throws Exception{
		logger.debug(Literal.ENTERING);

		List<String> keys = BatchProcessorUtil.getAllKeysByIndex(workbook, 0);
		Sheet sheet = workbook.getSheetAt(0);

		if (this.workbook instanceof HSSFWorkbook) {
			this.objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) this.workbook);
		} else if (this.workbook instanceof XSSFWorkbook) {
			this.objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) this.workbook);
		}
		
		int emptyRcdCount = 0;
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			JSONObject finalRequestJson = new JSONObject();
			JSONObject jsonForextendedField = new JSONObject();

			Row row = rows.next();
			int rowIndex = row.getRowNum();
			
			// Headers Data not required
			if(rowIndex == 0){
				continue;
			}

			String messageId =  String.valueOf(Math.random()) + rowIndex;
			int cellIndex = 0;
			String rootID = "";
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				// skipping header and other column value which is not inside header
				if (cellIndex < keys.size()) {
					
					if(cellIndex == 0){
						rootID = StringUtils.trimToEmpty(cell.toString());
					}

					// Only for ROOT ID Data fetching from Sheet
					if (StringUtils.isEmpty(cell.toString()) && cellIndex == 0) {
						finalRequestJson = new JSONObject(); // resting
						finalRequestJson.put(keys.get(cellIndex), objDefaultFormat.formatCellValue(cell, objFormulaEvaluator));
					} else { 
						// Data from Sheet other than ROOT ID
						objFormulaEvaluator.evaluate(cell);
						String cellValueStr = objDefaultFormat.formatCellValue(cell, objFormulaEvaluator);
						if(StringUtils.isNotBlank(rootID) && cellIndex == 0){
							doCompare(keys.get(cellIndex), getValueByColumnType(cell, cellValueStr), finalRequestJson, jsonForextendedField);
						}else{
							finalRequestJson.put(keys.get(cellIndex), objDefaultFormat.formatCellValue(cell, objFormulaEvaluator));
						}
					}
				}
				cellIndex++;
			}
			
			// checking finalRequestJson having value or not
			if (isJsonObjectValueEmpty(finalRequestJson)) {
				finalRequestJson = new JSONObject(); // resting
				emptyRcdCount = emptyRcdCount + 1;
			}
			
			// everything is fine just call api and write response back.
			if (finalRequestJson.length() == 0) {
				if(emptyRcdCount >= 2){
					break;
				}
				continue;
			}else{
				emptyRcdCount = 0;
			}
			
			callAPIForUploadReceipts(finalRequestJson, messageId);
		}
		logger.debug(Literal.LEAVING);
	}
	/**
	 * method will call appropriate api with given jsonobject and return response back to writing to excel
	 * 
	 * @param json
	 *            prepared jsonObject
	 * @param writebleSheet
	 *            response will write to this field
	 * @param messageId
	 *            to pass as input header
	 * @param lastCellIndex
	 *            cell index where the response will written
	 * @throws Exception 
	 */
	private synchronized void callAPIForUploadReceipts(JSONObject json, String messageId) throws Exception{
		if (json.length() > 0) {
			ReceiptUploadDetail receiptUploadDetail = callApi(json, messageId);
			if (receiptUploadDetail != null) {
				uploadDetailList.add(receiptUploadDetail);
			}
		}
	}
	
	/**
	 * Checking before calling api whether json object contains any value or not
	 * @param finalRequestJson
	 * @return
	 */
	private boolean isJsonObjectValueEmpty(JSONObject finalRequestJson) {
		Iterator<?> keys = finalRequestJson.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String value;
			try {
				value = String.valueOf(finalRequestJson.get(key));
				if (StringUtils.isNotBlank(value)) {
					return false;
				}
			} catch (Exception e) {
				logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
			}
		}
		return true;
	}

	/**
	 * calling api with json object and returning array as response
	 * @param jsondata
	 * @param messageId
	 * @return
	 * @throws Exception
	 */
	private ReceiptUploadDetail callApi(JSONObject jsondata, String messageId) throws Exception {
		logger.debug("API REQUEST :: " + jsondata.toString());

		String errorMsg = "";
		String errorCode = "0000";
		JSONObject reqJson = new JSONObject();
		ReceiptUploadDetail receiptUploadDetail = new ReceiptUploadDetail();
		List<UploadAlloctionDetail> listUploadAlloctionDetail = null;

		//set to false default,as default it should be false,for cheque/DD receipt mode
		isReceiptDetailsExits = false;

		try {
			String url = SysParamUtil.getValueAsString("RECEIPTAPIURL");
			if (jsondata.getString("RECEIPTPURPOSE").equalsIgnoreCase("ES")) {
				url = url + "finInstructionRest/loanInstructionService/earlySettlement";

				//fromDate
				reqJson.put("fromDate",
						DateUtility.formatDate(DateUtility.getUtilDate(jsondata.get("RECEIVEDDATE").toString(),
								DateFormat.LONG_DATE.getPattern()), PennantConstants.APIDateFormatter));
			} else if (jsondata.getString("RECEIPTPURPOSE").equalsIgnoreCase("SP")) {
				url = url + "finInstructionRest/loanInstructionService/manualPayment";
			} else if (jsondata.getString("RECEIPTPURPOSE").equalsIgnoreCase("EP")) {
				url = url + "finInstructionRest/loanInstructionService/partialSettlement";
			} else {
				url = " ";
				errorMsg = Labels.getLabel("inValid_ReceiptPurpose");
				errorCode = PennantConstants.ERR_9999;
			}
		} catch (Exception e) {
			errorMsg = Labels.getLabel("inValid_ReceiptPurpose");
			errorCode = PennantConstants.ERR_9999;
		}

		//root id
		try {
			reqJson.put("rootId", jsondata.get("<ROOT>_id"));
			receiptUploadDetail.setRootId(jsondata.getString("<ROOT>_id"));
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_RootID");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Receipt Purpose
		reqJson.put("receiptPurpose", jsondata.get("RECEIPTPURPOSE"));
		receiptUploadDetail.setReceiptPurpose(reqJson.getString("receiptPurpose"));

		//reCalType
		reqJson.put("reCalType", jsondata.get("EFFECTSCHDMETHOD"));
		receiptUploadDetail.setEffectSchdMethod(reqJson.getString("reCalType"));

		// Finance Reference
		reqJson.put("finReference", jsondata.get("REFERENCE"));
		receiptUploadDetail.setReference(reqJson.getString("finReference"));

		// Receipt Amount
		try {
			if (StringUtils.isBlank(String.valueOf(jsondata.get("RECEIPTAMOUNT")))) {
				reqJson.put("amount", BigDecimal.ZERO);
			} else {
				reqJson.put("amount", jsondata.getBigDecimal("RECEIPTAMOUNT").multiply(new BigDecimal(100)));
			}
			receiptUploadDetail.setReceiptAmount(reqJson.getBigDecimal("amount"));
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_ReceiptAmount");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Receipt Mode
		reqJson.put("paymentMode", jsondata.get("RECEIPTMODE"));
		receiptUploadDetail.setReceiptMode(reqJson.getString("paymentMode"));

		// Excess Adjust TO
		reqJson.put("excessAdjustTo", jsondata.get("EXCESSADJUSTTO"));
		receiptUploadDetail.setExcessAdjustTo(reqJson.getString("excessAdjustTo"));

		// Allocation Type
		reqJson.put("allocationType", jsondata.get("ALLOCATIONTYPE"));
		receiptUploadDetail.setAllocationType(reqJson.getString("allocationType"));

		// Remarks
		reqJson.put("remarks", jsondata.get("REMARKS"));
		receiptUploadDetail.setRemarks(reqJson.getString("remarks"));

		// Value Date
		try {
			reqJson.put("valueDate", DateUtility.formatDate(
					DateUtility.getUtilDate(jsondata.get("VALUEDATE").toString(), DateFormat.LONG_DATE.getPattern()),
					PennantConstants.APIDateFormatter));
			if (StringUtils.isBlank(reqJson.getString("valueDate"))) {
				receiptUploadDetail.setValueDate(null);
			} else {
				receiptUploadDetail.setValueDate(DateUtility.getUtilDate(jsondata.get("VALUEDATE").toString(),
						DateFormat.LONG_DATE.getPattern()));
			}
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_ValueDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Receipt Received Date/Receipt Value Date
		try {
			reqJson.put("receivedDate", DateUtility.formatDate(
					DateUtility.getUtilDate(jsondata.get("RECEIVEDDATE").toString(), DateFormat.LONG_DATE.getPattern()),
					PennantConstants.APIDateFormatter));
			if (StringUtils.isBlank(reqJson.getString("receivedDate"))) {
				receiptUploadDetail.setReceivedDate(null);
			} else {
				receiptUploadDetail.setReceivedDate(DateUtility.getUtilDate(jsondata.get("RECEIVEDDATE").toString(),
						DateFormat.LONG_DATE.getPattern()));
			}
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_ReceivedDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Partner Bank
		reqJson.put("depositAccount", jsondata.get("FUNDINGAC"));
		receiptUploadDetail.setFundingAc(reqJson.getString("depositAccount"));

		// Transaction Reference
		reqJson.put("transactionRef", jsondata.get("TRANSACTIONREF"));
		receiptUploadDetail.setTransactionRef(reqJson.getString("transactionRef"));

		// Payment Reference
		reqJson.put("paymentRef", jsondata.get("PAYMENTREF"));
		receiptUploadDetail.setPaymentRef(reqJson.getString("paymentRef"));

		// Favour Number
		reqJson.put("favourNumber", jsondata.get("FAVOURNUMBER"));
		receiptUploadDetail.setFavourNumber(reqJson.getString("favourNumber"));

		// Bank Code
		reqJson.put("bankCode", jsondata.get("BANKCODE"));
		receiptUploadDetail.setBankCode(reqJson.getString("bankCode"));

		// Cheque Number
		reqJson.put("chequeNo", jsondata.get("CHEQUEACNO"));
		receiptUploadDetail.setChequeNo(reqJson.getString("chequeNo"));

		// Status
		reqJson.put("status", jsondata.get("STATUS"));
		receiptUploadDetail.setStatus(reqJson.getString("status"));

		// Deposit Date
		try {
			reqJson.put("depositDate", DateUtility.formatDate(
					DateUtility.getUtilDate(jsondata.get("DEPOSITDATE").toString(), DateFormat.LONG_DATE.getPattern()),
					PennantConstants.APIDateFormatter));
			if (StringUtils.isBlank(reqJson.getString("depositDate"))) {
				receiptUploadDetail.setDepositDate(null);
			} else {
				receiptUploadDetail.setDepositDate(DateUtility.getUtilDate(jsondata.get("DEPOSITDATE").toString(),
						DateFormat.LONG_DATE.getPattern()));
			}
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_DepositDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Realization Date
		try {
			reqJson.put("realizationDate",
					DateUtility.formatDate(DateUtility.getUtilDate(jsondata.get("REALIZATIONDATE").toString(),
							DateFormat.LONG_DATE.getPattern()), PennantConstants.APIDateFormatter));
			if (StringUtils.isBlank(reqJson.getString("realizationDate"))) {
				receiptUploadDetail.setRealizationDate(null);
			} else {
				receiptUploadDetail.setRealizationDate(DateUtility
						.getUtilDate(jsondata.get("REALIZATIONDATE").toString(), DateFormat.LONG_DATE.getPattern()));
			}
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_RealizationDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		// Instrument Date -- Not using
		try {
			reqJson.put("instrumentDate",
					DateUtility.formatDate(DateUtility.getUtilDate(jsondata.get("INSTRUMENTDATE").toString(),
							DateFormat.LONG_DATE.getPattern()), PennantConstants.APIDateFormatter));
			if (StringUtils.isBlank(reqJson.getString("instrumentDate"))) {
				receiptUploadDetail.setInstrumentDate(null);
			} else {
				receiptUploadDetail.setInstrumentDate(DateUtility.getUtilDate(jsondata.get("INSTRUMENTDATE").toString(),
						DateFormat.LONG_DATE.getPattern()));
			}
		} catch (Exception e) {
			if (StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("inValid_InstrumentDate");
				errorCode = PennantConstants.ERR_9999;
			}
		}

		reqJson.put("reqType", "Inquiry");
		reqJson.put("isUpload", true);
		reqJson.put("entity", this.receiptUploadHeader.getEntityCode());
		reqJson.put("entityDesc", this.receiptUploadHeader.getEntityCodeDesc());
		reqJson.put("receiptFileName", this.receiptUploadHeader.getFileName());//To Check validation at Upload Level

		JSONArray allocationDetails = new JSONArray();
		if (jsondata.has("Sheet2")) {
			allocationDetails = jsondata.getJSONArray("Sheet2");
		}
		JSONArray allocationDetailsReq = new JSONArray();
		listUploadAlloctionDetail = new ArrayList<>();
		Map<String, Boolean> keyMap = new HashMap<>();

		// Reading Allocations based on ROOT ID
		for (int i = 0; i < allocationDetails.length(); i++) {

			JSONObject allocation = new JSONObject();
			UploadAlloctionDetail aloc = new UploadAlloctionDetail();

			// Allocation Type
			allocation.put("allocationType",
					allocationDetails.getJSONObject(i).get("ALLOCATIONTYPE").toString().toUpperCase());
			aloc.setAllocationType(allocation.get("allocationType").toString());

			// Allocation To
			allocation.put("referenceCode", allocationDetails.getJSONObject(i).get("REFERENCECODE"));
			aloc.setReferenceCode(allocation.get("referenceCode").toString());

			// Allocation Paid Amount
			if (!StringUtils.isBlank(String.valueOf(allocationDetails.getJSONObject(i).get("PAIDAMOUNT")))) {
				allocation.put("paidAmount",
						allocationDetails.getJSONObject(i).getBigDecimal("PAIDAMOUNT").multiply(new BigDecimal(100)));
				aloc.setPaidAmount(new BigDecimal(allocation.get("paidAmount").toString()));
			} else {
				allocation.put("paidAmount", BigDecimal.ZERO);
			}

			if (!StringUtils.isBlank(String.valueOf(allocationDetails.getJSONObject(i).get("WAIVEDAMOUNT")))) {
				allocation.put("waivedAmount",
						allocationDetails.getJSONObject(i).getBigDecimal("WAIVEDAMOUNT").multiply(new BigDecimal(100)));
				aloc.setWaivedAmount(new BigDecimal(allocation.get("waivedAmount").toString()));
			} else {
				allocation.put("waivedAmount", BigDecimal.ZERO);
			}

			allocationDetailsReq.put(allocation);
			String key = aloc.getAllocationType();
			if (StringUtils.isNotEmpty(aloc.getReferenceCode()) && (StringUtils.contains(aloc.getAllocationType(), "M")
					|| StringUtils.contains(aloc.getAllocationType(), "B")
					|| StringUtils.contains(aloc.getAllocationType(), "F"))) {
				key = key + "_" + aloc.getReferenceCode();
			}
			if (keyMap.containsKey(key) && StringUtils.isEmpty(errorCode)) {
				errorMsg = Labels.getLabel("Duplicate_AllocationDetail");
				errorCode = PennantConstants.ERR_9999;
			}
			keyMap.put(key, true);

			aloc.setRootId(jsondata.getString("<ROOT>_id"));
			listUploadAlloctionDetail.add(aloc);
		}

		reqJson.put("allocationDetails", allocationDetailsReq);

		receiptUploadDetail.setListAllocationDetails(listUploadAlloctionDetail);

		//validate basic data of excel
		if (StringUtils.isEmpty(errorMsg)) {
			String returnmsg = doBasicValidation(receiptUploadDetail);
			if (StringUtils.isNotEmpty(returnmsg)) {
				errorMsg = returnmsg;
				errorCode = PennantConstants.ERR_9999;
			}
		}

		reqJson.put("receiptdetailExits", isReceiptDetailsExits);

		// API CALL for ENQUIRY RESULT
		if (StringUtils.isEmpty(errorCode)) {

			
			Gson gson = new GsonBuilder()
					   .setDateFormat(PennantConstants.APIDateFormatter).create();
			FinServiceInstruction finServiceInstruction = gson.fromJson(reqJson.toString(),
					FinServiceInstruction.class);
			String method = null;
			if (jsondata.getString("RECEIPTPURPOSE").equalsIgnoreCase("ES")) {
				method = FinanceConstants.FINSER_EVENT_EARLYSETTLE;
			} else if (jsondata.getString("RECEIPTPURPOSE").equalsIgnoreCase("SP")) {
				method = FinanceConstants.FINSER_EVENT_SCHDRPY;
			} else if (jsondata.getString("RECEIPTPURPOSE").equalsIgnoreCase("EP")) {
				method = FinanceConstants.FINSER_EVENT_EARLYRPY;
			}

			AuditDetail auditDetail = getReceiptService().doReceiptValidations(finServiceInstruction, method);
			if (auditDetail.getErrorDetails() != null) {
				for (ErrorDetail errorDetail : auditDetail.getErrorDetails()) {
					errorCode = errorDetail.getCode();
					errorMsg = errorDetail.getError();
					break;
				}
			}

		}

		receiptUploadDetail.setJsonObject(reqJson.toString());
		receiptUploadDetail.setListAllocationDetails(listUploadAlloctionDetail);
		if (StringUtils.equals("0000", errorCode)) {
			receiptUploadDetail.setUploadStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
			receiptUploadDetail.setReason("");
		} else {
			receiptUploadDetail.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);
			receiptUploadDetail.setReason(errorCode + " : " + errorMsg);
		}

		//chcking for dedup
		uploadNewList.add(receiptUploadDetail);

		return receiptUploadDetail;

	}
	
	/**
	 * check basic validation like length issues
	 * @param receiptUploadDetail
	 */
	private String doBasicValidation(ReceiptUploadDetail receiptUploadDetail) {
		logger.debug(Literal.ENTERING);

		//reference
		if(StringUtils.isBlank(receiptUploadDetail.getReference())){
			return Labels.getLabel("invalid_reference");
		}
		
		//root id
		if(StringUtils.isBlank(receiptUploadDetail.getRootId())){
			return Labels.getLabel("invalid_rootid");
		} else if (lengthfour < receiptUploadDetail.getRootId().length()) {
			return Labels.getLabel("invalid_rootid_length");
		} else if (!StringUtils.isNumeric(receiptUploadDetail.getRootId())){
			return Labels.getLabel("invalid_rootid_data");
		}
		
		//receipt purpose
		if(StringUtils.isBlank(receiptUploadDetail.getReceiptPurpose())){
			return Labels.getLabel("invalid_ReceiptPurpose");
		} else if (lengthTwenty < receiptUploadDetail.getReceiptPurpose().length()){
			return Labels.getLabel("invalid_ReceiptPurpose_length");
		}
	
		//allocation type
		if(StringUtils.isBlank(receiptUploadDetail.getAllocationType())){
			return Labels.getLabel("invalid_AllocationType");
		} else if (lengthSingle < receiptUploadDetail.getAllocationType().length()){
			return Labels.getLabel("invalid_AllocationType_length");
		}
		
		//receipt amount
		if(BigDecimal.ZERO.compareTo(receiptUploadDetail.getReceiptAmount())==0){
			return Labels.getLabel("invalid_ReceiptAmt");
		}
		
		//EFFECTSCHDMETHOD
		if (!StringUtils.isBlank(receiptUploadDetail.getReceiptPurpose())
				&& StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptPurpose(), "EP")
				&& StringUtils.isBlank(receiptUploadDetail.getEffectSchdMethod())) {
			return Labels.getLabel("invalid_EffectiveSchdMth");
		} else if (!StringUtils.isBlank(receiptUploadDetail.getReceiptPurpose())
				&& StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptPurpose(), "EP")
				&& lengthTen < receiptUploadDetail.getEffectSchdMethod().length()) {
			return Labels.getLabel("invalid_EffectiveSchdMth_length");
		}
		
		//remarks
		if(StringUtils.isBlank(receiptUploadDetail.getRemarks())){
			return Labels.getLabel("invalid_Remarks");
		} else if (lengthHunder < receiptUploadDetail.getRemarks().length()){
			return Labels.getLabel("invalid_Remarks_length");
		}
		

		//VALUEDATE
		if(null == receiptUploadDetail.getValueDate()){
			return Labels.getLabel("invalid_ValueDate");
		}
		
		//RECEIVEDDATE
		if(null == receiptUploadDetail.getReceivedDate()){
			return Labels.getLabel("invalid_ReceivedDate");
		}
		
		//receipt mode
		if(StringUtils.isBlank(receiptUploadDetail.getReceiptMode())){
			return Labels.getLabel("invalid_ReceiptMode");
		} else if (lengthTen < receiptUploadDetail.getReceiptMode().length()){
			return Labels.getLabel("invalid_ReceiptMode_length");
		}
		
		//funding ac
		if(StringUtils.isBlank(receiptUploadDetail.getFundingAc())){
			return Labels.getLabel("invalid_FundingAcc");
		} else if (lengthEight < receiptUploadDetail.getFundingAc().length()){
			return Labels.getLabel("invalid_FundingAcc_length");
		}
		
		//Payment ref
		if (StringUtils.isNotBlank(receiptUploadDetail.getPaymentRef())
				&& lengthFifty < receiptUploadDetail.getPaymentRef().length()) {
			return Labels.getLabel("Invalid_PaymentRef_length");
		}
		
		//STATUS
		if(StringUtils.isBlank(receiptUploadDetail.getStatus())){
			return Labels.getLabel("invalid_Status");
		} else if (lengthSingle < receiptUploadDetail.getStatus().length()){
			return Labels.getLabel("invalid_Status_length");
		}
	    
		//check de-dupe validation
	    if(checkDedupCondition(receiptUploadDetail)){
	    	return Labels.getLabel("Dedup_Check");
	    }
		
		//check  FinReference whether it is present in maker stage
		if (StringUtils.isNotBlank(this.fileName.getValue())) {
			String finReference = getReceiptUploadHeaderService().getLoanReferenc(receiptUploadDetail.getReference(),
					this.fileName.getValue());
			if (StringUtils.isNotBlank(finReference)) {
				return Labels.getLabel("duplicate_referencExits_file");
			}
		}
		
		//check loan is active or not
		boolean isLoanRefExits = getReceiptUploadHeaderService().isFinReferenceExists(receiptUploadDetail.getReference(), "", false);
		if (!isLoanRefExits) {
			return Labels.getLabel("no_reference_exits");
		}
		
		//entity validation
		boolean isExits = getReceiptUploadHeaderService().isFinReferenceExitsWithEntity(
				receiptUploadDetail.getReference(), "_aview", this.entity.getValidatedValue());
		if (!isExits) {
			return Labels.getLabel("is_Entity_matching");
		}
		
		if ((StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)
				|| StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(), RepayConstants.RECEIPTMODE_DD))
				&& StringUtils.equalsIgnoreCase(receiptUploadDetail.getStatus(), RepayConstants.PAYSTATUS_REALIZED)) {
			
			boolean isreceiptdataExits = false;
			
			if (StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(), RepayConstants.RECEIPTMODE_CHEQUE)) {
				isreceiptdataExits = getReceiptUploadHeaderService().isReceiptDetailsExits(
						receiptUploadDetail.getReference(), RepayConstants.RECEIPTMODE_CHEQUE,
						receiptUploadDetail.getChequeNo(), receiptUploadDetail.getFavourNumber());
			} else {
				isreceiptdataExits = getReceiptUploadHeaderService().isReceiptDetailsExits(
						receiptUploadDetail.getReference(), RepayConstants.RECEIPTMODE_DD,
						receiptUploadDetail.getChequeNo(), receiptUploadDetail.getFavourNumber());
			}
			
		
			if (isreceiptdataExits) {
				isReceiptDetailsExits = true;
			}
		}

		logger.debug(Literal.LEAVING);
		//Second Sheet validation
		return doValidateAllocationDetails(receiptUploadDetail.getListAllocationDetails());
		
	}

	/**
	 * validate Allocation Details
	 * @param listAllocationDetails
	 * 
	 */
	private String doValidateAllocationDetails(List<UploadAlloctionDetail> listAllocationDetails) {
		logger.debug(Literal.ENTERING);
		
		String errorMsg = null;
		
		for (UploadAlloctionDetail uploadAlloctionDetail : listAllocationDetails) {
			
			
			if (StringUtils.isNotBlank(uploadAlloctionDetail.getReferenceCode())
					&& lengthEight < uploadAlloctionDetail.getReferenceCode().length()) {
				return Labels.getLabel("invalid_ReferenceCode_Length");
			}
			
			if(lengtheighteen < uploadAlloctionDetail.getPaidAmount().toString().length()){
				return Labels.getLabel("invalid_PaidAmount_Length");
			}
			
			if(lengtheighteen < uploadAlloctionDetail.getWaivedAmount().toString().length()){
				return Labels.getLabel("invalid_WaiverAmount_Length");
			}
		}
		
		logger.debug(Literal.LEAVING);
		return errorMsg;
	}

	/**
	 * validate each object with list and check dedub for loan reference with status,transaction ref,cheque or dd number
	 * 
	 * @param receiptUploadDetail
	 * @return dedup Check
	 */
	private boolean checkDedupCondition(ReceiptUploadDetail receiptUploadDetail) {
		
		for (int i = 0; i < uploadNewList.size(); i++) {
			
			if (StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_NEFT)
					|| StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_RTGS)
					|| StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_IMPS)) {
				if (uploadNewList.get(i).getReference().equals(receiptUploadDetail.getReference())
						&& uploadNewList.get(i).getReceiptMode().equals(receiptUploadDetail.getReceiptMode())
						&& uploadNewList.get(i).getTransactionRef().equals(receiptUploadDetail.getTransactionRef())) {
					return true;
				}
			} else if (StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
					|| StringUtils.equalsIgnoreCase(receiptUploadDetail.getReceiptMode(), DisbursementConstants.PAYMENT_TYPE_DD)) {
				if (uploadNewList.get(i).getReference().equals(receiptUploadDetail.getReference())
						&& uploadNewList.get(i).getReceiptMode().equals(receiptUploadDetail.getReceiptMode())
						&& uploadNewList.get(i).getBankCode().equals(receiptUploadDetail.getBankCode())
						&& uploadNewList.get(i).getFavourNumber().equals(receiptUploadDetail.getFavourNumber())) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Accepting single cell key and value and comparing with other sheet data
	 * 
	 * @param key
	 *            sheet key
	 * @param value
	 *            row value
	 * @param jsonForextendedField
	 * @param flag
	 * @finalRequestJson append prepared json to this.
	 */
	private boolean doCompare(String key, Object value, JSONObject finalRequestJson, JSONObject jsonForextendedField)
			throws Exception {
		logger.debug(Literal.ENTERING);

		Map<String, String> keyTypeMap = new HashMap<>();
		boolean isMappingFound = false;

		String sheetName = workbook.getSheetAt(1).getSheetName().replace("\"", "");
		List<String> KeyList = BatchProcessorUtil.getAllKeysByIndex(workbook, 1);

		// key is a array or object
		if (KeyList.contains(key)) {
			keyTypeMap.put(sheetName, BatchUploadProcessorConstatnt.A);
		} else {
			keyTypeMap.put(sheetName, BatchUploadProcessorConstatnt.NA);
		}

		if (KeyList.contains(key) && StringUtils.equals(BatchUploadProcessorConstatnt.ROOTKEY, key)) {
			int keyIndex = KeyList.indexOf(key);
			List<Map<String, Object>> allocationListedMap = getAllMappingRowsOfSheet(1, keyIndex, value);

			finalRequestJson.put(key, value);//adding root_id in json object

			if (!allocationListedMap.isEmpty()) {
				isMappingFound = true;
				prepareJsonForParent(sheetName, allocationListedMap, keyTypeMap.get(sheetName),
						finalRequestJson);
			}
		}
		logger.debug(Literal.LEAVING);
		return isMappingFound;
	}

	/**
	 * Prepare jsonObject for column <ROOT>_id
	 * @param key
	 * @param singleSheetMappedRows
	 * @param originalKey
	 * @param finalRequestJson
	 */
	private void prepareJsonForParent(String key, List<Map<String, Object>> singleSheetMappedRows, String originalKey,
			JSONObject finalRequestJson) {
		try {
			if (originalKey.equals(BatchUploadProcessorConstatnt.NA)) { // its a jsonObject
				finalRequestJson.put(key, listOfMapToJson(singleSheetMappedRows));
			} else {// its a jsonArray
				finalRequestJson.put(key, singleSheetMappedRows);
			}
		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
		}
	}

	/**
	 * util method convert list to json
	 * 
	 * @param prepairedLsitOfmap
	 *            mapped list
	 * @return JSONObject
	 */
	private List<JSONObject> listOfMapToJson(List<Map<String, Object>> prepairedLsitOfmap) {

		List<JSONObject> listJsonObject = new ArrayList<>();
		for (Map<String, Object> map : prepairedLsitOfmap) {
			JSONObject jsonObject = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					jsonObject.put(entry.getKey(), entry.getValue());

				} catch (Exception e) {
					logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
				}
			}
			listJsonObject.add(jsonObject);
		}
		return listJsonObject;
	}

	/**
	 * method will return list of all mapping values
	 * 
	 * @param sheetIndex
	 *            index of the sheet
	 * @param keyIndex
	 *            index of the key
	 * 
	 * @return List List of mapping values.
	 */
	public List<Map<String, Object>> getAllMappingRowsOfSheet(int sheetIndex, int keyIndex, Object value) {
		logger.debug(Literal.ENTERING);
		List<Map<String, Object>> allMappedRowsOfSheet = new ArrayList<>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Iterator<Row> rows = sheet.iterator();
		List<String> keyList = BatchProcessorUtil.getAllKeysByIndex(workbook,sheetIndex);
		while (rows.hasNext()) {
			Row row = rows.next();
			if (row != null && row.getRowNum() > 0) {

				Cell cell = row.getCell(0);
				if (cell == null) {
					continue;
				}

				int columnIndex = cell.getColumnIndex();
				if (columnIndex <= keyList.size()) {
					objFormulaEvaluator.evaluate(cell);
					String cellString = objDefaultFormat.formatCellValue(cell, objFormulaEvaluator).trim();
					if (cell.getColumnIndex() == keyIndex && cellString.equals(value.toString().trim())) {
						Map<String, Object> rowMap = new HashMap<>();
						for (int j = 0; j < keyList.size(); j++) {
							if (!keyList.get(j).toString().contains("_")) {

								//this will check null values and replace with eampty cell
								Cell cell1 = row.getCell(j);
								if (cell1 == null) {
									cell1 = row.createCell(j);
								}

								objFormulaEvaluator.evaluate(cell1);
								String cellValueStr = objDefaultFormat.formatCellValue(cell1, objFormulaEvaluator);
								rowMap.put(keyList.get(j), getValueByColumnType(cell1, cellValueStr));
							}
						}

						if (!rowMap.isEmpty() && rowMap.size() > 0) {
							allMappedRowsOfSheet.add(rowMap);
						}

					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return allMappedRowsOfSheet;
	}

	/** deciding cell type based on column format */
	public Object getValueByColumnType(Cell cell, String value) {
		Object result = null;
		if (value.equalsIgnoreCase(BatchUploadProcessorConstatnt.TRUE) || value.equalsIgnoreCase(BatchUploadProcessorConstatnt.FALSE)) {
			result = BatchProcessorUtil.boolFormater(value);
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
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
			} else if(!this.fileName.getValue().toString().matches("^[a-zA-Z0-9 ._]*$")){
				throw new WrongValueException(this.fileName,
						this.fileName.getValue() + ": file name should not contain special characters, Allowed special charaters are space,dot and underScore.");
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
				this.entity.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptUpload_entity.value"),
						null, true, true));
				this.receiptUploadHeader.setEntityCode(this.entity.getValue());
				this.receiptUploadHeader.setEntityCodeDesc(this.entity.getDescription());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		//set uploadprocess value to zero
		this.receiptUploadHeader.setUploadProgress(PennantConstants.RECEIPT_DEFAULT);

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
	private boolean validateUploadedFile() {
		logger.debug(Literal.ENTERING);

		if (this.fileImport == null) {
			logger.debug(Literal.LEAVING);
			return false;
		}

		//Reading excel data and returning as a workbook
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
			
			//If Uploaded Receipt record count > 1000
			if(sheet.getPhysicalNumberOfRows() > 1001){
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
					&& fsHeaderKeys.contains("RECEIPTMODE") && fsHeaderKeys.contains("FUNDINGAC")
					&& fsHeaderKeys.contains("PAYMENTREF") && fsHeaderKeys.contains("FAVOURNUMBER")
					&& fsHeaderKeys.contains("BANKCODE") && fsHeaderKeys.contains("CHEQUEACNO")
					&& fsHeaderKeys.contains("TRANSACTIONREF") && fsHeaderKeys.contains("STATUS")
					&& fsHeaderKeys.contains("DEPOSITDATE") && fsHeaderKeys.contains("REALIZATIONDATE")
					&& fsHeaderKeys.contains("INSTRUMENTDATE"))) {

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
