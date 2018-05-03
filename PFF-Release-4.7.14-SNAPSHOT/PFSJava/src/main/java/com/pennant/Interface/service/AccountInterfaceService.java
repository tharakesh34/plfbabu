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
import com.pennanttech.pennapps.core.InterfaceException;

public interface AccountInterfaceService {
	
	List<IAccounts> fetchExistAccount(List<IAccounts> accountDetails,String createNow) throws InterfaceException;
	
	List<IAccounts> fetchExistAccountList(IAccounts processAccount) throws InterfaceException;
	
	IAccounts fetchAccountAvailableBal(String processAccount) throws InterfaceException;
	
	BigDecimal getAccountAvailableBal(String accountId);
	
	Map<String, IAccounts> getAccountsAvailableBalMap(List<String> accountsList);
	
	List<IAccounts> getAccountsAvailableBalList(List<IAccounts> accountsList) throws InterfaceException;
	
	List<CoreBankAccountDetail> checkAccountID(List<CoreBankAccountDetail> coreAcctList) throws InterfaceException ;
	
	int removeAccountHolds() throws Exception ;
	
	List<AccountHoldStatus> addAccountHolds(List<AccountHoldStatus> accountslIst, Date valueDate,String holdType) throws InterfaceException;
	
	Map<String, String> getAccountCurrencyMap(Map<String, String> accountCcyMap) throws InterfaceException;
	
	InterfaceAccount createAccount(Customer customer) throws InterfaceException;

	CollateralMark collateralMarking(CollateralMark coltralMarkReq) throws InterfaceException;

	CollateralMark collateralDeMarking(CollateralMark coltralMarkReq) throws InterfaceException;
}
