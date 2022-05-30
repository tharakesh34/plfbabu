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
package com.pennant.webui.applicationmaster.assetclassificationheader;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.AssetClassificationDetail;
import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.AssetClassificationHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/AssetClassificationHeader/assetClassificationHeaderDialog.zul file. <br>
 */
public class AssetClassificationHeaderDialogCtrl extends GFCBaseCtrl<AssetClassificationHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AssetClassificationHeaderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AssetClassificationHeaderDialog;
	protected Textbox code;
	protected Textbox description;
	protected Intbox stageOrder;
	protected Checkbox active;
	protected Button btnFinType;
	protected Textbox finType;

	private AssetClassificationHeader assetClssfcatnHeader;
	private transient AssetClassificationHeaderListCtrl listCtrl;
	private transient AssetClassificationHeaderService assetClassificationHeaderService;
	private List<AssetClassificationDetail> classificationDetailsList = new ArrayList<AssetClassificationDetail>();

	/**
	 * default constructor.<br>
	 */
	public AssetClassificationHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssetClassificationHeaderDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.assetClssfcatnHeader.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssetClassificationHeaderDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AssetClassificationHeaderDialog);

		try {
			// Get the required arguments.
			this.assetClssfcatnHeader = (AssetClassificationHeader) arguments.get("assetClassificationHeader");
			this.listCtrl = (AssetClassificationHeaderListCtrl) arguments.get("assetClassificationHeaderListCtrl");

			if (this.assetClssfcatnHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AssetClassificationHeader assetClassificationHeader = new AssetClassificationHeader();
			BeanUtils.copyProperties(this.assetClssfcatnHeader, assetClassificationHeader);
			this.assetClssfcatnHeader.setBefImage(assetClassificationHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.assetClssfcatnHeader.isWorkflow(), this.assetClssfcatnHeader.getWorkflowId(),
					this.assetClssfcatnHeader.getNextTaskId());

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
			doShowDialog(this.assetClssfcatnHeader);
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
		this.description.setMaxlength(100);
		this.stageOrder.setMaxlength(2);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssetClassificationHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssetClassificationHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssetClassificationHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssetClassificationHeaderDialog_btnSave"));
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
		doShowNotes(this.assetClssfcatnHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		listCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.assetClssfcatnHeader.getBefImage());
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
	public void doWriteBeanToComponents(AssetClassificationHeader header) {
		logger.debug(Literal.ENTERING);

		this.code.setValue(header.getCode());
		this.description.setValue(header.getDescription());
		this.stageOrder.setValue(header.getStageOrder());

		if (header.isNewRecord() || (StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, header.getRecordType()))) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		} else {
			this.active.setChecked(header.isActive());
		}

		doFillFinTypeList(header.getAssetClassificationDetailList());

		this.recordStatus.setValue(header.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param classificationHeader
	 */
	public void doWriteComponentsToBean(AssetClassificationHeader classificationHeader) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			classificationHeader.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			classificationHeader.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Stage Order
		try {
			classificationHeader.setStageOrder(this.stageOrder.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			classificationHeader.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// templateId
		try {
			if (StringUtils.trimToNull(this.finType.getValue()) == null) {
				throw new WrongValueException(this.finType,
						Labels.getLabel("label_AssetClassificationHeaderDialog_FinType.value"));
			}
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

	/**
	 * Displays the dialog page.
	 * 
	 * @param assetClassificationHeader The entity that need to be render.
	 */
	public void doShowDialog(AssetClassificationHeader assetClassificationHeader) {
		logger.debug(Literal.LEAVING);

		if (assetClassificationHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.code.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(assetClassificationHeader.getRecordType())) {
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

		doWriteBeanToComponents(assetClassificationHeader);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.code.isReadonly()) {
			this.code.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssetClassificationHeaderDialog_Code.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssetClassificationHeaderDialog_Description.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}
		if (!this.stageOrder.isReadonly()) {
			this.stageOrder.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_AssetClassificationHeaderDialog_StageOrder.value"), true, false, 99));
		}
		if (!this.btnFinType.isDisabled() && (StringUtils.trimToNull(this.finType.getValue()) == null)) {
			this.finType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssetClassificationHeaderDialog_FinType.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.code.setConstraint("");
		this.description.setConstraint("");
		this.stageOrder.setConstraint("");
		this.finType.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.finType.setErrorMessage("");
		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
		this.stageOrder.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a AssetClassificationHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AssetClassificationHeader aAssetClassificationHeader = new AssetClassificationHeader();
		BeanUtils.copyProperties(this.assetClssfcatnHeader, aAssetClassificationHeader);

		doDelete(aAssetClassificationHeader.getCode(), aAssetClassificationHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.assetClssfcatnHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.code);
			readOnlyComponent(false, this.stageOrder);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.code);
			readOnlyComponent(true, this.stageOrder);
		}

		readOnlyComponent(isReadOnly("AssetClassificationHeaderDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("AssetClassificationHeaderDialog_Active"), this.active);
		this.btnFinType.setDisabled(isReadOnly("button_AssetClassificationHeaderDialog_FinType"));
		this.finType.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.assetClssfcatnHeader.isNewRecord()) {
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
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.stageOrder);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.finType);

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
		logger.debug(Literal.ENTERING);

		this.code.setValue("");
		this.description.setValue("");
		this.stageOrder.setValue(0);
		;
		this.active.setChecked(false);
		this.finType.setValue("");
		this.finType.setTooltiptext("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final AssetClassificationHeader classificationHeader = new AssetClassificationHeader();
		BeanUtils.copyProperties(this.assetClssfcatnHeader, classificationHeader);
		boolean isNew = false;

		boolean validateFields = true;
		if (this.userAction.getSelectedItem() != null) {
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")) {
				validateFields = false;
			}
		}

		if (isWorkFlowEnabled()) {
			classificationHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), classificationHeader.getNextTaskId(),
					classificationHeader);
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equals(classificationHeader.getRecordType()) && validateFields) {
			doClearMessage();
			doSetValidation();
			// fill the Promotion object with the components data
			doWriteComponentsToBean(classificationHeader);

		}

		// FintypeDetails
		classificationHeader.setAssetClassificationDetailList(fetchFinTypeDetails());

		isNew = classificationHeader.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(classificationHeader.getRecordType()).equals("")) {
				classificationHeader.setVersion(classificationHeader.getVersion() + 1);
				if (isNew) {
					classificationHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					classificationHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					classificationHeader.setNewRecord(true);
				}
			}
		} else {
			classificationHeader.setVersion(classificationHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(classificationHeader, tranType)) {
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
	protected boolean doProcess(AssetClassificationHeader header, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		header.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			header.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(header.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, header);
				}

				if (isNotesMandatory(taskId, header)) {
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

			header.setTaskId(taskId);
			header.setNextTaskId(nextTaskId);
			header.setRoleCode(getRole());
			header.setNextRoleCode(nextRoleCode);

			// AssetClassificationDetail
			List<AssetClassificationDetail> detailsList = header.getAssetClassificationDetailList();

			if (CollectionUtils.isNotEmpty(detailsList)) {
				for (AssetClassificationDetail details : detailsList) {
					if (StringUtils.isNotBlank(details.getRecordType())) {
						details.setHeaderId(header.getId());
						details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
						details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						details.setRecordStatus(header.getRecordStatus());
						details.setWorkflowId(header.getWorkflowId());
						details.setTaskId(taskId);
						details.setNextTaskId(nextTaskId);
						details.setRoleCode(getRole());
						details.setNextRoleCode(nextRoleCode);
						if (PennantConstants.RECORD_TYPE_DEL.equals(header.getRecordType())) {
							if (StringUtils.trimToNull(details.getRecordType()) == null) {
								details.setRecordType(header.getRecordType());
								details.setNewRecord(true);
							}
						}
					}
				}
			}

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(header, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				auditHeader = getAuditHeader(header, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(header, tranType), null);
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
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AssetClassificationHeader aAssetClassificationHeader = (AssetClassificationHeader) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = this.assetClassificationHeaderService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = this.assetClassificationHeaderService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = this.assetClassificationHeaderService.doApprove(auditHeader);

					if (aAssetClassificationHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = this.assetClassificationHeaderService.doReject(auditHeader);
					if (aAssetClassificationHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AssetClassificationHeaderDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AssetClassificationHeaderDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.assetClssfcatnHeader), true);
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

	public void onClick$btnFinType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		this.finType.setConstraint("");
		this.finType.setErrorMessage("");

		Map<String, Object> finTypeDataMap = new HashMap<String, Object>();
		String[] finTypes = this.finType.getValue().split(",");
		for (int i = 0; i < finTypes.length; i++) {
			finTypeDataMap.put(finTypes[i], null);
		}
		Object dataObject = ExtendedMultipleSearchListBox.show(this.window_AssetClassificationHeaderDialog,
				"FinanceType", finTypeDataMap);
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.finType.setTooltiptext("");
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> details = (Map<String, Object>) dataObject;
			if (details != null) {
				String tempCode = details.keySet().toString();
				tempCode = tempCode.replace("[", " ").replace("]", "").replace(" ", "");
				if (tempCode.startsWith(",")) {
					tempCode = tempCode.substring(1);
				}
				if (tempCode.endsWith(",")) {
					tempCode = tempCode.substring(0, tempCode.length() - 1);
				}
				this.finType.setValue(tempCode);
			}

			// Setting tooltip with Descriptions
			String toolTipDesc = "";
			for (String key : details.keySet()) {
				Object obj = (Object) details.get(key);
				if (!(obj instanceof String)) {
					FinanceType financeType = (FinanceType) obj;
					if (financeType != null) {
						toolTipDesc = toolTipDesc.concat(financeType.getFinTypeDesc() + " , ");
					}
				}
			}

			if (StringUtils.isNotBlank(toolTipDesc) && toolTipDesc.endsWith(", ")) {
				toolTipDesc = toolTipDesc.substring(0, toolTipDesc.length() - 2);
			}
			this.finType.setTooltiptext(toolTipDesc);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void setFinTypeDescription(List<AssetClassificationDetail> assetClassificationDetailList) {
		logger.debug(Literal.ENTERING);

		String finType = "";

		for (AssetClassificationDetail detail : assetClassificationDetailList) {
			if (detail.getFinType() != null
					&& !StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				finType = finType + detail.getFinType().concat(",");
			}
		}

		this.finType.setTooltip(getFormattedFinType(finType));

		logger.debug(Literal.LEAVING);

	}

	public String getFormattedFinType(String finType) {
		if (finType != null && finType.length() > 0 && finType.charAt(finType.length() - 1) == ',') {
			finType = finType.substring(0, finType.length() - 1);
		}
		return finType;
	}

	private void doFillFinTypeList(List<AssetClassificationDetail> detailList) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isEmpty(detailList)) {
			return;
		}

		setClassificationDetailsList(detailList);

		String tempFinType = "";
		for (AssetClassificationDetail detail : detailList) {
			if (!StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempFinType)) {
					tempFinType = detail.getFinType();
				} else {
					tempFinType = tempFinType.concat(",").concat(detail.getFinType());
				}
			}
		}
		this.finType.setValue(tempFinType);
		setFinTypeDescription(detailList);

		logger.debug(Literal.ENTERING);
	}

	private List<AssetClassificationDetail> fetchFinTypeDetails() {
		logger.debug(Literal.ENTERING);

		List<String> finTypesList = Arrays.asList(this.finType.getValue().split(","));
		List<AssetClassificationDetail> resultList = new ArrayList<AssetClassificationDetail>();
		List<AssetClassificationDetail> oldList = getClassificationDetailsList();
		Map<String, AssetClassificationDetail> valueMap = new HashMap<>();

		for (AssetClassificationDetail detail : oldList) {
			valueMap.put(detail.getFinType(), detail);
		}

		for (String finTypeVal : finTypesList) {
			if (StringUtils.isEmpty(finTypeVal)) {
				continue;
			}
			// Check object is already exists in saved list or not
			if (valueMap.containsKey(finTypeVal)) {
				// Removing from map to identify existing modifications
				boolean isDelete = false;
				if (this.userAction.getSelectedItem() != null) {
					if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| this.userAction.getSelectedItem().getLabel().contains("Reject")
							|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
						isDelete = true;
					}
				}
				if (!isDelete) {
					AssetClassificationDetail oldRcd = valueMap.get(finTypeVal);
					oldRcd.setNewRecord(false);
					oldRcd.setRecordType(oldRcd.getRecordType());
					oldRcd.setId(oldRcd.getId());
					valueMap.remove(finTypeVal);
					resultList.add(oldRcd);
					valueMap.remove(finTypeVal);
				}
			} else {
				AssetClassificationDetail detail = new AssetClassificationDetail();
				detail.setFinType(finTypeVal);
				detail.setNewRecord(true);
				detail.setVersion(1);
				detail.setRecordType(PennantConstants.RCD_ADD);
				resultList.add(detail);
			}
		}

		resultList.addAll(valueMap.values());

		// Removing unavailable records from DB by using Workflow details
		for (AssetClassificationDetail detail : oldList) {
			if (valueMap.containsKey(detail.getFinType())) {
				if (StringUtils.isBlank(detail.getRecordType())) {
					detail.setNewRecord(true);
					detail.setVersion(detail.getVersion() + 1);
					detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else {
					if (!StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				}
			} else {
				if (StringUtils.isEmpty(detail.getRecordType())
						&& !StringUtils.equals(detail.getRecordType(), PennantConstants.RCD_ADD)) {
					detail.setNewRecord(true);
					detail.setVersion(detail.getVersion() + 1);
					detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
		}
		logger.debug(Literal.LEAVING);

		return resultList;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(AssetClassificationHeader aAssetClassificationHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAssetClassificationHeader.getBefImage(),
				aAssetClassificationHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail,
				aAssetClassificationHeader.getUserDetails(), getOverideMap());
	}

	public List<AssetClassificationDetail> getClassificationDetailsList() {
		return classificationDetailsList;
	}

	public void setClassificationDetailsList(List<AssetClassificationDetail> classificationDetailsList) {
		this.classificationDetailsList = classificationDetailsList;
	}

	public void setAssetClassificationHeaderService(AssetClassificationHeaderService assetClassificationHeaderService) {
		this.assetClassificationHeaderService = assetClassificationHeaderService;
	}

}
