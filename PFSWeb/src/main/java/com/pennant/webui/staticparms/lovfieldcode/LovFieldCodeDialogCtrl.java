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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.service.staticparms.LovFieldCodeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/LovFieldCode/lovFieldCodeDialog.zul file.
 */
/**
 * @author S037
 *
 */
public class LovFieldCodeDialogCtrl extends GFCBaseCtrl<LovFieldCode> {
	private static final long serialVersionUID = 4551720052100965368L;
	private static final Logger logger = Logger.getLogger(LovFieldCodeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window        window_LovFieldCodeDialog; 	// autoWired
	protected Uppercasebox  fieldCode; 					// autoWired
	protected Textbox       fieldCodeDesc; 				// autoWired
	protected Combobox      fieldCodeType; 				// autoWired
	protected Checkbox      isActive; 					// autoWired
	protected Checkbox      fieldEdit;					// autoWired

	

	// not auto wired variables
	private LovFieldCode lovFieldCode;                            // overHanded per parameter
	private LovFieldCode prvLovFieldCode;                        // overHanded per parameter
	private transient LovFieldCodeListCtrl lovFieldCodeListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient LovFieldCodeService lovFieldCodeService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();
	private List<ValueLabel> listFieldCodeType=PennantStaticListUtil.getLovFieldType(); // autoWired

	/**
	 * default constructor.<br>
	 */
	public LovFieldCodeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LovFieldCodeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected LovFieldCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LovFieldCodeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_LovFieldCodeDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("lovFieldCode")) {
				this.lovFieldCode = (LovFieldCode) arguments.get("lovFieldCode");
				LovFieldCode befImage = new LovFieldCode();
				BeanUtils.copyProperties(this.lovFieldCode, befImage);
				this.lovFieldCode.setBefImage(befImage);
				setLovFieldCode(this.lovFieldCode);
			} else {
				setLovFieldCode(null);
			}

			doLoadWorkFlow(this.lovFieldCode.isWorkflow(),
					this.lovFieldCode.getWorkflowId(),
					this.lovFieldCode.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"LovFieldCodeDialog");
			}

			setListFieldCodeType();

			// READ OVERHANDED parameters !
			// we get the lovFieldCodeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete lovFieldCode here.
			if (arguments.containsKey("lovFieldCodeListCtrl")) {
				setLovFieldCodeListCtrl((LovFieldCodeListCtrl) arguments
						.get("lovFieldCodeListCtrl"));
			} else {
				setLovFieldCodeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getLovFieldCode());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LovFieldCodeDialog.onClose();
		}
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

		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LovFieldCodeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LovFieldCodeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LovFieldCodeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LovFieldCodeDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
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
		MessageUtil.showHelpWindow(event, window_LovFieldCodeDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.lovFieldCode.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
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
		
		if(aLovFieldCode.isNew() || StringUtils.equals(aLovFieldCode.getRecordType(),PennantConstants.RECORD_TYPE_NEW)){
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
	 * @throws Exception
	 */
	public void doShowDialog(LovFieldCode aLovFieldCode) throws Exception {
		logger.debug("Entering");

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

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_LovFieldCodeDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.fieldCode.isReadonly()){
			this.fieldCode.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldCodeDialog_FieldCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
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
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.fieldCode.setErrorMessage("");
		this.fieldCodeDesc.setErrorMessage("");
		this.fieldCodeType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

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
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_LovFieldCodeDialog_FieldCode.value")+" : "+aLovFieldCode.getFieldCode();
		
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isEmpty(aLovFieldCode.getRecordType())){
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
					closeDialog(); 
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
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
			//btnCancel.setVisible(true);
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
		this.fieldEdit.setDisabled(true);

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

		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isEmpty(aLovFieldCode.getRecordType())){
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
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
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

		aLovFieldCode.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLovFieldCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLovFieldCode.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aLovFieldCode.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aLovFieldCode.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aLovFieldCode);
				}

				if (isNotesMandatory(taskId, aLovFieldCode)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aLovFieldCode.setTaskId(taskId);
			aLovFieldCode.setNextTaskId(nextTaskId);
			aLovFieldCode.setRoleCode(getRole());
			aLovFieldCode.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aLovFieldCode, tranType);
			String operationRefs = getServiceOperations(taskId, aLovFieldCode);

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

				if (StringUtils.isBlank(method)){
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
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
						deleteNotes(getNotes(this.lovFieldCode),true);
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
			logger.error("Exception: ", e);
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

	// WorkFlow Components

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
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_LovFieldCodeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.lovFieldCode);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getLovFieldCodeListCtrl().search();
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.lovFieldCode.getFieldCode());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public LovFieldCode getPrvLovFieldCode() {
		return prvLovFieldCode;
	}
}
