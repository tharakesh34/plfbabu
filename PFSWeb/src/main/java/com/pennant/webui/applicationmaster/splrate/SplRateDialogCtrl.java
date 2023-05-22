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
 * * FileName : SplRateDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified
 * Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.splrate;

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
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.SplRateService;
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
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/SplRate/splRateDialog.zul file.
 */
public class SplRateDialogCtrl extends GFCBaseCtrl<SplRate> {
	private static final long serialVersionUID = -6395413534622055634L;
	private static final Logger logger = LogManager.getLogger(SplRateDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SplRateDialog;
	protected ExtendedCombobox sRType;
	protected Datebox sREffDate;
	protected Decimalbox sRRate;
	protected Checkbox deleteRate;

	// not auto wired variables
	private SplRate splRate; // overHanded per parameter
	private transient SplRateListCtrl splRateListCtrl; // overHanded per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient SplRateService splRateService;

	/**
	 * default constructor.<br>
	 */
	public SplRateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SplRateDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected SplRate object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_SplRateDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SplRateDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("splRate")) {
				this.splRate = (SplRate) arguments.get("splRate");
				SplRate befImage = new SplRate();
				BeanUtils.copyProperties(this.splRate, befImage);
				this.splRate.setBefImage(befImage);

				setSplRate(this.splRate);
			} else {
				setSplRate(null);
			}

			doLoadWorkFlow(this.splRate.isWorkflow(), this.splRate.getWorkflowId(), this.splRate.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "SplRateDialog");
			}

			// READ OVERHANDED parameters !
			// we get the splRateListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete splRate here.
			if (arguments.containsKey("splRateListCtrl")) {
				setSplRateListCtrl((SplRateListCtrl) arguments.get("splRateListCtrl"));
			} else {
				setSplRateListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSplRate());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SplRateDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.sRType.setMaxlength(8);
		this.sREffDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.sRRate.setMaxlength(13);
		this.sRRate.setFormat(PennantConstants.rateFormate9);
		this.sRRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.sRRate.setScale(9);

		this.sRType.setMandatoryStyle(true);
		this.sRType.setModuleName("SplRateCode");
		this.sRType.setValueColumn("SRType");
		this.sRType.setDescColumn("SRTypeDesc");
		this.sRType.setValidateColumns(new String[] { "SRType" });

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SplRateDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SplRateDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SplRateDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SplRateDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_SplRateDialog);
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
		doWriteBeanToComponents(this.splRate.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSplRate SplRate
	 */
	public void doWriteBeanToComponents(SplRate aSplRate) {
		logger.debug("Entering");
		this.sRType.setValue(aSplRate.getSRType());
		this.sREffDate.setValue(aSplRate.getSREffDate());
		this.sRRate.setValue(aSplRate.getSRRate() == null ? BigDecimal.ZERO : aSplRate.getSRRate());
		this.deleteRate.setChecked(aSplRate.isDelExistingRates());

		if (aSplRate.isNewRecord()) {
			this.sRType.setDescription("");
		} else {
			this.sRType.setDescription(aSplRate.getLovDescSRTypeName());
		}
		this.recordStatus.setValue(aSplRate.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSplRate
	 */
	public void doWriteComponentsToBean(SplRate aSplRate) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSplRate.setLovDescSRTypeName(this.sRType.getDescription());
			aSplRate.setSRType(this.sRType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.sREffDate.isDisabled()) {
				dateValidation();
			}
			aSplRate.setSREffDate(this.sREffDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.sRRate.isReadonly() && this.sRRate.getValue() == null) {
				throw new WrongValueException(sRRate, Labels.getLabel("FIELD_NO_NUMBER",
						new String[] { Labels.getLabel("label_SplRateDialog_SRRate.value") }));
			}
			if (!this.sRRate.isReadonly() && !this.sREffDate.isDisabled()) {
				dateValidation();
			}
			aSplRate.setSRRate(this.sRRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSplRate.setDelExistingRates(this.deleteRate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aSplRate.setLastMdfDate(SysParamUtil.getAppDate());

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aSplRate.setRecordStatus(this.recordStatus.getValue());
		setSplRate(aSplRate);
		logger.debug("Leaving");
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

		if (this.sREffDate.getValue().before(dateBackward) || this.sREffDate.getValue().after(dateForward)) {
			throw new WrongValueException(sREffDate,
					Labels.getLabel("DATE_ALLOWED_RANGE",
							new String[] { Labels.getLabel("label_SplRateDialog_SREffDate.value"),
									DateUtil.formatToShortDate(dateBackward),
									DateUtil.formatToShortDate(dateForward) }));
		}
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aSplRate
	 */
	public void doShowDialog(SplRate aSplRate) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSplRate.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.sRType.focus();
		} else {
			this.sRRate.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aSplRate.getRecordType())) {
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
			 * if(splRate.getRecordStatus().equals(Labels.getLabel("Approved"))){ final boolean splRateDel=
			 * getSplRateService().getSplRateListById( splRate.getSRType(),splRate.getSREffDate()); if(splRateDel){
			 * this.btnDelete.setVisible(false); } }
			 */
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSplRate);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SplRateDialog.onClose();
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

		if (!this.sREffDate.isDisabled()) {
			this.sREffDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_SplRateDialog_SREffDate.value"), true));
		}
		if (!this.sRRate.isDisabled()) {
			this.sRRate.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_SplRateDialog_SRRate.value"), 9, false));
		}
		if (!this.sRType.isReadonly()) {
			this.sRType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_SplRateDialog_SRType.value"), null, true, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.sREffDate.setConstraint("");
		this.sRRate.setConstraint("");
		this.sRType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.sREffDate.setErrorMessage("");
		this.sRRate.setErrorMessage("");
		this.sRType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getSplRateListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final SplRate aSplRate = new SplRate();
		BeanUtils.copyProperties(getSplRate(), aSplRate);

		String keyReference = Labels.getLabel("label_SplRateDialog_SRType.value") + " : " + aSplRate.getSRType();

		doDelete(keyReference, aSplRate);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getSplRate().isNewRecord()) {
			this.sRType.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.sREffDate.setDisabled(false);
		} else {
			this.sRType.setReadonly(true);
			this.sREffDate.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		// this.sREffDate.setDisabled(isReadOnly("SplRateDialog_sREffDate"));
		this.sRRate.setReadonly(isReadOnly("SplRateDialog_sRRate"));
		this.deleteRate.setDisabled(isReadOnly("SplRateDialog_deleteRate"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.splRate.isNewRecord()) {
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
		this.sRType.setReadonly(true);
		this.sREffDate.setDisabled(true);
		this.sRRate.setReadonly(true);
		this.deleteRate.setDisabled(true);
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

		// remove validation, if there are a save before
		this.sRType.setValue("");
		this.sRType.setDescription("");
		this.sREffDate.setText("");
		this.sRRate.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SplRate aSplRate = new SplRate();
		BeanUtils.copyProperties(getSplRate(), aSplRate);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SplRate object with the components data
		doWriteComponentsToBean(aSplRate);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSplRate.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSplRate.getRecordType())) {
				aSplRate.setVersion(aSplRate.getVersion() + 1);
				if (isNew) {
					aSplRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSplRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSplRate.setNewRecord(true);
				}
			}
		} else {
			aSplRate.setVersion(aSplRate.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aSplRate, tranType)) {
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
	 * @param aSplRate (SplRate)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(SplRate aSplRate, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSplRate.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSplRate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSplRate.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSplRate.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSplRate.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSplRate);
				}

				if (isNotesMandatory(taskId, aSplRate)) {
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

			aSplRate.setTaskId(taskId);
			aSplRate.setNextTaskId(nextTaskId);
			aSplRate.setRoleCode(getRole());
			aSplRate.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSplRate, tranType);

			String operationRefs = getServiceOperations(taskId, aSplRate);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSplRate, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSplRate, tranType);
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
		SplRate aSplRate = (SplRate) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getSplRateService().delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = getSplRateService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getSplRateService().doApprove(auditHeader);

					if (aSplRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getSplRateService().doReject(auditHeader);
					if (aSplRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_SplRateDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_SplRateDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.getSplRate()), true);
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

	// WorkFlow Details

	/**
	 * Get Audit Header Details
	 * 
	 * @param aSubSegment (SubSegment)
	 * @param tranType    (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(SplRate aSplRate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSplRate.getBefImage(), aSplRate);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aSplRate.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.getSplRate());
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getSplRate().getSRType() + PennantConstants.KEY_SEPERATOR
				+ DateUtil.format(getSplRate().getSREffDate(), PennantConstants.DBDateFormat);
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

	public SplRate getSplRate() {
		return this.splRate;
	}

	public void setSplRate(SplRate splRate) {
		this.splRate = splRate;
	}

	public void setSplRateService(SplRateService splRateService) {
		this.splRateService = splRateService;
	}

	public SplRateService getSplRateService() {
		return this.splRateService;
	}

	public void setSplRateListCtrl(SplRateListCtrl splRateListCtrl) {
		this.splRateListCtrl = splRateListCtrl;
	}

	public SplRateListCtrl getSplRateListCtrl() {
		return this.splRateListCtrl;
	}

}
