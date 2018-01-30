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
 * FileName    		:  FlagDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-07-2015    														*
 *                                                                  						*
 * Modified Date    :  14-07-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-07-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmasters.flag;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.FlagService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMasters/Flag/flagDialog.zul file.
 */
public class FlagDialogCtrl extends GFCBaseCtrl<Flag> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FlagDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FlagDialog; 
	protected Row 			row0; 
	protected Label 		label_FlagCode;
	protected Space 		space_FlagCode; 
 
	protected Textbox 		flagCode; 
	protected Label 		label_FlagDesc;
	protected Space 		space_FlagDesc; 
 
	protected Textbox 		flagDesc; 
	protected Row 			row1; 
	protected Label 		label_Active;
	protected Space 		space_Active; 
 
	protected Checkbox 		active; 
	private boolean 		enqModule=false;
	
	// not auto wired vars
	private Flag flag; // overhanded per param
	private transient FlagListCtrl flagListCtrl; // overhanded per param

	
	// ServiceDAOs / Domain Classes
	private transient FlagService flagService;

	/**
	 * default constructor.<br>
	 */
	public FlagDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FlagsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Flag object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FlagDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FlagDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule=(Boolean) arguments.get("enqModule");
			}else{
				enqModule=false;
			}
			
			// READ OVERHANDED params !
			if (arguments.containsKey("flag")) {
				this.flag = (Flag) arguments.get("flag");
				Flag befImage =new Flag();
				BeanUtils.copyProperties(this.flag, befImage);
				this.flag.setBefImage(befImage);
				
				setFlag(this.flag);
			} else {
				setFlag(null);
			}
			doLoadWorkFlow(this.flag.isWorkflow(),this.flag.getWorkflowId(),this.flag.getNextTaskId());
	
			if (isWorkFlowEnabled() && !enqModule){
					this.userAction	= setListRecordStatus(this.userAction);
					getUserWorkspace().allocateRoleAuthorities(getRole(), "FlagsDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}
	
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the flagListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete flag here.
			if (arguments.containsKey("flagListCtrl")) {
				setFlagListCtrl((FlagListCtrl) arguments.get("flagListCtrl"));
			} else {
				setFlagListCtrl(null);
			}
	
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFlag());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_FlagDialog);
		logger.debug("Leaving" +event.toString());
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
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		try {
			
			
			ScreenCTL.displayNotes(getNotes("Flag",getFlag().getFlagCode(),getFlag().getVersion()),this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" +event.toString());
	
	}


	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFlag
	 * @throws InterruptedException
	 */
	public void doShowDialog(Flag aFlag) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
				if (aFlag.isNew()) {
					this.flagCode.setVisible(true);
					this.btnCtrl.setInitNew();
					doEdit();
					// setFocus
					this.flagCode.focus();
				} else {
					this.flagCode.setReadonly(true);
					this.flagDesc.focus();
					if (isWorkFlowEnabled()){
						if (StringUtils.isNotBlank(aFlag.getRecordType())){
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
			doWriteBeanToComponents(aFlag);
			
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving") ;
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFlag().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.flagCode.setReadonly(isReadOnly("FlagsDialog_FlagCode"));
		} else {
			this.btnCancel.setVisible(true);
			this.flagCode.setReadonly(true);
		}

		
		this.flagDesc.setReadonly(isReadOnly("FlagsDialog_FlagDesc"));
		this.active.setDisabled(isReadOnly("FlagsDialog_Active"));
		if(getFlag().isNewRecord() || StringUtils.equals(getFlag().getRecordType(), PennantConstants.RECORD_TYPE_NEW)){
			//this.active.setDisabled(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.flag.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.flagCode.setReadonly(true);
		this.flagDesc.setReadonly(true);
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
		logger.debug("Leaving ");
	}

	// Helpers

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
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FlagsDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FlagsDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FlagsDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FlagsDialog_btnSave"));	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.flagCode.setMaxlength(6);
		this.flagDesc.setMaxlength(50);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		
		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.flag.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}
	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFlag
	 *            Flag
	 */
	public void doWriteBeanToComponents(Flag aFlag) {
		logger.debug("Entering") ;
		this.flagCode.setValue(aFlag.getFlagCode());
		this.flagDesc.setValue(aFlag.getFlagDesc());
		this.active.setChecked(aFlag.isActive());
		this.recordStatus.setValue(aFlag.getRecordStatus());
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFlag
	 */
	public void doWriteComponentsToBean(Flag aFlag) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Code
		try {
		    aFlag.setFlagCode(this.flagCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Description
		try {
		    aFlag.setFlagDesc(this.flagDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Active
		try {
			aFlag.setActive(this.active.isChecked());
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
		
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//Code
		if (!this.flagCode.isReadonly()){
			this.flagCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FlagDialog_FlagCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHA,true));
		}
		//Description
		if (!this.flagDesc.isReadonly()){
			this.flagDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FlagDialog_FlagDesc.value"),PennantRegularExpressions.REGEX_DESCRIPTION,true));
		}
	logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.flagCode.setConstraint("");
		this.flagDesc.setConstraint("");
	logger.debug("Leaving");
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}
	
	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.flagCode.setErrorMessage("");
		this.flagDesc.setErrorMessage("");
		logger.debug("Leaving");
	}
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getFlagListCtrl().search();
	}

	/**
	 * Deletes a Flag object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final Flag aFlag = new Flag();
		BeanUtils.copyProperties(getFlag(), aFlag);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +Labels.getLabel("label_FlagCode")+":"+ aFlag.getFlagCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFlag.getRecordType())){
				aFlag.setVersion(aFlag.getVersion()+1);
				aFlag.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aFlag.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aFlag.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFlag.getNextTaskId(), aFlag);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFlag,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (Exception e) {
				MessageUtil.showError(e);
			}
			
		}
		logger.debug("Leaving");
	}


	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		
		this.flagCode.setValue("");
		this.flagDesc.setValue("");
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
		final Flag aFlag = new Flag();
		BeanUtils.copyProperties(getFlag(), aFlag);
		boolean isNew = false;
		
		if(isWorkFlowEnabled()){
			aFlag.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFlag.getNextTaskId(), aFlag);
		}
		
		// force validation, if on, than execute by component.getValue()
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aFlag.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the Flag object with the components data
			doWriteComponentsToBean(aFlag);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aFlag.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFlag.getRecordType())){
				aFlag.setVersion(aFlag.getVersion()+1);
				if(isNew){
					aFlag.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFlag.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFlag.setNewRecord(true);
				}
			}
		}else{
			aFlag.setVersion(aFlag.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aFlag,tranType)){
				//doWriteBeanToComponents(aFlag);
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	
	private boolean doProcess(Flag aFlag,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aFlag.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFlag.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFlag.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			
			aFlag.setTaskId(getTaskId());
			aFlag.setNextTaskId(getNextTaskId());
			aFlag.setRoleCode(getRole());
			aFlag.setNextRoleCode(getNextRoleCode());
			
			if (StringUtils.isBlank(getOperationRefs())) {
					processCompleted = doSaveProcess(getAuditHeader(aFlag, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aFlag, PennantConstants.TRAN_WF);
				
				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aFlag, tranType), null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param  AuditHeader auditHeader
	 * @param method  (String)
	 * @return boolean
	 * 
	 */
	
	private boolean doSaveProcess(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		
		Flag aFlag = (Flag) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getFlagService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getFlagService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getFlagService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aFlag.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getFlagService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aFlag.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FlagDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_FlagDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("Flag",aFlag.getFlagCode(),aFlag.getVersion()),true);
					}
				}
				
				if (retValue==PennantConstants.porcessOVERIDE){
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
		
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components
	
	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Flag aFlag, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFlag.getBefImage(), aFlag);   
		return new AuditHeader(aFlag.getFlagCode(),null,null,null,auditDetail,aFlag.getUserDetails(),getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Flag getFlag() {
		return this.flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public void setFlagService(FlagService flagService) {
		this.flagService = flagService;
	}

	public FlagService getFlagService() {
		return this.flagService;
	}

	public void setFlagListCtrl(FlagListCtrl flagListCtrl) {
		this.flagListCtrl = flagListCtrl;
	}

	public FlagListCtrl getFlagListCtrl() {
		return this.flagListCtrl;
	}

}
