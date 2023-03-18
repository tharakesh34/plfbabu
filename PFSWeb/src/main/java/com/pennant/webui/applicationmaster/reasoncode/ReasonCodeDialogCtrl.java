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
 * * FileName : ReasonCodeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-12-2017 * * Modified
 * Date : 19-12-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-12-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.reasoncode;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.ReasonCategory;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.applicationmaster.ReasonTypes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.ReasonCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ReasonCode/reasonCodeDialog.zul file. <br>
 */
public class ReasonCodeDialogCtrl extends GFCBaseCtrl<ReasonCode> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ReasonCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReasonCodeDialog;
	protected ExtendedCombobox reasonTypeID;
	protected ExtendedCombobox reasonCategoryID;
	protected Textbox code;
	protected Textbox description;
	protected Checkbox active;
	private ReasonCode reasonCode; // overhanded per param

	private transient ReasonCodeListCtrl reasonCodeListCtrl; // overhanded per
																// param
	private transient ReasonCodeService reasonCodeService;

	/**
	 * default constructor.<br>
	 */
	public ReasonCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ReasonCodeDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.reasonCode.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ReasonCodeDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ReasonCodeDialog);

		try {
			// Get the required arguments.
			this.reasonCode = (ReasonCode) arguments.get("reasonCode");
			this.reasonCodeListCtrl = (ReasonCodeListCtrl) arguments.get("reasonCodeListCtrl");

			if (this.reasonCode == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			ReasonCode reasonCode = new ReasonCode();
			BeanUtils.copyProperties(this.reasonCode, reasonCode);
			this.reasonCode.setBefImage(reasonCode);

			// Render the page and display the data.
			doLoadWorkFlow(this.reasonCode.isWorkflow(), this.reasonCode.getWorkflowId(),
					this.reasonCode.getNextTaskId());

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
			doShowDialog(this.reasonCode);
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

		this.code.setMaxlength(8);
		this.description.setMaxlength(200);

		this.reasonTypeID.setMaxlength(10);
		this.reasonTypeID.setMandatoryStyle(true);
		this.reasonTypeID.setModuleName("ReasonTypes");
		this.reasonTypeID.setValueColumn("Code");
		this.reasonTypeID.setDescColumn("Description");
		this.reasonTypeID.setValidateColumns(new String[] { "Code" });

		this.reasonCategoryID.setMaxlength(10);
		this.reasonCategoryID.setMandatoryStyle(true);
		this.reasonCategoryID.setModuleName("ReasonCategory");
		this.reasonCategoryID.setValueColumn("Code");
		this.reasonCategoryID.setDescColumn("Description");
		this.reasonCategoryID.setValidateColumns(new String[] { "Code" });

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$reasonTypeID(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = reasonTypeID.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.reasonTypeID.setValue("");
			this.reasonTypeID.setDescription("");
			this.reasonTypeID.setAttribute("ReasonTypeId", null);
		} else {
			ReasonTypes details = (ReasonTypes) dataObject;
			this.reasonTypeID.setAttribute("ReasonTypeId", details.getId());
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$reasonCategoryID(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = reasonCategoryID.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.reasonCategoryID.setValue("");
			this.reasonCategoryID.setDescription("");
			this.reasonCategoryID.setAttribute("ReasonCategoryId", null);
		} else {
			ReasonCategory details = (ReasonCategory) dataObject;
			this.reasonCategoryID.setAttribute("ReasonCategoryId", details.getId());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ReasonCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ReasonCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ReasonCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ReasonCodeDialog_btnSave"));
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
		doShowNotes(this.reasonCode);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		reasonCodeListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.reasonCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param reasonCode
	 * 
	 */
	public void doWriteBeanToComponents(ReasonCode aReasonCode) {
		logger.debug(Literal.ENTERING);

		this.reasonCategoryID.setValue(String.valueOf(aReasonCode.getReasonCategoryID()));
		this.reasonTypeID.setValue(String.valueOf(aReasonCode.getReasonTypeID()));
		this.code.setValue(aReasonCode.getCode());
		this.description.setValue(aReasonCode.getDescription());
		this.active.setChecked(aReasonCode.isActive());

		if (aReasonCode.isNewRecord()) {
			this.reasonCategoryID.setDescription("");
			this.reasonTypeID.setDescription("");
			this.reasonCategoryID.setValue("");
			this.reasonTypeID.setValue("");
		} else {
			// Reason Type
			this.reasonTypeID.setValue(StringUtils.trimToEmpty(aReasonCode.getReasonTypeCode()),
					StringUtils.trimToEmpty(aReasonCode.getReasonTypeDesc()));
			if (aReasonCode.getReasonTypeID() != null) {
				this.reasonTypeID.setAttribute("ReasonTypeId", aReasonCode.getReasonTypeID());
			} else {
				this.reasonTypeID.setAttribute("ReasonTypeId", null);
			}
			// Reason Category
			this.reasonCategoryID.setValue(StringUtils.trimToEmpty(aReasonCode.getReasonCategoryCode()),
					StringUtils.trimToEmpty(aReasonCode.getReasonCategoryDesc()));
			if (aReasonCode.getReasonTypeID() != null) {
				this.reasonCategoryID.setAttribute("ReasonCategoryId", aReasonCode.getReasonCategoryID());
			} else {
				this.reasonCategoryID.setAttribute("ReasonCategoryId", null);
			}
		}

		if (aReasonCode.isNewRecord() || (aReasonCode.getRecordType() != null ? aReasonCode.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(aReasonCode.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aReasonCode
	 */
	public void doWriteComponentsToBean(ReasonCode aReasonCode) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Reason Type
		try {
			aReasonCode.setReasonTypeCode(this.reasonTypeID.getValue());
			aReasonCode.setReasonTypeDesc(this.reasonTypeID.getDescription());
			this.reasonTypeID.getValidatedValue();
			Object object = this.reasonTypeID.getAttribute("ReasonTypeId");
			if (object != null) {
				aReasonCode.setReasonTypeID(Long.parseLong(object.toString()));
			} else {
				aReasonCode.setReasonTypeID(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Reason Category
		try {
			aReasonCode.setReasonCategoryCode(this.reasonCategoryID.getValue());
			aReasonCode.setReasonCategoryDesc(this.reasonCategoryID.getDescription());
			this.reasonCategoryID.getValidatedValue();
			Object object = this.reasonCategoryID.getAttribute("ReasonCategoryId");
			if (object != null) {
				aReasonCode.setReasonCategoryID(Long.parseLong(object.toString()));
			} else {
				aReasonCode.setReasonCategoryID(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Code
		try {
			aReasonCode.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aReasonCode.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aReasonCode.setActive(this.active.isChecked());
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
	 * @param reasonCode The entity that need to be render.
	 */
	public void doShowDialog(ReasonCode reasonCode) {
		logger.debug(Literal.LEAVING);

		if (reasonCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.reasonTypeID.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(reasonCode.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.description.focus();
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

		doWriteBeanToComponents(reasonCode);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.reasonTypeID.isReadonly()) {
			this.reasonTypeID.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReasonCodeDialog_ReasonTypeID.value"), null, true, true));
		}
		if (!this.reasonCategoryID.isReadonly()) {
			this.reasonCategoryID.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ReasonCodeDialog_ReasonCategoryID.value"), null, true, true));
		}
		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_ReasonCodeDialog_Code.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ReasonCodeDialog_Description.value"), null, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.reasonTypeID.setConstraint("");
		this.reasonCategoryID.setConstraint("");
		this.code.setConstraint("");
		this.description.setConstraint("");

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

		final ReasonCode aReasonCode = new ReasonCode();
		BeanUtils.copyProperties(this.reasonCode, aReasonCode);

		doDelete(aReasonCode.getCode(), aReasonCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.reasonCode.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.reasonTypeID);
			readOnlyComponent(false, this.reasonCategoryID);
			readOnlyComponent(false, this.code);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.reasonTypeID);
			readOnlyComponent(true, this.reasonCategoryID);
			readOnlyComponent(true, this.code);

		}

		readOnlyComponent(isReadOnly("ReasonCodeDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("ReasonCodeDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.reasonCode.isNewRecord()) {
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

		readOnlyComponent(true, this.reasonTypeID);
		readOnlyComponent(true, this.reasonCategoryID);
		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.active);

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
		this.reasonTypeID.setValue("");
		this.reasonCategoryID.setValue("");
		this.code.setValue("");
		this.description.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final ReasonCode aReasonCode = new ReasonCode();
		BeanUtils.copyProperties(this.reasonCode, aReasonCode);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aReasonCode);

		isNew = aReasonCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aReasonCode.getRecordType())) {
				aReasonCode.setVersion(aReasonCode.getVersion() + 1);
				if (isNew) {
					aReasonCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aReasonCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aReasonCode.setNewRecord(true);
				}
			}
		} else {
			aReasonCode.setVersion(aReasonCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aReasonCode, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
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
	protected boolean doProcess(ReasonCode aReasonCode, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aReasonCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aReasonCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aReasonCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aReasonCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aReasonCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aReasonCode);
				}

				if (isNotesMandatory(taskId, aReasonCode)) {
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

			aReasonCode.setTaskId(taskId);
			aReasonCode.setNextTaskId(nextTaskId);
			aReasonCode.setRoleCode(getRole());
			aReasonCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aReasonCode, tranType);
			String operationRefs = getServiceOperations(taskId, aReasonCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aReasonCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aReasonCode, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
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
		ReasonCode aReasonCode = (ReasonCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = reasonCodeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = reasonCodeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = reasonCodeService.doApprove(auditHeader);

					if (aReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = reasonCodeService.doReject(auditHeader);
					if (aReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ReasonCodeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ReasonCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.reasonCode), true);
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

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ReasonCode aReasonCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aReasonCode.getBefImage(), aReasonCode);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aReasonCode.getUserDetails(),
				getOverideMap());
	}

	public void setReasonCodeService(ReasonCodeService reasonCodeService) {
		this.reasonCodeService = reasonCodeService;
	}

}
