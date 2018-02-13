package com.pennant.webui.applicationmasters.targetdetails;

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

import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.TargetDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class TargetDetailDialogCtrl extends GFCBaseCtrl<TargetDetail> {
	private static final long serialVersionUID = -2229794581795422226L;
	private static final Logger logger = Logger.getLogger(TargetDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_TargetDetailDialog; 			// autoWired
	protected Textbox 		targetCode; 						// autoWired
	protected Textbox 		targetDesc; 						// autoWired
	protected Checkbox 		targetIsActive; 					// autoWired


	// not auto wired variables
	private TargetDetail 	targetDetail; // overHanded per parameter



	private transient 		TargetDetailListCtrl targetDetailListCtrl; // overHanded per parameter

	// Button controller for the CRUD buttons
	private transient boolean 			validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient TargetDetailService 	targetDetailService;

	/**
	 * default constructor.<br>
	 */
	public TargetDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TargetDetailDialog";
	}

	// Component Events

	public void onCreate$window_TargetDetailDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_TargetDetailDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("targetDetail")) {
				this.targetDetail = (TargetDetail) arguments.get("targetDetail");
				TargetDetail befImage = new TargetDetail();
				BeanUtils.copyProperties(this.targetDetail, befImage);
				this.targetDetail.setBefImage(befImage);
				setTargetDetail(this.targetDetail);
			} else {
				setTargetDetail(null);
			}

			doLoadWorkFlow(this.targetDetail.isWorkflow(),
					this.targetDetail.getWorkflowId(),
					this.targetDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"TargetDetailDialog");
			}

			// READ OVERHANDED parameters !
			// we get the targetDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete targetDetail here.
			if (arguments.containsKey("targetDetailListCtrl")) {
				setTargetDetailListCtrl((TargetDetailListCtrl) arguments
						.get("targetDetailListCtrl"));
			} else {
				setTargetDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getTargetDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_TargetDetailDialog.onClose();
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
		MessageUtil.showHelpWindow(event, window_TargetDetailDialog);
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
		doShowNotes(this.targetDetail);
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aTargetDetail
	 * @throws Exception
	 */

	public void doShowDialog(TargetDetail aTargetDetail)throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aTargetDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.targetCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.targetDesc.focus();
				if (StringUtils.isNotBlank(aTargetDetail.getRecordType())) {
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
			doWriteBeanToComponents(aTargetDetail);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_TargetDetailDialog.onClose();
		}
		logger.debug("Leaving");
	}


	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TargetDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TargetDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TargetDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TargetDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}


	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.targetCode.setMaxlength(8);
		this.targetDesc.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void doWriteBeanToComponents(TargetDetail aTargetDetail) {
		logger.debug("Entering");
		this.targetCode.setValue(aTargetDetail.getTargetCode());
		this.targetDesc.setValue(aTargetDetail.getTargetDesc());
		this.targetIsActive.setChecked(aTargetDetail.isActive());
		this.recordStatus.setValue(aTargetDetail.getRecordStatus());

		if(aTargetDetail.isNew() || (aTargetDetail.getRecordType() != null ? aTargetDetail.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.targetIsActive.setChecked(true);
			this.targetIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTargetDetail
	 */
	
	public void doWriteComponentsToBean(TargetDetail aTargetDetail) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aTargetDetail.setTargetCode(this.targetCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTargetDetail.setTargetDesc(this.targetDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aTargetDetail.setActive(this.targetIsActive.isChecked());
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

		aTargetDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getTargetDetail().isNewRecord()) {
			this.targetCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.targetCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.targetDesc.setReadonly(isReadOnly("TargetDetailDialog_targetDesc"));
		this.targetIsActive.setDisabled(isReadOnly("TargetDetailDialog_targetIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.targetDetail.isNewRecord()) {
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

		this.targetCode.setReadonly(true);
		this.targetDesc.setReadonly(true);
		this.targetIsActive.setDisabled(true);

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
		this.targetCode.setValue("");
		this.targetDesc.setValue("");
		this.targetIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Deletes a TargetDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final TargetDetail aTargetDetail = new TargetDetail();
		BeanUtils.copyProperties(getTargetDetail(), aTargetDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_TargetDetailDialog_TargetCode.value")+" : "+aTargetDetail.getTargetCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aTargetDetail.getRecordType())) {
				aTargetDetail.setVersion(aTargetDetail.getVersion() + 1);
				aTargetDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aTargetDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aTargetDetail, tranType)) {
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

		final TargetDetail atargetDetail = new TargetDetail();
		BeanUtils.copyProperties(getTargetDetail(), atargetDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the TargetDetail object with the components data
		doWriteComponentsToBean(atargetDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = atargetDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(atargetDetail.getRecordType())) {
				atargetDetail.setVersion(atargetDetail.getVersion() + 1);
				if (isNew) {
					atargetDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					atargetDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					atargetDetail.setNewRecord(true);
				}
			}
		} else {
			atargetDetail.setVersion(atargetDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(atargetDetail, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}


	private boolean doProcess(TargetDetail aTargetDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aTargetDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aTargetDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTargetDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aTargetDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aTargetDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aTargetDetail);
				}
				if (isNotesMandatory(taskId, aTargetDetail)) {
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

			aTargetDetail.setTaskId(taskId);
			aTargetDetail.setNextTaskId(nextTaskId);
			aTargetDetail.setRoleCode(getRole());
			aTargetDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aTargetDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aTargetDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aTargetDetail,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aTargetDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		TargetDetail aTargetDetail = (TargetDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getTargetDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getTargetDetailService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getTargetDetailService().doApprove(auditHeader);

						if (aTargetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getTargetDetailService().doReject(auditHeader);

						if (aTargetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_TargetDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_TargetDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.targetDetail), true);
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
		doWriteBeanToComponents(this.targetDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	private void doSetValidation(){
		logger.debug("Entering");
		setValidationOn(true);
		if(!this.targetCode.isReadonly()){
			this.targetCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TargetDetailDialog_TargetCode.value"), PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM,true));
		}
		if (!this.targetDesc.isReadonly()){
			this.targetDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_TargetDetailDialog_TargetDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug("Leaving");
	}

	private void doRemoveValidation(){
		logger.debug("Entering");
		setValidation(false);
		this.targetCode.setConstraint("");
		this.targetDesc.setConstraint("");
		logger.debug("Leaving");
	}
	
	@Override
	protected void doClearMessage(){
		logger.debug("entering");
		this.targetCode.setErrorMessage("");
		this.targetDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_TargetDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getTargetDetailListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.targetDetail.getTargetCode());
	}

	private AuditHeader getAuditHeader(TargetDetail aTargetDetail,String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aTargetDetail.getBefImage(), aTargetDetail);
		return new AuditHeader(String.valueOf(aTargetDetail.getId()), null,
				null, null, auditDetail, aTargetDetail.getUserDetails(),
				getOverideMap());
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public TargetDetail getTargetDetail() {
		return targetDetail;
	}
	public void setTargetDetail(TargetDetail targetDetail) {
		this.targetDetail = targetDetail;
	}

	public TargetDetailListCtrl getTargetDetailListCtrl() {
		return targetDetailListCtrl;
	}
	public void setTargetDetailListCtrl(TargetDetailListCtrl targetDetailListCtrl) {
		this.targetDetailListCtrl = targetDetailListCtrl;
	}

	public TargetDetailService getTargetDetailService() {
		return targetDetailService;
	}
	public void setTargetDetailService(TargetDetailService targetDetailService) {
		this.targetDetailService = targetDetailService;
	}

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

}
