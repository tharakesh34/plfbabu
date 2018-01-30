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
 * FileName    		:  SecurityRoleDialogCtrl.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  10-8-2011														    *
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-8-2011      Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.securityrole;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityRoleService;
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
 * /WEB-INF/pages/Administration/SecurityRole/securityRoleDialog.zul file.
 */
public class SecurityRoleDialogCtrl extends GFCBaseCtrl<SecurityRole> {
	private static final long serialVersionUID = 8969578420575594907L;
	private static final Logger logger = Logger.getLogger(SecurityRoleDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window     window_SecurityRoleDialog;                   // autoWired
	protected Combobox   roleApp;                                     // autoWired
	protected Textbox    roleCd;                                      // autoWired
	protected Textbox    roleDesc;                                    // autoWired
	protected Textbox    roleCategory;                                // autoWired
	
	// not auto wired variables
	private SecurityRole     securityRole;                              // overHanded per parameter
	private transient    SecurityRoleListCtrl securityRoleListCtrl; 	// overHanded per parameter

	private transient boolean  validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient SecurityRoleService  securityRoleService;
	private transient PagedListService      pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SecurityRoleDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityRoleDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityRole object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityRoleDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SecurityRoleDialog);

		try {
			
			/* set components visible dependent of the users rights */
			doCheckRights();
			if (arguments.containsKey("securityRole")) {
				this.securityRole = (SecurityRole) arguments.get("securityRole");
				SecurityRole befImage = new SecurityRole();
				BeanUtils.copyProperties(this.securityRole, befImage);
				this.securityRole.setBefImage(befImage);
				setSecurityRole(this.securityRole);
			} else {
				setSecurityRole(null);
			}

			doLoadWorkFlow(this.securityRole.isWorkflow(),
					this.securityRole.getWorkflowId(),
					this.securityRole.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"SecurityRoleDialog");
			}

			// READ OVERHANDED parameters !
			// we get the securityRoleListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete securityRole here.
			if (arguments.containsKey("securityRoleListCtrl")) {
				setSecurityRoleListCtrl((SecurityRoleListCtrl) arguments.get("securityRoleListCtrl"));
			} else {
				setSecurityRoleListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSecurityRole());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SecurityRoleDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");

		//Empty sent any required attributes
		this.roleCd.setMaxlength(50);
		this.roleDesc.setMaxlength(100);
		this.roleCategory.setMaxlength(100);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
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

		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_SecurityRoleDialog);
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
		doWriteBeanToComponents(this.securityRole.getBefImage());
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
	 * @param aSecurityRole
	 *            SecurityRole
	 */
	public void doWriteBeanToComponents(SecurityRole aSecurityRole) {
		logger.debug("Entering ");
		fillComboBox(this.roleApp, String.valueOf(aSecurityRole.getRoleApp()), PennantStaticListUtil.getAppCodes(), "");
		this.roleCd.setValue(aSecurityRole.getRoleCd());
		this.roleDesc.setValue(aSecurityRole.getRoleDesc());
		this.roleCategory.setValue(aSecurityRole.getRoleCategory());

		this.recordStatus.setValue(aSecurityRole.getRecordStatus());
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityRole
	 */
	public void doWriteComponentsToBean(SecurityRole aSecurityRole) {
		logger.debug("Entering ");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			String strRoleApp = (String) this.roleApp.getSelectedItem().getValue();
			if (StringUtils.isBlank(strRoleApp)) {
				throw new WrongValueException(this.roleApp,Labels.getLabel("STATIC_INVALID"
						,new String[] { Labels.getLabel("label_SecurityRoleDialog_RoleApp.value") }));
			}
			aSecurityRole.setRoleApp(Integer.parseInt(strRoleApp));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSecurityRole.setRoleCd(this.roleCd.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityRole.setRoleDesc(this.roleDesc.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {

			aSecurityRole.setRoleCategory(this.roleCategory.getValue());

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

		aSecurityRole.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSecurityRole
	 * @throws Exception
	 */
	public void doShowDialog(SecurityRole aSecurityRole) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aSecurityRole.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.roleApp.focus();
		} else {
			this.roleDesc.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}	
		try {
			// fill the components with the data
			doWriteBeanToComponents(aSecurityRole);

			setDialog(DialogType.EMBEDDED);
			//this.window_SecurityRoleDialog.doModal();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_SecurityRoleDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.roleApp.isDisabled()) {
			this.roleApp.setConstraint(new StaticListValidator(PennantStaticListUtil.getAppCodes()
					,Labels.getLabel("label_SecurityRoleDialog_RoleApp.value")));
		}

		if (!this.roleCd.isReadonly()){
			this.roleCd.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityRoleDialog_RoleCd.value"),PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
		}

		if (!this.roleDesc.isReadonly()){
			this.roleDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityRoleDialog_RoleDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.roleApp.setConstraint("");
		this.roleCd.setConstraint("");
		this.roleDesc.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * This method set constraints for LOV fields
	 * @return void 
	 */
	private void doSetLOVValidation() {

	}

	/**
	 * This method remove constraints for LOV fields
	 */
	private void doRemoveLOVValidation() {

	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.roleCd.setErrorMessage("");
		this.roleApp.setErrorMessage("");
		this.roleDesc.setErrorMessage("");
		this.roleCategory.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a SecurityRole object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityRole aSecurityRole = new SecurityRole();
		BeanUtils.copyProperties(getSecurityRole(), aSecurityRole);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> "+ 
				Labels.getLabel("label_SecurityRoleDialog_RoleCd.value")+" : "+aSecurityRole.getRoleCd();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSecurityRole.getRecordType())){
				aSecurityRole.setVersion(aSecurityRole.getVersion()+1);
				aSecurityRole.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aSecurityRole.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(doProcess(aSecurityRole,tranType)){
					refreshList();
					closeDialog(); 
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");

		if (getSecurityRole().isNewRecord()){
			this.roleApp.setDisabled(false);
			this.btnCancel.setVisible(false);
		}else{
			this.roleApp.setDisabled(true);
			this.roleCd.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.roleDesc.focus();
		}

		this.roleDesc.setReadonly(isReadOnly("SecurityRoleDialog_roleDesc"));
		this.roleCategory.setReadonly(isReadOnly("SecurityRoleDialog_roleCategory"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.securityRole.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				this.roleApp.setDisabled(false);
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
		this.roleApp.setDisabled(true);
		this.roleCd.setReadonly(true);
		this.roleDesc.setReadonly(true);
		this.roleCategory.setReadonly(true);

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
		this.roleApp.setValue("");
		this.roleCd.setValue("");
		this.roleDesc.setValue("");
		this.roleCategory.setValue("");
		logger.debug("Leaving ");

	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");

		final SecurityRole aSecurityRole = new SecurityRole();
		BeanUtils.copyProperties(getSecurityRole(), aSecurityRole);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the SecurityRole object with the components data
		doWriteComponentsToBean(aSecurityRole);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSecurityRole.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSecurityRole.getRecordType())){
				aSecurityRole.setVersion(aSecurityRole.getVersion()+1);
				if(isNew){
					aSecurityRole.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aSecurityRole.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityRole.setNewRecord(true);
				}
			}
		}else{
			aSecurityRole.setVersion(aSecurityRole.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aSecurityRole,tranType)){
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * * This Method used for setting all workFlow details from userWorkSpace
	 *  and setting audit details to auditHeader
	 * @param aSecurityRole
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(SecurityRole aSecurityRole,String tranType){
		logger.debug("Entering ");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aSecurityRole.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSecurityRole.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityRole.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aSecurityRole.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityRole.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSecurityRole);
				}

				if (isNotesMandatory(taskId, aSecurityRole)) {
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
			aSecurityRole.setTaskId(taskId);
			aSecurityRole.setNextTaskId(nextTaskId);
			aSecurityRole.setRoleCode(getRole());
			aSecurityRole.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aSecurityRole, tranType);
			String operationRefs = getServiceOperations(taskId, aSecurityRole);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aSecurityRole, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aSecurityRole, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 *
	 * This Method used for calling the all Database  operations from the service by passing the  
	 * auditHeader and operationRefs(Method) as String
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		SecurityRole aSecurityRole=(SecurityRole)auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getSecurityRoleService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getSecurityRoleService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getSecurityRoleService().doApprove(auditHeader);

						if(aSecurityRole.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getSecurityRoleService().doReject(auditHeader);
						if(aSecurityRole.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_SecurityRoleDialog,auditHeader);
						return processCompleted; 
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SecurityRoleDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					if(deleteNotes){
						deleteNotes(getNotes(this.securityRole),true);
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
		logger.debug("Leaving ");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * This method creates AudiHeader Object and returns that Object 
	 * @param aSecurityRole
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityRole aSecurityRole, String tranType){
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityRole.getBefImage(), aSecurityRole);   
		return new AuditHeader(getReference(),null,null,null,auditDetail
				,aSecurityRole.getUserDetails(),getOverideMap());
	}

	/**
	 * When user Clicks on "Notes" button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.securityRole);
	}


	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getSecurityRoleListCtrl().search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.securityRole.getRoleID());
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

	public SecurityRole getSecurityRole() {
		return this.securityRole;
	}
	public void setSecurityRole(SecurityRole securityRole) {
		this.securityRole = securityRole;
	}

	public void setSecurityRoleService(SecurityRoleService securityRoleService) {
		this.securityRoleService = securityRoleService;
	}
	public SecurityRoleService getSecurityRoleService() {
		return this.securityRoleService;
	}

	public void setSecurityRoleListCtrl(SecurityRoleListCtrl securityRoleListCtrl) {
		this.securityRoleListCtrl = securityRoleListCtrl;
	}
	public SecurityRoleListCtrl getSecurityRoleListCtrl() {
		return this.securityRoleListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}
