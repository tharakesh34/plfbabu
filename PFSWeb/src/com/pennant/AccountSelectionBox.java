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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

public class AccountSelectionBox extends Hbox {
	private static final long serialVersionUID = -4246285143621221275L;
	private final static Logger logger = Logger.getLogger(AccountSelectionBox.class);
	
	private 	Space 		space;
	private 	Textbox 	textbox;
	private 	Button 		button;
	private 	Decimalbox 	decimalbox;
	private 	Hbox 		hbox;
	

	private 	List<IAccounts> accountDetails = new ArrayList<IAccounts>();
	private 	FinTypeAccount finTypeAccount = null;
	private 	IAccounts	selectedAccount = null;
	private 	int 		formatter = 0;
	
	/*** Mandatory Properties **/
	private 	String 		moduleName = "Accounts"; 				//mandatory
	private 	String 		custCIF = ""; 							//mandatory
	private 	String 		branchCode = ""; 						//mandatory

	private 	boolean 	isdisplayError = true;					//mandatory
	private 	boolean 	inputAllowed = true;					//mandatory

	private static final int	    tb_Width	     = 150;
	private static final int	    db_Width	     = 150;
	
	private transient AccountInterfaceService accountInterfaceService;

	/**
	 * AccountSelectionBox
	 * Constructor
	 * Defining the components and events
	 */
	public AccountSelectionBox() {
		logger.debug("Entering");

		space = new Space();
		space.setWidth("2px");
		this.appendChild(space);

		//Hbox
		hbox = new Hbox();
		hbox.setSpacing("2px");
		hbox.setSclass("cssHbox");

		//Textbox
		textbox = new Textbox();
		textbox.setStyle("border:0px;margin:0px;");
		textbox.setWidth(tb_Width + "px");

		// If input allowed set text box editable
		if (inputAllowed) {
			textbox.setReadonly(false);
			textbox.addForward("onChange", this, "onChangeTextbox");
		} else {
			textbox.setReadonly(true);
		}
		hbox.appendChild(textbox);

		//Button
		button = new Button();
		button.setSclass("cssBtnSearch");
		button.setImage("/images/icons/LOVSearch.png");
		button.addForward("onClick", this, "onButtonClick");
		hbox.appendChild(button);
		this.appendChild(hbox);

		decimalbox = new Decimalbox();
		decimalbox.setWidth(db_Width + "px");
		decimalbox.setReadonly(true);
		decimalbox.setStyle("border:none; background-color:white ;font-weight:bold; text-align:left;");
		decimalbox.setTabindex(-1);
		this.appendChild(decimalbox);

		logger.debug("Leaving");
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
			try {
				//doWrite();
			} catch (Exception e) {
				logger.debug(e.toString());
				e.printStackTrace();
			} finally {
				Events.postEvent("onFulfill", this, null);
			}
		}else{
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
		if(finTypeAccount != null){
			if(!prepareAccountsList()){
				return;
			}
		}
		try {
			if((moduleName != null && moduleName.length() != 0 )) {
				Object object = ExtendedSearchListBox.show(this, moduleName, this.accountDetails);
				if (object instanceof String) {
					doClear();
				} else {
					this.selectedAccount = (IAccounts) object;
					doWrite();
				}
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
		if(!StringUtils.trimToEmpty(finTypeAccount.getAccountReceivable()).equals("")){
			String[] accounts = finTypeAccount.getAccountReceivable().split(",");
			for (String accountId : accounts) {
				IAccounts iAccounts = new IAccounts();
				iAccounts.setAccountId(accountId);
				accountDetails.add(iAccounts);
			}
			setAccountDetails(getCoreBankAccountDetails(accountDetails));
		}	
		if(!this.finTypeAccount.getCustAccountTypes().equals("") && !StringUtils.trimToEmpty(this.custCIF).equals("")){
			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			//Removed the Account Currency to allow multiple currencies 
			//iAccount.setAcCcy(this.finTypeAccount.getFinCcy());
			iAccount.setAcCcy("");
			iAccount.setAcType(this.finTypeAccount.getCustAccountTypes().replace(",",""));
			iAccount.setAcCustCIF(this.custCIF);
			iAccount.setDivision(this.branchCode);

			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
			}catch (AccountNotFoundException e) {
				PTMessageUtils.showErrorMessage(e.getErrorMsg());
				return false;
			}	
			if(iAccountList != null && !iAccountList.isEmpty()){
				for (IAccounts iAccounts : iAccountList) {
					if(this.finTypeAccount.getAccountReceivable() == null  || !this.finTypeAccount.getAccountReceivable().contains(iAccounts.getAccountId())){
						accountDetails.add(iAccounts);
					}	
				}
			}
		}
		return true;
	}
	
	/**
	 * Get account details of the account 
	 * @param accountDetails
	 * @return
	 * @throws InterruptedException
	 */
	private List<IAccounts> getCoreBankAccountDetails(List<IAccounts> accountDetails) throws InterruptedException {
		try {
			return getAccountInterfaceService().getAccountsAvailableBalList(accountDetails);
		} catch (AccountNotFoundException  e) {
			logger.debug(e);
			accountDetails.clear();
			PTMessageUtils.showErrorMessage(e.getErrorMsg());
		}
		return accountDetails;
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
	 * Internal method to write the calues to the components
	 * @throws Exception
	 */
	private void doWrite() throws Exception {
		logger.debug("Entering");
		this.textbox.setValue(PennantApplicationUtil.formatAccountNumber(this.selectedAccount.getAccountId())); 
		this.textbox.setTooltiptext(selectedAccount.getAcShortName());
		JdbcSearchObject<Currency> searchObject = new JdbcSearchObject<Currency>(Currency.class);
		searchObject.addFilterEqual("CcyCode", this.selectedAccount.getAcCcy());
				PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<Currency> currencyList = pagedListService.getBySearchObject(searchObject);
		this.decimalbox.setText(PennantAppUtil.amountFormate(this.selectedAccount.getAcAvailableBal(),currencyList.get(0).getCcyEditField())); 
  		logger.debug("Leaving");
	}

 
	/**
	 * Validate the value of the text entered in the textbox 
	 * @param showError
	 * @throws InterruptedException 
	 * @throws WrongValueException 
	 */
	public void validateValue() throws WrongValueException, InterruptedException {
		String accno = this.textbox.getValue().replace("-", "");
		boolean valid = false;
		selectedAccount = null;
		if(!accno.equals("")){
			try {
				Pattern pattern = Pattern.compile(PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ACCOUNT));
				Matcher matcher = pattern.matcher(accno);
				valid = matcher.matches();
			} catch (Exception e) {
				logger.debug(e);
			}
			if (!valid) {
				throw new WrongValueException(this.textbox, Labels.getLabel(PennantRegularExpressions.REGEX_ACCOUNT,
						new String[] {Labels.getLabel("label_FinTypeAccountDialog_AccReceivableAccNumber.value") }));
			} else {
				IAccounts accountDetail = validateAccountInEquation(accno);
				if (accountDetail == null) {
					this.textbox.setFocus(true);
					this.decimalbox.setValue(BigDecimal.ZERO);
					Events.postEvent("onFulfill", this, null);
				//	doClear();	
					throw new WrongValueException(this.textbox, this.textbox.getValue() + " is not valid");
				}  else{
					this.selectedAccount = accountDetail;
					try {
						doWrite();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * this method validates account number balance <br>
	 * by passing argument to checkAccountID method in 
	 * AccountInterfaceService
	 */
	private IAccounts validateAccountInEquation(String accountId) throws InterruptedException {
		try {
			List<CoreBankAccountDetail> coreAcctList=new ArrayList<CoreBankAccountDetail>();
			CoreBankAccountDetail accountDetail=new CoreBankAccountDetail();
			accountDetail.setAccountNumber(accountId);
			coreAcctList.add(accountDetail);
			List<CoreBankAccountDetail> accountDetails = getAccountInterfaceService().checkAccountID(coreAcctList);
			if (accountDetails!=null && !accountDetails.isEmpty()) {
				accountDetail = accountDetails.get(0);
				IAccounts iAccount = new IAccounts();
				iAccount.setAccountId(accountDetail.getAccountNumber());
				iAccount.setAcCcy(accountDetail.getAcCcy());
				iAccount.setAcType(accountDetail.getAcType());
				iAccount.setAcShortName(accountDetail.getAcShrtName());
				if(StringUtils.trimToEmpty(accountDetail.getAmountSign()).equals("-")){
					iAccount.setAcAvailableBal(BigDecimal.ZERO.subtract(
						accountDetail.getAcBal()));
				}else{
					iAccount.setAcAvailableBal(accountDetail.getAcBal());
				}
				
				return iAccount;
			}
			
		} catch (AccountNotFoundException e) {
			logger.debug(e);
//			PTMessageUtils.showErrorMessage(e.getErrorMsg());
		}
		return null;
	}
	

	public void  setFinanceDetails(String fintType, String event, String finCcy) {
		this.finTypeAccount = PennantAppUtil.getFinanceAccounts(fintType, event, finCcy);
		if(finTypeAccount != null && finTypeAccount.isAlwManualEntry()){
			this.textbox.setReadonly(false);
		}
	}

	public AccountInterfaceService getAccountInterfaceService() {
		if (this.accountInterfaceService == null) {
			this.accountInterfaceService = (AccountInterfaceService) SpringUtil.getBean("accountInterfaceService");
		}
		return accountInterfaceService;
	}
	
	/**
	 * Get the value from the text box after validating it
	 * @return String
	 * @throws InterruptedException 
	 * @throws WrongValueException 
	 */
	public String getValidatedValue() throws WrongValueException, InterruptedException {
		this.textbox.getValue();//to call the constraint if any
		if (!StringUtils.trimToEmpty(this.textbox.getValue()).equals("")) {
			if (inputAllowed) {
				validateValue();
			}
 			return this.textbox.getValue();
		} else {
			return "";
		}

	}
	
	/**
	 * Set ReadOnly
	 * @param isReadOnly
	 */
	public void setReadonly(boolean isReadOnly) {
		this.button.setVisible(!isReadOnly);
		this.textbox.setReadonly(true);
 		if(finTypeAccount != null){
			if (this.finTypeAccount.isAlwManualEntry()) {
				this.textbox.setReadonly(isReadOnly);
			}  
			if (isReadOnly) {
				this.space.setSclass("");
			}
		}
 		if (this.textbox.isReadonly()) {
 			this.textbox.setTabindex(-1);			
		}
	}

	/**
	 * Set Value and description to the textbox and label
	 * @param value
	 * @param desc
	 */
	/*public void setValue(String value, String desc) {
	//	BigDecimal accountBalance = StringUtils.trimToEmpty(desc).equals("")?BigDecimal.ZERO:new BigDecimal(desc);	
		this.textbox.setValue(PennantApplicationUtil.formatAccountNumber(value));
		this.decimalbox.setText(desc);
 	}
*/	
	public void setValue(String value) {
		//	BigDecimal accountBalance = StringUtils.trimToEmpty(desc).equals("")?BigDecimal.ZERO:new BigDecimal(desc);
		if(!StringUtils.trimToEmpty(value).equals("")){
			this.textbox.setValue(PennantApplicationUtil.formatAccountNumber(value));
			try {
				validateValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			this.decimalbox.setText("");
		}
	}
	
/*	public void setBalance(String desc) {
		this.decimalbox.setText(desc);
	}*/

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
		if (mandatory) {
			this.space.setSclass("mandatory");
		} else {
			this.space.setSclass("");
		}
	}
	
	public String getSclass() {
		return StringUtils.trimToEmpty(this.space.getSclass());
	}

	public boolean isReadonly() {
		return this.textbox.isReadonly();
	}

   	public boolean isIsdisplayError() {
		return isdisplayError;
	}
	public void setIsdisplayError(boolean isdisplayError) {
		this.isdisplayError = isdisplayError;
	}
	
	public List<IAccounts> getAccountDetails() {
		return accountDetails;
	}

	public void setAccountDetails(List<IAccounts> accountDetails) {
		this.accountDetails = accountDetails;
	}

	public String getCustCIF() {
		return custCIF;
	}

	public void setCustCIF(String custCIF) {
		this.custCIF = custCIF;
	}

	public int getFormatter() {
		return formatter;
	}

	public void setFormatter(int formatter) {
		this.formatter = formatter;
	}
	
	public String getValue() {
		return textbox.getValue();//to call the constraint if any
 	}
	public String getDesc() {
		return this.decimalbox.getText();//to call the constraint if any
 	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
}
