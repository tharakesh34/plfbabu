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
 * FileName    		:  ScheduleMethodDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.staticparms.schedulemethod;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

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

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.service.staticparms.ScheduleMethodService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/ScheduleMethod/scheduleMethodDialog.zul file.
 */
public class ScheduleMethodDialogCtrl extends GFCBaseCtrl<ScheduleMethod> {
	private static final long serialVersionUID = 8452766351018963227L;
	private static final Logger logger = Logger.getLogger(ScheduleMethodDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_ScheduleMethodDialog; 	// autoWired
	protected Textbox 	schdMethod; 					// autoWired
	protected Textbox 	schdMethodDesc; 				// autoWired
	

	// not auto wired variables
	private ScheduleMethod scheduleMethod; 								// overHanded per parameter
	private transient ScheduleMethodListCtrl scheduleMethodListCtrl; 	// overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient ScheduleMethodService scheduleMethodService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public ScheduleMethodDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ScheduleMethodDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ScheduleMethod object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScheduleMethodDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScheduleMethodDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("scheduleMethod")) {
				this.scheduleMethod = (ScheduleMethod) arguments
						.get("scheduleMethod");
				ScheduleMethod befImage = new ScheduleMethod();
				BeanUtils.copyProperties(this.scheduleMethod, befImage);
				this.scheduleMethod.setBefImage(befImage);

				setScheduleMethod(this.scheduleMethod);
			} else {
				setScheduleMethod(null);
			}

			doLoadWorkFlow(this.scheduleMethod.isWorkflow(),
					this.scheduleMethod.getWorkflowId(),
					this.scheduleMethod.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"ScheduleMethodDialog");
			}

			// READ OVERHANDED parameters !
			// we get the scheduleMethodListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete scheduleMethod here.
			if (arguments.containsKey("scheduleMethodListCtrl")) {
				setScheduleMethodListCtrl((ScheduleMethodListCtrl) arguments
						.get("scheduleMethodListCtrl"));
			} else {
				setScheduleMethodListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getScheduleMethod());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ScheduleMethodDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.schdMethod.setMaxlength(8);
		this.schdMethodDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ScheduleMethodDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ScheduleMethodDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ScheduleMethodDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ScheduleMethodDialog_btnSave"));
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
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_ScheduleMethodDialog);
		logger.debug("Leaving");
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
		doWriteBeanToComponents(this.scheduleMethod.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aScheduleMethod
	 *            ScheduleMethod
	 */
	public void doWriteBeanToComponents(ScheduleMethod aScheduleMethod) {
		logger.debug("Entering");
		this.schdMethod.setValue(aScheduleMethod.getSchdMethod());
		this.schdMethodDesc.setValue(aScheduleMethod.getSchdMethodDesc());
		this.recordStatus.setValue(aScheduleMethod.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aScheduleMethod
	 */
	public void doWriteComponentsToBean(ScheduleMethod aScheduleMethod) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aScheduleMethod.setSchdMethod(this.schdMethod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aScheduleMethod.setSchdMethodDesc(this.schdMethodDesc.getValue());
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

		aScheduleMethod.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aScheduleMethod
	 * @throws Exception
	 */
	public void doShowDialog(ScheduleMethod aScheduleMethod)throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aScheduleMethod.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.schdMethod.focus();
		} else {
			this.schdMethodDesc.focus();
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
			doWriteBeanToComponents(aScheduleMethod);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ScheduleMethodDialog.onClose();
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

		if (!this.schdMethod.isReadonly()){
			this.schdMethod.setConstraint(new PTStringValidator(Labels.getLabel("label_ScheduleMethodDialog_SchdMethod.value"),PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}	
		if (!this.schdMethodDesc.isReadonly()){
			this.schdMethodDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_ScheduleMethodDialog_SchdMethodDesc.value"), 
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
		this.schdMethod.setConstraint("");
		this.schdMethodDesc.setConstraint("");
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
		this.schdMethod.setErrorMessage("");
		this.schdMethodDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a ScheduleMethod object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final ScheduleMethod aScheduleMethod = new ScheduleMethod();
		BeanUtils.copyProperties(getScheduleMethod(), aScheduleMethod);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_ScheduleMethodDialog_SchdMethod.value")+" : "+aScheduleMethod.getSchdMethod();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aScheduleMethod.getRecordType())) {
				aScheduleMethod.setVersion(aScheduleMethod.getVersion() + 1);
				aScheduleMethod.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aScheduleMethod.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aScheduleMethod, tranType)) {
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

		if (getScheduleMethod().isNewRecord()) {
			this.schdMethod.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.schdMethod.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.schdMethodDesc.setReadonly(isReadOnly("ScheduleMethodDialog_schdMethodDesc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.scheduleMethod.isNewRecord()) {
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

		this.schdMethod.setReadonly(true);
		this.schdMethodDesc.setReadonly(true);

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
		this.schdMethod.setValue("");
		this.schdMethodDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final ScheduleMethod aScheduleMethod = new ScheduleMethod();
		BeanUtils.copyProperties(getScheduleMethod(), aScheduleMethod);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ScheduleMethod object with the components data
		doWriteComponentsToBean(aScheduleMethod);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aScheduleMethod.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aScheduleMethod.getRecordType())) {
				aScheduleMethod.setVersion(aScheduleMethod.getVersion() + 1);
				if (isNew) {
					aScheduleMethod
					.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aScheduleMethod
					.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aScheduleMethod.setNewRecord(true);
				}
			}
		} else {
			aScheduleMethod.setVersion(aScheduleMethod.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aScheduleMethod, tranType)) {
				doWriteBeanToComponents(aScheduleMethod);
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
	 * @param aScheduleMethod
	 *            (ScheduleMethod)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ScheduleMethod aScheduleMethod, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aScheduleMethod.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aScheduleMethod.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aScheduleMethod.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aScheduleMethod.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aScheduleMethod.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aScheduleMethod);
				}
				if (isNotesMandatory(taskId, aScheduleMethod)) {
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

			aScheduleMethod.setTaskId(taskId);
			aScheduleMethod.setNextTaskId(nextTaskId);
			aScheduleMethod.setRoleCode(getRole());
			aScheduleMethod.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aScheduleMethod, tranType);

			String operationRefs = getServiceOperations(taskId, aScheduleMethod);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aScheduleMethod,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aScheduleMethod, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;

		ScheduleMethod aScheduleMethod = (ScheduleMethod) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getScheduleMethodService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getScheduleMethodService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getScheduleMethodService().doApprove(auditHeader);
						if (aScheduleMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getScheduleMethodService().doReject(auditHeader);
						if (aScheduleMethod.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_ScheduleMethodDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ScheduleMethodDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.scheduleMethod), true);
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
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aScheduleMethod
	 *            (ScheduleMethod)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(ScheduleMethod aScheduleMethod,String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aScheduleMethod.getBefImage(), aScheduleMethod);
		return new AuditHeader(aScheduleMethod.getSchdMethod(), null, null,
				null, auditDetail, aScheduleMethod.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_ScheduleMethodDialog,auditHeader);
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
		doShowNotes(this.scheduleMethod);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getScheduleMethodListCtrl().search();
	}
	
	
	@Override
	protected String getReference() {
		return String.valueOf(this.scheduleMethod.getSchdMethod());
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

	public ScheduleMethod getScheduleMethod() {
		return this.scheduleMethod;
	}
	public void setScheduleMethod(ScheduleMethod scheduleMethod) {
		this.scheduleMethod = scheduleMethod;
	}

	public void setScheduleMethodService(ScheduleMethodService scheduleMethodService) {
		this.scheduleMethodService = scheduleMethodService;
	}
	public ScheduleMethodService getScheduleMethodService() {
		return this.scheduleMethodService;
	}

	public void setScheduleMethodListCtrl(ScheduleMethodListCtrl scheduleMethodListCtrl) {
		this.scheduleMethodListCtrl = scheduleMethodListCtrl;
	}
	public ScheduleMethodListCtrl getScheduleMethodListCtrl() {
		return this.scheduleMethodListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
}
