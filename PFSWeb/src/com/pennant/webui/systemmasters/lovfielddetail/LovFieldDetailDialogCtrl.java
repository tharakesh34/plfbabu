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
 * FileName    		:  LovFieldDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2011    														*
 *                                                                  						*
 * Modified Date    :  19-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *19-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.lovfielddetail;

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

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
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
 * /WEB-INF/pages/SystemMasters/LovFieldDetail/lovFieldDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class LovFieldDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3760682176867299742L;
	private final static Logger logger = Logger.getLogger(LovFieldDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window            window_LovFieldDetailDialog;// autoWired
	protected ExtendedCombobox           fieldCode;                	// autoWired
	protected Textbox           fieldCodeValue;             // autoWired
	protected Textbox           valueDesc;            		// autoWired
	protected Checkbox          isActive;                   // autoWired
	protected Label             recordStatus;               // autoWired
	protected Radiogroup        userAction;                 // autoWired
	protected Groupbox          groupboxWf;                 // autoWired
	protected Button            btnNew;                     // autoWired
	protected Button            btnEdit;                    // autoWired
	protected Button            btnDelete;                  // autoWired
	protected Button            btnSave;                    // autoWired
	protected Button            btnCancel;                  // autoWired
	protected Button            btnClose;                   // autoWired
	protected Button            btnHelp;                    // autoWired
	protected Button            btnNotes;                   // autoWired
	
	// not auto wired variables
	private LovFieldDetail      lovFieldDetail;                     // over handed per parameters
	private transient LovFieldDetailListCtrl lovFieldDetailListCtrl;// over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_fieldCode;
	private transient String  		oldVar_fieldCodeValue;
	private transient String  		oldVar_valueDesc;
	private transient boolean  		oldVar_isActive;
	private transient String        oldVar_recordStatus;
	
	private transient boolean       validationOn;
	private boolean                 notes_Entered=false;
	
	// Button controller for the CRUD buttons
	private transient final String      btnCtroller_ClassPrefix = "button_LovFieldDetailDialog_";
	private transient ButtonStatusCtrl  btnCtrl;
	private transient String 		    oldVar_lovDescFieldCodeName;
	
	// ServiceDAOs / Domain Classes
	private transient LovFieldDetailService lovFieldDetailService;
	private transient PagedListService      pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public LovFieldDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected LovFieldDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LovFieldDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix,
				true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, 
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("lovFieldDetail")) {
			this.lovFieldDetail = (LovFieldDetail) args.get("lovFieldDetail");
			LovFieldDetail befImage =new LovFieldDetail();
			BeanUtils.copyProperties(this.lovFieldDetail, befImage);
			this.lovFieldDetail.setBefImage(befImage);
			setLovFieldDetail(this.lovFieldDetail);
		} else {
			setLovFieldDetail(null);
		}

		doLoadWorkFlow(this.lovFieldDetail.isWorkflow(),this.lovFieldDetail.getWorkflowId(),
				this.lovFieldDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "LovFieldDetailDialog");
		}


		// READ OVERHANDED parameters !
		// we get the lovFieldDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete lovFieldDetail here.
		if (args.containsKey("lovFieldDetailListCtrl")) {
			setLovFieldDetailListCtrl((LovFieldDetailListCtrl) args.get("lovFieldDetailListCtrl"));
		} else {
			setLovFieldDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getLovFieldDetail());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.fieldCode.setMaxlength(10);
		this.fieldCodeValue.setMaxlength(50);
		this.valueDesc.setMaxlength(50);

		this.fieldCode.setMandatoryStyle(true);
		this.fieldCode.setModuleName("LovFieldCode");
		this.fieldCode.setValueColumn("FieldCode");
		this.fieldCode.setDescColumn("FieldCodeDesc");
		this.fieldCode.setValidateColumns(new String[]{"FieldCode"});
		
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

		getUserWorkspace().alocateAuthorities("LovFieldDetailDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LovFieldDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LovFieldDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LovFieldDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LovFieldDetailDialog_btnSave"));
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
	public void onClose$window_LovFieldDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_LovFieldDetailDialog);
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
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * when "btnSearchFieldCode" is clicked 
	 * @param event
	 */
	public void onFulfill$fieldCode(Event event){
		logger.debug("Entering" + event.toString());
		Object dataObject = fieldCode.getObject();
		if (dataObject instanceof String){
			this.fieldCode.setValue(dataObject.toString());
			this.fieldCode.setDescription("");
		}else{
			LovFieldCode details= (LovFieldCode) dataObject;
			if (details != null) {
				this.fieldCode.setValue(details.getFieldCode());
				this.fieldCode.setDescription(details.getFieldCodeDesc());
			}
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
			closeDialog(this.window_LovFieldDetailDialog, "LovFieldDetail");	
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
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aLovFieldDetail
	 *            LovFieldDetail
	 */
	public void doWriteBeanToComponents(LovFieldDetail aLovFieldDetail) {
		logger.debug("Entering") ;
		this.fieldCode.setValue(aLovFieldDetail.getFieldCode());
		this.fieldCodeValue.setValue(aLovFieldDetail.getFieldCodeValue());
		this.valueDesc.setValue(aLovFieldDetail.getValueDesc());
		this.isActive.setChecked(aLovFieldDetail.isIsActive());

		if (aLovFieldDetail.isNewRecord()){
			this.fieldCode.setDescription("");
		}else{
			this.fieldCode.setDescription(aLovFieldDetail.getLovDescFieldCodeName());
		}
		this.recordStatus.setValue(aLovFieldDetail.getRecordStatus());
		
		if(aLovFieldDetail.isNew() || (aLovFieldDetail.getRecordType() != null ? aLovFieldDetail.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.isActive.setChecked(true);
			this.isActive.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLovFieldDetail
	 */
	public void doWriteComponentsToBean(LovFieldDetail aLovFieldDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aLovFieldDetail.setLovDescFieldCodeName(this.fieldCode.getDescription());
			aLovFieldDetail.setFieldCode(this.fieldCode.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aLovFieldDetail.setFieldCodeValue(this.fieldCodeValue.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aLovFieldDetail.setValueDesc(this.valueDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aLovFieldDetail.setIsActive(this.isActive.isChecked());
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
		aLovFieldDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aLovFieldDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(LovFieldDetail aLovFieldDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aLovFieldDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aLovFieldDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aLovFieldDetail = getLovFieldDetailService().getNewLovFieldDetail();
			setLovFieldDetail(aLovFieldDetail);
		} else {
			setLovFieldDetail(aLovFieldDetail);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aLovFieldDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fieldCodeValue.focus();
		} else {
			this.fieldCodeValue.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aLovFieldDetail.getRecordType()).equals("")) {
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
			doWriteBeanToComponents(aLovFieldDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_LovFieldDetailDialog);
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
		this.oldVar_fieldCode = this.fieldCode.getValue();
		this.oldVar_lovDescFieldCodeName = this.fieldCode.getDescription();
		this.oldVar_fieldCodeValue = this.fieldCodeValue.getValue();
		this.oldVar_valueDesc = this.valueDesc.getValue();
		this.oldVar_isActive = this.isActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.fieldCode.setValue(this.oldVar_fieldCode);
		this.fieldCode.setDescription(this.oldVar_lovDescFieldCodeName);
		this.fieldCodeValue.setValue(this.oldVar_fieldCodeValue);
		this.valueDesc.setValue(this.oldVar_valueDesc);
		this.isActive.setChecked(this.oldVar_isActive);
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
		
		if (this.oldVar_fieldCode != this.fieldCode.getValue()) {
			return true;
		}
		if (this.oldVar_fieldCodeValue != this.fieldCodeValue.getValue()) {
			return true;
		}
		if (this.oldVar_valueDesc != this.valueDesc.getValue()) {
			return true;
		}
		if (this.oldVar_isActive != this.isActive.isChecked()) {
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

		if (!this.fieldCodeValue.isReadonly()) {
			this.fieldCodeValue.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldDetailDialog_FieldCodeValue.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.valueDesc.isReadonly()) {
			this.valueDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldDetailDialog_ValueDesc.value"), 
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
		this.fieldCodeValue.setConstraint("");
		this.valueDesc.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.fieldCode.setErrorMessage("");
		this.fieldCodeValue.setErrorMessage("");
		logger.debug("Leaving");
	}
	/**
	 * This method sets validation for LovFileds
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.fieldCode.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldDetailDialog_FieldCode.value"), null, true));
		logger.debug("Leaving");
	}
	/**
	 * This method removes validation for LovFileds
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.fieldCode.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * 	 Method for refreshing the list after successful updation
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<LovFieldDetail> soLovFieldDetail = getLovFieldDetailListCtrl().
											getSearchObj();
		getLovFieldDetailListCtrl().pagingLovFieldDetailList.setActivePage(0);
		getLovFieldDetailListCtrl().getPagedListWrapper().setSearchObject(soLovFieldDetail);
		if(getLovFieldDetailListCtrl().listBoxLovFieldDetail!=null){
			getLovFieldDetailListCtrl().listBoxLovFieldDetail.getListModel();
		}
		logger.debug("Leaving");
	} 
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a LovFieldDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		
		final LovFieldDetail aLovFieldDetail = new LovFieldDetail();
		BeanUtils.copyProperties(getLovFieldDetail(), aLovFieldDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + 
										"\n\n --> " + aLovFieldDetail.getFieldCodeId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aLovFieldDetail.getRecordType()).equals("")){
				aLovFieldDetail.setVersion(aLovFieldDetail.getVersion()+1);
				aLovFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aLovFieldDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aLovFieldDetail,tranType)){
					refreshList();
					closeDialog(this.window_LovFieldDetailDialog, "LovFieldDetail"); 
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new LovFieldDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		final LovFieldDetail aLovFieldDetail = getLovFieldDetailService().getNewLovFieldDetail();
		setLovFieldDetail(aLovFieldDetail);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.fieldCodeValue.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getLovFieldDetail().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}

		this.fieldCode.setReadonly(isReadOnly("LovFieldDetailDialog_fieldCode"));
		this.fieldCodeValue.setReadonly(isReadOnly("LovFieldDetailDialog_fieldCodeValue"));
		this.valueDesc.setReadonly(isReadOnly("LovFieldDetailDialog_valueDesc"));
		this.isActive.setDisabled(isReadOnly("LovFieldDetailDialog_isActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.lovFieldDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
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
		this.fieldCode.setReadonly(true);
		this.fieldCodeValue.setReadonly(true);
		this.valueDesc.setReadonly(true);
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
		this.fieldCode.setDescription("");
		this.fieldCodeValue.setValue("");
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
		
		final LovFieldDetail aLovFieldDetail = new LovFieldDetail();
		BeanUtils.copyProperties(getLovFieldDetail(), aLovFieldDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the LovFieldDetail object with the components data
		doWriteComponentsToBean(aLovFieldDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aLovFieldDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aLovFieldDetail.getRecordType()).equals("")){
				aLovFieldDetail.setVersion(aLovFieldDetail.getVersion()+1);
				if(isNew){
					aLovFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aLovFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLovFieldDetail.setNewRecord(true);
				}
			}
		}else{
			aLovFieldDetail.setVersion(aLovFieldDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aLovFieldDetail,tranType)){
				doWriteBeanToComponents(aLovFieldDetail);
				refreshList();
				closeDialog(this.window_LovFieldDetailDialog, "LovFieldDetail");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Set the workFlow Details List to Object
	 * @param aLovFieldDetail
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(LovFieldDetail aLovFieldDetail,String tranType){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aLovFieldDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aLovFieldDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLovFieldDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aLovFieldDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aLovFieldDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aLovFieldDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,
						aLovFieldDetail))) {
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
				nextRoleCode= getWorkFlow().firstTask.owner;
				
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

			aLovFieldDetail.setTaskId(taskId);
			aLovFieldDetail.setNextTaskId(nextTaskId);
			aLovFieldDetail.setRoleCode(getRole());
			aLovFieldDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aLovFieldDetail, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aLovFieldDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aLovFieldDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aLovFieldDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Get the result after processing DataBase Operations
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		LovFieldDetail aLovFieldDetail = (LovFieldDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getLovFieldDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getLovFieldDetailService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getLovFieldDetailService().doApprove(auditHeader);

						if(aLovFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getLovFieldDetailService().doReject(auditHeader);
						if(aLovFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(this.window_LovFieldDetailDialog,
								auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_LovFieldDetailDialog, 
						auditHeader);
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
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 * @param aLovFieldDetail 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(LovFieldDetail aLovFieldDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aLovFieldDetail.getBefImage(), aLovFieldDetail);   
		return new AuditHeader(String.valueOf(aLovFieldDetail.getFieldCodeId()),
				null,null,null,auditDetail,aLovFieldDetail.getUserDetails(),getOverideMap());
	}

	/**
	 * when "btnNotes" is clicked 
	 * @param event
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
	
	/**
	 * Display Message in Error Box
	 * @param e
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,
					e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_LovFieldDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}
	
	/**
	 *  Check notes Entered or not
	 * @param notes
	 */
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
	
	/**
	 * Get the notes entered for rejected reason
	 * @return
	 */
	private Notes getNotes(){
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("LovFieldDetail");
		notes.setReference(String.valueOf(getLovFieldDetail().getFieldCodeId()));
		notes.setVersion(getLovFieldDetail().getVersion());
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

	public LovFieldDetail getLovFieldDetail() {
		return this.lovFieldDetail;
	}
	public void setLovFieldDetail(LovFieldDetail lovFieldDetail) {
		this.lovFieldDetail = lovFieldDetail;
	}

	public void setLovFieldDetailService(LovFieldDetailService lovFieldDetailService) {
		this.lovFieldDetailService = lovFieldDetailService;
	}
	public LovFieldDetailService getLovFieldDetailService() {
		return this.lovFieldDetailService;
	}

	public void setLovFieldDetailListCtrl(LovFieldDetailListCtrl lovFieldDetailListCtrl) {
		this.lovFieldDetailListCtrl = lovFieldDetailListCtrl;
	}
	public LovFieldDetailListCtrl getLovFieldDetailListCtrl() {
		return this.lovFieldDetailListCtrl;
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

}
