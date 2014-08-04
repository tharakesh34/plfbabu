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
 *
 * FileName    		:  AccountProcessUtil.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.dao.masters.SystemInternalAccountDefinitionDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class AccountProcessUtil implements Serializable {

    private static final long serialVersionUID = -1200799666995440280L;
	private Logger logger = Logger.getLogger(AccountProcessUtil.class);

	private AccountsDAO accountsDAO;
	private AccountTypeDAO accountTypeDAO;
	private SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO;

	private Accounts account;
	private Map<String, SystemInternalAccountDefinition> intAcMap ;
	private Map<String, AccountType> accountTypesMap ;
	private Map<String, Accounts> saveAccMap ;
	private Map<String, Accounts> updateAccMap ;
	
	public AccountProcessUtil() {
	    super();
    }

	/**
	 * Method for Account Details Updation after Postings
	 * @param financeMain
	 * @param accrualBal(Profit Details)
	 * @param dataSets
	 */
	public void procAccountUpdate(List<ReturnDataSet> dataSets , BigDecimal accrualBal){
		logger.debug("Entering");

		intAcMap = new HashMap<String, SystemInternalAccountDefinition>(1);
		accountTypesMap = new HashMap<String, AccountType>(1);
		saveAccMap = new HashMap<String, Accounts>(1);
		updateAccMap = new HashMap<String, Accounts>(1);

		for (int i = 0; i < dataSets.size(); i++) {

			ReturnDataSet set = dataSets.get(i);
			boolean isRcdSave = false;

			if(!(set.getAccountType().equals("DISB") 
					|| set.getAccountType().equals("REPAY")
					|| set.getAccountType().equals("INVSTR"))){

				//Check Account Details Already exist or not
				if(saveAccMap.containsKey(set.getAccount())){
					account = saveAccMap.get(set.getAccount());
					isRcdSave = true;
				}else if(updateAccMap.containsKey(set.getAccount())){
					account = updateAccMap.get(set.getAccount());
				}else{
					account = getAccountsDAO().getAccountsById(set.getAccount(), "");
					if(account == null){
						isRcdSave = true;
					}
				}
				
				//if Non of the Account is found create new A/c else update
				updateAccountDetails(account, set, accrualBal, isRcdSave);	
			}
		}
		
		//DB Insertion or updation of Account details
		if(saveAccMap.size() > 0){
			getAccountsDAO().saveList(new ArrayList<Accounts>(saveAccMap.values()),"");
		}
		if(updateAccMap.size() > 0){
			getAccountsDAO().updateList(new ArrayList<Accounts>(updateAccMap.values()),"");
		}
		
		intAcMap = null;
		accountTypesMap = null;
		saveAccMap = null;
		updateAccMap = null;
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Updation Of Account balances for every Postings
	 * @param list
	 * @param isPostingsSucces
	 */
	public void updateAccountInfo(List<ReturnDataSet> list){
		logger.debug("Entering");
		
		intAcMap = new HashMap<String, SystemInternalAccountDefinition>(1);
		accountTypesMap = new HashMap<String, AccountType>(1);
		saveAccMap = new HashMap<String, Accounts>(1);
		updateAccMap = new HashMap<String, Accounts>(1);

		//Prepare Accounts Accrual Balance if Shadow Postings
		for (int i = 0; i < list.size(); i++) {
			ReturnDataSet set = list.get(i);
			boolean isRcdSave = false;
			
			if(!(set.getAccountType().equals("DISB") 
					|| set.getAccountType().equals("REPAY")
					|| set.getAccountType().equals("INVSTR"))){

				//Check Account Details Already exist or not
				if(saveAccMap.containsKey(set.getAccount())){
					account = saveAccMap.get(set.getAccount());
					isRcdSave = true;
				}else if(updateAccMap.containsKey(set.getAccount())){
					account = updateAccMap.get(set.getAccount());
				}else{
					account = getAccountsDAO().getAccountsById(set.getAccount(), "");
					if(account == null){
						isRcdSave = true;
					}
				}
				
				//if Non of the Account is found create new A/c else update
				updateAccountDetails(account, set, set.getPostAmount(), isRcdSave);	

			}
		}
		
		//DB Insertion or updation of Account details
		if(saveAccMap.size() > 0){
			getAccountsDAO().saveList(new ArrayList<Accounts>(saveAccMap.values()),"");
		}
		if(updateAccMap.size() > 0){
			getAccountsDAO().updateList(new ArrayList<Accounts>(updateAccMap.values()),"");
		}
		
		intAcMap = null;
		accountTypesMap = null;
		saveAccMap = null;
		updateAccMap = null;
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Account Details updation
	 * @param account
	 */
	private void updateAccountDetails(Accounts acc, ReturnDataSet set, BigDecimal accrualBal, boolean isRcdSave){
		logger.debug("Entering");
		
		if(acc == null){
			acc =  new Accounts();

			acc.setAccountId(set.getAccount());
			acc.setAcCcy(set.getAcCcy());
			acc.setAcBranch(set.getFinBranch());

			String accType = set.getAccountType();
			if("Y".equals(set.getInternalAc())){
				SystemInternalAccountDefinition accountDefinition = null;
				if(!intAcMap.containsKey(set.getAccountType())){
					accountDefinition = getInternalAccountDefinitionDAO().getSystemInternalAccountDefinitionById(set.getAccountType(), "");
					intAcMap.put(accountDefinition.getSIACode(), accountDefinition);
				}else{
					accountDefinition = intAcMap.get(set.getAccountType());
				}
				if(accountDefinition != null){
					accType = accountDefinition.getSIAAcType();
				}
				acc.setAcCustId(0);
			}else{
				acc.setAcCustId(set.getCustId());
			}

			acc.setAcType(accType);

			AccountType accountType = null;
			if(!accountTypesMap.containsKey(accType)){
				accountType = getAccountTypeDAO().getAccountTypeById(accType, "");
				if(accountType != null){
					accountTypesMap.put(accountType.getAcType(), accountType);
				}
			}else{
				accountType = accountTypesMap.get(accType);
			}

			if(accountType != null){
				acc.setAcPurpose(accountType.getAcPurpose());
				acc.setAcFullName(accountType.getAcTypeDesc());
				acc.setAcShortName(accountType.getAcTypeDesc().length() > 20 ? accountType.getAcTypeDesc().substring(0, 18) : accountType.getAcTypeDesc());
			}

			acc.setInternalAc(set.getInternalAc().equals("Y") ? true : false);
			acc.setCustSysAc(set.getInternalAc().equals("N") ? true : false);
			acc.setAcPrvDayBal(BigDecimal.ZERO);
			acc.setAcOpenDate(set.getPostDate());
			acc.setAcCloseDate(null);
			acc.setAcActive(true);
			acc.setAcBlocked(false);
			acc.setAcClosed(false);
			acc.setHostAcNumber("");
			acc.setVersion(1);
			acc.setLastMntBy(1007);
			acc.setLastMntOn(new java.sql.Timestamp(set.getPostDate().getTime()));
			acc.setRecordStatus("Approved");
			acc.setRoleCode("");
			acc.setNextRoleCode("");
			acc.setTaskId("");
			acc.setNextTaskId("");
			acc.setRecordType("");
			acc.setWorkflowId(0);
		}else{
			acc.setVersion(acc.getVersion()+1);
		}

		acc.setAcLastCustTrnDate(set.getPostDate());
		acc.setAcLastSysTrnDate(set.getPostDate());

		//Accrual Balance 
		if(set.isShadowPosting()){
			if(set.getDrOrCr().equals("D")){
				accrualBal = BigDecimal.ZERO.subtract(accrualBal);
			}
			acc.setAcAccrualBal(acc.getAcAccrualBal().add(accrualBal));
		}else {
			
			// Debit or Credit Balances
			if(set.getDrOrCr().equals("C")){
				acc.setAcTodayCr(acc.getAcTodayCr().add(set.getPostAmount()));
				acc.setAcTodayBal(acc.getAcTodayBal().add(set.getPostAmount()));
			}else if(set.getDrOrCr().equals("D")){
				acc.setAcTodayDr(acc.getAcTodayDr().add(set.getPostAmount()));
				acc.setAcTodayBal(acc.getAcTodayBal().subtract(set.getPostAmount()));
			}

			//Net Balance
			acc.setAcTodayNet(acc.getAcTodayCr().subtract(acc.getAcTodayDr()));
		}

		// Account Details Updation/ Save
		if(isRcdSave){
			saveAccMap.put(set.getAccount(), acc);
		}else{
			updateAccMap.put(set.getAccount(), acc);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setAccountsDAO(AccountsDAO accountsDAO) {
		this.accountsDAO = accountsDAO;
	}
	public AccountsDAO getAccountsDAO() {
		return accountsDAO;
	}

	public void setInternalAccountDefinitionDAO(
			SystemInternalAccountDefinitionDAO internalAccountDefinitionDAO) {
		this.internalAccountDefinitionDAO = internalAccountDefinitionDAO;
	}
	public SystemInternalAccountDefinitionDAO getInternalAccountDefinitionDAO() {
		return internalAccountDefinitionDAO;
	}

	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}
	public AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}

}
