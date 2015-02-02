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
 * FileName    		:  CustomerGroupDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customergroup;

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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.LongValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerGroup/customerGroupDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerGroupDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4865083782879144591L;
	private final static Logger logger = Logger.getLogger(CustomerGroupDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerGroupDialog; // autoWired
	protected Longbox		custGrpID; 					// autoWired
	protected Textbox		custGrpCode; 				// autoWired
	protected Textbox		custGrpDesc; 				// autoWired
   	protected Textbox		custGrpRO1; 				// autoWired
	protected Longbox		custGrpLimit; 				// autoWired
	protected Checkbox 		custGrpIsActive; 			// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;


	// not auto wired variables
	private CustomerGroup customerGroup; // overHanded per parameter
	private transient CustomerGroupListCtrl customerGroupListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient long  		oldVar_custGrpID;
	private transient String  		oldVar_custGrpCode;
	private transient String  		oldVar_custGrpDesc;
	private transient String  		oldVar_custGrpRO1;
	private transient Long  		oldVar_custGrpLimit;
	private transient boolean  		oldVar_custGrpIsActive;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerGroupDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave;		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire
	
	protected Button 	btnSearchCustGrpRO1; 	// autowire
	protected Textbox 	lovDescCustGrpRO1Name;
	private transient String 		oldVar_lovDescCustGrpRO1Name;
	
	// ServiceDAOs / Domain Classes
	private transient CustomerGroupService customerGroupService;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerGroupDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerGroup object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerGroupDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("customerGroup")) {
			this.customerGroup = (CustomerGroup) args.get("customerGroup");
			CustomerGroup befImage =new CustomerGroup();
			BeanUtils.copyProperties(this.customerGroup, befImage);
			this.customerGroup.setBefImage(befImage);
			
			setCustomerGroup(this.customerGroup);
		} else {
			setCustomerGroup(null);
		}
	
		doLoadWorkFlow(this.customerGroup.isWorkflow(),this.customerGroup.getWorkflowId(),this.customerGroup.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerGroupDialog");
		}
	
		// READ OVERHANDED parameters !
		// we get the customerGroupListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerGroup here.
		if (args.containsKey("customerGroupListCtrl")) {
			setCustomerGroupListCtrl((CustomerGroupListCtrl) args.get("customerGroupListCtrl"));
		} else {
			setCustomerGroupListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerGroup());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.custGrpCode.setMaxlength(8);
		this.custGrpDesc.setMaxlength(50);
		this.custGrpRO1.setMaxlength(8);
	  	this.custGrpLimit.setMaxlength(8);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			
		}else{
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
		getUserWorkspace().alocateAuthorities("CustomerGroupDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerGroupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerGroupDialog_btnSave"));
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
	public void onClose$window_CustomerGroupDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerGroupDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" +event.toString());
		doNew();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" +event.toString());
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
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			closeDialog(this.window_CustomerGroupDialog, "CustomerGroup");
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
	 * @param aCustomerGroup
	 *            CustomerGroup
	 */
	public void doWriteBeanToComponents(CustomerGroup aCustomerGroup) {
		logger.debug("Entering");
		this.custGrpID.setValue(aCustomerGroup.getCustGrpID());
		this.custGrpCode.setValue(aCustomerGroup.getCustGrpCode());
		this.custGrpDesc.setValue(aCustomerGroup.getCustGrpDesc());
		this.custGrpRO1.setValue(aCustomerGroup.getCustGrpRO1());
		this.lovDescCustGrpRO1Name.setValue(StringUtils.trimToEmpty(aCustomerGroup.getCustGrpRO1()).equals("")?""
				:aCustomerGroup.getCustGrpRO1()+PennantConstants.KEY_SEPERATOR+aCustomerGroup.getLovDescCustGrpRO1Name());
	  	this.custGrpLimit.setValue(aCustomerGroup.getCustGrpLimit());
		this.custGrpIsActive.setChecked(aCustomerGroup.isCustGrpIsActive());
		this.recordStatus.setValue(aCustomerGroup.getRecordStatus());
		
		if(aCustomerGroup.isNew() || aCustomerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.custGrpIsActive.setChecked(true);
			this.custGrpIsActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerGroup
	 */
	public void doWriteComponentsToBean(CustomerGroup aCustomerGroup) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aCustomerGroup.setCustGrpID(this.custGrpID.longValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerGroup.setCustGrpCode(this.custGrpCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCustomerGroup.setCustGrpDesc(this.custGrpDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerGroup.setLovDescCustGrpRO1Name(this.lovDescCustGrpRO1Name.getValue());
		    aCustomerGroup.setCustGrpRO1(this.custGrpRO1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerGroup.setCustGrpLimit(this.custGrpLimit.longValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerGroup.setCustGrpIsActive(this.custGrpIsActive.isChecked());
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
		
		aCustomerGroup.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerGroup
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerGroup aCustomerGroup) throws InterruptedException {
		logger.debug("Entering");
		
		// if aCustomerGroup == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCustomerGroup == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCustomerGroup = getCustomerGroupService().getNewCustomerGroup();
			
			setCustomerGroup(aCustomerGroup);
		} else {
			setCustomerGroup(aCustomerGroup);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCustomerGroup.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custGrpCode.focus();
		} else {
			if (isWorkFlowEnabled()){
				this.custGrpCode.focus();
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
			doWriteBeanToComponents(aCustomerGroup);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerGroupDialog);
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
		this.oldVar_custGrpCode = this.custGrpCode.getValue();
		this.oldVar_custGrpDesc = this.custGrpDesc.getValue();
	  	this.oldVar_custGrpRO1 = this.custGrpRO1.getValue();
	  	this.oldVar_lovDescCustGrpRO1Name = this.lovDescCustGrpRO1Name.getValue();
		this.oldVar_custGrpLimit = this.custGrpLimit.longValue();
		this.oldVar_custGrpIsActive = this.custGrpIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custGrpID.setValue(this.oldVar_custGrpID);
		this.custGrpCode.setValue(this.oldVar_custGrpCode);
		this.custGrpDesc.setValue(this.oldVar_custGrpDesc);
		this.custGrpRO1.setValue(this.oldVar_custGrpRO1);
		this.lovDescCustGrpRO1Name.setValue(this.oldVar_lovDescCustGrpRO1Name);
		this.custGrpLimit.setValue(this.oldVar_custGrpLimit);
		this.custGrpIsActive.setChecked(this.oldVar_custGrpIsActive);
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
		// To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_custGrpCode != this.custGrpCode.getValue()) {
			return true;
		}
		if (this.oldVar_custGrpDesc != this.custGrpDesc.getValue()) {
			return true;
		}
		if (this.oldVar_custGrpRO1 != this.custGrpRO1.getValue()) {
			return true;
		}
		if (this.oldVar_custGrpLimit != this.custGrpLimit.longValue()) {
			return true;
		}
		if (this.oldVar_custGrpIsActive != this.custGrpIsActive.isChecked()) {
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
		
		if (!this.custGrpCode.isReadonly()){
			this.custGrpCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerGroupDialog_CustGrpCode.value"),null,true));
		}	
		if (!this.custGrpDesc.isReadonly()){
			this.custGrpDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerGroupDialog_CustGrpDesc.value"),null,true));
		}	
		if (!this.custGrpLimit.isReadonly()){
			this.custGrpLimit.setConstraint(new LongValidator(8,Labels.getLabel(
					"label_CustomerGroupDialog_CustGrpLimit.value")));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custGrpCode.setConstraint("");
		this.custGrpDesc.setConstraint("");
		this.custGrpLimit.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		this.lovDescCustGrpRO1Name.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerGroupDialog_CustGrpRO1.value"),null,true));
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		this.lovDescCustGrpRO1Name.setConstraint("");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custGrpCode.setErrorMessage("");
		this.custGrpDesc.setErrorMessage("");
		this.lovDescCustGrpRO1Name.setErrorMessage("");
		this.custGrpLimit.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCustGrpRO1(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerGroupDialog, "RelationshipOfficer");
		if (dataObject instanceof String) {
			this.custGrpRO1.setValue(dataObject.toString());
			this.lovDescCustGrpRO1Name.setValue("");
		} else {
			RelationshipOfficer details = (RelationshipOfficer) dataObject;
			if (details != null) {
				this.custGrpRO1.setValue(details.getROfficerCode());
				this.lovDescCustGrpRO1Name.setValue(details.getROfficerCode()
						+ "-" + details.getROfficerDesc());
			}
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerGroup object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final CustomerGroup aCustomerGroup = new CustomerGroup();
		BeanUtils.copyProperties(getCustomerGroup(), aCustomerGroup);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCustomerGroup.getCustGrpID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerGroup.getRecordType()).equals("")){
				aCustomerGroup.setVersion(aCustomerGroup.getVersion()+1);
				aCustomerGroup.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aCustomerGroup.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(doProcess(aCustomerGroup,tranType)){
					refreshList();
					closeDialog(this.window_CustomerGroupDialog, "CustomerGroup"); 
				}
			}catch (DataAccessException e){
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerGroup object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerGroup() in the frontEnd.
		// we get it from the backEnd.
		final CustomerGroup aCustomerGroup = getCustomerGroupService().getNewCustomerGroup();
		setCustomerGroup(aCustomerGroup);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.custGrpID.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getCustomerGroup().isNewRecord()){
		  	this.custGrpID.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.custGrpCode.setReadonly(isReadOnly("CustomerGroupDialog_custGrpCode"));
		}else{
			this.custGrpID.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.custGrpCode.setReadonly(true);
		}
		
			this.custGrpDesc.setReadonly(isReadOnly("CustomerGroupDialog_custGrpDesc"));
			this.custGrpRO1.setReadonly(isReadOnly("CustomerGroupDialog_custGrpRO1"));
			this.btnSearchCustGrpRO1.setDisabled(isReadOnly("CustomerGroupDialog_custGrpRO1"));
			this.custGrpLimit.setReadonly(isReadOnly("CustomerGroupDialog_custGrpLimit"));
			this.custGrpIsActive.setDisabled(isReadOnly("CustomerGroupDialog_custGrpIsActive"));
			
			if (isWorkFlowEnabled()){
				for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.customerGroup.isNewRecord()){
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
		this.custGrpID.setReadonly(true);
		this.custGrpCode.setReadonly(true);
		this.custGrpDesc.setReadonly(true);
		this.custGrpRO1.setReadonly(true);
		this.custGrpLimit.setReadonly(true);
		this.custGrpIsActive.setDisabled(true);
		
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
		this.custGrpCode.setValue("");
		this.custGrpDesc.setValue("");
		this.custGrpRO1.setText("");
		this.custGrpLimit.setText("0");
		this.custGrpIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final CustomerGroup aCustomerGroup = new CustomerGroup();
		BeanUtils.copyProperties(getCustomerGroup(), aCustomerGroup);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerGroup object with the components data
		doWriteComponentsToBean(aCustomerGroup);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aCustomerGroup.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			if (StringUtils.trimToEmpty(aCustomerGroup.getRecordType()).equals("")){
				aCustomerGroup.setVersion(aCustomerGroup.getVersion()+1);
				if(isNew){
					aCustomerGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerGroup.setNewRecord(true);
				}
			}
		}else{
			aCustomerGroup.setVersion(aCustomerGroup.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aCustomerGroup,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CustomerGroupDialog, "CustomerGroup");
			}

		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerGroup
	 *            (CustomerGroup)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerGroup aCustomerGroup,String tranType){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aCustomerGroup.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerGroup.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerGroup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerGroup);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCustomerGroup))) {
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

			aCustomerGroup.setTaskId(taskId);
			aCustomerGroup.setNextTaskId(nextTaskId);
			aCustomerGroup.setRoleCode(getRole());
			aCustomerGroup.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aCustomerGroup, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerGroup);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerGroup, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerGroup, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
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
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		CustomerGroup aCustomerGroup = (CustomerGroup)auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCustomerGroupService().delete(auditHeader);
						deleteNotes = true;
					}else{
						auditHeader = getCustomerGroupService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCustomerGroupService().doApprove(auditHeader);
						
						if (aCustomerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
						
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCustomerGroupService().doReject(auditHeader);
						
						if (aCustomerGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
						
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerGroupDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerGroupDialog, auditHeader);
				retValue = ErrorControl.showErrorControl(this.window_CustomerGroupDialog, auditHeader);
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
			logger.debug("Leaving");
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
	 * @param aCustomerAdditionalDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerGroup aCustomerGroup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerGroup.getBefImage(),aCustomerGroup);
		return new AuditHeader(String.valueOf(aCustomerGroup.getId()), null,
				null, null, auditDetail, aCustomerGroup.getUserDetails(), getOverideMap());
	}
	
	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("",e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CustomerGroupDialog, auditHeader);
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
		logger.debug("Entering" +event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		Notes notes = new Notes();
		notes.setModuleName("CustomerGroup");
		notes.setReference(String.valueOf(getCustomerGroup().getCustGrpID()));
		notes.setVersion(getCustomerGroup().getVersion());
		
		map.put("notes", notes);
		map.put("control", this);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}
	
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	
	
	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering ");
		final JdbcSearchObject<CustomerGroup> soCustomerAdditionalDetail = getCustomerGroupListCtrl().getSearchObj();
		getCustomerGroupListCtrl().pagingCustomerGroupList.setActivePage(0);
		getCustomerGroupListCtrl().getPagedListWrapper().setSearchObject(soCustomerAdditionalDetail);
		if (getCustomerGroupListCtrl().listBoxCustomerGroup != null) {
			getCustomerGroupListCtrl().listBoxCustomerGroup.getListModel();
		}
		logger.debug("Leaving ");
	}
	
	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerGroup");
		notes.setReference(String.valueOf(getCustomerGroup().getId()));
		notes.setVersion(getCustomerGroup().getVersion());
		logger.debug("Leaving ");
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

	public CustomerGroup getCustomerGroup() {
		return this.customerGroup;
	}
	public void setCustomerGroup(CustomerGroup customerGroup) {
		this.customerGroup = customerGroup;
	}

	public void setCustomerGroupService(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}
	public CustomerGroupService getCustomerGroupService() {
		return this.customerGroupService;
	}

	public void setCustomerGroupListCtrl(CustomerGroupListCtrl customerGroupListCtrl) {
		this.customerGroupListCtrl = customerGroupListCtrl;
	}
	public CustomerGroupListCtrl getCustomerGroupListCtrl() {
		return this.customerGroupListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}
	
}
