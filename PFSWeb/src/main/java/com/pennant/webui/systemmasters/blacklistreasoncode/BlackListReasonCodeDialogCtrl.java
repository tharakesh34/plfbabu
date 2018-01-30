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
 * FileName    		:  BlackListReasonCodeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.blacklistreasoncode;

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

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.service.systemmasters.impl.BlackListReasonCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/BlackListReasonCode/BlackListReasonCodeDialog.zul
 * file.
 */
public class BlackListReasonCodeDialogCtrl extends GFCBaseCtrl<BlackListReasonCode> {
	private static final long serialVersionUID = 374144767782969911L;
	private static final Logger logger = Logger.getLogger(BlackListReasonCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BlackListReasonCodeDialog; // autoWired

	protected Textbox 		bLRsnCode; 					// autoWired
	protected Textbox 		bLRsnDesc; 					// autoWired
	protected Checkbox 		bLIsActive; 				// autoWired


	// not autoWired variables
	private BlackListReasonCode blackListReasonCode; 			// overHanded per parameter
	private transient BlackListReasonCodeListCtrl blackListReasonCodeListCtrl; // overHanded
	// per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient BlackListReasonCodeService blackListReasonCodeService;

	/**
	 * default constructor.<br>
	 */
	public BlackListReasonCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BlackListReasonCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected BlackListReasonCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BlackListReasonCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BlackListReasonCodeDialog);

		/* set components visible dependent of the users rights */
		try {
			doCheckRights();

			if (arguments.containsKey("blackListReasonCode")) {
				this.blackListReasonCode = (BlackListReasonCode) arguments
						.get("blackListReasonCode");
				BlackListReasonCode befImage = new BlackListReasonCode();
				BeanUtils.copyProperties(this.blackListReasonCode, befImage);
				this.blackListReasonCode.setBefImage(befImage);

				setBlackListReasonCode(this.blackListReasonCode);
			} else {
				setBlackListReasonCode(null);
			}

			doLoadWorkFlow(this.blackListReasonCode.isWorkflow(),
					this.blackListReasonCode.getWorkflowId(),
					this.blackListReasonCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"BlackListReasonCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the blackListReasonCodeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete blackListReasonCode here.
			if (arguments.containsKey("blackListReasonCodeListCtrl")) {
				setBlackListReasonCodeListCtrl((BlackListReasonCodeListCtrl) arguments
						.get("blackListReasonCodeListCtrl"));
			} else {
				setBlackListReasonCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getBlackListReasonCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BlackListReasonCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.bLRsnCode.setMaxlength(8);
		this.bLRsnDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BlackListReasonCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BlackListReasonCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BlackListReasonCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BlackListReasonCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_BlackListReasonCodeDialog);
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
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving");
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
		doWriteBeanToComponents(this.blackListReasonCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBlackListReasonCode
	 *            BlackListReasonCode
	 */
	public void doWriteBeanToComponents(BlackListReasonCode aBlackListReasonCode) {
		logger.debug("Entering");
		this.bLRsnCode.setValue(aBlackListReasonCode.getBLRsnCode());
		this.bLRsnDesc.setValue(aBlackListReasonCode.getBLRsnDesc());
		this.bLIsActive.setChecked(aBlackListReasonCode.isBLIsActive());
		this.recordStatus.setValue(aBlackListReasonCode.getRecordStatus());
		
		if(aBlackListReasonCode.isNew() || (aBlackListReasonCode.getRecordType() != null ? aBlackListReasonCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.bLIsActive.setChecked(true);
			this.bLIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBlackListReasonCode
	 */
	public void doWriteComponentsToBean(BlackListReasonCode aBlackListReasonCode) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aBlackListReasonCode.setBLRsnCode(this.bLRsnCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBlackListReasonCode.setBLRsnDesc(this.bLRsnDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBlackListReasonCode.setBLIsActive(this.bLIsActive.isChecked());
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

		aBlackListReasonCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBlackListReasonCode
	 * @throws Exception
	 */
	public void doShowDialog(BlackListReasonCode aBlackListReasonCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aBlackListReasonCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bLRsnCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.bLRsnDesc.focus();
				if (StringUtils.isNotBlank(aBlackListReasonCode.getRecordType())) {
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
			doWriteBeanToComponents(aBlackListReasonCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BlackListReasonCodeDialog.onClose();
		}catch (Exception e) {
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

		if (!this.bLRsnCode.isReadonly()){
			this.bLRsnCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BlackListReasonCodeDialog_BLRsnCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	

		if (!this.bLRsnDesc.isReadonly()){
			this.bLRsnDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_BlackListReasonCodeDialog_BLRsnDesc.value"), 
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
		this.bLRsnCode.setConstraint("");
		this.bLRsnDesc.setConstraint("");
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
		this.bLRsnCode.setErrorMessage("");
		this.bLRsnDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a BlackListReasonCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final BlackListReasonCode aBlackListReasonCode = new BlackListReasonCode();
		BeanUtils.copyProperties(getBlackListReasonCode(),	aBlackListReasonCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_BlackListReasonCodeDialog_BLRsnCode.value")+ " : "+aBlackListReasonCode.getBLRsnCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aBlackListReasonCode.getRecordType())) {
				aBlackListReasonCode.setVersion(aBlackListReasonCode.getVersion() + 1);
				aBlackListReasonCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBlackListReasonCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBlackListReasonCode, tranType)) {
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

		if (getBlackListReasonCode().isNewRecord()) {
			this.bLRsnCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.bLRsnCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.bLRsnDesc.setReadonly(isReadOnly("BlackListReasonCodeDialog_bLRsnDesc"));
		this.bLIsActive.setDisabled(isReadOnly("BlackListReasonCodeDialog_bLIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.blackListReasonCode.isNewRecord()) {
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

		this.bLRsnCode.setReadonly(true);
		this.bLRsnDesc.setReadonly(true);
		this.bLIsActive.setDisabled(true);

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
		this.bLRsnCode.setValue("");
		this.bLRsnDesc.setValue("");
		this.bLIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final BlackListReasonCode aBlackListReasonCode = new BlackListReasonCode();
		BeanUtils.copyProperties(getBlackListReasonCode(), aBlackListReasonCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the BlackListReasonCode object with the components data
		doWriteComponentsToBean(aBlackListReasonCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBlackListReasonCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBlackListReasonCode.getRecordType())) {
				aBlackListReasonCode.setVersion(aBlackListReasonCode.getVersion() + 1);
				if (isNew) {
					aBlackListReasonCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBlackListReasonCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBlackListReasonCode.setNewRecord(true);
				}
			}
		} else {
			aBlackListReasonCode.setVersion(aBlackListReasonCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aBlackListReasonCode, tranType)) {
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
	 * @param aBlackListReasonCode
	 *            (BlackListReasonCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(BlackListReasonCode aBlackListReasonCode,	String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBlackListReasonCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBlackListReasonCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBlackListReasonCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBlackListReasonCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBlackListReasonCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBlackListReasonCode);
				}

				if (isNotesMandatory(taskId, aBlackListReasonCode)) {
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

			aBlackListReasonCode.setTaskId(taskId);
			aBlackListReasonCode.setNextTaskId(nextTaskId);
			aBlackListReasonCode.setRoleCode(getRole());
			aBlackListReasonCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBlackListReasonCode, tranType);

			String operationRefs = getServiceOperations(taskId, aBlackListReasonCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBlackListReasonCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aBlackListReasonCode, tranType);
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
		BlackListReasonCode aBlackListReasonCode = (BlackListReasonCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getBlackListReasonCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getBlackListReasonCodeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getBlackListReasonCodeService().doApprove(auditHeader);

						if (aBlackListReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBlackListReasonCodeService().doReject(auditHeader);

						if (aBlackListReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel(
						"InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BlackListReasonCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_BlackListReasonCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.blackListReasonCode), true);
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
	 * @param aBlackListReasonCode
	 *            (BlackListReasonCode)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(BlackListReasonCode aBlackListReasonCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aBlackListReasonCode.getBefImage(), aBlackListReasonCode);
		return new AuditHeader(String.valueOf(aBlackListReasonCode.getId()),
				null, null, null, auditDetail, aBlackListReasonCode.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_BlackListReasonCodeDialog, auditHeader);
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
		doShowNotes(this.blackListReasonCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getBlackListReasonCodeListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.blackListReasonCode.getBLRsnCode());
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

	public BlackListReasonCode getBlackListReasonCode() {
		return this.blackListReasonCode;
	}
	public void setBlackListReasonCode(BlackListReasonCode blackListReasonCode) {
		this.blackListReasonCode = blackListReasonCode;
	}

	public void setBlackListReasonCodeService(BlackListReasonCodeService blackListReasonCodeService) {
		this.blackListReasonCodeService = blackListReasonCodeService;
	}
	public BlackListReasonCodeService getBlackListReasonCodeService() {
		return this.blackListReasonCodeService;
	}

	public void setBlackListReasonCodeListCtrl(BlackListReasonCodeListCtrl blackListReasonCodeListCtrl) {
		this.blackListReasonCodeListCtrl = blackListReasonCodeListCtrl;
	}
	public BlackListReasonCodeListCtrl getBlackListReasonCodeListCtrl() {
		return this.blackListReasonCodeListCtrl;
	}

}
