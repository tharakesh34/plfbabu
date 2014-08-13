package com.pennant.Interface.service;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.Interface.model.IAccounts;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.coreinterface.model.CoreBankAccountDetail;

public interface AccountInterfaceService {
	
	public List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails,String createNow) throws AccountNotFoundException;
	public List<IAccounts> fetchExistAccountList(IAccounts processAccount) throws AccountNotFoundException;
	public IAccounts fetchAccountAvailableBal(String processAccount) throws AccountNotFoundException;
	public BigDecimal getAccountAvailableBal(String AccountId);
	public Map<String, IAccounts> getAccountsAvailableBalMap(List<String> accountsList);
	public List<IAccounts> getAccountsAvailableBalList(List<IAccounts> accountsList) throws AccountNotFoundException;
	public List<CoreBankAccountDetail> checkAccountID(List<CoreBankAccountDetail> coreAcctList) throws AccountNotFoundException ;
	public int removeAccountHolds() throws Exception ;
	public List<AccountHoldStatus> addAccountHolds(List<AccountHoldStatus> accountslIst, Date valueDate) throws EquationInterfaceException;
	public Map<String, String> getAccountCurrencyMap(Map<String, String> accountCcyMap) throws AccountNotFoundException;

}
