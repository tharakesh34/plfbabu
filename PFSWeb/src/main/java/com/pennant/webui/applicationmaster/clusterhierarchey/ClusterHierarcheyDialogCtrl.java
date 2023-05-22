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
 * * FileName : ClusterHierarcheyDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-11-2018 * *
 * Modified Date : 21-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-11-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.clusterhierarchey;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.ClusterHierarchyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ClusterHierarchey/clusterHierarcheyDialog.zul
 * file. <br>
 */
public class ClusterHierarcheyDialogCtrl extends GFCBaseCtrl<ClusterHierarchy> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ClusterHierarcheyDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ClusterHierarcheyDialog;
	protected Space space_Entity;
	protected ExtendedCombobox entity;
	protected Button button_ClusterHierarcheyList_ADDClusterHierarchey;
	protected Space space_ClusterType;
	protected Space space_seqOrder;
	private ClusterHierarchy clusterHierarchey;
	protected Listbox listBoxClusterType;

	private transient ClusterHierarcheyListCtrl clusterHierarcheyListCtrl;
	private transient ClusterHierarchyService clusterHierarchyService;

	/**
	 * default constructor.<br>
	 */
	public ClusterHierarcheyDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ClusterHierarcheyDialog";
	}

	@Override
	protected String getReference() {
		return this.clusterHierarchey.getEntity();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ClusterHierarcheyDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ClusterHierarcheyDialog);

		try {
			// Get the required arguments.
			this.clusterHierarchey = (ClusterHierarchy) arguments.get("clusterHierarchey");
			this.clusterHierarcheyListCtrl = (ClusterHierarcheyListCtrl) arguments.get("clusterHierarcheyListCtrl");

			if (clusterHierarchey == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			ClusterHierarchy aclusterHierarchey = new ClusterHierarchy();
			BeanUtils.copyProperties(this.clusterHierarchey, aclusterHierarchey);
			this.clusterHierarchey.setBefImage(aclusterHierarchey);

			// Render the page and display the data.
			doLoadWorkFlow(this.clusterHierarchey.isWorkflow(), this.clusterHierarchey.getWorkflowId(),
					this.clusterHierarchey.getNextTaskId());

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
			doShowDialog(this.clusterHierarchey);
			//
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
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$entity(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = entity.getObject();
		if (dataObject instanceof String) {
			this.entity.setValue(dataObject.toString());

		} else {
			Entity entitydetails = (Entity) dataObject;

			if (entitydetails != null) {
				this.entity.setValue(entitydetails.getEntityCode());
			}
		}
		this.entity.setReadonly(false);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$button_ClusterHierarcheyList_ADDClusterHierarchey(Event event) {
		logger.debug(Literal.ENTERING);

		ClusterHierarchy aclusterHierarchey = new ClusterHierarchy();
		aclusterHierarchey.setVersion(1);
		aclusterHierarchey.setNewRecord(true);

		appendClusterHierarchy(aclusterHierarchey);

		logger.debug(Literal.LEAVING);
	}

	public void appendClusterHierarchy(ClusterHierarchy clusterHierarchy) {
		logger.debug(Literal.ENTERING);

		Listitem item = new Listitem();
		Listcell listcell;
		Uppercasebox textBox = new Uppercasebox();
		textBox.setValue(clusterHierarchy.getClusterType());
		textBox.setMaxlength(8);

		if (clusterHierarchy.isNewRecord()) {
			textBox.setReadonly(false);
		} else {
			textBox.setReadonly(true);
		}

		listcell = new Listcell();
		listcell.appendChild(textBox);
		listcell.setParent(item);

		Intbox seqOrder = new Intbox(clusterHierarchy.getSeqOrder());
		listcell = new Listcell();
		listcell.appendChild(seqOrder);
		listcell.setParent(item);

		item.setAttribute("data", clusterHierarchy);

		if (clusterHierarchy.isNewRecord()) {
			textBox.setReadonly(false);
			seqOrder.setReadonly(false);
		} else {
			textBox.setReadonly(true);
			seqOrder.setReadonly(true);
		}

		listBoxClusterType.appendChild(item);

		if ("CLUSTER_HIERARCHY_APPROVER".equals(clusterHierarchy.getNextRoleCode())
				&& ("Submitted".equals(clusterHierarchy.getRecordStatus()))) {
			textBox.setReadonly(true);
			seqOrder.setReadonly(true);
			this.button_ClusterHierarcheyList_ADDClusterHierarchey.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	public ExtendedCombobox getEntity() {
		return entity;
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ClusterHierarcheyDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ClusterHierarcheyDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ClusterHierarcheyDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ClusterHierarcheyDialog_btnSave"));
		this.btnCancel.setVisible(false);
		// this.textBox.setReadonly(true);
		// this.seqOrder.setReadonly(true);
		// this.button_ClusterHierarcheyList_ADDClusterHierarchey.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		// doPrepareList(clusterHierarchey);
		doSave(clusterHierarchey);
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
		doShowNotes(this.clusterHierarchey);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		clusterHierarcheyListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.clusterHierarchey.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param clusterHierarchey
	 * 
	 */
	public void doWriteBeanToComponents(ClusterHierarchy aClusterHierarchey) {
		logger.debug(Literal.ENTERING);

		if (aClusterHierarchey != null) {
			this.entity.setValue(aClusterHierarchey.getEntity());
			this.recordStatus.setValue(aClusterHierarchey.getRecordStatus());
			for (ClusterHierarchy cluster : aClusterHierarchey.getClusterTypes()) {
				appendClusterHierarchy(cluster);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aClusterHierarchey
	 */
	public void doWriteComponentsToBean(ClusterHierarchy aClusterHierarchey) {
		logger.debug(Literal.ENTERING);

		doSetValidation();

		List<WrongValueException> wve = new ArrayList<>();

		try {
			aClusterHierarchey.setEntity(this.entity.getValue());

			if (StringUtils.isEmpty(this.entity.getValue())) {
				throw new WrongValueException(this.entity, "Entity is Mandatory.");
			}

			doPrepareList(aClusterHierarchey);

		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doPrepareList(ClusterHierarchy clusterHierarchey) {
		ClusterHierarchy clusterHierarcheys = null;
		Timestamp lastMntTime = new Timestamp(System.currentTimeMillis());
		clusterHierarchey.getClusterTypes().clear();
		for (Listitem listitem : listBoxClusterType.getItems()) {
			clusterHierarcheys = (ClusterHierarchy) listitem.getAttribute("data");
			List<Listcell> list = listitem.getChildren();
			Textbox clustertype = (Textbox) list.get(0).getFirstChild();
			clusterHierarcheys.setClusterType(clustertype.getValue());
			Intbox seqno = (Intbox) list.get(1).getFirstChild();
			clusterHierarcheys.setSeqOrder(seqno.getValue());

			clusterHierarcheys.setEntity(entity.getValue());
			clusterHierarcheys.setWorkflowId(clusterHierarchey.getWorkflowId());
			clusterHierarcheys.setLastMntOn(lastMntTime);
			if (!clusterHierarcheys.isNewRecord()) {
				clusterHierarcheys.setVersion(clusterHierarcheys.getVersion() + 1);
			}
			clusterHierarchey.getClusterTypes().add(clusterHierarcheys);
		}
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param clusterHierarchey The entity that need to be render.
	 */
	public void doShowDialog(ClusterHierarchy clusterHierarchey) {
		logger.debug(Literal.ENTERING);

		if (clusterHierarchey.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.entity.focus();
		} else {
			this.entity.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(clusterHierarchey.getRecordType())) {
					this.btnNotes.setVisible(true);
				}

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

		doWriteBeanToComponents(clusterHierarchey);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.entity.isReadonly()) {
			this.entity.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ClusterHierarcheyDialog_Entity.value"), null, true, true));
		}

		for (Component component : listBoxClusterType.getChildren()) {
			if (component instanceof Listhead) {
				continue;
			}

			Listitem listitem = (Listitem) component;
			Uppercasebox textBox = (Uppercasebox) listitem.getFirstChild().getFirstChild();
			textBox.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ClusterHierarcheyDialog_ClusterType.value"),
							PennantRegularExpressions.REGEX_ALPHA, true));
			Intbox seqOrder = (Intbox) listitem.getLastChild().getLastChild();
			seqOrder.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ClusterHierarcheyDialog_seqOrder.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		this.entity.setConstraint("");

		for (Component component : listBoxClusterType.getChildren()) {
			if (component instanceof Listhead) {
				continue;
			}

			Listitem listitem = (Listitem) component;
			Uppercasebox textBox = (Uppercasebox) listitem.getFirstChild().getFirstChild();
			textBox.setConstraint("");
		}

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

	/**
	 * Deletes a ClusterHierarchey object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final ClusterHierarchy aClusterHierarchey = new ClusterHierarchy();
		BeanUtils.copyProperties(this.clusterHierarchey, aClusterHierarchey);

		doDelete(aClusterHierarchey.getEntity(), aClusterHierarchey);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.clusterHierarchey.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.entity);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.entity);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.clusterHierarchey.isNewRecord()) {
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

		readOnlyComponent(true, this.entity);

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
		logger.debug(Literal.LEAVING);
		this.entity.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave(ClusterHierarchy clusters) {
		logger.debug(Literal.LEAVING);

		ClusterHierarchy aClusterHierarchey = new ClusterHierarchy();
		BeanUtils.copyProperties(clusters, aClusterHierarchey);
		boolean isNew = false;

		doWriteComponentsToBean(aClusterHierarchey);

		if (aClusterHierarchey.getClusterTypes().isEmpty()) {
			MessageUtil.showError("Should have atleast one cluster type.");
			return;
		}

		isNew = aClusterHierarchey.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aClusterHierarchey.getRecordType())) {
				aClusterHierarchey.setVersion(aClusterHierarchey.getVersion() + 1);
				if (isNew) {
					aClusterHierarchey.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aClusterHierarchey.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aClusterHierarchey.setNewRecord(true);
				}
			}
		} else {
			aClusterHierarchey.setVersion(aClusterHierarchey.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aClusterHierarchey, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final Exception e) {
			logger.error(Literal.EXCEPTION, e);
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
	protected boolean doProcess(ClusterHierarchy aClusterHierarchey, String tranType) {
		logger.debug(Literal.LEAVING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aClusterHierarchey.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aClusterHierarchey.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aClusterHierarchey.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aClusterHierarchey.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aClusterHierarchey);
				}

				if (isNotesMandatory(taskId, aClusterHierarchey)) {
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

			aClusterHierarchey.setTaskId(taskId);
			aClusterHierarchey.setNextTaskId(nextTaskId);
			aClusterHierarchey.setRoleCode(getRole());
			aClusterHierarchey.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aClusterHierarchey, tranType);
			String operationRefs = getServiceOperations(taskId, aClusterHierarchey);

			for (ClusterHierarchy clusterhierarchy : aClusterHierarchey.getClusterTypes()) {
				clusterhierarchy.setTaskId(taskId);
				clusterhierarchy.setNextTaskId(nextTaskId);
				clusterhierarchy.setRoleCode(getRole());
				clusterhierarchy.setNextRoleCode(nextRoleCode);
				clusterhierarchy.setLastMntBy(aClusterHierarchey.getLastMntBy());
				clusterhierarchy.setRecordStatus(aClusterHierarchey.getRecordStatus());
				clusterhierarchy.setUserDetails(aClusterHierarchey.getUserDetails());
				clusterhierarchy.setWorkflowId(aClusterHierarchey.getWorkflowId());
				clusterhierarchy.setRecordType(aClusterHierarchey.getRecordType());
			}

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aClusterHierarchey, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aClusterHierarchey, tranType);
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
		logger.debug(Literal.LEAVING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ClusterHierarchy aClusterHierarchey = (ClusterHierarchy) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = clusterHierarchyService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = clusterHierarchyService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = clusterHierarchyService.doApprove(auditHeader);

					if (aClusterHierarchey.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = clusterHierarchyService.doReject(auditHeader);
					if (aClusterHierarchey.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					ErrorControl.showErrorControl(this.window_ClusterHierarcheyDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ClusterHierarcheyDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.clusterHierarchey), true);
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

	private AuditHeader getAuditHeader(ClusterHierarchy aClusterHierarchey, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aClusterHierarchey.getBefImage(), aClusterHierarchey);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aClusterHierarchey.getUserDetails(),
				getOverideMap());
	}

	public void setClusterHierarchyService(ClusterHierarchyService clusterHierarchyService) {
		this.clusterHierarchyService = clusterHierarchyService;
	}

}
