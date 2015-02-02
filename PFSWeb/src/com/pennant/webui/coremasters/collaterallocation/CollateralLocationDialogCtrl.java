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
 * FileName    		:  CollateralLocationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.coremasters.collaterallocation;

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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.coremasters.CollateralLocation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.coremasters.CollateralLocationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/coremasters/CollateralLocation/collateralLocationDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CollateralLocationDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralLocationDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralLocationDialog; // autowired
	protected Textbox hZCLO; // autowired
	protected Textbox hZCLC; // autowired

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;

	// not auto wired vars
	private CollateralLocation collateralLocation; // overhanded per param
	private CollateralLocation prvCollateralLocation; // overhanded per param
	private transient CollateralLocationListCtrl collateralLocationListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_hZCLO;
	private transient String  		oldVar_hZCLC;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CollateralLocationDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	
	
	// ServiceDAOs / Domain Classes
	private transient CollateralLocationService collateralLocationService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	

	/**
	 * default constructor.<br>
	 */
	public CollateralLocationDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected CollateralLocation object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CollateralLocationDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {
			/* set components visible dependent of the users rights */
			//doCheckRights();
			
			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
	
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			
			// READ OVERHANDED params !
			if (args.containsKey("collateralLocation")) {
				this.collateralLocation = (CollateralLocation) args.get("collateralLocation");
				CollateralLocation befImage =new CollateralLocation();
				BeanUtils.copyProperties(this.collateralLocation, befImage);
				this.collateralLocation.setBefImage(befImage);
				
				setCollateralLocation(this.collateralLocation);
			} else {
				setCollateralLocation(null);
			}
		
			doLoadWorkFlow(this.collateralLocation.isWorkflow(),this.collateralLocation.getWorkflowId(),this.collateralLocation.getNextTaskId());
	
			if (isWorkFlowEnabled()){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "CollateralLocationDialog");
			}
	
		
			// READ OVERHANDED params !
			// we get the collateralLocationListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete collateralLocation here.
			if (args.containsKey("collateralLocationListCtrl")) {
				setCollateralLocationListCtrl((CollateralLocationListCtrl) args.get("collateralLocationListCtrl"));
			} else {
				setCollateralLocationListCtrl(null);
			}
	
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCollateralLocation());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_CollateralLocationDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.hZCLO.setMaxlength(4);
		this.hZCLC.setMaxlength(35);
		
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
		
		getUserWorkspace().alocateAuthorities("CollateralLocationDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CollateralLocationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CollateralLocationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralLocationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CollateralLocationDialog_btnSave"));
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
	public void onClose$window_CollateralLocationDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CollateralLocationDialog);
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
			closeDialog(this.window_CollateralLocationDialog, "CollateralLocation");	
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
	 * @param aCollateralLocation
	 *            CollateralLocation
	 */
	public void doWriteBeanToComponents(CollateralLocation aCollateralLocation) {
		logger.debug("Entering") ;
		this.hZCLO.setValue(aCollateralLocation.getHZCLO());
		this.hZCLC.setValue(aCollateralLocation.getHZCLC());
	
		this.recordStatus.setValue(aCollateralLocation.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCollateralLocation
	 */
	public void doWriteComponentsToBean(CollateralLocation aCollateralLocation) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aCollateralLocation.setHZCLO(this.hZCLO.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCollateralLocation.setHZCLC(this.hZCLC.getValue());
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
		
		aCollateralLocation.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCollateralLocation
	 * @throws InterruptedException
	 */
	public void doShowDialog(CollateralLocation aCollateralLocation) throws InterruptedException {
		logger.debug("Entering") ;
		
		// if aCollateralLocation == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCollateralLocation == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCollateralLocation = getCollateralLocationService().getNewCollateralLocation();
			
			setCollateralLocation(aCollateralLocation);
		} else {
			setCollateralLocation(aCollateralLocation);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aCollateralLocation.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.hZCLO.focus();
		} else {
			this.hZCLC.focus();
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
			doWriteBeanToComponents(aCollateralLocation);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CollateralLocationDialog);
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
		this.oldVar_hZCLO = this.hZCLO.getValue();
		this.oldVar_hZCLC = this.hZCLC.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.hZCLO.setValue(this.oldVar_hZCLO);
		this.hZCLC.setValue(this.oldVar_hZCLC);
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
		if (this.oldVar_hZCLO != this.hZCLO.getValue()) {
			return true;
		}
		if (this.oldVar_hZCLC != this.hZCLC.getValue()) {
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
		
		if (!this.hZCLO.isReadonly()){
			this.hZCLO.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralLocationDialog_HZCLO.value"),null,true));
		}	
		if (!this.hZCLC.isReadonly()){
			this.hZCLC.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralLocationDialog_HZCLC.value"),null,true));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.hZCLO.setConstraint("");
		this.hZCLC.setConstraint("");
	logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CollateralLocation object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CollateralLocation aCollateralLocation = new CollateralLocation();
		BeanUtils.copyProperties(getCollateralLocation(), aCollateralLocation);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCollateralLocation.getHZCLO();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCollateralLocation.getRecordType()).equals("")){
				aCollateralLocation.setVersion(aCollateralLocation.getVersion()+1);
				aCollateralLocation.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aCollateralLocation.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCollateralLocation,tranType)){
					refreshList();
					closeDialog(this.window_CollateralLocationDialog, "CollateralLocation"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CollateralLocation object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		final CollateralLocation aCollateralLocation = getCollateralLocationService().getNewCollateralLocation();
		aCollateralLocation.setNewRecord(true);
		setCollateralLocation(aCollateralLocation);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.hZCLO.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getCollateralLocation().isNewRecord()){
		  	this.hZCLO.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.hZCLO.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
		readOnlyComponent(isReadOnly("CollateralLocationDialog_hZCLC"), this.hZCLC);

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.collateralLocation.isNewRecord()){
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
		this.hZCLO.setReadonly(true);
		this.hZCLC.setReadonly(true);
		
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
		
		this.hZCLO.setValue("");
		this.hZCLC.setValue("");
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CollateralLocation aCollateralLocation = new CollateralLocation();
		BeanUtils.copyProperties(getCollateralLocation(), aCollateralLocation);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CollateralLocation object with the components data
		doWriteComponentsToBean(aCollateralLocation);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aCollateralLocation.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCollateralLocation.getRecordType()).equals("")){
				aCollateralLocation.setVersion(aCollateralLocation.getVersion()+1);
				if(isNew){
					aCollateralLocation.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCollateralLocation.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCollateralLocation.setNewRecord(true);
				}
			}
		}else{
			aCollateralLocation.setVersion(aCollateralLocation.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aCollateralLocation,tranType)){
				doWriteBeanToComponents(aCollateralLocation);
				refreshList();
				closeDialog(this.window_CollateralLocationDialog, "CollateralLocation");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(CollateralLocation aCollateralLocation,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aCollateralLocation.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCollateralLocation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCollateralLocation.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCollateralLocation.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCollateralLocation.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCollateralLocation);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCollateralLocation))) {
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

			aCollateralLocation.setTaskId(taskId);
			aCollateralLocation.setNextTaskId(nextTaskId);
			aCollateralLocation.setRoleCode(getRole());
			aCollateralLocation.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aCollateralLocation, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCollateralLocation);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCollateralLocation, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aCollateralLocation, tranType);
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
		
		CollateralLocation aCollateralLocation = (CollateralLocation) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCollateralLocationService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCollateralLocationService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCollateralLocationService().doApprove(auditHeader);

						if(aCollateralLocation.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCollateralLocationService().doReject(auditHeader);
						if(aCollateralLocation.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CollateralLocationDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_CollateralLocationDialog, auditHeader);
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
	


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CollateralLocation getCollateralLocation() {
		return this.collateralLocation;
	}

	public void setCollateralLocation(CollateralLocation collateralLocation) {
		this.collateralLocation = collateralLocation;
	}

	public void setCollateralLocationService(CollateralLocationService collateralLocationService) {
		this.collateralLocationService = collateralLocationService;
	}

	public CollateralLocationService getCollateralLocationService() {
		return this.collateralLocationService;
	}

	public void setCollateralLocationListCtrl(CollateralLocationListCtrl collateralLocationListCtrl) {
		this.collateralLocationListCtrl = collateralLocationListCtrl;
	}

	public CollateralLocationListCtrl getCollateralLocationListCtrl() {
		return this.collateralLocationListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	private AuditHeader getAuditHeader(CollateralLocation aCollateralLocation, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCollateralLocation.getBefImage(), aCollateralLocation);   
		return new AuditHeader(aCollateralLocation.getHZCLO(),null,null,null,auditDetail,aCollateralLocation.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CollateralLocationDialog, auditHeader);
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
	}
	private void doRemoveLOVValidation() {
	}
	
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("CollateralLocation");
		notes.setReference(getCollateralLocation().getHZCLO());
		notes.setVersion(getCollateralLocation().getVersion());
		return notes;
	}
	
	private void doClearMessage() {
		logger.debug("Entering");
			this.hZCLO.setErrorMessage("");
			this.hZCLC.setErrorMessage("");
	logger.debug("Leaving");
	}
	

private void refreshList(){
		final JdbcSearchObject<CollateralLocation> soCollateralLocation = getCollateralLocationListCtrl().getSearchObj();
		getCollateralLocationListCtrl().pagingCollateralLocationList.setActivePage(0);
		getCollateralLocationListCtrl().getPagedListWrapper().setSearchObject(soCollateralLocation);
		if(getCollateralLocationListCtrl().listBoxCollateralLocation!=null){
			getCollateralLocationListCtrl().listBoxCollateralLocation.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public CollateralLocation getPrvCollateralLocation() {
		return prvCollateralLocation;
	}
}
