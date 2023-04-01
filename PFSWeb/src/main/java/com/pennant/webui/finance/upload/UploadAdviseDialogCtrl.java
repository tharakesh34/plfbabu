package com.pennant.webui.finance.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.pff.fee.AdviseType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/SystemMaster/UploadHeader/UploadAdviseDialog.zul file. <br>
 * ************************************************************<br>
 */
public class UploadAdviseDialogCtrl extends GFCBaseCtrl<UploadHeader> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(UploadAdviseDialogCtrl.class);

	protected Window window_AdviseUploadDialog;
	protected Groupbox gb_DownloadDetails;
	protected Groupbox gb_UploadDetails;

	protected Radiogroup radioButtons;
	protected Radio downLoad;
	protected Radio upload;

	// Download
	protected ExtendedCombobox fileName;
	protected Button btnDownload;
	protected ExtendedCombobox downloadEntity;
	protected Datebox dateOfUpload;

	// Upload
	protected Textbox txtFileName;
	protected ExtendedCombobox uploadEntity;
	private UploadHeader uploadHeader;
	protected Button btnBrowse;
	protected Space space_txtFileName;

	private transient UploadListCtrl uploadListCtrl;
	private transient boolean validationOn;
	private transient UploadHeaderService uploadHeaderService;
	private transient ReceiptUploadHeaderService receiptUploadHeaderService;
	private transient ManualAdviseService manualAdviseService;
	private transient FinanceMainService financeMainService;

	private static final String MODULE_NAME = "ManualAdvise";

	private Workbook workbook = null;
	private ExcelFileImport fileImport = null;
	private Media media = null;
	private boolean csvFile = false;
	private final int totalColumns = 6;
	private String filePath = "";
	HashSet<String> validations = null;
	private String module = "";

	/**
	 * default constructor.<br>
	 */
	public UploadAdviseDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AdviseUploadDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_AdviseUploadDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AdviseUploadDialog);
		this.module = UploadConstants.MODULE_MANUAL_ADVISE;
		try {
			// Get the required arguments.
			this.uploadHeader = (UploadHeader) arguments.get("uploadHeader");
			this.uploadListCtrl = (UploadListCtrl) arguments.get("uploadListCtrl");

			if (this.uploadHeader == null) {
				// Create a new entity.
				uploadHeader = uploadHeaderService.getUploadHeader();
				uploadHeader.setNewRecord(true);
				uploadHeader.setTransactionDate(SysParamUtil.getAppDate());
				uploadHeader.setMakerId(getUserWorkspace().getUserDetails().getUserId());
				uploadHeader.setModule(this.module);
			}

			// Store the before image.
			UploadHeader uploadHeader = new UploadHeader();
			BeanUtils.copyProperties(this.uploadHeader, uploadHeader);
			this.uploadHeader.setBefImage(uploadHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.uploadHeader.isWorkflow(), this.uploadHeader.getWorkflowId(),
					this.uploadHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction, false, false);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.uploadHeader);
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

		// Entity selection for Download
		this.downloadEntity.setModuleName("Entity");
		this.downloadEntity.setDisplayStyle(2);
		this.downloadEntity.setValueColumn("EntityCode");
		this.downloadEntity.setDescColumn("EntityDesc");
		this.downloadEntity.setValidateColumns(new String[] { "EntityCode" });

		// Entity selection for Upload
		this.uploadEntity.setModuleName("Entity");
		this.uploadEntity.setDisplayStyle(2);
		this.uploadEntity.setValueColumn("EntityCode");
		this.uploadEntity.setDescColumn("EntityDesc");
		this.uploadEntity.setValidateColumns(new String[] { "EntityCode" });
		this.uploadEntity.setMandatoryStyle(true);

		this.fileName.setModuleName("AdviseUpload");
		this.fileName.setMandatoryStyle(true);
		this.fileName.setDisplayStyle(2);
		this.fileName.setValueColumn("UploadId");
		this.fileName.setDescColumn("FileName");
		this.fileName.setValueType(DataType.LONG);
		this.fileName.setValidateColumns(new String[] { "UploadId" });

		this.txtFileName.setReadonly(true);

		this.dateOfUpload.setFormat(DateFormat.SHORT_DATE.getPattern());

		setStatusDetails();
		setFilePath();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get the default upload from System parameters
	 * 
	 * @return
	 */
	private void setFilePath() {
		filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		filePath = filePath.concat(File.separator).concat("AdviseUpload");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AdviseUploadDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AdviseUploadDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AdviseUploadDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AdviseUploadDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		closeDialg(true);
	}

	/**
	 * 
	 */
	private void closeDialg(boolean confirmationreq) {
		doClose(confirmationreq);
	}

	protected void doPostClose() {
		Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();
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

		this.txtFileName.setText("");
		this.fileImport = null;
		Clients.clearWrongValue(this.btnBrowse);

		doRemoveValidation();
		this.media = event.getMedia();

		String fileName = this.media.getName();

		try {
			if (!(StringUtils.endsWith(fileName.toLowerCase(), ".csv"))) {
				MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid csv file.");
				this.media = null;
				return;
			} else {
				String filePath = getFilePath();
				if (StringUtils.endsWith(fileName.toLowerCase(), ".csv")) {
					csvFile = true;
				} else {
					csvFile = false;
					this.fileImport = new ExcelFileImport(media, filePath);
				}
				this.txtFileName.setText(fileName);
			}
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Process" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDownload(Event event) {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		String fileName = "";
		String entityCode = "";
		ArrayList<WrongValueException> wve = new ArrayList<>();
		try {
			this.downloadEntity.getValidateColumns();
			entityCode = this.downloadEntity.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.trimToNull(this.fileName.getValue()) == null) {
				throw new WrongValueException(this.fileName, Labels.getLabel("empty_file"));
			}
			fileName = this.fileName.getDescription();

		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorMessage(wve);

		StringBuilder condition = new StringBuilder();
		condition.append("Where FILENAME in (" + "'" + fileName + "'" + ")");
		condition.append("and ENTITYCode in (" + "'" + entityCode + "'" + ")");
		if (this.dateOfUpload.getValue() != null) {
			String uploadDate = DateUtil.formatToFullDate(this.dateOfUpload.getValue());
			condition.append("and UploadedDate in (" + "'" + uploadDate + "'" + ")");
		}

		String whereCond = new String(condition);

		StringBuilder searchCriteriaDesc = new StringBuilder(" ");
		searchCriteriaDesc.append("File Name is " + uploadHeader.getFileName());

		// Excel file downloading automatically using Jasper Report
		try {
			ReportsUtil.generateReport(getUserWorkspace().getLoggedInUser().getFullName(), "ManualAdviseUploadReport",
					whereCond, searchCriteriaDesc);
		} catch (Exception e) {
			MessageUtil.showError(e);
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private String getFilePath() {
		String filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		filePath = filePath.concat(File.separator).concat("AdviseUpload");
		return filePath;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.uploadHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param uploadHeader
	 * 
	 */
	public void doWriteBeanToComponents(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		this.uploadEntity.setValue(uploadHeader.getEntityCode());
		this.txtFileName.setValue(uploadHeader.getFileName());

		if (!uploadHeader.isNewRecord() && CollectionUtils.isNotEmpty(uploadHeader.getUploadManualAdvises())) {
			for (UploadManualAdvise adviseUpload : uploadHeader.getUploadManualAdvises()) {
				UploadManualAdvise befImage = new UploadManualAdvise();
				BeanUtils.copyProperties(adviseUpload, befImage);
				adviseUpload.setBefImage(befImage);
			}
		}

		this.recordStatus.setValue(uploadHeader.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aUploadHeader
	 */
	public boolean doWriteComponentsToBean(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();
		validations = new HashSet<String>();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			uploadHeader.setEntityCode(this.uploadEntity.getValue());
			uploadHeader.setEntityDesc(this.uploadEntity.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			uploadHeader.setFileName(this.txtFileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		try {
			getUploadedAdvises(uploadHeader);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
			return false;
		}

		uploadHeader.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
		return true;
	}

	private void getUploadedAdvises(UploadHeader uploadHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		if (this.csvFile) {
			// CSV File
			processCSVUploadDetails(uploadHeader);
		} else if (this.fileImport != null) {

			this.workbook = this.fileImport.writeFile();

			if (this.workbook != null) {
				List<String> keys = this.fileImport.getRowValuesByIndex(this.workbook, 0, 0, totalColumns);

				if (CollectionUtils.isEmpty(keys) || !("Loan Reference".equalsIgnoreCase(keys.get(0))
						&& "Advise Type".equalsIgnoreCase(keys.get(1)) && "Fee Type".equalsIgnoreCase(keys.get(2))
						&& "Value Date".equalsIgnoreCase(keys.get(3)) && "Advise amount".equalsIgnoreCase(keys.get(4))
						&& "Remarks".equalsIgnoreCase(keys.get(5)))) {
					throw new InterfaceException("Error",
							"The uploaded file format is invalid, please upload valid file");
				}

				Sheet sheet = this.workbook.getSheetAt(0);
				int noOfRows = sheet.getPhysicalNumberOfRows();

				if (noOfRows == 0 || noOfRows == 1) {
					throw new InterfaceException("Error", "File is empty.");
				} else if (noOfRows <= 25001) {
					// Process the records
					processExcelUploadDetails(uploadHeader);
					// Back up File
					this.fileImport.backUpFile();
				} else {
					throw new InterfaceException("Error", "File should not contain more than 25000 records.");
				}
			}
		} else {
			throw new AppException("The uploaded file could not be recognized. Please upload a valid file.");
		}

		logger.debug(Literal.LEAVING);
	}

	private void processCSVUploadDetails(UploadHeader uploadHeader) throws IOException, Exception {
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

		try {
			if (media.isBinary()) {
				FileUtils.writeByteArrayToFile(file, this.media.getByteData());
			} else {
				FileUtils.writeStringToFile(file, this.media.getStringData());
			}
			br = new BufferedReader(new FileReader(file));
			int count = 0;
			List<UploadManualAdvise> manualadvises = new ArrayList<UploadManualAdvise>();
			int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

			while ((line = br.readLine()) != null) {
				List<String> row = Arrays.asList(line.split(cvsSplitBy, totalColumns));

				if (row.size() >= totalColumns) {
					if (count == 0) { // Skip Header row
						if (CollectionUtils.isEmpty(row) || !("Loan Reference".equalsIgnoreCase(row.get(0))
								&& "Advise Type".equalsIgnoreCase(row.get(1)) && "Fee Type".equalsIgnoreCase(row.get(2))
								&& "Value Date".equalsIgnoreCase(row.get(3))
								&& "Advise amount".equalsIgnoreCase(row.get(4))
								&& "Remarks".equalsIgnoreCase(row.get(5)))) {
							throw new InterfaceException("Error",
									"The uploaded file could not be processed.Please upload a valid xls or xlsx file.");
						}
					} else {
						manualadvises.add(validateUploadDetails(row, formatter));
					}
				} else {
					// Failure Case
					UploadManualAdvise adviseUpload = new UploadManualAdvise();
					adviseUpload.setNewRecord(true);
					adviseUpload.setRecordType(PennantConstants.RCD_ADD);
					adviseUpload.setVersion(adviseUpload.getVersion() + 1);
					adviseUpload.setStatus("F");
					adviseUpload.setReason("Number of columns are not matching.");
					manualadvises.add(adviseUpload);
				}

				count++;
			}
			uploadHeader.setUploadManualAdvises(manualadvises);
			uploadHeader.setTotalRecords(count - 1);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
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
	}

	/**
	 * 
	 * @param row
	 * @param formatter
	 * @return
	 * @throws ParseException
	 */
	public UploadManualAdvise validateUploadDetails(List<String> row, int formatter) throws ParseException {
		logger.debug(Literal.ENTERING);

		boolean error = false;
		StringBuilder reason = new StringBuilder();

		Date valueDate = null;
		UploadManualAdvise adviseUpload = new UploadManualAdvise();
		FinanceMain fm = null;
		// Reference
		String finReference = StringUtils.trimToEmpty(row.get(0));
		if (StringUtils.isBlank(finReference)) {
			reason.append("Loan Reference is mandatory.");
			error = true;
		} else if (StringUtils.isNotBlank(finReference)) {
			if (finReference.length() > 20) {
				reason.append(" Loan Reference length is exceeded, it should be lessthan or equal to 20.");
				error = true;
				finReference = null;
			} else {
				fm = financeMainService.getFinanceMainForAdviseUpload(finReference);
				if (fm == null) {
					reason.append("Loan Reference doesn't exist.");
					error = true;
				} else {
					adviseUpload.setFinID(fm.getFinID());
				}
			}
		}

		adviseUpload.setFinReference(finReference);

		// Advise Type
		String type = row.get(1);
		if (StringUtils.isBlank(type)) {
			reason.append("Advise Type is mandatory.");
			error = true;
		} else {
			if (type.length() > 1) {
				reason.append("Advise type length is exceeded.");
				type = null;
			} else if (!UploadConstants.UPLOAD_PAYABLE_ADVISE.equals(type)
					&& !UploadConstants.UPLOAD_RECEIVABLE_ADVISE.equals(type)) {
				reason.append("Advise Type should be either");
				reason.append(UploadConstants.UPLOAD_PAYABLE_ADVISE);
				reason.append(" or ");
				reason.append(UploadConstants.UPLOAD_RECEIVABLE_ADVISE);
				error = true;
			}
		}
		adviseUpload.setAdviseType(type);

		// Fee Type
		String feeType = row.get(2);
		FeeType fee = uploadHeaderService.getApprovedFeeTypeByFeeCode(feeType);
		if (StringUtils.isBlank(feeType)) {
			reason.append("Fee Type is mandatory.");
			error = true;
		} else {
			if (feeType.length() > 8) {
				reason.append("Fee type length is exceeded.");
				error = true;
				feeType = feeType.substring(0, 8);
			} else {
				if (fee == null) {
					reason.append("Fee type doesn't exist.");
					error = true;
				} else {
					String adviseType = "";
					if (AdviseType.isReceivable(fee.getAdviseType())) {
						adviseType = UploadConstants.UPLOAD_RECEIVABLE_ADVISE;
					} else if (AdviseType.isPayable(fee.getAdviseType())) {
						adviseType = UploadConstants.UPLOAD_PAYABLE_ADVISE;
					}
					if (!StringUtils.equals(adviseType, type)) {
						reason.append("Fee Type with the given advise type doesn't exist.");
						error = true;
					}
					if (!fee.isManualAdvice()) {
						reason.append("manual advice not enable in the given fee types.");
						error = true;
					}
				}
			}
		}
		adviseUpload.setFeeTypeCode(feeType);

		// Value Date
		try {
			if (StringUtils.isBlank(row.get(3))) {
				reason.append("Value Date is mandatory.");
				error = true;
			} else {
				valueDate = getUtilDate(row.get(3));
				if (valueDate != null && fm != null) {
					if (valueDate.compareTo(fm.getFinStartDate()) < 0) {
						reason.append("Value Date should be greater than Finance Start Date.");
						error = true;
					} else if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE
							&& valueDate.compareTo(fm.getMaturityDate()) >= 0) {
						reason.append("Value Date should be less than or equal to Loan Maturity Date.");
						error = true;
					}
				}
			}
		} catch (Exception e) {
			reason.append("Value date format is incorrect.It's format should be dd/MM/yyyy.");
			error = true;
		}
		adviseUpload.setValueDate(valueDate);

		// Advise Amount
		String manualAdviseAmount = row.get(4);
		BigDecimal advise = null;
		if (StringUtils.isBlank(manualAdviseAmount)) {
			reason.append("Advise Amount is mandatory.");
			error = true;
		} else {
			try {
				advise = new BigDecimal(manualAdviseAmount);

				if (advise.compareTo(BigDecimal.ZERO) < 1) {
					throw new Exception("Advise amount should be greater than ZERO.");
				}
			} catch (NumberFormatException e) {
				reason.append("Advise Amount is invalid.");
				error = true;
			} catch (Exception e) {
				reason.append(e.getMessage());
				error = true;
			}

		}
		if (advise != null) {
			advise = PennantApplicationUtil.unFormateAmount(advise, 2);
		}
		adviseUpload.setAdviseAmount(advise);

		if (UploadConstants.UPLOAD_PAYABLE_ADVISE.equals(type)) {

			// eligibility amount validation
			ManualAdvise ma = new ManualAdvise();

			ma.setFinReference(finReference);
			ma.setFinID(fm.getFinID());
			ma.setValueDate(valueDate);

			BigDecimal eblAmount = manualAdviseService.getEligibleAmount(ma, fee);

			if (advise.compareTo(eblAmount) > 0) {
				reason.append("Advise Amount should be less than or equal to Eligible Amount.");
				error = true;
			}
		}

		// Remarks
		String remarks = row.get(5);
		if (StringUtils.isNotBlank(remarks)) {
			if (remarks.length() > 100) {
				reason.append(" Remarks length is exceeded.");
				error = true;
				remarks = null;
			}
		}
		adviseUpload.setRemarks(remarks);

		if (!error) {
			String key = finReference + type + feeType;

			if (validations.contains(key)) {
				reason.append(
						"Loan Reference is duplicated in the upload file with the same advise type and fee type.");
				error = true;
			} else {
				validations.add(key);
			}

			List<String> finEvents = uploadHeaderService.getFinEventByFinRef(finReference, "_Temp");

			if (CollectionUtils.isNotEmpty(finEvents)) {
				if (finEvents.contains(FinServiceEvent.ADDDISB) || finEvents.contains(FinServiceEvent.RATECHG)
						|| finEvents.contains(FinServiceEvent.EARLYRPY)) {
					reason = new StringBuilder(Labels.getLabel("LOAN_SERVICE_PROCESS"));
					error = true;
				} else if (finEvents.contains(FinServiceEvent.CANCELFIN)) {
					reason = new StringBuilder(Labels.getLabel("LOAN_CANCEL_PROCESS"));
					error = true;
				}
			}

			if (manualAdviseService.isManualAdviseExist(fm.getFinID())) {
				reason.append(Labels.getLabel("Finance_Inprogresss_ManualAdvise"));
				error = true;
			}

			if (manualAdviseService.isAdviseUploadExist(fm.getFinID())) {
				reason.append("Not allowed to maintain the LAN as It is already initiated for Manual Advise.");
				error = true;
			}

			String rcdMntnSts = financeMainService.getFinanceMainByRcdMaintenance(fm.getFinID());
			if (StringUtils.isNotEmpty(rcdMntnSts)) {
				reason.append(Labels.getLabel("Finance_Inprogresss_" + rcdMntnSts));
				error = true;
			}
		}

		adviseUpload.setNewRecord(true);
		adviseUpload.setRecordType(PennantConstants.RCD_ADD);
		adviseUpload.setVersion(adviseUpload.getVersion() + 1);

		if (error) {
			adviseUpload.setReason(reason.toString());
			adviseUpload.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
			adviseUpload.setRejectStage(UploadConstants.UPLOAD_MAKER_STAGE);
		} else {
			adviseUpload.setStatus(UploadConstants.UPLOAD_STATUS_SUCCESS);
		}

		logger.debug(Literal.LEAVING);
		return adviseUpload;
	}

	private Date getUtilDate(String date) throws ParseException {

		Date uDate = null;
		SimpleDateFormat df = new SimpleDateFormat(DateFormat.SHORT_DATE.getPattern());

		try {
			if (StringUtils.isBlank(date)) {
				return uDate;
			}

			String[] dateformat = date.split("/");

			if (dateformat.length != 3) {
				throw new ParseException(null, 0);
			}

			String dateValue = dateformat[0];
			String month = dateformat[1];
			String year = dateformat[2];

			boolean leapYear = false;

			if (StringUtils.isBlank(dateValue) || StringUtils.isBlank(month) || StringUtils.isBlank(year)) {
				throw new ParseException(null, 0);
			}

			int dateVal = Integer.parseInt(dateValue);

			if (year.length() == 4) {
				int yearValue = Integer.parseInt(year);
				int rem = yearValue % 4;
				if (rem == 0) {
					leapYear = true;
				}
			} else {
				throw new ParseException(null, 0);
			}

			switch (month.toUpperCase()) {
			case "01":
			case "03":
			case "05":
			case "07":
			case "08":
			case "10":
			case "12":
				if (dateVal > 31) {
					throw new ParseException(null, 0);
				}
				break;

			case "02":
				if (leapYear) {
					if (dateVal > 29) {
						throw new ParseException(null, 0);
					}
				} else {
					if (dateVal > 28) {
						throw new ParseException(null, 0);
					}
				}
				break;

			case "04":
			case "06":
			case "09":
			case "11":
				if (dateVal > 30) {
					throw new ParseException(null, 0);
				}
				break;

			default:
				throw new ParseException(null, 0);
			}

			uDate = df.parse(date);

		} catch (ParseException e) {
			throw e;
		}

		return uDate;
	}

	/**
	 * entry point of program, reading whole excel and calling other methods to prepare jsonObject.
	 * 
	 * @return String
	 * @throws Exception
	 */
	private void processExcelUploadDetails(UploadHeader uploadHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		List<UploadManualAdvise> adviseUploads = new ArrayList<UploadManualAdvise>();
		Sheet sheet = this.workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

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
						adviseUploads.add(validateUploadDetails(columns, formatter));
					} else {
						UploadManualAdvise adviseUpload = new UploadManualAdvise();
						adviseUpload.setNewRecord(true);
						adviseUpload.setRecordType(PennantConstants.RCD_ADD);
						adviseUpload.setVersion(adviseUpload.getVersion() + 1);
						adviseUpload.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
						adviseUpload.setReason("Number of columns are not matching.");
						adviseUploads.add(adviseUpload);
					}
				}
			}
		}
		uploadHeader.setTotalRecords(sheet.getPhysicalNumberOfRows() - 1);
		uploadHeader.setUploadManualAdvises(adviseUploads);

		logger.debug(Literal.LEAVING);
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

	/**
	 * Displays the dialog page.
	 * 
	 * @param aUploadHeader The entity that need to be render.
	 */
	public void doShowDialog(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (uploadHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(uploadHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			if (uploadListCtrl != null) {
				uploadListCtrl.getClass().getMethod("setUploadAdviseDialogCtrl", this.getClass()).invoke(uploadListCtrl,
						this);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		doCheckFields();

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// fill the components with the data
		doWriteBeanToComponents(uploadHeader);

		this.btnDelete.setVisible(false); // we are not providing delete option
		setDialog(DialogType.EMBEDDED);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(true);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.uploadEntity.isReadonly()) {
				this.uploadEntity.setConstraint(new PTStringValidator(
						Labels.getLabel("label_UploadAdviseDialog_Entity.value"), null, true, true));
				this.uploadEntity.setValue(this.uploadEntity.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.downloadEntity.isReadonly()) {
				this.downloadEntity.setConstraint(new PTStringValidator(
						Labels.getLabel("label_DownloadAdviseDialog_Entity.value"), null, true, true));
				this.downloadEntity.setValue(this.downloadEntity.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.fileName.isReadonly()) {
				this.fileName.setConstraint(new PTStringValidator(
						Labels.getLabel("label_DownloadAdviseDialog_FileName.value"), null, true, true));
				this.fileName.setValue(this.fileName.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.uploadEntity.isReadonly()) {
				if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
					throw new WrongValueException(this.btnBrowse, Labels.getLabel("empty_file"));
				} else {
					boolean fileExist = this.uploadHeaderService.isFileNameExist(this.txtFileName.getValue());
					if (fileExist) {
						throw new WrongValueException(this.txtFileName,
								this.txtFileName.getValue() + Labels.getLabel("label_File_Exits"));
					}
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

	public void onFulfill$downloadEntity(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		doAddFilter();

		logger.debug(Literal.LEAVING);
	}

	public void doAddFilter() {
		logger.debug(Literal.ENTERING);
		String entity = this.downloadEntity.getValue();
		String uploadDate = "";
		if (this.dateOfUpload.getValue() != null) {
			uploadDate = DateUtil.format(this.dateOfUpload.getValue(), PennantConstants.DBDateFormat);
		}
		Filter[] filters = new Filter[1];

		if (StringUtils.isNotEmpty(uploadDate) && StringUtils.isNotBlank(entity)) {
			filters = new Filter[4];
			filters[1] = new Filter("EntityCode", entity, Filter.OP_EQUAL);
			filters[2] = new Filter("TransactionDate", uploadDate, Filter.OP_GREATER_OR_EQUAL);
			filters[3] = new Filter("TransactionDate", uploadDate, Filter.OP_LESS_OR_EQUAL);

		} else if (StringUtils.isNotBlank(entity)) {
			filters = new Filter[2];
			filters[1] = new Filter("EntityCode", entity, Filter.OP_EQUAL);
		} else if (StringUtils.isNotEmpty(uploadDate)) {
			filters = new Filter[3];
			filters[1] = new Filter("TransactionDate", uploadDate, Filter.OP_GREATER_OR_EQUAL);
			filters[2] = new Filter("TransactionDate", uploadDate, Filter.OP_LESS_OR_EQUAL);
		}
		filters[0] = new Filter("Module", MODULE_NAME, Filter.OP_EQUAL);

		this.fileName.setValue("");
		this.fileName.setFilters(filters);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.txtFileName.setConstraint("");
		this.txtFileName.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.btnBrowse);
		this.txtFileName.setErrorMessage("");
		this.uploadEntity.setConstraint("");
		this.uploadEntity.setErrorMessage("");
		this.fileName.setConstraint("");
		this.fileName.setErrorMessage("");
		this.downloadEntity.setConstraint("");
		this.downloadEntity.setErrorMessage("");
		this.fileImport = null;

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final UploadHeader entity = new UploadHeader();
		BeanUtils.copyProperties(this.uploadHeader, entity);
		String keyReference = Labels.getLabel("label_UploadAdviseDialog_FileName.value") + " : "
				+ uploadHeader.getFileName();

		doDelete(keyReference, uploadHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.uploadHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(isReadOnly("button_AdviseUploadDialog_btnBrowse"), this.btnDownload);
			this.btnBrowse.setVisible(true);
			readOnlyComponent(isReadOnly("AdviseUploadDialog_uploadEntity"), this.uploadEntity);
		} else {
			this.btnBrowse.setVisible(false);
			this.btnDownload.setVisible(true);
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.uploadEntity);
		}

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
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.txtFileName);
		readOnlyComponent(true, this.uploadEntity);
		readOnlyComponent(true, this.btnBrowse);
		readOnlyComponent(true, this.btnDownload);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.uploadEntity.setValue("");
		this.txtFileName.setValue("");
		this.downloadEntity.setValue("");
		this.dateOfUpload.setValue(null);
		this.fileName.setValue("");

		logger.debug(Literal.LEAVING);
	}

	public void doCheckFields() {
		logger.debug(Literal.ENTERING);
		boolean isUpload = this.upload.isChecked();

		// Set focus point
		if (isUpload) {
			this.uploadEntity.focus();
		} else {
			this.downloadEntity.focus();
		}

		this.uploadEntity.setReadonly(!isUpload);
		this.btnBrowse.setDisabled(!isUpload);
		this.downloadEntity.setMandatoryStyle(!isUpload);
		this.fileName.setMandatoryStyle(!isUpload);

		this.dateOfUpload.setDisabled(isUpload);
		this.downloadEntity.setReadonly(isUpload);
		this.btnDownload.setDisabled(isUpload);
		this.fileName.setReadonly(isUpload);
		this.uploadEntity.setMandatoryStyle(isUpload);
		this.space_txtFileName.setSclass(isUpload ? PennantConstants.mandateSclass : "");

		logger.debug("Leaving ");
	}

	public void onCheck$downLoad(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		doCheckFields();

		/*
		 * this.downloadEntity.setValue(this.uploadEntity.getValue(), this.uploadEntity.getDescription());
		 * this.fileName.setValue(this.txtFileName.getValue()); this.dateOfUpload.setValue(null);
		 */
		this.uploadEntity.setValue("");
		this.txtFileName.setValue("");
		logger.debug("Leaving ");
	}

	public void onCheck$upload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		doCheckFields();

		this.downloadEntity.setValue("");
		this.dateOfUpload.setValue(null);
		this.fileName.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final UploadHeader aUploadHeader = new UploadHeader();
		BeanUtils.copyProperties(this.uploadHeader, aUploadHeader);
		boolean isNew = false;
		doSetValidation();

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		// fill the UploadHeader object with the components data
		if (!doWriteComponentsToBean(aUploadHeader)) {
			return;
		}

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aUploadHeader.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aUploadHeader.getRecordType())) {
				aUploadHeader.setVersion(aUploadHeader.getVersion() + 1);
				if (isNew) {
					aUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aUploadHeader.setNewRecord(true);
				}
			}
		} else {
			aUploadHeader.setVersion(aUploadHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aUploadHeader, tranType)) {
				closeDialg(false);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aUploadHeader (UploadHeader)
	 * 
	 * @param tranType      (String)
	 * 
	 * @return boolean
	 * 
	 */
	public boolean doProcess(UploadHeader aUploadHeader, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aUploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aUploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aUploadHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aUploadHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aUploadHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aUploadHeader);
				}

				if (isNotesMandatory(taskId, aUploadHeader)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aUploadHeader.setTaskId(taskId);
			aUploadHeader.setNextTaskId(nextTaskId);
			aUploadHeader.setRoleCode(getRole());
			aUploadHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aUploadHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aUploadHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aUploadHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aUploadHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		UploadHeader aUploadHeader = (UploadHeader) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = uploadHeaderService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = uploadHeaderService.saveOrUpdate(aAuditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

					// if (!uploadHeader.isFileDownload()
					// &&
					// !aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL))
					// {
					// throw new InterfaceException("Error", "File should be
					// downloaded at least once.");
					// }

					aAuditHeader = uploadHeaderService.doApprove(aAuditHeader);
					if (aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = uploadHeaderService.doReject(aAuditHeader);

					if (aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AdviseUploadDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_AdviseUploadDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.uploadHeader), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);

		return processCompleted;
	}

	private void showErrorMessage(ArrayList<WrongValueException> wve) {
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aUploadHeader
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(UploadHeader aUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aUploadHeader.getBefImage(), aUploadHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aUploadHeader.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.uploadHeader);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		if (uploadListCtrl != null) {
			uploadListCtrl.search();
		}
	}

	@Override
	protected String getReference() {
		return this.uploadHeader.getFileName();
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}

	public ReceiptUploadHeaderService getReceiptUploadHeaderService() {
		return receiptUploadHeaderService;
	}

	public void setReceiptUploadHeaderService(ReceiptUploadHeaderService receiptUploadHeaderService) {
		this.receiptUploadHeaderService = receiptUploadHeaderService;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}
}
