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
 * FileName    		:  WIFFinanceDisbursementDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.wiffinancedisbursement;

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
import org.zkoss.zul.Intbox;
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
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.WIFFinanceDisbursementService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/WIFFinanceDisbursement/wIFFinanceDisbursementDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class WIFFinanceDisbursementDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(WIFFinanceDisbursementDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WIFFinanceDisbursementDialog; // autowired
	protected Textbox finReference; // autowired
  	protected Datebox disbDate; // autowired
   	protected Intbox disbSeq; // autowired
	protected Textbox disbDesc; // autowired
	protected Decimalbox disbAmount; // autowired
  	protected Datebox disbActDate; // autowired
	protected Checkbox disbDisbursed; // autowired
	protected Checkbox disbIsActive; // autowired
	protected Textbox disbRemarks; // autowired

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;

	// not auto wired vars
	private FinanceDisbursement wIFFinanceDisbursement; // overhanded per param
	private FinanceDisbursement prvWIFFinanceDisbursement; // overhanded per param
	private transient WIFFinanceDisbursementListCtrl wIFFinanceDisbursementListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_finReference;
	private transient Date  		oldVar_disbDate;
	private transient int  		oldVar_disbSeq;
	private transient String  		oldVar_disbDesc;
	private transient BigDecimal  		oldVar_disbAmount;
	private transient Date  		oldVar_disbActDate;
	private transient boolean  		oldVar_disbDisbursed;
	private transient boolean  		oldVar_disbIsActive;
	private transient String  		oldVar_disbRemarks;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_WIFFinanceDisbursementDialog_";
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
	private transient WIFFinanceDisbursementService wIFFinanceDisbursementService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	

	/**
	 * default constructor.<br>
	 */
	public WIFFinanceDisbursementDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected WIFFinanceDisbursement object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_WIFFinanceDisbursementDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED params !
		if (args.containsKey("wIFFinanceDisbursement")) {
			this.wIFFinanceDisbursement = (FinanceDisbursement) args.get("wIFFinanceDisbursement");
			FinanceDisbursement befImage =new FinanceDisbursement();
			BeanUtils.copyProperties(this.wIFFinanceDisbursement, befImage);
			this.wIFFinanceDisbursement.setBefImage(befImage);
			
			setWIFFinanceDisbursement(this.wIFFinanceDisbursement);
		} else {
			setWIFFinanceDisbursement(null);
		}
	
		doLoadWorkFlow(this.wIFFinanceDisbursement.isWorkflow(),this.wIFFinanceDisbursement.getWorkflowId(),this.wIFFinanceDisbursement.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "WIFFinanceDisbursementDialog");
		}

	
		// READ OVERHANDED params !
		// we get the wIFFinanceDisbursementListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete wIFFinanceDisbursement here.
		if (args.containsKey("wIFFinanceDisbursementListCtrl")) {
			setWIFFinanceDisbursementListCtrl((WIFFinanceDisbursementListCtrl) args.get("wIFFinanceDisbursementListCtrl"));
		} else {
			setWIFFinanceDisbursementListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getWIFFinanceDisbursement());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finReference.setMaxlength(20);
	  	this.disbDate.setFormat(PennantConstants.dateFormat);
		this.disbSeq.setMaxlength(10);
		this.disbDesc.setMaxlength(50);
	  	this.disbAmount.setMaxlength(18);
	  	this.disbAmount.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.disbAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.disbAmount.setScale(0);
	  	this.disbActDate.setFormat(PennantConstants.dateFormat);
		
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
		
		getUserWorkspace().alocateAuthorities("WIFFinanceDisbursementDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceDisbursementDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceDisbursementDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceDisbursementDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceDisbursementDialog_btnSave"));
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
	public void onClose$window_WIFFinanceDisbursementDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_WIFFinanceDisbursementDialog);
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
			closeDialog(this.window_WIFFinanceDisbursementDialog, "WIFFinanceDisbursement");	
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
	 * @param aWIFFinanceDisbursement
	 *            WIFFinanceDisbursement
	 */
	public void doWriteBeanToComponents(FinanceDisbursement aWIFFinanceDisbursement) {
		logger.debug("Entering") ;
		this.finReference.setValue(aWIFFinanceDisbursement.getFinReference());
		this.disbDate.setValue(aWIFFinanceDisbursement.getDisbDate());
		this.disbSeq.setValue(aWIFFinanceDisbursement.getDisbSeq());
		this.disbDesc.setValue(aWIFFinanceDisbursement.getDisbDesc());
  		this.disbAmount.setValue(PennantAppUtil.formateAmount(aWIFFinanceDisbursement.getDisbAmount(),0));
		this.disbActDate.setValue(aWIFFinanceDisbursement.getDisbReqDate());
		this.disbDisbursed.setChecked(aWIFFinanceDisbursement.isDisbDisbursed());
		this.disbIsActive.setChecked(aWIFFinanceDisbursement.isDisbIsActive());
		this.disbRemarks.setValue(aWIFFinanceDisbursement.getDisbRemarks());
	
		this.recordStatus.setValue(aWIFFinanceDisbursement.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aWIFFinanceDisbursement
	 */
	public void doWriteComponentsToBean(FinanceDisbursement aWIFFinanceDisbursement) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aWIFFinanceDisbursement.setFinReference(this.finReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aWIFFinanceDisbursement.setDisbDate(this.disbDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aWIFFinanceDisbursement.setDisbSeq(this.disbSeq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aWIFFinanceDisbursement.setDisbDesc(this.disbDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.disbAmount.getValue()!=null){
			 	aWIFFinanceDisbursement.setDisbAmount(PennantAppUtil.unFormateAmount(this.disbAmount.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aWIFFinanceDisbursement.setDisbReqDate(this.disbActDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceDisbursement.setDisbDisbursed(this.disbDisbursed.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceDisbursement.setDisbIsActive(this.disbIsActive.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aWIFFinanceDisbursement.setDisbRemarks(this.disbRemarks.getValue());
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
		
		aWIFFinanceDisbursement.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceDisbursement
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDisbursement aFinanceDisbursement) throws InterruptedException {
		logger.debug("Entering") ;
		
	// if aFinanceDisbursement == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aFinanceDisbursement == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinanceDisbursement = getWIFFinanceDisbursementService().getNewWIFFinanceDisbursement();
			
			setWIFFinanceDisbursement(aFinanceDisbursement);
		} else {
			setWIFFinanceDisbursement(aFinanceDisbursement);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aFinanceDisbursement.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.disbDesc.focus();
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
			doWriteBeanToComponents(aFinanceDisbursement);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_WIFFinanceDisbursementDialog);
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
		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_disbDate = this.disbDate.getValue();
		this.oldVar_disbSeq = this.disbSeq.intValue();	
		this.oldVar_disbDesc = this.disbDesc.getValue();
		this.oldVar_disbAmount = this.disbAmount.getValue();
		this.oldVar_disbActDate = this.disbActDate.getValue();
		this.oldVar_disbDisbursed = this.disbDisbursed.isChecked();
		this.oldVar_disbIsActive = this.disbIsActive.isChecked();
		this.oldVar_disbRemarks = this.disbRemarks.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finReference.setValue(this.oldVar_finReference);
		this.disbDate.setValue(this.oldVar_disbDate);
		this.disbSeq.setValue(this.oldVar_disbSeq);
		this.disbDesc.setValue(this.oldVar_disbDesc);
	  	this.disbAmount.setValue(this.oldVar_disbAmount);
		this.disbActDate.setValue(this.oldVar_disbActDate);
		this.disbDisbursed.setChecked(this.oldVar_disbDisbursed);
		this.disbIsActive.setChecked(this.oldVar_disbIsActive);
		this.disbRemarks.setValue(this.oldVar_disbRemarks);
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
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.finReference.isReadonly()){
			this.finReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceDisbursementDialog_FinReference.value")}));
		}	
		if (!this.disbDate.isDisabled()){
			this.disbDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceDisbursementDialog_DisbDate.value")}));
		}
		if (!this.disbSeq.isReadonly()){
			this.disbSeq.setConstraint(new IntValidator(10,Labels.getLabel("label_WIFFinanceDisbursementDialog_DisbSeq.value")));
		}	
		if (!this.disbDesc.isReadonly()){
			this.disbDesc.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceDisbursementDialog_DisbDesc.value")}));
		}	
		if (!this.disbAmount.isReadonly()){
			this.disbAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceDisbursementDialog_DisbAmount.value")));
		}	
		if (!this.disbActDate.isDisabled()){
			this.disbActDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceDisbursementDialog_DisbActDate.value")}));
		}
		if (!this.disbRemarks.isReadonly()){
			this.disbRemarks.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceDisbursementDialog_DisbRemarks.value")}));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.disbDate.setConstraint("");
		this.disbSeq.setConstraint("");
		this.disbDesc.setConstraint("");
		this.disbAmount.setConstraint("");
		this.disbActDate.setConstraint("");
		this.disbRemarks.setConstraint("");
	logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a WIFFinanceDisbursement object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinanceDisbursement aFinanceDisbursement = new FinanceDisbursement();
		BeanUtils.copyProperties(getWIFFinanceDisbursement(), aFinanceDisbursement);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceDisbursement.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceDisbursement.getRecordType()).equals("")){
				aFinanceDisbursement.setVersion(aFinanceDisbursement.getVersion()+1);
				aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aFinanceDisbursement.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFinanceDisbursement,tranType)){
					refreshList();
					closeDialog(this.window_WIFFinanceDisbursementDialog, "WIFFinanceDisbursement"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new WIFFinanceDisbursement object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		final FinanceDisbursement aFinanceDisbursement = getWIFFinanceDisbursementService().getNewWIFFinanceDisbursement();
		aFinanceDisbursement.setNewRecord(true);
		setWIFFinanceDisbursement(aFinanceDisbursement);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.finReference.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getWIFFinanceDisbursement().isNewRecord()){
		  	this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
	 	this.disbDate.setDisabled(isReadOnly("WIFFinanceDisbursementDialog_disbDate"));
		this.disbSeq.setReadonly(isReadOnly("WIFFinanceDisbursementDialog_disbSeq"));
		this.disbDesc.setReadonly(isReadOnly("WIFFinanceDisbursementDialog_disbDesc"));
		this.disbAmount.setReadonly(isReadOnly("WIFFinanceDisbursementDialog_disbAmount"));
	 	this.disbActDate.setDisabled(isReadOnly("WIFFinanceDisbursementDialog_disbActDate"));
	 	this.disbDisbursed.setDisabled(isReadOnly("WIFFinanceDisbursementDialog_disbDisbursed"));
	 	this.disbIsActive.setDisabled(isReadOnly("WIFFinanceDisbursementDialog_disbIsActive"));
		this.disbRemarks.setReadonly(isReadOnly("WIFFinanceDisbursementDialog_disbRemarks"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.wIFFinanceDisbursement.isNewRecord()){
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
		this.finReference.setReadonly(true);
		this.disbDate.setDisabled(true);
		this.disbSeq.setReadonly(true);
		this.disbDesc.setReadonly(true);
		this.disbAmount.setReadonly(true);
		this.disbActDate.setDisabled(true);
		this.disbDisbursed.setDisabled(true);
		this.disbIsActive.setDisabled(true);
		this.disbRemarks.setReadonly(true);
		
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
		
		this.finReference.setValue("");
		this.disbDate.setText("");
		this.disbSeq.setText("");
		this.disbDesc.setValue("");
		this.disbAmount.setValue("");
		this.disbActDate.setText("");
		this.disbDisbursed.setChecked(false);
		this.disbIsActive.setChecked(false);
		this.disbRemarks.setValue("");
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceDisbursement aFinanceDisbursement = new FinanceDisbursement();
		BeanUtils.copyProperties(getWIFFinanceDisbursement(), aFinanceDisbursement);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the WIFFinanceDisbursement object with the components data
		doWriteComponentsToBean(aFinanceDisbursement);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aFinanceDisbursement.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceDisbursement.getRecordType()).equals("")){
				aFinanceDisbursement.setVersion(aFinanceDisbursement.getVersion()+1);
				if(isNew){
					aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceDisbursement.setNewRecord(true);
				}
			}
		}else{
			aFinanceDisbursement.setVersion(aFinanceDisbursement.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aFinanceDisbursement,tranType)){
				doWriteBeanToComponents(aFinanceDisbursement);
				refreshList();
				closeDialog(this.window_WIFFinanceDisbursementDialog, "WIFFinanceDisbursement");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(FinanceDisbursement aWIFFinanceDisbursement,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aWIFFinanceDisbursement.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aWIFFinanceDisbursement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aWIFFinanceDisbursement.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aWIFFinanceDisbursement.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aWIFFinanceDisbursement.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aWIFFinanceDisbursement);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aWIFFinanceDisbursement))) {
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

			aWIFFinanceDisbursement.setTaskId(taskId);
			aWIFFinanceDisbursement.setNextTaskId(nextTaskId);
			aWIFFinanceDisbursement.setRoleCode(getRole());
			aWIFFinanceDisbursement.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aWIFFinanceDisbursement, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aWIFFinanceDisbursement);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aWIFFinanceDisbursement, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aWIFFinanceDisbursement, tranType);
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
		
		FinanceDisbursement aWIFFinanceDisbursement = (FinanceDisbursement) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getWIFFinanceDisbursementService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getWIFFinanceDisbursementService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getWIFFinanceDisbursementService().doApprove(auditHeader);

						if(aWIFFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getWIFFinanceDisbursementService().doReject(auditHeader);
						if(aWIFFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_WIFFinanceDisbursementDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_WIFFinanceDisbursementDialog, auditHeader);
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

	public FinanceDisbursement getWIFFinanceDisbursement() {
		return this.wIFFinanceDisbursement;
	}

	public void setWIFFinanceDisbursement(FinanceDisbursement wIFFinanceDisbursement) {
		this.wIFFinanceDisbursement = wIFFinanceDisbursement;
	}

	public void setWIFFinanceDisbursementService(WIFFinanceDisbursementService wIFFinanceDisbursementService) {
		this.wIFFinanceDisbursementService = wIFFinanceDisbursementService;
	}

	public WIFFinanceDisbursementService getWIFFinanceDisbursementService() {
		return this.wIFFinanceDisbursementService;
	}

	public void setWIFFinanceDisbursementListCtrl(WIFFinanceDisbursementListCtrl wIFFinanceDisbursementListCtrl) {
		this.wIFFinanceDisbursementListCtrl = wIFFinanceDisbursementListCtrl;
	}

	public WIFFinanceDisbursementListCtrl getWIFFinanceDisbursementListCtrl() {
		return this.wIFFinanceDisbursementListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	private AuditHeader getAuditHeader(FinanceDisbursement aWIFFinanceDisbursement, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aWIFFinanceDisbursement.getBefImage(), aWIFFinanceDisbursement);   
		return new AuditHeader(aWIFFinanceDisbursement.getFinReference(),null,null,null,auditDetail,aWIFFinanceDisbursement.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_WIFFinanceDisbursementDialog, auditHeader);
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
		notes.setModuleName("WIFFinanceDisbursement");
		notes.setReference(getWIFFinanceDisbursement().getFinReference());
		notes.setVersion(getWIFFinanceDisbursement().getVersion());
		return notes;
	}
	
	private void doClearMessage() {
		logger.debug("Entering");
			this.finReference.setErrorMessage("");
			this.disbDate.setErrorMessage("");
			this.disbSeq.setErrorMessage("");
			this.disbDesc.setErrorMessage("");
			this.disbAmount.setErrorMessage("");
			this.disbActDate.setErrorMessage("");
			this.disbRemarks.setErrorMessage("");
	logger.debug("Leaving");
	}
	

private void refreshList(){
		final JdbcSearchObject<FinanceDisbursement> soWIFFinanceDisbursement = getWIFFinanceDisbursementListCtrl().getSearchObj();
		getWIFFinanceDisbursementListCtrl().pagingWIFFinanceDisbursementList.setActivePage(0);
		getWIFFinanceDisbursementListCtrl().getPagedListWrapper().setSearchObject(soWIFFinanceDisbursement);
		if(getWIFFinanceDisbursementListCtrl().listBoxWIFFinanceDisbursement!=null){
			getWIFFinanceDisbursementListCtrl().listBoxWIFFinanceDisbursement.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public FinanceDisbursement getPrvWIFFinanceDisbursement() {
		return prvWIFFinanceDisbursement;
	}
}
