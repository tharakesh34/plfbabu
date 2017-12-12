/**
 * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  BuilderGroupDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-05-2017    														*
 *                                                                  						*
 * Modified Date    :  17-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.buildergroup;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BuilderGroup;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.systemmasters.BuilderGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/masters/BuilderGroup/builderGroupDialog.zul file. <br>
 */
public class BuilderGroupDialogCtrl extends GFCBaseCtrl<BuilderGroup>{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BuilderGroupDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BuilderGroupDialog; 
	protected Uppercasebox 		name; 
	protected ExtendedCombobox 		segmentation; 
	private BuilderGroup builderGroup; // overhanded per param

	private transient BuilderGroupListCtrl builderGroupListCtrl; // overhanded per param
	private transient BuilderGroupService builderGroupService;


	/**
	 * default constructor.<br>
	 */
	public BuilderGroupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BuilderGroupDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(String.valueOf(this.builderGroup.getId()));
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
	public void onCreate$window_BuilderGroupDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_BuilderGroupDialog);


		try {
			// Get the required arguments.
			this.builderGroup = (BuilderGroup) arguments.get("builderGroup");
			this.builderGroupListCtrl = (BuilderGroupListCtrl) arguments.get("builderGroupListCtrl");

			if (this.builderGroup == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			BuilderGroup builderGroup = new BuilderGroup();
			BeanUtils.copyProperties(this.builderGroup, builderGroup);
			this.builderGroup.setBefImage(builderGroup);

			// Render the page and display the data.
			doLoadWorkFlow(this.builderGroup.isWorkflow(), this.builderGroup.getWorkflowId(),
					this.builderGroup.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if(!enqiryModule){
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName,getRole());
			}else{
				getUserWorkspace().allocateAuthorities(this.pageRightName,null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.builderGroup);
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

		this.name.setMaxlength(50);

		this.segmentation.setModuleName("LovFieldDetail");
		this.segmentation.setMandatoryStyle(true);
		this.segmentation.setValueColumn("FieldCodeValue");
		this.segmentation.setDescColumn("ValueDesc");
		this.segmentation.setDisplayStyle(2);
		this.segmentation.setValidateColumns(new String[] {"FieldCodeValue"});
		Filter segmentFilter[] = new Filter[1];
		segmentFilter[0] = new Filter("FieldCode", "SEGMENT", Filter.OP_EQUAL);
		this.segmentation.setFilters(segmentFilter);
		
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BuilderGroupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BuilderGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BuilderGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BuilderGroupDialog_btnSave"));
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
	public void onClick$btnDelete(Event event)  throws InterruptedException {
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
		doShowNotes(this.builderGroup);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		builderGroupListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.builderGroup.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}



	public void onFulfill$segmentation(Event event){
		logger.debug("Entering" + event.toString());
		Object dataObject = segmentation.getObject();
		if (dataObject instanceof String) {
			this.segmentation.setObject(null);
			this.segmentation.setValue("","");
		}else{
			if (dataObject instanceof LovFieldDetail) {
				LovFieldDetail lovFieldDetail = (LovFieldDetail) dataObject;
				this.segmentation.setObject(lovFieldDetail);
				this.segmentation.setValue(lovFieldDetail.getFieldCodeValue(), lovFieldDetail.getValueDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}	




	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param builderGroup
	 * 
	 */
	public void doWriteBeanToComponents(BuilderGroup aBuilderGroup) {
		logger.debug(Literal.ENTERING);

		this.name.setValue(aBuilderGroup.getName());
		this.segmentation.setValue(aBuilderGroup.getSegmentation());
		this.segmentation.setDescColumn(aBuilderGroup.getSegmentationName());

		if (aBuilderGroup.isNewRecord()){
			this.segmentation.setDescription("");
		}else{
			this.segmentation.setDescription(aBuilderGroup.getSegmentationName());
		}
		
		this.recordStatus.setValue(aBuilderGroup.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBuilderGroup
	 */
	public void doWriteComponentsToBean(BuilderGroup aBuilderGroup) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Name
		try {
			aBuilderGroup.setName(this.name.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Segmentation
		try {
			aBuilderGroup.setSegmentation(this.segmentation.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
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
	 * @param builderGroup
	 *            The entity that need to be render.
	 */
	public void doShowDialog(BuilderGroup builderGroup) {
		logger.debug(Literal.LEAVING);

		if (builderGroup.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.name.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(builderGroup.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.segmentation.focus();
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

		doWriteBeanToComponents(builderGroup);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.name.isReadonly()){
			this.name.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderGroupDialog_name.value"),PennantRegularExpressions.REGEX_ACC_HOLDER_NAME,true));
		}
		if (!this.segmentation.isReadonly()){
			this.segmentation.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderGroupDialog_segmentation.value"),null,true, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.name.setConstraint("");
		this.segmentation.setConstraint("");

		logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		//id
		//Name
		//Segmentation

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
	 * Deletes a BuilderGroup object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final BuilderGroup aBuilderGroup = new BuilderGroup();
		BeanUtils.copyProperties(this.builderGroup, aBuilderGroup);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aBuilderGroup.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aBuilderGroup.getRecordType()).equals("")){
				aBuilderGroup.setVersion(aBuilderGroup.getVersion()+1);
				aBuilderGroup.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aBuilderGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aBuilderGroup.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aBuilderGroup.getNextTaskId(), aBuilderGroup);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aBuilderGroup,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				MessageUtil.showError(e);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.builderGroup.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.name);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.name);
		}

		readOnlyComponent(isReadOnly("BuilderGroupDialog_segmentation"), this.segmentation);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.builderGroup.isNewRecord()) {
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


		readOnlyComponent(true, this.name);
		readOnlyComponent(true, this.segmentation);

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
		this.name.setValue("");
		this.segmentation.setValue("");
		this.segmentation.setDescription("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final BuilderGroup aBuilderGroup = new BuilderGroup();
		BeanUtils.copyProperties(this.builderGroup, aBuilderGroup);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aBuilderGroup);

		isNew = aBuilderGroup.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBuilderGroup.getRecordType())) {
				aBuilderGroup.setVersion(aBuilderGroup.getVersion() + 1);
				if (isNew) {
					aBuilderGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBuilderGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBuilderGroup.setNewRecord(true);
				}
			}
		} else {
			aBuilderGroup.setVersion(aBuilderGroup.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aBuilderGroup, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	private boolean doProcess(BuilderGroup aBuilderGroup, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBuilderGroup.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aBuilderGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBuilderGroup.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBuilderGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBuilderGroup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBuilderGroup);
				}

				if (isNotesMandatory(taskId, aBuilderGroup)) {
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

			aBuilderGroup.setTaskId(taskId);
			aBuilderGroup.setNextTaskId(nextTaskId);
			aBuilderGroup.setRoleCode(getRole());
			aBuilderGroup.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBuilderGroup, tranType);
			String operationRefs = getServiceOperations(taskId, aBuilderGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBuilderGroup, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBuilderGroup, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BuilderGroup aBuilderGroup = (BuilderGroup) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = builderGroupService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = builderGroupService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = builderGroupService.doApprove(auditHeader);

						if (aBuilderGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = builderGroupService.doReject(auditHeader);
						if (aBuilderGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BuilderGroupDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_BuilderGroupDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.builderGroup), true);
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
			logger.error("Exception: ", e);
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

	private AuditHeader getAuditHeader(BuilderGroup aBuilderGroup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBuilderGroup.getBefImage(), aBuilderGroup);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aBuilderGroup.getUserDetails(),
				getOverideMap());
	}

	public void setBuilderGroupService(BuilderGroupService builderGroupService) {
		this.builderGroupService = builderGroupService;
	}

}
