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
 * * FileName : AssetClassificationHeaderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 04-05-2020 * * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.pff.npa.web;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.npa.model.AssetClassCode;
import com.pennanttech.pff.npa.model.AssetSubClassCode;
import com.pennanttech.pff.npa.service.AssetSubClassCodeService;

public class AssetSubClassCodeDialogCtrl extends GFCBaseCtrl<AssetSubClassCode> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AssetSubClassCodeDialogCtrl.class);

	protected Window window_AssetSubClassCodeDialog;
	protected ExtendedCombobox classCode;
	protected Textbox code;
	protected Textbox description;
	protected Checkbox active;

	private AssetSubClassCode assetSubClassCode;

	private transient AssetSubClassCodeListCtrl assetSubClassCodeListCtrl;
	private transient AssetSubClassCodeService assetSubClassCodeService;

	/**
	 * default constructor.<br>
	 */
	public AssetSubClassCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssetSubClassCodeDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.assetSubClassCode.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_AssetSubClassCodeDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AssetSubClassCodeDialog);

		try {
			// Get the required arguments.
			this.assetSubClassCode = (AssetSubClassCode) arguments.get("assetSubClassCode");
			this.assetSubClassCodeListCtrl = (AssetSubClassCodeListCtrl) arguments.get("assetSubClassCodeListCtrl");

			if (this.assetSubClassCode == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AssetSubClassCode assetSubClassCode = this.assetSubClassCode.copyEntity();
			this.assetSubClassCode.setBefImage(assetSubClassCode);

			// Render the page and display the data.
			doLoadWorkFlow(this.assetSubClassCode.isWorkflow(), this.assetSubClassCode.getWorkflowId(),
					this.assetSubClassCode.getNextTaskId());

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
			doShowDialog(this.assetSubClassCode);
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

		this.classCode.setMandatoryStyle(true);
		this.classCode.setModuleName("AssetClassCode");
		this.classCode.setValueColumn("Code");
		this.classCode.setDescColumn("Description");
		this.classCode.setValidateColumns(new String[] { "Code" });

		this.description.setMaxlength(100);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssetSubClassCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssetSubClassCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssetSubClassCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssetSubClassCodeDialog_btnSave"));
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
		doShowNotes(this.assetSubClassCode);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		assetSubClassCodeListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.assetSubClassCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param assetClassificationHeader
	 * 
	 */
	public void doWriteBeanToComponents(AssetSubClassCode subClassCode) {
		logger.debug(Literal.ENTERING);

		if (subClassCode.isNewRecord()) {
			this.classCode.setDescription("");
		} else {
			AssetClassCode classCode = new AssetClassCode();
			classCode.setId(subClassCode.getAssetClassId());
			classCode.setCode(subClassCode.getClassCode());
			classCode.setDescription(subClassCode.getClassDescription());

			this.classCode.setValue(subClassCode.getClassCode());
			this.classCode.setDescription(subClassCode.getClassDescription());
			this.classCode.setObject(subClassCode);
		}

		this.code.setValue(subClassCode.getCode());
		this.description.setValue(subClassCode.getDescription());

		if (subClassCode.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(subClassCode.getRecordType())) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		} else {
			this.active.setChecked(subClassCode.isActive());
		}

		this.recordStatus.setValue(subClassCode.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param subClassCode
	 */
	public void doWriteComponentsToBean(AssetSubClassCode subClassCode) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			subClassCode.setClassCode(StringUtils.trimToNull(this.classCode.getValidatedValue()));
			Object obj = this.classCode.getObject();
			if (obj != null) {
				subClassCode.setAssetClassId(((AssetClassCode) obj).getId());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Code
		try {
			subClassCode.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			subClassCode.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			subClassCode.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(AssetSubClassCode AssetSubClassCode) {
		logger.debug(Literal.ENTERING);

		if (AssetSubClassCode.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.code.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(AssetSubClassCode.getRecordType())) {
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

		doWriteBeanToComponents(AssetSubClassCode);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.classCode.isReadonly()) {
			this.classCode.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetClassCodes_Code"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetSubClassCodes_Code"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetSubClassCodes_Description"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		this.classCode.setConstraint("");
		this.code.setConstraint("");
		this.description.setConstraint("");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		this.classCode.setErrorMessage("");
		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
	}

	/**
	 * Deletes a AssetClassificationHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AssetSubClassCode subClassCode = this.assetSubClassCode.copyEntity();

		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ subClassCode.getCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(subClassCode.getRecordType()).equals("")) {
				subClassCode.setVersion(subClassCode.getVersion() + 1);
				subClassCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					subClassCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					subClassCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), subClassCode.getNextTaskId(),
							subClassCode);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(subClassCode, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.assetSubClassCode.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.code);
			readOnlyComponent(false, this.classCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.code);
			readOnlyComponent(true, this.classCode);

		}

		readOnlyComponent(isReadOnly("AssetSubClassCodeDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("AssetSubClassCodeDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.assetSubClassCode.isNewRecord()) {
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
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.classCode);
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
		this.code.setValue("");
		this.description.setValue("");
		this.active.setChecked(false);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final AssetSubClassCode assetSubClassCode = this.assetSubClassCode.copyEntity();
		boolean isNew = false;

		boolean validateFields = true;
		if (this.userAction.getSelectedItem() != null) {
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")) {
				validateFields = false;
			}
		}

		if (isWorkFlowEnabled()) {
			assetSubClassCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), assetSubClassCode.getNextTaskId(),
					assetSubClassCode);
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equals(assetSubClassCode.getRecordType()) && validateFields) {
			doClearMessage();
			doSetValidation();
			// fill the Promotion object with the components data
			doWriteComponentsToBean(assetSubClassCode);

		}

		isNew = assetSubClassCode.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(assetSubClassCode.getRecordType()).equals("")) {
				assetSubClassCode.setVersion(assetSubClassCode.getVersion() + 1);
				if (isNew) {
					assetSubClassCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					assetSubClassCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					assetSubClassCode.setNewRecord(true);
				}
			}
		} else {
			assetSubClassCode.setVersion(assetSubClassCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(assetSubClassCode, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(AssetSubClassCode assetSubClassCode, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";
		assetSubClassCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		assetSubClassCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		assetSubClassCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			assetSubClassCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(assetSubClassCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, assetSubClassCode);
				}

				if (isNotesMandatory(taskId, assetSubClassCode)) {
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

			assetSubClassCode.setTaskId(taskId);
			assetSubClassCode.setNextTaskId(nextTaskId);
			assetSubClassCode.setRoleCode(getRole());
			assetSubClassCode.setNextRoleCode(nextRoleCode);

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(assetSubClassCode, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				auditHeader = getAuditHeader(assetSubClassCode, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(assetSubClassCode, tranType), null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AssetSubClassCode AssetSubClassCode = (AssetSubClassCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = this.assetSubClassCodeService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = this.assetSubClassCodeService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = this.assetSubClassCodeService.doApprove(auditHeader);

					if (AssetSubClassCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = this.assetSubClassCodeService.doReject(auditHeader);
					if (AssetSubClassCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AssetSubClassCodeDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AssetSubClassCodeDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.assetSubClassCode), true);
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

	private AuditHeader getAuditHeader(AssetSubClassCode AssetSubClassCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, AssetSubClassCode.getBefImage(), AssetSubClassCode);
		return new AuditHeader(getReference(), null, null, null, auditDetail, AssetSubClassCode.getUserDetails(),
				getOverideMap());
	}

	@Autowired
	public void setAssetSubClassCodeService(AssetSubClassCodeService assetSubClassCodeService) {
		this.assetSubClassCodeService = assetSubClassCodeService;
	}

}
