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
 * FileName    		:  SecurityOperationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014        Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securityoperation;

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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.administration.SecurityOperationDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityOperationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityOperation/securityoperationDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SecurityOperationDialogCtrl extends GFCBaseCtrl<SecurityOperation> {

	private static final long serialVersionUID = -1189739186571603178L;

	private static final Logger logger = Logger.getLogger(SecurityOperationDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	
	protected Window window_SecurityOperationDialog;                // autowired
	protected Textbox    oprCode;                                   // autoWired
	protected Textbox    oprDesc;                                   // autoWired
	protected Row        statusRow;                                 // autoWired
	
	
	/* not auto wired variables*/
	private SecurityOperation securityOperation;                           // over handed per parameter
	private transient SecurityOperationListCtrl securityOperationListCtrl; // over handed per parameter
	private transient SecurityOperationDAO securityOperationDAO;

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient SecurityOperationService securityOperationService;
	private transient PagedListService      pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SecurityOperationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityOperationDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityOperation object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityOperationDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SecurityOperationDialog);
		
		// READ OVERHANDED parameters !
		if (arguments.containsKey("SecurityOperation")) {
			this.securityOperation = (SecurityOperation) arguments.get("SecurityOperation");
			SecurityOperation befImage =new SecurityOperation();
			BeanUtils.copyProperties(this.securityOperation, befImage);
			this.securityOperation.setBefImage(befImage);
			setSecurityOperation(this.securityOperation);
		} else {
			setSecurityOperation(null);
		}

		doLoadWorkFlow(this.securityOperation.isWorkflow(),this.securityOperation.getWorkflowId(),this.securityOperation.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "SecurityOperationDialog");
		} else {
			getUserWorkspace().allocateAuthorities(super.pageRightName);
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();


		// READ OVERHANDED parameters !
		// we get the securityGroupListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete securityOperation here.
		if (arguments.containsKey("securityOperationListCtrl")) {
			setSecurityOperationListCtrl((SecurityOperationListCtrl) arguments.get("securityOperationListCtrl"));
		} else {
			setSecurityOperationListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSecurityOperation());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		//Empty sent any required attributes
		logger.debug("Entering ");
		this.oprCode.setMaxlength(50);
		this.oprDesc.setMaxlength(100);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities("SecurityOperationDialog",getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityOperationList_New"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityOperationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityOperationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityOperationDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering " + event.toString());
		doEdit();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_SecurityOperationDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doDelete();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering ");
		doWriteBeanToComponents(this.securityOperation.getBefImage());
		doReadOnly();
		this.btnCtrl.setBtnStatus_Save();
		this.btnEdit.setVisible(true);
		this.btnDelete.setVisible(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSecurityOperation
	 *            SecurityOperation
	 */
	public void doWriteBeanToComponents(SecurityOperation aSecurityOperation) {
		logger.debug("Entering ");
		this.oprCode.setValue(aSecurityOperation.getOprCode());
		this.oprDesc.setValue(aSecurityOperation.getOprDesc());
		this.recordStatus.setValue(aSecurityOperation.getRecordStatus());
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityOperation
	 */
	public void doWriteComponentsToBean(SecurityOperation aSecurityOperation) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSecurityOperation.setOprCode(this.oprCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityOperation.setOprDesc(this.oprDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aSecurityOperation.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSecurityOperation
	 * @throws InterruptedException
	 */
	public void doShowDialog(SecurityOperation aSecurityOperation) throws InterruptedException {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aSecurityOperation.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.oprCode.focus();
		} else {
			this.oprCode.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		if(!isWorkFlowEnabled()){
			this.userAction.setVisible(false);
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aSecurityOperation);
			
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);
		if (!this.oprCode.isReadonly()){
			this.oprCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityOperationDialog_OprCode.value"), PennantRegularExpressions.REGEX_UPP_BOX_ALPHA, true));
		}
		if (!this.oprDesc.isReadonly()){
			this.oprDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityOperationDialog_OprDescription.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.oprCode.setConstraint("");
		this.oprDesc.setConstraint("");
		logger.debug("Leaving ");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering ");
		this.oprCode.setErrorMessage("");
		this.oprDesc.setErrorMessage("");
		logger.debug("Leaving ");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * Deletes a SecurityOperation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityOperation aSecurityOperation = new SecurityOperation();
		BeanUtils.copyProperties(getSecurityOperation(), aSecurityOperation);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> "+Labels.getLabel("label_SecurityOperationDialog_OprCode.value")+ " : " + aSecurityOperation.getOprCode();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSecurityOperation.getRecordType())){
				aSecurityOperation.setVersion(aSecurityOperation.getVersion()+1);
				aSecurityOperation.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aSecurityOperation.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(doProcess(aSecurityOperation,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				showMessage(e);
			}
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		if (getSecurityOperation().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.oprCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.oprDesc.setReadonly(isReadOnly("SecurityOperationDialog_operationDescription"));
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.securityOperation.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.oprCode.setReadonly(true);
		this.oprDesc.setReadonly(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if(isWorkFlowEnabled()){ 
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before
		this.oprCode.setValue("");
		this.oprDesc.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityOperation aSecurityOperation = new SecurityOperation();
		BeanUtils.copyProperties(getSecurityOperation(), aSecurityOperation);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SecurityGroup object with the components data
		doWriteComponentsToBean(aSecurityOperation);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSecurityOperation.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSecurityOperation.getRecordType())){
				aSecurityOperation.setVersion(aSecurityOperation.getVersion()+1);
				if(isNew){
					aSecurityOperation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aSecurityOperation.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityOperation.setNewRecord(true);
				}
			}
		}else{
			aSecurityOperation.setVersion(aSecurityOperation.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if(doProcess(aSecurityOperation,tranType)){
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This Method used for setting all workFlow details from userWorkSpace 
	 * and setting audit details to auditHeader
	 * @param aSecurityOperation
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(SecurityOperation aSecurityOperation,String tranType){
		logger.debug("Entering ");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aSecurityOperation.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSecurityOperation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityOperation.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSecurityOperation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityOperation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSecurityOperation);
				}

				if (isNotesMandatory(taskId, aSecurityOperation)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aSecurityOperation.setTaskId(taskId);
			aSecurityOperation.setNextTaskId(nextTaskId);
			aSecurityOperation.setRoleCode(getRole());
			aSecurityOperation.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aSecurityOperation, tranType);

			String operationRefs = getServiceOperations(taskId, aSecurityOperation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aSecurityOperation, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aSecurityOperation, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * This Method used for calling the all Database  Operations from the service By
	 *  passing the  auditHeader and operationRefs(Method) as String
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		SecurityOperation aSecurityOperation=(SecurityOperation)auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;

		try {
			while(retValue==PennantConstants.porcessOVERIDE){
				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getSecurityOperationService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getSecurityOperationService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getSecurityOperationService().doApprove(auditHeader);
						if(aSecurityOperation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getSecurityOperationService().doReject(auditHeader);
						if(aSecurityOperation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_SecurityOperationDialog, auditHeader);
						return processCompleted; 
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_SecurityOperationDialog,auditHeader);

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					
					if(deleteNotes){
						deleteNotes(getNotes(this.securityOperation),true);
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
			logger.debug("Exception", e);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
   	 *	This method creates and returns Audit header Object
	 * @param aSecurityOperation
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityOperation aSecurityOperation, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityOperation.getBefImage(),aSecurityOperation);   
		return new AuditHeader(String.valueOf(aSecurityOperation.getId()),null,null,null
				,auditDetail,aSecurityOperation.getUserDetails(),getOverideMap());
	}

	/**
	 * This method shows message box with error message
	 * @param e
	 */
	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_SecurityOperationDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	/**
	 * when user clicks on "Notes" button.	
	 *
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.securityOperation);
		
	}

		
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getSecurityOperationListCtrl().search();
	}
	
	
	@Override
	protected String getReference() {
		return String.valueOf(this.securityOperation.getOprID());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public SecurityOperationService getSecurityOperationService() {
		return securityOperationService;
	}

	public void setSecurityOperationService(
			SecurityOperationService securityOperationService) {
		this.securityOperationService = securityOperationService;
	}

	public SecurityOperation getSecurityOperation() {
		return securityOperation;
	}

	public void setSecurityOperation(SecurityOperation securityOperation) {
		this.securityOperation = securityOperation;
	}

	public SecurityOperationListCtrl getSecurityOperationListCtrl() {
		return securityOperationListCtrl;
	}

	public void setSecurityOperationListCtrl(
			SecurityOperationListCtrl securityOperationListCtrl) {
		this.securityOperationListCtrl = securityOperationListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public SecurityOperationDAO getSecurityOperationDAO() {
		return securityOperationDAO;
	}

	public void setSecurityOperationDAO(SecurityOperationDAO securityOperationDAO) {
		this.securityOperationDAO = securityOperationDAO;
	}

}
