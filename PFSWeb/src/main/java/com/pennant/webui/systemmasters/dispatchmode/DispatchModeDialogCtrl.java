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
 * FileName    		:  DispatchModeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-08-2011    														*
 *                                                                  						*
 * Modified Date    :  18-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.dispatchmode;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.service.systemmasters.DispatchModeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/DispatchMode/dispatchModeDialog.zul file.
 */
public class DispatchModeDialogCtrl extends GFCBaseCtrl<DispatchMode> {
	private static final long serialVersionUID = 6974163751783013342L;
	private static final Logger logger = Logger.getLogger(DispatchModeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_DispatchModeDialog; 	// autoWired

	protected Textbox 	dispatchModeCode; 			// autoWired
	protected Textbox 	dispatchModeDesc; 			// autoWired
	protected Checkbox 	dispatchModeIsActive; 		// autoWired


	// not autoWiredd Variables
	private DispatchMode dispatchMode; // overHanded per parameter
	private transient DispatchModeListCtrl dispatchModeListCtrl;// overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient DispatchModeService dispatchModeService;

	/**
	 * default constructor.<br>
	 */
	public DispatchModeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DispatchModeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected DispatchMode object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DispatchModeDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DispatchModeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("dispatchMode")) {
				this.dispatchMode = (DispatchMode) arguments.get("dispatchMode");
				DispatchMode befImage = new DispatchMode();
				BeanUtils.copyProperties(this.dispatchMode, befImage);
				this.dispatchMode.setBefImage(befImage);

				setDispatchMode(this.dispatchMode);
			} else {
				setDispatchMode(null);
			}

			doLoadWorkFlow(this.dispatchMode.isWorkflow(),
					this.dispatchMode.getWorkflowId(),
					this.dispatchMode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"DispatchModeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the dispatchModeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete dispatchMode here.
			if (arguments.containsKey("dispatchModeListCtrl")) {
				setDispatchModeListCtrl((DispatchModeListCtrl) arguments
						.get("dispatchModeListCtrl"));
			} else {
				setDispatchModeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDispatchMode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_DispatchModeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.dispatchModeCode.setMaxlength(2);
		this.dispatchModeDesc.setMaxlength(50);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DispatchModeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DispatchModeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DispatchModeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DispatchModeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_DispatchModeDialog);
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
		doWriteBeanToComponents(this.dispatchMode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDispatchMode
	 *            DispatchMode
	 */
	public void doWriteBeanToComponents(DispatchMode aDispatchMode) {
		logger.debug("Entering");

		this.dispatchModeCode.setValue(aDispatchMode.getDispatchModeCode());
		this.dispatchModeDesc.setValue(aDispatchMode.getDispatchModeDesc());
		this.dispatchModeIsActive.setChecked(aDispatchMode.isDispatchModeIsActive());
		this.recordStatus.setValue(aDispatchMode.getRecordStatus());
		
		if(aDispatchMode.isNew() || (aDispatchMode.getRecordType() != null ? aDispatchMode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.dispatchModeIsActive.setChecked(true);
			this.dispatchModeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDispatchMode
	 */
	public void doWriteComponentsToBean(DispatchMode aDispatchMode) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aDispatchMode.setDispatchModeCode(this.dispatchModeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDispatchMode.setDispatchModeDesc(this.dispatchModeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDispatchMode.setDispatchModeIsActive(this.dispatchModeIsActive.isChecked());
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

		aDispatchMode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDispatchMode
	 * @throws Exception
	 */
	public void doShowDialog(DispatchMode aDispatchMode) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aDispatchMode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.dispatchModeCode.focus();
		} else {
			this.dispatchModeDesc.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aDispatchMode.getRecordType())) {
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
			doWriteBeanToComponents(aDispatchMode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_DispatchModeDialog.onClose();
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

		if (!this.dispatchModeCode.isReadonly()){
			this.dispatchModeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_DispatchModeDialog_DispatchModeCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.dispatchModeDesc.isReadonly()){
			this.dispatchModeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_DispatchModeDialog_DispatchModeDesc.value"), 
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
		this.dispatchModeCode.setConstraint("");
		this.dispatchModeDesc.setConstraint("");
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
		this.dispatchModeCode.setErrorMessage("");
		this.dispatchModeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a DispatchMode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final DispatchMode aDispatchMode = new DispatchMode();
		BeanUtils.copyProperties(getDispatchMode(), aDispatchMode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_DispatchModeDialog_DispatchModeCode.value")+" : "+aDispatchMode.getDispatchModeCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDispatchMode.getRecordType())) {
				aDispatchMode.setVersion(aDispatchMode.getVersion() + 1);
				aDispatchMode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDispatchMode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aDispatchMode, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
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

		if (getDispatchMode().isNewRecord()) {
			this.dispatchModeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.dispatchModeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.dispatchModeDesc.setReadonly(isReadOnly("DispatchModeDialog_dispatchModeDesc"));
		this.dispatchModeIsActive.setDisabled(isReadOnly("DispatchModeDialog_dispatchModeIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.dispatchMode.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.dispatchModeCode.setReadonly(true);
		this.dispatchModeDesc.setReadonly(true);
		this.dispatchModeIsActive.setDisabled(true);

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
		this.dispatchModeCode.setValue("");
		this.dispatchModeDesc.setValue("");
		this.dispatchModeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final DispatchMode aDispatchMode = new DispatchMode();
		BeanUtils.copyProperties(getDispatchMode(), aDispatchMode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DispatchMode object with the components data
		doWriteComponentsToBean(aDispatchMode);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aDispatchMode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDispatchMode.getRecordType())) {
				aDispatchMode.setVersion(aDispatchMode.getVersion() + 1);
				if (isNew) {
					aDispatchMode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDispatchMode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDispatchMode.setNewRecord(true);
				}
			}
		} else {
			aDispatchMode.setVersion(aDispatchMode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aDispatchMode, tranType)) {
				refreshList();
				// Close the Existing Dialog
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
	 * @param aDispatchMode
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(DispatchMode aDispatchMode, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDispatchMode.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aDispatchMode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDispatchMode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDispatchMode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDispatchMode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDispatchMode);
				}

				if (isNotesMandatory(taskId, aDispatchMode)) {
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

			aDispatchMode.setTaskId(taskId);
			aDispatchMode.setNextTaskId(nextTaskId);
			aDispatchMode.setRoleCode(getRole());
			aDispatchMode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDispatchMode, tranType);

			String operationRefs = getServiceOperations(taskId, aDispatchMode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDispatchMode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDispatchMode, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		DispatchMode aDispatchMode = (DispatchMode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getDispatchModeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getDispatchModeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getDispatchModeService().doApprove(auditHeader);

						if (aDispatchMode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getDispatchModeService().doReject(auditHeader);

						if (aDispatchMode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DispatchModeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_DispatchModeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.dispatchMode), true);
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
	 * Get Audit Header Details
	 * 
	 * @param aDispatchMode
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(DispatchMode aDispatchMode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDispatchMode.getBefImage(), aDispatchMode);
		return new AuditHeader(String.valueOf(aDispatchMode.getId()), null,
				null, null, auditDetail, aDispatchMode.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_DispatchModeDialog, auditHeader);
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
		doShowNotes(this.dispatchMode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getDispatchModeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.dispatchMode.getDispatchModeCode());
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

	public DispatchMode getDispatchMode() {
		return this.dispatchMode;
	}
	public void setDispatchMode(DispatchMode dispatchMode) {
		this.dispatchMode = dispatchMode;
	}

	public void setDispatchModeService(DispatchModeService dispatchModeService) {
		this.dispatchModeService = dispatchModeService;
	}
	public DispatchModeService getDispatchModeService() {
		return this.dispatchModeService;
	}

	public void setDispatchModeListCtrl(DispatchModeListCtrl dispatchModeListCtrl) {
		this.dispatchModeListCtrl = dispatchModeListCtrl;
	}
	public DispatchModeListCtrl getDispatchModeListCtrl() {
		return this.dispatchModeListCtrl;
	}

}
