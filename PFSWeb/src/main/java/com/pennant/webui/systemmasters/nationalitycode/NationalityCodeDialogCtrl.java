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
 * FileName    		:  NationalityCodeDialogCtrl.java                                      * 	  
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
package com.pennant.webui.systemmasters.nationalitycode;

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

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.service.systemmasters.NationalityCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/NationalityCode/nationalityCodeDialog.zul file.
 */
public class NationalityCodeDialogCtrl extends GFCBaseCtrl<NationalityCode> {
	private static final long serialVersionUID = 2856434917203365995L;
	private static final Logger logger = Logger.getLogger(NationalityCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_NationalityCodeDialog; 	

	protected Textbox 		txtNationalityCode; 			
	protected Textbox 		nationalityDesc; 				
	protected Checkbox 		nationalityIsActive; 			

	// not auto wired variables
	private NationalityCode nationalityCode; 						   // over handed per parameter
	private transient NationalityCodeListCtrl nationalityCodeListCtrl; // over handed per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient NationalityCodeService nationalityCodeService;

	/**
	 * default constructor.<br>
	 */
	public NationalityCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "NationalityCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected NationalityCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_NationalityCodeDialog(Event event)	throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_NationalityCodeDialog);

		try {
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("nationalityCode")) {
				this.nationalityCode = (NationalityCode) arguments
						.get("nationalityCode");
				NationalityCode befImage = new NationalityCode();
				BeanUtils.copyProperties(this.nationalityCode, befImage);
				this.nationalityCode.setBefImage(befImage);

				setNationalityCode(this.nationalityCode);
			} else {
				setNationalityCode(null);
			}

			doLoadWorkFlow(this.nationalityCode.isWorkflow(),
					this.nationalityCode.getWorkflowId(),
					this.nationalityCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"NationalityCodeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the nationalityCodeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete nationalityCode here.
			if (arguments.containsKey("nationalityCodeListCtrl")) {
				setNationalityCodeListCtrl((NationalityCodeListCtrl) arguments
						.get("nationalityCodeListCtrl"));
			} else {
				setNationalityCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getNationalityCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_NationalityCodeDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.txtNationalityCode.setMaxlength(2);
		this.nationalityDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_NationalityCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_NationalityCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_NationalityCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_NationalityCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_NationalityCodeDialog);
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
		doWriteBeanToComponents(this.nationalityCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aNationalityCode
	 *            NationalityCode
	 */
	public void doWriteBeanToComponents(NationalityCode aNationalityCode) {
		logger.debug("Entering");
		this.txtNationalityCode.setValue(aNationalityCode.getNationalityCode());
		this.nationalityDesc.setValue(aNationalityCode.getNationalityDesc());
		this.nationalityIsActive.setChecked(aNationalityCode.isNationalityIsActive());
		this.recordStatus.setValue(aNationalityCode.getRecordStatus());
		
		if(aNationalityCode.isNew() || (aNationalityCode.getRecordType() != null ? aNationalityCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.nationalityIsActive.setChecked(true);
			this.nationalityIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNationalityCode
	 */
	public void doWriteComponentsToBean(NationalityCode aNationalityCode) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aNationalityCode.setNationalityCode(this.txtNationalityCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aNationalityCode.setNationalityDesc(this.nationalityDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aNationalityCode.setNationalityIsActive(this.nationalityIsActive.isChecked());
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

		aNationalityCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aNationalityCode
	 * @throws Exception
	 */
	public void doShowDialog(NationalityCode aNationalityCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aNationalityCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.txtNationalityCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.nationalityDesc.focus();
				if (StringUtils.isNotBlank(aNationalityCode.getRecordType())) {
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
			doWriteBeanToComponents(aNationalityCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_NationalityCodeDialog.onClose();
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

		if (!this.txtNationalityCode.isReadonly()){
			this.txtNationalityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_NationalityCodeDialog_NationalityCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.nationalityDesc.isReadonly()){
			this.nationalityDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_NationalityCodeDialog_NationalityDesc.value"), 
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
		this.txtNationalityCode.setConstraint("");
		this.nationalityDesc.setConstraint("");
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
		this.txtNationalityCode.setErrorMessage("");
		this.nationalityDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a NationalityCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final NationalityCode aNationalityCode = new NationalityCode();
		BeanUtils.copyProperties(getNationalityCode(), aNationalityCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_NationalityCodeDialog_NationalityCode.value")+" : "+aNationalityCode.getNationalityCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aNationalityCode.getRecordType())) {
				aNationalityCode.setVersion(aNationalityCode.getVersion() + 1);
				aNationalityCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aNationalityCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aNationalityCode, tranType)) {
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

		if (getNationalityCode().isNewRecord()) {
			this.txtNationalityCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.txtNationalityCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.nationalityDesc.setReadonly(isReadOnly("NationalityCodeDialog_nationalityDesc"));
		this.nationalityIsActive.setDisabled(isReadOnly("NationalityCodeDialog_nationalityIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.nationalityCode.isNewRecord()) {
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

		this.txtNationalityCode.setReadonly(true);
		this.nationalityDesc.setReadonly(true);
		this.nationalityIsActive.setDisabled(true);

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
		this.txtNationalityCode.setValue("");
		this.nationalityDesc.setValue("");
		this.nationalityIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final NationalityCode aNationalityCode = new NationalityCode();
		BeanUtils.copyProperties(getNationalityCode(), aNationalityCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the NationalityCode object with the components data
		doWriteComponentsToBean(aNationalityCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aNationalityCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aNationalityCode.getRecordType())) {
				aNationalityCode.setVersion(aNationalityCode.getVersion() + 1);
				if (isNew) {
					aNationalityCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aNationalityCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aNationalityCode.setNewRecord(true);
				}
			}
		} else {
			aNationalityCode.setVersion(aNationalityCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aNationalityCode, tranType)) {
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
	 * @param aNationalityCode
	 *            (NationalityCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(NationalityCode aNationalityCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aNationalityCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aNationalityCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aNationalityCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aNationalityCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aNationalityCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aNationalityCode);
				}

				if (isNotesMandatory(taskId, aNationalityCode)) {
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

			aNationalityCode.setTaskId(taskId);
			aNationalityCode.setNextTaskId(nextTaskId);
			aNationalityCode.setRoleCode(getRole());
			aNationalityCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aNationalityCode, tranType);

			String operationRefs = getServiceOperations(taskId, aNationalityCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aNationalityCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aNationalityCode, tranType);
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
		NationalityCode aNationalityCode = (NationalityCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getNationalityCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getNationalityCodeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getNationalityCodeService().doApprove(auditHeader);

						if (aNationalityCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getNationalityCodeService().doReject(auditHeader);

						if (aNationalityCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_NationalityCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_NationalityCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.nationalityCode), true);
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
	private AuditHeader getAuditHeader(NationalityCode aNationalityCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aNationalityCode.getBefImage(), aNationalityCode);
		return new AuditHeader(String.valueOf(aNationalityCode.getId()), null,
				null, null, auditDetail, aNationalityCode.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_NationalityCodeDialog, auditHeader);
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
		doShowNotes(this.nationalityCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getNationalityCodeListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.nationalityCode.getNationalityCode());
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

	public NationalityCode getNationalityCode() {
		return this.nationalityCode;
	}
	public void setNationalityCode(NationalityCode nationalityCode) {
		this.nationalityCode = nationalityCode;
	}

	public void setNationalityCodeService(NationalityCodeService nationalityCodeService) {
		this.nationalityCodeService = nationalityCodeService;
	}
	public NationalityCodeService getNationalityCodeService() {
		return this.nationalityCodeService;
	}

	public void setNationalityCodeListCtrl(NationalityCodeListCtrl nationalityCodeListCtrl) {
		this.nationalityCodeListCtrl = nationalityCodeListCtrl;
	}
	public NationalityCodeListCtrl getNationalityCodeListCtrl() {
		return this.nationalityCodeListCtrl;
	}

}
