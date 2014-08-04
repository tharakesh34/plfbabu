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
 * FileName    		:  AccountEngineRuleDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accountenginerule;

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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.AccountEngineRule;
import com.pennant.backend.service.rmtmasters.AccountEngineRuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/AccountEngineRule/accountEngineRuleDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AccountEngineRuleDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5897198336118086716L;
	private final static Logger logger = Logger.getLogger(AccountEngineRuleDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_AccountEngineRuleDialog; // autoWired
	protected Textbox 	aEEvent; 						// autoWired
	protected Textbox 	aERule; 						// autoWired
	protected Textbox 	aERuleDesc; 					// autoWired
	protected Checkbox 	aEIsSysDefault; 				// autoWired

	protected Label 	recordStatus; 					// autoWired
	protected Radiogroup userAction;
	protected Groupbox 	groupboxWf;
	protected Row 		statusRow;

	// not autoWired variables
	private AccountEngineRule accountEngineRule; // overHanded per parameter
	private transient AccountEngineRuleListCtrl accountEngineRuleListCtrl; // overHanded  per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 	oldVar_aEEvent;
	private transient String 	oldVar_aERule;
	private transient String 	oldVar_aERuleDesc;
	private transient boolean 	oldVar_aEIsSysDefault;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_AccountEngineRuleDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autoWired
	protected Button btnEdit; 	// autoWired
	protected Button btnDelete; // autoWired
	protected Button btnSave; 	// autoWired
	protected Button btnCancel; // autoWired
	protected Button btnClose; 	// autoWired
	protected Button btnHelp; 	// autoWired
	protected Button btnNotes; 	// autoWired

	protected Button btnSearchAEEvent; // autoWired
	protected Textbox lovDescAEEventName;
	private transient String oldVar_lovDescAEEventName;

	// ServiceDAOs / Domain Classes
	private transient AccountEngineRuleService accountEngineRuleService;

	/**
	 * default constructor.<br>
	 */
	public AccountEngineRuleDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountEngineRule
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountEngineRuleDialog(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, 
				this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
				this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("accountEngineRule")) {
			this.accountEngineRule = (AccountEngineRule) args.get("accountEngineRule");
			AccountEngineRule befImage = new AccountEngineRule();
			BeanUtils.copyProperties(this.accountEngineRule, befImage);
			this.accountEngineRule.setBefImage(befImage);
			setAccountEngineRule(this.accountEngineRule);
		} else {
			setAccountEngineRule(null);
		}

		doLoadWorkFlow(this.accountEngineRule.isWorkflow(),
				this.accountEngineRule.getWorkflowId(),this.accountEngineRule.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "AccountEngineRuleDialog");
		}

		// READ OVERHANDED parameters !
		// we get the accountEngineRuleListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete accountEngineRule here.
		if (args.containsKey("accountEngineRuleListCtrl")) {
			setAccountEngineRuleListCtrl((AccountEngineRuleListCtrl) args.get("accountEngineRuleListCtrl"));
		} else {
			setAccountEngineRuleListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getAccountEngineRule());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.aEEvent.setMaxlength(8);
		this.aERule.setMaxlength(8);
		this.aERuleDesc.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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

		getUserWorkspace().alocateAuthorities("AccountEngineRuleDialog");

		this.btnNew.setVisible(getUserWorkspace()
				.isAllowed("button_AccountEngineRuleDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace()
				.isAllowed("button_AccountEngineRuleDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace()
				.isAllowed("button_AccountEngineRuleDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace()
				.isAllowed("button_AccountEngineRuleDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
	public void onClose$window_AccountEngineRuleDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old Var's
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_AccountEngineRuleDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
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
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}

		if(close){
			closeDialog(this.window_AccountEngineRuleDialog, "AccountEngineRule");	
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountEngineRule
	 *            AccountEngineRule
	 */
	public void doWriteBeanToComponents(AccountEngineRule aAccountEngineRule) {
		logger.debug("Entering");

		this.aEEvent.setValue(aAccountEngineRule.getAEEvent());
		this.aERule.setValue(aAccountEngineRule.getAERule());
		this.aERuleDesc.setValue(aAccountEngineRule.getAERuleDesc());
		this.aEIsSysDefault.setChecked(aAccountEngineRule.isAEIsSysDefault());
		if(!aEIsSysDefault.isChecked() && !aAccountEngineRule.isNew()){
			AccountEngineRule aERule = getAccountEngineRuleService().getAccountEngineRuleBySysDft(aAccountEngineRule);
			if(aERule != null){
				this.aEIsSysDefault.setDisabled(true);
			}
		}

		if (aAccountEngineRule.isNewRecord()) {
			this.lovDescAEEventName.setValue("");
		} else {
			this.lovDescAEEventName.setValue(aAccountEngineRule.getAEEvent()
					+ "-" + aAccountEngineRule.getLovDescAEEventName());
		}
		this.recordStatus.setValue(aAccountEngineRule.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccountEngineRule
	 */
	public void doWriteComponentsToBean(AccountEngineRule aAccountEngineRule) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAccountEngineRule.setLovDescAEEventName(this.lovDescAEEventName.getValue());
			aAccountEngineRule.setAEEvent(this.aEEvent.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountEngineRule.setAERule(this.aERule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountEngineRule.setAERuleDesc(this.aERuleDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountEngineRule.setAEIsSysDefault(this.aEIsSysDefault.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aAccountEngineRule.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAccountEngineRule
	 * @throws InterruptedException
	 */
	public void doShowDialog(AccountEngineRule aAccountEngineRule) throws InterruptedException {
		logger.debug("Entering");

		// if aAccountEngineRule == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aAccountEngineRule == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aAccountEngineRule = getAccountEngineRuleService().getNewAccountEngineRule();
			setAccountEngineRule(aAccountEngineRule);
		} else {
			setAccountEngineRule(aAccountEngineRule);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aAccountEngineRule.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.aEEvent.focus();
		} else {
			this.aERuleDesc.focus();
			if (isWorkFlowEnabled()) {
				if (!StringUtils.trimToEmpty(aAccountEngineRule.getRecordType()).equals("")){
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			this.aERule.setReadonly(true);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aAccountEngineRule);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_AccountEngineRuleDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_aEEvent = this.aEEvent.getValue();
		this.oldVar_lovDescAEEventName = this.lovDescAEEventName.getValue();
		this.oldVar_aERule = this.aERule.getValue();
		this.oldVar_aERuleDesc = this.aERuleDesc.getValue();
		this.oldVar_aEIsSysDefault = this.aEIsSysDefault.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		this.aEEvent.setValue(this.oldVar_aEEvent);
		this.lovDescAEEventName.setValue(this.oldVar_lovDescAEEventName);
		this.aERule.setValue(this.oldVar_aERule);
		this.aERuleDesc.setValue(this.oldVar_aERuleDesc);
		this.aEIsSysDefault.setChecked(this.oldVar_aEIsSysDefault);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		
		//To remove Error Messages
		doClearMessage();
		
		if (this.oldVar_aEEvent != this.aEEvent.getValue()) {
			return true;
		}
		if (this.oldVar_aERule != this.aERule.getValue()) {
			return true;
		}
		if (this.oldVar_aERuleDesc != this.aERuleDesc.getValue()) {
			return true;
		}
		if (this.oldVar_aEIsSysDefault != this.aEIsSysDefault.isChecked()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);
		if (!this.aERule.isReadonly()) {
			this.aERule.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountEngineRuleDialog_AERule.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.aERuleDesc.isReadonly()) {
			this.aERuleDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountEngineRuleDialog_AERuleDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.aERule.setConstraint("");
		this.aERuleDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescAEEventName.setConstraint("NO EMPTY:"+ Labels.getLabel(
				"FIELD_NO_EMPTY",new String[] { Labels.getLabel(
						"label_AccountEngineRuleDialog_AEEvent.value") }));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescAEEventName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.aERule.setErrorMessage("");
		this.aERuleDesc.setErrorMessage("");
		this.lovDescAEEventName.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<AccountEngineRule> soAcademic = getAccountEngineRuleListCtrl().getSearchObj();
		getAccountEngineRuleListCtrl().pagingAccountEngineRuleList.setActivePage(0);
		getAccountEngineRuleListCtrl().getPagedListWrapper().setSearchObject(soAcademic);
		if(getAccountEngineRuleListCtrl().listBoxAccountEngineRule!=null){
			getAccountEngineRuleListCtrl().listBoxAccountEngineRule.getListModel();
		}
		logger.debug("Leaving");
	} 

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a AccountEngineRule object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final AccountEngineRule aAccountEngineRule = new AccountEngineRule();
		BeanUtils.copyProperties(getAccountEngineRule(), aAccountEngineRule);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aAccountEngineRule.getAEEvent();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aAccountEngineRule.getRecordType()).equals("")) {
				aAccountEngineRule.setVersion(aAccountEngineRule.getVersion() + 1);
				aAccountEngineRule.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAccountEngineRule.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAccountEngineRule, tranType)) {
					refreshList();
					//Close the Dialog window
					closeDialog(this.window_AccountEngineRuleDialog,"AccountEngineRule");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new AccountEngineRule object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new AccountEngineRule() in the front end.
		// we get it from the back end.
		final AccountEngineRule aAccountEngineRule = getAccountEngineRuleService()
				.getNewAccountEngineRule();
		aAccountEngineRule.setNewRecord(true);
		setAccountEngineRule(aAccountEngineRule);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.aEEvent.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getAccountEngineRule().isNewRecord()) {
			this.btnSearchAEEvent.setDisabled(false);
			this.btnCancel.setVisible(false);
		} else {
			this.btnSearchAEEvent.setDisabled(true);
			this.aERule.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.aERule.setReadonly(isReadOnly("AccountEngineRuleDialog_aERule"));
		this.aERuleDesc.setReadonly(isReadOnly("AccountEngineRuleDialog_aERuleDesc"));
		this.aEIsSysDefault.setDisabled(isReadOnly("AccountEngineRuleDialog_aEIsSysDefault"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.accountEngineRule.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
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

		this.btnSearchAEEvent.setDisabled(true);
		this.aERule.setReadonly(true);
		this.aERuleDesc.setReadonly(true);
		this.aEIsSysDefault.setDisabled(true);

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
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		// remove validation, if there are a save before
		this.aEEvent.setValue("");
		this.lovDescAEEventName.setValue("");
		this.aERule.setValue("");
		this.aERuleDesc.setValue("");
		this.aEIsSysDefault.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final AccountEngineRule aAccountEngineRule = new AccountEngineRule();
		BeanUtils.copyProperties(getAccountEngineRule(), aAccountEngineRule);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the AccountEngineRule object with the components data
		doWriteComponentsToBean(aAccountEngineRule);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aAccountEngineRule.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType =PennantConstants.TRAN_WF;			
			if (StringUtils.trimToEmpty(aAccountEngineRule.getRecordType()).equals("")) {
				aAccountEngineRule.setVersion(aAccountEngineRule.getVersion() + 1);
				if (isNew) {
					aAccountEngineRule.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAccountEngineRule.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAccountEngineRule.setNewRecord(true);
				}
			}
		} else {
			aAccountEngineRule.setVersion(aAccountEngineRule.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aAccountEngineRule, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_AccountEngineRuleDialog, "AccountEngineRule");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAccountEngineRule (AccountEngineRule)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(AccountEngineRule aAccountEngineRule,String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAccountEngineRule.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aAccountEngineRule.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountEngineRule.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aAccountEngineRule.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountEngineRule.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,aAccountEngineRule);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aAccountEngineRule))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aAccountEngineRule.setTaskId(taskId);
			aAccountEngineRule.setNextTaskId(nextTaskId);
			aAccountEngineRule.setRoleCode(getRole());
			aAccountEngineRule.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountEngineRule, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aAccountEngineRule);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAccountEngineRule,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAccountEngineRule, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AccountEngineRule aAccountEngineRule = (AccountEngineRule) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getAccountEngineRuleService().delete(auditHeader);

						deleteNotes=true;	
					} else {
						auditHeader = getAccountEngineRuleService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getAccountEngineRuleService().doApprove(auditHeader);
						
						if(aAccountEngineRule.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getAccountEngineRuleService().doReject(auditHeader);
						
						if(aAccountEngineRule.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_AccountEngineRuleDialog,auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl( this.window_AccountEngineRuleDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					
					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchAEEvent(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(
				this.window_AccountEngineRuleDialog, "AccountEngineEvent");
		if (dataObject instanceof String) {
			this.aEEvent.setValue(dataObject.toString());
			this.lovDescAEEventName.setValue("");
		} else {
			AccountEngineEvent details = (AccountEngineEvent) dataObject;
			if (details != null) {
				this.aEEvent.setValue(details.getAEEventCode());
				this.lovDescAEEventName.setValue(details.getAEEventCode() + "-"
						+ details.getAEEventCodeDesc());
				AccountEngineRule accountEngineRule = new AccountEngineRule();
				accountEngineRule.setAEEvent(this.aEEvent.getValue());
				AccountEngineRule aERule = getAccountEngineRuleService().getAccountEngineRuleBySysDft(accountEngineRule);
				if(aERule != null){
					this.aEIsSysDefault.setChecked(false);
					this.aEIsSysDefault.setDisabled(true);
				}else{
					this.aEIsSysDefault.setChecked(false);
					this.aEIsSysDefault.setDisabled(false);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aAccountEngineRule
	 *            (AccountEngineRule)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(AccountEngineRule aAccountEngineRule,String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountEngineRule.getBefImage(), aAccountEngineRule);   
		return new AuditHeader(String.valueOf(aAccountEngineRule.getId()),null,null,null,
				auditDetail,aAccountEngineRule.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_AccountEngineRuleDialog,auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("AccountEngineRule");
		notes.setReference(getAccountEngineRule().getAEEvent());
		notes.setVersion(getAccountEngineRule().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public AccountEngineRule getAccountEngineRule() {
		return this.accountEngineRule;
	}
	public void setAccountEngineRule(AccountEngineRule accountEngineRule) {
		this.accountEngineRule = accountEngineRule;
	}

	public void setAccountEngineRuleService(
			AccountEngineRuleService accountEngineRuleService) {
		this.accountEngineRuleService = accountEngineRuleService;
	}
	public AccountEngineRuleService getAccountEngineRuleService() {
		return this.accountEngineRuleService;
	}

	public void setAccountEngineRuleListCtrl(
			AccountEngineRuleListCtrl accountEngineRuleListCtrl) {
		this.accountEngineRuleListCtrl = accountEngineRuleListCtrl;
	}
	public AccountEngineRuleListCtrl getAccountEngineRuleListCtrl() {
		return this.accountEngineRuleListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
	
}
