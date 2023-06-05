package com.pennant.webui.finance.feewaiverupload;

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
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FeeWaiverUpload;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.service.finance.FeeWaiverUploadHeaderService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/SystemMaster/UploadHeader/FeeWaiverUploadDialog.zul file. <br>
 * ************************************************************<br>
 */

public class FeeWaiverUploadDialogCtrl extends GFCBaseCtrl<UploadHeader> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(FeeWaiverUploadDialogCtrl.class);

	protected Window window_FeeWaiverUploadDialog;
	protected Groupbox gb_DownloadDetails;
	protected Groupbox gb_UploadDetails;

	protected Radiogroup radioButtons;
	protected Radio downLoad;
	protected Radio upload;

	// Download
	protected ExtendedCombobox fileName;
	protected Button btnDownload;
	protected Datebox dateOfUpload;

	// Upload
	protected Textbox txtFileName;
	private FeeWaiverUploadHeader uploadHeader;
	protected Button btnBrowse;
	protected Space space_txtFileName;

	private Workbook workbook = null;
	private final int totalColumns = 5;
	private Media media = null;
	private boolean csvFile = false;
	private String filePath = "";
	private ExcelFileImport fileImport = null;
	private String module = "";
	HashSet<String> validations = null;

	private transient FeeWaiverUploadListCtrl feeWaiverUploadListCtrl;
	private transient FinanceMainService financeMainService;
	private transient FeeWaiverUploadHeaderService feeWaiverUploadHeaderService;
	private transient FeeWaiverHeaderService feeWaiverHeaderService;

	/**
	 * default constructor.<br>
	 */
	public FeeWaiverUploadDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "WaiverUploadDialog";
	}

	public void onCreate$window_FeeWaiverUploadDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_FeeWaiverUploadDialog);
		this.module = UploadConstants.MODULE_FEE_WAIVER;
		try {
			// Get the required arguments.
			this.uploadHeader = (FeeWaiverUploadHeader) arguments.get("feeWaiverUploadHeader");
			this.feeWaiverUploadListCtrl = (FeeWaiverUploadListCtrl) arguments.get("feeWaiverUploadListCtrl");

			if (this.uploadHeader == null) {
				// Create a new entity.
				uploadHeader = feeWaiverUploadHeaderService.getUploadHeader();
				uploadHeader.setNewRecord(true);
				uploadHeader.setTransactionDate(SysParamUtil.getAppDate());
				uploadHeader.setMakerId(getUserWorkspace().getUserDetails().getUserId());
				uploadHeader.setModule(this.module);
			}

			// Store the before image.
			FeeWaiverUploadHeader uploadHeader = new FeeWaiverUploadHeader();
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

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.fileName.setModuleName("WaiverUpload");
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

	public void onClick$btnSave(Event event) {
		doSave();
	}

	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	public void onClick$btnClose(Event event) {
		closeDialog(true);
	}

	public void onCheck$downLoad(Event event) {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		doCheckFields();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(this.btnBrowse);
		this.txtFileName.setErrorMessage("");
		this.fileName.setConstraint("");
		this.fileName.setErrorMessage("");
		this.fileImport = null;

		logger.debug(Literal.LEAVING);
	}

	public void doCheckFields() {
		logger.debug(Literal.ENTERING);
		boolean isUpload = this.upload.isChecked();

		this.btnBrowse.setDisabled(!isUpload);
		this.fileName.setMandatoryStyle(!isUpload);

		this.dateOfUpload.setDisabled(isUpload);
		this.btnDownload.setDisabled(isUpload);
		this.fileName.setReadonly(isUpload);
		this.space_txtFileName.setSclass(isUpload ? PennantConstants.mandateSclass : "");

		logger.debug(Literal.LEAVING);
	}

	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.txtFileName);
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

	public void onCheck$upload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		this.dateOfUpload.setValue(null);
		this.fileName.setValue("");

		doClearMessage();
		doCheckFields();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WaiverUploadDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WaiverUploadDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_WaiverUploadDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WaiverUploadDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDownload(Event event) {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		String fileName = "";
		List<WrongValueException> wve = new ArrayList<>();

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
		if (this.dateOfUpload.getValue() != null) {
			String uploadDate = DateUtil.formatToFullDate(this.dateOfUpload.getValue()).toString();
			condition.append("and UploadedDate in (" + "'" + uploadDate + "'" + ")");
		}

		String whereCond = new String(condition);

		StringBuilder searchCriteriaDesc = new StringBuilder(" ");
		searchCriteriaDesc.append("File Name is " + uploadHeader.getFileName());

		String usrName = getUserWorkspace().getLoggedInUser().getFullName();

		ReportsUtil.generateReport(usrName, "BulkFeeWaiverUploadReport", whereCond, searchCriteriaDesc);

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
				// throw new WrongValueException(this.btnBrowse,
				// Labels.getLabel("empty_file"));
			} else {
				boolean fileExist = this.feeWaiverUploadHeaderService.isFileNameExist(this.txtFileName.getValue());
				if (fileExist) {
					throw new WrongValueException(this.txtFileName,
							this.txtFileName.getValue() + Labels.getLabel("label_File_Exits"));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorMessage(wve);

		logger.debug(Literal.LEAVING);

	}

	private void showErrorMessage(List<WrongValueException> wve) {
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	public void onUpload$btnBrowse(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setText("");
		this.fileImport = null;

		doRemoveValidation();
		this.media = event.getMedia();

		String fileName = this.media.getName();

		try {
			if (!(StringUtils.endsWith(fileName.toLowerCase(), ".xlsx"))) {
				MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid xlsx file.");
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

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setConstraint("");
		this.txtFileName.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private String getFilePath() {
		String filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		filePath = filePath.concat(File.separator).concat("FeeWaiverUpload");
		return filePath;
	}

	private void setFilePath() {
		filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		filePath = filePath.concat(File.separator).concat("FeeWaiverUpload");
	}

	public FeeWaiverUpload validateUploadDetails(List<String> row, int formatter) {
		FeeWaiverUpload waiverUpload = new FeeWaiverUpload();

		String reason = validate(row, waiverUpload);

		if (reason != null) {
			waiverUpload.setReason(reason);
			waiverUpload.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
			waiverUpload.setRejectStage(UploadConstants.UPLOAD_MAKER_STAGE);
		} else {
			waiverUpload.setNewRecord(true);
			waiverUpload.setRecordType(PennantConstants.RCD_ADD);
			waiverUpload.setVersion(waiverUpload.getVersion() + 1);
			waiverUpload.setStatus(UploadConstants.UPLOAD_STATUS_SUCCESS);
		}

		return waiverUpload;
	}

	private String validate(List<String> row, FeeWaiverUpload waiverUpload) {
		logger.debug(Literal.ENTERING);

		String finReference = row.get(0);

		if (StringUtils.isBlank(finReference)) {
			throw new AppException("Loan Reference is Mandatory. ");
		}

		if (finReference.length() > 20) {
			throw new AppException(" Loan Reference length is exceeded, it should be lessthan or equal to 20. ");
		}

		Long finID = financeMainService.getFinID(finReference);
		FinanceMain fm = financeMainService.getFinanceMain(finID, new String[] { "FinIsActive, FinStartDate" }, "");

		if (fm == null) {
			throw new AppException(" Incorrect LAN Reference Captured ");
		}

		if (!fm.isFinIsActive()) {
			throw new AppException(" LAN Reference is Inactive ");
		}

		waiverUpload.setFinReference(finReference);

		String feeType = row.get(1);
		if (StringUtils.isBlank(feeType)) {
			return " Fee Type is mandatory. ";
		}

		if (feeType.length() > 8) {
			return " Fee type length is exceeded. ";
		}

		FeeType fee = feeWaiverUploadHeaderService.getApprovedFeeTypeByFeeCode(feeType);
		if (fee == null) {
			return " Incorrect / Invalid Fee Type code - Please do check again ";
		}

		waiverUpload.setFeeTypeCode(feeType);

		try {
			if (StringUtils.isBlank(row.get(2))) {
				return " Value Date is mandatory. ";
			}

			Date appDate = SysParamUtil.getAppDate();
			Date valueDate = getUtilDate(row.get(2));
			if (DateUtil.compare(valueDate, fm.getFinStartDate()) < 0 || DateUtil.compare(valueDate, appDate) > 0) {
				return " Value date should be greater than Loan Start date & less than Current PLF Application date ";
			}

			waiverUpload.setValueDate(valueDate);
		} catch (Exception e) {
			return " Value date format is incorrect.It's format should be dd/MM/yyyy. ";
		}

		String waivedAmount = row.get(3);

		FeeWaiverHeader fwh = new FeeWaiverHeader();

		fwh.setNewRecord(true);
		fwh.setFinID(finID);
		fwh.setFinReference(waiverUpload.getFinReference());

		fwh = feeWaiverHeaderService.getFeeWaiverByFinRef(fwh);

		if (StringUtils.isBlank(waivedAmount)) {
			return " Waiver Amount is Mandatory ";
		}

		BigDecimal amount = BigDecimal.ZERO;
		try {
			amount = new BigDecimal(waivedAmount);
			if (amount.compareTo(BigDecimal.ZERO) <= 0) {
				throw new AppException("Waiver Amount should be greater than 0");
			}

		} catch (NumberFormatException e) {
			return " Waiver Amount is Invalid ";
		} catch (Exception e) {
			return e.getMessage();
		}

		boolean feeExistsforLan = false;
		for (FeeWaiverDetail waiverdetail : fwh.getFeeWaiverDetails()) {
			if (waiverdetail.getFeeTypeCode().equals(waiverUpload.getFeeTypeCode())) {
				feeExistsforLan = true;
				BigDecimal remainingFee = PennantApplicationUtil.formateAmount(
						waiverdetail.getReceivableAmount().subtract(waiverdetail.getReceivedAmount()),
						PennantConstants.defaultCCYDecPos);
				if (amount.compareTo(remainingFee) > 0) {
					return " Incorrect waiver amount provided against Fee Code + LAN Reference Combination ";
				}
			}
		}

		if (!feeExistsforLan) {
			return " Incorrect Fee Code provided against LAN Reference";
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			amount = PennantApplicationUtil.unFormateAmount(amount, 2);
		}

		waiverUpload.setWaivedAmount(amount);

		String remarks = row.get(4);
		if (StringUtils.isNotBlank(remarks) && remarks.length() > 100) {
			return " Remarks length is exceeded. ";
		}

		waiverUpload.setRemarks(remarks);

		String key = finReference + feeType;
		if (validations.contains(key)) {
			return " Loan Reference is duplicated in the upload file with the same fee type. ";
		} else {
			validations.add(key);
		}

		String reference = fwh.getFinReference();
		finID = fwh.getFinID();
		if (!fwh.isAlwtoProceed()) {
			return Labels.getLabel("Recipt_Is_In_Process") + reference;
		}

		String rcdMaintainSts = feeWaiverUploadHeaderService.getFinanceMainByRcdMaintenance(finID);
		if (StringUtils.isNotEmpty(rcdMaintainSts)) {
			return Labels.getLabel("Finance_Inprogresss_" + rcdMaintainSts);
		}

		logger.debug(Literal.LEAVING);
		return null;
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

	public void doShowDialog(FeeWaiverUploadHeader uploadHeader) {
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
			if (feeWaiverUploadListCtrl != null) {
				feeWaiverUploadListCtrl.getClass().getMethod("setUploadFeeWaiverDialogCtrl", this.getClass())
						.invoke(feeWaiverUploadListCtrl, this);
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

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final FeeWaiverUploadHeader aUploadHeader = new FeeWaiverUploadHeader();
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
				closeDialog(false);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.uploadHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(isReadOnly("button_WaiverUploadDialog_btnBrowse"), this.btnDownload);
			this.btnBrowse.setVisible(true);
		} else {
			this.btnBrowse.setVisible(false);
			this.btnDownload.setVisible(true);
			this.btnCancel.setVisible(true);
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

	public void doWriteBeanToComponents(FeeWaiverUploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setValue(uploadHeader.getFileName());

		if (!uploadHeader.isNewRecord() && CollectionUtils.isNotEmpty(uploadHeader.getUploadFeeWaivers())) {
			for (FeeWaiverUpload waiverUpload : uploadHeader.getUploadFeeWaivers()) {
				FeeWaiverUpload befImage = new FeeWaiverUpload();
				BeanUtils.copyProperties(waiverUpload, befImage);
				waiverUpload.setBefImage(befImage);
			}
		}

		this.recordStatus.setValue(uploadHeader.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	public boolean doWriteComponentsToBean(FeeWaiverUploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();
		validations = new HashSet<String>();

		List<WrongValueException> wve = new ArrayList<>();

		try {
			uploadHeader.setFileName(this.txtFileName.getValue());
			getUploadedWaivers(uploadHeader);

		} catch (WrongValueException we) {
			wve.add(we);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
			return false;
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

		uploadHeader.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
		return true;
	}

	public boolean doProcess(FeeWaiverUploadHeader aUploadHeader, String tranType) throws Exception {
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

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FeeWaiverUploadHeader aUploadHeader = (FeeWaiverUploadHeader) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						aAuditHeader = feeWaiverUploadHeaderService.delete(aAuditHeader);
						deleteNotes = true;
					} else {
						aAuditHeader = feeWaiverUploadHeaderService.saveOrUpdate(aAuditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						aAuditHeader = feeWaiverUploadHeaderService.doApprove(aAuditHeader);

						if (aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						aAuditHeader = feeWaiverUploadHeaderService.doReject(aAuditHeader);

						if (aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						aAuditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FeeWaiverUploadDialog, aAuditHeader);
						return processCompleted;
					}
				}

				aAuditHeader = ErrorControl.showErrorDetails(this.window_FeeWaiverUploadDialog, aAuditHeader);
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

		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private void getUploadedWaivers(FeeWaiverUploadHeader uploadHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		if (this.csvFile) {
			processCSVUploadDetails(uploadHeader);
		} else if (this.fileImport != null) {
			try {
				this.workbook = this.fileImport.writeFile();
			} catch (Exception e) {
				throw new InterfaceException("Error", "Invalid File format");
			}

			if (this.workbook != null) {
				List<String> keys = this.fileImport.getRowValuesByIndex(this.workbook, 0, 0, totalColumns);
				if (CollectionUtils.isEmpty(keys) || !("Loan Reference".equalsIgnoreCase(keys.get(0))
						&& "Fee / Charge Code".equalsIgnoreCase(keys.get(1))
						&& "Value Date".equalsIgnoreCase(keys.get(2)) && "Waived Amount".equalsIgnoreCase(keys.get(3))
						&& "Remarks".equalsIgnoreCase(keys.get(4)))) {
					throw new InterfaceException("Error",
							"The uploaded file could not be processed.Please upload a valid xlsx file.");
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
			throw new AppException("The uploaded file could not be recognized. Please upload a valid xlsx file.");
		}

		logger.debug(Literal.LEAVING);
	}

	private void processCSVUploadDetails(FeeWaiverUploadHeader uploadHeader) throws Exception {
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
			List<FeeWaiverUpload> feeWaivers = new ArrayList<FeeWaiverUpload>();
			int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

			while ((line = br.readLine()) != null) {
				List<String> row = Arrays.asList(line.split(cvsSplitBy, totalColumns));

				if (row.size() >= totalColumns) {
					if (count == 0) { // Skip Header row
						if (CollectionUtils.isEmpty(row) || !("Loan Reference".equalsIgnoreCase(row.get(0))
								&& "Fee / Charge Code".equalsIgnoreCase(row.get(1))
								&& "Value Date".equalsIgnoreCase(row.get(2))
								&& "Waived Amount".equalsIgnoreCase(row.get(3))
								&& "Remarks".equalsIgnoreCase(row.get(4)))) {
							throw new InterfaceException("Error",
									"The uploaded file could not be processed.Please upload a valid xls or xlsx file.");
						}
					} else {
						feeWaivers.add(validateUploadDetails(row, formatter));
					}
				} else {
					// Failure Case
					FeeWaiverUpload waiverUpload = new FeeWaiverUpload();
					waiverUpload.setNewRecord(true);
					waiverUpload.setRecordType(PennantConstants.RCD_ADD);
					waiverUpload.setVersion(waiverUpload.getVersion() + 1);
					waiverUpload.setStatus("F");
					waiverUpload.setReason("Number of columns are not matching.");
					feeWaivers.add(waiverUpload);
				}

				count++;
			}
			uploadHeader.setUploadFeeWaivers(feeWaivers);
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

	private void processExcelUploadDetails(FeeWaiverUploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		List<FeeWaiverUpload> waiverUploads = new ArrayList<FeeWaiverUpload>();
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
						waiverUploads.add(validateUploadDetails(columns, formatter));
					} else {
						FeeWaiverUpload waiverUpload = new FeeWaiverUpload();
						waiverUpload.setNewRecord(true);
						waiverUpload.setRecordType(PennantConstants.RCD_ADD);
						waiverUpload.setVersion(waiverUpload.getVersion() + 1);
						waiverUpload.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
						waiverUpload.setReason("Number of columns are not matching.");
						waiverUploads.add(waiverUpload);
					}
				}
			}
		}
		uploadHeader.setTotalRecords(sheet.getPhysicalNumberOfRows() - 1);
		uploadHeader.setUploadFeeWaivers(waiverUploads);

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

	private void closeDialog(boolean confirmationreq) {
		doClose(confirmationreq);
	}

	protected void doPostClose() {
		Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();
	}

	protected void refreshList() {
		if (feeWaiverUploadListCtrl != null) {
			feeWaiverUploadListCtrl.search();
		}
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	private AuditHeader getAuditHeader(FeeWaiverUploadHeader aUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aUploadHeader.getBefImage(), aUploadHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aUploadHeader.getUserDetails(),
				getOverideMap());
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setFeeWaiverUploadHeaderService(FeeWaiverUploadHeaderService feeWaiverUploadHeaderService) {
		this.feeWaiverUploadHeaderService = feeWaiverUploadHeaderService;
	}

	public FeeWaiverHeaderService getFeeWaiverHeaderService() {
		return feeWaiverHeaderService;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

}
