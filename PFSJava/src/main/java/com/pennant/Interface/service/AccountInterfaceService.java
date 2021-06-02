package com.pennant.Interface.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.Interface.model.IAccounts;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennanttech.pennapps.core.InterfaceException;

public interface AccountInterfaceService {

	BigDecimal getAccountAvailableBal(String accountId);

	List<IAccounts> getAccountsAvailableBalList(List<IAccounts> accountsList) throws InterfaceException;

	int removeAccountHolds() throws Exception;

	List<AccountHoldStatus> addAccountHolds(List<AccountHoldStatus> accountslIst, Date valueDate, String holdType)
			throws InterfaceException;

	Map<String, String> getAccountCurrencyMap(Map<String, String> accountCcyMap) throws InterfaceException;
}
