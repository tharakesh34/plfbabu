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
 * FileName    		:  SecurityGroupDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  10-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securitygroup;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityGroupService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityGroup/securityGroupDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SecurityGroupDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1709997819133952587L;
	private final static Logger logger = Logger.getLogger(SecurityGroupDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window     window_SecurityGroupDialog;                // autoWired
	protected Textbox    grpCode;                                   // autoWired
	protected Textbox    grpDesc;                                   // autoWired
	protected Label      recordStatus;                              // autoWired
	protected Radiogroup userAction;                                // autoWired
	protected Groupbox   groupboxWf;                                // autoWired
	protected Row        statusRow;                                 // autoWired
	protected Button     btnNew;                                    // autoWired
	protected Button     btnEdit;                                   // autoWired
	protected Button     btnDelete;                                 // autoWired
	protected Button     btnSave;                                   // autoWired
	protected Button     btnCancel;                                 // autoWired
	protected Button     btnClose;                                  // autoWired
	protected Button     btnHelp;                                   // autoWired
	protected Button     btnNotes;                                  // autoWired
	
	/* not auto wired variables*/
	private SecurityGroup securityGroup;                           // over handed per parameter
	private transient SecurityGroupListCtrl securityGroupListCtrl; // over handed per parameter
	
	/* old value variables for edit mode. that we can check if something on the
	 *  values are edited since the last initialization.*/
	private transient String  oldVar_grpCode;
	private transient String  oldVar_grpDesc;
	private transient String  oldVar_recordStatus;
	private transient boolean validationOn;
	private boolean           notes_Entered=false;
	
	/* Button controller for the CRUD buttons*/
	private transient final String          btnCtroller_ClassPrefix = "button_SecurityGroupDialog_";
	private transient ButtonStatusCtrl      btnCtrl;
	
	// ServiceDAOs / Domain Classes
	private transient SecurityGroupService securityGroupService;
	private transient PagedListService      pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SecurityGroupDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityGroup object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityGroupDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix
				, true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		/* get the parameters map that are over handed by creation.*/
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("securityGroup")) {
			this.securityGroup = (SecurityGroup) args.get("securityGroup");
			SecurityGroup befImage =new SecurityGroup();
			BeanUtils.copyProperties(this.securityGroup, befImage);
			this.securityGroup.setBefImage(befImage);
			setSecurityGroup(this.securityGroup);
		} else {
			setSecurityGroup(null);
		}

		doLoadWorkFlow(this.securityGroup.isWorkflow(),this.securityGroup.getWorkflowId(),this.securityGroup.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SecurityGroupDialog");
		}

		// READ OVERHANDED parameters !
		// we get the securityGroupListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete securityGroup here.
		if (args.containsKey("securityGroupListCtrl")) {
			setSecurityGroupListCtrl((SecurityGroupListCtrl) args.get("securityGroupListCtrl"));
		} else {
			setSecurityGroupListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSecurityGroup());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		//Empty sent any required attributes
		logger.debug("Entering ");
		this.grpCode.setMaxlength(50);
		this.grpDesc.setMaxlength(100);

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
		getUserWorkspace().alocateAuthorities("SecurityGroupDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_SecurityGroupDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
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
		// remember the old variables
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_SecurityGroupDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering " + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		}catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close=true;

		if (isDataChanged()) {
			logger.debug("doClose isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("doClose isDataChanged : False");
		}

		if(close){
			closeDialog(this.window_SecurityGroupDialog, "SecurityGroup");
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
		doResetInitValues();
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
	 * @param aSecurityGroup
	 *            SecurityGroup
	 */
	public void doWriteBeanToComponents(SecurityGroup aSecurityGroup) {
		logger.debug("Entering ");
		this.grpCode.setValue(aSecurityGroup.getGrpCode());
		this.grpDesc.setValue(aSecurityGroup.getGrpDesc());
		this.recordStatus.setValue(aSecurityGroup.getRecordStatus());
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSecurityGroup
	 */
	public void doWriteComponentsToBean(SecurityGroup aSecurityGroup) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSecurityGroup.setGrpCode(this.grpCode.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSecurityGroup.setGrpDesc(this.grpDesc.getValue());

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

		aSecurityGroup.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSecurityGroup
	 * @throws InterruptedException
	 */
	public void doShowDialog(SecurityGroup aSecurityGroup) throws InterruptedException {
		logger.debug("Entering ");
		
		// if aSecurityGroup == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSecurityGroup == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aSecurityGroup = getSecurityGroupService().getNewSecurityGroup();

			setSecurityGroup(aSecurityGroup);
		} else {
			setSecurityGroup(aSecurityGroup);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aSecurityGroup.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.grpCode.focus();
		} else {
			this.grpCode.focus();
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
			doWriteBeanToComponents(aSecurityGroup);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SecurityGroupDialog);
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_grpCode = this.grpCode.getValue();
		this.oldVar_grpDesc = this.grpDesc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.grpCode.setValue(this.oldVar_grpCode);
		this.grpDesc.setValue(this.oldVar_grpDesc);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving ");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering ");
		doClearMessage();
		boolean changed = false;

		if (!this.oldVar_grpCode.equals(this.grpCode.getValue())) {
			changed = true;
		}
		if (!this.oldVar_grpDesc.equals(this.grpDesc.getValue())) {
			changed = true;
		}
		logger.debug("Leaving ");
		return changed;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.grpCode.isReadonly()){
			this.grpCode.setConstraint(
					new SimpleConstraint(PennantConstants.ALPHANUM_UNDERSCORE_REGEX, Labels.getLabel("MAND_ALPHANUM_UNDERSCORE"
							,new String[]{Labels.getLabel("label_SecurityGroupDialog_GrpCode.value")})));
		}
		
		if (!this.grpDesc.isReadonly()){
			this.grpDesc.setConstraint(
					new SimpleConstraint(PennantConstants.DESC_REGEX, Labels.getLabel("MAND_FIELD_DESC"
							,new String[]{Labels.getLabel("label_SecurityGroupDialog_GrpDesc.value")})));
		}
		
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.grpCode.setConstraint("");
		this.grpDesc.setConstraint("");
		logger.debug("Leaving ");
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
	private void doClearMessage() {
		logger.debug("Entering ");
		this.grpCode.setErrorMessage("");
		this.grpDesc.setErrorMessage("");
		logger.debug("Leaving ");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * Deletes a SecurityGroup object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityGroup aSecurityGroup = new SecurityGroup();
		BeanUtils.copyProperties(getSecurityGroup(), aSecurityGroup);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> "+ aSecurityGroup.getGrpCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSecurityGroup.getRecordType()).equals("")){
				aSecurityGroup.setVersion(aSecurityGroup.getVersion()+1);
				aSecurityGroup.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aSecurityGroup.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(doProcess(aSecurityGroup,tranType)){
					refreshList();
					closeDialog(this.window_SecurityGroupDialog, "SecurityGroup"); 
				}

			}catch (DataAccessException e){
				showMessage(e);
			}
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Create a new SecurityGroup object. <br>
	 */
	private void doNew() {
		logger.debug("Entering ");
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new SecurityGroup() in the front end.
		// we get it from the back end.
		final SecurityGroup aSecurityGroup = getSecurityGroupService().getNewSecurityGroup();
		setSecurityGroup(aSecurityGroup);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.grpCode.focus();
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		if (getSecurityGroup().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.grpCode.setReadonly(true);
			this.btnCancel.setVisible(true);

		}
		this.grpDesc.setReadonly(isReadOnly("SecurityGroupDialog_grpDesc"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.securityGroup.isNewRecord()){
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
		this.grpCode.setReadonly(true);
		this.grpDesc.setReadonly(true);

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
		this.grpCode.setValue("");
		this.grpDesc.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final SecurityGroup aSecurityGroup = new SecurityGroup();
		BeanUtils.copyProperties(getSecurityGroup(), aSecurityGroup);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SecurityGroup object with the components data
		doWriteComponentsToBean(aSecurityGroup);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSecurityGroup.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSecurityGroup.getRecordType()).equals("")){
				aSecurityGroup.setVersion(aSecurityGroup.getVersion()+1);
				if(isNew){
					aSecurityGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aSecurityGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSecurityGroup.setNewRecord(true);
				}
			}
		}else{
			aSecurityGroup.setVersion(aSecurityGroup.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aSecurityGroup,tranType)){
				refreshList();
				closeDialog(this.window_SecurityGroupDialog, "SecurityGroup");
			}
		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * This Method used for setting all workFlow details from userWorkSpace 
	 * and setting audit details to auditHeader
	 * @param aSecurityGroup
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(SecurityGroup aSecurityGroup,String tranType){
		logger.debug("Entering ");;
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aSecurityGroup.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSecurityGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityGroup.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aSecurityGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityGroup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSecurityGroup);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId,aSecurityGroup))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aSecurityGroup.setTaskId(taskId);
			aSecurityGroup.setNextTaskId(nextTaskId);
			aSecurityGroup.setRoleCode(getRole());
			aSecurityGroup.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aSecurityGroup, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aSecurityGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aSecurityGroup, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aSecurityGroup, tranType);
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
		SecurityGroup aSecurityGroup=(SecurityGroup)auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getSecurityGroupService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getSecurityGroupService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getSecurityGroupService().doApprove(auditHeader);

						if(aSecurityGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getSecurityGroupService().doReject(auditHeader);
						
						if(aSecurityGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_SecurityGroupDialog, auditHeader);
						return processCompleted; 
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_SecurityGroupDialog,auditHeader);

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					
					if(deleteNotes){
						deleteNotes(getNotes(),true);
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
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
   	 *	This method creates and returns Audit header Object
	 * @param aSecurityGroup
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityGroup aSecurityGroup, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityGroup.getBefImage(),aSecurityGroup);   
		return new AuditHeader(String.valueOf(aSecurityGroup.getId()),null,null,null
				,auditDetail,aSecurityGroup.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_SecurityGroupDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
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
		logger.debug("Entering " + event.toString());

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Sets the notes 
	 * @param notes
	 */
	public void setNotes_entered(String notes) {
		logger.debug("Entering ");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * This method creates Notes Object ,sets data and returns that Object
	 * @return notes (Notes)
	 */
	private Notes getNotes(){
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("SecurityGroup");
		notes.setReference(String.valueOf(getSecurityGroup().getGrpID()));
		notes.setVersion(getSecurityGroup().getVersion());
		logger.debug("Leaving ");
		return notes;
	}
		
	/**
	 * Refreshes the list
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<SecurityGroup> soAcademic = getSecurityGroupListCtrl().getSearchObj();
		getSecurityGroupListCtrl().pagingSecurityGroupList.setActivePage(0);
		getSecurityGroupListCtrl().getPagedListWrapper().setSearchObject(soAcademic);
		if(getSecurityGroupListCtrl().listBoxSecurityGroup!=null){
			getSecurityGroupListCtrl().listBoxSecurityGroup.getListModel();
		}
		logger.debug("Leaving");
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

	public SecurityGroup getSecurityGroup() {
		return this.securityGroup;
	}
	public void setSecurityGroup(SecurityGroup securityGroup) {
		this.securityGroup = securityGroup;
	}

	public void setSecurityGroupService(SecurityGroupService securityGroupService) {
		this.securityGroupService = securityGroupService;
	}
	public SecurityGroupService getSecurityGroupService() {
		return this.securityGroupService;
	}

	public void setSecurityGroupListCtrl(SecurityGroupListCtrl securityGroupListCtrl) {
		this.securityGroupListCtrl = securityGroupListCtrl;
	}
	public SecurityGroupListCtrl getSecurityGroupListCtrl() {
		return this.securityGroupListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

}
