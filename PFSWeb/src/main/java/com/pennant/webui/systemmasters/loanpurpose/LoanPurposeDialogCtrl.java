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
 * * FileName : LoanPurposeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.loanpurpose;

import java.math.BigDecimal;
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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LoanPurpose;
import com.pennant.backend.service.systemmasters.LoanPurposeService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/LoanPurpose/LoanPurposeDialogCtrl.zul file.
 */
public class LoanPurposeDialogCtrl extends GFCBaseCtrl<LoanPurpose> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(LoanPurposeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LoanPurposeDialog; // autoWired
	protected Textbox loanPurposeCode; // autoWired
	protected Textbox loanPurposeDesc; // autoWired
	protected CurrencyBox loanEligibleAmount; // autoWired
	protected Checkbox loanPurposeIsActive; // autoWired
	protected Row row_EligibleAmount;

	// not autoWired Var's
	private LoanPurpose loanpurpose; // overHanded per parameters
	private transient LoanPurposeListCtrl loanPurposeListCtrl; // overHanded per
	// parameters

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient LoanPurposeService loanPurposeService;

	/**
	 * default constructor.<br>
	 */
	public LoanPurposeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LoanPurposeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected LoanPurpose object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LoanPurposeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_LoanPurposeDialog);

		/* set components visible dependent of the users rights */
		try {
			doCheckRights();

			this.loanpurpose = (LoanPurpose) arguments.get("LoanPurpose");

			LoanPurpose befImage = new LoanPurpose();
			BeanUtils.copyProperties(this.loanpurpose, befImage);

			this.loanpurpose.setBefImage(befImage);
			setLoanPurpose(this.loanpurpose);

			setLoanPurposeListCtrl((LoanPurposeListCtrl) arguments.get("LoanPurposeListCtrl"));

			doLoadWorkFlow(this.loanpurpose.isWorkflow(), this.loanpurpose.getWorkflowId(),
					this.loanpurpose.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "LoanPurposeDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getLoanPurpose());
			this.row_EligibleAmount.setVisible(isReadOnly("LoanPurposeDialog_EligibleAmountVisible"));
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LoanPurposeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.loanPurposeCode.setMaxlength(8);
		this.loanPurposeDesc.setMaxlength(50);
		this.loanEligibleAmount.setProperties(false, PennantConstants.defaultCCYDecPos);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LoanPurposeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LoanPurposeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LoanPurposeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LoanPurposeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_LoanPurposeDialog);
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
		doWriteBeanToComponents(this.loanpurpose.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aLoanPurpose LoanPurpose
	 */
	public void doWriteBeanToComponents(LoanPurpose aLoanPurpose) {
		logger.debug("Entering");
		this.loanPurposeCode.setValue(aLoanPurpose.getLoanPurposeCode());
		this.loanPurposeDesc.setValue(aLoanPurpose.getLoanPurposeDesc());
		this.loanPurposeIsActive.setChecked(aLoanPurpose.isLoanPurposeIsActive());
		this.loanEligibleAmount.setValue(
				PennantApplicationUtil.formateAmount(aLoanPurpose.getEligibleAmount(), CurrencyUtil.getFormat("")));
		this.recordStatus.setValue(aLoanPurpose.getRecordStatus());

		if (aLoanPurpose.isNewRecord() || (aLoanPurpose.getRecordType() != null ? aLoanPurpose.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.loanPurposeIsActive.setChecked(true);
			this.loanPurposeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLoanPurpose
	 */
	public void doWriteComponentsToBean(LoanPurpose aLoanPurpose) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aLoanPurpose.setLoanPurposeCode(this.loanPurposeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLoanPurpose.setLoanPurposeDesc(this.loanPurposeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLoanPurpose.setLoanPurposeIsActive(this.loanPurposeIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.loanEligibleAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
				aLoanPurpose.setEligibleAmount(PennantApplicationUtil.unFormateAmount(
						this.loanEligibleAmount.getValidateValue(), PennantConstants.defaultCCYDecPos));
			}
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

		aLoanPurpose.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aLoanPurpose
	 */
	public void doShowDialog(LoanPurpose aLoanPurpose) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aLoanPurpose.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.loanPurposeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.loanPurposeDesc.focus();
				if (StringUtils.isNotBlank(aLoanPurpose.getRecordType())) {
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
			doWriteBeanToComponents(aLoanPurpose);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_LoanPurposeDialog.onClose();
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

		if (!this.loanPurposeCode.isReadonly()) {
			this.loanPurposeCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LoanPurposeDialog_LoanPurposeCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		if (!this.loanPurposeDesc.isReadonly()) {
			this.loanPurposeDesc.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LoanPurposeDialog_LoanPurposeDesc.value"),
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
		this.loanPurposeCode.setConstraint("");
		this.loanPurposeDesc.setConstraint("");
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
		this.loanPurposeCode.setErrorMessage("");
		this.loanPurposeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final LoanPurpose aLoanPurpose = new LoanPurpose();
		BeanUtils.copyProperties(getLoanPurpose(), aLoanPurpose);

		String keyReference = Labels.getLabel("label_LoanPurposeDialog_LoanPurposeCode.value") + " : "
				+ aLoanPurpose.getLoanPurposeCode();

		doDelete(keyReference, aLoanPurpose);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getLoanPurpose().isNewRecord()) {
			this.loanPurposeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.loanPurposeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.loanPurposeDesc.setReadonly(isReadOnly("LoanPurposeDialog_loanPurposeDesc"));
		this.loanEligibleAmount.setReadonly(isReadOnly("LoanPurposeDialog_EligibleAmount"));
		this.loanPurposeIsActive.setDisabled(isReadOnly("LoanPurposeDialog_loanPurposeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.loanpurpose.isNewRecord()) {
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

		this.loanPurposeCode.setReadonly(true);
		this.loanPurposeDesc.setReadonly(true);
		this.loanEligibleAmount.setReadonly(true);
		this.loanPurposeIsActive.setDisabled(true);

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
		this.loanPurposeCode.setValue("");
		this.loanPurposeDesc.setValue("");
		this.loanPurposeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final LoanPurpose aLoanPurpose = new LoanPurpose();
		BeanUtils.copyProperties(getLoanPurpose(), aLoanPurpose);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the LoanPurpose object with the components data
		doWriteComponentsToBean(aLoanPurpose);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aLoanPurpose.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLoanPurpose.getRecordType())) {
				aLoanPurpose.setVersion(aLoanPurpose.getVersion() + 1);
				if (isNew) {
					aLoanPurpose.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLoanPurpose.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLoanPurpose.setNewRecord(true);
				}
			}
		} else {
			aLoanPurpose.setVersion(aLoanPurpose.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aLoanPurpose, tranType)) {
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
	 * @param aLoanPurpose (LoanPurpose)
	 * 
	 * @param tranType     (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(LoanPurpose aLoanPurpose, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aLoanPurpose.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLoanPurpose.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLoanPurpose.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aLoanPurpose.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aLoanPurpose.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aLoanPurpose);
				}

				if (isNotesMandatory(taskId, aLoanPurpose)) {
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

			aLoanPurpose.setTaskId(taskId);
			aLoanPurpose.setNextTaskId(nextTaskId);
			aLoanPurpose.setRoleCode(getRole());
			aLoanPurpose.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aLoanPurpose, tranType);

			String operationRefs = getServiceOperations(taskId, aLoanPurpose);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aLoanPurpose, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aLoanPurpose, tranType);
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
		LoanPurpose aLoanPurpose = (LoanPurpose) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getLoanPurposeService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getLoanPurposeService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getLoanPurposeService().doApprove(auditHeader);

					if (aLoanPurpose.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getLoanPurposeService().doReject(auditHeader);

					if (aLoanPurpose.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_LoanPurposeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_LoanPurposeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.loanpurpose), true);
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
	 * @param aLoanPurpose
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(LoanPurpose aLoanPurpose, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLoanPurpose.getBefImage(), aLoanPurpose);
		return new AuditHeader(String.valueOf(aLoanPurpose.getId()), null, null, null, auditDetail,
				aLoanPurpose.getUserDetails(), getOverideMap());

	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.loanpurpose);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getLoanPurposeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.loanpurpose.getLoanPurposeCode());
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

	public LoanPurpose getLoanPurpose() {
		return this.loanpurpose;
	}

	public void setLoanPurpose(LoanPurpose addressType) {
		this.loanpurpose = addressType;
	}

	public void setLoanPurposeService(LoanPurposeService addressTypeService) {
		this.loanPurposeService = addressTypeService;
	}

	public LoanPurposeService getLoanPurposeService() {
		return this.loanPurposeService;
	}

	public void setLoanPurposeListCtrl(LoanPurposeListCtrl addressTypeListCtrl) {
		this.loanPurposeListCtrl = addressTypeListCtrl;
	}

	public LoanPurposeListCtrl getLoanPurposeListCtrl() {
		return this.loanPurposeListCtrl;
	}
}
