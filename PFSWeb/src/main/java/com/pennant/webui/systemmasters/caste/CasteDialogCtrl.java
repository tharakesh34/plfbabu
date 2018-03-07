/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CasteDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.caste;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Caste;
import com.pennant.backend.service.systemmasters.CasteService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Caste/casteDialog.zul file.
 */
public class CasteDialogCtrl extends GFCBaseCtrl<Caste> {

	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = Logger.getLogger(CasteDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CasteDialog;
	protected Textbox casteCode;
	protected Textbox casteDesc;
	protected Checkbox casteIsActive;

	private transient boolean validationOn;

	private transient CasteListCtrl casteListCtrl;

	// ServiceDAOs / Domain Classes
	private transient CasteService casteService;
	private Caste caste;

	/**
	 * default constructor.<br>
	 */
	public CasteDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CasteDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Caste object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CasteDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CasteDialog);

		/* set components visible dependent of the users rights */
		try {
			doCheckRights();

			this.caste = (Caste) arguments.get("caste");

			Caste befImage = new Caste();
			BeanUtils.copyProperties(this.caste, befImage);

			this.caste.setBefImage(befImage);
			setCaste(this.caste);

			setCasteListCtrl((CasteListCtrl) arguments.get("casteListCtrl"));

			doLoadWorkFlow(this.caste.isWorkflow(), this.caste.getWorkflowId(), this.caste.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CasteDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCaste());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CasteDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.casteCode.setMaxlength(8);
		this.casteDesc.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CasteDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CasteDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CasteDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CasteDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
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
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());

		doEdit();

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

		MessageUtil.showHelpWindow(event, window_CasteDialog);

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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());

		doCancel();

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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.caste.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCaste
	 *            Caste
	 */
	public void doWriteBeanToComponents(Caste aCaste) {
		logger.debug("Entering");

		this.casteCode.setValue(aCaste.getCasteCode());
		this.casteDesc.setValue(aCaste.getCasteDesc());
		this.casteIsActive.setChecked(aCaste.isCasteIsActive());

		this.recordStatus.setValue(aCaste.getRecordStatus());

		if (aCaste.isNew() || (aCaste.getRecordType() != null ? aCaste.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.casteIsActive.setChecked(true);
			this.casteIsActive.setDisabled(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCaste
	 */
	public void doWriteComponentsToBean(Caste aCaste) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCaste.setCasteCode(this.casteCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCaste.setCasteDesc(this.casteDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCaste.setCasteIsActive(this.casteIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCaste.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCaste
	 * @throws Exception
	 */
	public void doShowDialog(Caste aCaste) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aCaste.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.casteCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.casteDesc.focus();
				if (StringUtils.isNotBlank(aCaste.getRecordType())) {
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
			doWriteBeanToComponents(aCaste);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CasteDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.casteCode.isReadonly()) {
			this.casteCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CasteDialog_CasteCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.casteDesc.isReadonly()) {
			this.casteDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CasteDialog_CasteDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		setValidationOn(false);
		this.casteCode.setConstraint("");
		this.casteDesc.setConstraint("");

		logger.debug("Leaving");
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
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.casteCode.setErrorMessage("");
		this.casteDesc.setErrorMessage("");

		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a Caste object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final Caste aCaste = new Caste();
		BeanUtils.copyProperties(getCaste(), aCaste);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_CasteDialog_CasteCode.value") + " : " + aCaste.getCasteCode();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCaste.getRecordType())) {
				aCaste.setVersion(aCaste.getVersion() + 1);
				aCaste.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCaste.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCaste, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCaste().isNewRecord()) {
			this.casteCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.casteCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.casteDesc.setReadonly(isReadOnly("CasteDialog_casteDesc"));
		this.casteIsActive.setDisabled(isReadOnly("CasteDialog_casteIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.caste.isNewRecord()) {
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

		this.casteCode.setReadonly(true);
		this.casteDesc.setReadonly(true);
		this.casteIsActive.setDisabled(true);

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
		this.casteCode.setValue("");
		this.casteDesc.setValue("");
		this.casteIsActive.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Caste aCaste = new Caste();
		BeanUtils.copyProperties(getCaste(), aCaste);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Caste object with the components data
		doWriteComponentsToBean(aCaste);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCaste.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCaste.getRecordType())) {
				aCaste.setVersion(aCaste.getVersion() + 1);
				if (isNew) {
					aCaste.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCaste.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCaste.setNewRecord(true);
				}
			}
		} else {
			aCaste.setVersion(aCaste.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aCaste, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCaste
	 *            (Caste)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Caste aCaste, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCaste.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCaste.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCaste.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCaste.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCaste.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCaste);
				}

				if (isNotesMandatory(taskId, aCaste)) {
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

			aCaste.setTaskId(taskId);
			aCaste.setNextTaskId(nextTaskId);
			aCaste.setRoleCode(getRole());
			aCaste.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCaste, tranType);

			String operationRefs = getServiceOperations(taskId, aCaste);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCaste, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCaste, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");

		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Caste aCaste = (Caste) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCasteService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCasteService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCasteService().doApprove(auditHeader);

						if (aCaste.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCasteService().doReject(auditHeader);

						if (aCaste.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CasteDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CasteDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.caste), true);
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

	// WorkFlow Components

	/**
	 * @param aCaste
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(Caste aCaste, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCaste.getBefImage(), aCaste);
		return new AuditHeader(String.valueOf(aCaste.getId()), null, null, null, auditDetail, aCaste.getUserDetails(),
				getOverideMap());

	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CasteDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}

		logger.debug("Leaving");
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
		doShowNotes(this.caste);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCasteListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.caste.getCasteCode());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public Caste getCaste() {
		return this.caste;
	}

	public void setCaste(Caste caste) {
		this.caste = caste;
	}

	public void setCasteService(CasteService casteService) {
		this.casteService = casteService;
	}

	public CasteService getCasteService() {
		return this.casteService;
	}

	public void setCasteListCtrl(CasteListCtrl casteListCtrl) {
		this.casteListCtrl = casteListCtrl;
	}

	public CasteListCtrl getCasteListCtrl() {
		return this.casteListCtrl;
	}
}
