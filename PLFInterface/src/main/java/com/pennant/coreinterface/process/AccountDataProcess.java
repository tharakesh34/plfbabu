package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.AccountBalance;
import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennant.exception.PFFInterfaceException;

public interface AccountDataProcess {

	
	
	int removeAccountHolds() throws PFFInterfaceException;

	List<AccountBalance> addAccountHolds(List<AccountBalance> accountslIst,
			String holdType) throws PFFInterfaceException;

	InterfaceAccount createAccount(InterfaceAccount accountDetail)
			throws PFFInterfaceException;

	CollateralMark collateralMarking(CollateralMark collateralMark)
			throws PFFInterfaceException;

	CollateralMark collateralDeMarking(CollateralMark collateralMark)
			throws PFFInterfaceException;
	
}
