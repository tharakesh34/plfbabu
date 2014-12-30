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
 * FileName    		:  CurrencyDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.currency;

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/Currency/currencyDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CurrencyDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2843265056714842214L;
	private final static Logger logger = Logger.getLogger(CurrencyDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CurrencyDialog; 		// autoWired

	protected Textbox 		ccyCode; 					// autoWired
	protected Textbox 		ccyNumber; 					// autoWired
	protected Textbox 		ccyDesc; 					// autoWired
	protected Textbox 		ccySwiftCode; 				// autoWired
   	protected Intbox 		ccyEditField; 				// autoWired
	protected Decimalbox 	ccyMinorCcyUnits; 			// autoWired
	protected ExtendedCombobox 		ccyDrRateBasisCode; 		// autoWired
	protected ExtendedCombobox 		ccyCrRateBasisCode; 		// autoWired
	protected Textbox 		ccyMinorCcyDesc; 			// autoWired
	protected Textbox 		ccySymbol; 					// autoWired
	protected Checkbox 		ccyIsIntRounding; 			// autoWired
	protected Decimalbox 	ccySpotRate; 				// autoWired
	protected Checkbox 		ccyIsReceprocal; 			// autoWired
	protected Decimalbox 	ccyUserRateBuy; 			// autoWired
	protected Decimalbox 	ccyUserRateSell; 			// autoWired
	protected Checkbox 		ccyIsMember; 				// autoWired
	protected Checkbox 		ccyIsGroup; 				// autoWired
	protected Checkbox 		ccyIsAlwForLoans; 			// autoWired
	protected Checkbox 		ccyIsAlwForDepo; 			// autoWired
	protected Checkbox 		ccyIsAlwForAc; 				// autoWired
	protected Checkbox 		ccyIsActive; 				// autoWired

	protected Label 		recordStatus; 				// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired Var's
	private Currency currency; // overHanded per parameter
	private transient CurrencyListCtrl currencyListCtrl; // overHanded per parameter

	// old value Var's for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  		oldVar_ccyCode;
	private transient String  		oldVar_ccyNumber;
	private transient String  		oldVar_ccyDesc;
	private transient String  		oldVar_ccySwiftCode;
	private transient int  			oldVar_ccyEditField;
	private transient BigDecimal  	oldVar_ccyMinorCcyUnits;
	private transient String  		oldVar_ccyDrRateBasisCode;
	private transient String  		oldVar_ccyCrRateBasisCode;
	private transient String  		oldVar_ccyMinorCcyDesc; 			
	private transient String  		oldVar_ccySymbol; 					
	private transient boolean  		oldVar_ccyIsIntRounding;
	private transient BigDecimal  	oldVar_ccySpotRate;
	private transient boolean  		oldVar_ccyIsReceprocal;
	private transient BigDecimal  	oldVar_ccyUserRateBuy;
	private transient BigDecimal  	oldVar_ccyUserRateSell;
	private transient boolean  		oldVar_ccyIsMember;
	private transient boolean  		oldVar_ccyIsGroup;
	private transient boolean  		oldVar_ccyIsAlwForLoans;
	private transient boolean  		oldVar_ccyIsAlwForDepo;
	private transient boolean  		oldVar_ccyIsAlwForAc;
	private transient boolean  		oldVar_ccyIsActive;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CurrencyDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire
	
	private transient String 	oldVar_lovDescCcyDrRateBasisCodeName;
	private transient String 	oldVar_lovDescCcyCrRateBasisCodeName;
	
	// ServiceDAOs / Domain Classes
	private transient CurrencyService currencyService;
	private transient PagedListService pagedListService;
	
	/**
	 * default constructor.<br>
	 */
	public CurrencyDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Currency object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CurrencyDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		
		// READ OVERHANDED parameters !
		if (args.containsKey("currency")) {
			this.currency = (Currency) args.get("currency");
			Currency befImage =new Currency();
			BeanUtils.copyProperties(this.currency, befImage);
			this.currency.setBefImage(befImage);
			
			setCurrency(this.currency);
		} else {
			setCurrency(null);
		}
	
		doLoadWorkFlow(this.currency.isWorkflow(),this.currency.getWorkflowId(),this.currency.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CurrencyDialog");
		}

	
		// READ OVERHANDED parameters !
		// we get the currencyListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete currency here.
		if (args.containsKey("currencyListCtrl")) {
			setCurrencyListCtrl((CurrencyListCtrl) args.get("currencyListCtrl"));
		} else {
			setCurrencyListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCurrency());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		//Empty sent any required attributes
		this.ccyCode.setMaxlength(3);
		this.ccyNumber.setMaxlength(3);
		this.ccyDesc.setMaxlength(50);
		this.ccySwiftCode.setMaxlength(3);
		this.ccyEditField.setMaxlength(1);
	  	this.ccyMinorCcyUnits.setMaxlength(5);
	  	this.ccySymbol.setMaxlength(3);
	  	this.ccyMinorCcyDesc.setMaxlength(50);
	  	this.ccyMinorCcyUnits.setFormat(PennantConstants.defaultNoFormate);
	  	this.ccyMinorCcyUnits.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.ccyMinorCcyUnits.setScale(0);
		this.ccyDrRateBasisCode.setMaxlength(8);
		this.ccyCrRateBasisCode.setMaxlength(8);
	  	this.ccySpotRate.setMaxlength(15);
	  	this.ccySpotRate.setFormat(PennantConstants.rateFormate9);
	  	this.ccySpotRate.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.ccySpotRate.setScale(9);
	  	this.ccyUserRateBuy.setMaxlength(15);
	  	this.ccyUserRateBuy.setFormat(PennantConstants.rateFormate9);
	  	this.ccyUserRateBuy.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.ccyUserRateBuy.setScale(9);
	  	this.ccyUserRateSell.setMaxlength(15);
	  	this.ccyUserRateSell.setFormat(PennantConstants.rateFormate9);
	  	this.ccyUserRateSell.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.ccyUserRateSell.setScale(9);
	  	
	  	this.ccyDrRateBasisCode.setModuleName("InterestRateBasisCode");
	  	this.ccyDrRateBasisCode.setMandatoryStyle(true);
	  	this.ccyDrRateBasisCode.setValueColumn("IntRateBasisCode");
	  	this.ccyDrRateBasisCode.setDescColumn("IntRateBasisDesc");
	  	this.ccyDrRateBasisCode.setValidateColumns(new String[]{"IntRateBasisCode"});
	  	
	  	this.ccyCrRateBasisCode.setMandatoryStyle(true);
	  	this.ccyCrRateBasisCode.setModuleName("InterestRateBasisCode");
	  	this.ccyCrRateBasisCode.setValueColumn("IntRateBasisCode");
	  	this.ccyCrRateBasisCode.setDescColumn("IntRateBasisDesc");
	  	this.ccyCrRateBasisCode.setValidateColumns(new String[]{"IntRateBasisCode"});
	  	
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("CurrencyDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CurrencyDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CurrencyDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CurrencyDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CurrencyDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
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
	public void onClose$window_CurrencyDialog(Event event) throws Exception {
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
		// remember the old Var's
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
		PTMessageUtils.showHelpWindow(event, window_CurrencyDialog);
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
			// close anyway
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
   public void onFulfill$ccyDrRateBasisCode(Event event){
	   logger.debug("Entering" + event.toString());

	   Object dataObject = ccyDrRateBasisCode.getObject(); 
	   if (dataObject instanceof String){
		   this.ccyDrRateBasisCode.setValue(dataObject.toString());
		   this.ccyDrRateBasisCode.setDescription("");
	   }else{
		   InterestRateBasisCode details= (InterestRateBasisCode) dataObject;
			if (details != null) {
				this.ccyDrRateBasisCode.setValue(details.getIntRateBasisCode());
				this.ccyDrRateBasisCode.setDescription(details.getIntRateBasisDesc());
			}
	   }
	   logger.debug("Leaving" + event.toString());
	}
   
   public void onClick$btnSearchCcyCrRateBasisCode(Event event){
	   logger.debug("Entering" + event.toString());
	   
	   Object dataObject =ccyCrRateBasisCode.getObject();
	   if (dataObject instanceof String){
		   this.ccyCrRateBasisCode.setValue(dataObject.toString());
		   this.ccyCrRateBasisCode.setDescription("");
	   }else{
		   InterestRateBasisCode details= (InterestRateBasisCode) dataObject;
			if (details != null) {
				this.ccyCrRateBasisCode.setValue(details.getIntRateBasisCode());
				this.ccyCrRateBasisCode.setDescription(details.getIntRateBasisDesc());
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
		logger.debug("Entering ");
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
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		
		if(close){
			closeDialog(this.window_CurrencyDialog, "Currency");
		}	
		logger.debug("Leaving ");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCurrency
	 *            Currency
	 */
	public void doWriteBeanToComponents(Currency aCurrency) {
		logger.debug("Entering ");
		this.ccyCode.setValue(aCurrency.getCcyCode());
		this.ccyNumber.setValue(aCurrency.getCcyNumber());
		this.ccyDesc.setValue(aCurrency.getCcyDesc());
		this.ccySwiftCode.setValue(aCurrency.getCcySwiftCode());
		this.ccyEditField.setValue(aCurrency.getCcyEditField());
	  	this.ccyMinorCcyUnits.setValue(PennantAppUtil.formateAmount(
	  			aCurrency.getCcyMinorCcyUnits(),0));
	    this.ccyDrRateBasisCode.setValue(aCurrency.getCcyDrRateBasisCode());
	    this.ccyCrRateBasisCode.setValue(aCurrency.getCcyCrRateBasisCode());
	    this.ccySymbol.setValue(aCurrency.getCcySymbol());
	    this.ccyMinorCcyDesc.setValue(aCurrency.getCcyMinorCcyDesc());
		this.ccyIsIntRounding.setChecked(aCurrency.isCcyIsIntRounding());
	  	this.ccySpotRate.setValue(aCurrency.getCcySpotRate() == null ? BigDecimal.ZERO : aCurrency.getCcySpotRate());
		this.ccyIsReceprocal.setChecked(aCurrency.isCcyIsReceprocal());
	  	this.ccyUserRateBuy.setValue(aCurrency.getCcyUserRateBuy() == null ? BigDecimal.ZERO : aCurrency.getCcyUserRateBuy());
	  	this.ccyUserRateSell.setValue(aCurrency.getCcyUserRateSell() == null ? BigDecimal.ZERO : aCurrency.getCcyUserRateSell());
		this.ccyIsMember.setChecked(aCurrency.isCcyIsMember());
		this.ccyIsGroup.setChecked(aCurrency.isCcyIsGroup());
		this.ccyIsAlwForLoans.setChecked(aCurrency.isCcyIsAlwForLoans());
		this.ccyIsAlwForDepo.setChecked(aCurrency.isCcyIsAlwForDepo());
		this.ccyIsAlwForAc.setChecked(aCurrency.isCcyIsAlwForAc());
		this.ccyIsActive.setChecked(aCurrency.isCcyIsActive());
	
	if (aCurrency.isNewRecord()){
		   this.ccyDrRateBasisCode.setDescription("");
		   this.ccyCrRateBasisCode.setDescription("");
	}else{
		   this.ccyDrRateBasisCode.setDescription(aCurrency.getLovDescCcyDrRateBasisCodeName());
		   this.ccyCrRateBasisCode.setDescription(aCurrency.getLovDescCcyCrRateBasisCodeName());
	}
		this.recordStatus.setValue(aCurrency.getRecordStatus());
		
		if(aCurrency.isNew() || (aCurrency.getRecordType() != null ? aCurrency.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.ccyIsActive.setChecked(true);
			this.ccyIsActive.setDisabled(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCurrency
	 */
	public void doWriteComponentsToBean(Currency aCurrency) {
		logger.debug("Entering ");
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aCurrency.setCcyCode(this.ccyCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCurrency.setCcyNumber(this.ccyNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCurrency.setCcyDesc(this.ccyDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCurrency.setCcySwiftCode(this.ccySwiftCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCurrency.setCcyEditField(this.ccyEditField.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if((!this.ccyMinorCcyUnits.isReadonly()) && (this.ccyMinorCcyUnits.getValue() != null) &&
					!(this.ccyMinorCcyUnits.getValue().intValue() ==0 || this.ccyMinorCcyUnits.getValue().intValue() ==10 || 
					this.ccyMinorCcyUnits.getValue().intValue() ==100 || this.ccyMinorCcyUnits.getValue().intValue() ==1000
					|| this.ccyMinorCcyUnits.getValue().intValue() ==10000)){
				
				throw new WrongValueException(ccyMinorCcyUnits, Labels.getLabel("FIELD_MINORCCYUNITS",
						new String[]{Labels.getLabel("label_CurrencyDialog_CcyMinorCcyUnits.value")}));
			}
			aCurrency.setCcyMinorCcyUnits(PennantAppUtil.formateAmount(this.ccyMinorCcyUnits.getValue(),0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCurrency.setLovDescCcyDrRateBasisCodeName(this.ccyDrRateBasisCode.getDescription());
	 		aCurrency.setCcyDrRateBasisCode(this.ccyDrRateBasisCode.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aCurrency.setLovDescCcyCrRateBasisCodeName(this.ccyCrRateBasisCode.getDescription());
	 		aCurrency.setCcyCrRateBasisCode(this.ccyCrRateBasisCode.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCurrency.setCcySymbol(this.ccySymbol.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aCurrency.setCcyMinorCcyDesc(this.ccyMinorCcyDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCurrency.setCcyIsIntRounding(this.ccyIsIntRounding.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (!this.ccySpotRate.isReadonly() && this.ccySpotRate.getValue() == null || this.ccySpotRate.getValue().doubleValue() == 0) {
				throw new WrongValueException(ccySpotRate, Labels.getLabel("const_NO_ZERO",
						new String[] { Labels.getLabel("label_CurrencyDialog_CcySpotRate.value") }));
			}
			aCurrency.setCcySpotRate(this.ccySpotRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCurrency.setCcyIsReceprocal(this.ccyIsReceprocal.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (!this.ccyUserRateBuy.isReadonly() && this.ccyUserRateBuy.getValue() == null || this.ccyUserRateBuy.getValue().doubleValue() == 0) {
				throw new WrongValueException(ccyUserRateBuy, Labels.getLabel("const_NO_ZERO",
						new String[] { Labels.getLabel("label_CurrencyDialog_CcyUserRateBuy.value") }));
			}
			aCurrency.setCcyUserRateBuy(this.ccyUserRateBuy.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (!this.ccyUserRateSell.isReadonly() && this.ccyUserRateSell.getValue() == null || this.ccyUserRateSell.getValue().doubleValue() == 0) {
				throw new WrongValueException(ccyUserRateSell, Labels.getLabel("const_NO_ZERO",
						new String[] { Labels.getLabel("label_CurrencyDialog_CcyUserRateSell.value") }));
			}
			aCurrency.setCcyUserRateSell(this.ccyUserRateSell.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCurrency.setCcyIsMember(this.ccyIsMember.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCurrency.setCcyIsGroup(this.ccyIsGroup.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCurrency.setCcyIsAlwForLoans(this.ccyIsAlwForLoans.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCurrency.setCcyIsAlwForDepo(this.ccyIsAlwForDepo.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCurrency.setCcyIsAlwForAc(this.ccyIsAlwForAc.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCurrency.setCcyIsActive(this.ccyIsActive.isChecked());
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
		
		aCurrency.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCurrency
	 * @throws InterruptedException
	 */
	public void doShowDialog(Currency aCurrency) throws InterruptedException {
		logger.debug("Entering ");
		// if aCurrency == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCurrency == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCurrency = getCurrencyService().getNewCurrency();
			
			setCurrency(aCurrency);
		} else {
			setCurrency(aCurrency);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCurrency.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ccyCode.focus();
		} else {
			this.ccyNumber.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aCurrency.getRecordType()).equals("")){
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
			doWriteBeanToComponents(aCurrency);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CurrencyDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member Var's. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_ccyCode = this.ccyCode.getValue();
		this.oldVar_ccyNumber = this.ccyNumber.getValue();
		this.oldVar_ccyDesc = this.ccyDesc.getValue();
		this.oldVar_ccySwiftCode = this.ccySwiftCode.getValue();
		this.oldVar_ccyEditField = this.ccyEditField.intValue();	
		this.oldVar_ccyMinorCcyUnits = this.ccyMinorCcyUnits.getValue();
 		this.oldVar_ccyDrRateBasisCode = this.ccyDrRateBasisCode.getValue();
 		this.oldVar_lovDescCcyDrRateBasisCodeName = this.ccyDrRateBasisCode.getDescription();
 		this.oldVar_ccyCrRateBasisCode = this.ccyCrRateBasisCode.getValue();
 		this.oldVar_lovDescCcyCrRateBasisCodeName = this.ccyCrRateBasisCode.getDescription();
 		this.oldVar_ccySymbol = this.ccySymbol.getValue();
 		this.oldVar_ccyMinorCcyDesc = this.ccyMinorCcyDesc.getValue();
		this.oldVar_ccyIsIntRounding = this.ccyIsIntRounding.isChecked();
		this.oldVar_ccySpotRate = this.ccySpotRate.getValue();
		this.oldVar_ccyIsReceprocal = this.ccyIsReceprocal.isChecked();
		this.oldVar_ccyUserRateBuy = this.ccyUserRateBuy.getValue();
		this.oldVar_ccyUserRateSell = this.ccyUserRateSell.getValue();
		this.oldVar_ccyIsMember = this.ccyIsMember.isChecked();
		this.oldVar_ccyIsGroup = this.ccyIsGroup.isChecked();
		this.oldVar_ccyIsAlwForLoans = this.ccyIsAlwForLoans.isChecked();
		this.oldVar_ccyIsAlwForDepo = this.ccyIsAlwForDepo.isChecked();
		this.oldVar_ccyIsAlwForAc = this.ccyIsAlwForAc.isChecked();
		this.oldVar_ccyIsActive = this.ccyIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the initial values from member Var's. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.ccyCode.setValue(this.oldVar_ccyCode);
		this.ccyNumber.setValue(this.oldVar_ccyNumber);
		this.ccyDesc.setValue(this.oldVar_ccyDesc);
		this.ccySwiftCode.setValue(this.oldVar_ccySwiftCode);
		this.ccyEditField.setValue(this.oldVar_ccyEditField);
		this.ccyMinorCcyUnits.setValue(this.oldVar_ccyMinorCcyUnits);
 		this.ccyDrRateBasisCode.setValue(this.oldVar_ccyDrRateBasisCode);
 		this.ccyDrRateBasisCode.setDescription(this.oldVar_lovDescCcyDrRateBasisCodeName);
 		this.ccyCrRateBasisCode.setValue(this.oldVar_ccyCrRateBasisCode);
 		this.ccyCrRateBasisCode.setDescription(this.oldVar_lovDescCcyCrRateBasisCodeName);
 		this.ccySymbol.setValue(this.oldVar_ccySymbol);
 		this.ccyMinorCcyDesc.setValue(this.oldVar_ccyMinorCcyDesc);
		this.ccyIsIntRounding.setChecked(this.oldVar_ccyIsIntRounding);
	  	this.ccySpotRate.setValue(this.oldVar_ccySpotRate);
		this.ccyIsReceprocal.setChecked(this.oldVar_ccyIsReceprocal);
	  	this.ccyUserRateBuy.setValue(this.oldVar_ccyUserRateBuy);
	  	this.ccyUserRateSell.setValue(this.oldVar_ccyUserRateSell);
		this.ccyIsMember.setChecked(this.oldVar_ccyIsMember);
		this.ccyIsGroup.setChecked(this.oldVar_ccyIsGroup);
		this.ccyIsAlwForLoans.setChecked(this.oldVar_ccyIsAlwForLoans);
		this.ccyIsAlwForDepo.setChecked(this.oldVar_ccyIsAlwForDepo);
		this.ccyIsAlwForAc.setChecked(this.oldVar_ccyIsAlwForAc);
		this.ccyIsActive.setChecked(this.oldVar_ccyIsActive);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		
		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving ");
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
		
		if (this.oldVar_ccyCode != this.ccyCode.getValue()) {
			return true;
		}
		if (this.oldVar_ccyNumber != this.ccyNumber.getValue()) {
			return true;
		}
		if (this.oldVar_ccyDesc != this.ccyDesc.getValue()) {
			return true;
		}
		if (this.oldVar_ccySwiftCode != this.ccySwiftCode.getValue()) {
			return true;
		}
		if (this.oldVar_ccyEditField != this.ccyEditField.intValue()) {
			return true;
		}
		if (this.oldVar_ccyMinorCcyUnits != this.ccyMinorCcyUnits.getValue()) {
			return true;
		}
		if (this.oldVar_ccyDrRateBasisCode != this.ccyDrRateBasisCode.getValue()) {
			return true;
		}
		if (this.oldVar_ccyCrRateBasisCode != this.ccyCrRateBasisCode.getValue()) {
			return true;
		}
		if (this.oldVar_ccySymbol != this.ccySymbol.getValue()) {
			return true;
		}
		if (this.oldVar_ccyMinorCcyDesc != this.ccyMinorCcyDesc.getValue()) {
			return true;
		}
		if (this.oldVar_ccyIsIntRounding != this.ccyIsIntRounding.isChecked()) {
			return true;
		}
		if (this.oldVar_ccySpotRate != this.ccySpotRate.getValue()) {
			return true;
		}
		if (this.oldVar_ccyIsReceprocal != this.ccyIsReceprocal.isChecked()) {
			return true;
		}
		if (this.oldVar_ccyUserRateBuy != this.ccyUserRateBuy.getValue()) {
			return true;
		}
		if (this.oldVar_ccyUserRateSell != this.ccyUserRateSell.getValue()) {
			return true;
		}
		if (this.oldVar_ccyIsMember != this.ccyIsMember.isChecked()) {
			return true;
		}
		if (this.oldVar_ccyIsGroup != this.ccyIsGroup.isChecked()) {
			return true;
		}
		if (this.oldVar_ccyIsAlwForLoans != this.ccyIsAlwForLoans.isChecked()) {
			return true;
		}
		if (this.oldVar_ccyIsAlwForDepo != this.ccyIsAlwForDepo.isChecked()) {
			return true;
		}
		if (this.oldVar_ccyIsAlwForAc != this.ccyIsAlwForAc.isChecked()) {
			return true;
		}
		if (this.oldVar_ccyIsActive != this.ccyIsActive.isChecked()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);
		
		if (!this.ccyCode.isReadonly()){
			this.ccyCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcyCode.value"),PennantRegularExpressions.REGEX_ALPHA_FL3, true));
		}	
		if (!this.ccyNumber.isReadonly()){
			this.ccyNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcyNumber.value"),PennantRegularExpressions.REGEX_NUMERIC_FL3, true));
		}	
		if (!this.ccyDesc.isReadonly()){
			this.ccyDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcyDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}	
		if (!this.ccySwiftCode.isReadonly()){
			this.ccySwiftCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcySwiftCode.value"),PennantRegularExpressions.REGEX_ALPHANUM_FL3, true));
		}	
		if (!this.ccyEditField.isReadonly()){
			if(this.ccyEditField.getValue() !=0){
				this.ccyEditField.setConstraint(new IntValidator(1,
						Labels.getLabel("label_CurrencyDialog_CcyEditField.value")));
			}
		}	
		// TODO-- Regextion is not working here, apply mandatory
		/*if (!this.ccyMinorCcyUnits.isReadonly()){
			this.ccyMinorCcyUnits.setConstraint(new SimpleConstraint(PennantConstants.MINORCCYUNITS_REGEX,
					Labels.getLabel("FIELD_MINORCCYUNITS",new String[]{Labels.getLabel(
					"label_CurrencyDialog_CcyMinorCcyUnits.value")})));
		}	*/
		if (!this.ccySymbol.isReadonly()){
			this.ccySymbol.setConstraint(new PTStringValidator(Labels
						.getLabel("label_CurrencyDialog_CcySymbol.value"), null, true));
		}	
		if (!this.ccyMinorCcyDesc.isReadonly()){
			this.ccyMinorCcyDesc.setConstraint(new PTStringValidator(Labels
						.getLabel("label_CurrencyDialog_CcyMinorCcyDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}	
		if (!this.ccySpotRate.isReadonly()){
			this.ccySpotRate.setConstraint(new RateValidator(15,9,
					Labels.getLabel("label_CurrencyDialog_CcySpotRate.value"),false));
		}
		if (!this.ccyUserRateBuy.isReadonly()){
			this.ccyUserRateBuy.setConstraint(new RateValidator(15,9,
					Labels.getLabel("label_CurrencyDialog_CcyUserRateBuy.value"),false));
		}
		
		if (!this.ccyUserRateSell.isReadonly()){
			this.ccyUserRateSell.setConstraint(new RateValidator(15,9,
					Labels.getLabel("label_CurrencyDialog_CcyUserRateSell.value"),false));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.ccyCode.setConstraint("");
		this.ccyNumber.setConstraint("");
		this.ccyDesc.setConstraint("");
		this.ccySwiftCode.setConstraint("");
		this.ccyEditField.setConstraint("");
		this.ccyMinorCcyUnits.setConstraint("");
		this.ccySymbol.setConstraint("");
		this.ccyMinorCcyDesc.setConstraint("");
		this.ccySpotRate.setConstraint("");
		this.ccyUserRateBuy.setConstraint("");
		this.ccyUserRateSell.setConstraint("");
		logger.debug("Leaving ");
	}
	
	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.ccyDrRateBasisCode.setConstraint(new PTStringValidator(Labels.getLabel(
						"label_CurrencyDialog_CcyDrRateBasisCode.value"), null, true));
		this.ccyCrRateBasisCode.setConstraint(new PTStringValidator(Labels.getLabel(
						"label_CurrencyDialog_CcyCrRateBasisCode.value"), null, true));
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.ccyDrRateBasisCode.setConstraint("");
		this.ccyCrRateBasisCode.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Enterring");
		this.ccyCode.setErrorMessage("");
		this.ccyNumber.setErrorMessage("");
		this.ccyDesc.setErrorMessage("");
		this.ccySwiftCode.setErrorMessage("");
		this.ccyEditField.setErrorMessage("");
		this.ccyMinorCcyUnits.setErrorMessage("");
		this.ccySymbol.setErrorMessage("");
		this.ccyMinorCcyDesc.setErrorMessage("");
		this.ccySpotRate.setErrorMessage("");
		this.ccyUserRateBuy.setErrorMessage("");
		this.ccyUserRateSell.setErrorMessage("");
		this.ccyDrRateBasisCode.setErrorMessage("");
		this.ccyCrRateBasisCode.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<Currency> soObject = getCurrencyListCtrl().getSearchObj();
		getCurrencyListCtrl().pagingCurrencyList.setActivePage(0);
		getCurrencyListCtrl().getPagedListWrapper().setSearchObject(soObject);
		if(getCurrencyListCtrl().listBoxCurrency!=null){
			getCurrencyListCtrl().listBoxCurrency.getListModel();
		}
		logger.debug("Leaving");
	} 
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Currency object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final Currency aCurrency = new Currency();
		BeanUtils.copyProperties(getCurrency(), aCurrency);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel(
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " 
				+ aCurrency.getCcyCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCurrency.getRecordType()).equals("")){
				aCurrency.setVersion(aCurrency.getVersion()+1);
				aCurrency.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aCurrency.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCurrency,tranType)){
					refreshList();
					closeDialog(this.window_CurrencyDialog, "Currency"); 
				}

			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving ");
	}

	/**
	 * Create a new Currency object. <br>
	 */
	private void doNew() {
		logger.debug("Entering ");
		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new Currency() in the frontEnd.
		// we get it from the backEnd.
		final Currency aCurrency = getCurrencyService().getNewCurrency();
		aCurrency.setNewRecord(true);
		setCurrency(aCurrency);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.ccyCode.focus();
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		if (getCurrency().isNewRecord()){
		  	this.ccyCode.setReadonly(false);
		  	this.ccyNumber.setDisabled(false);
		  	this.ccySwiftCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.ccyCode.setReadonly(true);
			this.ccyNumber.setDisabled(true);
			this.ccySwiftCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
		this.ccyDesc.setReadonly(isReadOnly("CurrencyDialog_ccyDesc"));
		this.ccyEditField.setReadonly(isReadOnly("CurrencyDialog_ccyEditField"));
		this.ccyMinorCcyUnits.setReadonly(isReadOnly("CurrencyDialog_ccyMinorCcyUnits"));
	  	this.ccyDrRateBasisCode.setReadonly(isReadOnly("CurrencyDialog_ccyDrRateBasisCode"));
	  	this.ccyCrRateBasisCode.setReadonly(isReadOnly("CurrencyDialog_ccyCrRateBasisCode"));
	  	this.ccySymbol.setReadonly(isReadOnly("CurrencyDialog_ccySymbol"));
	  	this.ccyMinorCcyDesc.setReadonly(isReadOnly("CurrencyDialog_ccyMinorCcyDesc"));
	 	this.ccyIsIntRounding.setDisabled(isReadOnly("CurrencyDialog_ccyIsIntRounding"));
		this.ccySpotRate.setReadonly(isReadOnly("CurrencyDialog_ccySpotRate"));
	 	this.ccyIsReceprocal.setDisabled(isReadOnly("CurrencyDialog_ccyIsReceprocal"));
		this.ccyUserRateBuy.setReadonly(isReadOnly("CurrencyDialog_ccyUserRateBuy"));
		this.ccyUserRateSell.setReadonly(isReadOnly("CurrencyDialog_ccyUserRateSell"));
	 	this.ccyIsMember.setDisabled(isReadOnly("CurrencyDialog_ccyIsMember"));
	 	this.ccyIsGroup.setDisabled(isReadOnly("CurrencyDialog_ccyIsGroup"));
	 	this.ccyIsAlwForLoans.setDisabled(isReadOnly("CurrencyDialog_ccyIsAlwForLoans"));
	 	this.ccyIsAlwForDepo.setDisabled(isReadOnly("CurrencyDialog_ccyIsAlwForDepo"));
	 	this.ccyIsAlwForAc.setDisabled(isReadOnly("CurrencyDialog_ccyIsAlwForAc"));
	 	this.ccyIsActive.setDisabled(isReadOnly("CurrencyDialog_ccyIsActive"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.currency.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.ccyCode.setReadonly(true);
		this.ccyNumber.setReadonly(true);
		this.ccyDesc.setReadonly(true);
		this.ccySwiftCode.setReadonly(true);
		this.ccyEditField.setReadonly(true);
		this.ccyMinorCcyUnits.setReadonly(true);
		this.ccyDrRateBasisCode.setReadonly(true);
		this.ccyCrRateBasisCode.setReadonly(true);
	  	this.ccySymbol.setReadonly(true);
	  	this.ccyMinorCcyDesc.setReadonly(true);
		this.ccyIsIntRounding.setDisabled(true);
		this.ccySpotRate.setReadonly(true);
		this.ccyIsReceprocal.setDisabled(true);
		this.ccyUserRateBuy.setReadonly(true);
		this.ccyUserRateSell.setReadonly(true);
		this.ccyIsMember.setDisabled(true);
		this.ccyIsGroup.setDisabled(true);
		this.ccyIsAlwForLoans.setDisabled(true);
		this.ccyIsAlwForDepo.setDisabled(true);
		this.ccyIsAlwForAc.setDisabled(true);
		this.ccyIsActive.setDisabled(true);
		
		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		
		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before
		
		this.ccyCode.setValue("");
		this.ccyNumber.setValue("");
		this.ccyDesc.setValue("");
		this.ccySwiftCode.setValue("");
		this.ccyEditField.setText("");
		this.ccyMinorCcyUnits.setValue("");
	  	this.ccyDrRateBasisCode.setValue("");
		this.ccyDrRateBasisCode.setDescription("");
	  	this.ccyCrRateBasisCode.setValue("");
		this.ccyCrRateBasisCode.setDescription("");
	  	this.ccySymbol.setValue("");
	  	this.ccyMinorCcyDesc.setValue("");
		this.ccyIsIntRounding.setChecked(false);
		this.ccySpotRate.setValue("");
		this.ccyIsReceprocal.setChecked(false);
		this.ccyUserRateBuy.setValue("");
		this.ccyUserRateSell.setValue("");
		this.ccyIsMember.setChecked(false);
		this.ccyIsGroup.setChecked(false);
		this.ccyIsAlwForLoans.setChecked(false);
		this.ccyIsAlwForDepo.setChecked(false);
		this.ccyIsAlwForAc.setChecked(false);
		this.ccyIsActive.setChecked(false);
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final Currency aCurrency = new Currency();
		BeanUtils.copyProperties(getCurrency(), aCurrency);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Currency object with the components data
		doWriteComponentsToBean(aCurrency);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aCurrency.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCurrency.getRecordType()).equals("")){
				aCurrency.setVersion(aCurrency.getVersion()+1);
				if(isNew){
					aCurrency.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCurrency.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCurrency.setNewRecord(true);
				}
			}
		}else{
			aCurrency.setVersion(aCurrency.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aCurrency,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CurrencyDialog, "Currency");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving ");
	}
	
	/**	
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCurrency (Currency)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Currency aCurrency,String tranType){
		logger.debug("Entering ");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aCurrency.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCurrency.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCurrency.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCurrency.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCurrency.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCurrency);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aCurrency))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
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

			aCurrency.setTaskId(taskId);
			aCurrency.setNextTaskId(nextTaskId);
			aCurrency.setRoleCode(getRole());
			aCurrency.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aCurrency, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCurrency);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCurrency, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCurrency, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		Currency aCurrency = (Currency) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {
			while(retValue==PennantConstants.porcessOVERIDE){
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCurrencyService().delete(auditHeader);

						deleteNotes=true;	
					}else{
						auditHeader = getCurrencyService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)){
						auditHeader = getCurrencyService().doApprove(auditHeader);

						if(aCurrency.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;	
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getCurrencyService().doReject(auditHeader);
						if(aCurrency.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_CurrencyDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(this.window_CurrencyDialog, auditHeader);
				
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
		logger.debug("Leaving ");
		return processCompleted;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 * @param aCurrency 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Currency aCurrency, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCurrency.getBefImage(), aCurrency);   
		return new AuditHeader(String.valueOf(aCurrency.getId()),null,null,null,auditDetail,aCurrency.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CurrencyDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
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
		logger.debug("Entering" + event.toString());
		
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
		logger.debug("Leaving" + event.toString());
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
		notes.setModuleName("Currency");
		notes.setReference(getCurrency().getCcyCode());
		notes.setVersion(getCurrency().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public Currency getCurrency() {
		return this.currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}
	public CurrencyService getCurrencyService() {
		return this.currencyService;
	}

	public void setCurrencyListCtrl(CurrencyListCtrl currencyListCtrl) {
		this.currencyListCtrl = currencyListCtrl;
	}
	public CurrencyListCtrl getCurrencyListCtrl() {
		return this.currencyListCtrl;
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
