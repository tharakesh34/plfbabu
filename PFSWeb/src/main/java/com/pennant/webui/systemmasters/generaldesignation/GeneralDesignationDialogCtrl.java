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
 * FileName    		:  GeneralDesignationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.generaldesignation;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.service.systemmasters.GeneralDesignationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/GeneralDesignation/generalDesignationDialog.zul file.
 */
public class GeneralDesignationDialogCtrl extends GFCBaseCtrl<GeneralDesignation> {
	private static final long serialVersionUID = -1696783232080077214L;
	private static final Logger logger = Logger.getLogger(GeneralDesignationDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_GeneralDesignationDialog; 	

	protected Textbox 		genDesignation; 					
	protected Textbox 		genDesgDesc; 
	protected Checkbox 		genDesgIsActive;

	// not auto wired Var's
	private GeneralDesignation generalDesignation; // overHanded per param
	private transient GeneralDesignationListCtrl generalDesignationListCtrl; // overHanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient GeneralDesignationService generalDesignationService;
	

	/**
	 * default constructor.<br>
	 */
	public GeneralDesignationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "GeneralDesignationDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected GeneralDesignation
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GeneralDesignationDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_GeneralDesignationDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("generalDesignation")) {
				this.generalDesignation = (GeneralDesignation) arguments
						.get("generalDesignation");
				GeneralDesignation befImage = new GeneralDesignation();
				BeanUtils.copyProperties(this.generalDesignation, befImage);
				this.generalDesignation.setBefImage(befImage);

				setGeneralDesignation(this.generalDesignation);
			} else {
				setGeneralDesignation(null);
			}

			doLoadWorkFlow(this.generalDesignation.isWorkflow(),
					this.generalDesignation.getWorkflowId(),
					this.generalDesignation.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"GeneralDesignationDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED params !
			// we get the generalDesignationListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete generalDesignation here.
			if (arguments.containsKey("generalDesignationListCtrl")) {
				setGeneralDesignationListCtrl((GeneralDesignationListCtrl) arguments
						.get("generalDesignationListCtrl"));
			} else {
				setGeneralDesignationListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getGeneralDesignation());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_GeneralDesignationDialog.onClose();
		}
		logger.debug("Leaving"+event.toString());
		
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.genDesignation.setMaxlength(8);
		this.genDesgDesc.setMaxlength(50);
		
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDesignationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDesignationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDesignationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDesignationDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());		
		doSave();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering"+event.toString());
		doEdit();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		MessageUtil.showHelpWindow(event, window_GeneralDesignationDialog);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		doDelete();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering"+event.toString());
		doCancel();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.generalDesignation.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aGeneralDesignation
	 *            GeneralDesignation
	 */
	public void doWriteBeanToComponents(GeneralDesignation aGeneralDesignation) {
		logger.debug("Entering");
		this.genDesignation.setValue(aGeneralDesignation.getGenDesignation());
		this.genDesgDesc.setValue(aGeneralDesignation.getGenDesgDesc());
		this.genDesgIsActive.setChecked(aGeneralDesignation.isGenDesgIsActive());
		this.recordStatus.setValue(aGeneralDesignation.getRecordStatus());
		
		if(aGeneralDesignation.isNew() || (aGeneralDesignation.getRecordType() != null ? aGeneralDesignation.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.genDesgIsActive.setChecked(true);
			this.genDesgIsActive.setDisabled(true);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aGeneralDesignation
	 */
	public void doWriteComponentsToBean(GeneralDesignation aGeneralDesignation) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aGeneralDesignation.setGenDesignation(this.genDesignation.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aGeneralDesignation.setGenDesgDesc(this.genDesgDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aGeneralDesignation.setGenDesgIsActive(this.genDesgIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		aGeneralDesignation.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aGeneralDesignation
	 * @throws Exception
	 */
	public void doShowDialog(GeneralDesignation aGeneralDesignation)
			throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aGeneralDesignation.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.genDesignation.focus();
		} else {
			if (isWorkFlowEnabled()){
				this.genDesgDesc.focus();
				if (StringUtils.isNotBlank(aGeneralDesignation.getRecordType())){
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aGeneralDesignation);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_GeneralDesignationDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.genDesignation.isReadonly()){
			this.genDesignation.setConstraint(new PTStringValidator(Labels.getLabel("label_GeneralDesignationDialog_GenDesignation.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHA, true));
		}	
		if (!this.genDesgDesc.isReadonly()){
			this.genDesgDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_GeneralDesignationDialog_GenDesgDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.genDesignation.setConstraint("");
		this.genDesgDesc.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.genDesignation.setErrorMessage("");
		this.genDesgDesc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getGeneralDesignationListCtrl().search();
	} 
	
	// CRUD operations

	/**
	 * Deletes a GeneralDesignation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final GeneralDesignation aGeneralDesignation = new GeneralDesignation();
		BeanUtils.copyProperties(getGeneralDesignation(), aGeneralDesignation);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_GeneralDesignationDialog_GenDesignation.value")+" : "+aGeneralDesignation.getGenDesignation();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aGeneralDesignation.getRecordType())){
				aGeneralDesignation.setVersion(aGeneralDesignation.getVersion()+1);
				aGeneralDesignation.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aGeneralDesignation.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aGeneralDesignation, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {

		logger.debug("Entering");
		if (getGeneralDesignation().isNewRecord()) {
			this.genDesignation.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.genDesignation.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.genDesgDesc.setReadonly(isReadOnly("GeneralDesignationDialog_genDesgDesc"));
		this.genDesgIsActive.setDisabled(isReadOnly("GeneralDesignationDialog_GenDesgIsActive"));
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.generalDesignation.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.genDesignation.setReadonly(true);
		this.genDesgDesc.setReadonly(true);
		this.genDesgIsActive.setDisabled(true);
		
		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		
		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before		
		this.genDesignation.setValue("");
		this.genDesgDesc.setValue("");
		this.genDesgIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {

		logger.debug("Entering");
		final GeneralDesignation aGeneralDesignation = new GeneralDesignation();
		BeanUtils.copyProperties(getGeneralDesignation(), aGeneralDesignation);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the GeneralDesignation object with the components data
		doWriteComponentsToBean(aGeneralDesignation);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aGeneralDesignation.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aGeneralDesignation.getRecordType())){
				aGeneralDesignation.setVersion(aGeneralDesignation.getVersion()+1);
				if(isNew){
					aGeneralDesignation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aGeneralDesignation.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aGeneralDesignation.setNewRecord(true);
				}
			}
		}else{
			aGeneralDesignation.setVersion(aGeneralDesignation.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			if (doProcess(aGeneralDesignation, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**	
	 * Set the workFlow Details List to Object
	 * 
	 * @param aGeneralDesignation (GeneralDesignation)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(GeneralDesignation aGeneralDesignation,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aGeneralDesignation.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getLoginUsrID());
		aGeneralDesignation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aGeneralDesignation.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aGeneralDesignation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aGeneralDesignation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aGeneralDesignation);
				}

				if (isNotesMandatory(taskId, aGeneralDesignation)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");
				
				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {
						
						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aGeneralDesignation.setTaskId(taskId);
			aGeneralDesignation.setNextTaskId(nextTaskId);
			aGeneralDesignation.setRoleCode(getRole());
			aGeneralDesignation.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aGeneralDesignation, tranType);
			
			String operationRefs = getServiceOperations(taskId, aGeneralDesignation);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aGeneralDesignation, PennantConstants
							.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aGeneralDesignation, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**	
	 * Get the result after processing DataBase Operations 
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		GeneralDesignation aGeneralDesignation = (GeneralDesignation) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getGeneralDesignationService().delete(auditHeader);

						deleteNotes=true;	
					}else{
						auditHeader = getGeneralDesignationService().saveOrUpdate(
								auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getGeneralDesignationService().doApprove(auditHeader);

						if(aGeneralDesignation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getGeneralDesignationService().doReject(auditHeader);
						if(aGeneralDesignation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_GeneralDesignationDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(
						this.window_GeneralDesignationDialog, auditHeader);
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					
					if(deleteNotes){
						deleteNotes(getNotes(this.generalDesignation),true);
					}
				}
				
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	
	// WorkFlow Components
	
	/**
	 * Get Audit Header Details
	 * @param aGeneralDesignation (GeneralDesignation)
	 * @param tranType (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(GeneralDesignation aGeneralDesignation,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aGeneralDesignation.getBefImage(), aGeneralDesignation);
		return new AuditHeader(String.valueOf(aGeneralDesignation.getId()),
				null, null, null, auditDetail,aGeneralDesignation.getUserDetails(),getOverideMap());
	}

	/**
	 * Method for Display Error Message
	 * @param e
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_GeneralDesignationDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.generalDesignation);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.generalDesignation.getGenDesignation());
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public GeneralDesignation getGeneralDesignation() {
		return this.generalDesignation;
	}
	public void setGeneralDesignation(GeneralDesignation generalDesignation) {
		this.generalDesignation = generalDesignation;
	}

	public void setGeneralDesignationService(
			GeneralDesignationService generalDesignationService) {
		this.generalDesignationService = generalDesignationService;
	}
	public GeneralDesignationService getGeneralDesignationService() {
		return this.generalDesignationService;
	}

	public void setGeneralDesignationListCtrl(
			GeneralDesignationListCtrl generalDesignationListCtrl) {
		this.generalDesignationListCtrl = generalDesignationListCtrl;
	}
	public GeneralDesignationListCtrl getGeneralDesignationListCtrl() {
		return this.generalDesignationListCtrl;
	}

}
