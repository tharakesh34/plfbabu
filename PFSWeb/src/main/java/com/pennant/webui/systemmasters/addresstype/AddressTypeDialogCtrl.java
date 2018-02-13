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
 * FileName    		:  AddressTypeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.addresstype;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.service.systemmasters.AddressTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/AddressType/addressTypeDialog.zul file.
 */
public class AddressTypeDialogCtrl extends GFCBaseCtrl<AddressType> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = Logger.getLogger(AddressTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_AddressTypeDialog; 	// autoWired
	protected Textbox 		addrTypeCode; 				// autoWired
	protected Textbox 		addrTypeDesc; 				// autoWired
	protected Intbox 		addrTypePriority; 			// autoWired
	protected Checkbox 		addrTypeIsActive; 			// autoWired
	protected Row			row_AddrTypePriority;		// autoWired


	// not autoWired Var's
	private AddressType addressType; 						   // overHanded per parameters
	private transient AddressTypeListCtrl addressTypeListCtrl; // overHanded per
	// parameters

	private transient boolean 	validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient AddressTypeService addressTypeService;

	/**
	 * default constructor.<br>
	 */
	public AddressTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AddressTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AddressType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AddressTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AddressTypeDialog);

		/* set components visible dependent of the users rights */
		try{
			doCheckRights();


			this.addressType = (AddressType) arguments.get("addressType");
			
			AddressType befImage = new AddressType();
			BeanUtils.copyProperties(this.addressType, befImage);
			
			this.addressType.setBefImage(befImage);		
			setAddressType(this.addressType);
			
			setAddressTypeListCtrl((AddressTypeListCtrl) arguments.get("addressTypeListCtrl"));

			doLoadWorkFlow(this.addressType.isWorkflow(),
					this.addressType.getWorkflowId(),
					this.addressType.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"AddressTypeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getAddressType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AddressTypeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.addrTypeCode.setMaxlength(8);
		this.addrTypeDesc.setMaxlength(50);
		this.addrTypePriority.setFormat(PennantAppUtil.formateInt(0));
		this.addrTypePriority.setMaxlength(4);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AddressTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AddressTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AddressTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AddressTypeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_AddressTypeDialog);
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
		doWriteBeanToComponents(this.addressType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAddressType
	 *            AddressType
	 */
	public void doWriteBeanToComponents(AddressType aAddressType) {
		logger.debug("Entering");
		this.addrTypeCode.setValue(aAddressType.getAddrTypeCode());
		this.addrTypeDesc.setValue(aAddressType.getAddrTypeDesc());
		if (ImplementationConstants.ALLOW_ADDRESSTYPE_PRIORITY) {
			this.addrTypePriority.setValue(aAddressType.getAddrTypePriority());
			this.row_AddrTypePriority.setVisible(true);
		}else{
			this.row_AddrTypePriority.setVisible(false);
		}
		this.addrTypeIsActive.setChecked(aAddressType.isAddrTypeIsActive());
		this.recordStatus.setValue(aAddressType.getRecordStatus());
		
		if(aAddressType.isNew() || (aAddressType.getRecordType() != null ? aAddressType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.addrTypeIsActive.setChecked(true);
			this.addrTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAddressType
	 */
	public void doWriteComponentsToBean(AddressType aAddressType) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAddressType.setAddrTypeCode(this.addrTypeCode.getValue()
					.toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAddressType.setAddrTypeDesc(this.addrTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (ImplementationConstants.ALLOW_ADDRESSTYPE_PRIORITY) {
				aAddressType.setAddrTypePriority(this.addrTypePriority.getValue());
			} else {
				aAddressType.setAddrTypePriority(aAddressType.getAddrTypePriority());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAddressType.setAddrTypeIsActive(this.addrTypeIsActive.isChecked());
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

		aAddressType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAddressType
	 * @throws Exception
	 */
	public void doShowDialog(AddressType aAddressType) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aAddressType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.addrTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.addrTypeDesc.focus();
				if (StringUtils.isNotBlank(aAddressType.getRecordType())) {
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
			doWriteBeanToComponents(aAddressType);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AddressTypeDialog.onClose();
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

		if (!this.addrTypeCode.isReadonly()){
			this.addrTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_AddressTypeDialog_AddrTypeCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	

		if (!this.addrTypeDesc.isReadonly()){
			this.addrTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_AddressTypeDialog_AddrTypeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.addrTypePriority.isReadonly()) {
			this.addrTypePriority.setConstraint(new PTNumberValidator(Labels.getLabel(
			"label_AddressTypeDialog_AddrTypePriority.value"), true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.addrTypeCode.setConstraint("");
		this.addrTypeDesc.setConstraint("");
		this.addrTypePriority.setConstraint("");
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
		this.addrTypeCode.setErrorMessage("");
		this.addrTypeDesc.setErrorMessage("");
		this.addrTypePriority.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a AddressType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final AddressType aAddressType = new AddressType();
		BeanUtils.copyProperties(getAddressType(), aAddressType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_AddressTypeDialog_AddrTypeCode.value") +" : "+ aAddressType.getAddrTypeCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aAddressType.getRecordType())) {
				aAddressType.setVersion(aAddressType.getVersion() + 1);
				aAddressType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAddressType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAddressType, tranType)) {
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

		if (getAddressType().isNewRecord()) {
			this.addrTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.addrTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.addrTypeDesc.setReadonly(isReadOnly("AddressTypeDialog_addrTypeDesc"));
		this.addrTypePriority.setReadonly(isReadOnly("AddressTypeDialog_addrTypePriority"));
		this.addrTypeIsActive.setDisabled(isReadOnly("AddressTypeDialog_addrTypeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.addressType.isNewRecord()) {
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

		this.addrTypeCode.setReadonly(true);
		this.addrTypeDesc.setReadonly(true);
		this.addrTypePriority.setReadonly(true);
		this.addrTypeIsActive.setDisabled(true);

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
		this.addrTypeCode.setValue("");
		this.addrTypeDesc.setValue("");
		this.addrTypePriority.setText("");
		this.addrTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final AddressType aAddressType = new AddressType();
		BeanUtils.copyProperties(getAddressType(), aAddressType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the AddressType object with the components data
		doWriteComponentsToBean(aAddressType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aAddressType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAddressType.getRecordType())) {
				aAddressType.setVersion(aAddressType.getVersion() + 1);
				if (isNew) {
					aAddressType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAddressType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAddressType.setNewRecord(true);
				}
			}
		} else {
			aAddressType.setVersion(aAddressType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aAddressType, tranType)) {
				refreshList();
				closeDialog();
			}

		}catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAddressType
	 *            (AddressType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(AddressType aAddressType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAddressType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAddressType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAddressType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAddressType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAddressType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAddressType);
				}

				if (isNotesMandatory(taskId, aAddressType)) {
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

			aAddressType.setTaskId(taskId);
			aAddressType.setNextTaskId(nextTaskId);
			aAddressType.setRoleCode(getRole());
			aAddressType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAddressType, tranType);

			String operationRefs = getServiceOperations(taskId, aAddressType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAddressType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAddressType, tranType);
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
		AddressType aAddressType = (AddressType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getAddressTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getAddressTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getAddressTypeService().doApprove(auditHeader);

						if (aAddressType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getAddressTypeService().doReject(auditHeader);

						if (aAddressType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AddressTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_AddressTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.addressType), true);
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
	 * @param aAddressType
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(AddressType aAddressType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAddressType.getBefImage(), aAddressType);
		return new AuditHeader(String.valueOf(aAddressType.getId()), null,
				null, null, auditDetail, aAddressType.getUserDetails(),	getOverideMap());

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
			ErrorControl.showErrorControl(this.window_AddressTypeDialog, auditHeader);
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
		doShowNotes(this.addressType);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getAddressTypeListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.addressType.getAddrTypeCode());
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

	public AddressType getAddressType() {
		return this.addressType;
	}
	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

	public void setAddressTypeService(AddressTypeService addressTypeService) {
		this.addressTypeService = addressTypeService;
	}
	public AddressTypeService getAddressTypeService() {
		return this.addressTypeService;
	}

	public void setAddressTypeListCtrl(AddressTypeListCtrl addressTypeListCtrl) {
		this.addressTypeListCtrl = addressTypeListCtrl;
	}
	public AddressTypeListCtrl getAddressTypeListCtrl() {
		return this.addressTypeListCtrl;
	}
}
