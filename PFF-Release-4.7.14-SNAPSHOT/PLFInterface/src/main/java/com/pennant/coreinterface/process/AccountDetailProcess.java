package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface AccountDetailProcess {

	List<CoreBankAccountDetail> fetchAccount(List<CoreBankAccountDetail> bankAccountDetails, String createNow)
			throws InterfaceException;

	List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail accountDetail) throws InterfaceException;
	
	CoreBankAccountDetail fetchAccountAvailableBal(CoreBankAccountDetail coreAcct) throws InterfaceException;

	List<CoreBankAccountDetail> fetchAccountsListAvailableBal(List<CoreBankAccountDetail> coreAcctList, boolean isCcyCheck)
			throws InterfaceException;
	List<CoreBankAccountDetail> fetchCustomerAccounts(CoreBankAccountDetail coreAcct) throws InterfaceException;
	
	
}
