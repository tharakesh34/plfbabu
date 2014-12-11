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
 * FileName    		:  PhoneTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.phonetype;

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
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.PhoneTypeService;
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
 * /WEB-INF/pages/SystemMaster/PhoneType/phoneTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class PhoneTypeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5966260372580930309L;
	private final static Logger logger = Logger.getLogger(PhoneTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_PhoneTypeDialog; 		// autoWired
	protected Textbox 	phoneTypeCode; 					// autoWired
	protected Textbox 	phoneTypeDesc; 					// autoWired
	protected Intbox 	phoneTypePriority; 				// autoWired
	protected Checkbox 	phoneTypeIsActive; 				// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	
	// not autoWired variables
	private 		  PhoneType 		phoneType; 			// over handed per parameter
	private transient PhoneTypeListCtrl phoneTypeListCtrl; 	// over handed per
	// parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 	oldVar_phoneTypeCode;
	private transient String 	oldVar_phoneTypeDesc;
	private transient int 		oldVar_phoneTypePriority;
	private transient boolean 	oldVar_phoneTypeIsActive;
	private transient String 	oldVar_recordStatus;

	private transient boolean 	validationOn;
	private boolean 			notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String 		btnCtroller_ClassPrefix = "button_PhoneTypeDialog_";
	private transient ButtonStatusCtrl 	btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire

	// ServiceDAOs / Domain Classes
	private transient PhoneTypeService phoneTypeService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public PhoneTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected PhoneType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PhoneTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("phoneType")) {
			this.phoneType = (PhoneType) args.get("phoneType");
			PhoneType befImage = new PhoneType();
			BeanUtils.copyProperties(this.phoneType, befImage);
			this.phoneType.setBefImage(befImage);
			setPhoneType(this.phoneType);
		} else {
			setPhoneType(null);
		}

		doLoadWorkFlow(this.phoneType.isWorkflow(),this.phoneType.getWorkflowId(), this.phoneType.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"PhoneTypeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the phoneTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete phoneType here.
		if (args.containsKey("phoneTypeListCtrl")) {
			setPhoneTypeListCtrl((PhoneTypeListCtrl) args.get("phoneTypeListCtrl"));
		} else {
			setPhoneTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getPhoneType());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.phoneTypeCode.setMaxlength(8);
		this.phoneTypeDesc.setMaxlength(50);
		this.phoneTypePriority.setMaxlength(3);

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

		getUserWorkspace().alocateAuthorities("PhoneTypeDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PhoneTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PhoneTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PhoneTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PhoneTypeDialog_btnSave"));
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
	public void onClose$window_PhoneTypeDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_PhoneTypeDialog);
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
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_PhoneTypeDialog, "PhoneType");
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
	 * @param aPhoneType
	 *            PhoneType
	 */
	public void doWriteBeanToComponents(PhoneType aPhoneType) {
		logger.debug("Entering");
		this.phoneTypeCode.setValue(aPhoneType.getPhoneTypeCode());
		this.phoneTypeDesc.setValue(aPhoneType.getPhoneTypeDesc());
		this.phoneTypePriority.setValue(aPhoneType.getPhoneTypePriority());
		this.phoneTypeIsActive.setChecked(aPhoneType.isPhoneTypeIsActive());
		this.recordStatus.setValue(aPhoneType.getRecordStatus());
		
		if(aPhoneType.isNew() || (aPhoneType.getRecordType() != null ? aPhoneType.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.phoneTypeIsActive.setChecked(true);
			this.phoneTypeIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPhoneType
	 */
	public void doWriteComponentsToBean(PhoneType aPhoneType) {
		logger.debug("Entering");

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aPhoneType.setPhoneTypeCode(this.phoneTypeCode.getValue()
					.toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPhoneType.setPhoneTypeDesc(this.phoneTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPhoneType.setPhoneTypePriority(this.phoneTypePriority.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aPhoneType.setPhoneTypeIsActive(this.phoneTypeIsActive.isChecked());
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

		aPhoneType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aPhoneType
	 * @throws InterruptedException
	 */
	public void doShowDialog(PhoneType aPhoneType) throws InterruptedException {
		logger.debug("Entering");

		// if aPhoneType == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aPhoneType == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aPhoneType = getPhoneTypeService().getNewPhoneType();

			setPhoneType(aPhoneType);
		} else {
			setPhoneType(aPhoneType);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aPhoneType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.phoneTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.phoneTypeDesc.focus();
				if (!StringUtils.trimToEmpty(aPhoneType.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aPhoneType);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_PhoneTypeDialog);
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
		this.oldVar_phoneTypeCode = this.phoneTypeCode.getValue();
		this.oldVar_phoneTypeDesc = this.phoneTypeDesc.getValue();
		this.oldVar_phoneTypePriority = this.phoneTypePriority.intValue();
		this.oldVar_phoneTypeIsActive = this.phoneTypeIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.phoneTypeCode.setValue(this.oldVar_phoneTypeCode);
		this.phoneTypeDesc.setValue(this.oldVar_phoneTypeDesc);
		this.phoneTypePriority.setValue(this.oldVar_phoneTypePriority);
		this.phoneTypeIsActive.setChecked(this.oldVar_phoneTypeIsActive);
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
		// To remove Error Messages
		doClearMessage();

		if(this.oldVar_phoneTypeCode != this.phoneTypeCode.getValue()){
			return true;
		}
		if (this.oldVar_phoneTypeDesc != this.phoneTypeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_phoneTypePriority != this.phoneTypePriority.intValue()) {
			return true;
		}
		if (this.oldVar_phoneTypeIsActive != this.phoneTypeIsActive.isChecked()) {
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

		if (!this.phoneTypeCode.isReadonly()){
			this.phoneTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_PhoneTypeDialog_PhoneTypeCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.phoneTypeDesc.isReadonly()){
			this.phoneTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_PhoneTypeDialog_PhoneTypeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.phoneTypePriority.isReadonly()) {
			this.phoneTypePriority.setConstraint(new IntValidator(10,Labels.getLabel(
					"label_PhoneTypeDialog_PhoneTypePriority.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.phoneTypeCode.setConstraint("");
		this.phoneTypeDesc.setConstraint("");
		this.phoneTypePriority.setConstraint("");
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
		this.phoneTypeCode.setErrorMessage("");
		this.phoneTypeDesc.setErrorMessage("");
		this.phoneTypePriority.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a PhoneType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final PhoneType aPhoneType = new PhoneType();
		BeanUtils.copyProperties(getPhoneType(), aPhoneType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + aPhoneType.getPhoneTypeCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aPhoneType.getRecordType()).equals("")) {
				aPhoneType.setVersion(aPhoneType.getVersion() + 1);
				aPhoneType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPhoneType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aPhoneType, tranType)) {
					refreshList();
					closeDialog(this.window_PhoneTypeDialog, "PhoneType");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new PhoneType object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new PhoneType() in the front end.
		// we get it from the back end.
		final PhoneType aPhoneType = getPhoneTypeService().getNewPhoneType();
		aPhoneType.setPhoneTypePriority(0); // initialization
		aPhoneType.setNewRecord(true);
		setPhoneType(aPhoneType);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.phoneTypeCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getPhoneType().isNewRecord()) {
			this.phoneTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.phoneTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.phoneTypeDesc.setReadonly(isReadOnly("PhoneTypeDialog_phoneTypeDesc"));
		this.phoneTypePriority.setReadonly(isReadOnly("PhoneTypeDialog_phoneTypePriority"));
		this.phoneTypeIsActive.setDisabled(isReadOnly("PhoneTypeDialog_phoneTypeIsActive"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.phoneType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.phoneTypeCode.setReadonly(true);
		this.phoneTypeDesc.setReadonly(true);
		this.phoneTypePriority.setReadonly(true);
		this.phoneTypeIsActive.setDisabled(true);

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
		this.phoneTypeCode.setValue("");
		this.phoneTypeDesc.setValue("");
		this.phoneTypePriority.setText("");
		this.phoneTypeIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final PhoneType aPhoneType = new PhoneType();
		BeanUtils.copyProperties(getPhoneType(), aPhoneType);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the PhoneType object with the components data
		doWriteComponentsToBean(aPhoneType);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aPhoneType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aPhoneType.getRecordType()).equals("")) {
				aPhoneType.setVersion(aPhoneType.getVersion() + 1);
				if (isNew) {
					aPhoneType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPhoneType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPhoneType.setNewRecord(true);
				}
			}
		} else {
			aPhoneType.setVersion(aPhoneType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aPhoneType, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_PhoneTypeDialog, "PhoneType");
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
	 * @param aPhoneType
	 *            (PhoneType)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(PhoneType aPhoneType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPhoneType.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aPhoneType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPhoneType.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aPhoneType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPhoneType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,aPhoneType);
				}
				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aPhoneType))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;

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

			aPhoneType.setTaskId(taskId);
			aPhoneType.setNextTaskId(nextTaskId);
			aPhoneType.setRoleCode(getRole());
			aPhoneType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPhoneType, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aPhoneType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPhoneType,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPhoneType, tranType);
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
		PhoneType aPhoneType = (PhoneType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getPhoneTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getPhoneTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getPhoneTypeService().doApprove(auditHeader);

						if (aPhoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getPhoneTypeService().doReject(auditHeader);

						if (aPhoneType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_PhoneTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_PhoneTypeDialog, auditHeader);
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
	// ++++++++++++++++++ WorkFlow Details +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * Get Audit Header Details
	 * 
	 * @param aPhoneType
	 *            (PhoneType)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(PhoneType aPhoneType, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aPhoneType.getBefImage(), aPhoneType);
		return new AuditHeader(String.valueOf(aPhoneType.getId()), null, null,
				null, auditDetail, aPhoneType.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_PhoneTypeDialog,auditHeader);
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
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		final JdbcSearchObject<PhoneType> soPhoneType = getPhoneTypeListCtrl().getSearchObj();
		getPhoneTypeListCtrl().pagingPhoneTypeList.setActivePage(0);
		getPhoneTypeListCtrl().getPagedListWrapper().setSearchObject(soPhoneType);
		if (getPhoneTypeListCtrl().listBoxPhoneType != null) {
			getPhoneTypeListCtrl().listBoxPhoneType.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("PhoneType");
		notes.setReference(getPhoneType().getPhoneTypeCode());
		notes.setVersion(getPhoneType().getVersion());
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

	public PhoneType getPhoneType() {
		return this.phoneType;
	}
	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

	public void setPhoneTypeService(PhoneTypeService phoneTypeService) {
		this.phoneTypeService = phoneTypeService;
	}
	public PhoneTypeService getPhoneTypeService() {
		return this.phoneTypeService;
	}

	public void setPhoneTypeListCtrl(PhoneTypeListCtrl phoneTypeListCtrl) {
		this.phoneTypeListCtrl = phoneTypeListCtrl;
	}
	public PhoneTypeListCtrl getPhoneTypeListCtrl() {
		return this.phoneTypeListCtrl;
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
