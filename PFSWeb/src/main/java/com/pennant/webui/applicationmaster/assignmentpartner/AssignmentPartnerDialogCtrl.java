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
 * * FileName : AssignmentPartnerDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * *
 * Modified Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.assignmentpartner;

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
import com.pennant.backend.model.applicationmaster.AssignmentPartner;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.AssignmentPartnerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/AssignmentPartner/assignmentPartnerDialog.zul
 * file. <br>
 */
public class AssignmentPartnerDialogCtrl extends GFCBaseCtrl<AssignmentPartner> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AssignmentPartnerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AssignmentPartnerDialog;
	protected Uppercasebox code;
	protected Textbox description;
	protected ExtendedCombobox entityCode;
	protected ExtendedCombobox gLCode;
	protected Textbox sapCustCode;
	protected Checkbox active;
	private AssignmentPartner assignmentPartner; // overhanded per param

	private transient AssignmentPartnerListCtrl assignmentPartnerListCtrl; // overhanded per param
	private transient AssignmentPartnerService assignmentPartnerService;

	/**
	 * default constructor.<br>
	 */
	public AssignmentPartnerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssignmentPartnerDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.assignmentPartner.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssignmentPartnerDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AssignmentPartnerDialog);

		try {
			// Get the required arguments.
			this.assignmentPartner = (AssignmentPartner) arguments.get("assignmentPartner");
			this.assignmentPartnerListCtrl = (AssignmentPartnerListCtrl) arguments.get("assignmentPartnerListCtrl");

			if (this.assignmentPartner == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AssignmentPartner assignmentPartner = new AssignmentPartner();
			BeanUtils.copyProperties(this.assignmentPartner, assignmentPartner);
			this.assignmentPartner.setBefImage(assignmentPartner);

			// Render the page and display the data.
			doLoadWorkFlow(this.assignmentPartner.isWorkflow(), this.assignmentPartner.getWorkflowId(),
					this.assignmentPartner.getNextTaskId());

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
			doShowDialog(this.assignmentPartner);
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
		this.description.setMaxlength(50);
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });
		this.gLCode.setMandatoryStyle(true);
		this.gLCode.setModuleName("AccountType");
		this.gLCode.setValueColumn("AcType");
		this.gLCode.setDescColumn("AcTypeDesc");
		this.gLCode.setValidateColumns(new String[] { "AcType" });
		this.sapCustCode.setMaxlength(20);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssignmentPartnerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssignmentPartnerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssignmentPartnerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssignmentPartnerDialog_btnSave"));
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
		doShowNotes(this.assignmentPartner);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		assignmentPartnerListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.assignmentPartner.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param assignmentPartner
	 * 
	 */
	public void doWriteBeanToComponents(AssignmentPartner aAssignmentPartner) {
		logger.debug(Literal.ENTERING);

		this.code.setValue(aAssignmentPartner.getCode());
		this.description.setValue(aAssignmentPartner.getDescription());
		this.entityCode.setValue(aAssignmentPartner.getEntityCode());
		this.gLCode.setValue(aAssignmentPartner.getGLCode());
		this.sapCustCode.setValue(aAssignmentPartner.getSapCustCode());
		this.active.setChecked(aAssignmentPartner.isActive());

		if (!aAssignmentPartner.isNewRecord()) {
			this.entityCode.setValue(StringUtils.trimToEmpty(aAssignmentPartner.getEntityCode()),
					StringUtils.trimToEmpty(aAssignmentPartner.getEntityCodeName()));

			this.gLCode.setValue(StringUtils.trimToEmpty(aAssignmentPartner.getGLCode()),
					StringUtils.trimToEmpty(aAssignmentPartner.getGLCodeName()));
		}

		if (aAssignmentPartner.isNewRecord()
				|| StringUtils.equals(aAssignmentPartner.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}

		this.recordStatus.setValue(aAssignmentPartner.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAssignmentPartner
	 */
	public void doWriteComponentsToBean(AssignmentPartner aAssignmentPartner) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			aAssignmentPartner.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aAssignmentPartner.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Entity
		try {
			aAssignmentPartner.setEntityCode(this.entityCode.getValidatedValue());
			/*
			 * Object obj = this.entityCode.getAttribute("EntityCode"); if (obj != null) {
			 * aAssignmentPartner.setEntityCode((String.valueOf(obj))); }
			 */
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// G L Code
		try {
			aAssignmentPartner.setGLCode(this.gLCode.getValidatedValue());
			Object obj = this.gLCode.getAttribute("GLCode");
			if (obj != null) {
				aAssignmentPartner.setGLCode((String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// SAPCustCode
		try {
			aAssignmentPartner.setSapCustCode(this.sapCustCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aAssignmentPartner.setActive(this.active.isChecked());
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
	 * @param assignmentPartner The entity that need to be render.
	 */
	public void doShowDialog(AssignmentPartner assignmentPartner) {
		logger.debug(Literal.LEAVING);

		if (assignmentPartner.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.code.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(assignmentPartner.getRecordType())) {
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

		doWriteBeanToComponents(assignmentPartner);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_AssignmentPartnerDialog_Code.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_ALPHAFIRST, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssignmentPartnerDialog_Description.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AssignmentPartnerDialog_EntityCode.value"), null, true, true));
		}
		if (!this.gLCode.isReadonly()) {
			this.gLCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AssignmentPartnerDialog_GLCode.value"), null, true, true));
		}
		if (!this.sapCustCode.isReadonly()) {
			this.sapCustCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssignmentPartnerDialog_SAPCustCode.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.code.setConstraint("");
		this.description.setConstraint("");
		this.entityCode.setConstraint("");
		this.gLCode.setConstraint("");

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
		logger.debug(Literal.LEAVING);

		final AssignmentPartner aAssignmentPartner = new AssignmentPartner();
		BeanUtils.copyProperties(this.assignmentPartner, aAssignmentPartner);

		doDelete(aAssignmentPartner.getCode(), aAssignmentPartner);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.assignmentPartner.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.code);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.code);

		}

		readOnlyComponent(isReadOnly("AssignmentPartnerDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("AssignmentPartnerDialog_EntityCode"), this.entityCode);
		readOnlyComponent(isReadOnly("AssignmentPartnerDialog_GLCode"), this.gLCode);
		readOnlyComponent(isReadOnly("AssignmentPartnerDialog_SAPCustCode"), this.sapCustCode);
		readOnlyComponent(isReadOnly("AssignmentPartnerDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.assignmentPartner.isNewRecord()) {
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

		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.entityCode);
		readOnlyComponent(true, this.gLCode);
		readOnlyComponent(true, this.sapCustCode);
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
		this.code.setValue("");
		this.description.setValue("");
		this.entityCode.setValue("");
		this.entityCode.setDescription("");
		this.gLCode.setValue("");
		this.gLCode.setDescription("");
		this.sapCustCode.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final AssignmentPartner aAssignmentPartner = new AssignmentPartner();
		BeanUtils.copyProperties(this.assignmentPartner, aAssignmentPartner);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aAssignmentPartner);

		isNew = aAssignmentPartner.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAssignmentPartner.getRecordType())) {
				aAssignmentPartner.setVersion(aAssignmentPartner.getVersion() + 1);
				if (isNew) {
					aAssignmentPartner.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAssignmentPartner.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAssignmentPartner.setNewRecord(true);
				}
			}
		} else {
			aAssignmentPartner.setVersion(aAssignmentPartner.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aAssignmentPartner, tranType)) {
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
	protected boolean doProcess(AssignmentPartner aAssignmentPartner, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAssignmentPartner.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aAssignmentPartner.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAssignmentPartner.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAssignmentPartner.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAssignmentPartner.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAssignmentPartner);
				}

				if (isNotesMandatory(taskId, aAssignmentPartner)) {
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

			aAssignmentPartner.setTaskId(taskId);
			aAssignmentPartner.setNextTaskId(nextTaskId);
			aAssignmentPartner.setRoleCode(getRole());
			aAssignmentPartner.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAssignmentPartner, tranType);
			String operationRefs = getServiceOperations(taskId, aAssignmentPartner);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAssignmentPartner, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAssignmentPartner, tranType);
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
		AssignmentPartner aAssignmentPartner = (AssignmentPartner) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = assignmentPartnerService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = assignmentPartnerService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = assignmentPartnerService.doApprove(auditHeader);

					if (aAssignmentPartner.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = assignmentPartnerService.doReject(auditHeader);
					if (aAssignmentPartner.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AssignmentPartnerDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AssignmentPartnerDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.assignmentPartner), true);
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

	private AuditHeader getAuditHeader(AssignmentPartner aAssignmentPartner, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAssignmentPartner.getBefImage(), aAssignmentPartner);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAssignmentPartner.getUserDetails(),
				getOverideMap());
	}

	public void setAssignmentPartnerService(AssignmentPartnerService assignmentPartnerService) {
		this.assignmentPartnerService = assignmentPartnerService;
	}

}