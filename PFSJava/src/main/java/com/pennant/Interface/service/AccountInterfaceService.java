package com.pennant.Interface.service;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.Interface.model.IAccounts;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennant.exception.PFFInterfaceException;

public interface AccountInterfaceService {
	
	List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails,String createNow) throws PFFInterfaceException;
	
	List<IAccounts> fetchExistAccountList(IAccounts processAccount) throws PFFInterfaceException;
	
	IAccounts fetchAccountAvailableBal(String processAccount) throws PFFInterfaceException;
	
	BigDecimal getAccountAvailableBal(String accountId);
	
	Map<String, IAccounts> getAccountsAvailableBalMap(List<String> accountsList);
	
	List<IAccounts> getAccountsAvailableBalList(List<IAccounts> accountsList) throws PFFInterfaceException;
	
	List<CoreBankAccountDetail> checkAccountID(List<CoreBankAccountDetail> coreAcctList) throws PFFInterfaceException ;
	
	int removeAccountHolds() throws Exception ;
	
	List<AccountHoldStatus> addAccountHolds(List<AccountHoldStatus> accountslIst, Date valueDate,String holdType) throws PFFInterfaceException;
	
	Map<String, String> getAccountCurrencyMap(Map<String, String> accountCcyMap) throws PFFInterfaceException;
	
	InterfaceAccount createAccount(Customer customer) throws PFFInterfaceException;

	CollateralMark collateralMarking(CollateralMark coltralMarkReq) throws PFFInterfaceException;

	CollateralMark collateralDeMarking(CollateralMark coltralMarkReq) throws PFFInterfaceException;
}
