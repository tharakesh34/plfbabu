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
 * FileName    		:  PenaltyCodeDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.rmtmasters.penaltycode;

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
import com.pennant.backend.model.rmtmasters.PenaltyCode;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.PenaltyCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/PenaltyCode/penaltyCodeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class PenaltyCodeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8261552875575897741L;
	private final static Logger logger = Logger.getLogger(PenaltyCodeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window   window_PenaltyCodeDialog;// autoWired
	protected Uppercasebox  penaltyType; 			// autoWired
	protected Textbox  penaltyDesc; 			// autoWired
	protected Checkbox penaltyIsActive; 		// autoWired

	protected Label 	 recordStatus; 			// autoWired
	protected Radiogroup userAction;
	protected Groupbox 	 groupboxWf;
	protected Row 		 statusRow;

	// not auto wired variables
	private PenaltyCode penaltyCode; // overHanded per parameter
	private transient PenaltyCodeListCtrl penaltyCodeListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  oldVar_penaltyType;
	private transient String  oldVar_penaltyDesc;
	private transient boolean oldVar_penaltyIsActive;
	private transient String  oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String 		btnCtroller_ClassPrefix = "button_PenaltyCodeDialog_";
	private transient ButtonStatusCtrl  btnCtrl;
	
	protected Button btnNew; 	// autoWire
	protected Button btnEdit; 	// autoWire
	protected Button btnDelete; // autoWire
	protected Button btnSave; 	// autoWire
	protected Button btnCancel; // autoWire
	protected Button btnClose; 	// autoWire
	protected Button btnHelp; 	// autoWire
	protected Button btnNotes; 	// autoWire
	
	// ServiceDAOs / Domain Classes
	private transient PenaltyCodeService penaltyCodeService;
	private transient PagedListService 	 pagedListService;

	/**
	 * default constructor.<br>
	 */
	public PenaltyCodeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected PenaltyCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PenaltyCodeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);


		// READ OVERHANDED parameters !
		if (args.containsKey("penaltyCode")) {
			this.penaltyCode = (PenaltyCode) args.get("penaltyCode");
			PenaltyCode befImage =new PenaltyCode();
			BeanUtils.copyProperties(this.penaltyCode, befImage);
			this.penaltyCode.setBefImage(befImage);

			setPenaltyCode(this.penaltyCode);
		} else {
			setPenaltyCode(null);
		}

		doLoadWorkFlow(this.penaltyCode.isWorkflow(),
				this.penaltyCode.getWorkflowId(),
				this.penaltyCode.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "PenaltyCodeDialog");
		}

		// READ OVERHANDED parameters !
		// we get the penaltyCodeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete penaltyCode here.
		if (args.containsKey("penaltyCodeListCtrl")) {
			setPenaltyCodeListCtrl((PenaltyCodeListCtrl) args
					.get("penaltyCodeListCtrl"));
		} else {
			setPenaltyCodeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getPenaltyCode());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.penaltyType.setMaxlength(8);
		this.penaltyDesc.setMaxlength(50);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
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
		getUserWorkspace().alocateAuthorities("PenaltyCodeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PenaltyCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PenaltyCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PenaltyCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PenaltyCodeDialog_btnSave"));
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
	public void onClose$window_PenaltyCodeDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_PenaltyCodeDialog);
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
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}

		if(close){
			closeDialog(this.window_PenaltyCodeDialog, "PenaltyCode");
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
	 * @param aPenaltyCode
	 *            PenaltyCode
	 */
	public void doWriteBeanToComponents(PenaltyCode aPenaltyCode) {
		logger.debug("Entering");
		this.penaltyType.setValue(aPenaltyCode.getPenaltyType());
		this.penaltyDesc.setValue(aPenaltyCode.getPenaltyDesc());
		this.penaltyIsActive.setChecked(aPenaltyCode.isPenaltyIsActive());
		this.recordStatus.setValue(aPenaltyCode.getRecordStatus());
		
		if(aPenaltyCode.isNew() || aPenaltyCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.penaltyIsActive.setChecked(true);
			this.penaltyIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPenaltyCode
	 */
	public void doWriteComponentsToBean(PenaltyCode aPenaltyCode) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aPenaltyCode.setPenaltyType(this.penaltyType.getValue().toUpperCase());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenaltyCode.setPenaltyDesc(this.penaltyDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aPenaltyCode.setPenaltyIsActive(this.penaltyIsActive.isChecked());
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

		aPenaltyCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aPenaltyCode
	 * @throws InterruptedException
	 */
	public void doShowDialog(PenaltyCode aPenaltyCode) throws InterruptedException {
		logger.debug("Entering");
		// if aPenaltyCode == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aPenaltyCode == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aPenaltyCode = getPenaltyCodeService().getNewPenaltyCode();

			setPenaltyCode(aPenaltyCode);
		} else {
			setPenaltyCode(aPenaltyCode);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aPenaltyCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.penaltyType.focus();
		} else {
			this.penaltyDesc.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aPenaltyCode.getRecordType()).equals("")){
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
			doWriteBeanToComponents(aPenaltyCode);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_PenaltyCodeDialog);
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
		this.oldVar_penaltyType = this.penaltyType.getValue();
		this.oldVar_penaltyDesc = this.penaltyDesc.getValue();
		this.oldVar_penaltyIsActive = this.penaltyIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.penaltyType.setValue(this.oldVar_penaltyType);
		this.penaltyDesc.setValue(this.oldVar_penaltyDesc);
		this.penaltyIsActive.setChecked(this.oldVar_penaltyIsActive);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
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
		//To clear the Error Messages
		doClearMessage();

		if (this.oldVar_penaltyType != this.penaltyType.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyDesc != this.penaltyDesc.getValue()) {
			return true;
		}
		if (this.oldVar_penaltyIsActive != this.penaltyIsActive.isChecked()) {
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

		if (!this.penaltyType.isReadonly()){
			this.penaltyType.setConstraint(new PTStringValidator(Labels.getLabel("label_PenaltyCodeDialog_PenaltyType.value"), 
					PennantRegularExpressions.REGEX_ALPHA, true));
		}	
		if (!this.penaltyDesc.isReadonly()){
			this.penaltyDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_PenaltyCodeDialog_PenaltyDesc.value"), 
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
		this.penaltyType.setConstraint("");
		this.penaltyDesc.setConstraint("");
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
		logger.debug("Enterring");
		this.penaltyType.setErrorMessage("");
		this.penaltyDesc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<PenaltyCode> soObject = getPenaltyCodeListCtrl().getSearchObj();
		getPenaltyCodeListCtrl().pagingPenaltyCodeList.setActivePage(0);
		getPenaltyCodeListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getPenaltyCodeListCtrl().listBoxPenaltyCode!=null){
			getPenaltyCodeListCtrl().listBoxPenaltyCode.getListModel();
		}
		logger.debug("Leaving");
	} 

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a PenaltyCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final PenaltyCode aPenaltyCode = new PenaltyCode();
		BeanUtils.copyProperties(getPenaltyCode(), aPenaltyCode);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aPenaltyCode.getPenaltyType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aPenaltyCode.getRecordType()).equals("")){
				aPenaltyCode.setVersion(aPenaltyCode.getVersion()+1);
				aPenaltyCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aPenaltyCode.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aPenaltyCode,tranType)){
					refreshList();
					closeDialog(this.window_PenaltyCodeDialog, "PenaltyCode"); 
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new PenaltyCode object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new PenaltyCode() in the frontEnd.
		// we get it from the backEnd.
		final PenaltyCode aPenaltyCode = getPenaltyCodeService()
				.getNewPenaltyCode();
		aPenaltyCode.setNewRecord(true);
		setPenaltyCode(aPenaltyCode);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.penaltyType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getPenaltyCode().isNewRecord()){
			this.penaltyType.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.penaltyType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.penaltyDesc
				.setReadonly(isReadOnly("PenaltyCodeDialog_penaltyDesc"));
		this.penaltyIsActive
				.setDisabled(isReadOnly("PenaltyCodeDialog_penaltyIsActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.penaltyCode.isNewRecord()){
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
		this.penaltyType.setReadonly(true);
		this.penaltyDesc.setReadonly(true);
		this.penaltyIsActive.setDisabled(true);

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
		this.penaltyType.setValue("");
		this.penaltyDesc.setValue("");
		this.penaltyIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final PenaltyCode aPenaltyCode = new PenaltyCode();
		BeanUtils.copyProperties(getPenaltyCode(), aPenaltyCode);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the PenaltyCode object with the components data
		doWriteComponentsToBean(aPenaltyCode);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aPenaltyCode.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aPenaltyCode.getRecordType()).equals("")){
				aPenaltyCode.setVersion(aPenaltyCode.getVersion()+1);
				if(isNew){
					aPenaltyCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aPenaltyCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPenaltyCode.setNewRecord(true);
				}
			}
		}else{
			aPenaltyCode.setVersion(aPenaltyCode.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aPenaltyCode,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_PenaltyCodeDialog, "PenaltyCode");
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
	 * @param aPenaltyCode (PenaltyCode)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(PenaltyCode aPenaltyCode,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aPenaltyCode.setLastMntBy(getUserWorkspace().getLoginUserDetails()
				.getLoginUsrID());
		aPenaltyCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPenaltyCode.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aPenaltyCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPenaltyCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aPenaltyCode);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aPenaltyCode))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
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

			aPenaltyCode.setTaskId(taskId);
			aPenaltyCode.setNextTaskId(nextTaskId);
			aPenaltyCode.setRoleCode(getRole());
			aPenaltyCode.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aPenaltyCode, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aPenaltyCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aPenaltyCode, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aPenaltyCode, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
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
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		PenaltyCode aPenaltyCode = (PenaltyCode) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getPenaltyCodeService().delete(auditHeader);

						deleteNotes=true;	
					} else {
						auditHeader = getPenaltyCodeService().saveOrUpdate(
								auditHeader);
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getPenaltyCodeService().doApprove(auditHeader);

						if(aPenaltyCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getPenaltyCodeService().doReject(
								auditHeader);
						if(aPenaltyCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_PenaltyCodeDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_PenaltyCodeDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					
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
	 * @param aPenaltyCode
	 *            (PenaltyCode)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(PenaltyCode aPenaltyCode, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aPenaltyCode.getBefImage(), aPenaltyCode);
		return new AuditHeader(String.valueOf(aPenaltyCode.getId()), null,
				null, null, auditDetail, aPenaltyCode.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_PenaltyCodeDialog,
					auditHeader);
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
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
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
		notes.setModuleName("PenaltyCode");
		notes.setReference(getPenaltyCode().getPenaltyType());
		notes.setVersion(getPenaltyCode().getVersion());
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

	public PenaltyCode getPenaltyCode() {
		return this.penaltyCode;
	}
	public void setPenaltyCode(PenaltyCode penaltyCode) {
		this.penaltyCode = penaltyCode;
	}

	public void setPenaltyCodeService(PenaltyCodeService penaltyCodeService) {
		this.penaltyCodeService = penaltyCodeService;
	}
	public PenaltyCodeService getPenaltyCodeService() {
		return this.penaltyCodeService;
	}

	public void setPenaltyCodeListCtrl(PenaltyCodeListCtrl penaltyCodeListCtrl) {
		this.penaltyCodeListCtrl = penaltyCodeListCtrl;
	}
	public PenaltyCodeListCtrl getPenaltyCodeListCtrl() {
		return this.penaltyCodeListCtrl;
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
