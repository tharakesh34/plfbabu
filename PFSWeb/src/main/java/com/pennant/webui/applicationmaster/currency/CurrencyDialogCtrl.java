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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Currency/currencyDialog.zul file.
 */
public class CurrencyDialogCtrl extends GFCBaseCtrl<Currency> {
	private static final long serialVersionUID = -2843265056714842214L;
	private static final Logger logger = Logger.getLogger(CurrencyDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
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
	// not auto wired Var's
	private Currency currency; // overHanded per parameter
	private transient CurrencyListCtrl currencyListCtrl; // overHanded per parameter


	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient CurrencyService currencyService;
	
	/**
	 * default constructor.<br>
	 */
	public CurrencyDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CurrencyDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Currency object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CurrencyDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CurrencyDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("currency")) {
				this.currency = (Currency) arguments.get("currency");
				Currency befImage = new Currency();
				BeanUtils.copyProperties(this.currency, befImage);
				this.currency.setBefImage(befImage);

				setCurrency(this.currency);
			} else {
				setCurrency(null);
			}

			doLoadWorkFlow(this.currency.isWorkflow(),
					this.currency.getWorkflowId(),
					this.currency.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CurrencyDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the currencyListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete currency here.
			if (arguments.containsKey("currencyListCtrl")) {
				setCurrencyListCtrl((CurrencyListCtrl) arguments
						.get("currencyListCtrl"));
			} else {
				setCurrencyListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCurrency());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CurrencyDialog.onClose();
		}
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
	  	this.ccyMinorCcyUnits.setFormat(PennantConstants.defaultNoLimiterFormate);
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
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CurrencyDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CurrencyDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CurrencyDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CurrencyDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
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
		MessageUtil.showHelpWindow(event, window_CurrencyDialog);
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
		logger.debug("Entering ");
		doWriteBeanToComponents(this.currency.getBefImage());
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
	 * @throws Exception
	 */
	public void doShowDialog(Currency aCurrency) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCurrency.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ccyCode.focus();
		} else {
			this.ccyNumber.focus();
			if (isWorkFlowEnabled()){
				if (StringUtils.isNotBlank(aCurrency.getRecordType())){
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

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CurrencyDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);
		
		if (!this.ccyCode.isReadonly()){
			this.ccyCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcyCode.value"),PennantRegularExpressions.REGEX_UPPBOX_ALPHA_FL3, true));
		}	
		if (!this.ccyNumber.isReadonly()){
			this.ccyNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcyNumber.value"),PennantRegularExpressions.REGEX_NUMERIC_FL3, true));
		}	
		if (!this.ccyDesc.isReadonly()){
			this.ccyDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcyDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}	
		if (!this.ccySwiftCode.isReadonly()){
			this.ccySwiftCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcySwiftCode.value"),PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_FL3, true));
		}	
		if (!this.ccyEditField.isReadonly()){
			if(this.ccyEditField.getValue() !=0){
				this.ccyEditField.setConstraint(new PTNumberValidator(Labels.getLabel("label_CurrencyDialog_CcyEditField.value"), true));
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
			this.ccySpotRate.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_CurrencyDialog_CcySpotRate.value"),9,true,false,9999));
		}
		if (!this.ccyUserRateBuy.isReadonly()){
			this.ccyUserRateBuy.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_CurrencyDialog_CcyUserRateBuy.value"),9,true,false,9999));
		}
		
		if (!this.ccyUserRateSell.isReadonly()){
			this.ccyUserRateSell.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_CurrencyDialog_CcyUserRateSell.value"),9,true,false,9999));
		}
		if (!this.ccyCrRateBasisCode.isReadonly()) {
			this.ccyCrRateBasisCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcyCrRateBasisCode.value"), null, true,true));
		}
		if (!this.ccyDrRateBasisCode.isReadonly()) {
			this.ccyDrRateBasisCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CurrencyDialog_CcyDrRateBasisCode.value"), null, true,true));
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
		this.ccyCrRateBasisCode.setConstraint("");
		this.ccyDrRateBasisCode.setConstraint("");
		logger.debug("Leaving ");
	}
	
	
	
	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
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
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList(){
		getCurrencyListCtrl().search();
	} 
	
	// CRUD operations

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
				"message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "+ 
				Labels.getLabel("label_CurrencyDialog_CcyCode.value")+" : "+aCurrency.getCcyCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCurrency.getRecordType())){
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
				if (doProcess(aCurrency, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
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
		
		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aCurrency.getRecordType())){
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

			if (doProcess(aCurrency, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
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
		
		aCurrency.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCurrency.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCurrency.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCurrency.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCurrency.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCurrency);
				}

				if (isNotesMandatory(taskId, aCurrency)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			
			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aCurrency.setTaskId(taskId);
			aCurrency.setNextTaskId(nextTaskId);
			aCurrency.setRoleCode(getRole());
			aCurrency.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aCurrency, tranType);
			
			String operationRefs = getServiceOperations(taskId, aCurrency);
			
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
				if (StringUtils.isBlank(method)){
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
						deleteNotes(getNotes(this.currency),true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}
	
	// WorkFlow Components
	
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
	 *  Get the window for entering Notes
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */ 
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.currency);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.currency.getCcyCode());
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
	
}
