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
 * FileName    		:  BaseRateDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.baserate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.BaseRateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/BaseRate/baseRateDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class BaseRateDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -5990530952612454146L;
	private final static Logger logger = Logger.getLogger(BaseRateDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BaseRateDialog; 	// autoWired

	protected Textbox 		bRType; 				// autoWired
  	protected Datebox 		bREffDate;	 			// autoWired
	protected Decimalbox 	bRRate; 				// autoWired
	protected Checkbox 		deleteRate; 			// autoWired	

	protected Label 		recordStatus; 			// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;


	// not autoWired Var's
	private BaseRate baseRate; 								// overHanded per parameter
	private transient BaseRateListCtrl baseRateListCtrl; 	// overHanded per parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  		oldVar_bRType;
	private transient Date  		oldVar_bREffDate;
	private transient BigDecimal  	oldVar_bRRate;
	private transient boolean  		oldVar_deleteRate;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_BaseRateDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWired
	protected Button btnEdit; 		// autoWired
	protected Button btnDelete; 	// autoWired
	protected Button btnSave; 		// autoWired
	protected Button btnCancel; 	// autoWired
	protected Button btnClose; 		// autoWired
	protected Button btnHelp; 		// autoWired
	protected Button btnNotes; 		// autoWired
	
	protected Button btnSearchBRType; // autoWired
	protected Textbox lovDescBRTypeName;
	private transient String 	oldVar_lovDescBRTypeName;
	
	// ServiceDAOs / Domain Classes
	private transient BaseRateService baseRateService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public BaseRateDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected BaseRate object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BaseRateDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		
		// READ OVERHANDED parameters !
		if (args.containsKey("baseRate")) {
			this.baseRate = (BaseRate) args.get("baseRate");
			BaseRate befImage =new BaseRate();
			BeanUtils.copyProperties(this.baseRate, befImage);
			this.baseRate.setBefImage(befImage);
			
			setBaseRate(this.baseRate);
		} else {
			setBaseRate(null);
		}
	
		doLoadWorkFlow(this.baseRate.isWorkflow(),this.baseRate.getWorkflowId(),
				this.baseRate.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "BaseRateDialog");
		}

	
		// READ OVERHANDED parameters !
		// we get the baseRateListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete baseRate here.
		if (args.containsKey("baseRateListCtrl")) {
			setBaseRateListCtrl((BaseRateListCtrl) args.get("baseRateListCtrl"));
		} else {
			setBaseRateListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getBaseRate());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.bRType.setMaxlength(8);
	  	this.bREffDate.setFormat(PennantConstants.dateFormat);
	  	this.bRRate.setMaxlength(13);
	  	this.bRRate.setFormat(PennantConstants.rateFormate9);
	  	this.bRRate.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.bRRate.setScale(9);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
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
		getUserWorkspace().alocateAuthorities("BaseRateDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BaseRateDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BaseRateDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BaseRateDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BaseRateDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
	public void onClose$window_BaseRateDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doClose();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());		
		doSave();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering"+event.toString());
		doEdit();
		// remember the old Var's
		doStoreInitValues();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_BaseRateDialog);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering"+event.toString());
		doNew();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		doDelete();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering"+event.toString());
		doCancel();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving"+event.toString());
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
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, 
					MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		
		if(close){
			closeDialog(this.window_BaseRateDialog, "BaseRate");
		}	
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBaseRate
	 *            BaseRate
	 */
	public void doWriteBeanToComponents(BaseRate aBaseRate) {
		logger.debug("Entering");
		this.bRType.setValue(aBaseRate.getBRType());
		this.bREffDate.setValue(aBaseRate.getBREffDate());
	  	this.bRRate.setValue(aBaseRate.getBRRate()==null?new BigDecimal(0):aBaseRate.getBRRate());
	  	this.deleteRate.setChecked(aBaseRate.isDelExistingRates());

	  	if (aBaseRate.isNewRecord()){
			   this.lovDescBRTypeName.setValue("");
		}else{
			   this.lovDescBRTypeName.setValue(aBaseRate.getBRType()+"-"+
					   aBaseRate.getLovDescBRTypeName());
		}
	  	
		this.recordStatus.setValue(aBaseRate.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBaseRate
	 */
	public void doWriteComponentsToBean(BaseRate aBaseRate) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			aBaseRate.setLovDescBRTypeName(this.lovDescBRTypeName.getValue());
		    aBaseRate.setBRType(this.bRType.getValue().toUpperCase());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(!this.bREffDate.isDisabled()){
				dateValidation();
			}
			aBaseRate.setBREffDate(this.bREffDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (!this.bRRate.isReadonly() && this.bRRate.getValue() == null) {
				throw new WrongValueException(bRRate,  Labels.getLabel("FIELD_NO_NUMBER",
						new String[] { Labels.getLabel("label_BaseRateDialog_BRRate.value") }));
			}
			if(!this.bRRate.isReadonly() && this.bREffDate.isDisabled() && (this.bRRate.getValue() != this.oldVar_bRRate)){
				dateValidation();
			}
			aBaseRate.setBRRate(this.bRRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try{
			aBaseRate.setDelExistingRates(this.deleteRate.isChecked());
		}catch(WrongValueException we ){
			wve.add(we);
		}
		aBaseRate.setLastMdfDate((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"));
		
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		aBaseRate.setRecordStatus(this.recordStatus.getValue());
		setBaseRate(aBaseRate);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBaseRate
	 * @throws InterruptedException
	 */
	public void doShowDialog(BaseRate aBaseRate) throws InterruptedException {

		logger.debug("Entering");
		// if aBaseRate == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aBaseRate == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aBaseRate = getBaseRateService().getNewBaseRate();
			setBaseRate(aBaseRate);
		} else {
			setBaseRate(aBaseRate);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aBaseRate.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.btnSearchBRType.focus();
		} else {
			this.bRRate.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aBaseRate.getRecordType()).equals("")){
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			
			this.btnDelete.setVisible(false);
			//Checking condition for deletion of Object or not
			/*if(baseRate.getRecordStatus().equals(Labels.getLabel("Approved"))){
				final boolean  baseRateDel= getBaseRateService().getBaseRateListById(
						baseRate.getBRType(),baseRate.getBREffDate());
				if(baseRateDel){
					this.btnDelete.setVisible(false);
				}
			}*/
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aBaseRate);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_BaseRateDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initialize values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_bRType = this.bRType.getValue();
		this.oldVar_lovDescBRTypeName = this.lovDescBRTypeName.getValue();
		this.oldVar_bREffDate = this.bREffDate.getValue();
		this.oldVar_bRRate = this.bRRate.getValue();
		this.oldVar_deleteRate = this.deleteRate.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initialize values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.bRType.setValue(this.oldVar_bRType);
		this.lovDescBRTypeName.setValue(this.oldVar_lovDescBRTypeName);
		this.bREffDate.setValue(this.oldVar_bREffDate);
	  	this.bRRate.setValue(this.oldVar_bRRate);
	  	this.deleteRate.setChecked(this.oldVar_deleteRate);
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
		//To clear the Error Messages
		doClearMessage();

		if (this.oldVar_bRType != this.bRType.getValue()) {
			return true;
		}

	  	String old_bREffDate = "";
	  	String new_bREffDate ="";
		if (this.oldVar_bREffDate!=null){
			old_bREffDate=DateUtility.formatDate(this.oldVar_bREffDate,PennantConstants.dateFormat);
		}
		if (this.bREffDate.getValue()!=null){
			new_bREffDate=DateUtility.formatDate(this.bREffDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_bREffDate).equals(
				StringUtils.trimToEmpty(new_bREffDate))) {
			return true;
		}
		if (this.oldVar_bRRate != this.bRRate.getValue()) {
			return true;
		}
		if(this.oldVar_deleteRate != this.deleteRate.isChecked()){
			return true;
		}
		
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.bREffDate.isDisabled()){
			this.bREffDate.setConstraint("NO EMPTY :" + Labels.getLabel(
					"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
							"label_BaseRateDialog_BREffDate.value")}));
		}
		if (!this.bRRate.isReadonly()){
			this.bRRate.setConstraint(new RateValidator(13,9,
					Labels.getLabel("label_BaseRateDialog_BRRate.value"),true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.bREffDate.setConstraint("");
		this.bRRate.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Set Validations for LOV Fields
	 */	
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescBRTypeName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel(
						"label_BaseRateDialog_BRType.value")}));
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */	
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescBRTypeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.bREffDate.setErrorMessage("");
		this.bRRate.setErrorMessage("");
		this.lovDescBRTypeName.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		final JdbcSearchObject<BaseRate> soObject = getBaseRateListCtrl().getSearchObj();
		getBaseRateListCtrl().pagingBaseRateList.setActivePage(0);
		getBaseRateListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getBaseRateListCtrl().listBoxBaseRate!=null){
			getBaseRateListCtrl().listBoxBaseRate.getListModel();
		}
	} 
	
	/**
	 * Method for BaseRate Date Validation
	 */
	public void dateValidation(){
		Date curBussniessDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		int daysBackward = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("BVRC").toString());
		Date dateBackward = DateUtility.addDays(curBussniessDate, (daysBackward * (-1)));
		
		int daysForward = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("FVRC").toString());
		Date dateForward = DateUtility.addDays(curBussniessDate, (daysForward));
		
		if(this.bREffDate.getValue().before(dateBackward) || this.bREffDate.getValue().after(dateForward)){
			throw new WrongValueException(bREffDate,  Labels.getLabel(
					"DATE_RANGE",new String[]{Labels.getLabel(
					"label_BaseRateDialog_BREffDate.value"),
					DateUtility.formatUtilDate(dateBackward, PennantConstants.dateFormat),
					DateUtility.formatUtilDate(dateForward, PennantConstants.dateFormat)}));
		}
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a BaseRate object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final BaseRate aBaseRate = new BaseRate();
		BeanUtils.copyProperties(getBaseRate(), aBaseRate);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
					+ "\n\n --> " + aBaseRate.getBRType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aBaseRate.getRecordType()).equals("")){
				aBaseRate.setVersion(aBaseRate.getVersion()+1);
				aBaseRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aBaseRate.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aBaseRate,tranType)){
					refreshList();
					closeDialog(this.window_BaseRateDialog, "BaseRate"); 
				}

			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}		
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new BaseRate object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old Var's
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new BaseRate() in the front end.
		// we get it from the back end.
		final BaseRate aBaseRate = getBaseRateService().getNewBaseRate();
		aBaseRate.setNewRecord(true);
		setBaseRate(aBaseRate);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.bRType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getBaseRate().isNewRecord()){
			this.btnSearchBRType.setDisabled(false);
			this.bREffDate.setDisabled(false);
			this.btnCancel.setVisible(false);
		}else{
			this.btnSearchBRType.setDisabled(true);
			this.bREffDate.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		
		this.bRRate.setReadonly(isReadOnly("BaseRateDialog_bRRate"));
		this.deleteRate.setDisabled(isReadOnly("BaseRateDialog_deleteRate"));
		
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.baseRate.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.btnSearchBRType.setDisabled(true);
		this.bREffDate.setDisabled(true);
		this.bRRate.setReadonly(true);
		this.deleteRate.setDisabled(true);
		
		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			
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
		this.bRType.setValue("");
		this.lovDescBRTypeName.setValue(""); 
		this.bREffDate.setText("");
		this.bRRate.setValue("0.00");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final BaseRate aBaseRate = new BaseRate();
		BeanUtils.copyProperties(getBaseRate(), aBaseRate);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the BaseRate object with the components data
		doWriteComponentsToBean(aBaseRate);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here
		
		isNew = aBaseRate.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aBaseRate.getRecordType()).equals("")){
				aBaseRate.setVersion(aBaseRate.getVersion()+1);
				if(isNew){
					aBaseRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aBaseRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBaseRate.setNewRecord(true);
				}
			}
		}else{
			aBaseRate.setVersion(aBaseRate.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aBaseRate,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_BaseRateDialog, "BaseRate");
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
	 * @param aBaseRate (BaseRate)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(BaseRate aBaseRate,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aBaseRate.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aBaseRate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBaseRate.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aBaseRate.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBaseRate.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aBaseRate);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aBaseRate))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
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

			aBaseRate.setTaskId(taskId);
			aBaseRate.setNextTaskId(nextTaskId);
			aBaseRate.setRoleCode(getRole());
			aBaseRate.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aBaseRate, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aBaseRate);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aBaseRate, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aBaseRate, tranType);
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
		BaseRate aBaseRate = (BaseRate) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getBaseRateService().delete(auditHeader);
						
						deleteNotes=true;	
					}else{
						auditHeader = getBaseRateService().saveOrUpdate(auditHeader);	
					}					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getBaseRateService().doApprove(auditHeader);

						if(aBaseRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getBaseRateService().doReject(auditHeader);
						if(aBaseRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_BaseRateDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(this.window_BaseRateDialog, auditHeader);
				
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
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

   public void onClick$btnSearchBRType(Event event){
	   logger.debug("Entering"+event.toString());
	   Object dataObject = ExtendedSearchListBox.show(this.window_BaseRateDialog,"BaseRateCode");
	   if (dataObject instanceof String){
		   this.bRType.setValue(dataObject.toString());
		   this.lovDescBRTypeName.setValue("");
	   }else{
		   BaseRateCode details= (BaseRateCode) dataObject;
			if (details != null) {
				this.bRType.setValue(details.getLovValue());
				   this.lovDescBRTypeName.setValue(
						   details.getLovValue()+"-"+details.getBRTypeDesc());
			}
	   }
	   logger.debug("Leaving"+event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 * @param aBaseRate 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(BaseRate aBaseRate, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBaseRate.getBefImage(), aBaseRate);   
		return new AuditHeader(getReference(),null,null,null,auditDetail,aBaseRate.getUserDetails(),getOverideMap());
	}
	
	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_BaseRateDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering"+event.toString());
		
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
		logger.debug("Leaving"+event.toString());
	}

	//Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	/**
	 * Get the notes entered for rejected reason
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("BaseRate");
		notes.setReference(getReference());
		notes.setVersion(getBaseRate().getVersion());
		logger.debug("Leaving");
		return notes;
	}
	
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getBaseRate().getBRType()+PennantConstants.KEY_SEPERATOR + 
		DateUtility.formatDate(getBaseRate().getBREffDate(), PennantConstants.DBDateFormat);
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

	public BaseRate getBaseRate() {
		return this.baseRate;
	}
	public void setBaseRate(BaseRate baseRate) {
		this.baseRate = baseRate;
	}

	public void setBaseRateService(BaseRateService baseRateService) {
		this.baseRateService = baseRateService;
	}
	public BaseRateService getBaseRateService() {
		return this.baseRateService;
	}

	public void setBaseRateListCtrl(BaseRateListCtrl baseRateListCtrl) {
		this.baseRateListCtrl = baseRateListCtrl;
	}
	public BaseRateListCtrl getBaseRateListCtrl() {
		return this.baseRateListCtrl;
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
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

}
