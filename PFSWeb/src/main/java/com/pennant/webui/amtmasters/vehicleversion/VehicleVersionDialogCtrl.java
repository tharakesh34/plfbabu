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
 * FileName    		:  VehicleVersionDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.amtmasters.vehicleversion;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.amtmasters.VehicleVersionService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/AMTMasters/VehicleVersion/vehicleVersionDialog.zul file.
 */
public class VehicleVersionDialogCtrl extends GFCBaseCtrl<VehicleVersion> {
	private static final long serialVersionUID = -5881791444317096938L;
	private static final Logger logger = Logger.getLogger(VehicleVersionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VehicleVersionDialog; // autowired
	protected ExtendedCombobox vehicleModelId; 	  // autowired
	protected Textbox vehicleVersionCode; 		  // autowired
	protected Combobox vehicleCategory;  		  // autowired
	protected Intbox vehicleDoors;  		  	  // autowired
	protected Intbox vehicleCc;  		       	  // autowired

	// not auto wired vars
	private VehicleVersion vehicleVersion; 							 // overhanded per param
	private transient VehicleVersionListCtrl vehicleVersionListCtrl; // overhanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient VehicleVersionService vehicleVersionService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public VehicleVersionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VehicleVersionDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected VehicleVersion object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VehicleVersionDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_VehicleVersionDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			if (arguments.containsKey("vehicleVersion")) {
				this.vehicleVersion = (VehicleVersion) arguments
						.get("vehicleVersion");
				VehicleVersion befImage = new VehicleVersion();
				BeanUtils.copyProperties(this.vehicleVersion, befImage);
				this.vehicleVersion.setBefImage(befImage);

				setVehicleVersion(this.vehicleVersion);
			} else {
				setVehicleVersion(null);
			}

			doLoadWorkFlow(this.vehicleVersion.isWorkflow(),
					this.vehicleVersion.getWorkflowId(),
					this.vehicleVersion.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"VehicleVersionDialog");
			}

			// READ OVERHANDED params !
			// we get the vehicleVersionListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete vehicleVersion here.
			if (arguments.containsKey("vehicleVersionListCtrl")) {
				setVehicleVersionListCtrl((VehicleVersionListCtrl) arguments
						.get("vehicleVersionListCtrl"));
			} else {
				setVehicleVersionListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVehicleVersion());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_VehicleVersionDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.vehicleVersionCode.setMaxlength(50);
		this.vehicleDoors.setMaxlength(2);
		this.vehicleCc.setMaxlength(4);
		this.vehicleModelId.setInputAllowed(false);
		this.vehicleModelId.setDisplayStyle(3); 
		this.vehicleModelId.setMandatoryStyle(true);
		this.vehicleModelId.setModuleName("VehicleModel");
		this.vehicleModelId.setValueColumn("lovDescVehicleManufacturerName");
		this.vehicleModelId.setDescColumn("VehicleModelDesc");
		this.vehicleModelId.setValidateColumns(new String[] { "VehicleModelId" });
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_VehicleVersionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_VehicleVersionDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_VehicleVersionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_VehicleVersionDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_VehicleVersionDialog);
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
		doWriteBeanToComponents(this.vehicleVersion.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVehicleVersion
	 *            VehicleVersion
	 */
	public void doWriteBeanToComponents(VehicleVersion aVehicleVersion) {
		logger.debug("Entering") ;
		
		this.vehicleVersionCode.setValue(aVehicleVersion.getVehicleVersionCode());
		this.vehicleDoors.setValue(aVehicleVersion.getVehicleDoors());
		this.vehicleCc.setValue(aVehicleVersion.getVehicleCc());
		this.vehicleModelId.setValue(String.valueOf(aVehicleVersion.getVehicleModelId()));
		fillComboBox(this.vehicleCategory, aVehicleVersion.getVehicleCategory(), PennantAppUtil.getFieldCodeList("VEHICLECAT"), "");
		if (aVehicleVersion.isNewRecord()) {
			this.vehicleModelId.setValue("");
		} else {
			this.vehicleModelId.setDescription(aVehicleVersion.getLovDescVehicleModelDesc());
		}
		this.recordStatus.setValue(aVehicleVersion.getRecordStatus());
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVehicleVersion
	 */
	public void doWriteComponentsToBean(VehicleVersion aVehicleVersion) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aVehicleVersion.setLovDescVehicleModelIdName(this.vehicleModelId.getDescription());
			aVehicleVersion.setVehicleModelId(Long.parseLong(this.vehicleModelId.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aVehicleVersion.setVehicleVersionCode(this.vehicleVersionCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aVehicleVersion.setVehicleDoors(this.vehicleDoors.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aVehicleVersion.setVehicleCc(this.vehicleCc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(!this.vehicleCategory.isDisabled()){
				if("#".equals(getComboboxValue(this.vehicleCategory))) {
					throw new WrongValueException(this.vehicleCategory, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_VehicleVersionDialog_VehicleCategory.value") }));
				}
			}
			aVehicleVersion.setVehicleCategory(getComboboxValue(this.vehicleCategory));
		} catch (WrongValueException we) {
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

		aVehicleVersion.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aVehicleVersion
	 * @throws Exception
	 */
	public void doShowDialog(VehicleVersion aVehicleVersion) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aVehicleVersion.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.vehicleModelId.focus();
		} else {
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
			doWriteBeanToComponents(aVehicleVersion);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_VehicleVersionDialog.onClose();
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

		if (!this.vehicleVersionCode.isReadonly()){
			this.vehicleVersionCode.setConstraint(new PTStringValidator(Labels.getLabel(
					"label_VehicleVersionDialog_VehicleVersionCode.value"), PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}	
		if (!this.vehicleDoors.isReadonly()){
			this.vehicleDoors.setConstraint(new PTNumberValidator(Labels.getLabel("label_VehicleVersionDialog_VehicleDoors.value"),true,false,2,8));
		}	
		if (!this.vehicleCc.isReadonly()){
			this.vehicleCc.setConstraint(new PTNumberValidator(Labels.getLabel("label_VehicleVersionDialog_VehicleCC.value"),true, false));
		}	
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		
		setValidationOn(false);
		this.vehicleVersionCode.setConstraint("");
		this.vehicleDoors.setConstraint("");
		this.vehicleCc.setConstraint("");
		
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a VehicleVersion object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final VehicleVersion aVehicleVersion = new VehicleVersion();
		BeanUtils.copyProperties(getVehicleVersion(), aVehicleVersion);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
		.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " +Labels.getLabel("label_VehicleVersionDialog_VehicleVersionCode.value")+": "+ aVehicleVersion.getVehicleVersionCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aVehicleVersion.getRecordType())){
				aVehicleVersion.setVersion(aVehicleVersion.getVersion()+1);
				aVehicleVersion.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aVehicleVersion.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aVehicleVersion,tranType)){
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

		if (getVehicleVersion().isNewRecord()) {
			this.vehicleVersionCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.vehicleVersionCode.setReadonly(true);
			this.vehicleDoors.setReadonly(false);
			this.vehicleCc.setReadonly(false);
			this.btnCancel.setVisible(true);
		}
		
		this.vehicleCategory.setDisabled(isReadOnly("VehicleVersionDialog_vehicleCategory"));
		this.vehicleDoors.setDisabled(isReadOnly("VehicleVersionDialog_vehicleDoors"));
		this.vehicleCc.setDisabled(isReadOnly("VehicleVersionDialog_vehicleCc"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.vehicleVersion.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.vehicleModelId.setReadonly(true);
		this.vehicleVersionCode.setReadonly(true);
		this.vehicleDoors.setReadonly(true);
		this.vehicleCc.setReadonly(true);
		this.vehicleCategory.setDisabled(true);

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

		this.vehicleModelId.setValue("");
		this.vehicleModelId.setDescription("");
		this.vehicleVersionCode.setValue("");
		this.vehicleDoors.setValue(0);
		this.vehicleCc.setValue(0);
		this.vehicleCategory.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VehicleVersion aVehicleVersion = new VehicleVersion();
		BeanUtils.copyProperties(getVehicleVersion(), aVehicleVersion);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the VehicleVersion object with the components data
		doWriteComponentsToBean(aVehicleVersion);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aVehicleVersion.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVehicleVersion.getRecordType())){
				aVehicleVersion.setVersion(aVehicleVersion.getVersion()+1);
				if(isNew){
					aVehicleVersion.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aVehicleVersion.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVehicleVersion.setNewRecord(true);
				}
			}
		}else{
			aVehicleVersion.setVersion(aVehicleVersion.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aVehicleVersion,tranType)){
				doWriteBeanToComponents(aVehicleVersion);
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
	 * @param aVehicleVersion
	 *            (VehicleVersion)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(VehicleVersion aVehicleVersion,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aVehicleVersion.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aVehicleVersion.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVehicleVersion.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVehicleVersion.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVehicleVersion.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVehicleVersion);
				}

				if (isNotesMandatory(taskId, aVehicleVersion)) {
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

			aVehicleVersion.setTaskId(taskId);
			aVehicleVersion.setNextTaskId(nextTaskId);
			aVehicleVersion.setRoleCode(getRole());
			aVehicleVersion.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aVehicleVersion, tranType);

			String operationRefs = getServiceOperations(taskId, aVehicleVersion);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aVehicleVersion, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aVehicleVersion, tranType);
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

		VehicleVersion aVehicleVersion = (VehicleVersion) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getVehicleVersionService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getVehicleVersionService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getVehicleVersionService().doApprove(auditHeader);

						if(aVehicleVersion.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getVehicleVersionService().doReject(auditHeader);
						if(aVehicleVersion.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_VehicleVersionDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_VehicleVersionDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.vehicleVersion),true);
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

	public void onFulfill$vehicleModelId(Event event){
		logger.debug("Entering"+event);
		Object dataObject = vehicleModelId.getObject();
		if (dataObject instanceof String){
			this.vehicleModelId.setValue(dataObject.toString());
			this.vehicleModelId.setDescription("");
		}else{
			VehicleModel details= (VehicleModel) dataObject;
			if (details != null) {
				this.vehicleModelId.setValue(String.valueOf(details.getVehicleModelId()));
				this.vehicleModelId.setDescription(details.getVehicleModelDesc());
			}
		}
		logger.debug("Leaving"+event);
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aVehicleVersion
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(VehicleVersion aVehicleVersion, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aVehicleVersion.getBefImage(), aVehicleVersion);   
		return new AuditHeader(String.valueOf(aVehicleVersion
				.getVehicleVersionId()), null, null, null, auditDetail,
				aVehicleVersion.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_VehicleVersionDialog, auditHeader);
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
		doShowNotes(this.vehicleVersion);
	}


	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.vehicleModelId.setErrorMessage("");
		this.vehicleVersionCode.setErrorMessage("");
		this.vehicleDoors.setErrorMessage("");
		this.vehicleCc.setErrorMessage("");
		this.vehicleCategory.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getVehicleVersionListCtrl().search();
	} 
	
	@Override
	protected String getReference() {
		return String.valueOf(this.vehicleVersion.getVehicleVersionId());
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

	public VehicleVersion getVehicleVersion() {
		return this.vehicleVersion;
	}
	public void setVehicleVersion(VehicleVersion vehicleVersion) {
		this.vehicleVersion = vehicleVersion;
	}

	public void setVehicleVersionService(VehicleVersionService vehicleVersionService) {
		this.vehicleVersionService = vehicleVersionService;
	}
	public VehicleVersionService getVehicleVersionService() {
		return this.vehicleVersionService;
	}

	public void setVehicleVersionListCtrl(VehicleVersionListCtrl vehicleVersionListCtrl) {
		this.vehicleVersionListCtrl = vehicleVersionListCtrl;
	}
	public VehicleVersionListCtrl getVehicleVersionListCtrl() {
		return this.vehicleVersionListCtrl;
	}

	private void doSetLOVValidation() {
		this.vehicleModelId.setConstraint(new PTStringValidator(Labels.getLabel(
						"label_VehicleVersionDialog_VehicleModelId.value"), null, true,true));
	}
	private void doRemoveLOVValidation() {
		this.vehicleModelId.setConstraint("");
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
}
