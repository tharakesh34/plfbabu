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
	
	List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails,String createNow) throws AccountNotFoundException;
	List<IAccounts> fetchExistAccountList(IAccounts processAccount) throws AccountNotFoundException;
	IAccounts fetchAccountAvailableBal(String processAccount) throws AccountNotFoundException;
	BigDecimal getAccountAvailableBal(String accountId);
	Map<String, IAccounts> getAccountsAvailableBalMap(List<String> accountsList);
	List<IAccounts> getAccountsAvailableBalList(List<IAccounts> accountsList) throws AccountNotFoundException;
	List<CoreBankAccountDetail> checkAccountID(List<CoreBankAccountDetail> coreAcctList) throws AccountNotFoundException ;
	int removeAccountHolds() throws Exception ;
	List<AccountHoldStatus> addAccountHolds(List<AccountHoldStatus> accountslIst, Date valueDate,String holdType) throws EquationInterfaceException;
	Map<String, String> getAccountCurrencyMap(Map<String, String> accountCcyMap) throws AccountNotFoundException;
}
