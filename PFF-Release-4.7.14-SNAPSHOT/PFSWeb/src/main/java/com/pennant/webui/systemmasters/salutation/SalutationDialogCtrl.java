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
 * FileName    		:  SalutationDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.salutation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.systemmasters.SalutationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/Salutation/salutationDialog.zul file.
 */
public class SalutationDialogCtrl extends GFCBaseCtrl<Salutation> {
	private static final long serialVersionUID = -3545695595801290469L;
	private static final Logger logger = Logger.getLogger(SalutationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_SalutationDialog; 
	protected Textbox 	salutationCode; 		 
	protected Textbox 	saluationDesc; 			 
	protected Checkbox 	salutationIsActive; 	 
	protected Checkbox 	systemDefault; 	 
	protected Combobox 	salutationGenderCode; 	 

	// not auto wired variables
	private Salutation salutation; // overHanded per parameter
	private transient SalutationListCtrl salutationListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient SalutationService salutationService;
	private Gender sysDefaultgender;


	/**
	 * default constructor.<br>
	 */
	public SalutationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SalutationDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Salutation object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SalutationDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SalutationDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("salutation")) {
				this.salutation = (Salutation) arguments.get("salutation");
				Salutation befImage = new Salutation();
				BeanUtils.copyProperties(this.salutation, befImage);
				this.salutation.setBefImage(befImage);

				setSalutation(this.salutation);
			} else {
				setSalutation(null);
			}

			doLoadWorkFlow(this.salutation.isWorkflow(),
					this.salutation.getWorkflowId(),
					this.salutation.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"SalutationDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the salutationListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete salutation here.
			if (arguments.containsKey("salutationListCtrl")) {
				setSalutationListCtrl((SalutationListCtrl) arguments
						.get("salutationListCtrl"));
			} else {
				setSalutationListCtrl(null);
			}
			setGenderSystemDefault();
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSalutation());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SalutationDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.salutationCode.setMaxlength(8);
		this.saluationDesc.setMaxlength(50);
		
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SalutationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SalutationDialog_btnEdit"));
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_SalutationDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SalutationDialog_btnSave"));
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
		doCheckSystemDefault();
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
		MessageUtil.showHelpWindow(event, window_SalutationDialog);
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
		doWriteBeanToComponents(this.salutation.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSalutation
	 *            Salutation
	 */
	public void doWriteBeanToComponents(Salutation aSalutation) {
		logger.debug("Entering");

		this.salutationCode.setValue(aSalutation.getSalutationCode());
		this.saluationDesc.setValue(aSalutation.getSaluationDesc());
		this.salutationIsActive.setChecked(aSalutation.isSalutationIsActive());
		this.systemDefault.setChecked(aSalutation.isSystemDefault());
		
		List<ValueLabel> genderList = PennantAppUtil.getGenderCodes();
		fillComboBox(this.salutationGenderCode, aSalutation.getSalutationGenderCode(), genderList, "");

		this.recordStatus.setValue(aSalutation.getRecordStatus());
		
		if(aSalutation.isNew() || (aSalutation.getRecordType() != null ? aSalutation.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.salutationIsActive.setChecked(true);
			this.salutationIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSalutation
	 */
	public void doWriteComponentsToBean(Salutation aSalutation) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSalutation.setSalutationCode(this.salutationCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalutation.setSaluationDesc(this.saluationDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalutation.setSalutationIsActive(this.salutationIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalutation.setSystemDefault(this.systemDefault.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(!this.salutationGenderCode.isDisabled() && this.salutationGenderCode.getSelectedIndex()<1){
				throw new WrongValueException(salutationGenderCode, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_SalutationDialog_SalutationGenderCode.value")}));
			}
			aSalutation.setSalutationGenderCode(this.salutationGenderCode.getSelectedItem().getValue().toString());
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
		aSalutation.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSalutation
	 * @throws Exception
	 */
	public void doShowDialog(Salutation aSalutation) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSalutation.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.salutationCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.saluationDesc.focus();
				if (StringUtils.isNotBlank(aSalutation.getRecordType())) {
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
			doWriteBeanToComponents(aSalutation);
			
			if (aSalutation.isNew() || isWorkFlowEnabled()) {
				doCheckSystemDefault();
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_SalutationDialog.onClose();
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
		if (!this.salutationCode.isReadonly()){
			this.salutationCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SalutationDialog_SalutationCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	

		if (!this.saluationDesc.isReadonly()){
			this.saluationDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SalutationDialog_SaluationDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (this.salutationGenderCode.isReadonly()) {
			this.salutationGenderCode.setConstraint(new PTStringValidator(Labels.getLabel(
					"label_SalutationDialog_SalutationGenderCode.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.salutationCode.setConstraint("");
		this.saluationDesc.setConstraint("");
		this.salutationGenderCode.setConstraint("");
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
		this.salutationCode.setErrorMessage("");
		this.saluationDesc.setErrorMessage("");
		this.salutationGenderCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a Salutation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final Salutation aSalutation = new Salutation();
		BeanUtils.copyProperties(getSalutation(), aSalutation);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
		Labels.getLabel("label_SalutationDialog_SalutationCode.value")+" : "+aSalutation.getSalutationCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSalutation.getRecordType())) {
				aSalutation.setVersion(aSalutation.getVersion() + 1);
				aSalutation.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSalutation.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aSalutation, tranType)) {
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

		if (getSalutation().isNewRecord()) {
			this.salutationCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.salutationCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.saluationDesc.setReadonly(isReadOnly("SalutationDialog_saluationDesc"));
		this.salutationIsActive.setDisabled(isReadOnly("SalutationDialog_salutationIsActive"));
		this.salutationGenderCode.setDisabled(isReadOnly("SalutationDialog_salutationGenderCode"));
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.salutation.isNewRecord()) {
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

		this.salutationCode.setReadonly(true);
		this.saluationDesc.setReadonly(true);
		this.salutationIsActive.setDisabled(true);
		this.systemDefault.setDisabled(true);
		this.salutationGenderCode.setDisabled(true);

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
		this.salutationCode.setValue("");
		this.saluationDesc.setValue("");
		this.salutationIsActive.setChecked(false);
		this.salutationGenderCode.setSelectedIndex(0);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Salutation aSalutation = new Salutation();
		BeanUtils.copyProperties(getSalutation(), aSalutation);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Salutation object with the components data
		doWriteComponentsToBean(aSalutation);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSalutation.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSalutation.getRecordType())) {
				aSalutation.setVersion(aSalutation.getVersion() + 1);
				if (isNew) {
					aSalutation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSalutation.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSalutation.setNewRecord(true);
				}
			}
		} else {
			aSalutation.setVersion(aSalutation.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aSalutation, tranType)) {
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
	 * @param aSalutation
	 *            (Salutation)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Salutation aSalutation, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSalutation.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSalutation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSalutation.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSalutation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSalutation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSalutation);
				}
				if (isNotesMandatory(taskId, aSalutation)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (!StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
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

			aSalutation.setTaskId(taskId);
			aSalutation.setNextTaskId(nextTaskId);
			aSalutation.setRoleCode(getRole());
			aSalutation.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSalutation, tranType);
			String operationRefs = getServiceOperations(taskId, aSalutation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSalutation, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSalutation, tranType);
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
		Salutation aSalutation = (Salutation) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSalutationService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSalutationService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSalutationService().doApprove(auditHeader);

						if (aSalutation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSalutationService().doReject(auditHeader);

						if (aSalutation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SalutationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SalutationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.salutation), true);
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

	// Work Flow Details

	/**
	 * Get Audit Header Details
	 * 
	 * @param aSalutation
	 *            (Salutation)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Salutation aSalutation, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aSalutation.getBefImage(), aSalutation);
		return new AuditHeader(String.valueOf(aSalutation.getId()), null, null,
				null, auditDetail, aSalutation.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_SalutationDialog,	auditHeader);
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
		doShowNotes(this.salutation);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSalutationListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.salutation.getSalutationCode());
	}
	
	
	public void setGenderSystemDefault(){
		Filter[] systemDefault=new Filter[1];
		systemDefault[0]=new Filter("SystemDefault", 1, Filter.OP_EQUAL);
		Object genderdef=	PennantAppUtil.getSystemDefault("Gender","", systemDefault);
		if (genderdef!=null) {
			sysDefaultgender=(Gender) genderdef;
		}
	}
	
	public void onChange$salutationGenderCode(Event event){
		logger.debug("Entering");
		doCheckSystemDefault();
		logger.debug("Leaving");
	}
	
	public void doCheckSystemDefault(){
		logger.debug("Entering");
		if (this.salutationGenderCode.getSelectedItem()!=null && !
				this.salutationGenderCode.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
			if (sysDefaultgender!=null && sysDefaultgender.getGenderCode().equals(this.salutationGenderCode.getSelectedItem().getValue().toString())) {
				this.systemDefault.setDisabled(isReadOnly("SalutationDialog_systemDefault"));
			}else{
				this.systemDefault.setDisabled(true);
				this.systemDefault.setChecked(false);
			}
		}else{
			this.systemDefault.setDisabled(true);
			this.systemDefault.setChecked(false);
		}
		logger.debug("Entering");
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

	public Salutation getSalutation() {
		return this.salutation;
	}
	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public void setSalutationService(SalutationService salutationService) {
		this.salutationService = salutationService;
	}
	public SalutationService getSalutationService() {
		return this.salutationService;
	}

	public void setSalutationListCtrl(SalutationListCtrl salutationListCtrl) {
		this.salutationListCtrl = salutationListCtrl;
	}
	public SalutationListCtrl getSalutationListCtrl() {
		return this.salutationListCtrl;
	}

}
