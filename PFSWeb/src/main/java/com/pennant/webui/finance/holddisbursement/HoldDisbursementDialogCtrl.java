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
 * * FileName : HoldDisbursementDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-10-2018 * *
 * Modified Date : 09-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-10-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.holddisbursement;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennant.backend.service.finance.HoldDisbursementService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/HoldDisbursement/holdDisbursementDialog.zul file. <br>
 */
public class HoldDisbursementDialogCtrl extends GFCBaseCtrl<HoldDisbursement> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(HoldDisbursementDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_HoldDisbursementDialog;
	protected Space space_FinReference;
	protected Textbox finReference;
	protected Space space_Hold;
	protected Checkbox hold;
	protected Space space_TotalLoanAmt;
	protected Decimalbox totalLoanAmt;
	protected Space space_DisbursedAmount;
	protected Decimalbox disbursedAmount;
	protected Space space_HoldLimitAmount;
	protected Decimalbox holdLimitAmount;
	protected Textbox remarks;

	private HoldDisbursement holdDisbursement; // overhanded per param

	private transient HoldDisbursementListCtrl holdDisbursementListCtrl; // overhanded per param
	private transient HoldDisbursementService holdDisbursementService;

	/**
	 * default constructor.<br>
	 */
	public HoldDisbursementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "HoldDisbursementDialog";
	}

	@Override
	protected String getReference() {
		return this.holdDisbursement.getFinReference();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_HoldDisbursementDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_HoldDisbursementDialog);

		try {
			// Get the required arguments.
			this.holdDisbursement = (HoldDisbursement) arguments.get("holdDisbursement");
			this.holdDisbursementListCtrl = (HoldDisbursementListCtrl) arguments.get("holdDisbursementListCtrl");

			if (this.holdDisbursement == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			HoldDisbursement holdDisbursement = new HoldDisbursement();
			BeanUtils.copyProperties(this.holdDisbursement, holdDisbursement);
			this.holdDisbursement.setBefImage(holdDisbursement);

			// Render the page and display the data.
			doLoadWorkFlow(this.holdDisbursement.isWorkflow(), this.holdDisbursement.getWorkflowId(),
					this.holdDisbursement.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.holdDisbursement);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setMaxlength(20);
		this.totalLoanAmt.setMaxlength(18);
		// this.totalLoanAmt.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.totalLoanAmt.setFormat(PennantConstants.rateFormate9);

		this.totalLoanAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.totalLoanAmt.setScale(PennantConstants.defaultCCYDecPos);
		this.disbursedAmount.setMaxlength(18);
		this.disbursedAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.disbursedAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.disbursedAmount.setScale(PennantConstants.defaultCCYDecPos);
		this.holdLimitAmount.setMaxlength(18);
		this.holdLimitAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.holdLimitAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.holdLimitAmount.setScale(PennantConstants.defaultCCYDecPos);
		this.remarks.setMaxlength(100);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_HoldDisbursementDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_HoldDisbursementDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_HoldDisbursementDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_HoldDisbursementDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.holdDisbursement);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		holdDisbursementListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.holdDisbursement.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param holdDisbursement
	 * 
	 */
	public void doWriteBeanToComponents(HoldDisbursement aHoldDisbursement) {
		logger.debug(Literal.ENTERING);

		this.finReference.setValue(aHoldDisbursement.getFinReference());
		this.hold.setChecked(aHoldDisbursement.isHold());
		this.totalLoanAmt.setValue(PennantApplicationUtil.formateAmount(aHoldDisbursement.getTotalLoanAmt(),
				PennantConstants.defaultCCYDecPos));
		this.disbursedAmount.setValue(PennantApplicationUtil.formateAmount(aHoldDisbursement.getDisbursedAmount(),
				PennantConstants.defaultCCYDecPos));
		this.holdLimitAmount.setValue(this.totalLoanAmt.getValue().subtract(this.disbursedAmount.getValue()));
		this.remarks.setValue(aHoldDisbursement.getRemarks());

		this.recordStatus.setValue(aHoldDisbursement.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aHoldDisbursement
	 */
	public void doWriteComponentsToBean(HoldDisbursement aHoldDisbursement) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Fin Reference
		try {
			aHoldDisbursement.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Hold
		try {
			aHoldDisbursement.setHold(this.hold.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Total Loan Amt
		/*
		 * try { if (this.totalLoanAmt.getValue() != null) {
		 * aHoldDisbursement.setTotalLoanAmt(PennantApplicationUtil.unFormateAmount(this.totalLoanAmt.getValue(),
		 * PennantConstants.defaultCCYDecPos)); } else { aHoldDisbursement.setTotalLoanAmt(BigDecimal.ZERO); } } catch
		 * (WrongValueException we) { wve.add(we); }
		 */
		// Disbursed Amount
		/*
		 * try { if (this.disbursedAmount.getValue() != null) {
		 * aHoldDisbursement.setDisbursedAmount(PennantApplicationUtil .unFormateAmount(this.disbursedAmount.getValue(),
		 * PennantConstants.defaultCCYDecPos)); } else { aHoldDisbursement.setDisbursedAmount(BigDecimal.ZERO); } }
		 * catch (WrongValueException we) { wve.add(we); }
		 */
		// Hold Limit Amount
		/*
		 * try { if (this.holdLimitAmount.getValue() != null) {
		 * aHoldDisbursement.setHoldLimitAmount(PennantApplicationUtil.unFormateAmount(
		 * this.totalLoanAmt.getValue().subtract(this.disbursedAmount.getValue()), PennantConstants.defaultCCYDecPos));
		 * } else { aHoldDisbursement.setHoldLimitAmount(BigDecimal.ZERO); } } catch (WrongValueException we) {
		 * wve.add(we); }
		 */
		// Remarks
		try {
			aHoldDisbursement.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param holdDisbursement The entity that need to be render.
	 */
	public void doShowDialog(HoldDisbursement holdDisbursement) {
		logger.debug(Literal.LEAVING);

		if (holdDisbursement.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.finReference.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(holdDisbursement.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.hold.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(holdDisbursement);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		/*
		 * if (!this.finReference.isReadonly()) { this.finReference.setConstraint( new
		 * PTStringValidator(Labels.getLabel("label_HoldDisbursementDialog_FinReference.value"),
		 * PennantRegularExpressions.REGEX_NAME, true)); }
		 */
		if (!this.totalLoanAmt.isReadonly()) {
			this.totalLoanAmt.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_HoldDisbursementDialog_TotalLoanAmt.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}
		if (!this.disbursedAmount.isReadonly()) {
			this.disbursedAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_HoldDisbursementDialog_DisbursedAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}
		if (!this.holdLimitAmount.isReadonly()) {
			this.holdLimitAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_HoldDisbursementDialog_HoldLimitAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks
					.setConstraint(new PTStringValidator(Labels.getLabel("label_HoldDisbursementDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_NAME, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.finReference.setConstraint("");
		this.totalLoanAmt.setConstraint("");
		this.disbursedAmount.setConstraint("");
		this.holdLimitAmount.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final HoldDisbursement aHoldDisbursement = new HoldDisbursement();
		BeanUtils.copyProperties(this.holdDisbursement, aHoldDisbursement);

		doDelete(aHoldDisbursement.getFinReference(), aHoldDisbursement);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.holdDisbursement.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.totalLoanAmt);
			readOnlyComponent(true, this.disbursedAmount);
			readOnlyComponent(true, this.holdLimitAmount);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.totalLoanAmt);
			readOnlyComponent(true, this.disbursedAmount);
			readOnlyComponent(true, this.holdLimitAmount);
		}

		readOnlyComponent(isReadOnly("HoldDisbursementDialog_Hold"), this.hold);
		readOnlyComponent(isReadOnly("HoldDisbursementDialog_Remarks"), this.remarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.holdDisbursement.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.hold);
		readOnlyComponent(true, this.totalLoanAmt);
		readOnlyComponent(true, this.disbursedAmount);
		readOnlyComponent(true, this.holdLimitAmount);
		readOnlyComponent(true, this.remarks);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.finReference.setValue("");
		this.hold.setChecked(false);
		this.totalLoanAmt.setValue("");
		this.disbursedAmount.setValue("");
		this.holdLimitAmount.setValue("");
		this.remarks.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final HoldDisbursement aHoldDisbursement = new HoldDisbursement();
		BeanUtils.copyProperties(this.holdDisbursement, aHoldDisbursement);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aHoldDisbursement);

		isNew = aHoldDisbursement.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aHoldDisbursement.getRecordType())) {
				aHoldDisbursement.setVersion(aHoldDisbursement.getVersion() + 1);
				if (isNew) {
					aHoldDisbursement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aHoldDisbursement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aHoldDisbursement.setNewRecord(true);
				}
			}
		} else {
			aHoldDisbursement.setVersion(aHoldDisbursement.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aHoldDisbursement, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(HoldDisbursement aHoldDisbursement, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aHoldDisbursement.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aHoldDisbursement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aHoldDisbursement.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aHoldDisbursement.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aHoldDisbursement.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aHoldDisbursement);
				}

				if (isNotesMandatory(taskId, aHoldDisbursement)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
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

			aHoldDisbursement.setTaskId(taskId);
			aHoldDisbursement.setNextTaskId(nextTaskId);
			aHoldDisbursement.setRoleCode(getRole());
			aHoldDisbursement.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aHoldDisbursement, tranType);
			String operationRefs = getServiceOperations(taskId, aHoldDisbursement);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aHoldDisbursement, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aHoldDisbursement, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		HoldDisbursement aHoldDisbursement = (HoldDisbursement) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = holdDisbursementService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = holdDisbursementService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = holdDisbursementService.doApprove(auditHeader);

					if (aHoldDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = holdDisbursementService.doReject(auditHeader);
					if (aHoldDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_HoldDisbursementDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_HoldDisbursementDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.holdDisbursement), true);
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

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(HoldDisbursement aHoldDisbursement, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aHoldDisbursement.getBefImage(), aHoldDisbursement);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aHoldDisbursement.getUserDetails(),
				getOverideMap());
	}

	public void setHoldDisbursementService(HoldDisbursementService holdDisbursementService) {
		this.holdDisbursementService = holdDisbursementService;
	}

}
