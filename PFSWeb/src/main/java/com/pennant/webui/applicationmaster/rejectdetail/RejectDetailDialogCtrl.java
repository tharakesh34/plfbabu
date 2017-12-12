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
 * FileName    		:  RejectDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.rejectdetail;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.RejectDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/RejectDetail/rejectDetailDialog.zul file.
 */
public class RejectDetailDialogCtrl extends GFCBaseCtrl<RejectDetail> {
	private static final long serialVersionUID = -2229794581795422226L;
	private static final Logger logger = Logger.getLogger(RejectDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RejectDetailDialog; // autoWired
	protected Textbox rejectCode; // autoWired
	protected Textbox rejectDesc; // autoWired
	protected Checkbox rejectIsActive; // autoWired
	protected Combobox rejectType; // autoWired

	// not auto wired variables
	private RejectDetail rejectDetail; // overHanded per parameter
	private transient RejectDetailListCtrl rejectDetailListCtrl; // overHanded
																	// per
	// Button controller for the CRUD buttons
	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient RejectDetailService rejectDetailService;

	/**
	 * default constructor.<br>
	 */
	public RejectDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "RejectDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected RejectDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RejectDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RejectDetailDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("rejectDetail")) {
				this.rejectDetail = (RejectDetail) arguments.get("rejectDetail");
				RejectDetail befImage = new RejectDetail();
				BeanUtils.copyProperties(this.rejectDetail, befImage);
				this.rejectDetail.setBefImage(befImage);
				setRejectDetail(this.rejectDetail);
			} else {
				setRejectDetail(null);
			}

			doLoadWorkFlow(this.rejectDetail.isWorkflow(), this.rejectDetail.getWorkflowId(),
					this.rejectDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "RejectDetailDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the rejectDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete rejectDetail here.
			if (arguments.containsKey("rejectDetailListCtrl")) {
				setRejectDetailListCtrl((RejectDetailListCtrl) arguments.get("rejectDetailListCtrl"));
			} else {
				setRejectDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getRejectDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RejectDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.rejectCode.setMaxlength(8);
		this.rejectDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RejectDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RejectDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RejectDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RejectDetailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_RejectDetailDialog);
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
		doWriteBeanToComponents(this.rejectDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aRejectDetail
	 *            RejectDetail
	 */
	public void doWriteBeanToComponents(RejectDetail aRejectDetail) {
		logger.debug("Entering");
		this.rejectCode.setValue(aRejectDetail.getRejectCode());
		this.rejectDesc.setValue(aRejectDetail.getRejectDesc());
		this.rejectIsActive.setChecked(aRejectDetail.isRejectIsActive());
		fillComboBox(this.rejectType,aRejectDetail.getRejectType(),PennantStaticListUtil.getRejectTypeList(),"");
		
		this.recordStatus.setValue(aRejectDetail.getRecordStatus());

		if (aRejectDetail.isNew()
				|| (aRejectDetail.getRecordType() != null ? aRejectDetail.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.rejectIsActive.setChecked(true);
			this.rejectIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRejectDetail
	 */
	public void doWriteComponentsToBean(RejectDetail aRejectDetail) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aRejectDetail.setRejectCode(this.rejectCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRejectDetail.setRejectDesc(this.rejectDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRejectDetail.setRejectIsActive(this.rejectIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRejectDetail.setRejectType(this.rejectType.getSelectedItem().getValue().toString());
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

		aRejectDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aRejectDetail
	 * @throws Exception
	 */
	public void doShowDialog(RejectDetail aRejectDetail) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aRejectDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.rejectCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.rejectDesc.focus();
				if (StringUtils.isNotBlank(aRejectDetail.getRecordType())) {
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
			doWriteBeanToComponents(aRejectDetail);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_RejectDetailDialog.onClose();
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

		if (!this.rejectCode.isReadonly()) {
			this.rejectCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_RejectDetailDialog_RejectCode.value"), PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM,
					true));
		}
		if (!this.rejectDesc.isReadonly()) {
			this.rejectDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_RejectDetailDialog_RejectDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		// Reject Type
		if (!this.rejectType.isDisabled()) {
			this.rejectType.setConstraint(new StaticListValidator(PennantStaticListUtil.getRejectTypeList(), Labels.getLabel("label_RejectDetailDialog_RejectType.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.rejectCode.setConstraint("");
		this.rejectDesc.setConstraint("");
		this.rejectType.setConstraint("");
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
		this.rejectCode.setErrorMessage("");
		this.rejectDesc.setErrorMessage("");
		this.rejectType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a RejectDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final RejectDetail aRejectDetail = new RejectDetail();
		BeanUtils.copyProperties(getRejectDetail(), aRejectDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_RejectDetailDialog_RejectCode.value") + " : " + aRejectDetail.getRejectCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aRejectDetail.getRecordType())) {
				aRejectDetail.setVersion(aRejectDetail.getVersion() + 1);
				aRejectDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aRejectDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aRejectDetail, tranType)) {
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

		if (getRejectDetail().isNewRecord()) {
			this.rejectCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.rejectCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.rejectDesc.setReadonly(isReadOnly("RejectDetailDialog_rejectDesc"));
		this.rejectIsActive.setDisabled(isReadOnly("RejectDetailDialog_rejectIsActive"));
		this.rejectType.setDisabled(isReadOnly("RejectDetailDialog_RejectType"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.rejectDetail.isNewRecord()) {
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

		this.rejectCode.setReadonly(true);
		this.rejectDesc.setReadonly(true);
		this.rejectIsActive.setDisabled(true);
		this.rejectType.setDisabled(true);

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
		this.rejectCode.setValue("");
		this.rejectDesc.setValue("");
		this.rejectIsActive.setChecked(false);
		this.rejectType.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final RejectDetail aRejectDetail = new RejectDetail();
		BeanUtils.copyProperties(getRejectDetail(), aRejectDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the RejectDetail object with the components data
		doWriteComponentsToBean(aRejectDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aRejectDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aRejectDetail.getRecordType())) {
				aRejectDetail.setVersion(aRejectDetail.getVersion() + 1);
				if (isNew) {
					aRejectDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aRejectDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aRejectDetail.setNewRecord(true);
				}
			}
		} else {
			aRejectDetail.setVersion(aRejectDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aRejectDetail, tranType)) {
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
	 * @param aRejectDetail
	 *            (RejectDetail)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(RejectDetail aRejectDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aRejectDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aRejectDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRejectDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aRejectDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRejectDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aRejectDetail);
				}
				if (isNotesMandatory(taskId, aRejectDetail)) {
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

			aRejectDetail.setTaskId(taskId);
			aRejectDetail.setNextTaskId(nextTaskId);
			aRejectDetail.setRoleCode(getRole());
			aRejectDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRejectDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aRejectDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aRejectDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aRejectDetail, tranType);
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
		RejectDetail aRejectDetail = (RejectDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getRejectDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getRejectDetailService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getRejectDetailService().doApprove(auditHeader);

						if (aRejectDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getRejectDetailService().doReject(auditHeader);

						if (aRejectDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RejectDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RejectDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.rejectDetail), true);
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

	// WorkFlow Details

	/**
	 * Get Audit Header Details
	 * 
	 * @param aRejectDetail
	 *            (RejectDetail)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(RejectDetail aRejectDetail, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aRejectDetail.getBefImage(), aRejectDetail);
		return new AuditHeader(String.valueOf(aRejectDetail.getId()), null, null, null, auditDetail,
				aRejectDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RejectDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
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
		doShowNotes(this.rejectDetail);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getRejectDetailListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.rejectDetail.getRejectCode());
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

	public RejectDetail getRejectDetail() {
		return this.rejectDetail;
	}

	public void setRejectDetail(RejectDetail rejectDetail) {
		this.rejectDetail = rejectDetail;
	}

	public void setRejectDetailService(RejectDetailService rejectDetailService) {
		this.rejectDetailService = rejectDetailService;
	}

	public RejectDetailService getRejectDetailService() {
		return this.rejectDetailService;
	}

	public void setRejectDetailListCtrl(RejectDetailListCtrl rejectDetailListCtrl) {
		this.rejectDetailListCtrl = rejectDetailListCtrl;
	}

	public RejectDetailListCtrl getRejectDetailListCtrl() {
		return this.rejectDetailListCtrl;
	}
}
