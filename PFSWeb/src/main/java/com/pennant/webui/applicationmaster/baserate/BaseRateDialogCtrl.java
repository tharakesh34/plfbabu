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
 * * FileName : BaseRateDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.baserate;

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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.BaseRateService;
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
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/BaseRate/baseRateDialog.zul file.
 */
public class BaseRateDialogCtrl extends GFCBaseCtrl<BaseRate> {
	private static final long serialVersionUID = -5990530952612454146L;
	private static final Logger logger = LogManager.getLogger(BaseRateDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BaseRateDialog;

	protected ExtendedCombobox bRType;
	protected ExtendedCombobox currency;
	protected Datebox bREffDate;
	protected Decimalbox bRRate;
	protected Checkbox deleteRate;
	protected Checkbox bRTypeIsActive;

	// not autoWired Var's
	private BaseRate baseRate; // overHanded per parameter
	private transient BaseRateListCtrl baseRateListCtrl; // overHanded per
															// parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient BaseRateService baseRateService;

	/**
	 * default constructor.<br>
	 */
	public BaseRateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BaseRateDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected BaseRate object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_BaseRateDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BaseRateDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("baseRate")) {
				this.baseRate = (BaseRate) arguments.get("baseRate");
				BaseRate befImage = new BaseRate();
				BeanUtils.copyProperties(this.baseRate, befImage);
				this.baseRate.setBefImage(befImage);

				setBaseRate(this.baseRate);
			} else {
				setBaseRate(null);
			}

			doLoadWorkFlow(this.baseRate.isWorkflow(), this.baseRate.getWorkflowId(), this.baseRate.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "BaseRateDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the baseRateListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete baseRate here.
			if (arguments.containsKey("baseRateListCtrl")) {
				setBaseRateListCtrl((BaseRateListCtrl) arguments.get("baseRateListCtrl"));
			} else {
				setBaseRateListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getBaseRate());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BaseRateDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.bRType.setMaxlength(8);
		this.bREffDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.bRRate.setMaxlength(13);
		this.bRRate.setFormat(PennantConstants.rateFormate9);
		this.bRRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.bRRate.setScale(9);

		this.bRType.setMandatoryStyle(true);
		this.bRType.setModuleName("BaseRateCode");
		this.bRType.setValueColumn("BRType");
		this.bRType.setDescColumn("BRTypeDesc");
		this.bRType.setValidateColumns(new String[] { "BRType" });

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BaseRateDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BaseRateDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BaseRateDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BaseRateDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_BaseRateDialog);
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
		doWriteBeanToComponents(this.baseRate.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBaseRate BaseRate
	 */
	public void doWriteBeanToComponents(BaseRate aBaseRate) {
		logger.debug("Entering");
		this.bRType.setValue(aBaseRate.getBRType());
		this.currency.setValue(aBaseRate.getCurrency());
		this.bREffDate.setValue(aBaseRate.getBREffDate());
		this.bRRate.setValue(aBaseRate.getBRRate() == null ? BigDecimal.ZERO : aBaseRate.getBRRate());
		this.deleteRate.setChecked(aBaseRate.isDelExistingRates());
		this.bRTypeIsActive.setChecked(aBaseRate.isbRTypeIsActive());

		if (aBaseRate.isNewRecord() || (aBaseRate.getRecordType() != null ? aBaseRate.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.bRTypeIsActive.setChecked(true);
			this.bRTypeIsActive.setDisabled(true);
		}

		if (aBaseRate.getBREffDate() != null
				&& DateUtil.compare(aBaseRate.getBREffDate(), SysParamUtil.getAppDate()) < 0) {
			this.bRRate.setDisabled(true);
		}

		if (aBaseRate.isNewRecord()) {
			this.bRType.setDescription("");
			this.currency.setDescription("");
		} else {
			this.bRType.setDescription(aBaseRate.getLovDescBRTypeName());
		}

		this.recordStatus.setValue(aBaseRate.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBaseRate
	 */
	public void doWriteComponentsToBean(BaseRate aBaseRate) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aBaseRate.setLovDescBRTypeName(this.bRType.getDescription());
			aBaseRate.setBRType(this.bRType.getValidatedValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBaseRate.setCurrency(this.currency.getValidatedValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.bREffDate.isDisabled()) {
				dateValidation();
			}
			aBaseRate.setBREffDate(this.bREffDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBaseRate.setBRRate(this.bRRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBaseRate.setDelExistingRates(this.deleteRate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBaseRate.setbRTypeIsActive(this.bRTypeIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aBaseRate.setLastMdfDate(SysParamUtil.getAppDate());

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aBaseRate.setRecordStatus(this.recordStatus.getValue());
		setBaseRate(aBaseRate);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aBaseRate
	 */
	public void doShowDialog(BaseRate aBaseRate) {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aBaseRate.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bRType.focus();
		} else {
			this.bRRate.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aBaseRate.getRecordType())) {
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
			 * if(baseRate.getRecordStatus().equals(Labels.getLabel("Approved"))) { final boolean baseRateDel=
			 * getBaseRateService().getBaseRateListById( baseRate.getBRType(),baseRate.getBREffDate()); if(baseRateDel){
			 * this.btnDelete.setVisible(false); } }
			 */
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aBaseRate);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BaseRateDialog.onClose();
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

		if (!this.bREffDate.isDisabled()) {
			this.bREffDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_BaseRateDialog_BREffDate.value"), true));

		}
		if (!this.bRType.isReadonly()) {
			this.bRType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BaseRateDialog_BRType.value"), null, true, true));
		}
		if (!this.currency.isReadonly()) {
			this.currency.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BaseRateDialog_Currency.value"), null, true, true));
		}
		if (!this.bRRate.isReadonly()) {
			this.bRRate.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_BaseRateDialog_BRRate.value"), 9, true, false, 9999));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.bREffDate.setConstraint("");
		this.bRRate.setConstraint("");
		this.bRType.setConstraint("");
		this.currency.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.bREffDate.setErrorMessage("");
		this.bRRate.setErrorMessage("");
		this.bRType.setErrorMessage("");
		this.currency.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getBaseRateListCtrl().search();
	}

	/**
	 * Method for BaseRate Date Validation
	 */
	public void dateValidation() {
		Date curBussniessDate = SysParamUtil.getAppDate();
		int daysBackward = SysParamUtil.getValueAsInt("BVRC");
		Date dateBackward = DateUtil.addDays(curBussniessDate, daysBackward * -1);

		int daysForward = SysParamUtil.getValueAsInt("FVRC");
		Date dateForward = DateUtil.addDays(curBussniessDate, daysForward);

		if (this.bREffDate.getValue().before(dateBackward) || this.bREffDate.getValue().after(dateForward)) {
			throw new WrongValueException(bREffDate,
					Labels.getLabel("DATE_ALLOWED_RANGE",
							new String[] { Labels.getLabel("label_BaseRateDialog_BREffDate.value"),
									DateUtil.formatToShortDate(dateBackward),
									DateUtil.formatToShortDate(dateForward) }));
		}
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final BaseRate aBaseRate = new BaseRate();
		BeanUtils.copyProperties(getBaseRate(), aBaseRate);

		doDelete(Labels.getLabel("label_BaseRateDialog_BRType.value") + " : " + aBaseRate.getBRType(), aBaseRate);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getBaseRate().isNewRecord()) {
			this.bRType.setReadonly(false);
			this.currency.setReadonly(false);
			this.bREffDate.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.bRType.setReadonly(true);
			this.currency.setReadonly(true);
			this.bREffDate.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		this.bRRate.setReadonly(isReadOnly("BaseRateDialog_bRRate"));
		this.deleteRate.setDisabled(isReadOnly("BaseRateDialog_deleteRate"));
		this.bRTypeIsActive.setDisabled(isReadOnly("BaseRateDialog_BRTypeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.baseRate.isNewRecord()) {
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
		this.bRType.setReadonly(true);
		this.currency.setReadonly(true);
		this.bREffDate.setDisabled(true);
		this.bRRate.setReadonly(true);
		this.deleteRate.setDisabled(true);
		this.bRTypeIsActive.setDisabled(true);

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
		this.bRType.setValue("");
		this.bRType.setDescription("");
		this.currency.setValue("");
		this.currency.setDescription("");
		this.bREffDate.setText("");
		this.bRRate.setValue("0.00");
		this.bRTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final BaseRate aBaseRate = new BaseRate();
		BeanUtils.copyProperties(getBaseRate(), aBaseRate);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the BaseRate object with the components data
		doWriteComponentsToBean(aBaseRate);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBaseRate.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBaseRate.getRecordType())) {
				aBaseRate.setVersion(aBaseRate.getVersion() + 1);
				if (isNew) {
					aBaseRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBaseRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBaseRate.setNewRecord(true);
				}
			}
		} else {
			aBaseRate.setVersion(aBaseRate.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aBaseRate, tranType)) {
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
	 * @param aBaseRate (BaseRate)
	 * 
	 * @param tranType  (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(BaseRate aBaseRate, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBaseRate.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBaseRate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBaseRate.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBaseRate.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBaseRate.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBaseRate);
				}

				if (isNotesMandatory(taskId, aBaseRate)) {
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

			aBaseRate.setTaskId(taskId);
			aBaseRate.setNextTaskId(nextTaskId);
			aBaseRate.setRoleCode(getRole());
			aBaseRate.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBaseRate, tranType);

			String operationRefs = getServiceOperations(taskId, aBaseRate);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBaseRate, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBaseRate, tranType);
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
		BaseRate aBaseRate = (BaseRate) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getBaseRateService().delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = getBaseRateService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getBaseRateService().doApprove(auditHeader);

					if (aBaseRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getBaseRateService().doReject(auditHeader);
					if (aBaseRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_BaseRateDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_BaseRateDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.baseRate), true);
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
	 * @param aBaseRate
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(BaseRate aBaseRate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBaseRate.getBefImage(), aBaseRate);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aBaseRate.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.baseRate);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getBaseRate().getBRType() + PennantConstants.KEY_SEPERATOR
				+ DateUtil.format(getBaseRate().getBREffDate(), PennantConstants.DBDateFormat);
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

	public BaseRate getBaseRate() {
		return this.baseRate;
	}

	public void setBaseRate(BaseRate baseRate) {
		this.baseRate = baseRate;
	}

	public void setBaseRateService(BaseRateService baseRateService) {
		this.baseRateService = baseRateService;
	}

	public BaseRateService getBaseRateService() {
		return this.baseRateService;
	}

	public void setBaseRateListCtrl(BaseRateListCtrl baseRateListCtrl) {
		this.baseRateListCtrl = baseRateListCtrl;
	}

	public BaseRateListCtrl getBaseRateListCtrl() {
		return this.baseRateListCtrl;
	}

}
