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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.amtmasters.VehicleVersionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/AMTMasters/VehicleVersion/vehicleVersionDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class VehicleVersionDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5881791444317096938L;
	private final static Logger logger = Logger.getLogger(VehicleVersionDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_VehicleVersionDialog; // autowired
	protected Longbox vehicleModelId; 			  // autowired
	protected Textbox vehicleVersionCode; 		  // autowired

	protected Label recordStatus; 	// autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	

	// not auto wired vars
	private VehicleVersion vehicleVersion; 							 // overhanded per param
	private transient VehicleVersionListCtrl vehicleVersionListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient long   oldVar_vehicleModelId;
	private transient String oldVar_vehicleVersionCode;
	private transient String oldVar_recordStatus;
	private transient String oldVar_lovDescVehicleModelIdName;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_VehicleVersionDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	protected Button btnSearchVehicleModelId; // autowire
	protected Textbox lovDescVehicleModelIdName;


	// ServiceDAOs / Domain Classes
	private transient VehicleVersionService vehicleVersionService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public VehicleVersionDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected VehicleVersion object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VehicleVersionDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("vehicleVersion")) {
			this.vehicleVersion = (VehicleVersion) args.get("vehicleVersion");
			VehicleVersion befImage =new VehicleVersion();
			BeanUtils.copyProperties(this.vehicleVersion, befImage);
			this.vehicleVersion.setBefImage(befImage);

			setVehicleVersion(this.vehicleVersion);
		} else {
			setVehicleVersion(null);
		}

		doLoadWorkFlow(this.vehicleVersion.isWorkflow(),
				this.vehicleVersion.getWorkflowId(),this.vehicleVersion.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "VehicleVersionDialog");
		}


		// READ OVERHANDED params !
		// we get the vehicleVersionListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete vehicleVersion here.
		if (args.containsKey("vehicleVersionListCtrl")) {
			setVehicleVersionListCtrl((VehicleVersionListCtrl) args
					.get("vehicleVersionListCtrl"));
		} else {
			setVehicleVersionListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getVehicleVersion());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.vehicleVersionCode.setMaxlength(50);

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

		getUserWorkspace().alocateAuthorities("VehicleVersionDialog");

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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_VehicleVersionDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_VehicleVersionDialog);
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
			throw e;
		}
		logger.debug("Leaving" + event.toString());
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
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_VehicleVersionDialog, "VehicleVersion");	
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
	 * @param aVehicleVersion
	 *            VehicleVersion
	 */
	public void doWriteBeanToComponents(VehicleVersion aVehicleVersion) {
		logger.debug("Entering") ;
		this.vehicleVersionCode.setValue(aVehicleVersion.getVehicleVersionCode());
		this.vehicleModelId.setValue(aVehicleVersion.getVehicleModelId());

		if (aVehicleVersion.isNewRecord()){
			this.lovDescVehicleModelIdName.setValue("");
		}else{
			this.lovDescVehicleModelIdName.setValue(aVehicleVersion.getVehicleModelId()
					+ "-" + aVehicleVersion.getLovDescVehicleModelDesc());
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
			aVehicleVersion.setLovDescVehicleModelIdName(this.lovDescVehicleModelIdName.getValue());
			aVehicleVersion.setVehicleModelId(this.vehicleModelId.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aVehicleVersion.setVehicleVersionCode(this.vehicleVersionCode.getValue());
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(VehicleVersion aVehicleVersion) throws InterruptedException {
		logger.debug("Entering") ;

		// if aVehicleVersion == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aVehicleVersion == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aVehicleVersion = getVehicleVersionService().getNewVehicleVersion();

			setVehicleVersion(aVehicleVersion);
		} else {
			setVehicleVersion(aVehicleVersion);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aVehicleVersion.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.lovDescVehicleModelIdName.focus();
		} else {
			this.lovDescVehicleModelIdName.focus();
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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_VehicleVersionDialog);
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
		this.oldVar_lovDescVehicleModelIdName = this.lovDescVehicleModelIdName.getValue();
		this.oldVar_vehicleModelId = this.vehicleModelId.longValue();
		this.oldVar_vehicleVersionCode = this.vehicleVersionCode.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.vehicleModelId.setValue(this.oldVar_vehicleModelId);
		this.lovDescVehicleModelIdName.setValue(this.oldVar_lovDescVehicleModelIdName);
		this.vehicleVersionCode.setValue(this.oldVar_vehicleVersionCode);
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
		if (this.oldVar_vehicleModelId != this.vehicleModelId.longValue()){
			return true;
		}
		if (this.oldVar_vehicleVersionCode != this.vehicleVersionCode.getValue()) {
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

		if (!this.vehicleVersionCode.isReadonly()){
			this.vehicleVersionCode.setConstraint("NO EMPTY:" + Labels.getLabel(
					"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
					"label_VehicleVersionDialog_VehicleVersionCode.value")}));
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
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		+ "\n\n --> " + aVehicleVersion.getVehicleVersionId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aVehicleVersion.getRecordType()).equals("")){
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
					closeDialog(this.window_VehicleVersionDialog, "VehicleVersion"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new VehicleVersion object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final VehicleVersion aVehicleVersion = getVehicleVersionService()
				.getNewVehicleVersion();
		setVehicleVersion(aVehicleVersion);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.vehicleVersionCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getVehicleVersion().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}

		this.vehicleVersionCode
				.setReadonly(isReadOnly("VehicleVersionDialog_vehicleVersionCode"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.vehicleVersion.isNewRecord()){
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
		this.btnSearchVehicleModelId.setDisabled(true);
		this.vehicleVersionCode.setReadonly(true);

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

		this.vehicleModelId.setText("");
		this.lovDescVehicleModelIdName.setValue("");
		this.vehicleVersionCode.setValue("");
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aVehicleVersion.getRecordType()).equals("")){
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
				closeDialog(this.window_VehicleVersionDialog, "VehicleVersion");
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

		aVehicleVersion.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aVehicleVersion.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVehicleVersion.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aVehicleVersion.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVehicleVersion.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aVehicleVersion);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aVehicleVersion))) {
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

			aVehicleVersion.setTaskId(taskId);
			aVehicleVersion.setNextTaskId(nextTaskId);
			aVehicleVersion.setRoleCode(getRole());
			aVehicleVersion.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aVehicleVersion, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aVehicleVersion);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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

	public void onClick$btnSearchVehicleModelId(Event event){

		Object dataObject = ExtendedSearchListBox.show(
				this.window_VehicleVersionDialog, "VehicleModel");
		if (dataObject instanceof String){
			this.vehicleModelId.setText(dataObject.toString());
			this.lovDescVehicleModelIdName.setValue("");
		}else{
			VehicleModel details= (VehicleModel) dataObject;
			if (details != null) {
				this.vehicleModelId.setValue(details.getVehicleModelId());
				this.lovDescVehicleModelIdName.setValue(details
						.getVehicleModelId()
						+ "-" + details.getVehicleModelDesc());
			}
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_VehicleVersionDialog, auditHeader);
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

		// call the zul-file with the parameters packed in a map
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
		notes.setModuleName("VehicleVersion");
		notes.setReference(String.valueOf(getVehicleVersion().getVehicleVersionId()));
		notes.setVersion(getVehicleVersion().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.lovDescVehicleModelIdName.setErrorMessage("");
		this.vehicleVersionCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updation
	private void refreshList(){
		final JdbcSearchObject<VehicleVersion> soVehicleVersion = getVehicleVersionListCtrl()
				.getSearchObj();
		getVehicleVersionListCtrl().pagingVehicleVersionList.setActivePage(0);
		getVehicleVersionListCtrl().getPagedListWrapper().setSearchObject(soVehicleVersion);
		if(getVehicleVersionListCtrl().listBoxVehicleVersion!=null){
			getVehicleVersionListCtrl().listBoxVehicleVersion.getListModel();
		}
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

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	private void doSetLOVValidation() {
		this.lovDescVehicleModelIdName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
						"label_VehicleVersionDialog_VehicleModelId.value")}));
	}
	private void doRemoveLOVValidation() {
		this.lovDescVehicleModelIdName.setConstraint("");
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
}
