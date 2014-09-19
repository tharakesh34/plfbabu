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
 * FileName    		:  SplRateDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.splrate;

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
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.SplRateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/SplRate/splRateDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SplRateDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6395413534622055634L;
	private final static Logger logger = Logger.getLogger(SplRateDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	 window_SplRateDialog; 	// autoWired
	protected Textbox 	 sRType; 				// autoWired
	protected Datebox 	 sREffDate; 			// autoWired
	protected Decimalbox sRRate; 				// autoWired
	protected Checkbox 	 deleteRate; 			// autoWired

	protected Label 	 recordStatus; 			// autoWired
	protected Radiogroup userAction;
	protected Groupbox 	 groupboxWf;

	// not auto wired variables
	private 		  SplRate 		  splRate; // overHanded per parameter
	private transient SplRateListCtrl splRateListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_sRType;
	private transient Timestamp  	oldVar_sREffDate;
	private transient BigDecimal  	oldVar_sRRate;
	private transient boolean  		oldVar_deleteRate;
	private transient String 		oldVar_recordStatus;
	
	private transient boolean validationOn;
	private 		  boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_SplRateDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	
	protected Button  btnNew; 			// autoWire
	protected Button  btnEdit; 			// autoWire
	protected Button  btnDelete; 		// autoWire
	protected Button  btnSave; 			// autoWire
	protected Button  btnCancel; 		// autoWire
	protected Button  btnClose; 		// autoWire
	protected Button  btnHelp; 			// autoWire
	protected Button  btnNotes; 		// autoWire
	
	protected Button  btnSearchSRType; 	// autoWire
	protected Textbox lovDescSRTypeName;
	private transient String 		oldVar_lovDescSRTypeName;
	
	// ServiceDAOs / Domain Classes
	private transient SplRateService 	splRateService;
	private transient PagedListService 	pagedListService;

	/**
	 * default constructor.<br>
	 */
	public SplRateDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SplRate object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SplRateDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("splRate")) {
			this.splRate = (SplRate) args.get("splRate");
			SplRate befImage =new SplRate();
			BeanUtils.copyProperties(this.splRate, befImage);
			this.splRate.setBefImage(befImage);

			setSplRate(this.splRate);
		} else {
			setSplRate(null);
		}

		doLoadWorkFlow(this.splRate.isWorkflow(), this.splRate.getWorkflowId(),
				this.splRate.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "SplRateDialog");
		}

		// READ OVERHANDED parameters !
		// we get the splRateListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete splRate here.
		if (args.containsKey("splRateListCtrl")) {
			setSplRateListCtrl((SplRateListCtrl) args.get("splRateListCtrl"));
		} else {
			setSplRateListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSplRate());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.sRType.setMaxlength(8);
		this.sREffDate.setFormat(PennantConstants.dateFormat);
		this.sRRate.setMaxlength(13);
		this.sRRate.setFormat(PennantConstants.rateFormate9);
		this.sRRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.sRRate.setScale(9);

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
		getUserWorkspace().alocateAuthorities("SplRateDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SplRateDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SplRateDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SplRateDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SplRateDialog_btnSave"));
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
	public void onClose$window_SplRateDialog(Event event) throws Exception {
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
		// remember the old variables
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_SplRateDialog);
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
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchSRType(Event event){
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_SplRateDialog,"SplRateCode");
		if (dataObject instanceof String){
			this.sRType.setValue(dataObject.toString());
			this.lovDescSRTypeName.setValue("");
		}else{
			SplRateCode details= (SplRateCode) dataObject;
			if (details != null) {
				this.sRType.setValue(details.getLovValue());
				this.lovDescSRTypeName.setValue(details.getSRType()+"-"+details.getSRTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
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
			logger.debug("doClose isDataChanged : true");

			// Show a confirm box
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("doClose isDataChanged : False");
		}

		if(close){
			closeDialog(this.window_SplRateDialog, "SplRate");
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
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSplRate
	 *            SplRate
	 */
	public void doWriteBeanToComponents(SplRate aSplRate) {
		logger.debug("Entering");
		this.sRType.setValue(aSplRate.getSRType());
		this.sREffDate.setValue(aSplRate.getSREffDate());
		this.sRRate.setValue(aSplRate.getSRRate()==null?new BigDecimal(0):aSplRate.getSRRate());
		this.deleteRate.setChecked(aSplRate.isDelExistingRates());

		if (aSplRate.isNewRecord()){
			this.lovDescSRTypeName.setValue("");
		}else{
			this.lovDescSRTypeName.setValue(aSplRate.getSRType()+"-"+aSplRate.getLovDescSRTypeName());
		}
		this.recordStatus.setValue(aSplRate.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSplRate
	 */
	public void doWriteComponentsToBean(SplRate aSplRate) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSplRate.setLovDescSRTypeName(this.lovDescSRTypeName.getValue());
			aSplRate.setSRType(this.sRType.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(!this.sREffDate.isDisabled()){
				dateValidation();
			}
			aSplRate.setSREffDate(this.sREffDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (!this.sRRate.isReadonly() && this.sRRate.getValue() == null) {
				throw new WrongValueException(sRRate,  Labels.getLabel("FIELD_NO_NUMBER",
						new String[] { Labels.getLabel("label_SplRateDialog_SRRate.value") }));
			}
			if(!this.sRRate.isReadonly() && this.sREffDate.isDisabled() && (this.sRRate.getValue() != this.oldVar_sRRate)){
				dateValidation();
			}
			aSplRate.setSRRate(this.sRRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try{
			aSplRate.setDelExistingRates(this.deleteRate.isChecked());
		}catch(WrongValueException we ){
			wve.add(we);
		}
		aSplRate.setLastMdfDate((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"));
		
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aSplRate.setRecordStatus(this.recordStatus.getValue());
		setSplRate(aSplRate);
		logger.debug("Leaving");
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
		
		if(this.sREffDate.getValue().before(dateBackward) || this.sREffDate.getValue().after(dateForward)){
			throw new WrongValueException(sREffDate,  Labels.getLabel(
					"DATE_RANGE",new String[]{Labels.getLabel(
					"label_SplRateDialog_SRRate.value"),
					DateUtility.formatUtilDate(dateBackward, PennantConstants.dateFormat),
					DateUtility.formatUtilDate(dateForward, PennantConstants.dateFormat)}));
		}
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSplRate
	 * @throws InterruptedException
	 */
	public void doShowDialog(SplRate aSplRate) throws InterruptedException {
		logger.debug("Entering");
		// if aSplRate == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aSplRate == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aSplRate = getSplRateService().getNewSplRate();
			setSplRate(aSplRate);
		} else {
			setSplRate(aSplRate);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aSplRate.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.sRType.focus();
		} else {
			this.sRRate.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aSplRate.getRecordType()).equals("")){
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			//Checking condition for deletion of Object or not
			/*if(splRate.getRecordStatus().equals(Labels.getLabel("Approved"))){
				final boolean  splRateDel= getSplRateService().getSplRateListById(
						splRate.getSRType(),splRate.getSREffDate());
				if(splRateDel){
					this.btnDelete.setVisible(false);
				}
			}*/
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSplRate);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SplRateDialog);
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_sRType = this.sRType.getValue();
		this.oldVar_lovDescSRTypeName = this.lovDescSRTypeName.getValue();
		this.oldVar_sREffDate = PennantAppUtil.getTimestamp(this.sREffDate.getValue());	
		this.oldVar_sRRate = this.sRRate.getValue();
		this.oldVar_deleteRate = this.deleteRate.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.sRType.setValue(this.oldVar_sRType);
		this.lovDescSRTypeName.setValue(this.oldVar_lovDescSRTypeName);
		this.sREffDate.setValue(this.oldVar_sREffDate);
		this.sRRate.setValue(this.oldVar_sRRate);
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

		if (this.oldVar_sRType != this.sRType.getValue()) {
			return true;
		}
		
		String oldSREffDate = "";
	  	String newSREffDate ="";
		if (this.oldVar_sREffDate!=null){
			oldSREffDate=DateUtility.formatDate(this.oldVar_sREffDate,PennantConstants.dateFormat);
		}
		if (this.sREffDate.getValue()!=null){
			newSREffDate=DateUtility.formatDate(this.sREffDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldSREffDate).equals(StringUtils.trimToEmpty(newSREffDate))) {
			return true;
		}
		if (this.oldVar_sRRate != this.sRRate.getValue()) {
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

		if (!this.sREffDate.isDisabled()){
			this.sREffDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SplRateDialog_SREffDate.value"), true));
		}
		if (!this.sRRate.isReadonly()){
			this.sRRate.setConstraint(new RateValidator(
					13,9,Labels.getLabel("label_SplRateDialog_SRRate.value"), true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.sREffDate.setConstraint("");
		this.sRRate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescSRTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_SplRateDialog_SRType.value"), null, true));
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescSRTypeName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.sREffDate.setErrorMessage("");
		this.sRRate.setErrorMessage("");
		this.lovDescSRTypeName.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<SplRate> soObject = getSplRateListCtrl().getSearchObj();
		getSplRateListCtrl().pagingSplRateList.setActivePage(0);
		getSplRateListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getSplRateListCtrl().listBoxSplRate!=null){
			getSplRateListCtrl().listBoxSplRate.getListModel();
		}
		logger.debug("Leaving");
	} 

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a SplRate object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SplRate aSplRate = new SplRate();
		BeanUtils.copyProperties(getSplRate(), aSplRate);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aSplRate.getSRType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSplRate.getRecordType()).equals("")){
				aSplRate.setVersion(aSplRate.getVersion()+1);
				aSplRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aSplRate.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aSplRate,tranType)){
					refreshList();
					closeDialog(this.window_SplRateDialog, "SplRate"); 
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new SplRate object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new SplRate() in the frontEnd.
		// we get it from the backEnd.
		final SplRate aSplRate = getSplRateService().getNewSplRate();
		aSplRate.setNewRecord(true);
		setSplRate(aSplRate);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.sRType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getSplRate().isNewRecord()){
			this.btnSearchSRType.setDisabled(false);
			this.btnCancel.setVisible(false);
			this.sREffDate.setDisabled(false);
		}else{
			this.btnSearchSRType.setDisabled(true);
			this.sREffDate.setDisabled(true);
			this.btnCancel.setVisible(true);
		}

		//this.sREffDate.setDisabled(isReadOnly("SplRateDialog_sREffDate"));
		this.sRRate.setReadonly(isReadOnly("SplRateDialog_sRRate"));
		this.deleteRate.setDisabled(isReadOnly("SplRateDialog_deleteRate"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.splRate.isNewRecord()){
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
		this.btnSearchSRType.setDisabled(true);
		this.sREffDate.setDisabled(true);
		this.sRRate.setReadonly(true);
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
		
		// remove validation, if there are a save before
		this.sRType.setValue("");
		this.lovDescSRTypeName.setValue("");
		this.sREffDate.setText("");
		this.sRRate.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SplRate aSplRate = new SplRate();
		BeanUtils.copyProperties(getSplRate(), aSplRate);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the SplRate object with the components data
		doWriteComponentsToBean(aSplRate);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aSplRate.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aSplRate.getRecordType()).equals("")){
				aSplRate.setVersion(aSplRate.getVersion()+1);
				if(isNew){
					aSplRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aSplRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSplRate.setNewRecord(true);
				}
			}
		}else{
			aSplRate.setVersion(aSplRate.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aSplRate,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_SplRateDialog, "SplRate");
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
	 * @param aSplRate (SplRate)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(SplRate aSplRate,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aSplRate.setLastMntBy(getUserWorkspace().getLoginUserDetails()
				.getLoginUsrID());
		aSplRate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSplRate.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aSplRate.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSplRate.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aSplRate);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().
						getAuditingReq(taskId,aSplRate))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel(
									"Notes_NotEmpty"));
							logger.debug("Leaving");
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

			aSplRate.setTaskId(taskId);
			aSplRate.setNextTaskId(nextTaskId);
			aSplRate.setRoleCode(getRole());
			aSplRate.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aSplRate, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aSplRate);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aSplRate, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aSplRate, tranType);
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
		SplRate aSplRate = (SplRate) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getSplRateService().delete(auditHeader);
						
						deleteNotes=true;
					} else {
						auditHeader = getSplRateService().saveOrUpdate(
								auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getSplRateService().doApprove(auditHeader);

						if(aSplRate.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getSplRateService().doReject(auditHeader);
						if(aSplRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_SplRateDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_SplRateDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					
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
	// ++++++++++++++++++ WorkFlow Details +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aSubSegment
	 *            (SubSegment)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(SplRate aSplRate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSplRate.getBefImage(), aSplRate);   
		return new AuditHeader(getReference(), null,null,null,auditDetail,aSplRate.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_SplRateDialog,auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
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
		notes.setModuleName("SplRate");
		notes.setReference(getReference());
		notes.setVersion(getSplRate().getVersion());
		logger.debug("Leaving");
		return notes;
	}
	
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return getSplRate().getSRType()+PennantConstants.KEY_SEPERATOR + 
			DateUtility.formatDate(getSplRate().getSREffDate(), PennantConstants.DBDateFormat);
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

	public SplRate getSplRate() {
		return this.splRate;
	}
	public void setSplRate(SplRate splRate) {
		this.splRate = splRate;
	}

	public void setSplRateService(SplRateService splRateService) {
		this.splRateService = splRateService;
	}
	public SplRateService getSplRateService() {
		return this.splRateService;
	}

	public void setSplRateListCtrl(SplRateListCtrl splRateListCtrl) {
		this.splRateListCtrl = splRateListCtrl;
	}
	public SplRateListCtrl getSplRateListCtrl() {
		return this.splRateListCtrl;
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
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

}
