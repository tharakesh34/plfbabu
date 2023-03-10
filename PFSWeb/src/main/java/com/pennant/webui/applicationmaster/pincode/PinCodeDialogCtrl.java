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
 * * FileName : PinCodeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-06-2017 * * Modified
 * Date : 01-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.pincode;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.service.applicationmaster.PinCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.service.hook.PinCodePostValidationHook;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/master/PinCode/pinCodeDialog.zul file. <br>
 */
public class PinCodeDialogCtrl extends GFCBaseCtrl<PinCode> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PinCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PinCodeDialog;
	protected Textbox pinCodes;
	protected ExtendedCombobox city;
	protected Checkbox active;
	private PinCode pinCode; // overhanded per param
	private Textbox areaName;
	private Longbox groupId;
	protected Checkbox isServiceable;

	private transient PinCodeListCtrl pinCodeListCtrl; // overhanded per param
	private transient PinCodeService pinCodeService;
	@Autowired(required = false)
	@Qualifier("pinCodePostValidationHook")
	private PinCodePostValidationHook<String, Integer> pinCodePostValidationHook;

	/**
	 * default constructor.<br>
	 */
	public PinCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PinCodeDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.pinCode.getPinCodeId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PinCodeDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PinCodeDialog);

		try {
			// Get the required arguments.
			this.pinCode = (PinCode) arguments.get("pincode");
			this.pinCodeListCtrl = (PinCodeListCtrl) arguments.get("pincodeListCtrl");

			if (this.pinCode == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			PinCode pinCode = new PinCode();
			BeanUtils.copyProperties(this.pinCode, pinCode);
			this.pinCode.setBefImage(pinCode);

			// Render the page and display the data.
			doLoadWorkFlow(this.pinCode.isWorkflow(), this.pinCode.getWorkflowId(), this.pinCode.getNextTaskId());

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
			doShowDialog(this.pinCode);
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

		this.pinCodes.setMaxlength(10);
		this.groupId.setMaxlength(8);
		this.city.setModuleName("CityVthCountry");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setMandatoryStyle(true);
		this.city.setValidateColumns(new String[] { "PCCity" });

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PinCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PinCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PinCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PinCodeDialog_btnSave"));
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
		doShowNotes(this.pinCode);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);

		this.active.setChecked(true);
		pinCodeListCtrl.fillListData();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.pinCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param pinCode
	 * 
	 */
	public void doWriteBeanToComponents(PinCode aPinCode) {
		logger.debug(Literal.ENTERING);

		this.pinCodes.setValue(aPinCode.getPinCode());
		this.city.setValue(aPinCode.getCity());
		this.active.setChecked(aPinCode.isActive());
		this.areaName.setValue(aPinCode.getAreaName());
		this.groupId.setValue(aPinCode.getGroupId());
		this.isServiceable.setChecked(aPinCode.isServiceable());
		this.recordStatus.setValue(aPinCode.getRecordStatus());
		if (aPinCode.isNewRecord()) {
			this.city.setDescription("");
		} else {
			this.city.setDescription(aPinCode.getPCCityName());
		}
		if (aPinCode.isNewRecord() || (aPinCode.getRecordType() != null ? aPinCode.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPinCode
	 */
	public void doWriteComponentsToBean(PinCode aPinCode) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// City
		try {
			aPinCode.setCity(this.city.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doSetCustomValidation(aPinCode);

		// Pin Code
		try {
			aPinCode.setPinCode(this.pinCodes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Area Name
		try {
			aPinCode.setAreaName(this.areaName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aPinCode.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Groupid
		try {
			aPinCode.setGroupId(this.groupId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// isSerivceable
		try {
			aPinCode.setServiceable(this.isServiceable.isChecked());
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

	private void doSetCustomValidation(PinCode aPinCode) {
		if (pinCodePostValidationHook == null) {
			return;
		}

		Object city = this.city.getObject();

		if (city == null)
			return;

		String country = ((City) city).getPCCountry();

		Integer minLen = pinCodePostValidationHook.getMinimumLength(country);
		if (minLen != null) {
			this.pinCodes.clearErrorMessage();
			this.pinCodes.setConstraint(new PTStringValidator(Labels.getLabel("label_PinCodeDialog_PinCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true, minLen, 10));
		}

	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param pinCode The entity that need to be render.
	 */
	public void doShowDialog(PinCode pinCode) {
		logger.debug(Literal.LEAVING);

		if (pinCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.pinCodes.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(pinCode.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.city.focus();
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

		doWriteBeanToComponents(pinCode);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);
		this.pinCodes.clearErrorMessage();

		if (!this.pinCodes.isReadonly()) {
			this.pinCodes.setConstraint(new PTStringValidator(Labels.getLabel("label_PinCodeDialog_PinCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true, 6, 10));
		}

		if (!this.city.isReadonly()) {
			this.city.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PinCodeDialog_City.value"), null, true));
		}
		if (!this.areaName.isReadonly()) {
			this.areaName.setConstraint(new PTStringValidator(Labels.getLabel("label_PinCodeDialog_AreaName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true, 3, 100));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.pinCodes.setConstraint("");
		this.city.setConstraint("");
		this.areaName.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// Pin Code Id
		// Pin Code
		// City
		// Active

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

		final PinCode aPinCode = new PinCode();
		BeanUtils.copyProperties(this.pinCode, aPinCode);

		doDelete(aPinCode.getPinCode(), aPinCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.pinCode.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.pinCodes);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.pinCodes);

		}

		readOnlyComponent(isReadOnly("PinCodeDialog_City"), this.city);
		readOnlyComponent(isReadOnly("PinCodeDialog_AreaName"), this.areaName);
		readOnlyComponent(isReadOnly("PinCodeDialog_Active"), this.active);
		readOnlyComponent(isReadOnly("PinCodeDialog_GroupId"), this.groupId);
		readOnlyComponent(isReadOnly("PinCodeDialog_IsServiceable"), this.isServiceable);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.pinCode.isNewRecord()) {
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

		readOnlyComponent(true, this.pinCodes);
		readOnlyComponent(true, this.city);
		readOnlyComponent(true, this.areaName);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.groupId);
		readOnlyComponent(true, this.isServiceable);

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
		this.pinCodes.setValue("");
		this.city.setValue("");
		this.city.setDescription("");
		this.active.setChecked(false);
		this.isServiceable.setChecked(false);
		this.groupId.setValue(0L);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final PinCode aPinCode = new PinCode();
		BeanUtils.copyProperties(this.pinCode, aPinCode);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aPinCode);

		isNew = aPinCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPinCode.getRecordType())) {
				aPinCode.setVersion(aPinCode.getVersion() + 1);
				if (isNew) {
					aPinCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPinCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPinCode.setNewRecord(true);
				}
			}
		} else {
			aPinCode.setVersion(aPinCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aPinCode, tranType)) {
				refreshList();
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
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(PinCode aPinCode, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPinCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aPinCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPinCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPinCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPinCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPinCode);
				}

				if (isNotesMandatory(taskId, aPinCode)) {
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

			aPinCode.setTaskId(taskId);
			aPinCode.setNextTaskId(nextTaskId);
			aPinCode.setRoleCode(getRole());
			aPinCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPinCode, tranType);
			String operationRefs = getServiceOperations(taskId, aPinCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPinCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPinCode, tranType);
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
		PinCode aPinCode = (PinCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = pinCodeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = pinCodeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = pinCodeService.doApprove(auditHeader);

					if (aPinCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = pinCodeService.doReject(auditHeader);
					if (aPinCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_PinCodeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_PinCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.pinCode), true);
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

	private AuditHeader getAuditHeader(PinCode aPinCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPinCode.getBefImage(), aPinCode);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aPinCode.getUserDetails(),
				getOverideMap());
	}

	public void setPinCodeService(PinCodeService pinCodeService) {
		this.pinCodeService = pinCodeService;
	}

}
