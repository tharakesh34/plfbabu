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
 * FileName    		:  CustomerNotesTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.customernotestype;

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
import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.CustomerNotesTypeService;
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
 * /WEB-INF/pages/ApplicationMaster/CustomerNotesType/customerNotesTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerNotesTypeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -7057719949392212414L;
	private final static Logger logger = Logger.getLogger(CustomerNotesTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CustomerNotesTypeDialog; // autoWired

	protected Textbox 	custNotesTypeCode; 				// autoWired
	protected Textbox 	custNotesTypeDesc; 				// autoWired
	protected Checkbox 	custNotesTypeIsPerminent; 		// autoWired
	protected Checkbox 	custNotesTypeIsActive; 			// autoWired
	protected Textbox 	custNotesTypeArchiveFrq; 		// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	

	// not autoWired variables
	private CustomerNotesType customerNotesType; 				// overHanded per parameters
	private transient CustomerNotesTypeListCtrl customerNotesTypeListCtrl; // overHanded per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String 	oldVar_custNotesTypeCode;
	private transient String 	oldVar_custNotesTypeDesc;
	private transient boolean 	oldVar_custNotesTypeIsPerminent;
	private transient boolean 	oldVar_custNotesTypeIsActive;
	private transient String 	oldVar_custNotesTypeArchiveFrq;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerNotesTypeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWired
	protected Button btnEdit; 		// autoWired
	protected Button btnDelete; 	// autoWired
	protected Button btnSave; 		// autoWired
	protected Button btnCancel; 	// autoWired
	protected Button btnClose; 		// autoWired
	protected Button btnHelp; 		// autoWired
	protected Button btnNotes; 		// autoWired

	protected Button btnSearchCustNotesTypeArchiveFrq;  	// autoWired
	protected Textbox lovDescCustNotesTypeArchiveFrqName;
	private transient String oldVar_lovDescCustNotesTypeArchiveFrqName;

	// ServiceDAOs / Domain Classes
	private transient CustomerNotesTypeService customerNotesTypeService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public CustomerNotesTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected CustomerNotesType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerNotesTypeDialog(Event event) throws Exception {
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
		if (args.containsKey("customerNotesType")) {
			this.customerNotesType = (CustomerNotesType) args.get("customerNotesType");
			CustomerNotesType befImage = new CustomerNotesType();
			BeanUtils.copyProperties(this.customerNotesType, befImage);
			this.customerNotesType.setBefImage(befImage);
			setCustomerNotesType(this.customerNotesType);
		} else {
			setCustomerNotesType(null);
		}

		doLoadWorkFlow(this.customerNotesType.isWorkflow(), this.customerNotesType.getWorkflowId(),	this.customerNotesType.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerNotesTypeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the customerNotesTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerNotesType here.
		if (args.containsKey("customerNotesTypeListCtrl")) {
			setCustomerNotesTypeListCtrl((CustomerNotesTypeListCtrl) args.get("customerNotesTypeListCtrl"));
		} else {
			setCustomerNotesTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerNotesType());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.custNotesTypeCode.setMaxlength(8);
		this.custNotesTypeDesc.setMaxlength(50);
		this.custNotesTypeArchiveFrq.setMaxlength(8);

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
		getUserWorkspace().alocateAuthorities("CustomerNotesTypeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerNotesTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerNotesTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerNotesTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerNotesTypeDialog_btnSave"));
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
	public void onClose$window_CustomerNotesTypeDialog(Event event)	throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerNotesTypeDialog);
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
			logger.debug("doClose isDataChanged(): true");

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
			logger.debug("doClose isDataChanged(): false");
		}

		if (close) {
			closeDialog(this.window_CustomerNotesTypeDialog, "CustomerNotesType");
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
	 * @param aCustomerNotesType
	 *            CustomerNotesType
	 */
	public void doWriteBeanToComponents(CustomerNotesType aCustomerNotesType) {
		logger.debug("Entering");
		this.custNotesTypeCode.setValue(aCustomerNotesType.getCustNotesTypeCode());
		this.custNotesTypeDesc.setValue(aCustomerNotesType.getCustNotesTypeDesc());
		this.custNotesTypeIsPerminent.setChecked(aCustomerNotesType.isCustNotesTypeIsPerminent());
		this.custNotesTypeIsActive.setChecked(aCustomerNotesType.isCustNotesTypeIsActive());
		this.custNotesTypeArchiveFrq.setValue(aCustomerNotesType.getCustNotesTypeArchiveFrq());

		if (aCustomerNotesType.isNewRecord()) {
			this.lovDescCustNotesTypeArchiveFrqName.setValue("");
		} else {
			this.lovDescCustNotesTypeArchiveFrqName.setValue(aCustomerNotesType.getCustNotesTypeArchiveFrq() + "-"
					+ aCustomerNotesType.getLovDescCustNotesTypeArchiveFrqName());
		}
		this.recordStatus.setValue(aCustomerNotesType.getRecordStatus());
		
		if(aCustomerNotesType.isNew() || (aCustomerNotesType.getRecordType() != null ? aCustomerNotesType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.custNotesTypeIsActive.setChecked(true);
			this.custNotesTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerNotesType
	 */
	public void doWriteComponentsToBean(CustomerNotesType aCustomerNotesType) {
		logger.debug("Entering");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerNotesType.setCustNotesTypeCode(this.custNotesTypeCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerNotesType.setCustNotesTypeDesc(this.custNotesTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerNotesType.setCustNotesTypeIsPerminent(this.custNotesTypeIsPerminent.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerNotesType.setCustNotesTypeIsActive(this.custNotesTypeIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerNotesType.setLovDescCustNotesTypeArchiveFrqName(this.lovDescCustNotesTypeArchiveFrqName.getValue());
			aCustomerNotesType.setCustNotesTypeArchiveFrq(this.custNotesTypeArchiveFrq.getValue());
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

		aCustomerNotesType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerNotesType
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerNotesType aCustomerNotesType) throws InterruptedException {
		logger.debug("Entering");

		// if aCustomerNotesType == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCustomerNotesType == null) {
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aCustomerNotesType = getCustomerNotesTypeService().getNewCustomerNotesType();

			setCustomerNotesType(aCustomerNotesType);
		} else {
			setCustomerNotesType(aCustomerNotesType);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aCustomerNotesType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custNotesTypeCode.focus();
		} else {
			this.custNotesTypeDesc.focus();
			if (isWorkFlowEnabled()) {
				if (!StringUtils.trimToEmpty(aCustomerNotesType.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aCustomerNotesType);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerNotesTypeDialog);
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
	 * Stores the initialize values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");

		this.oldVar_custNotesTypeCode = this.custNotesTypeCode.getValue();
		this.oldVar_custNotesTypeDesc = this.custNotesTypeDesc.getValue();
		this.oldVar_custNotesTypeIsPerminent = this.custNotesTypeIsPerminent.isChecked();
		this.oldVar_custNotesTypeIsActive = this.custNotesTypeIsActive.isChecked();
		this.oldVar_custNotesTypeArchiveFrq = this.custNotesTypeArchiveFrq.getValue();
		this.oldVar_lovDescCustNotesTypeArchiveFrqName = this.lovDescCustNotesTypeArchiveFrqName.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		this.custNotesTypeCode.setValue(this.oldVar_custNotesTypeCode);
		this.custNotesTypeDesc.setValue(this.oldVar_custNotesTypeDesc);
		this.custNotesTypeIsPerminent.setChecked(this.oldVar_custNotesTypeIsPerminent);
		this.custNotesTypeIsActive.setChecked(this.oldVar_custNotesTypeIsActive);
		this.custNotesTypeArchiveFrq.setValue(this.oldVar_custNotesTypeArchiveFrq);
		this.lovDescCustNotesTypeArchiveFrqName.setValue(this.oldVar_lovDescCustNotesTypeArchiveFrqName);
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

		if (this.oldVar_custNotesTypeCode != this.custNotesTypeCode.getValue()) {
			return true;
		}
		if (this.oldVar_custNotesTypeDesc != this.custNotesTypeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_custNotesTypeIsPerminent != this.custNotesTypeIsPerminent.isChecked()) {
			return true;
		}
		if (this.oldVar_custNotesTypeIsActive != this.custNotesTypeIsActive.isChecked()) {
			return true;
		}
		if (this.oldVar_custNotesTypeArchiveFrq != this.custNotesTypeArchiveFrq.getValue()) {
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

		if (!this.custNotesTypeCode.isReadonly()){
			this.custNotesTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerNotesTypeDialog_CustNotesTypeCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		if (!this.custNotesTypeDesc.isReadonly()){
			this.custNotesTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerNotesTypeDialog_CustNotesTypeDesc.value"), 
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
		this.custNotesTypeCode.setConstraint("");
		this.custNotesTypeDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		this.lovDescCustNotesTypeArchiveFrqName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerNotesTypeDialog_CustNotesTypeArchiveFrq.value"), 
				null, true));
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		this.lovDescCustNotesTypeArchiveFrqName.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custNotesTypeCode.setErrorMessage("");
		this.custNotesTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerNotesType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerNotesType aCustomerNotesType = new CustomerNotesType();
		BeanUtils.copyProperties(getCustomerNotesType(), aCustomerNotesType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCustomerNotesType.getCustNotesTypeCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerNotesType.getRecordType()).equals("")) {
				aCustomerNotesType.setVersion(aCustomerNotesType.getVersion() + 1);
				aCustomerNotesType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aCustomerNotesType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aCustomerNotesType, tranType)) {
					refreshList();
					closeDialog(this.window_CustomerNotesTypeDialog, "CustomerNotesType");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerNotesType object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		// we don't create a new CustomerNotesType() in the front end.
		// we get it from the back end.
		final CustomerNotesType aCustomerNotesType = getCustomerNotesTypeService().getNewCustomerNotesType();
		aCustomerNotesType.setNewRecord(true);
		setCustomerNotesType(aCustomerNotesType);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.custNotesTypeCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCustomerNotesType().isNewRecord()) {
			this.custNotesTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custNotesTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.custNotesTypeDesc.setReadonly(isReadOnly("CustomerNotesTypeDialog_custNotesTypeDesc"));
		this.custNotesTypeIsPerminent.setDisabled(isReadOnly("CustomerNotesTypeDialog_custNotesTypeIsPerminent"));
		this.custNotesTypeIsActive.setDisabled(isReadOnly("CustomerNotesTypeDialog_custNotesTypeIsActive"));
		this.btnSearchCustNotesTypeArchiveFrq.setDisabled(isReadOnly("CustomerNotesTypeDialog_custNotesTypeArchiveFrq"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerNotesType.isNewRecord()) {
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

		this.custNotesTypeCode.setReadonly(true);
		this.custNotesTypeDesc.setReadonly(true);
		this.custNotesTypeIsPerminent.setDisabled(true);
		this.custNotesTypeIsActive.setDisabled(true);
		this.btnSearchCustNotesTypeArchiveFrq.setDisabled(true);

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
		this.custNotesTypeCode.setValue("");
		this.custNotesTypeDesc.setValue("");
		this.custNotesTypeIsPerminent.setChecked(false);
		this.custNotesTypeIsActive.setChecked(false);
		this.custNotesTypeArchiveFrq.setValue("");
		this.lovDescCustNotesTypeArchiveFrqName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerNotesType aCustomerNotesType = new CustomerNotesType();
		BeanUtils.copyProperties(getCustomerNotesType(), aCustomerNotesType);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerNotesType object with the components data
		doWriteComponentsToBean(aCustomerNotesType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCustomerNotesType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerNotesType.getRecordType()).equals("")) {
				aCustomerNotesType.setVersion(aCustomerNotesType.getVersion() + 1);
				if (isNew) {
					aCustomerNotesType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerNotesType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerNotesType.setNewRecord(true);
				}
			}
		} else {
			aCustomerNotesType.setVersion(aCustomerNotesType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aCustomerNotesType, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CustomerNotesTypeDialog, "CustomerNotesType");
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
	 * @param aCustomerNotesType
	 *            (CustomerNotesType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerNotesType aCustomerNotesType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerNotesType.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerNotesType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerNotesType.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerNotesType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerNotesType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerNotesType);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCustomerNotesType))) {
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

			aCustomerNotesType.setTaskId(taskId);
			aCustomerNotesType.setNextTaskId(nextTaskId);
			aCustomerNotesType.setRoleCode(getRole());
			aCustomerNotesType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerNotesType, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aCustomerNotesType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerNotesType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerNotesType, tranType);
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
		CustomerNotesType aCustomerNotesType = (CustomerNotesType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerNotesTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerNotesTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerNotesTypeService().doApprove(auditHeader);

						if (aCustomerNotesType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerNotesTypeService().doReject(auditHeader);

						if (aCustomerNotesType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerNotesTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerNotesTypeDialog, auditHeader);
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
	// ++++++++++++ Search Button Component Events ++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchCustNotesTypeArchiveFrq(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerNotesTypeDialog, "Frequency");
		if (dataObject instanceof String) {
			this.custNotesTypeArchiveFrq.setValue(dataObject.toString());
			this.lovDescCustNotesTypeArchiveFrqName.setValue("");
		} else {
			Frequency details = (Frequency) dataObject;
			if (details != null) {
				this.custNotesTypeArchiveFrq.setValue(details.getLovValue());
				this.lovDescCustNotesTypeArchiveFrqName.setValue(details.getLovValue() + "-" + details.getFrqDesc());
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
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerNotesType aCustomerNotesType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerNotesType.getBefImage(), aCustomerNotesType);
		return new AuditHeader(String.valueOf(aCustomerNotesType.getId()),
				null, null, null, auditDetail, aCustomerNotesType.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerNotesTypeDialog, auditHeader);
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
		final JdbcSearchObject<CustomerNotesType> soCustomerNotesType = getCustomerNotesTypeListCtrl().getSearchObj();
		getCustomerNotesTypeListCtrl().pagingCustomerNotesTypeList.setActivePage(0);
		getCustomerNotesTypeListCtrl().getPagedListWrapper().setSearchObject(soCustomerNotesType);
		if (getCustomerNotesTypeListCtrl().listBoxCustomerNotesType != null) {
			getCustomerNotesTypeListCtrl().listBoxCustomerNotesType.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerNotesType");
		notes.setReference(getCustomerNotesType().getCustNotesTypeCode());
		notes.setVersion(getCustomerNotesType().getVersion());
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

	public CustomerNotesType getCustomerNotesType() {
		return this.customerNotesType;
	}
	public void setCustomerNotesType(CustomerNotesType customerNotesType) {
		this.customerNotesType = customerNotesType;
	}

	public void setCustomerNotesTypeService(CustomerNotesTypeService customerNotesTypeService) {
		this.customerNotesTypeService = customerNotesTypeService;
	}
	public CustomerNotesTypeService getCustomerNotesTypeService() {
		return this.customerNotesTypeService;
	}

	public void setCustomerNotesTypeListCtrl(CustomerNotesTypeListCtrl customerNotesTypeListCtrl) {
		this.customerNotesTypeListCtrl = customerNotesTypeListCtrl;
	}
	public CustomerNotesTypeListCtrl getCustomerNotesTypeListCtrl() {
		return this.customerNotesTypeListCtrl;
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
