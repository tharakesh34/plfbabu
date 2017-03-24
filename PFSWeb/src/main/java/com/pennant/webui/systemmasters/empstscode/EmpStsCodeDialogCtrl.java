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
 * FileName    		:  EmpStsCodeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.empstscode;

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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.service.systemmasters.EmpStsCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/EmpStsCode/empStsCodeDialog.zul file.
 */
public class EmpStsCodeDialogCtrl extends GFCBaseCtrl<EmpStsCode> {
	private static final long serialVersionUID = -8079491234061977678L;
	private final static Logger logger = Logger.getLogger(EmpStsCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_EmpStsCodeDialog;	

	protected Textbox 	empStsCode; 			
	protected Textbox 	empStsDesc; 			
	protected Checkbox 	empStsIsActive; 		

	// not autoWired variables
	private EmpStsCode stsCode; 	// overHanded per parameter
	private transient EmpStsCodeListCtrl empStsCodeListCtrl; // overHanded per
	// parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient EmpStsCodeService empStsCodeService;

	/**
	 * default constructor.<br>
	 */
	public EmpStsCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "EmpStsCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected EmpStsCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EmpStsCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EmpStsCodeDialog);

		try {
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("empStsCode")) {
				this.stsCode = (EmpStsCode) arguments.get("empStsCode");
				EmpStsCode befImage = new EmpStsCode();
				BeanUtils.copyProperties(this.stsCode, befImage);
				this.stsCode.setBefImage(befImage);
				setStsCode(this.stsCode);
			} else {
				setStsCode(null);
			}

			doLoadWorkFlow(this.stsCode.isWorkflow(),
					this.stsCode.getWorkflowId(), this.stsCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"EmpStsCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the empStsCodeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete empStsCode here.
			if (arguments.containsKey("empStsCodeListCtrl")) {
				setEmpStsCodeListCtrl((EmpStsCodeListCtrl) arguments
						.get("empStsCodeListCtrl"));
			} else {
				setEmpStsCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getStsCode());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_EmpStsCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.empStsCode.setMaxlength(8);
		this.empStsDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EmpStsCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EmpStsCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_EmpStsCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EmpStsCodeDialog_btnSave"));
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
		logger.debug("Entering");
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
		MessageUtil.showHelpWindow(event, window_EmpStsCodeDialog);
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
		doWriteBeanToComponents(this.stsCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aEmpStsCode
	 *            EmpStsCode
	 */
	public void doWriteBeanToComponents(EmpStsCode aEmpStsCode) {
		logger.debug("Entering");
		this.empStsCode.setValue(aEmpStsCode.getEmpStsCode());
		this.empStsDesc.setValue(aEmpStsCode.getEmpStsDesc());
		this.empStsIsActive.setChecked(aEmpStsCode.isEmpStsIsActive());
		this.recordStatus.setValue(aEmpStsCode.getRecordStatus());
		
		if(aEmpStsCode.isNew() || (aEmpStsCode.getRecordType() != null ? aEmpStsCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.empStsIsActive.setChecked(true);
			this.empStsIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEmpStsCode
	 */
	public void doWriteComponentsToBean(EmpStsCode aEmpStsCode) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aEmpStsCode.setEmpStsCode(this.empStsCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEmpStsCode.setEmpStsDesc(this.empStsDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEmpStsCode.setEmpStsIsActive(this.empStsIsActive.isChecked());
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

		aEmpStsCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aEmpStsCode
	 * @throws Exception
	 */
	public void doShowDialog(EmpStsCode aEmpStsCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aEmpStsCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.empStsCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.empStsDesc.focus();
				if (StringUtils.isNotBlank(aEmpStsCode.getRecordType())) {
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
			doWriteBeanToComponents(aEmpStsCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_EmpStsCodeDialog.onClose();
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

		if (!this.empStsCode.isReadonly()){
			this.empStsCode.setConstraint(new PTStringValidator(Labels.getLabel("label_EmpStsCodeDialog_EmpStsCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.empStsDesc.isReadonly()){
			this.empStsDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_EmpStsCodeDialog_EmpStsDesc.value"), 
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
		this.empStsCode.setConstraint("");
		this.empStsDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.empStsCode.setErrorMessage("");
		this.empStsDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a EmpStsCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final EmpStsCode aEmpStsCode = new EmpStsCode();
		BeanUtils.copyProperties(getStsCode(), aEmpStsCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_EmpStsCodeDialog_EmpStsCode.value")+" : "+aEmpStsCode.getEmpStsCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,Messagebox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(aEmpStsCode.getRecordType())) {
				aEmpStsCode.setVersion(aEmpStsCode.getVersion() + 1);
				aEmpStsCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aEmpStsCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aEmpStsCode, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				MessageUtil.showErrorMessage(e.getMessage());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getStsCode().isNewRecord()) {
			this.empStsCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.empStsCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.empStsDesc.setReadonly(isReadOnly("EmpStsCodeDialog_empStsDesc"));
		this.empStsIsActive.setDisabled(isReadOnly("EmpStsCodeDialog_empStsIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.stsCode.isNewRecord()) {
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

		this.empStsCode.setReadonly(true);
		this.empStsDesc.setReadonly(true);
		this.empStsIsActive.setDisabled(true);

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
		this.empStsCode.setValue("");
		this.empStsDesc.setValue("");
		this.empStsIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final EmpStsCode aEmpStsCode = new EmpStsCode();
		BeanUtils.copyProperties(getStsCode(), aEmpStsCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the EmpStsCode object with the components data
		doWriteComponentsToBean(aEmpStsCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aEmpStsCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aEmpStsCode.getRecordType())) {
				aEmpStsCode.setVersion(aEmpStsCode.getVersion() + 1);
				if (isNew) {
					aEmpStsCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aEmpStsCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEmpStsCode.setNewRecord(true);
				}
			}
		} else {
			aEmpStsCode.setVersion(aEmpStsCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {

			if (doProcess(aEmpStsCode, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aEmpStsCode
	 *            (EmpStsCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(EmpStsCode aEmpStsCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aEmpStsCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aEmpStsCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEmpStsCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aEmpStsCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aEmpStsCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aEmpStsCode);
				}

				if (isNotesMandatory(taskId, aEmpStsCode)) {
					try {
						if (!notesEntered) {
							MessageUtil.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error("Exception: ", e);
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

			aEmpStsCode.setTaskId(taskId);
			aEmpStsCode.setNextTaskId(nextTaskId);
			aEmpStsCode.setRoleCode(getRole());
			aEmpStsCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aEmpStsCode, tranType);

			String operationRefs = getServiceOperations(taskId, aEmpStsCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aEmpStsCode,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aEmpStsCode, tranType);
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
		EmpStsCode aEmpStsCode = (EmpStsCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getEmpStsCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getEmpStsCodeService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getEmpStsCodeService().doApprove(auditHeader);

						if (aEmpStsCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getEmpStsCodeService().doReject(auditHeader);

						if (aEmpStsCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_EmpStsCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_EmpStsCodeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.stsCode), true);
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
	private AuditHeader getAuditHeader(EmpStsCode aEmpStsCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aEmpStsCode.getBefImage(), aEmpStsCode);
		return new AuditHeader(String.valueOf(aEmpStsCode.getId()), null, null,
				null, auditDetail, aEmpStsCode.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_EmpStsCodeDialog,auditHeader);
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
		doShowNotes(this.stsCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getEmpStsCodeListCtrl().search();
	}


	@Override
	protected String getReference() {
		return String.valueOf(this.stsCode.getEmpStsCode());
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

	public EmpStsCode getStsCode() {
		return this.stsCode;
	}
	public void setStsCode(EmpStsCode stsCode) {
		this.stsCode = stsCode;
	}

	public void setEmpStsCodeService(EmpStsCodeService empStsCodeService) {
		this.empStsCodeService = empStsCodeService;
	}
	public EmpStsCodeService getEmpStsCodeService() {
		return this.empStsCodeService;
	}

	public void setEmpStsCodeListCtrl(EmpStsCodeListCtrl empStsCodeListCtrl) {
		this.empStsCodeListCtrl = empStsCodeListCtrl;
	}
	public EmpStsCodeListCtrl getEmpStsCodeListCtrl() {
		return this.empStsCodeListCtrl;
	}

}
