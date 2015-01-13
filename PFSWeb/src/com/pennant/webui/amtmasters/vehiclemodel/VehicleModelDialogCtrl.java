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
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.amtmasters.VehicleModelService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/AMTMaster/VehicleModel/vehicleModelDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class VehicleModelDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1605933161466141898L;

	private final static Logger logger = Logger.getLogger(VehicleModelDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleModelDialog; // autowired
	protected Textbox vehicleModelDesc; 		// autowired
	protected ExtendedCombobox vehicleManufacterId;

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	

	// not auto wired vars
	private VehicleModel vehicleModel; 							 // overhanded per param
	private VehicleModel prvVehicleModel; 						 // overhanded per param
	private transient VehicleModelListCtrl vehicleModelListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_vehicleModelDesc;
	private transient String oldVar_recordStatus;
	private transient long   oldVar_vehicleManufacterId;
	private transient String oldVar_lovDescManufacter;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_VehicleModelDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire
	
	
	// ServiceDAOs / Domain Classes
	private transient VehicleModelService vehicleModelService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	

	/**
	 * default constructor.<br>
	 */
	public VehicleModelDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected VehicleModel object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VehicleModelDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED params !
		if (args.containsKey("vehicleModel")) {
			this.vehicleModel = (VehicleModel) args.get("vehicleModel");
			VehicleModel befImage =new VehicleModel();
			BeanUtils.copyProperties(this.vehicleModel, befImage);
			this.vehicleModel.setBefImage(befImage);
			
			setVehicleModel(this.vehicleModel);
		} else {
			setVehicleModel(null);
		}
	
		doLoadWorkFlow(this.vehicleModel.isWorkflow(),this.vehicleModel.getWorkflowId(),this.vehicleModel.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "VehicleModelDialog");
		}

	
		// READ OVERHANDED params !
		// we get the vehicleModelListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete vehicleModel here.
		if (args.containsKey("vehicleModelListCtrl")) {
			setVehicleModelListCtrl((VehicleModelListCtrl) args.get("vehicleModelListCtrl"));
		} else {
			setVehicleModelListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getVehicleModel());
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
		
		getUserWorkspace().alocateAuthorities("VehicleModelDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VehicleModelDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VehicleModelDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VehicleModelDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VehicleModelDialog_btnSave"));
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
	public void onClose$window_VehicleModelDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_VehicleModelDialog);
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
			closeDialog(this.window_VehicleModelDialog, "VehicleModel");	
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
			aVehicleModel.setLovDescManufacter(this.vehicleManufacterId.getDescription());
			aVehicleModel.setVehicleManufacturerId(Long.valueOf(this.vehicleManufacterId.getValidatedValue()));	
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(VehicleModel aVehicleModel) throws InterruptedException {
		logger.debug("Entering") ;
		
		// if aVehicleModel == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aVehicleModel == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aVehicleModel = getVehicleModelService().getNewVehicleModel();
			
			setVehicleModel(aVehicleModel);
		} else {
			setVehicleModel(aVehicleModel);
		}

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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_VehicleModelDialog);
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
		this.oldVar_lovDescManufacter=this.vehicleManufacterId.getValue();
		this.oldVar_vehicleManufacterId=Long.valueOf(this.vehicleManufacterId.getValue());
		this.oldVar_vehicleModelDesc = this.vehicleModelDesc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.vehicleManufacterId.setValue(String.valueOf(this.oldVar_vehicleManufacterId));
		this.vehicleManufacterId.setValue(this.oldVar_lovDescManufacter);
		this.vehicleModelDesc.setValue(this.oldVar_vehicleModelDesc);
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
		if (this.oldVar_vehicleManufacterId != Long.valueOf(this.vehicleManufacterId.getValue())){
			return true;
		}
		if (this.oldVar_vehicleModelDesc != this.vehicleModelDesc.getValue()) {
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aVehicleModel.getVehicleModelId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aVehicleModel.getRecordType()).equals("")){
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
					closeDialog(this.window_VehicleModelDialog, "VehicleModel"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new VehicleModel object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		final VehicleModel aVehicleModel = getVehicleModelService().getNewVehicleModel();
		setVehicleModel(aVehicleModel);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.vehicleModelDesc.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getVehicleModel().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
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
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aVehicleModel.getRecordType()).equals("")){
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
				closeDialog(this.window_VehicleModelDialog, "VehicleModel");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(VehicleModel aVehicleModel,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aVehicleModel.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aVehicleModel.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVehicleModel.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aVehicleModel.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVehicleModel.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aVehicleModel);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aVehicleModel))) {
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

			aVehicleModel.setTaskId(taskId);
			aVehicleModel.setNextTaskId(nextTaskId);
			aVehicleModel.setRoleCode(getRole());
			aVehicleModel.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aVehicleModel, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aVehicleModel);
			
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
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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
			logger.error(e);
			e.printStackTrace();
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


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	private AuditHeader getAuditHeader(VehicleModel aVehicleModel, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVehicleModel.getBefImage(), aVehicleModel);   
		return new AuditHeader(String.valueOf(aVehicleModel.getVehicleModelId()),null,null,null,auditDetail,aVehicleModel.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_VehicleModelDialog, auditHeader);
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
	
	private void doClearMessage() {
		logger.debug("Entering");
		this.vehicleManufacterId.setErrorMessage("");
		this.vehicleModelDesc.setErrorMessage("");
	logger.debug("Leaving");
	}
	

private void refreshList(){
		final JdbcSearchObject<VehicleModel> soVehicleModel = getVehicleModelListCtrl().getSearchObj();
		getVehicleModelListCtrl().pagingVehicleModelList.setActivePage(0);
		getVehicleModelListCtrl().getPagedListWrapper().setSearchObject(soVehicleModel);
		if(getVehicleModelListCtrl().listBoxVehicleModel!=null){
			getVehicleModelListCtrl().listBoxVehicleModel.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public VehicleModel getPrvVehicleModel() {
		return prvVehicleModel;
	}
}
