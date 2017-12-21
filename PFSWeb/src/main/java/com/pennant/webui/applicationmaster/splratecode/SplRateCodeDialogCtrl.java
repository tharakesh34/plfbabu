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
 * FileName    		:  SplRateCodeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.splratecode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
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
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.SplRateCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/SplRateCode/splRateCodeDialog.zul file.
 */
public class SplRateCodeDialogCtrl extends GFCBaseCtrl<SplRateCode> {
	private static final long serialVersionUID = -8184469529624754015L;
	private static final Logger logger = Logger.getLogger(SplRateCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_SplRateCodeDialog; 	// autowired
	protected Textbox 	sRType; 					// autowired
	protected Textbox 	sRTypeDesc; 				// autowired
	protected Checkbox 	sRIsActive; 				// autowired



	// not auto wired vars
	private 		  SplRateCode 		  splRateCode; 			// overHanded per param
	private transient SplRateCodeListCtrl splRateCodeListCtrl; 	// overHanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient SplRateCodeService splRateCodeService;

	/**
	 * default constructor.<br>
	 */
	public SplRateCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SplRateCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SplRateCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SplRateCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SplRateCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("splRateCode")) {
				this.splRateCode = (SplRateCode) arguments.get("splRateCode");
				SplRateCode befImage = new SplRateCode();
				BeanUtils.copyProperties(this.splRateCode, befImage);
				this.splRateCode.setBefImage(befImage);
				setSplRateCode(this.splRateCode);
			} else {
				setSplRateCode(null);
			}

			doLoadWorkFlow(this.splRateCode.isWorkflow(),
					this.splRateCode.getWorkflowId(),
					this.splRateCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"SplRateCodeDialog");
			}

			// READ OVERHANDED params !
			// we get the splRateCodeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete splRateCode here.
			if (arguments.containsKey("splRateCodeListCtrl")) {
				setSplRateCodeListCtrl((SplRateCodeListCtrl) arguments
						.get("splRateCodeListCtrl"));
			} else {
				setSplRateCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSplRateCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SplRateCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.sRType.setMaxlength(8);
		this.sRTypeDesc.setMaxlength(50);

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
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_SplRateCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_SplRateCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_SplRateCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_SplRateCodeDialog_btnSave"));
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
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_SplRateCodeDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
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
		doWriteBeanToComponents(this.splRateCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSplRateCode
	 *            SplRateCode
	 */
	public void doWriteBeanToComponents(SplRateCode aSplRateCode) {
		logger.debug("Entering");
		this.sRType.setValue(aSplRateCode.getSRType());
		this.sRTypeDesc.setValue(aSplRateCode.getSRTypeDesc());
		this.sRIsActive.setChecked(aSplRateCode.isSRIsActive());
		this.recordStatus.setValue(aSplRateCode.getRecordStatus());
		
		if(aSplRateCode.isNew() || (aSplRateCode.getRecordType() != null ? aSplRateCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.sRIsActive.setChecked(true);
			this.sRIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSplRateCode
	 */
	public void doWriteComponentsToBean(SplRateCode aSplRateCode) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSplRateCode.setSRType(this.sRType.getValue().toUpperCase());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSplRateCode.setSRTypeDesc(this.sRTypeDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSplRateCode.setSRIsActive(this.sRIsActive.isChecked());
		}catch (WrongValueException we ) {
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

		aSplRateCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSplRateCode
	 * @throws Exception
	 */
	public void doShowDialog(SplRateCode aSplRateCode) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSplRateCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.sRType.focus();
		} else {
			this.sRTypeDesc.focus();
			if (isWorkFlowEnabled()){
				if (StringUtils.isNotBlank(aSplRateCode.getRecordType())){
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
			doWriteBeanToComponents(aSplRateCode);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SplRateCodeDialog.onClose();
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

		if (!this.sRType.isReadonly()){
			this.sRType.setConstraint(new PTStringValidator(Labels.getLabel("label_SplRateCodeDialog_SRType.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	
		if (!this.sRTypeDesc.isReadonly()){
			this.sRTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SplRateCodeDialog_SRTypeDesc.value"), 
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
		this.sRType.setConstraint("");
		this.sRTypeDesc.setConstraint("");
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
		this.sRType.setErrorMessage("");
		this.sRTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getSplRateCodeListCtrl().search();
	} 

	// CRUD operations

	/**
	 * Deletes a SplRateCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SplRateCode aSplRateCode = new SplRateCode();
		BeanUtils.copyProperties(getSplRateCode(), aSplRateCode);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_SplRateCodeDialog_SRType.value")+" : "+aSplRateCode.getSRType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSplRateCode.getRecordType())){
				aSplRateCode.setVersion(aSplRateCode.getVersion()+1);
				aSplRateCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aSplRateCode.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aSplRateCode,tranType)){
					refreshList();
					closeDialog(); 
				}
			}catch (DataAccessException e){
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
		if (getSplRateCode().isNewRecord()){
			this.sRType.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.sRType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.sRTypeDesc.setReadonly(isReadOnly("SplRateCodeDialog_sRTypeDesc"));
		this.sRIsActive.setDisabled(isReadOnly("SplRateCodeDialog_sRIsActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.splRateCode.isNewRecord()){
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
		this.sRType.setReadonly(true);
		this.sRTypeDesc.setReadonly(true);
		this.sRIsActive.setDisabled(true);

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
		this.sRType.setValue("");
		this.sRTypeDesc.setValue("");
		this.sRIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SplRateCode aSplRateCode = new SplRateCode();
		BeanUtils.copyProperties(getSplRateCode(), aSplRateCode);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SplRateCode object with the components data
		doWriteComponentsToBean(aSplRateCode);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aSplRateCode.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSplRateCode.getRecordType())){
				aSplRateCode.setVersion(aSplRateCode.getVersion()+1);
				if(isNew){
					aSplRateCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aSplRateCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSplRateCode.setNewRecord(true);
				}
			}
		}else{
			aSplRateCode.setVersion(aSplRateCode.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aSplRateCode,tranType)){
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
	 * @param aSplRateCode (SplRateCode)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(SplRateCode aSplRateCode,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aSplRateCode.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getUserId());
		aSplRateCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSplRateCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSplRateCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSplRateCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSplRateCode);
				}

				if (isNotesMandatory(taskId, aSplRateCode)) {
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

			aSplRateCode.setTaskId(taskId);
			aSplRateCode.setNextTaskId(nextTaskId);
			aSplRateCode.setRoleCode(getRole());
			aSplRateCode.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aSplRateCode, tranType);
			String operationRefs = getServiceOperations(taskId, aSplRateCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSplRateCode,
							PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aSplRateCode, tranType);
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
		SplRateCode aSplRateCode = (SplRateCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getSplRateCodeService().delete(auditHeader);

						deleteNotes=true;
					} else {
						auditHeader = getSplRateCodeService().saveOrUpdate(
								auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getSplRateCodeService().doApprove(auditHeader);

						if(aSplRateCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSplRateCodeService().doReject(
								auditHeader);
						if(aSplRateCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_SplRateCodeDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_SplRateCodeDialog, auditHeader);

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					
					if(deleteNotes){
						deleteNotes(getNotes(this.splRateCode),true);
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
	
	// WorkFlow Details
	
	/**
	 * Get Audit Header Details
	 * @param aSplRateCode (SplRateCode)
	 * @param tranType (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(SplRateCode aSplRateCode, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSplRateCode.getBefImage(), aSplRateCode);
		return new AuditHeader(String.valueOf(aSplRateCode.getId()),
				null, null, null, auditDetail,aSplRateCode.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_SplRateCodeDialog,
					auditHeader);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes(this.splRateCode));
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.splRateCode.getSRType());
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

	public SplRateCode getSplRateCode() {
		return this.splRateCode;
	}
	public void setSplRateCode(SplRateCode splRateCode) {
		this.splRateCode = splRateCode;
	}

	public void setSplRateCodeService(SplRateCodeService splRateCodeService) {
		this.splRateCodeService = splRateCodeService;
	}
	public SplRateCodeService getSplRateCodeService() {
		return this.splRateCodeService;
	}

	public void setSplRateCodeListCtrl(SplRateCodeListCtrl splRateCodeListCtrl) {
		this.splRateCodeListCtrl = splRateCodeListCtrl;
	}
	public SplRateCodeListCtrl getSplRateCodeListCtrl() {
		return this.splRateCodeListCtrl;
	}

}
