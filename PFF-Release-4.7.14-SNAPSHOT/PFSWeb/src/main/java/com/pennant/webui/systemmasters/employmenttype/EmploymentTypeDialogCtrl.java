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
 * FileName    		:  EmploymentTypeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.employmenttype;

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
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.service.systemmasters.EmploymentTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/EmploymentType/employmentTypeDialog.zul file.
 */
public class EmploymentTypeDialogCtrl extends GFCBaseCtrl<EmploymentType> {
	private static final long serialVersionUID = -6632169221044686005L;
	private static final Logger logger = Logger
			.getLogger(EmploymentTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  		window_EmploymentTypeDialog;

	protected Uppercasebox	empType; 					
	protected Textbox 		empTypeDesc; 		
	protected Checkbox empTypeIsActive; 			// autoWired

	// not auto wired Var's
	private EmploymentType employmentType; // overHanded per parameter
	private transient EmploymentTypeListCtrl employmentTypeListCtrl; // overHanded
																	// per parameter
	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient EmploymentTypeService employmentTypeService;

	/**
	 * default constructor.<br>
	 */
	public EmploymentTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "EmploymentTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected EmploymentType object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EmploymentTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EmploymentTypeDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("employmentType")) {
				this.employmentType = (EmploymentType) arguments
						.get("employmentType");
				EmploymentType befImage = new EmploymentType();
				BeanUtils.copyProperties(this.employmentType, befImage);
				this.employmentType.setBefImage(befImage);

				setEmploymentType(this.employmentType);
			} else {
				setEmploymentType(null);
			}

			doLoadWorkFlow(this.employmentType.isWorkflow(),
					this.employmentType.getWorkflowId(),
					this.employmentType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"EmploymentTypeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the employmentTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete employmentType here.
			if (arguments.containsKey("employmentTypeListCtrl")) {
				setEmploymentTypeListCtrl((EmploymentTypeListCtrl) arguments
						.get("employmentTypeListCtrl"));
			} else {
				setEmploymentTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getEmploymentType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_EmploymentTypeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.empType.setMaxlength(8);
		this.empTypeDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_EmploymentTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_EmploymentTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_EmploymentTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_EmploymentTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_EmploymentTypeDialog);
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
		doWriteBeanToComponents(this.employmentType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aEmploymentType
	 *            EmploymentType
	 */
	public void doWriteBeanToComponents(EmploymentType aEmploymentType) {
		logger.debug("Entering");
		this.empType.setValue(aEmploymentType.getEmpType());
		this.empTypeDesc.setValue(aEmploymentType.getEmpTypeDesc());
		this.recordStatus.setValue(aEmploymentType.getRecordStatus());
		this.empTypeIsActive.setChecked(aEmploymentType.isEmpTypeIsActive());
		
		if(aEmploymentType.isNew() || (aEmploymentType.getRecordType() != null ? aEmploymentType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.empTypeIsActive.setChecked(true);
			this.empTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEmploymentType
	 */
	public void doWriteComponentsToBean(EmploymentType aEmploymentType) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aEmploymentType.setEmpType(this.empType.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEmploymentType.setEmpTypeDesc(this.empTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEmploymentType.setEmpTypeIsActive(this.empTypeIsActive.isChecked());
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
		aEmploymentType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aEmploymentType
	 * @throws Exception
	 */
	public void doShowDialog(EmploymentType aEmploymentType) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aEmploymentType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.empType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.empTypeDesc.focus();
				if (StringUtils.isNotBlank(aEmploymentType.getRecordType())){
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
			doWriteBeanToComponents(aEmploymentType);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_EmploymentTypeDialog.onClose();
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
		if (!this.empType.isReadonly()) {
			this.empType.setConstraint(new PTStringValidator(Labels.getLabel("label_EmploymentTypeDialog_EmpType.value"), 
					PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
		}
		if (!this.empTypeDesc.isReadonly()) {
			this.empTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_EmploymentTypeDialog_EmpTypeDesc.value"), 
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
		this.empType.setConstraint("");
		this.empTypeDesc.setConstraint("");
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
		this.empType.setErrorMessage("");
		this.empTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getEmploymentTypeListCtrl().search();
	} 

	// CRUD operations

	/**
	 * Deletes a EmploymentType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final EmploymentType aEmploymentType = new EmploymentType();
		BeanUtils.copyProperties(getEmploymentType(), aEmploymentType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + Labels.getLabel("label_EmploymentTypeDialog_EmpType.value")+" : "+aEmploymentType.getEmpType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aEmploymentType.getRecordType())) {
				aEmploymentType.setVersion(aEmploymentType.getVersion() + 1);
				aEmploymentType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aEmploymentType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aEmploymentType, tranType)) {
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
		if (getEmploymentType().isNewRecord()) {
			this.empType.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.empType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.empTypeDesc
				.setReadonly(isReadOnly("EmploymentTypeDialog_empTypeDesc"));
		this.empTypeIsActive.setDisabled(isReadOnly("EmploymentTypeDialog_EmpTypeIsActive"));
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.employmentType.isNewRecord()) {
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
		this.empType.setReadonly(true);
		this.empTypeDesc.setReadonly(true);
		this.empTypeIsActive.setDisabled(true);

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
		this.empType.setValue("");
		this.empTypeDesc.setValue("");
		this.empTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final EmploymentType aEmploymentType = new EmploymentType();
		BeanUtils.copyProperties(getEmploymentType(), aEmploymentType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the EmploymentType object with the components data
		doWriteComponentsToBean(aEmploymentType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aEmploymentType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aEmploymentType.getRecordType())) {
				aEmploymentType.setVersion(aEmploymentType.getVersion() + 1);
				if (isNew) {
					aEmploymentType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aEmploymentType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEmploymentType.setNewRecord(true);
				}
			}
		} else {
			aEmploymentType.setVersion(aEmploymentType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aEmploymentType, tranType)) {
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
	 * @param aEmploymentType
	 *            (EmploymentType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(EmploymentType aEmploymentType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aEmploymentType.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getUserId());
		aEmploymentType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEmploymentType
				.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aEmploymentType.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aEmploymentType
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aEmploymentType);
				}

				if (isNotesMandatory(taskId, aEmploymentType)) {
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

			aEmploymentType.setTaskId(taskId);
			aEmploymentType.setNextTaskId(nextTaskId);
			aEmploymentType.setRoleCode(getRole());
			aEmploymentType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aEmploymentType, tranType);

			String operationRefs = getServiceOperations(taskId, aEmploymentType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aEmploymentType,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aEmploymentType, tranType);
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
		EmploymentType aEmploymentType = (EmploymentType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getEmploymentTypeService().delete(
								auditHeader);

						deleteNotes=true;	
					} else {
						auditHeader = getEmploymentTypeService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getEmploymentTypeService().doApprove(
								auditHeader);

						if(aEmploymentType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;		
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getEmploymentTypeService().doReject(
								auditHeader);
						if(aEmploymentType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_EmploymentTypeDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_EmploymentTypeDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					
					if(deleteNotes){
						deleteNotes(getNotes(this.employmentType),true);
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
	 * @param aEmploymentType
	 *            (EmploymentType)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(EmploymentType aEmploymentType,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aEmploymentType.getBefImage(), aEmploymentType);
		return new AuditHeader(String.valueOf(aEmploymentType.getId()), null,
				null, null, auditDetail, aEmploymentType.getUserDetails(),getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_EmploymentTypeDialog,
					auditHeader);
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
		doShowNotes(this.employmentType);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.employmentType.getEmpType());
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

	public EmploymentType getEmploymentType() {
		return this.employmentType;
	}
	public void setEmploymentType(EmploymentType employmentType) {
		this.employmentType = employmentType;
	}

	public void setEmploymentTypeService(
			EmploymentTypeService employmentTypeService) {
		this.employmentTypeService = employmentTypeService;
	}
	public EmploymentTypeService getEmploymentTypeService() {
		return this.employmentTypeService;
	}

	public void setEmploymentTypeListCtrl(
			EmploymentTypeListCtrl employmentTypeListCtrl) {
		this.employmentTypeListCtrl = employmentTypeListCtrl;
	}
	public EmploymentTypeListCtrl getEmploymentTypeListCtrl() {
		return this.employmentTypeListCtrl;
	}

}
