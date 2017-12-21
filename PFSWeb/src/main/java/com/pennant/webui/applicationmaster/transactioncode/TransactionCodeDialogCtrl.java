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
 * FileName    		:  TransactionCodeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.transactioncode;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/TransactionCode/transactionCodeDialog.zul
 * file.
 */
public class TransactionCodeDialogCtrl extends GFCBaseCtrl<TransactionCode> {
	private static final long serialVersionUID = -5775295643429759088L;
	private static final Logger logger = Logger.getLogger(TransactionCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_TransactionCodeDialog; 	// autoWired
	protected Textbox 		tranCode; 						// autoWired
	protected Textbox 		tranDesc; 						// autoWired
 	protected Combobox 		tranType; 						// autoWired
	protected Checkbox 		tranIsActive; 					// autoWired


	// not auto wired variables
	private TransactionCode transactionCode; // overHanded per parameter
	private transient TransactionCodeListCtrl transactionCodeListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient TransactionCodeService transactionCodeService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	/**
	 * default constructor.<br>
	 */
	public TransactionCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TransactionCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected TransactionCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TransactionCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_TransactionCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("transactionCode")) {
				this.transactionCode = (TransactionCode) arguments
						.get("transactionCode");
				TransactionCode befImage = new TransactionCode();
				BeanUtils.copyProperties(this.transactionCode, befImage);
				this.transactionCode.setBefImage(befImage);

				setTransactionCode(this.transactionCode);
			} else {
				setTransactionCode(null);
			}

			doLoadWorkFlow(this.transactionCode.isWorkflow(),
					this.transactionCode.getWorkflowId(),
					this.transactionCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"TransactionCodeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the transactionCodeListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete transactionCode here.
			if (arguments.containsKey("transactionCodeListCtrl")) {
				setTransactionCodeListCtrl((TransactionCodeListCtrl) arguments
						.get("transactionCodeListCtrl"));
			} else {
				setTransactionCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getTransactionCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_TransactionCodeDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.tranCode.setMaxlength(8);
		this.tranDesc.setMaxlength(50);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TransactionCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TransactionCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TransactionCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TransactionCodeDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving") ;
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
		MessageUtil.showHelpWindow(event, window_TransactionCodeDialog);
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
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.transactionCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aTransactionCode
	 *            TransactionCode
	 */
	public void doWriteBeanToComponents(TransactionCode aTransactionCode) {
		logger.debug("Entering") ;
		this.tranCode.setValue(aTransactionCode.getTranCode());
		this.tranDesc.setValue(aTransactionCode.getTranDesc());
		fillComboBox(this.tranType,aTransactionCode.getTranType(),PennantStaticListUtil.getTranTypeBoth(),"");
		this.tranIsActive.setChecked(aTransactionCode.isTranIsActive());
		this.recordStatus.setValue(aTransactionCode.getRecordStatus());
		
		if(aTransactionCode.isNew() || (aTransactionCode.getRecordType() != null ? aTransactionCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.tranIsActive.setChecked(true);
			this.tranIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTransactionCode
	 */
	public void doWriteComponentsToBean(TransactionCode aTransactionCode) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aTransactionCode.setTranCode(this.tranCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aTransactionCode.setTranDesc(this.tranDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(!this.tranType.isDisabled() && this.tranType.getSelectedIndex()<0){
				throw new WrongValueException(tranType, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_TransactionCodeDialog_TranType.value")}));
			}
		    aTransactionCode.setTranType(this.tranType.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aTransactionCode.setTranIsActive(this.tranIsActive.isChecked());
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
		
		aTransactionCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aTransactionCode
	 * @throws Exception
	 */
	public void doShowDialog(TransactionCode aTransactionCode) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aTransactionCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.tranCode.focus();
		} else {
			this.tranDesc.focus();
			if (isWorkFlowEnabled()){
				if (StringUtils.isNotBlank(aTransactionCode.getRecordType())) {
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
			doWriteBeanToComponents(aTransactionCode);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_TransactionCodeDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.tranCode.isReadonly()){
			this.tranCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionCodeDialog_TranCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.tranDesc.isReadonly()){
			this.tranDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_TransactionCodeDialog_TranDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.tranType.isDisabled()){
			this.tranType.setConstraint(new StaticListValidator(PennantStaticListUtil.getTranTypeBoth(),
					Labels.getLabel("label_TransactionCodeDialog_TranType.value")));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.tranCode.setConstraint("");
		this.tranDesc.setConstraint("");
		this.tranType.setConstraint("");
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
		logger.debug("Entering");
		this.tranCode.setErrorMessage("");
		this.tranDesc.setErrorMessage("");
		this.tranType.setErrorMessage("");
		logger.debug("Leaving");
	}	

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getTransactionCodeListCtrl().search();
	} 

	// CRUD operations

	/**
	 * Deletes a TransactionCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		
		final TransactionCode aTransactionCode = new TransactionCode();
		BeanUtils.copyProperties(getTransactionCode(), aTransactionCode);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_TransactionCodeDialog_TranCode.value")+" : "+aTransactionCode.getTranCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aTransactionCode.getRecordType())){
				aTransactionCode.setVersion(aTransactionCode.getVersion()+1);
				aTransactionCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aTransactionCode.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aTransactionCode,tranType)){
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

		if (getTransactionCode().isNewRecord()){
			this.tranCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.tranCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.tranDesc.setReadonly(isReadOnly("TransactionCodeDialog_tranDesc"));
		this.tranType.setDisabled(isReadOnly("TransactionCodeDialog_tranType"));
		this.tranIsActive.setDisabled(isReadOnly("TransactionCodeDialog_tranIsActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.transactionCode.isNewRecord()){
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
		this.tranCode.setReadonly(true);
		this.tranDesc.setReadonly(true);
		this.tranType.setDisabled(true);
		this.tranIsActive.setDisabled(true);
		
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
		this.tranCode.setValue("");
		this.tranDesc.setValue("");
		//this.tranType.setValue("");
		this.tranIsActive.setChecked(false);
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final TransactionCode aTransactionCode = new TransactionCode();
		BeanUtils.copyProperties(getTransactionCode(), aTransactionCode);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the TransactionCode object with the components data
		doWriteComponentsToBean(aTransactionCode);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aTransactionCode.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aTransactionCode.getRecordType())){
				aTransactionCode.setVersion(aTransactionCode.getVersion()+1);
				if(isNew){
					aTransactionCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aTransactionCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTransactionCode.setNewRecord(true);
				}
			}
		}else{
			aTransactionCode.setVersion(aTransactionCode.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			if(doProcess(aTransactionCode,tranType)){
				doWriteBeanToComponents(aTransactionCode);
				refreshList();
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
	 * @param aTransactionCode
	 *            (TransactionCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 */
	private boolean doProcess(TransactionCode aTransactionCode,String tranType){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aTransactionCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aTransactionCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTransactionCode.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aTransactionCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aTransactionCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aTransactionCode);
				}

				if (isNotesMandatory(taskId, aTransactionCode)) {
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

			aTransactionCode.setTaskId(taskId);
			aTransactionCode.setNextTaskId(nextTaskId);
			aTransactionCode.setRoleCode(getRole());
			aTransactionCode.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aTransactionCode, tranType);
			
			String operationRefs = getServiceOperations(taskId, aTransactionCode);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aTransactionCode, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aTransactionCode, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		
		TransactionCode aTransactionCode = (TransactionCode) auditHeader.
													getAuditDetail().getModelData();
		try {
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getTransactionCodeService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getTransactionCodeService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getTransactionCodeService().doApprove(auditHeader);

						if(aTransactionCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getTransactionCodeService().doReject(auditHeader);
						if(aTransactionCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_TransactionCodeDialog,
								auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_TransactionCodeDialog, 
						auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.transactionCode),true);
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
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Method For Rendering List into ComboBox
	 */

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aTransactionCode
	 *            (TransactionCode)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(TransactionCode aTransactionCode, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTransactionCode.getBefImage(),
				aTransactionCode);   
		return new AuditHeader(aTransactionCode.getTranCode(),null,null,null,auditDetail,
				aTransactionCode.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, 
					e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_TransactionCodeDialog, auditHeader);
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
		doShowNotes(this.transactionCode);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.transactionCode.getTranCode());
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

	public TransactionCode getTransactionCode() {
		return this.transactionCode;
	}
	public void setTransactionCode(TransactionCode transactionCode) {
		this.transactionCode = transactionCode;
	}

	public void setTransactionCodeService(TransactionCodeService transactionCodeService) {
		this.transactionCodeService = transactionCodeService;
	}
	public TransactionCodeService getTransactionCodeService() {
		return this.transactionCodeService;
	}

	public void setTransactionCodeListCtrl(TransactionCodeListCtrl transactionCodeListCtrl) {
		this.transactionCodeListCtrl = transactionCodeListCtrl;
	}
	public TransactionCodeListCtrl getTransactionCodeListCtrl() {
		return this.transactionCodeListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
}
