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
 * FileName    		:  BuilderProjcetDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-05-2017    														*
 *                                                                  						*
 * Modified Date    :  22-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.builderprojcet;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.service.systemmasters.BuilderProjcetService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/masters/BuilderProjcet/builderProjcetDialog.zul file. <br>
 */
public class BuilderProjcetDialogCtrl extends GFCBaseCtrl<BuilderProjcet>{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BuilderProjcetDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window                window_BuilderProjcetDialog; 
   //	protected Longbox 		        id; 
	protected Uppercasebox 		    name; 
    protected ExtendedCombobox 		builderId; 
	protected Textbox 		        apfNo; 
	private BuilderProjcet          builderProjcet; // overhanded per param

	private transient BuilderProjcetListCtrl builderprojcetListCtrl; // overhanded per param
	private transient BuilderProjcetService builderProjcetService;
	

	/**
	 * default constructor.<br>
	 */
	public BuilderProjcetDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BuilderProjcetDialog";
	}
	
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(String.valueOf(this.builderProjcet.getId()));
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
	public void onCreate$window_BuilderProjcetDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_BuilderProjcetDialog);

		
		try {
			// Get the required arguments.
			this.builderProjcet = (BuilderProjcet) arguments.get("builderprojcet");
			this.builderprojcetListCtrl = (BuilderProjcetListCtrl) arguments.get("builderprojcetListCtrl");

			if (this.builderProjcet == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			BuilderProjcet builderProjcet = new BuilderProjcet();
			BeanUtils.copyProperties(this.builderProjcet, builderProjcet);
			this.builderProjcet.setBefImage(builderProjcet);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.builderProjcet.isWorkflow(), this.builderProjcet.getWorkflowId(),
					this.builderProjcet.getNextTaskId());

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
			doShowDialog(this.builderProjcet);
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
			this.builderId.setModuleName("BuilderCompany");
			this.builderId.setValueColumn("Id");
			this.builderId.setDescColumn("Name");
			this.builderId.setValidateColumns(new String[] {"Id"});
			this.builderId.setMandatoryStyle(true);
			this.apfNo.setMaxlength(20);
		
		setStatusDetails();
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnSave"));
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
		doShowNotes(this.builderProjcet);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		builderprojcetListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.builderProjcet.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param builderProjcet
	 * 
	 */
	public void doWriteBeanToComponents(BuilderProjcet aBuilderProjcet) {
		logger.debug(Literal.ENTERING);
	
			this.name.setValue(aBuilderProjcet.getName());
			this.apfNo.setValue(aBuilderProjcet.getApfNo());
		
		if (aBuilderProjcet.isNewRecord()){
			   this.builderId.setDescription("");
		}else{
			this.builderId.setValue(String.valueOf(aBuilderProjcet.getBuilderId()));
			this.builderId.setDescription(String.valueOf(aBuilderProjcet.getbuilderIdName()));
		}
		this.recordStatus.setValue(aBuilderProjcet.getRecordStatus());
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBuilderProjcet
	 */
	public void doWriteComponentsToBean(BuilderProjcet aBuilderProjcet) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		/*//Project ID
		try {
		    aBuilderProjcet.setId(this.id.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		//Name
		try {
		    aBuilderProjcet.setName(this.name.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Company
		try {
			aBuilderProjcet.setBuilderId(Long.parseLong(this.builderId.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//APF No
		try {
		    aBuilderProjcet.setApfNo(this.apfNo.getValue());
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
	 * @param builderProjcet
	 *            The entity that need to be render.
	 */
	public void doShowDialog(BuilderProjcet builderProjcet) {
		logger.debug(Literal.LEAVING);

		if (builderProjcet.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.name.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(builderProjcet.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.name.focus();
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

		doWriteBeanToComponents(builderProjcet);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.name.isReadonly()){
			this.name.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderProjcetDialog_name.value"),PennantRegularExpressions.REGEX_ACC_HOLDER_NAME,true));
		}
		if (!this.builderId.isReadonly()){
			this.builderId.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderProjcetDialog_builderId.value"),null,true,true));
		}
		/*if (!this.apfNo.isReadonly()){
			this.apfNo.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderProjcetDialog_apfNo.value"),PennantRegularExpressions.REGEX_NAME,true));
		}*/
	
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		
		this.name.setConstraint("");
		this.builderId.setConstraint("");
		this.apfNo.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		//Project ID
		//Name
		//Company
		//APF No
		
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
	 * Deletes a BuilderProjcet object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final BuilderProjcet aBuilderProjcet = new BuilderProjcet();
		BeanUtils.copyProperties(this.builderProjcet, aBuilderProjcet);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aBuilderProjcet.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aBuilderProjcet.getRecordType()).equals("")){
				aBuilderProjcet.setVersion(aBuilderProjcet.getVersion()+1);
				aBuilderProjcet.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aBuilderProjcet.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aBuilderProjcet.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aBuilderProjcet.getNextTaskId(), aBuilderProjcet);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aBuilderProjcet,tranType)){
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
		
		if (this.builderProjcet.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			
		}
	
			readOnlyComponent(isReadOnly("BuilderProjcetDialog_name"), this.name);
			readOnlyComponent(isReadOnly("BuilderProjcetDialog_builderId"), this.builderId);
			readOnlyComponent(isReadOnly("BuilderProjcetDialog_apfNo"), this.apfNo);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.builderProjcet.isNewRecord()) {
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
			readOnlyComponent(true, this.builderId);
			readOnlyComponent(true, this.apfNo);

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
			  	this.builderId.setValue("");
			  	this.builderId.setDescription("");
				this.apfNo.setValue("");

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final BuilderProjcet aBuilderProjcet = new BuilderProjcet();
			BeanUtils.copyProperties(this.builderProjcet, aBuilderProjcet);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aBuilderProjcet);

			isNew = aBuilderProjcet.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aBuilderProjcet.getRecordType())) {
					aBuilderProjcet.setVersion(aBuilderProjcet.getVersion() + 1);
					if (isNew) {
						aBuilderProjcet.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aBuilderProjcet.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aBuilderProjcet.setNewRecord(true);
					}
				}
			} else {
				aBuilderProjcet.setVersion(aBuilderProjcet.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aBuilderProjcet, tranType)) {
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
		private boolean doProcess(BuilderProjcet aBuilderProjcet, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aBuilderProjcet.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			aBuilderProjcet.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aBuilderProjcet.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aBuilderProjcet.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aBuilderProjcet.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aBuilderProjcet);
					}

					if (isNotesMandatory(taskId, aBuilderProjcet)) {
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

				aBuilderProjcet.setTaskId(taskId);
				aBuilderProjcet.setNextTaskId(nextTaskId);
				aBuilderProjcet.setRoleCode(getRole());
				aBuilderProjcet.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aBuilderProjcet, tranType);
				String operationRefs = getServiceOperations(taskId, aBuilderProjcet);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aBuilderProjcet, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aBuilderProjcet, tranType);
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
			BuilderProjcet aBuilderProjcet = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = builderProjcetService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = builderProjcetService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = builderProjcetService.doApprove(auditHeader);

							if (aBuilderProjcet.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = builderProjcetService.doReject(auditHeader);
							if (aBuilderProjcet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_BuilderProjcetDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_BuilderProjcetDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.builderProjcet), true);
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

		private AuditHeader getAuditHeader(BuilderProjcet aBuilderProjcet, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aBuilderProjcet.getBefImage(), aBuilderProjcet);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aBuilderProjcet.getUserDetails(),
					getOverideMap());
		}

		public void setBuilderProjcetService(BuilderProjcetService builderProjcetService) {
			this.builderProjcetService = builderProjcetService;
		}
			
}