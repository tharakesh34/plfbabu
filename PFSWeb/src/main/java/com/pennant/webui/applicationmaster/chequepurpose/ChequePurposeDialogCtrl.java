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
 * FileName    		:  ChequePurposeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.chequepurpose;

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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.ChequePurpose;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.ChequePurposeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/ChequePurpose/chequePurposeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ChequePurposeDialogCtrl extends GFCBaseCtrl<ChequePurpose> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ChequePurposeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ChequePurposeDialog; 
	protected Row 			row0; 
	protected Label 		label_Code;
	protected Hlayout 		hlayout_Code;
	protected Space 		space_Code; 

	protected Textbox 		code; 
	protected Label 		label_Description;
	protected Hlayout 		hlayout_Description;
	protected Space 		space_Description; 

	protected Textbox 		description; 
	protected Row 			row1; 
	protected Label 		label_Active;
	protected Hlayout 		hlayout_Active;
	protected Space 		space_Active; 

	protected Checkbox 		active; 

	private boolean 		enqModule=false;

	// not auto wired vars
	private ChequePurpose chequePurpose; // overhanded per param
	private transient ChequePurposeListCtrl chequePurposeListCtrl; // overhanded per param

	
	// ServiceDAOs / Domain Classes
	private transient ChequePurposeService chequePurposeService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public ChequePurposeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ChequePurposeDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ChequePurpose object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ChequePurposeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChequePurposeDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule=(Boolean) arguments.get("enqModule");
			}else{
				enqModule=false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("chequePurpose")) {
				this.chequePurpose = (ChequePurpose) arguments.get("chequePurpose");
				ChequePurpose befImage =new ChequePurpose();
				BeanUtils.copyProperties(this.chequePurpose, befImage);
				this.chequePurpose.setBefImage(befImage);

				setChequePurpose(this.chequePurpose);
			} else {
				setChequePurpose(null);
			}
			doLoadWorkFlow(this.chequePurpose.isWorkflow(),this.chequePurpose.getWorkflowId(),this.chequePurpose.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ChequePurposeDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the chequePurposeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete chequePurpose here.
			if (arguments.containsKey("chequePurposeListCtrl")) {
				setChequePurposeListCtrl((ChequePurposeListCtrl) arguments.get("chequePurposeListCtrl"));
			} else {
				setChequePurposeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getChequePurpose());
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
		displayComponents(ScreenCTL.SCRN_GNEDT);
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
		doWriteBeanToComponents(this.chequePurpose.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
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
		MessageUtil.showHelpWindow(event, window_ChequePurposeDialog);
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


			ScreenCTL.displayNotes(getNotes("ChequePurpose",getChequePurpose().getCode(),getChequePurpose().getVersion()),this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" +event.toString());

	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aChequePurpose
	 * @throws InterruptedException
	 */
	public void doShowDialog(ChequePurpose aChequePurpose) throws InterruptedException {
		logger.debug("Entering");

		try {

			// fill the components with the data
			doWriteBeanToComponents(aChequePurpose);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aChequePurpose.isNewRecord()));
			
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving") ;
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode){
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(),isFirstTask(), this.userAction,this.code,this.description));

		if (getChequePurpose().isNewRecord()){
			setComponentAccessType("ChequePurposeDialog_Code", false, this.code, this.space_Code, this.label_Code, this.hlayout_Code,null);
		}

		logger.debug("Leaving");
	} 

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly= readOnly;
		if (readOnly){
			tempReadOnly = true;
		}else if (PennantConstants.RECORD_TYPE_DEL.equals(this.chequePurpose.getRecordType())) {
			tempReadOnly = true;
		}
		setComponentAccessType("ChequePurposeDialog_Code", true, this.code, this.space_Code, this.label_Code, this.hlayout_Code,null);		
		setComponentAccessType("ChequePurposeDialog_Description", tempReadOnly, this.description, this.space_Description, this.label_Description, this.hlayout_Description,null);
		setRowInvisible(this.row0, this.hlayout_Code,this.hlayout_Description);
		setComponentAccessType("ChequePurposeDialog_Active", tempReadOnly, this.active, this.space_Active, this.label_Active, this.hlayout_Active,null);
		setRowInvisible(this.row1, this.hlayout_Active,null);

		if (!getChequePurpose().isNewRecord() &&  PennantConstants.FIN_MGRCHQ__CHQPURPOSECODE.equals(getChequePurpose().getCode())) {
			setComponentAccessType("ChequePurposeDialog_Active", true, this.active, this.space_Active, this.label_Active, this.hlayout_Active,null);
		}

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		getUserWorkspace().allocateAuthorities("ChequePurposeDialog", getRole());
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ChequePurposeDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ChequePurposeDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ChequePurposeDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ChequePurposeDialog_btnSave"));
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.code.setMaxlength(8);
		this.description.setMaxlength(50);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		//setStatusDetails(gb_statusDetails,groupboxWf,south,enqModule);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aChequePurpose
	 *            ChequePurpose
	 */
	public void doWriteBeanToComponents(ChequePurpose aChequePurpose) {
		logger.debug("Entering") ;
		this.code.setValue(aChequePurpose.getCode());
		this.description.setValue(aChequePurpose.getDescription());
		this.active.setChecked(aChequePurpose.isActive());

		this.recordStatus.setValue(aChequePurpose.getRecordStatus());
		//this.recordType.setValue(PennantJavaUtil.getLabel(aChequePurpose.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aChequePurpose
	 */
	public void doWriteComponentsToBean(ChequePurpose aChequePurpose) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Code
		try {
			aChequePurpose.setCode(this.code.getValue().trim());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Description
		try {
			aChequePurpose.setDescription(this.description.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Active
		try {
			aChequePurpose.setActive(this.active.isChecked());
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
		if (!this.code.isReadonly()){
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_ChequePurposeDialog_Code.value"),PennantRegularExpressions.REGEX_UPPERCASENAME,true));
		}
		//Description
		if (!this.description.isReadonly()){
			this.description.setConstraint(new PTStringValidator(Labels.getLabel("label_ChequePurposeDialog_Description.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.code.setConstraint("");
		this.description.setConstraint("");
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
		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getChequePurposeListCtrl().search();
	}

	/**
	 * Deletes a ChequePurpose object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final ChequePurpose aChequePurpose = new ChequePurpose();
		BeanUtils.copyProperties(getChequePurpose(), aChequePurpose);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aChequePurpose.getCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aChequePurpose.getRecordType())){
				aChequePurpose.setVersion(aChequePurpose.getVersion()+1);
				aChequePurpose.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aChequePurpose.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aChequePurpose.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aChequePurpose.getNextTaskId(), aChequePurpose);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aChequePurpose,tranType)){
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
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.code.setValue("");
		this.description.setValue("");
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
		final ChequePurpose aChequePurpose = new ChequePurpose();
		BeanUtils.copyProperties(getChequePurpose(), aChequePurpose);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			aChequePurpose.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aChequePurpose.getNextTaskId(), aChequePurpose);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aChequePurpose.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the ChequePurpose object with the components data
			doWriteComponentsToBean(aChequePurpose);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aChequePurpose.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aChequePurpose.getRecordType())){
				aChequePurpose.setVersion(aChequePurpose.getVersion()+1);
				if(isNew){
					aChequePurpose.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aChequePurpose.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aChequePurpose.setNewRecord(true);
				}
			}
		}else{
			aChequePurpose.setVersion(aChequePurpose.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aChequePurpose,tranType)){
				//doWriteBeanToComponents(aChequePurpose);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
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

	private boolean doProcess(ChequePurpose aChequePurpose,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aChequePurpose.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aChequePurpose.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aChequePurpose.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aChequePurpose.setTaskId(getTaskId());
			aChequePurpose.setNextTaskId(getNextTaskId());
			aChequePurpose.setRoleCode(getRole());
			aChequePurpose.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aChequePurpose, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aChequePurpose, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aChequePurpose, tranType), null);
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

		ChequePurpose aChequePurpose = (ChequePurpose) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getChequePurposeService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getChequePurposeService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getChequePurposeService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aChequePurpose.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getChequePurposeService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aChequePurpose.getRecordType())){
							deleteNotes=true;
						}

					}else{
						//auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null,PennantConstants.ERR_SEV_ERROR));
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ChequePurposeDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_ChequePurposeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("ChequePurpose",aChequePurpose.getCode(),aChequePurpose.getVersion()),true);
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
			logger.warn("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ChequePurpose aChequePurpose, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aChequePurpose.getBefImage(), aChequePurpose);   
		return new AuditHeader(aChequePurpose.getCode(),null,null,null,auditDetail,aChequePurpose.getUserDetails(),getOverideMap());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public ChequePurpose getChequePurpose() {
		return this.chequePurpose;
	}

	public void setChequePurpose(ChequePurpose chequePurpose) {
		this.chequePurpose = chequePurpose;
	}

	public void setChequePurposeService(ChequePurposeService chequePurposeService) {
		this.chequePurposeService = chequePurposeService;
	}

	public ChequePurposeService getChequePurposeService() {
		return this.chequePurposeService;
	}

	public void setChequePurposeListCtrl(ChequePurposeListCtrl chequePurposeListCtrl) {
		this.chequePurposeListCtrl = chequePurposeListCtrl;
	}

	public ChequePurposeListCtrl getChequePurposeListCtrl() {
		return this.chequePurposeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
