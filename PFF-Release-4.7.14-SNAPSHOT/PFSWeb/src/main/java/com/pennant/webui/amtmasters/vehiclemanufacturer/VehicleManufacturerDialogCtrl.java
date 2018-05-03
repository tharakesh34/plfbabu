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
 * FileName    		:  VehicleManufacturerDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.amtmasters.vehiclemanufacturer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.amtmasters.VehicleManufacturerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/AMTMaster/VehicleManufacturer/vehicleManufacturerDialog.zul file.
 */
public class VehicleManufacturerDialogCtrl extends GFCBaseCtrl<VehicleManufacturer> {
	private static final long serialVersionUID = 530343247281763697L;
	private static final Logger logger = Logger.getLogger(VehicleManufacturerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_VehicleManufacturerDialog; // autowired
	protected Textbox 		manufacturerName; // autowired

	// not auto wired vars
	private VehicleManufacturer vehicleManufacturer; // overhanded per param
	private transient VehicleManufacturerListCtrl vehicleManufacturerListCtrl; // overhanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient VehicleManufacturerService vehicleManufacturerService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();


	/**
	 * default constructor.<br>
	 */
	public VehicleManufacturerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VehicleManufacturerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected VehicleManufacturer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VehicleManufacturerDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_VehicleManufacturerDialog);

		try {
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			if (arguments.containsKey("vehicleManufacturer")) {
				this.vehicleManufacturer = (VehicleManufacturer) arguments
						.get("vehicleManufacturer");
				VehicleManufacturer befImage = new VehicleManufacturer();
				BeanUtils.copyProperties(this.vehicleManufacturer, befImage);
				this.vehicleManufacturer.setBefImage(befImage);

				setVehicleManufacturer(this.vehicleManufacturer);
			} else {
				setVehicleManufacturer(null);
			}

			doLoadWorkFlow(this.vehicleManufacturer.isWorkflow(),
					this.vehicleManufacturer.getWorkflowId(),
					this.vehicleManufacturer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"VehicleManufacturerDialog");
			}

			// READ OVERHANDED params !
			// we get the vehicleManufacturerListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete vehicleManufacturer here.
			if (arguments.containsKey("vehicleManufacturerListCtrl")) {
				setVehicleManufacturerListCtrl((VehicleManufacturerListCtrl) arguments
						.get("vehicleManufacturerListCtrl"));
			} else {
				setVehicleManufacturerListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVehicleManufacturer());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_VehicleManufacturerDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.manufacturerName.setMaxlength(50);

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VehicleManufacturerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VehicleManufacturerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VehicleManufacturerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VehicleManufacturerDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_VehicleManufacturerDialog);
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
		doWriteBeanToComponents(this.vehicleManufacturer.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVehicleManufacturer
	 *            VehicleManufacturer
	 */
	public void doWriteBeanToComponents(VehicleManufacturer aVehicleManufacturer) {
		logger.debug("Entering") ;
		this.manufacturerName.setValue(aVehicleManufacturer.getManufacturerName());

		this.recordStatus.setValue(aVehicleManufacturer.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVehicleManufacturer
	 */
	public void doWriteComponentsToBean(VehicleManufacturer aVehicleManufacturer) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aVehicleManufacturer.setManufacturerName(this.manufacturerName.getValue());
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

		aVehicleManufacturer.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aVehicleManufacturer
	 * @throws Exception
	 */
	public void doShowDialog(VehicleManufacturer aVehicleManufacturer) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aVehicleManufacturer.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.manufacturerName.focus();
		} else {
			this.manufacturerName.focus();
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
			doWriteBeanToComponents(aVehicleManufacturer);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_VehicleManufacturerDialog.onClose();
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

		if (!this.manufacturerName.isReadonly()){
			this.manufacturerName.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleManufacturerDialog_ManufacturerName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.manufacturerName.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a VehicleManufacturer object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final VehicleManufacturer aVehicleManufacturer = new VehicleManufacturer();
		BeanUtils.copyProperties(getVehicleManufacturer(), aVehicleManufacturer);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_VehicleManufacturerDialog_ManufacturerName.value")+" : "+aVehicleManufacturer.getManufacturerName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aVehicleManufacturer.getRecordType())){
				aVehicleManufacturer.setVersion(aVehicleManufacturer.getVersion()+1);
				aVehicleManufacturer.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aVehicleManufacturer.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aVehicleManufacturer,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
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

		if (getVehicleManufacturer().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}

		this.manufacturerName.setReadonly(isReadOnly("VehicleManufacturerDialog_manufacturerName"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.vehicleManufacturer.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.manufacturerName.setReadonly(true);

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

		this.manufacturerName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VehicleManufacturer aVehicleManufacturer = new VehicleManufacturer();
		BeanUtils.copyProperties(getVehicleManufacturer(), aVehicleManufacturer);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the VehicleManufacturer object with the components data
		doWriteComponentsToBean(aVehicleManufacturer);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aVehicleManufacturer.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVehicleManufacturer.getRecordType())){
				aVehicleManufacturer.setVersion(aVehicleManufacturer.getVersion()+1);
				if(isNew){
					aVehicleManufacturer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aVehicleManufacturer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVehicleManufacturer.setNewRecord(true);
				}
			}
		}else{
			aVehicleManufacturer.setVersion(aVehicleManufacturer.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aVehicleManufacturer,tranType)){
				doWriteBeanToComponents(aVehicleManufacturer);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(VehicleManufacturer aVehicleManufacturer,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aVehicleManufacturer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVehicleManufacturer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVehicleManufacturer.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVehicleManufacturer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVehicleManufacturer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVehicleManufacturer);
				}

				if (isNotesMandatory(taskId, aVehicleManufacturer)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}


			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
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

			aVehicleManufacturer.setTaskId(taskId);
			aVehicleManufacturer.setNextTaskId(nextTaskId);
			aVehicleManufacturer.setRoleCode(getRole());
			aVehicleManufacturer.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aVehicleManufacturer, tranType);

			String operationRefs = getServiceOperations(taskId, aVehicleManufacturer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aVehicleManufacturer, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aVehicleManufacturer, tranType);
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

		VehicleManufacturer aVehicleManufacturer = (VehicleManufacturer) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getVehicleManufacturerService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getVehicleManufacturerService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getVehicleManufacturerService().doApprove(auditHeader);

						if(aVehicleManufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getVehicleManufacturerService().doReject(auditHeader);
						if(aVehicleManufacturer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_VehicleManufacturerDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_VehicleManufacturerDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.vehicleManufacturer),true);
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



	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public VehicleManufacturer getVehicleManufacturer() {
		return this.vehicleManufacturer;
	}

	public void setVehicleManufacturer(VehicleManufacturer vehicleManufacturer) {
		this.vehicleManufacturer = vehicleManufacturer;
	}

	public void setVehicleManufacturerService(VehicleManufacturerService vehicleManufacturerService) {
		this.vehicleManufacturerService = vehicleManufacturerService;
	}

	public VehicleManufacturerService getVehicleManufacturerService() {
		return this.vehicleManufacturerService;
	}

	public void setVehicleManufacturerListCtrl(VehicleManufacturerListCtrl vehicleManufacturerListCtrl) {
		this.vehicleManufacturerListCtrl = vehicleManufacturerListCtrl;
	}

	public VehicleManufacturerListCtrl getVehicleManufacturerListCtrl() {
		return this.vehicleManufacturerListCtrl;
	}

	private AuditHeader getAuditHeader(VehicleManufacturer aVehicleManufacturer, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVehicleManufacturer.getBefImage(), aVehicleManufacturer);   
		return new AuditHeader(String.valueOf(aVehicleManufacturer.getManufacturerId()),null,null,null,auditDetail,aVehicleManufacturer.getUserDetails(),getOverideMap());
	}

	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_VehicleManufacturerDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}


	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.vehicleManufacturer);
	}

	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.manufacturerName.setErrorMessage("");
		logger.debug("Leaving");
	}
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getVehicleManufacturerListCtrl().search();
	} 
	
	@Override
	protected String getReference() {
		return String.valueOf(this.vehicleManufacturer.getManufacturerId());
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

}
