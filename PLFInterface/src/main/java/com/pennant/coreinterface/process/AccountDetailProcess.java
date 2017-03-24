package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.exception.PFFInterfaceException;

public interface AccountDetailProcess {

	List<CoreBankAccountDetail> fetchAccount(List<CoreBankAccountDetail> bankAccountDetails, String createNow)
			throws PFFInterfaceException;

	List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail accountDetail) throws PFFInterfaceException;
	
	CoreBankAccountDetail fetchAccountAvailableBal(CoreBankAccountDetail coreAcct) throws PFFInterfaceException;

	List<CoreBankAccountDetail> fetchAccountsListAvailableBal(List<CoreBankAccountDetail> coreAcctList, boolean isCcyCheck)
			throws PFFInterfaceException;
	List<CoreBankAccountDetail> fetchCustomerAccounts(CoreBankAccountDetail coreAcct) throws PFFInterfaceException;
	
	
}
