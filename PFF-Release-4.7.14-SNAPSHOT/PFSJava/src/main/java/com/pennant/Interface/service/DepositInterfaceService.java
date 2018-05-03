package com.pennant.Interface.service;

import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennanttech.pennapps.core.InterfaceException;

public interface DepositInterfaceService {

	FetchDeposit fetchDeposits(FetchDeposit fetchDeposit) throws InterfaceException;
	
	FinCollaterals fetchDepositDetails(String depositReference) throws InterfaceException;
}
