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
 * FileName    		:  SalutationDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.salutation;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.SalutationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/Salutation/salutationDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class SalutationDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3545695595801290469L;
	private final static Logger logger = Logger.getLogger(SalutationDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window 	window_SalutationDialog; // autoWired
	protected Textbox 	salutationCode; 		 // autoWired
	protected Textbox 	saluationDesc; 			 // autoWired
	protected Checkbox 	salutationIsActive; 	 // autoWired
	protected Combobox 	salutationGenderCode; 	 // autoWired

	protected Label 		recordStatus; 		 // autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;


	// not auto wired variables
	private Salutation salutation; // overHanded per parameter
	private transient SalutationListCtrl salutationListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String 	oldVar_salutationCode;
	private transient String 	oldVar_saluationDesc;
	private transient boolean 	oldVar_salutationIsActive;
	private transient String 	oldVar_salutationGenderCode;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_SalutationDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire

	// ServiceDAOs / Domain Classes
	private transient SalutationService salutationService;
	private transient PagedListService pagedListService;


	/**
	 * default constructor.<br>
	 */
	public SalutationDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Salutation object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SalutationDialog(Event event) throws Exception {
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
		if (args.containsKey("salutation")) {
			this.salutation = (Salutation) args.get("salutation");
			Salutation befImage = new Salutation();
			BeanUtils.copyProperties(this.salutation, befImage);
			this.salutation.setBefImage(befImage);

			setSalutation(this.salutation);
		} else {
			setSalutation(null);
		}

		doLoadWorkFlow(this.salutation.isWorkflow(), this.salutation.getWorkflowId(), this.salutation.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SalutationDialog");
		}

		// READ OVERHANDED parameters !
		// we get the salutationListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete salutation here.
		if (args.containsKey("salutationListCtrl")) {
			setSalutationListCtrl((SalutationListCtrl) args.get("salutationListCtrl"));
		} else {
			setSalutationListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSalutation());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Empty sent any required attributes
		this.salutationCode.setMaxlength(8);
		this.saluationDesc.setMaxlength(50);
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
		getUserWorkspace().alocateAuthorities("SalutationDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SalutationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SalutationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SalutationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SalutationDialog_btnSave"));
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
	public void onClose$window_SalutationDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_SalutationDialog);
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
			closeDialog(this.window_SalutationDialog, "Salutation");
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
	 * @param aSalutation
	 *            Salutation
	 */
	public void doWriteBeanToComponents(Salutation aSalutation) {
		logger.debug("Entering");

		this.salutationCode.setValue(aSalutation.getSalutationCode());
		this.saluationDesc.setValue(aSalutation.getSaluationDesc());
		this.salutationIsActive.setChecked(aSalutation.isSalutationIsActive());

		String genderCode = "";
		if (StringUtils.trimToEmpty(aSalutation.getSalutationGenderCode()).length() > 0) {
			genderCode = StringUtils.trimToEmpty(aSalutation.getSalutationGenderCode());
		}
		List<ValueLabel> genderList = PennantAppUtil.getSalutationGenderCode();
		Comboitem comboitem;
		comboitem = new Comboitem();
		comboitem.setValue("");
		comboitem.setLabel(Labels.getLabel("common.Select"));
		salutationGenderCode.appendChild(comboitem);
		salutationGenderCode.setSelectedIndex(0);
		for (int i = 0; i < genderList.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(genderList.get(i).getValue());
			comboitem.setLabel(genderList.get(i).getLabel());
			salutationGenderCode.appendChild(comboitem);
			if (StringUtils.trimToEmpty(genderCode).equals(genderList.get(i).getValue())) {
				salutationGenderCode.setSelectedItem(comboitem);
			}
		}
		this.recordStatus.setValue(aSalutation.getRecordStatus());
		
		if(aSalutation.isNew() || (aSalutation.getRecordType() != null ? aSalutation.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.salutationIsActive.setChecked(true);
			this.salutationIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSalutation
	 */
	public void doWriteComponentsToBean(Salutation aSalutation) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSalutation.setSalutationCode(this.salutationCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalutation.setSaluationDesc(this.saluationDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aSalutation.setSalutationIsActive(this.salutationIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(!this.salutationGenderCode.isDisabled() && this.salutationGenderCode.getSelectedIndex()<1){
				throw new WrongValueException(salutationGenderCode, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_SalutationDialog_SalutationGenderCode.value")}));
			}
			aSalutation.setSalutationGenderCode(this.salutationGenderCode.getSelectedItem().getValue().toString());
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
		aSalutation.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSalutation
	 * @throws InterruptedException
	 */
	public void doShowDialog(Salutation aSalutation) throws InterruptedException {
		logger.debug("Entering");

		// if aSalutation == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSalutation == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aSalutation = getSalutationService().getNewSalutation();

			setSalutation(aSalutation);
		} else {
			setSalutation(aSalutation);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSalutation.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.salutationCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.saluationDesc.focus();
				if (!StringUtils.trimToEmpty(aSalutation.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aSalutation);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SalutationDialog);
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
		this.oldVar_salutationCode = this.salutationCode.getValue();
		this.oldVar_saluationDesc = this.saluationDesc.getValue();
		this.oldVar_salutationIsActive = this.salutationIsActive.isChecked();
		if (this.salutationGenderCode.getSelectedItem() != null) {
			this.oldVar_salutationGenderCode = this.salutationGenderCode.getSelectedItem().getValue().toString();
		}
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		this.salutationCode.setValue(this.oldVar_salutationCode);
		this.saluationDesc.setValue(this.oldVar_saluationDesc);
		this.salutationIsActive.setChecked(this.oldVar_salutationIsActive);
		for (int i = 0; i < salutationGenderCode.getItemCount(); i++) {
			if (salutationGenderCode.getItemAtIndex(i).getValue().toString().equals(this.oldVar_salutationGenderCode)) {
				this.salutationGenderCode.setSelectedIndex(i);
				break;
			}
			this.salutationGenderCode.setSelectedIndex(0);
		}
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

		if (this.oldVar_salutationCode != this.salutationCode.getValue()) {
			return true;
		}
		if (this.oldVar_saluationDesc != this.saluationDesc.getValue()) {
			return true;
		}
		if (this.oldVar_salutationIsActive != this.salutationIsActive.isChecked()) {
			return true;
		}
		if (this.oldVar_salutationGenderCode != this.salutationGenderCode.getSelectedItem().getValue().toString()) {
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
		if (!this.salutationCode.isReadonly()){
			this.salutationCode.setConstraint(new PTStringValidator(Labels.getLabel("label_SalutationDialog_SalutationCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}	

		if (!this.saluationDesc.isReadonly()){
			this.saluationDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_SalutationDialog_SaluationDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (this.salutationGenderCode.isReadonly()) {
			this.salutationGenderCode.setConstraint(new PTStringValidator(Labels.getLabel(
					"label_SalutationDialog_SalutationGenderCode.value"), null, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.salutationCode.setConstraint("");
		this.saluationDesc.setConstraint("");
		this.salutationGenderCode.setConstraint("");
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
		this.salutationCode.setErrorMessage("");
		this.saluationDesc.setErrorMessage("");
		this.salutationGenderCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Salutation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final Salutation aSalutation = new Salutation();
		BeanUtils.copyProperties(getSalutation(), aSalutation);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aSalutation.getSalutationCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSalutation.getRecordType()).equals("")) {
				aSalutation.setVersion(aSalutation.getVersion() + 1);
				aSalutation.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSalutation.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aSalutation, tranType)) {
					refreshList();
					closeDialog(this.window_SalutationDialog, "Salutation");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Salutation object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new Salutation() in the frontEnd.
		// we get it from the backEnd.
		final Salutation aSalutation = getSalutationService().getNewSalutation();
		aSalutation.setNewRecord(true);
		setSalutation(aSalutation);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.salutationCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getSalutation().isNewRecord()) {
			this.salutationCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.salutationCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.saluationDesc.setReadonly(isReadOnly("SalutationDialog_saluationDesc"));
		this.salutationIsActive.setDisabled(isReadOnly("SalutationDialog_salutationIsActive"));
		this.salutationGenderCode.setDisabled(isReadOnly("SalutationDialog_salutationGenderCode"));
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.salutation.isNewRecord()) {
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

		this.salutationCode.setReadonly(true);
		this.saluationDesc.setReadonly(true);
		this.salutationIsActive.setDisabled(true);
		this.salutationGenderCode.setDisabled(true);

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
		this.salutationCode.setValue("");
		this.saluationDesc.setValue("");
		this.salutationIsActive.setChecked(false);
		this.salutationGenderCode.setSelectedIndex(0);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Salutation aSalutation = new Salutation();
		BeanUtils.copyProperties(getSalutation(), aSalutation);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Salutation object with the components data
		doWriteComponentsToBean(aSalutation);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSalutation.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSalutation.getRecordType()).equals("")) {
				aSalutation.setVersion(aSalutation.getVersion() + 1);
				if (isNew) {
					aSalutation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSalutation.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSalutation.setNewRecord(true);
				}
			}
		} else {
			aSalutation.setVersion(aSalutation.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aSalutation, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_SalutationDialog, "Salutation");
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
	 * @param aSalutation
	 *            (Salutation)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Salutation aSalutation, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aSalutation.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aSalutation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSalutation.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aSalutation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSalutation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSalutation);
				}
				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aSalutation))) {
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

			aSalutation.setTaskId(taskId);
			aSalutation.setNextTaskId(nextTaskId);
			aSalutation.setRoleCode(getRole());
			aSalutation.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSalutation, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aSalutation);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSalutation, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSalutation, tranType);
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
		Salutation aSalutation = (Salutation) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getSalutationService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getSalutationService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getSalutationService().doApprove(auditHeader);

						if (aSalutation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSalutationService().doReject(auditHeader);

						if (aSalutation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SalutationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SalutationDialog, auditHeader);
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
	// ++++++++++++++++++ Work Flow Details +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aSalutation
	 *            (Salutation)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Salutation aSalutation, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aSalutation.getBefImage(), aSalutation);
		return new AuditHeader(String.valueOf(aSalutation.getId()), null, null,
				null, auditDetail, aSalutation.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_SalutationDialog,	auditHeader);
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
		final JdbcSearchObject<Salutation> soSalutation = getSalutationListCtrl().getSearchObj();
		getSalutationListCtrl().pagingSalutationList.setActivePage(0);
		getSalutationListCtrl().getPagedListWrapper().setSearchObject(soSalutation);
		if (getSalutationListCtrl().listBoxSalutation != null) {
			getSalutationListCtrl().listBoxSalutation.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Salutation");
		notes.setReference(getSalutation().getSalutationCode());
		notes.setVersion(getSalutation().getVersion());
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

	public Salutation getSalutation() {
		return this.salutation;
	}
	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public void setSalutationService(SalutationService salutationService) {
		this.salutationService = salutationService;
	}
	public SalutationService getSalutationService() {
		return this.salutationService;
	}

	public void setSalutationListCtrl(SalutationListCtrl salutationListCtrl) {
		this.salutationListCtrl = salutationListCtrl;
	}
	public SalutationListCtrl getSalutationListCtrl() {
		return this.salutationListCtrl;
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
