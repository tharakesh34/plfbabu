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
 * FileName    		:  BlackListReasonCodeDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.blacklistreasoncode;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.impl.BlackListReasonCodeService;
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
 * /WEB-INF/pages/SystemMaster/BlackListReasonCode/BlackListReasonCodeDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class BlackListReasonCodeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 374144767782969911L;
	private final static Logger logger = Logger.getLogger(BlackListReasonCodeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_BlackListReasonCodeDialog; // autoWired

	protected Textbox 		bLRsnCode; 					// autoWired
	protected Textbox 		bLRsnDesc; 					// autoWired
	protected Checkbox 		bLIsActive; 				// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not autoWired variables
	private BlackListReasonCode blackListReasonCode; 			// overHanded per parameter
	private transient BlackListReasonCodeListCtrl blackListReasonCodeListCtrl; // overHanded
	// per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 	oldVar_bLRsnCode;
	private transient String 	oldVar_bLRsnDesc;
	private transient boolean 	oldVar_bLIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_BlackListReasonCodeDialog_";
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
	private transient BlackListReasonCodeService blackListReasonCodeService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public BlackListReasonCodeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected BlackListReasonCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BlackListReasonCodeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("blackListReasonCode")) {
			this.blackListReasonCode = (BlackListReasonCode) args.get("blackListReasonCode");
			BlackListReasonCode befImage = new BlackListReasonCode();
			BeanUtils.copyProperties(this.blackListReasonCode, befImage);
			this.blackListReasonCode.setBefImage(befImage);

			setBlackListReasonCode(this.blackListReasonCode);
		} else {
			setBlackListReasonCode(null);
		}

		doLoadWorkFlow(this.blackListReasonCode.isWorkflow(), this.blackListReasonCode.getWorkflowId(),
				this.blackListReasonCode.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "BlackListReasonCodeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the blackListReasonCodeListWindow controller. So we have
		// access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete blackListReasonCode here.
		if (args.containsKey("blackListReasonCodeListCtrl")) {
			setBlackListReasonCodeListCtrl((BlackListReasonCodeListCtrl) args.get("blackListReasonCodeListCtrl"));
		} else {
			setBlackListReasonCodeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getBlackListReasonCode());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.bLRsnCode.setMaxlength(8);
		this.bLRsnDesc.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
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
		getUserWorkspace().alocateAuthorities("BlackListReasonCodeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BlackListReasonCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BlackListReasonCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BlackListReasonCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BlackListReasonCodeDialog_btnSave"));
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
	public void onClose$window_BlackListReasonCodeDialog(Event event) throws Exception {
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
		// remember the old variables
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
		PTMessageUtils.showHelpWindow(event, window_BlackListReasonCodeDialog);
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
		logger.debug("Leaving");
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
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving");
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
			logger.debug("doClose isDataChanged : true");

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
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeDialog(this.window_BlackListReasonCodeDialog, "BlackListReasonCode");
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
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBlackListReasonCode
	 *            BlackListReasonCode
	 */
	public void doWriteBeanToComponents(BlackListReasonCode aBlackListReasonCode) {
		logger.debug("Entering");
		this.bLRsnCode.setValue(aBlackListReasonCode.getBLRsnCode());
		this.bLRsnDesc.setValue(aBlackListReasonCode.getBLRsnDesc());
		this.bLIsActive.setChecked(aBlackListReasonCode.isBLIsActive());
		this.recordStatus.setValue(aBlackListReasonCode.getRecordStatus());
		
		if(aBlackListReasonCode.isNew() || (aBlackListReasonCode.getRecordType() != null ? aBlackListReasonCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.bLIsActive.setChecked(true);
			this.bLIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBlackListReasonCode
	 */
	public void doWriteComponentsToBean(BlackListReasonCode aBlackListReasonCode) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aBlackListReasonCode.setBLRsnCode(this.bLRsnCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBlackListReasonCode.setBLRsnDesc(this.bLRsnDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBlackListReasonCode.setBLIsActive(this.bLIsActive.isChecked());
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

		aBlackListReasonCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBlackListReasonCode
	 * @throws InterruptedException
	 */
	public void doShowDialog(BlackListReasonCode aBlackListReasonCode) throws InterruptedException {
		logger.debug("Entering");

		// if aBlackListReasonCode == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aBlackListReasonCode == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aBlackListReasonCode = getBlackListReasonCodeService().getNewBlackListReasonCode();

			setBlackListReasonCode(aBlackListReasonCode);
		} else {
			setBlackListReasonCode(aBlackListReasonCode);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aBlackListReasonCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bLRsnCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.bLRsnDesc.focus();
				if (!StringUtils.trimToEmpty(aBlackListReasonCode.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aBlackListReasonCode);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_BlackListReasonCodeDialog);
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_bLRsnCode = this.bLRsnCode.getValue();
		this.oldVar_bLRsnDesc = this.bLRsnDesc.getValue();
		this.oldVar_bLIsActive = this.bLIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.bLRsnCode.setValue(this.oldVar_bLRsnCode);
		this.bLRsnDesc.setValue(this.oldVar_bLRsnDesc);
		this.bLIsActive.setChecked(this.oldVar_bLIsActive);
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
		// Clear the Error messages
		doClearMessage();

		if (this.oldVar_bLRsnCode != this.bLRsnCode.getValue()) {
			return true;
		}
		if (this.oldVar_bLRsnDesc != this.bLRsnDesc.getValue()) {
			return true;
		}
		if (this.oldVar_bLIsActive != this.bLIsActive.isChecked()) {
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

		if (!this.bLRsnCode.isReadonly()){
			this.bLRsnCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BlackListReasonCodeDialog_BLRsnCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	

		if (!this.bLRsnDesc.isReadonly()){
			this.bLRsnDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_BlackListReasonCodeDialog_BLRsnDesc.value"), 
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
		this.bLRsnCode.setConstraint("");
		this.bLRsnDesc.setConstraint("");
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
		this.bLRsnCode.setErrorMessage("");
		this.bLRsnDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a BlackListReasonCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final BlackListReasonCode aBlackListReasonCode = new BlackListReasonCode();
		BeanUtils.copyProperties(getBlackListReasonCode(),	aBlackListReasonCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aBlackListReasonCode.getBLRsnCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aBlackListReasonCode.getRecordType()).equals("")) {
				aBlackListReasonCode.setVersion(aBlackListReasonCode.getVersion() + 1);
				aBlackListReasonCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBlackListReasonCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBlackListReasonCode, tranType)) {
					refreshList();
					closeDialog(this.window_BlackListReasonCodeDialog, "BlackListReasonCode");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new BlackListReasonCode object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new BlackListReasonCode() in the front end.
		// we get it from the back end.
		final BlackListReasonCode aBlackListReasonCode = getBlackListReasonCodeService().getNewBlackListReasonCode();
		aBlackListReasonCode.setNewRecord(true);
		setBlackListReasonCode(aBlackListReasonCode);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.bLRsnCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getBlackListReasonCode().isNewRecord()) {
			this.bLRsnCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.bLRsnCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.bLRsnDesc.setReadonly(isReadOnly("BlackListReasonCodeDialog_bLRsnDesc"));
		this.bLIsActive.setDisabled(isReadOnly("BlackListReasonCodeDialog_bLIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.blackListReasonCode.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.bLRsnCode.setReadonly(true);
		this.bLRsnDesc.setReadonly(true);
		this.bLIsActive.setDisabled(true);

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
		this.bLRsnCode.setValue("");
		this.bLRsnDesc.setValue("");
		this.bLIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final BlackListReasonCode aBlackListReasonCode = new BlackListReasonCode();
		BeanUtils.copyProperties(getBlackListReasonCode(), aBlackListReasonCode);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the BlackListReasonCode object with the components data
		doWriteComponentsToBean(aBlackListReasonCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBlackListReasonCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aBlackListReasonCode.getRecordType()).equals("")) {
				aBlackListReasonCode.setVersion(aBlackListReasonCode.getVersion() + 1);
				if (isNew) {
					aBlackListReasonCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBlackListReasonCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBlackListReasonCode.setNewRecord(true);
				}
			}
		} else {
			aBlackListReasonCode.setVersion(aBlackListReasonCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aBlackListReasonCode, tranType)) {
				refreshList();
				closeDialog(this.window_BlackListReasonCodeDialog, "BlackListReasonCode");
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
	 * @param aBlackListReasonCode
	 *            (BlackListReasonCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(BlackListReasonCode aBlackListReasonCode,	String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBlackListReasonCode.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aBlackListReasonCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBlackListReasonCode.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aBlackListReasonCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBlackListReasonCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aBlackListReasonCode);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aBlackListReasonCode))) {
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

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
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

			aBlackListReasonCode.setTaskId(taskId);
			aBlackListReasonCode.setNextTaskId(nextTaskId);
			aBlackListReasonCode.setRoleCode(getRole());
			aBlackListReasonCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBlackListReasonCode, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aBlackListReasonCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBlackListReasonCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aBlackListReasonCode, tranType);
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
		BlackListReasonCode aBlackListReasonCode = (BlackListReasonCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getBlackListReasonCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getBlackListReasonCodeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getBlackListReasonCodeService().doApprove(auditHeader);

						if (aBlackListReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBlackListReasonCodeService().doReject(auditHeader);

						if (aBlackListReasonCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel(
						"InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BlackListReasonCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_BlackListReasonCodeDialog, auditHeader);
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
			e.printStackTrace();
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
	 * @param aBlackListReasonCode
	 *            (BlackListReasonCode)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(BlackListReasonCode aBlackListReasonCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aBlackListReasonCode.getBefImage(), aBlackListReasonCode);
		return new AuditHeader(String.valueOf(aBlackListReasonCode.getId()),
				null, null, null, auditDetail, aBlackListReasonCode.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_BlackListReasonCodeDialog, auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		final JdbcSearchObject<BlackListReasonCode> soBlackListReasonCode = getBlackListReasonCodeListCtrl().getSearchObj();
		getBlackListReasonCodeListCtrl().pagingBlackListReasonCodeList.setActivePage(0);
		getBlackListReasonCodeListCtrl().getPagedListWrapper().setSearchObject(soBlackListReasonCode);
		if (getBlackListReasonCodeListCtrl().listBoxBlackListReasonCode != null) {
			getBlackListReasonCodeListCtrl().listBoxBlackListReasonCode.getListModel();
		}
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("BlackListReasonCode");
		notes.setReference(getBlackListReasonCode().getBLRsnCode());
		notes.setVersion(getBlackListReasonCode().getVersion());
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

	public BlackListReasonCode getBlackListReasonCode() {
		return this.blackListReasonCode;
	}
	public void setBlackListReasonCode(BlackListReasonCode blackListReasonCode) {
		this.blackListReasonCode = blackListReasonCode;
	}

	public void setBlackListReasonCodeService(BlackListReasonCodeService blackListReasonCodeService) {
		this.blackListReasonCodeService = blackListReasonCodeService;
	}
	public BlackListReasonCodeService getBlackListReasonCodeService() {
		return this.blackListReasonCodeService;
	}

	public void setBlackListReasonCodeListCtrl(BlackListReasonCodeListCtrl blackListReasonCodeListCtrl) {
		this.blackListReasonCodeListCtrl = blackListReasonCodeListCtrl;
	}
	public BlackListReasonCodeListCtrl getBlackListReasonCodeListCtrl() {
		return this.blackListReasonCodeListCtrl;
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
