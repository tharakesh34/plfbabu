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
 * FileName    		:  SukukBondDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.applicationmasters.sukukbond;

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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmasters.SukukBond;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.SukukBondService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMasters/SukukBond/sukukBondDialog.zul file.
 */
public class SukukBondDialogCtrl extends GFCBaseCtrl<SukukBond> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SukukBondDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SukukBondDialog; 
	protected Row 			row0; 
	protected Label 		label_BondCode;
	protected Hlayout 		hlayout_BondCode;
	protected Space 		space_BondCode; 
 
	protected Textbox 		bondCode; 
	protected Label 		label_BondDesc;
	protected Hlayout 		hlayout_BondDesc;
	protected Space 		space_BondDesc; 
 
	protected Textbox 		bondDesc; 

	
	protected Label 		recordType;	 
	protected Groupbox 		gb_statusDetails;
	private boolean 		enqModule=false;
	
	// not auto wired vars
	private SukukBond sukukBond; // overhanded per param
	private transient SukukBondListCtrl sukukBondListCtrl; // overhanded per param

	
	// ServiceDAOs / Domain Classes
	private transient SukukBondService sukukBondService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SukukBondDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SukukBondDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected SukukBond object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SukukBondDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SukukBondDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule=(Boolean) arguments.get("enqModule");
			}else{
				enqModule=false;
			}
			
			// READ OVERHANDED params !
			if (arguments.containsKey("sukukBond")) {
				this.sukukBond = (SukukBond) arguments.get("sukukBond");
				SukukBond befImage =new SukukBond();
				BeanUtils.copyProperties(this.sukukBond, befImage);
				this.sukukBond.setBefImage(befImage);
				
				setSukukBond(this.sukukBond);
			} else {
				setSukukBond(null);
			}
			doLoadWorkFlow(this.sukukBond.isWorkflow(),this.sukukBond.getWorkflowId(),this.sukukBond.getNextTaskId());
	
			if (isWorkFlowEnabled() && !enqModule){
					this.userAction	= setListRecordStatus(this.userAction);
					getUserWorkspace().allocateRoleAuthorities(getRole(), "SukukBondDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}
	
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the sukukBondListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete sukukBond here.
			if (arguments.containsKey("sukukBondListCtrl")) {
				setSukukBondListCtrl((SukukBondListCtrl) arguments.get("sukukBondListCtrl"));
			} else {
				setSukukBondListCtrl(null);
			}
	
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSukukBond());
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
		this.btnCancel.setVisible(true);
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
		doWriteBeanToComponents(this.sukukBond.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
		this.btnCancel.setVisible(false);
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
		MessageUtil.showHelpWindow(event, window_SukukBondDialog);
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
			
			
			ScreenCTL.displayNotes(getNotes("SukukBond",getSukukBond().getBondCode(),getSukukBond().getVersion()),this);

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
	 * @param aSukukBond
	 * @throws InterruptedException
	 */
	public void doShowDialog(SukukBond aSukukBond) throws InterruptedException {
		logger.debug("Entering");

		try {
		
			// fill the components with the data
			doWriteBeanToComponents(aSukukBond);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aSukukBond.isNewRecord()));
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
		
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(),isFirstTask(), this.userAction,this.bondCode,this.bondCode));
		
		if (getSukukBond().isNewRecord()){
			  	setComponentAccessType("SukukBondDialog_BondCode", false, this.bondCode, this.space_BondCode, this.label_BondCode, this.hlayout_BondCode,null);
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
		}else if (PennantConstants.RECORD_TYPE_DEL.equals(this.sukukBond.getRecordType())) {
			tempReadOnly = true;
		}
		setComponentAccessType("SukukBondDialog_BondCode", true, this.bondCode, this.space_BondCode, this.label_BondCode, this.hlayout_BondCode,null);		
  		setComponentAccessType("SukukBondDialog_BondDesc", tempReadOnly, this.bondDesc, this.space_BondDesc, this.label_BondDesc, this.hlayout_BondDesc,null);
		setRowInvisible(this.row0, this.hlayout_BondCode,this.hlayout_BondDesc);
		logger.debug("Leaving");
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
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SukukBondDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SukukBondDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SukukBondDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SukukBondDialog_btnSave"));	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.bondCode.setMaxlength(20);
	 	this.bondDesc.setMaxlength(150);
	
	setStatusDetails(gb_statusDetails,groupboxWf,south,enqModule);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSukukBond
	 *            SukukBond
	 */
	public void doWriteBeanToComponents(SukukBond aSukukBond) {
		logger.debug("Entering") ;
		this.bondCode.setValue(aSukukBond.getBondCode());
		this.bondDesc.setValue(aSukukBond.getBondDesc());
	
		this.recordStatus.setValue(aSukukBond.getRecordStatus());
		//this.recordType.setValue(PennantJavaUtil.getLabel(aSukukBond.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSukukBond
	 */
	public void doWriteComponentsToBean(SukukBond aSukukBond) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Code
		try {
		    aSukukBond.setBondCode(this.bondCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Description
		try {
		    aSukukBond.setBondDesc(this.bondDesc.getValue());
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
		if (!this.bondCode.isReadonly()){
			this.bondCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SukukBondDialog_BondCode.value"),PennantRegularExpressions.REGEX_UPPERCASENAME,true));
		}
		//Description
		if (!this.bondDesc.isReadonly()){
			this.bondDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SukukBondDialog_BondDesc.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
	logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.bondCode.setConstraint("");
		this.bondDesc.setConstraint("");
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
		this.bondCode.setErrorMessage("");
		this.bondDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getSukukBondListCtrl().search();
	}

	/**
	 * Deletes a SukukBond object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final SukukBond aSukukBond = new SukukBond();
		BeanUtils.copyProperties(getSukukBond(), aSukukBond);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_SukukBondDialog_BondCode.value")+" : "+aSukukBond.getBondCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSukukBond.getRecordType())){
				aSukukBond.setVersion(aSukukBond.getVersion()+1);
				aSukukBond.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aSukukBond.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aSukukBond.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aSukukBond.getNextTaskId(), aSukukBond);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aSukukBond,tranType)){
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
		
		this.bondCode.setValue("");
		this.bondDesc.setValue("");
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SukukBond aSukukBond = new SukukBond();
		BeanUtils.copyProperties(getSukukBond(), aSukukBond);
		boolean isNew = false;
		
		if(isWorkFlowEnabled()){
			aSukukBond.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aSukukBond.getNextTaskId(), aSukukBond);
		}
		
		// force validation, if on, than execute by component.getValue()
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aSukukBond.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the SukukBond object with the components data
			doWriteComponentsToBean(aSukukBond);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aSukukBond.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSukukBond.getRecordType())){
				aSukukBond.setVersion(aSukukBond.getVersion()+1);
				if(isNew){
					aSukukBond.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aSukukBond.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSukukBond.setNewRecord(true);
				}
			}
		}else{
			aSukukBond.setVersion(aSukukBond.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aSukukBond,tranType)){
				//doWriteBeanToComponents(aSukukBond);
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
	
	private boolean doProcess(SukukBond aSukukBond,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aSukukBond.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aSukukBond.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSukukBond.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			
			aSukukBond.setTaskId(getTaskId());
			aSukukBond.setNextTaskId(getNextTaskId());
			aSukukBond.setRoleCode(getRole());
			aSukukBond.setNextRoleCode(getNextRoleCode());
			
			if (StringUtils.isBlank(getOperationRefs())) {
					processCompleted = doSaveProcess(getAuditHeader(aSukukBond, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aSukukBond, PennantConstants.TRAN_WF);
				
				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aSukukBond, tranType), null);
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
		
		SukukBond aSukukBond = (SukukBond) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getSukukBondService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getSukukBondService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getSukukBondService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aSukukBond.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getSukukBondService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aSukukBond.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SukukBondDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_SukukBondDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("SukukBond",aSukukBond.getBondCode(),aSukukBond.getVersion()),true);
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

	private AuditHeader getAuditHeader(SukukBond aSukukBond, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSukukBond.getBefImage(), aSukukBond);   
		return new AuditHeader(aSukukBond.getBondCode(),null,null,null,auditDetail,aSukukBond.getUserDetails(),getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SukukBond getSukukBond() {
		return this.sukukBond;
	}

	public void setSukukBond(SukukBond sukukBond) {
		this.sukukBond = sukukBond;
	}

	public void setSukukBondService(SukukBondService sukukBondService) {
		this.sukukBondService = sukukBondService;
	}

	public SukukBondService getSukukBondService() {
		return this.sukukBondService;
	}

	public void setSukukBondListCtrl(SukukBondListCtrl sukukBondListCtrl) {
		this.sukukBondListCtrl = sukukBondListCtrl;
	}

	public SukukBondListCtrl getSukukBondListCtrl() {
		return this.sukukBondListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
