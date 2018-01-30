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
 * FileName    		:  CustomerGroupDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.customergroup;

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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerGroup/customerGroupDialog.zul file.
 */
public class CustomerGroupDialogCtrl extends GFCBaseCtrl<CustomerGroup> {
	private static final long serialVersionUID = 4865083782879144591L;
	private static final Logger logger = Logger.getLogger(CustomerGroupDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_CustomerGroupDialog; // autoWired
	protected Longbox		custGrpID; 					// autoWired
	protected Textbox		custGrpCode; 				// autoWired
	protected Textbox		custGrpDesc; 				// autoWired
   	protected Textbox		custGrpRO1; 				// autoWired
	protected Longbox		custGrpLimit; 				// autoWired
	protected Checkbox 		custGrpIsActive; 			// autoWired


	// not auto wired variables
	private CustomerGroup customerGroup; // overHanded per parameter
	private transient CustomerGroupListCtrl customerGroupListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	protected Button 	btnSearchCustGrpRO1; 	// autowire
	protected Textbox 	lovDescCustGrpRO1Name;
	
	
	// ServiceDAOs / Domain Classes
	private transient CustomerGroupService customerGroupService;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerGroupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerGroupDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerGroup object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerGroupDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerGroupDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		
		// READ OVERHANDED parameters !
		if (arguments.containsKey("customerGroup")) {
			this.customerGroup = (CustomerGroup) arguments.get("customerGroup");
			CustomerGroup befImage =new CustomerGroup();
			BeanUtils.copyProperties(this.customerGroup, befImage);
			this.customerGroup.setBefImage(befImage);
			
			setCustomerGroup(this.customerGroup);
		} else {
			setCustomerGroup(null);
		}
	
		doLoadWorkFlow(this.customerGroup.isWorkflow(),this.customerGroup.getWorkflowId(),this.customerGroup.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerGroupDialog");
		}else{
			getUserWorkspace().allocateAuthorities(super.pageRightName);
		}
	
		// READ OVERHANDED parameters !
		// we get the customerGroupListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerGroup here.
		if (arguments.containsKey("customerGroupListCtrl")) {
			setCustomerGroupListCtrl((CustomerGroupListCtrl) arguments.get("customerGroupListCtrl"));
		} else {
			setCustomerGroupListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerGroup());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.custGrpCode.setMaxlength(8);
		this.custGrpDesc.setMaxlength(50);
		this.custGrpRO1.setMaxlength(8);
	  	this.custGrpLimit.setMaxlength(8);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			
		}else{
			this.groupboxWf.setVisible(false);
			
		}
		logger.debug("Leaving");
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
		logger.debug("Entering");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerGroupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerGroupDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_CustomerGroupDialog);
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
		logger.debug("Entering");
		doWriteBeanToComponents(this.customerGroup.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerGroup
	 *            CustomerGroup
	 */
	public void doWriteBeanToComponents(CustomerGroup aCustomerGroup) {
		logger.debug("Entering");
		this.custGrpID.setValue(aCustomerGroup.getCustGrpID());
		this.custGrpCode.setValue(aCustomerGroup.getCustGrpCode());
		this.custGrpDesc.setValue(aCustomerGroup.getCustGrpDesc());
		this.custGrpRO1.setValue(aCustomerGroup.getCustGrpRO1());
		this.lovDescCustGrpRO1Name.setValue(StringUtils.isBlank(aCustomerGroup.getCustGrpRO1())?""
				:aCustomerGroup.getCustGrpRO1()+PennantConstants.KEY_SEPERATOR+aCustomerGroup.getLovDescCustGrpRO1Name());
	  	this.custGrpLimit.setValue(aCustomerGroup.getCustGrpLimit());
		this.custGrpIsActive.setChecked(aCustomerGroup.isCustGrpIsActive());
		this.recordStatus.setValue(aCustomerGroup.getRecordStatus());
		
		if(aCustomerGroup.isNew() || StringUtils.equals(aCustomerGroup.getRecordType(),PennantConstants.RECORD_TYPE_NEW)){
			this.custGrpIsActive.setChecked(true);
			this.custGrpIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerGroup
	 */
	public void doWriteComponentsToBean(CustomerGroup aCustomerGroup) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aCustomerGroup.setCustGrpID(this.custGrpID.longValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerGroup.setCustGrpCode(this.custGrpCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerGroup.setCustGrpDesc(this.custGrpDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerGroup.setLovDescCustGrpRO1Name(this.lovDescCustGrpRO1Name.getValue());
		    aCustomerGroup.setCustGrpRO1(this.custGrpRO1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerGroup.setCustGrpLimit(this.custGrpLimit.longValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerGroup.setCustGrpIsActive(this.custGrpIsActive.isChecked());
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
		
		aCustomerGroup.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerGroup
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerGroup aCustomerGroup) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCustomerGroup.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custGrpCode.focus();
		} else {
			if (isWorkFlowEnabled()){
				this.custGrpCode.focus();
				if (StringUtils.isNotBlank(aCustomerGroup.getRecordType())) {
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
			doWriteBeanToComponents(aCustomerGroup);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.custGrpCode.isReadonly()){
			this.custGrpCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerGroupDialog_CustGrpCode.value"), PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM,true));
		}	
		if (!this.custGrpDesc.isReadonly()){
			this.custGrpDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerGroupDialog_CustGrpDesc.value"),null,true));
		}	
		if (!this.custGrpLimit.isReadonly()){
			this.custGrpLimit.setConstraint(new PTNumberValidator(Labels.getLabel(
					"label_CustomerGroupDialog_CustGrpLimit.value"), false));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custGrpCode.setConstraint("");
		this.custGrpDesc.setConstraint("");
		this.custGrpLimit.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		this.lovDescCustGrpRO1Name.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerGroupDialog_CustGrpRO1.value"),null,true));
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		this.lovDescCustGrpRO1Name.setConstraint("");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custGrpCode.setErrorMessage("");
		this.custGrpDesc.setErrorMessage("");
		this.lovDescCustGrpRO1Name.setErrorMessage("");
		this.custGrpLimit.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCustGrpRO1(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerGroupDialog, "RelationshipOfficer");
		if (dataObject instanceof String) {
			this.custGrpRO1.setValue(dataObject.toString());
			this.lovDescCustGrpRO1Name.setValue("");
		} else {
			RelationshipOfficer details = (RelationshipOfficer) dataObject;
			if (details != null) {
				this.custGrpRO1.setValue(details.getROfficerCode());
				this.lovDescCustGrpRO1Name.setValue(details.getROfficerCode()
						+ "-" + details.getROfficerDesc());
			}
		}
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a CustomerGroup object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final CustomerGroup aCustomerGroup = new CustomerGroup();
		BeanUtils.copyProperties(getCustomerGroup(), aCustomerGroup);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCustomerGroup.getCustGrpCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerGroup.getRecordType())){
				aCustomerGroup.setVersion(aCustomerGroup.getVersion()+1);
				aCustomerGroup.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aCustomerGroup.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(doProcess(aCustomerGroup,tranType)){
					refreshList();
					closeDialog(); 
				}
			}catch (DataAccessException e){
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
		
		if (getCustomerGroup().isNewRecord()){
		  	this.custGrpID.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.custGrpCode.setReadonly(isReadOnly("CustomerGroupDialog_custGrpCode"));
		}else{
			this.custGrpID.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.custGrpCode.setReadonly(true);
		}
		
			this.custGrpDesc.setReadonly(isReadOnly("CustomerGroupDialog_custGrpDesc"));
			this.custGrpRO1.setReadonly(isReadOnly("CustomerGroupDialog_custGrpRO1"));
			this.btnSearchCustGrpRO1.setDisabled(isReadOnly("CustomerGroupDialog_custGrpRO1"));
			this.custGrpLimit.setReadonly(isReadOnly("CustomerGroupDialog_custGrpLimit"));
			this.custGrpIsActive.setDisabled(isReadOnly("CustomerGroupDialog_custGrpIsActive"));
			
			if (isWorkFlowEnabled()){
				for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.customerGroup.isNewRecord()){
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
		this.custGrpID.setReadonly(true);
		this.custGrpCode.setReadonly(true);
		this.custGrpDesc.setReadonly(true);
		this.custGrpRO1.setReadonly(true);
		this.custGrpLimit.setReadonly(true);
		this.custGrpIsActive.setDisabled(true);
		
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
		this.custGrpCode.setValue("");
		this.custGrpDesc.setValue("");
		this.custGrpRO1.setText("");
		this.custGrpLimit.setText("0");
		this.custGrpIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final CustomerGroup aCustomerGroup = new CustomerGroup();
		BeanUtils.copyProperties(getCustomerGroup(), aCustomerGroup);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerGroup object with the components data
		doWriteComponentsToBean(aCustomerGroup);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aCustomerGroup.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			if (StringUtils.isBlank(aCustomerGroup.getRecordType())){
				aCustomerGroup.setVersion(aCustomerGroup.getVersion()+1);
				if(isNew){
					aCustomerGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerGroup.setNewRecord(true);
				}
			}
		}else{
			aCustomerGroup.setVersion(aCustomerGroup.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aCustomerGroup,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerGroup
	 *            (CustomerGroup)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerGroup aCustomerGroup,String tranType){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aCustomerGroup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerGroup.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerGroup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerGroup);
				}

				if (isNotesMandatory(taskId, aCustomerGroup)) {
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

			aCustomerGroup.setTaskId(taskId);
			aCustomerGroup.setNextTaskId(nextTaskId);
			aCustomerGroup.setRoleCode(getRole());
			aCustomerGroup.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aCustomerGroup, tranType);
			String operationRefs = getServiceOperations(taskId, aCustomerGroup);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerGroup, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerGroup, tranType);
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
		CustomerGroup aCustomerGroup = (CustomerGroup)auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCustomerGroupService().delete(auditHeader);
						deleteNotes = true;
					}else{
						auditHeader = getCustomerGroupService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCustomerGroupService().doApprove(auditHeader);
						
						if (aCustomerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
						
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCustomerGroupService().doReject(auditHeader);
						
						if (aCustomerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
						
					}else{
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerGroupDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerGroupDialog, auditHeader);
				retValue=auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.customerGroup), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerAdditionalDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerGroup aCustomerGroup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerGroup.getBefImage(),aCustomerGroup);
		return new AuditHeader(String.valueOf(aCustomerGroup.getId()), null,
				null, null, auditDetail, aCustomerGroup.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail("",e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CustomerGroupDialog, auditHeader);
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
		doShowNotes(this.customerGroup);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCustomerGroupListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.customerGroup.getId());
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

	public CustomerGroup getCustomerGroup() {
		return this.customerGroup;
	}
	public void setCustomerGroup(CustomerGroup customerGroup) {
		this.customerGroup = customerGroup;
	}

	public void setCustomerGroupService(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}
	public CustomerGroupService getCustomerGroupService() {
		return this.customerGroupService;
	}

	public void setCustomerGroupListCtrl(CustomerGroupListCtrl customerGroupListCtrl) {
		this.customerGroupListCtrl = customerGroupListCtrl;
	}
	public CustomerGroupListCtrl getCustomerGroupListCtrl() {
		return this.customerGroupListCtrl;
	}
	
}
