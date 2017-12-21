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
 * FileName    		:  ScoringTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-11-2011    														*
 *                                                                  						*
 * Modified Date    :  08-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.bmtmasters.scoringtype;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.ScoringType;
import com.pennant.backend.service.bmtmasters.ScoringTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/ScoringType/scoringTypeDialog.zul file.
 */
public class ScoringTypeDialogCtrl extends GFCBaseCtrl<ScoringType> {
	private static final long serialVersionUID = 4967313068935251628L;
	private static final Logger logger = Logger.getLogger(ScoringTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ScoringTypeDialog; 	
	protected Textbox 		scoType; 					
	protected Textbox 		scoDesc; 					

	// not auto wired variables
	private ScoringType scoringType; 							// overHanded per parameter
	private transient ScoringTypeListCtrl scoringTypeListCtrl; 	// overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient ScoringTypeService scoringTypeService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public ScoringTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ScoringTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScoringTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScoringTypeDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("scoringType")) {
			this.scoringType = (ScoringType) arguments.get("scoringType");
			ScoringType befImage =new ScoringType();
			BeanUtils.copyProperties(this.scoringType, befImage);
			this.scoringType.setBefImage(befImage);
			setScoringType(this.scoringType);
		} else {
			setScoringType(null);
		}

		doLoadWorkFlow(this.scoringType.isWorkflow(),this.scoringType.getWorkflowId(),this.scoringType.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "ScoringTypeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the scoringTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete scoringType here.
		if (arguments.containsKey("scoringTypeListCtrl")) {
			setScoringTypeListCtrl((ScoringTypeListCtrl) arguments.get("scoringTypeListCtrl"));
		} else {
			setScoringTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getScoringType());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.scoType.setMaxlength(8);
		this.scoDesc.setMaxlength(50);

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

		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ScoringTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ScoringTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ScoringTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ScoringTypeDialog_btnSave"));
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
		logger.debug("Entering" +event.toString());
		doSave();
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_ScoringTypeDialog);
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
		doWriteBeanToComponents(this.scoringType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aScoringType (ScoringType)
	 */
	public void doWriteBeanToComponents(ScoringType aScoringType) {
		logger.debug("Entering") ;
		this.scoType.setValue(aScoringType.getScoType());
		this.scoDesc.setValue(aScoringType.getScoDesc());

		this.recordStatus.setValue(aScoringType.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aScoringType
	 */
	public void doWriteComponentsToBean(ScoringType aScoringType) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aScoringType.setScoType(this.scoType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aScoringType.setScoDesc(this.scoDesc.getValue());
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

		aScoringType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aScoringType
	 * @throws InterruptedException
	 */
	public void doShowDialog(ScoringType aScoringType) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aScoringType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.scoType.focus();
		} else {
			this.scoDesc.focus();
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
			doWriteBeanToComponents(aScoringType);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.scoType.isReadonly()){
			this.scoType.setConstraint(new PTStringValidator(Labels.getLabel("label_ScoringTypeDialog_ScoType.value"),null,true));
		}	
		if (!this.scoDesc.isReadonly()){
			this.scoDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_ScoringTypeDialog_ScoDesc.value"),null,true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.scoType.setConstraint("");
		this.scoDesc.setConstraint("");
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
		this.scoType.setErrorMessage("");
		this.scoDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a ScoringType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final ScoringType aScoringType = new ScoringType();
		BeanUtils.copyProperties(getScoringType(), aScoringType);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + 
									"\n\n --> " + aScoringType.getScoType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aScoringType.getRecordType())){
				aScoringType.setVersion(aScoringType.getVersion()+1);
				aScoringType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aScoringType.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aScoringType,tranType)){
					refreshList();
					//do close the Existing Dialog
					closeDialog(); 
				}
			}catch (DataAccessException e){
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getScoringType().isNewRecord()){
			this.scoType.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.scoType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.scoDesc.setReadonly(isReadOnly("ScoringTypeDialog_scoDesc"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.scoringType.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.scoType.setReadonly(true);
		this.scoDesc.setReadonly(true);

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
		this.scoType.setValue("");
		this.scoDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final ScoringType aScoringType = new ScoringType();
		BeanUtils.copyProperties(getScoringType(), aScoringType);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the ScoringType object with the components data
		doWriteComponentsToBean(aScoringType);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aScoringType.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aScoringType.getRecordType())){
				aScoringType.setVersion(aScoringType.getVersion()+1);
				if(isNew){
					aScoringType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aScoringType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aScoringType.setNewRecord(true);
				}
			}
		}else{
			aScoringType.setVersion(aScoringType.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aScoringType,tranType)){
				refreshList();
				//do close the Existing dialog
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aScoringType
	 *            (ScoringType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ScoringType aScoringType,String tranType){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aScoringType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aScoringType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aScoringType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aScoringType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aScoringType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aScoringType);
				}

				if (isNotesMandatory(taskId, aScoringType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}


			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();

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

			aScoringType.setTaskId(taskId);
			aScoringType.setNextTaskId(nextTaskId);
			aScoringType.setRoleCode(getRole());
			aScoringType.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aScoringType, tranType);

			String operationRefs = getServiceOperations(taskId, aScoringType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aScoringType, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aScoringType, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
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

		ScoringType aScoringType = (ScoringType) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getScoringTypeService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getScoringTypeService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getScoringTypeService().doApprove(auditHeader);

						if(aScoringType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getScoringTypeService().doReject(auditHeader);
						if(aScoringType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ScoringTypeDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_ScoringTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.scoringType),true);
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

	// WorkFlow Components

	/**
	 * @param aScoringType
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(ScoringType aScoringType, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aScoringType.getBefImage(), aScoringType);   
		return new AuditHeader(aScoringType.getScoType(),null,null,null,
				auditDetail,aScoringType.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ScoringTypeDialog, auditHeader);
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
		doShowNotes(this.scoringType);
	}

	// Method for refreshing the list after successful updation
	private void refreshList(){
		final JdbcSearchObject<ScoringType> soScoringType = getScoringTypeListCtrl().getSearchObj();
		getScoringTypeListCtrl().pagingScoringTypeList.setActivePage(0);
		getScoringTypeListCtrl().getPagedListWrapper().setSearchObject(soScoringType);
		if(getScoringTypeListCtrl().listBoxScoringType!=null){
			getScoringTypeListCtrl().listBoxScoringType.getListModel();
		}
	} 
	
	@Override
	protected String getReference() {
		return String.valueOf(this.scoringType.getScoType());
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

	public ScoringType getScoringType() {
		return this.scoringType;
	}
	public void setScoringType(ScoringType scoringType) {
		this.scoringType = scoringType;
	}

	public void setScoringTypeService(ScoringTypeService scoringTypeService) {
		this.scoringTypeService = scoringTypeService;
	}
	public ScoringTypeService getScoringTypeService() {
		return this.scoringTypeService;
	}

	public void setScoringTypeListCtrl(ScoringTypeListCtrl scoringTypeListCtrl) {
		this.scoringTypeListCtrl = scoringTypeListCtrl;
	}
	public ScoringTypeListCtrl getScoringTypeListCtrl() {
		return this.scoringTypeListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

}
