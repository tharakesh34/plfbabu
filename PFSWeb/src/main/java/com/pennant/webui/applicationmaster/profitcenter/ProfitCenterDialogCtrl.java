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
 * * FileName : ProfitCenterDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * *
 * Modified Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.profitcenter;

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

import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.ProfitCenterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/ProfitCenter/profitCenterDialog.zul file. <br>
 */
public class ProfitCenterDialogCtrl extends GFCBaseCtrl<ProfitCenter> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ProfitCenterDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProfitCenterDialog;
	protected Textbox profitCenterCode;
	protected Textbox profitCenterDesc;
	protected Checkbox active;
	private ProfitCenter profitCenter; // overhanded per param

	private transient ProfitCenterListCtrl profitCenterListCtrl; // overhanded per param
	private transient ProfitCenterService profitCenterService;

	/**
	 * default constructor.<br>
	 */
	public ProfitCenterDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProfitCenterDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.profitCenter.getProfitCenterID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ProfitCenterDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ProfitCenterDialog);

		try {
			// Get the required arguments.
			this.profitCenter = (ProfitCenter) arguments.get("profitCenter");
			this.profitCenterListCtrl = (ProfitCenterListCtrl) arguments.get("profitCenterListCtrl");

			if (this.profitCenter == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			ProfitCenter profitCenter = new ProfitCenter();
			BeanUtils.copyProperties(this.profitCenter, profitCenter);
			this.profitCenter.setBefImage(profitCenter);

			// Render the page and display the data.
			doLoadWorkFlow(this.profitCenter.isWorkflow(), this.profitCenter.getWorkflowId(),
					this.profitCenter.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.profitCenter);
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

		this.profitCenterCode.setMaxlength(15);
		this.profitCenterDesc.setMaxlength(50);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProfitCenterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProfitCenterDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProfitCenterDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProfitCenterDialog_btnSave"));
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
		doShowNotes(this.profitCenter);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		profitCenterListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.profitCenter.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param profitCenter
	 * 
	 */
	public void doWriteBeanToComponents(ProfitCenter aProfitCenter) {
		logger.debug(Literal.ENTERING);

		this.profitCenterCode.setValue(aProfitCenter.getProfitCenterCode());
		this.profitCenterDesc.setValue(aProfitCenter.getProfitCenterDesc());
		this.active.setChecked(aProfitCenter.isActive());
		if (aProfitCenter.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(aProfitCenter.getRecordType())) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(aProfitCenter.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProfitCenter
	 */
	public void doWriteComponentsToBean(ProfitCenter aProfitCenter) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Profit Center Code
		try {
			aProfitCenter.setProfitCenterCode(this.profitCenterCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Profit Center Description
		try {
			aProfitCenter.setProfitCenterDesc(this.profitCenterDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aProfitCenter.setActive(this.active.isChecked());
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
	 * @param profitCenter The entity that need to be render.
	 */
	public void doShowDialog(ProfitCenter profitCenter) {
		logger.debug(Literal.LEAVING);

		if (profitCenter.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.profitCenterCode.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(profitCenter.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.profitCenterDesc.focus();
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

		doWriteBeanToComponents(profitCenter);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.profitCenterCode.isReadonly()) {
			this.profitCenterCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ProfitCenterDialog_ProfitCenterCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FSLASH_SPACE, true));
		}
		if (!this.profitCenterDesc.isReadonly()) {
			this.profitCenterDesc.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ProfitCenterDialog_ProfitCenterDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.profitCenterCode.setConstraint("");
		this.profitCenterDesc.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// Profit Center ID
		// Profit Center Code
		// Profit Center Description
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

		final ProfitCenter aProfitCenter = new ProfitCenter();
		BeanUtils.copyProperties(this.profitCenter, aProfitCenter);

		doDelete(Labels.getLabel("label_ProfitCenterDialog_ProfitCenterCode.value") + " : "
				+ aProfitCenter.getProfitCenterCode(), aProfitCenter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.profitCenter.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.profitCenterCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.profitCenterCode);

		}

		readOnlyComponent(isReadOnly("ProfitCenterDialog_ProfitCenterDesc"), this.profitCenterDesc);
		readOnlyComponent(isReadOnly("ProfitCenterDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.profitCenter.isNewRecord()) {
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

		readOnlyComponent(true, this.profitCenterCode);
		readOnlyComponent(true, this.profitCenterDesc);
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
		this.profitCenterCode.setValue("");
		this.profitCenterDesc.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final ProfitCenter aProfitCenter = new ProfitCenter();
		BeanUtils.copyProperties(this.profitCenter, aProfitCenter);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aProfitCenter);

		isNew = aProfitCenter.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProfitCenter.getRecordType())) {
				aProfitCenter.setVersion(aProfitCenter.getVersion() + 1);
				if (isNew) {
					aProfitCenter.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProfitCenter.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProfitCenter.setNewRecord(true);
				}
			}
		} else {
			aProfitCenter.setVersion(aProfitCenter.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aProfitCenter, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
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
	protected boolean doProcess(ProfitCenter aProfitCenter, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aProfitCenter.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aProfitCenter.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProfitCenter.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aProfitCenter.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProfitCenter.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aProfitCenter);
				}

				if (isNotesMandatory(taskId, aProfitCenter)) {
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

			aProfitCenter.setTaskId(taskId);
			aProfitCenter.setNextTaskId(nextTaskId);
			aProfitCenter.setRoleCode(getRole());
			aProfitCenter.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProfitCenter, tranType);
			String operationRefs = getServiceOperations(taskId, aProfitCenter);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProfitCenter, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aProfitCenter, tranType);
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
		ProfitCenter aProfitCenter = (ProfitCenter) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = profitCenterService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = profitCenterService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = profitCenterService.doApprove(auditHeader);

					if (aProfitCenter.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = profitCenterService.doReject(auditHeader);
					if (aProfitCenter.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ProfitCenterDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ProfitCenterDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.profitCenter), true);
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

	private AuditHeader getAuditHeader(ProfitCenter aProfitCenter, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProfitCenter.getBefImage(), aProfitCenter);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aProfitCenter.getUserDetails(),
				getOverideMap());
	}

	public void setProfitCenterService(ProfitCenterService profitCenterService) {
		this.profitCenterService = profitCenterService;
	}

}
