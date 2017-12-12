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
 * FileName    		:  SukukBrokerDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-06-2015    														*
 *                                                                  						*
 * Modified Date    :  09-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmasters.sukukbroker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmasters.SukukBroker;
import com.pennant.backend.model.applicationmasters.SukukBrokerBonds;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.SukukBrokerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.applicationmasters.sukukbrokerbonds.model.SukukBrokerBondsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * This is the controller class for the /WEB-INF/pages/Application
 * Masters/SukukBroker/sukukBrokerDialog.zul file.
 */
public class SukukBrokerDialogCtrl extends GFCBaseCtrl<SukukBroker> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SukukBrokerDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	public Window window_SukukBrokerDialog; 
	protected Row 			row0; 
	protected Label 		label_BrokerCode;
	protected Hlayout 		hlayout_BrokerCode;
	protected Space 		space_BrokerCode; 
 
	protected Textbox 		brokerCode; 
	protected Label 		label_BrokerDesc;
	protected Hlayout 		hlayout_BrokerDesc;
	protected Space 		space_BrokerDesc; 
 
	protected Textbox 		brokerDesc; 
	protected Label 		recordType;	 
	private boolean 		enqModule=false;
	
	// not auto wired vars
	private SukukBroker sukukBroker; // overhanded per param
	private transient SukukBrokerListCtrl sukukBrokerListCtrl; // overhanded per param

	
	protected Button button_SukukBrokerBondsList_NewSukukBrokerBonds;
	
	// ServiceDAOs / Domain Classes
	private transient SukukBrokerService sukukBrokerService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SukukBrokerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SukukBrokerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected SukukBroker object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SukukBrokerDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SukukBrokerDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule=(Boolean) arguments.get("enqModule");
			}else{
				enqModule=false;
			}
			
			// READ OVERHANDED params !
			if (arguments.containsKey("sukukBroker")) {
				this.sukukBroker = (SukukBroker) arguments.get("sukukBroker");
				SukukBroker befImage =new SukukBroker();
				BeanUtils.copyProperties(this.sukukBroker, befImage);
				this.sukukBroker.setBefImage(befImage);
				
				setSukukBroker(this.sukukBroker);
			} else {
				setSukukBroker(null);
			}
			doLoadWorkFlow(this.sukukBroker.isWorkflow(),this.sukukBroker.getWorkflowId(),this.sukukBroker.getNextTaskId());
	
			if (isWorkFlowEnabled() && !enqModule){
					this.userAction	= setListRecordStatus(this.userAction);
					getUserWorkspace().allocateRoleAuthorities(getRole(), "SukukBrokerDialog");
			}
	
			/* set components visible dependent of the users rights */
			doCheckRights();
			

			// READ OVERHANDED params !
			// we get the sukukBrokerListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete sukukBroker here.
			if (arguments.containsKey("sukukBrokerListCtrl")) {
				setSukukBrokerListCtrl((SukukBrokerListCtrl) arguments.get("sukukBrokerListCtrl"));
			} else {
				setSukukBrokerListCtrl(null);
			}
			setSukukBrokerBondsPagedListWrapper();
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSukukBroker());
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
		doEdit();
	}
	
	private void doEdit() {
		logger.debug("Entering");

		if (this.sukukBroker.isNewRecord()) {
			this.brokerCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.brokerCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.brokerDesc.setReadonly(isReadOnly("SukukBrokerDialog_BrokerDesc"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.sukukBroker.isNewRecord()) {
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
		doCancel();
	}
	
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.sukukBroker.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_SukukBrokerDialog);
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
		doShowNotes(this.sukukBroker);
	}


	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSukukBroker
	 * @throws InterruptedException
	 */
	public void doShowDialog(SukukBroker aSukukBroker) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (sukukBroker.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.brokerCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.brokerDesc.focus();
				if (StringUtils.isNotBlank(sukukBroker.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		
		// fill the components with the data
		doWriteBeanToComponents(sukukBroker);
		//this.listBoxSukukBrokerBonds.setHeight((borderLayoutHeight-200)+"px");
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	
	}
	
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.brokerCode.setReadonly(true);
		this.brokerDesc.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug("Leaving");
		logger.debug("Entering");
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
			getUserWorkspace().allocateAuthorities(super.pageRightName,getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerDialog_btnSave"));	
			this.button_SukukBrokerBondsList_NewSukukBrokerBonds.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerDialog_btnNew"));

		}

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.brokerCode.setMaxlength(20);
	 	this.brokerDesc.setMaxlength(150);
	 	
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
	
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSukukBroker
	 *            SukukBroker
	 */
	public void doWriteBeanToComponents(SukukBroker aSukukBroker) {
		logger.debug("Entering") ;
		this.brokerCode.setValue(aSukukBroker.getBrokerCode());
		this.brokerDesc.setValue(aSukukBroker.getBrokerDesc());
		doFilllistbox(aSukukBroker.getSukukBrokerBonds());
		this.recordStatus.setValue(aSukukBroker.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aSukukBroker.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSukukBroker
	 */
	public void doWriteComponentsToBean(SukukBroker aSukukBroker) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Code
		try {
		    aSukukBroker.setBrokerCode(this.brokerCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Description
		try {
		    aSukukBroker.setBrokerDesc(this.brokerDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try{
			if(this.listBoxSukukBrokerBonds.getItemCount()<=0){
				throw new WrongValueException(this.button_SukukBrokerBondsList_NewSukukBrokerBonds,Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("window_SukukBrokerDialog.title")}));
			}
			aSukukBroker.setSukukBrokerBonds(sukukBrokerBondsList);
		}catch(WrongValueException we ) {
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
		if (!this.brokerCode.isReadonly()){
			this.brokerCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BrokerDialog_BrokerCode.value"),PennantRegularExpressions.REGEX_UPPERCASENAME,true));
		}
		//Description
		if (!this.brokerDesc.isReadonly()){
			this.brokerDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_BrokerDialog_BrokerDesc.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.brokerCode.setConstraint("");
		this.brokerDesc.setConstraint("");
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
		this.brokerCode.setErrorMessage("");
		this.brokerDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getSukukBrokerListCtrl().search();
	}

	/**
	 * Deletes a SukukBroker object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final SukukBroker aSukukBroker = new SukukBroker();
		BeanUtils.copyProperties(getSukukBroker(), aSukukBroker);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_BrokerDialog_BrokerCode.value")+" : "+aSukukBroker.getBrokerCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSukukBroker.getRecordType())){
				aSukukBroker.setVersion(aSukukBroker.getVersion()+1);
				aSukukBroker.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aSukukBroker.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aSukukBroker.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aSukukBroker.getNextTaskId(), aSukukBroker);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aSukukBroker,tranType)){
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
		
		this.brokerCode.setValue("");
		this.brokerDesc.setValue("");
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SukukBroker aSukukBroker = new SukukBroker();
		BeanUtils.copyProperties(getSukukBroker(), aSukukBroker);
		boolean isNew = false;
		
		if(isWorkFlowEnabled()){
			aSukukBroker.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aSukukBroker.getNextTaskId(), aSukukBroker);
		}
		
		// force validation, if on, than execute by component.getValue()
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aSukukBroker.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the SukukBroker object with the components data
			doWriteComponentsToBean(aSukukBroker);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aSukukBroker.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSukukBroker.getRecordType())){
				aSukukBroker.setVersion(aSukukBroker.getVersion()+1);
				if(isNew){
					aSukukBroker.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aSukukBroker.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSukukBroker.setNewRecord(true);
				}
			}
		}else{
			aSukukBroker.setVersion(aSukukBroker.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aSukukBroker,tranType)){
				//doWriteBeanToComponents(aSukukBroker);
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
	
	private boolean doProcess(SukukBroker aSukukBroker,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aSukukBroker.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aSukukBroker.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSukukBroker.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			
			aSukukBroker.setTaskId(getTaskId());
			aSukukBroker.setNextTaskId(getNextTaskId());
			aSukukBroker.setRoleCode(getRole());
			aSukukBroker.setNextRoleCode(getNextRoleCode());
			
			if (StringUtils.isBlank(getOperationRefs())) {
					processCompleted = doSaveProcess(getAuditHeader(aSukukBroker, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aSukukBroker, PennantConstants.TRAN_WF);
				
				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aSukukBroker, tranType), null);
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
		
		SukukBroker aSukukBroker = (SukukBroker) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getSukukBrokerService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getSukukBrokerService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getSukukBrokerService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aSukukBroker.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getSukukBrokerService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aSukukBroker.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SukukBrokerDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_SukukBrokerDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("SukukBroker",aSukukBroker.getBrokerCode(),aSukukBroker.getVersion()),true);
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

	private AuditHeader getAuditHeader(SukukBroker aSukukBroker, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSukukBroker.getBefImage(), aSukukBroker);   
		return new AuditHeader(aSukukBroker.getBrokerCode(),null,null,null,auditDetail,aSukukBroker.getUserDetails(),getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SukukBroker getSukukBroker() {
		return this.sukukBroker;
	}

	public void setSukukBroker(SukukBroker sukukBroker) {
		this.sukukBroker = sukukBroker;
	}

	public void setSukukBrokerService(SukukBrokerService sukukBrokerService) {
		this.sukukBrokerService = sukukBrokerService;
	}

	public SukukBrokerService getSukukBrokerService() {
		return this.sukukBrokerService;
	}

	public void setSukukBrokerListCtrl(SukukBrokerListCtrl sukukBrokerListCtrl) {
		this.sukukBrokerListCtrl = sukukBrokerListCtrl;
	}

	public SukukBrokerListCtrl getSukukBrokerListCtrl() {
		return this.sukukBrokerListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	//===================
	
	protected Listbox listBoxSukukBrokerBonds;
	protected Paging pagingSukukBrokerBondsList; // autowired
	private PagedListWrapper<SukukBrokerBonds> sukukBrokerBondsPagedListWrapper;
	private List<SukukBrokerBonds> sukukBrokerBondsList = new ArrayList<SukukBrokerBonds>();
	
	/**
	 * Call the TransactionEntry dialog with a new empty entry. <br>
	 */
	public void onClick$button_SukukBrokerBondsList_NewSukukBrokerBonds(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
			doClearMessage();
			doSetValidation();

		// create a new TransactionEntry object, We GET it from the backEnd.
		final SukukBrokerBonds aSukukBrokerBonds = new SukukBrokerBonds();
		aSukukBrokerBonds.setNewRecord(true);
		aSukukBrokerBonds.setBrokerCode(this.brokerCode.getValue());

		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("sukukBrokerBonds", aSukukBrokerBonds);
		map.put("sukukBrokerDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/SukukBrokerBonds/SukukBrokerBondsDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Double Click the Transaction Entry Item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onSukukBrokerBondsItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Listitem item = (Listitem) event.getOrigin().getTarget();
		SukukBrokerBonds itemdata = (SukukBrokerBonds) item.getAttribute("data");
		itemdata.setNewRecord(false);
		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("sukukBrokerBonds", itemdata);
		map.put("sukukBrokerDialogCtrl", this);
		map.put("role", getRole());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/SukukBrokerBonds/SukukBrokerBondsDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param transactionEntryList
	 */
	public void doFilllistbox(List<SukukBrokerBonds> transactionEntryList) {
		logger.debug("Entering");

		if (transactionEntryList != null) {
			getSukukBrokerBondsList().clear();
			setSukukBrokerBondsList(transactionEntryList);
			this.pagingSukukBrokerBondsList.setDetailed(true);
			getSukukBrokerBondsPagedListWrapper().initList(transactionEntryList, 
					this.listBoxSukukBrokerBonds, this.pagingSukukBrokerBondsList);
			this.listBoxSukukBrokerBonds.setItemRenderer(new SukukBrokerBondsListModelItemRenderer());
		}
		checkListboxcount();
		logger.debug("Leaving");
	}
	
	private void checkListboxcount() {
		logger.debug("Entering");
		if (this.listBoxSukukBrokerBonds.getItemCount() > 0) {
			this.brokerCode.setDisabled(false);
		} 
		logger.debug("Leaving");
	}
	
	public PagedListWrapper<SukukBrokerBonds> getSukukBrokerBondsPagedListWrapper() {
		return sukukBrokerBondsPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setSukukBrokerBondsPagedListWrapper() {
		if (this.sukukBrokerBondsPagedListWrapper == null) {
			this.sukukBrokerBondsPagedListWrapper = (PagedListWrapper<SukukBrokerBonds>) SpringUtil.getBean("pagedListWrapper");
		}
	}
	

	public List<SukukBrokerBonds> getSukukBrokerBondsList() {
		return sukukBrokerBondsList;
	}

	public void setSukukBrokerBondsList(List<SukukBrokerBonds> sukukBrokerBondsList) {
		this.sukukBrokerBondsList = sukukBrokerBondsList;
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.sukukBroker.getBrokerCode());
	}
	
	

}
