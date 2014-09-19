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
 * FileName    		:  CustomerStatusCodeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.customerstatuscode;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.CustomerStatusCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CustomerStatusCode/CustomerStatusCodeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerStatusCodeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7665708224082701621L;
	private final static Logger logger = Logger.getLogger(CustomerStatusCodeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CustomerStatusCodeDialog;	// autoWired

	protected Textbox 	custStsCode; 					// autoWired
	protected Textbox 	custStsDescription; 			// autoWired
	protected Intbox 	dueDays; 						// autoWired
	protected Checkbox 	suspendProfit; 					// autoWired
	protected Checkbox 	custStsIsActive; 				// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;


	// not autoWired variables
	private CustomerStatusCode customerStatusCode; 					// overHanded per parameter
	private transient CustomerStatusCodeListCtrl customerStatusCodeListCtrl; // overHanded
	// per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 	oldVar_custStsCode;
	private transient String 	oldVar_custStsDescription;
	private transient int 		oldVar_dueDays;
	private transient boolean 	oldVar_suspendProfit;
	private transient boolean 	oldVar_custStsIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerStatusCodeDialog_";
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
	private transient CustomerStatusCodeService customerStatusCodeService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public CustomerStatusCodeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerStatusCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerStatusCodeDialog(Event event) throws Exception {
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
		if (args.containsKey("customerStatusCode")) {
			this.customerStatusCode = (CustomerStatusCode) args.get("customerStatusCode");
			CustomerStatusCode befImage = new CustomerStatusCode();
			BeanUtils.copyProperties(this.customerStatusCode, befImage);
			this.customerStatusCode.setBefImage(befImage);
			setCustomerStatusCode(this.customerStatusCode);
		} else {
			setCustomerStatusCode(null);
		}

		doLoadWorkFlow(this.customerStatusCode.isWorkflow(), this.customerStatusCode.getWorkflowId(), this.customerStatusCode.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerStatusCodeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the customerStatusCodeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerStatusCode here.
		if (args.containsKey("customerStatusCodeListCtrl")) {
			setCustomerStatusCodeListCtrl((CustomerStatusCodeListCtrl) args.get("customerStatusCodeListCtrl"));
		} else {
			setCustomerStatusCodeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerStatusCode());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Leaving");

		// Empty sent any required attributes
		this.custStsCode.setMaxlength(8);
		this.custStsDescription.setMaxlength(50);
		this.dueDays.setMaxlength(4);

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

		getUserWorkspace().alocateAuthorities("CustomerStatusCodeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerStatusCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerStatusCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerStatusCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerStatusCodeDialog_btnSave"));
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
	public void onClose$window_CustomerStatusCodeDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerStatusCodeDialog);
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
			closeDialog(this.window_CustomerStatusCodeDialog, "CustomerStatusCode");
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
	 * @param aCustomerStatusCode
	 *            CustomerStatusCode
	 */
	public void doWriteBeanToComponents(CustomerStatusCode aCustomerStatusCode) {
		logger.debug("Entering");

		this.custStsCode.setValue(aCustomerStatusCode.getCustStsCode());
		this.custStsDescription.setValue(aCustomerStatusCode.getCustStsDescription());
		this.dueDays.setValue(aCustomerStatusCode.getDueDays());
		this.suspendProfit.setChecked(aCustomerStatusCode.isSuspendProfit());
		this.custStsIsActive.setChecked(aCustomerStatusCode.isCustStsIsActive());
		this.recordStatus.setValue(aCustomerStatusCode.getRecordStatus());
		
		if(aCustomerStatusCode.isNew() || (aCustomerStatusCode.getRecordType() != null ? aCustomerStatusCode.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.custStsIsActive.setChecked(true);
			this.custStsIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerStatusCode
	 */
	public void doWriteComponentsToBean(CustomerStatusCode aCustomerStatusCode) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerStatusCode.setCustStsCode(this.custStsCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerStatusCode.setCustStsDescription(this.custStsDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerStatusCode.setDueDays(this.dueDays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerStatusCode.setSuspendProfit(this.suspendProfit.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerStatusCode.setCustStsIsActive(this.custStsIsActive.isChecked());
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

		aCustomerStatusCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerStatusCode
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerStatusCode aCustomerStatusCode) throws InterruptedException {
		logger.debug("Entering");

		// if aCustomerStatusCode == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCustomerStatusCode == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aCustomerStatusCode = getCustomerStatusCodeService().getNewCustomerStatusCode();
			setCustomerStatusCode(aCustomerStatusCode);
		} else {
			setCustomerStatusCode(aCustomerStatusCode);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aCustomerStatusCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custStsCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.custStsDescription.focus();
				if (!StringUtils.trimToEmpty(aCustomerStatusCode.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aCustomerStatusCode);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerStatusCodeDialog);
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
		
		this.oldVar_custStsCode = this.custStsCode.getValue();
		this.oldVar_custStsDescription = this.custStsDescription.getValue();
		this.oldVar_dueDays = this.dueDays.intValue();
		this.oldVar_suspendProfit = this.suspendProfit.isChecked();
		this.oldVar_custStsIsActive = this.custStsIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.custStsCode.setValue(this.oldVar_custStsCode);
		this.custStsDescription.setValue(this.oldVar_custStsDescription);
		this.dueDays.setValue(this.oldVar_dueDays);
		this.suspendProfit.setChecked(this.oldVar_suspendProfit);
		this.custStsIsActive.setChecked(this.oldVar_custStsIsActive);
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
		// To Clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_custStsCode != this.custStsCode.getValue()) {
			return true;
		}
		if (this.oldVar_custStsDescription != this.custStsDescription.getValue()) {
			return true;
		}
		if (this.oldVar_dueDays!= this.dueDays.intValue()) {
			return true;
		}
		if (this.oldVar_suspendProfit != this.suspendProfit.isChecked()) {
			return true;
		}
		if (this.oldVar_custStsIsActive != this.custStsIsActive.isChecked()) {
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

		if (!this.custStsCode.isReadonly()){
			this.custStsCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerStatusCodeDialog_CustStsCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.custStsDescription.isReadonly()){
			this.custStsDescription.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerStatusCodeDialog_CustStsDescription.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.dueDays.isReadonly()) {
			this.dueDays.setConstraint(new IntValidator(4,Labels.getLabel(
			"label_CustomerStatusCodeDialog_DueDays.value")));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custStsCode.setConstraint("");
		this.custStsDescription.setConstraint("");
		this.dueDays.setConstraint("");
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
		this.custStsCode.setErrorMessage("");
		this.custStsDescription.setErrorMessage("");
		this.dueDays.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerStatusCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerStatusCode aCustomerStatusCode = new CustomerStatusCode();
		BeanUtils.copyProperties(getCustomerStatusCode(), aCustomerStatusCode);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCustomerStatusCode.getCustStsCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerStatusCode.getRecordType()).equals("")) {
				aCustomerStatusCode.setVersion(aCustomerStatusCode.getVersion() + 1);
				aCustomerStatusCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCustomerStatusCode.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCustomerStatusCode, tranType)) {
					refreshList();
					closeDialog(this.window_CustomerStatusCodeDialog, "CustomerStatusCode");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerStatusCode object. <br>
	 */
	private void doNew() {

		logger.debug("Entering");
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerStatusCode() in the front end.
		// we get it from the back end.
		final CustomerStatusCode aCustomerStatusCode = getCustomerStatusCodeService().getNewCustomerStatusCode();
		aCustomerStatusCode.setNewRecord(true);
		setCustomerStatusCode(aCustomerStatusCode);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.custStsCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {

		logger.debug("Entering");
		if (getCustomerStatusCode().isNewRecord()) {
			this.custStsCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custStsCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.custStsDescription.setReadonly(isReadOnly("CustomerStatusCodeDialog_custStsDescription"));
		this.dueDays.setDisabled(isReadOnly("CustomerStatusCodeDialog_dueDays"));
		this.suspendProfit.setDisabled(isReadOnly("CustomerStatusCodeDialog_suspendProfit"));
		this.custStsIsActive.setDisabled(isReadOnly("CustomerStatusCodeDialog_custStsIsActive"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerStatusCode.isNewRecord()) {
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
		this.custStsCode.setReadonly(true);
		this.custStsDescription.setReadonly(true);
		this.dueDays.setReadonly(true);
		this.suspendProfit.setDisabled(true);
		this.custStsIsActive.setDisabled(true);

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
		this.custStsCode.setValue("");
		this.custStsDescription.setValue("");
		this.dueDays.setText("");
		this.suspendProfit.setChecked(false);
		this.custStsIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerStatusCode aCustomerStatusCode = new CustomerStatusCode();
		BeanUtils.copyProperties(getCustomerStatusCode(), aCustomerStatusCode);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		/*String statusCode = getCustomerStatusCodeService().getCurDueDaysStatus(this.dueDays.intValue(),"_View");
		if(!StringUtils.trimToEmpty(statusCode).equals("")) {	
			PTMessageUtils.showErrorMessage(Labels.getLabel("CUST_STS_CODE_WITH_DUEDAYS_ALREADY_EXISTS",
					new String[]{statusCode, String.valueOf(this.dueDays.getValue()) }));
			return;
		}*/
		
		// fill the CustomerStatusCode object with the components data
		doWriteComponentsToBean(aCustomerStatusCode);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCustomerStatusCode.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerStatusCode.getRecordType()).equals("")) {
				aCustomerStatusCode.setVersion(aCustomerStatusCode.getVersion() + 1);
				if (isNew) {
					aCustomerStatusCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerStatusCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerStatusCode.setNewRecord(true);
				}
			}
		} else {
			aCustomerStatusCode.setVersion(aCustomerStatusCode.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCustomerStatusCode, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CustomerStatusCodeDialog, "CustomerStatusCode");
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
	 * @param aCustomerStatusCode
	 *            (CustomerStatusCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerStatusCode aCustomerStatusCode, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerStatusCode.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerStatusCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerStatusCode.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerStatusCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerStatusCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerStatusCode);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCustomerStatusCode))) {
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

			aCustomerStatusCode.setTaskId(taskId);
			aCustomerStatusCode.setNextTaskId(nextTaskId);
			aCustomerStatusCode.setRoleCode(getRole());
			aCustomerStatusCode.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerStatusCode, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aCustomerStatusCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerStatusCode, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerStatusCode, tranType);
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
		CustomerStatusCode aCustomerStatusCode = (CustomerStatusCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerStatusCodeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerStatusCodeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerStatusCodeService().doApprove(auditHeader);

						if (aCustomerStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerStatusCodeService().doReject(auditHeader);

						if (aCustomerStatusCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerStatusCodeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerStatusCodeDialog, auditHeader);
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
	 * @param aCustomerStatusCode
	 *            (CustomerStatusCode)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(CustomerStatusCode aCustomerStatusCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,aCustomerStatusCode.getBefImage(), aCustomerStatusCode);
		return new AuditHeader(String.valueOf(aCustomerStatusCode.getId()), null,
				null, null, auditDetail, aCustomerStatusCode.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerStatusCodeDialog, auditHeader);
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
		logger.debug("Leaving" + event.toString());
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
		final JdbcSearchObject<CustomerStatusCode> soCustomerStatusCode = getCustomerStatusCodeListCtrl().getSearchObj();
		getCustomerStatusCodeListCtrl().pagingCustomerStatusCodeList.setActivePage(0);
		getCustomerStatusCodeListCtrl().getPagedListWrapper().setSearchObject(soCustomerStatusCode);
		if (getCustomerStatusCodeListCtrl().listBoxCustomerStatusCode != null) {
			getCustomerStatusCodeListCtrl().listBoxCustomerStatusCode.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerStatusCode");
		notes.setReference(getCustomerStatusCode().getCustStsCode());
		notes.setVersion(getCustomerStatusCode().getVersion());
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

	public CustomerStatusCode getCustomerStatusCode() {
		return this.customerStatusCode;
	}
	public void setCustomerStatusCode(CustomerStatusCode customerStatusCode) {
		this.customerStatusCode = customerStatusCode;
	}
	/*public CustomerStatusCode getCustomerDueDays() {
		return this.dueDays;
	}
	public void setCustomerDueDays(CustomerStatusCode dueDays) {
		this.dueDays = dueDays;
	}*/

	public void setCustomerStatusCodeService(CustomerStatusCodeService customerStatusCodeService) {
		this.customerStatusCodeService = customerStatusCodeService;
	}
	public CustomerStatusCodeService getCustomerStatusCodeService() {
		return this.customerStatusCodeService;
	}

	public void setCustomerStatusCodeListCtrl(CustomerStatusCodeListCtrl customerStatusCodeListCtrl) {
		this.customerStatusCodeListCtrl = customerStatusCodeListCtrl;
	}
	public CustomerStatusCodeListCtrl getCustomerStatusCodeListCtrl() {
		return this.customerStatusCodeListCtrl;
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
