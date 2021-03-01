/**
o * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ClusterDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-11-2018    														*
 *                                                                  						*
 * Modified Date    :  21-11-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-11-2018       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.webui.applicationmaster.cluster;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Cluster/clusterDialog.zul file. <br>
 */
public class ClusterDialogCtrl extends GFCBaseCtrl<Cluster> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ClusterDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ClusterDialog;
	protected ExtendedCombobox entity;
	protected Textbox code;
	protected ExtendedCombobox clusterType;
	protected Textbox name;
	protected ExtendedCombobox parent;
	protected Textbox parentType;
	private Cluster cluster;

	private transient ClusterListCtrl clusterListCtrl;
	private transient ClusterService clusterService;

	/**
	 * default constructor.<br>
	 */
	public ClusterDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ClusterDialog";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.cluster.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_ClusterDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ClusterDialog);

		try {
			// Get the required arguments.
			this.cluster = (Cluster) arguments.get("cluster");
			this.clusterListCtrl = (ClusterListCtrl) arguments.get("clusterListCtrl");

			if (this.cluster == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			Cluster acluster = new Cluster();
			BeanUtils.copyProperties(this.cluster, acluster);
			this.cluster.setBefImage(acluster);

			// Render the page and display the data.
			doLoadWorkFlow(this.cluster.isWorkflow(), this.cluster.getWorkflowId(), this.cluster.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.cluster);
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
		this.entity.addForward(ExtendedCombobox.ON_FUL_FILL, self, "onChangeEntity", null);
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		this.code.setMaxlength(8);
		this.name.setMaxlength(50);

		this.clusterType.setMandatoryStyle(true);
		this.parent.setMandatoryStyle(true);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities(pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ClusterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ClusterDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ClusterDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ClusterDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.cluster);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		clusterListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.cluster.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param cluster
	 * 
	 */
	public void doWriteBeanToComponents(Cluster aCluster) {
		logger.debug(Literal.ENTERING);
		this.entity.setValue(aCluster.getEntity());

		if (StringUtils.isNotEmpty(aCluster.getEntity())) {
			Entity entity = new Entity();
			entity.setEntityCode(aCluster.getEntity());
			entity.setEntityDesc(aCluster.getEntityDesc());
			this.entity.setObject(entity);
			onChangeEntity();
		}

		this.clusterType.setValue(aCluster.getClusterType());

		if (aCluster.getClusterType() != null) {
			ClusterHierarchy acClusterHierarchey = new ClusterHierarchy();
			acClusterHierarchey.setClusterType(aCluster.getClusterType());
			acClusterHierarchey.setEntity((aCluster.getEntity()));
			this.clusterType.setObject(acClusterHierarchey);
			onChangeClusterType();
		}

		this.code.setValue(aCluster.getCode());
		this.name.setValue(aCluster.getName());

		Cluster aParent = new Cluster();
		if (aCluster.getParent() != null) {
			aParent.setId(aCluster.getParent());
			this.parent.setObject(aParent);
			this.parent.setValue(aCluster.getParentCode());
			this.parent.setDescription(aCluster.getParentName());
		} else {
			aParent.setId(null);
			this.parent.setValue(aCluster.getEntity());
		}
		this.parentType.setValue(aCluster.getParentType());
		this.recordStatus.setValue(aCluster.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	public void onChangeEntity(ForwardEvent event) {
		onChangeEntity();
	}

	private void onChangeEntity() {
		Object selectedEntiy = entity.getObject();

		if (selectedEntiy != null && selectedEntiy instanceof Entity) {
			doSetClusterTypeFilter((Entity) selectedEntiy);
		} else {
			doSetClusterTypeFilter(null);
		}
	}

	private void doSetClusterTypeFilter(Entity entity) {

		this.clusterType.setModuleName("ClusterHierarchy");
		this.clusterType.setValueColumn("ClusterType");
		this.clusterType.setDescColumn("ClusterType");
		this.clusterType.addForward(ExtendedCombobox.ON_FUL_FILL, self, "onChangeClusterType", null);
		this.clusterType.setValidateColumns(new String[] { "ClusterType" });

		String selectedEntity = this.entity.getValidatedValue();

		if (entity != null) {
			this.clusterType.setFilters(new Filter[] { new Filter("Entity", selectedEntity, Filter.OP_EQUAL) });
		} else {
			this.clusterType.setValue("");
			this.clusterType.setFilters(new Filter[] { new Filter("Entity", null, Filter.OP_EQUAL) });
			return;

		}
	}

	public void onChangeClusterType(ForwardEvent event) {
		onChangeClusterType();
	}

	private void onChangeClusterType() {
		Object selectedclusterType = clusterType.getObject();

		if (selectedclusterType != null && selectedclusterType instanceof ClusterHierarchy) {
			doSetParentFilter((ClusterHierarchy) selectedclusterType);
		} else {
			doSetParentFilter(null);
		}
	}

	private void doSetParentFilter(ClusterHierarchy aclusterHierarchey) {

		this.parent.setModuleName("Cluster");
		this.parent.setValueColumn("Code");
		this.parent.setDescColumn("Name");
		this.parent.setValidateColumns(new String[] { "Code" });

		if (aclusterHierarchey == null) {
			this.parent.setValue("");
			this.parent.setFilters(new Filter[] { new Filter("clusterType", null, Filter.OP_EQUAL) });
			return;
		}

		String selectedEntity = aclusterHierarchey.getEntity();
		String selectedClusterType = aclusterHierarchey.getClusterType();
		List<ClusterHierarchy> hierarchyList = clusterService.getClusterHierarcheyList(selectedEntity);
		String parentCluster = null;
		Iterator<ClusterHierarchy> it = hierarchyList.iterator();

		while (it.hasNext()) {
			ClusterHierarchy clusterHierarchey = it.next();
			if (StringUtils.equals(selectedClusterType, clusterHierarchey.getClusterType())) {
				parentCluster = clusterHierarchey.getClusterType();
				if (!isReadOnly("ClusterDialog_Parent")) {
					this.parent.setReadonly(false);
				}
				Filter[] clusterTypeFilter = new Filter[1];
				clusterTypeFilter[0] = new Filter("clusterType", parentCluster, Filter.OP_EQUAL);
				this.parent.setFilters(clusterTypeFilter);
				break;
			} else {
				parentCluster = null;
			}
		}
		if (parentCluster == null) {
			this.parentType.setValue(selectedClusterType);
			this.parent.setValue(selectedEntity);
			this.parent.setReadonly(true);
		}
		if (parentCluster != null) {
			this.parent.setFilters(new Filter[] { new Filter("clusterType", parentCluster, Filter.OP_EQUAL) });
		} else {
			this.parent.setFilters(new Filter[] { new Filter("clusterType", null, Filter.OP_EQUAL) });
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCluster
	 */
	public void doWriteComponentsToBean(Cluster aCluster) {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		doSetLOVValidation();

		List<WrongValueException> wve = new ArrayList<>();

		// Entity
		try {
			aCluster.setEntity(this.entity.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// CLusterTYpe
		try {

			this.clusterType.getValidatedValue();

			Object aObject = (Object) this.clusterType.getObject();
			if (aObject != null && aObject instanceof ClusterHierarchy) {
				ClusterHierarchy acClusterHierarchey = (ClusterHierarchy) aObject;
				aCluster.setClusterType(acClusterHierarchey.getClusterType());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Code
		try {
			aCluster.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Name
		try {
			aCluster.setName(this.name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.parent.getValidatedValue();
			if (StringUtils.equals(this.entity.getValue(), this.parent.getValue())) {
				aCluster.setParent(null);
			} else {
				Object aObject = (Object) this.parent.getObject();
				if (aObject != null && aObject instanceof Cluster) {
					Cluster cluster = (Cluster) aObject;
					aCluster.setParent(cluster.getId());

				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// ParenType
		try {
			aCluster.setParentType(parentType.getValue());
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

	/**
	 * Displays the dialog page.
	 * 
	 * @param cluster
	 *            The entity that need to be render.
	 */
	public void doShowDialog(Cluster cluster) {
		logger.debug(Literal.ENTERING);

		if (cluster.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.entity.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(cluster.getRecordType())) {
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

		doWriteBeanToComponents(cluster);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.entity.isReadonly()) {
			this.entity.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ClusterDialog_Entity.value"), null, true, true));
		}
		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_ClusterDialog_Code.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}
		if (!this.clusterType.isReadonly()) {
			this.clusterType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ClusterDialog_ClusterType.value"), null, true, true));
		}
		if (!this.name.isReadonly()) {
			this.name.setConstraint(new PTStringValidator(Labels.getLabel("label_ClusterDialog_Name.value"),
					PennantRegularExpressions.REGEX_CLUST_NAME, true));
		}
		if (!this.parent.isReadonly()) {
			this.parent.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ClusterDialog_Parent.value"), null, true, true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.entity.setConstraint("");
		this.code.setConstraint("");
		this.clusterType.setConstraint("");
		this.name.setConstraint("");
		this.parent.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a Cluster object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Cluster aCluster = new Cluster();
		BeanUtils.copyProperties(this.cluster, aCluster);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aCluster.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aCluster.getRecordType()).equals("")) {
				aCluster.setVersion(aCluster.getVersion() + 1);
				aCluster.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCluster.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aCluster.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aCluster.getNextTaskId(), aCluster);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCluster, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
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

		if (this.cluster.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.entity);
			readOnlyComponent(false, this.code);
			readOnlyComponent(false, this.clusterType);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.entity);
			readOnlyComponent(true, this.code);
			readOnlyComponent(true, this.clusterType);

		}

		readOnlyComponent(isReadOnly("ClusterDialog_Name"), this.name);
		readOnlyComponent(isReadOnly("ClusterDialog_Parent"), this.parent);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.cluster.isNewRecord()) {
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
		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.clusterType);
		readOnlyComponent(true, this.name);
		readOnlyComponent(true, this.parent);

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
		this.entity.setValue("");
		this.code.setValue("");
		this.clusterType.setValue("");
		this.name.setValue("");
		this.parent.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final Cluster aCluster = new Cluster();
		BeanUtils.copyProperties(this.cluster, aCluster);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aCluster);

		isNew = aCluster.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCluster.getRecordType())) {
				aCluster.setVersion(aCluster.getVersion() + 1);
				if (isNew) {
					aCluster.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCluster.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCluster.setNewRecord(true);
				}
			}
		} else {
			aCluster.setVersion(aCluster.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aCluster, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final Exception e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Cluster aCluster, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCluster.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aCluster.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCluster.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCluster.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCluster.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCluster);
				}

				if (isNotesMandatory(taskId, aCluster) && !notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCluster.setTaskId(taskId);
			aCluster.setNextTaskId(nextTaskId);
			aCluster.setRoleCode(getRole());
			aCluster.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCluster, tranType);
			String operationRefs = getServiceOperations(taskId, aCluster);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCluster, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCluster, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Cluster aCluster = (Cluster) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = clusterService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = clusterService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = clusterService.doApprove(auditHeader);

						if (aCluster.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = clusterService.doReject(auditHeader);
						if (aCluster.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						ErrorControl.showErrorControl(this.window_ClusterDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ClusterDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.cluster), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(Literal.EXCEPTION, e);
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

	private AuditHeader getAuditHeader(Cluster aCluster, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCluster.getBefImage(), aCluster);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aCluster.getUserDetails(),
				getOverideMap());
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

}
