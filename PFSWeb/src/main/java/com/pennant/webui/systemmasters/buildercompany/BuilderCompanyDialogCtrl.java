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
 * FileName    		:  BuilderCompanyDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.buildercompany;

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
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.service.systemmasters.BuilderCompanyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/masters/BuilderCompany/builderCompanyDialog.zul file. <br>
 */
public class BuilderCompanyDialogCtrl extends GFCBaseCtrl<BuilderCompany>{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BuilderCompanyDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected  Window                   window_BuilderCompanyDialog; 
	protected  Uppercasebox 		    name; 
    protected  ExtendedCombobox 		segmentation; 
    protected  ExtendedCombobox 		groupId; 
	private    BuilderCompany 			builderCompany; // overhanded per param

	private transient BuilderCompanyListCtrl buildercompanyListCtrl; // overhanded per param
	private transient BuilderCompanyService builderCompanyService;
	

	/**
	 * default constructor.<br>
	 */
	public BuilderCompanyDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BuilderCompanyDialog";
	}
	
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(String.valueOf(this.builderCompany.getId()));
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
	public void onCreate$window_BuilderCompanyDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_BuilderCompanyDialog);

		
		try {
			// Get the required arguments.
			this.builderCompany = (BuilderCompany) arguments.get("buildercompany");
			this.buildercompanyListCtrl = (BuilderCompanyListCtrl) arguments.get("buildercompanyListCtrl");

			if (this.builderCompany == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			BuilderCompany builderCompany = new BuilderCompany();
			BeanUtils.copyProperties(this.builderCompany, builderCompany);
			this.builderCompany.setBefImage(builderCompany);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.builderCompany.isWorkflow(), this.builderCompany.getWorkflowId(),
					this.builderCompany.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"BuilderCompanyDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.builderCompany);
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
		this.groupId.setModuleName("BuilderGroup");
		this.groupId.setValueColumn("Id");
		this.groupId.setDescColumn("Name");
		this.groupId.setValidateColumns(new String[]{"Id"});
		this.groupId.setMandatoryStyle(true);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BuilderCompanyDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BuilderCompanyDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BuilderCompanyDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BuilderCompanyDialog_btnSave"));
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
		doShowNotes(this.builderCompany);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		buildercompanyListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.builderCompany.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	


      public void onFulfillSegmentation(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.segmentation.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param builderCompany
	 * 
	 */
	
      public void doWriteBeanToComponents(BuilderCompany aBuilderCompany) {
  		logger.debug(Literal.ENTERING);
  		this.name.setValue(aBuilderCompany.getName());
  		this.segmentation.setValue(aBuilderCompany.getSegmentation());

  		if (aBuilderCompany.isNewRecord()){
  			this.segmentation.setDescription("");
  			this.groupId.setDescription("");
  		}else{
  			this.segmentation.setDescription(aBuilderCompany.getSegmentationName());
  			this.groupId.setValue(String.valueOf(aBuilderCompany.getGroupId()));
  			this.groupId.setDescription(String.valueOf(aBuilderCompany.getGroupIdName()));
  		}
  		
  		this.recordStatus.setValue(aBuilderCompany.getRecordStatus());
  		
  		logger.debug(Literal.LEAVING);
  	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBuilderCompany
	 */
	public void doWriteComponentsToBean(BuilderCompany aBuilderCompany) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Name
		try {
		    aBuilderCompany.setName(this.name.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Ssegmentation
		try {
			aBuilderCompany.setSegmentation(this.segmentation.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Builder Group
		try {
			
			aBuilderCompany.setGroupId(Long.parseLong(this.groupId.getValue()));
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
	 * @param builderCompany
	 *            The entity that need to be render.
	 */
	public void doShowDialog(BuilderCompany builderCompany) {
		logger.debug(Literal.LEAVING);

		if (builderCompany.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.name.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(builderCompany.getRecordType())) {
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

		doWriteBeanToComponents(builderCompany);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.name.isReadonly()){
			this.name.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_name.value"),PennantRegularExpressions.REGEX_ACC_HOLDER_NAME,true));
		}
		if (!this.segmentation.isReadonly()){
			this.segmentation.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_segmentation.value"),null,true,true));
		}
		if (!this.groupId.isReadonly()){
			this.groupId.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderCompanyDialog_groupId.value"),null,true,true));
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
		this.groupId.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		//id
		//Name
		//Ssegmentation
		//Builder Group
		
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
	 * Deletes a BuilderCompany object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final BuilderCompany aBuilderCompany = new BuilderCompany();
		BeanUtils.copyProperties(this.builderCompany, aBuilderCompany);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aBuilderCompany.getName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aBuilderCompany.getRecordType()).equals("")){
				aBuilderCompany.setVersion(aBuilderCompany.getVersion()+1);
				aBuilderCompany.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aBuilderCompany.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aBuilderCompany.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aBuilderCompany.getNextTaskId(), aBuilderCompany);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aBuilderCompany,tranType)){
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
		
		if (this.builderCompany.isNewRecord()) {
			readOnlyComponent(false, this.name);
			readOnlyComponent(false, this.groupId);
			this.btnCancel.setVisible(false);
		} else {
			readOnlyComponent(true, this.name);
			this.btnCancel.setVisible(true);
			
		}
			readOnlyComponent(isReadOnly("BuilderCompanyDialog_groupId"), this.groupId);
			readOnlyComponent(isReadOnly("BuilderCompanyDialog_segmentation"), this.segmentation);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.builderCompany.isNewRecord()) {
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
			readOnlyComponent(true, this.groupId);

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
			  	this.groupId.setValue("");
			  	this.groupId.setDescription("");

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final BuilderCompany aBuilderCompany = new BuilderCompany();
			BeanUtils.copyProperties(this.builderCompany, aBuilderCompany);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aBuilderCompany);

			isNew = aBuilderCompany.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aBuilderCompany.getRecordType())) {
					aBuilderCompany.setVersion(aBuilderCompany.getVersion() + 1);
					if (isNew) {
						aBuilderCompany.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aBuilderCompany.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aBuilderCompany.setNewRecord(true);
					}
				}
			} else {
				aBuilderCompany.setVersion(aBuilderCompany.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aBuilderCompany, tranType)) {
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
		private boolean doProcess(BuilderCompany aBuilderCompany, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aBuilderCompany.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			aBuilderCompany.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aBuilderCompany.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aBuilderCompany.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aBuilderCompany.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aBuilderCompany);
					}

					if (isNotesMandatory(taskId, aBuilderCompany)) {
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

				aBuilderCompany.setTaskId(taskId);
				aBuilderCompany.setNextTaskId(nextTaskId);
				aBuilderCompany.setRoleCode(getRole());
				aBuilderCompany.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aBuilderCompany, tranType);
				String operationRefs = getServiceOperations(taskId, aBuilderCompany);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aBuilderCompany, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aBuilderCompany, tranType);
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
			BuilderCompany aBuilderCompany = (BuilderCompany) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = builderCompanyService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = builderCompanyService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = builderCompanyService.doApprove(auditHeader);

							if (aBuilderCompany.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = builderCompanyService.doReject(auditHeader);
							if (aBuilderCompany.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_BuilderCompanyDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_BuilderCompanyDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.builderCompany), true);
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

		private AuditHeader getAuditHeader(BuilderCompany aBuilderCompany, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aBuilderCompany.getBefImage(), aBuilderCompany);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aBuilderCompany.getUserDetails(),
					getOverideMap());
		}

		public void setBuilderCompanyService(BuilderCompanyService builderCompanyService) {
			this.builderCompanyService = builderCompanyService;
		}
			
}