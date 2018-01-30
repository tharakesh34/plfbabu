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
 * FileName    		:  SubSegmentDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.subsegment;

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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.systemmasters.SubSegmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/SubSegment/subSegmentDialog.zul file.
 */
public class SubSegmentDialogCtrl extends GFCBaseCtrl<SubSegment> {
	private static final long serialVersionUID = -3976608317795122426L;
	private static final Logger logger = Logger.getLogger(SubSegmentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_SubSegmentDialog;
	protected Textbox 	segmentCode; 			
	protected Textbox 	subSegmentCode; 		
	protected Textbox 	subSegmentDesc; 		
	protected Checkbox 	subSegmentIsActive; 

	// not autoWired variables
	private SubSegment subSegment; // overHanded per parameter
	private transient SubSegmentListCtrl subSegmentListCtrl; // overHanded per parameter

	private transient boolean 	validationOn;

	protected Button btnSearchSegmentCode; 	// autoWire
	protected Textbox lovDescSegmentCodeName;
	

	// ServiceDAOs / Domain Classes
	private transient SubSegmentService subSegmentService;

	/**
	 * default constructor.<br>
	 */
	public SubSegmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SubSegmentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SubSegment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SubSegmentDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SubSegmentDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			this.btnEdit.setVisible(PennantConstants.CHANGE_SEGMENT);

			if (arguments.containsKey("subSegment")) {
				this.subSegment = (SubSegment) arguments.get("subSegment");
				SubSegment befImage = new SubSegment();
				BeanUtils.copyProperties(this.subSegment, befImage);
				this.subSegment.setBefImage(befImage);

				setSubSegment(this.subSegment);
			} else {
				setSubSegment(null);
			}

			doLoadWorkFlow(this.subSegment.isWorkflow(),
					this.subSegment.getWorkflowId(),
					this.subSegment.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"SubSegmentDialog");
			}

			// READ OVERHANDED parameters !
			// we get the subSegmentListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete subSegment here.
			if (arguments.containsKey("subSegmentListCtrl")) {
				setSubSegmentListCtrl((SubSegmentListCtrl) arguments
						.get("subSegmentListCtrl"));
			} else {
				setSubSegmentListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSubSegment());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SubSegmentDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.segmentCode.setMaxlength(8);
		this.subSegmentCode.setMaxlength(8);
		this.subSegmentDesc.setMaxlength(50);
		
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SubSegmentDialog_btnNew"));
		if(PennantConstants.CHANGE_SEGMENT){
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SubSegmentDialog_btnEdit"));
		}
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SubSegmentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SubSegmentDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_SubSegmentDialog);
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
		doWriteBeanToComponents(this.subSegment.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSubSegment
	 *            (SubSegment)
	 * 
	 */
	public void doWriteBeanToComponents(SubSegment aSubSegment) {
		logger.debug("Entering");
		this.segmentCode.setValue(aSubSegment.getSegmentCode());
		this.subSegmentCode.setValue(aSubSegment.getSubSegmentCode());
		this.subSegmentDesc.setValue(aSubSegment.getSubSegmentDesc());
		this.subSegmentIsActive.setChecked(aSubSegment.isSubSegmentIsActive());
		
		if (aSubSegment.isNewRecord()) {
			this.lovDescSegmentCodeName.setValue("");
		} else {
			this.lovDescSegmentCodeName.setValue(aSubSegment.getSegmentCode() + "-" + aSubSegment.getLovDescSegmentCodeName());
		}
		
		this.recordStatus.setValue(aSubSegment.getRecordStatus());
		
		if(aSubSegment.isNew() || StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, aSubSegment.getRecordType())){
			this.subSegmentIsActive.setChecked(true);
			this.subSegmentIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSubSegment
	 */
	public void doWriteComponentsToBean(SubSegment aSubSegment) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSubSegment.setLovDescSegmentCodeName(this.lovDescSegmentCodeName.getValue());
			aSubSegment.setSegmentCode(StringUtils.strip(this.segmentCode.getValue().toUpperCase()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSegment.setSubSegmentCode(StringUtils.strip(this.subSegmentCode.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSegment.setSubSegmentDesc(StringUtils.strip(this.subSegmentDesc.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSubSegment.setSubSegmentIsActive(this.subSegmentIsActive.isChecked());
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
		aSubSegment.setRecordStatus(this.recordStatus.getValue());
		setSubSegment(aSubSegment);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSubSegment
	 * @throws Exception
	 */
	public void doShowDialog(SubSegment aSubSegment) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSubSegment.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.lovDescSegmentCodeName.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.subSegmentDesc.focus();
				if (StringUtils.isNotBlank(aSubSegment.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				this.btnEdit.setVisible(PennantConstants.CHANGE_SEGMENT);
			}
			this.subSegmentCode.setReadonly(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSubSegment);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_SubSegmentDialog.onClose();
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
		if (!this.subSegmentCode.isReadonly()){
			this.subSegmentCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSegmentDialog_SubSegmentCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	

		if (!this.subSegmentDesc.isReadonly()){
			this.subSegmentDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSegmentDialog_SubSegmentDesc.value"), 
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
		this.segmentCode.setConstraint("");
		this.subSegmentCode.setConstraint("");
		this.subSegmentDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescSegmentCodeName.setConstraint(new PTStringValidator(Labels.getLabel("label_SubSegmentDialog_SegmentCode.value"), null, true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescSegmentCodeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.segmentCode.setErrorMessage("");
		this.subSegmentCode.setErrorMessage("");
		this.subSegmentDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations
	
	/**
	 * Deletes a SubSegment object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SubSegment aSubSegment = new SubSegment();
		BeanUtils.copyProperties(getSubSegment(), aSubSegment);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aSubSegment.getSegmentCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSubSegment.getRecordType())) {
				aSubSegment.setVersion(aSubSegment.getVersion() + 1);
				aSubSegment.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSubSegment.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSubSegment, tranType)) {
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
		if (getSubSegment().isNewRecord()) {
			this.segmentCode.setReadonly(true);
			this.subSegmentCode.setReadonly(false);
			this.btnSearchSegmentCode.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.segmentCode.setReadonly(true);
			this.subSegmentCode.setReadonly(true);
			//this.btnSearchSegmentCode.setDisabled(false);
			this.btnCancel.setVisible(true);
		}

		this.subSegmentDesc.setReadonly(isReadOnly("SubSegmentDialog_subSegmentDesc"));
		this.subSegmentIsActive.setDisabled(isReadOnly("SubSegmentDialog_subSegmentIsActive"));
		if(PennantConstants.CHANGE_SEGMENT) {
			this.btnSearchSegmentCode.setDisabled(isReadOnly("SubSegmentDialog_segmentCode"));
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.subSegment.isNewRecord()) {
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
		this.btnSearchSegmentCode.setDisabled(true);
		this.subSegmentCode.setReadonly(true);
		this.subSegmentDesc.setReadonly(true);
		this.subSegmentIsActive.setDisabled(true);

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
		this.segmentCode.setValue("");
		this.lovDescSegmentCodeName.setValue("");
		this.subSegmentCode.setValue("");
		this.subSegmentDesc.setValue("");
		this.subSegmentIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {

		logger.debug("Entering");
		final SubSegment aSubSegment = new SubSegment();
		BeanUtils.copyProperties(getSubSegment(), aSubSegment);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SubSegment object with the components data
		doWriteComponentsToBean(aSubSegment);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSubSegment.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSubSegment.getRecordType())) {
				aSubSegment.setVersion(aSubSegment.getVersion() + 1);
				if (isNew) {
					aSubSegment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSubSegment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSubSegment.setNewRecord(true);
				}
			}
		} else {
			aSubSegment.setVersion(aSubSegment.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSubSegment, tranType)) {
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
	 * @param aSubSegment
	 *            (SubSegment)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(SubSegment aSubSegment, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSubSegment.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSubSegment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSubSegment.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSubSegment.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSubSegment.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSubSegment);
				}

				if (isNotesMandatory(taskId, aSubSegment)) {
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

			aSubSegment.setTaskId(taskId);
			aSubSegment.setNextTaskId(nextTaskId);
			aSubSegment.setRoleCode(getRole());
			aSubSegment.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSubSegment, tranType);

			String operationRefs = getServiceOperations(taskId, aSubSegment);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSubSegment, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSubSegment, tranType);
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
		SubSegment aSubSegment = (SubSegment) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSubSegmentService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSubSegmentService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSubSegmentService().doApprove(auditHeader);

						if (aSubSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSubSegmentService().doReject(auditHeader);

						if (aSubSegment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SubSegmentDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SubSegmentDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.subSegment), true);
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

	// Search Button Component Events

	public void onClick$btnSearchSegmentCode(Event event) {

		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_SubSegmentDialog, "Segment");
		if (dataObject instanceof String) {
			this.segmentCode.setValue(dataObject.toString());
			this.lovDescSegmentCodeName.setValue("");
		} else {
			Segment details = (Segment) dataObject;
			if (details != null) {
				this.segmentCode.setValue(details.getLovValue());
				this.lovDescSegmentCodeName.setValue(details.getLovValue() + "-" + details.getSegmentDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aSubSegment
	 *            (SubSegment)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(SubSegment aSubSegment, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSubSegment.getBefImage(), aSubSegment);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aSubSegment.getUserDetails(), getOverideMap());

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
			ErrorControl.showErrorControl(this.window_SubSegmentDialog, auditHeader);
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
		doShowNotes(this.subSegment);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSubSegmentListCtrl().search();
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getSubSegment().getSubSegmentCode()+PennantConstants.KEY_SEPERATOR +
					getSubSegment().getSegmentCode();
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

	public SubSegment getSubSegment() {
		return this.subSegment;
	}
	public void setSubSegment(SubSegment subSegment) {
		this.subSegment = subSegment;
	}

	public void setSubSegmentService(SubSegmentService subSegmentService) {
		this.subSegmentService = subSegmentService;
	}
	public SubSegmentService getSubSegmentService() {
		return this.subSegmentService;
	}

	public void setSubSegmentListCtrl(SubSegmentListCtrl subSegmentListCtrl) {
		this.subSegmentListCtrl = subSegmentListCtrl;
	}
	public SubSegmentListCtrl getSubSegmentListCtrl() {
		return this.subSegmentListCtrl;
	}

}
