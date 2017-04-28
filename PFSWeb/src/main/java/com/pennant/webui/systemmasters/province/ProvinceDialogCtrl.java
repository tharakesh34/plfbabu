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
 * FileName    		:  ProvinceDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.systemmasters.province;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Province/provinceDialog.zul file.
 */
public class ProvinceDialogCtrl extends GFCBaseCtrl<Province> {
	private static final long serialVersionUID = 8900134469414443671L;
	private final static Logger logger = Logger.getLogger(ProvinceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProvinceDialog;
	protected ExtendedCombobox cPCountry;
	protected Uppercasebox cPProvince;
	protected Textbox cPProvinceName;
	protected Checkbox systemDefault;
	protected Textbox bankRefNo;
	protected Checkbox cPIsActive; // autoWired

	// not auto wired variables
	private Province  province; // overHanded per parameter
	private transient ProvinceListCtrl provinceListCtrl; // overHanded per parameter

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient ProvinceService  provinceService;
	private Country sysDefaultCountry;

	/**
	 * default constructor.<br>
	 */
	public ProvinceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProvinceDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Province object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProvinceDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProvinceDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("province")) {
				this.province = (Province) arguments.get("province");
				Province befImage = new Province();
				BeanUtils.copyProperties(this.province, befImage);
				this.province.setBefImage(befImage);

				setProvince(this.province);
			} else {
				setProvince(null);
			}

			doLoadWorkFlow(this.province.isWorkflow(),
					this.province.getWorkflowId(),
					this.province.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"ProvinceDialog");
			}

			// READ OVERHANDED parameters !
			// we get the provinceListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete province here.
			if (arguments.containsKey("provinceListCtrl")) {
				setProvinceListCtrl((ProvinceListCtrl) arguments
						.get("provinceListCtrl"));
			} else {
				setProvinceListCtrl(null);
			}
			setCountrySystemDefault();
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getProvince());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_ProvinceDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.cPCountry.setMaxlength(2);
		this.cPProvince.setMaxlength(8);
		this.cPProvinceName.setMaxlength(50);
		this.bankRefNo.setMaxlength(20);

		this.cPCountry.setMandatoryStyle(true);
		this.cPCountry.setModuleName("Country");
		this.cPCountry.setValueColumn("CountryCode");
		this.cPCountry.setDescColumn("CountryDesc");
		this.cPCountry.setValidateColumns(new String[]{"CountryCode"} );
		
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
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
		doCheckSystemDefault();
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
		MessageUtil.showHelpWindow(event, window_ProvinceDialog);
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
		logger.debug("Entering");
		doWriteBeanToComponents(this.province.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProvince
	 *            Province
	 */
	public void doWriteBeanToComponents(Province aProvince) {
		logger.debug("Entering");
		this.cPCountry.setValue(aProvince.getCPCountry());
		this.cPProvince.setValue(aProvince.getCPProvince());
		this.cPProvinceName.setValue(aProvince.getCPProvinceName());
		this.systemDefault.setChecked(aProvince.isSystemDefault());
		this.bankRefNo.setValue(aProvince.getBankRefNo());
		this.cPIsActive.setChecked(aProvince.iscPIsActive());
		
		if (aProvince.isNewRecord()){
			this.cPCountry.setDescription("");
		}else{
			this.cPCountry.setDescription(aProvince.getLovDescCPCountryName());
		}
		if(aProvince.isNew() || (aProvince.getRecordType() != null ? aProvince.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.cPIsActive.setChecked(true);
			this.cPIsActive.setDisabled(true);
		}
		this.recordStatus.setValue(aProvince.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProvince
	 */
	public void doWriteComponentsToBean(Province aProvince) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aProvince.setLovDescCPCountryName(this.cPCountry.getDescription());
			aProvince.setCPCountry(this.cPCountry.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvince.setCPProvince(this.cPProvince.getValue().toUpperCase());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvince.setCPProvinceName(this.cPProvinceName.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aProvince.setSystemDefault(this.systemDefault.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvince.setBankRefNo(this.bankRefNo.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aProvince.setcPIsActive(this.cPIsActive.isChecked());
		} catch (WrongValueException we) {
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

		aProvince.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aProvince
	 * @throws Exception
	 */
	public void doShowDialog(Province aProvince) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aProvince.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cPCountry.focus();
		} else {
			this.cPProvinceName.focus();
			if (isWorkFlowEnabled()){
				if (StringUtils.isNotBlank(aProvince.getRecordType())){
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
			doWriteBeanToComponents(aProvince);
			
			if (aProvince.isNew() || isWorkFlowEnabled()) {
				doCheckSystemDefault();
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_ProvinceDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.cPProvince.isReadonly()){
			this.cPProvince.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceDialog_CPProvince.value"), 
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHA, true));
		}	
		if (!this.cPProvinceName.isReadonly()){
			this.cPProvinceName.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceDialog_CPProvinceName.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}	
		if (!this.cPCountry.isReadonly()) {
			this.cPCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceDialog_CPCountry.value"), null, true,true));
		}
		if (!this.bankRefNo.isReadonly()){
			this.bankRefNo.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceDialog_BankRefNo.value"), 
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, false));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cPProvince.setConstraint("");
		this.cPProvinceName.setConstraint("");
		this.cPCountry.setConstraint("");
		this.bankRefNo.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.cPProvince.setErrorMessage("");
		this.cPProvinceName.setErrorMessage("");
		this.cPCountry.setErrorMessage("");
		this.bankRefNo.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getProvinceListCtrl().search();
	} 


	// CRUD operations

	/**
	 * Deletes a Province object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Province aProvince = new Province();
		BeanUtils.copyProperties(getProvince(), aProvince);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_ProvinceDialog_CPCountry.value")+" : "+aProvince.getCPCountry()+","+
				Labels.getLabel("label_ProvinceDialog_CPProvince.value")+" : "+aProvince.getCPProvince();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true);

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(aProvince.getRecordType())){
				aProvince.setVersion(aProvince.getVersion()+1);
				aProvince.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aProvince.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aProvince,tranType)){
					refreshList();
					closeDialog(); 
				}
			}catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getProvince().isNewRecord()){
			this.cPCountry.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.cPProvince.setReadonly(false);
		}else{
			this.cPCountry.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.cPProvince.setReadonly(true);
		}

		this.cPProvinceName.setReadonly(isReadOnly("ProvinceDialog_cPProvinceName"));
		this.bankRefNo.setReadonly(isReadOnly("ProvinceDialog_BankRefNo"));
		this.cPIsActive.setDisabled(isReadOnly("ProvinceDialog_CPIsActive"));
//		this.systemDefault.setDisabled(isReadOnly("ProvinceDialog_systemDefault"));
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.province.isNewRecord()){
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
		this.cPCountry.setReadonly(true);
		this.cPProvince.setReadonly(true);
		this.cPProvinceName.setReadonly(true);
		this.bankRefNo.setReadonly(true);
		this.systemDefault.setDisabled(true);
		this.cPIsActive.setDisabled(true);

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
		this.cPCountry.setValue("");
		this.cPCountry.setDescription("");
		this.cPProvince.setValue("");
		this.cPProvinceName.setValue("");
		this.bankRefNo.setValue("");
		this.cPIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Province aProvince = new Province();
		BeanUtils.copyProperties(getProvince(), aProvince);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Province object with the components data
		doWriteComponentsToBean(aProvince);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aProvince.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProvince.getRecordType())){
				aProvince.setVersion(aProvince.getVersion()+1);
				if(isNew){
					aProvince.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aProvince.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProvince.setNewRecord(true);
				}
			}
		}else{
			aProvince.setVersion(aProvince.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aProvince,tranType)){
				refreshList();
				// Close the Existing Dialog
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
	 * @param aProvince (Province)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Province aProvince,String tranType){
		logger.debug("Leaving");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aProvince.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getLoginUsrID());
		aProvince.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProvince.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aProvince.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProvince.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aProvince);
				}

				if (isNotesMandatory(taskId, aProvince)) {
					try {
						if (!notesEntered){
							MessageUtil.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
							return false;
						}
					} catch (InterruptedException e) {
						logger.error("Exception: ", e);
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

			aProvince.setTaskId(taskId);
			aProvince.setNextTaskId(nextTaskId);
			aProvince.setRoleCode(getRole());
			aProvince.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aProvince, tranType);

			String operationRefs = getServiceOperations(taskId, aProvince);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aProvince, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aProvince, tranType);
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
		Province aProvince = (Province) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getProvinceService().delete(auditHeader);

						deleteNotes=true;
					}else{
						auditHeader = getProvinceService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getProvinceService().doApprove(auditHeader);

						if(aProvince.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getProvinceService().doReject(auditHeader);
						if(aProvince.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_ProvinceDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_ProvinceDialog, auditHeader);

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					
					if(deleteNotes){
						deleteNotes(getNotes(this.province),true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	
	// WorkFlow Details	
	
	/**
	 * Get Audit Header Details
	 * @param aProvince (Province)
	 * @param tranType (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Province aProvince, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aProvince.getBefImage(), aProvince);
		return new AuditHeader(getReference(), null, null,
				null, auditDetail, aProvince.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ProvinceDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 *  Get the window for entering Notes
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.province);
	}
	
	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getProvince().getCPCountry()+PennantConstants.KEY_SEPERATOR +
				getProvince().getCPProvince();
	}
	
	
	public void setCountrySystemDefault(){
		Filter[] systemDefault=new Filter[1];
		systemDefault[0]=new Filter("SystemDefault", "1",Filter.OP_EQUAL);
		Object countrydef=	PennantAppUtil.getSystemDefault("Country","", systemDefault);
		if (countrydef!=null) {
			sysDefaultCountry=(Country) countrydef;
		}
	}
	
	public void onFulfill$cPCountry(Event event){
		logger.debug("Entering");
		doCheckSystemDefault();
		logger.debug("Leaving");
	}
	
	public void doCheckSystemDefault(){
		logger.debug("Entering");
		if (StringUtils.isNotBlank(this.cPCountry.getValue()) ) {
			if (sysDefaultCountry!=null && sysDefaultCountry.getCountryCode().equals(this.cPCountry.getValue())) {
				this.systemDefault.setDisabled(isReadOnly("ProvinceDialog_systemDefault"));
			}else{
				this.systemDefault.setDisabled(true);
				this.systemDefault.setChecked(false);
			}
		}else{
			this.systemDefault.setDisabled(true);
			this.systemDefault.setChecked(false);
		}
		logger.debug("Entering");
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

	public Province getProvince() {
		return this.province;
	}
	public void setProvince(Province province) {
		this.province = province;
	}

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}
	public ProvinceService getProvinceService() {
		return this.provinceService;
	}

	public void setProvinceListCtrl(ProvinceListCtrl provinceListCtrl) {
		this.provinceListCtrl = provinceListCtrl;
	}
	public ProvinceListCtrl getProvinceListCtrl() {
		return this.provinceListCtrl;
	}

}
