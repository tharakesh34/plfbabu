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
 * FileName    		:  CorpRelationCodeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.corprelationcode;

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
import com.pennant.backend.model.applicationmaster.CorpRelationCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.CorpRelationCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CorpRelationCode/corpRelationCodeDialog.zul
 * file.
 */
public class CorpRelationCodeDialogCtrl extends GFCBaseCtrl<CorpRelationCode> {
	private static final long serialVersionUID = 466858039679509098L;
	private static final Logger logger = Logger.getLogger(CorpRelationCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CorpRelationCodeDialog; // autoWired

	protected Textbox 		corpRelationCode; 		// autoWired
	protected Textbox 		corpRelationDesc; 		// autoWired
	protected Checkbox 		corpRelationIsActive; 	// autoWired

	// not autoWired variables
	private CorpRelationCode mCorpRelationCode; 	// over handed per parameters
	private transient CorpRelationCodeListCtrl corpRelationCodeListCtrl; // overHanded per parameters

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient CorpRelationCodeService corpRelationCodeService;

	/**
	 * default constructor.<br>
	 */
	public CorpRelationCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CorpRelationCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CorpRelationCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CorpRelationCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CorpRelationCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("corpRelationCode")) {
				this.mCorpRelationCode = (CorpRelationCode) arguments.get("corpRelationCode");
				CorpRelationCode befImage = new CorpRelationCode();
				BeanUtils.copyProperties(this.mCorpRelationCode, befImage);
				this.mCorpRelationCode.setBefImage(befImage);

				setMCorpRelationCode(this.mCorpRelationCode);
			} else {
				setMCorpRelationCode(null);
			}

			doLoadWorkFlow(this.mCorpRelationCode.isWorkflow(),
					this.mCorpRelationCode.getWorkflowId(),
					this.mCorpRelationCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CorpRelationCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the corpRelationCodeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete corpRelationCode here.
			if (arguments.containsKey("corpRelationCodeListCtrl")) {
				setCorpRelationCodeListCtrl((CorpRelationCodeListCtrl) arguments.get("corpRelationCodeListCtrl"));
			} else {
				setCorpRelationCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMCorpRelationCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CorpRelationCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.corpRelationCode.setMaxlength(8);
		this.corpRelationDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CorpRelationCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CorpRelationCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CorpRelationCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CorpRelationCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CorpRelationCodeDialog);
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
		doWriteBeanToComponents(this.mCorpRelationCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCorpRelationCode
	 *            CorpRelationCode
	 */
	public void doWriteBeanToComponents(CorpRelationCode aCorpRelationCode) {
		logger.debug("Entering");
		this.corpRelationCode.setValue(aCorpRelationCode.getCorpRelationCode());
		this.corpRelationDesc.setValue(aCorpRelationCode.getCorpRelationDesc());
		this.corpRelationIsActive.setChecked(aCorpRelationCode.isCorpRelationIsActive());
		this.recordStatus.setValue(aCorpRelationCode.getRecordStatus());
		
		if(aCorpRelationCode.isNew() || (aCorpRelationCode.getRecordType() != null ? aCorpRelationCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.corpRelationIsActive.setChecked(true);
			this.corpRelationIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCorpRelationCode
	 */
	public void doWriteComponentsToBean(CorpRelationCode aCorpRelationCode) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCorpRelationCode.setCorpRelationCode(this.corpRelationCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCorpRelationCode.setCorpRelationDesc(this.corpRelationDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCorpRelationCode.setCorpRelationIsActive(this.corpRelationIsActive.isChecked());
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

		aCorpRelationCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCorpRelationCode
	 * @throws Exception
	 */
	public void doShowDialog(CorpRelationCode aCorpRelationCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aCorpRelationCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.corpRelationCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.corpRelationDesc.focus();
				if (StringUtils.isNotBlank(aCorpRelationCode.getRecordType())) {
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
			doWriteBeanToComponents(aCorpRelationCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CorpRelationCodeDialog.onClose();
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

		if (!this.corpRelationCode.isReadonly()){
			this.corpRelationCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CorpRelationCodeDialog_CorpRelationCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.corpRelationDesc.isReadonly()){
			this.corpRelationDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CorpRelationCodeDialog_CorpRelationDesc.value"), 
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
		this.corpRelationCode.setConstraint("");
		this.corpRelationDesc.setConstraint("");
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
		this.corpRelationCode.setErrorMessage("");
		this.corpRelationDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a CorpRelationCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CorpRelationCode aCorpRelationCode = new CorpRelationCode();
		BeanUtils.copyProperties(getMCorpRelationCode(), aCorpRelationCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
		Labels.getLabel("label_CorpRelationCodeDialog_CorpRelationCode.value")+" : "+aCorpRelationCode.getCorpRelationCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCorpRelationCode.getRecordType())) {
				aCorpRelationCode.setVersion(aCorpRelationCode.getVersion() + 1);
				aCorpRelationCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCorpRelationCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aCorpRelationCode, tranType)) {
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

		if (getMCorpRelationCode().isNewRecord()) {
			this.corpRelationCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.corpRelationCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.corpRelationDesc.setReadonly(isReadOnly("CorpRelationCodeDialog_corpRelationDesc"));
		this.corpRelationIsActive.setDisabled(isReadOnly("CorpRelationCodeDialog_corpRelationIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.mCorpRelationCode.isNewRecord()) {
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

		this.corpRelationCode.setReadonly(true);
		this.corpRelationDesc.setReadonly(true);
		this.corpRelationIsActive.setDisabled(true);

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
		this.corpRelationCode.setValue("");
		this.corpRelationDesc.setValue("");
		this.corpRelationIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CorpRelationCode aCorpRelationCode = new CorpRelationCode();
		BeanUtils.copyProperties(getMCorpRelationCode(), aCorpRelationCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CorpRelationCode object with the components data
		doWriteComponentsToBean(aCorpRelationCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCorpRelationCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCorpRelationCode.getRecordType())) {
				aCorpRelationCode.setVersion(aCorpRelationCode.getVersion() + 1);
				if (isNew) {
					aCorpRelationCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCorpRelationCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCorpRelationCode.setNewRecord(true);
				}
			}
		} else {
			aCorpRelationCode.setVersion(aCorpRelationCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aCorpRelationCode, tranType)) {
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
	 * @param aCorpRelationCode
	 *            (CorpRelationCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CorpRelationCode aCorpRelationCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCorpRelationCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCorpRelationCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCorpRelationCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCorpRelationCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCorpRelationCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCorpRelationCode);
				}

				if (isNotesMandatory(taskId, aCorpRelationCode)) {
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

			aCorpRelationCode.setTaskId(taskId);
			aCorpRelationCode.setNextTaskId(nextTaskId);
			aCorpRelationCode.setRoleCode(getRole());
			aCorpRelationCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCorpRelationCode, tranType);

			String operationRefs = getServiceOperations(taskId, aCorpRelationCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCorpRelationCode,	PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCorpRelationCode, tranType);
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
		CorpRelationCode aCorpRelationCode = (CorpRelationCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCorpRelationCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCorpRelationCodeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCorpRelationCodeService().doApprove(auditHeader);

						if (aCorpRelationCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCorpRelationCodeService().doReject(auditHeader);

						if (aCorpRelationCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CorpRelationCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CorpRelationCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.mCorpRelationCode), true);
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
	 * @param aCorpRelationCode
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CorpRelationCode aCorpRelationCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCorpRelationCode.getBefImage(), aCorpRelationCode);
		return new AuditHeader(String.valueOf(aCorpRelationCode.getId()), null,
				null, null, auditDetail, aCorpRelationCode.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CorpRelationCodeDialog, auditHeader);
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
		doShowNotes(this.mCorpRelationCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCorpRelationCodeListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.mCorpRelationCode.getCorpRelationCode());
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

	public CorpRelationCode getMCorpRelationCode() {
		return this.mCorpRelationCode;
	}
	public void setMCorpRelationCode(CorpRelationCode mCorpRelationCode) {
		this.mCorpRelationCode = mCorpRelationCode;
	}

	public void setCorpRelationCodeService(CorpRelationCodeService corpRelationCodeService) {
		this.corpRelationCodeService = corpRelationCodeService;
	}
	public CorpRelationCodeService getCorpRelationCodeService() {
		return this.corpRelationCodeService;
	}

	public void setCorpRelationCodeListCtrl(CorpRelationCodeListCtrl corpRelationCodeListCtrl) {
		this.corpRelationCodeListCtrl = corpRelationCodeListCtrl;
	}
	public CorpRelationCodeListCtrl getCorpRelationCodeListCtrl() {
		return this.corpRelationCodeListCtrl;
	}

}
