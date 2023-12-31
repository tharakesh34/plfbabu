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
 * * FileName : PRelationCodeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.prelationcode;

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
import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennant.backend.service.systemmasters.PRelationCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/PRelationCode/pRelationCodeDialog.zul file.
 */
public class PRelationCodeDialogCtrl extends GFCBaseCtrl<PRelationCode> {
	private static final long serialVersionUID = -6648670330847809858L;
	private static final Logger logger = LogManager.getLogger(PRelationCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PRelationCodeDialog;

	protected Textbox pRelationCode;
	protected Textbox pRelationDesc;
	protected Checkbox relationCodeIsActive;

	// not autoWired Var's
	private PRelationCode mPRelationCode; // over handed per parameter
	private transient PRelationCodeListCtrl pRelationCodeListCtrl; // over handed per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient PRelationCodeService pRelationCodeService;

	/**
	 * default constructor.<br>
	 */
	public PRelationCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PRelationCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected PRelationCode object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_PRelationCodeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PRelationCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("pRelationCode")) {
				this.mPRelationCode = (PRelationCode) arguments.get("pRelationCode");
				PRelationCode befImage = new PRelationCode();
				BeanUtils.copyProperties(this.mPRelationCode, befImage);
				this.mPRelationCode.setBefImage(befImage);
				setMPRelationCode(this.mPRelationCode);
			} else {
				setMPRelationCode(null);
			}

			doLoadWorkFlow(this.mPRelationCode.isWorkflow(), this.mPRelationCode.getWorkflowId(),
					this.mPRelationCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "PRelationCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the pRelationCodeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete pRelationCode here.
			if (arguments.containsKey("pRelationCodeListCtrl")) {
				setPRelationCodeListCtrl((PRelationCodeListCtrl) arguments.get("pRelationCodeListCtrl"));
			} else {
				setPRelationCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMPRelationCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PRelationCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.pRelationCode.setMaxlength(8);
		this.pRelationDesc.setMaxlength(50);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PRelationCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PRelationCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PRelationCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PRelationCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_PRelationCodeDialog);
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
		doWriteBeanToComponents(this.mPRelationCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aPRelationCode PRelationCode
	 */
	public void doWriteBeanToComponents(PRelationCode aPRelationCode) {
		logger.debug("Entering");
		this.pRelationCode.setValue(aPRelationCode.getPRelationCode());
		this.pRelationDesc.setValue(aPRelationCode.getPRelationDesc());
		this.relationCodeIsActive.setChecked(aPRelationCode.isRelationCodeIsActive());
		this.recordStatus.setValue(aPRelationCode.getRecordStatus());

		if (aPRelationCode.isNewRecord()
				|| (aPRelationCode.getRecordType() != null ? aPRelationCode.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.relationCodeIsActive.setChecked(true);
			this.relationCodeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPRelationCode
	 */
	public void doWriteComponentsToBean(PRelationCode aPRelationCode) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aPRelationCode.setPRelationCode(this.pRelationCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPRelationCode.setPRelationDesc(this.pRelationDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPRelationCode.setRelationCodeIsActive(this.relationCodeIsActive.isChecked());
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

		aPRelationCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aPRelationCode
	 */
	public void doShowDialog(PRelationCode aPRelationCode) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aPRelationCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.pRelationCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.pRelationDesc.focus();
				if (StringUtils.isNotBlank(aPRelationCode.getRecordType())) {
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
			doWriteBeanToComponents(aPRelationCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_PRelationCodeDialog.onClose();
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

		if (!this.pRelationCode.isReadonly()) {
			this.pRelationCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PRelationCodeDialog_PRelationCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.pRelationDesc.isReadonly()) {
			this.pRelationDesc.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PRelationCodeDialog_PRelationDesc.value"),
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
		this.pRelationCode.setConstraint("");
		this.pRelationDesc.setConstraint("");
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
		this.pRelationCode.setErrorMessage("");
		this.pRelationDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final PRelationCode aPRelationCode = new PRelationCode();
		BeanUtils.copyProperties(getMPRelationCode(), aPRelationCode);

		String keyReference = Labels.getLabel("label_PRelationCodeDialog_PRelationCode.value") + " : "
				+ aPRelationCode.getPRelationCode();
		doDelete(keyReference, aPRelationCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getMPRelationCode().isNewRecord()) {
			this.pRelationCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.pRelationCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.pRelationDesc.setReadonly(isReadOnly("PRelationCodeDialog_pRelationDesc"));
		this.relationCodeIsActive.setDisabled(isReadOnly("PRelationCodeDialog_relationCodeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.mPRelationCode.isNewRecord()) {
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

		this.pRelationCode.setReadonly(true);
		this.pRelationDesc.setReadonly(true);
		this.relationCodeIsActive.setDisabled(true);

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
		this.pRelationCode.setValue("");
		this.pRelationDesc.setValue("");
		this.relationCodeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final PRelationCode aPRelationCode = new PRelationCode();
		BeanUtils.copyProperties(getMPRelationCode(), aPRelationCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the PRelationCode object with the components data
		doWriteComponentsToBean(aPRelationCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aPRelationCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPRelationCode.getRecordType())) {
				aPRelationCode.setVersion(aPRelationCode.getVersion() + 1);
				if (isNew) {
					aPRelationCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPRelationCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPRelationCode.setNewRecord(true);
				}
			}
		} else {
			aPRelationCode.setVersion(aPRelationCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aPRelationCode, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
				logger.debug("Leaving");
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aPRelationCode (PRelationCode)
	 * 
	 * @param tranType       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(PRelationCode aPRelationCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPRelationCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aPRelationCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPRelationCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPRelationCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPRelationCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPRelationCode);
				}
				if (isNotesMandatory(taskId, aPRelationCode)) {
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

			aPRelationCode.setTaskId(taskId);
			aPRelationCode.setNextTaskId(nextTaskId);
			aPRelationCode.setRoleCode(getRole());
			aPRelationCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPRelationCode, tranType);

			String operationRefs = getServiceOperations(taskId, aPRelationCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPRelationCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPRelationCode, tranType);
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
		PRelationCode aPRelationCode = (PRelationCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getPRelationCodeService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getPRelationCodeService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getPRelationCodeService().doApprove(auditHeader);

					if (aPRelationCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getPRelationCodeService().doReject(auditHeader);

					if (aPRelationCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_PRelationCodeDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_PRelationCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.mPRelationCode), true);
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

	// WorkFlow Details

	/**
	 * Get Audit Header Details
	 * 
	 * @param aPRelationCode
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(PRelationCode aPRelationCode, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPRelationCode.getBefImage(), aPRelationCode);
		return new AuditHeader(String.valueOf(aPRelationCode.getId()), null, null, null, auditDetail,
				aPRelationCode.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.mPRelationCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getPRelationCodeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.mPRelationCode.getPRelationCode());
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

	public PRelationCode getMPRelationCode() {
		return this.mPRelationCode;
	}

	public void setMPRelationCode(PRelationCode mPRelationCode) {
		this.mPRelationCode = mPRelationCode;
	}

	public void setPRelationCodeService(PRelationCodeService pRelationCodeService) {
		this.pRelationCodeService = pRelationCodeService;
	}

	public PRelationCodeService getPRelationCodeService() {
		return this.pRelationCodeService;
	}

	public void setPRelationCodeListCtrl(PRelationCodeListCtrl pRelationCodeListCtrl) {
		this.pRelationCodeListCtrl = pRelationCodeListCtrl;
	}

	public PRelationCodeListCtrl getPRelationCodeListCtrl() {
		return this.pRelationCodeListCtrl;
	}

}
