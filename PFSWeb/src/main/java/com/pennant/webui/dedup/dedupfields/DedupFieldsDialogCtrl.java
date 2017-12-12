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
 * FileName    		:  DedupFieldsDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.dedup.dedupfields;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.dedup.DedupFieldsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Dedup/DedupFields/dedupFieldsDialog.zul file.
 */
public class DedupFieldsDialogCtrl extends GFCBaseCtrl<DedupFields> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DedupFieldsDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DedupFieldsDialog; 

	protected Textbox fieldName; 
 	protected Combobox fieldControl; 

	// not auto wired vars
	private DedupFields dedupFields; // overhanded per param
	private transient DedupFieldsListCtrl dedupFieldsListCtrl; // overhanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient DedupFieldsService dedupFieldsService;
	private transient PagedListService pagedListService;
	
	private List<ValueLabel> listFieldControl=PennantStaticListUtil.getFieldTypeList(); 

	/**
	 * default constructor.<br>
	 */
	public DedupFieldsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DedupFieldsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected DedupFields object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DedupFieldsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DedupFieldsDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("dedupFields")) {
			this.dedupFields = (DedupFields) arguments.get("dedupFields");
			DedupFields befImage =new DedupFields();
			BeanUtils.copyProperties(this.dedupFields, befImage);
			this.dedupFields.setBefImage(befImage);
			
			setDedupFields(this.dedupFields);
		} else {
			setDedupFields(null);
		}
	
		doLoadWorkFlow(this.dedupFields.isWorkflow(),this.dedupFields.getWorkflowId(),this.dedupFields.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "DedupFieldsDialog");
		}

		setListFieldControl();
	
		// READ OVERHANDED params !
		// we get the dedupFieldsListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete dedupFields here.
		if (arguments.containsKey("dedupFieldsListCtrl")) {
			setDedupFieldsListCtrl((DedupFieldsListCtrl) arguments.get("dedupFieldsListCtrl"));
		} else {
			setDedupFieldsListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getDedupFields());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.fieldName.setMaxlength(50);
		
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
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DedupFieldsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DedupFieldsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DedupFieldsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DedupFieldsDialog_btnSave"));
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
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_DedupFieldsDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
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
		doWriteBeanToComponents(this.dedupFields.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDedupFields
	 *            DedupFields
	 */
	public void doWriteBeanToComponents(DedupFields aDedupFields) {
		logger.debug("Entering") ;
		this.fieldName.setValue(aDedupFields.getFieldName());
		this.fieldControl.setValue(PennantAppUtil.getlabelDesc(aDedupFields.getFieldControl(),listFieldControl));
	
		this.recordStatus.setValue(aDedupFields.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDedupFields
	 */
	public void doWriteComponentsToBean(DedupFields aDedupFields) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aDedupFields.setFieldName(this.fieldName.getValue());
		    
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDedupFields.setFieldControl(this.fieldControl.getValue());
		    
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
		
		aDedupFields.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDedupFields
	 * @throws Exception
	 */
	public void doShowDialog(DedupFields aDedupFields) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aDedupFields.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fieldName.focus();
		} else {
			this.fieldControl.focus();
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
			doWriteBeanToComponents(aDedupFields);
			
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
		logger.debug("Enterring");
		setValidationOn(true);
		
		if (!this.fieldName.isReadonly()){
			this.fieldName.setConstraint(new PTStringValidator(Labels.getLabel("label_DedupFieldsDialog_FieldName.value"),null,true));
		}	
		if (!this.fieldControl.isDisabled()){
			this.fieldControl.setConstraint(new StaticListValidator(listFieldControl,Labels.getLabel("label_DedupFieldsDialog_FieldControl.value")));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Enterring");
		setValidationOn(false);
		this.fieldName.setConstraint("");
		this.fieldControl.setConstraint("");
	logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a DedupFields object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	private void doDelete() throws InterruptedException {
		logger.debug("Enterring");	
		final DedupFields aDedupFields = new DedupFields();
		BeanUtils.copyProperties(getDedupFields(), aDedupFields);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aDedupFields.getFieldName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDedupFields.getRecordType())){
				aDedupFields.setVersion(aDedupFields.getVersion()+1);
				aDedupFields.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aDedupFields.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aDedupFields,tranType)){

					final JdbcSearchObject<DedupFields> soDedupFields = getDedupFieldsListCtrl().getSearchObj();
					// Set the ListModel
					getDedupFieldsListCtrl().getPagedListWrapper().setSearchObject(soDedupFields);

					// now synchronize the DedupFields listBox
					final ListModelList lml = (ListModelList) getDedupFieldsListCtrl().listBoxDedupFields.getListModel();

					// Check if the DedupFields object is new or updated -1
					// means that the obj is not in the list, so it's new ..
					if (lml.indexOf(aDedupFields) == -1) {
					} else {
						lml.remove(lml.indexOf(aDedupFields));
					}
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
		logger.debug("Enterring");
		
		if (getDedupFields().isNewRecord()){
		  	this.fieldName.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.fieldName.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
	 	this.fieldControl.setDisabled(isReadOnly("DedupFieldsDialog_fieldControl"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.dedupFields.isNewRecord()){
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
		logger.debug("Enterring");
		this.fieldName.setReadonly(true);
		this.fieldControl.setDisabled(true);
		
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
		logger.debug("Enterring");
		// remove validation, if there are a save before
		
		this.fieldName.setValue("");
		this.fieldControl.setValue("");
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Enterring");
		final DedupFields aDedupFields = new DedupFields();
		BeanUtils.copyProperties(getDedupFields(), aDedupFields);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DedupFields object with the components data
		doWriteComponentsToBean(aDedupFields);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aDedupFields.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDedupFields.getRecordType())){
				aDedupFields.setVersion(aDedupFields.getVersion()+1);
				if(isNew){
					aDedupFields.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aDedupFields.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDedupFields.setNewRecord(true);
				}
			}
		}else{
			aDedupFields.setVersion(aDedupFields.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aDedupFields,tranType)){
				doWriteBeanToComponents(aDedupFields);
				// ++ create the searchObject and init sorting ++ //
				final JdbcSearchObject<DedupFields> soDedupFields = getDedupFieldsListCtrl().getSearchObj();

				// Set the ListModel
				getDedupFieldsListCtrl().pagingDedupFieldsList.setActivePage(0);
				getDedupFieldsListCtrl().getPagedListWrapper().setSearchObject(soDedupFields);

				// call from cusromerList then synchronize the DedupFields listBox
				if (getDedupFieldsListCtrl().listBoxDedupFields != null) {
					// now synchronize the DedupFields listBox
					getDedupFieldsListCtrl().listBoxDedupFields.getListModel();
				}

				doReadOnly();
				this.btnCtrl.setBtnStatus_Save();

				// Close the Existing Dialog
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(DedupFields aDedupFields,String tranType){
		logger.debug("Enterring");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aDedupFields.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDedupFields.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDedupFields.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDedupFields.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDedupFields.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDedupFields);
				}

				if (isNotesMandatory(taskId, aDedupFields)) {
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
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aDedupFields.setTaskId(taskId);
			aDedupFields.setNextTaskId(nextTaskId);
			aDedupFields.setRoleCode(getRole());
			aDedupFields.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aDedupFields, tranType);
			
			String operationRefs = getServiceOperations(taskId, aDedupFields);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aDedupFields, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aDedupFields, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	

	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Enterring");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getDedupFieldsService().delete(auditHeader);
					}else{
						auditHeader = getDedupFieldsService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getDedupFieldsService().doApprove(auditHeader);
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getDedupFieldsService().doReject(auditHeader);
						deleteNotes();
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DedupFieldsDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(this.window_DedupFieldsDialog, auditHeader);
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
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
		
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	

   private void setListFieldControl(){
		for (int i = 0; i < listFieldControl.size(); i++) {
			
			   Comboitem comboitem = new Comboitem();
			   comboitem = new Comboitem();
			   comboitem.setLabel(listFieldControl.get(i).getLabel());
			   comboitem.setValue(listFieldControl.get(i).getValue());
			   this.fieldControl.appendChild(comboitem);
		} 
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

	public DedupFields getDedupFields() {
		return this.dedupFields;
	}

	public void setDedupFields(DedupFields dedupFields) {
		this.dedupFields = dedupFields;
	}

	public void setDedupFieldsService(DedupFieldsService dedupFieldsService) {
		this.dedupFieldsService = dedupFieldsService;
	}

	public DedupFieldsService getDedupFieldsService() {
		return this.dedupFieldsService;
	}

	public void setDedupFieldsListCtrl(DedupFieldsListCtrl dedupFieldsListCtrl) {
		this.dedupFieldsListCtrl = dedupFieldsListCtrl;
	}

	public DedupFieldsListCtrl getDedupFieldsListCtrl() {
		return this.dedupFieldsListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	private AuditHeader getAuditHeader(DedupFields aDedupFields, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDedupFields.getBefImage(), aDedupFields);   
		return new AuditHeader(aDedupFields.getFieldName(),null,null,null,auditDetail,aDedupFields.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("",e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_DedupFieldsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}
	
	
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.dedupFields);
	}
	
	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}
	
	private void deleteNotes(){
		NotesService notesService= (NotesService) SpringUtil.getBean("notesService");		
		notesService.delete(getNotes(this.dedupFields));
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.dedupFields.getFieldName());
	}

	
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
			this.fieldName.setErrorMessage("");
			this.fieldControl.setErrorMessage("");
	logger.debug("Leaving");
	}
	
}
