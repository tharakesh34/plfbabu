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
 * * FileName : NPAProvisionHeaderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020 * *
 * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.npaprovisionheader;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ReportsUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.NPAProvisionHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/NPAProvisionHeader/nPAProvisionHeaderDialog.zul
 * file. <br>
 */
public class NPAProvisionHeaderDialogCtrl extends GFCBaseCtrl<NPAProvisionHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(NPAProvisionHeaderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_NPAProvisionHeaderDialog;
	protected ExtendedCombobox entity;
	protected ExtendedCombobox finType;
	private NPAProvisionHeader nPAProvisionHeader;
	protected Listbox listBoxProvisionDeatils;
	protected Button btnExtract;
	protected Button btnCopyTo;
	private transient NPAProvisionHeaderListCtrl nPAProvisionHeaderListCtrl;
	private transient NPAProvisionHeaderService nPAProvisionHeaderService;
	protected ExtendedCombobox npaTemplateType;
	private List<ValueLabel> npaRulesList = new ArrayList<>();

	/**
	 * default constructor.<br>
	 */
	public NPAProvisionHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "NPAProvisionHeaderDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.nPAProvisionHeader.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_NPAProvisionHeaderDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_NPAProvisionHeaderDialog);

		try {
			// Get the required arguments.
			this.nPAProvisionHeader = (NPAProvisionHeader) arguments.get("nPAProvisionHeader");
			this.nPAProvisionHeaderListCtrl = (NPAProvisionHeaderListCtrl) arguments.get("nPAProvisionHeaderListCtrl");

			if (this.nPAProvisionHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			NPAProvisionHeader nPAProvisionHeader = new NPAProvisionHeader();
			BeanUtils.copyProperties(this.nPAProvisionHeader, nPAProvisionHeader);
			this.nPAProvisionHeader.setBefImage(nPAProvisionHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.nPAProvisionHeader.isWorkflow(), this.nPAProvisionHeader.getWorkflowId(),
					this.nPAProvisionHeader.getNextTaskId());

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
			doShowDialog(this.nPAProvisionHeader);
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

		this.entity.setMandatoryStyle(true);
		this.entity.setModuleName("Entity");
		this.entity.setValueColumn("entity");
		this.entity.setDescColumn("entityName");
		this.entity.setValidateColumns(new String[] { "entityCode" });
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("finType");
		this.finType.setDescColumn("finTypeName");
		this.finType.setValidateColumns(new String[] { "finType" });
		this.npaTemplateType.setMandatoryStyle(true);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_NPAProvisionHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_NPAProvisionHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_NPAProvisionHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_NPAProvisionHeaderDialog_btnSave"));
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
		doShowNotes(this.nPAProvisionHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		nPAProvisionHeaderListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.nPAProvisionHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param nPAProvisionHeader
	 * 
	 */
	public void doWriteBeanToComponents(NPAProvisionHeader provisionHeader) {
		logger.debug(Literal.ENTERING);

		this.entity.setValue(provisionHeader.getEntity());
		this.finType.setValue(provisionHeader.getFinType());
		this.entity.setDescription(provisionHeader.getEntityName());
		this.finType.setDescription(provisionHeader.getFinTypeName());
		this.recordStatus.setValue(provisionHeader.getRecordStatus());
		this.npaTemplateType.setValue(provisionHeader.getNpaTemplateCode());
		this.npaTemplateType.setDescription(provisionHeader.getNpaTemplateDesc());

		doFillProvisionDetails(provisionHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNPAProvisionHeader
	 */
	public void doWriteComponentsToBean(NPAProvisionHeader aNPAProvisionHeader) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param nPAProvisionHeader The entity that need to be render.
	 */
	public void doShowDialog(NPAProvisionHeader nPAProvisionHeader) {
		logger.debug(Literal.ENTERING);

		if (nPAProvisionHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.entity.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(nPAProvisionHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.finType.focus();
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
			this.btnExtract.setVisible(false);
		}

		doWriteBeanToComponents(nPAProvisionHeader);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a NPAProvisionHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final NPAProvisionHeader aNPAProvisionHeader = new NPAProvisionHeader();
		BeanUtils.copyProperties(this.nPAProvisionHeader, aNPAProvisionHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aNPAProvisionHeader.getFinType() + " - " + aNPAProvisionHeader.getFinTypeName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			doClearProvisionDeatils(aNPAProvisionHeader);
			if (StringUtils.trimToEmpty(aNPAProvisionHeader.getRecordType()).equals("")) {
				aNPAProvisionHeader.setVersion(aNPAProvisionHeader.getVersion() + 1);
				aNPAProvisionHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aNPAProvisionHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aNPAProvisionHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aNPAProvisionHeader.getNextTaskId(),
							aNPAProvisionHeader);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aNPAProvisionHeader, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doClearProvisionDeatils(NPAProvisionHeader aNPAProvisionHeader) {
		for (int i = 0; i < aNPAProvisionHeader.getProvisionDetailsList().size(); i++) {
			if (aNPAProvisionHeader.getProvisionDetailsList().get(i).isNewPrvDetail()) {
				aNPAProvisionHeader.getProvisionDetailsList().remove(i);
			}
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.nPAProvisionHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.btnCopyTo.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.btnCopyTo.setVisible(false);
		}

		if (StringUtils.equals(PennantConstants.RCD_STATUS_APPROVED, nPAProvisionHeader.getRecordStatus())) {
			this.btnCopyTo.setVisible(!isReadOnly("NPAProvisionHeaderDialog_DetailsList"));
		}

		readOnlyComponent(true, this.entity);
		readOnlyComponent(true, this.finType);
		readOnlyComponent(true, this.npaTemplateType);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.nPAProvisionHeader.isNewRecord()) {
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

		readOnlyComponent(true, this.entity);
		readOnlyComponent(true, this.finType);
		readOnlyComponent(true, this.npaTemplateType);

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
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final NPAProvisionHeader aNPAProvisionHeader = new NPAProvisionHeader();
		BeanUtils.copyProperties(this.nPAProvisionHeader, aNPAProvisionHeader);
		boolean isNew = false;

		if (!isNPAActive()) {
			MessageUtil.showError("Please select atleast one NPA checkbox.");
			return;
		}

		doWriteComponentsToBean(aNPAProvisionHeader);
		List<NPAProvisionDetail> listNPAProvisionDetail = getProvisionDetails();

		if (CollectionUtils.isEmpty(listNPAProvisionDetail)) {
			Clients.showNotification(Labels.getLabel("listbox_ProvPercDialog_Empty"), "info", null, null, -1);
			return;
		}
		aNPAProvisionHeader.setProvisionDetailsList(listNPAProvisionDetail);

		isNew = aNPAProvisionHeader.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aNPAProvisionHeader.getRecordType())) {
				aNPAProvisionHeader.setVersion(aNPAProvisionHeader.getVersion() + 1);
				if (isNew) {
					aNPAProvisionHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aNPAProvisionHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aNPAProvisionHeader.setNewRecord(true);
				}
			}
		} else {
			aNPAProvisionHeader.setVersion(aNPAProvisionHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aNPAProvisionHeader, tranType)) {
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
	 * NPA Checking
	 * 
	 * @return
	 */
	private boolean isNPAActive() {
		logger.debug(Literal.ENTERING);

		if (this.listBoxProvisionDeatils != null && this.listBoxProvisionDeatils.getItems().size() > 0) {
			for (int i = 0; i <= listBoxProvisionDeatils.getItems().size() - 1; i++) {
				Listitem item = listBoxProvisionDeatils.getItems().get(i);
				int seqNo = Integer.parseInt(item.getId().replaceAll("listitem_", ""));
				Checkbox nPAActive = (Checkbox) listBoxProvisionDeatils.getFellowIfAny("NPACheckBox_" + seqNo);
				if (nPAActive.isChecked()) {
					return true;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
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
	protected boolean doProcess(NPAProvisionHeader aNPAProvisionHeader, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aNPAProvisionHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aNPAProvisionHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aNPAProvisionHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aNPAProvisionHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aNPAProvisionHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aNPAProvisionHeader);
				}

				if (isNotesMandatory(taskId, aNPAProvisionHeader)) {
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

			aNPAProvisionHeader.setTaskId(taskId);
			aNPAProvisionHeader.setNextTaskId(nextTaskId);
			aNPAProvisionHeader.setRoleCode(getRole());
			aNPAProvisionHeader.setNextRoleCode(nextRoleCode);

			List<NPAProvisionDetail> npaProvisionDetailsList = aNPAProvisionHeader.getProvisionDetailsList();

			if (npaProvisionDetailsList != null && !npaProvisionDetailsList.isEmpty()) {
				for (NPAProvisionDetail details : npaProvisionDetailsList) {
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordType(aNPAProvisionHeader.getRecordType());
					details.setRecordStatus(aNPAProvisionHeader.getRecordStatus());
					details.setWorkflowId(aNPAProvisionHeader.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					details.setNewRecord(aNPAProvisionHeader.isNewRecord());
					if (PennantConstants.RECORD_TYPE_DEL.equals(aNPAProvisionHeader.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aNPAProvisionHeader.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(aNPAProvisionHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aNPAProvisionHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aNPAProvisionHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aNPAProvisionHeader, tranType);
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
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		NPAProvisionHeader aNPAProvisionHeader = (NPAProvisionHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = nPAProvisionHeaderService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = nPAProvisionHeaderService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = nPAProvisionHeaderService.doApprove(auditHeader);

					if (aNPAProvisionHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = nPAProvisionHeaderService.doReject(auditHeader);
					if (aNPAProvisionHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_NPAProvisionHeaderDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_NPAProvisionHeaderDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.nPAProvisionHeader), true);
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

	// ####### Provision details processing start #####////

	private void doFillProvisionDetails(NPAProvisionHeader provisionHeader) {
		logger.debug(Literal.ENTERING);

		this.listBoxProvisionDeatils.getItems().clear();
		List<NPAProvisionDetail> provisionDetailsList = provisionHeader.getProvisionDetailsList();
		if (CollectionUtils.isEmpty(provisionDetailsList)) {
			return;
		}
		// Sorting provision details based on Stage order
		sortProvisionDetails(provisionDetailsList);

		boolean isReadOnly = isReadOnly("NPAProvisionHeaderDialog_DetailsList");
		for (int i = 0; i < provisionDetailsList.size(); i++) {
			createProvisionDetailItem(isReadOnly, provisionDetailsList.get(i), i);

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creating Provision details row
	 * 
	 * @param isReadOnly
	 * @param detail
	 * @param
	 */
	private void createProvisionDetailItem(boolean isReadOnly, NPAProvisionDetail detail, int seqNum) {
		logger.debug(Literal.ENTERING);

		Listitem item = new Listitem();
		boolean mandatory = true;
		int stageOrder = detail.getAssetStageOrder();

		// Asset Code
		Listcell lc_AssetCode = new Listcell(detail.getAssetCode());
		item.appendChild(lc_AssetCode);
		lc_AssetCode.setParent(item);

		// NPA check box
		Listcell lc_NPACheckBox = new Listcell();
		Checkbox npa_CheckBox = new Checkbox();
		npa_CheckBox.setId("NPACheckBox_" + stageOrder);
		npa_CheckBox.setAttribute("Sequence", seqNum + 1);
		readOnlyComponent(isReadOnly, npa_CheckBox);
		npa_CheckBox.setChecked(detail.isNPAActive());
		npa_CheckBox.setWidth("50px");
		npa_CheckBox.addForward(Events.ON_CLICK, self, "onClick_NPA_CheckBox");
		lc_NPACheckBox.appendChild(npa_CheckBox);
		item.appendChild(lc_NPACheckBox);

		// DPD-Days
		Listcell lc_DPDDays = new Listcell();
		Intbox dpdDays = new Intbox();
		dpdDays.setId("DPDDays_" + stageOrder);
		readOnlyComponent(isReadOnly, dpdDays);
		dpdDays.setValue(detail.getDPDdays());
		dpdDays.setWidth("50px");
		dpdDays.setMaxlength(3);

		getSpacing(lc_DPDDays, dpdDays, mandatory, stageOrder, "DPDDays_");
		item.appendChild(lc_DPDDays);

		// NPA Payment Apportionment
		Listcell lc_NPAPaymt = new Listcell();
		Combobox paymnt_Combobox = new Combobox();
		paymnt_Combobox.setId("NPAPymntApprtn_" + stageOrder);
		readOnlyComponent(isReadOnly, paymnt_Combobox);
		fillComboBox(paymnt_Combobox, detail.getNPARepayApprtnmnt(), PennantStaticListUtil.getNPAPaymentTypes(), "");
		paymnt_Combobox.setWidth("100px");

		getSpacing(lc_NPAPaymt, paymnt_Combobox, mandatory, stageOrder, "NPAPymntApprtn_");
		item.appendChild(lc_NPAPaymt);

		// Active
		Listcell lc_ActiveCheckBox = new Listcell();
		Checkbox active_CheckBox = new Checkbox();
		active_CheckBox.setId("ActiveCheckBox_" + stageOrder);
		readOnlyComponent(isReadOnly, active_CheckBox);
		active_CheckBox.setChecked(detail.isActive());
		active_CheckBox.setWidth("50px");
		lc_ActiveCheckBox.appendChild(active_CheckBox);
		item.appendChild(lc_ActiveCheckBox);

		// Provision Rule
		Listcell lc_RuleComboBox = new Listcell();
		Combobox rule_Combobox = new Combobox();
		rule_Combobox.setId("NPARule_" + stageOrder);
		readOnlyComponent(isReadOnly, rule_Combobox);
		fillComboBox(rule_Combobox, String.valueOf(detail.getRuleId()), getNpaRulesList(), "");
		rule_Combobox.setWidth("200px");
		lc_RuleComboBox.appendChild(rule_Combobox);
		getSpacing(lc_RuleComboBox, rule_Combobox, mandatory, stageOrder, "NPARule_");
		item.appendChild(lc_RuleComboBox);

		item.setId("listitem_" + stageOrder);
		item.setAttribute("Object", detail);

		this.listBoxProvisionDeatils.appendChild(item);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Validate Provision details list
	 * 
	 * @return
	 */
	private List<NPAProvisionDetail> getProvisionDetails() {
		logger.debug(Literal.ENTERING);

		List<NPAProvisionDetail> provisionDetailsList = new LinkedList<>();

		if (this.listBoxProvisionDeatils.getItems().size() <= 0) {
			return provisionDetailsList;
		}

		int dpdDaysVal = 0;

		for (int i = 0; i < listBoxProvisionDeatils.getItems().size(); i++) {

			Listitem item = listBoxProvisionDeatils.getItems().get(i);
			int stageOrdrId = Integer.parseInt(item.getId().replaceAll("listitem_", ""));
			NPAProvisionDetail detail = (NPAProvisionDetail) item.getAttribute("Object");

			Checkbox npaCheckBox = (Checkbox) listBoxProvisionDeatils.getFellowIfAny("NPACheckBox_" + stageOrdrId);
			Intbox dpdDays = (Intbox) listBoxProvisionDeatils.getFellowIfAny("DPDDays_" + stageOrdrId);
			Combobox npaPymntApprtn = (Combobox) listBoxProvisionDeatils
					.getFellowIfAny("NPAPymntApprtn_" + stageOrdrId);
			Decimalbox intSecPerc = (Decimalbox) listBoxProvisionDeatils.getFellowIfAny("IntSecPercDec_" + stageOrdrId);
			Decimalbox intUnSecPerc = (Decimalbox) listBoxProvisionDeatils
					.getFellowIfAny("IntUnSecPercDec_" + stageOrdrId);
			Decimalbox regSecPerc = (Decimalbox) listBoxProvisionDeatils.getFellowIfAny("RegSecPercDec_" + stageOrdrId);
			Decimalbox regUnSecPerc = (Decimalbox) listBoxProvisionDeatils
					.getFellowIfAny("RegUnSecPercDec_" + stageOrdrId);

			Checkbox activeBox = (Checkbox) listBoxProvisionDeatils.getFellowIfAny("ActiveCheckBox_" + stageOrdrId);
			Combobox rulebox = (Combobox) listBoxProvisionDeatils.getFellowIfAny("NPARule_" + stageOrdrId);

			Clients.clearWrongValue(npaCheckBox);
			Clients.clearWrongValue(dpdDays);
			dpdDays.setErrorMessage("");
			dpdDays.setConstraint("");
			Clients.clearWrongValue(npaPymntApprtn);
			npaPymntApprtn.setErrorMessage("");
			npaPymntApprtn.setConstraint("");
			Clients.clearWrongValue(activeBox);
			Clients.clearWrongValue(rulebox);
			rulebox.setErrorMessage("");
			rulebox.setConstraint("");

			detail.setNPAActive(npaCheckBox.isChecked());
			detail.setDPDdays(dpdDays.intValue());
			detail.setNPARepayApprtnmnt(getComboboxValue(npaPymntApprtn));
			detail.setActive(activeBox.isChecked());
			detail.setRuleId(Long.valueOf(rulebox.getSelectedItem().getValue()));

			provisionDetailsList.add(detail);

			if (!npaCheckBox.isChecked() && detail.getDPDdays() == 0) {

				// Internal Secured Percentage
				if (detail.getIntSecPerc().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(intSecPerc, Labels.getLabel("INTERN_PROV_ZERO_PERCENT"));
				} else if (detail.getIntSecPerc().compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(intSecPerc, Labels.getLabel("NUMBER_MAXVALUE_PROV_EQ", new String[] {
							Labels.getLabel("label_Int_Secure_Perc"), detail.getIntSecPerc().toString() }));
				}

				// Internal UnSecured Percentage
				if (detail.getIntUnSecPerc().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(intUnSecPerc, Labels.getLabel("INTERN_PROV_ZERO_PERCENT"));
				} else if (detail.getIntUnSecPerc().compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(intUnSecPerc,
							Labels.getLabel("NUMBER_MAXVALUE_PROV_EQ", new String[] {
									Labels.getLabel("label_Int_UnSecure_Perc"), detail.getIntUnSecPerc().toString() }));
				}

				// Regularity Secured Percentage
				if (detail.getRegSecPerc().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(regSecPerc, Labels.getLabel("REG_PROV_ZERO_PERCENT"));
				} else if (detail.getRegSecPerc().compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(regSecPerc, Labels.getLabel("NUMBER_MAXVALUE_PROV_EQ", new String[] {
							Labels.getLabel("label_Reg_Secure_Perc"), detail.getRegSecPerc().toString() }));
				}

				// Regularity UnSecured Percentage
				if (detail.getRegUnSecPerc().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(regUnSecPerc, Labels.getLabel("REG_PROV_ZERO_PERCENT"));
				} else if (detail.getRegUnSecPerc().compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(regUnSecPerc,
							Labels.getLabel("NUMBER_MAXVALUE_PROV_EQ", new String[] {
									Labels.getLabel("label_Reg_UnSecure_Perc"), detail.getRegUnSecPerc().toString() }));
				}
				continue;
			}

			// DPD Days
			if (detail.getDPDdays() <= 0) {
				throw new WrongValueException(dpdDays, Labels.getLabel("DPDDAYS_SHOULD_NOT_ZERO_LABEL"));
			} else if (detail.getDPDdays() > 999) {
				throw new WrongValueException(dpdDays, Labels.getLabel("DPDDAYS_NUMBER_MAX_VALUE",
						new String[] { Labels.getLabel("label_DPDDays_Value"), String.valueOf(detail.getDPDdays()) }));
			}

			if (dpdDays.intValue() <= dpdDaysVal) {
				throw new WrongValueException(dpdDays, Labels.getLabel("label_DPDDays_Compare_Value") + dpdDaysVal);
			}

			dpdDaysVal = dpdDays.intValue();

			// NPA Payment Type
			if (StringUtils.equals(PennantConstants.List_Select, detail.getNPARepayApprtnmnt())) {
				throw new WrongValueException(npaPymntApprtn, Labels.getLabel("label_NPAPayemntType_Value"));
			}

			// NPA Rule
			if (StringUtils.equals(PennantConstants.List_Select, rulebox.getSelectedItem().getValue().toString())) {
				throw new WrongValueException(rulebox, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectNPAProvisionHeaderDialog_NPAType.value") }));
			}
		}
		logger.debug(Literal.LEAVING);

		return provisionDetailsList;
	}

	// NPA Check option
	public void onClick_NPA_CheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Checkbox checkbox = (Checkbox) event.getOrigin().getTarget();
		int sequence = (int) checkbox.getAttribute("Sequence");

		if (!checkbox.isChecked()) {

			// ALL Asset Codes are Set to Default values if user uncheck one NPA check box also.
			for (int i = 0; i < listBoxProvisionDeatils.getItems().size(); i++) {

				Listitem item = listBoxProvisionDeatils.getItems().get(i);
				// To UnCheck Particular Asset Code it will set to default values.
				// Listitem item = listBoxProvisionDeatils.getItems().get(sequence-1);
				int stageOrder = Integer.parseInt(item.getId().replaceAll("listitem_", ""));

				Checkbox unCheckNpaActive = (Checkbox) listBoxProvisionDeatils
						.getFellowIfAny("NPACheckBox_" + stageOrder);
				Combobox npaPymn = (Combobox) listBoxProvisionDeatils.getFellowIfAny("NPAPymntApprtn_" + stageOrder);
				Intbox dpdDays = (Intbox) listBoxProvisionDeatils.getFellowIfAny("DPDDays_" + stageOrder);
				Checkbox activeBox = (Checkbox) listBoxProvisionDeatils.getFellowIfAny("ActiveCheckBox_" + stageOrder);
				Combobox rulebox = (Combobox) listBoxProvisionDeatils.getFellowIfAny("NPARule_" + stageOrder);

				unCheckNpaActive.setChecked(false);
				activeBox.setChecked(false);
				dpdDays.setValue(0);
				fillComboBox(npaPymn, "", PennantStaticListUtil.getNPAPaymentTypes(), "");
				fillComboBox(rulebox, "", getNpaRulesList(), "");
			}
		} else {

			for (int i = sequence; i <= this.listBoxProvisionDeatils.getItems().size() - 1; ++i) {

				Listitem item = listBoxProvisionDeatils.getItems().get(i);
				int stageOrder = Integer.parseInt(item.getId().replaceAll("listitem_", ""));
				Checkbox nPAActive = (Checkbox) listBoxProvisionDeatils.getFellowIfAny("NPACheckBox_" + stageOrder);
				nPAActive.setChecked(true);

			}
		}

		logger.debug(Literal.LEAVING);
	}

	// Copy Button
	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING);
	}

	protected void doPostClose() {
		NPAProvisionHeader aNPAProvisionHeader = new NPAProvisionHeader();
		BeanUtils.copyProperties(this.nPAProvisionHeader, aNPAProvisionHeader);
		doWriteComponentsToBean(aNPAProvisionHeader);
		List<NPAProvisionDetail> listNPAProvisionDetail = getProvisionDetails();
		aNPAProvisionHeader.setProvisionDetailsList(listNPAProvisionDetail);
		Events.postEvent("onClick$button_NPAProvisionHeaderList_NewNPAProvisionHeader",
				nPAProvisionHeaderListCtrl.window_NPAProvisionHeaderList, aNPAProvisionHeader);
	}

	// Extract Button
	public void onClick$btnExtract(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		// Excel file downloading automatically using Jasper Report
		StringBuilder searchCriteriaDesc = new StringBuilder(" ");
		ReportsUtil.generateReport(getUserWorkspace().getLoggedInUser().getFullName(), "NPA_Provision", "",
				searchCriteriaDesc);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Get Spacing
	 * 
	 * @param lc_DPDDays
	 * @param component
	 * @param mandatory
	 * @param idName
	 * @param idSeq
	 */
	private void getSpacing(Listcell lc_DPDDays, Component component, boolean mandatory, int idSeq, String idName) {
		Hbox hbox = new Hbox();
		hbox.setParent(lc_DPDDays);
		Space space = new Space();
		space.setId("Space_" + idName + "" + idSeq);
		space.setSpacing("2px");
		if (mandatory) {
			space.setSclass(PennantConstants.mandateSclass);
		} else {
			space.setSclass("");
		}
		space.setParent(hbox);
		hbox.appendChild(component);
	}

	/**
	 * Sorting Provision details based on asset code
	 * 
	 * @param provisionDetailsList
	 */
	private void sortProvisionDetails(List<NPAProvisionDetail> provisionDetailsList) {
		Collections.sort(provisionDetailsList, new Comparator<NPAProvisionDetail>() {
			@Override
			public int compare(NPAProvisionDetail detail1, NPAProvisionDetail detail2) {
				return Long.compare(detail1.getAssetStageOrder(), detail2.getAssetStageOrder());
			}
		});
	}

	// ####### Provision details processing end #####////

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(NPAProvisionHeader aNPAProvisionHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aNPAProvisionHeader.getBefImage(), aNPAProvisionHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aNPAProvisionHeader.getUserDetails(),
				getOverideMap());
	}

	// getting rules based on provision Module for combobox
	public List<ValueLabel> getNpaRulesList() {
		if (CollectionUtils.isEmpty(npaRulesList)) {
			npaRulesList = nPAProvisionHeaderService
					.getRuleByModuleAndEvent(RuleConstants.MODULE_PROVSN, RuleConstants.MODULE_PROVSN, "").stream()
					.map(rule -> new ValueLabel(String.valueOf(rule.getId()), rule.getRuleCode()))
					.collect(Collectors.toList());
		}
		return npaRulesList;
	}

	public void setNPAProvisionHeaderService(NPAProvisionHeaderService nPAProvisionHeaderService) {
		this.nPAProvisionHeaderService = nPAProvisionHeaderService;
	}

}
