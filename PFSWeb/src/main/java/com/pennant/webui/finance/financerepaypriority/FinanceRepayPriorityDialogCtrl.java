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
 * FileName    		:  FinanceRepayPriorityDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-03-2012    														*
 *                                                                  						*
 * Modified Date    :  16-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-03-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financerepaypriority;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceRepayPriorityService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceRepayPriority/financeRepayPriorityDialog.zul file.
 */
public class FinanceRepayPriorityDialogCtrl extends GFCBaseCtrl<FinanceRepayPriority> {
	private static final long serialVersionUID = 2259700048994840972L;
	private static final Logger logger = Logger.getLogger(FinanceRepayPriorityDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceRepayPriorityDialog; // autowired
	protected Textbox finType; // autowired
	protected Intbox finPriority; // autowired

	// not auto wired vars
	private FinanceRepayPriority financeRepayPriority; // overhanded per param
	private transient FinanceRepayPriorityListCtrl financeRepayPriorityListCtrl; // overhanded per param

	private transient boolean validationOn;
	
	protected Button btnSearchFinType; // autowire
	protected Textbox lovDescFinTypeName;
	

	// ServiceDAOs / Domain Classes
	private transient FinanceRepayPriorityService financeRepayPriorityService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();


	/**
	 * default constructor.<br>
	 */
	public FinanceRepayPriorityDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceRepayPriorityDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceRepayPriority object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceRepayPriorityDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceRepayPriorityDialog);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();


			// READ OVERHANDED params !
			if (arguments.containsKey("financeRepayPriority")) {
				this.financeRepayPriority = (FinanceRepayPriority) arguments
						.get("financeRepayPriority");
				FinanceRepayPriority befImage = new FinanceRepayPriority();
				BeanUtils.copyProperties(this.financeRepayPriority, befImage);
				this.financeRepayPriority.setBefImage(befImage);

				setFinanceRepayPriority(this.financeRepayPriority);
			} else {
				setFinanceRepayPriority(null);
			}

			doLoadWorkFlow(this.financeRepayPriority.isWorkflow(),
					this.financeRepayPriority.getWorkflowId(),
					this.financeRepayPriority.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"FinanceRepayPriorityDialog");
			}

			// READ OVERHANDED params !
			// we get the financeRepayPriorityListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete financeRepayPriority here.
			if (arguments.containsKey("financeRepayPriorityListCtrl")) {
				setFinanceRepayPriorityListCtrl((FinanceRepayPriorityListCtrl) arguments
						.get("financeRepayPriorityListCtrl"));
			} else {
				setFinanceRepayPriorityListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinanceRepayPriority());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceRepayPriorityDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finPriority.setMaxlength(4);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_FinanceRepayPriorityDialog);
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
		doWriteBeanToComponents(this.financeRepayPriority.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceRepayPriority
	 *            FinanceRepayPriority
	 */
	public void doWriteBeanToComponents(FinanceRepayPriority aFinanceRepayPriority) {
		logger.debug("Entering") ;
		this.finType.setValue(aFinanceRepayPriority.getFinType());
		this.finPriority.setValue(aFinanceRepayPriority.getFinPriority());

		if (aFinanceRepayPriority.isNewRecord()){
			this.lovDescFinTypeName.setValue("");
		}else{
			this.lovDescFinTypeName.setValue(aFinanceRepayPriority.getFinType()+"-"+aFinanceRepayPriority.getLovDescFinTypeName());
		}
		this.recordStatus.setValue(aFinanceRepayPriority.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceRepayPriority
	 */
	public void doWriteComponentsToBean(FinanceRepayPriority aFinanceRepayPriority) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinanceRepayPriority.setLovDescFinTypeName(this.lovDescFinTypeName.getValue());
			aFinanceRepayPriority.setFinType(this.finType.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceRepayPriority.setFinPriority(this.finPriority.getValue());
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

		aFinanceRepayPriority.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceRepayPriority
	 * @throws Exception
	 */
	public void doShowDialog(FinanceRepayPriority aFinanceRepayPriority) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aFinanceRepayPriority.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.finPriority.focus();
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
			doWriteBeanToComponents(aFinanceRepayPriority);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_FinanceRepayPriorityDialog.onClose();
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

		if (!this.finPriority.isReadonly()){
			this.finPriority.setConstraint(new PTNumberValidator(Labels.getLabel("label_FinanceRepayPriorityDialog_FinPriority.value"), true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finPriority.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a FinanceRepayPriority object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinanceRepayPriority aFinanceRepayPriority = new FinanceRepayPriority();
		BeanUtils.copyProperties(getFinanceRepayPriority(), aFinanceRepayPriority);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "+ 
				Labels.getLabel("label_FinanceRepayPriorityDialog_FinType.value")+" : "+aFinanceRepayPriority.getFinType()+"-"+aFinanceRepayPriority.getLovDescFinTypeName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinanceRepayPriority.getRecordType())){
				aFinanceRepayPriority.setVersion(aFinanceRepayPriority.getVersion()+1);
				aFinanceRepayPriority.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aFinanceRepayPriority.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFinanceRepayPriority,tranType)){
					refreshList();
					closeDialog(); 
				}

			} catch (DataAccessException e) {
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

		if (getFinanceRepayPriority().isNewRecord()){
			this.btnSearchFinType.setDisabled(false);
			this.btnCancel.setVisible(false);
		}else{
			this.btnSearchFinType.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("FinanceRepayPriorityDialog_finPriority"), this.finPriority);

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeRepayPriority.isNewRecord()){
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
		this.btnSearchFinType.setDisabled(true);
		this.finPriority.setReadonly(true);

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

		this.finType.setValue("");
		this.lovDescFinTypeName.setValue("");
		this.finPriority.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceRepayPriority aFinanceRepayPriority = new FinanceRepayPriority();
		BeanUtils.copyProperties(getFinanceRepayPriority(), aFinanceRepayPriority);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinanceRepayPriority object with the components data
		doWriteComponentsToBean(aFinanceRepayPriority);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aFinanceRepayPriority.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceRepayPriority.getRecordType())){
				aFinanceRepayPriority.setVersion(aFinanceRepayPriority.getVersion()+1);
				if(isNew){
					aFinanceRepayPriority.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceRepayPriority.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceRepayPriority.setNewRecord(true);
				}
			}
		}else{
			aFinanceRepayPriority.setVersion(aFinanceRepayPriority.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aFinanceRepayPriority,tranType)){
				doWriteBeanToComponents(aFinanceRepayPriority);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(FinanceRepayPriority aFinanceRepayPriority,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aFinanceRepayPriority.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceRepayPriority.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceRepayPriority.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinanceRepayPriority.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceRepayPriority.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinanceRepayPriority);
				}

				if (isNotesMandatory(taskId, aFinanceRepayPriority)) {
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

			aFinanceRepayPriority.setTaskId(taskId);
			aFinanceRepayPriority.setNextTaskId(nextTaskId);
			aFinanceRepayPriority.setRoleCode(getRole());
			aFinanceRepayPriority.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aFinanceRepayPriority, tranType);

			String operationRefs = getServiceOperations(taskId, aFinanceRepayPriority);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aFinanceRepayPriority, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aFinanceRepayPriority, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		FinanceRepayPriority aFinanceRepayPriority = (FinanceRepayPriority) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinanceRepayPriorityService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getFinanceRepayPriorityService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinanceRepayPriorityService().doApprove(auditHeader);

						if(aFinanceRepayPriority.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinanceRepayPriorityService().doReject(auditHeader);
						if(aFinanceRepayPriority.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceRepayPriorityDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_FinanceRepayPriorityDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.financeRepayPriority),true);
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

	public void onClick$btnSearchFinType(Event event){

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceRepayPriorityDialog,"FinanceType");
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		}else{
			FinanceType details= (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.lovDescFinTypeName.setValue(details.getFinType()+"-"+details.getFinTypeDesc());
			}
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

	public FinanceRepayPriority getFinanceRepayPriority() {
		return this.financeRepayPriority;
	}

	public void setFinanceRepayPriority(FinanceRepayPriority financeRepayPriority) {
		this.financeRepayPriority = financeRepayPriority;
	}

	public void setFinanceRepayPriorityService(FinanceRepayPriorityService financeRepayPriorityService) {
		this.financeRepayPriorityService = financeRepayPriorityService;
	}

	public FinanceRepayPriorityService getFinanceRepayPriorityService() {
		return this.financeRepayPriorityService;
	}

	public void setFinanceRepayPriorityListCtrl(FinanceRepayPriorityListCtrl financeRepayPriorityListCtrl) {
		this.financeRepayPriorityListCtrl = financeRepayPriorityListCtrl;
	}

	public FinanceRepayPriorityListCtrl getFinanceRepayPriorityListCtrl() {
		return this.financeRepayPriorityListCtrl;
	}

	private AuditHeader getAuditHeader(FinanceRepayPriority aFinanceRepayPriority, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceRepayPriority.getBefImage(), aFinanceRepayPriority);   
		return new AuditHeader(aFinanceRepayPriority.getFinType(),null,null,null,auditDetail,aFinanceRepayPriority.getUserDetails(),getOverideMap());
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinanceRepayPriorityDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}


	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeRepayPriority);
	}

	private void doSetLOVValidation() {
		this.lovDescFinTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceRepayPriorityDialog_FinType.value"), null, true));
	}
	private void doRemoveLOVValidation() {
		this.lovDescFinTypeName.setConstraint("");
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.financeRepayPriority.getFinType());
	}


	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finPriority.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getFinanceRepayPriorityListCtrl().search();
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

}
