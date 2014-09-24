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
 * FileName    		:  LovFieldCodeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  04-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.lovfieldcode;

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
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.staticparms.LovFieldCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/LovFieldCode/lovFieldCodeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
/**
 * @author S037
 *
 */
public class LovFieldCodeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4551720052100965368L;
	private final static Logger logger = Logger.getLogger(LovFieldCodeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window        window_LovFieldCodeDialog; 	// autoWired
	protected Textbox       fieldCode; 					// autoWired
	protected Textbox       fieldCodeDesc; 				// autoWired
	protected Combobox      fieldCodeType; 				// autoWired
	protected Checkbox      isActive; 					// autoWired
	protected Checkbox      fieldEdit;					// autoWired
	protected Label         recordStatus;				// autoWired
	protected Radiogroup    userAction;                 // autoWired
	protected Groupbox      groupboxWf;                 // autoWired
	protected Button        btnNew;                     // autoWired
	protected Button        btnEdit;                    // autoWired
	protected Button        btnDelete;                  // autoWired
	protected Button        btnSave;                    // autoWired
	protected Button        btnCancel;                  // autoWired
	protected Button        btnClose;                   // autoWired
	protected Button        btnHelp;                    // autoWired
	protected Button        btnNotes;                   // autoWired

	// not auto wired variables
	private LovFieldCode lovFieldCode;                            // overHanded per parameter
	private LovFieldCode prvLovFieldCode;                        // overHanded per parameter
	private transient LovFieldCodeListCtrl lovFieldCodeListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initial.
	private transient String  		oldVar_fieldCode;
	private transient String  		oldVar_fieldCodeDesc;
	private transient String  		oldVar_fieldCodeType;
	private transient boolean  		oldVar_isActive;
	private transient boolean       oldVar_fieldEdit;
	private transient String oldVar_recordStatus;
	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_LovFieldCodeDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	// ServiceDAOs / Domain Classes
	private transient LovFieldCodeService lovFieldCodeService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private List<ValueLabel> listFieldCodeType=PennantStaticListUtil.getLovFieldType(); // autoWired

	/**
	 * default constructor.<br>
	 */
	public LovFieldCodeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected LovFieldCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LovFieldCodeDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("lovFieldCode")) {
			this.lovFieldCode = (LovFieldCode) args.get("lovFieldCode");
			LovFieldCode befImage =new LovFieldCode();
			BeanUtils.copyProperties(this.lovFieldCode, befImage);
			this.lovFieldCode.setBefImage(befImage);
			setLovFieldCode(this.lovFieldCode);
		} else {
			setLovFieldCode(null);
		}

		doLoadWorkFlow(this.lovFieldCode.isWorkflow(),this.lovFieldCode.getWorkflowId(),this.lovFieldCode.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "LovFieldCodeDialog");
		}

		setListFieldCodeType();

		// READ OVERHANDED parameters !
		// we get the lovFieldCodeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete lovFieldCode here.
		if (args.containsKey("lovFieldCodeListCtrl")) {
			setLovFieldCodeListCtrl((LovFieldCodeListCtrl) args.get("lovFieldCodeListCtrl"));
		} else {
			setLovFieldCodeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getLovFieldCode());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.fieldCode.setMaxlength(10);
		this.fieldCodeDesc.setMaxlength(50);

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

		getUserWorkspace().alocateAuthorities("LovFieldCodeDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LovFieldCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LovFieldCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LovFieldCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LovFieldCodeDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}


	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_LovFieldCodeDialog(Event event) throws Exception {
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
		// remember the old variables
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_LovFieldCodeDialog);
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
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving");
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
			closeDialog(this.window_LovFieldCodeDialog, "LovFieldCode");	
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
	 * @param aLovFieldCode
	 *            LovFieldCode
	 */
	public void doWriteBeanToComponents(LovFieldCode aLovFieldCode) {
		logger.debug("Entering") ;
		this.fieldCode.setValue(aLovFieldCode.getFieldCode());
		this.fieldCodeDesc.setValue(aLovFieldCode.getFieldCodeDesc());
		this.fieldCodeType.setValue(PennantAppUtil.getlabelDesc(
				aLovFieldCode.getFieldCodeType()==null?"":aLovFieldCode.getFieldCodeType().trim(),listFieldCodeType));
		this.isActive.setChecked(aLovFieldCode.isIsActive());
		this.fieldEdit.setChecked(aLovFieldCode.isFieldEdit());
		this.recordStatus.setValue(aLovFieldCode.getRecordStatus());
		
		if(aLovFieldCode.isNew() || aLovFieldCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.isActive.setChecked(true);
			this.isActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLovFieldCode
	 */
	public void doWriteComponentsToBean(LovFieldCode aLovFieldCode) {
		logger.debug("Entering") ;

		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aLovFieldCode.setFieldCode(this.fieldCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aLovFieldCode.setFieldCodeDesc(this.fieldCodeDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aLovFieldCode.setFieldCodeType(StringUtils.trim(this.fieldCodeType.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aLovFieldCode.setIsActive(this.isActive.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aLovFieldCode.setFieldEdit(this.fieldEdit.isChecked());
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
		aLovFieldCode.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aLovFieldCode
	 * @throws InterruptedException
	 */
	public void doShowDialog(LovFieldCode aLovFieldCode) throws InterruptedException {
		logger.debug("Entering") ;

		// if aLovFieldCode == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aLovFieldCode == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aLovFieldCode = getLovFieldCodeService().getNewLovFieldCode();
			setLovFieldCode(aLovFieldCode);
		} else {
			setLovFieldCode(aLovFieldCode);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aLovFieldCode.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fieldCode.focus();
		} else {
			this.fieldCodeDesc.focus();
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
			doWriteBeanToComponents(aLovFieldCode);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_LovFieldCodeDialog);
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_fieldCode = this.fieldCode.getValue();
		this.oldVar_fieldCodeDesc = this.fieldCodeDesc.getValue();
		this.oldVar_fieldCodeType = this.fieldCodeType.getValue();
		this.oldVar_isActive = this.isActive.isChecked();
		this.oldVar_fieldEdit = this.fieldEdit.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.fieldCode.setValue(this.oldVar_fieldCode);
		this.fieldCodeDesc.setValue(this.oldVar_fieldCodeDesc);
		this.fieldCodeType.setValue(this.oldVar_fieldCodeType);
		this.isActive.setChecked(this.oldVar_isActive);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.fieldEdit.setChecked(this.oldVar_fieldEdit);
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

		if(this.oldVar_fieldCode != this.fieldCode.getValue()){
			return true;
		}
		if(this.oldVar_fieldCodeDesc != this.fieldCodeDesc.getValue()){
			return true;
		}
		if(this.oldVar_fieldCodeType != this.fieldCodeType.getValue()){
			return true;
		}
		if(this.oldVar_fieldEdit != this.fieldEdit.isChecked()){
			return true;
		}
		if(this.oldVar_isActive != this.isActive.isChecked()){
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

		if (!this.fieldCode.isReadonly()){
			this.fieldCode.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldCodeDialog_FieldCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.fieldCodeDesc.isReadonly()) {
			this.fieldCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldCodeDialog_FieldCodeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.fieldCodeType.isDisabled()){
			this.fieldCodeType.setConstraint(new StaticListValidator(listFieldCodeType,Labels.getLabel(
			"label_LovFieldCodeDialog_FieldCodeType.value")));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.fieldCode.setConstraint("");
		this.fieldCodeDesc.setConstraint("");
		this.fieldCodeType.setConstraint("");
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
		this.fieldCode.setErrorMessage("");
		this.fieldCodeDesc.setErrorMessage("");
		this.fieldCodeType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a LovFieldCode object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	

		final LovFieldCode aLovFieldCode = new LovFieldCode();
		BeanUtils.copyProperties(getLovFieldCode(), aLovFieldCode);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aLovFieldCode.getFieldCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aLovFieldCode.getRecordType()).equals("")){
				aLovFieldCode.setVersion(aLovFieldCode.getVersion()+1);
				aLovFieldCode.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aLovFieldCode.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(doProcess(aLovFieldCode,tranType)){
					refreshList();
					closeDialog(this.window_LovFieldCodeDialog, "LovFieldCode"); 
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new LovFieldCode object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		final LovFieldCode aLovFieldCode = getLovFieldCodeService().getNewLovFieldCode();
		aLovFieldCode.setNewRecord(true);
		setLovFieldCode(aLovFieldCode);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.fieldCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getLovFieldCode().isNewRecord()){
			this.fieldCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.fieldCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.fieldCodeDesc.setReadonly(isReadOnly("LovFieldCodeDialog_fieldCodeDesc"));
		this.fieldCodeType.setDisabled(isReadOnly("LovFieldCodeDialog_fieldCodeType"));
		this.fieldEdit.setDisabled(isReadOnly("LovFieldCodeDialog_fieldEdit"));
		this.isActive.setDisabled(isReadOnly("LovFieldCodeDialog_isActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.lovFieldCode.isNewRecord()){
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
		this.fieldCode.setReadonly(true);
		this.fieldCodeDesc.setReadonly(true);
		this.fieldCodeType.setDisabled(true);
		this.isActive.setDisabled(true);

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
		this.fieldCode.setValue("");
		this.fieldCodeDesc.setValue("");
		this.fieldCodeType.setValue("");
		this.isActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final LovFieldCode aLovFieldCode = new LovFieldCode();
		BeanUtils.copyProperties(getLovFieldCode(), aLovFieldCode);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the LovFieldCode object with the components data
		doWriteComponentsToBean(aLovFieldCode);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aLovFieldCode.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aLovFieldCode.getRecordType()).equals("")){
				aLovFieldCode.setVersion(aLovFieldCode.getVersion()+1);
				if(isNew){
					aLovFieldCode.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aLovFieldCode.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLovFieldCode.setNewRecord(true);
				}
			}
		}else{
			aLovFieldCode.setVersion(aLovFieldCode.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aLovFieldCode,tranType)){
				doWriteBeanToComponents(aLovFieldCode);
				refreshList();
				closeDialog(this.window_LovFieldCodeDialog, "LovFieldCode");
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
	 * @param aLovFieldCode
	 *            (LovFieldCode)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(LovFieldCode aLovFieldCode,String tranType){
		logger.debug("Entering");

		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aLovFieldCode.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aLovFieldCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLovFieldCode.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aLovFieldCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aLovFieldCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aLovFieldCode);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aLovFieldCode))) {
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

			aLovFieldCode.setTaskId(taskId);
			aLovFieldCode.setNextTaskId(nextTaskId);
			aLovFieldCode.setRoleCode(getRole());
			aLovFieldCode.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aLovFieldCode, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aLovFieldCode);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aLovFieldCode, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aLovFieldCode, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
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
		boolean deleteNotes=false;

		LovFieldCode aLovFieldCode = (LovFieldCode) auditHeader.getAuditDetail().getModelData();

		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getLovFieldCodeService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getLovFieldCodeService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getLovFieldCodeService().doApprove(auditHeader);

						if(aLovFieldCode.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getLovFieldCodeService().doReject(auditHeader);
						if(aLovFieldCode.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_LovFieldCodeDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_LovFieldCodeDialog, auditHeader);
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

	/**
	 * Set the code for selecting the FieldCodeType
	 */
	private void setListFieldCodeType(){
		for (int i = 0; i < listFieldCodeType.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listFieldCodeType.get(i).getLabel());
			comboitem.setValue(listFieldCodeType.get(i).getValue());
			this.fieldCodeType.appendChild(comboitem);
		} 
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aLovFieldCode
	 *            (LovFieldCode)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(LovFieldCode aLovFieldCode, String tranType){

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aLovFieldCode.getBefImage(), aLovFieldCode);   
		return new AuditHeader(aLovFieldCode.getFieldCode(),null,null,null,
				auditDetail,aLovFieldCode.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_LovFieldCodeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
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
		logger.debug("Entering"+ event.toString());

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
		logger.debug("Leaving"+ event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Method for refreshing the list after successful updation
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<LovFieldCode> soLovFieldCode = getLovFieldCodeListCtrl().getSearchObj();
		getLovFieldCodeListCtrl().pagingLovFieldCodeList.setActivePage(0);
		getLovFieldCodeListCtrl().getPagedListWrapper().setSearchObject(soLovFieldCode);
		if(getLovFieldCodeListCtrl().listBoxLovFieldCode!=null){
			getLovFieldCodeListCtrl().listBoxLovFieldCode.getListModel();
		}
		logger.debug("Leaving");
	} 

	// Get the notes entered for rejected reason
	private Notes getNotes(){
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("LovFieldCode");
		notes.setReference(getLovFieldCode().getFieldCode());
		notes.setVersion(getLovFieldCode().getVersion());
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

	public LovFieldCode getLovFieldCode() {
		return this.lovFieldCode;
	}
	public void setLovFieldCode(LovFieldCode lovFieldCode) {
		this.lovFieldCode = lovFieldCode;
	}

	public void setLovFieldCodeService(LovFieldCodeService lovFieldCodeService) {
		this.lovFieldCodeService = lovFieldCodeService;
	}
	public LovFieldCodeService getLovFieldCodeService() {
		return this.lovFieldCodeService;
	}

	public void setLovFieldCodeListCtrl(LovFieldCodeListCtrl lovFieldCodeListCtrl) {
		this.lovFieldCodeListCtrl = lovFieldCodeListCtrl;
	}
	public LovFieldCodeListCtrl getLovFieldCodeListCtrl() {
		return this.lovFieldCodeListCtrl;
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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public LovFieldCode getPrvLovFieldCode() {
		return prvLovFieldCode;
	}
}
