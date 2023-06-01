package com.pennant.webui.finance.upload;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;
import com.pennant.backend.service.finance.MiscPostingUploadService;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.JvPostingConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class MiscPostingUploadDialogCtrl extends GFCBaseCtrl<UploadHeader> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(MiscPostingUploadDialogCtrl.class);

	protected Window window_MiscPostingUploadDialog;

	protected Textbox txtFileName;
	protected ExtendedCombobox entity;
	private UploadHeader uploadHeader;
	private transient UploadListCtrl uploadListCtrl;
	private transient boolean validationOn;
	private transient UploadHeaderService uploadHeaderService;
	private MiscPostingUploadService miscPostingUploadService;

	protected Tab tabRejectRecords;
	protected Button approval_RejectButton;
	protected Button approval_SuccessButton;
	protected Listheader listHeader_CheckBox_Name;
	protected Listheader listHeader_CheckBox_Name2;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Listcell listCell_Checkbox2;
	protected Listitem listItem_Checkbox2;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Checkbox listHeader_CheckBox_Comp2;
	protected Checkbox list_CheckBox;
	protected Checkbox list_CheckBox2;
	protected Grid grid_basicDetails;
	protected Listbox listBoxUploadDetails;
	protected Listbox listBoxUploadFailedDetails;
	protected Listbox listBoxUploadRejectDetails;
	private PagedListWrapper<MiscPostingUpload> successPagedListWrapper;
	private PagedListWrapper<MiscPostingUpload> failedPagedListWrapper;
	private PagedListWrapper<MiscPostingUpload> rejectPagedListWrapper;
	private String module;
	private Map<Long, MiscPostingUpload> miscPostingValueMap = new HashMap<>();

	private Map<Long, MiscPostingUpload> successMiscMap = new HashMap<>();
	private Map<Long, MiscPostingUpload> rejectedMiscMap = new HashMap<>();
	private Map<Long, MiscPostingUpload> failedMiscMap = new HashMap<>();

	private List<MiscPostingUpload> successMiscList = new ArrayList<>();
	private List<MiscPostingUpload> rejectedMiscList = new ArrayList<>();
	private List<MiscPostingUpload> failedMiscList = new ArrayList<>();

	private boolean enqModule = false;

	/**
	 * default constructor.<br>
	 */
	public MiscPostingUploadDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MiscPostingUploadDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_MiscPostingUploadDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_MiscPostingUploadDialog);

		try {
			// Get the required arguments.
			this.uploadHeader = (UploadHeader) arguments.get("uploadHeader");
			this.uploadListCtrl = (UploadListCtrl) arguments.get("uploadListCtrl");
			this.module = (String) arguments.get("module");

			if (arguments.containsKey("enqModule")) {
				this.enqModule = (Boolean) arguments.get("enqModule");
			}

			if (this.uploadHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			UploadHeader uploadHeader = new UploadHeader();
			BeanUtils.copyProperties(this.uploadHeader, uploadHeader);
			this.uploadHeader.setBefImage(uploadHeader);

			for (MiscPostingUpload miscPostingUpload : this.uploadHeader.getMiscPostingUploads()) {
				miscPostingValueMap.put(miscPostingUpload.getMiscPostingId(), miscPostingUpload);
			}

			// Render the page and display the data.
			doLoadWorkFlow(this.uploadHeader.isWorkflow(), this.uploadHeader.getWorkflowId(),
					this.uploadHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !this.enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			setFailedUploadPageList();
			setSuccessUploadPageList();
			setRejectUploadPageList();

			doSetFieldProperties();
			doCheckRights();

			getBorderLayoutHeight();
			this.listBoxUploadDetails.setHeight(borderLayoutHeight - 180 + "px");
			this.listBoxUploadFailedDetails.setHeight(borderLayoutHeight - 180 + "px");
			listBoxUploadRejectDetails.setHeight(borderLayoutHeight - 180 + "px");

			doShowDialog(this.uploadHeader);

			if (JvPostingConstants.MISCELLANEOUSPOSTING_APPROVER.equals(module)) {
				this.approval_RejectButton.setVisible(true);
				this.approval_SuccessButton.setVisible(true);
				this.tabRejectRecords.setVisible(true);
			} else {
				this.listBoxUploadDetails.setMultiple(false);
				this.listBoxUploadDetails.setCheckmark(false);
				this.listBoxUploadRejectDetails.setMultiple(false);
				this.listBoxUploadRejectDetails.setCheckmark(false);
			}
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
		this.entity.setMandatoryStyle(false);
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.setDisplayStyle(2);
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MiscPostingUploadDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MiscPostingUploadDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MiscPostingUploadDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MiscPostingUploadDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$approval_RejectButton(Event event) {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.listBoxUploadDetails);
		if (listBoxUploadDetails.getSelectedItems().isEmpty()) {
			MessageUtil.showMessage(" Please select at least one record. ");
			return;
		}

		for (Listitem listitem : this.listBoxUploadDetails.getSelectedItems()) {
			if (this.listBoxUploadDetails.getSelectedItems().contains(listitem)) {
				MiscPostingUpload miscPostingUpload = (MiscPostingUpload) listitem.getAttribute("data");
				miscPostingUpload.setReason("");
				this.rejectedMiscMap.put(miscPostingUpload.getMiscPostingId(), miscPostingUpload);
				this.successMiscMap.remove(miscPostingUpload.getMiscPostingId());
			}
		}

		doFillList(new ArrayList<MiscPostingUpload>(successMiscMap.values()), listBoxUploadDetails);
		doFillList(new ArrayList<MiscPostingUpload>(rejectedMiscMap.values()), listBoxUploadRejectDetails);

		logger.debug("Leaving" + event.toString());
	}

	public void doFillList(List<MiscPostingUpload> miscPostingUpload, Listbox listbox) {
		logger.debug("Entering");

		if (miscPostingUpload != null && !miscPostingUpload.isEmpty()) {
			listbox.getItems().clear();
			Listcell lc;
			for (MiscPostingUpload miscPosting : miscPostingUpload) {

				Listitem item = new Listitem();

				lc = new Listcell(String.valueOf(miscPosting.getTransactionId()));
				lc.setParent(item);

				lc = new Listcell(miscPosting.getBatchPurpose());
				lc.setParent(item);

				lc = new Listcell(miscPosting.getBranch());
				lc.setParent(item);

				lc = new Listcell(miscPosting.getPostAgainst());
				lc.setParent(item);

				String reference = miscPosting.getReference();

				if (StringUtils.isNotBlank(reference)) {
					reference = reference.toUpperCase();
				}

				lc = new Listcell(reference);
				lc.setParent(item);

				lc = new Listcell(miscPosting.getAccount());
				lc.setParent(item);

				String txnEntry = miscPosting.getTxnEntry();
				if (!StringUtils.isEmpty(miscPosting.getTxnEntry())) {
					if (StringUtils.equals(miscPosting.getTxnEntry(), "C")) {
						txnEntry = "Cr";
					} else if (StringUtils.equals(miscPosting.getTxnEntry(), "D")) {
						txnEntry = "Dr";
					}
				}

				lc = new Listcell(txnEntry);
				lc.setParent(item);

				lc = new Listcell(CurrencyUtil.format(miscPosting.getTxnAmount(), 2));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(DateUtil.formatToLongDate(miscPosting.getValueDate()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(miscPosting.getReason());
				lc.setParent(item);

				item.setAttribute("data", miscPosting);
				listbox.appendChild(item);
			}
		} else {
			listbox.getItems().clear();
		}
		logger.debug("Leaving");
	}

	public void onClick$approval_SuccessButton(Event event) {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.listBoxUploadRejectDetails);
		if (listBoxUploadRejectDetails.getSelectedItems().isEmpty()) {
			MessageUtil.showMessage(" Please select at least one record. ");
			return;
		}

		for (Listitem listitem : this.listBoxUploadRejectDetails.getSelectedItems()) {
			if (this.listBoxUploadRejectDetails.getSelectedItems().contains(listitem)) {
				MiscPostingUpload miscPostingUpload = (MiscPostingUpload) listitem.getAttribute("data");
				this.successMiscMap.put(miscPostingUpload.getMiscPostingId(), miscPostingUpload);
				this.rejectedMiscMap.remove(miscPostingUpload.getMiscPostingId());
			}
		}

		doFillList(new ArrayList<MiscPostingUpload>(successMiscMap.values()), listBoxUploadDetails);
		doFillList(new ArrayList<MiscPostingUpload>(rejectedMiscMap.values()), listBoxUploadRejectDetails);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
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
		doClose(this.btnSave.isVisible());
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

		this.txtFileName.setValue(uploadHeader.getFileName());
		this.entity.setValue(uploadHeader.getEntityCode(), uploadHeader.getEntityDesc());
		this.recordStatus.setValue(uploadHeader.getRecordStatus());

		for (MiscPostingUpload miscPostingUpload : uploadHeader.getMiscPostingUploads()) {

			if (StringUtils.equals(miscPostingUpload.getUploadStatus(), PennantConstants.UPLOAD_STATUS_SUCCESS)) {
				successMiscList.add(miscPostingUpload);
				successMiscMap.put(miscPostingUpload.getMiscPostingId(), miscPostingUpload);
			} else if (StringUtils.equals(miscPostingUpload.getUploadStatus(), PennantConstants.UPLOAD_STATUS_FAIL)) {
				failedMiscMap.put(miscPostingUpload.getMiscPostingId(), miscPostingUpload);
				failedMiscList.add(miscPostingUpload);
			} else {
				rejectedMiscMap.put(miscPostingUpload.getMiscPostingId(), miscPostingUpload);
				rejectedMiscList.add(miscPostingUpload);
			}
		}

		// setting success records in success tab
		doFillList(successMiscList, listBoxUploadDetails);
		doFillList(failedMiscList, listBoxUploadFailedDetails);
		doFillList(rejectedMiscList, listBoxUploadRejectDetails);

		this.recordStatus.setValue(uploadHeader.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aUploadHeader
	 */
	public void doWriteComponentsToBean(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			this.uploadHeader.setFileName(this.txtFileName.getValue());
			this.uploadHeader.setEntityCode(this.entity.getValue().toString());
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

		uploadHeader.setRecordStatus(this.recordStatus.getValue());

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

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// validating MicPosting File Data & Length
		if (uploadHeader.isNewRecord()) {
			uploadHeader.setMiscPostingUploads(miscPostingUploadService.validateMiscPostingUploads(uploadHeader));
		}

		// fill the components with the data
		doWriteBeanToComponents(uploadHeader);

		if (enqModule) {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
			this.tabRejectRecords.setVisible(true);
		}

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.txtFileName.setConstraint("");

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
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final UploadHeader UpHeader = new UploadHeader();
		BeanUtils.copyProperties(this.uploadHeader, UpHeader);

		String keyReference = Labels.getLabel("label_MiscPostingUploadDialog_Filename.value") + " : "
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
		} else {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.txtFileName);

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

		this.txtFileName.setValue("");

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
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the UploadHeader object with the components data
		doWriteComponentsToBean(aUploadHeader);

		if (this.userAction.getSelectedItem() != null) {
			if ("Approve".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {
				if (dosetTotalCount(aUploadHeader)) {
					return;
				}
			} else {
				setMiscPostingListAndCount(aUploadHeader);
				if (this.successMiscList != null) {
					if (this.successMiscList.size() == 0 || this.successMiscList.size() == 1) {
						MessageUtil.showMessage("At least Two record status should be Success.");
						return;
					}
				}
			}
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
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showMessage(e.toString());
		}

		logger.debug(Literal.LEAVING);
	}

	private void setMiscPostingListAndCount(UploadHeader aUploadHeader) {
		logger.debug(Literal.ENTERING);
		int successCount = 0;
		List<MiscPostingUpload> successMiscPostingList = new ArrayList<>();
		List<MiscPostingUpload> rejectMiscpostingList = new ArrayList<>();

		if (!aUploadHeader.isNewRecord()) {
			successMiscList = new ArrayList<>(this.successMiscMap.values());
			failedMiscList = new ArrayList<>(this.failedMiscMap.values());
			rejectedMiscList = new ArrayList<>(this.rejectedMiscMap.values());
		}

		for (MiscPostingUpload miscPostingUpload : successMiscList) {
			miscPostingUpload.setUploadStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
			miscPostingUpload.setReason("");
			successMiscPostingList.add(miscPostingUpload);
			successCount++;
		}

		for (MiscPostingUpload miscPostingUpload : rejectedMiscList) {
			miscPostingUpload.setUploadStatus(PennantConstants.UPLOAD_STATUS_REJECT);
			miscPostingUpload.setReason("Manually rejected by the approver ");
			rejectMiscpostingList.add(miscPostingUpload);
		}

		List<MiscPostingUpload> totalMiscRecors = new ArrayList<>();
		totalMiscRecors.addAll(failedMiscList);
		totalMiscRecors.addAll(successMiscPostingList);
		totalMiscRecors.addAll(rejectMiscpostingList);

		// clearing exsting data
		aUploadHeader.getMiscPostingUploads().clear();
		// setting new data
		aUploadHeader.setMiscPostingUploads(totalMiscRecors);

		aUploadHeader.setSuccessCount(successCount);
		aUploadHeader.setFailedCount(aUploadHeader.getMiscPostingUploads().size() - successCount);
		aUploadHeader.setTotalRecords(aUploadHeader.getMiscPostingUploads().size());

		logger.debug(Literal.LEAVING);
	}

	private boolean dosetTotalCount(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		List<MiscPostingUpload> successMiscRecords = new ArrayList<MiscPostingUpload>(successMiscMap.values());
		List<MiscPostingUpload> listSuccessRecord = new ArrayList<>();

		if (successMiscRecords != null) {
			if (successMiscRecords.size() == 0 || successMiscRecords.size() == 1) {
				MessageUtil.showMessage("At least Two record status should be Success.");
				return true;
			}

			for (MiscPostingUpload miscPostingUpload : successMiscRecords) {
				miscPostingUpload.setReason("");
				miscPostingUpload.setUploadStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
			}

			listSuccessRecord = miscPostingUploadService.validateBasedonTransactionId(successMiscRecords, uploadHeader);

		}

		for (MiscPostingUpload miscPostingUpload : listSuccessRecord) {
			if (StringUtils.equals(miscPostingUpload.getUploadStatus(), PennantConstants.UPLOAD_STATUS_FAIL)) {
				if (StringUtils.isNotBlank(miscPostingUpload.getReference())) {
					MessageUtil.showMessage("Invalid postings against reference: " + miscPostingUpload.getReference()
							+ " with Transaction Id :" + miscPostingUpload.getTransactionId());
					return true;
				}
			}
		}

		setMiscPostingListAndCount(uploadHeader);

		logger.debug(Literal.LEAVING);
		return false;
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
	protected boolean doProcess(UploadHeader aUploadHeader, String tranType) {
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
						MessageUtil.showMessage(Labels.getLabel("Notes_NotEmpty"));
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
					aAuditHeader = uploadHeaderService.doApprove(aAuditHeader);
					if (aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aUploadHeader.setValidationReq(false);
					aAuditHeader = uploadHeaderService.doReject(aAuditHeader);

					if (aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_MiscPostingUploadDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_MiscPostingUploadDialog, aAuditHeader);
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
		uploadListCtrl.search();
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

	@SuppressWarnings("unchecked")
	public void setFailedUploadPageList() {
		if (this.failedPagedListWrapper == null) {
			this.failedPagedListWrapper = (PagedListWrapper<MiscPostingUpload>) SpringUtil.getBean("pagedListWrapper");
		}
	}

	@SuppressWarnings("unchecked")
	public void setSuccessUploadPageList() {
		if (this.successPagedListWrapper == null) {
			this.successPagedListWrapper = (PagedListWrapper<MiscPostingUpload>) SpringUtil.getBean("pagedListWrapper");
		}
	}

	@SuppressWarnings("unchecked")
	public void setRejectUploadPageList() {
		if (this.rejectPagedListWrapper == null) {
			this.rejectPagedListWrapper = (PagedListWrapper<MiscPostingUpload>) SpringUtil.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<MiscPostingUpload> getSuccessPagedListWrapper() {
		return successPagedListWrapper;
	}

	public void setSuccessPagedListWrapper(PagedListWrapper<MiscPostingUpload> successPagedListWrapper) {
		this.successPagedListWrapper = successPagedListWrapper;
	}

	public PagedListWrapper<MiscPostingUpload> getFailedPagedListWrapper() {
		return failedPagedListWrapper;
	}

	public void setFailedPagedListWrapper(PagedListWrapper<MiscPostingUpload> failedPagedListWrapper) {
		this.failedPagedListWrapper = failedPagedListWrapper;
	}

	public MiscPostingUploadService getMiscPostingUploadService() {
		return miscPostingUploadService;
	}

	public void setMiscPostingUploadService(MiscPostingUploadService miscPostingUploadService) {
		this.miscPostingUploadService = miscPostingUploadService;
	}

	public class MiscPostingUploadListModelItemRenderer implements ListitemRenderer<MiscPostingUpload>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8236407402292558694L;

		public MiscPostingUploadListModelItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, MiscPostingUpload miscPosting, int count) throws Exception {

			Listcell lc;

			lc = new Listcell(String.valueOf(miscPosting.getTransactionId()));
			lc.setParent(item);

			lc = new Listcell(miscPosting.getBatchPurpose());
			lc.setParent(item);

			lc = new Listcell(miscPosting.getBranch());
			lc.setParent(item);

			lc = new Listcell(miscPosting.getPostAgainst());
			lc.setParent(item);

			String reference = miscPosting.getReference();

			if (StringUtils.isNotBlank(reference)) {
				reference = reference.toUpperCase();
			}

			lc = new Listcell(reference);
			lc.setParent(item);

			lc = new Listcell(miscPosting.getAccount());
			lc.setParent(item);

			String txnEntry = miscPosting.getTxnEntry();
			if (!StringUtils.isEmpty(miscPosting.getTxnEntry())) {
				if (StringUtils.equals(miscPosting.getTxnEntry(), "C")) {
					txnEntry = "Cr";
				} else if (StringUtils.equals(miscPosting.getTxnEntry(), "D")) {
					txnEntry = "Dr";
				}
			}

			lc = new Listcell(txnEntry);
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(miscPosting.getTxnAmount(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(DateUtil.formatToLongDate(miscPosting.getValueDate()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(miscPosting.getReason());
			lc.setParent(item);

		}

	}

	public class MiscPostingUploadRejectListItemRenderer implements ListitemRenderer<MiscPostingUpload>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1575041147690519358L;

		public MiscPostingUploadRejectListItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, MiscPostingUpload miscPosting, int count) throws Exception {

			Listcell lc;

			lc = new Listcell(String.valueOf(miscPosting.getTransactionId()));
			lc.setParent(item);

			lc = new Listcell(miscPosting.getBatchPurpose());
			lc.setParent(item);

			lc = new Listcell(miscPosting.getBranch());
			lc.setParent(item);

			lc = new Listcell(miscPosting.getPostAgainst());
			lc.setParent(item);

			String reference = miscPosting.getReference();

			if (StringUtils.isNotBlank(reference)) {
				reference = reference.toUpperCase();
			}

			lc = new Listcell(reference);
			lc.setParent(item);

			lc = new Listcell(miscPosting.getAccount());
			lc.setParent(item);

			String txnEntry = miscPosting.getTxnEntry();
			if (!StringUtils.isEmpty(miscPosting.getTxnEntry())) {
				if (StringUtils.equals(miscPosting.getTxnEntry(), "C")) {
					txnEntry = "Cr";
				} else if (StringUtils.equals(miscPosting.getTxnEntry(), "D")) {
					txnEntry = "Dr";
				}
			}

			lc = new Listcell(txnEntry);
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(miscPosting.getTxnAmount(), 2));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(DateUtil.formatToLongDate(miscPosting.getValueDate()));
			lc.setStyle("text-align:right;");
			lc.setParent(item);

			lc = new Listcell(miscPosting.getReason());
			lc.setParent(item);

		}

	}

	public PagedListWrapper<MiscPostingUpload> getRejectPagedListWrapper() {
		return rejectPagedListWrapper;
	}

	public void setRejectPagedListWrapper(PagedListWrapper<MiscPostingUpload> rejectPagedListWrapper) {
		this.rejectPagedListWrapper = rejectPagedListWrapper;
	}
}
