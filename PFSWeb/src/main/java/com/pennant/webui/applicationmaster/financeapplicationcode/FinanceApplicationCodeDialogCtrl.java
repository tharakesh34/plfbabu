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
 * FileName    		:  FinanceApplicationCodeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.financeapplicationcode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.FinanceApplicationCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/FinanceApplicationCode
 * /financeApplicationCodeDialog.zul file.
 */
public class FinanceApplicationCodeDialogCtrl extends GFCBaseCtrl<FinanceApplicationCode> {
	private static final long serialVersionUID = -2489293301745014852L;
	private static final Logger logger = Logger.getLogger(FinanceApplicationCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_FinanceApplicationCodeDialog;	// autoWired

	protected Textbox 		finAppType; 						// autoWired
	protected Textbox 		finAppDesc; 						// autoWired
	protected Checkbox 		finAppIsActive; 					// autoWired

	// not autoWired variables
	private FinanceApplicationCode financeApplicationCode; // overHanded per parameter
	private transient FinanceApplicationCodeListCtrl financeApplicationCodeListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient FinanceApplicationCodeService financeApplicationCodeService;

	/**
	 * default constructor.<br>
	 */
	public FinanceApplicationCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceApplicationCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceApplicationCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceApplicationCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceApplicationCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("financeApplicationCode")) {
				this.financeApplicationCode = (FinanceApplicationCode) arguments
						.get("financeApplicationCode");
				FinanceApplicationCode befImage = new FinanceApplicationCode();
				BeanUtils.copyProperties(this.financeApplicationCode, befImage);
				this.financeApplicationCode.setBefImage(befImage);
				setFinanceApplicationCode(this.financeApplicationCode);
			} else {
				setFinanceApplicationCode(null);
			}

			doLoadWorkFlow(this.financeApplicationCode.isWorkflow(),
					this.financeApplicationCode.getWorkflowId(),
					this.financeApplicationCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"FinanceApplicationCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the financeApplicationCodeListWindow controller. So we
			// have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete financeApplicationCode here.
			if (arguments.containsKey("financeApplicationCodeListCtrl")) {
				setFinanceApplicationCodeListCtrl((FinanceApplicationCodeListCtrl)arguments.get("financeApplicationCodeListCtrl"));
			} else {
				setFinanceApplicationCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinanceApplicationCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceApplicationCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.finAppType.setMaxlength(8);
		this.finAppDesc.setMaxlength(50);

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
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceApplicationCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceApplicationCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceApplicationCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceApplicationCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_FinanceApplicationCodeDialog);
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
		doWriteBeanToComponents(this.financeApplicationCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceApplicationCode
	 *            FinanceApplicationCode
	 */
	public void doWriteBeanToComponents(FinanceApplicationCode aFinanceApplicationCode) {
		logger.debug("Entering");
		this.finAppType.setValue(aFinanceApplicationCode.getFinAppType());
		this.finAppDesc.setValue(aFinanceApplicationCode.getFinAppDesc());
		this.finAppIsActive.setChecked(aFinanceApplicationCode.isFinAppIsActive());
		this.recordStatus.setValue(aFinanceApplicationCode.getRecordStatus());
		
		if(aFinanceApplicationCode.isNew() || (aFinanceApplicationCode.getRecordType() != null ? aFinanceApplicationCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.finAppIsActive.setChecked(true);
			this.finAppIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceApplicationCode
	 */
	public void doWriteComponentsToBean(FinanceApplicationCode aFinanceApplicationCode) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinanceApplicationCode.setFinAppType(this.finAppType.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceApplicationCode.setFinAppDesc(this.finAppDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceApplicationCode.setFinAppIsActive(this.finAppIsActive.isChecked());
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

		aFinanceApplicationCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceApplicationCode
	 * @throws Exception
	 */
	public void doShowDialog(FinanceApplicationCode aFinanceApplicationCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aFinanceApplicationCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finAppType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.finAppDesc.focus();
				if (StringUtils.isNotBlank(aFinanceApplicationCode.getRecordType())) {
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
			doWriteBeanToComponents(aFinanceApplicationCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceApplicationCodeDialog.onClose();
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

		if (!this.finAppType.isReadonly()){
			this.finAppType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceApplicationCodeDialog_FinAppType.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	

		if (!this.finAppDesc.isReadonly()){
			this.finAppDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceApplicationCodeDialog_FinAppDesc.value"), 
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
		this.finAppType.setConstraint("");
		this.finAppDesc.setConstraint("");
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
		this.finAppType.setErrorMessage("");
		this.finAppDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a FinanceApplicationCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final FinanceApplicationCode aFinanceApplicationCode = new FinanceApplicationCode();
		BeanUtils.copyProperties(getFinanceApplicationCode(), aFinanceApplicationCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_FinanceApplicationCodeDialog_FinAppType.value")+" : "+aFinanceApplicationCode.getFinAppType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinanceApplicationCode.getRecordType())) {
				aFinanceApplicationCode.setVersion(aFinanceApplicationCode.getVersion() + 1);
				aFinanceApplicationCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinanceApplicationCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aFinanceApplicationCode, tranType)) {
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

		if (getFinanceApplicationCode().isNewRecord()) {
			this.finAppType.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finAppType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.finAppDesc.setReadonly(isReadOnly("FinanceApplicationCodeDialog_finAppDesc"));
		this.finAppIsActive.setDisabled(isReadOnly("FinanceApplicationCodeDialog_finAppIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeApplicationCode.isNewRecord()) {
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

		this.finAppType.setReadonly(true);
		this.finAppDesc.setReadonly(true);
		this.finAppIsActive.setDisabled(true);

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
		this.finAppType.setValue("");
		this.finAppDesc.setValue("");
		this.finAppIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinanceApplicationCode aFinanceApplicationCode = new FinanceApplicationCode();
		BeanUtils.copyProperties(getFinanceApplicationCode(), aFinanceApplicationCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinanceApplicationCode object with the components data
		doWriteComponentsToBean(aFinanceApplicationCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aFinanceApplicationCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceApplicationCode.getRecordType())) {
				aFinanceApplicationCode.setVersion(aFinanceApplicationCode.getVersion() + 1);
				if (isNew) {
					aFinanceApplicationCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceApplicationCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceApplicationCode.setNewRecord(true);
				}
			}
		} else {
			aFinanceApplicationCode.setVersion(aFinanceApplicationCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aFinanceApplicationCode, tranType)) {
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
	 * @param aFinanceApplicationCode
	 *            (FinanceApplicationCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(FinanceApplicationCode aFinanceApplicationCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinanceApplicationCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceApplicationCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceApplicationCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinanceApplicationCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceApplicationCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinanceApplicationCode);
				}

				if (isNotesMandatory(taskId, aFinanceApplicationCode)) {
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
					nextRoleCode =getTaskOwner(nextTaskId);
				}
			}

			aFinanceApplicationCode.setTaskId(taskId);
			aFinanceApplicationCode.setNextTaskId(nextTaskId);
			aFinanceApplicationCode.setRoleCode(getRole());
			aFinanceApplicationCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceApplicationCode, tranType);
			String operationRefs = getServiceOperations(taskId, aFinanceApplicationCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceApplicationCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinanceApplicationCode, tranType);
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
		FinanceApplicationCode aFinanceApplicationCode = (FinanceApplicationCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceApplicationCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceApplicationCodeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceApplicationCodeService().doApprove(auditHeader);

						if (aFinanceApplicationCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceApplicationCodeService().doReject(auditHeader);

						if (aFinanceApplicationCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceApplicationCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceApplicationCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.financeApplicationCode), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
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
	 * @param aFinanceApplicationCode
	 *            (FinanceApplicationCode)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(FinanceApplicationCode aFinanceApplicationCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceApplicationCode.getBefImage(), aFinanceApplicationCode);
		return new AuditHeader(String.valueOf(aFinanceApplicationCode.getId()),
				null, null, null, auditDetail, aFinanceApplicationCode.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_FinanceApplicationCodeDialog, auditHeader);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes(this.financeApplicationCode));
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,	map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getFinanceApplicationCodeListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.financeApplicationCode.getFinAppType());
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

	public FinanceApplicationCode getFinanceApplicationCode() {
		return this.financeApplicationCode;
	}
	public void setFinanceApplicationCode(FinanceApplicationCode financeApplicationCode) {
		this.financeApplicationCode = financeApplicationCode;
	}

	public void setFinanceApplicationCodeService(FinanceApplicationCodeService financeApplicationCodeService) {
		this.financeApplicationCodeService = financeApplicationCodeService;
	}
	public FinanceApplicationCodeService getFinanceApplicationCodeService() {
		return this.financeApplicationCodeService;
	}

	public void setFinanceApplicationCodeListCtrl(FinanceApplicationCodeListCtrl financeApplicationCodeListCtrl) {
		this.financeApplicationCodeListCtrl = financeApplicationCodeListCtrl;
	}
	public FinanceApplicationCodeListCtrl getFinanceApplicationCodeListCtrl() {
		return this.financeApplicationCodeListCtrl;
	}

}
