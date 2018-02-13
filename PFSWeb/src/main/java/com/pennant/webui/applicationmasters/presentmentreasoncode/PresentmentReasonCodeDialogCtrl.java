package com.pennant.webui.applicationmasters.presentmentreasoncode;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.PresentmentReasonCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.PresentmentReasonCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class PresentmentReasonCodeDialogCtrl extends GFCBaseCtrl<PresentmentReasonCode> {
	private static final long serialVersionUID = -2229794581795422226L;
	private static final Logger logger = Logger.getLogger(PresentmentReasonCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_PresentmentReasonCodeDialog; 			
	protected Textbox 		code; 						
	protected Textbox 		description; 						
	protected Checkbox 		active; 					


	// not auto wired variables
	private PresentmentReasonCode 	presentmentReasonCode; 



	private transient 		PresentmentReasonCodeListCtrl presentmentReasonCodeListCtrl; // overHanded per parameter

	// Button controller for the CRUD buttons
	private transient boolean 			validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient PresentmentReasonCodeService 	presentmentReasonCodeService;

	/**
	 * default constructor.<br>
	 */
	public PresentmentReasonCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PresentmentReasonCodeDialog";
	}

	// Component Events

	public void onCreate$window_PresentmentReasonCodeDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PresentmentReasonCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("presentmentReasonCode")) {
				this.presentmentReasonCode = (PresentmentReasonCode) arguments.get("presentmentReasonCode");
				PresentmentReasonCode befImage = new PresentmentReasonCode();
				BeanUtils.copyProperties(this.presentmentReasonCode, befImage);
				this.presentmentReasonCode.setBefImage(befImage);
				setPresentmentReasonCode(this.presentmentReasonCode);
			} else {
				setPresentmentReasonCode(null);
			}

			doLoadWorkFlow(this.presentmentReasonCode.isWorkflow(),
					this.presentmentReasonCode.getWorkflowId(),
					this.presentmentReasonCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"PresentmentReasonCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the PresentmentReasonCodeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete PresentmentReasonCode here.
			if (arguments.containsKey("presentmentReasonCodeListCtrl")) {
				setPresentmentReasonCodeListCtrl((PresentmentReasonCodeListCtrl) arguments
						.get("presentmentReasonCodeListCtrl"));
			} else {
				setPresentmentReasonCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getPresentmentReasonCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PresentmentReasonCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_PresentmentReasonCodeDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.presentmentReasonCode);
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aPresentmentReasonCode
	 * @throws Exception
	 */

	public void doShowDialog(PresentmentReasonCode aPresentmentReasonCode)throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aPresentmentReasonCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.code.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.description.focus();
				if (StringUtils.isNotBlank(aPresentmentReasonCode.getRecordType())) {
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
			// fill the components with the data
			doWriteBeanToComponents(aPresentmentReasonCode);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PresentmentReasonCodeDialog.onClose();
		}
		logger.debug("Leaving");
	}


	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PresentmentReasonCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PresentmentReasonCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PresentmentReasonCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PresentmentReasonCodeDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}


	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.code.setMaxlength(8);
		this.description.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void doWriteBeanToComponents(PresentmentReasonCode aPresentmentReasonCode) {
		logger.debug("Entering");
		this.code.setValue(aPresentmentReasonCode.getCode());
		this.description.setValue(aPresentmentReasonCode.getDescription());
		this.active.setChecked(aPresentmentReasonCode.isActive());
		this.recordStatus.setValue(aPresentmentReasonCode.getRecordStatus());

		if(aPresentmentReasonCode.isNew() || (aPresentmentReasonCode.getRecordType() != null ? aPresentmentReasonCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPresentmentReasonCode
	 */
	
	public void doWriteComponentsToBean(PresentmentReasonCode aPresentmentReasonCode) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aPresentmentReasonCode.setCode(this.code.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPresentmentReasonCode.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPresentmentReasonCode.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aPresentmentReasonCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getPresentmentReasonCode().isNewRecord()) {
			this.code.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.code.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.description.setReadonly(isReadOnly("PresentmentReasonCodeDialog_Description"));
		this.active.setDisabled(isReadOnly("PresentmentReasonCodeDialog_Active"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.presentmentReasonCode.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.code.setReadonly(true);
		this.description.setReadonly(true);
		this.active.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}


	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.code.setValue("");
		this.description.setValue("");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Deletes a PresentmentReasonCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final PresentmentReasonCode aPresentmentReasonCode = new PresentmentReasonCode();
		BeanUtils.copyProperties(getPresentmentReasonCode(), aPresentmentReasonCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_PresentmentReasonCodeDialog_Code.value")+" : "+aPresentmentReasonCode.getCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aPresentmentReasonCode.getRecordType())) {
				aPresentmentReasonCode.setVersion(aPresentmentReasonCode.getVersion() + 1);
				aPresentmentReasonCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPresentmentReasonCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aPresentmentReasonCode, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final PresentmentReasonCode presentmentReasonCode = new PresentmentReasonCode();
		BeanUtils.copyProperties(getPresentmentReasonCode(), presentmentReasonCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the PresentmentReasonCode object with the components data
		doWriteComponentsToBean(presentmentReasonCode);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = presentmentReasonCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(presentmentReasonCode.getRecordType())) {
				presentmentReasonCode.setVersion(presentmentReasonCode.getVersion() + 1);
				if (isNew) {
					presentmentReasonCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					presentmentReasonCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					presentmentReasonCode.setNewRecord(true);
				}
			}
		} else {
			presentmentReasonCode.setVersion(presentmentReasonCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(presentmentReasonCode, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}


	private boolean doProcess(PresentmentReasonCode aPresentmentReasonCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPresentmentReasonCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aPresentmentReasonCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPresentmentReasonCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPresentmentReasonCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPresentmentReasonCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPresentmentReasonCode);
				}
				if (isNotesMandatory(taskId, aPresentmentReasonCode)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
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

			aPresentmentReasonCode.setTaskId(taskId);
			aPresentmentReasonCode.setNextTaskId(nextTaskId);
			aPresentmentReasonCode.setRoleCode(getRole());
			aPresentmentReasonCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPresentmentReasonCode, tranType);

			String operationRefs = getServiceOperations(taskId, aPresentmentReasonCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPresentmentReasonCode,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aPresentmentReasonCode, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		PresentmentReasonCode aPresentmentReasonCode = (PresentmentReasonCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getPresentmentReasonCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getPresentmentReasonCodeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getPresentmentReasonCodeService().doApprove(auditHeader);

						if (aPresentmentReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getPresentmentReasonCodeService().doReject(auditHeader);

						if (aPresentmentReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_PresentmentReasonCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_PresentmentReasonCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.presentmentReasonCode), true);
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

	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.presentmentReasonCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	private void doSetValidation(){
		logger.debug("Entering");
		setValidationOn(true);
		if(!this.code.isReadonly()){
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_PresentmentReasonCodeDialog_Code.value"), PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM,true));
		}
		if (!this.description.isReadonly()){
			this.description.setConstraint(new PTStringValidator(Labels.getLabel("label_PresentmentReasonCodeDialog_Description.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug("Leaving");
	}

	private void doRemoveValidation(){
		logger.debug("Entering");
		setValidation(false);
		this.code.setConstraint("");
		this.description.setConstraint("");
		logger.debug("Leaving");
	}
	
	@Override
	protected void doClearMessage(){
		logger.debug("entering");
		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
		logger.debug("Leaving");
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_PresentmentReasonCodeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getPresentmentReasonCodeListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.presentmentReasonCode.getCode());
	}

	private AuditHeader getAuditHeader(PresentmentReasonCode aPresentmentReasonCode,String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aPresentmentReasonCode.getBefImage(), aPresentmentReasonCode);
		return new AuditHeader(String.valueOf(aPresentmentReasonCode.getId()), null,
				null, null, auditDetail, aPresentmentReasonCode.getUserDetails(),
				getOverideMap());
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public PresentmentReasonCode getPresentmentReasonCode() {
		return presentmentReasonCode;
	}
	public void setPresentmentReasonCode(PresentmentReasonCode presentmentReasonCode) {
		this.presentmentReasonCode = presentmentReasonCode;
	}

	public PresentmentReasonCodeListCtrl getPresentmentReasonCodeListCtrl() {
		return presentmentReasonCodeListCtrl;
	}
	public void setPresentmentReasonCodeListCtrl(PresentmentReasonCodeListCtrl presentmentReasonCodeListCtrl) {
		this.presentmentReasonCodeListCtrl = presentmentReasonCodeListCtrl;
	}

	public PresentmentReasonCodeService getPresentmentReasonCodeService() {
		return presentmentReasonCodeService;
	}
	public void setPresentmentReasonCodeService(PresentmentReasonCodeService presentmentReasonCodeService) {
		this.presentmentReasonCodeService = presentmentReasonCodeService;
	}

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
}