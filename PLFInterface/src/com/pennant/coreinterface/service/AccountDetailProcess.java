package com.pennant.coreinterface.service;

import java.util.List;

import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.coreinterface.model.AccountBalance;
import com.pennant.coreinterface.model.CoreBankAccountDetail;

public interface AccountDetailProcess {

	List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail coreAcct) throws AccountNotFoundException;

	List<CoreBankAccountDetail> fetchAccount(List<CoreBankAccountDetail> bankAccountDetails, String createNow)
			throws AccountNotFoundException;

	CoreBankAccountDetail fetchAccountAvailableBal(CoreBankAccountDetail coreAcct) throws AccountNotFoundException;

	List<CoreBankAccountDetail> fetchAccountsListAvailableBal(List<CoreBankAccountDetail> coreAcctList, boolean isCcyCheck)
			throws AccountNotFoundException;

	int removeAccountHolds() throws EquationInterfaceException;

	List<AccountBalance> addAccountHolds(List<AccountBalance> accountslIst)	throws EquationInterfaceException;

}
