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
 * FileName    		:  AccountEngineEventDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.bmtmasters.accountengineevent;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.AccountEngineEventService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/AccountEngineEvent/accountEngineEventDialog.zul
 * file.
 */
public class AccountEngineEventDialogCtrl extends GFCBaseCtrl<AccountEngineEvent> {
	private static final long serialVersionUID = -5231127902551957898L;
	private static final Logger logger = Logger.getLogger(AccountEngineEventDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_AccountEngineEventDialog;	
	protected Textbox 		aEEventCode; 						
	protected Textbox 		aEEventCodeDesc; 					

	// not auto wired Var's
	private AccountEngineEvent accountEngineEvent; 			// overHanded per parameter
	private transient AccountEngineEventListCtrl accountEngineEventListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient AccountEngineEventService accountEngineEventService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public AccountEngineEventDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AccountEngineEventDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountEngineEvent
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountEngineEventDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AccountEngineEventDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();
			if (arguments.containsKey("accountEngineEvent")) {
				this.accountEngineEvent = (AccountEngineEvent) arguments
						.get("accountEngineEvent");
				AccountEngineEvent befImage = new AccountEngineEvent();
				BeanUtils.copyProperties(this.accountEngineEvent, befImage);
				this.accountEngineEvent.setBefImage(befImage);

				setAccountEngineEvent(this.accountEngineEvent);
			} else {
				setAccountEngineEvent(null);
			}

			doLoadWorkFlow(this.accountEngineEvent.isWorkflow(),
					this.accountEngineEvent.getWorkflowId(),
					this.accountEngineEvent.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"AccountEngineEventDialog");
			}

			// READ OVERHANDED parameters !
			// we get the accountEngineEventListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete accountEngineEvent here.
			if (arguments.containsKey("accountEngineEventListCtrl")) {
				setAccountEngineEventListCtrl((AccountEngineEventListCtrl) arguments
						.get("accountEngineEventListCtrl"));
			} else {
				setAccountEngineEventListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getAccountEngineEvent());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AccountEngineEventDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.aEEventCode.setMaxlength(8);
		this.aEEventCodeDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountEngineEventDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountEngineEventDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AccountEngineEventDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountEngineEventDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_AccountEngineEventDialog);
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
		doWriteBeanToComponents(this.accountEngineEvent.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountEngineEvent
	 *            AccountEngineEvent
	 */
	public void doWriteBeanToComponents(AccountEngineEvent aAccountEngineEvent) {
		logger.debug("Entering");
		this.aEEventCode.setValue(aAccountEngineEvent.getAEEventCode());
		this.aEEventCodeDesc.setValue(aAccountEngineEvent.getAEEventCodeDesc());
		this.recordStatus.setValue(aAccountEngineEvent.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccountEngineEvent
	 */
	public void doWriteComponentsToBean(AccountEngineEvent aAccountEngineEvent) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAccountEngineEvent.setAEEventCode(this.aEEventCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountEngineEvent.setAEEventCodeDesc(this.aEEventCodeDesc.getValue());
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

		aAccountEngineEvent.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAccountEngineEvent
	 * @throws Exception
	 */
	public void doShowDialog(AccountEngineEvent aAccountEngineEvent) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aAccountEngineEvent.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.aEEventCode.focus();
		} else {
			this.aEEventCodeDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aAccountEngineEvent);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AccountEngineEventDialog.onClose();
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

		if (!this.aEEventCode.isReadonly()){
			this.aEEventCode.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountEngineEventDialog_AEEventCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	

		if (!this.aEEventCodeDesc.isReadonly()){
			this.aEEventCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountEngineEventDialog_AEEventCodeDesc.value"), 
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
		this.aEEventCode.setConstraint("");
		this.aEEventCodeDesc.setConstraint("");
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
		this.aEEventCode.setErrorMessage("");
		this.aEEventCodeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a AccountEngineEvent object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final AccountEngineEvent aAccountEngineEvent = new AccountEngineEvent();
		BeanUtils.copyProperties(getAccountEngineEvent(), aAccountEngineEvent);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aAccountEngineEvent.getAEEventCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aAccountEngineEvent.getRecordType())) {
				aAccountEngineEvent.setVersion(aAccountEngineEvent.getVersion() + 1);
				aAccountEngineEvent.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAccountEngineEvent.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aAccountEngineEvent, tranType)) {
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
		if (getAccountEngineEvent().isNewRecord()) {
			this.aEEventCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.aEEventCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.aEEventCodeDesc.setReadonly(isReadOnly("AccountEngineEventDialog_aEEventCodeDesc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.accountEngineEvent.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.aEEventCode.setReadonly(true);
		this.aEEventCodeDesc.setReadonly(true);

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
		this.aEEventCode.setValue("");
		this.aEEventCodeDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final AccountEngineEvent aAccountEngineEvent = new AccountEngineEvent();
		BeanUtils.copyProperties(getAccountEngineEvent(), aAccountEngineEvent);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the AccountEngineEvent object with the components data
		doWriteComponentsToBean(aAccountEngineEvent);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aAccountEngineEvent.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAccountEngineEvent.getRecordType())) {
				aAccountEngineEvent.setVersion(aAccountEngineEvent.getVersion() + 1);
				if (isNew) {
					aAccountEngineEvent.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAccountEngineEvent.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAccountEngineEvent.setNewRecord(true);
				}
			}
		} else {
			aAccountEngineEvent.setVersion(aAccountEngineEvent.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aAccountEngineEvent, tranType)) {
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
	 * @param aAccountEngineEvent
	 *            (AccountEngineEvent)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(AccountEngineEvent aAccountEngineEvent, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAccountEngineEvent.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAccountEngineEvent.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountEngineEvent.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAccountEngineEvent.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountEngineEvent.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAccountEngineEvent);
				}

				if (isNotesMandatory(taskId, aAccountEngineEvent)) {
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

			aAccountEngineEvent.setTaskId(taskId);
			aAccountEngineEvent.setNextTaskId(nextTaskId);
			aAccountEngineEvent.setRoleCode(getRole());
			aAccountEngineEvent.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountEngineEvent, tranType);

			String operationRefs = getServiceOperations(taskId, aAccountEngineEvent);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAccountEngineEvent, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAccountEngineEvent, tranType);
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
		AccountEngineEvent aAccountEngineEvent = (AccountEngineEvent) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getAccountEngineEventService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getAccountEngineEventService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getAccountEngineEventService().doApprove(auditHeader);

						if (aAccountEngineEvent.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getAccountEngineEventService().doReject(auditHeader);

						if (aAccountEngineEvent.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AccountEngineEventDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_AccountEngineEventDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.accountEngineEvent), true);
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
	 * @param aAccountEngineEvent
	 *            (AccountEngineEvent)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(AccountEngineEvent aAccountEngineEvent, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountEngineEvent.getBefImage(), aAccountEngineEvent);
		return new AuditHeader(String.valueOf(aAccountEngineEvent.getId()),
				null, null, null, auditDetail,aAccountEngineEvent.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_AccountEngineEventDialog, auditHeader);
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
		doShowNotes(this.accountEngineEvent);
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<AccountEngineEvent> soAccountEngineEvent = getAccountEngineEventListCtrl().getSearchObj();
		getAccountEngineEventListCtrl().pagingAccountEngineEventList.setActivePage(0);
		getAccountEngineEventListCtrl().getPagedListWrapper().setSearchObject(soAccountEngineEvent);
		if (getAccountEngineEventListCtrl().listBoxAccountEngineEvent != null) {
			getAccountEngineEventListCtrl().listBoxAccountEngineEvent.getListModel();
		}
		logger.debug("Leaving");
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.accountEngineEvent.getAEEventCode());
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

	public AccountEngineEvent getAccountEngineEvent() {
		return this.accountEngineEvent;
	}
	public void setAccountEngineEvent(AccountEngineEvent accountEngineEvent) {
		this.accountEngineEvent = accountEngineEvent;
	}

	public void setAccountEngineEventService(AccountEngineEventService accountEngineEventService) {
		this.accountEngineEventService = accountEngineEventService;
	}
	public AccountEngineEventService getAccountEngineEventService() {
		return this.accountEngineEventService;
	}

	public void setAccountEngineEventListCtrl(AccountEngineEventListCtrl accountEngineEventListCtrl) {
		this.accountEngineEventListCtrl = accountEngineEventListCtrl;
	}
	public AccountEngineEventListCtrl getAccountEngineEventListCtrl() {
		return this.accountEngineEventListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
