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
 * FileName    		:  CheckListDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.checklist;

import java.io.Serializable;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.LongValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/CheckListDetail/checkListDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CheckListDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 2164774289694537365L;
	private final static Logger logger = Logger.getLogger(CheckListDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CheckListDetailDialog; 	// autoWired
	protected Longbox 	ansSeqNo; 						// autoWired
	protected Textbox 	ansDesc; 						// autoWired
	protected Textbox 	ansCond; 						// autoWired
	protected Checkbox 	remarkAllow; 					// autoWired
	protected Checkbox  docRequired;                    // autoWired
	protected Combobox  docType;                    	// autoWired
	protected Row		row_DocType;					// autoWired
	protected Checkbox 	remarkMand; 					// autoWired
	protected Label    recordStatus; 				    // autoWired
	
	protected Button   btnNew; 		                    // autoWired
	protected Button   btnEdit; 		                // autoWired
	protected Button   btnDelete; 	                    // autoWired
	protected Button   btnSave; 		                // autoWired
	protected Button   btnCancel; 	                    // autoWired
	protected Button   btnClose; 		                // autoWired
	protected Button   btnHelp; 		                // autoWired
	protected Button   btnNotes; 		                // autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired variables
	private CheckListDetail checkListDetail; // overHanded per parameter
	private CheckListDetail prvCheckListDetail; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient long  		oldVar_ansSeqNo;
	private transient String  		oldVar_ansDesc;
	private transient String  		oldVar_ansCond;
	private transient boolean  		oldVar_remarkAllow;
	private transient boolean  		oldVar_docRequired;
	private transient String  		oldVar_docType;
	private transient boolean  		oldVar_remarkMand;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CheckListDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private CheckListDialogCtrl checkListDialogCtrl = null;
	private CheckList checkList;
	private boolean isNewRecord=false;
	private List<CheckListDetail> chkListDetailList;

	/**
	 * default constructor.<br>
	 */
	public CheckListDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CheckListDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CheckListDetailDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("checkListDetail")) {
			this.checkListDetail = (CheckListDetail) args.get("checkListDetail");
			CheckListDetail befImage =new CheckListDetail();
			BeanUtils.copyProperties(this.checkListDetail, befImage);
			this.checkListDetail.setBefImage(befImage);

			setCheckListDetail(this.checkListDetail);
		} else {
			setCheckListDetail(null);
		}

		// READ OVERHANDED parameters !
		if (args.containsKey("checkList")) {
			this.checkList = (CheckList) args.get("checkList");
			setCheckList(this.checkList);
		} 

		if (args.containsKey("checkListDialogCtrl")) {
			this.checkListDialogCtrl = (CheckListDialogCtrl) args.get("checkListDialogCtrl");
			setCheckListDialogCtrl(this.checkListDialogCtrl);

			this.checkListDetail.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "CheckListDetailDialog");
			}
		} 
		doLoadWorkFlow(this.checkListDetail.isWorkflow(),this.checkListDetail.getWorkflowId(),this.checkListDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CheckListDetailDialog");
		}


		// READ OVERHANDED parameters !
		// we get the checkListDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete checkListDetail here.

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCheckListDetail());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.ansDesc.setMaxlength(100);
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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

		getUserWorkspace().alocateAuthorities("CheckListDetailDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CheckListDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CheckListDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CheckListDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CheckListDetailDialog_btnSave"));
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
	public void onClose$window_CheckListDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CheckListDetailDialog);
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
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "checks" Remarks checkBox. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$remarkAllow(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		if(!this.remarkAllow.isChecked()){
			this.remarkMand.setChecked(false);
			this.remarkMand.setDisabled(true);
		}else{
			this.remarkMand.setDisabled(false);
		}	
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when the "checks" Doc Required checkBox. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$docRequired(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		onCheckDocRequire();
		logger.debug("Leaving " + event.toString());
	}
	
	private void onCheckDocRequire(){
		if(this.docRequired.isChecked()){
			this.row_DocType.setVisible(true);
		}else{
			this.row_DocType.setVisible(false);
			this.docType.setSelectedIndex(0);
		}	
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
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| 
					MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			this.window_CheckListDetailDialog.onClose();	
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
	 * @param aCheckListDetail
	 *            CheckListDetail
	 */
	public void doWriteBeanToComponents(CheckListDetail aCheckListDetail) {
		logger.debug("Entering") ;
		this.ansSeqNo.setValue(aCheckListDetail.getAnsSeqNo());
		this.ansDesc.setValue(aCheckListDetail.getAnsDesc());
		this.ansCond.setValue(aCheckListDetail.getAnsCond());
		this.remarkAllow.setChecked(aCheckListDetail.isRemarksAllow());
		this.docRequired.setChecked(aCheckListDetail.isDocRequired());
		fillComboBox(this.docType, aCheckListDetail.getDocType(), PennantAppUtil.getDocumentTypes(), "");
		onCheckDocRequire();
		this.remarkMand.setChecked(aCheckListDetail.isRemarksMand());
		this.recordStatus.setValue(aCheckListDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCheckListDetail
	 */
	public void doWriteComponentsToBean(CheckListDetail aCheckListDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCheckListDetail.setAnsSeqNo(this.ansSeqNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setAnsDesc(this.ansDesc.getValue());
			aCheckListDetail.setLovDescCheckListDesc(this.checkList.getCheckListDesc());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setAnsCond("Condition");
			//aCheckListDetail.setAnsCond(this.ansCond.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setRemarksAllow(this.remarkAllow.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setDocRequired(this.docRequired.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setDocType(this.docType.getSelectedItem().getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckListDetail.setRemarksMand(this.remarkMand.isChecked());
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

		aCheckListDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCheckListDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CheckListDetail aCheckListDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aCheckListDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCheckListDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCheckListDetail = new CheckListDetail();
			this.isNewRecord=true;
			setCheckListDetail(aCheckListDetail);
		} else {
			setCheckListDetail(aCheckListDetail);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCheckListDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ansDesc.focus();
		} else {
			this.ansDesc.focus();
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
			doWriteBeanToComponents(aCheckListDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.window_CheckListDetailDialog.doModal();
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
		this.oldVar_ansSeqNo = this.ansSeqNo.longValue();
		this.oldVar_ansDesc = this.ansDesc.getValue();
		this.oldVar_ansCond = this.ansCond.getValue();
		this.oldVar_remarkAllow = this.remarkAllow.isChecked();
		this.oldVar_docRequired = this.docRequired.isChecked();
		this.oldVar_docType = this.docType.getSelectedItem().getValue().toString();
		this.oldVar_remarkMand = this.remarkMand.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.ansSeqNo.setValue(this.oldVar_ansSeqNo);
		this.ansDesc.setValue(this.oldVar_ansDesc);
		this.ansCond.setValue(this.oldVar_ansCond);
		this.remarkAllow.setChecked(this.oldVar_remarkAllow);
		this.remarkMand.setChecked(this.oldVar_remarkMand);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.docRequired.setChecked(this.oldVar_docRequired);
		fillComboBox(this.docType, this.oldVar_docType, PennantAppUtil.getDocumentTypes(), "");

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

		if (this.oldVar_ansDesc != this.ansDesc.getValue()) {
			return true;
		}
		if (this.oldVar_remarkAllow != this.remarkAllow.isChecked()) {
			return true;
		}
		if (this.oldVar_docRequired != this.docRequired.isChecked()) {
			return true;
		}
		if (this.oldVar_remarkMand != this.remarkMand.isChecked()) {
			return true;
		}
		if (this.oldVar_docRequired != this.docRequired.isChecked()) {
			return true;
		}
		if (this.oldVar_docType != this.docType.getSelectedItem().getValue().toString()) {
			return true;
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
		
		if (!this.ansSeqNo.isReadonly()){
			this.ansSeqNo.setConstraint(new LongValidator(19,Labels.getLabel("label_CheckListDetailDialog_AnsSeqNo.value")));
		}

		if (!this.ansDesc.isReadonly()){
			this.ansDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CheckListDetailDialog_AnsDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));	
		}
		if (!this.ansCond.isReadonly()){
			this.ansCond.setConstraint(new SimpleConstraint(("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[] { Labels.getLabel("label_CheckListDetailDialog_AnsRemarks.value") }))));
		}
		
		if(!this.docType.isDisabled()){
			if(this.docRequired.isChecked()){
				this.docType.setConstraint(new StaticListValidator(PennantAppUtil.getDocumentTypes(), 
						Labels.getLabel("label_CheckListDetailDialog_DocType.value")));
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.ansSeqNo.setConstraint("");
		this.ansDesc.setConstraint("");
		this.ansCond.setConstraint("");
		this.docType.setConstraint("");
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
		this.ansSeqNo.setErrorMessage("");
		this.ansDesc.setErrorMessage("");
		this.ansCond.setErrorMessage("");
		this.docType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CheckListDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CheckListDetail checkListDetail = new CheckListDetail();
		BeanUtils.copyProperties(getCheckListDetail(), checkListDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + checkListDetail.getAnsDesc();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(checkListDetail.getRecordType()).equals("")){
				checkListDetail.setVersion(checkListDetail.getVersion()+1);
				checkListDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			
				if (isWorkFlowEnabled()){
					checkListDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}else if (StringUtils.trimToEmpty(checkListDetail.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				checkListDetail.setVersion(checkListDetail.getVersion() + 1);
				checkListDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newChkListDetailProcess(checkListDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CheckListDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCheckListDialogCtrl().doFillCheckListDetailsList(this.chkListDetailList);

					this.window_CheckListDetailDialog.onClose();
				}

			}catch (DataAccessException e){
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CheckListDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final CheckListDetail aCheckListDetail = new CheckListDetail();
		setCheckListDetail(aCheckListDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCheckListDetail().isNewRecord()){
			this.btnCancel.setVisible(false);
			this.remarkMand.setDisabled(true);
		}else{
			this.btnCancel.setVisible(true);
		}

		this.ansSeqNo.setReadonly(isReadOnly("CheckListDetailDialog_ansSeqNo"));
		this.ansCond.setReadonly(isReadOnly("CheckListDetailDialog_ansCond"));
		this.remarkAllow.setDisabled(isReadOnly("CheckListDetailDialog_remarksAllow"));
		this.docRequired.setDisabled(isReadOnly("CheckListDetailDialog_docRequired"));
		this.docType.setDisabled(isReadOnly("CheckListDetailDialog_docType"));
		this.ansDesc.setReadonly(isReadOnly("CheckListDetailDialog_ansDesc"));
		if(	this.remarkAllow.isChecked() && !isReadOnly("CheckListDetailDialog_remarksMand")
				&& 	!this.remarkAllow.isDisabled()){
			this.remarkMand.setDisabled(false);
		}else{
			this.remarkMand.setDisabled(true);
		}

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.checkListDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			if(getCheckListDetail().isNewRecord()){
				this.btnCtrl.setBtnStatus_New();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setBtnStatus_Edit();
			}
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.ansSeqNo.setReadonly(true);
		this.ansDesc.setReadonly(true);
		this.ansCond.setReadonly(true);
		this.remarkAllow.setDisabled(true);
		this.docRequired.setDisabled(true);
		this.docType.setDisabled(true);
		this.remarkMand.setDisabled(true);

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

		this.ansSeqNo.setText("");
		this.ansDesc.setValue("");
		this.ansCond.setValue("");
		this.remarkAllow.setChecked(false);
		this.docRequired.setChecked(false);
		this.remarkMand.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CheckListDetail aCheckListDetail = new CheckListDetail();
		BeanUtils.copyProperties(getCheckListDetail(), aCheckListDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CheckListDetail object with the components data
		doWriteComponentsToBean(aCheckListDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

   		isNew = aCheckListDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCheckListDetail.getRecordType()).equals("")){
				aCheckListDetail.setVersion(aCheckListDetail.getVersion()+1);
				if(isNew){
					aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCheckListDetail.setNewRecord(true);
				}
			}
		}else{
			/*set the tranType according to RecordType*/
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
				aCheckListDetail.setVersion(1);
				aCheckListDetail.setRecordType(PennantConstants.RCD_ADD);
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}

			if(StringUtils.trimToEmpty(aCheckListDetail.getRecordType()).equals("")){
				tranType =PennantConstants.TRAN_UPD;
				aCheckListDetail.setRecordType(PennantConstants.RCD_UPD);
			}
			if(aCheckListDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNew){
				tranType =PennantConstants.TRAN_ADD;
			} else if(aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
				tranType =PennantConstants.TRAN_UPD;
			} 
		}

		try {
			AuditHeader auditHeader =  newChkListDetailProcess(aCheckListDetail,tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CheckListDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getCheckListDialogCtrl().doFillCheckListDetailsList(this.chkListDetailList);

				this.window_CheckListDetailDialog.onClose();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method added the CheckListdetail object into chkListDetailList
	 *  by setting RecordType according to tranType
	 *  <p>eg: 	if(tranType==PennantConstants.TRAN_DEL){
	 *  	aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
	 *  }</p>
	 * @param  aCheckListDetail (CheckListDetail)
	 * @param  tranType (String)
	 * @return auditHeader (AuditHeader)
	 */
	private AuditHeader newChkListDetailProcess(CheckListDetail aCheckListDetail,String tranType){
		logger.debug("Entering ");
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aCheckListDetail, tranType);
		chkListDetailList= new ArrayList<CheckListDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCheckListDetail.getAnsDesc());
		errParm[0] = PennantJavaUtil.getLabel("label_AnsDesc") + ":"+valueParm[0];

		if(getCheckListDialogCtrl().getChekListDetailsList()!=null 
				&& getCheckListDialogCtrl().getChekListDetailsList().size()>0){
			for (int i = 0; i < getCheckListDialogCtrl().getChekListDetailsList().size(); i++) {
				CheckListDetail checkListDetail = getCheckListDialogCtrl().getChekListDetailsList().get(i);

				if(aCheckListDetail.getAnsDesc().trim().equalsIgnoreCase(checkListDetail.getAnsDesc().trim()) ){ 
					// Both Current and Existing list expense same
					/*if same educational expenses added twice set error detail*/
					if(getCheckListDetail().isNew()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm)
								, getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							chkListDetailList.add(aCheckListDetail);
						}
						else if(aCheckListDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCheckListDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							chkListDetailList.add(aCheckListDetail);
						}else if(aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCheckListDialogCtrl().getChekListDetailsList().size(); j++) {
								CheckListDetail chkDetail =  getCheckListDialogCtrl().getChekListDetailsList().get(j);
								if(aCheckListDetail.getAnsDesc().trim().equalsIgnoreCase(checkListDetail.getAnsDesc().trim())){
									chkListDetailList.add(chkDetail);
								}
							}
						}else if(aCheckListDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							if(this.checkList != null && this.checkList.isWorkflow()){
							aCheckListDetail.setNewRecord(true);
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD ){
							chkListDetailList.add(checkListDetail);
						}
					}
				}else{
					chkListDetailList.add(checkListDetail);
				}
			}
		}
		if(!recordAdded){
			chkListDetailList.add(aCheckListDetail);
		}
		return auditHeader;
	} 


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aCheckListDetail
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CheckListDetail aCheckListDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCheckListDetail.getBefImage(), aCheckListDetail);   
		return new AuditHeader(String.valueOf(aCheckListDetail.getCheckListId())
				,null,null,null,auditDetail,aCheckListDetail.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CheckListDetailDialog, auditHeader);
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
		logger.debug("Entering");
		// logger.debug(event.toString());

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
		logger.debug("Leaving");
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

	// Get the notes entered for rejected reason
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("CheckListDetail");
		/*notes.setReference(getCheckListDetail().getCheckListId());*/
		notes.setVersion(getCheckListDetail().getVersion());
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

	public CheckListDetail getCheckListDetail() {
		return this.checkListDetail;
	}
	public void setCheckListDetail(CheckListDetail checkListDetail) {
		this.checkListDetail = checkListDetail;
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

	public CheckListDetail getPrvCheckListDetail() {
		return prvCheckListDetail;
	}

	public CheckListDialogCtrl getCheckListDialogCtrl() {
		return checkListDialogCtrl;
	}
	public void setCheckListDialogCtrl(CheckListDialogCtrl checkListDialogCtrl) {
		this.checkListDialogCtrl = checkListDialogCtrl;
	}

	public CheckList getCheckList() {
		return checkList;
	}
	public void setCheckList(CheckList checkList) {
		this.checkList = checkList;
	}

	public boolean isNewRecord() {
		return isNewRecord;
	}
	public void setNewRecord(boolean isNewRecord) {
		this.isNewRecord = isNewRecord;
	}

	public void setChkListDetailList(List<CheckListDetail> chkListDetailList) {
		this.chkListDetailList = chkListDetailList;
	}
	public List<CheckListDetail> getChkListDetailList() {
		return chkListDetailList;
	}
}
