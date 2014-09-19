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
 * FileName    		:  FinanceRepayPriorityDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-03-2012    														*
 *                                                                  						*
 * Modified Date    :  16-03-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-03-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financerepaypriority;

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
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.FinanceRepayPriorityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceRepayPriority/financeRepayPriorityDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceRepayPriorityDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 2259700048994840972L;
	private final static Logger logger = Logger.getLogger(FinanceRepayPriorityDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceRepayPriorityDialog; // autowired
	protected Textbox finType; // autowired
	protected Intbox finPriority; // autowired

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;

	// not auto wired vars
	private FinanceRepayPriority financeRepayPriority; // overhanded per param
	private transient FinanceRepayPriorityListCtrl financeRepayPriorityListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_finType;
	private transient int  		oldVar_finPriority;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinanceRepayPriorityDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire

	protected Button btnSearchFinType; // autowire
	protected Textbox lovDescFinTypeName;
	private transient String 		oldVar_lovDescFinTypeName;

	// ServiceDAOs / Domain Classes
	private transient FinanceRepayPriorityService financeRepayPriorityService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();


	/**
	 * default constructor.<br>
	 */
	public FinanceRepayPriorityDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceRepayPriority object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceRepayPriorityDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("financeRepayPriority")) {
			this.financeRepayPriority = (FinanceRepayPriority) args.get("financeRepayPriority");
			FinanceRepayPriority befImage =new FinanceRepayPriority();
			BeanUtils.copyProperties(this.financeRepayPriority, befImage);
			this.financeRepayPriority.setBefImage(befImage);

			setFinanceRepayPriority(this.financeRepayPriority);
		} else {
			setFinanceRepayPriority(null);
		}

		doLoadWorkFlow(this.financeRepayPriority.isWorkflow(),this.financeRepayPriority.getWorkflowId(),this.financeRepayPriority.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceRepayPriorityDialog");
		}


		// READ OVERHANDED params !
		// we get the financeRepayPriorityListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeRepayPriority here.
		if (args.containsKey("financeRepayPriorityListCtrl")) {
			setFinanceRepayPriorityListCtrl((FinanceRepayPriorityListCtrl) args.get("financeRepayPriorityListCtrl"));
		} else {
			setFinanceRepayPriorityListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceRepayPriority());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finPriority.setMaxlength(4);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);

		}else{
			this.groupboxWf.setVisible(false);

		}

		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;

		getUserWorkspace().alocateAuthorities("FinanceRepayPriorityDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceRepayPriorityDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
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
	public void onClose$window_FinanceRepayPriorityDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceRepayPriorityDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
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
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
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
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// GUI Process


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
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_FinanceRepayPriorityDialog, "FinanceRepayPriority");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceRepayPriority
	 *            FinanceRepayPriority
	 */
	public void doWriteBeanToComponents(FinanceRepayPriority aFinanceRepayPriority) {
		logger.debug("Entering") ;
		this.finType.setValue(aFinanceRepayPriority.getFinType());
		this.finPriority.setValue(aFinanceRepayPriority.getFinPriority());

		if (aFinanceRepayPriority.isNewRecord()){
			this.lovDescFinTypeName.setValue("");
		}else{
			this.lovDescFinTypeName.setValue(aFinanceRepayPriority.getFinType()+"-"+aFinanceRepayPriority.getLovDescFinTypeName());
		}
		this.recordStatus.setValue(aFinanceRepayPriority.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceRepayPriority
	 */
	public void doWriteComponentsToBean(FinanceRepayPriority aFinanceRepayPriority) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinanceRepayPriority.setLovDescFinTypeName(this.lovDescFinTypeName.getValue());
			aFinanceRepayPriority.setFinType(this.finType.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceRepayPriority.setFinPriority(this.finPriority.getValue());
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

		aFinanceRepayPriority.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceRepayPriority
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceRepayPriority aFinanceRepayPriority) throws InterruptedException {
		logger.debug("Entering") ;

		// if aFinanceRepayPriority == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aFinanceRepayPriority == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinanceRepayPriority = getFinanceRepayPriorityService().getNewFinanceRepayPriority();

			setFinanceRepayPriority(aFinanceRepayPriority);
		} else {
			setFinanceRepayPriority(aFinanceRepayPriority);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aFinanceRepayPriority.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.finPriority.focus();
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
			doWriteBeanToComponents(aFinanceRepayPriority);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_FinanceRepayPriorityDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_lovDescFinTypeName = this.lovDescFinTypeName.getValue();
		this.oldVar_finPriority = this.finPriority.intValue();	
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finType.setValue(this.oldVar_finType);
		this.lovDescFinTypeName.setValue(this.oldVar_lovDescFinTypeName);
		this.finPriority.setValue(this.oldVar_finPriority);
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
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finPriority != this.finPriority.intValue()) {
			return  true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.finPriority.isReadonly()){
			this.finPriority.setConstraint(new IntValidator(4,Labels.getLabel("label_FinanceRepayPriorityDialog_FinPriority.value")));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finPriority.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a FinanceRepayPriority object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinanceRepayPriority aFinanceRepayPriority = new FinanceRepayPriority();
		BeanUtils.copyProperties(getFinanceRepayPriority(), aFinanceRepayPriority);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceRepayPriority.getFinType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceRepayPriority.getRecordType()).equals("")){
				aFinanceRepayPriority.setVersion(aFinanceRepayPriority.getVersion()+1);
				aFinanceRepayPriority.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aFinanceRepayPriority.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFinanceRepayPriority,tranType)){
					refreshList();
					closeDialog(this.window_FinanceRepayPriorityDialog, "FinanceRepayPriority"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FinanceRepayPriority object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final FinanceRepayPriority aFinanceRepayPriority = getFinanceRepayPriorityService().getNewFinanceRepayPriority();
		aFinanceRepayPriority.setFinPriority(0); // init
		aFinanceRepayPriority.setNewRecord(true);
		setFinanceRepayPriority(aFinanceRepayPriority);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.finType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFinanceRepayPriority().isNewRecord()){
			this.btnSearchFinType.setDisabled(false);
			this.btnCancel.setVisible(false);
		}else{
			this.btnSearchFinType.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("FinanceRepayPriorityDialog_finPriority"), this.finPriority);

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeRepayPriority.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.btnSearchFinType.setDisabled(true);
		this.finPriority.setReadonly(true);

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

		this.finType.setValue("");
		this.lovDescFinTypeName.setValue("");
		this.finPriority.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceRepayPriority aFinanceRepayPriority = new FinanceRepayPriority();
		BeanUtils.copyProperties(getFinanceRepayPriority(), aFinanceRepayPriority);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the FinanceRepayPriority object with the components data
		doWriteComponentsToBean(aFinanceRepayPriority);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aFinanceRepayPriority.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceRepayPriority.getRecordType()).equals("")){
				aFinanceRepayPriority.setVersion(aFinanceRepayPriority.getVersion()+1);
				if(isNew){
					aFinanceRepayPriority.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceRepayPriority.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceRepayPriority.setNewRecord(true);
				}
			}
		}else{
			aFinanceRepayPriority.setVersion(aFinanceRepayPriority.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aFinanceRepayPriority,tranType)){
				doWriteBeanToComponents(aFinanceRepayPriority);
				refreshList();
				closeDialog(this.window_FinanceRepayPriorityDialog, "FinanceRepayPriority");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(FinanceRepayPriority aFinanceRepayPriority,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aFinanceRepayPriority.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceRepayPriority.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceRepayPriority.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aFinanceRepayPriority.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceRepayPriority.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceRepayPriority);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aFinanceRepayPriority))) {
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


			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
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

			aFinanceRepayPriority.setTaskId(taskId);
			aFinanceRepayPriority.setNextTaskId(nextTaskId);
			aFinanceRepayPriority.setRoleCode(getRole());
			aFinanceRepayPriority.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aFinanceRepayPriority, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aFinanceRepayPriority);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aFinanceRepayPriority, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aFinanceRepayPriority, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		FinanceRepayPriority aFinanceRepayPriority = (FinanceRepayPriority) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinanceRepayPriorityService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getFinanceRepayPriorityService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinanceRepayPriorityService().doApprove(auditHeader);

						if(aFinanceRepayPriority.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinanceRepayPriorityService().doReject(auditHeader);
						if(aFinanceRepayPriority.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceRepayPriorityDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_FinanceRepayPriorityDialog, auditHeader);
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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	public void onClick$btnSearchFinType(Event event){

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceRepayPriorityDialog,"FinanceType");
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		}else{
			FinanceType details= (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.lovDescFinTypeName.setValue(details.getFinType()+"-"+details.getFinTypeDesc());
			}
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

	public FinanceRepayPriority getFinanceRepayPriority() {
		return this.financeRepayPriority;
	}

	public void setFinanceRepayPriority(FinanceRepayPriority financeRepayPriority) {
		this.financeRepayPriority = financeRepayPriority;
	}

	public void setFinanceRepayPriorityService(FinanceRepayPriorityService financeRepayPriorityService) {
		this.financeRepayPriorityService = financeRepayPriorityService;
	}

	public FinanceRepayPriorityService getFinanceRepayPriorityService() {
		return this.financeRepayPriorityService;
	}

	public void setFinanceRepayPriorityListCtrl(FinanceRepayPriorityListCtrl financeRepayPriorityListCtrl) {
		this.financeRepayPriorityListCtrl = financeRepayPriorityListCtrl;
	}

	public FinanceRepayPriorityListCtrl getFinanceRepayPriorityListCtrl() {
		return this.financeRepayPriorityListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}


	private AuditHeader getAuditHeader(FinanceRepayPriority aFinanceRepayPriority, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceRepayPriority.getBefImage(), aFinanceRepayPriority);   
		return new AuditHeader(aFinanceRepayPriority.getFinType(),null,null,null,auditDetail,aFinanceRepayPriority.getUserDetails(),getOverideMap());
	}

	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinanceRepayPriorityDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}


	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	private void doSetLOVValidation() {
		this.lovDescFinTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceRepayPriorityDialog_FinType.value"), null, true));
	}
	private void doRemoveLOVValidation() {
		this.lovDescFinTypeName.setConstraint("");
	}

	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("FinanceRepayPriority");
		notes.setReference(getFinanceRepayPriority().getFinType());
		notes.setVersion(getFinanceRepayPriority().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finPriority.setErrorMessage("");
		logger.debug("Leaving");
	}


	private void refreshList(){
		final JdbcSearchObject<FinanceRepayPriority> soFinanceRepayPriority = getFinanceRepayPriorityListCtrl().getSearchObj();
		getFinanceRepayPriorityListCtrl().pagingFinanceRepayPriorityList.setActivePage(0);
		getFinanceRepayPriorityListCtrl().getPagedListWrapper().setSearchObject(soFinanceRepayPriority);
		if(getFinanceRepayPriorityListCtrl().listBoxFinanceRepayPriority!=null){
			getFinanceRepayPriorityListCtrl().listBoxFinanceRepayPriority.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

}
