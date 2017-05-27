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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.dao.accounts.AccountsHistoryDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.accounts.AccountsHistory;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class AccountProcessUtil implements Serializable {

	private static final long	serialVersionUID	= -1200799666995440280L;

	private AccountsDAO			accountsDAO;
	private AccountsHistoryDAO	accountsHistoryDAO;
	private AccountTypeDAO		accountTypeDAO;

	public AccountProcessUtil() {
		super();
	}

	public void procAccountUpdate(List<ReturnDataSet> dataSets) {

		Map<String, Accounts> accountMap = new HashMap<String, Accounts>(1);
		Map<String, AccountsHistory> accountHistMap = new HashMap<String, AccountsHistory>(1);
		Map<String, AccountType> accountTypeMap = new HashMap<String, AccountType>(1);

		for (int i = 0; i < dataSets.size(); i++) {
			ReturnDataSet posting = dataSets.get(i);
			if(posting.getPostAmount().compareTo(BigDecimal.ZERO) <= 0 ){
				continue;
			}
			String acTypeKey = posting.getAccountType();
			AccountType accountType = new AccountType();

			if (!accountTypeMap.containsKey(acTypeKey)) {
				accountType = accountTypeDAO.getAccountTypeById(acTypeKey, "");
				accountTypeMap.put(acTypeKey, accountType);
			} else {
				accountType = accountTypeMap.get(acTypeKey);
			}
			prepareAccounts(accountMap, posting, accountType);
			PrepareAccountsHist(accountHistMap, posting);
		}

		//Update Accounts
		for (Entry<String, Accounts> account : accountMap.entrySet()) {
			accountsDAO.saveOrUpdate(account.getValue(), "");
		}

		//Update Accounts History
		for (Entry<String, AccountsHistory> accountHist : accountHistMap.entrySet()) {
			accountsHistoryDAO.saveOrUpdate(accountHist.getValue());
		}

	}

	public void prepareAccounts(Map<String, Accounts> accountMap, ReturnDataSet posting, AccountType accountType) {
		String accountKey = posting.getAccount();
		Accounts account = new Accounts();

		if (!accountMap.containsKey(accountKey)) {
			account = prepareAccountData(posting, account, accountType);
		} else {
			account = accountMap.get(accountKey);
		}

		if (posting.isShadowPosting()) {
			if (StringUtils.equals(posting.getDrOrCr(), "D")) {
				account.setShadowBal(account.getShadowBal().subtract(posting.getPostAmount()));
			} else {
				account.setShadowBal(account.getShadowBal().add(posting.getPostAmount()));
			}
		} else {
			if (StringUtils.equals(posting.getDrOrCr(), "D")) {
				account.setAcBalance(account.getAcBalance().subtract(posting.getPostAmount()));
			} else {
				account.setAcBalance(account.getAcBalance().add(posting.getPostAmount()));
			}
		}

		accountMap.put(accountKey, account);
	}

	public Accounts prepareAccountData(ReturnDataSet posting, Accounts account, AccountType accountType) {
		account.setAccountId(posting.getAccount());
		account.setAcCcy(posting.getAcCcy());
		account.setAcType(posting.getAccountType());
		account.setAcBranch(posting.getPostBranch());
		account.setAcCustId(0);
		account.setAcPurpose(accountType.getAcPurpose());
		account.setAcFullName(accountType.getAcTypeDesc());
		account.setAcShortName(accountType.getAcTypeDesc().length() > 20 ? accountType.getAcTypeDesc().substring(0, 18)
				: accountType.getAcTypeDesc());
		account.setInternalAc(posting.isInternalAc());
		account.setCustSysAc(!posting.isInternalAc());
		account.setAcOpenDate(DateUtility.getAppDate());
		account.setAcLastCustTrnDate(account.getAcOpenDate());
		account.setAcLastSysTrnDate(account.getAcOpenDate());
		account.setAcActive(true);

		account.setVersion(0);
		account.setLastMntBy(0);
		account.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		account.setRecordStatus("Approved");
		account.setRoleCode("");
		account.setNextRoleCode("");
		account.setTaskId("");
		account.setNextTaskId("");
		account.setRecordType("");
		account.setWorkflowId(0);
		return account;
	}

	public void PrepareAccountsHist(Map<String, AccountsHistory> accountHistMap, ReturnDataSet posting) {
		String accountHistKey = posting.getAccount().concat(DateUtility.formatToShortDate(posting.getPostDate()));
		AccountsHistory accountHist = new AccountsHistory();

		if (!accountHistMap.containsKey(accountHistKey)) {
			accountHist.setAccountId(posting.getAccount());
			accountHist.setPostDate(posting.getPostDate());
		} else {
			accountHist = accountHistMap.get(accountHistKey);
		}

		if (posting.isShadowPosting()) {
			if (StringUtils.equals(posting.getDrOrCr(), "D")) {
				accountHist.setShadowBal(accountHist.getShadowBal().subtract(posting.getPostAmount()));
			} else {
				accountHist.setShadowBal(accountHist.getShadowBal().add(posting.getPostAmount()));
			}
		} else {
			if (StringUtils.equals(posting.getDrOrCr(), "D")) {
				accountHist.setTodayDebits(accountHist.getTodayDebits().subtract(posting.getPostAmount()));
				accountHist.setAcBalance(accountHist.getAcBalance().subtract(posting.getPostAmount()));
			} else {
				accountHist.setTodayCredits(accountHist.getTodayCredits().add(posting.getPostAmount()));
				accountHist.setAcBalance(accountHist.getAcBalance().add(posting.getPostAmount()));
			}

			accountHist.setTodayNet(accountHist.getTodayDebits().add(accountHist.getTodayCredits()));

		}

		accountHistMap.put(accountHistKey, accountHist);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAccountsDAO(AccountsDAO accountsDAO) {
		this.accountsDAO = accountsDAO;
	}

	public AccountsDAO getAccountsDAO() {
		return accountsDAO;
	}

	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}

	public AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}

	public AccountsHistoryDAO getAccountsHistoryDAO() {
		return accountsHistoryDAO;
	}

	public void setAccountsHistoryDAO(AccountsHistoryDAO accountsHistoryDAO) {
		this.accountsHistoryDAO = accountsHistoryDAO;
	}

}
