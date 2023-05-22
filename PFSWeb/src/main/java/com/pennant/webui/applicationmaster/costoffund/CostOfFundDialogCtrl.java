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
 * * FileName : CostOfFundDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.costoffund;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.CostOfFund;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.CostOfFundService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/CostOfFund/costOfFundDialog.zul file.
 */
public class CostOfFundDialogCtrl extends GFCBaseCtrl<CostOfFund> {
	private static final long serialVersionUID = -5990530952612454146L;
	private static final Logger logger = LogManager.getLogger(CostOfFundDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CostOfFundDialog;

	protected ExtendedCombobox cofCode;
	protected ExtendedCombobox currency;
	protected Datebox cofEffDate;
	protected Decimalbox cofRate;
	protected Checkbox deleteRate;
	protected Checkbox active;

	// not autoWired Var's
	private CostOfFund costOfFund; // overHanded per parameter
	private transient CostOfFundListCtrl costOfFundListCtrl; // overHanded per
																// parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient CostOfFundService costOfFundService;

	/**
	 * default constructor.<br>
	 */
	public CostOfFundDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CostOfFundsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CostOfFund object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CostOfFundDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CostOfFundDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("costOfFund")) {
				this.costOfFund = (CostOfFund) arguments.get("costOfFund");
				CostOfFund befImage = new CostOfFund();
				BeanUtils.copyProperties(this.costOfFund, befImage);
				this.costOfFund.setBefImage(befImage);

				setCostOfFund(this.costOfFund);
			} else {
				setCostOfFund(null);
			}

			doLoadWorkFlow(this.costOfFund.isWorkflow(), this.costOfFund.getWorkflowId(),
					this.costOfFund.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the costOfFundListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete costOfFund here.
			if (arguments.containsKey("costOfFundListCtrl")) {
				setCostOfFundListCtrl((CostOfFundListCtrl) arguments.get("costOfFundListCtrl"));
			} else {
				setCostOfFundListCtrl(null);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCostOfFund());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CostOfFundDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.cofCode.setMaxlength(8);
		this.cofEffDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.cofRate.setMaxlength(13);
		this.cofRate.setFormat(PennantConstants.rateFormate9);
		this.cofRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.cofRate.setScale(9);

		this.cofCode.setMandatoryStyle(true);
		this.cofCode.setModuleName("CostOfFundCode");
		this.cofCode.setValueColumn("CofCode");
		this.cofCode.setDescColumn("CofDesc");
		this.cofCode.setValidateColumns(new String[] { "CofCode" });

		this.currency.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.currency.setMandatoryStyle(true);
		this.currency.setModuleName("Currency");
		this.currency.setValueColumn("CcyCode");
		this.currency.setDescColumn("CcyDesc");
		this.currency.setValidateColumns(new String[] { "CcyCode" });

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CostOfFundsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CostOfFundsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CostOfFundsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CostOfFundsDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CostOfFundDialog);
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
		doWriteBeanToComponents(this.costOfFund.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCostOfFund CostOfFund
	 */
	public void doWriteBeanToComponents(CostOfFund aCostOfFund) {
		logger.debug("Entering");
		this.cofCode.setValue(aCostOfFund.getCofCode());
		this.currency.setValue(aCostOfFund.getCurrency());
		this.cofEffDate.setValue(aCostOfFund.getCofEffDate());
		this.cofRate.setValue(
				CurrencyUtil.parse(aCostOfFund.getCofRate() == null ? BigDecimal.ZERO : aCostOfFund.getCofRate(),
						PennantConstants.defaultCCYDecPos));
		this.deleteRate.setChecked(aCostOfFund.isDelExistingRates());
		this.active.setChecked(aCostOfFund.isActive());

		if (aCostOfFund.isNewRecord() || (aCostOfFund.getRecordType() != null ? aCostOfFund.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}

		if (aCostOfFund.isNewRecord()) {
			this.cofCode.setDescription("");
			this.currency.setDescription("");
		} else {
			this.cofCode.setDescription(aCostOfFund.getLovDescCofTypeName());
		}

		this.recordStatus.setValue(aCostOfFund.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCostOfFund
	 */
	public void doWriteComponentsToBean(CostOfFund aCostOfFund) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCostOfFund.setLovDescCofTypeName(this.cofCode.getDescription());
			aCostOfFund.setCofCode(this.cofCode.getValidatedValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCostOfFund.setCurrency(this.currency.getValidatedValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.cofEffDate.isDisabled()) {
				dateValidation();
			}
			aCostOfFund.setCofEffDate(this.cofEffDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCostOfFund.setCofRate(CurrencyUtil.unFormat(this.cofRate.getValue(), PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCostOfFund.setDelExistingRates(this.deleteRate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCostOfFund.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aCostOfFund.setLastMdfDate(SysParamUtil.getAppDate());

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCostOfFund.setRecordStatus(this.recordStatus.getValue());
		setCostOfFund(aCostOfFund);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCostOfFund
	 */
	public void doShowDialog(CostOfFund aCostOfFund) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aCostOfFund.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cofCode.focus();
		} else {
			this.cofRate.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aCostOfFund.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}

			// Checking condition for deletion of Object or not
			/*
			 * if(costOfFund.getRecordStatus().equals(Labels.getLabel("Approved"))) { final boolean costOfFundDel=
			 * getCostOfFundService().getCostOfFundListById( costOfFund.getBRType(),costOfFund.getBREffDate());
			 * if(costOfFundDel){ this.btnDelete.setVisible(false); } }
			 */
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCostOfFund);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CostOfFundDialog.onClose();
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

		if (!this.cofEffDate.isDisabled()) {
			this.cofEffDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_CostOfFundDialog_CofEffDate.value"), true));

		}
		if (!this.cofCode.isReadonly()) {
			this.cofCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CostOfFundDialog_CofCode.value"), null, true, true));
		}
		if (!this.currency.isReadonly()) {
			this.currency.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CostOfFundDialog_Currency.value"), null, true, true));
		}
		if (!this.cofRate.isReadonly()) {
			this.cofRate.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CostOfFundDialog_CofRate.value"),
					PennantConstants.defaultCCYDecPos, true, false, 100));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cofEffDate.setConstraint("");
		this.cofRate.setConstraint("");
		this.cofCode.setConstraint("");
		this.currency.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.cofEffDate.setErrorMessage("");
		this.cofRate.setErrorMessage("");
		this.cofCode.setErrorMessage("");
		this.currency.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCostOfFundListCtrl().search();
	}

	/**
	 * Method for CostOfFund Date Validation
	 */
	public void dateValidation() {
		Date curBussniessDate = SysParamUtil.getAppDate();
		int daysBackward = SysParamUtil.getValueAsInt("BVRC");
		Date dateBackward = DateUtil.addDays(curBussniessDate, daysBackward * -1);

		int daysForward = SysParamUtil.getValueAsInt("FVRC");
		Date dateForward = DateUtil.addDays(curBussniessDate, daysForward);

		if (this.cofEffDate.getValue().before(dateBackward) || this.cofEffDate.getValue().after(dateForward)) {
			throw new WrongValueException(cofEffDate,
					Labels.getLabel("DATE_ALLOWED_RANGE",
							new String[] { Labels.getLabel("label_CostOfFundDialog_CofEffDate.value"),
									DateUtil.formatToShortDate(dateBackward),
									DateUtil.formatToShortDate(dateForward) }));
		}
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CostOfFund aCostOfFund = new CostOfFund();
		BeanUtils.copyProperties(getCostOfFund(), aCostOfFund);

		doDelete(Labels.getLabel("label_CostOfFundDialog_CofCode.value") + " : " + aCostOfFund.getCofCode(),
				aCostOfFund);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCostOfFund().isNewRecord()) {
			this.cofCode.setReadonly(false);
			this.currency.setReadonly(false);
			this.cofEffDate.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.cofCode.setReadonly(true);
			this.currency.setReadonly(true);
			this.cofEffDate.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.cofRate.setReadonly(isReadOnly("CostOfFundsDialog_cofRate"));
		this.deleteRate.setDisabled(isReadOnly("CostOfFundsDialog_delExistingRates"));
		this.active.setDisabled(isReadOnly("CostOfFundsDialog_active"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.costOfFund.isNewRecord()) {
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
		this.cofCode.setReadonly(true);
		this.currency.setReadonly(true);
		this.cofEffDate.setDisabled(true);
		this.cofRate.setReadonly(true);
		this.deleteRate.setDisabled(true);
		this.active.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}

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
		this.cofCode.setValue("");
		this.cofCode.setDescription("");
		this.currency.setValue("");
		this.currency.setDescription("");
		this.cofEffDate.setText("");
		this.cofRate.setValue("0.00");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CostOfFund aCostOfFund = new CostOfFund();
		BeanUtils.copyProperties(getCostOfFund(), aCostOfFund);

		boolean isNew = false;
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CostOfFund object with the components data
		doWriteComponentsToBean(aCostOfFund);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCostOfFund.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCostOfFund.getRecordType())) {
				aCostOfFund.setVersion(aCostOfFund.getVersion() + 1);
				if (isNew) {
					aCostOfFund.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCostOfFund.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCostOfFund.setNewRecord(true);
				}
			}
		} else {
			aCostOfFund.setVersion(aCostOfFund.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCostOfFund, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCostOfFund (CostOfFund)
	 * 
	 * @param tranType    (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(CostOfFund aCostOfFund, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCostOfFund.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCostOfFund.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCostOfFund.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCostOfFund.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCostOfFund.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCostOfFund);
				}

				if (isNotesMandatory(taskId, aCostOfFund)) {
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

			aCostOfFund.setTaskId(taskId);
			aCostOfFund.setNextTaskId(nextTaskId);
			aCostOfFund.setRoleCode(getRole());
			aCostOfFund.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCostOfFund, tranType);

			String operationRefs = getServiceOperations(taskId, aCostOfFund);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCostOfFund, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCostOfFund, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
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
		CostOfFund aCostOfFund = (CostOfFund) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getCostOfFundService().delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = getCostOfFundService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getCostOfFundService().doApprove(auditHeader);

					if (aCostOfFund.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getCostOfFundService().doReject(auditHeader);
					if (aCostOfFund.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CostOfFundDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_CostOfFundDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.costOfFund), true);
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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCostOfFund
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CostOfFund aCostOfFund, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCostOfFund.getBefImage(), aCostOfFund);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aCostOfFund.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.costOfFund);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCostOfFund().getCofCode() + PennantConstants.KEY_SEPERATOR
				+ DateUtil.format(getCostOfFund().getCofEffDate(), PennantConstants.DBDateFormat);
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

	public CostOfFund getCostOfFund() {
		return this.costOfFund;
	}

	public void setCostOfFund(CostOfFund costOfFund) {
		this.costOfFund = costOfFund;
	}

	public void setCostOfFundService(CostOfFundService costOfFundService) {
		this.costOfFundService = costOfFundService;
	}

	public CostOfFundService getCostOfFundService() {
		return this.costOfFundService;
	}

	public void setCostOfFundListCtrl(CostOfFundListCtrl costOfFundListCtrl) {
		this.costOfFundListCtrl = costOfFundListCtrl;
	}

	public CostOfFundListCtrl getCostOfFundListCtrl() {
		return this.costOfFundListCtrl;
	}

}
