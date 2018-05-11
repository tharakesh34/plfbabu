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
 * FileName    		:  SukukFinanceMainDialogCtrl.java                                      * 	  
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
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class SukukFinanceMainDialogCtrl extends FinanceMainBaseCtrl {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(SukukFinanceMainDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_SukukFinanceMainDialog; 						// autoWired

	
	protected Uppercasebox  issueNumber;										// autoWired
	protected Intbox		noOfUnits;											// autoWired
	protected Decimalbox  	faceValue;											// autoWired
	protected Combobox	  	premiumType;										// autoWired
	protected Decimalbox 	premiumValue;										// autoWired
	protected Decimalbox  	pricePerUnit;										// autoWired
	protected Decimalbox  	yieldValue;											// autoWired
	protected Datebox  		lastCouponDate;										// autoWired
	protected Decimalbox  	accruedProfit;										// autoWired
	protected Datebox  		purchaseDate;										// autoWired
	protected Decimalbox  	fairValuePerUnit;									// autoWired
	protected Decimalbox  	fairValueAmount;									// autoWired

	// old value variables for edit mode. that we can check if something 
	// on the values are edited since the last initialization.
	protected transient String 			oldVar_issueNumber;
	protected transient int 			oldVar_noOfUnits;
	protected transient BigDecimal		oldVar_faceValue;
	protected transient int 			oldVar_premiumType;
	protected transient BigDecimal 		oldVar_premiumValue;
	protected transient BigDecimal 		oldVar_pricePerUnit;
	protected transient BigDecimal 		oldVar_yieldValue;
	protected transient Date 			oldVar_lastCouponDate;
	protected transient BigDecimal 		oldVar_accruedProfit;
	protected transient Date 			oldVar_purchaseDate;
	protected transient BigDecimal 		oldVar_fairValuePerUnit;
	protected transient BigDecimal 		oldVar_fairValueAmount;

	/**
	 * default constructor.<br>
	 */
	public SukukFinanceMainDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.doSetProperties();
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SukukFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SukukFinanceMainDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			FinanceMain befImage = new FinanceMain();
			BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
			setFinanceDetail(getFinanceDetail());
			old_NextRoleCode = getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
		}

		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (arguments.containsKey("financeMainListCtrl")) {
			setFinanceMainListCtrl((FinanceMainListCtrl) arguments.get("financeMainListCtrl"));
		} 
		
		if (arguments.containsKey("financeSelectCtrl")) {
			setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
		} 

		if (arguments.containsKey("tabbox")) {
			listWindowTab = (Tab) arguments.get("tabbox");
		}

		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		if (arguments.containsKey("eventCode")) {
			eventCode = (String) arguments.get("eventCode");
		}
		
		if (arguments.containsKey("menuItemRightName")) {
			menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain);

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateMenuRoleAuthorities(getRole(), super.pageRightName, menuItemRightName);
		}else{
			this.south.setHeight("0px");
		}

		setMainWindow(window_SukukFinanceMainDialog);
		setProductCode("Sukuk");
		
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52+ "px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceDetail());
		Events.echoEvent("onPostWinCreation", this.self, null);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		super.doSetFieldProperties();
		
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		//Finance Premium/ Discount Details
		this.issueNumber.setMaxlength(20);
		this.noOfUnits.setMaxlength(9);
		this.faceValue.setMaxlength(18);
		this.faceValue.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.premiumValue.setMaxlength(18);
		this.premiumValue.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.pricePerUnit.setMaxlength(18);
		this.pricePerUnit.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.yieldValue.setMaxlength(18);
		this.yieldValue.setFormat(PennantConstants.rateFormate9);
		this.accruedProfit.setMaxlength(18);
		this.accruedProfit.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.fairValuePerUnit.setMaxlength(18);
		this.fairValuePerUnit.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.fairValueAmount.setMaxlength(18);
		this.fairValueAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.lastCouponDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.purchaseDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_SukukFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		processSave();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_SukukFinanceMainDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}

	// GUI operations

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException 
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException, InterruptedException, 
	InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		aFinanceDetail.getFinScheduleData().getFinanceMain().setFinRepayPftOnFrq(true);

		//this.disbAcctId.setMandatoryStyle(false);
		this.repayAcctId.setMandatoryStyle(false);
		this.downPayAccount.setMandatoryStyle(false);
		this.row_downPayBank.setVisible(false);
		this.row_downPaySupl.setVisible(false);
				
		super.doWriteBeanToComponents(aFinanceDetail, onLoadProcess);
		//FIXME : DataSet Removal to be worked on if it requires in future
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData
	 *            (FinScheduleData)
	 * @throws Exception 
	 */
	public void doWriteComponentsToBean(FinanceDetail aFinanceDetail) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		ArrayList<WrongValueException> wve = super.doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());
		//FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		//FIXME : DataSet Removal to be worked on if it requires in future
		//Premium Detail Validation
		//aFinanceDetail.setPremiumDetail(doPreparePremiumDetail(aFinanceDetail, aFinanceMain.getRepayPftFrq(), wve));
		
		//FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Preparing Premium Details
	 * @param financeDetail
	 */
	/*private FinancePremiumDetail doPreparePremiumDetail(FinanceDetail detail, String repayPftFrq, ArrayList<WrongValueException> wve){
		logger.debug("Entering");
		
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		FinancePremiumDetail premiumDetail = detail.getPremiumDetail();
		//Finance Premium or Discount Details
		if(premiumDetail == null){
			premiumDetail = new FinancePremiumDetail();
		}
		try {
			premiumDetail.setIssueNumber(this.issueNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setNoOfUnits(this.noOfUnits.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setFaceValue(PennantApplicationUtil.unFormateAmount(this.faceValue.getValue(),formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setPremiumType(getComboboxValue(this.premiumType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setPremiumValue(PennantApplicationUtil.unFormateAmount(this.premiumValue.getValue(),formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setPricePerUnit(PennantApplicationUtil.unFormateAmount(this.pricePerUnit.getValue(),formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setYieldValue(this.yieldValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			
			if(!this.lastCouponDate.isDisabled()){

				if(this.lastCouponDate.getValue() != null){
					if(!FrequencyUtil.isFrqDate(repayPftFrq, this.lastCouponDate.getValue())){
						throw new WrongValueException(this.lastCouponDate,  Labels.getLabel("FRQ_DATE_MISMATCH", 
								new String[] { Labels.getLabel("label_SukukFinanceMainDialog_LastCouponDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value") }));
					}

					if(this.finStartDate.getValue() != null){ 
						if(this.finStartDate.getValue().compareTo(this.lastCouponDate.getValue()) > 0){
							throw new WrongValueException(this.lastCouponDate,  Labels.getLabel("DATE_NOT_BEFORE", 
									new String[] { Labels.getLabel("label_SukukFinanceMainDialog_LastCouponDate.value"),
									Labels.getLabel("label_FinanceMainDialog_FinStartDate.value") }));
						}

						if(this.lastCouponDate.getValue().compareTo(this.purchaseDate.getValue()) > 0){
							throw new WrongValueException(this.lastCouponDate,  Labels.getLabel("DATE_NOT_AFTER", 
									new String[] { Labels.getLabel("label_SukukFinanceMainDialog_LastCouponDate.value"),
									Labels.getLabel("label_SukukFinanceMainDialog_PurchaseDate.value") }));
						}
					}

					if(this.maturityDate_two.getValue() != null){
						if(this.lastCouponDate.getValue().compareTo(this.maturityDate_two.getValue()) >= 0){
							throw new WrongValueException(this.lastCouponDate,  Labels.getLabel("DATE_ALLOWED_BEFORE", 
									new String[] { Labels.getLabel("label_SukukFinanceMainDialog_LastCouponDate.value"),
									Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") }));
						}
					}
				}
			}
			premiumDetail.setLastCouponDate(this.lastCouponDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setAccruedProfit(PennantApplicationUtil.unFormateAmount(this.accruedProfit.getValue(),formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.maturityDate_two.getValue() != null && !this.maturityDate.isDisabled()){
				if(this.purchaseDate.getValue().compareTo(this.maturityDate_two.getValue()) >= 0){
					throw new WrongValueException(this.maturityDate_two,  Labels.getLabel("DATE_ALLOWED_AFTER", 
							new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
							Labels.getLabel("label_SukukFinanceMainDialog_PurchaseDate.value") }));
				}
			}
			premiumDetail.setPurchaseDate(this.purchaseDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setFairValuePerUnit(PennantApplicationUtil.unFormateAmount(this.fairValuePerUnit.getValue(),formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			premiumDetail.setFairValueAmount(PennantApplicationUtil.unFormateAmount(this.fairValueAmount.getValue(),formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		logger.debug("Leaving");
		return premiumDetail;
	}*/

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	public boolean isDataChanged(boolean close) {
		logger.debug("Entering");

		//Finance Premium or Discount Details
		if(!StringUtils.equals(this.issueNumber.getValue(),this.oldVar_issueNumber)){
			return true;
		}
		if(this.noOfUnits.intValue() != this.oldVar_noOfUnits){
			return true;
		}
		if(this.faceValue.getValue().compareTo(oldVar_faceValue)!=0){
			return true;
		}
		
		if(this.premiumType.getSelectedIndex() != this.oldVar_premiumType){
			return true;
		}
		if(this.premiumValue.getValue().compareTo(this.oldVar_premiumValue)!=0){
			return true;
		}
		if(this.pricePerUnit.getValue().compareTo(this.oldVar_pricePerUnit)!=0){
			return true;
		}
		if(this.yieldValue.getValue().compareTo(this.oldVar_yieldValue)!=0){
			return true;
		}
		if(DateUtility.compare(this.lastCouponDate.getValue(),this.oldVar_lastCouponDate)!= 0){
			return true;
		}
		if(this.accruedProfit.getValue().compareTo(this.oldVar_accruedProfit)!=0){
			return true;
		}
		if(DateUtility.compare(this.purchaseDate.getValue(),this.oldVar_purchaseDate)!=0){
			return true;
		}
		if(this.fairValuePerUnit.getValue().compareTo(this.oldVar_fairValuePerUnit)!=0){
			return true;
		}
		if(this.fairValueAmount.getValue().compareTo(this.oldVar_fairValueAmount)!=0){
			return true;
		}
		
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	protected void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		super.doSetValidation();

		//Finance Premium or Discount Details
		int ccyformatter =CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		if (!this.issueNumber.isReadonly()) {
			this.issueNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_SukukFinanceMainDialog_IssueNumber.value"),null,true));
		}
		if (!this.noOfUnits.isReadonly()) {
			this.noOfUnits.setConstraint(new PTNumberValidator( Labels.getLabel("label_SukukFinanceMainDialog_IssueNumber.value"), true));
		}
		if (!this.faceValue.isDisabled()) {
			this.faceValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SukukFinanceMainDialog_FaceValue.value"), ccyformatter,true));
		}
		if (!this.premiumType.isDisabled()) {
			this.premiumType.setConstraint(new StaticListValidator(PennantStaticListUtil.getPremiumTypeList(), Labels.getLabel("label_SukukFinanceMainDialog_PremiumType.value")));
		}
		if (!this.premiumValue.isDisabled()) {
			this.premiumValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SukukFinanceMainDialog_PremiumValue.value"), ccyformatter,true));
		}
		if (!this.pricePerUnit.isDisabled()) {
			this.pricePerUnit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SukukFinanceMainDialog_PurchasePricePerUnit.value"), ccyformatter,true));
		}
		if (!this.fairValuePerUnit.isDisabled()) {
			this.fairValuePerUnit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SukukFinanceMainDialog_FairValuePerUnit.value"), ccyformatter,false));
		}
		if (!this.lastCouponDate.isDisabled()) {
			this.lastCouponDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SukukFinanceMainDialog_LastCouponDate.value"), true));
		}
		/*if (!this.yieldValue.isDisabled()) {
			this.yieldValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SukukFinanceMainDialog_Yield.value"), ccyformatter,false));
		}
		if (!this.accruedProfit.isDisabled()) {
			this.accruedProfit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SukukFinanceMainDialog_AccruedProfit.value"), ccyformatter,false));
		}
		if (!this.fairValuePerUnit.isDisabled()) {
			this.fairValuePerUnit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SukukFinanceMainDialog_FairValuePerUnit.value"), ccyformatter,false));
		}
		if (!this.fairValueAmount.isDisabled()) {
			this.fairValueAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SukukFinanceMainDialog_fairValueAmount.value"), ccyformatter,false));
		}*/

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	protected void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);

		super.doRemoveValidation();
		
		//Finance Premium or Discount Details
		this.issueNumber.setConstraint("");
		this.noOfUnits.setConstraint("");
		this.faceValue.setConstraint("");
		this.premiumType.setConstraint("");
		this.premiumValue.setConstraint("");
		this.pricePerUnit.setConstraint("");
		this.yieldValue.setConstraint("");
		this.accruedProfit.setConstraint("");
		this.lastCouponDate.setConstraint("");
		this.fairValuePerUnit.setConstraint("");
		this.fairValueAmount.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages.
	 * */
	public void doClearMessage() {
		logger.debug("Entering");
		
		super.doClearMessage();
		
		//Finance Premium or Discount Details
		this.issueNumber.setErrorMessage("");
		this.noOfUnits.setErrorMessage("");
		this.faceValue.setErrorMessage("");
		this.premiumType.setErrorMessage("");
		this.premiumValue.setErrorMessage("");
		this.pricePerUnit.setErrorMessage("");
		this.yieldValue.setErrorMessage("");
		this.accruedProfit.setErrorMessage("");
		this.lastCouponDate.setErrorMessage("");
		this.fairValuePerUnit.setErrorMessage("");
		this.fairValueAmount.setErrorMessage("");
				
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");
		
		super.doEdit();
		this.finAmount.setDisabled(true);

		//Finance Premium or Discount Details
		this.issueNumber.setReadonly(isReadOnly("FinanceMainDialog_issueNumber"));
		this.noOfUnits.setReadonly(isReadOnly("FinanceMainDialog_noOfUnits"));
		this.faceValue.setDisabled(isReadOnly("FinanceMainDialog_faceValue"));
		this.premiumType.setDisabled(isReadOnly("FinanceMainDialog_premiumType"));
		this.premiumValue.setDisabled(isReadOnly("FinanceMainDialog_premiumValue"));
		this.pricePerUnit.setDisabled(true);//isReadOnly("FinanceMainDialog_pricePerUnit")
		this.yieldValue.setDisabled(true);//isReadOnly("FinanceMainDialog_yieldValue")
		this.accruedProfit.setDisabled(true);//isReadOnly("FinanceMainDialog_accruedProfit")
		this.lastCouponDate.setDisabled(isReadOnly("FinanceMainDialog_lastCouponDate"));
		this.purchaseDate.setDisabled(true);//isReadOnly("FinanceMainDialog_purchaseDate")
		this.fairValuePerUnit.setDisabled(isReadOnly("FinanceMainDialog_fairValuePerUnit"));
		this.fairValueAmount.setDisabled(true);//isReadOnly("FinanceMainDialog_fairValueAmount")
				
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		super.doReadOnly();
		
		//Finance Premium or Discount Details
		this.issueNumber.setReadonly(true);
		this.noOfUnits.setReadonly(true);
		this.faceValue.setDisabled(true);
		this.premiumType.setDisabled(true);
		this.premiumValue.setDisabled(true);
		this.pricePerUnit.setDisabled(true);
		this.yieldValue.setDisabled(true);
		this.accruedProfit.setDisabled(true);
		this.lastCouponDate.setDisabled(true);
		this.purchaseDate.setDisabled(true);
		this.fairValuePerUnit.setDisabled(true);
		this.fairValueAmount.setDisabled(true);
		
		logger.debug("Leaving");
	}

	//*************************************************************//
	//*********** Purchase Value Calculation Details **************//
	//*************************************************************//

	public void onChange$noOfUnits(Event event){
		logger.debug("Entering " + event.toString());
		doCalcPurchaseValue();
		logger.debug("Leaving " + event.toString());
	}
	
	public void onChange$faceValue(Event event){
		logger.debug("Entering " + event.toString());
		doCalcPurchaseValue();
		logger.debug("Leaving " + event.toString());
	}
	
	public void onChange$premiumType(Event event){
		logger.debug("Entering " + event.toString());
		doCalcPurchaseValue();
		logger.debug("Leaving " + event.toString());
	}
	
	public void onChange$premiumValue(Event event){
		logger.debug("Entering " + event.toString());
		doCalcPurchaseValue();
		logger.debug("Leaving " + event.toString());
	}
	
	public void onChange$fairValuePerUnit(Event event){
		logger.debug("Entering " + event.toString());
		doCalcPurchaseValue();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Calculation for Purchase Value based on Premium Details
	 */
	private void doCalcPurchaseValue(){
		logger.debug("Entering");
		int formatter =CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		this.finAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO,formatter ));
		this.fairValueAmount.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO,formatter ));
		this.pricePerUnit.setValue(PennantApplicationUtil.formateAmount(BigDecimal.ZERO,formatter ));
		
		//Check No Of Units
		if(this.noOfUnits.intValue() < 0){
			return;
		}
		
		//Fair Value Amount Calculation
		fairValueCalculation();
		
		//Check Face Value
		if(this.faceValue.getValue().compareTo(BigDecimal.ZERO) <= 0){
			return;
		}

		//Check Premium Type
		if(this.premiumType.getSelectedItem() == null || this.premiumType.getSelectedIndex() <= 0){
			return;
		}
		
		//Check Premium Type
		if(this.premiumValue.getValue().compareTo(BigDecimal.ZERO) < 0){
			return;
		}
		
		//Calculation For Purchase Price Per Unit
		String prmType = this.premiumType.getSelectedItem().getValue().toString();
		if(prmType.equals(FinanceConstants.PREMIUMTYPE_PREMIUM)){
			BigDecimal untiPrice = this.faceValue.getValue().add(this.premiumValue.getValue());
			this.pricePerUnit.setValue(untiPrice);
		}else if(prmType.equals(FinanceConstants.PREMIUMTYPE_DISCOUNT)){
			
			BigDecimal faceVal = this.faceValue.getValue().setScale( formatter, RoundingMode.HALF_DOWN);
			BigDecimal untiPrice = faceVal.subtract(this.premiumValue.getValue());
			if(untiPrice.compareTo(BigDecimal.ZERO) <= 0){
				return;
			}
			this.pricePerUnit.setValue(untiPrice);
		}
		
		//Calculation For Purchase Value
		BigDecimal purValue = this.pricePerUnit.getValue().multiply(new BigDecimal(this.noOfUnits.intValue()));
		this.finAmount.setValue(purValue);
		
		//Fair Value Amount Calculation
		fairValueCalculation();
		
		logger.debug("Leaving");
	}
	
	public void onChange$finStartDate(Event event){
		logger.debug("Entering " + event.toString());
		fairValueCalculation();
		logger.debug("Leaving " + event.toString());
	}
	
	private void fairValueCalculation(){
		
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		Date purchaseDate = this.purchaseDate.getValue();
		Date startDate = this.finStartDate.getValue();
		this.fairValuePerUnit.setDisabled(isReadOnly("FinanceMainDialog_fairValuePerUnit"));
		
		if(startDate != null && purchaseDate != null && StringUtils.isEmpty(moduleDefiner)){

			//Fair Value Amount Re-Calculation
			if(startDate != null && startDate.compareTo(purchaseDate) == 0){
				this.fairValuePerUnit.setDisabled(true);
				this.fairValuePerUnit.setValue(this.pricePerUnit.getValue());
			}
		}
		
		if(this.fairValuePerUnit.getValue().compareTo(BigDecimal.ZERO) > 0){
			BigDecimal fairVal = this.fairValuePerUnit.getValue().setScale( formatter, RoundingMode.HALF_DOWN);
			BigDecimal fairValue = fairVal.multiply(new BigDecimal(this.noOfUnits.intValue()));
			this.fairValueAmount.setValue(fairValue);
		}
	}
	
	public void onCheck$manualSchedule(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		super.onCheckmanualSchedule();
		logger.debug("Leaving " + event.toString());
	}
}

