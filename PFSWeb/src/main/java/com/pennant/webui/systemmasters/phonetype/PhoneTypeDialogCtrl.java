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
 * FileName    		:  PhoneTypeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.phonetype;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.systemmasters.PhoneTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/PhoneType/phoneTypeDialog.zul file.
 */
public class PhoneTypeDialogCtrl extends GFCBaseCtrl<PhoneType> {
	private static final long serialVersionUID = -5966260372580930309L;
	private static final Logger logger = Logger.getLogger(PhoneTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_PhoneTypeDialog; 		
	protected Textbox 	phoneTypeCode; 					
	protected Textbox 	phoneTypeDesc; 		
	protected Combobox  phoneTypeRegex;
	protected Intbox 	phoneTypePriority; 				
	protected Checkbox 	phoneTypeIsActive; 	
	protected Row		row_PhoneTypePriority;

	// not autoWired variables
	private 		  PhoneType 		phoneType; 			// over handed per parameter
	private transient PhoneTypeListCtrl phoneTypeListCtrl; 	// over handed per
	// parameter

	private transient boolean 	validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient PhoneTypeService phoneTypeService;

	/**
	 * default constructor.<br>
	 */
	public PhoneTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PhoneTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected PhoneType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PhoneTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PhoneTypeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("phoneType")) {
				this.phoneType = (PhoneType) arguments.get("phoneType");
				PhoneType befImage = new PhoneType();
				BeanUtils.copyProperties(this.phoneType, befImage);
				this.phoneType.setBefImage(befImage);
				setPhoneType(this.phoneType);
			} else {
				setPhoneType(null);
			}

			doLoadWorkFlow(this.phoneType.isWorkflow(),
					this.phoneType.getWorkflowId(),
					this.phoneType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"PhoneTypeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the phoneTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete phoneType here.
			if (arguments.containsKey("phoneTypeListCtrl")) {
				setPhoneTypeListCtrl((PhoneTypeListCtrl) arguments
						.get("phoneTypeListCtrl"));
			} else {
				setPhoneTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getPhoneType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PhoneTypeDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.phoneTypeCode.setMaxlength(8);
		this.phoneTypeDesc.setMaxlength(50);
		this.phoneTypePriority.setMaxlength(3);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PhoneTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PhoneTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PhoneTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PhoneTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_PhoneTypeDialog);
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
		doWriteBeanToComponents(this.phoneType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aPhoneType
	 *            PhoneType
	 */
	public void doWriteBeanToComponents(PhoneType aPhoneType) {
		logger.debug("Entering");
		this.phoneTypeCode.setValue(aPhoneType.getPhoneTypeCode());
		this.phoneTypeDesc.setValue(aPhoneType.getPhoneTypeDesc());
		fillComboBox(this.phoneTypeRegex, aPhoneType.getPhoneTypeRegex(), PennantStaticListUtil.getPhoneTypeRegex(), "");
		if (ImplementationConstants.ALLOW_PHONETYPE_PRIORITY) {
			this.phoneTypePriority.setValue(aPhoneType.getPhoneTypePriority());
			this.row_PhoneTypePriority.setVisible(true);
		} else {
			this.row_PhoneTypePriority.setVisible(false);
		}
		this.phoneTypeIsActive.setChecked(aPhoneType.isPhoneTypeIsActive());
		this.recordStatus.setValue(aPhoneType.getRecordStatus());
		
		if(aPhoneType.isNew() || (aPhoneType.getRecordType() != null ? aPhoneType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.phoneTypeIsActive.setChecked(true);
			this.phoneTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPhoneType
	 */
	public void doWriteComponentsToBean(PhoneType aPhoneType) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aPhoneType.setPhoneTypeCode(this.phoneTypeCode.getValue()
					.toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPhoneType.setPhoneTypeDesc(this.phoneTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.phoneTypeRegex.getSelectedItem()==null || StringUtils.trimToEmpty(this.phoneTypeRegex.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.phoneTypeRegex,Labels.getLabel("STATIC_INVALID",new String[]{Labels.getLabel("label_PhoneTypeDialog_PhoneTypeRegex.value")}));
			}
			aPhoneType.setPhoneTypeRegex(this.phoneTypeRegex.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (ImplementationConstants.ALLOW_PHONETYPE_PRIORITY) {
				aPhoneType.setPhoneTypePriority(this.phoneTypePriority.getValue());
			} else {
				aPhoneType.setPhoneTypePriority(aPhoneType.getPhoneTypePriority());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPhoneType.setPhoneTypeIsActive(this.phoneTypeIsActive.isChecked());
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

		aPhoneType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aPhoneType
	 * @throws Exception
	 */
	public void doShowDialog(PhoneType aPhoneType) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aPhoneType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.phoneTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.phoneTypeDesc.focus();
				if (StringUtils.isNotBlank(aPhoneType.getRecordType())) {
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
			doWriteBeanToComponents(aPhoneType);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_PhoneTypeDialog.onClose();
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

		if (!this.phoneTypeCode.isReadonly()){
			this.phoneTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_PhoneTypeDialog_PhoneTypeCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.phoneTypeDesc.isReadonly()){
			this.phoneTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_PhoneTypeDialog_PhoneTypeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.phoneTypePriority.isReadonly()) {
			this.phoneTypePriority.setConstraint(new PTNumberValidator(Labels.getLabel("label_PhoneTypeDialog_PhoneTypePriority.value"), true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.phoneTypeCode.setConstraint("");
		this.phoneTypeDesc.setConstraint("");
		this.phoneTypePriority.setConstraint("");
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
		this.phoneTypeCode.setErrorMessage("");
		this.phoneTypeDesc.setErrorMessage("");
		this.phoneTypePriority.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a PhoneType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final PhoneType aPhoneType = new PhoneType();
		BeanUtils.copyProperties(getPhoneType(), aPhoneType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
		Labels.getLabel("label_PhoneTypeDialog_PhoneTypeCode.value")+" : "+aPhoneType.getPhoneTypeCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aPhoneType.getRecordType())) {
				aPhoneType.setVersion(aPhoneType.getVersion() + 1);
				aPhoneType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPhoneType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aPhoneType, tranType)) {
					refreshList();
					closeDialog();
				}
			}catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getPhoneType().isNewRecord()) {
			this.phoneTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.phoneTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.phoneTypeDesc.setReadonly(isReadOnly("PhoneTypeDialog_phoneTypeDesc"));
		readOnlyComponent(isReadOnly("PhoneTypeDialog_phoneTypeDesc"), this.phoneTypeRegex);
		this.phoneTypePriority.setReadonly(isReadOnly("PhoneTypeDialog_phoneTypePriority"));
		this.phoneTypeIsActive.setDisabled(isReadOnly("PhoneTypeDialog_phoneTypeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.phoneType.isNewRecord()) {
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
		this.phoneTypeCode.setReadonly(true);
		this.phoneTypeDesc.setReadonly(true);
		readOnlyComponent(true, this.phoneTypeRegex);
		this.phoneTypeRegex.setReadonly(true);
		this.phoneTypePriority.setReadonly(true);
		this.phoneTypeIsActive.setDisabled(true);

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
		this.phoneTypeCode.setValue("");
		this.phoneTypeDesc.setValue("");
		this.phoneTypePriority.setText("");
		this.phoneTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final PhoneType aPhoneType = new PhoneType();
		BeanUtils.copyProperties(getPhoneType(), aPhoneType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the PhoneType object with the components data
		doWriteComponentsToBean(aPhoneType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aPhoneType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPhoneType.getRecordType())) {
				aPhoneType.setVersion(aPhoneType.getVersion() + 1);
				if (isNew) {
					aPhoneType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPhoneType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPhoneType.setNewRecord(true);
				}
			}
		} else {
			aPhoneType.setVersion(aPhoneType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aPhoneType, tranType)) {
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
	 * @param aPhoneType
	 *            (PhoneType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(PhoneType aPhoneType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPhoneType.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aPhoneType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPhoneType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPhoneType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPhoneType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPhoneType);
				}
				if (isNotesMandatory(taskId, aPhoneType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aPhoneType.setTaskId(taskId);
			aPhoneType.setNextTaskId(nextTaskId);
			aPhoneType.setRoleCode(getRole());
			aPhoneType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPhoneType, tranType);
			String operationRefs = getServiceOperations(taskId, aPhoneType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPhoneType,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPhoneType, tranType);
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
		PhoneType aPhoneType = (PhoneType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getPhoneTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getPhoneTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getPhoneTypeService().doApprove(auditHeader);

						if (aPhoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getPhoneTypeService().doReject(auditHeader);

						if (aPhoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_PhoneTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_PhoneTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.phoneType), true);
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
	 * @param aPhoneType
	 *            (PhoneType)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(PhoneType aPhoneType, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aPhoneType.getBefImage(), aPhoneType);
		return new AuditHeader(String.valueOf(aPhoneType.getId()), null, null,
				null, auditDetail, aPhoneType.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_PhoneTypeDialog,auditHeader);
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
		doShowNotes(this.phoneType);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getPhoneTypeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.phoneType.getPhoneTypeCode());
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

	public PhoneType getPhoneType() {
		return this.phoneType;
	}
	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

	public void setPhoneTypeService(PhoneTypeService phoneTypeService) {
		this.phoneTypeService = phoneTypeService;
	}
	public PhoneTypeService getPhoneTypeService() {
		return this.phoneTypeService;
	}

	public void setPhoneTypeListCtrl(PhoneTypeListCtrl phoneTypeListCtrl) {
		this.phoneTypeListCtrl = phoneTypeListCtrl;
	}
	public PhoneTypeListCtrl getPhoneTypeListCtrl() {
		return this.phoneTypeListCtrl;
	}

}
