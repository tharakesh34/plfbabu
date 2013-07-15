package com.pennant.Interface.service;


import java.math.BigDecimal;
import java.util.List;

import com.pennant.Interface.model.IAccounts;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface AccountInterfaceService {
	
	public List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails,String createNow ,boolean newConnection) throws AccountNotFoundException;
	public List<IAccounts> fetchExistAccountList(IAccounts processAccount) throws AccountNotFoundException;
	public IAccounts fetchAccountAvailableBal(IAccounts processAccount,boolean newConnection);
	public BigDecimal getAccountAvailableBal(String AccountId);
}
