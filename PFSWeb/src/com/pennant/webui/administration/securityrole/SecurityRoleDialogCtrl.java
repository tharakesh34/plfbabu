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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.administration.SecurityRoleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Administration/SecurityRole/securityRoleDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SecurityRoleDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8969578420575594907L;
	private final static Logger logger = Logger.getLogger(SecurityRoleDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window     window_SecurityRoleDialog;                   // autoWired
	protected Combobox   roleApp;                                     // autoWired
	protected Textbox    roleCd;                                      // autoWired
	protected Textbox    roleDesc;                                    // autoWired
	protected Textbox    roleCategory;                                // autoWired
	protected Label      recordStatus;                                // autoWired
	protected Radiogroup userAction;                                  // autoWired
	protected Groupbox   groupboxWf;                                  // autoWired
	protected Row        statusRow;                                   // autoWired
	protected Button     btnNew;                                      // autoWired
	protected Button     btnEdit;                                     // autoWired
	protected Button     btnDelete;                                   // autoWired
	protected Button     btnSave;                                     // autoWired
	protected Button     btnCancel;                                   // autoWired
	protected Button     btnClose;                                    // autoWired
	protected Button     btnHelp;                                     // autoWired
	protected Button     btnNotes;                                    // autoWired

	// not auto wired variables
	private SecurityRole     securityRole;                              // overHanded per parameter
	private transient    SecurityRoleListCtrl securityRoleListCtrl; 	// overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String   oldVar_roleApp;
	private transient String   oldVar_roleCd;
	private transient String   oldVar_roleDesc;
	private transient String   oldVar_roleCategory;
	private transient String   oldVar_recordStatus;
	private transient boolean  validationOn;
	private boolean            notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String          btnCtroller_ClassPrefix = "button_SecurityRoleDialog_";
	private transient ButtonStatusCtrl      btnCtrl;

	// ServiceDAOs / Domain Classes
	private transient SecurityRoleService  securityRoleService;
	private transient PagedListService      pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SecurityRoleDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityRole object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SecurityRoleDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix
				, true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("securityRole")) {
			this.securityRole = (SecurityRole) args.get("securityRole");
			SecurityRole befImage =new SecurityRole();
			BeanUtils.copyProperties(this.securityRole, befImage);
			this.securityRole.setBefImage(befImage);
			setSecurityRole(this.securityRole);
		} else {
			setSecurityRole(null);
		}

		doLoadWorkFlow(this.securityRole.isWorkflow(),this.securityRole.getWorkflowId(),this.securityRole.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SecurityRoleDialog");
		}

		// READ OVERHANDED parameters !
		// we get the securityRoleListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete securityRole here.
		if (args.containsKey("securityRoleListCtrl")) {
			setSecurityRoleListCtrl((SecurityRoleListCtrl) args.get("securityRoleListCtrl"));
		} else {
			setSecurityRoleListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSecurityRole());
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

		getUserWorkspace().alocateAuthorities("SecurityRoleDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityRoleDialog_btnSave"));
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
	public void onClose$window_SecurityRoleDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_SecurityRoleDialog);
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
		} catch (final WrongValueException e) {
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
			int conf = MultiLineMessageBox.show(msg, title,	MultiLineMessageBox.YES
					| MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_SecurityRoleDialog, "SecurityRole");
		}
		logger.debug("Leaving ");
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
			if (StringUtils.trimToEmpty(strRoleApp).equalsIgnoreCase("")) {
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(SecurityRole aSecurityRole) throws InterruptedException {
		logger.debug("Entering ");

		// if aSecurityRole == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSecurityRole == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aSecurityRole = getSecurityRoleService().getNewSecurityRole();

			setSecurityRole(aSecurityRole);
		} else {
			setSecurityRole(aSecurityRole);
		}

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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SecurityRoleDialog);
			//this.window_SecurityRoleDialog.doModal();
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
		this.oldVar_roleApp = this.roleApp.getValue();
		this.oldVar_roleCd = this.roleCd.getValue();
		this.oldVar_roleDesc = this.roleDesc.getValue();
		this.oldVar_roleCategory = this.roleCategory.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.roleApp.setValue(this.oldVar_roleApp);
		this.roleCd.setValue(this.oldVar_roleCd);
		this.roleDesc.setValue(this.oldVar_roleDesc);
		this.roleCategory.setValue(this.oldVar_roleCategory);
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
		if (!this.oldVar_roleApp .equals(this.roleApp.getValue())) {
			changed = true;
		}
		if (!this.oldVar_roleCd .equals(this.roleCd.getValue())) {
			changed = true;
		}
		if (!this.oldVar_roleDesc.equals(this.roleDesc.getValue())) {
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

		if (!this.roleApp.isDisabled()) {
			this.roleApp.setConstraint(new StaticListValidator(PennantStaticListUtil.getAppCodes()
					,Labels.getLabel("label_SecurityRoleDialog_RoleApp.value")));
		}

		if (!this.roleCd.isReadonly()){
			this.roleCd.setConstraint(new PTStringValidator(Labels.getLabel("label_SecurityRoleDialog_RoleCd.value"),PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
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
	private void doClearMessage() {
		logger.debug("Entering");
		this.roleCd.setErrorMessage("");
		this.roleApp.setErrorMessage("");
		this.roleDesc.setErrorMessage("");
		this.roleCategory.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> "+ aSecurityRole.getRoleCd();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSecurityRole.getRecordType()).equals("")){
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
					closeDialog(this.window_SecurityRoleDialog, "SecurityRole"); 
				}

			}catch (DataAccessException e){
				showMessage(e);
			}

		}
		logger.debug("Leaving ");
	}

	/**
	 * Create a new SecurityRole object. <br>
	 */
	private void doNew() {
		logger.debug("Entering ");

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new SecurityRole() in the front end.
		// we get it from the back end.
		final SecurityRole aSecurityRole = getSecurityRoleService().getNewSecurityRole();
		setSecurityRole(aSecurityRole);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.roleCd.focus();
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aSecurityRole.getRecordType()).equals("")){
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
				closeDialog(this.window_SecurityRoleDialog, "SecurityRole");
			}

		} catch (final DataAccessException e) {
			showMessage(e);
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

		aSecurityRole.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSecurityRole.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSecurityRole.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aSecurityRole.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSecurityRole.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSecurityRole);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId,aSecurityRole))) {
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
			aSecurityRole.setTaskId(taskId);
			aSecurityRole.setNextTaskId(nextTaskId);
			aSecurityRole.setRoleCode(getRole());
			aSecurityRole.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aSecurityRole, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aSecurityRole);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_SecurityRoleDialog,auditHeader);
						return processCompleted; 
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SecurityRoleDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

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
	 * This method creates AudiHeader Object and returns that Object 
	 * @param aSecurityRole
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityRole aSecurityRole, String tranType){
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityRole.getBefImage(), aSecurityRole);   
		return new AuditHeader(String.valueOf(aSecurityRole.getId()),null,null,null,auditDetail
				,aSecurityRole.getUserDetails(),getOverideMap());
	}

	/**
	 * This method shows Message box with error message
	 * @param e
	 */
	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_SecurityRoleDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * When user Clicks on "Notes" button
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
	 * This method Creates Notes Object ,sets data and returns that Object
	 * @return notes (Notes)
	 */
	private Notes getNotes(){
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("SecRole");
		notes.setReference(String.valueOf(getSecurityRole().getRoleID()));
		notes.setVersion(getSecurityRole().getVersion());
		logger.debug("Leaving ");
		return notes;
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
	 * Refreshes the list
	 */
	private void refreshList(){
		final JdbcSearchObject<SecurityRole> soAcademic = getSecurityRoleListCtrl().getSearchObj();
		getSecurityRoleListCtrl().pagingSecurityRoleList.setActivePage(0);
		getSecurityRoleListCtrl().getPagedListWrapper().setSearchObject(soAcademic);
		if(getSecurityRoleListCtrl().listBoxSecurityRole!=null){
			getSecurityRoleListCtrl().listBoxSecurityRole.getListModel();
		}
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}
}
