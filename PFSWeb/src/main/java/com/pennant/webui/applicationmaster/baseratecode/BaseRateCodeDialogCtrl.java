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
 * FileName    		:  BaseRateCodeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.baseratecode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.BaseRateCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/BaseRateCode/baseRateCodeDialog.zul file.
 */
public class BaseRateCodeDialogCtrl extends GFCBaseCtrl<BaseRateCode> {
	private static final long serialVersionUID = 190631304555025244L;
	private static final Logger logger = Logger.getLogger(BaseRateCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BaseRateCodeDialog; // autoWired

	protected Textbox bRType; // autoWired
	protected Textbox bRTypeDesc; // autoWired
	protected Checkbox bRTypeIsActive; // autoWired

	// not autoWired Var's
	private BaseRateCode baseRateCode; 							 // overHanded per parameter
	private transient BaseRateCodeListCtrl baseRateCodeListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient BaseRateCodeService baseRateCodeService;

	/**
	 * default constructor.<br>
	 */
	public BaseRateCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BaseRateCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected BaseRateCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BaseRateCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BaseRateCodeDialog);

		try {
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("baseRateCode")) {
				this.baseRateCode = (BaseRateCode) arguments.get("baseRateCode");
				BaseRateCode befImage = new BaseRateCode();
				BeanUtils.copyProperties(this.baseRateCode, befImage);
				this.baseRateCode.setBefImage(befImage);

				setBaseRateCode(this.baseRateCode);
			} else {
				setBaseRateCode(null);
			}

			doLoadWorkFlow(this.baseRateCode.isWorkflow(),
					this.baseRateCode.getWorkflowId(),
					this.baseRateCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"BaseRateCodeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the baseRateCodeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete baseRateCode here.
			if (arguments.containsKey("baseRateCodeListCtrl")) {
				setBaseRateCodeListCtrl((BaseRateCodeListCtrl) arguments
						.get("baseRateCodeListCtrl"));
			} else {
				setBaseRateCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getBaseRateCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BaseRateCodeDialog.onClose();
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.bRType.setMaxlength(8);
		this.bRTypeDesc.setMaxlength(50);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BaseRateCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BaseRateCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BaseRateCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BaseRateCodeDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_BaseRateCodeDialog);
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
		doWriteBeanToComponents(this.baseRateCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBaseRateCode
	 *            BaseRateCode
	 */
	public void doWriteBeanToComponents(BaseRateCode aBaseRateCode) {
		logger.debug("Entering");
		this.bRType.setValue(aBaseRateCode.getBRType());
		this.bRTypeDesc.setValue(aBaseRateCode.getBRTypeDesc());
		this.recordStatus.setValue(aBaseRateCode.getRecordStatus());
		this.bRTypeIsActive.setChecked(aBaseRateCode.isbRTypeIsActive());
		if(aBaseRateCode.isNew() || (aBaseRateCode.getRecordType() != null ? aBaseRateCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.bRTypeIsActive.setChecked(true);
			this.bRTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBaseRateCode
	 */
	public void doWriteComponentsToBean(BaseRateCode aBaseRateCode) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aBaseRateCode.setBRType(this.bRType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aBaseRateCode.setBRTypeDesc(this.bRTypeDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aBaseRateCode.setbRTypeIsActive(this.bRTypeIsActive.isChecked());
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

		aBaseRateCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBaseRateCode
	 * @throws Exception
	 */
	public void doShowDialog(BaseRateCode aBaseRateCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aBaseRateCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bRType.focus();
		} else {
			if (isWorkFlowEnabled()){
				this.bRTypeDesc.focus();
				if (StringUtils.isNotBlank(aBaseRateCode.getRecordType())){
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
			doWriteBeanToComponents(aBaseRateCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BaseRateCodeDialog.onClose();
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

		if (!this.bRType.isReadonly()){
			this.bRType.setConstraint(new PTStringValidator(Labels.getLabel("label_BaseRateCodeDialog_BRType.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	
		if (!this.bRTypeDesc.isReadonly()){
			this.bRTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_BaseRateCodeDialog_BRTypeDesc.value"), 
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
		this.bRType.setConstraint("");
		this.bRTypeDesc.setConstraint("");
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
		this.bRType.setErrorMessage("");
		this.bRTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getBaseRateCodeListCtrl().search();
	} 

	// CRUD operations

	/**
	 * Deletes a BaseRateCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final BaseRateCode aBaseRateCode = new BaseRateCode();
		BeanUtils.copyProperties(getBaseRateCode(), aBaseRateCode);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "+ 
				Labels.getLabel("label_BaseRateCodeDialog_BRType.value")+" : "+aBaseRateCode.getBRType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aBaseRateCode.getRecordType())){
				aBaseRateCode.setVersion(aBaseRateCode.getVersion()+1);
				aBaseRateCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aBaseRateCode.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aBaseRateCode,tranType)){
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
		if (getBaseRateCode().isNewRecord()){
			this.bRType.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.bRType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.bRTypeDesc.setReadonly(isReadOnly("BaseRateCodeDialog_bRTypeDesc"));
		this.bRTypeIsActive.setDisabled(isReadOnly("BaseRateCodeDialog_BRTypeIsActive"));
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.baseRateCode.isNewRecord()){
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
		this.bRType.setReadonly(true);
		this.bRTypeDesc.setReadonly(true);
		this.bRTypeIsActive.setDisabled(true);
		
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
		this.bRType.setValue("");
		this.bRTypeDesc.setValue("");
		this.bRTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final BaseRateCode aBaseRateCode = new BaseRateCode();
		BeanUtils.copyProperties(getBaseRateCode(), aBaseRateCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the BaseRateCode object with the components data
		doWriteComponentsToBean(aBaseRateCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBaseRateCode.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBaseRateCode.getRecordType())){
				aBaseRateCode.setVersion(aBaseRateCode.getVersion()+1);
				if(isNew){
					aBaseRateCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aBaseRateCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBaseRateCode.setNewRecord(true);
				}
			}
		}else{
			aBaseRateCode.setVersion(aBaseRateCode.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aBaseRateCode,tranType)){
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
	 * @param aBaseRateCode (BaseRateCode)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(BaseRateCode aBaseRateCode,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aBaseRateCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aBaseRateCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBaseRateCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBaseRateCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBaseRateCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBaseRateCode);
				}

				if (isNotesMandatory(taskId, aBaseRateCode)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aBaseRateCode.setTaskId(taskId);
			aBaseRateCode.setNextTaskId(nextTaskId);
			aBaseRateCode.setRoleCode(getRole());
			aBaseRateCode.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aBaseRateCode, tranType);

			String operationRefs = getServiceOperations(taskId, aBaseRateCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aBaseRateCode, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aBaseRateCode, tranType);
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
		BaseRateCode aBaseRateCode = (BaseRateCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getBaseRateCodeService().delete(auditHeader);

						if(aBaseRateCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else{
						auditHeader = getBaseRateCodeService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getBaseRateCodeService().doApprove(auditHeader);
						if(aBaseRateCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes =true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getBaseRateCodeService().doReject(auditHeader);
						if(aBaseRateCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_BaseRateCodeDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_BaseRateCodeDialog,auditHeader);

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
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
	 * @param aBaseRateCode 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(BaseRateCode aBaseRateCode, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBaseRateCode.getBefImage(), aBaseRateCode);   
		return new AuditHeader(String.valueOf(aBaseRateCode.getId()),null,null,null,auditDetail,aBaseRateCode.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_BaseRateCodeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 *  Get the window for entering Notes
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */	
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("BaseRateCode");
		notes.setReference(getBaseRateCode().getBRType());
		notes.setVersion(getBaseRateCode().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public BaseRateCode getBaseRateCode() {
		return this.baseRateCode;
	}
	public void setBaseRateCode(BaseRateCode baseRateCode) {
		this.baseRateCode = baseRateCode;
	}

	public void setBaseRateCodeService(BaseRateCodeService baseRateCodeService) {
		this.baseRateCodeService = baseRateCodeService;
	}
	public BaseRateCodeService getBaseRateCodeService() {
		return this.baseRateCodeService;
	}

	public void setBaseRateCodeListCtrl(BaseRateCodeListCtrl baseRateCodeListCtrl) {
		this.baseRateCodeListCtrl = baseRateCodeListCtrl;
	}
	public BaseRateCodeListCtrl getBaseRateCodeListCtrl() {
		return this.baseRateCodeListCtrl;
	}

}
