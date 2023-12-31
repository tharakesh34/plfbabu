/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : MaritalStatusCodeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * *
 * Modified Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.maritalstatuscode;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.service.systemmasters.MaritalStatusCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/MaritalStatusCode/maritalStatusCodeDialog.zul file.
 */
public class MaritalStatusCodeDialogCtrl extends GFCBaseCtrl<MaritalStatusCode> {
	private static final long serialVersionUID = 4779853546531132252L;
	private static final Logger logger = LogManager.getLogger(MaritalStatusCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_MaritalStatusCodeDialog;

	protected Textbox maritalStsCode;
	protected Textbox maritalStsDesc;
	protected Checkbox maritalStsIsActive;
	protected Checkbox systemDefault;

	// not autoWired variables
	private MaritalStatusCode maritalStatusCode; // over handed per parameter
	private transient MaritalStatusCodeListCtrl maritalStatusCodeListCtrl; // over handed per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient MaritalStatusCodeService maritalStatusCodeService;

	/**
	 * default constructor.<br>
	 */
	public MaritalStatusCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MaritalStatusCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected MaritalStatusCode object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_MaritalStatusCodeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_MaritalStatusCodeDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("maritalStatusCode")) {
				this.maritalStatusCode = (MaritalStatusCode) arguments.get("maritalStatusCode");
				MaritalStatusCode befImage = new MaritalStatusCode();
				BeanUtils.copyProperties(this.maritalStatusCode, befImage);
				this.maritalStatusCode.setBefImage(befImage);

				setMaritalStatusCode(this.maritalStatusCode);
			} else {
				setMaritalStatusCode(null);
			}

			doLoadWorkFlow(this.maritalStatusCode.isWorkflow(), this.maritalStatusCode.getWorkflowId(),
					this.maritalStatusCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "MaritalStatusCodeDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the maritalStatusCodeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete maritalStatusCode here.
			if (arguments.containsKey("maritalStatusCodeListCtrl")) {
				setMaritalStatusCodeListCtrl((MaritalStatusCodeListCtrl) arguments.get("maritalStatusCodeListCtrl"));
			} else {
				setMaritalStatusCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMaritalStatusCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_MaritalStatusCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.maritalStsCode.setMaxlength(8);
		this.maritalStsDesc.setMaxlength(50);

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MaritalStatusCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MaritalStatusCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MaritalStatusCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MaritalStatusCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_MaritalStatusCodeDialog);
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
	 * @param event An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.maritalStatusCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMaritalStatusCode MaritalStatusCode
	 */
	public void doWriteBeanToComponents(MaritalStatusCode aMaritalStatusCode) {
		logger.debug("Entering");
		this.maritalStsCode.setValue(aMaritalStatusCode.getMaritalStsCode());
		this.maritalStsDesc.setValue(aMaritalStatusCode.getMaritalStsDesc());
		this.maritalStsIsActive.setChecked(aMaritalStatusCode.isMaritalStsIsActive());
		this.systemDefault.setChecked(aMaritalStatusCode.isSystemDefault());
		this.recordStatus.setValue(aMaritalStatusCode.getRecordStatus());

		if (aMaritalStatusCode.isNewRecord()
				|| (aMaritalStatusCode.getRecordType() != null ? aMaritalStatusCode.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.maritalStsIsActive.setChecked(true);
			this.maritalStsIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aMaritalStatusCode
	 */
	public void doWriteComponentsToBean(MaritalStatusCode aMaritalStatusCode) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aMaritalStatusCode.setMaritalStsCode(this.maritalStsCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMaritalStatusCode.setMaritalStsDesc(this.maritalStsDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMaritalStatusCode.setMaritalStsIsActive(this.maritalStsIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMaritalStatusCode.setSystemDefault(this.systemDefault.isChecked());
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

		aMaritalStatusCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aMaritalStatusCode
	 */
	public void doShowDialog(MaritalStatusCode aMaritalStatusCode) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aMaritalStatusCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.maritalStsCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.maritalStsDesc.focus();
				if (StringUtils.isNotBlank(aMaritalStatusCode.getRecordType())) {
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
			doWriteBeanToComponents(aMaritalStatusCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_MaritalStatusCodeDialog.onClose();
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

		if (!this.maritalStsCode.isReadonly()) {
			this.maritalStsCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MaritalStatusCodeDialog_MaritalStsCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.maritalStsDesc.isReadonly()) {
			this.maritalStsDesc.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MaritalStatusCodeDialog_MaritalStsDesc.value"),
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
		this.maritalStsCode.setConstraint("");
		this.maritalStsDesc.setConstraint("");
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
		this.maritalStsCode.setErrorMessage("");
		this.maritalStsDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final MaritalStatusCode aMaritalStatusCode = new MaritalStatusCode();
		BeanUtils.copyProperties(getMaritalStatusCode(), aMaritalStatusCode);

		doDelete(Labels.getLabel("label_MaritalStatusCodeDialog_MaritalStsCode.value") + " : "
				+ aMaritalStatusCode.getMaritalStsCode(), aMaritalStatusCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getMaritalStatusCode().isNewRecord()) {
			this.maritalStsCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.maritalStsCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.maritalStsDesc.setReadonly(isReadOnly("MaritalStatusCodeDialog_maritalStsDesc"));
		this.maritalStsIsActive.setDisabled(isReadOnly("MaritalStatusCodeDialog_maritalStsIsActive"));
		this.systemDefault.setDisabled(isReadOnly("MaritalStatusCodeDialog_systemDefault"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.maritalStatusCode.isNewRecord()) {
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

		this.maritalStsCode.setReadonly(true);
		this.maritalStsDesc.setReadonly(true);
		this.maritalStsIsActive.setDisabled(true);
		this.systemDefault.setDisabled(true);

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
		this.maritalStsCode.setValue("");
		this.maritalStsDesc.setValue("");
		this.maritalStsIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final MaritalStatusCode aMaritalStatusCode = new MaritalStatusCode();
		BeanUtils.copyProperties(getMaritalStatusCode(), aMaritalStatusCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the MaritalStatusCode object with the components data
		doWriteComponentsToBean(aMaritalStatusCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aMaritalStatusCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aMaritalStatusCode.getRecordType())) {
				aMaritalStatusCode.setVersion(aMaritalStatusCode.getVersion() + 1);
				if (isNew) {
					aMaritalStatusCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aMaritalStatusCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMaritalStatusCode.setNewRecord(true);
				}
			}
		} else {
			aMaritalStatusCode.setVersion(aMaritalStatusCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aMaritalStatusCode, tranType)) {
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
	 * @param aMaritalStatusCode (MaritalStatusCode)
	 * 
	 * @param tranType           (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(MaritalStatusCode aMaritalStatusCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aMaritalStatusCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aMaritalStatusCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMaritalStatusCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aMaritalStatusCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aMaritalStatusCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aMaritalStatusCode);
				}

				if (isNotesMandatory(taskId, aMaritalStatusCode)) {
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

			aMaritalStatusCode.setTaskId(taskId);
			aMaritalStatusCode.setNextTaskId(nextTaskId);
			aMaritalStatusCode.setRoleCode(getRole());
			aMaritalStatusCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aMaritalStatusCode, tranType);
			String operationRefs = getServiceOperations(taskId, aMaritalStatusCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aMaritalStatusCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aMaritalStatusCode, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		MaritalStatusCode aMaritalStatusCode = (MaritalStatusCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getMaritalStatusCodeService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getMaritalStatusCodeService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getMaritalStatusCodeService().doApprove(auditHeader);

					if (aMaritalStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getMaritalStatusCodeService().doReject(auditHeader);

					if (aMaritalStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_MaritalStatusCodeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_MaritalStatusCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.maritalStatusCode), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aMaritalStatusCode (MaritalStatusCode)
	 * @param tranType           (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(MaritalStatusCode aMaritalStatusCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMaritalStatusCode.getBefImage(), aMaritalStatusCode);
		return new AuditHeader(String.valueOf(aMaritalStatusCode.getId()), null, null, null, auditDetail,
				aMaritalStatusCode.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.maritalStatusCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getMaritalStatusCodeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.maritalStatusCode.getMaritalStsCode());
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

	public MaritalStatusCode getMaritalStatusCode() {
		return this.maritalStatusCode;
	}

	public void setMaritalStatusCode(MaritalStatusCode maritalStatusCode) {
		this.maritalStatusCode = maritalStatusCode;
	}

	public void setMaritalStatusCodeService(MaritalStatusCodeService maritalStatusCodeService) {
		this.maritalStatusCodeService = maritalStatusCodeService;
	}

	public MaritalStatusCodeService getMaritalStatusCodeService() {
		return this.maritalStatusCodeService;
	}

	public void setMaritalStatusCodeListCtrl(MaritalStatusCodeListCtrl maritalStatusCodeListCtrl) {
		this.maritalStatusCodeListCtrl = maritalStatusCodeListCtrl;
	}

	public MaritalStatusCodeListCtrl getMaritalStatusCodeListCtrl() {
		return this.maritalStatusCodeListCtrl;
	}

}
