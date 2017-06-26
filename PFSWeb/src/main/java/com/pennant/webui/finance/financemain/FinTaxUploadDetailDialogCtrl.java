package com.pennant.webui.finance.financemain;

import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
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
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.service.finance.FinTaxUploadDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.InterfaceException;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class FinTaxUploadDetailDialogCtrl extends GFCBaseCtrl<FinTaxUploadHeader> {

	private static final long					serialVersionUID			= 1L;
	private final static Logger					logger						= Logger
			.getLogger(FinTaxUploadDetailDialogCtrl.class);

	private FinTaxUploadDetailListCtrl			finTaxUploadDetailListCtrl	= null;	// over
	protected Window							window_FinTaxUploadDetail;			// autoWired
	protected Label								fileUpload;							// autoWired
	protected Button							btnUpload;							// autoWired

	protected Button							btnSave;							// autowired
	protected Button							btnCancel;							// autowired
	protected Textbox							uploadedfileName;
	protected Grid								grid_UploadedDetails;
	protected Label								recordStatus;
	protected Borderlayout						borderLayout_FinTaxUploadDetail;	// autoWired
	protected Listbox							listBoxFileData;

	protected Label								fileName;
	protected Label								totalNoofRecords;
	protected Label								batchCreationDate;
	protected Label								batchApprovedDate;
	protected Label								status;

	private FinTaxUploadHeader					finTaxUploadHeader;

	private transient FinTaxUploadDetailService	finTaxUploadDetailService;

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceTaxDetailDialog";
	}

	public void onCreate$window_FinTaxUploadDetail(Event event) throws Exception {

		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTaxUploadDetail);

		try {
			// Get the required arguments.
			this.finTaxUploadHeader = (FinTaxUploadHeader) arguments.get("finTaxUploadHeader");
			this.finTaxUploadDetailListCtrl = (FinTaxUploadDetailListCtrl) arguments.get("finTaxUploadDetailListCtrl");

			if (this.finTaxUploadHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

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
		this.listBoxFileData.setHeight(this.borderLayoutHeight - 200 + "px");
		Checkbox box= new Checkbox();
		this.listBoxFileData.getListhead().getFirstChild().appendChild(box);
		if (!finTaxUploadHeader.isNew()) {
			grid_UploadedDetails.setVisible(false);
			doFillHeaderData(finTaxUploadHeader.getFileName(), finTaxUploadHeader.getBatchCreatedDate(),
					finTaxUploadHeader.getNumberofRecords(), finTaxUploadHeader.getStatus());
		}

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
	 * @throws Exception
	 */
	public void onUpload$btnUpload(UploadEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		boolean header = true;
		List<FinTaxUploadDetail> finTaxUploadDetailList = new ArrayList<>();
		boolean isSupported = false;
		int totalCount = 0;
		String status = null;
		Media media = event.getMedia();
		if ("xls".equals(media.getFormat())) {
			isSupported = true;
		}

		if (isSupported) {

			HSSFWorkbook workbook = new HSSFWorkbook(media.getStreamData());
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();

			while (iterator.hasNext()) {
				status = "Initiated";
				try {
					Row nextRow = iterator.next();
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					Cell cell = cellIterator.next();
					if (header) {
						header = false;
						continue;
					}
					totalCount++;
					parseExcelData(finTaxUploadDetailList, cellIterator, cell);
				} catch (Exception e) {
					logger.debug(e);
				}
			}
			doFillHeaderData(media.getName(), DateUtility.getAppDate(), totalCount, status);
			doFillFinTaxUploadData(finTaxUploadDetailList);
			getFinTaxUploadHeader().setFinTaxUploadDetailList(finTaxUploadDetailList);

		} else {
			MessageUtil.showError(Labels.getLabel("GSTUpload_Supported_Document"));
		}

		logger.debug("Leaving" + event.toString());
	}

	private FinTaxUploadDetail parseExcelData(List<FinTaxUploadDetail> finTaxUploadDetail, Iterator<Cell> cellIterator,
			Cell cell) {
		FinTaxUploadDetail fintaxDetail;
		fintaxDetail = new FinTaxUploadDetail();
		fintaxDetail.setTaxCode(getValue(cell));
		fintaxDetail.setAggrementNo(getValue(cellIterator.next()));
		fintaxDetail.setApplicableFor(getValue(cellIterator.next()));
		fintaxDetail.setApplicant(getValue(cellIterator.next()));
		fintaxDetail.setAddrLine1(getValue(cellIterator.next()));
		fintaxDetail.setAddrLine2(getValue(cellIterator.next()));
		fintaxDetail.setAddrLine3(getValue(cellIterator.next()));
		fintaxDetail.setAddrLine4(getValue(cellIterator.next()));
		fintaxDetail.setPinCode(getValue(cellIterator.next()));
		fintaxDetail.setCity(getValue(cellIterator.next()));
		fintaxDetail.setProvince(getValue(cellIterator.next()));
		fintaxDetail.setCountry(getValue(cellIterator.next()));
		fintaxDetail.setTaxExempted(getValue(cellIterator.next()).equals("Y") ? true : false);
		finTaxUploadDetail.add(fintaxDetail);
		return fintaxDetail;
	}

	private static String getValue(Cell cell) {
		String value = "";

		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			if (cell.getBooleanCellValue()) {
				value = "Y";
			} else {
				value = "N";
			}
			break;
		case Cell.CELL_TYPE_NUMERIC:
			value = String.valueOf(cell.getNumericCellValue());
			break;
		}

		return value;
	}

	private void doFillHeaderData(String fileName, Date curBDay, long total, String Status) {
		this.fileName.setValue(fileName);
		this.batchCreationDate.setValue(DateUtility.formatDate(curBDay, DateFormat.LONG_DATE.getPattern()));
		this.totalNoofRecords.setValue(total + "");
		this.status.setValue(Status + "");

	}

	private void doFillFinTaxUploadData(List<FinTaxUploadDetail> finTaxUploadDetailList) {
		this.listBoxFileData.getItems().clear();
		if (finTaxUploadDetailList != null && !finTaxUploadDetailList.isEmpty()) {
			for (FinTaxUploadDetail taxMappingDetail : finTaxUploadDetailList) {
				Listitem item = new Listitem();
				Listcell lc;
				
				lc = new Listcell();		
				Checkbox ckActive= new Checkbox();
				ckActive.setChecked(false);
				ckActive.setParent(lc);
				lc.setParent(item);

				lc = new Listcell(taxMappingDetail.getTaxCode());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getAggrementNo());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getApplicableFor());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getApplicant());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getPinCode());
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

	private void doShowDialogPage(FinTaxUploadDetail finTaxUploadDetail) {
		logger.debug(Literal.ENTERING);

		FinanceTaxDetail financeTaxDetail = new FinanceTaxDetail();
		financeTaxDetail.setFinReference(finTaxUploadDetail.getAggrementNo());
		financeTaxDetail.setApplicableFor(finTaxUploadDetail.getApplicableFor());
		financeTaxDetail.setTaxCustId(Long.valueOf(finTaxUploadDetail.getApplicant()));
		financeTaxDetail.setAddrLine1(finTaxUploadDetail.getAddrLine1());
		financeTaxDetail.setAddrLine2(finTaxUploadDetail.getAddrLine2());
		financeTaxDetail.setAddrLine3(finTaxUploadDetail.getAddrLine3());
		financeTaxDetail.setAddrLine4(finTaxUploadDetail.getAddrLine4());
		financeTaxDetail.setCountry(finTaxUploadDetail.getCountry());
		financeTaxDetail.setProvince(finTaxUploadDetail.getProvince());
		financeTaxDetail.setCity(finTaxUploadDetail.getCity());
		financeTaxDetail.setPinCode(finTaxUploadDetail.getPinCode());

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("FinTaxUploadDetail", finTaxUploadDetail);
		arg.put("FinTaxUploadDetailDialogCtrl", this);
		arg.put("financeTaxDetail", financeTaxDetail);
		arg.put("enqiryModule", true);

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
	 * @param event
	 *            An event sent to the event handler of the component.
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
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws Exception {
		doSave();
	}

	private void doSave() {
		logger.debug(Literal.ENTERING);
		FinTaxUploadHeader afinTaxUploadHeader = new FinTaxUploadHeader();
		BeanUtils.copyProperties(getFinTaxUploadHeader(), afinTaxUploadHeader);
		boolean isNew = false;
		List<FinTaxUploadDetail> finTaxUploadDetailList = new ArrayList<>();
		String userAction = this.userAction.getSelectedItem().getLabel();

		//validate  once before save
		if (this.listBoxFileData.getItems().isEmpty()) {
			throw new WrongValueException(this.btnUpload, "Please Upload a Valid File to save");
		}

		doWriteComponentsToBean(finTaxUploadDetailList, userAction, afinTaxUploadHeader);

		isNew = afinTaxUploadHeader.isNew();
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
					msg = "Fee Postings with Reference " + afinTaxUploadHeader.getBatchReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
	}

	private void doWriteComponentsToBean(List<FinTaxUploadDetail> finTaxUploadDetailList, String userAction,
			final FinTaxUploadHeader afinTaxUploadHeader) {
		FinTaxUploadDetail finTaxUploadDetail;
		//fill the header data
		afinTaxUploadHeader.setFileName(this.fileName.getValue());
		afinTaxUploadHeader.setNumberofRecords(Integer.parseInt(this.totalNoofRecords.getValue()));
		afinTaxUploadHeader.setBatchCreatedDate(DateUtility.getAppDate());
		afinTaxUploadHeader.setStatus(this.status.getValue());

		List<Listitem> Listitems = this.listBoxFileData.getItems();
		//Iterate through the list items to set the statuses
		for (Listitem listitem : Listitems) {
			finTaxUploadDetail = (FinTaxUploadDetail) listitem.getAttribute("data");
			finTaxUploadDetail.setRecordType(PennantConstants.RCD_ADD);

			//change the record status as per selection 
			if (listitem.isSelected()) {
				setStatusbyUserAction(finTaxUploadDetail, userAction);
			}
			finTaxUploadDetailList.add(finTaxUploadDetail);
		}
		afinTaxUploadHeader.setFinTaxUploadDetailList(finTaxUploadDetailList);
	}

	private void setStatusbyUserAction(FinTaxUploadDetail finTaxUploadDetail, String userAction) {
		if (StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			if ("Save".equalsIgnoreCase(userAction)) {
				finTaxUploadDetail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
			} else if ("Submit".equalsIgnoreCase(userAction)) {
				finTaxUploadDetail.setRecordStatus(PennantConstants.RCD_STATUS_SUBMITTED);
			}
		} else {
			if ("Resubmit".equalsIgnoreCase(userAction)) {
				finTaxUploadDetail.setRecordStatus(PennantConstants.RCD_STATUS_RESUBMITTED);
			} else if ("Reject".equalsIgnoreCase(userAction)) {
				finTaxUploadDetail.setRecordStatus(PennantConstants.RCD_STATUS_REJECTED);
			} else if ("Approve".equalsIgnoreCase(userAction)) {
				finTaxUploadDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
		}
	}

	private boolean doProcess(FinTaxUploadHeader aFinTaxUploadHeader, String tranType) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinTaxUploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
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
		try {
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinTaxUploadDetail, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_FinTaxUploadDetail, auditHeader);
				retValue = auditHeader.getProcessStatus();
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
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
	private void refreshList() {
		final JdbcSearchObject<FinTaxUploadHeader> soFinTaxUploadHeader = getFinTaxUploadDetailListCtrl()
				.getSearchObject();
		getFinTaxUploadDetailListCtrl().pagingFinTaxDetailUploadList.setActivePage(0);
		getFinTaxUploadDetailListCtrl().getPagedListWrapper().setSearchObject(soFinTaxUploadHeader);
		if (getFinTaxUploadDetailListCtrl().listBoxFinTaxUploadDetail != null) {
			getFinTaxUploadDetailListCtrl().listBoxFinTaxUploadDetail.getListModel();
		}
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if (doClose(this.btnSave.isVisible())) {
			getFinTaxUploadDetailListCtrl().refreshList();
		}
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

}