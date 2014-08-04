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
 * FileName    		:  AccountEngineEventDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.bmtmasters.accountengineevent;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.AccountEngineEventService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/BMTMasters/AccountEngineEvent/accountEngineEventDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AccountEngineEventDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5231127902551957898L;
	private final static Logger logger = Logger.getLogger(AccountEngineEventDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_AccountEngineEventDialog;	// autoWired
	protected Textbox 		aEEventCode; 						// autoWired
	protected Textbox 		aEEventCodeDesc; 					// autoWired

	protected Label 		recordStatus; 						// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired Var's
	private AccountEngineEvent accountEngineEvent; 			// overHanded per parameter
	private transient AccountEngineEventListCtrl accountEngineEventListCtrl; // overHanded per parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String oldVar_aEEventCode;
	private transient String oldVar_aEEventCodeDesc;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_AccountEngineEventDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWired
	protected Button btnEdit; 		// autoWired
	protected Button btnDelete; 	// autoWired
	protected Button btnSave; 		// autoWired
	protected Button btnCancel; 	// autoWired
	protected Button btnClose; 		// autoWired
	protected Button btnHelp; 		// autoWired
	protected Button btnNotes; 		// autoWired

	// ServiceDAOs / Domain Classes
	private transient AccountEngineEventService accountEngineEventService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public AccountEngineEventDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountEngineEvent
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountEngineEventDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("accountEngineEvent")) {
			this.accountEngineEvent = (AccountEngineEvent) args.get("accountEngineEvent");
			AccountEngineEvent befImage = new AccountEngineEvent();
			BeanUtils.copyProperties(this.accountEngineEvent, befImage);
			this.accountEngineEvent.setBefImage(befImage);

			setAccountEngineEvent(this.accountEngineEvent);
		} else {
			setAccountEngineEvent(null);
		}

		doLoadWorkFlow(this.accountEngineEvent.isWorkflow(), this.accountEngineEvent.getWorkflowId(), 
				this.accountEngineEvent.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"AccountEngineEventDialog");
		}

		// READ OVERHANDED parameters !
		// we get the accountEngineEventListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete accountEngineEvent here.
		if (args.containsKey("accountEngineEventListCtrl")) {
			setAccountEngineEventListCtrl((AccountEngineEventListCtrl) args.get("accountEngineEventListCtrl"));
		} else {
			setAccountEngineEventListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getAccountEngineEvent());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.aEEventCode.setMaxlength(8);
		this.aEEventCodeDesc.setMaxlength(50);

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
		getUserWorkspace().alocateAuthorities("AccountEngineEventDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountEngineEventDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountEngineEventDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AccountEngineEventDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountEngineEventDialog_btnSave"));
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
	public void onClose$window_AccountEngineEventDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_AccountEngineEventDialog);
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
		} catch (final WrongValueException e) {
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
			logger.debug("isDataChanged(): true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged(): false");
		}

		if (close) {
			closeDialog(this.window_AccountEngineEventDialog, "AccountEngineEvent");
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
	 * @param aAccountEngineEvent
	 *            AccountEngineEvent
	 */
	public void doWriteBeanToComponents(AccountEngineEvent aAccountEngineEvent) {
		logger.debug("Entering");
		this.aEEventCode.setValue(aAccountEngineEvent.getAEEventCode());
		this.aEEventCodeDesc.setValue(aAccountEngineEvent.getAEEventCodeDesc());
		this.recordStatus.setValue(aAccountEngineEvent.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccountEngineEvent
	 */
	public void doWriteComponentsToBean(AccountEngineEvent aAccountEngineEvent) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAccountEngineEvent.setAEEventCode(this.aEEventCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAccountEngineEvent.setAEEventCodeDesc(this.aEEventCodeDesc.getValue());
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

		aAccountEngineEvent.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAccountEngineEvent
	 * @throws InterruptedException
	 */
	public void doShowDialog(AccountEngineEvent aAccountEngineEvent) throws InterruptedException {
		logger.debug("Entering");
		// if aAccountEngineEvent == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aAccountEngineEvent == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aAccountEngineEvent = getAccountEngineEventService().getNewAccountEngineEvent();
			setAccountEngineEvent(aAccountEngineEvent);
		} else {
			setAccountEngineEvent(aAccountEngineEvent);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aAccountEngineEvent.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.aEEventCode.focus();
		} else {
			this.aEEventCodeDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aAccountEngineEvent);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_AccountEngineEventDialog);
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
		this.oldVar_aEEventCode = this.aEEventCode.getValue();
		this.oldVar_aEEventCodeDesc = this.aEEventCodeDesc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.aEEventCode.setValue(this.oldVar_aEEventCode);
		this.aEEventCodeDesc.setValue(this.oldVar_aEEventCodeDesc);
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
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_aEEventCode != this.aEEventCode.getValue()) {
			return true;
		}
		if (this.oldVar_aEEventCodeDesc != this.aEEventCodeDesc.getValue()) {
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

		if (!this.aEEventCode.isReadonly()){
			this.aEEventCode.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountEngineEventDialog_AEEventCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	

		if (!this.aEEventCodeDesc.isReadonly()){
			this.aEEventCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountEngineEventDialog_AEEventCodeDesc.value"), 
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
		this.aEEventCode.setConstraint("");
		this.aEEventCodeDesc.setConstraint("");
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
	private void doClearMessage() {
		logger.debug("Entering");
		this.aEEventCode.setErrorMessage("");
		this.aEEventCodeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a AccountEngineEvent object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final AccountEngineEvent aAccountEngineEvent = new AccountEngineEvent();
		BeanUtils.copyProperties(getAccountEngineEvent(), aAccountEngineEvent);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aAccountEngineEvent.getAEEventCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aAccountEngineEvent.getRecordType()).equals("")) {
				aAccountEngineEvent.setVersion(aAccountEngineEvent.getVersion() + 1);
				aAccountEngineEvent.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAccountEngineEvent.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aAccountEngineEvent, tranType)) {
					refreshList();
					closeDialog(this.window_AccountEngineEventDialog, "AccountEngineEvent");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new AccountEngineEvent object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new AccountEngineEvent() in the FrontEnd.
		// we get it from the BackEnd.
		final AccountEngineEvent aAccountEngineEvent = getAccountEngineEventService().getNewAccountEngineEvent();
		aAccountEngineEvent.setNewRecord(true);
		setAccountEngineEvent(aAccountEngineEvent);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.aEEventCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getAccountEngineEvent().isNewRecord()) {
			this.aEEventCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.aEEventCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.aEEventCodeDesc.setReadonly(isReadOnly("AccountEngineEventDialog_aEEventCodeDesc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.accountEngineEvent.isNewRecord()) {
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
		this.aEEventCode.setReadonly(true);
		this.aEEventCodeDesc.setReadonly(true);

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
		this.aEEventCode.setValue("");
		this.aEEventCodeDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final AccountEngineEvent aAccountEngineEvent = new AccountEngineEvent();
		BeanUtils.copyProperties(getAccountEngineEvent(), aAccountEngineEvent);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the AccountEngineEvent object with the components data
		doWriteComponentsToBean(aAccountEngineEvent);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aAccountEngineEvent.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aAccountEngineEvent.getRecordType()).equals("")) {
				aAccountEngineEvent.setVersion(aAccountEngineEvent.getVersion() + 1);
				if (isNew) {
					aAccountEngineEvent.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAccountEngineEvent.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAccountEngineEvent.setNewRecord(true);
				}
			}
		} else {
			aAccountEngineEvent.setVersion(aAccountEngineEvent.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aAccountEngineEvent, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_AccountEngineEventDialog, "AccountEngineEvent");
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
	 * @param aAccountEngineEvent
	 *            (AccountEngineEvent)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(AccountEngineEvent aAccountEngineEvent, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAccountEngineEvent.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aAccountEngineEvent.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccountEngineEvent.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aAccountEngineEvent.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccountEngineEvent.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aAccountEngineEvent);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aAccountEngineEvent))) {
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

			aAccountEngineEvent.setTaskId(taskId);
			aAccountEngineEvent.setNextTaskId(nextTaskId);
			aAccountEngineEvent.setRoleCode(getRole());
			aAccountEngineEvent.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAccountEngineEvent, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aAccountEngineEvent);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAccountEngineEvent, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAccountEngineEvent, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
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
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AccountEngineEvent aAccountEngineEvent = (AccountEngineEvent) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getAccountEngineEventService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getAccountEngineEventService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getAccountEngineEventService().doApprove(auditHeader);

						if (aAccountEngineEvent.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getAccountEngineEventService().doReject(auditHeader);

						if (aAccountEngineEvent.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AccountEngineEventDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_AccountEngineEventDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAccountEngineEvent
	 *            (AccountEngineEvent)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(AccountEngineEvent aAccountEngineEvent, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountEngineEvent.getBefImage(), aAccountEngineEvent);
		return new AuditHeader(String.valueOf(aAccountEngineEvent.getId()),
				null, null, null, auditDetail,aAccountEngineEvent.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_AccountEngineEventDialog, auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,	map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<AccountEngineEvent> soAccountEngineEvent = getAccountEngineEventListCtrl().getSearchObj();
		getAccountEngineEventListCtrl().pagingAccountEngineEventList.setActivePage(0);
		getAccountEngineEventListCtrl().getPagedListWrapper().setSearchObject(soAccountEngineEvent);
		if (getAccountEngineEventListCtrl().listBoxAccountEngineEvent != null) {
			getAccountEngineEventListCtrl().listBoxAccountEngineEvent.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("AccountEngineEvent");
		notes.setReference(getAccountEngineEvent().getAEEventCode());
		notes.setVersion(getAccountEngineEvent().getVersion());
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

	public AccountEngineEvent getAccountEngineEvent() {
		return this.accountEngineEvent;
	}
	public void setAccountEngineEvent(AccountEngineEvent accountEngineEvent) {
		this.accountEngineEvent = accountEngineEvent;
	}

	public void setAccountEngineEventService(AccountEngineEventService accountEngineEventService) {
		this.accountEngineEventService = accountEngineEventService;
	}
	public AccountEngineEventService getAccountEngineEventService() {
		return this.accountEngineEventService;
	}

	public void setAccountEngineEventListCtrl(AccountEngineEventListCtrl accountEngineEventListCtrl) {
		this.accountEngineEventListCtrl = accountEngineEventListCtrl;
	}
	public AccountEngineEventListCtrl getAccountEngineEventListCtrl() {
		return this.accountEngineEventListCtrl;
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
