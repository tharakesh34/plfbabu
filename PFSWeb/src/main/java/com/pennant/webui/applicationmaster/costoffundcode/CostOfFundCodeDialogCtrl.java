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
 * FileName    		:  CostOfFundCodeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmaster.costoffundcode;

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

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.CostOfFundCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.CostOfFundCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CostOfFundCode/costOfFundCodeDialog.zul file.
 */
public class CostOfFundCodeDialogCtrl extends GFCBaseCtrl<CostOfFundCode> {
	private static final long serialVersionUID = 190631304555025244L;
	private static final Logger logger = Logger.getLogger(CostOfFundCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CostOfFundCodeDialog; // autoWired

	protected Textbox cofCode; // autoWired
	protected Textbox cofDesc; // autoWired
	protected Checkbox active; // autoWired

	// not autoWired Var's
	private CostOfFundCode costOfFundCode; 							 // overHanded per parameter
	private transient CostOfFundCodeListCtrl costOfFundCodeListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient CostOfFundCodeService costOfFundCodeService;

	/**
	 * default constructor.<br>
	 */
	public CostOfFundCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CostOfFundCodesDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CostOfFundCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CostOfFundCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CostOfFundCodeDialog);

		try {
			
			// READ OVERHANDED parameters !
			if (arguments.containsKey("costOfFundCode")) {
				this.costOfFundCode = (CostOfFundCode) arguments.get("costOfFundCode");
				CostOfFundCode befImage = new CostOfFundCode();
				BeanUtils.copyProperties(this.costOfFundCode, befImage);
				this.costOfFundCode.setBefImage(befImage);

				setCostOfFundCode(this.costOfFundCode);
			} else {
				setCostOfFundCode(null);
			}

			doLoadWorkFlow(this.costOfFundCode.isWorkflow(),
					this.costOfFundCode.getWorkflowId(),
					this.costOfFundCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the costOfFundCodeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete costOfFundCode here.
			if (arguments.containsKey("costOfFundCodeListCtrl")) {
				setCostOfFundCodeListCtrl((CostOfFundCodeListCtrl) arguments
						.get("costOfFundCodeListCtrl"));
			} else {
				setCostOfFundCodeListCtrl(null);
			}
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCostOfFundCode());
		} catch (Exception e) {
			com.pennanttech.pennapps.web.util.MessageUtil.showError(e);
			this.window_CostOfFundCodeDialog.onClose();
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.cofCode.setMaxlength(8);
		this.cofDesc.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CostOfFundCodesDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CostOfFundCodesDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CostOfFundCodesDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CostOfFundCodesDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CostOfFundCodeDialog);
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
		doWriteBeanToComponents(this.costOfFundCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCostOfFundCode
	 *            CostOfFundCode
	 */
	public void doWriteBeanToComponents(CostOfFundCode aCostOfFundCode) {
		logger.debug("Entering");
		this.cofCode.setValue(aCostOfFundCode.getCofCode());
		this.cofDesc.setValue(aCostOfFundCode.getCofDesc());
		this.recordStatus.setValue(aCostOfFundCode.getRecordStatus());
		this.active.setChecked(aCostOfFundCode.isActive());
		if(aCostOfFundCode.isNew() || (aCostOfFundCode.getRecordType() != null ? aCostOfFundCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCostOfFundCode
	 */
	public void doWriteComponentsToBean(CostOfFundCode aCostOfFundCode) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCostOfFundCode.setCofCode(this.cofCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCostOfFundCode.setCofDesc(this.cofDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCostOfFundCode.setActive(this.active.isChecked());
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

		aCostOfFundCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCostOfFundCode
	 * @throws Exception
	 */
	public void doShowDialog(CostOfFundCode aCostOfFundCode) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aCostOfFundCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cofCode.focus();
		} else {
			if (isWorkFlowEnabled()){
				this.cofDesc.focus();
				if (StringUtils.isNotBlank(aCostOfFundCode.getRecordType())){
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
			doWriteBeanToComponents(aCostOfFundCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CostOfFundCodeDialog.onClose();
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

		if (!this.cofCode.isReadonly()){
			this.cofCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CostOfFundCodeDialog_CofCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}	
		if (!this.cofDesc.isReadonly()){
			this.cofDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CostOfFundCodeDialog_CofDesc.value"), 
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
		this.cofCode.setConstraint("");
		this.cofDesc.setConstraint("");
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
		this.cofCode.setErrorMessage("");
		this.cofDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getCostOfFundCodeListCtrl().search();
	} 

	// CRUD operations

	/**
	 * Deletes a CostOfFundCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final CostOfFundCode aCostOfFundCode = new CostOfFundCode();
		BeanUtils.copyProperties(getCostOfFundCode(), aCostOfFundCode);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "+ 
				Labels.getLabel("label_CostOfFundCodeDialog_CofCode.value")+" : "+aCostOfFundCode.getCofCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCostOfFundCode.getRecordType())){
				aCostOfFundCode.setVersion(aCostOfFundCode.getVersion()+1);
				aCostOfFundCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCostOfFundCode.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCostOfFundCode,tranType)){
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
		if (getCostOfFundCode().isNewRecord()){
			this.cofCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.cofCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.cofDesc.setReadonly(isReadOnly("CostOfFundCodesDialog_cofDesc"));
		this.active.setDisabled(isReadOnly("CostOfFundCodesDialog_active"));
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.costOfFundCode.isNewRecord()){
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
		this.cofCode.setReadonly(true);
		this.cofDesc.setReadonly(true);
		this.active.setDisabled(true);
		
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
		this.cofCode.setValue("");
		this.cofDesc.setValue("");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CostOfFundCode aCostOfFundCode = new CostOfFundCode();
		BeanUtils.copyProperties(getCostOfFundCode(), aCostOfFundCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CostOfFundCode object with the components data
		doWriteComponentsToBean(aCostOfFundCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCostOfFundCode.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCostOfFundCode.getRecordType())){
				aCostOfFundCode.setVersion(aCostOfFundCode.getVersion()+1);
				if(isNew){
					aCostOfFundCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCostOfFundCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCostOfFundCode.setNewRecord(true);
				}
			}
		}else{
			aCostOfFundCode.setVersion(aCostOfFundCode.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCostOfFundCode,tranType)){
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
	 * @param aCostOfFundCode (CostOfFundCode)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CostOfFundCode aCostOfFundCode,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCostOfFundCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCostOfFundCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCostOfFundCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCostOfFundCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCostOfFundCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCostOfFundCode);
				}

				if (isNotesMandatory(taskId, aCostOfFundCode)) {
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

			aCostOfFundCode.setTaskId(taskId);
			aCostOfFundCode.setNextTaskId(nextTaskId);
			aCostOfFundCode.setRoleCode(getRole());
			aCostOfFundCode.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCostOfFundCode, tranType);

			String operationRefs = getServiceOperations(taskId, aCostOfFundCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCostOfFundCode, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCostOfFundCode, tranType);
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
		CostOfFundCode aCostOfFundCode = (CostOfFundCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCostOfFundCodeService().delete(auditHeader);

						if(aCostOfFundCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else{
						auditHeader = getCostOfFundCodeService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getCostOfFundCodeService().doApprove(auditHeader);
						if(aCostOfFundCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes =true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getCostOfFundCodeService().doReject(auditHeader);
						if(aCostOfFundCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_CostOfFundCodeDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_CostOfFundCodeDialog,auditHeader);

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
	 * @param aCostOfFundCode 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CostOfFundCode aCostOfFundCode, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCostOfFundCode.getBefImage(), aCostOfFundCode);   
		return new AuditHeader(String.valueOf(aCostOfFundCode.getId()),null,null,null,auditDetail,aCostOfFundCode.getUserDetails(),getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CostOfFundCodeDialog, auditHeader);
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
		notes.setModuleName("CostOfFundCode");
		notes.setReference(getCostOfFundCode().getCofCode());
		notes.setVersion(getCostOfFundCode().getVersion());
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

	public CostOfFundCode getCostOfFundCode() {
		return this.costOfFundCode;
	}
	public void setCostOfFundCode(CostOfFundCode costOfFundCode) {
		this.costOfFundCode = costOfFundCode;
	}

	public void setCostOfFundCodeService(CostOfFundCodeService costOfFundCodeService) {
		this.costOfFundCodeService = costOfFundCodeService;
	}
	public CostOfFundCodeService getCostOfFundCodeService() {
		return this.costOfFundCodeService;
	}

	public void setCostOfFundCodeListCtrl(CostOfFundCodeListCtrl costOfFundCodeListCtrl) {
		this.costOfFundCodeListCtrl = costOfFundCodeListCtrl;
	}
	public CostOfFundCodeListCtrl getCostOfFundCodeListCtrl() {
		return this.costOfFundCodeListCtrl;
	}

}
