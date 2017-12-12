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
 * FileName    		:  GroupStatusCodeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.groupstatuscode;

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
import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.service.systemmasters.GroupStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/GroupStatusCode/groupStatusCodeDialog.zul file.
 */
public class GroupStatusCodeDialogCtrl extends GFCBaseCtrl<GroupStatusCode> {
	private static final long serialVersionUID = 3163745278891119377L;
	private static final Logger logger = Logger.getLogger(GroupStatusCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_GroupStatusCodeDialog; 	

	protected Textbox 		grpStsCode; 					
	protected Textbox 		grpStsDescription; 				
	protected Checkbox 		grpStsIsActive; 				

	// not autoWired variables
	private GroupStatusCode groupStatusCode; // over handed per parameter
	private transient GroupStatusCodeListCtrl groupStatusCodeListCtrl; // over handed per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient GroupStatusCodeService groupStatusCodeService;

	/**
	 * default constructor.<br>
	 */
	public GroupStatusCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "GroupStatusCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected GroupStatusCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GroupStatusCodeDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_GroupStatusCodeDialog);

		try {
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("groupStatusCode")) {
				this.groupStatusCode = (GroupStatusCode) arguments
						.get("groupStatusCode");
				GroupStatusCode befImage = new GroupStatusCode();
				BeanUtils.copyProperties(this.groupStatusCode, befImage);
				this.groupStatusCode.setBefImage(befImage);
				setGroupStatusCode(this.groupStatusCode);
			} else {
				setGroupStatusCode(null);
			}

			doLoadWorkFlow(this.groupStatusCode.isWorkflow(),
					this.groupStatusCode.getWorkflowId(),
					this.groupStatusCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"GroupStatusCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the groupStatusCodeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete groupStatusCode here.
			if (arguments.containsKey("groupStatusCodeListCtrl")) {
				setGroupStatusCodeListCtrl((GroupStatusCodeListCtrl) arguments
						.get("groupStatusCodeListCtrl"));
			} else {
				setGroupStatusCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getGroupStatusCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_GroupStatusCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.grpStsCode.setMaxlength(8);
		this.grpStsDescription.setMaxlength(50);

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
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_GroupStatusCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_GroupStatusCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_GroupStatusCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_GroupStatusCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_GroupStatusCodeDialog);
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
		doWriteBeanToComponents(this.groupStatusCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aGroupStatusCode
	 *            GroupStatusCode
	 */
	public void doWriteBeanToComponents(GroupStatusCode aGroupStatusCode) {
		logger.debug("Entering");
		this.grpStsCode.setValue(aGroupStatusCode.getGrpStsCode());
		this.grpStsDescription.setValue(aGroupStatusCode.getGrpStsDescription());
		this.grpStsIsActive.setChecked(aGroupStatusCode.isGrpStsIsActive());
		this.recordStatus.setValue(aGroupStatusCode.getRecordStatus());
		
		if(aGroupStatusCode.isNew() || (aGroupStatusCode.getRecordType() != null ? aGroupStatusCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.grpStsIsActive.setChecked(true);
			this.grpStsIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aGroupStatusCode
	 */
	public void doWriteComponentsToBean(GroupStatusCode aGroupStatusCode) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aGroupStatusCode.setGrpStsCode(this.grpStsCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aGroupStatusCode.setGrpStsDescription(this.grpStsDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aGroupStatusCode.setGrpStsIsActive(this.grpStsIsActive.isChecked());
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

		aGroupStatusCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aGroupStatusCode
	 * @throws Exception
	 */
	public void doShowDialog(GroupStatusCode aGroupStatusCode)throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aGroupStatusCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.grpStsCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.grpStsDescription.focus();
				if (StringUtils.isNotBlank(aGroupStatusCode.getRecordType())) {
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
			doWriteBeanToComponents(aGroupStatusCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_GroupStatusCodeDialog.onClose();
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

		if (!this.grpStsCode.isReadonly()){
			this.grpStsCode.setConstraint(new PTStringValidator(Labels.getLabel("label_GroupStatusCodeDialog_GrpStsCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	
		if (!this.grpStsDescription.isReadonly()){
			this.grpStsDescription.setConstraint(new PTStringValidator(Labels.getLabel("label_GroupStatusCodeDialog_GrpStsDescription.value"), 
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
		this.grpStsCode.setConstraint("");
		this.grpStsDescription.setConstraint("");
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
		this.grpStsCode.setErrorMessage("");
		this.grpStsDescription.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a GroupStatusCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final GroupStatusCode aGroupStatusCode = new GroupStatusCode();
		BeanUtils.copyProperties(getGroupStatusCode(), aGroupStatusCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
		Labels.getLabel("label_GroupStatusCodeDialog_GrpStsCode.value")+" : "+aGroupStatusCode.getGrpStsCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aGroupStatusCode.getRecordType())) {
				aGroupStatusCode.setVersion(aGroupStatusCode.getVersion() + 1);
				aGroupStatusCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aGroupStatusCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aGroupStatusCode, tranType)) {
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

		if (getGroupStatusCode().isNewRecord()) {
			this.grpStsCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.grpStsCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.grpStsDescription.setReadonly(isReadOnly("GroupStatusCodeDialog_grpStsDescription"));
		this.grpStsIsActive.setDisabled(isReadOnly("GroupStatusCodeDialog_grpStsIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.groupStatusCode.isNewRecord()) {
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

		this.grpStsCode.setReadonly(true);
		this.grpStsDescription.setReadonly(true);
		this.grpStsIsActive.setDisabled(true);

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
		this.grpStsCode.setValue("");
		this.grpStsDescription.setValue("");
		this.grpStsIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final GroupStatusCode aGroupStatusCode = new GroupStatusCode();
		BeanUtils.copyProperties(getGroupStatusCode(), aGroupStatusCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the GroupStatusCode object with the components data
		doWriteComponentsToBean(aGroupStatusCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aGroupStatusCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aGroupStatusCode.getRecordType())) {
				aGroupStatusCode.setVersion(aGroupStatusCode.getVersion() + 1);
				if (isNew) {
					aGroupStatusCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aGroupStatusCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aGroupStatusCode.setNewRecord(true);
				}
			}
		} else {
			aGroupStatusCode.setVersion(aGroupStatusCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aGroupStatusCode, tranType)) {
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
	 * @param aGroupStatusCode
	 *            (GroupStatusCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(GroupStatusCode aGroupStatusCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aGroupStatusCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aGroupStatusCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aGroupStatusCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aGroupStatusCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aGroupStatusCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aGroupStatusCode);
				}
				if (isNotesMandatory(taskId, aGroupStatusCode)) {
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

			aGroupStatusCode.setTaskId(taskId);
			aGroupStatusCode.setNextTaskId(nextTaskId);
			aGroupStatusCode.setRoleCode(getRole());
			aGroupStatusCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aGroupStatusCode, tranType);

			String operationRefs = getServiceOperations(taskId, aGroupStatusCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aGroupStatusCode,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aGroupStatusCode, tranType);
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
		GroupStatusCode aGroupStatusCode = (GroupStatusCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getGroupStatusCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getGroupStatusCodeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getGroupStatusCodeService().doApprove(auditHeader);

						if (aGroupStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getGroupStatusCodeService().doReject(auditHeader);

						if (aGroupStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_GroupStatusCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_GroupStatusCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.groupStatusCode), true);
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
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(GroupStatusCode aGroupStatusCode,String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aGroupStatusCode.getBefImage(), aGroupStatusCode);
		return new AuditHeader(String.valueOf(aGroupStatusCode.getId()), null,
				null, null, auditDetail, aGroupStatusCode.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_GroupStatusCodeDialog,auditHeader);
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
		doShowNotes(this.groupStatusCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getGroupStatusCodeListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.groupStatusCode.getGrpStsCode());
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

	public GroupStatusCode getGroupStatusCode() {
		return this.groupStatusCode;
	}
	public void setGroupStatusCode(GroupStatusCode groupStatusCode) {
		this.groupStatusCode = groupStatusCode;
	}

	public void setGroupStatusCodeService(GroupStatusCodeService groupStatusCodeService) {
		this.groupStatusCodeService = groupStatusCodeService;
	}
	public GroupStatusCodeService getGroupStatusCodeService() {
		return this.groupStatusCodeService;
	}

	public void setGroupStatusCodeListCtrl(GroupStatusCodeListCtrl groupStatusCodeListCtrl) {
		this.groupStatusCodeListCtrl = groupStatusCodeListCtrl;
	}
	public GroupStatusCodeListCtrl getGroupStatusCodeListCtrl() {
		return this.groupStatusCodeListCtrl;
	}
}
