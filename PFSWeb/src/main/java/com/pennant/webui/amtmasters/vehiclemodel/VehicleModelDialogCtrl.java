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
 * FileName    		:  VehicleModelDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.amtmasters.vehiclemodel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.amtmasters.VehicleModelService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/AMTMaster/VehicleModel/vehicleModelDialog.zul file.
 */
public class VehicleModelDialogCtrl extends GFCBaseCtrl<VehicleModel> {

	private static final long serialVersionUID = 1605933161466141898L;

	private static final Logger logger = Logger.getLogger(VehicleModelDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VehicleModelDialog; // autowired
	protected Textbox vehicleModelDesc; 		// autowired
	protected ExtendedCombobox vehicleManufacterId;

	// not auto wired vars
	private VehicleModel vehicleModel; 							 // overhanded per param
	private transient VehicleModelListCtrl vehicleModelListCtrl; // overhanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient VehicleModelService vehicleModelService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	

	/**
	 * default constructor.<br>
	 */
	public VehicleModelDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VehicleModelDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected VehicleModel object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VehicleModelDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_VehicleModelDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			if (arguments.containsKey("vehicleModel")) {
				this.vehicleModel = (VehicleModel) arguments.get("vehicleModel");
				VehicleModel befImage = new VehicleModel();
				BeanUtils.copyProperties(this.vehicleModel, befImage);
				this.vehicleModel.setBefImage(befImage);

				setVehicleModel(this.vehicleModel);
			} else {
				setVehicleModel(null);
			}

			doLoadWorkFlow(this.vehicleModel.isWorkflow(),
					this.vehicleModel.getWorkflowId(),
					this.vehicleModel.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"VehicleModelDialog");
			}

			// READ OVERHANDED params !
			// we get the vehicleModelListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete vehicleModel here.
			if (arguments.containsKey("vehicleModelListCtrl")) {
				setVehicleModelListCtrl((VehicleModelListCtrl) arguments
						.get("vehicleModelListCtrl"));
			} else {
				setVehicleModelListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVehicleModel());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_VehicleModelDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.vehicleModelDesc.setMaxlength(50);
		this.vehicleManufacterId.setInputAllowed(false);
		this.vehicleManufacterId.setDisplayStyle(3); 
		this.vehicleManufacterId.setMandatoryStyle(true);
		this.vehicleManufacterId.setModuleName("VehicleManufacturer");
		this.vehicleManufacterId.setValueColumn("ManufacturerId");
		this.vehicleManufacterId.setDescColumn("ManufacturerName");
		this.vehicleManufacterId.setValidateColumns(new String[] { "ManufacturerId" });
		
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
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VehicleModelDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VehicleModelDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VehicleModelDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VehicleModelDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_VehicleModelDialog);
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
		doWriteBeanToComponents(this.vehicleModel.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVehicleModel
	 *            VehicleModel
	 */
	public void doWriteBeanToComponents(VehicleModel aVehicleModel) {
		logger.debug("Entering") ;
		this.vehicleModelDesc.setValue(aVehicleModel.getVehicleModelDesc());
		this.vehicleManufacterId.setValue(String.valueOf(aVehicleModel.getVehicleManufacturerId()));
		if (aVehicleModel.isNewRecord()){
			   this.vehicleManufacterId.setDescription("");
		}else{
			   this.vehicleManufacterId.setDescription(aVehicleModel.getLovDescVehicleManufacturerName());
		}
		this.recordStatus.setValue(aVehicleModel.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVehicleModel
	 */
	public void doWriteComponentsToBean(VehicleModel aVehicleModel) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			aVehicleModel.setLovDescVehicleManufacturerName(this.vehicleManufacterId.getDescription());
			aVehicleModel.setVehicleManufacturerId(Long.parseLong(this.vehicleManufacterId.getValidatedValue()));	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
		    aVehicleModel.setVehicleModelDesc(this.vehicleModelDesc.getValue());
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
		
		aVehicleModel.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aVehicleModel
	 * @throws Exception
	 */
	public void doShowDialog(VehicleModel aVehicleModel) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aVehicleModel.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.vehicleManufacterId.focus();
		} else {
			this.vehicleManufacterId.focus();
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
			doWriteBeanToComponents(aVehicleModel);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_VehicleModelDialog.onClose();
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
		
		if (!this.vehicleModelDesc.isReadonly()){
			this.vehicleModelDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleModelDialog_VehicleModelDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.vehicleModelDesc.setConstraint("");
	logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a VehicleModel object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final VehicleModel aVehicleModel = new VehicleModel();
		BeanUtils.copyProperties(getVehicleModel(), aVehicleModel);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_VehicleModelDialog_VehicleModelDesc.value")+" : "+aVehicleModel.getVehicleModelDesc();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aVehicleModel.getRecordType())){
				aVehicleModel.setVersion(aVehicleModel.getVersion()+1);
				aVehicleModel.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aVehicleModel.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aVehicleModel,tranType)){
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
		
		if (getVehicleModel().isNewRecord()){
			vehicleManufacterId.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			vehicleManufacterId.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
		this.vehicleModelDesc.setReadonly(isReadOnly("VehicleModelDialog_vehicleModelDesc"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.vehicleModel.isNewRecord()){
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
		this.vehicleManufacterId.setReadonly(true);
		this.vehicleModelDesc.setReadonly(true);
		
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
		this.vehicleManufacterId.setValue("");
		this.vehicleManufacterId.setDescription("");
		this.vehicleModelDesc.setValue("");
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VehicleModel aVehicleModel = new VehicleModel();
		BeanUtils.copyProperties(getVehicleModel(), aVehicleModel);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the VehicleModel object with the components data
		doWriteComponentsToBean(aVehicleModel);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aVehicleModel.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVehicleModel.getRecordType())){
				aVehicleModel.setVersion(aVehicleModel.getVersion()+1);
				if(isNew){
					aVehicleModel.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aVehicleModel.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVehicleModel.setNewRecord(true);
				}
			}
		}else{
			aVehicleModel.setVersion(aVehicleModel.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aVehicleModel,tranType)){
				doWriteBeanToComponents(aVehicleModel);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(VehicleModel aVehicleModel,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aVehicleModel.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVehicleModel.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVehicleModel.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVehicleModel.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVehicleModel.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVehicleModel);
				}

				if (isNotesMandatory(taskId, aVehicleModel)) {
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

			aVehicleModel.setTaskId(taskId);
			aVehicleModel.setNextTaskId(nextTaskId);
			aVehicleModel.setRoleCode(getRole());
			aVehicleModel.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aVehicleModel, tranType);
			
			String operationRefs = getServiceOperations(taskId, aVehicleModel);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aVehicleModel, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aVehicleModel, tranType);
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
		
		VehicleModel aVehicleModel = (VehicleModel) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getVehicleModelService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getVehicleModelService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getVehicleModelService().doApprove(auditHeader);

						if(aVehicleModel.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getVehicleModelService().doReject(auditHeader);
						if(aVehicleModel.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_VehicleModelDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_VehicleModelDialog, auditHeader);
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
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());
		
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	public void onFulfill$vehicleManufacterId(Event event){
		logger.debug("Entering"+event);
		   Object dataObject = vehicleManufacterId.getObject();
		   if (dataObject instanceof String){
			   this.vehicleManufacterId.setValue(dataObject.toString());
			   this.vehicleManufacterId.setDescription("");
		   }else{
			   VehicleManufacturer details= (VehicleManufacturer) dataObject;
				if (details != null) {
					this.vehicleManufacterId.setValue(String.valueOf(details.getManufacturerId()));
					this.vehicleManufacterId.setDescription(details.getManufacturerName());
				}
		   }
		   logger.debug("Leaving"+event);
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

	public VehicleModel getVehicleModel() {
		return this.vehicleModel;
	}

	public void setVehicleModel(VehicleModel vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public void setVehicleModelService(VehicleModelService vehicleModelService) {
		this.vehicleModelService = vehicleModelService;
	}

	public VehicleModelService getVehicleModelService() {
		return this.vehicleModelService;
	}

	public void setVehicleModelListCtrl(VehicleModelListCtrl vehicleModelListCtrl) {
		this.vehicleModelListCtrl = vehicleModelListCtrl;
	}

	public VehicleModelListCtrl getVehicleModelListCtrl() {
		return this.vehicleModelListCtrl;
	}
	
	private AuditHeader getAuditHeader(VehicleModel aVehicleModel, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVehicleModel.getBefImage(), aVehicleModel);   
		return new AuditHeader(String.valueOf(aVehicleModel.getVehicleModelId()),null,null,null,auditDetail,aVehicleModel.getUserDetails(),getOverideMap());
	}
	
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_VehicleModelDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
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
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doSetLOVValidation() {
		this.vehicleManufacterId.setConstraint(new PTStringValidator(Labels.getLabel("label_VehicleManufacturerDialog_ManufacturerId.value"), null, true,true));
	}
	private void doRemoveLOVValidation() {
		this.vehicleManufacterId.setConstraint("");
	}
	
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("VehicleModel");
		notes.setReference(String.valueOf(getVehicleModel().getVehicleModelId()));
		notes.setVersion(getVehicleModel().getVersion());
		return notes;
	}
	
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.vehicleManufacterId.setErrorMessage("");
		this.vehicleModelDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getVehicleModelListCtrl().search();
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

}
