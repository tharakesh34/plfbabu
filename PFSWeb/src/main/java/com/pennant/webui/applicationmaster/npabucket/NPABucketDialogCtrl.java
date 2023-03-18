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
 * * FileName : NPABucketDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-04-2017 * * Modified
 * Date : 21-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.npabucket;

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

import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.NPABucketService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/NPABucket/nPABucketDialog.zul file. <br>
 */
public class NPABucketDialogCtrl extends GFCBaseCtrl<NPABucket> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(NPABucketDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_NPABucketDialog;
	protected Textbox bucketCode;
	protected Textbox bucketDesc;
	protected Checkbox active;
	private NPABucket nPABucket; // overhanded per param

	private transient NPABucketListCtrl nPABucketListCtrl; // overhanded per param
	private transient NPABucketService nPABucketService;

	/**
	 * default constructor.<br>
	 */
	public NPABucketDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "NPABucketDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.nPABucket.getBucketID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_NPABucketDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_NPABucketDialog);

		try {
			// Get the required arguments.
			this.nPABucket = (NPABucket) arguments.get("npabucket");
			this.nPABucketListCtrl = (NPABucketListCtrl) arguments.get("npabucketListCtrl");

			if (this.nPABucket == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			NPABucket nPABucket = new NPABucket();
			BeanUtils.copyProperties(this.nPABucket, nPABucket);
			this.nPABucket.setBefImage(nPABucket);

			// Render the page and display the data.
			doLoadWorkFlow(this.nPABucket.isWorkflow(), this.nPABucket.getWorkflowId(), this.nPABucket.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.nPABucket);
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

		this.bucketCode.setMaxlength(8);
		this.bucketDesc.setMaxlength(50);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_NPABucketDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_NPABucketDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_NPABucketDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_NPABucketDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
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
		doCancel();
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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.nPABucket);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		nPABucketListCtrl.search();
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.nPABucket.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param nPABucket
	 * 
	 */
	public void doWriteBeanToComponents(NPABucket aNPABucket) {
		logger.debug(Literal.ENTERING);

		this.bucketCode.setValue(aNPABucket.getBucketCode());
		this.bucketDesc.setValue(aNPABucket.getBucketDesc());
		this.active.setChecked(aNPABucket.isActive());

		if (aNPABucket.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(aNPABucket.getRecordType())) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(aNPABucket.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNPABucket
	 */
	public void doWriteComponentsToBean(NPABucket aNPABucket) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Bucket Code
		try {
			aNPABucket.setBucketCode(this.bucketCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bucket Description
		try {
			aNPABucket.setBucketDesc(this.bucketDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aNPABucket.setActive(this.active.isChecked());
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
	 * @param nPABucket The entity that need to be render.
	 */
	public void doShowDialog(NPABucket nPABucket) {
		logger.debug(Literal.LEAVING);

		if (nPABucket.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bucketCode.focus();
		} else {
			this.bucketCode.setReadonly(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(nPABucket.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.bucketDesc.focus();
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

		doWriteBeanToComponents(nPABucket);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.bucketCode.isReadonly()) {
			this.bucketCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_NPABucketDialog_BucketCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.bucketDesc.isReadonly()) {
			this.bucketDesc
					.setConstraint(new PTStringValidator(Labels.getLabel("label_NPABucketDialog_BucketDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.bucketCode.setConstraint("");
		this.bucketDesc.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// Bucket ID
		// Bucket Code
		// Bucket Description
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

		final NPABucket aNPABucket = new NPABucket();
		BeanUtils.copyProperties(this.nPABucket, aNPABucket);

		doDelete(Labels.getLabel("label_NPABucketDialog_BucketCode.value") + " : " + aNPABucket.getBucketCode(),
				aNPABucket);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.nPABucket.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.bucketCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.bucketCode);
		}
		readOnlyComponent(isReadOnly("NPABucketDialog_BucketDesc"), this.bucketDesc);
		readOnlyComponent(isReadOnly("NPABucketDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.nPABucket.isNewRecord()) {
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

		readOnlyComponent(true, this.bucketCode);
		readOnlyComponent(true, this.bucketDesc);
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
		this.bucketCode.setValue("");
		this.bucketDesc.setValue("");
		this.active.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final NPABucket aNPABucket = new NPABucket();
		BeanUtils.copyProperties(this.nPABucket, aNPABucket);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aNPABucket);

		isNew = aNPABucket.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aNPABucket.getRecordType())) {
				aNPABucket.setVersion(aNPABucket.getVersion() + 1);
				if (isNew) {
					aNPABucket.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aNPABucket.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aNPABucket.setNewRecord(true);
				}
			}
		} else {
			aNPABucket.setVersion(aNPABucket.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aNPABucket, tranType)) {
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
	protected boolean doProcess(NPABucket aNPABucket, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aNPABucket.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aNPABucket.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aNPABucket.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aNPABucket.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aNPABucket.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aNPABucket);
				}

				if (isNotesMandatory(taskId, aNPABucket)) {
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

			aNPABucket.setTaskId(taskId);
			aNPABucket.setNextTaskId(nextTaskId);
			aNPABucket.setRoleCode(getRole());
			aNPABucket.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aNPABucket, tranType);
			String operationRefs = getServiceOperations(taskId, aNPABucket);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aNPABucket, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aNPABucket, tranType);
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
		NPABucket aNPABucket = (NPABucket) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = nPABucketService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = nPABucketService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = nPABucketService.doApprove(auditHeader);

					if (aNPABucket.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = nPABucketService.doReject(auditHeader);
					if (aNPABucket.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_NPABucketDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_NPABucketDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.nPABucket), true);
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

	private AuditHeader getAuditHeader(NPABucket aNPABucket, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aNPABucket.getBefImage(), aNPABucket);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aNPABucket.getUserDetails(),
				getOverideMap());
	}

	public void setNPABucketService(NPABucketService nPABucketService) {
		this.nPABucketService = nPABucketService;
	}

}
