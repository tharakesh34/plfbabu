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
 * * FileName : InterestRateBasisCodeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 *
 * * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.staticparms.interestratebasiscode;

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
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.service.staticparms.InterestRateBasisCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/StaticParms/InterestRateBasisCode
 * /interestRateBasisCodeDialog.zul file.
 */
public class InterestRateBasisCodeDialogCtrl extends GFCBaseCtrl<InterestRateBasisCode> {
	private static final long serialVersionUID = 6369726125407866076L;
	private static final Logger logger = LogManager.getLogger(InterestRateBasisCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_InterestRateBasisCodeDialog; // autoWired

	protected Textbox intRateBasisCode; // autoWired
	protected Textbox intRateBasisDesc; // autoWired
	protected Checkbox intRateBasisIsActive; // autoWired

	// not autoWired variables
	private InterestRateBasisCode interestRateBasisCode; // over handed per parameter
	private transient InterestRateBasisCodeListCtrl interestRateBasisCodeListCtrl; // overHanded per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient InterestRateBasisCodeService interestRateBasisCodeService;

	/**
	 * default constructor.<br>
	 */
	public InterestRateBasisCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InterestRateBasisCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected InterestRateBasisCode object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_InterestRateBasisCodeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_InterestRateBasisCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("interestRateBasisCode")) {
				this.interestRateBasisCode = (InterestRateBasisCode) arguments.get("interestRateBasisCode");
				InterestRateBasisCode befImage = new InterestRateBasisCode();
				BeanUtils.copyProperties(this.interestRateBasisCode, befImage);
				this.interestRateBasisCode.setBefImage(befImage);
				setInterestRateBasisCode(this.interestRateBasisCode);
			} else {
				setInterestRateBasisCode(null);
			}

			doLoadWorkFlow(this.interestRateBasisCode.isWorkflow(), this.interestRateBasisCode.getWorkflowId(),
					this.interestRateBasisCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "InterestRateBasisCodeDialog");
			}

			// READ OVERHANDED parameters !
			// we get the interestRateBasisCodeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete interestRateBasisCode here.
			if (arguments.containsKey("interestRateBasisCodeListCtrl")) {
				setInterestRateBasisCodeListCtrl(
						(InterestRateBasisCodeListCtrl) arguments.get("interestRateBasisCodeListCtrl"));
			} else {
				setInterestRateBasisCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getInterestRateBasisCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_InterestRateBasisCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.intRateBasisCode.setMaxlength(8);
		this.intRateBasisDesc.setMaxlength(50);

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InterestRateBasisCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InterestRateBasisCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InterestRateBasisCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InterestRateBasisCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_InterestRateBasisCodeDialog);
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
		doWriteBeanToComponents(this.interestRateBasisCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aInterestRateBasisCode InterestRateBasisCode
	 */
	public void doWriteBeanToComponents(InterestRateBasisCode aInterestRateBasisCode) {
		logger.debug("Entering");
		this.intRateBasisCode.setValue(aInterestRateBasisCode.getIntRateBasisCode());
		this.intRateBasisDesc.setValue(aInterestRateBasisCode.getIntRateBasisDesc());
		this.intRateBasisIsActive.setChecked(aInterestRateBasisCode.isIntRateBasisIsActive());
		this.recordStatus.setValue(aInterestRateBasisCode.getRecordStatus());

		if (aInterestRateBasisCode.isNewRecord() || StringUtils.trimToEmpty(aInterestRateBasisCode.getRecordType())
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.intRateBasisIsActive.setChecked(true);
			this.intRateBasisIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aInterestRateBasisCode
	 */
	public void doWriteComponentsToBean(InterestRateBasisCode aInterestRateBasisCode) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aInterestRateBasisCode.setIntRateBasisCode(this.intRateBasisCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aInterestRateBasisCode.setIntRateBasisDesc(this.intRateBasisDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aInterestRateBasisCode.setIntRateBasisIsActive(this.intRateBasisIsActive.isChecked());
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

		aInterestRateBasisCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aInterestRateBasisCode
	 */
	public void doShowDialog(InterestRateBasisCode aInterestRateBasisCode) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aInterestRateBasisCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.intRateBasisCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.intRateBasisDesc.focus();
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aInterestRateBasisCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_InterestRateBasisCodeDialog.onClose();
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

		if (!this.intRateBasisCode.isReadonly()) {
			this.intRateBasisCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_InterestRateBasisCodeDialog_IntRateBasisCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.intRateBasisDesc.isReadonly()) {
			this.intRateBasisDesc.setConstraint(
					new PTStringValidator(Labels.getLabel("label_InterestRateBasisCodeDialog_IntRateBasisDesc.value"),
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
		this.intRateBasisCode.setConstraint("");
		this.intRateBasisDesc.setConstraint("");
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
		this.intRateBasisCode.setErrorMessage("");
		this.intRateBasisDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final InterestRateBasisCode aInterestRateBasisCode = new InterestRateBasisCode();
		BeanUtils.copyProperties(getInterestRateBasisCode(), aInterestRateBasisCode);

		String keyReference = Labels.getLabel("label_InterestRateBasisCodeDialog_IntRateBasisCode.value") + " : "
				+ aInterestRateBasisCode.getIntRateBasisCode();

		doDelete(keyReference, aInterestRateBasisCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getInterestRateBasisCode().isNewRecord()) {
			this.intRateBasisCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.intRateBasisCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.intRateBasisDesc.setReadonly(isReadOnly("InterestRateBasisCodeDialog_intRateBasisDesc"));
		this.intRateBasisIsActive.setDisabled(isReadOnly("InterestRateBasisCodeDialog_intRateBasisIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.interestRateBasisCode.isNewRecord()) {
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

		this.intRateBasisCode.setReadonly(true);
		this.intRateBasisDesc.setReadonly(true);
		this.intRateBasisIsActive.setDisabled(true);

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
		logger.debug("Leaving");
		// remove validation, if there are a save before
		this.intRateBasisCode.setValue("");
		this.intRateBasisDesc.setValue("");
		this.intRateBasisIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final InterestRateBasisCode aInterestRateBasisCode = new InterestRateBasisCode();
		BeanUtils.copyProperties(getInterestRateBasisCode(), aInterestRateBasisCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the InterestRateBasisCode object with the components data
		doWriteComponentsToBean(aInterestRateBasisCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aInterestRateBasisCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aInterestRateBasisCode.getRecordType())) {
				aInterestRateBasisCode.setVersion(aInterestRateBasisCode.getVersion() + 1);
				if (isNew) {
					aInterestRateBasisCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aInterestRateBasisCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aInterestRateBasisCode.setNewRecord(true);
				}
			}
		} else {
			aInterestRateBasisCode.setVersion(aInterestRateBasisCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aInterestRateBasisCode, tranType)) {
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
	 * @param aInterestRateBasisCode
	 * @param tranType
	 * @return
	 */
	protected boolean doProcess(InterestRateBasisCode aInterestRateBasisCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aInterestRateBasisCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aInterestRateBasisCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aInterestRateBasisCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aInterestRateBasisCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aInterestRateBasisCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aInterestRateBasisCode);
				}

				if (isNotesMandatory(taskId, aInterestRateBasisCode)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aInterestRateBasisCode.setTaskId(taskId);
			aInterestRateBasisCode.setNextTaskId(nextTaskId);
			aInterestRateBasisCode.setRoleCode(getRole());
			aInterestRateBasisCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aInterestRateBasisCode, tranType);

			String operationRefs = getServiceOperations(taskId, aInterestRateBasisCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aInterestRateBasisCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aInterestRateBasisCode, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		InterestRateBasisCode aInterestRateBasisCode = (InterestRateBasisCode) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getInterestRateBasisCodeService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getInterestRateBasisCodeService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getInterestRateBasisCodeService().doApprove(auditHeader);

					if (aInterestRateBasisCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getInterestRateBasisCodeService().doReject(auditHeader);

					if (aInterestRateBasisCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_InterestRateBasisCodeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_InterestRateBasisCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.interestRateBasisCode), true);
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
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(InterestRateBasisCode aInterestRateBasisCode, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aInterestRateBasisCode.getBefImage(),
				aInterestRateBasisCode);
		return new AuditHeader(String.valueOf(aInterestRateBasisCode.getId()), null, null, null, auditDetail,
				aInterestRateBasisCode.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.interestRateBasisCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getInterestRateBasisCodeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.interestRateBasisCode.getIntRateBasisCode());
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

	public InterestRateBasisCode getInterestRateBasisCode() {
		return this.interestRateBasisCode;
	}

	public void setInterestRateBasisCode(InterestRateBasisCode interestRateBasisCode) {
		this.interestRateBasisCode = interestRateBasisCode;
	}

	public void setInterestRateBasisCodeService(InterestRateBasisCodeService interestRateBasisCodeService) {
		this.interestRateBasisCodeService = interestRateBasisCodeService;
	}

	public InterestRateBasisCodeService getInterestRateBasisCodeService() {
		return this.interestRateBasisCodeService;
	}

	public void setInterestRateBasisCodeListCtrl(InterestRateBasisCodeListCtrl interestRateBasisCodeListCtrl) {
		this.interestRateBasisCodeListCtrl = interestRateBasisCodeListCtrl;
	}

	public InterestRateBasisCodeListCtrl getInterestRateBasisCodeListCtrl() {
		return this.interestRateBasisCodeListCtrl;
	}

}
