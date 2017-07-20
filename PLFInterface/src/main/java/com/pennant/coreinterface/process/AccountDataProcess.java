package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.AccountBalance;
import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennanttech.pennapps.core.InterfaceException;

public interface AccountDataProcess {

	
	
	int removeAccountHolds() throws InterfaceException;

	List<AccountBalance> addAccountHolds(List<AccountBalance> accountslIst,
			String holdType) throws InterfaceException;

	InterfaceAccount createAccount(InterfaceAccount accountDetail)
			throws InterfaceException;

	CollateralMark collateralMarking(CollateralMark collateralMark)
			throws InterfaceException;

	CollateralMark collateralDeMarking(CollateralMark collateralMark)
			throws InterfaceException;
	
}
