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
 * FileName    		:  GeneralDepartmentDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.generaldepartment;

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

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.service.systemmasters.GeneralDepartmentService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/GeneralDepartment/generalDepartmentDialog.zul file.
 */
public class GeneralDepartmentDialogCtrl extends GFCBaseCtrl<GeneralDepartment> {
	private static final long serialVersionUID = 4896504189951469996L;
	private static final Logger logger = Logger.getLogger(GeneralDepartmentDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  window_GeneralDepartmentDialog; 	

	protected Uppercasebox	 genDepartment; 					
	protected Textbox 	 genDeptDesc; 	
	protected Checkbox genDeptIsActive; // autoWired
	
	// not auto wired Var's
	private GeneralDepartment generalDepartment; // overHanded per param
	private transient GeneralDepartmentListCtrl generalDepartmentListCtrl; // overHanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient GeneralDepartmentService generalDepartmentService;

	/**
	 * default constructor.<br>
	 */
	public GeneralDepartmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "GeneralDepartmentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected GeneralDepartment object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GeneralDepartmentDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_GeneralDepartmentDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("generalDepartment")) {
				this.generalDepartment = (GeneralDepartment) arguments
						.get("generalDepartment");
				GeneralDepartment befImage = new GeneralDepartment();
				BeanUtils.copyProperties(this.generalDepartment, befImage);
				this.generalDepartment.setBefImage(befImage);

				setGeneralDepartment(this.generalDepartment);
			} else {
				setGeneralDepartment(null);
			}

			doLoadWorkFlow(this.generalDepartment.isWorkflow(),
					this.generalDepartment.getWorkflowId(),
					this.generalDepartment.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"GeneralDepartmentDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED params !
			// we get the generalDepartmentListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete generalDepartment here.
			if (arguments.containsKey("generalDepartmentListCtrl")) {
				setGeneralDepartmentListCtrl((GeneralDepartmentListCtrl) arguments
						.get("generalDepartmentListCtrl"));
			} else {
				setGeneralDepartmentListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getGeneralDepartment());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_GeneralDepartmentDialog.onClose();
		}

		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.genDepartment.setMaxlength(8);
		this.genDeptDesc.setMaxlength(50);
		
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
				"button_GeneralDepartmentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDepartmentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDepartmentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_GeneralDepartmentDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_GeneralDepartmentDialog);
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
		doWriteBeanToComponents(this.generalDepartment.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aGeneralDepartment
	 *            GeneralDepartment
	 */
	public void doWriteBeanToComponents(GeneralDepartment aGeneralDepartment) {
		logger.debug("Entering");
		this.genDepartment.setValue(aGeneralDepartment.getGenDepartment());
		this.genDeptDesc.setValue(aGeneralDepartment.getGenDeptDesc());
		this.recordStatus.setValue(aGeneralDepartment.getRecordStatus());
		this.genDeptIsActive.setChecked(aGeneralDepartment.isGenDeptIsActive());
		if(aGeneralDepartment.isNew() || (aGeneralDepartment.getRecordType() != null ? aGeneralDepartment.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.genDeptIsActive.setChecked(true);
			this.genDeptIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aGeneralDepartment
	 */
	public void doWriteComponentsToBean(GeneralDepartment aGeneralDepartment) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			aGeneralDepartment.setGenDepartment(this.genDepartment.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aGeneralDepartment.setGenDeptDesc(this.genDeptDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aGeneralDepartment.setGenDeptIsActive(this.genDeptIsActive.isChecked());
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
		
		aGeneralDepartment.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aGeneralDepartment
	 * @throws Exception
	 */
	public void doShowDialog(GeneralDepartment aGeneralDepartment) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aGeneralDepartment.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.genDepartment.focus();
		} else {
			if (isWorkFlowEnabled()){
				this.genDeptDesc.focus();
				if (StringUtils.isNotBlank(aGeneralDepartment.getRecordType())){
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
			doWriteBeanToComponents(aGeneralDepartment);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_GeneralDepartmentDialog.onClose();
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
		
		if (!this.genDepartment.isReadonly()){
			this.genDepartment.setConstraint(new PTStringValidator(Labels.getLabel("label_GeneralDepartmentDialog_GenDepartment.value"), 
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHA, true));
		}	
		if (!this.genDeptDesc.isReadonly()){
			this.genDeptDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_GeneralDepartmentDialog_GenDeptDesc.value"), 
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
		this.genDepartment.setConstraint("");
		this.genDeptDesc.setConstraint("");
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
		this.genDepartment.setErrorMessage("");
		this.genDeptDesc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getGeneralDepartmentListCtrl().search();
	} 

	// CRUD operations

	/**
	 * Deletes a GeneralDepartment object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final GeneralDepartment aGeneralDepartment = new GeneralDepartment();
		BeanUtils.copyProperties(getGeneralDepartment(), aGeneralDepartment);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_GeneralDepartmentDialog_GenDepartment.value")+" : "+aGeneralDepartment.getGenDepartment();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aGeneralDepartment.getRecordType())){
				aGeneralDepartment.setVersion(aGeneralDepartment.getVersion()+1);
				aGeneralDepartment.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aGeneralDepartment.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aGeneralDepartment,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getGeneralDepartment().isNewRecord()){
			this.genDepartment.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.genDepartment.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
			this.genDeptDesc.setReadonly(isReadOnly("GeneralDepartmentDialog_genDeptDesc"));
			this.genDeptIsActive.setDisabled(isReadOnly("GeneralDepartmentDialog_GenDeptIsActive"));
			if (isWorkFlowEnabled()){
				for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.generalDepartment.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
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
		this.genDepartment.setReadonly(true);
		this.genDeptDesc.setReadonly(true);
		this.genDeptIsActive.setDisabled(true);
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
		this.genDepartment.setValue("");
		this.genDeptDesc.setValue("");
		this.genDeptIsActive.setChecked(false);
		logger.debug("Leaving");
		
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		
		logger.debug("Entering");
		final GeneralDepartment aGeneralDepartment = new GeneralDepartment();
		BeanUtils.copyProperties(getGeneralDepartment(), aGeneralDepartment);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the GeneralDepartment object with the components data
		doWriteComponentsToBean(aGeneralDepartment);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aGeneralDepartment.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aGeneralDepartment.getRecordType())){
				aGeneralDepartment.setVersion(aGeneralDepartment.getVersion()+1);
				if(isNew){
					aGeneralDepartment.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aGeneralDepartment.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aGeneralDepartment.setNewRecord(true);
				}
			}
		}else{
			aGeneralDepartment.setVersion(aGeneralDepartment.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			if(doProcess(aGeneralDepartment,tranType)){
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
	 * @param aGeneralDepartment (GeneralDepartment)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(GeneralDepartment aGeneralDepartment,String tranType){
		
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aGeneralDepartment.setLastMntBy(getUserWorkspace()
				.getLoggedInUser().getUserId());
		aGeneralDepartment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aGeneralDepartment.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aGeneralDepartment.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aGeneralDepartment.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aGeneralDepartment);
				}

				if (isNotesMandatory(taskId, aGeneralDepartment)) {
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

			aGeneralDepartment.setTaskId(taskId);
			aGeneralDepartment.setNextTaskId(nextTaskId);
			aGeneralDepartment.setRoleCode(getRole());
			aGeneralDepartment.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aGeneralDepartment, tranType);
			
			String operationRefs = getServiceOperations(taskId, aGeneralDepartment);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aGeneralDepartment,
							PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aGeneralDepartment, tranType);
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
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		GeneralDepartment aGeneralDepartment = (GeneralDepartment) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getGeneralDepartmentService().delete(auditHeader);

						deleteNotes=true;
					}else{
						auditHeader = getGeneralDepartmentService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getGeneralDepartmentService().doApprove(auditHeader);

						if(aGeneralDepartment.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getGeneralDepartmentService().doReject(auditHeader);
						if(aGeneralDepartment.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_GeneralDepartmentDialog,auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(
						this.window_GeneralDepartmentDialog, auditHeader);
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					
					if(deleteNotes){
						deleteNotes(getNotes(this.generalDepartment),true);
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
	 * @param aGeneralDepartment (GeneralDepartment)
	 * @param tranType (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(GeneralDepartment aGeneralDepartment,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aGeneralDepartment.getBefImage(), aGeneralDepartment);
		return new AuditHeader(String.valueOf(aGeneralDepartment.getId()),
				null, null, null, auditDetail,
				aGeneralDepartment.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_GeneralDepartmentDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.generalDepartment);
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.generalDepartment.getGenDepartment());
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

	public GeneralDepartment getGeneralDepartment() {
		return this.generalDepartment;
	}
	public void setGeneralDepartment(GeneralDepartment generalDepartment) {
		this.generalDepartment = generalDepartment;
	}

	public void setGeneralDepartmentService(
			GeneralDepartmentService generalDepartmentService) {
		this.generalDepartmentService = generalDepartmentService;
	}
	public GeneralDepartmentService getGeneralDepartmentService() {
		return this.generalDepartmentService;
	}

	public void setGeneralDepartmentListCtrl(
			GeneralDepartmentListCtrl generalDepartmentListCtrl) {
		this.generalDepartmentListCtrl = generalDepartmentListCtrl;
	}
	public GeneralDepartmentListCtrl getGeneralDepartmentListCtrl() {
		return this.generalDepartmentListCtrl;
	}


}
