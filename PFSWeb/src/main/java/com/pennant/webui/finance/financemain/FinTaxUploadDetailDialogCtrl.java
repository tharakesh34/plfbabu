package com.pennant.webui.finance.financemain;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.service.finance.FinTaxUploadDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinTaxUploadDetailDialogCtrl extends GFCBaseCtrl<FinTaxUploadHeader> {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = LogManager.getLogger(FinTaxUploadDetailDialogCtrl.class);

	private FinTaxUploadDetailListCtrl finTaxUploadDetailListCtrl = null; // over
	protected Window window_FinTaxUploadDetail; // autoWired
	protected Label fileUpload; // autoWired
	protected Button btnUpload; // autoWired

	protected Button btnSave; // autowired
	protected Button btnCancel; // autowired
	protected Textbox uploadedfileName;
	protected Grid grid_UploadedDetails;
	protected Label recordStatus;
	protected Borderlayout borderLayout_FinTaxUploadDetail; // autoWired
	protected Listbox listBoxFileData;
	protected Checkbox select;

	protected Label fileName;
	protected Label totalNoofRecords;
	protected Label batchCreationDate;
	protected Label batchApprovedDate;
	protected Label status;
	protected Listheader listheader_Select;
	private FinTaxUploadHeader finTaxUploadHeader;
	private Media media;
	protected Button button_ErrorDetails; // autoWired
	protected Label label_FinTaxUploadDialog_Errors;
	private AuditHeader retAuditHeader;

	private transient FinTaxUploadDetailService finTaxUploadDetailService;

	private boolean isvalidData = true;
	private FinanceMainDAO financeMainDAO;
	private PinCodeDAO pinCodeDAO;

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceTaxDetailDialog";
	}

	public void onCreate$window_FinTaxUploadDetail(Event event) {

		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTaxUploadDetail);

		try {
			// Get the required arguments.
			this.finTaxUploadHeader = (FinTaxUploadHeader) arguments.get("finTaxUploadHeader");
			this.finTaxUploadDetailListCtrl = (FinTaxUploadDetailListCtrl) arguments.get("finTaxUploadDetailListCtrl");

			/*
			 * if (this.finTaxUploadHeader == null) { throw new Exception(Labels.getLabel("error.unhandled")); }
			 */

			// Store the before image.
			FinTaxUploadHeader finTaxUploadHeader = new FinTaxUploadHeader();
			BeanUtils.copyProperties(this.finTaxUploadHeader, finTaxUploadHeader);
			this.finTaxUploadHeader.setBefImage(finTaxUploadHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.finTaxUploadHeader.isWorkflow(), this.finTaxUploadHeader.getWorkflowId(),
					this.finTaxUploadHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			}
			doCheckRights();
			doShowDialog(this.finTaxUploadHeader);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	private void doShowDialog(FinTaxUploadHeader finTaxUploadHeader) {

		logger.debug("Entering");
		this.listBoxFileData.setHeight(this.borderLayoutHeight - 220 + "px");

		if (StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			this.listheader_Select.setVisible(false);
		} else {
			this.listheader_Select.setVisible(true);
		}

		if (!finTaxUploadHeader.isNewRecord()) {
			grid_UploadedDetails.setVisible(false);
			doFillHeaderData(finTaxUploadHeader.getFileName(), finTaxUploadHeader.getBatchCreatedDate(),
					finTaxUploadHeader.getNumberofRecords(), finTaxUploadHeader.getStatus());
		}
		this.recordStatus.setValue(finTaxUploadHeader.getRecordStatus());
		doFillFinTaxUploadData(finTaxUploadHeader.getFinTaxUploadDetailList());
		setDialog(DialogType.EMBEDDED);
		logger.debug("Leaving");

	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnDelete"));
		this.btnSave.setVisible(true);
		this.btnNotes.setVisible(true);
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onUpload$btnUpload(UploadEvent event) throws IOException {
		logger.debug("Entering" + event.toString());
		boolean header = true;
		List<FinTaxUploadDetail> finTaxUploadDetailList = new ArrayList<>();
		int totalCount = 0;
		String status = null;
		media = event.getMedia();

		Sheet firstSheet;
		retAuditHeader = null;
		this.button_ErrorDetails.setVisible(false);
		this.label_FinTaxUploadDialog_Errors.setVisible(false);

		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			return;
		}

		if (media.getName().length() > 100) {
			throw new WrongValueException(this.uploadedfileName, Labels.getLabel("label_Filename_length_File"));
		} else {
			this.uploadedfileName.setValue(media.getName());
		}

		if (MediaUtil.isXls(media)) {
			firstSheet = new HSSFWorkbook(media.getStreamData()).getSheetAt(0);
		} else {
			firstSheet = new XSSFWorkbook(media.getStreamData()).getSheetAt(0);
		}

		Iterator<Row> iterator = firstSheet.iterator();

		while (iterator.hasNext()) {
			status = "Initiated";
			try {
				Row nextRow = iterator.next();
				if (header) {
					header = false;
					continue;
				}
				totalCount++;
				parseExcelData(finTaxUploadDetailList, nextRow);
				if (!isvalidData) {
					MessageUtil.showError(Labels.getLabel("label_File_Format"));
					isvalidData = true;
					this.uploadedfileName.setValue("");
					return;
				}
			} catch (Exception e) {
				logger.debug(e);
			}
		}

		doFillHeaderData(media.getName(), SysParamUtil.getAppDate(), totalCount, status);
		doFillFinTaxUploadData(finTaxUploadDetailList);
		getFinTaxUploadHeader().setFinTaxUploadDetailList(finTaxUploadDetailList);

		logger.debug(Literal.LEAVING);
	}

	private FinTaxUploadDetail parseExcelData(List<FinTaxUploadDetail> finTaxUploadDetail, Row nextRow) {
		FinTaxUploadDetail fintaxDetail;

		fintaxDetail = new FinTaxUploadDetail();
		fintaxDetail.setTaxCode(getValue(nextRow.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setAggrementNo(getValue(nextRow.getCell(1, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setApplicableFor(getValue(nextRow.getCell(2, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setApplicant(getValue(nextRow.getCell(3, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setAddrLine1(getValue(nextRow.getCell(4, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setAddrLine2(getValue(nextRow.getCell(5, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setAddrLine3(getValue(nextRow.getCell(6, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setAddrLine4(getValue(nextRow.getCell(7, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setPinCode(getValue(nextRow.getCell(8, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setCity(getValue(nextRow.getCell(9, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setProvince(getValue(nextRow.getCell(10, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setCountry(getValue(nextRow.getCell(11, MissingCellPolicy.CREATE_NULL_AS_BLANK)));
		fintaxDetail.setTaxExempted(
				getValue(nextRow.getCell(12, MissingCellPolicy.CREATE_NULL_AS_BLANK)).equals("Y") ? true : false);
		String pinCodeID = getValue(nextRow.getCell(13, MissingCellPolicy.CREATE_NULL_AS_BLANK));

		if (StringUtils.trimToNull(pinCodeID) != null) {
			fintaxDetail.setPinCodeID(Long.valueOf(pinCodeID));
		}

		PinCode pincode = pinCodeDAO.getPinCodeById(fintaxDetail.getPinCodeID(), "_AView");

		if (pincode == null) {
			MessageUtil.showError("Pin Code ID is in valid.");
			return fintaxDetail;
		}

		finTaxUploadDetail.add(fintaxDetail);
		return fintaxDetail;
	}

	private String getValue(Cell cell) {
		String value = "";

		switch (cell.getCellType()) {
		case STRING:
			value = cell.getStringCellValue();
			break;
		case BOOLEAN:
			if (cell.getBooleanCellValue()) {
				value = "Y";
			} else {
				value = "N";
			}
			break;
		case NUMERIC:
			value = String.valueOf(cell.getNumericCellValue());
			if (value.contains(".0")) {
				value = value.replace(".0", "");
			}
			break;
		default:
			break;
		}

		return value;
	}

	private void doFillHeaderData(String fileName, Date curBDay, long total, String Status) {
		this.fileName.setValue(fileName);
		this.batchCreationDate.setValue(DateUtil.format(curBDay, DateFormat.LONG_DATE.getPattern()));
		this.totalNoofRecords.setValue(total + "");
		this.status.setValue(Status + "");

	}

	private void doFillFinTaxUploadData(List<FinTaxUploadDetail> finTaxUploadDetailList) {
		this.listBoxFileData.getItems().clear();
		Clients.clearWrongValue(this.btnUpload);
		if (finTaxUploadDetailList != null && !finTaxUploadDetailList.isEmpty()) {
			for (FinTaxUploadDetail taxMappingDetail : finTaxUploadDetailList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell();
				Checkbox selected = new Checkbox();
				ComponentsCtrl.applyForward(selected, "onCheck=onChecklistItemSelect");
				selected.setChecked(false);
				selected.setParent(lc);
				lc.setParent(item);

				lc = new Listcell(taxMappingDetail.getTaxCode());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getAggrementNo());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getApplicableFor());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getApplicant());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getAddrLine1());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getAddrLine2());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getAddrLine3());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getAddrLine4());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getPinCode());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(taxMappingDetail.getPinCodeID()));
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getCity());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getProvince());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getCountry());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.isTaxExempted() ? "Y" : "N");
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getRecordStatus());
				lc.setParent(item);
				item.setAttribute("data", taxMappingDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceTaxDetailItemDoubleClicked");
				this.listBoxFileData.appendChild(item);
			}
		}
	}

	public void onFinanceTaxDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxFileData.getSelectedItem();
		FinTaxUploadDetail finTaxUploadDetail = (FinTaxUploadDetail) selectedItem.getAttribute("data");

		doShowDialogPage(finTaxUploadDetail);
		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(FinTaxUploadDetail tud) {
		logger.debug(Literal.ENTERING);

		FinanceTaxDetail td = new FinanceTaxDetail();

		Long finID = financeMainDAO.getActiveFinID(tud.getAggrementNo());

		td.setFinID(finID);
		td.setFinReference(tud.getAggrementNo());
		td.setApplicableFor(tud.getApplicableFor());
		td.setCustCIF(tud.getApplicant());
		td.setTaxNumber(tud.getTaxCode());
		td.setAddrLine1(tud.getAddrLine1());
		td.setAddrLine2(tud.getAddrLine2());
		td.setAddrLine3(tud.getAddrLine3());
		td.setAddrLine4(tud.getAddrLine4());
		td.setCountry(tud.getCountry());
		td.setProvince(tud.getProvince());
		td.setCity(tud.getCity());
		td.setPinCode(tud.getPinCode());
		td.setPinCodeId(tud.getPinCodeID());
		td.setTaxExempted(tud.isTaxExempted());

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("FinTaxUploadDetail", tud);
		arg.put("FinTaxUploadDetailDialogCtrl", this);
		arg.put("financeTaxDetail", td);
		arg.put("enquirymode", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceTaxDetail/FinanceTaxDetailDialog.zul", null,
					arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.finTaxUploadHeader);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public String getReference() {
		return this.finTaxUploadHeader.getBatchReference() + "";
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);
		FinTaxUploadHeader afinTaxUploadHeader = new FinTaxUploadHeader();
		BeanUtils.copyProperties(getFinTaxUploadHeader(), afinTaxUploadHeader);
		boolean isNew = false;
		List<FinTaxUploadDetail> finTaxUploadDetailList = new ArrayList<>();
		String userAction = this.userAction.getSelectedItem().getLabel();

		// validate once before save
		if (this.listBoxFileData.getItems().isEmpty()) {
			throw new WrongValueException(this.btnUpload, "Please Upload a Valid File to save");
		}
		if (afinTaxUploadHeader.isNewRecord()) {
			copyInputStreamToFile(media.getByteData(), new File(SysParamUtil.getValueAsString("GST_FILEUPLOAD_PATH")));
		}

		doSetValidation(userAction);
		doWriteComponentsToBean(finTaxUploadDetailList, userAction, afinTaxUploadHeader);

		isNew = afinTaxUploadHeader.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(afinTaxUploadHeader.getRecordType())) {
				afinTaxUploadHeader.setVersion(afinTaxUploadHeader.getVersion() + 1);
				if (isNew) {
					afinTaxUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					afinTaxUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					afinTaxUploadHeader.setNewRecord(true);
				}
			}
		} else {
			afinTaxUploadHeader.setVersion(afinTaxUploadHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(afinTaxUploadHeader, tranType)) {
				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(afinTaxUploadHeader.getRoleCode(),
						afinTaxUploadHeader.getNextRoleCode(), afinTaxUploadHeader.getBatchReference() + "",
						" GST Upload ", afinTaxUploadHeader.getRecordStatus());
				if (StringUtils.equals(afinTaxUploadHeader.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " GST Upload with Reference " + afinTaxUploadHeader.getBatchReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
	}

	private void copyInputStreamToFile(byte[] data, File file) {
		try {

			File backup = new File(file.getPath());
			if (backup != null && !backup.exists()) {
				backup.mkdir();
			}

			FileUtils.writeByteArrayToFile(file, data);

		} catch (Exception e) {
			logger.debug(e);
		}
	}

	private void doSetValidation(String userAction) {
		List<Listitem> Listitems = this.listBoxFileData.getItems();
		boolean selected = false;
		for (Listitem listitem : Listitems) {
			if (((Checkbox) listitem.getFirstChild().getFirstChild()).isChecked()) {
				selected = true;
			}
		}

		if (!selected && (StringUtils.equals(userAction, "Approve") || StringUtils.equals(userAction, "Reject"))) {
			throw new WrongValueException(this.listBoxFileData, Labels.getLabel("MandateDataList_NoEmpty"));
		}
	}

	private void doWriteComponentsToBean(List<FinTaxUploadDetail> finTaxUploadDetailList, String userAction,
			final FinTaxUploadHeader afinTaxUploadHeader) {
		FinTaxUploadDetail finTaxUploadDetail;
		int seqno = 0;

		// fill the header data
		afinTaxUploadHeader.setFileName(this.fileName.getValue());
		afinTaxUploadHeader.setNumberofRecords(Integer.parseInt(this.totalNoofRecords.getValue()));
		afinTaxUploadHeader.setBatchCreatedDate(SysParamUtil.getAppDate());

		if (StringUtils.equals(userAction, "Approve")) {
			afinTaxUploadHeader.setBatchApprovedDate(SysParamUtil.getAppDate());
		}
		afinTaxUploadHeader.setStatus(this.status.getValue());
		List<Listitem> Listitems = this.listBoxFileData.getItems();

		if (StringUtils.equals(userAction, "Approve") || StringUtils.equals(userAction, "Reject")) {
			for (Listitem listitem : Listitems) {
				finTaxUploadDetail = (FinTaxUploadDetail) listitem.getAttribute("data");
				finTaxUploadDetail.setRecordType(PennantConstants.RCD_ADD);
				if (((Checkbox) listitem.getFirstChild().getFirstChild()).isChecked()) {

					if (this.finTaxUploadHeader.isNewRecord()) {
						finTaxUploadDetail.setSeqNo(seqno++);
					}
					finTaxUploadDetailList.add(finTaxUploadDetail);
				}
				afinTaxUploadHeader.setFinTaxUploadDetailList(finTaxUploadDetailList);
			}

		} else {

			// Iterate through the list items to set the statuses
			for (Listitem listitem : Listitems) {
				finTaxUploadDetail = (FinTaxUploadDetail) listitem.getAttribute("data");
				finTaxUploadDetail.setRecordType(PennantConstants.RCD_ADD);
				if (this.finTaxUploadHeader.isNewRecord()) {
					seqno = seqno + 1;
					finTaxUploadDetail.setSeqNo(seqno);
				}
				finTaxUploadDetailList.add(finTaxUploadDetail);
			}
			afinTaxUploadHeader.setFinTaxUploadDetailList(finTaxUploadDetailList);
		}
		checkForSelection(afinTaxUploadHeader);
	}

	private void checkForSelection(FinTaxUploadHeader afinTaxUploadHeader) {
		List<Listitem> Listitems = this.listBoxFileData.getItems();
		boolean totalSelected = false;
		for (Listitem listitem : Listitems) {
			if (((Checkbox) listitem.getFirstChild().getFirstChild()).isChecked()) {
				totalSelected = true;
			} else {
				totalSelected = false;
				break;
			}
		}
		afinTaxUploadHeader.setTotalSelected(totalSelected);
	}

	protected boolean doProcess(FinTaxUploadHeader aFinTaxUploadHeader, String tranType) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinTaxUploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinTaxUploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinTaxUploadHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinTaxUploadHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinTaxUploadHeader);
				}

				if (isNotesMandatory(taskId, aFinTaxUploadHeader)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aFinTaxUploadHeader.setTaskId(taskId);
			aFinTaxUploadHeader.setNextTaskId(nextTaskId);
			aFinTaxUploadHeader.setRoleCode(getRole());
			aFinTaxUploadHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinTaxUploadHeader, tranType);

			String operationRefs = getServiceOperations(taskId, aFinTaxUploadHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinTaxUploadHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinTaxUploadHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinTaxUploadHeader finTaxUploadHeader = (FinTaxUploadHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getFinTaxUploadDetailService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getFinTaxUploadDetailService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getFinTaxUploadDetailService().doApprove(auditHeader);
					if (finTaxUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getFinTaxUploadDetailService().doReject(auditHeader);
					if (finTaxUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FinTaxUploadDetail, auditHeader);
					return processCompleted;
				}
			}

			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 1) {
				this.button_ErrorDetails.setVisible(true);
				this.label_FinTaxUploadDialog_Errors.setVisible(true);
				retAuditHeader = auditHeader;
				MessageUtil.showError(Labels.getLabel("label_FinTaxUploadDialog_Error"));
				retValue = PennantConstants.porcessCANCEL;
			} else {
				auditHeader = ErrorControl.showErrorDetails(this.window_FinTaxUploadDetail, auditHeader);
				retValue = auditHeader.getProcessStatus();
			}
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.finTaxUploadHeader), true);
				}
			}
			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug("Leaving");
		return processCompleted;
	}

	private AuditHeader getAuditHeader(FinTaxUploadHeader afinTaxUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinTaxUploadHeader.getBefImage(), afinTaxUploadHeader);
		return new AuditHeader(Long.toString(afinTaxUploadHeader.getBatchReference()), null, null, null, auditDetail,
				afinTaxUploadHeader.getUserDetails(), getOverideMap());
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	protected void refreshList() {
		final JdbcSearchObject<FinTaxUploadHeader> soFinTaxUploadHeader = getFinTaxUploadDetailListCtrl()
				.getSearchObject();
		getFinTaxUploadDetailListCtrl().pagingFinTaxDetailUploadList.setActivePage(0);
		getFinTaxUploadDetailListCtrl().getPagedListWrapper().setSearchObject(soFinTaxUploadHeader);
		if (getFinTaxUploadDetailListCtrl().listBoxFinTaxUploadDetail != null) {
			getFinTaxUploadDetailListCtrl().listBoxFinTaxUploadDetail.getListModel();
		}
	}

	public void onCheck$select(Event event) {
		logger.debug("Entering");
		List<Listitem> Listitems = this.listBoxFileData.getItems();
		if (this.select.isChecked()) {
			for (Listitem listitem : Listitems) {
				((Checkbox) listitem.getFirstChild().getFirstChild()).setChecked(true);
				Clients.clearWrongValue(this.listBoxFileData);
			}
		} else {
			for (Listitem listitem : Listitems) {
				((Checkbox) listitem.getFirstChild().getFirstChild()).setChecked(false);

			}
		}
		logger.debug("Leaving");
	}

	public void onChecklistItemSelect(Event event) {
		List<Listitem> Listitems = this.listBoxFileData.getItems();
		boolean totalselected = false;
		for (Listitem listitem : Listitems) {
			if (((Checkbox) listitem.getFirstChild().getFirstChild()).isChecked()) {
				Clients.clearWrongValue(this.listBoxFileData);
				totalselected = true;
			} else {
				totalselected = false;
				break;
			}

		}
		this.select.setChecked(totalselected);
	}

	public void onClick$button_ErrorDetails(Event event) {
		logger.debug("Entering");
		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("AuditHeader", retAuditHeader);

		try {
			Executions.createComponents("/WEB-INF/pages/ErrorDetail/AuditErrorDetailsList.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	protected void doPostClose() {
		getFinTaxUploadDetailListCtrl().refreshList();
	}

	public FinTaxUploadDetailListCtrl getFinTaxUploadDetailListCtrl() {
		return finTaxUploadDetailListCtrl;
	}

	public void setFinTaxUploadDetailListCtrl(FinTaxUploadDetailListCtrl finTaxUploadDetailListCtrl) {
		this.finTaxUploadDetailListCtrl = finTaxUploadDetailListCtrl;
	}

	public FinTaxUploadHeader getFinTaxUploadHeader() {
		return finTaxUploadHeader;
	}

	public void setFinTaxUploadHeader(FinTaxUploadHeader finTaxUploadHeader) {
		this.finTaxUploadHeader = finTaxUploadHeader;
	}

	public FinTaxUploadDetailService getFinTaxUploadDetailService() {
		return finTaxUploadDetailService;
	}

	public void setFinTaxUploadDetailService(FinTaxUploadDetailService finTaxUploadDetailService) {
		this.finTaxUploadDetailService = finTaxUploadDetailService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		this.pinCodeDAO = pinCodeDAO;
	}

}