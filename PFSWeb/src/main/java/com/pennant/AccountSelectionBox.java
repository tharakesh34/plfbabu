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
 * FileName    		:  ModuleSearchBox.java		                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-05-2013    														*
 *                                                                  						*
 * Modified Date    :  23-05-2013    														*
 *                                                                  						*
 * Description 		:  Module Search box                                            		*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Satish/Chaitanya	      0.1       		                            * 
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
package com.pennant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountSelectionBox extends Hbox {
	private static final long serialVersionUID = -4246285143621221275L;
	private static final Logger logger = Logger.getLogger(AccountSelectionBox.class);
	
	// Constants for the Manager Cheques 
	public static final String MGRCHQ_CR_FIN_EVENT = "MGRCHQCR";
	public static final String MGRCHQ_DR_FIN_EVENT = "MGRCHQDR";
	public static final String NOTAPPLICABLE = "NA";
	
	private 	Space 				space;
	private 	Uppercasebox		textbox;
	private 	Button 				button;
	private 	Decimalbox 			decimalbox;
	private 	Hbox 				hbox;
	
	/*** Mandatory Properties **/
	private 	String 				custCIF 			= ""; 		//mandatory
	private 	String 				branchCode 			= ""; 		//mandatory
	private 	int 				formatter 			= 0;
	private static final int	    tb_Width	     	= 150;
	private static final int	    db_Width	     	= 150;

	private 	boolean 			alwManualInput 		= false;	//mandatory
	private 	String 				accReceivables	 	= null;		//mandatory
	private 	String 				accTypes 			= null;		//mandatory
	private 	String 				currency 			= null;		//mandatory
	private 	boolean 			mandatory 			= false;	//mandatory

	private transient AccountInterfaceService accountInterfaceService;
	private 	List<IAccounts> 	accountDetails = new ArrayList<IAccounts>();
	private 	IAccounts			selectedAccount = null;
	
	private static final String	    errormesage1	 = "Invalid Account : ";

	/**
	 * AccountSelectionBox
	 * Constructor
	 * Defining the components and events
	 */
	public AccountSelectionBox() {
		super();
		logger.debug("Entering");
		
		// Create default object. 
		
		this.space = new Space();
		this.space.setWidth("2px");
		this.appendChild(space);

		//Hbox
		this.hbox = new Hbox();
		this.hbox.setSpacing("2px");
		this.hbox.setSclass("cssHbox");

		//Textbox
		this.textbox = new Uppercasebox();
		this.textbox.setStyle("border:0px;margin:0px;");
		this.textbox.setWidth(tb_Width + "px");
		this.textbox.setMaxlength(LengthConstants.LEN_ACCOUNT);

		// If input allowed set text box editable
		//this.textbox.addForward("onChange", this, "onChangeTextbox");
		if (alwManualInput) {
			this.textbox.setReadonly(false);
		} else {
			this.textbox.setReadonly(true);
		}
		this.hbox.appendChild(this.textbox);

		//Button
		this.button = new Button();
		this.button.setSclass("cssBtnSearch");
		this.button.setImage("/images/icons/LOVSearch.png");
		this.button.addForward("onClick", this, "onButtonClick");
		this.hbox.appendChild(this.button);
		this.appendChild(hbox);
		
		Space spaceBtwComp = new Space();
		spaceBtwComp.setWidth("10px");
		this.appendChild(spaceBtwComp);

		this.decimalbox = new Decimalbox();
		this.decimalbox.setWidth(db_Width + "px");
		this.decimalbox.setReadonly(true);
		this.decimalbox.setStyle("border:none; background-color:white ;font-weight:bold; text-align:left;");
		this.decimalbox.setTabindex(-1);
		this.decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.appendChild(decimalbox);

		logger.debug("Leaving");
	}
	
	/**
	 * Method to set Account Selection box Properties with defined definition on Finance Type Configuration
	 * @param finType
	 * @param accEvent
	 * @param finCcy
	 */
	public void  setAccountDetails(String finType, String accEvent, String finCcy) {
		FinTypeAccount finTypeAc = PennantAppUtil.getFinanceAccounts(finType, accEvent, finCcy);
		if(finTypeAc != null){
			alwManualInput 		= finTypeAc.isAlwManualEntry();		
			accReceivables	 	= finTypeAc.getAccountReceivable();
			accTypes			= finTypeAc.getCustAccountTypes();
		}
		
		currency = finCcy;
		this.button.setSclass("cssBtnSearch");
		this.button.setDisabled(false);
		if(alwManualInput){
			this.textbox.setReadonly(false);
		}else{
			this.textbox.setReadonly(true);
		}
	}
	
	/**
	 * Method to set Account Selection box Properties with external properties definition
	 * @param accTypeList
	 * @param accRecvbls
	 * @param alwManualEntry
	 */
	public void  setAcountDetails(String accTypeList, String accRecvbls, boolean alwManualEntry) {
		alwManualInput 		= alwManualEntry;		
		accReceivables	 	= StringUtils.trimToEmpty(accRecvbls);
		accTypes			= StringUtils.trimToEmpty(accTypeList);
		if(alwManualInput){
			this.textbox.setReadonly(false);
		}
	}
	
	/**
	 * Method to set Account Selection box Properties with external properties definition
	 * @param custMnemonic
	 * @param accTypeList
	 * @param accRecvbls
	 * @param alwManualEntry
	 */
	public void  setAccountDetails(String custMnemonic, String accTypeList, String accRecvbls, boolean alwManualEntry) {
		custCIF 			= custMnemonic;		
		alwManualInput 		= alwManualEntry;		
		accReceivables	 	= StringUtils.trimToEmpty(accRecvbls);
		accTypes			= StringUtils.trimToEmpty(accTypeList);
		if(alwManualInput){
			this.textbox.setReadonly(false);
		}
	}
	
	/**
	 * Called when changing the value of the text box
	 * @param event
	 * @throws InterruptedException 
	 * @throws WrongValueException 
	 */
	public void onChangeTextbox(Event event) throws  InterruptedException {
		logger.debug("Entering");
		
		this.setConstraint("");
		this.setErrorMessage("");
		Clients.clearWrongValue(this.button);
		validateValue();

		if (this.selectedAccount != null) {
			Events.postEvent("onFulfill", this, null);
		}else{
			if(StringUtils.isNotEmpty(this.textbox.getValue())){
				MessageUtil
						.showError(errormesage1 + PennantApplicationUtil.formatAccountNumber(this.textbox.getValue()));
			}
			this.setValue("");
		}
		logger.debug("Leaving");
	}
 
	/**
	 * Called when clicking on a button
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onButtonClick(Event event) throws InterruptedException {
		logger.debug("Entering");
		
		this.textbox.setErrorMessage("");
		Clients.clearWrongValue(this.button);
		if(!prepareAccountsList()){
			return;
		}
		
		try {
			
			Object object = ExtendedSearchListBox.show(this, "Accounts", this.accountDetails);
			if (object instanceof String) {
				doClear();
			} else {
				this.selectedAccount = (IAccounts) object;
				doWrite();
			}
			logger.debug("Leaving");
		} catch (Exception e) {
			logger.debug(e);
		} finally {
			Events.postEvent("onFulfill", this, null);
		}
	}
	
	/**
	 * Prepare the accounts list
	 * @throws InterruptedException
	 */
	private boolean prepareAccountsList() throws InterruptedException {
		accountDetails.clear();
		if(StringUtils.isNotBlank(accReceivables)){
			String[] accounts = accReceivables.split(",");
			for (String accountId : accounts) {
				IAccounts iAccounts = new IAccounts();
				iAccounts.setAccountId(accountId);
				accountDetails.add(iAccounts);
			}
			setAccountDetails(getCoreBankAccountDetails(accountDetails));
		}

		List<IAccounts> iAccountList = new ArrayList<IAccounts>();
		IAccounts iAccount = new IAccounts();
		//Removed the Account Currency to allow multiple currencies 
		iAccount.setAcCcy(currency);
		iAccount.setAcType(accTypes);
		iAccount.setAcCustCIF(this.custCIF);
		iAccount.setDivision(this.branchCode);

		try {
			iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
		}catch (InterfaceException e) {
			MessageUtil.showError(e);
			return false;
		}	

		if(iAccountList != null && !iAccountList.isEmpty()){
			for (IAccounts iAccounts : iAccountList) {
				if(accReceivables == null  || !accReceivables.contains(iAccounts.getAccountId())){
					accountDetails.add(iAccounts);
				}	
			}
		}
		return true;
	}
	
	/**
	 * Get account details of the account 
	 * @param returnAccList
	 * @return
	 * @throws InterruptedException
	 */
	private List<IAccounts> getCoreBankAccountDetails(List<IAccounts> returnAccList) throws InterruptedException {
		try {
			return getAccountInterfaceService().getAccountsAvailableBalList(returnAccList);
		} catch (InterfaceException  e) {
			returnAccList.clear();
			MessageUtil.showError(e);
		}
		return returnAccList;
	}
	
	/**
	 * Used to clear the values and attributes of the components
	 * @param event
	 */
	private void doClear() {
		logger.debug("Entering");
		this.textbox.setValue("");
		this.textbox.setTooltip("");
		this.decimalbox.setText("");
		this.textbox.setAttribute("data", null);
		logger.debug("Leaving");
	}

	/**
	 * Validate the value of the text entered in the textbox 
	 * @param showError
	 * @throws InterruptedException 
	 * @throws WrongValueException 
	 */
	public void validateValue() throws InterruptedException {
		//
	}
	
	/**
	 * Internal method to write the values to the components
	 * @throws InterruptedException 
	 * @throws Exception
	 */
	private void doWrite() throws InterruptedException {
		logger.debug("Entering");

		if(this.selectedAccount == null){
			return;
		}
		this.textbox.setValue(PennantApplicationUtil.formatAccountNumber(this.selectedAccount.getAccountId())); 
		this.textbox.setTooltiptext(selectedAccount.getAcShortName());

		this.decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(CurrencyUtil.getFormat(this.selectedAccount.getAcCcy()))); 
		this.decimalbox.setText(PennantAppUtil.amountFormate(this.selectedAccount.getAcAvailableBal(),CurrencyUtil.getFormat(this.selectedAccount.getAcCcy())));
		setFormatter(CurrencyUtil.getFormat(this.selectedAccount.getAcCcy()));
		logger.debug("Leaving");
	}
	
	/**
	 * Get the value from the text box after validating it
	 * @return String
	 * @throws InterruptedException 
	 * @throws WrongValueException 
	 */
	public String getValidatedValue() throws WrongValueException, InterruptedException {
		this.textbox.getValue();//to call the constraint if any
		if (alwManualInput && (StringUtils.isNotBlank(this.textbox.getValue()))) {
			validateValue();
		}
		return this.textbox.getValue();
	}
	
	/**
	 * Method to Set Read only properties to Account Selection Box
	 * */
	public void setReadonly(boolean isReadOnly) {
		this.button.setDisabled(isReadOnly);
		this.textbox.setReadonly(true);

		if (alwManualInput) {
			this.textbox.setReadonly(isReadOnly);
		}
		
		if(accReceivables == null && accTypes == null){
			this.button.setDisabled(true);
		}
		
		if (isReadOnly) {
			//this.space.setSclass("");
			this.button.setSclass(null);
		}else{
			this.button.setSclass("cssBtnSearch");
			if(isMandatory()){
				this.space.setSclass(PennantConstants.mandateSclass);
			}
		}
		
 		if (this.textbox.isReadonly()) {
 			this.textbox.setTabindex(-1);			
		}
	}
	
	public void setValue(String value) {
		if(StringUtils.isNotBlank(value)){
			this.textbox.setValue(PennantApplicationUtil.formatAccountNumber(value));
			try {
				validateValue();
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}else{
			this.textbox.setValue("");
			this.decimalbox.setText("");
		}
	}	
	public String getValue() {
		return textbox.getValue();
 	}
	public String getAcCcy() {
		if(this.selectedAccount == null){
			return "";
		}
		return this.selectedAccount.getAcCcy();
	}
	public String getAcShrtName() {
		if(this.selectedAccount == null){
			return "";
		}
		return this.selectedAccount.getAcShortName();
	}

	public void setTextBoxWidth(int width) {
		if (this.textbox != null) {
			this.textbox.setWidth(width + "px");
		}
	}
	
	public void setConstraint(String constraint) {
		this.textbox.setConstraint(constraint);
	}
	public void setConstraint(Constraint constraint) {
		this.textbox.setConstraint(constraint);
	}

	public void setErrorMessage(String errmsg) {
		this.textbox.setErrorMessage(errmsg);
	}
	public void clearErrorMessage() {
		this.textbox.clearErrorMessage();
	}

	public void setMandatoryStyle(boolean mandatory) {
		setMandatory(mandatory);
		if (mandatory) {
			this.space.setSclass(PennantConstants.mandateSclass);
		} else {
			this.space.setSclass("");
		}
	}

	public boolean isReadonly() {
		return this.textbox.isReadonly();
	}

	public void setButtonVisible(boolean isVisible) {
		this.button.setDisabled(!isVisible);
		this.button.setVisible(isVisible);
		if(!isVisible){
			this.button.setSclass(null);
		}else{
			this.button.setSclass("cssBtnSearch");
		}
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public void setAccReceivables(String accReceivables) {
		this.accReceivables = accReceivables;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getCustCIF() {
		return custCIF;
	}
	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}
	
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	public BigDecimal getAcBalance() {
		return PennantApplicationUtil.unFormateAmount(this.decimalbox.getValue(), formatter);
	}
	
	public int getFormatter() {
		return formatter;
	}
	public void setFormatter(int formatter) {
		this.formatter = formatter;
		this.decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
	}
	
	public List<IAccounts> getAccountDetails() {
		return accountDetails;
	}
	public void setAccountDetails(List<IAccounts> accountDetails) {
		this.accountDetails = accountDetails;
	}
	
	public IAccounts getSelectedAccount() {
		return selectedAccount;
	}
	public void setSelectedAccount(IAccounts selectedAccount) {
		this.selectedAccount = selectedAccount;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		if (this.accountInterfaceService == null) {
			this.accountInterfaceService = (AccountInterfaceService) SpringUtil.getBean("accountInterfaceService");
		}
		return accountInterfaceService;
	}

}
