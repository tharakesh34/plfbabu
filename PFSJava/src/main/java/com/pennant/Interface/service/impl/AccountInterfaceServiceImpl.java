package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountInterfaceServiceImpl implements AccountInterfaceService {
	private static Logger logger = LogManager.getLogger(AccountInterfaceServiceImpl.class);

	public AccountInterfaceServiceImpl() {
		super();
	}

	/**
	 * Method for Fetch Account detail depends on Parameter key fields
	 * 
	 * @param coreAcct
	 * @return
	 * @throws InterfaceException
	 */
	public List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails, String createNow)
			throws InterfaceException {
		logger.debug("Entering");

		return null;
	}

	/**
	 * Method for Fetch Account details list depends on Parameter key fields
	 * 
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public List<IAccounts> fetchExistAccountList(IAccounts processAccount) throws InterfaceException {
		logger.debug("Entering");

		return null;
	}

	/**
	 * Method for Fetch Funding Account Balance depends on Parameter key fields
	 * 
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public IAccounts fetchAccountAvailableBal(String processAccount) throws InterfaceException {
		logger.debug("Entering");

		return null;
	}

	/**
	 * Method for Fetch Funding Account Balance depends on Parameter key fields
	 * 
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public BigDecimal getAccountAvailableBal(String accountId) {
		logger.debug("Entering");

		return BigDecimal.ZERO;
	}

	/**
	 * Method for Fetch Funding Accounts Balance depends on Parameter key fields
	 * 
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	public List<IAccounts> getAccountsAvailableBalList(List<IAccounts> accountsList) throws InterfaceException {
		logger.debug("Entering");

		IAccounts account = null;
		List<CoreBankAccountDetail> coreBankAccountDetailList = new ArrayList<CoreBankAccountDetail>();
		CoreBankAccountDetail accountDetail = null;
		for (IAccounts accountNumber : accountsList) {
			accountDetail = new CoreBankAccountDetail();
			accountDetail.setAccountNumber(accountNumber.getAccountId());
			coreBankAccountDetailList.add(accountDetail);
		}

		List<IAccounts> accountList = null;
		accountList = new ArrayList<IAccounts>();
		if (coreBankAccountDetailList != null && !coreBankAccountDetailList.isEmpty()) {
			for (CoreBankAccountDetail coreBankAccountDetail : coreBankAccountDetailList) {
				account = new IAccounts();
				account.setAccountId(coreBankAccountDetail.getAccountNumber());
				account.setAcType(coreBankAccountDetail.getAcType());
				account.setAcCcy(coreBankAccountDetail.getAcCcy());
				account.setAcShortName(coreBankAccountDetail.getAcShrtName());
				account.setAvailBalSign(coreBankAccountDetail.getAmountSign());
				account.setAcAvailableBal(coreBankAccountDetail.getAcBal());
				if (StringUtils.equals(coreBankAccountDetail.getAmountSign(), "-")) {
					account.setAcAvailableBal(BigDecimal.ZERO.subtract(coreBankAccountDetail.getAcBal()));
				}
				accountList.add(account);
			}
		}

		logger.debug("Leaving");
		return accountList;
	}

	/**
	 * Method for Fetch Funding Accounts Balance depends on Parameter key fields
	 * 
	 * @param processAccount
	 * @return {@link List} of {@link IAccounts}
	 * 
	 * @throws InterfaceException
	 */
	@Override
	public Map<String, String> getAccountCurrencyMap(Map<String, String> accountCcyMap) throws InterfaceException {
		logger.debug("Entering");

		List<CoreBankAccountDetail> coreBankAccountDetailList = new ArrayList<CoreBankAccountDetail>();
		CoreBankAccountDetail accountDetail = null;

		List<String> accountsList = new ArrayList<String>(accountCcyMap.keySet());
		for (String accountNumber : accountsList) {
			accountDetail = new CoreBankAccountDetail();
			accountDetail.setAccountNumber(accountNumber);
			coreBankAccountDetailList.add(accountDetail);
		}

		accountCcyMap = null;
		if (coreBankAccountDetailList != null && !coreBankAccountDetailList.isEmpty()) {
			accountCcyMap = new HashMap<String, String>(coreBankAccountDetailList.size());
			for (CoreBankAccountDetail coreBankAccountDetail : coreBankAccountDetailList) {
				accountCcyMap.put(coreBankAccountDetail.getAccountNumber(), coreBankAccountDetail.getAcCcy());
			}
		} else {
			accountCcyMap = new HashMap<String, String>(1);
		}

		logger.debug("Leaving");
		return accountCcyMap;
	}

	/**
	 * Method for Removing Holds on Accounts Before Payments Recovery
	 */
	@Override
	public int removeAccountHolds() throws Exception {
		return 0;
	}

	/**
	 * Method for Removing Account Holds on Based on List of Repay Account Details
	 * 
	 * @return
	 * @throws EquationInterfaceException
	 */
	@Override
	public List<AccountHoldStatus> addAccountHolds(List<AccountHoldStatus> accountslIst, Date valueDate,
			String holdType) throws InterfaceException {
		logger.debug("Entering");

		return new ArrayList<AccountHoldStatus>();
	}
}
