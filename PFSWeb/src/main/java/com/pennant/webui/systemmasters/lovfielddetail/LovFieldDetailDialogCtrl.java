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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.systemmasters.LovFieldDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/LovFieldDetail/lovFieldDetailDialog.zul file.
 */
public class LovFieldDetailDialogCtrl extends GFCBaseCtrl<LovFieldDetail> {
	private static final long serialVersionUID = -3760682176867299742L;
	private static final Logger logger = Logger.getLogger(LovFieldDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window            window_LovFieldDetailDialog;
	protected ExtendedCombobox           fieldCode;                	
	protected Textbox           fieldCodeValue;             
	protected Textbox           valueDesc;            		
	protected Checkbox          isActive;                   
	protected Checkbox          systemDefault;               
	
	// not auto wired variables
	private LovFieldDetail      lovFieldDetail;                     // over handed per parameters
	private transient LovFieldDetailListCtrl lovFieldDetailListCtrl;// over handed per parameters

	private transient boolean       validationOn;

	// ServiceDAOs / Domain Classes
	private transient LovFieldDetailService lovFieldDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public LovFieldDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LovFieldDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected LovFieldDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LovFieldDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_LovFieldDetailDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("lovFieldDetail")) {
				this.lovFieldDetail = (LovFieldDetail) arguments.get("lovFieldDetail");
				LovFieldDetail befImage = new LovFieldDetail();
				BeanUtils.copyProperties(this.lovFieldDetail, befImage);
				this.lovFieldDetail.setBefImage(befImage);
				setLovFieldDetail(this.lovFieldDetail);
			} else {
				setLovFieldDetail(null);
			}

			doLoadWorkFlow(this.lovFieldDetail.isWorkflow(),
					this.lovFieldDetail.getWorkflowId(),
					this.lovFieldDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"LovFieldDetailDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the lovFieldDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete lovFieldDetail here.
			if (arguments.containsKey("lovFieldDetailListCtrl")) {
				setLovFieldDetailListCtrl((LovFieldDetailListCtrl) arguments
						.get("lovFieldDetailListCtrl"));
			} else {
				setLovFieldDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getLovFieldDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_LovFieldDetailDialog.onClose();
		}
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LovFieldDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LovFieldDetailDialog_btnEdit"));
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_LovFieldDetailDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LovFieldDetailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_LovFieldDetailDialog);
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
		doWriteBeanToComponents(this.lovFieldDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		this.btnDelete.setVisible(false);
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
		this.systemDefault.setChecked(aLovFieldDetail.isSystemDefault());

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
		try {
			aLovFieldDetail.setSystemDefault(this.systemDefault.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();

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
	 * @throws Exception
	 */
	public void doShowDialog(LovFieldDetail aLovFieldDetail) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aLovFieldDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fieldCode.focus();
		} else {
			this.fieldCodeValue.focus();
			if (isWorkFlowEnabled()){
				if (StringUtils.isNotBlank(aLovFieldDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				btnDelete.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aLovFieldDetail);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_LovFieldDetailDialog.onClose();
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

		if (!this.fieldCodeValue.isReadonly()) {
			this.fieldCodeValue.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldDetailDialog_FieldCodeValue.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.valueDesc.isReadonly()) {
			this.valueDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldDetailDialog_ValueDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.fieldCode.isReadonly()) {
			this.fieldCode.setConstraint(new PTStringValidator(Labels.getLabel("label_LovFieldDetailDialog_FieldCode.value"), null, true,true));
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
		this.fieldCode.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.fieldCode.setErrorMessage("");
		this.fieldCodeValue.setErrorMessage("");
		this.valueDesc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getLovFieldDetailListCtrl().search();
	} 
	
	// CRUD operations

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") +"\n\n --> " + 
				Labels.getLabel("label_LovFieldDetailDialog_FieldCode.value")+" : "+aLovFieldDetail.getFieldCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aLovFieldDetail.getRecordType())){
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
					closeDialog(); 
				}
			}catch (Exception e) {
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

		if (getLovFieldDetail().isNewRecord()){
			this.btnCancel.setVisible(false);
			this.btnDelete.setVisible(false);
			this.fieldCode.setReadonly(isReadOnly("LovFieldDetailDialog_fieldCode"));
			this.fieldCodeValue.setReadonly(isReadOnly("LovFieldDetailDialog_fieldCodeValue"));
		}else{
			this.btnCancel.setVisible(true);
			this.fieldCode.setReadonly(true);
			this.fieldCodeValue.setReadonly(true);
		}

		this.valueDesc.setReadonly(isReadOnly("LovFieldDetailDialog_valueDesc"));
		this.isActive.setDisabled(isReadOnly("LovFieldDetailDialog_isActive"));
		this.systemDefault.setDisabled(isReadOnly("LovFieldDetailDialog_systemDefault"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.lovFieldDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
				btnDelete.setVisible(false);
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
		this.systemDefault.setDisabled(true);

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

		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aLovFieldDetail.getRecordType())){
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
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
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

		aLovFieldDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aLovFieldDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLovFieldDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aLovFieldDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aLovFieldDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aLovFieldDetail);
				}

				if (isNotesMandatory(taskId, aLovFieldDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}


			if (!StringUtils.isBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();
				
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

			aLovFieldDetail.setTaskId(taskId);
			aLovFieldDetail.setNextTaskId(nextTaskId);
			aLovFieldDetail.setRoleCode(getRole());
			aLovFieldDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aLovFieldDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aLovFieldDetail);

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

				if (StringUtils.isBlank(method)){
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
						deleteNotes(getNotes(this.lovFieldDetail),true);
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
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	// WorkFlow Components
	
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
		doShowNotes(this.lovFieldDetail);
	}
	
	/**
	 * Display Message in Error Box
	 * @param e
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,
					e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_LovFieldDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.lovFieldDetail.getFieldCodeId());
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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

}
